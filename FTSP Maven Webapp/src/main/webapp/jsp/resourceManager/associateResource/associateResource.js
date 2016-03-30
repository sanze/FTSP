/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

var jsonString = new Array();

var store = new Ext.data.Store({
	url : 'associate-resource!resourceTaskInit.action',
	reader : new Ext.data.JsonReader({
			root : 'rows',//json数据的key值
			fields :["RC_TASK_ID","TASK_NAME", "DESCRIPTION", "PERIOD",
						"TASK_STATUS", "LATEST_EXECUTE_TIME", "LATEST_EXECUTE_RESULT", "NEXT_EXECUTE_TIME"
						]
	})
});
store.load();

// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
//	header: ""
});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
		// columns are not sortable by default
	},
	columns : [new Ext.grid.RowNumberer({
						id : 'rowNumbererId',
						width : 26,
					}),checkboxSelectionModel,{
						id : 'RC_TASK_ID',
						header : '任务编号',
						dataIndex : 'RC_TASK_ID',
						hidden : true,
						width : 80
			}, {
				id : 'TASK_NAME',
				header : '任务名称',
				dataIndex : 'TASK_NAME',
				width : 100
			}, {
				id : 'DESCRIPTION',
				header : '描述',
				dataIndex : 'DESCRIPTION',
				width : 300
			}, {
				id : 'PERIOD',
				header : '同步周期',
				dataIndex : 'PERIOD',
				width : 100,
				renderer : function(v) {
					var hours = 0;
					var minutes = 0;
					if (v/60 < 10) {
						hours = '0' + Math.floor(v/60);
					} else {
						hours = Math.floor(v/60);
					}
					if (v%60 < 10) {
						minutes = '0' + v%60;
					} else {
						minutes = v%60;
					}
					return '每天 ' + hours + ':' + minutes;
				}
			}, {
				id : 'TASK_STATUS',
				header : '状态',
				dataIndex : 'TASK_STATUS',
				width : 70,
				renderer : function(v) {
					if (v == '1') {
						return "启用";
					} else if (v == '0') {
						return "挂起";
					} else {
						return v;
					}
				}
			}, {
				id : 'LATEST_EXECUTE_TIME',
				header : '上次执行时间',
				dataIndex : 'LATEST_EXECUTE_TIME',
				width : 150
			},{
				id : 'LATEST_EXECUTE_RESULT',
				header : '上次执行结果',
				dataIndex : 'LATEST_EXECUTE_RESULT',
				width : 100
			},{
				id : 'NEXT_EXECUTE_TIME',
				header : '下次执行时间',
				dataIndex : 'NEXT_EXECUTE_TIME',
				width : 150,
				renderer : function(v, r, t) {
					if (t.get("TASK_STATUS") == '0') {
						return "";
					} else {
						return v;
					}
				}
			}],
			autoScroll : true
});

var pageTool = new Ext.PagingToolbar({
			id : 'pageTool',
			pageSize : 200,// 每页显示的记录值
			store : store,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});
var gridPanel = new Ext.grid.EditorGridPanel({
			id : "gridPanel",
			region : "center",
			cm : cm,
			store : store,
			stripeRows : true, // 交替行效果
			loadMask : true,
			selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
			forceFit : true,
//			tbar: pageTool,
//			viewConfig : {
//				forceFit : true
//			},
			bbar : pageTool, 
			tbar : ['-', {
						text : '任务状态',
						privilege : viewAuth,
						handler : displayTaskState

					}, '-',{
						text : '手动执行',
						privilege : modAuth,
						handler : executeManually
					}, '-',{
						text : '设置',
						privilege : modAuth,
						handler : setting
					}, '-',{
						text : '启用',
						privilege : modAuth,
						handler : startUsing
					}, '-',{
						text : '挂起',
						privilege : modAuth,
						handler : holdOn
					}]
		});

