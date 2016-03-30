
var myPageSize = 400;

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
	baseParams : {"displayAll" : true,"displayNone" : true,"authDomain":false},
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
	}
});

var store = new Ext.data.Store({
	//1代表查询corba连接
	url : 'connection!getTopoLinkSyncListByEmsGroupId.action',
	baseParams : {
		"emsConnectionModel.emsGroupId" : "-99",
		"limit" : myPageSize
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsConnectionId", "emsDisplayName", "TYPE", "linkSyncTime",
			"linkSyncStatus", "linkSyncResult", "emsGroupId", "emsGroupName","syncMode" ])
});

//************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true,
	header : ""
});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true, // columns are not sortable by default  
		forceFit : false
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'emsGroupName',
		header : '网管分组',
		width : 80,
		dataIndex : 'emsGroupName',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	}, {
		id : 'emsDisplayName',
		header : '网管名称',
		width : 160,
		dataIndex : 'emsDisplayName',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	}, {
		id : 'TYPE',
		header : '网管类型',
		dataIndex : 'TYPE',
		width : 100
	}, {
		id : 'linkSyncStatus',
		header : '同步状态',
		dataIndex : 'linkSyncStatus',
		renderer : colorGrid,
		width : 100
	}, {
		id : 'syncMode',
		header : '同步模式',
		dataIndex : 'syncMode', 
		width : 100
	}, {
		id : 'linkSyncTime',
		header : '成功同步时间',
		dataIndex : 'linkSyncTime',
		width : 140
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	stripeRows : true,
	autoScroll : true,
	frame : false,
	cm : cm,
	store : store,
	loadMask : true,
	clicksToEdit : 2,//设置点击几次才可编辑  
	selModel : checkboxSelectionModel, //必须加不然不能选checkbox
	viewConfig: {
        forceFit:false
    },
	bbar : pageTool,
	tbar : [ '-', '网管分组：' , {
		xtype : 'combo',
		id : 'emsGroup',
		name : 'emsGroup',
		emptyText : '请选择网管分组',
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
				store.baseParams = {
					"emsConnectionModel.emsGroupId" : emsGroupId,
					"limit" : myPageSize
				};
				store.load();
			}
		}
	}, '-', {
		id : "syncInfo",
		text : '同步',
		privilege:actionAuth,
		//disabled:true,
		icon : '../../resource/images/btnImages/sync.png',
		handler : function() {
			syncInfo();
		}
	},{
		text : '同步模式', 
		privilege : modAuth,
		menu : {
			items : [{
				text : '自动同步',
				handler : function(){
					editSyncMode(2);
				}
			}, {
				text : '人工同步',
				handler : function(){
					editSyncMode(1);
				}
			}]
		}
	} ]
});
//同步模式 1.手工同步 2.自动同步
function editSyncMode(syncMode){  
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		var jsonData = {
			"emsConnectionId" : cell[0].get('emsConnectionId'),
			"syncMode":syncMode
		}; 
		Ext.Ajax.request({
			url : 'connection!editSyncMode.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var pageTool = Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
			},
			error : function(response) {
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				Ext.Msg.alert("错误", response.responseText);
			}
		}); 
	} else{
		Ext.Msg.alert("提示","请选择网管！");
		return;
	}
}


// 颜色设置
function modeColorGrid(v, m) {
	if (v == '自动') {
		m.css = 'x-grid-font-blue';
	} else if (v == '手动') {
		m.css = 'x-grid-font-orange';
	} else {
		m.css = 'x-grid-font-red';
	}
	return v;
}

function connectionColorGrid(v, m) {
	if (v == '连接正常') {
		m.css = 'x-grid-font-blue';
	} else if (v == '网络中断') {
		m.css = 'x-grid-font-red';
	} else if (v == '连接异常') {
		m.css = 'x-grid-font-orange';
	}
	return v;
}
  
function colorGrid(v, m) {
	if (v == '已同步') {
		m.css = 'x-grid-font-black';
	}else if (v == '未同步') {
		m.css = 'x-grid-font-red';
	}else if (v == '同步失败') {
		m.css = 'x-grid-font-red';
	}else if (v == '需要同步') {
		m.css = 'x-grid-font-orange';
	}else if (v == '正在同步') {
		m.css = 'x-grid-font-blue';
	}
	return v;
}

var subFrameData;
//同步选中网管
function syncInfo() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		var jsonData = {
				"emsConnectionModel.emsConnectionId" : cell[0].get('emsConnectionId'),
				"emsConnectionModel.emsGroupId" : cell[0].get('emsGroupId')
		};
		var emsConnectionId = cell[0].get('emsConnectionId');
		var emsGroupId = cell[0].get('emsGroupId');
