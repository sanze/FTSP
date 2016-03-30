/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */ 
var centerFormPanel = new Ext.FormPanel({
	id : 'centerFormPanel',
	region : "center",
	border : false,
	frame : false,
	labelWidth : 120,
	width : 300,
	bodyStyle : 'padding:10px 10px 0;',
	items : [{				
		layout : 'form',			
		border : false,			
		labelAlign : 'left',			
		items : [{			
			xtype : 'displayfield',		
			value  : '资源关联任务名称',		
			hideLabel : true,		
	        style: {				
	            marginTop : '25px',				
	            marginLeft : '25px'				
	        }				
		},{			
			xtype : 'textfield',		
			id : 'taskNameField',		
			name : 'taskNameField',
			hideLabel : true,
			readOnly : true,
			width : 250,		
	        style: {				
	            marginTop : '5px',				
	            marginLeft : '50px'				
	        }				
		},{			
			xtype : 'displayfield',		
			value  : '描述',		
			hideLabel : true,		
	        style: {				
	            marginTop : '10px',				
	            marginLeft : '25px'				
	        }				
		},{			
			xtype : 'textarea',		
			id : 'descriptionTextArea',		
			name : 'descriptionTextArea',
			hideLabel : true,
			readOnly : true,
			width : 250,
			height:40,		
	        style: {				
	            marginTop : '5px',				
	            marginLeft : '50px'				
	        }				
		},{
			xtype : 'displayfield',
			value  : '同步周期',
			hideLabel : true,
	        style: {
	            marginTop : '10px',
	            marginLeft : '25px'
	        }
		},{
			layout : 'column',
			border : false,
			bodyStyle : 'padding:0px 25px 0',	
			items : [{
				xtype : 'textfield',
				id : 'cycleField',
				name : 'cycleField',
				hideLabel : true,
				width : 100,	
		        style: {			
		            marginTop : '5px',			
		            marginLeft : '25px'			
		        },
		    	listeners : {
		    		focus : function(cycleField){
		    			var periodDisplayFocus = Ext.getCmp("cycleField").getValue().split(" ");
		    			if (periodDisplayFocus[0] == "每天") {
		    				Ext.getCmp("cycleField").setValue(periodDisplayFocus[1]);				
		    			}
		    		},
		            blur: function(cycleField){ 
	            		var periodDisplayBlur = Ext.getCmp("cycleField").getValue();
	            		if (periodDisplayBlur.indexOf(":") == -1) {
	            			Ext.Msg.alert('信息', '同步周期输入格式不对，请按照<br><br>时间格式（00:00）格式输入！');
	            			return;
	            		} else {
	            			var periodDisplayBlurSp = periodDisplayBlur.split(":");
	            			var reg =  /^\d+$/;
            				if (reg.test(periodDisplayBlurSp[0]) && reg.test(periodDisplayBlurSp[1]) 
            						&& periodDisplayBlurSp[0] < 24 && periodDisplayBlurSp[1] < 60 
            						&& periodDisplayBlurSp[0].length <=2 && periodDisplayBlurSp[1].length <=2) {
            					var periodDisplayBlurHours = periodDisplayBlurSp[0];
            					var periodDisplayBlurMinutes = periodDisplayBlurSp[1];
            					if (periodDisplayBlurSp[0] < 10 && periodDisplayBlurSp[0].length < 2) {
            						periodDisplayBlurHours = "0" + periodDisplayBlurSp[0];
            					}
            					if (periodDisplayBlurSp[1] < 10 && periodDisplayBlurSp[1].length < 2) {
            						periodDisplayBlurMinutes = "0" + periodDisplayBlurSp[1];
            					}
            					Ext.getCmp("cycleField").setValue('每天 ' 
            							+ periodDisplayBlurHours + ":" + periodDisplayBlurMinutes);
	            			} else {
	            				Ext.Msg.alert('信息', '同步周期输入格式不对，请按照<br><br>时间格式（00:00）格式输入！');
		            			return;
	            			}
	            		}
                    }  
		    	}
			},{		
				layout : 'form',	
				border : false,	
				items : [{	
					xtype : 'displayfield',
					value  : '挂起',
					hideLabel : true,
			        style: {		
			            marginTop : '10px',		
			            marginLeft : '40px'		
			        }		
				}]	
			},{		
				xtype : 'checkbox',	
				id : 'holdOnCheckbox',	
				name : 'holdOnCheckbox',	
				hideLabel : true,
		        style: {			
		            marginTop : '10px',			
		            marginLeft : '5px'			
		        }	
			}]		
		},{			
			xtype : 'radio',		
			id : 'getAllData',		
			name : 'getAllData',		
			boxLabel : '获取全部数据',		
			hideLabel : true,		
			checked : true,		
	        style: {				
	            marginTop : '25px',				
	            marginLeft : '25px'				
	        }				
		}]			
	}],
	buttonAlign:"right",
	buttons : [{
		text : '确定',
		handler : save
	}, {
		text : '取消',
		handler : close
	}]
});

