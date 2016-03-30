/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/

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
	        //this.layout();	        
	        this.applyEmptyText();
	        this.fireEvent('refresh', this);
	        
	    }
});

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
// 告警收敛显示选择标志
var convergeChecked=false;
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
		header : '最近持续时间',
		dataIndex : 'RECENT_DURATION',
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
 * 创建告警过滤器数据源
 */
var filterStore = new Ext.data.Store({
	url : 'fault!getAlarmFiltersSummaryByUserId.action',
	reader : new Ext.data.JsonReader({
		root : "rows",
		fields: ["FILTER_ID", "FILTER_NAME", "STATUS"],
	})
});

/**
 * 创建告警过滤器下拉框
 */
var filterCombo = new Ext.form.ComboBox({
	id : 'filterCombo',
	store : filterStore,
	valueField : 'FILTER_ID',
	displayField : 'FILTER_NAME',
	triggerAction : 'all',	
	editable : false,
	allowBlank : false,
	resizable: true,
	width :150,	
});

/**
 * 加载告警过滤器数据源
 */
filterStore.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '过滤器数据源查询失败！请重新查询');
		}else{
			var firstValue = -1; //-1:无
			for (var i = 0; i< records.length; i++){
				if (records[i].get('STATUS') == 1){ //1:启用
					firstValue = records[i].get('FILTER_ID');
					defaultFilterId = firstValue;
				} 
			}
			Ext.getCmp('filterCombo').setValue(firstValue);
		}
	}
});


/**
 * 创建告警状态数据源
 */
var arrayStatus = [[1,'全部'],[2,'已清除'],[3,'未清除'],[4,'已确认'],[5,'已确认已清除'],[6,'已确认未清除'],[7,'未确认'],[8,'已清除未确认'],[9,'未确认未清除']];
var statusStore = new Ext.data.ArrayStore({
	fields: ["retrunValue", "displayText"],
	data: arrayStatus
});

/**
 * 创建告警状态下拉框
 */
var statusCombo = new Ext.form.ComboBox({
	id : 'statusCombo',
	store : arrayStatus,
	mode: 'local',
	triggerAction : 'all',	
	editable : false,
	allowBlank : false,
	resizable: true,
	width :100,	
});

/**
 * 创建当前告警列表数据源
 */ 
var store = new Ext.data.Store({
	url : 'fault!getCurrentAlarms.action',// 数据请求地址
//	baseParams : {// 请求参数
//		'jsonString' : Ext.encode({'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,'emsGroupId':staticEmsGroupId,
//									'alarmLv':alarmLvStatus,'ptpId':view_ptpId,'unitId':view_unitId,'filterId':filterId,
//									'statusId':statusId,'isConverge':convergeChecked}),// 把对象转成JSON格式字符串
//		'limit':pageSize// 每页显示多少条数据
//	},
	reader : new Ext.data.JsonReader({// 数据源数据格式
				totalProperty : 'total',// 记录数
				root : 'rows'// 列表数据
			}, 
			['_id','OBJECT_TYPE','PTP_ID','NE_ID','UNIT_ID','BASE_LINK_ID','BASE_PRO_GROUP_ID','SHELF_NO','CTP_ID',
			 'IS_ACK','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','EMS_NAME','SUBNET_NAME',
			 'NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NAME','DOMAIN','PTP_TYPE',
			 'INTERFACE_RATE','CTP_NAME','IS_CLEAR','FIRST_TIME','AMOUNT','NE_TIME','SERVICE_AFFECTING','NE_TYPE',
			 'CLEAR_TIME','DURATION','RECENT_DURATION','ACK_TIME','ACK_USER','ALARM_TYPE','CONVERGE_FLAG','HAVE_CHILD'])
});

