/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

/*
 * store.proxy = new Ext.data.HttpProxy({ url : 'getTaskInfoList.action' });
 * store.baseParams = { "taskInfoModel.taskId" : taskId }; store.load({ callback :
 * function(r, options, success) { if (success) { } else { Ext.Msg.alert('错误',
 * '查询失败！请重新查询'); } } });
 */

var westPanel;
(function() {
	var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		leafType : 4
	};
	var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
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
		html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0 />'
	});
})();

// ************************* 模板列表 ****************************
var store = new Ext.data.Store({
	url : 'regular-pm-analysis!getCompareValueByPage.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "PM_COMPARE_ID", "PM_DESCRIPTION", "PM_COMPARE_VALUE", "DISPLAY_CTP", "UPDATE_TIME",
			"PM_STD_INDEX", "TARGET_TYPE", "GROUP_NAME", "EMS_DISPLAY_NAME", "SUBNET_DISPLAY_NAME",
			"NE_DISPLAY_NAME", "PRODUCT_NAME", "AREA_NAME", "STATION_NAME", "PTP_TYPE",
			"PORT_DESC", "DOMAIN", "RATE", "MAX_OUT", "MIN_OUT", "MAX_IN", "MIN_IN", "UNIT_DESC",
			"UPPER", "LOWER", "UPPER_OFFSET", "LOWER_OFFSET", "OFFSET" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var compareValueField = new Ext.form.TextField({
	editable : true,
	allowBlank : false
});

var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
	// columns are not sortable by default
	},
	stateId : "setCompareValueGridId",
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),
			{
				id : 'ID',
				header : 'ID',
				dataIndex : 'PM_COMPARE_ID',
				width : 100,
				hidden : true,
				hideable : false
			},
			{
				id : 'GROUP_NAME',
				header : '网管分组',
				dataIndex : 'GROUP_NAME',
				width : 100,
				hidden : true
			},
			{
				id : 'EMS_DISPLAY_NAME',
				header : '网管',
				dataIndex : 'EMS_DISPLAY_NAME',
				width : 100,
				hidden : true
			},
			{
				id : 'SUBNET_DISPLAY_NAME',
				header : '子网',
				dataIndex : 'SUBNET_DISPLAY_NAME',
				width : 100,
				hidden : true
			},
			{
				id : 'NE_DISPLAY_NAME',
				header : '网元',
				dataIndex : 'NE_DISPLAY_NAME',
				width : 100
			},
			{
				id : 'AREA_NAME',
				header : top.FieldNameDefine.AREA_NAME,
				dataIndex : 'AREA_NAME',
				width : 80,
				hidden : true
			},
			{
				id : 'STATION_NAME',
				header : top.FieldNameDefine.STATION_NAME,
				dataIndex : 'STATION_NAME',
				width : 80,
				hidden : true
			},
			{
				id : 'PRODUCT_NAME',
				header : '网元型号',
				dataIndex : 'PRODUCT_NAME',
				width : 140,
				hidden : true
			},
			{
				id : 'PORT_DESC',
		header : '板卡/端口',
				dataIndex : 'PORT_DESC',
		width : 150,
		renderer : function(v,m,r){
			if(r.get("TARGET_TYPE")==6){
				return r.get("UNIT_DESC");
			}else{
				return v;
			}
		}
	}, {
				id : 'DOMAIN',
				header : '业务类型',
				dataIndex : 'DOMAIN',
				width : 60,
				hidden : true,
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
				width : 60,
				hidden : true
			},
			{
				id : 'DISPLAY_CTP',
				header : '通道号',
				dataIndex : 'DISPLAY_CTP',
				width : 80
			},
			{
				id : 'PM_DESCRIPTION',
				header : '性能事件',
				dataIndex : 'PM_DESCRIPTION',
				width : 130
			},
			{
				id : 'PM_COMPARE_VALUE',
				tooltip : '可编辑列',
				header : '<span style="font-weight:bold">基准值</span>',
				dataIndex : 'PM_COMPARE_VALUE',
				width : 80,
				editor : compareValueField
			// new Ext.form.NumberField({
			// allowBlank : false,
			// allowNegative : true,
			// allowDecimals : true,
			// decimalPrecision : 2,
			// maxLenth : 100
			// })
			},{
		id : 'offset',
		header : '基准值偏差',
		dataIndex : 'OFFSET',
		width : 120
	}, {
		id : 'declaredUpper',
		header : '标称上限',
		dataIndex : 'declaredUpper',
		width : 120,
		renderer : function(v,m,r){
			var pmStdIndex = r.get("PM_STD_INDEX");
			switch (pmStdIndex) {
				case 'TPL_AVG':
				case 'TPL_CUR':
				case 'TPL_MAX':
				case 'TPL_MIN':
					return r.get("MAX_OUT");
				case 'RPL_AVG':
				case 'RPL_CUR':
				case 'RPL_MAX':
				case 'RPL_MIN':
					return r.get("MAX_IN");
			}
			return r.get("UPPER");
		}
	}, {
		id : 'upperOffset',
		header : '上限值偏差',
		dataIndex : 'UPPER_OFFSET',
		width : 120
	}, {
		id : 'declaredLower',
		header : '标称下限',
		dataIndex : 'declaredLower',
		width : 120,
		renderer : function(v,m,r){
			var pmStdIndex = r.get("PM_STD_INDEX");
			switch (pmStdIndex) {
				case 'TPL_AVG':
				case 'TPL_CUR':
				case 'TPL_MAX':
				case 'TPL_MIN':
					return r.get("MIN_OUT");
				case 'RPL_AVG':
				case 'RPL_CUR':
				case 'RPL_MAX':
				case 'RPL_MIN':
					return r.get("MIN_IN");
			}
			return r.get("LOWER");
		}
	}, {
		id : 'lowerOffset',
		header : '下限值偏差',
		dataIndex : 'LOWER_OFFSET',
		width : 120
	},
			{
				id : 'MAX_OUT',
				header : '输出最大光功率',
				dataIndex : 'MAX_OUT',
				width : 100
			},
			{
				id : 'MIN_OUT',
				header : '输出最小光功率',
				dataIndex : 'MIN_OUT',
				width : 100
			},
			{
				id : 'MAX_IN',
				header : '过载点',
				dataIndex : 'MAX_IN',
				width : 80
			},
			{
				id : 'MIN_IN',
				header : '灵敏度',
				dataIndex : 'MIN_IN',
				width : 80
			},
			{
				id : 'UPDATE_TIME',
				header : '更新时间',
				dataIndex : 'UPDATE_TIME',
				width : 120,
				renderer : function(value) {
					return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
							value.time)) : "";
				}
			} ]
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var gridPanel = new Ext.grid.EditorGridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	stateId : "setCompareValueGridId",
	stateful : true,
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [ '-', {
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchCompareValue
	}, "-", {
		text : '生成基准值',
		privilege : addAuth,
		icon : '../../../resource/images/btnImages/set_baseline.png',
		menu : {
			items : [ {
				text : '不覆盖原基准值',
				privilege : addAuth,
				handler : generateCompareValue(0)
			}, {
				text : '覆盖原基准值',
				privilege : addAuth,
				handler : generateCompareValue(1)
			} ]
		}
	}, "-", {
		text : '保存',
		icon : '../../../resource/images/btnImages/disk.png',
		privilege : modAuth,
		handler : saveCompareValue
	} ],
	bbar : pageTool,
	listeners : {
		'beforeedit' : function(e) {
			var pmStdIndex = e.record.get("PM_STD_INDEX");
			var regex;
			switch (pmStdIndex) {
			case 'RPL_CUR':
			case 'RPL_MAX':
			case 'RPL_MIN':
			case 'TPL_CUR':
			case 'TPL_MAX':
			case 'TPL_MIN':
			case 'RPL_AVG':
			case 'TPL_AVG':
			case 'PCLSOP_CUR':
			case 'PCLSOP_MAX':
			case 'PCLSOP_MIN':
				regex = /^(((-)?\d{1,2}.\d{1,2})|((-)?\d{1,2}))$/;
				break;
			case 'PCLSWL_CUR':
			case 'PCLSWL_MAX':
			case 'PCLSWL_MIN':
			case 'PCLSWLO_CUR':
			case 'PCLSWLO_MAX':
			case 'PCLSWLO_MIN':
				regex = /^((\d{1,4}.\d{1,2})|(\d{1,4}))$/;
				break;
			case 'PCLSSNR_CUR':
			case 'PCLSSNR_MAX':
			case 'PCLSSNR_MIN':
				regex = /^((\d{1,2}.\d{1})|(\d{1,2}))$/;
				break;
			case 'FEC_BEF_COR_ER':
			case 'FEC_AFT_COR_ER':
				regex = /^1E-(\d)+$/
			default:
				break;
			}
			compareValueField.regex = regex;
		}
	}
});

