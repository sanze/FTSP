emergeSet.setHeight(320);
listGrid.setHeight(280);

var fileBrowsebutton = new Ext.ux.form.FileUploadField({
	buttonText : '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;导入&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
	id : 'uploadFile',
	name : 'uploadFile',
	width : 70, 
    buttonOnly: true,
    listeners: {
        'fileselected': function(fb, v){
        	//检测是否pdf格式
        	if (v!=''&&
        			(!/^.*?\.(pdf)$/.test(v)&&
        			!/^.*?\.(xlsx)$/.test(v)&&
        			!/^.*?\.(xls)$/.test(v)&&
        			!/^.*?\.(docx)$/.test(v)&&
        			!/^.*?\.(doc)$/.test(v))) {
        		Ext.Msg.alert("提示","请上传pdf,xlsx,xls,docx,doc格式文档！");
        		return;
        	}else{
        		var needUpload = true;
        		//获取文件名
        		var fileName = v.split("\\")[v.split("\\").length-1];
        		//检测是否含有同名文件
        		storeFile.each(function(record) {
        			if(record.get('DISPALY_NAME') == fileName){
        				needUpload = false;
        				Ext.Msg.alert("提示","存在同名文件，请更改文件名后上传！");
        			}
        		});
        		//上传文档
        		if(needUpload){
            		importFile(fileName);
        		}
        	}
        }
    }
});

var formPanel = new Ext.FormPanel({
	id : 'formPanel', 
	region:'center',
	bodyStyle : 'padding:30px 30px 0 30px',
	fileUpload : true,
	autoScroll : true, 
	items : [{
		border:false,
		layout : 'column',  
		items:[{
			layout : 'form',
			columnWidth: 0.48,
			border : false,
			labelWidth:60,
			items:[{
				id:'EP_NAME',
				xtype:'textfield',
				fieldLabel:'预案名称',
				sideText:'<font color=red>*</font>',
				allowBlank:false,
				anchor : '90%' 
			},{
				id:'EP_TYPE',
				xtype:'displayfield',
				fieldLabel:'预案类型'
			},{
				id:'KEY_WORD',
				xtype:'textfield',
				fieldLabel:'关 键 字',
				anchor : '80%' 
			},emergeSet,{
				labelWidth : 60,
				border : false,
				items : [ {
					layout : 'hbox',
					border : false,
					items : [ {
						border : false,
						flex : 12
					}, 
					fileBrowsebutton, 
					{
						border : false,
						flex : 1
					}, {
						xtype : 'button', 
						width : 70, 
						text : '删除',
						handler : function() { 
							Ext.Msg.confirm('提示', '确认删除？', function(btn) {
								if (btn == "yes") {
									var record = listGrid.getSelectionModel().getSelections();
									if(record.length==0){
										Ext.Msg.alert("提示","请选择需要删除的文件！");
										return;
									}
									storeFile.remove(record);
									listGrid.getView().refresh();
									listGrid.getSelectionModel().selectRow(0);
								}
							});
						}
					}, {
						border : false,
						flex : 1
					}, {
						xtype : 'button', 
						width : 70, 
						text : '确定',
						handler :  function(){
							modifyEmergencyPlan();
						}
					}, {
						border : false,
						flex : 1
					}, {
						xtype : 'button', 
						width : 70, 
						text : '取消',
						handler : function(){
							var editWindow = parent.Ext.getCmp('editWindow');
							if (editWindow) {
								editWindow.close();
							}
						 } 
					}]
				} ]
			}]
		},{
			columnWidth: 0.04, 
	     	border:false ,
			items:[{
				xtype: 'spacer', 
	        	border:false ,
	        	height:20
			}]  
		}, { 
			columnWidth: 0.48,
			xtype:'fieldset',
	        title: '预案预览',     
	        items :[{
	            xtype: 'panel', 
	        	border:false,
	            height:380,
	            html : '<iframe id= "preview" frameborder="0" width="100%" height="100%"/>'
	        }]
	    }] 
	} ] 
});  

