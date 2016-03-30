/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

//var statusData=[
//	['1','启动'],
//	['2','停止']
//];
//
//var statusStore=new Ext.data.ArrayStore({
//	fields:[
//		{name:'value'},
//		{name:'serviceStatus'}
//	 ]
//});
//statusStore.loadData(statusData);
//
////连接模式
//var status = new Ext.form.ComboBox({
//	id:'status',
//	name: 'status',
//	fieldLabel: '连接状态',
//    typeAhead: true,
//    triggerAction: 'all',
//    lazyRender:true,
//    mode: 'local',
//    store: statusStore,
//    valueField: 'value',
//    displayField: 'serviceStatus',
//    allowBlank:true,
//	editable:false,
//	anchor: '95%'
//});

//var serviceIp = new Ext.FormPanel({
//	id:"serviceIp",
////	title:'区域',
//	region:"north",
//    frame:false,
//    border:false,
////    bodyStyle:'padding:10px 10px 0 10px',
////	height: 88,
////    labelWidth: 80,
////    labelAlign: 'right',
//    collapsed: false,   // initially collapse the group
//    collapseMode: 'mini',
//    split:true,
//    items: [{
//        layout:'column',
//        border:false,
//        items:[{
//            layout: 'form',
//            labelSeparator:"：",
//            border:false,
//            items: [{
//            	xtype:'numberfield',
//            	id:'ip1',
//				name: 'ip1',
//				fieldLabel: '网管IP地址',
//				triggerAction: 'all',
//				valueField: 'ip1Value',
//				displayField: 'ip1Value',
//				allowBlank:true,
//			    allowDecimals:false,
//			    allowNegative : false,
//			    minLength:1,
//			    maxLength:3,
//			    minValue : 0,
//			    maxValue:255,
//				editable:false,
//				width:30,
//				listeners:{
//					select:function(combo,record,index){
//					
//					}
//				}
//            }]
//        },{
//			xtype: 'label',
//			text: '。',
//			width: 3
//		},{
//            layout: 'form',
//            labelSeparator:"",
//            labelWidth: 10,
//            border:false,
//            items: [{
//            	xtype:'numberfield',
//            	id:'ip2',
//				name: 'ip2',
////				fieldLabel: '网管IP地址：',
//				triggerAction: 'all',
//				valueField: 'ip2Value',
//				displayField: 'ip2Value',
//				allowBlank:true,
//			    allowDecimals:false,
//			    allowNegative : false,
//			    minLength:1,
//			    maxLength:3,
//			    minValue : 0,
//			    maxValue:255,
//				editable:false,
//				width:30,
//				listeners:{
//					select:function(combo,record,index){
//					
//					}
//				}
//            }]
//        },{
//			xtype: 'label',
//			text: '。',
//			width: 3
//		},{
//            layout: 'form',
//            labelSeparator:"",
//            labelWidth: 10,
//            border:false,
//            items: [{
//            	xtype:'numberfield',
//            	id:'ip3',
//				name: 'ip3',
////				fieldLabel: '网管IP地址：',
//				triggerAction: 'all',
//				valueField: 'ip3Value',
//				displayField: 'ip3Value',
//				allowBlank:true,
//			    allowDecimals:false,
//			    allowNegative : false,
//			    minLength:1,
//			    maxLength:3,
//			    minValue : 0,
//			    maxValue:255,
//				editable:false,
//				width:30,
//				listeners:{
//					select:function(combo,record,index){
//					
//					}
//				}
//            }]
//        },{
//			xtype: 'label',
//			text: '。',
//			width: 3
//		},{
//            layout: 'form',
//            labelSeparator:"",
//            labelWidth: 10,
//            border:false,
//            items: [{
//            	xtype:'numberfield',
//            	id:'ip4',
//				name: 'ip4',
////				fieldLabel: '网管IP地址：',
//				triggerAction: 'all',
//				valueField: 'ip4Value',
//				displayField: 'ip4Value',
//				allowBlank:true,
//			    allowDecimals:false,
//			    allowNegative : false,
//			    minLength:1,
//			    maxLength:3,
//			    minValue : 0,
//			    maxValue:255,
//				editable:false,
//				width:30,
//				listeners:{
//					select:function(combo,record,index){
//					
//					}
//				}
//            }]
//        }]
//    }]
//});

