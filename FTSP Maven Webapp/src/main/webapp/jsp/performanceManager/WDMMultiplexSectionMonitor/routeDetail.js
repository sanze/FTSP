var emsId = "-3";
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 1,
	 "limit":400
};
jsonString.push(map);
var forwardStore = new Ext.data.Store({
			url : 'multiple-section!selectMultiplePtpRoute.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["STATION_NAME", "AREA_NAME", "PM_MULTI_SEC_PTP_ID",
							"BASE_EMS_CONNECTION_ID", "MULTI_SEC_ID",
							"EQUIP_NAME", "PTP_ID", "SUB_PTP_ID", "PTP_NAME",
							"SUB_PTP_NAME", "CALCULATE_POINT",
							"SUB_CALCULATE_POINT", "NOTE", "SUB_NOTE",
							"PM_TYPE", "PORT_TYPE", "ROUTE_TYPE",
							"HISTORY_PM_VALUE", "SUB_HISTORY_PM_VALUE",
							"CUT_PM_VALUE", "SUB_CUT_PM_VALUE",
							"CURRENT_PM_VALUE", "SUB_CURRENT_PM_VALUE",
							"HISTORY_PM_TIME", "CUT_PM_TIME",
							"CURRENT_PM_TIME", "SUB_PM_TYPE", "SUB_ROUTE_TYPE",
							"IS_VIRTUAL", "SUB_IS_VIRTUAL", "MODEL",
							"SUB_MODEL","PM_BEFORE_CUTOVER","PM_AFTER_CUTOVER","PM_DIFF_CUTOVER",
							"PM_BEFORE_CUTOVER_SUB","PM_AFTER_CUTOVER_SUB","PM_DIFF_CUTOVER_SUB"])
		});
