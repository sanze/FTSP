var bay = new Ext.Flex({
	id : 'bay',
    type : "testBay",
	isLocalDebug : false,
	border:false 
});

var bayPanel = new Ext.Panel({
    region:'center',
	id : 'bayPanel',
	layout : 'fit',
	border : false,
	items : [bay]
}); 

bay.on("initialize", function () { 
	Ext.Ajax.request({
        url: "resource!queryRCCard.action",
        method: 'POST',
        params:{"cellId":rcId},
        scope:this,
        success:function(resp) {
            dat = Ext.decode(resp.responseText); 
            if (dat&&dat.returnResult==0){
            	Ext.Msg.alert("提示", dat.returnMessage);
            }else{
	        	Ext.getCmp("bay").loadData(dat); 
			}
        },
		failure : function(response) {
			Ext.Msg.alert("错误", response.returnMessage);
		},
		error : function(response) {
			Ext.Msg.alert("错误", response.returnMessage);
		} 
    }); 
});
 
//链接到当前告警页面
function openCurrentAlarm(rcId){
	var href = location.href;
	var index = href.indexOf("jsp") + 4;
	var preUrl = href.substr(0, index);
	var url = preUrl + "alarm/currentAlarm.jsp?rcId="+rcId;
	parent.addTabPage(url,"当前告警");
}

Ext.onReady(function(){
 	Ext.Msg = top.Ext.Msg;
 	Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
 	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
 	var win = new Ext.Viewport({
 		id : 'win',
 		loadMask : true,
 		layout : 'border',
 		items : [bayPanel],
 		renderTo : Ext.getBody()
 	});
 	win.show(); 
});
