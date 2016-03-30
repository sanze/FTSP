/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
//全局变量定义
var confirmOrApply;
var timeData = [

[ '1', '1' ], [ '2', '2' ], [ '3', '3' ], [ '4', '4' ], [ '5', '5' ],
		[ '6', '6' ], [ '7', '7' ], [ '8', '8' ] ];
var timeStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
timeStore.loadData(timeData);

var linkTypeData = [

[ '1', '外部link' ], [ '2', '内部link' ]

];
var linkTypeStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
linkTypeStore.loadData(linkTypeData);
var linkTypeCombo = new Ext.form.ComboBox({
	id : 'linkTypeCombo',
	store : linkTypeStore,
	mode : 'local',
	emptyText : '类型',
	width : 130,
	triggerAction : 'all',
	valueField : 'value',
	displayField : 'displayName',
	editable : false
});
/*------------------------------------------冲突电路------------------------------------------*/

var circuitStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "CIR_CIRCUIT_ID", "cir_no", "source_no", "svc_type", "client_name",
			"a_end_ctp", "z_end_ctp", "a_end_port", "z_end_port", "rate",
			"cir_name", "a_end_user_name", "z_end_user_name",
			"IS_COMPLETE_CIR", "a_end_ne", "a_end_ems", "a_end_ems_group",
			"z_end_ne", "z_end_ems", "Z_end_ems_group", "A_CTP_ID", "Z_CTP_ID",
			"USED_FOR","CIR_CIRCUIT_INFO_ID" ])
});

// ==========================page=============================
var circuitCheckboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : true,
	header : ""
});
circuitCheckboxSelectionModel.sortLock();
var circuitCm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), circuitCheckboxSelectionModel, {
		id : 'circuitNo',
		header : '电路编号',
		dataIndex : 'cir_no',
		width : 100
	}, {
		id : 'systemSourceNo',
		header : '资源系统编号',
		dataIndex : 'source_no',
		width : 100
	}, {
		id : 'circuitType',
		header : "电路类别",
		hidden:true,
		dataIndex : 'svc_type',
		width : 100,
		renderer : function(v) {
			if (v == 1) {
				return "SDH电路";
			}
			if (v == 2)
				return "以太网电路";
			if (v == 3)
				return "WDM电路";
		}
	}, {
		id : 'ane',
		header : 'A端网元',
		hidden:true,
		dataIndex : 'a_end_ne',
		width : 100

	}, {
		id : 'aport',
		header : 'A端端口',
		dataIndex : 'a_end_port',
		width : 100

	}, {
		id : 'actp',
		header : 'A端时隙',
		dataIndex : 'a_end_ctp',
		width : 100

	}, {
		id : 'zne',
		header : 'Z端网元',
		hidden:true,
		dataIndex : 'z_end_ne',
		width : 100

	}, {
		id : 'zport',
		header : 'Z端端口',
		dataIndex : 'z_end_port',
		width : 100

	}, {
		id : 'zctp',
		header : 'Z端时隙',
		dataIndex : 'z_end_ctp',
		width : 100

	}, {
		id : 'rate',
		header : '电路速率',
		dataIndex : 'rate',
		width : 100
		
	}, {
		id : 'type',
		header : '电路类型',
		dataIndex : 'IS_COMPLETE_CIR',
		width : 100,
		renderer : function(v) {
			if (v == 0)
				return "不完整";
			if (v == 1)
				return "完整";
		}

	}, {
		id : 'circuitName',
		header : '路由名称',
		dataIndex : 'cir_name',
		width : 100
	}, {
		id : 'clientName',
		header : '客户名称',
		dataIndex : 'client_name',
		width : 100
	}, {
		id : 'usedFor',
		header : '用途',
		dataIndex : 'USED_FOR',
		width : 100
	}, {
		id : 'AEndUserName',
		header : 'A端用户',
		dataIndex : 'a_end_user_name',
		width : 100
	}, {
		id : 'ZEndUserName',
		header : 'Z端用户',
		dataIndex : 'z_end_user_name',
		width : 100
	}, {
		id : 'AEMS',
		header : 'A端所属网管',
		hidden:true,
		dataIndex : 'a_end_ems',
		width : 100
	}, {
		id : 'AEMSGroup',
		header : 'A端所属网管分组',
		hidden:true,
		dataIndex : 'a_end_ems_group',
		width : 100
	}, {
		id : 'ZEMS',
		header : 'Z端所属网管',
		hidden:true,
		dataIndex : 'z_end_ems',
		width : 100
	}, {
		id : 'ZEMSGroup',
		header : 'Z端所属网管分组',
		hidden:true,
		dataIndex : 'Z_end_ems_group',
		width : 100
	} ]
});

var circuitPageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 1000,// 每页显示的记录值
	store : circuitStore,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var circuitGridPanel = new Ext.grid.EditorGridPanel({
	id : "circuitGridPanel",
	region : "center",
	cm : circuitCm,
	store : circuitStore,
	stripeRows : true, // 交替行效果
//	loadMask : {
//		msg : '数据加载中...'
//	},
	selModel : circuitCheckboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	bbar : circuitGridPanel
});

var circuitWindow=new Ext.Window({
	        id:'circuitWindow',
	        title:'冲突电路',
	        layout:'fit',
	        width:1900,
	        height:1500,
	        isTopContainer : true,
	        shadow:false,
	        autoScroll:true,
	        closeAction:'hide',
			items:circuitGridPanel	     	
			});
/*------------------------------------------冲突电路------------------------------------------*/
/*-----------------------------------------------操作权限组-----------------------------------------*/
var privilegeStore = new Ext.data.Store({
	url : 'inspect-task!getPrivilegeGroupList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ {
		name : "groupId",
		mapping : "SYS_USER_GROUP_ID"
	}, {
		name : "groupName",
		mapping : "GROUP_NAME"
	} ])
});
privilegeStore.load({
	callback : function(r, options, success) {
		if (!success) {
			var obj = Ext.decode(r.responseText);
			Ext.Msg.alert("提示", obj.returnMessage);
		}
	}
});

var privilegeCKSM = new Ext.grid.CheckboxSelectionModel();
var privilegeCM = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), privilegeCKSM, {
		id : 'groupId',
		header : 'ID',
		dataIndex : 'groupId',
		width : 100,
		hidden : true
	}, {
		id : 'groupName',
		header : '组名',
		dataIndex : 'groupName'
	} ]
});
var privilegeGrid = new Ext.grid.GridPanel({
	id : "privilegeGrid",
	title : '',
	height : 150,
	cm : privilegeCM,
	sm : privilegeCKSM,
	store : privilegeStore,
	stripeRows : true, // 交替行效果
	autoScroll:true,
	loadMask : true,
	selModel : privilegeCKSM, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	}
});

var linkNameOrId = new Ext.form.TextField({
	id : 'linkNameOrId',

	emptyText : '链路名称/编号',
	width : 130

});
/*-----------------------------------割 接 链 路---------------------------------------------*/

var storeI = new Ext.data.Store({
	url : 'cutover-task!getLink.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "LINK_NAME", "A_NAME", "Z_NAME", "BASE_LINK_ID" ])
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
		id : 'LINK_NAME',
		header : '名称',
		dataIndex : 'LINK_NAME',
		width : 100
	}, {
		id : 'A_NAME',
		header : 'A端网元端口',
		dataIndex : 'A_NAME',
		width : 150
	}, {
		id : 'Z_NAME',
		header : 'Z端端口',
		dataIndex : 'Z_NAME',
		width : 250
	}, {
		id : 'BASE_LINK_ID',
		header : 'BASE_LINK_ID',
		dataIndex : 'BASE_LINK_ID',
		hidden : true,
		width : 250
	} ]
});
var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : storeI,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
var emsGroupCombo1 = Ext.apply(emsGroupCombo,{emptyText:'网管分组'});
var emsCombo1 = Ext.apply(emsCombo,{emptyText:'网管'});
var gridPanelI = new Ext.grid.GridPanel({
	id : 'gridPanelI',
	cm : cmI,
	height : 270,
	title : '所有链路',
	store : storeI,
	region : 'north',
	selModel : smI,
	bbar : pageTool,
	loadMask : true,
	stripeRows : true,
	tbar : ['-', emsGroupCombo1, '-', emsCombo1, '-', linkTypeCombo,'-',
			linkNameOrId,'-', {
				text : '查询',
				icon : '../../../resource/images/btnImages/search.png',
				handler : search
			},  '->', {
				text : '新增',
				icon : '../../../resource/images/btnImages/add.png',
				handler : add
			} ]
});

