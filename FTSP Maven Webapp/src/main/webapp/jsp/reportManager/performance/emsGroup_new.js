//定义查询条件panel高度
var queryHeight = 30;
// 定义FusionCharts图高度
var chartHeight =(Ext.getBody().getHeight()-queryHeight)*0.6;
// 时间
var time = '';
//查询条件tab
var queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : queryHeight,
	border : false,
	tbar : ['-','网管分组：',emsGroupCombo,'-',
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
 * 创建FusionCharts图panel
 */
var chartPanel_1 = new Ext.Panel({
	id : 'chartPanel_1',
	height : chartHeight,
	layout:'form',
	border: false,
	width:'90%',
	html:'<div id="fushionChart1" style="text-align:center;margin:10px"></div>'
});

/**
 * 创建FusionCharts图panel
 */
var chartPanel_2 = new Ext.Panel({
	id : 'chartPanel_2',
	height : chartHeight,
	border: false,
	hidden:true,
	layout:'form',
	width:'90%',
	html:'<div id="fushionChart2" style="text-align:center;margin:10px"></div>'
});

/**
 * 创建FusionCharts图panel
 */
var chartPanel_3= new Ext.Panel({
	id : 'chartPanel_3',
	height : chartHeight,
	border: false,
	hidden:true,
	layout:'form',
	width:'90%',
	html:'<div id="fushionChart3" style="text-align:center;margin:10px"></div>'
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


var clikeGroupName='';
var gName='';
//钻取方法
function FusionChartClick(obj){
	var conditions = hexToDec(obj2str(obj));
	var object = Ext.decode(conditions);
	if(object.level=='one'){//点击第一层时清空第三层数据
		clikeGroupName=object.emsId;
		gName=object.label;
		chartPanel_3.hide();
	}else if(object.level=='two'){
		clikeGroupName=object.emsId;
	}else if(object.level=='three'){
		return;
	}
	
	Ext.Ajax.request({
	    url: 'report!fusionChart_Performance.action',
	    method : 'POST',
	    params: {
	    	"jsonString" : Ext.encode({'level':object.level,'label':object.label,
	    		'group_name':clikeGroupName,'gName':gName,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
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

function setFusion(){
	Ext.Ajax.request({
	    url: 'report!fusionChart_Performance.action',
	    method : 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'level':'','GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
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
		id:'viewPort',
		//autoScroll:false,
		//bodyStyle:'overflow-y:auto;overflow-x:hidden',
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [titlePanel,centerPanel]
	});
	view.show();
});

