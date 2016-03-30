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
// ----------------------------------------------------
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
// ---------------------------------------------------------
var storeMulti = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "trunkLineName", "MSName", "direction", "MSId", "MSNameList",
			"MSNameTag", 'TLId' ])
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cmMulti = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'trunkLineName',
		header : '干线名称',
		dataIndex : 'trunkLineName',
		width : 100
	}, {
		id : 'MSNameList',
		header : '包含复用段',
		dataIndex : 'MSNameList',
		hidden : true,
		width : 150,
		renderer : MSNameListRenderer
	}, {
		id : 'MSName',
		header : '复用段名称',
		dataIndex : 'MSName',
		width : 100
	}, {
		id : 'direction',
		header : '方向',
		dataIndex : 'direction',
		width : 100,
		renderer : directionRenderer
	} ]
});
// ---------------------------------------------------

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	store : storeMulti,
	cm : cmMulti,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-',{
		text : '添加复用段',
		privilege:addAuth,
		id : 'addMS',
		icon : '../../../resource/images/btnImages/add.png',
		handler : addMS
	},'-', {
		text : '添加干线',
		privilege:addAuth,
		id : 'addTL',
		icon : '../../../resource/images/btnImages/add.png',
		handler : addTL
	},'-', {
		text : '删除对象',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : del
	},'-', {
		text : '清空对象',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function() {
			Ext.Msg.confirm("提示","是否清空所有对象数据？",function(btn){
				if(btn=='yes'){
					storeMulti.removeAll();
					triggerForButton();
				}
			});
		}
	} ],
	buttons : [ '->', {
		text : '保存',
		privilege:modAuth,
		id:'save',
		icon : '../../../resource/images/btnImages/disk.png',
		handler : saveTask
	} ]
});
// -----------------------------------------------------------------------
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
			items : [ {
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
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ {
					xtype : 'checkbox',
					id : 'needExport',
					boxLabel : '是否生成Excel',
					checked : true,
					disabled : true
				} ]

			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 80,
				border : false,
				items : [ privilegeCombo ]

			} ]
		} ]
	} ]

};
// --------------------------------------------------------
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
		editable:false,
		store : store,
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
// -----------------------------------------------------------------
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
		editable:false,
		value : 2,
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
		store : store,
		editable:false,
		hidden : true,
		value : 3,
		valueField : 'id',
		displayField : 'delay'
	});
})();
// -------------------------------------------------------------------
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
		editable:false,
		value : 1,
		valueField : 'id',
		displayField : 'hour'
	});
})();
// --------------------------------------------------------------
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
				columnWidth : 0.21,
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

var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	autoScroll:true,
	labelAlign : 'left',
	height : 190,
	collapsible : true,
	items : [ rowI, rowII ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
function directionRenderer(v) {
	if (v == 1)
		return '单向';
	if (v == 2)
		return '双向';
}
function MSNameListRenderer(v, m, r) {
	var tag = "<div title=\"" + r.get('MSNameTag') + "\">" + v + "</div>";
	return tag;

}
// 从已选删除
function del() {
	var selected = grid.getSelectionModel().getSelections();
	if (selected && selected.length > 0) {
		Ext.Msg.confirm("提示","是否删除选中的对象？",function(btn){
			if(btn=="yes"){
				for ( var i = 0; i < selected.length; i++) {
					storeMulti.remove(selected[i]);
				}
				triggerForButton();
			}
		});
	}else{
		Ext.Msg.alert("提示","请先选取对象！");
	}
}

// 控制添加复用段和干线的按钮
function triggerForButton() {
	if (storeMulti.getCount() > 0) {
		if (storeMulti.getAt(0).get('TLId')) {
			Ext.getCmp('addMS').setDisabled(true);
			Ext.getCmp('addTL').setDisabled(false);
		}
		if (storeMulti.getAt(0).get('MSId')) {
			Ext.getCmp('addMS').setDisabled(false);
			Ext.getCmp('addTL').setDisabled(true);
		}
	} else {
		Ext.getCmp('addMS').setDisabled(false);
		Ext.getCmp('addTL').setDisabled(false);
	}
}

function beforeSave() {
	if (Ext.getCmp('northPanel').getForm().isValid()) {
		if (storeMulti.getCount() < 1) {
			Ext.Msg.alert('信息', '请添加复用段或干线！');
			return;
		}
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
				if (result.returnResult == 0) {
					Ext.Msg.alert("提示", result.returnMessage);
				} else {
					saveTask();
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}

function saveTask() {
	var taskName = Ext.getCmp('reportTaskName').getValue();
	var needExport = Ext.getCmp('needExport').getValue() ? 1 : 0;
	var privilege = Ext.getCmp('privilege').getValue();
	var period = Ext.getCmp('periodCb').getValue();// 0:日报；1：月报
	var hour = Ext.getCmp('hourCb').getValue();// 采集结束后几小时
	var delay = period == 0 ? Ext.getCmp('delay4DailyCb').getValue() : Ext
			.getCmp('delay4MonthlyCb').getValue();// 延迟几天
	var list = new Array();
	storeMulti.each(function(record) {
		var nodes;
		if (storeMulti.getAt(0).get('MSId'))
			nodes = {
				'targetId' : record.get('MSId'),
				'targetType' : targetMultiSec
			};
		if (storeMulti.getAt(0).get('TLId'))
			nodes = {
				'targetId' : record.get('TLId'),
				'targetType' : targetTrunkLine
			};
		list.push(Ext.encode(nodes));
	});
	var params = {
		'modifyList' : list,
		'searchCond.taskName' : taskName,
		'searchCond.needExport' : needExport,
		'searchCond.privilege' : privilege,
		'searchCond.period' : period,
		'searchCond.hour' : hour,
		'searchCond.delay' : delay,
		'searchCond.taskId' : taskId
	};
	top.Ext.getBody().mask('正在保存，请稍候...');
	Ext.Ajax.request({
		url : 'pm-report!updateMSReportTask.action',
		method : 'POST',
		params : params,
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage, function() {
				var f =   top.window.frames["f_自动作业计划管理"];
				var pageTool = f.pageTool;
				if (f) {
					if(f.loadTaskName)
						f.loadTaskName();
					else
						f.contentWindow.loadTaskName();
					pageTool.doLoad(pageTool.cursor);
				}
				if (result.returnResult == 1)
					parent.closeTab('修改报表');
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
		url : 'pm-report!initMSReportTaskInfo.action',
		method : 'POST',
		params : param,
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult != 0) {
				var taskInfo = result.taskInfo[0];
				var taskNodesInfo = result.taskNodesInfo;
				if (taskInfo) {
					Ext.getCmp('reportTaskName').setValue(taskInfo.taskName);
					Ext.getCmp('needExport').setValue(
							taskInfo.needExport == 1 ? true : false);
					Ext.getCmp('privilege').setValue(taskInfo.privilege);
					Ext.getCmp('periodCb').setValue(taskInfo.period);
					Ext.getCmp('hourCb').setValue(taskInfo.hour);
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
				if (taskNodesInfo) {
					// 页面column隐藏显示
					if (taskNodesInfo.rows[0].TLId) {
						grid.getColumnModel().setHidden(3, false);
						grid.getColumnModel().setHidden(4, true);
						grid.getColumnModel().setHidden(5, true);
					} else {
						grid.getColumnModel().setHidden(3, true);
						grid.getColumnModel().setHidden(4, false);
						grid.getColumnModel().setHidden(5, false);
					}
					storeMulti.loadData(taskNodesInfo);
					triggerForButton();
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