forwardStore.load();
// ************************* 任务信息列模型 ****************************
var forwardCheckboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : true,
	header : ""
});
var forwardCm = new Ext.ux.grid.LockingColumnModel({
			// specify any defaults for each column
			defaults : {
				sortable : true
				// columns are not sortable by default
			},
			columns : [forwardCheckboxSelectionModel, {
						id : 'PM_MULTI_SEC_PTP_ID',
						header : 'PM_MULTI_SEC_PTP_ID',
						dataIndex : 'PM_MULTI_SEC_PTP_ID',
						locked : false,
						hidden : true
					}, {
						id : 'MULTI_SEC_ID',
						header : 'MULTI_SEC_ID',
						dataIndex : 'MULTI_SEC_ID',
						locked : false,
						hidden : true
					}, {
						id : 'AREA_NAME',
						header : top.FieldNameDefine.AREA_NAME,
						dataIndex : 'AREA_NAME',
						locked : false,
						width : 100
					}, {
						id : 'STATION_NAME',
						header : top.FieldNameDefine.STATION_NAME,
						locked : false,
						dataIndex : 'STATION_NAME'
					}, {
						id : 'EQUIP_NAME',
						header : '网元/光缆',
						locked : false,
						width : 100,
						dataIndex : 'EQUIP_NAME'
					}, {
						id : 'PTP_NAME',
						header : '端口(主)',
						locked : false,
						width : 100,
						dataIndex : 'PTP_NAME'
					}, {
						id : 'MODEL',
						header : '光放型号(主)',
						locked : false,
						width : 100,
						dataIndex : 'MODEL'
					}, {
						id : 'PM_TYPE',
						header : '性能项(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_TYPE',
						renderer : function(value, cellmeta, record) {
							if (value == "1") {
								return "输入光功率(dBm)";
							} else if (value == "2") {
								return "输出光功率(dBm)";
							} else if (value == "3") {
								return "衰耗值(dB)";
							} else if (value == "4") {
								return "段衰耗(dB)";
							}else {
								return "";
							}
						}
					}, {
						id : 'CALCULATE_POINT',
						header : '计算理论值(主)',
						locked : false,
						width : 100,
						dataIndex : 'CALCULATE_POINT'
					}, {
						id : 'CUT_PM_VALUE',
						header : '工程基准值(主)',
						locked : false,
						width : 100,
						dataIndex : 'CUT_PM_VALUE'
					}, {
						id : 'HISTORY_PM_VALUE',
						header : '历史值(主)',
						locked : false,
						width : 100,
						dataIndex : 'HISTORY_PM_VALUE'
					}, {
						id : 'PM_BEFORE_CUTOVER',
						header : '割接前值(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_BEFORE_CUTOVER'
					}, {
						id : 'PM_AFTER_CUTOVER',
						header : '割接后值(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_AFTER_CUTOVER'
					}, {
						id : 'PM_DIFF_CUTOVER',
						header : '割接差值(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_DIFF_CUTOVER'
					}, {
						id : 'SUB_PTP_NAME',
						header : '端口(备)',
						locked : false,
						width : 100,
						dataIndex : 'SUB_PTP_NAME'
					}, {
						id : 'SUB_MODEL',
						header : '光放型号(备)',
						locked : false,
						width : 100,
						dataIndex : 'SUB_MODEL'
					}, {
						id : 'SUB_PM_TYPE',
						header : '性能项(备)',
						locked : false,
						width : 100,
						dataIndex : 'SUB_PM_TYPE',
						renderer : function(value, cellmeta, record) {
							if (value == "1") {
								return "输入光功率(dBm)";
							} else if (value == "2") {
								return "输出光功率(dBm)";
							} else if (value == "3") {
								return "衰耗值(dB)";
							} else if (value == "4") {
								return "段衰耗(dB)";
							}else {
								return "";
							}
						}
					}, {
						id : 'SUB_CALCULATE_POINT',
						header : '计算理论值(备)',
						width : 100,
						locked : false,
						dataIndex : 'SUB_CALCULATE_POINT'
					}, {
						id : 'SUB_CUT_PM_VALUE',
						header : '工程基准值(备)',
						width : 100,
						locked : false,
						dataIndex : 'SUB_CUT_PM_VALUE'
					}, {
						id : 'SUB_HISTORY_PM_VALUE',
						header : '历史值(备)',
						locked : false,
						width : 100,
						dataIndex : 'SUB_HISTORY_PM_VALUE'
					}, {
						id : 'PM_BEFORE_CUTOVER_SUB',
						header : '割接前值(备)',
						width : 100,
						locked : false,
						dataIndex : 'PM_BEFORE_CUTOVER_SUB'
					}, {
						id : 'PM_AFTER_CUTOVER_SUB',
						header : '割接后值(备)',
						width : 100,
						locked : false,
						dataIndex : 'PM_AFTER_CUTOVER_SUB'
					}, {
						id : 'PM_DIFF_CUTOVER_SUB',
						header : '割接差值(备)',
						width : 100,
						locked : false,
						dataIndex : 'PM_DIFF_CUTOVER_SUB'
					}]
		});

