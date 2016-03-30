var emsId = "-3";
var isSaveF = false;
var isSaveO = false;
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 1,
	"limit" : 400
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
							"SUB_MODEL","FACTORY"])
		});
forwardStore.load();
// ************************* 任务信息列模型 ****************************
var forwardCheckboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
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
						header : '理论值(主)',
						locked : false,
						width : 100,
						dataIndex : 'CALCULATE_POINT'
					}, {
						id : 'CUT_PM_VALUE',
						header : '基准值(主)',
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
						id : 'CURRENT_PM_VALUE',
						header : '当前值(主)',
						locked : false,
						width : 100,
						dataIndex : 'CURRENT_PM_VALUE'
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
						header : '理论值(备)',
						width : 100,
						locked : false,
						dataIndex : 'SUB_CALCULATE_POINT'
					}, {
						id : 'SUB_CUT_PM_VALUE',
						header : '基准值(备)',
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
						id : 'SUB_CURRENT_PM_VALUE',
						header : '当前值(备)',
						width : 100,
						locked : false,
						dataIndex : 'SUB_CURRENT_PM_VALUE'
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
	selModel : forwardCheckboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	bbar : forwardPageTool,
	tbar : {
		xtype : "container",
		border : false,
		items : [{
			// tbar第二行工具栏
			xtype : "toolbar",
			items : ['-',{
						text : '更新基准值',
						// privilege:actionAuth,
						icon : '../../../resource/images/btnImages/set_baseline.png',
						handler : function() {
							updateForward(1);
						}

					}, '-',{
						text : '性能趋势图',
						// privilege:viewAuth,
						icon : '../../../resource/images/btnImages/chart.png',
						handler : function() {
							newForwardPicture();

						}

					},'-', {
						text : '保存',
						// privilege:modAuth,
						icon : '../../../resource/images/btnImages/disk.png',
						handler : function() {
							saveForward();
						}

					}]
		}]

	}
});

/**
 * 查看性能趋势图
 */
function newForwardPicture() {
	var cell = forwardPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
		if (cell[0].get('ROUTE_TYPE') == 1
				|| cell[0].get('SUB_ROUTE_TYPE') == 1) {
			if (cell[0].get('ROUTE_TYPE') == 1) {
				var url = getDiagramURL(1, cell[0]);
				parent.parent.addTabPage(url, "主端口性能趋势图");

			}

			if (cell[0].get('SUB_ROUTE_TYPE') == 1) {
				setTimeout('newForwardPictureLater()', 1000);
			}

		} else {
			Ext.Msg.alert('信息', '请选择主备用至少有一条为端口的记录进行查询！');
			return;
		}

	} else {
		Ext.Msg.alert('信息', '请选择记录，每次只能选择一条！');
		return;
	}

}

function newForwardPictureLater() {

	var cell = forwardPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
		if (cell[0].get('SUB_ROUTE_TYPE') == 1) {
			var url = getDiagramSubURL(1, cell[0]);
			parent.parent.addTabPage(url, "备端口性能趋势图");
		}

	}

}

function getDiagramURL(type, record) {
	var url = '../../jsp/performanceManager/multipleSection/performanceDiagram.jsp?';
	var pmStdIndex = "";
	if (record.get('PM_TYPE') == 1) {
		pmStdIndex = "RPL_CUR";
	} else if (record.get('PM_TYPE') == 2) {
		pmStdIndex = "TPL_CUR";
	}
	// 如果是中兴网管，则取平均值
	if(record.get('FACTORY') == 2){
		if (record.get('PM_TYPE') == 1) {
			pmStdIndex = "RPL_AVG";
		} else if (record.get('PM_TYPE') == 2) {
			pmStdIndex = "TPL_AVG";
		}
	}
	var emsConnectionId = record.get('BASE_EMS_CONNECTION_ID');
	var id = 'ptpId=' + record.get('PTP_ID');
	var name = record.get('EQUIP_NAME') + ":" + record.get('PTP_NAME') + ":"
			+ pmStdIndex
	url = url + id + '&pmStdIndex=' + pmStdIndex + '&emsConnectionId='
			+ emsConnectionId + "&type=" + type + "&name=" + name + "&num=1";
	return url;
}

