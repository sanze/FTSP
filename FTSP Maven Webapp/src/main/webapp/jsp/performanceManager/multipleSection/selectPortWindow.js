Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
// 创建一个名为sp的命名空间,selectPort的缩写
var sp = new Object();
// 创建一个名为common的命名空间
var common = new Object();
//全局变量,用来存放勾选节点信息,点击"确定"按钮时赋值
var result = {};
// 全局变量，勾选节点的对象,在创建window是赋值
var cell = {};
// 创建一个树对象
common.tree = {
	// 通过参数创建树，返回包含树的html页面，frameId为树的Id，emsId为起始节点
	'getTree' : function(frameId, emsId) {
		var treeParams = {
			rootId : emsId,
			rootType : 2,
			checkModel : "single",
			rootVisible : true,
			// 规定数显示到的层数。4表示树可以展开到网元
			leafType : 8
		};
		var treeurl = "../../commonManager/tree.jsp?"
				+ Ext.urlEncode(treeParams);
		var tree = '<iframe id=' + frameId + 'name =' + frameId + ' src ="'
				+ treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0/>'
		return tree;
	},
	// 从指定树上获取选择的节点信息；
	'getSelectEl' : function(treeId, info) {
		if (typeof (info) == 'undefined') {
			info = [ "nodeId", "nodeLevel" ];
		}
		var iframe = window.frames[treeId] || window.frames[0];
		var result;
		if (iframe.getCheckedNodes) {
			result = iframe.getCheckedNodes(info);
		} else {
			result = iframe.contentWindow.getCheckedNodes(info);
		}
		return result;
	}
};

// 显示一个window
sp.showWindow = function(emsId, mulId) {
	cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
	// 获取一个树，frame的Id='selectPortTree'
	var treeFrame = common.tree.getTree("selectPortTree", emsId);
	// 定义Panel存放树
	var centerPanel = new Ext.FormPanel({
		region : 'center',
		border : false,
		forceFit : true,
		html : treeFrame
	});
	// 定义panel存放方向选择
	var northPanel = new Ext.FormPanel({
		region : 'north',
		height : 50,
		bodyStyle : 'padding:15px 20px;',
		labelWidth : 60,
		border : false,
		items : {
			fieldLabel : '方向选择',
			id : 'fangxiang',
			xtype : 'radiogroup',
			columns : 2,
			items : [ {
				boxLabel : "正向",
				name : 'new_direction',
				inputValue : 1,
				checked : true
			}, {
				boxLabel : "反向",
				name : 'new_direction',
				inputValue : 2
			} ]
		}
	});
	if (cell[0].get('DIRECTION') == 1) {
		Ext.getCmp('fangxiang').setDisabled(true);
	}
	var viewPanel = new Ext.Panel({
		layout : 'border',
		border : false,
		items : [ northPanel, centerPanel ]
	});
	// 创建一个window并显示;title：'指定起始端口'
	new Ext.Window({
		title : '指定起始端口',
		layout : 'fit',
		id : 'selectPortWindow',
		modal : true,
		// 这里与MulitipleSectionList.js耦合。。。。
		width : 320,
		height : Ext.getCmp('win').getHeight() * 0.7,
		minWidth : 320,
		buttons : [ {
			text : '确定',
			handler : function() {
				sp.process();
			}
		}, {
			text : '取消',
			handler : function() {
				// 获取该button所有xtype为'window'的容器并销毁
				this.findParentByType('window').destroy();
			}
		} ],
		items : [ viewPanel ]
	}).show();
};

// 处理'确定'按钮的点击事件
sp.process = function() {
	// 设置需要从树上获取那些数据
	var info = [ "nodeId", "nodeLevel", "text", "path:nodeId",
			"path:nodeLevel", "emsId" ];
	// 从指定的树上获取勾选节点信息
	result = common.tree.getSelectEl("selectPortTree", info);
	// 判断都选节点的合法性
	if (result.length < 1) {
		Ext.Msg.alert('提示', '请勾选端口节点！');
	} else if (result[0].nodeLevel != 8) {
		Ext.Msg.alert('提示', '勾选的节点类型必须为端口！');
	} else {// 如果勾选节点类型为端口
		// 给查询参数赋值
		var param1 = {
			'mulId' : cell[0].get('PM_MULTI_SEC_ID'),
			'direction' : Ext.getCmp('fangxiang').getValue().inputValue
		};
		// 查询该光复用段是否存在路由记录
		access('multiple-section!hasRecord.action', param1, hasRecord);
	}
};

// 封装一个提交请求的方法。url:action名称,param:需要传递的参数,callBackFun:成功返回后的回调函数
var access = function(url, param, callBackFun) {
	// 处理前添加一个mask,禁止处理过程中操作
	Ext.getCmp('selectPortWindow').getEl().mask('正在处理...');
	Ext.Ajax.request({
		url : url,
		method : 'POST',
		params : param,
		success : callBackFun,
		failure : function(response) {
			// 删除mask
			Ext.getCmp('selectPortWindow').getEl().unmask();
			Ext.Msg.alert('错误', '网络连接异常！');
		}
	});
};

// access回调函数：存在？(覆盖？覆盖：放弃)：覆盖。response:服务器的回复。
var hasRecord = function(response) {
	// 删除mask
	Ext.getCmp('selectPortWindow').getEl().unmask();
	var obj = Ext.decode(response.responseText);
	// 查询成功
	if (obj.returnResult == 1) {
		var url = 'multiple-section!autoCreateRoute.action';

		var param2 = {
			'mulId' : cell[0].get('PM_MULTI_SEC_ID'),
			'direction' : Ext.getCmp('fangxiang').getValue().inputValue,
			'startPtp' : result[0].nodeId
		};
		if (obj.returnMessage == "false") {
			access(url, param2, isOk);
		} else {
			Ext.Msg.confirm('提示', '已存在信息，是否覆盖？', function(btn) {
				if (btn == 'yes') {
					access(url, param2, isOk);
				}
			});
		}
	}
	// 查询失败
	if (obj.returnResult == 0) {
		Ext.Msg.alert('提示', obj.returnMessage);
	}
};

// 判断数据库操作成功?跳转到路由页面：告知失败原因。response:服务器的回复。
var isOk = function(response) {
	// 删除mask
	Ext.getCmp('selectPortWindow').getEl().unmask();
	var obj = Ext.decode(response.responseText);
	// 数据插入成功
	if (obj.returnResult == 1) {
		// 弹出自动生成成功提示框,注意:此提示框必须在跳转页面之前,否则会出现布局问题...
		Ext.Msg.alert('提示','自动路由生成成功');
		//跳转到路由页面
		var mul_id = cell[0].get('PM_MULTI_SEC_ID');
		var sec_name = cell[0].get('SEC_NAME');
		var emsId = cell[0].get('BASE_EMS_CONNECTION_ID');
		var direction = cell[0].get('DIRECTION');
		var url = "../performanceManager/multipleSection/addRouteManu.jsp?mul_id="
				+ mul_id + "&emsId=" + emsId + "&direction=" + direction;
		parent.addTabPage(url, "路由设置(" + sec_name + ")");
		
	}
	// 数据插入失败
	if (obj.returnResult == 0) {
		Ext.Msg.alert('提示', obj.returnMessage);
	}
};
