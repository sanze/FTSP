/**
* 创建厂家下拉框
 */
var factoryCombo = new Ext.form.ComboBox({
	id : 'factoryCombo',
	fieldLabel : '厂家',
	store :  new Ext.data.ArrayStore({
		fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:FACTORY
	}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	disabled : true,
	width : 130
//	listeners : {
//		select : function(combo, record, index) {
//			var factory = record.get('value');
//			// 还原告警名称下拉框
//			Ext.getCmp('alarmNameCombo').reset();
//			// 动态改变告警名称数据源的参数
//			alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory' : factory,'alarmName':Ext.getCmp('alarmNameCombo').getValue(),'type': 'current'})};
//			// 加载告警名称数据源
//			alarmNameStore.load({
//				callback : function(records,options,success){
//					if (!success) {
//						Ext.Msg.alert('错误', '查询失败！请重新查询');
//					}
//				}
//			});
//		}
//	}
});

/**
 * 创建告警名称数据源
 */
var alarmNameStore = new Ext.data.Store({
	url : 'fault!getAlarmNameByFactory.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['key']
	})
});

/**
 * 创建表格列模型
 */
var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [{
		header : '厂家id',
		dataIndex : 'FACTORY_ID',
		width : 80,
		hidden : true
	},{
		header : '厂家',
		dataIndex : 'FACTORY',
		width : 80
	}, {
		header : '告警名称',
		dataIndex : 'ALARM_NAME',
		width : 149
	}]
});

/**
 * 创建已选告警名称表格
 */
var gridPanel = new Ext.grid.GridPanel({
	id : 'gridPanel',	
	title :'已选告警名称',
	height : 260, 
	store : new Ext.data.ArrayStore({
		fields : [{name:'FACTORY_ID'},{name:'FACTORY'},{name:'ALARM_NAME'}],
		data : []
	}),
	loadMask : true,
	cm : cm,
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	view : new Ext.grid.GridView(),
	
});
/**
 * 创建告警名称下拉框
 */ 
var alarmNameCombo = new Ext.form.ComboBox({
	id : 'alarmNameCombo',
	fieldLabel : '告警名称',
	store : alarmNameStore,
	valueField : 'key',
	displayField : 'key',
	listEmptyText : '未找到匹配的结果',
	loadingText : '搜索中...',
	mode :'remote', 
	disabled : true,
	width : 130,
	resizable: true,
	triggerAction : 'all'
//	listeners : {
//		beforequery:function(queryEvent){// 每次输入后触发
//			// 获取厂家下拉框的值
//			var factory = Ext.getCmp('factoryCombo').getValue();
//			// 获取告警名称下拉框输入的值，和历史的内容比较，如果不相同，则取后台模糊查询
//			if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
//				// 把历史值更新为当前值
//				queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
//				alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory':factory,'alarmName':queryEvent.combo.getRawValue(),'type': 'current'})};
//				alarmNameStore.load();
//				return false;
//			}
//		},
//		  scope : this
//		}
});

/**
 * 创建告警名称多选框
 */
var alarmName = {
	xtype : 'fieldset',
	title : '告警名称',
	style : 'margin-left:10px;',
	height : 290,
	width : 520,
	labelWidth : 60,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			border : false,
			items : [{
				layout : 'form',
				border : false,
				disabled : true,
				style : 'margin-top:30px;',
				items : factoryCombo
			},{
				layout : 'form',
				border : false,
				style : 'margin-top:20px;',
				disabled : true,
				items : alarmNameCombo
			}]
		}, {
			layout : 'form',
			border : false,
			width : 30,
			style : 'margin-left:10px;margin-top:77px;',
			items : {
				xtype : 'button',
				disabled : true,
				text : '>>'
			}
		}, {
			layout : 'form',
			border : false,
			width : 250,
			style : 'margin-left:10px;',
			items :gridPanel
		}]
	}]
};

/**
 * 创建告警类型多选框
 */
