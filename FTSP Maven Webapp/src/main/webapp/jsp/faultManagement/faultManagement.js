/**
 * 使用该文件前必须为myStateId,myPageTool,myStore赋值
 */
// 主Grid
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
	stateId : myStateId,// 保持列锁定状态
	columns : [ new Ext.grid.RowNumberer({
		width : 26,
		locked : true
	}), checkboxSelectionModel,{
		id :'faultId_cm',
		header: '故障Id',
		dataIndex : 'FAULT_ID',
		width : 100,
		hidden : true
	}, {
		id : 'faultNo_cm',
		header : '故障编号',
		dataIndex : 'FAULT_NO',
		width : 100
	}, {
		id : 'transSys_cm',
		header : '传输系统',
		dataIndex : 'SYSTEM_NAME',
		width : 100
	}, {
		id : 'faultType_cm',
		header : '故障类别',
		dataIndex : 'TYPE',
		width : 100,
		renderer: function(value){
			if(value == 1){
				return "设备故障";
			}else if(value == 2){
				return "线路故障";
			}else return value;
		}
	}, {
		id : 'faultReason_cm',
		header : '故障原因',
		dataIndex : 'REASON_NAME',
		width : 100
	}, {
		id : 'faultLocation_cm',
		header : '故障位置',
		dataIndex : 'FAULT_LOCATION',
		width : 100
	}, {
		id : 'state_cm',
		header : '状态',
		dataIndex : 'STATUS',
		width : 100,
		renderer : statusRenderer
	}, {
		id : 'confirmPerson_cm',
		header : '确认者',
		dataIndex : 'ACK_USER',
		width : 100
	}, {
		id : 'startTime_cm',
		header : '开始时间',
		dataIndex : 'START_TIME',
		width : 100,
		renderer : function(value) {
			return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
					value.time)) : "";
		}
	},{
		id : 'alarmClearTime_cm',
		header : '告警结束时间',
		dataIndex : 'ALM_CLEAR_TIME',
		width : 100,
		renderer : function(value) {
			return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
					value.time)) : "";
		}
	},{
		id : 'recoveryTime_cm',
		header : '确认恢复时间',
		dataIndex : 'END_TIME',
		width : 100,
		renderer : function(value) {
			return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
					value.time)) : "";
		}
	}, {
		id : 'alarmLast_cm',
		header : '告警历时',
		dataIndex : 'ALARM_LAST',
		width : 100
	},
//	{
//		id : 'endTime_cm',
//		header : '结束时间',
//		dataIndex : 'END_TIME',
//		width : 100,
//		renderer : function(value) {
//			return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
//					value.time)) : "";
//		}
//	},
	{
		id : 'faultLast_cm',
		header : '故障历时',
		dataIndex : 'FAULT_LAST',
		width : 100
	}, {
		id : 'systemInterupt_cm',
		header : '系统阻断',
		dataIndex : 'IS_BROKEN',
		width : 100,
		renderer : function(v, m, r){
			if(typeof v == 'number' && v == 0){
				return '未阻断';
			}else if(typeof v == 'number' && v == 1){
				return '阻断';
			}else{
				return '无法判断';
			}
		}
	}, {
		id : 'faultGenerate_cm',
		header : '故障生成',
		dataIndex : 'SOURCE',
		width : 100,
		renderer: faultGenerateRenderer
	}, {
		id : 'ems_cm',
		header : '所属网管',
		dataIndex : 'EMS_NAME',
		width : 100
	} ]
});

//var transformSystemStore = new Ext.data.Store({
//	reader : new Ext.data.JsonReader({
//		root : 'rows',
//		fields : [ 'RESOURCE_PROJECTS_ID', 'DISPLAY_NAME' ]
//	}),
//	proxy : new Ext.data.HttpProxy({
//		url : 'fault-statistics!getTransformSystem.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
//		disableCaching : false
//	})
//});
//transformSystemStore.load();
//var transformSystemCombo = new Ext.form.ComboBox({
//	id : 'transformSystemCombo',
//	store : transformSystemStore,
//	displayField : "DISPLAY_NAME",
//	valueField : 'RESOURCE_PROJECTS_ID',
//	triggerAction : 'all',
//	mode : 'local',
//	editable : false,
//	allowBlank : false,
//	value : '全部',
//	width : 100
//});


