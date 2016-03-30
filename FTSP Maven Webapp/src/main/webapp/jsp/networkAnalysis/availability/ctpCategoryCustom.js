/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var myPageSize = 200;
var storedata = [ [ '1', '公共板卡' ], [ '2', '业务板卡' ] ];
var ctpCategoryStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
ctpCategoryStore.loadData(storedata);

var categoryStore = new Ext.data.Store({
	//1代表查
	url : 'network!ctpNameCustomList.action',
	baseParams : {
		"limit":200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["NWA_UNIT_TYPE_ID","SORT_B","SORT_A"])
});
categoryStore.load();

var factoryStore = new Ext.data.Store({
	url : 'network!getFactoryGroup.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "factoryName", "factoryId" ])
});
factoryStore.load({
	callback : function(r, options, success) {
		if (success) {
			
		} else {
			Ext.Msg.alert('错误', '厂家数据加载失败！');
		}
	}
});

var store = new Ext.data.Store({
	//1代表查
	url : 'network!getCtpCategoryListById.action',
	baseParams : {
		"factoryId" : "0",
		"limit":200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["NWA_UNIT_TYPE_DEFINE_ID","FACTORY","UNIT_NAME","NWA_UNIT_TYPE_ID","SORT_B","SORT_A"])
});

store.load();

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});


var tbar = new Ext.Toolbar({
	id : 'tbar',
	items : [  {
				xtype : 'tbspacer',
				width : 10
				},
	          '厂家：',
	          {
				xtype : 'combo',
				id : 'factory',
				name : 'factory',
				fieldLabel : '厂家',
				mode : "local",
				width : 140,
				value:'全部',
				store : factoryStore,
				displayField : "factoryName",
				valueField : 'factoryId',
				triggerAction : 'all',
				anchor : '95%',
				listeners : {
					select : function(combo, record, index) {
						var factoryId = Ext.getCmp('factory').getValue();
						//加载网元同步列表
						var jsonData = {
							"factoryId" : factoryId,
							"limit" : 200
						};
						store.proxy = new Ext.data.HttpProxy({
							url : 'network!getCtpCategoryListById.action'
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
					}}
	          	},{
					xtype : 'tbspacer',
					width : 10
				},{
					icon : '../../../resource/images/btnImages/modify.png',
					xtype : 'button',
					text:'保存',
					handler:function(){
						setCtpCategory();
					}
				},{
					icon : '../../../resource/images/btnImages/chart.png',
					xtype : 'button',
					text:'槽道可用信息导入',
					hidden:true,
					handler:function(){
						importAcceptEqptType();
					}
				}]
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});

var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		 sortable : true,
		 forceFit : true,
		 align:'left'
	},
	columns:[ new Ext.grid.RowNumberer({
					width : 26,
					id:'num'
				}),
	          checkboxSelectionModel,
				{
					id : 'NWA_UNIT_TYPE_DEFINE_ID',
					header : 'id',
					dataIndex : 'NWA_UNIT_TYPE_DEFINE_ID',
					width : 80,
					hidden : true
				},
				{
					id:'FACTORY',
					header:'厂家',
					dataIndex:'FACTORY',
					width:150,
					renderer:function(value){
						if(value == '1'){
							return '华为';
						}else if(value == '2'){
							return '中兴';
						}else if(value == '3'){
							return '朗讯';
						}else if(value == '4'){
							return '烽火';
						}else if(value == '5'){
							return '贝尔';
						}else if(value == '9'){
							return '富士通';
						}
					}
				},
				{
					id:'UNIT_NAME',
					header:'板卡名称',
					dataIndex:'UNIT_NAME',
					width:150
				},
				{
					id:'SORT_B',
					header:'板卡小类名称',
					dataIndex:'NWA_UNIT_TYPE_ID',
					width:150,
					editor:new Ext.form.ComboBox({
						id:'unitCategoryName',
						name:'unitCategoryName',
						store : categoryStore,
						displayField : "SORT_B",
						valueField : 'NWA_UNIT_TYPE_ID',
						triggerAction: 'all',
						listeners:{
							select : function(combo, record, index){  
			                //获取存储在ComboBox中的状态值值  
			                var record = gridPanel.getSelectionModel().getSelected();
			                record.set('SORT_A',categoryStore.getAt(index).get('SORT_A'));
			                record.set('SORT_B',categoryStore.getAt(index).get('NWA_UNIT_TYPE_ID'));
			                record.commit();
							}
						}}),
					 renderer:function(value, cellmeta, record){
						//获取当前id="combo_process"的comboBox选择的值
					      var index = categoryStore.find(Ext
					        .getCmp('unitCategoryName').valueField, value);
					      var record = categoryStore.getAt(index);
					      var displayText = "";
					      // 如果下拉列表没有被选择，那么record也就不存在，这时候，返回传进来的value，
					      // 而这个value就是grid的deal_with_name列的value，所以直接返回
					      if (record == null || record == undefined) {
					      //返回默认值，这是与网上其他解决办法不同的。这个方法才是正确的。我研究了很久才发现。
					       displayText = value;
					      } else {
					       displayText = record.data.SORT_B;//获取record中的数据集中的process_name字段的值
					      }
					      return displayText;
					     }	
				},
				{
					id:'SORT_A',
					header:'板卡大类',
					dataIndex:'SORT_A',
					width:150,
					renderer:function(value){
						if(value == '1'){
							return '公共板卡';
						}else if(value == '2'){
							return '业务板卡';
						}
					}
				}
]});

// ==========================page=============================
var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : columnModel,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
//	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
//	stateId : "attenuationSearchGridId",
	stateful : true,
	bbar : pageTool,
	tbar : tbar
});

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'border',
	items : [searchPanel,gridPanel]
}); 

//==========================save ctp category=============================
function setCtpCategory(){
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {
		gridPanel.getEl().mask("正在执行,请稍候...");
		var unitTypeList = new Array();
		for ( var i = 0; i < cell.length; i++) {
			var unitTypeValue = {
					'NWA_UNIT_TYPE_DEFINE_ID' : cell[i].get('NWA_UNIT_TYPE_DEFINE_ID'),
					'NWA_UNIT_TYPE_ID' : cell[i].get('SORT_B')
			};
			unitTypeList.push(Ext.encode(unitTypeValue));
		}
		var jsonData = {
			"modifyList" : unitTypeList
		};
		// 提交修改，不然store.getModifiedRecords();数据会累加
		store.commitChanges();
		Ext.Ajax.request({
			url : 'network!setCtpCategory.action',
			params : jsonData,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				
				if(result.returnResult == 1){
			    	Ext.Msg.alert("信息", result.returnMessage, function(r) {
			    		//刷新数据
		    			var pageTool = Ext.getCmp('pageTool');
		            	if(pageTool){
		    				pageTool.doLoad(pageTool.cursor);
		    			}
			    	});
	            }
	            if(result.returnResult == 0){
	            	Ext.Msg.alert("提示",result.returnMessage);
	            }
			},
			failure : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			},
			error : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			}
		});
	}
}

//各类型槽道板卡端口图表生成
function importAcceptEqptType(){
	Ext.getBody().mask("正在导入........");
	var params={
	};
	
	Ext.Ajax.request({
		url : 'network!importAcceptEqptType.action',
		params : params,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", "导入成功！");
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
//	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [gridPanel],
		renderTo : Ext.getBody()
	});
	
	win.show();
	
	
});