//		Ext.Msg.show(processMessageconfig);
		Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url : 'connection!topoLinkSync.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				Ext.getBody().unmask();
				var pageTool = Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息", "同步失败（"+obj.returnMessage+")");

				}
				if (obj.returnResult == 1) {
					function showSyncNe(){
						if(obj.isNeedSyncNe == true){
							subFrameData={total:obj['syncNeList'].length,rows:obj['syncNeList']};
							var panelHeight = Ext.getCmp('gridPanel').getSize().height; 	
							var windowHeight = 420;
							if(panelHeight < windowHeight) {
								windowHeight = panelHeight*1;
							}
							var url = 'topoLinkSyncNeList.jsp?emsConnectionId=' + emsConnectionId + '&emsGroupId=' + emsGroupId ;
							topoLinkSyncNeWindow = new Ext.Window({ 
								id:'topoLinkSyncNeWindow', 
								title:'提示',
							    width : 340,  
							    height : windowHeight, 
							    isTopContainer : true,
							    modal : true,
						        plain:true,  //是否为透明背景   
								html : '<iframe  id="topoLinkSyncNeWindow_panel" name = "topoLinkSyncNeWindow_panel"  src='+url+' height="100%" width="100%" frameborder=0 border=0/>' 
							});  
							topoLinkSyncNeWindow.show();
						}
					}
					function showSyncEms(){
						if(obj.isNeedSyncEms == true) {
							Ext.Msg.alert("信息", "请先进行网管同步!");
						}else{
							showSyncNe();
						}
					}
					function showChange(){
						if (obj.isChanged==true) {
							if(obj['changeList']==null||obj['changeList'].length==0){
								if(obj.isNeedSyncEms||obj.isNeedSyncNe){
									showSyncEms();
								}else{
									Ext.Msg.alert("信息", "同步完成");
								}
								return;
							}
							subFrameData={total:obj['changeList'].length,rows:obj['changeList']};
							var panelHeight = Ext.getCmp('gridPanel').getSize().height; 	
							var windowHeight = 420;
							if(panelHeight < windowHeight) {
								windowHeight = panelHeight*1;
							}
							var url = 'topoLinkSyncChangeList.jsp?emsConnectionId=' + emsConnectionId + '&emsGroupId=' + emsGroupId ;
							topoLinkSyncChangeWindow = new Ext.Window({ 
								id:'topoLinkSyncChangeWindow', 
								title:'链路更新结果',
							    width : 680,  
							    height : windowHeight, 
							    isTopContainer : true,
							    modal : true,
						        plain:true,  //是否为透明背景   
								html : '<iframe  id="topoLinkSyncChangeWindow_panel" name = "topoLinkSyncChangeWindow_panel"  src='+url+' height="100%" width="100%" frameborder=0 border=0/>' 
							});  
							topoLinkSyncChangeWindow.show();
							topoLinkSyncChangeWindow.on('close',showSyncEms);
						}else{
							if(obj.isNeedSyncEms||obj.isNeedSyncNe){
								showSyncEms();
							}else{
								Ext.Msg.alert("信息", "链路无变化!");
							}
						}
					}
					showChange();
					/*
					
					
					if(obj.isNeedSyncEms == true) {
						Ext.Msg.alert("信息", "请先进行网管同步!");
					} else {
						if (obj.isChanged == true) {
							subFrameData={total:obj['total'],rows:obj['rows']};
							if(obj.isNeedSyncNe == true){
								var panelHeight = Ext.getCmp('gridPanel').getSize().height; 	
								var windowHeight = 420;
								if(panelHeight < windowHeight) {
									windowHeight = panelHeight*1;
								}
								var url = 'topoLinkSyncNeList.jsp?emsConnectionId=' + emsConnectionId + '&emsGroupId=' + emsGroupId ;
								topoLinkSyncNeWindow = new Ext.Window({ 
									id:'topoLinkSyncNeWindow', 
									title:'提示',
								    width : 340,  
								    height : windowHeight, 
								    isTopContainer : true,
								    modal : true,
							        plain:true,  //是否为透明背景   
									html : '<iframe  id="topoLinkSyncNeWindow_panel" name = "topoLinkSyncNeWindow_panel"  src='+url+' height="100%" width="100%" frameborder=0 border=0/>' 
								});  
								topoLinkSyncNeWindow.show();
							} else if(obj.isNeedSyncNe == false){
								if(obj['rows']==null||obj['rows'].length==0){
									Ext.Msg.alert("信息", "同步完成");
									return;
								}
								var panelHeight = Ext.getCmp('gridPanel').getSize().height; 	
								var windowHeight = 420;
								if(panelHeight < windowHeight) {
									windowHeight = panelHeight*1;
								}
								var url = 'topoLinkSyncChangeList.jsp?emsConnectionId=' + emsConnectionId + '&emsGroupId=' + emsGroupId ;
								topoLinkSyncChangeWindow = new Ext.Window({ 
									id:'topoLinkSyncChangeWindow', 
									title:'链路更新结果',
								    width : 680,  
								    height : windowHeight, 
								    isTopContainer : true,
								    modal : true,
							        plain:true,  //是否为透明背景   
									html : '<iframe  id="topoLinkSyncChangeWindow_panel" name = "topoLinkSyncChangeWindow_panel"  src='+url+' height="100%" width="100%" frameborder=0 border=0/>' 
								});  
								topoLinkSyncChangeWindow.show();
							};
						} else if (obj.isChanged  == false) {
							Ext.Msg.alert("信息", "链路无变化!");
						}
					}*/
				}
			},
			error : function(response) {
				Ext.getBody().unmask();
//				clearInterval(timer);
				Ext.Msg.hide();
//				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				Ext.getBody().unmask();
//				clearInterval(timer);
				Ext.Msg.hide();
//				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", response.responseText);
			}
		});
//		timer = setInterval(getProcessPersent, 1000);
	} else{
		Ext.Msg.alert("提示", "请选择要同步的网管！");
	}
}

Ext.onReady(function() {

	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	// 	Ext.Msg = top.Ext.Msg; 
	Ext.Ajax.timeout = 90000000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel ]
	});
	//win.show();	

	//放最后才能显示遮罩效果
	store.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});
