//-------------------电路任务用---------------
var TASK_PARAM = {
 TASK_CIR_LIST : null,
 TASK_NE_LIST : null,
 TASK_PTP_INFO_LIST : null,
 TASK_CTP_INFO_LIST : null,
 TASK_PTP_LIST : null,
 TASK_CTP_LIST : null,
 TASK_UNIT_LIST : null,
 IS_LOADED : false
};
Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
);

/**
 * 定义高级查询窗口的初始化参数
 */
// 告警源选择(左侧树)
//var treeCheck = '';
// 告警源过滤条件

//锁定滚动条--刷新后滚动条不移动
Ext.override( Ext.ux.grid.LockingGridView,{
	 refresh : function(headersToo){
	    	
	        this.fireEvent('beforerefresh', this);
	        this.grid.stopEditing(true);
	        var result = this.renderBody();
	        this.mainBody.update(result[0]).setWidth(this.getTotalWidth());
	        this.lockedBody.update(result[1]).setWidth(this.getLockedWidth());
	        if(headersToo === true){
	            this.updateHeaders();
	            this.updateHeaderSortState();
	        }
	        
	        this.processRows(0, true);
//	        this.layout();	        
	        this.applyEmptyText();
	        this.fireEvent('refresh', this);
	        
	    }
});

/**-------------------留着吧--------------------*/
var pageSize=500;

var alarmSource = 'all';
// 告警级别
var alarmLevelCheck = '';
// 确认状态
var confirmStatusCheck = '';
// 清除状态
var clearStatusCheck = '';
// 告警名称->厂家
var factoryValue = '-1';
// 告警名称->告警名称
var alarmName = '';
// 告警类型
var alarmTypeCheck = '';
// 首次发生时间
var firstOneStatus = true;
var firstTwoStatus = false;
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
// 告警反转
var reversalCheck = '';
/**
 * 定义定时器的全局变量
 */
// 定时器
var t = "";
// 告警灯级别
var alarmLvStatus = '';
/**
 * 定义默认值设置标志(只有首次加载时赋值)
 */
var emsGroupDefaultStatus = true;
var emsDefaultStatus = true;
var neDefaultStatus = true;
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

var voiceFlag=false;
// 告警过滤器ID
var filterId = -1;
var defaultFilterId = -1;
// 告警状态ID
var statusId = 1; //1:全部
var defaultStatusId = 1;
// 对象树最大选择数
var maxSelectNum = 50;

//告警收敛显示选择标志
var convergeChecked=false;
/**----------------------------------------*/



/**
 * 创建表格选择模型(多选、单选.....)
 */ 
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
////	singleSelect ：true,// 表示只能单选，默认false,
////	sortable : true,//表示选择框列可以排序，默认fasle
//});
//sm.sortLock();
//var sm = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
/**
 * 创建数告警列表列模型
 */
