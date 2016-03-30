var objType='';
/*-----------------------------------主告警名称------------------------------*/ 
var alarmName = new Ext.form.TextField({
	id:'ALARM_NAME', 
	fieldLabel:"主告警名称",		 
	sideText:'<font color=red>*</font>',
	allowBlank:false,
	anchor : '90%'  
});

/*-----------------------------------告警对象类型------------------------------*/ 
var objTypeMapping = [ [ 1, '网元' ], [ 9, '板卡' ], [ 5, '端口' ], [ 6, '通道' ]];   
var type;
(function() {
	var typeStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'type'
		} ]
	});
	typeStore.loadData(objTypeMapping);
	type = new Ext.form.ComboBox({
		id : 'type',
		fieldLabel : '告警对象类型',
		mode : 'local',
		sideText:'<font color=red>*</font>',
		allowBlank:false,
		store : typeStore,
		displayField : 'type',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		value : 1,
		resizable : true,
		listeners:{
			'select':function(){
				objType = Ext.getCmp('type').getValue();
				if(objType==1 || objType==9){
					//网元板卡 
					centerPanel.remove(centerPanel.findById('portType')); 
					centerPanel.remove(centerPanel.findById('channelType'));   
				}	
				if(objType==5){
					//端口 
					centerPanel.remove(centerPanel.findById('portType')); 
					centerPanel.remove(centerPanel.findById('channelType'));  
					centerPanel.insert(2,portType); 
				}	
				if(objType==6){
					//通道
					centerPanel.remove(centerPanel.findById('portType')); 
					centerPanel.remove(centerPanel.findById('channelType'));  
					centerPanel.insert(2,channelType); 
				}
				centerPanel.doLayout();
			}
		}
	});
})(); 

/*-----------------------------------端口ptpType-----------------------------*/
var portType = {
	id : 'portType',
	xtype : 'panel',  
	border : false, 
	padding:20,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			border : false,
			columnWidth : .5,
			defaultType: 'checkbox',
			labelWidth : 15,
			items : [{
					boxLabel : 'STM-1',  
					checked : false
				}, {
					boxLabel : 'STM-4',  
					checked : false
				}, {
					boxLabel : 'STM-16',  
					checked : false
				}, {
					boxLabel : 'STM-64',  
					checked : false
				}]
			},{
				layout : 'form',
				border : false,
				columnWidth : .5,
				defaultType: 'checkbox',
				labelWidth : 15,
				items : [{
					boxLabel : 'STM-256',   
					checked : false
				}, {
					boxLabel : '光传送',   
					checked : false
				}, {
					boxLabel : '光复用',   
					checked : false
				}]
			}]
		}]
	};

/*-----------------------------------通道ptpType-----------------------------*/
var channelType = {
	id : 'channelType',
	xtype : 'panel',
	border : false, 
	padding:20,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			border : false,
			columnWidth : .5,
			defaultType: 'checkbox',
			labelWidth : 15,
			items : [{
					boxLabel : '64C',  
					checked : false
				}, {
					boxLabel : '16C',  
					checked : false
				}, {
					boxLabel : '8C',  
					checked : false
				}, {
					boxLabel : '4C',  
					checked : false
				}]
			},{
				layout : 'form',
				border : false,
				columnWidth : .5,
				defaultType: 'checkbox',
				labelWidth : 15,
				items : [{
					boxLabel : 'AU4/VC4',  
					checked : false
				}, {
					boxLabel : 'VC3',  
					checked : false
				}, {
					boxLabel : '光通道',  
					checked : false
				}]
			}]
		}]
	};

