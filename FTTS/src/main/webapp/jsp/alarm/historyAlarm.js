//分页数据大小
var pageSizeCount = 200;
//告警背景色
var ALARM_LEVEL_CR_STYLE = 'background-color:#FF0000';
var ALARM_LEVEL_MJ_STYLE = 'background-color:#FF8000';
var ALARM_LEVEL_MN_STYLE = 'background-color:#FFFF00';
var ALARM_LEVEL_WR_STYLE = 'background-color:#0000FF';

var selModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect:true
});

var cm = new Ext.grid.ColumnModel({
    defaults: {
        sortable: true
    },
    columns: [new Ext.grid.RowNumberer({
    	width:26
    	}),selModel, {
			 header: '告警ID',
			 dataIndex: 'alarmId',
			 sortable:false,
			 hidden:true
		 },{ 
        	 header: '确认状态', 
        	 dataIndex: 'ACK_STATUS',
        	 width:60,
        	 renderer:function(value, metaData, record, rowIdx, colIdx, store){
        		 if(value == 1){
            		 return "已确认";
        		 }else if(value == 2){
        			 return "未确认";
        		 }else{
        			 return "未知状态";
        		 }
        	 }
         },{ 
        	 header: '告警级别', 
        	 dataIndex: 'ALARM_LEVEL',
        	 width:80,
        	 renderer:function(value, metaData, record, rowIdx, colIdx, store){
        		 if(value == 4){
        			 metaData.style = ALARM_LEVEL_WR_STYLE;
            		 return "<span style='color:#FFFFFF'>提示</span>";
        		 }else if(value == 3 || value == 5){
        			 metaData.style = ALARM_LEVEL_MN_STYLE;
            		 return "<span style='color:#000000'>一般</span>";
        		 }else if(value == 2){
        			 metaData.style = ALARM_LEVEL_MJ_STYLE;
            		 return "<span style='color:#000000'>严重</span>";
        		 }else if(value == 1){
        			 metaData.style = ALARM_LEVEL_CR_STYLE;
            		 return "<span style='color:#FFFFFF'>紧急</span>";
        		 }
        	 }
         },
         { header: '告警内容',sortable:false, dataIndex: 'content',width:180},
//         { 
//        	 header: '告警类型', 
//        	 dataIndex: 'ALARM_TYPE',
//        	 renderer:function(value, metaData, record, rowIdx, colIdx, store){
//        		 if(value == 1){
//            		 return "设备告警";
//        		 }else if(value == 2){
//        			 return "线路告警";
//        		 }else if(value == 3){
//        			 return "网管告警";
//        		 }else{
//        			 return "未知告警";
//        		 }
//        	 }
//         },
         { header: '区域', dataIndex: 'REGION_NAME'},
         { header: '局站', dataIndex: 'stationName'},
         { header: '设备编号', dataIndex: 'EQPT_NO'},
         { header: '设备名称', dataIndex: 'EQPT_NAME'},
         { 
        	 header: '设备类型', 
        	 dataIndex: 'EQPT_TYPE',
        	 renderer:function(value, metaData, record, rowIdx, colIdx, store){
        		 if(value == 111) {
            		 return "RTU";
        		 } else {
        			 return "未知设备";
        		 }
        	 }
         },
         { 
        	 header: '槽道',
        	 dataIndex: 'SLOT_NO',
        	 width:50
         },
         { 
        	 header: '机盘型号',
        	 dataIndex: 'CARD_TYPE',
        	 renderer:function(value, metaData, record, rowIdx, colIdx, store){
        		 if(value == 1){
            		 return "PWR";
        		 }else if(value == 2){
        			 return "MCU";
        		 }else if(value == 3){
        			 return "OTDR";
        		 }else if(value == 4){
        			 return "OSW";
        		 }else if(value == 5){
        			 return "OPM";
        		 }else if(value == 6){
        			 return "OLS";
        		 }
        	 }
         },
         { 
        	 header: '设备端口',
        	 dataIndex: 'CARD_PORT',
        	 width:60,
        	 renderer:function(value, metaData, record, rowIdx, colIdx, store){
        		 if(value >= 0){
        			 return value;
        		 }
        	 }
         },
//         { header: '测试链路信息',sortable:false, dataIndex: 'testLinkInfo'},
//         { header: '光路信息1',sortable:false, dataIndex: 'lightPathInfo1'},
//         { header: '光路信息2',sortable:false, dataIndex: 'lightPathInfo2'},
         { 
        	 header: '断点信息',
        	 sortable:false, 
        	 dataIndex: 'breakPointInfo',
        	 renderer:function(value, metaData, record, rowIdx, colIdx, store){
        		 var content = value.split(",")[0];
        		 if(value != null && value != ''){
        			 return ('<a href="#" onclick = "displayBreakPointOnGisPage('+rowIdx+')">'+content+ '</a>');
        		 }
        	 }
         },
         { header: '发生时间', dataIndex: 'ALARM_OCCUR_DATE'},
         { header: '确认时间', dataIndex: 'ACK_DATE'},
         { header: '确认者', dataIndex: 'USER_NAME'}]
});

