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
var MJcount;
var MNcount;
var WRcount;
var searchPanel = new Ext.FormPanel({
	id : 'searchPanel',
	region : 'north',
	height : 70,
	bodyStyle : 'padding:20px 10px 0',
	// collapsible : true,
	autoScroll : true,
	items : [ {
		border : false,
		//layout : 'hbox',
		items : [ {
			border : false,
			layout : 'column',
			items : [ {
				// columnWidth : 0.13,
				width : 100,
				layout : 'form',
				labelWidth : 60,
				border : false,
				labelWidth : 5,
				items : [ {
					xtype : 'checkbox',
					id : 'MJ',
					checked : true,
					boxLabel : '重要预警',
					inputValue : '3'
				}]
			}, {
				// columnWidth : 0.13,
				width : 100,
				layout : 'form',
				labelWidth : 60,
				border : false,
				labelWidth : 5,
				items : [ {
					xtype : 'checkbox',
					id : 'MN',
					checked : true,
					boxLabel : '次要预警',
					inputValue : '2'
				} ]
			}, {
				// columnWidth : 0.13,
				width : 100,
				layout : 'form',
				id : 'thisCol',
				labelWidth : 70,
				border : false,
				labelWidth : 5,
				items : [ {
					xtype : 'checkbox',
					id : 'WR',
					checked : true,
					boxLabel : '一般预警',
					inputValue : '1'
				}]
			}]
		}]
	} ]
});
// ************************* 模板列表 ****************************
var store = new Ext.data.Store(
{
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"ID",mapping:"neId"},
	   {name:"EMS",mapping:"emsDisplayName"},
	   {name:"AREA",mapping:"areaName"},
	   {name:"MULIT_CIRCLE_NE",mapping:"neName"},
	   {name:"NE_TYPE",mapping:"productName"},
	   {name:"CIRCLE_COUNT",mapping:"count"}
    ])
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
				header : '大汇聚点',
				dataIndex : 'MULIT_CIRCLE_NE',
				width : 150,
				renderer : function(v, metadata, record) {
					circle = record.get('CIRCLE_COUNT');
					if (circle >= WRcount && circle < MNcount) {
						return '<font color="#EEDC82">' + v + '</font>';
					} else if (circle >= MNcount && circle < MJcount) {
						return '<font color="#EE9A00">' + v + '</font>';
					} else if (circle >= MJcount) {
						return '<font color="#EE0000">' + v + '</font>';
					}else{
						return '<font color="black">' + v + '</font>';
					}
				}
			},
			{
				id : 'NE_TYPE',
				header : '网元型号',
				dataIndex : 'NE_TYPE',
				width : 150
			},
			{
				id : 'CIRCLE_COUNT',
				header : '汇聚链数',
				dataIndex : 'CIRCLE_COUNT',
				width : 100,
				renderer : function(v, metadata, record) { 
					return ((v == null) ? "" : "<a href='#'>" + v + "</a>");
				}
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
	stateId : "setCompareValueGridId",
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
	bbar : pageTool,
    listeners: {
        'cellclick':function(grid, rowIndex, columnIndex, e) {
            var record = grid.getStore().getAt(rowIndex); 
            var fieldName = grid.getColumnModel().getDataIndex(columnIndex);
            if('CIRCLE_COUNT'==fieldName){
            	toCircleDetail(record.get("ID"),record.get("MULIT_CIRCLE_NE")) ;
            }
        }
    }
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
	
	var MJ = null;
	var MN = null;
	var WR = null;
	var MJChecked = 1;
	var MNChecked = 1;
	var WRChecked = 1;
	if(Ext.getCmp('MJ').getValue() == false){
		MJChecked = 0;
	}
	if(Ext.getCmp('MN').getValue() == false){
		MNChecked = 0;
	}
	if(Ext.getCmp('WR').getValue() == false){
		WRChecked = 0;
	}
	MJ = Ext.getCmp('MJ').getRawValue();
	MN = Ext.getCmp('MN').getRawValue();
	WR = Ext.getCmp('WR').getRawValue();

	gridPanel.getEl().mask("正在查询...");
	store.proxy = new Ext.data.HttpProxy({url:"network!searchLargeConvergenceNodeList.action"});
	map={
			"treeNodes":nodes,
			"MJChecked":MJChecked,
			"MNChecked":MNChecked,
			"WRChecked":WRChecked,
			"MJ":MJ,
			"MN":MN,
			"WR":WR,
			"limit":limit
	};
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
	map["flag"] = 6; 
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

function toCircleDetail(id,neName) { 
	var url = 'largeConvergenceDetial.jsp?id=' + id;// +"&authSequence=all";
	var title = '所属链列表(' + neName +")";
	var nodeListWin = new Ext.Window({
		id : 'nodeListWin',
		title : title,
		width : 850,
		height : 400,
		isTopContainer : true,
		modal : true,
		plain : true, // 是否为透明背景
		html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />'
	});
	nodeListWin.show();
	// nodeListWin.setHeight(Ext.getCmp('win').getHeight() * 0.9);
	// nodeListWin.setWidth(Ext.getCmp('win').getWidth() * 0.9);
	// nodeListWin.center();
	// nodeListWin.doLayout();
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
		items : [ searchPanel, gridPanel ]
	});

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ centerPanel, westPanel ]
	});
	win.show();
	
	Ext.Ajax.request({
		url:'network!getWRConfig.action',
		method:'Post',
		params:{"cycleType" : 1},
		success:function(response){
			var obj = Ext.decode(response.responseText);
			Ext.getCmp('MJ').setRawValue(obj.MJ);
			MJcount = obj.MJ;
			Ext.getCmp('MN').setRawValue(obj.MN);
			MNcount = obj.MN;
			Ext.getCmp('WR').setRawValue(obj.WR);
			WRcount = obj.WR;
		},
		error:function(response){
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("错误",obj.returnMessage);
		},
		failure:function(response){
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("失败",obj.returnMessage);
		}
	})

});