//任务状态
function displayTaskState() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length <= 0) {
		Ext.Msg.alert('信息', '请选择一个任务！');
	} else if (cell.length > 1) {
		Ext.Msg.alert('信息', '只能选择一个任务！');
	} else if (cell.length == 1) {
//		Ext.Msg.confirm('提示', '确认选择这个任务？', function(button) {
//		
//		if (button == 'yes') {
			var taskId = cell[0].get("RC_TASK_ID");
			var url = "displayTaskStatewindow.jsp";
			displayTaskStateWindow = new Ext.Window({
				id : 'displayTaskStateWindow',
				title : '任务状态',
				width : Ext.getBody().getWidth()*0.7,
				height : Ext.getBody().getHeight()-90, 
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
					+ url + '?taskId=' + taskId
					+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
			displayTaskStateWindow.show();
//		}
//	});
	}
}

//手动执行
function executeManually() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length <= 0) {
		Ext.Msg.alert('信息', '请选择一个任务！');
	} else if (cell.length > 1) {
		Ext.Msg.alert('信息', '只能选择一个任务！');
	} else if (cell.length == 1) {
		var taskId = cell[0].get("RC_TASK_ID");
		Ext.Ajax.request({
			url:'associate-resource!manualStart.action',
			method:'Post',
			params: {'rcTaskId':taskId},
			success: function(response) {
				var obj = Ext.decode(response.responseText);
				if(obj.returnResult == false){
					Ext.Msg.alert("提示",obj.msg);
				}else{
					// 刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				}
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

//设置
function setting() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length <= 0) {
		Ext.Msg.alert('信息', '请选择要设置的任务！');
	} else if (cell.length > 1) {
		Ext.Msg.alert('信息', '只能对一个任务进行设置！');
	} else if (cell.length = 1) {
		Ext.Msg.confirm('提示', '确认对这个任务进行设置？', function(button) {
			if (button == 'yes') {
				var url = "associatedTaskSetting.jsp?rcTaskId=" + cell[0].get("RC_TASK_ID");		
				associatedTaskSetting = new Ext.Window({
					id : 'associatedTaskSetting',
					title : '设置',
					width : 380,
					height : 350,
					isTopContainer : true,
					modal : true,
					plain : true, // 是否为透明背景
					html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
				});
				associatedTaskSetting.show();
			}
		});

	}
}

//启用
function startUsing() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		Ext.Msg.confirm('提示', '确认启用选择的任务？', function(button) {
			if (button == 'yes') {
				var resourceTaskList = new Array();
				for (var i = 0; i < cell.length; i++) {
					resourceTaskList.push(cell[i].get("RC_TASK_ID"));
				}
				var jsonData = {
						"resourceTaskList" : resourceTaskList
				};
				
				Ext.Ajax.request({
					url:'associate-resource!startTask.action',
					method:'Post',
					params:jsonData,
					success: function(response) {
						var obj = Ext.decode(response.responseText);
						if(obj.returnResult == false){
							Ext.Msg.alert("提示",obj.msg);
						}else{
							// 刷新列表
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						}
				    },	
				    error:function(response) {
				    	Ext.Msg.alert("错误",response.responseText);
				    },
				    failure:function(response) {
				    	Ext.Msg.alert("错误",response.responseText);
				    }		   
				});
			}
		});

	} else {
		Ext.Msg.alert('信息', '请选择要启用的任务！');
	}

}
// 挂起
function holdOn() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		Ext.Msg.confirm('提示', '确认挂起选择的任务？', function(button) {
			if (button == 'yes') {
				

				var resourceTaskList = new Array();
				for (var i = 0; i < cell.length; i++) {
					resourceTaskList.push(cell[i].get("RC_TASK_ID"));			
				}
				var jsonData = {
						"resourceTaskList" : resourceTaskList
				};
				
				Ext.Ajax.request({
					url:'associate-resource!holdOn.action',
					method:'Post',
					params:jsonData,
					success: function(response) {
						var obj = Ext.decode(response.responseText);
						if(obj.returnResult == false){
							Ext.Msg.alert("提示",obj.msg);
						}else{
							// 刷新列表
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						}
				    },	
				    error:function(response) {
				    	Ext.Msg.alert("错误",response.responseText);
				    },
				    failure:function(response) {
				    	Ext.Msg.alert("错误",response.responseText);
				    }		   
				});
			
				
			}
		});

	} else {
		Ext.Msg.alert('信息', '请选择要挂起的任务！');
	}
}

Ext.onReady(function() {

	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;

	var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [gridPanel]
			});
	});
