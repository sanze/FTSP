/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

Ext.override(Ext.form.NumberField, {
setValue : function(v){
v = typeof v == 'number' ? v : parseFloat(String(v).replace(this.decimalSeparator, "."));
v = isNaN(v) ? '' : v.toFixed(this.decimalPrecision).replace(".", this.decimalSeparator);
return Ext.form.NumberField.superclass.setValue.call(this, v);
}
});

// --------------------domainCombo---------------------
var domainData = [ [ '1', 'SDH' ], [ '2', 'WDM' ], [ '3', 'ETH' ]];
var domainStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
domainStore.loadData(domainData);
var domainCombo = new Ext.form.ComboBox({
	id : 'domainCombo',
	name : 'domainCombo',
	fieldLabel : '业务类型',
	allowBlank:false,
	store : domainStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	anchor : '95%',
	sideText : '<font color=red>*</font>',
	listeners : {
		select : function(combo, record, index) {
			Ext.getCmp('portCombo').reset();
			// 设置prot类型数据源
			if (record.get('value') == '1') {
				portStore.loadData(sdhData);
			} else if (record.get('value') == '2') {
				portStore.loadData(wdmData);
			} else if (record.get('value') == '3') {
				portStore.loadData(ethData);
			}
		}
	}
});
// --------------------portCombo---------------------
var ethData = [ [ 'FE', 'FE' ], [ 'GE', 'GE' ],
        		[ '10GE', '10GE' ] ];
var sdhData = [ [ 'STM-1', 'STM-1' ], [ 'STM-4', 'STM-4' ],
		[ 'STM-16', 'STM-16' ], [ 'STM-64', 'STM-64' ],
		[ 'STM-256', 'STM-256' ] ];
var wdmData = [  [ 'OTU1', 'OTU1' ], [ 'OTU2', 'OTU2' ],
		[ 'OTU3', 'OTU3' ] ];
var allData = [ [ 'MAC', 'MAC' ], [ 'STM-1', 'STM-1' ],
		[ 'STM-4', 'STM-4' ], [ 'STM-16', 'STM-16' ], [ 'STM-64', 'STM-64' ],
		[ 'STM-256', 'STM-256' ] , [ 'OTU1', 'OTU1' ], [ 'OTU2', 'OTU2' ],
		[ 'OTU3', 'OTU3' ]];
var portStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
//portStore.loadData(emptyData);
var portCombo = new Ext.form.ComboBox({
	id : 'portCombo',
	name : 'portCombo',
	fieldLabel : '端口类型',
	allowBlank:false,
	store : portStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	anchor : '95%',
	sideText : '<font color=red>*</font>',
	listeners : {
		select : function(combo, record, index) {
		}
	}
});

