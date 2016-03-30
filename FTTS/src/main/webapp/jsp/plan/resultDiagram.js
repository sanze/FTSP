
//----------------------------- date picker -------------------------------------------------------
var startDate = {
	xtype : 'compositefield',
	fieldLabel : '起始日期',
	defaults : {
		flex : 2
	},
	items : [ {
				xtype : 'textfield',
				id : 'startDate',
				name : 'startDate',
				fieldLabel : '起始日期',
				allowBlank : false,
				readOnly : true,
				width : 100,
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "startDate",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd',
							autoPickDate : true,
							maxDate : '%y-%M-%d',
						});
						this.blur();
					}
				}
			} ]
};

var endDate = {
	xtype : 'compositefield',
	fieldLabel : '结束日期',
	defaults : {
		flex : 2
	},
	items : [ {
				xtype : 'textfield',
				id : 'endDate',
				name : 'endDate',
				fieldLabel : '结束日期',
				allowBlank : false,
				readOnly : true,
				width : 100,
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "endDate",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd',
							autoPickDate : true,
							maxDate : '%y-%M-%d',
						});
						this.blur();
					}
				}
			} ]
};

//----------------------------- field set ---------------------------------------------------------
var fieldSet1 = {
	id: 'displayParam',
    xtype: 'fieldset',
    layout: 'form',
	padding : '5px',
    width: 210,
    defaultType: 'radio',
    labelWidth: 65,
    items: [{
        checked: true,
        fieldLabel: '参数类型',
        boxLabel: '全程传输损耗',
        name: 'param',
        inputValue: 1
    }, {
        boxLabel: '全程光学距离',
        name: 'param',
        inputValue: 2
    }, {
        boxLabel: '事件计数',
        name: 'param',
        inputValue: 3
    }]
};

var otherParam = {
	id : 'otherParam',
	xtype : 'fieldset',
	layout : 'form',
	padding : '10px',
	width : 210,
	// height : 100,
	labelWidth : 60,
	items : [ startDate, endDate ]
};

//----------------------------- field -------------------------------------------------------------
var topField = {
		xtype : 'fieldset',
		id : 'topField',
		title : '光缆测试结果趋势图(周期)',
		layout : 'fit',
		width : Ext.getBody().getWidth()-160,
		items : [ {
			xtype : 'compositefield',
			items : [ new Ext.Spacer({ // 占位
				id : 'chart1',
				height : 420,
				width : Ext.getBody().getWidth()-420
			}), {
				xtype : 'fieldset',
				id : 'rightPartTop',
				layout : 'form',
				border : false,
				width : 250,
				items : [ fieldSet1, otherParam, {
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
							width : 50,
							handler : generateDiagram
						}
					}, {
						columnWidth : .5,
						border : false,
						items : new Ext.Spacer({
							id : 'exportDiagram1',
							width : 50
						})
					} ]
				} ]
			} ]
		} ]

	};

function generateDiagram() {
	var displayType;
	var param = Ext.getCmp('displayParam').items;
	for ( var i = 0; i < param.length; i++) {
		if(param.get(i).checked){
			displayType = param.get(i).inputValue;
		}
	}
	var startDate = Ext.getCmp('startDate').getValue();
	var endDate = Ext.getCmp('endDate').getValue();

	var searchParam = {
		'displayCond.testRouteId' : testRouteId,
		'displayCond.type' : displayType,
		'displayCond.startTime' : startDate,
		'displayCond.endTime' : endDate
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'plan!generateDiagram.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				showFusionCharts(result.returnMessage, "chart1", "TestResultChart");
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
};

function showFusionCharts(xmlStr, chartdiv, chartId) {
	var myChart = new FusionCharts("../../resource/FusionCharts/Charts/Line.swf", chartId, Ext.getBody().getWidth()-430, "420");
//	myChart.setDataURL("data1.xml");
	myChart.setDataXML(xmlStr);
	myChart.render(chartdiv);
}

function change_date(days) {
	// 参数表示在当前日期下要增加的天数  
	var now = new Date();  
	// + 1 代表日期加，- 1 代表日期减  
	now.setDate((now.getDate() + 1) - 1 * days);  
	var year = now.getFullYear();  
	var month = now.getMonth() + 1;  
	var day = now.getDate();  
	if (month < 10) {  
		month = '0' + month;  
	}  
	if (day < 10) {  
		day = '0' + day;  
	}  

	return year + '-' + month + '-' + day;  
};
//------------------------------ panel ------------------------------------------------------------
var mainPanel = new Ext.Panel({
	id : 'mainPanel',
	region : 'center',
	frame : false,
	layout : 'form',
	bodyStyle : 'padding:20px 50px 0 20px',
	autoScroll : true,
	items : [ topField ]
});
	
//-------------------------------------------------------------------------------------------------
Ext.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			Ext.Ajax.timeout = 900000;
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ mainPanel ],
				renderTo : Ext.getBody()
			});

//			exportPicture();
			win.show();

			Ext.getCmp("startDate").setValue(change_date(22));
			Ext.getCmp("endDate").setValue(change_date(1));
			generateDiagram();
//			showFusionCharts("", "chart1", "TestResultChart");
		});