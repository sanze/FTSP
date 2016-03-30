Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
var startNe=-1;
var endNe=-1;
var startPtp=-1;
var endPtp=-1;
var AlarmCSSDefs = [ "AlarmCss_INDETERMINATE", "AlarmCss_CRITICAL",
		"AlarmCss_MAJOR", "AlarmCss_MINOR", "AlarmCss_WARNING",
		"AlarmCss_CLEARED" ];
var circuitType = [ "undefined", "单向", "双向" ];
// pulic tool
var tools = [ {
	id : 'close',
	handler : function(e, target, panel) {
		panel.ownerCt.remove(panel, true);
	}
} ];
var fm = Ext.form;

var datCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},

	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), {
		id : 'route_neName',
		header : '网元',
		sortable : false,
		dataIndex : 'NE_NAME',
		width : 200
	}, {
		id : 'route_port',
		header : '端口',
		sortable : false,
		dataIndex : 'PORT',
		width : 300
	}, {
		id : 'route_ctp',
		header : '时隙',
		sortable : false,
		dataIndex : 'CTP',
		width : 200
	}, {
		id : 'route_ems_name',
		header : '所属网管',
		sortable : false,
		dataIndex : 'EMS_NAME',
		width : 500,
		resizable : true
	} ]
});

var ptnCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},

	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), {
		id : 'route_neName',
		header : '网元',
		sortable : false,
		dataIndex : 'NE_NAME',
		width : 200
	}, {
		id : 'route_port',
		header : '端口',
		sortable : false,
		dataIndex : 'PORT',
		width : 300
	}, {
		id : 'route_pw',
		header : '伪线',
		sortable : false,
		dataIndex : 'PW',
		width : 100
	},{
		id : 'route_tunel',
		header : '隧道',
		sortable : false,
		dataIndex : 'TUNEL',
		width : 100
	}, {
		id : 'route_ems_name',
		header : '所属网管',
		sortable : false,
		dataIndex : 'EMS_NAME',
		width : 100,
		resizable : true
	} ]
});

var otnCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
	// columns are not sortable by default
	},

	columns : [ new Ext.grid.RowNumberer({
		width : 26,
		locked : true
	}), {
		id : 'otn_route_neName',
		header : '网元',
		sortable : false,
		dataIndex : 'NE_NAME',
		width : 200
	}, {
		id : 'otn_route_port1',
		header : '端口1',
		sortable : false,
		dataIndex : 'PORT',
		width : 200
	}, {
		id : 'otn_route_ctp1',
		header : '时隙',
		sortable : false,
		dataIndex : 'CTP',
		width : 100
	}, {
		id : 'otn_route_port2',
		header : '端口2',
		sortable : false,
		dataIndex : 'PORT_TWO',
		width : 200
	}, {
		id : 'otn_route_ctp2',
		header : '时隙',
		sortable : false,
		dataIndex : 'CTP_TWO',
		width : 100
	} ]
});
var datStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "NE_NAME", "EMS_NAME", "CTP", "PORT" ])
});

var otnRouterStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : 'rows'
	}, [ "NE_NAME", "PORT", "PORT_TWO", "CTP", "CTP_TWO" ])
});

var ptnRouterStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : 'rows'
	}, [ "NE_NAME","EMS_NAME", "PORT", "PORT", "PW", "TUNEL" ])
});

var datCm;
var routeStore;
var routeComboStoreUrl;
var routeInfoUrl;
var topoDisplayUrl;
if (serviceType == 3) {
	dataCm = otnCm;
	routeStore = otnRouterStore;
	routeComboStoreUrl = 'circuit!getOtnCircuitBycircuitNo.action';
	routeInfoUrl = 'circuit!getOtnCirInfoById.action';
	topoDisplayUrl='circuit!getOtnRouteTopo.action';
}else if (serviceType == 4) {
	dataCm = ptnCm;
	routeStore = ptnRouterStore;
	routeComboStoreUrl = 'circuit!getPtnCircuitBycircuitNo.action';
	routeInfoUrl = 'circuit!getPtnCirInfoById.action';
	topoDisplayUrl='circuit!getPtnRouteTopo.action';
} else {
	dataCm = datCm;
	routeStore = datStore;
	routeComboStoreUrl = 'circuit!getCircuitBycircuitNo.action';
	routeInfoUrl = 'circuit!getCirInfoById.action';
	topoDisplayUrl='circuit!getRouteTopo.action';
}
// 电路网元路由信息
var datGrid = new Ext.grid.GridPanel({
	id : 'datGrid',
	region : "center",
	store : routeStore,
	cm : dataCm,
	// selModel : datSm,
	animCollapse : true,
	autoScroll : true,
	frame : false,
	border : false
});
var datPanel = new Ext.Panel({
	id : 'datPanel',
	flex : 7,
	layout : 'fit',
	items : [ datGrid ]
});
var routeComboStore = new Ext.data.Store({
	url : routeComboStoreUrl,
	baseParams : {
		"vCircuit" : vCircuit
	},

	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "displayname", "circuitId" ])
});

