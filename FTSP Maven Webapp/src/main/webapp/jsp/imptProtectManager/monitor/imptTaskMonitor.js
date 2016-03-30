//-------------------电路任务用---------------
var TASK_PARAM = {
 TASK_CIR_LIST : null,
 TASK_NE_LIST : null,
 TASK_PTP_LIST : null,
 TASK_PTP_INFO_LIST : null,
 TASK_CTP_INFO_LIST : null,
 IS_LOADED : false
};
//--------------------******------------------
// 拓扑图加载用
var rows = null;
var topo_initialized = false;
var topo = new Ext.Flex({
    id : "topo",
    xtype : 'flex',
    type : "apa",
    subType : 1
});
var initTopoUrl;
if (taskType == '设备'){
	initTopoUrl = 'impt-protect-task!getTopoDataEquip.action';
}else if (taskType == '电路') {
	initTopoUrl = 'impt-protect-task!getRouteTopoCircuit.action';
}

topo.on("initialize", function() {
	Ext.getBody().mask('请稍后...');
	Ext.Ajax.request({
		url : initTopoUrl,
		type : 'post',
		params : {
			'taskId' : taskId
		},
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if(!!obj){
				topo.loadData(obj);
				loadTaskItems(obj);
			}
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
});
function loadTaskItems(obj){
	if(!!obj.cirInfoList)
		TASK_PARAM.TASK_CIR_LIST = obj.cirInfoList;
	if(!!obj.ctpInfoList)
		TASK_PARAM.TASK_CTP_INFO_LIST = obj.ctpInfoList;
	var neList = new Array();
	var ptpList = new Array();
	var ptpInfoList = new Array();
	for(var i=0;i<obj.rows.length;i++){
		var item = obj.rows[i];
		if(item.nodeOrLine=='node'){
			if(neList.indexOf(item.nodeId)==-1)
				neList.push(item.nodeId);
		}else if(item.nodeOrLine=='line'){
			var line = item.linkAlarm;
			for(var j=0;j<line.length;j++){
				if(ptpList.indexOf(line[j].aEndPTP)==-1){
					ptpList.push(line[j].aEndPTP);
				}
				if(ptpList.indexOf(line[j].zEndPTP)==-1){
					ptpList.push(line[j].zEndPTP);
				}
			}
		}
	}
	TASK_PARAM.TASK_NE_LIST = neList;
	TASK_PARAM.TASK_PTP_LIST = ptpList;
	if(!!obj.ptpInfoList)
		TASK_PARAM.TASK_PTP_INFO_LIST = obj.ptpInfoList;
	TASK_PARAM.IS_LOADED = true;
}

var northPanel = new Ext.TabPanel({
	region : 'north',
	activeTab : 1,
	height : 350,
	deferredRender : false,
	split:true,
	items : [ {
		layout : 'fit',
		id : 'northtab1',
		title : 'GIS地图',
		items : [  ]
	}, {
		layout : 'fit',
		id : 'northtab2',
		title : '网络拓扑',
		items : topo
	} ]
});
var globalParams = null;
var centerPanel = new Ext.TabPanel({
	region : 'center',
	activeTab : 0,
//	deferredRender : false,
	layoutOnTabChange:true,
	items : [ {
		layout : 'fit',
		id : 'centertab1',
		title : '当前告警',
		html : "<iframe  id='currentAlarm' name = 'currentAlarm'  src = currentAlarm.jsp?"
			+ Ext.urlEncode({taskType:taskType,taskId:taskId,authSequence:authSequence})
			+ " height='100%' width='100%' frameBorder=0 border=0/>"
	}, {
		layout : 'fit',
		id : 'centertab2',
		title : '当前性能',
		html : "<iframe  id='currentPm' name = 'currentPm'  src = SDHCurrent.jsp?"
		+ Ext.urlEncode({taskType:taskType,taskId:taskId,authSequence:authSequence})
		+ " height='100%' width='100%' frameBorder=0 border=0/>"
	}, {
		layout : 'fit',
		id : 'centertab3',
		title : '设备性能越限',
		html : "<iframe  id='pmExceed' name = 'pmExceed'  src = pmExceedResult.jsp?"
			+ Ext.urlEncode({taskType:taskType,taskId:taskId,authSequence:authSequence})
			+ " height='100%' width='100%' frameBorder=0 border=0/>"
	}, {
		layout : 'fit',
		id : 'centertab4',
		title : '采集性能越限',
		html : "<iframe  id='historyPm' name = 'historyPm'  src = SDHHistory.jsp?"
			+ Ext.urlEncode({taskType:taskType,taskId:taskId,authSequence:authSequence})
			+ " height='100%' width='100%' frameBorder=0 border=0/>"
	} ],
	listeners:{
		tabchange : function(){
			globalParams = null;
		}
	}
});

Ext.getCmp("topo").on("vipmenu", function (e) {
    //console.table("菜单为：" + e.more.cmd);
    //console.table("参数为：" + Ext.encode(e.more.param));
    
    switch(e.more.cmd){
    case "CurrentAlarmNE":  // 网元当前告警
    	centerPanel.setActiveTab(0);
		var currentAlarm = window.frames["currentAlarm"];
		if(taskType == '设备'){
			if(!!currentAlarm.queryCurrentAlarm){
				currentAlarm.queryCurrentAlarm(e.more.param.toString(),NodeDefine.NE);
			}
		}else if(taskType == '电路'){
			var neList = e.more.param;
			var ptpInfo = TASK_PARAM.TASK_PTP_INFO_LIST;
			var ctpInfo = TASK_PARAM.TASK_CTP_INFO_LIST;
			var ptpList = new Array();
			var ctpList = new Array();
			for(var i=0;i<ptpInfo.length;i++){
				if(neList.indexOf(ptpInfo[i].BASE_NE_ID.toString())!=-1){
					ptpList.push(ptpInfo[i].BASE_PTP_ID);
				}
			}
			for(var i=0;i<ctpInfo.length;i++){
				if(neList.indexOf(ctpInfo[i].BASE_NE_ID.toString())!=-1){
					ctpList.push(ctpInfo[i].BASE_CTP_ID);
				}
			}
			currentAlarm.currentAlarm4Circuit(neList,ptpList,ctpList);
		}
		break;
    case "CurrentAlarmLink": // 链路当前告警
    	centerPanel.setActiveTab(0);
		 var currentAlarm = window.frames["currentAlarm"];
		 var ptpList = new Array();
		 var neList = new Array();
		 var link = e.more.param;
		 for(var i=0;i<link.length;i++){
			 if(ptpList.indexOf(link[i].aEndPTP)==-1)
				 ptpList.push(link[i].aEndPTP);
			 if(ptpList.indexOf(link[i].zEndPTP)==-1)
				 ptpList.push(link[i].zEndPTP);
		 }
		 if(taskType == '设备'){
			 if(!!currentAlarm.queryCurrentAlarm)
				currentAlarm.queryCurrentAlarm(ptpList.toString(),NodeDefine.PTP);
		 }else if(taskType == '电路'){
			 var ctpInfo = TASK_PARAM.TASK_CTP_INFO_LIST;
			 var ctpList = new Array();
			 for(var i=0;i<ctpInfo.length;i++){
					if(ptpList.indexOf(ctpInfo[i].BASE_PTP_ID.toString())!=-1){
						ctpList.push(ctpInfo[i].BASE_CTP_ID);
					}
				}
			 if(!!currentAlarm.currentAlarm4Circuit)
				 currentAlarm.currentAlarm4Circuit(neList,ptpList,ctpList);
		 }
		 break;
    case 'CurrentPMNe24h' :  // 网元24h当前性能
    	globalParams = null;
    	centerPanel.setActiveTab(1);
    	currentPMNe(e,2);
		break;
    case 'CurrentPMNe15min' :   // 网元15min当前性能
    	globalParams = null;
    	centerPanel.setActiveTab(1);
    	currentPMNe(e,1);
	    break;
    case 'CurrentPMLink24h' :  // Link15min当前性能
    	globalParams = null;
    	centerPanel.setActiveTab(1);
    	currentPMLink(e,2);
		break;
    case 'CurrentPMLink15min' :  // Link15min当前性能
    	centerPanel.setActiveTab(1);
    	currentPMLink(e,1);
    	break;
    case 'CollectPMExceed' : 
    	globalParams = null;
    	centerPanel.setActiveTab(3);
    	CollectPMExceed(e);
		break;
    case 'CollectPMExceedLink' : 
    	globalParams = null;
    	centerPanel.setActiveTab(3);
    	CollectPMExceedLink(e);
    	break;
    case "PMExceed":
    	centerPanel.setActiveTab(2);
    	var neIds = e.more.param;
    	var param = {};
    	if (taskType == '设备'){
    		param.ne = "(" + neIds.join(",") + ")";
    	}else{
    		var unitList = [], ptpList = [];
    		for(var i=0; i < TASK_PARAM.TASK_PTP_INFO_LIST.length; i++){
    			var ptpInfo = TASK_PARAM.TASK_PTP_INFO_LIST[i];
    			if(neIds.indexOf(ptpInfo.BASE_NE_ID) >= 0){
    				unitList.push(ptpInfo.BASE_UNIT_ID);
    				ptpList.push(ptpInfo.BASE_PTP_ID);
    	    	}
    		}
    		param.unit = "(" + unitList.join(",") + ")";
    		param.ptp = "(" + ptpList.join(",") + ")";
    	}
    	//当前时间
    	var d = new Date();
    	param.end = d.format("Y-m-d h:m:s");
//    	alert(param.end);
    	//24h之前
    	var pre = new Date(d - 24*60*60*1000);
    	param.start = pre.format("Y-m-d h:m:s");
    	var pmExceedFrame = window.frames["pmExceed"];
    	pmExceedFrame.loadData(param);
    	break;
    case "PMExceedLink":
    	centerPanel.setActiveTab(2);
    	var linkInfos = e.more.param;
    	var param = {};
    	if (taskType == '设备'){
    		param.ne = "(" + linkInfos.aNeId + "," + linkInfos.zNeId + ")";
    	}else{
    		var unitList = [];
    		for(var i=0; i < TASK_PARAM.TASK_PTP_INFO_LIST.length; i++){
    			var ptpInfo = TASK_PARAM.TASK_PTP_INFO_LIST[i];
    			if((ptpInfo.BASE_NE_ID == linkInfos.aNeId && ptpInfo.BASE_PTP_ID == linkInfos.aEndPTP)
    					|| (ptpInfo.BASE_NE_ID == linkInfos.zNeId && ptpInfo.BASE_PTP_ID == linkInfos.zEndPTP)){
    				unitList.push(ptpInfo.BASE_UNIT_ID);
    	    	}
    		}
    		param.unit = "(" + unitList.join(",") + ")";
    		param.ptp = "(" + linkInfos.aEndPTP + "," + linkInfos.zEndPTP + ")";
    	}
    	//当前时间
    	var d = new Date();
    	param.end = d.format("yyyy-MM-dd hh:mm:ss");
    	//24h之前
    	var pre = new Date(d - 24*60*60*1000);
    	param.start = pre.format("yyyy-MM-dd hh:mm:ss");
    	var pmExceedFrame = window.frames["pmExceed"];
    	pmExceedFrame.loadData(param);
    	break;
    case "SavePos":
    	var posArr = e.more.param;
    	var posArrStr = Ext.encode(posArr);
		Ext.Ajax.request({
	   			url : 'impt-protect-task!saveAPAPosition.action',
	   			params : {'param.posData' : posArrStr ,'param.SYS_TASK_ID':taskId},
	   			method : 'POST',
	   			success : function(response) {
	   				var result = Ext.decode(response.responseText);
	   				if (result.returnResult != 0) {
	   					Ext.Msg.alert("提示", result.returnMessage);
	   				}else{
	   					Ext.Msg.alert("提示", result.returnMessage);
	   				}
	   			},
	   			failure : function(response) {
	   				var result = Ext.decode(response.responseText);
	   				Ext.Msg.alert("提示", result.returnMessage);
	   			},
	   			error : function(response) {
	   				var result = Ext.decode(response.responseText);
	   				Ext.Msg.alert("提示", result.returnMessage);
	   			}
		});
    	break;
    }		
});

function currentPMNe(e,granularity){
	var currentPm = window.frames["currentPm"];
	if(taskType=='设备'){
		if(!!currentPm.performanceSearch && currentPm.initialized)
			currentPm.performanceSearch(e.more.param,NodeDefine.NE,granularity);
		else{
			globalParams = {
					type : 'ne',
					functionName : 'performanceSearch',
					nodelist : e.more.param,
					nodeLevel : NodeDefine.NE,
					granularity : granularity
			};
		}
	}else if(taskType == '电路'){
		var infoList = TASK_PARAM.TASK_PTP_INFO_LIST;
		var neList = e.more.param;
		var ptpInfoListSelect = new Array();
		var neInfoList = new Array();
		for(var i=0;i<infoList.length;i++){
			if(neList.indexOf(infoList[i].BASE_NE_ID.toString())!=-1){
				ptpInfoListSelect.push(Ext.encode(infoList[i]));
				if(neInfoList.indexOf(Ext.encode({BASE_NE_ID:infoList[i].BASE_NE_ID,BASE_EMS_CONNECTION_ID:infoList[i].BASE_EMS_CONNECTION_ID}))==-1)
					neInfoList.push(Ext.encode({BASE_NE_ID:infoList[i].BASE_NE_ID,BASE_EMS_CONNECTION_ID:infoList[i].BASE_EMS_CONNECTION_ID}));
			}
		}
		if(!!currentPm.performanceSearchCircuit && currentPm.initialized)
			currentPm.performanceSearchCircuit(ptpInfoListSelect,neInfoList,granularity);
		else{
			globalParams = {
					type : 'ne',
					functionName : 'performanceSearchCircuit',
					neInfoList : neInfoList,
					ptpInfoListSelect : ptpInfoListSelect,
					granularity : granularity
			};
		}
	}
}

function currentPMLink(e,granularity){
	var currentPm = window.frames["currentPm"];
	var neList = new Array();
	var ptpList = new Array();
	var link = e.more.param;
	for(var i=0;i<link.length;i++){
		if(ptpList.indexOf(link[i].aEndPTP)==-1)
			ptpList.push(link[i].aEndPTP);
		if(ptpList.indexOf(link[i].zEndPTP)==-1)
			ptpList.push(link[i].zEndPTP);
	 }
	if(!!currentPm.performanceSearchLink && currentPm.initialized )
		currentPm.performanceSearchLink(ptpList,granularity);
	else{
		globalParams = {
				type : 'link',
				functionName : 'performanceSearchLink',
				ptpList : ptpList,
				granularity : granularity
		};
	}
}

function CollectPMExceed(e){
	var historyPm = window.frames["historyPm"];
	if(taskType=='设备'){
		if(!!historyPm.performanceSearchNe && historyPm.initialized)
			historyPm.performanceSearchNe(e.more.param,NodeDefine.NE);
		else{
			globalParams = {
					type : 'ne',
					functionName : 'performanceSearchNe',
					nodelist : e.more.param,
					nodeLevel : NodeDefine.NE
			};
		}
	}else if(taskType == '电路'){
		var infoList = TASK_PARAM.TASK_PTP_INFO_LIST;
		var neList = e.more.param;
		var ptpInfoListSelect = new Array();
		var neInfoList = new Array();
		for(var i=0;i<infoList.length;i++){
			if(neList.indexOf(infoList[i].BASE_NE_ID.toString())!=-1){
				ptpInfoListSelect.push(Ext.encode(infoList[i]));
				if(neInfoList.indexOf(Ext.encode({BASE_NE_ID:infoList[i].BASE_NE_ID,BASE_EMS_CONNECTION_ID:infoList[i].BASE_EMS_CONNECTION_ID}))==-1)
					neInfoList.push(Ext.encode({BASE_NE_ID:infoList[i].BASE_NE_ID,BASE_EMS_CONNECTION_ID:infoList[i].BASE_EMS_CONNECTION_ID}));
			}
		}
		if(!!historyPm.getPmByNe4Circuit && historyPm.initialized)
			historyPm.getPmByNe4Circuit(neInfoList,ptpInfoListSelect);
		else{
			globalParams = {
					type : 'ne',
					functionName : 'getPmByNe4Circuit',
					neInfoList : neInfoList,
					ptpInfoListSelect : ptpInfoListSelect
			};
		}
	}
}


function CollectPMExceedLink(e){
	var historyPm = window.frames["historyPm"];
	var neList = new Array();
	var ptpList = new Array();
	var link = e.more.param;
	for(var i=0;i<link.length;i++){
		if(ptpList.indexOf(link[i].aEndPTP)==-1)
			ptpList.push(link[i].aEndPTP);
		if(ptpList.indexOf(link[i].zEndPTP)==-1)
			ptpList.push(link[i].zEndPTP);
	 }
	if(!!historyPm.performanceSearchNe && historyPm.initialized)
   		historyPm.performanceSearchNe(ptpList,NodeDefine.PTP);
   	else{
   		globalParams = {
   				type : 'link',
   				functionName : 'performanceSearchNe',
   				nodelist : ptpList,
   				nodeLevel : NodeDefine.PTP
   		};
   	}
}

Ext.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Ajax.timeout = 90000000;
			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ northPanel, centerPanel ]
			});
			win.show();
});
