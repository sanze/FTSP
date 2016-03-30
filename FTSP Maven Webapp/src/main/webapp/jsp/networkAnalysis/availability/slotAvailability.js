/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

//槽道可用率相关
//页面类型
var type = 1;

var headDisplay = ["网管分组","所属网管", "网元名称", "区域", "厂家",
                       "网元型号","综合"];

var headIndex  = ["EMS_GROUP_DISPLAY_NAME","EMS_DISPLAY_NAME", "NE_DISPLAY_NAME", "AREA_DISPLAY_NAME","FACTORY_DISPLAY_NAME",
                "PRODUCT_DISPLAY_NAME","AVAILABILITY"];
// ==========================page=============================
var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	colModel : new Ext.grid.ColumnModel({}),
	store : new Ext.data.Store({ reader : new Ext.data.JsonReader({}, headIndex) }),
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
//	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
//	stateId : "attenuationSearchGridId",
	stateful : true,
	bbar : pageTool,
	tbar : oneTbar
});

/*function colorGrid(v, m, r) {
	var value = v.split('%')[0];

	if(20<parseInt(value)&&parseInt(value)<=30){
		m.css = 'x-grid-font-blue';
	}
	if(10<=parseInt(value)&&parseInt(value)<=20){
		m.css = 'x-grid-font-orange';
	}
	if(parseInt(value)<10){
		m.css = 'x-grid-font-red';
	}
	return v;
}*/

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'border',
	items : [searchPanel,gridPanel]
});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
//	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ centerPanel, westPanel ],
		renderTo : Ext.getBody()
	});
	win.show();
	init(type);
	generalDiagram(type);
	
});