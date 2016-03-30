
var dat;
var url;
var swfPanel = new Ext.Panel({ 
//     width : 1400,
//     height : 700,
     region:'center',
//     title : '链路评估视图',  
     autoScroll:true,
     items : {
         id : "ola",
         xtype : 'flex',
         type : 'ola'
     } 
});
// 视图初始化
Ext.getCmp("ola").on("initialize", function () {
	Ext.Ajax.request({
		url:"link-eval-topo!getSystemList.action",
		method: 'POST',
		params:{"netLevel":0}, //0：所有系统
		scope:this,
		success:function(resp){
			var result = Ext.decode(resp.responseText);
			if (result&&result.total>0){
				Ext.getCmp("ola").loadSystem(result);
			}
			if (result.returnResult==0) {
				Ext.Msg.alert("提示", result.returnMessage);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		},
		error : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		} 
	});
});
// 系统级别变更
Ext.getCmp("ola").on("levelchange", function (e) {
	var jsonData = {
			"netLevel":e.more.systemLevel
		};
	Ext.Ajax.request({
		url:"link-eval-topo!getSystemList.action",
		method:"POST",
		params:jsonData,
		scope:this,
		success:function(resp){
			var result = Ext.decode(resp.responseText);
			Ext.getCmp("ola").loadSystem(result);
		},
		failure : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		},
		error : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		} 
	});
});
// 系统选择变更
Ext.getCmp("ola").on("systemselect", function (e) {
	var jsonData = {
			"sysId":e.more.systemId,
			"evalTime":e.more.date
		};
	Ext.Ajax.request({
		url:"link-eval-topo!getLinkEvalTopoData.action",
		method:"POST",
		params:jsonData,
		scope:this,
		success:function(resp){
            dat = Ext.decode(resp.responseText); 
            if (dat&&dat.total>0){
	        	Ext.getCmp("ola").loadData(dat); 
			}
            if (dat.returnResult==0) {
				Ext.Msg.alert("提示", dat.returnMessage);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		},
		error : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		} 
	});
});
// 日期选择变更
Ext.getCmp("ola").on("datechange", function (e) {
	var jsonData = {
			"sysId":e.more.systemId,
			"evalTime":e.more.date
		};
	Ext.Ajax.request({
		url:"link-eval-topo!getLinkEvalTopoData.action",
		method:"POST",
		params:jsonData,
		scope:this,
		success:function(resp){
            dat = Ext.decode(resp.responseText); 
            if (dat&&dat.total>0){
	        	Ext.getCmp("ola").loadData(dat); 
			}
            if (dat.returnResult==0) {
				Ext.Msg.alert("提示", dat.returnMessage);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("提示", response.returnMessage);
		},
		error : function(response) {
			Ext.Msg.alert("提示", response.returnMessage);
		} 
	});
});
// 网元节点双击
Ext.getCmp("ola").on("nodedblclick", function (e) {
	var neId = e.more.nodeId;
	var neName = e.more.nodeName;
	parent.addTabPage("../viewManager/bayface.jsp?neId="+neId, "网元:"+neName, authSequence);
});
// 网元右键-当前告警
Ext.getCmp("ola").on("necurrentalarm", function (e) {
	var neId = e.more.neId;
	var neInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	var emsId = null;
	var emsGroupId = null;
	parent.addTabPage("../faultManager/currentAlarm.jsp?view_neId=" + neId 
		+ "&view_emsId=" + emsId + "&view_emsGroupId=" + emsGroupId +"&view_neInfo=" + neInfo, "当前告警", authSequence);
});
// 网元右键-历史告警
Ext.getCmp("ola").on("nehistoryalarm", function (e) {
	var neId = e.more.neId;
	var neInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	var emsId = null;
	var emsGroupId = null;
	parent.addTabPage("../faultManager/historyAlarm.jsp?view_neId=" + neId + "&view_emsId=" + emsId
			+ "&view_emsGroupId=" + emsGroupId + "&view_neInfo=" + neInfo, "历史告警", authSequence);
});
// 网元右键-当前性能
Ext.getCmp("ola").on("necurrentpm", function (e) {
	var neId = e.more.neId;
	var neType = e.more.neType;
	var nodeInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	if(neType==1){//SDH
		parent.addTabPage("../performanceManager/PMsearch/SDHCurrent.jsp?nodeInfo=" + nodeInfo,
				"SDH当前性能查询", authSequence);		
	}else if(neType==2){//WDM
		parent.addTabPage("../performanceManager/PMsearch/WDMCurrent.jsp?nodeInfo=" + nodeInfo,
				"WDM当前性能查询", authSequence);
	}
});
// 网元右键-历史性能
Ext.getCmp("ola").on("nehistorypm", function (e) {
	var neId = e.more.neId;
	var neType = e.more.neType;
	var nodeInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	if(neType==1){//SDH
		parent.addTabPage("../performanceManager/PMsearch/SDHHistory.jsp?nodeInfo=" + nodeInfo,
				"SDH历史性能查询", authSequence);
	}else if(neType==2){//WDM
		parent.addTabPage("../performanceManager/PMsearch/WDMHistory.jsp?nodeInfo=" + nodeInfo,
				"WDM历史性能查询", authSequence);
	}
});
// Link线右键-光路评估详情
Ext.getCmp("ola").on("ola_detail", function (e) {
	var linkId = e.more.linkId;
	var date = e.more.date;
	parent.addTabPage("../evaluateManager/fiberLinkEvaluate/attenuationSearch.jsp?linkId=" + linkId + "&endDate=" + date,
			"光纤链路评估详情", authSequence);
});
// Link线右键-光路评估趋势
Ext.getCmp("ola").on("ola_trend", function (e) {
	var linkId = e.more.linkId;
	var date = e.more.date;
	parent.addTabPage("../evaluateManager/fiberLinkEvaluate/performanceDiagram.jsp?linkId=" + linkId + "&endDate=" + date,
			"光纤链路评估趋势图", authSequence);
});
// Link线右键-连接属性
Ext.getCmp("ola").on("link_attr", function (e) {
	var linkId = e.more.linkId;
	parent.addTabPage("../evaluateManager/fiberLinkEvaluate/setting.jsp?linkId=" + linkId, "光纤链路评估设置", authSequence);
});

Ext.onReady(function(){
 	Ext.Msg = top.Ext.Msg;
 	Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
 	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
 	var win = new Ext.Viewport({
 		id : 'win',
 		loadMask : true,
 		layout : 'border',
 		items : [swfPanel],
 		renderTo : Ext.getBody()
 	});
 	win.show();
});
