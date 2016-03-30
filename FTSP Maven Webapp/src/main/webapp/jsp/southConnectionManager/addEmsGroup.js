var emsGroupName = new Ext.form.TextField({
	id : 'emsGroupName',
	name : 'emsGroupName',
	fieldLabel : '网管分组名称',
	emptyText : '请输入网管分组名称........',
	sideText : '<font color=red>*</font>',
	minLength : 1,
	maxLength : 40,
	allowBlank : false,
	anchor : '95%'
//	,listeners : {
//		blur : function(t) {
//			var param = {
//				'emsGroupModel.emsGroupName' : t.getValue()
//			};
//			Ext.Ajax.request({
//				url : 'connection!checkNameExist.action',
//				method : 'POST',
//				params : param,
//				success : function(response) {
//					var result = Ext.util.JSON.decode(response.responseText);
//					if (result) {
//						if (0 == result.returnResult) {
//							Ext.MessageBox.show({
//								title : '信息',
//								width : 350,
//								height : 45,
//								msg : '该网管分组名称已存在,网管分组名称不可重复。请修改！',
//								buttons : Ext.MessageBox.OK,
//								icon : Ext.MessageBox.alert
//							});
//						}
//					}
//				}
//			});
//		}
//	}
});

var emsGroupNote = new Ext.form.TextField({
	id : 'emsGroupNote',
	name : 'emsGroupNote',
	fieldLabel : '网管分组备注',
	emptyText : '请输入网管分组备注........',
	minLength : 0,
	maxLength : 128,
	allowBlank : true,
	anchor : '95%'
});

var formPanel = new Ext.FormPanel({
	id : 'formPanel',
	border : true,
	//	title : "新增网管分组",
	region : 'center',
	labelWidth : 100,
	//autoScroll : true,
	bodyStyle : 'padding:10px 12px 0;',
	items : [ emsGroupName, emsGroupNote ],
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
	if (formPanel.getForm().isValid()) {
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
    		var obj = Ext.decode(response.responseText);
	    	if(obj.returnResult == 1){
				Ext.Msg.alert("信息", obj.returnMessage, function(r) {
					//刷新列表
					var pageTool = parent.Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
					//关闭修改任务信息窗口
					var win = parent.Ext.getCmp('addEmsGroupWindow');
					if (win) {
						win.close();
					}
				});
            }
        	if(obj.returnResult == 0){
        		Ext.Msg.alert("信息",obj.returnMessage);
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