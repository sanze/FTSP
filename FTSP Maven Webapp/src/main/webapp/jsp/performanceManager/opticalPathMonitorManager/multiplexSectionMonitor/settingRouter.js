//************************* tree *******************
var westPanel = new Ext.Panel(
		{
			id : "westPanel",
			region : "west",
			width : 300,
			autoScroll : true,
			collapsed : false, // initially collapse the group
			collapsible : false,
			collapseMode : 'mini',
			split : true,
			html : '<iframe name="tree_panel" id="tree_panel" src ="../../../../jsp/common/tree.jsp?nodeLimite=5" height="100%" width="100%" frameBorder=0 border=0/>'
		});

// ************************* 选网元 ****************************
var neRouteStore = new Ext.data.Store({
	url : 'getNeRouteList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "sectionId", "neRouteId", "neId", "neName", "neDisplayName", "connectionId",
			"connectionDisplayName", "neModel", "sequence" ])
});

var opticalModelStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "standardOpticalValueId", "factory", "type", "model", "maxOut", "minGain", "maxGain",
			"typicalGain", "maxIn", "minIn", "typicalIn" ])
});

var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), {
		id : 'sectionId',
		header : 'sectionId',
		dataIndex : 'sectionId',
		width : 150,
		hidden : true
	}, {
		id : 'neRouteId',
		header : 'neRouteId',
		dataIndex : 'neRouteId',
		width : 150,
		hidden : true
	}, {
		id : 'neId',
		header : 'neId',
		dataIndex : 'neId',
		width : 150,
		hidden : true
	}, {
		id : 'neName',
		header : 'neName',
		dataIndex : 'neName',
		width : 150,
		hidden : true
	}, {
		id : 'connectionId',
		header : 'connectionId',
		dataIndex : 'connectionId',
		width : 150,
		hidden : true
	}, {
		id : 'connectionDisplayName',
		header : '网络',
		dataIndex : 'connectionDisplayName',
		width : 150
	}, {
		id : 'neDisplayName',
		header : '网元名称',
		dataIndex : 'neDisplayName',
		width : 150
	}, {
		id : 'neModel',
		header : '网元型号',
		dataIndex : 'neModel',
		width : 150
	}, {
		id : 'sequence',
		header : '序列号',
		dataIndex : 'sequence',
		width : 150,
		hidden : true
	} ]
});

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : neRouteStore,
	stripeRows : true, // 交替行效果
	loadMask : true,
	viewConfig : {
		forceFit : true
	},
	listeners : {
		'rowclick' : function(gridPanel, rowIndex, e) {
			e.preventDefault();// 阻止默认事件的传递
			var record = gridPanel.getSelectionModel().getSelected();
			if (record.get("neRouteId")) {
				var jsonData = {
					"sectionWaveModel.sectionId" : record.get("sectionId"),
					"sectionWaveModel.neRouteId" : record.get("neRouteId"),
					"sectionWaveModel.type" : 2
				};

				ptpRouteStore.proxy = new Ext.data.HttpProxy({
					url : 'getPtpRouteList.action'
				});
				ptpRouteStore.baseParams = jsonData;
				ptpRouteStore.load({
					callback : function(r, options, success) {
						if (success) {
							ptpRoutePanel.getSelectionModel().selectFirstRow();
						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
				var jsonData1 = {
					"sectionWaveModel.neId" : record.get("neId")
				};

				opticalModelStore.proxy = new Ext.data.HttpProxy({
					url : 'getOpticalModelList.action'
				});
				opticalModelStore.baseParams = jsonData1;
				opticalModelStore.load({
					callback : function(r, options, success) {
						if (success) {

						} else {
							Ext.Msg.alert('错误', '光放类型加载失败！');
						}
					}
				});
			} else {
				ptpRouteStore.removeAll();
			}
		}
	},
	tbar : [ {
		text : '添加网元',
		icon : '../../../../resource/images/btnImages/add.png',
		handler : function() {
			addTarget();
		}
	}, {
		text : '删除网元',
		icon : '../../../../resource/images/btnImages/delete.png',
		handler : function() {
			removeTarget();
		}
	}, {
		text : '上移',
		icon : '../../../../resource/images/btnImages/up.png',
		handler : function() {
			moveUp(gridPanel, neRouteStore);
		}
	}, {
		text : '下移',
		icon : '../../../../resource/images/btnImages/down.png',
		handler : function() {
			movedown(gridPanel, neRouteStore);
		}
	}, {
		text : '保存',
		icon : '../../../../resource/images/btnImages/disk.png',
		handler : function() {
			applyNeRouteModify();
		}
	} ]
});

//添加网元目标
function addTarget() {
	var iframe = window.frames["tree_panel"];
	var selectedTargets = new Array();
	var checkedNodes = iframe.getCheckedNodes_all();
	// 过滤页面已存在的neId
	for ( var i = 0; i < checkedNodes.length; i++) {
		var neId = checkedNodes[i].split("-")[0];
		if (neRouteStore.find("neId", neId) < 0) {
			selectedTargets.push(checkedNodes[i]);
		}
	}
	if (selectedTargets.length > 0) {
		var jsonData = {
			"selectedTargets" : selectedTargets
		};
		top.Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url : 'getNeLevelInfo.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				top.Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				// append参数为true则为增加数据条目，不填默认为false为重载数据
				neRouteStore.loadData(obj, true);
				gridPanel.getSelectionModel().selectLastRow();
				if (gridPanel.getView().getRow(neRouteStore.getCount() - 1)) {
					gridPanel.getView().getRow(neRouteStore.getCount() - 1).click();
				}
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
	}
}

// 删除网元
function removeTarget() {
	var cell = gridPanel.getSelectionModel().getSelections();
	neRouteStore.remove(cell);
	gridPanel.getSelectionModel().selectLastRow();
	if (gridPanel.getView().getRow(neRouteStore.getCount() - 1)) {
		gridPanel.getView().getRow(neRouteStore.getCount() - 1).click();
	}
}

// 应用网元路由修改
function applyNeRouteModify() {
	var jsonString = new Array();
	neRouteStore.each(function(record) {
		var neRouteId = "";
		if (record.get("neRouteId")) {
			neRouteId = record.get("neRouteId");
		}
		var neRouteInfoModel = {
			"neId" : record.get("neId"),
			"neRouteId" : neRouteId
		};
		jsonString.push(neRouteInfoModel);
	})
	var jsonData = {
		"jsonString" : Ext.encode(jsonString),
		"sectionWaveModel.sectionId" : sectionId
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'modifyNeRouteInfo.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage,
						function(r) {
							neRouteStore
									.load({
										callback : function(r, options, success) {
											if (success) {
												gridPanel.getSelectionModel().selectLastRow();
												if (gridPanel.getView().getRow(
														neRouteStore.getCount() - 1)) {
													gridPanel.getView().getRow(
															neRouteStore.getCount() - 1).click();
												}
											} else {
												Ext.Msg.alert('错误', '查询失败！请重新查询');
											}
										}
									});
						});
			}
			if (obj.returnResult == 1) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
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
}

// ************************* 设路由 ****************************
var ptpRouteStore = new Ext.data.Store({
	url : 'getPtpRouteList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "sectionId", "neRouteId", "ptpRouteId", "equipName", "ptpId", "ptpName", "subPtpId",
			"subPtpName", "portNo", "subPortNo", "pmValue", "subPmValue", "calculatePoint",
			"subCalculatePoint", "note", "subNote", "oaValue", "subOaValue", "actualOaValue",
			"actualSubOaValue", "direction", "type", "routeType", "sequence", "model" ])
});

