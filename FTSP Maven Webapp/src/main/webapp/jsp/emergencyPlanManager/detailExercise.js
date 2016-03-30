//演习实施步骤
//var stepJsonReader=new Ext.data.JsonReader({
//	totalProperty : 'total',
//	root : "rows"
//	}, [  "description","startTime","endTime","persons","result"]);

var storeStep;
/*(function() {
	var data = [ [1, 'admin','2010/01/22', '2010/01/22', 'tt', '4' ],
			[  2,'admin','2010/01/22', '2010/01/22', 'tt', '3' ]];
	storeStep = new Ext.data.ArrayStore({
		fields : [ "id","description","startTime","endTime","persons","result"]
	});
	storeStep.loadData(data);
})();*/
(function(){
	storeStep = new Ext.data.Store({
		url : 'emergency-plan!getExerciseDetailList.action',
	    reader: new Ext.data.JsonReader({
	        totalProperty: 'total',
					root : "rows"
			},[
				"FAULT_EP_EXERCISE_DETAIL_ID","FAULT_EP_EXERCISE_ID","ACTION_DESC",
				"START_TIME","END_TIME","PARTICIPANTS","RESULT",
				"CREATE_TIME","UPDATE_TIME"
		])
	});	
})();

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect:false
}); 

var editor = new Ext.ux.grid.RowEditor({
    saveText: '更新',
    cancelText: '取消',
    commitChangesText: '●&nbsp;&nbsp; 请先提交你的修改',
    errorText: '错误'
});

var cm = new Ext.grid.ColumnModel({  
	columns : [
		new Ext.grid.RowNumberer({
			width : 26
		}),
//		checkboxSelectionModel,
		{  
			id : 'ACTION_DESC', 
		    header: '<span style="font-weight:bold">操作描述</span>',
			dataIndex : 'ACTION_DESC',
			width: 150,
	        editor: new Ext.form.TextField({
	        	allowBlank: false
	        }),
		    tooltip:'可编辑列'
		},{
			id : 'START_TIME', 
		    header: '<span style="font-weight:bold">开始时间</span>',
			dataIndex : 'START_TIME', 
			width: 150,
		    editor: {
		        xtype : 'textfield',
			      id : 'START_TIME',
			      name : 'START_TIME',
//			      fieldLabel : '演习开始日期', 
			  	anchor : '70%',  
			      cls : 'Wdate',
			      listeners : {
			          'focus' : function (th) {
			              WdatePicker({
			                  el : th.getId(),
			                  isShowClear : false,
			                  readOnly : true,
			                  dateFmt : 'yyyy-MM-dd HH:mm:ss',
			                  autoPickDate : true ,
			                  maxDate:'#F{$dp.$D(\'END_TIME\')}'
			              });
			              this.blur();
			          }
			      }
			  },
		    tooltip:'可编辑列' 
		},{
			id : 'END_TIME', 
		    header: '<span style="font-weight:bold">结束时间</span>',
			dataIndex : 'END_TIME',
			width: 150,
	        editor:  {
	        	xtype : 'textfield',
		        	id : 'END_TIME',
	        	name : 'END_TIME',
//		        	fieldLabel : '演习结束日期',  
	        	readOnly : true,
	        	anchor : '70%', 
	        	cls : 'Wdate',
	        	listeners : {
			          'focus' : function (th) {
			              WdatePicker({
			                  el : th.getId(),
			                  isShowClear : false,
			                  readOnly : true,
			                  dateFmt : 'yyyy-MM-dd HH:mm:ss',
			                  autoPickDate : true,
			                  minDate:'#F{$dp.$D(\'START_TIME\')}'
			              });
			              this.blur();
			          }
	        	}
	        },
		    tooltip:'可编辑列' 
		},{
			id : 'PARTICIPANTS', 
		    header: '<span style="font-weight:bold">参与人员</span>',
			dataIndex : 'PARTICIPANTS',
			width: 150,
	        editor: new Ext.form.TextField({
	        	allowBlank: true
	        }),
		    tooltip:'可编辑列' 
		},{
			id : 'RESULT', 
		    header: '<span style="font-weight:bold">实施结果</span>',
			dataIndex : 'RESULT',
			width: 80,
	        editor: new Ext.form.TextField({
	        	allowBlank: true
	        }),
		    tooltip:'可编辑列' 
		}]
});  

var stepGrid = new Ext.grid.GridPanel({
	id : 'stepGrid', 
	store : storeStep,
	height: Ext.getBody().getHeight()*0.35,
	cm : cm,
	clicksToEdit : 2,
	frame : false,
	stripeRows : true, // 交替行效果 
	plugins: [editor],
//	selModel : checkboxSelectionModel,
	loadMask : {
		msg : '正在执行，请稍后...'
	}
});
 
var stepSet = new Ext.form.FieldSet({ 
    title: '演习实施步骤', 
    height: Ext.getBody().getHeight()*0.5,   
    items :[stepGrid],
    buttons : [{
		text : '新增',
		handler : function() {    
			var defaultData = {
				ACTION_DESC: "",
				START_TIME: "",
				END_TIME:"",
				PARTICIPANTS:"",
				RESULT:""
			}; 
			storeStep.add(new storeStep.recordType(defaultData));   
		}
	}, {
		text : '删除 ',
		handler : function() { 
			var record = stepGrid.getSelectionModel().getSelections();
			storeStep.remove(record);
			stepGrid.getView().refresh();
			stepGrid.getSelectionModel().selectRow(0);
		}
	}, {
		text : '保存 ',
		handler : function() { 
			modifyExerciseDetail();
		}
	} ]
});  


