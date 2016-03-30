/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var id = 0;
var modeData = [ [ '自动', '0'], 
				 [ '手动', '1']];
var modeStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'displayName'
	}, {
		name : 'mode'
	} ]
});
modeStore.loadData(modeData);

// 添加按钮
var newNeForm = new Ext.Action({
    icon:'../../resource/images/btnImages/add.png',
    text: '新增',
    handler: function(){
    	id++;
        //添加新的网元输入框
        var neForm = new Ext.Panel({
            //column布局控件开始                
            id: 'ne_' + id,
            layout: 'column',
            border: false,
            items:[{
	            columnWidth:.225,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 60,
	            items: [{
			    	xtype: 'textfield',
			    	id:'neName_'+id,
			        name: 'neName_'+id,
			        fieldLabel: '网元名称',
		            sideText : '<font color=red>*</font>',
			        allowBlank:false
		        }]
	        },{
	            columnWidth:.225,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 60,
	            items: [{
			    	xtype: 'textfield',
			    	id:'userName_'+id,
			        name: 'userName_'+id,
			        fieldLabel: '登录用户',
		            sideText : '<font color=red>*</font>',
			        allowBlank:false
		        }]
	        },{
	            columnWidth:.225,
	            layout: 'form',
	            border:false,
	            labelSeparator:"：",
	            labelWidth: 60,
	            items: [{
		            xtype: 'textfield',
		            id:'password_'+id,
		            name: 'password_'+id,
		            inputType: 'password',
			        fieldLabel: '用户密码',
		            sideText : '<font color=red>*</font>',
			        allowBlank:false
		        }]
            },{
            	columnWidth: .225,
                layout: 'form',
                border: false,
                labelSeparator:"：",
	            labelWidth: 60,
                items: [{
                	xtype: 'combo',
			        id:'connectionMode_'+id,
			        name: 'connectionMode_'+id,
			        fieldLabel: '连接模式',
			        store:modeStore,
			        mode:"local",
			        displayField:"displayName",
					valueField:'mode',
			        triggerAction: 'all',
			        editable:false,
			        allowBlank:false,
			        sideText : '<font color=red>*</font>',
			        width:130
                }]
            },{
                columnWidth: .1,
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
                        var object = Ext.getCmp('ne_' + del_id);
                        //删除一行
                        formPanel.remove(object, true);
                    }
                }]
            }]
        });
        //添加fieldSet
        formPanel.add(neForm);
        //重新刷新
        formPanel.doLayout();
    }
});
 
 var formPanel = new Ext.FormPanel({
	region:"center",
    frame:false,
    border:false,
	bodyStyle:'padding:10px 10px 0',
//    autoHeight:true,
    labelAlign: 'right',
//    collapsed: false,   // initially collapse the group
//    collapsible: false,
//    collapseMode: 'mini',
	autoScroll:true,
//    split:true,
	items: [{
        layout:'column',
        border:false,
        items:[{
            columnWidth:.225,
            layout: 'form',
            border:false,
            labelWidth: 60,
            labelSeparator:"：",
            items: [{
		    	xtype: 'textfield',
		    	id:'neName_0',
		        name: 'neName_0',
		        fieldLabel: '网元名称',
	            sideText : '<font color=red>*</font>',
		        allowBlank:false
	        }]
        },{
            columnWidth:.225,
            layout: 'form',
            border:false,
            labelWidth: 60,
            labelSeparator:"：",
            items: [{
		    	xtype: 'textfield',
		    	id:'userName_0',
		        name: 'userName_0',
		        fieldLabel: '登录用户',
	            sideText : '<font color=red>*</font>',
		        allowBlank:false
	        }]
        },{
            columnWidth:.225,
            layout: 'form',
            border:false,
            labelWidth: 60,
            labelSeparator:"：",
            items: [{
	            xtype: 'textfield',
	            id:'password_0',
	            name: 'password_0',
	            inputType: 'password',
		        fieldLabel: '登录密码',
	            sideText : '<font color=red>*</font>',
		        allowBlank:false
	        }]
        },{
            	columnWidth: .225,
                layout: 'form',
                border: false,
                labelSeparator:"：",
	            labelWidth: 60,
                items: [{
                	xtype: 'combo',
			        id:'connectionMode_'+0,
			        name: 'connectionMode_'+0,
			        fieldLabel: '连接模式',
			        store:modeStore,
			        mode:"local",
			        displayField:"displayName",
					valueField:'mode',
			        triggerAction: 'all',
			        editable:false,
			        allowBlank:false,
			        sideText : '<font color=red>*</font>',
			        width:130
                }]
            }]
    }],
    tbar: [newNeForm],
	buttons: [{
        text: '确定',
        //定义表单提交事件
        handler: function(){
        	alert(formPanel.form.isValid());
            if (formPanel.form.isValid()) {
	            var jsonString=new Array();
	            var j = 0;
	            for(var i=0;i<=id;i++){
	            	var neModel = {
						"displayName":Ext.getCmp('neName_'+i).getValue(),
						"userName":Ext.getCmp('userName_'+i).getValue(),
						"password":Ext.getCmp('password_'+i).getValue(),
						"connectionMode":Ext.getCmp('connectionMode_'+i).getValue(),
						"emsConnectionId":emsConnectionId
			    	};
					jsonString.push(Ext.encode(neModel));
					
	            }
	          
	            
		        var jsonData = {
		        	"jsonString":jsonString
		    	};
                Ext.getBody().mask('正在执行，请稍候...');
		    	Ext.Ajax.request({ 
				    url: 'connection!addTelnetNe.action', 
				    method : 'POST',
				    params: jsonData,
				    success: function(response) {
				    	Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if(obj.returnResult == 0){
			            	Ext.Msg.alert("信息",obj.returnMessage, function(r){
			            		//刷新列表
			            		var pageTool = parent.Ext.getCmp('pageTool');
			            		if(pageTool){
			    					pageTool.doLoad(pageTool.cursor);
			    				}
			            		//关闭修改任务信息窗口
			    				var win = parent.Ext.getCmp('addTelnetNeWindow');
			    				if(win){
			    					win.close();
			    				}
			    			});
						}
						if(obj.returnResult == 1){
	            			Ext.Msg.alert("信息",obj.returnMessage);
	            		}
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
            }
        }
    }, {
        text: '取消',
        handler: function(){
            //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('addTelnetNeWindow');
			if(win){
				win.close();
			}
        }
    }]
});
 

Ext.onReady(function(){
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 

    var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [formPanel]
	});
	win.show();	
 });
