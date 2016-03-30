var store = new Ext.data.Store({ 
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["DISPLAY_NAME","areaName","PRODUCT_NAME",
	    "PORT_DESC"])
});
 
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), {
		id : 'DISPLAY_NAME',
		header : '网元名',
		dataIndex : 'DISPLAY_NAME',
		width:140
	}, {
		id : 'PRODUCT_NAME',
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width:140
	}, {
		id : 'areaName',
		header : top.FieldNameDefine.AREA_NAME+'信息',
		dataIndex : 'areaName' ,
		width:170
	}, {
		id : 'PORT_DESC',
		header : '端口 ',
		dataIndex : 'PORT_DESC',
		width:270
	}]
});

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true ,
	buttons : [ {
		text : '确定',
		handler : function() {
			var win = parent.Ext.getCmp('nodeListWin');
			if (win) {
				win.close();
			}
		}
	} ]
});

function initData(){   
    store.proxy = new Ext.data.HttpProxy({
		url : 'network!searchAreaNodeList.action'
    });
	store.baseParams ={"paramMap.rlId" : rlId,
			"paramMap.areaName" : areaName};
	store.load({ 
	    callback : function (r, options, success) { 
	        if (success) {
	        }else {
	            var obj = Ext.decode(r.responseText);
	            Ext.Msg.alert("提示", obj.returnMessage);
	        }
	    }
	});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	initData();
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();
});