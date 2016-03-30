/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// var jiheNeName = new Ext.form.TextField({
// id : 'jiheNeName',
// name : 'jiheNeName',
// fieldLabel : '资源系统网元名',
// sideText : '<font color=red>*</font>',
// allowBlank : false,
// anchor : '95%'
// });
//
// var ftspNeName = new Ext.form.TextField({
// id : 'ftspNeName',
// name : 'ftspNeName',
// fieldLabel : '网管网元名',
// sideText : '<font color=red>*</font>',
// allowBlank : false,
// anchor : '95%'
// });
//
// var connectInfo = {
// xtype : 'fieldset',
// title : '新增网元对应关系',
// autoHeight : true,
// items : [jiheNeName, ftspNeName, {
// xtype : '',
// border : false,
// labelSeparator : '',
// fieldLabel : '<font color=red>*为必填</font>'
// }]
// };
var formPanel = new Ext.FormPanel({
			id : 'formPanel',
			region : "center",
			border : false,
			frame : false,
			autoScroll : true,
			labelWidth : 120,
			width : 200,
			bodyStyle : 'padding:10px 12px 0;',
			items : [{
						xtype : 'textfield',
						id : 'jiheNeName',
						name : 'jiheNeName',
						fieldLabel : '资源系统网元标识',
						sideText : '<font color=red>*</font>',
						allowBlank : false,
						anchor : '95%'
					}, {
						xtype : 'textfield',
						id : 'ftspNeName',
						name : 'ftspNeName',
						fieldLabel : '网管网元名',
						sideText : '<font color=red>*</font>',
						allowBlank : false,
						anchor : '95%'
					}],
			buttons : [{
						text : '确定',
						handler : function() {
							saveConfig();
						}
					}, {
						text : '取消',
						handler : function() {
							var win = parent.Ext.getCmp('addJiheNeWindow');
							if (win) {
								win.close();
							}
						}
					}]
		});

// 保存配置
function saveConfig() {
	if (formPanel.getForm().isValid()) {
		var jiheNeName = Ext.getCmp("jiheNeName").getValue();
		var ftspNeName = Ext.getCmp("ftspNeName").getValue();

		//top.Ext.getBody().mask('正在执行，请稍候...');
		var jsonString = new Array();
		// 0 是新增，1 是修改
		if (saveType == 0) {
			var map = {
				"RESOURCE_NE_NAME" : jiheNeName,
				"FTSP_NE_NAME" : ftspNeName

			};
			jsonString.push(map);

			var jsonData = {
				"jsonString" : Ext.encode(jsonString)
			};
			Ext.Ajax.request({
						url : 'resource-circuit!addResourceNe.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {
							var obj = Ext.decode(response.responseText);
							if (obj.returnResult == 1) {
								Ext.Msg.alert("提示", "新增网元对应关系成功！", function(r) {
											// 刷新列表
											var pageTool = parent.Ext
													.getCmp('pageTool');
											if (pageTool) {
												pageTool
														.doLoad(pageTool.cursor);
											}

											// 关闭修改任务信息窗口
											var win = parent.Ext
													.getCmp('addJiheNeWindow');
											if (win) {
												win.close();
											}

										});
							}
							if (obj.returnResult == 0) {
								Ext.Msg.alert("提示", obj.returnMessage);
							}
						},
						error : function(response) {
							top.Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						},
						failure : function(response) {
							top.Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						}
					});
		} else {

			var map = {
				"RESOURCE_NE_ID" : ne_id,
				"RESOURCE_NE_NAME" : Ext.getCmp("jiheNeName").getValue(),
				"FTSP_NE_NAME" : Ext.getCmp("ftspNeName").getValue()

			};
			jsonString.push(map);

			var jsonData = {
				"jsonString" : Ext.encode(jsonString)
			};
			// 提交修改，不然store.getModifiedRecords();数据会累加
			top.Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
						url : 'resource-circuit!modifyResourceNe.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {// 回调函数
							top.Ext.getBody().unmask();

							var obj = Ext.decode(response.responseText);
							if (obj.returnResult == 1) {
								Ext.Msg.alert("提示", "修改网元对应关系成功！", function(r) {
											// 刷新列表
											var pageTool = parent.Ext
													.getCmp('pageTool');
											if (pageTool) {
												pageTool
														.doLoad(pageTool.cursor);
											}
											var win = parent.Ext
													.getCmp('addJiheNeWindow');
											if (win) {
												win.close();
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

		}

	}

}

function initData() {
	if (saveType == 1) {
		Ext.getCmp("jiheNeName").setValue(res_name);
		Ext.getCmp("ftspNeName").setValue(ftsp_name);
	}
}

Ext.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.Ext.menu.MenuMgr.hideAll();
			}
			Ext.Msg = top.Ext.Msg;

			var win = new Ext.Viewport({
						id : 'win',
						layout : 'border',
						items : [formPanel],
						renderTo : Ext.getBody()
					});
			initData();
		});