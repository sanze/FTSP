/**
 * 创建网管分组数据源
 *//*
var emsGroupStore = new Ext.data.Store({
	proxy: new Ext.data.HttpProxy({
		url : 'fault!getAllEmsGroupsNoAll.action', 
		disableCaching: false
	}),
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});

*//**
 * 加载网管分组数据源
 *//*
emsGroupStore.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

*//**
 * 创建网管分组下拉框
 *//*
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : emsGroupStore,
	valueField : 'BASE_EMS_GROUP_ID',
	displayField : 'GROUP_NAME',
	editable : false,
	triggerAction : 'all',
	width :110,
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
				}
			});
		}
	}
});

*//**
 * 创建网管数据源
 *//*
var emsStore = new Ext.data.Store({
	url : 'fault!getAllEmsByEmsGroupIdNoAll.action',
	baseParams : {'jsonString':Ext.encode({'emsGroupId':''})},
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
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
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
	width :110
});
*/
/**
 * 创建告警名称文本框
 */
var alarmName = new Ext.form.TextField({
	id : 'alarmName',
	fieldLabel : '告警名称',
//	allowBlank : false,
//	maxLength:50,                
//	maxLengthText:'最多可输入50个字符',
//	blankText:'不能为空',
//	width : 110
});

/**
 * 创建告警级别下拉框
 */
var alarmLevel = new Ext.form.ComboBox({
	id : 'alarmLevelCombo',
	fieldLabel : '原告警级别',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '紧急' ], [ '2', '重要' ],[ '3', '次要' ], [ '4', '提示' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	editable : false,
	triggerAction : 'all',
	width :80
});

/**
 * 创建告警级别下拉框
 */
var newAlarmLevel = new Ext.form.ComboBox({
	id : 'newAlarmLevelCombo',
	fieldLabel : '新告警级别',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '紧急' ], [ '2', '重要' ],[ '3', '次要' ], [ '4', '提示' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	editable : false,
	triggerAction : 'all',
	width :80
});

/**
 * 创建主体部分
 */
var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : false,
	layout : 'form',
	width:700,
	items : [{
		layout : 'column',
		border : false,
		width:700,
		style : 'margin-left:20px;margin-top:30px',
		items : [{
			border : false,
			columnWidth : .2,
			tag:'span',
 		    html:'网管分组'
		},{
			border : false,
			columnWidth : .2,
			tag:'span',
 		    html:'网管'
		},{
			border : false,
			columnWidth : .26,
			tag:'span',
 		    html:'告警/事件名称'
		},{
			border : false,
			columnWidth : .17,
			tag:'span',
 		    html:'原告警/事件级别'
		},{
			border : false,
			columnWidth : .17,
			tag:'span',
 		    html:'重定义告警级别'
		}]
	},{
		layout : 'column',
		border : false,
		width:700,
		style : 'margin-left:20px;margin-top:20px',
		items : [{
			border : false,
			columnWidth : .2,
			items : emsGroupCombo
		},{
			border : false,
			columnWidth : .2,
			items : emsCombo
		},{
			border : false,
			layout : 'column',
			columnWidth : .26,
			items :[alarmName,{
				border : false,
				tag:'span',
	 		    html:'&nbsp;<span style="color:red">*</span>'
			}]
		},{
			border : false,
			layout : 'column',
			columnWidth : .17,
			items : [alarmLevel,{
				border : false,
				tag:'span',
	 		    html:'&nbsp;<span style="color:red">*</span>'
			}]
		},{
			border : false,
			layout : 'column',
			columnWidth : .17,
			items : [newAlarmLevel,{
				border : false,
				tag:'span',
	 		    html:'&nbsp;<span style="color:red">*</span>'
			}]
		}]
	}],
	buttons: [{
        text: '确定',
        style : 'margin-left:10px;',
        handler : function(){
        	addAlarmRedefine();
        }
    },{
        text: '取消',
        style : 'margin-left:10px;',
        handler : function(){
        	var win = parent.Ext.getCmp('addAlarmRedefineWindow');
			if(win){
				win.close();
			}
        }
    }]
});

