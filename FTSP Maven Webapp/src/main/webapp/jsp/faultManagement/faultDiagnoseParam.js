var centerPanel = new Ext.Panel({ 
		id:'timeParamPanel',
		region:'center',
		bodyStyle : 'padding:40px 20px 0px 30px', 
		items :[{
			layout : 'form',
			border:false,
			items:[{
				layout : {
					type : 'hbox',
					align : 'middle'
				},
				border : false,
				items : [{ 
					border : false,
					style : 'align:center;',
					html : '<span><font size=2px>故障诊断时延：</font></span>'
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
				html : '<span><font size=2px>注意：故障诊断时延如果设置过短，可能会导致故障分析不准确。</font></span>'
			},{
				layout : {
					type : 'hbox',
					align : 'middle'
				},
				border : false,
				style: 'margin-top:15px',
				items : [{
					border : false,
					html : '<span><font size=2px>自动产生故障是否持续页面推送：</font></span>'
				},{
					xtype : 'radiogroup',
					id : 'pushFlagRadioGroup',
					width : 130,
					items : [{
						boxLabel : '推送',
						id : 'pushRadio',
						name : 'pushFlag',
						inputValue : 1
					},{
						boxLabel : '不推送',
						id : 'noPushRadio',
						name : 'pushFlag',
						inputValue : 2
//						checked: true
					}]
				}]
			}]
		}],
		buttons : [ {
			text : '确定',
			handler : setFaultDiagnoseParam
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
 * 设置故障诊断参数
 */
function setFaultDiagnoseParam(){
	
	if (Ext.getCmp("timeParam").isValid()) {
		Ext.Ajax.request({
		    url: 'fault-diagnose!setFaultDiagnoseParam.action',
		    method: 'POST',
		    params: {
		    	'paramMap.timer' : Ext.getCmp("timeParam").getValue(),
		    	'paramMap.pushFlag' : (Ext.getCmp("pushFlagRadioGroup").getValue().inputValue == 1) ? true : false
		    },
		    success : function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	if (obj.returnResult == 1) {  
		    		Ext.Msg.alert("提示", "故障诊断参数设置成功！", function(btn) { 
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
 * 初始化故障诊断参数
 */
function getFaultDiagnoseParam(){
	Ext.Ajax.request({
	    url: 'fault-diagnose!getFaultDiagnoseParam.action',
	    method: 'POST', 
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if (obj.returnResult == 1) {
	    		Ext.getCmp('timeParam').setValue(obj.timer);
	    		if(obj.pushFlag == 'true'){
	    			Ext.getCmp("pushRadio").setValue(true);
	    		}else{
	    			Ext.getCmp("noPushRadio").setValue(true);
	    		}
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
	getFaultDiagnoseParam();
});