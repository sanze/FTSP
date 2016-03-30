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

var paramStore;
(function(){
	paramStore = new Ext.data.ArrayStore({
	idIndex : 0,
	fields : [ 'paramId', 'paramName']
});
	
var SDHData = [ [ 0, "链路衰耗" ], [ 1, "衰耗系数"]];
paramStore.loadData(SDHData);
})();


// **************************checkboxes-bottom***********************
// 链路衰耗
var lineLossBottom = {
	id : 'lineLossBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	border : false,
	// width : 280,
	// height : 150,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ZXHLLSH_Bottom',
		name : 'ZXHLLSH_Bottom',
		boxLabel : '链路衰耗：主信号',
		inputValue : 'ATT_VALUE'
	}, {
		xtype : 'checkbox',
		id : 'OSCSH_Bottom',
		name : 'OSCSH_Bottom',
		boxLabel : '链路衰耗：OSC信号',
		inputValue : 'ATT_VALUE_OSC'
	}, {
		xtype : 'checkbox',
		id : 'SHZJZ_Bottom',
		name : 'SHZJZ_Bottom',
		boxLabel : '衰耗基准值',
		inputValue : 'ATT_STD'
	} ]
};

// 衰耗系数
var lossCoefficientBottom = {
	id : 'lossCoefficientBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ZXHSHXS_Bottom',
		name : 'ZXHSHXS_Bottom',
		boxLabel : '衰耗系数α0：主信号',
		inputValue : 'ATT_COEFFICIENT'
	}, {
		xtype : 'checkbox',
		id : 'OSCSHXS_Bottom',
		name : 'OSCSHXS_Bottom',
		boxLabel : '衰耗系数α0：OSC信号',
		inputValue : 'ATT_COEFFICIENT_OSC'
	}, {
		xtype : 'checkbox',
		id : 'SHXSLLZ_Bottom',
		name : 'SHXSLLZ_Bottom',
		boxLabel : '衰耗系数理论值',
		inputValue : 'ATT_COEFFICIENT_THEORY'
	}, {
		xtype : 'checkbox',
		id : 'SHXSJGZ_Bottom',
		name : 'SHXSJGZ_Bottom',
		boxLabel : '衰耗系数竣工值',
		inputValue : 'ATT_COEFFICIENT_BUILD'
	}, {
		xtype : 'checkbox',
		id : 'SHXSJYZ_Bottom',
		name : 'SHXSJYZ_Bottom',
		boxLabel : '衰耗系数经验值',
		inputValue : 'ATT_COEFFICIENT_EXPERIENCE'
	} ]
};

// **************************checkboxes-top***********************
// 链路衰耗
var lineLoss = {
	id : 'lineLoss',
	xtype : 'checkboxgroup',
	columns : 1,
	border : false,
	// width : 280,
	// height : 150,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ZXHSH',
		name : 'ZXHSH',
		boxLabel : '链路衰耗：主信号',
		inputValue : 'ATT_VALUE',
		checked: true
	}, {
		xtype : 'checkbox',
		id : 'OSCSH',
		name : 'OSCSH',
		boxLabel : '链路衰耗：OSC信号',
		inputValue : 'ATT_VALUE_OSC',
		checked: true
	}, {
		xtype : 'checkbox',
		id : 'SHZJZ',
		name : 'SHZJZ',
		boxLabel : '衰耗基准值',
		inputValue : 'ATT_STD',
		checked: true
	} ]
};

