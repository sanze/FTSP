
/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/
/**
 * 定义新增窗口的初始化参数
 */
// 告警名称->厂家
var factoryValue = 1;
// 主窗口
var alarmNameSelect = '';
var alarmTypeSelect = '';
var alarmLevelSelect = '';
var alarmAffectSelect = '';
var filterName = '';
var filterDesc = '';
// 告警源窗口
var resourceSelectIds = "";
var resourceSelectLvs = "";
var modifyResourceSelectIds = "";
var modifyResourceSelectLvs = "";
// 修改过滤器的ID标志位，用来判断是否是首次修改或者是否是修改同一条过滤器(如果是首次和不同过滤器侧数据从后台加载)
var filterFlag = '';
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
	},{
		header : '过滤器类型',
		dataIndex : 'FILTER_TYPE',
		width : 200,
		renderer : function(v){
			if(v==1){
				return '自定义过滤器';
			}else{
				return '割接过滤器';
			}
		}
	},{
		header : '有效时间',
		dataIndex : 'EFFECTIVE_TIME',
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
	url : 'fault!getAlarmFiltersComReportByUserId.action',
	baseParams : {
		'limit':500
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["FILTER_ID","FILTER_NAME","FILTER_TYPE","EFFECTIVE_TIME","CREATOR","STATUS","DESCRIPTION"])
});

/**
 * 创建表格工具栏
 */