var routerFormPanel = new Ext.form.FormPanel({
	// flex : 1,
	height : 30,
	tbar : [ "-",'电路编号：', {
		xtype : 'textfield',
		id : 'circuitNoT',
		disabled : true,
		width : 100
	}, "-",'路由名称：', {
		xtype : 'textfield',
		id : 'circuitNameT',
		disabled : true,
		width : 100
	}, "-",'资源编号：', {
		xtype : 'textfield',
		id : 'sysResNoT',
		disabled : true,
		width : 100

	}, "-",'路径选择：', {
		xtype : 'combo',
		id : 'routeCombo',
		store : routeComboStore,
		displayField : "displayname",
		valueField : 'circuitId',
		triggerAction : 'all',
		editable : false,
		autoSelect : true,
		mode : 'local',
		// width:300,
		// sideText : '<font color=red>*</font>',
		anchor : '95%',
		listeners : {
			select : function(a, b, c) {
				// TH.showVersion();
				var circuitId = b.data.circuitId;
				circuitIdInner=b.data.circuitId;
				getCirInfo(circuitId);
			}
		}

	}, "-", {
		text : '导出',
		handler : exportData,
		privilege : actionAuth,
		icon : '../../resource/images/btnImages/export.png'

	}, "-", {
		xtype : 'checkbox',
		id : 'checkBox001',
		boxLabel : '电路告警/性能',
		handler : function(checkbox, checked) {
			if (checked)
				Ext.getCmp('tabs1').show();
			if (!checked) {
				Ext.getCmp('tabs1').hide();
			}
		}
	}

	]
});

