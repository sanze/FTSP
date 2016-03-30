/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
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
									items : [{
												xtype : 'textfield',
												id : 'fiberName',
												name : 'fiberName',
												fieldLabel : '光缆名称',
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'caculateValue',
												name : 'caculateValue',
												fieldLabel : '理论段衰耗',
												emptyText : "浮点数，精度到小数点后2位",
												sideText : 'dB',
												width : 200
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
	var win = parent.Ext.getCmp('addLeftForwardFiberWindow');
	if (win) {
		win.close();
	}
}

function save() {
	var fiberName = Ext.getCmp("fiberName").getValue();
	var caculateValue = Ext.getCmp("caculateValue").getValue();
	if (formPanel.getForm().isValid()) {
		if (fiberName.length < 1) {
			Ext.Msg.alert("提示", "光缆名称不能为空！");
			return;
		}
		parent.saveLeftForwardFiber(fiberName,caculateValue,portType,type);
		parent.Ext.getCmp('addLeftForwardFiberWindow').close();
	}
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