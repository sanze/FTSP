/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
Ext.QuickTips.init(); 
var jsonString = new Array();
var map = {
	"userId" : userId,
	"BASE_EMS_GROUP_ID" : -99,
	"limit" : 200
};
jsonString.push(map);

var store = new Ext.data.Store({
			url : 'multiple-section!selectTrunkLine.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_TRUNK_LINE_ID", "BASE_EMS_CONNECTION_ID",
							"GROUP_NAME", "EMS_NAME", "TYPE", "DISPLAY_NAME"])
		});
store.load();
// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
			singleSelect : true,
			header : ""
		});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [new Ext.grid.RowNumberer({
						width : 26
					}), checkboxSelectionModel, {
				id : 'PM_TRUNK_LINE_ID',
				header : 'PM_TRUNK_LINE_ID',
				dataIndex : 'PM_TRUNK_LINE_ID',
				hidden : true
			}, {
				id : 'BASE_EMS_CONNECTION_ID',
				header : 'BASE_EMS_CONNECTION_ID',
				dataIndex : 'BASE_EMS_CONNECTION_ID',
				hidden : true
			}, {
				id : 'GROUP_NAME',
				header : '网管分组',
				width : 100,
				dataIndex : 'GROUP_NAME'
			}, {
				id : 'EMS_NAME',
				header : '网管',
				width : 100,
				dataIndex : 'EMS_NAME'
			}, {
				id : 'TYPE',
				header : '网管类型',
				width : 80,
				dataIndex : 'TYPE',
				renderer : function(v, r, t) {
					if (v == 12) {
						return "U2000 ";
					} else if (v == 21) {
						return "E300 ";
					} else if (v == 22) {
						return "U31 ";
					} else if (v == 31) {
						return "lucent ";
					} else if (v == 41) {
						return "烽火 otnm2000 ";
					} else if (v == 51) {
						return "富士通 ";
					} else {
						return " ";
					}
				}
			}, {
				id : 'DISPLAY_NAME',
				header : "<span style='font-weight:bold'>干线名称</span>",
				dataIndex : 'DISPLAY_NAME',
				tooltip:'可编辑列',
				width : 150,
				editor : new Ext.form.TextField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
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
	// title:'任务信息列表',
	cm : cm,
	store : store,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	// tbar: pageTool,
	forceFit : true, 
	tbar : ['-','网管分组：', emsGroupCombo,'-', '网管：', emsCombo,'-',

	{
				text : '查询',
				privilege : viewAuth,
				icon : '../../../resource/images/btnImages/search.png',
				handler : function() {
					select();
				}
			},'-', {
				text : '新增',
				privilege : addAuth,
				icon : '../../../resource/images/btnImages/add.png',
				handler : function() {
					add();
				}

			}, {
				text : '删除',
				privilege : delAuth,
				icon : '../../../resource/images/btnImages/delete.png',
				handler : function() {
					deleteSection();
				}

			}, '-',{
				text : '保存',
				privilege : modAuth,
				icon : '../../../resource/images/btnImages/disk.png',
				handler : function() {
					save();
				}

			},'-', {
				text : '导入',
				privilege : actionAuth,
				icon : '../../../resource/images/btnImages/import.png',
				handler : function() {
					importSection();
				}

			}]

});

// 查询干线信息

function select() {
	var groupId = Ext.getCmp('emsGroup').getValue();
	var emsId = Ext.getCmp('ems').getValue();
	var map;
	if (emsId != "") {
		map = {
			"userId" : userId,
			"BASE_EMS_CONNECTION_ID" : emsId,
			"limit" : 200
		}
	} else {
		map = {
			"userId" : userId,
			"BASE_EMS_GROUP_ID" : groupId,
			"limit" : 200

		};
	}

	var jsonString = new Array();
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};

	Ext.Ajax.request({
				url : 'multiple-section!selectTrunkLine.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {// 回调函数

					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 0) {
						Ext.Msg.alert("提示", obj.returnMessage);
						// store.rejectChanges() ;
					} else {

						store.proxy = new Ext.data.HttpProxy({
									url : 'multiple-section!selectTrunkLine.action'
								});
						store.baseParams = jsonData;
						store.load();

					}

				},
				error : function(response) {
					Ext.Msg.alert('错误', '保存失败！');
				},
				failure : function(response) {
					Ext.Msg.alert('错误', '保存失败！');
				}

			});
	// store.proxy = new Ext.data.HttpProxy({
	// url : 'multiple-section!selectTrunkLine.action'
	// });
	// store.baseParams = jsonData;
	// store.load({
	// callback : function(r, options, success) {
	// if (success) {
	//
	// } else {
	// Ext.Msg.alert('错误', '查询失败！请重新查询');
	// }
	// }
	// });
}

