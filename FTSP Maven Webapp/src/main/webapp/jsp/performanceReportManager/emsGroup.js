// 定义查询条件panel高度
var queryHeight = 20;
// 定义FusionCharts图高度
var chartHeight = 300;
// 时间
var time = '2013-10';
/**
 * 声明日期转换格式
 */
Date.prototype.format =function(format)
{
var o = {
"M+" : this.getMonth()+1, //month
"d+" : this.getDate(), //day
"H+" : this.getHours(), //hour
"m+" : this.getMinutes(), //minute
"s+" : this.getSeconds(), //second
"q+" : Math.floor((this.getMonth()+3)/3), //quarter
"S" : this.getMilliseconds() //millisecond
}
if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
(this.getFullYear()+"").substr(4- RegExp.$1.length));
for(var k in o)if(new RegExp("("+ k +")").test(format))
format = format.replace(RegExp.$1,
RegExp.$1.length==1? o[k] :
("00"+ o[k]).substr((""+ o[k]).length));
return format;
}

//时间控件
var queryTime = new Ext.form.DateField({
	id : 'queryTime',
	fieldLabel : '查询时间',
	format:'Y-m-d'
});
// 时间粒度控件
var timeTypeCombo = new Ext.form.ComboBox({
	id : 'timeTypeCombo',
	fieldLabel : '时间粒度',
	mode : 'local',
	store : new Ext.data.ArrayStore({
				fields : ['value','displayName'],
				data : [['year','年'],['month','月'],['day','天']] 
			}),
	valueField : 'value',
	displayField : 'displayName',
	triggerAction : 'all',// 表示下拉框每次加载所有的值，否则选择某个值后，下拉款里只有一个值
	width : 100,
	listeners : {
		// 设置下拉框的第一条数据为默认值
		beforerender : function(combo){
			// 获取下拉框的第一条记录
			var firstValue = combo.getStore().getRange()[1].get('value');
			// 设置下拉框默认值(这里直接设记录的value值，自动会显示和value对应的displayName)
			combo.setValue(firstValue);
		},
		select : function (combo,record,index){
//			Ext.getCmp('queryTime').getValue().format('Y');
//			queryTime.format('2013-04');
//			alert(Ext.getCmp('timeTypeCombo').getValue());
		}
	}
});
/**
 * 创建查询条件panel
 */
var queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : queryHeight,
	border : false,
	tbar : ['&nbsp;&nbsp;时间粒度：',timeTypeCombo,'-','&nbsp;&nbsp;查询时间：',queryTime,'-',{
		xtype : 'button',
		style : 'margin-left : 30px',
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		handler : query
	},{
		xtype : 'button',
		style : 'margin-left : 30px',
		text : '导出',
		icon : '../../../resource/images/btnImages/export.png'
	}]
	
});
/**
 * 创建FusionCharts图panel
 */
var chartPanel_1 = new Ext.Panel({
	id : 'chartPanel_1',
	layout:'form',
	columnWidth : .5,
//	width :200,
//	style : 'margin-left : 30px',
//	region : 'west',
	html:'<div id="fushionChart1"></div>',
});
/**
 * 创建FusionCharts图panel
 */
var chartPanel_2 = new Ext.Panel({
	id : 'chartPanel_2',
	columnWidth : .5,
//	region : 'east',
//	style : 'margin-left : 10px',
//	width :200,
	layout:'form',
	html:'<div id="fushionChart2"></div>',
});
/**
 * 创建FusionCharts图panel
 */
var chartPanel_3 = new Ext.Panel({
	id : 'chartPanel_3',
	layout:'form',
	columnWidth : .5,
//	height : 200,
//	width :200,
//	style : 'margin-left : 30px',
//	region : 'west',
	html:'<div id="fushionChart3"></div>',
});
/**
 * 创建FusionCharts图panel
 */
var chartPanel_4 = new Ext.Panel({
	id : 'chartPanel_4',
	columnWidth : .5,
//	region : 'east',
//	style : 'margin-left : 10px',
//	width :200,
	layout:'form',
	html:'<div id="fushionChart4"></div>',
});
//var paramMap = {'timeType':timeTypeCombo.getValue()};
/**
 * 创建表格panel
 */
