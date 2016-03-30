/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// 厂家combox
Ext.QuickTips.init(); 
/**
 * 1.HW 2.ZTE 3.朗讯 4.烽火 5.ALC 9.富士通
 * 
 * @type
 */
var factoryStore=new Ext.data.ArrayStore({
	fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
	data:[{key:0,value:"全部"}]
});
factoryStore.loadData(FACTORY,true);
var factoryCombo = {
	xtype : 'combo',
	id : 'factoryCombo',
	name : 'factoryCombo',
	fieldLabel : '厂家',
	store : factoryStore,
	displayField : "displayName",
	valueField : 'value',
	mode : 'local',
	triggerAction : 'all',
	editable : false,
	width : 150,
	value : 0
};

// 网管combox
var typeCombo = {
	xtype : 'combo',
	id : 'typeCombo',
	name : 'typeCombo',
	fieldLabel : '光放类型',
	store : new Ext.data.ArrayStore({
				fields : ['value', 'displayName'],
				data : [['0', '全部'], ['1', '后置放大器'], ['2', '前置放大器'],
						['3', '线路放大器']]
			}),
	displayField : "displayName",
	valueField : 'value',
	mode : 'local',
	triggerAction : 'all',
	editable : false,
	width : 150,
	value : "0"
};

// 光放型号
var modelStore = new Ext.data.Store({
			url : 'multiple-section!selectMultipleModel.action',
			baseParams : {
				"limit" : 200
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["MODEL"])
		});
// modelStore.load();

// 网管combox
var modelCombo = {
	xtype : 'combo',
	id : 'modelCombo',
	name : 'modelCombo',
	fieldLabel : '光放型号',
	store : modelStore,
	displayField : "MODEL",
	valueField : 'MODEL',
	triggerAction : 'all',
	editable : false,
	width : 150,
	value : "全部"
}
var jsonString = new Array();
var map = {
	"limit" : 200
};
jsonString.push(map);

var store = new Ext.data.Store({
			url : 'multiple-section!selectStandOptVal.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_STD_OPT_AMP_ID", "FACTORY", "TYPE", "MODEL",
							"MAX_OUT", "MIN_GAIN", "MAX_GAIN", "TYPICAL_GAIN",
							"MAX_IN", "MIN_IN", "TYPICAL_IN"])
		});
store.load();
// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
			singleSelect : true,
			header : ""
		});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [new Ext.grid.RowNumberer({
						width : 26
					}), checkboxSelectionModel, {
				id : 'PM_STD_OPT_AMP_ID',
				header : 'PM_STD_OPT_AMP_ID',
				dataIndex : 'PM_STD_OPT_AMP_ID',
				hidden : true
			}, {
				id : 'FACTORY',
				header : '厂家',
				width : 40,
				dataIndex : 'FACTORY',
				renderer : function(v, r, t) {
			    	for(var fac in FACTORY){
			    		if(v==FACTORY[fac]['key']){
				    		return FACTORY[fac]['value'];
				    	}
			    	}
			    	return v;
				}
			}, {
				id : 'TYPE',
				header : '光放类型',
				width : 80,
				dataIndex : 'TYPE',
				renderer : function(v, r, t) {
					if (v == 1) {
						return "后置放大器";
					} else if (v == 2) {
						return "前置放大器";
					} else if (v == 3) {
						return "线路放大器";
					} else {
						return "";
					}

				}
			}, {
				id : 'MODEL',
				header : '光放型号',
				width : 100,
				dataIndex : 'MODEL'
			}, {
				id : 'MAX_OUT',
				header : "最大输出功率(dBm)",
				width : 100,
				dataIndex : 'MAX_OUT'
			}, {
				id : 'MIN_GAIN',
				header : "增益最小值(dB)",
				dataIndex : 'MIN_GAIN',
				width : 100
			}, {
				id : 'MAX_GAIN',
				header : "增益最大值(dB)",
				width : 100,
				dataIndex : 'MAX_GAIN'
				
			}, {
				id : 'TYPICAL_GAIN',
				header : "增益典型值(dB)",
				width : 100,
				dataIndex : 'TYPICAL_GAIN'
			}, {
				id : 'MAX_IN',
				header : "输入最大值(dB)",
				width : 100,
				dataIndex : 'MAX_IN'
			}, {
				id : 'MIN_IN',
				header : "输入最小值(dB)",
				width : 100,
				dataIndex : 'MIN_IN'
			}, {
				id : 'TYPICAL_IN',
				header : "输入典型值(dB)",
				width : 100,
				dataIndex : 'TYPICAL_IN'
			}]
});

var pageTool = new Ext.PagingToolbar({
			id : 'pageTool',
			pageSize : 200,// 每页显示的记录值
			store : store,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var gridPanel = new Ext.grid.EditorGridPanel({
			id : "gridPanel",
			region : "center",
			// title:'任务信息列表',
			cm : cm,
			store : store, 
			// autoHeight:true,
			// autoExpandColumn: 'experimentType', // column with this id will
			// be expanded
			// collapsed: false, // initially collapse the group
			// collapsible: true,
			stripeRows : true, // 交替行效果
			loadMask : true,
			selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
			bbar : pageTool,
			// tbar: pageTool,
			forceFit : true,
			tbar : ['-','厂家：', factoryCombo, '-','光放类型：', typeCombo, '-','光放型号：', modelCombo, '-',
			   {text : '查询',
				icon : '../../../resource/images/btnImages/search.png',
				handler : function() {
					select();
				}}
 ]

		});

function select() {
	var jsonString = new Array();
	var factory = Ext.getCmp('factoryCombo').getValue();
	if (Ext.getCmp('factoryCombo').getValue() == "0") {
		factory = "";
	}

	var type = Ext.getCmp('typeCombo').getValue();
	if (Ext.getCmp('typeCombo').getValue() == "0") {
		type = "";
	}
	var model = Ext.getCmp('modelCombo').getValue();
	if (Ext.getCmp('modelCombo').getValue() == "全部") {
		model = "";
	}
	var map = {
		"FACTORY" : factory,
		"TYPE" : type,
		"MODEL" : model,
		"limit" : 200
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)

	};
	store.baseParams = jsonData;
	store.proxy = new Ext.data.HttpProxy({
				url : 'multiple-section!selectStandOptVal.action'
			});
	store.load({
				callback : function(r, options, success) {
					if (success) {

					} else {
						Ext.Msg.alert('错误', '更新失败！请重新更新');
					}
				}
			});
}


store.on("load", function(s) {
			store.rejectChanges ();
		});

function getSelectedId(){
	var returnValue;
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		returnValue = {};
		returnValue.id = cell[0].get('PM_STD_OPT_AMP_ID');
		returnValue.model = cell[0].get('MODEL');
	}
	return returnValue;
}

function init() {
	// Ext.getCmp('factoryCombo').setValue(0);
	// Ext.getCmp('typeCombo').setValue(0);
	// Ext.getCmp('modelCombo').setValue("");

}
Ext.onReady(function() {

	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	}
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;

	var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [gridPanel]
			});
	init();
		// win.show();
});
