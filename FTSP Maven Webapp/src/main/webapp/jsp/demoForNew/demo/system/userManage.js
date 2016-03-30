// 网管分组
var userGroupCombo = new Ext.form.ComboBox({
	id : 'userGroupCombo',
	name : 'userGroupCombo',
	fieldLabel : '用户组',
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
	title : '用户管理',
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
		header : '姓名',
		dataIndex : 'emsGroup',
		width : 100
	}, {
		id : 'emsName',
		header : '登录名',
		dataIndex : 'emsName',
		width : 100
	}, {
		id : 'emsType',
		header : '工号',
		dataIndex : 'emsType',
		width : 100
	}, {
		id : 'syncCycle',
		header : '部门',
		dataIndex : 'syncCycle',
		width : 100
	}, {
		id : 'taskStatus',
		header : '职务',
		dataIndex : 'taskStatus',
		width : 100
	}, {
		id : 'executionStatus',
		header : '组名',
		dataIndex : 'executionStatus',
		width : 100
	}, {
		id : 'lastSyncTime',
		header : '创建时间',
		dataIndex : 'lastSyncTime',
		width : 100
	}, {
		id : 'nextSyncTime',
		header : '邮箱',
		dataIndex : 'nextSyncTime',
		width : 100
	}, {
		id : 'automaticSync',
		header : '手机号码',
		dataIndex : 'automaticSync',
		width : 150
	}, {
		id : 'delay',
		header : '设备管理域',
		dataIndex : 'delay',
		width : 100
	}, {
		id : 'quanxian',
		header : '权限管理域',
		dataIndex : 'quanxian',
		width : 100
	}, {
		id : 'beizhu',
		header : '备注',
		dataIndex : 'beizhu',
		width : 100
	}]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["emsGroup","emsName","emsType","syncCycle","taskStatus","executionStatus","lastSyncTime","nextSyncTime","automaticSync","delay","quanxian","beizhu"])
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
              {name:'executionStatus'},{name:'lastSyncTime'},{name:'nextSyncTime'},{name:'automaticSync'},{name:'delay'},{name:'quanxian'},{name:'beizhu'}];// 模拟静态数据时用的，真实环境下不用
// 查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [
		        ["张三","24","CSZ10001","一部","担当","A,B,C","2013/8/13","hehe@cn.fujitsu.com","138xxxx","A,B,C","A,B,C",""],
		        ["张三","24","CSZ10001","一部","担当","A,B,C","2013/8/13","hehe@cn.fujitsu.com","138xxxx","A,B,C","A,B,C",""],
		        ["张三","24","CSZ10001","一部","担当","A,B,C","2013/8/13","hehe@cn.fujitsu.com","138xxxx","A,B,C","A,B,C",""],
		        ["张三","24","CSZ10001","一部","担当","A,B,C","2013/8/13","hehe@cn.fujitsu.com","138xxxx","A,B,C","A,B,C",""],
		        ["张三","24","CSZ10001","一部","担当","A,B,C","2013/8/13","hehe@cn.fujitsu.com","138xxxx","A,B,C","A,B,C",""]
		        ]
	}),
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [ "用户组：", userGroupCombo, '-', {
		text : '删除',
		style : 'margin-left:10px;'
	}, {
		text : '修改',
		style : 'margin-left:10px;'
	}, {
		text : '新增',
		style : 'margin-left:10px;'
	}, {
		text : '详情',
		style : 'margin-left:10px;'
	}, {
		text : '重置密码',
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