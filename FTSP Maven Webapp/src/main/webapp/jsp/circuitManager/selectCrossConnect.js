/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";

// --------------------srore for createCircle---------------------
var myPageSize = 200;
var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	rootVisible : false,
	// 设定树只能单选
	checkModel : "single",
	leafType : 4
};
var exportData;
// 共通树url
var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
var connectRateData = [ [ 'VC12', 'VC12' ], [ 'VC3', 'VC3' ], [ 'VC4', 'VC4' ],
		[ 'VC4-4C', 'VC4-4C' ], [ 'VC4-8C', 'VC4-8C' ],
		[ 'VC4-16C', 'VC4-16C' ], [ 'VC4-64C', 'VC4-64C' ], [ '', '全部' ] ];
var store_connectRate = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]

});
store_connectRate.loadData(connectRateData);

var connectRateCombo = new Ext.form.ComboBox({
	id : 'connectRateCombo',
	store : store_connectRate,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : '',
	width : 100
});
// --------------------srore for circuitstate---------------------

var store_circuitstate = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
var circuitStateData = [ [ '1', '离散' ], [ '2', '正常' ], [ '0', '全部' ] ];
store_circuitstate.loadData(circuitStateData);
var circuitStateCombo = new Ext.form.ComboBox({
	id : 'circuitStateCombo',
	store : store_circuitstate,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : '0',
	width : 100
});
// ---------------------------store for
// crosschange--------------------------------

var store_crosschange = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
var crossChangeDate = [ [ '1', '新增' ], [ '2', '删除' ], [ '3', '不变' ],
		[ '0', '全部' ] ];
store_crosschange.loadData(crossChangeDate);
var crossChangeCombo = new Ext.form.ComboBox({
	id : 'crossChangeCombo',
	store : store_crosschange,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : '0',
	width : 100
});

var store_isFix = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
var isFixData = [ [ '0', '否' ], [ '1', '是' ], [ '2', '全部' ] ];
store_isFix.loadData(isFixData);
var isFixCombo = new Ext.form.ComboBox({
	id : 'isFixCombo',
	store : store_isFix,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : '0',
	width : 100
});

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

// ================stores===================
var JsonReader = new Ext.data.JsonReader({
	totalProperty : "total",
	root : "rows"
}, [ "BASE_OTN_CRS_ID","BASE_NE_ID","NE_DISPLAY_NAME", "DISPLAY_NAME", "A_END_CTP", "Z_END_CTP",
		"CONNECT_RATE", "CHANGE_STATE", "CIRCUIT_COUNT", "A_END_PORT",
		"Z_END_PORT", "IS_FIX" ]);
var store = new Ext.data.Store({
	reader : JsonReader,
	url : 'circuit!selectCrossConnect.action'
});

// ------------------------------StroeForTest---------------------------------

// ==========================page=============================
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
// {解决checkbox列无法锁定问题
checkboxSelectionModel.sortLock();
// }解决checkbox列无法锁定问题

