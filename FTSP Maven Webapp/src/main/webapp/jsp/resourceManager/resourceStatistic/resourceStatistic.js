//定义查询条件panel高度
var queryHeight = 40;
// 定义FusionCharts图高度
var chartHeight = 370;
// 时间
var time = '';
var stores = [];
var cms = [];
var type;  
var nodes;
//var store;
var locator = {
    id:"area",
	xtype : "area",
	minSize : 230,
	maxSize : 320,
	autoScroll : true,
	forceFit : true,
	collapsed : false,
	collapsible : true,
	split : true,
	maxLevel : 11,
	checkModel : "multiple"
};
var areaField = new Ext.form.TextField({
	id : 'areaField',
	fieldLabel : "区&nbsp&nbsp&nbsp属", 
	readOnly : true,
	listeners : {
		focus : function(field){  
           var win = new Ext.Window({ 
                   layout : 'fit',
                   modal : true,
                   closable:true,
                   plain:true,
	       		   width : 300,
				   height : (Ext.getBody().getHeight()-60)*0.7,
				   pageX :field.getPosition()[0],
				   pageY :field.getPosition()[1]+20,
                   items : [locator],
                   buttons: [{
                   	scope:this,
                       text: '确定',
                       handler: function(){
                           nodes = Ext.getCmp("area").getSelectedNodes();
                           if(nodes.total == 0){
                               //没选中
                               Ext.Msg.alert('提示', '请选择'+top.FieldNameDefine.AREA_NAME+'或'+top.FieldNameDefine.STATION_NAME+'！');
                           } else{
                   			var tmp="";
            				for(var i=0;i<nodes.total;i++){
            					tmp+=nodes.nodes[i].text+",";
            				}
                			field.setValue(tmp);
                               win.close();
                           }
                       }
                   },{
                       text: '取消',
                       handler: function(){
                           win.close();
                       }
                   }],
                   buttonAlign:"center"
               });
            win.show();
			field.blur();
		}
	}
}); 


var statisticType = new Ext.form.RadioGroup({ 
id : 'statisticType',
name : 'statisticType',
width:400,
items : [{
    xtype : 'label',
    columnWidth : 50,
    text : '　　'
},{
    name : 'statisticType1', 
    inputValue : 0,
    boxLabel : '统计网元',
    checked : true

}, {
    name : 'statisticType1', 
    inputValue : 1,
    boxLabel : '统计板卡'
}, {
    name : 'statisticType1', 
    inputValue : 2,
    boxLabel : '统计端口'
}  ],
listeners : {
    change : function() {
		type = Ext.getCmp("statisticType").getValue().inputValue;
		//根据type动态配置store和columnModel  
		
           var pageTool = gridPanel.getBottomToolbar();  
           pageTool.bindStore(stores[type]); 
           pageTool.doRefresh(); 
           gridPanel.reconfigure(stores[type], cms[type]); 
           gridPanel.getView().refresh();   
           query();
    }
} 
});
/**
 * 创建查询条件panel
 */   
var queryPanel = new Ext.FormPanel({
	id : 'queryPanelGroup',
	height : queryHeight,
	region : 'north',
	border : false,
	bodyStyle : 'padding:10px 10px 0 10px',  
    labelAlign: 'right',
    collapsed: false,   // initially collapse the group 
	items : [{
		layout : 'hbox',
		border : false,
		items : [{
			layout : 'form',
			labelSeparator : "：",
			border : false, 
			items : [areaField]
		},statisticType,{ 
			layout : 'form',
			labelSeparator : "", 
			border : false,
			items : [{
	            layout : 'hbox',
	            border : false,
	            forceFit : false, 
	            width:250,
	            items : [ {
	                xtype : 'label',
	                width : 50,
	                text : '　　'
	            }, {
	                xtype : 'button',
	                text : '查询' ,
	                width : 60,
	                privilege : viewAuth,
	                handler : query
	            }, {
	                xtype : 'label',
	                width : 20,
	                text : '　　'
	            }
//	            ,{
//	                xtype : 'button',
//	                text : '导出',
//	                width : 60,
//	                privilege : viewAuth,
//	                handler : function() { 
//	                }
//	            } 
	            ]
			}]
		 }]
		}]
}); 

/**
 * 创建列表数据源
 */
var storeNe = new Ext.data.Store({
	url : 'resource-statistic!getStatisticGrid.action',// 数据请求地址 
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, ["fullPath","areaName", "stationName", "neName", "neModel", "emsName"]) 
});

