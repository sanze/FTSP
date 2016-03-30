/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
var myPageSize = 200;
Ext.QuickTips.init(); 
// ================stores===================
var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "CIR_CIRCUIT_ID", "cir_no", "source_no", "svc_type", "client_name",
			"a_end_ctp", "z_end_ctp", "a_end_port", "z_end_port", "rate",
			"cir_name", "a_end_user_name", "z_end_user_name",
			"IS_COMPLETE_CIR", "a_end_ne", "a_end_ems", "a_end_ems_group",
			"z_end_ne", "z_end_ems", "Z_end_ems_group", "A_CTP_ID", "Z_CTP_ID",
			"USED_FOR", "CIR_CIRCUIT_INFO_ID" ])
});
var exportData;
// ==========================page=============================
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : singleSelect==false?false:true,
	header : singleSelect==false?null:""
});
checkboxSelectionModel.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	stateId : 'cirListGrid',//保持列锁定状态
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), checkboxSelectionModel, {
		id : 'circuitNo',
		header : '电路编号',
		dataIndex : 'cir_no',
		width : 100
	}, {
		id : 'systemSourceNo',
		header :  '<span style="font-weight:bold">资源编号</span>',
		dataIndex : 'source_no',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		}),
		tooltip:readOnly==true?null:'可编辑列'
	}, {
		id : 'circuitType',
		header : "业务类型",
		dataIndex : 'svc_type',
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
		id : 'ane',
		header : 'A端网元',
		dataIndex : 'a_end_ne',
		width : 100

	}, {
		id : 'aport',
		header : 'A端端口',
		dataIndex : 'a_end_port',
		width : 100

	}, {
		id : 'actp',
		header : 'A端时隙',
		dataIndex : 'a_end_ctp',
		width : 100

	}, {
		id : 'zne',
		header : 'Z端网元',
		dataIndex : 'z_end_ne',
		width : 100

	}, {
		id : 'zport',
		header : 'Z端端口',
		dataIndex : 'z_end_port',
		width : 100

	}, {
		id : 'zctp',
		header : 'Z端时隙',
		dataIndex : 'z_end_ctp',
		width : 100

	}, {
		id : 'rate',
		header : '电路速率',
		dataIndex : 'rate',
		width : 100,
		listeners : {
			beforerender : function() {
				if (serviceType == 3) {
					Ext.getCmp('rate').hide();
				}
			}
		}
	}, {
		id : 'type',
		header : '电路类别',
		dataIndex : 'IS_COMPLETE_CIR',
		width : 100,
		renderer : function(v) {
			if (v == 0)
				return "不完整";
			if (v == 1)
				return "完整";
		}

	}, {
		id : 'circuitName',
		header :'<span style="font-weight:bold">路由名称</span>',
		dataIndex : 'cir_name',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		}),
		tooltip:readOnly==true?null:'可编辑列'
	}, {
		id : 'clientName',
		header : '<span style="font-weight:bold">客户名称</span>',
		dataIndex : 'client_name',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		}),
		tooltip:readOnly==true?null:'可编辑列'
	}, {
		id : 'usedFor',
		header : '<span style="font-weight:bold">用途</span>',
		dataIndex : 'USED_FOR',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		}),
		tooltip:readOnly==true?null:'可编辑列'
	}, {
		id : 'AEndUserName',
		header :'<span style="font-weight:bold">A端用户</span>',
		dataIndex : 'a_end_user_name',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		}),
		tooltip:readOnly==true?null:'可编辑列'
	}, {
		id : 'ZEndUserName',
		header :'<span style="font-weight:bold">Z端用户</span>',
		dataIndex : 'z_end_user_name',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		}),
		tooltip:readOnly==true?null:'可编辑列'
	}, {
		id : 'AEMS',
		header : 'A端所属网管',
		dataIndex : 'a_end_ems',
		hidden:true,
		width : 100
	}, {
		id : 'AEMSGroup',
		header : 'A端所属网管分组',
		dataIndex : 'a_end_ems_group',
		hidden:true,
		width : 100
	}, {
		id : 'ZEMS',
		header : 'Z端所属网管',
		dataIndex : 'z_end_ems',
		hidden:true,
		width : 100
	}, {
		id : 'ZEMSGroup',
		header : 'Z端所属网管分组',
		dataIndex : 'Z_end_ems_group',
		hidden:true,
		width : 100
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	stateId : 'cirListGrid', //注意！！！这个ID不能与其他页面的重复
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
	tbar : {hidden:readOnly==true?true:false,
			items:[ '-',
			        {
					text : '保存',
					privilege : modAuth,
					icon : '../../resource/images/btnImages/disk.png',
					handler : modifyCircuit,
				}, "-",{
					text : '路由详情',
					privilege : viewAuth,
					icon : '../../resource/images/btnImages/setTask.png',
					handler : selectCircuitRoute
				},"-", {
					text : '导出',
					privilege : actionAuth,
					icon : '../../resource/images/btnImages/export.png',
					handler : exportData
				}]
			}
});

