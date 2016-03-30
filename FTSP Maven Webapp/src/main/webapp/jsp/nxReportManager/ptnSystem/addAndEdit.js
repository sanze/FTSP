var target = null;
var limit = null;
var nodeLevel = null;
(function() {

	switch (sysType) {
	case SYS_TYPE_DEFINE.DECENTRALIZED_RING:
		nodeLevel = CommonDefine.TREE.NODE.EMS;
		fieldLabel = "所属网管";
		break;
	case SYS_TYPE_DEFINE.CENTRALIZED_RING:
		nodeLevel = CommonDefine.TREE.NODE.NE;
		fieldLabel = "中心业务网元";
		limit = 2;
		break;
	case SYS_TYPE_DEFINE.CENTALIZED_CHAIN:
		fieldLabel = "中心业务网元";
		nodeLevel = CommonDefine.TREE.NODE.NE;
		limit = 1;
		break;
	}

	target = new Ext.ux.EquipTreeCombo({
		fieldLabel : fieldLabel,
		id : "targetId",
		width : 800,
		listWidth : null,
		checkableLevel : [ nodeLevel ],
		leafType : nodeLevel,
		allowBlank : false,
		checkModel : "single",
		rootVisible : false,
		listeners : {
			checkchange : function(node, checked) {
				var treePanel = Ext.getCmp('treePanel');
				if(!!!sysId)
					portStore.removeAll(); // 换的时候clear
				if (checked != 'all') {
					treePanel.setDisabled(true);
					return true;
				} else {
					treePanel.setDisabled(false);
					var Node = node.id.split("-");
					var targetLevel = Node[0];
					var targetId = Node[1];
					treePanel.setRoot(targetId, targetLevel);
				}
				return true;
			}
		}
	});
})();



var sysName = {
	xtype : 'textfield',
	fieldLabel : "系统名称",
	id : "sysName",
	anchor : '90%',
	sideText : '<font color=red>*</font>',
	allowBlank : false
};

var sysCapacityStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ 'key', 'value' ],
	data:[[1,1],[10,10],[40,40],[100,100]]
});
// 系统类型的下拉框
var sysCapacity = new Ext.form.ComboBox({
	fieldLabel : "系统容量",
	id : 'sysCapacity',
	typeAhead : true,
	allowBlank : false,
	editable:false,
	triggerAction : 'all',
	mode : 'local',
	width : 120,
	sideText : 'G<font color=red>*</font>',
	store : sysCapacityStore,
	valueField : 'key',
	value:10,
	displayField : 'value'
});

var infoPanel = new Ext.FormPanel({
	id : 'infoPanel',
	region : 'north',
	title : '系统信息',
	height : 120,
	bodyStyle : 'padding:30px 30px 0 30px',
	defaults : {
		// bodyStyle : 'padding:10px 0 10px 10px',
		border : false
	},
	items : [ {
		layout : "column",
		defaults : {
			columnWidth : 0.45,
			border : false,
			layout : 'form',
			labelWidth : 100,
			labelSeparator : "："
		},
		items : [ {
			items : sysName
		}, {
			items : sysCapacity
		} ]
	}, target ]
});
// ---------------------------------------------------

// %%%%%%%%%%%%%%%%%%%%%%%%%%%PORT%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


var treePanel = new Ext.ux.EquipTreePanel({
	id : 'treePanel',
	xtype : 'equiptree',
	rootVisible : false,
	title : "",
	region : "west",
	width : 250,
	checkModel : limit == 1 ? 'single' : 'multiple',
	split : true,
	// collapsible : true,
	collapseMode : 'mini',
	// autoScroll:true,
	boxMinWidth : 250,
	boxMinHeight : 260,
	forceSameLevel : true,
	checkableLevel : [ 8 ],
	leafType : 8
// filterBy: CommonDefine.filterNE_WDM,
// checkNodes: checkNodes,
// onGetChecked:onGetChecked,
// onCheckChange:onCheckChange,
// listeners:{
// afterrender : checkNode
// }
});



// ************************* 已选板卡 *****************************


