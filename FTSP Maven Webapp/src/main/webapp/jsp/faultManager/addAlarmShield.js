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
	width : 130,
	listeners : {
		select : function(combo, record, index) {
			var factory = record.get('value');
			// 还原告警名称下拉框
			Ext.getCmp('alarmNameCombo').reset();
			// 动态改变告警名称数据源的参数
			alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory' : factory,'alarmName':Ext.getCmp('alarmNameCombo').getValue(),'type': 'current'})};
			// 加载告警名称数据源
			alarmNameStore.load({
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
	width : 130,
	triggerAction : 'all',
	resizable: true,
	listeners : {
		beforequery:function(queryEvent){// 每次输入后触发
			// 获取厂家下拉框的值
			var factory = Ext.getCmp('factoryCombo').getValue();
			// 获取告警名称下拉框输入的值，和历史的内容比较，如果不相同，则取后台模糊查询
			if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
				// 把历史值更新为当前值
				queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
				alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory':factory,'alarmName':queryEvent.combo.getRawValue(),'type': 'current'})};
				alarmNameStore.load();
				return false;
			}
		},
		  scope : this
		}
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
				style : 'margin-top:30px;',
				items : factoryCombo
			},{
				layout : 'form',
				border : false,
				style : 'margin-top:20px;',
				items : alarmNameCombo
			}]
		}, {
			layout : 'form',
			border : false,
			width : 30,
			style : 'margin-left:10px;margin-top:30px;',
			items : [{
				xtype : 'button',
				text : '>>',
				width : 30,
				handler : function(){
					rightAdd();
				}
				},{
				xtype : 'button',
				style : 'margin-top:20px;',
				text : '<<<',
				width : 30,
				handler : function(){
					rightMove();
				}
			}]
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
		style : 'margin-top:10px;',
		inputValue : '1',
		checked : false
	}, {
		boxLabel : '重要',
		style : 'margin-top:20px;',
		inputValue : '2',
		checked : false
	}, {
		boxLabel : '次要',
		style : 'margin-top:20px;',
		inputValue : '3',
		checked : false
	}, {
		boxLabel : '提示',
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
		style : 'margin-left:20px;',
		inputValue : 1,
		checked : false
	}, {
		boxLabel : '<span><font size="2">不影响</font></span>',
		style : 'margin-left:20px;',
		inputValue : 2,
		checked : false
	}, {
		boxLabel : '<span><font size="2">未知</font></span>',
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
					readOnly : true,
	    			width : 400
				}
			},{
				layout : 'form',
				border : false,
				items :{
					border : false,
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
					fieldLabel : '屏蔽模板名称<span style="color:red">*</span>',
					allowBlank : false,
					labelSeparator : ' ', //表单label与其他元素朋分符
					maxLength:50,                
					maxLengthText:'最多可输入50个字符',
	    			blankText:'不能为空',
	    			width : 150
				}
			},{
				layout : 'form',
				border : false,
				width : 500,
				labelWidth : 35,
				style : 'margin-left:10px;margin-top:10px;',
				items : {
					id : 'shieldDesc',
					xtype : 'textfield',
					labelSeparator : ' ', //表单label与其他元素朋分符
					fieldLabel : '描述<span style="color:red">*</span>',
					allowBlank : false,
					maxLength:100,                
					maxLengthText:'最多可输入100个字符',
	    			blankText:'不能为空',
					width : 450
				}
			}]
		}]
	}],
	buttons:[{
		text:'确定',
		handler : function(){
			// 已选告警名称
			var alarmNameCheck = '';
			var gridRecords = gridPanel.getStore().getRange();
			for ( var i = 0; i < gridRecords.length; i++) {
				alarmNameCheck += gridRecords[i].get('FACTORY_ID') + "," + gridRecords[i].get('ALARM_NAME') + "=";
			}
			alarmNameCheck = alarmNameCheck.substring(0, alarmNameCheck.lastIndexOf("="));
			// 告警类型
			var alarmTypeCheck = '';
			var alarmType = Ext.getCmp('alarmType').items.get(0).items;
			for ( var m = 0; m < alarmType.length; m++) {
				var alarmTypeChild = alarmType.get(m).items;
				for ( var n = 0; n < alarmTypeChild.length; n++) {
					if(alarmTypeChild.get(n).checked){
						alarmTypeCheck += alarmTypeChild.get(n).inputValue + ',';
					}
				}
			}
			alarmTypeCheck = alarmTypeCheck.substring(0, alarmTypeCheck.lastIndexOf(","));
			// 告警级别
			var alarmLevelCheck = '';
			var alarmLevel = Ext.getCmp('alarmLevel').items;
			for ( var i = 0; i < alarmLevel.length; i++) {
				if(alarmLevel.get(i).checked){
					alarmLevelCheck += alarmLevel.get(i).inputValue + ',';
				}
			}
			alarmLevelCheck = alarmLevelCheck.substring(0, alarmLevelCheck.lastIndexOf(","));
			// 业务影响
			var affectTypeCheck = '';
			var affectType = Ext.getCmp('affectType').items;
			for ( var b = 0; b < affectType.length; b++) {
				if(affectType.get(b).checked){
					affectTypeCheck += affectType.get(b).inputValue + ',';
				}
			}
			affectTypeCheck = affectTypeCheck.substring(0, affectTypeCheck.lastIndexOf(","));
			// 过滤器名称
			var shieldName = Ext.getCmp('shieldName').getValue();
			// 过滤器描述
			var shieldDesc = Ext.getCmp('shieldDesc').getValue();
			if(!centerPanel.getForm().isValid()){
				return false;
			}
			if(type=='modify'){
				Ext.Ajax.request({
				    url: 'fault!modifyShieldFilter.action',
				    method: 'POST',
				    params: {
				    	'jsonString' : Ext.encode({'shieldId':shieldId,'alarmName':alarmNameCheck,'alarmType':alarmTypeCheck,'alarmLevel':alarmLevelCheck,'affectType':affectTypeCheck,
				    		'shieldName':shieldName,'shieldDesc':shieldDesc,'resourceSelectIds':parent.modifyResourceSelectIds,'resourceSelectLvs':parent.modifyResourceSelectLvs})
				    },
				    success : function(response) {
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							parent.store.load({
								callback : function(records,options,success){
									if (!success) {
										Ext.Msg.alert('错误', '查询失败！请重新查询');
									}
								}
							});
							// 关闭新增窗口
							var win = parent.Ext.getCmp('addAlarmShieldWindow');
							if(win){
								win.close();
							}
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("信息", obj.returnMessage);
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
			}else{
				Ext.Ajax.request({
				    url: 'fault!addAlarmShield.action',
				    method: 'POST',
				    params: {
				    	'jsonString' : Ext.encode({'alarmName':alarmNameCheck,'alarmType':alarmTypeCheck,'alarmLevel':alarmLevelCheck,'affectType':affectTypeCheck,
				    		'shieldName':shieldName,'shieldDesc':shieldDesc,'resourceSelectIds':parent.resourceSelectIds,'resourceSelectLvs':parent.resourceSelectLvs})
				    },
				    success : function(response) {
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							parent.store.reload({
								callback : function(records,options,success){
									if (!success) {
										Ext.Msg.alert('错误', '查询失败！请重新查询');
									}
								}
							});
							// 关闭新增窗口
							var win = parent.Ext.getCmp('addAlarmShieldWindow');
							if(win){
								win.close();
							}
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("信息", obj.returnMessage);
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
		}
	},{
		text:'重置',
		handler : function(){
			centerPanel.getForm().reset();
			// 获取已选告警名称表格所有记录
			var gridRecords = gridPanel.getStore().getRange();
			// 循环删除
			for ( var i = 0; i < gridRecords.length; i++) {
				gridPanel.getStore().remove(gridRecords[i]);
			}
			if(type=='modify'){
		    	// 屏蔽器名称
		    	Ext.getCmp('shieldName').setValue(parent.alarmShieldName);
		    	// 屏蔽器描述
		    	Ext.getCmp('shieldDesc').setValue(parent.alarmShieldDesc);
		    	// 告警源设置
		    	Ext.getCmp('alarmResource').setValue(parent.modifyNeName);
		    	// 已选告警名称
		    	var nameArr = parent.alarmNameSelect.split('=');
		    	for ( var i = 0; i < nameArr.length; i++) {
		    		// 定义表格的一条记录
			    	var record = new Ext.data.Record(['FACTORY_ID','FACTORY','ALARM_NAME']); 
			    	record.set('FACTORY_ID',nameArr[i].split(',')[0]);
			    	record.set('FACTORY',nameArr[i].split(',')[1]);
			    	record.set('ALARM_NAME',nameArr[i].split(',')[2]);
			    	// 向表格里添加值
			    	gridPanel.getStore().add(record);
				}
		    	// 告警类型
		    	var alarmTypeSelect = parent.alarmTypeSelect.split(',');
		    	var alarmType = Ext.getCmp('alarmType').items.get(0).items;
		    	for ( var i = 0; i < alarmTypeSelect.length; i++) {
		    		for ( var m = 0; m < alarmType.length; m++) {
						var alarmTypeChild = alarmType.get(m).items;
						for ( var n = 0; n < alarmTypeChild.length; n++) {
							if(alarmTypeSelect[i]==alarmTypeChild.get(n).inputValue){
								alarmTypeChild.get(n).setValue(true);
							}
						}
					}
				}
		    	// 告警级别
		    	var alarmLevelSelect = parent.alarmLevelSelect.split(',');
				var alarmLevel = Ext.getCmp('alarmLevel').items;
				for ( var j = 0; j < alarmLevelSelect.length; j++) {
					for ( var i = 0; i < alarmLevel.length; i++) {
						if(alarmLevelSelect[j]==alarmLevel.get(i).inputValue){
							alarmLevel.get(i).setValue(true);
						}
					}
				}
				// 业务影响
				var affectTypeSelect = parent.alarmAffectSelect.split(',');
				var affectType = Ext.getCmp('affectType').items;
				for ( var i = 0; i < affectTypeSelect.length; i++) {
					for ( var b = 0; b < affectType.length; b++) {
						if(affectTypeSelect[i]==affectType.get(b).inputValue){
							affectType.get(b).setValue(true);
						}
					}
				}
			}else{
				Ext.getCmp('alarmResource').setValue(parent.neName);
			}	
		}
	},{
		text:'取消',
		handler : function(){
			if(type=='modify'){
				// 清空所有操作数据
				parent.alarmShieldName = '';
				parent.alarmShieldDesc = '';
				parent.alarmNameSelect = '';
				parent.alarmTypeSelect = '';
				parent.alarmLevelSelect = '';
				parent.alarmAffectSelect = '';
				parent.modifyNeName = '';
				parent.modifyResourceSelectIds = '';
				parent.modifyResourceSelectLvs = '';
				parent.modifyShieldFlag = '';
			}else{
				// 告警源
				parent.resourceSelectIds = '';
				parent.resourceSelectLvs = '';
				parent.neName = '';
			}
			var win = parent.Ext.getCmp('addAlarmShieldWindow');
			if(win){
				win.close();
			}
		}
	}]
});

/**
 * 告警名称右移
 */
function rightAdd(){
	// 定义表格的一条记录
	var record = new Ext.data.Record(['FACTORY_ID','FACTORY','ALARM_NAME']); 
	// 厂家名称
	var factory = Ext.getCmp('factoryCombo').getValue();
	var factoryDisplay = Ext.getCmp('factoryCombo').getRawValue();
	// 告警名称
	var alarmName = Ext.getCmp('alarmNameCombo').getValue();
	if(alarmName==''){
		Ext.Msg.alert('提示','请选择告警名称');
		return false;
	}
	// 已选择的告警名称
	var recordSelect = gridPanel.getStore().getRange();
	var count = 0;
	// 如果右边已经存在的，则不能右移
	for ( var i = 0; i < recordSelect.length; i++) {
		if(factory==recordSelect[i].get('FACTORY_ID')&&alarmName==recordSelect[i].get('ALARM_NAME')){
			count ++;
		}
	}
	if(count==0){
		record.set('FACTORY_ID',factory);
		record.set('FACTORY',factoryDisplay);
		record.set('ALARM_NAME',alarmName);
		// 向表格里添加值
		gridPanel.getStore().add(record);
	}
}


/**
 * 告警名称左移
 */
function rightMove(){
	var rows = gridPanel.getSelectionModel().getSelections();
	if(rows==undefined || rows==null){
		return;
	}
	gridPanel.getStore().remove(rows[0]);
}

/**
 * 告警源选择设置
 */
function alarmSourceClick(){
	if(type=='modify'){
		parent.modifyAlarmShieldResourceWindow(shieldId);
	}else{
		parent.addAlarmShieldResourceWindow();
	}
}


/**
 * 初始化窗口
 */
function initData(){
	// 告警名称->厂家
	Ext.getCmp('factoryCombo').setValue(parent.factoryValue);
	if(type=="modify"){
		Ext.Ajax.request({
		    url: 'fault!getAlarmShieldDetailById.action',
		    method: 'POST',
		    params: {'jsonString':Ext.encode({'shieldId':shieldId,'flag':'first'})},
		    success : function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	// 屏蔽器名称
		    	Ext.getCmp('shieldName').setValue(obj.main.SHIELD_NAME);
		    	parent.alarmShieldName = obj.main.SHIELD_NAME;
		    	// 屏蔽器描述
		    	Ext.getCmp('shieldDesc').setValue(obj.main.DESCRIPTION);
		    	parent.alarmShieldDesc = obj.main.DESCRIPTION;
		    	// 告警源设置
		    	// 这里清空，避免右键刷新的时候(父页面的数据会增多)
		    	parent.modifyNeName = '';
		    	var resourceArr = obj.resourceName;
		    	for ( var i = 0; i < resourceArr.length; i++) {
					parent.modifyNeName += resourceArr[i].DEVICE_NAME + ',';
				}
		    	parent.modifyNeName = parent.modifyNeName.substring(0,parent.modifyNeName.lastIndexOf(','));
		    	Ext.getCmp('alarmResource').setValue(parent.modifyNeName);
		    	// 这里清空，避免右键刷新的时候(父页面的数据会增多)
		    	parent.alarmNameSelect = '';
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
			    	parent.alarmNameSelect += record.get('FACTORY_ID')+','+record.get('FACTORY')+','+record.get('ALARM_NAME')+'='
			    	// 向表格里添加值
			    	gridPanel.getStore().add(record);
				}
		    	parent.alarmNameSelect = parent.alarmNameSelect.substring(0,parent.alarmNameSelect.lastIndexOf("="));
		    	// 告警类型
		    	parent.alarmTypeSelect = '';
		    	var alarmTypeSelect = obj.type;
		    	var alarmType = Ext.getCmp('alarmType').items.get(0).items;
		    	for ( var i = 0; i < alarmTypeSelect.length; i++) {
		    		for ( var m = 0; m < alarmType.length; m++) {
						var alarmTypeChild = alarmType.get(m).items;
						for ( var n = 0; n < alarmTypeChild.length; n++) {
							if(alarmTypeSelect[i].ALARM_TYPE==alarmTypeChild.get(n).inputValue){
								alarmTypeChild.get(n).setValue(true);
								parent.alarmTypeSelect += alarmTypeSelect[i].ALARM_TYPE+',';
							}
						}
					}
				}
		    	parent.alarmTypeSelect = parent.alarmTypeSelect.substring(0,parent.alarmTypeSelect.lastIndexOf(","));
		    	// 告警级别
		    	parent.alarmLevelSelect = '';
		    	var alarmLevelSelect = obj.level;
				var alarmLevel = Ext.getCmp('alarmLevel').items;
				for ( var j = 0; j < alarmLevelSelect.length; j++) {
					for ( var i = 0; i < alarmLevel.length; i++) {
						if(alarmLevelSelect[j].ALARM_LEVEL==alarmLevel.get(i).inputValue){
							alarmLevel.get(i).setValue(true);
							parent.alarmLevelSelect += alarmLevelSelect[j].ALARM_LEVEL+',';
						}
					}
				}
				parent.alarmLevelSelect = parent.alarmLevelSelect.substring(0,parent.alarmLevelSelect.lastIndexOf(","));
				// 业务影响
				parent.alarmAffectSelect = '';
				var affectTypeSelect = obj.affect;
				var affectType = Ext.getCmp('affectType').items;
				for ( var i = 0; i < affectTypeSelect.length; i++) {
					for ( var b = 0; b < affectType.length; b++) {
						if(affectTypeSelect[i].ALARM_AFFECTING==affectType.get(b).inputValue){
							affectType.get(b).setValue(true);
							parent.alarmAffectSelect += affectTypeSelect[i].ALARM_AFFECTING+',';
						}
					}
				}
				parent.alarmAffectSelect = parent.alarmAffectSelect.substring(0,parent.alarmAffectSelect.lastIndexOf(","));
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
	}else{
		if(alarmIds!=''&&alarmIds!='null'){
			Ext.Ajax.request({
				url: 'fault!getAlarmByIds.action',
			    method: 'POST',
			    params: {
			    	'jsonString' : Ext.encode({'alarmIds':alarmIds,'tabFlag':tabFlag})// 把对象转成JSON格式字符串
			    },
			    success: function(response) {
			    	var obj = Ext.decode(response.responseText);
			    	var nameArr=obj.rows;
			    	for ( var i = 0; i < nameArr.length; i++) {
			    		var record = new Ext.data.Record(['FACTORY_ID','FACTORY','ALARM_NAME']); 
				    	record.set('FACTORY_ID',nameArr[i].FACTORY);
				    	record.set('ALARM_NAME',nameArr[i].NATIVE_PROBABLE_CAUSE);
				    	for(var fac in FACTORY){
				    		if(nameArr[i].FACTORY==FACTORY[fac]['key']){
					    		record.set('FACTORY',FACTORY[fac]['value']);
					    		break;
					    	}
				    	}
				    	// 向表格里添加值
				    	gridPanel.getStore().add(record);
					}
			    },
			    error:function(response) {
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("错误",response.responseText);
			    },
			    failure:function(response) {
			    	top.Ext.getBody().unmask();
		        	Ext.Msg.alert("错误",response.responseText);
			    }
			});
		}
	}
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
