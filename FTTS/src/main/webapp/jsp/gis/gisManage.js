////regionModel 数据模型定义
//Ext.define('regionModel', {
//	extend: 'Ext.data.Model',
//	fields:[{type:'int',name:'REGION_ID'},
//	        {type:'string',name:'REGION_NAME'}]
//});
//
////区域下拉框
//var region_combo_store = Ext.create('Ext.data.Store', {
//	model:regionModel,
//	proxy: {
//        type: 'ajax',
//        url: 'gis!getRegions.action',
//		reader: {
//			type: 'json',
//			root: 'data'
//		}
//    },
//    autoLoad: true,
//    remoteSort:true
//});
//
//var region_combo = Ext.create('Ext.form.ComboBox', {
//    store: region_combo_store,
//    queryMode: 'local',
//    emptyText:'区域',
//	labelAlign: 'right',
//	valueField:'REGION_ID',
//	displayField:'REGION_NAME',
//	listeners:{ 
//		'beforeselect':function(combo,record,index){			
//			cable_combo.setValue(null);
//			cable_combo.getStore().removeAll();
//			resource_type_combo.setValue(null);
//			resource_combo.setValue(null);
//			resource_combo.getStore().removeAll();
//        },
//        'select': function(combo,record,index){
//        	cable_combo_store.getProxy().extraParams = { 
//        		'jsonString':Ext.encode({'resourceType': 4, 
//        		'regionId': region_combo.getValue()})
//        	};
//        	cable_combo_store.load();
//        }
//   }
//});
//
////光缆模型
//Ext.define('cableModel', {
//	extend: 'Ext.data.Model',
//	fields:[{type:'string',name:'RESOURCE_ID'},
//	        {type:'string',name:'RESOURCE_NAME'}]
//});
//
//var cable_combo_store = Ext.create('Ext.data.Store', {
//	model:cableModel,
//	proxy: {
//        type: 'ajax',
//        url: 'gis!getResourceNames.action',
//		reader: {
//			type: 'json',
//			root: 'data'
//		}
//    },
//    autoLoad: false,
//    remoteSort:true
//});
//
//var cable_combo = Ext.create('Ext.form.ComboBox', {
//    store: cable_combo_store,
//    queryMode: 'local',
//    emptyText:'光缆',
//	labelAlign: 'right',
//	valueField:'RESOURCE_ID',
//	displayField:'RESOURCE_NAME',
//	listeners:{ 
//		'beforeselect':function(combo,record,index){			
//			resource_type_combo.setValue(null);
//			resource_combo.setValue(null);
//			resource_combo.getStore().removeAll();
//        }
//   }
//});
//
//
////资源类型下拉框
//Ext.define('resourceTypeModel', {
//	extend: 'Ext.data.Model',
//	fields:[{type:'int',name:'RESOURCE_TYPE'},
//	        {type:'string',name:'RESOURCE_TYPE_NAME'}]
//});
//
//var resource_type_combo_store = Ext.create('Ext.data.Store', {
//	model:resourceTypeModel,
//	data:[
//	      {'RESOURCE_TYPE':0,'RESOURCE_TYPE_NAME':'机房'},
////          {'RESOURCE_TYPE':4,'RESOURCE_TYPE_NAME':'光缆'},
//          {'RESOURCE_TYPE':5,'RESOURCE_TYPE_NAME':'光缆段'},
//          {'RESOURCE_TYPE':1,'RESOURCE_TYPE_NAME':'管井/杆塔'},
//          {'RESOURCE_TYPE':3,'RESOURCE_TYPE_NAME':'交接箱'}]
//});
//
//var resource_type_combo = Ext.create('Ext.form.ComboBox', {
//    store: resource_type_combo_store,
//    queryMode: 'local',
//    emptyText:'资源类型',
//	labelAlign: 'right',
//	valueField:'RESOURCE_TYPE',
//	displayField:'RESOURCE_TYPE_NAME',
//	listeners:{
//        'beforeselect':function(combo,record,index){
//        	if(region_combo.getValue() == null ||cable_combo.getValue() == null){
//        		Ext.Msg.alert("提示","请先选择光缆!");
//        	}
//        	
//        	if(resource_combo.getValue() != null){
//        		resource_combo.setValue(null);
//    			resource_combo.getStore().removeAll();
//        	}
//        },
//        'select': function(combo,record,index){
//        	if(region_combo.getValue() == null || cable_combo.getValue() == null){
//        		this.setValue(null);
//        	}
//        	resource_combo_store.getProxy().extraParams = { 
//        		'jsonString':Ext.encode({'resourceType': this.getValue(), 
//        		'regionId': region_combo.getValue(),
//        		'cableId':cable_combo.getValue()})
//        	};
//        	resource_combo_store.reload();
//        }
//   }
//});
//
////资源名模型
//Ext.define('resourceModel', {
//	extend: 'Ext.data.Model',
//	fields:[{type:'string',name:'RESOURCE_ID'},
//	        {type:'string',name:'RESOURCE_NAME'},
//	        {type:'string',name:'LNG'},
//	        {type:'string',name:'LAT'},
//	        {type:'string',name:'TOWER_TUBE_TYPE'}]
//});
//
//var resource_combo_store = Ext.create('Ext.data.Store', {
//	model:resourceModel,
//	proxy: {
//        type: 'ajax',
//        url: 'gis!getResourceNames.action',
//		reader: {
//			type: 'json',
//			root: 'data'
//		}
//    },
//    autoLoad: false,
//    remoteSort:true
//});
//
//var type;
////资源名下拉框
//var resource_combo = Ext.create('Ext.form.ComboBox', {
//    store: resource_combo_store,
//    queryMode: 'local',
//    emptyText:'资源名',
//	labelAlign: 'right',
//	valueField:'RESOURCE_ID',
//	displayField:'RESOURCE_NAME',
//	listeners:{
//        'select': function(combo,record,index){
//        	if(resource_type_combo.getValue() == 5){
//        		return;
//        	}
//        	if(record[0].data.LNG == '' ||record[0].data.LAT == ''){
//        		Ext.getCmp('locate_btn').disable();
//        		Ext.getCmp('add_btn').enable();
//        	}else{
//        		Ext.getCmp('add_btn').disable();
//        		Ext.getCmp('locate_btn').enable();
//        	}
//        	if(record[0].data.TOWER_TUBE_TYPE != ''){
//        		//杆塔管井表中的type需要+1才能匹配系统中的资源type
//        		type = record[0].data.TOWER_TUBE_TYPE+1;
//        	}else{
//        		type = resource_type_combo.getValue();
//        	}
//        }
//   }
//});

