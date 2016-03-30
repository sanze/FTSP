var centerPanel = new Ext.Panel({ 
		id:'timeParamPanel',
		region:'center',
		bodyStyle : 'padding:40px 20px 0px 30px', 
		items :[{ 
			layout : 'form',
			border:false,
			items:[{
				layout : 'column',
				border : false,
				items : [{ 
					border : false, 
					html : '<span><font size=2px>告警收敛时延：</font></span>'
				},{
					id:'timeParam',
					xtype:'numberfield',
					maxValue:300,
					minValue:60,
					allowDecimals:false,
					width:100
				},{ 
					border : false, 
					html : '<span><font size=2px>秒  （60~300秒）</font></span>'
				}]
			},{
				border : false, 
				style: 'margin-top:5px',
				html : '<span><font size=2px>告警收敛规则，将收敛时延范围内的告警；</font></span>'
			},{
				border : false, 
				style: 'margin-top:5px',
				html : '<span><font size=2px>注意：告警收敛时延如果设置过短，可能会导致告警收敛不完全。</font></span>'
			}]
		}],
		buttons : [ {
			text : '确定',
			handler : setConvergeTime
		}, {
			text : '取消 ',
			handler : function() {
				var win = parent.Ext.getCmp('timeParamWin');
				if (win) {
					win.close();
				}
			}
		}]
	});  

/**
 * 设置延迟收敛时间
 */
function setConvergeTime(){   
	var second = Ext.getCmp('timeParam').getValue(); 
	if (Ext.getCmp("timeParam").isValid()) {
		Ext.Ajax.request({
		    url: 'alarm-converge!setConvergeTime.action',
		    method: 'POST',
		    params: {'paramMap.PARAM_VALUE':Ext.getCmp('timeParam').getValue()},
		    success : function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	if (obj.returnResult == 1) {  
		    		Ext.Msg.alert("提示", obj.returnMessage, function(btn) { 
						var win = parent.Ext.getCmp('timeParamWin');
						if (win) {
							win.close();
						}   
		    		}); 
				}
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		});
	}
}

/**
 * 初始化延迟收敛时间
 */
function getConvergeTime(){   
	Ext.Ajax.request({
	    url: 'alarm-converge!getConvergeTime.action',
	    method: 'POST', 
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if (obj.returnResult == 1) {
	    		Ext.getCmp('timeParam').setValue(obj.result.PARAM_VALUE); 
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

 
Ext.onReady(function(){
	Ext.QuickTips.init(); 
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){
 		top.Ext.menu.MenuMgr.hideAll();
	};  

  	var win =new Ext.Viewport({
  		id:'win',
        layout : 'border', 
        items : centerPanel
	});
	getConvergeTime();
 });