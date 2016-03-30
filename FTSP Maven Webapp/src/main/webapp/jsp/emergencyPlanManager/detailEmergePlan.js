emergeSet.setHeight(515);
listGrid.setHeight(420);
var myPageSize = 200;
emergeSet.addButton(
	{text:'导出'},
	function(){
		var cell = listGrid.getSelectionModel().getSelections();
		if(cell.length > 0){
			Ext.Msg.confirm('提示','是否下载选中预案？',function(r){
				if(r == 'yes'){
					if(cell.length == 1){
						var filePath = cell[0].get("FILE_PATH");
						window.location.href="download.action?"+Ext.urlEncode({filePath:filePath});
					}else{
						var filePathList = new Array();
						for(var i=0;i<cell.length;i++){
							filePathList.push(cell[i].get("FILE_PATH"));
						}
						Ext.Ajax.request({ 
						    url: 'pm-report!zipReport.action',
						    method : 'POST',
						    params: {
						    	"filePathList":filePathList,
						    	"reportSearchJsonString":""
						    },
						    success: function(response) {
						    	top.Ext.getBody().unmask();
						    	var obj = Ext.decode(response.responseText);
						    	if(obj.returnResult == 1){
						    		window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
						    	}
						    	if(obj.returnResult == 0){
					        		Ext.Msg.alert("提示",obj.returnMessage);
					        	}
						    },
						    error:function(response) {
						    	top.Ext.getBody().unmask();
					        	Ext.Msg.alert("异常",response.responseText);
						    },
						    failure:function(response) {
						    	top.Ext.getBody().unmask();
					        	Ext.Msg.alert("异常",response.responseText);
						    }
						});
					}
				}
			});
		}else{
			Ext.Msg.alert('提示','请先选取预案！');
		}
	}
);

var emergeComfield1 = {
		id : 'emergeComfield1',
		layout : 'column',
		border : false,
		items : [ { 
			border : false,
			layout : 'form',
			columnWidth : 0.5, 
			items : [{ 
				id:'emergePlanNameDis1', 
				xtype :'displayfield',
				fieldLabel  : "预案名称" 
			}]
		}, {
			border : false,
			layout : 'form',
			columnWidth : 0.5,  
			items : [{
				id:'emergeTypeDis1', 
		    	xtype:'displayfield',
		    	fieldLabel  : "预案类型" 
		    }]
	}] 
};

var emergeComfield2 = {
		id : 'emergeComfield2',
		layout : 'column',
		border : false,
		items : [ { 
			border : false,
			layout : 'form',
			columnWidth : 0.25, 
			items : [{ 
				id:'emergePlanNameDis2', 
				xtype :'displayfield',
				fieldLabel  : "预案名称" 
			}]
		}, {
			border : false,
			layout : 'form',
			columnWidth : 0.25,  
			items : [{
				id:'emergeTypeDis2', 
		    	xtype:'displayfield',
		    	fieldLabel  : "预案类型" 
		    }]
	}] 
};
var url = "preview.jsp";
//预演内容的tab
var emergencyContext = new Ext.FormPanel({
	id : 'emergencyContext', 
	region:'center',
	bodyStyle : 'padding:30px 30px 0 30px',
	autoScroll : true,  
	items : [{
		border:false,
		layout : 'column',
		items:[{
			layout : 'form',
			columnWidth: 0.48, 
			border : false,  
			items:[emergeComfield1,emergeSet] 
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
        	border:false,
			items :[{
				xtype: 'spacer', 
	        	border:false,
        		height: 22
        	},{
				xtype:'fieldset', 
		        title: '预案预览', 
		        items:[{
		        	xtype: 'panel', 
		        	border:false,
		            height:480,
		            html : '<iframe id= "preview" frameborder="0" width="100%" height="100%"/>'
		        }] 
        	}]
	    }] 
	}]
});   

var northPanel = new Ext.form.FormPanel({
	id:'northPanel',
//	region:'north', 
	height:50,
	bodyStyle : 'padding:20px 10px 0 ', 
	border : false, 
	autoScroll : true,  
	items:[emergeComfield2]
}); 

