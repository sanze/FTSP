// ************************* 日期 ****************************
var today = new Date();
var todayStr = today.format("yyyy-MM-dd hh:mm:ss");
today.setDate(today.getDate() - 7);
var lastWeekStr = today.format("yyyy-MM-dd hh:mm:ss");
var dataCombos = {
	xtype : 'compositefield',
	fieldLabel : '查询时间段',
	labelSeparator : "：",
	items : [ {
		xtype : 'textfield',
		id : 'startTime',
		name : 'startTime',
		fieldLabel : '开始时间',
		allowBlank : false,
		width : 150,
		value : lastWeekStr,
		sideText : '<font color=red>*</font>',
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "startTime",
					isShowClear : false,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd HH:mm:ss',
					autoPickDate : true,
					maxDate:'#F{$dp.$D(\'endTime\')}'
				});
				this.blur();
			}
		}
	}, {
		xtype : 'label',
		html : '<font color=red>*</font>'
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
		xtype : 'spacer',
		width : 20
	}, {
		xtype : 'label',
		text : '至'
	}, {
		xtype : 'textfield',
		id : 'endTime',
		name : 'endTime',
		fieldLabel : '结束时间',
		allowBlank : false,
		width : 150,
		value : todayStr,
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "endTime",
					isShowClear : false,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd HH:mm:ss',
					autoPickDate : true,
					minDate:'#F{$dp.$D(\'startTime\')}'
				});
				this.blur();
			}
		}
	}, {
		xtype : 'label',
		html : '<font color=red>*</font>'
	}, {
		xtype : 'button',
		id : 'resetEndTime',
		name : 'resetEndTime',
		text : '清空',
		width : 40,
		handler : function() {
			Ext.getCmp('endTime').setValue("");
		}
	} ]

};

// ************************* 查询条件 ****************************
var searchPanel = new Ext.FormPanel({
	id : 'searchPanel',
	region : 'north',
	title : '查询条件',
	height : 290,
	bodyStyle : 'padding:20px 10px 0',
	autoScroll : true,
	collapsible : true,
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	items : [ {
		layout : 'form',
		border : false,
		labelWidth : 80,
		items : [ dataCombos, SDH ]
	} ],
	tbar : [ '-', {
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : function() {
			performanceSearch();
		}
	} ]
});

// ****************************************查询结果******************

// renderer,转化为超链接
function toTemplateRenderer(value, metadata, record) {
	var infos = {
		"PM_STD_INDEX" : record.get("PM_STD_INDEX"),
		"PM_DESCRIPTION" : record.get("PM_DESCRIPTION"),
		"UNIT" : record.get("UNIT"),
		"THRESHOLD_1" : record.get("THRESHOLD_1"),
		"THRESHOLD_2" : record.get("THRESHOLD_2"),
		"THRESHOLD_3" : record.get("THRESHOLD_3"),
		"FILTER_VALUE" : record.get("FILTER_VALUE"),
		"OFFSET" : record.get("OFFSET"),
		"UPPER_OFFSET" : record.get("UPPER_OFFSET"),
		"LOWER_OFFSET" : record.get("LOWER_OFFSET")
	};
	infos = encodeURI(encodeURI(Ext.encode(infos)));
	return ((value == null) ? "" : "<a href='#' onclick=toDetailTemplate('" + infos + "',"
			+ record.get("TYPE") + ")>" + value + "</a>");
}

