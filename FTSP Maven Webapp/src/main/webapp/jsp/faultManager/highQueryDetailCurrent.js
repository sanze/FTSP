/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/

//告警数据背景颜色  
var PS_CRITICAL_IMAGE = '';
var PS_MAJOR_IMAGE = '';
var PS_MINOR_IMAGE = '';
var PS_WARNING_IMAGE = '';
var PS_CLEARED_IMAGE = '';
// 定义告警字体颜色
var PS_CRITICAL_FONT = '';
var PS_MAJOR_FONT = '';
var PS_MINOR_FONT = '';
var PS_WARNING_FONT = '';
var PS_CLEARED_FONT = '';
/**
 * 创建表格选择模型(多选、单选.....)
 */ 
//var sm = new Ext.ux.grid.LockingCheckboxSelectionModel();
//sm.sortLock();
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : false
});
var highParam = '';
var url = location.search;
//获取权限参数
if (url.indexOf("?") != -1) {
	var str = url.substr(1);
	strs = str.split("&");
	for ( var i = 0; i < strs.length; i++) {
		if (strs[i].split("=")[0] == "highParam") {
			highParam = strs[i].split("=")[1];
			break;
		}
	}
}
highParam = decodeURIComponent(highParam);
var toobar = (function(){
	//解析参数
	var queryParam = Ext.decode(highParam);
	//判断是否当前告警
	var isCurrentAlarm = queryParam.type == "current";
	//alert(highParam);
	//console.log(isCurrentAlarm);
	var tb = new Ext.Toolbar({
		items : [{
	        text: '确认',
	        icon : '../../resource/images/btnImages/tick.png',
	        privilege : modAuth,
	        handler : function (){
	        	manualConfirm();
	        }
	    },{
	        text: '反确认',
	        icon : '../../resource/images/btnImages/arrow_undo.png',
	        privilege : modAuth,
	        handler : function (){
	        	antiConfirm();
	        }
	    },'-',{
	        text: '派单',
	        icon : '../../resource/images/btnImages/user_suit.png',
	        //privilege : modAuth, 
	        disabled:true
	    },'-',{
	        text: '反转',
//	        icon : '../../resource/images/btnImages/arrow_undo.png',
	        privilege : modAuth,
	        handler : function (){
	        	reversal();
	        }
	    },{
	        text: '取消反转',
//	        icon : '../../resource/images/btnImages/arrow_undo.png',
	        privilege : modAuth,
	        handler : function (){
	        	antiReversal();
	        }
	    },'-',{
	        text: '详情',
	        icon : '../../resource/images/btnImages/information.png',
	        privilege : viewAuth,
	        handler : function (){
	        	queryAlarmDetail();
	        }
	    },'-',{
	        text: '相关电路', 
	        privilege : viewAuth,
	        handler : function(){
	        	gotoCircuit();
	        }
	    },'-',{
			text : "导出",
			icon : '../../resource/images/btnImages/export.png',
			privilege : viewAuth,
			handler : function() {
				exportCurrentAlarmData();
			}
		}]
	});
	return tb;
})();
/**
 * 创建数告警列表列模型
 */
