
// 标题面板
var titlePanel = new Ext.FormPanel({
	title : '割接任务',
	region : 'north'
	
});

// 影响电路
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : false
});
sm.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), sm, {
		id : 'a',
		header : '电路编号',
		dataIndex : 'a',
		width : 200
	},{
		id : 'b',
		header : '资源系统编号',
		dataIndex : 'b',
		width : 200
	},{
		id : 'c',
		header : 'A端端口',
		dataIndex : 'c',
		width : 200
	},{
		id : 'd',
		header : 'A端时隙',
		dataIndex : 'd',
		width : 200
	},{
		id : 'e',
		header : 'Z端端口',
		dataIndex : 'e',
		width : 200
	}]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["a","b","c","d","e"])
//	[{name:'emsGroup'},{name:'emsName'},{name:'emsType'},{name:'syncCycle'}])
});

var pageTool = new Ext.PagingToolbar({
	pageSize : 500,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '总记录数 {5} - {5} of {5}',
	emptyMsg : "没有记录"
});

var fields = [{name:'a'},{name:'b'},{name:'c'},{name:'d'},{name:'e'},];// 模拟静态数据时用的，真实环境下不用

// 查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [["1","11","xxx","xxx","xxx"],
		        ["1","11","xxx","xxx","xxx"]
			]
	}),
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [{
		text : '详情',
		style : 'margin-left:10px;'
	},{
		text : '导出',
		style : 'margin-left:10px;'
	}],
	bbar : pageTool
});


//端口性能值
var cm1 = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		locked : true
	}), sm, {
		id : 'a',
		header : '网元',
		dataIndex : 'a',
		width : 200
	},{
		id : 'b',
		header : '端口',
		dataIndex : 'b',
		width : 200
	},{
		id : 'c',
		header : '割接前快照',
		dataIndex : 'c',
		width : 200
	},{
		id : 'd',
		header : '割接后快照',
		dataIndex : 'd',
		width : 200
	},{
		id : 'e',
		header : '差值',
		dataIndex : 'e',
		width : 200
	},{
		id : 'f',
		header : '割接前快照时间',
		dataIndex : 'f',
		width : 200
	},{
		id : 'g',
		header : '割接后快照时间',
		dataIndex : 'g',
		width : 200
	}]
});

var store1 = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["a","b","c","d","e","f","g"])
//	[{name:'emsGroup'},{name:'emsName'},{name:'emsType'},{name:'syncCycle'}])
});

var pageTool1 = new Ext.PagingToolbar({
	pageSize : 500,// 每页显示的记录值
	store : store1,
	displayInfo : true,
	displayMsg : '总记录数 {5} - {5} of {5}',
	emptyMsg : "没有记录"
});

var fields1 = [{name:'a'},{name:'b'},{name:'c'},{name:'d'},{name:'e'},{name:'f'},{name:'g'}];// 模拟静态数据时用的，真实环境下不用

// 查询结果列表
var gridPanel1 = new Ext.grid.EditorGridPanel({
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [["NE-001","7-1","-19.1","-29.0","-9.9","2013/10/12","2013/10/12"],
		        ["NE-001","7-1","-19.1","-29.0","-9.9","2013/10/12","2013/10/12"]
			]
	}),
	cm : cm1,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [{
		text : '导出',
		style : 'margin-left:10px;'
	}],
	bbar : pageTool1
});

// 相关告警
var cm2 = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		locked : true
	}), sm, {
		id : 'a',
		header : '网元',
		dataIndex : 'a',
		width : 200
	},{
		id : 'b',
		header : '类型',
		dataIndex : 'b',
		width : 200
	},{
		id : 'c',
		header : '告警级别',
		dataIndex : 'c',
		width : 200
	},{
		id : 'd',
		header : '告警名称',
		dataIndex : 'd',
		width : 200
	},{
		id : 'e',
		header : 'AID',
		dataIndex : 'e',
		width : 200
	},{
		id : 'f',
		header : '割接前告警快照时间',
		dataIndex : 'f',
		width : 200
	},{
		id : 'g',
		header : '割接后告警快照时间',
		dataIndex : 'g',
		width : 200
	}]
});

var store2 = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["a","b","c","d","e","f","g"])
//	[{name:'emsGroup'},{name:'emsName'},{name:'emsType'},{name:'syncCycle'}])
});

var pageTool2 = new Ext.PagingToolbar({
	pageSize : 500,// 每页显示的记录值
	store : store2,
	displayInfo : true,
	displayMsg : '总记录数 {5} - {5} of {5}',
	emptyMsg : "没有记录"
});

var fields2 = [{name:'a'},{name:'b'},{name:'c'},{name:'d'},{name:'e'},{name:'f'},{name:'g'}];// 模拟静态数据时用的，真实环境下不用

// 查询结果列表
var gridPanel2 = new Ext.grid.EditorGridPanel({
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [["NE-001","通信","重要","LOS","7-1","2013/10/12","2013/10/12"],
		        ["NE-001","通信","重要","LOS","7-1","2013/10/12","2013/10/12"]
			]
	}),
	cm : cm2,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [{xtype : 'radio',boxLabel : '全部告警',name :'aaa',checked : true},{xtype : 'radio',boxLabel : '割接前',name :'aaa'},{xtype : 'radio',boxLabel : '割接后',name :'aaa'},{
		text : '导出',
		style : 'margin-left:10px;'
	}],
	bbar : pageTool1
});


// 中心模块
var centerPanel = new Ext.TabPanel({
	region : 'center',
	border : false,
	activeTab: 0, // 默认激活第1个tab页
	items : [{
		layout : 'fit',
		title: "影响电路",
		items : gridPanel
	},{
		layout : 'fit',
		title: "端口性能值",
		items : gridPanel1
	},{
		layout : 'fit',
		title: "相关告警",
		items : gridPanel2
	}] 
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