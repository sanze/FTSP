function debug(type, title, msg){
	console[type](title + " - " + msg);
}

var id = 0;   
//-------------------光纤芯号存储store与光缆名称级联-----------------------------
var fiberStore =new Ext.data.Store({
	url : 'resource-dframe!getFiberNameList.action',
	baseParams : { 
	},
    reader: new Ext.data.JsonReader({ 
				root : "rows"
		},[
			"FIBER_NO"
	])
});

//-------------------光缆名称的联想输入框------开始------------------------------
var cableStore = new Ext.data.Store(
{
    proxy : new Ext.data.HttpProxy({
		url : 'resource-dframe!getCableNameList.action',
		async: false
    }), 
    pageSize:10,
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
       "RESOURCE_CABLE_ID","CABLE_NAME"
    ])
});

var Cable = new Ext.form.ComboBox({
    id: 'cableName_0',
    width: 130,
    minListWidth: 220,
    store: cableStore,
    fieldLabel: '光缆名称',
    valueField: 'RESOURCE_CABLE_ID',
    displayField: 'CABLE_NAME',
    emptyText : '输入对象名',
    listEmptyText: '未找到匹配的结果',
    loadingText: '搜索中...',
    mode:'remote',  
    pageSize:cableStore.pageSize,
    queryDelay: 400,
    typeAhead: false,
    autoSelect:false,
    enableKeyEvents : true,
    resizable: true,
    autoScroll:true,
    listeners : {
      keypress: function(field, event) {
        field.setValue(field.getRawValue());
        if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
          gKey = field.getValue();
          if(gKey == null || gKey==""){
            return;
          }
        }
      },
      beforequery:function(event){
        if(event.combo.lastQuery!=event.combo.getRawValue()){
        	event.combo.lastQuery=event.combo.getRawValue();
        	queryCable(event.combo,event.combo.getRawValue());
          return false;
        }
      },
      select:function(combo,record,index){
    	  fiberStore.baseParams={
    			"RESOURCE_CABLE_ID":record.get("RESOURCE_CABLE_ID")
    		};
    	  fiberStore.load({
    		   callback : function(r, options, success) {
    		       if (success) { 		
    		         } else {
    		            Ext.Msg.alert('错误', '查询失败！请重新查询');
    		    	}
    		    }
    		 });  
      },
      scope : this
    }
  }); 
		
function queryCable(combo,gKey){ 
    cableStore.baseParams={
		"CABLE_NAME":gKey,
		"limit": cableStore.pageSize
	};
	cableStore.load({
		callback : function(records,options,success){
			if(!success)
				Ext.Msg.alert("提示","模糊搜索出错");
		}
	});
    combo.expand();
}
//-------------------光缆名称的联想输入框 ----结束--------------------------------

