var store = new Ext.data.Store({
	url : 'circuit!selectLinks.action',
	reader : new Ext.data.JsonReader({
		totalProperty : "total",
		root : "rows"
	}, [  "BASE_LINK_ID", "NATIVE_EMS_NAME", "A_EMS_NAME", "A_NE_NAME", "A_END_PORT",
			"Z_EMS_NAME", "Z_NE_NAME", "Z_END_PORT", "DIRECTION", "A_STATION_ID","Z_STATION_ID",
			"IS_MANUAL", "USER_LABEL","IS_CONFLICT","cables","cableSection","fiber","fiberId",
			"cableId","aEndPtp","zEndPtp"]),
			url : 'circuit!selectLinks.action'
});
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false,
	header : ''
});
var cm = new Ext.grid.ColumnModel({ 
	defaults : {
		sortable : true 
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), checkboxSelectionModel, {
		id : 'BASE_LINK_ID',
		header : '链路号',
		dataIndex : 'BASE_LINK_ID',
		width : 50
	}, {
		id : 'NATIVE_EMS_NAME',
		header : '链路原始名称',
		dataIndex : 'NATIVE_EMS_NAME',
		width : 100
	},{
		id : 'USER_LABEL',
		header : '链路规范名称',
		dataIndex : 'USER_LABEL',
		width : 100
	}, {
		id : 'emsNameA',
		header : 'A端网管',
		dataIndex : 'A_EMS_NAME',
		width : 100
	}, {
		id : 'neNameA',
		header : 'A端网元',
		dataIndex : 'A_NE_NAME',
		width : 100
	}, {
		id : 'portNameA',
		header : 'A端端口',
		dataIndex : 'A_END_PORT',
		width : 100
	}, {
		id : 'emsNameZ',
		header : 'Z端网管',
		dataIndex : 'Z_EMS_NAME',
		width : 100
	}, {
		id : 'neNameZ',
		header : 'Z端网元',
		dataIndex : 'Z_NE_NAME',
		width : 100
	}, {
		id : 'portNameZ',
		header : 'Z端端口',
		dataIndex : 'Z_END_PORT',
		width : 100
	}, {
		id : 'direction',
		header : '方向',
		dataIndex : 'DIRECTION',
		width : 100,
		renderer : function(v) {
			if (v == 0) {
				return "单向";
			}
			if (v == 1) {
				return "双向";
			}
		}
	}, {
		id : 'isMan',
		header : '生成方式',
		dataIndex : 'IS_MANUAL',
		width : 100,
		renderer : function(v) {
			if (v == 0) {
				return ("自动");
			}
			if (v == 1) {
				return ("手动");
			}
		}
	}, {
		id : 'IS_CONFLICT',
		header : '状态',
		dataIndex : 'IS_CONFLICT',
		width : 100,
		renderer : function(v) {
			if (v == 0) {
				return "";
			} else if (v == 1) {
				return "冲突项";
			}else if (v == 2) {
				return "冲突源";
			}
		}
	},{
		id : 'cables',
		header : '光缆',
		dataIndex : 'cables',
		width : 100
	},{
		id : 'cableSection',
		header : '光缆段',
		dataIndex : 'cableSection',
		width : 100
	},{
		id : 'fiber',
		header : '光纤',
		dataIndex : 'fiber',
		width : 100
	}]
});

//d.定义一个显示页面的工具条
var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	frame : false,
	stripeRows : true, // 交替行效果
	collapsible : false,
	forceFit : false, 
	loadMask : {
		msg : '数据加载中...'
	},
	selModel : checkboxSelectionModel,  
	bbar : pageTool,
	tbar : [{
		text : '删除关联', 
		handler :cancelRelateFiber
	}]
}); 

function cancelRelateFiber(){
	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();  
	if(cell.length==0){
		Ext.Msg.alert("提示","请选择数据！");
		return;
	} else { 
		Ext.Msg.confirm("提示", "确认删除和光缆光纤的关联？", function(btn) {
			if (btn == "yes") {   
				var map = new Array();
				for(var i=0;i<cell.length;i++){
					var relFiber= {
						"BASE_LINK_ID":cell[i].get("BASE_LINK_ID"),
						"DIRECTION":cell[i].get("DIRECTION"),
						"aNodeId":cell[i].get("aEndPtp"),
						"zNodeId":cell[i].get("zEndPtp")
			    	};
					map.push(relFiber);
				}  
				Ext.Ajax.request({
					url : 'circuit!cancelRelateFiber.action',
					params : {
						"jsonString":Ext.encode(map)
					},
					success : function(response) { 
						var obj = Ext.decode(response.responseText); 
						if (obj.returnResult == 1) {  
							Ext.Msg.alert("提示",obj.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							});  
						} 
						if (obj.returnResult == 0) {  
							Ext.Msg.alert('提示', obj.returnMessage);
						}
					},
					error:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    },
				    failure:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    }
				});
			}
		});
	}
}
Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};

	var win = new Ext.Viewport({
		id : 'win',
		loadMask : true,
		layout : 'border',
		items : [ gridPanel ],
		renderTo : Ext.getBody()
	}); 
	
	var jsonData = {
			"linkId" : 0,
			"limit" : 200,
			"aNodeId" : -1, 
			"linkType":1
		};
	if(!!cableId && cableId>0){ 
		jsonData["processKey"]=cableId;
		jsonData["jsonString"]="cableId"; 
	}
	if(!!fiberId && fiberId>0){
		jsonData["processKey"]=fiberId;
		jsonData["jsonString"]="fiberId";  
	}   
	store.baseParams = jsonData;
	store.load();
});