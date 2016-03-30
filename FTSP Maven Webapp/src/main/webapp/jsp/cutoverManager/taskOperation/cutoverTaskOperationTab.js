//Ext.override(Ext.Button, { 
//         onMouseOut : function(e){        var internal = e.within(this.el) && e.target != this.el.dom;                this.fireEvent('mouseout', this, e);        if(this.isMenuTriggerOut(e, internal)){            this.fireEvent('menutriggerout', this, this.menu, e);        }    },    
//});

window.onresize = function () {Ext.getCmp("tabs").setHeight(Ext.getCmp('win').getHeight()-125);
Ext.getCmp("tabs").setWidth(Ext.getCmp('win').getWidth());}
var snapshotBefore;
var snapshotAfter;
//重新设置图片大小 	 
function ResizeImage(obj, MaxW, MaxH) { 

	if (obj != null) 
		imageObject = obj; 	
//	var state=imageObject.readyState; 	
//	if(state!='complete') { 
//		setTimeout("ResizeImage(null,"+MaxW+","+MaxH+")",50); 
//		return; 
//	} 	
	var oldImage = new Image(); 
	oldImage.src = imageObject.src; 
	var dW=oldImage.width; 
	var dH=oldImage.height; 
	if(dW>MaxW || dH>MaxH) 
	{ 
		a=dW/MaxW; 
		b=dH/MaxH; 
		if(b>a) 
			a=b; 
		dW=dW/a; 
		dH=dH/a; 
	} 
	if(dW > 0 && dH > 0) 
	{
		imageObject.width=dW; 
		imageObject.height=dH; 
	} 
}
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
 var img1 = '<img  src="../../../resource/images/割接任务进度框图1.png" onload="ResizeImage(this,500,90);">';
 var img2 = '<img  src="../../../resource/images/割接任务进度框图2.png" onload="ResizeImage(this,500,90);">';	 
 var img3 = '<img  src="../../../resource/images/割接任务进度框图3.png" onload="ResizeImage(this,500,90);">';
 var img4 = '<img  src="../../../resource/images/割接任务进度框图4.png" onload="ResizeImage(this,500,90);">';
 var img5 = '<img  src="../../../resource/images/割接任务进度框图5.png" onload="ResizeImage(this,500,90);">';
 
Ext.onReady(function(){
Ext.Ajax.timeout = 900000;
 	  var panel_1 = new Ext.Panel({
        id: 'panel_1',
        renderTo:"div_1",
        margins: '0 0 0 0',
        border:false,
        split:false,
        html : img1
      });
      var panel_2 = new Ext.Panel({
        id: 'panel_2',
        renderTo:"div_2",
        margins: '0 0 0 0',
        border:false,
        split:false,
        html : img2
      });
      var panel_3 = new Ext.Panel({
        id: 'panel_3',
        renderTo:"div_3",
        margins: '0 0 0 0',
        border:false,
        split:false,
        html : img3
      });
      var panel_4 = new Ext.Panel({
        id: 'panel_4',
        renderTo:"div_4",
        margins: '0 0 0 0',
        border:false,
        split:false,
        html : img4
      });
      var panel_5 = new Ext.Panel({
        id: 'panel_5',
        renderTo:"div_5",
        margins: '0 0 0 0',
        border:false,
        split:false,
        html : img5
      });
  /*****-----------------异常性能---------------*****/
  
var cmPm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [
			new Ext.grid.RowNumberer({
				width:26,
				locked : true
			}),
			{
				id : 'DISPLAY_NE',
				header : '网元',
				dataIndex : 'DISPLAY_NE',
				width : 80
			},

			{
				id : 'DISPLAY_PORT_DESC',
				header : '端口',
				dataIndex : 'DISPLAY_PORT_DESC',
				width : 150
			},
			{
				id : 'PTP_TYPE',
				header : '端口类型',
				dataIndex : 'PTP_TYPE',
				hidden:true,
				width : 60
			},
			{
				id : 'RATE',
				header : '速率',
				dataIndex : 'RATE',
				hidden:true,
				width : 50
			},
			{
				id : 'DISPLAY_CTP',
				header : '通道',
				dataIndex : 'DISPLAY_CTP',
				hidden:true,
				width : 100
			},
			{
				id : 'PM_DESCRIPTION',
				header : '性能事件',
				dataIndex : 'PM_DESCRIPTION',
				
				width : 130
			},
			{
				id : 'LOCATION',
				header : '方向',
				dataIndex : 'LOCATION',
				hidden:true,
				width : 60,
				renderer : function(v) {
					switch (v) {
					case 1:
						return "近端接收";
					case 2:
						return "远端接收";
					case 3:
						return "近端发送";
					case 4:
						return "远端发送";
					case 5:
						return "双向";
					default:
						return null;
					}
				}
			},
			{
				id : 'VALUE_BEFORE',
				header : '割接前快照值',
				dataIndex : 'VALUE_BEFORE',
				width : 150
			},
			{
				id : 'VALUE_AFTER',
				header : '割接后快照值',
				dataIndex : 'VALUE_AFTER',
				width : 150
			},
			{
				id : 'DIFFERENCE',
				header : '差值',
				dataIndex : 'DIFFERENCE',
				width : 150,renderer : function(v, metadata, record) {
					exLv = record.get('level');
					if (exLv == 0) {
						return '<font color=black>' + v + '</font>';
					} else if (exLv == 1) {
						return '<font color=blue>' + v + '</font>';
					} else if (exLv == 2) {
						return '<font color=orange>' + v + '</font>';
					} else if (exLv == 3) {
						return '<font color=red>' + v + '</font>';
					}
				}
			},
//			{
//				id : 'level',
//				header : '割接level快照值',
//				dataIndex : 'level',
//				width : 150
//			},
			{
				id : 'PM_COMPARE_VALUE',
				header : '性能基准值',
				dataIndex : 'PM_COMPARE_VALUE',
				hidden:true,
				width : 65
			},
			{
				id : 'EXCEPTION_COUNT',
				header : '连续异常',
				dataIndex : 'EXCEPTION_COUNT',
				hidden:true,
				width : 60
			},

			{
				id : 'TIME_BEFORE',
				header : '割接前快照时间',
				dataIndex : 'TIME_BEFORE',
				width : 120
			},{
				id : 'TIME_AFTER',
				header : '割接后快照时间',
				dataIndex : 'TIME_AFTER',
				width : 120
			} ]
});

