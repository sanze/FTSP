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

var dataSrcComboStore;
(function() {
	var data = [ [ 0, '原始数据' ], [ 1, '异常数据' ] ];
	dataSrcComboStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'src'
		} ]
	});
	dataSrcComboStore.loadData(data);
})();

var storeNe = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsGroup","ems", "subNet", "ne", "neType", "emsId", "subNetId", "neId",
			"nodeLevel", 'nodeId' ])
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cmNe = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 100
	}, {
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 100
	}, {
		id : 'subNet',
		header : '子网',
		dataIndex : 'subNet',
		width : 100
	}, {
		id : 'ne',
		header : '网元',
		dataIndex : 'ne',
		width : 150
	}, {
		id : 'neType',
		header : '网元型号',
		dataIndex : 'neType',
		width : 100
	}, {
		id : 'type',
		header : '类型',
		dataIndex : 'nodeLevel',
		hidden : true,
		width : 100
	} ]
});

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	// title:'网元选择',
	store : storeNe,
	cm : cmNe,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-',{
		text : '添加',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : addNe
	}, '-',{
		text : '删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
			var items = grid.getSelectionModel().getSelections();
			if(items.length>0){
				Ext.Msg.confirm("提示","是否删除选中的对象？",function(btn){
					if(btn=="yes"){
						for ( var i = 0; i < items.length; i++) {
							storeNe.remove(items[i]);
						}
						storeNe.commitChanges();
					}
				});
			}else{
				Ext.Msg.alert("提示","请先选取对象！");
			}
		}
	}, '-',{
		text : '清空',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function() {			
			Ext.Msg.confirm("提示","是否清空所有对象数据？",function(btn){
			if(btn=="yes"){
				storeNe.removeAll();
				storeNe.commitChanges();
			}
		});
		}
	} ],
	buttons : [ '->', {
		text : '保存',
		id:'save',
		privilege:modAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : beforeSave
	} ]
});
// -----------------------------------------------------------------------
var privilegeStore = new Ext.data.Store({
	url : 'pm-report!getPrivilegeGroupList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ {
		name : "groupId",
		mapping : "SYS_USER_GROUP_ID"
	}, {
		name : "groupName",
		mapping : "GROUP_NAME"
	} ])
});
privilegeStore.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			var obj = Ext.decode(r.responseText);
			Ext.Msg.alert("提示", obj.returnMessage);
		}
	}
});
var privilegeCombo = new Ext.ux.form.LovCombo({
	id : 'privilege',
	fieldLabel : '报表共享范围',
	hideOnSelect : false,
	width : 200,
	anchor : '95%',
	editable : false,
	triggerAction : 'all',
	store : privilegeStore,
	displayField : 'groupName',
	valueField : 'groupId'
});

var rowI = {
	layout : 'column',
	width:1120,
	border : false,
	items : [ {
		columnWidth : 1,
		border : false,
		layout : 'form',
		items : [ {
			xtype : 'fieldset',
			title : '基本设置 ',
			layout : 'column',
			items : [
					{
						columnWidth : 0.2,
						layout : 'form',
						labelWidth : 60,
						border : false,
						items : [ {
							xtype : 'textfield',
							id : 'reportTaskName',
							anchor : '95%',
							allowBlank : false,
							fieldLabel : '任务名称'
						} ]
					},
					{
						columnWidth : 0.2,
						layout : 'form',
						labelWidth : 60,
						border : false,
						items : [ {
							xtype : 'combo',
							id : 'dataSrcCombo',
							store : dataSrcComboStore,
							fieldLabel : '数据源',
							triggerAction : 'all',
							anchor : '95%',
							mode : 'local',
							value : 0,
							editable : false,
							allowBlank : false,
							valueField : 'id',
							displayField : 'src',
							listeners : {
								'select' : function(combo, record, index) {
									if (combo.getValue() === 0) {
										// Ext.getCmp('continueAbnormal').setDisabled(true);
										Ext.getCmp('continueAbnormal')
												.setVisible(false);
									}
									if (combo.getValue() === 1) {
										// Ext.getCmp('continueAbnormal').setDisabled(false);
										Ext.getCmp('continueAbnormal')
												.setVisible(true);
									}
								}
							}
						} ]
					}, {
						columnWidth : 0.15,
						layout : 'form',
						id : 'thisCol',
						labelWidth : 70,
						border : false,
						items : [ {
							xtype : 'numberfield',
							id : 'continueAbnormal',
							hidden : true,
							// disabledClass:'x-item-disabled-modified',
							anchor : '90%',
							emptyText : '1~10',
							maxValue : 10,
							minValue : 1,
							allowDecimals : false,
							allowNegative : false,
							sideText : '次',
							allowBlank : true,
							fieldLabel : '连续异常≥'
						} ]
					}, {
						columnWidth : 0.16,
						layout : 'form',
						labelWidth : 40,
						border : false,
						items : [ {
							xtype : 'checkbox',
							id : 'needExport',
							boxLabel : '是否生成Excel',
							checked : true,
							disabled : true
						} ]

					}, {
						columnWidth : 0.24,
						layout : 'form',
						labelWidth : 80,
						border : false,
						items : [ privilegeCombo ]

					} ]
		} ]
	} ]

};

