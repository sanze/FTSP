
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
					layout : 'form',
					border : false,
					style : 'margin-left:10px;margin-top:20px;',
					items : {
						xtype : 'textfield',
						fieldLabel : '设备管理域名',
						width : 200
					}
				},{
					layout : 'form',
					border : false,
					style : 'margin-left:10px;margin-top:20px;',
					items : {
						xtype : 'textarea',
						fieldLabel : '描述',
						width : 500,
						height : 50
					}
				},{
					tag : 'span',
					border : false,
					style : 'margin-left:10px;margin-top:20px;',
					html : '<font size="2">配置设备</font>'
				},{
					layout : 'form',
					border : false,
					style : 'margin-left:10px;',
					items :[{
						xtype : 'textarea',
						height : 200,
						width : 400,
						value : '系统->用户->用户管理—>增、删、改、查、执行'
					}]
				}]
			}]
		});
		
		var	win = new Ext.Window({
				title : '详情',
				width : 820,
				height : 580,
				border : false,
				layout : 'border',
				closeAction : 'hide',
				items : centerPanel,
  				buttons:[{
  					text:'关闭'
  				}],

			});
		win.show();
	});
});
