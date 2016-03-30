/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

Ext.BLANK_IMAGE_URL = "../../ext/resources/images/default/s.gif";

var store = new Ext.data.Store({
			url : 'resource-circuit!selectResourceCircuit.action',
			baseParams : {
				"limit" : 200
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["RESOURCE_CIR_ID", "RESOURCE_CIR_NAME", "ROUTE_NUMBER",
							"COMPARE_RESULT", "COMPARE_REASON",
							"DIFF_ROUTE_ID", "ROUTE_NAME", "CLIENT_NAME",
							"USED_FOR", "CIR_TYPE", "CIR_MODEL", "A_PORT",
							"Z_PORT", "A_USER", "Z_USER"])
		});
store.load();
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
						id : 'RESOURCE_CIR_ID',
						header : '稽核电路Id',
						dataIndex : 'RESOURCE_CIR_ID',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'RESOURCE_CIR_NAME',
						header : '稽核电路名称',
						dataIndex : 'RESOURCE_CIR_NAME',
						hidden : false,// hidden colunm
						width : 350
					}, {
						id : 'ROUTE_NUMBER',
						header : '路径数',
						dataIndex : 'ROUTE_NUMBER',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'A_PORT',
						header : 'A端业务节点',
						dataIndex : 'A_PORT',
						width : 100
					}, {
						id : 'Z_PORT',
						header : 'Z端业务节点',
						dataIndex : 'Z_PORT',
						width : 100
					}, {
						id : 'COMPARE_RESULT',
						header : '比对结果',
						dataIndex : 'COMPARE_RESULT',
						// hidden colunm
						width : 80,
						renderer : function(v) {
							if (v == 0) {
								return "未比对";
							} else if (v == 1) {
								return "相同";
							} else if (v == 2) {
								return "不相同";
							}
						}
					}, {
						id : 'COMPARE_REASON',
						header : '结果解析',
						dataIndex : 'COMPARE_REASON',
						// hidden colunm
						width : 500
					}, {
						id : 'DIFF_ROUTE_ID',
						header : '差异电路',
						dataIndex : 'DIFF_ROUTE_ID',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'ROUTE_NAME',
						header : '路由名称',
						dataIndex : 'ROUTE_NAME',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'CLIENT_NAME',
						header : '客户名称',
						dataIndex : 'CLIENT_NAME',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'USED_FOR',
						header : '电路用途',
						dataIndex : 'USED_FOR',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'CIR_TYPE',
						header : '电路类别',
						dataIndex : 'CIR_TYPE',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'CIR_MODEL',
						header : '业务/电路类型',
						dataIndex : 'CIR_MODEL',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'A_USER',
						header : 'A端用户',
						dataIndex : 'A_USER',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'Z_USER',
						header : 'Z端用户',
						dataIndex : 'Z_USER',
						hidden : true,// hidden colunm
						width : 100
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

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	// title:'用户管理',
	cm : cm,
	store : store,
	// autoExpandColumn: 'roleName', // column with this id will be
	// expanded
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	// view: new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	bbar : pageTool,
	tbar : ['-',{
				text : '路由信息',
				privilege : viewAuth,
				icon : '../../../resource/images/btnImages/setTask.png',
				handler : selectCkeckRoute
			},'-', {
				text : '电路比对',
				privilege : actionAuth,
				handler : compareCircuit
			},'-', {
				text : '比对信息',
				privilege : viewAuth,
				handler : compareResult
			},'-', {
				text : '导入电路',
				privilege : actionAuth,
				icon : '../../../resource/images/btnImages/import.png',
				handler : importLink
			}, '-',{
				text : '结果统计',
				privilege : viewAuth,
				handler : resultCount
			},{
				text : '导入网管SDH',
				privilege : actionAuth,
				hidden:true,
				icon : '../../../resource/images/btnImages/import.png',
				handler : importNcLink
			},{
				text : '导入网管WDM',
				privilege : actionAuth,
				hidden:true,
				icon : '../../../resource/images/btnImages/import.png',
				handler : importNcWDM
			},{
				text : '导入资源',
				privilege : actionAuth,
				hidden:true,
				icon : '../../../resource/images/btnImages/import.png',
				handler : importNcResource
			}]
});

