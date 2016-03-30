/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var neName = new Ext.form.TextField({
	id:'userLabel',
    name: 'userLabel',
    fieldLabel: '规范网元名称',
    allowBlank:false,
//    disabled:true,
    anchor: '95%'
});

var formPanel = new Ext.FormPanel({
	id:'formPanel',
	region:"center",
	//title:'修改网元设置',
    border:false,
    frame:false,
	autoScroll:true,
    labelWidth: 100,
    width: 200,
    bodyStyle: 'padding:10px 12px 0;',
    items: [ neName,{
		fieldLabel : '同步模式',
		id : 'SYNC_MODE',
		xtype : 'radiogroup',
		columns : 2,
		items : [ {
			boxLabel : "手动同步",
			name : 'mode',
			inputValue : 1,
			checked : true
		}, {
			boxLabel : "自动同步",
			name : 'mode',
			inputValue : 2
		} ]
	}],
    buttons: [{
        text: '确定',
        handler: function(){
        	saveConfig();
        }
     },{
        text: '取消',
        handler: function(){
            //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('modifyCorbaNeWindow');
			if(win){
				win.close();
			}
        }
    }]
});

// 保存配置
function saveConfig() {
	if (formPanel.getForm().isValid()) {
		var userLabel = Ext.getCmp("userLabel").getValue();
		var syncMode = Ext.getCmp("SYNC_MODE").getValue().inputValue;
		Ext.getBody().mask('正在执行，请稍候...');
		// 修改网元信息
		var jsonData = {
			"neModel.neId" : neId,
			"neModel.emsConnectionId" : emsConnectionId,
			"neModel.userLabel" : userLabel,
			"neModel.syncMode" : syncMode
		};
		Ext.Ajax.request({
			url : 'connection!modifyCorbaNe.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 1) {						
						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						// 关闭修改任务信息窗口
						var win = parent.Ext.getCmp('modifyCorbaNeWindow');
						if (win) {
							win.close();
						}
					}
					if (obj.returnResult == 0) {
						Ext.Msg.alert("信息",obj.returnMessage);
					}
				
			},
			error : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		});
	}
}


function initData(neId,emsConnectionId){
	var jsonData = {
		"neModel.neId":neId,
		"neModel.emsConnectionId":emsConnectionId
	};
	Ext.Ajax.request({
	    url: 'connection!getTelnetNeByNeId.action', 
	    method : 'POST',
	    params: jsonData,
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	//使用归一化名称
	    	Ext.getCmp("userLabel").setValue(obj.USER_LABEL);
	    	Ext.getCmp('SYNC_MODE').setValue(obj.syncMode);
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
    
 Ext.onReady(function(){
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
// 	Ext.Msg = top.Ext.Msg; 
 	
 	initData(neId,emsConnectionId);
 	
  	var win = new Ext.Viewport({
        id:'win',
        layout : 'border',
		items : [
		  formPanel
		],
		renderTo : Ext.getBody()
	});
	
 });