var inOutstore = new Ext.data.SimpleStore({
	fields : [ 'id', 'direction' ],
	data : [ [ 'IN', '输入光功率(dBm)' ], [ 'OUT', '输出光功率(dBm)' ], [ 'VA', '衰耗值(dBm)' ] ]
});

var ptpRoutecm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), {
		id : 'ptpRouteId',
		header : 'ptpRouteId',
		dataIndex : 'ptpRouteId',
		width : 150,
		hidden : true
	}, {
		id : 'ptpRouteSequence',
		header : '序列号',
		dataIndex : 'sequence',
		width : 150,
		hidden : true
	}, {
		id : 'ptpId',
		header : 'ptpId',
		dataIndex : 'ptpId',
		width : 150,
		hidden : true
	}, {
		id : 'subPtpId',
		header : 'subPtpId',
		dataIndex : 'subPtpId',
		width : 150,
		hidden : true
	}, {
		id : 'portNo',
		header : 'portNo',
		dataIndex : 'portNo',
		width : 150,
		hidden : true
	}, {
		id : 'subPortNo',
		header : 'subPortNo',
		dataIndex : 'subPortNo',
		width : 150,
		hidden : true
	}, {
		id : 'routeType',
		header : 'routeType',
		dataIndex : 'routeType',
		width : 150,
		hidden : true
	}, {
		id : 'equipName',
		header : '单板',
		dataIndex : 'equipName',
		width : 150
	}, {
		id : 'ptpName',
		header : '主用端口',
		dataIndex : 'ptpName',
		width : 150,
		renderer : colorGrid
	}, {
		id : 'direction',
		header : 'IN/OUT',
		dataIndex : 'direction',
		renderer : formatDirection,
		width : 150,
		editor : new Ext.form.ComboBox({
			mode : "local",
			displayField : "direction",
			valueField : 'id',
			store : inOutstore,
			triggerAction : 'all',
			editable : false,
			allowBlank : false,
			listeners : {
				select : function(combo, r, index) {
					var record = ptpRoutePanel.getSelectionModel().getSelected();
					if (combo.getValue() == "VA") {
						record.set("routeType", 4);
					} else {
						record.set("routeType", 1);
					}
				}
			}
		})
	}, {
		id : 'model',
		header : '光放型号',
		dataIndex : 'model',
		width : 150,
		editor : new Ext.form.ComboBox({
			displayField : "model",
			valueField : 'model',
			store : opticalModelStore,
			triggerAction : 'all',
			editable : false,
			allowBlank : true
		})
	}, {
		id : 'note',
		header : '备注信息(主)',
		dataIndex : 'note',
		width : 150,
		editor : new Ext.form.TextField({
			allowBlank : true
		}),
		renderer : colorGrid
	}, {
		id : 'oaValue',
		header : '光放理论值(主)',
		dataIndex : 'oaValue',
		width : 150,
		editor : new Ext.form.NumberField({
			allowDecimals : true,
			allowNegative : true,
			decimalPrecision : 2
		// 精确到小数点后两位
		}),
		editable : false,
		renderer : colorGrid
	}, {
		id : 'actualOaValue',
		header : '光放参考值(主)',
		dataIndex : 'actualOaValue',
		width : 150,
		editor : new Ext.form.NumberField({
			allowDecimals : true,
			allowNegative : true,
			decimalPrecision : 2
		// 精确到小数点后两位
		}),
		editable : false,
		renderer : colorGrid
	}, {
		id : 'subPtpName',
		header : '备用端口',
		dataIndex : 'subPtpName',
		width : 150,
		renderer : subColorGrid
	}, {
		id : 'subNote',
		header : '备注信息(备)',
		dataIndex : 'subNote',
		width : 150,
		renderer : subColorGrid,
		editor : new Ext.form.TextField({
			allowBlank : true
		})
	}, {
		id : 'subOaValue',
		header : '光放理论值(备)',
		dataIndex : 'subOaValue',
		width : 150,
		renderer : subColorGrid,
		editor : new Ext.form.NumberField({
			allowDecimals : true,
			allowNegative : true,
			decimalPrecision : 2
		// 精确到小数点后两位
		}),
		editable : false
	}, {
		id : 'actualSubOaValue',
		header : '光放参考值(备)',
		dataIndex : 'actualSubOaValue',
		width : 150,
		renderer : subColorGrid,
		editor : new Ext.form.NumberField({
			allowDecimals : true,
			allowNegative : true,
			decimalPrecision : 2
		// 精确到小数点后两位
		}),
		editable : false
	} ]
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var ptpRoutePanel = new Ext.grid.EditorGridPanel({
	id : "ptpRoutePanel",
	region : "south",
	height : 300,
	padding : '5,5,5,5',
	labelWidth : 100,
	labelAlign : 'right',
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	cm : ptpRoutecm,
	store : ptpRouteStore,
	clicksToEdit : 2,// 设置点击几次才可编辑
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : new Ext.grid.CheckboxSelectionModel(), // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	},
	tbar : [
			{
				text : '主用端口',
				icon : '../../../../resource/images/buttonImages/port.png',
				handler : function() {
					setPort(1);
				}
			},
			{
				text : '备用端口',
				icon : '../../../../resource/images/buttonImages/subPort.png',
				handler : function() {
					setPort(2);
				}
			},
			{
				text : 'DCM盘',
				icon : '../../../../resource/images/buttonImages/card.png',
				handler : function() {
					if (!checkIsNeRouteCompleted(neRouteStore)) {
						Ext.Msg.alert("信息", "请先保存网元路由信息！");
						return;
					}
					var neRouteRecord = gridPanel.getSelectionModel().getSelected();
					if (neRouteRecord) {

					} else {
						Ext.Msg.alert("信息", "请先选择需要添加路由的网元！");
						return;
					}
					addEmptyRoute();
					var record = ptpRoutePanel.getSelectionModel().getSelected();
					// 普通端口 1.普通端口 2.DCM 3.光缆信息 4.VA4
					record.set("routeType", 2);
					record.set("equipName", "DCM");
				}
			},
			{
				text : '光缆信息',
				icon : '../../../../resource/images/buttonImages/fiber.png',
				handler : function() {
					if (!checkIsNeRouteCompleted(neRouteStore)) {
						Ext.Msg.alert("信息", "请先保存网元路由信息！");
						return;
					}
					var url = "addFiberInfo.jsp?trunkLineId=" + trunkLineId;
					var fiberInfoWindow = new Ext.Window({
						id : 'fiberInfoWindow',
						title : '光缆信息',
						width : 400,
						height : 200,
						// closable:false,
						isTopContainer : true,
						modal : true,
						autoScroll : true,
						html : '<iframe src = ' + url
								+ ' height="100%" width="100%" frameBorder=0 border=0/>'
					});
					fiberInfoWindow.show();
				}
			}, {
				text : '上移',
				icon : '../../../../resource/images/btnImages/up.png',
				handler : function() {
					moveUp(ptpRoutePanel, ptpRouteStore);
				}
			}, {
				text : '下移',
				icon : '../../../../resource/images/btnImages/down.png',
				handler : function() {
					movedown(ptpRoutePanel, ptpRouteStore);
				}
			}, {
				text : '添加路由',
				icon : '../../../../resource/images/buttonImages/route_add.png',
				handler : addEmptyRoute
			}, {
				text : '添加配置行',
				icon : '../../../../resource/images/buttonImages/route_add.png',
				handler : function() {
					addEmptyRoute();
					var record = ptpRoutePanel.getSelectionModel().getSelected();
					// 普通端口 1.普通端口 2.DCM 3.光缆信息 4.VA4 5 其他
					record.set("routeType", 5);
					record.set("note", "请输入配置信息");
				}
			}, {
				text : '删除路由',
				icon : '../../../../resource/images/buttonImages/route_delete.png',
				handler : function() {
					var record = ptpRoutePanel.getSelectionModel().getSelected();
					ptpRouteStore.remove(record);
					ptpRoutePanel.getView().refresh();
					ptpRoutePanel.getSelectionModel().selectLastRow();
				}
			}, {
				text : '保存',
				icon : '../../../../resource/images/btnImages/disk.png',
				handler : applyPtpRouteModify
			} ]
});

