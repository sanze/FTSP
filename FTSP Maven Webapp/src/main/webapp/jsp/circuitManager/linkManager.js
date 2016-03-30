/* 1、定义该页面需要使用的变量===========================================================================*/
cableComboGridField.width=220;
fiberComboGridField.width=220;
cable1ComboGridField.width=220;
fiber1ComboGridField.width=220;
Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
// 定义页面一次显示记录的条数，同时也是数据库一次取记录的个数；
var myPageSize = 200;

// 规定共通树显示格式的参数
var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	checkModel : "single",
	rootVisible : false,
	// 规定数显示到的层数。8表示树可以展开的到第八个层级
	leafType : 8
};
var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
// 导出是查询的json数据；
var exportData;
// 定义a、z端ptpid的全局变量，当修改链路时若不改变ptp则使用原链路ptp若修改则将修改后的ptpid赋值
var newA_END_PTP = -1;
var newZ_END_PTP = -1;
var modifyA_END_PTP = -1;
var modifyZ_END_PTP = -1;
var newA_END_NE = -1;
var newA_END_EMS = -1;
var newZ_END_NE = -1;
var newZ_END_EMS = -1;
var result = null;
var selectedTree = null;
var selectLinkId;
var isManual;

/*
 * 2、创建Ext组件==================================================================================
 */

/*
 * 2.1创建一棵共通树---------------------------------------------------------------------
 */

var westPanel = new Ext.Panel({
	id : "westPanel",
	region : "west",
	width : 280,
	height : 800,
	minSize : 230,
//	maxSize : 320,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="tree_panel" name = "tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0/>'
});

var addTreePanel = new Ext.Panel({
	id : "addTreePanel",
	region : "west",
	width : 210,
	height : 400,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="addtreePanel" name = "addtreePanel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0/>'
});

var modifyTreePanel = new Ext.Panel({
	id : "modifyTreePanel",
	region : "west",
	width : 210,
	height : 400,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="modifytreePanel" name = "modifytreePanel" src ="'
			+ treeurl + '" height="100%" width="100%" frameBorder=0 border=0/>'
});

/*
 * 2.2创建eastPanel界面----------------------------------------------------------------
 */

/*
 * 2.2.1弹窗..............................................................
 */

// a.创建一个导入文件的FormPanel
var fileUploadForm = new Ext.FormPanel({
	fileUpload : true,
	frame : true,
	bodyStyle : 'padding: 20px 10px 0 10px;',
	labelWidth : 100,
	defaults : {
		anchor : '95%',
		allowBlank : false,
		msgTarget : 'side'
	},
	items : [ {
		xtype : 'fileuploadfield',
		id : 'uploadFile',
		fieldLabel : '文件路径',
		name : 'uploadFile',
		buttonText : '',
		regex : /^.*?\.(xls|xlsx)$/,
		buttonCfg : {
			iconCls : 'uploader'
		}
	}]
});
// 将formPanel放入弹窗中，形成一个导入数据的弹窗；
var fileUploadWindow = new Ext.Window({
	id : 'fileUploadWindow',
	title : '导入文件',
	width : 500,
	height : 150,
	minWidth : 350,
	minHeight : 100,
	layout : 'fit',
	plain : false,
	resizable : false,
	constrain : true,
	closeAction : 'hide',// 关闭窗口
	bodyStyle : 'padding:1px;',
	items : [ fileUploadForm ],
	buttons : [ {
		text : '确定',
		handler : function() {
			if (fileUploadForm.getForm().isValid()) {
				fileUploadForm.getForm().submit({
					url : 'circuit!importLinksExcel.action',
					params : {
						"jsonString" : Ext.getCmp('uploadFile')
								.getValue()
					},
					success : function(form, action) {
						Ext.MessageBox.hide();
						fileUploadWindow.hide();
						Ext.getCmp('uploadFile').reset();
						Ext.Msg.alert("提示", "导入成功");
					},
					failure : function(form, action) {
						var obj = Ext.decode(action.response.responseText);
						Ext.MessageBox.hide();
						fileUploadWindow.hide();
						Ext.Msg.alert("提示", obj.result);
					}
				});
				Ext.Msg.show({
					   title:'提示',
					   msg: '正在导入，请稍后......'
					});

			}
		}
	}, {
		text : '取消',
		handler : function() {
			fileUploadWindow.hide();
		}
	} ]
});