function add() {
	var url = "addTrunkLine.jsp";
	addTrunkLineWindow = new Ext.Window({
				id : 'addTrunkLineWindow',
				title : '新增干线信息',
				width : 400,
				height : 250,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
	addTrunkLineWindow.show();
	// 调节高度
	if (addTrunkLineWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addTrunkLineWindow.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		addTrunkLineWindow.setHeight(addTrunkLineWindow.getInnerHeight());
	}
	// 调节宽度
	if (addTrunkLineWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addTrunkLineWindow.setWidth(Ext.getCmp('win').getWidth() * 0.6);
	} else {
		addTrunkLineWindow.setWidth(addTrunkLineWindow.getInnerWidth());
	}
	addTrunkLineWindow.center();

}

function deleteSection() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {

		Ext.Msg.confirm('提示', '确认删除选择的干线？', function(button) {
			if (button == 'yes') {

				for (var i = 0; i < cell.length; i++) {
					var map = {
						"PM_TRUNK_LINE_ID" : cell[i].get('PM_TRUNK_LINE_ID')
					};
					jsonString.push(map);
				}
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				store.commitChanges();
				Ext.Ajax.request({
							url : 'multiple-section!deleteTrunkLine.action',
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
		Ext.Msg.alert('信息', '请选择需要删除的干线！');
	}
}

function save() {

	var jsonString = new Array();
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {

		for (var i = 0; i < cell.length; i++) {
			var map = {
				"PM_TRUNK_LINE_ID" : cell[i].get('PM_TRUNK_LINE_ID'),
				"DISPLAY_NAME" : cell[i].get('DISPLAY_NAME'),
				"BASE_EMS_CONNECTION_ID":cell[i].get('BASE_EMS_CONNECTION_ID')
			};
			jsonString.push(map);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		// store.commitChanges();
		Ext.Ajax.request({
					url : 'multiple-section!modifyTrunkLine.action',
					method : 'POST',
					params : jsonData,

					success : function(response) {// 回调函数

						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							store.commitChanges();
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
						Ext.Msg.alert('错误', '保存失败！');
					},
					failure : function(response) {
						Ext.Msg.alert('错误', '保存失败！');
					}

				});

	} else {
		Ext.Msg.alert('信息', '没有需要保存的干线！');
	}

}

var groupStore_import = new Ext.data.Store({
			url : 'multiple-section!selectAllGroup.action',

			reader : new Ext.data.JsonReader({

			}		, ["BASE_EMS_GROUP_ID", "GROUP_NAME"])
		});
groupStore_import.load({
			callback : function(r, options, success) {
				if (success) {

				} else {
					Ext.Msg.alert('错误', '加载失败！');
				}
			}
		});
// 网管分组combox
var groupCombo_import = {
	xtype : 'combo',
	id : 'emsGroup_import',
	name : 'emsGroup_import',
	fieldLabel : '网管分组',
	store : groupStore_import,
	displayField : "GROUP_NAME",
	valueField : 'BASE_EMS_GROUP_ID',
	sideText : '<font color=red>*</font>',
	triggerAction : 'all',
	editable : false,
	width : 200,
	listeners : {
		select : function(combo, record, index) {
			// var emsConnectionId =
			// Ext.getCmp('ems').getRawValue();
			var emsConnectionId = Ext.getCmp('emsGroup_import').getValue();

			var jsonData = {
				"emsGroupId" : emsConnectionId,
				"limit" : 200
			};
			emsStore_import.proxy = new Ext.data.HttpProxy({
						url : 'multiple-section!selectAllEMS.action'
					});
			emsStore_import.baseParams = jsonData;
			emsStore_import.load({
						callback : function(r, options, success) {
							if (success) {
								// 将网管选择的值设为空
								Ext.getCmp('ems_import').setValue("");
							} else {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});

		}
	}

}
var emsStore_import = new Ext.data.Store({
			url : 'multiple-section!selectAllEMS.action',
			baseParams : {
				"limit" : 200
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["DISPLAY_NAME", "BASE_EMS_CONNECTION_ID"])
		});

// 网管combox
var emsCombo_import = {
	xtype : 'combo',
	id : 'ems_import',
	name : 'ems_import',
	fieldLabel : '网&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;管',
	store : emsStore_import,
	displayField : "DISPLAY_NAME",
	valueField : 'BASE_EMS_CONNECTION_ID',
	sideText : '<font color=red>*</font>',
	triggerAction : 'all',
	editable : false,
	width : 200

}

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
			items : [groupCombo_import, emsCombo_import, {
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
			height : 225,
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
							url : 'multiple-section!UploadTrunkLine.action',
							waitTitle : "文件上传",
							waitMsg : '正在上传并导入,请稍候...',
							params : {
								"emsId" : Ext.getCmp('ems_import').getValue(),
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
								var obj = Ext
										.decode(action.response.responseText);
								Ext.MessageBox.hide();
								fileUploadWindow.hide();

								Ext.Msg.alert("错误", obj.result);
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

function importSection() {
	Ext.getCmp('uploadFile').reset();
	fileUploadWindow.show();
}
function init() {
	Ext.getCmp('emsGroup').setValue("-99");
	Ext.getCmp('emsGroup').setRawValue("全部");
}
Ext.onReady(function() {

	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	}
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;

	var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [gridPanel]
			});
	init();
		// win.show();
});
