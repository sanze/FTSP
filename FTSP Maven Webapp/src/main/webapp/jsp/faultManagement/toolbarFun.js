info_Equip={faultId : 0};
info_Line={faultId :0};

//设置默认开始时间
function setDefaultStartTime() {
	
	var sevenDayBefore = new Date();
	sevenDayBefore.setDate(sevenDayBefore.getDate() - 7);
	
	Ext.getCmp("startTime").setValue(sevenDayBefore.format("yyyy-MM-dd 00:00:00"));
	Ext.getCmp("endTime").setValue((new Date()).format("yyyy-MM-dd hh:mm:ss"));
}

//查询
var queryFaultInfo = function() {
	
	faultManagementStore.baseParams = {
		"paramMap.startTime" : Ext.getCmp('startTime').getValue(),
		"paramMap.endTime" : Ext.getCmp('endTime').getValue(),
		"paramMap.status" : Ext.getCmp('statusCombo').getValue(),
		"paramMap.faultGenerate" : Ext.getCmp('faultGenerateCombo').getValue(),
		"limit" : 200
	};
	
	faultManagementStore.load({
		callback : function(r, o, s){
			if(!s){
				Ext.Msg.alert("提示：","查询故障信息失败！");
			}
		}
	});
};

//var initEquipWindow = function(info){
//	alarmConvergenceStore_Equip.baseParams.faultId = info.faultId;
//	alarmConvergenceStore_Equip.load();
//	Ext.getCmp('faultReasonCombo').setValue(info.reasonName1);
//	Ext.getCmp('faultSubReasonCombo').setValue(info.reasonName2);
//	if(info.reason1){
//		faultSubReasonStore.baseParams.parentFaultReasonId = info.reason1;
//		Ext.getCmp('faultSubReasonCombo').enable();
//	}
//	if(info.sysName){
//		Ext.getCmp('transformSystemCombo_Equip').setValue(info.sysName);
//		Ext.getCmp('stateCombo_Equip').setValue(info.stationName);
//		stateStore_Line.baseParams.sysId = info.sysId;
//		Ext.getCmp('stateCombo_Equip').enable();
//	}
//	Ext.getCmp('ems_win').setValue(info.ems);
//	Ext.getCmp('ne_win').setValue(info.neName);
//	Ext.getCmp('transforSys_win').setValue(info.sysName);
//	Ext.getCmp('station_Equip').setValue(info.stationName);
//	Ext.getCmp('slot_win').setValue(info.unitName);
//	Ext.getCmp('startTime_win').setValue(info.startTime);
//	Ext.getCmp('endTime_win').setValue(info.endTime);
//	Ext.getCmp('confirmTime_win').setValue(info.confirmTime);
//	Ext.getCmp('area_Equip').setValue(info.memo);
//	Ext.getCmp('rightCombo').setValue(info.accuracy==0?'':info.accuracy);
//	Ext.getCmp('rightCombo').render();
//	if(info.isBroken){
//		Ext.getCmp('systemStop').setValue(true);
//	}else{
//		Ext.getCmp('systemStop').setValue(false);
//	}
//	
//};
//var initLineWindow = function(info){
//	alarmConvergenceStore_Line.baseParams.faultId = info.faultId;
//	alarmConvergenceStore_Line.load();
//	Ext.getCmp('faultType_auto').onSetValue('line',true);
//	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
//	var r = cell[0].get('reason_name').split('-');
//	Ext.getCmp('faultReasonCombo_line').setValue(info.reasonName1);
//	Ext.getCmp('faultSubReasonCombo_line').setValue(info.reasonName2);
//	faultSubReasonStore_line.baseParams.parentFaultReasonId = info.reason1;
//	Ext.getCmp('transformSystemCombo_Line').setValue(info.sysName);
//	Ext.getCmp('faultSubReasonCombo_line').enable();
//	Ext.getCmp('startTime_win_line').setValue(info.startTime);
//	Ext.getCmp('endTime_win_line').setValue(info.endTime);
//	Ext.getCmp('confirmTime_win_line').setValue(info.confirmTime);
//	Ext.getCmp('area_Line').setValue(info.memo);
//	if(info.sysName){
//		Ext.getCmp('transformSystemCombo_Line').setValue(info.sysName);
//		Ext.getCmp('stateCombo_Line').setValue(info.danwei);
//		Ext.getCmp('startStateCombo_Line').setValue(info.aStation);
//		Ext.getCmp('endStateCombo_Line').setValue(info.zStation);
//		Ext.getCmp('distanceStateCombo_Line').setValue(info.nearStation);
//		stateStore_Line.baseParams.sysId = info.sysId;
//		Ext.getCmp('stateCombo_Line').enable();
//		Ext.getCmp('startStateCombo_Line').enable();
//		Ext.getCmp('endStateCombo_Line').enable();
//		Ext.getCmp('distanceStateCombo_Line').enable();
//	}
//	Ext.getCmp('jingdu_win').setValue(info.longitude);
//	Ext.getCmp('weidu_win').setValue(info.latitude);
//	Ext.getCmp('kilometer_win').setValue(info.distance);
//	Ext.getCmp('rightCombo_line').setValue(info.accuracy==0?'':info.accuracy);
//	Ext.getCmp('rightCombo_line').render();
//	if(info.isBroken){
//		Ext.getCmp('systemStop_line').setValue(true);
//	}else{
//		Ext.getCmp('systemStop_line').setValue(false);
//	}
//};


