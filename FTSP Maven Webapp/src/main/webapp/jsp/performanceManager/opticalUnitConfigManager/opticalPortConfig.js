Ext.QuickTips.init(); 

var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	rootVisible : false,
	leafType : 8
};
var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
var westPanel = new Ext.Panel({
	id : "westPanel",
	region : "west",
	width : 300,
	autoScroll : true,
	boxMinWidth : 230,
	boxMinHeight : 260,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0 />'
});
// -------------------data for apply--------------------
var domainDataA = [ [ '1', 'SDH' ], [ '2', 'WDM' ], [ '3', 'ETH' ] ];
var ethDataA = [ [ 'MAC', '全部' ] ];
var sdhDataA = [ [ 'STM-1', 'STM-1' ], [ 'STM-4', 'STM-4' ],
		[ 'STM-16', 'STM-16' ], [ 'STM-64', 'STM-64' ],
		[ 'STM-256', 'STM-256' ] ];
var wdmDataA = [ [ 'OTU1', 'OTU1' ], [ 'OTU2', 'OTU2' ], [ 'OTU3', 'OTU3' ] ];
// ----------------------------------------------------

// --------------------domainCombo---------------------
var domainData = [ [ '0', '全部' ], [ '1', 'SDH' ], [ '2', 'WDM' ],
		[ '3', 'ETH' ] ];
var domainStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
domainStore.loadData(domainData);
var domainCombo = new Ext.form.ComboBox({
	id : 'domainCombo',
	name : 'domainCombo',
	fieldLabel : '业务类型',
	editable : false,
	store : domainStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	width : 100,
	listeners : {
		select : function(combo, record, index) {
			// 重置combo
			Ext.getCmp('portCombo').reset();
			Ext.getCmp('opticalStandardCombo').reset();
			Ext.getCmp('opticalModelCombo').reset();
			// 设置port类型数据源
			if (record.get('value') == 1) {
				portStore.loadData(sdhData);
			} else if (record.get('value') == 2) {
				portStore.loadData(wdmData);
			} else if (record.get('value') == 3) {
				portStore.loadData(ethData);
			} else if (record.get('value') == 4) {
				// portStore.loadData(atmData);
			} else if (record.get('value') == 0) {
				portStore.loadData(allData);
			}
			// 加载combo
			getopticalStandardComboValue();
			getopticalModelComboValue();
		}
	}
});
// --------------------portCombo---------------------
var ethData = [ [ 'MAC', '全部' ]];
var sdhData = [ [ '全部', '全部' ], [ 'STM-1', 'STM-1' ], [ 'STM-4', 'STM-4' ],
		[ 'STM-16', 'STM-16' ], [ 'STM-64', 'STM-64' ],
		[ 'STM-256', 'STM-256' ] ];
var wdmData = [ [ '全部', '全部' ], [ 'OTU1', 'OTU1' ], [ 'OTU2', 'OTU2' ],
		[ 'OTU3', 'OTU3' ] ];
var allData = [ [ '全部', '全部' ], [ 'MAC', 'ETH' ], [ 'STM-1', 'STM-1' ],
		[ 'STM-4', 'STM-4' ], [ 'STM-16', 'STM-16' ], [ 'STM-64', 'STM-64' ],
		[ 'STM-256', 'STM-256' ], [ 'OTU1', 'OTU1' ], [ 'OTU2', 'OTU2' ],
		[ 'OTU3', 'OTU3' ] ];
var portStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
portStore.loadData(allData);
var portCombo = new Ext.form.ComboBox({
	id : 'portCombo',
	name : 'portCombo',
	fieldLabel : '端口类型',
	store : portStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	// value:'全部',
	triggerAction : 'all',
	width : 100,
	listeners : {
		select : function(combo, record, index) {
			Ext.getCmp('opticalStandardCombo').reset();
			Ext.getCmp('opticalModelCombo').reset();
			// 重置光口标准combo和store
			getopticalStandardComboValue();
			getopticalModelComboValue();
		}
	}
});

