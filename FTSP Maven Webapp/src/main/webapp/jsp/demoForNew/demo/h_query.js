// 告警源下拉框
var alarmSourceCombo = new Ext.form.ComboBox({
	fieldLabel : '告警源过滤条件',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', 'STM-64及以上' ], [ '2', 'xxxxx' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'STM-64及以上',
	mode : 'local',
	triggerAction : 'all',
	listeners : {
		select : function(combo, record, index) {
		}
	}
});

//厂家下拉框
var factoryCombo = new Ext.form.ComboBox({
	fieldLabel : '厂家',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '华为' ], [ '2', '中兴' ], [ '3', '烽火' ], [ '4', '朗讯' ], [ '5', '富士通' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'华为',
	mode : 'local',
	triggerAction : 'all',
	anchor : '90%',
	listeners : {
		select : function(combo, record, index) {
		}
	}
});

//时间范围下拉框
var timeRangeCombo = new Ext.form.ComboBox({
	fieldLabel : '',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '小于等于' ], [ '2', '大于等于' ]]
				}),
	valueField : 'value',
	style : 'margin-left :-100px;',
	displayField : 'displayName',
	emptyText:'小于等于',
	width : 100,
	mode : 'local',
	triggerAction : 'all',
	listeners : {
		select : function(combo, record, index) {
		}
	}
});

// 告警级别多选框
var alarmLevel = {
	xtype : 'fieldset',
	title : '级别',
	style : 'margin-left:10px;',
	height : 200,
	width : 140,
	labelWidth : 1,
	items : [ {
		xtype : 'checkbox',
		name : 'urgent',
		boxLabel : '紧急',
		style : 'margin-top:10px;',
		inputValue : 1,
		checked : false
	}, {
		xtype : 'checkbox',
		name : 'important',
		boxLabel : '重要',
		style : 'margin-top:16px;',
		inputValue : 2,
		checked : false
	}, {
		xtype : 'checkbox',
		name : 'ciyao',
		boxLabel : '次要',
		style : 'margin-top:16px;',
		inputValue : 3,
		checked : false
	}, {
		xtype : 'checkbox',
		name : 'tishi',
		boxLabel : '提示',
		style : 'margin-top:16px;',
		inputValue : 4,
		checked : false
	}, {
		xtype : 'checkbox',
		name : 'weizhi',
		boxLabel : '未知',
		style : 'margin-top:16px;',
		inputValue : 5,
		checked : false
	} ]
};

//确认状态多选框
var confirmStatus = {
	xtype : 'fieldset',
	title : '确认状态',
	style : 'margin-left:10px;',
	height : 90,
	width : 140,
	labelWidth : 1,
	items : [ {
		xtype : 'checkbox',
		name : 'yiqueren',
		boxLabel : '已确认',
		style : 'margin-top:10px;',
		inputValue : 1,
		checked : false
	}, {
		xtype : 'checkbox',
		name : 'weiqueren',
		boxLabel : '未确认',
		style : 'margin-top:16px;',
		inputValue : 2,
		checked : false
	}]
};

//清除状态多选框
var clearStatus = {
	xtype : 'fieldset',
	title : '清除状态',
	style : 'margin-left:10px;',
	height : 90,
	width : 140,
	labelWidth : 1,
	items : [ {
		xtype : 'checkbox',
		name : 'yiqingchu',
		boxLabel : '已清除',
		style : 'margin-top:6px;',
		inputValue : 1,
		checked : false
	}, {
		xtype : 'checkbox',
		name : 'weiqingchu',
		boxLabel : '未确认',
		style : 'margin-top:16px;',
		inputValue : 2,
		checked : false
	}]
};

//告警名称多选框
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
		items : {
			xtype : 'textfield',
			fieldLabel : '告警名称',
			allowBlank : true,
			anchor : '90%'
		}
	}]
};


