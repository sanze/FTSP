/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
/*
var treeParams={
	rootId:2010,
	rootType:2,
	//rootText:"二干_省网五期",
	rootVisible:true,
	leafType:4,
    checkModel:"single",
    onlyLeafCheckable:true
}
var treeurl="../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
*/

//==================For the Tree====================
/*var westPanel = {
	xtype:'equiptreepanel',
	rootVisible: false,
	title:"",
	region:"west",
	width: 250,
	//autoScroll:true,
    boxMinWidth: 250,
    boxMinHeight: 260,
    filterBy: CommonDefine.filterNE_WDM,
    //checkNodes: checkNodes,
    onGetChecked:onGetChecked,
    onCheckChange:onCheckChange,
    listeners:{
    	render : checkNode
    }
};*/
var westPanel = new Ext.ux.EquipTreePanel({
	xtype:'equiptree',
	rootVisible: false,
	title:"",
	region:"west",
	width: 250,
	split: true,
	//collapsible : true,
	collapseMode: 'mini',
	//autoScroll:true,
    boxMinWidth: 250,
    boxMinHeight: 260,
    forceSameLevel: true,
    checkableLevel: [2,4],
    filterBy: CommonDefine.filterNE_WDM,
    //checkNodes: checkNodes,
    onGetChecked:onGetChecked,
    //onCheckChange:onCheckChange,
    listeners:{
    	afterrender : checkNode
    }
});
function checkNode(){
	this.checkNodes("2-1");
}

//==========================center=============================
var centerPanel = new Ext.form.FormPanel({
	region:"center",
	items:[{
		xtype:'equiptreecombo',
		id: 'treecombo',
		treeId: 'treepanel',
		rootVisible: false
	}]
});
//==========================center=============================

function onGetChecked(getFunc){
    var result=getFunc(["nodeId","nodeLevel","text","path:text","emsId"]);
    Ext.Msg.alert("GetChecked",Ext.util.JSON.encode(result));
	//var result=getFunc();
    //Ext.Msg.alert("节点数据",result);
}

function onCheckChange(node,checked){
    Ext.Msg.alert("CheckChange",node.text+' '+checked);
}
function getEquipTreeNodes(){
	var result=westPanel.getCheckedNodes(["nodeId","nodeLevel","text","path:text","emsId"]);
    Ext.Msg.alert("GetChecked",Ext.util.JSON.encode(result));
}

Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	//document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
    var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [centerPanel,westPanel],
        renderTo : Ext.getBody()
    });
	//初始化时使用需要延时等待treePanel初始化,其他地方使用无需延时.
    setTimeout("getEquipTreeNodes()",1000);
  });