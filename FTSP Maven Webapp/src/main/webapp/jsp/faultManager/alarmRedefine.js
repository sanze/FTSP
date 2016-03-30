/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/

/**
 * 创建网管分组数据源
 */
var emsGroupStore = new Ext.data.Store({
	proxy: new Ext.data.HttpProxy({
		url : 'common!getAllEmsGroups.action', 
		disableCaching: false
	}),
	baseParams : {"displayAll" : true,"displayNone" : true},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});

/**
 * 加载网管分组数据源
 */
emsGroupStore.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}else{
			var firstValue = records[0].get('BASE_EMS_GROUP_ID');
			Ext.getCmp('emsGroupCombo').setValue(firstValue);
		}
	}
});

/**
 * 创建网管分组下拉框
 */
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : emsGroupStore,
	valueField : 'BASE_EMS_GROUP_ID',
	displayField : 'GROUP_NAME',
	editable : false,
	triggerAction : 'all',
	width :150,
	resizable: true,
	listeners : {
		select : function(combo, record, index) {
			store.baseParams = {'jsonString':Ext.encode({'emsGroupId':record.get('BASE_EMS_GROUP_ID')}),
				'limit':500
			},
			store.load({
				callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
			});
		}
	}
});

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
		dataIndex : 'ID',
		width : 200,	
		hidden : true
	},{
		header : '网管ID',
		dataIndex : 'EMS_ID',
		width : 200,
		hidden : true
	},{
		header : '网管分组',
		dataIndex : 'GROUP_NAME',
		width : 150
	}, {
		header : '网管名称',
		dataIndex : 'EMS_NAME',
		width : 150
	}, {
		header : '告警/事件名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 150
	}, {
		header : '原告警/事件等级',
		dataIndex : 'ALARM_LEVEL',
		width : 150,
		renderer : changeDisplayValue
	}, {
		header : '重定义告警等级',
		dataIndex : 'NEW_ALARM_LEVEL',
		width : 150,
		renderer : changeDisplayValue
	}, {
		header : '状态',
		dataIndex : 'STATUS',
		width : 150,
		renderer : function(v){
			if(v==2){
				return '禁用';
			}else{
				return '启动';
			}
		}
	}]
});

/**
 * 下拉框显示值转换
 */
function changeDisplayValue(v){
	if(v==1){
		return '紧急';
	}else if(v==2){
		return '重要';
	}else if(v==3){
		return '次要';
	}else if(v==4){
		return '提示';
	}
}

/**
 * 创建表格数据源
 */
var store = new Ext.data.Store({
	url : 'fault!getAlarmRedefineByEmsGroup.action',
	baseParams : {'jsonString':Ext.encode({'emsGroupId':'-99'}),'limit':500},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["ID","EMS_ID","GROUP_NAME","EMS_NAME","NATIVE_PROBABLE_CAUSE","ALARM_LEVEL","NEW_ALARM_LEVEL","STATUS"])
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
//	title :'告警及事件重定义',
	region : 'center',
	store : store,
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	clicksToEdit : 1,
	/*stateId:'alarmRedefineId',  
	stateful:true,*/
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-','网管分组：',emsGroupCombo,'-',{
		privilege:addAuth,
        text: '新增',
        icon : '../../resource/images/btnImages/add.png',
        handler : function(){
        	addAlarmRedefine();
        }
    },{
    	privilege:delAuth,
        text: '删除',
        icon : '../../resource/images/btnImages/delete.png',
        handler : function(){
        	deleteAlarmRedefine();
        }
    },{
    	privilege:modAuth,
        text: '修改',
        icon : '../../resource/images/btnImages/modify.png',
        handler : function(){
        	modifyAlarmRedefine();
        }
    },'-',{
    	privilege:actionAuth,
        text: '启用',
        icon : '../../resource/images/btnImages/control_play.png',
        handler : function(){
        	updateAlarmRedefineStatus(1);
        }
    },{
    	privilege:actionAuth,
        text: '挂起',
        icon : '../../resource/images/btnImages/control_stop.png',
        handler : function(){
        	updateAlarmRedefineStatus(2);
        }
    }],
    bbar : pageTool
});

/**
 * 新增告警重定义
 */
function addAlarmRedefine(){
	var url = 'addAlarmRedefine.jsp?type=add';
	var addAlarmRedefineWindow = new Ext.Window({
		id : 'addAlarmRedefineWindow',
		title : '新增告警及事件重定义规则',
		width : 740,
		height : 200,
		isTopContainer : true,
		modal : true,
		autoScroll : false,
		resizable : false,
		html : "<iframe  id='addAlarmRedefine_panel' name = 'addAlarmRedefine_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addAlarmRedefineWindow.show();
}

/**
 * 删除告警重定义
 */
function deleteAlarmRedefine(){
	// 需要删除的记录
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择模板');
	}else{
		var redefineIds = '';
		var count = 0;
		for ( var i = 0; i < records.length; i++) {
			redefineIds += records[i].get('ID') + ',';
			if(records[i].get('STATUS')==1){
				count++;
			}
		}
		redefineIds = redefineIds.substring(0, redefineIds.lastIndexOf(','));
		if(count==0){
			Ext.Msg.confirm('系统提示','删除模板后，无法恢复。是否确认？',function(btn){       
				if(btn=='yes'){
					
						Ext.Ajax.request({
						    url: 'fault!deleteAlarmRedefine.action',
						    method: 'POST',
						    params: {'jsonString':Ext.encode({'redefineIds' : redefineIds})},
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
			},this);
		}else{
			Ext.Msg.alert('提示', '模板在启用状态，请先禁用');
		}
	}
}

/**
 * 修改告警重定义
 */
function modifyAlarmRedefine(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择模板');
	}else if(records.length==1){
		// 重定义模板ID
		var redefineId = records[0].get('ID');
		var url = 'addAlarmRedefine.jsp?type=modify&redefineId='+redefineId;
		var addAlarmRedefineWindow = new Ext.Window({
			id : 'addAlarmRedefineWindow',
			title : '修改告警及事件重定义规则',
			width : 820,
			height : 200,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='addAlarmRedefine_panel' name = 'addAlarmRedefine_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		addAlarmRedefineWindow.show();
	}else{
		Ext.Msg.alert('提示', '请勿多选');
	}
}

/**
 * 修改告警重定义状态
 */
function updateAlarmRedefineStatus(flag){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择模板');
	}else {
		for ( var i = 0; i < records.length; i++) {
			var redefineId = records[i].get('ID');
			Ext.Ajax.request({
			    url: 'fault!updateAlarmRedefineStatus.action',
			    method: 'POST',
			    params: {'jsonString':Ext.encode({'redefineId':redefineId,'flag':flag})},
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
 * 创建主体部分
 */
//var centerPanel = new Ext.Panel({
//	id : 'centerPanel',
//	region : 'center',
//	autoScroll : true,
//	layout : 'border',
//	items : gridPanel
//});

/**
 * 初始化EXT
 */
Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
		layout : 'border',
		items : gridPanel
	});
});