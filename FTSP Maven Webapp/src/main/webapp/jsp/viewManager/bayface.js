
var dat;
var url;
var swfPanel = new Ext.Panel({ 
     width : 1400,
     height : 700,
     region:'center',
     title : '面板图',  
     autoScroll:true,
     items : {
         id : "flex",
         xtype : 'flex',
         type : 'bayface'
     } 
});

Ext.getCmp("flex").on("initialize", function () {
	//默认颜色配置 
	var arrColor = [0xff0000,0xff8000,0xffff00,0x800000,0x00ff00];
	function getBayfaceData(neId, specialShelfNo){
		Ext.Ajax.request({
	        url: "bayface!getBayfaceData.action",
	        method: 'POST',
	        params:{"neId":neId, "speShelfNo":specialShelfNo},
	        scope:this,
	        success:function(resp) {
	            dat = Ext.decode(resp.responseText); 
	            if (dat&&dat.returnResult==0){
	            	Ext.Msg.alert("提示", dat.returnMessage);
	            }else{
		        	Ext.getCmp("flex").loadData(dat); 
				}
	        },
			failure : function(response) {
				Ext.Msg.alert("提示", response.returnMessage);
			},
			error : function(response) {
				Ext.Msg.alert("提示", response.returnMessage);
			} 
	    });
	}
	Ext.Ajax.request({
		url:"bayface!getAlarmColorSet.action",
		method: 'POST',
		scope:this,
		success:function(resp){
			var result = Ext.decode(resp.responseText);
			if (result&&result.returnResult==0){
				Ext.Msg.alert("提示", result.returnMessage);
			}else{
				//组装告警颜色数据模型 
				arrColor[0] = result.PS_CRITICAL_IMAGE;
				arrColor[1] = result.PS_MAJOR_IMAGE;
				arrColor[2] = result.PS_MINOR_IMAGE;
				arrColor[3] = result.PS_WARNING_IMAGE;
				arrColor[4] = result.PS_CLEARED_IMAGE; 
			}
        	Ext.getCmp("flex").setAlarmStyle(arrColor);
        	getBayfaceData(neId,specialShelfNo);
		},
		failure : function(response) {
			Ext.Msg.alert("提示", "获取用户告警颜色配置失败,使用默认配置!");
			Ext.getCmp("flex").setAlarmStyle(arrColor); 
			getBayfaceData(neId,specialShelfNo);
		},
		error : function(response) {
			Ext.Msg.alert("提示", "获取用户告警颜色配置失败,使用默认配置!");
			Ext.getCmp("flex").setAlarmStyle(arrColor); 
			getBayfaceData(neId,specialShelfNo);
		} 
	});
});  

Ext.getCmp("flex").on("MSGTOOL", function (e) { 
		Ext.msg.alert("提示","未识别单元盘的属性菜单无效！"); 
});  
 