var storeII = new Ext.data.Store({
	url : 'cutover-task!initCutoverLink.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "LINK_NAME", "A_NAME", "Z_NAME", "BASE_LINK_ID" ])
});
var smII = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var cmII = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), smII, {
		id : 'LINK_NAME',
		header : '名称',
		dataIndex : 'LINK_NAME',
		width : 100
	}, {
		id : 'A_NAME',
		header : 'A端网元端口',
		dataIndex : 'A_NAME',
		width : 150
	}, {
		id : 'Z_NAME',
		header : 'Z端端口',
		dataIndex : 'Z_NAME',
		width : 150
	}, {
		id : 'TARGET_ID',
		header : 'BASE_LINK_ID',
		dataIndex : 'BASE_LINK_ID',
		hidden : true,
		width : 150
	} ]
});
var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	title : '割接链路',
	cm : cmII,
	store : storeII,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : smII, // 必须加不然不能选checkbox
	// viewConfig : {
	// forceFit : true
	// },
	tbar : [ "->", {
		text : '删除',
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
			var records = gridPanel.getSelectionModel().getSelections();
			var len = records.length;
			if (len <= 0) {
				parent.Ext.Msg.alert("提示", "请选择需要删除的链路！");
			} else {
				for ( var i = 0; i < len; i++) {
					gridPanel.store.remove(records[i]);
				}
			}
		}
	} ]
