// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓下拉框↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
//====起始日期====@
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
	// width : 160,
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
	// width : 160,
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

// ====创建人====@
var creatorStore = new Ext.data.Store({
	url : 'pm-report!getCreatorComboValuePrivilege.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "userName", "userId" ])
});
// 页面打开初始化创建人下拉框
creatorStore.load();
var creatorCombo = new Ext.form.ComboBox({
	id : 'creatorCombo',
	name : 'creatorCombo',
	fieldLabel : '创建人',
	store : creatorStore,
	valueField : 'userId',
	displayField : 'userName',
	editable : false,
	mode : 'local',
	// autoSelect:true,
	value : "全部",
	triggerAction : 'all',
	anchor : '85%',
	resizable : true,
	// width : 150,
	listeners : {
		select : loadTaskName
	}
});

// ====报表类型====@
var periodCb;
(function() {
	var data = [ [ 0, '日报' ], [ 1, '月报' ], [ 9, '全部' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'period'
		} ]
	});
	store.loadData(data);
	periodCb = new Ext.form.ComboBox({
		id : 'periodCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '类型',
		anchor : '85%',
		store : store,
		editable : false,
		// width : 150,
		value : 9,
		valueField : 'id',
		displayField : 'period',
		listeners : {
			select : loadTaskName
		}
	});
})();

// ====数据源=====@
var dataSrcComboStore;
(function() {
	var data = [ [ 0, '原始数据' ], [ 1, '异常数据' ], [ 9, '全部' ] ];
	dataSrcComboStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'src'
		} ]
	});
	dataSrcComboStore.loadData(data);
})();
var dataSrcCombo = new Ext.form.ComboBox({
	id : 'dataSrcCombo',
	name : 'dataSrcCombo',
	fieldLabel : '数据源',
	store : dataSrcComboStore,
	valueField : 'id',
	displayField : 'src',
	editable : false,
	mode : 'local',
	value : 9,
	triggerAction : 'all',
	anchor : '85%',
	listeners : {
		select : loadTaskName
	}
});

// ====任务名称====@
var taskNameStore = new Ext.data.Store({
	url : 'pm-report!getTaskNameComboValuePrivilege.action',
	storeId : 'taskId',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "taskName", "taskId", "taskType", "dataSrc", "period" ])
});
// 页面打开初始化创建人下拉框
taskNameStore.baseParams = {
	'searchCond.needAll' : 0
};
taskNameStore.load();

var taskNameCombo = new Ext.form.ComboBox({
	id : 'taskNameCombo',
	name : 'taskNameCombo',
	fieldLabel : '任务名称',
	sideText : '<font color=red>*</font>',
	store : taskNameStore,
	valueField : 'taskId',
	displayField : 'taskName',
	selectOnFocus : true,
	allowBlank : false,
	resizable : true,
	anchor : '85%',
	mode : 'local'
});

// ====任务名称加载方法====@
function loadTaskName() {
	var creator = Ext.getCmp('creatorCombo').getValue();
	var period = Ext.getCmp('periodCb').getValue();
	var dataSrc = Ext.getCmp('dataSrcCombo').getValue();
	taskNameStore.baseParams = {
		'searchCond.creator' : creator == '全部' ? 0 : creator,
		'searchCond.period' : period,
		'searchCond.dataSrc' : dataSrc,
		'searchCond.needAll' : 0
	};
	taskNameStore.load();
}
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓块Σ(⊙▽⊙"a 块↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
// ============@

var searchPanel = new Ext.form.FormPanel({
	id : 'searchPanel',
	border : false,
	region : 'north',
	height : 100,
	labelAlign : 'left',
	padding : 10,
	items : [ {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : 0.2,
			border : false,
			labelWidth : 60,
			layout : 'form',
			items : [ startTime, endTime ]
		}, {
			columnWidth : 0.2,
			border : false,
			labelWidth : 50,
			layout : 'form',
			items : [ creatorCombo, periodCb ]
		}, {
			columnWidth : 0.25,
			border : false,
			labelWidth : 60,
			layout : 'form',
			items : [ dataSrcCombo, taskNameCombo ]
		} ]
	} ],
	tbar : [ '-', {
		text : '查询',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/search.png',
		handler : searchAnalysis
	}, '-', {
		text : '重置',
		icon : '../../../resource/images/btnImages/arrow_undo.png',
		handler : function() {
			searchPanel.getForm().reset();
			Ext.getCmp('startTime').setValue('');
			Ext.getCmp('endTime').setValue('');
		}
	} ]
});
// ==============@
var sm = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});