// b.新建新增、修改链路的弹窗
var newLinkForm = new Ext.FormPanel({
	id : "newLinkForm",
	frame : true,
	border : false,
//	width : 400,
	bodyStyle : 'padding:40px 50px 20px',
	labelAlign : 'right',
	items : [ {
		xtype : 'textfield',
		id : 'newNativeLinkName',
		width : 193,
		fieldLabel : '链路原始名称'
	}, {
		xtype : 'textfield',
		id : 'newUserLabel',
		width : 193,
		fieldLabel : '链路自定义名称'
	},{
		fieldLabel : 'A端端口',
		border : false,
		layout : 'column',
		items : [ {
			xtype : 'textfield',
			id : 'newA_END_NAME',
			allowBlank : false,
			width : 200,
			readOnly : true
		}, {
			xtype : 'displayfield',
			value : "<font color=red>*</font>",
			width : 20
		}, {
			xtype : 'button',
			border : false,
			id : 'newBtnA',
			text : '···',
			width : 20,
			height : 15,
			handler : function() {
				addTreeWindow_A.show();
			}
		} ]
	}, {
		fieldLabel : 'Z端端口',
		border : false,
		layout : 'column',
		items : [ {
			xtype : 'textfield',
			id : 'newZ_END_NAME',
			allowBlank : false,
			width : 200,
			readOnly : true
		}, {
			xtype : 'displayfield',
			width : 20,
			value : "<font color=red>*</font>"
		}, {
			xtype : 'button',
			border : false,
			text : '···',
			id : 'newBtnZ',
			width : 20,
			height : 15,
			handler : function() {
				addTreeWindow_Z.show();
			}
		} ]
	}, {
		fieldLabel : '链路方向',
		id : 'new_radiogroup',
		xtype : 'radiogroup',
		anchor : '95%',
		columns : 2,
		items : [ {
			boxLabel : "单向",
			name : 'new_direction',
			inputValue : 0
		}, {
			boxLabel : "双向",
			name : 'new_direction',
			inputValue : 1,
			checked : true
		} ]
	}
//	,{
//		fieldLabel : '锁定',
//		id : 'new1_radiogroup',
//		xtype : 'radiogroup',
//		anchor : '95%',
//		columns : 2,
//		items : [ {
//			boxLabel : "锁定",
//			name : 'new_locked',
//			inputValue : 1
//		}, {
//			boxLabel : "不锁定",
//			name : 'new_locked',
//			inputValue : 0,
//			checked : true
//		} ]
//	}
	]
});
/*var addPanel = new Ext.Panel({
	id : 'addPanel',
	autoHeight : true,
	autoWidth : true,
	border : false,
	autoScroll : true,
	layout : 'column',
	items : [newLinkForm ]
});*/
// 将formPanel放入弹窗中，形成一个新增链路的弹窗；
var newLinkWindow = new Ext.Window({
	id : 'addLink',
	title : '新增链路',
	width : 500,
	height:300,
	minWidth : 350,
	minHeight : 100,
	layout : 'fit',
	plain : false,
	modal : true, 
	border : false,
	constrain : true,
	closeAction : 'hide',// 关闭窗口
	bodyStyle : 'padding:1px;',
	items : [ newLinkForm ],
	buttons : [
			{
				text : '确定',
				handler : function() {
					if (newLinkForm.getForm().isValid()) {
						var nativeLinkName = Ext.getCmp('newNativeLinkName').getValue();
						if (nativeLinkName.length == 0) {
							nativeLinkName = Ext.getCmp('newA_END_NAME').getValue()
									+ "="
									+ Ext.getCmp('newZ_END_NAME').getValue();
						}
						var newUserLabel = Ext.getCmp('newUserLabel').getValue();
						if (newUserLabel.length == 0) {
							newUserLabel = Ext.getCmp('newA_END_NAME').getValue()
									+ "="
									+ Ext.getCmp('newZ_END_NAME').getValue();
						}
						var map = {
							"nativeLinkName" : nativeLinkName,
							"userLabel" : newUserLabel,
							"direction" : Ext.getCmp('new_radiogroup')
									.getValue().inputValue,
							"A_END_PTP" : newA_END_PTP,
							"A_END_NE" : newA_END_NE,
							"A_END_EMS" : newA_END_EMS,
							"Z_END_PTP" : newZ_END_PTP,
							"Z_END_NE" : newZ_END_NE,
							"Z_END_EMS" : newZ_END_EMS,
							"linkType":1
//							"islocked" : Ext.getCmp('new1_radiogroup').getValue().inputValue
						};
						var jsonData = {
							"jsonString" : Ext.encode(map)
						};
						parent.Ext.getBody().mask('请稍后...');
						Ext.Ajax.request({
							url : 'circuit!addLink.action',
							method : 'POST',
							params : jsonData,
							success : function(response) {
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("提示", obj.returnMessage);
								newLinkForm.getForm().reset();
								newA_END_PTP = -1;
								newA_END_NE = -1;
								newA_END_EMS = -1;
								newZ_END_PTP = -1;
								newZ_END_NE = -1;
								newZ_END_EMS = -1;
								if (obj.returnResult == 1) {
									newLinkWindow.hide();
								}
								parent.Ext.getBody().unmask();
							},
							failure : function(response) {
								Ext.Msg.alert("提示", "新增失败");
								newLinkForm.getForm().reset();
								newA_END_PTP = -1;
								newA_END_NE = -1;
								newA_END_EMS = -1;
								newZ_END_PTP = -1;
								newZ_END_NE = -1;
								newZ_END_EMS = -1;
								newLinkWindow.hide();
								parent.Ext.getBody().unmask();
							}
						});
					}
				}
			}, {
				text : '取消',
				handler : function() {
					newA_END_PTP = -1;
					newA_END_NE = -1;
					newA_END_EMS = -1;
					newZ_END_PTP = -1;
					newZ_END_NE = -1;
					newZ_END_EMS = -1;
					newLinkForm.getForm().reset();
					newLinkWindow.hide();
				}
			} ]
});