// 格式化选择的日期
function formatDirection(value) {
	if (value == 'IN') {
		return '输入光功率(dBm)';
	}
	if (value == 'OUT') {
		return '输出光功率(dBm)';
	}
	if (value == 'VA') {
		return '衰耗值(dBm)';
	}
};

function colorGrid(v, m, r) {
	m.css = 'x-grid-font-blue';
	return v;
}

function subColorGrid(v, m, r) {
	m.css = 'x-grid-font-orange';
	return v;
}

// 添加一条空白路由数据
function addEmptyRoute() {
	var ptpRouteData = {
		ptpRouteId : "",
		equipName : "",
		ptpId : "",
		subPtpId : "",
		portNo : "",
		subPortNo : "",
		ptpName : "",
		subPtpName : "",
		note : "",
		subNote : "",
		oaValue : "",
		subOaValue : "",
		actualOaValue : "",
		actualSubOaValue : ""
	}
	var record = ptpRouteStore.recordType;
	var p = new record(ptpRouteData);
	ptpRouteStore.insert(ptpRouteStore.getCount(), p);
	ptpRoutePanel.getView().refresh();
	ptpRoutePanel.getSelectionModel().selectLastRow();
}

// 检查网元路由是否保存完毕
function checkIsNeRouteCompleted(store) {
	var flag = true;
	store.each(function(record) {
		if (record.get("neRouteId")) {

		} else {
			flag = false;
		}
	});
	return flag;
}

