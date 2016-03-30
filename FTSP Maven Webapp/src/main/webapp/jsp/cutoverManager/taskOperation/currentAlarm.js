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
var factoryValue = 1;
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
 * 定义定时器的全局变量
 */
var refreshFlag = true;
// 告警灯级别
var alarmLvStatus = '';
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
 * 创建表格选择模型(多选、单选.....)
 */ 
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
//	singleSelect ：true,// 表示只能单选，默认false,
//	sortable : true,//表示选择框列可以排序，默认fasle
});
sm.sortLock();
/**
 * 创建数告警列表列模型
 */
var cm = new Ext.ux.grid.LockingColumnModel({
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
	},
//	{
//		header : '归一化名称',
//		dataIndex : 'NORMAL_CAUSE',
//		hidden:true,
//		width : 100
//	},
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
	},
//	{
//		header : '网元型号',
//		dataIndex : 'PRODUCT_NAME',
//		hidden:true,
//		width : 100
//	},
	{
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
	},
//	{
//		header : '业务类型',
//		dataIndex : 'DOMAIN',
//		hidden:true,
//		width : 100
//	},{
//		header : '端口类型',
//		dataIndex : 'PTP_TYPE',
//		hidden:true,
//		width : 100
//	},
	{
		header : '速率',
		dataIndex : 'INTERFACE_RATE',
		width : 100
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		hidden:true,
		width : 100
	},
//	{
//		header : '首次发生时间',
//		dataIndex : 'FIRST_TIME',
//		hidden:true,
//		width : 100
//	},{
//		header : '频次',
//		dataIndex : 'AMOUNT',
//		hidden:true,
//		width : 100
//	},{
//		header : '最近发生时间',
//		dataIndex : 'UPDATE_TIME',
//		hidden:true,
//		width : 100
//	},{
//		header : '业务影响',
//		dataIndex : 'SERVICE_AFFECTING',
//		hidden:true,
//		width : 100
//	},{
//		header : '清除时间',
//		dataIndex : 'CLEAR_TIME',
//		hidden:true,
//		width : 100
//	},{
//		header : '确认时间',
//		dataIndex : 'ACK_TIME',
//		hidden:true,
//		width : 100
//	},{
//		header : '确认者',
//		dataIndex : 'ACK_USER',
//		hidden:true,
//		width : 100
//	},
	{
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
	},
//	{
//		header : '告警标准名',
//		dataIndex : 'PROBABLE_CAUSE',
//		hidden:true,
//		width : 100
//	},
	{
		header : '割接前快照时间',
		dataIndex : 'SNAPSHOT_TIME_BEFORE',
		width : 100
	},{
		header : '割接后快照时间',
		dataIndex : 'SNAPSHOT_TIME_AFTER',
		width : 100
	}
//	,{
//		header : '清除状态',
//		dataIndex : 'IS_CLEAR',
//		hidden:true,
//		width : 100,
//		renderer : function(value){
//			if(value==1){
//				return '已清除';
//			}else{
//				return '未清除';
//			}
//		}
//	}
	]
});

/**
 * 创建当前告警列表数据源
 */ 
