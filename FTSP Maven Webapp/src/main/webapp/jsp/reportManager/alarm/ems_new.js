//定义查询条件panel高度
var queryHeight = 30;
// 定义FusionCharts图高度
var chartHeight = (Ext.getBody().getHeight()-queryHeight)*0.6;
// 时间
var time = '';
//查询条件tab
var queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : queryHeight,
	border : false,
	tbar : ['-','网管分组：',emsGroupCombo,'-',
	        '网管：',emsTextField,'-',
	        '时间粒度：',timeFinenessCombo,'-',
	        {text:'年度：',xtype :'label',id:'yearText'},queryYear,
	        {text:'月份：',xtype :'label',id:'monthText',hidden : true},queryMonth,
	        {text:'日期：',xtype :'label',id:'dayText',hidden : true},queryDay,'-',
    {
		xtype : 'button',
		text : '查询',
		icon : qpath+'/resource/images/btnImages/search.png',
		handler : queryByCondition
	}]
	
});

/**

 * 创建FusionCharts图panel  第一层
 */
var chartPanel_1 = new Ext.Panel({
	id : 'chartPanel_1',
	layout:'form',
	border: false,
	width:'100%',
	height : chartHeight,
	html:'<div id="fushionChart1" style="text-align:center;margin:10px"></div>'
});

/**
 * 创建FusionCharts图panel  第二层
 */
var chartPanel_2 = new Ext.Panel({
	id : 'chartPanel_2',
	border: false,
	hidden:true,
	width:'100%',
	height : chartHeight,
	layout:'form',
	html:'<div id="fushionChart2" style="text-align:center;margin:10px"></div>'
});


/**
 * 创建FusionCharts图panel  第三层
 */
var chartPanel_3 = new Ext.Panel({
	id : 'chartPanel_3',
	border: false,
	hidden:true,
	width:'100%',
	height : chartHeight,
	layout:'form',
	html:'<div id="fushionChart3" style="text-align:center;margin:10px"></div>'
});

/**
 * 创建表格panel
 */
var store = new Ext.data.Store({
	url : 'report!getEmsInfo_Alarm.action',// 数据请求地址
	baseParams : {// 请求参数
		"jsonString" : Ext.encode({'GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'EMSIDS':emsIds,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time}),
		'limit':500
	},
	reader : new Ext.data.JsonReader(
			{totalProperty : 'total',root : "rows"},	
			['EMS_GROUP_NAME','EMS_NAME','count']),
    listeners:{
 		beforeload:function(){
 			store.baseParams={// 请求参数
 					"jsonString" : Ext.encode({'GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'EMSIDS':emsIds,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time}),
 					'limit':500
 				}
 		}
 	 }
});

// 创建表格列模型
var cm = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true,// 表示所有列可以排序
		align:'center'
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}),
	{
		header : '网管分组',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	},{
		header : '网管',
		dataIndex : 'EMS_NAME',
		width : 100
	},{
		header : '网管数目',
		dataIndex : 'count',
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
	region : 'center',
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	forceFit : true,
	height : Ext.getBody().getHeight()-queryHeight-chartHeight,
	bbar : pageTool
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
 * 创建center的north(包含查询条件queryPanel和FusionCharts图chartPanel)
 */
var centerPanel = new Ext.Panel({
	region : 'center',
	autoScroll : true,
	bodyStyle:'overflow-x:hidden;',
	height : Ext.getBody().getHeight()-queryHeight,
	layout : 'form',
	border:false,
	items : [chartPanel_1,chartPanel_2,chartPanel_3]
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


var clikeEmsName='';
var eName='';
//钻取方法
function FusionChartClick(obj){
	var conditions = hexToDec(obj2str(obj));
	var object = Ext.decode(conditions);
	if(object.level=='one'){//点击第一层时清空第三层数据
		clikeEmsName=object.emsId;
		eName=object.label;
		chartPanel_3.hide();
	}else if(object.level=='two'){
		clikeEmsName=object.emsId;
	}else if(object.level=='three'){
		return;
	}
	Ext.Ajax.request({
    url: 'report!emsFusionChart_Alarm.action',
    method : 'POST',
    params: {
    	"jsonString" : Ext.encode({'level':object.level,'label':object.label,
    		'GROUPID' : Ext.getCmp('emsGroupCombo').getValue(),
    		'EMS_NAME':clikeEmsName,'eName':eName,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
    },
    success: function(response) {
    	var o= Ext.decode(response.responseText);
    	if(object.level=='one'){
    		var chart=null;
    		if(Ext.getCmp('timeFinenessCombo').getValue()=='day'){
    			chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/Pie3D.swf", "chart2Id", "90%", chartHeight);  
    		}else{
    			chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/StackedColumn3D.swf", "chart2Id", "90%", chartHeight);  
    		}
    		chart.setDataXML(o.xml_1);  
    		chart.render("fushionChart2");
    		Ext.get('fushionChart1').setTop(100);
    		chartPanel_2.show();
    	}else if(object.level=='two'){
    		var chart=null;
    		if(Ext.getCmp('timeFinenessCombo').getValue()=='year'){
    		   chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/StackedColumn3D.swf", "chart3Id", "90%", chartHeight);        
    		}else if(Ext.getCmp('timeFinenessCombo').getValue()=='month'){
    		   chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/Pie3D.swf", "chart3Id", "90%", chartHeight);    
    		}else if(Ext.getCmp('timeFinenessCombo').getValue()=='day'){
    			return;
    		}
    		chart.setDataXML(o.xml_1);  
    		chart.render("fushionChart3");
    		chartPanel_3.show();
    	}
    	var d = centerPanel.body.dom;
		d.scrollTop = d.scrollHeight - d.offsetHeight;
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
	chartPanel_2.hide();
	chartPanel_3.hide();
	setFusion();
}


//生成第一层图表
function setFusion(){
	Ext.Ajax.request({
	    url: 'report!emsFusionChart_Alarm.action',
	    method : 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'level':'','GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'EMSIDS':emsIds,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var xml1 = obj.xml_1;
	    	if(xml1!=null && xml1!=''){
		    	var chart1 = new FusionCharts(qpath+"/resource/FusionCharts/Charts/Pie3D.swf", "chart1Id", "90%", chartHeight);        
		    	chart1.setDataXML(xml1);  
		    	chart1.render("fushionChart1");
	    	}
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


// Ext加载
Ext.onReady(function(){
	var view = new Ext.Viewport({
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [titlePanel,centerPanel]
	});
	view.show();
});