function getDiagramSubURL(type, record) {
	var url = '../../jsp/performanceManager/multipleSection/performanceDiagram.jsp?';
	var pmStdIndex = "";
	if (record.get('SUB_PM_TYPE') == 1) {
		pmStdIndex = "RPL_CUR";
	} else if (record.get('SUB_PM_TYPE') == 2) {
		pmStdIndex = "TPL_CUR";
	}
		// 如果是中兴网管，则取平均值
	if(record.get('FACTORY') == 2){
		if (record.get('SUB_PM_TYPE') == 1) {
			pmStdIndex = "RPL_AVG";
		} else if (record.get('SUB_PM_TYPE') == 2) {
			pmStdIndex = "TPL_AVG";
		}
	}
	var emsConnectionId = record.get('BASE_EMS_CONNECTION_ID');
	var id = 'ptpId=' + record.get('SUB_PTP_ID');
	var name = record.get('EQUIP_NAME') + ":" + record.get('SUB_PTP_NAME')
			+ ":" + pmStdIndex
	url = url + id + '&pmStdIndex=' + pmStdIndex + '&emsConnectionId='
			+ emsConnectionId + "&type=" + type + "&name=" + name + "&num=1";
	return url;
}

var infoPanel = new Ext.FormPanel({
			id : 'infoPanel',
			name : 'infoPanel',
			region : 'north',
			bodyStyle : 'padding:30px 10px 10px',
			height : 120,
			border : false,
			labelAlign : 'right',
			items : [{
						layout : 'hbox',
						border : false,
						items : [{
									 // 第一列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'TRUNK_NAME',
												name : 'TRUNK_NAME',
												fieldLabel : '干线名称&nbsp;&nbsp;&nbsp;',
												maxLength : 64,
												allowBlank : true,
												disabled : true,
												width:230
											}]
								}, {
									 // 第二列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'sec_name',
												name : 'sec_name',
												fieldLabel : '复用段名称&nbsp;&nbsp;&nbsp;',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												width:230
											}]
								}, {
									 // 第一列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									labelWidth:50,
									items : [{
												xtype : 'textfield',
												id : 'direction',
												name : 'direction',
												fieldLabel : '方向',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												width:40
											}]
								}, {
									// 第一列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									labelWidth:80,
									items : [{
												xtype : 'numberfield',
												id : 'std_wave',
												name : 'std_wave',
												fieldLabel : '标称波道数',
												maxLength : 64,
												allowBlank : true,
												disabled : true,
												width:30
											}]
								}, {
									 // 第一列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									labelWidth:80,
									items : [{
												xtype : 'numberfield',
												id : 'actully_wave',
												name : 'actully_wave',
												fieldLabel : '实际波道数',
												maxLength : 64,
												allowBlank : true,
												disabled : true,
												width:30
											}]
								}]

					}, {

						layout : 'hbox',
						border : false,
						items : [{
									 // 第一列
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
												width:230
											}]
								}, {
									// 第二列
									layout : 'form',
									labelSeparator : "：",
									border : false,
									items : [{
												xtype : 'textfield',
												id : 'current_time',
												name : 'current_time',
												fieldLabel : '当前性能时间',
												disabled : true,
												maxLength : 64,
												allowBlank : true,
												width:230
											}]
								}]

					}],
			tbar : ['-','历史值：', {
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
					}, '-',{
						xtype : 'button',
						id : 'resetStartTime',
						name : 'resetStartTime',
						text : '清空',
						icon : '../../../resource/images/btnImages/bin_empty.png',
						width : 40,
						handler : function() {
							Ext.getCmp('startTime').setValue("");
						}
					},'-', {
						text : '查询',
						icon : '../../../resource/images/btnImages/search.png',
						handler : function() {
							select();
						}

					},'-', {
						text : '同步当前性能',
						icon : '../../../resource/images/btnImages/sync.png',
						handler : function() {
							sycPmCurrent();

						}

					},'-', {
						text : '导出',
						icon : '../../../resource/images/btnImages/export.png',
						handler : function() {
							secExport();
						}

					}]

		});

