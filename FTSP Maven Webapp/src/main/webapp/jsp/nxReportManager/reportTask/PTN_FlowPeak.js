var TARGET_NAME = "系统";
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
var store = new Ext.data.Store({
	url : 'nx-report!getPtnSysList.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
	}, [ "GROUP_NAME", "EMS_DISPLAY_NAME", "SYS_NAME", "SYS_CAPACITY",
			"SYS_TYPE", "T_RESOURCE_PTN_SYS_ID", "TARGET_ID" ])
});

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
		privilege : addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : addPtnSys
	}, '-', {
		text : '删除',
		privilege : delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function(){deleteTarget(1);}
	}, '-', {
		text : '清空',
		privilege : delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function(){deleteTarget(2);}
	} ],
	buttons : [ '->', {
		text : '保存',
		privilege : addAuth,
		id : 'saveButton',
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function() {
			beforeSave(false,'T_RESOURCE_PTN_SYS_ID');
		}
	}, {
		text : '预览',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function() {
			beforeSave(true,'T_RESOURCE_PTN_SYS_ID');
		}
	} ]
});


var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '作业计划设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	autoScroll : true,
	labelAlign : 'left',
	height : 190,
	collapsible : true,
	items : [ rowI, rowII ]
});

function addPtnSys(){
	var win = new Ext.Window(
			{
				id : 'addPtnSysWin',
				title : "新增报表所含系统",
				width : Ext.getBody().getWidth() * 0.8,
				height : Ext.getBody().getHeight() - 80,
				closeAction : 'close',
				layout : 'fit',
				stateful : false,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe id="addPtnSys_f" name = "addPtnSys_f" src = "addPtnSys.jsp" height="100%" width="100%"  frameBorder=0 border=0/>',
				buttons : [ {
					text : "确定",
					handler : function() {
						var addWin = window.frames["addPtnSys_f"];
						addWin.add();
					}
				}, {
					text : "取消",
					handler : function() {
						win.close();
					}
				} ]
			});
	win.show();
}
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓



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
				if (!!taskNodes && taskNodes.length > 0) {
					var nodeList = new Array();
					for ( var i = 0; i < taskNodes.length; i++) {
						nodeList.push(taskNodes[i]['nodeId']);
					}
					store.baseParams = {
						'intList' : nodeList
					};
					store.load({
						callback : function(records, options, success) {
							// records：加载的数据数组 ，options:调用load方法的配置对象 ，success:
							// 布尔值，是否加载成功
							if (!success) {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							} else {
							}
						}
					});
				}
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
// --------------------------------------------------------------------
// ---------------------------------------------------------------------
Ext
		.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			var mainWin = new Ext.Viewport({
				id : 'mainWin',
				title : "新增报表",
				layout : 'border',
				// autoScroll:true,
				items : [ northPanel, grid ],
				renderTo : Ext.getBody()
			});
			mainWin.show();
			Ext.getCmp("periodCb").disable();
			Ext.getCmp("dataSrcCombo").disable();
			if (!!taskId) {
				initTaskInfo();
				if (userId != creatorId && userId != -1)
					Ext.getCmp("saveButton").disable();
			}
		});