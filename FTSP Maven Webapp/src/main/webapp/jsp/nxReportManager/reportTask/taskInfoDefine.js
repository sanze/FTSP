
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
//-----------------------------------------------------------------------
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
			var initValue = privilegeStore.reader.jsonData.initValue;
			privilegeCombo.setValue(initValue);
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
	id : 'rowI',
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
							sideText : '<font color=red>*</font>',
							fieldLabel : '任务名称'
						} ]
					},
					{
						columnWidth : 0.22,
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
							hidden:true,
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
	var data = [ [ 3, '三天' ], [ 4, '四天' ],
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
		value : 3,
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

//====开始月====@
var hourCb = {
	xtype : 'textfield',
	id : 'hourCb',
	name : 'hourCb',
	fieldLabel : '生成时间',
//	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	anchor : '95%',
	cls : 'Wdate',
	value:'12:00',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "hourCb",
				isShowClear : false,
				readOnly : true,
//				maxDate : '%y-%M',
				dateFmt : 'HH:mm',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
		}
	}
};
var rowII = {
	layout : 'column',
	id : 'rowII',
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
				columnWidth : 0.20,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ periodCb ]
			}, {
				columnWidth : 0.22,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ delay4DailyCb, delay4MonthlyCb ]
			}, {
				columnWidth :  0.24,
				layout : 'form',
				labelWidth : 80,
				border : false,
				items : [ hourCb ]
			} ]
		} ]
	} ]

};