var storeNe = new Ext.data.Store({
	url : 'pm-report!searchNeReportAnalysis.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "exportDate", "collectSuccessRate", "pmAbnormalRate", "abnormal", "reportId", "failedId",
			"taskName" ])
});

var cmNe = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'exportDate',
		header : '日期',
		dataIndex : 'exportDate',
		width : 100
	}, {
		id : 'collectSuccessRate',
		header : '网元监测成功率(成功/总数)',
		dataIndex : 'collectSuccessRate',
		width : 200
	}, {
		id : 'pmAbnormalRate',
		header : '端口性能正常率(正常/总数)',
		dataIndex : 'pmAbnormalRate',
		width : 200,
		// modified 2014/06/24 for wrong relationship between
		// ptpCollectSuccessRate and pmAbnormalRate
		hide : true,
		hideable : false
	}, {
		id : 'abnormal',
		header : '异常性能事件统计',
		dataIndex : 'abnormal',
		width : 200
	} ]
});

var storeMS = new Ext.data.Store({
	url : 'pm-report!searchMSReportAnalysis.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "exportDate", "ptpCollectSuccessRate", 'MSCollectSuccessRate', "pmAbnormalRate",
			"abnormal", "reportId", "MSFailedId", "ptpFailedId", "taskName" ])
});

var cmMS = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'exportDate',
		header : '日期',
		dataIndex : 'exportDate',
		width : 100
	}, {
		id : 'ptpCollectSuccessRate',
		header : '端口监测成功率(成功/总数)',
		dataIndex : 'ptpCollectSuccessRate',
		width : 200
	}, {
		id : 'MSCollectSuccessRate',
		header : '复用段监测成功率(成功/总数)',
		dataIndex : 'MSCollectSuccessRate',
		width : 200
	}, {
		id : 'pmAbnormalRate',
		header : '复用段性能正常率(正常/总数)',
		dataIndex : 'pmAbnormalRate',
		width : 200,
		// modified 2014/06/24 for wrong relationship between
		// ptpCollectSuccessRate and pmAbnormalRate
		hidden : true,
		hideable : false
	}, {
		id : 'abnormal',
		header : '复用段异常性能事件统计',
		dataIndex : 'abnormal',
		width : 200
	} ]
});

var dataGrid = new Ext.grid.GridPanel({
	id : 'dataGrid',
	title : '统计信息',
	region : 'center',
	store : storeNe,
	// height:300,
	cm : cmNe,
	loadMask : true,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ {
		xtype : 'tbseparator',
		hidden : true,
		id : 'separator0'
	}, {
		text : '监测失败端口',
		privilege : viewAuth,
		id : 'faliedPtp',
		hidden : true,
		//disabled:true,
		handler : showCollectFailedPtp
	}, '-', {
		text : '监测失败复用段',
		privilege : viewAuth,
		id : 'failedMS',
		hidden : true,
		//disabled:true,
		handler : showCollectFailedMS
	}, {
		text : '监测失败网元',
		privilege : viewAuth,
		id : 'failedNe',
		handler : showCollectFailedNe
	}, '-', {
		text : '导出',
		privilege : actionAuth,
		icon : '../../../resource/images/btnImages/export.png',
		handler : exportAnalysis
	}, '-', {
		text : '显示性能数据清单',
		privilege : viewAuth,
		handler : showOriginalData
	} ]
});

