/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// 光复用段
var section = {
	xtype : 'textfield',
	id : 'setion',
	name : 'setion',
	maxLength : 256,
	allowBlank : true
}

var sectionStore = new Ext.data.Store(
{
    proxy : new Ext.data.HttpProxy({
		url : 'multiple-section!selectMultipleSection.action',
		async: false
    }), 
    pageSize:10,
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
       "PM_MULTI_SEC_ID","SEC_NAME"
    ])
});

var section = new Ext.form.ComboBox({
    id: 'setion',
    //width: 150,
    minListWidth: 220,
    store: sectionStore,
    //fieldLabel: '光缆名称',
    valueField: 'SEC_NAME',
    displayField: 'SEC_NAME',
    emptyText : '输入对象名',
    listEmptyText: '未找到匹配的结果',
    loadingText: '搜索中...',
    mode:'remote',  
    pageSize:sectionStore.pageSize,
    queryDelay: 400,
    typeAhead: false,
    autoSelect:false,
    enableKeyEvents : true,
    resizable: true,
    autoScroll:true,
	resizable: true,
    listeners : {
      keypress: function(field, event) {
        field.setValue(field.getRawValue());
        if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
          gKey = field.getValue();
          if(gKey == null || gKey==""){
            return;
          }
        }
      },
      beforequery:function(event){
        if(event.combo.lastQuery!=event.combo.getRawValue()){
        	event.combo.lastQuery=event.combo.getRawValue();
        	querySection(event.combo,event.combo.getRawValue());
          return false;
        }
      },
      scope : this
    }
  }); 
		
function querySection(combo,gKey){
		var jsonString = new Array();
	var map = {
		"userId" : userId,
		"BASE_EMS_GROUP_ID" : Ext.getCmp('emsGroup').getValue(),
		"BASE_EMS_CONNECTION_ID" : Ext.getCmp('ems').getValue(),
		"PM_TRUNK_LINE_ID" : Ext.getCmp('trunkLine').getValue(),
		"SEC_NAME" : gKey,
		"limit" : sectionStore.pageSize
	};
	if (Ext.getCmp('ems').getValue() == "") {
		map = {
			"userId" : userId,
			"BASE_EMS_GROUP_ID" : Ext.getCmp('emsGroup').getValue(),
			"PM_TRUNK_LINE_ID" : Ext.getCmp('trunkLine').getValue(),
			"SEC_NAME" : gKey,
			"limit" : sectionStore.pageSize
		};
	}
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)

	};
	 
    sectionStore.baseParams=jsonData;
	sectionStore.load({
		callback : function(records,options,success){
			if(!success)
				Ext.Msg.alert("提示","模糊搜索出错");
		}
	});
    combo.expand();
}

var jsonString = new Array();
var map = {
	"userId" : userId,
	"BASE_EMS_GROUP_ID" : -99,
	"limit" : 200
};
jsonString.push(map);

var store = new Ext.data.Store({
			url : 'ms-cutover!searchMultiplexSection.action',
			baseParams : {
				"searchCond.taskId" : taskId
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_MULTI_SEC_ID", "SEC_NAME", "STD_WAVE",
							"ACTULLY_WAVE", "DIRECTION", "PM_TRUNK_LINE_ID",
							"TRUNK_NAME", "TYPE", "EMS_NAME", "EMS_GROUP_NAME",
							"BASE_EMS_CONNECTION_ID","CUTOVER_REFRESH_TIME","SEC_STATE_CUTOVER"])
		});
store.load({
	callback : function(r, options, success) {
		if (success) {

		} else {
			Ext.Msg.alert('错误', '查询失败！');
		}
	}
});
//Ext.Ajax.request({
//			url : 'ms-cutover!searchMultiplexSection.action',
//			method : 'POST',
//			baseParams : {
//				"searchCond.taskId" : taskId
//			},
//			success : function(response) {// 回调函数
//
//				var obj = Ext.decode(response.responseText);
//				if (obj.returnResult == 0) {
//					Ext.Msg.alert("信息", obj.returnMessage);
//					// store.rejectChanges() ;
//				} else {
//
//					// 刷新列表
//					var pageTool = Ext.getCmp('pageTool');
//					if (pageTool) {
//						pageTool.doLoad(pageTool.cursor);
//					}
//
//				}
//
//			},
//			error : function(response) {
//				Ext.Msg.alert('错误', '保存失败！');
//			},
//			failure : function(response) {
//				Ext.Msg.alert('错误', '保存失败！');
//			}
//
//		});

// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
			singleSelect : false,
//			header : ""
		});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
				id : 'PM_MULTI_SEC_ID',
				header : 'PM_MULTI_SEC_ID',
				dataIndex : 'PM_MULTI_SEC_ID',
				hidden : true
			}, {
				id : 'BASE_EMS_CONNECTION_ID',
				header : 'BASE_EMS_CONNECTION_ID',
				dataIndex : 'BASE_EMS_CONNECTION_ID',
				hidden : true
			}, {
				id : 'EMS_GROUP_NAME',
				header : '网管分组',
				width : 100,
				dataIndex : 'EMS_GROUP_NAME'
			}, {
				id : 'EMS_NAME',
				header : '网管',
				width : 100,
				dataIndex : 'EMS_NAME'
			}, {
				id : 'TYPE',
				header : '网管类型',
				width : 80,
				dataIndex : 'TYPE',
				renderer : function(v, r, t) {
					if (v == 12) {
						return "U2000 ";
					} else if (v == 21) {
						return "E300 ";
					} else if (v == 22) {
						return "U31 ";
					} else if (v == 31) {
						return "lucent ";
					} else if (v == 41) {
						return "烽火 otnm2000 ";
					} else if (v == 51) {
						return "富士通 ";
					} else {
						return " ";
					}
				}
			}, {
				id : 'TRUNK_NAME',
				header : '干线名称',
				dataIndex : 'TRUNK_NAME',
				width : 150
			}, {
				id : 'SEC_NAME',
				header : "光复用段名称",
				dataIndex : 'SEC_NAME',
				editor : new Ext.form.TextField({
							allowBlank : false
						})
			}, {
				id : 'DIRECTION',
				header : '方向',
				width : 100,
				dataIndex : 'DIRECTION',
				renderer : function(v, r, t) {
					if (v == 1) {
						return "单向";
					} else if (v == 2) {
						return "双向";
					} else {
						return "";
					}
				}
				
			}, {
				id : 'SEC_STATE_CUTOVER',
				header : '复用段割接状态',
				dataIndex : 'SEC_STATE_CUTOVER',
				renderer : function(v, r, t) {
					if (v == 3) {
						return "<font color='red'>重要预警</font>";
					} else if (v == 2) {
						return "<font color='orange'>次要预警</font>";
					} else if (v == 1) {
						return "<font color='blue'>一般预警</font>";
					} else if (v == 0) {
						return "正常";
					} else {
						return "割接准备";
					}
				}
			}, {
				id : 'CUTOVER_REFRESH_TIME',
				header : '割接刷新时间',
				dataIndex : 'CUTOVER_REFRESH_TIME'
			}, {
				id : 'STD_WAVE',
				header : "标称波道数",
				width : 100,
				dataIndex : 'STD_WAVE'
			}, {
				id : 'ACTULLY_WAVE',
				header : "实际波道数",
				width : 100,
				dataIndex : 'ACTULLY_WAVE'
			}]
});

var pageTool = new Ext.PagingToolbar({
			id : 'pageTool',
			pageSize : 200,// 每页显示的记录值
			store : store,
			displayInfo : true,
			displayMsg : '总记录数 {0} - {1} of {2}',
			emptyMsg : "没有记录"
		});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	// title:'任务信息列表',
	cm : cm,
	store : store,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	// tbar: pageTool,
	viewConfig : {
		forceFit : true
	},
	tbar : {
		xtype : "container",
		border : false,
		items : [{
			xtype : "toolbar",
			items : [{
						text : '详情',
//						privilege : viewAuth,
						icon : '../../../resource/images/buttonImages/search.png',
						handler : function() {
							routeDetail();
						}

					}, {
						text : '割接前刷新',
//						privilege : actionAuth,
						icon : '../../../resource/images/buttonImages/reset.png',
						handler : function() {
							sycPm(1);
						}

					}, {
						text : '割接后值刷新',
//						privilege : actionAuth,
						icon : '../../../resource/images/buttonImages/reset.png',
						handler : function() {
							sycPm(2);
						}

					}]
		}]

	}
});

