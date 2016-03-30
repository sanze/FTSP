/**
 * 创建主体部分
 */
var centerPanel = new Ext.form.FormPanel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'form',
	items : [{
		layout : 'column',
		style : 'margin-top:30px;',
		border : false,
		items : [{
			xtype : 'checkbox',
			boxLabel : '告警定时确认:',
			style : 'margin-left:15px;',
			inputValue : 1,
			checked : false,
			hidden:true
		},{
			border : false,
		    tag:'span',
		    style : 'margin-left:15px;margin-top:2px',
		    html:'告警定时确认：每'
		},{
			id : 'alarmConfirm',
			layout : 'form',
			border : false,
			xtype : 'numberfield',
			height:18,
			width : 40,
//			emptyText :'1~48',
			allowDecimals : false,//不允许输入小数 
			allowNegative : false,//不允许输入负数
//			allowBlank : false,
//			blankText : '请输入1~48之间的整数',
			maxValue:48,//最大值                 
			maxText:'请输入1~48之间的整数',
			minValue:1,//最小值        
			minText:'请输入1~48之间的整数'
		},{
			border : false,
		    tag:'span',
		    style : 'margin-top:2px',
		    html:'小时自动执行'
		}]
	},{
		border : false,
	    tag:'span',
	    style : 'margin-left:15px;margin-top:30px',
	    html:'说明：按照上述设置的间隔时间，将会把主界面中设置为"定时确认"，同时"清除时间"满足条件的告警进行自动确认。'
	}],
	buttons: [{
        text: '确定',
        style : 'margin-left:10px;',
        handler : function(){
        	antoConfirmSet();
        	var win = parent.Ext.getCmp('autoConfirmSetWindow');
			if(win){
				win.close();
			}
        }
    },{
        text: '取消',
        style : 'margin-left:10px;',
        handler : function(){
        	var win = parent.Ext.getCmp('autoConfirmSetWindow');
			if(win){
				win.close();
			}
        }
    }]
});

/**
 * 定时确认周期设置
 */
function antoConfirmSet(){
	var alarmConfirmShift = Ext.getCmp('centerPanel').items;
	// 告警确认设置
	//var alarmConformStatus = alarmConfirmShift.get(0).items.get(0).checked;
	var alarmConformStatus = true;
	var alarmConfirm = Ext.getCmp('alarmConfirm').getValue();
	// 验证格式
	if(!Ext.getCmp('centerPanel').getForm().isValid()){
		return false;
	}
	Ext.Ajax.request({
	    url: 'fault!modifyAlarmConfirmShift.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'alarmConfirmStatus':alarmConformStatus,'alarmConfirm':alarmConfirm,'flag':'confirm'})},
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
 * 初始化告警自动确认时间设置
 */
function initAlarmConfirm(){
	Ext.Ajax.request({
	    url: 'fault!getAlarmConfirmShift.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'flag':'confirm'})},
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	// 告警自动确认
	    	var alarmConfirm = obj.confirm.PARAM_VALUE.split(',');
	    	if(alarmConfirm[0]=='true'){
	    		Ext.getCmp('centerPanel').items.get(0).items.get(0).setValue(true);
	    	}
	    	Ext.getCmp('alarmConfirm').setValue(alarmConfirm[1]);
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

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	initAlarmConfirm();
  	new Ext.Viewport({
        layout : 'border',
        items : centerPanel
	});
 });