/**
 * 新增/修改告警重定义
 */
function addAlarmRedefine(){
	// 网管分组ID
	var emsGroupId = Ext.getCmp('emsGroupCombo').getValue();
	if(emsGroupId==''){
		Ext.Msg.alert('错误', '请选择网管分组');
		return false;
	}
	// 网管ID
	var emsId = Ext.getCmp('emsCombo').getValue();
	if(emsId==''){
		Ext.Msg.alert('错误', '请选择网管');
		return false;
	}
	// 告警/事件名称
	var alarmName = Ext.getCmp('alarmName').getValue();
	if(alarmName==''){
		Ext.Msg.alert('错误', '请输入告警/事件名称');
		return false;
	}
	// 原告警/事件级别
	var alarmLevel = Ext.getCmp('alarmLevelCombo').getValue();
	if(alarmLevel==''){
		Ext.Msg.alert('错误', '请选择原告警/事件级别');
		return false;
	}
	// 重定义告警级别
	var newAlarmLevel = Ext.getCmp('newAlarmLevelCombo').getValue();
	if(newAlarmLevel==''){
		Ext.Msg.alert('错误', '请选择重定义告警级别');
		return false;
	}
	// 请求地址
	var url = '';
	// 请求参数
	var params = '';
	if(type=='add'){
		url = 'fault!addAlarmRedefine.action';
		params = {'jsonString':Ext.encode({'emsId':emsId,'alarmName':alarmName,'alarmLevel':alarmLevel,'newAlarmLevel':newAlarmLevel})};
	}else{
		url = 'fault!modifyAlarmRedefine.action';
		params = {'jsonString':Ext.encode({'emsId':emsId,'alarmName':alarmName,'alarmLevel':alarmLevel,'newAlarmLevel':newAlarmLevel,'redefineId':redefineId})};
	}
	Ext.Ajax.request({
	    url: url,
	    method: 'POST',
	    params: params,
	    success : function(response) {
	    	parent.store.load({
	    		callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
	    	});
	    	var win = parent.Ext.getCmp('addAlarmRedefineWindow');
			if(win){
				win.close();
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

/**
 * 初始化参数
 */
function initData(){
	emsGroupStore.load({
	    callback : function (r, options, success) {
	        if (success) {
	        	if(type=='modify'){
		        	Ext.Ajax.request({
		    		    url: 'fault!getAlarmRedefineById.action',
		    		    method: 'POST',
		    		    params: {'jsonString':Ext.encode({'redefineId':redefineId})},
		    		    success : function(response) {
		    		    	var obj = Ext.decode(response.responseText);
		    		    	// 设置告警/事件名称
		    		    	Ext.getCmp('alarmName').setValue(obj.NATIVE_PROBABLE_CAUSE);
		    		    	// 设置原告警/事件级别
		    		    	Ext.getCmp('alarmLevelCombo').setValue(obj.ALARM_LEVEL);
		    		    	// 设置重定义告警级别
		    		    	Ext.getCmp('newAlarmLevelCombo').setValue(obj.NEW_ALARM_LEVEL);
		    		    	// 设置网管分组
		    		    	Ext.getCmp('emsGroupCombo').setValue(obj.BASE_EMS_GROUP_ID);
		    		    	emsStore.baseParams.emsGroupId = obj.BASE_EMS_GROUP_ID;
		    				emsCombo.enable();
		    				emsStore.load({
		    					callback : function(records,options,success){
		    						if (!success) {
		    							Ext.Msg.alert('错误', '查询失败！请重新查询');
		    						}else{
					    		    	Ext.getCmp('emsCombo').setValue(obj.EMS_ID);
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
		    		});
	        	}
	        } else {
	            Ext.Msg.alert('错误', '网管分组查询失败！请重新查询');
	        }
	    }
	});
} 

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	initDisplayOption(emsStore,false,true);
 	initData();
  	new Ext.Viewport({
        layout : 'border',
        items : centerPanel
	});
 });
