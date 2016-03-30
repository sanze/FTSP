/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var myPageSize = 400;

//var pageTool = new Ext.PagingToolbar({
//	id : 'pageTool',
//	pageSize : myPageSize,// 每页显示的记录值
//	store : store,
//	displayInfo : true,
//	displayMsg : '当前 {0} - {1} ，总数 {2}',
//	emptyMsg : "没有记录"
//});
//function connectionColorGrid(v, m) {
//	if (v == '连接正常') {
//		m.css = 'x-grid-font-blue';
//	} else if (v == '网络中断') {
//		m.css = 'x-grid-font-red';
//	} else if (v == '连接异常') {
//		m.css = 'x-grid-font-orange';
//	}
//	return v;
//}

//var emsGroupStore = new Ext.data.Store({
//	url : 'connection!getConnectGroup.action',
//	baseParams : {
//		"emsGroupId" : "-1"
//	},
//	reader : new Ext.data.JsonReader({
//		totalProperty : 'total',
//		root : "rows"
//	}, [ "GROUP_NAME", "BASE_EMS_GROUP_ID" ])
//});
//
//emsGroupStore.load({
//	callback : function(r, options, success) {
//		if (success) {
//
//		} else {
//			Ext.Msg.alert('提示', '查询网管分组失败！请重新查询');
//		}
//	}
//});

/**
 * 创建网管分组数据源
 */
var emsGroupStore = new Ext.data.Store({
	// 获取数据源地址
	proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
		url : 'common!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
		disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
	}),
	baseParams : {"displayAll" : true,"displayNone" : true},
	// record格式
	reader : new Ext.data.JsonReader({
		root : 'rows',//json数据的key值
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});
// 访问地址，加载数据(如果没有这一句，则不会去后台查询)
emsGroupStore.load({
	// 回调函数
	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
		// 获取下拉框的第一条记录
		var firstValue = records[0].get('BASE_EMS_GROUP_ID');
		// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
		Ext.getCmp('emsGroup').setValue(firstValue);
		
		//加载ems数据
		emsStore.baseParams.emsGroupId = firstValue;
		emsStore.load();
	}
});

//var emsConnectStore = new Ext.data.Store({
//	url : 'connection!getEmsConnection.action',
////	baseParams : {
////		"emsGroupId" : "-1"
////	},
//	reader : new Ext.data.JsonReader({
//		totalProperty : 'total',
//		root : "rows"
//	}, [ "emsConnectionId", "emsConnectionName", "connnectionType" ])
//});
////emsConnectStore.load({
////	callback : function(r, options, success) {
////		if (success) {
////
////		} else {
////			Ext.Msg.alert('提示', '查询网管失败！请重新查询！');
////		}
////	}
////});

/**
 * 创建网管数据源
 */
var emsStore = new Ext.data.Store({
	url : 'common!getAllEmsByEmsGroupId.action',
	baseParams : {'emsGroupId':-99,"displayAll" : false},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['BASE_EMS_CONNECTION_ID','DISPLAY_NAME','CONNETION_TYPE','domainAuth']
	}),
	listeners:{
	  	"exception": function(proxy,type,action,options,response,arg){
	  		Ext.Msg.alert("提示","加载出错"+
				"<BR>Status:"+response.statusText||"unknow");
	  	},
	  	"load": function(store, records, options){
	  	}
	}
});

///**
// * 加载网管数据源
// */
//emsStore.load({
//	// 回调函数
//	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
////		var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
////		Ext.getCmp('emsCombo').setValue(firstValue);
//	}
//});

//var store = new Ext.data.Store(
//		{
//			url : 'connection!getSyncNeListByEmsInfo.action',
//			baseParams : {
//				"emsGroupId" : "-1",
//				"emsConnectionId" : "-1",
//				"limit" : myPageSize
//			},
//			reader: new Ext.data.JsonReader({
//		        totalProperty: 'total',
//				root : "rows"
//		    },[
//"neId", "neName","neSerialNo","suportRates", "emsConnectionId", "emsConnectionName",
//"emsGroupId", "emsGroupName", "stationName", "areaName", "factory",
//"productName", "VERSION", "syncStatus", "syncTime", "userName",
//"loginStatus", "loginMode","isDel"   
//		    ])
//		});