/*
 * , plugins: [rightMenu]
 */
});
function search() {
	var emsGroupId = Ext.getCmp("emsGroupCombo").getValue();
	var emsId = Ext.getCmp("emsCombo").getValue();
	var linkType = Ext.getCmp("linkTypeCombo").getValue();
	var linkNameOrId = Ext.getCmp("linkNameOrId").getValue();

	// 查询条件
	var jsonData = {
		"searchCondition.emsGroupId" : emsGroupId,
		"searchCondition.emsId" : emsId,
		"searchCondition.linkType" : linkType,
		"searchCondition.linkNameOrId" : linkNameOrId,
		"start" : 0,
		"limit" : 200
	};
	storeI.baseParams = jsonData;
	storeI.load({
		callback : function(records, options, success) {
			if (!success) {
				Ext.Msg.alert("提示", "查询出错");
			}
			gridPanelI.getEl().unmask();
		}
	});
}

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
/*-----------------------------------------------割接任务详细信息-----------------------------------------*/
var eastPanel = new Ext.form.FormPanel({
	title : "",
	id : "eastPanel",
	region : "east",
	bodyStyle : 'padding:10px 15px 10px 15px;',
	width : 350,
	autoScroll : true,
	collapsible : false,
	labelWidth : 60,
	// labelAlign: 'right',
	fbar : [ {
		text : '确定',
		id : 'ok',
		name : 'ok',
		handler : function() {
			confirmOrApply = 0;
			applyConfig();
		}
	}, {
		text : '取消',
		id : 'cancel',
		name : 'cancel',
		handler : function() {
			var window = parent.Ext.getCmp('addWindow');
			if (window) {
				window.close();
			}
		}
	}, {
		text : '应用',
		id : 'apply',
		name : 'apply',
		handler : function() {
			confirmOrApply = 1;
			applyConfig();
		}
	}, {
		xtype : 'label',
		width : '15px'
	} ],
	// defaults: {
	// anchor: '100%',
	// labelStyle:"margin-bottom:10px;",
	// style:"margin-bottom:10px;"
	// },
	items : [
			{
				xtype : 'hidden',
				fieldLabel : '任务ID',
				disabled : true
			},
			{
				xtype : 'textfield',
				id : "taskName",
				name : "taskName",
				fieldLabel : '任务名称',
				width : 200,
				allowBlank : false,
				sideText : '<font color=red>*</font>',
				blankText : "必填",
				validationEvent : 'blur',
				invalidText : '任务名重复',
				// validateOnBlur: true,
				// validationDelay:2000,
				// validateOnChange: false,
				validator : function(val) {
					var vastatic = false;
					var validator = this;
					var error = true;
					cutoverTaskId = cutoverTaskId == null
							|| cutoverTaskId == 'null' ? "" : cutoverTaskId;
					Ext.Ajax.request({
						url : 'cutover-task!checkTaskNameExist.action',
						method : 'post',
						scope : validator,
						params : {
							'searchCondition.taskName' : val,
							'searchCondition.cutoverTaskId' : cutoverTaskId
						},
						success : function(response, opts) {
							var obj = Ext.decode(response.responseText);
							if (obj.exit) {
								vastatic = true;
								Ext.Msg.alert("提示", "有相同名称的割接任务。名称不可重复。");
								this.setValue("");
							} else {
								vastatic = false;
							}
						},
						failure : function(response, opts) {
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("提示", obj.result.returnMessage);
						}
					});
					// return vastatic;
				},
				listeners : {
					focus : {
						fn : function() {
							this.clearInvalid();
						}
					}
				}
			}, {
				xtype : 'textarea',
				id : "taskDescription",
				name : "taskDescription",
				width : 200,
				height : 40,
				fieldLabel : '任务描述',
				value : ''
			}, {
				xtype : 'compositefield',
				fieldLabel : '开始时间',
				width:200,
				border : false,
				items : [ {
					xtype : 'textfield',
					id : 'startTime',
					name : 'startTime',
					width:140,
					cls : 'Wdate',
					listeners : {
						'focus' : function() {
							WdatePicker({
								el : "startTime",
								isShowClear : false,
								readOnly : true,
								dateFmt : 'yyyy/MM/dd HH:mm',
								autoPickDate : true

							})
						}
					}
				}, {
					xtype : 'displayfield',
//					columnWidth : 0.05,
					value : "<font color=red>*</font>",
					fieldLabel : ''
				}, {
					xtype : 'checkbox',
					id : "hangUp",
//					columnWidth : 0.25,
					boxLabel : '挂起'

				} ]
			}, {
				xtype : 'textfield',
				id : 'endTime',
				name : 'endTime',
				fieldLabel : '结束时间',
				width:140,
				cls : 'Wdate',
				sideText : '<font color=red> *</font>',
				value : this.nowTime,
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "endTime",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy/MM/dd HH:mm',
							autoPickDate : true

						})
					}
				}
			}, {xtype:'fieldset',
				border:false,
				labelWidth:1,
				layout:"form",
				items:[{
					xtype : 'compositefield',
					flex : 5,
					items : [ {

						xtype : 'checkbox',
						id : 'filterAlarm',
						boxLabel : '割接期间对综告接口自动过滤告警',
						flex : 3,
						checked : true

					}, {
						xtype : 'displayfield',
						value : "",
						fieldLabel : '',
						flex : 1
					} ]
				}, {
					xtype : 'compositefield',
					flex : 5,
					items : [ {

						xtype : 'checkbox',
						id : 'updateCompareValue',
						boxLabel : '割接完成后自动更新基准值',
						flex : 3,
						checked : true

					}, {
						xtype : 'displayfield',
						value : "",
						fieldLabel : '',
						flex : 1
					} ]
				},{
					xtype : 'compositefield',
//					width:0,
					items : [ {
						xtype : 'radio',
						boxLabel : '割接前',
						checked : true,
						id : 'delayCollect',
						listeners : {
							'check' : function() {
								if (this.checked == true) {
									Ext.getCmp('nowCollect').setValue(false);
									Ext.getCmp('notCollect').setValue(false);
								}
							}
						}

					}, {
						xtype : 'combo',
						fieldLabel : '',
						id : "snapshot",
						store : timeStore,
						mode : 'local',
						valueFile : 'value',
						triggerAction : 'all',
						displayField : 'displayName',
						width : 40
//						anchor : '50%'

					}, {
						xtype : 'displayfield',
						value : "小时自动采集性能值和告警快照",
						fieldLabel : ''
					} ]
				}, {
					xtype : 'compositefield',
					labelWidth : 10,
					flex : 5,
					items : [ {
						xtype : 'radio',
						id : 'nowCollect',
						flex : 3,
						boxLabel : '立即采集性能值和告警快照',
						listeners : {
							'check' : function() {
								if (this.checked == true) {
									Ext.getCmp('delayCollect').setValue(false);
									Ext.getCmp('notCollect').setValue(false);
								}
							}
						}

					}, {
						xtype : 'displayfield',
						value : "",
						fieldLabel : '',
						flex : 1
					} ]
				}, {
					xtype : 'compositefield',
					labelWidth : 10,
					flex : 5,
					items : [ {
						xtype : 'radio',
						id : 'notCollect',
						width : 300,
						boxLabel : '不自动采集性能值和告警快照',
						listeners : {
							'check' : function() {
								if (this.checked == true) {
									Ext.getCmp('nowCollect').setValue(false);
									Ext.getCmp('delayCollect').setValue(false);
								}
							}
						}

					}, {
						xtype : 'displayfield',
						value : "",
						fieldLabel : '',
						flex : 1
					} ]
				}]}, {
				xtype : 'fieldset',
				title : '操 作 权 限 组',
				height:150,
				items : [ privilegeGrid ]
			} ]
});

