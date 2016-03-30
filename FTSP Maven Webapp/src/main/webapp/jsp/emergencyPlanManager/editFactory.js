var formPanel = new Ext.FormPanel({
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
			items:[{
				id:'FACTORY',
				xtype:'textfield',
				fieldLabel:'设 &nbsp&nbsp备&nbsp&nbsp厂&nbsp&nbsp 家',
				sideText:'<font color=red>*</font>',
				allowBlank:false,
				anchor : '75%' 
			},{
				id:'CONTACT_PERSON',
				xtype:'textfield', 
				fieldLabel:'技术支持联系人', 
				anchor : '75%' 
			},{
				id:'TEL',
				xtype:'textfield', 
				fieldLabel:'联&nbsp&nbsp系&nbsp&nbsp方&nbsp&nbsp式', 
				anchor : '75%' 
			},{
				id:'AREA',
				xtype:'textfield', 
				fieldLabel:'服&nbsp&nbsp务&nbsp&nbsp区&nbsp&nbsp域', 
				anchor : '75%' 
			},{
				id:'ADDRESS',
				xtype:'textfield', 
				fieldLabel:'厂&nbsp&nbsp家&nbsp&nbsp地&nbsp&nbsp址', 
				anchor : '100%' 
			},{
				id:'HOT_LINE',
				xtype:'textfield', 
				fieldLabel:'厂家服务热线', 
				anchor : '75%' 
			},{
				id:'NOTE',
				xtype:'textfield', 
				fieldLabel:'备&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp注', 
				anchor : '75%' 
			}] 
		}]
	}],
	buttons : [ {
		text : '确定',
		handler : function() { 
			modifyFactoryContact();
		}
	}, {
		text : '取消 ',
		handler : function() {
			var win = parent.Ext.getCmp('editWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});   

function initData(){
	var jsonData = {
		"FAULT_FACTORY_CONTACT_ID":factoryContactId
	};
	var jsonString = Ext.encode(jsonData);
    Ext.Ajax.request({ 
		url: 'emergency-plan!initFactoryContact.action',
		method : 'POST',
		params: {"jsonString":jsonString},
		success: function(response) {
		    var obj = Ext.decode(response.responseText);
		    Ext.getCmp('FACTORY').setValue(obj.FACTORY);
		    Ext.getCmp('CONTACT_PERSON').setValue(obj.CONTACT_PERSON);
		    Ext.getCmp('TEL').setValue(obj.TEL);
		    Ext.getCmp('AREA').setValue(obj.AREA);
		    Ext.getCmp('ADDRESS').setValue(obj.ADDRESS);
		    Ext.getCmp('HOT_LINE').setValue(obj.HOT_LINE);
		    Ext.getCmp('NOTE').setValue(obj.NOTE);
		},
		error:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		},
		failure:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		}
	});
}

function modifyFactoryContact(){
	
	if(formPanel.getForm().isValid()){
		var jsonData = {
			"editType":editType,
			"FAULT_FACTORY_CONTACT_ID":factoryContactId,
			"FACTORY":Ext.getCmp('FACTORY').getValue(),
			"CONTACT_PERSON":Ext.getCmp('CONTACT_PERSON').getValue(),
			"TEL":Ext.getCmp('TEL').getValue(),
			"AREA":Ext.getCmp('AREA').getValue(),
			"ADDRESS":Ext.getCmp('ADDRESS').getValue(),
			"HOT_LINE":Ext.getCmp('HOT_LINE').getValue(),
			"NOTE":Ext.getCmp('NOTE').getValue()
		};
		var jsonString = Ext.encode(jsonData);
		Ext.getBody().mask('正在执行，请稍候...');
	    Ext.Ajax.request({ 
			url: 'emergency-plan!modifyFactoryContact.action',
			method : 'POST',
			params: {"jsonString":jsonString},
			success: function(response) {
				Ext.getBody().unmask();
			    var obj = Ext.decode(response.responseText);
			    if(obj.returnResult == 1){
			    	
			    	var message = "新增厂家联系方式成功！";
			    	
			    	if(editType ==1){
			    		message = "修改成功！";
			    	}
			    	
					Ext.Msg.alert("信息", message, function(r) {
						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						//修改模式 关闭窗口
						if(editType ==1){
							// 关闭修改任务信息窗口
							var win = parent.Ext
									.getCmp('editWindow');
							if (win) {
								win.close();
							}
						}else{
							Ext.Msg.confirm('信息', '继续添加？', function(btn) {
								if (btn == 'yes') {
									//重置窗口
									formPanel.getForm().reset();
								} else {
									// 关闭修改任务信息窗口
									var win = parent.Ext
											.getCmp('editWindow');
									if (win) {
										win.close();
									}
								}
							});
						}
					});
	            }
	            if(obj.returnResult == 0){
	            	Ext.Msg.alert("提示",obj.returnMessage);
	            }
			},
			error:function(response) {
			    Ext.getBody().unmask();
	            Ext.Msg.alert("异常",response.responseText);
			},
			failure:function(response) {
			    Ext.getBody().unmask();
	            Ext.Msg.alert("异常",response.responseText);
			}
		});
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ formPanel ]
	});
	win.show();   
	if(editType==1){ 
		//修改预案
		initData();
	} 
});