var addTreeWindow_A = new Ext.Window({
	id : 'addTreeWindow_A',
	title : '端口选择',
	width : 300,
	autoHeight : true,
	minWidth : 350,
	minHeight : 400,
	layout : 'fit',
	plain : false,
	modal : true,
	resizable : false,
	closeAction : 'hide',// 关闭窗口
	bodyStyle : 'padding:1px;',
	constrain : true,
	items : [ addTreePanel ],
	buttons : [ {
		text : '确定',
		handler : function() {
			selectedTree = "addtreePanel";
			getPort();
			// 判断是否勾选节点
			if (result.length == 0 || result.length > 1) {
				Ext.Msg.alert("提示", "没有勾选或多选！");
				// 判断是否选择端口
			} else if (result[0].nodeLevel != 8) {
				Ext.Msg.alert("提示", "请选择端口！");
				// 获取A端PTP/EMS/NE的Id
			} else { 
				var j = 0;
				var nodeIdArray = result[0]['path:nodeId'].split(":");
				var nodeLevelArray = result[0]['path:nodeLevel'].split(":");
				var textArray = result[0]['path:text'].split(":");
				for ( var i = 0; i < nodeLevelArray.length; i++) {
					if (nodeLevelArray[i] == 4) {
						j = i;
						break;
					}
				}
				Ext.getCmp('newA_END_NAME').setValue(textArray[j]+"_"+result[0].text);
				newA_END_PTP = result[0].nodeId;
				newA_END_NE = nodeIdArray[j];
				newA_END_EMS = result[0].emsId;
				addTreeWindow_A.hide();
			}
			selectTree = null;
			result = null;
		}
	}, {
		text : '取消',
		handler : function() {
			addTreeWindow_A.hide();
		}
	} ]
});
var addTreeWindow_Z = new Ext.Window({
	id : 'addTreeWindow_Z',
	title : '端口选择',
	width : 300,
	autoHeight : true,
	minWidth : 350,
	minHeight : 400,
	layout : 'fit',
	plain : false,
	modal : true,
	constrain : true,
	resizable : false,
	closeAction : 'hide',// 关闭窗口
	bodyStyle : 'padding:1px;',
	items : [ modifyTreePanel ],
	buttons : [ {
		text : '确定',
		handler : function() {
			selectedTree = "modifytreePanel";
			getPort();
			if (result.length == 0 || result.length > 1) {
				Ext.Msg.alert("提示", "没有勾选或多选！");
			} else if (result[0].nodeLevel != 8) {
				Ext.Msg.alert("提示", "请选择端口！");
			} else { 
				var j = 0;
				var nodeIdArray = result[0]['path:nodeId'].split(":");
				var nodeLevelArray = result[0]['path:nodeLevel'].split(":");
				var textArray = result[0]['path:text'].split(":");  
				for ( var i = 0; i < nodeLevelArray.length; i++) {
					if (nodeLevelArray[i] == 4) {
						j = i;
						break;
					}
				}
				Ext.getCmp('newZ_END_NAME').setValue(textArray[j]+"_"+result[0].text);
				newZ_END_PTP = result[0].nodeId;
				newZ_END_NE = nodeIdArray[j];
				newZ_END_EMS = result[0].emsId;
				addTreeWindow_Z.hide();
			}
			selectTree = null;
			result = null;
		}
	}, {
		text : '取消',
		handler : function() {
			addTreeWindow_Z.hide();
		}
	} ]
});


/* 2.2.2查询结果显示区域........................................................... */

// a.定义一个Store用来存放从数据库查询到数据；
var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : "total",
		root : "rows"
	}, [ "BASE_LINK_ID", "NATIVE_EMS_NAME", "A_EMS_NAME", "A_NE_NAME", "A_END_PORT",
			"Z_EMS_NAME", "Z_NE_NAME", "Z_END_PORT", "DIRECTION", "A_STATION_ID","Z_STATION_ID",
			"IS_MANUAL", "USER_LABEL","IS_CONFLICT","cables","cableSection","fiber","fiberId",
			"cableId","aEndPtp","zEndPtp"]),
			url : 'circuit!selectLinks.action'
});

// b.定义一个选择框对象
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : false,// 单选
	header : ""
});
// 解决checkbox列无法锁定问题
checkboxSelectionModel.sortLock();