/**
 * 导出光复用段路由详情
 */
function secExport() {
	if (isSaveF || isSaveO) {
		Ext.Msg.confirm("确认", "光复用段数据被修改，是否保存？", function(button) {
					if (button == 'yes') {
						saveForward();
						saveForward();

					}
					isSaveF = false;
					isSaveO = false;
					return;
				});
	} else {
		var url = 'multiple-section!ecportSecDetail.action';
		var jsonData = {
			"MULTI_SEC_ID" : mul_id,
			"SEC_NAME":Ext.getCmp('sec_name').getValue()
		};
		top.Ext.getBody().mask('正在导出到Excel，请稍候...');
		Ext.Ajax.request({
					url : url,
					method : 'POST',
					params : {
						"jsonString" : Ext.encode(jsonData),
						"limit" : -1
					},
					success : function(response) {
						top.Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);

						if (obj.returnResult == 1) {
							window.location.href = "download.action?"
									+ Ext.urlEncode({
												filePath : obj.returnMessage
											});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", "导出数据失败！");
						}
					},
					error : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});
	}

}
function sycPmCurrent() {
	if (isSaveF || isSaveO) {
		Ext.Msg.confirm("确认", "光复用段数据被修改，是否保存？", function(button) {
					if (button == 'yes') {
						saveForward();

					}
					isSaveF = false;
					isSaveO = false;
					return;
				});
	} else {
		// 判断正反是否已选择端口

		var jsonString = new Array();
		var forward = forwardPanel.getSelectionModel().getSelections();
		var opposite = oppositePanel.getSelectionModel().getSelections();
		if (forward.length > 0 || opposite.length > 0) {
			if (forward.length > 0) {
				for (var i = 0; i < forward.length; i++) {
					var map = {
						"PM_MULTI_SEC_ID" : mul_id,
						"PM_MULTI_SEC_PTP_ID" : forward[i]
								.get('PM_MULTI_SEC_PTP_ID'),
						"PTP_ID" : forward[i].get('PTP_ID'),
						"SUB_PTP_ID" : forward[i].get('SUB_PTP_ID'),
						"ROUTE_TYPE" : forward[i].get('ROUTE_TYPE'),
						"SUB_ROUTE_TYPE" : forward[i].get('SUB_ROUTE_TYPE'),
						"PM_TYPE" : forward[i].get('PM_TYPE'),
						"SUB_PM_TYPE" : forward[i].get('SUB_PM_TYPE')
					};
					jsonString.push(map);
				}

			}
			if (opposite.length > 0) {

				for (var i = 0; i < opposite.length; i++) {
					var map = {
						"PM_MULTI_SEC_ID" : mul_id,
						"PM_MULTI_SEC_PTP_ID" : opposite[i]
								.get('PM_MULTI_SEC_PTP_ID'),
						"PTP_ID" : opposite[i].get('PTP_ID'),
						"SUB_PTP_ID" : opposite[i].get('SUB_PTP_ID'),
						"ROUTE_TYPE" : opposite[i].get('ROUTE_TYPE'),
						"SUB_ROUTE_TYPE" : opposite[i].get('SUB_ROUTE_TYPE'),
						"PM_TYPE" : opposite[i].get('PM_TYPE'),
						"SUB_PM_TYPE" : opposite[i].get('SUB_PM_TYPE')
					};
					jsonString.push(map);
				}

			}

		} else {
			for (var i = 0; i < forwardStore.getCount(); i++) {
				var map = {
					"PM_MULTI_SEC_ID" : mul_id,
					"PM_MULTI_SEC_PTP_ID" : forwardStore.getAt(i)
							.get('PM_MULTI_SEC_PTP_ID'),
					"PTP_ID" : forwardStore.getAt(i).get('PTP_ID'),
					"SUB_PTP_ID" : forwardStore.getAt(i).get('SUB_PTP_ID'),
					"ROUTE_TYPE" : forwardStore.getAt(i).get('ROUTE_TYPE'),
					"SUB_ROUTE_TYPE" : forwardStore.getAt(i)
							.get('SUB_ROUTE_TYPE'),
					"PM_TYPE" : forwardStore.getAt(i).get('PM_TYPE'),
					"SUB_PM_TYPE" : forwardStore.getAt(i)
							.get('SUB_PM_TYPE')
				};
				jsonString.push(map);
			}
			for (var i = 0; i < oppositeStore.getCount(); i++) {
				var map = {
					"PM_MULTI_SEC_ID" : mul_id,
					"PM_MULTI_SEC_PTP_ID" : oppositeStore.getAt(i)
							.get('PM_MULTI_SEC_PTP_ID'),
					"PTP_ID" : oppositeStore.getAt(i).get('PTP_ID'),
					"SUB_PTP_ID" : oppositeStore.getAt(i).get('SUB_PTP_ID'),
					"ROUTE_TYPE" : oppositeStore.getAt(i).get('ROUTE_TYPE'),
					"SUB_ROUTE_TYPE" : oppositeStore.getAt(i)
							.get('SUB_ROUTE_TYPE'),
					"PM_TYPE" : oppositeStore.getAt(i).get('PM_TYPE'),
					"SUB_PM_TYPE" : oppositeStore.getAt(i)
							.get('SUB_PM_TYPE')
				};
				jsonString.push(map);
			}
		}

		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		Ext.getBody().mask('同步中...');
		Ext.Ajax.request({
					url : 'multiple-section!sycPmByMultipleByPort.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {// 回调函数
						 Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							Ext.Msg.alert("提示", obj.returnMessage, function(r) {
										init();
										// 刷新列表
										var forwardPageTool = Ext
												.getCmp('forwardPageTool');
										if (forwardPageTool) {
											forwardPageTool
													.doLoad(forwardPageTool.cursor);
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
							Ext.Msg.alert("提示", obj.returnMessage);
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
}
/** ***********反向tab********************* */
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 2,
	"limit" : 400
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
							"SUB_MODEL"])
		});
oppositeStore.load();
// ************************* 任务信息列模型 ****************************
var oppositeCheckboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
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
						header : '理论值(主)',
						width : 100,
						dataIndex : 'CALCULATE_POINT'
					}, {
						id : 'CUT_PM_VALUE',
						header : '基准值(主)',
						width : 100,
						dataIndex : 'CUT_PM_VALUE'
					}, {
						id : 'HISTORY_PM_VALUE',
						header : '历史值(主)',
						width : 100,
						dataIndex : 'HISTORY_PM_VALUE'
					}, {
						id : 'CURRENT_PM_VALUE',
						header : '当前值(主)',
						width : 100,
						dataIndex : 'CURRENT_PM_VALUE'
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
						header : '理论值(备)',
						width : 100,
						dataIndex : 'SUB_CALCULATE_POINT'
					}, {
						id : 'SUB_CUT_PM_VALUE',
						header : '基准值(备)',
						width : 100,
						dataIndex : 'SUB_CUT_PM_VALUE'
					}, {
						id : 'SUB_HISTORY_PM_VALUE',
						header : '历史值(备)',
						width : 100,
						dataIndex : 'SUB_HISTORY_PM_VALUE'
					}, {
						id : 'SUB_CURRENT_PM_VALUE',
						header : '当前值(备)',
						width : 100,
						dataIndex : 'SUB_CURRENT_PM_VALUE'
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
	bbar : oppositePageTool,
	tbar : {
		xtype : "container",
		border : false,
		items : [{
			// tbar第二行工具栏
			xtype : "toolbar",
			items : ['-',{
						text : '更新基准值',
						// privilege:actionAuth,
						icon : '../../../resource/images/btnImages/set_baseline.png',
						handler : function() {
							updateForward(2);
						}

					},'-', {
						text : '性能趋势图',
						// privilege:viewAuth,
						icon : '../../../resource/images/btnImages/chart.png',
						handler : function() {
							newOppositePicture();
							// newForwardPicture();

						}

					}, '-',{
						text : '保存',
						// privilege:modAuth,
						icon : '../../../resource/images/btnImages/disk.png',
						handler : function() {
							saveOpposite();
						}

					}]
		}]

	}
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
						id : 'forward',
						// anchor : "right 100%",
						items : [forwardPanel]
					}, {
						title : '反向',
						layout : 'fit',
						id : 'opposite',
						// anchor : "right 100%",
						items : [oppositePanel]
					}],
			listeners : {
				tabchange : function(tabPanel, panel) {
					if (panel != null) {
						var forwardPageTool = Ext.getCmp('forwardPageTool');
						if (forwardPageTool) {
							forwardPageTool.doLoad(forwardPageTool.cursor);
						}
						var oppositePageTool = Ext.getCmp('oppositePageTool');
						if (oppositePageTool) {
							oppositePageTool.doLoad(oppositePageTool.cursor);
						}
					}
				}
			}
		})
