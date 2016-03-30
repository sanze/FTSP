/**
 * 定义全局变量
 */
//var modifyNeModelSelect = parent.modifyNeModelSelect;
//var modifyPortModelSelect = parent.modifyPortModelSelect;
//var modifyPtpAlarmStatus = parent.modifyPtpAlarmStatus;
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
			// 动态改变网元型号数据源的参数
			leftGridStore.baseParams = {'jsonString':Ext.encode({'factory' : factory})};
			// 加载网元型号数据源
			leftGridStore.load({
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
 * 创建左侧表格数据源
 */
var leftGridStore = new Ext.data.Store({
	url : 'fault!getAllNeModelByFactory.action', 
	baseParams : {'jsonString':Ext.encode({'factory' : parent.factoryValue})},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields :['BASE_PRODUCT_MAPPING_ID','PRODUCT_NAME']
	})
});

/**
 * 加载左侧表格数据源
 */
leftGridStore.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

/**
 * 创建左侧表格列模型
 */
var leftCm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [{
		header : '网元型号ID',
		dataIndex : 'BASE_PRODUCT_MAPPING_ID',
		width : 10,
		hidden : true
	},{
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 150
	}]
});

/**
 * 创建左侧表格
 */
var leftGridPanel = new Ext.grid.GridPanel({
	id : 'leftGridPanel',	
	height : 270, 
	store : leftGridStore,
//	loadMask : true,
	cm : leftCm,
	animCollapse : false,
	frame : false,
//	stripeRows : true, 
	view : new Ext.grid.GridView(),
});

/**
 * 创建右侧表格列模型
 */
var rightCm = new Ext.grid.ColumnModel({
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
		width : 100
	}, {
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 150
	},{
		header : '网元型号id',
		dataIndex : 'BASE_PRODUCT_MAPPING_ID',
		width : 80,
		hidden : true
	}]
});

/**
 * 创建右侧表格
 */
var rightGridPanel = new Ext.grid.GridPanel({
	id : 'rightGridPanel',	
	title :'已选网元型号',
	height : 305, 
	store : new Ext.data.ArrayStore({
		fields : [{name:'FACTORY_ID'},{name:'FACTORY'},{name:'PRODUCT_NAME'},{name:'BASE_PRODUCT_MAPPING_ID'}],
		data : []
	}),
	loadMask : true,
	cm : rightCm,
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	view : new Ext.grid.GridView(),
});

/**
 * 创建网元型号多选框
 */
var neModel = {
	xtype : 'fieldset',
	title : '网元型号',
	style : 'margin-left:10px;',
	height : 350,
	width : 540,
	labelWidth : 40,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			border : false,
			items : [{
				layout : 'form',
				border : false,
				items : factoryCombo
			},{
				layout : 'form',
				border : false,
				style : 'margin-top:20px;',
				items : leftGridPanel
			}]
		}, {
			layout : 'form',
			border : false,
			width : 30,
			style : 'margin-left:13px;margin-top:77px;',
			items :[{
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
			width : 280,
			style : 'margin-left:10px;',
			items :rightGridPanel
		}]
	}]
};

/**
 * 创建端口型号多选框
 */
var portModel = {
	id : 'portModel',
	xtype : 'fieldset',
	title : '端口型号',
	style : 'margin-left:10px;',
	defaultType: 'checkbox',
	height : 320,
	width : 200,
	labelWidth : 30,
	items : [ {
		boxLabel : '光传送单元',
		inputValue : 'OTS',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : '光复用单元',
		inputValue : 'OMS',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : '光通道',
		inputValue : 'OCH',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : '光监控信道',
		inputValue : 'OSC&OSCNI',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : 'STM-64及以上',
		inputValue : 'STM-64~STM-256',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : 'STM-16',
		inputValue : 'STM-16',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : 'STM-4',
		inputValue : 'STM-4',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : 'STM-1',
		inputValue : 'STM-1',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : 'E1',
		inputValue : 'E1',
		style : 'margin-top:10px;',
		checked : false
	}, {
		boxLabel : 'E3',
		inputValue : 'E3',
		style : 'margin-top:10px;',
		checked : false
	}]
};

/**
 * 创建border布局的center部分
 */
var centerPanel = new Ext.FormPanel({
	border : false,
	region : 'center',
	layout : 'column',
	labelWidth : 5,
	items : [{
		layout : 'form',
		border : false,
		style : 'margin-top:10px;',
		items : neModel
	},{
		layout : 'form',
		border : false,
		style : 'margin-top:10px;margin-left:30px;',
		items : [portModel,{
			id : 'ctpAlarm',
			xtype : 'checkbox',
			boxLabel : '通道告警',
			inputValue : 'ctpAlarm',
			checked : false
		}]
	}]
});

