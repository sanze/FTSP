var store = new Ext.data.Store({ 
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "DISPLAY_NAME", "VC4" ,"VC12" ,"aNeDisplayName" ,"aPortDesc",
	     "zNeDisplayName","zPortDesc", "DIRECTION",,"IS_MANUAL"])
});
var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), {
		id : 'DISPLAY_NAME',
		header : '链路名称',
		dataIndex : 'DISPLAY_NAME',
		width : 70
	},{
		id : 'VC4',
		header : 'VC4时隙可用率',
		dataIndex : 'VC4',
		width : 95,
		renderer :colorGrid
	},{
		id : 'VC12',
		header : 'VC12时隙可用率',
		dataIndex : 'VC12',
		width : 100,
		renderer :colorGrid
	}, {
		id : 'aNeDisplayName',
		header : 'A端网元',
		dataIndex : 'aNeDisplayName',
		width : 120
	},{
		id : 'aPortDesc',
		header : 'A端端口',
		dataIndex : 'aPortDesc',
		width : 150
	}, {
		id : 'zNeDisplayName',
		header : 'z端网元',
		dataIndex : 'zNeDisplayName',
		width : 120 
	},{
		id : 'zPortDesc',
		header : 'z端端口',
		dataIndex : 'zPortDesc',
		width : 150
	}, {
		id : 'DIRECTION',
		header : '方向',
		dataIndex : 'DIRECTION',
		width : 60,
		renderer : function(v) {
			if (v == 0) {
				return "单向";
			}else if (v == 1) {
				return "双向";
			}else {
				return v;
			}
		}
	},{
		id : 'IS_MANUAL',
		header : '生成方式',
		dataIndex : 'IS_MANUAL',
		width : 70,
		renderer : function(v) {
			if (v == 0) {
				return "自动";
			}else if (v == 1) {
				return "手动";
			}else {
				return v;
			}
		}
	}]
});

var swfPanel = new Ext.Panel({
	id : 'swfPaneltpId',
	region : "north",
	height : 300, 
	collapsible : true,
	collapseFirst : false,
	items : [ {
		xtype : "flex",
		id : "flex",
		type : "tp"
	} ]
});

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true 
});

function colorGrid(v,m,r) { 
	if(m.id=="VC4"){
		return compareValue(v,MS_VC4_MJ,MS_VC4_MN,MS_VC4_WR); 
	} 
	if(m.id=="VC12"){ 
		return compareValue(v,MS_VC12_MJ,MS_VC12_MN,MS_VC12_WR);
	}
	return v;
}

function initData(){   
	gridPanel.getEl().mask("正在查询...");
    store.proxy = new Ext.data.HttpProxy({
		url : 'network!searchDetailMulti.action'
    });
	store.baseParams ={"paramMap.rlId" : rlId};
	store.load({ 
	    callback : function (r, options, success) { 
	        if (success) {
	        	gridPanel.getEl().unmask();
	        }else {
	        	gridPanel.getEl().unmask();
	            var obj = Ext.decode(r.responseText);
	            Ext.Msg.alert("提示", obj.returnMessage);
	        }
	    }
	});
	Ext.getCmp("flex").on("initialize", function() {
		Ext.Ajax.request({
			url : 'network!getTopoNodeAndLink.action',
			type : 'post',
			params : {
				"paramMap.rlId" : rlId
			},
			success : function(response) {  
				var obj = Ext.decode(response.responseText);
				if(obj.returnResult==1){
					Ext.getCmp('flex').loadData(obj);
				} 
			},
			error : function(response) { 
				Ext.Msg.alert("异常", response.responseText);
			},
			failure : function(response) { 
				Ext.Msg.alert("异常", response.responseText);
			}
		}); 
	});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000; 
	getEarlyAlarmSetting(); 
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [swfPanel,gridPanel]
	});
	win.show();
	initData();
});