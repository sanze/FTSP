/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var formPanel = new Ext.FormPanel({
	id : 'formPanel',
	name : 'formPanel',
	region : "center",
	border : false,
	frame : false,
	autoScroll : true,
	bodyStyle : 'padding:20px 20px 0;',
	items : [ {
		xtype : 'textfield',
		id : 'PM_STD_INDEX',
		name : 'PM_STD_INDEX',
		fieldLabel : '性能事件',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'TYPE',
		name : 'TYPE',
		fieldLabel : '性能类型',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'PM_DESCRIPTION',
		name : 'PM_DESCRIPTION',
		fieldLabel : '描述',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'UNIT',
		name : 'UNIT',
		fieldLabel : '单位',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'OFFSET',
		name : 'OFFSET',
		fieldLabel : '基准值偏差',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'UPPER_OFFSET',
		name : 'UPPER_OFFSET',
		fieldLabel : '上限值偏差',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'LOWER_OFFSET',
		name : 'LOWER_OFFSET',
		fieldLabel : '下限值偏差',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'THRESHOLD_1',
		name : 'THRESHOLD_1',
		fieldLabel : '阈值1',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'THRESHOLD_2',
		name : 'THRESHOLD_2',
		fieldLabel : '阈值2',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'THRESHOLD_3',
		name : 'THRESHOLD_3',
		fieldLabel : '阈值3',
		readOnly : true,
		width : 200
	}, {
		xtype : 'textfield',
		id : 'FILTER_VALUE',
		name : 'FILTER_VALUE',
		fieldLabel : '过滤值',
		readOnly : true,
		width : 200
	} ],
	buttons : [ {
		text : '确定',
		handler : function() {
			// 关闭修改任务信息窗口
			var win = parent.Ext.getCmp('templateInfoWin');
			if (win) {
				win.close();
			}
		}
	} ]
});

function initData() {
	if (TYPE == 2) {
		// 计数值
		Ext.getCmp('TYPE').setValue("计数值");
		Ext.getCmp('OFFSET').setVisible(false);
		Ext.getCmp('UPPER_OFFSET').setVisible(false);
		Ext.getCmp('LOWER_OFFSET').setVisible(false);
		if (isCurrent == 0) {
			// 历史性能
			var info = Ext.decode(decodeURI(infos));
			Ext.getCmp('PM_STD_INDEX').setValue(info.PM_STD_INDEX);
			Ext.getCmp('PM_DESCRIPTION').setValue(info.PM_DESCRIPTION);
			Ext.getCmp('UNIT').setValue(info.UNIT);
			Ext.getCmp('THRESHOLD_1').setValue(info.THRESHOLD_1);
			Ext.getCmp('THRESHOLD_2').setValue(info.THRESHOLD_2);
			Ext.getCmp('THRESHOLD_3').setValue(info.THRESHOLD_3);
			Ext.getCmp('FILTER_VALUE').setValue(info.FILTER_VALUE);
		}
	} else if (TYPE == 1) {
		// 物理量
		Ext.getCmp('TYPE').setValue("物理量");
		Ext.getCmp('THRESHOLD_1').setVisible(false);
		Ext.getCmp('THRESHOLD_2').setVisible(false);
		Ext.getCmp('THRESHOLD_3').setVisible(false);
		Ext.getCmp('FILTER_VALUE').setVisible(false);
		if (isCurrent == 0) {
			// 历史性能
			var info = Ext.decode(decodeURI(infos));
			Ext.getCmp('PM_STD_INDEX').setValue(info.PM_STD_INDEX);
			Ext.getCmp('PM_DESCRIPTION').setValue(info.PM_DESCRIPTION);
			Ext.getCmp('UNIT').setValue(info.UNIT);
			Ext.getCmp('OFFSET').setValue(info.OFFSET);
			Ext.getCmp('UPPER_OFFSET').setValue(info.UPPER_OFFSET);
			Ext.getCmp('LOWER_OFFSET').setValue(info.LOWER_OFFSET);
		}
	}
	if (isCurrent == 1) {
		// 当前性能,需要查询数据库
		Ext.Ajax.request({
			url : 'pm-search!getCurrentPmTempleteInfo.action',
			params : {
				"searchCond.templateId" : TEMPLATE_ID,
				"searchCond.pmStdIndex" : PM_STD_INDEX,
				"searchCond.domain" : domain
			},
			method : 'POST',
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					if (1 == result.returnResult) {
						Ext.getCmp('PM_STD_INDEX').setValue(result.PM_STD_INDEX);
						Ext.getCmp('PM_DESCRIPTION').setValue(result.PM_DESCRIPTION);
						Ext.getCmp('UNIT').setValue(result.UNIT);
						if (TYPE == 2) {
							// 计数值
							Ext.getCmp('THRESHOLD_1').setValue(result.THRESHOLD_1);
							Ext.getCmp('THRESHOLD_2').setValue(result.THRESHOLD_2);
							Ext.getCmp('THRESHOLD_3').setValue(result.THRESHOLD_3);
							Ext.getCmp('FILTER_VALUE').setValue(result.FILTER_VALUE);
						} else if (TYPE == 1) {
							// 物理量
							Ext.getCmp('OFFSET').setValue(result.OFFSET);
							Ext.getCmp('UPPER_OFFSET').setValue(result.UPPER_OFFSET);
							Ext.getCmp('LOWER_OFFSET').setValue(result.LOWER_OFFSET);
						}
					} else {
						Ext.Msg.alert("提示", result.returnMessage);
					}
				}
			},
			failure : function(response) {
				Ext.Msg.alert("提示", "查询执行状态出错");
			},
			error : function(response) {
				Ext.Msg.alert("提示", "查询执行状态出错");
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
	initData();
});