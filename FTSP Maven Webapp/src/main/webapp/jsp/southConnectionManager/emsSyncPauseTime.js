var Data_pauseTime = [['10', '10分钟'], ['20', '20分钟'],
                      ['30', '30分钟'], ['40', '40分钟'],
                      ['50', '50分钟'], ['60', '60分钟'],
                      ['70', '70分钟'], ['80', '80分钟'],
                      ['90', '90分钟']];
var pauseTimeStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'value'
					}, {
						name : 'displayName'
					}]
		});
pauseTimeStore.loadData(Data_pauseTime);

var formPanel = new Ext.FormPanel({
	id:"formPanel",
//	title:'采集任务控制',
	region:"center",
    frame:false,
    border:false,
    bodyStyle:'padding:10px 10px 0 10px',
	height: 100,
    labelWidth: 80,
    labelAlign: 'right',
    collapsed: false,   // initially collapse the group
    collapseMode: 'mini',
    split:true,
    items: [{
            layout: 'form',
            labelSeparator:"：",
            border:false,
            items: [{
            	xtype:'combo',
            	id:'pauseCombo',
				name: 'pauseCombo',
				fieldLabel: '暂停采集',
				emptyText:'请选择暂停时间',
				triggerAction: 'all',
				store:pauseTimeStore,
				mode:'local',
				valueField: 'value',
				displayField: 'displayName',
				allowBlank:false,
				editable:false,
				width:200,
				listeners : {
					'select' : function() {
						var pauseTime = Ext.getCmp('pauseCombo').getValue();
						if (pauseTime > 0) {
//							pauseTimeStore.loadData(Data_pauseTime);
							Ext.getCmp('explain').setValue("<font color=red>注：暂停后将继续采集</font>");
						}
					}
				}
            },{
				xtype : 'displayfield',
				id : 'explain',
				name : 'explain',
				fieldLabel : '',
				width : 200,
				height : 25
			}]
    }],
	buttons : [ {
		text : '确定',
		handler : function() {
			saveConfig();
		}
	}, {
		text : '取消',
		handler : function() {
			//关闭修改任务信息窗口
			var win = parent.Ext.getCmp('pauseTimeWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});

function saveConfig() {
	if (formPanel.getForm().isValid()) {
		var minutes = Ext.getCmp("pauseCombo").getValue();

		var jsonData = {
			"emsConnectionId" : emsConnectionId,
			"taskId" : taskId,
			"minutes":minutes
		};

		Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url : 'connection!updateEmsConnectionSync.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				Ext.getBody().unmask();
				
				// 刷新列表
				var pageTool = parent.parent.Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
				//关闭修改任务信息窗口
				var win = parent.Ext.getCmp('pauseTimeWindow');
				if(win){
					win.close();
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
		parent.Ext.getCmp('proceed').enable();
		parent.Ext.getCmp('stop').enable();
	} else {
		
	}
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
});