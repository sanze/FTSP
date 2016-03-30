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
	bbar : circuitPageTool
});

var circuitWindow=new Ext.Window({
	        id:'circuitWindow',
	        title:'存在冲突电路！',
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
/*-----------------------------------割 接 设 备---------------------------------------------*/
var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	rootVisible : false,
	leafType : 8
};
var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
// ==================For the Tree====================
var westPanel = new Ext.Panel({
	title : "",
	id : "westPanel",
	region : "west",
	width : 250,
	// autoScroll:true,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="tree_panel" name="tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0/>'
});

/*
 * var store = new Ext.data.ArrayStore({ fields: [
 * {name:'equipType'},{name:'equipId'},{ name: 'equipFullName'} ] });
 */

// 割接设备store
var store = new Ext.data.Store({
	url : 'cutover-task!initCutoverEquip.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows",
		fields : [ {
			name : 'equipType',
			mapping : "TARGET_TYPE"
		}, {
			name : 'equipId',
			mapping : "TARGET_ID"
		}, {
			name : 'equipFullName',
			mapping : "TARGET_NAME"
		} ]
	})

});

// 添加割接设备

function onGetChecked(getFunc){
	if(!Array.indexOf){
	   Array.prototype.indexOf = function(Object){
	     for(var i = 0;i<this.length;i++){
	        if(this[i] == Object){
	           return i;
	         }
	     }
	     return -1;
	   }
	}
		
	var pathParam="path"+"："+"text";
	var emsResult=getFunc(["nodeLevel","nodeId",pathParam],"all",2);
	var subnetResult = getFunc(["nodeLevel","nodeId","neId",pathParam],"all",3);
	if(emsResult.length!=0 ||subnetResult.length!=0)
		{
   			 parent.Ext.Msg.alert("提示","不可以选择网管或者子网，最大单位为网元！");
   			 return;
   		}
	var neResult=getFunc(["nodeLevel","nodeId",pathParam],"all",4);
	var portResult = getFunc(["nodeLevel","nodeId","neId",pathParam],"all",8);
    var result = [];
    var neIdArray = [];
    var neOfPtpArray = [];
    if(neResult.length!=0 && portResult.length==0)
    {
    	result = neResult;
    }
    else if (neResult.length==0 && portResult.length!=0)
    {
    	result = portResult;
    }
    else
    {
    	for(i=0,len=neResult.length;i<len;i++)
    	{
    		neIdArray.push(neResult[i].nodeId);
    	}
    	for(i=0,len=portResult.length;i<len;i++)
    	{
    		neOfPtpArray.push(portResult[i].neId);
    	}
    	for(i=0,len=neOfPtpArray.length;i<len;i++)
    	{
    		if(neIdArray.indexOf(neOfPtpArray[i])==-1)
    		{
    			Ext.Msg.alert('提示', '网元和端口不可同时选择');
				return;
    		}
    	}
    	result = neResult;
    }
    var reader = new Ext.data.ArrayReader({
      fields : [
        {name:'equipType',mapping:"nodeLevel"},
        {name:'equipId',mapping:"nodeId"},
        {name: 'equipFullName',mapping:pathParam}]
    })
    obj=reader.readRecords(result);
    for(i=0;i<obj.records.length;i++){
    	if(obj.records[i].get('equipType')== 1 || obj.records[i].get('equipType')== 2|| obj.records[i].get('equipType')== 3)
   		{
   			 parent.Ext.Msg.alert("提示","不可以选择网管或者子网，最大单位为网元！");
   			 return;
   		}
    }
    var Records=[];
    for (i=0; i < obj.records.length; i++) {
			if (obj.records[0].get('equipType') != obj.records[i].get('equipType')) {
				Ext.Msg.alert('提示', '网元和端口不可同时选择');
				return;

		}
	}
	// 防止选中节点重复添加到store中
	for (i = 0; i < obj.records.length; i++) {
		var recordIndex = store.findBy(function(record, id) {
			if (record.get('equipType') == obj.records[i].get('equipType')
					&& record.get('equipId') == obj.records[i].get('equipId')) {
				return true;
			}
		});

		if (recordIndex == -1) {
			Records.push(obj.records[i])
		}
	}

	store.add(Records);
}

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'equipType',
		header : 'type',
		dataIndex : 'equipType',
		hidden : true
	}, {
		id : 'equipId',
		header : 'id',
		dataIndex : 'equipId',
		hidden : true
	}, {
		id : 'equipFullName',
		header : '设备',
		dataIndex : 'equipFullName'
	} ]
});
/*
 * var rightMenu = new Ext.ux.grid.RightMenu({ items : [{ text : '删除',
 * recHandler : function(record, rowIndex, grid) { } }] });
 */
