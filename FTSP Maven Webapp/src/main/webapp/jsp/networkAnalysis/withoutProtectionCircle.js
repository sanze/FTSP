var map={};  
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
	defaults : {
		sortable : false 
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),checkboxSelectionModel,
			{
				id : 'areaName',
				header : '所属'+top.FieldNameDefine.AREA_NAME,
				dataIndex : 'areaName',
				width : 100
			},{
				id : 'SYS_NAME',
				header : '无保护环',
				dataIndex : 'SYS_NAME',
				width : 100
			},{
				id : 'SYS_CODE',
				header : '系统代号',
				dataIndex : 'SYS_CODE',
				width : 70
			},{
				id : 'DOMAIN',
				header : '技术体制',
				dataIndex : 'DOMAIN',
				width : 80,
				renderer : function(v, m, r) {   
					if (v == 1) {
						return 'SDH';
					} else if (v == 2) {
						return 'WDM';
					}else if (v == 3) {
						return 'MSTP';
					}else if (v == 4) {
						return 'MSAP';
					}else if (v == 5) {
						return 'ASON';
					}else if (v == 6) {
						return 'PDH';
					}else{
						return v;
					}
				} 
			}, {
				id : 'TYPE',
				header : '拓扑结构',
				dataIndex : 'TYPE',
				width : 80,
				renderer : function(v, m, r) {  
					if (v == 1) {
						return '环';
					} else if (v == 2) {
						return '链';
					}else{
						return "其它";
					}
				} 
			} , {
				id : 'TRANS_MEDIUM',
				header : '传输介质',
				dataIndex : 'TRANS_MEDIUM',
				width : 80,
				renderer : function(v, m, r) {  
					if (v == 1) {
						return '光';
					} else if (v == 2) {
						return '电';
					}else if (v == 3) {
						return '微波';
					}else{
						return v;
					}
				}
			} , {
				id : 'NODE_COUNT',
				header : '环节点数',
				dataIndex : 'NODE_COUNT',
				width : 70,
				renderer :function(v, m, r){    
					return ((v==null) ? "" : "<a href='#' onclick=toCountDetail("+ r.get("RESOURCE_TRANS_SYS_ID")+")>" 
								+v+ "</a>");    
				}
			} , {
				id : 'PRO_GROUP_TYPE',
				header : '保护类型',
				dataIndex : 'PRO_GROUP_TYPE',
				width : 100,
				renderer : function(v, m, r) {  
					if (v == 0) {
						return '1+1 MSP';
					} else if (v == 1) {
						return '1:N MSP';
					}else if (v == 2) {
						return '2F BLSR';
					}else if (v == 3) {
						return '4F BLSR';
					}else if (v == 4) {
						return '1+1 ATM';
					}else if (v == 5) {
						return '1:N ATM';
					}else{
						return v;
					}
				}
			} , {
				id : 'WAVE_COUNT',
				header : '波道数',
				dataIndex : 'WAVE_COUNT',
				width : 70
			}, {
				id : 'RATE',
				header : '速率',
				dataIndex : 'RATE',
				width : 70
			} , {
				id : 'NET_LEVEL',
				header : top.FieldNameDefine.NET_LEVEL_NAME,
				dataIndex : 'NET_LEVEL',
				width : 80,
				renderer : netLevelRenderer
			} , {
				id : 'GENERATE_METHOD',
				header : '生成方式',
				dataIndex : 'GENERATE_METHOD',
				width : 80,
				renderer : function(v, m, r) { 
					if (v == 1) {
						return '自动';
					} else if (v == 2) {
						return '手动';
					}else{
						return v;
					}
				}
			} , {
				id : 'STATUS',
				header : '状态',
				dataIndex : 'STATUS',
				width : 70,
				renderer : function(v, m, r) { 
					if (v == 1) {
						return '存在';
					} else if (v == 2) {
						return '不存在';
					}else{
						return v;
					}
				}
			} , {
				id : 'emsDisplayName',
				header : '所在网管',
				dataIndex : 'emsDisplayName',
				width : 120
			}  , {
				id : 'NOTE',
				header : '备注',
				dataIndex : 'NOTE',
				width : 70
			}  ]
}); 

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	frame : false,
	stripeRows : true, // 交替行效果 
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	stateId : "setWPClId",
	stateful : true,
	tbar : [ '-', {
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchWithoutProCircle
	}, "-", {
		text : '导出',
		icon : '../../resource/images/btnImages/export.png',
		privilege : actionAuth,
		handler : exportData
	} ],
	bbar : pageTool
}); 
 
function searchWithoutProCircle() { 
	checkedNodeIds = tree.getSelectedNodes();
	var areaIds="";
	for(var i=0;i<checkedNodeIds.total;i++){    
		if(checkedNodeIds.nodes[i].id != 0){
			areaIds += checkedNodeIds.nodes[i].id+","; 
		}
	}
	areaIds=areaIds.substring(0, areaIds.lastIndexOf(',')); 
	
	map = {
		"paramMap.areaIds" : areaIds,
		"paramMap.tag":3,
		"limit":200
	};   
	gridPanel.getEl().mask("正在查询...");
	store.proxy = new Ext.data.HttpProxy({
		url : 'network!searchCommonEarlyAlarm.action'
		});
		store.baseParams = map;
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
 
}

//导出
function exportData(){ 
	map["paramMap.flag"] = 7;
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				{ 
					gridPanel.getEl().mask("正在导出...");
					exportRequest(map);
				}
			}
		});
	} else{
		gridPanel.getEl().mask("正在导出...");
		exportRequest(map); 
	} 
} 

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000; 
	getEarlyAlarmSetting();  

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel, westPanel ]
	});
	win.show();
    tree = Ext.getCmp("tree"); 
});
