/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/
/**
 * 创建网管分组数据源
 */
Ext.QuickTips.init();
var emsGroupStore = new Ext.data.Store({
	proxy: new Ext.data.HttpProxy({
		url : 'common!getAllEmsGroups.action', 
		disableCaching: false
	}),
	baseParams : {"displayAll" : true,"displayNone" : true},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});

/**
 * 加载网管分组数据源
 */
emsGroupStore.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}else{
			var firstValue = records[0].get('BASE_EMS_GROUP_ID');
			Ext.getCmp('emsGroupCombo').setValue(firstValue);
		}
	}
});

/**
 * 创建网管分组下拉框
 */
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : emsGroupStore,
	valueField : 'BASE_EMS_GROUP_ID',
	displayField : 'GROUP_NAME',
	editable : false,
	triggerAction : 'all',
	width :150,
	resizable: true,
	listeners : {
		select : function(combo, record, index) {
			store.baseParams = {'jsonString':Ext.encode({'emsGroupId':record.get('BASE_EMS_GROUP_ID')}),
				'limit':500
			},
			store.load({
				callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
			});
		}
	}
});

/**
* 创建厂家下拉框
 */
var factoryCombo = new Ext.form.ComboBox({
	id : 'factoryCombo',
	fieldLabel : '厂家',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '不确认' ], [ '2', '立即确认' ],[ '3', '定时确认' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	width : 130,
	editable : false,
	listeners : {
		select : function(combo, record, index) {
			var factory = record.get('value');
		}
	}
});

/**
 * 下拉框显示值转换
 */
function changeDisplayValue(v){
	var index = factoryCombo.getStore().find('value',v);
	return factoryCombo.getStore().getAt(index).data.displayName;
}

/**
* 创建表格选择模型
 */
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel();
sm.sortLock();

/**
 * 创建表格列模型
 */
var cm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), sm, {
		header : 'id',
		dataIndex : 'AUTO_CONFIRM_ID',
		width : 200,
		hidden : true
	},{
		header : '网管ID',
		dataIndex : 'BASE_EMS_CONNECTION_ID',
		width : 200,
		hidden : true
	},{
		header : '网管分组',
		dataIndex : 'GROUP_NAME',
		width : 100
	}, {
		header : '网管名称',
		dataIndex : 'EMS_NAME',
		width : 100
	}, {
		header : '网管类型',
		dataIndex : 'TYPE',
		width : 100,
		renderer : function(v){
			for(var type in NMS_TYPE){
				if(v==NMS_TYPE[type]['key']){
					return NMS_TYPE[type]['value'];
				}
			}
			return v;
		}
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>紧急</div></div>",
		header : '<span style="font-weight:bold">紧急</span>',
		dataIndex : 'PS_CRITICAL_CONFIRM',
		width : 100,
		editor : factoryCombo,
		renderer : changeDisplayValue,
		tooltip:'可编辑列'
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>重要</div></div>",
		header : '<span style="font-weight:bold">重要</span>',
		dataIndex : 'PS_MAJOR_CONFIRM',
		width : 100,
		editor : factoryCombo,
		renderer : changeDisplayValue,
		tooltip:'可编辑列'
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>次要</div></div>",
		header : '<span style="font-weight:bold">次要</span>',
		dataIndex : 'PS_MINOR_CONFIRM',
		width : 100,
		editor : factoryCombo,
		renderer : changeDisplayValue,
		tooltip:'可编辑列'
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>提示</div></div>",
		header : '<span style="font-weight:bold">提示</span>',
		dataIndex : 'PS_WARNING_CONFIRM',
		width : 100,
		editor : factoryCombo,
		renderer : changeDisplayValue,
		tooltip:'可编辑列'
	}, {
		//header : "<div><div style='float:left'><img src='../../resource/images/btnImages/modify.png' title='编辑' /></div><div>定时确认，清除时间达到(分钟)</div></div>",
		header : '<span style="font-weight:bold">定时确认，清除时间达到(分钟)</span>',
		dataIndex : 'TIMING_TIME',
		width : 200,
		editor : new Ext.form.NumberField({
//			allowBlank: false,
			allowNegative : false,
			maxLength : 10,
			maxLengthText : '长度超过限制',
			allowDecimals:false,//不允许输入小数 
			nanText:'请输入有效整数',//无效数字提示
//			maxValue:365,//最大值                 
//			maxText:'请输入0~365之间的整数',
//			minValue:0,//最小值        
//			minText:'请输入0~365之间的整数'
		}),
		tooltip:'可编辑列'
	}]
});

/**
 * 创建表格数据源
 */