var storeUnit = new Ext.data.Store({
	url : 'resource-statistic!getStatisticGrid.action',// 数据请求地址
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, ["fullPath","areaName", "stationName", "neName", "neModel",
			    "emsName","unitDesc", "unitName"]) 
});
var storePort= new Ext.data.Store({
	url : 'resource-statistic!getStatisticGrid.action',
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, ["fullPath","areaName", "stationName", "neName", "neModel",
			    "emsName", "portNo", "rate"]) 
});

var cmNe = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : false
	}),{
		id : 'areaName',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName', 
		width : 200
	}, {
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'stationName', 
		width : 200
	},{
		id : 'emsName',
		header : '网管',
		dataIndex : 'emsName', 
		width : 200
	}, {
		id : 'neName',
		header : '网元',
		dataIndex : 'neName', 
		width : 200
	},{
		id : 'neModel',
		header : '网元型号',
		dataIndex : 'neModel', 
		width : 120
	}]
});
var cmUnit = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : false
	}),{
		id : 'areaName',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName', 
		width : 200
	}, {
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'stationName', 
		width : 100
	}, {
		id : 'neName',
		header : '网元',
		dataIndex : 'neName', 
		width : 100
	},{
		id : 'neModel',
		header : '网元型号',
		dataIndex : 'neModel', 
		width : 120
	},{
		id : 'unitDesc',
		header : '槽道',
		dataIndex : 'unitDesc', 
		width : 160
	},{
		id : 'unitName',
		header : '板卡',
		dataIndex : 'unitName', 
		width : 100
	},{
		id : 'emsName',
		header : '网管',
		dataIndex : 'emsName', 
		width : 120
	}]
});
var cmPort = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : false
	}),{
		id : 'areaName',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName', 
		width : 200
	}, {
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'stationName', 
		width : 100
	}, {
		id : 'neName',
		header : '网元',
		dataIndex : 'neName', 
		width : 100
	},{
		id : 'neModel',
		header : '网元型号',
		dataIndex : 'neModel', 
		width : 140
	},{
		id : 'portNo',
		header : '端口',
		dataIndex : 'portNo', 
		width : 200
	},{
		id : 'rate',
		header : '端口类型',
		dataIndex : 'rate', 
		width : 100
	},{
		id : 'emsName',
		header : '网管',
		dataIndex : 'emsName', 
		width : 120
	}]
});

stores = [storeNe,storeUnit,storePort];
cms = [cmNe,cmUnit,cmPort];


var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : storeNe,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
//创建表格
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	cm : cmNe,
	store : storeNe,
	stripeRows : true, // 交替行效果
	loadMask : true,
	forceFit : true,
	bbar:pageTool
});

/** 
 * * 创建FusionCharts图panel
 */
var chartPanel = new Ext.Panel({
	id : 'chartPanel',
	layout:'form',
	height : chartHeight,
	width:'100%',
	border: false,
	html:'<div id="fushionChart1" style="text-align:center;margin:10px"></div>'
});


var childCenterPanel = new Ext.Panel({
	height : 410,
	border : false,
	region : 'north',
	layout : 'form',
	items : [queryPanel,chartPanel]
});

//查询按钮调用的方法
function query(){   
	var jsonString = new Array();
	if(nodes && (nodes.total> 0)){  
		for(var i=0;i<nodes.total;i++){
			var map = {
				"id" : nodes.nodes[i].id,
				"lvl" :  nodes.nodes[i].level,
				"text": nodes.nodes[i].text
			};  
			jsonString.push(map);
		} 
		var key;
		if(type==0){
			key="neModel";
		}else if(type == 1){
			key="unitName";
		}else if(type == 2){
			key="ptpType";
		}
		var jsonData =  {
				"jsonString" : Ext.encode(jsonString),
				"type" : key,
				"limit" : 200
		};
		stores[type].baseParams =jsonData;
		stores[type].load();
			
		
		Ext.Ajax.request({
		    url:'resource-statistic!getStatisticChart.action',
		    method : 'POST',
		    params: jsonData,
		    success: function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	var chart = new FusionCharts("../../../resource/FusionCharts/Charts/StackedColumn3D.swf", "chart1Id", "90%", chartHeight);        
		    	chart.setDataXML(obj.xml);  
		    	chart.render("fushionChart1");
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
	}else{
		Ext.Msg.alert("提示","请先选择"+top.FieldNameDefine.AREA_NAME+"或"+top.FieldNameDefine.STATION_NAME+"!");
	}
}
 
// Ext加载
Ext.onReady(function(){
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	var view = new Ext.Viewport({
		id:'viewport',
		layout : 'border',
		items : [childCenterPanel,gridPanel],
		renderTo : Ext.getBody()
	});
	view.show();
	type=0;
});