// 衰耗系数
var lossCoefficient = {
	id : 'lossCoefficient',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ZXHSHXS',
		name : 'ZXHSHXS',
		boxLabel : '衰耗系数α0：主信号',
		inputValue : 'ATT_COEFFICIENT'
	}, {
		xtype : 'checkbox',
		id : 'OSCSHXS',
		name : 'OSCSHXS',
		boxLabel : '衰耗系数α0：OSC信号',
		inputValue : 'ATT_COEFFICIENT_OSC'
	}, {
		xtype : 'checkbox',
		id : 'SHXSLLZ',
		name : 'SHXSLLZ',
		boxLabel : '衰耗系数理论值',
		inputValue : 'ATT_COEFFICIENT_THEORY'
	}, {
		xtype : 'checkbox',
		id : 'SHXSJGZ',
		name : 'SHXSJGZ',
		boxLabel : '衰耗系数竣工值',
		inputValue : 'ATT_COEFFICIENT_BUILD'
	}, {
		xtype : 'checkbox',
		id : 'SHXSJYZ',
		name : 'SHXSJYZ',
		boxLabel : '衰耗系数经验值',
		inputValue : 'ATT_COEFFICIENT_EXPERIENCE'
	} ]
};


var panel1 = new Ext.form.FormPanel({
	id : 'panel1',
	autoScroll : true,
	border : false,
	height : 121,
	width : 250,
	// margins:{top:20, right:0, bottom:20, left:0},
//	style : " margin-top: 20px; margin-bottom: 15px;",
	labelWidth : 59,
	items : []
});
var panel2 = new Ext.form.FormPanel({
	id : 'panel2',
	autoScroll : true,
	border : false,
	height : 121,
	width : 250,
	// margins:{top:20, right:0, bottom:20, left:0},
//	style : " margin-top: 20px; margin-bottom: 15px;",
	labelWidth : 59,
	items : []
});
// **********************************combo********************
// TODO
var paramCombo = new Ext.form.ComboBox({
	id : 'paramCombo',
	fieldLabel : '参数类型',
	mode : 'local',
	labelSeparator : ':',
	triggerAction : 'all',
	editable:false,
	store : paramStore,
	valueField : 'paramId',
	displayField : 'paramName',
	listeners : {
		'select' : function(combo, record, index) {
			// 如果有checkbox组，先remove掉
			if (panel1.get(0)) {
				panel1.remove(panel1.get(0));
			}
			switch (record.get('paramId')) {
			case 0: // 链路衰耗
				panel1.add(lineLoss);
				panel1.doLayout();
				break;
			case 1: // 衰耗系数
				panel1.add(lossCoefficient);
				panel1.doLayout();
				break;
			}
		}
	}
});

