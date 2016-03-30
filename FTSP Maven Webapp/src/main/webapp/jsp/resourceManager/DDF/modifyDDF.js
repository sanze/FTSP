function debug(type, title, msg){
	console[type](title + " - " + msg);
}

//--------------------------------- 修改DDF架------------------------------------------- 
var modifyDdfForm = new Ext.FormPanel({
	id : "modifyDdfForm", 
	region:"center",
    frame:false,
    bodyStyle:'padding:20px 30px 0',
    labelAlign: 'left', 
    autoScroll:true,   
	items : [{
        labelSeparator:"：",
        border:false,
		layout : 'form',    
		items : [{
	    	xtype: 'textfield',
	    	id:'mDdfNo',
	        name: 'mDdfNo',
	        fieldLabel: 'DDF端子号',
	        width: 200,
	        disabled:true,
	        allowBlank:false
        },{
	    	xtype: 'textfield',
	    	id:'mUseable',
	        name: 'mUseable',
	        width: 200,
	        fieldLabel: '用 &nbsp&nbsp&nbsp&nbsp&nbsp途'
        },{
    	    xtype: 'textfield',
            id:'mNote',
            name: 'mNote',
            width: 200,
	        fieldLabel: '备&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp注'
        }]
	}],
    buttonAlign : "right",
	buttons : [{
	text : '确定',
       handler: function(){
            if (modifyDdfForm.form.isValid()) {
            	var jsonData = { 
					"conMap.useable":Ext.getCmp('mUseable').getValue(),
					"conMap.note":Ext.getCmp('mNote').getValue(),
					"conMap.ddfId":ddfId
            	} ;    
		    	Ext.Ajax.request({ 
		    		url : 'resource-dframe!modifyDDF.action',
				    method : 'POST',
				    params: jsonData,
				    success: function(response) {
				    	top.Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if(obj.returnResult == 1){
							Ext.Msg.alert("提示",obj.returnMessage, function(r){	
						   		var pageTool = parent.Ext.getCmp('pageTool');
			            		if(pageTool){
			    					pageTool.doLoad(pageTool.cursor);
			    				}
								//关闭修改任务信息窗口
			            		parent.useStore.reload();
			    				var win = parent.Ext.getCmp('modifyDdfWindow');
			    				if(win){
			    					win.close();
			    				}   
							}); 
						}
						if(obj.returnResult == 0){
	            			Ext.Msg.alert("错误",obj.returnMessage);
	            		}
				    },
				    error:function(response) {
				    	top.Ext.getBody().unmask();
				    	Ext.Msg.alert("异常",response.responseText);
				    },
				    failure:function(response) {
				    	top.Ext.getBody().unmask();
				    	Ext.Msg.alert("异常",response.responseText);
				    }
				}); 
        }
	 }
	},{
	    text: '取消',
	    handler: function(){
	    	var win = parent.Ext.getCmp('modifyDdfWindow');
			if(win){
				win.close();
			}
	    }
	 }]
}); 
 
Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
	Ext.Ajax.timeout=900000;   

	var win = new Ext.Viewport({
	        id:'win',
			layout : 'border',
			items : [modifyDdfForm]
	}); 
	Ext.getCmp('mDdfNo').setValue(mDdfNo);
	Ext.getCmp('mUseable').setValue(mUseable);
	Ext.getCmp('mNote').setValue(mNote); 
	win.show();	 
 });
