
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
(function init(){
	Ext.Ajax.request({
		url: 'fault!getAlarmColorSet.action',
	    method: 'POST',
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	// 告警灯显示
//	    	var alarmLightHidden = obj.alarmLightHidden;
//	    	if(alarmLightHidden!=''){
//	    		var arr = alarmLightHidden.split(',');
//	    		for ( var i = 0; i < arr.length; i++) {
//					if(arr[i]==1){
//						Ext.getCmp('PS_CRITICAL').setVisible(false);
//					}else if(arr[i]==2){
//						Ext.getCmp('PS_MAJOR').setVisible(false);
//					}else if(arr[i]==3){
//						Ext.getCmp('PS_MINOR').setVisible(false);
//					}else if(arr[i]==4){
//						Ext.getCmp('PS_WARNING').setVisible(false);
//					}else if(arr[i]==5){
//						Ext.getCmp('PS_CLEARED').setVisible(false);
//					}
//				}
//	    	}
	    	// 告警灯背景颜色  
//	    	Ext.getCmp('PS_CRITICAL').addClass(getBackgroundImage(obj.PS_CRITICAL_IMAGE));
//	    	Ext.getCmp('PS_MAJOR').addClass(getBackgroundImage(obj.PS_MAJOR_IMAGE));
//	    	Ext.getCmp('PS_MINOR').addClass(getBackgroundImage(obj.PS_MINOR_IMAGE));
//	    	Ext.getCmp('PS_WARNING').addClass(getBackgroundImage(obj.PS_WARNING_IMAGE));
//	    	Ext.getCmp('PS_CLEARED').addClass(getBackgroundImage(obj.PS_CLEARED_IMAGE));
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
})();

function addAlarm() {
	
	var treeParams = {
			rootId : 0,
			rootType : 0,
			rootText : "FTSP",
			rootVisible : false,
			checkModel : "multiple",
//			checkModel : "single",
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
		maxSize : 320,
		autoScroll : true,
		forceFit : true,
		collapsed : false, // initially collapse the group
		collapsible : false,
		collapseMode : 'mini',
		split : true,
		html : '<iframe id="tree_panel" name = "tree_panel" src =' + treeurl
				+ ' height="100%" width="100%" frameBorder=0 border=0/>'
	});
	
	var pageSize=500;
	
	/**
	 * 创建表格选择模型(多选、单选.....)
	 */ 
	var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
////		singleSelect ：true,// 表示只能单选，默认false,
////		sortable : true,//表示选择框列可以排序，默认fasle
	//});
	//sm.sortLock();
	//var sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false,
		locked : true
	});
	/**
	 * 创建数告警列表列模型
	 */
	var cm = new Ext.ux.grid.LockingColumnModel({
	//var cm = new Ext.grid.ColumnModel({
		defaults : {// 所有列默认的属性
			sortable : true// 表示所有列可以排序
		},
		columns : [ new Ext.grid.RowNumberer({
//			header : '序号',// 行号列的列名,默认为空
			width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
			locked : true
		}), sm, 
		{
			header : '告警数据ID',
			dataIndex : '_id',
			width : 80,
			hidden : true
		},{
			header : '告警对象类型',
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
			header : '已和故障关联',
			dataIndex : 'ANALYSIS_STATUS',
			width : 80,
			renderer : function(value){
				if(value == -1){
					return '否';
				}else{
					return '是';
				}
			},
			locked : true
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
		}]
	});
	
	var staticNeId = '';
	var staticEmsGroupId = '';
	var staticEmsId = '';
	var staticSubNeId = '';
	
	// 告警灯级别
	var alarmLvStatus = '';
	var view_ptpId = '';
	var view_unitId = '';
	// 告警过滤器ID
	var filterId = -1;
	// 告警状态ID
	var statusId = 1; //1:全部
	// 对象树最大选择数
	var maxSelectNum = 50;
	
	/**
	 * 创建当前告警列表数据源
	 */ 
	var store = new Ext.data.Store({
		url : 'fault!getCurrentAlarms.action',// 数据请求地址
		baseParams : {// 请求参数
			'jsonString' : Ext.encode({'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,'emsGroupId':staticEmsGroupId
				,'alarmLv':alarmLvStatus,'ptpId':view_ptpId,'unitId':view_unitId,'filterId':filterId,'statusId':statusId,'isConverge':false
				}),// 把对象转成JSON格式字符串
			'limit':pageSize// 每页显示多少条数据
		},
		reader : new Ext.data.JsonReader({// 数据源数据格式
					totalProperty : 'total',// 记录数
					root : 'rows'// 列表数据
				}, 
				['_id','OBJECT_TYPE','PTP_ID','NE_ID','UNIT_ID','BASE_LINK_ID','BASE_PRO_GROUP_ID','SHELF_NO','CTP_ID',
				 'IS_ACK','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','EMS_NAME',
				 'NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NAME','DOMAIN','PTP_TYPE',
				 'INTERFACE_RATE','CTP_NAME','IS_CLEAR','FIRST_TIME','AMOUNT','NE_TIME','SERVICE_AFFECTING',
				 'CLEAR_TIME','DURATION','ACK_TIME','ACK_USER','ALARM_TYPE','CONVERGE_FLAG','ANALYSIS_STATUS'])
	});
	
	/**
	 * 创建表格分页工具栏
	 */
	var pageTool = new Ext.PagingToolbar({
		pageSize : pageSize,
		store : store,
		displayInfo : true,
		displayMsg : '当前 {0} - {1} ，总数 {2}',
		emptyMsg : '没有记录'
	});
	
	/**
	 * 创建一个可编辑的表格
	 */
	var editGridPanel = new Ext.grid.GridPanel({
		region : 'center',
		store : store,
		id:"addCurrAlarmPanel",
		cm : cm,
		selModel : sm, // 必须加不然不能选checkbox
		stripeRows : true, // 交替行效果
		forceFit : true,
		loadMask : true,
//		stateId:'currentAlarmStateId',
		stateful:false,
		view : new Ext.ux.grid.LockingGridView({
			scrollDelay:false,
			cacheSize:100,
			syncHeights: false,
			cleanDelay:500//,
			//autoScrollTop:false
		}),
//		view : new Ext.ux.grid.BufferView({}),
		tbar : [{
			text : '查询',
			handler : function(){
				staticNeId = '';
				staticEmsGroupId = '';
				staticEmsId = '';
				staticSubNeId = '';
				
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
				
//				Ext.getBody().mask('读取中...');
				
				store.baseParams = {// 请求参数
					'jsonString' : Ext.encode({'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,'emsGroupId':staticEmsGroupId
						,'alarmLv':alarmLvStatus,'ptpId':view_ptpId,'unitId':view_unitId,'filterId':filterId,'statusId':statusId,'isConverge':false
						}),// 把对象转成JSON格式字符串
					'limit':pageSize// 每页显示多少条数据
				};
				store.load({
					callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
						if (!success) {
//							Ext.getBody().unmask();
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}else{
//							Ext.getBody().unmask();
						}
					}
				});
			}
		}],
		bbar : pageTool
	});
	
	var currAlarmWindow = new Ext.Window({
		id : 'currAlarmWindow',
		layout : 'border',
		title : '当前告警',
		modal : true,
		width : 0.63 * Ext.getCmp('win').getWidth(),
		height : 0.9 * Ext.getCmp('win').getHeight() + 10,
		minWidth : 0.5 * Ext.getCmp('win').getWidth(),
		autoScroll : true,
		items : [ westPanel, editGridPanel],
		buttons : [{
			text : '确定',
			handler : function(){
				
				var cell = Ext.getCmp('addCurrAlarmPanel').getSelectionModel().getSelections();
				if(cell.length == 0){
					Ext.Msg.alert("提示","请选择告警！");
				}else{
					var related = false;
					for(var i=0;i<cell.length;i++){
						if(cell[i].get("ANALYSIS_STATUS") != -1){
							related = true;
							break;
						}
					}
					if(related){
						Ext.Msg.alert("提示", "所选告警中存在被故障关联过的告警，请重新选择！");
					}else{
						addAlarmCache(cell);
						currAlarmWindow.close();
						setFaultTime();
						
						enableSaveFaultInfoBtn();
					}
				}
			}
		},{
			text : '取消',
			handler : function(){
				currAlarmWindow.close();
			}
		}]
	});
	
	currAlarmWindow.show();
	store.load();
}

