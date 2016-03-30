/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// ============create the Data Store==========
// --------------已分配用户权限列表----------------
var emsStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'value'
					}, {
						name : 'displayName'
					}]
		});
emsStore.loadData([['1', '历史值'], ['2', '当前值'], ['3', '理论值']]);
//

// 网管分组combox
var tt = {
	xtype : 'combo',
	id : 'tt1',
	name : 'tt1',
	fieldLabel : '基准值数据源',
	store : emsStore,
	displayField : 'displayName',
	valueField : 'value',
	triggerAction : 'all',
	mode : "local",
	editable : false
}

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
									items : [tt]
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
	var win = parent.Ext.getCmp('addForwardWindow');
	if (win) {
		win.close();
	}
}

function save() {
	var type = Ext.getCmp('tt1').getValue();
	parent.changeValue(type,value);
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