function validationNode() {
	// console.log("validationNode");
	// 节点校验
	var iframe = window.frames["tree_panel"];
	var selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "leaf", [ 0, 1, 2 ],
			"all");
	for ( var i = 0; i < selectedTargets.length; i++) {
		if (selectedTargets[i].nodeLevel == 1) {
			Ext.Msg.alert('提示', '请不要选择网管分组！');
			return null;
		}
	}
	selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "leaf", [ 2 ], [ "all",
			"part" ]);
	if (selectedTargets.length > 1) {
		Ext.Msg.alert('提示', '最多选择一个网管！');
		return null;
	}
	// 选择tree中选中的节点
	selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "emsId" ], "leaf");
	if (selectedTargets.length == 0) {
		Ext.Msg.alert('提示', '请选择节点！');
		return null;
	}
	var nodelist = new Array();
	for ( var i = 0; i < selectedTargets.length; i++) {
		nodelist.push(Ext.encode(selectedTargets[i]));
	}
	return nodelist;
}

function searchCompareValue() {
	var nodelist = validationNode();
	if (nodelist == null) {
		return;
	}
	store.baseParams = {
		"start" : 0,
		"limit" : 200,
		"modifyList" : nodelist
	};
	gridPanel.getEl().mask("正在查询,请稍候");
	store.load({
		callback : function(records, options, success) {
			if (!success) {
				Ext.Msg.alert("提示", "查询出错");
			}
			gridPanel.getEl().unmask();
		}
	});
}

