var autoBackupPanel;
(function() {
	// 数据库选择
	var autoDataChoose = new Ext.form.RadioGroup({
		fieldLabel : '数据库 ',
		anchor : '95%',
		id : 'autoDataChoose',
		name : 'autoDataChoose',
		items : [ {
			inputValue : '0',
			id:'autoDataChooseName1',
			name : 'autoDataChooseName',
			boxLabel : '主数据库',
			checked : true
		}, {
			inputValue : '1',
			id:'autoDataChooseName2',
			name : 'autoDataChooseName',
			boxLabel : '告警及日志数据库'
		} ],
		listeners : {
			change : function() {
				if (Ext.getCmp('autoDataChoose').getValue().inputValue == 0) {
					Ext.getCmp('autoBPHTML').setText(backupPath.autoMysqlPath);
					setDatabaseSettings(autoValuePri);
				} else {
					Ext.getCmp('autoBPHTML').setText(backupPath.autoMongodbPath);
					setDatabaseSettings(autoValueSec);
				}
			}
		}
	});
	// 是否自动备份
	var isAutoBackup = new Ext.form.RadioGroup({
		fieldLabel : '自动备份 ',
		anchor : '95%',
		id : 'isAutoBackup',
		name : 'isAutoBackup',
		items : [ {
			inputValue : '0',
			id:'isAutoBackupName1',
			name : 'isAutoBackupName',
			boxLabel : '是',
			checked : true
		}, {
			inputValue : '1',
			id:'isAutoBackupName2',
			name : 'isAutoBackupName',
			boxLabel : '否'
		} ]
	});
	// 备份路径
	var autoBPHTML = new Ext.form.Label({
		id : 'autoBPHTML',
		fieldLabel : '备份路径'
	});
	// 备份路径选择
	var isCopyBackup;
	(function() {
		var data = [ [ 0, '不备份文件' ], [ 1, '备份文件' ] ];
		var store = new Ext.data.ArrayStore({
			fields : [ {
				name : 'id'
			}, {
				name : 'isBackUp'
			} ]
		});
		store.loadData(data);
		isCopyBackup = new Ext.form.ComboBox({
			id : 'isCopyBackup',
			triggerAction : 'all',
			mode : 'local',
			fieldLabel : '是否备份文件',
			anchor : '95%',
			editable : false,
			store : store,
			value : 0,
			valueField : 'id',
			displayField : 'isBackUp',
			listeners : {
				'select' : function(combo, record, index) {
					if (combo.getValue() == 1) {
						Ext.getCmp('backupPathCF2').show();
					}
					if (combo.getValue() == 0) {
						Ext.getCmp('backupPathCF2').hide();
					}
				}
			}
		});
	})();
	var backupPathCF = new Ext.form.CompositeField({
		id : 'backupPathCF2',
		fieldLabel : '备份后文件拷贝到',
		hidden : true,
		items : [ {
			xtype : 'textfield',
			id : 'autoCopyPath',
			name : 'autoCopyPath',
			emptyText : '请输入正确格式,例如  c:\\databackup\\autocopy',
			width : 240,
			allowBlank : true,
		}, {
			xtype : 'button',
			text : "路径检查",
			width : 60,
			listeners : {
				"click" : function() {
					var bPath = Ext.getCmp('autoCopyPath').getValue();
					if (bPath == undefined || bPath == null || bPath == '') {
						Ext.Msg.alert("提示", "请输入备份的拷贝路径!");
						return;
					}
					checkPathAndCreatePathAuto();
				}
			}
		} ]
	});
	// 保留份数
	var retainParts = {
		xtype : 'combo',
		id : 'retainAmount',
		name : 'retainAmount',
		mode : "local",
		fieldLabel : '保留最近',
		editable : false,
		value : '3',
		store : new Ext.data.ArrayStore({
			fields : [ 'value', 'displayName' ],
			data : [ [ '1', '1份' ], [ '2', '2份' ], [ '3', '3份' ], [ '5', '5份' ], [ '10', '10份' ] ]
		}),
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		anchor : '95%',
	};

	// ====================时间设置====================

	var timeFinenessCombo = {
		xtype : 'combo',
		id : 'timeFinenessCombo',
		name : 'timeFinenessCombo',
		editable : false,
		mode : "local",
		fieldLabel : '周期设定',
		anchor : '95%',
		value : 'day',
		store : new Ext.data.ArrayStore({
			fields : [ 'value', 'displayName' ],
			data : [ [ 'day', '每日' ], [ 'week', '每周' ], [ 'month', '每月' ] ]
		}),
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		listeners : {
			'select' : function(combo, record, index) {
				Ext.getCmp('dateChoose').setValue('');
				Ext.getCmp('beginDate').setValue('');
				Ext.getCmp('summary').setValue('');
				if (record.data.value == 'day') {
					Ext.getCmp('queryWeekCombo').setVisible(false);
					Ext.getCmp('queryWeekCombo').setValue(1);
					Ext.getCmp('queryMonthCombo').setVisible(false);
					Ext.getCmp('queryMonthCombo').setValue(1);
					Ext.getCmp('waringLabel').setVisible(false);
				} else if (record.data.value == 'week') {
					Ext.getCmp('queryMonthCombo').setVisible(false);
					Ext.getCmp('queryMonthCombo').setValue(1);
					Ext.getCmp('waringLabel').setVisible(false);
					Ext.getCmp('queryWeekCombo').setVisible(true);
					Ext.getCmp('queryWeekCombo').setValue(1);
				} else if (record.data.value == 'month') {
					Ext.getCmp('queryWeekCombo').setVisible(false);
					Ext.getCmp('queryWeekCombo').setValue(1);
					Ext.getCmp('queryMonthCombo').setVisible(true);
					Ext.getCmp('waringLabel').setVisible(true);
				}
			}
		}
	}

	// 时间
	var dateChoose = {
		xtype : 'textfield',
		id : 'dateChoose',
		name : 'dateChoose',
		fieldLabel : '时间',
		allowBlank : false,
		anchor : '95%',
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "dateChoose",
					isShowClear : false,
					readOnly : true,
					dateFmt : 'HH:mm',
					autoPickDate : true,
					// maxDate : '%H-%m',
					onpicking : function(dp) {
						var dateStr = dp.cal.getNewDateStr();
						setBeginDate(dp.cal.getNewDateStr());
					}
				});
				this.blur();
			}
		}
	};
	// 加载周数据
	var queryWeekCombo = {
		xtype : 'combo',
		id : 'queryWeekCombo',
		name : 'queryWeekCombo',
		fieldLabel : '星期',
		editable : false,
		mode : "local",
		value : '1',
		hidden : true,
		store : new Ext.data.ArrayStore({
			fields : [ {
				name : "value",
				mapping : "value"
			}, {
				name : "displayName",
				mapping : "displayName"
			} ]
		}),
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		anchor : '95%',
		listeners : {
			beforequery : function(queryEvent) {
				var arr = [];
				for ( var i = 1; i <= 7; i++) {
					var json = {};
					json.value = i;
					json.displayName = i;
					arr.push(json);
				}
				Ext.getCmp('queryWeekCombo').getStore().loadData(arr);
			},
			select : function(combo, record, index) {
				setBeginDate();
			}
		}
	};
	// 加载月数据
	var queryMonthCombo = {
		xtype : 'combo',
		id : 'queryMonthCombo',
		name : 'queryMonthCombo',
		editable : false,
		fieldLabel : '日期',
		mode : "local",
		value : '1',
		hidden : true,
		store : new Ext.data.ArrayStore({
			fields : [ {
				name : "value",
				mapping : "value"
			}, {
				name : "displayName",
				mapping : "displayName"
			} ]
		}),
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		anchor : '95%',
		listeners : {
			beforequery : function(queryEvent) {
				var arr = [];
				for ( var i = 1; i <= 31; i++) {
					var json = {};
					json.value = i;
					json.displayName = i;
					arr.push(json);
				}
				Ext.getCmp('queryMonthCombo').getStore().loadData(arr);
			},
			select : function(combo, record, index) {
				setBeginDate();
			}
		}

	};
	var waringLabel = new Ext.form.DisplayField({
		id : 'waringLabel',
		hidden : true,
		fieldLabel : '',
		value : '<span style="color:red">注：如选择29~31号，则在部分月份无法执行</span>'
	});
	// 开始时间
	var beginDate = {
		xtype : 'textfield',
		id : 'beginDate',
		name : 'beginDate',
		fieldLabel : '开始时间',
		disabled : true,
		allowBlank : true,
		anchor : '95%'
	};
	// 摘要
	var summary = {
		xtype : 'textfield',
		id : 'summary',
		name : 'summary',
		fieldLabel : '摘要',
		disabled : true,
		allowBlank : true,
		anchor : '95%'
	};

	// for layout
	var columns = {
		layout : 'column',
		border : false,
		labelWidth : 150,
		items : [
				{
					columnWidth : .5,
					border : false,
					layout : 'form',
					items : [ autoDataChoose, isAutoBackup, autoBPHTML, isCopyBackup, backupPathCF,
							retainParts, timeFinenessCombo ]
				},
				{
					columnWidth : .5,
					border : false,
					layout : 'form',
					items : [ {
						xtype : 'fieldset',
						labelWidth : 150,
						bodyStyle : 'padding:10px 20px 0 20px',
						anchor : '95%',
						title : '周期设定',
						height : 200,
						items : [ queryMonthCombo, waringLabel, queryWeekCombo, dateChoose,
								beginDate, summary ]
					} ]
				} ]
	};

	autoBackupPanel = new Ext.FormPanel({
		id : 'autoBackupPanel',
		title : '自动备份',
		height : 300,
		labelWidth : 150,
		bodyStyle : 'padding:20px 20px 0 20px',
		items : [ {
			layout : 'form',
			border : false,
			items : [ columns ]
		} ],
		tbar : [ {
			text : '保存设置',
			icon : '../../../resource/images/btnImages/disk.png',
			handler : beginAutoBackup
		} ]
	});
})();


