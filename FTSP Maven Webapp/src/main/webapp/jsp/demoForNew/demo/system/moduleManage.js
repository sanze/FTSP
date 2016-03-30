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
	triggerAction : 'all',
//	anchor : '20%',
	listeners : {
		select : function(combo, record, index) {
			// 还原网管下拉框
//			Ext.getCmp('emsCombo').reset();
			userCombo.reset();
			// 根据网管分组，设置网管下拉框的数据源
			if (record.get('value') == '1') {
				Ext.getCmp('userCombo').store.loadData([['3','张三'],['4','李四']]);
			} else if (record.get('value') == '2') {
				Ext.getCmp('userCombo').store.loadData([['5','王五'],['6','路人']]);
			}
		}
	}
});

//网管 
var userCombo = new Ext.form.ComboBox({
	id : 'userCombo',
	name : 'userCombo',
	fieldLabel : '网管',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName']
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText : '全部',
	mode :'local',
	triggerAction : 'all'
	
});


// 标题面板
var titlePanel = new Ext.FormPanel({
	title : '模块管理',
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
		header : '模块名称',
		dataIndex : 'emsGroup',
		width : 200
	}, {
		id : 'emsName',
		header : '当前状态',
		dataIndex : 'emsName',
		width : 200
	}, {
		id : 'emsType',
		header : '开启',
		dataIndex : 'emsType',
		width : 200
	}, {
		id : 'syncCycle',
		header : '关闭',
		dataIndex : 'syncCycle',
		width : 200
	}]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["emsGroup","emsName","emsType","syncCycle"])
//	[{name:'name'},{name:'createPerson'},{name:'status'},{name:'desc'}])
});

var pageTool = new Ext.PagingToolbar({
	pageSize : 500,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '总记录数 {5} - {5} of {5}'
//	emptyMsg : "没有记录"
});


var fields = [{name:'emsGroup'},{name:'emsName'},{name:'emsType'},{name:'syncCycle'}];// 模拟静态数据时用的，真实环境下不用
// 查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [
		        ["模块一","开启","开启","关闭"],
		        ["模块一","开启","开启","关闭"],
		        ["模块一","关闭","开启","关闭"],
		        ["模块一","关闭","开启","关闭"],
		        ["模块一","开启","开启","关闭"]
		        ]
	}),
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	
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