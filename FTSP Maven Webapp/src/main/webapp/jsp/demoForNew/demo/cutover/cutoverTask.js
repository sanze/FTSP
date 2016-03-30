
// 标题面板
var titlePanel = new Ext.FormPanel({
	title : '割接任务',
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
		header : '割接任务名称',
		dataIndex : 'emsGroup',
		width : 200
	}, {
		id : 'emsName',
		header : '预计开始时间',
		dataIndex : 'emsName',
		width : 200
	}, {
		id : 'emsType',
		header : '预计结束时间',
		dataIndex : 'emsType',
		width : 200
	}, {
		id : 'syncCycle',
		header : '状态',
		dataIndex : 'syncCycle',
		width : 200
	}, {
		id : 'taskStatus',
		header : '数据快照',
		dataIndex : 'taskStatus',
		width : 200
	}, {
		id : 'pingbi',
		header : '告警屏蔽',
		dataIndex : 'pingbi',
		width : 200
	}, {
		id : 'createPerson',
		header : '创建人',
		dataIndex : 'createPerson',
		width : 200
	}, {
		id : 'createTime',
		header : '创建时间',
		dataIndex : 'createTime',
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
	["emsGroup","emsName","emsType","syncCycle","taskStatus","pingbi","createPerson","createTime","desc"])
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
              {name:'pingbi'},{name:'createPerson'},{name:'createTime'},{name:'desc'}];// 模拟静态数据时用的，真实环境下不用

// 查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [
		        ["xxx割接","2013/8/13","2013/8/15","待命","快照","过滤","admin","2013/8/13","xxxxxxx"],
		        ["xxx割接","2013/8/13","2013/8/15","待命","快照","过滤","admin","2013/8/13","xxxxxxx"],
		        ["xxx割接","2013/8/13","2013/8/15","待命","快照","过滤","admin","2013/8/13","xxxxxxx"],
		        ["xxx割接","2013/8/13","2013/8/15","待命","快照","过滤","admin","2013/8/13","xxxxxxx"],
		        ["xxx割接","2013/8/13","2013/8/15","待命","快照","过滤","admin","2013/8/13","xxxxxxx"]
		        ]
	}),
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [ '时间范围：从',{xtype:'datefield',width:150},'至',{xtype:'datefield',width:150},'-','割接任务名：',{xtype:'textfield',width:150},'-',{
		text : '查询',
		style : 'margin-left:10px;'
	}, {
		text : '新增',
		style : 'margin-left:10px;',
		menu : {
			items : [ {
				text : '按网元和端口新增',
				handler : function() {
//					exportPerformanceSearch();
				}
			}, {
				text : '按链路新增',
				handler : function() {
//					exportPerformanceSearch();
				}
			}, {
				text : '按光缆新增',
				handler : function() {
//					exportPerformanceSearch();
				}
			}  ]
		}
	},{
		text : '删除',
		style : 'margin-left:10px;'
	},{
		text : '修改',
		style : 'margin-left:10px;'
	},{
		text : '任务操作',
		style : 'margin-left:10px;'
	},{
		text : '割接报告',
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