// =======================链路更新=========================
var formPanel = new Ext.FormPanel({
	region : "north",
	frame : false,
	border : false,
	bodyStyle : 'padding:10px 10px 0 10px',
	autoHeight : true,
	// labelWidth: 100,
	labelAlign : 'left',
	collapsed : true, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	items : [{
		layout : 'column',
		border : false,
		items : [{
					columnWidth : .4,
					layout : 'form',
					border : false,
					items : [{

								xtype : 'textfield',
								id : 'circuitName',
								name : 'circuitName',
								fieldLabel : '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电路名称',
								maxLength : 256,
								allowBlank : true,
								width : 300

							}]
				}, {
					columnWidth : .2,
					layout : 'form',
					border : false,
					items : [{
						xtype : 'button',
						privilege : viewAuth,
						width : 60,
						border : false,
						icon : '../../../resource/images/btnImages/search.png',
						handler : selectCircuit,
						text : '查询'
					}]
				}]
	}]

});

var leftPanel = new Ext.Panel({
			id : 'leftPanel',
			region : 'center',
			autoScroll : true,
			layout : 'border',
			items : [formPanel, gridPanel]
		});
// -------------------------------失败网管再次更新-----------------------------------
function selectCircuit() {
	var jsonData = {
		"resCirName" : Ext.getCmp('circuitName').getValue(),
		"limit" : 200
	};
	store.proxy = new Ext.data.HttpProxy({
				url : 'resource-circuit!selectResourceCircuit.action'
			});
	store.baseParams = jsonData;
	store.load({
				callback : function(r, options, success) {
					if (success) {

					} else {
						Ext.Msg.alert('错误', '更新失败！请重新更新');
					}
				}
			});
}

// --------------------------------------------------------------
function selectCkeckRoute() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {

		var VCircuit = cell[0].get('RESOURCE_CIR_ID');
		var routeNumber = cell[0].get('ROUTE_NUMBER');

		var url = "../resourceManager/circuit/checkRoute.jsp?VCircuit="
				+ VCircuit + "&routeNumber=" + routeNumber;
		parent.addTabPage(url, "路由信息：(" + VCircuit + ")");
	} else if (cell.length > 1) {
		Ext.Msg.alert('提醒', '请选择单条电路查询路由！');
	} else {
		Ext.Msg.alert('提醒', '请先选择要查询路由的电路！');
	}
}

function compareResult() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {

		var VCircuit = cell[0].get('RESOURCE_CIR_ID');
		var routeNumber = cell[0].get('ROUTE_NUMBER');
		var differencr_route = cell[0].get('DIFF_ROUTE_ID');
		var circuitJiheName = cell[0].get('RESOURCE_CIR_NAME');

		var url = "../resourceManager/circuit/compareRoute.jsp?VCircuit="
				+ VCircuit + "&routeNumber=" + routeNumber
				+ "&differencr_route=" + differencr_route + "&circuitJiheName="
				+ circuitJiheName;
		parent.addTabPage(url, "比对结果：(" + VCircuit + ")");
	} else if (cell.length > 1) {
		Ext.Msg.alert('提醒', '请选择单条电路查询比对信息！');
	} else {
		Ext.Msg.alert('提醒', '请选择需要查看比对信息的电路！');
	}
}

