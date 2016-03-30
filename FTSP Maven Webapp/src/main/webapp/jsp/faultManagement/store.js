//每页显示数据个数默认为200
var pageToolConfig = {
		"pageSize" : 200,
		"displayInfo" : true,
		"displayMsg" : '当前 {0} - {1} ，总数 {2}',
		"emptyMsg" : '没有记录'
};

//定义一个alarmConvergenceStore和alarmConvergencePageTool
var alarmConvergenceStore_Equip = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : "total",
		root : "rows"
	}, ['alarm_name','severity','ne_name','start_time','clean_time','severity_name','alarm_id']),
	url : 'fault-statistics!getAlarmByFaultId.action'
});
var alarmConvergenceStore_Line = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : "total",
		root : "rows"
	}, ['alarm_name','severity','ne_name','start_time','clean_time','severity_name','alarm_id']),
	url : 'fault-statistics!getAlarmByFaultId.action'
});
var alarmConvergencePageTool = new Ext.PagingToolbar({
	id : 'alarmConvergencePageTool',
	store : alarmConvergenceStore_Equip,
	pageSize : pageToolConfig.pageSize,// 每页显示的记录值
	displayInfo : pageToolConfig.displayInfo,
	displayMsg : pageToolConfig.displayMsg,
	emptyMsg : pageToolConfig.emptyMsg
});

//定义一个alarmAndPerformAnalysisStore和alarmAndPerformAnalysisPageTool
var alarmAndPerformAnalysisStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : "total",
		root : "rows"
	}, []),
	url : 'circuit!selectLinks.action'
});
var alarmAndPerformAnalysisPageTool = new Ext.PagingToolbar({
	id : 'alarmAndPerformAnalysisPageTool',
	store : alarmAndPerformAnalysisStore,
	pageSize : pageToolConfig.pageSize,// 每页显示的记录值
	displayInfo : pageToolConfig.displayInfo,
	displayMsg : pageToolConfig.displayMsg,
	emptyMsg : pageToolConfig.emptyMsg
});

//定义一个faultManagementStore和faultManagementPageTool
var faultManagementStore = new Ext.data.Store({
	url : 'fault-management!getFaultList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : "total",
		root : "rows"
	}, ['FAULT_ID','FAULT_NO','REASON_NAME','SOURCE','TYPE','SYSTEM_NAME','EMS_NAME',
	    'IS_BROKEN','START_TIME','END_TIME','ACK_USER','FAULT_LAST','STATUS',
	    'FAULT_LOCATION','NE_NAME','UNIT_NAME','FACTORY','ALM_CLEAR_TIME','ALARM_LAST'
	])
});
var faultManagementPageTool = new Ext.PagingToolbar({
	id : 'faultManagementPageTool',
	store : faultManagementStore,
	pageSize : pageToolConfig.pageSize,// 每页显示的记录值
	displayInfo : pageToolConfig.displayInfo,
	displayMsg : pageToolConfig.displayMsg,
	emptyMsg : pageToolConfig.emptyMsg
});