// c.定义需要显示的内容
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), checkboxSelectionModel, {
		id : 'BASE_LINK_ID',
		header : '链路号',
		dataIndex : 'BASE_LINK_ID',
		width : 50
	}, {
		id : 'NATIVE_EMS_NAME',
		header : '链路原始名称',
		dataIndex : 'NATIVE_EMS_NAME',
		width : 100
	},{
		id : 'USER_LABEL',
		header : '链路自定义名称',
		dataIndex : 'USER_LABEL',
		width : 100
	}, {
		id : 'emsNameA',
		header : 'A端网管',
		dataIndex : 'A_EMS_NAME',
		width : 100
	}, {
		id : 'neNameA',
		header : 'A端网元',
		dataIndex : 'A_NE_NAME',
		width : 100
	}, {
		id : 'portNameA',
		header : 'A端端口',
		dataIndex : 'A_END_PORT',
		width : 100
	}, {
		id : 'emsNameZ',
		header : 'Z端网管',
		dataIndex : 'Z_EMS_NAME',
		width : 100
	}, {
		id : 'neNameZ',
		header : 'Z端网元',
		dataIndex : 'Z_NE_NAME',
		width : 100
	}, {
		id : 'portNameZ',
		header : 'Z端端口',
		dataIndex : 'Z_END_PORT',
		width : 100
	}, {
		id : 'direction',
		header : '方向',
		dataIndex : 'DIRECTION',
		width : 100,
		renderer : function(v) {
			if (v == 0) {
				return "单向";
			}
			if (v == 1) {
				return "双向";
			}
		}
	}, {
		id : 'isMan',
		header : '生成方式',
		dataIndex : 'IS_MANUAL',
		width : 100,
		renderer : function(v) {
			if (v == 0) {
				return ("自动");
			}
			if (v == 1) {
				return ("手动");
			}
		}
	}, {
		id : 'IS_CONFLICT',
		header : '状态',
		dataIndex : 'IS_CONFLICT',
		width : 100,
		renderer : function(v) {
			if (v == 0) {
				return "";
			} else if (v == 1) {
				return "冲突项";
			}else if (v == 2) {
				return "冲突源";
			}
		}
	},{
		id : 'cables',
		header : '光缆',
		dataIndex : 'cables',
		width : 100
	},{
		id : 'cableSection',
		header : '光缆段',
		dataIndex : 'cableSection',
		width : 100
	},{
		id : 'fiber',
		header : '光纤',
		dataIndex : 'fiber',
		width : 100
	}]
});
// d.定义一个显示页面的工具条
var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

/*
 * 2.2.2查询区域.....................................................................
 */

// 定义一个queryForm用来存放查询条件
var eastPanel = new Ext.Panel({
	id : 'eastPanel',
	region : "center",
	autoScroll : true,
	layout : 'fit',
	items : [ new Ext.grid.EditorGridPanel({
		id : "gridPanel",
		// title:'用户管理',
		cm : cm,
		store : store,
		// height : 550,
		stripeRows : true, // 交替行效果
		loadMask : {
			msg : '数据加载中...'
		},
		selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
		view : new Ext.ux.grid.LockingGridView(),
		forceFit : true,
		bbar : pageTool,
		tbar : [ "-",{
			xtype : 'displayfield',
			value : "链路号："
		}, {
			xtype : 'numberfield',
			id : 'linkId'
		},"-", {
			text : '查询',
			privilege : viewAuth,
			icon : '../../resource/images/btnImages/search.png',
			handler : queryLinks,
			id : "query"
		},"-", {
			text : '新增',
			privilege : addAuth,
			icon : '../../resource/images/btnImages/add.png',
			handler : addLink
		}, {
			text : '删除',
			privilege : delAuth,
			icon : '../../resource/images/btnImages/delete.png',
			handler : deleteLink
		}, {
			text : '修改',
			privilege : modAuth,
			icon : '../../resource/images/btnImages/modify.png',
			handler : modifyLink
		},"-", {
			text : '光纤',
			privilege : modAuth,  
			menu : {
				items : [ {
					text : '关联光纤',
					handler :relateFiber
				}, {
					text : '删除关联',
					handler :cancelRelateFiber
				} ]
			}
		},"-", {
			text : '导入',
			privilege : actionAuth,
			icon : '../../resource/images/btnImages/import.png',
			handler : importLinks
		}, {
			text : '导出',
			privilege : actionAuth,
			icon : '../../resource/images/btnImages/export.png',
			menu : {
				items : [ {
					text : '导出页面数据',
					handler : function(){
						exportLinks(1);
					}
				}, {
					text : '导出底层数据',
					handler : function(){
						exportLinks(2);
					}
				} ]
			}
		} ]
	}) ]
});

/*
 * 3、函数方法=====================================================================================
 */

/*
 * 3.1查询链路--------------------------------------------------------------------------
 */

