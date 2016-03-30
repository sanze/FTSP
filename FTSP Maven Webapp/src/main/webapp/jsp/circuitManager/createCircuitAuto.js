/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

var jsonString = new Array();
var map = {
	"userId" : userId
};
jsonString.push(map);

var emsStore = new Ext.data.Store({
			url : 'circuit!getAllGroup.action',
			baseParams : {
				"jsonString" : Ext.encode(jsonString)
			},
			reader : new Ext.data.JsonReader({

			}		, ["BASE_EMS_GROUP_ID", "GROUP_NAME"])
		});
emsStore.load({
			callback : function(r, options, success) {
				if (success) {

				} else {
					Ext.Msg.alert('错误', '加载失败！');
				}
			}
		});

var store = new Ext.data.Store({
			url : 'circuit!getAllEMSTask.action',
			baseParams : {
				"userId" : userId,
				"emsGroupId" : -99,
				"limit" : 200
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["GROUP_NAME", "DISPLAY_NAME", "TASK_STATUS",
							"END_TIME", "NEXT_TIME", "PERIOD_TYPE", "PERIOD",
							"SYS_TASK_ID", "RESULT"

					])
		});

Ext.Ajax.request({
			url : 'circuit!getAllEMSTask.action',
			method : 'POST',
			params : {
				"userId" : userId,
				"emsGroupId" : -99,
				"limit" : 200
			},

			success : function(response) {// 回调函数

				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 0) {
					Ext.Msg.alert("提示", obj.returnMessage);

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
// store.load();
function getProcessPersent() {
	// 遍历store,如果显示状态执行中，则查询进度
	store.each(function(record) {
				if (record.get('RESULT') == 3) {
					var url = 'common!getProcessPercent.action?processKey='
							+ record.get('SYS_TASK_ID') + '&t=' + new Date();
					Ext.Ajax.request({
								url : url,
								method : 'POST',
								// 处理ajax的返回数据
								success : function(response, options) {
									alert(obj.text);
									var obj = Ext.decode(response.responseText);
									if (processPercent >= 1) {
										record.set("RESULT", 1);
									} else {
										record.set("RESULT", "执行中(" + obj.text
														+ ")");
									}
								},
								failure : function() {
									Ext.Msg.alert('错误', '获取进度发生错误了！');
								}
							});
				}

			});

}
// ************************* 任务信息列模型 ****************************
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
		// columns are not sortable by default
	},
	columns : [new Ext.grid.RowNumberer({
						width : 26
					}), checkboxSelectionModel, {
				id : 'SYS_TASK_ID',
				header : '任务id',
				dataIndex : 'SYS_TASK_ID',
				width : 150,
				hidden : true
			}, {
				id : 'GROUP_NAME',
				header : '分组名称',
				dataIndex : 'GROUP_NAME',
				width : 100
			}, {
				id : 'DISPLAY_NAME',
				header : '网管名',
				dataIndex : 'DISPLAY_NAME',
				width : 100
			}, {
				id : 'cycle',
				header : '任务周期',
				dataIndex : 'cycle',
				width : 150,
				renderer : function(v, r, t) {
					// 0,0,1,0，0:9:00 年，季，月，周，日，9：00
					var vv = t.get("PERIOD_TYPE");
					var vt = t.get("PERIOD").split(",");
					var time = t.get("");
					var ret;
					if (vv == 2) { // 每周
						ret = "每周 ";
						if (vt[3] == 2) {
							ret += "周一 ";
						} else if (vt[3] == 3) {
							ret += "周二 ";
						} else if (vt[3] == 4) {
							ret += "周三 ";
						} else if (vt[3] == 5) {
							ret += "周四 ";
						} else if (vt[3] == 6) {
							ret += "周五 ";
						} else if (vt[3] == 7) {
							ret += "周六 ";
						} else if (vt[3] == 1) {
							ret += "周日 ";
						}
						ret += vt[5];
					} else if (vv == 3) { // if(vv == 3)
						ret = "每月 ";

						ret += vt[4] + "号 " + vt[5];

					}
					return "<div><div  style='float:left'>"
							+ ret
							+ "</div><div style='float:right'><img "
							+ "src='../../resource/images/btnImages/modify.png' title='编辑' "
							+ "onclick ='addCycle("+t.get("TASK_STATUS")+")' /></div></div>";
				}

			}, {
				id : 'TASK_STATUS',
				header : '任务状态',
				dataIndex : 'TASK_STATUS',
				width : 100,
				renderer : function(v) {
					if (v == 1) {
						return "启用";
					} else if (v == 2) {
						return "挂起";
					} else {
						return v;
					}
				}
			}, {
				id : 'END_TIME',
				header : '上次结束时间',
				dataIndex : 'END_TIME',
				width : 150
			}, {
				id : 'NEXT_TIME',
				header : '下次开始时间',
				dataIndex : 'NEXT_TIME',
				width : 150,
				renderer : function(v, r, t) {
					if (t.get("TASK_STATUS") == 2) {
						return "-";
					} else {
						return v;
					}
				}
			}, {
				id : 'RESULT',
				header : '执行状态',
				dataIndex : 'RESULT',
				width : 100,
				renderer : function(v, r, t) {
					// 执行成功 2.执行失败 3.执行中 4.执行中止
					if (v == 1) {
						return "执行成功 ";
					} else if (v == 2) {
						return "执行失败";
					} else if (v == 3) {
						return "执行中";
					} else if (v == 4) {
						return "执行中止";
					} else {
						return v;
					}
				}
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
			stripeRows : true, // 交替行效果
			loadMask : true,
			selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
			forceFit : true,
			// tbar: pageTool,
//			viewConfig : {
//				forceFit : true
//			},
			bbar : pageTool, 
			tbar : ['-','网管分组：', emsGroupCombo,'-', {
						text : '启用',
						privilege : actionAuth,
						icon : '../../resource/images/btnImages/control_play.png',
						handler : qiyong

					}, '-',{
						text : '挂起',
						privilege : actionAuth,
						icon : '../../resource/images/btnImages/control_stop.png',
						handler : holdOn
					}]
		});

function addCycle(type) {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
		if(type==1){
				Ext.Msg.alert('提示', '不能修改启用中的任务，请先将任务挂起！');
				return;
		}
		var url = "addCycle.jsp?period_type=" + cell[0].get("PERIOD_TYPE")
				+ "&period=" + cell[0].get("PERIOD") + "&nexttime="
				+ cell[0].get("NEXT_TIME").replace(" ", ",") + "&task_id="
				+ cell[0].get("SYS_TASK_ID");
		addCycleWindow = new Ext.Window({
					id : 'addCycleWindow',
					title : '周期设置',
					width : 400,
					height : 300,
					isTopContainer : true,
					modal : true,
					plain : true, // 是否为透明背景
					html : '<iframe src='
							+ url
							+ ' height="100%" width="100%" frameborder=0 border=0/>'
				});
		addCycleWindow.show();
	} else {
		Ext.Msg.alert('提示', '周期设置记录不能为空');
	}

}

function qiyong() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		Ext.Msg.confirm('提示', '确认启用选择的任务？', function(button) {
			if (button == 'yes') {
				for (var i = 0; i < cell.length; i++) {
					var map = {
						"SYS_TASK_ID" : cell[i].get('SYS_TASK_ID'),
						"TASK_STATUS" : 1,
						"PERIOD_TYPE" : cell[i].get("PERIOD_TYPE"),
						"PERIOD" : cell[i].get("PERIOD")
					};
					jsonString.push(map);
				}
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				store.commitChanges();
				Ext.Ajax.request({
							url : 'circuit!setCircuitTaskOn.action',
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
		Ext.Msg.alert('信息', '请选择要启用的网管！');
	}

}
// 挂起
function holdOn() {
	var jsonString = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) {
		Ext.Msg.confirm('提示', '确认挂起选择的任务？', function(button) {
			if (button == 'yes') {
				for (var i = 0; i < cell.length; i++) {
					var map = {
						"SYS_TASK_ID" : cell[i].get('SYS_TASK_ID'),
						"TASK_STATUS" : 2,
						"PERIOD_TYPE" : cell[i].get("PERIOD_TYPE"),
						"PERIOD" : cell[i].get("PERIOD"),
						"DISPLAY_NAME" : cell[i].get("DISPLAY_NAME")
					};
					jsonString.push(map);
				}
				var jsonData = {
					"jsonString" : Ext.encode(jsonString)
				};
				store.commitChanges();
				Ext.Ajax.request({
					url : 'circuit!checkTask.action',
					method : 'POST',
					params : jsonData,

					success : function(response) {// 回调函数

						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage
											+ "选择的任务正在执行中，确认终止执行并挂起任务？",
									function(r) {
										Ext.Ajax.request({
											url : 'circuit!setCircuitTaskHold.action',
											method : 'POST',
											params : jsonData,

											success : function(response) {// 回调函数

												var obj = Ext
														.decode(response.responseText);
												if (obj.returnResult == 1) {
													Ext.Msg.alert("提示",
															obj.returnMessage,
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
													Ext.Msg.alert("提示",
															obj.returnMessage);
												}

											},
											error : function(response) {
												Ext.Msg.alert('错误', '保存失败！');
											},
											failure : function(response) {
												Ext.Msg.alert('错误', '保存失败！');
											}

										});
									});
						}
						if (obj.returnResult == 1) {
							Ext.Ajax.request({
								url : 'circuit!setCircuitTaskHold.action',
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
		Ext.Msg.alert('信息', '请选择要挂起的网管！');
	}
}

Ext.onReady(function() {

	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	// Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 90000000;

	var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [gridPanel]
			});
		// win.show();
	});