/**
 * 创建border布局的south部分
 */
var southPanel = new Ext.Panel({
	region : 'south',
	buttons:[{
		text:'确定',
		handler : function(){
			// 网元型号
			var records = rightGridPanel.getStore().getRange();
			if(type=='modify'){
				// 先清空原来的值，否则会在原有值的基础上直接相加
				parent.modifyNeModelSelect = '';
				for ( var i = 0; i < records.length; i++) {
					parent.modifyNeModelSelect += records[i].get('FACTORY_ID') + "," + records[i].get('FACTORY') + "," + records[i].get('PRODUCT_NAME') + "," + records[i].get('BASE_PRODUCT_MAPPING_ID') + ";";
				}
				parent.modifyNeModelSelect = parent.modifyNeModelSelect.substring(0, parent.modifyNeModelSelect.lastIndexOf(";"));
				// 端口型号
				parent.modifyPortModelSelect = '';
				var portModel = Ext.getCmp('portModel').items;
				for ( var i = 0; i < portModel.length; i++) {
					if(portModel.get(i).checked){
						parent.modifyPortModelSelect += portModel.get(i).inputValue + ',';
					}
				}
				parent.modifyPortModelSelect = parent.modifyPortModelSelect.substring(0, parent.modifyPortModelSelect.lastIndexOf(","));
				// 通道告警状态
				parent.modifyPtpAlarmStatus = Ext.getCmp('ctpAlarm').checked;
			}else{
				// 先清空原来的值，否则会在原有值的基础上直接相加
				parent.neModelSelect = '';
				for ( var i = 0; i < records.length; i++) {
					parent.neModelSelect += records[i].get('FACTORY_ID') + "," + records[i].get('FACTORY') + "," + records[i].get('PRODUCT_NAME') + "," + records[i].get('BASE_PRODUCT_MAPPING_ID') + ";";
				}
				parent.neModelSelect = parent.neModelSelect.substring(0, parent.neModelSelect.lastIndexOf(";"));
				// 端口型号
				parent.portModelSelect = '';
				var portModel = Ext.getCmp('portModel').items;
				for ( var i = 0; i < portModel.length; i++) {
					if(portModel.get(i).checked){
						parent.portModelSelect += portModel.get(i).inputValue + ',';
					}
				}
				parent.portModelSelect = parent.portModelSelect.substring(0, parent.portModelSelect.lastIndexOf(","));
				// 通道告警状态
				parent.ptpAlarmStatus = Ext.getCmp('ctpAlarm').checked;
			}
			// 关闭告警源选择窗口
			var win = parent.Ext.getCmp('currentAlarmFilterResourceTypeWindow');
			if(win){
				win.close();
			}
		}
	},{
		text:'重置',
		handler : function(){
			centerPanel.getForm().reset();
			// 动态改变网元型号数据源的参数
			leftGridStore.baseParams = {'jsonString':Ext.encode({'factory' : parent.factoryValue})};
			// 加载网元型号数据源
			leftGridStore.load({
				callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
			});
			var gridRecords = rightGridPanel.getStore().getRange();
			// 循环删除
			for ( var i = 0; i < gridRecords.length; i++) {
				rightGridPanel.getStore().remove(gridRecords[i]);
			}
			// 获取右侧已选的数据
			//var rightRecords = rightGridPanel.getStore().getRange();
			if(type=='modify'){
				// 之前已保存的数据
				var neModelSelect = parent.modifyNeModelSelect.split(";");
				/*for ( var i = 0; i < rightRecords.length; i++) {
					var count = 0 ;
					for ( var j = 0; j < neModelSelectArr.length; j++) {
						if(neModelSelectArr[j].split(",")[0]==rightRecords[i].get('FACTORY_ID')&&neModelSelectArr[j].split(",")[2]==rightRecords[i].get('PRODUCT_NAME')){
							count ++;
						}
					}
					if(count==0){
						rightGridPanel.getStore().remove(rightRecords[i]);
					}
				}*/
				
				for ( var i = 0; i < neModelSelect.length; i++) {
					// 定义右侧表格的一条记录
					var record = new Ext.data.Record(['FACTORY_ID','FACTORY','PRODUCT_NAME','BASE_PRODUCT_MAPPING_ID']);
					record.set('FACTORY_ID',neModelSelect[i].split(',')[0]);
					record.set('FACTORY',neModelSelect[i].split(',')[1]);
					record.set('PRODUCT_NAME',neModelSelect[i].split(',')[2]);
					record.set('BASE_PRODUCT_MAPPING_ID',neModelSelect[i].split(',')[3]);
					rightGridPanel.getStore().add(record);
				}
				
				// 端口型号
				var portModelCheck = parent.modifyPortModelSelect.split(',');
				var portModel = Ext.getCmp('portModel').items;
				if(portModelCheck!=''){
					for ( var i = 0; i < portModel.length; i++) {
						for ( var j = 0; j < portModelCheck.length; j++) {
							if(portModel.get(i).inputValue==portModelCheck[j]){
								portModel.get(i).setValue(true);
							}
						}
					}
				}
				// 通道告警状态
				var ptpAlarmStatusSelect = parent.modifyPtpAlarmStatus;
				Ext.getCmp('ctpAlarm').setValue(parent.modifyPtpAlarmStatus);
				
			}else{
				// 之前已保存的数据
				var neModelSelectArr = parent.neModelSelect.split(";");
				for ( var i = 0; i < rightRecords.length; i++) {
					var count = 0 ;
					for ( var j = 0; j < neModelSelectArr.length; j++) {
						if(neModelSelectArr[j].split(",")[0]==rightRecords[i].get('FACTORY_ID')&&neModelSelectArr[j].split(",")[2]==rightRecords[i].get('PRODUCT_NAME')){
							count ++;
						}
					}
					if(count==0){
						rightGridPanel.getStore().remove(rightRecords[i]);
					}
				}
			}
		}
	},{
		text:'取消',
		handler : function(){
			// 关闭告警源选择窗口
			var win = parent.Ext.getCmp('currentAlarmFilterResourceTypeWindow');
			if(win){
				win.close();
			}
		}
	}]
});