var centerPanel = new Ext.FormPanel({
	id : 'formPanel', 
	region:'center',
	bodyStyle : 'padding:30px 30px 0 30px',
	autoScroll : true,  
	items : [{
		border:false, 
		items:[{
			layout : 'form', 
			border : false,
			labelWidth:90,
			items:[ alarmName,type] 
		}]
	}],
	buttons : [ {
		text : '确定',
		handler : function(){
			if(centerPanel.getForm().isValid()){
				//判断主告警名称是否重复
				 var alarmName = Ext.getCmp('ALARM_NAME').getValue();
				 if(alarmName==null || alarmName==""){
					 Ext.Msg.alert("提示","主告警名称不能为空！");
					 return;
				 }
			     if(!(editType==0 && (parent.storeCond.findExact("ALARM_NAME", alarmName) >=0))){ 
					// 对象等级
					var alarmLevelCheck = '';
					var alarmLevel='';
					if(objType==5){
						alarmLevel = Ext.getCmp('portType').get(0).items;
					}	
					if(objType==6){
						alarmLevel = Ext.getCmp('channelType').get(0).items;
					} 
					for ( var i = 0; i < alarmLevel.length; i++) {
						var alarmTypeChild = alarmLevel.get(i).items;
						for ( var j = 0; j < alarmTypeChild.length; j++) {
							if(alarmTypeChild.get(j).checked){
								var channel = alarmTypeChild.get(j).boxLabel;
								if(channel=="AU4/VC4") channel="VC4";
								alarmLevelCheck += channel + ',';
							}
						}
					}    
					alarmLevelCheck = alarmLevelCheck.substring(0, alarmLevelCheck.lastIndexOf(","));  
					if((objType==5 || objType==6)&& alarmLevelCheck==''){
						 Ext.Msg.alert("提示","对象等级必选！"); 
						 return;
					}
					if(editType==0){
						//新增
						var defaultData = {
							ALARM_NAME: Ext.getCmp('ALARM_NAME').getValue(),
							OBJECT_TYPE:parseInt(Ext.getCmp('type').getValue()),
							LEVEL:alarmLevelCheck 
						}; 
						parent.storeCond.add(new parent.storeCond.recordType(defaultData));   
					}else if(editType==1){   
							parent.storeCond.getAt(linenum).set('ALARM_NAME',Ext.getCmp('ALARM_NAME').getValue()); 
							parent.storeCond.getAt(linenum).set('OBJECT_TYPE',parseInt(Ext.getCmp('type').getValue())); 
							parent.storeCond.getAt(linenum).set('LEVEL',alarmLevelCheck);  
					}
				    var win = parent.Ext.getCmp('editCondtion');
					if (win) {
						win.close();
					 }
			     } else{
			    	 Ext.Msg.alert("提示","主告警名称冲突！");
			     }
			}
		}
	}, {
		text : '取消 ',
		handler : function() {
			var win = parent.Ext.getCmp('editCondtion');
			if (win) {
				win.close();
			}
		}
	} ]
});   

function initData(){
	if(objType==5){
		centerPanel.insert(2,portType); 
	}
	if(objType==6){
		centerPanel.insert(2,channelType); 
	}
//	//主告警名称 
	Ext.getCmp('ALARM_NAME').setValue(alarmName);	 
	//对象类型 
	Ext.getCmp('type').setValue(objType); 
	Ext.getCmp('type').setRawValue(objType); 
	//对象等级  
	var alarmLevel='';
	if(objType==5){
		alarmLevel = Ext.getCmp('portType').get(0).items;
	}	
	if(objType==6){
		alarmLevel = Ext.getCmp('channelType').get(0).items;
	} 
	for ( var i = 0; i < alarmLevel.length; i++) {
		var alarmTypeChild = alarmLevel.get(i).items;
		for ( var j = 0; j < alarmTypeChild.length; j++) {
			var check = alarmLevelCheck.split(",");
			for(var m in check){
				if(check[m]==alarmTypeChild.items[j].boxLabel){
					alarmTypeChild.get(j).setValue(true);
				}
			}
		} 
	} 
} 

Ext.onReady(function(){
	Ext.QuickTips.init(); 
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){
 		top.Ext.menu.MenuMgr.hideAll();
	};   
	if(editType==1){
  		initData();
  	}
  	var win =new Ext.Viewport({
  		id:'win',
        layout : 'border', 
        items : centerPanel
	});  
 });