var cm = new Ext.ux.grid.LockingColumnModel({
//var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), sm, 
	{
		header : '告警数据ID',
		dataIndex : '_id',
		width : 80,
		hidden : true
	},{
		header : '告警类型',
		dataIndex : 'OBJECT_TYPE',
		width : 80,
		hidden : true
	},{
		header : '端口ID',
		dataIndex : 'PTP_ID',
		width : 80,
		hidden : true
	},{
		header : '网元ID',
		dataIndex : 'NE_ID',
		width : 80,
		hidden : true
	},{
		header : '板卡ID',
		dataIndex : 'UNIT_ID',
		width : 80,
		hidden : true
	},{
		header : '链路ID',
		dataIndex : 'BASE_LINK_ID',
		width : 80,
		hidden : true
	},{
		header : '保护组ID',
		dataIndex : 'BASE_PRO_GROUP_ID',
		width : 80,
		hidden : true
	},{
		header : '子架号',
		dataIndex : 'SHELF_NO',
		width : 30,
		hidden : true
	},{
		header : '确认',
		dataIndex : 'IS_ACK',
		width : 50,
		renderer : function(value){
			if(value==1){
				return '确认';
			}else if(value==2){
				return '';
			}
		},
		locked : true
	},{
		header : '告警级别',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 80,
		renderer : function(value,meta,record){
			if(record.data["IS_CLEAR"]==2){
				if(value==1){
					meta.css= PS_CRITICAL_IMAGE;
					return '<font color='+PS_CRITICAL_FONT+'>紧急</font>';
				}else if(value==2){
					meta.css= PS_MAJOR_IMAGE;
					return '<font color='+PS_MAJOR_FONT+'>重要</font>';
				}else if(value==3){
					meta.css= PS_MINOR_IMAGE;
					return '<font color='+PS_MINOR_FONT+'>次要</font>';
				}else if(value==4){
					meta.css= PS_WARNING_IMAGE;
					return '<font color='+PS_WARNING_FONT+'>提示</font>';
				}
			}else{
				meta.css= PS_CLEARED_IMAGE;
				if(value==1){
					return '<font color='+PS_CLEARED_FONT+'>紧急</font>';
				}else if(value==2){
					return '<font color='+PS_CLEARED_FONT+'>重要</font>';
				}else if(value==3){
					return '<font color='+PS_CLEARED_FONT+'>次要</font>';
				}else if(value==4){
					return '<font color='+PS_CLEARED_FONT+'>提示</font>';
				}
			}
		},
		locked : true
	},{
		header : '告警名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100,
		locked : true
	},{
		header : '归一化名称',
		dataIndex : 'NORMAL_CAUSE',
		width : 100,
		locked : true
	},{
		header : '网管分组',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	},{
		header : '网管',
		dataIndex : 'EMS_NAME',
		width : 100
	},{
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	},{
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 80
	},{
		header : '槽道',
		dataIndex : 'SLOT_DISPLAY_NAME',
		width : 100
	},{
		header : '板卡',
		dataIndex : 'UNIT_NAME',
		width : 100
	},{
		header : '端口',
		dataIndex : 'PORT_NAME',
		width : 80
	},{
		header : '业务类型',
		dataIndex : 'DOMAIN',
		width : 60,
		renderer : function(value){
			if(value==1){
				return 'SDH';
			}else if(value==2){
				return 'WDM';
			}else if(value==3){
				return 'ETH';
			}else if(value==4){
				return 'ATM';
			}
		}
	},{
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		width : 80
	},{
		header : '速率',
		dataIndex : 'INTERFACE_RATE',
		width : 80
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		width : 100
	},{
		header : '清除状态',
		dataIndex : 'IS_CLEAR',
		width : 80,
		renderer : function(value){
			if(value==1){
				return '已清除';
			}else{
				return '未清除';
			}
		}
	},{
		header : '首次发生时间',
		dataIndex : 'FIRST_TIME',
		width : 100
	},{
		header : '频次',
		dataIndex : 'AMOUNT',
		width : 40
	},{
		header : '最近发生时间',
		dataIndex : 'NE_TIME',
		width : 100
	},{
		header : '业务影响',
		dataIndex : 'SERVICE_AFFECTING',
		width : 60,
		renderer : function(value){
			if(value==1){
				return '影响';
			}else if(value==2){
				return '不影响';
			}else if(value==0){
				return '未知';
			}
		}
	},{
		header : '清除时间',
		dataIndex : 'CLEAR_TIME',
		width : 100
	},{
		header : '持续时间',
		dataIndex : 'DURATION',
		width : 100
	},{
		header : '确认时间',
		dataIndex : 'ACK_TIME',
		width : 100
	},{
		header : '确认者',
		dataIndex : 'ACK_USER',
		width : 80
	},{
		header : '告警类型',
		dataIndex : 'ALARM_TYPE',
		width : 60,
		renderer : function(value){
			if(value==0){
				return '通信';
			}else if(value==1){
				return '服务';
			}else if(value==2){
				return '设备';
			}else if(value==3){
				return '处理';
			}else if(value==4){
				return '环境';
			}else if(value==5){
				return '安全';
			}else if(value==6){
				return '连接';
			}
		}
	},{
		header : '告警反转',
		dataIndex : 'REVERSAL',
		width : 60,
		renderer : function(value){
			if(value==true){
				return '反转';
			}else if(value==false){
				return '未反转';
			}
		}
	}]
});

