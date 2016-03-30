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
var flexCB = null;
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
// ------------------------------StroeForTest---------------------------------
var exportJsonData;
var formPanel = new Ext.FormPanel({
	region : "north",
	// labelAlign: 'top',
	frame : false,
	border : false,
	// title: '新建任务单',
	bodyStyle : 'padding:10px 10px 0 10px',
	// autoHeight : true,
	height : 105,
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
							columnWidth : .4, // 第一列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'ROUTE_NAME',
										name : 'ROUTE_NAME',
										fieldLabel : '路由名称',
										allowBlank : true,
										readonly : true,
										disabled : true,
										anchor : '95%',
										dataIndex : ''
									}]
						}, {
							columnWidth : .3, // 第二列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'CLIENT_NAME',
										name : 'CLIENT_NAME',
										fieldLabel : '客户名称',
										readonly : true,
										disabled : true,
										allowBlank : true,
										anchor : '95%'
									}]
						}, {
							columnWidth : .3, // 第三列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'USED_FOR',
										name : 'USED_FOR',
										fieldLabel : '电路用途',
										disabled : true,
										readonly : true,
										allowBlank : true,
										anchor : '95%'
									}]
						}]
			}, {
				layout : 'column',
				border : false,
				items : [{
							columnWidth : .4, // 第一列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'A_PORT',
										name : 'A_PORT',
										fieldLabel : 'A端节点',
										allowBlank : true,
										readonly : true,
										disabled : true,
										anchor : '95%',
										dataIndex : ''
									}]
						}, {
							columnWidth : .3, // 第二列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'A_USER',
										name : 'A_USER',
										fieldLabel : 'A端用户',
										readonly : true,
										disabled : true,
										allowBlank : true,
										anchor : '95%'
									}]
						}, {
							columnWidth : .3, // 第三列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'CIR_MODEL',
										name : 'CIR_MODEL',
										fieldLabel : '电路类型',
										disabled : true,
										readonly : true,
										allowBlank : true,
										anchor : '95%'
									}]
						}]
			}, {
				layout : 'column',
				border : false,
				items : [{
							columnWidth : .4, // 第一列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'Z_PORT',
										name : 'Z_PORT',
										fieldLabel : 'Z端节点',
										allowBlank : true,
										readonly : true,
										disabled : true,
										anchor : '95%',
										dataIndex : ''
									}]
						}, {
							columnWidth : .3, // 第二列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
										xtype : 'textfield',
										id : 'Z_USER',
										name : 'Z_USER',
										fieldLabel : 'Z端用户',
										readonly : true,
										disabled : true,
										allowBlank : true,
										anchor : '95%'
									}]
						}, {
							columnWidth : .3, // 第三列
							layout : 'form',
							labelSeparator : "：",
							border : false,
							items : [{
								xtype : 'combo',
								id : 'circuit',
								name : 'circuit',
								fieldLabel : '路径',
								store : circuitstore,
								displayField : "displayName",
								valueField : 'value',
								triggerAction : 'all',
								editable : false,
								anchor : '95%',
								// width:300,
								// sideText : '<font color=red>*</font>',
								mode : "local",
								listeners : {
									select : function(combo, record, index) {
										// TH.showVersion();

										var circuitId = Ext.getCmp('circuit')
												.getValue();
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
													callback : function(r,
															options, success) {
														if (success) {
															// initTopo(r);
														} else {
															Ext.Msg
																	.alert(
																			'错误',
																			'查询失败！请重新查询');
														}
													}
												});
									}
								}

							}]
						}]
			}]

		/**
		 * tbar: [{ text: '查询（不包含归档）',, handler:
		 * function(){ searchTaskInfo('0'); } },{ text: '查询（包含归档）', handler:
		 * function(){ searchTaskInfo('1'); } } ]
		 */
});
var datLoaded = false;
var gDat = null;

