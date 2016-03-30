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
         }, {
        	 header : '告警级别',
        	 dataIndex : 'ALARM_LEVEL',
        	 width : 80,
        	 renderer : function(value, metaData, record, rowIdx, colIdx, store) {
        		 if(value == 4) {
        			 metaData.style = ALARM_LEVEL_WR_STYLE;
            		 return "<span style='color:#FFFFFF'>提示</span>";
        		 }else if(value == 3) {
        			 metaData.style = ALARM_LEVEL_MN_STYLE;
            		 return "<span style='color:#000000'>一般</span>";
        		 }else if(value == 2) {
        			 metaData.style = ALARM_LEVEL_MJ_STYLE;
            		 return "<span style='color:#000000'>严重</span>";
        		 }else if(value == 1) {
        			 metaData.style = ALARM_LEVEL_CR_STYLE;
            		 return "<span style='color:#FFFFFF'>紧急</span>";
        		 }
        	 }
         },
         { header: '告警内容',sortable:false, dataIndex: 'content',width:180},
         { header: '区域', dataIndex: 'REGION_NAME'},
         { header : '局站', dataIndex : 'stationName' },
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

var currentAlarmStore = new Ext.data.Store({
	url : 'alarm!queryCurrentAlarm.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ 'alarmId','ACK_STATUS','TEST_RESULT_ID','ALARM_LEVEL','content',
         'ALARM_TYPE','REGION_NAME','stationName','EQPT_NO','EQPT_NAME','EQPT_TYPE','SLOT_NO',
         'CARD_PORT','CARD_TYPE','testLinkInfo','lightPathInfo1',
         'lightPathInfo2','breakPointInfo','ALARM_OCCUR_DATE','ACK_DATE',
         'USER_NAME' ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : pageSizeCount,// 每页显示的记录值
	store : currentAlarmStore,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

//--------------------- 查询条件相关 -------------------------

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
	fields : [ {
		name : 'value'
	}, {
		name : 'name'
	} ]
});
severityStore.loadData(severityData);

//设备类型
var equipTypeData = [[1, 'RTU'], [2, 'CTU']];
var equipTypeStore = new Ext.data.ArrayStore({
	fields : [ {name : 'value'}, 
	           {name : 'name'} 
	]
});
equipTypeStore.loadData(equipTypeData);

//--------------------------- 告警同步相关 -------------------------

//区域
var alarmSyncAreaStore = new Ext.data.Store({
	url : 'alarm!getAlarmSyncArea.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['RESOURCE_AREA_ID','AREA_NAME']
	})
});

//局站
var alarmSyncStationStore = new Ext.data.Store({
	url : 'alarm!getAlarmSyncStation.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['RESOURCE_STATION_ID', 'STATION_NAME']
	}),
	listeners : {
		'beforeload' : function(store, options) {
			var areaId = Ext.getCmp('alarmSyncAreaCombo').getValue();
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
var alarmSyncEquipStore = new Ext.data.Store({
	url : 'alarm!getAlarmSyncEquip.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['RC_ID','NAME']
	}),
	listeners : {
		'beforeload' : function(store, options) {
			var params = {};
			var areaId = Ext.getCmp('alarmSyncAreaCombo').getValue();
			if(areaId != '' && areaId != undefined) {
				params.areaId = areaId;
			}
			var stationId = Ext.getCmp('alarmSyncStationCombo').getValue();
			if(stationId != '' && stationId != undefined){
				params.stationId = stationId;
			}
			
			options.params = {
				'jsonString' : Ext.encode(params)
			};
		}
	}
});

