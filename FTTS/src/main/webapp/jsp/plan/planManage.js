/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
//获取数据列表
var myPageSize = 200;
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :false});

var nodes;
var areaId;
//区域树     
function getTree(field,level){  
	var win = new Ext.Window({ 
		layout : 'fit',
		modal : true,
		height : 400,
		width : 260,
		pageX :field.getPosition()[0],
		pageY :field.getPosition()[1]+20,
		items : [{
			id:'area',
			xtype : "area",
			maxLevel:level,
		}],
		buttons: [{ 
			text: '确定',
			handler: function(){
				nodes = Ext.getCmp("area").getSelectedNodes();
				if(nodes.total >0){ 
					field.setValue(nodes.nodes[0].text);
					areaId = nodes.nodes[0].id;
//					initPage();
					planStore.setBaseParam('jsonString',areaId);
					planStore.load();
					win.close();
				}
			}
		},{
			text: '取消',
			handler: function(){ win.close(); }
		}]
	});
	win.show();
	field.blur();
}  

//区域树
var areaTree = {
	xtype: 'textfield',
	id : 'areaField', 
	readOnly : true,
	emptyText:'选择区域',
	width: 110,
	listeners : {
		'focus' : function(field){ 
			getTree(this,10); 
			field.blur();
		}
	}
};

var planStore = new Ext.data.Store({
	url : 'plan!getPlanList.action',
	baseParams :{
		"jsonString":areaId
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	[ "TEST_PLAN_ID","NAME","NUMBER","FACTORY","TYPE","START_TIME","STATUS","AREA_NAME","STATION_NAME","ROOM_NAME","NOTE"])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store: planStore,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns:[ new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel,{
			id:'TEST_PLAN_ID',
			header:'计划ID',
			dataIndex:'TEST_PLAN_ID',
			hidden:true,
			width:120
		},{
			id:'NAME',
			header:'设备名称',
			dataIndex:'NAME',
			width:120
		},{
			id:'NUMBER',
			header:'设备编号',
			dataIndex:'NUMBER',
			width:120
		},{
			id:'FACTORY',
			header:'厂家',
			dataIndex:'FACTORY',
			width:120,
			renderer: function(val){
				if(val == 11){
					return "昕天卫";
				}else if(val == 12){
					return "中博";
				}
			}
		},{
			id:'TYPE',
			header:'设备类型',
			dataIndex:'TYPE',
			width:120,
			renderer: function(val){
				if(val == 111){
					return "RTU";
				}else if(val == 112){
					return "CTU";
				}
			}
		},{
			id:'START_TIME',
			header:'执行开始时间',
			dataIndex:'START_TIME',
			width:120
		},{
			id:'STATUS',
			header:'状态',
			dataIndex:'STATUS',
			width:120,
			renderer: function(val){
				if(val == 0){
					return "启用";
				}else if(val == 1){
					return "挂起";
				}
			}
		},{
			id:'AREA_NAME',
			header:'区域',
			dataIndex:'AREA_NAME',
			width:120
		},{
			id:'STATION_NAME',
			header:'局站',
			dataIndex:'STATION_NAME',
			width:120
		},{
			id:'ROOM_NAME',
			header:'机房',
			dataIndex:'ROOM_NAME',
			width:120
		},{
			id:'NOTE',
			header:'备注',
			dataIndex:'NOTE',
			width:120
		}
	]}
);

var gridPanel = new Ext.grid.GridPanel({
	region : 'center',
	stripeRows : true,
	autoScroll : true,
	frame : false,
	store : planStore,
	loadMask : true,
	border:true,
	cm : columnModel,
	selModel:checkboxSelectionModel,
	bbar: pageTool,
	tbar : ['-',{
		xtype:"label",
		text:"区域：",
		width:100
	},areaTree,'-',{
		text : '挂起',
		icon : '../../resource/images/btnImages/control_pause_blue.png',
		handler : planPending
	},'-',{
		text : '启用',
		icon : '../../resource/images/btnImages/control_play_blue.png',
		handler : planStartup
	},'-',{
		text : '设置',
		icon : '../../resource/images/btnImages/config_add.png',
		handler : planConfig
	}]
});

var PlanConfigWin = new Ext.Window({
	id:'PlanConfigWin',
	width: 830,
	height: 420,
	y:30,
	closable: true,
	closeAction: 'hide',
	border: false,
	modal: true,
	buttonAlign:'center',
	items: [planConfigForm],
	buttons: [{
		xtype : 'button',
		text : '确定',
//		icon : '../../resource/images/btnImages/add.png',
		handler : function() {
			modifyTestRoute("ok");
		}
	}, {
		xtype : 'button',
		text : '取消',
//		icon : '../../resource/images/btnImages/arrow_undo.png',
		handler : function() {
			// 提交修改，不然store.getModifiedRecords()数据会累加
			routeGridPanel.getStore().commitChanges();
			PlanConfigWin.hide();
		}
	}, {
		xtype : 'button',
		text : '应用',
//		icon : '../../resource/images/btnImages/disk.png',
		handler : function() {
			modifyTestRoute("apply");
		}
	}]
});

