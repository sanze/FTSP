/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */


var jsonString = new Array();
var map = {
	"limit" : 200
};
jsonString.push(map);

var store = new Ext.data.Store({
			url : 'multiple-section!selectMultipleSection.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_MULTI_SEC_ID", "SEC_NAME", "STD_WAVE",
							"ACTULLY_WAVE", "DIRECTION", "PM_TRUNK_LINE_ID",
							"PM_UPDATE_TIME", "SEC_STATE", "ROUTE_UPDATE_TIME",
							"TRUNK_NAME", "TYPE", "EMS_NAME", "EMS_GROUP_NAME",
							"BASE_EMS_CONNECTION_ID"])
		});
// store.load();
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
						id : 'PM_MULTI_SEC_ID',
						header : 'PM_MULTI_SEC_ID',
						dataIndex : 'PM_MULTI_SEC_ID',
						hidden : true
					}, {
						id : 'BASE_EMS_CONNECTION_ID',
						header : 'BASE_EMS_CONNECTION_ID',
						dataIndex : 'BASE_EMS_CONNECTION_ID',
						hidden : true
					}, {
						id : 'EMS_GROUP_NAME',
						header : '网管分组',
						width : 100,
						dataIndex : 'EMS_GROUP_NAME'
					}, {
						id : 'EMS_NAME',
						header : '网管',
						width : 100,
						dataIndex : 'EMS_NAME'
					}, {
						id : 'TYPE',
						header : '网管类型',
						width : 80,
						dataIndex : 'TYPE'
					}, {
						id : 'TRUNK_NAME',
						header : '干线名称',
						dataIndex : 'TRUNK_NAME',
						width : 150
					}, {
						id : 'SEC_NAME',
						header : '光复用段名称',
						dataIndex : 'SEC_NAME'
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
	// title:'任务信息列表',
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	// tbar: pageTool,
	viewConfig : {
		forceFit : true
	},
	tbar : {
		xtype : "container",
		border : false,
		items : [{
			// tbar第一行工具栏
			xtype : "toolbar",
			items : ['-','网管分组：', emsGroupCombo, '-','网管：', emsCombo, '-','干线：', trunkCombo,'-', {
						text : '查询',
						icon : '../../../resource/images/btnImages/search.png',
						handler : function() {
							search();
						}

					}]
		}, {
			// tbar第二行工具栏
			xtype : "toolbar",
			items : ['-',{
						text : '上移',
						icon : '../../../resource/images/btnImages/up.png',
						handler : function() {
							upForward(gridPanel, store);
						}

					}, '-',{
						text : '下移',
						icon : '../../../resource/images/btnImages/down.png',
						handler : function() {
							downForward(gridPanel, store);
						}

					}]
		}]

	},
	buttons : [{
				text : '确定',
				handler : function() {
					save();
				}
			}, {
				text : '取消',
				handler : function() {
					close();
				}
			}]
});

/**
 * 上移
 * 
 * @param {}
 *            forwardUpPanel panel
 * @param {}
 *            forwardUpStore store
 */
function upForward(gridPanel, store) {

	var record = gridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert("提示", "请选择要移动的光复用段！");
		return;
	}
	var index = store.indexOf(record);
	if (index == 0) {
		return;
	}
	store.remove(record);
	store.insert(index - 1, record);
	gridPanel.getView().refresh();
	gridPanel.getSelectionModel().selectRow(index - 1);
}

/**
 * 下移
 * 
 * @param {}
 *            forwardUpPanel
 * @param {}
 *            forwardUpStore
 */
function downForward(gridPanel, store) {
	var record = gridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert("提示", "请选择要移动的光复用段！");
		return;
	}
	var index = store.indexOf(record);
	if (index == store.getCount() - 1) {
		return;
	}
	store.remove(record);
	store.insert(index + 1, record);
	gridPanel.getView().refresh();
	gridPanel.getSelectionModel().selectRow(index + 1);
}

// 查询光复用端信息
function search() {
	var jsonString = new Array();
	var trunkLine = Ext.getCmp('trunkLine').getValue();
	if (trunkLine.length<1) {
		Ext.Msg.alert("提示", "请选择具体的干线！");
		return;
	}
	var map = {
		"userId":userId,
		"BASE_EMS_GROUP_ID" : Ext.getCmp('emsGroup').getValue(),
		"BASE_EMS_CONNECTION_ID" : Ext.getCmp('ems').getValue(),
		"PM_TRUNK_LINE_ID" : trunkLine,
		"limit" : 200
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)

	};
		Ext.Ajax.request({
				url : 'multiple-section!selectMultipleSection.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {// 回调函数

					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 0) {
						Ext.Msg.alert("提示", obj.returnMessage);
						// store.rejectChanges() ;
					} else {

						store.proxy = new Ext.data.HttpProxy({
									url : 'multiple-section!selectMultipleSection.action'
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
//	store.baseParams = jsonData;
//	store.proxy = new Ext.data.HttpProxy({
//				url : 'multiple-section!selectMultipleSection.action'
//			});
//	store.load({
//				callback : function(r, options, success) {
//					if (success) {
//						
//					} else {
//						Ext.Msg.alert('错误', '更新失败！请重新更新');
//					}
//				}
//			});
}
/**
 * 重置
 */
function reset() {
	Ext.getCmp('emsGroup').setValue(0);
	Ext.getCmp('ems').setValue(0);
	Ext.getCmp('trunkLine').setValue(0);
	Ext.getCmp('emsGroup').setRawValue("全部");
	Ext.getCmp('ems').setRawValue("全部");
	Ext.getCmp('trunkLine').setRawValue("全部");
}

function save() {

	var jsonString = new Array();
	for (var i = 0; i < store.getCount(); i++) {
		var map = {
			"PM_MULTI_SEC_ID" : store.getAt(i).get('PM_MULTI_SEC_ID')
		};
		jsonString.push(map);
	}

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	// store.commitChanges();
	Ext.Ajax.request({
				url : 'multiple-section!sortMultipleSection.action',
				method : 'POST',
				params : jsonData,

				success : function(response) {// 回调函数

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
						// store.rejectChanges() ;
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

function close() {
	var win = parent.Ext.getCmp('sortMultipleSectionWindow');
	if (win) {
		win.close();
	}
}

function init() {

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
	// win.show();
	init();
});
