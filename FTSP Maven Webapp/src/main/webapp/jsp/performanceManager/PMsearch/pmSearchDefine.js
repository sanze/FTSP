Ext.state.Manager.setProvider(new Ext.state.SessionStorageStateProvider({
	expires : new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365))
}));

var searchTag = 0;
// ---------for treePanel init---------
var westPanel;
(function() {
	var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		containerId : "westPanel",
		leafType : 6
	};
	var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
	function treeFilter(tree, parent, node) {
		if (node.attributes["nodeLevel"] == NodeDefine.NE && node.attributes["additionalInfo"]
				&& NodeDefine.TYPE.indexOf(node.attributes["additionalInfo"]["TYPE"]) != -1) {
			return false;
		}
	}
	westPanel = new Ext.Panel({
		id : "westPanel",
		region : "west",
		width : 280,
		autoScroll : true,
		boxMinWidth : 230,
		boxMinHeight : 260,
		forceFit : true,
		collapsed : false, // initially collapse the group
		collapsible : false,
		collapseMode : 'mini',
		split : true,
		filterBy : treeFilter,
		html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0 />'
	});
})();

// ---------for gridPanel init---------
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : false
});
sm.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	stateId : stateIdvar,
	columns : [
			new Ext.grid.RowNumberer({
				width : 26,
				locked : true
			}),
			sm,
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
				header : '板卡/端口',
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
						return "-";
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
				id : 'UNIT',
				header : '单位',
				dataIndex : 'UNIT',
				width : 50
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
				width : 120,
				hidden : false,
				renderer : function(value, metadata, record) {
					return toTemplateRenderer(value, metadata, record);
				}
			},
			{
				id : 'GRANULARITY',
				header : '周期',
				dataIndex : 'GRANULARITY',
				width : 80,
				renderer : function(v) {
					switch (v) {
					case 1:
						return "15 min";
					case 2:
						return "24 hour";
					default:
						return null;
					}
				}
			},
			{
				id : 'RETRIEVAL_TIME',
				header : '开始时间',
				dataIndex : 'RETRIEVAL_TIME',
				width : 120,
				renderer : function(value) {
					return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
							value.time)) : "";
				}
			}, {
				id : 'TEMPLATE_ID',
				header : '模板ID',
				dataIndex : 'TEMPLATE_ID',
				width : 100,
				hidden : true,
				hideable : false
			}, {
				id : 'PM_INDEX',
				header : '原始RETRIEVAL_TIME',
				dataIndex : 'PM_INDEX',
				width : 100,
				hidden : true
			}  ]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "ID", "BASE_EMS_CONNECTION_ID", "BASE_NE_ID", "BASE_RACK_ID", "BASE_SHELF_ID",
			"BASE_SLOT_ID", "BASE_SUB_SLOT_ID", "BASE_UNIT_ID", "BASE_SUB_UNIT_ID", "BASE_PTP_ID",
			"BASE_OTN_CTP_ID", "BASE_SDH_CTP_ID", "TARGET_TYPE", "LAYER_RATE", "PM_STD_INDEX",
			"PM_INDEX", "PM_VALUE", "PM_COMPARE_VALUE", "PM_COMPARE_VALUE_DISPLAY", "TYPE",
			"THRESHOLD_1", "THRESHOLD_2", "THRESHOLD_3", "FILTER_VALUE", "OFFSET", "UPPER_VALUE",
			"UPPER_OFFSET", "LOWER_VALUE", "LOWER_OFFSET", "PM_DESCRIPTION", "LOCATION", "UNIT",
			"GRANULARITY", "EXCEPTION_LV", "EXCEPTION_COUNT", "RETRIEVAL_TIME",
			"DISPLAY_EMS_GROUP", "DISPLAY_EMS", "DISPLAY_SUBNET", "DISPLAY_NE", "DISPLAY_AREA",
			"DISPLAY_STATION", "DISPLAY_PRODUCT_NAME", "DOMAIN", "DISPLAY_PORT_DESC", "RATE",
			"DISPLAY_CTP", "DISPLAY_TEMPLATE_NAME", "PTP_TYPE", "TEMPLATE_ID" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var dataKineCombo;
