/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
// 定义全局变量来衡量修改后是否已经保存
var isSaveopposite = true;
var neIdopposite;
var neRouteIdopposite;
var neNameopposite;
var virPortopposite = new Array();

var pmTypeStore = new Ext.data.ArrayStore({
			fields : [{
						name : 'value'
					}, {
						name : 'displayName'
					}]
		});
pmTypeStore.loadData([['1', '输入光功率(dBm)'], ['2', '输出光功率(dBm)'],
		['3', '衰耗值(dB)'], ['4', '段衰耗(dB)']]);

var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id
};
jsonString.push(map);
// 查询光放型号
var modelTypeStore = new Ext.data.Store({
			url : 'multiple-section!selectModelType.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_STD_OPT_AMP_ID", "MODEL"])
		});
// modelTypeStore.load();

// 正向
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 2
};
jsonString.push(map);

var oppositeUpStore = new Ext.data.Store({
			url : 'multiple-section!selectMultipleSectionNe.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["MULTI_SEC_NE_ID", "MULTI_SEC_ID", "BASE_NE_ID",
							"EMS_GROUP_NAME", "EMS_NAME", "NE_NAME",
							"PRODUCT_NAME", "STATION_NAME", "AREA_NAME"])
		});

// ************************* 任务信息列模型 ****************************
var oppositeUpCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
			singleSelect : true,
			header : ""
		});
var oppositeUpCm = new Ext.grid.ColumnModel({
			// specify any defaults for each column
			defaults : {
				sortable : true
				// columns are not sortable by default
			},
			columns : [oppositeUpCheckboxSelectionModel, {
						id : 'MULTI_SEC_NE_ID',
						header : 'MULTI_SEC_NE_ID',
						dataIndex : 'MULTI_SEC_NE_ID',
						hidden : true
					}, {
						id : 'MULTI_SEC_ID',
						header : 'MULTI_SEC_ID',
						dataIndex : 'MULTI_SEC_ID',
						hidden : true
					}, {
						id : 'BASE_NE_ID',
						header : 'BASE_NE_ID',
						dataIndex : 'BASE_NE_ID',
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
						id : 'NE_NAME',
						header : '网元路由顺序',
						width : 150,
						dataIndex : 'NE_NAME'
					}, {
						id : 'AREA_NAME',
						header : top.FieldNameDefine.AREA_NAME,
						dataIndex : 'AREA_NAME',
						width : 150
					}, {
						id : 'STATION_NAME',
						header : top.FieldNameDefine.STATION_NAME,
						dataIndex : 'STATION_NAME'
					}, {
						id : 'PRODUCT_NAME',
						header : '网元型号',
						width : 100,
						dataIndex : 'PRODUCT_NAME'
					}]
		});