//故障生成
var faultGenerateData = [ [ 0, '全部' ],[1, '自动生成'],[2, '人工录入'] ];
var faultGenerateStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
faultGenerateStore.loadData(faultGenerateData);
function faultGenerateRenderer(v, m, r) {
	return (typeof v == 'number' && faultGenerateData[v] != null) ? faultGenerateData[v][1] : v;
}

var faultGenerateCombo = new Ext.form.ComboBox({
	id : 'faultGenerateCombo',
	store : faultGenerateStore,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : 0,
	width : 100
});
// 一个buttons的配置数组,
var tbar = [ "-", "开始时间：从", {
	xtype : 'textfield',
	id : 'startTime',
	name : 'startTime',
	allowBlank : false,
	width : 150,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startTime",
				isShowClear : false,
				readOnly : true,
				dateFmt : 'yyyy-MM-dd HH:mm:ss',
				autoPickDate : true,
				onpicked: function(){
					var endTime=Ext.getCmp('endTime').getValue();
					var startTime=Ext.getCmp('startTime').getValue();
					if(endTime&&endTime.length>0&&
						(Date.parseDate(endTime,"Y-m-d H:i:s").getTime()<
						 Date.parseDate(startTime,"Y-m-d H:i:s").getTime()))
						Ext.getCmp('endTime').setValue('');
				}
			});
			this.blur();
		}
	}
	}, "至", {
		xtype : 'textfield',
		id : 'endTime',
		name : 'endTime',
		allowBlank : false,
		width : 150,
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "endTime",
					isShowClear : false,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd HH:mm:ss',
					autoPickDate : true,
					minDate:Ext.getCmp('startTime').getValue()
				});
				this.blur();
			}
		}
	},{
		xtype : 'button',
		text : '清空',
		handler : function(){
			Ext.getCmp("startTime").reset();
			Ext.getCmp("endTime").reset();
		}
	}, "-", "状态：",statusCombo, "-", "故障生成：",faultGenerateCombo, "-", {
		text : '查询',
		handler : queryFaultInfo,
		icon : '../../resource/images/btnImages/search.png',
		privilege : viewAuth
	}, "-", {
		text : '故障信息',
		handler : faultInfo,
		icon : '../../resource/images/btnImages/information.png',
		privilege : actionAuth
	}, "-", {
		text : '创建',
//		handler : create,
		icon : '../../resource/images/btnImages/add.png',
		privilege : addAuth,
		menu : [{
			text : '设备故障',
			handler : function(){
				createFaultInfoWindow(true, 1);
				Ext.getCmp("faultGenerateDis").setValue(faultGenerateRenderer(2));
				Ext.getCmp("faultTypeDis").setValue(faultTypeRenderer(1));
				Ext.getCmp("faultConfirmBtn").setText("故障生成");
				Ext.getCmp("faultConfirmBtn").removeListener('click', faultConfirm);
				Ext.getCmp("faultConfirmBtn").addListener('click',faultGenerate);
				Ext.getCmp("analyzeAccuracyCombo").setVisible(false);
				Ext.getCmp("analyzeAccuracyDis").setVisible(false);
			}
		},{
			text : '线路故障',
			handler : function(){
				createFaultInfoWindow(true, 2);
				Ext.getCmp("faultGenerateDis").setValue(faultGenerateRenderer(2));
				Ext.getCmp("faultTypeDis").setValue(faultTypeRenderer(2));
				Ext.getCmp("faultConfirmBtn").setText("故障生成");
				Ext.getCmp("faultConfirmBtn").removeListener('click', faultConfirm);
				Ext.getCmp("faultConfirmBtn").addListener('click',faultGenerate);
				Ext.getCmp("analyzeAccuracyCombo").setVisible(false);
				Ext.getCmp("analyzeAccuracyDis").setVisible(false);
			}
		}]
	}, "-", {
		text : '删除',
		handler : del,
		icon : '../../resource/images/btnImages/delete.png',
		privilege : delAuth
	}, "-", {
		text : '故障统计',
		handler : faultStatistics,
		icon : '../../resource/images/btnImages/information.png',
		privilege : viewAuth
	} ];

// 定义一个gridPanel
var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	stateId : myStateId, // 注意！！！这个ID不能与其他页面的重复
	stateful : false,
	autoScroll : true,
	cm : cm,
	store : faultManagementStore,
	stripeRows : true, // 交替行效果
	loadMask : {
		msg : '数据加载中...'
	},
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	bbar : faultManagementPageTool,
	tbar : tbar
});