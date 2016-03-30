netLevelComboField.emptyText="全部";
projectComboGridField.allowBlank=false;
var querypanel = new Ext.form.FormPanel({
	id : "querypanel",
	name : "querypanel",
	region:"north",  
	height : 50, 
	border : false,
	tbar:['-',netLevelComboBar,'-',projectComboGridBar,'-', '月份：',{
		xtype : 'textfield',
		id : 'queryMonth', 
		name : 'queryMonth', 
		allowBlank : false,
		readOnly : true,
		anchor : '95%',
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "queryMonth",
					isShowClear : false,
					readOnly : true,
//					maxDate : new Date().getDate()<=3?'%y-{%M-2}':'%y-{%M-1}',
					dateFmt : 'yyyy-MM',
					autoPickDate : true
				});
				this.blur();
			}
		}
	},'-',{
		xtype : 'button',
		text : '查询', 
		id : 'searchPlanBtu',
		icon : '../../../resource/images/btnImages/search.png',
		handler : function(){
			generateDiagram();
		}
	},{
		xtype : 'button',
		text : '重置', 
		id : 'resetBtu',
		icon : '../../../resource/images/btnImages/refresh.png',
		handler : function(){
			netLevelComboBar.getForm().reset();
			projectComboGridBar.getForm().reset();
			Ext.getCmp('queryMonth').reset();
		}
	},'->',{
		xtype : 'displayfield', 
		value:"A级",
		width: 55, 
		style : 'background:#33CC00;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp',{
		xtype : 'displayfield',
		value:"B级",
		width : 55,
		style : 'background:#3366CC;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp',{
		xtype : 'displayfield',
		value:"C级",
		width : 55,
		style : 'background:#FF9933;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp',{
		xtype : 'displayfield',
		value:"D级",
		width : 55,
		style : 'background:#FF0000;line-height:20px;text-align : center;vertical-align:middle;margin-right:30px;'
	},'&nbsp','&nbsp','&nbsp']
}); 

var linePanel = new Ext.Panel({   
	region:"center", 
	autoScroll:true,  
	border:false, 
    id:'lineField',
    name:'lineField',  
    items:[] 
});

function generateDiagram(){ 
	var netLevel = netLevelComboField.getValue();
	var project = projectComboGridField.comboGrid.getValue();
	var collectDate = Ext.getCmp('queryMonth').getValue();
	if(project==null || project=="" || collectDate==null ||collectDate=="") {
		Ext.Msg.alert("提示","请选择系统和月份！");
		return;
	}
	var searchParam = {
		'netLevel' : netLevel,
		'transSysId' : project,
		'month' : collectDate 
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'evaluate-statistic!generateDiagramLine.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.decode(response.responseText); 
 			if(result.returnResult==1){
 				if(result.returnMessage=="性能数据为空！"){  
 					Ext.Msg.alert("提示",result.returnMessage);
 					Ext.getCmp('lineField').removeAll();
					Ext.getCmp('lineField').doLayout(); 
 					return;
 				}
 				Ext.getCmp('lineField').removeAll();
				json = eval(result.rList); 
			    for(var i=0; i<json.length; i++){ 
			    	var spacer = {
						xtype : 'compositefield',
						border : false,
						height:270,
						items : [{
							xtype : 'displayfield',  
							width: 60,  
							height:260, 
							style : 'padding-top:130px;text-align:center;',
							value : i+1
						},{
							xtype : 'spacer',
							id : json[i].lineId
						}]
					};  
					Ext.getCmp('lineField').add(spacer);
					Ext.getCmp('lineField').doLayout();   
					showFusionCharts(json[i].chartXml, json[i].lineId, "nendChart1");
					Ext.getCmp('lineField').doLayout();   
			    } 
			}else{ 
				Ext.Msg.alert("提示", "评估趋势图生成失败！");
			}
		},
		failure : function(response) {
			var result = Ext.decode(response.responseText);
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

function showFusionCharts(xmlStr, chartdiv, chartId) {  
	var myChart = new FusionCharts("../../../resource/FusionCharts/Charts/MSLine.swf",  
			chartId, Ext.getCmp('win').getWidth()*0.9,"250"); 
	myChart.setDataXML(xmlStr);
	myChart.render(chartdiv);
}

Ext.onReady(function(){ 
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}; 
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	
	var win = new Ext.Viewport({ 
		id : 'win', 
		layout : 'border',
		items : [querypanel,linePanel],
		renderTo : Ext.getBody()
	});
	win.show();   
});