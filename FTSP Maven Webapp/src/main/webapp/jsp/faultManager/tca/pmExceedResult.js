//窗口关闭时清除引用
var arg = top.pmExceed.args[tabId];
//console.log(arg.param);
function genCol(field, value){
	return {
		xtype:'compositefield',
		height:20,
		items:[{
			xtype:"label",
			text:field,
			width:120
		}, {
			xtype:"label",
			text:value,
			flex:1
		}]
	};
}

var westPanel = new Ext.Panel({
	id : "westPanel",
	region : "north",
	autoScroll : true,
	height : 150,
	border : false,
	boxMinHeight : 160,
	layout : 'form',
	defaults : {
		margins : '5 5 5 5'
	},
	items : [{
		xtype : "label",
		fieldLabel:"越限事件类别",
		labelWidth:110,
		text : [ "已结束", "未结束" ][arg.param.eventType],
		flex : 1
	}, {
		xtype : "label",
		fieldLabel:"监测周期",
		labelWidth:110,
		text : arg.param.periodNames,
		flex : 1
	}, {
		xtype : "label",
		fieldLabel:"发生时间",
		labelWidth:110,
		text : "从 " + arg.param.startTime + " 到  " + arg.param.endTime,
		flex : 1
	}, {
		xtype : "label",
		fieldLabel:"TP等级",
		labelWidth:110,
		text : arg.param.tpNames,
		flex : 1
	}, {
		xtype : "label",
		fieldLabel:"物理量",
		labelWidth:110,
		text : arg.param.physicNames,
		flex : 1
	}, {
		xtype : "label",
		fieldLabel:"计数值",
		labelWidth:110,
		text : arg.param.numbericNames,
		flex : 1
	}, {
		xtype : "label",
		fieldLabel:"选择对象",
		labelWidth:110,
		id:"targetLabel",
		text : arg.param.targetNames,
		flex : 1
	} ]
});
//页面大小调整
Ext.getCmp("targetLabel").on("afterrender", function(me){
	var height = me.getHeight() + me.getPosition()[1];
	westPanel.setHeight(height + 10);
	Ext.getCmp("win").doLayout();
});

