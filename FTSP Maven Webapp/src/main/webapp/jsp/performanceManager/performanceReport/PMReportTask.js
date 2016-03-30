  Ext.state.Manager.setProvider(   
    new Ext.state.SessionStorageStateProvider({   
      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
    })   
  );

//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓创建人下拉框↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
var creatorStore = new Ext.data.Store({
	url : 'pm-report!getCreatorComboValue.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "userName", "userId" ])
});
// 页面打开初始化创建人下拉框
creatorStore.load();
var creatorCombo = new Ext.form.ComboBox({
	id : 'creatorCombo',
	name : 'creatorCombo',
	fieldLabel : '创建人',
	store : creatorStore,
	valueField : 'userId',
	displayField : 'userName',
	editable : false,
	mode : 'local',
	// autoSelect:true,
	value : "全部",
	triggerAction : 'all',
	width : 150,
	resizable: true,
	listeners : {
		select : loadTaskName
	}
});
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓报表类型下拉框↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
var periodCb;
(function() {
	var data = [ [ 9, '全部' ],[ 0, '日报' ], [ 1, '月报' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'period'
		} ]
	});
	store.loadData(data);
	periodCb = new Ext.form.ComboBox({
		id : 'periodCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '类型',
		anchor : '95%',
		store : store,
		editable : false,
		width : 150,
		value : 9,
		valueField : 'id',
		displayField : 'period',
		listeners : {
			select : loadTaskName
		}
	});
})();
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓数据源下拉框↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
var dataSrcComboStore;
(function() {
	var data = [ [9,'全部'],[ 0, '原始数据' ], [ 1, '异常数据' ]];
	dataSrcComboStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'src'
		} ]
	});
	dataSrcComboStore.loadData(data);
})();
var dataSrcCombo = new Ext.form.ComboBox({
	id : 'dataSrcCombo',
	name : 'dataSrcCombo',
	fieldLabel : '数据源',
	store : dataSrcComboStore,
	valueField : 'id',
	displayField : 'src',
	editable : false,
	mode : 'local',
	value : 9,
	triggerAction : 'all',
	width : 150,
	listeners : {
		select : loadTaskName
	}
});
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓任务名称下拉框↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
var taskNameStore = new Ext.data.Store({
	url : 'pm-report!getTaskNameComboValue.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "taskName", "taskId" ])
});
// 页面打开初始化创建人下拉框
taskNameStore.baseParams = {'searchCond.needAll':1};
taskNameStore.load();

var taskNameCombo = new Ext.form.ComboBox({
	id : 'taskNameCombo',
	name : 'taskNameCombo',
	fieldLabel : '任务名称',
	store : taskNameStore,
	valueField : 'taskId',
	displayField : 'taskName',
	mode : 'local',
	resizable: true,
	// autoSelect:true,
	// value:"全部",
	// triggerAction : 'all',
	width : 150
//	listeners : {
//		select : function(combo, record, index) {
//			loadTaskName()
//		}
//	}
});
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
function loadTaskName() {
	var creator = Ext.getCmp('creatorCombo').getValue();
	var period = Ext.getCmp('periodCb').getValue();
	var dataSrc = Ext.getCmp('dataSrcCombo').getValue();
	taskNameStore.baseParams = {
		'searchCond.creator' : creator == '全部' ? 0 : creator,
		'searchCond.period' : period,
		'searchCond.dataSrc' : dataSrc,
		'searchCond.needAll' : 1
	};
	taskNameStore.load();
}
// --------------------------------
var oneTbar = new Ext.Toolbar({
	id : 'oneTbar',
	items : [ '-',"创建人:", creatorCombo, '-',"报表类型:", periodCb, '-',"数据源:", dataSrcCombo,
	          '-',"任务名称:", taskNameCombo ]
});

var store = new Ext.data.Store({
	url : 'pm-report!searchReportTask.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "taskName", "dataSource", "doExport", "reportType", "creatTime",
			"creator","taskId","taskType","creatorId" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var sm = new Ext.grid.CheckboxSelectionModel({
	singleSelect:true
});

var cm = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'taskName',
		header : '报表任务名称',
		dataIndex : 'taskName',
		width : 100
	}, {
		id : 'dataSource',
		header : '数据源',
		dataIndex : 'dataSource',
		width : 100,
		renderer:dataSourceRenderer
	}, {
		id : 'reportType',
		header : '类型',
		dataIndex : 'reportType',
		width : 100,
		renderer:reportTypeRenderer
	}, {
		id : 'creatTime',
		header : '创建时间',
		dataIndex : 'creatTime',
		width : 100
	}, {
		id : 'creator',
		header : '创建人',
		dataIndex : 'creator',
		width : 100
	} ]
});

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	store : store,
	border:false,
	cm : cm,
	stateId:'centerPanelStateRestoreId',  
	stateful:true,
	loadMask:true,
