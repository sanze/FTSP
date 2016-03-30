/**
 * 定义新增窗口的初始化参数
 */
/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/

// 告警名称->厂家
var factoryValue = 1;
// 主窗口
var alarmNameSelect = '';
var alarmTypeSelect = '';
var alarmLevelSelect = '';
var alarmAffectSelect = '';
var alarmResourceSelect = '';
var filterName = '';
var filterDesc = '';
// 告警源窗口
var resourceSelectIds = "";
var resourceSelectLvs = "";
var modifyResourceSelectIds = "";
var modifyResourceSelectLvs = "";
// 告警源类型窗口
var neModelSelect = "";
var portModelSelect = "";
var ptpAlarmStatus = false;
var modifyNeModelSelect = "";
var modifyPortModelSelect = "";
var modifyPtpAlarmStatus = false;
// 修改过滤器的ID标志位，用来判断是否是首次修改或者是否是修改同一条过滤器(如果是首次和不同过滤器侧数据从后台加载)
var filterFlag = '';
var modifyFilterFlag = '';

/**
* 创建表格选择模型
 */
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel();
sm.sortLock();

/**
 * 创建表格列模型
 */
var cm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), sm, {
		header : 'id',
		dataIndex : 'FILTER_ID',
		width : 200,	
		hidden : true
	},{
		header : '名称',
		dataIndex : 'FILTER_NAME',
		width : 200
	}, {
		header : '创建者',
		dataIndex : 'CREATOR',
		width : 200
	}, {
		header : '状态',
		dataIndex : 'STATUS',
		width : 200,
		renderer : function(v){
			if(v==2){
				return '挂起';
			}else{
				return '启用';
			}
		}
	}, {
		header : '描述',
		dataIndex : 'DESCRIPTION',
		width : 200
	}]
});

/**
 * 创建表格数据源
 */
var store = new Ext.data.Store({
	url : 'fault!getAlarmFiltersByUserId.action',
	baseParams : {
		'limit':500
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["FILTER_ID","FILTER_NAME","CREATOR","STATUS","DESCRIPTION"])
});

/**
 * 创建表格工具栏
 */
var pageTool = new Ext.PagingToolbar({
	pageSize : 500,
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});

/**
 * 创建表格实例
 */
var gridPanel = new Ext.grid.EditorGridPanel({
//	title :'当前告警过滤器',
	region : 'center',
	store : store,
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	clicksToEdit : 2,
	/*stateId:'currentAlarmFilterId',  
	stateful:true,*/
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-',{
			privilege:addAuth,
	        text: '新增',
	        icon : '../../resource/images/btnImages/add.png',
	        handler : function(){
	        	addAlarmFilter();
	        }
	        	
	    },{
	    	privilege:delAuth,
	        text: '删除',
	        icon : '../../resource/images/btnImages/delete.png',
	        handler : function(){
	        	deleteAlarmFilter();
	        }
	    },{
	    	privilege:modAuth,
	        text: '修改',
	        icon : '../../resource/images/btnImages/modify.png',
	        handler : function(){
	        	modifyAlarmFilter();
	        }
	    },'-',{
	    	privilege:actionAuth,
	        text: '启用',
	        icon : '../../resource/images/btnImages/control_play.png',
	        handler : function(){
	        	updateAlarmFilterStatus(1);
	        }
	    },{
	    	privilege:actionAuth,
	        text: '挂起',
	        icon : '../../resource/images/btnImages/control_stop.png',
	        handler : function(){
	        	updateAlarmFilterStatus(2);
	        }
	    }],
	bbar : pageTool
});

/**
 * 加载表格数据源
 */
store.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

/**
 * 新增过滤器
 */
