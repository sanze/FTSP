var map={};

// ************************* 查询条件 ****************************
var structCombo;
(function() {
	var dataKineData = [ [ '环', '1' ], [ '链', '2' ], [ '全部', '3' ] ];
	var dataKineStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'displayName'
		}, {
			name : 'dataKind'
		} ]
	});
	dataKineStore.loadData(dataKineData);
	structCombo = new Ext.form.ComboBox({
		id : 'structCombo',
		fieldLabel : '拓扑结构',
		privilege : viewAuth,
		store : dataKineStore,
		displayField : "displayName",
		valueField : 'dataKind',
		triggerAction : 'all',
		mode : 'local',
		editable : false,
		allowBlank : false,
		value : '3',
		width : 100
	});
})(); 

var row = [  
       	{ header: ''},//header表示父表头标题，colspan表示包含子列数目   
       	{ header: ''},   
       	{ header: ''},
    	{ header: ''},
    	{ header: ''},
       	{ header: '复用段VC4时隙', colspan: 3, align: 'center' },   
       	{ header: '复用段VC12时隙', colspan: 3, align: 'center' },  
       	{ header: ''},   
      	{ header: ''}, 
      	{ header: ''}, 
      	{ header: ''}, 
    	{ header: ''},
      	{ header: ''}, 
      	{ header: ''}, 
      	{ header: ''}, 
      	{ header: ''}, 
      	{ header: ''}, 
      	{ header: ''}, 
      	{ header: ''}
        ];

       var group = new Ext.ux.grid.ColumnHeaderGroup({  
           rows: [row]  
       }); 
var searchPanel = new Ext.FormPanel({
	id : 'searchPanel',
	region : 'north',
	height : 90,
	bodyStyle : 'padding:20px 10px 0', 
	autoScroll : true,
	items : [{
		border : false, 
		items : [{ 
			border : false, 
			layout : 'column',
			items : [ { 
				width : 120,
				layout : 'form',
				labelWidth : 80,
				border : false,
				labelWidth : 5,
				items : [ {
					xtype : 'checkbox',
					id : 'alarm4',
					checked : true,
					boxLabel : '重要预警',
					inputValue : '4'
					} ]
				}, { 
					width : 120,
					layout : 'form',
					labelWidth : 80,
					border : false,
					labelWidth : 5,
					items : [ {
						xtype : 'checkbox',
						id : 'alarm3',
						checked : true,
						boxLabel : '次要预警',
						inputValue : '3'
					} ]
				}, { 
					width : 250,
					layout : 'form',
					labelWidth : 80,
					border : false,
					items : [ structCombo ]
	
				}]
		}, { 
			border : false, 
			layout : 'column',
				items : [{ 
					width : 120,
					layout : 'form',
					labelWidth : 80,
					border : false,
					labelWidth : 5,
					items : [ {
						xtype : 'checkbox',
						id : 'alarm2',
						checked : true,
						boxLabel : '一般预警',
						inputValue : '2'
					} ]
				}, { 
					width : 120,
					layout : 'form',
					labelWidth : 80,
					border : false,
					labelWidth : 5,
					items : [ {
						xtype : 'checkbox',
						id : 'alarm1',
						checked : true,
						boxLabel : '无预警',
						inputValue : '1'
					} ]
				}, { 
					width : 250,
					layout : 'form',
					labelWidth : 80,
					border : false,
					items : [ levelCombo ]
			}]
		}]
	}]
});

var store = new Ext.data.Store({ 
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["RESOURCE_TRANS_SYS_ID","areaName","SYS_NAME","SYS_CODE","DOMAIN",
	    "VC4_OCCUPANCY_MAX","VC4_OCCUPANCY_AVG","VC4_OCCUPANCY_MIN",
	    "VC12_OCCUPANCY_MAX","VC12_OCCUPANCY_AVG","VC12_OCCUPANCY_MIN", 
	    "TYPE","NODE_COUNT","PRO_GROUP_TYPE","RATE","NET_LEVEL","WAVE_COUNT",
	    "GENERATE_METHOD","STATUS","emsDisplayName","NOTE","TRANS_MEDIUM"])
});
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});
var cm = new Ext.grid.ColumnModel({ 
	defaults : {
		sortable : false 
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}), checkboxSelectionModel,
			{
				id : 'areaName',
				header : '所属'+top.FieldNameDefine.AREA_NAME,
				dataIndex : 'areaName',
				width : 100
			},{
				id : 'SYS_NAME',
				header : '系统名称',
				dataIndex : 'SYS_NAME',
				width : 100
			},
			{
				id : 'SYS_CODE',
				header : '系统代号',
				dataIndex : 'SYS_CODE',
				width : 70
			}, {
				id : 'VC4_OCCUPANCY_MAX',
				header : '最大可用率',
				dataIndex : 'VC4_OCCUPANCY_MAX',
				width : 80,
				renderer :colorGrid
			}, {
				id : 'VC4_OCCUPANCY_AVG',
				header : '平均可用率',
				dataIndex : 'VC4_OCCUPANCY_AVG',
				width : 80,
				renderer :colorGrid 
			}, {
				id : 'VC4_OCCUPANCY_MIN',
				header : '最小可用率',
				dataIndex : 'VC4_OCCUPANCY_MIN',
				width : 80,
				renderer :colorGrid
			}, {
				id : 'VC12_OCCUPANCY_MAX',
				header : '最大可用率',
				dataIndex : 'VC12_OCCUPANCY_MAX',
				width : 80,
				renderer :colorGrid  
			}, {
				id : 'VC12_OCCUPANCY_AVG',
				header : '平均可用率',
				dataIndex : 'VC12_OCCUPANCY_AVG',
				width : 80,
				renderer :colorGrid
			}, {
				id : 'VC12_OCCUPANCY_MIN',
				header : '最小可用率',
				dataIndex : 'VC12_OCCUPANCY_MIN',
				width : 80,
				renderer :colorGrid
			}, {
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
				header : '节点数',
				dataIndex : 'NODE_COUNT',
				width : 70
			} , {
				id : 'PRO_GROUP_TYPE',
				header : '保护类型',
				dataIndex : 'PRO_GROUP_TYPE',
				width : 100,
				renderer : proTypeRenderer
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

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	frame : false,
	stripeRows : true, // 交替行效果 
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	stateId : "multiEarlyWarnId",
	stateful : true,
	tbar : [ '-', {
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchMultiEarlyWarn
	},'-', {
		text : '详情',
		icon : '../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchDetailMultiEarlyWarn
	}, "-", {
		text : '导出',
		icon : '../../resource/images/btnImages/export.png',
		privilege : actionAuth,
		handler : exportData
	} ],
	bbar : pageTool,
	plugins: group // 要加上这一句
});