function compareCircuit() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		for (var i = 0; i < cell.length; i++) {
			var map = {
				"RESOURCE_CIR_ID" : cell[i].get('RESOURCE_CIR_ID'),
				"ROUTE_NUMBER" : cell[i].get('ROUTE_NUMBER'),
				"RESOURCE_CIR_NAME" : cell[i].get('RESOURCE_CIR_NAME')
			};
			jsonString.push(map);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};

		top.Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
					url : 'resource-circuit!compareCircuit.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {// 回调函数
						top.Ext.getBody().unmask();

						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							Ext.Msg.alert("提示", obj.returnMessage, function(r) {
										// 刷新列表
										var pageTool = Ext.getCmp('pageTool');
										if (pageTool) {
											pageTool.doLoad(pageTool.cursor);
										}
									});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
					},
					error : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert('错误', '保存失败！');
					},
					failure : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert('错误', '保存失败！');
					}

				});
	} else {
		Ext.Msg.alert('提醒', '请选择需要比对的电路！');
	}
}

// 上传文件用
var fileUploadForm = new Ext.FormPanel({
			fileUpload : true,
			frame : true,
			bodyStyle : 'padding: 10px 10px 0 10px;',
			labelWidth : 100,
			defaults : {
				anchor : '95%',
				allowBlank : false,
				msgTarget : 'side'
			},
			items : [{
						xtype : 'displayfield',
						value : "<font color=red>上传文件类型为xls或xlsx文件</font>"
					}, {
						xtype : 'fileuploadfield',
						id : 'uploadFile',
						// emptyText: '选择需要导入文件',
						fieldLabel : '导入数据',
						name : 'uploadFile',
						buttonText : '',
						regex : /^.*?\.(xls|xlsx)$/,
						buttonCfg : {
							iconCls : 'uploader'
						}
					}]
		});

// 上传文件用
var fileUploadWindow = new Ext.Window({
			id : 'fileUploadWindow',
			title : '文件导入',
			width : 500,
			height : 150,
			minWidth : 350,
			minHeight : 100,
			layout : 'fit',
			plain : false,
			resizable : false,
			closeAction : 'hide',// 关闭窗口
			bodyStyle : 'padding:1px;',
			buttonAlign : 'right',
			items : [fileUploadForm],
			buttons : [{
				text : '确定',
				handler : function() {
					if (fileUploadForm.getForm().isValid()) {
						fileUploadForm.getForm().submit({
							url : 'resource-circuit!UploadResCir.action',
							waitTitle : "文件上传",
							waitMsg : '正在上传并导入,请稍候...',
							params : {
								"jsonString" : Ext.getCmp('uploadFile')
										.getValue()
							},
							success : function(form, action) {
								// 刷新列表
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								Ext.MessageBox.hide();
								fileUploadWindow.hide();
								Ext.getCmp('uploadFile').reset();
								Ext.Msg.alert("提示", "导入成功");
							},
							failure : function(form, action) {
								var obj = Ext
										.decode(action.response.responseText);
								Ext.MessageBox.hide();
								fileUploadWindow.hide();

								Ext.Msg.alert("错误", obj.returnMessage);
							}
						})
					}
				}
			}, {
				text : '取消',
				handler : function() {
					fileUploadWindow.hide();
				}
			}]
		});

/**
 * 导入link
 */
function importLink() {
	Ext.getCmp('uploadFile').reset();
	fileUploadWindow.show();
}


// 上传文件用
var fileUploadFormNc = new Ext.FormPanel({
			fileUpload : true,
			frame : true,
			bodyStyle : 'padding: 10px 10px 0 10px;',
			labelWidth : 100,
			defaults : {
				anchor : '95%',
				allowBlank : false,
				msgTarget : 'side'
			},
			items : [{
						xtype : 'displayfield',
						value : "<font color=red>上传文件类型为xls或xlsx文件</font>"
					}, {
						xtype : 'fileuploadfield',
						id : 'uploadFileNc',
						// emptyText: '选择需要导入文件',
						fieldLabel : '导入数据',
						name : 'uploadFileNc',
						buttonText : '',
						regex : /^.*?\.(xls|xlsx)$/,
						buttonCfg : {
							iconCls : 'uploader'
						}
					}]
		});