var paramComboBottom = new Ext.form.ComboBox({
	id : 'paramComboBottom',
	fieldLabel : '参数类型',
	mode : 'local',
	labelSeparator : ':',
	triggerAction : 'all',
	editable:false,
	store : paramStore,
	valueField : 'paramId',
	displayField : 'paramName',
	listeners : {
		'select' : function(combo, record, index) {
			// 如果有checkbox组，先remove掉
			if (panel2.get(0)) {
				panel2.remove(panel2.get(0));
			}
			switch (record.get('paramId')) {
			case 0: // 链路衰耗
				panel2.add(lineLossBottom);
				panel2.doLayout();
				break;
			case 1: // 衰耗系数
				panel2.add(lossCoefficientBottom);
				panel2.doLayout();
				break;
			}
		}
	}
});
// ******************************date picker*****************************
var startTime = {
	xtype : 'compositefield',
	fieldLabel : '开始时间',
	defaults : {
		flex : 2
	},
	items : [
			{
				xtype : 'textfield',
				id : 'startTime',
				name : 'startTime',
				fieldLabel : '开始时间',
				allowBlank : false,
				readOnly : true,
				value : startDate,
//				width : 160,
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "startTime",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd',
							autoPickDate : true,
//							maxDate : '%y-%M-%d',
							onpicked : function() {
								Ext.getCmp('startTimeBottom').setValue(
										Ext.getCmp('startTime').getValue());
								var endTime=Ext.getCmp('endTime').getValue();
								var startTime=Ext.getCmp('startTime').getValue();
								var endTimeVal;
								var startTimeVal;
								var endTimeMax;
								if(endTime&&endTime.length>0){
									endTimeVal=Date.parseDate(endTime,"Y-m-d");
								}
								if(startTime&&startTime.length>0){
									startTimeVal=Date.parseDate(startTime,"Y-m-d");
									endTimeMax=startTimeVal.add(Date.MONTH,2);
								}
								
								if(endTimeVal&&
									((endTimeVal.getTime()<startTimeVal.getTime())||
									  endTimeVal.getTime()>endTimeMax.getTime()))//选择的起始时间晚于结束时间
									Ext.getCmp('endTime').setValue(startTimeVal.add(Date.MONTH,1).format("yyyy-MM-dd"));
									Ext.getCmp('endTimeBottom').setValue(
	        							 Ext.getCmp('endTime').getValue());
							}

						});
						this.blur();
					}
				}
			} ]
};
var endTime = {
		xtype : 'compositefield',
		fieldLabel : '结束时间',
		defaults : {
			flex : 2
		},
		items : [
		         {
		        	 xtype : 'textfield',
		        	 id : 'endTime',
		        	 name : 'endTime',
		        	 fieldLabel : '结束时间',
		        	 allowBlank : false,
		        	 readOnly : true,
		        	 value:endDate,
//		        	 width : 160,
		        	 cls : 'Wdate',
		        	 listeners : {
		        		 'focus' : function() {
		        			 WdatePicker({
		        				 el : "endTime",
		        				 isShowClear : false,
		        				 readOnly : true,
		        				 dateFmt : 'yyyy-MM-dd',
		        				 autoPickDate : true,
		        				 maxDate : Date.parseDate(Ext.getCmp('startTime').getValue(),"Y-m-d").add(Date.MONTH,2).format("yyyy-MM-dd"),
		        				 minDate:Ext.getCmp('startTime').getValue(),
		        				 onpicked : function() {
		        					 Ext.getCmp('endTimeBottom').setValue(
		        							 Ext.getCmp('endTime').getValue());
		        				 }
		        			 
		        			 });
		        			 this.blur();
		        		 }
		        	 }
		         } ]
};

var startTimeBottom = {
	xtype : 'compositefield',
	fieldLabel : '开始时间',
	defaults : {
		flex : 2
	},
	items : [ {
		xtype : 'textfield',
		id : 'startTimeBottom',
		name : 'startTimeBottom',
		disabled : true,
		fieldLabel : '开始时间',
//		width : 160,
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "startTimeBottom",
					isShowClear : false,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd',
					autoPickDate : true,
					maxDate : '%y-%M-%d'
				});
				this.blur();
			}
		}
	}
	// , {
	// xtype : 'button',
	// id : 'resetStartTimeBottom',
	// name : 'resetStartTimeBottom',
	// text : '清空',
	// width : 45,
	// handler : function() {
	// Ext.getCmp('startTimeBottom').setValue("");
	// }
	// }
	]
};

var endTimeBottom = {
		xtype : 'compositefield',
		fieldLabel : '结束时间',
		defaults : {
			flex : 2
		},
		items : [ {
			xtype : 'textfield',
			id : 'endTimeBottom',
			name : 'endTimeBottom',
			disabled : true,
			fieldLabel : '结束时间',
//			width : 160,
			cls : 'Wdate',
			listeners : {
				'focus' : function() {
					WdatePicker({
						el : "endTimeBottom",
						isShowClear : false,
						readOnly : true,
						dateFmt : 'yyyy-MM-dd',
						autoPickDate : true,
						maxDate : '%y-%M-%d'
					});
					this.blur();
				}
			}
		}
		]
};

// *************************field set ******************************
var otherParam1 = {
	id : 'otherParam1',
//	xtype : 'fieldset',
	layout : 'form',
	padding : '10px 0 0 0',
	// height : 100,
	labelWidth : 60,
	items : [ startTime, endTime ]
};
var otherParam2 = {
	id : 'otherParam2',
//	xtype : 'fieldset',
	layout : 'form',
	padding : '10px 0 0 0',
	// height : 100,
	labelWidth : 60,
	items : [ startTimeBottom, endTimeBottom ]
};

