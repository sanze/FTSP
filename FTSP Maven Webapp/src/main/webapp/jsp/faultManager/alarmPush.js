/**
 * 创建告警推送设置tab
 */
var alarmPush = new Ext.Panel({
	title : '告警推送设置',
	region : 'center',
	id : 'alarmPush',
	border : false,
	items : [{
		layout : 'column',
		border : false,
		style : 'margin-top:50px',
		items : [{
			style : 'margin-left:202px;',
			border : false,
			html : '告警产生'
		},{
			style : 'margin-left:100px;',
			border : false,
			html : '告警清除'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-top:20px',
		items : [{
			style : 'margin-left:100px;',
			xtype :"radio",
	        name : 'alarmPushRadio',
	        inputValue : '1,1'
		},{
			style : 'margin-left:80px;',
			border : false,
			html : '每次推送'
		},{
			style : 'margin-left:100px;',
			border : false,
			html : '每次推送'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-top:20px',
		items : [{
			style : 'margin-left:100px;',
			xtype :"radio",
	        name : 'alarmPushRadio',
	        inputValue : '1,2'
		},{
			style : 'margin-left:80px;',
			border : false,
			html : '每次推送'
		},{
			style : 'margin-left:100px;',
			border : false,
			html : '已清除、已确认推送'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-top:20px',
		items : [{
			style : 'margin-left:100px;',
			xtype :"radio",
	        name : 'alarmPushRadio',
	        inputValue : '1,3'
		},{
			style : 'margin-left:80px;',
			border : false,
			html : '每次推送'
		},{
			style : 'margin-left:100px;',
			border : false,
			html : '当前告警转为历史告警推送'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-top:20px',
		items : [{
			style : 'margin-left:100px;',
			xtype :"radio",
	        name : 'alarmPushRadio',
	        inputValue : '2,3'
		},{
			style : 'margin-left:80px;',
			border : false,
			html : '首次推送'
		},{
			style : 'margin-left:100px;',
			border : false,
			html : '当前告警转为历史告警推送'
		}]
	}],
	bbar : new Ext.Toolbar({
		items : ['->',{
			xtype : 'button',
			text:'确定',
			handler : function(){
				saveAlarmPush();
			}
		}]
	})
})

/**
 * 保存告警推送设置
 */
function saveAlarmPush(){
	var value = '';
	var alarmPush = Ext.getCmp('alarmPush').items;
	// 循环从1开始，因为第一行是头部(列名)
	for ( var i = 1; i < alarmPush.length; i++) {
		var alarmPushChild = alarmPush.get(i).items;
		if(alarmPushChild.get(0).checked){
			value = alarmPushChild.get(0).inputValue;
		}
	}
	Ext.Ajax.request({
	    url: 'fault!modifyAlarmPush.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'alarmParam':value})},
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
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
	})
}

/**
 * 初始化告警推送设置tab页
 */
function initAlarmPush(){
	Ext.Ajax.request({
	    url: 'fault!getAlarmPush.action',
	    method: 'POST',
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var alarmPush = Ext.getCmp('alarmPush').items;
	    	// 循环从1开始，因为第一行是头部(列名)
	    	for ( var i = 1; i < alarmPush.length; i++) {
	    		var alarmPushChild = alarmPush.get(i).items;
	    		if(alarmPushChild.get(0).inputValue==obj.PARAM_VALUE){
	    			alarmPushChild.get(0).setValue(true);
	    		}
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
	})
}

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
	initAlarmPush();
	new Ext.Viewport({
		layout : 'border',
		items : alarmPush
	});
});