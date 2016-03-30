var store = new Ext.data.Store(
{
	//1代表查询corba连接
	/*url: 'connection!topoLinkSyncReturnInfo.action',
	baseParams:{"emsConnectionModel.emsConnectionId" :emsConnectionId,
		"emsConnectionModel.emsGroupId" :emsGroupId},*/
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    "emsConnectionId","emsDisplayName","neDisplayName","neSerialNo","neId","suportRates"
    ])
});

 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit: false
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),{
            id: 'emsConnectionId',
            header: 'emsConnectionId',
            dataIndex: 'emsConnectionId',
            hidden:true
        },{
            id: 'neId',
            header: '网元编号',
            dataIndex: 'neId',
            hidden:true
        },{
            id: 'suportRates',
            header: '连接速率',
            dataIndex: 'suportRates',
            hidden:true
        },{
            id: 'emsDisplayName',
            header: '网管名称',
            width:120,
            dataIndex: 'emsDisplayName'
//            ,editor: new Ext.form.TextField({
//                allowBlank: false
//            })
        },{
            id: 'neDisplayName',
            header: '网元名称',
            width:160,
            dataIndex: 'neDisplayName'
        }]
    });

var connectListPanel = new Ext.grid.GridPanel({
	id:"connectListPanel",
	region:"center",
	title : "链路同步部分成功，需要先同步以下网元基础数据",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,	
	viewConfig: {
        forceFit:false
    },
	buttons : [ {
		text : '立即同步网元',
		handler : function() {
			syncNeList();
		}
	}, {
		text : '取消',
		handler : function() {
			//关闭修改任务信息窗口
			var win = parent.Ext.getCmp('topoLinkSyncNeWindow');
			if (win) {
				win.close();
			}
		}
	} ]
}); 

//var formPanel = new Ext.FormPanel({
//	id : 'formPanel',
//	border : true,
////	title : "新增网管分组",
//	region : 'center',
//	labelWidth : 100,
//	autoScroll : true,
//	bodyStyle : 'padding:10px 12px 0;',
//	items : [ connectListPanel ],
//	buttons : [ {
//		text : '立即同步网元',
//		handler : function() {
//			syncNeList();
//		}
//	}, {
//		text : '取消',
//		handler : function() {
//			//关闭修改任务信息窗口
//			var win = parent.Ext.getCmp('topoLinkSyncNeWindow');
//			if (win) {
//				win.close();
//			}
//		}
//	} ]
//});

function syncNeList() {
	var processKey = "syncNeList"+new Date().getTime();
	var jString = new Array();
	for ( var i = 0; i < store.getCount(); i++) {
		
		var neModel = {
			"emsConnectionId" : store.getAt(i).get('emsConnectionId'),//store.getById(i).get('emsConnectionId'),//cell[i].get('emsConnectionId'),
			"neId" : store.getAt(i).get('neId'),
			"suportRates" : store.getAt(i).get('suportRates'),
			"displayName" : store.getAt(i).get('neDisplayName'),
			"syncName" : processKey
		};
//		store.each(function(record) {alert(record.get('name')); });  
	
		jString.push(neModel);
	}
	var jsonData = {
		"jString" : Ext.encode(jString)
	};

	Ext.Msg.confirm('提示', '同步网元将从网管同步网元的基础数<br>据，如果选择网元数量较多，时间可能<br>较长！是否确认同步？',
			function(btn) {
				if (btn == 'yes') {
//					top.Msg.show(processMessageconfig);
					// Ext.getBody().mask('正在执行，请稍候...');
					Ext.Ajax.request({
						url : 'connection!syncSelectedNe.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {
//							Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);
							if (obj.returnResult == 1) {
								Ext.Msg.alert("信息", obj.returnMessage);
								// 刷新列表
								var pageTool = parent.Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								//关闭修改任务信息窗口
								var win = parent.Ext.getCmp('topoLinkSyncNeWindow');
								if (win) {
									win.close();
								}
							}
							if (obj.returnResult == 0) {
								Ext.Msg.alert("信息", obj.returnMessage);
							}
						},
						error : function(response) {
//							 Ext.getBody().unmask();
							 clearTimer();
							Ext.Msg.hide();
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("错误", obj.returnMessage);
						},
						failure : function(response) {
//							 Ext.getBody().unmask();
							 clearTimer();
							Ext.Msg.hide();
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("错误", obj.returnMessage);
						}
					});
					showProcessBar(processKey); 
				} else {

				}
			});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
//	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 360000000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ connectListPanel ],
		renderTo : Ext.getBody()
	});
	
	//放最后才能显示遮罩效果
	if(parent){
		if(parent.subFrameData){
			store.loadData(parent.subFrameData);
		}
	}
});