var ipAddress=new Ext.form.TextField({
    xtype: 'textfield',
    id:'ipAddress',
    name: 'ipAddress',
    fieldLabel: 'IP地址',
    emptyText:'请输入连接IP地址........',
    sideText : '<font color=red>*</font>',
	regex : /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
    allowBlank:false,
    anchor: '95%'
});

var port = new Ext.form.TextField({
	id : 'port',
	name : 'port',
	fieldLabel : '端口号',
	emptyText : '请输入端口号........',
    sideText : '<font color=red>*</font>',
	minLength : 1,
	maxLength : 40,
	allowBlank : false,
	anchor : '95%'
});

var serviceName = new Ext.form.TextField({
	id : 'serviceName',
	name : 'serviceName',
	fieldLabel : '接入服务器名称',
	emptyText : '请输入接入服务器名称........',
    sideText : '<font color=red>*</font>',
	minLength : 1,
	maxLength : 40,
	allowBlank : false,
	anchor : '95%'
});

var serviceNote = new Ext.form.TextField({
	id : 'serviceNote',
	name : 'serviceNote',
	fieldLabel : '接入服务器备注',
	emptyText : '请输入接入服务器备注........',
	minLength : 0,
	maxLength : 128,
	allowBlank : true,
	anchor : '95%'
});

var formPanel = new Ext.FormPanel({
	id:'formPanel',
	region:"center",
	//title:'修改网元设置',
    border:false,
    frame:false,
	autoScroll:true,
    labelWidth: 100,
    width: 200,
    bodyStyle: 'padding:10px 12px 0;',
    items: [serviceName, 
            ipAddress,
            port, 
            serviceNote 
    ],
    buttons: [{
        text: '确定',
        handler: function(){
        	saveConfig();
        }
     },{
        text: '取消',
        handler: function(){
            //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('modifySysServiceWindow');
			if(win){
				win.close();
			}
        }
    }]
});

//保存配置
function saveConfig(){
	if(formPanel.getForm().isValid()){
   		var serviceName = Ext.getCmp("serviceName").getValue();
   		var serviceNote = Ext.getCmp("serviceNote").getValue();
//   		var status = Ext.getCmp("status").getValue();
   		var port = Ext.getCmp("port").getValue();
		var ip = Ext.getCmp("ipAddress").getValue();
		if(ip == "127.0.0.1"){
			Ext.Msg.alert("提示", "接入服务器 IP 不可以设置为 127.0.0.1 ！");
			return;
		}
		Ext.getBody().mask('正在执行，请稍候...');
		//修改网元信息
		var jsonData = {
			"sysServiceModel.sysSvcRecordId":sysSvcRecordId,
			"sysServiceModel.serviceName":serviceName,
			"sysServiceModel.note":serviceNote,
			"sysServiceModel.port":port,
			"sysServiceModel.ip" : ip
		};
		Ext.Ajax.request({
			url: 'connection!modifySysService.action', 
			method : 'POST',
			params: jsonData,
			success: function(response) {
				
			    var obj = Ext.decode(response.responseText);
				Ext.getBody().unmask();
	            Ext.Msg.alert("信息",obj.returnMessage, function(r){
			    	if(obj.returnResult == 1){
		            	//刷新列表
		            	var pageTool = parent.Ext.getCmp('pageTool');
		            	if(pageTool){
		    				pageTool.doLoad(pageTool.cursor);
		    			}
		    			//关闭修改任务信息窗口
				    	var win = parent.Ext.getCmp('modifySysServiceWindow');
				    	if(win){
				    		win.close();
				    	}
	                }
	    		});
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


function initData(sysSvcRecordId){
	var jsonData = {
		"sysServiceModel.sysSvcRecordId":sysSvcRecordId
	};
	Ext.Ajax.request({
	    url: 'connection!getSysServiceBySysSvcId.action', 
	    method : 'POST',
	    params: jsonData,
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	
	    	Ext.getCmp("ipAddress").setValue(obj.IP);
	    	Ext.getCmp("port").setValue(obj.PORT);
	    	Ext.getCmp("serviceName").setValue(obj.SERVICE_NAME);
	    	Ext.getCmp("serviceNote").setValue(obj.NOTE);
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
    
 Ext.onReady(function(){
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 

 	initData(sysSvcRecordId);
 	
  	var win = new Ext.Viewport({
        id:'win',
        layout : 'border',
		items : [
		  formPanel
		],
		renderTo : Ext.getBody()
	});
	
 });