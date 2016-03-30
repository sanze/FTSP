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
			var win = parent.Ext.getCmp('modifyEmsGroupWindow');
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
		"emsGroupModel.emsGroupId" : emsGroupId,
		"emsGroupModel.emsGroupName" : emsGroupName,
		"emsGroupModel.emsGroupNote" : emsGroupNote
	};
	Ext.Ajax.request({
		url : 'connection!modifyEmsGroup.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("信息", obj.returnMessage, function(r) {
				if (obj.returnResult == 1) {
					// 刷新列表
					var pageTool = parent.Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
					// 关闭修改任务信息窗口
					var win = parent.Ext.getCmp('modifyEmsGroupWindow');
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

function initData(emsGroupId) {
	var jsonData = {
		"emsGroupModel.emsGroupId" : emsGroupId,
		"limit" : 200
	};
	Ext.Ajax.request({
		url : 'connection!getEmsGroupListByGroupId.action',
		type : 'post',
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);

			Ext.getCmp("emsGroupName").setValue(obj.rows[0].GROUP_NAME);
			if (null == obj.rows[0].NOTE) {

			} else {
				Ext.getCmp("emsGroupNote").setValue(obj.rows[0].NOTE);
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

	initData(emsGroupId);

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ formPanel ],
		renderTo : Ext.getBody()
	});
});