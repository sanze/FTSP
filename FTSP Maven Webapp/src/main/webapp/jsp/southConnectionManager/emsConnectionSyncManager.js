/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var myPageSize = 200;

//var emsGroupStore = new Ext.data.Store({
//	url : 'connection!getConnectGroup.action',
//	baseParams : {
//		"emsGroupId" : "-1"
//	},
//	reader : new Ext.data.JsonReader({
//		totalProperty : 'total',
//		root : "rows"
//	}, [ "GROUP_NAME", "BASE_EMS_GROUP_ID" ])
//});
//
//emsGroupStore.load({
//	callback : function(r, options, success) {
//		if (success) {
//
//		} else {
//			Ext.Msg.alert('提示', '查询网管分组失败！请重新查询');
//		}
//	}
//});

/**
 * 创建网管分组数据源
 */
var emsGroupStore = new Ext.data.Store({
	// 获取数据源地址
	proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
		url : 'common!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
		disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
	}),
	baseParams : {"displayAll" : true,"displayNone" : true,"authDomain":false},
	// record格式
	reader : new Ext.data.JsonReader({
		root : 'rows',//json数据的key值
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});
// 访问地址，加载数据(如果没有这一句，则不会去后台查询)
emsGroupStore.load({
	// 回调函数
	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
		// 获取下拉框的第一条记录
		var firstValue = records[0].get('BASE_EMS_GROUP_ID');

		// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
		Ext.getCmp('emsGroup').setValue(firstValue);
	}
});

var store = new Ext.data.Store(
		{
			// 1代表查询corba连接
			url : 'connection!getEmsConnectionSyncInfo.action',
			baseParams : {
				"emsGroupId" : "-99",
				"limit" : myPageSize
			},
			reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, [ "emsConnectionId", "emsDisplayName", "TYPE", "emsGroupId",
					"emsGroupName",

					"executeStatus", "periodType", "period", "taskId","taskInfoId",

					"taskStatus", "latestSyncTime", "nextSyncTime","time",
					"internalEmsName" ])
		});

 var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: myPageSize,//每页显示的记录值
	store: store,
    displayInfo: true,
//    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
 });

 var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
		singleSelect : true,
		header : ""
 });
 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit:false
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),checkboxSelectionModel,{
            id: 'emsConnectionId',
            header: 'id',
            dataIndex: 'emsConnectionId',
            hidden:true
        },{
            id: 'taskId',
            header: 'id',
            dataIndex: 'taskId',
            hidden:true
        },{
            id: 'emsGroupName',
            header: '网管分组',
            width:80,
            dataIndex: 'emsGroupName'
        },{
            id: 'emsDisplayName',
            header: '网管名称',
            width:120,
            dataIndex: 'emsDisplayName'
        },{
            id: 'TYPE',
            header: '网管类型',
            dataIndex: 'TYPE',
            width:100
        },{
            id: 'syncTime',
            header: '同步周期',
            width:180,
            dataIndex: 'syncTime',
			renderer : function(v, r, t) {
				// 0,0,1,0，0:9:00 年，季，月，周，日，9：00
				var vv = t.get("periodType");
				var vt = t.get("period").split(",");
				var time = t.get("");
				var ret;
				if (vv == 2) { // 每周
					ret = "每周 ";
					if (vt[3] == 2) {
						ret += "周一 ";
					} else if (vt[3] == 3) {
						ret += "周二 ";
					} else if (vt[3] == 4) {
						ret += "周三 ";
					} else if (vt[3] == 5) {
						ret += "周四 ";
					} else if (vt[3] == 6) {
						ret += "周五 ";
					} else if (vt[3] == 7) {
						ret += "周六 ";
					} else if (vt[3] == 1) {
						ret += "周日 ";
					}
					ret += vt[5];
				} else if (vv == 3) { // if(vv == 3)
					ret = "每月 ";

					ret += vt[4] + "号 " + vt[5];

				} else if (vv == 4) { // if(vv == 3)
					ret = "每季度 ";

					ret += "第 " + vt[2] + "个月   " + vt[4] + "号 " + vt[5];

				}
				return "<div><div  style='float:left'>"
						+ ret
						+ "</div><div style='float:right'><img "
						+ "src='../../resource/images/btnImages/modify.png' title='编辑' "
						+ "onclick ='syncCycleSetting()' /></div></div>";
			}
        },{
            id: 'taskStatus',
		    header: '任务状态',
            dataIndex: 'taskStatus',
            width:80,
			renderer : function(v) {
				if (v == 1) {
					return "启用";
				} else if (v == 2) {
					return "挂起";
				} else {
					return "-";
				}
			}
        },{
            id:'executeStatus',
            header: '执行状态',
            width:100,
            dataIndex: 'executeStatus',
            renderer : function(v, r, t) {
				if (v == 1) {
					return "执行成功";
				} else if (v == 2) {
					return "执行失败";
				} else if (v == 3) {
					return "执行中";
				} else if (v == 4) {
					return "执行中止";
				} else if (v == 5) {
	    			var vt ;
	    			var ret;
					if(null != t.get("time") && t.get("time") >0) {
						 vt = t.get("time");
							ret = "暂停( " + vt + "分钟 )";
					} else {
						ret = "暂停" ;
					}	   
					return ret;
				} else if (v == 6) {
					return "部分成功";
				} else {
					return "-";
				}
			}
        },{
            id:'latestSyncTime',
            header: '上次同步时间',
            width:140,
            dataIndex: 'latestSyncTime'
        },{
            id:'nextSyncTime',
            header: '下次同步时间',
            width:140,
            dataIndex: 'nextSyncTime'
        }]
    });


