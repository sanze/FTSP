// Document.domain="localhost";
/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

Ext.BLANK_IMAGE_URL = "../../ext/resources/images/default/s.gif";

// ************************************************************************************
/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
var pageLoaded = false;
// ================stores===================
var store = new Ext.data.Store({
			url : 'resource-circuit!selectResCirRoute.action',
			baseParams : {
				"resCirId" : VCircuit,
				"routeNum" : 1
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["RESOURCE_CIR_ROUTE_ID", "CTP_VALUE", "PORT_VALUE",
							"CTP_ORGI_VALUE", "PORT_ORGI_VALUE", "UNIT_NAME",
							"UNIT_NO", "NE_INTERNAL_NAME", "NE_NAME",
							"RESOURCE_CIR_ID", "NE_ID", "ROUTE_NO",
							"IS_COMPARE"])
		});

var lu = ['1', '路径-1'];
var Data_connectRate = new Array();
Data_connectRate[0] = lu;
for (var i = 2; i <= routeNumber; i++) {
	var lu = [i, '路径-' + i];
	Data_connectRate[i - 1] = lu;
}
var circuitstore = new Ext.data.ArrayStore({
			fields : [{
						name : 'value'
					}, {
						name : 'displayName'
					}]
		});
circuitstore.loadData(Data_connectRate);

// FTSP生成的电路
var store_ftsp = new Ext.data.Store({
			url : 'resource-circuit!selectCircuitRoute',
			baseParams : {
				"resCirId" : VCircuit,
				"routeNum" : 1
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["NE_NAME", "END_PORT", "END_CTP"])
		});

var circuitstore_ftsp = new Ext.data.Store({
			url : 'resource-circuit!getFtspRouteNumber.action',
			baseParams : {
				"resCirId" : VCircuit
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["displayname", "circuitId"])
		});
circuitstore_ftsp.load({
			callback : function(r, options, success) {
				if (success) {
					if (circuitstore_ftsp.getCount() > 0) {
						// 给下拉框赋初值
						Ext.getCmp('circuit_ftsp').setValue(circuitstore_ftsp
								.getAt(0).get('displayname'));
						var jsonData = {
							"resCirId" : circuitstore_ftsp.getAt(0)
									.get('circuitId')
						};
						exportJsonData = jsonData;
						store_ftsp.proxy = new Ext.data.HttpProxy({
									url : 'resource-circuit!selectCircuitRoute.action'
								});
						store_ftsp.baseParams = jsonData;
						store_ftsp.load({
									callback : function(r, options, success) {
										if (success) {
										} else {
											Ext.Msg.alert('错误', '查询失败！请重新查询');
										}
									}
								});

					}
				} 

			}
		});
// ------------------------------StroeForTest---------------------------------
var exportJsonData;
var formPanel = new Ext.FormPanel({
	id : "formPanel",
	region : "north",
	// labelAlign: 'top',
	frame : false,
	border : false,
	// title: '新建任务单',
	bodyStyle : 'padding:10px 10px 0 10px',
	// autoHeight : true,
	height : 70,
	labelWidth : 60,
	labelAlign : 'left',
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			columnWidth : 1, // 第一列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [{
				// 统计信息
				xtype : 'label',
				autoHeight : true,
				id : "CNT_INFO",
				html : '<table style="font-size:14px;" cellspacing="5px" align ="center" ><tr  style="height:20px;">'
						+ '<td >' + circuitJiheName + '</td></tr></table>'

			}]
		}]
	}, {
		layout : 'column',
		border : false,
		items : [{
			columnWidth : .5, // 第三列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [{
				xtype : 'combo',
				id : 'circuit',
				name : 'circuit',
				fieldLabel : '资源电路',
				store : circuitstore,
				displayField : "displayName",
				valueField : 'value',
				triggerAction : 'all',
				editable : false,
				// width:300,
				// sideText : '<font color=red>*</font>',
				mode : "local",
				listeners : {
					select : function(combo, record, index) {
						// TH.showVersion();

						var circuitId = Ext.getCmp('circuit').getValue();
						// 加载网元同步列表
						var jsonData = {
							"resCirId" : VCircuit,
							"routeNum" : circuitId
						};
						exportJsonData = jsonData;
						store.proxy = new Ext.data.HttpProxy({
									url : 'resource-circuit!selectResCirRoute.action'
								});
						store.baseParams = jsonData;
						store.load({
									callback : function(r, options, success) {
										if (success) {
											// initTopo(r);
										} else {
											Ext.Msg.alert('错误', '查询失败！请重新查询');
										}
									}
								});
					}
				}

			}]
		}, {
			columnWidth : .5, // 第一列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [new Ext.form.RadioGroup({
						fieldLabel : '排序',
						id : 'direction',
						name : 'direction',
						items : [{
									name : 'direction',
									inputValue : '0',
									boxLabel : '升序',
									checked : true

								}, {
									name : 'direction',
									inputValue : '1',
									boxLabel : '降序'

								}],
						listeners : {
							'change' : function() {
								var f = Ext.getCmp('direction').getValue().inputValue;
								if (f == 0) {
									store.sort('RESOURCE_CIR_ROUTE_ID', 'ASC');
								} else if (f == 1) {
									store.sort('RESOURCE_CIR_ROUTE_ID', 'DESC');
								}

							}
						}

					})]
		}]
	}

	]

});

