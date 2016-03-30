// ************************* 种类设置 15min/1day ****************************
var dateType = new Ext.form.RadioGroup({
	fieldLabel : '数据种类 ',
	labelSeparator : "：",
	width : 200,
	id : 'PMTypeGroup',
	name : 'PMTypeGroup',
	items : [ {
		name : 'PMType',
		inputValue : 1,
		boxLabel : '15 min',
		checked : true
	}, {
		name : 'PMType',
		inputValue : 2,
		boxLabel : '24 hour'

	} ]
})

// ************************* 查询条件 ****************************
var searchPanel = new Ext.FormPanel({
	id : 'searchPanel',
	region : 'north',
	title : '查询条件',
	height : 290,
	bodyStyle : 'padding:20px 10px 0',
	collapsible : true,
	autoScroll : true,
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	items : [ {
		layout : 'form',
		border : false,
		labelWidth : 70,
		items : [ dateType, SDH ]
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

// *****************************查询结果***********************

// renderer,转化为超链接
function toTemplateRenderer(value, metadata, record) {
	return ((value == null) ? "" : "<a href='#' onclick=toDetailTemplate("
			+ record.get("TEMPLATE_ID") + "," + record.get("TYPE") + ",'"
			+ record.get("PM_STD_INDEX") + "')>" + value + "</a>");
}

// 跳转至模板信息
function toDetailTemplate(TEMPLATE_ID, TYPE, PM_STD_INDEX) {
	var url = 'templateInfo.jsp?TEMPLATE_ID=' + TEMPLATE_ID + '&TYPE=' + TYPE + '&PM_STD_INDEX='
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

// 性能查询
function performanceSearch() {
	var nodelist = validateCurrentPm();
	if (nodelist == null) {
		return;
	}
	if (searchPanel.form.findField('PMType').getGroupValue() == null) {
		Ext.Msg.alert('提示', '请选择数据种类！');
		return;
	}
	var rate = getPmStdIndex.getRate();
	if (rate.length == 1 && rate[0] == [ 'in' ]) {
		Ext.Msg.alert('提示', '请选择查询对象TP等级！');
		return;
	}
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
	// 查询条件
	Ext.Msg.confirm('提示', '该操作将耗费较长时间,是否继续?', function(btn) {
		if (btn == 'yes') {
			var jsonDate = {
				"userId" : userId,
				"modifyList" : nodelist,
				"rateList" : rate,
				"searchCond.granularity" : searchPanel.form.findField('PMType').getGroupValue(),
				"stringList" : getPmStdIndex.getSdhPmStdIndex(),
				"searchCond.maxMin" : Ext.getCmp('SDHMaxMin').getValue()
			};
			grid.getEl().mask("正在查询,请稍候");
			Ext.Ajax.request({
				url : "pm-search!searchCurrentSdhPmDate.action",
				params : jsonDate,
				method : 'POST',
				success : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if (result) {
						if (1 == result.returnResult) {
							if (result.returnMessage != "" && result.returnMessage != null) {
								Ext.Msg.alert("提示", "部分网元出错！<br>" + result.returnMessage);
							}
							searchTag = result.searchTag;
							Ext.getCmp('dataKineCombo').reset();
							store.proxy = new Ext.data.HttpProxy({
								url : "pm-search!getCurrentSdhPmDate.action"
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
			"searchCond.tempTableName" : 1
		}
		window.location.href = "pm-search!downloadPmResult.action?" + Ext.urlEncode(params);
	}
}

// 选中性能类型后重新查询
function dataKineComboListener(combo, record, index) {
	store.proxy = new Ext.data.HttpProxy({
		url : "pm-search!getCurrentSdhPmDate.action"
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
	// Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ westPanel, centerPanel ],
		renderTo : Ext.getBody()
	});
	win.show();

	store.proxy = new Ext.data.HttpProxy({
		url : "pm-search!getCurrentSdhPmDate.action"
	});
	store.baseParams = {
		"start" : 0,
		"limit" : 200,
		"userId" : userId,
		"searchCond.exception" : 1,
		"searchCond.searchTag" : searchTag
	};

	if (nodeInfo) {
		initCurrentPm(nodeInfo);
	}

});
