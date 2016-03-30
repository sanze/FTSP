var store = new Ext.data.Store(
{
	url:'network!searchPolycycleList.action',
	baseParams:{'neId':id},
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"ID",mapping:"id"},
	   {name:"CIRCLE_NAME",mapping:"circleName"},
	   {name:"NODE_COUNT",mapping:"nodeCount"},
	   {name:"RATE",mapping:"rate"},
	   {name:"PRODUCT_TYPE",mapping:"protectType"},
	   {name:"LEVEL",mapping:"level"},
	   {name:"ptpId",mapping:"ptpId"},
	   {name:"PORT",mapping:"port"}
    ])
});

store.load({
	callback: function(r, options, success){
		gridPanel.getEl().unmask();
		if(!success){
			var obj = Ext.decode(response.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});

var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}),{
		id : 'ID',
		header : 'ID',
		dataIndex : 'ID',
		width : 150,
		hidden : true
	}, {
		id : 'CIRCLE_NAME',
		header : '环名',
		dataIndex : 'CIRCLE_NAME',
		width : 100
	}, {
		id : 'NODE_COUNT',
		header : '节点数',
		dataIndex : 'NODE_COUNT',
		width : 30
	}, {
		id : 'RATE',
		header : '速率',
		dataIndex : 'RATE',
		width : 50
	}, {
		id : 'PRODUCT_TYPE',
		header : '保护类型',
		dataIndex : 'PRODUCT_TYPE',
		width : 60
	}, {
		id : 'LEVEL',
		header : '级别',
		dataIndex : 'LEVEL',
		width : 60
	}, {
		id : 'PORT',
		header : '端口 ',
		dataIndex : 'PORT',
		width : 200
	}]
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	viewConfig : {
		 forceFit : true
	},
	buttons : [ {
		text : '返回',
		handler : function() {
			var win = parent.Ext.getCmp('nodeListWin');
			if (win) {
				win.close();
			}
		}
	} ]
});


Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = top.Ext.menu.MenuMgr.hideAll;
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	gridPanel.getEl().mask("正在查询...");
	win.show();
/*
	if (columns == "hidden") {
		var column = cm.getColumnById('PORT3');
		column.hidden = true;
		column.hideable = false;
		column = cm.getColumnById('PORT4');
		column.hidden = true;
		column.hideable = false;
		gridPanel.reconfigure(store, cm);
	}*/
});