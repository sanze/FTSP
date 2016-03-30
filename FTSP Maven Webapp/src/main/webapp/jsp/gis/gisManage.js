Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
var area = "";
//区域树    
var areaTree = new Ext.form.TextField({
	id : 'areaField', 
	readOnly : true,
	emptyText:'选择区域',
	width: 150,
	listeners : {
		'focus' : function(field){ 
			getTree(this,2);
		}
	}
}); 

var radioGroup = new Ext.form.RadioGroup({
	height:25,
	items:[{
		layout: 'column',
		items: [
		        {name:"etype",inputValue:"0",width:80,boxLabel:"按资源",checked:true},
		        {name:"etype",inputValue:"1",width:90,boxLabel:"按传输系统"}
		        ]
	}],
	listeners:{ 
		"change":function(radio, newV, oldV, e){
			if(newV.inputValue == '1'){
				if(areaTree.getValue() == ""){
					Ext.Msg.alert("提示：","请先选择区域！");
				}else{
					sys_combo.show();
				}
			}else{
				sys_combo.setValue(null);
				sys_combo.hide();
				// 切换到资源显示方式后刷新当前地图显示
				gisMap.window.reloadGisData();
			}
		}
   }
});

var sys_combo_store = new Ext.data.Store({
	url: 'gis!getTransSystems.action', 
	reader: new Ext.data.JsonReader({
		totalProperty: 'total',
		root : "data"
	},[
	   "RESOURCE_TRANS_SYS_ID","SYS_NAME"
	   ])
});

var sys_combo = new Ext.form.ComboBox({
    store: sys_combo_store,
    mode:"local",
    editable:false,
    triggerAction: 'all',
    emptyText:'传输系统',
	width:200,
	valueField:'RESOURCE_TRANS_SYS_ID',
	displayField:'SYS_NAME',
	listeners:{ 
		'select' : function(combo,record,index){
			gisMap.window.reloadGisData();
		}
   }
});

var gis_toolbar = new Ext.Toolbar({
	id:'gis_toolbar',
	height:35,
	style:"padding:7px 0 0 0;",
	items: ["区域：",areaTree,"&nbsp&nbsp","显示方式：",radioGroup,sys_combo, '->', 
     {
		text : '查询',
		icon : 'resource/images/btnImages/search.png',
		handler : function(){
			if(areaTree.getValue() == null)
				return;
			radioGroup.setValue(0);
			getSysComboData();
			gisMap.window.refreshWithCity(areaTree.getValue());
		}
	},"&nbsp&nbsp",
	{
		text : '刷新',
		icon : 'resource/images/btnImages/refresh.png',
		handler : function(){
			//gisMap.window.location.reload();
			gisMap.window.refresh();
		}
	}]
});

function getSysComboData(){
	sys_combo_store.load({
		params : {"jsonString":Ext.encode({"area":areaTree.areaId})},
		callback : function(r, o, s){
			if(!s){
				Ext.Msg.alert("提示：","查询传输系统失败！");
			}
		}
	});
}

var northPanel = new Ext.form.FormPanel({
	id : "northPanel",
	region:'north',
//	title:'north',
	height:35,
	items:[gis_toolbar]
});

function formatOfHtml(text){
	return '<font face="黑体" color="#2F4F4F" size="2"><b>'+text+'</b></font>';
}

function makeLineDataOfLegend(lineHeight, imageName, imageHeight, description) {
	var prefixOfImageFilePath = "resource/images/GisImages/";
	var suffixOfImageFile = ".png";
	var filePath = prefixOfImageFilePath + imageName + suffixOfImageFile;
	return '<tr><td height="'+ lineHeight + 'px" align="center"><img src="'+ filePath + 
		   '" height="'+imageHeight+'px"/></td>' + '<td height="'+ lineHeight + 'px">' +
		   formatOfHtml(description) + '</td></tr>';
}

