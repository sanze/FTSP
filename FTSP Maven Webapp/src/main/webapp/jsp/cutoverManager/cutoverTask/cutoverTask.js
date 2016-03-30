/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
 Date.prototype.format = function(fmt)
{ //author: meizz
  var o = {
    "M+" : this.getMonth()+1,                 //月份
    "d+" : this.getDate(),                    //日
    "h+" : this.getHours(),                   //小时
    "m+" : this.getMinutes(),                 //分
    "s+" : this.getSeconds(),                 //秒
    "q+" : Math.floor((this.getMonth()+3)/3), //季度
    "S"  : this.getMilliseconds()             //毫秒
  };
  if(/(y+)/.test(fmt))
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
  for(var k in o)
    if(new RegExp("("+ k +")").test(fmt))
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
  return fmt;
}
function getDate(day){
    var zdate=new Date();
    var sdate=zdate.getTime()-(1*24*60*60*1000);
    var edate=new Date(sdate-(day*24*60*60*1000)).format("yyyy-MM-dd");
    return edate;
 
}
var statusData=[
	['5','等待&进行中'],
	['1','等待'],
	['2','进行中'],
	['3','完成'],
	['7','完成(基准值需要更新)'],
	['8','完成(基准值已更新)'],
	['4','挂起'],
	['6','全部']
];
var statusStore=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'displayName'}
	 ]
});
statusStore.loadData(statusData);

var store = new Ext.data.Store({
	url : 'cutover-task!getCutoverTask.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "SYS_TASK_ID", "TASK_NAME", "START_TIME_ESTIMATE", "END_TIME_ESTIMATE", "STATUS","TASK_STATUS",
			"USER_NAME", "CREATE_TIME", "DESCRIPTION", "START_TIME_ACTUAL","END_TIME_ACTUAL" ])
});

// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect:true});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'SYS_TASK_ID',
		header : 'ID',
		dataIndex : 'SYS_TASK_ID',
		hidden : true,
		hideable : false,
		width : 150
	}, {
		id : 'TASK_NAME',
		header : '割接任务名称',
		dataIndex : 'TASK_NAME',
		width : 150
	}, {
		id : 'START_TIME_ESTIMATE',
		header : '预计开始时间',
		dataIndex : 'START_TIME_ESTIMATE',
		width : 150
	}, {
		id : 'END_TIME_ESTIMATE',
		header : '预计结束时间',
		dataIndex : 'END_TIME_ESTIMATE',
		width : 150
	}, {
		id : 'STATUS',
		header : '状态',
		dataIndex : 'STATUS',
		renderer : formatStatus,
		width : 150
	}, {
		id : 'CREATE_PERSON',
		header : '创建人',
		dataIndex : 'USER_NAME',
		width : 150
	}, {
		id : 'CREATE_TIME',
		header : '创建时间',
		dataIndex : 'CREATE_TIME',
		width : 150
	}, {
		id : 'DESCRIPTION',
		header : '描述',
		dataIndex : 'DESCRIPTION',
		width : 150
	}, {
		id : 'START_TIME_ACTUAL',
		header : '实际开始时间',
		dataIndex : 'START_TIME_ACTUAL',
		width : 150
	},{
		id : 'END_TIME_ACTUAL',
		header : '实际结束时间',
		dataIndex : 'END_TIME_ACTUAL',
		width : 150
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var searchCombo;
(function() {
	var searchStore = new Ext.data.Store({
		url : 'regular-pm-analysis!getBaseEmsGroups.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "BASE_EMS_GROUP_ID", "GROUP_NAME" ])
	});
	searchCombo = new Ext.form.ComboBox({
		fieldLabel : "网管分组",
		store : searchStore,
		triggerAction : "all",
		editable : false,
		valueField : 'BASE_EMS_GROUP_ID',
		displayField : 'GROUP_NAME',
		resizable: true,
		listeners : {
			'select' : function(combo, record, index) {
				var jsonData = {
					"start" : 0,
					"limit" : 200,
					"emsGroupId" : combo.getValue()
				};
				store.baseParams = jsonData;
				store.load({
					callback : function(records, options, success) {
						if (!success)
							Ext.Msg.alert("提示", "查询出错");
					}
				});
			}
		}
	})
})();

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	tbar:[
	    	'-',"时间范围：从",{
		                xtype:'textfield',
		                id:'startTime',
		                name:'startTime',
		                width:120,
		                anchor:'95%',
		                cls : 'Wdate',
			            value:this.nowTime,
			        	anchor: '80%',
		                listeners: {
		                	'focus': function(){
		                		WdatePicker({
		                			el : "startTime",
									isShowClear : false,
									readOnly : true,
									dateFmt : 'yyyy-MM-dd',
									autoPickDate : true
									
								})
							}
		                }
		       }," 至",
		       {
	                xtype:'textfield',
	                id:'endTime',
	                name:'endTime',
	                anchor:'95%',
	                width:120,
	                cls : 'Wdate',
		            value:this.nowTime,
		        	anchor: '80%',
	                listeners: {
	                	'focus': function(){
	                		WdatePicker({
	                			el : "endTime",
								isShowClear : false,
								readOnly : true,
								dateFmt : 'yyyy-MM-dd',
								autoPickDate : true
								
							})
						}
	                }
		       },'-'/*,{xtype: 'tbspacer', width: 10}*/,'任务状态:',{
		            xtype     : 'combo',
		            fieldLabel: '',
		            id:"statusCombo",
		            store:statusStore,
		            valueField:"value",
		            mode : 'local',
		            triggerAction: 'all',
		            displayField:"displayName",
		            width:120,
		            anchor:'50%'
		            
		        },'-'/*{xtype: 'tbspacer', width: 10}*/,'割接任务名:',{
		            xtype     : 'textfield',
		            fieldLabel: '',
		            id:"cutoverTaskName",
//		            emptyText:"割接任务名",
		            width:120,
		            anchor:'50%'
		            
		        },'-',/*{xtype: 'tbspacer', width: 10},*/
		        {
			        text: '查询',
			        privilege:viewAuth,
			        icon:'../../../resource/images/btnImages/search.png',
			        handler: searchCutoverTask
	    		},'-',/*{xtype: 'tbspacer', width: 10},*/
		       {
					xtype: 'splitbutton',
					text:'新增',
					privilege:addAuth, 
					icon:'../../../resource/images/btnImages/add.png',
//					style: 'margin:0px 0px 0px 10px;',
					handler: function(b,e){this.showMenu();},
					menu:{
						items: [
						{
							text: '按设备割接',
							handler: addByNeAndPtp
						}, {
							text: '按链路割接',
							handler: addByLink
						},{
							text: '按光缆割接',
							disabled:true,
							handler: function(b,e){
								
							}
						}
						]
					}
				},'-',/*{xtype: 'tbspacer', width: 10},*/
		        {
			        text: '删除',
			        privilege:delAuth,
			        icon:'../../../resource/images/btnImages/delete.png',
			        handler: deleteCutoverTask
	    		},'-',/*{xtype: 'tbspacer', width: 10},*/
		        {
			        text: '修改',
			        privilege:modAuth,
			        icon:'../../../resource/images/btnImages/modify.png',
			        handler: modifyCutoverTask
	    		},'-',/*{xtype: 'tbspacer', width: 10},*/
		        {
			        text: '任务操作',
			        privilege:viewAuth,
			        icon:'../../../resource/images/btnImages/wand.png',
			        handler: taskOperation
	    		},'-',/*{xtype: 'tbspacer', width: 10},*/
		        {
			        text: '割接报告',
			        privilege:viewAuth,
			        icon:'../../../resource/images/btnImages/report.png',
			        handler: function(){
			           downLoadReport();	
			        }
	    		},{xtype: 'tbspacer', width: 10},
	    		{
			        text: '更新基准值',
			        privilege:modAuth,
			        icon:'../../../resource/images/btnImages/report.png',
			        handler: function(){
			           updateCompareValue();	
			        }
	    		},
	    		{
			        text: '评估参数',
			        privilege:viewAuth,
			        icon:'../../../resource/images/btnImages/report.png',
			        handler: function(){
			           evaluationConfig();	
			        }
	    		}
//	    		,
//		        {
//			        text: '割接报告测试按钮',
//			        privilege:viewAuth,
//			        handler: function(){
//			           Ext.Ajax.request({
//							url : 'cutover-task!generateReport.action',
//							params : {},
//							type : 'post',
//							success: function(response) {
//					            Ext.Msg.alert("成功","报告生成成功！");
//						    },
//						    error:function(response) {
//						    	top.Ext.getBody().unmask();
//						    	Ext.Msg.alert("错误",response.responseText);
//						    },
//						    failure:function(response) {
//						    	top.Ext.getBody().unmask();
//						    	Ext.Msg.alert("错误",response.responseText);
//						    }
//						});	
//			        }
//	    		}
	    ],
//	viewConfig : {
//		forceFit : true
//	},
	listeners : {
		'rowdblclick' : function(gridPanel, rowIndex, e) {
		}
	}

});
//查询割接任务
function searchCutoverTask() {
	var startTime =  Ext.getCmp("startTime").getValue();
	var endTime = Ext.getCmp("endTime").getValue();
	var status = Ext.getCmp("statusCombo").getValue();
	var cutoverTaskName = Ext.getCmp("cutoverTaskName").getValue();
	// 查询条件
	var jsonData = {
		"searchCondition.startTime" : startTime,
		"searchCondition.endTime" : endTime,
		"searchCondition.status" : status,
		"searchCondition.cutoverTaskName" : cutoverTaskName,
		"searchCondition.currentUserId" : userId,
		"start" : 0,
		"limit" : 200
		
	};
	store.baseParams = jsonData;
	store.load({
					callback : function(records, options, success) {
						if (!success) {
							Ext.Msg.alert("提示", "查询出错");
						}
						gridPanel.getEl().unmask();
					}
				});
}
//按网元和端口新增任务
function addByNeAndPtp()
{
	var addWindow=new Ext.Window({
	        id:'addWindow',
	        title:'割接任务设置(设备)',
	        width:1900,
	        height:1500,
	        isTopContainer : true,
	        shadow:false,
	        autoScroll:true,
			html:'<iframe src = "editTaskByNeAndPtp.jsp'+'" height="100%" width="100%" frameBorder=0 border=0/>'	     	
			});
	     	addWindow.show();
			if (addWindow.getHeight() > Ext.getCmp('win').getHeight()) {
				addWindow.setHeight(Ext.getCmp('win').getHeight() * 0.8);
			} else {
				gridPanel.setHeight(addWindow.getInnerHeight());
			}
			if (addWindow.getWidth() > Ext.getCmp('win').getWidth()) {
				addWindow.setWidth(Ext.getCmp('win').getWidth() * 0.8);
			} else {
				gridPanel.setWidth(addWindow.getInnerWidth());
			}
			addWindow.center();
			addWindow.doLayout();
}
//按链路新增任务
function addByLink()
{
	var addWindow=new Ext.Window({
	        id:'addWindow',
	        title:'割接任务设置(链路)',
	        width:1900,
	        height:1500,
	        shadow:false,
	        resizable:false,
//	        isTopContainer : true,
//	        modal : true,
	        autoScroll:true,
	        html:'<iframe src = "editTaskByLink.jsp'+'" height="100%" width="100%" frameBorder=0 border=0/>' 
	     	});
	     	addWindow.show();
	     	if (addWindow.getHeight() > Ext.getCmp('win').getHeight()) {
				addWindow.setHeight(Ext.getCmp('win').getHeight() * 0.9);
			} else {
//				gridPanel.setHeight(addWindow.getInnerHeight());
			}
			if (addWindow.getWidth() > Ext.getCmp('win').getWidth()) {
				addWindow.setWidth(Ext.getCmp('win').getWidth() * 0.9);
			} else {
//				gridPanel.setWidth(addWindow.getInnerWidth());
			}
			addWindow.center();
			addWindow.doLayout();
}