// ====================时间设置函数们====================
// 获取给定时间的指定日期
function getDayByDays(day) {
	var today = new Date();
	var targetday_milliseconds = today.getTime() + 1000 * 60 * 60 * 24 * day;
	today.setTime(targetday_milliseconds); // 注意，这行是关键代码
	var tYear = today.getFullYear();
	var tMonth = today.getMonth() + 1;
	var tDate = today.getDate();
	return tYear + "/" + tMonth + "/" + tDate;
}
// 根据星期获取下一个时间
function getNextDate(weekDay) {
	// 0是星期日,1是星期一,...
	var nowDate = new Date();
	weekDay %= 7;
	var day = nowDate.getDay();
	var time = nowDate.getTime();
	var sub = 0;
	if (weekDay > day) {
		sub = weekDay - day;
	} else {
		sub = 7 - day + weekDay;
	}
	time += sub * 24 * 3600000;
	nowDate.setTime(time);
	return nowDate.getFullYear() + '/' + (nowDate.getMonth() + 1) + '/' + nowDate.getDate();
}
// 获取给定日期的下月时间
function getNextMonth(t) {
	var tarr = t.split('-');
	var year = tarr[0]; // 获取当前日期的年
	var month = tarr[1]; // 获取当前日期的月
	var day = tarr[2]; // 获取当前日期的日
	var days = new Date(year, month, 0);
	days = days.getDate();// 获取当前日期中的月的天数
	var year2 = year;
	var month2 = parseInt(month) + 1;
	if (month2 == 13) {
		year2 = parseInt(year2) + 1;
		month2 = 1;
	}
	var day2 = day;
	var days2 = new Date(year2, month2, 0);
	days2 = days2.getDate();
	if (day2 > days2) {
		day2 = days2;
	}
	var t2 = year2 + '/' + month2 + '/' + day2;
	return t2;
}
function getWeekFormat(week) {
	if (week == 1) {
		return "一";
	} else if (week == 2) {
		return "二";
	} else if (week == 3) {
		return "三";
	} else if (week == 4) {
		return "四";
	} else if (week == 5) {
		return "五";
	} else if (week == 6) {
		return "六";
	} else if (week == 7) {
		return "日";
	}
}
// 根据选择设置开始时间及摘要
function setBeginDate(dateStr) {
	if (!dateStr) {
		dateStr = Ext.getCmp('dateChoose').getValue();
		if (!dateStr) {
			return;
		}
	}
	var qhour = dateStr.split(':')[0];
	var qmin = dateStr.split(':')[1];
	var fin = Ext.getCmp('timeFinenessCombo').getValue();
	if (fin == 'day') {
		if (new Date().getHours() < qhour
				|| (new Date().getHours() == qhour && new Date().getMinutes() < qmin)) {
			var nowDate = new Date();
			date = nowDate.getFullYear() + '/' + (nowDate.getMonth() + 1) + '/'
					+ nowDate.getDate();
		} else {
			date = getDayByDays(1);
		}
		Ext.getCmp('beginDate').setValue(date + " " + dateStr);
		Ext.getCmp('summary').setValue("每日" + " " + dateStr);

	} else if (fin == 'week') {
		var week = Ext.getCmp('queryWeekCombo').getValue();
		var date = "";
		if (week == new Date().getDay() && new Date().getHours() < qhour) {
			var nowDate = new Date();
			date = nowDate.getFullYear() + '/' + (nowDate.getMonth() + 1) + '/'
					+ nowDate.getDate();
		} else if (week == new Date().getDay() && new Date().getHours() == qhour
				&& new Date().getMinutes() < qmin) {
			var nowDate = new Date();
			date = nowDate.getFullYear() + '/' + (nowDate.getMonth() + 1) + '/'
					+ nowDate.getDate();
		} else {
			date = getNextDate(week);
		}
		Ext.getCmp('beginDate').setValue(date + " " + dateStr);
		Ext.getCmp('summary').setValue("每周" + getWeekFormat(week) + " " + dateStr);
	} else if (fin == 'month') {
		var day = Ext.getCmp('queryMonthCombo').getValue();
		if (new Date().getDate() < day) {
			var nowDate = new Date();
			date = nowDate.getFullYear() + '/' + (nowDate.getMonth() + 1) + '/' + day;
		} else if (new Date().getDate() == day && new Date().getHours() < qhour) {
			var nowDate = new Date();
			date = nowDate.getFullYear() + '/' + (nowDate.getMonth() + 1) + '/'
					+ nowDate.getDate();
		} else if (new Date().getDate() == day && new Date().getHours() == qhour
				&& new Date().getMinutes() < qmin) {
			var nowDate = new Date();
			date = nowDate.getFullYear() + '/' + (nowDate.getMonth() + 1) + '/'
					+ nowDate.getDate();
		} else {
			var nowDate = new Date();
			date = getNextMonth(nowDate.getFullYear() + "-" + (nowDate.getMonth() + 1) + "-"
					+ day);
		}
		Ext.getCmp('beginDate').setValue(date + " " + dateStr);
		Ext.getCmp('summary').setValue("每月" + day + " 号" + dateStr);
	}
}
// ====================时间设置函数们结束====================

