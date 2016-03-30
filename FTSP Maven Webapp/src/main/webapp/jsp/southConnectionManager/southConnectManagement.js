/*!
 * Ext JS Library 3.4.0

 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
//var emsGroupId = -1; 

var myPageSize = 200;
var emsGroupId;
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
//			Ext.Msg.alert('错误', '查询网管分组失败！请重新查询！');
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
	baseParams : {"displayAll" : true,"displayNone" : true,"authDomain":false},
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
		Ext.getCmp('emsGroupCombo').setValue(firstValue);
	}
});
// 创建网管分组下拉框
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : emsGroupStore,// 数据源
	valueField : 'BASE_EMS_GROUP_ID',// 下拉框实际值
	displayField : 'GROUP_NAME',// 下拉框显示值
	editable : false,
	triggerAction : 'all',// 每次加载所有值，否则下拉框选择一个值后，再点击就只有一个值
	width :150,
	resizable: true,
	listeners : {// 监听事件
		select : function(combo, record, index) {
			var emsGroupId = combo.getValue();
			// 还原网管下拉框
			Ext.getCmp('emsCombo').reset();
			// 动态改变网管数据源的参数
			emsStore.baseParams = {'emsGroupId':emsGroupId,"displayAll" : false};
			// 加载网管数据源
			emsStore.load({
				callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}else{
//						// 获取下拉框的第一条记录
//						var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//						// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
//						Ext.getCmp('emsCombo').setValue(firstValue);
					}
				}
			});
		}
	}
});

var store = new Ext.data.Store({
	//1代表查询corba连接
	url : 'connection!getConnectionListByGroupId.action',
	baseParams : {
		"emsGroupId" : "-99",
		"limit" : myPageSize
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "BASE_EMS_CONNECTION_ID", "DISPLAY_NAME", "BASE_EMS_GROUP_ID",
			"GROUP_NAME", "TYPE",

			"SERVICE_NAME", "CONNETION_TYPE", "CONNECTION_MODE",
			"CONNECT_STATUS", "USER_NAME","STATUS",

			"EMS_VERSION", "IP", "PORT", "EMS_NAME", "INTERNAL_EMS_NAME",
			"IDL_VERSION", "ENCODE","EXCEPTION_REASON",

			"GATEWAY_NE_ID", "NeDisplayName", "COLLECT_STATUS","time",
			"INTERVAL_TIME", "TIME_OUT","THREAD_NUM","ITERATOR_NUM" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,//每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : true,
	header : ""
});
checkboxSelectionModel.sortLock();
var columnModel = new Ext.ux.grid.LockingColumnModel({


	// specify any defaults for each column
	defaults : {
		sortable : true
//		,forceFit : false
	},
	stateId:"southConnectManagementId",
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
		}), checkboxSelectionModel, {
		id : 'BASE_EMS_CONNECTION_ID',
		header : 'id',
		dataIndex : 'BASE_EMS_CONNECTION_ID',
		hidden : true
	}, {
		id : 'groupName',
		header : '网管分组',
		width : 80,
		dataIndex : 'GROUP_NAME',
		locked : true
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'displayName',
		header : '网管名称',
		width : 120,
		dataIndex : 'DISPLAY_NAME',
		locked : true
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'type',
		header : '网管类型',
		width : 80,
		dataIndex : 'TYPE',
		locked : true
	}, {
		id : 'serviceName',
		header : '接入服务器',
		width : 80,
		dataIndex : 'SERVICE_NAME',
		renderer : function(v, m, t) {
			//连接异常
			if(t.get("STATUS") == 2){
				m.css='x-grid-font-orange';
				return v+"（异常）";
			}
			//连接中断
			else if(t.get("STATUS") == 3){
				m.css='x-grid-font-red';
				return v+"（中断）";
			}else
				return v;
		}
	}, {
		id : 'connectionType',
		header : '接口类型',
		width : 80,
		dataIndex : 'CONNETION_TYPE'
	}, {
		id : 'connectionMode',
		header : '连接模式',
		width : 80,
		dataIndex : 'CONNECTION_MODE'
	}, {
		id : 'connectStatus',
		header : '连接状态',
		width : 100,
		dataIndex : 'CONNECT_STATUS',
		renderer : function(v, m, t) {
			if (v == 1) {
				return "连接正常";
			} else if (v == 2) {
				m.css='x-grid-font-orange';
				if (t.get("EXCEPTION_REASON").replace(/(^s*)|(s*$)/g, "").length >0){
					var vt = t.get("EXCEPTION_REASON");
					return "连接异常("+ vt +")";
				}else{
					return "连接异常";
				}
			} else if (v == 3) {
				m.css='x-grid-font-red';
				return "网络中断";

			} else if (v == 4) {
				return "连接中断";

			} else {
				return "";
			}
		}
	}, {
		id : 'userName',
		header : '用户名',
		width : 80,
		dataIndex : 'USER_NAME'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'emsVersion',
		header : '网管版本',
		width : 120,
		dataIndex : 'EMS_VERSION'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'ip',
		header : '网管IP地址',
		dataIndex : 'IP',
		width : 120
//		,editor : new Ext.form.TextField({
//			regex : /\d+\.\d+\.\d+\.\d+/,
//			allowBlank : false
//		})
	}, {
		id : 'port',
		header : '端口号',
		dataIndex : 'PORT',
		width : 60
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'emsName',
		header : 'EMS Name',
		width : 100,
		dataIndex : 'EMS_NAME'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
//	}, {
//		id : 'internalEmsName',
//		header : '内部EMS Name',
//		width : 100,
//		dataIndex : 'INTERNAL_EMS_NAME',
//		editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'idlVersion',
		header : 'IDL版本号',
		width : 80,
		dataIndex : 'IDL_VERSION'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'encode',
		header : '编码格式',
		width : 100,
		dataIndex : 'ENCODE'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'THREAD_NUM',
		header : '线程数',
		width : 100,
		dataIndex : 'THREAD_NUM'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'ITERATOR_NUM',
		header : '迭代数',
		width : 100,
		dataIndex : 'ITERATOR_NUM'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'gatewayNeName',
		header : '网关网元',
		width : 100,
		dataIndex : 'NeDisplayName'
//		,editor : new Ext.form.TextField({
//			allowBlank : false
//		})
	}, {
		id : 'collectStatus',
		header : '采集任务控制',
		width : 100,
		dataIndex : 'COLLECT_STATUS',
//		renderer : function(v) {
//			if (v == 2) {
//				return "正常采集";
//			} else if (v == 3) {
//				return "暂停";
//			} else if (v == 4) {
//				return "禁止采集";
//			} else  {
//				return "";
//			}
//		}
		renderer : function(v, r, t) {
			var vt ;
			var ret;
			if (v == 2) { 
				ret =  "正常采集";
			} else	if (v == 3) {
//				alert(t.get("time"));
				if(null != t.get("time") && t.get("time") >0) {
					 vt = t.get("time");
						ret = "暂停( " + vt + "分钟 )";
				} else {
					ret = "暂停采集" ;
				}	   
			} else if (v == 4) {
				ret = "禁止采集";
			} else {
				ret = " ";
			}
			return  ret;
		}
	}, {
		id : 'intervalTime',
		header : '命令间隔(s)',
		width : 80,
		dataIndex : 'INTERVAL_TIME'
//		,editor : new Ext.form.NumberField({
//			allowDecimals : false,
//			allowNegative : false,
//			minValue : 1,
//			allowBlank : false
//		})
	}, {
		id : 'timeOut',
		header : '命令超时(s)',
		width : 80,
		dataIndex : 'TIME_OUT'
//		,editor : new Ext.form.NumberField({
//			allowDecimals : false,
//			allowNegative : false,
//			minValue : 600,
//			allowBlank : false
//		})
	} ]
});

Ext.state.Manager.setProvider(new Ext.state.SessionStorageStateProvider({
	expires : new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365))
}));

var connectListPanel = new Ext.grid.EditorGridPanel({
	id : "connectListPanel",
	name : "connectListPanel",
	region : "center",
	stripeRows : true,
	autoScroll : true,
	frame : false,
	cm : columnModel,
	store : store,
	loadMask : {
		msg : '正在执行，请稍后...'
	},
	clicksToEdit : 2,//设置点击几次才可编辑  
	selModel : checkboxSelectionModel, //必须加不然不能选checkbox 
	view : new Ext.ux.grid.LockingGridView(),
	viewConfig : {
		forceFit : false
	},
    stateId:'southConnectManagementId',  
    stateful:true,
	bbar : pageTool,
	tbar : ['-', '网管分组：', {
		xtype : 'combo',
		id : 'emsGroupCombo',
		fieldLabel : '网管分组',
		store : emsGroupStore,// 数据源
		valueField : 'BASE_EMS_GROUP_ID',// 下拉框实际值
		displayField : 'GROUP_NAME',// 下拉框显示值
		editable : false,
		triggerAction : 'all',// 每次加载所有值，否则下拉框选择一个值后，再点击就只有一个值
		width :150,
		listeners : {
			select : function(combo, record, index) {
				emsGroupId = Ext.getCmp('emsGroupCombo').getValue();
//				alert(emsGroupId);
				//加载网元同步列表
				var jsonData = {
					"emsGroupId" : emsGroupId,
					"limit" : myPageSize
				};
				exportData = jsonData;
				store.proxy = new Ext.data.HttpProxy({
					url : 'connection!getConnectionListByGroupId.action'
				});

				store.baseParams = jsonData;

				store.load({

					callback : function(r, options, success) {
						if (success) {

						} else {
							Ext.getCmp('connectListPanel').unmask();
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
			}
		}
	},{
		xtype: 'label',
		text: '',
		width: 20
	},'-', {
		text : '启动连接',
		privilege:actionAuth,
		icon : '../../resource/images/btnImages/connect.png',
		handler : function() {
			startConnect();
		}
	},{
		text : '断开连接',
		privilege:actionAuth,
		icon : '../../resource/images/btnImages/disconnect.png',
		handler : function() {
			disConnect();
		}
	},'-',{
		text : '任务控制',
		privilege:actionAuth,
		icon : '../../resource/images/btnImages/setTask.png',
		handler : function() {
			taskControl();
		}
	},'-',{
		text : '新增',
		privilege:addAuth,
		icon : '../../resource/images/btnImages/add.png',
		handler : function() {
			addConnect();
		}
	},{
		text : '删除',
		privilege:delAuth,
		icon : '../../resource/images/btnImages/delete.png',
		handler : function() {
			deleteConnect();
		}
	},{
		text : '修改',
		privilege:modAuth,
		icon : '../../resource/images/btnImages/modify.png',
		handler : function() {
			modifyConnect();
		}
	},'-',{
		text : '导出',
		privilege:actionAuth,
		icon : '../../resource/images/btnImages/export.png',
		handler : function() {
			exportExcel();
		}
	} ]

});

function exportExcel() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "导出信息为空！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				exportRequest();
			}
		});
	} else
		exportRequest();
}

var exportRequest = function() {
	Ext.getBody().mask('正在导出到Excel，请稍候...');
	emsGroupId = Ext.getCmp('emsGroupCombo').getValue();
	Ext.Ajax.request({
		url : 'connection!exportExcel.action',
		type : 'post',
		params : {
			"emsGroupId" : emsGroupId,
			"limit" : myPageSize
		},
		success : function(response) {
			Ext.getBody().unmask();
			var rs=Ext.decode(response.responseText);
			if(rs.returnResult==1 &&rs.returnMessage!=""){
				var destination={
						"filePath":rs.returnMessage
				};
				window.location.href="download!execute.action?"+Ext.urlEncode(destination);
			}
			else Ext.Msg.alert("提示","导出失败！");
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
};

//启动连接
function startConnect() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	var selectedTaskId = new Array();
	if (cell.length > 0) {
		
		var jsonData = {
			"emsConnectionModel.emsConnectionId" : cell[0]
					.get("BASE_EMS_CONNECTION_ID"),
			"emsConnectionModel.connectionType" : cell[0].get("CONNETION_TYPE")=="CORBA"?1:2,
			"emsConnectionModel.connectStatus" : cell[0].get("CONNECT_STATUS"),
			"emsConnectionModel.gateWayNeId" : cell[0].get("GATEWAY_NE_ID")
		};

		if (cell[0].get("CONNECT_STATUS") == 1) {
			Ext.Msg.alert("提示", "已在连接状态");
		} else {
			Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
				url : 'connection!startConnect.action',
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
					Ext.Msg.alert("错误",response.responseText);
				},
				failure : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", response.responseText);
				}
			});
		}

	} else {
		Ext.Msg.alert("提示", "请选择需要启动的连接！");
	}
}

//断开连接
function disConnect() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	var selectedTaskId = new Array();

	if (cell.length > 0) {
		var jsonData = {
			"emsConnectionModel.emsConnectionId" : cell[0]
					.get("BASE_EMS_CONNECTION_ID"),
			"emsConnectionModel.connectMode" : cell[0].get("CONNECTION_MODE")=="自动"?0:1,
			"emsConnectionModel.connectionType" : cell[0].get("CONNETION_TYPE")=="CORBA"?1:2
		};

		if (cell[0].get("CONNECT_STATUS") == 1) {
			Ext.Msg.confirm('提示', '断开连接，将无法和网管通信，是否确认？', function(btn) {
				if (btn == 'yes') {
					
					Ext.getBody().mask('正在执行，请稍候...');
				
					Ext.Ajax.request({
						url : 'connection!disConnect.action',
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
//							Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("错误", response.responseText);
						},
						failure : function(response) {
//							Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("错误", response.responseText);
						}
					});
				} else {

				}
			});
		} else {
			Ext.Msg.alert("提示", "连接已经中断！");
		}

	} else
		Ext.Msg.alert("提示", "请选择需要断开的连接！");
}

// 任务控制
function taskControl() {
	var cell = connectListPanel.getSelectionModel().getSelections();

	if (cell.length > 0 && cell.length < 2) {

		var emsConnectionId = cell[0].get("BASE_EMS_CONNECTION_ID");
		var collectStatus = cell[0].get("COLLECT_STATUS");
		var collectStartTime = cell[0].get("COLLEC_START_TIME");
		var minutes = 0;
		var url = 'taskControl.jsp?emsConnectionId=' + emsConnectionId;

		// 选择采集任务状态
		var choseStatusFormPanel = new Ext.FormPanel(
				{
					region : "center",
					frame : false,
					border : false,
					height : 80,
					bodyStyle : 'padding:5px 5px 0 5px;',
					labelWidth : 40,
					labelAlign : 'right',
					items : [ {
						layout : "column",
						border : false,
						items : [ {
							layout : 'form',
							// labelSeparator:"：",
							border : false,
							items : [
									{
										xtype : 'radio',
										boxLabel : '<span title="允许各种任务通过接口自动采集网管或者网元数据">正常采集</span>',
										id : 'normalCollection',
										name : 'statusLevel',
										inputValue : 2
									},
									{
										xtype : 'radio',
										boxLabel : '<span title="禁止各种任务通过接口自动采集网管或者网元数据。但不影响手动采集">禁止采集</span>',
										id : 'prohibitCollection',
										name : 'statusLevel',
										inputValue : 4
									},
									{
										xtype : 'radio',
										boxLabel : '<span title="暂停各种任务通过接口自动采集网管或者网元数据。但不影响手动采集">暂停采集</span>',
										id : 'pauseCollection',
										name : 'statusLevel',
										inputValue : 3
									} ]
						} ]
					} ]
				});
		// 选择区域级别窗口
		var selectStatusLevelWindow = new Ext.Window(
				{
					id : 'selectStatusLevelWindow',
					title : '采集任务控制',
					width : 200,
					height : 150,
					// isTopContainer : true,
					modal : true,
					plain : true, // 是否为透明背景
					items : choseStatusFormPanel,
					buttons : [
							{
								text : '确定', 
								handler : function() {
									var level = choseStatusFormPanel.getForm()
											.findField('statusLevel')
											.getGroupValue();
									// 设值choseStatusFormPanel.getForm().findField('areaLevel').setValue(v);
									selectStatusLevelWindow.close();
									if (level == 3) {
										pauseCollectionSetting(emsConnectionId,level);
									} else {
										var jsonData = {
											"emsConnectionModel.emsConnectionId" : emsConnectionId,
											"emsConnectionModel.collectStatus" : level
										};
										Ext.getBody().mask('正在执行，请稍候...');
										Ext.Ajax
												.request({
													url : 'connection!updateCollectStatus.action',
													method : 'POST',
													params : jsonData,
													success : function(response) {
														Ext.getBody()
																.unmask();
														// 刷新列表
														var pageTool =Ext.getCmp('pageTool');
														if (pageTool) {
															pageTool.doLoad(pageTool.cursor);
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
									}
								}
							},
							{
								text : '取消', 
								handler : function() {
									// 关闭修改任务信息窗口
									var win = Ext
											.getCmp('selectStatusLevelWindow');
									if (win) {
										win.close();
									}
								}
							} ]
				});
		selectStatusLevelWindow.show();
		
		choseStatusFormPanel.getForm().findField('statusLevel').setValue(
				collectStatus);
	} else {
		Ext.Msg.alert("提示", "请选择需要修改的任务控制连接，每次选择一条！");
	}
}

function pauseCollectionSetting(emsConnectionId,level){
	var url = 'taskControl.jsp?emsConnectionId=' + emsConnectionId + '&level=' + level ;
	pauseMinutesWindow = new Ext.Window({ 
		id:'pauseMinutesWindow', 
		title:'暂停时间设置',
	    width : 300,  
	    height : 150, 
	    isTopContainer : true,
	    modal : true,
        plain:true,  //是否为透明背景   
		html : '<iframe  id="pauseWindow_panel" name = "pauseWindow_panel"  src='+url+' height="100%" width="100%" frameborder=0 border=0/>' 
	});  
	pauseMinutesWindow.show();
}

//增加连接
function addConnect() {
	var panelHeight = Ext.getCmp('connectListPanel').getSize().height; 	
	var windowHeight = 500;
	if(panelHeight < windowHeight) {
		windowHeight = panelHeight*1;
	}
	
	var addConnectWindow = new Ext.Window(
			{
				id : 'addConnectWindow',
				title : '新增接口连接',
				width : 400,
				height : windowHeight,
				isTopContainer : true,
				modal : true,
				autoScroll : true,
				html : '<iframe id="addConnect_panel" name = "addConnect_panel"  src = "addSouthConnection.jsp" height="100%" width="100%" frameBorder=0 border=0/>'
			});
	addConnectWindow.show();
}

//修改连接
function modifyConnect() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var emsConnectionId = cell[0].get("BASE_EMS_CONNECTION_ID");
		var url = 'modifySouthConnection.jsp?emsConnectionId=' + emsConnectionId;
		
		var panelHeight = Ext.getCmp('connectListPanel').getSize().height; 	
		var windowHeight = 500;
		if(panelHeight < windowHeight) {
			windowHeight = panelHeight*1;
		}
		var modifyConnectWindow = new Ext.Window({
			id : 'modifyConnectWindow',
			title : '修改接口连接',
			width : 400,
			height : windowHeight,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : '<iframe  id="modifyConnect_panel" name = "modifyConnect_panel"  src = ' + url
					+ ' height="100%" width="100%" frameBorder=0 border=0/>'
		});
		modifyConnectWindow.show();

	} else
		Ext.Msg.alert("提示", "请选择需要修改的连接，每次选择一条！");
}

//删除连接
function deleteConnect() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	var selectedTaskId = new Array();
	if (cell.length > 0) {

		var jsonData = {
			"emsConnectionModel.emsConnectionId" : cell[0]
					.get("BASE_EMS_CONNECTION_ID"),
			"emsConnectionModel.connectStatus" : cell[0].get("CONNECT_STATUS"),
			"emsConnectionModel.gateWayNeId" : cell[0].get("GATEWAY_NE_ID")
		};
		if (cell[0].get("CONNECT_STATUS") == 1) {
			Ext.Msg.alert("提示", "您选择的连接接口处于连接状态，请中断连接后再做删除操作！");
		} else {
		Ext.Msg.confirm('提示', '删除接口连接，将会删除该网管下所有数据是否确认删除？', function(btn) {
			if (btn == 'yes') {
				Ext.getBody().mask('正在执行，请稍候...');
				Ext.Ajax.request({
					url : 'connection!deleteConnection.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("信息", obj.returnMessage, function(r) {
							//刷新列表
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
			} else {

			}
		});
		}
	} else {
		Ext.Msg.alert("提示", "请选择需要删除的连接！");
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	//collapse menu
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ connectListPanel ]
	});

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