var store;

(function(){
		store = new Ext.data.Store({
			url : 'emergency-plan!getExerciseList.action',
		    reader: new Ext.data.JsonReader({
		        totalProperty: 'total',
						root : "rows"
				},[
					"FAULT_EP_EXERCISE_ID","FAULT_EP_ID","DISPALY_NAME",
					"START_TIME","END_TIME","PARTICIPANTS","RESULT","ASSESSMENT",
					"CREATE_TIME","UPDATE_TIME"
			])
		});	
})();
//(function() {
//	var data = [ [ '1', '省网三四期_华为','2010/01/22', '2010/01/22', 'tt', '3' ],
//		[ '2', '省网三四期_华为','2010/01/22', '2010/01/22', 'tt', '3' ]];
//	
//	store = new Ext.data.ArrayStore({
//		fields : [ "EXERCISE_ID","EXERCISE_NAME","START_TIME",
//		         "END_TIME","USERS","RESULT"]
//	});
//	store.loadData(data);
//})();


var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect:false
}); 

var cm = new Ext.grid.ColumnModel({ 
defaults : {
	sortable : false 
},
columns : [
		new Ext.grid.RowNumberer({
			width : 26
		}),checkboxSelectionModel,
		{
			id : 'DISPALY_NAME',
			header : '演习名称',
			dataIndex : 'DISPALY_NAME',
			width : 150
		},{
			id : 'START_TIME',
			header : '演习开始时间',
			dataIndex : 'START_TIME',
			width : 150 
		},{
			id : 'END_TIME',
			header : '演习结束时间',
			dataIndex : 'END_TIME',
			width : 150
		},{
			id : 'PARTICIPANTS',
			header : '参与人员',
			dataIndex : 'PARTICIPANTS',
			width : 200 
		}, {
			id : 'RESULT',
			header : '演习结果',
			dataIndex : 'RESULT',
			width : 80 ,
			renderer:resultRenderer
		}]
});  

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store, 
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
}); 

var exerPanel = new Ext.grid.GridPanel({
	id : 'exerPanel', 
//	region : 'center',
	store : store,
	cm : cm,  
	flex:1,
	frame : false,
	stripeRows : true, // 交替行效果 
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox 
	tbar : [ '-',{
		text : '新增',
		icon : '../../resource/images/btnImages/add.png',
		privilege : addAuth,
		handler:function (){
			editExercise(0);
		}
	},{
		text : '删除',
		icon : '../../resource/images/btnImages/delete.png',
		privilege : delAuth,
		handler : function(){
			deleteExercise();
		}
	},{
		text : '修改',
		icon : '../../resource/images/btnImages/modify.png',
		privilege : modAuth,
		handler :function (){ 
			editExercise(1);
		}
	} ,'-',{
		text : '详情',
		icon : '../../resource/images/btnImages/information.png',
		privilege : viewAuth,
		handler : detailExercise
	}],
	bbar : pageTool
	});  

//演习列表tab 
var exerciseList = new Ext.Panel({
	id : 'exerciseList', 
	layout : 'vbox',
	layoutConfig:{
		align:"stretch",
		pack:"start"
	},
	items:[northPanel,exerPanel]
}); 

//新增或修改演习
function editExercise(type){
	var title="",url="";
	if(type==0){
		title="新增演习";
		url= "editExercise.jsp?FAULT_EP_ID="+FAULT_EP_ID+"&editType="+type;
	}else if(type==1){
		title="修改演习";
		var cell = exerPanel.getSelectionModel().getSelections();
		if(cell.length!=1){
			Ext.Msg.alert("提示","请选择一条需要修改的演习！");
			return;
		} 
		url = "editExercise.jsp?FAULT_EP_EXERCISE_ID="+cell[0].get('FAULT_EP_EXERCISE_ID')+"&FAULT_EP_ID="+FAULT_EP_ID+"&editType="+type; 
	}

	var editWindow=new Ext.Window({
        id:'editExerciseWindow',
        title:title,
        width : Ext.getBody().getWidth()*0.3,      
        height : Ext.getBody().getHeight()-250, 
        isTopContainer : true,
        modal : true,
        autoScroll:false,
		maximized:false,
		html : '<iframe src='
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
     });
    editWindow.show();  
} 