// =================函数===================
//根据任务Id给设置页面赋初始值
function initData(emsConnectionId) {

	Ext.Ajax.request({
		url : 'associate-resource!getResourceTaskById.action',
		type : 'post',
		params : {
			"rcTaskId" : rcTaskId
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			//任务名称
			Ext.getCmp("taskNameField").setValue(obj.TASK_NAME);
			//任务描述
			Ext.getCmp("descriptionTextArea").setValue(obj.DESCRIPTION);
			//同步周期
			var hours = 0;
			var minutes = 0;
			var periodStandard = null;
			var period = obj.PERIOD;
			if (period/60 < 10) {
				hours = '0' + Math.floor(period/60);
			} else {
				hours = Math.floor(period/60);
			}
			if (period%60 < 10) {
				minutes = '0' + period%60;
			} else {
				minutes = period%60;
			}
			periodStandard = '每天 ' + hours + ':' + minutes;
			Ext.getCmp("cycleField").setValue(periodStandard);
			//挂起圈选框的值
			if (obj.TASK_STATUS == '1') {
				Ext.getCmp("holdOnCheckbox").setValue(false);
			}
			else if (obj.TASK_STATUS == '0') {
				Ext.getCmp("holdOnCheckbox").setValue(true);
			}
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

//确定按钮
function save() {
	//把时间周期转换成分钟数
	var periodNum;
	var periodArray = Ext.getCmp("cycleField").getValue().split(" ");
	//当时间格式正确的情况下才能保存
	if (periodArray.length == 2) {
		var periodNumArray = periodArray[1].split(":");
		var periodNumHours = periodNumArray[0];
		var periodNumMinutes = periodNumArray[1];
		if (periodNumHours < 10 && periodNumHours.length == 2) {
			var periodNumHoursArray = periodNumHours.split("0");
			periodNumHours = periodNumHoursArray[1];
		}
		if (periodNumMinutes < 10 && periodNumMinutes.length == 2) {
			var periodNumMinutesArray = periodNumMinutes.split("0");
			periodNumMinutes = periodNumMinutesArray[1];
		}
		//分钟形式的同步周期
		periodNum = Number(periodNumHours * 60) + Number(periodNumMinutes);
		//任务状态
		var taskStutas;
		if (Ext.getCmp('holdOnCheckbox').getValue() == true) {
			taskStutas = "0";
		}
		else if (Ext.getCmp('holdOnCheckbox').getValue() == false) {
			taskStutas = "1";
		}
		
		var jsonData = {
				"jsonString" : Ext.encode({"PERIOD":periodNum, "RC_TASK_ID":rcTaskId,
					"TASK_STATUS" : taskStutas})
		};
		
		Ext.Ajax.request({
			url : 'associate-resource!setResourceTask.action',
			method : 'POST',
			params : {'jsonString' : Ext.encode({'PERIOD':periodNum, 'RC_TASK_ID':rcTaskId,
					  	  'TASK_STATUS' : taskStutas})},
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					//刷新列表
					var pageTool = parent.Ext.getCmp('pageTool');
					if(pageTool){
						pageTool.doLoad(pageTool.cursor);
					}
					
					// 关闭修改任务信息窗口
					var win = parent.Ext.getCmp('associatedTaskSetting');
					if (win) {
						win.close();
					}
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("提示", obj.returnMessage);
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

//取消按钮
function close() {
	var win = parent.Ext.getCmp('associatedTaskSetting');
	if (win) {
		win.close();
	}
}

Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			initData(rcTaskId);
			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [centerFormPanel]
			});
		});