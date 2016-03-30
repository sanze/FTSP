var minutesStore=[
	['10','10 分钟'],
	['20','20 分钟'],
	['30','30 分钟'],
	['40','40 分钟'],
	['50','50 分钟'],
	['60','60 分钟'],
	['70','70 分钟'],
	['80','80 分钟'],
	['90','90 分钟']
];
var store_minutes=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'minutes'}
	 ]
});
store_minutes.loadData(minutesStore);

var minutesCombo = new Ext.form.ComboBox({
	id : 'minutesCombo',
	name : 'minutesCombo',
	fieldLabel : '暂停时间',
	emptyText:'请选择暂停时间',
	triggerAction : 'all',
	store : minutesStore,
	valueField : 'value',
	displayField : 'minutes',
	allowBlank : true,
	editable : false,
	width : 120
});

var formPanel = new Ext.FormPanel({
	region : "center",
	frame : false,
	bodyStyle : 'padding:20px 10px 0',
	labelWidth : 100,
	labelAlign : 'right',
	autoScroll : true,
	items : [ minutesCombo ],
	buttons : [ {
		text : '保存',
		icon : '../../resource/images/btnImages/disk.png',
		handler : save
	} ]
});

function save() {
	if (formPanel.getForm().isValid()) {
		var minutes = Ext.getCmp("minutesCombo").getValue();

		var jsonData = {
			"emsConnectionModel.emsConnectionId" : emsConnectionId,
			"emsConnectionModel.collectStatus" : level,
			"minutes":minutes
		};
		Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url : 'connection!updateCollectStatus.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				Ext.getBody().unmask();
				// 刷新列表
				var pageTool = parent.Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
	            //关闭修改任务信息窗口
				var win = parent.Ext.getCmp('pauseMinutesWindow');
				if(win){
					win.close();
				}
				
//				var jsonData = {
//						"emsConnectionModel.emsConnectionId" : emsConnectionId
//					};
////				alert(emsConnectionId);
////					Ext.getBody().mask('正在执行，请稍候...');
//					Ext.Ajax.request({
//								url : 'connection!timeUpdateCollectStatus.action',
//								method : 'POST',
//								params : jsonData,
//								success : function(response) {
//									Ext.getBody()
//											.unmask();
//									var obj = Ext.decode(response.responseText);
//									if (obj.returnResult == 1) {
////										Ext.Msg.alert("信息", obj.returnMessage);
//										// 刷新列表
//										var pageTool = Ext.getCmp('pageTool');
//										if (pageTool) {
//											pageTool.doLoad(pageTool.cursor);
//										}
//									}
//
//								},
//								error : function(response) {
//									Ext.getBody().unmask();
//									Ext.Msg.alert("错误",response.responseText);
//								},
//								failure : function(response) {
//									Ext.getBody().unmask();
//									Ext.Msg.alert("错误",response.responseText);
//								}
//							});
				
			},
			error : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", obj.returnMessage);
			},
			failure : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误", obj.returnMessage);
			}
		});
	}
} 

function initData(){
	// just for demo
	formPanel.getForm().reset();
	
}


Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};

	var win = new Ext.Viewport({
		id : 'win',
		loadMask : true,
		layout : 'border',
		items : [ formPanel ],
		renderTo : Ext.getBody()
	});
	initData();
});