var region_combo_store = new Ext.data.Store({
	//url: 'getRouteStart.action', 
	reader: new Ext.data.JsonReader({
		totalProperty: 'total',
		root : "data"
	},[
	   "REGION_ID","REGION_NAME"
	   ])
});

var region_combo = new Ext.form.ComboBox({
    store: ["南京","苏州","无锡","常州","南通"],
    mode:"local",
    //editable:false,
    triggerAction: 'all',
    emptyText:'区域',
	width:200,
//	valueField:'REGION_ID',
//	displayField:'REGION_NAME',
	listeners:{ 
		
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
				sys_combo.show();
			}else{
				sys_combo.hide();
			}
		}
   }
});

var sys_combo = new Ext.form.ComboBox({
    store: ["1","2"],
    mode:"local",
    //editable:false,
    triggerAction: 'all',
    emptyText:'传输系统',
	width:200,
//	valueField:'REGION_ID',
//	displayField:'REGION_NAME',
	listeners:{ 
		
   }
});

var gis_toolbar = new Ext.Toolbar({
	id:'gis_toolbar',
	height:35,
	style:"padding:7px 0 0 0;",
	items: ["区域：",region_combo,"&nbsp&nbsp","显示方式：",radioGroup,sys_combo, '->', 
     {
		text : '查询',
		icon : 'resource/images/btnImages/search.png',
		handler : function(){
			
		}
	},"&nbsp&nbsp",
	{
		text : '刷新',
		icon : 'resource/images/btnImages/refresh.png',
		handler : function(){
			gisMap.window.location.reload();			
		}
	}]
});

var northPanel = new Ext.form.FormPanel({
	id : "northPanel",
	region:'north',
//	title:'north',
	height:35,
	items:[gis_toolbar]
});