var historyAlarmStore = new Ext.data.Store({
	url : 'alarm!queryHistoryAlarm.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ 'alarmId','ACK_STATUS','TEST_RESULT_ID','ALARM_LEVEL','content',
         'ALARM_TYPE','REGION_NAME','stationName','EQPT_NO','EQPT_NAME','EQPT_TYPE','SLOT_NO',
         'CARD_PORT','CARD_TYPE','testLinkInfo','lightPathInfo1',
         'lightPathInfo2','breakPointInfo','ALARM_OCCUR_DATE','ACK_DATE',
         'USER_NAME' ])
});

//区域
var areaStore = new Ext.data.Store({
	url : 'alarm!getAlarmSyncArea.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['RESOURCE_AREA_ID','AREA_NAME']
	})
});

//局站
var stationStore = new Ext.data.Store({
	url : 'alarm!getAlarmSyncStation.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['RESOURCE_STATION_ID', 'STATION_NAME']
	}),
	listeners : {
		'beforeload' : function(store, options) {
			var areaId = Ext.getCmp('areaCombo').getValue();
			if(areaId == '' || areaId == undefined) {
				areaId = 0;
			}
			options.params = {
				'jsonString' : Ext.encode({
					'areaId' : areaId
				})
			};
		}
	}
});

//设备
var equipStore = new Ext.data.Store({
	url : 'alarm!getAlarmSyncEquip.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['RC_ID','NAME']
	}),
	listeners : {
		'beforeload' : function(store, options) {
			var params = {};
			var areaId = Ext.getCmp('areaCombo').getValue();
			if(areaId != '' && areaId != undefined) {
				params.areaId = areaId;
			}
			var stationId = Ext.getCmp('stationCombo').getValue();
			if(stationId != '' && stationId != undefined){
				params.stationId = stationId;
			}
			
			options.params = {
				'jsonString' : Ext.encode(params)
			};
		}
	}
});

//告警级别
var severityData = [[ 4, '紧急' ], [ 3, '严重' ], [ 2, '一般' ], [ 1, '提示' ]];
var severityStore = new Ext.data.ArrayStore({
	fields : [ 
		{name : 'value'}, 
		{name : 'name'} 
	]
});
severityStore.loadData(severityData);

//设备类型
var equipTypeData = [[1, 'RTU'], [2, 'CTU']];
var equipTypeStore = new Ext.data.ArrayStore({
	fields : [ 
	    {name : 'value'}, 
	    {name : 'name'}
	]
});
equipTypeStore.loadData(equipTypeData);