var store = new Ext.data.Store({
	url : 'connection!getSyncNeListByEmsInfo.action',
	baseParams : {
		"emsGroupId" : "-99",
		"emsConnectionId" : "-99",
		"limit" : myPageSize
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "BASE_NE_ID", "NATIVE_EMS_NAME","DISPLAY_NAME", "USER_LABEL","NAME", "SUPORT_RATES", "TYPE",
			"BASE_EMS_CONNECTION_ID", "emsConnectionName", "CONNETION_TYPE","BASE_EMS_CONNECTION_ID",
			"GROUP_NAME", "STATION_NAME", "AREA_NAME", "FACTORY",
			"PRODUCT_NAME", "VERSION", "BASIC_SYNC_STATUS", "BASIC_SYNC_RESULT","BASIC_SYNC_TIME", "USER_NAME",
			"COMMUNICATION_STATE", "CONNECTION_MODE", "IS_DEL","SYNC_MODE","BASIC_SYNC_TIME_DISPLAY"]),
		listeners:{
		  	"exception": function(proxy,type,action,options,response,arg){
		  		Ext.Msg.alert("提示","加载出错"+
					"<BR>Status:"+response.statusText||"unknow");
		  	}
		}
});

 var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: myPageSize,//每页显示的记录值
	store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
 });

 var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	 singleSelect : false
 });
 var columnModel = new Ext.ux.grid.LockingColumnModel({
        // specify any defaults for each column
		defaults : {
			sortable : true
		// columns are not sortable by default
		},
		columns : [ new Ext.grid.RowNumberer({
			width : 26,
			locked : true
		}), checkboxSelectionModel, {
			id : 'BASE_NE_ID',
			header : 'BASE_NE_ID',
			dataIndex : 'BASE_NE_ID',
			width : 150,
			hidden : true
		}, {
			id : 'CONNETION_TYPE',
			header : 'CONNETION_TYPE',
			dataIndex : 'CONNETION_TYPE',
			width : 150,
			hidden : true
		}, {
			id : 'GROUP_NAME',
			header : '网管分组',
			dataIndex : 'GROUP_NAME',
			width : 80,
			locked : true
		}, {
			id : 'emsConnectionName',
			header : '网管',
			dataIndex : 'emsConnectionName',
			width : 120,
			locked : true
		}, {
			id : 'DISPLAY_NAME',
			header : '网元名称',
			dataIndex : 'DISPLAY_NAME',
			width : 80,
			locked : true
		}, {
			id : 'USER_LABEL',
			header : '规范网元名称',
			dataIndex : 'USER_LABEL',
			width : 80,
			locked : true
		}, {
			id : 'AREA_NAME',
			header : top.FieldNameDefine.AREA_NAME,
			dataIndex : 'AREA_NAME',
			width : 100
		}, {
			id : 'STATION_NAME',
			header : top.FieldNameDefine.STATION_NAME,
			dataIndex : 'STATION_NAME',
			width : 100
		}, {
			id : 'FACTORY',
			header : '厂家',
			dataIndex : 'FACTORY',
			width : 80,
			renderer:factoryRenderer
		}, {
			id : 'PRODUCT_NAME',
			header : '网元型号',
			dataIndex : 'PRODUCT_NAME',
			width : 140
		}, {
			id : 'VERSION',
			header : '网元版本',
			dataIndex : 'VERSION',
			width : 80
		}, {
			id : 'BASIC_SYNC_STATUS',
			header : '同步状态',
			dataIndex : 'BASIC_SYNC_STATUS',
			renderer : colorGrid,
			width : 200
		}, {
			id : 'SYNC_MODE',
			header : '同步模式',
			dataIndex : 'SYNC_MODE',
			width : 80,
			renderer:syncModeRenderer
		},{
			id : 'BASIC_SYNC_TIME_DISPLAY',
			header : '成功同步时间',
			dataIndex : 'BASIC_SYNC_TIME_DISPLAY',
			width : 120
		}, {
			id : 'USER_NAME',
			header : '登录用户名',
			dataIndex : 'USER_NAME',
			width : 100
		}, {
			id : 'COMMUNICATION_STATE',
			header : '登录状态',
			width : 80,
			dataIndex : 'COMMUNICATION_STATE',
			renderer:loginStatusRenderer
		}, {
			id : 'CONNECTION_MODE',
			header : '登录模式',
			width : 80,
			dataIndex : 'CONNECTION_MODE',
			renderer:loginModeRenderer
		}, {
			id : 'IS_DEL',
			header : '网管侧状态',
			dataIndex : 'IS_DEL',
			width : 80,
			renderer : function(v,m) {
					if (v == 0) {
						return "正常";
					} else if (v == 2) {
						m.css='x-grid-font-red';
						return "已删除";
					} else {
						return "";
					}
			}
		} ]
	});
 

 Ext.state.Manager.setProvider(new Ext.state.SessionStorageStateProvider({
	expires : new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365))
}));

