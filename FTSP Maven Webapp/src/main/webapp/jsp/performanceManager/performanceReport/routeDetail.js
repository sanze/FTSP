var emsId = "-3";
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 1
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
	}, [ "STATION_NAME", "AREA_NAME", "PM_MULTI_SEC_PTP_ID",
			"BASE_EMS_CONNECTION_ID", "MULTI_SEC_ID", "EQUIP_NAME", "PTP_ID",
			"SUB_PTP_ID", "PTP_NAME", "SUB_PTP_NAME", "CALCULATE_POINT",
			"SUB_CALCULATE_POINT", "NOTE", "SUB_NOTE", "PM_TYPE", "PORT_TYPE",
			"ROUTE_TYPE", "HISTORY_PM_VALUE", "SUB_HISTORY_PM_VALUE",
			"CUT_PM_VALUE", "SUB_CUT_PM_VALUE", "HISTORY_PM_TIME", "CUT_PM_TIME",
			"CURRENT_PM_TIME", "SUB_PM_TYPE", "SUB_ROUTE_TYPE", "IS_VIRTUAL",
			"SUB_IS_VIRTUAL", "MODEL", "SUB_MODEL" ])
});
forwardStore.load();
// ************************* 任务信息列模型 ****************************
var forwardCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});
var forwardCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ forwardCheckboxSelectionModel, {
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
		dataIndex : 'PM_TYPE',
		renderer : function(value, cellmeta, record) {
			
			if (value == "1") {
				return "输入光功率";
			} else if (value == "2") {
				return "输出光功率";
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
		header : '性能值(主)',
		width : 100,
		dataIndex : 'HISTORY_PM_VALUE'
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
		dataIndex : 'SUB_PM_TYPE',
		renderer : function(value, cellmeta, record) {
			
			if (value == "1") {
				return "输入光功率";
			} else if (value == "2") {
				return "输出光功率";
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
		header : '性能值(备)',
		width : 100,
		dataIndex : 'SUB_HISTORY_PM_VALUE'
	}]
});

var forwardPageTool = new Ext.PagingToolbar({
	id : 'forwardPageTool',
	pageSize : 200,// 每页显示的记录值
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
	bbar : forwardPageTool
});

var infoPanel = new Ext.FormPanel({
	id : 'infoPanel',
	name : 'infoPanel',
	region : 'north',
	padding:'15',
	height:50,
	border : false,
	items : [ {
		layout : 'column',
		border : false,
		labelWidth:80,
		labelAlign:'right',
		items : [ {
			columnWidth : .17, // 第一列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'TRUNK_NAME',
				name : 'TRUNK_NAME',
				fieldLabel : '干线名称',
				maxLength : 64,
				allowBlank : true,
				disabled : true,
				anchor : '95%'
			} ]
		}, {
			columnWidth : .17, // 第二列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'sec_name',
				name : 'sec_name',
				fieldLabel : '复用段名称',
				disabled : true,
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		}, {
			columnWidth :.17, // 第一列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'direction',
				name : 'direction',
				fieldLabel : '方向',
				disabled : true,
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		}, {
			columnWidth : .17, // 第一列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [ {
				xtype : 'numberfield',
				id : 'std_wave',
				name : 'std_wave',
				fieldLabel : '标称波道数',
				maxLength : 64,
				allowBlank : true,
				disabled : true,
				anchor : '95%'
			} ]
		}, {
			columnWidth : .17, // 第一列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [ {
				xtype : 'numberfield',
				id : 'actully_wave',
				name : 'actully_wave',
				fieldLabel : '实际波道数',
				maxLength : 64,
				allowBlank : true,
				disabled : true,
				anchor : '95%'
			} ]
		} ]

	}]
});

/** ***********反向tab********************* */
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 2
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
	}, [ "STATION_NAME", "AREA_NAME", "PM_MULTI_SEC_PTP_ID", "MULTI_SEC_ID",
			"EQUIP_NAME", "PTP_ID", "SUB_PTP_ID", "PTP_NAME", "SUB_PTP_NAME",
			"CALCULATE_POINT", "SUB_CALCULATE_POINT", "NOTE", "SUB_NOTE",
			"PM_TYPE", "PORT_TYPE", "ROUTE_TYPE", "HISTORY_PM_VALUE",
			"SUB_HISTORY_PM_VALUE", "CUT_PM_VALUE", "SUB_CUT_PM_VALUE","HISTORY_PM_TIME",
			"CUT_PM_TIME", "CURRENT_PM_TIME", "SUB_PM_TYPE", "SUB_ROUTE_TYPE",
			"IS_VIRTUAL", "SUB_IS_VIRTUAL", "MODEL", "SUB_MODEL" ])
});
oppositeStore.load();
// ************************* 任务信息列模型 ****************************
var oppositeCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});
var oppositeCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ oppositeCheckboxSelectionModel, {
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
		dataIndex : 'PM_TYPE'
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
		header : '性能值(主)',
		width : 100,
		dataIndex : 'HISTORY_PM_VALUE'
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
		dataIndex : 'SUB_PM_TYPE'
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
		header : '性能值(备)',
		width : 100,
		dataIndex : 'SUB_HISTORY_PM_VALUE'
	} ]
});

var oppositePageTool = new Ext.PagingToolbar({
	id : 'oppositePageTool',
	pageSize : 200,// 每页显示的记录值
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
	bbar : oppositePageTool
});

var tab = new Ext.TabPanel({
	id : 'tabs1',
	anchor : "right 70%",
	region : "center",
	activeTab : 0,
	// plugins: new Ext.ux.TabCloseMenu(),
	items : [ {
		title : '正向',
		layout : 'fit',
		// anchor : "right 100%",
		items : [ forwardPanel ]
	}, {
		title : '反向',
		layout : 'fit',
		// anchor : "right 100%",
		items : [ oppositePanel ]
	} ]
});


/**
 * 查询历史性能，刷新历史性能时间
 */
function select() {
	var time = retrievalTime;
//	var time = '2014-1-1';

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
					// 刷新列表
					var forwardPageTool = Ext.getCmp('forwardPageTool');
					if (forwardPageTool) {
						forwardPageTool.doLoad(forwardPageTool.cursor);
					}

					var oppositePageTool = Ext.getCmp('oppositePageTool');
					if (oppositePageTool) {
						oppositePageTool.doLoad(oppositePageTool.cursor);
					}
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
				method : 'POST',
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
					emsId = obj.BASE_EMS_CONNECTION_ID;
					select();
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

Ext
		.onReady(function() {
			Ext.Msg = parent.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [ infoPanel, tab ],
				renderTo : Ext.getBody()
			});
			init();
		});