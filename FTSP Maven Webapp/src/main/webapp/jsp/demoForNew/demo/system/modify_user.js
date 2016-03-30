//厂家下拉框
var departCombo = new Ext.form.ComboBox({
	fieldLabel : '部门',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '一部' ], [ '2', '二部' ], [ '3', '三部' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'一部',
	mode : 'local',
	triggerAction : 'all',
	width : 100,
//	anchor : '90%',
	listeners : {
		select : function(combo, record, index) {
		}
	}
});

//职位下拉框
var zhiweiCombo = new Ext.form.ComboBox({
	fieldLabel : '职位',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '部长' ], [ '2', '科长' ], [ '3', '担当' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'一部',
	mode : 'local',
	triggerAction : 'all',
	width : 100
});



// 点击按钮，初始化弹出框
Ext.onReady(function() {
	var button = Ext.get('show-btn');
	button.on('click', function() {
		var centerPanel = new Ext.FormPanel({
			region : 'center',
			items : [{
				layout : 'form',
				border : false,
				items : [{
					layout : 'column',
					border : false,
					labelWidth : 60,//
					style : 'margin-top:10px;',
					items : [{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : {
							xtype : 'textfield',
							fieldLabel : '姓名',
							emptyText : '张三',
							allowBlank : true,
							width : 100,}
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : {
							xtype : 'textfield',
							fieldLabel : '登录名',
							allowBlank : true,
							emptyText : 'haha',
							width : 100
							}
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : {
							xtype : 'textfield',
							fieldLabel : '工号',
							emptyText : 'CSZ1001',
//							value : 'CSZ1001',
							allowBlank : true,
							width : 100,}
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : departCombo
					}]
				},{
					layout : 'column',
					border : false,
					labelWidth : 60,//
					style : 'margin-top:20px;',
					items : [{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : {
							xtype : 'textfield',
							fieldLabel : '手机',
							allowBlank : true,
							emptyText : '138xxxxx',
							width : 100
							}
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : {
							xtype : 'textfield',
							fieldLabel : '邮箱',
							allowBlank : true,
							emptyText : '138xxxxx',
							width : 100
							}
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : zhiweiCombo
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:10px;',
						items : new Ext.Button({
									style : 'margin-left:15px;',
									text : '修改密码'
								})
					}]
				},{
					layout : 'form',
					border : false,
					labelWidth : 60,//
					style : 'margin-left:10px;margin-top:20px;',
					items : {
						xtype : 'textfield',
						width : 500,
						fieldLabel : '备注'
					}
				},{
					layout : 'column',
					border : false,
					style : 'margin-left:10px;margin-top:20px;',
					items : [{
						layout : 'form',
						border : false,
						items : [{
							tag : 'span',
							html : '<font size="2">设备管理域</font>'
						},{
							xtype : 'textarea',
							style : 'margin-left:-105px;',
							height : 200,
							width : 250
						}]
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:100px;',
						items : [{
							tag : 'span',
							html : '<font size="2">权限管理域</font>'
						},{
							xtype : 'textarea',
							style : 'margin-left:-105px;',
							height : 200,
							width : 250
						}]
					}]
				}]
			}]
		});
		
		var	win = new Ext.Window({
				title : '修改用户',
				width : 820,
				height : 580,
				border : false,
				layout : 'border',
				closeAction : 'hide',
				items : centerPanel,
  				buttons:[{
  					text:'确认'
  				},{
  					text:'重置'
  				},{
  					text:'取消'
  				}],

			});
		win.show();
	});
});