var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	title : '',
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	},
	tbar : [ "割接设备", "->", {
		text : '删除',
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
			var records = gridPanel.getSelectionModel().getSelections();
			var len = records.length;
			if (len <= 0) {
				parent.Ext.Msg.alert("提示", "请选择需要删除的设备！");
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
	height : 120,
	cm : privilegeCM,
	sm : privilegeCKSM,
	autoScroll:true,
	store : privilegeStore,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : privilegeCKSM, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	}
});
/*-----------------------------------------------割接任务详细信息-----------------------------------------*/
var eastPanel = new Ext.form.FormPanel({
	title : "",
	id : "eastPanel",
	region : "east",
	bodyStyle : 'padding:10px 15px 10px 15px;',
	width : 380,
	autoScroll : true,
	collapsible : false,
	labelWidth :60,
	buttons : [ {
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
//	defaults : {
//		anchor : '100%',
//		labelStyle : "margin-bottom:10px;",
//		style : "margin-bottom:10px;"
//	},
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
				width:250,
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
				width:250,
				height:40,
				fieldLabel : '任务描述',
				value : ''
			}, {
				xtype : 'compositefield',
				fieldLabel : '开始时间',
				width:200,
				border:false,
				items : [ {
					xtype : 'textfield',
					id : 'startTime',
					name : 'startTime',
					width:140,
					cls : 'Wdate',
					value : this.nowTime,
					listeners : {
						'focus' : function() {
							WdatePicker({
								el : "startTime",
								isShowClear : false,
								readOnly : true,
								dateFmt : 'yyyy/MM/dd HH:mm',
								autoPickDate : true

							});
							this.blur();
						}
					}
				}, {
					xtype : 'displayfield',
//					columnWidth:0.05,
					value : "<font color=red>*</font>",
					fieldLabel : ''
				}, {
					xtype : 'checkbox',
//					columnWidth:0.3,
					id : "hangUp",
					boxLabel : '挂起'

				} ]
			}, {
				xtype : 'textfield',
				id : 'endTime',
				name : 'endTime',
				fieldLabel : '结束时间',
				width:140,
				sideText : '<font color=red> *</font>',
				cls : 'Wdate',
				value : this.nowTime,
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "endTime",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy/MM/dd HH:mm',
							autoPickDate : true

						});
						this.blur();
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
				},  {
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
				items : [ privilegeGrid ]
			} ]
});

function setCycle(field) {

	setWindow.show();

}

// ==========================center=============================
var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	border : false,
	region : 'center',
	autoScroll : true,
	layout : 'border',
	items : [ gridPanel ]
});
// ==========================center=============================

/*-----------------------------------------------方法部分-----------------------------------------*/

// ==========================编辑割接任务=============================
// 应用设置
function applyConfig() {
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "割接设备不能为空！");
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
	for ( var i = 0; i < store.getCount(); i++) {
		if (store.getAt(i).get("equipType") < 4) {
			Ext.Msg.alert("提示", "不可以选择网管或者子网，最大单位为网元。");
			return;
		}
		cutoverEquipList.push(store.getAt(i).get("equipType") + "_"
				+ store.getAt(i).get("equipId"));
		cutoverEquipNameList.push(store.getAt(i).get("equipFullName"));
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
		"searchCondition.taskStatus" : 1,
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
						if(obj.conflict != undefined && obj.conflict == 1)
						{
							parent.Ext.getCmp('addWindow').getEl().unmask();
							circuitStore.loadData({'rows':obj.rows});
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
							else
								parent.Ext.getCmp('addWindow').getEl().unmask();
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
			parent.Ext.getCmp('addWindow').getEl().unmask();
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
			parent.Ext.getCmp('addWindow').getEl().unmask();
			Ext.Msg.alert("提示", response.responseText);
		},
		failure : function(response) {
			parent.Ext.getCmp('addWindow').getEl().unmask();
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
	// 周期类型及周期
	// var periodType = Ext.getCmp('period').value;
	// var periodTime;
	// var createMonth = Ext.getCmp('createMonth').value;
	// var createDay = Ext.getCmp('createDay').value;
	// var createTime = Ext.get('createTime').dom.value;

	if (periodType == 3) { // 每月
		periodTime = "0,0,0,0," + createDay + "," + createTime;
		// alert(periodTime);
	} else {// 每年(每季)
		periodTime = "0,0," + createMonth + ",0," + createDay + ","
				+ createTime;
		// alert(periodTime);
	}

	// 开始时间、下次执行时间
	// var startTime = Ext.getCmp('startTime').value;
	// var nextTime = Ext.getCmp('nextTime').value;

	// 操作权限组
	var selections = privilegeGrid.getSelectionModel().getSelections();
	var privilegeList = new Array();
	if (selections.length == 0) {
		Ext.Msg.alert('提示', '请选择需要关联的性能数据条目！');
		return;
	}
	for ( var i = 0; i < selections.length; i++) {
		privilegeList.push(selections[i].get("groupId"));
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

	if (cutoverTaskId != null && cutoverTaskId != 'null' && cutoverTaskId != "") {
		var jsonData = {
			"searchCondition.cutoverTaskId" : cutoverTaskId
		}
		var cutoverEquipList = new Array();
		store.baseParams = jsonData;
		store.load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "加载割接设备失败!");
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
				// privilegeParamId = task.privilegeParamId;
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
	}

	else {
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
			loadMask : true,
			layout : 'border',
			items : [ centerPanel, westPanel, eastPanel ],
			renderTo : Ext.getBody()
		});
	}
	initData();
	Ext.getCmp('snapshot').setValue('4');
});