var store = new Ext.data.Store({
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
var pageTool = new Ext.PagingToolbar({
	pageSize : 200,
	id:'pageTool',
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});

/**
 * 修改定时器全局变量
 */
function modifyReflash(){
	refreshFlag = true;
	queryCurrentAlarm();
}

/**
 * 手动查询
 */
function manualQuery(){
	queryCurrentAlarm();
	refreshFlag = false;
	setTimeout("modifyReflash()",10000);
}

/**
 * 创建表格第工具栏
 */
var firstTbar = new Ext.Toolbar({
	border : false,
	items : ['-',{
		            xtype     : 'radio',
		            boxLabel: '全部告警',
		            id:'alarmsAll',
		            checked:true,
		            listeners: {
		            	'check': function(){
		            		if(this.checked ===  true)
		            		{
		            			Ext.getCmp('alarmsBefore').setValue(false);
		            			Ext.getCmp('alarmsAfter').setValue(false);
		            			
		            			
		            				/**
									 * 加载数据
									 */
									store.baseParams = {// 请求参数
										'searchCondition.cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
										'searchCondition.alarmType': 1, //alarmtype=1,说明查询全部告警
										'limit':200// 每页显示多少条数据
									}; 
									store.load({
										callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
											if (!success) {
												Ext.Msg.alert('提示', '查询失败！请重新查询');
											}
										}
									});
		            			
		            			
		            		}
						}
            		}
		            
		        },'-',{
		            xtype     : 'radio',
		            boxLabel: '割接前',
		            id:'alarmsBefore',
		            listeners: {
		            	'check': function(){
		            		if(this.checked ==  true)
		            		{
		            			Ext.getCmp('alarmsAll').setValue(false);
		            			Ext.getCmp('alarmsAfter').setValue(false);
		            			/**
								 * 加载数据
								 */
								store.baseParams = {// 请求参数
									'searchCondition.cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
									'searchCondition.alarmType': 2, //alarmtype=2,说明查询割接前告警
									'limit':200// 每页显示多少条数据
								}; 
								store.load({
									callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
										if (!success) {
											Ext.Msg.alert('提示', '查询失败！请重新查询');
										}
									}
								});
		            		}
						}
            		}
		            
		        },'-',{
		            xtype     : 'radio',
		            boxLabel: '割接后',
		            id:'alarmsAfter',
		            listeners: {
		            	'check': function(){
		            		if(this.checked ==  true)
		            		{
		            			Ext.getCmp('alarmsAll').setValue(false);
		            			Ext.getCmp('alarmsBefore').setValue(false);
		            			/**
								 * 加载数据
								 */
								store.baseParams = {// 请求参数
									'searchCondition.cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
									'searchCondition.alarmType': 3, //alarmtype=3,说明查询割接后告警
									'limit':200// 每页显示多少条数据
								}; 
								store.load({
									callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
										if (!success) {
											Ext.Msg.alert('提示', '查询失败！请重新查询');
										}
									}
								});
		            		}
						}
            		}
		            
		        },'-',{
	        text: '导出',
	        icon : '../../../resource/images/btnImages/export.png',
	        handler : function (){
				exportExcel();
			} 
	    },'-',{
	        text: '割接期间告警', 
	         handler : function (){
				alarmsDuringCutover();
			} 
	    }
//	    ,{
//	        text: '刷新', 
//	    },{// 告警灯->紧急告警
//	    	id : 'PS_CRITICAL',
//			style : 'margin-left:150px;',
//			height : 30,
//			cls : 'button-jinji',
//			width : 60,
//			onMouseOver : function(e){
//			},
//			listeners : {
//				beforerender : function(but){
//					// 加载告警灯的当前告警数
//					getCurrentAlarmCount(1,but);
//				},
//				click : function(but,event){
//					// 修改告警灯的状态
//					modifyLightStatus(1,but);
//					// 根据点击的告警灯过滤当前告警
//					queryCurrentAlarmByAlarmLv(alarmLvStatus);
//				}
//			}
//		},{// 告警灯->重要告警
//			id : 'PS_MAJOR',
//			style : 'margin-left:15px;',
//			height : 30,
//			cls : 'button-zhongyao',
//			width : 60,
//			onMouseOver : function(e){
//			},
//			listeners : {
//				beforerender : function(but){
//					getCurrentAlarmCount(2,but);
//				},
//				click : function(but,event){
//					// 修改告警灯的状态
//					modifyLightStatus(2,but);
//					queryCurrentAlarmByAlarmLv(alarmLvStatus);
//				}
//			}
//		},{// 告警灯->次要告警
//			id : 'PS_MINOR',
//			style : 'margin-left:15px;',
//			height : 30,
//			cls : 'button-ciyao',
//			width : 60,
//			onMouseOver : function(e){
//			},
//			listeners : {
//				beforerender : function(but){
//					getCurrentAlarmCount(3,but);
//				},
//				click : function(but,event){
//					// 修改告警灯的状态
//					modifyLightStatus(3,but);
//					queryCurrentAlarmByAlarmLv(alarmLvStatus);
//				}
//			}
//		},{// 告警灯->提示告警
//			id : 'PS_WARNING',
//			style : 'margin-left:15px;',
//			height : 30,
//			cls : 'button-tishi',
//			width : 60,
//			onMouseOver : function(e){
//			},
//			listeners : {
//				beforerender : function(but){
//					getCurrentAlarmCount(4,but);
//				},
//				click : function(but,event){
//					// 修改告警灯的状态
//					modifyLightStatus(4,but);
//					queryCurrentAlarmByAlarmLv(alarmLvStatus);
//					but.addClass('button-tishi_a');
//				}
//			}
//		},{// 告警灯->已清除告警
//			id : 'PS_CLEARED',
//			style : 'margin-left:15px;',
//			height : 30,
//			cls : 'button-cleared',
//			width : 60,
//			onMouseOver : function(e){
//			},
//			listeners : {
//				beforerender : function(but){
//					getCurrentAlarmCount(5,but);
//				},
//				click : function(but,event){
//					// 修改告警灯的状态
//					modifyLightStatus(5,but);
//					queryCurrentAlarmByAlarmLv(alarmLvStatus);
//				}
//			}
//		}
		]
});