var formPanel = new Ext.Panel({
			id : 'formPanel',
			region : "center",
			border : false,
			frame : false,
			autoScroll : true,
			// labelWidth : 120,
			layout : 'border',
			//width : 200,
			bodyStyle : 'padding:10px 10px 0;',
			items : [infoPanel, tab]

		});

/**
 * 查询历史性能，刷新历史性能时间
 */
function select() {
	if (isSaveF || isSaveO) {
		Ext.Msg.confirm("确认", "光复用段数据被修改，是否保存？", function(button) {
					if (button == 'yes') {
						saveForward();

					}
					isSaveF = false;
					isSaveO = false;
					return;
				});
	} else {
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
		Ext.getBody().mask('查询中...');
		Ext.Ajax.request({
					url : 'multiple-section!sycPmHistory.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {// 回调函数
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							Ext.Msg.alert("提示", obj.returnMessage, function(r) {
										// 刷新列表
										var forwardPageTool = Ext
												.getCmp('forwardPageTool');
										if (forwardPageTool) {
											forwardPageTool
													.doLoad(forwardPageTool.cursor);
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
							Ext.Msg.alert("提示", obj.returnMessage);
						}
					},
					error : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '查询失败！');
					},
					failure : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '查询失败！');
					}

				});
	}
}
/**
 * 更新正向基准值 value （1是正向 2 是反向）在复用段详情页面使用 ,（3 正左，4 正右 5 反左 6 反右） 在复用段设置页面使用
 */
