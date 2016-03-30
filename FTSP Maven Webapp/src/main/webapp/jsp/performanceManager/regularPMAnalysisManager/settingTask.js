var subIds="";
var store = new Ext.data.Store({
	url : 'regular-pm-analysis!getNeList.action',
	baseParams : {
		"searchCond.emsId" : emsId
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "EMS_DISPLAY_NAME", "BASE_NE_ID", "DISPLAY_NAME", "TYPE", "PRODUCT_NAME", "NE_LEVEL",
			"LAST_COLLECT_TIME", "COLLECT_INTERVAL", "COLLECT_NUMBIC", "COLLECT_PHYSICAL",
			"COLLECT_CTP","SUBNET_DISPLAY_NAME"]),
	sortInfo : {
		field : 'BASE_NE_ID',
		direction : 'DESC'
	}
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),
			checkboxSelectionModel,
			{
				id : 'BASE_NE_ID',
				header : 'neId',
				dataIndex : 'BASE_NE_ID',
				width : 150,
				hidden : true,
				hideable : false
			},
			{
				id : 'EMS_DISPLAY_NAME',
				header : '网管',
				dataIndex : 'EMS_DISPLAY_NAME',
				width : 150
			},
			{
				id : 'SUBNET_DISPLAY_NAME',
				header : '子网',
				dataIndex : 'SUBNET_DISPLAY_NAME',
				width : 150
			},
			{
				id : 'DISPLAY_NAME',
				header : '网元',
				dataIndex : 'DISPLAY_NAME',
				width : 180
			},
			{
				id : 'TYPE',
				header : '类型',
				dataIndex : 'TYPE',
				width : 40,
				renderer : function(v) {
					if (v == 1) {
						return "SDH";
					} else if (v == 2) {
						return "WDM";
					} else if (v == 3) {
						return "OTN";
					} else if (v == 4) {
						return "PTN";
					} else {
						return "";
					}
				}
			},
			{
				id : 'PRODUCT_NAME',
				header : '型号',
				dataIndex : 'PRODUCT_NAME',
				width : 150
			},
			{
				id : 'NE_LEVEL',
				header : '采集方式',
				dataIndex : 'NE_LEVEL',
				width : 60,
				renderer : function(v) {
					if (v == 1) {
						return "每天采";
					} else if (v == 2) {
						return "循环采";
					} else if (v == 3) {
						return "不采集";
					} else {
						return "";
					}
				}
			},
			{
				id : 'LAST_COLLECT_TIME',
				header : '最近采集时间',
				dataIndex : 'LAST_COLLECT_TIME',
				renderer : function(value) {
					return value.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
							value.time)) : "";
				},
				width : 115
			}, {
				id : 'COLLECT_INTERVAL',
				header : '上次采集间隔(天)',
				dataIndex : 'COLLECT_INTERVAL',
				width : 100
			}, {
				id : 'COLLECT_NUMBIC',
				header : '采集计数值',
				dataIndex : 'COLLECT_NUMBIC',
				width : 65,
				renderer : function(v) {
					if (v == 1) {
						return "是";
					} else if (v == 0) {
						return "否";
					}
				}
			}, {
				id : 'COLLECT_PHYSICAL',
				header : '采集物理量',
				dataIndex : 'COLLECT_PHYSICAL',
				width : 65,
				renderer : function(v) {
					if (v == 1) {
						return "是";
					} else if (v == 0) {
						return "否";
					}
				}
			}, {
				id : 'COLLECT_CTP',
				header : '采集通道',
				dataIndex : 'COLLECT_CTP',
				width : 60,
				renderer : function(v) {
					if (v == 1) {
						return "是";
					} else if (v == 0) {
						return "否";
					}
				}
			} ]
});

var typeCombo;
(function() {
	var searchStore = new Ext.data.ArrayStore({
		fields : [ 'value', 'display' ],
		data : [ [ 0, '全部' ], [ 1, 'SDH' ], [ 2, 'WDM' ], [ 3, 'OTN' ], [ 4, 'PTN' ] ]
	});
	typeCombo = new Ext.form.ComboBox({
		store : searchStore,
		valueField : 'value',
		displayField : 'display',
		mode : 'local',
		triggerAction : 'all',
		editable : false,
		width : 100,
		value : 0,
		listeners : {
			'select' : function(combo, record, index) {
				modelCombo.reset();
				var jsonData = {
					"searchCond.emsId" : emsId,
					"searchCond.type" : combo.getValue()
				};
				modelStore.baseParams = jsonData;
				modelStore.load({
					callback : function(records, options, success) {
						if (!success)
							Ext.Msg.alert("提示", "查询出错");
					}
				});
			}
		}
	});
})();

var modelStore = new Ext.data.Store({
	url : 'regular-pm-analysis!getProductNames.action',
	baseParams : {
		"searchCond.emsId" : emsId,
		"searchCond.type" : 0
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "PRODUCT_NAME" ])
});

var modelCombo = new Ext.form.ComboBox({
	store : modelStore,
	triggerAction : "all",
	editable : false,
	valueField : 'PRODUCT_NAME',
	displayField : 'PRODUCT_NAME',
	width : 100,
	value : '全部',
	resizable : true 
}); 