// ==========================center=============================
var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	border : false,
	width : '60%',
	region : 'center',
	autoScroll : true,
	layout : 'border',
	items : [ gridPanelI, gridPanel ]
});
var actualPanel = new Ext.Panel({
	id : 'actualPanel',
	border : false,
	// width:992,
	// height:688,
	autoScroll : true,
	layout : 'border',
	items : [ centerPanel, eastPanel ]
});
// ==========================center=============================

/*-----------------------------------------------方法部分-----------------------------------------*/

// ==========================编辑割接任务=============================
// 应用设置
function applyConfig() {
	if (storeII.getCount() == 0) {
		Ext.Msg.alert("提示", "割接链路不能为空！");
		return;
	}
	var taskName = Ext.getCmp('taskName').getValue();
	var taskDescription = Ext.getCmp('taskDescription').getValue();
	var startTime = new Date(Ext.getCmp('startTime').getValue());
	var endTime = new Date(Ext.getCmp('endTime').getValue());

	if (taskName == "") {
		Ext.Msg.alert("提示", "割接任务名称不能为空！");
		return;
	}
	if (Ext.getCmp('startTime').getValue() == ""
			|| Ext.getCmp('endTime').getValue() == "") {
		Ext.Msg.alert("提示", "起止时间不能为空！");
		return;
	}
	if (endTime - startTime < 0) {
		Ext.Msg.alert("提示", "结束时间不能早于开始时间！");
		return;
	}
	if ((endTime - startTime) / 1000 / 60 / 60 > 24) {
		Ext.Msg.alert("提示", "预计开始时间与结束时间不超过24小时。");
		return;
	}
	var currentTime = new Date();
	if (startTime - currentTime <= 0) {
		Ext.Msg.alert("提示", "预计开始时间应晚于当前时间。");
		return;
	}
	// 任务状态
	var taskStatus;
	// 挂起
	if (Ext.getCmp("hangUp").checked == true) {
		taskStatus = 4;
	} else
		// 等待中
		taskStatus = 1;

	var filterAlarm = Ext.getCmp('filterAlarm').checked == true ? 1 : 0;
	var autoUpdateCompareValue = Ext.getCmp('updateCompareValue').checked == true ? 1 : 0;
	// 是否自动采集快照，预计开始时间之前几小时开始采集快照
	// 0：不自动采集，-1：立即采集，1~8：预计开始时间之前x小时开始采集
	var snapshot;
	if (Ext.getCmp("delayCollect").checked == true) {
		if (Ext.getCmp("snapshot") == "") {
			Ext.Msg.alert("提示", "请选择需要提前采集快照的时间。");
			return;
		}
		snapshot = Ext.getCmp("snapshot").getValue();
	} else if (Ext.getCmp("notCollect").checked == true)
		snapshot = 0;
	else if (Ext.getCmp("nowCollect").checked == true)
		snapshot = -1;
	if (startTime.getTime() - snapshot * 60 * 60 * 1000 < currentTime.getTime()) {
		Ext.Msg.alert("提示", "割接前" + snapshot + "小时自动采集性能值和告警快照，时间不足。");
		return;
	}
	// 割接设备
	var cutoverEquipList = new Array();
	var cutoverEquipNameList = new Array();
	for ( var i = 0; i < storeII.getCount(); i++) {
		cutoverEquipList
				.push("99" + "_" + storeII.getAt(i).get("BASE_LINK_ID"));
		cutoverEquipNameList.push(storeII.getAt(i).get("LINK_NAME") + '@'
				+ storeII.getAt(i).get("A_NAME") + '@'
				+ storeII.getAt(i).get("Z_NAME"));
	}
	// 操作权限组
	var selections = privilegeGrid.getSelectionModel().getSelections();
	var privilegeList = new Array();
//	if (selections.length == 0) {
//		Ext.Msg.alert('提示', '请选择操作权限组！');
//		return;
//	}
	for ( var i = 0; i < selections.length; i++) {
		privilegeList.push(selections[i].get("groupId"));
	}
	var jsonData = {
		"searchCondition.cutoverTaskId" : cutoverTaskId,
		"searchCondition.taskName" : taskName,
		"searchCondition.taskDescription" : taskDescription,
		"searchCondition.startTime" : Ext.getCmp("startTime").getValue(),
		"searchCondition.endTime" : Ext.getCmp("endTime").getValue(),
		"searchCondition.status" : taskStatus,
		// taskStatus保存割接任务类型：1：按网元端口割接，2：按链路割接，3：按复用段割接，4：按光缆割接
		"searchCondition.taskStatus" : 2,
		"searchCondition.filterAlarm" : filterAlarm,
		"searchCondition.autoUpdateCompareValue" : autoUpdateCompareValue,
		"searchCondition.snapshot" : snapshot,
		"cutoverEquipList" : cutoverEquipList,
		"cutoverEquipNameList" : cutoverEquipNameList,
		"privilegeList" : privilegeList
	}
	var url;
	parent.Ext.getCmp('addWindow').getEl().mask("正在检查电路，此过程耗时较长，请耐心等候！");
	// 新增任务
	if (cutoverTaskId == null || cutoverTaskId == "null" || cutoverTaskId == "")
	{
		url = "cutover-task!addCutoverTask.action";
		
		circuitStore.proxy = new Ext.data.HttpProxy({
			url : 'cutover-task!addCutoverTaskPreCheck.action'
		});
//		circuitStore.baseParams = jsonData;
//		circuitStore.load({
//			callback : function(r, options, success) {
//				if (success) {
//				if(circuitStore.reader.jsonData.conflict==1)
//				{
//					circuitWindow.show();
//					circuitWindow.setHeight(parent.Ext.getCmp('addWindow').getHeight() * 0.8);
//					circuitWindow.setWidth(parent.Ext.getCmp('addWindow').getWidth() * 0.8);
//					circuitWindow.center();
//					circuitWindow.doLayout();
//				}
//				else
//				{
//					circuitWindow.show();
//					circuitWindow.setHeight(parent.Ext.getCmp('addWindow').getHeight() * 0.8);
//					circuitWindow.setWidth(parent.Ext.getCmp('addWindow').getWidth() * 0.8);
//					circuitWindow.center();
//					circuitWindow.doLayout();
					Ext.Ajax.request({
					url : url,
					method : 'Post',
					params : jsonData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						parent.Ext.getCmp('addWindow').getEl().unmask();
						if(obj.conflict != undefined && obj.conflict == 1)
						{
							circuitStore.loadData(obj);
							circuitWindow.show();
							circuitWindow.setHeight(parent.Ext.getCmp('addWindow').getHeight() * 0.8);
							circuitWindow.setWidth(parent.Ext.getCmp('addWindow').getWidth() * 0.8);
							circuitWindow.center();
							circuitWindow.doLayout();
						}
						if (obj.returnResult!= undefined && obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
						if (obj.returnResult!= undefined && obj.returnResult == 1) {
							cutoverTaskId = obj.returnName;
							Ext.Msg.alert("提示", obj.returnMessage);
							parent.store.reload();
							if (confirmOrApply == 0) {
								var window = parent.Ext.getCmp('addWindow');
								if (window) {
									window.close();
								}
							}
						}
			
					},
					error : function(response) {
						Ext.Msg.alert("提示", response.responseText);
					},
					failure : function(response) {
						Ext.Msg.alert("提示", response.responseText);
					}
			
				})
//				}
//				} else {
//				}
//			}
//		});
		
	}
		
	else
	{
		// 修改任务
		url = "cutover-task!modifyCutoverTask.action";
		Ext.Ajax.request({
		url : url,
		method : 'Post',
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
			if (obj.returnResult == 1) {
				Ext.Msg.alert("提示", obj.returnMessage);
				parent.store.reload();
				if (confirmOrApply == 0) {
					var window = parent.Ext.getCmp('addWindow');
					if (window) {
						window.close();
					}
				}
			}

		},
		error : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		},
		failure : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		}

		})
	}
		
}

