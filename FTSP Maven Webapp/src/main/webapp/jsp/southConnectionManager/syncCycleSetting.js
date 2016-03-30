/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// ============create the Data Store==========
// --------------已分配用户权限列表----------------
var groupDistributeStoreData = [

];

var groupDistributeStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'displayName'
					}, {
						name : 'userGroupId'
					}]
		});
groupDistributeStore.loadData(groupDistributeStoreData);

var Data_connectRate = [['2', '每周'], ['3', '每月'], ['4', '每季度']];
var repeatStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'value'
					}, {
						name : 'displayName'
					}]
		});
repeatStore.loadData(Data_connectRate);

var Data_week = [['2', '周一'], ['3', '周二'], ['4', '周三'], ['5', '周四'],
		['6', '周五'], ['7', '周六'], ['1', '周日']];

var Data_month = new Array();
for (var i = 1; i <= 31; i++) {
	Data_month[i - 1] = [i, i + '号'];
}

var timeStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'time'
					}, {
						name : 'displayName'
					}]
		});

//var Data_quarter = [['1', '第1个月'], ['2', '第2个月'], ['3', '第3个月']];

var Data_quarter = new Array();
for (var i = 1; i <= 3; i++) {
	Data_quarter[i - 1] = [i,   '第 ' + i + ' 个月'];
}

var quarterStore = new Ext.data.ArrayStore({
	fields : [{
				name : 'value'
			}, {
				name : 'displayName'
			}]
});
quarterStore.loadData(Data_quarter);

// ==================页面====================
var formPanel = new Ext.FormPanel({
	region : "center",
	// labelAlign: 'top',
	frame : false,
	// title: '新增用户',
	bodyStyle : 'padding:20px 10px 0',
	// labelWidth: 100,
	labelAlign : 'right',
	autoScroll : true,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			columnWidth : .9,
			layout : 'form',
			border : false,
			labelSeparator : "：",
			items : [{
				xtype : 'combo',
				id : 'repeat',
				name : 'repeat',
				fieldLabel : '重复',
				store : repeatStore,
				displayField : "displayName",
				valueField : 'value',
				triggerAction : 'all',
				allowBlank : true,
				width : 230,
				mode : "local",
				listeners : {
					'select' : function() {
						var cyc = Ext.getCmp('repeat').getValue();
						if (cyc == 2) {
							Ext.getCmp('month').setVisible(false);
							timeStore.loadData(Data_week);
							Ext.getCmp('explain').setValue("");

						} else if (cyc == 3){
							Ext.getCmp('month').setVisible(false);
							timeStore.loadData(Data_month);
							Ext.getCmp('explain')
									.setValue("<font color=red>注：如选择29-31号，则部分月份无法执行</font>");
						}  else if (cyc == 4){
						
							Ext.getCmp('month').setVisible(true);
							quarterStore.loadData(Data_quarter);
							
							timeStore.loadData(Data_month);
							Ext.getCmp('explain')
									.setValue("<font color=red>注：如选择29-31号，则部分月份无法执行</font>");
						}
						Ext.getCmp('month').setValue(1);
						Ext.getCmp('time').setValue(1);
						updateState();
					}

				}
			}, {
				xtype : 'combo',
				id : 'month',
				name : 'month',
				fieldLabel : '月份',
				store : quarterStore,
				displayField : "displayName",
				valueField : 'value',
				triggerAction : 'all',
				width : 230,
				mode : "local",
				listeners : {
					'select' : function() {
						updateState();
					}

				}
			}, {
				xtype : 'combo',
				id : 'time',
				name : 'time',
				fieldLabel : '日期',
				store : timeStore,
				displayField : "displayName",
				valueField : 'time',
				triggerAction : 'all',
				width : 230,
				mode : "local",
				listeners : {
					'select' : function() {
						updateState();
					}

				}
			}, {
				xtype : 'displayfield',
				id : 'explain',
				name : 'explain',
				fieldLabel : '',
				width : 230,
				height : 30
			}, {

				xtype : 'textfield',
				id : 'beginTime',
				name : 'beginTime',
				fieldLabel : '开始时间',
				// anchor : '95%',
				cls : 'Wdate',
				// value:this.nowTime,
				width : 230,
				listeners : {
					'focus' : function() {
						WdatePicker({
									el : "beginTime",
									isShowClear : false,
									readOnly : true,
									dateFmt : 'HH:mm',
									autoPickDate : true,
									maxDate : '%y-%M-%d'
								});
						this.blur();
						updateState();
					}
				}

			}, {
				xtype : 'textfield',
				id : 'startTime',
				name : 'startTime',
				fieldLabel : '开始日期',
				readOnly : true,
				// anchor : '95%',
				width : 230

			}, {
				xtype : 'textfield',
				id : 'note',
				name : 'note',
				fieldLabel : '摘要',
				width : 230
					// anchor: '95%'
				}]
		}]
	}],
	buttons : [{
				text : '确定',
				handler : save
			}, {
				text : '取消',
				handler : close
			}]
});