function queryLinks() {
	if(Ext.getCmp('linkId').isValid()){
		var linkId = Ext.getCmp('linkId').getValue();
		linkId = !!linkId ? linkId : 0;
		var jsonData;
		selectedTree = "tree_panel";
		getPort();
		if (result.length == 0 && linkId == 0) {
			Ext.Msg.alert("提示", "请选择查询条件");
		} else {
			if (result.length == 0) {
				jsonData = {
					"linkId" : linkId,
					"limit" : myPageSize,
					"aNodeId" : -1,
					"aNodeLevel" : -1,
					"userId" : userId,
					"displayName" : displayName,
					"tag":1
				};
			} else {
				if (result[0].nodeLevel != 8 && result[0].nodeLevel != 4
						&& result[0].nodeLevel != 2 && result[0].nodeLevel != 1) {
					Ext.Msg.alert("提示", "请选择端口/网元/网管/网管分组！");
					selectTree = null;
					result = null;
				} else {
					jsonData = {
						"linkId" : linkId,
						"limit" : myPageSize,
						"aNodeId" : result[0].nodeId,
						"aNodeLevel" : result[0].nodeLevel,
						"userId" : userId,
						"displayName" : displayName,
						"tag":1, 
						"linkType":1
					};
				}
			}
			exportData = jsonData;
			store.proxy = new Ext.data.HttpProxy({
				url : 'circuit!selectLinks.action'
			});
			store.baseParams = jsonData;
			store.load({
				callback : function(r, options, success) {
					if (success) {
						selectTree = null;
						result = null;
					} else {
						selectTree = null;
						result = null;
						top.Ext.getBody().unmask();
						Ext.Msg.alert('提示', '没有查询到符合条件的电路');
					}
				}
			});
		}
	}
}
/*
 * 3.2新增链路--------------------------------------------------------------------------
 */

function addLink() {
	newLinkWindow.show();
}
/*
 * 3.3删除链路--------------------------------------------------------------------------
 */

function deleteLink() {
	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
	if (cell.length == 0) {
		Ext.Msg.alert("提示", "请选择需要删除的链路！");
	} else if(cell.length > 1){
		Ext.Msg.alert("提示", "请选择单条链路！");
	}else {// 如要删除多个返回1453版本
		var jsonString = {
			"linkId" : cell[0].get('BASE_LINK_ID'),
			"direction" : cell[0].get('DIRECTION')
		};
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		Ext.Msg.confirm('提示', '确认删除？', function(btn) {
			if (btn == "yes") {
				Ext.getBody().mask('请稍等...');
				Ext.Ajax.request({
					url : 'circuit!deleteLinks.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage, function(r) {
							});
						}
						if (obj.returnResult == 1) {
							Ext.Msg.alert("提示", obj.returnMessage);
							// 刷新列表
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						};
						Ext.getBody().unmask();
					},
					failue : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '保存失败！');
					}
				});
			}
		});
	}
}

/*
 * 3.4修改链路--------------------------------------------------------------------------
 */