var store = new Ext.data.Store({
	url : 'report!getEmsGroupInfo_Performance.action',// 数据请求地址
	
	baseParams : {// 请求参数
		"jsonString" : Ext.encode({'timeType':'month', 'time':time}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({// 
				totalProperty : 'total',
				root : "rows"
			},  ["DISPLAY_EMS_GROUP", "DISPLAY_EMS", "DISPLAY_SUBNET", "DISPLAY_NE",  "DISPLAY_AREA", 
			     "DISPLAY_STATION", "NET_TYPE", "SHELF_NAME", "CARD_NAME", "PORT", "BUSINESS_TYPE", 
			     "PTP_TYPE", "SPEED", "DISPLAY_CTP", "DISPLAY_TEMPLATE_NAME", "COLLECT_TIME"])
});
// 创建表格选择模型(多选、单选.....)
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
});
// 创建表格列模型
var cm = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : false
	}), checkboxSelectionModel, {
		id : 'DISPLAY_EMS_GROUP',
		header : '网管分组',
		dataIndex : 'DISPLAY_EMS_GROUP',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'DISPLAY_EMS',
		header : '网管名称',
		dataIndex : 'DISPLAY_EMS',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'DISPLAY_SUBNET',
		header : '子网名称',
		dataIndex : 'DISPLAY_SUBNET',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'DISPLAY_NE',
		header : '网元名称',
		dataIndex : 'DISPLAY_NE',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'DISPLAY_AREA',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'DISPLAY_AREA',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'DISPLAY_STATION',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'DISPLAY_STATION',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'NET_TYPE',
		header : '型号',
		dataIndex : 'NET_TYPE',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'SHELF_NAME',
		header : '子架',
		dataIndex : 'SHELF_NAME',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'CARD_NAME',
		header : '板卡',
		dataIndex : 'CARD_NAME',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'PORT',
		header : '端口',
		dataIndex : 'PORT',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'BUSINESS_TYPE',
		header : '业务类型',
		dataIndex : 'BUSINESS_TYPE',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'PTP_TYPE',
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'SPEED',
		header : '速率',
		dataIndex : 'SPEED',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'DISPLAY_CTP',
		header : '通道',
		dataIndex : 'DISPLAY_CTP',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'DISPLAY_TEMPLATE_NAME',
		header : '性能分析模板',
		dataIndex : 'DISPLAY_TEMPLATE_NAME',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'COLLECT_TIME',
		header : '采集时间',
		dataIndex : 'COLLECT_TIME',
		hidden : false,// hidden colunm
		width : 200,
	}]
});
// 创建表格分页工具栏
var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 500,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
// 创建表格
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	forceFit : true,
	bbar : pageTool,
});
/**
 * 创建center的FusionCharts图chartPanel
 */
var chartPanel_first = new Ext.Panel({
	height : chartHeight,
//	border : false,
//	region : 'north',
	layout : 'column',
	items : [chartPanel_1,chartPanel_2]
});
/**
 * 创建center的FusionCharts图chartPanel
 */
var chartPanel_second = new Ext.Panel({
//	height : chartHeight,
//	border : false,
//	region : 'north',
	layout : 'column',
	items : [chartPanel_3,chartPanel_4]
});
/**
 * 创建center的north(包含查询条件queryPanel和FusionCharts图chartPanel)
 */
var childCenterPanel = new Ext.Panel({
	autoScroll : true,
	bodyStyle:'overflow-x:hidden;',
//	autoHeight : true,
//	autoWidth : true,
//	height : queryHieght + chartHeight +chartHeight,
	height : chartHeight,
	region : 'north',
	layout : 'form',
	items : [chartPanel_first,chartPanel_second]
});
/**
 * 创建border布局的头部(north)
 */
var titlePanel = new Ext.Panel({
	title : '按网管分组统计',
	height : 50,
	region : 'north',
	layout : 'form',
	items : [queryPanel]
});
/**
 * 创建border布局的主体(center)
 */
var centerPanel = new Ext.Panel({
	layout : 'border',
	region : 'center',
	border : false,
	items : [childCenterPanel,gridPanel]
});

var hexToDec = function(str) {
	str=str.replace(/\\/g,"%");
	return unescape(str);
}
/* object to string */
function obj2str(o){
	var r = [], i, j = 0, len;
	if(o == null) {
		return o;
	}
	if(typeof o == 'string'){
		return '"'+o+'"';
	}
	if(typeof o == 'object'){
		if(!o.sort){
			r[j++]='{';
			for(i in o){
				r[j++]= '"';
				r[j++]= i;
				r[j++]= '":';
				r[j++]= obj2str(o[i]);
				r[j++]= ',';
			}
			//可能的空对象
			//r[r[j-1] == '{' ? j:j-1]='}';
			r[j-1] = '}';
		}else{
			r[j++]='[';
			for(i =0, len = o.length;i < len; ++i){
				r[j++] = obj2str(o[i]);
				r[j++] = ',';
			}
			//可能的空数组
			r[len==0 ? j:j-1]=']';
		}
		return r.join('');
	}
	return o.toString();
}