var rowHeightOfLegend = 22;
var imgHeightOfLegend = 17;
var map_guide_resource = '<table>'+
	makeLineDataOfLegend(rowHeightOfLegend, 'station_diaoddl_na', imgHeightOfLegend, '：调度大楼') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_weibz_na', imgHeightOfLegend, '：微波站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_glfz_na', imgHeightOfLegend, '：光缆分支') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_shengcjd_na', imgHeightOfLegend, '：生产基地') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_gongdyys_na', imgHeightOfLegend, '：供电营业所') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_qianyz_na', imgHeightOfLegend, '：牵引站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_huodc_na', imgHeightOfLegend, '：火电厂') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_shuidc_na', imgHeightOfLegend, '：水电厂') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_chousxnz_na', imgHeightOfLegend, '：抽水蓄能站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_hedc_na', imgHeightOfLegend, '：核电厂') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_500bdz_na', imgHeightOfLegend, '：500Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_220bdz_na', imgHeightOfLegend, '：220Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_110bdz_na', imgHeightOfLegend, '：110Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_35bdz_na', imgHeightOfLegend, '：35Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_unknown_na', imgHeightOfLegend, '：未知类型局站') +
	'<tr><td height="22px"><hr width=30px size=1 color="green"></td>'+
	'<td height="22px">'+formatOfHtml("：光缆段")+'</td></tr>' +
	makeLineDataOfLegend(rowHeightOfLegend, 'breakPoint', imgHeightOfLegend, '：光缆中断点') +
	'</table>';

var map_guide_alarm = '<table>'+
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=16px size=15 color="#FFFFFF"></td>' +
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：正常")+'</td></tr>' +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=16px size=15 color="red"></td>' +
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：紧急告警")+'</td></tr>' +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=16px size=15 color="orange"></td>' +
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：重要告警")+'</td></tr>' +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=16px size=15 color="yellow"></td>' +
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：次要告警")+'</td></tr>' +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=16px size=15 color="#B97A57"></td>' +
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：提示告警")+'</td></tr>' +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=20px size=1 color="green"></td>' +
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：线路正常")+'</td></tr>' +	
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=20px size=1 color="red"></td>' +
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：线路告警")+'</td></tr>' +
	makeLineDataOfLegend(rowHeightOfLegend, 'breakPoint', imgHeightOfLegend, '：光缆中断点') +
	'</table>';
	
var map_guide_overlay = '<table>'+
	makeLineDataOfLegend(rowHeightOfLegend, 'station_diaoddl_na', imgHeightOfLegend, '：调度大楼') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_weibz_na', imgHeightOfLegend, '：微波站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_glfz_na', imgHeightOfLegend, '：光缆分支') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_shengcjd_na', imgHeightOfLegend, '：生产基地') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_gongdyys_na', imgHeightOfLegend, '：供电营业所') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_qianyz_na', imgHeightOfLegend, '：牵引站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_huodc_na', imgHeightOfLegend, '：火电厂') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_shuidc_na', imgHeightOfLegend, '：水电厂') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_chousxnz_na', imgHeightOfLegend, '：抽水蓄能站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_hedc_na', imgHeightOfLegend, '：核电厂') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_500bdz_na', imgHeightOfLegend, '：500Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_220bdz_na', imgHeightOfLegend, '：220Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_110bdz_na', imgHeightOfLegend, '：110Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_35bdz_na', imgHeightOfLegend, '：35Kv变电站') +
	makeLineDataOfLegend(rowHeightOfLegend, 'station_unknown_na', imgHeightOfLegend, '：未知类型局站') +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=30px size=1 color="#8968CD"></td>'+
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：全部测试覆盖")+'</td></tr>' +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=30px size=1 color="#87CEFA"></td>'+
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：部分测试覆盖")+'</td></tr>' +
	'<tr><td height="' + rowHeightOfLegend + 'px"><hr width=30px size=1 color="#606060"></td>'+
	'<td height="' + rowHeightOfLegend + 'px">'+formatOfHtml("：无测试覆盖")+'</td></tr>' +
	makeLineDataOfLegend(rowHeightOfLegend, 'breakPoint', imgHeightOfLegend, '：光缆中断点') +
	'</table>';
	
