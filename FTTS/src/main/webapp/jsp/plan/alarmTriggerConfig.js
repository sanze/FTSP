/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :false});


 var alarmStore = new Ext.data.Store({
	 url : 'plan!getTriggerAlarm.action',
	 reader : new Ext.data.JsonReader({
		 totalProperty : 'total',
		 root : "rows"
	 }, ["ALARM_NAME"])
 });

var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns:[ new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel,{
			id:'ALARM_NAME',
			header:'告警名称',
			dataIndex:'ALARM_NAME',
			width:180
		}
	]}
);

var gridPanel = new Ext.grid.GridPanel({
	stripeRows : true,
	autoScroll : true,
	frame : false,
	store : alarmStore,
	loadMask : false,
	border:true,
	height: 500,
	cm : columnModel,
	selModel:checkboxSelectionModel,
	tbar : ['-',{
		text : '新增',
		icon : '../../resource/images/btnImages/add.png',
		handler : function() {
			var modefyType = 0;
			Ext.getCmp('modefyType').setValue(modefyType);
			alarmNameWin.setTitle("新增告警名称");
			alarmNameWin.show();
			}
	},'-',{
		text : '删除',
		icon : '../../resource/images/btnImages/delete.png',
		handler : function() {
			var vRecords = gridPanel.getSelectionModel().getSelections();
			var vCount = vRecords.length; // 得到记录长度
			if (vCount <= 0){
				Ext.Msg.alert("提示","未选中任何记录！");
			}else{
				Ext.Msg.confirm("警告","即将删除触发测试告警名称，是否继续？",
				    function(btn){
				        if(btn=='yes'){
				        	var ALARM_NAME = '';
				        	
				        	var total = alarmStore.getCount();
				        	var cells = gridPanel.getSelectionModel().getSelections();
				        	
				        	for(var i=0;i<total;i++){
				        		var isDel = false;
				        		var currAlarm = alarmStore.getAt(i).data.ALARM_NAME;
				        		for(var j=0;j<cells.length;j++){
				        			var modifyAlarm = cells[j].get("ALARM_NAME");
						        	  if(currAlarm == modifyAlarm){
						        		  isDel = true;
						        		  break;
						        	  }
				        		}
				        		if(isDel == false && currAlarm != ''){
				        			  ALARM_NAME += currAlarm;
				        			  ALARM_NAME += ",";
				        		  }
				        	}
				        	ALARM_NAME = ALARM_NAME.substr(0,ALARM_NAME.length-1);
				        	
				        	var parameters =
				        	{
				        		"jsonString":Ext.encode({
				        			'PARAM_VALUE':ALARM_NAME
				        		})
				        	}
				        	Ext.Ajax.timeout=120000;
				        	top.Ext.getBody().mask('正在执行，请稍候...');
				        	Ext.Ajax.request({
				        	    url: 'plan!modifyTriggerAlarm.action',
				        	    params: parameters,
				        	    success: function(response){
				        	    	top.Ext.getBody().unmask();
				        			var obj = Ext.decode(response.responseText);    	
				        			
				        			alarmStore.reload();
				        			Ext.getCmp("modefyType").setValue("");
				        			Ext.getCmp("alarmName").setValue("");
				        	    }
				        	});
				        }
				    })
				}
			}
	},'-',{
		text : '修改',
		icon : '../../resource/images/btnImages/modify.png',
		handler : function() {
			var cells = gridPanel.getSelectionModel().getSelections();
			if(cells.length == 1){
				var modefyType = 1;
				var alarmName = cells[0].get("ALARM_NAME");
				Ext.getCmp('modefyType').setValue(modefyType);
				Ext.getCmp('alarmName').setValue(alarmName);
				alarmNameWin.setTitle("修改告警名称");
				alarmNameWin.show();
			}else{
				Ext.Msg.show({
				   title:'错误',
				   msg: '每次仅可修改一个告警！',
				   buttons: Ext.Msg.CANCEL,
				   icon: Ext.MessageBox.ERROR
				});
			}
		}
	}]
});

fieldset = {
        xtype: 'fieldset',
        width: 400,
        title: '触发测试告警名称',
        items: [gridPanel]
    };
