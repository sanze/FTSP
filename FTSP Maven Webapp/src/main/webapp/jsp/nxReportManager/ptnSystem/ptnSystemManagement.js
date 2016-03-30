var VIEW_DATA = null;
function addSys(sysType) {

	var addWindow = new Ext.Window(
			{
				id : 'addWindow',
				title : '新增'+sysTypeRenderer(sysType),
				width : Ext.getBody().getWidth() * 0.8,
				height : Ext.getBody().getHeight() - 80,
				closeAction : 'close',
				border : 'fit',
				stateful : false,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe id="addAndEdit" name = "addAndEdit" src = "addAndEdit.jsp?sysType= '
						+ sysType
						+ '" height="100%" width="100%"  frameBorder=0 border=0/>',
				buttons : [ {
					text : "确定",
					handler : function() {
						var addAndEdit = window.frames["addAndEdit"];
						// addAndEdit.selectedUnitGrid.stopEditing();
						addAndEdit.save();
					}
				}, {
					text : "取消",
					handler : function() {
						addWindow.close();
					}
				} ]
			});
	addWindow.show();
}
function viewSys(sysType, sysId) {
	var viewWindow = new Ext.Window(
			{
				id : 'viewWindow',
				title : sysTypeRenderer(sysType),
				width : Ext.getBody().getWidth() * 0.8,
				height : Ext.getBody().getHeight() - 80,
				closeAction : 'close',
				border : 'fit',
				stateful : false,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe id="addAndEdit" name = "addAndEdit" src = "addAndEdit.jsp?sysType= '
						+ sysType
						+ '&sysId='
						+ sysId
						+ '" height="100%" width="100%"  frameBorder=0 border=0/>',
				buttons : [ {
					text : "关闭",
					handler : function() {
						viewWindow.close();
					}
				} ]
			});
	viewWindow.show();
}

/**
 * 删除
 */
function delSys() {
	var selections = centerPanel.selModel.getSelections();
	if (!selections || selections.length == 0) {
		Ext.Msg.alert("提示", "请先选系统！");
		return;
	}
	Ext.Msg.confirm("确认", "是否删除选中的系统？", function(btn) {
		if (btn == 'yes') {
			var idList = new Array();
			for ( var i = 0; i < selections.length; i++) {
				idList.push(selections[i].get("T_RESOURCE_PTN_SYS_ID"));
			}
			var params = {
				"paramMap.idToDel" : idList.toString()
			};
			Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
				url : 'nx-report!delPtnSys.action',
				params : params,
				method : 'POST',
				success : function(response) {
					Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					if (result.returnResult == 0) {
						Ext.Msg.alert("提示", result.returnMessage);
					}
					if (result.returnResult == 1) {
						Ext.Msg.alert("提示", result.returnMessage);
						pageTool.doLoad(pageTool.cursor);
					}
				},
				failure : function(response) {
					Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					Ext.Msg.alert("提示", result.returnMessage);
				},
				error : function(response) {
					Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					Ext.Msg.alert("提示", result.returnMessage);
				}
			});
		}
	});

}
var store = new Ext.data.Store({
	url : 'nx-report!getPtnSysList.action',
	baseParams : {},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "GROUP_NAME", "EMS_DISPLAY_NAME", "SYS_NAME", "SYS_CAPACITY",
			"SYS_TYPE", "T_RESOURCE_PTN_SYS_ID", "TARGET_ID" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
var centerPanel = new Ext.grid.GridPanel({
	id : 'centerPanel',
	// border : false,
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : sm, // 必须加不然不能选checkbox
	autoScroll : true,
	forceFit : true,
	frame : false,
	bbar : pageTool,
	tbar : [ "网管分组：", emsGroupCombo, "网管：", emsCombo, "类型：", sysTypeCombo, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/search.png',
		text : '查询',
		privilege : modAuth,
		handler : function() {
			var emsGroup = Ext.getCmp('emsGroupCombo').getValue();
			var ems = Ext.getCmp('emsCombo').getValue();
			var sysType = Ext.getCmp('sysTypeCombo').getValue();
			var params = {
				'paramMap.emsGroupId' : emsGroup,
				'paramMap.emsId' : ems,
				'paramMap.sysType' : sysType,
				'limit' : 200
			};
			store.baseParams = params;
			store.load();
		}
	}, {
		xtype : 'splitbutton',
		text : '新增',
		privilege : addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : function(b, e) {
			this.showMenu();
		},
		menu : {
			items : SYS_MENU
		}
	}, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/delete.png',
		text : '删除',
		privilege : delAuth,
		handler : delSys
	}, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/search.png',
		text : '详情',
		privilege : viewAuth,
		handler : function() {
			var selections = centerPanel.selModel.getSelections();
			if (!selections || selections.length == 0) {
				Ext.Msg.alert("提示", "请选取要查看的系统！");
				return;
			}
			if (selections.length > 1) {
				Ext.Msg.alert("提示", "只能选取一个系统进行查看！");
				return;
			}
			var sysType = selections[0].get("SYS_TYPE");
			var sysId = selections[0].get("T_RESOURCE_PTN_SYS_ID");
			VIEW_DATA = {
				sysName : selections[0].get("SYS_NAME"),
				sysCapacity : selections[0].get("SYS_CAPACITY"),
				targetId : selections[0].get("TARGET_ID")
			};
			viewSys(sysType, sysId);
		}
	} ]
});

Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			Ext.Ajax.timeout = 900000;
			emsStore.baseParams.displayAll = true;
			var win = new Ext.Viewport({
				id : 'win',
				layout : 'fit',
				items : [ centerPanel ]
			});
			win.show();
			// initData();
		});