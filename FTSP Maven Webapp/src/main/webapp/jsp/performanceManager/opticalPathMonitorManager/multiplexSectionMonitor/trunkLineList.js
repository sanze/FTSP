/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var emsStore = new Ext.data.Store({
	url : 'getConnectionList.action',
	baseParams : {
		"connectInfoModel.connectionType" : "0",
		"connectInfoModel.needToShowAll" : true
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsConnectionId", "displayName", "emsConnectionName", "type",
			"emsConTypeName", "ip", "port", "intervalTime", "timeOut" ])
});
emsStore.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '加载失败！');
		}
	}
});

var store = new Ext.data.Store({
	// 1代表查询corba连接
	url : 'getTrunkLineListNew.action',

	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "trunkLineId", "netWorkName", "netWorkTypeName", "displayName",
			"trunkLineName" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var columnModel = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true,
		forceFit : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'trunkLineId',
		header : 'id',
		dataIndex : 'trunkLineId',
		hidden : true
	}, {
		id : 'netWorkName',
		header : '网络',
		width : 300,
		dataIndex : 'netWorkName'/*
									 * , editor: new Ext.form.TextField({
									 * allowBlank: false })
									 */
	}, {
		id : 'netWorkTypeName',
		header : '网络类型',
		width : 300,
		dataIndex : 'netWorkTypeName'/*
										 * , editor: new Ext.form.TextField({
										 * allowBlank: false })
										 */
	}, {
		id : 'displayName',
		header : '干线名称',
		width : 300,
		dataIndex : 'trunkLineName',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	} ]
});

var trunkLineListPanel = new Ext.grid.EditorGridPanel({
	id : "trunkLineListPanel",
	region : "center",
	stripeRows : true,
	autoScroll : true,
	frame : false,
	cm : columnModel,
	store : store,
	loadMask : true,
	clicksToEdit : 2,// 设置点击几次才可编辑
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	// viewConfig: {
	// forceFit:true
	// },
	bbar : pageTool,
	tbar : [ "网管分组:", {
		xtype : 'combo',
		id : 'emsGroup',
		name : 'emsGroup',
		mode : 'local',
		store : new Ext.data.ArrayStore({
			id : 0,
			fields : [ 'myId', 'displayText' ],
			data : [ [ 1, 'item1' ], [ 2, 'item2' ] ]
		}),
		valueField : 'myId',
		displayField : 'displayText',
		width : 100,
		triggerAction : 'all'
	}, "网管:", {
		xtype : 'combo',
		id : 'ems',
		name : 'ems',
		fieldLabel : '网络',
		store : emsStore,
		displayField : "displayName",
		valueField : 'emsConnectionId',
		triggerAction : 'all',
		width : 100,
		listeners : {
			select : function(combo, record, index) {
				var emsConnectionId = Ext.getCmp('ems').getValue();
				// 加载网元同步列表
				var jsonData = {
					"sectionWaveModel.emsConnectionId" : emsConnectionId,
					"pageSize" : 200
				};
				store.proxy = new Ext.data.HttpProxy({
					url : 'getTrunkLineListNew.action'
				});
				store.baseParams = jsonData;
				store.load({
					callback : function(r, options, success) {
						if (success) {

						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
			}
		}
	}, "-", {
		text : '新增',
		icon : '../../../../resource/images/btnImages/add.png',
		handler : function() {
			addTrunkLine();
		}
	}, {
		text : '删除',
		icon : '../../../../resource/images/btnImages/delete.png',
		handler : function() {
			deleteTrunkLines();
		}
	}, {
		text : '保存',
		icon : '../../../../resource/images/btnImages/disk.png',
		handler : function() {
			modifyTrunkLines();
		}
	} ]

});

// 增加干线信息
function addTrunkLine() {
	var addTrunkLineWindow = new Ext.Window(
			{
				id : 'addTrunkLineWindow',
				title : '干线信息',
				width : 400,
				height : 200,
				isTopContainer : true,
				modal : true,
				autoScroll : true,
				html : '<iframe src = "addTrunkLine.jsp" height="100%" width="100%" frameBorder=0 border=0/>'
			});
	addTrunkLineWindow.show();
}

// 保存
function modifyTrunkLines() {
	var records = store.getModifiedRecords();
	var jsonString = new Array();
	for ( var i = 0; i < records.length; i++) {
		var TrunkLineModel = {
			"trunkLineId" : records[i].get("trunkLineId"),
			"trunkLineName" : records[i].get("trunkLineName")
		};
		jsonString.push(TrunkLineModel);
	}
	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};

	Ext.Msg.confirm('提示', '确认修改？', function(btn) {
		if (btn == 'yes') {
			top.Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
				url : 'modifyTrunkLines.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					// 提交修改，不然store.getModifiedRecords();数据会累加
					store.commitChanges();
					top.Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("信息", obj.returnMessage, function(r) {
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					});
				},
				error : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				}
			});
		} else {

		}
	});
}

// 删除干线信息
function deleteTrunkLines() {
	var jsonString = new Array();
	var records = trunkLineListPanel.getSelectionModel().getSelections();
	if (records.length > 0) {
		for ( var i = 0; i < records.length; i++) {
			var TrunkLineModel = {
				"trunkLineId" : records[i].get("trunkLineId"),
				"trunkLineName" : records[i].get("trunkLineName")
			};
			jsonString.push(TrunkLineModel);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		Ext.Msg.confirm('提示', '此操作将同时删除干线下的所有光缆信息，确认删除？', function(btn) {
			if (btn == 'yes') {
				top.Ext.getBody().mask('正在执行，请稍候...');
				Ext.Ajax.request({
					url : 'deleteTrunkLines.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						top.Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("信息", obj.returnMessage, function(r) {
							// 刷新列表
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						});
					},
					error : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});
			} else {

			}
		});
	} else {
		Ext.Msg.alert("提示", "请选择需要删除的干线信息！");
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../../../resource/ext/resources/images/default/s.gif";
	// Ext.Ajax.timeout=900000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ trunkLineListPanel ]
	});

	var jsonData = {
		"pageSize" : 200
	};

	store.proxy = new Ext.data.HttpProxy({
		url : 'getTrunkLineListNew.action'
	});
	store.baseParams = jsonData;
	store.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});