
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
					layout : 'column',
					border : false,
					style : 'margin-left:10px;margin-top:20px;',
					items : [{
						layout : 'form',
						border : false,
						items :[{
							tag : 'span',
							html : '<font size="2">配置设备</font>'
						},{
							xtype : 'textarea',
							style : 'margin-left:-105px;',
							height : 200,
							width : 250
						}]
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:30px;',
						items : [{
							border : false,
							tag : 'span',
							style : 'margin-top:70px;',
							html : '<font size="2">>></font>'
						},{
							border : false,
							tag : 'span',
							style : 'margin-top:50px;',
							html : '<font size="2"><<</font>'
						}]
					},{
						layout : 'form',
						border : false,
						style : 'margin-left:30px;',
						items :[{
							xtype : 'textarea',
							style : 'margin-left:-105px;',
							height : 220,
							width : 250
						}]
					}]
				}]
			}]
		});
		
		var	win = new Ext.Window({
				title : '修改设备管理域',
				width : 820,
				height : 580,
				border : false,
				layout : 'border',
				closeAction : 'hide',
				items : centerPanel,
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
