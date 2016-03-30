var store = new Ext.data.Store({
	url : 'regular-pm-analysis!getBaseEmses.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "BASE_EMS_CONNECTION_ID", "SYS_TASK_ID", "GROUP_NAME", "SERVICE_NAME", "DISPLAY_NAME",
			"TYPE", "COLLEC_START_TIME", "COLLEC_END_TIME", "COLLECT_SOURCE", "TASK_STATUS",
			"RESULT" ])
});

// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'BASE_EMS_CONNECTION_ID',
		header : 'ID',
		dataIndex : 'BASE_EMS_CONNECTION_ID',
		hidden : true,
		hideable : false,
		width : 150
	}, {
		id : 'SYS_TASK_ID',
		header : 'ID',
		dataIndex : 'SYS_TASK_ID',
		hidden : true,
		hideable : false,
		width : 150
	}, {
		id : 'GROUP_NAME',
		header : '网管分组',
		dataIndex : 'GROUP_NAME',
		width : 150
	}, {
		id : 'SERVICE_NAME',
		header : '接入服务器',
		dataIndex : 'SERVICE_NAME',
		width : 150
	}, {
		id : 'DISPLAY_NAME',
		header : '网管',
		dataIndex : 'DISPLAY_NAME',
		width : 150
	}, {
		id : 'TYPE',
		header : '网管类型',
		dataIndex : 'TYPE',
		width : 150,
		renderer : function(v) {
			for(var type in NMS_TYPE){
				if(v==NMS_TYPE[type]['key']){
					return NMS_TYPE[type]['value'];
				}
			}
			return v;
		}
	}, {
		id : 'COLLEC_START_TIME',
		header : '<span style="font-weight:bold">允许采集开始时间</span>',
		tooltip : '可编辑列',
		dataIndex : 'COLLEC_START_TIME',
		width : 150,
		editor : new Ext.form.TextField({
			id : 'startTimeEditor',
			editable : true,
			allowBlank : false,
			regex : /^((0?[0-9]|1[0-9]|2[0-3]):[0-5][0-9])$|^00:00$/
		})
	}, {
		id : 'COLLEC_END_TIME',
		header : '<span style="font-weight:bold">允许采集结束时间</span>',
		tooltip : '可编辑列',
		dataIndex : 'COLLEC_END_TIME',
		width : 150,
		editor : new Ext.form.TextField({
			id : 'endTimeEditor',
			editable : true,
			allowBlank : false,
			regex : /^((0?[0-9]|1[0-9]|2[0-3]):[0-5][0-9])$|^00:00$/
		}),
		renderer : function(v, metadata, record) {
			startTime = record.get('COLLEC_START_TIME');
			if (startTime > v) {
				return '次日 ' + v;
			} else {
				return v;
			}
		}
	// new Ext.form.TimeField({
	// format : 'H:i',
	// increment : 30,
	// allowBlank : false,
	// editable : true,
	// autoSelect : false,
	// listeners : {
	// 'select' : function(combo, record, index) {
	// var endTime = combo.getValue();
	// var cell = gridPanel.getSelectionModel().getSelections();
	// var startTime = cell[0].get("COLLEC_START_TIME");
	// if (endTime <= startTime) {
	// Ext.Msg.alert("提示", "结束时间应晚于开始时间！");
	// combo.setValue("");
	// }
	// }
	// }
	// })
	}, {
		id : 'COLLECT_SOURCE',
		header : '<span style="font-weight:bold">采集数据类型</span>',
		tooltip : '可编辑列',
		dataIndex : 'COLLECT_SOURCE',
		renderer : formatSource,
		width : 150,
		editor : new Ext.form.ComboBox({
			store : new Ext.data.ArrayStore({
				id : 0,
				fields : [ 'value', 'display' ],
				data : [ [ 1, '当前性能' ], [ 2, '历史性能' ] ]
			}),
			valueField : 'value',
			displayField : 'display',
			mode : 'local',
			triggerAction : 'all',
			editable : false,
			allowBlank : false
		})
	}, {
		id : 'TASK_STATUS',
		header : '任务状态',
		dataIndex : 'TASK_STATUS',
		renderer : formatStatus,
		width : 150
	}, {
		id : 'RESULT',
		header : '执行状态',
		dataIndex : 'RESULT',
		renderer : formatResult,
		width : 150
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

emsGroupCombo.value = "全部";
emsGroupCombo.addListener('select', function(combo, record, index) {
	var jsonData = {
		"start" : 0,
		"limit" : 200,
		"emsGroupId" : combo.getValue(),
		"userId" : userId
	};
	store.baseParams = jsonData;
	store.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "查询出错");
		}
	});
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	tbar : [ '-', "网管分组：", emsGroupCombo, '-', {
		text : '启用',
		icon : '../../../resource/images/btnImages/control_play.png',
		privilege : actionAuth,
		handler : function() {
			changeTaskStatus(1);
		}
	}, {
		text : '挂起',
		icon : '../../../resource/images/btnImages/control_stop.png',
		privilege : actionAuth,
		handler : function() {
			changeTaskStatus(2);
		}
	}, "-", {
		text : '保存',
		icon : '../../../resource/images/btnImages/disk.png',
		privilege : modAuth,
		handler : function() {
			saveTask();
		}
	}, "-", {
		text : '设置',
		icon : '../../../resource/images/btnImages/modify.png',
		privilege : modAuth,
		handler : function() {
			setTask();
		}
	}, "-", {
		text : '执行状态',
		icon : '../../../resource/images/btnImages/setTask.png',
		privilege : viewAuth,
		handler : function() {
			showStatus();
		}
	} ],
	// viewConfig : {
	// forceFit : true
	// },
	listeners : {
		// 'beforeedit' : function(e) {
		// var field = e.field;
		// var length = e.value.length;
		// if (field == 'COLLEC_END_TIME' && length > 5) {
		// alert(e.value.substring(3, length));
		// e.value = e.value.substring(3, length);
		// //Ext.getCmp('endTimeEditor').setValue(e.value.substring(3, length));
		// //record.set("COLLEC_END_TIME", e.value.substring(3, length));
		// }
		// },
		'afteredit' : function(e) {
			var record = e.record;
			var field = e.field;
			if (e.value.length == 4) {
				record.set(field, '0' + e.value);
			}
			if (field == 'COLLEC_END_TIME') {
				if (record.get('COLLEC_START_TIME') == record.get('COLLEC_END_TIME')) {
					// record.set("COLLEC_END_TIME", '次日 ' + e.value);
					Ext.Msg.alert("提示", "开始时间和结束时间不能相同！");
					record.set(field, e.originalValue);
				}
			}

		}
	}

});