(function() {
	var dataKineData = [ [ '所有数据', '1' ], [ '异常数据', '2' ], [ '一般预警', '3' ], [ '次要预警', '4' ],
			[ '重要预警', '5' ] ];
	var dataKineStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'displayName'
		}, {
			name : 'dataKind'
		} ]
	});
	dataKineStore.loadData(dataKineData);
	dataKineCombo = new Ext.form.ComboBox({
		id : 'dataKineCombo',
		privilege : viewAuth,
		store : dataKineStore,
		displayField : "displayName",
		valueField : 'dataKind',
		triggerAction : 'all',
		mode : 'local',
		editable : false,
		allowBlank : false,
		value : '1',
		width : 100,
		listeners : {
			'select' : function(combo, record, index) {
				dataKineComboListener(combo, record, index);
			}
		}
	});
})();
//if(!!taskId)
//	loadmask=true;
//else
//	loadmask = false;
// 当前性能查询列表
var grid = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	stateId : stateIdvar,
//	loadMask : loadmask,
	stateful : true,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [ "-",dataKineCombo, "-", {
		icon : '../../../resource/images/btnImages/export.png',
		text : "导出",
		privilege : viewAuth,
		handler : function() {
			exportPerformanceSearch();
		}
	}, "-", {
		text : '性能趋势图',
		icon : '../../../resource/images/btnImages/chart.png',
		privilege : viewAuth,
		handler : function() {
			showPmDiagram();
		}
	},"-", {
		text : '设为基准值',
		icon : '../../../resource/images/btnImages/set_baseline.png',
		privilege : modAuth,
		handler : setPmCompare
	} ],
	bbar : pageTool
});

// ==========================函数定义==========================
function setPmCompare() {
	var cell = grid.getSelectionModel().getSelections();
	var len = cell.length;
	if (len >= 1) {
		var pmList = new Array();
		for ( var i = 0; i < len; i++) {
			if (cell[i].get("TYPE") != 1) {
				Ext.Msg.alert('信息', '请选择物理量性能项！');
				return;
			} else {
				var pm = {
					"TARGET_TYPE" : cell[i].get("TARGET_TYPE"),
					"PM_STD_INDEX" : cell[i].get("PM_STD_INDEX"),
					"PM_DESCRIPTION" : cell[i].get("PM_DESCRIPTION"),
					"PM_VALUE" : cell[i].get("PM_VALUE"),
					"BASE_OTN_CTP_ID" : cell[i].get("BASE_OTN_CTP_ID"),
					"BASE_SDH_CTP_ID" : cell[i].get("BASE_SDH_CTP_ID"),
					"BASE_PTP_ID" : cell[i].get("BASE_PTP_ID"),
					"BASE_UNIT_ID" : cell[i].get("BASE_UNIT_ID"),
					"BASE_NE_ID" : cell[i].get("BASE_NE_ID"),
					"DISPLAY_CTP" : cell[i].get("DISPLAY_CTP")
				};
				pmList.push(Ext.encode(pm));
			}
		}
		var jsonData = {
			"modifyList" : pmList
		};
		Ext.getBody().mask("正在执行,请稍候");
		Ext.Ajax.request({
			url : 'pm-search!setCompareValueFromPm.action',
			params : jsonData,
			method : 'POST',
			success : function(response) {
				Ext.getBody().unmask()
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
				}
			},
			failure : function(response) {
				Ext.getBody().unmask()
				Ext.Msg.alert("提示", "保存基准值出错!");
			},
			error : function(response) {
				Ext.getBody().unmask()
				Ext.Msg.alert("提示", "保存基准值出错!");
			}
		});
	} else {
		Ext.Msg.alert('信息', '请选择物理量性能项！');
	}
}