Ext.getCmp("flex").on("UNIT", function (e) {  
	var jsonData = {
			"map.neId":e.more.param.neId,
			"map.rackNo":e.more.param.rackNo,
			"map.shelfNo":e.more.param.shelfNo,
			"map.slotNo":e.more.param.slotNo
		};
	Ext.Ajax.request({
		url:"bayface!getBayfaceUintId.action",
		method:'POST',
		params:jsonData,
		success:function (response){
			top.Ext.getBody().unmask;
			var obj = Ext.decode(response.responseText);
			if(obj == null || obj=="" || (obj&& 0==obj.returnResult)){
	    		Ext.Msg.alert("提示","板卡信息获取失败！"); 
	    		return;
	    	} 
			if(e.id == "当前告警"){
				url = '../faultManager/currentAlarm.jsp?view_unitId='+obj.BASE_UNIT_ID;
				parent.addTabPage(url,"当前告警",authSequence);
			}else{
				var nodeInfo = "\"6-" + obj.BASE_UNIT_ID + "\"";
				if(e.id == "当前性能"){ // Domain: 1-sdh;2-wdm;3-eth
					if(obj.DOMAIN == 1 || obj.DOMAIN==3){ 
						url = "../performanceManager/PMsearch/SDHCurrent.jsp?nodeInfo="+nodeInfo;
						parent.addTabPage(url,"SDH/ETH当前性能查询",authSequence);
					}else{ 
						url = "../performanceManager/PMsearch/WDMCurrent.jsp?nodeInfo="+nodeInfo;
						parent.addTabPage(url,"WDM当前性能查询",authSequence); 
					} 
					
				}else if(e.id == "历史性能"){
					if(obj.DOMAIN == 1 || obj.DOMAIN==3){
						url = "../performanceManager/PMsearch/SDHHistory.jsp?nodeInfo="+nodeInfo;
						parent.addTabPage(url,"SDH/ETH历史性能查询",authSequence);
					}else{
						url = "../performanceManager/PMsearch/WDMHistory.jsp?nodeInfo="+nodeInfo;
						parent.addTabPage(url,"WDM历史性能查询",authSequence); 
					} 
				}
			}	
		}
	});
});


Ext.getCmp("flex").on("PORT", function (e) {   
//	数据库取端口的设备类型SDH、WDM
	var jsonData = {
		"map.neId":e.more.param.neId,
		"map.rackNo":e.more.param.rackNo,
		"map.shelfNo":e.more.param.shelfNo,
		"map.slotNo":e.more.param.slotNo,
		"map.portNo":e.more.param.portNo
	};
	Ext.Ajax.request({
		url:"bayface!getPortDomain.action",
		type:"post",
		params:jsonData,
		success:function (response){
			top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText); 
	    	if(obj == null || obj=="" || (obj&& 0==obj.returnResult)){
	    		Ext.Msg.alert("提示","端口信息获取失败！"); 
	    		return;
	    	} 
	    	if (e.id =="通道路由"){
				//四个参数flag标志是面板图调用右键菜单,端口ID，端口的树等级8，端口类型
				url = "../circuitManager/selectCircuitPortReasult.jsp?flag="
					+'6'+"&nodes="+obj.BASE_PTP_ID+"&nodeLevel="+'8'+"&serviceType="+obj.DOMAIN;
				parent.addTabPage(url,"通道路由",authSequence);
			} 	
		},
	    error:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	});  
});   


Ext.getCmp("flex").on("BACKGROUND", function (e) { 
	Ext.Ajax.request({
		url:"bayface!getNeRelate.action",
		type:"POST",
		params:{"neId":e.more.param.neId},
		success:function (response){
			top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText); 
	    	if(obj == null || obj=="" || (obj&& 0==obj.returnResult)){
	    		Ext.Msg.alert("提示","网元信息获取失败！"); 
	    		return;
	    	}
	    	var neInfo = "\"4-" + obj.BASE_NE_ID + "\"";//4是网元的在节点中的级别
	    	if(e.id == "当前告警"){
	    		url = "../faultManager/currentAlarm.jsp?view_neId="+obj.BASE_NE_ID
	    			 +"&view_emsId="+obj.BASE_EMS_CONNECTION_ID 
	    			 +"&view_emsGroupId="+obj.BASE_EMS_GROUP_ID
	    			 +"&view_neInfo="+neInfo; 
	    		parent.addTabPage(url,"当前告警",authSequence);
	    	}else if (e.id =="历史告警"){ 
	    		url="../faultManager/historyAlarm.jsp?view_neId="+obj.BASE_NE_ID
	    			+"&view_emsId="+obj.BASE_EMS_CONNECTION_ID 
	    			+"&view_emsGroupId="+obj.BASE_EMS_GROUP_ID
	    			+"&view_neInfo="+neInfo;
	    		parent.addTabPage(url,"历史告警",authSequence);
	    	} 	
		},
	    error:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 
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
