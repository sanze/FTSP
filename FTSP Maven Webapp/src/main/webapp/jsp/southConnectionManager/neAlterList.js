var store = new Ext.data.Store({
	url : 'connection!getAlterNeByEmsConnectionId.action',
	baseParams : {          
		"emsConnectionId" : emsConnectionId
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "neName", "changeType" ])
});


var columnModel = new Ext.grid.ColumnModel({
    // specify any defaults for each column
    defaults: {
        sortable: true,
        forceFit: false
    },
    columns: [new Ext.grid.RowNumberer({
		width : 26
	}),{
        id: 'changeType',
        header: '变化情况',
        width:120,
        dataIndex: 'changeType',
		renderer : function(v) {
			if (v == 1) {
				return "新增网元";
			} else if (v == 2) {
				return "删除网元";
			} else {
				return "";
			}
		}
    },{
        id: 'neName',
        header: '网元名称',
        width:160,
        dataIndex: 'neName'
    }]
});


var connectListPanel = new Ext.grid.GridPanel({
	id : "connectListPanel",
	region : "center",
	// title : "链路同步部分成功，需要先同步以下网元基础数据",
	stripeRows : true,
	autoScroll : true,
	height: 300,

	frame : false,
	cm : columnModel,
	store : store,
	loadMask : true,
	viewConfig:{forceFit : true},
	buttons : [ {
		text : '更新',
		handler : function() {
			saveConfig();
		}
	}, {
		text : '取消',
		handler : function() {
			
			// 刷新列表
			var pageTool = parent.Ext.getCmp('pageTool');
//			alert(pageTool);
			if (pageTool) {
				pageTool.doLoad(pageTool.cursor);
			}
			//关闭修改任务信息窗口
			var win = parent.Ext.getCmp('neSyncListWindow');
			if (win) {
				win.close();
			}
		}
	}, {
		text : '导出',
		handler : function() {
			exportReport();
		}
	} ]
}); 

function exportReport() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "导出信息为空！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				exportRequest();
			}
		});
	} else
		exportRequest();
}

var exportRequest = function() {
	Ext.getBody().mask('正在导出到Excel，请稍候...');
	Ext.Ajax.request({
		url : 'connection!exportAlterNeExcel.action',
		type : 'post',
		params : {
			"emsConnectionId" : emsConnectionId
		},
		success : function(response) {
			Ext.getBody().unmask();
			var rs=Ext.decode(response.responseText);
			if(rs.returnResult==1 &&rs.returnMessage!=""){
				var destination={
						"filePath":rs.returnMessage
				};
				window.location.href="connection-download-excel!execute.action?"+Ext.urlEncode(destination);
			}
			else Ext.Msg.alert("提示","导出失败！");
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
};

function saveConfig() {
	var jsonData = {
		"emsConnectionId" : emsConnectionId
	};
		Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url : 'connection!neListSyncAdd.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {// 回调函数
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 0) {
					Ext.getBody().unmask();
					Ext.Msg.alert("信息", obj.returnMessage);
				}
				if (obj.returnResult == 1) {
					Ext.getBody().unmask();
					Ext.Msg.alert("信息", obj.returnMessage);
					// 刷新列表
					var pageTool = parent.Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
					//关闭修改任务信息窗口
					var win = parent.Ext.getCmp('neSyncListWindow');
					if (win) {
						win.close();
					}

				}
			},
			error : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("信息", obj.returnMessage);
			},
			failure : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("信息", obj.returnMessage);
			}
		});
};

//var formPanel = new Ext.FormPanel({
//	id : "formPanel",
//	region : "center",
//	frame : false,
//	border : false,
//	layout : 'border',
////	bodyStyle : 'padding:10px 10px 0 10px',
////	height : 375,
//	labelWidth : 80,
//	labelAlign : 'right',
//	collapsed : false, // initially collapse the group
//	collapseMode : 'mini',
//	split : true,
//	items : [ connectListPanel ],
//	buttons : [ {
//		text : '更新',
//		handler : function() {
//			saveConfig();
//		}
//	}, {
//		text : '取消',
//		handler : function() {
//			
//			// 刷新列表
//			var pageTool = parent.Ext.getCmp('pageTool');
//			alert(pageTool);
//			if (pageTool) {
//				pageTool.doLoad(pageTool.cursor);
//			}
//			
//			//关闭修改任务信息窗口
//			var win = parent.Ext.getCmp('neSyncListWindow');
//			if (win) {
//				win.close();
//			}
//		}
//	}, {
//		text : '导出',
//		handler : function() {
//			exportReport();
//		}
//	} ]
//});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Ajax.timeout=360000000; 
//	Ext.Msg = top.Ext.Msg;
	
	var win = new Ext.Viewport({
		id : 'win',
//		region : "center",
		layout : 'border',
		border:false,
		items : [ connectListPanel ],
		renderTo : Ext.getBody()
	});
	
	//放最后才能显示遮罩效果
	store.load({
		callback : function(r, options, success) {
			if (success) {
				if(store.getCount()==0){
					// 刷新列表
					var pageTool = parent.Ext.getCmp('pageTool');
//					alert(pageTool);
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
					
					//关闭修改任务信息窗口
					var win = parent.Ext.getCmp('neSyncListWindow');
					if (win) {
						win.close();
					}
					Ext.Msg.alert('信息', '网元列表无变化！');

				} else {
					
				}
			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});