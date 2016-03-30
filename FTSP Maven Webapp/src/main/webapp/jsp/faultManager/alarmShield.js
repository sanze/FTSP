
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
var alarmShieldName = '';
var alarmShieldDesc = '';
var neName = '';
var modifyNeName = '';
// 告警源窗口
var resourceSelectIds = "";
var resourceSelectLvs = "";
var modifyResourceSelectIds = "";
var modifyResourceSelectLvs = "";
// 修改屏蔽器的ID标志位，用来判断是否是首次修改或者是否是修改同一条屏蔽器(如果是首次和不同屏蔽器侧数据从后台加载)
var modifyShieldFlag = '';
/**
* 创建厂家下拉框
 */
var factoryCombo = new Ext.form.ComboBox({
	id : 'factoryCombo',
	fieldLabel : '厂家',
	editable : false,
	store :  new Ext.data.ArrayStore({
		fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:FACTORY
	}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	width : 130,
	listeners : {
		select : function(combo, record, index) {
			var factory = record.get('value');
			// 还原告警名称下拉框
			Ext.getCmp('alarmNameCombo').reset();
			// 动态改变告警名称数据源的参数
			alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory' : factory,'alarmName':Ext.getCmp('alarmNameCombo').getValue(),'type': 'current'})};
			// 加载告警名称数据源
			alarmNameStore.load({
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
 * 创建告警名称数据源
 */
var alarmNameStore = new Ext.data.Store({
	url : 'fault!getAlarmNameByFactoryFromShield.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['NATIVE_PROBABLE_CAUSE']
	})
});

/**
 * 创建告警名称下拉框
 */ 
var alarmNameCombo = new Ext.form.ComboBox({
	id : 'alarmNameCombo',
	fieldLabel : '告警名称',
	store : alarmNameStore,
	valueField : 'NATIVE_PROBABLE_CAUSE',
	displayField : 'NATIVE_PROBABLE_CAUSE',
	listEmptyText : '未找到匹配的结果',
	loadingText : '搜索中...',
	mode :'remote', 
	width : 130,
	triggerAction : 'all',
	resizable: true,
	listeners : {
		beforequery:function(queryEvent){// 每次输入后触发
			// 获取厂家下拉框的值
			var factory = Ext.getCmp('factoryCombo').getValue();
			// 获取告警名称下拉框输入的值，和历史的内容比较，如果不相同，则取后台模糊查询
			if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
				// 把历史值更新为当前值
				queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
				alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory':factory,'alarmName':queryEvent.combo.getRawValue(),'type': 'current'})};
				alarmNameStore.load();
				return false;
			}
		},
		  scope : this
		}
});

/**
 * 创建网管分组数据源
 *//*
var EmsGroupStore = new Ext.data.Store({
	proxy: new Ext.data.HttpProxy({
		url : 'fault!getAllEmsGroups.action', 
		disableCaching: false
	}),
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});
EmsGroupStore.load({
	callback : function(records,options,success){
//		var firstValue = records[0].get('BASE_EMS_GROUP_ID');
//		Ext.getCmp('emsGroupCombo').setValue(firstValue);
	}
});
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : EmsGroupStore,
	valueField : 'BASE_EMS_GROUP_ID',
	displayField : 'GROUP_NAME',
	editable : false,
	triggerAction : 'all',
	width :150,
	listeners : {
		select : function(combo, record, index) {
			var emsGroupId = record.get('BASE_EMS_GROUP_ID');
			Ext.getCmp('emsCombo').reset();
			emsStore.baseParams = {'jsonString':Ext.encode({'emsGroupId' : emsGroupId})};
			emsStore.load({
				callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
//					else{
//						var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//						Ext.getCmp('emsCombo').setValue(firstValue);
//					}
				}
			});
		}
	}
});

*//**
 * 创建网管数据源
 *//*
var emsStore = new Ext.data.Store({
	url : 'fault!getAllEmsByEmsGroupId.action',
	baseParams : {'jsonString':Ext.encode({'emsGroupId':'all'})},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['BASE_EMS_CONNECTION_ID','DISPLAY_NAME']
	})
});

*//**
 * 加载网管数据源
 *//*
emsStore.load({
	callback : function(records,options,success){
//		var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//		Ext.getCmp('emsCombo').setValue(firstValue);
	}
});

*//**
 * 创建网管下拉框
 *//*
var emsCombo = new Ext.form.ComboBox({
	id : 'emsCombo',
	fieldLabel : '网管',
	store : emsStore,
	valueField : 'BASE_EMS_CONNECTION_ID',
	displayField : 'DISPLAY_NAME',
	editable : false,
	triggerAction : 'all',
	width :150,
	listeners : {
		select : function(combo, record, index) {
			var emsId = record.get('BASE_EMS_CONNECTION_ID');
			Ext.getCmp('neCombo').reset();
			neStore.baseParams = {'jsonString':Ext.encode({'emsId' : emsId,'neName':Ext.getCmp('neCombo').getValue()})};
			neStore.load({
				callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
//					else{
//						var firstValue = records[0].get('BASE_NE_ID');
//						Ext.getCmp('neCombo').setValue(firstValue);
//					}
				}
			});
		}
	}
});
*//**
 * 创建网元数据源
 *//*
var neStore = new Ext.data.Store({
	url : 'fault!getAllNeByEmsIdAndNename.action',
	baseParams : {'jsonString':Ext.encode({'emsId':'all','neName':''})},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['BASE_NE_ID','DISPLAY_NAME']
	})
});

*//**
 * 加载网元数据源
 *//*
neStore.load({
	callback : function(records,options,success){
//		var firstValue = records[0].get('BASE_NE_ID');
//		Ext.getCmp('neCombo').setValue(firstValue);
	}
});

*//**
 * 创建网元下拉框
 *//* 
var neCombo = new Ext.form.ComboBox({
	id : 'neCombo',
	fieldLabel : '网元',
	store : neStore,
	valueField : 'BASE_NE_ID',
	displayField : 'DISPLAY_NAME',
	listEmptyText : '未找到匹配的结果',
	loadingText : '搜索中...',
	width : 150,
	triggerAction : 'all',
	listeners : {
		beforequery:function(queryEvent){
			var emsId = Ext.getCmp('emsCombo').getValue();
			if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
				queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
				neStore.baseParams = {'jsonString':Ext.encode({'emsId':emsId,'neName' : queryEvent.combo.getRawValue()})};
				neStore.load();
				return false;
			}
		},
		  scope : this
		}
});*/

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
		dataIndex : 'SHIELD_ID',
		width : 200,	
		hidden : true
	},{
		header : '名称',
		dataIndex : 'SHIELD_NAME',
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
				return '禁用';
			}else{
				return '启动';
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
	url : 'fault!getAllAlarmShield.action',
	baseParams : {'jsonString':Ext.encode({'flag':'all'}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["SHIELD_ID","SHIELD_NAME","CREATOR","STATUS","DESCRIPTION"])
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
 * 创建表格查询条件工具栏
 */
var oneTbar = new Ext.Toolbar({
	id : 'oneTbar',
	items : ['-','厂家：',factoryCombo,'-','告警名称：',alarmNameCombo,'-','网管分组：',emsGroupCombo,'-','网管：',emsCombo,'-','网元：',neCombo,'-',{
		text : '查询',
        icon : '../../resource/images/btnImages/search.png',
        privilege:viewAuth,
		handler : function(){
			getAlarmShield();
		}
	}]
});

/**
 * 创建表格实例
 */
var gridPanel = new Ext.grid.EditorGridPanel({
//	title :'告警及事件屏蔽',
	region : 'center',
	store : store,
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	clicksToEdit : 2,
	/*stateId:'alarmShieldId',  
	stateful:true,*/
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-',{
		privilege:addAuth,
        text: '新增',
        icon : '../../resource/images/btnImages/add.png',
        handler : function(){
        	addAlarmShield();
        }
        	
    },{
    	privilege:delAuth,
        text: '删除',
        icon : '../../resource/images/btnImages/delete.png',
        handler : function(){
        	deleteAlarmShield();
        }
    },{
    	privilege:modAuth,
        text: '修改',
        icon : '../../resource/images/btnImages/modify.png',
        handler : function(){
        	modifyAlarmShield();
        }
    },'-',{
    	privilege:viewAuth,
        text: '详情',
        icon : '../../resource/images/btnImages/application.png',
        handler : function(){
        	viewAlarmShield();
        }
    },'-',{
    	privilege:actionAuth,
        text: '启用',
        icon : '../../resource/images/btnImages/control_play.png',
        handler : function(){
        	updateAlarmShieldStatus(1);
        }
    },{
    	privilege:actionAuth,
        text: '禁用',
        icon : '../../resource/images/btnImages/control_stop.png',
        handler : function(){
        	updateAlarmShieldStatus(2);
        }
    },'-',{
    	privilege:viewAuth,
		text : '查询条件',
		id : 'searchCond',
		enableToggle: true,
		icon : '../../resource/images/btnImages/getChecked.gif',
		handler : function() {
			if (!oneTbar.hidden) {
				oneTbar.hide();
				gridPanel.syncSize();
				Ext.getCmp("win").doLayout();
			} else {
				oneTbar.show();
				gridPanel.syncSize();
				Ext.getCmp("win").doLayout();
			}
		}
	}],
	listeners : {
		render : function() {
			oneTbar.render(this.tbar); 
			oneTbar.hide();
		},
		destroy : function() {
			Ext.destroy(oneTbar);
		}
	},
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
 * 新增屏蔽器
 */
function addAlarmShield(){
	var url = 'addAlarmShield.jsp?type=add&alarmIds=';
	var addAlarmShieldWindow = new Ext.Window({
		id : 'addAlarmShieldWindow',
		title : '新增告警及事件屏蔽模板',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='addAlarmShield_panel' name = 'addAlarmShield_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addAlarmShieldWindow.show();
	// 调节高度
	if (addAlarmShieldWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addAlarmShieldWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		addAlarmShieldWindow.setHeight(addAlarmShieldWindow
				.getInnerHeight());
	}
	// 调节宽度
//	if (addAlarmShieldWindow.getWidth() > Ext.getCmp('win').getWidth()) {
//		addAlarmShieldWindow.setWidth(Ext.getCmp('win').getWidth() );
//	} else {
//		addAlarmShieldWindow.setWidth(highQueryWindow
//				.getInnerWidth());
//	}
	addAlarmShieldWindow.center();
}

/**
 * 新增屏蔽器,告警源设置窗口
 */
function addAlarmShieldResourceWindow(){
	var url = 'alarmShieldResource.jsp?type=add';
	var alarmShieldResourceWindow = new Ext.Window({
		id : 'alarmShieldResourceWindow',
		title : '告警源设置',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='alarmShieldResource_panel' name = 'alarmShieldResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	alarmShieldResourceWindow.show();
	// 调节高度
	if (alarmShieldResourceWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		alarmShieldResourceWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		alarmShieldResourceWindow.setHeight(alarmShieldResourceWindow
				.getInnerHeight());
	}
	alarmShieldResourceWindow.center();

}

/**
 * 修改告警屏蔽器
 */
function modifyAlarmShield(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择屏蔽器');
	}else if(records.length==1){
		// 屏蔽器ID
		var shieldId = records[0].get('SHIELD_ID');
		var url = 'addAlarmShield.jsp?type=modify&shieldId='+shieldId;
		var addAlarmShieldWindow = new Ext.Window({
			id : 'addAlarmShieldWindow',
			title : '修改告警屏蔽器',
			width : 820,
			height : 560,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='addAlarmShieldWindow_panel' name = 'addAlarmShieldWindow_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		addAlarmShieldWindow.show();
		// 调节高度
		if (addAlarmShieldWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			addAlarmShieldWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
		} else {
			addAlarmShieldWindow.setHeight(addAlarmShieldWindow
					.getInnerHeight());
		}
		addAlarmShieldWindow.center();
	}else{
		Ext.Msg.alert('提示', '请勿多选');
	}
}

/**
 * 修改屏蔽器,告警源设置窗口
 */
function modifyAlarmShieldResourceWindow(shieldId){
	var url = 'alarmShieldResource.jsp?type=modify&shieldId='+shieldId;
	var alarmShieldResourceWindow = new Ext.Window({
		id : 'alarmShieldResourceWindow',
		title : '告警源选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='alarmShieldResource_panel' name = 'alarmShieldResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	alarmShieldResourceWindow.show();
	// 调节高度
	if (alarmShieldResourceWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		alarmShieldResourceWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		alarmShieldResourceWindow.setHeight(alarmShieldResourceWindow
				.getInnerHeight());
	}
	alarmShieldResourceWindow.center();
}

/**
 * 删除屏蔽器
 */
function deleteAlarmShield(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择模板');
	}else{
		Ext.Msg.confirm('系统提示','删除模板后，无法恢复。是否确认？',function(btn){       
			if(btn=='yes'){
				var shieldIds = '';
				var count = 0;
				for ( var i = 0; i < records.length; i++) {
					shieldIds += records[i].get('SHIELD_ID')+',';
					if(records[i].get('STATUS')==1){
						count++;
					}
				}
				shieldIds = shieldIds.substring(0,shieldIds.lastIndexOf(','))
				if(count==0){
					Ext.Ajax.request({
					    url: 'fault!deleteAlarmShield.action',
					    method: 'POST',
					    params: {'jsonString':Ext.encode({'shieldIds' : shieldIds})},
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
 * 修改屏蔽器状态
 */
function updateAlarmShieldStatus(flag){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择屏蔽器');
	}else {
		for ( var i = 0; i < records.length; i++) {
			var shieldId = records[i].get('SHIELD_ID');
			Ext.Ajax.request({
			    url: 'fault!updateAlarmShieldStatus.action',
			    method: 'POST',
			    params: {'jsonString':Ext.encode({'shieldId':shieldId,'flag' : flag})},
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
 * 查看屏蔽器详情
 */
function viewAlarmShield(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择屏蔽器');
	}else if(records.length==1){
		// 屏蔽器ID
		var shieldId = records[0].get('SHIELD_ID');
		var url = 'viewAlarmShield.jsp?shieldId='+shieldId;
		var viewAlarmShieldWindow = new Ext.Window({
			id : 'viewAlarmShieldWindow',
			title : '查看告警及事件屏蔽模板',
			width : 820,
			height : 560,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='viewAlarmShield_panel' name = 'viewAlarmShield_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		viewAlarmShieldWindow.show();
		// 调节高度
		if (viewAlarmShieldWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			viewAlarmShieldWindow.setHeight(Ext.getCmp('win').getHeight() );
		} else {
			viewAlarmShieldWindow.setHeight(viewAlarmShieldWindow
					.getInnerHeight());
		}
		viewAlarmShieldWindow.center();
	} else{
		Ext.Msg.alert('提示', '请勿多选');
	}
}

/**
 * 查看屏蔽器详情,告警源设置窗口
 */
function viewAlarmShieldResourceWindow(shieldId){
	var url = 'viewAlarmShieldResource.jsp?shieldId='+shieldId;
	var viewAlarmShieldResourceWindow = new Ext.Window({
		id : 'viewAlarmShieldResourceWindow',
		title : '告警源选择',
		width : 820,
		height : 560,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='viewAlarmShieldResource_panel' name = 'viewAlarmShieldResource_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	viewAlarmShieldResourceWindow.show();
}

/**
 * 查询符合条件的屏蔽器
 */
function getAlarmShield(){
	store.baseParams = {'jsonString':Ext.encode({'flag':'condition','factory' : Ext.getCmp('factoryCombo').getValue(),'alarmName':Ext.getCmp('alarmNameCombo').getValue(),
		'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue(),'neId':Ext.getCmp('neCombo').getValue()}),
		'limit':500
	},
	store.load({
		callback : function(records,options,success){
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}
		}
	})
}

function init(){
	if(alarmIds!=''&&alarmIds!='null'){
		var url = 'addAlarmShield.jsp?type=add&alarmIds='+alarmIds+'&tabFlag='+tabFlag;
		var addAlarmShieldWindow = new Ext.Window({
			id : 'addAlarmShieldWindow',
			title : '新增告警及事件屏蔽模板',
			width : 820,
			height : 560,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='addAlarmShield_panel' name = 'addAlarmShield_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		addAlarmShieldWindow.show();
		// 调节高度
		if (addAlarmShieldWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			addAlarmShieldWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
		} else {
			addAlarmShieldWindow.setHeight(addAlarmShieldWindow
					.getInnerHeight());
		}
		// 调节宽度
//		if (addAlarmShieldWindow.getWidth() > Ext.getCmp('win').getWidth()) {
//			addAlarmShieldWindow.setWidth(Ext.getCmp('win').getWidth() );
//		} else {
//			addAlarmShieldWindow.setWidth(highQueryWindow
//					.getInnerWidth());
//		}
		addAlarmShieldWindow.center();
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
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	init();
});