//删除割接任务
function deleteCutoverTask()
{
   var selectRecord =gridPanel.getSelectionModel().getSelections(); 
   if(selectRecord.length != 1)
   {
      Ext.Msg.alert("提示","必须选择一个任务。");
   }else{
      var taskIdList = new Array();
      var status = 0;
      for(var i = 0; i< selectRecord.length;i++){
         if(selectRecord[i].get("STATUS") == "1" ||selectRecord[i].get("STATUS") == "2"){
           status = 1;
           break;
         }
         taskIdList.push(selectRecord[i].get("SYS_TASK_ID"));
      }
      if(status == 1){
         Ext.Msg.alert("提示","只能删除处于挂起、完成状态的任务。");
      }else{
        // deleteTask(taskIdList);
        Ext.Msg.confirm('提示','将会删除所选择的任务。确认删除？',
        function(btn){
        if(btn=='yes'){
        	var jsonData = {
		       "taskIdList":taskIdList
		    }
        	Ext.Ajax.request({
			url : 'cutover-task!deleteTask.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
		        Ext.Msg.alert("提示",obj.returnMessage);
		        store.reload();
			},
			error : function(response) {
				Ext.Msg.alert("提示",response.responseText);
			},
			failure : function(response) {
				Ext.Msg.alert("提示",response.responseText);
			}
			});
        
        }
        })
        
	        
        	
      }
      
   }
}

