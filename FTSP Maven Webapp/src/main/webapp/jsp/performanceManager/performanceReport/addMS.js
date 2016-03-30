//添加复用段
function addMS() {
//	grid.reconfigure(storeMulti,cmMulti);
	grid.getColumnModel().setHidden(3,true);
	grid.getColumnModel().setHidden(4,false);
	grid.getColumnModel().setHidden(5,false);
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓复用段信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	var store = new Ext.data.Store({
		url : 'pm-report!searchMS.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "trunkLineName", "MSName", "direction", "MSId" ])
	});
	var sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	var cm = new Ext.grid.ColumnModel({
		defaults : {
			sortable : true
		},
		columns : [ new Ext.grid.RowNumberer({
			width : 26
		}), sm, {
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
			renderer:directionRenderer
		}, {
			id : 'MSId',
			header : 'MSId',
			dataIndex : 'MSId',
			hidden : true,
			width : 150
		} ]
	});
	
	//@@@@@@@@@@@@@@@@@@
	var emsGroupStore = new Ext.data.Store({
		// 获取数据源地址
		proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
			url : 'common!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
			disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
		}),
		//设置默认值
		baseParams : {"displayAll" : true,"displayNone" : true},
		// record格式
		reader : new Ext.data.JsonReader({
			root : 'rows',//json数据的key值
			fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
		})
	});
	
	var emsGroupCombo = new Ext.form.ComboBox({
		id : 'emsGroupCombo',
		fieldLabel : '网管分组',
		store : emsGroupStore,// 数据源
		valueField : 'BASE_EMS_GROUP_ID',// 下拉框实际值
		displayField : 'GROUP_NAME',// 下拉框显示值
		editable : false,
		triggerAction : 'all',// 每次加载所有值，否则下拉框选择一个值后，再点击就只有一个值
		width :150,
		resizable: true,
		listeners : {// 监听事件
			select : function(combo, record, index) {
				var emsGroupId = combo.getValue();
				if(emsGroupId){
					emsCombo.enable();
				// 还原网管下拉框
					emsCombo.reset();
				// 动态改变网管数据源的参数
					emsStore.baseParams.emsGroupId = emsGroupId;
					emsStore.baseParams.displayAll = false;
//					emsStore.baseParams = {'emsGroupId':emsGroupId};
//					// 加载网管数据源
				emsStore.load({
					callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
						if (!success) {
								Ext.Msg.alert('错误', '查询失败！');
						}else{
//							// 获取下拉框的第一条记录
//							var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//							// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
//							Ext.getCmp('emsCombo').setValue(firstValue);
						}
					}
				});
				}else{
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
			fields : ['BASE_EMS_CONNECTION_ID','DISPLAY_NAME']
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
		editable : false,
		triggerAction : 'all',
		disabled:true,
		width :150,
		resizable: true,
		listeners : {
			select : function(combo, record, index) {
				var emsId = combo.getValue();
				if(emsId){
					trunkLineCombo.enable();
					trunkLineCombo.reset();
					trunkLineStore.baseParams = {
						'searchCond.emsId' : combo.getValue()
					};
					trunkLineStore.load();
				}else{
					neCombo.disable();
				}
			}
		}
	});
	//@@@@@@@@@@@@@@@@@@
/*
	// ---------------------------------------------------------------------

	var emsGroupStore = new Ext.data.Store({
		url : 'pm-report!getEmsGroup.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "emsGroupId", "emsGroupName" ])
	});
	emsGroupStore.load();
	var emsGroupCombo = new Ext.form.ComboBox({
		id : 'emsGroupCombo',
		store : emsGroupStore,
		mode : 'local',
		width : 130,
		triggerAction : 'all',
		emptyText : '网管分组',
		valueField : 'emsGroupId',
		displayField : 'emsGroupName',
		editable : false
	});
	emsGroupCombo.on('select', function(combo, record, index) {
		emsCombo.reset();
		emsStore.baseParams = {
			'searchCond.emsGroupId' : combo.getValue()
		};
		emsStore.load();

		trunkLineCombo.reset();
		trunkLineStore.baseParams = {
			'searchCond.emsGroupId' : combo.getValue()
		};
		trunkLineStore.load();
	});
	// ---------------------------------------------------------------------
	var emsStore = new Ext.data.Store({
		url : 'pm-report!getEms.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "emsId", "emsName" ])
	});
	emsStore.load();
	var emsCombo = new Ext.form.ComboBox({
		id : 'emsCombo',
		store : emsStore,
		mode : 'local',
		emptyText : '网管',
		width : 130,
		triggerAction : 'all',
		valueField : 'emsId',
		displayField : 'emsName',
		editable : false
	});
	emsCombo.on('select', function(combo, record, index) {
		trunkLineCombo.reset();
		trunkLineStore.baseParams = {
			'searchCond.emsGroupId' : Ext.getCmp('emsGroupCombo').getValue(),
			'searchCond.emsId' : combo.getValue()
		};
		trunkLineStore.load();
	});*/
	// -----------------------------------------------------------------
	var trunkLineStore = new Ext.data.Store({
		url : 'pm-report!getTrunkLine.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "trunkLineId", "trunkLineName" ])
	});
//	trunkLineStore.load();
	var trunkLineCombo = new Ext.form.ComboBox({
		id : 'trunkLineCombo',
		store : trunkLineStore,
		mode : 'local',
//		emptyText : '干线',
		disabled:true,
		width : 130,
		triggerAction : 'all',
		editable : false,
		valueField : 'trunkLineId',
		displayField : 'trunkLineName',
		resizable: true,
		editable : true
	});
	// ----------------------------------------------------------------
	var gridPanel = new Ext.grid.GridPanel({
		id : 'gridPanel',
		cm : cm,
		height : 500,
		width : 800,
		title : '复用段信息',
		store : store,
//		region : 'center',
		selModel : sm,
		stripeRows : true,
		tbar : [ '-','网管分组',emsGroupCombo, '-','网管', emsCombo, '-','干线', trunkLineCombo, '-',{
			text : '查询',
			icon : '../../../resource/images/btnImages/search.png',
			handler : search
		} ]
	});
	// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑复用段信息结束↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

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
		store.baseParams = params;
		store.load({
			callback : function(r, scope, success) {
				if (!success)
					Ext.Msg.alert('提示', '查询复用段失败！');
			}
		});
	}
	// 添加到已选
	function add(close) {
		var selected = gridPanel.getSelectionModel().getSelections();
		var records = new Array();
		if (selected && selected.length > 0) {
			for ( var i = 0; i < selected.length; i++) {
				var recordIndex = storeMulti.findBy(function(rec, id) {
					if (rec.get('MSId') == selected[i].get('MSId')) {
						return true;
					}
				});
				if (recordIndex == -1) {
					records.push(selected[i]);
				}
			}
		}
		storeMulti.add(records);
		if (close == 1)
			win.close();
		
		triggerForButton();
	}
	
//	function directionRenderer(v) {
//		if (v == 1)
//			return '单向';
//		if (v == 2)
//			return '双向';
//	}
	//----------------------------------------------------------------------------
	var win = new Ext.Window({
		id : 'addTaskWin',
		title : '添加复用段',
		layout : 'form',
		height : 500,
		width : 800,
		autoScroll : true,
		items : [ gridPanel ],
		buttons : [ {
			text : '应用', 
			handler : function() {
				add(0);
			}
		}, {
			text : '确定', 
			handler : function() {
				add(1);
			}
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
		gridPanel.setHeight(win.getInnerHeight());
	}
	if (win.getWidth() > Ext.getCmp('win').getWidth()) {
		win.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		gridPanel.setWidth(win.getInnerWidth());
	}
	win.center();
	win.doLayout();
	// alert(win.getHeight());
	// alert(Ext.getCmp('win').getHeight());
}