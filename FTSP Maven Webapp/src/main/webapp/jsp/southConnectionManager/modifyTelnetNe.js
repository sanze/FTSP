/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

//var modeitems=[
//    {boxLabel: '自动', id: 'modeAuto', name: 'mode'},
//    {boxLabel: '手动', id: 'modeManual', name: 'mode'}
//];
var emsConnectionId;
var modeData=[
	['0','自动'],
	['1','手动']
];

var modeStore=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'connectionMode'}
	 ]
});
modeStore.loadData(modeData);

//连接模式
var mode = new Ext.form.ComboBox({
	id:'connectMode',
	name: 'connectMode',
	fieldLabel: '连接模式',
    typeAhead: true,
    triggerAction: 'all',
    lazyRender:true,
    mode: 'local',
    store: modeStore,
    valueField: 'value',
    displayField: 'connectionMode',
    allowBlank:true,
	editable:false,
	anchor: '95%'
});


var neName = new Ext.form.TextField({
	id:'neName',
    name: 'neName',
    fieldLabel: '网元名称',
    allowBlank:false,
//    disabled:true,
    anchor: '95%'
});

var userName = new Ext.form.TextField({
	id:'userName',
    name: 'userName',
    fieldLabel: '登录用户',
    allowBlank:false,
    anchor: '95%'
});

var password=new Ext.form.TextField({
	xtype: 'textfield',
	id:'password',
	name: 'password',
	inputType: 'password',
	fieldLabel: '密  码',
	emptyText:'请输入登录密码',
	allowBlank:false,
    maxLength: 30,
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
    items: [
            neName,
    userName,
    password,
   	mode
    ],
    buttons: [{
        text: '确定',
        handler: function(){
        	saveConfig();
        }
     },{
        text: '取消',
        handler: function(){
            //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('modifyTelnetNeWindow');
			if(win){
				win.close();
			}
        }
    }]
});

// 保存配置
function saveConfig() {
	if (formPanel.getForm().isValid()) {
		var neName = Ext.getCmp("neName").getValue();
		var userName = Ext.getCmp("userName").getValue();
		var password = Ext.getCmp("password").getValue();
		var mode = Ext.getCmp("connectMode").getValue();
		alert(mode);
		Ext.getBody().mask('正在执行，请稍候...');
		// 修改网元信息
		var jsonData = {
			"neModel.neId" : neId,
			"neModel.emsConnectionId" : emsConnectionId,
			"neModel.displayName" : neName,
			"neModel.userName" : userName,
			"neModel.password" : password,
			"neModel.connectionMode" : mode
		};
		Ext.Ajax.request({
			url : 'connection!modifyTelnetNe.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				Ext.getBody().unmask();
				Ext.Msg.alert("信息", obj.returnMessage, function(r) {
					if (obj.returnResult == 1) {
						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						// 关闭修改任务信息窗口
						var win = parent.Ext.getCmp('modifyTelnetNeWindow');
						if (win) {
							win.close();
						}
					}
				});
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


function initData(neId){
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
	    	Ext.getCmp("neName").setValue(obj.neName);
	    	Ext.getCmp("userName").setValue(obj.userName);
	    	Ext.getCmp("password").setValue(obj.password);
	    	Ext.getCmp("connectMode").setValue(obj.connectionMode);
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
 	Ext.Msg = top.Ext.Msg; 
 	
 	initData(neId);
 	
  	var win = new Ext.Viewport({
        id:'win',
        layout : 'border',
		items : [
		  formPanel
		],
		renderTo : Ext.getBody()
	});
	
 });