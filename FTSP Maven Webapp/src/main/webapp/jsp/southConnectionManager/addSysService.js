var serviceName = new Ext.form.TextField({
	id : 'serviceName',
	name : 'serviceName',
	fieldLabel : '接入服务器名称',
	emptyText : '请输入接入服务器名称........',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	minLength : 1,
	maxLength : 40,
	anchor : '95%'
});

var ipAddress = new Ext.form.TextField(
		{
			xtype : 'textfield',
			id : 'ipAddress',
			name : 'ipAddress',
			fieldLabel : 'IP地址',
			emptyText : '请输入连接IP地址........',
			sideText : '<font color=red>*</font>',
			regex : /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
			allowBlank : false,
			anchor : '95%',
			listeners : {
				blur : function(t) {
					if(t.getValue() == "127.0.0.1"){
						Ext.Msg.alert("提示", "接入服务器 IP 不可以设置为 127.0.0.1 ！");
						return;
					}
					var param = {
						'emsConnectionModel.ip' : t.getValue()
					};
					Ext.Ajax.request({
						url : 'connection!checkIpAddressExist.action',
						method : 'POST',
						params : param,
						success : function(response) {
							var result = Ext.util.JSON
									.decode(response.responseText);
							if (result) {
								if (0 == result.returnResult) {
									Ext.MessageBox.show({
										title : '信息',
										width : 350,
										height : 45,
										msg : '该IP地址已经存在。请填写新的IP地址！',
										buttons : Ext.MessageBox.OK,
										icon : Ext.MessageBox.alert
									});
								}
							}
						}
					});
				}
			}
		});

var port = new Ext.form.TextField({
	id : 'port',
	name : 'port',
	fieldLabel : '端口号',
	emptyText : '请输入端口号........',
	sideText : '<font color=red>*</font>',
	minLength : 1,
	maxLength : 40,
	allowBlank : false,
	anchor : '95%'
});

var serviceNote = new Ext.form.TextField({
	id : 'serviceNote',
	name : 'serviceNote',
	fieldLabel : '接入服务器备注',
	emptyText : '请输入接入服务器备注........',
	minLength : 0,
	maxLength : 128,
	allowBlank : true,
	anchor : '95%'
});

var formPanel = new Ext.FormPanel({
	id : 'formPanel',
	name : 'formPanel',
	region : "center",
	border : false,
	frame : false,
	autoScroll : true,
	// labelWidth: 120,
	// width: 200,
	bodyStyle : 'padding:10px 12px 0;',
	items : [ serviceName, ipAddress, port, serviceNote ],
	buttons : [ {
		text : '确定',
		handler : function() {
			saveConfig();
		}
	}, {
		text : '取消',
		handler : function() {
			// 关闭修改任务信息窗口
			var win = parent.Ext.getCmp('addServiceWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});

function saveConfig() {
	if (formPanel.getForm().isValid()) {
		var serviceName = Ext.getCmp("serviceName").getValue();
		var serviceNote = Ext.getCmp("serviceNote").getValue();
		var port = Ext.getCmp("port").getValue();
		var ip = Ext.getCmp("ipAddress").getValue();

		Ext.getBody().mask('正在执行，请稍候...');

		var jsonAddData = {
			"sysServiceModel.serviceName" : serviceName,
			"sysServiceModel.note" : serviceNote,
			"sysServiceModel.port" : port,
			"sysServiceModel.ip" : ip
		};
		Ext.Ajax.request({
			url : 'connection!addSysService.action',
			method : 'POST',
			params : jsonAddData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				Ext.getBody().unmask();
				Ext.Msg.alert("信息", obj.returnMessage, function(r) {
					if (obj.returnResult == 1) {
						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						// 关闭修改任务信息窗口
						var win = parent.Ext.getCmp('addServiceWindow');
						if (win) {
							win.close();
						}
					}
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