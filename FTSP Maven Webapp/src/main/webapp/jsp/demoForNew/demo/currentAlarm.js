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
	triggerAction : 'all',
	width :150,
//	anchor : '20%',
	listeners : {
		select : function(combo, record, index) {
			// 还原网管下拉框
//			Ext.getCmp('emsCombo').reset();
			emsCombo.reset();
			// 根据网管分组，设置网管下拉框的数据源
			if (record.get('value') == '1') {
				Ext.getCmp('emsCombo').store.loadData([['3','观前街'],['4','独墅湖']]);
			} else if (record.get('value') == '2') {
				Ext.getCmp('emsCombo').store.loadData([['5','夫子庙'],['6','新街口']]);
			}
		}
	}
});

//网管 
var emsCombo = new Ext.form.ComboBox({
	id : 'emsCombo',
	name : 'emsCombo',
	fieldLabel : '网管',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName']
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText : '全部',
	mode :'local',
	triggerAction : 'all',
	width :150,
	listeners : {
		select : function(combo, record, index) {
			// 还原网元下拉框
			Ext.getCmp('neCombo').reset();
			// 根据网管，设置网元下拉框的数据源
			if (record.get('value') == '3') {
				Ext.getCmp('neCombo').store.loadData([['7','观前街一号'],['8','观前街二号']]);
			} else if (record.get('value') == '4') {
				Ext.getCmp('neCombo').store.loadData([['9','独墅湖一号'],['10','独墅湖二号']]);
			} else if (record.get('value') == '5') {
				Ext.getCmp('neCombo').store.loadData([['11','夫子庙一号'],['12','夫子庙二号']]);
			} else if (record.get('value') == '6') {
				Ext.getCmp('neCombo').store.loadData([['13','新街口一号'],['14','新街口二号']]);
			}
		}
	}
});

// 网元
var neCombo = new Ext.form.ComboBox({
	id : 'neCombo',
	name : 'neCombo',
	fieldLabel : '网元',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName']
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText : '全部',
	mode : 'local',
	width :150,
	triggerAction : 'all'
});

// 标题面板
var titlePanel = new Ext.FormPanel({
	title : '当前告警',
	region : 'north'
});


//Ext.getDom('sync').style.background = 'red' ;



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
		id : 'confirm',
		header : '确 派2',
		dataIndex : 'confirm',
		width : 100
	}, {
		id : 'alermLevel',
		header : '告警级别',
		dataIndex : 'alermLevel',
		width : 100
	}, {
		id : 'alermName',
		header : '告警名称',
		dataIndex : 'alermName',
		width : 100
	}, {
		id : 'guiyihua',
		header : '归一化',
		dataIndex : 'guiyihua',
		width : 100
	}, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 100
	}, {
		id : 'emsName',
		header : '网管',
		dataIndex : 'emsName',
		width : 100
	}, {
		id : 'neName',
		header : '网元',
		dataIndex : 'neName',
		width : 100
	}, {
		id : 'neType',
		header : '网元型号',
		dataIndex : 'neType',
		width : 100
	}, {
		id : 'slotName',
		header : '槽道',
		dataIndex : 'slotName',
		width : 100
	}, {
		id : 'banka',
		header : '板卡',
		dataIndex : 'banka',
		width : 100
	}, {
		id : 'port',
		header : '端口',
		dataIndex : 'port',
		width : 100
	}, {
		id : 'yewuleixing',
		header : '业务类型',
		dataIndex : 'yewuleixing',
		width : 100
	}, {
		id : 'portTyle',
		header : '端口类型',
		dataIndex : 'portTyle',
		width : 100
	}, {
		id : 'sulv',
		header : '速率',
		dataIndex : 'sulv',
		width : 100
	}, {
		id : 'tongdao',
		header : '通道',
		dataIndex : 'tongdao',
		width : 100
	}]
});

var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["confirm","alermLevel","alermName","guiyihua","emsGroup", "emsName","neName","neType","slotName","banka","port","yewuleixing","portTyle","sulv","tongdao"])
});

var pageTool = new Ext.PagingToolbar({
	pageSize : 500,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});


var tab1 = new Ext.Toolbar({
	border : false,
	items : [ "网管分组：", emsGroupCombo, '-', "网管：", emsCombo, '-', "网元：", neCombo, '-',{
			text: '查询',
			icon : '../resource/images/buttonImages/search.png'
		},{
	        text: '高级查询',
	        icon : '../resource/images/buttonImages/search.png'
	    },{
	        text: '全部告警',
	        icon : '../resource/images/buttonImages/export.png',
	    },{
	        text: '刷新',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
			style : 'margin-left:150px;',
			text : '2000',
			height : 30,
//			pressed:true,
			cls : 'button-jinji',
			width : 60,
			onMouseOver : function(e){
			}
		},{
			style : 'margin-left:15px;',
			text : '3000',
			height : 30,
//			pressed:true,
			cls : 'button-zhongyao',
			width : 60,
			onMouseOver : function(e){
			}
		},{
			style : 'margin-left:15px;',
			text : '4000',
			height : 30,
//			pressed:true,
			cls : 'button-ciyao',
			width : 60,
			onMouseOver : function(e){
			}
		},{
			style : 'margin-left:15px;',
			text : '5000',
			height : 30,
//			pressed:true,
			cls : 'button-tishi',
			width : 60,
			onMouseOver : function(e){
			}
		},{
			style : 'margin-left:15px;',
			text : '6000',
			height : 30,
//			pressed:true,
			cls : 'button-zhengchang',
			width : 60,
			onMouseOver : function(e){
			}
		}
//		new Ext.Button({
//			style : 'margin-left:15px;',
//			text : '6000',
//			height : 30,
//			pressed:true,
//			width : 60
//		})
		],
});

var tab2 = new Ext.Toolbar({
	border : false,
	items : [{
	        text: '确认',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '反确认',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '派单',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '相关电路',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '告警同步',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '屏蔽',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '过滤器',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '父告警',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '子告警',
	        icon : '../resource/images/buttonImages/refresh.png',
	    },{
	        text: '导出',
	        icon : '../resource/images/buttonImages/export.png',
	    }],
});

var tab = new Ext.Toolbar({
	layout : 'form',
	border : false,
	items : [tab1,tab2]
});

//var tab = new Ext.Toolbar({
//	layout : 'column',
//	border : false,
//	items : [tab3,new Ext.Button({
//		style : 'margin-left:15px;',
//		text : '6000',
//		height : 40,
//		pressed:true,
//		width : 60
//	})]
//});
// 查询结果列表
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
//	tbar : [ "网管分组：", emsGroupCombo, '-', "网管：", emsCombo, '-', "网元：", neCombo, '-',{
//			text: '查询'
//		},{
//	        text: '高级查询'
//	    },{
//	        text: '全部告警'
//	    },{
//	        text: '刷新'
//	    },{
//	        text: '确认'
//	    },{
//	        text: '反确认'
//	    },{
//	        text: '派单'
//	    },{
//	        text: '相关电路'
//	    },{
//	        text: '告警同步'
//	    },{
//	        text: '屏蔽'
//	    },{
//	        text: '过滤器'
//	    },{
//	        text: '父告警'
//	    },{
//	        text: '子告警'
//	    },{
//	        text: '导出'
//	    }],
	tbar : tab,
	bbar : pageTool
});

// 中心模块
var centerPanel = new Ext.Panel({
	region : 'center',
	border : false,
	layout : 'border',
	items : [ gridPanel ]
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