var northPanel = new Ext.Panel({
	id : 'northPanel',
	title : '查询条件',
	border : false,
	height : 300,
	collapsible : true,
	layout : 'border',
	region : 'north',
	items : [ searchPanel, dataGrid ]
});
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓CenterGrid↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
var smPm = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});

var storeNePm = new Ext.data.Store({
	url : 'pm-report!searchReportDetailNePm.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "ptpId", 'ctpId', "domain", "emsGroup", "ems", "ne", "neType", 'portDesc', 'ctp',
			"pmDesc", "pmValue", "exceptionLv",'location', 'retrievalTime', "pmStdIndex", "emsId", "targetType",
			"pmType", "unitId", "neId",
			"DISPLAY_SUBNET",
			"DISPLAY_AREA",
			"DISPLAY_STATION",
			"DISPLAY_PRODUCT_NAME",
			"PTP_TYPE",
			"RATE",
			"UNIT",
			"PM_COMPARE_VALUE_DISPLAY",
			"PM_COMPARE_VALUE",
			"EXCEPTION_COUNT",
			"THRESHOLD_1",
			"THRESHOLD_2",
			"THRESHOLD_3",
			"FILTER_VALUE",
			"OFFSET",
			"UPPER_VALUE",
			"UPPER_OFFSET",
			"LOWER_VALUE",
			"LOWER_OFFSET",
			"GRANULARITY"])
});

