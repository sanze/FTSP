// ---------for gridPanel init---------
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : false
});
sm.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [
			new Ext.grid.RowNumberer({
				width:26,
				locked : true
			}),
			sm,

			{
				id : 'DISPLAY_NE',
				header : '网元',
				dataIndex : 'DISPLAY_NE',
				width : 80
			},

			{
				id : 'DISPLAY_PORT_DESC',
				header : '端口',
				dataIndex : 'DISPLAY_PORT_DESC',
				width : 150
			},
//			{
//				id : 'PTP_TYPE',
//				header : '端口类型',
//				dataIndex : 'PTP_TYPE',
//				hidden:true,
//				width : 60
//			},
//			{
//				id : 'RATE',
//				header : '速率',
//				dataIndex : 'RATE',
//				hidden:true,
//				width : 50
//			},
//			{
//				id : 'DISPLAY_CTP',
//				header : '通道',
//				dataIndex : 'DISPLAY_CTP',
//				hidden:true,
//				width : 100
//			},
			{
				id : 'PM_DESCRIPTION',
				header : '性能事件',
				dataIndex : 'PM_DESCRIPTION',
				
				width : 130
			},
//			{
//				id : 'LOCATION',
//				header : '方向',
//				dataIndex : 'LOCATION',
//				hidden:true,
//				width : 60,
//				renderer : function(v) {
//					switch (v) {
//					case 1:
//						return "近端接收";
//					case 2:
//						return "远端接收";
//					case 3:
//						return "近端发送";
//					case 4:
//						return "远端发送";
//					case 5:
//						return "双向";
//					default:
//						return null;
//					}
//				}
//			},
			{
				id : 'VALUE_BEFORE',
				header : '割接前快照值',
				dataIndex : 'VALUE_BEFORE',
				width : 150
			},
			{
				id : 'VALUE_AFTER',
				header : '割接后快照值',
				dataIndex : 'VALUE_AFTER',
				width : 150
			},
			{
				id : 'DIFFERENCE',
				header : '差值',
				dataIndex : 'DIFFERENCE',
				width : 150,renderer : function(v, metadata, record) {
					exLv = record.get('level');
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
//			{
//				id : 'level',
//				header : '割接level快照值',
//				dataIndex : 'level',
//				width : 150
//			},
//			{
//				id : 'PM_COMPARE_VALUE',
//				header : '性能基准值',
//				dataIndex : 'PM_COMPARE_VALUE',
//				hidden:true,
//				width : 65
//			},
//			{
//				id : 'EXCEPTION_COUNT',
//				header : '连续异常',
//				dataIndex : 'EXCEPTION_COUNT',
//				hidden:true,
//				width : 60
//			},

			{
				id : 'TIME_BEFORE',
				header : '割接前快照时间',
				dataIndex : 'TIME_BEFORE',
				width : 120
			},{
				id : 'TIME_AFTER',
				header : '割接后快照时间',
				dataIndex : 'TIME_AFTER',
				width : 120
				
			},{
				id : 'EVALUATION_SCORE',
				header : '评估量化值',
				dataIndex : 'EVALUATION_SCORE',
				width : 120
				
			} ]
});

