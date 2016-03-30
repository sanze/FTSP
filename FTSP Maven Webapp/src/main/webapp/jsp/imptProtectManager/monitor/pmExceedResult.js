var gridPanel;
var dataStore;
var param = {};
function loadData(params){
//	console.dir(params);
	dataStore.baseParams.jsonString = Ext.encode(params);
	dataStore.load();
}
function getTrendURL(type, record) {
	var url = '../../jsp/performanceManager/PMsearch/performanceDiagram.jsp?';
	var pmStdIndex = record.get('PM_STD_INDEX');
	var emsConnectionId = record.get('BASE_EMS_CONNECTION_ID');
	var targetType = record.get('TARGET_TYPE');
	var starttime = record.get('ARISES_TIME') ? (Ext.util.Format.dateRenderer('Y-m-d'))
			(new Date(record.get('ARISES_TIME').time)) : "";
	var id;
	if (targetType == 6)
		id = 'unitId=' + record.get('BASE_UNIT_ID');
	if (targetType == 7)
		id = 'ptpId=' + record.get('BASE_PTP_ID');
	if (targetType == 8)
		id = 'ctpId=' + record.get('BASE_SDH_CTP_ID');
	if (targetType == 9)
		id = 'ctpId=' + record.get('BASE_OTN_CTP_ID');
	url = url + id + '&pmStdIndex=' + pmStdIndex + '&emsConnectionId=' + emsConnectionId + '&type='
			+ type + '&starttime=' + starttime + '&targetType=' + targetType;
	return url;
}
(function(){
	dataStore = new Ext.data.Store({
		url : 'impt-protect-task!getPmExceedData.action',
		baseParams : {
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
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var domainNames = [ "", "设备保护", "环保护", "ATM保护", "RPR保护", "WDM保护" ];
	var protGrpTypeNames = [ "1+1 MSP", "1:N MSP", "2F BLSR", "4F BLSR",
			"1+1 ATM", "1:N ATM" ];
	var affectedBusiness = [ "全部","未知","无业务","一般业务","大客户业务","重保业务"];
	var severity = ["INDETERMINATE", "CRITICAL", "MAJOR", "MINOR", "WARNING", "CLEARED"];
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
                    hidden : true,
					dataIndex : 'ARISES_TIME',
					renderer : function(value) {
						return !!value && !!value.time ? (Ext.util.Format
								.dateRenderer('Y-m-d H:i:s'))(new Date(
								value.time)) : "";
					}
				}, {
					id : 'CLEAR_TIME',
					header : '清除时间',
                    hidden : true,
					dataIndex : 'CLEAR_TIME',
					renderer : function(value) {
						return !!value.time ? (Ext.util.Format
								.dateRenderer('Y-m-d H:i:s'))(new Date(
								value.time)) : "";
					}
				}, {
					id : 'SAVE_TIME',
					header : 'FTSP入库时间',
                    hidden : true,
					dataIndex : 'SAVE_TIME',
					renderer : function(value) {
						return !!value.time ? (Ext.util.Format
								.dateRenderer('Y-m-d H:i:s'))(new Date(
								value.time)) : "";
					}
				}]
	});
	gridPanel = new Ext.grid.GridPanel({
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
		tbar : [{
			xtype:"button",
			text:"所有越限",
			icon : '../../../resource/images/btnImages/associate.png',
			handler:function(){
				delete(param.start);
				delete(param.end);
				loadData(param);
			}
		}, {
			xtype:"button",
			text:"刷新",
			icon : '../../../resource/images/btnImages/refresh.png',
			handler:function(){
				loadData(param);
			}
		}, {
			xtype:"button",
			text:"趋势图",
			icon : '../../../resource/images/btnImages/chart.png',
			handler:function(){
				var cell = gridPanel.getSelectionModel().getSelections();
				if (cell.length == 1) {
					var url = getTrendURL(2, cell[0]);
					top.addTabPage(url, "性能趋势图",authSequence);
				} else {
					Ext.Msg.alert('信息', '请选择记录，每次只能选择一条！');
				}
			}
		}, {
			xtype:"button",
			text:"导出",
			icon : '../../../resource/images/btnImages/export.png',
			handler:function(){
//				var hiddenColoumms=getGridHiddenColomn();
				var params = dataStore.baseParams;
				params.limit = 0;
				window.location.href = "impt-protect-task!exportPmExceedData.action?" + Ext.urlEncode(params);
			}
		}]
	});
})();
/**
 * 设备任务：任务内网元所有的性能越限；
 * 电路任务：电路经过板卡的板卡级性能、经过的端口的性能
 * 		经过的板卡的板卡级告警:UNIT ID=经过的板卡ID，and OBJECT_TYPE=设备
 * 		经过的端口的告警：PTP ID=经过的端口ID
 */
function storeLoader(){
	if (parent.taskType == '设备'){
		param.ne = "(" + parent.TASK_PARAM.TASK_NE_LIST.join(",") + ")";
	}else{
		var unitList = [];
		for(var i=0;i<parent.TASK_PARAM.TASK_PTP_INFO_LIST.length;i++){
			unitList.push(parent.TASK_PARAM.TASK_PTP_INFO_LIST[i].BASE_UNIT_ID);
		}
		param.unit = "(" + unitList.join(",") + ")";
//		parent.TASK_PARAM.TASK_PTP_LIST.push(89290);
//		parent.TASK_PARAM.TASK_PTP_LIST.push(218815);
		param.ptp = "(" + parent.TASK_PARAM.TASK_PTP_LIST.join(",") + ")";
	}
	//当前时间
	var d = new Date();
	param.end = d.format("yyyy-MM-dd hh:mm:ss");
	//24h之前
	var pre = new Date(d - 24*60*60*1000);
	param.start = pre.format("yyyy-MM-dd hh:mm:ss");
	loadData(param);
//	console.dir(param);
}
Ext.onReady(function() {
        Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
        Ext.Ajax.timeout = 90000000;
        document.onmousedown = function() {
            top.Ext.menu.MenuMgr.hideAll();
        };
        // Ext.Msg = top.Ext.Msg;

        var win = new Ext.Viewport({
            id : 'win',
            layout : 'fit',
            items : [gridPanel],
            renderTo : Ext.getBody()
        });
        win.show();
        storeLoader();
    });