// 查询光复用端信息
function search() {
	var jsonString = new Array();
	var map = {
		"userId" : userId,
		"BASE_EMS_GROUP_ID" : Ext.getCmp('emsGroup').getValue(),
		"BASE_EMS_CONNECTION_ID" : Ext.getCmp('ems').getValue(),
		"PM_TRUNK_LINE_ID" : Ext.getCmp('trunkLine').getValue(),
		"SEC_NAME" : Ext.getCmp('setion').getValue(),
		"limit" : 200
	};
	if (Ext.getCmp('ems').getValue() == "") {
		map = {
			"userId" : userId,
			"BASE_EMS_GROUP_ID" : Ext.getCmp('emsGroup').getValue(),
			"PM_TRUNK_LINE_ID" : Ext.getCmp('trunkLine').getValue(),
			"SEC_NAME" : Ext.getCmp('setion').getValue(),
			"limit" : 200
		};
	}
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)

	};
	store.baseParams = jsonData;
	store.proxy = new Ext.data.HttpProxy({
				url : 'multiple-section!selectMultipleSection.action'
			});
	store.load({
				callback : function(r, options, success) {
					if (success) {

					} else {
						Ext.Msg.alert('错误', '更新失败！请重新更新');
					}
				}
			});
}

/**
 * 同步当前性能
 */
function sycPm( cutoverFlag ) {
	var processKey = "sycMultipleCutover"+new Date().getTime();
	var tabIds = [];
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length < 1) {
		Ext.Msg.alert("提示", "请选择需要同步性能的光复用段！");
	} else {
		var jsonString = new Array();
		for (var i = 0; i < cell.length; i++) {
			var map = {
				"PM_MULTI_SEC_ID" : cell[i].get('PM_MULTI_SEC_ID'),
				"processKey":processKey
			};
			jsonString.push(map);
			tabIds.push("WDM光复用段割接路由详情(" + cell[i].get('SEC_NAME') + ")");
		}

		var jsonData = {
			"jsonString" : Ext.encode(jsonString),
			"cutoverFlag" : cutoverFlag
		};
		Ext.Ajax.request({
					url : 'multiple-section!sycPmByMultiple.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {// 回调函数
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							clearTimer();
							Ext.Msg.alert("信息", obj.returnMessage, function(r) {
										// 刷新列表
										var pageTool = Ext.getCmp('pageTool');
										if (pageTool) {
											pageTool.doLoad(pageTool.cursor);
										}
									});
							// 刷新已经打开的页面
							for (var i = 0; i < tabIds.length; i++) {
								refreshTab(tabIds[i]);

							}
						}
						if (obj.returnResult == 0) {
							clearTimer();
							Ext.Msg.alert("信息", obj.returnMessage);
						}
					},
					error : function(response) {
						clearTimer();
						Ext.Msg.alert('错误', '同步失败！');
					},
					failure : function(response) {
						clearTimer();
						Ext.Msg.alert('错误', '同步失败！');
					}

				});
		showProcessBar(processKey);

	}
}


function routeDetail() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length < 1) {
		Ext.Msg.alert("提示", "请选择需要查看的光复用段光复用段！");
	} else {
		for(var i=0;i<cell.length;i++){
			var mul_id = cell[i].get('PM_MULTI_SEC_ID');
			var sec_name = cell[i].get('SEC_NAME');
			var direction  = cell[i].get('DIRECTION');
	
			var url = "../performanceManager/WDMMultiplexSectionMonitor/routeDetail.jsp?mul_id="
					+ mul_id+"&direction="+direction+ "&curTabId="
					+ Ext.encode("WDM光复用段割接路由详情(" + sec_name + ")");
			parent.addTabPage(url, "WDM光复用段割接路由详情(" + sec_name + ")",authSequence);
		}
	} 
}

function refreshTab(tabIds) {
	var tab = parent.getTab(tabIds);
	if (tab)
		tab.refreshImpl();
}

function init() {
}
Ext.onReady(function() {

	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	}
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;

	var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [gridPanel]
			});
	// win.show();
	init();
});