function modifyLink() {
	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
	if (cell.length == 0) {
		Ext.Msg.alert("提示", "请选择需要修改名称的链路！");
	} else if(cell.length > 1) {
		Ext.Msg.alert("提示", "只能选择一条链路进行修改！");
	} else {		
		// 修改链路
		var modifyLinkForm = new Ext.FormPanel({
			id : "modifyLinkForm",
			frame : true,
			border : false,
			bodyStyle : 'padding:20px 10px 10px',
			labelAlign : 'right',
			autoScroll : true,
			items : [ {
				border : false

			}, {
				xtype : 'textfield',
				id : 'modifyNativeLinkName',
				width : 193,
				fieldLabel : '链路原始名称'
			}, {
				xtype : 'textfield',
				id : 'modifyUserLabel',
				width : 193,
				fieldLabel : '链路规范名称'
			}, {
				fieldLabel : 'A端节点',
				border : false,
				layout : 'column',
				items : [ {
					xtype : 'textfield',
					disabled : true,
					id : 'modifyA_END_NAME',
					width : 200
				} ]
			}, {
				fieldLabel : 'Z端节点',
				border : false,

				layout : 'column',
				items : [ {
					xtype : 'textfield',
					disabled : true,
					id : 'modifyZ_END_NAME',
					width : 200
				} ]
			},

			{
				fieldLabel : '链路方向',
				xtype : 'radiogroup',
				disabled : true,
				id : 'modify_radiogroup',
				anchor : '95%',
				columns : 2,
				items : [ {
					boxLabel : "单向",
					name : 'modify_direction',
					inputValue : 0
				}, {
					boxLabel : "双向",
					name : 'modify_direction',
					inputValue : 1
				} ]
			}
//			, {
//				fieldLabel : '锁定',
//				xtype : 'radiogroup', 
//				id : 'modify1_radiogroup',
//				anchor : '95%',
//				columns : 2,
//				items : [ {
//					boxLabel : "锁定",
//					name : 'modify_locked',
//					inputValue : 1
//				}, {
//					boxLabel : "不锁定",
//					name : 'modify_locked',
//					inputValue : 0
//				} ]
//			} 
			]
		});
		var modifyPanel = new Ext.Panel({
			id : 'modifyPanel',
			autoHeight : true,
			autoWidth : true,
			border : false,
			autoScroll : true,
			items : [modifyLinkForm ]
		});
		
		Ext.getCmp('modifyNativeLinkName').setValue(cell[0].get('NATIVE_EMS_NAME'));
		Ext.getCmp('modifyUserLabel').setValue(cell[0].get('USER_LABEL'));
//		Ext.getCmp('modify1_radiogroup').setValue(cell[0].get('IS_LOCKED'));
		Ext.getCmp('modifyA_END_NAME').setValue(cell[0].get('A_NE_NAME')+"_"+cell[0].get('A_END_PORT'));
		Ext.getCmp('modifyZ_END_NAME').setValue(cell[0].get('Z_NE_NAME')+"_"+cell[0].get('Z_END_PORT'));
		Ext.getCmp('modify_radiogroup').setValue(cell[0].get('DIRECTION'));
		selectLinkId = cell[0].get('BASE_LINK_ID');
		isManual = cell[0].get('IS_MANUAL');
		
		// 将formPanel放入弹窗中，形成一个新增链路的弹窗；
		var modifyLinkWindow = new Ext.Window({
			id : 'modifyLink',
			title : '修改链路',
			width : 400,
			height : 260,
			minWidth : 350,
			minHeight : 100,
			layout : 'fit',
			plain : false,
			modal : true,
			constrain : true,
			resizable : false,
			closeAction : 'hide',// 关闭窗口
			bodyStyle : 'padding:1px;',
			// buttonAlign : 'center',
			items : [ modifyPanel ],
			buttonAlign : 'right',
			buttons : [
					{
						text : '确定',
						handler : function() {
							if(modifyLinkForm.form.isDirty() && modifyLinkForm.form.isValid()){
							// 将链路名称，链路id，AZ端ptpid，链路方向信息传给后台
							var map = {
								"nativeLinkName" : Ext.getCmp('modifyNativeLinkName').getValue(),
								"userLabel" : Ext.getCmp('modifyUserLabel').getValue(),
								"direction" : Ext.getCmp('modify_radiogroup')
										.getValue().inputValue,
								"linkId" : selectLinkId,
								"A_END_PTP" : modifyA_END_PTP,
								"Z_END_PTP" : modifyZ_END_PTP
//								"islocked" : Ext.getCmp('modify1_radiogroup').getValue().inputValue,
							};
							var jsonData = {
								"jsonString" : Ext.encode(map)
							};
							Ext.Ajax.request({
								url : 'circuit!addLink.action',
								method : 'POST',
								params : jsonData,
								success : function(response) {
									Ext.getBody().unmask();
									var obj = Ext.decode(response.responseText);
									modifyA_END_PTP = -1;
									modifyZ_END_PTP = -1;
									Ext.Msg.alert("提示", '修改链路成功！');
									// 清除表单内容
									modifyLinkForm.getForm().reset();
									// 关闭弹窗
									modifyLinkWindow.close();
									var pageTool = Ext.getCmp('pageTool');
									if (pageTool) {
										pageTool.doLoad(pageTool.cursor);
									}

								},
								failure : function(response) {
									Ext.getBody().unmask();
									Ext.Msg.alert("提示", "更新失败");
									modifyA_END_PTP = -1;
									modifyZ_END_PTP = -1;
									newLinkForm.getForm().reset();
									newLinkWindow.hide();
								}
							});
						}else{
							// 关闭弹窗
							modifyLinkWindow.close();
						}
						}
					}, {
						text : '取消',
						handler : function() {
							modifyA_END_PTP = -1;
							modifyZ_END_PTP = -1;
							modifyLinkWindow.close();
							//modifyLinkForm.getForm().reset();
						}
					} ]
		});
		
		modifyLinkWindow.show();
	}
}
 