//修改割接任务
function modifyCutoverTask()
{
   var selectRecord =gridPanel.getSelectionModel().getSelections(); 
   if(selectRecord.length != 1)
   {
      Ext.Msg.alert("提示","必须选择一个任务。");
   }else{
   		 //taskStatus保存割接任务类型：1：按网元端口割接，2：按链路割接，3：按复用段割接，4：按光缆割接
   		 var taskStatus = selectRecord[0].get("TASK_STATUS");
   		 var cutoverTaskId = selectRecord[0].get("SYS_TASK_ID");
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
//			maximized:true,
	        html:'<iframe src = "editTaskByNeAndPtp.jsp?cutoverTaskId='+cutoverTaskId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
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
//			maximized:true,
	        html:'<iframe src = "editTaskByLink.jsp?cutoverTaskId='+cutoverTaskId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
	     	});
   		 }
    	 addWindow.show();
	if (addWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addWindow.setHeight(Ext.getCmp('win').getHeight() * 0.8);
	} else {
//		panel.setHeight(win.getInnerHeight());
	}
	if (addWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addWindow.setWidth(Ext.getCmp('win').getWidth() * 0.8);
	} else {
//		panel.setHeight(win.getInnerHeight());
	}
	addWindow.center();
   }
}
//任务操作
function taskOperation()
{
   var selectRecord =gridPanel.getSelectionModel().getSelections(); 
   if(selectRecord.length != 1)
   {
      Ext.Msg.alert("提示","必须选择一个任务。");
   }else{
   		 //taskStatus保存割接任务类型：1：按网元端口割接，2：按链路割接，3：按复用段割接，4：按光缆割接
   		 var taskStatus = selectRecord[0].get("TASK_STATUS");
   		 var cutoverTaskId = selectRecord[0].get("SYS_TASK_ID");
   		 var url = "../cutoverManager/taskOperation/cutoverTaskOperationTab.jsp?cutoverTaskId="+cutoverTaskId+"&taskStatus="+taskStatus;
   		 parent.addTabPage(url, "割接任务操作"+"－"+selectRecord[0].get("TASK_NAME"), url);
//   		 var addWindow=new Ext.Window({
//	        id:'addWindow',
//	        title:'割接任务操作',
//	        width:600,
//	        height:400,
//	        isTopContainer : true,
//	        modal : true,
//	        autoScroll:true,
//			maximized:true,
//	        html:'<iframe src = "../taskOperation/cutoverTaskOperationTab.jsp?cutoverTaskId='+cutoverTaskId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
//	     	});
//		  addWindow.show();
   }
   		
	
}
//更新基准值
function updateCompareValue()
{
   var selectRecord =gridPanel.getSelectionModel().getSelections(); 
   if(selectRecord.length != 1)
   {
      Ext.Msg.alert("提示","必须选择一个任务。");
   }else{
   		 //taskStatus保存割接任务类型：1：按网元端口割接，2：按链路割接，3：按复用段割接，4：按光缆割接
   		 var taskStatus = selectRecord[0].get("TASK_STATUS");
   		 var cutoverTaskId = selectRecord[0].get("SYS_TASK_ID");
	var compareWindow=new Ext.Window({
	        id:'compareWindow',
	        title:'更新基准值',
	        width:1900,
	        height:1500,
	        shadow:false,
	        resizable:false,
//	        isTopContainer : true,
//	        modal : true,
	        autoScroll:true,
	        html:'<iframe src = "pmValueWithDifference.jsp?cutoverTaskId='+cutoverTaskId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
	     	});
	     	compareWindow.show();
	     	if (compareWindow.getHeight() > Ext.getCmp('win').getHeight()) {
				compareWindow.setHeight(Ext.getCmp('win').getHeight() * 0.6);
			} else {
//				gridPanel.setHeight(addWindow.getInnerHeight());
			}
			if (compareWindow.getWidth() > Ext.getCmp('win').getWidth()) {
				compareWindow.setWidth(Ext.getCmp('win').getWidth() * 0.6);
			} else {
//				gridPanel.setWidth(addWindow.getInnerWidth());
			}
			compareWindow.center();
			compareWindow.doLayout();
	}
}
//评估参数设置
function evaluationConfig()
{
	var configWindow=new Ext.Window({
	        id:'configWindow',
	        title:'评估参数',
	        width:1900,
	        height:1500,
	        shadow:false,
	        resizable:false,
//	        isTopContainer : true,
//	        modal : true,
	        autoScroll:true,
	        html:'<iframe src = "evaluationConfig.jsp?authSequence='+authSequence+'" height="100%" width="100%" frameBorder=0 border=0/>' 
	     	});
	     	configWindow.show();
	     	if (configWindow.getHeight() > Ext.getCmp('win').getHeight()) {
				configWindow.setHeight(Ext.getCmp('win').getHeight() * 0.6);
			} else {
//				gridPanel.setHeight(addWindow.getInnerHeight());
			}
			if (configWindow.getWidth() > Ext.getCmp('win').getWidth()) {
				configWindow.setWidth(Ext.getCmp('win').getWidth() * 0.6);
			} else {
//				gridPanel.setWidth(addWindow.getInnerWidth());
			}
			configWindow.center();
			configWindow.doLayout();
}
// 格式化任务状态
function formatStatus(value) {
	if (value == 1) {
		return '等待';
	} else if (value == 2) {
		return '进行中';
	} else if (value == 3) {
		return '完成';
	} else if (value == 4) {
		return '挂起';
	} else if (value == 7) {
		return '完成(基准值需要更新)';
	} else if (value == 8) {
		return '完成(基准值已更新)';
	} else {
		return value;
	}
};