var gridPanel;
var dataStore;
(function() {
	dataStore = new Ext.data.Store({
		url : 'fault!getPmExceedData.action',
		baseParams : {
			jsonString:Ext.encode(arg.param),
			start : 0,
			limit : 100
		},
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "ID", "FILTER_FOR_CLEAR", "BASE_EMS_GROUP_ID", "BASE_SUBNET_ID",
				"BASE_EMS_CONNECTION_ID", "BASE_NE_ID", "BASE_RACK_ID",
				"BASE_SHELF_ID", "BASE_SLOT_ID", "BASE_SUB_SLOT_ID",
				"BASE_UNIT_ID", "BASE_SUB_UNIT_ID", "BASE_PTP_ID",
				"BASE_SDH_CTP_ID", "BASE_OTN_CTP_ID", "EMS_GROUP_NAME",
				"SUBNET_NAME", "EMS_NAME", "NE_NAME", "SLOT_NAME", "UNIT_NAME",
				"PORT_NAME", "CTP_NAME", "PRODUCT_NAME", "NATIVE_EMS_NAME",
				"TARGET_TYPE", "LAYER_RATE", "DOMAIN", "PTP_TYPE", "RATE",
				"PERCEIVED_SEVERITY", "PM_STD_INDEX", "PM_INDEX", "PM_VALUE",
				"TYPE", "PM_DESCRIPTION", "LOCATION", "UNIT", "GRANULARITY",
				"THRESHOLD_TYPE", "CLEAR_STATUS", "IS_CLEARABLE", "EMS_TIME",
				"ARISES_TIME", "SAVE_TIME", "CLEAR_TIME" ])
	});
	dataStore.load();
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var domainNames = [ "", "设备保护", "环保护", "ATM保护", "RPR保护", "WDM保护" ];
	var protGrpTypeNames = [ "1+1 MSP", "1:N MSP", "2F BLSR", "4F BLSR",
			"1+1 ATM", "1:N ATM" ];
	var affectedBusiness = [ "全部","未知","无业务","一般业务","大客户业务","重保业务"];
	var severity = ["INDETERMINATE","CRITICAL","MAJOR","MINOR","WARNING","CLEARED"];
	var thrType = ["TWM_HIGHEST","TWM_HIGH","TWM_LOW","TWM_LOWEST"];
	var granularty = ["-","15分钟","24小时"];
	var cm = new Ext.grid.ColumnModel({
		// specify any defaults for each column
		defaults : {
			sortable : true,
			width : 100
		// columns are not sortable by default
		},
		columns : [
				new Ext.grid.RowNumberer({
					width : 26
				}),
				selModel,
				{
					id : 'TRAFFIC_FAULT',
					header : '影响业务类型',
					dataIndex : 'TRAFFIC_FAULT',
					renderer : function(v, m, r) {
						return affectedBusiness[v];
					}
				},
				{
					id : 'PERCEIVED_SEVERITY',
					header : '级别',
					dataIndex : 'PERCEIVED_SEVERITY',
					renderer : function(v, m, r) {
						return severity[v];
					}
				},
				{
					id : 'EMS_GROUP_NAME',
					header : '网管分组',
					dataIndex : 'EMS_GROUP_NAME'
				},{
					id : 'EMS_NAME',
					header : '网管',
					dataIndex : 'EMS_NAME'
				},
				{
					id : 'NE_NAME',
					header : '网元',
					dataIndex : 'NE_NAME'
				},
				{
					id : 'PRODUCT_NAME',
					header : '网元型号',
					dataIndex : 'PRODUCT_NAME'
				},
				{
					id : 'OBJECT_NAME',
					header : '监测对象',
					dataIndex : 'OBJECT_NAME'
				},
				{
					id : 'DOMAIN',
					header : '业务类型',
					dataIndex : 'DOMAIN',
					renderer : function(v, m, r) {
						return domainNames[v >> 0];
					}
				},
				{
					id : 'PTP_TYPE',
					header : '端口类型',
					dataIndex : 'PTP_TYPE'
				},
				{
					id : 'PM_DESCRIPTION',
					header : '性能事件',
					dataIndex : 'PM_DESCRIPTION'
				}, {
					id : 'GRANULARITY',
					header : '监测周期',
					dataIndex : 'GRANULARITY',
					renderer : function(v, m, r) {
						return granularty[v >> 0];
					}
				}, {
					id : 'PM_LOCATION',
					header : '位置',
					dataIndex : 'PM_LOCATION'
				}, {
					id : 'VALUE',
					header : '性能值',
					dataIndex : 'VALUE'
				}, {
					id : 'THRESHOLD_TYPE',
					header : '门限类型',
					dataIndex : 'THRESHOLD_TYPE',
					renderer : function(v, m, r) {
						return thrType[v >> 0];
					}
				}, {
					id : 'ARISES_TIME',
					header : '发生时间',
					dataIndex : 'ARISES_TIME',
					renderer : function(value) {
						return !!value && !!value.time ? (Ext.util.Format
								.dateRenderer('Y-m-d H:i:s'))(new Date(
								value.time)) : "";
					}
				}, {
					id : 'CLEAR_TIME',
					header : '清除时间',
					dataIndex : 'CLEAR_TIME',
					renderer : function(value) {
						return !!value.time ? (Ext.util.Format
								.dateRenderer('Y-m-d H:i:s'))(new Date(
								value.time)) : "";
					}
				}, {
					id : 'SAVE_TIME',
					header : 'FTSP入库时间',
					dataIndex : 'SAVE_TIME',
					renderer : function(value) {
						return !!value.time ? (Ext.util.Format
								.dateRenderer('Y-m-d H:i:s'))(new Date(
								value.time)) : "";
					}
				}]
	});
	var cmbDomain = new Ext.form.ComboBox({
	    typeAhead: true,
	    triggerAction: 'all',
//	    lazyRender:true,
	    mode: 'local',
	    width:120,
	    store: new Ext.data.ArrayStore({
	        id: 0,
	        fields: [
	            'id',
	            'displayText'
	        ],
	        //"全部","未知","无业务","一般业务","大客户业务","重保业务"
	        data: [[0, '未知'], [1, '无业务'], [2, '一般业务'], [3, '大客户业务'], [4, '重保业务'], [9, '全部']]
	    }),
	    value:9,
	    valueField: 'id',
	    displayField: 'displayText',
		listeners : {
			'select' : function(cmb) {
//				console.log(" - select - ");
//				console.log(cmb.getValue());
				if(cmb.getValue() != 9)
					cmb.getStore().filter("TRAFFIC_FAULT", cmb.getValue());
				else
					cmb.getStore().clearFilter();
			}
		}
	});

	gridPanel = new Ext.grid.EditorGridPanel({
		id : "gridPanel",
		region : "center",
		autoScroll : true,
//		title : '用户管理',
		cm : cm,
		border : false,
		store : dataStore,
		stripeRows : true, // 交替行效果
		loadMask : true,
		selModel : selModel, // 必须加不然不能选checkbox
		forceFit : true,
		frame : false,
		tbar : [cmbDomain,{
			xtype:"button",
			text:"相关电路",
			icon : '../../../resource/images/btnImages/associate.png',
			handler:function(){
				var records = gridPanel.getSelectionModel().getSelections();
				if(records.length==1){
					var i=0;
					var serviceType;
					var nodeLevel;
					var nodes;
					var domain = records[0].get('DOMAIN');
					if(domain==1){
						serviceType=1;
					}else if(domain==2){
						serviceType=3;
					}else if(domain==3){
						serviceType=2;
					}else{
						Ext.Msg.alert('提示', '无相关电路信息');
						return false;
					}
					var objType =records[0].get('OBJECT_TYPE');
					if(objType==4){//网元
						nodeLevel=1;
						nodes=records[0].get('BASE_NE_ID');
					}else if(objType==7){//PTP
						nodeLevel=8;
						nodes=records[0].get('BASE_PTP_ID');
					}else if(objType==8){//CTP
						nodeLevel=9;
						nodes=records[0].get('BASE_SDH_CTP_ID');
					}else if(objType==9){//CTP
						nodeLevel=9;
						nodes=records[0].get('BASE_OTN_CTP_ID');
					}else{
						Ext.Msg.alert('提示', '无相关电路信息');
						return false;
					}
					if(nodes!=''){
						parent.parent.addTabPage('../../jsp/circuitManager/selectCircuitPortReasult.jsp?serviceType='+serviceType+'&nodeLevel='+nodeLevel+'&nodes='+nodes+'&flag=7', "相关性查询",authSequence );  		
					}
				}else if(records.length<1){
					Ext.Msg.alert('提示', '请选择一条记录！');
				}else{
					Ext.Msg.alert('提示', '请勿多选！');
				}
				
			}
		},{
			xtype:"button",
			text:"趋势图",
			icon : '../../../resource/images/btnImages/chart.png',
			handler:function(){
				Ext.Msg.alert('提示', '待定！');
			}
		},{
			xtype:"button",
			text:"导出",
			icon : '../../../resource/images/btnImages/export.png',
			handler:function(){
//				var hiddenColoumms=getGridHiddenColomn();
				var params = dataStore.baseParams;
				params.limit = 0;
				window.location.href = "fault!exportPmExceedData.action?" + Ext.urlEncode(params);
			}
		}]
	});
})();
Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			Ext.Ajax.timeout = 90000000;
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			// Ext.Msg = top.Ext.Msg;

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ westPanel, gridPanel ],
				renderTo : Ext.getBody()
			});
			win.show();
		});