// TODO
var fieldSet1 = {
	id : 'fieldSet1',
//	xtype : 'fieldset',
//	padding : '10px 0 0 0',
	layout : 'form',
	border: false,
	// height : 250,
	labelWidth : 60,
	items : [ paramCombo, panel1, startTime, endTime]
};

var fieldSet2 = {
	id : 'fieldSet2',
//	xtype : 'fieldset',
//	padding : '10px 0 0 0',
	layout : 'form',
	border: false,
	// width : 320,
	// height : 250,
	labelWidth : 60,
	items : [ paramComboBottom, panel2, startTimeBottom, endTimeBottom]
};

var topField = {
	xtype : 'fieldset',
//	layout : 'form',
	id : 'topField',
	title : '光纤链路趋势图1',
//	layout : 'fit',
//	width : Ext.getBody().getWidth()-50,
	items : [ {
		xtype : 'compositefield',
		hideLabel: true,
		id:'tf',
		items : [ new Ext.Spacer({ // 占位
			id : 'chart1',
			height : (window.screen.availHeight-window.screenTop)*0.5-60,
			width : Ext.getBody().getWidth()-250
		}), {
			xtype : 'fieldset',
			id : 'rightPartTop',
			layout : 'form',
			border : false,
			width : 240,
			items : [ fieldSet1, /*otherParam1,*/ {
				layout : 'column',
//				height : 40,
				border : false,
				items : [ {
					columnWidth : .5,
					border : false,
					items : {
						xtype : 'button',
						id : 'generateDiagram1',
						text : '生成趋势图',
						ctCls : 'margin-for-button',
						width : 80,
						handler : generateDiagramNend
					}
				}, {
					columnWidth : .5,
					border : false,
					items : new Ext.Spacer({
						id : 'exportDiagram1',
						width : 120
					})
				} ]
			} ]
		} ]
	} ]

};

var nendOrFend = 'nend';

var switchButton = new Ext.Button({
	id : 'switchButton',
	text : '切换到远端(当前：本端)',
//	width : 80,
	handler : function() {
	},
	listeners : {
		'click' : function(t, e) {
			if (nendOrFend == 'nend') {
				nendOrFend = 'fend';
				switchButton.setText('切换到本端(当前：远端)');
				var index = paramStore.find("paramId", Ext.getCmp('paramCombo')
						.getValue());
				Ext.getCmp('paramComboBottom').setValue(
						Ext.getCmp('paramCombo').getValue());
				Ext.getCmp('paramComboBottom').fireEvent('select',
						Ext.getCmp('paramComboBottom'),
						paramStore.getAt(index),
						Ext.getCmp('paramCombo').getValue());

				// 参数复制
				panel2.get(0).setValue(panel1.get(0).getValue());
				Ext.getCmp('startTimeBottom').setValue(
						Ext.getCmp('startTime').getValue());
				Ext.getCmp('endTimeBottom').setValue(
						Ext.getCmp('endTime').getValue());
			} else {
				nendOrFend = 'nend';
				switchButton.setText('切换到远端(当前：本端)');
			}
		}
	}
});

