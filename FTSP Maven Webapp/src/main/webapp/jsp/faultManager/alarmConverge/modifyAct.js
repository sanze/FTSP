var alarmNameStore = new Ext.data.Store({
	url : 'alarm-converge!getAlarmNameByFactory.action',
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['key']
	})
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
			// 获取告警名称下拉框输入的值，和历史的内容比较，如果不相同，则取后台模糊查询
			if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
				// 把历史值更新为当前值
				queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
				alarmNameStore.baseParams = {'jsonString':queryEvent.combo.getRawValue()};
				alarmNameStore.load();   
				return false;
			}
		},
		  scope : this
		}
});


var store =new Ext.data.ArrayStore({
	fields : [{name:'ALARM_NAME'}],
	data : []
});
var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [{
		dataIndex : 'ALARM_NAME',
		width : 149
	}]
});

var selAlarmgrid = new Ext.grid.GridPanel({
	id : 'selAlarmgrid',
	border:false,
	height : 260,  
	store : store,
	cm : cm,
	hideHeaders:true, 
	frame : false,
	stripeRows : true
});

var centerPanel = new Ext.FormPanel({  
	id : 'formPanel', 
	region:'center',
	bodyStyle : 'padding:20px 20px 0 30px',
	autoScroll : true,  
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			border : false,
			columnWidth:0.4,
			labelWidth:60,
			items : [{ 
				xtype:'spacer',
				height:80
			},{
				border : false, 
				html : '<span><font size = "2">可选告警：</font></span>'
			},{ 
				xtype:'spacer',
				height:30
			},alarmNameCombo]  
		},{
			layout : 'form',
			border : false,
			columnWidth:0.1,
			style : 'margin-top:110px;margin-right:20px;',
			items : [{
				xtype : 'button',
				text : '>',
				width : 30,
				handler : leftToRight
			},{
				xtype : 'button',
				style : 'margin-top:20px;margin-right:20px;',
				text : '<<',
				width : 30,
				handler : RightToleft
			}]
		},{
			layout : 'form',
			columnWidth:0.5,
			border : false, 
			items :[{
				xtype:'fieldset',
				title:'已选告警', 
				items:[selAlarmgrid]
			}]
		}]
	}],
	buttons : [ {
		text : '确定',
		handler : function() { 
			// 已选告警名称
			var alarmNameCheck = '';
			var gridRecords = store.getRange();
			for ( var i = 0; i < gridRecords.length; i++) {
				alarmNameCheck += gridRecords[i].get('ALARM_NAME') + ",";
			}
			alarmNameCheck = alarmNameCheck.substring(0, alarmNameCheck.lastIndexOf(","));  
			parent.storeAct.getAt(linenum).set('ALARMS',alarmNameCheck); 
			var win = parent.Ext.getCmp('modifyAction');
			if (win) {
				win.close();
			}
		}
	}, {
		text : '取消 ',
		handler : function() {
			var win = parent.Ext.getCmp('modifyAction');
			if (win) {
				win.close();
			}
		}
	} ]
}); 

//左移
function leftToRight(){  
	// 定义表格的一条记录
	var record = new Ext.data.Record(['ALARM_NAME']); 
	// 厂家名称
	var alarmName = Ext.getCmp('alarmNameCombo').getValue();
	if(alarmName==''){
		Ext.Msg.alert('提示','请选择告警名称');
		return false;
	}
	record.set('ALARM_NAME',alarmName);
	// 已选择的告警名称
	var right = store.getRange(); 
	// 如果右边已经存在的，则不能右移
	for ( var i = 0; i < right.length; i++) {
		if (store.findExact("ALARM_NAME", alarmName) <0) {
			store.add(record);
			store.removeAt(store.findExact("ALARM_NAME", "所有"));
		}  
	}  
//所有的情况
	if(alarmName=='所有'){
		store.removeAll();
		store.add(record);
	}
}

//右移
function RightToleft(){
	var right = selAlarmgrid.getSelectionModel().getSelections(); 
	if(right==undefined || right==null){
		return;
	}
	store.remove(right);
}
 
//初始化数据
function initData(){ 
	var data = new Array();
	data = ALARMS.split(",");
	for(var i=0;i<data.length;i++){
		var record = new Ext.data.Record(['ALARM_NAME']); 
		record.set('ALARM_NAME',data[i]);
		store.add(record);
	} 
}

Ext.onReady(function(){
	Ext.QuickTips.init(); 
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){
 		top.Ext.menu.MenuMgr.hideAll();
	};  
	initData();
  	var win =new Ext.Viewport({
  		id:'win',
        layout : 'border', 
        items : centerPanel
	});
 });