// 更改任务状态
function changeTaskStatus(statusFlag) {
	var emsIds = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	var msg;

	if (cell.length > 0) {
		if (statusFlag == 1) {
			msg = '是否要启用性能采集任务？';
		}
		if (statusFlag == 4) {
			for ( var i = 0; i < cell.length; i++) {
				if (cell[i].get('COLLECT_STATUS') == 2 || cell[i].get('COLLECT_STATUS') == 3) {
					Ext.Msg.alert("提示", "不能挂起正在执行中的任务！");
					return;
				}
			}
			msg = '是否要挂起性能采集任务？';
		}
		for ( var i = 0; i < cell.length; i++) {
			var emsId = {
				"BASE_EMS_CONNECTION_ID" : cell[i].get('BASE_EMS_CONNECTION_ID'),
				"COLLECT_STATUS" : statusFlag
			};
			emsIds.push(Ext.encode(emsId));
		}
		var jsonData = {
			"modifyList" : emsIds
		};
		Ext.Msg.confirm('信息', msg, function(btn) {
			if (btn == 'yes') {
				Ext.Ajax.request({
					url : 'regular-pm-analysis!changeTaskStatus.action',
					params : jsonData,
					type : 'post',
					success : function(response) {
						gridPanel.getEl().unmask();
						var result = Ext.util.JSON.decode(response.responseText);
						if (result) {
							Ext.Msg.alert("提示", result.returnMessage);
							if (1 == result.returnResult) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							}
						}
					},
					failure : function(response) {
						gridPanel.getEl().unmask();
						Ext.Msg.alert("提示", "更改执行状态出错");
					},
					error : function(response) {
						gridPanel.getEl().unmask();
						Ext.Msg.alert("提示", "更改执行状态出错");
					}
				});
			}
		});
	} else
		Ext.Msg.alert("提示", "请选择需要启用的采集任务！！");
}
// 保存
function saveTask() {
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {
		gridPanel.getEl().mask("正在执行,请稍候...");
		var emsList = new Array();
		for ( var i = 0; i < cell.length; i++) {
			var ems = {
				'BASE_EMS_CONNECTION_ID' : cell[i].get('BASE_EMS_CONNECTION_ID'),
				'COLLEC_START_TIME' : cell[i].get('COLLEC_START_TIME'),
				'COLLEC_END_TIME' : cell[i].get('COLLEC_END_TIME'),
				'COLLECT_SOURCE' : cell[i].get('COLLECT_SOURCE')
			};
			emsList.push(Ext.encode(ems));
		}
		var jsonData = {
			"modifyList" : emsList
		};
		// 提交修改，不然store.getModifiedRecords();数据会累加
		store.commitChanges();
		Ext.Ajax.request({
			url : 'regular-pm-analysis!modifyEmses.action',
			params : jsonData,
			type : 'post',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
					if (1 == result.returnResult) {
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					}
				}
			},
			failure : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			},
			error : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			}
		});
	}
}