var oppositeUpPageTool = new Ext.PagingToolbar({
			id : 'oppositeUpPageTool',
			pageSize : 200,// 每页显示的记录值
			store : oppositeUpStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var oppositeUpPanel = new Ext.grid.GridPanel({
	id : "oppositeUpPanel",
	region : "north",
	flex : 4,
	// title:'任务信息列表',
	cm : oppositeUpCm,
	store : oppositeUpStore,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: true, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : oppositeUpCheckboxSelectionModel, // 必须加不然不能选checkbox
	bbar : oppositeUpPageTool,
	listeners : {
		'rowclick' : function(oppositeUpPanel, rowIndex, e) {
			e.preventDefault();// 阻止默认事件的传递
			if (!isPortopposite) {
				Ext.Msg.alert('信息', '请先保存端口修改信息！');
				return;
			}
			var record = oppositeUpPanel.getSelectionModel().getSelected();
			if (!record) {
				return;
			}
			var jsonString = new Array();
			var map = {
				"MULTI_SEC_NE_ID" : record.get('MULTI_SEC_NE_ID')
			};
			jsonString.push(map);
			var jsonData = {
				"jsonString" : Ext.encode(jsonString),
				"DIRECTION" : 2
			};

			oppositeDownLeftStore.proxy = new Ext.data.HttpProxy({
						url : 'multiple-section!selectPtpRouteList.action'
					});
			oppositeDownLeftStore.baseParams = jsonData;
			oppositeDownLeftStore.load({
						callback : function(r, options, success) {
							if (success) {
								oppositeDownLeftGridPanel.getSelectionModel()
										.selectFirstRow();
							} else {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
			oppositeDownRightStore.proxy = new Ext.data.HttpProxy({
						url : 'multiple-section!selectSubPtpRouteList.action'
					});
			oppositeDownRightStore.baseParams = jsonData;
			oppositeDownRightStore.load({
						callback : function(r, options, success) {
							if (success) {
								oppositeDownRightGridPanel.getSelectionModel()
										.selectFirstRow();
							} else {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
			// 网元id赋值
			neIdopposite = record.get('BASE_NE_ID');
			neRouteIdopposite = record.get('MULTI_SEC_NE_ID');
			neNameopposite = record.get('NE_NAME');
			
			// 查询光放型号
// var jsonString = new Array();
// var map = {
// "BASE_NE_ID" : record.get('BASE_NE_ID')
// };
// jsonString.push(map);
// var jsonData = {
// "jsonString" : Ext.encode(jsonString)
// };
//
// modelTypeStore.proxy = new Ext.data.HttpProxy({
// url : 'multiple-section!selectModelType.action'
// });
// modelTypeStore.baseParams = jsonData;
// modelTypeStore.load({
// callback : function(r, options, success) {
// if (success) {
//
// } else {
// Ext.Msg.alert('错误', '查询失败！请重新查询');
// }
// }
// });
		}
	},
	tbar : {
		xtype : "container",
		border : false,
		items : [{
					// tbar第一行工具栏
					xtype : "toolbar",
					items : ['网元顺序设置']
				}, {
					// tbar第二行工具栏
					xtype : "toolbar",
					items : ['-',{
								text : '添加网元',
								icon : '../../../resource/images/btnImages/add.png',
								handler : function() {
									addNeopposite();
								}

							}, {
								text : '移除网元',
								icon : '../../../resource/images/btnImages/delete.png',
								handler : function() {
									deleteNeopposite();
								}

							},'-', {
								text : '上移',
								icon : '../../../resource/images/btnImages/up.png',
								handler : function() {
									upopposite(oppositeUpPanel, oppositeUpStore);

								}

							}, {
								text : '下移',
								icon : '../../../resource/images/btnImages/down.png',
								handler : function() {
									downopposite(oppositeUpPanel,
											oppositeUpStore);
								}

							},'-', {
								text : '保存',
								icon : '../../../resource/images/btnImages/disk.png',
								handler : function() {
									saveopposite();
								}

							}]
				}]

	}
});

oppositeUpPanel.getStore().addListener({
		beforeload:function(store,records,options){
				// 查询光放型号
			var jsonString = new Array();
			var map = {
				"FACTORY" : factory
			};
			jsonString.push(map);
			var jsonData = {
				"jsonString" : Ext.encode(jsonString)
			};

			modelTypeStore.proxy = new Ext.data.HttpProxy({
						url : 'multiple-section!selectModelType.action'
					});
			modelTypeStore.baseParams = jsonData;
			modelTypeStore.load({
						callback : function(r, options, success) {
							if (success) {
								
							} else {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
   		 }
	
});
/**
 * 设置正向网元
 * 
 * @param {}
 *            result tree 返回结果集
 */
function setNeopposite(result) {
	var isAgain = false;
	var text = "";
	// 获取网元相关信息
	for (var i = 0; i < result.length; i++) {
		var neId = result[i]["nodeId"];
		// 判断网元是否已被选中
		for (var j = 0; j < oppositeUpStore.getCount(); j++) {
			if (neId == oppositeUpStore.getAt(j).get('BASE_NE_ID')) {
				text += result[i]["text"] + ", ";
				isAgain = true;
			}
		}
		if (!isAgain) {
			var jsonString = new Array();
			var map = {
				"BASE_NE_ID" : neId
			};
			jsonString.push(map);

			var jsonData = {
				"jsonString" : Ext.encode(jsonString)
			};
			Ext.Ajax.request({
						url : 'multiple-section!selectByNeId.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {// 回调函数
							var obj = Ext.decode(response.responseText);
							// 查询成功以后，record赋值
							var select = oppositeUpPanel.getSelectionModel()
									.getSelected();
							var index = oppositeUpStore.indexOf(select);
							var neRouteData = {
								BASE_NE_ID : obj.BASE_NE_ID,
								EMS_GROUP_NAME : obj.EMS_GROUP_NAME,
								EMS_NAME : obj.EMS_NAME,
								NE_NAME : obj.NE_NAME,
								PRODUCT_NAME : obj.PRODUCT_NAME,
								STATION_NAME : obj.STATION_NAME,
								AREA_NAME : obj.AREA_NAME,
								MULTI_SEC_ID : mul_id,
								MULTI_SEC_NE_ID : ""
							}
							var record = oppositeUpStore.recordType;
							var p = new record(neRouteData);
							if (index == -1) {
								oppositeUpStore.insert(oppositeUpStore
												.getCount(), p);
							} else {
								oppositeUpStore.insert(index, p);
							}

							oppositeUpPanel.getView().refresh();
							oppositeUpPanel.getSelectionModel().selectLastRow();
							isSaveopposite = false;

						},
						error : function(response) {
							Ext.Msg.alert('错误', '保存失败！');
						},
						failure : function(response) {
							Ext.Msg.alert('错误', '保存失败！');
						}

					});
		}

	}
	if (text.length > 1) {
		Ext.Msg.alert('提示', text + "已经添加过！");
	}
	Ext.getCmp('addNeForwardWindow').close();

}

// 正向新增网元
function addNeopposite() {
if (!isPortopposite) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}
	var url = "addTree.jsp?level=4&type=2&checkModel=multiple&rootType=2&rootId="
			+ emsId + "&rootVisible=" + true;
	// var url = "addTree.jsp?level=4&type=1&checkModel=multiple";
	addNeForwardWindow = new Ext.Window({
				id : 'addNeForwardWindow',
				title : '网元选择',
				// width : 210,
				autoWidth : true,
				height : 400,
				// layout : 'fit',
				plain : false,
				modal : true,
				resizable : false,
				// closeAction : 'hide',// 关闭窗口
				bodyStyle : 'padding:1px;',
				buttonAlign : 'center',
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'

			});
	addNeForwardWindow.show();
	// 调节高度
	if (addNeForwardWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addNeForwardWindow.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		addNeForwardWindow.setHeight(addNeForwardWindow.getInnerHeight());
	}
	// 调节宽度
	if (addNeForwardWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addNeForwardWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		addNeForwardWindow.setWidth(addNeForwardWindow.getInnerWidth());
	}
	addNeForwardWindow.center();
}
/**
 * 移除网元
 */
function deleteNeopposite() {
if (!isPortopposite) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}
	var select = oppositeUpPanel.getSelectionModel().getSelections();

	if (select.length > 0) {
		if (oppositeDownLeftStore.getCount() > 0) {
			Ext.Msg.confirm("确认", "选择网元已经添加端口顺序数据，是否删除？", function(button) {
						if (button == 'yes') {
							// 标记为需要保存
							isSaveopposite = false;
							oppositeUpStore.remove(select);
							if(oppositeUpStore.getCount()>0){
								oppositeUpPanel.getView().refresh();
								oppositeUpPanel.getSelectionModel().selectLastRow();
							}else{
								oppositeDownLeftStore.removeAll();
								oppositeDownRightStore.removeAll();
							}
						}
					});
		} else {
			// 增加豆蔻绑定判断，先删除端口，未完成
			Ext.Msg.confirm("确认", "你确定要移除所选网元？", function(button) {
						if (button == 'yes') {
							// 标记为需要保存
							isSaveopposite = false;
							oppositeUpStore.remove(select);
							if(oppositeUpStore.getCount()>0){
								oppositeUpPanel.getView().refresh();
								oppositeUpPanel.getSelectionModel().selectLastRow();
							}else{
								oppositeDownLeftStore.removeAll();
								oppositeDownRightStore.removeAll();
							}
						}
					});
		}
	} else {
		Ext.Msg.alert("提示", "请选择要删除的网元！");
	}
}

/**
 * 上移
 * 
 * @param {}
 *            oppositeUpPanel panel
 * @param {}
 *            oppositeUpStore store
 */
function upopposite(oppositeUpPanel, oppositeUpStore) {
	if (!isPortopposite) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}
	var record = oppositeUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert("提示", "请选择要移动的网元！");
		return;
	}
	var index = oppositeUpStore.indexOf(record);
	if (index == 0) {
		return;
	}
	// 标记为需要保存
	isSaveopposite = false;
	oppositeUpStore.remove(record);
	oppositeUpStore.insert(index - 1, record);
	oppositeUpPanel.getView().refresh();
	oppositeUpPanel.getSelectionModel().selectRow(index - 1);
}

/**
 * 下移
 * 
 * @param {}
 *            oppositeUpPanel
 * @param {}
 *            oppositeUpStore
 */
function downopposite(oppositeUpPanel, oppositeUpStore) {
if (!isPortopposite) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}
	var record = oppositeUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert("提示", "请选择要移动的网元！");
		return;
	}
	var index = oppositeUpStore.indexOf(record);
	if (index == oppositeUpStore.getCount() - 1) {
		return;
	}
	// 标记为需要保存
	isSaveopposite = false;
	oppositeUpStore.remove(record);
	oppositeUpStore.insert(index + 1, record);
	oppositeUpPanel.getView().refresh();
	oppositeUpPanel.getSelectionModel().selectRow(index + 1);
}

/**
 * 保存网元顺序
 */
function saveopposite() {
	if (!isPortopposite) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}
	var jsonString = new Array();
	for (var i = 0; i < oppositeUpStore.getCount(); i++) {
		var map = {
			"MULTI_SEC_NE_ID" : oppositeUpStore.getAt(i).get('MULTI_SEC_NE_ID'),
			"MULTI_SEC_ID" : oppositeUpStore.getAt(i).get('MULTI_SEC_ID'),
			"BASE_NE_ID" : oppositeUpStore.getAt(i).get('BASE_NE_ID'),
			"DIRECTION" : 2
		};
		jsonString.push(map);
	}

	var jsonData = {
		"mulId" : mul_id,
		"direction" : 2,
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
				url : 'multiple-section!saveNeForward.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {// 回调函数
					isSaveopposite = true;
					var obj = Ext.decode(response.responseText);
					if (obj.returnResult == 1) {
						Ext.Msg.alert("提示", obj.returnMessage, function(r) {
									// 刷新列表
									var oppositeUpPageTool = Ext
											.getCmp('oppositeUpPageTool');
									if (oppositeUpPageTool) {
										oppositeUpPageTool
												.doLoad(oppositeUpPageTool.cursor);
									}
									oppositeUpPanel.getSelectionModel()
											.selectFirstRow();
									if (oppositeUpPanel.getView().getRow(0)) {
										oppositeUpPanel.getView().getRow(0)
												.click();
									}
									neIdopposite = oppositeUpStore.getAt(0)
											.get('BASE_NE_ID')
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

// 正向路由设置，端口设置
var oppositeDownTitleGridPanel = new Ext.Panel({

	id : "oppositeDownTitleGridPanel",
	region : "north",
	// flex : 1,
	// cm : oppositeUpCm,
	// store : oppositeUpStore,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	// stripeRows : true, // 交替行效果
	// loadMask : true,
	// selModel : oppositeUpCheckboxSelectionModel, // 必须加不然不能选checkbox
	// bbar : oppositeUpPageTool,
	// title:'任务信息列表',
	// stripeRows : true, // 交替行效果
	// loadMask : true,
	// tbar: pageTool,
	viewConfig : {
		forceFit : true
	},
	tbar : {
		xtype : "container",
		border : false,
		items : [{
					// tbar第一行工具栏
					xtype : "toolbar",
					items : ['网元内端口顺序设定']
				}, {
					// tbar第一行工具栏
					xtype : "toolbar",
					items : ['-',{
						text : '重置',
						icon : '../../../resource/images/btnImages/arrow_undo.png',
						handler : function() {
							refreshopposite();

						}

					}, '-',{
						text : '主备路由切换',
						handler : function() {
							exchangeopposite();
						}

					}, '-',{
						text : '保存',
						icon : '../../../resource/images/btnImages/disk.png',
						handler : function() {
							savePortopposite();
						}

					}]
				}]

	}
});

/**
 * 正向重置
 */
function refreshopposite() {

	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
		isPortopposite = true;
	oppositeDownLeftStore.reload();
	oppositeDownRightStore.reload();

}

/**
 * 主被路由切换
 */
function exchangeopposite() {

	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
		isPortopposite = false;
	var tempStore = new Ext.data.Store({
				url : 'multiple-section!selectMultipleSection.action',
				baseParams : {
					"jsonString" : Ext.encode(jsonString)
				},
				reader : new Ext.data.JsonReader({
							totalProperty : 'total',
							root : "rows"
						}, ["PM_MULTI_SEC_PTP_ID", "MULTI_SEC_ID",
								"MULTI_SECT_NE_ROUTE_ID", "PTP_ID",
								"EQUIP_NAME", "PTP_NAME", "PM_STD_OPT_AMP_ID",
								"PM_TYPE", "NOTE", "CALCULATE_POINT",
								"CUT_PM_VALUE", "ROUTE_TYPE"])
			});
	// 先将左边store的值赋给临时store
	var i = tempStore.getCount();
	oppositeDownLeftStore.each(function(record) {
				var rec = {
					PM_MULTI_SEC_PTP_ID : record.get('PM_MULTI_SEC_PTP_ID'),
					MULTI_SEC_ID : record.get('MULTI_SEC_ID'),
					MULTI_SECT_NE_ROUTE_ID : record
							.get('MULTI_SECT_NE_ROUTE_ID'),
					PTP_ID : record.get('PTP_ID'),
					EQUIP_NAME : record.get('EQUIP_NAME'),
					PTP_NAME : record.get('PTP_NAME'),
					PM_STD_OPT_AMP_ID : record.get('PM_STD_OPT_AMP_ID'),
					PM_TYPE : record.get('PM_TYPE'),
					NOTE : record.get('NOTE'),
					CALCULATE_POINT : record.get('CALCULATE_POINT'),
					CUT_PM_VALUE : record.get('CUT_PM_VALUE'),
					ROUTE_TYPE : record.get('ROUTE_TYPE')
				}
				var record = tempStore.recordType;
				var p = new record(rec);
				tempStore.insert(i, p);
				i++;

			});
	oppositeDownLeftStore.removeAll();
	// 将右边store的值赋给左边变量
	var j = oppositeDownLeftStore.getCount();
	oppositeDownRightStore.each(function(record) {
				var rec = {
					PM_MULTI_SEC_PTP_ID : record.get('PM_MULTI_SEC_PTP_ID'),
					MULTI_SEC_ID : record.get('MULTI_SEC_ID'),
					MULTI_SECT_NE_ROUTE_ID : record
							.get('MULTI_SECT_NE_ROUTE_ID'),
					PTP_ID : record.get('SUB_PTP_ID'),
					EQUIP_NAME : record.get('EQUIP_NAME'),
					PTP_NAME : record.get('SUB_PTP_NAME'),
					PM_STD_OPT_AMP_ID : record.get('SUB_PM_STD_OPT_AMP_ID'),
					PM_TYPE : record.get('SUB_PM_TYPE'),
					NOTE : record.get('SUB_NOTE'),
					CALCULATE_POINT : record.get('SUB_CALCULATE_POINT'),
					CUT_PM_VALUE : record.get('SUB_CUT_PM_VALUE'),
					ROUTE_TYPE : record.get('SUB_ROUTE_TYPE')
				}
				var record = oppositeDownLeftStore.recordType;
				var p = new record(rec);
				oppositeDownLeftStore.insert(j, p);
				j++;
			});
	oppositeDownRightStore.removeAll();
	// 将临时store的值赋给右边
	var k = oppositeDownRightStore.getCount();
	tempStore.each(function(record) {
				var rec = {
					PM_MULTI_SEC_PTP_ID : record.get('PM_MULTI_SEC_PTP_ID'),
					MULTI_SEC_ID : record.get('MULTI_SEC_ID'),
					MULTI_SECT_NE_ROUTE_ID : record
							.get('MULTI_SECT_NE_ROUTE_ID'),
					SUB_PTP_ID : record.get('PTP_ID'),
					EQUIP_NAME : record.get('EQUIP_NAME'),
					SUB_PTP_NAME : record.get('PTP_NAME'),
					SUB_PM_STD_OPT_AMP_ID : record.get('PM_STD_OPT_AMP_ID'),
					SUB_PM_TYPE : record.get('PM_TYPE'),
					SUB_NOTE : record.get('NOTE'),
					SUB_CALCULATE_POINT : record.get('CALCULATE_POINT'),
					SUB_CUT_PM_VALUE : record.get('CUT_PM_VALUE'),
					SUB_ROUTE_TYPE : record.get('ROUTE_TYPE')
				}
				var record = oppositeDownRightStore.recordType;
				var p = new record(rec);
				oppositeDownRightStore.insert(k, p);
				k++;
			});

}

/**
 * 保存正向端口配置
 */
function savePortopposite() {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	// 判断是否值量表store相等
	if (oppositeDownRightStore.getCount() > 0
			&& oppositeDownLeftStore.getCount() != oppositeDownRightStore
					.getCount()) {
		Ext.Msg.alert('提示', "主备用行数不对等，请添加空行补全！");
		return;
	}

	// 判断光缆是否在最下面
	var gl = ""; // 记录主用光缆的位置
	var gr = ""; // 记录备用光缆的位置
	var jsonString = new Array();
	for (var i = 0; i < oppositeDownLeftStore.getCount(); i++) {
		// 判断光缆是否在最下面
		if (oppositeDownLeftStore.getAt(i).get('ROUTE_TYPE') == 5) {
			gl = i;
			if (i != oppositeDownLeftStore.getCount() - 1) {
				Ext.Msg.alert('提示', "主用端口侧，光缆最多只有一条且放在最末尾！");
				return;
			}
		}
// // 端口和虚拟端口的光放和性能类型不能为空
// if ((oppositeDownLeftStore.getAt(i).get('ROUTE_TYPE') ==
// 1&&oppositeDownLeftStore.getAt(i).get('EQUIP_TYPE').indexOf("LAC") ==-1)
// || oppositeDownLeftStore.getAt(i).get('ROUTE_TYPE') == 2) {
// // 查看
// if (oppositeDownLeftStore.getAt(i).get('PM_STD_OPT_AMP_ID') == "") {
// Ext.Msg.alert('提示', "主用端口"
// + oppositeDownLeftStore.getAt(i)
// .get('PTP_NAME') + "光放型号不能为空！");
// return;
// }
// if (oppositeDownLeftStore.getAt(i).get('PM_TYPE') == "") {
// Ext.Msg.alert('提示', "主用端口"
// + oppositeDownLeftStore.getAt(i)
// .get('PTP_NAME') + "性能类型不能为空！");
// return;
// }
// }
		if (oppositeDownRightStore.getCount() > 0
				&& oppositeDownRightStore.getAt(i).get('SUB_ROUTE_TYPE') == 5) {
			gr = i;
			if (i != oppositeDownLeftStore.getCount() - 1) {
				Ext.Msg.alert('提示', "备用端口侧，光缆最多只有一条且放在最末尾！");
				return;
			}
		}
		// 端口和虚拟端口的光放和性能类型不能为空
// if (oppositeDownRightStore.getCount() > 0) {
// if ((oppositeDownRightStore.getAt(i).get('SUB_ROUTE_TYPE') ==
// 1&&oppositeDownRightStore.getAt(i).get('SUB_EQUIP_TYPE').indexOf("LAC") ==-1)
// || oppositeDownRightStore.getAt(i).get('SUB_ROUTE_TYPE') == 2) {
// // 查看
// if (oppositeDownRightStore.getAt(i)
// .get('SUB_PM_STD_OPT_AMP_ID') == "") {
// Ext.Msg.alert('提示', "主用端口"
// + oppositeDownRightStore.getAt(i)
// .get('SUB_PTP_NAME') + "光放型号不能为空！");
// return;
// }
// if (oppositeDownRightStore.getAt(i).get('SUB_PM_TYPE') == "") {
// Ext.Msg.alert('提示', "主用端口"
// + oppositeDownRightStore.getAt(i)
// .get('SUB_PTP_NAME') + "性能类型不能为空！");
// return;
// }
// }
// }

		var map = {
			"PM_MULTI_SEC_PTP_ID" : oppositeDownLeftStore.getAt(i)
					.get('PM_MULTI_SEC_PTP_ID'),
			"MULTI_SEC_ID" : oppositeDownLeftStore.getAt(i).get('MULTI_SEC_ID'),
			"MULTI_SECT_NE_ROUTE_ID" : oppositeDownLeftStore.getAt(i)
					.get('MULTI_SECT_NE_ROUTE_ID'),
			"PTP_ID" : oppositeDownLeftStore.getAt(i).get('PTP_ID'),
			"EQUIP_NAME" : oppositeDownLeftStore.getAt(i).get('EQUIP_NAME'),
			"PTP_NAME" : oppositeDownLeftStore.getAt(i).get('PTP_NAME'),
			"PM_STD_OPT_AMP_ID" : oppositeDownLeftStore.getAt(i)
					.get('PM_STD_OPT_AMP_ID'),
			"PM_TYPE" : oppositeDownLeftStore.getAt(i).get('PM_TYPE'),
			"NOTE" : oppositeDownLeftStore.getAt(i).get('NOTE'),
			"CALCULATE_POINT" : oppositeDownLeftStore.getAt(i)
					.get('CALCULATE_POINT'),
			"CUT_PM_VALUE" : oppositeDownLeftStore.getAt(i).get('CUT_PM_VALUE'),
			"ROUTE_TYPE" : oppositeDownLeftStore.getAt(i).get('ROUTE_TYPE'),
			"SUB_PTP_ID" : "-1",
			"SUB_PTP_NAME" : "",
			"SUB_PM_TYPE" : "",
			"SUB_NOTE" : "",
			"SUB_CALCULATE_POINT" : "",
			"SUB_CUT_PM_VALUE" : "",
			"SUB_ROUTE_TYPE" : "",
			"SUB_PM_STD_OPT_AMP_ID" : "",
			"DIRECTION" : 2
		};
		if (oppositeDownRightStore.getCount() > 0) {
			map = {
				"PM_MULTI_SEC_PTP_ID" : oppositeDownLeftStore.getAt(i)
						.get('PM_MULTI_SEC_PTP_ID'),
				"MULTI_SEC_ID" : oppositeDownLeftStore.getAt(i)
						.get('MULTI_SEC_ID'),
				"MULTI_SECT_NE_ROUTE_ID" : oppositeDownLeftStore.getAt(i)
						.get('MULTI_SECT_NE_ROUTE_ID'),
				"PTP_ID" : oppositeDownLeftStore.getAt(i).get('PTP_ID'),
				"EQUIP_NAME" : oppositeDownLeftStore.getAt(i).get('EQUIP_NAME'),
				"PTP_NAME" : oppositeDownLeftStore.getAt(i).get('PTP_NAME'),
				"PM_STD_OPT_AMP_ID" : oppositeDownLeftStore.getAt(i)
						.get('PM_STD_OPT_AMP_ID'),
				"PM_TYPE" : oppositeDownLeftStore.getAt(i).get('PM_TYPE'),
				"NOTE" : oppositeDownLeftStore.getAt(i).get('NOTE'),
				"CALCULATE_POINT" : oppositeDownLeftStore.getAt(i)
						.get('CALCULATE_POINT'),
				"CUT_PM_VALUE" : oppositeDownLeftStore.getAt(i)
						.get('CUT_PM_VALUE'),
				"ROUTE_TYPE" : oppositeDownLeftStore.getAt(i).get('ROUTE_TYPE'),
				"SUB_PTP_ID" : oppositeDownRightStore.getAt(i)
						.get('SUB_PTP_ID'),
				"SUB_PTP_NAME" : oppositeDownRightStore.getAt(i)
						.get('SUB_PTP_NAME'),
				"SUB_PM_TYPE" : oppositeDownRightStore.getAt(i)
						.get('SUB_PM_TYPE'),
				"SUB_NOTE" : oppositeDownRightStore.getAt(i).get('SUB_NOTE'),
				"SUB_CALCULATE_POINT" : oppositeDownRightStore.getAt(i)
						.get('SUB_CALCULATE_POINT'),
				"SUB_CUT_PM_VALUE" : oppositeDownRightStore.getAt(i)
						.get('SUB_CUT_PM_VALUE'),
				"SUB_ROUTE_TYPE" : oppositeDownRightStore.getAt(i)
						.get('SUB_ROUTE_TYPE'),
				"SUB_PM_STD_OPT_AMP_ID" : oppositeDownRightStore.getAt(i)
						.get('SUB_PM_STD_OPT_AMP_ID'),
				"DIRECTION" : 2
			}
		}
		jsonString.push(map);
	}

	if (oppositeDownRightStore.getCount() > 0 && gl != gr) {
		Ext.Msg.alert('提示', "请确认主备用侧都有光缆，且在同一位置！");
		return;
	}
	var jsonData = {
		"neId" : neRouteIdopposite,
		"direction" : 2,
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
		url : 'multiple-section!savePtpForward.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {// 回调函数
			/**
			 * 标记为需要保存
			 * 
			 * @type Boolean
			 */
			isSaveopposite = true;
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert("提示", obj.returnMessage, function(r) {
						isPortopposite = true;
							// 刷新列表
							var oppositeDownLeftPageTool = Ext
									.getCmp('oppositeDownLeftPageTool');
							if (oppositeDownLeftPageTool) {
								oppositeDownLeftPageTool
										.doLoad(oppositeDownLeftPageTool.cursor);
							}

							var oppositeDownRightPageTool = Ext
									.getCmp('oppositeDownRightPageTool');
							if (oppositeDownRightPageTool) {
								oppositeDownRightPageTool
										.doLoad(oppositeDownRightPageTool.cursor);
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
// 主用端口信息
var jsonString = new Array();
var map = {
	"limit" : 200
};
jsonString.push(map);

var oppositeDownLeftStore = new Ext.data.Store({
			url : 'multiple-section!selectPtpRouteList.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_MULTI_SEC_PTP_ID", "MULTI_SEC_ID",
							"MULTI_SECT_NE_ROUTE_ID", "PTP_ID", "EQUIP_NAME",
							"PTP_NAME", "PM_STD_OPT_AMP_ID", "PM_TYPE", "NOTE",
							"CALCULATE_POINT", "CUT_PM_VALUE", "ROUTE_TYPE","EQUIP_TYPE"])
		});
// oppositeDownLeftStore.load();
// ************************* 任务信息列模型 ****************************
var oppositeDownLeftCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel(
		{
			singleSelect : true,
			header : ""
		});
var oppositeDownLeftCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [ oppositeDownLeftCheckboxSelectionModel, {
				id : 'PM_MULTI_SEC_PTP_ID',
				header : 'PM_MULTI_SEC_PTP_ID',
				dataIndex : 'PM_MULTI_SEC_PTP_ID',
				hidden : true
			}, {
				id : 'MULTI_SEC_ID',
				header : 'MULTI_SEC_ID',
				dataIndex : 'MULTI_SEC_ID',
				hidden : true
			}, {
				id : 'MULTI_SECT_NE_ROUTE_ID',
				header : 'MULTI_SECT_NE_ROUTE_ID',
				dataIndex : 'MULTI_SECT_NE_ROUTE_ID',
				hidden : true
			}, {
				id : 'ROUTE_TYPE',
				header : 'ROUTE_TYPE',
				dataIndex : 'ROUTE_TYPE',
				hidden : true
			}, {
				id : 'EQUIP_TYPE',
				header : 'EQUIP_TYPE',
				dataIndex : 'EQUIP_TYPE',
				hidden : true
			}, {
				id : 'PTP_ID',
				header : 'PTP_ID',
				dataIndex : 'PTP_ID',
				hidden : true
			}, {
				id : 'EQUIP_NAME',
				header : '网元/光缆',
				width : 100,
				dataIndex : 'EQUIP_NAME'
			}, {
				id : 'PTP_NAME',
				header : '端口(主)',
				width : 100,
				dataIndex : 'PTP_NAME'
			}, {
				id : 'PM_STD_OPT_AMP_ID',
				header : "<span style='font-weight:bold'>光放型号(主)</span>",
				// width : 80,
				dataIndex : 'PM_STD_OPT_AMP_ID',
				tooltip:'可编辑列',
				renderer : function(value, cellmeta, record) {
					// 通过匹配value取得ds索引
					var index = modelTypeStore.find("PM_STD_OPT_AMP_ID", value);
					// 通过索引取得记录ds中的记录集
					var record = modelTypeStore.getAt(index);
					// 返回记录集中的value字段的值
					var returnvalue = "";
					if (record == null) {
						// 返回默认值，这是与网上其他解决办法不同的。这个方法才是正确的。我研究了很久才发现。
						returnvalue = value;
					} else {
						returnvalue = record.get('MODEL');// 获取record中的数据集中的process_name字段的值
					}
					return returnvalue; // 注意这个地方的value是上面displayField中的value
				},
				editor : new Ext.form.ComboBox({
							id : "modeTypeni",
							name : "modeTypeni",
							mode : "local",
							displayField : "MODEL",
							valueField : 'PM_STD_OPT_AMP_ID',
							store : modelTypeStore,
							triggerAction : 'all',
							resizable: true,
							editable : false,
							allowBlank : true
						})
			}, {
				id : 'PM_TYPE',
				header : "<span style='font-weight:bold'>性能类型(主)</span>",
				dataIndex : 'PM_TYPE',
				tooltip:'可编辑列',
				renderer : function(value, cellmeta, record) {
					if (value == "1") {
						return "输入光功率(dBm)";
					} else if (value == "2") {
						return "输出光功率(dBm)";
					} else if (value == "3") {
						return "衰耗值(dB)";
					} else if (value == "4") {
						return "段衰耗(dB)";
					}else {
						return "";
					}
				},
				editor : new Ext.form.ComboBox({
							id : "pmTypeni",
							name : "pmTypeni",
							mode : 'local',
							store : new Ext.data.ArrayStore({
										fields : ['value', 'displayName'],
										data : [['1', '输入光功率(dBm)'], ['2', '输出光功率(dBm)'],
												['3', '衰耗值(dB)'], ['4', '段衰耗(dB)']]
									}),
							displayField : "displayName",
							valueField : 'value',
							triggerAction : 'all',
							editable : false,
							allowBlank : true
						})
			}, {
				id : 'NOTE',
				header : "<span style='font-weight:bold'>备用信息(主)</span>",
				dataIndex : 'NOTE',
				tooltip:'可编辑列',
				width : 100,
				editor : new Ext.form.TextField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'CALCULATE_POINT',
				header : "<span style='font-weight:bold'>理论值(主)</span>",
				tooltip:'可编辑列',
				width : 80,
				dataIndex : 'CALCULATE_POINT',
				renderer : function(value, cellmeta, record) {
					return Ext.util.Format.number(value, '0.00');
				},
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'CUT_PM_VALUE',
				header : "<span style='font-weight:bold'>基准值(主)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'CUT_PM_VALUE',
				renderer : function(value, cellmeta, record) {
					return Ext.util.Format.number(value, '0.00');
				},
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}]
});

var oppositeDownLeftPageTool = new Ext.PagingToolbar({
			id : 'oppositeDownLeftPageTool',
			pageSize : 200,// 每页显示的记录值
			store : oppositeDownLeftStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var oppositeDownLeftGridPanel = new Ext.grid.EditorGridPanel({
	id : "oppositeDownLeftGridPanel",
	flex : 6,
	// title:'任务信息列表',
	cm : oppositeDownLeftCm,
	store : oppositeDownLeftStore,
	// collapsed: true, // initially collapse the group
	// collapsible: true,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : oppositeDownLeftCheckboxSelectionModel, // 必须加不然不能选checkbox
	bbar : oppositeDownLeftPageTool,
	// tbar: pageTool,
	// viewConfig : {
	// forceFit : true
	// },
	tbar : {
		xtype : "container",
		border : false,
		items : [{
			// tbar第二行工具栏
			xtype : "toolbar",
			items : ['-',{
						text : '新增端口',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							addPortLeftopposite(1, 2);
						}

					}, {
						text : '新增虚拟端口',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							addVirtualPortLeftopposite(1, 2);
						}
					}, {
						text : '修改虚拟端口',
						icon : '../../../resource/images/btnImages/modify.png',
						handler : function() {
							// modifyVirtualPortLeftopposite(1,2);
							modifyVirtualPortLeftForward(1, 2);
						}

					}, {
						text : '新增光缆',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							addLeftoppositeFiber(1, 2);
						}

					},'-', {
						text : '上移',
						icon : '../../../resource/images/btnImages/up.png',
						handler : function() {
							upLeftopposite(oppositeDownLeftGridPanel,
									oppositeDownLeftStore);
						}

					}, {
						text : '下移',
						icon : '../../../resource/images/btnImages/down.png',
						handler : function() {
							downLeftopposite(oppositeDownLeftGridPanel,
									oppositeDownLeftStore);
						}

					}, '-',{
						text : '删除',
						icon : '../../../resource/images/btnImages/delete.png',
						handler : function() {
							deleteLeftopposite();
						}

					},'-', {
						text : '基准值生成',
						icon : '../../../resource/images/btnImages/set_baseline.png',
						handler : function() {
							newStandLeftForward(5);
						}

					}]
		}]

	}
});

/**
 * 新增端口选择树
 * 
 * @param {}
 *            type 1. 左 2.右
 */
function addPortLeftopposite(portType) {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请先选择要设定端口的网元！');
		return;
	}
	var url = "addTree.jsp?level=8&type=4&checkModel=multiple&portType="
			+ portType + "&rootType=4&rootId=" + neIdopposite + "&rootVisible="
			+ true;
	addNeForwardWindow = new Ext.Window({
				id : 'addNeForwardWindow',
				title : '端口选择',
				// width : 210,
				autoWidth : true,
				height : 400,
				// layout : 'fit',
				plain : false,
				modal : true,
				resizable : false,
				// closeAction : 'hide',// 关闭窗口
				bodyStyle : 'padding:1px;',
				buttonAlign : 'center',
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'

			});
	addNeForwardWindow.show();
	// 调节高度
	if (addNeForwardWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addNeForwardWindow.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		addNeForwardWindow.setHeight(addNeForwardWindow.getInnerHeight());
	}
	// 调节宽度
	if (addNeForwardWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addNeForwardWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		addNeForwardWindow.setWidth(addNeForwardWindow.getInnerWidth());
	}
	addNeForwardWindow.center();
}

/**
 * 正向主用端口设置
 * 
 * @param {}
 *            result 树返回的结果集
 */
function setPortLeftopposite(result) {
	var portName = "";
	for(var i = 0 ; i <result.length;i++){
	var portId = result[i]["nodeId"];
	var neName = result[i]['path:text'].split(":");
	var neId = result[i]['path:nodeId'].split(":");
	// 判断网元是否已被选中
	for (var j = 0; j < oppositeDownLeftStore.getCount(); j++) {
		var ptpId = (oppositeDownLeftStore.getAt(j).get('PTP_ID') + "")
				.split(",");
		for (var k = 0; k < ptpId.length; k++) {
			if (portId == ptpId[k]) {
				portName+= result[i]["text"]+",";
			}
		}

	}
	}
	if(portName.length>0){
	portName = portName.substring(0,portName.length-1);
	Ext.Msg.confirm("确认",  portName + "已经添加过!是否继续？", function(button) {
				if (button == 'yes') {
					for(var i = 0 ; i <result.length;i++){
						var portId = result[i]["nodeId"];
						var neName = result[i]['path:text'].split(":");
						var neId = result[i]['path:nodeId'].split(":");
				if (neId[neId.length - 4] != neIdopposite&&neId[0] != neIdopposite) {
					Ext.Msg.alert('提示', "请选择所属网元下的端口！");
					return;
				}
				// 判断选择是否跨网元
				var jsonString = new Array();
				var map = {
					"BASE_PTP_ID" : portId,
					"EQUIP_NAME" : neName[neName.length - 4],
					"EQUIP_TYPE": neName[neName.length - 2]
				};
				jsonString.push(map);
			
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				Ext.Ajax.request({
							url : 'multiple-section!selecrPtpName.action',
							method : 'POST',
							params : jsonData,
							success : function(response,param) {
								var obj = Ext.decode(response.responseText);
								var pa = Ext.decode(param.params.jsonString);
								isPortopposite = false;
								// 查询成功以后，record赋值
								var select = oppositeDownLeftGridPanel.getSelectionModel()
										.getSelected();
								var index = oppositeDownLeftStore.indexOf(select);
			
								var ptpRouteData = {
									PTP_ID : pa[0].BASE_PTP_ID,
									EQUIP_NAME : pa[0].EQUIP_NAME,
									EQUIP_TYPE: pa[0].EQUIP_TYPE,
									PTP_NAME : obj.ptpName,
									MULTI_SEC_ID : mul_id,
									ROUTE_TYPE : 1,
									PM_TYPE : "",
									PM_STD_OPT_AMP_ID : "",
									MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
								}
								var record = oppositeDownLeftStore.recordType;
								var p = new record(ptpRouteData);
								if (index == -1) {
									oppositeDownLeftStore.insert(oppositeDownLeftStore
													.getCount(), p);
								} else {
									oppositeDownLeftStore.insert(index, p);
								}
			
								oppositeDownLeftGridPanel.getView().refresh();
								oppositeDownLeftGridPanel.getSelectionModel()
										.selectLastRow();
							},
							error : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							},
							failure : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							}
			
						});
			}
				}
				});
		
	}else{
		for(var i = 0 ; i <result.length;i++){
			var portId = result[i]["nodeId"];
			var neName = result[i]['path:text'].split(":");
			var neId = result[i]['path:nodeId'].split(":");
			if (neId[neId.length - 4] != neIdopposite&&neId[0] != neIdopposite) {
			Ext.Msg.alert('提示', "请选择所属网元下的端口！");
			return;
		}
		// 判断选择是否跨网元
		var jsonString = new Array();
		var map = {
			"BASE_PTP_ID" : portId,
			"EQUIP_NAME" : neName[neName.length - 4],
			"EQUIP_TYPE": neName[neName.length - 2]
		};
		jsonString.push(map);
	
		var jsonData = {
			"jsonString" : Ext.encode(jsonString)
		};
		Ext.Ajax.request({
					url : 'multiple-section!selecrPtpName.action',
					method : 'POST',
					params : jsonData,
					success : function(response,param) {
						var obj = Ext.decode(response.responseText);
						var pa = Ext.decode(param.params.jsonString);
							isPortopposite = false;
						// 查询成功以后，record赋值
						var select = oppositeDownLeftGridPanel.getSelectionModel()
								.getSelected();
						var index = oppositeDownLeftStore.indexOf(select);
						var ptpRouteData = {
							PTP_ID : pa[0].BASE_PTP_ID,
							EQUIP_NAME : pa[0].EQUIP_NAME,
							EQUIP_TYPE: pa[0].EQUIP_TYPE,
							PTP_NAME : obj.ptpName,
							MULTI_SEC_ID : mul_id,
							ROUTE_TYPE : 1,
							PM_TYPE : "",
							PM_STD_OPT_AMP_ID : "",
							MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
						}
						var record = oppositeDownLeftStore.recordType;
						var p = new record(ptpRouteData);
						if (index == -1) {
							oppositeDownLeftStore.insert(oppositeDownLeftStore
											.getCount(), p);
						} else {
							oppositeDownLeftStore.insert(index, p);
						}
	
						oppositeDownLeftGridPanel.getView().refresh();
						oppositeDownLeftGridPanel.getSelectionModel()
								.selectLastRow();
					},
					error : function(response) {
						Ext.Msg.alert('错误', '保存失败！');
					},
					failure : function(response) {
						Ext.Msg.alert('错误', '保存失败！');
					}
	
				});
	}
	}
	Ext.getCmp('addNeForwardWindow').close();

}
/**
 * 正向备用端口设置
 * 
 * @param {}
 *            result 树返回的结果集
 */
function setPortRightopposite(result) {
	var portName = "";
	for(var i = 0 ; i <result.length;i++){
	var portId = result[i]["nodeId"];
	var neName = result[i]['path:text'].split(":");
	var neId = result[i]['path:nodeId'].split(":");
	
	// 判断网元是否已被选中
	for (var j = 0; j < oppositeDownRightStore.getCount(); j++) {
		var ptpId = (oppositeDownRightStore.getAt(j).get('SUB_PTP_ID') + "")
				.split(",");
		for (var k = 0; k < ptpId.length; k++) {
			if (portId == ptpId[k]) {
				portName+= result[i]["text"]+",";
			}
		}

	}
	}
	if(portName.length>0){
	portName = portName.substring(0,portName.length-1);
	Ext.Msg.confirm("确认",  portName + "已经添加过!是否继续？", function(button) {
				if (button == 'yes') {
					for(var i = 0 ; i <result.length;i++){
						var portId = result[i]["nodeId"];
						var neName = result[i]['path:text'].split(":");
						var neId = result[i]['path:nodeId'].split(":");
				if (neId[neId.length - 4] != neIdopposite&&neId[0] != neIdopposite) {
					Ext.Msg.alert('提示', "请选择所属网元下的端口！");
					return;
				}
				// 判断选择是否跨网元
				var jsonString = new Array();
				var map = {
					"BASE_PTP_ID" : portId,
					"EQUIP_NAME" : neName[neName.length - 4],
					"EQUIP_TYPE": neName[neName.length - 2]
				};
				jsonString.push(map);
			
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				Ext.Ajax.request({
							url : 'multiple-section!selecrPtpName.action',
							method : 'POST',
							params : jsonData,
							success : function(response,param) {
								var obj = Ext.decode(response.responseText);
								var pa = Ext.decode(param.params.jsonString);
									isPortopposite = false;
								// 查询成功以后，record赋值
								var select = oppositeDownRightGridPanel.getSelectionModel()
										.getSelected();
								var index = oppositeDownRightStore.indexOf(select);
			
								var ptpRouteData = {
									SUB_PTP_ID : pa[0].BASE_PTP_ID,
									SUB_EQUIP_NAME : pa[0].EQUIP_NAME,
									SUB_EQUIP_TYPE: pa[0].EQUIP_TYPE,
									SUB_PTP_NAME : obj.ptpName,
									MULTI_SEC_ID : mul_id,
									SUB_ROUTE_TYPE : 1,
									SUB_PM_TYPE : "",
									SUB_PM_STD_OPT_AMP_ID : "",
									MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
								}
								var record = oppositeDownRightStore.recordType;
								var p = new record(ptpRouteData);
								if (index == -1) {
									oppositeDownRightStore.insert(oppositeDownRightStore
													.getCount(), p);
								} else {
									oppositeDownRightStore.insert(index, p);
								}
			
								oppositeDownRightGridPanel.getView().refresh();
								oppositeDownRightGridPanel.getSelectionModel()
										.selectLastRow();
							},
							error : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							},
							failure : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							}
			
						});
		}
				}
				});
	}else{
		for(var i = 0 ; i <result.length;i++){
			var portId = result[i]["nodeId"];
			var neName = result[i]['path:text'].split(":");
			var neId = result[i]['path:nodeId'].split(":");
				if (neId[neId.length - 4] != neIdopposite&&neId[0] != neIdopposite) {
					Ext.Msg.alert('提示', "请选择所属网元下的端口！");
					return;
				}
				// 判断选择是否跨网元
				var jsonString = new Array();
				var map = {
					"BASE_PTP_ID" : portId,
					"EQUIP_NAME" : neName[neName.length - 4],
					"EQUIP_TYPE": neName[neName.length - 2]
				};
				jsonString.push(map);
			
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				Ext.Ajax.request({
							url : 'multiple-section!selecrPtpName.action',
							method : 'POST',
							params : jsonData,
							success : function(response,param) {
								var obj = Ext.decode(response.responseText);
								var pa = Ext.decode(param.params.jsonString);
									isPortopposite = false;
								// 查询成功以后，record赋值
								var select = oppositeDownRightGridPanel.getSelectionModel()
										.getSelected();
								var index = oppositeDownRightStore.indexOf(select);
			
								var ptpRouteData = {
									SUB_PTP_ID : pa[0].BASE_PTP_ID,
									SUB_EQUIP_NAME : pa[0].EQUIP_NAME,
									SUB_EQUIP_TYPE : pa[0].EQUIP_TYPE,
									SUB_PTP_NAME : obj.ptpName,
									MULTI_SEC_ID : mul_id,
									SUB_ROUTE_TYPE : 1,
									SUB_PM_TYPE : "",
									SUB_PM_STD_OPT_AMP_ID : "",
									MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
								}
								var record = oppositeDownRightStore.recordType;
								var p = new record(ptpRouteData);
								if (index == -1) {
									oppositeDownRightStore.insert(oppositeDownRightStore
													.getCount(), p);
								} else {
									oppositeDownRightStore.insert(index, p);
								}
			
								oppositeDownRightGridPanel.getView().refresh();
								oppositeDownRightGridPanel.getSelectionModel()
										.selectLastRow();
							},
							error : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							},
							failure : function(response) {
								Ext.Msg.alert('错误', '保存失败！');
							}
			
						});
	}
	}
	Ext.getCmp('addNeForwardWindow').close();

}
/**
 * 新增光缆信息
 */
function addLeftoppositeFiber(portType, type) {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请先选择要设定端口的网元！');
		return;
	}
	var url = "addFiber.jsp?portType=" + portType + "&type=" + type;
	addLeftForwardFiberWindow = new Ext.Window({
		id : 'addLeftForwardFiberWindow',
		title : '新增光缆',
		width : 400,
		// autoWidth : true,
		height : 200,
		// layout : 'fit',
		plain : false,
		modal : true,
		resizable : false,
		// closeAction : 'hide',// 关闭窗口
		bodyStyle : 'padding:1px;',
		buttonAlign : 'center',
		html : '<iframe id="addLeftForwardFiber" name = "addLeftForwardFiber" src='
				+ url + ' height="100%" width="100%" frameborder=0 border=0/>'

	});
	addLeftForwardFiberWindow.show();
	// 调节高度
	if (addLeftForwardFiberWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addLeftForwardFiberWindow
				.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		addLeftForwardFiberWindow.setHeight(addLeftForwardFiberWindow
				.getInnerHeight());
	}
	// 调节宽度
	if (addLeftForwardFiberWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addLeftForwardFiberWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		addLeftForwardFiberWindow.setWidth(addLeftForwardFiberWindow
				.getInnerWidth());
	}
	addLeftForwardFiberWindow.center();
}

/**
 * 新增虚拟端口
 * 
 * @param {}
 *            virName 虚拟端口显示名
 * @param {}
 *            caculateDown 理论衰耗值
 * @param {}
 *            rows 行数
 * @param {}
 *            direction 方向： 输入 或输出
 * @param {}
 *            ids 端口id集合
 * @param {}
 *            type 类型 ['1', '自定义'], ['2', '衰耗值'], ['3', '空白行'], ['4', '合并光功率']
 *            portType 1. 主 2.备
 */
function saveVirtualPortLeftopposite(virName, caculateDown, rows, direction,
		ids, type, portType) {
	// ROUTE_TYPE :1.ptp口，2.虚拟端口，3.衰耗器，4.段衰耗，5.光缆，6.其他(空行),7.自定义
	// var virPort;
	// if (portType == 2) {
	// if (type == 1) {
	// virPort = {
	// SUB_PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// SUB_PTP_NAME : virName,
	// SUB_CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 7,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 2) {
	// virPort = {
	// SUB_PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// SUB_PTP_NAME : virName,
	// SUB_CALCULATE_POINT : caculateDown,
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 3,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 3) {
	// virPort = {
	// SUB_PTP_ID : "",
	// EQUIP_NAME : "",
	// SUB_PTP_NAME : "",
	// SUB_CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 6,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 4) {
	// virPort = {
	// SUB_PTP_ID : ids,
	// EQUIP_NAME : neNameopposite,
	// SUB_PTP_NAME : virName,
	// SUB_CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 2,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// }
	// if (type != 3) {
	// var record = oppositeDownRightStore.recordType;
	// var p = new record(virPort);
	//
	// oppositeDownRightStore.insert(oppositeDownRightStore.getCount(), p);
	//
	// oppositeDownRightGridPanel.getView().refresh();
	// oppositeDownRightGridPanel.getSelectionModel().selectLastRow();
	// } else {
	// for (var i = 0; i < rows; i++) {
	// var record = oppositeDownRightStore.recordType;
	// var p = new record(virPort);
	//
	// oppositeDownRightStore.insert(oppositeDownRightStore.getCount(),
	// p);
	//
	// oppositeDownRightGridPanel.getView().refresh();
	// oppositeDownRightGridPanel.getSelectionModel().selectLastRow();
	// }
	// }
	// } else {
	//
	// if (type == 1) {
	// virPort = {
	// PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// PTP_NAME : virName,
	// CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 7,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 2) {
	// virPort = {
	// PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// PTP_NAME : virName,
	// CALCULATE_POINT : caculateDown,
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 3,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 3) {
	// virPort = {
	// PTP_ID : "",
	// EQUIP_NAME : "",
	// PTP_NAME : "",
	// CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 6,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 4) {
	// virPort = {
	// PTP_ID : ids,
	// EQUIP_NAME : neNameopposite,
	// PTP_NAME : virName,
	// CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 2,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// }
	// if (type != 3) {
	// var record = oppositeDownLeftStore.recordType;
	// var p = new record(virPort);
	//
	// oppositeDownLeftStore.insert(oppositeDownLeftStore.getCount(), p);
	//
	// oppositeDownLeftGridPanel.getView().refresh();
	// oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
	// } else {
	// for (var i = 0; i < rows; i++) {
	// var record = oppositeDownLeftStore.recordType;
	// var p = new record(virPort);
	//
	// oppositeDownLeftStore.insert(oppositeDownLeftStore.getCount(), p);
	//
	// oppositeDownLeftGridPanel.getView().refresh();
	// oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
	// }
	// }
	//
	// }
}
/**
 * 保存修改的虚拟端口
 * 
 * @param {}
 *            virName 虚拟端口显示名
 * @param {}
 *            caculateDown 理论衰耗值
 * @param {}
 *            rows 行数
 * @param {}
 *            direction 方向： 输入 或输出
 * @param {}
 *            ids 端口id集合
 * @param {}
 *            type 类型 ['1', '自定义'], ['2', '衰耗值'], ['3', '空白行'], ['4', '合并光功率']
 */
function saveModifyVirtualPortLeftopposite(virName, caculateDown, rows,
		direction, ids, type, portType) {
	// ROUTE_TYPE :1.ptp口，2.虚拟端口，3.衰耗器，4.段衰耗，5.光缆，6.其他(空行),7.自定义
	// var virPort;
	// if (portType == 1) {
	// if (type == 1) {
	// virPort = {
	// PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// PTP_NAME : virName,
	// CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 7,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 2) {
	// virPort = {
	// PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// PTP_NAME : virName,
	// CALCULATE_POINT : caculateDown,
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 3,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 3) {
	// virPort = {
	// PTP_ID : "",
	// EQUIP_NAME : "",
	// PTP_NAME : "",
	// CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 6,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 4) {
	// virPort = {
	// PTP_ID : ids,
	// EQUIP_NAME : neNameopposite,
	// PTP_NAME : virName,
	// CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// ROUTE_TYPE : 2,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// }
	// // var select = oppositeDownLeftGridPanel.getSelectionModel()
	// // .getSelected();
	// // var index = oppositeDownLeftStore.indexOf(select);
	// // var record = oppositeDownLeftStore.recordType;
	// // var p = new record(virPort);
	// //
	// // oppositeDownLeftStore.insert(oppositeDownLeftStore.getCount(), p);
	// //
	// // oppositeDownLeftGridPanel.getView().refresh();
	// // oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
	// var select = oppositeDownLeftGridPanel.getSelectionModel().getSelected();
	// var index = oppositeDownLeftStore.indexOf(select);
	//
	// oppositeDownLeftStore.remove(select);
	// var record = oppositeDownLeftStore.recordType;
	// var p = new record(virPort);
	// if (index == -1) {
	// oppositeDownLeftStore.insert(oppositeDownLeftStore.getCount(), p);
	// } else {
	// oppositeDownLeftStore.insert(index, p);
	// }
	//
	// oppositeDownLeftGridPanel.getView().refresh();
	// oppositeDownLeftGridPanel.getSelectionModel().selectRow(index);
	// // oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
	// } else {
	//
	// if (type == 1) {
	// virPort = {
	// SUB_PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// SUB_PTP_NAME : virName,
	// SUB_CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 7,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 2) {
	// virPort = {
	// SUB_PTP_ID : "",
	// EQUIP_NAME : neNameopposite,
	// SUB_PTP_NAME : virName,
	// SUB_CALCULATE_POINT : caculateDown,
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 3,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 3) {
	// virPort = {
	// SUB_PTP_ID : "",
	// EQUIP_NAME : "",
	// SUB_PTP_NAME : "",
	// SUB_CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 6,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// } else if (type == 4) {
	// virPort = {
	// SUB_PTP_ID : ids,
	// EQUIP_NAME : neNameopposite,
	// SUB_PTP_NAME : virName,
	// SUB_CALCULATE_POINT : "",
	// MULTI_SEC_ID : mul_id,
	// SUB_ROUTE_TYPE : 2,
	// MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
	// }
	// }
	// // var select = oppositeDownLeftGridPanel.getSelectionModel()
	// // .getSelected();
	// // var index = oppositeDownLeftStore.indexOf(select);
	// // var record = oppositeDownLeftStore.recordType;
	// // var p = new record(virPort);
	// //
	// // oppositeDownLeftStore.insert(oppositeDownLeftStore.getCount(), p);
	// //
	// // oppositeDownLeftGridPanel.getView().refresh();
	// // oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
	// var select = oppositeDownRightGridPanel.getSelectionModel()
	// .getSelected();
	// var index = oppositeDownRightStore.indexOf(select);
	//
	// oppositeDownRightStore.remove(select);
	// var record = oppositeDownRightStore.recordType;
	// var p = new record(virPort);
	// if (index == -1) {
	// oppositeDownRightStore.insert(oppositeDownRightStore.getCount(), p);
	// } else {
	// oppositeDownRightStore.insert(index, p);
	// }
	//
	// oppositeDownRightGridPanel.getView().refresh();
	// oppositeDownRightGridPanel.getSelectionModel().selectRow(index);
	// // oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
	//
	// }
}

/**
 * 向上移动左边框的记录
 * 
 * @param {}
 *            oppositeDownLeftGridPanel
 * @param {}
 *            oppositeDownLeftStore
 */
function upLeftopposite(oppositeDownLeftGridPanel, oppositeDownLeftStore) {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeDownLeftGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
	isPortopposite = false;
	var index = oppositeDownLeftStore.indexOf(record);
	if (index == 0) {
		return;
	}
	oppositeDownLeftStore.remove(record);
	oppositeDownLeftStore.insert(index - 1, record);
	oppositeDownLeftGridPanel.getView().refresh();
	oppositeDownLeftGridPanel.getSelectionModel().selectRow(index - 1);
}

/**
 * 向下移动左边框的记录
 * 
 * @param {}
 *            oppositeDownLeftGridPanel
 * @param {}
 *            oppositeDownLeftStore
 */
function downLeftopposite(oppositeDownLeftGridPanel, oppositeDownLeftStore) {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeDownLeftGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
		isPortopposite = false;
	var index = oppositeDownLeftStore.indexOf(record);
	if (index == oppositeDownLeftStore.getCount() - 1) {
		return;
	}
	oppositeDownLeftStore.remove(record);
	oppositeDownLeftStore.insert(index + 1, record);
	oppositeDownLeftGridPanel.getView().refresh();
	oppositeDownLeftGridPanel.getSelectionModel().selectRow(index + 1);
}

/**
 * 删除左边框的记录
 */
function deleteLeftopposite() {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeDownLeftGridPanel.getSelectionModel().getSelections();
	if (record.length > 0) {
			isPortopposite = false;
		for (var i = 0; i < record.length; i++) {
			oppositeDownLeftStore.remove(record[i]);
		}

	} else {
		Ext.Msg.alert('信息', '请选择要删除的记录！');
		return;
	}

}

/**
 * 新增虚拟端口 1. 主 2 备
 */
function addVirtualPortLeftopposite(portType, type) {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请先选择要设定端口的网元！');
		return;
	}
	
	var neId = "";
	if (type == 1) {
		neId = neIdForward;
	} else if (type == 2) {
		neId = neIdopposite;
	}
	var url = "addVirPort.jsp?portType=" + portType + "&type=" + type
			+ "&neId=" + neId;
	addVirtualPortLeftForwardWindow = new Ext.Window({
		id : 'addVirtualPortLeftForwardWindow',
		name : 'addVirtualPortLeftForwardWindow',
		title : '新增虚拟端口',
		width : 450,
		// autoWidth : true,
		height : 300,
		// layout : 'fit',
		plain : false,
		modal : true,
		resizable : false,
		// closeAction : 'hide',// 关闭窗口
		bodyStyle : 'padding:1px;',
		buttonAlign : 'center',
		html : '<iframe id = "addVirtualPortLeftForwardWindow" name = "addVirtualPortLeftForwardWindow" src='
				+ url + ' height="100%" width="100%" frameborder=0 border=0/>'

	});
	addVirtualPortLeftForwardWindow.show();
	// 调节高度
	if (addVirtualPortLeftForwardWindow.getHeight() > Ext.getCmp('win')
			.getHeight()) {
		addVirtualPortLeftForwardWindow.setHeight(Ext.getCmp('win').getHeight()
				* 0.7);
	} else {
		addVirtualPortLeftForwardWindow
				.setHeight(addVirtualPortLeftForwardWindow.getInnerHeight());
	}
	// 调节宽度
	if (addVirtualPortLeftForwardWindow.getWidth() > Ext.getCmp('win')
			.getWidth()) {
		addVirtualPortLeftForwardWindow.setWidth(Ext.getCmp('win').getWidth()
				* 0.7);
	} else {
		addVirtualPortLeftForwardWindow
				.setWidth(addVirtualPortLeftForwardWindow.getInnerWidth());
	}
	addVirtualPortLeftForwardWindow.center();
}

function modifyVirtualPortLeftopposite(portType, type) {
	// // 不能多选，也不能为空
	// var select = "";
	// if (portType == 1) {
	// select = oppositeDownLeftGridPanel.getSelectionModel().getSelections();
	// } else {
	// select = oppositeDownRightGridPanel.getSelectionModel().getSelections();
	// }
	// if (select.length < 1) {
	// Ext.Msg.alert('提示', '请选择需要修改的项！');
	// return;
	// } else {
	// var routeType = "";
	// var virName = "";
	// var caculateDown = "";
	// var ids = "";
	// if (portType == 1) {
	// routeType = select[0].get("ROUTE_TYPE");
	// virName = select[0].get("PTP_NAME");
	// caculateDown = select[0].get("CALCULATE_POINT");
	// ids = select[0].get("PTP_ID");
	// } else {
	// routeType = select[0].get("SUB_ROUTE_TYPE");
	// virName = select[0].get("SUB_PTP_NAME");
	// caculateDown = select[0].get("SUB_CALCULATE_POINT");
	// ids = select[0].get("SUB_PTP_ID");
	// }
	// if (routeType == 2 || routeType == 3 || routeType == 7) {
	// var type;
	// if (routeType == 2) {
	// type = 4;
	// } else if (routeType == 3) {
	// type = 2;
	// } else if (routeType == 7) {
	// type = 1;
	// }
	// var url = "modifyVirPort.jsp?type=" + type + "&virName=" + virName
	// + "&caculateDown=" + caculateDown + "&ids=" + ids
	// + "&portType=" + portType;
	// modifyVirtualPortLeftoppositeWindow = new Ext.Window({
	// id : 'modifyVirtualPortLeftoppositeWindow',
	// name : 'modifyVirtualPortLeftoppositeWindow',
	// title : '修改虚拟端口',
	// width : 450,
	// // autoWidth : true,
	// height : 300,
	// // layout : 'fit',
	// plain : false,
	// modal : true,
	// resizable : false,
	// // closeAction : 'hide',// 关闭窗口
	// bodyStyle : 'padding:1px;',
	// buttonAlign : 'center',
	// html : '<iframe id = "modifyVirtualPortLeftoppositeWindow" name =
	// "modifyVirtualPortLeftoppositeWindow" src='
	// + url
	// + ' height="100%" width="100%" frameborder=0 border=0/>'
	//
	// });
	// modifyVirtualPortLeftoppositeWindow.show();
	// // 调节高度
	// if (modifyVirtualPortLeftoppositeWindow.getHeight() > Ext
	// .getCmp('win').getHeight()) {
	// modifyVirtualPortLeftoppositeWindow.setHeight(Ext.getCmp('win')
	// .getHeight()
	// * 0.7);
	// } else {
	// modifyVirtualPortLeftoppositeWindow
	// .setHeight(modifyVirtualPortLeftoppositeWindow
	// .getInnerHeight());
	// }
	// // 调节宽度
	// if (modifyVirtualPortLeftoppositeWindow.getWidth() > Ext
	// .getCmp('win').getWidth()) {
	// modifyVirtualPortLeftoppositeWindow.setWidth(Ext.getCmp('win')
	// .getWidth()
	// * 0.7);
	// } else {
	// modifyVirtualPortLeftoppositeWindow
	// .setWidth(modifyVirtualPortLeftoppositeWindow
	// .getInnerWidth());
	// }
	// modifyVirtualPortLeftoppositeWindow.center();
	//
	// } else {
	// Ext.Msg.alert('提示', '该行不能修改！');
	// return;
	// }
	// }

}
// 备用端口信息
var jsonString = new Array();
var map = {
	"limit" : 200
};
jsonString.push(map);

var oppositeDownRightStore = new Ext.data.Store({
			url : 'multiple-section!selectSubPtpRouteList.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["PM_MULTI_SEC_PTP_ID", "MULTI_SEC_ID",
							"MULTI_SECT_NE_ROUTE_ID", "SUB_PTP_ID",
							"EQUIP_NAME", "SUB_PTP_NAME",
							"SUB_PM_STD_OPT_AMP_ID", "SUB_PM_TYPE", "SUB_NOTE",
							"SUB_CALCULATE_POINT", "SUB_CUT_PM_VALUE",
							"SUB_ROUTE_TYPE","SUB_EQUIP_TYPE"])
		});
// oppositeDownRightStore.load();
// ************************* 任务信息列模型 ****************************
var oppositeDownRightCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel(
		{
			singleSelect : true,
			header : ""
		});
var oppositeDownRightCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [ oppositeDownRightCheckboxSelectionModel, {
				id : 'PM_MULTI_SEC_PTP_ID',
				header : 'PM_MULTI_SEC_PTP_ID',
				dataIndex : 'PM_MULTI_SEC_PTP_ID',
				hidden : true
			}, {
				id : 'MULTI_SEC_ID',
				header : 'MULTI_SEC_ID',
				dataIndex : 'MULTI_SEC_ID',
				hidden : true
			}, {
				id : 'MULTI_SECT_NE_ROUTE_ID',
				header : 'MULTI_SECT_NE_ROUTE_ID',
				dataIndex : 'MULTI_SECT_NE_ROUTE_ID',
				hidden : true
			}, {
				id : 'SUB_ROUTE_TYPE',
				header : 'SUB_ROUTE_TYPE',
				dataIndex : 'SUB_ROUTE_TYPE',
				hidden : true
			}, {
				id : 'SUB_EQUIP_TYPE',
				header : 'SUB_EQUIP_TYPE',
				dataIndex : 'SUB_EQUIP_TYPE',
				hidden : true
			}, {
				id : 'SUB_PTP_ID',
				header : 'SUB_PTP_ID',
				dataIndex : 'SUB_PTP_ID',
				hidden : true
			}, {
				id : 'SUB_PTP_NAME',
				header : '端口(备)',
				width : 100,
				dataIndex : 'SUB_PTP_NAME'
			}, {
				id : 'SUB_PM_STD_OPT_AMP_ID',
				header : "<span style='font-weight:bold'>光放型号(备)</span>",
				// width : 80,
				dataIndex : 'SUB_PM_STD_OPT_AMP_ID',
				tooltip:'可编辑列',
				renderer : function(value, cellmeta, record) {
					// 通过匹配value取得ds索引
					var index = modelTypeStore.find("PM_STD_OPT_AMP_ID", value);
					// 通过索引取得记录ds中的记录集
					var record = modelTypeStore.getAt(index);
					// 返回记录集中的value字段的值
					var returnvalue = "";
					if (record == null) {
						// 返回默认值，这是与网上其他解决办法不同的。这个方法才是正确的。我研究了很久才发现。
						returnvalue = value;
					} else {
						returnvalue = record.get('MODEL');// 获取record中的数据集中的process_name字段的值
					}
					return returnvalue; // 注意这个地方的value是上面displayField中的value
				},
				editor : new Ext.form.ComboBox({
							id : "modeTypeRightni",
							name : "modeTypeRightni",
							mode : "local",
							displayField : "MODEL",
							valueField : 'PM_STD_OPT_AMP_ID',
							store : modelTypeStore,
							triggerAction : 'all',
							resizable: true,
							editable : false,
							allowBlank : true
						})
			}, {
				id : 'SUB_PM_TYPE',
				header : "<span style='font-weight:bold'>性能类型(备)</span>",
				dataIndex : 'SUB_PM_TYPE',
				tooltip:'可编辑列',
				renderer : function(value, cellmeta, record) {
					if (value == "1") {
						return "输入光功率(dBm)";
					} else if (value == "2") {
						return "输出光功率(dBm)";
					} else if (value == "3") {
						return "衰耗值(dB)";
					} else if (value == "4") {
						return "段衰耗(dB)";
					}else {
						return "";
					}
				},
				editor : new Ext.form.ComboBox({
							id : "pmTypeRightni",
							name : "pmTypeRightni",
							mode : 'local',
							store : new Ext.data.ArrayStore({
										fields : ['value', 'displayName'],
										data : [['1', '输入光功率(dBm)'], ['2', '输出光功率(dBm)'],
												['3', '衰耗值(dB)'], ['4', '段衰耗(dB)']]
									}),
							displayField : "displayName",
							valueField : 'value',
							triggerAction : 'all',
							editable : false,
							allowBlank : true
						})
			}, {
				id : 'SUB_NOTE',
				header : "<span style='font-weight:bold'>备用信息(备)</span>",
				dataIndex : 'SUB_NOTE',
				tooltip:'可编辑列',
				width : 100,
				editor : new Ext.form.TextField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'SUB_CALCULATE_POINT',
				header : "<span style='font-weight:bold'>理论值(备)</span>",
				width : 80,
				dataIndex : 'SUB_CALCULATE_POINT',
				renderer : function(value, cellmeta, record) {
					return Ext.util.Format.number(value, '0.00');
				},
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'SUB_CUT_PM_VALUE',
				header : "<span style='font-weight:bold'>基准值(备)</span>",
				width : 100,
				tooltip:'可编辑列',
				dataIndex : 'SUB_CUT_PM_VALUE',
				renderer : function(value, cellmeta, record) {
					return Ext.util.Format.number(value, '0.00');
				},
				editor : new Ext.form.NumberField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}]
});

