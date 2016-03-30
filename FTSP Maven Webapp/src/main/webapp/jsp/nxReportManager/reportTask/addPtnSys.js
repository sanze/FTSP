var store_w = new Ext.data.Store({
	url : 'nx-report!getPtnSysList.action',
	baseParams : {},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "GROUP_NAME", "EMS_DISPLAY_NAME", "SYS_NAME", "SYS_CAPACITY",
			"SYS_TYPE", "T_RESOURCE_PTN_SYS_ID", "TARGET_ID" ])
});

var sm_w = new Ext.grid.CheckboxSelectionModel({
// singleSelect : true
});
var cm_w = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true,
		width : 100
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm_w, {
		id : 'GROUP_NAME',
		header : '网管分组',
		dataIndex : 'GROUP_NAME'
	}, {
		id : 'EMS_DISPLAY_NAME',
		header : '网管',
		dataIndex : 'EMS_DISPLAY_NAME'
	}, {
		id : 'SYS_NAME',
		header : '系统名称',
		dataIndex : 'SYS_NAME'
	}, {
		id : 'SYS_CAPACITY',
		header : '系统容量',
		dataIndex : 'SYS_CAPACITY'
	}, {
		id : 'SYS_TYPE',
		header : '系统类型',
		dataIndex : 'SYS_TYPE',
		width : 200,
		renderer : sysTypeRenderer
	} ]
});

// function
function search() {
	var emsGroup = Ext.getCmp('emsGroupCombo').getValue();
	var ems = Ext.getCmp('emsCombo').getValue();
	var sysType = Ext.getCmp('sysTypeCombo').getValue();
	var params = {
		'paramMap.emsGroupId' : emsGroup,
		'paramMap.emsId' : ems,
		'paramMap.sysType' : sysType
	};
	store_w.baseParams = params;
	store_w.load();
}

var grid = new Ext.grid.GridPanel({
	id : 'addPtnSysGrid',
	// border : false,
	cm : cm_w,
	store : store_w,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : sm_w, // 必须加不然不能选checkbox
	autoScroll : true,
	forceFit : true,
	frame : false,
	tbar : [ "网管分组：", emsGroupCombo, "网管：", emsCombo, "类型：", sysTypeCombo, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/search.png',
		text : '查询',
//		privilege : viewAuth,
		handler : search
	} ]
});

function add() {
	var sel = sm_w.getSelections();
	if (sel.length == 0) {
		Ext.Msg.alert("提示", "请选取要添加的系统！");
		return;
	}
	var records = [];
	for ( var i = 0; i < sel.length; i++) {
		if (parent.store.find("T_RESOURCE_PTN_SYS_ID", sel[i]
				.get("T_RESOURCE_PTN_SYS_ID")) == -1)
			records.push(sel[i]);
	}
	parent.store.add(records, true);
	parent.Ext.getCmp('addPtnSysWin').close();
}
	
Ext.onReady(function() {
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
		items : [ grid ]
	});
	subwin.show();
});
