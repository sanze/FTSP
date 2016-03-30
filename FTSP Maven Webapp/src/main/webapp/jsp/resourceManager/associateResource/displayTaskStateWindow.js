/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// ============create the Data Store==========
// --------------已分配用户权限列表----------------
//================假的store========================
var store = new Ext.data.Store({
	url : 'associate-resource!getResourceTaskStatus.action',
	baseParams : {"rcTaskId" : taskId},
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, ["TIME","ACTION"])
});

store.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});


// ==================页面====================
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		menuDisabled : true
		// columns are not sortable by default
	},
	columns : [new Ext.grid.RowNumberer(),{
				id : 'TIME',
				header : '时间',
				dataIndex : 'TIME',
				width : 120
			}, {
				id : 'ACTION',
				header : '执行记录',
				dataIndex : 'ACTION',
				width : 400
			}]
});

var gridPanel = new Ext.grid.EditorGridPanel({
			id : "gridPanel",
			region : "center",
			cm : cm,
			store:store,
			stripeRows : true, // 交替行效果
			forceFit : true,
			frame : false,
			collapsible : false,
			autoExpandColumn: 'ACTION',
			buttonAlign:"center",
			buttons : [{
				text : '关闭',
				handler : close
			}]
		});

// =================函数===================

function close() {
	var win = parent.Ext.getCmp('displayTaskStateWindow');
	if (win) {
		win.close();
	}
}

Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [gridPanel]
			});
		});