/**
 * 把两个工具栏合成一个工具栏
 */
var tbar = new Ext.Toolbar({
	layout : 'form',
	border : false,
	items : [firstTbar]
});
/**
 * 创建一个可编辑的表格
 */
var editGridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	forceFit : true,
//	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	tbar : firstTbar,
	bbar : pageTool
});

/**
 * 当前告警基础查询(后期需要加入过滤器的条件)
 */
//function queryCurrentAlarm(){
//	if(refreshFlag){
//		setTimeout('queryCurrentAlarm()',10000);
//		// 获取当前的页数
//		var start = (pageTool.getPageData().activePage-1)*pageTool.pageSize;
//		Ext.Ajax.request({
//		    url: 'fault!getCurrentAlarms.action',
//		    method: 'POST',
//		    params: {
//		    	'jsonString' : Ext.encode({'neId':Ext.getCmp('neCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue(),'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'alarmLv':alarmLvStatus}),// 把对象转成JSON格式字符串
//		    	'start' : start,
//		    	'limit':5// 每页显示多少条数据
//		    },
//		    success: function(response) {
//		    	//新总页数
//		    	var newTotalPage = 0;
//		    	// 区域，根据余数是否等于0，判断不不是整除
//		    	var flag = Ext.decode(response.responseText).total%pageTool.pageSize;
//		    	if(flag==0){
//		    		newTotalPage = Ext.decode(response.responseText).total/pageTool.pageSize;
//		    	}else{
//		    		newTotalPage = (Ext.decode(response.responseText).total-flag)/pageTool.pageSize + 1;
//		    	}
//		    	var oldCurrentPage = pageTool.getPageData().activePage;
//		    	if(newTotalPage==0){
//		    		store.baseParams={// 请求参数
//		    			'jsonString' : Ext.encode({'neId':Ext.getCmp('neCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue(),'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'alarmLv':alarmLvStatus}),// 把对象转成JSON格式字符串
//		    			'limit':5// 每页显示多少条数据
//		    		};
//		    		store.load();
//		    	}else{
//				    if(oldCurrentPage>newTotalPage){// 表示查询的页数，超出了总页数(自动把查询的页，更新为最后一页)
//				    	store.baseParams={// 请求参数
//				    			'jsonString' : Ext.encode({'neId':Ext.getCmp('neCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue(),'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'alarmLv':alarmLvStatus}),// 把对象转成JSON格式字符串
//				    			'limit':5// 每页显示多少条数据
//				    	};
//			    		pageTool.changePage(newTotalPage);
//			    	}else{
//			    		pageTool.afterTextItem.setText(String.format(pageTool.afterPageText, newTotalPage));// 设置总页数
//			    		pageTool.inputItem.setValue(oldCurrentPage);// 重新设置当前页
//			    		store.baseParams={// 请求参数
//				    			'jsonString' : Ext.encode({'neId':Ext.getCmp('neCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue(),'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'alarmLv':alarmLvStatus}),// 把对象转成JSON格式字符串
//				    			'limit':5// 每页显示多少条数据
//				    	};
//			    		if(oldCurrentPage<newTotalPage){
//			    			pageTool.next.setDisabled(false);
//				    		pageTool.last.setDisabled(false);
//			    		}
//				    	store.totalLength = Ext.decode(response.responseText).total;
//				    	pageTool.updateInfo();
//				    	// 创建一个record对象，用来转换后台的记录
//				    	var record = Ext.data.Record.create(['_id','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','NATIVE_EMS_NAME','NE_NAME','PRODUCT_NAME','SLOT_NO','UNIT_NAME','PORT_NO','DOMAIN',
//				    	                     			 'PTP_TYPE','INTERFACE_RATE','CTP_NAME','FIRST_TIME','AMOUNT','UPDATE_TIME','SERVICE_AFFECTING','CLEAR_TIME','ACK_TIME','ACK_USER','ALARM_TYPE','PROBABLE_CAUSE']); 
//				    	// 新记录
//				    	var newRecords = Ext.decode(response.responseText).rows;
//				    	// 旧记录
//				    	var oldRecords = store.getRange();
//				    	for ( var i = 0; i < oldRecords.length; i++) {
//				    		var count = 0;
//				    		for ( var j = 0; j < newRecords.length; j++) {
//				    			if(oldRecords[i].get('_id')==newRecords[j]._id){
//				    				count ++ ;
//				    			}
//							}
//				    		if(count==0){
//				    			store.remove(oldRecords[i]);
//				    			store.singleSort("_id","ASC");
//				    		}
//						}
//				    	// 清理后(删除已经不存在的告警记录)的旧记录
//				    	var removeOldRecords = store.getRange();
//				    	for ( var i = 0; i < newRecords.length; i++) {
//				    		var count = 0;
//							for ( var j = 0; j < removeOldRecords.length; j++) {
//								if(newRecords[i]._id==removeOldRecords[j].get('_id')){
//									count ++ ;
//									// 如果存在则根据更新时间判断该告警是否有变动，如果有变动则更新(先删除、再插入)
//									if(newRecords[i].UPDATE_TIME!=removeOldRecords[j].get('UPDATE_TIME')){
//										store.removeAt(j);
//										store.insert(j,new record(newRecords[i]));
//										store.singleSort("_id","ASC");
//									}
//								}
//							}
//							if(count==0){
//								store.add(new record(newRecords[i]));
//							}
//						}
//			    	}
//			    }
//		    	//--------------------------------------
//				getCurrentAlarmCount(1,Ext.getCmp('PS_CRITICAL'));
//				getCurrentAlarmCount(2,Ext.getCmp('PS_MAJOR'));
//				getCurrentAlarmCount(3,Ext.getCmp('PS_MINOR'));
//				getCurrentAlarmCount(4,Ext.getCmp('PS_WARNING'));
//				getCurrentAlarmCount(5,Ext.getCmp('PS_CLEARED'));
//		    },
//		    error:function(response) {
//		    	top.Ext.getBody().unmask();
//	        	Ext.Msg.alert("错误",response.responseText);
//		    },
//		    failure:function(response) {
//		    	top.Ext.getBody().unmask();
//	        	Ext.Msg.alert("错误",response.responseText);
//		    }
//		}); 
//	}
//}

