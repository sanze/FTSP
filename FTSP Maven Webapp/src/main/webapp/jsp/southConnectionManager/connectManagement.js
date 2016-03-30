/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
//var myData = {"total":"3","rows":[
//	{"connectServer":"接入1","connectStatus":"启动","ip":"192.168.0.1","remarks":"接入服务器1的"},
//	{"connectServer":"接入2","connectStatus":"停止","ip":"192.168.0.2","remarks":"接入服务器2的"},
//	{"connectServer":"接入3","connectStatus":"停止","ip":"192.168.0.3","remarks":"接入服务器3的"}
//]};
// 
//var store = new Ext.data.Store(
//{
//	//1代表查询corba连接
//	//url: 'getConnectionList.action',
//	baseParams:{"connectInfoModel.connectionType":"1"},
//	reader: new Ext.data.JsonReader({
//        totalProperty: 'total',
//		root : "rows"
//    },[
//	    "connectServer","connectStatus","ip","remarks"
//    ])
//});
//
//store.loadData(myData);
var store = new Ext.data.Store({
	url : 'connection!getSysServiceRecord.action',
	baseParams : {
		"sysSvcRecordId" : "-1",
		"limit" : 200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "SYS_SVC_RECORD_ID", "SERVICE_NAME", "IP", "PORT", "STATUS", "NOTE" ])
});

store.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '接入服务器查询失败！请重新查询');
		}
	}
});

var data_status = [ [ '1', '启动' ], [ '2', '停止' ] ];
var store_status = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
store_status.loadData(data_status);

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true,
	header : ""
});
var columnModel = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true,
		forceFit : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'SYS_SVC_RECORD_ID',
		header : 'id',
		dataIndex : 'SYS_SVC_RECORD_ID',
		hidden : true
	}, {
		id : 'connectServer',
		header : '接入服务器',
		width : 140,
		dataIndex : 'SERVICE_NAME'
	}, {
		id : 'ip',
		header : 'IP地址',
		dataIndex : 'IP',
		width : 100
	}, {
		id : 'port',
		header : '端口号',
		dataIndex : 'PORT',
		width : 100
	}, 
	{
		id : 'status',
		header : '状态',
		dataIndex : 'STATUS',
		width : 100,
		renderer : function(v, m, t) {
			var display = "";
			switch(v){
			case 1:
//				m.css='x-grid-font-orange';
				display = "连接正常";
				break;
			case 2:
				m.css='x-grid-font-orange';
				display = "连接异常";
				break;
			case 3:
				m.css='x-grid-font-red';
				display = "网络中断";
				break;
			}
			return display;
		}
	}, 
	{
		id : 'remarks',
		header : '备注',
		dataIndex : 'NOTE',
		width : 250
	} ]
});

function transDomainName(v) {
	if (v == 1)
		return "启动";
	if (v == 2)
		return "停止";
}

var connectListPanel = new Ext.grid.EditorGridPanel({
	id : "connectListPanel",
	name :"connectListPanel",
	region : "center",
	stripeRows : true,
	autoScroll : true,
	frame : false,
	cm : columnModel,
	store : store,
	loadMask : true,
	clicksToEdit : 2,//设置点击几次才可编辑  
	selModel : checkboxSelectionModel, //必须加不然不能选checkbox 
	viewConfig : {
		forceFit : false
	},
	bbar : pageTool,
	tbar : ['-',  {
		text : '新增',
		privilege:addAuth,
		icon : '../../resource/images/btnImages/add.png',
		handler : function() {
			addSysServiceRecord();
		}
	}, {
		text : '删除',
		privilege:delAuth,
		icon : '../../resource/images/btnImages/delete.png',
		handler : function() {
			deleteSysServiceRecord();
		}
	},{
		text : '修改',
		privilege:modAuth,
		icon : '../../resource/images/btnImages/modify.png',
		handler : function() {
			saveSysServiceRecord();
		}
	} ]

});

//启动连接
function addSysServiceRecord() {
	var addServiceWindow = new Ext.Window(
			{
				id : 'addServiceWindow',
				title : '新增接入服务器',
				width : 400,
				height : 220,
				isTopContainer : true,
				modal : true,
				autoScroll : true,
				html : '<iframe id="addSysService_panel" name = "addSysService_panel" src = "addSysService.jsp" height="100%" width="100%" frameBorder=0 border=0/>'
			});
	addServiceWindow.show();
}

//删除接入服务器
function deleteSysServiceRecord() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
			var jsonData = {
				"sysServiceModel.sysSvcRecordId" : cell[0].get("SYS_SVC_RECORD_ID")
			};
					Ext.getBody().mask('正在执行，请稍候...');
					Ext.Ajax.request({
						url : 'connection!getSouthConnectionListBySysServiceId.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {
							Ext.getBody().unmask();
							var obj = Ext.decode(response.responseText);
							if(obj.total > 0) {
								Ext.Msg.alert("提示", "接入服务器存在南向连接，请删除或者转移连接后再删除接入服务器。");
							} else {
								Ext.Ajax.request({
									url : 'connection!deleteSysService.action',
									method : 'POST',
									params : jsonData,
									success : function(response) {
										Ext.getBody().unmask();
										var obj = Ext.decode(response.responseText);
										Ext.Msg.alert("信息", obj.returnMessage, function(r) {
											//刷新列表
											var pageTool = Ext.getCmp('pageTool');
											if (pageTool) {
												pageTool.doLoad(pageTool.cursor);
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
				
		
	} else {
		Ext.Msg.alert("提示", "请选择接入服务器！");
	}
}

//保存接入服务器
function saveSysServiceRecord() {
	var cell = connectListPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var sysSvcRecordId = cell[0].get("SYS_SVC_RECORD_ID");
		var url = 'modifySysService.jsp?sysSvcRecordId=' + sysSvcRecordId;

		var modifySysServiceWindow = new Ext.Window(
				{
					id : 'modifySysServiceWindow',
					title : '修改接入服务器',
					width : 400,
					height : 200,
					isTopContainer : true,
					modal : true,
					autoScroll : true,
					html : '<iframe  id="modifySysService_panel" name = "modifySysService_panel"  src = '
							+ url
							+ ' height="100%" width="100%" frameBorder=0 border=0/>'
				});
		modifySysServiceWindow.show();

	} else {
		Ext.Msg.alert("提示", "请选择需要修改的接入服务器，每次选择一条！");
	}
}

function colorGrid(v, m) {
	if (v == '连接正常') {
		m.css = 'x-grid-font-blue';
	} else if (v == '网络中断') {
		m.css = 'x-grid-font-red';
	} else if (v == '连接异常') {
		m.css = 'x-grid-font-orange';
	}
	return v;
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	//collapse menu
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();}
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ connectListPanel ]
	});

	//放最后才能显示遮罩效果
	store.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});