var cmNePm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),
			smPm,
			{
				id : 'ptpId',
				header : 'ptpId',
				dataIndex : 'ptpId',
				hidden : true,
				width : 100
			},
			{
				id : 'ctpId',
				header : 'ctpId',
				dataIndex : 'ctpId',
				hidden : true,
				width : 100
			},
			{
				id : 'pmStdIndex',
				header : 'pmStdIndex',
				dataIndex : 'pmStdIndex',
				hidden : true,
				width : 100
			},
			{
				id : 'emsId',
				header : 'emsId',
				dataIndex : 'emsId',
				hidden : true,
				width : 100
			},
			{
				id : 'targetType',
				header : 'targetType',
				dataIndex : 'targetType',
				hidden : true,
				width : 100
			},
			{
				id : 'emsGroup',
				header : '网管分组',
				dataIndex : 'emsGroup',
				width : 100
			},
			{
				id : 'ems',
				header : '网管',
				dataIndex : 'ems',
				width : 100
			},
			{
				id : 'ne',
				header : '网元',
				dataIndex : 'ne',
				width : 100
			},
			{
				id : 'neType',
				header : '网元类型',
				dataIndex : 'neType',
				width : 100,
				renderer : function(v) {
					var neTypes = [ "未知", "SDH", "WDM", "OTN", "PTN", "微波", "FTTX", "未知", "未知",
							"VirtualNE" ];
					neTypes[99] = "未知";
					return neTypes[v >> 0];
				}
			},{
				id : 'DISPLAY_AREA',
				header : top.FieldNameDefine.AREA_NAME,
				dataIndex : 'DISPLAY_AREA',
				width : 80,
				hidden : true
			},
			{
				id : 'DISPLAY_STATION',
				header : top.FieldNameDefine.STATION_NAME,
				dataIndex : 'DISPLAY_STATION',
				width : 80,
				hidden : true
			},
			{
				id : 'DISPLAY_PRODUCT_NAME',
				header : '型号',
				dataIndex : 'DISPLAY_PRODUCT_NAME',
				width : 110
			}, {
				id : 'portDesc',
				header : '端口',
				dataIndex : 'portDesc',
				width : 80
			}, {
				id : 'PTP_TYPE',
				header : '端口类型',
				dataIndex : 'PTP_TYPE',
				width : 60
			},
			{
				id : 'RATE',
				header : '速率',
				dataIndex : 'RATE',
				width : 50
			},{
				id : 'ctp',
				header : '通道',
				dataIndex : 'ctp',
				width : 80
			},{
				id : 'domain',
				header : '业务类型',
				dataIndex : 'domain',
				hidden : true,
				width : 100,
				renderer : function(v) {
					switch (v) {
					case 1:
						return "SDH";
					case 2:
						return "WDM";
					case 3:
						return "ETH";
					case 4:
						return "ATM";
					}
				}
			}, {
				id : 'pmDesc',
				header : '性能事件',
				dataIndex : 'pmDesc',
				width : 80
			}, {
				id : 'location',
				header : '方向',
				dataIndex : 'location',
				width : 100,
				renderer : function(v) {
					var locTypes = [ "本端接收", "远端接收", "本端发送", "远端发送", "双向" ];
					return locTypes[v - 1];
				}
			}, {
				id : 'pmValue',
				header : '性能值',
				dataIndex : 'pmValue',
				width : 80
			}, {
				id : 'UNIT',
				header : '单位',
				dataIndex : 'UNIT',
				width : 50
			},
			{
				id : 'PM_COMPARE_VALUE_DISPLAY',
				header : '性能比较值',
				dataIndex : 'PM_COMPARE_VALUE_DISPLAY',
				width : 120,
				renderer : function(v, metadata, record) {
					exLv = record.get('exceptionLv');
					if (exLv == 0) {
						return '<font color=black>' + v + '</font>';
					} else if (exLv == 1) {
						return '<font color=blue>' + v + '</font>';
					} else if (exLv == 2) {
						return '<font color=orange>' + v + '</font>';
					} else if (exLv == 3) {
						return '<font color=red>' + v + '</font>';
					}
				}
			},{
				id : 'PM_COMPARE_VALUE',
				header : '性能基准值',
				dataIndex : 'PM_COMPARE_VALUE',
				hidden : true,
				width : 65
			},
			{
				id : 'EXCEPTION_COUNT',
				header : '连续异常',
				dataIndex : 'EXCEPTION_COUNT',
				width : 60
			},
			{
				id : 'THRESHOLD_1',
				header : '计数值阈值1',
				dataIndex : 'THRESHOLD_1',
				hidden : true,
				width : 60
			},
			{
				id : 'THRESHOLD_2',
				header : '计数值阈值2',
				dataIndex : 'THRESHOLD_2',
				hidden : true,
				width : 60
			},
			{
				id : 'THRESHOLD_3',
				header : '计数值阈值3',
				dataIndex : 'THRESHOLD_3',
				hidden : true,
				width : 60
			},
			{
				id : 'FILTER_VALUE',
				header : '计数值过滤值',
				dataIndex : 'FILTER_VALUE',
				hidden : true,
				width : 60
			},
			{
				id : 'OFFSET',
				header : '物理量基准值偏差',
				dataIndex : 'OFFSET',
				hidden : true,
				width : 60
			},
			{
				id : 'UPPER_VALUE',
				header : '物理量上限值',
				dataIndex : 'UPPER_VALUE',
				hidden : true,
				width : 60
			},
			{
				id : 'UPPER_OFFSET',
				header : '物理量上限值偏差',
				dataIndex : 'UPPER_OFFSET',
				hidden : true,
				width : 60
			},
			{
				id : 'LOWER_VALUE',
				header : '物理量下限值',
				dataIndex : 'LOWER_VALUE',
				hidden : true,
				width : 60
			},
			{
				id : 'LOWER_OFFSET',
				header : '物理量下限值偏差',
				dataIndex : 'LOWER_OFFSET',
				hidden : true,
				width : 60
			},
			{
				id : 'GRANULARITY',
				header : '周期',
				dataIndex : 'GRANULARITY',
				width : 80,
				renderer : function(v) {
					switch (parseInt(v)) {
					case 1:
						return "15 min";
					case 2:
						return "24 hour";
					default:
						return null;
					}
				}
			},{
				id : 'retrievalTime',
				header : '采集时间',
				dataIndex : 'retrievalTime',
				width : 80
			} ]
});

var storeMSPm = new Ext.data.Store({
	url : 'pm-report!searchReportDetailMSPm.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ 'MSId', "emsGroup", "ems", "emsType", "TL", "MS", "direction", 'standardWave',
			'actualWave', 'MSStatus', 'retrievalTime' ])
});

