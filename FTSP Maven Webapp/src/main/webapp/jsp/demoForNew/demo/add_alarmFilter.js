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
	width : 130,
//	anchor : '90%',
	listeners : {
		select : function(combo, record, index) {
		}
	}
});

//告警名称多选框
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
				style : 'margin-top:10px;',
				items : {
					xtype : 'textfield',
					fieldLabel : '告警名称',
					allowBlank : true,
//					anchor : '90%'
				}
			}]
		}, {
			layout : 'form',
			border : false,
			width : 30,
			items :{
				border : false,
				style : 'margin-left:10px;margin-top:70px;cursor:hand;',
				tag : 'span',
				html : '<font size="2">>></font>'
			}
		}, {
			layout : 'form',
			border : false,
//			labelWidth : 1,
			width : 250,
			style : 'margin-left:10px;',
			items : [ {
				border : false,
//				style : 'margin-left:10px;',
				tag : 'span',
				html : '<font size="2">已选告警名称</font>'
			}, {
				layout : 'column',
//				border : false,
//				style : 'margin-left:44px;',
				cls : 'button-red',
				items : [ {
					border : false,
					tag : 'span',
					html : '<font size="2">厂家</font>'
				}, {
					border : false,
					style : 'margin-left:40px;',
					tag : 'span',
					html : '<font size="2">告警名称</font>'
				} ]
			}, {
				xtype : 'textarea',
				style : 'margin-left:-65px;',
				height : 200,
				width : 250,
				value : '华为       R_LOS'
			} ]
		}]
	}]
};


//告警类型多选框
var alarmType = {
	xtype : 'fieldset',
	title : '告警类型',
	style : 'margin-left:10px;',
	height : 200,
	width : 150,
	items : [{
		layout : 'table',
		border : false,
		style : 'margin-top:8px;',
		items : [{
			xtype : 'checkbox',
			name : 'tongxin',
			boxLabel : '通信',
			style : 'margin-left:15px;',
			inputValue : 1,
			checked : true
		}, {
			xtype : 'checkbox',
			name : 'shebei',
			boxLabel : '设备',
			style : 'margin-left:15px;',
			inputValue : 2,
			checked : false,
		}]
	},{
		layout : 'table',
		border : false,
		style : 'margin-top:16px;',
		items : [{
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
			checked : false,
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
		}]
	},{
		layout : 'table',
		border : false,
		style : 'margin-top:16px;',
		items : [{
			xtype : 'checkbox',
			name : 'lianjie',
			boxLabel : '连接',
			style : 'margin-left:15px;',
			inputValue : 7,
			checked : false
		}]
	}]
};

//告警级别多选框
var alarmLevel = {
	xtype : 'fieldset',
	title : '级别',
	style : 'margin-left:10px;',
	height : 200,
	width : 100,
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

//业务影响多选框
var typeAffect = {
	xtype : 'fieldset',
	title : '业务影响',
	style : 'margin-left:10px;',
	height : 70,
	width : 260,
	items : [{
		layout : 'column',
		border : false,
		style : 'margin-top:10px;',
		items : [{
			layout : 'form',
    		border : false,
			width : 100,
    		items : {
    	        xtype:"radio",
    	        name : 'affectType',
    	        fieldLabel:"",
    	        boxLabel:"影响",
    	        style : 'margin-left:-80px;',
    	    }
		},{
			layout : 'form',
			width : 100,
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
};

//告警源多选框
var alarmSource = {
	xtype : 'fieldset',
	title : '告警源',
	style : 'margin-left:10px;',
	height : 70,
	width : 790,
	items : [{
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			border : false,
			width : 120,
			labelAlign : 'left',
			items : {
    	        xtype :"radio",
    	        name : 'alarmSourceRadio',
    	        fieldLabel :"",
    	        boxLabel :"告警源选择",
    	        style : 'margin-left:-80px;',
    	        checked : true
    	    }
		},{
			layout : 'form',
			border : false,
			items :{
			    xtype: 'box', //或者xtype: 'component',
			    width: 20, //图片宽度
			    height: 20, //图片高度
			    style : 'cursor:hand;',
			    autoEl: {
			        tag: 'img',    //指定为img标签
			        src: '../resource/images/index_top_home.gif'    //指定url路径
			    }
			}
		},{
			layout : 'form',
			border : false,
			style : 'margin-left:60px;',
			width : 145,
			items : {
    	        xtype :"radio",
    	        name : 'alarmSourceRadio',
    	        fieldLabel :"",
    	        boxLabel :"告警源类型选择",
    	        style : 'margin-left:-80px;',
    	    }
		},{
			layout : 'form',
			border : false,
			items :{
			    xtype: 'box', //或者xtype: 'component',
			    width: 20, //图片宽度
			    height: 20, //图片高度
			    style : 'cursor:hand;',
			    autoEl: {
			        tag: 'img',    //指定为img标签
			        src: '../resource/images/index_top_home.gif'    //指定url路径
			    }
			}
		}]
	}]
	
};

// 点击按钮，初始化弹出框
Ext.onReady(function() {
	alert('aaa');
	var button = Ext.get('show-btn');
	button.on('click', function() {
		var centerPanel = new Ext.FormPanel({
			region : 'center',
			items : [ {
				title : '',
				layout : 'form',
				border : false,
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
							items : typeAffect
						}]
					}]
				},{
					layout : 'form',
					border : false,
					items : alarmSource
				},{
					layout : 'form',
					border : false,
					width : 800,
					labelWidth : 50,
					style : 'margin-left:10px;margin-top:10px;',
					items : {
						xtype : 'textfield',
						fieldLabel : '描述',
						allowBlank : true,
						width : 735,
					}
				}]
			}]
		});
		var	win = new Ext.Window({
				title : '新增当前告警过滤器',
				width : 820,
				height : 580,
				border : false,
				layout : 'border',
				closeAction : 'hide',
				items : centerPanel,
  				buttons:[{
  					text:'确定'
  				},{
  					text:'应用'
  				},{
  					text:'取消'
  				},{
  					text:'重置'
  				}],

			});
		win.show();
	});
});
