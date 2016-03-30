
var store = new Ext.data.Store({
	url : 'circuit!getSubCircuitInfo.action',
	baseParams : {
		
		"parentCir": parentCir
		
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [  "cir_no", "source_no", "svc_type",
			"client_name", "a_end_ctp", "z_end_ctp", "a_end_port",
			"z_end_port", "rate", "cir_name", "a_end_user_name",
			"z_end_user_name", "IS_COMPLETE_CIR", "a_end_ne", "a_end_ems",
			"a_end_ems_group", "z_end_ne", "z_end_ems",
			"Z_end_ems_group", "A_CTP_ID", "Z_CTP_ID", "USED_FOR" ])
});

var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : true
});
checkboxSelectionModel.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	stateId : 'subCirListGrid',
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
		header : '资源编号',
		dataIndex : 'source_no',
		width : 100
	}, {
		id : 'circuitType',
		header : "电路类别",
		dataIndex : 'svc_type',
		width : 100,
		renderer : function() {
				return "ETH电路";
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
		width : 100

	}, {
		id : 'type',
		header : '电路类型',
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
		header : '路由名称',
		dataIndex : 'cir_name',
		width : 100
	}, {
		id : 'clientName',
		header : '客户名称',
		dataIndex : 'client_name',
		width : 100
	}, {
		id : 'usedFor',
		header : '用途',
		dataIndex : 'USED_FOR',
		width : 100
	}, {
		id : 'AEndUserName',
		header : 'A端用户',
		dataIndex : 'a_end_user_name',
		width : 100
	}, {
		id : 'ZEndUserName',
		header : 'Z端用户',
		dataIndex : 'z_end_user_name',
		width : 100
	}, {
		id : 'AEMS',
		header : 'A端所属网管',
		dataIndex : 'a_end_ems',
		width : 100
	}, {
		id : 'AEMSGroup',
		header : 'A端所属网管分组',
		dataIndex : 'a_end_ems_group',
		width : 100
	}, {
		id : 'ZEMS',
		header : 'Z端所属网管',
		dataIndex : 'z_end_ems',
		width : 100
	}, {
		id : 'ZEMSGroup',
		header : 'Z端所属网管分组',
		dataIndex : 'Z_end_ems_group',
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
	stateId : 'subCirListGrid', //注意！！！这个ID不能与其他页面的重复
	stateful : true,
	// title:'用户管理',
	cm : cm,
	store : store,
	// autoExpandColumn: 'roleName', // column with this id will be
	// expanded
	stripeRows : true, // 交替行效果
	loadMask : {
		msg : '数据加载中...'
	},
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	bbar : pageTool,
	tbar : [ '-',
   {
		text : '路由详情',
		privilege:viewAuth,
		icon : '../../resource/images/btnImages/setTask.png',
		handler : selectCircuitRoute
	},"-", {
		text : '导出',
		privilege:actionAuth,
		icon : '../../resource/images/btnImages/export.png',
		handler : exportData
	} ]
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
	var cell = gridPanel.getSelectionModel().getSelections();
	var jsonData={
			"parentCir": parentCir,
			"cirNo":cirNo,
			"displayName":displayName,
			"flag":4
	};
	Ext.Ajax.request({
		url : 'circuit-export!exportExcel.action',
		type : 'post',
		params : {
			"jsonString":Ext.encode(jsonData)
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
// -------------------------------路由相关查询-----------------------------------
function selectCircuitRoute() {

	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
			var vCircuit = cell[0].get('cir_no');
			var tiuc_type=cell[0].get('svc_type')==2?1:cell[0].get('svc_type')
			var url = "../circuitManager/apaView.jsp?vCircuit=" + vCircuit+"&serviceType="+tiuc_type;
			parent.addTabPage(url, "路由详情：(" + vCircuit + ")",authSequence);
			} else {
		Ext.Msg.alert('提醒', '请先选择要查询路由的电路！');
	}
}

// ----------------------------DeleteUserFunction---------------------------------

function backpage() {
	window.location.replace("selectCircuitPort.jsp");

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
	store.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				top.Ext.getBody().unmask();
				Ext.Msg.alert('错误', '更新失败！请重新更新');

			}
		}
	});
	win.show();

});