function getDiagramURL(type, record) {
	var url = '../../jsp/performanceManager/PMsearch/performanceDiagram.jsp?';
	var pmStdIndex = record.get('PM_STD_INDEX');
	var emsConnectionId = record.get('BASE_EMS_CONNECTION_ID');
	var targetType = record.get('TARGET_TYPE');
	var starttime = record.get('RETRIEVAL_TIME') ? (Ext.util.Format.dateRenderer('Y-m-d'))
			(new Date(record.get('RETRIEVAL_TIME').time)) : "";
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

function validateHistoryPm() {
	// 校验
	if (!searchPanel.getForm().isValid()) {
		return null;
	}
	// 选择tree中选中的节点
	var iframe = window.frames["tree_panel"];
	var selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "leaf",
			[ 0, 1, 2, 3, 4 ], "all");
	for ( var i = 0; i < selectedTargets.length; i++) {
		if (selectedTargets[i].nodeLevel != 4) {
			Ext.Msg.alert('提示', '请勿选择网管分组、网管或子网！');
			return null;
		}
	}
	selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "leaf", [ 4 ], [ "all",
			"part" ]);
	if (selectedTargets.length > 20) {
		Ext.Msg.alert('提示', '网元不超过20个！');
		return null;
	}
	var nodes = new Array();
	selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "emsId" ], "leaf");
	if (selectedTargets.length == 0) {
		Ext.Msg.alert('提示', '请选择查询的网元、板卡！');
		return null;
	}
	for ( var i = 0; i < selectedTargets.length; i++) {
		nodes.push(Ext.encode(selectedTargets[i]));
	}
	var startTime = Ext.getCmp('startTime').getValue();
	startTime = new Date(startTime.replace(/-/g, "/"));
	var endTime = Ext.getCmp('endTime').getValue();
	endTime = new Date(endTime.replace(/-/g, "/"));
	if (startTime > endTime) {
		Ext.Msg.alert('提示', '结束时间不能小于开始时间！');
		return null;
	}
	var time = endTime.getTime() - startTime.getTime();
	var days = parseInt(time / (1000 * 60 * 60 * 24));
	if (days > 100) {
		Ext.Msg.alert('提示', '历史性能查询日期间隔不超过100天！');
		return null;
	}
	return nodes;
}

function validateCurrentPm() {
	// 校验
	if (!searchPanel.getForm().isValid()) {
		return;
	}
	// 选择tree中选中的节点
	var iframe = window.frames["tree_panel"];
	var selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "leaf",
			[ 0, 1, 2, 3, 4 ], "all");
	for ( var i = 0; i < selectedTargets.length; i++) {
		if (selectedTargets[i].nodeLevel != 4) {
			Ext.Msg.alert('提示', '请勿选择网管分组、网管或子网！');
			return;
		}
	}
	selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "leaf", [ 4 ], [ "all",
			"part" ]);
	if (selectedTargets.length > 5) {
		Ext.Msg.alert('提示', '网元不超过5个！');
		return;
	}
	selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "leaf");
	if (selectedTargets.length == 0) {
		Ext.Msg.alert('提示', '请选择查询的网元、板卡！');
		return;
	}

	var nodelist = new Array();
	for ( var i = 0; i < selectedTargets.length; i++) {
		nodelist.push(Ext.encode(selectedTargets[i]));
	}
	return nodelist;
}

function checkNodesForWait(nodeInfo) {
	var iframe = window.frames["tree_panel"];
	if (iframe.checkNodes) {
		iframe.checkNodes(nodeInfo);
	} else {
		setTimeout("checkNodesForWait(\"" + nodeInfo + "\")", 500);
	}

}
function initCurrentPm(nodeInfo) {
	setTimeout("checkNodesForWait(\"" + nodeInfo + "\")", 500);
	// setTimeout("performanceSearch()",4000);
}
function initHistoryPm(nodeInfo) {
	// Ext.getCmp('startTime').setValue(startTimeString);
	// Ext.getCmp('endTime').setValue(endTimeString);
	setTimeout("checkNodesForWait(\"" + nodeInfo + "\")", 500);
	// setTimeout("performanceSearch()",4000);
}
