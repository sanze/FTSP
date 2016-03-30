//--------------------srore for createCircle---------------------
var Data_connectRate = [ [ '64C', '64C' ], [ '16C', '16C' ], [ '4C', '4C' ],
		[ 'VC4', 'VC4' ], [ 'VC3', 'VC3' ], [ 'VC12', 'VC12' ], [ 'C', '全部' ] ];
var store_connectRate = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
store_connectRate.loadData(Data_connectRate);

//--------------------srore for circuitstate---------------------
var Data_circuitstate = [ [ '0', '离散' ], [ '1', '正常' ], [ '-1', '全部' ]

];
var store_circuitstate = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
store_circuitstate.loadData(Data_circuitstate);

//---------------------------srore for crosschange--------------------------------

var Data_crosschange = [ [ '1', '最近一次新增' ], [ '2', '最近一次删除' ],
		[ '3', '已删除' ], [ '-1', '全部' ]

];
var store_crosschange = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
store_crosschange.loadData(Data_crosschange);

var store = new Ext.data.Store({
	url : 'connection!getCrsNeDetailInfoByNeId.action',
	baseParams : {
		"neModel.neId" : neId,
		"neModel.type" : neType,
		"limit" : 200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "crossConnectionId", "neId", "neName", "portA", "nameCtpA", "portZ",
			"nameCtpZ", "connectRate", "circuitCount" , "changeState"])
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});
var columnModel = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true,
		forceFit : false
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'crossConnectionId',
		header : '交叉连接Id',
		dataIndex : 'crossConnectionId',
		hidden : true,//hidden colunm
		width : 100
	}, {
		id : 'portA',
		header : 'A端节点',
		dataIndex : 'portA',
		width : 120
	}, {
		id : 'nameCtpA',
		header : 'A端时隙',
		dataIndex : 'nameCtpA',
		width : 120
	}, {
		id : 'portZ',
		header : 'Z端节点',
		dataIndex : 'portZ',
		width : 120
	}, {
		id : 'nameCtpZ',
		header : 'Z端时隙',
		dataIndex : 'nameCtpZ',
		width : 120
	}, {
		id : 'connectRate',
		header : '连接速率',
		dataIndex : 'connectRate',
		width : 80
	}, {
		id : 'circuitCount',
		header : '连接类别',
		dataIndex : 'circuitCount',
		width : 80,
		renderer : function(v) {
			if (v > 0) {
				return "正常";
			} else {
				return "离散";
			}
		}
	}, {
		id : 'circuitChange',
		header : '连接变化',
		dataIndex : 'changeState',
		width : 100
		,renderer : function(v) {
			if (v == 1) {
				return "新增";
			}
			if (v == 2) {
				return "删除";
			} 
			if (v == 3) {
				return "删除";
			} 
			if (v == 4) {
				return "新增";
			} else
				return " ";
		}
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,//每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});


var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	cm : columnModel,
	store : store,
	stripeRows : true, // 交替行效果
	//    loadMask: true,
	selModel : checkboxSelectionModel, //必须加不然不能选checkbox  
	bbar : pageTool,
	viewConfig : {
		forceFit : false
	},
    viewConfig:{forceFit : true},
	tbar : [ {
		xtype : 'label',
		text : '',
		width : 50
	}, '-', {
		text : '网元名称：'
	}, {
		xtype : 'textfield',
		id : 'neName',
		name : 'neName',
		fieldLabel : '网元名称',
		emptyText : '请输入网元名称........',
		allowBlank : false,
		width : 60
	}, '-', {
		text : '连接速率：'
	}, {
		xtype : 'combo',
		id : 'connectRate',
		name : 'connectRate',
		mode : "local",
		editable : false,
		store : store_connectRate,
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		value : '全部',
		//	            allowBlank:false,
		width : 60,
		listeners : {
			select : function(combo, record, index) {
				var circuitCount = Ext.getCmp('circuitState').getValue();
				var connectRate = Ext.getCmp('connectRate').getValue();
				var changeState = Ext.getCmp('crossChange').getValue();
				// 加载网元同步列表
				var jsonData = {
					"neModel.neId" : neId,
					"neModel.type" : neType,
					"sdhCrsModel.rate" : connectRate=="全部"?"C":connectRate,
					"sdhCrsModel.circuitCount" : circuitCount,
					"sdhCrsModel.changeState" : changeState,
					"limit" : 200
				};
				store.proxy = new Ext.data.HttpProxy({
					url : 'connection!getCrsNeDetailInfoByNeId.action'
				});
				store.baseParams = jsonData;
				store.load({
					callback : function(r, options, success) {
//						Ext.getBody().unmask();
						if (success) {

						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
			}
		}
	}, '-', {
		text : '类别：'
	}, {
		xtype : 'combo',
		id : 'circuitState',
		name : 'circuitState',
		mode : "local",
		editable : false,
		store : store_circuitstate,
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		value : '-1',
		//	            allowBlank:false,
		width : 60,
		listeners : {
			select : function(combo, record, index) {
				var circuitCount = Ext.getCmp('circuitState').getValue();
				var connectRate = Ext.getCmp('connectRate').getValue();
				var changeState = Ext.getCmp('crossChange').getValue();
				// 加载网元同步列表
				var jsonData = {
					"neModel.neId" : neId,
					"neModel.type" : neType,
					"sdhCrsModel.rate" : connectRate=="全部"?"C":connectRate,
					"sdhCrsModel.circuitCount" : circuitCount,
					"sdhCrsModel.changeState" : changeState,
					"limit" : 200
				};
				store.proxy = new Ext.data.HttpProxy({
					url : 'connection!getCrsNeDetailInfoByNeId.action'
				});
				store.baseParams = jsonData;
				store.load({
					callback : function(r, options, success) {
						if (success) {

						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
			}
		}
	}, '-', {
		text : '变化：'
	}, {
		xtype : 'combo',
		id : 'crossChange',
		name : 'crossChange',
		mode : "local",
		editable : false,
		store : store_crosschange,
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		value : '-1',
		//	            allowBlank:false,
		width : 120,
		listeners : {
			select : function(combo, record, index) {
				var circuitCount = Ext.getCmp('circuitState').getValue();
				var connectRate = Ext.getCmp('connectRate').getValue();
				var changeState = Ext.getCmp('crossChange').getValue();

				// 加载网元同步列表
				var jsonData = {
					"neModel.neId" : neId,
					"neModel.type" : neType,
					"sdhCrsModel.rate" : connectRate=="全部"?"C":connectRate,
					"sdhCrsModel.circuitCount" : circuitCount,
					"sdhCrsModel.changeState" : changeState,
					"limit" : 200
				};

				store.proxy = new Ext.data.HttpProxy({
					url : 'connection!getCrsNeDetailInfoByNeId.action'
				});

				store.baseParams = jsonData;

				store.load({

					callback : function(r, options, success) {
						if (success) {

						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
			}
		}
	} ],
	listeners : {
		'rowdblclick' : function(gridPanel, rowIndex, e) {
		}
	}
});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	// 	Ext.Msg = top.Ext.Msg; 
	Ext.Ajax.timeout = 90000000;

	// 	initData(emsConnectionId);

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel ]
	});
	win.show();
	Ext.getCmp('neName').setValue(neName);
	Ext.getCmp("neName").setDisabled(true);
	//放最后才能显示遮罩效果
	store.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});