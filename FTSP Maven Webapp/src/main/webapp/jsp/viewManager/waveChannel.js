var dat;
var swfPanel = new Ext.Panel({ 
     width : 1400,
     height : 700,
     title : '面板图',  
     autoScroll:true,
     items : {
         id : "flex",
         xtype : 'flex',
         type : 'bayface'
     } 
});

Ext.getCmp("flex").on("initialize", function () {
//获取用户的颜色配置
	var arrColor = [0xff0000,0xff0000,0xff8000,0xffff00,0xeeeeee,0x00ff00]; 
	
    Ext.Ajax.request({
        url: "bayface!getBayfaceData.action",
        method : 'POST',
//        params:{neId:"397"},//9560
//        params:{neId:"409"},//6800
        params:{neId:"410"},//7500
//        params:{neId:"411"},//Metro 1000v3
//        params:{neId:"412"},//FW4560
//        params:{neId:"413"},//ADM-U 没有告警福端口
//        params:{neId:"415"},//没有风扇
//        params:{neId:"416"},//9500
//        params:{neId:"417"},//多子架8800
        scope:this,
        success:function(resp) {
            dat = Ext.decode(resp.responseText);   
        	Ext.getCmp("flex").setAlarmStyle(arrColor); 
        	Ext.getCmp("flex").loadData(dat, "unit"); 
        }
    }); 
});  

Ext.getCmp("flex").on("MSGTOOL", function () { 
		Ext.msg.alert("提示","未识别单元盘的属性菜单无效！"); 
});  
 
Ext.getCmp("flex").on("UNIT", function () { 
if(e.id == "当前告警"){
//	e.more.param
}
});  

Ext.getCmp("flex").on("PORT", function () { 
	if(e.id == "当前性能"){
//		e.more.param
	}else if(e.id == "历史性能"){
	}else if (e.id =="通道路由"){
	} 
});   


Ext.getCmp("flex").on("BACKGROUND", function () { 
	if(e.id == "当前告警"){
//		e.more.param
	}else if (e.id =="历史告警"){
	} 
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
