/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var myPageSize = 400;

//var pageTool = new Ext.PagingToolbar({
//	id : 'pageTool',
//	pageSize : myPageSize,// 每页显示的记录值
//	store : store,
//	displayInfo : true,
//	displayMsg : '当前 {0} - {1} ，总数 {2}',
//	emptyMsg : "没有记录"
//});

//var emsGroupStore = new Ext.data.Store({
//	url : 'connection!getConnectGroup.action',
//	baseParams : {
//		"emsGroupId" : "-1"
//	},
//	reader : new Ext.data.JsonReader({
//		totalProperty : 'total',
//		root : "rows"
//	}, [ "GROUP_NAME", "BASE_EMS_GROUP_ID" ])
//});
//
//emsGroupStore.load({
//	callback : function(r, options, success) {
//		if (success) {
//
//		} else {
//			Ext.Msg.alert('提示', '查询网管分组失败！请重新查询');
//		}
//	}
//});

/**
 * 创建网管分组数据源
 */
var emsGroupStore = new Ext.data.Store({
	// 获取数据源地址
	proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
		url : 'common!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
		disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
	}),
	baseParams : {"displayAll" : true,"displayNone" : true},
	// record格式
	reader : new Ext.data.JsonReader({
		root : 'rows',//json数据的key值
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});
// 访问地址，加载数据(如果没有这一句，则不会去后台查询)
emsGroupStore.load({
	// 回调函数
	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
		// 获取下拉框的第一条记录
		var firstValue = records[0].get('BASE_EMS_GROUP_ID');
		// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
		Ext.getCmp('emsGroup').setValue(firstValue);
		//加载ems数据
		emsStore.baseParams.emsGroupId = firstValue;
		emsStore.load();

	}
});

//var emsConnectStore = new Ext.data.Store({
//	url : 'connection!getEmsConnection.action',
////	baseParams : {
////		"emsGroupId" : "-1"
////	},
//	reader : new Ext.data.JsonReader({
//		totalProperty : 'total',
//		root : "rows"
//	}, [ "emsConnectionId", "emsConnectionName", "connnectionType" ])
//});
////emsConnectStore.load({
////	callback : function(r, options, success) {
////		if (success) {
////
////		} else {
////			Ext.Msg.alert('提示', '查询网管失败！请重新查询！');
////		}
////	}
////});
 
/**
 * 创建网管数据源
 */
var emsStore = new Ext.data.Store({
	url : 'common!getAllEmsByEmsGroupId.action',
	baseParams : {'emsGroupId':-99,"displayAll" : false},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['BASE_EMS_CONNECTION_ID','DISPLAY_NAME']
	})
});

///**
// * 加载网管数据源
// */
//emsStore.load({
//	// 回调函数
//	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
////		var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
////		Ext.getCmp('emsCombo').setValue(firstValue);
//	}
//});

var store = new Ext.data.Store(
		{
			url : 'connection!getMstpListByEmsInfo.action',
			baseParams : {
				"emsGroupId" : "-99",
				"emsConnectionId" : "-99",
				"limit" : myPageSize
			},
			reader: new Ext.data.JsonReader({
		        totalProperty: 'total',
				root : "rows"
		    },[
			    "neId","neSerialNo","neName","type","emsConnectionId","emsGroupId","areaName",
			    "VERSION","stationName","roomName","factory","productName","neSyncStatus","neSyncTime"   
		    ])
		});

 var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: myPageSize,//每页显示的记录值
	store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
 });

 var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),checkboxSelectionModel,{
    		id : 'neId',
    		header : 'id',
    		dataIndex : 'neId',
    		width : 150,
    		hidden : true
    	}, {
        	id:'neName',
        	header: '网元名称',
        	dataIndex: 'neName',
        	width: 100
        },{
            id:'areaName',
            header: top.FieldNameDefine.AREA_NAME,
            dataIndex: 'areaName',
            width: 80,
            hidden:true
        },{
            id:'stationName',
            header: top.FieldNameDefine.STATION_NAME,
            dataIndex: 'stationName',
            width: 150,
            hidden:true
        },{
            id:'roomName',
            header: '机房',
            dataIndex: 'roomName',
            width: 120,
            hidden:true
        },{
            id:'factory',
            header: '厂家',
            dataIndex: 'factory',
            width: 80
        },{
        	id:'productName',
            header: '网元型号',
            dataIndex: 'productName',
            width: 140
        },{
        	id:'neSyncStatus',
        	header: '同步状态',
        	dataIndex: 'neSyncStatus',
        	renderer: colorGrid,
        	width: 80
    	},{
        	id:'neSyncTime',
        	header: '成功同步时间',
        	dataIndex: 'neSyncTime',
        	width: 140
    	}]
    });

 
