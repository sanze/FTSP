/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

Ext.QuickTips.init(); 
// 光复用段
var section = {
	xtype : 'textfield',
	id : 'setion',
	name : 'setion',
	maxLength : 256,
	allowBlank : true
};

var sectionStore = new Ext.data.Store({
			proxy : new Ext.data.HttpProxy({
						url : 'multiple-section!selectMultipleSection.action',
						async : false
					}),
			pageSize : 10,
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_MULTI_SEC_ID", "SEC_NAME"])
		});

var section = new Ext.form.ComboBox({
			id : 'setion',
			// width: 150,
			minListWidth : 220,
			store : sectionStore,
			// fieldLabel: '光缆名称',
			valueField : 'SEC_NAME',
			displayField : 'SEC_NAME',
			emptyText : '输入对象名',
			listEmptyText : '未找到匹配的结果',
			loadingText : '搜索中...',
			mode : 'remote',
			pageSize : sectionStore.pageSize,
			queryDelay : 400,
			typeAhead : false,
			autoSelect : false,
			enableKeyEvents : true,
			resizable : true,
			autoScroll : true,
			listeners : {
				keypress : function(field, event) {
					field.setValue(field.getRawValue());
					if (event.getKey() == event.ENTER) {// 输入回车后开始过滤节点树
						gKey = field.getValue();
						if (gKey == null || gKey == "") {
							return;
						}
					}
				},
				expand: function(combo) {
					querySection(combo, combo.getRawValue());
				},
				beforequery : function(event) {
					if (event.combo.lastQuery != event.combo.getRawValue()) {
						event.combo.lastQuery = event.combo.getRawValue();
						querySection(event.combo, event.combo.getRawValue());
						return false;
					}
				},
				scope : this
			}
		});

function querySection(combo, gKey) {
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

	sectionStore.baseParams = jsonData;
	sectionStore.load({
				callback : function(records, options, success) {
					if (!success)
						Ext.Msg.alert("提示", "模糊搜索出错");
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
			url : 'multiple-section!selectMultipleSection.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_MULTI_SEC_ID", "SEC_NAME", "STD_WAVE",
							"ACTULLY_WAVE", "DIRECTION", "PM_TRUNK_LINE_ID",
							"PM_UPDATE_TIME", "SEC_STATE", "ROUTE_UPDATE_TIME",
							"TRUNK_NAME", "TYPE", "EMS_NAME", "EMS_GROUP_NAME",
							"BASE_EMS_CONNECTION_ID","FACTORY"])
		});

Ext.Ajax.request({
			url : 'multiple-section!selectMultipleSection.action',
			method : 'POST',
			params : {
				"jsonString" : Ext.encode(jsonString)
			},

			success : function(response) {// 回调函数

				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 0) {
					Ext.Msg.alert("提示", obj.returnMessage);
					// store.rejectChanges() ;
				} else {

					// 刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}

				}

			},
			error : function(response) {
				Ext.Msg.alert('错误', '保存失败！');
			},
			failure : function(response) {
				Ext.Msg.alert('错误', '保存失败！');
			}

		});

// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({

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
				header : "<span style='font-weight:bold'>光复用段名称</span>",
				dataIndex : 'SEC_NAME',
				tooltip:'可编辑列',
				renderer : function(v, r, t) {
					//return v.replace(/^\s+/,"");
					return v.replace(/\s/ig,'');
				},
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
				id : 'STD_WAVE',
				header : "<span style='font-weight:bold'>标称波道数</span>",
				dataIndex : 'STD_WAVE',
				tooltip:'可编辑列',
				editor : new Ext.form.NumberField({
							allowDecimals : false,
							allowNegative : false,
							minValue : 1,
							allowBlank : false
						})
			}, {
				id : 'ACTULLY_WAVE',
				header : "<span style='font-weight:bold'>实际波道数</span>",
				dataIndex : 'ACTULLY_WAVE',
				tooltip:'可编辑列',
				editor : new Ext.form.NumberField({
							allowDecimals : false,
							allowNegative : false,
							minValue : 1,
							allowBlank : false
						})
			}, {
				id : 'PM_UPDATE_TIME',
				header : '当前性能更新时间',
				dataIndex : 'PM_UPDATE_TIME'
			}, {
				id : 'SEC_STATE',
				header : '复用段状态',
				dataIndex : 'SEC_STATE',
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
						return "";
					}
				}
			}, {
				id : 'ROUTE_UPDATE_TIME',
				header : '路由更新时间',
				dataIndex : 'ROUTE_UPDATE_TIME'
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
	tbar : {
		xtype : "container",
		border : false,
		items : [{
			// tbar第一行工具栏
			xtype : "toolbar",
			items : ['-','网管分组', emsGroupCombo, '-','网管', emsCombo, '-','干线', trunkCombo,
			         '-','复用段', section,'-', {
						text : '查询',
						privilege : viewAuth,
						icon : '../../../resource/images/btnImages/search.png',
						handler : function() {
							search();
						}

					},'-', {
						text : '重置',
						icon : '../../../resource/images/btnImages/arrow_undo.png',
						handler : function() {
							reset();
						}
					}]
		}, {
			// tbar第二行工具栏
			xtype : "toolbar",
			items : ['-',{
						text : '新增',
						privilege : addAuth,
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							add();
						}

					}, {
						text : '删除',
						privilege : delAuth,
						icon : '../../../resource/images/btnImages/delete.png',
						handler : function() {
							deleteSection();
						}

					},'-', {
						text : '保存',
						privilege : modAuth,
						icon : '../../../resource/images/btnImages/disk.png',
						handler : function() {
							save();
						}

					},'-', {
						text : '自动路由',
						privilege : addAuth,
						disabled:true,
						handler : function() {// 处理“自动路由”按钮点击事件
							var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
							// 没有勾选复用段
							if (cell.length < 1) {
								Ext.Msg.alert("提示", "请选择需要设置路由的光复用段");
							} else if(cell.length>1){
								Ext.Msg.alert("提示", "请不要多选");
							}else{// 勾选了一条复用段信息，获取网管Id
								var emsId = cell[0].get('BASE_EMS_CONNECTION_ID');
								//根据emsId创建一个'window'
								sp.showWindow(emsId);
							}
						}

					}, {
						text : '手动路由',
						privilege : addAuth,
						handler : function() {
							addRouteMaun();
						}

					}, '-',{
						text : '详情',
						privilege : viewAuth,
						icon : '../../../resource/images/btnImages/setTask.png',
						handler : function() {
							routeDetail();
						}

					},'-', {
						text : '同步当前性能',
						privilege : actionAuth,
						icon : '../../../resource/images/btnImages/sync.png',
						handler : function() {
							sycPm();
						}

					}, '-',{
						text : '设置复用段顺序',
						privilege : actionAuth,
						handler : function() {
							sortMultipleSection();
						}

					},
					{
						text : '导出',
						hidden:true,
						handler : function() {
							exportSection();
						}

					},
					{
						text : '导入',
						hidden:true,
						handler : function() {
							importSection();
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
 * 重置
 */
function reset() {
	var firstValue = -99;
	Ext.getCmp('emsGroup').setValue(firstValue);
	Ext.getCmp('ems').reset();
	Ext.getCmp('trunkLine').reset();
	Ext.getCmp('setion').reset();
	emsStore.baseParams = {
			'emsGroupId' : firstValue,
			"displayAll" : false
		};
	emsStore.load({
		// 回调函数
		callback : function(records, options, success) {// records：加载的数据数组
		}
	});
	trunkLineStore.removeAll();
}

/**
 * 新增光复用段
 */
function add() {

	// 新建光复用段
	var url = "addMultipleSection.jsp";
	addMultipleSectionWindow = new Ext.Window({
				id : 'addMultipleSectionWindow',
				title : '新增光复用段',
				width : 340,
				height : 370,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
	addMultipleSectionWindow.show();
	// 调节高度
	if (addMultipleSectionWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addMultipleSectionWindow.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		addMultipleSectionWindow.setHeight(addMultipleSectionWindow
				.getInnerHeight());
	}
	// 调节宽度
	if (addMultipleSectionWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addMultipleSectionWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		addMultipleSectionWindow.setWidth(addMultipleSectionWindow
				.getInnerWidth());
	}
	addMultipleSectionWindow.center();

}

function addRouteType(id, name) {
	// 新建路由方式
	var url = "addRouteType.jsp?id=" + id + "&name=" + name;
	addRouteTypeWindow = new Ext.Window({
				id : 'addRouteTypeWindow',
				title : '路由建立方式选择',
				width : 320,
				height : 200,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
	addRouteTypeWindow.show();
}

// 删除光复用段
function deleteSection() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {

		Ext.Msg.confirm('提示', '删除光复用段不可恢复，是否删除选中的光复用段？', function(button) {
			if (button == 'yes') {

				for (var i = 0; i < cell.length; i++) {
					var map = {
						"PM_MULTI_SEC_ID" : cell[i].get('PM_MULTI_SEC_ID')
					};
					jsonString.push(map);
				}
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				store.commitChanges();
				Ext.Ajax.request({
							url : 'multiple-section!deleteMultipleSection.action',
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
		Ext.Msg.alert('信息', '请先选取光复用段！');
	}
}

function save() {

	var jsonString = new Array();
	var cell = store.getModifiedRecords();
	if (cell.length > 0) {

		for (var i = 0; i < cell.length; i++) {
			var std = cell[i].get('STD_WAVE');
			var act = cell[i].get('ACTULLY_WAVE');
			if (act > std) {
				Ext.Msg.alert("提示", "实际波道数不能大于标称波道数！");
				return;
			}
			var map = {
				"PM_MULTI_SEC_ID" : cell[i].get('PM_MULTI_SEC_ID'),
				"PM_TRUNK_LINE_ID":cell[i].get('PM_TRUNK_LINE_ID'),
				"SEC_NAME" : cell[i].get('SEC_NAME').replace(/\s/ig,''),
				"STD_WAVE" : cell[i].get('STD_WAVE'),
				"ACTULLY_WAVE" : cell[i].get('ACTULLY_WAVE')
			};
			jsonString.push(map);
		}
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		// store.commitChanges();
		Ext.Ajax.request({
					url : 'multiple-section!modifyMultipleSection.action',
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
							// store.rejectChanges() ;
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
		Ext.Msg.alert('信息', '没有需要保存的光复用段！');
	}

}

/**
 * 同步当前性能
 */
function sycPm() {
	var processKey = "sycMultiple" + new Date().getTime();
	var tabIds = [];
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length < 1) {
		Ext.Msg.alert("提示", "请选择需要同步性能的光复用段！");
	} else {
		var jsonString = new Array();
		for (var i = 0; i < cell.length; i++) {
			var map = {
				"PM_MULTI_SEC_ID" : cell[i].get('PM_MULTI_SEC_ID'),
				"processKey" : processKey
			};
			jsonString.push(map);
			tabIds.push("路由详情(" + cell[i].get('SEC_NAME') + ")");
		}

		var jsonData = {
			"jsonString" : Ext.encode(jsonString)

		};

		Ext.Ajax.request({
					url : 'multiple-section!sycPmByMultiple.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {// 回调函数
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							clearTimer();
							Ext.Msg.alert("提示", obj.returnMessage, function(r) {
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
							Ext.Msg.alert("提示", obj.returnMessage);
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
function addRouteMaun() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length < 1) {
		Ext.Msg.alert("提示", "请选择需要设置路由的光复用段！");
	} else if (cell.length == 1) {
		var mul_id = cell[0].get('PM_MULTI_SEC_ID');
		var sec_name = cell[0].get('SEC_NAME');
		var emsId = cell[0].get('BASE_EMS_CONNECTION_ID');
		var factory = cell[0].get('FACTORY');
		var direction = cell[0].get('DIRECTION');
		var url = "../performanceManager/multipleSection/addRouteManu.jsp?mul_id="
				+ mul_id + "&emsId=" + emsId + "&direction=" + direction+"&factory="+factory;
		parent.addTabPage(url, "路由设置(" + sec_name + ")");
	} else {
		Ext.Msg.alert("提示", "请不要多选！");
	}
}

function addRouteMaunType(id, name, emsId) {
	var url = "../performanceManager/multipleSection/addRouteManu.jsp?mul_id="
			+ id + "&emsId=" + emsId;
	parent.addTabPage(url, "路由设置(" + name + ")");

}

function routeDetail() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length < 1) {
		Ext.Msg.alert("提示", "请选择需要查看的光复用段光复用段！");
	} else if (cell.length == 1) {
		var mul_id = cell[0].get('PM_MULTI_SEC_ID');
		var sec_name = cell[0].get('SEC_NAME');
		var direction = cell[0].get('DIRECTION');

		var url = "../performanceManager/multipleSection/routeDetail.jsp?mul_id="
				+ mul_id
				+ "&direction="
				+ direction
				+ "&curTabId="
				+ Ext.encode("路由详情(" + sec_name + ")");
		parent.addTabPage(url, "路由详情(" + sec_name + ")");
	} else {
		Ext.Msg.alert("提示", "请不要多选！");
	}
}

/**
 * 设置光复用段
 */
function sortMultipleSection() {
	// 新建光复用段
	var url = "sortMultipleSection.jsp";
	sortMultipleSectionWindow = new Ext.Window({
				id : 'sortMultipleSectionWindow',
				title : '干线内光复用分段顺序设置',
				width : 720,
				height : 450,
				isTopContainer : true,
				modal : true,
				plain : true, // 是否为透明背景
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'
			});
	sortMultipleSectionWindow.show();
	// 调节高度
	if (sortMultipleSectionWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		sortMultipleSectionWindow
				.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		sortMultipleSectionWindow.setHeight(sortMultipleSectionWindow
				.getInnerHeight());
	}
	// 调节宽度
	if (sortMultipleSectionWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		sortMultipleSectionWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		sortMultipleSectionWindow.setWidth(sortMultipleSectionWindow
				.getInnerWidth());
	}
	sortMultipleSectionWindow.center();
}

// 导出光复用段所有信息
function exportSection(){
	var url = 'multiple-section!exportAllInformation.action';
	var cell = gridPanel.getSelectionModel().getSelections();
	var jsonString = new Array();
	if (cell.length < 1) {
		
	} else {
		for (var i = 0; i < cell.length; i++) {
			var map = {
				"PM_MULTI_SEC_ID" : cell[i].get('PM_MULTI_SEC_ID')
			};
			jsonString.push(map);
		}
	}
	var jsonData = {
		"jsonString" : Ext.encode(jsonString)

	};

	top.Ext.getBody().mask('正在导出到Excel，请稍候...');
	Ext.Ajax.request({
				url : url,
				method : 'POST',
				params : jsonData,
				success : function(response) {
					top.Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 1) {
						window.location.href = "download.action?"
								+ Ext.urlEncode({filePath : obj.returnMessage});
					}
					if (obj.returnResult == 0) {
						Ext.Msg.alert("提示", "导出数据失败！");
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
var fileUploadForm = new Ext.FormPanel({
			fileUpload : true,
			frame : true,
			bodyStyle : 'padding: 10px 10px 0 10px;',
			labelWidth : 100,
			defaults : {
				anchor : '95%',
				allowBlank : false,
				msgTarget : 'side'
			},
			items : [{
						xtype : 'displayfield',
						value : "<font color=red>上传文件类型为xls或xlsx文件</font>"
					}, {
						xtype : 'fileuploadfield',
						id : 'uploadFile',
						// emptyText: '选择需要导入文件',
						fieldLabel : '导入数据',
						name : 'uploadFile',
						buttonText : '',
						regex : /^.*?\.(xls|xlsx)$/,
						buttonCfg : {
							iconCls : 'uploader'
						}
					}]
		});
// 上传文件用
var fileUploadWindow = new Ext.Window({
			id : 'fileUploadWindow',
			title : '文件导入',
			width : 500,
			height : 160,
			minWidth : 350,
			minHeight : 100,
			layout : 'fit',
			plain : false,
			resizable : false,
			closeAction : 'hide',// 关闭窗口
			bodyStyle : 'padding:1px;',
			buttonAlign : 'right',
			items : [fileUploadForm],
			buttons : [{
				text : '确定',
				handler : function() {
					if (fileUploadForm.getForm().isValid()) {
						fileUploadForm.getForm().submit({
							url : 'multiple-section!UploadSectionAll.action',
							waitTitle : "文件上传",
							waitMsg : '正在上传并导入,请稍候...',
							timeOut : 90000000,
							params : {
								"jsonString" : Ext.getCmp('uploadFile')
										.getValue()
							},
							success : function(form, action) {
								var obj = Ext
										.decode(action.response.responseText);
								// 刷新列表
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								Ext.MessageBox.hide();
								fileUploadWindow.hide();
								Ext.getCmp('uploadFile').reset();
								Ext.Msg.alert("提示", "文件导入成功！");

							},
							failure : function(form, action) {
								var obj = Ext
										.decode(action.response.responseText);
								Ext.MessageBox.hide();
								fileUploadWindow.hide();

								Ext.Msg.alert("错误", obj.returnMessage);
							},
							error : function(form, action) {
								var obj = Ext
										.decode(action.response.responseText);
								Ext.MessageBox.hide();
								fileUploadWindow.hide();

								Ext.Msg.alert("错误", obj.returnMessage);
							}
						})
					}
				}
			}, {
				text : '取消',
				handler : function() {
					fileUploadWindow.hide();
				}
			}]
		});

function importSection() {
	Ext.getCmp('uploadFile').reset();
	fileUploadWindow.show();
}

function refreshTab(tabIds) {
	var tab = parent.getTab(tabIds);
	if (tab)
		tab.refreshImpl();
}

store.on("load", function(s) {
			store.rejectChanges ();
		});
	
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
