/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// ============create the Data Store==========
// --------------已分配用户权限列表----------------
var factoryStore=new Ext.data.ArrayStore({
	fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
	data:[{key:0,value:"默认"}]
});
factoryStore.loadData(FACTORY,true);
var factoryCombo = {
	xtype : 'combo',
	id : 'factoryCombo',
	name : 'factoryCombo',
	fieldLabel : '厂家',
	store : factoryStore,
	displayField : "displayName",
	valueField : 'value',
	mode : 'local',
	triggerAction : 'all',
	editable : false,
	width : 200,
	sideText : '<font color=red>*</font>',
	value : "0"

}

// 网管combox
var typeCombo = {
	xtype : 'combo',
	id : 'typeCombo',
	name : 'typeCombo',
	fieldLabel : '光放类型',
	store : new Ext.data.ArrayStore({
				fields : ['value', 'displayName'],
				data : [['0', '默认'], ['1', '后置放大器'], ['2', '前置放大器'],
						['3', '线路放大器']]
			}),
	displayField : "displayName",
	valueField : 'value',
	mode : 'local',
	triggerAction : 'all',
	editable : false,
	width : 200,
	value : "0"
}
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
									layout : 'form',
									border : false,
									labelSeparator : "：",
									items : [factoryCombo, typeCombo, {
												xtype : 'textfield',
												id : 'MODEL',
												name : 'MODEL',
												fieldLabel : '光放型号',
												sideText : '<font color=red>*</font>',
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'MAX_OUT',
												name : 'MAX_OUT',
												fieldLabel : '最大输出功率(dBm)',
												emptyText : "浮点数，精度到小数点后2位",
												sideText : '<font color=red>*</font>',
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'MIN_GAIN',
												name : 'MIN_GAIN',
												emptyText : "浮点数，精度到小数点后2位",
												fieldLabel : '增益最小值(dB)',
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'MAX_GAIN',
												name : 'MAX_GAIN',
												emptyText : "浮点数，精度到小数点后2位且大于0",
												fieldLabel : '增益最大值(dB)',
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'TYPICAL_GAIN',
												name : 'TYPICAL_GAIN',
												emptyText : "浮点数，精度到小数点后2位且大于0",
												fieldLabel : '增益典型值(dB)',
												sideText : '<font color=red>*</font>',
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'MIN_IN',
												name : 'MIN_IN',
												fieldLabel : '输入最小值(dB)',
												emptyText : "浮点数，精度到小数点后2位且大于0",
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'MAX_IN',
												name : 'MAX_IN',
												fieldLabel : '输入最大值(dB)',
												emptyText : "浮点数，精度到小数点后2位",
												width : 200
											}, {
												xtype : 'numberfield',
												id : 'TYPICAL_IN',
												name : 'TYPICAL_IN',
												fieldLabel : '输入典型值(dB)',
												emptyText : "浮点数，精度到小数点后2位",
												width : 200
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
	var win = parent.Ext.getCmp('addStandOptValWindow');
	if (win) {
		win.close();
	}
}

function save() {

	if (formPanel.getForm().isValid()) {
		// 判断参数是否为空
		var factory = Ext.getCmp("factoryCombo").getValue();
		if (factory=='0') {
			Ext.Msg.alert('提示', '厂家不能为空！');
			return;
		}
		var model = Ext.getCmp("MODEL").getValue();
		if (model.length < 1) {
			Ext.Msg.alert("提示", "光放型号不能为空！");
			return;
		}
		var maxOut = Ext.getCmp("MAX_OUT").getValue();
		if (maxOut.length < 1) {
			Ext.Msg.alert("提示", "输出最大功率不能为空！");
			return;
		}
		// “增益最小值”、“增益最大值”、“增益典型值”的数据格式为正整数
		var min_gain = Ext.getCmp("MIN_GAIN").getValue();
		if (min_gain < 0) {
			Ext.Msg.alert("提示", "增益最小值必须大于0！");
			return;
		}
		var max_gain = Ext.getCmp("MAX_GAIN").getValue();
		if (max_gain < 0) {
			Ext.Msg.alert("提示", "增益最大值必须大于0！");
			return;
		}
		var typicalGain = Ext.getCmp("TYPICAL_GAIN").getValue();
		if (typicalGain.length < 1) {
			Ext.Msg.alert("提示", "增益典型值不能为空！");
			return;
		}
		if (typicalGain < 0) {
			Ext.Msg.alert("提示", "增益典型值必须大于0！");
			return;
		}
		// “增益最小值”<“增益典型值”<“增益最大值”
		if (min_gain != "" && min_gain > typicalGain) {
			Ext.Msg.alert("提示", "增益最小值要小于增益典型值！");
			return;
		}
		if (max_gain != "" && typicalGain > max_gain) {
			Ext.Msg.alert("提示", "增益典型值要小于增益最大值！");
			return;
		}
		if (min_gain != "" && max_gain != "" && min_gain > max_gain) {
			Ext.Msg.alert("提示", "增益最小值要小于增益最大值！");
			return;
		}
		// “输入最小值”<“输入典型值”<“输入最大值”。
		var min_in = Ext.getCmp("MIN_IN").getValue();
		var max_in = Ext.getCmp("MAX_IN").getValue();
		var typical_in = Ext.getCmp("TYPICAL_IN").getValue();
		if(min_in!=""&&typical_in!=""&&min_in>typical_in){
			Ext.Msg.alert("提示", "输入最小值要小于输入典型值！");
			return;
		}
		if(typical_in!=""&&max_in!=""&&typical_in>max_in){
			Ext.Msg.alert("提示", "输入典型值要小于输入最大值！");
			return;
		}
		if(min_in!=""&&max_in!=""&&min_in>max_in){
			Ext.Msg.alert("提示", "输入最小值要小于输入最大值！");
			return;
		}
		var jsonString = new Array();
		var map = {
			"FACTORY" : factory,
			"TYPE" : Ext.getCmp("typeCombo").getValue(),
			"MODEL" : Ext.getCmp("MODEL").getValue(),
			"MAX_OUT" : Ext.getCmp("MAX_OUT").getValue(),
			"MIN_GAIN" : Ext.getCmp("MIN_GAIN").getValue(),
			"MAX_GAIN" : Ext.getCmp("MAX_GAIN").getValue(),
			"TYPICAL_GAIN" : Ext.getCmp("TYPICAL_GAIN").getValue(),
			"MIN_IN" : Ext.getCmp("MIN_IN").getValue(),
			"MAX_IN" : Ext.getCmp("MAX_IN").getValue(),
			"TYPICAL_IN" : Ext.getCmp("TYPICAL_IN").getValue()
		};
		jsonString.push(map);

		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		Ext.Ajax.request({
			url : 'multiple-section!addStandOptVal.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {// 回调函数
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					Ext.Msg.alert("提示", obj.returnMessage, function(r) {

						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						Ext.Msg.confirm('信息', '继续添加？', function(btn) {
									if (btn == 'yes') {

									} else {
										// 关闭修改任务信息窗口
										var win = parent.Ext
												.getCmp('addStandOptValWindow');
										if (win) {
											win.close();
										}
									}
								});

					});
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("提示", obj.returnMessage);
				}

			},
			error : function(response) {
				Ext.Msg.alert('错误', '保存失败！');
			},
			failure : function(response) {
				Ext.Msg.alert('错误', '保存失败！');
			}

		});

	}

}

Ext.onReady(function() {
	Ext.Msg = parent.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}
	Ext.QuickTips.init(); // 开启悬停提示
	Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
	var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
});