var forwardPageTool = new Ext.PagingToolbar({
			id : 'forwardPageTool',
			pageSize : 400,// 每页显示的记录值
			store : forwardStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var forwardPanel = new Ext.grid.EditorGridPanel({
	id : "forwardPanel",
	region : "north",
	cm : forwardCm,
	store : forwardStore,
	stripeRows : true, // 交替行效果
	loadMask : true,
//	selModel : forwardCheckboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	bbar : forwardPageTool
});




var infoPanel = new Ext.FormPanel({
			id : 'infoPanel',
			name : 'infoPanel',
			region : 'north',
			bodyStyle : 'padding:30px 10px 10px',
			height:120,
			border : false,
			items : [{
						layout : 'column',
						border : false,
						items : [{
									columnWidth : .2, // 第一列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'TRUNK_NAME',
												name : 'TRUNK_NAME',
												fieldLabel : '干线名称',
												maxLength : 64,
												allowBlank : true,
												disabled : true,
												anchor : '95%'
											}]
								}, {
									columnWidth : .2, // 第二列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'sec_name',
												name : 'sec_name',
												fieldLabel : '复用段名称',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												anchor : '95%'
											}]
								}, {
									columnWidth : .2, // 第三列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'direction',
												name : 'direction',
												fieldLabel : '方向',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												anchor : '95%'
											}]
								}, {
									columnWidth : .2, // 第四列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'numberfield',
												id : 'std_wave',
												name : 'std_wave',
												fieldLabel : '标称波道数',
												maxLength : 64,
												allowBlank : true,
												disabled : true,
												anchor : '95%'
											}]
								}, {
									columnWidth : .2, // 第五列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'numberfield',
												id : 'actully_wave',
												name : 'actully_wave',
												fieldLabel : '实际波道数',
												maxLength : 64,
												allowBlank : true,
												disabled : true,
												anchor : '95%'
											}]
								}]

					}, {

						layout : 'column',
						border : false,
						items : [{
									columnWidth : .2, // 第一列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'history_time',
												name : 'history_time',
												fieldLabel : '历史值日期',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												anchor : '95%'
											}]
								}, {
									columnWidth : .2, // 第二列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'refresh_before',
												name : 'refresh_before',
												fieldLabel : '割接前值时间',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												anchor : '95%'
											}]
								}, {
									columnWidth : .2, // 第三列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'refresh_after',
												name : 'refresh_after',
												fieldLabel : '割接后时间',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												anchor : '95%'
											}]
								}, {
									columnWidth : .2, // 第四列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'cutover_status',
												name : 'cutover_status',
												fieldLabel : '割接状态',
												maxLength : 64,
												allowBlank : true,
												disabled : true,
												anchor : '95%'
											}]
								}]

					}],
			bbar : ['历史值:', {
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
											dateFmt : 'yyyy-MM-dd',
											autoPickDate : true,
											maxDate : '%y-%M-%d'
										});
								this.blur();
							}
						}
					}, {
						xtype : 'button',
						id : 'resetStartTime',
						name : 'resetStartTime',
						text : '清空',
						width : 40,
						handler : function() {
							Ext.getCmp('startTime').setValue("");
						}
					}, {
						text : '查询',
						icon : '../../../resource/images/btnImages/search.png',
						handler : function() {
							select();
						}

					}, {
						text : '割接前值刷新',
						icon : '../../../resource/images/buttonImages/up.png',
						handler : function() {
							sycPmCurrent(1);

						}

					}, {
						text : '割接后值刷新',
						icon : '../../../resource/images/buttonImages/up.png',
						handler : function() {
							sycPmCurrent(2);

						}

					}, {
						text : '页面刷新',
						icon : '../../../resource/images/buttonImages/up.png',
						handler : function() {
							forwardStore.load();
							oppositeStore.load();
							init();
							
						}

					}, {
						text : '导出',
						icon : '../../../resource/images/btnImages/export.png',
						handler : function() {
							secExport();
						}

					}]

		});
	
/**
 *  导出光复用段路由详情
 */