function getNewText(text){
	return '<font face="黑体" color="#2F4F4F" size="2"><b>'+text+'</b></font>'
}

var map_guide = '<table>'+
	'<tr><td height="50px"><img src="resource/images/GisImages/Station_NoAlarm.png"/></td>'+
	'<td height="50px">'+getNewText("：机房")+'</td></tr>'+
	'<tr><td height="50px"><hr width=30px size=24 color="#8968CD"></td>'+
	'<td height="50px">'+getNewText("：全部测试覆盖")+'</td></tr>'+
	'<tr><td height="50px"><hr width=30px size=6 color="#87CEFA"></td>'+
	'<td height="50px">'+getNewText("：部分测试覆盖")+'</td></tr>'+
	'<tr><td height="50px"><hr width=30px size=6 color="green"></td>'+
	'<td height="50px">'+getNewText("：无测试覆盖")+'</td></tr>'+
	'<tr><td height="50px"><hr width=30px size=6 color="red"></td>'+
	'<td height="50px">'+getNewText("：紧急告警")+'</td></tr>'+
	'<tr><td height="50px"><hr width=30px size=6 color="orange"></td>'+
	'<td height="50px">'+getNewText("：重要告警")+'</td></tr>'+
	'<tr><td height="50px"><hr width=30px size=6 color="yellow"></td>'+
	'<td height="50px">'+getNewText("：次要告警")+'</td></tr>'+
	'<tr><td height="50px"><hr width=30px size=6 color="blue"></td>'+
	'<td height="50px">'+getNewText("：提示告警")+'</td></tr>'
	+'</table>'

var guidePanel = new Ext.Panel({
	html:map_guide
});

var fieldPanel = new  Ext.form.FieldSet({
	title:'<font size="2">资源显示方式</font>',
	style:"margin-top:50px;",
	items:[
	       {
	    	   xtype:'panel',
	    	   items:[{
	    	        xtype: 'radio',
	    	        boxLabel: getNewText("&nbsp普通"),
	    	        name: 'type',
	    	        inputValue: '0',
	    	        width:90,
	    	        height:30,
	    	        checked:true,
	    	        listeners:{
	    	        	'check':function(o,checked){
	    	        		if(checked){
	    	        			gisMap.window.displayDataByStrategy(o.inputValue);
	    	        		}
	    	        	}
	    	        }
	    	    },
	    	    {
	    	        xtype: 'radio',
	    	        boxLabel: getNewText("&nbsp显示告警"),
	    	        name: 'type',
	    	        height:30,
	    	        inputValue: '1',
	    	        listeners:{
	    	        	'check':function(o,checked){
	    	        		if(checked){
	    	        			gisMap.window.displayDataByStrategy(o.inputValue);
	    	        		}
	    	        	}
	    	        }
	    	    },
	    	    {
	    	        xtype: 'radio',
	    	        boxLabel: getNewText("&nbsp测试路由覆盖"),
	    	        name: 'type',
	    	        height:30,
	    	        inputValue: '2',
	    	        listeners:{
	    	        	'check':function(o,checked){
	    	        		if(checked){
	    	        			gisMap.window.displayDataByStrategy(o.inputValue);
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
	title:'图例',
	frame:'true',
	split : true,
	items:[guidePanel,fieldPanel]
});

var gis_panel = new Ext.form.FormPanel({
	id:'gis_panel',
	title:'center',
	region:'center',
	//tbar:gis_toolbar,
	html:'<iframe name="gisMap" src="jsp/gis/gisMap.jsp" frameborder="0" width="100%" height="100%"/>'
});

//Ext.application({name:"welcomeGis",launch:function(){
//	Ext.create('Ext.container.Viewport',{
//		layout:'border',	//布局为border
//		items:[northPanel,westPanel,gis_panel]
//	  });
//  }
//});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.Ajax.timeout = 900000;
	//Ext.Msg = top.Ext.Msg;
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
	
	var win = new Ext.Viewport({
		layout:'border',	//布局为border
		items:[northPanel,eastPanel,gis_panel]
	});
	win.show();
	sys_combo.hide();
});