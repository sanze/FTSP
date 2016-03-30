//数据库选择
var recoverDatabaseChoosePanel = {
		id : 'recoverDatabaseChoosePanel',
		style : 'margin-left:20px;margin-top:20px;',
		height : 20,
		border : false,
		layout : 'column',
		items : [
			{
				border : false,
				style : 'padding-top:3px;',
				width:80,
				html : '<span>数据库:</span>'
			}, 
	         {
				id:'recoverDataChoose',
				width:400,
				xtype: 'radiogroup',
				bodyStyle : 'padding:0 0 0 80px',
				items:[
			       {
			           inputValue: '0',
			           name: 'recoverDataChooseName',
			           boxLabel: '主数据库',
			           checked:true
			       }, {
			           inputValue: '1',
			           name: 'recoverDataChooseName',
			           boxLabel: '告警及日志数据库'
			       }
	        ]}
	    ]
};




//恢复路径
var autoBackupPathChoose = {
	id : 'recoverBackupPathChoose',
	style : 'margin-left:20px;margin-top:40px;',
	border : false,
	layout : 'column',
	items : [
		{
			border : false,
			style : 'padding-top:3px;',
			html : '<span>恢复路径：</span>'
		},
		{
	        xtype : 'textfield',
	        id : 'recoverBackupPath',
	        name : 'recoverBackupPath',
	        width : 360,
	        height : 20,
	        allowBlank : true,
	        anchor : '95%'
	    }
    ]
};




//立即恢复按钮
var autoBackupButton = {
	id : 'recevorButton',
	style : 'margin-left:460px;margin-top:40px;margin-bottom:20px;',
	height : 20,
	border : false,
	layout : 'column',
	items : [
		{
			xtype: 'button',
		    text: "立即恢复",
		    height:20,
		    width: 60,
		    listeners: { "click": function () {
		    	beginRecover();
		     }
		    }
		}
]};


var dataRecoverPanel = new Ext.FormPanel({
	region : 'center',
	border :false,
	items : [{
		layout : 'form',
		border : true,
		style : 'padding:20px;',
		width : 800,
		items:[recoverDatabaseChoosePanel,autoBackupPathChoose,
		       {
					border : false,
					style : 'margin-left:20px;margin-top:40px;',
					width:700,
					html : '<span>数据恢复过程可能会影响系统正常使用,建议停止系统应用后再进行恢复操作!</span>'
				}, 
		       autoBackupButton]
	}]
});


function beginRecover(){
	var recoverPath=Ext.getCmp('recoverBackupPath').getValue();
	if(recoverPath==undefined || recoverPath==null || recoverPath==''){
		Ext.Msg.alert("提示", "请输入恢复路径!");
		return;
	}
	
	Ext.Ajax.request({
	    url: 'data-backup!beginRecover.action', 
	    method : 'POST',
	    params: {
	    	'database':Ext.getCmp('recoverDataChoose').getValue().inputValue,
	    	'copyPath':Ext.getCmp('recoverBackupPath').getValue()
	    },
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("提示",obj.returnMessage);
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






