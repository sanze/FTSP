//定义查询条件panel高度
var queryHeight = 30;
// 定义FusionCharts图高度
var chartHeight = (Ext.getBody().getHeight()-queryHeight)*0.6;
// 时间
var time = '';

var ems = new Ext.form.TextField({
	id : 'ems',
	fieldLabel : '网管',
	listeners: {
        focus:function(field){
        	getTree(5,field.fieldLabel,field.getPosition()[0],field.getPosition()[1]);
        	field.blur();// 赋值后，主动失去焦点，否则不能立即出发下了个获取焦点事件
        }
    }
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
			['_id','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','NATIVE_EMS_NAME','NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NO','DOMAIN',
				 'PTP_TYPE','INTERFACE_RATE','CTP_NAME','FIRST_TIME','AMOUNT','UPDATE_TIME','SERVICE_AFFECTING','CLEAR_TIME','ACK_TIME','ACK_USER','ALARM_TYPE','PROBABLE_CAUSE','IS_CLEAR']),
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
		sortable : true// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}),
	{
		header : '告警数据ID',
		dataIndex : '_id',
		width : 100,
		hidden : true
	},{
		header : '告警级别',
		dataIndex : 'PERCEIVED_SEVERITY',
		width : 100,
		renderer : function(value,meta,record){
			if(record.data["IS_CLEAR"]==2){
			if(value==1){
				return '<font color="#FF0000">紧急</font>';
			}else if(value==2){
				return '<font color="#FF8000">重要</font>';
			}else if(value==3){
				return '<font color="#FFFF00">次要</font>';
			}else if(value==4){
				return '<font color="#800000">提示</font>';
			}
			}else{
				return '<font color="#00FF00">已清除</font>';
			}

		}
	},{
		header : '告警名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100
	},{
		header : '归一化名称',
		dataIndex : 'NORMAL_CAUSE',
		width : 100
	},{
		header : '网管分组',
		dataIndex : 'EMS_GROUP_NAME',
		width : 100
	},{
		header : '网管',
		dataIndex : 'NATIVE_EMS_NAME',
		width : 100
	},{
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	},{
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 80
	},{
		header : '槽道',
		dataIndex : 'SLOT_DISPLAY_NAME',
		width : 80
	},{
		header : '板卡',
		dataIndex : 'UNIT_NAME',
		width : 80
	},{
		header : '端口',
		dataIndex : 'PORT_NO',
		width : 80
	},{
		header : '业务类型',
		dataIndex : 'DOMAIN',
		width : 80
	},{
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		width : 80
	},{
		header : '速率',
		dataIndex : 'INTERFACE_RATE',
		width : 80
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		width : 80
	},{
		header : '首次发生时间',
		dataIndex : 'FIRST_TIME',
		width : 100
	},{
		header : '频次',
		dataIndex : 'AMOUNT',
		width : 80
	},{
		header : '最近发生时间',
		dataIndex : 'UPDATE_TIME',
		width : 100
	},{
		header : '业务影响',
		dataIndex : 'SERVICE_AFFECTING',
		width : 100
	},{
		header : '清除时间',
		dataIndex : 'CLEAR_TIME',
		width : 100
	},{
		header : '确认时间',
		dataIndex : 'ACK_TIME',
		width : 100
	},{
		header : '确认者',
		dataIndex : 'ACK_USER',
		width : 80
	},{
		header : '告警类型',
		dataIndex : 'ALARM_TYPE',
		width : 80
	},{
		header : '告警标准名',
		dataIndex : 'PROBABLE_CAUSE',
		width : 100
	},{
		header : '清除状态',
		dataIndex : 'IS_CLEAR',
		width : 80,
		renderer : function(value){
			if(value==1){
				return '已清除';
			}else{
				return '未清除';
			}
		}
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
/**
 * 创建border布局的主体(center)
 */
var centerPanel = new Ext.Panel({
	layout : 'form',
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
	    url: 'report!emsFusionChart_Alarm.action',
	    method : 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'flag':1,'GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'EMSIDS':emsIds,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var xml1 = obj.xml_1;
	    	if(xml1!=null && xml1!=''){
		    	var chart= new FusionCharts("../../../resource/FusionCharts/Charts/Column3D.swf", "chart1Id", "100%", chartHeight);        
		    	chart.setDataXML(xml1);  
		    	chart.render("fushionChart1");
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

//钻取方法
function FusionChartClick(obj){
	var conditions = hexToDec(obj2str(obj));
	var object = Ext.decode(conditions);
	Ext.Ajax.request({
    url: 'report!emsFusionChart_Alarm.action',
    method : 'POST',
    params: {
    	"jsonString" : Ext.encode({'caption':object.caption,'label':object.label,'seriesName':object.seriesName,
    		'GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'EMSIDS':emsIds,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})
    },
    success: function(response) {
    	var obj = Ext.decode(response.responseText);
    	var flag = obj.flag;
    	if(flag==3 && obj.xml_1!=null && obj.xml_1!=''){
    		var chart = new FusionCharts("../../../resource/FusionCharts/Charts/Column3D.swf", "chart2Id", "100%", chartHeight);        
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
	store.load({
		params : {// 请求参数
		"jsonString" : Ext.encode({'GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'EMSIDS':emsIds,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time}),
		'start':0,
		'limit':500
	}});
	setFusion();
}

//导出
function exportInfo(){
	window.location.href= 'report!exportEmsInfo_Alarm.action?jsonString='+Ext.encode({'GROUPID':Ext.getCmp('emsGroupCombo').getValue(),'EMSIDS':emsIds,'timeType':Ext.getCmp('timeFinenessCombo').getValue(),'time':time})+"&limit=5000";
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


// Ext加载
Ext.onReady(function(){
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	var view = new Ext.Viewport({
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [titlePanel,centerPanel]
	});
	view.show();
});

