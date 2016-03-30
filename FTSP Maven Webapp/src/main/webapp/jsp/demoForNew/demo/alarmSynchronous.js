// 网管分组
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	name : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '苏州' ], [ '2', '南京' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'全部',
	mode : 'local',
	triggerAction : 'all'
//	anchor : '20%',
});

// 标题面板
var titlePanel = new Ext.FormPanel({
	title : '告警自动同步',
	region : 'north'
});


//*****************************查询结果***********************
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : false
});
sm.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), sm, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 100
	}, {
		id : 'emsName',
		header : '网管名称',
		dataIndex : 'emsName',
		width : 100
	}, {
		id : 'emsType',
		header : '网管类型',
		dataIndex : 'emsType',
		width : 100
	}, {
		id : 'syncCycle',
		header : '同步周期',
		dataIndex : 'syncCycle',
		width : 100
	}, {
		id : 'taskStatus',
		header : '任务状态',
		dataIndex : 'taskStatus',
		width : 100
	}, {
		id : 'executionStatus',
		header : '执行状态',
		dataIndex : 'executionStatus',
		width : 100
	}, {
		id : 'lastSyncTime',
		header : '上次同步时间',
		dataIndex : 'lastSyncTime',
		width : 100
	}, {
		id : 'nextSyncTime',
		header : '下次同步时间',
		dataIndex : 'nextSyncTime',
		width : 100
	}, {
		id : 'automaticSync',
		header : '南向连接回复后自动同步',
		dataIndex : 'automaticSync',
		width : 150
	}, {
		id : 'delay',
		header : '延时',
		dataIndex : 'delay',
		width : 100
	}]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["emsGroup","emsName","emsType","syncCycle","taskStatus","executionStatus","lastSyncTime","nextSyncTime","automaticSync","delay"])
//	[{name:'name'},{name:'createPerson'},{name:'status'},{name:'desc'}])
});

var pageTool = new Ext.PagingToolbar({
	pageSize : 500,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '总记录数 {5} - {5} of {5}'
//	emptyMsg : "没有记录"
});

var fields = [{name:'emsGroup'},{name:'emsName'},{name:'emsType'},{name:'syncCycle'},{name:'taskStatus'},
              {name:'executionStatus'},{name:'lastSyncTime'},{name:'nextSyncTime'},{name:'automaticSync'},{name:'delay'}];// 模拟静态数据时用的，真实环境下不用
// 查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [
		        ["一级干线","京九广一期","U2000","4","启用","成功","2013/8/13 9:29","2013/8/13 13:29","是","10"],
		        ["二级干线","南京-苏州二期","OTNM2000","24","启用","失败","2013/8/13 9:29","2013/8/13 13:29","是","10"],
		        ["杭州","本地网","T2000","8","启用","成功","2013/8/13 9:29","2013/8/13 13:29","是","10"],
		        ["二级干线","南京-苏州二期","OTNM2000","24","启用","失败","2013/8/13 9:29","2013/8/13 13:29","是","10"],
		        ["二级干线","南京-苏州二期","OTNM2000","24","启用","失败","2013/8/13 9:29","2013/8/13 13:29","是","10"]
		        ]
	}),
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [ "网管分组：", emsGroupCombo, '-', {
		text : '启用',
		style : 'margin-left:10px;'
	}, {
		text : '挂起',
		style : 'margin-left:10px;'
	}, {
		text : '保存',
		style : 'margin-left:10px;'
	}  ],
	bbar : pageTool
});

// 中心模块
var centerPanel = new Ext.Panel({
	region : 'center',
	border : false,
	layout : 'border',
	items : gridPanel 
});


Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
//		loadMask : true,//定义可以在加载数据前显示提示信息
		layout : 'border',
		items : [ titlePanel,centerPanel]
	});
});