// 格式化采集数据源
function formatSource(value) {
	if (value == 1) {
		return '当前性能';
	} else if (value == 2) {
		return '历史性能';
	}
};

// 格式化采集状态
function formatStatus(value) {
	if (value == 1) {
		return '启用';
	} else if (value == 2) {
		return '挂起';
	} else if (value == 3) {
		return '删除';
	} else {
		return value;
	}
};

// 格式化执行状态
function formatResult(value) {
	if (value == 1) {
		return '执行成功';
	} else if (value == 2) {
		return '执行失败';
	} else if (value == 3) {
		return '正在执行';
	} else if (value == 4) {
		return '部分成功';
	} else if (value == 5) {
		return '暂停';
	} else if (value == 6) {
		return '部分成功';
	} else {
		return value;
	}
};

// 更改任务状态
function changeTaskStatus(statusFlag) {
	var emsIds = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	var msg = null;

	if (cell.length >= 1) {
		var emsList = new Array();
		for ( var i = 0; i < cell.length; i++) {
			var ems = {
				"SYS_TASK_ID" : cell[i].get("SYS_TASK_ID"),
				"TASK_STATUS" : statusFlag
			};
			emsList.push(Ext.encode(ems));
		}
		var jsonData = {
			"modifyList" : emsList
		};

		if (statusFlag == 2) {
			var taskIdList = new Array();
			for ( var i = 0; i < cell.length; i++) {
				taskIdList.push(cell[i].get("SYS_TASK_ID"));
			}
			var jsonData2 = {
				"modifyList" : taskIdList
			};
			Ext.Ajax.request({
				url : 'regular-pm-analysis!getTasksIsRunning.action',
				params : jsonData2,
				method : 'POST',
				success : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if (result && result.returnResult) {
						if (1 == result.isRunning) {
							msg = '选择的任务正在执行中，<br>确认终止执行并挂起任务？';
						} else {
							msg = '是否要挂起性能采集任务？';
						}
						Ext.Msg.confirm('信息', msg, function(btn) {
							if (btn == 'yes') {
								doChangeTaskStatus(jsonData);
							} else if (statusFlag == 2 && 1 == result.isRunning) {
								pageTool.doLoad(pageTool.cursor);
							}
						});
					} else {
						Ext.Msg.alert("提示", "查询执行状态出错");
					}
				},
				failure : function(response) {
					gridPanel.getEl().unmask();
					Ext.Msg.alert("提示", "查询执行状态出错");
				},
				error : function(response) {
					gridPanel.getEl().unmask();
					Ext.Msg.alert("提示", "查询执行状态出错");
				}
			});
		} else if (statusFlag == 1) {
			msg = '是否要启用性能采集任务？';
			Ext.Msg.confirm('信息', msg, function(btn) {
				if (btn == 'yes') {
					doChangeTaskStatus(jsonData);
				} else if (statusFlag == 2) {
					pageTool.doLoad(pageTool.cursor);
				}
			});
		}

	} else if (statusFlag == 2) {
		Ext.Msg.alert("提示", "请选择需要挂起的采集任务！");
	} else if (statusFlag == 1) {
		Ext.Msg.alert("提示", "请选择需要启用的采集任务！");
	}
}