var connectListPanel = new Ext.grid.GridPanel({
	id:"connectListPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox
//	viewConfig: {
//        forceFit:false
//    },
    view : new Ext.ux.grid.LockingGridView({
		scrollDelay:false,
		cacheSize:100,
		syncHeights: false,
		cleanDelay:500//,
		//autoScrollTop:false
	}),
    stateId:'neSyncManagementId',  
    stateful:true,
	bbar: pageTool, 
	tbar : [ '-', '网管分组：', {
		xtype : 'combo',
		id : 'emsGroup',
		name : 'emsGroup',
		emptyText:'请选择网管分组',
		// fieldLabel: '请选择网管分组',
		// sideText : '<font color=red>*</font>',
		mode : "local",
		editable : false,
		store : emsGroupStore,
		displayField : "GROUP_NAME",
		valueField : 'BASE_EMS_GROUP_ID',
		triggerAction : 'all',
		allowBlank:true,
		editable:false,
		width : 120,
		anchor : '95%',
		listeners : {
			select : function(combo, record, index) {
				var emsGroupId = Ext.getCmp('emsGroup').getValue();
				emsStore.baseParams = {
						"emsGroupId":emsGroupId
					};
				emsStore.load();
				Ext.getCmp('emsConnect').reset();			
			}
		}
	}, '-', '网管：', {
		xtype : 'combo',
		id : 'emsConnect',
		name : 'emsConnect',
		emptyText:'请先选择网管',
		// fieldLabel: '请选择网管',
		// sideText : '<font color=red>*</font>',
		mode : "local",
		editable : false,
		store : emsStore,
		displayField : "DISPLAY_NAME",
		valueField : 'BASE_EMS_CONNECTION_ID',
		triggerAction : 'all',
		allowBlank:true,
		editable:false,
		width : 120,
		anchor : '95%',
		listeners : {
			select : function(combo, record, index) {
				var emsGroupId = Ext.getCmp('emsGroup').getValue();
				var emsConnectId = Ext.getCmp('emsConnect').getValue();

				// 加载网元同步列表
				var jsonData = {
					"emsGroupId" : emsGroupId,
					"emsConnectionId" : emsConnectId,
					"limit" : myPageSize
				};

				store.proxy = new Ext.data.HttpProxy({
					url : 'connection!getSyncNeListByEmsInfo.action'
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
		id : "syncNeList",
		privilege:actionAuth,
		text : '列表同步',
		// disabled:true,
		icon : '../../resource/images/btnImages/sync.png',
		handler : function() {
			getNeAlterList();
		}
	}, {
		text : '网元同步',
		privilege:actionAuth,
		icon : '../../resource/images/btnImages/sync.png',
		handler : function() {
			syncNe();
		}
	}, '-', {
		id : "addNe",
		privilege:addAuth,
		text : '新增网元',
		// disabled:true,
		icon : '../../resource/images/btnImages/add.png',
		handler : function() {
			addTelnetNe();
		}
	}, {
		id : "delNe",
		privilege:delAuth,
		text : '删除网元',
		// disabled:true,
		icon : '../../resource/images/btnImages/delete.png',
		handler : function() {
			deleteTelnetNe();
		}
	},{
		id : "editNe",
		privilege:modAuth,
		text : '修改网元',
		// disabled:true,
		icon : '../../resource/images/btnImages/modify.png',
		handler : function() {
			modifyTelnetNe();
		}
	},  {
		text : '同步模式',
		icon : '../../resource/images/btnImages/sync.png',
		privilege : modAuth,
		menu : {
			items : [ {
				text : '自动同步',
				handler : function(){
					modifyNeSyncMode(2);
				}
			}, {
				text : '手工同步',
				handler : function(){
					modifyNeSyncMode(1);
				}
			}]
		}
	},  {
		id : "startConnection",
		text : '登录网元',
		privilege:actionAuth,
		// disabled:true, 
		handler : function() {
			logonTelnetNe();
		}
	}, {
		id : "stopConnection",
		text : '退出登录',
		privilege:actionAuth,
		// disabled:true, 
		handler : function() {
			logoutTelnetNe();
		}
	} ]
});

// 登录telnet网元
function logonTelnetNe() {
	var jsonString = new Array();
	var cell = connectListPanel.getSelectionModel().getSelections();

	if (cell.length > 0) {
		if (cell.length == 1) {
			var emsConnectionId = cell[0].get("BASE_EMS_CONNECTION_ID");
			var connectionType = cell[0].get("CONNETION_TYPE");
			var loginStatus = cell[0].get("COMMUNICATION_STATE");
			if (connectionType == 1) {
				Ext.Msg.alert('提示', '所选网元不支持该操作！');
			} else {
				if (loginStatus == 1) {
					Ext.Msg.alert("提示", "所选取网元已处于登录状态！");
				} else {
					for ( var i = 0; i < cell.length; i++) {
						var neModel = {
							"emsConnectionId" : cell[i].get('BASE_EMS_CONNECTION_ID'),
							"neSerialNo" : cell[i].get('NAME'),
							"neId" : cell[i].get('BASE_NE_ID')
						};
						jsonString.push(neModel);
					}
					var jsonData = {
						"jString" : Ext.encode(jsonString)
					};
					Ext.getBody().mask('正在执行，请稍候...');
					Ext.Ajax.request({
						url : 'connection!logonTelnetNe.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {
							Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("信息", obj.returnMessage, function(r) {
								// 刷新列表
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							});
						},
						error : function(response) {
							Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("错误", response.responseText);
						},
						failure : function(response) {
							Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("错误", response.responseText);
						}
					});
				}
			}
		} else if (cell.length > 1) {

			for ( var i = 0; i < cell.length; i++) {
				if (cell[i].get("CONNETION_TYPE") == 2) {

					var neModel = {
						"emsConnectionId" : cell[i].get('BASE_EMS_CONNECTION_ID'),
						"neSerialNo" : cell[i].get('NAME'),
						"neId" : cell[i].get('BASE_NE_ID')
					};
					jsonString.push(neModel);
				}
			}
			var jsonData = {
				"jString" : Ext.encode(jsonString)
			};
			Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
				url : 'connection!logonTelnetNe.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("信息", obj.returnMessage, function(r) {
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					});
				},
				error : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", response.responseText);
				}
			});
		}
	} else {
		Ext.Msg.alert("提示", "请先选取需要登录的网元！");
	}
}
// 退出登录telnet网元
function logoutTelnetNe() {
	var jsonString = new Array();
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		if (cell.length == 1) {
			var emsConnectionId = cell[0].get("BASE_EMS_CONNECTION_ID");
			var connectionType = cell[0].get("CONNETION_TYPE");
			var loginStatus = cell[0].get("COMMUNICATION_STATE");
			if (connectionType == 1) {
				Ext.Msg.alert('提示', '所选网元不支持该操作！');
			} else {
				if (loginStatus !==1) {
					Ext.Msg.alert("提示", "所选取网元已处于退出登录状态！");
				} else {
					Ext.Msg
							.confirm(
									'提示',
									'退出登录将无法接收告警，采集性能数据。是否确定退出登录？',
									function(btn) {
										if (btn == 'yes') {
											for ( var i = 0; i < cell.length; i++) {
												var neModel = {
														"emsConnectionId" : cell[i].get('BASE_EMS_CONNECTION_ID'),
														"neSerialNo" : cell[i].get('NAME'),
														"neId" : cell[i].get('BASE_NE_ID')
												};
												jsonString.push(neModel);
											}
											var jsonData = {
												"jString" : Ext
														.encode(jsonString)
											};
											Ext.getBody().mask('正在执行，请稍候...');
											Ext.Ajax
													.request({
														url : 'connection!logonTelnetNe.action',
														method : 'POST',
														params : jsonData,
														success : function(
																response) {
															Ext.getBody()
																	.unmask();
															var obj = Ext
																	.decode(response.responseText);
															Ext.Msg
																	.alert(
																			"信息",
																			obj.returnMessage,
																			function(
																					r) {
																				// 刷新列表
																				var pageTool = Ext
																						.getCmp('pageTool');
																				if (pageTool) {
																					pageTool
																							.doLoad(pageTool.cursor);
																				}
																			});
														},
														error : function(
																response) {
															Ext.getBody()
																	.unmask();
															var obj = Ext
																	.decode(response.responseText);
															Ext.Msg
																	.alert(
																			"错误",
																			response.responseText);
														},
														failure : function(
																response) {
															Ext.getBody()
																	.unmask();
															var obj = Ext
																	.decode(response.responseText);
															Ext.Msg
																	.alert(
																			"错误",
																			response.responseText);
														}
													});
										} else {
										}
									});
				}
			}

		} else if (cell.length > 1) {

			Ext.Msg
					.confirm(
							'提示',
							'退出登录将无法接收告警，采集性能数据。是否确定退出登录？',
							function(btn) {
								if (btn == 'yes') {
									for ( var i = 0; i < cell.length; i++) {
										if (cell[0].get("CONNETION_TYPE") == 2 ) {
											var neModel = {
													"emsConnectionId" : cell[i].get('BASE_EMS_CONNECTION_ID'),
													"neSerialNo" : cell[i].get('NAME'),
													"neId" : cell[i].get('BASE_NE_ID')
											};
											jsonString.push(neModel);
										}
									}
									var jsonData = {
										"jString" : Ext.encode(jsonString)
									};
									Ext.getBody().mask('正在执行，请稍候...');
									Ext.Ajax
											.request({
												url : 'connection!logonTelnetNe.action',
												method : 'POST',
												params : jsonData,
												success : function(response) {
													Ext.getBody().unmask();
													var obj = Ext
															.decode(response.responseText);
													Ext.Msg
															.alert(
																	"信息",
																	obj.returnMessage,
																	function(r) {
																		// 刷新列表
																		var pageTool = Ext
																				.getCmp('pageTool');
																		if (pageTool) {
																			pageTool
																					.doLoad(pageTool.cursor);
																		}
																	});
												},
												error : function(response) {
													Ext.getBody().unmask();
													var obj = Ext
															.decode(response.responseText);
													Ext.Msg
															.alert(
																	"错误",
																	response.responseText);
												},
												failure : function(response) {
													Ext.getBody().unmask();
													var obj = Ext
															.decode(response.responseText);
													Ext.Msg
															.alert(
																	"错误",
																	response.responseText);
												}
											});
								}
							});
		}
	} else {
		Ext.Msg.alert("提示", "请先选取需要退出登录的网元！");
	}
}

