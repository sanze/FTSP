//新增割接任务
function addTask() {
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓复用段信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	var storeI = new Ext.data.Store({
		url : 'pm-report!searchMS.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "trunkLineName", "MSName", "direction", "MSId" ])
	});
	var smI = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	var cmI = new Ext.grid.ColumnModel({
		defaults : {
			sortable : true
		},
		columns : [ new Ext.grid.RowNumberer({
			width : 26
		}), smI, {
			id : 'trunkLineName',
			header : '干线名称',
			dataIndex : 'trunkLineName',
			width : 100
		}, {
			id : 'MSName',
			header : '复用段名称',
			dataIndex : 'MSName',
			width : 150
		}, {
			id : 'direction',
			header : '方向',
			dataIndex : 'direction',
			width : 150,
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
			id : 'MSId',
			header : 'MSId',
			dataIndex : 'MSId',
			hidden : true,
			width : 150
		} ]
	});
	
	
	// @@@@@@@@@@@@@@@@@@
	// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching:false，
	// 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
	var emsGroupStore = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : 'common!getAllEmsGroups.action', 
			disableCaching : false // 是否禁用缓存，设置false禁用默认的参数_dc
		}),
		// 设置默认值
		baseParams : {
			"displayAll" : true,
			"displayNone" : true
		},
		// record格式
		reader : new Ext.data.JsonReader({
			root : 'rows',// json数据的key值
			fields : [ 'BASE_EMS_GROUP_ID', 'GROUP_NAME' ]
		})
	});

	var emsGroupCombo = new Ext.form.ComboBox({
		id : 'emsGroupCombo',
		fieldLabel : '网管分组',
		emptyText : '网管分组',
		store : emsGroupStore,
		valueField : 'BASE_EMS_GROUP_ID',
		displayField : 'GROUP_NAME',
		editable : false,
		triggerAction : 'all',
		width : 100,
		resizable: true,
		listeners : {
			select : function(combo, record, index) {
				var emsGroupId = combo.getValue();
				if (emsGroupId) {
					emsCombo.enable();
					// 还原网管下拉框
					emsCombo.reset();
					// 动态改变网管数据源的参数
					emsStore.baseParams.emsGroupId = emsGroupId;
					emsStore.baseParams.displayAll = false;
					// emsStore.baseParams = {'emsGroupId':emsGroupId};
					// // 加载网管数据源
					emsStore.load({
						callback : function(records, options, success) {
							if (!success) {
								Ext.Msg.alert('错误', '查询失败！');
							} else {
								// // 获取下拉框的第一条记录
								// var firstValue =
								// records[0].get('BASE_EMS_CONNECTION_ID');
								// //
								// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
								// Ext.getCmp('emsCombo').setValue(firstValue);
							}
						}
					});
				} else {
					emsCombo.disable();
				}

			}
		}
	});

	/**
	 * 创建网管数据源
	 */
	var emsStore = new Ext.data.Store({
		url : 'common!getAllEmsByEmsGroupId.action',
		baseParams : {},
		reader : new Ext.data.JsonReader({
			root : 'rows',
			fields : [ 'BASE_EMS_CONNECTION_ID', 'DISPLAY_NAME' ]
		})
	});

	/**
	 * 创建网管下拉框
	 */
	var emsCombo = new Ext.form.ComboBox({
		id : 'emsCombo',
		fieldLabel : '网管',
		store : emsStore,
		valueField : 'BASE_EMS_CONNECTION_ID',
		displayField : 'DISPLAY_NAME',
		emptyText : '网管',
		editable : false,
		triggerAction : 'all',
		disabled : true,
		width : 100,
		resizable: true,
		listeners : {
			select : function(combo, record, index) {
				var emsId = combo.getValue();
				if (emsId) {
					trunkLineCombo.enable();
					trunkLineCombo.reset();
					trunkLineStore.baseParams = {
						'searchCond.emsId' : combo.getValue()
					};
					trunkLineStore.load();
				} else {
					neCombo.disable();
				}
			}
		}
	});
	// @@@@@@@@@@@@@@@@@@
	// -----------------------------------------------------------------
	var trunkLineStore = new Ext.data.Store({
		url : 'pm-report!getTrunkLine.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "trunkLineId", "trunkLineName" ])
	});
	// trunkLineStore.load();
	var trunkLineCombo = new Ext.form.ComboBox({
		id : 'trunkLineCombo',
		store : trunkLineStore,
		mode : 'local',
		emptyText : '干线',
		disabled : true,
		width : 100,
		triggerAction : 'all',
		editable : false,
		valueField : 'trunkLineId',
		displayField : 'trunkLineName',
		resizable: true,
		editable : true
	});
	// ----------------------------------------------------------------

	var gridPanelI = new Ext.grid.GridPanel({
		id : 'gridPanelI',
		cm : cmI,
		height : 220,
		title : '复用段信息',
		store : storeI,
		region : 'north',
		selModel : smI,
		stripeRows : true,
		tbar : [ emsGroupCombo,'<br/>', emsCombo, '<br/>', trunkLineCombo, {
			text : '查询',
			icon : '../../../resource/images/btnImages/search.png',
			handler : search
		}, '-', '->', {
			text : '新增',
			icon : '../../../resource/images/btnImages/add.png',
			handler : add
		} ]
	});
	// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑复用段信息结束↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓复用段信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	var storeII = new Ext.data.Store({
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "trunkLineName", "MSName", "direction", "MSId" ])
	});
	var smII = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	var cmII = new Ext.grid.ColumnModel({
		defaults : {
			sortable : true
		},
		columns : [ new Ext.grid.RowNumberer(), smII, {
			id : 'trunkLineName',
			header : '干线名称',
			dataIndex : 'trunkLineName',
			width : 100
		}, {
			id : 'MSName',
			header : '复用段名称',
			dataIndex : 'MSName',
			width : 150
		}, {
			id : 'direction',
			header : '方向',
			dataIndex : 'direction',
			width : 150,
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
			id : 'MSId',
			header : 'MSId',
			dataIndex : 'MSId',
			hidden : true,
			width : 150
		} ]
	});
	var gridPanelII = new Ext.grid.GridPanel({
		id : 'gridPanelII',
		title : '割接复用段信息',
		cm : cmII,
		store : storeII,
		region : 'center',
		selModel : smII,
		stripeRows : true,
		tbar : [ '->', {
			text : '删除',
			icon : '../../../resource/images/btnImages/delete.png',
			handler : del
		}, {
			text : '清空',
			icon : '../../../resource/images/btnImages/arrow_undo.png',
			handler : function() {
				storeII.removeAll();
			}
		} ]
	});
	// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑复用段信息结束↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓列一↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	var colI = {
		layout : 'border',
		region : 'center',
		border : false,
		items : [ gridPanelI, gridPanelII ]
	};
	// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑列一结束↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓列二↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	var colII = new Ext.form.FormPanel({
		id : 'formPanel',
		title : '信息设置',
		// border : false,
		width : 300,
		padding : '20',
		labelWidth : 80,
		region : 'east',
		items : [ {
			xtype : 'textfield',
			id : 'taskName',
			fieldLabel : '割接任务名称',
			width : 150,
			sideText : '<font color=red>*</font>',
			allowBlank : false
		}, {
			xtype : 'textarea',
			id : 'description',
			width : 150,
			fieldLabel : '描述',
			height : 50
		}, {
			xtype : 'textfield',
			id : 'start',
			name : 'start',
			allowBlank : false,
			fieldLabel : '开始时间',
			sideText : '<font color=red>*</font>',
			width : 150,
			cls : 'Wdate',
			listeners : {
				'focus' : function() {
					WdatePicker({
						el : "start",
						isShowClear : false,
						readOnly : true,
						dateFmt : 'yyyy-MM-dd',
						autoPickDate : true,
						maxDate:  '#F{$dp.$D(\'end\',{d:0})}'
					// maxDate : '%y-%M-%d'
					});
					this.blur();
				}
			}
		}, {
			xtype : 'textfield',
			id : 'end',
			name : 'end',
			fieldLabel : '结束时间',
			sideText : '<font color=red>*</font>',
			allowBlank : false,
			width : 150,
			cls : 'Wdate',
			listeners : {
				'focus' : function() {
					WdatePicker({
						el : "end",
						isShowClear : false,
						readOnly : true,
						dateFmt : 'yyyy-MM-dd',
						autoPickDate : true,
						minDate : '#F{$dp.$D(\'start\',{d:0})}'
					});
					this.blur();
				}
			}
		} ]
	});
	// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑列二结束↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 查找复用段信息
	function search() {
		var emsGroupId = Ext.getCmp('emsGroupCombo').getValue();
		var emsId = Ext.getCmp('emsCombo').getValue();
		var trunkLine = Ext.getCmp('trunkLineCombo').getValue();
		if(emsId==''){
			Ext.Msg.alert('提示','请选择网管！');
			return;
		}
		var params = {
			'searchCond.emsId' : emsId,
			'searchCond.trunkLineId' : trunkLine == '' ? 0 : trunkLine
		};
		storeI.baseParams = params;
		storeI.load({
			callback : function(r, scope, success) {
				if (!success)
					Ext.Msg.alert('提示', '查询复用段失败！');
			}
		});
	}
	// 添加到已选
	function add() {
		var selected = gridPanelI.getSelectionModel().getSelections();
		if (selected && selected.length > 0) {
			for ( var i = 0; i < selected.length; i++) {
				// storeI.remove(selected[i]);
				if (storeII.indexOf(selected[i]) == -1)
					storeII.add(selected[i]);
			}
		}
	}
	// 从已选删除
	function del() {
		var selected = gridPanelII.getSelectionModel().getSelections();
		if (selected && selected.length > 0) {
			for ( var i = 0; i < selected.length; i++) {
				storeII.remove(selected[i]);
			}
		}
	}
	// 保存任务
	function saveTask() {
		if (storeII.getCount() > 0) {
			if (colII.getForm().isValid()) {
				var taskName = Ext.getCmp('taskName').getValue();
				var description = Ext.getCmp('description').getValue();
				var startTime = Ext.getCmp('start').getValue();
				var endTime = Ext.getCmp('end').getValue();
				var MSIdArray = new Array();
				storeII.each(function(rec) {
					MSIdArray.push(rec.get('MSId'));
				});
				var params = {
					'searchCond.taskName' : taskName,
					'searchCond.description' : description,
					'searchCond.startTime' : startTime,
					'searchCond.endTime' : endTime,
					'condList' : MSIdArray,
					'taskTypes' : [TASK_TYPE]
				};
				// 检查是否重复名称
				Ext.Ajax
						.request({
							url : 'ms-cutover!checkTaskNameDuplicate.action',
							method : 'POST',
							params : params,
							success : function(response) {
								var obj = Ext.decode(response.responseText);
								if (obj.returnResult == 1) {
									Ext.Ajax
											.request({
												url : 'ms-cutover!saveMSCutoverTask.action',
												type : 'post',
												params : params,
												success : function(response) {
													var obj = Ext.decode(response.responseText);
													if (obj.returnResult == 1) {
														Ext.Msg.alert("提示",obj.returnMessage);
														loadTaskNameCombo();
														var pageTool = Ext
																.getCmp('pageTool');
														if (pageTool) {
															pageTool
																	.doLoad(pageTool.cursor);
														}
														win.close();
													} else if (obj.returnResult == 0)
														Ext.Msg.alert("提示",obj.returnMessage);
												},
												failure : function(response) {
													Ext.Msg
															.alert("提示",obj.returnMessage);
												}
											});
								} else if (obj.returnResult == 0)
									Ext.Msg.alert("提示", obj.returnMessage);
							},
							failure : function(response) {
								Ext.Msg.alert("提示", obj.returnMessage);
							}
						});
			} else {
				var taskName = Ext.getCmp('taskName').getValue();
				var startTime = Ext.getCmp('start').getValue();
				var endTime = Ext.getCmp('end').getValue();
				if (taskName == '' || taskName == null) {
					Ext.Msg.alert("提示", "请输入割接任务名称！");
				} else if (startTime == '' || endTime == '' || endTime == null
						|| startTime == null) {
					Ext.Msg.alert("提示", "请输入割接任务时间！");
				}
			}
		} else {
			Ext.Msg.alert("提示", "请添加复用段！");
		}
	}
	var panel = new Ext.Panel({
		height : 450,
		// width : 800,
		layout : 'border',
		border : false,
		items : [ colI, colII ]
	});
	var win = new Ext.Window({
		id : 'addTaskWin',
		title : '复用段割接任务设置',
		layout : 'form',
		height : 500,
		width : 800,
		autoScroll : true,
		items : [ panel ],
		buttons : [ {
			text : '确定',
			handler : saveTask
		}, {
			text : '取消',
			handler : function() {
				win.close();
			}
		} ]
	});
	win.show();
	if (win.getHeight() > Ext.getCmp('win').getHeight()) {
		win.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		panel.setHeight(win.getInnerHeight());
	}
	win.center();
	// win.doLayout();
	// alert(win.getHeight());
	// alert(Ext.getCmp('win').getHeight());

}