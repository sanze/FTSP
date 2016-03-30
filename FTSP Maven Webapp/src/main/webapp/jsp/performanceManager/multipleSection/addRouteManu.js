/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
// 定义全局变量来衡量修改后是否已经保存
var isSave = true;
var isSaveopposite = true;

var isPort = true;
var isPortopposite = true;

var neIdForward="";
var neRouteIdForward;
var neNameForward;
var virPortForward = new Array();
var neIdopposite;
var neRouteIdopposite;
var neNameopposite;
var virPortopposite = new Array();
Ext.QuickTips.init(); 
// 正向
var jsonString = new Array();
var map = {
	"MULTI_SEC_ID" : mul_id,
	"DIRECTION" : 1
};
jsonString.push(map);

var forwardUpStore = new Ext.data.Store({
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
var forwardUpCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
			singleSelect : true,
			header : ""
		});
var forwardUpCm = new Ext.grid.ColumnModel({
			// specify any defaults for each column
			defaults : {
				sortable : true
				// columns are not sortable by default
			},
			columns : [forwardUpCheckboxSelectionModel, {
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

var forwardUpPageTool = new Ext.PagingToolbar({
			id : 'forwardUpPageTool',
			pageSize : 200,// 每页显示的记录值
			store : forwardUpStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var forwardUpPanel = new Ext.grid.GridPanel({
	id : "forwardUpPanel",
	region : "north",
	flex : 4,
	// title:'任务信息列表',
	cm : forwardUpCm,
	store : forwardUpStore,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: true, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : forwardUpCheckboxSelectionModel, // 必须加不然不能选checkbox
	bbar : forwardUpPageTool,
	listeners : {
		'rowclick' : function(forwardUpPanel, rowIndex, e) {
			e.preventDefault();// 阻止默认事件的传递
			if (!isPort) {
				Ext.Msg.alert('信息', '请先保存端口修改信息！');
				return;
			}
			var record = forwardUpPanel.getSelectionModel().getSelected();
			// 如果不选则为空
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
				"DIRECTION" : 1
			};

			forwardDownLeftStore.proxy = new Ext.data.HttpProxy({
						url : 'multiple-section!selectPtpRouteList.action'
					});
			forwardDownLeftStore.baseParams = jsonData;
			forwardDownLeftStore.load({
						callback : function(r, options, success) {
							if (success) {
								forwardDownLeftGridPanel.getSelectionModel()
										.selectFirstRow();
							} else {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
			forwardDownRightStore.proxy = new Ext.data.HttpProxy({
						url : 'multiple-section!selectSubPtpRouteList.action'
					});
			forwardDownRightStore.baseParams = jsonData;
			forwardDownRightStore.load({
						callback : function(r, options, success) {
							if (success) {
								forwardDownRightGridPanel.getSelectionModel()
										.selectFirstRow();
							} else {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
			// 网元id赋值
			neIdForward = record.get('BASE_NE_ID');
			neRouteIdForward = record.get('MULTI_SEC_NE_ID');
			neNameForward = record.get('NE_NAME');
// // 查询光放型号
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
								// privilege:addAuth,
								icon : '../../../resource/images/btnImages/add.png',
								handler : function() {
									addNeForward();
								}

							}, {
								text : '移除网元',
								// privilege:delAuth,
								icon : '../../../resource/images/btnImages/delete.png',
								handler : function() {
									deleteNeForward();
								}

							},'-', {
								text : '上移',
								icon : '../../../resource/images/btnImages/up.png',
								handler : function() {
									upForward(forwardUpPanel, forwardUpStore);

								}

							}, {
								text : '下移',
								icon : '../../../resource/images/btnImages/down.png',
								handler : function() {
									downForward(forwardUpPanel, forwardUpStore);
								}

							}, '-',{
								text : '保存',
								icon : '../../../resource/images/btnImages/disk.png',
								handler : function() {
									saveForward();
								}

							}]
				}]

	}
});

forwardUpPanel.getStore().addListener({

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
 * 获取选择的网元
 * 
 * @param {}
 *            result 返回选择的结果集
 * @param {}
 *            type 类型 1是正向网元 2 是反向网元，3是正向端口 4是反向端口
 * @param {}
 *            portType 1. 左 2.右
 */
function getNeFromTree(result, type, portType) {
	if (result.length == 0) {
		Ext.Msg.alert("提示", "没有勾选！");
		// 判断是否选择端口
	} else if ((type == 1 || type == 2)) {
		for (var i = 0; i < result.length; i++) {
			if (result[i].nodeLevel != 4) {

				Ext.Msg.alert("提示", "请选择网元级别！");
				return;
			}
		}

		if (type == 1) {
			// 1是正向网元
			setNeForward(result);
		} else if (type == 2) {
			// 2 是反向网元
			setNeopposite(result);
		}
	} else if ((type == 3 || type == 4 || type == 5)) {
		for (var i = 0; i < result.length; i++) {
			if (result[i].nodeLevel != 8) {

				Ext.Msg.alert("提示", "请选择端口级别！");
				return;
			}
		}

		if (type == 3) {
			// 3是正向端口
			if (portType == 1) {
				setPortLeftForward(result)
			} else {
				setPortRightForward(result)
			}

		} else if (type == 4) {
			// 4是反向端口
			if (portType == 1) {
				setPortLeftopposite(result)
			} else {
				setPortRightopposite(result)
			}

		} else if (type == 5) {
			if (refrshForward != null) {
				refrshForward(result);
			}
		}
	} else {

	}

}

var refrshForward = null;

/**
 * 设置正向网元
 * 
 * @param {}
 *            result tree 返回结果集
 */
function setNeForward(result) {
	var isAgain = false;
	var text = "";
	// 获取网元相关信息
	for (var i = 0; i < result.length; i++) {
		var neId = result[i].nodeId
		// 判断网元是否已被选中
		for (var j = 0; j < forwardUpStore.getCount(); j++) {
			if (neId == forwardUpStore.getAt(j).get('BASE_NE_ID')) {
				text += result[i].text + ", ";
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
							var select = forwardUpPanel.getSelectionModel()
									.getSelected();
							var index = forwardUpStore.indexOf(select);
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
							var record = forwardUpStore.recordType;
							var p = new record(neRouteData);
							if (index == -1) {
								forwardUpStore.insert(
										forwardUpStore.getCount(), p);
							} else {
								forwardUpStore.insert(index, p);
							}

							forwardUpPanel.getView().refresh();
							forwardUpPanel.getSelectionModel().selectLastRow();
							// 标记为需要保存
							isSave = false;
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
function addNeForward() {
	if (!isPort) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}

	var url = "addTree.jsp?level=4&type=1&checkModel=multiple&rootType=2&rootId="
			+ emsId + "&rootVisible=" + true;
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
function deleteNeForward() {
	if (!isPort) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}

	var select = forwardUpPanel.getSelectionModel().getSelections();
	if (select.length > 0) {
		// 判断网元是否含有端口
		if (forwardDownLeftStore.getCount() > 0) {
			Ext.Msg.confirm("确认", "选择网元已经添加端口顺序数据，是否删除？", function(button) {
						if (button == 'yes') {

							// 标记为需要保存
							isSave = false;
							forwardUpStore.remove(select);
							if(forwardUpStore.getCount()>0){
								forwardUpPanel.getView().refresh();
								forwardUpPanel.getSelectionModel().selectLastRow();
							}else{
								forwardDownLeftStore.removeAll();
								forwardDownRightStore.removeAll();
							}
						
						}
					});
		} else {
			// 增加豆蔻绑定判断，先删除端口，未完成
			Ext.Msg.confirm("确认", "你确定要移除所选网元？", function(button) {
						if (button == 'yes') {

							// 标记为需要保存
							isSave = false;
							forwardUpStore.remove(select);
							if(forwardUpStore.getCount()>0){
							forwardUpPanel.getView().refresh();
							forwardUpPanel.getSelectionModel().selectLastRow();
							}else{
								forwardDownLeftStore.removeAll();
								forwardDownRightStore.removeAll();
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
 *            forwardUpPanel panel
 * @param {}
 *            forwardUpStore store
 */
function upForward(forwardUpPanel, forwardUpStore) {
	if (!isPort) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}

	var record = forwardUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert("提示", "请选择要移动的网元！");
		return;
	}
	/**
	 * 标记为需要保存
	 * 
	 * @type Boolean
	 */
	isSave = false;
	var index = forwardUpStore.indexOf(record);
	if (index == 0) {
		return;
	}
	forwardUpStore.remove(record);
	forwardUpStore.insert(index - 1, record);
	forwardUpPanel.getView().refresh();
	forwardUpPanel.getSelectionModel().selectRow(index - 1);
}

/**
 * 下移
 * 
 * @param {}
 *            forwardUpPanel
 * @param {}
 *            forwardUpStore
 */
function downForward(forwardUpPanel, forwardUpStore) {
	if (!isPort) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}

	var record = forwardUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert("提示", "请选择要移动的网元！");
		return;
	}
	/**
	 * 标记为需要保存
	 * 
	 * @type Boolean
	 */
	isSave = false;
	var index = forwardUpStore.indexOf(record);
	if (index == forwardUpStore.getCount() - 1) {
		return;
	}
	forwardUpStore.remove(record);
	forwardUpStore.insert(index + 1, record);
	forwardUpPanel.getView().refresh();
	forwardUpPanel.getSelectionModel().selectRow(index + 1);
}

/**
 * 保存网元顺序
 */
function saveForward() {
	if (!isPort) {
		Ext.Msg.alert('信息', '请先保存端口修改信息！');
		return;
	}
	var jsonString = new Array();
	for (var i = 0; i < forwardUpStore.getCount(); i++) {
		var map = {
			"MULTI_SEC_NE_ID" : forwardUpStore.getAt(i).get('MULTI_SEC_NE_ID'),
			"MULTI_SEC_ID" : forwardUpStore.getAt(i).get('MULTI_SEC_ID'),
			"BASE_NE_ID" : forwardUpStore.getAt(i).get('BASE_NE_ID'),
			"DIRECTION" : 1
		};
		jsonString.push(map);
	}

	var jsonData = {
		"mulId" : mul_id,
		"direction" : 1,
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
		url : 'multiple-section!saveNeForward.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {// 回调函数
			// 标记为已保存
			isSave = true;
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert("提示", obj.returnMessage, function(r) {
							// 刷新列表
							var forwardUpPageTool = Ext
									.getCmp('forwardUpPageTool');
							if (forwardUpPageTool) {
								forwardUpPageTool
										.doLoad(forwardUpPageTool.cursor);
							}

							if (forwardUpPanel.getView().getRow(0)) {
								forwardUpPanel.getView().getRow(0).click();
							}
							forwardUpPanel.getSelectionModel().selectFirstRow();
							if (forwardUpStore.getCount() > 0) {
								neIdForward = forwardUpStore.getAt(0)
										.get('BASE_NE_ID')
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

// 正向路由设置，端口设置
var forwardDownTitleGridPanel = new Ext.Panel({

	id : "forwardDownTitleGridPanel",
	region : "north",
	// flex : 1,
	// cm : forwardUpCm,
	// store : forwardUpStore,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	// stripeRows : true, // 交替行效果
	// loadMask : true,
	// selModel : forwardUpCheckboxSelectionModel, // 必须加不然不能选checkbox
	// bbar : forwardUpPageTool,
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
							refreshForward();

						}

					}, '-',{
						text : '主备路由切换',
						handler : function() {
							exchangeForward();
						}

					}, '-',{
						text : '保存',
						icon : '../../../resource/images/btnImages/disk.png',
						handler : function() {
							savePortForward();
						}

					}]
				}]

	}
});

/**
 * 正向重置
 */
function refreshForward() {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	isPort = true;
	forwardDownLeftStore.reload();
	forwardDownRightStore.reload();

}

/**
 * 主被路由切换
 */
function exchangeForward() {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	isPort = false;
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
	forwardDownLeftStore.each(function(record) {
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

				forwardDownRightGridPanel.getView().refresh();
				forwardDownRightGridPanel.getSelectionModel().selectLastRow();

			});
	forwardDownLeftStore.removeAll();
	// 将右边store的值赋给左边变量
	var j = forwardDownLeftStore.getCount();
	forwardDownRightStore.each(function(record) {
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
				var record = forwardDownLeftStore.recordType;
				var p = new record(rec);
				forwardDownLeftStore.insert(j, p);
				j++;
			});
	forwardDownRightStore.removeAll();
	// 将临时store的值赋给右边
	var k = forwardDownRightStore.getCount();
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
				var record = forwardDownRightStore.recordType;
				var p = new record(rec);
				forwardDownRightStore.insert(k, p);
				k++;
			});

}

/**
 * 保存正向端口配置
 */
function savePortForward() {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	isPort = true;
	// 判断是否值量表store相等
	if (forwardDownRightStore.getCount() > 0
			&& forwardDownLeftStore.getCount() != forwardDownRightStore
					.getCount()) {
		Ext.Msg.alert('提示', "主备用行数不对等，请添加空行补全！");
		return;
	}

	// 判断光缆是否在最下面

	var gl = ""; // 记录主用光缆的位置
	var gr = ""; // 记录备用光缆的位置
	var jsonString = new Array();
	for (var i = 0; i < forwardDownLeftStore.getCount(); i++) {
		// 判断光缆是否在最下面
		if (forwardDownLeftStore.getAt(i).get('ROUTE_TYPE') == 5) {
			gl = i;
			if (i != forwardDownLeftStore.getCount() - 1) {
				Ext.Msg.alert('提示', "主用端口侧，光缆最多只有一条且放在最末尾！");
				return;
			}
		}
// // 端口 并且不为LAC盘和虚拟端口的光放和性能类型不能为空
// if ((forwardDownLeftStore.getAt(i).get('ROUTE_TYPE') ==
// 1&&forwardDownLeftStore.getAt(i).get('EQUIP_TYPE').indexOf("LAC") ==-1)
// || forwardDownLeftStore.getAt(i).get('ROUTE_TYPE') == 2) {
// // 查看
// if (forwardDownLeftStore.getAt(i).get('PM_STD_OPT_AMP_ID') == "") {
// Ext.Msg.alert('提示', "主用端口"
// + forwardDownLeftStore.getAt(i).get('PTP_NAME')
// + "光放型号不能为空！");
// return;
// }
// if (forwardDownLeftStore.getAt(i).get('PM_TYPE') == "") {
// Ext.Msg.alert('提示', "主用端口"
// + forwardDownLeftStore.getAt(i).get('PTP_NAME')
// + "性能类型不能为空！");
// return;
// }
// }
		if (forwardDownRightStore.getCount() > 0
				&& forwardDownRightStore.getAt(i).get('SUB_ROUTE_TYPE') == 5) {
			gr = i;
			if (i != forwardDownLeftStore.getCount() - 1) {
				Ext.Msg.alert('提示', "备用端口侧，光缆最多只有一条且放在最末尾！");
				return;
			}
		}
// // 端口和虚拟端口的光放和性能类型不能为空
// if (forwardDownRightStore.getCount() > 0) {
// if ((forwardDownRightStore.getAt(i).get('SUB_ROUTE_TYPE') ==
// 1&&forwardDownLeftStore.getAt(i).get('SUB_EQUIP_TYPE').indexOf("LAC") ==-1)
// || forwardDownRightStore.getAt(i).get('SUB_ROUTE_TYPE') == 2) {
// // 查看
// if (forwardDownRightStore.getAt(i).get('SUB_PM_STD_OPT_AMP_ID') == "") {
// Ext.Msg.alert('提示', "备用端口"
// + forwardDownRightStore.getAt(i)
// .get('SUB_PTP_NAME') + "光放型号不能为空！");
// return;
// }
// if (forwardDownRightStore.getAt(i).get('SUB_PM_TYPE') == "") {
// Ext.Msg.alert('提示', "备用端口"
// + forwardDownRightStore.getAt(i)
// .get('SUB_PTP_NAME') + "性能类型不能为空！");
// return;
// }
// }
// }
		var map = {
			"PM_MULTI_SEC_PTP_ID" : forwardDownLeftStore.getAt(i)
					.get('PM_MULTI_SEC_PTP_ID'),
			"MULTI_SEC_ID" : forwardDownLeftStore.getAt(i).get('MULTI_SEC_ID'),
			"MULTI_SECT_NE_ROUTE_ID" : forwardDownLeftStore.getAt(i)
					.get('MULTI_SECT_NE_ROUTE_ID'),
			"PTP_ID" : forwardDownLeftStore.getAt(i).get('PTP_ID'),
			"EQUIP_NAME" : forwardDownLeftStore.getAt(i).get('EQUIP_NAME'),
			"PTP_NAME" : forwardDownLeftStore.getAt(i).get('PTP_NAME'),
			"PM_STD_OPT_AMP_ID" : forwardDownLeftStore.getAt(i)
					.get('PM_STD_OPT_AMP_ID'),
			"PM_TYPE" : forwardDownLeftStore.getAt(i).get('PM_TYPE'),
			"NOTE" : forwardDownLeftStore.getAt(i).get('NOTE'),
			"CALCULATE_POINT" : forwardDownLeftStore.getAt(i)
					.get('CALCULATE_POINT'),
			"CUT_PM_VALUE" : forwardDownLeftStore.getAt(i).get('CUT_PM_VALUE'),
			"ROUTE_TYPE" : forwardDownLeftStore.getAt(i).get('ROUTE_TYPE'),
			"SUB_PTP_ID" : "-1",
			"SUB_PTP_NAME" : "",
			"SUB_PM_TYPE" : "",
			"SUB_NOTE" : "",
			"SUB_CALCULATE_POINT" : "",
			"SUB_CUT_PM_VALUE" : "",
			"SUB_ROUTE_TYPE" : "",
			"SUB_PM_STD_OPT_AMP_ID" : "",
			"DIRECTION" : 1
		};
		if (forwardDownRightStore.getCount() > 0) {
			map = {
				"PM_MULTI_SEC_PTP_ID" : forwardDownLeftStore.getAt(i)
						.get('PM_MULTI_SEC_PTP_ID'),
				"MULTI_SEC_ID" : forwardDownLeftStore.getAt(i)
						.get('MULTI_SEC_ID'),
				"MULTI_SECT_NE_ROUTE_ID" : forwardDownLeftStore.getAt(i)
						.get('MULTI_SECT_NE_ROUTE_ID'),
				"PTP_ID" : forwardDownLeftStore.getAt(i).get('PTP_ID'),
				"EQUIP_NAME" : forwardDownLeftStore.getAt(i).get('EQUIP_NAME'),
				"PTP_NAME" : forwardDownLeftStore.getAt(i).get('PTP_NAME'),
				"PM_STD_OPT_AMP_ID" : forwardDownLeftStore.getAt(i)
						.get('PM_STD_OPT_AMP_ID'),
				"PM_TYPE" : forwardDownLeftStore.getAt(i).get('PM_TYPE'),
				"NOTE" : forwardDownLeftStore.getAt(i).get('NOTE'),
				"CALCULATE_POINT" : forwardDownLeftStore.getAt(i)
						.get('CALCULATE_POINT'),
				"CUT_PM_VALUE" : forwardDownLeftStore.getAt(i)
						.get('CUT_PM_VALUE'),
				"ROUTE_TYPE" : forwardDownLeftStore.getAt(i).get('ROUTE_TYPE'),
				"SUB_PTP_ID" : forwardDownRightStore.getAt(i).get('SUB_PTP_ID'),
				"SUB_PTP_NAME" : forwardDownRightStore.getAt(i)
						.get('SUB_PTP_NAME'),
				"SUB_PM_TYPE" : forwardDownRightStore.getAt(i)
						.get('SUB_PM_TYPE'),
				"SUB_NOTE" : forwardDownRightStore.getAt(i).get('SUB_NOTE'),
				"SUB_CALCULATE_POINT" : forwardDownRightStore.getAt(i)
						.get('SUB_CALCULATE_POINT'),
				"SUB_CUT_PM_VALUE" : forwardDownRightStore.getAt(i)
						.get('SUB_CUT_PM_VALUE'),
				"SUB_ROUTE_TYPE" : forwardDownRightStore.getAt(i)
						.get('SUB_ROUTE_TYPE'),
				"SUB_PM_STD_OPT_AMP_ID" : forwardDownRightStore.getAt(i)
						.get('SUB_PM_STD_OPT_AMP_ID'),
				"DIRECTION" : 1
			};
		}
		jsonString.push(map);
	}
	if (forwardDownRightStore.getCount() > 0 && !gl == gr) {
		Ext.Msg.alert('提示', "请确认主备用侧都有光缆，且在同一位置！");
		return;
	}
	var jsonData = {
		"neId" : neRouteIdForward,
		"direction" : 1,
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
			isSave = true;
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert("提示", obj.returnMessage, function(r) {
							// 刷新列表
							var forwardDownLeftPageTool = Ext
									.getCmp('forwardDownLeftPageTool');
							if (forwardDownLeftPageTool) {
								forwardDownLeftPageTool
										.doLoad(forwardDownLeftPageTool.cursor);
							}

							var forwardDownRightPageTool = Ext
									.getCmp('forwardDownRightPageTool');
							if (forwardDownRightPageTool) {
								forwardDownRightPageTool
										.doLoad(forwardDownRightPageTool.cursor);
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

var forwardDownLeftStore = new Ext.data.Store({
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
// forwardDownLeftStore.load();
// ************************* 任务信息列模型 ****************************
var forwardDownLeftCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel(
		{
			singleSelect : true,
			header : ""

		});
var forwardDownLeftCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [ forwardDownLeftCheckboxSelectionModel, {
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
			},{
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
				tooltip:'可编辑列',
				dataIndex : 'PM_STD_OPT_AMP_ID',
				renderer : function(value, cellmeta, record) {
					// 通过匹配value取得ds索引
					// var index = modelTypeStore.find(Ext
					// .getCmp('MODEL').valueField,
					// value);
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
							id : "modeType",
							name : "modeType",
							mode : "local",
							displayField : "MODEL",
							valueField : 'PM_STD_OPT_AMP_ID',
							store : modelTypeStore,
							triggerAction : 'all',
							editable : false,
							resizable: true,
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
							id : "pmType",
							name : "pmType",
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
				width : 100,
				tooltip:'可编辑列',
				editor : new Ext.form.TextField({
							// allowBlank: false,
							allowNegative : true,
							maxLenth : 100
						})
			}, {
				id : 'CALCULATE_POINT',
				header : "<span style='font-weight:bold'>理论值(主)</span>",
				width : 80,
				dataIndex : 'CALCULATE_POINT',
				tooltip:'可编辑列',
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

var forwardDownLeftPageTool = new Ext.PagingToolbar({
			id : 'forwardDownLeftPageTool',
			pageSize : 200,// 每页显示的记录值
			store : forwardDownLeftStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var forwardDownLeftGridPanel = new Ext.grid.EditorGridPanel({
	id : "forwardDownLeftGridPanel",
	flex : 6,
	// title:'任务信息列表',
	cm : forwardDownLeftCm,
	store : forwardDownLeftStore,
	// collapsed: true, // initially collapse the group
	// collapsible: true,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : forwardDownLeftCheckboxSelectionModel, // 必须加不然不能选checkbox
	bbar : forwardDownLeftPageTool,
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
							addPortLeftForward(1, 3);
						}

					}, {
						text : '新增虚拟端口',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							addVirtualPortLeftForward(1, 1);
						}
					}, {
						text : '修改虚拟端口',
						icon : '../../../resource/images/btnImages/modify.png',
						handler : function() {
							modifyVirtualPortLeftForward(1, 1);
						}

					}, {
						text : '新增光缆',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							addLeftForwardFiber(1, 1);
						}

					},'-', {
						text : '上移',
						icon : '../../../resource/images/btnImages/up.png',
						handler : function() {
							upLeftForward(forwardDownLeftGridPanel,
									forwardDownLeftStore);
						}

					}, {
						text : '下移',
						icon : '../../../resource/images/btnImages/down.png',
						handler : function() {
							downLeftForward(forwardDownLeftGridPanel,
									forwardDownLeftStore);
						}

					}, '-',{
						text : '删除',
						icon : '../../../resource/images/btnImages/delete.png',
						handler : function() {
							deleteLeftForward();
						}

					},'-', {
						text : '基准值生成',
						icon : '../../../resource/images/btnImages/set_baseline.png',
						handler : function() {
							newStandLeftForward(3);
						}

					}]
		}]

	}
});

/**
 * 更新正向基准值 value （1是正向 2 是反向）在复用段详情页面使用 ,（3 正左，4 正右 5 反左 6 反右） 在复用段设置页面使用
 */
function newStandLeftForward(value) {
	if (value == "3" || value == "4") {
		if (!isSave) {
			Ext.Msg.alert('信息', '请先保存网元修改信息！');
			return;
		}
		isPort = false;
	} else if (value == "5" || value == "6") {
		if (!isSaveopposite) {
			Ext.Msg.alert('信息', '请先保存网元修改信息！');
			return;
		}
		isPortopposite= false;
	}
	if (value == "3") {
		var select = forwardDownLeftGridPanel.getSelectionModel()
				.getSelections();
		// 选中了，就赋值选中的记录
		if (select.length > 0) {
			for (var i = 0; i < select.length; i++) {
				forwardDownLeftStore.each(function(record) {
							if (select[i].get("PM_MULTI_SEC_PTP_ID") == record
									.get("PM_MULTI_SEC_PTP_ID")) {
								// 理论值
								record.set("CUT_PM_VALUE", record
												.get("CALCULATE_POINT"));
								record.set("SUB_CUT_PM_VALUE", record
												.get("SUB_CALCULATE_POINT"));
							}
						})
			}

		} else {
			// 如果没选，则赋值全部
			forwardDownLeftStore.each(function(record) {
						// 理论值
						record.set("CUT_PM_VALUE", record
										.get("CALCULATE_POINT"));
						record.set("SUB_CUT_PM_VALUE", record
										.get("SUB_CALCULATE_POINT"));

					})
		}
	} else if (value == "4") {

		var select = forwardDownRightGridPanel.getSelectionModel()
				.getSelections();
		// 选中了，就赋值选中的记录
		if (select.length > 0) {
			for (var i = 0; i < select.length; i++) {
				forwardDownRightStore.each(function(record) {
							if (select[i].get("PM_MULTI_SEC_PTP_ID") == record
									.get("PM_MULTI_SEC_PTP_ID")) {
								// 理论值
								record.set("CUT_PM_VALUE", record
												.get("CALCULATE_POINT"));
								record.set("SUB_CUT_PM_VALUE", record
												.get("SUB_CALCULATE_POINT"));
							}
						})
			}

		} else {
			// 如果没选，则赋值全部
			forwardDownRightStore.each(function(record) {
						// 理论值
						record.set("CUT_PM_VALUE", record
										.get("CALCULATE_POINT"));
						record.set("SUB_CUT_PM_VALUE", record
										.get("SUB_CALCULATE_POINT"));
					})
		}

	} else if (value == "5") {
		var select = oppositeDownLeftGridPanel.getSelectionModel()
				.getSelections();
		// 选中了，就赋值选中的记录
		if (select.length > 0) {
			for (var i = 0; i < select.length; i++) {
				oppositeDownLeftStore.each(function(record) {
							if (select[i].get("PM_MULTI_SEC_PTP_ID") == record
									.get("PM_MULTI_SEC_PTP_ID")) {
								// 理论值
								record.set("CUT_PM_VALUE", record
												.get("CALCULATE_POINT"));
								record.set("SUB_CUT_PM_VALUE", record
												.get("SUB_CALCULATE_POINT"));
							}
						})
			}

		} else {
			// 如果没选，则赋值全部
			oppositeDownLeftStore.each(function(record) {
						// 理论值
						record.set("CUT_PM_VALUE", record
										.get("CALCULATE_POINT"));
						record.set("SUB_CUT_PM_VALUE", record
										.get("SUB_CALCULATE_POINT"));
					})
		}
	} else if (value == "6") {

		var select = oppositeDownRightGridPanel.getSelectionModel()
				.getSelections();
		// 选中了，就赋值选中的记录
		if (select.length > 0) {
			for (var i = 0; i < select.length; i++) {
				oppositeDownRightStore.each(function(record) {
							if (select[i].get("PM_MULTI_SEC_PTP_ID") == record
									.get("PM_MULTI_SEC_PTP_ID")) {
								// 理论值
								record.set("CUT_PM_VALUE", record
												.get("CALCULATE_POINT"));
								record.set("SUB_CUT_PM_VALUE", record
												.get("SUB_CALCULATE_POINT"));
							}
						})
			}

		} else {
			// 如果没选，则赋值全部
			oppositeDownRightStore.each(function(record) {
						// 理论值
						record.set("CUT_PM_VALUE", record
										.get("CALCULATE_POINT"));
						record.set("SUB_CUT_PM_VALUE", record
										.get("SUB_CALCULATE_POINT"));
					})
		}

	}

}

/**
 * 新增端口选择树
 * 
 * @param {}
 *            type 1. 左 2.右
 */
function addPortLeftForward(portType, type) {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardUpPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请先选择要设定端口的网元！');
		return;
	}
	var url = "addTree.jsp?level=8&checkModel=multiple&portType=" + portType
			+ "&type=3&rootType=4&rootId=" + neIdForward + "&rootVisible="
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
function setPortLeftForward(result) {
	isPort = false;
	var portName = "";
	for(var i = 0 ; i <result.length;i++){
		var neName = result[i]['path:text'].split(":");
		var neId = result[i]['path:nodeId'].split(":");
		var portId = result[i]["nodeId"];
		
		// 判断网元是否已被选中
		for (var j = 0; j < forwardDownLeftStore.getCount(); j++) {
			var ptpId = (forwardDownLeftStore.getAt(j).get('PTP_ID') + "")
					.split(',');
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
								var neName = result[i]['path:text'].split(":");
								var neId = result[i]['path:nodeId'].split(":");
								var portId = result[i]["nodeId"];
							if (neId[neId.length - 4] != neIdForward&&neId[0] != neIdForward) {
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

									// 查询成功以后，record赋值
									var select = forwardDownLeftGridPanel.getSelectionModel()
											.getSelected();
									var index = forwardDownLeftStore.indexOf(select);
									var ptpRouteData = {
										PTP_ID : pa[0].BASE_PTP_ID,
										EQUIP_NAME : pa[0].EQUIP_NAME,
										EQUIP_TYPE: pa[0].EQUIP_TYPE,
										PTP_NAME : obj.ptpName,
										MULTI_SEC_ID : mul_id,
										ROUTE_TYPE : 1,
										PM_TYPE : "",
										PM_STD_OPT_AMP_ID : "",
										MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
									}
									var record = forwardDownLeftStore.recordType;
									var p = new record(ptpRouteData);
									if (index == -1) {
										forwardDownLeftStore.insert(forwardDownLeftStore.getCount(), p);
									} else {
										forwardDownLeftStore.insert(index, p);
									}
						
									forwardDownLeftGridPanel.getView().refresh();
									forwardDownLeftGridPanel.getSelectionModel().selectLastRow();
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
				var neName = result[i]['path:text'].split(":");
				var neId = result[i]['path:nodeId'].split(":");
				var portId = result[i]["nodeId"];
			if (neId[neId.length - 4] != neIdForward&&neId[0] != neIdForward) {
				Ext.Msg.alert('提示', "请选择所属网元下的端口！");
				return;
			}
			// 判断选择是否跨网元
			var jsonString = new Array();
			var map = {
				"BASE_PTP_ID" : portId,
				"EQUIP_NAME":neName[neName.length - 4],
				"EQUIP_TYPE":neName[neName.length - 2]
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
					// 查询成功以后，record赋值
					var select = forwardDownLeftGridPanel.getSelectionModel()
							.getSelected();
					var index = forwardDownLeftStore.indexOf(select);
					var ptpRouteData = {
						PTP_ID : pa[0].BASE_PTP_ID,
						EQUIP_NAME : pa[0].EQUIP_NAME,
						EQUIP_TYPE : pa[0].EQUIP_TYPE,
						PTP_NAME : obj.ptpName,
						MULTI_SEC_ID : mul_id,
						ROUTE_TYPE : 1,
						PM_TYPE : "",
						PM_STD_OPT_AMP_ID : "",
						MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
					}
					var record = forwardDownLeftStore.recordType;
					var p = new record(ptpRouteData);
					if (index == -1) {
						forwardDownLeftStore.insert(forwardDownLeftStore.getCount(), p);
					} else {
						forwardDownLeftStore.insert(index, p);
					}
		
					forwardDownLeftGridPanel.getView().refresh();
					forwardDownLeftGridPanel.getSelectionModel().selectLastRow();
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
function setPortRightForward(result) {
	isPort = false;
	var portName = "";

	for(var i = 0 ; i <result.length;i++){
	var portId = result[i]["nodeId"];
	var neName = result[i]['path:text'].split(":");
	var neId = result[i]['path:nodeId'].split(":");
	// 判断网元是否已被选中
	for (var j = 0; j < forwardDownRightStore.getCount(); j++) {
		var ptpId = (forwardDownRightStore.getAt(j).get('SUB_PTP_ID') + "")
				.split(',');
		for (var k = 0; k < ptpId.length; k++) {
			if (portId == ptpId[k]) {
				portName+= result[i]["text"]+",";
				
			}
		}

	};
	}
	if(portName.length>0){
		portName = portName.substring(0,portName.length-1);
		Ext.Msg.confirm("确认",  portName + "已经添加过!是否继续？", function(button) {
					if (button == 'yes') {
						for(var i = 0 ; i <result.length;i++){
							var neName = result[i]['path:text'].split(":");
							var neId = result[i]['path:nodeId'].split(":");
							var portId = result[i]["nodeId"];
					if (neId[neId.length - 4] != neIdForward&&neId[0] != neIdForward) {
						Ext.Msg.alert('提示', "请选择所属网元下的端口！");
						return;
					}
					// 判断选择是否跨网元
					var jsonString = new Array();
					var map = {
						"BASE_PTP_ID" : portId,
						"EQUIP_NAME" : neName[neName.length - 4],
						"EQUIP_TYPE" : neName[neName.length - 2]
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
									// 查询成功以后，record赋值
									var select = forwardDownRightGridPanel.getSelectionModel()
											.getSelected();
									var index = forwardDownRightStore.indexOf(select);
				
									var ptpRouteData = {
										SUB_PTP_ID : pa[0].BASE_PTP_ID,
										SUB_EQUIP_NAME : pa[0].EQUIP_NAME,
										SUB_EQUIP_TYPE: pa[0].EQUIP_TYPE,
										SUB_PTP_NAME : obj.ptpName,
										MULTI_SEC_ID : mul_id,
										SUB_ROUTE_TYPE : 1,
										SUB_PM_TYPE : "",
										SUB_PM_STD_OPT_AMP_ID : "",
										MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
									}
									var record = forwardDownRightStore.recordType;
									var p = new record(ptpRouteData);
									if (index == -1) {
										forwardDownRightStore.insert(forwardDownRightStore
														.getCount(), p);
									} else {
										forwardDownRightStore.insert(index, p);
									}
				
									forwardDownRightGridPanel.getView().refresh();
									forwardDownRightGridPanel.getSelectionModel()
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
						}});
					}else{
						for(var i = 0 ; i <result.length;i++){
							var neName = result[i]['path:text'].split(":");
							var neId = result[i]['path:nodeId'].split(":");
							var portId = result[i]["nodeId"];
					if (neId[neId.length - 4] != neIdForward&&neId[0] != neIdForward) {
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
									// 查询成功以后，record赋值
									var select = forwardDownRightGridPanel.getSelectionModel()
											.getSelected();
									var index = forwardDownRightStore.indexOf(select);
				
									var ptpRouteData = {
										SUB_PTP_ID : pa[0].BASE_PTP_ID,
										SUB_EQUIP_NAME : pa[0].EQUIP_NAME,
										SUB_EQUIP_TYPE: pa[0].EQUIP_TYPE,
										SUB_PTP_NAME : obj.ptpName,
										MULTI_SEC_ID : mul_id,
										SUB_ROUTE_TYPE : 1,
										SUB_PM_TYPE : "",
										SUB_PM_STD_OPT_AMP_ID : "",
										MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
									}
									var record = forwardDownRightStore.recordType;
									var p = new record(ptpRouteData);
									if (index == -1) {
										forwardDownRightStore.insert(forwardDownRightStore
														.getCount(), p);
									} else {
										forwardDownRightStore.insert(index, p);
									}
				
									forwardDownRightGridPanel.getView().refresh();
									forwardDownRightGridPanel.getSelectionModel()
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
 * 
 * @param {}
 *            portType 1. 左 2 右
 * @param {}
 *            type 1. 正向 2 反向
 */
function addLeftForwardFiber(portType, type) {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardUpPanel.getSelectionModel().getSelected();
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
		html : '<iframe id="addLeftForwardFiberWindow" name = "addLeftForwardFiberWindow" src='
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
 * 保存干线
 * 
 * @param {}
 *            fiberName 干线名称
 * @param {}
 *            caculateValue 理论值 portType 1.主 2.备 type 1.正向， 2是反向
 */
function saveLeftForwardFiber(fiberName, caculateValue, portType, type) {
	
	var fiber = "";
	if (type == "1") {
		isPort = false;
		if (portType == 1) {
			fiber = {
				PTP_ID : "",
				EQUIP_NAME : "光缆",
				PTP_NAME : fiberName,
				CALCULATE_POINT : caculateValue,
				MULTI_SEC_ID : mul_id,
				ROUTE_TYPE : 5,
				PM_TYPE:4,
				MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
			}
			var record = forwardDownLeftStore.recordType;
			var p = new record(fiber);

			forwardDownLeftStore.insert(forwardDownLeftStore.getCount(), p);

			forwardDownLeftGridPanel.getView().refresh();
			forwardDownLeftGridPanel.getSelectionModel().selectLastRow();
		} else {
			fiber = {
				SUB_PTP_ID : "",
				EQUIP_NAME : "光缆",
				SUB_PTP_NAME : fiberName,
				SUB_CALCULATE_POINT : caculateValue,
				MULTI_SEC_ID : mul_id,
				SUB_ROUTE_TYPE : 5,
				SUB_PM_TYPE:4,
				MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
			}
			var record = forwardDownRightStore.recordType;
			var p = new record(fiber);

			forwardDownRightStore.insert(forwardDownRightStore.getCount(), p);

			forwardDownRightGridPanel.getView().refresh();
			forwardDownRightGridPanel.getSelectionModel().selectLastRow();
		}

	} else if (type == "2") {
			isPortopposite = false;
		if (portType == 1) {
			fiber = {
				PTP_ID : "",
				EQUIP_NAME : "光缆",
				PTP_NAME : fiberName,
				CALCULATE_POINT : caculateValue,
				MULTI_SEC_ID : mul_id,
				ROUTE_TYPE : 5,
				PM_TYPE:4,
				MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
			}
			var record = oppositeDownLeftStore.recordType;
			var p = new record(fiber);

			oppositeDownLeftStore.insert(oppositeDownLeftStore.getCount(), p);

			oppositeDownLeftGridPanel.getView().refresh();
			oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
		} else {
			fiber = {
				SUB_PTP_ID : "",
				EQUIP_NAME : "光缆",
				SUB_PTP_NAME : fiberName,
				SUB_CALCULATE_POINT : caculateValue,
				MULTI_SEC_ID : mul_id,
				SUB_ROUTE_TYPE : 5,
				SUB_PM_TYPE:4,
				MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
			}
			var record = oppositeDownRightStore.recordType;
			var p = new record(fiber);

			oppositeDownRightStore.insert(oppositeDownRightStore.getCount(), p);

			oppositeDownRightGridPanel.getView().refresh();
			oppositeDownRightGridPanel.getSelectionModel().selectLastRow();
		}
	}
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
 *            virType 类型 ['1', '自定义'], ['2', '衰耗值'], ['3', '空白行'], ['4',
 *            '合并光功率'] portType 1. 主 2.备 type 1 正向 2 反向
 */
function saveVirtualPortLeftForward(virName, caculateDown, rows, direction,
		ids, virType, portType, type) {
			isPort = false;
	// ROUTE_TYPE :1.ptp口，2.虚拟端口，3.衰耗器，4.段衰耗，5.光缆，6.其他(空行),7.自定义
	var virPort;
	if (type == "1") {
		isPort = false;
		if (portType == 2) {
			if (virType == 1) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 2) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 3,
					SUB_PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 3) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : "",
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 4) {
				virPort = {
					SUB_PTP_ID : ids,
					EQUIP_NAME : neNameForward,
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_PM_TYPE : direction,
					SUB_PM_STD_OPT_AMP_ID : "",
					SUB_ROUTE_TYPE : 2,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			}
			var select = forwardDownRightGridPanel.getSelectionModel()
					.getSelected();
			var index = forwardDownRightStore.indexOf(select);

			if (virType != 3) {
				var record = forwardDownRightStore.recordType;
				var p = new record(virPort);

				if (index == -1) {
					forwardDownRightStore.insert(forwardDownRightStore
									.getCount(), p);
				} else {
					forwardDownRightStore.insert(index, p);
				}

				forwardDownRightGridPanel.getView().refresh();
				forwardDownRightGridPanel.getSelectionModel().selectLastRow();
			} else {
				for (var i = 0; i < rows; i++) {
					var record = forwardDownRightStore.recordType;
					var p = new record(virPort);

					if (index == -1) {
						forwardDownRightStore.insert(forwardDownRightStore
										.getCount(), p);
					} else {
						forwardDownRightStore.insert(index, p);
					}

					forwardDownRightGridPanel.getView().refresh();
					forwardDownRightGridPanel.getSelectionModel()
							.selectLastRow();
				}
			}
		} else {

			if (virType == 1) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 2) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 3,
					PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 3) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : "",
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 4) {
				virPort = {
					PTP_ID : ids,
					EQUIP_NAME : neNameForward,
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					PM_TYPE : direction,
					PM_STD_OPT_AMP_ID : "",
					ROUTE_TYPE : 2,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			}
			var select = forwardDownLeftGridPanel.getSelectionModel()
					.getSelected();
			var index = forwardDownLeftStore.indexOf(select);

			if (virType != 3) {
				var record = forwardDownLeftStore.recordType;
				var p = new record(virPort);

				if (index == -1) {
					forwardDownLeftStore.insert(
							forwardDownLeftStore.getCount(), p);
				} else {
					forwardDownLeftStore.insert(index, p);
				}
				// forwardDownLeftStore.insert(forwardDownLeftStore.getCount(),
				// p);

				forwardDownLeftGridPanel.getView().refresh();
				forwardDownLeftGridPanel.getSelectionModel().selectLastRow();
			} else {
				for (var i = 0; i < rows; i++) {
					var record = forwardDownLeftStore.recordType;
					var p = new record(virPort);

					if (index == -1) {
						forwardDownLeftStore.insert(forwardDownLeftStore
										.getCount(), p);
					} else {
						forwardDownLeftStore.insert(index, p);
					}

					forwardDownLeftGridPanel.getView().refresh();
					forwardDownLeftGridPanel.getSelectionModel()
							.selectLastRow();
				}
			}

		}
	} else if (type == "2") {
isPortopposite = false;
		if (portType == 2) {
			if (virType == 1) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 2) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 3,
					SUB_PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 3) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : "",
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 4) {
				virPort = {
					SUB_PTP_ID : ids,
					EQUIP_NAME : neNameopposite,
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_PM_TYPE : direction,
					SUB_PM_STD_OPT_AMP_ID : "",
					SUB_ROUTE_TYPE : 2,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			}
			var select = oppositeDownRightGridPanel.getSelectionModel()
					.getSelected();
			var index = oppositeDownRightStore.indexOf(select);

			if (virType != 3) {
				var record = oppositeDownRightStore.recordType;
				var p = new record(virPort);

				if (index == -1) {
					oppositeDownRightStore.insert(oppositeDownRightStore
									.getCount(), p);
				} else {
					oppositeDownRightStore.insert(index, p);
				}

				oppositeDownRightGridPanel.getView().refresh();
				oppositeDownRightGridPanel.getSelectionModel().selectLastRow();
			} else {
				for (var i = 0; i < rows; i++) {
					var record = oppositeDownRightStore.recordType;
					var p = new record(virPort);

					if (index == -1) {
						oppositeDownRightStore.insert(oppositeDownRightStore
										.getCount(), p);
					} else {
						oppositeDownRightStore.insert(index, p);
					}

					oppositeDownRightGridPanel.getView().refresh();
					oppositeDownRightGridPanel.getSelectionModel()
							.selectLastRow();
				}
			}
		} else {

			if (virType == 1) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 2) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 3,
					PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 3) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : "",
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 4) {
				virPort = {
					PTP_ID : ids,
					EQUIP_NAME : neNameopposite,
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 2,
					PM_STD_OPT_AMP_ID : "",
					PM_TYPE : direction,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			}

			var select = oppositeDownLeftGridPanel.getSelectionModel()
					.getSelected();
			var index = oppositeDownLeftStore.indexOf(select);
			if (virType != 3) {
				var record = oppositeDownLeftStore.recordType;
				var p = new record(virPort);

				if (index == -1) {
					oppositeDownLeftStore.insert(oppositeDownLeftStore
									.getCount(), p);
				} else {
					oppositeDownLeftStore.insert(index, p);
				}

				oppositeDownLeftGridPanel.getView().refresh();
				oppositeDownLeftGridPanel.getSelectionModel().selectLastRow();
			} else {
				for (var i = 0; i < rows; i++) {
					var record = oppositeDownLeftStore.recordType;
					var p = new record(virPort);

					if (index == -1) {
						oppositeDownLeftStore.insert(oppositeDownLeftStore
										.getCount(), p);
					} else {
						oppositeDownLeftStore.insert(index, p);
					}

					oppositeDownLeftGridPanel.getView().refresh();
					oppositeDownLeftGridPanel.getSelectionModel()
							.selectLastRow();
				}
			}

		}

	}
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
function saveModifyVirtualPortLeftForward(virName, caculateDown, rows,
		direction, ids, type, portType, virType) {
	// ROUTE_TYPE :1.ptp口，2.虚拟端口，3.衰耗器，4.段衰耗，5.光缆，6.其他(空行),7.自定义
	var virPort;
	if (type == 1) {
		isPort = false;
		if (portType == 1) {
			if (virType == 1) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 2) {
				virPort = {
					PTP_ID : "",
					// EQUIP_NAME : neNameForward,
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 3,
					PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 3) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : "",
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 4) {
				virPort = {
					PTP_ID : ids,
					EQUIP_NAME : neNameForward,
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					PM_TYPE : direction,
					PM_STD_OPT_AMP_ID : "",
					ROUTE_TYPE : 2,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			}
			var select = forwardDownLeftGridPanel.getSelectionModel()
					.getSelected();
			var index = forwardDownLeftStore.indexOf(select);

			forwardDownLeftStore.remove(select);
			var record = forwardDownLeftStore.recordType;
			var p = new record(virPort);
			if (index == -1) {
				forwardDownLeftStore.insert(forwardDownLeftStore.getCount(), p);
			} else {
				forwardDownLeftStore.insert(index, p);
			}

			forwardDownLeftGridPanel.getView().refresh();
			forwardDownLeftGridPanel.getSelectionModel().selectRow(index);
		} else {
			if (virType == 1) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 2) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 3,
					SUB_PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 3) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : "",
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			} else if (virType == 4) {
				virPort = {
					SUB_PTP_ID : ids,
					EQUIP_NAME : neNameForward,
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_PM_TYPE : direction,
					SUB_PM_STD_OPT_AMP_ID : "",
					SUB_ROUTE_TYPE : 2,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdForward
				}
			}

			var select = forwardDownRightGridPanel.getSelectionModel()
					.getSelected();
			var index = forwardDownRightStore.indexOf(select);

			forwardDownRightStore.remove(select);
			var record = forwardDownRightStore.recordType;
			var p = new record(virPort);
			if (index == -1) {
				forwardDownRightStore.insert(forwardDownRightStore.getCount(),
						p);
			} else {
				forwardDownRightStore.insert(index, p);
			}

			forwardDownRightGridPanel.getView().refresh();
			forwardDownRightGridPanel.getSelectionModel().selectRow(index);

		}
	} else if (type == 2) {
		isPortopposite = false;
		if (portType == 1) {
			if (virType == 1) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 2) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : virName,
					CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 3,
					PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 3) {
				virPort = {
					PTP_ID : "",
					EQUIP_NAME : "",
					PTP_NAME : "",
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 4) {
				virPort = {
					PTP_ID : ids,
					EQUIP_NAME : neNameopposite,
					PTP_NAME : virName,
					CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					PM_TYPE : direction,
					PM_STD_OPT_AMP_ID : "",
					ROUTE_TYPE : 2,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			}
			var select = oppositeDownLeftGridPanel.getSelectionModel()
					.getSelected();
			var index = oppositeDownLeftStore.indexOf(select);

			oppositeDownLeftStore.remove(select);
			var record = oppositeDownLeftStore.recordType;
			var p = new record(virPort);
			if (index == -1) {
				oppositeDownLeftStore.insert(oppositeDownLeftStore.getCount(),
						p);
			} else {
				oppositeDownLeftStore.insert(index, p);
			}

			oppositeDownLeftGridPanel.getView().refresh();
			oppositeDownLeftGridPanel.getSelectionModel().selectRow(index);
		} else {

			if (virType == 1) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 7,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 2) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : caculateDown,
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 3,
					SUB_PM_TYPE:3,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 3) {
				virPort = {
					SUB_PTP_ID : "",
					EQUIP_NAME : "",
					SUB_PTP_NAME : "",
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_ROUTE_TYPE : 6,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			} else if (virType == 4) {
				virPort = {
					SUB_PTP_ID : ids,
					EQUIP_NAME : neNameopposite,
					SUB_PTP_NAME : virName,
					SUB_CALCULATE_POINT : "",
					MULTI_SEC_ID : mul_id,
					SUB_PM_TYPE : direction,
					SUB_PM_STD_OPT_AMP_ID : "",
					SUB_ROUTE_TYPE : 2,
					MULTI_SECT_NE_ROUTE_ID : neRouteIdopposite
				}
			}

			var select = oppositeDownRightGridPanel.getSelectionModel()
					.getSelected();
			var index = oppositeDownRightStore.indexOf(select);

			oppositeDownRightStore.remove(select);
			var record = oppositeDownRightStore.recordType;
			var p = new record(virPort);
			if (index == -1) {
				oppositeDownRightStore.insert(
						oppositeDownRightStore.getCount(), p);
			} else {
				oppositeDownRightStore.insert(index, p);
			}

			oppositeDownRightGridPanel.getView().refresh();
			oppositeDownRightGridPanel.getSelectionModel().selectRow(index);

		}

	}
}

/**
 * 向上移动左边框的记录
 * 
 * @param {}
 *            forwardDownLeftGridPanel
 * @param {}
 *            forwardDownLeftStore
 */
function upLeftForward(forwardDownLeftGridPanel, forwardDownLeftStore) {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardDownLeftGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
	isPort = false;
	var index = forwardDownLeftStore.indexOf(record);
	if (index == 0) {
		return;
	}
	forwardDownLeftStore.remove(record);
	forwardDownLeftStore.insert(index - 1, record);
	forwardDownLeftGridPanel.getView().refresh();
	forwardDownLeftGridPanel.getSelectionModel().selectRow(index - 1);
}

/**
 * 向下移动左边框的记录
 * 
 * @param {}
 *            forwardDownLeftGridPanel
 * @param {}
 *            forwardDownLeftStore
 */
function downLeftForward(forwardDownLeftGridPanel, forwardDownLeftStore) {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardDownLeftGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
	isPort = false;
	var index = forwardDownLeftStore.indexOf(record);
	if (index == forwardDownLeftStore.getCount() - 1) {
		return;
	}
	forwardDownLeftStore.remove(record);
	forwardDownLeftStore.insert(index + 1, record);
	forwardDownLeftGridPanel.getView().refresh();
	forwardDownLeftGridPanel.getSelectionModel().selectRow(index + 1);
}

/**
 * 删除左边框的记录
 */
function deleteLeftForward() {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardDownLeftGridPanel.getSelectionModel().getSelections();
	if (record.length > 0) {
		isPort = false;
		for (var i = 0; i < record.length; i++) {
			forwardDownLeftStore.remove(record[i]);
		}

	} else {
		Ext.Msg.alert('信息', '请选择要删除的记录！');
		return;
	}
}

/**
 * 新增虚拟端口 1. 主 2 备 type 1 正向 2 反向
 */
function addVirtualPortLeftForward(portType, type) {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardUpPanel.getSelectionModel().getSelected();
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

/**
 * 
 * @param {}
 *            portType 1 主 2 备
 * @param {}
 *            type 1 正向 2 反向
 */
function modifyVirtualPortLeftForward(portType, type) {
	if (type == 1) {
		if (!isSave) {
			Ext.Msg.alert('信息', '请先保存网元修改信息！');
			return;
		}
	} else {
		if (!isSaveopposite) {
			Ext.Msg.alert('信息', '请先保存网元修改信息！');
			return;
		}
	}
	var neId = "";
	if (type == 1) {
		neId = neIdForward;
	} else if (type == 2) {
		neId = neIdopposite;
	}
	// 不能多选，也不能为空
	var select = "";
	if (portType == 1 && type == 1) {
		select = forwardDownLeftGridPanel.getSelectionModel().getSelections();
	} else if (portType == 2 && type == 1) {
		select = forwardDownRightGridPanel.getSelectionModel().getSelections();
	} else if (portType == 1 && type == 2) {
		select = oppositeDownLeftGridPanel.getSelectionModel().getSelections();
	} else if (portType == 2 && type == 2) {
		select = oppositeDownRightGridPanel.getSelectionModel().getSelections();
	}
	if (select.length < 1) {
		Ext.Msg.alert('提示', '请选择需要修改的项！');
		return;
	} else {
		var routeType = "";
		var virName = "";
		var caculateDown = "";
		var ids = "";
		if (portType == 1) {
			routeType = select[0].get("ROUTE_TYPE");
			virName = select[0].get("PTP_NAME");
			caculateDown = select[0].get("CALCULATE_POINT");
			ids = select[0].get("PTP_ID");
		} else {
			routeType = select[0].get("SUB_ROUTE_TYPE");
			virName = select[0].get("SUB_PTP_NAME");
			caculateDown = select[0].get("SUB_CALCULATE_POINT");
			ids = select[0].get("SUB_PTP_ID");
		}
		var name = virName.split("(虚拟)")[0]
		if (routeType == 2 || routeType == 3 || routeType == 7) {
			var virType;
			if (routeType == 2) {
				virType = 4;
			} else if (routeType == 3) {
				virType = 2;
			} else if (routeType == 7) {
				virType = 1;
			}
			var url = "modifyVirPort.jsp?virType=" + virType + "&virName="
					+ name + "&caculateDown=" + caculateDown + "&ids=" + ids
					+ "&portType=" + portType + "&type=" + type + "&neId="
					+ neId;
			modifyVirtualPortLeftForwardWindow = new Ext.Window({
				id : 'modifyVirtualPortLeftForwardWindow',
				name : 'modifyVirtualPortLeftForwardWindow',
				title : '修改虚拟端口',
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
				html : '<iframe id = "modifyVirtualPortLeftForwardWindow" name = "modifyVirtualPortLeftForwardWindow" src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'

			});
			modifyVirtualPortLeftForwardWindow.show();
			// 调节高度
			if (modifyVirtualPortLeftForwardWindow.getHeight() > Ext
					.getCmp('win').getHeight()) {
				modifyVirtualPortLeftForwardWindow.setHeight(Ext.getCmp('win')
						.getHeight()
						* 0.7);
			} else {
				modifyVirtualPortLeftForwardWindow
						.setHeight(modifyVirtualPortLeftForwardWindow
								.getInnerHeight());
			}
			// 调节宽度
			if (modifyVirtualPortLeftForwardWindow.getWidth() > Ext
					.getCmp('win').getWidth()) {
				modifyVirtualPortLeftForwardWindow.setWidth(Ext.getCmp('win')
						.getWidth()
						* 0.7);
			} else {
				modifyVirtualPortLeftForwardWindow
						.setWidth(modifyVirtualPortLeftForwardWindow
								.getInnerWidth());
			}
			modifyVirtualPortLeftForwardWindow.center();

		} else {
			Ext.Msg.alert('提示', '该行不能修改！');
			return;
		}
	}

}
// 备用端口信息
var jsonString = new Array();
var map = {
	"limit" : 200
};
jsonString.push(map);

var forwardDownRightStore = new Ext.data.Store({
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
// forwardDownRightStore.load();
// ************************* 任务信息列模型 ****************************
var forwardDownRightCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel(
		{
			singleSelect : true,
			header : ""
		});
var forwardDownRightCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [ forwardDownRightCheckboxSelectionModel, {
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
			},  {
				id : 'SUB_EQUIP_TYPE',
				header : 'SUB_EQUIP_TYPE',
				dataIndex : 'SUB_EQUIP_TYPE',
				hidden : true
			},{
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
							id : "modeTypeRight",
							name : "modeTypeRight",
							mode : "local",
							displayField : "MODEL",
							valueField : 'PM_STD_OPT_AMP_ID',
							store : modelTypeStore,
							triggerAction : 'all',
							editable : false,
							resizable: true,
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
							id : "pmTypeRight",
							name : "pmTypeRight",
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
				tooltip:'可编辑列',
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

var forwardDownRightPageTool = new Ext.PagingToolbar({
			id : 'forwardDownRightPageTool',
			pageSize : 200,// 每页显示的记录值
			store : forwardDownRightStore,
			displayInfo : true,
			displayMsg : '当前 {0} - {1} ，总数 {2}',
			emptyMsg : "没有记录"
		});

var forwardDownRightGridPanel = new Ext.grid.EditorGridPanel({
	id : "forwardDownRightGridPanel",
	flex : 5,
	// title:'任务信息列表',
	cm : forwardDownRightCm,
	store : forwardDownRightStore,
	// collapsed: true, // initially collapse the group
	// collapsible: true,
	// autoHeight:true,
	// autoExpandColumn: 'experimentType', // column with this id will
	// be expanded
	// collapsed: false, // initially collapse the group
	// collapsible: true,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : forwardDownRightCheckboxSelectionModel, // 必须加不然不能选checkbox
	bbar : forwardDownRightPageTool,
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
							addPortRightForward(2, 4);
						}

					}, {
						text : '新增虚拟端口',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							addVirtualPortRightForward(2, 1);
						}

					}, {
						text : '修改虚拟端口',
						icon : '../../../resource/images/btnImages/modify.png',
						handler : function() {
							modifyVirtualPortRightForward(2, 1);
						}

					}, {
						text : '新增光缆',
						icon : '../../../resource/images/btnImages/add.png',
						handler : function() {
							addRightForwardFiber(2, 1);

						}

					}, '-',{
						text : '上移',
						icon : '../../../resource/images/btnImages/up.png',
						handler : function() {
							upRightForward(forwardDownRightGridPanel,
									forwardDownRightStore);
						}

					}, {
						text : '下移',
						icon : '../../../resource/images/btnImages/down.png',
						handler : function() {
							downRightForward(forwardDownRightGridPanel,
									forwardDownRightStore);
						}

					}, '-',{
						text : '删除',
						icon : '../../../resource/images/btnImages/delete.png',
						handler : function() {
							deleteRightForward();
						}

					}, '-',{
						text : '基准值生成',
						icon : '../../../resource/images/btnImages/set_baseline.png',
						handler : function() {
							newStandLeftForward(4);
						}

					}]
		}]

	}
});

/**
 * 调用左边的树
 */
function addPortRightForward(portType, type) {
	addPortLeftForward(portType, type);
}

/**
 * 备用端口新增虚拟端口
 */
function addVirtualPortRightForward(portType, type) {
	addVirtualPortLeftForward(portType, type);
}

/**
 * 修改备用虚拟端口
 */
function modifyVirtualPortRightForward(portType, type) {
	modifyVirtualPortLeftForward(portType, type);
}

/**
 * 新增备用光纤 1.左 2.右
 */
function addRightForwardFiber(portType, type) {
	addLeftForwardFiber(portType, type);
}

function upRightForward(forwardDownRightGridPanel, forwardDownRightStore) {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	
	var record = forwardDownRightGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
	isPort = false;
	var index = forwardDownRightStore.indexOf(record);
	if (index == 0) {
		return;
	}
	forwardDownRightStore.remove(record);
	forwardDownRightStore.insert(index - 1, record);
	forwardDownRightGridPanel.getView().refresh();
	forwardDownRightGridPanel.getSelectionModel().selectRow(index - 1);
}

function downRightForward(forwardDownRightGridPanel, forwardDownRightStore) {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardDownRightGridPanel.getSelectionModel().getSelected();
	if (!record) {
		Ext.Msg.alert('信息', '请选择要移动的记录！');
		return;
	}
	isPort = false;
	var index = forwardDownRightStore.indexOf(record);
	if (index == forwardDownRightStore.getCount() - 1) {
		return;
	}
	forwardDownRightStore.remove(record);
	forwardDownRightStore.insert(index + 1, record);
	forwardDownRightGridPanel.getView().refresh();
	forwardDownRightGridPanel.getSelectionModel().selectRow(index + 1);
}

function deleteRightForward() {
	if (!isSave) {
		Ext.Msg.alert('信息', '请先保存网元修改信息！');
		return;
	}
	var record = forwardDownRightGridPanel.getSelectionModel().getSelections();
	if (record.length > 0) {
		isPort = false;
		for (var i = 0; i < record.length; i++) {
			forwardDownRightStore.remove(record[i]);
		}

	} else {
		Ext.Msg.alert('信息', '请选择要删除的记录！');
		return;
	}

}
// 正向组装panel 开始
// 最下面的两个store
var forwardDownGridPanel = new Ext.Panel({
			id : "forwardDownGridPanel",
			region : "center",
			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			flex : 3,
			items : [forwardDownLeftGridPanel, forwardDownRightGridPanel]
		});
// store和标题组装
var forwardDownPanel = new Ext.Panel({
			id : "forwardDownPanel",
			region : "center",
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			flex : 6,
			items : [forwardDownTitleGridPanel, forwardDownGridPanel]
		});
// 上下组装
var forwardGrid = new Ext.Panel({
			id : "forwardGrid",
			region : "center",
			layout : {
				type : 'vbox',
				align : 'stretch'
			},

			items : [forwardUpPanel, forwardDownPanel]
		});

// 反向
// var oppositeGrid = new Ext.Panel({
// id : "oppositeGrid",
// region : "center",
// items : [forwardUpPanel, forwardDownPanel]
//
// });
var tab = new Ext.TabPanel({
			id : 'tabs1',
			anchor : "right 100%",
			region : "center",
			activeTab : 0,
			// plugins: new Ext.ux.TabCloseMenu(),
			deferredRender : false,
			items : [{
						title : '正向',
						layout : 'fit',
						id : 'forward',
						items : [forwardGrid]
					}, {
						title : '反向',
						id : 'opposite',
						layout : 'fit',
						items : [oppositeGrid]
					}]
		})
var formPanel = new Ext.FormPanel({
			id : 'formPanel',
			region : "center",
			border : false,
			frame : false,
			autoScroll : true,
			// labelWidth : 120,
			// layout:'border',
			// width : 200,
			bodyStyle : 'padding:10px 10px 0;',
			items : [tab]

		});
forwardDownRightStore.on("load", function(s) {
			s.each(function(record) {
						if (record.get('SUB_PTP_ID') == -1) {
							s.remove(record);
						}
					})
		})
function init() {
	// 将反向tab灰掉
	if (direction == 1) {
		Ext.getCmp("opposite").setDisabled(true);
	}
}
Ext.onReady(function() {
	Ext.Msg = parent.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}
	Ext.QuickTips.init(); // 开启悬停提示
	Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
	var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
(function() {

		forwardUpStore.load({
					callback : function(r, options, success) {
						if (success) {
							forwardUpPanel.getSelectionModel().selectFirstRow();
							if (forwardUpPanel.getView().getRow(0)) {
								forwardUpPanel.getView().getRow(0).click();
							}
						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
		oppositeUpStore.load({
					callback : function(r, options, success) {
						if (success) {
							oppositeUpPanel.getSelectionModel()
									.selectFirstRow();
							if (oppositeUpPanel.getView().getRow(0)) {
								oppositeUpPanel.getView().getRow(0).click();
							}
						} else {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
				});
	}).defer(1500);

	init();
});