function exportData() {
	if (routeStore.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	} else if (routeStore.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				{
					exportRequest();
				}
			}
		});
	} else
		exportRequest();
}
var exportRequest = function() {
	// win.getEl().mask("正在导出...");
	var exportData = {
		"cirNo" : vCircuit,
		"serviceType" : serviceType
	};
	Ext.Ajax.request({
		url : 'circuit-export!exportRoute.action',
		type : 'post',
		params : {
			"jsonString" : Ext.encode(exportData)
		},
		success : function(response) {
			// win.getEl().unmask();
			var rs = Ext.decode(response.responseText);
			if (rs.returnResult == 1 && rs.returnMessage != "") {
				var destination = {
					"filePath" : rs.returnMessage
				};
				window.location.href = "download!execute.action?"
						+ Ext.urlEncode(destination);
			} else {
				// win.getEl().unmask();
				Ext.Msg.alert("提示", "导出失败！");
			}
		},
		error : function(response) {
			// win.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			// win.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
};

var formPanel1 = new Ext.Panel({
	frame : false,
	autoScroll : true,
	// title: '新增用户',
	flex : 3,
	bodyStyle : 'padding:30px 20px 0',
	labelAlign : 'right',
	items : [ {
		layout : 'form',
		border : false,
		autoScroll : true,
		xtype : 'form',
		labelWidth : 60,
		labelPad : 0,
		labelSeparator : "：",
		items : [ {
			xtype : 'textfield',
			id : 'circuitNo',
			width : 200,
			readOnly : true,
			// disabled : true,
			fieldLabel : '电路编号'
		}, {
			xtype : 'textfield',
			id : 'circuitName',
			width : 200,
			readOnly : true,
			fieldLabel : '路由名称'
		}, {
			xtype : 'textfield',
			id : 'sysResNo',
			readOnly : true,
			width : 200,
			fieldLabel : '资源编号'
		}, {
			xtype : 'textfield',
			id : 'clientName',
			readOnly : true,
			width : 200,
			fieldLabel : '客户名称'
		}, {
			xtype : 'textfield',
			id : 'aport',
			name : 'aport',
			readOnly : true,
			width : 200,
			fieldLabel : 'A端端口'
		}, {
			xtype : 'textfield',
			id : 'actp',
			name : 'actp',
			width : 200,
			readOnly : true,
			fieldLabel : 'A端时隙'
		}, {
			xtype : 'textfield',
			id : 'zport',
			name : 'zport',
			width : 200,
			readOnly : true,
			fieldLabel : 'Z端端口'
		}, {
			xtype : 'textfield',
			id : 'zctp',
			name : 'zctp',
			width : 200,
			readOnly : true,
			fieldLabel : 'Z端时隙'
		}, {
			xtype : 'textfield',
			id : 'usedFor',
			name : 'usedFor',
			width : 200,
			readOnly : true,
			fieldLabel : '用途'
		}, {
			xtype : 'textfield',
			id : 'AEndUserName',
			name : 'AEndUserName',
			readOnly : true,
			width : 200,
			fieldLabel : 'A端用户'
		}, {
			xtype : 'textfield',
			id : 'ZEndUserName',
			readOnly : true,
			width : 200,
			name : 'ZEndUserName',
			fieldLabel : 'Z端用户'
		}, {
			xtype : 'textfield',
			id : 'AConnectRate',
			readOnly : true,
			width : 200,
			name : 'AConnectRate',
			fieldLabel : '电路速率'
		}, {
			xtype : 'textfield',
			id : 'serviceType',
			readOnly : true,
			width : 200,
			fieldLabel : '业务类型'
		}, {
			xtype : 'textfield',
			id : 'circuitType',
			name : 'circuitType',
			readOnly : true,
			width : 200,
			fieldLabel : '电路类别'
		} ]
	} ]
});

var circuitInfo = new Ext.Panel({
	id : 'circuitInfo',
	title : '路由信息',
	height : 480,
	collapsible : true,
	layout : {
		type : 'hbox',
		padding : '0',
		align : 'stretch'
	},
	items : [ datPanel, formPanel1 ]
});
var pmSm = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true,
	hidden : true
});
var pmCm = new Ext.grid.ColumnModel(
		{
			// specify any defaults for each column
			defaults : {
				sortable : true
			// columns are not sortable by default
			},
			columns : [
					new Ext.grid.RowNumberer({
						width : 26
					}),
					pmSm,
					{
						id : 'DISPLAY_EMS_GROUP',
						header : '网管分组',
						dataIndex : 'DISPLAY_EMS_GROUP',
						width : 100,
						locked : true
					},
					{
						id : 'DISPLAY_EMS',
						header : '网管',
						dataIndex : 'DISPLAY_EMS',
						width : 100,
						locked : true
					},
					{
						id : 'DISPLAY_SUBNET',
						header : '子网',
						dataIndex : 'DISPLAY_SUBNET',
						width : 80,
						locked : true
					},
					{
						id : 'DISPLAY_NE',
						header : '网元',
						dataIndex : 'DISPLAY_NE',
						width : 80,
						locked : true
					},
					{
						id : 'DISPLAY_AREA',
						header : top.FieldNameDefine.AREA_NAME,
						dataIndex : 'DISPLAY_AREA',
						width : 80,
						hidden : true
					},
					{
						id : 'DISPLAY_STATION',
						header : top.FieldNameDefine.STATION_NAME,
						dataIndex : 'DISPLAY_STATION',
						width : 80,
						hidden : true
					},
					{
						id : 'DISPLAY_PRODUCT_NAME',
						header : '型号',
						dataIndex : 'DISPLAY_PRODUCT_NAME',
						width : 110
					},
					{
						id : 'DISPLAY_PORT_DESC',
						header : '端口',
						dataIndex : 'DISPLAY_PORT_DESC',
						width : 150
					},
					{
						id : 'DOMAIN',
						header : '业务类型',
						dataIndex : 'DOMAIN',
						width : 60,
						renderer : function(v) {
							switch (v) {
							case 1:
								return "SDH";
							case 2:
								return "WDM";
							case 3:
								return "ETH";
							case 4:
								return "ATM";
							}
						}
					},
					{
						id : 'PTP_TYPE',
						header : '端口类型',
						dataIndex : 'PTP_TYPE',
						width : 60
					},
					{
						id : 'RATE',
						header : '速率',
						dataIndex : 'RATE',
						width : 50
					},
					{
						id : 'DISPLAY_CTP',
						header : '通道',
						dataIndex : 'DISPLAY_CTP',
						width : 100
					},
					{
						id : 'PM_DESCRIPTION',
						header : '性能事件',
						dataIndex : 'PM_DESCRIPTION',
						width : 130
					},
					{
						id : 'LOCATION',
						header : '方向',
						dataIndex : 'LOCATION',
						width : 60,
						renderer : function(v) {
							switch (v) {
							case 1:
								return "近端接收";
							case 2:
								return "远端接收";
							case 3:
								return "近端发送";
							case 4:
								return "远端发送";
							case 5:
								return "双向";
							default:
								return null;
							}
						}
					},
					{
						id : 'PM_VALUE',
						header : '性能值',
						dataIndex : 'PM_VALUE',
						width : 50,
						renderer : function(v, metadata, record) {
							exLv = record.get('EXCEPTION_LV');
							if (exLv == 0) {
								return '<font color=black>' + v + '</font>';
							} else if (exLv == 1) {
								return '<font color=blue>' + v + '</font>';
							} else if (exLv == 2) {
								return '<font color=orange>' + v + '</font>';
							} else if (exLv == 3) {
								return '<font color=red>' + v + '</font>';
							}
						}
					},
					{
						id : 'PM_COMPARE_VALUE_DISPLAY',
						header : '性能比较值',
						dataIndex : 'PM_COMPARE_VALUE_DISPLAY',
						width : 120,
						renderer : function(v, metadata, record) {
							exLv = record.get('EXCEPTION_LV');
							if (exLv == 0) {
								return '<font color=black>' + v + '</font>';
							} else if (exLv == 1) {
								return '<font color=blue>' + v + '</font>';
							} else if (exLv == 2) {
								return '<font color=orange>' + v + '</font>';
							} else if (exLv == 3) {
								return '<font color=red>' + v + '</font>';
							}
						}
					},
					{
						id : 'PM_COMPARE_VALUE',
						header : '性能基准值',
						dataIndex : 'PM_COMPARE_VALUE',
						hidden : true,
						width : 65
					},
					{
						id : 'EXCEPTION_COUNT',
						header : '连续异常',
						dataIndex : 'EXCEPTION_COUNT',
						width : 60
					},
					{
						id : 'THRESHOLD_1',
						header : '计数值阈值1',
						dataIndex : 'THRESHOLD_1',
						hidden : true,
						width : 60
					},
					{
						id : 'THRESHOLD_2',
						header : '计数值阈值2',
						dataIndex : 'THRESHOLD_2',
						hidden : true,
						width : 60
					},
					{
						id : 'THRESHOLD_3',
						header : '计数值阈值3',
						dataIndex : 'THRESHOLD_3',
						hidden : true,
						width : 60
					},
					{
						id : 'FILTER_VALUE',
						header : '计数值过滤值',
						dataIndex : 'FILTER_VALUE',
						hidden : true,
						width : 60
					},
					{
						id : 'OFFSET',
						header : '物理量基准值偏差',
						dataIndex : 'OFFSET',
						hidden : true,
						width : 60
					},
					{
						id : 'UPPER_VALUE',
						header : '物理量上限值',
						dataIndex : 'UPPER_VALUE',
						hidden : true,
						width : 60
					},
					{
						id : 'UPPER_OFFSET',
						header : '物理量上限值偏差',
						dataIndex : 'UPPER_OFFSET',
						hidden : true,
						width : 60
					},
					{
						id : 'LOWER_VALUE',
						header : '物理量下限值',
						dataIndex : 'LOWER_VALUE',
						hidden : true,
						width : 60
					},
					{
						id : 'LOWER_OFFSET',
						header : '物理量下限值偏差',
						dataIndex : 'LOWER_OFFSET',
						hidden : true,
						width : 60
					},
					{
						id : 'DISPLAY_TEMPLATE_NAME',
						header : '性能分析模板',
						dataIndex : 'DISPLAY_TEMPLATE_NAME',
						width : 100,
						renderer : function(value, metadata, record) {
							return ((value == null) ? "" : "<a href='#' onclick=toDetailTemplate("
								+ record.get("TEMPLATE_ID") + "," + record.get("TYPE") + ",'"
								+ record.get("PM_STD_INDEX") + "')>" + value + "</a>");
						}
					},
					{
						id : 'RETRIEVAL_TIME',
						header : '采集时间',
						dataIndex : 'RETRIEVAL_TIME',
						width : 100,
						renderer : function(value) {
							return value.time ? (Ext.util.Format
									.dateRenderer('Y-m-d H:i:s'))(new Date(
									value.time)) : "";
						}
					}, {
						id : 'TEMPLATE_ID',
						header : '模板ID',
						dataIndex : 'TEMPLATE_ID',
						width : 100,
						hidden : true,
						hideable : false
					} ]
		});
//显示性能模板
function toDetailTemplate(TEMPLATE_ID, TYPE, PM_STD_INDEX) {
	var url = '../performanceManager/PMsearch/templateInfo.jsp?TEMPLATE_ID=' + TEMPLATE_ID + '&TYPE=' + TYPE + '&PM_STD_INDEX='
			+ PM_STD_INDEX + '&isCurrent=1&domain=1';
	var templateInfoWin = new Ext.Window({
		id : 'templateInfoWin',
		title : '性能分析模板',
		width : 360,
		height : 320,
		isTopContainer : true,
		modal : true,
		plain : true, // 是否为透明背景
		html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />'
	});
	templateInfoWin.show();
}

var pmStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "ID", "BASE_EMS_CONNECTION_ID", "BASE_NE_ID", "BASE_RACK_ID",
			"BASE_SHELF_ID", "BASE_SLOT_ID", "BASE_SUB_SLOT_ID",
			"BASE_UNIT_ID", "BASE_SUB_UNIT_ID", "BASE_PTP_ID",
			"BASE_OTN_CTP_ID", "BASE_SDH_CTP_ID", "TARGET_TYPE", "LAYER_RATE",
			"PM_STD_INDEX", "PM_INDEX", "PM_VALUE", "PM_COMPARE_VALUE",
			"PM_COMPARE_VALUE_DISPLAY", "TYPE", "THRESHOLD_1", "THRESHOLD_2",
			"THRESHOLD_3", "FILTER_VALUE", "OFFSET", "UPPER_VALUE",
			"UPPER_OFFSET", "LOWER_VALUE", "LOWER_OFFSET", "PM_DESCRIPTION",
			"LOCATION", "UNIT", "GRANULARITY", "EXCEPTION_LV",
			"EXCEPTION_COUNT", "RETRIEVAL_TIME", "DISPLAY_EMS_GROUP",
			"DISPLAY_EMS", "DISPLAY_SUBNET", "DISPLAY_NE", "DISPLAY_AREA",
			"DISPLAY_STATION", "DISPLAY_PRODUCT_NAME", "DOMAIN",
			"DISPLAY_PORT_DESC", "RATE", "DISPLAY_CTP",
			"DISPLAY_TEMPLATE_NAME", "PTP_TYPE", "TEMPLATE_ID" ])
});