var storePm = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "ID", "BASE_EMS_CONNECTION_ID", "BASE_NE_ID", "BASE_RACK_ID", "BASE_SHELF_ID",
			"BASE_SLOT_ID", "BASE_SUB_SLOT_ID", "BASE_UNIT_ID", "BASE_SUB_UNIT_ID", "BASE_PTP_ID",
			"BASE_OTN_CTP_ID", "BASE_SDH_CTP_ID", "TARGET_TYPE", "LAYER_RATE", "PM_STD_INDEX",
			"PM_INDEX", "PM_VALUE", "PM_COMPARE_VALUE", "TYPE", "THRESHOLD_1", "THRESHOLD_2",
			"THRESHOLD_3", "OFFSET", "UPPER_VALUE", "UPPER_OFFSET", "LOWER_VALUE", "LOWER_OFFSET",
			"PM_DESCRIPTION", "LOCATION", "UNIT", "GRANULARITY", "EXCEPTION_LV", "EXCEPTION_COUNT",
			"RETRIEVAL_TIME", "DISPLAY_EMS_GROUP", "DISPLAY_EMS", "DISPLAY_SUBNET", "DISPLAY_NE",
			"DISPLAY_AREA", "DISPLAY_STATION", "DISPLAY_PRODUCT_NAME", "DISPLAY_DOMAIN",
			"DISPLAY_PORT_DESC", "RATE", "DISPLAY_CTP", "DISPLAY_TEMPLATE_NAME", "PTP_TYPE",
			"TEMPLATE_ID","VALUE_BEFORE","VALUE_AFTER","DIFFERENCE","TIME_BEFORE","TIME_AFTER","level" ])
});

var pageToolPm = new Ext.PagingToolbar({
	id : 'pageToolPm',
	pageSize : 20,// 每页显示的记录值
	store : storePm,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
var gridPanelPm = new Ext.grid.GridPanel({
	region : 'center',
	store : storePm,
	title:'性能异常',
	cm : cmPm,
	height:150,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	forceFit : true,
//	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	bbar : pageToolPm
});
  /*****-----------------异常性能结束---------------*****/
  
  /*****-----------------异常告警---------------*****/
  	/**
	*告警状态renderer方法
	**/
	function alarmCategoryRenderer(v)
	{
		if(v == 1)
			return '不变';
		else if(v == 2)
			return '消除';
		else if(v == 3)
			return '新增';
		else 	
			return "";
	}
   /**
 * 创建数告警列表列模型
 */
var cmAlarm = new Ext.ux.grid.LockingColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : true
	}),
	{
		header : '告警数据ID',
		dataIndex : '_id',
		width : 100,
		hidden : true
	},{
		header : '类型',
		dataIndex : 'ALARM_CATEGORY',
		width : 100,
		renderer : alarmCategoryRenderer
	},{
		header : '告警级别',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 100,
		renderer : function(value,meta,record){
			
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
			
		},
	},{
		header : '告警名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100
	},{
		header : '归一化名称',
		dataIndex : 'NORMAL_CAUSE',
		hidden:true,
		width : 100
	},
//	{//此处ROOM保存区域信息
//		header : '区域',
//		dataIndex : 'ROOM',
//		width : 100
//	},{
//		header : '局站',
//		dataIndex : 'STATION',
//		width : 100
//	},
	{
		header : '网管分组',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	},{
		header : '网管',
		dataIndex : 'NATIVE_EMS_NAME',
		width : 100
	},{
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	},{
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		hidden:true,
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
		dataIndex : 'PORT_NO',
		width : 100
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		width : 100
	},{
		header : '业务类型',
		dataIndex : 'DOMAIN',
		hidden:true,
		width : 100
	},{
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		hidden:true,
		width : 100
	},{
		header : '速率',
		dataIndex : 'INTERFACE_RATE',
		width : 100
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		hidden:true,
		width : 100
	},{
		header : '首次发生时间',
		dataIndex : 'FIRST_TIME',
		hidden:true,
		width : 100
	},{
		header : '频次',
		dataIndex : 'AMOUNT',
		hidden:true,
		width : 100
	},{
		header : '最近发生时间',
		dataIndex : 'UPDATE_TIME',
		hidden:true,
		width : 100
	},{
		header : '业务影响',
		dataIndex : 'SERVICE_AFFECTING',
		hidden:true,
		width : 100
	},{
		header : '清除时间',
		dataIndex : 'CLEAR_TIME',
		hidden:true,
		width : 100
	},{
		header : '确认时间',
		dataIndex : 'ACK_TIME',
		hidden:true,
		width : 100
	},{
		header : '确认者',
		dataIndex : 'ACK_USER',
		hidden:true,
		width : 100
	},{
		header : '告警类型',
		dataIndex : 'ALARM_TYPE',
		width : 100,
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
		header : '告警标准名',
		dataIndex : 'PROBABLE_CAUSE',
		hidden:true,
		width : 100
	},{
		header : '割接前快照时间',
		dataIndex : 'SNAPSHOT_TIME_BEFORE',
		width : 100
	},{
		header : '割接后快照时间',
		dataIndex : 'SNAPSHOT_TIME_AFTER',
		width : 100
	},{
		header : '清除状态',
		dataIndex : 'IS_CLEAR',
		hidden:true,
		width : 100,
		renderer : function(value){
			if(value==1){
				return '已清除';
			}else{
				return '未清除';
			}
		}
	}]
});

/**
 * 创建当前告警列表数据源
 */ 
var storeAlarm = new Ext.data.Store({
	url : 'cutover-task!getCurrentAlarms.action',// 数据请求地址
	baseParams : {// 请求参数
		'cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
		'limit':200// 每页显示多少条数据
	},
	reader : new Ext.data.JsonReader({// 数据源数据格式
				totalProperty : 'total',// 记录数
				root : 'rows'// 列表数据
			}, 
			['_id','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME',
			'NATIVE_EMS_NAME','NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NO',
			'DOMAIN','SNAPSHOT_TIME_BEFORE','SNAPSHOT_TIME_AFTER',
			 'PTP_TYPE','INTERFACE_RATE','CTP_NAME','FIRST_TIME','AMOUNT','UPDATE_TIME',
			 'SERVICE_AFFECTING','CLEAR_TIME','ACK_TIME','ACK_USER','ALARM_TYPE','PROBABLE_CAUSE',
			 'IS_CLEAR','ALARM_CATEGORY','ROOM','STATION'])
});



/**
 * 创建表格分页工具栏
 */
var pageToolAlarm = new Ext.PagingToolbar({
	pageSize : 20,
	id:'pageToolAlarm',
	store : storeAlarm,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});
var gridPanelAlarm = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : storeAlarm,
	title:'告警异常',
	cm : cmAlarm,
	height:150,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	forceFit : true,
//	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	bbar : pageToolAlarm
});
  /*****-----------------异常告警结束---------------*****/
    
  /*****-----------------异常倒换---------------*****/
     /**
 * 创建数倒换列表列模型
 */
