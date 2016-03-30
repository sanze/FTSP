var startTime = {
    xtype : 'textfield',
    id : 'START_TIME',
    name : 'START_TIME',
    fieldLabel : '演习开始日期', 
	anchor : '70%',  
    cls : 'Wdate',
    listeners : {
        'focus' : function () {
            WdatePicker({
                el : "START_TIME",
                isShowClear : false,
                readOnly : true,
                dateFmt : 'yyyy-MM-dd HH:mm:ss',
                autoPickDate : true,
                maxDate:'#F{$dp.$D(\'END_TIME\')}'
            });
            this.blur();
        }
    }
};

var endTime = {
	xtype : 'textfield',
	id : 'END_TIME',
	name : 'END_TIME',
	fieldLabel : '演习结束日期',  
	readOnly : true,
	anchor : '70%', 
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "END_TIME",
				isShowClear : false,
				readOnly : true,
			    dateFmt : 'yyyy-MM-dd HH:mm:ss',
                autoPickDate : true,
                minDate:'#F{$dp.$D(\'START_TIME\')}'
			});
			this.blur();
		}
	}
};

var formPanel = new Ext.FormPanel({
	id : 'formPanel', 
	region:'center',
	bodyStyle : 'padding:30px 30px 0 30px',
	autoScroll : true,  
	items : [{
		border:false, 
		items:[{
			layout : 'form', 
			border : false,
			labelWidth:80,
			items:[{
				id:'DISPALY_NAME',
				xtype:'textfield',
				fieldLabel:'演 习 名 称',
				sideText:'<font color=red>*</font>',
				allowBlank:false,
				anchor : '70%' 
			},startTime,endTime,
			{
				id:'PARTICIPANTS',
				xtype:'textfield',
				height:140,
				fieldLabel:'演习参与人员', 
				anchor : '90%' 
			}] 
		}]
	}],
	buttons : [ {
		text : '确定',
		handler : function() { 
			modifyExercise();
		}
	}, {
		text : '取消 ',
		handler : function() {
			var win = parent.Ext.getCmp('editExerciseWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});  
  
function initData(){
	var jsonData = {
			"FAULT_EP_EXERCISE_ID":FAULT_EP_EXERCISE_ID
	};
	var jsonString = Ext.encode(jsonData);
    Ext.Ajax.request({ 
		url: 'emergency-plan!initExercise.action',
		method : 'POST',
		params: {"jsonString":jsonString},
		success: function(response) {
		    var obj = Ext.decode(response.responseText);
		    Ext.getCmp('DISPALY_NAME').setValue(obj.DISPALY_NAME);
		    Ext.getCmp('PARTICIPANTS').setValue(obj.PARTICIPANTS);
		    Ext.getCmp('START_TIME').setValue(obj.START_TIME);
		    Ext.getCmp('END_TIME').setValue(obj.END_TIME);
		},
		error:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		},
		failure:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		}
	});
}

function modifyExercise(){
	if(formPanel.getForm().isValid()){
		
		var startTime = Ext.getCmp('START_TIME').getValue();
		startTime = new Date(startTime.replace(/-/g, "/"));
		var endTime = Ext.getCmp('END_TIME').getValue();
		endTime = new Date(endTime.replace(/-/g, "/"));
		if (startTime > endTime) {
			Ext.Msg.alert('提示', '结束时间不能小于开始时间！');
			return;
		}
		var jsonData = {
			"editType":editType,
			"FAULT_EP_ID":FAULT_EP_ID,
			"FAULT_EP_EXERCISE_ID":FAULT_EP_EXERCISE_ID,
			"DISPALY_NAME":Ext.getCmp('DISPALY_NAME').getValue(),
			"START_TIME":Ext.getCmp('START_TIME').getValue(),
			"END_TIME":Ext.getCmp('END_TIME').getValue(),
			"PARTICIPANTS":Ext.getCmp('PARTICIPANTS').getValue()
		};
		var jsonString = Ext.encode(jsonData);
		Ext.getBody().mask('正在执行，请稍候...');
	    Ext.Ajax.request({ 
			url: 'emergency-plan!modifyExercise.action',
			method : 'POST',
			params: {"jsonString":jsonString},
			success: function(response) {
				Ext.getBody().unmask();
			    var obj = Ext.decode(response.responseText);
			    if(obj.returnResult == 1){
			    	
			    	var message = "新增演习成功！";
			    	
			    	if(editType ==1){
			    		message = "修改演习成功！";
			    	}
			    	
					Ext.Msg.alert("信息", message, function(r) {
						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						// 关闭修改任务信息窗口
						var win = parent.Ext
								.getCmp('editExerciseWindow');
						if (win) {
							win.close();
						}
					});
	            }
	            if(obj.returnResult == 0){
	            	Ext.Msg.alert("提示",obj.returnMessage);
	            }
			},
			error:function(response) {
			    Ext.getBody().unmask();
	            Ext.Msg.alert("异常",response.responseText);
			},
			failure:function(response) {
			    Ext.getBody().unmask();
	            Ext.Msg.alert("异常",response.responseText);
			}
		});
	}
} 

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ formPanel ]
	});
	win.show();   
	if(editType==1){ 
		//修改预案
		initData();
	} 
});