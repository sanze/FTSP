/**
 * 声明日期转换格式
 */
Date.prototype.format =function(format)
{
	var o = {
		"M+" : this.getMonth()+1, //month
		"d+" : this.getDate(), //day
		"H+" : this.getHours(), //hour
		"m+" : this.getMinutes(), //minute
		"s+" : this.getSeconds(), //second
		"q+" : Math.floor((this.getMonth()+3)/3), //quarter
		"S" : this.getMilliseconds() //millisecond
	};
	if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
			(this.getFullYear()+"").substr(4- RegExp.$1.length));
	for(var k in o)
		if(new RegExp("("+ k +")").test(format))
	format = format.replace(RegExp.$1,RegExp.$1.length==1? o[k] :
				("00"+ o[k]).substr((""+ o[k]).length));
	return format;
};
/**
 * 创建告警源下拉框
 */ 
var alarmSourceCombo = new Ext.form.ComboBox({
	id : 'alarmSourceCombo',
	fieldLabel : '告警源过滤条件',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ 'all', '全部' ], [ 'OTS', '光传送单元' ],[ 'OMS', '光复用单元' ], [ 'OCH', '光通道' ], [ 'OSC,OSCNI', '光监控信道' ],
					       [ 'STM-1~STM-256', 'SDH端口' ],[ 'STM-64~STM-256', 'STM-64及以上' ],[ 'STM-16', 'STM-16' ],[ 'STM-4', 'STM-4' ],
					       [ 'STM-1', 'STM-1' ],[ 'PDH', 'E1&E3&E4' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local', 
	triggerAction : 'all',
	editable : false,
	width :150
});

/**
 * 创建厂家下拉框
 */
var factoryStore=new Ext.data.ArrayStore({
	fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
	data:[{key:-1,value:"全部"}]
});
factoryStore.loadData(FACTORY,true);
var factoryCombo = new Ext.form.ComboBox({
	id : 'factoryCombo',
	fieldLabel : '厂家',
	store : factoryStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	editable : false,
	triggerAction : 'all',
	anchor : '90%',
	listeners : {
//		beforerender : function(combo) {
//			combo.setValue('1');
//		},
		select : function(combo, record, index) {
			var factory = record.get('value');
			// 还原告警名称下拉框
			Ext.getCmp('alarmNameCombo').reset();
			// 动态改变告警名称数据源的参数
			alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory' : factory,'alarmName':Ext.getCmp('alarmNameCombo').getValue(),'type': type})};
			// 加载告警名称数据源
			alarmNameStore.load({
				callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
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
	selectOnFocus : true,
	mode :'remote', 
	width : 150,
	triggerAction : 'all',
	anchor : '90%',
	resizable: true,
	listeners : {
		beforequery:function(queryEvent){// 每次输入后触发
			// 获取厂家下拉框的值
			var factory = Ext.getCmp('factoryCombo').getValue();
			// 获取告警名称下拉框输入的值，和历史的内容比较，如果不相同，则取后台模糊查询
			if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
				// 把历史值更新为当前值
				queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
				alarmNameStore.baseParams = {'jsonString':Ext.encode({'factory':factory,'alarmName' : queryEvent.combo.getRawValue(),'type': type})};
				alarmNameStore.load();
				return false;
			}
		},
		  scope : this
		}
});

/**
 * 创建持续时间范围下拉框
 */
var continueTimeRangeCombo = new Ext.form.ComboBox({
	id : 'continueTimeRangeCombo',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '小于等于' ], [ '2', '大于等于' ]]
				}),
	valueField : 'value',
	style : 'margin-left :-100px;',
	displayField : 'displayName',
	width : 80,
	mode : 'local',
	triggerAction : 'all'
});

/**
 * 创建频次范围下拉框
 */
var frequencyRangeCombo = new Ext.form.ComboBox({
	id : 'frequencyRangeCombo',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '小于等于' ], [ '2', '大于等于' ]]
				}),
	valueField : 'value',
	style : 'margin-left :-100px;',
	displayField : 'displayName',
	width : 80,
	mode : 'local',
	triggerAction : 'all'
});

/**
 * 告警级别多选框
 */
var alarmLevel = {
	id : 'alarmLevel',
	xtype : 'fieldset',
	title : '级别',
	style : 'margin-left:10px;',
	defaultType: 'checkbox',
	height : 200,
	width : 140,
	labelWidth : 1,
	items : [ {
		boxLabel : '紧急',
		style : 'margin-top:10px;',
		inputValue : '1',
		checked : false
	}, {
		boxLabel : '重要',
		style : 'margin-top:16px;',
		inputValue : '2',
		checked : false
	}, {
		boxLabel : '次要',
		style : 'margin-top:16px;',
		inputValue : '3',
		checked : false
	}, {
		boxLabel : '提示',
		style : 'margin-top:16px;',
		inputValue : '4',
		checked : false
	} ]
};

/**
 * 创建确认状态多选框
 */
var confirmStatus = {
	id : 'confirmStatus',
	xtype : 'fieldset',
	title : '确认状态',
	style : 'margin-left:10px;',
	defaultType: 'checkbox',
	height : 90,
	width : 140,
	labelWidth : 1,
	items : [ {
		boxLabel : '已确认',
		style : 'margin-top:10px;',
		inputValue : 1,
		checked : false
	}, {
		boxLabel : '未确认',
		style : 'margin-top:16px;',
		inputValue : 2,
		checked : false
	}]
};

