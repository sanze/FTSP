//定义查询条件panel高度
var queryHeight = 30;
// 定义FusionCharts图高度
var chartHeight =(Ext.getBody().getHeight()-queryHeight)*0.6;
// 时间
var time = '';



/**
 * 创建网管分组数据源
 */
var emsGroupStorePer = new Ext.data.Store({
	// 获取数据源地址
	proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
		url : 'report!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
		disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
	}),
	baseParams : {"displayAll" : true,"displayNone" : false},
	// record格式
	reader : new Ext.data.JsonReader({
		root : 'rows',//json数据的key值
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});
// 访问地址，加载数据(如果没有这一句，则不会去后台查询)
emsGroupStorePer.load({
	// 回调函数
	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
		// 获取下拉框的第一条记录
		var firstValue = records[0].get('BASE_EMS_GROUP_ID');
		// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
		Ext.getCmp('emsGroupComboPer').setValue(firstValue);
	}
});


// 创建网管分组下拉框
var emsGroupComboPer = new Ext.form.ComboBox({
	id : 'emsGroupComboPer',
	fieldLabel : '网管分组',
	store : emsGroupStorePer,// 数据源
	valueField : 'BASE_EMS_GROUP_ID',// 下拉框实际值
	displayField : 'GROUP_NAME',// 下拉框显示值
	editable : false,
	triggerAction : 'all',// 每次加载所有值，否则下拉框选择一个值后，再点击就只有一个值
	width :120,
	resizable: true,
	listeners : {// 监听事件
		select : function(combo, record, index) {
		}
	}
});



/**
 * 创建FusionCharts图panel
 */
var chartPanel_1 = new Ext.Panel({
	id : 'chartPanel_1',
	layout:'form',
	border: false,
	width:'100%',
	html:'<div id="fushionChart1" style="text-align:center;margin:10px"></div>'
});

/**
 * 创建FusionCharts图panel
 */
var chartPanel_2 = new Ext.Panel({
	id : 'chartPanel_2',
	border: false,
	layout:'form',
	width:'100%',
	html:'<div id="fushionChart2" style="text-align:center;margin:10px"></div>'
});

/**
 * 创建表格panel
 */
var store = new Ext.data.Store({
	url : 'report!getEmsGroupInfo_Performance.action',// 数据请求地址
	baseParams : {// 请求参数
		"jsonString" : Ext.encode({'GROUPID':Ext.getCmp('emsGroupComboPer').getValue(),'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			},  ["DISPLAY_EMS_GROUP", "DISPLAY_EMS", "DISPLAY_SUBNET", "DISPLAY_NE",  "DISPLAY_AREA", 
			     "DISPLAY_STATION", "NET_TYPE", "SHELF_NAME", "CARD_NAME", "PORT", "BUSINESS_TYPE", 
			     "PTP_TYPE", "SPEED", "DISPLAY_CTP", "DISPLAY_TEMPLATE_NAME", "COLLECT_TIME"]),
     listeners:{
 		beforeload:function(){
 			store.baseParams={// 请求参数
 					"jsonString" : Ext.encode({'GROUPID':Ext.getCmp('emsGroupComboPer').getValue(),'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time}),
 					'limit':500
 				}
 		}
 	}	
});


//创建表格列模型
var cm = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : false
	}),{
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
	},{
		id : 'NET_TYPE',
		header : '型号',
		dataIndex : 'NET_TYPE',
		hidden : false,// hidden colunm
		width : 80,
	},{
		id : 'PORT',
		header : '端口',
		dataIndex : 'PORT',
		hidden : false,// hidden colunm
		width : 80,
	}, {
		id : 'BUSINESS_TYPE',
		header : '业务类型',
		dataIndex : 'BUSINESS_TYPE',
		hidden : false,// hidden colunm
		width : 80,
	}, {
		id : 'PTP_TYPE',
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		hidden : false,// hidden colunm
		width : 80,
	}, {
		id : 'SPEED',
		header : '速率',
		dataIndex : 'SPEED',
		hidden : false,// hidden colunm
		width : 80,
	}, {
		id : 'DISPLAY_CTP',
		header : '通道',
		dataIndex : 'DISPLAY_CTP',
		hidden : false,// hidden colunm
		width : 80,
	},{
		id : 'PERFORMANCE_EVENT',
		header : '性能事件',
		dataIndex : 'PERFORMANCE_EVENT',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'DIRECTION',
		header : '方向',
		dataIndex : 'DIRECTION',
		hidden : false,// hidden colunm
		width : 80,
	}, {
		id : 'PM_VALUE',
		header : '性能值',
		dataIndex : 'PM_VALUE',
		hidden : false,// hidden colunm
		width : 80,
	}, {
		id : 'PM_COMPARE_VALUE',
		header : '性能基准值',
		dataIndex : 'PM_COMPARE_VALUE',
		hidden : false,// hidden colunm
		width : 80,
	}, {
		id : 'EXCEPTION_COUNT',
		header : '连续异常',
		dataIndex : 'EXCEPTION_COUNT',
		hidden : false,// hidden colunm
		width : 80,
	},{
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
		width : 120,
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
	forceFit : true,
	height : Ext.getBody().getHeight()-queryHeight-chartHeight,
	bbar : pageTool,
});