var formPanel_ftsp = new Ext.FormPanel({
	id : 'formPanel_ftsp',
	region : "north",
	// labelAlign: 'top',
	frame : false,
	border : false,
	// title: '新建任务单',
	bodyStyle : 'padding:10px 10px 0 10px',
	// autoHeight : true,
	height : 70,
	labelWidth : 60,
	labelAlign : 'left',
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			columnWidth : 1, // 第一列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [{
				// 统计信息
				xtype : 'label',
				autoHeight : true,
				id : "tt",
				html : '<table style="font-size:14px;" cellspacing="5px" align = "center"><tr style="height:20px;">'
						+ '<td id="CNT_CR" class="x-grid-background-red" style="width:30px"></td><td>不相同</td>'
						+ '<td id="CNT_MN" class="x-grid-background-green" style="width:30px"></td><td>相同</td>'
						+ '<td id="CNT_WR"  class="x-grid-background-orange" style="width:30px"></td><td>未比对</td></tr></table>'

			}]
		}]
	}, {
		layout : 'column',
		border : false,
		items : [{
			columnWidth : .5, // 第三列
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [{
				xtype : 'combo',
				id : 'circuit_ftsp',
				name : 'circuit_ftsp',
				fieldLabel : '网管电路',
				store : circuitstore_ftsp,
				displayField : "displayname",
				valueField : 'circuitId',
				triggerAction : 'all',
				editable : false,
				// width:300,
				// sideText : '<font color=red>*</font>',
				anchor : '95%',
				listeners : {
					select : function(combo, record, index) {
						// TH.showVersion();

						var circuitId = Ext.getCmp('circuit_ftsp').getValue();

						// 加载网元同步列表
						var jsonData = {
							"resCirId" : circuitId
							// "RouteNumber" : circuitId,
						};
						exportJsonData = jsonData;
						store_ftsp.proxy = new Ext.data.HttpProxy({
									url : 'resource-circuit!selectCircuitRoute.action'
								});
						store_ftsp.baseParams = jsonData;
						store_ftsp.load({
									callback : function(r, options, success) {
										if (success) {
											// initTopo(r);
										} else {
											Ext.Msg.alert('错误', '查询失败！请重新查询');
										}
									}
								});
					}
				}

			}]
		}]
	}

	]

});

// ==========================page=============================================================
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var checkboxSelectionModel_ftsp = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
			// specify any defaults for each column
			defaults : {
				sortable : true
				// columns are not sortable by default
			},

			columns : [new Ext.grid.RowNumberer({
				width : 26
			}), checkboxSelectionModel, {
						id : 'RESOURCE_CIR_ROUTE_ID',
						header : '路由id',
						// sortable : false,
						dataIndex : 'RESOURCE_CIR_ROUTE_ID',
						// hidden:true,//hidden colunm
						hidden : true,
						width : 200
					}, {
						id : 'NE_INTERNAL_NAME',
						header : '稽核电路网元标识',
						// sortable : false,
						dataIndex : 'NE_INTERNAL_NAME',
						hidden : true,
						width : 150
					}, {
						id : 'NE_NAME',
						header : '网元名',
						sortable : false,
						dataIndex : 'NE_NAME',
						width : 150
					}, {
						id : 'UNIT_NO',
						header : '槽道号',
						// sortable : false,
						dataIndex : 'UNIT_NO',
						hidden : true,
						width : 150
					}, {
						id : 'UNIT_NAME',
						header : '板卡名',
						// sortable : false,
						dataIndex : 'UNIT_NAME',
						width : 150
					}, {
						id : 'PORT_VALUE',
						header : '端口',
						// sortable : false,
						dataIndex : 'PORT_VALUE',
						width : 130
					}, {
						id : 'CTP_VALUE',
						header : '时隙',
						// sortable : false,
						dataIndex : 'CTP_VALUE',
						width : 150
					}, {
						id : 'CTP_ORGI_VALUE',
						header : '时隙原始值',
						// sortable : false,
						dataIndex : 'CTP_ORGI_VALUE',
						hidden : true,
						width : 150
					}, {
						id : 'PORT_ORGI_VALUE',
						header : '端口原始值',
						// sortable : false,
						hidden : true,
						dataIndex : 'PORT_ORGI_VALUE',
						width : 150
					}, {
						id : 'RESOURCE_CIR_ID',
						header : '电路id',
						// sortable : false,
						hidden : true,
						dataIndex : 'RESOURCE_CIR_ID',
						width : 150
					}, {
						id : 'NE_ID',
						header : '网元id',
						// sortable : false,
						hidden : true,
						dataIndex : 'NE_ID',
						width : 150
					}, {
						id : 'ROUTE_NO',
						header : '路径号',
						// sortable : false,
						hidden : true,
						dataIndex : 'ROUTE_NO',
						width : 150
					}, {
						id : 'IS_COMPARE',
						header : '是否比对过',
						// sortable : false,
						hidden : true,
						dataIndex : 'IS_COMPARE',
						width : 150
					}]
		});