var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	})
	// 如果需要添加选择框则取消注释，并将gridPanel中selModel注释取消
	, checkboxSelectionModel, {
		id : 'crossConnectionId',
		header : '交叉连接Id',
		dataIndex : 'BASE_OTN_CRS_ID',
		hidden : true,// hidden colunm
		width : 100
	}, {
		id : 'neId',
		header : '网元Id',
		dataIndex : 'BASE_NE_ID',
		hidden : true,// hidden colunm
		width : 100
	}, {
		id : 'emsName',
		header : '网管名称',
		hidden : true,
		dataIndex : 'DISPLAY_NAME',
		width : 100
	}, {
		id : 'neDisplayName',
		header : '网元名称',
		hidden : true ,
		dataIndex : 'NE_DISPLAY_NAME',
		width : 100
	}, {
		id : 'namePortA',
		header : 'A端节点',
		dataIndex : 'A_END_PORT',
		width : 250
	}, {
		id : 'nameCtpA',
		header : 'A端时隙',
		dataIndex : 'A_END_CTP',
		width : 100
	}, {
		id : 'namePortZ',
		header : 'Z端节点',
		dataIndex : 'Z_END_PORT',
		width : 250
	}, {
		id : 'nameCtpZ',
		header : 'Z端时隙',
		dataIndex : 'Z_END_CTP',
		width : 100
	}, {
		id : 'connectRate',
		header : '连接速率',
		dataIndex : 'CONNECT_RATE',
		width : 100
	}, {
		id : 'circuitstate',
		header : '交叉连接类别',
		dataIndex : 'CIRCUIT_COUNT',
		width : 100,
		renderer : function(v) {
			if (v > 0) {
				return "正常";
			} else {
				return "离散";
			}
		}
	}, {
		id : 'circuitchange',
		header : '交叉连接变化',
		dataIndex : 'CHANGE_STATE',
		width : 100,
		renderer : function(v) {
			if (v == 1) {
				return "新增";
			}
			if (v == 2) {
				return "删除";
			} else
				return "不变";
		}
	}, {
		id : 'IS_FIX',
		header : '固定连接',
		dataIndex : 'IS_FIX',
		width : 100,
		renderer : function(v) {
			if (v == 0) {
				return "否";
			}
			else if (v == 1) {
				return "是";
			}else{
				return v;
			}
		}
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	// title:'用户管理',
	cm : cm,
	store : store,
	// autoExpandColumn: 'roleName', // column with this id will be
	// expanded
	stripeRows : true, // 交替行效果
	loadMask : {
		msg : '正在执行，请稍后...'
	},
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	bbar : pageTool,
	tbar : [ "-"," 连接速率：", connectRateCombo, "-","交叉连接类别：", circuitStateCombo,
	         "-"," 交叉连接变化：", crossChangeCombo, '-', "固定连接：", isFixCombo,{
				text : '查询',
				privilege : viewAuth,
				icon : '../../resource/images/btnImages/search.png',
				handler : selectCrossConnect
			},"-", {
				text : '导出',
				privilege : actionAuth,
				icon : '../../resource/images/btnImages/export.png',
				handler : exportCrossConnect
			} ]

});

// =======================链路更新=========================

// -------------------------------失败网管再次更新-----------------------------------
function selectCrossConnect() {
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var checkedNodeIds;
	if (iframe.getCheckedNodes) {
		checkedNodeIds = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ],
				"top");
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes([ "nodeId",
				"nodeLevel" ], "top");
	}
	if (checkedNodeIds.length == 0 || checkedNodeIds[0].nodeLevel != 4
			|| checkedNodeIds.length > 1) {
		Ext.Msg.alert("提示", "请选择查询对象！");
	} else {
		var jsonData = {
			// "selectedTargets":checkedNodeIds,
			"connectRate" : Ext.getCmp('connectRateCombo').getValue(),
			"circuitState" : Ext.getCmp('circuitStateCombo').getValue(),
			"crossChange" : Ext.getCmp('crossChangeCombo').getValue(),
			"isFix" : Ext.getCmp('isFixCombo').getValue(),
			"jsonString" : Ext.encode(checkedNodeIds[0]),
			"limit" : myPageSize,
			"displayName" : displayName
		};
		exportData = jsonData;
//		store.proxy = new Ext.data.HttpProxy({
//			url : 'circuit!selectCrossConnect.action'
//		});
		store.baseParams = jsonData;
		store.load({
			callback : function(r, options, success) {
				if (success) {
				} else {
					Ext.Msg.alert('提示', '内部错误');
				}
				;
			}
		});
	}
	;
}

function exportCrossConnect() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				exportRequest();
			}
		});
	} else
		exportRequest();
}

var exportRequest = function() {
	gridPanel.getEl().mask("正在导出...");
	Ext.Ajax.request({
		url : 'circuit-export!exportCrossConnectExcel.action',
		type : 'post',
		params : exportData,
		success : function(response) {
			var rs = Ext.decode(response.responseText);
			if (rs.returnResult == 1 && rs.returnMessage != "") {
				gridPanel.getEl().unmask();
				var destination = {
					"filePath" : rs.returnMessage
				};
				window.location.href = "download!execute.action?"
						+ Ext.urlEncode(destination);
			} else {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "导出失败！");
			}
		},
		error : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
};
// var exportRequest=function(){
// Ext.Ajax.request({
// url : 'circuit!exportCrossConnectExcel.action',
// type : 'post',
// params : exportData,
// success : function(response) {
// Ext.Msg.alert("提示","success！");
// },
// error : function(response) {
// Ext.Msg.alert("异常", response.responseText);
// },
// failure : function(response) {
// Ext.Msg.alert("异常", response.responseText);
// }
// });
// };

// ----------------------------DeleteUserFunction---------------------------------

// -----------------------------------------init the
// page--------------------------------------------

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
		items : [ westPanel, gridPanel ],
		renderTo : Ext.getBody()
	});
	win.show();
});