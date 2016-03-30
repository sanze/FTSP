/**
 * 创建一个formPanel作为border布局的center
 */
var formPanel = new Ext.FormPanel({
	region : 'center',
	defaults :{labelStyle : 'margin-left:0px;margin-top:-3px'},
	border : true,
	height:400,
	autoScroll:true,
	bodyStyle :'padding-left:20px;padding-top:20px',
	items : [
		{
			id : 'alarmName',
			xtype : 'displayfield',
			fieldLabel : '告警名称'
		},{
			id : 'standardAlarmName',
			xtype : 'displayfield',
			fieldLabel : '标准告警名称'
		},{
			id : 'ack',
			xtype : 'displayfield',
			fieldLabel : '确认'
		},{
			id : 'alarmLevel',
			xtype : 'displayfield',
			fieldLabel : '告警级别'
		},{
			id : 'emsGroup',
			xtype : 'displayfield',
			fieldLabel : '网管分组'
		},{
			id : 'ems',
			xtype : 'displayfield',
			fieldLabel : '网管'
		},{
			id : 'ne',
			xtype : 'displayfield',
			fieldLabel : '网元'
		},{
			id : 'neModel',
			xtype : 'displayfield',
			fieldLabel : '网元型号'
		},{
			id : 'slot',
			xtype : 'displayfield',
			fieldLabel : '槽道'
		},{
			id : 'unit',
			xtype : 'displayfield',
			fieldLabel : '板卡'
		},{
			id : 'port',
			xtype : 'displayfield',
			fieldLabel : '端口'
		},{
			id : 'serviceType',
			xtype : 'displayfield',
			fieldLabel : '业务类型'
		},{
			id : 'portType',
			xtype : 'displayfield',
			fieldLabel : '端口类型'
		},{
			id : 'rate',
			xtype : 'displayfield',
			fieldLabel : '速率'
		},{
			id : 'path',
			xtype : 'displayfield',
			fieldLabel : '通道'
		},{
			id : 'locationInfo',
			xtype : 'displayfield',
			fieldLabel : '原始定位信息'
		},{
			id : 'location',
			xtype : 'displayfield',
			fieldLabel : '位置'
		},{
			id : 'direction',
			xtype : 'displayfield',
			fieldLabel : '方向'
		},{
			id : 'serviceAffect',
			xtype : 'displayfield',
			fieldLabel : '业务影响'
		},{
			id : 'firstTime',
			xtype : 'displayfield',
			fieldLabel : '首次发生时间'
		},{
			id : 'count',
			xtype : 'displayfield',
			fieldLabel : '频次'
		},{
			id : 'updateTime',
			xtype : 'displayfield',
			fieldLabel : '最近发生时间'
		},{
			id : 'clearTime',
			xtype : 'displayfield',
			fieldLabel : '清除时间'
		},{
			id : 'duration',
			xtype : 'displayfield',
			fieldLabel : '持续时间'
		},{
			id : 'recentDuration',
			xtype : 'displayfield',
			fieldLabel : '最近持续时间'
		},{			
			id : 'ackTime',
			xtype : 'displayfield',
			fieldLabel : '确认时间'
		},{
			id : 'ackUser',
			xtype : 'displayfield',
			fieldLabel : '确认者'
		},{
			id : 'emsTime',
			xtype : 'displayfield',
			fieldLabel : '网管发送时间'
		},{
			id : 'createTime',
			xtype : 'displayfield',
			fieldLabel : '告警入库时间'
		},{
			id : 'passTime',
			xtype : 'displayfield',
			fieldLabel : '入库用时'
		},
		{xtype: 'tbspacer', height:10,shadow:false},
		{
			id : 'alarmType',
			xtype : 'displayfield',
			fieldLabel : '告警类型'
		},{
			id : 'alarmReason',
			xtype : 'displayfield',
			fieldLabel : '告警描述'
		},{
			id : 'handlingSuggestion',
			xtype : 'displayfield',
			fieldLabel : '处理建议'
		},
		{xtype: 'tbspacer', height:10,shadow:false},
		{
			id : 'area',
			xtype : 'displayfield',
			fieldLabel : top.FieldNameDefine.AREA_NAME
		},{
			id : 'station',
			xtype : 'displayfield',
			fieldLabel : top.FieldNameDefine.STATION_NAME
		},{
			id : 'engine',
			xtype : 'displayfield',
			fieldLabel : '机房'
		},{
			id : 'charterPerson',
			xtype : 'displayfield',
			fieldLabel : '包机人'
		}],
	buttons:[{
		text:'确定',
		handler: function(){
		    //关闭告警详情窗口
			var win = parent.Ext.getCmp('addWindow');
			if(win){
				win.close();
			}
		}
	}]
});