var alarmType = {
	id : 'alarmType',
	xtype : 'fieldset',
	title : '告警类型',
	style : 'margin-left:10px;',
	height : 200,
	width : 150,
	items : [{
		layout : 'column',
		border : false,
		disabled : true,
		items : [{
			layout : 'form',
			border : false,
			columnWidth : .5,
			defaultType: 'checkbox',
			labelWidth : 1,
			items : [{
				boxLabel : '通信',
				style : 'margin-top:10px;',
				inputValue : 0,
				checked : false
			}, {
				boxLabel : '处理',
				style : 'margin-top:20px;',
				inputValue : 3,
				checked : false
			}, {
				boxLabel : '服务',
				style : 'margin-top:20px;',
				inputValue : 1,
				checked : false
			}, {
				boxLabel : '连接',
				style : 'margin-top:20px;',
				inputValue : 6,
				checked : false
			}]
		},{
			layout : 'form',
			border : false,
			columnWidth : .5,
			defaultType: 'checkbox',
			labelWidth : 1,
			items : [{
				boxLabel : '设备',
				style : 'margin-top:10px;',
				inputValue : 2,
				checked : false
			}, {
				boxLabel : '环境',
				style : 'margin-top:20px;',
				inputValue : 4,
				checked : false
			}, {
				boxLabel : '安全',
				style : 'margin-top:20px;',
				inputValue : 5,
				checked : false
			}]
		}]
	}]
};

/**
 * 创建告警级别多选框
 */
var alarmLevel = {
	id : 'alarmLevel',
	xtype : 'fieldset',
	title : '级别',
	style : 'margin-left:10px;',
	defaultType: 'checkbox',
	height : 200,
	width : 100,
	labelWidth : 1,
	items : [ {
		boxLabel : '紧急',
		disabled : true,
		style : 'margin-top:10px;',
		inputValue : '1',
		checked : false
	}, {
		boxLabel : '重要',
		disabled : true,
		style : 'margin-top:20px;',
		inputValue : '2',
		checked : false
	}, {
		boxLabel : '次要',
		disabled : true,
		style : 'margin-top:20px;',
		inputValue : '3',
		checked : false
	}, {
		boxLabel : '提示',
		disabled : true,
		style : 'margin-top:20px;',
		inputValue : '4',
		checked : false
	}]
};

/**
 * 创建业务影响多选框
 */
var affectType = {
	id : 'affectType',
	xtype : 'fieldset',
	title : '业务影响',
	style : 'margin-left:10px;',
	defaultType: 'checkbox',
	height : 70,
	width : 260,
	layout : 'hbox',
	labelWidth : 1,
	items : [ {
		boxLabel : '<span><font size="2">影响</font></span>',
		disabled : true,
		style : 'margin-left:20px;',
		inputValue : 1,
		checked : false
	}, {
		boxLabel : '<span><font size="2">不影响</font></span>',
		disabled : true,
		style : 'margin-left:20px;',
		inputValue : 2,
		checked : false
	}, {
		boxLabel : '<span><font size="2">未知</font></span>',
		disabled : true,
		style : 'margin-left:20px;',
		inputValue : 3,
		checked : false
	}]
};

/**
 * 创建border布局的center
 */
var centerPanel = new Ext.FormPanel({
	region : 'center',
	autoScroll:true,
	width:800,
	items : [ {
		title : '',
		layout : 'form',
		border : false,
		width:800,
		items : [{
			layout : 'column',
			border : false,
			style : 'margin-top:10px;',
			items : [{
				layout : 'form',
				border : false,
				items : alarmName
			},{
				layout : 'form',
				border : false,
				items : [{
					layout : 'column',
					border : false,
					items : [{
						layout : 'form',
						border : false,
						items : alarmType
					},{
						layout : 'form',
						border : false,
						items : alarmLevel
					}]
				},{
					layout : 'form',
					border : false,
					items : affectType
				}]
			}]
		},{
			layout : 'column',
			border : false,
			items : [{
				layout : 'form',
				border : false,
				width : 500,
				labelWidth : 80,
				style : 'margin-left:10px;',
				items : {
					id : 'alarmResource',
					xtype : 'textfield',
					fieldLabel : '告警源选择',
					disabled : true,
	    			width : 400
				}
			},{
				layout : 'form',
				border : false,
				items :{
					border : false,
					disabled : true,
					html : '<img src="../../resource/images/btnImages/setTask.png" onclick="alarmSourceClick()" style="cursor:hand"></img>'
				}
			}]
		},{
			layout : 'column',
			border : false,
			items : [{
				layout : 'form',
				border : false,
				width : 250,
				labelWidth : 80,
				style : 'margin-left:10px;margin-top:10px;',
				items : {
					id : 'shieldName',
					xtype : 'textfield',
					fieldLabel : '屏蔽模板名称',
					disabled : true,
//					allowBlank : false,
//					maxLength:50,                
//					maxLengthText:'最多可输入50个字符',
//	    			blankText:'不能为空',
	    			width : 150
				}
			},{
				layout : 'form',
				border : false,
				width : 530,
				labelWidth : 35,
				style : 'margin-left:10px;margin-top:10px;',
				items : {
					id : 'shieldDesc',
					xtype : 'textfield',
					fieldLabel : '描述',
					disabled : true,
//					allowBlank : false,
//					maxLength:100,                
//					maxLengthText:'最多可输入100个字符',
//	    			blankText:'不能为空',
					width : 490
				}
			}]
		}]
	}],
	buttons:[{
		text:'取消',
		handler : function(){
			var win = parent.Ext.getCmp('viewAlarmShieldWindow');
			if(win){
				win.close();
			}
		}
	}]
});

