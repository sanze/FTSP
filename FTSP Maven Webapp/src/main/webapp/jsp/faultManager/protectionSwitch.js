var descLevel1 = "省", descLevel2 = "市", descLevel3 = "区", descLevel4 = "街道";
var curLevel = "";
var tree = null;
var myPageSize = 200;
function getSelection() {
	// 选择tree中选中的节点
	var iframe = window.frames["tree_panel"];
    var sels = iframe.getCheckedNodes(["nodeId", "nodeLevel", "text"], "top",
            [1], "all");
    if(sels.length>0){
    	Ext.Msg.alert("提示", "请勿选择网管分组节点！");
    	return {length:0};
    }
    sels = iframe.getCheckedNodes(["nodeId", "nodeLevel", "text"], "top",
            [2, 3, 4], "all");
    if(!sels || sels.length ==0){
    	Ext.Msg.alert("提示", "请先选择要查询的节点！");
    	return null;
    }
	var rv = {"1":[],"2":[],"3":[],"4":[]};
	for ( var i = 0; i < sels.length; i++) {
		rv[("_" + sels[i].nodeLevel).substr(1, 1)].push(sels[i].nodeId);
	}
    rv.length = sels.length;
	return rv;
}

var westPanel;
(function() {
	var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		containerId : "westPanel",
		checkModel : "multiple",
		leafType : 4
	};
	var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
	function treeFilter(tree, parent, node) {
		if (node.attributes["nodeLevel"] == NodeDefine.NE
				&& node.attributes["additionalInfo"]
				&& node.attributes["additionalInfo"]["TYPE"] == NodeDefine.TYPE) {
			return false;
		}
	}
	westPanel = new Ext.Panel({
		id : "westPanel",
		region : "west",
		width : 280,
		autoScroll : true,
		boxMinWidth : 230,
		boxMinHeight : 260,
		forceFit : true,
		collapsed : false, // initially collapse the group
		collapsible : false,
		collapseMode : 'mini',
		split : true,
		//		filterBy : treeFilter,
		html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0 />'
	});
})();
var centerPanel;
var dataStore;
(function() {
	dataStore = new Ext.data.Store({
		url : 'fault!getProtectionSwitch.action',
		baseParams : {
			start : 0,
			limit : myPageSize
		},
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "EMS_GROUP_NAME", "EMS_NAME", "SUBNET_NAME", "NE_NAME",
				"ALARM_PRO_SWITCH_ID", "BASE_EMS_CONNECTION_ID", "BASE_NE_ID",
				"NOTIFICATIONID", "EMS_TIME", "NE_TIME", "PROTECT_TYPE",
				"SWITCH_RESON", "PROTECTION_GROUP_TYPE", "GROUP_NAME", "OBJECT_TYPE",
				"PROTECTED_TP_DESC", "SWITCH_AWAY_FROM_TP_DESC", "SWITCH_TO_TP_DESC",
				"NATIVE_EMS_NAME","PROTECT_CATEGORY" ])
	});
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var protTypeNames = [ "", "设备保护", "线路保护", "ATM保护", "RPR保护", "WDM保护" ];
	var protGrpTypeNames = ["1+1 MSP","1:N MSP","2F BLSR","4F BLSR","1+1 ATM","1:N ATM"];
	var switchReason = ["NA","RESTORED","SIGNAL_FAIL","SIGNAL_MISMATCH","SIGNAL_DEGRADE","AUTOMATIC_SWITCH","MANUAL"];
	var protType = {"NT_EPROTECTION_SWITCH":"设备保护",
			"NT_PROTECTION_SWITCH":"线路保护",
			"环保护":"线路保护",
			"NT_ATMPROTECTION_SWITCH":"ATM保护",
			"NT_RPRPROTECTION_SWITCH":"RPR保护",
			"NT_WDMPROTECTION_SWITCH":"WDM保护",
			getValue:function(key){
				var rv = protType[key];
				if(!rv){
					rv = key;
				}
				return rv;
			}};

	var cm = new Ext.grid.ColumnModel({
		// specify any defaults for each column
		defaults : {
			sortable : true,
			width : 100
		// columns are not sortable by default
		},
		columns : [ new Ext.grid.RowNumberer({
			width : 26
		}), selModel, {
			id : 'emsGroup',
			header : '网管分组',
			dataIndex : 'EMS_GROUP_NAME',
			renderer : function(v, m, r) {
				return v == "(NULL)" || !v ? "-" : v;
			}
		}, {
			id : 'ems',
			header : '网管',
			dataIndex : 'EMS_NAME'
		}, {
			id : 'subnet',
			header : '子网',
			dataIndex : 'SUBNET_NAME',
			renderer : function(v, m, r) {
				return v == "(NULL)" || !v ? "-" : v;
			}
		}, {
			id : 'ne',
			header : '网元',
			dataIndex : 'NE_NAME'
		}, {
			id : 'protectType',
			header : '保护类别',
			dataIndex : 'PROTECT_CATEGORY',
			renderer : function(v, m, r) {
				return protType.getValue(v);
			}
		}, {
			id : 'levelName',
			header : '保护组名称',
			dataIndex : 'GROUP_NAME'
		}, {
			id : 'protGrpType',
			header : '保护组类型',
			dataIndex : 'PROTECT_TYPE',
			renderer : function(v, m, r) {
				return protGrpTypeNames[v >> 0];
			}
		}, {
			id : 'switchReason',
			header : '倒换原因',
			dataIndex : 'SWITCH_RESON',
			renderer : function(v, m, r) {
				return switchReason[v >> 0];
			}
		}, {
			id : 'emsTime',
			header : '发生时间',
			dataIndex : 'EMS_TIME',
			renderer : function(value) {
				return !!value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
						value.time)) : "";
			}
		}, {
			id : 'protectedTP',
			header : '被保护对象',
			dataIndex : 'PROTECTED_TP_DESC'
		}, {
			id : 'switchAwayFromTP',
			header : '从对象',
			dataIndex : 'SWITCH_AWAY_FROM_TP_DESC'
		}, {
			id : 'switchToTP',
			header : '切换到对象',
			dataIndex : 'SWITCH_TO_TP_DESC'
		} ]
	});
	var pageTool = new Ext.PagingToolbar({
		id:'pageTool',
		pageSize: myPageSize,//每页显示的记录值
		store: dataStore,
		displayInfo: true,
		displayMsg : '当前 {0} - {1} ，总数 {2}',
		emptyMsg: "没有记录"
	});
	var gridPanel = new Ext.grid.EditorGridPanel({
		id : "gridPanel",
		autoScroll : true,
		// title:'用户管理',
		cm : cm,
		border : false,
		store : dataStore,
		stripeRows : true, // 交替行效果
		loadMask : true,
		selModel : selModel, // 必须加不然不能选checkbox
		forceFit : true,
		frame : false,
		bbar:pageTool
	});
	
	var today = new Date();
	var todayStr = today.format("yyyy-MM-dd 23:59:59");
	today.setDate(today.getDate() - 7);
	var lastWeekStr = today.format("yyyy-MM-dd 00:00:00");
	centerPanel = new Ext.Panel({
		id : 'centerPanel',
		region : 'center',
		//border : false,
		layout : 'fit',
		autoScroll : true,
		tbar : [ "-", "发生时间：从", {
			xtype : 'textfield',
			id : 'startTime',
			name : 'startTime',
			fieldLabel : '开始时间',
			allowBlank : false,
			width : 150,
			value : lastWeekStr,
			sideText : '<font color=red>*</font>',
			cls : 'Wdate',
			listeners : {
				'focus' : function() {
					WdatePicker({
						el : "startTime",
						isShowClear : false,
						readOnly : true,
						dateFmt : 'yyyy-MM-dd HH:mm:ss',
						autoPickDate : true,
                        maxDate : Ext.getCmp('endTime').getValue()
					});
					this.blur();
				}
			}
		}, " 到 ", {
			xtype : 'textfield',
			id : 'endTime',
			name : 'endTime',
			fieldLabel : '开始时间',
			allowBlank : false,
			width : 150,
			value : todayStr,
			cls : 'Wdate',
			listeners : {
				'focus' : function() {
					WdatePicker({
						el : "endTime",
						isShowClear : false,
						readOnly : true,
						dateFmt : 'yyyy-MM-dd HH:mm:ss',
                        minDate:Ext.getCmp('startTime').getValue(),
						autoPickDate : true,
						maxDate : '%y-%M-%d'
					});
					this.blur();
				}
			}
		}, {
			xtype : 'button',
			icon : '../../resource/images/btnImages/search.png',
			text : '查询',
			handler : function(){
				var targets = getSelection();
				if(!targets){
					return;
				}
				var param = {
					'startTime' : Ext.getCmp('startTime').getValue(),
					'endTime' : Ext.getCmp('endTime').getValue()
				};
				if(targets["1"].length>0)
					param.emsGroup = "(" + targets["1"].join(",") + ")";
				if(targets["2"].length>0)
					param.ems = "(" + targets["2"].join(",") + ")";
				if(targets["3"].length>0)
					param.subnet = "(" + targets["3"].join(",") + ")";
				if(targets["4"].length>0)
					param.ne = "(" + targets["4"].join(",") + ")";
				dataStore.baseParams.jsonString = Ext.encode(param);
				dataStore.removeAll();
				dataStore.load();
			}
		} ],
		items : [ gridPanel ]
	});
})();
Ext.onReady(function() {
			Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";

			Ext.Ajax.timeout = 900000;
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			// Ext.Msg = top.Ext.Msg;

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ westPanel, centerPanel ],
				renderTo : Ext.getBody()
			});
			win.show();
		});