// ==================页面====================
var formPanel = new Ext.FormPanel({
	region : "center",
	frame : false,
	bodyStyle : 'padding:20px 10px 0',
	labelWidth : 140,
	labelAlign : 'left',
	autoScroll : true,
	items : [ {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .9,
			layout : 'form',
			border : false,
			labelSeparator : "：",
			items : [ domainCombo, portCombo, {
				xtype : 'textfield',
				id : 'model',
				name : 'model',
				sideText : '<font color=red>*</font>',
				fieldLabel : '光口标准',
				allowBlank : false,
				anchor : '95%',
				listeners:{
					blur: function(t){
						var param = {'searchCond.stdName':t.getValue()};
						Ext.Ajax.request({
							url:'optical-unit-config!checkOptStdName.action',
							method : 'POST',
							params:param,
							success : function(response) {
								var result = Ext.util.JSON.decode(response.responseText);
								if (result) {
									if (0 == result.returnResult) {
								        Ext.MessageBox.show({
								            title: '信息',
								            msg: '\"光口标准\"名称重复，请修改！',
								            buttons: Ext.MessageBox.OK,
								            icon: Ext.MessageBox.ERROR
								        });
									}
								}
							}
						});
					}
				}
			}, {
				xtype : 'numberfield',
				id : 'maxOut',
				name : 'maxOut',
				fieldLabel : '最大输出功率(dBm)',
				allowBlank : true,
				anchor : '95%',
				listeners:{
					blur:function(){
						if(Ext.getCmp('minOut').getValue()!=''&&Ext.getCmp('maxOut').getValue()!=''){
							if(Ext.getCmp('minOut').getValue()>=Ext.getCmp('maxOut').getValue())
								Ext.MessageBox.show({
									title: '错误',
									msg: '\"最大输出光功率\"必须大于\"最小输出光功率\"！',
									buttons: Ext.MessageBox.OK,
									icon: Ext.MessageBox.ERROR
								});
						}
					}
				}
			}, {
				xtype : 'numberfield',
				id : 'minOut',
				name : 'minOut',
				fieldLabel : '最小输出功率(dBm)',
				allowBlank : true,
				anchor : '95%',
				listeners:{
					blur:function(){
						if(Ext.getCmp('minOut').getValue()!=''&&Ext.getCmp('maxOut').getValue()!=''){
							if(Ext.getCmp('minOut').getValue()>=Ext.getCmp('maxOut').getValue())
								Ext.MessageBox.show({
									title: '错误',
									msg: '\"最大输出光功率\"必须大于\"最小输出光功率\"！',
									buttons: Ext.MessageBox.OK,
									icon: Ext.MessageBox.ERROR
								});
						}
					}
				}
			}, {
				xtype : 'numberfield',
				id : 'maxIn',
				name : 'maxIn',
				fieldLabel : '过载点(dBm)',
				allowBlank : true,
				anchor : '95%',
				listeners:{
				blur:function(){
						if(Ext.getCmp('maxIn').getValue()!=''&&Ext.getCmp('minIn').getValue()!=''){
							if(Ext.getCmp('minIn').getValue()>=Ext.getCmp('maxIn').getValue())
								   Ext.MessageBox.show({
							            title: '错误',
							            msg: '\"过载点\"必须大于\"灵敏度\"！',
							            buttons: Ext.MessageBox.OK,
							            icon: Ext.MessageBox.ERROR
							        });
						}
					}
				}
			}, {
				xtype : 'numberfield',
				id : 'minIn',
				name : 'minIn',
				fieldLabel : '灵敏度(dBm)',
				allowBlank : true,
				anchor : '95%',
				listeners:{
				blur:function(){
						if(Ext.getCmp('maxIn').getValue()!=''&&Ext.getCmp('minIn').getValue()!=''){
							if(Ext.getCmp('minIn').getValue()>=Ext.getCmp('maxIn').getValue())
								   Ext.MessageBox.show({
							            title: '错误',
							            msg: '\"过载点\"必须大于\"灵敏度\"！',
							            buttons: Ext.MessageBox.OK,
							            icon: Ext.MessageBox.ERROR
							        });
						}
					}
				}
			}, {
				xtype : 'numberfield',
				id : 'distance',
				name : 'distance',
				fieldLabel : '传送距离',
				allowNegative : false,
				decimalPrecision:0,
				allowBlank : true,
				anchor : '95%'
			}, {
				xtype : 'numberfield',
				id : 'centerWaveLength',
				name : 'centerWaveLength',
				fieldLabel : '中心波长',
				allowBlank : true,
				anchor : '95%'
			} ]
		} ]
	} ],
	buttons : [ {
		text : '确定',
//		privilege:addAuth, 
		handler : saveNewOptStd
	}, {
		text : '取消', 
		handler : function() {
			var win = parent.Ext.getCmp('addStandardOpticalValueWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});

// =================函数===================
function checkOptStdName(name){
	var param = {'searchCond.stdName':name};
	Ext.Ajax.request({
		url:'optical-unit-config!checkOptStdName.action',
		method : 'POST',
		params:param,
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				if (0 == result.returnResult) {
			        return 0;
				}else{
					return 1;
					}
					
			}
		}
	});
}

function saveNewOptStd() {
	if (formPanel.getForm().isValid()) {
		var maxOut = Ext.getCmp('maxOut').getValue();
		var minOut = Ext.getCmp('minOut').getValue();
		var maxIn = Ext.getCmp('maxIn').getValue();
		var minIn = Ext.getCmp('minIn').getValue();
		if(maxOut<=minOut){
			Ext.MessageBox.show({
				title: '错误',
				msg: '\"最大输出光功率\"必须大于\"最小输出光功率\"！',
				buttons: Ext.MessageBox.OK,
				icon: Ext.MessageBox.ERROR
			});
			return;
		}
		if(maxIn<=minIn){
			   Ext.MessageBox.show({
		            title: '错误',
		            msg: '\"过载点\"必须大于\"灵敏度\"！',
		            buttons: Ext.MessageBox.OK,
		            icon: Ext.MessageBox.ERROR
		        });
			   return;
		}
		var param = {'searchCond.stdName':Ext.getCmp('model').getValue()};
		Ext.Ajax.request({
			url:'optical-unit-config!checkOptStdName.action',
			method : 'POST',
			params:param,
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					if (0 == result.returnResult) {
				        Ext.MessageBox.show({
				            title: '信息',
				            msg: '\"光口标准\"名称重复，请修改！',
				            buttons: Ext.MessageBox.OK,
				            icon: Ext.MessageBox.ERROR
				        });
					}else{
						 var standardOpticalDetail = {
						 "searchCond.domain" : Ext.getCmp('domainCombo').getValue(),
						 "searchCond.ptpType":Ext.getCmp('portCombo').getValue(),
						 "searchCond.model" : Ext.getCmp('model').getValue(),
						 "searchCond.maxOut" : Ext.util.Format.number(maxOut, '0.00'),
						 "searchCond.minOut" : Ext.util.Format.number(minOut, '0.00'),
						 "searchCond.maxIn" : Ext.util.Format.number(maxIn, '0.00'),
						 "searchCond.minIn" : Ext.util.Format.number(minIn, '0.00'),
						 "searchCond.distance" : Ext.getCmp('distance').getValue(),
						 "searchCond.centerWaveLength" : Ext.util.Format.number(Ext.getCmp('centerWaveLength').getValue(), '0.00')
						 };
						
						 top.Ext.getBody().mask('正在执行，请稍候...');
						
						 // 提交
						 Ext.Ajax.request({
							 url : 'optical-unit-config!saveNewOptStd.action',
							 method : 'POST',
							 params : standardOpticalDetail,
							 success : function(response) {
								 top.Ext.getBody().unmask();
									var result = Ext.util.JSON.decode(response.responseText);
									if (result) {
										Ext.Msg.confirm("提示", result.returnMessage,function(btn){
											if(btn!="yes"){
												var win = parent.Ext.getCmp('addStandardOpticalValueWindow');
												if (win) {
													win.close();
												}
											}
										});
									}
									var pageTool = parent.Ext.getCmp('pageTool');
									if (pageTool) {
										pageTool.doLoad(pageTool.cursor);
									}
									
								}
							});
					}
				}
			}
		});
	}
}



Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	var win = new Ext.Viewport({
		id : 'win',
		loadMask : true,
		layout : 'border',
		items : [ formPanel ],
		renderTo : Ext.getBody()
	});
});