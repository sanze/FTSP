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
var paramStore = new Ext.data.ArrayStore({
	idIndex : 0,
	fields : [ 'paramId', 'paramName', 'pmType' ]
});
var SDHData = [ [ 0, "再生段", 1 ], [ 1, "复用段", 1 ], [ 4, "VC4通道", 1 ],
		[ 5, "VC3通道", 1 ], [ 6, "VC12通道", 1 ], [ 7, "输出光功率", 2 ],
		[ 8, "输入光功率", 2 ] ];
var WDMData = [ [ 2, "光监控信道", 1 ], [ 3, "FEC误码率", 1 ], [ 7, "输出光功率", 2 ],
		[ 8, "输入光功率", 2 ], [ 9, "每信道光功率", 2 ], [ 10, "每信道中心波长", 2 ],
		[ 11, "信噪比", 2 ], [ 12, "OTU误码", 1 ], [ 13, "ODU误码", 1 ] ];
(function() {
	if (type == 1)
		paramStore.loadData(SDHData);
	if (type == 2)
		paramStore.loadData(WDMData);
})();

// 输入光功率
var inputLightPower = {
	id : 'inputLightPower',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	disabled : true,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'RPL_AVG',
		name : 'RPL_AVG',
		boxLabel : '输入光功率平均值',
		inputValue : 'RPL_AVG'
	},{
		xtype : 'checkbox',
		id : 'RPL_CUR',
		name : 'RPL_CUR',
		boxLabel : '输入光功率当前值',
		inputValue : 'RPL_CUR',
		checked:true
	}, {
		xtype : 'checkbox',
		id : 'RPL_MAX',
		name : 'RPL_MAX',
		boxLabel : '输入光功率最大值',
		inputValue : 'RPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'RPL_MIN',
		name : 'RPL_MIN',
		boxLabel : '输入光功率最小值',
		inputValue : 'RPL_MIN'
	} ]
};

// 输入光功率
var inputLightPowerAvg = {
	id : 'inputLightPowerAvg',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	disabled : true,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'RPL_AVG',
		name : 'RPL_AVG',
		boxLabel : '输入光功率平均值',
		inputValue : 'RPL_AVG',
		checked:true
	},{
		xtype : 'checkbox',
		id : 'RPL_CUR',
		name : 'RPL_CUR',
		boxLabel : '输入光功率当前值',
		inputValue : 'RPL_CUR'
		
	}, {
		xtype : 'checkbox',
		id : 'RPL_MAX',
		name : 'RPL_MAX',
		boxLabel : '输入光功率最大值',
		inputValue : 'RPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'RPL_MIN',
		name : 'RPL_MIN',
		boxLabel : '输入光功率最小值',
		inputValue : 'RPL_MIN'
	} ]
};


// 输出光功率
var outputLightPower = {
	id : 'outputLightPower',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	disabled : true,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'TPL_AVG',
		name : 'TPL_AVG',
		boxLabel : '输出光功率平均值',
		editable:false,
		inputValue : 'TPL_AVG'
	},{
		xtype : 'checkbox',
		id : 'TPL_CUR',
		name : 'TPL_CUR',
		boxLabel : '输出光功率当前值',
		editable:false,
		inputValue : 'TPL_CUR',
		checked:true
	}, {
		xtype : 'checkbox',
		id : 'TPL_MAX',
		name : 'TPL_MAX',
		boxLabel : '输出光功率最大值',
		editable:false,
		inputValue : 'TPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'TPL_MIN',
		name : 'TPL_MIN',
		boxLabel : '输出光功率最小值',
		editable:false,
		inputValue : 'TPL_MIN'
	} ]
};