var cmMSPm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), smPm, {
		id : 'MSId',
		header : 'MSId',
		dataIndex : 'MSId',
		hidden : true,
		width : 100
	}, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 100
	}, {
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 100
	}, {
		id : 'emsType',
		header : '网络类型',
		dataIndex : 'emsType',
		width : 100
	}, {
		id : 'TL',
		header : '干线名称',
		dataIndex : 'TL',
		width : 100
	}, {
		id : 'MS',
		header : '光复用段名称',
		dataIndex : 'MS',
		width : 80
	}, {
		id : 'direction',
		header : '方向',
		dataIndex : 'direction',
		width : 80,
		renderer : directionRender
	}, {
		id : 'standardWave',
		header : '标称波道数',
		dataIndex : 'standardWave',
		width : 100
	}, {
		id : 'actualWave',
		header : '实际波道数',
		dataIndex : 'actualWave',
		width : 80
	}, {
		id : 'MSStatus',
		header : '复用段状态',
		dataIndex : 'MSStatus',
		width : 80,
		renderer : MSStautsRender
	}, {
		id : 'retrievalTime',
		header : '采集时间',
		dataIndex : 'retrievalTime',
		width : 80
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : storeNePm,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var centerGrid = new Ext.grid.GridPanel({
	id : 'centerGrid',
	region : 'center',
	store : storeNePm,
	cm : cmNePm,
	title : '性能数据清单',
	loadMask : true,
	selModel : smPm,
	autoScroll : true,
	stripeRows : true,
	bbar : pageTool,
	tbar : [ {
		xtype : 'tbseparator',
		hidden : true,
		id : 'separator1'
	}, {
		text : '详情',
		hidden : true,
		id : 'MSDetail',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/setTask.png',
		handler : showMSDetail
	}, {
		xtype : 'tbseparator',
		id : 'separator2'
	}, {
		text : '性能趋势图',
		id : "showDiagram",
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/chart.png',
		handler : showPmDiagram
	}, {
		xtype : 'tbseparator',
		id : 'separator3'
	}, {
		text : '设为基准值',
		id : 'setCompareValue',
		privilege : modAuth,
		icon : '../../../resource/images/btnImages/set_baseline.png',
		handler : setPmCompare
	}, {
		xtype : 'tbseparator',
		id : 'separator4'
	}, {
		text : '只显示异常数据',
		id : 'showAbnormal',
		privilege : viewAuth,
		enableToggle : true,
		handler : showAbnormalData
	} ]
});

// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

// 查询统计信息
function searchAnalysis() {
	// if (searchPanel.getForm().isValid()) {
	var taskId = taskNameCombo.getValue();
	var startTime = Ext.getCmp('startTime').getValue();
	var endTime = Ext.getCmp('endTime').getValue();
	var end_Time = new Date(endTime);
	var start_Time = new Date(startTime);
	if (end_Time.getTime() <= start_Time.getTime()) {
		Ext.Msg.alert("提示", "结束日期必须晚于起始日期！");
		return;
	}
	var time = end_Time.getTime() - start_Time.getTime();
	var days = parseInt(time / (1000 * 60 * 60 * 24));
	if (days > 100) {
		Ext.Msg.alert('提示', '历史性能查询日期间隔不超过100天！');
		return;
	}
	if (startTime == "" || startTime == null) {
		Ext.Msg.alert("提示", "请选择起始日期！");
		return;
	}
	if (endTime == "" || endTime == null) {
		Ext.Msg.alert("提示", "请选择结束日期！");
		return;
	}
	if (taskId == "" || taskId == null) {
		Ext.Msg.alert("提示", "请选择报表任务！");
		return;
	}
	// var userId = Ext.getCmp('creatorCombo').getValue();
	// var period = Ext.getCmp('periodCb').getValue();
	// var dataSrc = Ext.getCmp('dataSrcCombo').getValue();

	var taskType = taskNameStore.getAt(taskNameStore.find('taskId', taskId)).get('taskType');
	var dataSrc = taskNameStore.getAt(taskNameStore.find('taskId', taskId)).get('dataSrc');
	var period = taskNameStore.getAt(taskNameStore.find('taskId', taskId)).get('period');
	if (dataSrc == 0)
		dataSrc = '原始数据';
	if (dataSrc == 1)
		dataSrc = '异常数据';
	if (period == 0)
		period = '日报';
	if (period == 1)
		period = '月报';
	var taskName = taskNameStore.getAt(taskNameStore.find('taskId', taskId)).get('taskName');

	var param = {
		"searchCond.taskId" : taskId,
		"searchCond.startTime" : startTime,
		"searchCond.endTime" : endTime
	};
	if (taskType == taskTypeMS) {
		dataGrid.reconfigure(storeMS, cmMS);
		centerGrid.reconfigure(storeMSPm, cmMSPm);
		pageTool.bindStore(storeMSPm);
		Ext.getCmp('failedMS').setVisible(true);
		Ext.getCmp('separator0').setVisible(true);
		Ext.getCmp('faliedPtp').setVisible(true);
		Ext.getCmp('failedNe').setVisible(false);
		Ext.getCmp('showDiagram').setVisible(false);
		Ext.getCmp('separator2').setVisible(false);
		Ext.getCmp('setCompareValue').setVisible(false);
		Ext.getCmp('separator3').setVisible(false);
		Ext.getCmp('MSDetail').setVisible(true);
		Ext.getCmp('separator1').setVisible(true);
		// 加载统计数据
		storeMS.baseParams = param;
		storeMS.load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "查询失败！");
				if (success) {
					// 把需要导出的文件名信息保存一下
					exportFilenameAnalysis = taskName + '-性能统计数据(' + startTime + '-' + endTime
							+ '-' + period + '-' + dataSrc + ')';
					exportFilenameAnalysisCFMS = exportFilenameAnalysis + '-采集失败复用段';
					exportFilenameAnalysisCFPTP = exportFilenameAnalysis + '-采集失败端口';
					reportTypeMark = taskType;
				}
			}
		});
	} else if (taskType == taskTypeNE) {
		dataGrid.reconfigure(storeNe, cmNe);
		centerGrid.reconfigure(storeNePm, cmNePm);
		pageTool.bindStore(storeNePm);
		Ext.getCmp('separator0').setVisible(false);
		Ext.getCmp('failedMS').setVisible(false);
		Ext.getCmp('faliedPtp').setVisible(false);
		Ext.getCmp('failedNe').setVisible(true);
		Ext.getCmp('showDiagram').setVisible(true);
		Ext.getCmp('separator2').setVisible(true);
		Ext.getCmp('setCompareValue').setVisible(true);
		Ext.getCmp('separator3').setVisible(true);
		Ext.getCmp('MSDetail').setVisible(false);
		Ext.getCmp('separator1').setVisible(false);
		// 加载统计数据
		storeNe.baseParams = param;
		storeNe.load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "查询失败！");
				if (success) {
					// 把需要导出的文件名信息保存一下
					exportFilenameAnalysis = taskName + '-性能统计数据(' + startTime + '-' + endTime
							+ '-' + period + '-' + dataSrc + ')';
					exportFilenameAnalysisCFNE = exportFilenameAnalysis + '-采集失败网元';
					reportTypeMark = taskType;
				}
			}
		});
	}
	// }
}
// 显示详细性能数据
function showOriginalData() {
	selected = dataGrid.getSelectionModel().getSelected();
	if (!selected) {
		Ext.Msg.alert('提示', '请选择报表！');
		return;
	}
	var reportId = selected.get('reportId');
	centerGrid.getStore().baseParams = {
		"searchCond.reportId" : reportId,
		"searchCond.dataSrc" : "normal",
		"start" : 0,
		"limit" : 200
	};
	centerGrid.getStore().load({
		callback : function(records, options, success) {
			if (success)
				reportIdMark = selected.get('reportId');
			if (!success)
				Ext.Msg.alert("提示", "查询失败！");
		}
	});
}