var linkInfoWindow = null;
function initData(cell,obj){  
    linkInfoWindow.findById('origName').setValue(cell.get('NATIVE_EMS_NAME'));
	linkInfoWindow.findById('nativeName').setValue(cell.get('USER_LABEL'));
	linkInfoWindow.findById('aPort').setValue(cell.get('A_END_PORT')); 
	linkInfoWindow.findById('zPort').setValue(cell.get('Z_END_PORT'));  
	cableComboGridField.comboGrid.store.load({
		scope:cableComboGridField,
		params:{
			'aStationId':cell.get('A_STATION_ID')?cell.get('A_STATION_ID'):0,
			'zStationId':cell.get('Z_STATION_ID')?cell.get('Z_STATION_ID'):0,
		    'DIRECTION':1
		},
		callback:function(r,options,success){
			this.comboGrid.setValue(cell.get('cableId'));
			fiberComboGridField.comboGrid.setValue(cell.get('fiberId'));
		}
	});
	if(!!obj){
		Ext.getCmp("origName1").setVisible(true);
		Ext.getCmp("nativeName1").setVisible(true);
		Ext.getCmp("aPort1").setVisible(true); 
		Ext.getCmp("zPort1").setVisible(true);
		Ext.getCmp("cable1ComboGridField").setVisible(true);
		Ext.getCmp("fiber1ComboGridField").setVisible(true);
		linkInfoWindow.findById('origName1').setValue(cell.get('NATIVE_EMS_NAME'));
		linkInfoWindow.findById('nativeName1').setValue(cell.get('USER_LABEL'));
		linkInfoWindow.findById('aPort1').setValue(cell.get('Z_END_PORT')); 
		linkInfoWindow.findById('zPort1').setValue(cell.get('A_END_PORT'));  
		cable1ComboGridField.comboGrid.store.load({
			scope:cable1ComboGridField,
			params:{
				'aStationId':cell.get('A_STATION_ID')?cell.get('A_STATION_ID'):0,
				'zStationId':cell.get('Z_STATION_ID')?cell.get('Z_STATION_ID'):0,
				'DIRECTION':1
			},
			callback:function(r,options,success){
				this.comboGrid.setValue(obj.cableId_);
				fiber1ComboGridField.comboGrid.setValue(obj.fiberId_);
			}
		}); 
	}else{
		Ext.getCmp("origName1").setVisible(false);
		Ext.getCmp("nativeName1").setVisible(false);
		Ext.getCmp("aPort1").setVisible(false); 
		Ext.getCmp("zPort1").setVisible(false);
		Ext.getCmp("cable1ComboGridField").setVisible(false);
		Ext.getCmp("fiber1ComboGridField").setVisible(false);
	} 
	linkInfoWindow.show(); 
}
var linkId,linkId_=null;
function relateFiber(){   
	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();   
	if (cell.length == 1) {  
		linkId= cell[0].get("BASE_LINK_ID");
		if(cell[0].get('DIRECTION')==1){
			Ext.Ajax.request({
				url : 'circuit!getRelateInfo.action',
				params : {
					linkId:cell[0].get("BASE_LINK_ID"),
					aNodeId:cell[0].get("aEndPtp"),
					zNodeId:cell[0].get("zEndPtp")
				},
				success : function(response) { 
					var obj = Ext.decode(response.responseText); 
					if (obj.returnResult == 1) {  
						linkId_= obj.linkId_;
					    initData(cell[0],obj);
					} 
					if (obj.returnResult == 0) {  
						Ext.Msg.alert('提示', obj.returnMessage);
					}
				},
				error:function(response) { 
		        	Ext.Msg.alert("异常",response.responseText);
			    },
			    failure:function(response) { 
		        	Ext.Msg.alert("异常",response.responseText);
			    }
			});
		}else{
			   initData(cell[0]);
		}  
	} else if (cell.length == 0) {
		Ext.Msg.alert("提示", "请选择一条数据！");
		return;
	} else {
		Ext.Msg.alert("提示", "只能选择一条数据进行关联 ！");
		return;
	} 
}

function cancelRelateFiber(){
	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();  
	if(cell.length==0){
		Ext.Msg.alert("提示","请选择数据！");
		return;
	} else{ 
		for(var i=0;i<cell.length;i++){
			if(cell[i].get("fiber")==""){
				Ext.Msg.alert("提示","链路未关联光纤！");
				return;
			}
		}
		Ext.Msg.confirm("提示", "确认删除和光缆光纤的关联？", function(btn) {
			if (btn == "yes") {   
				var map = new Array();
				for(var i=0;i<cell.length;i++){
					var relFiber= {
						"BASE_LINK_ID":cell[i].get("BASE_LINK_ID"),
						"DIRECTION":cell[i].get("DIRECTION"),
						"aNodeId":cell[i].get("aEndPtp"),
						"zNodeId":cell[i].get("zEndPtp")
			    	};
					map.push(relFiber);
				}  
				Ext.Ajax.request({
					url : 'circuit!cancelRelateFiber.action',
					params : {
						"jsonString":Ext.encode(map)
					},
					success : function(response) { 
						var obj = Ext.decode(response.responseText); 
						if (obj.returnResult == 1) {  
							Ext.Msg.alert("提示",obj.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							});  
						} 
						if (obj.returnResult == 0) {  
							Ext.Msg.alert('提示', obj.returnMessage);
						}
					},
					error:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    },
				    failure:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    }
				});
			}
		});
	}
}


/*
 * 3.5导入链路信息------------------------------------------------------------------------
 */

function importLinks() {
	fileUploadWindow.show();
}
/*
 * 3.6导出链路信息-------------------------------------------------------------------------
 */

function exportLinks(exportType) {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "信息列表为空！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				exportRequest();
			}
		});
	} else
		exportRequest(exportType);
}

var exportRequest = function(exportType) {
	var url = 'circuit-export!exportExcel.action';
	if(exportType == 1){
		url = 'circuit-export!linksOnPageExport.action';
	}
	eastPanel.getEl().mask("正在导出...");
	Ext.Ajax.request({
		url : url,
		type : 'post',
		params : {
			"jsonString" : Ext.encode(exportData)
		},
		success : function(response) {
			eastPanel.getEl().unmask();
			var rs = Ext.decode(response.responseText);
			if (rs.returnResult == 1 && rs.returnMessage != "") {
				var destination = {
					"filePath" : rs.returnMessage
				};
				window.location.href = "download!execute.action?"
						+ Ext.urlEncode(destination);
			} else {
				eastPanel.getEl().unmask();
				Ext.Msg.alert("提示", "导出失败！");
			}
		},
		error : function(response) {
			eastPanel.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			eastPanel.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
};
/*
 * 3.7
 * 确定AZ端端口-----------------------------------------------------------------------
 */
function getPort() {
	var iframe = window.frames[selectedTree] || window.frames[0];
	// 兼容不同浏览器的取值方式
	if (iframe.getCheckedNodes) {

		result = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "text",
				"path:nodeId", "path:nodeLevel", "path:text", "emsId" ], "top");
	} else {
		result = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel",
				"text", "path:nodeId", "path:nodeLevel", "path:text",  "emsId" ], "top");
	}
}

