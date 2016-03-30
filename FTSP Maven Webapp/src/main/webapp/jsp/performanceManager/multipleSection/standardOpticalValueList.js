/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// 厂家combox
Ext.QuickTips.init(); 
/**
 * 1.HW 2.ZTE 3.朗讯 4.烽火 5.ALC 9.富士通
 * 
 * @type
 */
var factoryStore=new Ext.data.ArrayStore({
	fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
	data:[{key:0,value:"全部"}]
});
factoryStore.loadData(FACTORY,true);
var factoryCombo = {
	xtype : 'combo',
	id : 'factoryCombo',
	name : 'factoryCombo',
	fieldLabel : '厂家',
	store : factoryStore,
	displayField : "displayName",
	valueField : 'value',
	mode : 'local',
	triggerAction : 'all',
	editable : false,
	width : 150,
	value : 0
}

// 网管combox
var typeCombo = {
	xtype : 'combo',
	id : 'typeCombo',
	name : 'typeCombo',
	fieldLabel : '光放类型',
	store : new Ext.data.ArrayStore({
				fields : ['value', 'displayName'],
				data : [['0', '全部'], ['1', '后置放大器'], ['2', '前置放大器'],
						['3', '线路放大器']]
			}),
	displayField : "displayName",
	valueField : 'value',
	mode : 'local',
	triggerAction : 'all',
	editable : false,
	width : 150,
	value : "0"
}

// 光放型号
var modelStore = new Ext.data.Store({
			url : 'multiple-section!selectMultipleModel.action',
			baseParams : {
				"limit" : 200
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["MODEL"])
		});
// modelStore.load();

// 网管combox
var modelCombo = {
	xtype : 'combo',
	id : 'modelCombo',
	name : 'modelCombo',
	fieldLabel : '光放型号',
	store : modelStore,
	displayField : "MODEL",
	valueField : 'MODEL',
	triggerAction : 'all',
	editable : false,
	width : 150,
	value : "全部"
}
var jsonString = new Array();
var map = {
	"limit" : 200
};
jsonString.push(map);

var store = new Ext.data.Store({
			url : 'multiple-section!selectStandOptVal.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_STD_OPT_AMP_ID", "FACTORY", "TYPE", "MODEL",
							"MAX_OUT", "MIN_GAIN", "MAX_GAIN", "TYPICAL_GAIN",
							"MAX_IN", "MIN_IN", "TYPICAL_IN"])
		});
store.load();
// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
			singleSelect : true,
			header : ""
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
				id : 'PM_STD_OPT_AMP_ID',
				header : 'PM_STD_OPT_AMP_ID',
				dataIndex : 'PM_STD_OPT_AMP_ID',
				hidden : true
			}, {
				id : 'FACTORY',
				header : '厂家',
				width : 40,
				dataIndex : 'FACTORY',
				renderer : function(v, r, t) {
			    	for(var fac in FACTORY){
			    		if(v==FACTORY[fac]['key']){
				    		return FACTORY[fac]['value'];
				    	}
			    	}
			    	return v;
				}
			}, {
				id : 'TYPE',
				header : '光放类型',
				width : 80,
				dataIndex : 'TYPE',
				renderer : function(v, r, t) {
					if (v == 1) {
						return "后置放大器";
					} else if (v == 2) {
						return "前置放大器";
					} else if (v == 3) {
						return "线路放大器";
					} else {
						return "";
					}

				}
			}, {
				id : 'MODEL',
				header : '光放型号',
				width : 100,
				dataIndex : 'MODEL'
			}, {
				id : 'MAX_OUT',
				header : "<span style='font-weight:bold'>最大输出功率(dBm)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'MAX_OUT',
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'MIN_GAIN',
				header : "<span style='font-weight:bold'>增益最小值(dB)</span>",
				dataIndex : 'MIN_GAIN',
				width : 100,
				tooltip:'可编辑列',
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'MAX_GAIN',
				header : "<span style='font-weight:bold'>增益最大值(dB)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'MAX_GAIN',
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'TYPICAL_GAIN',
				header : "<span style='font-weight:bold'>增益典型值(dB)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'TYPICAL_GAIN',
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'MAX_IN',
				header : "<span style='font-weight:bold'>输入最大值(dB)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'MAX_IN',
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'MIN_IN',
				header : "<span style='font-weight:bold'>输入最小值(dB)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'MIN_IN',
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'TYPICAL_IN',
				header : "<span style='font-weight:bold'>输入典型值(dB)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'TYPICAL_IN',
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}]
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
			forceFit : true,
			tbar : ['-','厂家：', factoryCombo, '-','光放类型：', typeCombo, '-','光放型号：', modelCombo,
			        '-',{
						text : '查询',
						privilege : viewAuth,
						icon : '../../../resource/images/btnImages/search.png',
						handler : function() {
							select();
						}

					}, '-',{
						text : '新增',
						privilege : addAuth,
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							add();
						}

					},{
						text : '删除',
						privilege : delAuth,
						icon : '../../../resource/images/btnImages/delete.png',
						handler : function() {
							deleteSection();
						}

					}, '-',{
						text : '保存',
						privilege : modAuth,
						icon : '../../../resource/images/btnImages/disk.png',
						handler : function() {
							save();
						}

					}]

		});