var oppositeDownRightPageTool = new Ext.PagingToolbar({
			id : 'oppositeDownRightPageTool',
			pageSize : 200,// 每页显示的记录值
			store : oppositeDownRightStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var oppositeDownRightGridPanel = new Ext.grid.EditorGridPanel({
	id : "oppositeDownRightGridPanel",
	flex : 5,
	// title:'任务信息列表',
	cm : oppositeDownRightCm,
	store : oppositeDownRightStore,
	// collapsed: true, // initially collapse the group
	// collapsible: true,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : oppositeDownRightCheckboxSelectionModel, // 必须加不然不能选checkbox
	bbar : oppositeDownRightPageTool,
	// tbar: pageTool,
	// viewConfig : {
	// forceFit : true
	// },
	tbar : {
		xtype : "container",
		border : false,
		items : [{
			// tbar第二行工具栏
			xtype : "toolbar",
			items : ['-',{
						text : '新增端口',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							// addPortRightopposite(2,2);
							addPortLeftopposite(2, 2);
						}

					}, {
						text : '新增虚拟端口',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							// addVirtualPortRightopposite(2,2);
							addVirtualPortLeftopposite(2, 2);
						}

					}, {
						text : '修改虚拟端口',
						icon : '../../../resource/images/btnImages/modify.png',
						handler : function() {
							// modifyVirtualPortRightopposite(2,2);
							modifyVirtualPortLeftForward(2, 2);
						}

					}, {
						text : '新增光缆',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							// addRightoppositeFiber(2,2);
							addLeftoppositeFiber(2, 2);
						}

					},'-', {
						text : '上移',
						icon : '../../../resource/images/btnImages/up.png',
						handler : function() {
							upRightopposite(oppositeDownRightGridPanel,
									oppositeDownRightStore);
						}

					}, {
						text : '下移',
						icon : '../../../resource/images/btnImages/down.png',
						handler : function() {
							downRightopposite(oppositeDownRightGridPanel,
									oppositeDownRightStore);
						}

					}, '-',{
						text : '删除',
						icon : '../../../resource/images/btnImages/delete.png',
						handler : function() {
							deleteRightopposite();
						}

					}, '-',{
						text : '基准值生成',
						icon : '../../../resource/images/btnImages/set_baseline.png',
						handler : function() {
							newStandLeftForward(6);
						}

					}]
		}]

	}
});