// 跳转至模板信息
function toDetailTemplate(infos, TYPE) {
	var url = 'templateInfo.jsp?isCurrent=0&infos=' + infos + '&TYPE=' + TYPE;
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

// 性能查询
function performanceSearch() {
	var rate = getPmStdIndex.getRate();
	if (rate.length == 1 && rate[0] == [ 'in' ]) {
		Ext.Msg.alert('提示', '请选择查询对象TP等级！');
		return;
	}
	// if (getPmStdIndex.getSdhPmStdIndex().length == 0) {
	// Ext.Msg.alert('提示', '请选择物理量或计数值！');
	// return;
	// }
	var selected = false;
	Ext.getCmp('sdhPhysicFieldset').items.each(function(item) {
		if (item.getValue()) {
			selected = true;
		}
	});
	Ext.getCmp('sdhNumbericFieldset').items.each(function(item) {
		if (item.getValue()) {
			selected = true;
		}
	});
	if (!selected) {
		Ext.Msg.alert('提示', '请选择物理量或计数值对象！');
		return;
	}
	var nodes = validateHistoryPm();
	if (nodes == null) {
		return;
	}

	// 查询条件
	var jsonDate = {
		"userId" : userId,
		"modifyList" : nodes,
		"searchCond.startTime" : Ext.getCmp('startTime').getValue(),
		"searchCond.endTime" : Ext.getCmp('endTime').getValue(),
		"rateList" : getPmStdIndex.getRate(),
		"stringList" : getPmStdIndex.getSdhPmStdIndex(),
		"searchCond.maxMin" : Ext.getCmp('SDHMaxMin').getValue()
	};
	grid.getEl().mask("正在查询,请稍候");
	Ext.Ajax.request({
		url : "pm-search!searchHistorySdhPmDate.action",
		params : jsonDate,
		method : 'POST',
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				if (1 == result.returnResult) {
					searchTag = result.returnMessage;
					Ext.getCmp('dataKineCombo').reset();
					store.proxy = new Ext.data.HttpProxy({
						url : "pm-search!getHistorySdhPmDate.action"
					});
					store.baseParams = {
						"start" : 0,
						"limit" : 200,
						"userId" : userId,
						"searchCond.exception" : 1,
						"searchCond.searchTag" : searchTag
					};
					store.load({
						callback : function(records, options, success) {
							if (!success) {
								Ext.Msg.alert("提示", "查询出错");
							}
							grid.getEl().unmask();
						}
					});
				} else {
					grid.getEl().unmask();
					Ext.Msg.alert("提示", result.returnMessage);
				}
			}
		},
		failure : function(response) {
			grid.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			grid.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

// 显示趋势图
function showPmDiagram() {
	var cell = grid.getSelectionModel().getSelections();
	if (cell.length == 1) {
		var url = getDiagramURL(1, cell[0]);
		parent.parent.addTabPage(url, "性能趋势图");
	} else {
		Ext.Msg.alert('信息', '请选择记录，每次只能选择一条！');
	}

}

// 导出 当前性能查询
function exportPerformanceSearch() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("信息", "导出信息为空！");
		return;
	} else {
		var params = {
			"userId" : userId,
			"searchCond.exception" : dataKineCombo.getValue(),
			"searchCond.searchTag" : searchTag,
			"searchCond.tempTableName" : 3
		}
		window.location.href = "pm-search!downloadPmResult.action?" + Ext.urlEncode(params);
	}
}

// 选中性能类型后重新查询
function dataKineComboListener(combo, record, index) {
	store.proxy = new Ext.data.HttpProxy({
		url : "pm-search!getHistorySdhPmDate.action"
	});
	store.baseParams = {
		"start" : 0,
		"limit" : 200,
		"userId" : userId,
		"searchCond.exception" : combo.getValue(),
		"searchCond.searchTag" : searchTag
	};
	grid.getEl().mask("正在查询,请稍候");
	store.load({
		callback : function(records, options, success) {
			if (!success) {
				Ext.Msg.alert("提示", "查询出错");
			}
			grid.getEl().unmask();
		}
	});
}


// *****************************页面布局及初始化***********************
var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	border : false,
	layout : 'border',
	autoScroll : true,
	items : [ searchPanel, grid ]
});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 90000000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};

	// var column = cm.getColumnById("CYCLE");
	// column.hidden = true;
	// column.hideable = false;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ westPanel, centerPanel ],
		renderTo : Ext.getBody()
	});
	win.show();

	store.proxy = new Ext.data.HttpProxy({
		url : "pm-search!getHistorySdhPmDate.action"
	});
	store.baseParams = {
		"start" : 0,
		"limit" : 200,
		"userId" : userId,
		"searchCond.exception" : 1,
		"searchCond.searchTag" : searchTag
	};
	if (nodeInfo) {
		initHistoryPm(nodeInfo);
	}
});