/**
 * 右移
 */
function rightAdd(){
	// 获取左侧表格选择的数据
	var records = leftGridPanel.getSelectionModel().getSelections();
	// 获取右侧已选的数据
	var rightRecords = rightGridPanel.getStore().getRange();
	// 厂家名称
	var factory = Ext.getCmp('factoryCombo').getValue();
	var factoryDisplay = Ext.getCmp('factoryCombo').getRawValue();
	for ( var i = 0; i < records.length; i++) {
		// 通过计数值判断左侧选择的是否已经在右侧存在，如果不存在侧移到右边，否则不处理
		var count = 0 ;
		for ( var j = 0; j < rightRecords.length; j++) {
			if(factory==rightRecords[j].get('FACTORY_ID')&&records[i].get('PRODUCT_NAME')==rightRecords[j].get('PRODUCT_NAME')){
				count ++ ;
			}
		}
		if(count==0){
			// 定义右侧表格的一条记录
			var record = new Ext.data.Record(['FACTORY_ID','FACTORY','PRODUCT_NAME','BASE_PRODUCT_MAPPING_ID']); 
			record.set('FACTORY_ID',factory);
			record.set('FACTORY',factoryDisplay);
			record.set('PRODUCT_NAME',records[i].get('PRODUCT_NAME'));
			record.set('BASE_PRODUCT_MAPPING_ID',records[i].get('BASE_PRODUCT_MAPPING_ID'));
			rightGridPanel.getStore().add(record);
		}
	}
}

/**
 * 初始化窗口
 */