function setFusion(){
	
	Ext.Ajax.request({
	    url: 'report!fusionChart_Performance.action',
	    method : 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'flag':1,'timeType':timeTypeCombo.getValue(), 'time':time})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var xml1 = obj.xml_1;
	    	var xml2 = obj.xml_2;
	    	var chart1 = new FusionCharts("../../../resource/FusionCharts/Charts/Pie3D.swf", "chart1Id", "90%", chartHeight);        
	    	chart1.setDataXML(xml1);  
	    	chart1.render("fushionChart1");
	    	var chart2 = new FusionCharts("../../../resource/FusionCharts/Charts/StackedColumn3D.swf", "chart2Id", "90%", chartHeight);        
	    	chart2.setDataXML(xml2);  
	    	chart2.render("fushionChart2");
	    },
	    error:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 
}

//钻取方法
function FusionChartClick(obj){
	var conditions = hexToDec(obj2str(obj));
	var object = Ext.decode(conditions);
//	alert(object);
	Ext.Ajax.request({
    url: 'report!fusionChart_Performance.action',
    method : 'POST',
    params: {
    	"jsonString" : Ext.encode({'caption':object.caption,'label':object.label,'seriesName':object.seriesName,
    					'timeType':timeTypeCombo.getValue(), 'time':time})
    },
    success: function(response) {
    	var obj = Ext.decode(response.responseText);
    	var flag = obj.flag;
//    	alert(obj.flag);
    	if(flag==3){
    		var chart = new FusionCharts("../../../resource/FusionCharts/Charts/StackedColumn3D.swf", "chart3Id", "90%", chartHeight);        
    		chart.setDataXML(obj.xml);  
    		chart.render("fushionChart3");
    	}else if(flag==4){
//    		alert(obj.xml);
    		var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Pie3D.swf", "chart4Id", "90%", chartHeight);        
    		chart.setDataXML(obj.xml);  
    		chart.render("fushionChart4");
    	}
    },
    error:function(response) {
    	top.Ext.getBody().unmask();
    	Ext.Msg.alert("错误",response.responseText);
    },
    failure:function(response) {
    	top.Ext.getBody().unmask();
    	Ext.Msg.alert("错误",response.responseext);
    }
	}); 
}

//查询按钮调用的方法
function query(){
//	alert(queryTime.getValue().format("yyyy-MM-dd HH:mm:ss"));
	if((timeTypeCombo.getValue())=='month'){
		time = queryTime.getValue().format("yyyy-MM");
	}
	store.baseParams = {// 请求参数
		"jsonString" : Ext.encode({'timeType':timeTypeCombo.getValue(), 'time':time}),
		'limit':500
	};
	store.reload();
	setFusion();
}

//加载数据的方法
function loadData(){
	store.load({
		callback : function(records, options, success){//回调函数
			if(success){
				if(records.length == 0){
					Ext.Msg.alert("信息","查询结果为空！"); 
					}                		    	
				}else{
					Ext.Msg.alert("错误",'查询失败，请重新查询！');
		            }
		 		}
			}
	);
}

//钻取方法
//function drill(data,flag){
//	
//	Ext.Ajax.request({
//	    url: 'report!fusionChart_Performance.action',
//	    method : 'POST',
//	    params: {
//	    	"jsonString" : Ext.encode({'ID':data,'flag':flag})
//	    },
//	    success: function(response) {
//	    	var obj = Ext.decode(response.responseText);
//	    	if(flag==3){
//	    		var xml = "<chart bgcolor='F3f3f3' formatNumberScale='0' baseFontSize='10' xAxisName='' " +
//	    				  "caption='" + obj.caption + "异常级别统计' bgcolor='#DFE8F6' yAxisName='' scrollbars='1'>" + obj.xml + "</chart>";
//	    		var chart = new FusionCharts("../../../resource/FusionCharts/Charts/StackedColumn3D.swf", "chart3Id", "90%", chartHeight);        
//	    		chart.setDataXML(xml);  
//	    		chart.render("fushionChart3");
//	    	}else if(flag==4){
//	    		var xml = "<graph caption='异常性能总计' showNames='1' bgcolor='F3f3f3' decimalPrecision='0'>" + obj.xml + "</graph>";
//	    		var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Pie3D.swf", "chart3Id", "90%", chartHeight);        
//	    		chart.setDataXML(xml);  
//	    		chart.render("fushionChart4");
//	    	}
//	    },
//	    error:function(response) {
//	    	top.Ext.getBody().unmask();
//        	Ext.Msg.alert("错误",response.responseText);
//	    },
//	    failure:function(response) {
//	    	top.Ext.getBody().unmask();
//        	Ext.Msg.alert("错误",response.responseText);
//	    }
//	}); 
//	store.baseParams = {"jsonString" : Ext.encode({'ID':data,'flag':flag}),'limit':500};
//	store.proxy = new Ext.data.HttpProxy({url:'report!fusionChart_Performance.action'});
//	store.reload();
// }

// Ext加载
Ext.onReady(function(){
	var view = new Ext.Viewport({
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [titlePanel,centerPanel]
	});
	view.show();
	loadData();
	setFusion();
});