function initPage(){
	var parameters =
	{
		"jsonString":Ext.encode({
			'RESOURCE_AREA_ID':areaId
		})
	}
	planStore.reload({
//		params:parameters,
		callback: function(r, options, success){   
			if(success){ 
				
			}else{
				
			}   
		} 
	});
}
function planPending(){
	var cells = gridPanel.getSelectionModel().getSelections();
	if(cells.length > 0){
		var planIds = "";
		for(var i=0;i<cells.length;i++){
			planIds += cells[i].get("TEST_PLAN_ID");
			planIds += ",";
		}
		planIds = planIds.substr(0,planIds.length -1);
		var parameters =
		{
			"jsonString":Ext.encode({
				'planIds':planIds
			})
		}
		Ext.Ajax.timeout=120000;
		top.Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
		    url: 'plan!pendingPlan.action',
		    params: parameters,
		    success: function(response){
		    	top.Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);    	
				initPage();
		    }
		});
	}else{
		Ext.Msg.show({
		   title:'错误',
		   msg: '请选择测试计划！',
		   buttons: Ext.Msg.CANCEL,
		   icon: Ext.MessageBox.ERROR
		});

	}
}

function planStartup(){
	var cells = gridPanel.getSelectionModel().getSelections();
	if(cells.length > 0){
		var planIds = "";
		for(var i=0;i<cells.length;i++){
			planIds += cells[i].get("TEST_PLAN_ID");
			planIds += ",";
		}
		planIds = planIds.substr(0,planIds.length -1);
		var parameters =
		{
			"jsonString":Ext.encode({
				'planIds':planIds
			})
		}
		Ext.Ajax.timeout=120000;
		top.Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
		    url: 'plan!startUpPlan.action',
		    params: parameters,
		    success: function(response){
		    	top.Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);    	
				initPage();
		    }
		});
	}else{
		Ext.Msg.show({
		   title:'错误',
		   msg: '请选择测试计划！',
		   buttons: Ext.Msg.CANCEL,
		   icon: Ext.MessageBox.ERROR
		});

	}
}

function planConfig(){
	var cells = gridPanel.getSelectionModel().getSelections();
	if(cells.length == 1){
		var planId = cells[0].get("TEST_PLAN_ID");
		var parameters =
		{
			"jsonString":Ext.encode({
				'planId':planId
			})
		}
		var path = 'plan!getRouteListByPlanId.action';
		routeStore.proxy = new Ext.data.HttpProxy({url:path});
		routeStore.load({
			params:parameters,
			callback: function(r, options, success){   
				if(success){ 
					
				}else{
					
				}   
			} 
		});
//		PlanConfigWin.setPosition(300,50);
		var name = cells[0].get("NAME");
		PlanConfigWin.setTitle("测试计划设置("+name+")");
		PlanConfigWin.show();
		PlanConfigWin.center();
	}else{
		Ext.Msg.show({
		   title:'错误',
		   msg: '每次仅可选择一个计划配置！',
		   buttons: Ext.Msg.CANCEL,
		   icon: Ext.MessageBox.ERROR
		});

	}
}

function modifyTestRoute(button){
	var vRecords = routeGridPanel.getStore().getModifiedRecords();
	var vCount = vRecords.length; //得到记录长度
	if (vCount <= 0){
		PlanConfigWin.hide();
		//Ext.Msg.alert("提示","您没有修改任何值！");
	}else{
		Ext.Msg.confirm('警告',"即将更新测试路由的测试周期/衰耗基准值偏差数据，是否继续？",
		    function(btn){
		        if(btn=='yes'){
		        	var testRouteValueList = new Array();
		    		for (var i = 0; i < vCount; i++) {
		    			var testRouteValue = {
		    					'TEST_ROUTE_ID' : vRecords[i].get('TEST_ROUTE_ID'),
		    					'TEST_PERIOD' : vRecords[i].get('TEST_PERIOD'),
		    					'ATT_OFFSET' : vRecords[i].get('ATT_OFFSET')
		    				};
		    			testRouteValueList.push(Ext.encode(testRouteValue));
		    		}
		    		var jsonData = {
		    				"modifyList" : testRouteValueList
		    			};
		    		Ext.Ajax.timeout=120000;
					top.Ext.getBody().mask('正在执行，请稍候...');
					// 提交修改，不然store.getModifiedRecords()数据会累加
					routeGridPanel.getStore().commitChanges();
					Ext.Ajax.request({
					    url: 'plan!modifyTestRouteValue.action',
					    params: jsonData,
					    success: function(response){
					    	top.Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);  
							
							if(button == "ok"){
								PlanConfigWin.hide();
							}else{
								routeStore.reload();
							}
							
							Ext.Msg.show({
							   title:'提示',
							   msg: '更新成功！',
							   buttons: Ext.Msg.CANCEL,
							   icon: Ext.MessageBox.INFO
							});
					    }
					});
		        }
		    })
		}
};

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.Ajax.timeout = 900000;
	// Ext.Msg = top.Ext.Msg;
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
	
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [gridPanel]
	});
	initPage();
});