var cmEvent = new Ext.ux.grid.LockingColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : true
	}),
	{
		header : 'ID',
		dataIndex : '_id',
		width : 100,
		hidden : true
	},{
		header : '网元',
		dataIndex : 'ALARM_CATEGORY',
		width : 100,
		renderer : alarmCategoryRenderer
	},{
		header : '保护类别',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 100,
		renderer : function(value,meta,record){
			if(value==1){
				return '<font color="#FF0000">紧急</font>';
			}else if(value==2){
				return '<font color="#FF8000">重要</font>';
			}else if(value==3){
				return '<font color="#FFFF00">次要</font>';
			}else if(value==4){
				return '<font color="#800000">提示</font>';
			}
			

		}
	},{
		header : '保护组名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100
	},{
		header : '保护组类型',
		dataIndex : 'NORMAL_CAUSE',
		hidden:true,
		width : 100
	},{//此处ROOM保存区域信息
		header : '倒换原因',
		dataIndex : 'ROOM',
		width : 100
	},{
		header : '发生时间',
		dataIndex : 'STATION',
		width : 100
	},{
		header : '被保护对象',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	},{
		header : '从对象',
		dataIndex : 'NATIVE_EMS_NAME',
		width : 100
	},{
		header : '切换到对象',
		dataIndex : 'NE_NAME',
		width : 100
	}]
});

/**
 * 创建倒换事件列表数据源
 */ 
var storeEvent = new Ext.data.Store({
	url : 'cutover-task!getCurrentAlarms.action',// 数据请求地址
	baseParams : {// 请求参数
		'cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
		'limit':200// 每页显示多少条数据
	},
	reader : new Ext.data.JsonReader({// 数据源数据格式
				totalProperty : 'total',// 记录数
				root : 'rows'// 列表数据
			}, 
			['_id','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME',
			'NATIVE_EMS_NAME','NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NO',
			'DOMAIN','SNAPSHOT_TIME_BEFORE','SNAPSHOT_TIME_AFTER',
			 'PTP_TYPE','INTERFACE_RATE','CTP_NAME','FIRST_TIME','AMOUNT','UPDATE_TIME',
			 'SERVICE_AFFECTING','CLEAR_TIME','ACK_TIME','ACK_USER','ALARM_TYPE','PROBABLE_CAUSE',
			 'IS_CLEAR','ALARM_CATEGORY','ROOM','STATION'])
});



/**
 * 创建表格分页工具栏
 */
var pageToolEvent = new Ext.PagingToolbar({
	pageSize : 20,
	id:'pageToolEvent',
	store : storeEvent,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});
var gridPanelEvent = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : storeEvent,
	title:'倒换异常',
	cm : cmEvent,
	height:150,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	forceFit : true,
//	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	bbar : pageToolEvent
});
  
     
  /*****-----------------异常倒换结束---------------*****/
  var formPanel1 = new Ext.form.FormPanel({
        id: 'formPanel1',
        title:'　',
        margins: '0 0 0 0',
        autoScroll:true,
        align:"left",
        border:false,
        split:false,
        items:[gridPanelPm,gridPanelAlarm,gridPanelEvent]
      });
    /*****-----------------异常性能（割接后）---------------*****/
  
var cmPmAfter = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [
			new Ext.grid.RowNumberer({
				width:26,
				locked : true
			}),
			{
				id : 'DISPLAY_NE',
				header : '网元',
				dataIndex : 'DISPLAY_NE',
				width : 80
			},

			{
				id : 'DISPLAY_PORT_DESC',
				header : '端口',
				dataIndex : 'DISPLAY_PORT_DESC',
				width : 150
			},
			{
				id : 'PTP_TYPE',
				header : '端口类型',
				dataIndex : 'PTP_TYPE',
				hidden:true,
				width : 60
			},
			{
				id : 'RATE',
				header : '速率',
				dataIndex : 'RATE',
				hidden:true,
				width : 50
			},
			{
				id : 'DISPLAY_CTP',
				header : '通道',
				dataIndex : 'DISPLAY_CTP',
				hidden:true,
				width : 100
			},
			{
				id : 'PM_DESCRIPTION',
				header : '性能事件',
				dataIndex : 'PM_DESCRIPTION',
				
				width : 130
			},
			{
				id : 'LOCATION',
				header : '方向',
				dataIndex : 'LOCATION',
				hidden:true,
				width : 60,
				renderer : function(v) {
					switch (v) {
					case 1:
						return "近端接收";
					case 2:
						return "远端接收";
					case 3:
						return "近端发送";
					case 4:
						return "远端发送";
					case 5:
						return "双向";
					default:
						return null;
					}
				}
			},
			{
				id : 'VALUE_BEFORE',
				header : '割接前快照值',
				dataIndex : 'VALUE_BEFORE',
				width : 150
			},
			{
				id : 'VALUE_AFTER',
				header : '割接后快照值',
				dataIndex : 'VALUE_AFTER',
				width : 150
			},
			{
				id : 'DIFFERENCE',
				header : '差值',
				dataIndex : 'DIFFERENCE',
				width : 150,renderer : function(v, metadata, record) {
					exLv = record.get('level');
					if (exLv == 0) {
						return '<font color=black>' + v + '</font>';
					} else if (exLv == 1) {
						return '<font color=blue>' + v + '</font>';
					} else if (exLv == 2) {
						return '<font color=orange>' + v + '</font>';
					} else if (exLv == 3) {
						return '<font color=red>' + v + '</font>';
					}
				}
			},
//			{
//				id : 'level',
//				header : '割接level快照值',
//				dataIndex : 'level',
//				width : 150
//			},
			{
				id : 'PM_COMPARE_VALUE',
				header : '性能基准值',
				dataIndex : 'PM_COMPARE_VALUE',
				hidden:true,
				width : 65
			},
			{
				id : 'EXCEPTION_COUNT',
				header : '连续异常',
				dataIndex : 'EXCEPTION_COUNT',
				hidden:true,
				width : 60
			},

			{
				id : 'TIME_BEFORE',
				header : '割接前快照时间',
				dataIndex : 'TIME_BEFORE',
				width : 120
			},{
				id : 'TIME_AFTER',
				header : '割接后快照时间',
				dataIndex : 'TIME_AFTER',
				width : 120
			},{
				id : 'EVALUATION_SCORE',
				header : '评估量化值',
				dataIndex : 'EVALUATION_SCORE',
				width : 120
				
			} ]
});

var storePmAfter = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "ID", "BASE_EMS_CONNECTION_ID", "BASE_NE_ID", "BASE_RACK_ID", "BASE_SHELF_ID",
			"BASE_SLOT_ID", "BASE_SUB_SLOT_ID", "BASE_UNIT_ID", "BASE_SUB_UNIT_ID", "BASE_PTP_ID",
			"BASE_OTN_CTP_ID", "BASE_SDH_CTP_ID", "TARGET_TYPE", "LAYER_RATE", "PM_STD_INDEX",
			"PM_INDEX", "PM_VALUE", "PM_COMPARE_VALUE", "TYPE", "THRESHOLD_1", "THRESHOLD_2",
			"THRESHOLD_3", "OFFSET", "UPPER_VALUE", "UPPER_OFFSET", "LOWER_VALUE", "LOWER_OFFSET",
			"PM_DESCRIPTION", "LOCATION", "UNIT", "GRANULARITY", "EXCEPTION_LV", "EXCEPTION_COUNT",
			"RETRIEVAL_TIME", "DISPLAY_EMS_GROUP", "DISPLAY_EMS", "DISPLAY_SUBNET", "DISPLAY_NE",
			"DISPLAY_AREA", "DISPLAY_STATION", "DISPLAY_PRODUCT_NAME", "DISPLAY_DOMAIN",
			"DISPLAY_PORT_DESC", "RATE", "DISPLAY_CTP", "DISPLAY_TEMPLATE_NAME", "PTP_TYPE",
			"TEMPLATE_ID","VALUE_BEFORE","VALUE_AFTER","DIFFERENCE","TIME_BEFORE","TIME_AFTER","level","EVALUATION_SCORE" ])
});