/**
 * 点击某个告警级别过滤告警列表数据
 * @param alarmLv
 * @returns
 */
//function queryCurrentAlarmByAlarmLv(alarmLv){
//	store.baseParams = {// 请求参数
//		'jsonString' : Ext.encode({'neId':Ext.getCmp('neCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue(),'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'alarmLv':alarmLv}),// 把对象转成JSON格式字符串
//		'limit':5// 每页显示多少条数据
//	};
//	store.load({
//		callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
//			if (!success) {
//				Ext.Msg.alert('错误', '查询失败！请重新查询');
//			}
//		}
//	});
//}

/**
 * 查询某告警级别的当前告警数
 * @param alarmLv
 * @param but
 * @returns
 */
//function getCurrentAlarmCount(alarmLv,but){ 
//	Ext.Ajax.request({
//	    url: 'fault!getCurrentAlarmCount.action',
//	    method: 'POST',
//	    params: {
//	    	'jsonString' : Ext.encode({'neId':Ext.getCmp('neCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue(),'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'alarmLv':alarmLv})// 把对象转成JSON格式字符串
//	    },
//	    success: function(response) {
//	    	// 将json格式的字符串，转换成对象
//	    	var obj = Ext.decode(response.responseText);
//	    	but.setText(''+obj.total);
//	    },
//	    error:function(response) {
//	    	top.Ext.getBody().unmask();
//        	Ext.Msg.alert("错误",response.responseText);
//	    },
//	    failure:function(response) {
//	    	top.Ext.getBody().unmask();
//        	Ext.Msg.alert("错误",response.responseText);
//	    }
//	}); 
//}