var store = new Ext.data.Store({
	url : "cutover-task!searchPmValue.action",
	baseParams: {
		"start" : 0,
		"limit" : 200,
		"searchCondition.cutoverTaskId" : cutoverTaskId
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "ID", "BASE_EMS_CONNECTION_ID", "BASE_NE_ID", "BASE_RACK_ID", "BASE_SHELF_ID",
			"BASE_SLOT_ID", "BASE_SUB_SLOT_ID", "BASE_UNIT_ID", "BASE_SUB_UNIT_ID", "BASE_PTP_ID",
			"BASE_OTN_CTP_ID", "BASE_SDH_CTP_ID", "TARGET_TYPE", "LAYER_RATE", "PM_STD_INDEX",
			"PM_INDEX", "PM_VALUE", "PM_COMPARE_VALUE", "TYPE", "THRESHOLD_1", "THRESHOLD_2",
			"THRESHOLD_3", "OFFSET", "UPPER_VALUE", "UPPER_OFFSET", "LOWER_VALUE", "LOWER_OFFSET",
			"PM_DESCRIPTION", "LOCATION", "UNIT", "GRANULARITY", "EXCEPTION_LV", "EXCEPTION_COUNT",
			"RETRIEVAL_TIME", "DISPLAY_EMS_GROUP", "DISPLAY_EMS", "DISPLAY_SUBNET", "DISPLAY_NE",
			"DISPLAY_AREA", "DISPLAY_STATION", "DISPLAY_PRODUCT_NAME", "DISPLAY_DOMAIN",
			"DISPLAY_PORT_DESC", "RATE", "DISPLAY_CTP", "DISPLAY_TEMPLATE_NAME", "PTP_TYPE",
			"TEMPLATE_ID","VALUE_BEFORE","VALUE_AFTER","DIFFERENCE","TIME_BEFORE","TIME_AFTER","level","EVALUATION_SCORE" ])
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
	var dataKineData = [ [ '显示所有数据', '1' ], [ '显示所有异常数据', '2' ], [ '显示一般预警数据', '3' ],
			[ '显示次要预警数据', '4' ], [ '显示重要预警数据', '5' ] ];
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

// 当前性能查询列表
var grid = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	loadMask: {msg:"正在查询,请稍候"},
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-',{
		icon : '../../../resource/images/btnImages/export.png',
		text : "导出",
		handler : function() {
			exportPerformanceSearch();
		}
	} ],
	bbar : pageTool
});


// *****************************查询结果***********************






// 性能查询
function performanceSearch() {
	var nodelist = validateCurrentPm();
	if (nodelist == null) {
		return;
	}
	// 查询条件
	Ext.Msg.confirm('提示', '该操作将耗费较长时间,是否继续?', function(btn) {
		if (btn == 'yes') {
			var jsonDate = {
				"userId" : userId,
				"modifyList" : nodelist,
				"rateList" : getPmStdIndex.getRate(),
				"searchCond.granularity" : searchPanel.form.findField('PMType').getGroupValue(),
				"stringList" : getPmStdIndex.getSdhPmStdIndex()
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
							searchTag = result.returnMessage;
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


// 导出 当前性能查询
function exportPerformanceSearch() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	}
//	else if (store.getTotalCount() > 2000) {
//		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
//			if (btn == 'yes') {
//				{
//					exportRequest();
//				}
//			}
//		});
	else exportRequest();
}
var exportRequest=function(){
	grid.getEl().mask("正在导出...");
	var exportData = {
		"searchCondition.flag":"1",
		"cutoverTaskId" : cutoverTaskId
	}
	Ext.Ajax.request({
		url : 'cutover-task!downloadResult.action',
		type : 'post',
		params : exportData,
		success : function(response) {
			grid.getEl().unmask();
			var rs=Ext.decode(response.responseText);
			if(rs.returnResult==1&&rs.returnMessage!=""){
				var destination={
						"filePath":rs.returnMessage
				};
				window.location.href="download!execute.action?"+Ext.urlEncode(destination);
			}
			else {
				grid.getEl().unmask();
				Ext.Msg.alert("提示","导出失败！");
			}
		},
		error : function(response) {
			grid.getEl().unmask();
			Ext.Msg.alert("提示", response.responseText);
		},
		failure : function(response) {
			grid.getEl().unmask();
			Ext.Msg.alert("提示", response.responseText);
		}
	});
};


// *****************************页面布局及初始化***********************
var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	border : false,
	layout : 'border',
	autoScroll : true,
	items : [ grid ]
});
//刷新grid列表
window.refresh = function(){
	//刷新列表
	var pageTool = Ext.getCmp('pageTool');
	if(pageTool){
		//grid.getEl().mask("正在查询,请稍候");
		pageTool.doLoad(pageTool.cursor);
		//grid.getEl().unmask();
	}
}
Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	// Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [centerPanel ],
		renderTo : Ext.getBody()
	});
	win.show();
	
	//grid.getEl().mask("正在查询,请稍候");
	store.load();
});