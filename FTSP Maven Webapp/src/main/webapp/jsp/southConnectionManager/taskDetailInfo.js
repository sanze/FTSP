var store = new Ext.data.Store(
{
	//1代表查询corba连接
	url: 'connection!getTaskDetailInfo.action',
	baseParams:{"emsConnectionId":emsConnectionId},
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    "emsConnectionId","taskType","emsName","neName","syncStatus"//,"syncResult"
    ])
});

 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit:false
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),{
            id: 'emsConnectionId',
            header: 'id',
            dataIndex: 'emsConnectionId',
            hidden:true
        },{
            id: 'emsDisplayName',
            header: '网管名称',
            width:120,
            dataIndex: 'emsName'
        },{
            id: 'taskType',
            header: '任务类别',
            dataIndex: 'taskType',
            width:120
        },{
            id: 'neDisplayName',
            header: '网元名称',
            width:120,
            dataIndex: 'neName'
        },{
            id: 'taskStatus',
		    header: '状态',
            dataIndex: 'syncStatus',
            width:180
        }]
    });

var connectListPanel = new Ext.grid.GridPanel({
	id:"connectListPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	clicksToEdit: 2,//设置点击几次才可编辑  
//	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox 
	viewConfig: {
        forceFit:false
    },
	listeners : {
    	'rowdblclick' : function(gridPanel, rowIndex, e){
    	},
    	'beforeedit' : function(e){}
    }
	
}); 

var formPanel = new Ext.FormPanel({
	id : 'formPanel',
	border : true,
//	title : "新增网管分组",
	region : 'center',
	labelWidth : 100,
	layout:'fit',
	autoScroll : true,
	bodyStyle : 'padding:10px 12px 0;',
	items : [ connectListPanel ],
	buttons : [ 
//	            {
//		id : 'setting',
//		text : '暂停',
//		handler : function() {
//			pauseTime();
//		}
//	}, 
	{
		id : 'proceed',
		text : '继续',
		handler : function() {
			proceedSetting();
		}
	}, {
		id :'stop',
		text : '停止',
		handler : function() {
			//关闭修改任务信息窗口
//			var win = parent.Ext.getCmp('taskStatusWindow');
//			if (win) {
//				win.close();
//			}
			stopTask();
		}
	} ]
});

function pauseTime() {
	var url = 'emsSyncPauseTime.jsp?emsConnectionId=' + emsConnectionId + '&taskId=' + taskId ;
	pauseTimeWindow = new Ext.Window({ 
		id:'pauseTimeWindow', 
		title:'暂停时间设置',
	    width : 370,  
	    height : 150, 
	    isTopContainer : true,
	    modal : true,
        plain:true,  //是否为透明背景   
		html : '<iframe  id="pauseWindow_panel" name = "pauseWindow_panel"  src='+url+' height="100%" width="100%" frameborder=0 border=0/>' 
	});  
	pauseTimeWindow.show();
//	Ext.getCmp('setting').hide();
	Ext.getCmp('proceed').show();
	Ext.getCmp('proceed').enable();
}

function proceedSetting() {
	var jsonData = {
		"emsConnectionId" : emsConnectionId,
		"taskId" : taskId
	};

	Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'connection!proceedTaskSetting.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				// 刷新列表
				Ext.Msg.alert("信息", obj.returnMessage, function(r) {
					// 刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				});
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}		
		},
		error : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("错误", obj.returnMessage);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("错误", obj.returnMessage);
		}
	});
}

function stopTask() {
	var jsonData = {
			"emsConnectionId" : emsConnectionId,
			"taskId" : taskId
		};

		Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url : 'connection!stopTaskSetting.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					// 刷新列表
					Ext.Msg.alert("信息", obj.returnMessage, function(r) {
						// 刷新父页面列表
						var pageTool = parent.Ext.getCmp('pageTool');			
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					});
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息", obj.returnMessage);
					// 刷新父页面列表
					var pageTool = parent.Ext.getCmp('pageTool');			
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
					// 刷新列表
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
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ formPanel ],
		renderTo : Ext.getBody()
		
	});
	
	//放最后才能显示遮罩效果
	store.load({
		callback: function(r, options, success){  
			
			if(success){ 
				// 刷新列表
				var pageTool = parent.Ext.getCmp('pageTool');
				
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
				if(executeStatus == 3 ) {
					Ext.getCmp('proceed').disable();
//					Ext.getCmp('setting').enable();
					Ext.getCmp('stop').enable();
				} else if( executeStatus == 5) {
					Ext.getCmp('proceed').enable();
//					Ext.getCmp('setting').enable();
					Ext.getCmp('stop').enable();
				} else if (executeStatus == 1 || executeStatus == 4) {
//					Ext.getCmp('setting').hide();
//					Ext.getCmp('proceed').isVisible(true);
					Ext.getCmp('proceed').disable();
//					Ext.getCmp('setting').disable();
					Ext.getCmp('stop').disable();	
				} else {
//					Ext.getCmp('setting').disable() ;
					Ext.getCmp('stop').disable();
					Ext.getCmp('proceed').disable();
				}
			}else{
				Ext.Msg.alert('错误','加载失败！');    
			}
		}
	}); 
});