var bottomField = {
	xtype : 'fieldset',
//	layout : 'form',
	id : 'bottomField',
	title : '光纤链路趋势图2',
//	layout : 'fit',
	//width : 1180,
	//region : 'center',
	items : [ {
		xtype : 'compositefield',
		hideLabel: true,
		items : [ new Ext.Spacer({ // 占位
			id : 'chart2',
			height : (window.screen.availHeight-window.screenTop)*0.5-60,
			width : Ext.getBody().getWidth()-250
		}), {
			xtype : 'fieldset',
			layout : 'form',
			width : 240,
			border : false,
			items : [ switchButton, new Ext.Spacer({
				height : 10
			}), fieldSet2, /*otherParam2, */{
				layout : 'column',
				border : false,
				items : [ {
					columnWidth : .5,
					border : false,
					items : {
						xtype : 'button',
						id : 'generateDiagram2',
						text : '生成趋势图',
						ctCls : 'margin-for-button',
						width : 80,
						handler : generateDiagramFend
					}
				}, {
					columnWidth : .5,
					border : false,
					items : {
						columnWidth : .5,
						border : false,
						items : new Ext.Spacer({
							id : 'exportDiagram2',
							width : 120
						})
					}
				} ]
			} ]
		} ]
	} ]
};

// *********************************panel****************************

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	frame : false,
	layout : 'form',
	bodyStyle : 'padding:10px',
	autoScroll : true,
	items : [ topField, bottomField ]
});

// --------------------------------------------functions---------------------------------------

function showFusionCharts(xmlStr, chartdiv, chartId) {

	var myChart = new FusionCharts(
			"../../../resource/FusionCharts/Charts/MSLine.swf", chartId, "100%",
			"100%");
//	if(chartdiv=='chart1'){
//		if(Ext.getCmp('paramCombo').getValue()==0)
//			myChart.setDataURL("lineLoss.xml");//ui用
//		else if(Ext.getCmp('paramCombo').getValue()==1)
//			myChart.setDataURL("lossCoefficient.xml");//ui用
//	}else{
//		if(Ext.getCmp('paramComboBottom').getValue()==0)
//			myChart.setDataURL("lineLoss.xml");//ui用
//		else if(Ext.getCmp('paramComboBottom').getValue()==1)
//			myChart.setDataURL("lossCoefficient.xml");//ui用
//	}
	myChart.setDataXML(xmlStr);
	myChart.render(chartdiv);
}