/**
 * 创建center的north(包含查询条件queryPanel和FusionCharts图chartPanel)
 */
var childCenterPanel = new Ext.Panel({
	autoScroll : true,
	bodyStyle:'overflow-x:hidden;',
	height : chartHeight,
	layout : 'form',
	border:false,
	items : [chartPanel_1,chartPanel_2]
});



queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : queryHeight,
	border : false,
	tbar : ['&nbsp;&nbsp;网管分组:',emsGroupComboPer,'&nbsp;&nbsp;时间粒度:',timeFinenessCombo,' ',queryYear,' ',
	        queryMonth,' ',queryDay,' ',{
		xtype : 'button',
		style : 'margin-left : 30px',
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		handler : queryByCondition
	},{
		xtype : 'button',
		style : 'margin-left : 30px',
		text : '导出',
		icon : '../../../resource/images/btnImages/export.png',
		handler : exportByCondition
	}]
	
});




/**
 * 创建border布局的头部(north)
 */
var titlePanel = new Ext.Panel({
	title : '',
	height : queryHeight,
	region : 'north',
	layout : 'form',
	border:false,
	items : [queryPanel]
});
/**
 * 创建border布局的主体(center)
 */
var centerPanel = new Ext.Panel({
	region : 'center',
	layout : 'form',
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
	    	'jsonString' : Ext.encode({'flag':1,'GROUPID':Ext.getCmp('emsGroupComboPer').getValue(),'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var xml1 = obj.xml_1;
	    	var xml2 = obj.xml_2;
	    	var chart1 = new FusionCharts("../../../resource/FusionCharts/Charts/Column3D.swf", "chart1Id", "80%", chartHeight);        
	    	chart1.setDataXML(xml1);  
	    	chart1.render("fushionChart1");
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
	Ext.Ajax.request({
    url: 'report!fusionChart_Performance.action',
    method : 'POST',
    params: {
    	"jsonString" : Ext.encode({'caption':object.caption,'label':object.label,
    		'GROUPID':Ext.getCmp('emsGroupComboPer').getValue(),'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
    },
    success: function(response) {
    	var obj = Ext.decode(response.responseText);
    	var flag = obj.flag;
    	if(flag==3 && obj.xml_1!=null && obj.xml_1!=''){
    		var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Column3D.swf", "chart2Id", "80%", chartHeight);        
    		chart.setDataXML(obj.xml_1);  
    		chart.render("fushionChart2");
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
	var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Column3D.swf", "chart2Id", "80%", chartHeight);        
	chart.setDataXML('');  
	chart.render("fushionChart2");
	store.load({
	params : {// 请求参数
		"jsonString" : Ext.encode({'GROUPID':Ext.getCmp('emsGroupComboPer').getValue(),'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time}),
		'start':0,
		'limit':500
	}});
	setFusion();
}

//导出
function exportInfo(){
	var url = 'report!exportEmsGroupInfo_Performance.action?jsonString='+Ext.encode({'GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})+"&limit=5000";
	document.exportExcel.action = url;
	document.exportExcel.method = "post";
	document.exportExcel.submit();

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
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	var view = new Ext.Viewport({
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [titlePanel,centerPanel]
	});
	view.show();
});