var cm = new Ext.ux.grid.LockingColumnModel({
	stateId:stateIdvar,  
//var cm = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
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






/**
 * 创建当前告警列表数据源
 */ 
var store = new Ext.data.Store({
	url : 'fault!getCurrentAlarms.action',// 数据请求地址
	baseParams : {// 请求参数
		'jsonString' : Ext.encode({'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,'emsGroupId':staticEmsGroupId,'alarmLv':alarmLvStatus,'ptpId':view_ptpId,'unitId':view_unitId,'filterId':filterId,'statusId':statusId}),// 把对象转成JSON格式字符串
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
			 'CLEAR_TIME','DURATION','ACK_TIME','ACK_USER','ALARM_TYPE'])
});

store.on("load", function(st, recs, opt){
	var lvls = ["", Beeper.CR, Beeper.MJ, Beeper.MN, Beeper.WR];
	var flags =[];
	var curAlarm = "";
	Ext.each(recs, function(v){
		flags[v.get("PERCEIVED_SEVERITY")] |= (v.get("IS_CLEAR")==2 && v.get("IS_ACK")==2 );
		return true;
	});
	curAlarm = "";
	for(var i = 0; i<5;i++){
		if(!!flags[i]){
			curAlarm = lvls[i];
			Beeper.loop(curAlarm, 3);
			return;
		}
	}
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
 * 创建表格第一行工具栏
 */
var tbar = new Ext.Toolbar({
	border : false,
	items : [ '-',{
	        text: '所有告警', 
	        privilege : viewAuth,
	        handler : function(){
	        	queryCurrentAlarm(TASK_PARAM.TASK_NE_LIST,NodeDefine.NE);
	        }
	    },'-',{
	        text: '确认',
	        icon : '../../../resource/images/btnImages/tick.png',
	        privilege : modAuth,
	        handler : function (){
	        	manualConfirm();
	        }
	    },'-',{
	        text: '反转',
//	        icon : '../../resource/images/btnImages/user_suit.png',
	        privilege : modAuth,
	        handler : function (){
	        	reversal();
	        }
	    },'-',{
	        text: '告警刷新', 
	        privilege : viewAuth,
	        handler : function (){
				// 所有告警，与初次进入页面显示内容相同
				getCurrentAlarms();
			} 
	    },'-',{
	        text: '相关电路', 
	        privilege : viewAuth,
	        handler : function(){
	        	gotoCircuit();
	        }
	    },'-',{
	    	id : 'autoRefresh',
	        xtype : 'checkbox',
	        boxLabel : '自动刷新',
	        privilege : viewAuth,
	        listeners : {
				check : function(checkbox,b){ 
					if(b){
						if(!convergeChecked){
							editGridPanel.getView().disableAutoScroll();
						} 
						t = window.setInterval('autoRefresh()',10000);
					}else{
						window.clearInterval(t);
						if(!convergeChecked){
							editGridPanel.getView().enableAutoScroll();
						} 
					}
				}
			}
	    },'-',{
	    	id : 'converge',
	        xtype : 'checkbox',
	        boxLabel : '告警收敛',
	        privilege : viewAuth,
	        listeners : {
				check : function(checkbox,b){
					if(b){
						convergeChecked=true;
					   	queryCurrentAlarm(nodeIdStr_page,nodeLevel_page);
                        Ext.getCmp('gridPanel').getLayout().setActiveItem(1);   
					}else{
						convergeChecked=false;
					   	queryCurrentAlarm(nodeIdStr_page,nodeLevel_page);
                        Ext.getCmp('gridPanel').getLayout().setActiveItem(0); 
					}
                    Ext.getCmp('gridPanel').doLayout();
				}
			}
	    }]
});



/**
 * 创建一个可编辑的表格
 */
var editGridPanel = new Ext.grid.GridPanel({
	region : 'center',
	store : store,
	id:"gaojing",
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	stripeRows : true, // 交替行效果
	forceFit : true,
	loadMask : true,
	stateId:stateIdvar,  
	stateful:true,
	view : new Ext.ux.grid.LockingGridView({
		scrollDelay:false,
		cacheSize:100,
		syncHeights: false,
		cleanDelay:500//,
		//autoScrollTop:false
	}),
//	view : new Ext.ux.grid.BufferView({}),
	tbar : tbar,
	bbar : pageTool
});

editGridPanel.on('rowdblclick', function(grid, rowIndex, e){
	grid.getSelectionModel().selectRow(rowIndex);
	gotoBayface();
});
var nodeIdStr_page='';
var nodeLevel_page='';

/**
 * @@@分权分域到网元@@@
 * 当前告警基础查询
 */
function queryCurrentAlarm(nodeIdStr,nodeLevel){
	staticNeId = '';
	staticEmsGroupId = '';
	staticEmsId = '';
	staticSubNeId = '';
	view_ptpId = '';
	view_unitId = '';
	nodeIdStr_page = nodeIdStr;
	nodeLevel_page = nodeLevel;
	// 选择对象数据组织
	if(nodeLevel==NodeDefine.EMSGROUP){
		staticEmsGroupId = nodeIdStr;
	}else if(nodeLevel==NodeDefine.EMS){
		staticEmsId = nodeIdStr;
	}else if(nodeLevel==NodeDefine.SUBNET){
		staticSubNeId = nodeIdStr;
	}else if(nodeLevel==NodeDefine.NE){
		staticNeId = nodeIdStr;
	}else if(nodeLevel==NodeDefine.PTP){
		view_ptpId = nodeIdStr;
	}
		
	if(convergeChecked){ 
		storeCg.baseParams = {// 请求参数
				'jsonString' : Ext.encode({'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,
					'emsGroupId':staticEmsGroupId,'alarmLv':alarmLvStatus,'ptpId':view_ptpId,'unitId':view_unitId,
					'filterId':filterId,'statusId':statusId,'isConverge':convergeChecked}),// 把对象转成JSON格式字符串
				'limit':pageSize// 每页显示多少条数据
			},
		storeCg.load({
			callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
				if (!success) {
					Ext.getBody().unmask();
					Ext.Msg.alert('错误', '查询失败！请重新查询');
				}else{
					Ext.getBody().unmask(); 
				}
			}
		});
	}else{ 	
		store.baseParams = {// 请求参数
			'jsonString' : Ext.encode({'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,
				'emsGroupId':staticEmsGroupId,'alarmLv':alarmLvStatus,'ptpId':view_ptpId,'unitId':view_unitId,
				'filterId':filterId,'statusId':statusId,'isConverge':convergeChecked}),// 把对象转成JSON格式字符串
			'limit':pageSize// 每页显示多少条数据
		},
		store.load({
			callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
				if (!success) {
					Ext.getBody().unmask();
					Ext.Msg.alert('错误', '查询失败！请重新查询');
				}else{
					Ext.getBody().unmask(); 
				}
			}
		});
	}
}



/**
 * 告警自动刷新
 */
function autoRefresh(){
	panel=activeGridPane(convergeChecked);
	// 先记录当前选中的记录
	var cell = panel.getSelectionModel().getSelections();

	// 如果页面没有发现生变化，去掉读取中标记
	if(convergeChecked){ 
		storeCg.setBaseParam('start',pageToolCg.cursor);
		storeCg.load({
			callback : function(r, options, success) {
				if (success) {
					var num = new Array();
					if(cell.length>0){
						for(var i = 0 ; i <cell.length;i++){
							var index = storeCg.find("_id", cell[i].get('_id'));
							num[i]=index;
						}					
					}
					panel.getSelectionModel().selectRows(num);
					// 如果不进行布局layout，则此语句可以去掉
//					panel.getView().scroller.dom.scrollLeft = left;
				} else {
					Ext.Msg.alert('错误', '查询失败！请重新查询');
				}
			}
		});
	}else{
		// 如果不进行布局layout，则此语句可以去掉
		var left =panel.getView().scroller.dom.scrollLeft;
		store.setBaseParam('start',pageTool.cursor);
		store.load({
			callback : function(r, options, success) {
				if (success) {
					var num = new Array();
					if(cell.length>0){
						for(var i = 0 ; i <cell.length;i++){
							var index = store.find("_id", cell[i].get('_id'));
							num[i]=index;
						}					
					}
					panel.getSelectionModel().selectRows(num);
					// 如果不进行布局layout，则此语句可以去掉
					panel.getView().scroller.dom.scrollLeft = left;
				} else {
					Ext.Msg.alert('错误', '查询失败！请重新查询');
				}
			}
		});
	} 

}


/**
 * 告警手动确认
 */
function manualConfirm(){
	panel=activeGridPane(convergeChecked); 
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length>=1){
		Ext.Msg.confirm('提示','你确定要确认所选告警？',function(btn){       
			if(btn=='yes'){
				var ids = '';
				for ( var i = 0; i < records.length; i++) {
					 // 如果未确认
					if(records[i].get('IS_ACK')==2){
						ids += records[i].get('_id') + ',';
					}
				}
				ids = ids.substring(0, ids.lastIndexOf(','));
				if(ids!=''){
					Ext.Ajax.request({
					    url: 'fault!alarmManualConfirm.action',
					    method: 'POST',
					    params: {
					    	'jsonString' : Ext.encode({'ids':ids})// 把对象转成JSON格式字符串
					    },
					    success: function(response) {
					    	queryCurrentAlarm(nodeIdStr_page,nodeLevel_page);
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
 * 查询所有当前告警(相当于重新进入当前告警页面)
 */
function getCurrentAlarms(){
	if(taskType=='设备'){
		staticNeId='';
		staticSubNeId='';
		staticEmsId='';
		staticEmsGroupId='';
		queryCurrentAlarm(nodeIdStr_page,nodeLevel_page);
	}else if(taskType == '电路'){
		initAlarmCir();
	}
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


function gotoCircuit(){
	panel=activeGridPane(convergeChecked);
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
/*
function gotoBayface(){
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length==1){
		var i=0;
		var neId;
		var neName;
		var speShelfNo;
		neId = records[i].get('NE_ID');
		if(neId == '' || neId == null){
			Ext.Msg.alert('提示', '无相关网元信息');
		}else{
			neName = records[i].get('NE_NAME');
			speShelfNo = records[i].get('SHELF_NO');
			if (speShelfNo=='') {
				parent.addTabPage('../viewManager/bayface.jsp?neId='+neId, "网元:"+neName, authSequence);
			} else {
				parent.addTabPage('../viewManager/bayface.jsp?neId='+neId+'&speShelfNo='+speShelfNo, "网元:"+neName, authSequence);
			}

		}
	}else if(records.length<1){
		Ext.Msg.alert('提示', '请选择告警');
	}else{
		Ext.Msg.alert('提示', '请勿多选');
	}

}
*/
/**
 * 告警反转
 */
function reversal(){
	panel=activeGridPane(convergeChecked);
	var records = editGridPanel.getSelectionModel().getSelections();
	if(records.length>=1){
		Ext.Msg.confirm('提示','告警反转操作，将会抑制告警显示，直到该告警状态改变，是否确定？',function(btn){       
			if(btn=='yes'){
				var ids = '';
				for ( var i = 0; i < records.length; i++) {
					 // 如果未清除
//					if(records[i].get('IS_CLEAR')==2){
						ids += records[i].get('_id') + ',';
//					}
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
					    	queryCurrentAlarm(nodeIdStr_page,nodeIdLevel_page);
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


/*----------------------------增加收敛告警显示部分的grid---------------------------*/

var gridPanel = {
	id:'gridPanel',
    region: 'center',
    xtype: 'panel', 
    layout : 'card',
    activeItem :1, 
    border:false,
    items : [{
        id: 'card-1',
        layout : 'fit',
        border:false,
        items: [editGridPanel]
    },{
        id: 'card-2',
        layout : 'fit',
        border:false,
        items: [treeGridPanel]
    }],
    tbar : tbar
};

var panel=editGridPanel;
function activeGridPane(convergeChecked){
	if(convergeChecked){
		return treeGridPanel;
	}else{
		return editGridPanel;
	}
}

function currentAlarm4Circuit(neList,ptpList,ctpList){
	store.proxy = new Ext.data.HttpProxy({
	    method: 'POST',
	    url: 'impt-protect-task!getAlarmForNodesOfCir.action'
	});
	storeCg.proxy = new Ext.data.HttpProxy({
	    method: 'POST',
	    url: 'impt-protect-task!getAlarmForNodesOfCir.action'
	});
	var storeP;
	if(convergeChecked){ 
		storeP = storeCg;
	}else{ 	
		storeP = store;
	}
	storeP.baseParams = {'neList':neList,'ptpList':ptpList,'ctpList':ctpList,"convergeChecked":convergeChecked,start:0,limit:200};
	storeP.load({
		callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}
		}
	});
}
/**
 * Ext初始化
 */
Ext.onReady(function() {
	Ext.Ajax.timeout = 900000;
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = '../../../resource/ext/resources/images/default/s.gif';
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	init();
	if(taskType=='设备'){
		Ext.getBody().mask('请稍后...');
		Ext.Ajax.request({
		    url: 'impt-protect-task!getItemsOfEquipTask.action',
		    method: 'POST',
		    params: {'taskId' : taskId },
		    success: function(response) {
		    	Ext.getBody().unmask();
		    	var obj = Ext.decode(response.responseText);
		    	if(!!obj.linkList){
			    	var ptpList = new Array();
			    	for(var i=0;i<obj.linkList.length;i++){
			    		if(ptpList.indexOf(obj.linkList[i].aPtpId)==-1)
			    			ptpList.push(obj.linkList[i].aPtpId);
			    		if(ptpList.indexOf(obj.linkList[i].zPtpId)==-1)
			    			ptpList.push(obj.linkList[i].zPtpId);
			    	}
			    	TASK_PARAM.TASK_PTP_LIST = ptpList;	
		    	}
		    	TASK_PARAM.TASK_NE_LIST = obj.neIdList;
		    	TASK_PARAM.IS_LOADED = true;
		    	queryCurrentAlarm(TASK_PARAM.TASK_NE_LIST.toString(),NodeDefine.NE);
		    },
		    error:function(response) {
		    	Ext.getBody().unmask();
	        	Ext.Msg.alert("错误",response.responseText);
		    },
		    failure:function(response) {
		    	Ext.getBody().unmask();
	        	Ext.Msg.alert("错误",response.responseText);
		    }
		});
	}else if(taskType=='电路'){
//		if(parent.TASK_PARAM.IS_LOADED){
//			TASK_PARAM = parent.TASK_PARAM;
//			currentAlarm4Circuit(TASK_PARAM.TASK_NE_LIST,TASK_PARAM.TASK_PTP_LIST);
//		}else{
			Ext.getBody().mask('请稍后...');
			Ext.Ajax.request({
			    url: 'impt-protect-task!getItemsOfCircuitTask.action',
			    method: 'POST',
			    params: {'taskId' : taskId },
			    success: function(response) {
			    	Ext.getBody().unmask();
			    	var obj = Ext.decode(response.responseText);
			    	TASK_PARAM.TASK_CIR_LIST = obj.cirInfoList;
			    	TASK_PARAM.TASK_CTP_LIST = obj.ctpList;
			    	TASK_PARAM.TASK_PTP_LIST = obj.ptpList;
			    	TASK_PARAM.TASK_NE_LIST = obj.neList;
			    	TASK_PARAM.TASK_UNIT_LIST = obj.unitList;
			    	TASK_PARAM.TASK_PTP_INFO_LIST = obj.ptpInfoList;
			    	TASK_PARAM.TASK_CTP_INFO_LIST = obj.ctpInfoList;
			    	TASK_PARAM.IS_LOADED = true;
			    	currentAlarm4Circuit(TASK_PARAM.TASK_NE_LIST,TASK_PARAM.TASK_PTP_LIST,TASK_PARAM.TASK_CTP_LIST);
			    },
			    error:function(response) {
			    	Ext.getBody().unmask();
		        	Ext.Msg.alert("错误",response.responseText);
			    },
			    failure:function(response) {
			    	Ext.getBody().unmask();
		        	Ext.Msg.alert("错误",response.responseText);
			    }
			});
		}
//	}
	new Ext.Viewport({
		id : 'win',
		layout : 'border',
		border : false,
		items : [gridPanel]
	});
	Ext.getCmp('gridPanel').getLayout().setActiveItem(0); 
	Beeper.init();
});