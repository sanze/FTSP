var formPanel = new Ext.FormPanel({
	id : 'formPanel',
	height : 120,
	bodyStyle : 'padding:30px 30px 0 30px',
	autoScroll : true,
	defaults : {
		xtype : 'fieldset',
		bodyStyle : 'padding:10px 0 10px 10px'
	},
	items : [ {
		layout : 'column',
		title : '资源预警设置', 
		defaults : {
			columnWidth : 0.5,
			border : false,
			layout : 'form',
			labelWidth : 115,
			labelSeparator : "：",
			defaults : {
				border : false,
				layout : 'hbox',
				layoutConfig : {
					align : 'middle'

				},
				defaults : {
					xtype : 'label'
				}
			}
		},
		items : [ {
			items : [ {
				fieldLabel : '槽道可用率',
				items : [ {
					text : '重要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'SLOT_MJ',
					allowDecimals:false,
					minValue:0, 
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '次要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'SLOT_MN',
					allowDecimals:false,
					minValue:0, 
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '一般≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'SLOT_WR',
					allowDecimals:false,
					minValue:0,
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				} ]
			}, {
				fieldLabel : '交叉连接可用率',
				items : [ {
					text : '重要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'CRS_MJ',
					allowDecimals:false,
					minValue:0,  
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '次要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'CRS_MN',
					allowDecimals:false,
					minValue:0,  
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '一般≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'CRS_WR',
					allowDecimals:false,
					minValue:0,
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				} ]
			},{} ]
		}, {
			items : [ {
				fieldLabel : '端口可用率',
				items : [ {
					text : '重要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'PTP_MJ',
					allowDecimals:false,
					minValue:0,
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '次要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'PTP_MN',
					allowDecimals:false,
					minValue:0,
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '一般≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'PTP_WR',
					allowDecimals:false,
					minValue:0,  
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				} ]
			}, {
				fieldLabel : '复用段VC4可用率', 
				items : [ {
					text : '重要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'MS_VC4_MJ',
					allowDecimals:false,
					minValue:0, 
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '次要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'MS_VC4_MN',
					allowDecimals:false,
					minValue:0,  
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '一般≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'MS_VC4_WR',
					allowDecimals:false,
					minValue:0,  
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				} ]
			},{
				fieldLabel : '复用段VC12可用率', 
				items : [ {
					text : '重要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'MS_VC12_MJ',
					allowDecimals:false,
					minValue:0, 
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '次要≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'MS_VC12_MN',
					allowDecimals:false,
					minValue:0,  
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				}, {
					text : '一般≤',
					width : 35
				}, {
					xtype : 'numberfield',
					id : 'MS_VC12_WR',
					allowDecimals:false,
					minValue:0,  
					maxValue:100,
					flex : 1
				}, {
					text : '%',
					width : 50
				} ]
			}  ]
		} ]
	}, {
		layout : 'form',
		labelWidth : 100,
		labelSeparator : "：",
		xtype : 'fieldset',
		title : '网络结构安全预警设置',
		bodyStyle : 'padding:10px 10px 10px 10px',
		// 对items应用默认配置
		defaults : {
			border : false,
			layout : 'hbox',
			layoutConfig : {
				align : 'middle'

			},
			defaults : {
				xtype : 'label'
			}
		},
		items : [ {
			fieldLabel : '超大环',
			items : [ {
				text : '重要 网元数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'LARGE_RING_MJ',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '次要 网元数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'LARGE_RING_MN',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '一般 网元数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'LARGE_RING_WR',
				allowDecimals:false,
				minValue:0,
				width : 50
			} , {
				text : '个',
				width : 20
			}]
		}, {
			fieldLabel : '长单链',
			items : [ {
				text : '重要 网元数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'LONG_CHAIN_MJ',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '次要 网元数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'LONG_CHAIN_MN',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '一般 网元数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'LONG_CHAIN_WR',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			} ]
		}, {
			fieldLabel : '大汇聚点',
			items : [ {
				text : '重要 链数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'FOCAL_POINT_MJ',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '次要 链数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'FOCAL_POINT_MN',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '一般 链数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'FOCAL_POINT_WR',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}]
		}, {
			fieldLabel : '多环节点',
			items : [ {
				text : '重要 环数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'MULTI_NODE_MJ',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '次要 环数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'MULTI_NODE_MN',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}, {
				text : ' ',
				width : 25
			}, {
				text : '一般 环数≥',
				width : 80
			}, {
				xtype : 'numberfield',
				id : 'MULTI_NODE_WR',
				allowDecimals:false,
				minValue:0,
				width : 50
			}, {
				text : '个',
				width : 20
			}]
		} ]
	} ],
	buttons : [{
		text : '保存',
		privilege:modAuth, 
		handler : applySettings 
	}, {
		text : '重置',
		privilege:viewAuth,
		handler : initData
	}]
});
 
function compareValue(str,important,less,normal){ 
	if(important!='' && important!=0){
		if((less!='' && less!=0) && important>=less){  
			Ext.Msg.alert("提示",str+="重要大于/等于次要，请修正！");  
			return true;
		}
		if((normal!='' && normal!=0)&& important>=normal){ 
			Ext.Msg.alert("提示",str+="重要大于/等于一般，请修正！");  
			return true;
		}
		if((less!='' && less!=0) &&(normal!='' && normal!=0)&& less>=normal){ 
			Ext.Msg.alert("提示",str+="次要大于/等于一般，请修正！");  
			return true;
		}
	}else{ 
		if((less!='' && less!=0)&& (normal!='' && normal!=0) && less>=normal){ 
			Ext.Msg.alert("提示",str+="次要大于/等于一般，请修正！");  
			return true;
		} 
	} 
	return false;
}


function compareValueNet(str,important,less,normal){ 
	if(important!='' && important!=0){
		if((less!='' && less!=0) && important<=less){  
			Ext.Msg.alert("提示",str+="重要小于/等于次要，请修正！");  
			return true;
		}
		if((normal!='' && normal!=0)&& important<=normal){ 
			Ext.Msg.alert("提示",str+="重要小于/等于一般，请修正！");  
			return true;
		}
		if((less!='' && less!=0) &&(normal!='' && normal!=0)&& less<=normal){ 
			Ext.Msg.alert("提示",str+="次要小于/等于一般，请修正！");  
			return true;
		}
	}else{ 
		if((less!='' && less!=0)&& (normal!='' && normal!=0) && less<=normal){ 
			Ext.Msg.alert("提示",str+="次要小于/等于一般，请修正！");  
			return true;
		} 
	} 
	return false;
}

//应用
function applySettings() { 
	//验证表单数据正确性
	if(!formPanel.getForm().isValid()){
		return;
	};
	//判断设置值是否合理
	if(compareValue("槽道可用率",Ext.getCmp('SLOT_MJ').getValue(),
		Ext.getCmp('SLOT_MN').getValue(),Ext.getCmp('SLOT_WR').getValue()))
		return;
	if(compareValue("端口可用率",Ext.getCmp('PTP_MJ').getValue(),
		Ext.getCmp('PTP_MN').getValue(),Ext.getCmp('PTP_WR').getValue()))
		return;
	if(compareValue("交叉连接可用率",Ext.getCmp('CRS_MJ').getValue(),
		Ext.getCmp('CRS_MN').getValue(),Ext.getCmp('CRS_WR').getValue()))
		return;
	if(compareValue("复用段VC4可用率",Ext.getCmp('MS_VC4_MJ').getValue(),
		Ext.getCmp('MS_VC4_MN').getValue(),Ext.getCmp('MS_VC4_WR').getValue()))
		return;
	if(compareValue("复用段VC12可用率",Ext.getCmp('MS_VC12_MJ').getValue(),
			Ext.getCmp('MS_VC12_MN').getValue(),Ext.getCmp('MS_VC12_WR').getValue()))
			return;
	if(compareValueNet("超大环",Ext.getCmp('LARGE_RING_MJ').getValue(),
		Ext.getCmp('LARGE_RING_MN').getValue(),Ext.getCmp('LARGE_RING_WR').getValue()))
		return;
	if(compareValueNet("长单链",Ext.getCmp('LONG_CHAIN_MJ').getValue(),
		Ext.getCmp('LONG_CHAIN_MN').getValue(),Ext.getCmp('LONG_CHAIN_WR').getValue()))
		return;
	if(compareValueNet("大汇聚点",Ext.getCmp('FOCAL_POINT_MJ').getValue(),
		Ext.getCmp('FOCAL_POINT_MN').getValue(),Ext.getCmp('FOCAL_POINT_WR').getValue()))
		return;
	if(compareValueNet("多环节点",Ext.getCmp('MULTI_NODE_MJ').getValue(),
		Ext.getCmp('MULTI_NODE_MN').getValue(),Ext.getCmp('MULTI_NODE_WR').getValue()))
		return;
 
	var map = {  
		"paramMap.SLOT_MJ":Ext.getCmp('SLOT_MJ').getValue()*1.0,
		"paramMap.SLOT_MN":Ext.getCmp('SLOT_MN').getValue()*1.0,
		"paramMap.SLOT_WR":Ext.getCmp('SLOT_WR').getValue()*1.0,
		"paramMap.CRS_MJ":Ext.getCmp('CRS_MJ').getValue()*1.0,
		"paramMap.CRS_MN":Ext.getCmp('CRS_MN').getValue()*1.0,
		"paramMap.CRS_WR":Ext.getCmp('CRS_WR').getValue()*1.0,
		"paramMap.PTP_MJ":Ext.getCmp('PTP_MJ').getValue()*1.0,
		"paramMap.PTP_MN":Ext.getCmp('PTP_MN').getValue()*1.0,
		"paramMap.PTP_WR":Ext.getCmp('PTP_WR').getValue()*1.0,
		"paramMap.MS_VC4_MJ":Ext.getCmp('MS_VC4_MJ').getValue()*1.0,
		"paramMap.MS_VC4_MN":Ext.getCmp('MS_VC4_MN').getValue()*1.0,
		"paramMap.MS_VC4_WR":Ext.getCmp('MS_VC4_WR').getValue()*1.0,
		"paramMap.MS_VC12_MJ":Ext.getCmp('MS_VC12_MJ').getValue()*1.0,
		"paramMap.MS_VC12_MN":Ext.getCmp('MS_VC12_MN').getValue()*1.0,
		"paramMap.MS_VC12_WR":Ext.getCmp('MS_VC12_WR').getValue()*1.0,
		"paramMap.LARGE_RING_MJ":Ext.getCmp('LARGE_RING_MJ').getValue()*1.0,
		"paramMap.LARGE_RING_MN":Ext.getCmp('LARGE_RING_MN').getValue()*1.0,
		"paramMap.LARGE_RING_WR":Ext.getCmp('LARGE_RING_WR').getValue()*1.0,
		"paramMap.LONG_CHAIN_MJ":Ext.getCmp('LONG_CHAIN_MJ').getValue()*1.0,
		"paramMap.LONG_CHAIN_MN":Ext.getCmp('LONG_CHAIN_MN').getValue()*1.0,
		"paramMap.LONG_CHAIN_WR":Ext.getCmp('LONG_CHAIN_WR').getValue()*1.0,
		"paramMap.FOCAL_POINT_MJ":Ext.getCmp('FOCAL_POINT_MJ').getValue()*1.0,
		"paramMap.FOCAL_POINT_MN":Ext.getCmp('FOCAL_POINT_MN').getValue()*1.0,
		"paramMap.FOCAL_POINT_WR":Ext.getCmp('FOCAL_POINT_WR').getValue()*1.0,
		"paramMap.MULTI_NODE_MJ":Ext.getCmp('MULTI_NODE_MJ').getValue()*1.0,
		"paramMap.MULTI_NODE_MN":Ext.getCmp('MULTI_NODE_MN').getValue()*1.0,
		"paramMap.MULTI_NODE_WR":Ext.getCmp('MULTI_NODE_WR').getValue()*1.0
	};
	Ext.Ajax.request({
		url : 'network!updateEarlyAlarmSetting.action',
		method : 'POST',
		params : map,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
		    if(obj.returnResult == 1 || obj.returnResult == 0){
                Ext.Msg.alert("提示",obj.returnMessage);
            }
		},
		error : function(response) {
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			Ext.Msg.alert("错误", response.responseText);
		} 
	}); 
}  

//初始化&&重置
function initData() {
	Ext.Ajax.request({
		url : 'network!getEarlyAlarmSetting.action',
		method : 'POST',
		params : {"jsonString":"*"},
		success : function(response) {
			var obj = Ext.decode(response.responseText); 
			if (obj.returnResult && obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}else{
	            Ext.getCmp('SLOT_MJ').setValue(obj.SLOT_MJ);
	            Ext.getCmp('SLOT_MN').setValue(obj.SLOT_MN);
	            Ext.getCmp('SLOT_WR').setValue(obj.SLOT_WR);
	            Ext.getCmp('CRS_MJ').setValue(obj.CRS_MJ);
	            Ext.getCmp('CRS_MN').setValue(obj.CRS_MN);
	            Ext.getCmp('CRS_WR').setValue(obj.CRS_WR);
	            Ext.getCmp('PTP_MJ').setValue(obj.PTP_MJ);
	            Ext.getCmp('PTP_MN').setValue(obj.PTP_MN);
	            Ext.getCmp('PTP_WR').setValue(obj.PTP_WR);
	            Ext.getCmp('MS_VC4_MJ').setValue(obj.MS_VC4_MJ);
	            Ext.getCmp('MS_VC4_MN').setValue(obj.MS_VC4_MN);
	            Ext.getCmp('MS_VC4_WR').setValue(obj.MS_VC4_WR);
	            Ext.getCmp('MS_VC12_MJ').setValue(obj.MS_VC12_MJ);
	            Ext.getCmp('MS_VC12_MN').setValue(obj.MS_VC12_MN);
	            Ext.getCmp('MS_VC12_WR').setValue(obj.MS_VC12_WR);
	            Ext.getCmp('LARGE_RING_MJ').setValue(obj.LARGE_RING_MJ);
	            Ext.getCmp('LARGE_RING_MN').setValue(obj.LARGE_RING_MN);
	            Ext.getCmp('LARGE_RING_WR').setValue(obj.LARGE_RING_WR);
	            Ext.getCmp('LONG_CHAIN_MJ').setValue(obj.LONG_CHAIN_MJ);
	            Ext.getCmp('LONG_CHAIN_MN').setValue(obj.LONG_CHAIN_MN);
	            Ext.getCmp('LONG_CHAIN_WR').setValue(obj.LONG_CHAIN_WR);
	            Ext.getCmp('FOCAL_POINT_MJ').setValue(obj.FOCAL_POINT_MJ);
	            Ext.getCmp('FOCAL_POINT_MN').setValue(obj.FOCAL_POINT_MN);
	            Ext.getCmp('FOCAL_POINT_WR').setValue(obj.FOCAL_POINT_WR);
	            Ext.getCmp('MULTI_NODE_MJ').setValue(obj.MULTI_NODE_MJ);
	            Ext.getCmp('MULTI_NODE_MN').setValue(obj.MULTI_NODE_MN);
	            Ext.getCmp('MULTI_NODE_WR').setValue(obj.MULTI_NODE_WR);
	    	}
		},
		error : function(response) {
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			Ext.Msg.alert("错误", response.responseText);
		} 
	});
}  

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	initData();
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'fit',
		items : [ formPanel ]
	});
	win.show(); 
});