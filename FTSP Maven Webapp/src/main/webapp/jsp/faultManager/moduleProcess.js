
/**
 * 创建主体部分
 */
var centerPanel = new Ext.Panel({
	//title : 'ssdd',
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'form'
//	items : gridPanel
});

//function initData(){
//	var panel = '';
//	for ( var i = 0; i < 2; i++) {
//		if(i%2==0){
//			// 一行中的一个
//			var childPanel = new Ext.Panel({
//				layout : 'form',
////				items : []
//				title : 'a'
//			});
//			// 一行
//			panel = new Ext.Panel({
//				layout : 'column',
//				items : childPanel
//			});
//		}else{
//			var childPanel = new Ext.Panel({
//				layout : 'form',
//				title : 'a'
//			});
//			panel.add(childPanel);
//			centerPanel.add(panel);
//		}
//		centerPanel.add(panel);
//	}
//}

function initData(){
	Ext.Ajax.request({
	    url: 'server-monitor!getAllServers.action',
	    method: 'POST',
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	// 服务器信息
	    	var server = obj.rows;
	    	if(server.length>0){
	    		var panel = '';
		    	for ( var i = 0; i < server.length; i++) {
		    		if(i%2==0){
		    			// 一行中的一个
		    			var childPanel = new Ext.Panel({
		    				layout : 'form',
		    				columnWidth : .4,
		    				border:false,
		    				items :[{
		    					layout : 'column',
		    					border : false,
		    					//height : 100,
		    					bodyStyle : 'padding:10px',
		    					//style : 'margin-left:20px;margin-top:20px;',
		    					items : [{
		    						layout : 'form',
		    						border : false,
		    						html : '<img src="../../resource/images/btnImages/server.png" onclick=gotoDetail("'+server[i].SERVER_IP+'","'+server[i].SERVER_NAME+'") style="cursor:hand;width:80px;height:95px"></img>'
		    					},{
		    						layout : 'form',
		    						id:'ipPanel'+i,
		    						border : true,
		    						items : [{
		    							layout : 'column',
		    							border : false,
		    							items : [{
		    								tag:'span',
		    								border : false,
		    					 		    html:'主机名：'
		    							},{
		    								tag:'span',
		    								border : false,
		    					 		    html:server[i].SERVER_NAME
		    							}]
		    						},{
		    							layout : 'column',
		    							border : false,
		    							items : [{
		    								tag:'span',
		    								border : false,
		    					 		    html:'主机IP1：'
		    							},{
		    								tag:'span',
		    								border : false,
		    					 		    html:server[i].SERVER_IP
		    							}]
		    						}]
		    					}]
		    				}]
		    			});
		    			//加入其它ip
    					var cnt=0;
		    			if(server[i].otherIps && server[i].otherIps.length>0){
		    				for(var m=0;m<server[i].otherIps.length;m++){
		    					if(server[i].otherIps[m] != server[i].SERVER_IP){
				    				var p = new Ext.Panel({
		    							layout : 'column',
		    							border : false,
		    							items : [{
		    								tag:'span',
		    								border : false,
		    					 		    html:'主机IP'+(cnt+2)+'：'
		    							},{
		    								tag:'span',
		    								border : false,
		    					 		    html:server[i].otherIps[m]
		    							}]
		    						});
				    				Ext.getCmp('ipPanel'+i).add(p);
				    				cnt++;
		    					}
		    				}
		    			}
		    			
		    			
		    			// 一行
		    			panel = new Ext.Panel({
		    				layout : 'column',
		    				items : childPanel,
		    				border:false
		    			});
		    		}else{
		    			var childPanel = new Ext.Panel({
		    				layout : 'form',
		    				columnWidth : .4,
		    				border:false,
		    				items :[{
		    					layout : 'column',
		    					border : false,
		    					//height : 100,
		    					bodyStyle : 'padding:10px',
		    					//style : 'margin-left:10px;margin-top:5px;',
		    					items : [{
		    						layout : 'form',
		    						border : false,
		    						html : '<img src="../../resource/images/btnImages/server.png" onclick=gotoDetail("'+server[i].SERVER_IP+'","'+server[i].SERVER_NAME+'") style="cursor:hand;width:80px;height:95px"></img>'
		    					},{
		    						layout : 'form',
		    						id:'ipPanel'+(i+1),
		    						border : true,
		    						items : [{
		    							layout : 'column',
		    							border : false,
		    							items : [{
		    								tag:'span',
		    								border : false,
		    					 		    html:'主机名：'
		    							},{
		    								tag:'span',
		    								border : false,
		    					 		    html:server[i].SERVER_NAME
		    							}]
		    						},{
		    							layout : 'column',
		    							border : false,
		    							items : [{
		    								tag:'span',
		    								border : false,
		    					 		    html:'主机IP1：'
		    							},{
		    								tag:'span',
		    								border : false,
		    					 		    html:server[i].SERVER_IP
		    							}]
		    						}]
		    					}]
		    				}]
		    			});
		    			
		    			
		    			//加入其它ip
		    			var cnt=0;
		    			if(server[i].otherIps && server[i].otherIps.length>0){
		    				for(var m=0;m<server[i].otherIps.length;m++){
		    					if(server[i].otherIps[m] != server[i].SERVER_IP){
				    				var p = new Ext.Panel({
		    							layout : 'column',
		    							border : false,
		    							items : [{
		    								tag:'span',
		    								border : false,
		    					 		    html:'主机IP'+(cnt+2)+'：'
		    							},{
		    								tag:'span',
		    								border : false,
		    					 		    html:server[i].otherIps[m]
		    							}]
		    						});
				    				Ext.getCmp('ipPanel'+(i+1)).add(p);
				    				cnt++;
		    					}
		    				}
		    			}
		    			
		    			
		    			
		    			panel.add(childPanel);
		    			centerPanel.add(panel);
		    		}
		    	}
		    	if(server.length%2!=0){
	    			centerPanel.add(panel);
	    		}
	    	}
	    	centerPanel.doLayout();
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	})
}

function gotoDetail(ip,name){
	//window.location.href="../statusManagement/sysMon.jsp?ipAddress="+ip;
	parent.addTabPage("../statusManagement/sysMon.jsp?ipAddress="+ip, name+"详情");
}

/**
 * 初始化EXT
 */
Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	initData();
	new Ext.Viewport({
		layout : 'border',
		items : centerPanel
	});
});