function select() {
	var jsonString = new Array();
	var factory = Ext.getCmp('factoryCombo').getValue();
	if (Ext.getCmp('factoryCombo').getValue() == "0") {
		factory = "";
	}

	var type = Ext.getCmp('typeCombo').getValue();
	if (Ext.getCmp('typeCombo').getValue() == "0") {
		type = "";
	}
	var model = Ext.getCmp('modelCombo').getValue();
	if (Ext.getCmp('modelCombo').getValue() == "全部") {
		model = "";
	}
	var map = {
		"FACTORY" : factory,
		"TYPE" : type,
		"MODEL" : model,
		"limit" : 200
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)

	};
	store.baseParams = jsonData;
	store.proxy = new Ext.data.HttpProxy({
				url : 'multiple-section!selectStandOptVal.action'
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

// 查询干线信息
function add() {
	var url = "addStandOptVal.jsp";
	addStandOptValWindow = new Ext.Window({
				id : 'addStandOptValWindow',
				title : '新增光放类型',
				width : 400,
				height : 425,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
	addStandOptValWindow.show();
	// 调节高度
	if (addStandOptValWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addStandOptValWindow.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		addStandOptValWindow.setHeight(addStandOptValWindow.getInnerHeight());
	}
	// 调节宽度
	if (addStandOptValWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addStandOptValWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		addStandOptValWindow.setWidth(addStandOptValWindow.getInnerWidth());
	}
	addStandOptValWindow.center();

}

function deleteSection() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {

		Ext.Msg.confirm('提示', '确认删除选择的光放单元？', function(button) {
			if (button == 'yes') {

				for (var i = 0; i < cell.length; i++) {
					var map = {
						"PM_STD_OPT_AMP_ID" : cell[i].get('PM_STD_OPT_AMP_ID')
					};
					jsonString.push(map);
				}
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				store.commitChanges();
				Ext.Ajax.request({
							url : 'multiple-section!deleteStandOptVal.action',
							method : 'POST',
							params : jsonData,

							success : function(response) {// 回调函数

								var obj = Ext.decode(response.responseText);
								if (obj.returnResult == 1) {
									Ext.Msg.alert("提示", obj.returnMessage,
											function(r) {
												// 刷新列表
												var pageTool = Ext
														.getCmp('pageTool');
												if (pageTool) {
													pageTool
															.doLoad(pageTool.cursor);
												}
											});
								}
								if (obj.returnResult == 0) {
									Ext.Msg.alert("提示", obj.returnMessage);
								}

							},
							error : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							},
							failure : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							}

						});
			}
		});

	} else {
		Ext.Msg.alert('信息', '请选择需要删除的光放单元！');
	}
}

function save() {

	var jsonString = new Array();
	var cell = store.getModifiedRecords();
	//alert(cell.length);
	if (cell.length > 0) {
		for (var i = 0; i < cell.length; i++) {
			// 判断大小是否正确
			var maxOut = cell[i].get('MAX_OUT');
			if (maxOut.length < 1) {
				Ext.Msg.alert("提示", "输出最大功率不能为空！");
				return;
			}
			// “增益最小值”、“增益最大值”、“增益典型值”的数据格式为正整数
			var min_gain = cell[i].get('MIN_GAIN');
			if (min_gain < 0) {
				Ext.Msg.alert("提示", "增益最小值必须大于0！");
				return;
			}
			var max_gain = cell[i].get('MAX_GAIN');
			if (max_gain < 0) {
				Ext.Msg.alert("提示", "增益最大值必须大于0！");
				return;
			}
			var typicalGain = cell[i].get('TYPICAL_GAIN');
			if (typicalGain.length < 1) {
				Ext.Msg.alert("提示", "增益典型值不能为空！");
				return;
			}
			if (typicalGain < 0) {
				Ext.Msg.alert("提示", "增益典型值必须大于0！");
				return;
			}
			// “增益最小值”<“增益典型值”<“增益最大值”
			if (min_gain != "" && min_gain > typicalGain) {
				Ext.Msg.alert("提示", "增益最小值要小于增益典型值！");
				return;
			}
			if (max_gain != "" && typicalGain > max_gain) {
				Ext.Msg.alert("提示", "增益典型值要小于增益最大值！");
				return;
			}
			if (min_gain != "" && max_gain != "" && min_gain > max_gain) {
				Ext.Msg.alert("提示", "增益最小值要小于增益最大值！");
				return;
			}
			// “输入最小值”<“输入典型值”<“输入最大值”。
			var min_in = cell[i].get('MIN_IN');
			var max_in = cell[i].get('MAX_IN');
			var typical_in = cell[i].get('TYPICAL_IN');
			if (min_in != "" && typical_in != "" && min_in > typical_in) {
				Ext.Msg.alert("提示", "输入最小值要小于输入典型值！");
				return;
			}
			if (typical_in != "" && max_in != "" && typical_in > max_in) {
				Ext.Msg.alert("提示", "输入典型值要小于输入最大值！");
				return;
			}
			if (min_in != "" && max_in != "" && min_in > max_in) {
				Ext.Msg.alert("提示", "输入最小值要小于输入最大值！");
				return;
			}
			var map = {
				"PM_STD_OPT_AMP_ID" : cell[i].get('PM_STD_OPT_AMP_ID'),
				"MAX_OUT" : cell[i].get('MAX_OUT'),
				"MIN_GAIN" : cell[i].get('MIN_GAIN'),
				"MAX_GAIN" : cell[i].get('MAX_GAIN'),
				"TYPICAL_GAIN" : cell[i].get('TYPICAL_GAIN'),
				"MIN_IN" : cell[i].get('MIN_IN'),
				"MAX_IN" : cell[i].get('MAX_IN'),
				"TYPICAL_IN" : cell[i].get('TYPICAL_IN')
			};
			jsonString.push(map);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		// store.commitChanges();
		Ext.Ajax.request({
					url : 'multiple-section!modifyStandOptVal.action',
					method : 'POST',
					params : jsonData,

					success : function(response) {// 回调函数

						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							store.commitChanges();
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
						Ext.Msg.alert('错误', '保存失败！');
					},
					failure : function(response) {
						Ext.Msg.alert('错误', '保存失败！');
					}

				});

	} else {
		Ext.Msg.alert('信息', '没有需要保存的光放单元！');
	}

}

store.on("load", function(s) {
			store.rejectChanges ();
		});
		
function init() {
	// Ext.getCmp('factoryCombo').setValue(0);
	// Ext.getCmp('typeCombo').setValue(0);
	// Ext.getCmp('modelCombo').setValue("");

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
	init();
		// win.show();
});