formPanel = new Ext.FormPanel({
	id:'formPanel',
	region:"center",
    border:false,
    frame:false,
	autoScroll:true,
    bodyStyle: 'padding:10px 12px 0;',
    items: [{
    	 xtype: 'fieldset',
    	 title: '配置',
    	 autoHeight: true,
    	 width: 400,
    	 defaultType: 'checkbox',
    	 hideLabels: true,
    	 layout: 'hbox',
    	 defaults: {
    	    flex: 1
    	},
    	items: [{
    		boxLabel: '<span style="font-size:12px">启用触发测试</span>', 
    		id: 'golConf',
    	    name: 'golConf', 
    	    listeners : {
    	    	'check' : function(r, checked){
    	    		alarmTriggerConfig(checked);
    	    	}
    	    }
    	},{
    		xtype : 'button',
    		text : '更新告警路由映射',
    		width: 100,
    		handler : function(){
    			Ext.Ajax.request({
    			    url: 'plan!InitAlarm2Route.action',
    			    success: function(response){
    			    	Ext.Msg.show({
    			 		   title:'提示',
    			 		   msg: '更新成功！',
    			 		   buttons: Ext.Msg.OK,
    			 		   icon: Ext.MessageBox.INFO
    			 		});
    			    },
    			    error:function(response){
    			    	Ext.Msg.show({
     			 		   title:'提示',
     			 		   msg: '更新失败！',
     			 		   buttons: Ext.Msg.OK,
     			 		   icon: Ext.MessageBox.INFO
     			 		});
     			    }
    			});
    		}
    	}]
        
    },fieldset
    ]
});
var alarmNameWin = new Ext.Window({
	id:'alarmNameWin',
	width: 300,
	height: 150,
	y:150,
	closable: true,
	closeAction: 'hide',
	border: false,
	modal: true,
	buttonAlign:'center',
	items: [{
		xtype:"panel",
		frame : true,
		border : false,
		bodyStyle : 'padding:20px 10px 20px',
		labelAlign : 'right',
		items : [{
			xtype: 'textfield',
	        id:'modefyType',
	        name: 'modefyType',
	        hidden: true
		},{
			xtype:"label",
			text:"告警名称：",
			width:100
		},{
			xtype: 'textfield',
	        id:'alarmName',
	        name: 'alarmName',
	        allowBlank:false,
	        width:200
		}]
	}],
	buttons: [{
		xtype : 'button',
		text : '确定',
//		icon : '../../resource/images/btnImages/add.png',
		handler : function() {
			createOrUpdateTriggerAlarm();
		}
	}, {
		xtype : 'button',
		text : '取消',
//		icon : '../../resource/images/btnImages/arrow_undo.png',
		handler : function() {
			alarmNameWin.hide();
		}
	}]
});

function initPage(){
	Ext.Ajax.request({
	    url: 'plan!getTriggerAlarmStatus.action',
	    success: function(response){
			var obj = Ext.decode(response.responseText);    	
			var triggerSwitch = obj.PARAM_VALUE;
			alarmStore.load();
			if(triggerSwitch == 1){
				Ext.getCmp("golConf").setValue(1);
			}else{
				gridPanel.disable(true);
			}
			
	    }
	});
}

function alarmTriggerConfig(checked){
	var value = 0;
	if(checked){
		value = 1;
	}
	var parameters =
	{
		"jsonString":Ext.encode({
			'PARAM_VALUE':value
		})
	}
	Ext.Ajax.request({
	    url: 'plan!modifyAlarmTriggerStstus.action',
	    params: parameters,
	    success: function(response){
	    	if(checked){
    			gridPanel.enable(true);
    		}else{
    			gridPanel.disable(true);
    		}
	    }
	});
}
function createOrUpdateTriggerAlarm(){
	
	var modifyType = Ext.getCmp('modefyType').getValue();
	var alarmName = Ext.getCmp("alarmName").getValue();
	var ALARM_NAME = '';
	var modifyAlarm = '';
	
	if(modifyType == 1){
		var cells = gridPanel.getSelectionModel().getSelections();
		var modifyAlarm = cells[0].get("ALARM_NAME");
	}
	
	var total = alarmStore.getCount();
	for(var i=0;i<total;i++){
	  var currAlarm = alarmStore.getAt(i).data.ALARM_NAME;
	  if(modifyType == 1 && currAlarm == modifyAlarm){
		  
	  }else{
		  if(currAlarm != ''){
			  ALARM_NAME += currAlarm;
			  ALARM_NAME += ",";
		  }
	  }
	}
	ALARM_NAME += alarmName;
	ALARM_NAME += ",";
	
	ALARM_NAME = ALARM_NAME.substr(0,ALARM_NAME.length-1);
	
	var parameters =
	{
		"jsonString":Ext.encode({
			'PARAM_VALUE':ALARM_NAME
		})
	}
	Ext.Ajax.timeout=120000;
	//top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
	    url: 'plan!modifyTriggerAlarm.action',
	    params: parameters,
	    success: function(response){
	    	//top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);    	
			
			alarmStore.reload();
			alarmNameWin.hide();
			Ext.getCmp("modefyType").setValue("");
			Ext.getCmp("alarmName").setValue("");
	    }
	});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.Ajax.timeout = 900000;
	// Ext.Msg = top.Ext.Msg;
//	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
	
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [formPanel]
	});
	// 刷新当前页
	initPage();
});
