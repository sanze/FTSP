
var formPanel = new Ext.FormPanel({
	region:"north",
    frame:false,
    id:"formPanel",
    border:false,
	bodyStyle : 'padding:10px 10px 0 10px',
	height : 70,
    labelAlign: 'right',
    collapsed: false,
    collapseMode: 'mini',
    split:true,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [{
				xtype : 'combo',
				id : 'areaCombo',
				name : 'areaCombo',
				store : areaStore,
				fieldLabel : '区域',
//				allowBlank : true,
				width : 150,
				valueField: 'RESOURCE_AREA_ID',
			    displayField: 'AREA_NAME',
			    triggerAction: 'all',
				mode: 'remote',
			    listeners: {
			    	'select' : function(combox, record, index){
			    		Ext.getCmp('stationCombo').reset();
			    		Ext.getCmp('eqptNameCombo').reset();
			    	}
			    }
			}]
		}, {
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [{
				xtype : 'combo',
				id : 'eqptNameCombo',
				name : 'eqptNameCombo',
				fieldLabel : '设备名称',
				store : equipStore,
				valueField: 'RC_ID',
			    displayField: 'NAME',
			    triggerAction: 'all',
			    mode: 'remote',
			    listeners: {
			        'beforequery' : function(qe){
			            delete qe.combo.lastQuery;
			        }
			    },
				allowBlank : true,
				width : 150
			}]
		}, {
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [{
				xtype : 'combo',
				id : 'severityCombo',
				name : 'severityCombo',
				store : severityStore,
				mode : 'local',
				triggerAction : 'all',
				displayField : 'name',
			    valueField : 'value',
				fieldLabel : '告警级别',
				allowBlank : true,
				width : 150
			}]
		}, { 
			layout : 'form',
			labelSeparator : "", 
			border : false,
			items : [{
	            layout : 'column',
	            border : false,
	            forceFit : false,
				width : 150, 
	            items : [ {
	                xtype : 'label',
	                columnWidth : .10,
	                text : '　　'
	            }, {
	                xtype : 'button',
	                text : '查询',
	                columnWidth : .4,
	                handler : queryCurrentAlarm
	            }, {
	                xtype : 'label',
	                columnWidth : .10,
	                text : '　　'
	            }, {
	                xtype : 'button',
	                text : '重置',
	                columnWidth : .4,
	                width : 60,
	                handler : function() {
	                	Ext.getCmp('areaCombo').reset();
	                	Ext.getCmp('stationCombo').reset();
			    		Ext.getCmp('eqptNameCombo').reset();
			    		Ext.getCmp('equipTypeCombo').reset();
			    		Ext.getCmp('severityCombo').reset();
			    		Ext.getCmp('confirmed').setValue(true);
			    		Ext.getCmp('unconfirm').setValue(true);
	                }
	            }]
			}]
		 }]
		},{
			layout : 'column',
			border : false,
			items : [{
				layout : 'form',
				labelSeparator : "：",
				border : false,
				items : [{
					xtype : 'combo',
					store : equipTypeStore,
					id : 'stationCombo',
					name : 'stationCombo',
					fieldLabel : '局站',
					width : 150,
					store : stationStore,
					valueField: 'RESOURCE_STATION_ID',
				    displayField: 'STATION_NAME',
				    triggerAction: 'all',
				    mode: 'remote',
				    listeners: {
				        'beforequery' : function(qe){
				            delete qe.combo.lastQuery;
				        },
				    	'select' : function(combox, record, index){
				    		Ext.getCmp('eqptNameCombo').reset();
				    	}
					    
				    }
				}]
			}, {
				layout : 'form',
				labelSeparator : "：",
				border : false,
				items : [{
					xtype : 'combo',
					store : equipTypeStore,
					id : 'equipTypeCombo',
					name : 'equipTypeCombo',
					mode : 'local',
					triggerAction : 'all',
//					resizable: true,
					displayField : 'name',
				    valueField : 'value',
					fieldLabel : '设备类型',
					allowBlank : true,
					editable : true,
					width : 150
				}]
			}, {
				layout : 'form',
				labelSeparator : "：",
				border : false,
				items : [{
					xtype : 'checkboxgroup',
					id : 'statusCheckbox',
					name : 'statusCheckbox',
					fieldLabel : '确认状态',
					items : [
					    {boxLabel: '已确认', id: 'confirmed', name: 'status', checked : true},
					    {boxLabel: '未确认', id : 'unconfirm', name: 'status', checked : true}
					],
					width : 150
				}]
			}]
		}]
});

