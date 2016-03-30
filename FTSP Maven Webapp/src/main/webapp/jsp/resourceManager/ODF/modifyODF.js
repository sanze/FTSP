function debug(type, title, msg){
	console[type](title + " - " + msg);
}

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
 
//--------------------------------- 修改ODF架-------------------------------------------

var modifyOdfForm = new Ext.FormPanel({
	id : "modifyOdfForm", 
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
	    	id:'mOdfNo',
	        name: 'mOdfNo',
	        fieldLabel: 'ODF端子号',
	        width: 200,
	        disabled:true,
	        allowBlank:false
        },{
	    	xtype: 'combo',
            id: 'mCableName',
   		    width: 200,
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
		    	  //光缆光纤需同步变化
		    	  Ext.getCmp('mFiberNo').reset();
		    	  
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
        },{
        	xtype: 'combo',
			id:'mFiberNo', 		
			fieldLabel: '光纤芯号',
			store:fiberStore,
			displayField:"FIBER_NO",
			valueField : 'FIBER_NO',
			triggerAction : 'all',
			editable : false,  
		    width: 200
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
            if (modifyOdfForm.form.isValid()) {
            	var jsonData = {
        			"conMap.odfNo":Ext.getCmp('mOdfNo').getValue(), 
        			"conMap.cableId":Ext.getCmp('mCableName').getValue(),
					"conMap.fiberNo":Ext.getCmp('mFiberNo').getValue(),
					"conMap.useable":Ext.getCmp('mUseable').getValue(),
					"conMap.note":Ext.getCmp('mNote').getValue(),
					"conMap.odfId":odfId,
					"conMap.roomId":roomId
            	} ;    
		    	Ext.Ajax.request({ 
		    		url : 'resource-dframe!modifyODF.action',
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
			    				var win = parent.Ext.getCmp('modifyOdfWindow');
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
	    	var win = parent.Ext.getCmp('modifyOdfWindow');
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
			items : [modifyOdfForm]
	}); 
	Ext.getCmp('mOdfNo').setValue(mOdfNo);
	Ext.getCmp('mCableName').setValue(mCableId);
	Ext.getCmp('mCableName').setRawValue(mCableName);
	Ext.getCmp('mFiberNo').setValue(mFiberNo);
	Ext.getCmp('mUseable').setValue(mUseable);
	Ext.getCmp('mNote').setValue(mNote); 
	win.show();	 
 });
