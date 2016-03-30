/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/
//Ext.override( Ext.ux.grid.LockingGridView,{
//    scrollTop:function() {
//         this.scroller.dom.scrollTop =0;
//         this.scroller.dom.scrollLeft =0;
//   },
//   scrollToTop:Ext.emptyFn
//});

var pageSize=500;

/**
 * @@@分权分域到网元@@@
 */
var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		checkModel : "multiple",
		leafType : 4
	};

//共通树url
var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
var westPanel = new Ext.Panel({
	id : "westPanel",
	region : "west",
	width : 280,
	height : 800,
	minSize : 230,
//	maxSize : 320,
	autoScroll : true,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="tree_panel" name = "tree_panel" src =' + treeurl
			+ ' height="100%" width="100%" frameBorder=0 border=0/>'
});

/**
 * 定义高级查询窗口的初始化参数
 */
// 告警源选择(左侧树)
//var treeCheck = '';
// 高级查询参数
var highParam = '';
// 告警源过滤条件
var alarmSource = 'all';
// 告警级别
var alarmLevelCheck = '';
// 确认状态
var confirmStatusCheck = '';
// 清除状态
var clearStatusCheck = '';
// 告警名称->厂家
var factoryValue = -1;
// 告警名称->告警名称
var alarmName = '';
// 告警类型
var alarmTypeCheck = '';
// 首次发生时间
var firstOneStatus = false;
var firstTwoStatus = true;
var firstThreeStatus = false;
var firstStart = '';
var firstEnd = '';
var firstDay = '';
var firstHour = '';
var firstMinute = '';
// 清除时间
var clearOneStatus = true;
var clearTwoStatus = false;
var clearStart = '';
var clearEnd = '';
// 持续时间
var continueTimeStatus = false;
var continueTimeRange = 1;
var continueDay = '';
var continueHour = '';
var continueMinute = '';
// 频次
var frequencyStatus = false;
var frequencyRange = 1;
var frequencyCount = '';
// 业务影响
var affectTypeCheck = '';
//告警反转
var reversalCheck = '';
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
//对象树最大选择数
var maxSelectNum = 50;
/**
 * 声明日期转换格式
 */
Date.prototype.format =function(format)
{
var o = {
"M+" : this.getMonth()+1, //month
"d+" : this.getDate(), //day
"H+" : this.getHours(), //hour
"m+" : this.getMinutes(), //minute
"s+" : this.getSeconds(), //second
"q+" : Math.floor((this.getMonth()+3)/3), //quarter
"S" : this.getMilliseconds() //millisecond
};
if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
(this.getFullYear()+"").substr(4- RegExp.$1.length));
for(var k in o)if(new RegExp("("+ k +")").test(format))
format = format.replace(RegExp.$1,
RegExp.$1.length==1? o[k] :
("00"+ o[k]).substr((""+ o[k]).length));
return format;
};

/**
 * 创建表格选择模型(多选、单选.....)
 */ 
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
//sm.sortLock();
//var sm = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});

/**
 * 创建表格列模型
 */
var cm = new Ext.ux.grid.LockingColumnModel({
//var  cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : true
	}), sm,{
		header : '告警数据ID',
		dataIndex : '_id',
		width : 100,
		hidden : true
	},{
		header : '告警类型',
		dataIndex : 'OBJECT_TYPE',
		width : 100,
		hidden : true
	},{
		header : '端口ID',
		dataIndex : 'PTP_ID',
		width : 100,
		hidden : true
	},{
		header : '网元ID',
		dataIndex : 'NE_ID',
		width : 100,
		hidden : true
	},{
		header : '板卡ID',
		dataIndex : 'UNIT_ID',
		width : 100,
		hidden : true
	},{
		header : '链路ID',
		dataIndex : 'BASE_LINK_ID',
		width : 100,
		hidden : true
	},{
		header : '保护组ID',
		dataIndex : 'BASE_PRO_GROUP_ID',
		width : 100,
		hidden : true
	},{
		header : '告警级别',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 100,
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
		header : '子网',
		dataIndex : 'SUBNET_NAME',
		width : 70,
		hidden : true
	},{
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	},{
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 100
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
		width : 100
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
	}]
});

/**
 * 创建表格第一行工具栏
 */