function checkPathAndCreatePathAuto() {
	if(!judgePathIsLegal(Ext.getCmp('autoCopyPath').getValue())){
		Ext.Msg.alert("错误","请输入合法的文件路径");
		return;
	}
	
	
	Ext.Ajax.request({
		timeout : 9000000,
		url : 'data-backup!checkPathIsExists.action',
		method : 'POST',
		async : false,
		params : {
			'copyPath' : Ext.getCmp('autoCopyPath').getValue()
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnMessage == 'true') {
				Ext.Msg.alert("提示", '路径已存在');
				return;
			} else {
				Ext.MessageBox.confirm("选择框", "拷贝路径不存在,是否创建路径", function(add) {
					if (add == 'yes') {
						beginCreatePath(Ext.getCmp('autoCopyPath').getValue());
					} else {
						return;
					}
				});
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

// 检查copy路径是否存在
function checkAutoCopyPathIsExists() {
	if(Ext.getCmp('isAutoBackup').getValue().inputValue=='0'){
		if(!judgePathIsLegal(Ext.getCmp('autoCopyPath').getValue())){
			Ext.Msg.alert("错误","请输入合法的文件路径");
			return;
		}
	}else{
		autoBackup();
		return;
	}
	
	Ext.Ajax.request({
		timeout : 9000000,
		url : 'data-backup!checkPathIsExists.action',
		method : 'POST',
		async : false,
		params : {
			'copyPath' : Ext.getCmp('autoCopyPath').getValue()
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnMessage == 'true') {
				autoBackup();
				return;
			} else {
				Ext.MessageBox.confirm("选择框", "拷贝路径不存在,是否创建路径", function(add) {
					if (add == 'yes') {
						beginCreatePath(Ext.getCmp('autoCopyPath').getValue());
						autoBackup();
					} else {
						return;
					}
				});
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		}
	});

}

function beginAutoBackup() {
	var isCopy = 1;
	if (Ext.getCmp('isCopyBackup').getValue() == 1) {
		var bPath = Ext.getCmp('autoCopyPath').getValue();
		if (bPath == undefined || bPath == null || bPath == '') {
			Ext.Msg.alert("提示", "请输入自动备份的拷贝路径!");
			return;
		}
		isCopy = 0;
	} else {
		isCopy = 1;
	}
	if (isCopy == 0) {
		checkAutoCopyPathIsExists();
	} else {
		autoBackup();
		return;
	}
}

function autoBackup() {
	var beginDate = Ext.getCmp('beginDate').getValue();
	if (!beginDate) {
		Ext.Msg.alert("提示", "请选择自动备份的周期时间!");
		return;
	}
	var timeType = Ext.getCmp('timeFinenessCombo').getValue();
	var day = Ext.getCmp('queryMonthCombo').getValue();
	var week = Ext.getCmp('queryWeekCombo').getValue();
	var dateStr = Ext.getCmp('dateChoose').getValue();
	var qhour = dateStr.split(':')[0];
	var qmin = dateStr.split(':')[1];
	parent.hour = qhour;
	parent.min = qmin;
	var nextExecuteDate = Ext.getCmp('beginDate').getValue();

	var isCopy = 1;
	if (Ext.getCmp('isCopyBackup').getValue() == 1) {
		var bPath = Ext.getCmp('autoCopyPath').getValue();
		if (bPath == undefined || bPath == null || bPath == '') {
			Ext.Msg.alert("提示", "请输入自动备份的拷贝路径!");
			return;
		}
		isCopy = 0;
	} else {
		isCopy = 1;
	}
	var period = Ext.getCmp('summary').getValue();
	if (Ext.getCmp('isAutoBackup').getValue().inputValue == 0) {
		if (period == undefined || period == null || period == '') {
			Ext.Msg.alert("提示", "请选择自动备份的周期!");
			return;
		}
		if(Ext.getCmp('autoDataChoose').getValue().inputValue==0){
			autoValuePri='0'+","+Ext.getCmp('isAutoBackup').getValue().inputValue+","+isCopy+","+Ext.getCmp('autoCopyPath').getValue()+","+Ext.getCmp('retainAmount').getValue()+","+timeType+","+nextExecuteDate;
		}else{
			autoValueSec='1'+","+Ext.getCmp('isAutoBackup').getValue().inputValue+","+isCopy+","+Ext.getCmp('autoCopyPath').getValue()+","+Ext.getCmp('retainAmount').getValue()+","+timeType+","+nextExecuteDate;
		}
		Ext.Ajax.request({
			url : 'data-backup!autoBackup.action',
			method : 'POST',
			params : {
				'database' : Ext.getCmp('autoDataChoose').getValue().inputValue,
				'autoBackup' : Ext.getCmp('isAutoBackup').getValue().inputValue,
				'isCopy' : isCopy,
				'copyPath' : Ext.getCmp('autoCopyPath').getValue(),
				'timeType' : timeType,
				'day' : day,
				'week' : week,
				'hour' : qhour,
				'min' : qmin,
				'retainNum' : Ext.getCmp('retainAmount').getValue(),
				'nextExecuteDate' : nextExecuteDate
			},
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("提示", obj.returnMessage);
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		});
	} else {
		Ext.MessageBox.confirm('提示', '确认取消自动备份吗?', function(r) {
			if (r == 'no') {
				return;
			} else {
				Ext.Ajax.request({
					url : 'data-backup!autoBackup.action',
					method : 'POST',
					params : {
						'database' : Ext.getCmp('autoDataChoose').getValue().inputValue,
						'autoBackup' : Ext.getCmp('isAutoBackup').getValue().inputValue,
						'isCopy' : isCopy,
						'copyPath' : Ext.getCmp('autoCopyPath').getValue(),
						'timeType' : timeType,
						'day' : day,
						'week' : week,
						'hour' : qhour,
						'min' : qmin,
						'retainNum' : Ext.getCmp('retainAmount').getValue(),
						'nextExecuteDate' : nextExecuteDate
					},
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("提示", obj.returnMessage);
					},
					error : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});

			}
		});
	}
}
