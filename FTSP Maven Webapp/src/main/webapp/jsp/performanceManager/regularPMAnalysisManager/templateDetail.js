var dataKindData = [ [ '计数值', '0' ], [ '物理量', '1' ] ];
var dataKindStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'displayName'
	}, {
		name : 'id'
	} ]
});
dataKindStore.loadData(dataKindData);
var dataKindCombo = new Ext.form.ComboBox({
	id : 'dataKindCombo',
	store : dataKindStore,
	displayField : "displayName",
	valueField : 'id',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : '0',
	width : 100,
	listeners : {
		'select' : function(combo, record, index) {
			if(record.get('id')==1){
				gridPanel.reconfigure(physicalStore,physicalCm);
				getPhysical();
			}else{
				gridPanel.reconfigure(numbericStore,numbericCm);
				getNumberic();
			}
		}
	}
});

var numbericStore = new Ext.data.Store({
	url : 'regular-pm-analysis!getNumberic.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "pmStdIndex", "pmDesc", "factory", "unit", "threshold1", "threshold2", "threshold3",
			"filterValue", "domain", "granularity" ])
});
var numbericCm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}),  {
		id : 'pmStdIndex',
		header : '性能事件',
		dataIndex : 'pmStdIndex',
		width :120
	},  {
		id : 'granularity',
		header : '周期',
		dataIndex : 'granularity',
		width : 120,
		renderer : function(v) {
			if (v == 1) {
				return "15min";
			} else if (v == 2) {
				return "24hour";
			}
		}
	}, {
		id : 'domain',
		header : '性能类型',
		dataIndex : 'domain',
		width :120,
		renderer:domainRenderer
	}, {
		id : 'pmDesc',
		header : '描述',
		dataIndex : 'pmDesc',
		width : 120
	}, {
		id : 'factory',
		header : '厂家',
//		dataIndex : 'factory',
		width : 120,
		renderer:transFactoryName
	}, {
		id : 'unit',
		header : '单位',
		dataIndex : 'unit',
		width : 120
	}, {
		id : 'threshold1',
		header : '阈值1',
		dataIndex : 'threshold1',
		width : 120
	}, {
		id : 'threshold2',
		header : '阈值2',
		dataIndex : 'threshold2',
		width : 120
	}, {
		id : 'threshold3',
		header : '阈值3',
		dataIndex : 'threshold3',
		width : 120
	}, {
		id : 'filterValue',
		header : '过滤值',
		dataIndex : 'filterValue',
		width : 120
	}]
});
var physicalStore = new Ext.data.Store({
	url : 'regular-pm-analysis!getPhysical.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "pmStdIndex", "pmDesc", "factory", "unit", "offset", "upperOffset", "lowerOffset",
			"domain", "upper", "lower" ])
});
var physicalCm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}),  {
		id : 'pmStdIndex',
		header : '性能事件',
		dataIndex : 'pmStdIndex',
		width :120
	},  {
		id : 'domain',
		header : '性能类型',
		dataIndex : 'domain',
		width :120,
		renderer:domainRenderer
	}, {
		id : 'pmDesc',
		header : '描述',
		dataIndex : 'pmDesc',
		width : 120
	}, {
		id : 'factory',
		header : '厂家',
//		dataIndex : 'factory',
		width : 120,
		renderer:transFactoryName
	}, {
		id : 'unit',
		header : '单位',
		dataIndex : 'unit',
		width : 120
	},{
		id : 'offset',
		header : '基准值偏差',
		dataIndex : 'offset',
		width : 120
	}, 
//	{
//		id : 'upperValue',
//		header : '上限值',
//		dataIndex : 'upperValue',
//		width : 120
//	}, {
//		id : 'lowerValue',
//		header : '下限值',
//		dataIndex : 'lowerValue',
//		width : 120
//	}, 
	{
		id : 'upper',
		header : '标称上限',
		dataIndex : 'upper',
		width : 120
	}, {
		id : 'upperOffset',
		header : '上限值偏差',
		dataIndex : 'upperOffset',
		width : 120
	}, {
		id : 'lower',
		header : '标称下限',
		dataIndex : 'lower',
		width : 120
	}, {
		id : 'lowerOffset',
		header : '下限值偏差',
		dataIndex : 'lowerOffset',
		width : 120
	}]
});

var gridPanel = new Ext.grid.GridPanel({
	region : "center",
	frame : false,
	store : numbericStore,
	loadMask:true,
	cm : numbericCm,
	tbar : [ '-',"数据类型:", dataKindCombo ]
});

function getNumberic(){
	numbericStore.baseParams = {"templateId":templateId};
	numbericStore.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "计数值详情加载失败");
		}
	});
}

function getPhysical(){
	physicalStore.baseParams = {"templateId":templateId};
	physicalStore.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "计数值详情加载失败");
		}
	});
}

function transFactoryName(){
	var v=factory;
	for(var fac in FACTORY){
		if(v==FACTORY[fac]['key']){
    		return FACTORY[fac]['value'];
    	}
	}
	return v;
}

function domainRenderer(v){
	switch(v){
	case 1: return 'SDH';break;
	case 2: return 'WDM';break;
	case 3: return 'ETH';break;
	case 4: return 'ATM';break;
	default:return '';
}
}

Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			getNumberic();
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [ gridPanel ],
				renderTo : Ext.getBody()
			});
		});