var connectListPanel = new Ext.grid.EditorGridPanel({
	id:"connectListPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox
	viewConfig: {
        forceFit:false
    },
	bbar: pageTool, 
	tbar: [ '-', '网管分组：' , {
		xtype : 'combo',
		id : 'emsGroup',
		name : 'emsGroup',
		emptyText : '请选择网管分组',
		// fieldLabel: '请选择网管分组',
		// sideText : '<font color=red>*</font>',
		mode : "local",
		editable : false,
		store : emsGroupStore,
		displayField : "GROUP_NAME",
		valueField : 'BASE_EMS_GROUP_ID',
		triggerAction : 'all',
		// allowBlank:false,
		width : 120,
		anchor : '95%',
		listeners : {
			select : function(combo, record, index) {
				var emsGroupId = Ext.getCmp('emsGroup').getValue();
				store.baseParams = {
					"emsGroupId" : emsGroupId,
					"limit" : myPageSize
				};
				store.load();
			}
		}
        },'-',{
            text: '启用',
            privilege:actionAuth, 
            icon:'../../resource/images/btnImages/control_play.png',
            handler : function(){
            	startTask();
        	}
        },{
            text: '挂起',
            privilege:actionAuth, 
            icon:'../../resource/images/btnImages/control_stop.png',
            handler : function(){
            	disTask();
        	}
        },'-',{
            text: '手动同步',
            privilege:actionAuth, 
            icon:'../../resource/images/btnImages/sync.png',
            handler : function(){
            	manualSync();
        	}
        },'-',{
            text: '任务状态',
            privilege:actionAuth, 
            icon:'../../resource/images/btnImages/setTask.png',
            handler : function(){
            	taskStatus();
        	}
        }
	]
}); 

function startTask(){
	var cell = connectListPanel.getSelectionModel().getSelections();
	var selectedTaskId = new Array();
	if (cell.length > 0) {
		if (cell.length == 1) {

		var jsonString = new Array();
		var map = {
				"taskId" : cell[0].get("taskId"),
				 "periodType":cell[0].get("periodType"),
				 "period":cell[0].get("period")
		};
		jsonString.push(map);

		var jsonData = {
			"jString" : Ext.encode(jsonString)
		};

			Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
				url : 'connection!startTask.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					Ext.getBody().unmask();
					//刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				},
				error : function(response) {
					Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				}
			});
		
		} else {
			Ext.Msg.alert("提示", "请勿多选网管！");
		}
	} else
		Ext.Msg.alert("提示", "请选择网管！");
}

function disTask(){
	var cell = connectListPanel.getSelectionModel().getSelections();
	var selectedTaskId = new Array();
	if (cell.length > 0) {
		if (cell.length == 1) {		
		var jsonString = new Array();
		var map = {
				"emsConnectionId" : cell[0].get("emsConnectionId"),
				"taskId" :cell[0].get("taskId"),
				"taskStatus" :cell[0].get("taskStatus"),
				"executeStatus" :cell[0].get("executeStatus")
		};
		jsonString.push(map);

		var jsonData = {
			"jString" : Ext.encode(jsonString)
		};
		
		if (cell[0].get("executeStatus") == 3 ||cell[0].get("executeStatus") == 5) {
			Ext.Msg.confirm("提示", "任务正在执行，如果挂起将会停止任务。是否确定？", function(btn) {
				if (btn == 'yes') {
		
					Ext.Ajax.request({
						url : 'connection!disTask.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {
							Ext.getBody().unmask();
							//刷新列表
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						},
						error : function(response) {
							Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						},
						failure : function(response) {
							Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						}
					});
				} else {

				}
			});
		} else {
			
			Ext.Ajax.request({
				url : 'connection!disTask.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					Ext.getBody().unmask();
					//刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				},
				error : function(response) {
					Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				}
			});
		}
		} else {
			Ext.Msg.alert("提示", "请勿多选网管！");
		}
	} else {
		Ext.Msg.alert("提示", "请选择网管！");
	}
}