// 删除telnet网元
function deleteTelnetNe() {
	var jsonString = new Array();
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		if (cell.length > 1) {
			Ext.Msg.alert("提示", "请勿多选！");
		} else {
			var emsConnectionId = cell[0].get("BASE_EMS_CONNECTION_ID");
			if (cell[0].get('COMMUNICATION_STATE') == 0) {
				Ext.Msg.alert("提示", "网元在登录状态，请先退出登录、或者列表同步！");
			} else {
				Ext.Msg
						.confirm(
								'提示',
								'删除网元，将会删除该网元所有数据。是否确认删除？',
								function(btn) {
									if (btn == 'yes') {
										// for ( var i = 0; i < cell.length;
										// i++) {
										// var syncNeInfoModel = {
										// "neSerialNo" :
										// cell[i].get('neSerialNo'),
										// "neId" : cell[i].get('neId')
										// };
										// jsonString.push(syncNeInfoModel);
										// }
										var jsonData = {
											"neModel.neId" : cell[0]
													.get('BASE_NE_ID'),
											"neModel.emsConnectionId" : emsConnectionId
										};
										Ext.getBody().mask('正在执行，请稍候...');
										Ext.Ajax
												.request({
													url : 'connection!deleteTelnetNe.action',
													method : 'POST',
													params : jsonData,
													success : function(response) {
														Ext.getBody().unmask();
														var obj = Ext
																.decode(response.responseText);
														Ext.Msg
																.alert(
																		"信息",
																		obj.returnMessage,
																		function(
																				r) {
																			// 刷新列表
																			var pageTool = Ext
																					.getCmp('pageTool');
																			if (pageTool) {
																				pageTool
																						.doLoad(pageTool.cursor);
																			}
																		});
													},
													error : function(response) {
														Ext.getBody().unmask();
														var obj = Ext
																.decode(response.responseText);
														Ext.Msg
																.alert(
																		"错误",
																		response.responseText);
													},
													failure : function(response) {
														Ext.getBody().unmask();
														var obj = Ext
																.decode(response.responseText);
														Ext.Msg
																.alert(
																		"错误",
																		response.responseText);
													}
												});
									}
								});
			}
		}
	} else {
		Ext.Msg.alert("提示", "请先选取需要删除的网元！");
	}
}