// 设置任务内容
function setTask() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var url = 'settingTask.jsp?emsId=' + cell[0].get("BASE_EMS_CONNECTION_ID");
		var setTaskWin = new Ext.Window({
			id : 'setTaskWin',
			title : '采集任务设置',
			width : 800,
			height : 400,
			isTopContainer : true,
			modal : true,
			plain : true, // 是否为透明背景
			html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />'
		});
		setTaskWin.show();
	} else {
		Ext.Msg.alert("提示", "请先选取任务，只能选择一条！");
	}
}
// 执行状态
function showStatus() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var url = 'collectingStatus.jsp?emsId=' + cell[0].get("BASE_EMS_CONNECTION_ID");
		var setTaskWin = new Ext.Window({
			id : 'collectingStatus',
			title : '采集任务执行状态',
			width : 800,
			height : 400,
			isTopContainer : true,
			modal : true,
			plain : true, // 是否为透明背景
			html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />'
		});
		setTaskWin.show();
	} else {
		Ext.Msg.alert("提示", "请先选取任务，只能选择一条！");
	}

}

function downLoadReport(){
    var cell = gridPanel.getSelectionModel().getSelections();
    if(cell.length==0){
        Ext.Msg.alert("提示","必须选择一个割接任务。");
        return;
    }
    if(cell.length>1){
        Ext.Msg.alert("提示","只可选择一个割接任务！");
        return;
    }else{
       var cutoverTaskId = cell[0].get("SYS_TASK_ID");
    }
    if(cell[0].get("END_TIME_ACTUAL")=="")
    {
    	Ext.Msg.alert("提示","割接任务尚未完成，请待任务完成后再查看报告。");
        return;
    }
    Ext.Ajax.request({
		url : 'cutover-task!downLoadReport.action',
		params : {"cutoverTaskId":cutoverTaskId},
		type : 'post',
		success: function(response) {
            top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
	    	if(obj.returnResult == 1){
		    	window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
            }
        	if(obj.returnResult == 0){
        		Ext.Msg.alert("提示","导出数据失败！");
        	}
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
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	Ext.getCmp("statusCombo").setValue("6");
	var tody =  getDate(-1);
	var sevenDaysBefore = getDate(6);//前七天
	Ext.getCmp("startTime").setValue(sevenDaysBefore);
	Ext.getCmp("endTime").setValue(tody);
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();
	
	store.baseParams = {
		"searchCondition.startTime" : sevenDaysBefore,
		"searchCondition.endTime" : tody,
		"searchCondition.status" : "6",
		"searchCondition.cutoverTaskName" : "",
		"searchCondition.currentUserId" : userId,
		"start" : 0,
		"limit" : 200
	};
	store.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "加载失败");
		}
	});
});