// ==========================page=============================
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();

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
						sortable : false,
						dataIndex : 'RESOURCE_CIR_ROUTE_ID',
						// hidden:true,//hidden colunm
						hidden : true,
						width : 200
					}, {
						id : 'NE_INTERNAL_NAME',
						header : '稽核电路网元标识',
						sortable : false,
						dataIndex : 'NE_INTERNAL_NAME',
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
						sortable : false,
						dataIndex : 'UNIT_NO',
						width : 150
					}, {
						id : 'UNIT_NAME',
						header : '板卡名',
						sortable : false,
						dataIndex : 'UNIT_NAME',
						width : 150
					}, {
						id : 'PORT_VALUE',
						header : '端口',
						sortable : false,
						dataIndex : 'PORT_VALUE',
						width : 130
					}, {
						id : 'CTP_VALUE',
						header : '时隙',
						sortable : false,
						dataIndex : 'CTP_VALUE',
						width : 150
					}, {
						id : 'CTP_ORGI_VALUE',
						header : '时隙原始值',
						sortable : false,
						dataIndex : 'CTP_ORGI_VALUE',
						hidden : true,
						width : 150
					}, {
						id : 'PORT_ORGI_VALUE',
						header : '端口原始值',
						sortable : false,
						hidden : true,
						dataIndex : 'PORT_ORGI_VALUE',
						width : 150
					}, {
						id : 'RESOURCE_CIR_ID',
						header : '电路id',
						sortable : false,
						hidden : true,
						dataIndex : 'RESOURCE_CIR_ID',
						width : 150
					}, {
						id : 'NE_ID',
						header : '网元id',
						sortable : false,
						hidden : true,
						dataIndex : 'NE_ID',
						width : 150
					}, {
						id : 'ROUTE_NO',
						header : '路径号',
						sortable : false,
						hidden : true,
						dataIndex : 'ROUTE_NO',
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
				forceFit : true
			},
			bbar : pageTool
		});

// =======================链路更新=========================
function modifyLink() {

}
// -------------------------------取消重置-----------------------------------
function closeTB() {

}
// -------------------------------失败网管再次更新-----------------------------------

// ----------------------------Flex回调---------------------------------
function rtrvData(callback) {
	// console.log("rtrvData @");
	// console.log(callback);
	// console.log("datLoaded = " + datLoaded);
	flexCB = callback;
	if (datLoaded) {
		flexCB(gDat);
	}
}
// -----------------------------initial----------------------------------
function initData() {
	Ext.getCmp('circuit').setValue('路径-1');
	if (VCircuit != null) {
		var jsonData = {
			"resCirId" : VCircuit
		};
		Ext.Ajax.request({
					url : 'resource-circuit!getSingleCir.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						Ext.getCmp('ROUTE_NAME').setValue(obj.ROUTE_NAME);
						Ext.getCmp('CLIENT_NAME').setValue(obj.CLIENT_NAME);
						Ext.getCmp('A_USER').setValue(obj.A_USER);
						Ext.getCmp('A_PORT').setValue(obj.A_PORT);
						Ext.getCmp('CIR_MODEL').setValue(obj.CIR_MODEL);
						Ext.getCmp('USED_FOR').setValue(obj.USED_FOR);
						Ext.getCmp('Z_PORT').setValue(obj.Z_PORT);
						Ext.getCmp('Z_USER').setValue(obj.Z_USER);
						// Ext.getCmp('circuitType').setValue(obj.circuitType);

					},
					error : function(response) {
						Ext.Msg.alert("异常", response.responseText);
					},
					failure : function(response) {
						Ext.Msg.alert("异常", response.responseText);
					}
				});
		// 未分配用户组列表

	}

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
						title : "B类比较值设定",
						layout : 'border',
						items : [formPanel, gridPanel],
						renderTo : Ext.getBody()
					});
			store.load();
			initData();
			win.show();
			pageLoaded = true;
		});