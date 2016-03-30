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

var Data_connectRate = [['2', '每周'], ['3', '每月']];
var emsStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'value'
					}, {
						name : 'displayName'
					}]
		});
emsStore.loadData(Data_connectRate);

var Data_time_week = [['2', '周一'], ['3', '周二'], ['4', '周三'], ['5', '周四'],
		['6', '周五'], ['7', '周六'], ['1', '周日']];

var Data_time_month = new Array();
for (var i = 1; i <= 31; i++) {
	Data_time_month[i - 1] = [i, i + '号'];
}

var timeStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'time'
					}, {
						name : 'displayName'
					}]
		});

// ==================页面====================
var formPanel = new Ext.FormPanel({
	region : "center",
	// labelAlign: 'top',
	frame : false,
	// title: '新增用户',
	bodyStyle : 'padding:20px 10px 0',
	// labelWidth: 100,
	labelAlign : 'left',
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
				id : 'ems',
				name : 'ems',
				fieldLabel : '重复',
				store : emsStore,
				displayField : "displayName",
				valueField : 'value',
				triggerAction : 'all',
				allowBlank : true,
				width : 200,
				mode : "local",
				listeners : {
					'select' : function() {
						var cyc = Ext.getCmp('ems').getValue();
						if (cyc == 2) {
							timeStore.loadData(Data_time_week);
							Ext.getCmp('explain').setValue("");

						} else {
							timeStore.loadData(Data_time_month);
							Ext
									.getCmp('explain')
									.setValue("<font color=red>注：如选择29-31号，则部分月份无法执行</font>");
						}
						Ext.getCmp('time').setValue(1);
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
				width : 200,
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
				width : 200,
				height : 25
			},
			 {

				xtype : 'textfield',
				id : 'beginTime',
				name : 'beginTime',
				fieldLabel : '开始时间',
				// anchor : '95%',
				cls : 'Wdate',
				// value:this.nowTime,
				width : 200,
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

			},{
				xtype : 'textfield',
				id : 'startTime',
				name : 'startTime',
				fieldLabel : '开始日期',
				readOnly : true,
				// anchor : '95%',
				width : 200

			}, {
				xtype : 'textfield',
				id : 'note',
				name : 'note',
				fieldLabel : '摘要',
				width : 200
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
	var win = parent.Ext.getCmp('addCycleWindow');
	if (win) {
		win.close();
	}
}

function save() {

	var period;
	var period_type;
	if (Ext.getCmp('ems').getValue() == 2) {
		period_type = 2;
		period = "0,0,0," + Ext.getCmp('time').getValue() + ",0,"
				+ Ext.getCmp('beginTime').getValue()+":00";
	} else if (Ext.getCmp('ems').getValue() == 3) {
		period_type = 3;
		period = "0,0,0,0," + Ext.getCmp('time').getValue() + ","
				+ Ext.getCmp('beginTime').getValue()+":00";
	}

	var jsonString = new Array();
	var map = {
		"PERIOD_TYPE" : period_type,
		"PERIOD" : period,
		"SYS_TASK_ID" : task_id,
		"NEXT_TIME" : Ext.getCmp('startTime').getValue(),
		"TASK_DESCRIPTION" : Ext.getCmp('note').getValue()

	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
				url : 'circuit!setCycle.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 1) {

						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						// 关闭修改任务信息窗口
						var win = parent.Ext.getCmp('addCycleWindow');
						if (win) {
							win.close();

						}

					}
					if (obj.returnResult == 0) {
						Ext.Msg.alert("提示", obj.returnMessage);
					}
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

function updateState() {

	var period;
	var period_type;
	if (Ext.getCmp('ems').getValue() == 2) {
		period_type = 2;
		period = "0,0,0," + Ext.getCmp('time').getValue() + ",0,"
				+ Ext.getCmp('beginTime').getValue()+":00";
	} else if (Ext.getCmp('ems').getValue() == 3) {
		period_type = 3;
		period = "0,0,0,0," + Ext.getCmp('time').getValue() + ","
				+ Ext.getCmp('beginTime').getValue()+":00";
	}

	var jsonString = new Array();
	var map = {
		"PERIOD_TYPE" : period_type,
		"PERIOD" : period
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
				url : 'circuit!setBeginTime.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 1) {

						// 赋值
						Ext.getCmp('startTime').setValue(obj.NEXT_TIME);
						// 赋值摘要
						Ext.getCmp('note').setValue(Ext.getCmp('ems')
								.getRawValue()
								+ " "
								+ Ext.getCmp('time').getRawValue()
								+ " "
								+ Ext.getCmp('beginTime').getValue()+":00");

					}
					if (obj.returnResult == 0) {
						Ext.Msg.alert("提示", obj.returnMessage);
					}
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

function init() {

	var time = period.split(",");
	var zhai_1;
	var zhai_2;
	var zhai_3 = time[5];
	// 判断周期
	if (period_type == 2) {
		zhai_1 = "每周 ";
		Ext.getCmp('ems').setValue(2);
		// Ext.getCmp('ems').setRawValue("每周");
		timeStore.loadData(Data_time_week);
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
			Ext.getCmp('time').setValue(time[3]);
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
	} else if (period_type == 3) {
		zhai_1 = "每月 ";
		zhai_2 = time[4] + "号 ";
		Ext.getCmp('ems').setValue(3);
		// Ext.getCmp('ems').setRawValue("每月");
		timeStore.loadData(Data_time_month);
		Ext.getCmp('time').setValue(time[4]);
		Ext.getCmp('explain')
				.setValue("<font color=red>注：如选择29-31号，则部分月份无法执行</font>");

	}

	Ext.getCmp('beginTime').setValue(time[5].substring(0,time[5].length-3));
	Ext.getCmp('note').setValue(zhai_1 + zhai_2 + zhai_3);
	Ext.getCmp('startTime').setValue(nexttime.replace(",", " "));
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