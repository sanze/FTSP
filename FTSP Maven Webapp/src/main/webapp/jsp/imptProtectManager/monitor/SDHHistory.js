
//-------------------电路任务用---------------
var TASK_PARAM = {
 TASK_CIR_LIST : null,
 TASK_NE_LIST : null,
 TASK_PTP_INFO_LIST : null,
 TASK_PTP_LIST : null,
 TASK_UNIT_LIST : null,
 IS_LOADED : false
};
// ****************************************查询结果******************

// renderer,转化为超链接
function toTemplateRenderer(value, metadata, record) {
	var infos = {
		"PM_STD_INDEX" : record.get("PM_STD_INDEX"),
		"PM_DESCRIPTION" : record.get("PM_DESCRIPTION"),
		"UNIT" : record.get("UNIT"),
		"THRESHOLD_1" : record.get("THRESHOLD_1"),
		"THRESHOLD_2" : record.get("THRESHOLD_2"),
		"THRESHOLD_3" : record.get("THRESHOLD_3"),
		"FILTER_VALUE" : record.get("FILTER_VALUE"),
		"OFFSET" : record.get("OFFSET"),
		"UPPER_OFFSET" : record.get("UPPER_OFFSET"),
		"LOWER_OFFSET" : record.get("LOWER_OFFSET")
	};
	infos = encodeURI(encodeURI(Ext.encode(infos)));
	return ((value == null) ? "" : "<a href='#' onclick=toDetailTemplate('" + infos + "',"
			+ record.get("TYPE") + ")>" + value + "</a>");
}

// 跳转至模板信息
function toDetailTemplate(infos, TYPE) {
	var url = 'templateInfo.jsp?isCurrent=0&infos=' + infos + '&TYPE=' + TYPE;
	var templateInfoWin = new Ext.Window({
		id : 'templateInfoWin',
		title : '性能分析模板',
		width : 360,
		height : 320,
		isTopContainer : true,
		modal : true,
		plain : true, // 是否为透明背景
		html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />'
	});
	templateInfoWin.show();
}

// 性能查询
function performanceSearchNe(nodelist,nodeLevel) {
	if (nodelist == null) {
		return;
	}
	var nodeList = new Array();
	for(var i=0;i<nodelist.length;i++){
		nodeList.push(Ext.encode({nodeId:nodelist[i],nodeLevel:nodeLevel}));
	}
	// 查询条件
	var params = {
			"objList" : nodeList
	};
	performanceSearch(params)
}


