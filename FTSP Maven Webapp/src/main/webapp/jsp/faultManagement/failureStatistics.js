//========日期下拉=======@
Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";

var today = new Date();
var todayStr = today.format("yyyy-MM-dd");
today.setDate(today.getDate() - 7);
var lastWeekStr = today.format("yyyy-MM-dd");

var startTime = {
	xtype : 'textfield',
	id : 'startTime',
	name : 'startTime',
	fieldLabel : '起始日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	width : 120,
	value : lastWeekStr,
	anchor : '85%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startTime",
				isShowClear : false,
				readOnly : true,
				dateFmt : 'yyyy-MM-dd',
				maxDate : '%y-%M-%d',
				autoPickDate : true
			});
			this.blur();
		}
	}
};

// ====结束日期====@
var endTime = {
	xtype : 'textfield',
	id : 'endTime',
	name : 'endTime',
	fieldLabel : '结束日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	anchor : '85%',
	width : 120,
	value : todayStr,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "endTime",
				isShowClear : false,
				readOnly : true,
				dateFmt : 'yyyy-MM-dd',
				maxDate : '%y-%M-%d',
				autoPickDate : true
			});
			this.blur();
		}
	}
};

// =========传输系统========@

var projectStore = new Ext.data.Store({
	url : 'project!getAllProject.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ {
		name : "projectName",
		mapping : "SYS_NAME"
	}, {
		name : "projectId",
		mapping : "RESOURCE_TRANS_SYS_ID"
	} ])
});
/*projectStore.baseParams = {
	'paramMap.needAll' : 0
};*/
projectStore.load();

var projectCombo = new Ext.ux.form.LovCombo({
	id : 'projectCombo',
	name : 'projectCombo',
	fieldLabel : '传输系统',
	hideOnSelect : false,
	store : projectStore,
	valueField : 'projectId',
	displayField : 'projectName',
	editable : false,
	mode : 'local',
	width : 120,
//	value : "全部",
	emptyText:'全部',
	triggerAction : 'all',
	anchor : '85%',
	resizable : true
});

// ====统计项目====@
var statisticItemCb;
(function() {
	var data = [ [ 0, '故障类型' ], [ 1, '故障原因' ], [ 2, '厂家' ], [ 3, '网元型号' ],
			[ 4, '板卡类型' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'statisticItem'
		} ]
	});
	store.loadData(data);
	statisticItemCb = new Ext.form.ComboBox({
		id : 'statisticItemCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '统计项目',
		anchor : '85%',
		store : store,
		editable : false,
		width : 120,
		value : 0,
		valueField : 'id',
		displayField : 'statisticItem'
	});
})();

// *******************************************************************
/**
 * 创建FusionCharts图panel 第一层
 */
var chartPanel_1 = new Ext.Panel(
		{
			id : 'chartPanel_1',
			layout : 'fit',
			width : '100%',
			border : false,
			html : '<div id="fushionChart1" style="text-align:center;margin:10px"></div>'
		});

/**
 * 创建FusionCharts图panel 第二层
 */
var chartPanel_2 = new Ext.Panel(
		{
			id : 'chartPanel_2',
			border : false,
			layout : 'fit',
			width : '100%',
			html : '<div id="fushionChart2" style="text-align:center;margin:10px"></div>'
		});
// Ext.apply(projectsComboGridBar,{labelWidth:30});
var panel = new Ext.Panel({
	id : 'centerPanel',
	region : "center",
	border : false,
	autoScroll : true,
	layout : 'column',
	items : [ {
		columnWidth : 0.5,
		border : false,
		layout : 'fit',
		items : chartPanel_1
	}, {
		columnWidth : 0.5,
		border : false,
		layout : 'fit',
		items : chartPanel_2
	} ],
	tbar : [ '-', '开始时间:', startTime, '-', '结束时间:', endTime, '-', '传输系统',
			projectCombo, '-', '统计项目', statisticItemCb, '-', {
				text : '统计',
				icon : '../../resource/images/btnImages/chart.png',
				handler : function(){
					doChartLeft();
					doChartRight();
				}
			/*
			 * function(){ var myChart = new
			 * FusionCharts("../../resource/FusionCharts/Charts/StackedColumn3D.swf",
			 * "chart2", '90%', Ext.getCmp('centerPanel').getHeight()*0.8);
			 * myChart.setDataURL("column.xml");
			 * Ext.getCmp('chartPanel_2').setHeight(Ext.getCmp('centerPanel').getHeight()*0.9);
			 * myChart.render("fushionChart2"); //---- var myChart1 = new
			 * FusionCharts("../../resource/FusionCharts/Charts/Pie3D.swf",
			 * "chart1", '90%', Ext.getCmp('centerPanel').getHeight()*0.8);
			 * myChart1.setDataURL("PIE.xml");
			 * Ext.getCmp('chartPanel_1').setHeight(Ext.getCmp('centerPanel').getHeight()*0.9);
			 * myChart1.render("fushionChart1"); }
			 */
			} ]
});

function doChartLeft() {
	var param = {
		'paramMap.projectIds' : Ext.getCmp('projectCombo').getValue(),
		'paramMap.start' : Ext.getCmp('startTime').getValue(),
		'paramMap.end' : Ext.getCmp('endTime').getValue()
	};
	Ext.Ajax.request({
		url : 'fault-statistics!getFaultStatisticsTotal.action',
		method : 'POST',
		params : param,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			var xml = obj.chartXml;
			if (xml != null && xml != '') {
				var chart = new FusionCharts(
						"../../resource/FusionCharts/Charts/Pie3D.swf",
						"chart1", "90%",
						Ext.getCmp('centerPanel').getHeight() * 0.8);
				chart.setDataXML(xml);
				Ext.getCmp('chartPanel_1').setHeight(
						Ext.getCmp('centerPanel').getHeight() * 0.9);
				chart.render("fushionChart1");
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}
function doChartRight() {
	var chartType = Ext.getCmp('statisticItemCb').getValue();
	var param = {
		'paramMap.projectIds' : Ext.getCmp('projectCombo').getValue(),
		'paramMap.start' : Ext.getCmp('startTime').getValue(),
		'paramMap.end' : Ext.getCmp('endTime').getValue(),
		'paramMap.chartType' : chartType
	};
	Ext.Ajax
			.request({
				url : 'fault-statistics!getFaultStatisticsClassify.action',
				method : 'POST',
				params : param,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					var xml = obj.chartXml;
					if (xml != null && xml != '') {
						var chart = new FusionCharts(
								"../../resource/FusionCharts/Charts/StackedColumn3D.swf",
								"chart2", '90%', Ext.getCmp('centerPanel')
										.getHeight() * 0.8);
						chart.setDataXML(xml);
						Ext.getCmp('chartPanel_2').setHeight(
								Ext.getCmp('centerPanel').getHeight() * 0.9);
						chart.render("fushionChart2");
					}
				},
				error : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				}
			});
}
Ext.onReady(function() {
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	var win = new Ext.Viewport({
		id : 'win',
		title : "故障管理",
		layout : 'border',
		items : [ panel ],
		renderTo : Ext.getBody()
	});
	win.show();
});