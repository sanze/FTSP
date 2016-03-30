/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var store = new Ext.data.Store(
{
	url: 'getFiberList.action',
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    "fiberId","displayName"
	    
    ])
});

store.proxy = new Ext.data.HttpProxy({
	url : 'getFiberList.action'
});
store.baseParams = {"sectionWaveModel.trunkLineId":trunkLineId};
store.load({
	callback: function(r, options, success){  
		if(success){
			
		}else{
			Ext.Msg.alert('错误','查询失败！请重新查询');
		}
	}  
});

//主用光缆
var fiber=new Ext.form.ComboBox({  
    id:'fiber',
    name: 'fiber',
    fieldLabel: '主光缆',
    displayField:"displayName",
    valueField:'fiberId',	
    selectOnFoucs:true,
    editable:true,
    store:store,
    allowBlank:false,
    triggerAction: 'all',
	resizable: true,
    anchor: '95%'
});

//主用光缆距离
var fiberDistance = new Ext.form.NumberField({
    id:'fiberDistance',
    name: 'fiberDistance',
    fieldLabel: '主光缆距离',
    allowDecimals:true,
    allowNegative : true,
    allowBlank:false,
    minValue : 1,
    anchor: '95%'
});

//备用光缆
var subFiber=new Ext.form.ComboBox({  
    id:'subFiber',
    name: 'subFiber',
    fieldLabel: '备用光缆',
    displayField:"displayName",
    valueField:'fiberId',	
    selectOnFoucs:true,
    editable:true,
    store:store,
//    allowBlank:false,
    triggerAction: 'all',
	resizable: true,
    anchor: '95%'
});

//备用光缆距离
var subFiberDistance = new Ext.form.NumberField({
    id:'subFiberDistance',
    name: 'subFiberDistance',
    fieldLabel: '备用光缆距离',
    allowDecimals:true,
    allowNegative : true,
    minValue : 1,
    anchor: '95%'
});

var formPanel = new Ext.FormPanel({
	id:'formPanel',
	region:"center",
    border:false,
    frame:false,
	autoScroll:true,
    labelWidth: 120,
    width: 200,
    bodyStyle: 'padding:10px 12px 0;',
    items: [
        fiber,fiberDistance,subFiber,subFiberDistance
    ],
    buttons: [{
        text: '确定',
        handler: function(){
        	if(formPanel.getForm().isValid()){
        		var ptpId = Ext.getCmp("fiber").getValue();
        		var ptpName = Ext.getCmp("fiber").getRawValue();
        		var subPtpId = Ext.getCmp("subFiber").getValue();
        		var subPtpName = Ext.getCmp("subFiber").getRawValue();
        		var note = Ext.getCmp("fiberDistance").getValue();
        		var subNote = Ext.getCmp("subFiberDistance").getValue();
        		parent.addFiberInfo(ptpId,ptpName,subPtpId,subPtpName,note,subNote);
        		//关闭修改任务信息窗口
				var win = parent.Ext.getCmp('fiberInfoWindow');
				if(win){
					win.close();
				}
        	}
		}
     },{
        text: '取消',
        handler: function(){
            //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('fiberInfoWindow');
			if(win){
				win.close();
			}
        }
    }]
});
    
 Ext.onReady(function(){
 	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();}
 	Ext.Msg = top.Ext.Msg; 
	  	
  	var win = new Ext.Viewport({
        id:'win',
        layout : 'border',
		items : [
		  formPanel
		],
		renderTo : Ext.getBody()
	});
 });