var pmPageTool = new Ext.PagingToolbar({
	id : 'pmPageTool',
	pageSize : 200, // 每页显示的记录值
	store : pmStore,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
// 用于性能查询全局变量
var nodeList = [];
// 计算框选网元个数;
var neIsOk = 0;
// 用于告警查询全局变量
var neListAlarm = [];// 网元数组；
var ptpListAlarm = [];// 端口数组
var pmGrid = new Ext.grid.GridPanel({
	id : 'pmGrid',
	title : '性能信息',
	store : pmStore,
	flex : 1,
	cm : pmCm,
	selModel : pmSm,
	animCollapse : true,
	autoScroll : true,
	frame : false,
	bbar : pmPageTool,
	stripeRows : true, // 交替行效果
	viewConfig : {
		forceFit : false
	},
	tbar : [ "-",new Ext.form.RadioGroup({
		id : 'section',
		name : 'section',
		columns : 2,
//		width : 200,
		items : [{
			name : 'section1',
			// id : "15min",
			inputValue : 1,
			boxLabel : '15分钟',
			checked : true

		}, {
			name : 'section1',
			// id : "24h",
			inputValue : 2,
			boxLabel : '24小时'
		} ]

	}), "-",{
		text : '刷新',
		icon : '../../resource/images/btnImages/refresh.png',
		handler : function() {
			// neIsOk:标识网元个数是否大于5个
			if (neIsOk < 6) {
				Ext.Msg.confirm("提示", "性能查询耗时较长，是否继续？", function(btn) {
					if (btn == 'yes') {
						{
							getPmInfo();
						}
					}
				});
			} else {
				Ext.Msg.alert("提示", "网元个数不得超过5个");
			}
		}
	} ]
});

var pmPanelTest = new Ext.Panel({
	anchor : "100% 100%",
	layout : 'fit',
	items : [ pmGrid ]
});
var almStore = new Ext.data.Store({
	url : 'circuit!getCurrentAlarmForCircuit.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	},
			[ '_id', 'IS_ACK', 'PERCEIVED_SEVERITY', 'NATIVE_PROBABLE_CAUSE',
					'NORMAL_CAUSE', 'EMS_GROUP_NAME', 'NATIVE_EMS_NAME','EMS_NAME',
					'NE_NAME', 'PRODUCT_NAME', 'SLOT_DISPLAY_NAME',
					'UNIT_NAME', 'PORT_NO', 'DOMAIN', 'PTP_TYPE',
					'INTERFACE_RATE', 'CTP_NAME', 'FIRST_TIME', 'AMOUNT',
					'NE_TIME', 'SERVICE_AFFECTING', 'CLEAR_TIME',
					'ACK_TIME', 'ACK_USER', 'ALARM_TYPE', 'PROBABLE_CAUSE',
					'IS_CLEAR' ])
});

var almSm = new Ext.grid.CheckboxSelectionModel();

var almCm = new Ext.grid.ColumnModel({

	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), almSm, {
		header : '告警数据ID',
		dataIndex : '_id',
		width : 100,
		hidden : true
	}, {
		id : 'perceivedSeverity',
		header : '告警等级',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 100,
		renderer : function(value, meta, record) {
			if (record.data["IS_CLEAR"] == 2) {
				if (value == 1) {
					return '<font color="#FF0000">紧急</font>';
				} else if (value == 2) {
					return '<font color="#FF8000">重要</font>';
				} else if (value == 3) {
					return '<font color="#FFFF00">次要</font>';
				} else if (value == 4) {
					return '<font color="#800000">提示</font>';
				}
			} else {
				return '<font color="#00FF00">已清除</font>';
			}

		}
	}, {
		header : '告警名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100
	}, {
		header : '归一化名称',
		dataIndex : 'NORMAL_CAUSE',
		width : 100
	}, {
		header : '网管分组',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	}, {
		header : '网管',
		dataIndex : 'EMS_NAME',
		width : 100
	}, {
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	}, {
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 100
	}, {
		header : '槽道',
		dataIndex : 'SLOT_DISPLAY_NAME',
		width : 100
	}, {
		header : '板卡',
		dataIndex : 'UNIT_NAME',
		width : 100
	}, {
		header : '端口',
		dataIndex : 'PORT_NO',
		width : 100
	}, {
		header : '业务类型',
		dataIndex : 'DOMAIN',
		width : 100,
		renderer : function(value) {
			if (value == 1) {
				return 'SDH';
			} else if (value == 2) {
				return 'WDM';
			} else if (value == 3) {
				return 'ETH';
			} else if (value == 4) {
				return 'ATM';
			}
		}
	}, {
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		width : 100
	}, {
		header : '速率',
		dataIndex : 'INTERFACE_RATE',
		width : 100
	}, {
		header : '通道',
		dataIndex : 'CTP_NAME',
		width : 100
	}, {
		header : '首次发生时间',
		dataIndex : 'FIRST_TIME',
		width : 100
	}, {
		header : '频次',
		dataIndex : 'AMOUNT',
		width : 100
	}, {
		header : '最近发生时间',
		dataIndex : 'NE_TIME',
		width : 100
	}, {
		header : '业务影响',
		dataIndex : 'SERVICE_AFFECTING',
		width : 100,
		renderer : function(value) {
			if (value == 1) {
				return '影响';
			} else if (value == 2) {
				return '不影响';
			} else if (value == 3) {
				return '未知';
			}
		}
	}, {
		header : '清除时间',
		dataIndex : 'CLEAR_TIME',
		width : 100
	}, {
		header : '确认时间',
		dataIndex : 'ACK_TIME',
		width : 100
	}, {
		header : '确认者',
		dataIndex : 'ACK_USER',
		width : 100
	}, {
		header : '告警类型',
		dataIndex : 'ALARM_TYPE',
		width : 100,
		renderer : function(value) {
			if (value == 0) {
				return '通信';
			} else if (value == 1) {
				return '服务';
			} else if (value == 2) {
				return '设备';
			} else if (value == 3) {
				return '处理';
			} else if (value == 4) {
				return '环境';
			} else if (value == 5) {
				return '安全';
			} else if (value == 6) {
				return '连接';
			}
		}
	}, {
		header : '告警标准名',
		dataIndex : 'PROBABLE_CAUSE',
		width : 100
	}, {
		header : '清除状态',
		dataIndex : 'IS_CLEAR',
		width : 100,
		renderer : function(value) {
			if (value == 1) {
				return '已清除';
			} else {
				return '未清除';
			}
		}
	} ]
});