// 添加按钮
var newODFForm = new Ext.Action({ 
    text: '增加',
    handler: function(){ 
    	id++;  
    	var mulfiberStore = "fiberStore"+id;
    	var mulcableId = 'cableName_'+id;
    	var mulfiberStore=new Ext.data.Store({
    		url : 'resource-dframe!getFiberNameList.action',
    		baseParams : { 
    		},
    	    reader: new Ext.data.JsonReader({ 
    					root : "rows"
    			},[
    				"FIBER_NO"
    		])
    	}); 
    	
    	var mulcableStore = "cableStore"+id; 
    	var mulcableStore = new Ext.data.Store(
		{
		    proxy : new Ext.data.HttpProxy({
				url : 'resource-dframe!getCableNameList.action',
				async: false
		    }), 
		    pageSize:10,
			reader: new Ext.data.JsonReader({
		        totalProperty: 'total',
				root : "rows"
		    },[
		       "RESOURCE_CABLE_ID","CABLE_NAME"
		    ])
		}); 		
		function queryCable(combo,gKey){
			mulcableStore.baseParams={
				"CABLE_NAME":gKey,
				"limit": mulcableStore.pageSize
			};
		    mulcableStore.load({
				callback : function(records,options,success){
					if(!success)
						Ext.Msg.alert("提示","模糊搜索出错");
				}
			});
		    combo.expand();
		} 
    	
        var odfForm = new Ext.Panel({
            //column布局控件开始                
            id: 'odf_' + id,
            layout: 'column',
            border: false,
            items:[{
	            columnWidth:.19,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 72,
	            items: [{
			    	xtype: 'textfield',
			    	id:'odfNo_'+id,
			        name: 'odfNo_'+id,
			        fieldLabel: 'ODF端子号',
			        sideText : '<font color=red>*</font>',  
			        width: 130,
			        allowBlank:false
		        }]
	        },{
	            columnWidth:.19,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 60,
	            items: [{
					xtype: 'combo',
            		id:mulcableId,
        		    width: 130,
        		    minListWidth: 220,
        		    store: mulcableStore,
        		    fieldLabel: '光缆名称',
        		    valueField: 'RESOURCE_CABLE_ID',
        		    displayField: 'CABLE_NAME',
        		    emptyText : '输入对象名',
        		    listEmptyText: '未找到匹配的结果',
        		    loadingText: '搜索中...',
        		    mode:'remote',  
        		    pageSize:mulcableStore.pageSize,
        		    queryDelay: 400,
        		    typeAhead: false,
        		    autoSelect:false,
        		    enableKeyEvents : true,
        		    resizable: true,
        		    autoScroll:true,
        		    listeners : {
        		      keypress: function(field, event) {
        		        field.setValue(field.getRawValue());
        		        if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
        		          gKey = field.getValue();
        		          if(gKey == null || gKey==""){
        		            return;
        		          }
        		        }
        		      },
        		      beforequery:function(event){
        		        if(event.combo.lastQuery!=event.combo.getRawValue()){
        		        	event.combo.lastQuery=event.combo.getRawValue();
        		        	queryCable(event.combo,event.combo.getRawValue());
        		          return false;
        		        }
        		      },
        		      select:function(combo,record,index){
        		    	  mulfiberStore.baseParams={
        		    			"RESOURCE_CABLE_ID":record.get("RESOURCE_CABLE_ID")
        		    		};
        		    	  mulfiberStore.load({
        		    		   callback : function(r, options, success) {
        		    		       if (success) { 		
        		    		         } else {
        		    		            Ext.Msg.alert('错误', '查询失败！请重新查询');
        		    		    	}
        		    		    }
        		    		 });  
        		      },
        		      scope : this
        		    } 
				}] 
	        },{
	            columnWidth:.19,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 60,
	            items: [{
					xtype: 'combo',
					id:'fiberNo_'+id, 		
					fieldLabel: '光纤芯号',
					store: mulfiberStore,
					emptyText:'请先选择光缆名称',
				    width: 130,
					displayField:"FIBER_NO",
					valueField : 'FIBER_NO',
					triggerAction : 'all',
					editable : false,  
					listeners : { 
				} 
				}]
	        },{
	            columnWidth:.19,
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
	            columnWidth:.19,
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
                        var object = Ext.getCmp('odf_' + del_id);
                        //删除一行
                        formPanel.remove(object, true);
                    }
                }]
            }]
        });
        //添加fieldSet
        formPanel.add(odfForm);
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
            columnWidth:.19,
            layout: 'form',
            border:false,
            labelSeparator:"：",
            labelWidth: 72,
            items: [{
		    	xtype: 'textfield',
		    	id:'odfNo_0',
		        name: 'odfNo_0',
		        fieldLabel: 'ODF端子号',
		        width: 130,
		        sideText : '<font color=red>*</font>',  
		        allowBlank:false
	        }]
        },{
            columnWidth:.19,
            layout: 'form',
            border:false,
            labelSeparator:"：",
            labelWidth: 60,
            items: [Cable] 
        },{
            columnWidth:.19,
            layout: 'form',
            border:false,
            labelSeparator:"：",
            labelWidth: 60,
            items: [{
				xtype: 'combo',
				id:'fiberNo_0', 		
				fieldLabel: '光纤芯号',
				store:fiberStore,
				displayField:"FIBER_NO",
				valueField : 'FIBER_NO',
				triggerAction : 'all',
				editable : false,  
				width: 130,
				emptyText:'请先选择光缆名称'
			}]
        },{
            columnWidth:.19,
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
            columnWidth:.19,
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
    }],        buttonAlign : "right",
	buttons: [ 
	          newODFForm,
        {
        text: '确定',
        //定义表单提交事件
        handler: function(){
            if (formPanel.form.isValid()) {
	            var odfNos = new Object();
	        	var odfList = new Array();
	            for(var i=0;i<=id;i++){
	             	var odf = {
            			"odfNo":Ext.getCmp('odfNo_'+i).getValue(), 
						"fiberNo":Ext.getCmp('fiberNo_'+i).getValue(),
						"useable":Ext.getCmp('useable_'+i).getValue(),
						"note":Ext.getCmp('note_'+i).getValue(),
						"cableId":Ext.getCmp('cableName_'+i).getValue()
	            	};
	             	
			    	var odfNo = Ext.getCmp('odfNo_'+i).getValue();
			    	if(!odfNos[odfNo]){
			    		odfNos[odfNo]=1;
			    	}else{
			    		Ext.Msg.alert('信息','ODF端子号不能重复！');
			    		return ;
			    	}
	             	odfList.push(Ext.encode(odf)); 
	            }  
        		var jsonData = {
        			"dataList" : odfList,
        			"RESOURCE_ROOM_ID":roomId
        		}; 
		    	Ext.Ajax.request({ 
		    		url : 'resource-dframe!addODF.action',
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
			    				var win = parent.Ext.getCmp('addOdfWindow');
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
        	var win = parent.Ext.getCmp('addOdfWindow');
			if(win){
				win.close();
			}
        }
    }]
}); 
 
//增加条目：“*为必填项”
//var tipPanel = new Ext.FormPanel({
//	region:"center",
//    frame:false,
//    border:false,
//	items: [{
//		xtype:'label',
//		html:'<font color=red size = 2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*为必填项</font>'
//	}]
//});

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