//修改按钮传递网管连接Id，将相应信息显示在修改页面上
function initData(alarmId,type){
	Ext.Ajax.request({
	    url: 'fault!getAlarmDetail.action',
	    method : 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'alarmId':alarmId,'type':type})// 把对象转成JSON格式字符串
	    },
	    success: function(response) {
	    	// 将json格式的字符串，转换成对象
	    	var obj = Ext.decode(response.responseText);
	    	Ext.getCmp('alarmName').setValue(obj.NATIVE_PROBABLE_CAUSE);// 告警名称
	    	Ext.getCmp('standardAlarmName').setValue(obj.PROBABLE_CAUSE);// 标准告警名称
	    	var ack=obj.IS_ACK;
	    	var ackStr='';
	    	if(ack==1){
	    		ackStr='确认';
			}else if(ack==2){
				ackStr='';
			}
	    	Ext.getCmp('ack').setValue(ackStr);// 确认
	    	var value=obj.PERCEIVED_SEVERITY;
	    	var alarmLevel='';
	    	if(value==1){
	    		alarmLevel='紧急告警';
			}else if(value==2){
				alarmLevel='重要告警';
			}else if(value==3){
				alarmLevel='次要告警';
			}else if(value==4){
				alarmLevel='提示';
			}
	    	Ext.getCmp('alarmLevel').setValue(alarmLevel);// 告警级别
	    	Ext.getCmp('emsGroup').setValue(obj.EMS_GROUP_NAME);// 网管分组
	    	Ext.getCmp('ems').setValue(obj.EMS_NAME);// 网管
	    	Ext.getCmp('ne').setValue(obj.NE_NAME);// 网元
	    	Ext.getCmp('neModel').setValue(obj.PRODUCT_NAME);// 网元型号
	    	Ext.getCmp('slot').setValue(obj.SLOT_DISPLAY_NAME);// 槽道
	    	Ext.getCmp('unit').setValue(obj.UNIT_NAME);// 板卡
	    	Ext.getCmp('port').setValue(obj.PORT_NAME);// 端口
	    	var domain = obj.DOMAIN;
	    	var domainStr = '';
	    	if(domain==1){
	    		domainStr='SDH';
	    	}else if(domain==2){
	    		domainStr='WDM';
	    	}else if(domain==3){
	    		domainStr='ETH';
	    	}else if(domain==4){
	    		domainStr='ATM';
	    	}
	    	Ext.getCmp('serviceType').setValue(domainStr);// 业务类型
	    	Ext.getCmp('portType').setValue(obj.PTP_TYPE);// 端口类型
	    	Ext.getCmp('rate').setValue(obj.INTERFACE_RATE);// 速率
	    	Ext.getCmp('path').setValue(obj.CTP_NAME);// 通道
	    	Ext.getCmp('locationInfo').setValue(obj.LOCATION_INFO);// 定位信息
	    	Ext.getCmp('location').setValue(obj.LOCATION);// 位置
	    	Ext.getCmp('direction').setValue(obj.DIRECTION);// 方向
	    	var serviceAffect=obj.SERVICE_AFFECTING;
	    	var srvAffectStr='';
	    	if(serviceAffect==1){
	    		srvAffectStr='影响';
	    	}else if(serviceAffect==2){
	    		srvAffectStr='不影响';
	    	}else if(value==0){
				srvAffectStr='未知';
	    	}
	    	Ext.getCmp('serviceAffect').setValue(srvAffectStr);// 业务影响
	    	Ext.getCmp('firstTime').setValue(obj.FIRST_TIME);// 首次发生时间
	    	Ext.getCmp('count').setValue(obj.AMOUNT);// 频次
	    	Ext.getCmp('updateTime').setValue(obj.NE_TIME);// 最近发生时间
	    	Ext.getCmp('clearTime').setValue(obj.CLEAR_TIME);// 清除时间
	    	Ext.getCmp('duration').setValue(obj.DURATION);// 持续时间
	    	Ext.getCmp('recentDuration').setValue(obj.RECENT_DURATION);// 最近持续时间
	    	Ext.getCmp('ackTime').setValue(obj.ACK_TIME);// 确认时间
	    	Ext.getCmp('ackUser').setValue(obj.ACK_USER);// 确认者
	    	Ext.getCmp('emsTime').setValue(obj.EMS_TIME);// 网管发送时间
	    	Ext.getCmp('createTime').setValue(obj.CREATE_TIME);// 告警入库时间
	    	Ext.getCmp('passTime').setValue(obj.PASS_TIME);// 入库用时
	    	
	    	var alarmType=obj.ALARM_TYPE;
	    	var alarmTypeStr='';
	    	if(alarmType==0){
	    		alarmTypeStr='通信';
			}else if(alarmType==1){
				alarmTypeStr='服务';
			}else if(alarmType==2){
				alarmTypeStr='设备';
			}else if(alarmType==3){
				alarmTypeStr='处理';
			}else if(alarmType==4){
				alarmTypeStr='环境';
			}else if(alarmType==5){
				alarmTypeStr='安全';
			}else if(alarmType==6){
				alarmTypeStr='连接';
			}
	    	Ext.getCmp('alarmType').setValue(alarmTypeStr);// 告警类型
	    	Ext.getCmp('alarmReason').setValue(obj.ALARM_REASON);// 告警描述
	    	var suggestion=obj.HANDLING_SUGGESTION;
	    	Ext.getCmp('handlingSuggestion').setValue(suggestion);
//	    	if(suggestion==undefined || suggestion==null || suggestion==''){
//	    		Ext.getCmp('handlingSuggestion').setValue('');
//	    	}else{
//	    		suggestion=suggestion.substring(2);
//	    		Ext.getCmp('handlingSuggestion').setValue(suggestion.replace(/\\\\/g,'<br/>').replace(/(^\s*)|(\s*$)/g,''));// 处理建议
//	    	}
	    	
	    	Ext.getCmp('area').setValue(obj.DISPLAY_AREA);// 区域
	    	Ext.getCmp('station').setValue(obj.DISPLAY_STATION);// 局站
	    	Ext.getCmp('engine').setValue(obj.RESOURCE_ROOM);// 机房
	    	Ext.getCmp('charterPerson').setValue(obj.INSPECT_ENGINEER);// 包机人
    	},
	    error:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 
}


Ext.onReady(function(){
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	initData(alarmId,type);
  	new Ext.Viewport({
        layout : 'border',
		items : formPanel
	});
 });