//告警类型多选框
var alarmType = {
	xtype : 'fieldset',
	title : '告警类型',
	style : 'margin-left:10px;',
	height : 90,
	width : 290,
	labelWidth : 1,
	items : [{
		layout : 'table',
		border : false,
		style : 'margin-top:3px;',
		items : [{
			xtype : 'checkbox',
			name : 'tongxin',
			boxLabel : '通信',
			style : 'margin-left:15px;',
			inputValue : 1,
			checked : false
		}, {
			xtype : 'checkbox',
			name : 'shebei',
			boxLabel : '设备',
			style : 'margin-left:15px;',
			inputValue : 2,
			checked : false,
		}, {
			xtype : 'checkbox',
			name : 'chuli',
			boxLabel : '处理',
			style : 'margin-left:15px;',
			inputValue : 3,
			checked : false
		}, {
			xtype : 'checkbox',
			name : 'huanjing',
			boxLabel : '环境',
			style : 'margin-left:15px;',
			inputValue : 4,
			checked : false
		}]
	},{
		layout : 'table',
		border : false,
		style : 'margin-top:16px;',
		items : [{
			xtype : 'checkbox',
			name : 'fuwu',
			boxLabel : '服务',
			style : 'margin-left:15px;',
			inputValue : 5,
			checked : false
		}, {
			xtype : 'checkbox',
			name : 'anquan',
			boxLabel : '安全',
			style : 'margin-left:15px;',
			inputValue : 6,
			checked : false,
		}, {
			xtype : 'checkbox',
			name : 'lianjie',
			boxLabel : '连接',
			style : 'margin-left:15px;',
			inputValue : 3,
			checked : false
		}]
	}]
};

//首次发生时间多选框
var firstTime = {
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
        fieldLabel:"",
        boxLabel:"任何时间",
        style : 'margin-left:-15px;',
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
    	        name : 'firstRadio',
    	        fieldLabel: ""
    	    }
    	},{
    		layout : 'form',
    		border : false,
    		items : [{
    	        xtype:"datefield",
    	        fieldLabel:"从",
    	        width : 200
    		},{
    	        layout : 'form',
    	        style : 'margin-top:10px;',
    	        border : false,
    	        items : {
    	        	xtype:"datefield",
        	        fieldLabel:"到",
        	        width : 200
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
    	        fieldLabel: "",
    	        name : 'firstRadio',
    	        boxLabel:"最近",
    	        checked : true
    	    }
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			xtype: "numberfield",
    			style : 'margin-left:-18px;',
    			width : 35,
    			allowDecimals:false,//不允许输入小数 
    			nanText:'请输入有效整数',//无效数字提示
    			maxValue:365,//最大值                 
    			maxText:'值太大',
    			minValue:0,//最小值        
    			minText:'值太小'
    		}
    	
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			border : false,
    		    tag:'span',
    		    html:'<font size="4">天</font>'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			xtype: "numberfield",
    			style : 'margin-left:-18px;',
    			width : 35,
    			allowDecimals:false,//不允许输入小数 
    			nanText:'请输入有效整数',//无效数字提示
    			maxValue:60,//最大值                 
    			maxText:'值太大',
    			minValue:0,//最小值        
    			minText:'值太小'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			border : false,
    		    tag:'span',
    		    html:'<font size="4">时</font>'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			xtype: "numberfield",
    			style : 'margin-left:-18px;',
    			width : 35,
    			allowDecimals:false,//不允许输入小数 
    			nanText:'请输入有效整数',//无效数字提示
    			maxValue:60,//最大值                 
    			maxText:'值太大',
    			minValue:0,//最小值        
    			minText:'值太小'
    		}
    	},{
    		layout : 'form',
    		border : false,
    		items : {
    			border : false,
    		    tag:'span',
    		    html:'<font size="4">分</font>'
    		}
    	}]
    }]
};

//清除时间多选框
var clearTime = {
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
        fieldLabel:"",
        boxLabel:"任何时间",
        style : 'margin-left:-15px;',
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
    	        name : 'clearRadio',
    	        fieldLabel: ""
    	    }
    	},{
    		layout : 'form',
    		border : false,
    		items : [{
    	        xtype:"datefield",
    	        fieldLabel:"从",
    	        width : 200
    		},{
    	        layout : 'form',
    	        style : 'margin-top:10px;',
    	        border : false,
    	        items : {
    	        	xtype:"datefield",
        	        fieldLabel:"到",
        	        width : 200
    	        }
    		}]
    	}]
    }]
};


