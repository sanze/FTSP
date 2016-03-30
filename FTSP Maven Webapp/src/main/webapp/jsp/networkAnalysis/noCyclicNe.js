/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
/******************************树结构加载 开始**********************************/
var map={};  
var treeParams={
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		leafType:leafType,
	    checkModel:"multiple"
	    //onlyLeafCheckable:false
	};
var treeurl="../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
	
/**
 * 左侧的树
 */
var westPanel = new Ext.Panel(
{
	id : "westPanel",
	region : "west",
	width : 280,
	height : 800,
	minSize : 230,
	maxSize : 320,
	autoScroll : true,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="tree_panel" name ="tree_panel" src ="'+treeurl+'"  height="100%" width="100%" frameBorder=0 border=0/>'
});
/*****************************树结构加载 结束**********************************/

/******************************查询条件定义**********************************/
var limit = 200;

// ************************* 模板列表 ****************************
var store = new Ext.data.Store(
{
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows",
    	fields: [
    	  	   {name:"ID",mapping:"neId"},
    		   {name:"EMS",mapping:"emsDisplayName"},
    		   {name:"AREA",mapping:"areaName"},
    		   {name:"MULIT_CIRCLE_NE",mapping:"neName"},
    		   {name:"NE_TYPE",mapping:"productName"},
    		   {name:"LOCATION",mapping:"networkLocation"}
    	    ]
    })
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
	// columns are not sortable by default
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),
			{
				id : 'ID',
				header : 'ID',
				dataIndex : 'ID',
				width : 100,
				hidden : true,
				hideable : false
			},
			checkboxSelectionModel,
			{
				id : 'EMS',
				header : '所属网管',
				dataIndex : 'EMS',
				width : 150
			},
			{
				id : 'AREA',
				header : top.FieldNameDefine.AREA_NAME+'信息',
				dataIndex : 'AREA',
				width : 150
			},
			{
				id : 'MULIT_CIRCLE_NE',
				header : '未成环网元',
				dataIndex : 'MULIT_CIRCLE_NE',
				width : 150
			},
			{
				id : 'NE_TYPE',
				header : '网元型号',
				dataIndex : 'NE_TYPE',
				width : 150
			},
			{
				id : 'LOCATION',
				header : '网络位置',
				dataIndex : 'LOCATION',
				width : 100
			} ]
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	stateId : "noCyclicNeGridId",
	stateful : true,
	tbar : [ '-', {
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchCompareValue
	}, "-", {
		text : '导出',
		icon : '../../resource/images/btnImages/export.png',
		privilege : actionAuth,
		handler : exportData
	} ],
	bbar : pageTool
});

function searchCompareValue() {
	
	var iframe = window.frames["tree_panel"];
	// 兼容不同浏览器的取值方式
	if (iframe.getCheckedNodes) {
		result = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "text",
				"path:text" ]);
	} else {
		result = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel",
				"text", "path:text" ]);
	}
	
	if(result.length == 0){
		Ext.Msg.alert("提示","请选择网管或子网！");
		return;
	}
	
	var nodes = new Array();
	for ( var i = 0; i < result.length; i++) {
		nodes.push(Ext.encode(result[i]));
	}
	map={
			"treeNodes":nodes,
			"limit":limit
	};
	gridPanel.getEl().mask("正在查询...");
	store.proxy = new Ext.data.HttpProxy({url:"network!searchNoCyclicNodeList.action"});
	store.baseParams = map;

	store.load({
		callback: function(response, success){
			gridPanel.getEl().unmask();
			if(!success){
				var obj = Ext.decode(response.responseText);
	    		Ext.Msg.alert("提示",obj.returnMessage);
			}
		}
	});
}

function exportData() {
	map["flag"] = 8; 
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				{ 
					gridPanel.getEl().mask("正在导出...");
					exportExcelByParams(map);
				}
			}
		});
	} else{
		gridPanel.getEl().mask("正在导出...");
		exportExcelByParams(map); 
	} 
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = top.Ext.menu.MenuMgr.hideAll;
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;

	var centerPanel = new Ext.Panel({
		id : 'centerPanel',
		region : 'center',
		border : false,
		layout : 'border',
		autoScroll : true,
		items : [gridPanel]
	});

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ centerPanel, westPanel ]
	});
	win.show();
});
