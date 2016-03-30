/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/
Ext.QuickTips.init();
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



var taskStatusCombo = new Ext.form.ComboBox({
	id : 'taskStatusCombo',
	fieldLabel : '任务状态',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '启用' ], [ '2', '挂起' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	width : 130,
	editable : false
//	listeners : {
//		select : function(combo, record, index) {
//			var factory = record.get('value');
//		}
//	}
});

function changeDisplayValue(v){
	var index = taskStatusCombo.getStore().find('value',v);
	return taskStatusCombo.getStore().getAt(index).data.displayName;
//	alert(taskStatusCombo.getStore().getAt(index).data);
}
var syncFlagCombo = new Ext.form.ComboBox({
	id : 'syncFlagCombo',
	fieldLabel : '南向连接恢复后是否自动同步',
	store : new Ext.data.ArrayStore({
		fields : ['value','displayName'],
		data:[ [ '1', '是' ], [ '2', '否' ]]
	}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	width : 130,
	editable : false,
	listeners : {
		select : function(combo, record, index) {
			var factory = record.get('value');
		}
	}
});

function changesyncFlagDisplayValue(v){
	var index = syncFlagCombo.getStore().find('value',v);
	return syncFlagCombo.getStore().getAt(index).data.displayName;
}
var delayTimeCombo = new Ext.form.ComboBox({
	id : 'delayTimeCombo',
	fieldLabel : '任务状态',
	store : new Ext.data.ArrayStore({
		fields : ['value','displayName'],
		data:[ [ '10', '10' ], [ '15', '15' ],[ '20', '20' ],[ '25', '25' ],[ '30', '30' ]]
	}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	width : 130,
	editable : false,
	listeners : {
		select : function(combo, record, index) {
			var factory = record.get('value');
		}
	}
});

function changedelayTimeDisplayValue(v){
	var index = delayTimeCombo.getStore().find('value',v);
	return delayTimeCombo.getStore().getAt(index).data.displayName;
}



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
		header : 'BASE_EMS_CONNECTION_ID',
		dataIndex : 'BASE_EMS_CONNECTION_ID',
		width : 200,	
		hidden : true
	},{
		header : '网管分组',
		dataIndex : 'GROUP_NAME',
		width : 100
	}, {
		header : '网管名称',
		dataIndex : 'EMS_NAME',
		width : 100
	}, {
		header : '网管类型',
		dataIndex : 'TYPE',
		width : 100,
		renderer : function(v){
			for(var type in NMS_TYPE){
				if(v==NMS_TYPE[type]['key']){
					return NMS_TYPE[type]['value'];
				}
			}
			return v;
		}
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>同步周期(小时)</div></div>",
		header : '<span style="font-weight:bold">同步周期(小时)</span>',
		dataIndex : 'SYNCHRONIZATION_CIRCLE',
		width : 100,
		editor : new Ext.form.NumberField({
//			allowBlank: false,
			allowNegative : false,
//			maxLength : 10,
//			maxLengthText : '长度超过限制',
			allowDecimals:false,//不允许输入小数 
			nanText:'请输入有效整数',//无效数字提示
			maxValue:48,//最大值                 
			maxText:'请输入6~48之间的整数',
			minValue:6,//最小值        
			minText:'请输入6~48之间的整数'
		}),
		tooltip:'可编辑列'
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>任务状态</div></div>",
		header : '<span style="font-weight:bold">任务状态</span>',
		dataIndex : 'TASK_STATUS',
		width : 100,
		editor : taskStatusCombo,
		renderer : changeDisplayValue,
		tooltip:'可编辑列'
	}, {
		header : '执行状态',
		dataIndex : 'EXECUTE_STATUS',
		width : 100,
		renderer : function(v){
			if(v=='1'){
				return '成功';
			}else if(v=='2'){
				return '失败';
			}else{
				return '';
			}
		}
	}, {
		header : '上次同步时间',
		dataIndex : 'LATEST_SYNCHRONIZATION_TIME',
		width : 200
	}, {
		header : '下次同步时间',
		dataIndex : 'NEXT_SYNCHRONIZATION_TIME',
		width : 200
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>南向连接恢复后自动同步</div></div>",
		header : '<span style="font-weight:bold">南向连接恢复后自动同步</span>',
		dataIndex : 'SYNCHRONIZATION_FLAG',
		width : 100,
		editor : syncFlagCombo,
		renderer : changesyncFlagDisplayValue,
		tooltip:'可编辑列'
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>延时(分钟)</div></div>",
		header : '<span style="font-weight:bold">延时(分钟)</span>',
		dataIndex : 'DELAY_TIME',
		width : 100,
		editor : delayTimeCombo,
		renderer : changedelayTimeDisplayValue,
		tooltip:'可编辑列'
	}]
});

/**
 * 创建表格数据源
 */
var store = new Ext.data.Store({
	url : 'fault!getAlarmAutoSynchByEmsGroup.action',
	baseParams : {'jsonString':Ext.encode({'emsGroupId':'-99'}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	["ID","BASE_EMS_CONNECTION_ID","GROUP_NAME","EMS_NAME","TYPE","SYNCHRONIZATION_CIRCLE","TASK_STATUS","EXECUTE_STATUS","LATEST_SYNCHRONIZATION_TIME","NEXT_SYNCHRONIZATION_TIME","SYNCHRONIZATION_FLAG","DELAY_TIME"])
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
//	title :'告警自动同步',
	region : 'center',
	store : store,
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	clicksToEdit : 1,
	/*stateId:'alarmAutoSynchId',  
	stateful:true,*/
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-','网管分组：',emsGroupCombo,'-',{
        text: '保存',
        icon : '../../resource/images/btnImages/disk.png',
        privilege:modAuth,
        handler : function(){
        	save();
        }
    }],
    bbar : pageTool
});



/**
 * 保存方法
 */
function save(){
	var modifyRecords = store.getModifiedRecords();
	var json = new Array(); 
	var saflag=true;
	for (var i = 0; i < modifyRecords.length; i++) { 
		// 将修改的对象加入到json中 
		if(!modifyRecords[i].data.SYNCHRONIZATION_CIRCLE&&modifyRecords[i].data.TASK_STATUS=='1'){
			Ext.Msg.alert('错误', '同步周期未设置,请重新设置');
			saflag =false;
			break;
		}else{
			Ext.each(modifyRecords[i], function(item) { 
				json.push(item.data); 
			});
		}
	}
	store.commitChanges();
	if(saflag){
	if(json.length>0){
		Ext.Ajax.request({
			url : 'fault!modifyAlarmAutoSynch.action',
			method : 'POST',
			params : {'jsonString':Ext.encode(json)},
			success : function(response) {// 回调函数
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					Ext.Msg.alert("信息", obj.returnMessage);
					top.Ext.getBody().unmask();
					store.load({
						callback : function(records,options,success){
							if (!success) {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
				}else{
					Ext.Msg.alert("信息", obj.returnMessage);
				}
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert('错误', '保存失败！');
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert('错误', '保存失败！');
			}
		});
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
		parent.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
		layout : 'border',
		items : gridPanel
	});
});