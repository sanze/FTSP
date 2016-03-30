/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var trunkLineStore = new Ext.data.Store({
	// url: 'getTrunkLineListByConnId.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "trunkLineId", "displayName" ])
});
/*
 * trunkLineStore.load({ callback: function(r, options, success){ if(success){
 * 
 * }else{ Ext.Msg.alert('错误','加载失败！'); } } });
 */

function getTrunkLineData(emsConnectionId) {
	// 重置下拉框
	Ext.getCmp("groupName").reset();
	var path = 'getTrunkLineListByConnId.action?emsConnectionId='
			+ encodeURI(encodeURI(emsConnectionId));
	trunkLineStore.proxy = new Ext.data.HttpProxy({
		url : path
	});
	trunkLineStore.load({
		callback : function(r, options, success) {
			if (success) {
			} else {
				Ext.Msg.alert('错误', '干线信息加载失败！请重新查询');
			}
		}// callback function end
	});
};

var emsStore = new Ext.data.Store({
	url : 'getConnectionList.action',
	baseParams : {
		"connectInfoModel.connectionType" : "0"
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsConnectionId", "displayName", "emsConnectionName", "type", "emsConTypeName", "ip",
			"port", "intervalTime", "timeOut" ])
});
emsStore.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '加载失败！');
		}
	}
});

var emsConnectionName = new Ext.form.ComboBox({
	id : 'emsConnectionName',
	name : 'emsConnectionName',
	fieldLabel : '网络',
	store : emsStore,
	displayField : "displayName",
	valueField : 'emsConnectionId',
	triggerAction : 'all',
	editable : false,
	// emptyText:'干线名称........',
	allowBlank : false,
	anchor : '95%',
	resizable: true,
	listeners : {
		select : function(combo, record, index) {
			getTrunkLineData(Ext.getCmp("emsConnectionName").getValue());
		}
	}
});

var groupName = new Ext.form.ComboBox({
	id : 'groupName',
	name : 'groupName',
	fieldLabel : '干线名称',
	store : trunkLineStore,
	displayField : "displayName",
	valueField : 'trunkLineId',
	triggerAction : 'all',
	editable : false,
	// emptyText:'干线名称........',
	allowBlank : false,	
	resizable: true,
	anchor : '95%'
});

var sectionName = new Ext.form.TextField({
	id : 'sectionName',
	name : 'sectionName',
	fieldLabel : '光复用段名称',
	allowBlank : false,
	anchor : '95%'
});

var standardWave = new Ext.form.NumberField({
	id : 'standardWave',
	name : 'standardWave',
	fieldLabel : '标称波道数',
	// emptyText:'标称波道数',
	allowBlank : false,
	allowDecimals : false,
	allowNegative : false,
	minValue : 1,
	anchor : '95%'
});

var actullyWave = new Ext.form.NumberField({
	id : 'actullyWave',
	name : 'actullyWave',
	fieldLabel : '实际波道数',
	// emptyText:'实际波道数',
	allowBlank : false,
	allowDecimals : false,
	allowNegative : false,
	minValue : 1,
	anchor : '95%'
});

var direction = new Ext.form.TextField({
	id : 'direction',
	name : 'direction',
	fieldLabel : '方向',
	// emptyText:'干线名称........',
	allowBlank : false,
	anchor : '95%'
});
/*
 * var keptLong = new Ext.form.NumberField({ id:'keptLong', name: 'keptLong',
 * fieldLabel: '保存时长', emptyText:'输入1-3的整数', sideText:'<font color=red>年</font>',
 * allowBlank: false, allowDecimals:false, allowNegative : false, minValue : 1,
 * maxValue : 3, anchor: '95%' });
 * 
 * var keptDate = new Ext.form.NumberField({ id:'keptDate', name: 'keptDate',
 * fieldLabel: '保存时长', emptyText:'输入1-31的整数', sideText:'<font color=red>号</font>',
 * allowBlank: false, allowDecimals:false, allowNegative : false, minValue : 1,
 * maxValue : 31, anchor: '95%' });
 */

var sectionWaveInfo = {
	xtype : 'fieldset',
	title : '复用段信息',
	autoHeight : true,
	items : [ emsConnectionName, groupName, sectionName, standardWave, actullyWave, direction /*
																								 * ,
																								 * keptLong,
																								 * keptDate
																								 */
	]
};
var formPanel = new Ext.FormPanel({
	id : 'formPanel',
	region : "center",
	border : false,
	frame : false,
	autoScroll : true,
	labelWidth : 120,
	width : 200,
	bodyStyle : 'padding:10px 12px 0;',
	items : [ sectionWaveInfo ],
	buttons : [ {
		text : '保存',
		handler : function() {
			if (formPanel.getForm().isValid()) {
				var trunkLineId = Ext.getCmp("groupName").getValue();
				var emsConnectionId = Ext.getCmp("emsConnectionName").getValue();
				var emsConnectionName = Ext.getCmp("emsConnectionName").getRawValue();
				var groupName = Ext.getCmp("groupName").getRawValue();
				var sectionName = Ext.getCmp("sectionName").getValue();
				var standardWave = Ext.getCmp("standardWave").getValue();
				var actullyWave = Ext.getCmp("actullyWave").getValue();
				var direction = Ext.getCmp("direction").getValue();
				// var keptLong = Ext.getCmp("keptLong").getValue();
				// var keptDate = Ext.getCmp("keptDate").getValue();

				var jsonData = {
					"sectionWaveModel.trunkLineId" : trunkLineId,
					"sectionWaveModel.emsConnectionId" : emsConnectionId,
					"sectionWaveModel.emsConnectionName" : emsConnectionName,
					"sectionWaveModel.groupName" : groupName,
					"sectionWaveModel.sectionName" : sectionName,
					"sectionWaveModel.standardWave" : standardWave,
					"sectionWaveModel.actullyWave" : actullyWave,
					"sectionWaveModel.direction" : direction
				/*
				 * , "sectionWaveModel.keptLong":keptLong,
				 * "sectionWaveModel.keptDate":keptDate
				 */
				};

				top.Ext.getBody().mask('正在执行，请稍候...');

				Ext.Ajax.request({
					url : 'addMultiSection.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						top.Ext.getBody().unmask();
						Ext.Msg.alert("信息", obj.returnMessage, function(r) {
							// 刷新列表
							var pageTool = parent.Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
							Ext.Msg.confirm('信息', '继续添加？', function(btn) {
								if (btn == 'yes') {

								} else {
									// 关闭修改任务信息窗口
									var win = parent.Ext.getCmp('addMulitySectionWindow');
									if (win) {
										win.close();
									}
								}
							});
						});
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
		}
	}, {
		text : '重置',
		handler : function() {
			formPanel.getForm().reset();
		}
	} ]
});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	}
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ formPanel ],
		renderTo : Ext.getBody()
	});
});

