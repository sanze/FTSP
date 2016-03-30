// 定义查询条件panel高度
var queryHieght = 20;
// 定义FusionCharts图宽度
var chartWidth = 450;
//定义FusionCharts图高度
var chartHeight = 380;
/**
 * 查询条件-->区域
 */
var areaField = new Ext.form.TextField({
	id : 'areaField',
	fieldLabel : top.FieldNameDefine.AREA_NAME,
	readOnly : true,
	listeners : {
		focus : function(field){
			alertTree(field.fieldLabel,field.getPosition()[0],field.getPosition()[1]);
			field.blur();// 赋值后，主动失去焦点，否则不能立即出发下了个获取焦点事件
		}
	}
}); 

/**
 * 查询条件-->局站
 */
var stationField = new Ext.form.TextField({
	id : 'stationField',
	fieldLabel : top.FieldNameDefine.STATION_NAME,
	readOnly : true,
	listeners : {
		focus : function(field){
			alertTree(field.fieldLabel,field.getPosition()[0],field.getPosition()[1]);
			field.blur();
		}
	}
});

/**
 * 创建查询条件panel
 */
var queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : queryHieght,
	border : false,
	tbar : [top.FieldNameDefine.AREA_NAME+'：',areaField,'-',top.FieldNameDefine.STATION_NAME+'：',stationField,'-',{
		xtype : 'button',
		style : 'margin-left : 30px',
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png'
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
var chartPanel = new Ext.Panel({
	id : 'chartPanel',
	layout : 'column',
	border : false,
	items : [{
		border : false,
		columnWidth : .5,
		html:'<div id="leftChart"></div>',
	},{
		border : false,
		columnWidth : .5,
		html:'<div id="rightChart"></div>',
	}]
});

/**
 * 创建表格数据源
 */
var store = new Ext.data.Store({
	url : 'report!getResourceDetailByStation.action',
	baseParams : {
		"jsonString" : Ext.encode({'stationId':'1,2'}),
		'limit':200
	},
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, ["AREA_NAME","STATION_NAME","ROOM_NAME","GROUP_NAME","EMS_NAME","NE_NAME","NE_MODEL","SHELF_NO","SOLT_NO","UNIT_NAME","RATE","PORT_NO","PTP_TYPE","DOMAIN","HARD_WARE_VERSION","SOFT_WARE_VERSION"])
});

/**
 * 加载表格数据
 */
store.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

/**
 * 创建表格选择模型(多选、单选.....)
 */
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();

/**
 * 创建表格列模型
 */
var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : false
	}), checkboxSelectionModel,{
		id : 'AREA_NAME',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'AREA_NAME',
		width : 100
	},{
		id : 'STATION_NAME',
		header : top.FieldNameDefine.STATION_NAME+'名称',
		dataIndex : 'STATION_NAME',
		width : 100
	},{
		id : 'ROOM_NAME',
		header : '机房名称',
		dataIndex : 'ROOM_NAME',
		width : 100
	},{
		id : 'GROUP_NAME',
		header : '网管分组',
		dataIndex : 'GROUP_NAME',
		width : 100
	},{
		id : 'EMS_NAME',
		header : '网管名称',
		dataIndex : 'EMS_NAME',
		width : 100
	},{
		id : 'NE_NAME',
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	},{
		id : 'NE_MODEL',
		header : '网元型号',
		dataIndex : 'NE_MODEL',
		width : 100
	},{
		id : 'SHELF_NO',
		header : '子架',
		dataIndex : 'SHELF_NO',
		width : 100
	},{
		id : 'SOLT_NO',
		header : '槽道',
		dataIndex : 'SOLT_NO',
		width : 100
	},{
		id : 'UNIT_NAME',
		header : '板卡',
		dataIndex : 'UNIT_NAME',
		width : 100
	},{
		id : 'RATE',
		header : '速率',
		dataIndex : 'RATE',
		width : 100
	},{
		id : 'PORT_NO',
		header : '端口号',
		dataIndex : 'PORT_NO',
		width : 100
	},{
		id : 'PTP_TYPE',
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		width : 100
	},{
		id : 'DOMAIN',
		header : '业务类型',
		dataIndex : 'DOMAIN',
		width : 100
	},{
		id : 'HARD_WARE_VERSION',
		header : '硬件版本',
		dataIndex : 'HARD_WARE_VERSION',
		width : 100
	},{
		id : 'SOFT_WARE_VERSION',
		header : '软件版本',
		dataIndex : 'SOFT_WARE_VERSION',
		width : 100
	}]
});