//修改telnet网元
function modifyTelnetNe() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var emsConnectionId = cell[0].get("BASE_EMS_CONNECTION_ID");

		var num = store.find("emsConnectionId", emsConnectionId);

		var connectionType = cell[0].get("CONNETION_TYPE");

		if (connectionType == 1) {
			var neId = cell[0].get("BASE_NE_ID");
			
			var url = 'modifyCorbaNe.jsp?neId=' + neId + '&emsConnectionId='
					+ cell[0].get("BASE_EMS_CONNECTION_ID");

			var modifyCorbaNeWindow = new Ext.Window(
					{
						id : 'modifyCorbaNeWindow',
						title : '修改网元设置',
						width : 400,
						height : 150,
						isTopContainer : true,
						modal : true,
						autoScroll : true,
						html : '<iframe id="modifyNe_panel" name = "modifyNe_panel"  src = '
								+ url
								+ ' height="100%" width="100%" frameBorder=0 border=0/>'
					});
			modifyCorbaNeWindow.show();
		} else {
			var neId = cell[0].get("neId");
			var url = 'modifyTelnetNe.jsp?neId=' + neId + '&emsConnectionId='
					+ cell[0].get("BASE_EMS_CONNECTION_ID");

			var modifyTelnetNeWindow = new Ext.Window(
					{
						id : 'modifyTelnetNeWindow',
						title : '修改网元设置',
						width : 400,
						height : 225,
						isTopContainer : true,
						modal : true,
						autoScroll : true,
						html : '<iframe id="modifyNe_panel" name = "modifyNe_panel"  src = '
								+ url
								+ ' height="100%" width="100%" frameBorder=0 border=0/>'
					});
			modifyTelnetNeWindow.show();
		}
	} else if (cell.length > 1) {
		Ext.Msg.alert("提示", "请勿多选！");
	} else {
		Ext.Msg.alert("提示", "请选择需要修改的网元，每次选择一条！");
	}
}