// 故障信息
//var faultInfo = function() {
//	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
//	if(cell.length == 0){
//		Ext.Msg.alert("提示","请选择");
//	}else{
//		create();
//		var r = cell[0].get('reason_name').split('-');
//		var info = {
//				"faultId" : cell[0].get('fault_id'),
//				"reason" : cell[0].get('reason_name'),
//				"reasonName1" : r[0],
//				"reasonName2" : r[1],
//				"reason1" : cell[0].get('reason1'),
//				"reason2" : cell[0].get('reason2'),
//				"neName" : cell[0].get('ne_name'),
//				"stationName" : cell[0].get('station_name'),
//				"unitId" : cell[0].get('unit_id'),
//				"unitName" : cell[0].get('unit_name'),
//				"ems" : cell[0].get('ems_name'),
//				"sysName" : cell[0].get('system_name'),
//				"startTime" :cell[0].get('start_time').time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//						cell[0].get('start_time').time)) : "",
//				"endTime" :cell[0].get('end_time').time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//						cell[0].get('end_time').time)) : "",
//				"confirmTime" :cell[0].get('CONFIRM_TIME').time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//						cell[0].get('CONFIRM_TIME').time)) : "",
//				"accuracy" : cell[0].get('ACCURACY'),
//				"latitude" : cell[0].get('LATITUDE'),
//				"longitude": cell[0].get('LONGITUDE'),
//				"isBroken" : cell[0].get('IS_BROKEN'),
//				"danwei" : cell[0].get('maintenancer'),
//				"aStation":cell[0].get('A_station'),
//				"zStation":cell[0].get('Z_station'),
//				"nearStation":cell[0].get('near_station'),
//				"distance" : cell[0].get('distance'),
//				"type" : cell[0].get('TYPE'),
//				"sysId" : cell[0].get('RESOURCE_PROJECTS_ID'),
//				"memo" : cell[0].get('memo'),
//				"source" : cell[0].get('source')
//			};
//		info_Equip = info;
//		info_Line = info;
//		if(info.source ==1){
//			Ext.getCmp('faultSource_auto').onSetValue('auto', true);
//		}else{
//			Ext.getCmp('faultSource_auto').onSetValue('manul',true);
//		}
//		if(info.type == 1){
//			initEquipWindow(info);
//		}else {
//			initLineWindow(info);
//		}
//	}
//};
// 创建
//var create = function() {
//	alarmConvergenceStore_Equip.baseParams.faultId = 0;
//	alarmConvergenceStore_Equip.baseParams.faultId = 0;
//	var northPanel = new Ext.FormPanel({
//		region : 'north',
//		height : 50,
//		layout : 'column',
//		bodyStyle : 'padding:10px 20px;',
//		border : false,
//		items : [ {
//			html : '<label>故障源：</label>',
//			bodyStyle : 'padding:5px;',
//			border : false
//		}, {
//			columnWidth : 0.3,
//			id : 'faultSource_auto',
//			xtype : 'radiogroup',
//			disabled : true,
//			columns : 2,
//			items : [ {
//				boxLabel : "自动生成",
//				id : 'auto',
//				name : 'type',
//				inputValue : 1
//			}, {
//				boxLabel : "人工录入",
//				id : "manul",
//				name : 'type',
//				inputValue : 2
//			} ]
//		} ]
//	});
//
//	var config = {
//		columnWidth : .1,
//		border : false,
//		items : [ {
//			html : '<label>故障原因：</label>',
//			bodyStyle : 'padding:5px;',
//			border : false
//		}, {
//			border : false,
//			height : 10
//		}, {
//			html : '<label>故障定位：</label>',
//			bodyStyle : 'padding:5px;',
//			border : false
//		}, {
//			border : false,
//			height : 85
//		}, {
//			html : '<label>故障时间：</label>',
//			bodyStyle : 'padding:5px;',
//			border : false
//		}, {
//			border : false,
//			height : 40
//		}, {
//			html : '<label>相关告警：</label>',
//			bodyStyle : 'padding:5px;',
//			border : false
//		}, {
//			border : false,
//			height : 80
//		}, {
//			html : '<label>故障描述：</label>',
//			bodyStyle : 'padding:5px;',
//			border : false
//		}, {
//			border : false,
//			height : 100
//		} ]
//	};
//	var windowModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
//		singleSelect : true,
//		header : ""
//	});
//	windowModel.sortLock();
//
//	var alarmCm_Equip = new Ext.ux.grid.LockingColumnModel({
//		// specify any defaults for each column
//		defaults : {
//			sortable : true
//		},
//		columns : [ new Ext.grid.RowNumberer({
//			width : 26,
//			locked : true
//		}), windowModel, {
//			id : 'alarmName_Equip',
//			header : '告警名称',
//			dataIndex : 'alarm_name',
//			width : 100
//		}, {
//			id : 'alarmLevel_Equip',
//			header : '告警等级',
//			dataIndex : 'severity_name',
//			width : 100
//		}, {
//			id : 'neName_Equip',
//			header : '网元名',
//			dataIndex : 'ne_name',
//			width : 100
//		}, {
//			id : 'alarmStartTime_Equip',
//			header : '开始时间',
//			dataIndex : 'start_time',
//			width : 100,
//			renderer : function(value) {
//				return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//						value.time)) : "";
//			}
//		}, {
//			id : 'alarmEndTime_Equip',
//			header : '结束时间',
//			dataIndex : 'clean_time',
//			width : 100,
//			renderer : function(value) {
//				return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//						value.time)) : "";
//			}
//		} ]
//	});
//	var alarmGrid_Equip = new Ext.grid.EditorGridPanel({
//		id : "alarmGrid_Equip",
//		cm : alarmCm_Equip,
//		height : 100,
//		store : alarmConvergenceStore_Equip,
//		stripeRows : true, // 交替行效果
//		loadMask : {
//			msg : '数据加载中...'
//		},
//		selModel : windowModel, // 必须加不然不能选checkbox
//		view : new Ext.ux.grid.LockingGridView(),
//		forceFit : true
//	});
//
//	var rightData = [ [ '1', '准确' ], [ '2', '部分准确' ], [ '3', '不准确' ] ];
//	var rightStore = new Ext.data.ArrayStore({
//		fields : [ {
//			name : 'value'
//		}, {
//			name : 'displayName'
//		} ]
//	});
//	rightStore.loadData(rightData);
//	var rightCombo = new Ext.form.ComboBox({
//		id : 'rightCombo',
//		fieldLabel : '判断准确性',
//		store : rightStore,
//		displayField : "displayName",
//		valueField : 'value',
//		triggerAction : 'all',
//		mode : 'local',
//		editable : false,
//		value : '',
//		width : 100
//	});
//	// 定义一个故障类型faultReasonStore从数据库获取故障类型
//	 var faultReasonStore = new Ext.data.JsonStore({
//		root : 'rows',
//		fields : [ 'REASON_ID', 'REASON_NAME','REASON_TYPE'],
//		url : 'fault-statistics!getFaultReason.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
//		listeners : {
//		    load : function(store,records){    
//		    	faultReasonStore.filterBy(function(record,id){  
//		       if(record.get('REASON_TYPE') == 1){        
//		                 return true;       
//		       }       
//		       else return false;
//		      });
//		    }
//		}
//	});
//	// 定义一个faultSubReasonStore根据选择的faultReasonStore的FAULT_REASON_ID从数据库中加载子故障类型,使用前必须为其指定参数
//	faultSubReasonStore = new Ext.data.Store({
//		url : 'fault-statistics!getSubFaultReason.action',
//		reader : new Ext.data.JsonReader({
//			root : 'rows',
//			fields : [ 'REASON_ID', 'REASON_NAME' ]
//		})
//	});
//	
//	var faultReasonCombo = new Ext.form.ComboBox(
//			{
//				id : 'faultReasonCombo',
//				store : faultReasonStore,
//				displayField : "REASON_NAME",
//				valueField : 'REASON_ID',
//				triggerAction : 'all',
//				editable : false,
//				value : '',
//				width : 120,
//				listeners : {// 监听事件
//					select : function(combo, record, index) {
//						var parentFaultReasonId = combo.getValue();
//						info_Equip.reason1 = combo.getValue();
//						if (parentFaultReasonId) {
//							faultSubReasonCombo.enable();
//							// 还原二级故障下拉框
//							faultSubReasonCombo.reset();
//							info_Equip.reason2 = '';
//							// 动态改变参数
//							faultSubReasonStore.baseParams.parentFaultReasonId = parentFaultReasonId;
//							faultSubReasonStore.load();
//						} else {
//							faultSubReasonCombo.disable();
//						}
//					}
//				}
//			});
//	var faultSubReasonCombo = new Ext.form.ComboBox({
//		id : 'faultSubReasonCombo',
//		disabled : true,
//		store : faultSubReasonStore,
//		displayField : "REASON_NAME",
//		valueField : 'REASON_ID',
//		triggerAction : 'all',
//		editable : false,
//		value : '',
//		width : 120,
//		listeners : {
//			select :function(combo,record,index){
//				info_Equip.reason2 = combo.getValue();
//			}
//		}
//	});
//	 transformSystemStore_Line = new Ext.data.Store({
//		reader : new Ext.data.JsonReader({
//			root : 'rows',
//			fields : [ 'RESOURCE_PROJECTS_ID', 'DISPLAY_NAME','REASON_TYPE' ]
//		}),
//		proxy : new Ext.data.HttpProxy({
//			url : 'fault-statistics!getTransformSystemList.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
//			disableCaching : false
//		}),
//		 listeners : {
//			    load : function(store,records){    
//			    	faultSubReasonStore.filterBy(function(record,id){       
//			       if(record.get('REASON_TYPE') == 2){        
//			                 return true;       
//			       }       
//			       else return false;
//			      });}
//			    }
//	});
//	
//	transformSystemStore_Line.load();
//	 stateStore_Line = new Ext.data.Store({
//		url : 'fault-statistics!getStateBySysId.action',
//		reader : new Ext.data.JsonReader({
//			root : 'rows',
//			fields : [ 'RESOURCE_STATION_ID', 'STATION_NAME' ]
//		})
//	});
//	var stateCombo_Line = new Ext.form.ComboBox({
//		id : 'stateCombo_Line',
//		disabled : true,
//		fieldLabel: '维护单位',
//		store : stateStore_Line,
//		displayField : "STATION_NAME",
//		valueField : 'RESOURCE_STATION_ID',
//		triggerAction : 'all',
//		editable : false,
//		value : '',
//		width : 120,
//		listeners: {
//			select : function(combo, record, index) {
//				info_Line.danwei = combo.getRawValue();
//			}
//		}
//	});
//	var stateCombo_Equip = new Ext.form.ComboBox({
//		id : 'stateCombo_Equip',
//		disabled : true,
//		fieldLabel: '台站',
//		store : stateStore_Line,
//		displayField : "STATION_NAME",
//		valueField : 'RESOURCE_STATION_ID',
//		triggerAction : 'all',
//		editable : false,
//		hidden :true,
//		value : '',
//		width : 120,
//		listeners: {
//			select : function(combo, record, index) {
//				info_Equip.stationName = combo.getRawValue();
//			}
//		}
//	});
//	var startStateCombo_Line = new Ext.form.ComboBox({
//		id : 'startStateCombo_Line',
//		disabled : true,
//		fieldLabel: '起始台站',
//		store : stateStore_Line,
//		displayField : "STATION_NAME",
//		valueField : 'RESOURCE_STATION_ID',
//		triggerAction : 'all',
//		editable : false,
//		value : '',
//		width : 120,
//		listeners: {
//			select : function(combo, record, index) {
//				info_Line.aStation = combo.getRawValue();
//			}
//		}
//	});
//	var endStateCombo_Line = new Ext.form.ComboBox({
//		id : 'endStateCombo_Line',
//		disabled : true,
//		fieldLabel: '终点台站',
//		store : stateStore_Line,
//		displayField : "STATION_NAME",
//		valueField : 'RESOURCE_STATION_ID',
//		triggerAction : 'all',
//		editable : false,
//		value : '',
//		width : 120,
//		listeners: {
//			select : function(combo, record, index) {
//				info_Line.zStation = combo.getRawValue();
//			}
//		}
//	});
//	var distanceStateCombo_Line = new Ext.form.ComboBox({
//		id : 'distanceStateCombo_Line',
//		disabled : true,
//		fieldLabel: '距离',
//		store : stateStore_Line,
//		displayField : "STATION_NAME",
//		valueField : 'RESOURCE_STATION_ID',
//		triggerAction : 'all',
//		editable : false,
//		value : '',
//		width : 120,
//		listeners: {
//			select : function(combo, record, index) {
//				info_Line.nearStation = combo.getRawValue();
//			}
//		}
//	});
//	var transformSystemCombo_Line = new Ext.form.ComboBox({
//		id : 'transformSystemCombo_Line',
//		fieldLabel : '系统名称',
//		store : transformSystemStore_Line,
//		displayField : "DISPLAY_NAME",
//		valueField : 'RESOURCE_PROJECTS_ID',
//		triggerAction : 'all',
//		mode : 'local',
//		editable : false,
//		width : 120,
//		listeners : {// 监听事件
//			select : function(combo, record, index) {
//				var sysId = combo.getValue();
//				if (sysId) {
//					info_Line.sysName = combo.getRawValue();
//					stateCombo_Line.enable();
//					stateCombo_Line.reset();
//					startStateCombo_Line.enable();
//					startStateCombo_Line.reset();
//					endStateCombo_Line.enable();
//					endStateCombo_Line.reset();
//					distanceStateCombo_Line.enable();
//					distanceStateCombo_Line.reset();
//					// 动态改变参数
//					stateStore_Line.baseParams.sysId = sysId;
//					stateStore_Line.load();
//				} else {
//					stateCombo_Line.disable();
//					startStateCombo_Line.disable();
//					endStateCombo_Line.disable();
//					distanceStateCombo.disable();
//				}
//			}
//		}
//	});
//	var transformSystemCombo_Equip = new Ext.form.ComboBox({
//		id : 'transformSystemCombo_Equip',
//		fieldLabel : '系统名称',
//		store : transformSystemStore_Line,
//		displayField : "DISPLAY_NAME",
//		valueField : 'RESOURCE_PROJECTS_ID',
//		triggerAction : 'all',
//		mode : 'local',
//		hidden : true,
//		editable : false,
//		width : 120,
//		listeners : {// 监听事件
//			select : function(combo, record, index) {
//				var sysId = combo.getValue();
//				if (sysId) {
//					info_Equip.sysName = combo.getRawValue();
//					stateCombo_Equip.enable();
//					info_Equip.systemId = sysId;
//					stateCombo_Equip.reset();
//					info_Equip.stationName ='';
//					// 动态改变参数
//					stateStore_Line.baseParams.sysId = sysId;
//				} else {
//					stateCombo_Equip.disable();
//				}
//			}
//		}
//	});
//	
//	var windowModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
//		singleSelect : true,
//		header : ""
//	});
//	windowModel.sortLock();
//
//	var alarmCm_Line = new Ext.ux.grid.LockingColumnModel({
//		// specify any defaults for each column
//		defaults : {
//			sortable : true
//		// columns are not sortable by default
//		},
//		columns : [ new Ext.grid.RowNumberer({
//			width : 26,
//			locked : true
//		}), windowModel, {
//			id : 'alarmName_Line',
//			header : '告警名称',
//			dataIndex : 'alarm_name',
//			width : 100
//		}, {
//			id : 'alarmLevel_Line',
//			header : '告警等级',
//			dataIndex : 'severity_name',
//			width : 100
//		}, {
//			id : 'neName_Line',
//			header : '网元名',
//			dataIndex : 'ne_name',
//			width : 100
//		}, {
//			id : 'alarmStartTime_Line',
//			header : '开始时间',
//			dataIndex : 'start_time',
//			width : 100,
//			renderer : function(value) {
//				return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//						value.time)) : "";
//			}
//		}, {
//			id : 'alarmEndTime_Line',
//			header : '结束时间',
//			dataIndex : 'clean_time',
//			width : 100,
//			renderer : function(value) {
//				return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//						value.time)) : "";
//			}
//		} ]
//	});
//	var alarmGrid_line = new Ext.grid.EditorGridPanel({
//		id : "alarmGrid_line",
//		cm : alarmCm_Line,
//		height : 100,
//		store : alarmConvergenceStore_Line,
//		stripeRows : true, // 交替行效果
//		loadMask : {
//			msg : '数据加载中...'
//		},
//		selModel : windowModel, // 必须加不然不能选checkbox
//		view : new Ext.ux.grid.LockingGridView(),
//		forceFit : true
//	});
//
//	var rightData_line = [ [ '1', '准确' ], [ '2', '部分准确' ], [ '3', '不准确' ] ];
//	var rightStore_line = new Ext.data.SimpleStore({
//		fields : [ {
//			name : 'value'
//		}, {
//			name : 'displayName'
//		} ]
//	});
//	rightStore_line.loadData(rightData);
//	var rightCombo_line = new Ext.form.ComboBox({
//		id : 'rightCombo_line',
//		fieldLabel : '判断准确性',
//		store : rightStore_line,
//		displayField : "displayName",
//		valueField : 'value',
//		triggerAction : 'all',
//		mode : 'local',
//		editable : false,
//		value : '',
//		width : 100
//	});
//	
//	// 定义一个故障类型faultReasonStore_line从数据库获取故障类型
//	 var faultReasonStore_line = new Ext.data.JsonStore({
//			root : 'rows',
//			fields : [ 'REASON_ID', 'REASON_NAME','REASON_TYPE'],
//			url : 'fault-statistics!getFaultReason.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
//			listeners : {
//			    load : function(store,records){    
//			    	faultReasonStore_line.filterBy(function(record,id){  
//			       if(record.get('REASON_TYPE') == 2){        
//			                 return true;       
//			       }       
//			       else return false;
//			      });
//			    }
//			}
//		});
//	faultSubReasonStore_line = new Ext.data.Store({
//		url : 'fault-statistics!getSubFaultReason.action',
//		reader : new Ext.data.JsonReader({
//			root : 'rows',
//			fields : [ 'REASON_ID', 'REASON_NAME' ]
//		})
//	});
//	
//	var faultReasonCombo_line = new Ext.form.ComboBox(
//			{
//				id : 'faultReasonCombo_line',
//				store : faultReasonStore_line,
//				displayField : "REASON_NAME",
//				valueField : 'REASON_ID',
//				triggerAction : 'all',
//				editable : false,
//				value : '',
//				width : 120,
//				listeners : {// 监听事件
//					select : function(combo, record, index) {
//						var parentFaultReasonId = combo.getValue();
//						info_Line.reason1 = combo.getValue();
//						if (parentFaultReasonId) {
//							faultSubReasonCombo_line.enable();
//							// 还原二级故障下拉框
//							faultSubReasonCombo_line.reset();
//							info_Line.reason2 = '';
//							// 动态改变参数
//							faultSubReasonStore_line.baseParams.parentFaultReasonId = parentFaultReasonId;
//							// 加载二级故障数据源
//							faultSubReasonStore_line.load({
//								callback : function(records, options, success) {
//									if (!success) {
//										Ext.Msg.alert('错误', '查询失败！');
//									} else {
//									}
//								}
//							});
//						} else {
//							faultSubReasonCombo_line.disable();
//						}
//					}
//				}
//			});
//	var faultSubReasonCombo_line = new Ext.form.ComboBox({
//		id : 'faultSubReasonCombo_line',
//		disabled : true,
//		store : faultSubReasonStore_line,
//		displayField : "REASON_NAME",
//		valueField : 'REASON_ID',
//		triggerAction : 'all',
//		editable : false,
//		value : '',
//		width : 120,
//		listeners :{
//			select : function(combo,record,index){
//				info_Line.reason2 = combo.getValue();
//			}
//		}
//	});
//	var info_auto = {
//		xtype : 'fieldset',
//		bodyStyle : 'padding:15px 20px;',
//		region : "center",
//		items : [ {
//			xtype : 'panel',
//			layout : 'column',
//			border : false,
//			items : [ {
//				html : '<label>故障类型：</label>',
//				bodyStyle : 'padding:5px;',
//				border : false
//			}, {
//				columnWidth : 0.3,
//				id : 'faultType_auto',
//				xtype : 'radiogroup',
//				columns : 2,
//				listeners : {
//					change : function(radioGroup, radio) {
//						if (radio.inputValue == 1) {
//							Ext.getCmp('lineFaultPanel').hide();
//							Ext.getCmp('equipFaultPanel').show();
//						} else {
//							Ext.getCmp('equipFaultPanel').hide();
//							Ext.getCmp('lineFaultPanel').show();
//						}
//					}
//				},
//				items : [ {
//					boxLabel : "设备故障",
//					name : 'faultType',
//					id : 'equip',
//					inputValue : 1,
//					checked : true
//				}, {
//					boxLabel : "线路故障",
//					id : 'line',
//					name : 'faultType',
//					inputValue : 2
//				} ]
//			} ]
//		}, {
//			xtype : 'panel',
//			height : 420,
//			layout : 'column',
//			border : false,
//			items : [ {
//				border : false,
//				id : 'equipFaultPanel',
//				height : 420,
//				bodyStyle : 'padding:10px 0px',
//				items : [ {
//					layout : 'column',
//					border : false,
//					height : 420,
//					items : [ config, {
//						columnWidth : .9,
//						border : false,
//						items : [ {
//							border : false,
//							layout : 'hbox',
//							items : [ {
//								border : false,
//								items : [ faultReasonCombo ]
//							}, {
//								border : false,
//								bodyStyle : 'padding:0px 20px',
//								items : [ faultSubReasonCombo ]
//							}, {
//								border : false,
//								layout : 'form',
//								items : [ {
//									xtype : 'checkbox',
//									id : 'systemStop',
//									boxLabel : '系统阻断',
//									handler : function(checkbox, checked) {
//										if (checked) {
//											info_Equip.isBroken = 1;
//										}
//										if (!checked) {
//											info_Equip.isBroken = 0;
//										}
//									}
//								} ]
//							}, {
//								border : false,
//								layout : 'form',
//								labelSeparator : "：",
//								labelWidth : 75,
//								labelPad : 0,
//								bodyStyle : 'padding:0px 20px',
//								items : [ rightCombo ]
//							} ]
//						}, {
//							border : false,
//							height : 10
//						}, {
//							border : false,
//							items : [ {
//								xtype : 'panel',
//								border : false,
//								layout : 'fit',
//								items : [ {
//									xtype : 'panel',
//									border : false,
//									width : 750,
//									items : [ {
//										xtype : 'panel',
//										border : false,
//										layout : 'hbox',
//										items : [ {
//											xtype : 'panel',
//											layout : 'form',
//											labelSeparator : "：",
//											labelWidth : 75,
//											labelPad : 0,
//											border : false,
//											items : [transformSystemCombo_Equip, {
//												xtype : 'textfield',
//												id : 'transforSys_win',
//												fieldLabel : '系统名称',
//												readOnly : true,
//												maxLength : 64,
//												allowBlank : true
//											},{
//												xtype : 'textfield',
//												id : 'ne_win',
//												fieldLabel : '网元',
//												readOnly : true,
//												maxLength : 64,
//												allowBlank : true
//											}, {
//												border : false,
//												height : 55
//											} ]
//										}, {
//											width : 20,
//											border : false
//										}, {
//											xtype : 'panel',
//											layout : 'form',
//											labelSeparator : "：",
//											labelWidth : 45,
//											labelPad : 0,
//											border : false,
//											items : [ {
//												xtype : 'textfield',
//												id : 'ems_win',
//												fieldLabel : '网管',
//												readOnly : true,
//												maxLength : 64,
//												allowBlank : true
//											}, {
//												xtype : 'textfield',
//												id : 'slot_win',
//												fieldLabel : '板卡',
//												readOnly : true,
//												maxLength : 64,
//												allowBlank : true
//											} ]
//										}, {
//											width : 20,
//											border : false
//										}, {
//											xtype : 'panel',
//											layout : 'form',
//											labelSeparator : "：",
//											labelWidth : 45,
//											labelPad : 0,
//											border : false,
//											items : [stateCombo_Equip, {
//												xtype : 'textfield',
//												id : 'station_Equip',
//												fieldLabel : '台站',
//												readOnly : true,
//												maxLength : 64,
//												allowBlank : true
//											}, {
//												xtype : 'button',
//												id : 'equipBtn_win',
//												text : '...',
//												handler :function(){
//													var treeParams = {
//															rootId : 0,
//															rootType : 0,
//															rootText : "FTSP",
//															checkModel : "single",
//															rootVisible : false,
//															// 规定数显示到的层数。8表示树可以展开的到第八个层级
//															leafType : 6
//														};
//														var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
//													var modifyTreePanel = new Ext.Panel({
//														id : "modifyTreePanel",
//														region : "west",
//														width : 210,
//														height : 400,
//														forceFit : true,
//														collapsed : false, // initially collapse the group
//														collapsible : false,
//														collapseMode : 'mini',
//														split : true,
//														html : '<iframe id="modifytreePanel" name = "modifytreePanel" src ="'
//																+ treeurl + '" height="100%" width="100%" frameBorder=0 border=0/>'
//													});
//													var addTreeWindow_Z = new Ext.Window({
//														id : 'addTreeWindow_Z',
//														title : '板卡选择',
//														width : 300,
//														autoHeight : true,
//														minWidth : 350,
//														minHeight : 400,
//														layout : 'fit',
//														plain : false,
//														modal : true,
//														constrain : true,
//														resizable : false,
//														bodyStyle : 'padding:1px;',
//														items : [ modifyTreePanel ],
//														buttons : [ {
//															text : '确定',
//															handler : function() {
//																var iframe = window.frames["tree_panel"] || window.frames[0];
//																var zNode;
//																// 兼容不同浏览器的取值方式
//																if (iframe.getCheckedNodes) {
//																	zNode = iframe.getCheckedNodes(null, "all");
//																} else {
//																	zNode = iframe.contentWindow.getCheckedNodes(null,
//																			"all");
//																}
//
//																if (zNode.length < 1)
//																	Ext.Msg.alert("重新选择", "选择不能为空，请重新选择");
//																else if (zNode.length > 1)
//																	Ext.Msg.alert("重新选择", "不能多选，请重新选择");
//																else if (zNode[0]['attributes']['nodeLevel'] != 6)
//																	Ext.Msg.alert("重新选择", "请选择板卡");
//																else {
//																	zNodeId = zNode[0]['attributes'].nodeId;
//																	zNodeLevel = zNode[0]['attributes'].nodeLevel;
//																	var unitName=zNode[0]['attributes']['additionalInfo']['UNIT_DESC'];
//																	var emsName=zNode[0]['attributes']['emsName'];
//																	var parentNode = zNode[0];
//																	var subnetName,neName;
//																	while(parentNode){
//																		if(parentNode['attributes']['nodeLevel']==4){
//																			neName=parentNode['attributes']['text'];
//																		}else if(parentNode['attributes']['nodeLevel']==3){
//																			subnetName=parentNode['attributes']['text'];
//																		}else if(parentNode['attributes']['nodeLevel']<3){
//																			break;
//																		}
//																		parentNode=parentNode.parentNode;
//																	}
//																	
//																	Ext.getCmp('ne_win').setValue(neName);
//																	Ext.getCmp('slot_win').setValue(unitName);
//																	Ext.getCmp('ems_win').setValue(emsName);
//																	Ext.getCmp('transforSys_win').setValue(subnetName);
//																	info_Equip.ems = emsName;
//																	info_Equip.unitId = zNodeId;
//																	info_Equip.unitName = unitName;
//																	info_Equip.neName = neName;
//																	info_Equip.sysName = subnetName;
//																	
//																	var jsonData = {
//																		"unitId" : zNodeId
//																	};
//																	Ext.Ajax.request({
//																		url : 'fault-statistics!getEquipName.action',
//																		method : 'POST',
//																		params : jsonData,
//																		success : function(response) {
//																			var object = Ext.decode(response.responseText);
//																			if(object.errorCode != 0){
//																				Ext.Msg.alert("异常", "未找到台站信息");
//																			}else{
//																				//Ext.getCmp('ne_win').setValue(object.neName);
//																				//Ext.getCmp('slot_win').setValue(object.unitName);
//																				//Ext.getCmp('ems_win').setValue(object.emsName);
//																				//Ext.getCmp('transforSys_win').setValue(object.subNetName);
//																				Ext.getCmp('station_Equip').setValue(object.stationName);
//																				//info_Equip.ems = object.emsName;
//																				//info_Equip.unitId = zNodeId;
//																				//info_Equip.unitName = object.unitName;
//																				//info_Equip.neName = object.neName;
//																				//info_Equip.sysName = object.subNetName;
//																				info_Equip.stationName = object.stationName;
//																				addTreeWindow_Z.close();
//																			}
//																		},
//																		failure : function(response) {
//																			Ext.Msg.alert("异常", "无法连接到服务器");
//																		}
//																	});
//																}
//															}
//														}, {
//															text : '取消',
//															handler : function() {
//																addTreeWindow_Z.close();
//															}
//														} ]
//													});
//													addTreeWindow_Z.show();
//												} 
//											} ]
//										} ]
//									} ]
//								} ]
//							} ]
//						}, {
//							border : false,
//							layout : 'column',
//							items : [ {
//								html : '<label>开始时间：</label>',
//								bodyStyle : 'padding:8px 0px;',
//								border : false
//							}, {
//								xtype : 'textfield',
//								id : 'startTime_win',
//								name : 'startTime_win',
//								allowBlank : false,
//								width : 150,
//								cls : 'Wdate',
//								listeners : {
//									'focus' : function() {
//										WdatePicker({
//											el : "startTime_win",
//											isShowClear : false,
//											readOnly : true,
//											dateFmt : 'yyyy-MM-dd HH:mm',
//											autoPickDate : true,
//											maxDate : '%y-%M-%d'
//										});
//										this.blur();
//									}
//								}
//							}, {
//								html : '<label>结束时间：</label>',
//								bodyStyle : 'padding:5px;',
//								border : false
//							}, {
//								xtype : 'textfield',
//								id : 'endTime_win',
//								name : 'endTime_win',
//								allowBlank : false,
//								width : 150,
//								cls : 'Wdate',
//								listeners : {
//									'focus' : function() {
//										WdatePicker({
//											el : "endTime_win",
//											isShowClear : false,
//											readOnly : true,
//											dateFmt : 'yyyy-MM-dd HH:mm',
//											autoPickDate : true,
//											maxDate : '%y-%M-%d'
//										});
//										this.blur();
//									}
//								}
//							}, {
//								html : '<label>确认恢复：</label>',
//								bodyStyle : 'padding:5px;',
//								border : false
//							}, {
//								xtype : 'textfield',
//								id : 'confirmTime_win',
//								name : 'confirmTime_win',
//								allowBlank : false,
//								width : 150,
//								cls : 'Wdate',
//								listeners : {
//									'focus' : function() {
//										WdatePicker({
//											el : "confirmTime_win",
//											isShowClear : false,
//											readOnly : true,
//											dateFmt : 'yyyy-MM-dd HH:mm',
//											autoPickDate : true,
//											maxDate : '%y-%M-%d'
//										});
//										this.blur();
//									}
//								}
//							} ]
//						}, {
//							height : 10,
//							border : false
//						}, {
//							border : false,
//							layout : 'hbox',
//							items : [ {
//								border : false,
//								flex : 12
//							}, {
//								xtype : 'button',
//								width : 40,
//								flex : 4,
//								text : '刷新',
//								handler : function(){
//									alarmFresh(alarmConvergenceStore_Equip);
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 40,
//								flex : 4,
//								text : '增加',
//								handler :function(){
//									showAlarm();
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 40,
//								flex : 4,
//								text : '删除',
//								handler : function(){
//									deleteAlarm(info_Equip,"alarmGrid_Equip",alarmConvergenceStore_Equip);
//								}
//							} ]
//
//						}, {
//							border : false,
//							items : alarmGrid_Equip
//						}, {
//							border : false,
//							height : 10
//						}, {
//							xtype : 'panel',
//							border : false,
//							layout : 'fit',
//							items : {
//								xtype : 'textarea',
//								id :'area_Equip'
//							}
//						}, {
//							border : false,
//							height : 8
//						}, {
//							border : false,
//							layout : 'hbox',
//							items : [ {
//								xtype : 'button',
//								flex : 2,
//								text : '保存',
//								handler : function(){
//									getEquipInfo();
//									save(info_Equip,0,fault_window);
//								}
//							}, {
//								border : false,
//								flex : 12
//							}, {
//								xtype : 'button',
//								width : 70,
//								flex : 2,
//								text : '故障响应',
//								handler : function(){
//									getEquipInfo();
//									save(info_Equip,2,fault_window);
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 70,
//								flex : 2,
//								text : '故障恢复',
//								handler : function(){
//									getEquipInfo();
//									save(info_Equip,3,fault_window);
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 70,
//								flex : 2,
//								text : '归档',
//								handler : function(){
//									getEquipInfo();
//									save(info_Equip,5,fault_window);
//								}
//							} ]
//						} ]
//					} ]
//				} ]
//			}, {
//				border : false,
//				height : 420,
//				bodyStyle : 'padding:10px 0px',
//				items : [ {
//					layout : 'column',
//					border : false,
//					id : 'lineFaultPanel',
//					height : 420,
//					items : [ config, {
//						columnWidth : .9,
//						border : false,
//						items : [ {
//							border : false,
//							layout : 'hbox',
//							items : [ {
//								border : false,
//								items : [ faultReasonCombo_line ]
//							}, {
//								border : false,
//								bodyStyle : 'padding:0px 20px',
//								items : [ faultSubReasonCombo_line ]
//							}, {
//								border : false,
//								layout : 'form',
//								items : [ {
//									xtype : 'checkbox',
//									id : 'systemStop_line',
//									boxLabel : '系统阻断',
//									handler : function(checkbox, checked) {
//										if (checked) {
//											info_Line.isBroken = 1;
//										}
//										if (!checked) {
//											info_Line.isBroken = 0;
//										}
//									}
//								} ]
//							}, {
//								border : false,
//								layout : 'form',
//								labelSeparator : "：",
//								labelWidth : 75,
//								labelPad : 0,
//								bodyStyle : 'padding:0px 20px',
//								items : [ rightCombo_line ]
//							} ]
//						}, {
//							border : false,
//							height : 10
//						}, {
//							border : false,
//							items : [ {
//								xtype : 'panel',
//								border : false,
//								layout : 'fit',
//								items : [ {
//									xtype : 'panel',
//									border : false,
//									width : 750,
//									items : [ {
//										xtype : 'panel',
//										border : false,
//										layout : 'hbox',
//										width : 750,
//										items : [ {
//											xtype : 'panel',
//											layout : 'form',
//											labelSeparator : "：",
//											labelWidth : 75,
//											labelPad : 0,
//											border : false,
//											items : [ transformSystemCombo_Line,
//											          startStateCombo_Line, distanceStateCombo_Line ]
//										}, {
//											width : 20,
//											border : false
//										}, {
//											xtype : 'panel',
//											layout : 'form',
//											labelSeparator : "：",
//											labelWidth : 75,
//											labelPad : 0,
//											border : false,
//											items : [ stateCombo_Line,
//											          endStateCombo_Line, {
//												xtype : 'textfield',
//												id : 'kilometer_win',
//												fieldLabel : '公里'
//											} ]
//										}, {
//											width : 20,
//											border : false
//										}, {
//											xtype : 'panel',
//											layout : 'form',
//											labelSeparator : "：",
//											labelWidth : 45,
//											labelPad : 0,
//											border : false,
//											items : [ {
//												height : 25,
//												border : false
//											}, {
//												xtype : 'textfield',
//												id : 'jingdu_win',
//												fieldLabel : '经度'
//											}, {
//												xtype : 'textfield',
//												id : 'weidu_win',
//												fieldLabel : '纬度'
//											} , {
//												height : 25,
//												border : false
//											}]
//										} ]
//									} ]
//								} ]
//							} ]
//						}, {
//							border : false,
//							layout : 'column',
//							items : [ {
//								html : '<label>开始时间：</label>',
//								bodyStyle : 'padding:8px 0px;',
//								border : false
//							}, {
//								xtype : 'textfield',
//								id : 'startTime_win_line',
//								name : 'startTime_win_line',
//								allowBlank : false,
//								width : 150,
//								cls : 'Wdate',
//								listeners : {
//									'focus' : function() {
//										WdatePicker({
//											el : "startTime_win_line",
//											isShowClear : false,
//											readOnly : true,
//											dateFmt : 'yyyy-MM-dd HH:mm',
//											autoPickDate : true,
//											maxDate : '%y-%M-%d'
//										});
//										this.blur();
//									}
//								}
//							}, {
//								html : '<label>结束时间：</label>',
//								bodyStyle : 'padding:5px;',
//								border : false
//							}, {
//								xtype : 'textfield',
//								id : 'endTime_win_line',
//								name : 'endTime_win_line',
//								allowBlank : false,
//								width : 150,
//								cls : 'Wdate',
//								listeners : {
//									'focus' : function() {
//										WdatePicker({
//											el : "endTime_win_line",
//											isShowClear : false,
//											readOnly : true,
//											dateFmt : 'yyyy-MM-dd HH:mm',
//											autoPickDate : true,
//											maxDate : '%y-%M-%d'
//										});
//										this.blur();
//									}
//								}
//							}, {
//								html : '<label>确认恢复：</label>',
//								bodyStyle : 'padding:5px;',
//								border : false
//							}, {
//								xtype : 'textfield',
//								id : 'confirmTime_win_line',
//								name : 'confirmTime_win_line',
//								allowBlank : false,
//								width : 150,
//								cls : 'Wdate',
//								listeners : {
//									'focus' : function() {
//										WdatePicker({
//											el : "confirmTime_win_line",
//											isShowClear : false,
//											readOnly : true,
//											dateFmt : 'yyyy-MM-dd HH:mm',
//											autoPickDate : true,
//											maxDate : '%y-%M-%d'
//										});
//										this.blur();
//									}
//								}
//							} ]
//						}, {
//							height : 10,
//							border : false
//						}, {
//							border : false,
//							layout : 'hbox',
//							items : [ {
//								border : false,
//								flex : 12
//							}, {
//								xtype : 'button',
//								width : 40,
//								flex : 4,
//								text : '刷新',
//								handler : function(){
//									alarmFresh(alarmConvergenceStore_Line);
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 40,
//								flex : 4,
//								text : '增加',
//								handler :function(){
//									showAlarm();
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 40,
//								flex : 4,
//								text : '删除',
//								handler : function(){
//									deleteAlarm(info_Equip,"alarmGrid_line",alarmConvergenceStore_Line);
//								}
//							} ]
//
//						}, {
//							border : false,
//							items : alarmGrid_line
//						}, {
//							border : false,
//							height : 10
//						}, {
//							xtype : 'panel',
//							border : false,
//							layout : 'fit',
//							items : {
//								xtype : 'textarea',
//								id : 'area_Line'
//							}
//						}, {
//							border : false,
//							height : 10
//						}, {
//							border : false,
//							layout : 'hbox',
//							items : [ {
//								xtype : 'button',
//								flex : 2,
//								text : '保存',
//								handler : function(){
//									getLineInfo();
//									save(info_Line,0,fault_window);
//								}
//							}, {
//								border : false,
//								flex : 12
//							}, {
//								xtype : 'button',
//								width : 70,
//								flex : 2,
//								text : '故障响应',
//								handler : function(){
//									getLineInfo();
//									save(info_Line,2,fault_window);
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 70,
//								flex : 2,
//								text : '故障恢复',
//								handler : function(){
//									getLineInfo();
//									save(info_Line,3,fault_window);
//								}
//							}, {
//								xtype : '',
//								border : false,
//								flex : 1
//							}, {
//								xtype : 'button',
//								width : 70,
//								flex : 2,
//								text : '归档',
//								handler : function(){
//									getLineInfo();
//									save(info_Line,5,fault_window);
//								}
//							} ]
//						} ]
//					} ]
//				} ]
//			} ]
//		} ]
//	};
//	var page = {
//		height : 0.8 * Ext.getCmp('win').getHeight() - 35,
//		weight : 0.8 * Ext.getCmp('win').getWidth() - 32
//	};
//	var mainPanel = new Ext.Panel({
//		width : 870,
//		height : 550,
//		bodyStyle : 'padding:6px 20px;',
//		layout : 'form',
//		items : [ northPanel, info_auto ]
//	});
//	var fault_window = new Ext.Window({
//		title : "故障信息",
//		modal : true,
//		width : 0.63 * Ext.getCmp('win').getWidth(),
//		height : 0.9 * Ext.getCmp('win').getHeight() + 10,
//		minWidth : 0.5 * Ext.getCmp('win').getWidth(),
//		autoScroll : true,
//		listeners : {
//			beforeclose : function (p){
//				info_Equip = {};
//				info_Line = {};
//				alarmConvergenceStore_Equip.removeAll();
//				alarmConvergenceStore_Line.removeAll();
//				var pageTool = Ext.getCmp('faultManagementPageTool');
//				pageTool.doLoad(pageTool.cursor);
//			}
//		},
//		items : [ mainPanel ]
//	});
//	fault_window.show();
//	Ext.getCmp('faultSource_auto').onSetValue('manul', true);
//	Ext.getCmp('lineFaultPanel').hide();
//};
// 删除
var del = function() {
	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
	if (cell.length == 0) {
		Ext.Msg.alert("提示", "请选择需要删除的故障记录！");
	} else {
		var faultId = {
			"paramMap.faultId" : cell[0].get('FAULT_ID')
		};
		Ext.Msg.confirm('提示', '确认删除？', function(btn) {
			if (btn == "yes") {
				Ext.Ajax.request({
					url : 'fault-management!deleteFaultRecord.action',
					method : 'POST',
					params : faultId,
					success : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
						if (obj.returnResult == 1) {
							
							Ext.Msg.alert("提示", "删除成功！", function(r) {
								var pageTool = Ext.getCmp('faultManagementPageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								
								updateFaultInfo_Main();
							});
						};
					},
					failue : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '访问服务器失败！');
					}
				});
			}
		});
	}
};
// 故障统计
var faultStatistics = function() {
	parent.addTabPage("../faultManagement/failureStatistics.jsp", "故障统计", authSequence);
};
function getLineInfo(){
	info_Line.type = 2;
	info_Line.source = Ext.getCmp('faultSource_auto').getValue().inputValue;
	info_Line.accuracy = Ext.getCmp('rightCombo_line').getValue();
	info_Line.startTime = Ext.getCmp('startTime_win_line').getValue();
	info_Line.endTime = Ext.getCmp('endTime_win_line').getValue();
	info_Line.confirmTime = Ext.getCmp('confirmTime_win_line').getValue();
	info_Line.longitude = Ext.getCmp('jingdu_win').getValue();
	info_Line.latitude = Ext.getCmp('weidu_win').getValue();
	info_Line.distance = Ext.getCmp('kilometer_win').getValue();
	info_Line.memo = Ext.getCmp('area_Line').getValue();
}
function getEquipInfo(){
	info_Equip.type = 1;
	info_Equip.source = Ext.getCmp('faultSource_auto').getValue().inputValue;
	info_Equip.accuracy = Ext.getCmp('rightCombo').getValue();
	info_Equip.startTime = Ext.getCmp('startTime_win').getValue();
	info_Equip.endTime = Ext.getCmp('endTime_win').getValue();
	info_Equip.confirmTime = Ext.getCmp('confirmTime_win').getValue();
	info_Equip.memo = Ext.getCmp('area_Equip').getValue();
}
function save(infoType,next,window){
	var jsonData = {
			"jsonString" : Ext.encode(infoType)
		};
		Ext.Ajax.request({
			url : 'fault-statistics!save.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var object = Ext.decode(response.responseText);
				if(object.returnResult ==1){
					if(infoType.type==1){
						info_Equip.faultId = object.total;
					}else{
						info_Line.faultId = object.total;
					}
					if(next>1){
						Ext.Ajax.request({
							url:'fault-statistics!faultProcess.action',
							method : 'POST',
							params : {"faultId":infoType.faultId==0?infoType.faultId:infoType.faultId,"processType":next},
							success : function(response){
								var result = Ext.decode(response.responseText);
								if(result.returnResult == 1 ){
									// 故障确认后更新故障通知信息
									if (next == 2) {updateMsgBox();}
									if(next != 3){
										Ext.Msg.alert('提示','操作成功');
									}else if(infoType.type ==1){
										//设备故障
//										Ext.getCmp('endTime_win').setValue(result.returnMessage);
									}else{
										//线路故障
//										Ext.getCmp('endTime_win_line').setValue(result.returnMessage);
									}Ext.Msg.alert('提示','操作成功');
									if(typeof(window)=='object'){
										window.close();
									}
								}else{
									Ext.Msg.alert('提示',result.returnMessage);
								}
							},
							failure : function(response){
								Ext.Msg.alert("异常", "无法访问服务器");
							}
						});
					}
					else {
						if(typeof(window)=='object'&&!next){
							window.close();
						}
						Ext.Msg.alert('提示','保存成功');
					}
				}else{
					Ext.Msg.alert('提示',object.returnMessage);
				}
			},
			failure : function(response) {
				Ext.Msg.alert("异常", response.responseText);
			}
		});
}
function showAlarm(){
	var store = new Ext.data.Store({
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, ["AlarmId","AlarmName","NeName","StartTime","ClearTime","Severity","PtpId",
		    "SystemName","EmsName","StationName","UnitDesc","UnitId"]),
		url : 'fault-statistics!getCurAlmList.action' // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
	});
	store.load();
	// ==========================page=============================
	var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
		singleSelect : true,
		header : ""
	});
	checkboxSelectionModel.sortLock();
	var cm = new Ext.ux.grid.LockingColumnModel({
		// specify any defaults for each column
		defaults : {
			sortable : true
		// columns are not sortable by default
		},
		columns : [ new Ext.grid.RowNumberer({
			width:26,
			locked : true
		}), checkboxSelectionModel,{
			header : '告警名称',
			dataIndex : 'AlarmName',
			width : 100
		}, {
			header : '告警等级',
			dataIndex : 'Severity',
			width : 100
		},  {
			header : '板卡',
			dataIndex : 'UnitDesc',
			width : 100
		},{
			header : '网元名',
			dataIndex : 'NeName',
			width : 100
		}, {
			header : '开始时间',
			dataIndex : 'StartTime',
			width : 100
		}, {
			header : '结束时间',
			dataIndex : 'ClearTime',
			width : 100
		}]
	});

	var pageTool = new Ext.PagingToolbar({
		pageSize : 500,// 每页显示的记录值
		store : store,
		displayInfo : true,
		displayMsg : '当前 {0} - {1} ，总数 {2}',
		emptyMsg : "没有记录"
	});

	var gridPanel = new Ext.grid.EditorGridPanel({
		region : "center",
		id : 'gridPanel_Alarm',
		cm : cm,
		store : store,
		stripeRows : true, // 交替行效果
		loadMask : {
			msg : '数据加载中...'
		},
		selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
		view : new Ext.ux.grid.LockingGridView(),
		forceFit : true,
		bbar : pageTool
	});
	var alarmWindow = new Ext.Window({
		height : 500,
		width : 700,
		modal : true,
		title : '当前告警',
		layout : 'border',
		items :[ gridPanel],
		listeners : {
			beforeClose : function(){
				if(info_Equip.faultId){
					alarmConvergenceStore_Equip.load();
				}if(info_Line.faultId){
					alarmConvergenceStore_Line.load();
				}
			}
		},
		buttons : [ {
			text : '确定',
			handler : function() {
				var cell = Ext.getCmp('gridPanel_Alarm').getSelectionModel().getSelections();
				if(cell.length > 0){
					var params = {
							alarmId : cell[0].get('AlarmId'),
							faultId : info_Equip.faultId==0?info_Line.faultId:info_Equip.faultId,
						    startTime : cell[0].get('StartTime'),
						    alarmName : cell[0].get('AlarmName'),
						    neName : cell[0].get('NeName'),
						    clearTime :cell[0].get('ClearTime'),
						    severity : cell[0].get('Severity'),
						    systemName : cell[0].get('SystemName'),
						    emsName : cell[0].get('EmsName'),
						    stationName : cell[0].get('StationName'),
						    unitName : cell[0].get('UnitDesc')
					};
						alarmConvergenceStore_Equip.baseParams.faultId = params.faultId;
						alarmConvergenceStore_Line.baseParams.faultId = params.faultId;
					Ext.Ajax.request({
						url:'fault-statistics!alarmAdd.action',
						method : 'POST',
						params : params,
						success : function(response){
							var result = Ext.decode(response.responseText);
							if(result.returnResult == 1){
								Ext.Msg.alert('提示','操作成功');
							}else{
								Ext.Msg.alert('提示',result.returnMessage);
							}
						},
						failure : function(response){
							Ext.Msg.alert("异常", "无法访问服务器");
						}
					});
				}else{
					Ext.Msg.alert('提示','请勾选需要增加的告警');
				}
			}
		}, {
			text : '取消',
			handler : function() {
				alarmWindow.close();
			}
		} ]
	});
	alarmWindow.show();
}
function deleteAlarm(infoType,gridId,store){
	//alert(gridId);
	var cell = Ext.getCmp(gridId).getSelectionModel().getSelections();
	var alarmId = cell[0].get('alarm_id');
	Ext.Ajax.request({
		url:'fault-statistics!alarmDelete.action',
		method : 'POST',
		params : {"faultId":infoType.faultId?infoType.faultId:0,"alarmId":alarmId},
		success : function(response){
			var result = Ext.decode(response.responseText);
			if(result.returnResult == 1){
				Ext.Msg.alert('提示','操作成功');
				alarmFresh(store);
			}else{
				Ext.Msg.alert('提示',result.returnMessage);
			}
		},
		failure : function(response){
			Ext.Msg.alert("异常", "无法访问服务器");
		}
	});
}
function alarmFresh(store){
	store.load();
}
// 更新主界面故障信息显示
function updateMsgBox() {
	WebMsgPush.updateFaultMsg();
}