/**
 * 修改告警灯的状态
 * @param alarmLv
 * @param but
 */
//function modifyLightStatus(alarmLv,but){
//	if(alarmLv==1){// 点击"紧急告警灯"
//		if(but.pressed==false){// 如果此灯为亮
//			Ext.getCmp('PS_MAJOR').toggle();
//			Ext.getCmp('PS_MINOR').toggle();
//			Ext.getCmp('PS_WARNING').toggle();
//			Ext.getCmp('PS_CLEARED').toggle();
//		}else{
//			but.toggle();
//			var majorStatus = Ext.getCmp('PS_MAJOR').pressed;
//			if(majorStatus==false){
//				Ext.getCmp('PS_MAJOR').toggle();
//			};
//			var minorStatus = Ext.getCmp('PS_MINOR').pressed;
//			if(minorStatus == false){
//				Ext.getCmp('PS_MINOR').toggle();
//			};
//			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
//			if(warningStatus==false){
//				Ext.getCmp('PS_WARNING').toggle();
//			}
//			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
//			if(clearStatus==false){
//				Ext.getCmp('PS_CLEARED').toggle();
//			}
//		}
//	}else if(alarmLv==2){
//		if(but.pressed==false){
//			Ext.getCmp('PS_CRITICAL').toggle();
//			Ext.getCmp('PS_MINOR').toggle();
//			Ext.getCmp('PS_WARNING').toggle();
//			Ext.getCmp('PS_CLEARED').toggle();
//		}else{
//			but.toggle();
//			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
//			if(majorStatus==false){
//				Ext.getCmp('PS_CRITICAL').toggle();
//			};
//			var minorStatus = Ext.getCmp('PS_MINOR').pressed;
//			if(minorStatus == false){
//				Ext.getCmp('PS_MINOR').toggle();
//			};
//			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
//			if(warningStatus==false){
//				Ext.getCmp('PS_WARNING').toggle();
//			}
//			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
//			if(clearStatus==false){
//				Ext.getCmp('PS_CLEARED').toggle();
//			}
//		}
//	}else if(alarmLv==3){
//		if(but.pressed==false){
//			Ext.getCmp('PS_CRITICAL').toggle();
//			Ext.getCmp('PS_MAJOR').toggle();
//			Ext.getCmp('PS_WARNING').toggle();
//			Ext.getCmp('PS_CLEARED').toggle();
//		}else{
//			but.toggle();
//			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
//			if(majorStatus==false){
//				Ext.getCmp('PS_CRITICAL').toggle();
//			};
//			var minorStatus = Ext.getCmp('PS_MAJOR').pressed;
//			if(minorStatus == false){
//				Ext.getCmp('PS_MAJOR').toggle();
//			};
//			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
//			if(warningStatus==false){
//				Ext.getCmp('PS_WARNING').toggle();
//			}
//			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
//			if(clearStatus==false){
//				Ext.getCmp('PS_CLEARED').toggle();
//			}
//		}
//	}else if(alarmLv==4){
//		if(but.pressed==false){
//			Ext.getCmp('PS_CRITICAL').toggle();
//			Ext.getCmp('PS_MAJOR').toggle();
//			Ext.getCmp('PS_MINOR').toggle();
//			Ext.getCmp('PS_CLEARED').toggle();
//		}else{
//			but.toggle();
//			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
//			if(majorStatus==false){
//				Ext.getCmp('PS_CRITICAL').toggle();
//			};
//			var minorStatus = Ext.getCmp('PS_MAJOR').pressed;
//			if(minorStatus == false){
//				Ext.getCmp('PS_MAJOR').toggle();
//			};
//			var warningStatus = Ext.getCmp('PS_MINOR').pressed;
//			if(warningStatus==false){
//				Ext.getCmp('PS_MINOR').toggle();
//			}
//			var clearStatus = Ext.getCmp('PS_CLEARED').pressed;
//			if(clearStatus==false){
//				Ext.getCmp('PS_CLEARED').toggle();
//			}
//		}
//	}else if(alarmLv==5){
//		if(but.pressed==false){
//			Ext.getCmp('PS_CRITICAL').toggle();
//			Ext.getCmp('PS_MAJOR').toggle();
//			Ext.getCmp('PS_MINOR').toggle();
//			Ext.getCmp('PS_WARNING').toggle();
//		}else{
//			but.toggle();
//			var majorStatus = Ext.getCmp('PS_CRITICAL').pressed;
//			if(majorStatus==false){
//				Ext.getCmp('PS_CRITICAL').toggle();
//			};
//			var minorStatus = Ext.getCmp('PS_MAJOR').pressed;
//			if(minorStatus == false){
//				Ext.getCmp('PS_MAJOR').toggle();
//			};
//			var warningStatus = Ext.getCmp('PS_MINOR').pressed;
//			if(warningStatus==false){
//				Ext.getCmp('PS_MINOR').toggle();
//		}
//			var warningStatus = Ext.getCmp('PS_WARNING').pressed;
//			if(warningStatus==false){
//				Ext.getCmp('PS_WARNING').toggle();
//			}
//		}
//	}
//	// 告警灯级别
//	alarmLvStatus = alarmLv;
//	if(Ext.getCmp('PS_CRITICAL').pressed==false&&Ext.getCmp('PS_MAJOR').pressed==false){
//		alarmLvStatus = '';
//	}
//};