//	autoHeight:true,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	bbar : pageTool,
	listeners : {
		render : function() {
			oneTbar.render(this.tbar); // add one tbar
			oneTbar.hide();
//			grid.syncSize();
//			centerPanel.doLayout();
		},
		destroy : function() {
			Ext.destroy(oneTbar);// 这一句不加可能会有麻烦滴
		}
	},
	tbar : [ '-',{
		text : '新增报表任务',
		privilege:addAuth,  
		icon : '../../../resource/images/btnImages/add.png',
		menu : {
			items : [ {
				text : '按网元生成',
				handler : addByNe
			}, {
				text : '按WDM复用段生成',
				handler : addByMultiSec
			} ]
		}
	}, '-',{
		text : '删除报表任务',
		privilege:delAuth, 
		icon : '../../../resource/images/btnImages/delete.png',
		handler : deleteReportTask
	}, '-',{
		text : '修改报表任务',
		privilege:modAuth,
		icon : '../../../resource/images/btnImages/modify.png',
		handler : editTask
	}, '-', {
		text : '查询',
		privilege:viewAuth,
		icon : '../../../resource/images/btnImages/search.png',
		handler : searchReportTask
	}, {
		text : '高级查询条件',
		privilege:viewAuth,
		id : 'searchCond',
		enableToggle : true,
		icon : '../../../resource/images/btnImages/getChecked.gif',
		handler : function() {
			if (!oneTbar.hidden) {
				Ext.getCmp('oneTbar').setVisible(false);
				grid.syncSize();
				centerPanel.doLayout();
			} else {
				Ext.getCmp('oneTbar').setVisible(true);
				grid.syncSize();
				centerPanel.doLayout();
			}
		}
	} ]
});

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'border',
	items : [ grid ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
//修改任务
function editTask(){
	var record = grid.getSelectionModel().getSelected(); 
	if(!record){
		Ext.Msg.alert("提示", "请选择任务！");
		return;
	}
	var taskId = record.get('taskId');
	var creatorId = record.get('creatorId');
	var taskType = record.get('taskType');
	if(taskType==taskTypeMS)
		var url = "../../jsp/performanceManager/performanceReport/editMSTask.jsp";
	else if(taskType==taskTypeNE)
		var url = "../../jsp/performanceManager/performanceReport/editNETask.jsp";
	var url=url+"?taskId="+taskId+"&creatorId="+creatorId;
	parent.addTabPage(url, "修改报表",authSequence);
}

// 按网元生成
function addByNe() {
	var url = "../../jsp/performanceManager/performanceReport/addPMReportTaskByNe.jsp";
	parent.addTabPage(url, "新增报表",authSequence);
}

// 按WDM复用段生成
function addByMultiSec() {
	var url = "../../jsp/performanceManager/performanceReport/addPMReportTaskByMultiSec.jsp";
	parent.addTabPage(url, "新增报表",authSequence);
}
// 查询报表任务
function searchReportTask() {
	var creator = Ext.getCmp('creatorCombo').getValue();
	var period = Ext.getCmp('periodCb').getValue();
	var dataSrc = Ext.getCmp('dataSrcCombo').getValue();
	var taskId = Ext.getCmp('taskNameCombo').getValue();
	var params = {
		'searchCond.creator' : creator == '全部' ? 0 : creator,
		'searchCond.period' : period,
		'searchCond.dataSrc' : dataSrc,
		'start' : 0,
		'limit' : 200,
		'searchCond.taskId' : taskId == '' ? 0 : taskId
	};
	if (!oneTbar.hidden)
		store.baseParams = params;
	else
		store.baseParams = {
			'start' : 0,
			'limit' : 200
	};
	store.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "查询失败！");
		}
	});
}

function deleteReportTask(){
	var record = grid.getSelectionModel().getSelected(); 
	if(!record){
		Ext.Msg.alert("提示", "请选择任务！");
		return;
	}
	if(userId.toString() != record.get('creatorId').toString()){
		Ext.Msg.alert("提示", "无法删除他人创建计划！");
		return;
	}
	Ext.Msg.confirm('提示','确认删除？',function(btn){
		if(btn=='yes'){
			var params = {
					'searchCond.taskId':record.get('taskId'),
					'searchCond.taskType':record.get('taskType')
			};
			Ext.Ajax.request({
				url:'pm-report!deleteReportTask.action',
				method:'POST',
				params:params,
				success : function(response) {
					grid.getEl().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					if (result) {
						// 提交修改，不然store.getModifiedRecords();数据会累加
						if (1 == result.returnResult) {
							store.commitChanges();
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								loadTaskName();
								pageTool.doLoad(pageTool.cursor);
							}
						}else{
							Ext.Msg.alert("提示", result.returnMessage);
						}
					}
				},
				failure : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					grid.getEl().unmask();
					Ext.Msg.alert("提示", result.returnMessage);
				}
			});
		}
	});
}

function dataSourceRenderer(v,t,r){
	if(v==0)
		return '原始数据';
	if(v==1)
		return '异常数据';
}

function doExportRenderer(v){
	if(v==0)
		return '否';
	if(v==1)
		return '是';
}

function reportTypeRenderer(v){
	if(v==0)
		return '日报';
	if(v==1)
		return '月报';
}
Ext
		.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			var win = new Ext.Viewport({
				id : 'win',
				title : "性能报表管理",
				layout : 'border',
				items : [ centerPanel ],
				renderTo : Ext.getBody()
			});
			win.doLayout();
//			grid.syncSize();
			searchReportTask();
			win.show();
		});