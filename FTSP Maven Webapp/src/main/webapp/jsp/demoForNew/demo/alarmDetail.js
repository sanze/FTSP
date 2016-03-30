
var centerPanel = new Ext.FormPanel({
	title : '告警详情',
	region : 'center',
	layout : 'form',
	border : false,
	items : [{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">区域</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">南京市/鼓楼区</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">局站</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">鼓楼老局</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">机房</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">17F综合机房</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">包机人</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">秦策</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">定位信息</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">5-12?????OTU X ???-12-55TQX-204(ClientLP4/ClientLP4)-CLIENT:1:1</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">位置</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">近段</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">方向</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">接受</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">告警描述</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
		    tag:'span',
		    html:'<font size="4">接受线路侧信号丢失</font>'
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:20px;',
		items : [{
			border : false,
			width : 100,
		    tag:'span',
		    html:'<font size="4">处理建议</font>'
		},{
			border : false,
			style : 'margin-left:50px;',
			items : [{
				border : false,
			    tag:'span',
			    html:'<font size="4">(1)本站单板光接口处未连接尾纤</font>'
			},{
				border : false,
			    tag:'span',
			    html:'<font size="4">(2)对端站单板激光器关闭</font>'
			},{
				border : false,
			    tag:'span',
			    html:'<font size="4">(3)传输线路断纤</font>'
			},{
				border : false,
			    tag:'span',
			    html:'<font size="4">(4)传输线路衰耗过大</font>'
			},{
				border : false,
			    tag:'span',
			    html:'<font size="4">(5)对端站单板发送部分故障</font>'
			},{
				border : false,
			    tag:'span',
			    html:'<font size="4">(6)本站接受部分故障</font>'
			}]
		  
		}]
	}],
	buttons : [{
		text : '确定',
	}]
});


// 页面初始化
Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
//		loadMask : true,//定义可以在加载数据前显示提示信息
		layout : 'border',
		items : [centerPanel]
	});
});