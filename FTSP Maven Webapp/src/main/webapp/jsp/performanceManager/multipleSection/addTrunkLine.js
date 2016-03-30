/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// ============create the Data Store==========
// --------------已分配用户权限列表----------------
// ==================页面====================
var formPanel = new Ext.FormPanel({
			region : "center",
			// labelAlign: 'top',
			frame : false,
			// title: '新增用户',
			bodyStyle : 'padding:20px 10px 0',
			// labelWidth: 100,
			labelAlign : 'left',
			autoScroll : true,
			items : [{
						layout : 'column',
						border : false,
						items : [{
									layout : 'form',
									border : false,
									labelSeparator : "：",
									items : [emsGroupCombo, emsCombo, {
												xtype : 'textfield',
												id : 'trunkLine',
												name : 'trunkLine',
												fieldLabel : '干线名称',
												sideText : '<font color=red>*</font>',
												width : 150
											}]
								}]
					}],
			buttons : [{
						text : '确定',
						handler : save
					}, {
						text : '取消',
						handler : close
					}]
		});

// =================函数===================
function close() {
	var win = parent.Ext.getCmp('addTrunkLineWindow');
	if (win) {
		win.close();
	}
}

function save() {

	// 判断参数是否为空
	var groupId = Ext.getCmp("emsGroup").getValue();
	if (groupId.length < 1) {
		Ext.Msg.alert('提示', '网管分组不能为空！');
		return;
	}
	var emsId = Ext.getCmp("ems").getValue();
	if (emsId.length < 1) {
		Ext.Msg.alert("提示", "网管不能为空！");
		return;
	}
	var trunkLine = Ext.getCmp("trunkLine").getValue();
	if (trunkLine.length < 1) {
		Ext.Msg.alert("提示", "干线名称不能为空！");
		return;
	}
	var jsonString = new Array();
	var map = {
		"BASE_EMS_CONNECTION_ID" : emsId,
		"DISPLAY_NAME" : trunkLine
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
		url : 'multiple-section!addTrunkLine.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {// 回调函数
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert("提示", obj.returnMessage, function(r) {

							// 刷新列表
							var pageTool = parent.Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
							Ext.Msg.confirm('信息', '继续添加？', function(btn) {
										if (btn == 'yes') {

										} else {
											// 关闭修改任务信息窗口
											var win = parent.Ext
													.getCmp('addTrunkLineWindow');
											if (win) {
												win.close();
											}
										}
									});

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

Ext.onReady(function() {
	Ext.Msg = parent.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}
	Ext.QuickTips.init(); // 开启悬停提示
	Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
	var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
});