var almPageTool = new Ext.PagingToolbar({
	id : 'almPageTool',
	pageSize : 500, // 每页显示的记录值
	store : almStore,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var almGrid = new Ext.grid.GridPanel({
	id : "almGrid",
	region : "center",
	// anchor : "right 100%",
	flex : 1,
	title : '告警信息',
	cm : almCm,
	store : almStore,
	// autoExpandColumn: 'roleName', // column with this id will be
	// expanded
	stripeRows : true, // 交替行效果
	loadMask : {
		msg : '数据加载中...'
	},
	selModel : almSm, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : false
	// // 根据每一行的Alarm级别设置相应的CSS样式
	// getRowClass : function(record, index) {
	// var alm = record.get('perceivedSeverity');
	// return AlarmCSSDefs[alm];
	// }
	},
	bbar : almPageTool,
	tbar : [ "-",{
		text : '刷新',
		icon : '../../resource/images/btnImages/refresh.png',
		handler : getAlmInfo
	} ]
});

var almPanelTest = new Ext.Panel({
	anchor : "100% 100%",
	layout : 'fit',
	items : [ almGrid ]
});
var canvasPanel = new Ext.Panel({
	id : 'canvasPanel',
	height : 300,
	title : '路由图',
	collapsible : true,
	collapseFirst : false,
	items : [ {
		xtype : "flex",
		id : "flex",
		type : "apa"
	} ]
});
// 刷新topo图方法
function refresh() {
//	if(serviecType !=3){
		nodeList = [];
		// 计算框选网元个数;
		neIsOk = 0;
		// 用于告警查询全局变量
		neListAlarm = [];// 网元数组；
		ptpListAlarm = [];// 端口数组
		var jsonDataTopo2 = {
			"vCircuit" : vCircuit
		};
		Ext.Ajax.request({
			url :topoDisplayUrl,
			type : 'post',
			params : jsonDataTopo2,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				Ext.getCmp('flex').loadData(obj);
			},
			error : function(response) {
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				Ext.Msg.alert("错误", response.responseText);
			}
		});
//	}else{
//		var obj = {"total":0};
//		Ext.getCmp('flex').loadData(obj);
//	}
}
var timerID = -1;
Ext.onReady(function() {
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	var win = new Ext.Viewport({
		id : 'win',
		layout : {/*
		 */
			type : 'vbox',
			padding : '0',
			align : 'stretch'
		},
		autoScroll : true,
		defaults : {
			margins : '0 5 0 0'
		},
		items : [ routerFormPanel, {
			xtype : "panel",
			flex : 1,
			id : 'mainPane',
			layout : "anchor",
			autoScroll : true,
			items : [ circuitInfo, canvasPanel, new Ext.TabPanel({
				id : 'tabs1',
				anchor : "right 80%",
				hidden : true,
				region : "center",
				activeTab : 0,
				items : [ {
					title : '当前告警',
					layout : 'anchor',
					items : [ almPanelTest ]
				}, {
					title : '当前性能',
					layout : 'anchor',
					items : [ pmPanelTest ]
				} ]
			}) ]
		} ],
		renderTo : Ext.getBody()
	});
	win.show();
	initData();
	pageLoaded = true;

});
var ids = [];
/**
 * 稍微封装了下Ext.Ajax.request
 * 
 * @param {}
 *            url Action名称
 * @param {}
 *            param 参数
 * @param {}
 *            callback 回调函数
 */