function getTree(id,pageX,pageY){ 
	var treeParams = {
			rootId : emsId,
			rootVisible:true,
			rootType : 2,   
			leafType : 3,
			checkModel : "multiple" 
		};
	var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
	var treePanel = new Ext.Panel({
		id:"treePanel", 
		region : "center",
		boxMinWidth : 230,
		boxMinHeight : 300, 
		forceFit : true,  
		html : '<iframe id="tree_panel" name="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	var treeWindow = new Ext.Window({ 
		title:"子网",
		layout : 'border',
		id : 'treeWindow',
		height : 400,
		width : 260,
		modal : true,
		plain : true,
		pageX :pageX,
		pageY :pageY+20, 
        autoScroll:true,
		items : treePanel,
		buttons : [ {
			text : '确定', 
			handler : function(){
                 var selectedTargets=window.frames["tree_panel"].getCheckedNodes([ "nodeId","text"],"all",3,"all"); 
                 var str='';
                 subIds='';
                 for(var i=0;i<selectedTargets.length;i++){
                	 str+=selectedTargets[i].text+","; 
                	 subIds+=selectedTargets[i].nodeId+","; 
                 }   
                 subIds=subIds.substring(0,subIds.lastIndexOf(',')); 
                 Ext.getCmp(id).setValue(str.substring(0, str.lastIndexOf(','))); 
                 treeWindow.close();
			}
		}, {
			text : '取消', 
			handler : function() {
				treeWindow.close();
			}
		} ]
	});
	treeWindow.show();
}
var subNetTextField = new Ext.form.TextField({
	id : 'subNetTextField',
	fieldLabel : '子网',
	width :120,
	listeners: {
        focus:function(field){ 
        	getTree(field.id,field.getPosition()[0],field.getPosition()[1]);
        	field.blur();
        }
    }
}); 

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	// viewConfig : {
	// forceFit : true
	// },
	tbar : [ '-',"子网",subNetTextField,'-', "类型：", typeCombo, '-', "型号：", modelCombo, '-',{
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png', 
		handler : function() {
			var jsonData = {
					"searchCond.emsId" : emsId,
					"searchCond.type" : typeCombo.getValue(),
					"searchCond.productName" : modelCombo.getValue(),
					"searchCond.subIds" : subIds
			}; 
			store.baseParams = jsonData;
			store.load({
				callback : function(records, options, success) {
					if (!success)
						Ext.Msg.alert("提示", "查询出错");
				}
			}); 
		} 
	}, '-', {
		text : '采集方式',
		icon : '../../../resource/images/btnImages/clock.png',
		menu : {
			items : [ {
				text : '每天采',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('NE_LEVEL', 1);
					}
				}
			}, {
				text : '循环采',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('NE_LEVEL', 2);
					}
				}
			}, {
				text : '不采集',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('NE_LEVEL', 3);
					}
				}
			} ]
		}
	}, "-", {
		text : '计数值',
		icon : '../../../resource/images/btnImages/page_green.png',
		menu : {
			items : [ {
				text : '采集计数值',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('COLLECT_NUMBIC', 1);
					}
				}
			}, {
				text : '取消计数值',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('COLLECT_NUMBIC', 0);
						if (cell[i].get("COLLECT_PHYSICAL") == 0) {
							cell[i].set('NE_LEVEL', 3);
						}
					}
				}
			} ]
		}
	}, {
		text : '物理量',
		icon : '../../../resource/images/btnImages/page.png',
		menu : {
			items : [ {
				text : '采集物理量',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('COLLECT_PHYSICAL', 1);
					}
				}
			}, {
				text : '取消物理量',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('COLLECT_PHYSICAL', 0);
						if (cell[i].get("COLLECT_NUMBIC") == 0) {
							cell[i].set('NE_LEVEL', 3);
						}
					}
				}
			} ]
		}
	}, {
		text : '通道',
		icon : '../../../resource/images/btnImages/page_red.png',
		menu : {
			items : [ {
				text : '采集通道',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('COLLECT_CTP', 1);
					}
				}
			}, {
				text : '取消通道',
				handler : function() {
					var cell = gridPanel.getSelectionModel().getSelections();
					if (cell.length == 0) {
						Ext.Msg.alert("提示", "请选择需要设置的网元！");
						return;
					}
					for ( var i = 0; i < cell.length; i++) {
						cell[i].set('COLLECT_CTP', 0);
					}
				}
			} ]
		}
	} ],
	buttons : [ {
		text : '确定',
		// privilege : modAuth,
		handler : function() {
			saveTask(0);
		}
	}, {
		text : '取消',
		handler : function() {
			var win = parent.Ext.getCmp('setTaskWin');
			if (win) {
				win.close();
			}
		}
	}, {
		text : '应用',
		// privilege : modAuth,
		handler : function() {
			saveTask(1);
		}
	} ]
});

// 保存任务内容
function saveTask(isApply) {
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {
		gridPanel.getEl().mask("正在执行,请稍候...");
		var neList = new Array();
		for ( var i = 0; i < cell.length; i++) {
			var ne = {
				'BASE_NE_ID' : cell[i].get('BASE_NE_ID'),
				'NE_LEVEL' : cell[i].get('NE_LEVEL'),
				'COLLECT_NUMBIC' : cell[i].get('COLLECT_NUMBIC'),
				'COLLECT_PHYSICAL' : cell[i].get('COLLECT_PHYSICAL'),
				'COLLECT_CTP' : cell[i].get('COLLECT_CTP')
			};
			neList.push(Ext.encode(ne));
		}
		var jsonData = {
			"modifyList" : neList
		};
		Ext.Ajax.request({
			url : 'regular-pm-analysis!modifyNes.action',
			params : jsonData,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
					if (1 == result.returnResult) {
						if (isApply) {
							// 提交修改，不然store.getModifiedRecords();数据会累加
							store.commitChanges();
							store.load();
						} else {
							var win = parent.Ext.getCmp('setTaskWin');
							if (win) {
								win.close();
							}
						}
					}
				}
			},
			failure : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			},
			error : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			}
		});
	} else {
		if (!isApply) {
			// 没有改动，确定关闭窗口
			var win = parent.Ext.getCmp('setTaskWin');
			if (win) {
				win.close();
			}
		}
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();

	store.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "加载失败");
		}
	});
});