function addAlarmFilter(){
	var url = 'addAlarmFilter.jsp?type=add';
	var addAlarmFilterWindow = new Ext.Window({
		id : 'addAlarmFilterWindow',
		title : '新增当前告警过滤器',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='addAlarmFilter_panel' name = 'addAlarmFilter_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addAlarmFilterWindow.show();
	// 调节高度
	if (addAlarmFilterWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addAlarmFilterWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		addAlarmFilterWindow.setHeight(addAlarmFilterWindow
				.getInnerHeight());
	}
	addAlarmFilterWindow.center();
}

/**
 * 新增过滤器,告警源选择窗口
 */
function addAlarmFilterResourceWindow(){
	var url = 'currentAlarmFilterResource.jsp?type=add';
	var currentAlarmFilterResourceWindow = new Ext.Window({
		id : 'currentAlarmFilterResourceWindow',
		title : '告警源选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='currentAlarmFilterResource_panel' name = 'currentAlarmFilterResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	currentAlarmFilterResourceWindow.show();
	// 调节高度
	if (currentAlarmFilterResourceWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		currentAlarmFilterResourceWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		currentAlarmFilterResourceWindow.setHeight(currentAlarmFilterResourceWindow
				.getInnerHeight());
	}
	currentAlarmFilterResourceWindow.center();
}

/**
 * 新增过滤器,告警源类型选择窗口
 */
function addAlarmFilterResourceTypeWindow(){
	var url = 'currentAlarmFilterResourceType.jsp?type=add';
	var currentAlarmFilterResourceTypeWindow = new Ext.Window({
		id : 'currentAlarmFilterResourceTypeWindow',
		title : '告警源类型选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='currentAlarmFilterResourceType_panel' name = 'currentAlarmFilterResourceType_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	currentAlarmFilterResourceTypeWindow.show();
	// 调节高度
	if (currentAlarmFilterResourceTypeWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		currentAlarmFilterResourceTypeWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		currentAlarmFilterResourceTypeWindow.setHeight(currentAlarmFilterResourceTypeWindow
				.getInnerHeight());
	}
	currentAlarmFilterResourceTypeWindow.center();
}

/**
 * 修改告警过滤器
 */
function modifyAlarmFilter(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择过滤器');
	}else if(records.length==1){
		// 过滤器ID
		var filterId = records[0].get('FILTER_ID');
		var url = 'addAlarmFilter.jsp?type=modify&filterId='+filterId;
		var addAlarmFilterWindow = new Ext.Window({
			id : 'addAlarmFilterWindow',
			title : '修改当前告警过滤器',
			width : 820,
			height : 560,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='addAlarmFilter_panel' name = 'addAlarmFilter_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
			
		});
		addAlarmFilterWindow.show();
		// 调节高度
		if (addAlarmFilterWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			addAlarmFilterWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
		} else {
			addAlarmFilterWindow.setHeight(addAlarmFilterWindow
					.getInnerHeight());
		}
		addAlarmFilterWindow.center();
	}else{
		Ext.Msg.alert('提示', '请勿多选');
	}
}

/**
 * 修改过滤器,告警源选择窗口
 */
function modifyAlarmFilterResourceWindow(filterId){
	var url = 'currentAlarmFilterResource.jsp?type=modify&filterId='+filterId;
	var currentAlarmFilterResourceWindow = new Ext.Window({
		id : 'currentAlarmFilterResourceWindow',
		title : '告警源选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='currentAlarmFilterResource_panel' name = 'currentAlarmFilterResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	currentAlarmFilterResourceWindow.show();
	// 调节高度
	if (currentAlarmFilterResourceWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		currentAlarmFilterResourceWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		currentAlarmFilterResourceWindow.setHeight(currentAlarmFilterResourceWindow
				.getInnerHeight());
	}
	currentAlarmFilterResourceWindow.center();
}

/**
 * 修改过滤器,告警源类型选择窗口
 */
function modifyAlarmFilterResourceTypeWindow(filterId){
	var url = 'currentAlarmFilterResourceType.jsp?type=modify&filterId='+filterId;
	var currentAlarmFilterResourceTypeWindow = new Ext.Window({
		id : 'currentAlarmFilterResourceTypeWindow',
		title : '告警源类型选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='currentAlarmFilterResourceType_panel' name = 'currentAlarmFilterResourceType_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	currentAlarmFilterResourceTypeWindow.show();
	// 调节高度
	if (currentAlarmFilterResourceTypeWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		currentAlarmFilterResourceTypeWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		currentAlarmFilterResourceTypeWindow.setHeight(currentAlarmFilterResourceTypeWindow
				.getInnerHeight());
	}
	currentAlarmFilterResourceTypeWindow.center();
}

/**
 * 删除过滤器
 */
function deleteAlarmFilter(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择过滤器');
	}else{
		Ext.Msg.confirm('系统提示','过滤器删除后，无法恢复。是否确认？',function(btn){       
			if(btn=='yes'){
				var filterIds = '';
				var count = 0;
				for ( var i = 0; i < records.length; i++) {
					filterIds += records[i].get('FILTER_ID')+',';
					if(records[i].get('STATUS')==1){
						count++;
					}
				}
				filterIds = filterIds.substring(0,filterIds.lastIndexOf(','))
				if(count==0){
					Ext.Ajax.request({
					    url: 'fault!deleteAlarmFilter.action',
					    method: 'POST',
					    params: {'jsonString':Ext.encode({'filterIds' : filterIds})},
					    success : function(response) {
					    	store.load({
					    		callback : function(records,options,success){
									if (!success) {
										Ext.Msg.alert('错误', '查询失败！请重新查询');
									}
								}
					    	});
					    	
						},
						error : function(response) {
							top.Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						},
						failure : function(response) {
							top.Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						}
					})	
				}else{
					Ext.Msg.alert('提示', '模板在启用状态，请先禁用');
				}
			}              
		},this);
	}
}

/**
 * 修改过滤器状态
 */
function updateAlarmFilterStatus(flag){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择过滤器');
	}else{
		if(flag==1&&records.length>1){
			Ext.Msg.alert('提示', '请勿多选');
		}else if(flag==1&&records.length==1&&records[0].get('STATUS')==1){
			store.load({
	    		callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
	    	});
		}else{
			var filterIds = '';
			for ( var i = 0; i < records.length; i++) {
				filterIds += records[i].get('FILTER_ID') + ',';
			}
			filterIds = filterIds.substring(0,filterIds.lastIndexOf(','));
			Ext.Ajax.request({
			    url: 'fault!updateAlarmFilterStatus.action',
			    method: 'POST',
			    params: {'jsonString':Ext.encode({'filterIds':filterIds,'flag':flag})},
			    success : function(response) {
			    	store.load({
			    		callback : function(records,options,success){
							if (!success) {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
			    	});
				},
				error : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				}
			})
		}
	}
}


/**
 * 初始化EXT
 */
Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
		id:"win",
		layout : 'border',
		items : gridPanel
	});
});