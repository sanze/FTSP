/**
 * 使用该页面前必须为gridPanel赋值
 */
//显示页面,包括页面初始化
Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
//必须为gridPanel变量赋值,该文件依赖于main.js文件
Ext.onReady(function(){
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Ajax.timeout = 900000;
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel ],
		renderTo : Ext.getBody()
	});
	win.show();
	setDefaultStartTime();
	queryFaultInfo();
});