//删除演习
function deleteExercise(){
	var ids = new Array();
	var cell = exerPanel.getSelectionModel().getSelections();
	if(cell.length<1){
		Ext.Msg.alert("提示","请选择需要删除的演习！");
		return;
	} 
	Ext.Msg.confirm('提示', '确认删除？', function(btn) {
		if (btn == "yes") {
		    for(var i = 0; i< cell.length;i++){
		    	ids.push(cell[i].get("FAULT_EP_EXERCISE_ID"));
		    }
		    var jsonData = {
				"ids":ids
			};
		    var jsonString = Ext.encode(jsonData);
			Ext.getBody().mask('正在执行，请稍候...');
		    Ext.Ajax.request({ 
				url: 'emergency-plan!deleteExerciseByIds.action',
				method : 'POST',
				params: {"jsonString":jsonString},
				success: function(response) {
					Ext.getBody().unmask();
				    var obj = Ext.decode(response.responseText);
				    if(obj.returnResult == 1){
				    	Ext.Msg.alert("信息", "删除成功！", function(r) {
				    		//刷新数据
			    			var pageTool = Ext.getCmp('pageTool');
			            	if(pageTool){
			    				pageTool.doLoad(pageTool.cursor);
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
	});
}


//演习详情
function detailExercise(){
	var cell = exerPanel.getSelectionModel().getSelections();
	if(cell.length!=1){
		Ext.Msg.alert("提示","请选择一条演习记录！");
		return;
	} 
	var title="演习详情"; 
	var url = "detailExercise.jsp?FAULT_EP_EXERCISE_ID="+cell[0].get('FAULT_EP_EXERCISE_ID');  
	var detailWindow=new Ext.Window({
        id:'detailWindow',
        title:title,
        width : Ext.getBody().getWidth()*0.6,      
        height : Ext.getBody().getHeight()-50, 
        isTopContainer : true,
        modal : true,
        autoScroll:false,
		maximized:false,
		html : '<iframe src='
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
     });
	detailWindow.show();  
} 
  
var tabPanel = new Ext.TabPanel({
	region : 'center',
	activeTab : 0, 
	deferredRender : false,
	items : [ {
		layout : 'fit',
		id : 'tab1',
		title : '预案内容',
		items : [ emergencyContext ]
	}, {
		layout : 'fit',
		id : 'tab2',
		title : '演习列表',
		items : [exerciseList],
		listeners:{
			activate :function(tab){
				store.load();
			 }
		}
	}]
});

//初始化store参数
function initExerciseStore(){
	var jsonData = {
		"FAULT_EP_ID":FAULT_EP_ID
	};
	var jsonString = Ext.encode(jsonData);
	
	store.baseParams = {
		"jsonString":jsonString,
		"limit" : myPageSize
	};
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

function resultRenderer(v, m, r) {
	var result = v;
	if(typeof v == 'number'){
		switch(v){
		case 0:
			result = "失败";
			break;
		case 1:
			result = "成功";
			break;
		}
	}
	return result;
}

function initData(){
	//初始化演习列表
	initExerciseStore();
	//初始化预案列表
	initEmergencyPlanContent();
}

Ext.onReady(function (){
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Ajax.timeout = 90000000;
	var win = new Ext.Viewport({
		id:'win',
		layout:'border',
		items:[tabPanel]
	});
	win.show(); 
	Ext.getCmp('emergePlanNameDis1').setValue(emergeName);
	Ext.getCmp('emergePlanNameDis2').setValue(emergeName);
	Ext.getCmp('emergeTypeDis1').setValue(emergeTypeName);
	Ext.getCmp('emergeTypeDis2').setValue(emergeTypeName);
	
	initData();
		
});