/**
 * 告警源选择设置
 */
function alarmSourceClick(){
	parent.viewAlarmShieldResourceWindow(shieldId);
}


/**
 * 初始化窗口
 */
function initData(){
	// 告警名称->厂家
	Ext.getCmp('factoryCombo').setValue(parent.factoryValue);
	Ext.Ajax.request({
	    url: 'fault!getAlarmShieldDetailById.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'shieldId':shieldId,'flag':'first'})},
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	// 屏蔽器名称
	    	Ext.getCmp('shieldName').setValue(obj.main.SHIELD_NAME);
	    	// 屏蔽器描述
	    	Ext.getCmp('shieldDesc').setValue(obj.main.DESCRIPTION);
	    	// 告警源设置
	    	var neName = '';
	    	var resourceArr = obj.resourceName;
	    	for ( var i = 0; i < resourceArr.length; i++) {
	    		neName += resourceArr[i].DEVICE_NAME + ',';
			}
	    	neName = neName.substring(0,neName.lastIndexOf(','));
	    	Ext.getCmp('alarmResource').setValue(neName);
	    	// 已选告警名称
	    	var nameArr = obj.name;
	    	for ( var i = 0; i < nameArr.length; i++) {
	    		// 定义表格的一条记录
		    	var record = new Ext.data.Record(['FACTORY_ID','FACTORY','ALARM_NAME']); 
		    	record.set('FACTORY_ID',nameArr[i].FACTORY);
		    	record.set('ALARM_NAME',nameArr[i].NATIVE_PROBABLE_CAUSE);
		    	record.set('FACTORY',nameArr[i].FACTORY);
		    	for(var fac in FACTORY){
		    		if(nameArr[i].FACTORY==FACTORY[fac]['key']){
			    		record.set('FACTORY',FACTORY[fac]['value']);
			    		break;
			    	}
		    	}
		    	// 向表格里添加值
		    	gridPanel.getStore().add(record);
			}
	    	// 告警类型
	    	var alarmTypeSelect = obj.type;
	    	var alarmType = Ext.getCmp('alarmType').items.get(0).items;
	    	for ( var i = 0; i < alarmTypeSelect.length; i++) {
	    		for ( var m = 0; m < alarmType.length; m++) {
					var alarmTypeChild = alarmType.get(m).items;
					for ( var n = 0; n < alarmTypeChild.length; n++) {
						if(alarmTypeSelect[i].ALARM_TYPE==alarmTypeChild.get(n).inputValue){
							alarmTypeChild.get(n).setValue(true);
						}
					}
				}
			}
	    	// 告警级别
	    	var alarmLevelSelect = obj.level;
			var alarmLevel = Ext.getCmp('alarmLevel').items;
			for ( var j = 0; j < alarmLevelSelect.length; j++) {
				for ( var i = 0; i < alarmLevel.length; i++) {
					if(alarmLevelSelect[j].ALARM_LEVEL==alarmLevel.get(i).inputValue){
						alarmLevel.get(i).setValue(true);
					}
				}
			}
			// 业务影响
			var affectTypeSelect = obj.affect;
			var affectType = Ext.getCmp('affectType').items;
			for ( var i = 0; i < affectTypeSelect.length; i++) {
				for ( var b = 0; b < affectType.length; b++) {
					if(affectTypeSelect[i].ALARM_AFFECTING==affectType.get(b).inputValue){
						affectType.get(b).setValue(true);
					}
				}
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

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	initData();
  	new Ext.Viewport({
        layout : 'border',
        items : centerPanel
	});
 });