// 只显示异常数据
function showAbnormalData() {
	if (!reportIdMark) {
		return;
	}
	if (Ext.getCmp('showAbnormal').pressed) {// 按钮弹出状态
		centerGrid.getStore().baseParams = {
			"searchCond.reportId" : reportIdMark,
			"searchCond.dataSrc" : "abnormal",
			"start" : 0,
			"limit" : 200
		};
		centerGrid.getStore().load({
			callback : function(records, options, success) {
				if (!success) {
					Ext.Msg.alert("提示", "查询失败！");
					// Ext.getCmp('showAbnormal').toggle(false);
				}
			}
		});
	} else {
		centerGrid.getStore().baseParams = {
			"searchCond.reportId" : reportIdMark,
			"searchCond.dataSrc" : "normal",
			"start" : 0,
			"limit" : 200
		};
		centerGrid.getStore().load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "查询失败！");
				// Ext.getCmp('showAbnormal').toggle(true);
			}
		});
	}

}

// 导出
function exportAnalysis() {
	var list = new Array();
	if (!dataGrid.getStore().getCount() > 0)
		return;
	if (reportTypeMark == taskTypeNE) {
		dataGrid.getStore().each(function(rec) {
			var abnormal = rec.get('abnormal').replace(/<[^>]*>/g, '');
			var record = {
				"exportDate" : rec.get('exportDate'),
				"collectSuccessRate" : rec.get('collectSuccessRate'),
				"pmAbnormalRate" : rec.get('pmAbnormalRate'),
				"abnormal" : abnormal
			};
			list.push(Ext.encode(record));
		});
		var params = {
			'modifyList' : Ext.encode(list),
			'searchCond.filename' : exportFilenameAnalysis,
			'searchCond.exportType' : 1
		};
	} else if (reportTypeMark == taskTypeMS) {
		dataGrid.getStore().each(function(rec) {
			var abnormal = rec.get('abnormal').replace(/<[^>]*>/g, '');
			var record = {
				"exportDate" : rec.get('exportDate'),
				"ptpCollectSuccessRate" : rec.get('ptpCollectSuccessRate'),
				"MSCollectSuccessRate" : rec.get('MSCollectSuccessRate'),
				"pmAbnormalRate" : rec.get('pmAbnormalRate'),
				"abnormal" : abnormal
			};
			list.push(Ext.encode(record));
		});
		var params = {
			'modifyList' : Ext.encode(list),
			'searchCond.filename' : exportFilenameAnalysis,
			'searchCond.exportType' : 2
		};
	}
	Ext.getBody().mask('正在导出，请稍候...');
	// 调用方法 如
	post('pm-report!exportAndDownloadPmAnalysisInfo.action', params);
	// var url = "pm-report!exportAndDownloadPmAnalysisInfo.action?"
	// + Ext.urlEncode(params);
	// alert(url);
	// window.location.href = url;
	Ext.getBody().unmask();
}