var gDat = null;
var gId = -1;
var gPtpIds = null;
var isFirstInit = 1;
function initData() {
	if (vCircuit != null) {
		routeComboStore.load({
			callback : function(r, options, success) {
				if (success) {
					Ext.getCmp('routeCombo').setValue(r[0].get('displayname'));
					circuitId = routeComboStore.getAt(0).get('circuitId');
					circuitIdInner= routeComboStore.getAt(0).get('circuitId');
					getCirInfo(circuitId);
					isFirstInit = 0;
				} else {
					Ext.Msg.alert('错误', '加载失败！');
				}
			}
		});
	}
}

Ext.getCmp("flex").on("initialize", function() {
//	if(serviceType != 3){
		var jsonDataTopo1 = {
				"vCircuit" : vCircuit
			};
			Ext.Ajax.request({
				url : topoDisplayUrl,
				type : 'post',
				params : jsonDataTopo1,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					//console.log(JSON.stringify(obj));
					Ext.getCmp('flex').loadData(obj);
				},
				error : function(response) {
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					Ext.Msg.alert("错误", response.responseText);
				}
			});
//	}else{
//		var obj={"total":0};
//		Ext.getCmp('flex').loadData(obj);
//	}
});
// 获取路由显示，填写路由信息
function getCirInfo(circuitId) {
	// 加载网元同步列表
	var jsonData = {
		"vCircuit" : circuitId,
		'type' : serviceType,
		"serviceType" : serviceType
	};
	var jsonString = {
		// 电路id
		"vCircuit" : circuitId,
		// 电路类型
		'type' : serviceType,
		'serviceType' : serviceType,
		"limit" : 200
	};
	// routeStore：当电路类型为以太网或sdh时值为datStore，当电路类型为otn时值为otnRouterStore
	routeStore.proxy = new Ext.data.HttpProxy({
		url : 'circuit!getCircuitRoute.action'
	});
	routeStore.baseParams = jsonData;
	routeStore.load();
	Ext.Ajax.request({
		url : routeInfoUrl,
		type : 'post',
		params : {
			"jsonString" : Ext.encode(jsonData)
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			// console.log(obj);
			Ext.getCmp('circuitNo').setValue(obj.rows[0].cir_no);
			Ext.getCmp('circuitName').setValue(obj.rows[0].cir_name);
			Ext.getCmp('sysResNo').setValue(obj.rows[0].source_no);
			Ext.getCmp('clientName').setValue(obj.rows[0].client_name);
			Ext.getCmp('aport').setValue(obj.rows[0].a_end_port);
			Ext.getCmp('actp').setValue(obj.rows[0].a_end_ctp);
			Ext.getCmp('zport').setValue(obj.rows[0].z_end_port);
			Ext.getCmp('zctp').setValue(obj.rows[0].z_end_ctp);
			Ext.getCmp('usedFor').setValue(obj.rows[0].USED_FOR);
			Ext.getCmp('AEndUserName').setValue(obj.rows[0].a_end_user_name);
			Ext.getCmp('ZEndUserName').setValue(obj.rows[0].z_end_user_name);
			Ext.getCmp('AConnectRate').setValue(obj.rows[0].rate);
			Ext.getCmp('circuitType').setValue(circuitType[obj.circuitType]);
			Ext.getCmp('circuitNoT').setValue(obj.rows[0].cir_no);
			Ext.getCmp('circuitNameT').setValue(obj.rows[0].cir_name);
			Ext.getCmp('sysResNoT').setValue(obj.rows[0].source_no);
			startNe=obj.rows[0].a_ne_id;
			endNe=obj.rows[0].z_ne_id;
			startPtp=obj.rows[0].a_ptp_id;
			endPtp=obj.rows[0].z_ptp_id;
			
			if (obj.rows[0].svc_type == 1 ) {
				Ext.getCmp('serviceType').setValue('SDH');
			}
			if (obj.rows[0].svc_type == 2) {
				Ext.getCmp('serviceType').setValue('ETH');
			}
			if (obj.rows[0].svc_type == 3)
				Ext.getCmp('serviceType').setValue('OTN');
			if (obj.rows[0].svc_type == 4)
				Ext.getCmp('serviceType').setValue('PTN');
		},
		error : function(response) {
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}

var cirNo = {
	"vCircuit" : vCircuit,
	"type" : serviceType
}
Ext.Ajax.request({
	url : "circuit!getNeAndPortByCirNo.action",
	params : cirNo,
	type : 'post',
	success : function(response) {
		var result = Ext.util.JSON.decode(response.responseText);
		if (result.neList) {
			var ddddd = {
				"neList" : result.neList,
				"ptpList" : result.ptpList
			}
			almStore.baseParams = {
				"jsonString" : Ext.encode(ddddd)
			};
			almStore.load({
				callback : function(records, options, success) {
					if (!success) {
						Ext.Msg.alert("提示", "查询出错");
					}
				}
			});
		}
	}
})

/** ********* */
// 右键链路查性能
function linkCurrPM(ptpIds) {
	getPtpIdsFromString(ptpIds);
	Ext.Msg.confirm("提示", "性能查询耗时较长，是否继续？", function(btn) {
		if (btn == 'yes') {
			{
				Ext.getCmp('checkBox001').setValue(true);
				Ext.getCmp('tabs1').setActiveTab(1);
				getPmInfo();
			}
		}
	});
}
// 右键链路查告警
function linkCurrAlarm(ptpIds) {
	Ext.getCmp('checkBox001').setValue(true);
	Ext.getCmp('tabs1').setActiveTab(0);
	getPtpIdsFromString(ptpIds);
	getAlmInfo();
}
// 将ptpIds转成数组，并调用getNeIdsAndPtpIds方法为nodeList/portList赋值
function getPtpIdsFromString(ptpIds) {
	var tempPortList = ptpIds.split(",");
	var temNodeList = [];
	neListAlarm = [];
	ptpListAlarm = tempPortList;
	setNeIdList(temNodeList, tempPortList);
}
// 获取框选网元ID和端口，给全局变量nodeList/portList/赋值
function setNeIdList(neIdList, ptpIdList) {
	// 给告警查询赋值
	neListAlarm = neIdList;
	ptpListAlarm = ptpIdList;
	nodeList = [];
	// 为网元个数赋值
	neIsOk = neIdList.length;
	for ( var i = 0; i < neIdList.length; i++) {
//		var list = {
//			"nodeLevel" : 4,
//			"nodeId" : neIdList[i]
//		};
		if(neIdList[i]==startNe){
			nodeList.push(Ext.encode({"nodeLevel":8,"nodeId":startPtp}));
		}
		if(neIdList[i]==endNe){
			nodeList.push(Ext.encode({"nodeLevel":8,"nodeId":endPtp}));
		}
	}
	for ( var j = 0; j < ptpIdList.length; j++) {
		var list2 = {
			"nodeLevel" : 8,
			"nodeId" : ptpIdList[j]
		};
		nodeList.push(Ext.encode(list2));
	}
}
function currentAlarm(neIdList, ptpIdList) {
	Ext.getCmp('checkBox001').setValue(true);
	Ext.getCmp('tabs1').setActiveTab(0);
	neListAlarm = neIdList;
	ptpListAlarm = ptpIdList;
	getAlmInfo();
}
function currentPM(neIdList, ptpIdList) {
	setNeIdList(neIdList, ptpIdList);
	if (neIsOk < 6) {
		Ext.Msg.confirm("提示", "性能查询耗时较长，是否继续？", function(btn) {
			if (btn == 'yes') {
				{
					Ext.getCmp('checkBox001').setValue(true);
					Ext.getCmp('tabs1').setActiveTab(1);
					getPmInfo();
				}
			}
		});
	} else {
		Ext.Msg.alert("提示", "网元个数不得超过5个");
	}
}
var circuitIdInner=0;
function innerRoute(neId) {
	var jsonDataInner={
			"cirNo":circuitIdInner,
			"aNodeId":neId
	};
	innerRouteStore.baseParams = jsonDataInner;
	innerRouteStore.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				// top.Ext.getBody().unmask();
				//Ext.Msg.alert('提示', '没有查询到符合条件的电路');

			}
		}
	});
	innerRouteWindow.show();
}
function getAlmInfo() {
	if (neListAlarm.length > 0 || ptpListAlarm.length > 0) {
		var jsonDateAlarm = {
			"neList" : neListAlarm,
			"ptpList" : ptpListAlarm,
			"cirNo":vCircuit,
			"type":serviceType,
			"limit" : 500
		};
		almStore.baseParams = {
			"jsonString" : Ext.encode(jsonDateAlarm)
		};
		almStore.load({
			callback : function(records, options, success) {
				if (!success) {
					Ext.Msg.alert("提示", "查询出错");
				}
			}
		});
	} else
		Ext.Msg.alert('提示', '请选择网元！');
}

