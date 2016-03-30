//割接设备多选框
var clearTime = {
	xtype : 'fieldset',
//	title : '清除时间',
	style : 'margin-left:10px;',
	height : 450,
	width : 290,
	labelWidth : 15,
	border : true,
	autoScroll : true,
	items : [{
		layout : 'form',
		border : false,
		items : [{
			layout : 'form',
			border : false,
			items : {
				xtype : 'checkbox',
				name : 'chixushijian',
				labelSeparator : '',
				fieldLabel : '1',
				boxLabel : '二干:华为一期:于网2:10-南京7500',
				style : 'margin-left:15px;',
				inputValue : 1,
				checked : false 
			}
		},{
			layout : 'form',
			border : false,
			items : {
				xtype : 'checkbox',
				name : 'chixushijian',
				labelSeparator : '',
				fieldLabel : '2',
				boxLabel : '二干:华为一期:于网2:10-南京7500',
				style : 'margin-left:15px;',
				inputValue : 1,
				checked : false 
			}
		},{
			layout : 'form',
			border : false,
			items : {
				xtype : 'checkbox',
				name : 'chixushijian',
				labelSeparator : '',
				fieldLabel : '3',
				boxLabel : '二干:华为一期:于网2:10-南京7500',
				style : 'margin-left:15px;',
				inputValue : 1,
				checked : false 
			}
		},{
			layout : 'form',
			border : false,
			items : {
				xtype : 'checkbox',
				name : 'chixushijian',
				labelSeparator : '',
				fieldLabel : '4',
				boxLabel : '二干:华为一期:于网2:10-南京7500',
				style : 'margin-left:15px;',
				inputValue : 1,
				checked : false 
			}
		}]
	}]
};


//时间下拉框
var timeCombo = new Ext.form.ComboBox({
	id : 'timeCombo',
	name : 'timeCombo',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '1' ], [ '2', '2' ], [ '3', '3' ], [ '4', '4' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'1',
	mode : 'local',
	triggerAction : 'all',
	width : 40,
	style : 'margin-left:-105px;'
});


// 点击按钮，初始化弹出框
Ext.onReady(function() {
	var button = Ext.get('show-btn');
	button.on('click', function() {
		var centerPanel = new Ext.FormPanel({
			region : 'center',
			items : [{
				layout : 'column',
				border : false,
				style : 'margin-top:20px;',
				items : [{
					layout : 'form',
					border : false,
					items : [{
						layout : 'column',
						border : false,
						style : 'margin-left:10px;',
						width : 300,
						items : [{
							border : false,
							style : 'margin-top:5px;',
			    		    tag:'span',
			    		    html:'<font size="2">割接设备</font>'
						},new Ext.Button({
							style:'margin-left:200px;',
							text : '删除',
							//icon:'../../../resource/icons/buttonImages/arrow_out.png',
							handler : function(){
							}
						})]
					},{
						layout : 'form',
						border : false,
						style : 'margin-top:10px;',
						items : clearTime
					}]
				},{
					layout : 'form',
					border : false,
					labelWidth : 60,
					items : [{
						xtype : 'textfield',
						fieldLabel : '任务名',
						width : 220
					},{
						xtype : 'textarea',
						fieldLabel : '描述',
						width : 220
					}]
				},{
					layout : 'column',
					border : false,
					width : 285,
					style : 'margin-top:10px;',
					items : [{
						layout : 'form',
						border : false,
						labelWidth : 60,
						items : {
							xtype : 'datefield',
							fieldLabel : '开始日期',
							width : 150
						}
					},{
						layout : 'form',
						border : false,
						labelWidth : 5,
						items : {
							xtype : 'checkbox',
							boxLabel : '挂起',
							inputValue : 1,
							checked : false
						}
					}]
				},{
					layout : 'form',
					border : false,
					labelWidth : 60,
					style : 'margin-top:10px;',
					items : {
						xtype : 'datefield',
						fieldLabel : '结束日期',
						width : 150
					}
				},{
					layout : 'form',
					border : false,
					style : 'margin-top:10px;',
					items : {
						xtype : 'checkbox',
						boxLabel : '割接期间综告接口自动对接',
						style : 'margin-left:-105px;',
					}
				},{
					layout : 'column',
					border : false,
					style : 'margin-top:10px;',
					width : 285,
					items : [{
						layout : 'form',
						border : false,
						width : 60,
						items : {
							xtype : 'radio',
							boxLabel : '割接前',
							style : 'margin-left:-105px;',
						}
					},{
						layout : 'form',
						border : false,
						width : 40,
						items : timeCombo
					},{
						layout : 'form',
						border : false,
						width : 185,
						items : {
							border : false,
			    		    tag:'span',
			    		    html:'<font size="2">小时自动采集性能值和告警快照</font>'
						}
					}]
				},{
					layout : 'form',
					border : false,
					style : 'margin-top:10px;',
					width : 285,
					items : {
						xtype : 'radio',
						boxLabel : '立即采集性能值和告警快照',
						style : 'margin-left:-105px;',
					}
				},{
					layout : 'form',
					border : false,
					style : 'margin-top:10px;',
					width : 285,
					items : {
						xtype : 'radio',
						boxLabel : '不自动采集性能值和告警快照',
						style : 'margin-left:-105px;',
					}
				}]
			}]
		});
		
		// 创建左侧网元树panel
		var westPanel = new Ext.FormPanel({
			region : 'west',// 放在西边，即左侧
			width : 200,
//			collapsible : true,// 允许伸缩
			items : [{
				layout : 'form',
				border : false,
				items : [{
					layout : 'column',
					border : false,
					items : [{
						layout : 'form',
						border : false,
						labelWidth : 20,
						style : 'margin-left:-20px;',
						items : {
							xtype : 'textfield',
//							fieldLabel : '告警名称',
							allowBlank : true,
							emptyText : '查询内容'
						}
					},{
						layout : 'form',
						border : false,
						labelWidth : 20,
						items : {
							xtype : 'button',
							text : '查询'
						}
					},{
						layout : 'form',
						border : false,
//						labelWidth : 20,
						items : {
							tag : 'span',
							border : false,
							html : '<font size="3">>></font>'
						}
					}]
				}]
			}]
			
		});
		var	win = new Ext.Window({
				title : '割接任务设置',
				width : 820,
				height : 600,
				border : false,
				layout : 'border',
				closeAction : 'hide',
				items : [westPanel,centerPanel],
  				buttons:[{
  					text:'确定'
  				},{
  					text:'取消'
  				},{
  					text:'应用'
  				}],

			});
		win.show();
	});
});