store.on("load", function(st, recs, opt){
	//先清除状态
	Beeper.clear();
	//重新计算数据
	Ext.each(recs, function(v){
		if((v.get("IS_CLEAR")==2 && v.get("IS_ACK")==2 )){
			Beeper.add(v.get("EMS_NAME"), v.get("PERCEIVED_SEVERITY"));
		}
		return true;
	});
	//播放
	Beeper.play();
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
var firstTbar = new Ext.Toolbar({
	border : false,
	items : [ '-','过滤器：', filterCombo, '-', '状态：', statusCombo, '-',{
			text: '查询',
			icon : '../../resource/images/btnImages/search.png',
			privilege : viewAuth,
			handler : function (){
				//延迟半秒执行，网元下拉框中加载太慢导致的取值不正确
				(function(){
					queryCurrentAlarm();
				}).defer(500);
			} 
		},{
	        text: '高级查询', 
	        privilege : viewAuth,
	        handler : function (){
				// 高级查询
				queryCurrentAlarm_high();
			} 
	    },{
	        text: '全部告警', 
	        privilege : viewAuth,
	        handler : function (){
				// 所有告警，与初次进入页面显示内容相同
				getCurrentAlarms();
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
					   	queryCurrentAlarm();
                        Ext.getCmp('gridPanel').getLayout().setActiveItem(1);   
					}else{
						convergeChecked=false;
					   	queryCurrentAlarm();
                        Ext.getCmp('gridPanel').getLayout().setActiveItem(0); 
					}
                    Ext.getCmp('gridPanel').doLayout();
				}
			}
	    },'-',{
	    	id : 'soundSwitch',
	    	icon : '../../resource/images/btnImages/sound.png',
	    	disabled:false,
	        //xtype : 'checkbox',
	        //boxLabel : '声音提示',
	        //checked : localStorage.getItem("playSound")==null?true:localStorage.getItem("playSound")=="true",
	        privilege : viewAuth,
	        listeners : {
				/*check : function(checkbox,b){
					Beeper.canPlay = b;
					localStorage.setItem("playSound", b);
				},*/
	        	beforerender : function(but){
	        		if(localStorage.getItem("playSound")==null?true:localStorage.getItem("playSound")=="true"){
	        			Ext.getCmp('soundSwitch').setIcon('../../resource/images/btnImages/sound.png');
			    		Beeper.canPlay = true;
						localStorage.setItem("playSound", true);
						voiceFlag=false;
	        		}else{
	        			Ext.getCmp('soundSwitch').setIcon('../../resource/images/btnImages/sound_mute.png');
			    		Beeper.canPlay = false;
						localStorage.setItem("playSound", false);
						voiceFlag=true;
	        		}
	        	},
			    click : function(but,event){
			    	if(voiceFlag){
			    		Ext.getCmp('soundSwitch').setIcon('../../resource/images/btnImages/sound.png');
			    		Beeper.canPlay = true;
						localStorage.setItem("playSound", true);
						voiceFlag=false;
			    	}else{
			    		Ext.getCmp('soundSwitch').setIcon('../../resource/images/btnImages/sound_mute.png');
			    		Beeper.canPlay = false;
						localStorage.setItem("playSound", false);
						voiceFlag=true;
			    	}
					Beeper.toggle();
				}
			}
	    },'-','->',{// 告警灯->紧急告警
	    	id : 'PS_CRITICAL',
			height : 30,
//			cls : 'button1',
			width : 60,
			onMouseOver : function(e){
			},
			privilege : viewAuth,
			listeners : {
				beforerender : function(but){
					getAllLvAlarmCount(isToday);
				},
				click : function(but,event){
					Ext.getCmp('statusCombo').setValue(defaultStatusId);
					if(statusId!=defaultStatusId)
						getAllLvAlarmCount(isToday);
					modifyLightStatus(1,but);
					queryCurrentAlarmByAlarmLv(alarmLvStatus, isToday);
				}
			}
		},{// 告警灯->重要告警
			id : 'PS_MAJOR',
			style : 'margin-left:3px;',
			height : 30,
//			cls : 'button2',
			width : 60,
			onMouseOver : function(e){
			},
			privilege : viewAuth,
			listeners : {
				click : function(but,event){
					Ext.getCmp('statusCombo').setValue(defaultStatusId);
					if(statusId!=defaultStatusId)
						getAllLvAlarmCount(isToday);
					modifyLightStatus(2,but);
					queryCurrentAlarmByAlarmLv(alarmLvStatus, isToday);
				}
			}
		},{// 告警灯->次要告警
			id : 'PS_MINOR',
			style : 'margin-left:3px;',
			height : 30,
//			cls : 'button3',
			width : 60,
			onMouseOver : function(e){
			},
			privilege : viewAuth,
			listeners : {
				click : function(but,event){
					Ext.getCmp('statusCombo').setValue(defaultStatusId);
					if(statusId!=defaultStatusId)
						getAllLvAlarmCount(isToday);
					modifyLightStatus(3,but);
					queryCurrentAlarmByAlarmLv(alarmLvStatus, isToday);
				}
			}
		},{// 告警灯->提示告警
			id : 'PS_WARNING',
			style : 'margin-left:3px;',
			height : 30,
//			cls : 'button4',
			width : 60,
			onMouseOver : function(e){
			},
			privilege : viewAuth,
			listeners : {
				click : function(but,event){
					Ext.getCmp('statusCombo').setValue(defaultStatusId);
					if(statusId!=defaultStatusId)
						getAllLvAlarmCount(isToday);
					modifyLightStatus(4,but);
					queryCurrentAlarmByAlarmLv(alarmLvStatus, isToday);
				}
			}
		},{// 告警灯->已清除告警
			id : 'PS_CLEARED',
			style : 'margin-left:3px;',
			height : 30,
//			cls : 'button5',
			width : 60,
			onMouseOver : function(e){
			},
			privilege : viewAuth,
			listeners : {
				click : function(but,event){
					Ext.getCmp('statusCombo').setValue(defaultStatusId);
					if(statusId!=defaultStatusId)
						getAllLvAlarmCount(isToday);
					modifyLightStatus(5,but);
					queryCurrentAlarmByAlarmLv(alarmLvStatus, isToday);
				}
			}
		}]
});