function performanceSearch(params){

	grid.getEl().mask("正在查询,请稍候");
	Ext.Ajax.request({
		url : "impt-protect-task!searchHistoryDataIntoTempTable.action",
		params : params,
		method : 'POST',
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				if (1 == result.returnResult) {
					searchTag = result.returnMessage;
					Ext.getCmp('dataKineCombo').reset();
					store.proxy = new Ext.data.HttpProxy({
						url : "pm-search!getHistorySdhPmDate.action"
					});
					store.baseParams = {
							"start" : 0,
							"limit" : 200,
							"userId" : userId,
							"searchCond.exception" : 2,//异常数据
							"searchCond.searchTag" : searchTag
					};
					dataKineCombo.setValue(2);
					store.load({
						callback : function(records, options, success) {
							if (!success) {
								Ext.Msg.alert("提示", "查询出错");
							}
							grid.getEl().unmask();
						}
					});
				} else {
					grid.getEl().unmask();
					Ext.Msg.alert("提示", result.returnMessage);
				}
			}
		},
		failure : function(response) {
			grid.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			grid.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

// 显示趋势图
function showPmDiagram() {
	var cell = grid.getSelectionModel().getSelections();
	if (cell.length == 1) {
		var url = getDiagramURL(1, cell[0]);
		parent.parent.addTabPage(url, "性能趋势图");
	} else {
		Ext.Msg.alert('信息', '请选择记录，每次只能选择一条！');
	}

}

// 导出 当前性能查询
function exportPerformanceSearch() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("信息", "导出信息为空！");
		return;
	} else {
		var params = {
			"userId" : userId,
			"searchCond.exception" : dataKineCombo.getValue(),
			"searchCond.searchTag" : searchTag,
			"searchCond.tempTableName" : 3
		}
		window.location.href = "pm-search!downloadPmResult.action?" + Ext.urlEncode(params);
	}
}
function init(){
	var jsonData = {
    		"taskId":taskId
    	};
	var nodeIdStr="";
	Ext.Ajax.request({
	      url:'impt-protect-task!getTaskTargetNe.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
            var obj = Ext.decode(response.responseText);
            neList = obj.neList;
            if(!!neList&&neList.length>0){
            	var nodeList = new Array();
            	for(var i=0;i<neList.length;i++){
            		nodeList.push(Ext.encode({
            			'nodeLevel':4,
            			'nodeId':neList[i]["nodeId"],
            			'emsId':neList[i]["emsId"]
            		}));
            	}
            	var params = {
            			"objList" : nodeList
            		};
            	performanceSearch(params);
            }
	      },
	      failure : function(response) {
			 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
			 	"<BR>Status:"+response.statusText||"unknow");
		 },
		 error : function(response) {
			 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
				"<BR>Status:"+response.statusText||"unknow");
		 }
	  });
}
function getPmByNe4Circuit(neInfoList,ptpInfoList){

	var params = {
			"ptpInfoList" : ptpInfoList,
			"neInfoList" : neInfoList
		};
	grid.getEl().mask("正在查询,请稍候");
	Ext.Ajax.request({
		url : "impt-protect-task!getHistoryPmDataCir.action",
		params : params,
		method : 'POST',
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				if (1 == result.returnResult) {
					searchTag = result.searchTag;
					Ext.getCmp('dataKineCombo').reset();
					store.proxy = new Ext.data.HttpProxy({
						url : "pm-search!getHistorySdhPmDate.action"
					});
					store.baseParams = {
							"start" : 0,
							"limit" : 200,
							"userId" : userId,
							"searchCond.exception" : 2,//异常数据
							"searchCond.searchTag" : searchTag
					};
					dataKineCombo.setValue(2);
					store.load({
						callback : function(records, options, success) {
							if (!success) {
								Ext.Msg.alert("提示", "查询出错");
							}
							grid.getEl().unmask();
						}
					});
				} else {
					grid.getEl().unmask();
					Ext.Msg.alert("提示", result.returnMessage);
				}
			}
		},
		failure : function(response) {
			grid.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			grid.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

//选中性能类型后重新查询
function dataKineComboListener(combo, record, index) {
	store.proxy = new Ext.data.HttpProxy({
		url : "pm-search!getHistorySdhPmDate.action"
	});
	store.baseParams = {
		"start" : 0,
		"limit" : 200,
		"userId" : userId,
		"searchCond.exception" : combo.getValue(),
		"searchCond.searchTag" : searchTag
	};
	grid.getEl().mask("正在查询,请稍候");
	store.load({
		callback : function(records, options, success) {
			if (!success) {
				Ext.Msg.alert("提示", "查询出错");
			}
			grid.getEl().unmask();
		}
	});
}

// *****************************页面布局及初始化***********************
var initialized = false;
Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 90000000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};

	// var column = cm.getColumnById("CYCLE");
	// column.hidden = true;
	// column.hideable = false;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ grid ],
		renderTo : Ext.getBody()
	});
	win.show();
	initialized = true;
	if(taskType == '设备'){
		if(!!parent.globalParams){
			var p = parent.globalParams;
			if(p.type == 'ne')
				window[p.functionName](p.nodelist,p.nodeLevel);
	//		else
	//			window[p.functionName](p.granularity,p.ptpList);
		}else{
			init();
		}
	}else if(taskType=='电路'){
		if(!!parent.globalParams){
			var p = parent.globalParams;
			if(p.type == 'ne')
				window[p.functionName](p.ptpInfoListSelect,p.neInfoList);
			else
				window[p.functionName](p.granularity,p.ptpList);
		}else{
			Ext.getBody().unmask('请稍后...');
			Ext.Ajax.request({
			    url: 'impt-protect-task!getItemsOfCircuitTask.action',
			    method: 'POST',
			    params: {'taskId' : taskId },
			    success: function(response) {
			    	Ext.getBody().unmask();
			    	var obj = Ext.decode(response.responseText);
			    	TASK_PARAM.TASK_CIR_LIST = obj.cirInfoList;
			    	TASK_PARAM.TASK_PTP_LIST = obj.ptpList;
			    	TASK_PARAM.TASK_NE_LIST = obj.neList;
			    	TASK_PARAM.TASK_UNIT_LIST = obj.unitList;
			    	TASK_PARAM.TASK_PTP_INFO_LIST = obj.ptpInfoList;
			    	TASK_PARAM.IS_LOADED = true;
			    	var neList = obj.neList;
			    	var infoList = obj.ptpInfoList;
			    	var ptpInfoListSelect = new Array();
					var neInfoList = new Array();
					for(var i=0;i<infoList.length;i++){
						ptpInfoListSelect.push(Ext.encode(infoList[i]));
						if(neList.indexOf(infoList[i].BASE_NE_ID)!=-1){
							if(neInfoList.indexOf(Ext.encode({BASE_NE_ID:infoList[i].BASE_NE_ID,BASE_EMS_CONNECTION_ID:infoList[i].BASE_EMS_CONNECTION_ID}))==-1)
								neInfoList.push(Ext.encode({BASE_NE_ID:infoList[i].BASE_NE_ID,BASE_EMS_CONNECTION_ID:infoList[i].BASE_EMS_CONNECTION_ID}));
						}
					}
			    	getPmByNe4Circuit(neInfoList,ptpInfoListSelect);
			    },
			    error:function(response) {
			    	Ext.getBody().unmask();
		        	Ext.Msg.alert("错误",response.responseText);
			    },
			    failure:function(response) {
			    	Ext.getBody().unmask();
		        	Ext.Msg.alert("错误",response.responseText);
			    }
			});
		}
	}	
//
//	store.proxy = new Ext.data.HttpProxy({
//		url : "pm-search!getHistorySdhPmDate.action"
//	});
//	store.baseParams = {
//		"start" : 0,
//		"limit" : 200,
//		"userId" : userId,
//		"searchCond.exception" : 1,
//		"searchCond.searchTag" : searchTag
//	};
//	if (nodeInfo) {
//		initHistoryPm(nodeInfo);
//	}
});