// 输出光功率
var outputLightPowerAvg = {
	id : 'outputLightPowerAvg',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	disabled : true,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'TPL_AVG',
		name : 'TPL_AVG',
		boxLabel : '输出光功率平均值',
		editable:false,
		inputValue : 'TPL_AVG',
		checked:true
	},{
		xtype : 'checkbox',
		id : 'TPL_CUR',
		name : 'TPL_CUR',
		boxLabel : '输出光功率当前值',
		editable:false,
		inputValue : 'TPL_CUR'
		
	}, {
		xtype : 'checkbox',
		id : 'TPL_MAX',
		name : 'TPL_MAX',
		boxLabel : '输出光功率最大值',
		editable:false,
		inputValue : 'TPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'TPL_MIN',
		name : 'TPL_MIN',
		boxLabel : '输出光功率最小值',
		editable:false,
		inputValue : 'TPL_MIN'
	} ]
};


var timeRangeStore = new Ext.data.ArrayStore({
	idIndex : 0,
	fields : [ 'timeRangeId', 'timeRangeName' ],
	data : [ [ 1, "前10天" ], [ 2, "前20天" ], [ 3, "前30天" ], [ 4, "后10天" ],
			[ 5, "后20天" ], [ 6, "后30天" ] ]
});
var panel1 = new Ext.form.FormPanel({
	id : 'panel1',
	autoScroll : true,
	border : false,
	height : 175,
	width : 280,
	// margins:{top:20, right:0, bottom:20, left:0},
	style : " margin-top: 20px; margin-bottom: 15px;",
	labelWidth : 59,
	items : []
});

var timeRangeCombo = new Ext.form.ComboBox({
	id : 'timeRangeCombo',
	mode : 'local',
	fieldLabel : '时间段',
	store : timeRangeStore,
	editable:false,
	value:1,
	labelSeparator : ':',
	width : 160,
	triggerAction : 'all',
	valueField : 'timeRangeId',
	displayField : 'timeRangeName'
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
				value:(Ext.util.Format.dateRenderer('Y-m-d'))(new Date()),
				width : 160,
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "startTime",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd',
							autoPickDate : true,
							maxDate : '%y-%M-%d'

						});
						this.blur();
					}
				}
			} ]
};


// *************************field set ******************************
var otherParam1 = {
	id : 'otherParam1',
	xtype : 'fieldset',
	layout : 'form',
	padding : '10px',
	// height : 100,
	labelWidth : 60,
	items : [ startTime, timeRangeCombo ]
};

// TODO
var paramCombo = new Ext.form.ComboBox({
	id : 'paramCombo',
	fieldLabel : '参数类型',
	mode : 'local',
	labelSeparator : ':',
	triggerAction : 'all',
	disabled : true,
	store : paramStore,
	editable:false,
	valueField : 'paramId',
	displayField : 'paramName'
});

var panel1 = new Ext.form.FormPanel({
	id : 'panel1',
	autoScroll : true,
	border : false,
	height : 175,
	width : 280,
	// margins:{top:20, right:0, bottom:20, left:0},
	style : " margin-top: 20px; margin-bottom: 15px;",
	labelWidth : 59,
	items : []
});

// TODO
var fieldSet1 = {
	id : 'fieldSet1',
	xtype : 'fieldset',
	padding : '15px',
	layout : 'form',
	editable:false,
	// height : 250,
	labelWidth : 60,
	border : true,
	items : [paramCombo,panel1]
};