/**
 * 创建表格分页工具栏
 */
var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 500,
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

/**
 * 创建表格
 */
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	cm : cm,
	store : store,
	stripeRows : true, 
	loadMask : true,
	selModel : checkboxSelectionModel,
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
	title : '按'+top.FieldNameDefine.STATION_NAME+'统计网元型号',
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

/**
 * 加载左侧chart的数据
 */
function getLeftChart(){
	Ext.Ajax.request({
	    url : 'report!getResourceChartByStation.action',
	    method : 'POST',
	    params : {
	    	'jsonString' : Ext.encode({'stationId':'1,2','stationName':'李家港,夏家胡同','flag':'left'})
//	    	'jsonString' : Ext.encode({'stationId':'2','stationName':'夏家胡同','flag':'left'})
	    },
	    success: function(response) {
	    	var data = Ext.decode(response.responseText);
	    	var chart = new FusionCharts("../../../resource/FusionCharts/Charts/StackedColumn3D.swf", "chart3Id", chartWidth, chartHeight);        
	    	chart.setDataXML(data.xml);  
	    	chart.render("leftChart"); 
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

/**
 * 加载右侧chart的数据
 */
function getRightChart(){
	Ext.Ajax.request({
	    url : 'report!getResourceChartByStation.action',
	    method : 'POST',
	    params : {
	    	'jsonString' : Ext.encode({'stationId':'1,2','flag':'right'})
	    },
	    success: function(response) {
	    	// 将json格式的字符串，转换成对象
	    	var data = Ext.decode(response.responseText);
	    	var chart = new FusionCharts("../../../resource/FusionCharts/Charts/StackedColumn3D.swf", "chart3Id", chartWidth, chartHeight);        
	    	chart.setDataXML(data.xml);  
	    	chart.render("rightChart"); 
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

/**
 * 弹出树状结构
 * @param title 标题
 * @param pageX	横坐标
 * @param pageY 从坐标
 */
function alertTree(title,pageX,pageY){
	var url = 'reportTree.jsp?type=area';
	var alertTreeWindow = new Ext.Window({
		id : title,
		title : title,
		width : 200,
		height : 300,
		pageX :pageX,
		pageY :pageY+20,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id="+title+"'_panel' name = "+title+"'_panel'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>",
		buttons:[{
			text:'确定',
		},{
			text:'取消',
			handler : function(){
				var win = parent.Ext.getCmp(title);
				if(win){
					win.close();
				}
			}
		}]
	});
//	var alertTreeWindow = new Ext.Window({
//		layout : 'fit',
//		id : 'alertTree',
//		title : title,
//		width : 200,
//		height : 300,
//		pageX :pageX,
//		pageY :pageY+20,
//		isTopContainer : true,
//		modal : true,
//		autoScroll : true,
//		items : {
//			id : "tree",
//            xtype : "area",
//            maxLevel : 4,
//            checkModel : "multiple"
//		},
//		buttons:[{
//			text:'确定',
//		}]
//	})
	alertTreeWindow.show();
}

//查询按钮调用的方法
function query(){
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
// Ext加载
Ext.onReady(function(){
	new Ext.Viewport({
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [titlePanel,centerPanel]
	});
	getLeftChart();
	getRightChart();
});