// =======================数据导出=========================
function exportData() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	}
	else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				{
					exportRequest();
				}
			}
		});
	}else exportRequest();
}
var exportRequest=function(){
	gridPanel.getEl().mask("正在导出...");
	Ext.Ajax.request({
		url : 'circuit-export!exportExcel.action',
		type : 'post',
		params : {
			"jsonString" : Ext.encode(exportData)
		},
		success : function(response) {
			gridPanel.getEl().unmask();
			var rs=Ext.decode(response.responseText);
			if(rs.returnResult==1&&rs.returnMessage!=""){
				var destination={
						"filePath":rs.returnMessage
				};
				window.location.href="download!execute.action?"+Ext.urlEncode(destination);
			}
			else {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示","导出失败！");
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

// =======================链路更新=========================

// -------------------------------取消重置-----------------------------------

function modifyCircuit() {
	var jsonString = new Array();
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {
		for ( var i = 0; i < cell.length; i++) {
			var map = {
				"A_END_CTP" : cell[i].get('A_CTP_ID'),
				"Z_END_CTP" : cell[i].get('Z_CTP_ID'),
				"SOURCE_NO" : cell[i].get('source_no'),
				"CIR_NAME" : cell[i].get('cir_name'),
				"CLIENT_NAME" : cell[i].get('client_name'),
				"USED_FOR" : cell[i].get('USED_FOR'),
				"A_END_USER_NAME" : cell[i].get('a_end_user_name'),
				"Z_END_USER_NAME" : cell[i].get('z_end_user_name'),
				"PTN_INFO" : cell[i].get('CIR_CIRCUIT_INFO_ID'),		
				"SVC_TYPE":cell[i].get('svc_type')

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
				if (obj.returnResult == 0) {
					Ext.Msg.alert("提示", obj.returnMessage, function(r) {
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					});
				}
				if (obj.returnResult == 1) {
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
		var vCircuit = cell[0].get('cir_no');
		// 1.SDH 2.以太网 3.WDM 4.PTN
		if (cell[0].get('svc_type') == 1||cell[0].get('svc_type') == 3 ||cell[0].get('svc_type') == 4) {
			var url = "../circuitManager/apaView.jsp?vCircuit=" + vCircuit
					+ "&serviceType=" + cell[0].get('svc_type');
			parent.addTabPage(url, "路由详情：(" + vCircuit + ")", authSequence);

		} else if (cell[0].get('svc_type') == 2) {
			var parentCir = cell[0].get('CIR_CIRCUIT_INFO_ID');
			var url = "../circuitManager/subCircuit.jsp?parentCir=" + parentCir+"&cirNo="+vCircuit;
			parent.addTabPage(url, "子电路清单：(" + parentCir + ")", authSequence);
		}
	} else
		Ext.Msg.alert('提醒', '请先选择要查询路由的电路！');
}

// ----------------------------DeleteUserFunction---------------------------------

function backpage() {
	window.location.replace("selectCircuitPort.jsp");

}
// -----------------------------------------init the
// page--------------------------------------------
function init(){
	// 初始化
	
}
// ************************************************************************************

Ext.onReady(function() {
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Ajax.timeout = 900000;
	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		title : "B类比较值设定",
		layout : 'border',
		items : [ gridPanel ],
		renderTo : Ext.getBody()
	});
	if (flag == 2) {
		var jsonString = {
			"flag" : 2,
			"clientName" : clientName,
			"circuitName" : circuitName,
			"useFor" : useFor,
			"serviceType" : serviceType,
			"circuitNo" : circuitNo,
			"systemSourceNo" : systemSourceNo,
			"sourceNo" : sourceNo,
			"connectRate" : connectRate,
			"circuitState" : circuitState,
			"linkId" : linkId,
			"nodes" : nodes,
			"nodeLevel" : nodeLevel,
			"aLocationId" : aLocationId,
			"aLocationLevel" : aLocationLevel,
			"advancedCon" : advancedCon,
			"limit" : myPageSize,
			"displayName":displayName
		};
		exportData = jsonString;
		store.proxy = new Ext.data.HttpProxy({
			url : 'circuit!selectCircuitAbout.action'
		});

		store.baseParams = {
			"jsonString" : Ext.encode(jsonString)
		};
		store.load({
			callback : function(r, options, success) {
				if (success) {

				} else {
					// top.Ext.getBody().unmask();
					Ext.Msg.alert('提示', '内部错误！');

				}
			}
		});
	}
	if (flag == 7) {
		store.proxy = new Ext.data.HttpProxy({
			url : 'circuit!getAboutCircuit.action'
		});
		jsonString = {
			"flag" : 7,
			"nodes":nodes,
			"nodeLevel" :nodeLevel,
			"serviceType" : serviceType,
			"limit" : myPageSize,
			"displayName":displayName
		};
		exportData = jsonString;
		store.baseParams = {
			"jsonString" : Ext.encode(jsonString)
		}, store.load({
			callback : function(r, options, success) {
				if (success) {

				} else {
					// top.Ext.getBody().unmask();
					Ext.Msg.alert('提示', '内部错误！');
				}
			}
		});
	}
	if (flag == 1) {
		store.proxy = new Ext.data.HttpProxy({
			url : 'circuit!getCircuitByPtp.action'
		});
		jsonString = {
			"flag" : 1,
			"aNodeId" : aNodeId,
			"aNodeLevel" : aNodeLevel,
			"zNodeId" : zNodeId,
			"zNodeLevel" : zNodeLevel,
			"aLocationLevel" : aLocationLevel,
			"aLocationId" : aLocationId,
			"zLocationLevel" : zLocationLevel,
			"zLocationId" : zLocationId,
			"serviceType" : serviceType,
			"limit" : myPageSize,
			"displayName":displayName
		};
		exportData = jsonString;
		store.baseParams = {
			"jsonString" : Ext.encode(jsonString)
		}, store.load({
			callback : function(r, options, success) {
				if (success) {

				} else {
					// top.Ext.getBody().unmask();
					Ext.Msg.alert('提示', '内部错误！');
				}
			}
		});
	}
	if(flag==6){
		var ptpType;
		switch(serviceType){
		case "1":ptpType=1;break;
		case "2":ptpType=3;break;
		case "3":ptpType=2;break;
		}
		var jsonString = {
				"flag" : 2,
				"clientName" : "",
				"circuitName" : "",
				"useFor" : "",
				"serviceType" :ptpType,
				"circuitNo" : "",
				"systemSourceNo" : "",
				"sourceNo" : "",
				"connectRate" : "",
				"circuitState" : "",
				"linkId" : "",
				"nodes" : nodes+"/",
				"nodeLevel" : nodeLevel,
				"aLocationId" : "",
				"aLocationLevel" : "",
				"limit" : myPageSize,
				"displayName":displayName
			};
			exportData = jsonString;
			store.proxy = new Ext.data.HttpProxy({
				url : 'circuit!selectCircuitAbout.action'
			});

			store.baseParams = {
				"jsonString" : Ext.encode(jsonString)
			};
			store.load({
				callback : function(r, options, success) {
					if (success) {

					} else {
						// top.Ext.getBody().unmask();
						Ext.Msg.alert('提示', '内部错误！');

					}
				}
			});
	}
	win.show();

});