// =================函数===================
function close() {
	var win = parent.Ext.getCmp('syncCycleWindow');
	if (win) {
		win.close();
	}
}

function save() {

	var period;
	var periodType;

	if (Ext.getCmp('repeat').getValue() == 2) {
		periodType = 2;
		period = "0,0,0," + Ext.getCmp('time').getValue() + ",0,"
				+ Ext.getCmp('beginTime').getValue();
	} else if (Ext.getCmp('repeat').getValue() == 3) {
		periodType = 3;
		period = "0,0,0,0," + Ext.getCmp('time').getValue() + ","
				+ Ext.getCmp('beginTime').getValue();
	} else if (Ext.getCmp('repeat').getValue() == 4) {
		periodType = 4;
		period = "0,1,"+ Ext.getCmp('month').getValue() + ",0," + Ext.getCmp('time').getValue() + ","
				+ Ext.getCmp('beginTime').getValue();
	}

	var jsonString = new Array();
	var map = {
		"PERIOD_TYPE" : periodType,
		"PERIOD" : period,
		"SYS_TASK_ID" : taskId,
		"TASK_STATUS" : taskStatus,
		"NEXT_TIME" : Ext.getCmp('startTime').getValue(),
		"TASK_DESCRIPTION" : Ext.getCmp('note').getValue()

	};
	jsonString.push(map);

	var jsonData = {
		"jString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
				url : 'connection!setCycle.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 1) {						
									// 刷新列表
									var pageTool = parent.Ext
											.getCmp('pageTool');
									if (pageTool) {
										pageTool.doLoad(pageTool.cursor);
									}
									// 关闭修改任务信息窗口
									var win = parent.Ext
											.getCmp('syncCycleWindow');
									if (win) {
										win.close();
									}
								
					}
					if (obj.returnResult == 0) {
						Ext.Msg.alert("信息", obj.returnMessage);
					}
				},
				error : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", response.responseText);
				}
			});

}

function updateState() {

	var period;
	var periodType;
	if (Ext.getCmp('repeat').getValue() == 2) {
		period ="";
		periodType = 2;
		period = "0,0,0," + Ext.getCmp('time').getValue() + ",0,"
				+ Ext.getCmp('beginTime').getValue();
	} else if (Ext.getCmp('repeat').getValue() == 3) {
		period ="";
		periodType = 3;
		period = "0,0,0,0," + Ext.getCmp('time').getValue() + ","
				+ Ext.getCmp('beginTime').getValue();
	} else if (Ext.getCmp('repeat').getValue() == 4) {
		period ="";
		periodType = 4;
		period = "0,1,"+ Ext.getCmp('month').getValue() + ",0," + Ext.getCmp('time').getValue() + ","
				+ Ext.getCmp('beginTime').getValue();
	}
	var jsonString = new Array();
	var map = {
		"PERIOD_TYPE" : periodType,
		"PERIOD" : period
	};
	jsonString.push(map);

	var jsonData = {
		"jString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
				url : 'connection!setBeginTime.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 1) {

						// 赋值
						Ext.getCmp('startTime').setValue(obj.NEXT_TIME);
						// 赋值摘要
						if(Ext.getCmp('repeat').getRawValue() == '每月'||Ext.getCmp('repeat').getRawValue() == '每周'){
							Ext.getCmp('note')
							.setValue(Ext.getCmp('repeat').getRawValue() + " "
									+ Ext.getCmp('time').getRawValue() + " "
									+ Ext.getCmp('beginTime').getValue());
						} else if (Ext.getCmp('repeat').getRawValue() == '每季度'){
							Ext.getCmp('note')
							.setValue(Ext.getCmp('repeat').getRawValue() + " "
									+ Ext.getCmp('month').getRawValue() + " "
									+ Ext.getCmp('time').getRawValue() + " "
									+ Ext.getCmp('beginTime').getValue());
						}
					}
					if (obj.returnResult == 0) {
						Ext.Msg.alert("信息", obj.returnMessage);
					}
				},
				error : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", response.responseText);
				}
			});
}