function addAlarmCache(cell){
	
	for(var i=0;i<cell.length;i++){
		var data = {
			ALARM_ID : cell[i].get("_id"),
			CONVERGE_FLAG : cell[i].get("CONVERGE_FLAG"),
			ALARM_NAME : cell[i].get("NATIVE_PROBABLE_CAUSE"),
			SEVERITY : cell[i].get("PERCEIVED_SEVERITY"),
			NE_NAME : cell[i].get("NE_NAME"),
			START_TIME_STR : cell[i].get("FIRST_TIME"),
			CLEAN_TIME_STR : cell[i].get("CLEAR_TIME")
		};
		var store = Ext.getCmp("gridPanel_Alarm").getStore();
		var flag = false;
		store.each(function(r){
			if(data.ALARM_ID == r.get("ALARM_ID")){
				flag = true;
				return false;
			}
		});
		if(!flag){
			store.add(new store.recordType(data));
		}
	}
}

//设置故障时间（前台控件）
function setFaultTime(){
	
	var store = Ext.getCmp("gridPanel_Alarm").getStore();
	var records = store.getRange();
	var startTimeMain = null;
	var startTimeSub = null;
	var endTimeMain = null;
	var endTimeSub = null;
	var tempTime = null;
	var hasMain = false;
	var isEndMain = true;
	var isEndSub = true;
	
	for(var i=0; i<records.length; i++){
		var startTimeStr = records[i].get("START_TIME_STR");
		var endTimeStr = records[i].get("CLEAN_TIME_STR");
		var convergeFlag = records[i].get("CONVERGE_FLAG");
		
		if(convergeFlag == 1){//主告警
			hasMain = true;
			if(startTimeStr != null && startTimeStr != ""){
				if(startTimeMain == null){
					startTimeMain = dateStr2Date(startTimeStr);
				}else{
					tempTime = dateStr2Date(startTimeStr);
					startTimeMain = (startTimeMain > tempTime) ? tempTime : startTimeMain;
				}
			}
			
			if(endTimeStr != null && endTimeStr != ""){
				if(endTimeMain == null){
					endTimeMain = dateStr2Date(endTimeStr);
				}else{
					tempTime = dateStr2Date(endTimeStr);
					endTimeMain = (endTimeMain < tempTime) ? tempTime : endTimeMain;
				}
			}else{
				isEndMain = false;
			}
		}else{
			if(startTimeStr != null && startTimeStr != ""){
				if(startTimeSub == null){
					startTimeSub = dateStr2Date(startTimeStr);
				}else{
					tempTime = dateStr2Date(startTimeStr);
					startTimeSub = (startTimeSub > tempTime) ? tempTime : startTimeSub;
				}
			}
			
			if(endTimeStr != null && endTimeStr != ""){
				if(endTimeSub == null){
					endTimeSub = dateStr2Date(endTimeStr);
				}else{
					tempTime = dateStr2Date(endTimeStr);
					endTimeSub = (endTimeSub < tempTime) ? tempTime : endTimeSub;
				}
			}else{
				isEndSub = false;
			}
		}
	}
	
	Ext.getCmp("startTimeFaultInfo").setValue("");
	Ext.getCmp("alarmEndFaultInfo").setValue("");
	
	if(hasMain){
		if(startTimeMain != null){
			Ext.getCmp("startTimeFaultInfo").setValue(date2DateStr(startTimeMain));
		}
		
		if(isEndMain){
			if(endTimeMain != null){
				Ext.getCmp("alarmEndFaultInfo").setValue(date2DateStr(endTimeMain));
			}
		}
	}else{
		if(startTimeSub != null){
			Ext.getCmp("startTimeFaultInfo").setValue(date2DateStr(startTimeSub));
		}
		
		if(isEndSub){
			if(endTimeSub != null){
				Ext.getCmp("alarmEndFaultInfo").setValue(date2DateStr(endTimeSub));
			}
		}
	}
}

//时间字符串转换为时间对象
function dateStr2Date(dateStr){
	return new Date(Date.parse(dateStr.replace(/-/g, "/")));
}

//时间对象转成时间字符串
function date2DateStr(date){
	return date.format("yyyy-MM-dd hh:mm:ss");
}
