function colorGrid(v,m,r) {  
	if(m.id=="VC4_OCCUPANCY_MAX" || m.id=="VC4_OCCUPANCY_AVG" || m.id=="VC4_OCCUPANCY_MIN"){
		return compareValue(v,MS_VC4_MJ,MS_VC4_MN,MS_VC4_WR);
	}
	if(m.id=="VC12_OCCUPANCY_MAX" || m.id=="VC12_OCCUPANCY_AVG" || m.id=="VC12_OCCUPANCY_MIN"){
		return compareValue(v,MS_VC12_MJ,MS_VC12_MN,MS_VC12_WR);
	} 
} 

function searchDetailMultiEarlyWarn() { 
	 var cell = gridPanel.getSelectionModel().getSelections(); 
    if (cell.length == 0) {
        Ext.Msg.alert("提示", "请选择一条记录！");
        return;
    }   
	var url = '../networkAnalysis/multiDetail.jsp?rlId='+cell[0].get('RESOURCE_TRANS_SYS_ID');
	parent.addTabPage(url, "复用段时隙可用率详情（"+cell[0].get('SYS_NAME')+"）", authSequence);  
}

function initData(){
    store.proxy = new Ext.data.HttpProxy({
		url : 'network!initMultiEarlyWarn.action'
    });
	store.baseParams = {"paramMap.rlId":rlId};
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
var isSearched = false;
function searchMultiEarlyWarn(){ 
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
			"paramMap.structCombo":Ext.getCmp("structCombo").getValue(),
			"paramMap.levelCombo":Ext.getCmp("levelCombo").getValue(),
			"limit":200
		}; 
	if(Ext.getCmp("alarm1").checked)
		map["paramMap.alarm1"] = Ext.getCmp("alarm1").inputValue;
	if(Ext.getCmp("alarm2").checked)
		map["paramMap.alarm2"] = Ext.getCmp("alarm2").inputValue;
	if(Ext.getCmp("alarm3").checked)
		map["paramMap.alarm3"] = Ext.getCmp("alarm3").inputValue;
	if(Ext.getCmp("alarm4").checked)
		map["paramMap.alarm4"] = Ext.getCmp("alarm4").inputValue;
	if(!(map["paramMap.alarm1"] || map["paramMap.alarm2"] ||
			map["paramMap.alarm3"] || map["paramMap.alarm4"])){
		Ext.Msg.alert("提示", "请勾选预警复选框！");
		return; 
	}else{
		if(MS_VC4_MJ==null || MS_VC4_MJ==0 || MS_VC12_MJ == null || MS_VC12_MJ==0){
			Ext.Msg.alert("提示", "预警设置加载未完成！");
			return; 
		} 
		map["paramMap.VC4MJ"]=MS_VC4_MJ;
		map["paramMap.VC4MN"]=MS_VC4_MN;
		map["paramMap.VC4WR"]=MS_VC4_WR;
		map["paramMap.VC12MJ"]=MS_VC12_MJ;
		map["paramMap.VC12MN"]=MS_VC12_MN;
		map["paramMap.VC12WR"]=MS_VC12_WR;
	}
	isSearched=true;
    store.proxy = new Ext.data.HttpProxy({
		url : 'network!searchMultiEarlyWarn.action'
    });
	store.baseParams = map;
	gridPanel.getEl().mask("正在查询...");
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
	if(isSearched==false && store.getCount() == 1){
		map = {
				"paramMap.rlId":rlId,
				"paramMap.flag":21
			};  
		gridPanel.getEl().mask("正在导出...");
		exportRequest(map);
	}else{ 
		map["paramMap.flag"] = 2;
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
} 

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Ajax.timeout = 90000000; 

	if(rlId!=null && rlId!=''){
		initData();
	} 
	getEarlyAlarmSetting(); 
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
    tree = Ext.getCmp("tree"); 
});
