function debug(type, title, msg){
	console[type](title + " - " + msg);
}
var id = 0;   

// 添加按钮
var newDDFForm = new Ext.Action({ 
    text: '增加',
    handler: function(){ 
    	id++;  
        var ddfForm = new Ext.Panel({
            //column布局控件开始                
            id: 'ddf_' + id,
            layout: 'column',
            border: false,
            items:[{
	            columnWidth:.3,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 72,
	            items: [{
			    	xtype: 'textfield',
			    	id:'ddfNo_'+id,
			        name: 'ddfNo_'+id,
			        fieldLabel: 'DDF端子号',
			        sideText : '<font color=red>*</font>',  
			        width: 130,
			        allowBlank:false
		        }]
	        },{
	            columnWidth:.3,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 50,
	            items: [{
			    	xtype: 'textfield',
			    	id:'useable_'+id,
			        name: 'useable_'+id,
			        width: 130,
			        fieldLabel: '用途'
		        }]
	        },{
	            columnWidth:.3,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 50,
	            items: [{
		            xtype: 'textfield',
		            id:'note_'+id,
		            name: 'note_'+id,
		            width: 130,
			        fieldLabel: '备注'
		        }]
	        },{
                columnWidth: .05,
                layout: 'form',
                border: false,
                items: [{
                    xtype: 'button',
                    text: '删除',
                    value: id,
                    scope: this,
                    handler: function(obj){
                    	id--;
                        var del_id = obj.value;
                        var object = Ext.getCmp('ddf_' + del_id);
                        //删除一行
                        formPanel.remove(object, true);
                    }
                }]
            }]
        });
        //添加fieldSet
        formPanel.add(ddfForm);
        //重新刷新
        formPanel.doLayout();
    }
});
 
 var formPanel = new Ext.FormPanel({
	region:"center",
    frame:false,
    border:false,
    height:290,
	bodyStyle:'padding:10px 10px 0',
    labelAlign: 'left',
	autoScroll:true,
	items: [{
        layout:'column',
        border:false,
        items:[{
            columnWidth:.3,
            layout: 'form',
            border:false,
            labelSeparator:"：",
            labelWidth: 72,
            items: [{
		    	xtype: 'textfield',
		    	id:'ddfNo_0',
		        name: 'ddfNo_0',
		        fieldLabel: 'DDF端子号',
		        width: 130,
		        sideText : '<font color=red>*</font>',  
		        allowBlank:false
	        }]
        },{
            columnWidth:.3,
            layout: 'form',
            border:false,
            labelSeparator:"：",
            labelWidth: 50,
            items: [{
		    	xtype: 'textfield',
		    	id:'useable_0',
		        name: 'useable_0',
		        width: 130,
		        fieldLabel: '用途'
	        }]
        },{
            columnWidth:.3,
            layout: 'form',
            border:false,
            labelSeparator:"：",
            labelWidth: 50,
            items: [{
	            xtype: 'textfield',
	            id:'note_0',
	            name: 'note_0',
	            width: 130,
		        fieldLabel: '备注'
	        }]
        }]
    }],
    buttonAlign : "right",
	buttons: [
	          newDDFForm,
        {
        text: '确定',
        //定义表单提交事件
        handler: function(){
            if (formPanel.form.isValid()) {
	            var ddfNos = new Object();
	        	var ddfList = new Array();
	            for(var i=0;i<=id;i++){
	             	var ddf = {
            			"ddfNo":Ext.getCmp('ddfNo_'+i).getValue(), 
						"useable":Ext.getCmp('useable_'+i).getValue(),
						"note":Ext.getCmp('note_'+i).getValue()
	            	};
	             	
			    	var ddfNo = Ext.getCmp('ddfNo_'+i).getValue();
			    	if(!ddfNos[ddfNo]){
			    		ddfNos[ddfNo]=1;
			    	}else{
			    		Ext.Msg.alert('提示','DDF端子号已存在！');
			    		return ;
			    	}
	             	ddfList.push(Ext.encode(ddf)); 
	            }  
        		var jsonData = {
        			"dataList" : ddfList,
        			"RESOURCE_ROOM_ID":roomId
        		}; 
		    	Ext.Ajax.request({ 
		    		url : 'resource-dframe!addDDF.action',
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
			            		parent.useStore.reload();
								//关闭修改任务信息窗口
			    				var win = parent.Ext.getCmp('addDdfWindow');
			    				if(win){
			    					win.close();
			    				}  
							}); 
						}
						if(obj.returnResult == 0){
	            			Ext.Msg.alert("提示",obj.returnMessage);
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
        	var win = parent.Ext.getCmp('addDdfWindow');
			if(win){
				win.close();
			}
        }
    }]
}); 
 
//增加条目：“*为必填项”
// var tipPanel = new Ext.FormPanel({
// 	region:"center",
//     frame:false,
//     border:false,
// 	items: [{
// 		xtype:'label',
// 		html:'<font color=red size = 2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*为必填项</font>'
// 	}]
// });

 
Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
	Ext.Ajax.timeout=900000;   
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [formPanel]
	});  
	 win.show();	
 });