var store = new Ext.data.Store({
	url : 'fault!getAlarmAutoConfirmByEmsGroup.action',
	baseParams : {'jsonString':Ext.encode({'emsGroupId':'-99'}),
		'limit':500
	},
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	["AUTO_CONFIRM_ID","BASE_EMS_CONNECTION_ID","GROUP_NAME","EMS_NAME","TYPE","PS_CRITICAL_CONFIRM","PS_MAJOR_CONFIRM","PS_MINOR_CONFIRM","PS_WARNING_CONFIRM","TIMING_TIME"])
});

/**
 * 加载表格数据源
 */
store.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});
var pageTool = new Ext.PagingToolbar({
	pageSize : 500,
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
/**
 * 创建表格实例
 */
var gridPanel = new Ext.grid.EditorGridPanel({
//	title :'告警自动确认设置',
	region : 'center',
	store : store,
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	/*stateId:'alarmAutoConfirmId',  
	stateful:true,*/
	clicksToEdit : 1,
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-','网管分组：',emsGroupCombo,'-',{
        text: '不确认',
        privilege:modAuth,
        handler : function(){
        	confirmSet('no');
        }
    },{
        text: '立即确认',
        privilege:modAuth,
        handler : function(){
        	confirmSet('immediately');
        }
    },{
        text: '定时确认',
        privilege:modAuth,
        handler : function(){
        	confirmSet('timing');
        }
    },'-',{
        text: '保存',
        icon : '../../resource/images/btnImages/disk.png',
        privilege:modAuth,
        handler : function(){
        	save();
        }
    },'-',{
        text: '定时确认周期',
        privilege:addAuth,
        handler : function(){
        	autoConfirmSet();
        }
    }],
    bbar : pageTool
});

/**
 * 定时确认周期设置
 */
function autoConfirmSet(){
	var url = 'autoConfirmSet.jsp';
	var autoConfirmSetWindow = new Ext.Window({
		id : 'autoConfirmSetWindow',
		title : '定时确认周期设置',
		width : 600,
		height : 210,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='autoConfirmSet_panel' name = 'autoConfirmSet_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	autoConfirmSetWindow.show();
}

/**
 * 保存方法
 */
function save(){
	var modifyRecords = store.getModifiedRecords();
	var json = new Array(); 
	for (var i = 0; i < modifyRecords.length; i++) { 
		// 将修改的对象加入到json中 
		Ext.each(modifyRecords[i], function(item) { 
			json.push(item.data); 
			//alert(item.data.AUTO_CONFIRM_ID);
		}) 
	}
	store.commitChanges();
	if(json.length>0){
		Ext.Ajax.request({
			url : 'fault!modifyAlarmAutoConfirm.action',
			method : 'POST',
			params : {'jsonString':Ext.encode(json)},
			success : function(response) {// 回调函数
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					Ext.Msg.alert("信息", obj.returnMessage);
					top.Ext.getBody().unmask();
					store.load({
						callback : function(records,options,success){
							if (!success) {
								Ext.Msg.alert('错误', '查询失败！请重新查询');
							}
						}
					});
				}else{
					Ext.Msg.alert("信息", obj.returnMessage);
				}
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert('错误', '保存失败！');
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert('错误', '保存失败！');
			}
		});
	}
}

/**
 * 确认设置
 */
function confirmSet(flag){
	// 网管ID
	var emsIds = '';
	// 自动确认ID
	var autoConformIds = '';
	// 确认时间
	var timingTimes = '';
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择自动确认设置');
	}else{
		for ( var i = 0; i < records.length; i++) {
			emsIds += records[i].get('BASE_EMS_CONNECTION_ID') + ',';
			autoConformIds += records[i].get('AUTO_CONFIRM_ID') + ',';
			timingTimes += records[i].get('TIMING_TIME') + ',';
			
		}
		emsIds = emsIds.substring(0, emsIds.lastIndexOf(','));
		autoConformIds = autoConformIds.substring(0, autoConformIds.lastIndexOf(','));
		timingTimes = timingTimes.substring(0, timingTimes.lastIndexOf(','));
		Ext.Ajax.request({
		    url: 'fault!confirmSet.action',
		    method: 'POST',
		    params: {'jsonString':Ext.encode({'emsIds':emsIds,'autoConformIds':autoConformIds,'timingTimes':timingTimes,'flag':flag})},
		    success : function(response) {
		    	store.load({
		    		callback : function(records,options,success){
						if (!success) {
							Ext.Msg.alert('错误', '查询失败！请重新查询');
						}
					}
		    	});
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		})	
	}
}

/**
 * 创建主体部分
 */
//var centerPanel = new Ext.Panel({
//	id : 'centerPanel',
//	region : 'center',
//	autoScroll : true,
//	layout : 'border',
//	items : gridPanel
//});

/**
 * 初始化EXT
 */
Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
		layout : 'border',
		items : gridPanel
	});
});