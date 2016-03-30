Ext.override(Ext.form.CheckboxGroup, {
	getValue : function(mode) {
		var v = [];
		if (mode == 1) {
			this.items.each(function(item) {
				if (item.getValue())
					v.push(item.getRawValue());
			});
			return v;
		} else {
			this.items.each(function(item) {
				v.push(item.getValue());
			});
			return v;
		}
	}
});


var store = new Ext.data.Store({
	url:'nx-report!searchWaveDirById.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [  "emsGroup", "ems", "ne", "subnet",
			"factory", "neType", 'networkName',"unit","waveDirId",
			"waveDir","stdWaveNum","actWaveNum","station","neId" ])
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 130
	}, {
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 130
	}, {
		id : 'subnet',
		header : '子网',
		dataIndex : 'subnet',
		width : 120
	}, {
		id : 'ne',
		header : '网元名称',
		dataIndex : 'ne',
		width : 180
	}, {
		id : 'factory',
		header : '设备厂家',
		dataIndex : 'factory',
		width : 100,
		renderer : factoryRenderer
	}, {
		id : 'neType',
		header : '网元型号',
		dataIndex : 'neType',
		width : 140
	}, {
		id : 'unit',
		header : '单位',
		dataIndex : 'unit',
		width : 100
	}, {
		id : 'station',
		header : '站名',
		dataIndex : 'station',
		width : 100
	}, {
		id : 'networkName',
		header : '网络名称',
		dataIndex : 'networkName',
		width : 120
	}, {
		id : 'waveDir',
		header : '方向',
		dataIndex : 'waveDir',
		width : 120
	} , {
		id : 'stdWaveNum',
		header : '容量（波数）',
		dataIndex : 'stdWaveNum',
		width : 100
	} , {
		id : 'actWaveNum',
		header : '实开（波数）',
		dataIndex : 'actWaveNum',
		width : 100
	} ]
});

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	 title:'波分方向选择',
	store : store,
	cm : cm,
	loadMask:true,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-',{
		text : '新增',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : addWaveDir
	}, '-',{
		text : '删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
			var items = grid.getSelectionModel().getSelections();
			if(items.length>0){
				Ext.Msg.confirm("提示","是否删除选中的对象？",function(btn){
					if(btn=="yes"){
						for ( var i = 0; i < items.length; i++) {
							store.remove(items[i]);
						}
						store.commitChanges();
					}
				});
			}else{
				Ext.Msg.alert("提示","请先选取对象！");
			}
		}
	}, '-',{
		text : '清空',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function() {
			Ext.Msg.confirm("提示","是否清空所有对象数据？",function(btn){
				if(btn=="yes"){
					store.removeAll();
					store.commitChanges();
				}
			});
			
		}
	} ],
	buttons : [ '->', {
		text : '保存',
		privilege:addAuth,
		id:'saveButton',
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function(){beforeSave(false,"waveDirId",false,true);}
	},{
		text : '预览',
		privilege:viewAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function(){beforeSave(true,"waveDirId",false,true);}
	} ]
});

var rowIII = {
	id : 'rowIII',
	border : false,
	width:1120,
	layout : 'column',
	items : [ {
		columnWidth : 1,
		layout : 'form',
		border : false,
		items : [ WDM ]
	} ]
};

var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	autoScroll:true,
	labelAlign : 'left',
	height : 350,
	collapsible : true,
	items : [ rowI, rowII, rowIII ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓


/**
 * 修改时初始化页面
 */
function initTaskInfo(){
	var param = {
			'paramMap.taskId' : taskId
		};
	top.Ext.getBody().mask("正在加载");
	Ext.Ajax.request({
		url : 'nx-report!initReportTaskInfo.action',
		method : 'POST',
		params : param,
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult != 0) {
				var taskInfo = result.taskInfo[0];
				var taskNodes = result.taskNodes;
				//上半
				if (!!taskInfo) {
					Ext.getCmp('reportTaskName').setValue(taskInfo.taskName);
					Ext.getCmp('privilege').setValue(taskInfo.privilege);
					Ext.getCmp('periodCb').setValue(taskInfo.period);
					Ext.getCmp('hourCb').setValue(taskInfo.hour);
					Ext.getCmp('dataSrcCombo').setValue(taskInfo.dataSrc);
					if(taskInfo.dataSrc==1){
						Ext.getCmp('continueAbnormal').setVisible(true);
						Ext.getCmp('continueAbnormal').setValue(taskInfo.continueAbnormal);
					}else{
						Ext.getCmp('continueAbnormal').setVisible(false);
					}
					if (taskInfo.period == 0) {
						delay4MonthlyCb.hide();
						delay4DailyCb.show();
						delay4DailyCb.setValue(taskInfo.delay);
					}
					if (taskInfo.period == 1) {
						delay4DailyCb.hide();
						delay4MonthlyCb.show();
						delay4MonthlyCb.setValue(taskInfo.delay);
					}
					//各种选框还原
					Ext.getCmp('WDMTPLevel').setValue(taskInfo.WDMTpCheckedStatus.split(', '));
					Ext.getCmp('WDMPhysical').setValue(taskInfo.WDMPhyCheckedStatus.split(', '));
					Ext.getCmp('WDMNumberic').setValue(taskInfo.WDMNumCheckedStatus.split(', '));
				}
				//下半
				if (!!taskNodes&&taskNodes.length>0) {
					var waveDirList = new Array();
					for(var i=0;i<taskNodes.length;i++){
						waveDirList.push(Ext.encode({"nodeId":taskNodes[i]['nodeId']}));
					}
					store.baseParams={'modifyList':waveDirList};
					store.load({
						callback : function(records,options,success){
							// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
							if (!success) {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}else{
							}
						}
					});
				}
			} else {
				Ext.Msg.alert("提示", result.returnMessage);
			}
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}
//--------------------------------------------------------------------
function factoryRenderer(v){
	for(var i=0;i<FACTORY.length;i++){
		if(v==FACTORY[i]['key'])
			return FACTORY[i]['value'];
	}
	return v;
}
//---------------------------------------------------------------------
Ext
		.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			var win = new Ext.Viewport({
				id : 'win',
				title : "新增报表",
				layout : 'border',
				// autoScroll:true,
				items : [ northPanel, grid ],
				renderTo : Ext.getBody()
			});
			win.show();
			Ext.getCmp('WDMTPLevel').items.get(0).disable();
			Ext.getCmp('WDMPhysical').items.get(0).disable();
			Ext.getCmp('WDMPhysical').items.get(1).disable();
			if(!!taskId){
				initTaskInfo();
				if(userId!=creatorId&&userId!=-1)
					Ext.getCmp("saveButton").disable();
			}
		});