// 增加telnet网元
function addTelnetNe() {
	if (Ext.getCmp('emsConnect').getValue() == "") {
		Ext.Msg.alert('提示', '请选择一个TELNET接口的网管！');
	} else {
		var emsConnectionId = Ext.getCmp('emsConnect').getValue();

		// 以下两行获取所选网管的 连接类型是 Corba 还是 Telnet
		var num = emsStore.find("BASE_EMS_CONNECTION_ID", emsConnectionId);
	
		var connectionType = emsStore.getAt(num).get("CONNETION_TYPE");

		// 判断网管是Corba 还是 Telnet
		if (connectionType == 1) {
			Ext.Msg.alert('提示', '请选择一个TELNET接口的网管！');
		} else {
			var url = "addTelnetNe.jsp?emsConnectionId=" + emsConnectionId;
			var addTelnetNeWindow = new Ext.Window(
					{
						id : 'addTelnetNeWindow',
						title : '新增网元',
						width : 950,
						height : 400,
						isTopContainer : true,
						modal : true,
						autoScroll : true,
						html : '<iframe id="addNe_panel" name = "addNe_panel"  src = '
								+ url
								+ ' height="100%" width="100%" frameBorder=0 border=0/>'
					});
			addTelnetNeWindow.show();
		}
	}
}


function modifyNeSyncMode(syncMode) {
	var neIdList = new Array();
	// var emsConnectionId = Ext.getCmp('emsConnect').getValue();
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		for(var i=0;i<cell.length;i++){
			neIdList.push(cell[i].get('BASE_NE_ID'));
		}
	}else{
		Ext.Msg.alert('提示', '请选择至少一条记录！');
		return;
	}
	Ext.getBody().mask('正在执行，请稍候...');
	// 修改网元信息
	var jsonData = {
		"neModel.neIdList":neIdList,
		"neModel.syncMode" : syncMode
	};
	Ext.Ajax.request({
		url : 'connection!modifyCorbaNe.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {						
					// 刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息",obj.returnMessage);
				}
			
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
	
	
}