function initData(){
	// 厂家
	Ext.getCmp('factoryCombo').setValue(parent.factoryValue);
	if(type=="modify"){
		if(parent.modifyFilterFlag!=filterId){
			parent.modifyFilterFlag = filterId;
			Ext.Ajax.request({
			    url: 'fault!getAlarmFilterDetailById.action',
			    method: 'POST',
			    params: {'jsonString':Ext.encode({'filterId' : filterId,'flag': 'third'})},
			    success : function(response) {
			    	var neModelSelect = Ext.decode(response.responseText).neModel;
			    	if(neModelSelect!=''){
			    		for ( var i = 0; i < neModelSelect.length; i++) {
							// 定义右侧表格的一条记录
							var record = new Ext.data.Record(['FACTORY_ID','FACTORY','PRODUCT_NAME','BASE_PRODUCT_MAPPING_ID']);
							record.set('FACTORY_ID',neModelSelect[i].FACTORY);
							record.set('FACTORY',neModelSelect[i].FACTORY);
					    	for(var fac in FACTORY){
					    		if(neModelSelect[i].FACTORY==FACTORY[fac]['key']){
						    		record.set('FACTORY',FACTORY[fac]['value']);
						    		break;
						    	}
					    	}
							record.set('PRODUCT_NAME',neModelSelect[i].PRODUCT_NAME);
							record.set('BASE_PRODUCT_MAPPING_ID',neModelSelect[i].NE_MODEL_ID);
							parent.modifyNeModelSelect += record.get('FACTORY_ID')+','+record.get('FACTORY')+','+record.get('PRODUCT_NAME')+','+record.get('BASE_PRODUCT_MAPPING_ID')+';',
							rightGridPanel.getStore().add(record);
						}
			    	}
			    	parent.modifyNeModelSelect = parent.modifyNeModelSelect.substring(0,parent.modifyNeModelSelect.lastIndexOf(';'));
			    	// 端口型号
					var portModelCheck = Ext.decode(response.responseText).ptpModel;
					var portModel = Ext.getCmp('portModel').items;
					if(portModelCheck!=''){
						for ( var i = 0; i < portModel.length; i++) {
							for ( var j = 0; j < portModelCheck.length; j++) {
								if(portModel.get(i).inputValue==portModelCheck[j].PTP_MODEL){
									portModel.get(i).setValue(true);
									parent.modifyPortModelSelect += portModelCheck[j].PTP_MODEL+',';
								}
							}
						}
					}
					parent.modifyPortModelSelect = parent.modifyPortModelSelect.substring(0,parent.modifyPortModelSelect.lastIndexOf(','));
					// 通道告警状态
					var ptpAlarmStatusSelect = Ext.decode(response.responseText).ptpAlarmStatus.CTP_ALARM_FLAG;
					if(ptpAlarmStatusSelect==1){
						parent.modifyPtpAlarmStatus = true;
					}
					Ext.getCmp('ctpAlarm').setValue(parent.modifyPtpAlarmStatus);
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
		}else{
			// 已选网元型号
			if(parent.modifyNeModelSelect!=''){// 避免是空值的时候，通过截取;的数组长度是1(这样右侧表格会添加一条全是空的记录)
				var neModelSelect = parent.modifyNeModelSelect.split(';');
				for ( var i = 0; i < neModelSelect.length; i++) {
					// 定义右侧表格的一条记录
					var record = new Ext.data.Record(['FACTORY_ID','FACTORY','PRODUCT_NAME','BASE_PRODUCT_MAPPING_ID']);
					record.set('FACTORY_ID',neModelSelect[i].split(',')[0]);
					record.set('FACTORY',neModelSelect[i].split(',')[1]);
					record.set('PRODUCT_NAME',neModelSelect[i].split(',')[2]);
					record.set('BASE_PRODUCT_MAPPING_ID',neModelSelect[i].split(',')[3]);
					rightGridPanel.getStore().add(record);
				}
			}
			// 端口型号
			var portModelCheck = parent.modifyPortModelSelect.split(',');
			var portModel = Ext.getCmp('portModel').items;
			for ( var i = 0; i < portModel.length; i++) {
				for ( var j = 0; j < portModelCheck.length; j++) {
					if(portModel.get(i).inputValue==portModelCheck[j]){
						portModel.get(i).setValue(true);
					}
				}
			}
			// 通道告警状态
			Ext.getCmp('ctpAlarm').setValue(parent.modifyPtpAlarmStatus);
		}
	}else{
		// 已选网元型号
		if(parent.neModelSelect!=''){// 避免是空值的时候，通过截取;的数组长度是1(这样右侧表格会添加一条全是空的记录)
			var neModelSelect = parent.neModelSelect.split(';');
			for ( var i = 0; i < neModelSelect.length; i++) {
				// 定义右侧表格的一条记录
				var record = new Ext.data.Record(['FACTORY_ID','FACTORY','PRODUCT_NAME','BASE_PRODUCT_MAPPING_ID']);
				record.set('FACTORY_ID',neModelSelect[i].split(',')[0]);
				record.set('FACTORY',neModelSelect[i].split(',')[1]);
				record.set('PRODUCT_NAME',neModelSelect[i].split(',')[2]);
				record.set('BASE_PRODUCT_MAPPING_ID',neModelSelect[i].split(',')[3]);
				rightGridPanel.getStore().add(record);
			}
		}
		// 端口型号
		var portModelCheck = parent.portModelSelect.split(',');
		var portModel = Ext.getCmp('portModel').items;
		for ( var i = 0; i < portModel.length; i++) {
			for ( var j = 0; j < portModelCheck.length; j++) {
				if(portModel.get(i).inputValue==portModelCheck[j]){
					portModel.get(i).checked = true;
				}
			}
		}
		// 通道告警状态
		Ext.getCmp('ctpAlarm').checked = parent.ptpAlarmStatus;
	}
}
/**
 * 告警名称左移
 */
function rightMove(){
	var rows = rightGridPanel.getSelectionModel().getSelections();
	if(rows==undefined || rows==null){
		return;
	}
	rightGridPanel.getStore().remove(rows[0]);
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
        items : [centerPanel,southPanel]
	});
 });