// --------------------opticalStandardCombo---------------------
var opticalStandardStore = new Ext.data.Store({
	url : 'optical-unit-config!getOptStdComboValue.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "pmStdOptPortId", "model" ])
});

var opticalStandardCombo = new Ext.form.ComboBox({
	id : 'opticalStandardCombo',
	name : 'opticalStandardCombo',
	fieldLabel : '光口标准',
	store : opticalStandardStore,
	mode : 'local',
	displayField : "model",
	valueField : 'pmStdOptPortId',
	width : 100,
	resizable: true,
	triggerAction : 'all'
});
// 页面打开初始化加载光口标准
(function() {
	var searchParam = {
		'searchCond.needAll' : 1,
		'searchCond.needNull' : 1
	};
	opticalStandardStore.baseParams = searchParam;
	opticalStandardStore.load();
})();

function getopticalStandardComboValue() {
	var domain = Ext.getCmp('domainCombo').getValue();
	var ptpType = Ext.getCmp('portCombo').getValue();
	var searchParam = {
		'searchCond.domain' : domain,
		'searchCond.ptpType' : ptpType,
		'searchCond.needAll' : 1,
		'searchCond.needNull' : 1
	};
	opticalStandardStore.baseParams = searchParam;
	opticalStandardStore.load();
}

// --------------------opticalModelCombo---------------------
var opticalModelStore = new Ext.data.Store({
	url : 'optical-unit-config!getOptModelComboValue.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "optModel" ])
});

var opticalModelCombo = new Ext.form.ComboBox({
	id : 'opticalModelCombo',
	name : 'opticalModelCombo',
	fieldLabel : '光模块',
	store : opticalModelStore,
	mode : 'local',
	displayField : "optModel",
	valueField : 'optModel',
	width : 100,
	resizable: true,
	triggerAction : 'all'
});
// 页面打开初始化加载光模块
(function() {
	var searchParam = {
		'searchCond.needAll' : 1,
		'searchCond.needNull' : 1
	};
	opticalModelStore.baseParams = searchParam;
	opticalModelStore.load();
})();

function getopticalModelComboValue() {
	var domain = Ext.getCmp('domainCombo').getValue();
	var ptpType = Ext.getCmp('portCombo').getValue();
	var searchParam = {
		'searchCond.domain' : domain,
		'searchCond.ptpType' : ptpType,
		'searchCond.needAll' : 1,
		'searchCond.needNull' : 1
	};
	opticalModelStore.baseParams = searchParam;
	opticalModelStore.load({
		callback : function(records, options, success) {
		}
	});
}