function doChangeTaskStatus(jsonData) {
	gridPanel.getEl().mask("正在执行,请稍候");
	Ext.Ajax.request({
		url : 'regular-pm-analysis!changeTaskStatus.action',
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
			Ext.Msg.alert("提示", "更改执行状态出错");
		},
		error : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("提示", "更改执行状态出错");
		}
	});
}

// 保存
function saveTask() {
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {
		gridPanel.getEl().mask("正在执行,请稍候...");
		var emsList = new Array();
		for ( var i = 0; i < cell.length; i++) {
			var ems = {
				'BASE_EMS_CONNECTION_ID' : cell[i].get('BASE_EMS_CONNECTION_ID'),
				'SYS_TASK_ID' : cell[i].get('SYS_TASK_ID'),
				'COLLEC_START_TIME' : cell[i].get('COLLEC_START_TIME'),
				'COLLEC_END_TIME' : cell[i].get('COLLEC_END_TIME'),
				'COLLECT_SOURCE' : cell[i].get('COLLECT_SOURCE')
			};
			emsList.push(Ext.encode(ems));
		}
		var jsonData = {
			"modifyList" : emsList
		};
		// 提交修改，不然store.getModifiedRecords();数据会累加
		store.commitChanges();
		Ext.Ajax.request({
			url : 'regular-pm-analysis!modifyEmses.action',
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
				Ext.Ajax.request({
					url : 'pm-report!controlReportTaskTime.action',
					method : 'POST'
				});
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

// 设置任务内容
function setTask() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var url = 'settingTask.jsp?emsId=' + cell[0].get("BASE_EMS_CONNECTION_ID");// +"&authSequence=all";
		var setTaskWin = new Ext.Window({
			id : 'setTaskWin',
			title : '采集任务设置',
			width : 850,
			height : 400,
			isTopContainer : true,
			modal : true,
			plain : true, // 是否为透明背景
			html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />'
		});
		setTaskWin.show();
		// if (setTaskWin.getHeight() > Ext.getCmp('win').getHeight()) {
		setTaskWin.setHeight(Ext.getCmp('win').getHeight() * 0.9);
		setTaskWin.setWidth(Ext.getCmp('win').getWidth() * 0.9);
		// }
		setTaskWin.center();
		setTaskWin.doLayout();
	} else {
		Ext.Msg.alert("提示", "请先选取任务，只能选择一条！");
	}
}
// 执行状态
function showStatus() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var url = '../../jsp/performanceManager/regularPMAnalysisManager/collectingStatus.jsp?emsId=' + cell[0].get("BASE_EMS_CONNECTION_ID")
				+ '&taskId=' + cell[0].get("SYS_TASK_ID");
		var title = '采集任务执行状态('+cell[0].get('DISPLAY_NAME')+')';
		parent.addTabPage(url, title,authSequence);
	} else {
		Ext.Msg.alert("提示", "请先选取任务，只能选择一条！");
	}

}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = parent.Ext.menu.MenuMgr.hideAll;
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();

	store.baseParams = {
		"start" : 0,
		"limit" : 200,
		"emsGroupId" : -99,
		"userId" : userId
	};
	store.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "加载失败");
		}
	});
});