// 上传文件用
var fileUploadWindowNc = new Ext.Window({
			id : 'fileUploadWindowNc',
			title : '文件导入',
			width : 500,
			height : 150,
			minWidth : 350,
			minHeight : 100,
			layout : 'fit',
			plain : false,
			resizable : false,
			closeAction : 'hide',// 关闭窗口
			bodyStyle : 'padding:1px;',
			buttonAlign : 'right',
			items : [fileUploadFormNc],
			buttons : [{
				text : '确定',
				handler : function() {
					if (fileUploadFormNc.getForm().isValid()) {
						fileUploadFormNc.getForm().submit({
							url : 'resource-circuit!UploadNc.action',
							waitTitle : "文件上传",
							waitMsg : '正在上传并导入,请稍候...',
							params : {
								"jsonString" : Ext.getCmp('uploadFileNc')
										.getValue()
							},
							success : function(form, action) {
								// 刷新列表
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								Ext.MessageBox.hide();
								fileUploadWindowNc.hide();
								Ext.getCmp('uploadFileNc').reset();
								Ext.Msg.alert("提示", "导入成功");
							},
							failure : function(form, action) {
								var obj = Ext
										.decode(action.response.responseText);
								Ext.MessageBox.hide();
								fileUploadWindowNc.hide();

								Ext.Msg.alert("错误", obj.returnMessage);
							}
						})
					}
				}
			}, {
				text : '取消',
				handler : function() {
					fileUploadWindowNc.hide();
				}
			}]
		});
function importNcLink(){
	Ext.getCmp('uploadFileNc').reset();
	fileUploadWindowNc.show();
}


// 上传文件用
var fileUploadFormNcWDM = new Ext.FormPanel({
			fileUpload : true,
			frame : true,
			bodyStyle : 'padding: 10px 10px 0 10px;',
			labelWidth : 100,
			defaults : {
				anchor : '95%',
				allowBlank : false,
				msgTarget : 'side'
			},
			items : [{
						xtype : 'displayfield',
						value : "<font color=red>上传文件类型为xls或xlsx文件</font>"
					}, {
						xtype : 'fileuploadfield',
						id : 'uploadFileNcWDM',
						// emptyText: '选择需要导入文件',
						fieldLabel : '导入数据',
						name : 'uploadFileNcWDM',
						buttonText : '',
						regex : /^.*?\.(xls|xlsx)$/,
						buttonCfg : {
							iconCls : 'uploader'
						}
					}]
		});

// 上传文件用
var fileUploadWindowNcWDM = new Ext.Window({
			id : 'fileUploadWindowNcWDM',
			title : '文件导入',
			width : 500,
			height : 150,
			minWidth : 350,
			minHeight : 100,
			layout : 'fit',
			plain : false,
			resizable : false,
			closeAction : 'hide',// 关闭窗口
			bodyStyle : 'padding:1px;',
			buttonAlign : 'right',
			items : [fileUploadFormNcWDM],
			buttons : [{
				text : '确定',
				handler : function() {
					if (fileUploadFormNcWDM.getForm().isValid()) {
						fileUploadFormNcWDM.getForm().submit({
							url : 'resource-circuit!UploadNcWDM.action',
							waitTitle : "文件上传",
							waitMsg : '正在上传并导入,请稍候...',
							params : {
								"jsonString" : Ext.getCmp('uploadFileNcWDM')
										.getValue()
							},
							success : function(form, action) {
								// 刷新列表
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								Ext.MessageBox.hide();
								fileUploadWindowNcWDM.hide();
								Ext.getCmp('uploadFileNcWDM').reset();
								Ext.Msg.alert("提示", "导入成功");
							},
							failure : function(form, action) {
								var obj = Ext
										.decode(action.response.responseText);
								Ext.MessageBox.hide();
								fileUploadWindowNcWDM.hide();

								Ext.Msg.alert("错误", obj.returnMessage);
							}
						})
					}
				}
			}, {
				text : '取消',
				handler : function() {
					fileUploadWindowNcWDM.hide();
				}
			}]
		});

