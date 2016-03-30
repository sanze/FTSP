// 告警级别
var alarmLevelCombo = new Ext.form.ComboBox({
	fieldLabel : '告警级别',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '重要' ], [ '2', '次要' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'全部',
	mode : 'local',
	triggerAction : 'all'
});

//区域
var areaCombo = new Ext.form.ComboBox({
	fieldLabel : '区域',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '南京' ], [ '2', '苏州' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'全部',
	mode : 'local',
	triggerAction : 'all'
});

//厂家
var factoryCombo = new Ext.form.ComboBox({
	fieldLabel : '厂家',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', 'xxx' ], [ '2', 'xxxx' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'全部',
	mode : 'local',
	triggerAction : 'all'
});

//设备类型
var equipTypeCombo = new Ext.form.ComboBox({
	fieldLabel : '设备类型',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', 'SDH' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'全部',
	mode : 'local',
	triggerAction : 'all'
});

//网络
var emsCombo = new Ext.form.ComboBox({
	fieldLabel : '网络',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', 'T2000' ], [ '2', 'E300' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'全部',
	mode : 'local',
	triggerAction : 'all'
});


//标题面板
var titlePanel = new Ext.FormPanel({
	title : '机房类型',
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
		header : '机房名称',
		dataIndex : 'emsGroup',
		width : 200
	}, {
		id : 'emsName',
		header : '局站',
		dataIndex : 'emsName',
		width : 200
	}, {
		id : 'emsType',
		header : '区域',
		dataIndex : 'emsType',
		width : 200
	}, {
		id : 'syncCycle',
		header : '机房类型',
		dataIndex : 'syncCycle',
		width : 200
	}, {
		id : 'taskStatus',
		header : '联系人',
		dataIndex : 'taskStatus',
		width : 200
	}, {
		id : 'pingbi',
		header : '电话',
		dataIndex : 'pingbi',
		width : 200
	}, {
		id : 'createPerson',
		header : '备注',
		dataIndex : 'createPerson',
		width : 200
	}]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["emsGroup","emsName","emsType","syncCycle","taskStatus","pingbi","createPerson"])
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
           {name:'pingbi'},{name:'createPerson'}];// 模拟静态数据时用的，真实环境下不用

//查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
//	store : store,
	store : new Ext.data.ArrayStore({// 模拟静态数据时用的，真实环境下，用上面注释的
		fields : fields,
		data : [
		        ["A机房","xxx","xxxx","xxx","xxx","xxx","xxx","xxx","xxxxxxx"]
		        
		        ]
	}),
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : [ '区域:',areaCombo,'-','网络:',emsCombo,'-','机房类型：',equipTypeCombo,'-','机房名称:',factoryCombo,'-',{
		text : '查询',
		style : 'margin-left:10px;'
	},{
		text : '导出',
		style : 'margin-left:10px;'
	}],
	bbar : pageTool
});


//中心模块
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