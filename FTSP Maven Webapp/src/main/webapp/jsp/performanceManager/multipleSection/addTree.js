/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// 供网元选择
var treeParams = {
	rootId : rootId,
	rootType : rootType,
	//rootText : "FTSP",
	checkModel : checkModel,
	rootVisible : rootVisible,
	// 规定数显示到的层数。4表示树可以展开到网元
	leafType : level
};
var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
// 新增数panel
var treePanel = new Ext.Panel({
			id : "treePanel",
			region : "center",
			forceFit : true,
			collapsed : false, // initially collapse the group
			collapsible : false,
			collapseMode : 'mini',
			split : true,
			html : '<iframe id="tree_panel" name = "tree_panel" src ="'
					+ treeurl
					+ '" height="100%" width="100%" frameBorder=0 border=0/>',
			buttons : [{
						text : '确定',
						handler : save

					}, {
						text : '取消',
						handler : cancel

					}]
		});

// 确认所选的项
function save() {
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var result;
	if (iframe.getCheckedNodes) {

		result = iframe.getCheckedNodes(["nodeId", "nodeLevel", "text",
						"path:nodeId", "path:nodeLevel", "path:text"], "top");
	} else {
		result = iframe.contentWindow.getCheckedNodes(["nodeId", "nodeLevel",
						"text", "path:nodeId", "path:nodeLevel", "path:text"],
				"top");
	}

	parent.getNeFromTree(result,type,portType);
	//parent.Ext.getCmp('addNeForwardWindow').close();
}

// 取消所选项
function cancel() {
	var win = parent.Ext.getCmp('addNeForwardWindow');
	if (win) {
		win.close();
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
				items : [treePanel],
				renderTo : Ext.getBody()
			});
});