/**
 * 查看当前告警详情
 */
//function queryAlarmDetail() {
//	// 获取选择的记录
//	var record = editGridPanel.getSelectionModel().getSelections();
//	if (record.length > 0 && record.length < 2) {// 如果选择一条记录
//		// 告警ID
//		var alarmId = record[0].get('_id');
//		// 跳转地址
//		var url = 'alarmDetail.jsp?alarmId='+alarmId+'&type=current';
//		var alarmDetailWindow = new Ext.Window({
//			id : 'alarmDetailWindow',
//			title : '告警详情',
//			width : 400,
//			height : 300,
//			isTopContainer : true,
//			modal : true,
//			autoScroll : true,
//			html : "<iframe  id='alarmDetail_panel' name = 'alarmDetail_panel'  src = " + url
//					+ " height='100%' width='100%' frameBorder=0 border=0/>"
//		});
//		alarmDetailWindow.show();
//	} else{
//		Ext.Msg.alert('提示', '请选择需要查看的告警，每次选择一条！');
//	}
//}

/**
 * 告警高级查询
 */
//function queryCurrentAlarm_high(){
//	var url = 'highQuery.jsp?type=current';
//	var highQueryWindow = new Ext.Window({
//		id : 'highQueryWindow',
//		title : '高级查询',
//		width : 820,
//		height : 600,
//		isTopContainer : true,
//		modal : true,
//		autoScroll : true,
//		html : "<iframe  id='highQuery_panel' name = 'highQuery_panel'  src = " + url
//				+ " height='100%' width='100%' frameBorder=0 border=0/>"
//	});
//	highQueryWindow.show();
//}

