/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// 初始化查询条件
var arry = new Array();
var con = {
	"RESOURCE_NE_NAME" : "",
	"FTSP_NE_NAME" : ""
};

arry.push(con);

var store = new Ext.data.Store({
			url : 'resource-circuit!getNeRelation.action',

			baseParams : {
				"jsonString" : Ext.encode(arry),
				"limit" : 200
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["RESOURCE_NE_ID", "RESOURCE_NE_NAME", "FTSP_NE_NAME",
							"NE_NAME"])
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
						id : 'RESOURCE_NE_ID',
						header : '比对表Id',
						dataIndex : 'RESOURCE_NE_ID',
						hidden : true,// hidden colunm
						width : 100
					}, {
						id : 'RESOURCE_NE_NAME',
						header : '资源系统网元标识',
						dataIndex : 'RESOURCE_NE_NAME',
						hidden : false,// hidden colunm
						width : 350
					}, {
						id : 'FTSP_NE_NAME',
						header : '网管网元名',
						dataIndex : 'FTSP_NE_NAME',
						hidden : false,// hidden colunm
						width : 200
					}, {
						id : 'NE_NAME',
						header : '资源系统内部网元名',
						dataIndex : 'NE_NAME',
						hidden : true,// hidden colunm
						width : 200
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
				text : '新增',
				privilege : addAuth,
				icon : '../../../resource/images/btnImages/add.png',
				handler : addJiheNe
			}, {
				text : '删除',
				privilege : delAuth,
				icon : '../../../resource/images/btnImages/delete.png',
				handler : deleteJiheNe
			}, {
				text : '修改',
				privilege : modAuth,
				icon : '../../../resource/images/btnImages/modify.png',
				handler : modify
			},'-', {
				text : '导入',
				privilege : actionAuth,
				icon : '../../../resource/images/btnImages/import.png',
				handler : importJiheNe
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
					columnWidth : .3,
					layout : 'form',
					border : false,
					items : [{
								xtype : 'textfield',
								id : 'sourceName',
								name : 'sourceName',
								fieldLabel : '资源系统网元标识',
								maxLength : 256,
								allowBlank : true,
								width : 200
							}]
				}, {
					columnWidth : .3,
					layout : 'form',
					border : false,
					items : [{
								xtype : 'textfield',
								id : 'jiheName',
								name : 'jiheName',
								fieldLabel : '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;网管网元名',
								maxLength : 256,
								allowBlank : true,
								width : 200
							}]
				}, {
					columnWidth : .1,
					layout : 'form',
					border : false,
					items : [{
						xtype : 'button',
						privilege : viewAuth,
						width : 60,
						border : false,
						icon : '../../../resource/images/btnImages/search.png',
						handler : selectNeRelation,
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
function selectNeRelation() {
	var jsonString = new Array();
	var map = {
		"RESOURCE_NE_NAME" : Ext.getCmp('sourceName').getValue(),
		"FTSP_NE_NAME" : Ext.getCmp('jiheName').getValue()
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString),
		"limit" : 200
	};
	store.proxy = new Ext.data.HttpProxy({
				url : 'resource-circuit!getNeRelation.action'
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

function modify() {

	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length < 1) {
		Ext.Msg.alert('提示', '请选择需要修改的网元对应关系！');
	} else if (cell.length > 1) {
		Ext.Msg.alert('提示', '请不要多选！');
	} else {
		var addJiheNeWindow = new Ext.Window({
					id : 'addJiheNeWindow',
					title : '修改网元对应关系',
					width : 400,
					height : 170,
					isTopContainer : true,
					modal : true,
					autoScroll : true,
					html : '<iframe src = "addCheckNe.jsp?saveType=1&ne_id='
							+ cell[0].get('RESOURCE_NE_ID')
							+ '&res_name='
							+ cell[0].get('RESOURCE_NE_NAME')
							+ '&ftsp_name='
							+ cell[0].get('FTSP_NE_NAME')
							+ '" height="100%" width="100%" frameBorder=0 border=0/>'
				});
		addJiheNeWindow.show();
	}

	// if (cell.length > 0) {
	// for (var i = 0; i < cell.length; i++) {
	// var map = {
	// "RESOURCE_NE_ID" : cell[i].get('RESOURCE_NE_ID'),
	// "RESOURCE_NE_NAME" : cell[i].get('RESOURCE_NE_NAME'),
	// "FTSP_NE_NAME" : cell[i].get('FTSP_NE_NAME'),
	// "NE_NAME" : cell[i].get('NE_NAME')
	//
	// };
	// jsonString.push(map);
	// }
	// var jsonData = {
	// "jsonString" : Ext.encode(jsonString)
	// };
	// // 提交修改，不然store.getModifiedRecords();数据会累加
	// store.commitChanges();
	// top.Ext.getBody().mask('正在执行，请稍候...');
	// Ext.Ajax.request({
	// url : 'resource-circuit!modifyResourceNe.action',
	// method : 'POST',
	// params : jsonData,
	// success : function(response) {// 回调函数
	// top.Ext.getBody().unmask();
	//
	// var obj = Ext.decode(response.responseText);
	// if (obj.returnResult == 1) {
	// Ext.Msg.alert("提示", obj.returnMessage, function(r) {
	// // 刷新列表
	// var pageTool = Ext.getCmp('pageTool');
	// if (pageTool) {
	// pageTool.doLoad(pageTool.cursor);
	// }
	// });
	// }
	// if (obj.returnResult == 0) {
	// Ext.Msg.alert("提示", obj.returnMessage);
	// }
	//
	// },
	// error : function(response) {
	// top.Ext.getBody().unmask();
	// Ext.Msg.alert('错误', '保存失败！');
	// },
	// failure : function(response) {
	// top.Ext.getBody().unmask();
	// Ext.Msg.alert('错误', '保存失败！');
	// }
	//
	// });
	// }
}

function addJiheNe() {
	var addJiheNeWindow = new Ext.Window({
		id : 'addJiheNeWindow',
		title : '新增网元对应关系',
		width : 400,
		height : 170,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : '<iframe src = "addCheckNe.jsp?saveType=0" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	addJiheNeWindow.show();

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
							url : 'resource-circuit!UploadcheckNe.action',
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
								Ext.Msg.alert("提示", "文件导入成功！");

							},
							failure : function(form, action) {
								Ext.getCmp('uploadFile').reset();
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
					Ext.getCmp('uploadFile').reset();
					fileUploadWindow.hide();
				}
			}]
		});

/**
 * 导入link
 */
function importJiheNe() {
	Ext.getCmp('uploadFile').reset();
	fileUploadWindow.show();
}

function deleteJiheNe() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		Ext.Msg.confirm('信息', '确认删除？', function(btn) {
			if (btn == 'yes') {

				for (var i = 0; i < cell.length; i++) {
					var map = {
						"RESOURCE_NE_ID" : cell[i].get('RESOURCE_NE_ID')

					};
					jsonString.push(map);
				}
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};

				Ext.Ajax.request({
							url : 'resource-circuit!deleteResourceNe.action',
							method : 'POST',
							params : jsonData,
							success : function(response) {// 回调函数
								var obj = Ext.decode(response.responseText);
								if (obj.returnResult == 1) {
									Ext.Msg.alert("提示", obj.returnMessage,
											function(r) {
												// 刷新列表
												var pageTool = Ext
														.getCmp('pageTool');
												if (pageTool) {
													pageTool
															.doLoad(pageTool.cursor);
												}
											});
								}
								if (obj.returnResult == 0) {
									Ext.Msg.alert("提示", obj.returnMessage);
								}
							},
							error : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							},
							failure : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							}

						});
			}
		});

	} else {
		Ext.Msg.alert('提示', '请选择需要删除的网元对应关系！');
	}
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