// *************************** 单端口 *******************************
// #588 此处下拉框改为显示model
var optStdEditStore = new Ext.data.Store({
	url : 'optical-unit-config!getOptStdComboValue.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "pmStdOptPortId", "model" ])
});
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : true,
	header : ""
});
sm.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	stateId : "opticalPortConfigGridId",
	columns : [ new Ext.grid.RowNumberer({
		width : 26,
		locked : true
	}), sm, {
		id : 'ptpId',
		header : 'ptpId',
		dataIndex : 'ptpId',
		hidden : true,
		width : 100
	},  {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 100
	}, {
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 100
	}, {
		id : 'subnet',
		header : '子网',
		hidden : true,
		dataIndex : 'subnet',
		width : 100
	},{
		id : 'NeDisplayName',
		header : '网元',
		dataIndex : 'NeDisplayName',
		width : 100
	}, {
		id : 'area',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'area',
		hidden : true,
		width : 100
	}, {
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'stationName',
		hidden : true,
		width : 100
	}, {
		id : 'NeType',
		header : '型号',
		dataIndex : 'NeType',
		width : 100
	}, {
		id : 'portDescription',
		header : '端口',
		dataIndex : 'portDescription',
		width : 80
	}, {
		id : 'domain',
		header : '业务类型',
		dataIndex : 'domain',
		renderer : transDomainName,
		width : 100
	}, {
		id : 'ptpType',
		header : '端口类型',
		dataIndex : 'ptpType',
		width : 100
	}, {
		id : 'rate',
		header : '速率',
		hidden : true,
		dataIndex : 'rate',
		width : 100
	}, {
		id : 'optModel',
		header : '光模块',
		dataIndex : 'optModel',
		width : 150
	}, {
		id : 'tplMax',
		header : '输出光功率基准值',
		dataIndex : 'tplMax',
		width : 150
	}, {
		id : 'rplMax',
		header : '输入光功率基准值',
		dataIndex : 'rplMax',
		width : 150
	}, {
		id : 'optStd',
		header : '<span style="font-weight:bold">光口标准</span>',
		dataIndex : 'optStd',
		width : 150,
		renderer : transStdName,
		tooltip:'可编辑列',
		editor : new Ext.form.ComboBox({
			mode : "local",
			id : "optStdEdit",
			triggerAction : "all",
			editable : false,
			allowBlank : true,
			store : optStdEditStore,
			valueField : "pmStdOptPortId",
			resizable: true,
			displayField : "model"
		})
	}, {
		id : 'maxOut',
		header : '输出最大',
		dataIndex : 'maxOut',
		width : 100

	}, {
		id : 'minOut',
		header : '输出最小',
		dataIndex : 'minOut',
		width : 150
	}, {
		id : 'maxIn',
		header : '输入最大',
		dataIndex : 'maxIn',
		width : 100
	}, {
		id : 'minIn',
		header : '输入最小',
		dataIndex : 'minIn',
		width : 100
	} ]
});

var store = new Ext.data.Store({
	url : 'optical-unit-config!searchPtpOptModelInfo.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ 'NeDisplayName', 'area', 'optStdId', 'stationName', 'NeType',
			'portDescription', 'domain', 'rate', 'ptpId', 'optModel', 'optStd',
			'maxOut', 'minOut', 'maxIn', 'minIn', 'domain', 'ptpType','ems','emsGroup','subnet',
			'tplMax','rplMax'])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var grid = new Ext.grid.EditorGridPanel({
	id : 'grid',
	name : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm,
	border : false,
	collapsed : false,
	collapsible : false,
	loadMask : true,
	autoScroll : true,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	stateId : "opticalPortConfigGridId",
	stateful : true,
	bbar : pageTool,
	tbar : [ '-',{
		text : '光口标准设置',
		privilege : actionAuth,
		menu : {
			items : [ {
				text : '手动设置',
				handler : applyPtpOptStdBatch
			}, {
				text : '自动设置',
				handler : autoApplyPtpOptStd
			} ]
		}

	}, '-', {
		text : '查询',
		privilege : viewAuth,
		icon : '../../../resource/images/btnImages/search.png',
		handler : searchPtpOptModelInfo
	}, {
		text : '高级查询条件',
		id : 'searchCond',
		privilege : viewAuth,
		enableToggle : true,
		icon : '../../../resource/images/btnImages/getChecked.gif',
		handler : function() {
			if (!oneTbar.hidden) {
				Ext.getCmp('oneTbar').setVisible(false);
				grid.syncSize();
				centerPanel.doLayout();
			} else {
				Ext.getCmp('oneTbar').setVisible(true);
				grid.syncSize();
				centerPanel.doLayout();
			}
		}
	}, '-', {
		text : '保存',
		privilege : modAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : savePtpOptStdApplication
	} ],
	listeners : {
		rowdblclick : function(t, rowIndex, e) {
			e.preventDefault();// 阻止默认事件的传递
			var domain = t.store.getAt(rowIndex).get('domain');
			var ptpType = t.store.getAt(rowIndex).get('ptpType');
			optStdEditStore.baseParams = {
				'searchCond.domain' : domain,
				'searchCond.ptpType' : ptpType,
				'searchCond.needAll' : 0,
				'searchCond.needNull' : 1
			};
			optStdEditStore.load({
				callback : function(records, options, success) {
					if (!success)
						Ext.Msg.alert("提示", "光模块标准加载失败！");
				}
			});
		},
		render : function() {
			oneTbar.render(this.tbar); // add one tbar
			oneTbar.hide();
		},
		destroy : function() {
			Ext.destroy(oneTbar);// 这一句不加可能会有麻烦滴
		}
	}
});