var formPanel = new Ext.FormPanel({
	region : 'north',
    frame : false,
    id : 'formPanel',
    border : false,
	bodyStyle : 'padding:10px 10px 0px 45px',
	height : 100,
    collapsed: false,
    collapseMode: 'mini',
    split:true,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			labelSeparator : "",
			bodyStyle : 'padding:0px 10px 0px 0px;',
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'startTimeFrom',
				name : 'startTimeFrom',
				fieldLabel : '告警发生时间：从',
//				emptyText:'请选择开始日期...',
				allowBlank : true,
				width : 150,
//				sideText : '<font color=red>*</font>',
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "startTimeFrom",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd HH:mm:ss',
							autoPickDate : true,
							maxDate : '#F{$dp.$D(\'startTimeTo\')}'
						});
						this.blur();
					}
				}
			}]
		},{
			layout : 'form',
			labelSeparator : "",
			bodyStyle : 'padding:0px 30px 0px 0px;',
			labelWidth : 10,
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'startTimeTo',
				name : 'startTimeTo',
				fieldLabel : '到',
//				emptyText:'请选择结束日期...',
				allowBlank : true,
				width : 150,
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "startTimeTo",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd HH:mm:ss',
							autoPickDate : true,
							minDate : '#F{$dp.$D(\'startTimeFrom\')}'
						});
						this.blur();
					}
				}
			}]
		},{
			layout : 'form',
			labelSeparator : "",
			bodyStyle : 'padding:0px 10px 0px 0px;',
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'endTimeFrom',
				name : 'endTimeFrom',
				fieldLabel : '告警结束时间：从',
				allowBlank : true,
//				emptyText:'请选择开始日期...',
				width : 150,
//				sideText : '<font color=red>*</font>',
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "endTimeFrom",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd HH:mm:ss',
							autoPickDate : true,
							maxDate : '#F{$dp.$D(\'endTimeTo\')}'
						});
						this.blur();
					}
				}
			}]
		},{
			layout : 'form',
			labelSeparator : "",
			border : false,
			bodyStyle : 'padding:0px 30px 0px 0px;',
			labelWidth : 10,
			items : [{
				xtype : 'textfield',
				id : 'endTimeTo',
				name : 'endTimeTo',
				fieldLabel : '到',
//				emptyText:'请选择结束日期...',
				allowBlank : false,
				width : 150,
//				sideText : '<font color=red>*</font>',
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "endTimeTo",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd HH:mm:ss',
							autoPickDate : true,
							minDate : '#F{$dp.$D(\'endTimeFrom\')}'
						});
						this.blur();
					}
				}
			}]
		}]
		}, {
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			labelSeparator : "：",
			labelWidth : 40,
			border : false,
			bodyStyle : 'padding:0px 30px 0px 0px;',
			items : [{
				xtype : 'combo',
				id : 'areaCombo',
				name : 'areaCombo',
				store : areaStore,
				fieldLabel : '区域',
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
			bodyStyle : 'padding:0px 30px 0px 0px;',
			labelWidth : 70,
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
				width : 150
			}]
		}, {
			layout : 'form',
			labelSeparator : "：",
			labelWidth : 70,
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
	                handler : queryHistoryAlarm
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
	                	Ext.getCmp('startTimeFrom').reset();
	                	Ext.getCmp('startTimeTo').reset();
	                	Ext.getCmp('endTimeFrom').reset();
	                	Ext.getCmp('endTimeTo').reset();
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
				labelWidth : 40,
				bodyStyle : 'padding:0px 30px 0px 0px;',
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
				bodyStyle : 'padding:0px 30px 0px 0px;',
				labelWidth : 70,
				items : [{
					xtype : 'combo',
					store : equipTypeStore,
					id : 'equipTypeCombo',
					name : 'equipTypeCombo',
					mode : 'local',
					triggerAction : 'all',
					displayField : 'name',
				    valueField : 'value',
					fieldLabel : '设备类型',
					width : 150
				}]
			}, {
				layout : 'form',
				labelSeparator : "：",
				border : false,
				labelWidth : 70,
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
	
	//告警发生时间 起
	var startTimeFrom = Ext.getCmp("startTimeFrom").getValue();
	if(startTimeFrom != '') {
		param.startTimeFrom = startTimeFrom;
	}
	//告警发生时间 止
	var startTimeTo = Ext.getCmp("startTimeTo").getValue();
	if(startTimeTo != '') {
		param.startTimeTo = startTimeTo;
	}
	//告警结束时间 起
	var endTimeFrom = Ext.getCmp("endTimeFrom").getValue();
	if(endTimeFrom != '') {
		param.endTimeFrom = endTimeFrom;
	}
	//告警结束时间 止
	var endTimeTo = Ext.getCmp("endTimeTo").getValue();
	if(endTimeTo != '') {
		param.endTimeTo = endTimeTo;
	}
	
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

//查询历史告警
function queryHistoryAlarm() {
	historyAlarmStore.load({
		params : getParaJson()
	});
	historyAlarmStore.baseParams = getParaJson();
}

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : pageSizeCount,// 每页显示的记录值
	store : historyAlarmStore,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.GridPanel({
	id : 'gridPanel',
	title : '　',
	region : 'center',
	loadMask : '数据加载中...',
	cm : cm,
    store : historyAlarmStore,
    selModel : selModel,
    tbar : [
//            {
//		text : '导出',
//		icon : '../../resource/images/buttons/xls.jpg'
//	}
    ],
    bbar : pageTool
});

Ext.onReady(function() {
	var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [formPanel, gridPanel],
        renderTo : Ext.getBody()
    });
	
	queryHistoryAlarm();
});

