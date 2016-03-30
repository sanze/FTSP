/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var emsStore = new Ext.data.Store({
	url : 'getConnectionList.action',
	baseParams : {
		"connectInfoModel.connectionType" : "0"
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsConnectionId", "netWorkName", "displayName" ])
});
emsStore.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '加载失败！');
		}
	}
});

var netWorkName = new Ext.form.ComboBox({
	id : 'netWorkName',
	name : 'netWorkName',
	fieldLabel : '网络',
	store : emsStore,
	displayField : "displayName",
	valueField : 'emsConnectionId',
	triggerAction : 'all',
	editable : false,
	allowBlank : false,
	resizable: true,
	anchor : '95%'
});

var trunkLineName = new Ext.form.TextField({
	id : 'trunkLineName',
	name : 'trunkLineName',
	fieldLabel : '干线名称',
	allowBlank : false,
	anchor : '95%'
});

var trunkLineInfo = {
	xtype : 'fieldset',
	title : '干线信息',
	autoHeight : true,
	items : [ netWorkName, trunkLineName ]
};

var formPanel = new Ext.FormPanel(
		{
			id : 'formPanel',
			region : "center",
			border : false,
			frame : false,
			autoScroll : true,
			labelWidth : 120,
			width : 200,
			bodyStyle : 'padding:10px 12px 0;',
			items : [ trunkLineInfo ],
			buttons : [
					{
						text : '保存',
						handler : save
					}, {
						text : '重置',
						handler : function() {
							formPanel.getForm().reset();
						}
					} ]
		});

function save(){
	if (formPanel.getForm().isValid()) {
		var emsConnectionId = Ext.getCmp("netWorkName")
				.getValue();
		var netWorkName = Ext.getCmp("netWorkName")
				.getRawValue();
		var trunkLineName = Ext.getCmp("trunkLineName")
				.getRawValue();

		var jsonData = {
			"trunkLineModel.emsConnectionId" : emsConnectionId,
			"trunkLineModel.netWorkName" : netWorkName,
			"trunkLineModel.trunkLineName" : trunkLineName
		};

		top.Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax
				.request({
					url : 'addTrunkLine.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						var obj = Ext
								.decode(response.responseText);
						top.Ext.getBody().unmask();
						Ext.Msg.alert(
										"信息",
										obj.returnMessage,
										function(r) {
											// 刷新列表
											var pageTool = parent.Ext.getCmp('pageTool');
											if (pageTool) {
												pageTool.doLoad(pageTool.cursor);
											}
											Ext.Msg.confirm(
															'信息',
															'继续添加？',
															function(btn) {
																if (btn == 'yes') {

																} else {
																	// 关闭修改任务信息窗口
																	var win = parent.Ext.getCmp('addTrunkLineWindow');
																	if (win) {
																		win.close();
																	}
																}
															});
										});
					},
					error : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误",
								response.responseText);
					},
					failure : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误",
								response.responseText);
					}
				});
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	}
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ formPanel ],
		renderTo : Ext.getBody()
	});
});