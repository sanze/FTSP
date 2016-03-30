
//加载时数据
var queryHourComboPanel= {
		id : 'queryHourComboPanel',
		style : 'margin-left:20px;margin-top:20px;',
		border : false,
		layout : 'column',
		items : [
			{
				border : false,
				width:40,
				html : '<span>时：</span>'
			},  {
				xtype : 'combo',
				style : 'margin-left:0',
				id : 'queryHourCombo',
				name : 'queryHourCombo',
				fieldLabel : '',
				mode : "local",
				width : 140,
				editable : false,
				value:parent.qhour==0?1:parent.qhour,
				store : new Ext.data.ArrayStore({
					fields:[
							   {name:"value",mapping:"value"},
							   {name:"displayName",mapping:"displayName"}
					   ]
					}),
				valueField : 'value',
				displayField : 'displayName',
				triggerAction : 'all',
				anchor : '95%',
				listeners : {
					beforequery:function(queryEvent){
						var arr=[];
						for(var i=1;i<=24;i++){
							var json={};
							json.value=i;
							json.displayName=i;
							arr.push(json);
						}
						Ext.getCmp('queryHourCombo').getStore().loadData(arr);  
					},
					select : function(combo, record, index) {
						
					}
				}
          	}
	  ]
};



//加载分数据
var queryMinuteComboPanel= {
		id : 'queryMinuteComboPanel',
		style : 'margin-left:20px;margin-top:20px;',
		border : false,
		layout : 'column',
		items : [
			{
				border : false,
				width:40,
				html : '<span>分：</span>'
			},  {
				xtype : 'combo',
				style : 'margin-left:0',
				id : 'queryMinuteCombo',
				name : 'queryMinuteCombo',
				fieldLabel : '',
				mode : "local",
				width : 140,
				editable : false,
				value:parent.qmin,
				store : new Ext.data.ArrayStore({
					fields:[
							   {name:"value",mapping:"value"},
							   {name:"displayName",mapping:"displayName"}
					   ]
					}),
				valueField : 'value',
				displayField : 'displayName',
				triggerAction : 'all',
				anchor : '95%',
				listeners : {
					beforequery:function(queryEvent){
						var arr=[];
						for(var i=0;i<=59;i++){
							var json={};
							json.value=i;
							json.displayName=i;
							arr.push(json);
						}
						Ext.getCmp('queryMinuteCombo').getStore().loadData(arr);  
					},
					select : function(combo, record, index) {
						
						
						
					}
				}
          	}
	  ]
};


var dateChoosePanel = new Ext.FormPanel({
	region : 'center',
	border :false,
	items : [{
		layout : 'form',
		border : false,
		items:[queryHourComboPanel,queryMinuteComboPanel]
	}],
	buttons: [{
		id:'ok',
	    text: '确定',
	    handler: function(){
	    	var hour=Ext.getCmp('queryHourCombo').getValue();
	    	var min=Ext.getCmp('queryMinuteCombo').getValue();
	    	parent.setChooseDate(hour,min);
	    	//关闭修改任务信息窗口
			var win = parent.Ext.getCmp('dateChooseWindow');
			if(win){
				win.close();
			}
		}
	 },
	 { xtype: 'tbspacer', width: 60,shadow:false },
	 {
	    text: '取消',
	    handler: function(){
	        //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('dateChooseWindow');
			if(win){
				win.close();
			}
	    }
	}]
});

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
 	Ext.Msg = top.Ext.Msg; 
  	new Ext.Viewport({
        layout : 'border',
        items : dateChoosePanel
	});
});