function updateForward(value) {
	// 更新基准值
	var url = "selectValueType.jsp?value=" + value;
	addForwardWindow = new Ext.Window({
				id : 'addForwardWindow',
				title : '更新基准值',
				width : 320,
				height : 200,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
	addForwardWindow.show();
}

/**
 * 
 * @param {}
 *            type 1历史值 2 当前值 3 理论值
 * @param {}
 *            value 1 正向 2 反向
 */
function changeValue(type, value) {
	if (value == "1") {
		isSaveF = true;
		var select = forwardPanel.getSelectionModel().getSelections();
		// 选中了，就赋值选中的记录
		if (select.length > 0) {
			for (var i = 0; i < select.length; i++) {
				forwardStore.each(function(record) {
							if (select[i].get("PM_MULTI_SEC_PTP_ID") == record
									.get("PM_MULTI_SEC_PTP_ID")) {
								if (type == "1") {
									// 历史值
									record.set("CUT_PM_VALUE", record
													.get("HISTORY_PM_VALUE"));
									record.set("SUB_CUT_PM_VALUE",
											record.get("SUB_HISTORY_PM_VALUE"));
								} else if (type == "2") {
									// 当前值
									record.set("CUT_PM_VALUE", record
													.get("CURRENT_PM_VALUE"));
									record.set("SUB_CUT_PM_VALUE",
											record.get("SUB_CURRENT_PM_VALUE"));
								} else if (type == "3") {
									// 理论值
									record.set("CUT_PM_VALUE", record
													.get("CALCULATE_POINT"));
									record
											.set(
													"SUB_CUT_PM_VALUE",
													record
															.get("SUB_CALCULATE_POINT"));
								}
							}
						})
			}

		} else {
			// 如果没选，则赋值全部
			forwardStore.each(function(record) {

						if (type == "1") {
							// 历史值
							record.set("CUT_PM_VALUE", record
											.get("HISTORY_PM_VALUE"));
							record.set("SUB_CUT_PM_VALUE", record
											.get("SUB_HISTORY_PM_VALUE"));
						} else if (type == "2") {
							// 当前值
							record.set("CUT_PM_VALUE", record
											.get("CURRENT_PM_VALUE"));
							record.set("SUB_CUT_PM_VALUE", record
											.get("SUB_CURRENT_PM_VALUE"));
						} else if (type == "3") {
							// 理论值
							record.set("CUT_PM_VALUE", record
											.get("CALCULATE_POINT"));
							record.set("SUB_CUT_PM_VALUE", record
											.get("SUB_CALCULATE_POINT"));
						}

					})
		}
	} else if (value == "2") {
		isSaveO = true;
		var select = oppositePanel.getSelectionModel().getSelections();
		// 选中了，就赋值选中的记录
		if (select.length > 0) {
			for (var i = 0; i < select.length; i++) {
				oppositeStore.each(function(record) {
							if (select[i].get("PM_MULTI_SEC_PTP_ID") == record
									.get("PM_MULTI_SEC_PTP_ID")) {
								if (type == "1") {
									// 历史值
									record.set("CUT_PM_VALUE", record
													.get("HISTORY_PM_VALUE"));
									record.set("SUB_CUT_PM_VALUE",
											record.get("SUB_HISTORY_PM_VALUE"));
								} else if (type == "2") {
									// 当前值
									record.set("CUT_PM_VALUE", record
													.get("CURRENT_PM_VALUE"));
									record.set("SUB_CUT_PM_VALUE",
											record.get("SUB_CURRENT_PM_VALUE"));
								} else if (type == "3") {
									// 理论值
									record.set("CUT_PM_VALUE", record
													.get("CALCULATE_POINT"));
									record
											.set(
													"SUB_CUT_PM_VALUE",
													record
															.get("SUB_CALCULATE_POINT"));
								}
							}
						})
			}

		} else {
			// 如果没选，则赋值全部
			oppositeStore.each(function(record) {

						if (type == "1") {
							// 历史值
							record.set("CUT_PM_VALUE", record
											.get("HISTORY_PM_VALUE"));
							record.set("SUB_CUT_PM_VALUE", record
											.get("SUB_HISTORY_PM_VALUE"));
						} else if (type == "2") {
							// 当前值
							record.set("CUT_PM_VALUE", record
											.get("CURRENT_PM_VALUE"));
							record.set("SUB_CUT_PM_VALUE", record
											.get("SUB_CURRENT_PM_VALUE"));
						} else if (type == "3") {
							// 理论值
							record.set("CUT_PM_VALUE", record
											.get("CALCULATE_POINT"));
							record.set("SUB_CUT_PM_VALUE", record
											.get("SUB_CALCULATE_POINT"));
						}

					})
		}

	}
	Ext.getCmp('addForwardWindow').close();
}

/**
 * 
 */
function saveForward() {
	var cell = forwardStore.getModifiedRecords();
	if (cell.length < 1) {
		Ext.Msg.alert('提示', '没有修改过的记录，无需保存！');
		return;
	} else {
		var jsonString = new Array();
		for (var i = 0; i < cell.length; i++) {
			var map = {
				"PM_MULTI_SEC_PTP_ID" : cell[i].get('PM_MULTI_SEC_PTP_ID'),
				"CUT_PM_VALUE" : cell[i].get('CUT_PM_VALUE'),
				"SUB_CUT_PM_VALUE" : cell[i].get('SUB_CUT_PM_VALUE')
			};
			jsonString.push(map);
		}

		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		Ext.getBody().mask('保存中...');
		Ext.Ajax.request({
					url : 'multiple-section!saveMultipleDetail.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {// 回调函数
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							isSaveF = false;
							Ext.Msg.alert("提示", obj.returnMessage, function(r) {
										// 刷新列表
										var forwardPageTool = Ext
												.getCmp('forwardPageTool');
										if (forwardPageTool) {
											forwardPageTool
													.doLoad(forwardPageTool.cursor);
										}
									});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
					},
					error : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '保存失败！');
					},
					failure : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '保存失败！');
					}

				});

	}
}
/**
 * 查看性能趋势图
 */