var pageToolPmAfter = new Ext.PagingToolbar({
	id : 'pageToolPmAfter',
	pageSize : 20,// 每页显示的记录值
	store : storePmAfter,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
var gridPanelPmAfter = new Ext.grid.GridPanel({
	region : 'center',
	store : storePmAfter,
	title:'性能异常',
	cm : cmPmAfter,
	height:150,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	forceFit : true,
//	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	bbar : pageToolPmAfter
});
  /*****-----------------异常性能结束---------------*****/
  
  /*****-----------------异常告警---------------*****/
  	/**
	*告警状态renderer方法
	**/
	function alarmCategoryRenderer(v)
	{
		if(v == 1)
			return '不变';
		else if(v == 2)
			return '消除';
		else if(v == 3)
			return '新增';
		else 	
			return "";
	}
   /**
 * 创建数告警列表列模型
 */
var cmAlarmAfter = new Ext.ux.grid.LockingColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : true
	}),
	{
		header : '告警数据ID',
		dataIndex : '_id',
		width : 100,
		hidden : true
	},{
		header : '类型',
		dataIndex : 'ALARM_CATEGORY',
		width : 100,
		renderer : alarmCategoryRenderer
	},{
		header : '告警级别',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 100,
		renderer : function(value,meta,record){
			
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
			
		},
	},{
		header : '告警名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100
	},{
		header : '归一化名称',
		dataIndex : 'NORMAL_CAUSE',
		hidden:true,
		width : 100
	},
//	{//此处ROOM保存区域信息
//		header : '区域',
//		dataIndex : 'ROOM',
//		width : 100
//	},{
//		header : '局站',
//		dataIndex : 'STATION',
//		width : 100
//	},
	{
		header : '网管分组',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	},{
		header : '网管',
		dataIndex : 'NATIVE_EMS_NAME',
		width : 100
	},{
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	},{
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		hidden:true,
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
		dataIndex : 'PORT_NO',
		width : 100
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		width : 100
	},{
		header : '业务类型',
		dataIndex : 'DOMAIN',
		hidden:true,
		width : 100
	},{
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		hidden:true,
		width : 100
	},{
		header : '速率',
		dataIndex : 'INTERFACE_RATE',
		width : 100
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		hidden:true,
		width : 100
	},{
		header : '首次发生时间',
		dataIndex : 'FIRST_TIME',
		hidden:true,
		width : 100
	},{
		header : '频次',
		dataIndex : 'AMOUNT',
		hidden:true,
		width : 100
	},{
		header : '最近发生时间',
		dataIndex : 'UPDATE_TIME',
		hidden:true,
		width : 100
	},{
		header : '业务影响',
		dataIndex : 'SERVICE_AFFECTING',
		hidden:true,
		width : 100
	},{
		header : '清除时间',
		dataIndex : 'CLEAR_TIME',
		hidden:true,
		width : 100
	},{
		header : '确认时间',
		dataIndex : 'ACK_TIME',
		hidden:true,
		width : 100
	},{
		header : '确认者',
		dataIndex : 'ACK_USER',
		hidden:true,
		width : 100
	},{
		header : '告警类型',
		dataIndex : 'ALARM_TYPE',
		width : 100,
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
		header : '告警标准名',
		dataIndex : 'PROBABLE_CAUSE',
		hidden:true,
		width : 100
	},{
		header : '割接前快照时间',
		dataIndex : 'SNAPSHOT_TIME_BEFORE',
		width : 100
	},{
		header : '割接后快照时间',
		dataIndex : 'SNAPSHOT_TIME_AFTER',
		width : 100
	},{
		header : '清除状态',
		dataIndex : 'IS_CLEAR',
		hidden:true,
		width : 100,
		renderer : function(value){
			if(value==1){
				return '已清除';
			}else{
				return '未清除';
			}
		}
	},{
				id : 'EVALUATION_SCORE',
				header : '评估量化值',
				dataIndex : 'EVALUATION_SCORE',
				width : 120
				
			}]
});

/**
 * 创建当前告警列表数据源
 */ 
var storeAlarmAfter = new Ext.data.Store({
	url : 'cutover-task!getCurrentAlarms.action',// 数据请求地址
	baseParams : {// 请求参数
		'cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
		'limit':200// 每页显示多少条数据
	},
	reader : new Ext.data.JsonReader({// 数据源数据格式
				totalProperty : 'total',// 记录数
				root : 'rows'// 列表数据
			}, 
			['_id','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME',
			'NATIVE_EMS_NAME','NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NO',
			'DOMAIN','SNAPSHOT_TIME_BEFORE','SNAPSHOT_TIME_AFTER',
			 'PTP_TYPE','INTERFACE_RATE','CTP_NAME','FIRST_TIME','AMOUNT','UPDATE_TIME',
			 'SERVICE_AFFECTING','CLEAR_TIME','ACK_TIME','ACK_USER','ALARM_TYPE','PROBABLE_CAUSE',
			 'IS_CLEAR','ALARM_CATEGORY','ROOM','STATION','EVALUATION_SCORE'])
});



/**
 * 创建表格分页工具栏
 */
var pageToolAlarmAfter = new Ext.PagingToolbar({
	pageSize : 20,
	id:'pageToolAlarmAfter',
	store : storeAlarmAfter,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});
var gridPanelAlarmAfter = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : storeAlarmAfter,
	title:'告警异常',
	cm : cmAlarmAfter,
	height:150,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	forceFit : true,
//	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	bbar : pageToolAlarmAfter
});
  /*****-----------------异常告警结束---------------*****/
    
  /*****-----------------异常倒换---------------*****/
     /**
 * 创建数倒换列表列模型
 */