var connectListPanel = new Ext.grid.GridPanel({
	id:"connectListPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox
	viewConfig: {
        forceFit:false
    },
	bbar: pageTool, 
	tbar: [ '-', '网管分组：', {
		xtype : 'combo',
		id : 'emsGroup',
		name : 'emsGroup',
		emptyText:'请选择网管分组',
		// fieldLabel: '请选择网管分组',
		// sideText : '<font color=red>*</font>',
		mode : "local",
		editable : false,
		store : emsGroupStore,
		displayField : "GROUP_NAME",
		valueField : 'BASE_EMS_GROUP_ID',
		triggerAction : 'all',
		// allowBlank:false,
		width : 120,
		anchor : '95%',
		listeners : {
			select : function(combo, record, index) {
				var emsGroupId = Ext.getCmp('emsGroup').getValue();
				emsStore.baseParams = {
						"emsGroupId":emsGroupId
					};
				emsStore.load();
				Ext.getCmp('emsConnect').reset();			
			}
		}
	}, '-',  '网管：' , {
		xtype : 'combo',
		id : 'emsConnect',
		name : 'emsConnect',
		emptyText:'请先选择网管分组',
		// fieldLabel: '请选择网管',
		// sideText : '<font color=red>*</font>',
		mode : "local",
		editable : false,
		store : emsStore,
		displayField : "DISPLAY_NAME",
		valueField : 'BASE_EMS_CONNECTION_ID',
		triggerAction : 'all',
		// allowBlank:false,
		width : 120,
		anchor : '95%',
		listeners : {
			select : function(combo, record, index) {
				var emsGroupId = Ext.getCmp('emsGroup').getValue();
				var emsConnectId = Ext.getCmp('emsConnect').getValue();

				// 加载网元同步列表
				var jsonData = {
					"emsGroupId" : emsGroupId,
					"emsConnectionId" : emsConnectId,
					"limit" : myPageSize
				};

				store.proxy = new Ext.data.HttpProxy({
					url : 'connection!getMstpListByEmsInfo.action'
				});

				store.baseParams = jsonData;

				store.load({

					callback : function(r, options, success) {
						if (success) {

						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
			}
		}
	}, '-',{
            text: '同步',
            privilege:actionAuth,
            icon:'../../resource/images/btnImages/sync.png',
            handler : function(){
            	mstpSync();
        	}
        }
	]
	
}); 

function mstpSync(){
	var processKey = "mstpSync"+new Date().getTime();
	var jsonString=new Array();
	var emsConnectionId = Ext.getCmp('emsConnect').getValue();
	var cell = connectListPanel.getSelectionModel().getSelections();
	if(cell.length>0){

	    	for(var i=0;i<cell.length;i++){
	    		var neModel = {
	    			"emsConnectionId":cell[i].get('emsConnectionId'),
					"name":cell[i].get('neSerialNo'),
					"displayName" : cell[i].get('neName'),
					"neId" : cell[i].get('neId'),
					"syncName": processKey
		    	};
		    	jsonString.push(neModel);
	        }
	        var jsonData = {
	        	"jString":Ext.encode(jsonString)
	    	};

		Ext.Msg.confirm('提示', '同步多个网元以太网信息，时间可能较长，是否同步？',
				function(btn) {
					if (btn == 'yes') {
//						top.Msg.show(processMessageconfig);
						// Ext.getBody().mask('正在执行，请稍候...');
						Ext.Ajax.request({
						    url: 'connection!syncMstpNe.action', 
						    method : 'POST',
						    params: jsonData,
							success : function(response) {
//								Ext.getBody().unmask();
								var obj = Ext.decode(response.responseText);
						    	if(obj.returnResult == 1){
						    		//刷新列表
			                		var pageTool = Ext.getCmp('pageTool');
			                		if(pageTool){
				    					pageTool.doLoad(pageTool.cursor);
				    				}
				                }
				            	if(obj.returnResult == 0){
//				            		clearInterval(timer);
//				            		Ext.Msg.hide();
				            		Ext.Msg.alert("信息",obj.returnMessage);
				            	}
							},
							error : function(response) {
								// Ext.getBody().unmask();
								clearTimer();
								Ext.Msg.hide();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("信息", response.responseText);
							},
							failure : function(response) {
								// Ext.getBody().unmask();
								clearTimer();
								Ext.Msg.hide();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("信息", response.responseText);
							}
						});
						showProcessBar(processKey);  
//						timer = setInterval(getProcessPersent, 1000);
					} else {
//							如果“否”，关闭弹窗，不做任何操作。
					}
				});
	} else {
		Ext.Msg.alert("提示", "请先选取需要同步的网元！");
	}
}


function modeColorGrid(v,m){
	if(v=='自动'){
		m.css='x-grid-font-blue';
	}else if(v=='手动'){
		m.css='x-grid-font-orange';
	}else{
		m.css='x-grid-font-red';
	}
	return v;
}

function colorGrid(v,m) {
	if(v=='连接正常'){
		m.css='x-grid-font-blue';
	}else if(v=='网络中断'){
		m.css='x-grid-font-red';
	}else if(v=='连接异常'){
		m.css='x-grid-font-orange';
	}
	return v;
}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout=18000000; 
	//collapse menu
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
//	Ext.Msg = top.Ext.Msg;
	
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [connectListPanel]
	});
	
	//放最后才能显示遮罩效果
	store.load({
		callback: function(r, options, success){   
			if(success){  
	
			}else{
				Ext.Msg.alert('错误','加载失败！');    
			}
		}
	}); 
 });