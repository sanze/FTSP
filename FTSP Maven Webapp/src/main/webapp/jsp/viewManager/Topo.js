
Ext.onReady(function(){
	var view = new Ext.Viewport({ 
		layout:'fit',
		items:[{
			region:'center',
			id:'topo',
			isLocalDebug:false,
			xtype:'flex'
		}]
	});
	view.show();
	Ext.getCmp("topo").on("initialize", function () {

	    Ext.Ajax.request({
	        url: "topo!getNode.action",
	        method : 'POST',
	        params:{"nodeId":-1,
					"nodeType":-1,
					"direction":"forward",
					"privilege":authSequence
					},
	        scope:this,
	        success:function(resp) {
	            dat = Ext.decode(resp.responseText);
	            if (dat&&dat.returnResult==0){
	            	Ext.Msg.alert("提示", dat.returnMessage);
	            }else{
	            	Ext.getCmp("topo").loadData(dat);
	            }
	        }
	    });
	});
});

function openBayface(neId, neName){
	parent.addTabPage("../viewManager/bayface.jsp?neId="+neId, "网元:"+neName, authSequence);
}

//查看网管属性
function openEMSAttributes(emsId){
	parent.addTabPage("../southConnectionManager/emsConnectionProperty.jsp?emsConnectionId="+emsId, 
			"网管属性(" + emsId + ")", authSequence);
}

//查看网管当前告警
function openEMSCurrentAlarm(emsId,emsGroupId){
	if(emsGroupId == -1){
		emsGroupId = null;
	}
	var emsInfo = "\"2-" + emsId + "\"";//4是网元的在节点中的级别
	parent.addTabPage("../faultManager/currentAlarm.jsp?view_emsId=" + emsId + 
			"&view_emsGroupId=" + emsGroupId +"&view_emsInfo=" + emsInfo, "当前告警", authSequence);
}

//查看网元当前告警
function openNeCurrentAlarm(neId,emsId,emsGroupId){
	if(emsGroupId == -1){
		emsGroupId = null;
	}
	var neInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	parent.addTabPage("../faultManager/currentAlarm.jsp?view_neId=" + neId + "&view_emsId=" + emsId
			+ "&view_emsGroupId=" + emsGroupId + "&view_neInfo=" + neInfo, "当前告警", authSequence);
}

//查看网元历史告警
function openNeHistoryAlarm(neId,emsId,emsGroupId){
	if(emsGroupId == -1){
		emsGroupId = null;
	}
	var neInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	parent.addTabPage("../faultManager/historyAlarm.jsp?view_neId=" + neId + "&view_emsId=" + emsId
			+ "&view_emsGroupId=" + emsGroupId + "&view_neInfo=" + neInfo, "历史告警", authSequence);
}

//查看连线告警
function openLinkAlarm(ptpIdStr){ 
	parent.addTabPage("../faultManager/currentAlarm.jsp?view_ptpId=" + ptpIdStr, "当前告警", authSequence);
}

//查看连接属性
function openLinkAttributes(linkIdStr){
	parent.addTabPage("../circuitManager/linkManager.jsp?linkId=" + linkIdStr, "链路管理", authSequence);
}

//查看SDH网元当前性能
function openSDHCurrentPM(neId){
	var nodeInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	parent.addTabPage("../performanceManager/PMsearch/SDHCurrent.jsp?nodeInfo=" + nodeInfo,
			"SDH/ETH当前性能查询", authSequence);
}

//查看WDM网元当前性能
function openWDMCurrentPM(neId){
	var nodeInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	parent.addTabPage("../performanceManager/PMsearch/WDMCurrent.jsp?nodeInfo=" + nodeInfo,
			"WDM当前性能查询", authSequence);
}

//查看SDH网元历史性能
function openSDHHistoryPM(neId){
	var nodeInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	parent.addTabPage("../performanceManager/PMsearch/SDHHistory.jsp?nodeInfo=" + nodeInfo,
			"SDH/ETH历史性能查询", authSequence);
}

//查看WDM网元历史性能
function openWDMHistoryPM(neId){
	var nodeInfo = "\"4-" + neId + "\"";//4是网元的在节点中的级别
	parent.addTabPage("../performanceManager/PMsearch/WDMHistory.jsp?nodeInfo=" + nodeInfo,
			"WDM历史性能查询", authSequence);
}