var periodCb;
(function() {
	var data = [ [ 0, '日报' ], [ 1, '月报' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'period'
		} ]
	});
	store.loadData(data);
	periodCb = new Ext.form.ComboBox({
		id : 'periodCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '类型',
		anchor : '95%',
		store : store,
		editable : false,
		value : 0,
		valueField : 'id',
		displayField : 'period'
	});
})();
periodCb.on('select', function(combo, record, index) {
	if (combo.getValue() == 0) {
		delay4MonthlyCb.hide();
		delay4DailyCb.show();
	}
	if (combo.getValue() == 1) {
		delay4DailyCb.hide();
		delay4MonthlyCb.show();
	}
});
// 延迟时间-日报
var delay4DailyCb;
(function() {
	var data = [ [ 2, '两天' ], [ 3, '三天' ], [ 4, '四天' ],
			[ 5, '五天' ], [ 6, '六天' ], [ 7, '七天' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'delay'
		} ]
	});
	store.loadData(data);
	delay4DailyCb = new Ext.form.ComboBox({
		id : 'delay4DailyCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '生成延迟',
		anchor : '95%',
		store : store,
		value : 2,
		editable : false,
		valueField : 'id',
		displayField : 'delay'
	});
})();
// 延迟时间-月报
var delay4MonthlyCb;
(function() {
	var data = [ [ 3, '每月3日' ], [ 4, '每月4日' ], [ 5, '每月5日' ], [ 6, '每月6日' ], [ 7, '每月7日' ],
	 			[ 8, '每月8日' ], [ 9, '每月9日' ], [ 10, '每月10日' ], [ 11, '每月11日' ],
	 			[ 12, '每月12日' ], [ 13, '每月13日' ], [ 14, '每月14日' ], [ 15, '每月15日' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'delay'
		} ]
	});
	store.loadData(data);
	delay4MonthlyCb = new Ext.form.ComboBox({
		id : 'delay4MonthlyCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '生成日期',
		anchor : '95%',
		editable : false,
		store : store,
		hidden : true,
		value : 3,
		valueField : 'id',
		displayField : 'delay'
	});
})();

// 采集完成几小时后
var hourCb;
(function() {
	var data = [ [ 1, '采集结束一小时后' ], [ 2, '采集结束两小时后' ], [ 3, '采集结束三小时后' ],
			[ 4, '采集结束四小时后' ], [ 5, '采集结束五小时后' ], [ 6, '采集结束六小时后' ],
			[ 7, '采集结束七小时后' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'hour'
		} ]
	});
	store.loadData(data);
	hourCb = new Ext.form.ComboBox({
		id : 'hourCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '生成时间',
		anchor : '95%',
		store : store,
		editable : false,
		value : 1,
		valueField : 'id',
		displayField : 'hour'
	});
})();

var rowII = {
	layout : 'column',
	width:1120,
	border : false,
	items : [ {
		columnWidth : 1,
		border : false,
		layout : 'form',
		items : [ {
			xtype : 'fieldset',
			title : '报表周期 ',
			layout : 'column',
			items : [ {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ periodCb ]
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ delay4DailyCb, delay4MonthlyCb ]
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ hourCb ]
			} ]
		} ]
	} ]

};

var rowIII = {
	id : 'rowIII',
	width:1120,
	border : false,
	layout : 'column',
	items : [ {
		columnWidth : 0.5,
		layout : 'form',
		border : false,
		items : [ SDH ]
	}, {
		columnWidth : 0.5,
		layout : 'form',
		border : false,
		items : [ WDM ]
	} ]
};