var cmEventAfter = new Ext.ux.grid.LockingColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : true
	}),
	{
		header : 'ID',
		dataIndex : '_id',
		width : 100,
		hidden : true
	},{
		header : '网元',
		dataIndex : 'ALARM_CATEGORY',
		width : 100,
		renderer : alarmCategoryRenderer
	},{
		header : '保护类别',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 100,
		renderer : function(value,meta,record){
			if(value==1){
				return '<font color="#FF0000">紧急</font>';
			}else if(value==2){
				return '<font color="#FF8000">重要</font>';
			}else if(value==3){
				return '<font color="#FFFF00">次要</font>';
			}else if(value==4){
				return '<font color="#800000">提示</font>';
			}
			

		}
	},{
		header : '保护组名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100
	},{
		header : '保护组类型',
		dataIndex : 'NORMAL_CAUSE',
		hidden:true,
		width : 100
	},{
		header : '倒换原因',
		dataIndex : 'ROOM',
		width : 100
	},{
		header : '发生时间',
		dataIndex : 'STATION',
		width : 100
	},{
		header : '被保护对象',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	},{
		header : '从对象',
		dataIndex : 'NATIVE_EMS_NAME',
		width : 100
	},{
		header : '切换到对象',
		dataIndex : 'NE_NAME',
		width : 100
	}]
});

/**
 * 创建当前告警列表数据源
 */ 
var storeEventAfter = new Ext.data.Store({
	url : 'cutover-task!getCurrentAlarms.action',// 数据请求地址
	baseParams : {// 请求参数
		'cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
		'limit':200// 每页显示多少条数据
	},
	reader : new Ext.data.JsonReader({// 数据源数据格式
				totalProperty : 'total',// 记录数
				root : 'rows'// 列表数据
			}, 
			['_id','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME',
			'NATIVE_EMS_NAME','NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NO',
			'DOMAIN','SNAPSHOT_TIME_BEFORE','SNAPSHOT_TIME_AFTER',
			 'PTP_TYPE','INTERFACE_RATE','CTP_NAME','FIRST_TIME','AMOUNT','UPDATE_TIME',
			 'SERVICE_AFFECTING','CLEAR_TIME','ACK_TIME','ACK_USER','ALARM_TYPE','PROBABLE_CAUSE',
			 'IS_CLEAR','ALARM_CATEGORY','ROOM','STATION'])
});



/**
 * 创建表格分页工具栏
 */
var pageToolEventAfter = new Ext.PagingToolbar({
	pageSize : 20,
	id:'pageToolEventAfter',
	store : storeEventAfter,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});
var gridPanelEventAfter = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : storeEventAfter,
	title:'倒换异常',
	cm : cmEventAfter,
	height:150,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	forceFit : true,
