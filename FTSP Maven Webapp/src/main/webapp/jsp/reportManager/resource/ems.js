//定义查询条件panel高度
var queryHeight = 30;
// 定义FusionCharts图高度
var chartHeight = (Ext.getBody().getHeight()-queryHeight)*0.6;
// 时间
var time = '';

/**
 * 创建查询条件panel
 */
queryPanel = new Ext.Panel({
	id : 'queryPanelGroup',
	height : queryHeight,
	region : 'north',
	border : false,
	tbar : ['-','网管分组：',emsGroupCombo,'-','网管：',emsTextField,'-',
    {
		xtype : 'button',
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		handler : query
	},'-',{
		xtype : 'button',
		text : '导出',
		icon : '../../../resource/images/btnImages/export.png',
		handler : exportByCondition
	}]
	
});
/**
 * 创建FusionCharts图panel
 */
//设置fusionChart
var chartPanel = new Ext.Panel({
	id : 'chartPanel',
	layout:'form',
	height : chartHeight,
	width:'100%',
	border: false,
	html:'<div id="fushionChart1" style="text-align:center;margin:10px"></div>'
});
/**
 * 创建表格panel
 */
// 创建表格数据源
var store = new Ext.data.Store({
	url : 'report!getEmsInfo_Resource.action',// 数据请求地址
	baseParams : {// 请求参数
		"jsonString" : Ext.encode({'PARA1' : Ext.getCmp('emsGroupCombo').getValue(),'PARA2' : emsIds}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
	},  ["area_name", "station_name", "room_name", "group_name",  "display_name", 
			     "net_name", "product_name", "name", "hard_ware_version", "soft_ware_version"])
});


store.addListener({
	beforeload:function(store,records,options){
		var json={};
		json.jsonString=Ext.encode({'PARA1' : Ext.getCmp('emsGroupCombo').getValue(),'PARA2' : emsIds});
		store.baseParams = json;
	}
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
	}),{
		id : 'group_name',
		header : '网管分组',
		dataIndex : 'group_name',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'display_name',
		header : '网管',
		dataIndex : 'display_name',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'net_name',
		header : '网元',
		dataIndex : 'net_name',
		hidden : false,// hidden colunm
		width : 100
	},{
		id : 'product_name',
		header : '网元型号',
		dataIndex : 'product_name',
		hidden : false,// hidden colunm
		width : 80
	},{
		id : 'area_name',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'area_name',
		hidden : false,// hidden colunm
		width : 80
	}, {
		id : 'station_name',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'station_name',
		hidden : false,// hidden colunm
		width : 100
	}, {
		id : 'room_name',
		header : '机房',
		dataIndex : 'room_name',
		hidden : false,// hidden colunm
		width : 100
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
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	forceFit : true,
	height : Ext.getBody().getHeight()-queryHeight-chartHeight,
	bbar : pageTool
});


/**
 * 创建border布局的主体(center)
 */
var centerPanel = new Ext.Panel({
	region : 'center',
	layout : 'form',
	border : false,
	items : [chartPanel,gridPanel]
});


function setFusion(){
	Ext.Ajax.request({
	    url: 'report!emsFusionChart_Resource.action',
	    method : 'POST',
	    params: {
	    	"jsonString" : Ext.encode({'PARA1' : Ext.getCmp('emsGroupCombo').getValue(),'PARA2' : emsIds})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Column3D.swf", "chart1Id", "90%", chartHeight);        
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
	store.load({
	params:{"jsonString" : Ext.encode({'PARA1' : Ext.getCmp('emsGroupCombo').getValue(),'PARA2' : emsIds}),
		'strat':0,
		'limit':500
	}});
    setFusion();
}

//导出
function exportInfo(){
	window.location.href= 'report!exportEmsInfo_Resource.action?jsonString='+Ext.encode({'PARA1' : Ext.getCmp('emsGroupCombo').getValue(),'PARA2' : emsIds})+"&limit=5000";
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
//钻取方法
function FusionChartClick(obj){
//	var conditions = hexToDec(obj2str(obj));
//	var object = Ext.decode(conditions);
//	store.baseParams = {"jsonString" : Ext.encode({'ems_name':object.label}),'limit':500};
//	store.reload();
}

// Ext加载
Ext.onReady(function(){
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	var view = new Ext.Viewport({
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [queryPanel,centerPanel]
	});
	view.show();
});