/**
 * 创建表格第二个工具栏
 */
var secondTbar = new Ext.Toolbar({
	border : false,
	items : ['-',{
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
//        icon : '../../resource/images/btnImages/user_suit.png',
        privilege : modAuth,
        handler : function (){
        	reversal();
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
        text: '告警同步',
        icon : '../../resource/images/btnImages/sync.png',
        privilege : actionAuth,
        handler : function(){
        	alarmSynch();
        }
    },'-',{
        text: '屏蔽',
        icon : '../../resource/images/btnImages/error_delete.png',
        handler : function(){
        	alarmShield();
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
/**
 * 把两个工具栏合成一个工具栏
 */
var tbar = new Ext.Toolbar({
	layout : 'form',
	border : false,
	items : [firstTbar,secondTbar]
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
	loadMask : false,
	stateId:'currentAlarmStateId',  
	stateful:true,
	view : new Ext.ux.grid.LockingGridView({
		scrollDelay:false,
		cacheSize:100,
		syncHeights: false,
		cleanDelay:500//,
		//autoScrollTop:false
	}), 
	bbar : pageTool
//	view : new Ext.ux.grid.BufferView({}),
});

editGridPanel.on('rowdblclick', function(grid, rowIndex, e){
	grid.getSelectionModel().selectRow(rowIndex);
	gotoBayface();
});


/**
 * @@@分权分域到网元@@@
 * 当前告警基础查询
 */
function queryCurrentAlarm(showAll){
	isToday = false;
	staticNeId = '';
	staticEmsGroupId = '';
	staticEmsId = '';
	staticSubNeId = '';
	staticStationId = '';
	if (showAll){
		// 过滤器ID
		filterId = defaultFilterId;
		Ext.getCmp('filterCombo').setValue(defaultFilterId);
		// 告警状态ID
		statusId = defaultStatusId;
		Ext.getCmp('statusCombo').setValue(defaultStatusId);
	} else {
		filterId = Ext.getCmp('filterCombo').getValue();
		statusId = Ext.getCmp('statusCombo').getValue();
	}
	
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
	view_ptpId = '';
	view_unitId = '';
	if(alarmLvStatus!=''){
		if(alarmLvStatus==1){// 点击"紧急告警灯"
				Ext.getCmp('PS_MAJOR').toggle();
				Ext.getCmp('PS_MINOR').toggle();
				Ext.getCmp('PS_WARNING').toggle();
				Ext.getCmp('PS_CLEARED').toggle();
		}else if(alarmLvStatus==2){
				Ext.getCmp('PS_CRITICAL').toggle();
				Ext.getCmp('PS_MINOR').toggle();
				Ext.getCmp('PS_WARNING').toggle();
				Ext.getCmp('PS_CLEARED').toggle();
		}else if(alarmLvStatus==3){
				Ext.getCmp('PS_CRITICAL').toggle();
				Ext.getCmp('PS_MAJOR').toggle();
				Ext.getCmp('PS_WARNING').toggle();
				Ext.getCmp('PS_CLEARED').toggle();
		}else if(alarmLvStatus==4){
				Ext.getCmp('PS_CRITICAL').toggle();
				Ext.getCmp('PS_MAJOR').toggle();
				Ext.getCmp('PS_MINOR').toggle();
				Ext.getCmp('PS_CLEARED').toggle();
		}else if(alarmLvStatus==5){
				Ext.getCmp('PS_CRITICAL').toggle();
				Ext.getCmp('PS_MAJOR').toggle();
				Ext.getCmp('PS_MINOR').toggle();
				Ext.getCmp('PS_WARNING').toggle();
		}
		alarmLvStatus='';
	}
	Ext.getBody().mask('读取中...');
	
	if(convergeChecked){ 
		storeCg.baseParams = {// 请求参数
				'jsonString' : Ext.encode({'stationId':staticStationId,'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,
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
			'jsonString' : Ext.encode({'stationId':staticStationId,'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,
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
	getAllLvAlarmCount(isToday);
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
	getAllLvAlarmCount(isToday);

}

/**
 * 查询告警灯告警数 @@@分权分域到网元@@@
 */
function getAllLvAlarmCount(isAddToday){
	filterId = Ext.getCmp('filterCombo').getValue();
	statusId = Ext.getCmp('statusCombo').getValue();
	Ext.Ajax.request({
		timeout: 60000,
		url: 'fault!getAllCurrentAlarmCount.action',
	    method: 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'stationId':staticStationId,'neId':staticNeId,'subnetId':staticSubNeId,'emsId':staticEmsId,
	    		'emsGroupId':staticEmsGroupId,'ptpId':view_ptpId,'unitId':view_unitId,'isAddAtToday':isAddToday,
	    		'filterId':filterId,'statusId':statusId})// 把对象转成JSON格式字符串
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	Ext.getCmp('PS_CRITICAL').setText('<font color='+PS_CRITICAL_FONT+'>'+obj.PS_CRITICAL+'</font>');
	    	Ext.getCmp('PS_MAJOR').setText('<font color='+PS_MAJOR_FONT+'>'+obj.PS_MAJOR+'</font>');
	    	Ext.getCmp('PS_MINOR').setText('<font color='+PS_MINOR_FONT+'>'+obj.PS_MINOR+'</font>');
	    	Ext.getCmp('PS_WARNING').setText('<font color='+PS_WARNING_FONT+'>'+obj.PS_WARNING+'</font>');
	    	Ext.getCmp('PS_CLEARED').setText('<font color='+PS_CLEARED_FONT+'>'+obj.PS_CLEARED+'</font>');
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

/**
 * 点击某个告警级别过滤告警列表数据
 * @param alarmLv
 * @returns
 */
function queryCurrentAlarmByAlarmLv(alarmLv,isToday){
	filterId = Ext.getCmp('filterCombo').getValue();
	statusId = Ext.getCmp('statusCombo').getValue();
	if(convergeChecked){ 
		storeCg.baseParams = {// 请求参数
			'jsonString' : Ext.encode({'stationId':staticStationId,'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,
				'emsGroupId':staticEmsGroupId,'alarmLv':alarmLv,'ptpId':view_ptpId,'unitId':view_unitId,
				'filterId':filterId,'statusId':statusId,'isConverge':convergeChecked,'isAddAtToday':isToday}),// 把对象转成JSON格式字符串
			'limit':pageSize// 每页显示多少条数据
		};
		
		storeCg.load({
			callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
				if (!success) {
					Ext.Msg.alert('错误', '查询失败！请重新查询');
				}else{
					if(alarmLvStatus==1){
				    	Ext.getCmp('PS_CRITICAL').setText('<font color='+PS_CRITICAL_FONT+'>'+storeCg.totalLength+'</font>');
					}else if(alarmLvStatus==2){
						Ext.getCmp('PS_MAJOR').setText('<font color='+PS_MAJOR_FONT+'>'+storeCg.totalLength+'</font>');
					}else if(alarmLvStatus==3){
						Ext.getCmp('PS_MINOR').setText('<font color='+PS_MINOR_FONT+'>'+storeCg.totalLength+'</font>');
					}else if(alarmLvStatus==4){
						Ext.getCmp('PS_WARNING').setText('<font color='+PS_WARNING_FONT+'>'+storeCg.totalLength+'</font>');
					}else if(alarmLvStatus==5){
						Ext.getCmp('PS_CLEARED').setText('<font color='+PS_CLEARED_FONT+'>'+storeCg.totalLength+'</font>');
					}
				}
			}
		});
	}else{
		store.baseParams = {// 请求参数
				'jsonString' : Ext.encode({'stationId':staticStationId,'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,
					'emsGroupId':staticEmsGroupId,'alarmLv':alarmLv,'ptpId':view_ptpId,'unitId':view_unitId,
					'filterId':filterId,'statusId':statusId,'isConverge':convergeChecked,'isAddAtToday':isToday}),// 把对象转成JSON格式字符串
				'limit':pageSize// 每页显示多少条数据
			};
			
			store.load({
				callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}else{
						if(alarmLvStatus==1){
					    	Ext.getCmp('PS_CRITICAL').setText('<font color='+PS_CRITICAL_FONT+'>'+store.totalLength+'</font>');
						}else if(alarmLvStatus==2){
							Ext.getCmp('PS_MAJOR').setText('<font color='+PS_MAJOR_FONT+'>'+store.totalLength+'</font>');
						}else if(alarmLvStatus==3){
							Ext.getCmp('PS_MINOR').setText('<font color='+PS_MINOR_FONT+'>'+store.totalLength+'</font>');
						}else if(alarmLvStatus==4){
							Ext.getCmp('PS_WARNING').setText('<font color='+PS_WARNING_FONT+'>'+store.totalLength+'</font>');
						}else if(alarmLvStatus==5){
							Ext.getCmp('PS_CLEARED').setText('<font color='+PS_CLEARED_FONT+'>'+store.totalLength+'</font>');
						}
					}
				}
			});
	}
}

/**
 * 修改告警灯的状态
 * @param alarmLv
 * @param but
 */
function modifyLightStatus(alarmLv,but){
	if(alarmLv==1){// 点击"紧急告警灯"
		if(but.pressed==false){// 如果此灯为亮
			Ext.getCmp('PS_MAJOR').toggle();
			Ext.getCmp('PS_MINOR').toggle();
			Ext.getCmp('PS_WARNING').toggle();
			Ext.getCmp('PS_CLEARED').toggle();
		}else{
			but.toggle();
			var majorStatus = Ext.getCmp('PS_MAJOR').pressed;
			if(majorStatus==false){
				Ext.getCmp('PS_MAJOR').toggle();
			};
			var minorStatus = Ext.getCmp('PS_MINOR').pressed;
			if(minorStatus == false){
				Ext.getCmp('PS_MINOR').toggle();
			};
			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
			if(warningStatus==false){
				Ext.getCmp('PS_WARNING').toggle();
			}
			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
			if(clearStatus==false){
				Ext.getCmp('PS_CLEARED').toggle();
			}
		}
	}else if(alarmLv==2){
		if(but.pressed==false){
			Ext.getCmp('PS_CRITICAL').toggle();
			Ext.getCmp('PS_MINOR').toggle();
			Ext.getCmp('PS_WARNING').toggle();
			Ext.getCmp('PS_CLEARED').toggle();
		}else{
			but.toggle();
			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
			if(majorStatus==false){
				Ext.getCmp('PS_CRITICAL').toggle();
			};
			var minorStatus = Ext.getCmp('PS_MINOR').pressed;
			if(minorStatus == false){
				Ext.getCmp('PS_MINOR').toggle();
			};
			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
			if(warningStatus==false){
				Ext.getCmp('PS_WARNING').toggle();
			}
			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
			if(clearStatus==false){
				Ext.getCmp('PS_CLEARED').toggle();
			}
		}
	}else if(alarmLv==3){
		if(but.pressed==false){
			Ext.getCmp('PS_CRITICAL').toggle();
			Ext.getCmp('PS_MAJOR').toggle();
			Ext.getCmp('PS_WARNING').toggle();
			Ext.getCmp('PS_CLEARED').toggle();
		}else{
			but.toggle();
			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
			if(majorStatus==false){
				Ext.getCmp('PS_CRITICAL').toggle();
			};
			var minorStatus = Ext.getCmp('PS_MAJOR').pressed;
			if(minorStatus == false){
				Ext.getCmp('PS_MAJOR').toggle();
			};
			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
			if(warningStatus==false){
				Ext.getCmp('PS_WARNING').toggle();
			}
			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
			if(clearStatus==false){
				Ext.getCmp('PS_CLEARED').toggle();
			}
		}
	}else if(alarmLv==4){
		if(but.pressed==false){
			Ext.getCmp('PS_CRITICAL').toggle();
			Ext.getCmp('PS_MAJOR').toggle();
			Ext.getCmp('PS_MINOR').toggle();
			Ext.getCmp('PS_CLEARED').toggle();
		}else{
			but.toggle();
			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
			if(majorStatus==false){
				Ext.getCmp('PS_CRITICAL').toggle();
			};
			var minorStatus = Ext.getCmp('PS_MAJOR').pressed;
			if(minorStatus == false){
				Ext.getCmp('PS_MAJOR').toggle();
			};
			var warningStatus = Ext.getCmp('PS_MINOR').pressed;
			if(warningStatus==false){
				Ext.getCmp('PS_MINOR').toggle();
			}
			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
			if(clearStatus==false){
				Ext.getCmp('PS_CLEARED').toggle();
			}
		}
	}else if(alarmLv==5){
		if(but.pressed==false){
			Ext.getCmp('PS_CRITICAL').toggle();
			Ext.getCmp('PS_MAJOR').toggle();
			Ext.getCmp('PS_MINOR').toggle();
			Ext.getCmp('PS_WARNING').toggle();
		}else{
			but.toggle();
			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
			if(majorStatus==false){
				Ext.getCmp('PS_CRITICAL').toggle();
			};
			var minorStatus = Ext.getCmp('PS_MAJOR').pressed;
			if(minorStatus == false){
				Ext.getCmp('PS_MAJOR').toggle();
			};
			var warningStatus = Ext.getCmp('PS_MINOR').pressed;
			if(warningStatus==false){
				Ext.getCmp('PS_MINOR').toggle();
		}
			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
			if(warningStatus==false){
				Ext.getCmp('PS_WARNING').toggle();
			}
		}
	}
	// 告警灯级别
	alarmLvStatus = alarmLv;
	if(Ext.getCmp('PS_CRITICAL').pressed==false&&Ext.getCmp('PS_MAJOR').pressed==false){
		alarmLvStatus = '';
	}
};

/**
 * 告警手动确认
 */
function manualConfirm(){
	panel=activeGridPane(convergeChecked); 
	var records = panel.getSelectionModel().getSelections();
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
					    	queryCurrentAlarm();
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
 * 告警反确认
 */
function antiConfirm(){
	panel=activeGridPane(convergeChecked);
	var records = panel.getSelectionModel().getSelections();
	if(records.length>=1){
		Ext.Msg.confirm('提示','反确认操作,将会把告警恢复到未确认状态。是否确定？',function(btn){       
			if(btn=='yes'){
				var ids = '';
				for ( var i = 0; i < records.length; i++) {
					 // 如果已确认
					if(records[i].get('IS_ACK')==1){
						ids += records[i].get('_id') + ',';
					}
				}
				ids = ids.substring(0, ids.lastIndexOf(','));
				if(ids!=''){
					Ext.Ajax.request({
					    url: 'fault!alarmAntiConfirm.action',
					    method: 'POST',
					    params: {
					    	'jsonString' : Ext.encode({'ids':ids})// 把对象转成JSON格式字符串
					    },
					    success: function(response) {
					    	queryCurrentAlarm();
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
	panel=activeGridPane(convergeChecked);
	// 获取选择的记录
	var record = panel.getSelectionModel().getSelections();
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
			alarmDetailWindow.setHeight(alarmDetailWindow
					.getInnerHeight());
		}
		alarmDetailWindow.center();
	} else{
		Ext.Msg.alert('提示', '请选择需要查看的告警，每次选择一条！');
	}
}

/**
 * 告警高级查询
 */
function queryCurrentAlarm_high(){
	var url = 'highQuery.jsp?type=current&authSequence=' + authSequence;
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
 * 查询所有当前告警(相当于重新进入当前告警页面)
 */
function getCurrentAlarms(){
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var checkedNodeIds;
	if (iframe.getCheckedNodes) {
		checkedNodeIds = iframe.getCheckedNodes();
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes();
	}
	if (checkedNodeIds&&checkedNodeIds.length>0) 
		checkedNodeIds[0].ui.onCheck(); 
	staticNeId='';
	staticSubNeId='';
	staticEmsId='';
	staticEmsGroupId='';

	queryCurrentAlarm(true);
}

/**
 * 告警同步
 */
function alarmSynch(){
	var url = 'alarmSynch.jsp';
	var alarmSynchWindow = new Ext.Window({
		id : 'alarmSynchWindow',
		title : '告警同步',
		width : 300,
		height : 300,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='alarmSynch_panel' name = 'alarmSynch_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	alarmSynchWindow.show();
}

/**
 * 获取告警灯背景图片样式
 * @param c
 * @returns {String}
 */
function getBackgroundImage(c){
	if(c=='#ff0000'){
		return 'button1';
	}else if(c=='#ff8000'){
		return 'button2';
	}else if(c=='#ffff00'){
		return 'button3';
	}else if(c=='#800000'){
		return 'button4';
	}else if(c=='#00ff00'){
		return 'button5';
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
 * 跳转告警屏蔽
 */
function alarmShield(){
	panel=activeGridPane(convergeChecked);
	var records = panel.getSelectionModel().getSelections();
	if(records.length>=1){
		var ids = '';
		for ( var i = 0; i < records.length; i++) {
			ids += records[i].get('_id') + ',';
		}
		ids = ids.substring(0, ids.lastIndexOf(','));
		parent.parent.addTabPage('../../jsp/faultManager/alarmShield.jsp?alarmIds='+ids+'&tabFlag=current', "告警及事件屏蔽",authSequence);
	}else{
		parent.parent.addTabPage('../../jsp/faultManager/alarmShield.jsp?alarmIds='+'&tabFlag=current', "告警及事件屏蔽",authSequence);
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
	    	// 告警灯显示
	    	var alarmLightHidden = obj.alarmLightHidden;
	    	if(alarmLightHidden!=''){
	    		var arr = alarmLightHidden.split(',');
	    		for ( var i = 0; i < arr.length; i++) {
					if(arr[i]==1){
						Ext.getCmp('PS_CRITICAL').setVisible(false);
					}else if(arr[i]==2){
						Ext.getCmp('PS_MAJOR').setVisible(false);
					}else if(arr[i]==3){
						Ext.getCmp('PS_MINOR').setVisible(false);
					}else if(arr[i]==4){
						Ext.getCmp('PS_WARNING').setVisible(false);
					}else if(arr[i]==5){
						Ext.getCmp('PS_CLEARED').setVisible(false);
					}
				}
	    	}
	    	// 告警灯背景颜色  
	    	Ext.getCmp('PS_CRITICAL').addClass(getBackgroundImage(obj.PS_CRITICAL_IMAGE));
	    	Ext.getCmp('PS_MAJOR').addClass(getBackgroundImage(obj.PS_MAJOR_IMAGE));
	    	Ext.getCmp('PS_MINOR').addClass(getBackgroundImage(obj.PS_MINOR_IMAGE));
	    	Ext.getCmp('PS_WARNING').addClass(getBackgroundImage(obj.PS_WARNING_IMAGE));
	    	Ext.getCmp('PS_CLEARED').addClass(getBackgroundImage(obj.PS_CLEARED_IMAGE));
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
//导出 当前告警数据
function exportCurrentAlarmData(){
	staticStationId = '';
	var hiddenColoumms=getGridHiddenColomn();
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
	}else{
		staticNeId = '';
		staticEmsGroupId = '';
		staticEmsId = '';
		staticSubNeId = '';
	}
	view_ptpId = '';
	view_unitId = '';
	var params = {// 请求参数
		'jsonString' : Ext.encode({'stationId':staticStationId,'neId':staticNeId,'emsId':staticEmsId,'emsGroupId':staticEmsGroupId,
			'subnetId':staticSubNeId,'alarmLv':alarmLvStatus,'ptpId':view_ptpId,'unitId':view_unitId,
			'hiddenColoumms':hiddenColoumms,'filterId':filterId,'statusId':statusId,'isConverge':false}),// 把对象转成JSON格式字符串
		'limit':5000// 每页显示多少条数据
	};
	window.location.href=  "fault!downloadCurrentAlarmResult.action?" + Ext.urlEncode(params);
}

function gotoCircuit(){
	panel=activeGridPane(convergeChecked);
	var records = panel.getSelectionModel().getSelections();
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
		var neType=records[i].get('NE_TYPE');
		if (neType==4) //如果是PTN网元，则其上的电路都是PTN电路（无论板卡的domain是什么）
			serviceType=4; //PTN电路
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

function gotoBayface(){
	panel=activeGridPane(convergeChecked);
	var records = panel.getSelectionModel().getSelections();
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

/**
 * 告警反转
 */
function reversal(){
	panel=activeGridPane(convergeChecked);
	var records = panel.getSelectionModel().getSelections();
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
					    	queryCurrentAlarm();
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

function checkNodesForWait(nodeInfo) {
	var iframe = window.frames["tree_panel"];
	if (iframe.checkNodes) {
		iframe.checkNodes(nodeInfo);
	} else {
		setTimeout("checkNodesForWait(\"" + nodeInfo + "\")", 500);
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
/**
 * Ext初始化
 */
Ext.onReady(function() {
	Ext.Ajax.timeout = 900000;
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = '../../resource/ext/resources/images/default/s.gif';
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	init();
	new Ext.Viewport({
		id : 'win',
		layout : 'border',
		border : false,
		items : [westPanel,gridPanel]
	});
	Ext.getCmp('gridPanel').getLayout().setActiveItem(0);  
	// 设置告警状态下拉框默认值
	Ext.getCmp('statusCombo').setValue(1); //1:全部

	// 延时加载告警查询，以等待告警过滤下拉列表加载完成
	setTimeout("initShowAlm()",500);
	
	// 选择网元告警时初始化树对象
	if (neInfo) {
		setTimeout("checkNodesForWait(\"" + neInfo + "\")", 500);
	}
	// 选择网管告警时初始化树对象
	if (emsInfo) {
		setTimeout("checkNodesForWait(\"" + emsInfo + "\")", 500);
	}
	Beeper.init();
});

function initShowAlm(){
	if (severity>4 ) {
		// 非主页跳转而来
		filterId = Ext.getCmp('filterCombo').getValue();
		statusId = Ext.getCmp('statusCombo').getValue();
		store.baseParams = {// 请求参数
				'jsonString' : Ext.encode({'stationId':staticStationId,'subnetId':staticSubNeId,'neId':staticNeId,'emsId':staticEmsId,
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
		getAllLvAlarmCount(isToday);
		
	} else {
		// 从主页跳转而来
		if (severity==1) {
			modifyLightStatus(severity, Ext.getCmp('PS_CRITICAL'));
		} else if (severity==2) {
			modifyLightStatus(severity, Ext.getCmp('PS_MAJOR'));
		} else if (severity==3) {
			modifyLightStatus(severity, Ext.getCmp('PS_MINOR'));
		} else {
			modifyLightStatus(severity, Ext.getCmp('PS_WARNING'));
		}
		Ext.getCmp('statusCombo').setValue(defaultStatusId);
		if(statusId!=defaultStatusId)
			getAllLvAlarmCount(isToday);
		queryCurrentAlarmByAlarmLv(alarmLvStatus, isToday);
	}
}