function exportPicture() {
	// 参数1：为处理程序标识，参数2为：上文中提到的导出需要用到的swf文件
	var myExportComponent = new FusionChartsExportObject("nendExporter",
			"../../../resource/FusionCharts/Charts/FCExporter.swf"); 

	myExportComponent.componentAttributes.btnColor = 'F5F5F5';
	myExportComponent.componentAttributes.btnBorderColor = '666666';
	myExportComponent.componentAttributes.btnFontFace = 'Verdana';
	myExportComponent.componentAttributes.btnFontColor = '333333';
	myExportComponent.componentAttributes.btnFontSize = '12';
	// Title of button
	myExportComponent.componentAttributes.btnsavetitle = '另存为';
	myExportComponent.componentAttributes.btndisabledtitle = '右键生成图片';
	myExportComponent.render("exportDiagram1");
	// 参数1：为处理程序标识，参数2为：上文中提到的导出需要用到的swf文件
	var myExportComponent = new FusionChartsExportObject("fendExporter",
			"../../../resource/FusionCharts/Charts/FCExporter.swf"); 

	myExportComponent.componentAttributes.btnColor = 'F5F5F5';
	myExportComponent.componentAttributes.btnBorderColor = '666666';
	myExportComponent.componentAttributes.btnFontFace = 'Verdana';
	myExportComponent.componentAttributes.btnFontColor = '333333';
	myExportComponent.componentAttributes.btnFontSize = '12';
	// Title of button
	myExportComponent.componentAttributes.btnsavetitle = '另存为';
	myExportComponent.componentAttributes.btndisabledtitle = '右键生成图片';
	myExportComponent.render("exportDiagram2");
}
// 本端
function generateDiagramNend() {
	var fieldId = getId(Ext.getCmp('paramCombo').getValue(), 1);
	var displayItems = Ext.getCmp(fieldId).getValue(1);
	var startTime = Ext.getCmp('startTime').getValue();
	var endTime = Ext.getCmp('endTime').getValue();
	if(displayItems==''||displayItems==null){
		Ext.Msg.alert('提示', '请选择查询项目！');
		return;
	}
	if (startTime == '') {
		Ext.Msg.alert('提示', '请选择开始时间！');
		return;
	}
	if (endTime == '') {
		Ext.Msg.alert('提示', '请选择时间段！');
		return;
	}

	var searchParam = {
		'displayItems' : displayItems.toString(),
		'collectDate' : startTime,
		'endDate' : endTime,
		'linkId' : linkId,
		'nendOrFend' : 'nend'
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'fiber-link-evaluate!generateDiagram.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				showFusionCharts(result.chartXml, "chart1", "nendChart");
				return;
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});

}
// 远端
function generateDiagramFend() {
	var fieldId = getId(Ext.getCmp('paramComboBottom').getValue(), 2);
	var displayItems = Ext.getCmp(fieldId).getValue(1);
	var startTime = Ext.getCmp('startTimeBottom').getValue();
	var endTime = Ext.getCmp('endTimeBottom').getValue();
	if(displayItems==''||displayItems==null){
		Ext.Msg.alert('提示', '请选择查询项目！');
		return;
	}
	if (startTime == '') {
		Ext.Msg.alert('提示', '请选择开始时间！');
		return;
	}
	if (endTime == '') {
		Ext.Msg.alert('提示', '请选择结束时间！');
		return;
	}
	var searchParam = {
		'displayItems' : displayItems.toString(),
		'collectDate' : startTime,
		'endDate' : endTime,
		'linkId' : linkId,
		'nendOrFend' : nendOrFend
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'fiber-link-evaluate!generateDiagram.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				showFusionCharts(result.chartXml, "chart2", "fendChart");
				return;
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
	
}
// 通过所选择的combo获取对应的checkboxgroup的id
function getId(paramId, location) {
	if (location == 1) {
		switch (paramId) {
		case 0:
			return 'lineLoss';
		case 1:
			return 'lossCoefficient';
		}
	} else {
		switch (paramId) {
		case 0:
			return 'lineLossBottom';
		case 1:
			return 'lossCoefficientBottom';
		}
	}
}
// TODO 通过pm std index获取对应的check box group的id
function getParentId(index) {
	var record = new Ext.data.Record.create([ {
		name : 'paramId',
		type : 'long'
	}, {
		name : 'paramName',
		type : 'string'
	} ]);
	switch (index) {
	// 链路衰耗
	case 'LLSH':
	case 'SHZJZ':
		return new record({
			paramId : 0,
			paramName : "链路衰耗"
		});
		// 衰耗系数
	case 'SHXS':
	case 'SHXSLLZ':
	case 'SHXSJGZ':
	case 'MS_CSES':{
		return new record({
			paramId : 1,
			paramName : "衰耗系数"
		});
	}}

}
// --------------------------------------------------------------------------------------------

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	// Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ centerPanel ],
		renderTo : Ext.getBody()
	});
	Ext.getCmp('paramCombo').setValue(0);
	Ext.getCmp('paramComboBottom').setValue(0);
	var index = Ext.getCmp('paramCombo').getStore().find('paramId',
			0);
	Ext.getCmp('paramCombo').fireEvent('select',
			Ext.getCmp('paramCombo'),
			Ext.getCmp('paramCombo').getStore().getAt(index), index);
	Ext.getCmp('paramComboBottom').fireEvent('select',
			Ext.getCmp('paramComboBottom'),
			Ext.getCmp('paramComboBottom').getStore().getAt(index), index);
	//exportPicture();
	win.show();
//	Ext.getCmp('topField').setWidth(win.getWidth()*0.95);
//	Ext.getCmp('chart1').setWidth(win.getWidth()*0.95-350);
//	Ext.getCmp('rightPartTop').setPosition(win.getWidth()*0.95-400);
	generateDiagramNend();
	switchButton.fireEvent('click');
	generateDiagramFend();
});