var alarmSyncWindow = new Ext.Window({
	id : 'alarmSyncWindow',
	title : '告警同步',
	width : 400,
	height : 240,
	modal : true,
	closeAction : 'hide',
	labelWidth : 80,
	layout : 'form',
	bodyStyle : 'background-color:#ffffff;padding:20px 50px 20px 40px',
	items : [{
			xtype : 'combo',
			id : 'alarmSyncAreaCombo',
			labelAlign : 'left',
			width : 210,
			fieldLabel : '区域',
			store : alarmSyncAreaStore,
			valueField: 'RESOURCE_AREA_ID',
		    displayField: 'AREA_NAME',
		    triggerAction: 'all',
		    mode: 'remote',
		    listeners: {
		    	'select' : function(combox, record, index){
		    		Ext.getCmp('alarmSyncStationCombo').reset();
		    		Ext.getCmp('alarmSyncEquipCombo').reset();
		    	}
		    }
		},{ 
			xtype : 'tbspacer',
			height : 10 
		},{
			xtype : 'combo',
			id : 'alarmSyncStationCombo',
			labelAlign : 'left',
			width : 210,
			fieldLabel : '局站',
			store : alarmSyncStationStore,
			valueField: 'RESOURCE_STATION_ID',
		    displayField: 'STATION_NAME',
		    triggerAction: 'all',
		    mode: 'remote',
		    listeners: {
		        'beforequery' : function(qe){
		            delete qe.combo.lastQuery;
		        },
		        'select' : function(combox, record, index){
		    		Ext.getCmp('alarmSyncEquipCombo').reset();
		    	}
		    }
		},{ 
			xtype : 'tbspacer',
			height : 10 
		},{
			xtype : 'combo',
			id : 'alarmSyncEquipCombo',
			labelAlign: 'left',
			width : 210,
			fieldLabel : '设备名称',
			store : alarmSyncEquipStore,
			valueField: 'RC_ID',
		    displayField: 'NAME',
		    triggerAction: 'all',
		    mode: 'remote',
		    listeners: {
		        'beforequery' : function(qe){
		            delete qe.combo.lastQuery;
		        }
		    }
		}
	],
	buttons : [
	{
		text : '同步',
		handler : function() {
			
			alarmSyncWindow.hide();
			Ext.getBody().mask("正在同步中，请稍候...");
			
			var params = {};
			
			var areaId = Ext.getCmp('alarmSyncAreaCombo').getValue();
			var stationId = Ext.getCmp('alarmSyncStationCombo').getValue();
			var eqptId = Ext.getCmp('alarmSyncEquipCombo').getValue();
			
			if(eqptId != '' && eqptId != undefined) {
				params.eqptId = eqptId;
			}else{ 
				if(stationId != '' && stationId != undefined){
					params.stationId = stationId;
				}else{
					if(areaId != '' && areaId != undefined) {
						params.areaId = areaId;
					}
				}
			}
			
			Ext.Ajax.request({
				url : 'alarm!syncAlarm.action',
				type : 'post',
				params : {
					'jsonString' : Ext.encode(params)
				},
				success : function(response, options){
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					if(obj.returnResult == 1) {
						var details = obj.details;
						if(details.length == 0) {
							Ext.Msg.alert("提示", "告警同步成功！");
						} else {
							showDetails(obj);
						}
					} else {
						Ext.Msg.alert("提示：", "告警同步失败！");
					}
				},
				failure:function(){
					Ext.getBody().unmask();
					Ext.Msg.alert("提示：", "后台运行出错！");
				},
				error:function(){
					Ext.getBody().unmask();
					Ext.Msg.alert("提示：", "后台运行出错！");
				}
			});
		}
	},{
		text : '取消',
		handler : function() {
			alarmSyncWindow.setVisible(false);
		}
	}]
});

//------------ 展示同步失败设备详情 ----------------------------------------

var detailStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		fields : ['EQPT_NO', 'EQPT_NAME', 'MSG']
	})
});

var detailColumns = new Ext.grid.ColumnModel({
    defaults: {
        sortable: true
    },
    columns: [
		new Ext.grid.RowNumberer({width:26}),
		new Ext.grid.CheckboxSelectionModel({singleSelect:true}), 
		{ 
			header: '设备编号',
			dataIndex: 'EQPT_NO'
		},{
			header: '设备名称',
			dataIndex: 'EQPT_NAME'
		},{
			header: '失败原因',
			dataIndex: 'MSG'
		}]
});

var detailGrid = new Ext.grid.GridPanel({
	id : 'detailGrid',
	region : 'center',
	autoScroll : true,
    store : detailStore,
    columns : detailColumns
});

var detailWinow = new Ext.Window({
	id : 'detailWindow',
	title : '同步失败设备',
	width : 400,
	height : 240,
	modal : true,
	layout : 'border',
	items : [detailGrid],
	buttons : [
	{
		text : '确定',
		handler : function() {
			detailWin.hide();
		}
	}]
});

//显示同步详情
function showDetails(obj) {
	detailStore.loadData(obj.details);
	detailWinow.show();
}