/**
 * 创建当前告警列表数据源
 */ 
var store = new Ext.data.Store({
	url : 'fault!getAlarms_High.action',
	baseParams : {
		'jsonString':highParam,
		'limit':500
	},
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : 'rows'
			}, 
			['_id','OBJECT_TYPE','PTP_ID','NE_ID','UNIT_ID','BASE_LINK_ID','BASE_PRO_GROUP_ID','SHELF_NO','CTP_ID',
			 'IS_ACK','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','EMS_NAME','NE_NAME',
			 'PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NAME','DOMAIN','PTP_TYPE','INTERFACE_RATE','CTP_NAME',
			 'IS_CLEAR','FIRST_TIME','AMOUNT','NE_TIME','SERVICE_AFFECTING','CLEAR_TIME','DURATION','ACK_TIME','ACK_USER',
			 'ALARM_TYPE','PROBABLE_CAUSE','REVERSAL'])
});

/**
 * 加载数据
 */
store.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

/**
 * 创建表格分页工具栏
 */
var pageTool = new Ext.PagingToolbar({
	pageSize : 500,
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});

/**
 * 创建一个可编辑的表格
 */
//var editGridPanel = new Ext.grid.EditorGridPanel({
var editGridPanel = new Ext.grid.GridPanel({
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm, 
	stripeRows : true, 
	clicksToEdit : 2,
	forceFit : true,
	loadMask : true,
	tbar:toobar,
	stateId:'highQueryDetailCurrentId',  
	stateful:true,
	view : new Ext.ux.grid.LockingGridView({
		scrollDelay:false,
		cacheSize:100,
		syncHeights: false,
		cleanDelay:500//,
		//autoScrollTop:false
	}),
	bbar : pageTool
});

/**
 * 获取告警背景颜色
 * @param c
 * @returns {String}
 */
function getBackgroundFont(c){
	if(c=='#ff0000'){
		return 'alarm-color1';
	}else if(c=='#ff8000'){
		return 'alarm-color2';
	}else if(c=='#ffff00'){
		return 'alarm-color3';
	}else if(c=='#800000'){
		return 'alarm-color4';
	}else if(c=='#00ff00'){
		return 'alarm-color5';
	}
}

/**
 * 初始化告警颜色
 */
function init(){
	Ext.Ajax.request({
		url: 'fault!getAlarmColorSet.action',
	    method: 'POST',
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	// 告警数据背景颜色  
	    	PS_CRITICAL_IMAGE = getBackgroundFont(obj.PS_CRITICAL_IMAGE);
	    	PS_MAJOR_IMAGE = getBackgroundFont(obj.PS_MAJOR_IMAGE);
	    	PS_MINOR_IMAGE = getBackgroundFont(obj.PS_MINOR_IMAGE);
	    	PS_WARNING_IMAGE = getBackgroundFont(obj.PS_WARNING_IMAGE);
	    	PS_CLEARED_IMAGE = getBackgroundFont(obj.PS_CLEARED_IMAGE);
	    	// 定义告警字体颜色
	    	PS_CRITICAL_FONT = obj.PS_CRITICAL_FONT;
	    	PS_MAJOR_FONT = obj.PS_MAJOR_FONT;
	    	PS_MINOR_FONT = obj.PS_MINOR_FONT;
	    	PS_WARNING_FONT = obj.PS_WARNING_FONT;
	    	PS_CLEARED_FONT = obj.PS_CLEARED_FONT;
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

/**
 * Ext初始化
 */
Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = '../../resource/ext/resources/images/default/s.gif';
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	init();
	new Ext.Viewport({
		id : 'win',
		layout : 'border',
		border : false,
		items : editGridPanel
	});
});

//********************高级查询功能扩展**********************
/**
 * 告警手动确认
 */
function manualConfirm(){
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length>=1){
		Ext.Msg.confirm('提示','你确定要确认所选告警？',function(btn){       
			if(btn=='yes'){
				var ids = [];
				for ( var i = 0; i < records.length; i++) {
					 // 如果未确认
					if(records[i].get('IS_ACK')==2){
						ids.push(records[i].get('_id'));
					}
				}
				ids = ids.join(",");
				if(ids!=''){
					Ext.Ajax.request({
					    url: 'fault!alarmManualConfirm.action',
					    method: 'POST',
					    params: {
					    	'jsonString' : Ext.encode({'ids':ids})// 把对象转成JSON格式字符串
					    },
					    success: function(response) {
//					    	queryCurrentAlarm();
					    	store.reload();
					    },
					    error:function(response) {
				        	Ext.Msg.alert("错误",response.responseText);
					    },
					    failure:function(response) {
				        	Ext.Msg.alert("错误",response.responseText);
					    }
					});
				}
			}              
		},this);
	}else{
		Ext.Msg.alert('提示', '请选择告警');
	}
}
/**
 * 告警反确认
 */