function addTask() {

	// 割接设备
	var inspectEquipList = new Array();
	var inspectEquipNameList = new Array();
	for ( var i = 0; i < store.getCount(); i++) {
		inspectEquipList.push(store.getAt(i).get("equipType") + "_"
				+ store.getAt(i).get("equipId"));
		inspectEquipNameList.push(store.getAt(i).get("equipFullName"));
	}

	// var engineerId = Ext.getCmp('engineerID').getValue();
	var taskName = Ext.getCmp('taskName').getValue();
	var taskDescription = Ext.getCmp('taskDescription').getValue();

	// 巡检项目取值
	var myCheckBoxGroup = Ext.getCmp('taskItem');
	var inspectItemList = new Array();
	for ( var i = 0; i < myCheckBoxGroup.items.length; i++) {
		if (myCheckBoxGroup.items.itemAt(i).checked) {
			// alert(myCheckBoxGroup.items.itemAt(i).name);
			inspectItemList.push(myCheckBoxGroup.items.itemAt(i).name);
		}
	}

	// 是否挂起
	var handUp = Ext.getCmp('handUp').checked;
	alert(handUp);
	if (handUp == true) {
		handUp = 2;
	} else {
		handUp = 1;
	}

	if (periodType == 3) { // 每月
		periodTime = "0,0,0,0," + createDay + "," + createTime;
		// alert(periodTime);
	} else {// 每年(每季)
		periodTime = "0,0," + createMonth + ",0," + createDay + ","
				+ createTime;
		// alert(periodTime);
	}

	var jsonData = {
		"inspectTaskId" : inspectTaskId,
		"taskName" : taskName,
		"taskDescription" : taskDescription,
		"inspectItemList" : inspectItemList,
		"periodType" : periodType,
		"periodTime" : periodTime,
		"startTime" : startTime1,
		"nextTime" : nextTime1,
		"handUp" : handUp,
		"privilegeList" : privilegeList,
		"inspectEquipList" : inspectEquipList,
		"inspectEquipNameList" : inspectEquipNameList,
		"privilegeParamId" : privilegeParamId,
		"inspectItemParamId" : inspectItemParamId

	}

	var url;
	if (saveType == 0) {
		ur = 'inspect-task!addInspectTask.action';
	} else {
		ur = 'inspect-task!updateInspectTask.action';
		alert(ur);
	}

	Ext.Ajax.request({
		url : ur,
		method : 'Post',
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
			if (obj.returnResult == 1) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
		},
		error : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		},
		failure : function(response) {
			Ext.Msg.alert("提示", response.responseText);
		}

	})

}

