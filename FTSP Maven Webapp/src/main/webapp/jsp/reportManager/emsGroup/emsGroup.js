// 定义查询条件panel高度
var queryHieght = 20;
//定义FusionCharts图高度
var chartHeight = 380;
/**
 * 查询条件-->区域
 */
var areaField = new Ext.form.TextField({
	id : 'areaField',
	fieldLabel : '网管分组',
//	readOnly : true,
//	listeners : {
//		focus : function(field){
//			field.setValue('haha');
//			field.blur();// 赋值后，主动失去焦点，否则不能立即出发下了个获取焦点事件
//		}
//	}
}); 
/**
 * 创建查询条件panel
 */
var queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : queryHieght,
	border : false,
	tbar : ['&nbsp;&nbsp;网管分组：',areaField,'-',{
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
//设置fusionChart
var chartPanel = new Ext.Panel({
	id : 'chartPanel',
	html:'<div id="fushionChart1"></div>',
});
/**
 * 创建表格panel
 */
// 创建表格数据源
var store = new Ext.data.Store({
	url : 'report!getEmsGroupInfo.action',// 数据请求地址
	
	baseParams : {// 请求参数
		"jsonString" : Ext.encode({'PARA' : areaField.getValue()}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({// 
				totalProperty : 'total',
				root : "rows"
			},  ["area_name", "station_name", "room_name", "group_name",  "ems_name", 
			     "net_name", "product_name", "name", "hard_ware_version", "soft_ware_version"])
});
//store.load();
// 创建表格选择模型(多选、单选.....)
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
//	singleSelect ：true,// 表示只能单选，默认false,
//	sortable : true,//表示选择框列可以排序，默认fasle
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
		id : 'group_name',
		header : '网管分组',
		dataIndex : 'group_name',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'ems_name',
		header : '网管名称',
		dataIndex : 'ems_name',
		hidden : false,// hidden colunm
		width : 200
	}, {
		id : 'net_name',
		header : '网元名称',
		dataIndex : 'net_name',
		hidden : false,// hidden colunm
		width : 200
	}, {
		id : 'area_name',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'area_name',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'station_name',
		header : '网站名称',
		dataIndex : 'station_name',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'room_name',
		header : '机房名称',
		dataIndex : 'room_name',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'product_name',
		header : '网元型号',
		dataIndex : 'product_name',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'name',
		header : '板卡',
		dataIndex : 'name',
		hidden : false,// hidden colunm
		width : 300,
	}, {
		id : 'hard_ware_version',
		header : '硬件版本',
		dataIndex : 'hard_ware_version',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'soft_ware_version',
		header : '软件版本',
		dataIndex : 'soft_ware_version',
		hidden : false,// hidden colunm
		width : 100,
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
 * 创建center的north(包含查询条件queryPanel和FusionCharts图chartPanel)
 */
var childCenterPanel = new Ext.Panel({
	height : queryHieght + chartHeight,
	border : false,
	region : 'north',
	layout : 'form',
	items : [queryPanel,chartPanel]
});
/**
 * 创建border布局的头部(north)
 */
var titlePanel = new Ext.Panel({
	title : '按网管分组统计',
	region : 'north'
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

function setFusion(){
	
	Ext.Ajax.request({
	    url: 'report!fusionChart.action',
	    method : 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'PARA' : areaField.getValue()})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var xml = "<graph showNames='1' decimalPrecision='0'>" + obj.xml + "</graph>";
	    	var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Column3D.swf", "chart3Id", "100%", chartHeight);        
	    	chart.setDataXML(xml);  
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
}

//查询按钮调用的方法
function query(){
//	alert(areaField.getValue());
	store.baseParams = {// 请求参数
		"jsonString" : Ext.encode({'PARA' : areaField.getValue()}),
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
function drill(data){
//	alert(data);
	store.baseParams = {"jsonString" : Ext.encode({'ID':data}),'limit':500};
	store.reload();
 }

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