//演习结构评估
//结果评定
var resultMapping = [ [ 1, '成功' ], [ 0, '失败' ]]; 
var result;
(function() {
	var resultStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'result'
		} ]
	});
	resultStore.loadData(resultMapping);
	result = new Ext.form.ComboBox({
		id : 'result',
		fieldLabel : '结果评定',
		mode : 'local',
		store : resultStore,
		displayField : 'result',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '25%',
		resizable : true
	});
})(); 

var resultSet = new Ext.form.FieldSet({ 
    title: '演习结果评估', 
    height: Ext.getBody().getHeight()*0.35,     
    items :[{
    	layout : 'form',  
		border : false, 
		labelWidth:60,
		items:[result,{
				id:'resultText',
				hideLabel:true,
				anchor:'100%',
			    height: Ext.getBody().getHeight()*0.16,  
				xtype:'textfield',
				disabled:true
			}]
    }],
    buttons : [ {
		text : '编辑',
		handler : function() { 
			Ext.getCmp('resultText').setDisabled(false);
		}
	}, {
		text : '保存 ',
		handler : function() { 
			modifyExercise();
		}
	} ]
});  

var formPanel = new Ext.FormPanel({
	id : 'formPanel', 
	region:'center',
	bodyStyle : 'padding:20px 20px 0 20px',
	autoScroll : true,  
	items : [stepSet,resultSet],
	buttons : [ {
		text : '导出',
		handler : function() {
			exportExercise();
		}
	}, {
		text : '关闭 ',
		handler : function() {
			//刷新数据
			var pageTool = parent.Ext.getCmp('pageTool');
        	if(pageTool){
				pageTool.doLoad(pageTool.cursor);
			}
        	var win = parent.Ext.getCmp('detailWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});  

function modifyExercise(){
	var jsonData = {
		"editType":1,
		"FAULT_EP_EXERCISE_ID":FAULT_EP_EXERCISE_ID,
		"RESULT":Ext.getCmp('result').getValue(),
		"ASSESSMENT":Ext.getCmp('resultText').getValue()
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
		    	
//		    	var message = "新增演习成功！";
//		    	
//		    	if(editType ==1){
//		    		message = "修改演习成功！";
//		    	}
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

function modifyExerciseDetail(){
		
	storeStep.commitChanges();
	
	var dataArray = new Array();
	storeStep.each(function(record) {
		if(record.get('ACTION_DESC') == null||record.get('ACTION_DESC') == ""){
			Ext.Msg.alert("提示","操作描述未填写！");
			return;
		}
		var data = {
			"ACTION_DESC":record.get('ACTION_DESC'),
			"START_TIME":record.get('START_TIME'),
			"END_TIME":record.get('END_TIME'),
			"PARTICIPANTS":record.get('PARTICIPANTS'),
			"RESULT":record.get('RESULT')
		};
		dataArray.push(data);
	});
	var jsonData = {
		"FAULT_EP_EXERCISE_ID":FAULT_EP_EXERCISE_ID,
		"records":dataArray
	};
	var jsonString = Ext.encode(jsonData);
	Ext.getBody().mask('正在执行，请稍候...');
    Ext.Ajax.request({ 
		url: 'emergency-plan!modifyExerciseDetail.action',
		method : 'POST',
		params: {"jsonString":jsonString},
		success: function(response) {
			Ext.getBody().unmask();
		    var obj = Ext.decode(response.responseText);
		    if(obj.returnResult == 1){
		    	
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

//导出演习详情
function exportExercise(){
	var jsonData = {
			"FAULT_EP_EXERCISE_ID":FAULT_EP_EXERCISE_ID
	};
	var jsonString = Ext.encode(jsonData);
	Ext.getBody().mask('正在执行，请稍候...');
    Ext.Ajax.request({ 
		url: 'emergency-plan!exportExercise.action',
		method : 'POST',
		params: {"jsonString":jsonString},
		success: function(response) {
			Ext.getBody().unmask();
		    var obj = Ext.decode(response.responseText);
		    if(obj.returnResult == 1){
		    	window.location.href="download.action?"+Ext.urlEncode({filePath:obj.fileName});
            }
            if(obj.returnResult == 0){
            	Ext.Msg.alert("提示","导出失败");
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

//初始化store参数
function initExerciseDetailData(id){
	var jsonData = {
		"FAULT_EP_EXERCISE_ID":id
	};
	var jsonString = Ext.encode(jsonData);
	
	storeStep.baseParams = {
		"jsonString":jsonString
	};
	storeStep.load();
}

//初始化store参数
function initExerciseResult(id){
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
		    Ext.getCmp('result').setValue(obj.RESULT);
		    Ext.getCmp('resultText').setValue(obj.ASSESSMENT);
		},
		error:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		},
		failure:function(response) {
            Ext.Msg.alert("异常",response.responseText);
		}
	});
}

//初始化store参数
function initData(id){
	//初始化演习步骤列表
	initExerciseDetailData(id);
	//初始化评估结果
	initExerciseResult(id);
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
	
	initData(FAULT_EP_EXERCISE_ID);
});