var oneTbar = new Ext.Toolbar({
	id : 'oneTbar',
	items : [ '-',"业务类型 :", domainCombo, '-',"端口类型:", portCombo,'-', "光模块:",
			opticalModelCombo, '-',"光口标准 :", opticalStandardCombo ]
});

// 检查树的选择情况是否符合要求
function checkNodesIfLegal(selectedTargets) {
	if (selectedTargets == '') {
		return '请选择节点！';
	}
	var emsId = selectedTargets[0].emsId;
	var countEms = 0;
	for ( var i = 0; i < selectedTargets.length; i++) {
		if (selectedTargets[i].nodeLevel == 1)
			return '请勿选择网管分组！';
		if (selectedTargets[i].emsId != emsId)
			return '只能选择同一网管的节点！';
	}
	return '';
}

// 查询
function searchPtpOptModelInfo() {
	var iframe = window.frames["tree_panel"];
	var selectedTargets;
	var list = new Array();
	var emsIds = new Array();
	var domain = Ext.getCmp('domainCombo').getValue();
	var ptpType = Ext.getCmp('portCombo').getValue();
	var optModel = Ext.getCmp('opticalModelCombo').getValue();
	var optStd = Ext.getCmp('opticalStandardCombo').getValue();
	var params;
	if (iframe.getCheckedNodes) {
		selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel",
				"emsId" ], "leaf");
		if (checkNodesIfLegal(selectedTargets) != '') {
			Ext.MessageBox.show({
				title : '错误',
				msg : checkNodesIfLegal(selectedTargets),
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
			return;
		}
		for ( var i = 0; i < selectedTargets.length; i++) {
			// if (selectedTargets[i].nodeLevel != 2) {
			var nodes = {
				'nodeId' : selectedTargets[i].nodeId,
				'nodeLevel' : selectedTargets[i].nodeLevel
			};
			list.push(Ext.encode(nodes));
			// }
			if (selectedTargets[i].emsId != undefined)
				emsIds.push(selectedTargets[i].emsId);
		}
		if (emsIds.length == 0) {
			if (Ext.getCmp('oneTbar').isVisible())
				params = {
					'modifyList' : list,
					'searchCond.domain' : domain,
					'searchCond.ptpType' : ptpType,
					'searchCond.optModel' : optModel,
					'searchCond.optStd' : optStd,
					'start' : 0,
					'limit' : 200
				};
			else
				params = {
					'modifyList' : list,
					'start' : 0,
					'limit' : 200
				};
		} else {
			if (Ext.getCmp('oneTbar').isVisible())
				params = {
					'modifyList' : list,
					'condList' : emsIds,
					'searchCond.domain' : domain,
					'searchCond.ptpType' : ptpType,
					'searchCond.optModel' : optModel,
					'searchCond.optStd' : optStd,
					'start' : 0,
					'limit' : 200
				};
			else
				params = {
					'modifyList' : list,
					'condList' : emsIds,
					'start' : 0,
					'limit' : 200
				};
		}
		store.baseParams = params;
		store.load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "查询失败！");
			}
		});
	}
}
// 保存
function savePtpOptStdApplication() {
	var records = store.getModifiedRecords();
	var list = new Array();
	if (records.length > 0) {
		for ( var i = 0; i < records.length; i++) {
			var ptpAndOptStd = {
				"optStdId" : records[i].get("optStd"),
				"ptpId" : records[i].get("ptpId")
			};
			list.push(Ext.encode(ptpAndOptStd));
		}
		var saveParams = {
			"modifyList" : list
		};
		Ext.Ajax.request({
			url : 'optical-unit-config!savePtpOptStdApplication.action',
			params : saveParams,
			method : 'POST',
			success : function(response) {
				grid.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
					// 提交修改，不然store.getModifiedRecords();数据会累加
					if (1 == result.returnResult) {
						store.commitChanges();
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					}
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				grid.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				grid.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}
// 应用
function applyPtpOptStdBatch() {
	var iframe = window.frames["tree_panel"];
	var list = new Array();
	var emsIds = new Array();
	var selectedTargets;
	if (iframe.getCheckedNodes) {
		selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel",
				"emsId" ], "leaf");
		if (checkNodesIfLegal(selectedTargets) != '') {
			Ext.MessageBox.show({
				title : '错误',
				msg : checkNodesIfLegal(selectedTargets),
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
			return;
		}
		for ( var i = 0; i < selectedTargets.length; i++) {
			// if (selectedTargets[i].nodeLevel != 2) {
			var nodes = {
				'nodeId' : selectedTargets[i].nodeId,
				'nodeLevel' : selectedTargets[i].nodeLevel
			};
			list.push(Ext.encode(nodes));
			// }
			if (selectedTargets[i].emsId != undefined)
				emsIds.push(selectedTargets[i].emsId);
		}
		// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓弹窗用store↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
		var domainWinStore = new Ext.data.ArrayStore({
			id : 0,
			fields : [ {
				name : 'value'
			}, {
				name : 'displayName'
			} ]
		});
		domainWinStore.loadData(domainDataA);

		var ptpTypeWinStore = new Ext.data.ArrayStore({
			id : 0,
			fields : [ {
				name : 'value'
			}, {
				name : 'displayName'
			} ]
		});

		var optModelWinStore = new Ext.data.Store({
			url : 'optical-unit-config!getOptModelFromNodes.action',
			reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, [ "optModel" ])
		});

		var optStdWinStore = new Ext.data.Store({
			url : 'optical-unit-config!getOptStdComboValue.action',
			reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, [ "pmStdOptPortId", "model" ])
		});
		// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
		// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓方法↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
		// 确定和应用的按钮
		function apply(isClose) {
			if (panel.getForm().isValid()) {
				var params = {
					'modifyList' : list,
					'condList' : emsIds,
					'searchCond.domain' : Ext.getCmp('domain_win').getValue(),
					'searchCond.ptpType' : Ext.getCmp('ptpType_win').getValue(),
					'searchCond.optModel' : Ext.getCmp('optModel_win')
							.getValue(),
					'searchCond.optStdId' : Ext.getCmp('optStd_win').getValue()
				};
				Ext.Ajax.request({
					url : 'optical-unit-config!applyPtpOptStdBatch.action',
					params : params,
					method : 'POST',
					success : function(response) {
						grid.getEl().unmask();
						var result = Ext.util.JSON
								.decode(response.responseText);
						if (result) {
							Ext.Msg.alert("提示", result.returnMessage);
							// 提交修改，不然store.getModifiedRecords();数据会累加
							if (1 == result.returnResult) {
								store.commitChanges();
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								if (isClose)
									Ext.getCmp('applyWin').close();
							}
						}
					}
				});
			}
		}
		// 选择domain后
		function onSelectDomain() {
			return function(combo, record, index) {
				Ext.getCmp('ptpType_win').reset();
				// 设置prot类型数据源
				if (record.get('value') == 1) {
					ptpTypeWinStore.loadData(sdhDataA);
				} else if (record.get('value') == 2) {
					ptpTypeWinStore.loadData(wdmDataA);
				} else if (record.get('value') == 3) {
					ptpTypeWinStore.loadData(ethDataA);
				}
			};
		}
		// 选择ptpType后
		function onSelectPtpType() {
			return function(combo, record, index) {
				Ext.getCmp('optModel_win').reset();
				optModelWinStore.baseParams = {
					'modifyList' : list,
					'condList' : emsIds,
					'searchCond.domain' : Ext.getCmp('domain_win').getValue(),
					'searchCond.ptpType' : combo.getValue()
				};
				optModelWinStore.load();
				Ext.getCmp('optStd_win').reset();
				Ext.getCmp('max_out').reset();
				Ext.getCmp('min_out').reset();
				Ext.getCmp('max_in').reset();
				Ext.getCmp('min_in').reset();
				optStdWinStore.baseParams = {
					'searchCond.domain' : Ext.getCmp('domain_win').getValue(),
					'searchCond.ptpType' : combo.getValue(),
					'searchCond.needAll' : 0,
					'searchCond.needNull' : 1
				};
				optStdWinStore.load();
			};
		}
		// 选择光口标准后
		function onSelectOptStd() {
			return function(combo, record, index) {
				var param = {
					'searchCond.optStdId' : combo.getValue()
				};
				Ext.Ajax.request({
					url : 'optical-unit-config!getOptStdInfo.action',
					params : param,
					method : 'POST',
					success : function(response) {
						var result = Ext.util.JSON
								.decode(response.responseText);
						if (result) {
							Ext.getCmp('max_out').setValue(result.maxOut);
							Ext.getCmp('min_out').setValue(result.minOut);
							Ext.getCmp('max_in').setValue(result.maxIn);
							Ext.getCmp('min_in').setValue(result.minIn);
						}
					}
				});
			};
		}
		// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑方法END↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

		// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓弹窗↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

		var panel = new Ext.FormPanel({
			region : "center",
			frame : false,
			border : false,
			bodyStyle : 'padding:15px 15px 0 15px;',
			labelWidth : 60,
			labelAlign : 'left',
			items : [ {
				layout : 'form',
				labelSeparator : ":",
				border : false,
				items : [ {
					xtype : 'fieldset',
					id : 'fitCond',
					title : '匹配条件 ',
					layout : 'form',
					items : [ {
						xtype : 'combo',
						id : 'domain_win',
						fieldLabel : '业务类型',
						allowBlank : false,
						store : domainWinStore,
						mode : 'local',
						renderer : transDomainName,
						triggerAction : "all",
						editable : false,
						valueField : 'value',
						displayField : 'displayName',
						listeners : {
							select : onSelectDomain()
						}
					}, {
						xtype : 'combo',
						id : 'ptpType_win',
						fieldLabel : '端口类型',
						allowBlank : false,
						store : ptpTypeWinStore,
						triggerAction : "all",
						mode : 'local',
						editable : false,
						valueField : 'value',
						displayField : 'displayName',
						listeners : {
							select : onSelectPtpType()
						}
					}, {
						xtype : 'combo',
						id : 'optModel_win',
						fieldLabel : '光模块',
						store : optModelWinStore,
						allowBlank : false,
						triggerAction : "all",
						mode : 'local',
						editable : false,
						valueField : 'optModel',
						displayField : 'optModel'
					} ]
				}, {
					xtype : 'fieldset',
					id : 'optType',
					title : '光口类型 ',
					layout : 'form',
					items : [ {
						xtype : 'combo',
						id : 'optStd_win',
						fieldLabel : '光口标准',
						store : optStdWinStore,
						allowBlank : false,
						triggerAction : "all",
						mode : 'local',
						editable : false,
						valueField : 'pmStdOptPortId',
						displayField : 'model',
						listeners : {
							select : onSelectOptStd()
						}

					}, {
						xtype : 'textfield',
						id : 'max_out',
						fieldLabel : '输出最大',
						anchor:"80%",
//						width : 184,
						readOnly : true
					}, {
						xtype : 'textfield',
						id : 'min_out',
						fieldLabel : '输出最小',
						anchor:"80%",
//						width : 184,
						readOnly : true
					}, {
						xtype : 'textfield',
						id : 'max_in',
						fieldLabel : '输入最大',
						anchor:"80%",
//						width : 184,
						readOnly : true
					}, {
						xtype : 'textfield',
						id : 'min_in',
						fieldLabel : '输入最小',
						anchor:"80%",
//						width : 184,
						readOnly : true
					} ]
				} ]
			} ]
		});
		// 弹出的窗口
		var applyWin = new Ext.Window({
			id : 'applyWin',
			title : '光口类型应用',
			width : 380,
			height : 400,
			layout : 'border',
			items : panel,
			buttons : [ {
				text : '应用',
				privilege : actionAuth,
				handler : function() {
					apply(false);
				}
			}, {
				text : '确定',
				privilege : actionAuth,
				handler : function() {
					apply(true);
				}
			}, {
				text : '取消',
				handler : function() {
					Ext.getCmp('applyWin').close();
				}
			} ]
		});

		applyWin.show();
		// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑弹窗END↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	}
}

// 自动设置
function autoApplyPtpOptStd() {
	Ext.Msg
			.confirm(
					"提示",
					"将根据光功率基准值进行模糊匹配，请确认已生成基准值。此操作需要等待较长时间，是否继续？",
					function(btn) {
						if (btn == "yes") {
							var iframe = window.frames["tree_panel"];
							var list = new Array();
							var emsIds = new Array();
							var selectedTargets;
							var processKey = "autoApplyOptStd"
									+ new Date().getTime();
							if (iframe.getCheckedNodes) {
								selectedTargets = iframe.getCheckedNodes([
										"nodeId", "nodeLevel", "emsId" ],
										"leaf");
								if (checkNodesIfLegal(selectedTargets) != '') {
									Ext.MessageBox
											.show({
												title : '错误',
												msg : checkNodesIfLegal(selectedTargets),
												buttons : Ext.MessageBox.OK,
												icon : Ext.MessageBox.ERROR
											});
									return;
								}
								for ( var i = 0; i < selectedTargets.length; i++) {
									// if (selectedTargets[i].nodeLevel != 2) {
									var nodes = {
										'nodeId' : selectedTargets[i].nodeId,
										'nodeLevel' : selectedTargets[i].nodeLevel
									};
									list.push(Ext.encode(nodes));
									// }
									if (selectedTargets[i].emsId != undefined)
										emsIds.push(selectedTargets[i].emsId);
								}
								var params = {
									'modifyList' : list,
									'condList' : emsIds,
									'processBarKey' : processKey
								};
								//console.log(params);
								//console.log(Ext.encode(params));
								Ext.Ajax
										.request({
											url : 'optical-unit-config!autoApplyPtpOptStd.action',
											params : params,
											method : 'POST',
											success : function(response) {
												grid.getEl().unmask();
												var result = Ext.util.JSON
														.decode(response.responseText);
												if (result) {
													Ext.Msg
															.alert(
																	"提示",
																	result.returnMessage);
													// 提交修改，不然store.getModifiedRecords();数据会累加
													if (1 == result.returnResult) {
														store.commitChanges();
														var pageTool = Ext
																.getCmp('pageTool');
														Ext.apply(store.baseParams, params);
														if (pageTool) {
															pageTool.doLoad(pageTool.cursor);
														}
													}
												}
												clearTimer();
											},
											error : function(response) {
												grid.getEl().unmask();
												clearTimer();
											},
											failure : function(response) {
												grid.getEl().unmask();
												clearTimer();
											}

										});
								showProcessBar(processKey);
							}
						}
					});
}

// 标准名renderer
function transStdName(v) {
	if (optStdEditStore.find("pmStdOptPortId", v) != -1)
		return optStdEditStore.getAt(optStdEditStore.find("pmStdOptPortId", v))
				.get("model");
	else
		return v;

}
// domain的renderer
function transDomainName(v) {
	if (v == 1)
		return "SDH";
	if (v == 2)
		return "WDM";
	if (v == 3)
		return "ETH";
	if (v == 4)
		return "ATM";
}

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'border',
	items : [ grid ]
});

Ext
		.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			//Ext.Msg = top.Ext.Msg;

			var win = new Ext.Viewport({
				id : 'win',
				title : "光口类型配置",
				layout : 'border',
				items : [ westPanel, centerPanel ],
				renderTo : Ext.getBody()
			});
			win.show();
		});