/**
 * 创建清除状态多选框
 */
var clearStatus = {
	id : 'clearStatus',
	xtype : 'fieldset',
	title : '清除状态',
	style : 'margin-left:10px;',
	defaultType: 'checkbox',
	height : 90,
	width : 140,
	labelWidth : 1,
	items : [ {
		boxLabel : '已清除',
		style : 'margin-top:6px;',
		inputValue : 1,
		checked : false
	}, {
		boxLabel : '未清除',
		style : 'margin-top:16px;',
		inputValue : 2,
		checked : false
	}]
};

/**
 * 创建告警名称多选框
 */
var alarmName = {
	xtype : 'fieldset',
	title : '告警名称',
	style : 'margin-left:10px;',
	height : 90,
	width : 290,
	labelWidth : 60,
	items : [{
		layout : 'form',
		border : false,
		style : 'margin-top:5px;',
		items : factoryCombo
	},{
		layout : 'form',
		border : false,
		style : 'margin-top:10px;',
		items : alarmNameCombo
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
	height : 90,
	width : 290,
	labelWidth : 1,	
	items : [{
		layout : 'column',
		border : false,
		style : 'margin-top:3px;',
		items : [{
			xtype : 'checkbox',
			boxLabel : '<span><font size="2">通信</font></span>',
			style : 'margin-left:15px;',
			inputValue :0,
			checked : false
		}, {
			xtype : 'checkbox',
			boxLabel : '<span><font size="2">设备</font></span>',
			style : 'margin-left:15px;',
			inputValue : 2,
			checked : false
		}, {
			xtype : 'checkbox',
			boxLabel : '<span><font size="2">处理</font></span>',
			style : 'margin-left:15px;',
			inputValue : 3,
			checked : false
		}, {
			xtype : 'checkbox',
			boxLabel : '<span><font size="2">环境</font></span>',
			style : 'margin-left:15px;',
			inputValue : 4,
			checked : false
		}]
	},{
		layout : 'column',
		border : false,
		style : 'margin-top:16px;',
		items : [{
			xtype : 'checkbox',
			boxLabel : '<span><font size="2">服务</font></span>',
			style : 'margin-left:15px;',
			inputValue : 1,
			checked : false
		}, {
			xtype : 'checkbox',
			boxLabel : '<span><font size="2">安全</font></span>',
			style : 'margin-left:15px;',
			inputValue : 5,
			checked : false
		}, {
			xtype : 'checkbox',
			boxLabel : '<span><font size="2">连接</font></span>',
			style : 'margin-left:15px;',
			inputValue : 6,
			checked : false
		}]
	}]
};

/**
 * 创建首次发生时间多选框
 */
var firstTime = {
	id : 'firstTime',
	xtype : 'fieldset',
	title : '首次发生时间',
	style : 'margin-left:10px;',
	height : 150,
	width : 290,
	labelWidth : 20,
	border : true,
	items : [{
        xtype:"radio",
        name : 'firstRadio',
        boxLabel:"任何时间",
        style : 'margin-left:-15px;',
        checked : true
    },{
    	layout : 'column',
    	border : false,
    	style : 'margin-top:10px;',
    	items : [{
    		layout : 'form',
    		border : false,
    		style : 'margin-left:-15px;',
    		items :{
    	        xtype: 'radio',
    	        name : 'firstRadio'
    	    }
    	},{
    		layout : 'form',
    		border : false,
    		items : [{
    	        id : 'firstStart',
    			xtype : 'textfield',
    			fieldLabel:"从",
    			readOnly : true,
    			cls : 'Wdate',
    			width : 180,
    			listeners : {
    				'focus' : function() {
    					WdatePicker({
    						el : 'firstStart',
    						isShowClear : true,
    						readOnly : true,
    						dateFmt : 'yyyy-MM-dd HH:mm:ss',
    						autoPickDate : true,
    						maxDate : '%y-%M-%d'
    					});
    					this.blur();
    				}
    			}
    		},{
    	        layout : 'form',
    	        style : 'margin-top:10px;',
    	        border : false,
    	        items : {
    	        	id : 'firstEnd',
        			xtype : 'textfield',
        			fieldLabel:"到",
        			readOnly : true,
        			cls : 'Wdate',
        			width : 180,
        			listeners : {
        				'focus' : function() {
        					WdatePicker({
        						el : 'firstEnd',
        						isShowClear : true,
        						readOnly : true,
        						dateFmt : 'yyyy-MM-dd HH:mm:ss',
        						autoPickDate : true,
        						maxDate : '%y-%M-%d'
        					});
        					this.blur();
        				}
        			}
    	        }
    		}]
    	}]
    },{
    	layout : 'column',
    	border : false,
    	style : 'margin-top:10px;margin-left:-15px;',
    	items : [{
    		layout : 'form',
    		border : false,
    		items :{
    	        xtype: "radio",
    	        name : 'firstRadio',
    	        boxLabel:""
    	    }
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			border : false,
    		    tag:'span',
    		    html:'<font size="2">最近</font>'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			id : 'firstDay',
    			xtype: "numberfield",
    			style : 'margin-left:-18px;',
    			width : 35,
    			allowDecimals:false,//不允许输入小数 
    			nanText:'请输入有效整数',//无效数字提示
    			maxValue:365,//最大值                 
    			maxText:'请输入0~365之间的整数',
    			minValue:0,//最小值        
    			minText:'请输入0~365之间的整数'
    		}
    	
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			border : false,
    		    tag:'span',
    		    html:'<font size="2">天</font>'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			id : 'firstHour',
    			xtype: "numberfield",
    			style : 'margin-left:-18px;',
    			width : 35,
    			allowDecimals:false,//不允许输入小数 
    			nanText:'请输入有效整数',//无效数字提示
    			maxValue:60,//最大值                 
    			maxText:'请输入0~60之间的整数',
    			minValue:0,//最小值        
    			minText:'请输入0~60之间的整数'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			border : false,
    		    tag:'span',
    		    html:'<font size="2">时</font>'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			id : 'firstMinute',
    			xtype: "numberfield",
    			style : 'margin-left:-18px;',
    			width : 35,
    			allowDecimals:false,//不允许输入小数 
    			nanText:'请输入有效整数',//无效数字提示
    			maxValue:60,//最大值                 
    			maxText:'请输入0~60之间的整数',
    			minValue:0,//最小值        
    			minText:'请输入0~60之间的整数'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			border : false,
    		    tag:'span',
    		    html:'<font size="2">分</font>'
    		}
    	}]
    }]
};

