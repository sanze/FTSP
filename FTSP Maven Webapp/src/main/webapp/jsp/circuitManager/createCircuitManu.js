/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
Ext.QuickTips.init();
var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	rootVisible : false,
	checkModel : "multiple",
	leafType : 4
};

// 共通树url
var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
var exportData;
var westPanel = new Ext.Panel({
	id : "westPanel",
	region : "west",
	width : 280,
	height : 800,
	minSize : 230,
//	maxSize : 320,
	autoScroll : true,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="tree_panel" name = "tree_panel" src =' + treeurl
			+ ' height="100%" width="100%" frameBorder=0 border=0/>'
});
// ================stores===================

// var store = new Ext.data.Store({
//
// reader : new Ext.data.JsonReader({
// totalProperty : 'total',
// root : "rows"
// }, ["circuitId", "circuitNo", "systemSourceNo",
// "circuitName", "clientName", "usedFor",
// "circuitType", "aport", "AEnd64c", "AEnd16c",
// "AEnd8c", "AEnd4c", "AEndJ", "AEndK", "AEndL",
// "AEndM", "AEndUserName", "zport", "ZEnd64c",
// "ZEnd16c", "ZEnd8c", "ZEnd4c", "ZEndJ", "ZEndK",
// "ZEndL", "ZEndM", "ZEndUserName", "AEndPort",
// "ZEndPort", "circuitinfoid", "circuitinfoNo",
// "circuitcount", "aendjorigin", "zendjorigin","actpid","zctpid",
// "actp", "zctp"])
// });
// ------------------------------StroeForTest---------------------------------
var store = new Ext.data.Store({
	baseParams : {
		"limit" : 200
	},
	url : 'circuit!selectCircuitLast.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "CIR_CIRCUIT_INFO_ID", "CIR_NO", "SOURCE_NO", "SVC_TYPE",
			"CLIENT_NAME", "A_END_CTP", "Z_END_CTP", "A_PORT", "Z_PORT",
			"RATE", "CIR_NAME", "A_END_USER_NAME", "Z_END_USER_NAME",
			"IS_COMPLETE_CIR", "A_NE", "A_EMS", "A_EMS_GROUP", "Z_NE", "Z_EMS",
			"Z_EMS_GROUP", "A_CTP", "Z_CTP", "USED_FOR", "USED_FOR" ])
});
// ==========================page=============================
// ==========================page=============================
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : true,
	header : ""
});
checkboxSelectionModel.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	stateId : 'cirManuId',
	columns : [ new Ext.grid.RowNumberer({
		width : 26,
		locked : true
	}), checkboxSelectionModel, {
		id : 'CIR_NO',
		header : '电路编号',
		dataIndex : 'CIR_NO',
		width : 100
	}, {
		id : 'SOURCE_NO',
		header : '<span style="font-weight:bold">资源编号</span>',
		dataIndex : 'SOURCE_NO',
		width : 100,
		tooltip : '可编辑列',
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	}, {
		id : 'SVC_TYPE',
		header : "业务类型",
		dataIndex : 'SVC_TYPE',
		width : 100,
		renderer : function(v) {
			if (v == 1) {
				return "SDH电路";
			}
			if (v == 2)
				return "以太网电路";
			if (v == 3)
				return "WDM电路";
		}
	}, {
		id : 'A_NE',
		header : 'A端网元',
		dataIndex : 'A_NE',
		width : 100

	}, {
		id : 'A_PORT',
		header : 'A端端口',
		dataIndex : 'A_PORT',
		width : 100

	}, {
		id : 'A_CTP',
		header : 'A端时隙',
		dataIndex : 'A_CTP',
		width : 100

	}, {
		id : 'Z_NE',
		header : 'Z端网元',
		dataIndex : 'Z_NE',
		width : 100

	}, {
		id : 'Z_PORT',
		header : 'Z端端口',
		dataIndex : 'Z_PORT',
		width : 100

	}, {
		id : 'Z_CTP',
		header : 'Z端时隙',
		dataIndex : 'Z_CTP',
		width : 100

	}, {
		id : 'RATE',
		header : '电路速率',
		dataIndex : 'RATE',
		width : 50,
		listeners : {
			beforerender : function() {
				if (serviceType == 3) {
					Ext.getCmp('RATE').hide();
				}
			}
		}
	}, {
		id : 'IS_COMPLETE_CIR',
		header : '电路类型',
		dataIndex : 'IS_COMPLETE_CIR',
		width : 50,
		renderer : function(v) {
			if (v == 0)
				return "不完整";
			if (v == 1)
				return "完整";
		}

	}, {
		id : 'CIR_NAME',
		header : "<span style='font-weight:bold'>路由名称</span>",
		dataIndex : 'CIR_NAME',
		width : 100,
		tooltip : '可编辑列',
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	}, {
		id : 'CLIENT_NAME',
		header : "<span style='font-weight:bold'>客户名称</span>",
		dataIndex : 'CLIENT_NAME',
		tooltip : '可编辑列',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	}, {
		id : 'USED_FOR',
		header : "<span style='font-weight:bold'>用途</span>",
		dataIndex : 'USED_FOR',
		tooltip : '可编辑列',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	}, {
		id : 'A_END_USER_NAME',
		header : "<span style='font-weight:bold'>A端用户</span>",
		dataIndex : 'A_END_USER_NAME',
		width : 100,
		tooltip : '可编辑列',
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	}, {
		id : 'Z_END_USER_NAME',
		header : "<span style='font-weight:bold'>Z端用户</span>",
		dataIndex : 'Z_END_USER_NAME',
		tooltip : '可编辑列',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	}, {
		id : 'A_EMS',
		header : 'A端所属网管',
		dataIndex : 'A_EMS',
		hidden : true,
		width : 100
	}, {
		id : 'A_EMS_GROUP',
		header : 'A端所属网管分组',
		dataIndex : 'A_EMS_GROUP',
		hidden : true,
		width : 100
	}, {
		id : 'Z_EMS',
		header : 'Z端所属网管',
		dataIndex : 'Z_EMS',
		hidden : true,
		width : 100
	}, {
		id : 'Z_EMS_GROUP',
		header : 'Z端所属网管分组',
		dataIndex : 'Z_EMS_GROUP',
		hidden : true,
		width : 100
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

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	stateId : 'cirManuId', // 注意！！！这个ID不能与其他页面的重复
	stateful : true,
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : {
		msg : '数据加载中...'
	},
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	bbar : pageTool,
	tbar : [ '-', {
		text : '电路生成',
		privilege : actionAuth,
		handler : createCircuit

	}, {
		text : '全网生成',
		privilege : actionAuth,
		handler : createAllCircuit

	}, '-',{
		text : '电路详情',
		privilege : viewAuth,
		icon : '../../resource/images/btnImages/setTask.png',
		handler : selectCircuitRoute
	}, '-',{
		text : '保存',
		privilege : modAuth,
		icon : '../../resource/images/btnImages/disk.png',
		handler : modifyCircuit
	}, {
		text : '导出',
		privilege : actionAuth,
		icon : '../../resource/images/btnImages/export.png',
		handler : exportCircuit
	},'-', '&nbsp;&nbsp;电路类型：', {
		xtype : 'combo',
		id : 'serviceTypeCombo',
		mode : 'local',
		labelSeparator : ':',
		fieldLabel : '电路业务类型',
		store : new Ext.data.ArrayStore({
			fields : [ 'value', 'displayName' ],
			data : [ [ 1, 'SDH' ], [ 2, 'ETH' ], [ 3, 'OTN/WDM' ],[ 4, 'PTN' ] ]
		}),
		valueField : 'value',
		displayField : 'displayName',
		triggerAction : 'all',
		disabled :true,
		value : 1,
		listeners : {

			select : function(combo, record, index) {

				var type = Ext.getCmp('serviceTypeCombo').getValue();

				var jsonData = {
					"type" : type,
					"limit" : 200
				};
				store.proxy = new Ext.data.HttpProxy({
					url : 'circuit!selectCircuitLast.action'
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
	}

	]
});
// =======================链路更新=========================

// 电路生成
function createCircuit() {
	var processKey = "newCir" + new Date().getTime();
	var isgo = true;
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var checkedNodeIds;
	if (iframe.getCheckedNodes) {
		checkedNodeIds = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ],
				"top");
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes([ "nodeId",
				"nodeLevel" ], "top");
	}
	var arr = new Array();
	arr = Ext.encode(checkedNodeIds);
	// 判断是否在同一级别
	var level = 0;

	if (checkedNodeIds.length > 500) {
		isgo = false;
		Ext.Msg.alert("提示", "选择的数量不要超过五百个，如数量较大，请直接选择父节点");
		return;
	} else if (checkedNodeIds.length > 1) {
		level = checkedNodeIds[0]["nodeLevel"]
		for ( var i = 1; i < checkedNodeIds.length; i++) {
			if (level != checkedNodeIds[i]["nodeLevel"]) {
				isgo = false;
				Ext.Msg.alert("提示", "只能选择同一级别，请不要跨级别选择");
				return;
			}

		}
	}
	var jsonData = {

		"limit" : 200

	};
	var jsonData_ = {
		"limit" : 200,
		"processKey" : processKey,
		"jsonString" : Ext.encode(checkedNodeIds)
	};
	// Ext.getBody().mask('生成中...');
	Ext.Ajax.request({
		url : 'circuit!createCircuit.action',
		method : 'POST',
		params : jsonData_,
		success : function(response) {// 回调函数

			var obj = Ext.decode(response.responseText);
			Ext.getBody().unmask();
			if (obj.returnResult == 1) {
				Ext.getCmp("serviceTypeCombo").setDisabled(false); 
				Ext.getCmp("pageTool").setDisabled(false);
				store.proxy = new Ext.data.HttpProxy({
					url : 'circuit!selectCircuitLast.action'

				});
				store.baseParams = jsonData;
				store.load({
					callback : function(r, options, success) {
						Ext.getBody().unmask();
						if (success) {
							Ext.getBody().unmask();
						} else {
							clearTimer();
							Ext.Msg.alert('错误', '更新失败！请重新更新');
						}
					}
				});

			}
			if (obj.returnResult == 0) {
				clearTimer();
				Ext.Msg.alert("提示", obj.returnMessage);
			}

		},
		error : function(response) {
//			clearTimer();
		},
		failure : function(response) {
//			clearTimer();
		}

	});
	if (checkedNodeIds.length > 0 && isgo) {
		showProcessBar(processKey);
	}

}

// 全网电路生成
function createAllCircuit() {
	var processKey = "newCir" + new Date().getTime();
	var jsonData = {
		"processKey" : processKey,
		"limit" : 200

	};
	// Ext.getBody().mask('生成中...');
	Ext.Ajax.request({
		url : 'circuit!createAllCircuit.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {// 回调函数

			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.getBody().unmask();
				Ext.getCmp("serviceTypeCombo").setDisabled(false);
				Ext.getCmp("pageTool").setDisabled(false);
				var jsonData_ = {
					"type" : 1,
					"limit" : 200

				};
				store.proxy = new Ext.data.HttpProxy({
					url : 'circuit!selectCircuitLast.action'

				});
				store.baseParams = jsonData_;
				store.load({
					callback : function(r, options, success) {

						if (success) {
						} else {
							clearTimer();
							Ext.Msg.alert('错误', '更新失败！请重新更新');
						}
					}
				});

			}
			if (obj.returnResult == 0) {
				clearTimer();
				Ext.Msg.alert("提示", obj.returnMessage);
			}

		},
		error : function(response) {
//			clearTimer();
		},
		failure : function(response) {
//			clearTimer();
		}

	});
	showProcessBar(processKey);

}
// ----------------------------------------------------------------------
function exportCircuit() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				{
					exportRequest();
				}
			}
		});
	} else
		exportRequest();
}
var exportRequest = function() {
	gridPanel.getEl().mask("正在导出...");
	var type = Ext.getCmp('serviceTypeCombo').getValue();
	exportData = {
		"type" : type,
		"displayName" : displayName,
		"flag" : 5
	};
	Ext.Ajax.request({
		url : 'circuit-export!exportExcel.action',
		type : 'post',
		params : {
			"jsonString" : Ext.encode(exportData)
		},
		success : function(response) {
			gridPanel.getEl().unmask();
			var rs = Ext.decode(response.responseText);
			if (rs.returnResult == 1 && rs.returnMessage != "") {
				var destination = {
					"filePath" : rs.returnMessage
				};
				window.location.href = "download!execute.action?"
						+ Ext.urlEncode(destination);
			} else {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "导出失败！");
			}
		},
		error : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
};
function modifyCircuit() {
	var jsonString = new Array();
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {
		for ( var i = 0; i < cell.length; i++) {
			var map = {
				"A_END_CTP" : cell[i].get('A_END_CTP'),
				"Z_END_CTP" : cell[i].get('Z_END_CTP'),
				"SOURCE_NO" : cell[i].get('SOURCE_NO'),
				"CIR_NAME" : cell[i].get('CIR_NAME'),
				"CLIENT_NAME" : cell[i].get('CLIENT_NAME'),
				"USED_FOR" : cell[i].get('USED_FOR'),
				"A_END_USER_NAME" : cell[i].get('A_END_USER_NAME'),
				"Z_END_USER_NAME" : cell[i].get('Z_END_USER_NAME'),
				"PTN_INFO" : cell[i].get('CIR_CIRCUIT_INFO_ID'),		
				"SVC_TYPE" : cell[i].get('SVC_TYPE')
			};
			jsonString.push(map);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		// 提交修改，不然store.getModifiedRecords();数据会累加
		store.commitChanges();
		Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url : 'circuit!modifyCircuit.action',
			method : 'POST',
			params : jsonData,

			success : function(response) {// 回调函数
				Ext.getBody().unmask();

				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					Ext.Msg.alert("提示", obj.returnMessage, function(r) {
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					});
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("提示", obj.returnMessage);
				}

			},
			error : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert('错误', '保存失败！');
			},
			failure : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert('错误', '保存失败！');
			}

		});
	}
}
// -------------------------------路由相关查询-----------------------------------
function selectCircuitRoute() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		if (cell[0].get('SVC_TYPE') == 1||cell[0].get('SVC_TYPE')==3||cell[0].get('SVC_TYPE')==4) {
			var vCircuit = cell[0].get('CIR_NO');
			var infoId = cell[0].get('CIR_CIRCUIT_INFO_ID');
			var url = "../circuitManager/apaView.jsp?vCircuit=" + vCircuit
					+ "&infoId=" + infoId + "&serviceType="
					+ cell[0].get('SVC_TYPE');
			parent.addTabPage(url, "电路详情：(" + vCircuit + ")", authSequence);

		} else if (cell[0].get('SVC_TYPE') == 2) {
			var vCircuit = cell[0].get('CIR_NO');
			;
			var infoId = cell[0].get('CIR_CIRCUIT_INFO_ID');
			var url = "../circuitManager/subCircuit.jsp?parentCir=" + infoId
					+ "&cirNo=" + vCircuit;
			parent.addTabPage(url, "子电路清单：(" + vCircuit + ")", authSequence);
		}
	} else {
		Ext.Msg.alert('提醒', '请先选择要查询路由的电路！');
	}
}

// ----------------------------DeleteUserFunction---------------------------------

// -----------------------------------------init the
// page--------------------------------------------

// ************************************************************************************

Ext.onReady(function() {
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;
	var win = new Ext.Viewport({
		id : 'win',
		title : "B类比较值设定",
		layout : 'border',
		items : [ gridPanel, westPanel ],
		renderTo : Ext.getBody()
	});
	win.show();

});