// 检查ptp路由是否符合输入要求
function checkIsPtpRouteCompleted(store) {
	var flag = true;
	store.each(function(record) {
		if (((record.get("ptpId") || record.get("subPtpId")) && record.get("direction"))
				|| (record.get("routeType") == 2) || (record.get("routeType") == 3)
				|| (record.get("routeType") == 5)) {

		} else {
			flag = false;
		}
	});
	return flag;
}

// 应用端口路由修改
function applyPtpRouteModify() {
	var neRouteRecord = gridPanel.getSelectionModel().getSelected();
	if (neRouteRecord) {

	} else {
		Ext.Msg.alert("信息", "请先选择需要添加路由的网元！");
		return;
	}
	if (!checkIsPtpRouteCompleted(ptpRouteStore)) {
		Ext.Msg.alert("信息", "主用端口/备用端口及方向不能为空！");
		return;
	}

	var jsonString = new Array();
	ptpRouteStore.each(function(record) {
		var ptpRouteInfoModel = {
			"equipName" : record.get("equipName"),
			"ptpId" : record.get("ptpId"),
			"subPtpId" : record.get("subPtpId"),
			"ptpName" : record.get("ptpName"),
			"subPtpName" : record.get("subPtpName"),
			"portNo" : record.get("portNo"),
			"subPortNo" : record.get("subPortNo"),
			"note" : record.get("note"),
			"subNote" : record.get("subNote"),
			"oaValue" : record.get("oaValue"),
			"subOaValue" : record.get("subOaValue"),
			"actualOaValue" : record.get("actualOaValue"),
			"actualSubOaValue" : record.get("actualSubOaValue"),
			"direction" : record.get("direction"),
			"routeType" : record.get("routeType"),
			"model" : record.get("model")
		};
		jsonString.push(ptpRouteInfoModel);
	});

	var jsonData = {
		"jsonString" : Ext.encode(jsonString),
		"sectionWaveModel.sectionId" : sectionId,
		"sectionWaveModel.neRouteId" : neRouteRecord.get("neRouteId")
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'modifyPtpRouteInfo.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage, function(r) {
					ptpRouteStore.reload();
					ptpRouteStore.load({
						callback : function(r, options, success) {
							if (success) {
								ptpRoutePanel.getSelectionModel().selectLastRow();
							} else {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
				});
			}
			if (obj.returnResult == 1) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
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
}

// 设置端口信息
function setPort(type) {
	if (!checkIsNeRouteCompleted(neRouteStore)) {
		Ext.Msg.alert("信息", "请先保存网元路由信息！");
		return;
	}
	var neRouteRecord = gridPanel.getSelectionModel().getSelected();
	if (neRouteRecord) {

	} else {
		Ext.Msg.alert("信息", "请先选择需要添加路由的网元！");
		return;
	}
	var iframe = window.frames["tree_panel"];
	var selectedTargets = new Array();
	var checkedNodes = iframe.getAllCheckedNodes();
	if (checkedNodes.length == 1) {
		var id = checkedNodes[0].split("-")[0];
		var nodeType = checkedNodes[0].split("-")[1];
		if (nodeType == 5) {
			var record = ptpRoutePanel.getSelectionModel().getSelected();
			if (record) {
				var ptpId = "";
				var subPtpId = "";
				// 设置主用端口
				if (type == 1) {
					ptpId = id;
					// 设置备用端口
					if (record.get("subPtpId")) {
						subPtpId = record.get("subPtpId");
					}
				}
				// 设置备用端口
				if (type == 2) {
					subPtpId = id;
					// 设置主用端口
					if (record.get("ptpId")) {
						ptpId = record.get("ptpId");
					}
				}
				var jsonData = {
					"ptpRouteInfoModel.neId" : neRouteRecord.get("neId"),
					"ptpRouteInfoModel.ptpId" : ptpId,
					"ptpRouteInfoModel.subPtpId" : subPtpId,
					"ptpRouteInfoModel.type" : type
				};
				Ext.Ajax.request({
					url : 'checkPtpAndSubPtpInfo.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 0) {
							if (type == 1) {
								record.set("ptpId", obj.ptpId);
								record.set("ptpName", obj.ptpName);
								record.set("portNo", obj.portNo);
								record.set("direction", obj.direction);
							} else {
								record.set("subPtpId", obj.subPtpId);
								record.set("subPtpName", obj.subPtpName);
								record.set("subPortNo", obj.subPortNo);
								record.set("direction", obj.direction);
							}
							// 普通端口 1.普通端口 2.DCM 3.光缆信息 4.VA4
							record.set("routeType", 1);
							record.set("equipName", obj.equipName);
							iframe.triggerRootNode();
						}
						if (obj.returnResult == 1) {
							Ext.Msg.alert("信息", obj.returnMessage);
						}
					},
					error : function(response) {
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						Ext.Msg.alert("错误", response.responseText);
					}
				});
			}
		} else {
			Ext.Msg.alert("信息", "请选择一个端口！");
		}
	} else {
		Ext.Msg.alert("信息", "请选择一个端口！");
	}
}