/**
 * 调用左边的树
 */
function addPortRightopposite(portType) {
	// addPortLeftopposite(portType);
}

/**
 * 备用端口新增虚拟端口
 */
function addVirtualPortRightopposite(portType, type) {
	addVirtualPortLeftopposite(portType, type);
}

/**
 * 修改备用虚拟端口
 */
function modifyVirtualPortRightopposite(portType) {
	// modifyVirtualPortLeftopposite(portType);
}

/**
 * 新增备用光纤 1.左 2.右
 */
function addRightoppositeFiber(portType) {
	// addLeftoppositeFiber(portType);
}

function upRightopposite(oppositeDownRightGridPanel, oppositeDownRightStore) {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeDownRightGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
	var index = oppositeDownRightStore.indexOf(record);
	if (index == 0) {
		return;
	}
	isPortopposite = false;
	oppositeDownRightStore.remove(record);
	oppositeDownRightStore.insert(index - 1, record);
	oppositeDownRightGridPanel.getView().refresh();
	oppositeDownRightGridPanel.getSelectionModel().selectRow(index - 1);
}

function downRightopposite(oppositeDownRightGridPanel, oppositeDownRightStore) {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeDownRightGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
	var index = oppositeDownRightStore.indexOf(record);
	if (index == oppositeDownRightStore.getCount() - 1) {
		return;
	}
	isPortopposite = false;
	oppositeDownRightStore.remove(record);
	oppositeDownRightStore.insert(index + 1, record);
	oppositeDownRightGridPanel.getView().refresh();
	oppositeDownRightGridPanel.getSelectionModel().selectRow(index + 1);
}