var guidePanel = new Ext.Panel({
	id: 'guidePanel',
	html:map_guide_resource
});

var fieldPanel = new  Ext.form.FieldSet({
	title:'<font size="2">显示方式</font>',
	style:"margin-top:10px;",
	items:[
	       {
	    	   xtype:'panel',
	    	   items:[{
	    	        xtype: 'radio',
	    	        boxLabel: formatOfHtml("&nbsp静态资源"),
	    	        name: 'type',
	    	        inputValue: '0',
	    	        width:90,
	    	        height:25,
	    	        checked:true,
	    	        listeners:{
	    	        	'check':function(o,checked){
	    	        		if(checked){
	    	        			gisMap.window.displayDataByStrategy(o.inputValue);
	    	        			Ext.getCmp('guidePanel').body.update(map_guide_resource);
	    	        		}
	    	        	}
	    	        }
	    	    },
	    	    {
	    	        xtype: 'radio',
	    	        boxLabel: formatOfHtml("&nbsp显示告警"),
	    	        name: 'type',
	    	        height:25,
	    	        inputValue: '1',
	    	        listeners:{
	    	        	'check':function(o,checked){
	    	        		if(checked){
	    	        			gisMap.window.displayDataByStrategy(o.inputValue);
	    	        			Ext.getCmp('guidePanel').body.update(map_guide_alarm);
	    	        		}
	    	        	}
	    	        }
	    	    },
	    	    {
	    	        xtype: 'radio',
	    	        boxLabel: formatOfHtml("&nbsp测试路由覆盖"),
	    	        name: 'type',
	    	        height:25,
	    	        inputValue: '2',
	    	        listeners:{
	    	        	'check':function(o,checked){
	    	        		if(checked){
	    	        			gisMap.window.displayDataByStrategy(o.inputValue);
	    	        			Ext.getCmp('guidePanel').body.update(map_guide_overlay);
	    	        		}
	    	        	}
	    	        }
	    	    }]
	       }
    ]    
})

var	eastPanel = new Ext.form.FormPanel({
	id : "eastPanel",
	region:'east',
	width: 200,
	collapsible : true,
	collapsed: false, 
	title:'显示方式及图例',
	frame:'true',
	split : true,
	items:[fieldPanel,guidePanel]
});

var gis_panel = new Ext.form.FormPanel({
	id:'gis_panel',
	title:'center',
	region:'center',
	//tbar:gis_toolbar,
	html:'<iframe name="gisMap" src="jsp/gis/gisMap.jsp" frameborder="0" width="100%" height="100%"/>'
});

// 获取首个顶级区域名称和ID
function init_area(){
	Ext.Ajax.request({
		timeout: 60000,
		url: 'area!getTopAreaName.action',
	    method: 'POST',
	    params: {},
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	areaTree.setValue(obj.NAME);
	    	areaTree.areaId = obj.ID;
	    	area = obj.NAME;
	    	// 获取首个顶级区域名称后再打开地图
	    	var win = new Ext.Viewport({
	    		layout:'border',	//布局为border
	    		items:[northPanel,eastPanel,gis_panel]
	    	});
	    	win.show();
	    	// 初始化传输系统下拉框列表内容
	    	getSysComboData();
	    },
	    error:function(response) {
	    	Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    }
	});
};

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.Ajax.timeout = 900000;
	//Ext.Msg = top.Ext.Msg;
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
	init_area();
//	var win = new Ext.Viewport({
//		layout:'border',	//布局为border
//		items:[northPanel,eastPanel,gis_panel]
//	});
//	win.show();
	sys_combo.hide();
});