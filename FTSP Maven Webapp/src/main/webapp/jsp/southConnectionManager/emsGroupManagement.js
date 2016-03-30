/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var emsGroupStore = new Ext.data.Store({
//	url : 'connection!getEmsGroupListByGroupId.action',common!getAllEmsGroups.action
	//不需要权限管理--汤健确认
	url : 'connection!getEmsGroupListByGroupId.action',
//	url : 'common!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
//	baseParams : {"displayAll" : false,"displayNone" : false,"authDomain":false},
	baseParams : {
		"emsGroupModel.emsGroupId" : "-1",
		"limit" : 200
	},
//	baseParams : {"displayAll" : false,"displayNone" : false},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows", 
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME', 'NOTE']
	})
//	}, [ "BASE_EMS_GROUP_ID", "GROUP_NAME", "NOTE" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : emsGroupStore,
	displayInfo : true,
	autoScroll : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
	 


var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true,
	header : ""
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
		id : 'BASE_EMS_GROUP_ID',
		header : 'BASE_EMS_GROUP_ID',
		dataIndex : 'BASE_EMS_GROUP_ID',
		hidden : true
	}, {
		id : 'GROUP_NAME',
		header : '网管分组名称',
		width : 140,
		dataIndex : 'GROUP_NAME'
	}, {
		id : 'NOTE',
		header : '备注',
		width : 300,
		dataIndex : 'NOTE'
	} ]
});

var connectListPanel = new Ext.grid.EditorGridPanel({
	id : "connectListPanel",
	region : "center",
	stripeRows : true,
	autoScroll : false,
	frame : false,
	cm : columnModel,
	store : emsGroupStore,
	loadMask : true,
	clicksToEdit : 2,// 设置点击几次才可编辑
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : false
	},
	bbar : pageTool,
	tbar : [ '-',{
		text : '新增',
		privilege:addAuth,
		icon : '../../resource/images/btnImages/add.png',
		handler : function() {
			addEmsGroup();
		}
	}, {
		text : '删除',
		privilege:delAuth, 
		icon : '../../resource/images/btnImages/delete.png',
		handler : function() {
			deleteEmsGroup();
		}
	},{
		text : '修改',
		privilege:modAuth,
		icon : '../../resource/images/btnImages/modify.png',
		handler : function() {
			modifyEmsGroup();
		}
	} ]
});

// 修改网管分组
function modifyEmsGroup() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var emsGroupId = cell[0].get("BASE_EMS_GROUP_ID");
		var url = 'modifyEmsGroup.jsp?emsGroupId=' + emsGroupId;

		var modifyEmsGroupWindow = new Ext.Window(
				{
					id : 'modifyEmsGroupWindow',
					title : '修改网管分组',
					width : 400,
					height : 150,
					isTopContainer : true,
					modal : true,
					autoScroll : true,
					html : '<iframe  id="modifyEmsGroup_panel" name = "modifyEmsGroup_panel"  src ='
							+ url
							+ ' height="100%" width="100%" frameBorder=0 border=0/>'
				});
		modifyEmsGroupWindow.show();
	}
}

// 删除网管分组
function deleteEmsGroup() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	var selectedTaskId = new Array();
	if (cell.length > 0) {
		var jsonData = {
			"emsGroupModel.emsGroupId" : cell[0].get("BASE_EMS_GROUP_ID")
		};
		Ext.Msg
				.confirm(
						'提示',
						'删除EMS分组，不会删除下属网管信息。原来下属网管将无分组信息。是否确认删除？',
						function(btn) {
							if (btn == 'yes') {
								Ext.getBody().mask('正在执行，请稍候...');
								Ext.Ajax
										.request({
											url : 'connection!deleteEmsGroup.action',
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
												Ext.Msg.alert("错误",
														response.responseText);
											},
											failure : function(response) {
												Ext.getBody().unmask();
												Ext.Msg.alert("错误",
														response.responseText);
											}
										});
							} else {

							}
						});
	} else
		Ext.Msg.alert("提示", "请选择需要删除的网管分组！");
}

// 新增网管分组
function addEmsGroup() {
	var addEmsGroupWindow = new Ext.Window(
			{
				id : 'addEmsGroupWindow',
				title : '新增网管分组',
				width : 400,
				height : 150,
				isTopContainer : true,
				modal : true,
				autoScroll : true,
				html : '<iframe  id="addEmsGroup_panel" name = "addEmsGroup_panel"  src = "addEmsGroup.jsp?saveType=0" height="100%" width="100%" frameBorder=0 border=0/>'
			});
	addEmsGroupWindow.show();
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ connectListPanel ]
	});

	// 放最后才能显示遮罩效果
	emsGroupStore.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});