//	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	bbar : pageToolEventAfter
});
  
     
  /*****-----------------异常倒换结束---------------*****/
  var formPanel2 = new Ext.form.FormPanel({
        id: 'formPanel2',
        title:'　',
        margins: '0 0 0 0',
        autoScroll:true,
        border:false,
        split:false,
        items:[gridPanelPmAfter,gridPanelAlarmAfter,gridPanelEventAfter]
      });
      
  var tabs = new Ext.TabPanel({
    	id:'tabs',
    	renderTo:"div10",
    	deferredRender : false, 
//        activeTab : 0,
        items : [
        {
            title: '影响电路',
            visible : true,
            html : '<iframe id = "circuit" name = "circuit" src="circuitsInfluenced.jsp?cutoverTaskId='+cutoverTaskId +'" frameborder="0" width="100%" height="100%"/>'
        },
        {
            title: '端口性能值',
            visible : true,
            listeners:{  
             	activate :function(tab){
             	 	tab.getUpdater().refresh();
             		//刷新页面
             		var id = "PM";
             		var iframe = window.frames[id];
             		if(iframe.refresh){
             			iframe.refresh();
             		}
         
//             		//作用当tab页切换到待办任务列表页面时自动刷新界面
//             		var id = "f_"+title;
//             		if(tab.title == '待办任务列表'){
//             			Ext.get(id).dom.src="../taskInfoModule/undoListOfTask.jsp";
//             		}
            	}
            },
            html : '<iframe id = "PM" name = "PM" src="performanceValue.jsp?cutoverTaskId='+cutoverTaskId +'" frameborder="0" width="100%" height="100%"/>'
        },
         {
            title: '相关告警',
            visible : true,
            listeners:{  
             	activate :function(tab){
             	 	tab.getUpdater().refresh();
             		//刷新页面
             		var id = "alarm";
             		var iframe = window.frames[id];
             		if(iframe.refresh){
             			iframe.refresh();
             		}
         
//             		//作用当tab页切换到待办任务列表页面时自动刷新界面
//             		var id = "f_"+title;
//             		if(tab.title == '待办任务列表'){
//             			Ext.get(id).dom.src="../taskInfoModule/undoListOfTask.jsp";
//             		}
            	}
            },
            html : '<iframe id = "alarm" name = "alarm" src="currentAlarm.jsp?cutoverTaskId='+cutoverTaskId +'" frameborder="0" width="100%" height="100%"/>'
        },
        {
            title: '割接前评估',
            visible : true,
            layout:'fit',
            listeners:{  
             	activate :function(tab){
             	storePm.proxy = new Ext.data.HttpProxy({
					url : "cutover-task!searchPmValueBefore.action"
				});
				storePm.baseParams = {
					"start" : 0,
					"limit" : 200,
					"searchCondition.cutoverTaskId" : cutoverTaskId
				};
				storePm.load({
					callback : function(records, options, success) {
						
					}
				});
				//异常告警加载
				storeAlarm.proxy = new Ext.data.HttpProxy({
					url : "cutover-task!searchAlarmBefore.action"
				});
				storeAlarm.baseParams = {
					"start" : 0,
					"limit" : 200,
					"searchCondition.cutoverTaskId" : cutoverTaskId
				};
				storeAlarm.load({
					callback : function(records, options, success) {
						
					}
				});
				//异常倒换加载
				storeEvent.proxy = new Ext.data.HttpProxy({
					url : "cutover-task!searchEventBefore.action"
				});
				storeEvent.baseParams = {
					"start" : 0,
					"limit" : 200,
					"searchCondition.cutoverTaskId" : cutoverTaskId
				};
				storeEvent.load({
					callback : function(records, options, success) {
						
					}
				});
				setTimeout(function()
				{
				
					if(storePm.getCount()==0 && storeAlarm.getCount()==0 && storeEvent.getCount()==0 )
						formPanel1.setTitle("评估结果：未发现异常，可以正常进行割接。");
					else
					{
						var comment = "评估结果：发现";
						if(storePm.getCount()!=0)
							comment = comment+"性能异常、"
						if(storeAlarm.getCount()!=0)
							comment = comment+"告警异常、"
						if(storePm.getCount()!=0)
							comment = comment+"倒换异常,"
						comment = comment+"请对异常状态进行确认，谨慎进行割接。";
						formPanel1.setTitle(comment);
					}
				}, 
				
				2000);
//             	 	tab.getUpdater().refresh();
//             		//刷新页面
//             		var id = "alarm";
//             		var iframe = window.frames[id];
//             		if(iframe.refresh){
//             			iframe.refresh();
//             		}
         
//             		//作用当tab页切换到待办任务列表页面时自动刷新界面
//             		var id = "f_"+title;
//             		if(tab.title == '待办任务列表'){
//             			Ext.get(id).dom.src="../taskInfoModule/undoListOfTask.jsp";
//             		}
            	}
            },
            items: formPanel1
        },
        {
            title: '割接完成评估',
            visible : true,
            layout:'fit',
            listeners:{  
             	activate :function(tab){
             	storePmAfter.proxy = new Ext.data.HttpProxy({
					url : "cutover-task!searchPmValueAfter.action"
				});
				storePmAfter.baseParams = {
					"start" : 0,
					"limit" : 200,
					"searchCondition.cutoverTaskId" : cutoverTaskId
				};
				storePmAfter.load({
					callback : function(records, options, success) {
						
					}
				});
				//异常告警加载
				storeAlarmAfter.proxy = new Ext.data.HttpProxy({
					url : "cutover-task!searchAlarmAfter.action"
				});
				storeAlarmAfter.baseParams = {
					"start" : 0,
					"limit" : 200,
					"searchCondition.cutoverTaskId" : cutoverTaskId
				};
				storeAlarmAfter.load({
					callback : function(records, options, success) {
						
					}
				});
				//异常倒换加载
				storeEventAfter.proxy = new Ext.data.HttpProxy({
					url : "cutover-task!searchEventAfter.action"
				});
				storeEventAfter.baseParams = {
					"start" : 0,
					"limit" : 200,
					"searchCondition.cutoverTaskId" : cutoverTaskId
				};
				storeEventAfter.load({
					callback : function(records, options, success) {
						
					}
				});
				var param = {
					"searchCondition.cutoverTaskId" : cutoverTaskId
				}
				Ext.Ajax.request({
			    url: 'cutover-task!evaluate.action',
			    method: 'POST',
			    params: param,
			    success: function(response) {
			    	// 将json格式的字符串，转换成对象
			    	var obj = Ext.decode(response.responseText);
			    	if(obj.score!=null)
			    	{
			    		formPanel2.setTitle("评估结果：经过检测，本次割接的量化评分为"+obj.score+"分，评估为"+obj.evaluation+"。");
			    	}
			    },
			    error:function(response) {
			    	tabs.getTabEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    },
			    failure:function(response) {
			    	tabs.getTabEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    }
				});
//             	 	tab.getUpdater().refresh();
//             		//刷新页面
//             		var id = "alarm";
//             		var iframe = window.frames[id];
//             		if(iframe.refresh){
//             			iframe.refresh();
//             		}
         
//             		//作用当tab页切换到待办任务列表页面时自动刷新界面
//             		var id = "f_"+title;
//             		if(tab.title == '待办任务列表'){
//             			Ext.get(id).dom.src="../taskInfoModule/undoListOfTask.jsp";
//             		}
            	}
            },
            items: formPanel2
        }
        ]
    });
    var button1 = new Ext.Panel({
        id: 'button1',
        renderTo:"buttonDiv1",
        margins: '0 0 0 0',
        border:false,
        width:90,
        height:22,
        split:false,
        	items : [
		{
			xtype: 'button',
		    text: "割接任务",
		    width: 60,
		    listeners: { "click": function () {
		    	if(taskStatus == 1)
		   		 {
		   		 	var addWindow=new Ext.Window({
			        id:'addWindow',
			        title:'割接任务设置(设备)',
			        width:1000,
	        		height:500,
			        isTopContainer : true,
			        modal : true,
			        autoScroll:true,
//					maximized:true,
			        html:'<iframe src = "../../cutoverManager/cutoverTask/editTaskByNeAndPtp.jsp?cutoverTaskId='+cutoverTaskId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
			     	});
		   		 }
		   		 else if(taskStatus == 2)
		   		 {
		   		 	var addWindow=new Ext.Window({
			        id:'addWindow',
			        title:'割接任务设置(链路)',
			        width:1000,
	        		height:500,
			        isTopContainer : true,
			        modal : true,
			        autoScroll:true,
//					maximized:true,
			        html:'<iframe src = "../../cutoverManager/cutoverTask/editTaskByLink.jsp?cutoverTaskId='+cutoverTaskId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
			     	});
		   		 }
		    	 addWindow.show();
		     }
		    }
		}
  ]
      });
	
	    var button2 = new Ext.Panel({
        id: 'button2',
        renderTo:"buttonDiv2",
        margins: '0 0 0 0',
        border:false,
        width:100,
        height:22,
        split:false,
        	items : [
		{
			xtype: 'button',
		    text: "查看影响电路",
		    width: 80,
		    listeners: { "click": function () {
		    	Ext.getCmp("tabs").setActiveTab(0);
		     }
		    }
		}
  ]
      });
      
      	var button3 = new Ext.Panel({
        id: 'button3',
        renderTo:"buttonDiv3",
        margins: '0 0 0 0',
        border:false,
        width:100,
        height:22,
        split:false,
        	items : [
		{
			xtype: 'button',
		    text: "割接前快照",
		    width: 70,
		    listeners: { "click": function () {
		    	cutoverBefore();
		    	
		     }
		    }
		}
  ]
      });
      
      	var button4 = new Ext.Panel({
        id: 'button4',
        renderTo:"buttonDiv4",
        margins: '0 0 0 0',
        border:false,
        width:100,
        height:22,
        split:false,
        	items : [
		{
			xtype: 'button',
		    text: "过滤告警",
		    width: 70,
		    listeners: { "click": function () {
		    	filterAlarm();
		    	
		     }
		    }
		}
  ]
      });
      
      	var button5 = new Ext.Panel({
        id: 'button5',
        renderTo:"buttonDiv5",
        margins: '0 0 0 0',
        border:false,
        width:100,
        height:22,
        split:false,
        	items : [
			{
		            xtype     : 'displayfield',
		            height:22,
		            style : 'font-size:12px;',
		            value:"－割接－＞",
		            fieldLabel: ''
		        }
  ]
      });
      
      	var button6 = new Ext.Panel({
        id: 'button6',
        renderTo:"buttonDiv6",
        margins: '0 0 0 0',
        border:false,
        width:100,
        height:22,
        split:false,
        	items : [
		{
			xtype: 'button',
		    text: "割接后快照",
		    width: 70,
		    listeners: { "click": function () {
		    	cutoverAfter();
		    	
		     }
		    }
		}
  ]
      });
      
      	var button7 = new Ext.Panel({
        id: 'button7',
        renderTo:"buttonDiv7",
        margins: '0 0 0 0',
        border:false,
        width:100,
        height:22,
        split:false,
        	items : [
		{
			xtype: 'button',
		    text: "割接完成",
		    width: 70,
		    listeners: { "click": function () {
		    	cutoverComplete();
		    	
		     }
		    }
		}
  ]
      });
