//定义查询条件panel高度
var queryHeight = 30;
// 定义FusionCharts图高度
var chartHeight = (Ext.getBody().getHeight() - queryHeight) * 0.6;
// 时间
var time = '';


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
 * 创建border布局的主体(center)
 */
var centerPanel = new Ext.Panel({
	layout : 'form',
	region : 'center',
	border : false,
	autoScroll:true,
	items:[chartPanel_1,chartPanel_2,chartPanel_3],
	tbar : ['-','网管分组：', emsGroupCombo, '-','网管：', emsTextField, '-',
		'时间粒度：',
	 timeFinenessCombo, '-', {
		text : '年度：',
		xtype :'label',
		id : 'yearText'
	}, queryYear, '', {
		text : '月份：',
		xtype :'label',
		id : 'monthText',
		hidden : true
	}, queryMonth, '', {
		text : '日期：',
		xtype :'label',
		id : 'dayText',
		hidden : true
	}, queryDay, '-', {
		xtype : 'button',
		text : '查询',
		icon : qpath + '/resource/images/btnImages/search.png',
		handler : setFusion
	} ]
});

var hexToDec = function(str) {
	str = str.replace(/\\/g, "%");
	return unescape(str);
}
/* object to string */
function obj2str(o) {
	var r = [], i, j = 0, len;
	if (o == null) {
		return o;
	}
	if (typeof o == 'string') {
		return '"' + o + '"';
	}
	if (typeof o == 'object') {
		if (!o.sort) {
			r[j++] = '{';
			for (i in o) {
				r[j++] = '"';
				r[j++] = i;
				r[j++] = '":';
				r[j++] = obj2str(o[i]);
				r[j++] = ',';
			}
			// 可能的空对象
			// r[r[j-1] == '{' ? j:j-1]='}';
			r[j - 1] = '}';
		} else {
			r[j++] = '[';
			for (i = 0, len = o.length; i < len; ++i) {
				r[j++] = obj2str(o[i]);
				r[j++] = ',';
			}
			// 可能的空数组
			r[len == 0 ? j : j - 1] = ']';
		}
		return r.join('');
	}
	return o.toString();
}

function setFusion() {
	setQueryTime();
	Ext.Ajax.request({
		url : 'report!emsFusionChart_Performance.action',
		method : 'POST',
		params : {
				'condMap.firstGraph' : 1,
				'condMap.GROUPID' : Ext.getCmp('emsGroupCombo').getValue(),
				'condMap.EMSIDS' : emsIds,
				'condMap.timeType' : Ext.getCmp('timeFinenessCombo').getValue(),
				'condMap.time' : time
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			var xml1 = obj.xml_1;
			if (xml1 != null && xml1 != '') {
				var chart = new FusionCharts(
						"../../../resource/FusionCharts/Charts/Pie3D.swf",
						"chart1Id", "90%", chartHeight);
				chart.setDataXML(xml1);
				chart.render("fushionChart1");
			}
			chartPanel_2.hide();
			chartPanel_3.hide();
			FusionChartClick.timeType = Ext.getCmp('timeFinenessCombo').getValue();
			FusionChartClick.time = time;
		},
		error : function(response) {
			chartPanel_1.hide();
			chartPanel_2.hide();
			chartPanel_3.hide();
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			chartPanel_1.hide();
			chartPanel_2.hide();
			chartPanel_3.hide();
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

// 钻取方法
function FusionChartClick(obj) {
	
	var conditions = hexToDec(obj2str(obj));
	var object = Ext.decode(conditions);
//	alert(object);
//	return;
	var params = {
			"condMap.caption":object.caption,
			"condMap.emsId":object.emsId,
			"condMap.label":object.label,
			"condMap.level":object.level,
			'condMap.timeType' : FusionChartClick.timeType,
			'condMap.time' : FusionChartClick.time
	}
	Ext.Ajax.request({
		url : 'report!emsFusionChart_Performance.action',
		method : 'POST',
		params : params,
		success : function(response) {
			var o= Ext.decode(response.responseText);
			if(object.level=='one'){
	    		var chart=null;
	    		if(FusionChartClick.timeType=='day'){
	    			chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/Pie3D.swf", "chart2Id", "90%", chartHeight);  
	    		}else{
	    			chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/StackedColumn3D.swf", "chart2Id", "90%", chartHeight);  
	    		}
	    		chart.setDataXML(o.xml_1);  
	    		chart.render("fushionChart2");
    			chartPanel_2.show();
    			chartPanel_3.hide();
	    	}else if(object.level=='two'){
	    		var chart=null;
	    		if(FusionChartClick.timeType=='year'){
	    		   chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/StackedColumn3D.swf", "chart3Id", "90%", chartHeight);        
	    		}else if(FusionChartClick.timeType=='month'){
	    		   chart = new FusionCharts(qpath+"/resource/FusionCharts/Charts/Pie3D.swf", "chart3Id", "90%", chartHeight);    
	    		}else if(FusionChartClick.timeType=='day'){
	    			return;
	    		}
	    		chart.setDataXML(o.xml_1);  
	    		chart.render("fushionChart3");
	    		chartPanel_3.show();
	    	}
			var d = centerPanel.body.dom;
			d.scrollTop = d.scrollHeight;
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseext);
		}
	});

}

// Ext加载
Ext.onReady(function() {
	var view = new Ext.Viewport({
		layout : 'border',
		items : [ centerPanel ]
	});
	view.show();
});