var topField = {
	xtype : 'fieldset',
	id : 'topField',
	title : name,
	 width : 1180,
	layout : 'fit',
	items : [ {
		xtype : 'compositefield',
		// defaults : {
		// flex : 2
		// },
		items : [ new Ext.Spacer({ // 占位
			id : 'chart1',
			height : 320,
			width : 800
		}), {
			xtype : 'fieldset',
			id : 'rightPartTop',
			layout : 'form',
			border : false,
			width : 350,
			items : [  fieldSet1,otherParam1, {
				layout : 'column',
				height : 40,
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



var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	frame : false,
	layout : 'form',
	bodyStyle : 'padding:20px 50px 0 20px',
	autoScroll : true,
	items : [ topField ]
});

// --------------------------------------------functions---------------------------------------

function showFusionCharts(xmlStr, chartdiv, chartId) {

	var myChart = new FusionCharts(
			"../../../resource/FusionCharts/Charts/MSLine.swf", chartId, "800",
			"380");

	// myChart.setDataURL("dataLine.xml");
	myChart.setDataXML(xmlStr);
	myChart.render(chartdiv);
}

function exportPicture() {
	var myExportComponent = new FusionChartsExportObject("nendExporter",
			"../../../resource/FusionCharts/Charts/FCExporter.swf"); // 参数1：为处理程序标识，参数二为：上文中提到的导出需要用到的swf文件

	myExportComponent.componentAttributes.btnColor = 'F5F5F5';

	myExportComponent.componentAttributes.btnBorderColor = '666666';

	myExportComponent.componentAttributes.btnFontFace = 'Verdana';

	myExportComponent.componentAttributes.btnFontColor = '333333';

	myExportComponent.componentAttributes.btnFontSize = '12';
	// Title of button
	myExportComponent.componentAttributes.btnsavetitle = '另存为';
	myExportComponent.componentAttributes.btndisabledtitle = '右键生成图片';
	myExportComponent.render("exportDiagram1");
}
// 本端
function generateDiagramNend() {
//	var fieldId = getId(Ext.getCmp('paramCombo').getValue(), 1);
//	var pmStdIndexSelect = Ext.getCmp(fieldId).getValue(1);
	var startTime = Ext.getCmp('startTime').getValue();
	var timeRange = Ext.getCmp('timeRangeCombo').getValue();
//	var needLimit = Ext.getCmp('max_min').getValue() ? 1 : 0;
//	var pmType = paramStore.getAt(paramStore.find('paramId',Ext.getCmp('paramCombo').getValue())).get('pmType');
//	if (startTime == '') {
//		Ext.Msg.alert('提示', '请选择开始时间！');
//		return;
//	}
//	if (timeRange == '') {
//		Ext.Msg.alert('提示', '请选择时间段！');
//		return;
//	}
//	if (Ext.getCmp('panel1').get(0).getValue(1).length > 6) {
//		Ext.Msg.alert('提示', '请勿选择超过6个性能！');
//		return;
//	}
	var searchParam = {
		'searchCond.pmStdIndex' : pmStdIndex,
		'searchCond.startTime' : startTime,
		'searchCond.timeRange' : timeRange,
		'searchCond.ptpId' : ptpId,
		'searchCond.ctpId' : ctpId,
		'searchCond.emsConnectionId' : emsConnectionId,
		'searchCond.needLimit' : 0,
		'searchCond.type' : type,
		'searchCond.pmType' : 2
	};
	Ext.Ajax.request({
		url : 'pm-search!generateDiagramNend.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			centerPanel.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				showFusionCharts(result.returnMessage, "chart1", "nendChart");
				return;
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			centerPanel.getEl().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			centerPanel.getEl().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});

}
function init(){
	
	if(pmStdIndex =="RPL_CUR"){
		// 输入
		Ext.getCmp('paramCombo').setValue("输入光功率");
		panel1.add(inputLightPower);
		
	}else if(pmStdIndex =="TPL_CUR"){
		// 输出
		Ext.getCmp('paramCombo').setValue("输出光功率");
		panel1.add(outputLightPower);
	}else if(pmStdIndex =="RPL_AVG"){
		
		// 输入
		Ext.getCmp('paramCombo').setValue("输入光功率");
		panel1.add(inputLightPowerAvg);
	}else {
		// 输出
		Ext.getCmp('paramCombo').setValue("输出光功率");
		panel1.add(outputLightPowerAvg);
	}
	panel1.doLayout();
}

// --------------------------------------------------------------------------------------------

Ext
		.onReady(function() {
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
			// showFusionCharts();
			init();
			exportPicture();
			win.show();

		});