/**
 * 创建清除时间多选框
 */
var clearTime = {
	id : 'clearTime',
	xtype : 'fieldset',
	title : '清除时间',
	style : 'margin-left:10px;',
	height : 150,
	width : 290,
	labelWidth : 15,
	border : true,
	items : [{
        xtype:"radio",
        name : 'clearRadio',
        boxLabel:"任何时间",
        style : 'margin-left:-15px;',
        checked : false
    },{
    	layout : 'column',
    	border : false,
    	style : 'margin-top:10px;',
    	items : [{
    		layout : 'form',
    		border : false,
    		style : 'margin-left:-15px;',
    		items :{
    	        xtype: "radio",
    	        name : 'clearRadio'
    	    }
    	},{
    		layout : 'form',
    		border : false,
    		items : [{
    	        id : 'clearStart',
    			xtype : 'textfield',
    			fieldLabel:"从",
    			readOnly : true,
    			cls : 'Wdate',
    			width : 180,
    			listeners : {
    				'focus' : function() {
    					WdatePicker({
    						el : 'clearStart',
    						isShowClear : true,
    						readOnly : true,
    						dateFmt : 'yyyy-MM-dd HH:mm:ss',
    						autoPickDate : true,
    						maxDate : '%y-%M-%d'
    					});
    					this.blur();
    				}
    			}
    		},{
    	        layout : 'form',
    	        style : 'margin-top:10px;',
    	        border : false,
    	        items : {
    	        	 id : 'clearEnd',
    	    			xtype : 'textfield',
    	    			fieldLabel:"到",
    	    			readOnly : true,
    	    			cls : 'Wdate',
    	    			width : 180,
    	    			listeners : {
    	    				'focus' : function() {
    	    					WdatePicker({
    	    						el : 'clearEnd',
    	    						isShowClear : true,
    	    						readOnly : true,
    	    						dateFmt : 'yyyy-MM-dd HH:mm:ss',
    	    						autoPickDate : true,
    	    						maxDate : '%y-%M-%d'
    	    					});
    	    					this.blur();
    	    				}
    	    			}
    	        }
    		}]
    	}]
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
	height : 60,
	width : 290,
	layout : 'hbox',
	labelWidth : 1,
	items : [ {
		boxLabel : '<span><font size="2">影响</font></span>',
		style : 'margin-left:10px;',
		inputValue : 1,
		checked : false
	}, {
		boxLabel : '<span><font size="2">不影响</font></span>',
		style : 'margin-left:10px;',
		inputValue : 2,
		checked : false
	}, {
		boxLabel : '<span><font size="2">未知</font></span>',
		style : 'margin-left:10px;',
		inputValue : 0,
		checked : false
	}]
};

/**
 * 创建告警反转多选框
 */
var almReversal = {
	id : 'almReversal',
	xtype : 'fieldset',
	title : '告警反转',
	style : 'margin-left:10px;',
	defaultType: 'checkbox',
	height : 60,
	width : 290,
	layout : 'hbox',
	labelWidth : 1,
	items : [ {
		boxLabel : '<span><font size="2">反转</font></span>',
		style : 'margin-left:10px;',
		inputValue : 1,
		checked : false
	}, {
		boxLabel : '<span><font size="2">未反转</font></span>',
		style : 'margin-left:10px;',
		inputValue : 0,
		checked : false
	}]
};

/**
 * 创建border布局的center部分
 */
var centerPanel = new Ext.FormPanel({
	autoScroll : true,
	forceFit : true,
	region : 'center',
	padding:'0 0 0 30px',
	trackResetOnLoad : true,
	width : 600,
	items : [ {
		layout : 'form',
		border : false,
		width : 600,
		items : [{
			layout : 'form',
			border : false,
			style : 'margin-top:10px;margin-left:10px;',
			items : alarmSourceCombo
		},{
			layout : 'column',
			border : false,
			style : 'margin-top:10px;',
			items : [{
				layout : 'form',
				border : false,
				items : alarmLevel
			},{
				layout : 'form',
				border : false,
				items : [confirmStatus,clearStatus]
			},{
				layout : 'form',
				border : false,
				items : [alarmName,alarmType]
			}]
		},{
			layout : 'column',
			border : false,
			items : [{
				layout : 'form',
				border : false,
				items : firstTime 
			},{
				layout : 'form',
				border : false,
				items : clearTime
			}]
		},{
			layout : 'column',
			border : false,
			items : [{
				layout : 'form',
				border : false,
				items : almReversal 
			},{
				layout : 'form',
				border : false,
				items : affectType
			}]
		},{
			layout : 'column',
			border : false,
//			style :'margin-top:10px;',
//			width : 600,
			items : [{
				id : 'continueTime',
				columnWidth: .6,
				layout : 'column',
				style :'margin-top:10px;margin-bottom:10px;',
				border : false,
				items : [{
					xtype : 'checkbox',
					boxLabel : '<span><font size="2">持续时间</font></span>',
					style : 'margin-left:15px;',
					inputValue : 1,
					checked : false
	    		},{
					layout : 'form',
					border : false,
					style : 'margin-left:10px;',
					width : 85,
					items :continueTimeRangeCombo
				},{
		    		layout : 'form',
		    		border : false,
		    		width :40,
		    		items : {
		    			id : 'continueDay',
		    			xtype: "numberfield",
		    			style : 'margin-left:-100px;',
		    			width : 35,
		    			allowDecimals:false,//不允许输入小数 
		    			nanText:'请输入有效整数',//无效数字提示
		    			maxValue:365,//最大值                 
		    			maxText:'请输入0~365之间的整数',
		    			minValue:0,//最小值        
		    			minText:'请输入0~365之间的整数'
		    		}
		    	},{
		    		layout : 'form',
		    		border : false,
		    		items : {
		    			border : false,
		    			style :'margin-top:4px;',
		    		    tag:'span',
		    		    html:'<font size="2">天</font>'
		    		}
		    	},{
		    		layout : 'form',
		    		border : false,
		    		width :40,
		    		items : {
		    			id : 'continueHour',
		    			xtype: "numberfield",
		    			style : 'margin-left:-100px;',
		    			width : 35,
		    			allowDecimals:false,//不允许输入小数 
		    			nanText:'请输入有效整数',//无效数字提示
		    			maxValue:60,//最大值                 
		    			maxText:'请输入0~60之间的整数',
		    			minValue:0,//最小值        
		    			minText:'请输入0~60之间的整数'
		    		}
		    	},{
		    		layout : 'form',
		    		border : false,
		    		items : {
		    			border : false,
		    			style :'margin-top:4px;',
		    		    tag:'span',
		    		    html:'<font size="2">时</font>'
		    		}
		    	},{
		    		layout : 'form',
		    		border : false,
		    		width :40,
		    		items : {
		    			id : 'continueMinute',
		    			xtype: "numberfield",
		    			style : 'margin-left:-100px;',
		    			width : 35,
		    			allowDecimals:false,//不允许输入小数 
		    			nanText:'请输入有效整数',//无效数字提示
		    			maxValue:60,//最大值                 
		    			maxText:'请输入0~60之间的整数',
		    			minValue:0,//最小值        
		    			minText:'请输入0~60之间的整数'
		    		}
		    	},{
		    		layout : 'form',
		    		border : false,
		    		items : {
		    			border : false,
		    			style :'margin-top:4px;',
		    		    tag:'span',
		    		    html:'<font size="2">分</font>'
		    		}
		    	}]
			},{
				id : 'frequency',
				columnWidth: .4,
				layout : 'column',
				border : false,
				style :'margin-top:10px;',
				items : [{
					xtype : 'checkbox',
					boxLabel : '<span><font size="2">频次</font></span>',
					style : 'margin-left:40px;',
					inputValue : 2,
					checked : false
				},{
					layout : 'form',
					border : false,
					style : 'margin-left:10px;',
					width : 85,
					items :frequencyRangeCombo
				},{
		    		layout : 'form',
		    		border : false,
		    		width :40,
		    		items : {
		    			id : 'frequencyCount',
		    			xtype: "numberfield",
		    			style : 'margin-left:-100px;',
		    			width : 35,
		    			allowDecimals:false,//不允许输入小数 
		    			nanText:'请输入有效整数',//无效数字提示
		    			maxValue:1000,//最大值                 
		    			maxText:'请输入0~1000之间的整数',
		    			minValue:0,//最小值        
		    			minText:'请输入0~1000之间的整数'
		    		}
		    	},{
		    		layout : 'form',
		    		border : false,
		    		items : {
		    			border : false,
		    			style :'margin-top:4px;',
		    		    tag:'span',
		    		    html:'<font size="2">次</font>'
		    		}
		    	}]
			}]
		}]
	}]
});

/**
 * 树状结构参数
 */
var treeParams={
	rootId:0,
	rootType:0,
	rootText:"FTSP",
	rootVisible:false,
	leafType:7
};

/**
 * 创建border布局的west部分
 */
var westPanel = new Ext.FormPanel({
	title : '告警源选择',
	region : 'west',
	width : 280,
	autoScroll : true,
	forceFit : true,
	collapsible : true,// 允许伸缩
	html : '<iframe id="tree_panel" name = "tree_panel" src ="../commonManager/tree.jsp?'+Ext.urlEncode(treeParams)+'" height="100%" width="100%" frameBorder=0 border=0/>'
});

/**
 * 创建border布局的south部分
 */
var southPanel = new Ext.FormPanel({
	region : 'south',
	autoScroll : true,
	forceFit : true,
	buttons:[{
		text:'确定',
		handler : function(){
			// 告警源选择
			var iframe = window.frames["tree_panel"] || window.frames[0];
			var checkedNodeIds;
			if (iframe.getCheckedNodes) {
				checkedNodeIds = iframe.getCheckedNodes(["nodeId", "nodeLevel"], "top");
			} else {
				checkedNodeIds = iframe.contentWindow.getCheckedNodes(["nodeId","nodeLevel"], "top");
			}
			var nodeIds = "";
			var nodeLevels = "";
			for ( var i = 0; i < checkedNodeIds.length; i++) {
				nodeIds += checkedNodeIds[i].nodeId + ",";
				nodeLevels += checkedNodeIds[i].nodeLevel + ",";
			}
//			parent.treeCheck = Ext.encode(checkedNodeIds);
			// 告警源过滤条件
			parent.alarmSource = Ext.getCmp('alarmSourceCombo').getValue();
			// 告警级别
			var alarmLevel = Ext.getCmp('alarmLevel').items;
			// 先清空原来的值，否则会在原有值的技术上直接相加
			parent.alarmLevelCheck = '';
			for ( var i = 0; i < alarmLevel.length; i++) {
				if(alarmLevel.get(i).checked){
					parent.alarmLevelCheck += alarmLevel.get(i).inputValue + ',';
				}
			}
			// 确认状态
			var confirmStatus = Ext.getCmp('confirmStatus').items;
			parent.confirmStatusCheck = '';
			for ( var j = 0; j < confirmStatus.length; j++) {
				if(confirmStatus.get(j).checked){
					parent.confirmStatusCheck += confirmStatus.get(j).inputValue + ',';
				}
			}
			// 清除状态
			var clearStatus = Ext.getCmp('clearStatus').items;
			parent.clearStatusCheck = '';
			for ( var j = 0; j < clearStatus.length; j++) {
				if(clearStatus.get(j).checked){
					parent.clearStatusCheck += clearStatus.get(j).inputValue + ',';
				}
			}
			// 告警名称->厂家
			parent.factoryValue = Ext.getCmp('factoryCombo').getValue();
			// 告警名称->告警名称
			parent.alarmName = Ext.getCmp('alarmNameCombo').getRawValue();
			// 告警类型
			var alarmType = Ext.getCmp('alarmType').items;
			parent.alarmTypeCheck = '';
			for ( var m = 0; m < alarmType.length; m++) {
				var alarmTypeChild = alarmType.get(m).items;
				for ( var n = 0; n < alarmTypeChild.length; n++) {
					if(alarmTypeChild.get(n).checked){
						parent.alarmTypeCheck += alarmTypeChild.get(n).inputValue + ',';
					}
				}
			}
			
			// 首次发生时间
			parent.firstOneStatus = Ext.getCmp('firstTime').items.get(0).checked;
			parent.firstTwoStatus = Ext.getCmp('firstTime').items.get(1).items.get(0).items.get(0).checked;
			parent.firstThreeStatus = Ext.getCmp('firstTime').items.get(2).items.get(0).items.get(0).checked;
			parent.firstStart = Ext.getCmp('firstStart').getValue();
			parent.firstEnd = Ext.getCmp('firstEnd').getValue();
			parent.firstDay = Ext.getCmp('firstDay').getValue();
			parent.firstHour = Ext.getCmp('firstHour').getValue();
			parent.firstMinute = Ext.getCmp('firstMinute').getValue();
			if(parent.firstTwoStatus){
				if(parent.firstStart==""){
					Ext.Msg.alert('提示','请选择首次发生时间的从时间');
					return false;
				}
				if(parent.firstEnd==""){
					Ext.Msg.alert('提示','请选择首次发生时间的到时间');
					return false;
				}
				// 首次发生时间范围检查
				if(!dateRangeCheckIsOk(parent.firstStart,parent.firstEnd)){
					Ext.Msg.alert('提示','首次发生时间范围选择错误：结束时间必须大于开始时间，请重新选择');
					return false;
				}
			}
			if(parent.firstThreeStatus){
				if(parent.firstDay=="" && typeof parent.firstDay=='string'){
					Ext.Msg.alert('提示','请输入首次发生时间最近的天');
					return false;
				}
				if(parent.firstHour=="" && typeof parent.firstHour=='string'){
					Ext.Msg.alert('提示','请输入首次发生时间最近的小时');
					return false;
				}
				if(parent.firstMinute=="" && typeof parent.firstMinute=='string'){
					Ext.Msg.alert('提示','请输入首次发生时间最近的分钟');
					return false;
				}
				if(parent.firstDay==0&&parent.firstHour==0&&parent.firstMinute==0){
					Ext.Msg.alert('提示','首次发生最近时间必须大于0分钟,请重新输入');
					return false;
				}
			}

			// 清除时间
			parent.clearOneStatus = Ext.getCmp('clearTime').items.get(0).checked;
			parent.clearTwoStatus = Ext.getCmp('clearTime').items.get(1).items.get(0).items.get(0).checked;
			parent.clearStart = Ext.getCmp('clearStart').getValue();
			parent.clearEnd = Ext.getCmp('clearEnd').getValue();
			if(parent.clearTwoStatus){
				if(parent.clearStart==""){
					Ext.Msg.alert('提示','请选择清除时间的从时间');
					return false;
				}
				if(parent.clearEnd==""){
					Ext.Msg.alert('提示','请选择清除时间的到时间');
					return false;
				}
				// 清除时间范围检查
				if(!dateRangeCheckIsOk(parent.clearStart,parent.clearEnd)){
					Ext.Msg.alert('提示','清除时间范围选择错误：结束时间必须大于开始时间，请重新选择');
					return false;
				}				
			}
			// 持续时间
			parent.continueTimeStatus = Ext.getCmp('continueTime').items.get(0).checked;
			parent.continueTimeRange = Ext.getCmp('continueTimeRangeCombo').getValue();
			parent.continueDay = Ext.getCmp('continueDay').getValue();
			parent.continueHour = Ext.getCmp('continueHour').getValue();
			parent.continueMinute = Ext.getCmp('continueMinute').getValue();
			if(parent.continueTimeStatus){
				if(parent.continueDay=="" && typeof parent.continueDay=='string'){
					Ext.Msg.alert('提示','请输入持续时间的天');
					return false;
				}
				if(parent.continueHour=="" && typeof parent.continueHour=='string'){
					Ext.Msg.alert('提示','请输入持续时间的小时');
					return false;
				}
				if(parent.continueMinute=="" && typeof parent.continueMinute=='string'){
					Ext.Msg.alert('提示','请输入持续时间的分钟');
					return false;
				}
				if(parent.continueDay==0&&parent.continueHour==0&&parent.continueMinute==0){
					Ext.Msg.alert('提示','持续时间必须大于0分钟,请重新输入');
					return false;
				}
			}
			// 频次
			parent.frequencyStatus = Ext.getCmp('frequency').items.get(0).checked;
			parent.frequencyRange = Ext.getCmp('frequencyRangeCombo').getValue();
			parent.frequencyCount = Ext.getCmp('frequencyCount').getValue();
			if(parent.frequencyStatus){
				if(parent.frequencyCount==""){
					Ext.Msg.alert('提示','请输入频次');
					return false;
				}
			}
			// 业务影响
			var affectType = Ext.getCmp('affectType').items;
			parent.affectTypeCheck = '';
			for ( var b = 0; b < affectType.length; b++) {
				if(affectType.get(b).checked){
					parent.affectTypeCheck += affectType.get(b).inputValue + ',';
				}
			}
			// 告警反转
			var almReversal = Ext.getCmp('almReversal').items;
			parent.reversalCheck = '';
			for ( var b = 0; b < almReversal.length; b++) {
				if(almReversal.get(b).checked){
					parent.reversalCheck += almReversal.get(b).inputValue + ',';
				}
			}
			if(!centerPanel.getForm().isValid()){
				return false;
			}
			// 封装高级查询条件
			var highParam = Ext.encode({'nodeIds':nodeIds,'nodeLevels':nodeLevels,'alarmSource':parent.alarmSource,'alarmLevel':parent.alarmLevelCheck,'confirmStatus':parent.confirmStatusCheck,
    			'clearStatus':parent.clearStatusCheck,'factory':parent.factoryValue,'alarmName':parent.alarmName,'alarmType':parent.alarmTypeCheck,
    			'firstOneStatus':parent.firstOneStatus,'firstTwoStatus':parent.firstTwoStatus,'firstThreeStatus':parent.firstThreeStatus,
    			'firstStart':parent.firstStart,'firstEnd':parent.firstEnd,'firstDay':parent.firstDay,'firstHour':parent.firstHour,
    			'firstMinute':parent.firstMinute,'clearOneStatus':parent.clearOneStatus,'clearTwoStatus':parent.clearTwoStatus,
    			'clearStart':parent.clearStart,'clearEnd':parent.clearEnd,'continueTimeStatus':parent.continueTimeStatus,
    			'continueTimeRange':parent.continueTimeRange,'continueDay':parent.continueDay,'continueHour':parent.continueHour,
    			'continueMinute':parent.continueMinute,'frequencyStatus':parent.frequencyStatus,'frequencyRange':parent.frequencyRange,
    			'frequencyCount':parent.frequencyCount,'affectType':parent.affectTypeCheck,'type':type,'almReversal':parent.reversalCheck
			});
			// 添加高级查询结果页面
			var title="高级查询";
			var url;
			if(type=="current"){
				title="当前告警"+title;
				url = "../faultManager/highQueryDetailCurrent.jsp?highParam=";
			}else if(type=="history"){
				title="历史告警"+title;
				url = "../faultManager/highQueryDetailHistory.jsp?highParam=";
			}
			parent.parent.addTabPage(url + highParam, title, authSequence);
			// 关闭高级查询窗口
			var win = parent.Ext.getCmp('highQueryWindow');
			if(win){
				win.close();
			}
			
		}
	},{
		text:'重置',
		handler : function(){
			//centerPanel.getForm().reset();
			//Ext.getCmp('alarmNameCombo').clearValue();
			resetPage();
		}
	},{
		text:'取消',
		handler : function(){
			// 关闭高级查询窗口
			var win = parent.Ext.getCmp('highQueryWindow');
			if(win){
				win.close();
			}
		}
	}]
});

/**
 * 初始化窗口信息
 */
function initData(){
	// 告警源选择(因为是异步树，无法加载已选的数据)
//	var treeSelect = Ext.decode(parent.treeCheck);
//	for ( var i = 0; i < treeSelect.length; i++) {
//		alert(treeSelect[i].nodeId+"----"+treeSelect[i].nodeLevel);
//	}
	// 告警源过滤条件
	Ext.getCmp('alarmSourceCombo').setValue(parent.alarmSource);
	// 告警级别
	var alarmLevelSelect = parent.alarmLevelCheck.split(',');
	var alarmLevel = Ext.getCmp('alarmLevel').items;
	for ( var i = 0; i < alarmLevel.length; i++) {
		for ( var j = 0; j < alarmLevelSelect.length; j++) {
			if(alarmLevel.get(i).inputValue==alarmLevelSelect[j]){
				alarmLevel.get(i).checked = true;
			}
		}
	}
	// 确认状态
	var confirmStatusSelect = parent.confirmStatusCheck.split(',');
	var confirmStatus = Ext.getCmp('confirmStatus').items;
	for ( var i = 0; i < confirmStatus.length; i++) {
		for ( var j = 0; j < confirmStatusSelect.length; j++) {
			// 历史告警告警查询禁用确认状态选择框
			if(type=="current") {
				if(confirmStatus.get(i).inputValue==confirmStatusSelect[j]){
					confirmStatus.get(i).checked = true;
				}				
			}else{
				confirmStatus.get(i).disabled = true;
			}
		}
	}
	// 清除状态
	var clearStatusSelect = parent.clearStatusCheck.split(',');
	var clearStatus = Ext.getCmp('clearStatus').items;
	for ( var i = 0; i < clearStatus.length; i++) {
		for ( var j = 0; j < clearStatusSelect.length; j++) {
			// 历史告警告警查询禁用清除状态选择框
			if(type=="current") {
				if(clearStatus.get(i).inputValue==clearStatusSelect[j]){
					clearStatus.get(i).checked = true;
				}				
			}else{
				clearStatus.get(i).disabled = true;
			}
		}
	}
	// 告警名称->厂家
	Ext.getCmp('factoryCombo').setValue(parent.factoryValue);
	// 告警名称->告警名称
	Ext.getCmp('alarmNameCombo').setValue(parent.alarmName);
	// 告警类型
	if(parent.alarmTypeCheck!=''){
		var alarmTypeSelect = parent.alarmTypeCheck.split(',');
		var alarmType = Ext.getCmp('alarmType').items;
		for ( var m = 0; m < alarmType.length; m++) {
			var alarmTypeChild = alarmType.get(m).items;
			for ( var n = 0; n < alarmTypeChild.length; n++) {
				for ( var i = 0; i < alarmTypeSelect.length; i++) {
					if(alarmTypeChild.get(n).inputValue==alarmTypeSelect[i]){
						alarmTypeChild.get(n).checked = true;
					}
				}
			}
		}
	}
	
	var today = new Date();
	var todayStr = today.format("yyyy-MM-dd HH:mm:ss");
	today.setDate(today.getDate()-7);
	var lastWeekStr = today.format("yyyy-MM-dd HH:mm:ss");
	// 首次发生时间
	if(parent.firstOneStatus){
		Ext.getCmp('firstTime').items.get(0).checked = true;
	}
	if(parent.firstTwoStatus){
		Ext.getCmp('firstTime').items.get(1).items.get(0).items.get(0).checked = true;
	}
	if(parent.firstThreeStatus){
		Ext.getCmp('firstTime').items.get(2).items.get(0).items.get(0).checked = true;
	}
	if (type=='history' && parent.firstStart==''){
		Ext.getCmp('firstStart').setValue(lastWeekStr);
		Ext.getCmp('firstEnd').setValue(todayStr);
	}else{
		Ext.getCmp('firstStart').setValue(parent.firstStart);
		Ext.getCmp('firstEnd').setValue(parent.firstEnd);		
	}
	Ext.getCmp('firstDay').setValue(parent.firstDay);
    Ext.getCmp('firstHour').setValue(parent.firstHour);
	Ext.getCmp('firstMinute').setValue(parent.firstMinute);
	// 清除时间
	if(parent.clearOneStatus){
		Ext.getCmp('clearTime').items.get(0).checked = true;
	}
	if(parent.clearTwoStatus){
		Ext.getCmp('clearTime').items.get(1).items.get(0).items.get(0).checked = true;
	}
	Ext.getCmp('clearStart').setValue(parent.clearStart);
	Ext.getCmp('clearEnd').setValue(parent.clearEnd);
	// 持续时间
	if(parent.continueTimeStatus){
		Ext.getCmp('continueTime').items.get(0).checked = true;
	}
	Ext.getCmp('continueTimeRangeCombo').setValue(parent.continueTimeRange);
	Ext.getCmp('continueDay').setValue(parent.continueDay);
	Ext.getCmp('continueHour').setValue(parent.continueHour);
	Ext.getCmp('continueMinute').setValue(parent.continueMinute);
	// 频次
	if(parent.frequencyStatus){
		Ext.getCmp('frequency').items.get(0).checked = true;
	}
	Ext.getCmp('frequencyRangeCombo').setValue(parent.frequencyRange);
	Ext.getCmp('frequencyCount').setValue(parent.frequencyCount);
	// 业务影响
	var affectTypeSelect = parent.affectTypeCheck.split(',');
	var affectType = Ext.getCmp('affectType').items;
	for ( var i = 0; i < affectType.length; i++) {
		for ( var j = 0; j < affectTypeSelect.length; j++) {
			var regg=/^0$/;
			if(affectType.get(i).inputValue=='0' && regg.test(affectTypeSelect[j])){
				affectType.get(i).checked = true;
			}else if(affectType.get(i).inputValue!='0' && affectType.get(i).inputValue==affectTypeSelect[j]){
				affectType.get(i).checked = true;
			}
		}
	}
	// 告警反转
	var almReversalSelect = parent.reversalCheck.split(',');
	var almReversal = Ext.getCmp('almReversal').items;
	for ( var i = 0; i < almReversal.length; i++) {
		for ( var j = 0; j < almReversalSelect.length; j++) {
			var regg=/^0$/;
			if(almReversal.get(i).inputValue=='0' && regg.test(almReversalSelect[j])){
				almReversal.get(i).checked = true;
			}else if(almReversal.get(i).inputValue!='0' && almReversal.get(i).inputValue==almReversalSelect[j]){
				almReversal.get(i).checked = true;
			}
		}
	}
	
}

function resetPage(){
	// 告警源过滤条件
	Ext.getCmp('alarmSourceCombo').setValue('all');
	// 告警级别
	var alarmLevel = Ext.getCmp('alarmLevel').items;

	for ( var i = 0; i < alarmLevel.length; i++) {
		alarmLevel.get(i).setValue(false);
	}
	// 确认状态
	var confirmStatus = Ext.getCmp('confirmStatus').items;
	for ( var i = 0; i < confirmStatus.length; i++) {
		confirmStatus.get(i).setValue(false);
	}
	// 清除状态
	var clearStatus = Ext.getCmp('clearStatus').items;
	for ( var i = 0; i < clearStatus.length; i++) {
		clearStatus.get(i).setValue(false);
	}
	// 告警名称->厂家
	Ext.getCmp('factoryCombo').setValue('-1');
	// 告警名称->告警名称
	Ext.getCmp('alarmNameCombo').clearValue();
	// 告警类型
	var alarmType = Ext.getCmp('alarmType').items;
	var alarmType = Ext.getCmp('alarmType').items;
	for ( var m = 0; m < alarmType.length; m++) {
		var alarmTypeChild = alarmType.get(m).items;
		for ( var n = 0; n < alarmTypeChild.length; n++) {
			alarmTypeChild.get(n).setValue(false);
		}
	}

	// 首次发生时间
	Ext.getCmp('firstTime').items.get(0).setValue(true);
	Ext.getCmp('firstStart').setValue('');
	Ext.getCmp('firstEnd').setValue('');
	Ext.getCmp('firstDay').setValue('');
    Ext.getCmp('firstHour').setValue('');
	Ext.getCmp('firstMinute').setValue('');
	// 清除时间
	Ext.getCmp('clearTime').items.get(0).setValue(true);
	Ext.getCmp('clearStart').setValue('');
	Ext.getCmp('clearEnd').setValue('');
	// 持续时间
	Ext.getCmp('continueTime').items.get(0).setValue(false);
	Ext.getCmp('continueTimeRangeCombo').setValue('1');
	Ext.getCmp('continueDay').setValue('');
	Ext.getCmp('continueHour').setValue('');
	Ext.getCmp('continueMinute').setValue('');
	// 频次
	Ext.getCmp('frequency').items.get(0).setValue(false);
	Ext.getCmp('frequencyRangeCombo').setValue('1');
	Ext.getCmp('frequencyCount').setValue('');
	// 业务影响
	var affectType = Ext.getCmp('affectType').items;
	for ( var i = 0; i < affectType.length; i++) {
		affectType.get(i).setValue(false);
	}
	// 告警反转
	var almReversal = Ext.getCmp('almReversal').items;
	for ( var i = 0; i < almReversal.length; i++) {
		almReversal.get(i).setValue(false);
	}
}
//日期范围检查，检查结束日期是否大于开始日期
function dateRangeCheckIsOk(startDate,endDate){
	if(startDate=="" || endDate==""){
		return true;
	}
	// 开始日期
	var startDateTimeStr=startDate.split(" ");
	var startDateStr=startDateTimeStr[0].split("-");
	var startTimeStr=startDateTimeStr[1].split(":");
	var start = startDateStr[0]+startDateStr[1]+startDateStr[2]+startTimeStr[0]+startTimeStr[1]+startTimeStr[2];
	// 结束日期
	var endDateTimeStr=endDate.split(" ");
	var endDateStr=endDateTimeStr[0].split("-");
	var endTimeStr=endDateTimeStr[1].split(":");
	var end = endDateStr[0]+endDateStr[1]+endDateStr[2]+endTimeStr[0]+endTimeStr[1]+endTimeStr[2];
	// 日期比较
	if(end>start){
		return true;
	}else{
		return false;
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
        items : [westPanel,centerPanel,southPanel]
	});
 });