function secExport(){
	var url='multiple-section!ecportSecDetail.action';
	var jsonData = {
		"MULTI_SEC_ID":mul_id
	};
	//拼一个filename出来
	var MSName = curTabId.match(/\((.*)\)/)[1];
	var direc = direction == 1 ? "单向" : "双向";
	var nowdate = new Date();
	var fileName = MSName + "\(" + direc + "\)" + "割接数据_" + nowdate.getFullYear()+"-"
			+ (nowdate.getMonth()+1)+"-" + nowdate.getDate() +"_"+ nowdate.getHours()
			+ nowdate.getMinutes() + nowdate.getSeconds();
	MSName=null,direc=null,nowdate=null;
	//好了
	top.Ext.getBody().mask('正在导出到Excel，请稍候...');
   	Ext.Ajax.request({
	    url: url,
	    method : 'POST',
	    params: {
	    	"jsonString":Ext.encode(jsonData),
	    	"limit":-1,
	    	"cutoverFlag":1,
	    	"filename":fileName
	    },
	    success: function(response) {
            top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
	    	
	    	if(obj.returnResult == 1){
		    	window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
            }
        	if(obj.returnResult == 0){
        		Ext.Msg.alert("提示","导出数据失败！");
        	}
	    },
	    error:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	});
	
}
function sycPmCurrent(cutoverFlag) {
	var jsonString = new Array();
	var map = {
		"PM_MULTI_SEC_ID" : mul_id
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString),
		"cutoverFlag":cutoverFlag
	};
	Ext.getBody().mask('同步中...');
	Ext.Ajax.request({
		url : 'multiple-section!sycPmByMultiple.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {// 回调函数
			 Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert("信息", obj.returnMessage, function(r) {
							// 刷新列表
							var forwardPageTool = Ext.getCmp('forwardPageTool');
							if (forwardPageTool) {
								forwardPageTool.doLoad(forwardPageTool.cursor);
							}
							var oppositePageTool = Ext
									.getCmp('oppositePageTool');
							if (oppositePageTool) {
								oppositePageTool
										.doLoad(oppositePageTool.cursor);
							}
						});
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
		},
		error : function(response) {
			 Ext.getBody().unmask();
			Ext.Msg.alert('错误', '同步失败！');
		},
		failure : function(response) {
			 Ext.getBody().unmask();
			Ext.Msg.alert('错误', '同步失败！');
		}

	});

}
/** ***********反向tab********************* */
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 2,
	"limit":400
};
jsonString.push(map);
var oppositeStore = new Ext.data.Store({
			url : 'multiple-section!selectMultiplePtpRoute.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["STATION_NAME", "AREA_NAME", "PM_MULTI_SEC_PTP_ID",
							"MULTI_SEC_ID", "EQUIP_NAME", "PTP_ID",
							"SUB_PTP_ID", "PTP_NAME", "SUB_PTP_NAME",
							"CALCULATE_POINT", "SUB_CALCULATE_POINT", "NOTE",
							"SUB_NOTE", "PM_TYPE", "PORT_TYPE", "ROUTE_TYPE",
							"HISTORY_PM_VALUE", "SUB_HISTORY_PM_VALUE",
							"CUT_PM_VALUE", "SUB_CUT_PM_VALUE",
							"CURRENT_PM_VALUE", "SUB_CURRENT_PM_VALUE",
							"HISTORY_PM_TIME", "CUT_PM_TIME",
							"CURRENT_PM_TIME", "SUB_PM_TYPE", "SUB_ROUTE_TYPE",
							"IS_VIRTUAL", "SUB_IS_VIRTUAL", "MODEL",
							"SUB_MODEL","PM_BEFORE_CUTOVER","PM_AFTER_CUTOVER","PM_DIFF_CUTOVER",
							"PM_BEFORE_CUTOVER_SUB","PM_AFTER_CUTOVER_SUB","PM_DIFF_CUTOVER_SUB"])
		});
oppositeStore.load();
// ************************* 任务信息列模型 ****************************
var oppositeCheckboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
			singleSelect : true,
			header : ""
		});