var portStore = new Ext.data.Store({
	 url : 'nx-report!getLinksBySysId.action',
	reader : new Ext.data.JsonReader({
		root : 'links',
		fields : [ "aEndNe", "aEndPtp", "zEndNe", "zEndPtp", "targetId",
				"ptpId" ]
	})
});
var portSM = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});
var portCM = new Ext.grid.ColumnModel({
	defaults : {
		sortable : false
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26,
		locked : true
	}), portSM, {
		id : 'aEndNe',
		header : 'A端网元',
		dataIndex : 'aEndNe',
		width : 100
	}, {
		id : 'aEndPtp',
		header : 'A端端口',
		dataIndex : 'aEndPtp',
		width : 100
	}, {
		id : 'zEndNe',
		header : 'Z端网元',
		dataIndex : 'zEndNe',
		width : 100
	}, {
		id : 'zEndPtp',
		header : 'Z端端口',
		dataIndex : 'zEndPtp',
		width : 100
	} ]
});
var dupMark = false;
var portGrid = new Ext.grid.GridPanel({
	id : 'portGrid',
	store : portStore,
	cm : portCM,
	boxMinWidth : 400,
	loadMask : true,
	selModel : portSM,
	viewConfig : {
		forceFit : true
	},
	stripeRows : true,
	tbar : [ '-', {
		text : "上移",
		icon : "../../../resource/images/btnImages/up.png",
		handler : function() {
			var selection = portGrid.selModel.getSelections();
			if (!selection.length > 0) {
				Ext.Msg.alert("提示", "请先选取对象！");
				return;
			}
			upForward(portGrid, portStore);
		}
	}, '-', {
		text : "下移",
		icon : "../../../resource/images/btnImages/down.png",
		handler : function() {
			var selection = portGrid.selModel.getSelections();
			if (!selection.length > 0) {
				Ext.Msg.alert("提示", "请先选取对象！");
				return;
			}
			downForward(portGrid, portStore);
		}
	} ]
});
if (limit != null)
	portGrid.getTopToolbar().setVisible(false);
// ------------------------- 已选板卡 -----------------------------

var portInfo = new Ext.Panel({
	id : "portInfo",
	title : '系统结构',
	region : "center",
	layout : 'column',
	border : false,
	bodyStyle : 'padding:15px 30px 10px 30px',
	defaults : {
		height : 280
	},
	items : [ treePanel, {
		width : 50,
		border : false,
		layout : {
			type : 'vbox',
			pack : 'start', // 纵向对齐方式 start：从顶部；center：从中部；end：从底部
			align : 'center' // 对齐方式
		// center、left、right：居中、左对齐、右对齐；stretch：延伸；stretchmax：以最大的元素为标准延伸
		},
		defaults : {
			xtype : 'button'
		},
		items : [ {
			xtype : 'tbspacer', // 插入的空填充
			flex : 1
		}, {
			icon : '../../../resource/images/btnImages/right2.gif',
			height : 10,
			width : 40,
			flex : 1, // 表示当前子元素尺寸所占的均分的份数。
			handler : toRight
		}, {
			xtype : 'tbspacer', // 插入的空填充
			flex : 1
		}, {
			icon : '../../../resource/images/btnImages/left2.gif',
			height : 10,
			width : 40,
			flex : 1, // 表示当前子元素尺寸所占的均分的份数。
			handler : toLeft
		}, {
			xtype : 'tbspacer', // 插入的空填充
			flex : 1
		}, {
			icon : '../../../resource/images/btnImages/all.gif',
			height : 10,
			width : 40,
			flex : 1, // 表示当前子元素尺寸所占的均分的份数。
			handler : function() {
				Ext.Msg.confirm("确认", "确认清空？",function(btn){
					if(btn=='yes'){
						portStore.removeAll();
					}
				});
			}
		}, {
			xtype : 'tbspacer', // 插入的空填充
			flex : 1
		} ]
	}, portGrid ]
});

/**
 * 上移
 * 
 * @param {}
 *            forwardUpPanel panel
 * @param {}
 *            forwardUpStore store
 */
function upForward(forwardUpPanel, forwardUpStore) {
	var records = forwardUpPanel.getSelectionModel().getSelections();
	if (!!records && records.length > 1) {
		Ext.Msg.alert("提示", "只能选择一条记录！");
		return;
	}
	var record = records[0];
	var index = forwardUpStore.indexOf(record);
	if (index == 0) {
		return;
	}
	forwardUpStore.remove(record);
	forwardUpStore.insert(index - 1, record);
	forwardUpPanel.getView().refresh();
	forwardUpPanel.getSelectionModel().selectRow(index - 1);
}

