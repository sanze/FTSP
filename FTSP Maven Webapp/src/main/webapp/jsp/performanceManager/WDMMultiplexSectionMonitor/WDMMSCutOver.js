var today = new Date();
var todayStr = today.format("yyyy-MM-dd");
today.setDate(today.getDate()-7);
var lastWeekStr = today.format("yyyy-MM-dd");
var fromTime = {
	xtype : 'textfield',
	id : 'fromTime',
	name : 'fromTime',
	fieldLabel : '从：',
	width : 130,
	value: lastWeekStr,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "fromTime",
				isShowClear : false,
				readOnly : true,
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
//				maxDate : '%y-%M-%d'
			});
			this.blur();
		}
	}
};
var toTime = {
	xtype : 'textfield',
	id : 'toTime',
	name : 'toTime',
	fieldLabel : '至：',
	width : 130,
	value: todayStr,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "toTime",
				isShowClear : false,
				readOnly : true,
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
//				maxDate : '%y-%M-%d'
			});
			this.blur();
		}
	}
};

var taskNameStore =  new Ext.data.Store({
	url : 'ms-cutover!loadTaskNameCombo.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "taskName", "taskId" ])
});

function loadTaskNameCombo(){
	var params = {
			'searchCond.taskType':TASK_TYPE
	};
	taskNameStore.baseParams = params;
	taskNameStore.load();
}
loadTaskNameCombo();

var store = new Ext.data.Store({
	url : 'ms-cutover!searchCutoverTask.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "taskName", "startTime", "endTime", "creator", "createTime",
			"description", "cutoverTaskId" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer(), sm, {
		id : 'taskName',
		header : '割接任务名称',
		dataIndex : 'taskName',
		width : 100
	}, {
		id : 'startTime',
		header : '开始时间',
		dataIndex : 'startTime',
		width : 100
	}, {
		id : 'endTime',
		header : '结束时间',
		dataIndex : 'endTime',
		width : 100
	}, {
		id : 'creator',
		header : '创建人',
		dataIndex : 'creator',
		width : 100
	}, {
		id : 'createTime',
		header : '创建时间',
		dataIndex : 'createTime',
		width : 100
	}, {
		id : 'description',
		header : '描述',
		dataIndex : 'description',
		width : 100
	} ]
});

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	store : store,
	cm : cm,
	selModel : sm,
	autoScroll : true,
	loadMask:true,
	stripeRows : true,
	bbar : pageTool,
	tbar : [ '开始时间：从：', fromTime, '至：', toTime, '任务名：', {
		xtype : 'combo',
		width : 120,
		id : 'cutoverTaskName',
		store:taskNameStore,
		mode:'local',
		displayField:'taskName',
		valueField:'taskId'
	}, {
		text : '查询',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/search.png',
		handler : searchCutoverTask
	}, {
		text : '重置',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/arrow_undo.png',
		handler : function() {
			// fromTime.reset();
			// toTime.reset();
			Ext.getCmp('cutoverTaskName').reset();
			Ext.getCmp('fromTime').reset();
			Ext.getCmp('toTime').reset();
		}
	}, '-', {
		text : '新增',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : addTask
	}, {
		text : '删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : deleteCutoverTask
	}, {
		text : '修改',
		privilege:modAuth,
		icon : '../../../resource/images/btnImages/modify.png',
		handler : editTask
	}, '-', {
		text : '任务操作',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/wand.png',
		handler : operateTask
	}, {
		text : '刷新',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/refresh.png',
		handler : function() {
			var pageTool = Ext.getCmp('pageTool');
			if (pageTool) {
				pageTool.doLoad(pageTool.cursor);
			}
		}
	} ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
// 查询割接任务
function searchCutoverTask() {
	var start = Ext.getCmp('fromTime').getValue();
	var end = Ext.getCmp('toTime').getValue();
	var taskId = Ext.getCmp('cutoverTaskName').getValue();
	var params = {
		"start" : 0,
		"limit" : 200,
		'searchCond.taskType':TASK_TYPE,
		'searchCond.start' : start==''?null:start,
		'searchCond.end' : end==''?null:end,
		'searchCond.taskId' : taskId==''?null:taskId
	};
	store.baseParams = params;
	store.load({
		callback : function(r, scope, success) {
			if (!success)
				Ext.Msg.alert('提示', '查询任务失败！');
		}
	});
}

// 删除割接任务
function deleteCutoverTask() {
	var selected = grid.getSelectionModel().getSelections();
	var idArray = new Array();
	if (selected && selected.length > 0) {
		Ext.Msg.confirm('提示', '是否删除选中的光复用段割接任务？', function(btn) {
			if (btn == 'yes') {
				for ( var i = 0; i < selected.length; i++) {
					idArray.push(selected[i].get('cutoverTaskId'));
				}
				var params = {
					'condList' : idArray
				};
				Ext.Ajax.request({
					url : 'ms-cutover!deleteCutoverTask.action',
					params : params,
					method : 'POST',
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							Ext.Msg.alert("提示", obj.returnMessage);
							loadTaskNameCombo();
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						} else if (obj.returnResult == 0)
							Ext.Msg.alert("提示", obj.returnMessage);
					},
					failure : function(response) {
						Ext.Msg.alert("提示", obj.returnMessage);
					}
				});
			}
		});
	} else {
		Ext.Msg.alert("提示", "请先选取复用段割接任务！");
	}
}

function operateTask() {
	var selected = grid.getSelectionModel().getSelections();
	if (selected && selected.length == 1) {
		var taskId = selected[0].get('cutoverTaskId');
		var taskName = selected[0].get('taskName');
		parent.addTabPage(
				"../performanceManager/WDMMultiplexSectionMonitor/multipleSectionList.jsp?taskId="
						+ taskId, "WDM光复用段割接信息(" + taskName + ")");
	} else {
		Ext.Msg.alert("提示", "请选择任务,只能选择一条任务！");
	}
}

Ext.onReady(function() {
	Ext.Ajax.timeout = 900000;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	var win = new Ext.Viewport({
		id : 'win',
		title : "光复用段割接",
		layout : 'border',
		items : [ grid ],
		renderTo : Ext.getBody()
	});
	win.show();
});