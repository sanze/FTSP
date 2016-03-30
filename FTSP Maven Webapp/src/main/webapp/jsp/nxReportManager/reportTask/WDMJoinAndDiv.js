Ext.override(Ext.form.CheckboxGroup, {
	getValue : function(mode) {
		var v = [];
		if (mode == 1) {
			this.items.each(function(item) {
				if (item.getValue())
					v.push(item.getRawValue());
			});
			return v;
		} else {
			this.items.each(function(item) {
				v.push(item.getValue());
			});
			return v;
		}
	}
});


/* 【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/


var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	title : TARGET_NAME+'选择',
	store : store,
	cm : cm,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-', {
		text : '新增',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : function(){addOrEditTarget(1);}
	},'-', {
		text : '修改',
		privilege:modAuth,
		icon : '../../../resource/images/btnImages/modify.png',
		handler : function(){addOrEditTarget(2);}
	}, '-', {
		text : '删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function(){deleteTarget(1);}
	}, '-', {
		text : '清空',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function(){deleteTarget(2);}
	} ],
	buttons : [ '->', {
		text : '保存',
		id : 'saveButton',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function() {
			beforeSave(false,'RESOURCE_UNIT_MANAGE_ID');
		}
	}, {
		text : '预览',
		privilege:viewAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function() {
			beforeSave(true,'RESOURCE_UNIT_MANAGE_ID');
		}
	} ]
});

var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	autoScroll : true,
	labelAlign : 'left',
	height : 190,
	collapsible : true,
	items : [ rowI, rowII ]
});

/**【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/

/* 【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/



/**
 * 修改时初始化页面
 */
function initTaskInfo() {
	var param = {
		'paramMap.taskId' : taskId
	};
	top.Ext.getBody().mask("正在加载");
	Ext.Ajax.request({
		url : 'nx-report!initReportTaskInfo.action',
		method : 'POST',
		params : param,
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult != 0) {
				var taskInfo = result.taskInfo[0];
				var taskNodes = result.taskNodes;
				// 上半
				if (!!taskInfo) {
					Ext.getCmp('reportTaskName').setValue(taskInfo.taskName);
					Ext.getCmp('privilege').setValue(taskInfo.privilege);
					Ext.getCmp('periodCb').setValue(taskInfo.period);
					Ext.getCmp('hourCb').setValue(taskInfo.hour);
					Ext.getCmp('dataSrcCombo').setValue(taskInfo.dataSrc);
					if (taskInfo.dataSrc == 1) {
						Ext.getCmp('continueAbnormal').setVisible(true);
						Ext.getCmp('continueAbnormal').setValue(
								taskInfo.continueAbnormal);
					} else {
						Ext.getCmp('continueAbnormal').setVisible(false);
					}
					if (taskInfo.period == 0) {
						delay4MonthlyCb.hide();
						delay4DailyCb.show();
						delay4DailyCb.setValue(taskInfo.delay);
					}
					if (taskInfo.period == 1) {
						delay4DailyCb.hide();
						delay4MonthlyCb.show();
						delay4MonthlyCb.setValue(taskInfo.delay);
					}
				}
				// 下半
				store.baseParams = {'taskId':taskId,'reportType':TASK_TYPE};
				store.load();
			} else {
				Ext.Msg.alert("提示", result.returnMessage);
			}
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

/**【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/

Ext.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			var win = new Ext.Viewport({
				id : 'win',
				title : "新增报表",
				layout : 'border',
				// autoScroll:true,
				items : [ northPanel, grid ],
				renderTo : Ext.getBody()
			});
			win.show();
			if (!!taskId) {
				initTaskInfo();
				if (userId != creatorId && userId != -1)
					Ext.getCmp("saveButton").disable();
			}
		});