// ******************* 共用方法 *******************
// 行数据上移一格
function moveUp(gridPanel, store) {
	var record = gridPanel.getSelectionModel().getSelected();
	if (!record) {
		return;
	}
	var index = store.indexOf(record);
	if (index == 0) {
		return;
	}
	store.remove(record);
	store.insert(index - 1, record);
	gridPanel.getView().refresh();
	gridPanel.getSelectionModel().selectRow(index - 1);
}

// 行数据下移一格
function movedown(gridPanel, store) {
	var record = gridPanel.getSelectionModel().getSelected();
	if (!record) {
		return;
	}
	var index = store.indexOf(record);
	if (index == store.getCount() - 1) {
		return;
	}
	store.remove(record);
	store.insert(index + 1, record);
	gridPanel.getView().refresh();
	gridPanel.getSelectionModel().selectRow(index + 1);
}

function initDate() {
	neRouteStore.proxy = new Ext.data.HttpProxy({
		url : 'getNeRouteList.action'
	});
	neRouteStore.baseParams = {
		"sectionWaveModel.sectionId" : sectionId
	};
	neRouteStore.load({
		callback : function(r, options, success) {
			if (success) {
				gridPanel.getSelectionModel().selectFirstRow();
				if (gridPanel.getView().getRow(0)) {
					gridPanel.getView().getRow(0).click();
				}
			} else {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}
		}
	});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	}
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel, westPanel, ptpRoutePanel ]
	});
	win.show();
	initDate();
});