var firstTbar = new Ext.Toolbar({
	border : false,
	items : [ '-',/**'网管分组：', emsGroupCombo, '-', '网管：', emsCombo, '-', '网元：', neCombo, '-',*/{
			text: '查询',
			icon : '../../resource/images/btnImages/search.png',
			privilege : viewAuth,
			handler : function (){
				//延迟半秒执行，网元下拉框中加载太慢导致的取值不正确
				(function(){
					queryHistoryAlarm();
				}).defer(500);
			} 
		},{
	        text: '高级查询', 
	        privilege : viewAuth,
	        handler : function(){
	        	queryHistoryAlarm_high();
	        }
	    },'-',{
	        text: '相关电路',
	        privilege : viewAuth,
	        handler : function(){
	        	gotoCircuit();
	        }
	    },'-',{
	        text: '详情',
	        icon : '../../resource/images/btnImages/information.png',
	        privilege : viewAuth,
	        handler : function (){
	        	queryAlarmDetail();
	        }
	    },'-',{
	        text: '屏蔽',
	        icon : '../../resource/images/btnImages/error_delete.png',
	        handler : function (){
	        	alarmShield();
	        }
	    },'-',{
	    	text : "导出",
			icon : '../../resource/images/btnImages/export.png',
			privilege : viewAuth,
			handler : function() {
				exportHistoryAlarmData();
			}
		}]
});

function getDayBefore(){
	var d = new Date();
	d.setDate(d.getDate()-1);    //得到前一天的
	return d.format("yyyy-MM-dd HH:mm:ss");
}

/**
 * 创建表格第二个工具栏
 */
var today = new Date();
var todayStr = today.format("yyyy-MM-dd HH:mm:ss");
today.setDate(today.getDate()-7);
var lastWeekStr = today.format("yyyy-MM-dd HH:mm:ss");
var secondTbar = new Ext.Toolbar({
	border : false,
	items : [ '-','首次发生时间：', '从', {
		id : 'firstStart',
		xtype : 'textfield',
		readOnly : true,
		cls : 'Wdate',
		width : 150,
		value:lastWeekStr,
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : 'firstStart',
					isShowClear : true,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd HH:mm:ss',
					autoPickDate : true,
					maxDate : '%y-%M-%d'
				});
				this.blur();
			}
		}
	},'到', {
		id : 'firstEnd',
		xtype : 'textfield',
		readOnly : true,
		cls : 'Wdate',
		width : 150,
		value:todayStr,
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : 'firstEnd',
					isShowClear : true,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd HH:mm:ss',
					autoPickDate : true,
					maxDate : '%y-%M-%d'
				});
				this.blur();
			}
		}
	}, '-', '清除时间：', '从', {
		id : 'clearStart',
		xtype : 'textfield',
		readOnly : true,
		cls : 'Wdate',
		width : 150,
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : 'clearStart',
					isShowClear : true,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd HH:mm:ss',
					autoPickDate : true,
					maxDate : '%y-%M-%d'
				});
				this.blur();
			}
		}
	},  '到', {
		id : 'clearEnd',
		xtype : 'textfield',
		readOnly : true,
		cls : 'Wdate',
		width : 150,
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : 'clearEnd',
					isShowClear : true,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd HH:mm:ss',
					autoPickDate : true,
					maxDate : '%y-%M-%d'
				});
				this.blur();
			}
		}
	}

	]
});

/**
 * 把两个工具栏合成一个工具栏
 */
var tbar = new Ext.Toolbar({
	layout : 'form',
	border : false,
	items : [firstTbar,secondTbar]
});

/**
 * 创建数据源
 */
var store = new Ext.data.Store({
	url : 'fault!getHistoryAlarms.action',
	baseParams : {
		"jsonString" : Ext.encode({'neId':staticNeId,'subnetId':"",'emsId':staticEmsId,'emsGroupId':staticEmsGroupId,
		'firstStart':new Date().format('yyyy-MM-dd')+' 00:00:00','firstEnd':new Date().format('yyyy-MM-dd HH:mm:ss'),'clearStart':'','clearEnd':''}),
		'limit':pageSize
	},
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, 
			['_id','OBJECT_TYPE','PTP_ID','NE_ID','UNIT_ID','BASE_LINK_ID','BASE_PRO_GROUP_ID','CTP_ID',
			 'PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','EMS_NAME','SUBNET_NAME','NE_NAME','PRODUCT_NAME',
			 'SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NAME','DOMAIN','PTP_TYPE','INTERFACE_RATE','CTP_NAME','FIRST_TIME','AMOUNT',
			 'NE_TIME','SERVICE_AFFECTING','CLEAR_TIME','DURATION','ACK_TIME','ACK_USER','ALARM_TYPE','PROBABLE_CAUSE'])
});