//获取查询条件
function getParaJson() {
	
	var param = {};
	var areaId = Ext.getCmp("areaCombo").getValue();
	if(areaId != '' && areaId != undefined) {
		param.areaId = areaId;
	}
	var stationId = Ext.getCmp("stationCombo").getValue();
	if(stationId != '' && stationId != undefined){
		param.stationId = stationId;
	}
	var eqptId = Ext.getCmp("eqptNameCombo").getValue();
	if(eqptId != '' && eqptId != undefined){
		param.eqptId = eqptId;
	}
	var eqptType = Ext.getCmp("equipTypeCombo").getValue();
	if(eqptType != '' && eqptType != undefined){
		param.eqptType = eqptType;
	}
	var severity = Ext.getCmp("severityCombo").getValue();
	if(severity != '' && severity != undefined){
		param.severity = severity;
	}
	
	var ackStatus = '';
	var confirmed = Ext.getCmp("confirmed").getValue();
	var unconfirm = Ext.getCmp("unconfirm").getValue();
	if(confirmed) ackStatus += '1';
	if(unconfirm) ackStatus += '2';
	if(ackStatus != '' && ackStatus != '12'){
		param.ackStatus = ackStatus;
	}
	
	return {
		'jsonString' : Ext.encode(param),
		'limit' : pageSizeCount
	};
}

//查询当前告警
function queryCurrentAlarm() {
	currentAlarmStore.load({
		params : getParaJson()
	});
	currentAlarmStore.baseParams = getParaJson();
}

//告警确认
function confirmAlarm() {
	
	var cells = gridPanel.getSelectionModel().getSelections();
	if(cells.length == 0) {
		Ext.Msg.alert("提示：", "请选择告警！");
	}else{
		var currentAlarmIds = "";
		for(var i=0;i<cells.length;i++) {
			currentAlarmIds += (cells[i].get("alarmId") + ',');
		}
		
		Ext.Msg.confirm("提示：","是否确认所选告警？",function(r){
			if(r == "yes"){
				Ext.Ajax.request({
		    		url: 'alarm!confirmAlarm.action',
		    		method: "post",
		    		params: {
		    			'jsonString':Ext.encode({"currentAlarmIds" : currentAlarmIds})
		    		},
		    		success: function (response, options) {
		    			var obj = Ext.decode(response.responseText);
		    			if(obj.returnResult == 1) {
		    				Ext.Msg.alert("提示", "告警确认成功！", function(r) {
		    					queryCurrentAlarm();
							});
		    			}else{
		    				Ext.Msg.alert("提示：", "告警确认失败！");
		    			}
		    		},
		    		failure: function () {
		    			Ext.Msg.alert("提示：", "告警确认出错！");
		    		},
		    		error: function() {
		    			Ext.Msg.alert("提示：", "告警确认出错！");
		    		}
				});
			}
		});
	}
}

var gridPanel = new Ext.grid.GridPanel({
	id : 'gridPanel',
	region : 'center',
	cm : cm,
    store : currentAlarmStore,
    selModel : selModel,
    loadMask : '数据加载中...',
    tbar : ['-', {
		text : '告警确认',
		handler : confirmAlarm
	}, '-', {
		text : '告警同步',
		handler : function() {
			alarmSyncWindow.show();
		}
	}
//	,{
//		text : '导出',
//		icon : '../../resource/images/buttons/xls.jpg'
//	}
	],
	bbar : pageTool
});

Ext.onReady(function() {
//	Ext.Msg = top.Ext.Msg;
//	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
//	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
//	Ext.Ajax.timeout=900000;
	var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [formPanel, gridPanel],
        renderTo : Ext.getBody()
    });
	
	if(rcId != 'null') {
		currentAlarmStore.load({
			params : {
				'jsonString' : Ext.encode({'eqptId' : rcId}),
				'limit' : pageSizeCount
			}
		});
	} else if (stationId != 'null') {
		currentAlarmStore.load({
			params : {
				'jsonString' : Ext.encode({'stationId' : stationId}),
				'limit' : pageSizeCount
			}
		});
	} else {
		queryCurrentAlarm();
	}
});