/**
 * 下移
 * 
 * @param {}
 *            forwardUpPanel
 * @param {}
 *            forwardUpStore
 */
function downForward(forwardUpPanel, forwardUpStore) {
	var records = forwardUpPanel.getSelectionModel().getSelections();
	if (!!records && records.length > 1) {
		Ext.Msg.alert("提示", "只能选择一条记录！");
		return;
	}
	var record = records[0];
	var index = forwardUpStore.indexOf(record);
	if (index == forwardUpStore.getCount() - 1) {
		return;
	}
	forwardUpStore.remove(record);
	forwardUpStore.insert(index + 1, record);
	forwardUpPanel.getView().refresh();
	forwardUpPanel.getSelectionModel().selectRow(index + 1);
}

// =============================================================

var all = new Ext.Panel({
	id : 'all',
	border : false,
	layout : "border",
	boxMinHeight : 420,
	// height:500,
	boxMinWidth : 1000,
	defaults : {
		width : 900
	},
	items : [ infoPanel, portInfo ]
});

// **************************RENDERERS*************************


// **************************FUNCTIONS**************************
/**
 * 保存方向
 */
function save() {
	if (!infoPanel.getForm().isValid()) {
		Ext.Msg.alert("提示", "有必填项没有填写！");
		return;
	}
	if(portStore.getCount()==0){
		Ext.Msg.alert("提示", "请添加端口！");
		return;
	}
	var ptpList = new Array();
	portStore.each(function(r) { 
		ptpList.push(r.get('ptpId'));
	});
	var info = infoPanel.getForm().getValues();
	
	var target = Ext.getCmp('targetId').getCheckedNodes([ "nodeId", "emsId" ]);
	var params = {
		"paramMap.targetId" : target[0].nodeId,
		"paramMap.emsId" : target[0].emsId,
		"paramMap.sysName" : info.sysName,
		"paramMap.sysCapacity" : info.sysCapacity,
		"paramMap.sysType" : sysType,
		"intList" : ptpList
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'nx-report!savePtnSys.action',
		params : params,
		method : 'POST',
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult == 0) {
				Ext.Msg.alert("提示", result.returnMessage);
			}
			if (result.returnResult == 1) {
				parent.pageTool.doLoad(parent.pageTool.cursor);
				Ext.Msg.alert("提示", "数据保存成功!", function(btn) {
						parent.Ext.getCmp('addWindow').close();
				});
			}
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

/**
 * 修改时初始化
 */
function init() {
	 infoPanel.getForm().setValues(parent.VIEW_DATA);
	 if(!!sysId){
	Ext.getCmp("targetId").treeField.checkNodes(nodeLevel+"-"+parent.VIEW_DATA.targetId);
	}
	 var params = {
			 'paramMap.sysId':sysId
	 };
	 portStore.baseParams = params;
	 portStore.load();
//	 all.setDisabled(true);
}

function toRight() {
	var selection = treePanel.getCheckedNodes([ "nodeId" ]);
	if (!selection.length > 0) {
		Ext.Msg.alert("提示", "请先选取对象！");
		return;
	}
	if (!!limit && portStore.getCount()+selection.length > limit) {
		Ext.Msg.alert("提示", "超过" + limit + "个端口，请先移除端口！");
		return;
	}
	var list = new Array();
	for ( var i = 0; i < selection.length; i++) {
		if (portStore.find('ptpId', selection[i].nodeId) == -1)
			list.push(selection[i].nodeId);
	}
	if (list.length == 0)
		return;
	var params = {
		"intList" : list
	};

	Ext.Ajax.request({
		url : 'nx-report!getLinkByAEnd.action',
		params : params,
		method : 'POST',
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			if (!!result && result.returnResult == 1) {
				portStore.loadData(result, true);
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			gridPanel.getEl().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			gridPanel.getEl().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

function toLeft() {
	var record = portSM.getSelected();
	if (!!!record) {
		Ext.Msg.alert("提示", "请先选取对象！");
		return;
	}
	portStore.remove(record);
}

Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			Ext.Ajax.timeout = 900000;
			var subwin = new Ext.Viewport({
				id : 'subwin',
				layout : 'fit',
				autoScroll : true,
				items : [ all ]
			});
			subwin.show();
//			setTimeout("getEquipTreeNodes()", 1000);
			if(!!sysId)
				init();
		});