function importNcWDM(){
		Ext.getCmp('uploadFileNcWDM').reset();
		fileUploadWindowNcWDM.show();
}

var fileUploadFormNcResource = new Ext.FormPanel({
	fileUpload : true,
	frame : true,
	bodyStyle : 'padding: 10px 10px 0 10px;',
	labelWidth : 100,
	defaults : {
		anchor : '95%',
		allowBlank : false,
		msgTarget : 'side'
	},
	items : [{
				xtype : 'displayfield',
				value : "<font color=red>上传文件类型为xls或xlsx文件</font>"
			}, {
				xtype : 'fileuploadfield',
				id : 'uploadFileNcResource',
				// emptyText: '选择需要导入文件',
				fieldLabel : '导入数据',
				name : 'uploadFileNcResource',
				buttonText : '',
				regex : /^.*?\.(xls|xlsx)$/,
				buttonCfg : {
					iconCls : 'uploader'
				}
			},{
				fieldLabel : '是否转换',
				id : 'ctpChangeType',
				xtype : 'radiogroup',
				columns : 2,
				items : [ {
					boxLabel : "否",
					name : 'new_direction',
					inputValue : 0,
					checked : true
				}, {
					boxLabel : "是",
					name : 'new_direction',
					inputValue : 1
				} ]
			}]
});

//上传文件用
var fileUploadWindowNcResource = new Ext.Window({
	id : 'fileUploadWindowNcResource',
	title : '文件导入',
	width : 500,
	height : 150,
	minWidth : 350,
	minHeight : 100,
	layout : 'fit',
	plain : false,
	resizable : false,
	closeAction : 'hide',// 关闭窗口
	bodyStyle : 'padding:1px;',
	buttonAlign : 'right',
	items : [fileUploadFormNcResource],
	buttons : [{
		text : '确定',
		handler : function() {
			if (fileUploadFormNcResource.getForm().isValid()) {
				fileUploadFormNcResource.getForm().submit({
					url : 'resource-circuit-special!importResourceFile.action',
					waitTitle : "文件上传",
					waitMsg : '正在上传并导入,请稍候...',
					params : {
						"jsonString" : Ext.getCmp('uploadFileNcResource').getValue(),
						"type" :Ext.getCmp('ctpChangeType').getValue().inputValue
					},
					success : function(form, action) {
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						Ext.MessageBox.hide();
						fileUploadWindowNcResource.hide();
						Ext.getCmp('uploadFileNcResource').reset();
						Ext.Msg.alert("提示", "导入成功");
					},
					failure : function(form, action) {
						var obj = Ext
								.decode(action.response.responseText);
						Ext.MessageBox.hide();
						fileUploadWindowNcResource.hide();

						Ext.Msg.alert("错误", obj.returnMessage);
					}
				})
			}
		}
	}, {
		text : '取消',
		handler : function() {
			fileUploadWindowNcResource.hide();
		}
	}]
});

function importNcResource(){
	Ext.getCmp('uploadFileNcResource').reset();
	fileUploadWindowNcResource.show();
}

function resultCount() {
	var resultCountWindow = new Ext.Window({
		id : 'resultCountWindow',
		title : '比对结果统计',
		width : 400,
		height : 180,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : '<iframe src = "resultCount.jsp?saveType=0" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	resultCountWindow.show();
}

// -----------------------------------------init the
// page--------------------------------------------

Ext.onReady(function() {
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			}
			Ext.Msg = top.Ext.Msg;
			Ext.Ajax.timeout = 9000000;
			var win = new Ext.Viewport({
						id : 'win',
						title : "B类比较值设定",
						layout : 'border',
						items : [leftPanel],
						renderTo : Ext.getBody()
					});
			win.show();
			formPanel.toggleCollapse(true);
		});