function deleteRightopposite() {
	if (!isSaveopposite) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = oppositeDownRightGridPanel.getSelectionModel().getSelections();
	if (record.length > 0) {
		isPortopposite = false;
		for (var i = 0; i < record.length; i++) {
			oppositeDownRightStore.remove(record[i]);
		}

	} else {
		Ext.Msg.alert('信息', '请选择要删除的记录！');
		return;
	}

}
// 正向组装panel 开始
// 最下面的两个store
var oppositeDownGridPanel = new Ext.Panel({
			id : "oppositeDownGridPanel",
			region : "center",
			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			flex : 3,
			items : [oppositeDownLeftGridPanel, oppositeDownRightGridPanel]
		});
// store和标题组装
var oppositeDownPanel = new Ext.Panel({
			id : "oppositeDownPanel",
			region : "center",
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			flex : 6,
			items : [oppositeDownTitleGridPanel, oppositeDownGridPanel]
		});
// 上下组装
var oppositeGrid = new Ext.Panel({
			id : "oppositeGrid",
			region : "center",
			layout : {
				type : 'vbox',
				align : 'stretch'
			},

			items : [oppositeUpPanel, oppositeDownPanel]
		});

oppositeDownRightStore.on("load", function(s) {

			s.each(function(record) {
						if (record.get('SUB_PTP_ID') == -1) {
							s.remove(record);
						}
					});
		});