function antiConfirm(){
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length>=1){
		Ext.Msg.confirm('提示','反确认操作,将会把告警恢复到未确认状态。是否确定？',function(btn){       
			if(btn=='yes'){
				var ids = [];
				for ( var i = 0; i < records.length; i++) {
					 // 如果已确认
					if(records[i].get('IS_ACK')==1){
						ids.push(records[i].get('_id'));
					}
				}
				ids = ids.join(",");
				if(ids!=''){
					Ext.Ajax.request({
					    url: 'fault!alarmAntiConfirm.action',
					    method: 'POST',
					    params: {
					    	'jsonString' : Ext.encode({'ids':ids})// 把对象转成JSON格式字符串
					    },
					    success: function(response) {
//					    	queryCurrentAlarm();
					    	store.reload();
					    },
					    error:function(response) {
				        	Ext.Msg.alert("错误",response.responseText);
					    },
					    failure:function(response) {
				        	Ext.Msg.alert("错误",response.responseText);
					    }
					});
				}
			}              
		},this);
	}else{
		Ext.Msg.alert('提示', '请选择告警');
	}
}

/**
 * 告警反转
 */
function reversal(){
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length>=1){
		Ext.Msg.confirm('提示','告警反转操作，将会抑制告警显示，直到该告警状态改变，是否确定？',function(btn){       
			if(btn=='yes'){
				var ids = '';
				for ( var i = 0; i < records.length; i++) {
					 // 如果未清除
					if(records[i].get('IS_CLEAR')==2){
						ids += records[i].get('_id') + ',';
					}
				}
				ids = ids.substring(0, ids.lastIndexOf(','));
				if(ids!=''){
					Ext.Ajax.request({
					    url: 'fault!alarmReversal.action',
					    method: 'POST',
					    params: {
					    	'jsonString' : Ext.encode({'ids':ids})// 把对象转成JSON格式字符串
					    },
					    success: function(response) {
					    	store.reload();
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
			}              
		},this);
	}else{
		Ext.Msg.alert('提示', '请选择告警');
	}	
}

/**
 * 取消告警反转
 */
function antiReversal(){
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length>=1){
		Ext.Msg.confirm('提示','取消告警反转操作，将会撤销告警显示抑制，是否确定？',function(btn){       
			if(btn=='yes'){
				var ids = '';
				for ( var i = 0; i < records.length; i++) {
					 // 如果未清除
//					if(records[i].get('REVERSAL')==true){
						ids += records[i].get('_id') + ',';
//					}
				}
				ids = ids.substring(0, ids.lastIndexOf(','));
				if(ids!=''){
					Ext.Ajax.request({
					    url: 'fault!antiAlarmReversal.action',
					    method: 'POST',
					    params: {
					    	'jsonString' : Ext.encode({'ids':ids})// 把对象转成JSON格式字符串
					    },
					    success: function(response) {
					    	store.reload();
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
			}              
		},this);
	}else{
		Ext.Msg.alert('提示', '请选择告警');
	}	
}

/**
 * 查看当前告警详情
 */
function queryAlarmDetail() {
	// 获取选择的记录
	var record = editGridPanel.getSelectionModel().getSelections();
	if (record.length > 0 && record.length < 2) {// 如果选择一条记录
		// 告警ID
		var alarmId = record[0].get('_id');
		// 跳转地址
		var url = 'alarmDetail.jsp?alarmId='+alarmId+'&type=current';
		var alarmDetailWindow = new Ext.Window({
			id : 'addWindow',
			title : '告警详情',
			width : 450,
			height : 560,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='alarmDetail_panel' name = 'alarmDetail_panel'  src = " + url
					+ " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		alarmDetailWindow.show();
		if (alarmDetailWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			alarmDetailWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
		} else {
			alarmDetailWindow.setHeight(alarmDetailWindow.getInnerHeight());
		}
		alarmDetailWindow.center();
	} else{
		Ext.Msg.alert('提示', '请选择需要查看的告警，每次选择一条！');
	}
}
/**
 * 相关电路
 * @returns {Boolean}
 */
function gotoCircuit(){
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length==1){
		var i=0;
		var serviceType;
		var nodeLevel;
		var nodes;
		var domain = records[i].get('DOMAIN');
		if(domain==1){
			serviceType=1;
		}else if(domain==2){
			serviceType=3;
		}else if(domain==3){
			serviceType=2;
		}else{
			Ext.Msg.alert('提示', '无相关电路信息');
			return false;
		}
		var objType =records[i].get('OBJECT_TYPE');
		if(objType==1){//网元
			nodeLevel=1;
			nodes=records[i].get('NE_ID');
		}else if(objType==5){//PTP
			nodeLevel=8;
			nodes=records[i].get('PTP_ID');
		}else if(objType==6){//CTP
			nodeLevel=9;
			nodes=records[i].get('CTP_ID');
		}else if(objType==9){//设备(板卡)
			nodeLevel=7;
			nodes=records[i].get('UNIT_ID');
		}else if(objType==3){//链路
			nodeLevel=3;//电路侧需转换
			nodes=records[i].get('BASE_LINK_ID');
		}else if(objType==10){//保护组
			nodeLevel=10;
			nodes=records[i].get('BASE_PRO_GROUP_ID');
		}else{
			Ext.Msg.alert('提示', '无相关电路信息');
			return false;
		}
		if(nodes!=''){
			parent.parent.addTabPage('../../jsp/circuitManager/selectCircuitPortReasult.jsp?serviceType='+serviceType+'&nodeLevel='+nodeLevel+'&nodes='+nodes+'&flag=7', "相关性查询",authSequence );  		
		}
	}else if(records.length<1){
		Ext.Msg.alert('提示', '请选择告警');
	}else{
		Ext.Msg.alert('提示', '请勿多选');
	}

}
//获取隐藏的所有列
function getGridHiddenColomn(){
	var count=cm.getColumnCount();
	var columns='';
	for(var i=0;i<count;i++){
		var t=cm.isHidden(i);
		if(t){
			columns+=cm.getDataIndex(i);
				columns+=',';
		}
	}
	columns=columns.substring(0,columns.length-1);
	return columns;
}
//导出 当前告警数据
function exportCurrentAlarmData(){
	var hiddenColoumms=getGridHiddenColomn();
	var p = Ext.decode(highParam);
	var params = {// 请求参数
		'jsonString' : Ext.encode({
			'nodeIds':p.nodeIds,'nodeLevels':p.nodeLevels,'alarmSource':p.alarmSource,'alarmLevel':p.alarmLevel,
			'confirmStatus':p.confirmStatus,'clearStatus':p.clearStatus,'factory':p.factory,'alarmName':p.alarmName,
			'alarmType':p.alarmType,'firstOneStatus':p.firstOneStatus,'firstTwoStatus':p.firstTwoStatus,
			'firstThreeStatus':p.firstThreeStatus,'firstStart':p.firstStart,'firstEnd':p.firstEnd,'firstDay':p.firstDay,
			'firstHour':p.firstHour,'firstMinute':p.firstMinute,'clearOneStatus':p.clearOneStatus,'clearTwoStatus':p.clearTwoStatus,
			'clearStart':p.clearStart,'clearEnd':p.clearEnd,'continueTimeStatus':p.continueTimeStatus,
			'continueTimeRange':p.continueTimeRange,'continueDay':p.continueDay,'continueHour':p.continueHour,
			'continueMinute':p.continueMinute,'frequencyStatus':p.frequencyStatus,'frequencyRange':p.frequencyRange,
			'frequencyCount':p.frequencyCount,'affectType':p.affectType,'type':p.type,'almReversal':p.almReversal,
			'hiddenColoumms':hiddenColoumms}),// 把对象转成JSON格式字符串
		'limit':5000// 每页显示多少条数据
	};
	window.location.href=  "fault!downloadDetailAlarmResult.action?" + Ext.urlEncode(params);
}