// 同步选中网元
function syncNe() {
	
	var processKey = "syncNe"+new Date().getTime();
	var jString = new Array();
	// var emsConnectionId = Ext.getCmp('emsConnect').getValue();
	var cell = connectListPanel.getSelectionModel().getSelections();
	var neNameStr = "";
	if (cell.length > 0) {
		for ( var i = 0; i < cell.length; i++) {
			var neModel = {
				"emsConnectionId" : cell[i].get('BASE_EMS_CONNECTION_ID'),
				"name" : cell[i].get('NAME'),
				"suportRates" : cell[i].get('SUPORT_RATES'),
				"type" : cell[i].get('TYPE'),
				"displayName" : cell[i].get('DISPLAY_NAME'),
				"neId" : cell[i].get('BASE_NE_ID'),
				"syncName": processKey
			};
			
			if (cell[i].get('TYPE') == 99 ) {
				neNameStr += cell[i].get('DISPLAY_NAME') + ', ';
			} 
			jString.push(neModel);
		}
		
		if (neNameStr != "") {
			Ext.Msg.alert("提示", neNameStr + " 是未知类型的网元，不可以同步!");
		} else {
			var jsonData = {
				"jString" : Ext.encode(jString)
			};

			Ext.Msg.confirm(
							'提示',
							'同步网元将从网管同步网元的基础数据，如果选择网元数量较多，时间可能较长！是否确认同步？',
							function(btn) {
								if (btn == 'yes') {
									// top.Msg.show(processMessageconfig);
//						Ext.getBody().mask('正在执行，请稍候...');
									Ext.Ajax.request({
												url : 'connection!syncSelectedNe.action',
												method : 'POST',
												params : jsonData,
												success : function(response) {
//													Ext.getBody().unmask();
													var obj = Ext
															.decode(response.responseText);
													if (obj.returnResult == 1) {
														Ext.Msg.alert(
																		"信息",
																		obj.returnMessage);
														// 刷新列表
														var pageTool = Ext
																.getCmp('pageTool');
														if (pageTool) {
															pageTool
																	.doLoad(pageTool.cursor);
														}
													}
													if (obj.returnResult == 0) {
														Ext.Msg.alert(
																		"信息",
																		obj.returnMessage);
														// 刷新列表
														var pageTool = Ext
																.getCmp('pageTool');
														if (pageTool) {
															pageTool
																	.doLoad(pageTool.cursor);
														}
													}
												},
												error : function(response) {
													// Ext.getBody().unmask();
												    clearTimer();
													Ext.Msg.hide();
													var obj = Ext
															.decode(response.responseText);
													Ext.Msg
															.alert(
																	"错误",
																	response.responseText == ""?"超时了！":response.responseText);
												},
												failure : function(response) {
													// Ext.getBody().unmask();
													clearTimer();
													Ext.Msg.hide();
													var obj = Ext
															.decode(response.responseText);
													Ext.Msg
															.alert(
																	"错误",
																	response.responseText == ""?"超时了！":response.responseText);
												}
											});
									//进度条显示
						showProcessBar(processKey);
								} else {

								}
							});
		}
	} else {
		Ext.Msg.alert("提示", "请先选取需要同步的网元！");
	}
}

//断开指定网元连接
function disConnect() {
	var jsonString = new Array();
	var emsConnectionId = Ext.getCmp('emsConnect').getValue();
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		for ( var i = 0; i < cell.length; i++) {
			var syncNeInfoModel = {
				"neId" : cell[i].get('BASE_NE_ID')
			};
			jsonString.push(syncNeInfoModel);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString),
			"syncNeInfoModel.emsConnectionId" : emsConnectionId
		// ,"pageSize":200
		};
		// Ext.getBody().mask('正在执行，请稍候...');
		Ext.Msg.show(processMessageconfig_disconnect);
		Ext.Ajax.request({
			url : 'disconnectTelnetNe.action',
			method : 'POST',
			params : jsonData,
			// timeout: 90000,
			success : function(response) {
				// clearInterval(timer);
				// Ext.getBody().unmask();
				// 刷新列表
				var pageTool = Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
			},
			error : function(response) {
//				clearInterval(timer);
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
//				clearInterval(timer);
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", response.responseText);
			}
		});
		timer = setInterval(getProcessPersent_disconnect, 1000);
	} else
		Ext.Msg.alert("提示", "请选择需要修改的网元！");
}

//连接指定网元
function startConnect() {
	var jsonString = new Array();
	var emsConnectionId = Ext.getCmp('emsConnect').getValue();
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		for ( var i = 0; i < cell.length; i++) {
			var syncNeInfoModel = {
				"neId" : cell[i].get('BASE_NE_ID')
			};
			jsonString.push(syncNeInfoModel);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString),
			"syncNeInfoModel.emsConnectionId" : emsConnectionId
		};
		Ext.getBody().mask('正在执行，请稍候...');
		Ext.Msg.show(processMessageconfig_connect);
		Ext.Ajax.request({
			url : 'connectTelnetNe.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					// 刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息", obj.returnMessage);
				}	
			},
			error : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {

				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", response.responseText);
			}
		});
		timer = setInterval(getProcessPersent_connect, 1000);
	} else {
		Ext.Msg.alert("提示", "请选择需要登录的网元！");
	}
}

