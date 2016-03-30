//====开始日期====@
var startTime = {
	xtype : 'textfield',
	id : 'startTime',
	name : 'startTime',
	fieldLabel : '起始日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	// width : 160,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
//				minDate:  '#F{$dp.$D(\'endTime\',{d:-40})}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startTime = Ext.getCmp('startTime').getValue();
//			var endTime = Ext.getCmp('endTime').getValue();
//			if(startTime&&endTime)
//				dayLimitCheck(startTime,endTime);
		}
	}
};

//====结束日期====@
var endTime = {
	xtype : 'textfield',
	id : 'endTime',
	name : 'endTime',
	fieldLabel : '结束日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	anchor : '95%',
	// width : 160,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "endTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
				minDate:'#F{$dp.$D(\'startTime\',{d:0})}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startTime = Ext.getCmp('startTime').getValue();
//			var endTime = Ext.getCmp('endTime').getValue();
//			if(startTime&&endTime)
//				dayLimitCheck(startTime,endTime);
		}
	}
};

//====开始月====@
var startMonth = {
	xtype : 'textfield',
	id : 'startMonth',
	name : 'startMonth',
	fieldLabel : '起始月份',
	sideText : '<font color=red>*</font>',
	hidden : true,
	allowBlank : false,
	readOnly : true,
	// width : 160,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startMonth",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M',
				dateFmt : 'yyyy-MM',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startMonth = Ext.getCmp('startMonth').getValue();
//			var endMonth = Ext.getCmp('endMonth').getValue();
//			if(startMonth&&endMonth)
//				monthLimitCheck(startMonth,endMonth);
		}
	}
};

//====结束月====@
var endMonth = {
	xtype : 'textfield',
	id : 'endMonth',
	name : 'endMonth',
	fieldLabel : '结束月份',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	hidden : true,
	readOnly : true,
	anchor : '95%',
	// width : 160,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "endMonth",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M',
				minDate:'#F{$dp.$D(\'startMonth\',{d:0})}',
				dateFmt : 'yyyy-MM',
				isShowToday : false,
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startMonth = Ext.getCmp('startMonth').getValue();
//			var endMonth = Ext.getCmp('endMonth').getValue();
//			if(startMonth&&endMonth)
//				monthLimitCheck(startMonth,endMonth);
		}
	}
};

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


//---------------------------------------------------
var periodCb;
(function() {
	var data = [ [ 0, '每日' ], [ 1, '每月' ] ];
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
		Ext.getCmp('startTime').show();
		Ext.getCmp('endTime').show();
		Ext.getCmp('startMonth').hide();
		Ext.getCmp('endMonth').hide();
		Ext.getCmp('pmDate').hide();
	}
	if (combo.getValue() == 1) {
		Ext.getCmp('startTime').hide();
		Ext.getCmp('endTime').hide();
		Ext.getCmp('startMonth').show();
		Ext.getCmp('endMonth').show();
		Ext.getCmp('pmDate').show();
	}
});
// -----------------------------------------------------------------
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
//--------------------------------------------------------
//每月性能日期
var pmDate;
(function() {
	var data = [ [ 1, '1日' ], [ 2, '2日' ], [ 3, '3日' ], [ 4, '4日' ],
			[ 5, '5日' ], [ 6, '6日' ], [ 7, '7日' ], [ 8, '8日' ], [ 9, '9日' ],
			[ 10, '10日' ], [ 11, '11日' ], [ 12, '12日' ], [ 13, '13日' ],
			[ 14, '14日' ], [ 15, '15日' ], [ 16, '16日' ], [ 17, '17日' ],
			[ 18, '18日' ], [ 19, '19日' ], [ 20, '20日' ], [ 21, '21日' ],
			[ 22, '22日' ], [ 23, '23日' ], [ 24, '24日' ], [ 25, '25日' ],
			[ 26, '26日' ], [ 27, '27日' ], [ 28, '28日' ], [ 29, '29日' ],
			[ 30, '30日' ], [ 31, '31日' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'pmDate'
		} ]
	});
	store.loadData(data);
	pmDate = new Ext.form.ComboBox({
		id : 'pmDate',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '性能日期',
		anchor : '95%',
		store : store,
		hidden : true,
		value : 1,
		valueField : 'id',
		displayField : 'pmDate'
	});
})();
//-------------------------------------------------------------------

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
							sideText : '<font color=red>*</font>',
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
							value:1,
							allowDecimals : false,
							allowNegative : false,
							sideText : '次',
							allowBlank : true,
							fieldLabel : '连续异常≥'
						} ]
					}, {
						columnWidth : 0.24,
						layout : 'form',
						labelWidth : 80,
						border : false,
						items : [ privilegeCombo ]

					}, {
						columnWidth : 0.16,
						layout : 'form',
						labelWidth : 40,
						border : false,
						items : [ periodCb ]

					} ]
		} ]
	} ]

};




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
			title : '报表时间 ',
			layout : 'column',
			items : [ {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [  startTime, startMonth  ]
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [endTime, endMonth]
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ pmDate ]
			} ]
		} ]
	} ]

};


//-------------------------------------------------------
//检查日期跨度
function dayLimitCheck(start,end){
	var startTime = new Date(start.replace(/-/g, "/"));
	var endTime = new Date(end.replace(/-/g, "/"));
	if(endTime-startTime <0)
	{
	    Ext.Msg.alert("提示","结束时间不能早于开始时间！");
	    return false;
	}
	if((endTime-startTime)/1000/60/60/24 >40)
	{
	    Ext.Msg.alert("提示","间隔时间不超过40天！");
	    return false;
	}
	return true;
}
//检查月份跨度
function monthLimitCheck(start,end){
	var startTime = new Date(start.replace(/-/g, "/"));
	var endTime = new Date(end.replace(/-/g, "/"));
	if(endTime.getFullYear()==startTime.getFullYear()){
		if(endTime.getMonth()-startTime.getMonth()>12){
			 Ext.Msg.alert("提示","不能超过12个月！");
			 return false;
		}
	}else{
		if(endTime.getFullYear()-startTime.getFullYear()>1){
			 Ext.Msg.alert("提示","不能超过12个月！");
			 return false;
		}else if(startTime.getMonth()-endTime.getMonth()<0){
			 Ext.Msg.alert("提示","不能超过12个月！");
			 return false;
		}
	}
	return true;
}
//--------------------------------------------