var oppositeCm = new Ext.ux.grid.LockingColumnModel({
			// specify any defaults for each column
			defaults : {
				sortable : true
				// columns are not sortable by default
			},
			columns : [oppositeCheckboxSelectionModel, {
						id : 'PM_MULTI_SEC_PTP_ID',
						header : 'PM_MULTI_SEC_PTP_ID',
						dataIndex : 'PM_MULTI_SEC_PTP_ID',
						hidden : true
					}, {
						id : 'MULTI_SEC_ID',
						header : 'MULTI_SEC_ID',
						dataIndex : 'MULTI_SEC_ID',
						hidden : true
					}, {
						id : 'AREA_NAME',
						header : top.FieldNameDefine.AREA_NAME,
						dataIndex : 'AREA_NAME',
						width : 100
					}, {
						id : 'STATION_NAME',
						header : top.FieldNameDefine.STATION_NAME,
						dataIndex : 'STATION_NAME'
					}, {
						id : 'EQUIP_NAME',
						header : '网元/光缆',
						width : 100,
						dataIndex : 'EQUIP_NAME'
					}, {
						id : 'PTP_NAME',
						header : '端口(主)',
						width : 100,
						dataIndex : 'PTP_NAME'
					}, {
						id : 'MODEL',
						header : '光放型号(主)',
						width : 100,
						dataIndex : 'MODEL'
					}, {
						id : 'PM_TYPE',
						header : '性能项(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_TYPE',
						renderer : function(value, cellmeta, record) {
							if (value == "1") {
								return "输入光功率(dBm)";
							} else if (value == "2") {
								return "输出光功率(dBm)";
							} else if (value == "3") {
								return "衰耗值(dB)";
							} else if (value == "4") {
								return "段衰耗(dB)";
							}else {
								return "";
							}
						}
					}, {
						id : 'CALCULATE_POINT',
						header : '计算理论值(主)',
						width : 100,
						dataIndex : 'CALCULATE_POINT'
					}, {
						id : 'CUT_PM_VALUE',
						header : '工程基准值(主)',
						width : 100,
						dataIndex : 'CUT_PM_VALUE'
					}, {
						id : 'HISTORY_PM_VALUE',
						header : '历史值(主)',
						width : 100,
						dataIndex : 'HISTORY_PM_VALUE'
					}, {
						id : 'PM_BEFORE_CUTOVER',
						header : '割接前值(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_BEFORE_CUTOVER'
					}, {
						id : 'PM_AFTER_CUTOVER',
						header : '割接后值(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_AFTER_CUTOVER'
					}, {
						id : 'PM_DIFF_CUTOVER',
						header : '割接差值(主)',
						width : 100,
						locked : false,
						dataIndex : 'PM_DIFF_CUTOVER'
					}, {
						id : 'SUB_PTP_NAME',
						header : '端口(备)',
						width : 100,
						dataIndex : 'SUB_PTP_NAME'
					}, {
						id : 'SUB_MODEL',
						header : '光放型号(备)',
						width : 100,
						dataIndex : 'SUB_MODEL'
					}, {
						id : 'SUB_PM_TYPE',
						header : '性能项(备)',
						width : 100,
						locked : false,
						dataIndex : 'SUB_PM_TYPE',
						renderer : function(value, cellmeta, record) {
							
							if (value == "1") {
								return "输入光功率(dBm)";
							} else if (value == "2") {
								return "输出光功率(dBm)";
							} else if (value == "3") {
								return "衰耗值(dB)";
							} else if (value == "4") {
								return "段衰耗(dB)";
							}else {
								return "";
							}
						}
					}, {
						id : 'SUB_CALCULATE_POINT',
						header : '计算理论值(备)',
						width : 100,
						dataIndex : 'SUB_CALCULATE_POINT'
					}, {
						id : 'SUB_CUT_PM_VALUE',
						header : '工程基准值(备)',
						width : 100,
						dataIndex : 'SUB_CUT_PM_VALUE'
					}, {
						id : 'SUB_HISTORY_PM_VALUE',
						header : '历史值(备)',
						width : 100,
						dataIndex : 'SUB_HISTORY_PM_VALUE'
					}, {
						id : 'PM_BEFORE_CUTOVER_SUB',
						header : '割接前值(备)',
						width : 100,
						locked : false,
						dataIndex : 'PM_BEFORE_CUTOVER_SUB'
					}, {
						id : 'PM_AFTER_CUTOVER_SUB',
						header : '割接后值(备)',
						width : 100,
						locked : false,
						dataIndex : 'PM_AFTER_CUTOVER_SUB'
					}, {
						id : 'PM_DIFF_CUTOVER_SUB',
						header : '割接差值(备)',
						width : 100,
						locked : false,
						dataIndex : 'PM_DIFF_CUTOVER_SUB'
					}]
		});