//初始化 预案信息
function initEmergencyPlan() {  
	var jsonData = {
			"FAULT_EP_ID":FAULT_EP_ID
	};
	var jsonString = Ext.encode(jsonData);
    Ext.Ajax.request({ 
		url: 'emergency-plan!initEmergencyPlan.action',
		method : 'POST',
		params: {"jsonString":jsonString},
		success: function(response) {
		    var obj = Ext.decode(response.responseText);
		    Ext.getCmp('EP_NAME').setValue(obj.DISPALY_NAME);
		    Ext.getCmp('KEY_WORD').setValue(obj.KEY_WORD);
//		    Ext.getCmp('EP_TYPE').setValue(obj.EP_TYPE);
		},
		error:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		},
		failure:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		}
	});
} 

//初始化 文档信息
function initEmergencyPlanContent() {  
	var jsonData = {
			"FAULT_EP_ID":FAULT_EP_ID
	};
	var jsonString = Ext.encode(jsonData);
	
	storeFile.baseParams = {
		"jsonString":jsonString
	};
	storeFile.load();
} 

function initData(){
	//初始化 预案信息
	initEmergencyPlan() ;
	//初始化 文档信息
	initEmergencyPlanContent();
}

//上传文档
function importFile(fileName) {
	//提交表单
	formPanel.getForm().submit({
		url : 'emergency-plan!importFile.action',
		params : {
			"jsonString" : fileName
		},
		success : function(form, action) {
			var obj = Ext.decode(action.response.responseText);
			// 上传文档
			var defaultData = {
				DISPALY_NAME : obj.DISPALY_NAME,
				FILE_PATH : obj.FILE_PATH
			};
			storeFile.add(new storeFile.recordType(defaultData));
			
    		//重置，不然下次不会触发
    		fileBrowsebutton.reset();
		},
		failure : function(form, action) {
			//总是会走到这个分支，原因不明
			var obj = Ext.decode(action.response.responseText);
			// 上传文档
			var defaultData = {
				DISPALY_NAME : obj.DISPALY_NAME,
				FILE_PATH : obj.FILE_PATH
			};
			storeFile.add(new storeFile.recordType(defaultData));
			//重置，不然下次不会触发
    		fileBrowsebutton.reset();
		}
	});
} 

function modifyEmergencyPlan(){
	if(formPanel.getForm().isValid()){
		//预案列表
		var dataArray = new Array();
		storeFile.each(function(record) {
			var data = {
				"DISPALY_NAME":record.get('DISPALY_NAME'),
				"FILE_PATH":record.get('FILE_PATH')
			};
			dataArray.push(data);
		});
		var jsonData = {
			"editType":editType,
			"FAULT_EP_ID":FAULT_EP_ID,
			"DISPALY_NAME":Ext.getCmp('EP_NAME').getValue(),
			"EP_TYPE":emergeTypeValue,
			"KEY_WORD":Ext.getCmp('KEY_WORD').getValue(),
			"records":dataArray
		};
		var jsonString = Ext.encode(jsonData);
		Ext.getBody().mask('正在执行，请稍候...');
	    Ext.Ajax.request({ 
			url: 'emergency-plan!modifyEmergencyPlan.action',
			method : 'POST',
			params: {"jsonString":jsonString},
			success: function(response) {
				Ext.getBody().unmask();
			    var obj = Ext.decode(response.responseText);
			    if(obj.returnResult == 1){
			    	
			    	var message = "新增应急预案成功！";
			    	
			    	if(editType ==1){
			    		message = "修改应急预案成功！";
			    	}
			    	
					Ext.Msg.alert("信息", message, function(r) {
						// 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						// 关闭修改任务信息窗口
						var win = parent.Ext
								.getCmp('editWindow');
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
	Ext.getCmp('EP_TYPE').setValue(emergeTypeName);
	if(editType==1){ 
		//修改预案
		initData();
	}
});