var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	autoScroll:true,
	labelAlign : 'left',
	height : 440,
	collapsible : true,
	items : [ rowI, rowII, rowIII ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
function beforeSave() {
	if (Ext.getCmp('northPanel').getForm().isValid()) {
		if (storeNe.getCount() < 1) {
			Ext.Msg.alert('信息', '请添加网元！');
			return;
		}
		// alert();
		var taskName = Ext.getCmp('reportTaskName').getValue();
		var param = {
			'searchCond.taskName' : taskName,
			'searchCond.taskId' : taskId
		};
		Ext.Ajax.request({
			url : 'pm-report!checkTaskNameDuplicate.action',
			method : 'POST',
			params : param,
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				// gridPanel.getEl().unmask();
				if (result.returnResult == 0) {
					Ext.Msg.alert("提示", result.returnMessage);
				} else {
					saveTask();
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				// gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}

function saveTask() {
	var otherWDMTP = Ext.getCmp('WDMTPLevelOther').getValue() ? 1 : 0;
	var otherSDHTP = Ext.getCmp('SDHTPLevelOther').getValue() ? 1 : 0;
	// SDH性能参数
	var SdhPhy = getPmChecked.getSdhPhysical();
	var SdhNum = getPmChecked.getSdhNumeric();
	var SdhPm = new Array();
	SdhPm.push.apply(SdhPm, SdhPhy);
	SdhPm.push.apply(SdhPm, SdhNum);
	var SdhTp = getPmChecked.getSdhTp();
	// WDM性能参数
	var WdmPhy = getPmChecked.getWdmPhysical();
	var WdmNum = getPmChecked.getWdmNumberic();
	var WdmPm = new Array();
	WdmPm.push.apply(WdmPm, WdmPhy);
	WdmPm.push.apply(WdmPm, WdmNum);
	var WdmTp = getPmChecked.getWdmTp();
	var taskName = Ext.getCmp('reportTaskName').getValue();
	var dataSrc = Ext.getCmp('dataSrcCombo').getValue();// 0:正常；1:异常
	var needExport = Ext.getCmp('needExport').getValue() ? 1 : 0;
	var continueAbnormal = Ext.getCmp('continueAbnormal').getValue();
	var privilege = Ext.getCmp('privilege').getValue();
	var period = Ext.getCmp('periodCb').getValue();// 0:日报；1：月报
	var hour = Ext.getCmp('hourCb').getValue();// 采集结束后几小时
	var delay = period == 0 ? Ext.getCmp('delay4DailyCb').getValue() : Ext
			.getCmp('delay4MonthlyCb').getValue();// 延迟几天
	
	//SDH的选择情况
	var SDHPhyCheckedStatus = Ext.getCmp('SDHPhysical').getValue(0);
	var SDHNumCheckedStatus = Ext.getCmp('SDHNumberic').getValue(0);
	var SDHTpCheckedStatus = Ext.getCmp('SDHTPLevel').getValue(0);
	var SDHMaxMin = Ext.getCmp('SDHMaxMin').getValue();
	//WDM的选择情况
	var WDMPhyCheckedStatus = Ext.getCmp('WDMPhysical').getValue(0);
	var WDMNumCheckedStatus = Ext.getCmp('WDMNumberic').getValue(0);
	var WDMTpCheckedStatus = Ext.getCmp('WDMTPLevel').getValue(0);
	var WDMMaxMin = Ext.getCmp('WDMMaxMin').getValue();
	
	var list = new Array();
	storeNe.each(function(record) {
		var nodes = {
			'nodeId' : record.get('nodeId'),
			'nodeLevel' : record.get('nodeLevel')
		};
		list.push(Ext.encode(nodes));
	});
	var params = {
		'modifyList' : list,
		'searchCond.otherWDMTP' : otherWDMTP,
		'searchCond.otherSDHTP' : otherSDHTP,
		'searchCond.SdhPm' : SdhPm.toString(),
		'searchCond.WdmPm' : WdmPm.toString(),
		'searchCond.SdhTp' : SdhTp.toString(),
		'searchCond.WdmTp' : WdmTp.toString(),
		'searchCond.taskName' : taskName,
		'searchCond.dataSrc' : dataSrc,
		'searchCond.needExport' : needExport,
		'searchCond.continueAbnormal' : continueAbnormal != '' ? continueAbnormal
				: 1,
		'searchCond.privilege' : privilege,
		'searchCond.period' : period,
		'searchCond.hour' : hour,
		'searchCond.delay' : delay,
		'searchCond.SDHPhyCheckedStatus' : SDHPhyCheckedStatus,
		'searchCond.SDHNumCheckedStatus' : SDHNumCheckedStatus,
		'searchCond.SDHTpCheckedStatus' : SDHTpCheckedStatus,
		'searchCond.WDMPhyCheckedStatus' : WDMPhyCheckedStatus,
		'searchCond.WDMNumCheckedStatus' : WDMNumCheckedStatus,
		'searchCond.WDMTpCheckedStatus' : WDMTpCheckedStatus,
		'searchCond.SDHMaxMin' : SDHMaxMin?1:0,
		'searchCond.WDMMaxMin' : WDMMaxMin?1:0,
		'searchCond.taskId' : taskId
	};
	top.Ext.getBody().mask('正在保存，请稍候...');
	Ext.Ajax.request({
		url : 'pm-report!updateNeReportTask.action',
		method : 'POST',
		params : params,
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage,function(){
				if (result.returnResult == 1) {
					var f = top.window.frames["f_自动作业计划管理"];
					var pageTool;
					if (f) {
						if (f.loadTaskName) {
							f.loadTaskName();
							pageTool = f.pageTool;
						} else {
							f.contentWindow.loadTaskName();
							pageTool = f.contentWindow.pageTool;
						}
						pageTool.doLoad(pageTool.cursor);
					}
					
					if (result.returnResult == 1)
						parent.closeTab('修改报表');
				}
			});
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

function initTaskInfo() {
	var param = {
		'searchCond.taskId' : taskId
	};
	top.Ext.getBody().mask("正在加载");
	Ext.Ajax.request({
		url : 'pm-report!initNEReportTaskInfo.action',
		method : 'POST',
		params : param,
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult != 0) {
				var taskInfo = result.taskInfo[0];
				var taskNodesInfo = result.taskNodesInfo;
				//上半
				if (taskInfo) {
					Ext.getCmp('reportTaskName').setValue(taskInfo.taskName);
					Ext.getCmp('needExport').setValue(
							taskInfo.needExport == 1 ? true : false);
					Ext.getCmp('privilege').setValue(taskInfo.privilege);
					Ext.getCmp('periodCb').setValue(taskInfo.period);
					Ext.getCmp('hourCb').setValue(taskInfo.hour);
					Ext.getCmp('dataSrcCombo').setValue(taskInfo.dataSrc);
					if(taskInfo.dataSrc==1){
						Ext.getCmp('continueAbnormal').setVisible(true);
						Ext.getCmp('continueAbnormal').setValue(taskInfo.continueAbnormal);
					}else{
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
					//各种选框还原
					Ext.getCmp('SDHTPLevel').setValue(taskInfo.SDHTpCheckedStatus.split(', '));
					Ext.getCmp('SDHPhysical').setValue(taskInfo.SDHPhyCheckedStatus.split(', '));
					Ext.getCmp('SDHNumberic').setValue(taskInfo.SDHNumCheckedStatus.split(', '));
					Ext.getCmp('SDHMaxMin').setValue(taskInfo.SDHMaxMin);
					Ext.getCmp('WDMTPLevel').setValue(taskInfo.WDMTpCheckedStatus.split(', '));
					Ext.getCmp('WDMPhysical').setValue(taskInfo.WDMPhyCheckedStatus.split(', '));
					Ext.getCmp('WDMNumberic').setValue(taskInfo.WDMNumCheckedStatus.split(', '));
					Ext.getCmp('WDMMaxMin').setValue(taskInfo.WDMMaxMin);
				}
				//下半
				if (taskNodesInfo) {
					storeNe.loadData(taskNodesInfo);
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

Ext
		.onReady(function() {
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
			initTaskInfo();
			if(creatorId!=userId){
				Ext.Msg.alert("提示","非当前用户创建的任务只能浏览不能修改！");
				Ext.getCmp('save').disable();
//				Ext.getCmp('northPanel').disable();
			}
			win.show();
		});