/*
 * 4、加载页面=====================================================================================
 */

var linkInfoPanel = new Ext.FormPanel({
    id : "form",
	frame : false,
	border : false,
	bodyStyle : 'padding:20px 10px 20px 20px', 
	labelWidth : 90,
	labelAlign : 'left',
	forcefit:true,
	split : true,
	items : [{   
		xtype : 'textfield',
		id : 'origName', 
		fieldLabel : '链路原始名称',  
		readOnly:true,
		width:220
	},{
		xtype : 'textfield',
		id : 'nativeName', 
		fieldLabel : '链路自定义名称', 
		readOnly:true,
		width:220
	},{
		xtype : 'textfield',
		id : 'aPort', 
		fieldLabel : 'A端节点',  
		readOnly:true,
		width:220
	},{
		xtype : 'textfield',
		id : 'zPort', 
		fieldLabel : 'Z端节点',  
		readOnly:true,
		width:220
	},cableComboGridField,
    fiberComboGridField,{
        xtype : 'spacer',
        id : 'spacer',   
        width:220,
        border:false ,
        height:20
    },{   
		xtype : 'textfield',
		id : 'origName1', 
		fieldLabel : '链路原始名称',  
		readOnly:true,
		width:220
	},{
		xtype : 'textfield',
		id : 'nativeName1', 
		fieldLabel : '链路自定义名称', 
		readOnly:true,
		width:220
	},{
		xtype : 'textfield',
		id : 'aPort1', 
		fieldLabel : 'A端节点',  
		readOnly:true,
		width:220
	},{
		xtype : 'textfield',
		id : 'zPort1', 
		fieldLabel : 'Z端节点',  
		readOnly:true,
		width:220
	},cable1ComboGridField,fiber1ComboGridField],
	buttons : [ {
		scope : this,
		text : '确定',
		handler : function() {
			if(linkInfoPanel.form.isDirty() && linkInfoPanel.form.isValid()){
				if(cable1ComboGridField.value==""){
					return;
				}
				
				var param={
					"linkId" : linkId, 
					"fiberId" : fiberComboGridField.comboGrid.getValue()
				}; 
				if(!!linkId_){
					param["linkId_"]=linkId_; 
					param["fiberId_"]=fiber1ComboGridField.comboGrid.getValue(); 
				} 
				var jsonData = {
				    "jsonString":Ext.encode(param)
				};  
				Ext.Ajax.request({
					url : 'circuit!relateFiber.action',
					method : "POST",
					params : jsonData,
					success : function(response) { 
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							linkInfoWindow.hide(); 
							Ext.Msg.alert("提示", obj.returnMessage); 
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}  
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("信息", obj.returnMessage);
						}
					},
					error : function(response) {
						maskEl.unmask();
						Ext.Msg.alert("异常", response.responseText);
					},
					failure : function(response) {
						maskEl.unmask();
						Ext.Msg.alert("异常", response.responseText);
					}
				}); 
			} 
		}
	}, {
		text : '取消',
		handler : function() { 
			linkInfoWindow.hide(); 
		}
	}]
});


function init(){ 
	linkInfoWindow=new Ext.Window({
		title :"关联光纤",
		id : 'win', 
		modal : true,
		closable : true,
		plain : true,
		closeAction : 'close',
		width : 400, 
		forcefit:true, 
		items : [ linkInfoPanel ],  
		renderTo: Ext.getBody(), 
		autoScroll : true,
		closeAction: 'hide', 
	}); 
}

Ext.onReady(function() {
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 9000000;
	var win = new Ext.Viewport({
		id : 'win',
		title : "B类比较值设定",
		layout : 'border',
		items : [ westPanel, eastPanel ],
		renderTo : Ext.getBody()
	});
	win.show();  
	init();
	if (passedLinkId != "null") {
		var jsonData1 = {
			"jsonString" : passedLinkId,
			"limit" : myPageSize,
			"aNodeId" : 0,
			"aNodeLevel" : -1,
			"userId" : userId,
			"displayName" : displayName,
			"tag":2,
			"linkType":1
		};
		exportData = jsonData1;
		store.proxy = new Ext.data.HttpProxy({
			url : 'circuit!getLinksByIds.action'
		});
		store.baseParams = jsonData1;
		store.load({
			callback : function(r, options, success) {
				if (success) {
					selectTree = null;
					result = null;
				} else {
					selectTree = null;
					result = null;
					top.Ext.getBody().unmask();
					Ext.Msg.alert('提示', '没有查询到符合条件的电路');
				}
			}
		});
	}
});