/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

var store = new Ext.data.Store({
	url : 'regular-pm-analysis!getNeStateList.action',
	baseParams : {
		"searchCond.neId" : neId
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "EMS_DISPLAY_NAME", "BASE_NE_ID", "DISPLAY_NAME", "TYPE", "PRODUCT_NAME", "NE_LEVEL",
			"BELONG_TO_DATE", "COLLECT_TIME","COLLECT_TYPE", "COLLECT_RESULT" ])
});
var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
var d =  {rows:[{
	"EMS_DISPLAY_NAME" : "一干_兰成波分",
	"DISPLAY_NAME" : "兰州雁滩",
	"TYPE" : 2,
	"PRODUCT_NAME" : "ZXWM M900(100M)",
	"NE_LEVEL":1,
	"BELONG_TO_DATE":"2014-12-8",
	"COLLECT_TIME":"2014-12-8 21:23:22",
	"COLLECT_RESULT":"失败（失败原因:接口错误：网管获取数据异常！）"
},{
	"EMS_DISPLAY_NAME" : "一干_兰成波分",
	"DISPLAY_NAME" : "兰州雁滩",
	"TYPE" : 2,
	"PRODUCT_NAME" : "ZXWM M900(100M)",
	"NE_LEVEL":1,
	"BELONG_TO_DATE":"2014-12-9",
	"COLLECT_TIME":"2014-12-9 21:55:20",
	"COLLECT_RESULT":"成功（项数:23）"
}],total:1} ;
store.loadData(d);
// ************************* 任务信息列模型 ****************************
// var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),
		/*	{
				id : 'BASE_NE_ID',
				header : 'neId',
				dataIndex : 'BASE_NE_ID',
				width : 150,
				hidden : true,
				hideable : false
			},
			{
				id : 'EMS_DISPLAY_NAME',
				header : '网管',
				dataIndex : 'EMS_DISPLAY_NAME',
				width : 150
			},
			{
				id : 'DISPLAY_NAME',
				header : '网元',
				dataIndex : 'DISPLAY_NAME',
				width : 180
			},
			{
				id : 'TYPE',
				header : '类型',
				dataIndex : 'TYPE',
				width : 50,
				renderer : function(v) {
					if (v == 1) {
						return "SDH";
					} else if (v == 2) {
						return "WDM";
					} else if (v == 3) {
						return "OTN";
					} else if (v == 4) {
						return "PTN";
					} else {
						return "";
					}
				}
			},
			{
				id : 'PRODUCT_NAME',
				header : '型号',
				dataIndex : 'PRODUCT_NAME',
				width : 150
			},*/
			{
				id : 'BELONG_TO_DATE',
				header : '计划日期',
				dataIndex : 'BELONG_TO_DATE',
//				renderer : function(value) {
//					return value.time ? (Ext.util.Format.dateRenderer('Y-m-d'))(new Date(
//							value.time)) : "";
//				},
				width : 115
			}, {
				id : 'COLLECT_TIME',
				header : '采集时间',
				dataIndex : 'COLLECT_TIME',
				width : 120
//				,
//				renderer : function(value) {
//					return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//							value.time)) : "";
//				}
			}, {
				id : 'COLLECT_TYPE',
				header : '采集方式',
				dataIndex : 'COLLECT_TYPE',
				width : 60,
				renderer : function(v) {
					if (v == 1) {
						return "每天采";
					} else if (v == 2) {
						return "循环采";
					} else if (v == 3) {
						return "不采集";
					} else {
						return "";
					}
				}
			},{
				id : 'COLLECT_RESULT',
				header : '状态',
				dataIndex : 'COLLECT_RESULT',
				width : 200
			} ]
});
var today = new Date();
var todayStr = today.format("yyyy-MM-dd");
today.setDate(today.getDate()-29);//这里写29，不然包括首尾日期实际上是31天
var dateDefaultStr = today.format("yyyy-MM-dd");
//====开始日期====@
var startTime = {
	xtype : 'textfield',
	id : 'startTime',
	name : 'startTime',
	fieldLabel : '起始日期',
	value: dateDefaultStr,
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	 width : 100,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
				minDate:  '%y-%M-#{%d-364}',
//				minDate:  '#F{$dp.$D(\'endTime\',{d:-40})}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		}
	}
};

//====结束日期====@
var endTime = {
	xtype : 'textfield',
	id : 'endTime',
	name : 'endTime',
	fieldLabel : '结束日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	value: todayStr,
	anchor : '95%',
	 width : 100,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "endTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
				minDate:'#F{$dp.$D(\'startTime\',{d:0})}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		}
	}
};
var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	bbar : pageTool,
	stripeRows : true, // 交替行效果
	loadMask : true,
	// selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	// viewConfig : {
	// forceFit : true
	// },
	tbar : [ '-', "计划日期：从：", startTime, '-', "到：", endTime,'-',{
		text:'查询',
		icon:'../../../resource/images/btnImages/search.png',
		handler: search
	}]
});
function search(){
	var start = Ext.getCmp('startTime').getValue();
	var end = Ext.getCmp('endTime').getValue();
	var param = {
			"searchCond.startTime":start,
			"searchCond.endTime":end,
			"searchCond.neId" : neId,
			"start":0,
			"limit":200
	};
	store.baseParams = param;
	store.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "查询出错");
		}
	});
}
Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();        
	search();
//	store.load({
//		callback : function(records, options, success) {
//			if (!success)
//				Ext.Msg.alert("提示", "加载失败");
//		}
//	});
});
