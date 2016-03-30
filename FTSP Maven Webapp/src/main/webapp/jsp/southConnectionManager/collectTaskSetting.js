var Data_pauseTime = [['1', '10分钟'], ['2', '20分钟'],
                      ['3', '30分钟'], ['4', '40分钟'],
                      ['5', '50分钟'], ['6', '60分钟'],
                      ['7', '70分钟'], ['8', '80分钟'],
                      ['9', '90分钟']];
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
	title:'采集任务控制',
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
				triggerAction: 'local',
				store:pauseTimeStore,
				valueField: 'value',
				displayField: 'displayName',
				allowBlank:false,
				editable:false,
				width:200,
				listeners : {
					'select' : function() {
						var pauseTime = Ext.getCmp('pauseCombo').getValue();
						if (pauseTime > 0) {
							pauseTimeStore.loadData(Data_pauseTime);
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
			var win = parent.Ext.getCmp('addEmsGroupWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});

function saveConfig() {
	var emsGroupName = Ext.getCmp("emsGroupName").getValue();

	var emsGroupNote = Ext.getCmp("emsGroupNote").getValue();

	Ext.getBody().mask('正在执行，请稍候...');
	var jsonData = {
		"emsGroupModel.emsGroupName" : emsGroupName,
		"emsGroupModel.emsGroupNote" : emsGroupNote
	};
	Ext.Ajax.request({
		url : 'connection!addEmsGroup.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("信息", obj.returnMessage, function(r) {
				//刷新列表
				var pageTool = parent.Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
				Ext.Msg.confirm('信息', '继续添加？', function(btn) {
					if (btn == 'yes') {
						Ext.getCmp('emsGroupName').reset();
						Ext.getCmp('emsGroupNote').reset();
					} else {
						//关闭修改任务信息窗口
						var win = parent.Ext.getCmp('addEmsGroupWindow');
						if (win) {
							win.close();
						}
					}
				});
			});
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
});