function post(URL, PARAMS) {
	var temp = document.createElement("form");
	temp.action = URL;
	temp.method = "post";
	temp.style.display = "none";
	for ( var x in PARAMS) {
		var opt = document.createElement("textarea");
		opt.name = x;
		opt.value = PARAMS[x];
		// alert(opt.name)
		temp.appendChild(opt);
	}
	document.body.appendChild(temp);
	temp.submit();
	return temp;
}

// 显示趋势图
function showPmDiagram() {
	var cell = centerGrid.getSelectionModel().getSelected();
	if (cell) {
		var type = cell.get('domain');
		var url = getDiagramURL(type, cell);
		parent.parent.addTabPage(url, "显示性能趋势图");
	} else {
		Ext.Msg.alert('信息', '请选择记录！');
	}
	;
}

// 产生趋势图的链接
function getDiagramURL(type, record) {
	var url = '../../jsp/performanceManager/PMsearch/performanceDiagram.jsp?';
	var pmStdIndex = record.get('pmStdIndex');
	var emsConnectionId = record.get('emsId');
	var targetType = record.get('targetType');
	var starttime = record.get('retrievalTime');
	var id;
	if (targetType == 7)
		id = 'ptpId=' + record.get('ptpId');
	if (targetType == 8)
		id = 'ctpId=' + record.get('ctpId');
	if (targetType == 9)
		id = 'ctpId=' + record.get('ctpId');
	url = url + id + '&pmStdIndex=' + pmStdIndex + '&emsConnectionId=' + emsConnectionId + '&type='
			+ type + '&starttime=' + starttime;
	return url;
}