function init() {
	
	var time = period.split(",");
	var zhai_1;
	var zhai_2;
	var zhai_3;
	var zhai_4 = time[5];
	Ext.getCmp('month').setVisible(false);
	// 判断周期
	if (periodType == 2) {
		
		zhai_1 = "每周 ";
		Ext.getCmp('repeat').setValue(2);
		
		// Ext.getCmp('repeat').setRawValue("每周");
		timeStore.loadData(Data_week);
		Ext.getCmp('explain').setValue("");
		if (time[3] == 2) {			
			Ext.getCmp('time').setValue(time[3]);
			zhai_2 = "周一 ";
		} else if (time[3] == 3) {
			Ext.getCmp('time').setValue(time[3]);
			zhai_2 = "周二 ";
		} else if (time[3] == 4) {
			Ext.getCmp('time').setValue(time[3]);
			zhai_2 = "周三 ";
		} else if (time[3] == 5) {
			Ext.getCmp('time').setValue("周四");
			zhai_2 = "周四 ";
		} else if (time[3] == 6) {
			Ext.getCmp('time').setValue(time[3]);
			zhai_2 = "周五 ";
		} else if (time[3] == 7) {
			Ext.getCmp('time').setValue(time[3]);
			zhai_2 = "周六 ";
		} else if (time[3] == 1) {
			Ext.getCmp('time').setValue(time[3]);
			zhai_2 = "周日 ";
		}
		Ext.getCmp('note').setValue(zhai_1 + zhai_2  + zhai_4);
	} else if (periodType == 3) {
		
		zhai_1 = "每月 ";
		zhai_2 = time[4] + "号 ";
		
		Ext.getCmp('repeat').setValue(3);
		// Ext.getCmp('repeat').setRawValue("每月");
		timeStore.loadData(Data_month);
		Ext.getCmp('time').setValue(time[4]);
		Ext.getCmp('explain')
				.setValue("<font color=red>注：如选择29-31号，则部分月份无法执行</font>");
		Ext.getCmp('note').setValue(zhai_1 + zhai_2 + zhai_4);
	} else if (periodType == 4) {
		
		zhai_1 = "每季度 ";
		zhai_2 = "第 " + time[2] + " 个月 ";
		zhai_3 = time[4] + "号 ";
		Ext.getCmp('month').setVisible(true);
		Ext.getCmp('repeat').setValue(4);
		quarterStore.loadData(Data_quarter);
		Ext.getCmp('month').setValue(time[2]);
		timeStore.loadData(Data_month);
		Ext.getCmp('time').setValue(time[4]);
		Ext.getCmp('explain')
				.setValue("<font color=red>注：如选择29-31号，则部分月份无法执行</font>");
		Ext.getCmp('note').setValue(zhai_1 + zhai_2 + zhai_3 + zhai_4);
	}

	Ext.getCmp('beginTime').setValue(time[5]);
	
	Ext.getCmp('startTime').setValue(nextSyncTime.replace(",", " "));
}

Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
			var win = new Ext.Viewport({
						id : 'win',
						loadMask : true,
						layout : 'border',
						items : [formPanel],
						renderTo : Ext.getBody()
					});
			init();
		});