function newOppositePicture() {
	var cell = oppositePanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
		if (cell[0].get('ROUTE_TYPE') == 1
				|| cell[0].get('SUB_ROUTE_TYPE') == 1) {
			if (cell[0].get('ROUTE_TYPE') == 1) {
				var url = getDiagramURL(1, cell[0]);
				parent.parent.addTabPage(url, "主端口性能趋势图");

			}

			if (cell[0].get('SUB_ROUTE_TYPE') == 1) {
				setTimeout('newOppositePictureLater()', 1000);
			}

		} else {
			Ext.Msg.alert('信息', '请选择主备用至少有一条为端口的记录进行查询！');
			return;
		}

	} else {
		Ext.Msg.alert('信息', '请选择记录，每次只能选择一条！');
		return;
	}

}

function newOppositePictureLater() {

	var cell = oppositePanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
		if (cell[0].get('SUB_ROUTE_TYPE') == 1) {
			var url = getDiagramSubURL(1, cell[0]);
			parent.parent.addTabPage(url, "备端口性能趋势图");
		}

	}

}

/**
 * 更新反向基准值 value （1是正向 2 是反向）在复用段详情页面使用 ,（3 正左，4 正右 5 反左 6 反右） 在复用段设置页面使用
 */
function updateOpposite(value) {
	// 更新基准值
	var url = "selectValueType.jsp?value=" + value;
	addForwardWindow = new Ext.Window({
				id : 'addForwardWindow',
				title : '更新基准值',
				width : 320,
				height : 200,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
	addForwardWindow.show();
}

/**
 * 
 */
function saveOpposite() {
	var cell = oppositeStore.getModifiedRecords();
	if (cell.length < 1) {
		Ext.Msg.alert('提示', '没有修改过的记录，无需保存！');
		return;
	} else {
		var jsonString = new Array();
		for (var i = 0; i < cell.length; i++) {
			var map = {
				"PM_MULTI_SEC_PTP_ID" : cell[i].get('PM_MULTI_SEC_PTP_ID'),
				"CUT_PM_VALUE" : cell[i].get('CUT_PM_VALUE'),
				"SUB_CUT_PM_VALUE" : cell[i].get('SUB_CUT_PM_VALUE')
			};
			jsonString.push(map);
		}

		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		Ext.Ajax.request({
					url : 'multiple-section!saveMultipleDetail.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {// 回调函数
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							isSaveO = false;
							Ext.Msg.alert("提示", obj.returnMessage, function(r) {
										// 刷新列表
										var oppositePageTool = Ext
												.getCmp('oppositePageTool');
										if (oppositePageTool) {
											oppositePageTool
													.doLoad(oppositePageTool.cursor);
										}
									});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
					},
					error : function(response) {
						Ext.Msg.alert('错误', '保存失败！');
					},
					failure : function(response) {
						Ext.Msg.alert('错误', '保存失败！');
					}

				});

	}
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
					Ext.getCmp('current_time').setValue(obj.PM_UPDATE_TIME);
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
	if (direction == 1) {
		Ext.getCmp("opposite").setDisabled(true);
	}

}

parent.getTab(curTabId).refreshImpl= function refreshImpl(){
	init();
	forwardStore.load();
	oppositeStore.load();
};

Ext.onReady(function() {
	Ext.Msg = parent.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
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
	//刷新页面数据
	
});