//割接前快照
function cutoverBefore()
{
	//panel_2.update('<img  src="../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
		if(snapshotBefore!=null && snapshotBefore!="")
		{
			Ext.Msg.confirm('提示','已经存在快照数据，再次抓取会覆盖之前数据。确认重新抓取？',
	        function(btn){
	        if(btn=='yes'){
	        	tabs.getEl().mask("执行中，请稍候。");
	            Ext.Ajax.request({
			    url: 'cutover-task!snapshotBefore.action',
			    method: 'POST',
			    params: {
			    	'searchCondition.cutoverTaskId' : cutoverTaskId
			    },
			    success: function(response) {
			    tabs.getEl().unmask();
			    	// 将json格式的字符串，转换成对象
			    	var obj = Ext.decode(response.responseText);
			    	Ext.getCmp("tabs").setActiveTab(1);
			    	panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
			    	Ext.Msg.alert("提示",obj.returnMessage);
			    	var pmid = "PM";
             		var iframe = window.frames[pmid];
             		if(iframe)
             		if(iframe.refresh){
             			iframe.refresh();
             		}
             		var alarmid = "alarm";
             		var alarmiframe = window.frames[alarmid];
             		if(alarmiframe)
             		if(alarmiframe.refresh){
             			alarmiframe.refresh();
             		}	
			    },
			    error:function(response) {
			    	tabs.getTabEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    },
			    failure:function(response) {
			    	tabs.getTabEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    }
				});
	        }
	        })
		}
		else
		{
			Ext.Msg.confirm('提示','立即抓取快照数据。',
	        function(btn){
	        if(btn=='yes'){
	        tabs.getEl().mask("执行中，请稍候。");
	            Ext.Ajax.request({
			    url: 'cutover-task!snapshotBefore.action',
			    method: 'POST',
			    params: {
			    	'searchCondition.cutoverTaskId' : cutoverTaskId
			    },
			    success: function(response) {
			    	// 将json格式的字符串，转换成对象
			    	tabs.getEl().unmask();
			    	var obj = Ext.decode(response.responseText);
			    	Ext.getCmp("tabs").setActiveTab(1);
			    	
			    	Ext.Msg.alert("提示",obj.returnMessage);
			    	if(obj.returnResult==1)
			    	{
			    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
			    	}
			    	var pmid = "PM";
             		var iframe = window.frames[pmid];
             		if(iframe)
             		if(iframe.refresh){
             			iframe.refresh();
             		}
             		var alarmid = "alarm";
             		var alarmiframe = window.frames[alarmid];
             		if(alarmiframe)
             		if(alarmiframe.refresh){
             			alarmiframe.refresh();
             		}	
			    },
			    error:function(response) {
			    tabs.getEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    },
			    failure:function(response) {
			    tabs.getEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    }
				});
	        }
	        })
		}
		
}
//割接后快照
function cutoverAfter()
{
	//panel_4.update('<img  src="../../../resource/images/割接任务进度框图灰色4.png" onload="ResizeImage(this,500,90);">');
				if(snapshotAfter!=null && snapshotAfter!="")
		{
			 Ext.Msg.confirm('提示','已经存在快照数据，再次抓取会覆盖之前数据。确认重新抓取？',
	        function(btn){
	        if(btn=='yes'){
	        tabs.getEl().mask("执行中，请稍候。");
	            Ext.Ajax.request({
			    url: 'cutover-task!snapshotAfter.action',
			    method: 'POST',
			    params: {
			    	'searchCondition.cutoverTaskId' : cutoverTaskId
			    },
			    success: function(response) {
			    	// 将json格式的字符串，转换成对象
			    	tabs.getEl().unmask();
			    	var obj = Ext.decode(response.responseText);
			    	Ext.getCmp("tabs").setActiveTab(1);
			    	Ext.Msg.alert("提示",obj.returnMessage);
			    	if(obj.returnResult==1)
			    	{
			    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
				    	panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
			    		panel_4.update('<img  src="../../../resource/images/割接任务进度框图灰色4.png" onload="ResizeImage(this,500,90);">');
			    	}
			    	var pmid = "PM";
             		var iframe = window.frames[pmid];
             		if(iframe)
             		if(iframe.refresh){
             			iframe.refresh();
             		}
             		var alarmid = "alarm";
             		var alarmiframe = window.frames[alarmid];
             		if(alarmiframe)
             		if(alarmiframe.refresh){
             			alarmiframe.refresh();
             		}
			    },
			    error:function(response) {
			    tabs.getEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    },
			    failure:function(response) {
			    tabs.getEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    }
				});
	        }
	        })
		}
		else
		{
			 Ext.Msg.confirm('提示','立即抓取快照数据。',
	        function(btn){
	        if(btn=='yes'){
	        tabs.getEl().mask("执行中，请稍候。");
	            Ext.Ajax.request({
			    url: 'cutover-task!snapshotAfter.action',
			    method: 'POST',
			    params: {
			    	'searchCondition.cutoverTaskId' : cutoverTaskId
			    },
			    success: function(response) {
			    	// 将json格式的字符串，转换成对象
			    	tabs.getEl().unmask();
			    	var obj = Ext.decode(response.responseText);
			    	Ext.Msg.alert("提示",obj.returnMessage);
			    	if(obj.returnResult==1)
			    	{
			    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
				    	panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
			    		panel_4.update('<img  src="../../../resource/images/割接任务进度框图灰色4.png" onload="ResizeImage(this,500,90);">');
			    	}
			    	var pmid = "PM";
             		var iframe = window.frames[pmid];
             		if(iframe)
             		if(iframe.refresh){
             			iframe.refresh();
             		}
             		var alarmid = "alarm";
             		var alarmiframe = window.frames[alarmid];
             		if(alarmiframe)
             		if(alarmiframe.refresh){
             			alarmiframe.refresh();
             		}
			    },
			    error:function(response) {
			    tabs.getEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    },
			    failure:function(response) {
			    tabs.getEl().unmask();
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("提示",response.responseText);
			    }
				});
	        }
	        })
		}

}
//过滤告警
function filterAlarm()
{
//	panel_3.update('<img  src="../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
	
	 Ext.Msg.confirm('提示','综告输出接口上相关网元和端口告警将被过滤。',
	        function(btn){
	        if(btn=='yes'){
	        tabs.getEl().mask("执行中，请稍候。");
	            	Ext.Ajax.request({
				    url: 'cutover-task!filterAlarm.action',
				    method: 'POST',
				    params: {
				    	'searchCondition.cutoverTaskId' : cutoverTaskId
				    },
				    success: function(response) {
				    	// 将json格式的字符串，转换成对象
				    	tabs.getEl().unmask();
				    	var obj = Ext.decode(response.responseText);
				    	Ext.Msg.alert("提示",obj.returnMessage);
				    	if(obj.returnResult==1)
				    	{
				    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
				    		panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
				    	}
				    	
				    },
				    error:function(response) {
				    tabs.getEl().unmask();
				    	top.Ext.getBody().unmask();
			        	Ext.Msg.alert("提示",response.responseText);
				    },
				    failure:function(response) {
				    tabs.getEl().unmask();
				    	top.Ext.getBody().unmask();
			        	Ext.Msg.alert("提示",response.responseText);
				    }
				});
	        }
	        })

}
//割接完成
function cutoverComplete()
{
//	panel_5.update('<img  src="../../resource/images/割接任务进度框图灰色5.png" onload="ResizeImage(this,500,90);">');
	
		 Ext.Msg.confirm('提示','确认割接任务完成。将对综告释放过滤告警，更新基准值和生成割接报告。确认？',
	        function(btn){
	        if(btn=='yes'){
	        tabs.getEl().mask("执行中，请稍候。");
	            		Ext.Ajax.request({
					    url: 'cutover-task!cutoverComplete.action',
					    method: 'POST',
					    params: {
					    	'searchCondition.cutoverTaskId' : cutoverTaskId
					    },
					    success: function(response) {
					    	// 将json格式的字符串，转换成对象
					    	tabs.getEl().unmask();
					    	var obj = Ext.decode(response.responseText);
					    	Ext.Msg.alert("提示",obj.returnMessage);
					    	if(obj.returnResult==1)
					    	{
					    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
				    			panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
			    				panel_4.update('<img  src="../../../resource/images/割接任务进度框图灰色4.png" onload="ResizeImage(this,500,90);">');
			    				panel_5.update('<img  src="../../../resource/images/割接任务进度框图灰色5.png" onload="ResizeImage(this,500,90);">');
					    	}
					    	
					    },
					    error:function(response) {
					    tabs.getEl().unmask();
					    	top.Ext.getBody().unmask();
				        	Ext.Msg.alert("提示",response.responseText);
					    },
					    failure:function(response) {
					    tabs.getEl().unmask();
					    	top.Ext.getBody().unmask();
				        	Ext.Msg.alert("提示",response.responseText);
					    }
					});
	        }
	        })

} 
    var toolbarPanel = new Ext.Toolbar({
			id : "toolbarPanel",
//			renderTo:"buttonDiv",
			height:33,
			bodyStyle : 'border-style:solid;border-color:#4F94CD',
			    items: [
			        
			        {
            			xtype: 'tbspacer',
            			width: 20 // same as 'tbsplitbutton'				
        			},
			        {
			            // xtype: 'button', // default for Toolbars, same as 'tbbutton'
						cls:'x-btn-over',
						height:26,	
			            text: '割接任务'
			        },'-',
			        {
            			xtype: 'tbspacer',
            			width: 25 // same as 'tbsplitbutton'				
        			},
			        {
			            // xtype: 'button', // default for Toolbars, same as 'tbbutton'
						cls:'x-btn-over',
						height:26,		
			            text: '查看影响电路'
			        },'-',
			        {
            			xtype: 'tbspacer',
            			width: 30 // same as 'tbsplitbutton'				
        			},
			        {
			            // xtype: 'button', // default for Toolbars, same as 'tbbutton'
						cls:'x-btn-over',		
			            text: '割接前快照',
			            height:26,
			            handler: function(){panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');cutoverBefore();}
			        },'-',
			        {
            			xtype: 'tbspacer',
            			width: 45 // same as 'tbsplitbutton'				
        			},
			        {
			            // xtype: 'button', // default for Toolbars, same as 'tbbutton'
						cls:'x-btn-over',
						height:26,		
			            text: '过滤告警',
			            handler: function(){panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');filterAlarm();}
			        },'-',
			        {
            			xtype: 'tbspacer',
            			width: 60 // same as 'tbsplitbutton'				
        			},
			        '－割接－＞',
			        {
            			xtype: 'tbspacer',
            			width: 60 // same as 'tbsplitbutton'				
        			},
			        {
			            // xtype: 'button', // default for Toolbars, same as 'tbbutton'
						cls:'x-btn-over',		
			            text: '割接后快照',
			            height:26,
			            handler: function(){panel_4.update('<img  src="../../../resource/images/割接任务进度框图灰色4.png" onload="ResizeImage(this,500,90);">');cutoverAfter();}
			        },'-',
			        {
            			xtype: 'tbspacer',
            			width: 50 // same as 'tbsplitbutton'				
        			},
        			{
			            // xtype: 'button', // default for Toolbars, same as 'tbbutton'
						cls:'x-btn-over',	
						height:26,	
			            text: '割接完成',
			            handler: function(){panel_5.update('<img  src="../../../resource/images/割接任务进度框图灰色5.png" onload="ResizeImage(this,500,90);">');cutoverComplete();}
			        }
			    ]
			
		});
		//初始化割接任务进度状态
	Ext.Ajax.request({
	    url: 'cutover-task!initCutoverTaskProcess.action',
	    method: 'POST',
	    params: {
	    	'searchCondition.cutoverTaskId' : cutoverTaskId
	    },
	    success: function(response) {
	    	// 将json格式的字符串，转换成对象
	    	
	    	var obj = Ext.decode(response.responseText);
	    	snapshotBefore = obj.snapshotBefore;
	    	snapshotAfter = obj.snapshotAfter;
	    	if(obj.snapshotBefore!=null && obj.snapshotBefore!="")
	    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
	    	if(obj.filterAlarm!=null && obj.filterAlarm!="")
	    	{
	    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
	    		panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
	    	}
	    		
	    	if(obj.snapshotAfter!=null && obj.snapshotAfter!="")
	    	{
	    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
	    		panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
	    		panel_4.update('<img  src="../../../resource/images/割接任务进度框图灰色4.png" onload="ResizeImage(this,500,90);">');
	    	}
	    	if(obj.completeCutoverTask!=null && obj.completeCutoverTask!="")
	    	{
	    		panel_2.update('<img  src="../../../resource/images/割接任务进度框图灰色2.png" onload="ResizeImage(this,500,90);">');
	    		panel_3.update('<img  src="../../../resource/images/割接任务进度框图灰色3.png" onload="ResizeImage(this,500,90);">');
	    		panel_4.update('<img  src="../../../resource/images/割接任务进度框图灰色4.png" onload="ResizeImage(this,500,90);">');
	    		panel_5.update('<img  src="../../../resource/images/割接任务进度框图灰色5.png" onload="ResizeImage(this,500,90);">');
	    	}
	    	if(obj.snapshotBefore!=null && obj.snapshotBefore!="")
	    		Ext.getCmp("tabs").setActiveTab(1);	
	    	else if(obj.filterAlarm!=null && obj.filterAlarm!="")
	    		Ext.getCmp("tabs").setActiveTab(1);	
	    	else
	    		Ext.getCmp("tabs").setActiveTab(0);
	    },
	    error:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("提示",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("提示",response.responseText);
	    }
	});
		Ext.Ajax.request({
		url: 'fault!getAlarmColorSet.action',
	    method: 'POST',
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	// 告警灯显示
	    	var alarmLightHidden = obj.alarmLightHidden;
	    	if(alarmLightHidden!=''){
	    		var arr = alarmLightHidden.split(',');
	    		
	    	}
	    	
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
	var win = new Ext.Viewport({
	id : 'win'
	});
	Ext.getCmp("tabs").setHeight(Ext.getCmp('win').getHeight()-125);
	


});