var pageTool = new Ext.PagingToolbar({
			id : 'pageTool',
			pageSize : 200,// 每页显示的记录值
			store : store,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var gridPanel = new Ext.grid.GridPanel({
			id : "gridPanel",
			region : "center",
			flex : 6,
			// autoHeight:true,
			// height : "100%",
			// title:'用户管理',
			cm : cm,
			store : store,
			// autoExpandColumn: 'roleName', // column with this id will be
			// expanded
			stripeRows : true, // 交替行效果
			// loadMask: {msg: '数据加载中...'},
			selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
			// forceFit : true,
			viewConfig : {
				forceFit : true,
				enableRowBody : true,
				getRowClass : function(record, index) {
					var progress = record.get('isCompare');
					if (progress == "2") {
						return "x-grid-background-red";
					} else if (progress == "1") {
						return "x-grid-background-green";
					} else {
						return "x-grid-background-orange";
					}

				}
			},
			bbar : pageTool
		});

// ----------------------------------------------------------------

var cm_ftsp = new Ext.grid.ColumnModel({
			// specify any defaults for each column
			defaults : {
				sortable : true
				// columns are not sortable by default
			},

			columns : [new Ext.grid.RowNumberer({
				width : 26
			}), checkboxSelectionModel_ftsp,
					{
						id : 'NE_NAME',
						header : '网元名称',
						// sortable : false,
						dataIndex : 'NE_NAME',
						// hidden:true,//hidden colunm
						width : 200
					}, {
						id : 'END_PORT',
						header : '端口',
						// sortable : false,
						dataIndex : 'END_PORT',
						width : 150
					}, {
						id : 'END_CTP',
						header : '时隙',
						// sortable : false,
						dataIndex : 'END_CTP',
						width : 150
					}]
		});

var pageTool_ftsp = new Ext.PagingToolbar({
			id : 'pageTool_ftsp',
			pageSize : 200,// 每页显示的记录值
			store : store_ftsp,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var gridPanel_ftsp = new Ext.grid.GridPanel({
			id : "gridPanel_ftsp",
			region : "center",
			flex : 6,
			// autoHeight:true,
			// height : "100%",
			// title:'用户管理',
			cm : cm_ftsp,
			store : store_ftsp,
			// autoExpandColumn: 'roleName', // column with this id will be
			// expanded
			stripeRows : true, // 交替行效果
			// loadMask: {msg: '数据加载中...'},
			selModel : checkboxSelectionModel_ftsp, // 必须加不然不能选checkbox
			// forceFit : true,
			viewConfig : {
				forceFit : true
			},
			bbar : pageTool_ftsp
		});

// 将左侧的两个panel合成一个
var leftPanel = new Ext.Panel({
			id : 'leftPanel',
			// width:440,
			flex : 1,
			autoScroll : true,
			layout : 'border',
			height : "100%",
			items : [formPanel, gridPanel]
		});

// 将右侧的两个panel合成一个
var rightPanel = new Ext.Panel({
			id : 'rightPanel',
			// width:440,
			flex : 1,
			autoScroll : true,
			layout : 'border',
			height : "100%",
			items : [formPanel_ftsp, gridPanel_ftsp]
		});
var allPanel = new Ext.Panel({
			id : 'allPanel',
			region : 'center',
			autoScroll : true,
			layout : {
				type : 'hbox',
				padding : '5',
				align : 'stretch'
			},
			// autoHeight:true,
			height : "100%",
			items : [leftPanel, rightPanel]
		});
// -----------------------------initial----------------------------------
function initData() {
	Ext.getCmp('circuit').setValue('路径-1');
}
function exportRoute() {

}
// ************************************************************************************
// Ext.Loader.load(["../../thjs/TH.js", "../../thjs/Class.js",
// "../../thjs/Util.js", "../../thjs/Lib.js", "../../thjs/Manager.js"]);
Ext.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			}
			Ext.Msg = top.Ext.Msg;
			Ext.Ajax.timeout = 9000000;
			var win = new Ext.Viewport({
						id : 'win',
						// title : "B类比较值设定",
						layout : 'border',
						items : [allPanel],
						renderTo : Ext.getBody()
					});
			store.load();
			store_ftsp.load();
			initData();
			win.show();
			pageLoaded = true;
		});