// ----------------------------save handler for
// use--------------------------------

// -----------------------------修改，查看页面初始化数据----------------------------------
function initData() {

	if (cutoverTaskId != null && cutoverTaskId != "null" && cutoverTaskId != "") {
		var jsonData = {
			"searchCondition.cutoverTaskId" : cutoverTaskId
		}
		var cutoverEquipList = new Array();
		storeII.baseParams = jsonData;
		storeII.load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "加载割接链路失败!");
			}
		});

		Ext.Ajax.request({
			url : 'cutover-task!initTaskInfo.action',
			method : 'Post',
			params : jsonData,
			success : function(response) {
				var task = Ext.decode(response.responseText);
				Ext.getCmp('taskName').setValue(task.taskName);
				Ext.getCmp('taskDescription').setValue(task.taskDescription);
				Ext.getCmp('startTime').setValue(task.startTime);
				Ext.getCmp('endTime').setValue(task.endTime);

				var hangUp = task.taskStatus;
				if (task.taskStatus == 4) {
					Ext.getCmp('hangUp').setValue(true);
				} else {
					Ext.getCmp('hangUp').setValue(false);
				}
				var filterAlarm = task.filterAlarm;
				if (filterAlarm == 1)
					Ext.getCmp('filterAlarm').setValue(true);
				else
					Ext.getCmp('filterAlarm').setValue(false);
				var snapshot = task.snapshot;
				if (snapshot == 0)
					Ext.getCmp('notCollect').setValue(true);
				else if (snapshot == -1)
					Ext.getCmp('nowCollect').setValue(true);
				else {
					Ext.getCmp('delayCollect').setValue(true);
					Ext.getCmp('snapshot').setValue(snapshot);
				}
				var autoUpdateCompareValue = task.autoUpdateCompareValue;
				if(autoUpdateCompareValue == 1)
					Ext.getCmp('updateCompareValue').setValue(true);
				else
					Ext.getCmp('updateCompareValue').setValue(false);
				// 权限组加载
				var privilegeList = task.privilegeList;
				var rowIdList = new Array();

				// alert(privilegeList.length);
				// privilegeParamId = obj.privilegeParamId;
				// alert(privilegeParamId);
				for ( var i = 0; i < privilegeStore.getCount(); i++) {
					var groupId = privilegeStore.getAt(i).get("groupId");
					// alert("groupId:"+groupId);
					for ( var j = 0; j < privilegeList.length; j++) {
						// alert("privilege:"+privilegeList[j]);
						if (privilegeList[j] == groupId) {
							// privilegeGrid.getSelectionModel().selectRow(i);
							rowIdList.push(i);
						}
					}
				}
				privilegeGrid.getSelectionModel().selectRows(rowIdList);

			},
			error : function(response) {
				alert("error");
				Ext.Msg.alert("提示", response.responseText);
			},
			failure : function(response) {
				alert("failure");
				Ext.Msg.alert("提示", response.responseText);
			}

		})
	} else {
		var jsonData = {
			"userId" : userId
		}
		Ext.Ajax.request({
			url : 'inspect-task!getCurrentUserGroup.action',
			method : 'Post',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				var checkedGroupList = obj.checkedGroupList;
				var rowIdList = new Array();
				for ( var i = 0; i < privilegeStore.getCount(); i++) {
					var groupId = privilegeStore.getAt(i).get("groupId");
					for ( var j = 0; j < checkedGroupList.length; j++) {
						if (checkedGroupList[j].SYS_USER_GROUP_ID == groupId) {
							rowIdList.push(i);
						}
					}
				}
				privilegeGrid.getSelectionModel().selectRows(rowIdList);
			},
			error : function(response) {
				Ext.Msg.alert("提示", response.responseText);
			},
			failure : function(response) {
				Ext.Msg.alert("提示", response.responseText);
			}

		})
	}
}

Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 9000000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}

	if (saveType == 2) {
		var win = new Ext.Viewport({
			id : 'win',
			loadMask : true,
			layout : 'border',
			items : [ centerPanel, eastPanel ],
			renderTo : Ext.getBody()
		});
		// Ext.getCmp('ok').setDisabled(true);
		Ext.getCmp('apply').setVisible(false);
		Ext.getCmp('cancel').setVisible(false);
		Ext.getCmp('handUp').setVisible(false);
		Ext.getCmp('setButton').setVisible(false);
	} else {
		var win = new Ext.Viewport({
			id : 'win',
			// width:900,
			loadMask : true,
			layout : 'border',
			autoScroll : true,
			items : [ centerPanel, eastPanel ],
			renderTo : Ext.getBody()
		});
	}
	initData();
	Ext.getCmp('snapshot').setValue('4');
	Ext.getCmp("emsGroupCombo").setWidth(120);
	Ext.getCmp("emsCombo").setWidth(120);
});