/**
 * 创建表格分页工具栏
 */
var pageTool = new Ext.PagingToolbar({
	pageSize : pageSize,
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

/**
 * 创建一个可编辑的表格
 */
var editGridPanel = new Ext.grid.GridPanel({
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm, 
	stripeRows : true,
	forceFit : true,
	loadMask : true,
	stateId:'historyAlarmStateId',  
	stateful:true,
	view : new Ext.ux.grid.LockingGridView({
		scrollDelay:false,
		cacheSize:100,
		cleanDelay:500//,
//		autoScrollTop:false
	}),
//	view : new Ext.ux.grid.BufferView({}),
	tbar : tbar,
	bbar : pageTool
});

/**
 * 查看告警详情
 */
function queryAlarmDetail() {
	// 获取选择的记录
	var record = editGridPanel.getSelectionModel().getSelections();
	if (record.length > 0 && record.length < 2) {// 如果选择一条记录
		// 告警ID
		var alarmId = record[0].get('_id');
		// 跳转地址
		var url = 'alarmDetail.jsp?alarmId='+alarmId+'&type=history';
		var addWindow = new Ext.Window({
			id : 'addWindow',
			title : '告警详情',
			width : 450,
			height : 500,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='alarmDetail_panel' name = 'alarmDetail_panel'  src = " + url
					+ " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		addWindow.show();
		// 调节高度
		if (addWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			addWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
		} else {
			addWindow.setHeight(addWindow
					.getInnerHeight());
		}
		addWindow.center();
	} else{
		Ext.Msg.alert('提示', '请选择需要查看的告警，每次选择一条');
	}
}

/**
 * 历史告警基础查询  @@@分权分域到网元@@@
 */
function queryHistoryAlarm(){
	var staticEmsGroupId = '';
	var staticEmsId = '';
	var staticSubNeId = '';
	var staticNeId = '';
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var checkedNodeIds;
	if (iframe.getCheckedNodes) {
		checkedNodeIds = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ],
				"top");
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes([ "nodeId",
				"nodeLevel" ], "top");
	}
	
	if(checkedNodeIds&&checkedNodeIds.length>0){
		// 选择对象最大数检查
		if (checkedNodeIds.length > maxSelectNum){
			Ext.Msg.alert('提示', '您选择的对象数已经超过上限，请重新进行选择！');
			return;
		}
		// 选择对象级别检查
		var nodeLevel = checkedNodeIds[0]["nodeLevel"];
		for(var i=1; i<checkedNodeIds.length; i++){
			if(nodeLevel!=checkedNodeIds[i]["nodeLevel"]){
				Ext.Msg.alert('提示', '您同时选择了不同级别的对象，请重新进行选择！');
				return;
			}
		}
		// 选择对象数据组织
		if(checkedNodeIds[0]["nodeLevel"]==1){
			for(var i=0; i<checkedNodeIds.length; i++){
				staticEmsGroupId += checkedNodeIds[i]["nodeId"] + ',';;
			}
			staticEmsGroupId=staticEmsGroupId.substring(0, staticEmsGroupId.lastIndexOf(','));
		}else if(checkedNodeIds[0]["nodeLevel"]==2){
			for(var i=0; i<checkedNodeIds.length; i++){
				staticEmsId += checkedNodeIds[i]["nodeId"] + ',';;
			}
			staticEmsId=staticEmsId.substring(0, staticEmsId.lastIndexOf(','));
		}else if(checkedNodeIds[0]["nodeLevel"]==3){
			for(var i=0; i<checkedNodeIds.length; i++){
				staticSubNeId += checkedNodeIds[i]["nodeId"] + ',';;
			}
			staticSubNeId=staticSubNeId.substring(0, staticSubNeId.lastIndexOf(','));
		}else if(checkedNodeIds[0]["nodeLevel"]==4){
			for(var i=0; i<checkedNodeIds.length; i++){
				staticNeId += checkedNodeIds[i]["nodeId"] + ',';;
			}
			staticNeId=staticNeId.substring(0, staticNeId.lastIndexOf(','));
		}
	}
	var firstStart = Ext.getCmp('firstStart').getValue();
	var firstEnd = "";
	if(firstStart==""){
		if(Ext.getCmp('firstEnd').getValue()!=""){
			Ext.Msg.alert('错误', '请选择首次发生时间开始时间');
			return false;
		}
	}else{
		if(Ext.getCmp('firstEnd').getValue()==""){
			firstEnd = new Date().format('yyyy-MM-dd HH:mm:ss');
			Ext.getCmp('firstEnd').setValue(new Date().format('yyyy-MM-dd HH:mm:ss'));
		}else{
			firstEnd = Ext.getCmp('firstEnd').getValue();
		}
	}
	var clearStart = clearStart = Ext.getCmp('clearStart').getValue();
	var clearEnd = "";
	if(clearStart==""){
		if(Ext.getCmp('clearEnd').getValue()!=""){
			Ext.Msg.alert('错误', '请选择清除时间开始时间');
			return false;
		}
	}else{
		if(Ext.getCmp('clearEnd').getValue()==""){
			Ext.Msg.alert('错误', '请选择清除时间结束时间');
			return false;
		}else{
			clearEnd = Ext.getCmp('clearEnd').getValue();
		}
	}
	// 首次发生时间范围检查
	if (!dateRangeCheckIsOk(firstStart,firstEnd)){
		Ext.Msg.alert('提示', '首次发生时间范围选择错误：结束时间必须大于开始时间，请重新选择');
		return false;
	}
	// 清除时间范围检查
	if (!dateRangeCheckIsOk(clearStart,clearEnd)){
		Ext.Msg.alert('提示', '清除时间范围选择错误：结束时间必须大于开始时间，请重新选择');
		return false;
	}
	store.baseParams = {
			"jsonString" : Ext.encode({'neId':staticNeId,'subnetId':staticSubNeId,'emsId':staticEmsId
					,'emsGroupId':staticEmsGroupId,'firstStart':firstStart,'firstEnd':firstEnd
					,'clearStart':clearStart,'clearEnd':clearEnd}),
			'limit':pageSize
	};
	store.load({
		callback : function(records,options,success){
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}
		}
	});
}

/**
 * 告警高级查询
 */
function queryHistoryAlarm_high(){
	// 跳转地址
	var url = 'highQuery.jsp?type=history&authSequence=' + authSequence;
	var highQueryWindow = new Ext.Window({
		id : 'highQueryWindow',
		title : '高级查询',
		width:Ext.getBody().getWidth()*0.8,  
		height:Ext.getBody().getHeight(),
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='highQuery_panel' name = 'highQuery_panel'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	highQueryWindow.show();
	// 调节高度
	if (highQueryWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		highQueryWindow.setHeight(Ext.getCmp('win').getHeight()-20 );
	} else {
		highQueryWindow.setHeight(highQueryWindow
				.getInnerHeight());
	}
	// 调节宽度
//	if (highQueryWindow.getWidth() > Ext.getCmp('win').getWidth()) {
//		highQueryWindow.setWidth(Ext.getCmp('win').getWidth() );
//	} else {
//		highQueryWindow.setWidth(highQueryWindow
//				.getInnerWidth());
//	}
	highQueryWindow.center();
}

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
 * 跳转告警屏蔽
 */
function alarmShield(){
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length>=1){
		var ids = '';
		for ( var i = 0; i < records.length; i++) {
			ids += records[i].get('_id') + ',';
		}
		ids = ids.substring(0, ids.lastIndexOf(','));
		parent.parent.addTabPage('../../jsp/faultManager/alarmShield.jsp?alarmIds='+ids+'&tabFlag=history', "告警及事件屏蔽",authSequence);
	}else{
		parent.parent.addTabPage('../../jsp/faultManager/alarmShield.jsp?alarmIds='+'&tabFlag=history', "告警及事件屏蔽",authSequence);
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

//导出 历史告警数据
function exportHistoryAlarmData(){
	var hiddenColoumms=getGridHiddenColomn();
	var staticEmsGroupId = '';
	var staticEmsId = '';
	var staticSubNeId = '';
	var staticNeId = '';
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var checkedNodeIds;
	if (iframe.getCheckedNodes) {
		checkedNodeIds = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ],
				"top");
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes([ "nodeId",
				"nodeLevel" ], "top");
	}
	if(checkedNodeIds&&checkedNodeIds.length>0){
		if(checkedNodeIds[0]["nodeLevel"]==1){
			staticEmsGroupId = checkedNodeIds[0]["nodeId"];
			staticEmsId = '';
			staticSubNeId = '';
			staticNeId = '';
		}else if(checkedNodeIds[0]["nodeLevel"]==2){
			staticEmsId = checkedNodeIds[0]["nodeId"];
			staticEmsGroupId = '';
			staticSubNeId = '';
			staticNeId = '';
		}else if(checkedNodeIds[0]["nodeLevel"]==3){
			staticSubNeId =checkedNodeIds[0]["nodeId"];
			staticEmsGroupId = '';
			staticEmsId = '';
			staticNeId = '';
		}else if(checkedNodeIds[0]["nodeLevel"]==4){
			staticNeId =checkedNodeIds[0]["nodeId"];
			staticEmsGroupId = '';
			staticEmsId = '';
			staticSubNeId = '';
		}
	}
	var firstStart = Ext.getCmp('firstStart').getValue();
	var firstEnd = "";
	if(firstStart==""){
		if(Ext.getCmp('firstEnd').getValue()!=""){
			Ext.Msg.alert('错误', '请选择首次发生时间开始时间');
			return false;
		}
	}else{
		if(Ext.getCmp('firstEnd').getValue()==""){
			firstEnd = new Date().format('yyyy-MM-dd HH:mm:ss');
			Ext.getCmp('firstEnd').setValue(new Date().format('yyyy-MM-dd HH:mm:ss'));
		}else{
			firstEnd = Ext.getCmp('firstEnd').getValue();
		}
	}
	var clearStart = clearStart = Ext.getCmp('clearStart').getValue();
	var clearEnd = "";
	if(clearStart==""){
		if(Ext.getCmp('clearEnd').getValue()!=""){
			Ext.Msg.alert('错误', '请选择清除时间开始时间');
			return false;
		}
	}else{
		if(Ext.getCmp('clearEnd').getValue()==""){
			Ext.Msg.alert('错误', '请选择清除时间结束时间');
			return false;
		}else{
			clearEnd = Ext.getCmp('clearEnd').getValue();
		}
	}
	// 首次发生时间范围检查
	if (!dateRangeCheckIsOk(firstStart,firstEnd)){
		Ext.Msg.alert('提示', '首次发生时间范围选择错误：结束时间必须大于开始时间，请重新选择');
		return false;
	}
	// 清除时间范围检查
	if (!dateRangeCheckIsOk(clearStart,clearEnd)){
		Ext.Msg.alert('提示', '清除时间范围选择错误：结束时间必须大于开始时间，请重新选择');
		return false;
	}
	var params= {
			"jsonString" : Ext.encode({'neId':staticNeId,'subnetId':staticSubNeId,'emsId':staticEmsId
					,'emsGroupId':staticEmsGroupId,'firstStart':firstStart,'firstEnd':firstEnd
					,'clearStart':clearStart,'clearEnd':clearEnd,'hiddenColoumms':hiddenColoumms}),
			'limit':5000
	};
	window.location.href = "fault!downloadHistoryAlarmResult.action?" + Ext.urlEncode(params);
}

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
// 日期范围检查，检查结束日期是否大于开始日期
function dateRangeCheckIsOk(startDate,endDate){
	if(startDate=="" || endDate==""){
		return true;
	}
	// 开始日期
	var startDateTimeStr=startDate.split(" ");
	var startDateStr=startDateTimeStr[0].split("-");
	var startTimeStr=startDateTimeStr[1].split(":");
	var start = startDateStr[0]+startDateStr[1]+startDateStr[2]+startTimeStr[0]+startTimeStr[1]+startTimeStr[2];
	// 结束日期
	var endDateTimeStr=endDate.split(" ");
	var endDateStr=endDateTimeStr[0].split("-");
	var endTimeStr=endDateTimeStr[1].split(":");
	var end = endDateStr[0]+endDateStr[1]+endDateStr[2]+endTimeStr[0]+endTimeStr[1]+endTimeStr[2];
	// 日期比较
	if(end>start){
		return true;
	}else{
		return false;
	}
}

Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 300000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	init();
	new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [westPanel,editGridPanel]
	});
	//放最后才能显示遮罩效果
	/**
	 * 加载数据源
	 */
	store.load({
		callback : function(records,options,success){
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}
		}
	});
	// 选择网元告警时初始化树对象
	if (neInfo) {
		setTimeout("checkNodesForWait(\"" + neInfo + "\")", 500);
	}
});

function checkNodesForWait(nodeInfo) {
	var iframe = window.frames["tree_panel"];
	if (iframe.checkNodes) {
		iframe.checkNodes(nodeInfo);
	} else {
		setTimeout("checkNodesForWait(\"" + nodeInfo + "\")", 500);
	}
}