// 点击按钮，初始化弹出框
Ext.onReady(function() {
	var button = Ext.get('show-btn');
	button.on('click', function() {
		var centerPanel = new Ext.FormPanel({
			region : 'center',
			items : [ {
				title : '',
				layout : 'form',
				border : false,
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
					style :'margin-top:10px;',
					items : [{
						xtype : 'checkbox',
						name : 'chixushijian',
						boxLabel : '持续时间',
						style : 'margin-left:15px;',
						inputValue : 1,
						checked : false
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						width : 110,
						items :timeRangeCombo
					},{
			    		layout : 'form',
			    		border : false,
			    		width :40,
			    		items : {
			    			xtype: "numberfield",
			    			style : 'margin-left:-100px;',
			    			width : 35,
			    			allowDecimals:false,//不允许输入小数 
			    			nanText:'请输入有效整数',//无效数字提示
			    			maxValue:365,//最大值                 
			    			maxText:'值太大',
			    			minValue:0,//最小值        
			    			minText:'值太小'
			    		}
			    	},{
			    		layout : 'form',
			    		border : false,
			    		items : {
			    			border : false,
			    		    tag:'span',
			    		    html:'<font size="4">天</font>'
			    		}
			    	},{
			    		layout : 'form',
			    		border : false,
			    		width :40,
			    		items : {
			    			xtype: "numberfield",
			    			style : 'margin-left:-100px;',
			    			width : 35,
			    			allowDecimals:false,//不允许输入小数 
			    			nanText:'请输入有效整数',//无效数字提示
			    			maxValue:365,//最大值                 
			    			maxText:'值太大',
			    			minValue:0,//最小值        
			    			minText:'值太小'
			    		}
			    	},{
			    		layout : 'form',
			    		border : false,
			    		items : {
			    			border : false,
			    		    tag:'span',
			    		    html:'<font size="4">时</font>'
			    		}
			    	},{
			    		layout : 'form',
			    		border : false,
			    		width :40,
			    		items : {
			    			xtype: "numberfield",
			    			style : 'margin-left:-100px;',
			    			width : 35,
			    			allowDecimals:false,//不允许输入小数 
			    			nanText:'请输入有效整数',//无效数字提示
			    			maxValue:365,//最大值                 
			    			maxText:'值太大',
			    			minValue:0,//最小值        
			    			minText:'值太小'
			    		}
			    	},{
			    		layout : 'form',
			    		border : false,
			    		items : {
			    			border : false,
			    		    tag:'span',
			    		    html:'<font size="4">分</font>'
			    		}
			    	}]
				},{
					layout : 'column',
					border : false,
					style :'margin-top:10px;',
					items : [{
						xtype : 'checkbox',
						name : 'chixushijian',
						boxLabel : '业务影响',
						style : 'margin-left:15px;',
						inputValue : 2,
						checked : false
					},{
			    		layout : 'form',
			    		border : false,
			    		width :80,
			    		items : {
			    	        xtype:"radio",
			    	        name : 'affectType',
			    	        fieldLabel:"",
			    	        boxLabel:"影响",
			    	        style : 'margin-left:-80px;',
			    	        checked : true
			    	    }
			    	},{
			    		layout : 'form',
			    		border : false,
			    		items : {
			    	        xtype:"radio",
			    	        name : 'affectType',
			    	        fieldLabel:"",
			    	        boxLabel:"不影响",
			    	        style : 'margin-left:-80px;',
			    	    }
			    	}]
				}]
			}]
		});
		// 创建左侧网元树panel
		var westPanel = new Ext.FormPanel({
			title : '告警源选择',
			region : 'west',// 放在西边，即左侧
			width : 200,
			collapsible : true,// 允许伸缩
		});
		var	win = new Ext.Window({
				title : '当前告警源查询',
//				closable : true,
				width : 820,
				height : 600,
				border : false,
//				plain : true,
				layout : 'border',
				closeAction : 'hide',
				items : [westPanel,centerPanel],
  				buttons:[{
  					text:'确定'
  				},{
  					text:'重置'
  				},{
  					text:'取消'
  				}],

			});
		win.show();
	});
});
