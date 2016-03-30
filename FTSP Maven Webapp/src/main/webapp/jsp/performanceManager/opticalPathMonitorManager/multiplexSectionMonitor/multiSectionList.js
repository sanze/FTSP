/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var trunkLineStore = new Ext.data.Store({
	url : 'getTrunkLineList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "trunkLineId", "displayName" ])
});

trunkLineStore.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '加载失败！');
		}
	}
});

function getTrunkLineData(emsConnectionId) {
	// 重置下拉框
	var path = 'getTrunkLineListByConnId.action?emsConnectionId='
			+ encodeURI(encodeURI(emsConnectionId));
	trunkLineStore.proxy = new Ext.data.HttpProxy({
		url : path
	});
	trunkLineStore.load({
		callback : function(r, options, success) {
			if (success) {
			} else {
				Ext.Msg.alert('错误', '干线信息加载失败！请重新查询');
			}
		}// callback function end
	});
};

// 网管下拉框
var emsStore = new Ext.data.Store({
	url : 'getConnectionList.action',
	baseParams : {
		"connectInfoModel.connectionType" : "0",
		"connectInfoModel.needToShowAll" : true
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsConnectionId", "displayName", "emsConnectionName", "type", "emsConTypeName", "ip",
			"port", "intervalTime", "timeOut" ])
});
emsStore.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '加载失败！');
		}
	}
});

// 干线下拉框
var trunStore = new Ext.data.Store({
	url : 'getTrunkLineListNew.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "trunkLineId", "emsConnectionId", "netWorkName", "netWorkTypeName", "trunkLineName" ])
});
trunStore.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '加载失败！');
		}
	}
});

var store = new Ext.data.Store({
	url : 'getMultiSectionList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsGroupName", "sectionId", "trunkLineId", "emsConnectionId", "displayName",
			"emsConnectionName", "type", "emsConTypeName", "groupName", "sectionName", "direction",
			"standardWave", "actullyWave" ])
});

// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true,
		forceFit : false
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'sectionId',
		header : 'sectionId',
		dataIndex : 'sectionId',
		hidden : true
	}, {
		id : 'emsConnectionId',
		header : 'emsConnectionId',
		dataIndex : 'emsConnectionId',
		hidden : true
	}, {
		id : 'emsGroupName',
		header : '网管分组',
		width : 100,
		dataIndex : 'emsGroupName'
	}, {
		id : 'emsConnectionName',
		header : '网管',
		width : 100,
		dataIndex : 'emsConnectionName'
	}, {
		id : 'emsConTypeName',
		header : '网管类型',
		width : 80,
		dataIndex : 'emsConTypeName'
	}, {
		id : 'trunkLineId',
		header : '干线名称',
		dataIndex : 'trunkLineId',
		width : 150,
		editor : new Ext.form.ComboBox({
			id : "trunkLineEditor",
			store : trunkLineStore,
			editable : false,
			displayField : "displayName",
			valueField : 'trunkLineId',
			triggerAction : 'all',
			resizable: true,
			allowBlank : false
		}),
		renderer : function(value, p, r) {
			var index = trunStore.find(Ext.getCmp('trunkLineEditor').valueField, value);
			var record = trunStore.getAt(index);
			if (record) {
				return record.data.trunkLineName
			} else
				return value;
		}
	}, {
		id : 'sectionName',
		header : '光复用段名称',
		dataIndex : 'sectionName',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	}, {
		id : 'direction',
		header : '方向',
		width : 100,
		dataIndex : 'direction',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	}, {
		id : 'standardWave',
		header : '标称波道数',
		dataIndex : 'standardWave',
		editor : new Ext.form.NumberField({
			allowDecimals : false,
			allowNegative : false,
			minValue : 1,
			allowBlank : false
		})
	}, {
		id : 'actullyWave',
		header : '实际波道数',
		dataIndex : 'actullyWave',
		editor : new Ext.form.NumberField({
			allowDecimals : false,
			allowNegative : false,
			minValue : 1,
			allowBlank : false
		})
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var emsGroupCombo = {
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
};

var emsCombo = {
	xtype : 'combo',
	id : 'ems',
	name : 'ems',
	store : emsStore,
	displayField : "displayName",
	valueField : 'emsConnectionId',
	triggerAction : 'all',
	width : 100,
	listeners : {
		select : function(combo, record, index) {
			var emsConnectionId = Ext.getCmp('ems').getValue();
			Ext.getCmp('trun').setValue("");

			var jsonData_t = {
				"sectionWaveModel.emsConnectionId" : emsConnectionId,
				"pageSize" : 10000
			};
			trunStore.proxy = new Ext.data.HttpProxy({
				url : 'getTrunkLineListNew.action'
			});
			trunStore.baseParams = jsonData_t;
			trunStore.load({
				callback : function(r, options, success) {
					if (success) {

					} else {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
			});
			var jsonData = {
				"sectionWaveModel.emsConnectionId" : emsConnectionId,
				"sectionWaveModel.trunkLineId" : "",
				"pageSize" : 200
			};
			store.proxy = new Ext.data.HttpProxy({
				url : 'getMultiSectionListByEmsId.action'
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
};

var trunkLineCombo = {
	xtype : 'combo',
	id : 'trun',
	name : 'trun',
	store : trunStore,
	displayField : "trunkLineName",
	valueField : 'trunkLineId',
	triggerAction : 'all',
	width : 100,
	listeners : {
		select : function(combo, record, index) {
			var emsConnectionId = Ext.getCmp('ems').getValue();
			var trunkLineId = Ext.getCmp('trun').getValue();

			var jsonData = {
				"sectionWaveModel.emsConnectionId" : emsConnectionId,
				"sectionWaveModel.trunkLineId" : trunkLineId,
				"pageSize" : 200
			};

			store.proxy = new Ext.data.HttpProxy({
				url : 'getMultiSectionListByEmsId.action'
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
};

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	clicksToEdit : 2,// 设置点击几次才可编辑
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	viewConfig : {
		forceFit : true
	},
	listeners : {
		'rowdblclick' : function(gridPanel, rowIndex, e) {
		},
		'beforeedit' : function(e) {
			getTrunkLineData(e.record.get('emsConnectionId'));
		}
	},
	tbar : [ "网管分组:", emsGroupCombo, "网管:", emsCombo, "干线:", trunkLineCombo, "-", {
		text : '新增',
		icon : '../../../../resource/images/btnImages/add.png',
		handler : function() {
			addMulitySection();
		}
	}, {
		text : '删除',
		icon : '../../../../resource/images/btnImages/delete.png',
		handler : function() {
			deleteMulitySection();
		}
	}, {
		text : '保存',
		icon : '../../../../resource/images/btnImages/disk.png',
		handler : function() {
			modifyMulitySection();
		}
	}, {
		text : '设置路由',
		icon : '../../../../resource/images/buttonImages/route.png',
		handler : function() {
			settingMultiSection();
		}
	}, {
		text : '路由详细信息',
		icon : '../../../../resource/images/buttonImages/route_detail.png',
		handler : function() {
			routeDetail();
		}
	} ]
});

// 增加波分复用段
function addMulitySection() {
	var addMulitySectionWindow = new Ext.Window(
			{
				id : 'addMulitySectionWindow',
				title : '波分复用段信息',
				width : 400,
				height : 290,
				isTopContainer : true,
				modal : true,
				autoScroll : true,
				html : '<iframe src = "addMultiSection.jsp" height="100%" width="100%" frameBorder=0 border=0/>'
			});
	addMulitySectionWindow.show();
}

// 删除连接
function deleteMulitySection() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		var jsonData = {
			"sectionWaveModel.sectionId" : cell[0].get("sectionId")
		};
		Ext.Msg.confirm('提示', '确认删除？', function(btn) {
			if (btn == 'yes') {
				top.Ext.getBody().mask('正在执行，请稍候...');
				Ext.Ajax.request({
					url : 'deleteMultiSection.action',
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
	} else
		Ext.Msg.alert("提示", "请选择需要删除的复用段信息！");
}

// 保存
function modifyMulitySection() {
	var grid = gridPanel;
	var store = grid.getStore() // 获取grid中的store对象
	var records = store.getModifiedRecords()// 返回的是所有被修改的行

	if (records.length > 0) {
		Ext.Msg.confirm('提示', '确认修改？', function(btn) {
			if (btn == 'yes') {
				Ext.getBody().mask('正在执行，请稍候...');
				var modifyList = new Array();
				Ext.each(records, function(record) {
					var SectionWaveModel = {
						"sectionId" : record.get("sectionId"),
						"trunkLineId" : record.get('trunkLineId'),
						"sectionName" : record.get("sectionName"),
						"standardWave" : record.get("standardWave"),
						"actullyWave" : record.get("actullyWave"),
						"direction" : record.get("direction")
					};
					modifyList.push(SectionWaveModel);
				});
				var jsonData = {
					"jsonString" : Ext.encode(modifyList)
				};
				Ext.Ajax.request({
					url : 'modifyMultiSection.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						gridPanel.getStore().commitChanges();
						Ext.getBody().unmask();
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
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});
			} else {

			}
		});
	}
}

// 设置路由信息
function settingMultiSection() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		parent.parent
				.addTabPage(
						"../../jsp/performanceManager/opticalPathMonitorManager/multiplexSectionMonitor/settingRouter.jsp?sectionId="
								+ cell[0].get('sectionId')
								+ "&trunkLineId="
								+ cell[0].get('trunkLineId'), "路由设置(" + cell[0].get('sectionName')
								+ ")方向[" + cell[0].get('direction') + "]");
	} else {
		Ext.Msg.alert("提示", "请先选取复用段，只能选择一条！");
	}
}

// 查看路由完整信息
function routeDetail() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		var tabIds = [];
		for ( var i = 0; i < cell.length; i++) {
			var title = "路由详细信息(" + cell[i].get('sectionName') + ")方向[" + cell[i].get('direction')
					+ "]";
			tabIds.push(title);
		}
		for ( var i = 0; i < cell.length; i++) {
			var title = "路由详细信息(" + cell[i].get('sectionName') + ")方向[" + cell[i].get('direction')
					+ "]";
			parent.parent.addTabPage("../performanceManager/routeDetail.jsp?sectionId="
					+ cell[i].get('sectionId') + "&tabIds=" + Ext.encode(tabIds) + "&curTabId="
					+ Ext.encode(title), title);
		}
	} else {
		Ext.Msg.alert("提示", "请先选取复用段！");
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../../../resource/ext/resources/images/default/s.gif";
	// Ext.Ajax.timeout=900000;
	// collapse menu
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel ]
	});

	var jsonData = {
		"pageSize" : 200
	};

	store.proxy = new Ext.data.HttpProxy({
		url : 'getMultiSectionList.action'
	});
	store.baseParams = jsonData;
	// 放最后才能显示遮罩效果
	store.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});