function showMSDetail() {
	var selected = centerGrid.getSelectionModel().getSelected();
	if (!selected) {
		Ext.Msg.alert('提示', '请选择一条记录！');
		return;
	}
	var MSId = selected.get('MSId');
	// alert(MSId);
	// return;
	var sec_name = selected.get('MS');
	var retrievalTime = selected.get('retrievalTime');
	var direction = selected.get('direction');
	var url = "../performanceManager/performanceReport/routeDetail.jsp?mul_id=" + MSId
			+ "&retrievalTime=" + retrievalTime + "&direction=" + direction;
	parent.addTabPage(url, "路由详情(" + sec_name + ")");
}

function setPmCompare() {
	var selected = centerGrid.getSelectionModel().getSelected();
	if (selected) {
		if (selected.get("pmType") != 1) {
			Ext.Msg.alert('信息', '请选择物理量！');
			return;
		}
		Ext.Msg.confirm('确认', '确认将当前数据设为基准值？', function(btn) {
			if (btn == 'yes') {
				var pmList = new Array();
				var pm = {
					"TARGET_TYPE" : selected.get("targetType"),
					"PM_STD_INDEX" : selected.get("pmStdIndex"),
					"PM_DESCRIPTION" : selected.get("pmDesc"),
					"PM_VALUE" : selected.get("pmValue"),
					"BASE_OTN_CTP_ID" : selected.get("domain") == 2 ? selected.get('ctpId') : null,
					"BASE_SDH_CTP_ID" : selected.get("domain") == 1 ? selected.get('ctpId') : null,
					"BASE_PTP_ID" : selected.get("ptpId"),
					"BASE_UNIT_ID" : selected.get("unitId"),
					"BASE_NE_ID" : selected.get("neId"),
					"DISPLAY_CTP" : selected.get("ctp")
				};
				pmList.push(Ext.encode(pm));
				var jsonData = {
					"modifyList" : pmList
				};
				Ext.getBody().mask("正在执行,请稍候");
				Ext.Ajax.request({
					url : 'pm-search!setCompareValueFromPm.action',
					params : jsonData,
					method : 'POST',
					success : function(response) {
						Ext.getBody().unmask();
						var result = Ext.util.JSON.decode(response.responseText);
						if (result) {
							Ext.Msg.alert("提示", result.returnMessage);
						}
					},
					failure : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert("提示", "保存基准值出错!");
					},
					error : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert("提示", "保存基准值出错!");
					}
				});
			}
		});
	} else {
		Ext.Msg.alert('信息', '请选择记录！');
	}
}

function MSStautsRender(v) {
	switch (v) {
	case '0':
		return '<font color=black>正常</font>';
	case '1':
		return '<font color=blue>一般预警</font>';
	case '2':
		return '<font color=orange>次要预警</font>';
	case '3':
		return '<font color=red>重要预警</font>';
	case '4':
		return '<font color=black>数据不完整</font>';
	}
}

function directionRender(v) {
	if (v == "1")
		return "单向";
	if (v == "2")
		return "双向";
}
// ----------------------------------------------------
Ext.onReady(function() {
	Ext.Ajax.timeout = 900000;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	var win = new Ext.Viewport({
		id : 'win',
		title : "性能报表查询",
		layout : 'border',
		items : [ northPanel, centerGrid ],
		renderTo : Ext.getBody()
	});
	win.show();
});