var pageTool = new Ext.PagingToolbar({
	pageSize : 500,
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

/**
 * 创建表格实例
 */
var gridPanel = new Ext.grid.EditorGridPanel({
//	title :'综告接口过滤器',
	region : 'center',
	store : store,
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	clicksToEdit : 2,
	/*stateId:'alarmFilterComReportId',  
	stateful:true,*/
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-',{
			privilege:addAuth,
	        text: '新增',
	        icon : '../../resource/images/btnImages/add.png',
	        handler : function(){
	        	addAlarmFilterComReport();
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
	        	modifyAlarmFilterComReport();
	        }
	    },'-',{
	    	privilege:viewAuth,
	        text: '详情',
	        icon : '../../resource/images/btnImages/information.png',
	        handler : function(){
	        	viewAlarmFilterComReport();
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
	        text: '禁用',
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
 * 新增综告接口过滤器
 */
function addAlarmFilterComReport(){
	var url = 'addAlarmFilterComReport.jsp?type=add';
	var addAlarmFilterComReportWindow = new Ext.Window({
		id : 'addAlarmFilterComReportWindow',
		title : '新增综告接口过滤器',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='addAlarmFilterComReport_panel' name = 'addAlarmFilterComReport_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addAlarmFilterComReportWindow.show();
	// 调节高度
	if (addAlarmFilterComReportWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addAlarmFilterComReportWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		addAlarmFilterComReportWindow.setHeight(addAlarmFilterComReportWindow
				.getInnerHeight());
	}
	addAlarmFilterComReportWindow.center();
}

/**
 * 新增综告接口过滤器,告警源选择窗口
 */
function addAlarmFilterComReportResourceWindow(){
	var url = 'alarmFilterComReportResource.jsp?type=add';
	var alarmFilterComReportResourceWindow = new Ext.Window({
		id : 'alarmFilterComReportResourceWindow',
		title : '告警源选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='alarmFilterComReportResource_panel' name = 'alarmFilterComReportResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	alarmFilterComReportResourceWindow.show();
	// 调节高度
	if (alarmFilterComReportResourceWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		alarmFilterComReportResourceWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		alarmFilterComReportResourceWindow.setHeight(alarmFilterComReportResourceWindow
				.getInnerHeight());
	}
	alarmFilterComReportResourceWindow.center();
}

/**
 * 修改告警过滤器
 */
function modifyAlarmFilterComReport(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择过滤器');
	}else if(records.length==1){
		// 过滤器ID
		var filterId = records[0].get('FILTER_ID');
		var url = 'addAlarmFilterComReport.jsp?type=modify&filterId='+filterId;
		var addAlarmFilterComReportWindow = new Ext.Window({
			id : 'addAlarmFilterComReportWindow',
			title : '修改当前告警过滤器',
			width : 820,
			height : 560,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='addAlarmFilterComReport_panel' name = 'addAlarmFilterComReport_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
			
		});
		addAlarmFilterComReportWindow.show();
		// 调节高度
		if (addAlarmFilterComReportWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			addAlarmFilterComReportWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
		} else {
			addAlarmFilterComReportWindow.setHeight(addAlarmFilterComReportWindow
					.getInnerHeight());
		}
		addAlarmFilterComReportWindow.center();
	}else{
		Ext.Msg.alert('提示', '请勿多选');
	}
}

/**
 * 修改过滤器,告警源选择窗口
 */
function modifyAlarmFilterComReportResourceWindow(filterId){
	var url = 'alarmFilterComReportResource.jsp?type=modify&filterId='+filterId;
	var alarmFilterComReportResourceWindow = new Ext.Window({
		id : 'alarmFilterComReportResourceWindow',
		title : '告警源设置',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='alarmFilterComReportResource_panel' name = 'alarmFilterComReportResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	alarmFilterComReportResourceWindow.show();
	// 调节高度
	if (alarmFilterComReportResourceWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		alarmFilterComReportResourceWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		alarmFilterComReportResourceWindow.setHeight(alarmFilterComReportResourceWindow
				.getInnerHeight());
	}
	alarmFilterComReportResourceWindow.center();
}

/**
 * 查看综告接口过滤器详情
 */
function viewAlarmFilterComReport(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择过滤器');
	}else if(records.length==1){
		// 过滤器ID
		var filterId = records[0].get('FILTER_ID');
		var url = 'viewAlarmFilterComReport.jsp?filterId='+filterId;
		var viewAlarmFilterComReportWindow = new Ext.Window({
			id : 'viewAlarmFilterComReportWindow',
			title : '查看综告接口过滤器',
			width : 820,
			height : 560,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='viewAlarmFilterComReport_panel' name = 'viewAlarmFilterComReport_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		viewAlarmFilterComReportWindow.show();
		// 调节高度
		if (viewAlarmFilterComReportWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			viewAlarmFilterComReportWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
		} else {
			viewAlarmFilterComReportWindow.setHeight(viewAlarmFilterComReportWindow
					.getInnerHeight());
		}
		viewAlarmFilterComReportWindow.center();
	} else{
		Ext.Msg.alert('提示', '请勿多选');
	}
}

/**
 * 查看综告接口过滤器详情,告警源设置窗口
 */
function viewAlarmFilterComReportResourceWindow(filterId){
	var url = 'viewAlarmFilterComReportResource.jsp?filterId='+filterId;
	var viewAlarmFilterComReportResourceWindow = new Ext.Window({
		id : 'viewAlarmFilterComReportResourceWindow',
		title : '告警源选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='viewAlarmFilterComReportResource_panel' name = 'viewAlarmFilterComReportResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	viewAlarmFilterComReportResourceWindow.show();
	// 调节高度
	if (viewAlarmFilterComReportResourceWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		viewAlarmFilterComReportResourceWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		viewAlarmFilterComReportResourceWindow.setHeight(viewAlarmFilterComReportResourceWindow
				.getInnerHeight());
	}
	viewAlarmFilterComReportResourceWindow.center();
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
		var filterIds = '';
		for ( var i = 0; i < records.length; i++) {
			filterIds += records[i].get('FILTER_ID') + ',';
		}
		filterIds = filterIds.substring(0,filterIds.lastIndexOf(','));
		Ext.Ajax.request({
		    url: 'fault!updateAlarmFilterStatus.action',
		    method: 'POST',
		    params: {'jsonString':Ext.encode({'filterIds':filterIds,'flag':flag,'isIntegratedAlarm':true})},
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

/**
 * 初始化EXT
 */
Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
		id:'win',
		layout : 'border',
		items : gridPanel
	});
});