var oppositePageTool = new Ext.PagingToolbar({
			id : 'oppositePageTool',
			pageSize : 400,// 每页显示的记录值
			store : oppositeStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var oppositePanel = new Ext.grid.EditorGridPanel({
	id : "oppositePanel",
	region : "north",
	cm : oppositeCm,
	store : oppositeStore,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : oppositeCheckboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	bbar : oppositePageTool
});

var tab = new Ext.TabPanel({
			id : 'tabs1',
			anchor : "right 70%",
			region : "center",
			activeTab : 0,
			// plugins: new Ext.ux.TabCloseMenu(),
			items : [{
						title : '正向',
						layout : 'fit',
						id:'forward',
						// anchor : "right 100%",
						items : [forwardPanel]
					}, {
						title : '反向',
						layout : 'fit',
						id:'opposite',
						// anchor : "right 100%",
						items : [oppositePanel]
					}]
		})
var formPanel = new Ext.Panel({
			id : 'formPanel',
			region : "center",
			border : false,
			frame : false,
			// anchor : "right 100%",
			autoScroll : true,
			// labelWidth : 120,
			layout : 'border',
			// width : 200,
			bodyStyle : 'padding:10px 10px 0;',
			items : [infoPanel, tab]

		});

/**
 * 查询历史性能，刷新历史性能时间
 */
function select() {
	var time = Ext.getCmp("startTime").getValue();
	if (time.length < 1) {
		Ext.Msg.alert('提示', '请选择历史性能查询时间！');
		return;
	}

	var jsonString = new Array();

	var map = {
		"startTime" : time,
		"BASE_EMS_CONNECTION_ID" : emsId,
		"PM_MULTI_SEC_ID" : mul_id
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
		url : 'multiple-section!sycPmHistory.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {// 回调函数
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert("信息", obj.returnMessage, function(r) {
							// 刷新列表
							var forwardPageTool = Ext.getCmp('forwardPageTool');
							if (forwardPageTool) {
								forwardPageTool.doLoad(forwardPageTool.cursor);
							}

							var oppositePageTool = Ext
									.getCmp('oppositePageTool');
							if (oppositePageTool) {
								oppositePageTool
										.doLoad(oppositePageTool.cursor);
							}
							init();
						});
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
		},
		error : function(response) {
			Ext.Msg.alert('错误', '同步失败！');
		},
		failure : function(response) {
			Ext.Msg.alert('错误', '同步失败！');
		}

	});

}



/**
 * 初始化语句
 */
function init() {

	var jsonString = new Array();
	var map = {
		"MULTI_SEC_ID" : mul_id
	};
	jsonString.push(map);
	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
				url : 'multiple-section!selectMultipleAbout.action',
				type : 'post',
				params : jsonData,
				success : function(response) {
					var obj = Ext.decode(response.responseText);

					Ext.getCmp('TRUNK_NAME').setValue(obj.DISPLAY_NAME);
					Ext.getCmp('sec_name').setValue(obj.SEC_NAME);
					if (obj.DIRECTION == "2") {
						Ext.getCmp('direction').setValue("双向");
					} else {
						Ext.getCmp('direction').setValue("单向");
					}

					Ext.getCmp('std_wave').setValue(obj.STD_WAVE);
					Ext.getCmp('actully_wave').setValue(obj.ACTULLY_WAVE);
					Ext.getCmp('history_time').setValue(obj.PM_HISTORY_TIME);
					Ext.getCmp('refresh_before').setValue(obj.REFRESH_BEFORE);				
					Ext.getCmp('refresh_after').setValue(obj.REFRESH_AFTER);
					if (obj.SEC_STATE_CUTOVER == 3) {
						Ext.getCmp('cutover_status').setValue('重要告警');	
					} else if (obj.SEC_STATE_CUTOVER == 2) {
						Ext.getCmp('cutover_status').setValue('次要告警');	
					} else if (obj.SEC_STATE_CUTOVER == 1) {
						Ext.getCmp('cutover_status').setValue('一般告警');	
					} else if (obj.SEC_STATE_CUTOVER == 0) {
						Ext.getCmp('cutover_status').setValue('正常');	
					} else {
						Ext.getCmp('cutover_status').setValue('割接准备');	
					}
					emsId = obj.BASE_EMS_CONNECTION_ID;
				},
				error : function(response) {
					Ext.Msg.alert("异常", response.responseText);
				},
				failure : function(response) {
					Ext.Msg.alert("异常", response.responseText);
				}
			});
	// 将反向tab灰掉
	if(direction ==1){
		Ext.getCmp("opposite").setDisabled(true);
	}

}

parent.getTab(curTabId).refreshImpl= function refreshImpl(){
//	alert();
	init();
	forwardStore.load();
	oppositeStore.load();
};

Ext.onReady(function() {
	Ext.Msg = parent.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;
	Ext.QuickTips.init(); // 开启悬停提示
	Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
	var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
	init();

});