// 根据链路打开otn电路连接
function openOtnTab(vCircuit){
	 nu = vCircuit.split(";");

	for(var i = 0 ;i <nu.length;i++ ){
		var url = "../circuitManager/apaView.jsp?vCircuit=" + nu[i]+ "&serviceType=3";
		parent.addTabPage(url, "路由详情：(" + nu[i] + ")", authSequence);
	}

}

//var sdhStringList = ["TPL_CUR", "TPL_MAX", "TPL_MIN", "RPL_CUR",
//     				"RPL_MAX", "RPL_MIN", "RS_BBE", "RS_ES", "RS_SES", "RS_CSES",
//    				"RS_UAS", "RS_OFS", "MS_BBE", "MS_ES", "MS_SES", "MS_CSES",
//    				"MS_UAS", "VC4_BBE", "VC4_ES", "VC4_SES", "VC4_CSES",
//    				"VC4_UAS", "VC3_BBE", "VC3_ES", "VC3_SES", "VC3_CSES",
//    				"VC3_UAS", "VC12_BBE", "VC12_ES", "VC12_SES", "VC12_CSES",
//    				"VC12_UAS"];
var sdhDateUrl = "pm-search!searchCurrentSdhPmDate.action";
var sdhGetDateUrl = "pm-search!getCurrentSdhPmDate.action";
//var otnStringList = ["TPL_CUR","TPL_AVG","RPL_CUR","RPL_AVG","PCLSSNR_CUR",
//                     "PCLSWL_CUR","PCLSWLO_CUR","PCLSOP_CUR","OSC_BBE","OSC_ES",
//                     "OSC_SES","OSC_UAS","FEC_BEF_COR_ER","FEC_AFT_COR_ER","OTU_BBE",
//                     "OTU_ES","OTU_SES","OTU_UAS","OTU1_BBE","OTU1_ES","OTU1_SES","OTU1_UAS",
//                     "OTU2_BBE","OTU2_ES","OTU2_SES","OTU2_UAS","OTU3_BBE","OTU3_ES",
//                     "OTU3_SES","OTU3_UAS","OTU5G_BBE","OTU5G_ES","OTU5G_SES","OTU5G_UAS"];
var otnDateUrl = "pm-search!searchCurrentWDMPmDate.action";
var otnGetDateUrl = 'pm-search!getCurrentWdmPmDate.action';
if(serviceType != 3){
//	var stringList = sdhStringList;
	var dateUrl = sdhDateUrl;
	var getDateUrl = sdhGetDateUrl;
}else{
//	var stringList = otnStringList;
	var dateUrl = otnDateUrl;
	var getDateUrl = otnGetDateUrl;
}
function getPmInfo() {
	if (nodeList.length > 0) {
		var jsonDate = {
			"userId" : userId,
			"modifyList" : nodeList,
			"searchCond.granularity" : Ext.getCmp('section').getValue().inputValue,
			"rateList" : [ "not_in" ]
//			"stringList" : null
		};
		// var searchTag = 0;
		pmGrid.getEl().mask("正在查询,请稍候");
		Ext.Ajax.request({
			url : dateUrl,
			params : jsonDate,
			type : 'post',
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					if (1 == result.returnResult) {
						if (result.returnMessage != ""
								&& result.returnMessage != null) {
							Ext.Msg.alert("提示", "部分网元出错！<br>"
									+ result.returnMessage);
						}
						searchTag = result.searchTag;
						pmStore.proxy = new Ext.data.HttpProxy({
							url : getDateUrl
						});
						pmStore.baseParams = {
							"start" : 0,
							"limit" : 200,
							"userId" : userId,
							"searchCond.exception" : 1,
							"searchCond.searchTag" : searchTag
						};
						pmStore.load({
							callback : function(records, options, success) {
								if (!success) {
									Ext.Msg.alert("提示", "查询出错");
								}
								pmGrid.getEl().unmask();
							}
						});
					} else {
						pmGrid.getEl().unmask();
						Ext.Msg.alert("提示", result.returnMessage);
					}
				}
			},
			failure : function(response) {
				pmGrid.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				pmGrid.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	} else
		Ext.Msg.alert('提示', '请选择网元！');
}