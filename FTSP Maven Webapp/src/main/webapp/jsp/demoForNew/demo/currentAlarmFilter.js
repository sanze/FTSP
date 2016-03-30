// 标题面板
var titlePanel = new Ext.FormPanel({
	title : '当前告警过滤器',
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
		id : 'name',
		header : '名称',
		dataIndex : 'name',
		width : 200
	}, {
		id : 'createPerson',
		header : '创建者',
		dataIndex : 'createPerson',
		width : 200
	}, {
		id : 'status',
		header : '状态',
		dataIndex : 'status',
		width : 200
	}, {
		id : 'desc',
		header : '描述',
		dataIndex : 'desc',
		width : 200
	}]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["name","createPerson","status","desc"])
//	[{name:'name'},{name:'createPerson'},{name:'status'},{name:'desc'}])
});

var pageTool = new Ext.PagingToolbar({
	pageSize : 500,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '总记录数 {5} - {5} of {5}'
//	emptyMsg : "没有记录"
});

var fields = [{name:'name'},{name:'createPerson'},{name:'status'},{name:'desc'}];// 模拟静态数据时用的，真实环境下不用
// 查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [
		        ["全部告警","汤键","启用","所有的全部告警"],
		        ["紧急告警","汤键","挂起","所有的全部告警"],
		        ["干线告警","汤键","挂起","所有的全部告警"],
		        ["主线告警","汤键","挂起","所有的全部告警"],
		        ["其他告警","汤键","挂起","所有的全部告警"]
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
	        text: '新增',
	        style : 'margin-left:10px;'
	    },{
	        text: '删除',
	        style : 'margin-left:10px;'
	    },{
	        text: '修改',
	        style : 'margin-left:10px;'
	    },{
	        text: '启用',
	        style : 'margin-left:10px;'
	    },{
	        text: '挂起',
	        style : 'margin-left:10px;'
	    }],
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