function generateCompareValue(overwrite) {
	function generate(nodelist) {
		if (nodelist == null) {
			return;
		}
		// 进度条
		var processKey = "saveCompareValue" + new Date().getTime();
		showProcessBar(processKey);
		// 进度条
		var jsonData = {
			"modifyList" : nodelist,
			"searchCond.overwrite" : overwrite,
			"searchCond.processKey" : processKey
		};
		// gridPanel.getEl().mask("正在执行,请稍候...");
		Ext.Ajax.request({
			url : 'regular-pm-analysis!generateCompareValue.action',
			params : jsonData,
			method : 'POST',
			success : function(response) {
				// gridPanel.getEl().unmask();

				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					if (result.returnMessage.indexOf("\n") == -1) {
						Ext.Msg.alert("提示", result.returnMessage);
					} else {
						Ext.Msg.hide();
						var errorWin = new Ext.Window({
							id : 'errorWin',
							title : '提示',
							width : 320,
							height : 140,
							isTopContainer : true,
							modal : true,
							plain : true, // 是否为透明背景
							bodyStyle : 'padding:5px 13px 10px 5px;',
							border : false,
							items : [ {
								xtype : 'textarea',
								readOnly : true,
								autoScroll : true,
								value : result.returnMessage,
								width : '100%',
								height : '100%'
							} ],
							buttons : [ {
								text : '确定',
								handler : function() {
									errorWin.close();
								}
							} ]
						});
						errorWin.show();
					}
					if (1 == result.returnResult) {
						searchCompareValue();
					}
					clearTimer();
				}
			},
			failure : function(response) {
				clearTimer();
				// gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "更新比较值出错");
			},
			error : function(response) {
				clearTimer();
				// gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "更新比较值出错");
			}
		});
	}
	if (overwrite) {
		return function() {
			// console.log("覆盖");
			var nodelist = validationNode();
			if (nodelist) {
				Ext.Msg.confirm('提示',
						'<font color="red">此操作将会覆盖原基准值数据</font>，<br>并且需要等待较长时间，是否继续？',
						function(btn) {
							if (btn == 'yes') {
								generate(nodelist);
							}
						});
			}

		};
	} else {
		return function() {
			// console.log("不覆盖");
			var nodelist = validationNode();
			if (nodelist) {
				Ext.Msg.confirm('提示', '此操作需要等待较长时间，是否继续？', function(btn) {
					if (btn == 'yes') {
						generate(nodelist);
					}
				});
			}

		};
	}
}

function saveCompareValue() {
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {
		gridPanel.getEl().mask("正在执行,请稍候...");
		var compareValueList = new Array();
		for ( var i = 0; i < cell.length; i++) {
			var compareValue = {
				'PM_COMPARE_ID' : cell[i].get('PM_COMPARE_ID'),
				'PM_COMPARE_VALUE' : cell[i].get('PM_COMPARE_VALUE')
			};
			compareValueList.push(Ext.encode(compareValue));
		}
		var jsonData = {
			"modifyList" : compareValueList
		};
		// 提交修改，不然store.getModifiedRecords();数据会累加
		store.commitChanges();
		Ext.Ajax.request({
			url : 'regular-pm-analysis!modifyCompareValues.action',
			params : jsonData,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
					if (1 == result.returnResult) {
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					}
				}
			},
			failure : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			},
			error : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			}
		});
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel, westPanel ]
	});
	win.show();

});