// 取得网元变更列表
function getNeAlterList() {
//	alert(Ext.getCmp('emsConnect').getValue());
	if (Ext.getCmp('emsConnect').getValue() == "") {
		Ext.Msg.alert('提示', '请选择一个CORBA接口的网管！');
	} else {
		var emsConnectionId = Ext.getCmp('emsConnect').getValue();
		// 以下两行获取所选网管的 连接类型是 Corba 还是 Telnet
		var num = emsStore.find("BASE_EMS_CONNECTION_ID", emsConnectionId);
		// 判断网管是全部还是
		if (emsConnectionId == -99) {
			Ext.Msg.alert('提示', '请选择一个CORBA接口的网管！');
		} else {
			//网管权限 1.全部权限 0.部分权限
			var domainAuth = emsStore.getAt(num).get("domainAuth");
			//网管类型 1.corba连接 2.telnet类型
			var connectionType = emsStore.getAt(num).get("CONNETION_TYPE");
			if(domainAuth == 0){
				Ext.Msg.alert('提示', '不具备网管操作权限！');
			}else if(connectionType == 2) {
				Ext.Msg.alert('提示', '请选择一个CORBA接口的网管！');
			} else {
				var panelHeight = Ext.getCmp('connectListPanel').getSize().height;
				var windowHeight = 420;
				if (panelHeight < windowHeight) {
					windowHeight = panelHeight * 1;
				}
				var url = "neAlterList.jsp?emsConnectionId=" + emsConnectionId;
				var neSyncListWindow = new Ext.Window(
						{
							id : 'neSyncListWindow',
							title : '列表同步情况',
							width : 375,
							height : windowHeight,
							// layout : 'border',
							isTopContainer : true,
							modal : true,
							autoScroll : true,
							html : '<iframe  id="neSyncList_panel" name = "neSyncList_panel" src ='
									+ url
									+ ' height="100%" width="100%" frameBorder=0 border=0/>'
						});
				neSyncListWindow.show();
			}
		}
	}
}

//function modeColorGrid(v,m){
//	if(v=='自动'){
//		m.css='x-grid-font-blue';
//	}else if(v=='手动'){
//		m.css='x-grid-font-orange';
//	}else{
//		m.css='x-grid-font-red';
//	}
//	return v;
//}
function syncModeRenderer(v, m, r) {
	var result = v;
	if(typeof v == 'number'){
		switch(v){
		case 1:
			result = "手工同步";
			break;
		case 2:
			result = "自动同步";
			break;
		}
	}
	return result;
}

function factoryRenderer(v, m, r) {
	var result = v;
	if(typeof v == 'number'){
		switch(v){
		case 1:
			result = "华为";
			break;
		case 2:
			result = "中兴";
			break;
		case 3:
			result = "朗讯";
			break;
		case 4:
			result = "烽火";
			break;
		case 5:
			result = "贝尔";
			break;
		case 9:
			result = "富士通";
			break;
		}
	}
	return result;
}

function loginStatusRenderer(v, m, r) {
	var result = v;
	if(typeof v == 'number'){
		switch(v){
		case 0:
			result = "在线";
			break;
		case 1:
			result = "离线";
			break;
		case 2:
			result = "未知";
			break;
		}
	}
	return result;
}

function loginModeRenderer(v, m, r) {
	var result = v;
	if(typeof v == 'number'){
		switch(v){
		case 0:
			result = "自动";
			break;
		case 1:
			result = "手工";
			break;
		}
	}
	return result;
}

function colorGrid(v,m,r) {
	var display="";
	if(v==1){
//		m.css='x-grid-font-blue';
		display = "已同步";
	}else if(v == 2){
		m.css='x-grid-font-red';
		display = "未同步";
	}else if(v == 3){
		m.css='x-grid-font-red';
		display = "同步失败("+r.get("BASIC_SYNC_RESULT")+")";
	}else if(v == 4){
		m.css='x-grid-font-orange';
		display = "需要同步";
	}else if(v == 5){
		m.css='x-grid-font-blue';
		display = "正在同步";
	}
	return display;
}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout=3600000; 
	//collapse menu
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
//	Ext.Msg = top.Ext.Msg;
	
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [connectListPanel]
	});
	
	//放最后才能显示遮罩效果
	store.load({
		callback: function(r, options, success){   
			if(success){  
	
			}else{
				Ext.Msg.alert('错误','加载失败！');    
			}
		}
	}); 
 });