function alarmsDuringCutover(){
	var url = 'alarmDuringCutover.jsp?cutoverTaskId='+cutoverTaskId;
	var alarmsDuringCutoverWindow = new Ext.Window({
		id : 'alarmsDuringCutoverWindow',
		title : '割接期间告警',
//		width : 800,
//		height : 600,
		shadow:false,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='highQuery_panel' name = 'highQuery_panel'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>"
	});
//	alert(editGridPanel.getHeight())
//	alarmsDuringCutoverWindow.setWidth(531);
    alarmsDuringCutoverWindow.show();
	Ext.getCmp("alarmsDuringCutoverWindow").setWidth(editGridPanel.getWidth()*0.9);
	Ext.getCmp("alarmsDuringCutoverWindow").setHeight(editGridPanel.getHeight()*0.9);
	alarmsDuringCutoverWindow.center();
	
//	alarmsDuringCutoverWindow.syncSize();
	
}
//导出告警信息
function exportExcel() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	}
//	else if (store.getTotalCount() > 2000) {
//		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
//			if (btn == 'yes') {
//				{
//					exportRequest();
//				}
//			}
//		});
	else exportRequest();
}
var exportData = {// 请求参数
		'cutoverTaskId' : cutoverTaskId,
		'searchCondition.flag': 2 //
	}
var exportRequest=function(){
	editGridPanel.getEl().mask("正在导出...");
	Ext.Ajax.request({
		url : 'cutover-task!downloadResult.action',
		type : 'post',
		params : exportData,
		success : function(response) {
			editGridPanel.getEl().unmask();
			var rs=Ext.decode(response.responseText);
			if(rs.returnResult==1&&rs.returnMessage!=""){
				var destination={
						"filePath":rs.returnMessage
				};
				window.location.href="download!execute.action?"+Ext.urlEncode(destination);
			}
			else {
				editGridPanel.getEl().unmask();
				Ext.Msg.alert("提示","导出失败！");
			}
		},
		error : function(response) {
			editGridPanel.getEl().unmask();
			Ext.Msg.alert("提示", response.responseText);
		},
		failure : function(response) {
			editGridPanel.getEl().unmask();
			Ext.Msg.alert("提示", response.responseText);
		}
	});
};
/**
 * 添加tab页，作为高级查询结果展示
 */
function addTabPage(url, title){
	var iframeId = "f_" + title;
	centerPanel.remove(title);
	var tabPage = centerPanel.add({// 动态添加tab页
		id : title,
		visible : true,
		closable : true,
		title : title,
		html : '<iframe id= \'' + iframeId + '\' src=\'' + url+ '\' frameborder="0" width="100%" height="100%"/>'
	});
	centerPanel.setActiveTab(tabPage);// 设置当前tab页
}
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
 * 告警同步
 */
//function alarmSynch(){
//	var url = 'alarmSynch.jsp';
//	var alarmSynchWindow = new Ext.Window({
//		id : 'alarmSynchWindow',
//		title : '告警同步',
//		width : 300,
//		height : 300,
//		isTopContainer : true,
//		modal : true,
//		autoScroll : true,
//		html : "<iframe  id='alarmSynch_panel' name = 'alarmSynch_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
//	});
//	alarmSynchWindow.show();
//}

//刷新grid列表
window.refresh = function(){
	//刷新列表
	var pageTool = Ext.getCmp('pageTool');
	if(pageTool){
		pageTool.doLoad(pageTool.cursor);
	}
}
/**
 * Ext初始化
 */
Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	Ext.BLANK_IMAGE_URL = '../../resource/ext/resources/images/default/s.gif';
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
		id : 'win',
		layout : 'border',
		border : false,
		items : editGridPanel
	});
	
/**
 * 加载数据
 */
	store.baseParams = {// 请求参数
		'searchCondition.cutoverTaskId' : cutoverTaskId,// 把对象转成JSON格式字符串
		'searchCondition.alarmType': 1, //alarmtype=1,说明查询全部告警
		'limit':200// 每页显示多少条数据
	};
	editGridPanel.getEl().mask("正在查询,请稍候");
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
	    		store.load({
	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
		editGridPanel.getEl().unmask();
		if (!success) {
			Ext.Msg.alert('提示', '查询失败！请重新查询');
		}
	}
});
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

});