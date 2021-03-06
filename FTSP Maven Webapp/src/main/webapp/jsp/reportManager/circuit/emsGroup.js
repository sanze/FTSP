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
	anchor : '50%',
	layout:'form',
	html:'<div id="fushionChart1"></div>',
});
/**
 * 创建FusionCharts图panel
 */
//设置fusionChart
var chartPanel2 = new Ext.Panel({
	id : 'chartPanel2',
	anchor : '50%',
	layout:'form',
	html:'<div id="fushionChart2"></div>',
});
/**
 * 创建表格panel
 */
// 创建表格数据源
var store = new Ext.data.Store({
	url : 'report!getEmsGroupInfo_Circuit.action',// 数据请求地址
	
	baseParams : {// 请求参数
		"jsonString" : Ext.encode({'PARA' : areaField.getValue()}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({// 
				totalProperty : 'total',
				root : "rows"
			},  ["CIR_NO", "SOURCE_NO", "A_NET", "Z_NET",  "BUSI_TYPE", "A_CTP", "Z_CTP", "A_END_RATE", "Z_END_RATE", 
			     "SELECT_TYPE", "CLIENT_NAME", "USED_FOR", "A_END_USER_NAME", "Z_END_USER_NAME", "A_EMS", "Z_EMS", "A_GROUP", "Z_GROUP"])
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
		id : 'CIR_NO',
		header : '电路编号',
		dataIndex : 'CIR_NO',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'SOURCE_NO',
		header : '资源编号',
		dataIndex : 'SOURCE_NO',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'BUSI_TYPE',
		header : '业务类型',
		dataIndex : 'BUSI_TYPE',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'A_NET',
		header : 'A端网元',
		dataIndex : 'A_NET',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'Z_NET',
		header : 'Z端网元',
		dataIndex : 'Z_NET',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'A_CTP',
		header : 'A端时隙',
		dataIndex : 'A_CTP',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'Z_CTP',
		header : 'Z端时隙',
		dataIndex : 'Z_CTP',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'A_END_RATE',
		header : 'A端速率',
		dataIndex : 'A_END_RATE',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'Z_END_RATE',
		header : 'Z端速率',
		dataIndex : 'Z_END_RATE',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'SELECT_TYPE',
		header : '电路类别',
		dataIndex : 'SELECT_TYPE',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'CLIENT_NAME',
		header : '客户名称',
		dataIndex : 'CLIENT_NAME',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'USED_FOR',
		header : '用途',
		dataIndex : 'USED_FOR',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'A_END_USER_NAME',
		header : 'A端用户',
		dataIndex : 'A_END_USER_NAME',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'Z_END_USER_NAME',
		header : 'Z端用户',
		dataIndex : 'Z_END_USER_NAME',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'A_EMS',
		header : 'A端所属网管',
		dataIndex : 'A_EMS',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'Z_EMS',
		header : 'Z端所属网管',
		dataIndex : 'Z_EMS',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'A_GROUP',
		header : 'A端所属网管分组',
		dataIndex : 'A_GROUP',
		hidden : false,// hidden colunm
		width : 100,
	}, {
		id : 'Z_GROUP',
		header : 'Z端所属网管分组',
		dataIndex : 'Z_GROUP',
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
	height : chartHeight+10,
	autoScroll : true,
	border : false,
	region : 'north',
	layout : 'form',
	items : [chartPanel,chartPanel2]
});
/**
 * 创建border布局的头部(north)
 */
var titlePanel = new Ext.Panel({
	height : queryHieght+30,
	title : '按网管分组统计',
	region : 'north',
	items : queryPanel
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
	    url: 'report!emsGroupFusionChart_Circuit.action',
	    method : 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'PARA' : areaField.getValue()})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Pie3D.swf", "chart3Id", "100%", chartHeight);        
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
//function drill(data){
////	alert(data);
//	store.baseParams = {"jsonString" : Ext.encode({'ID':data}),'limit':500};
//	store.reload();
// }

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
//钻取方法
function FusionChartClick(obj){
	
	var conditions = hexToDec(obj2str(obj));
	var object = Ext.decode(conditions);
//	alert(1);
	Ext.Ajax.request({
	    url: 'report!emsGroupFusionChart_Circuit.action',
	    method : 'POST',
	    params: {
	    	"jsonString" : Ext.encode({'caption':object.caption,'label':object.label})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
    		var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Pie3D.swf", "chart3Id", "100%", chartHeight);        
    		chart.setDataXML(obj.xml);  
    		chart.render("fushionChart2");
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