function syncCycleSetting(){
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
		
		var url = "syncCycleSetting.jsp?periodType=" + cell[0].get("periodType")
				+ "&period=" + cell[0].get("period") + "&nextSyncTime="
				+ cell[0].get("nextSyncTime").replace(" ", ",") + "&taskId="
				+ cell[0].get("taskId") + "&taskStatus="+ cell[0].get("taskStatus");
		syncCycleWindow = new Ext.Window({
					id : 'syncCycleWindow',
					title : '周期设置',
					width : 420,
					height : 300,
					isTopContainer : true,
					modal : true,
					plain : true, // 是否为透明背景
					html : '<iframe src='
							+ url
							+ ' height="100%" width="100%" frameborder=0 border=0/>'
				});
		syncCycleWindow.show();
	} else {
		Ext.Msg.alert('提示', '周期设置记录不能为空');
	}
}

function manualSync(){
	var cell = connectListPanel.getSelectionModel().getSelections();
	var selectedTaskId = new Array();
	if (cell.length > 0) {
		if (cell.length == 1) {
		var jsonData = {
			"taskId" :cell[0].get("taskId"),
			"emsConnectionId" : cell[0].get("emsConnectionId")
		};
		if (cell[0].get("executeStatus") == 3 || cell[0].get("executeStatus") == 5) {
			Ext.Msg.alert("提示", "任务正在执行中！");
		} else {
			Ext.Msg.confirm("提示", "同步整个网管的数据，时间可能较长，是否同步？", function(btn) {
				if (btn == 'yes') {
//					Ext.getBody().mask('正在执行，请稍候...');
					Ext.Ajax.request({
						url : 'connection!manualSyncEms.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {
							Ext.getBody().unmask();

							if (obj.returnResult == 1) {
								Ext.Msg.alert("信息", obj.returnMessage);
								//刷新列表
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							}
							if (obj.returnResult == 0) {
								Ext.Msg.alert("信息", obj.returnMessage);
								//刷新列表
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							}
						},
						error : function(response) {
							Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						},
						failure : function(response) {
							Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						}
					});
				} else {

				}
			});
		}
		} else {
			Ext.Msg.alert("提示", "请勿多选网管！");
		}
	} else {
		Ext.Msg.alert("提示", "请选择要同步的网管！");
	}
}

function taskStatus(){
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
		var panelHeight = Ext.getCmp('connectListPanel').getSize().height; 	
		var windowHeight = 500;
		if(panelHeight < windowHeight) {
			windowHeight = panelHeight*1;
		}
		var url = "taskDetailInfo.jsp?emsConnectionId=" + cell[0].get("emsConnectionId")+"&taskId="+cell[0].get("taskId")+"&executeStatus="+cell[0].get("executeStatus");
		taskStatusWindow = new Ext.Window({
					id : 'taskStatusWindow',
					title : '任务状态',
					width : 630,
					height : windowHeight,
					isTopContainer : true,
					modal : true,
					plain : true, // 是否为透明背景
					html : '<iframe src='
							+ url
							+ ' height="100%" width="100%" frameborder=0 border=0/>'
				});
		taskStatusWindow.show();
	} else {
		Ext.Msg.alert('提示', '请选择网管');
	}
}

function modeColorGrid(v,m){
	if(v=='自动'){
		m.css='x-grid-font-blue';
	}else if(v=='手动'){
		m.css='x-grid-font-orange';
	}else{
		m.css='x-grid-font-red';
	}
	return v;
}

function colorGrid(v,m) {
	if(v=='连接正常'){
		m.css='x-grid-font-blue';
	}else if(v=='网络中断'){
		m.css='x-grid-font-red';
	}else if(v=='连接异常'){
		m.css='x-grid-font-orange';
	}
	return v;
}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout=900000; 
	//collapse menu
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
	Ext.Msg = top.Ext.Msg;
	
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [connectListPanel]
	});
	
	//放最后才能显示遮罩效果
	store.load({
		callback: function(r, options, success){   
			if(success){  
	
			}else{
				Ext.Msg.alert('错误','加载失败！');    
			}
		}
	}); 
 });