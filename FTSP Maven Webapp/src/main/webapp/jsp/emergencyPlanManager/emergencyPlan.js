var myPageSize = 200;
//查询条件
var searchPanel = new Ext.form.FormPanel({
	id:'searchPanel',
	region:'north',
	height:60,
	bodyStyle : 'padding:20px 10px 0 40px', 
	autoScroll : true,
	items:[{
		border : false, 
		layout : 'column',
		items:[{
			width : 300,
			layout : 'form',
			labelSeparator : "：",
			labelWidth : 60,
			border : false,
			items:[{
				id:'emergencyPlanName',
				xtype:'textfield',
				fieldLabel:'预案名称',
				anchor : '90%'
			}]
		},{
			width : 200,
			layout : 'form',
			labelSeparator : "：",
			labelWidth : 60,
			border : false,
			items:[emergencyType] 
		},{ 
			width : 200,
			layout : 'form',
			labelSeparator : "：",
			labelWidth : 50,
			border : false,
			items:[{
				id:'keyWord',
				xtype:'textfield',
				fieldLabel:'关键字',
				anchor : '90%'
			}] 
		}] 
	}]
});

var store;
(function() {
/*	测试数据
 * var data = [ [ '1', '省网三四期_华为', 3, 'admin','2010/01/22', '2010/01/22', 'tt', '3' ],
			[ '2', '省网三四期_华为', 3, 'admin','2010/01/22', '2010/01/22', 'tt', '3' ]];*/
	store = new Ext.data.Store({
		url : 'emergency-plan!getEmergencyPlanList.action',
		baseParams : {
//			"jsonString":'',
			"limit" : myPageSize
		},
	    reader: new Ext.data.JsonReader({
	        totalProperty: 'total',
					root : "rows"
			},[
				"FAULT_EP_ID","DISPALY_NAME","EP_TYPE",
				"KEY_WORD","NOTE","CREATE_USER","CREATE_TIME","CREATE_TIME_DISPLAY",
				"UPDATE_TIME","UPDATE_TIME_DISPLAY","USER_NAME"
		])
	});
/*	store = new Ext.data.ArrayStore({
		fields : [ "EMERGENCY_ID","EMERGENCY_NAME","EMERGENCY_TYPE",
		   	    "CREATE_USER","CREATE_TIME","UPDATE_TIME","KEYWORD","NOTE"]
	});
	store.loadData(data);*/
	store.load();
})();


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
				header : '预案名称',
				dataIndex : 'DISPALY_NAME',
				width : 160
			},{
				id : 'EP_TYPE',
				header : '预案类型',
				dataIndex : 'EP_TYPE',
				width : 120,
				renderer:typeRenderer
			},{
				id : 'USER_NAME',
				header : '创建人',
				dataIndex : 'USER_NAME',
				width : 120
			} , {
				id : 'KEY_WORD',
				header : '关键字',
				dataIndex : 'KEY_WORD',
				width : 120 
			} , {
				id : 'NOTE',
				header : '备注',
				dataIndex : 'NOTE',
				width : 250,
				hidden:true
			},{
				id : 'CREATE_TIME_DISPLAY',
				header : '创建时间',
				dataIndex : 'CREATE_TIME_DISPLAY',
				width : 200 
			}, {
				id : 'UPDATE_TIME_DISPLAY',
				header : '修改时间',
				dataIndex : 'UPDATE_TIME_DISPLAY',
				width : 200 
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
 
var gridPanel = new Ext.grid.GridPanel({
	id : 'gridPanel',
	region : 'center',
	store : store,
	cm : cm,
	frame : false,
	stripeRows : true, // 交替行效果 
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox 
	tbar : [ '-', {
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : function(){
			searchEmergencyPlan();
		}
	}, '-', {
		text : '重置',
		icon : '../../resource/images/btnImages/arrow_undo.png',
		privilege : actionAuth,
		handler : function(){
			searchPanel.getForm().reset();
		}
	},'-', {
		text : '新增',
		icon : '../../resource/images/btnImages/add.png',
		privilege : addAuth,
		menu : {
			items : [ {
				text : '设备预案',
				handler : function(){
					editEemergencyPlan(0,"设备预案",1);
				}
			}, {
				text : '网管预案',
				handler : function(){
					editEemergencyPlan(0,"网管预案",2);
				}
			} , {
				text : '电路预案',
				handler : function(){
					editEemergencyPlan(0,"电路预案",3);
				}
			}, {
				text : '传输系统预案',
				handler : function(){
					editEemergencyPlan(0,"传输系统预案",4);
				}
			}, {
				text : '机房环境预案',
				handler : function(){
					editEemergencyPlan(0,"机房环境预案",5);
				}
			}, {
				text : '后勤保障预案',
				handler : function(){
					editEemergencyPlan(0,"后勤保障预案",6);
				}
			}, {
				text : '其他预案',
				handler : function(){
					editEemergencyPlan(0,"其他预案",99);
				}
			}]
		}
	},{
		text : '删除',
		icon : '../../resource/images/btnImages/delete.png',
		privilege : delAuth,
		handler : function(){
			deleteEmergencyPlan();
		}
	},{
		text : '修改',
		icon : '../../resource/images/btnImages/modify.png',
		privilege : modAuth,
		handler :function (){
			editEemergencyPlan(1);
		}
	} ,'-',{
		text : '详情',
		icon : '../../resource/images/btnImages/information.png',
		privilege : viewAuth,
		handler : detailEmergePlan
	}],
	bbar : pageTool
});   

function detailEmergePlan(){  
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length==0){
		Ext.Msg.alert("提示","请选择一条记录！");
		return;
		}
	var url= "../emergencyPlanManager/detailEmergePlan.jsp?emergeName="+cell[0].get('DISPALY_NAME')+"&emergeTypeName="+typeRenderer(cell[0].get('EP_TYPE'))+"&emergeTypeValue="+cell[0].get('EP_TYPE')+"&FAULT_EP_ID="+cell[0].get('FAULT_EP_ID');  
	parent.addTabPage(url, "预案详情", authSequence);  
}

//新增或修改预案
function editEemergencyPlan(type,name,value){
	var title="",url="";
	if(type==0){
		title="新增预案";
		url= "editEmergencyPlan.jsp?emergeTypeName="+name+"&emergeTypeValue="+value+"&editType="+type; 
	}else if(type==1){
		title="修改预案";
		var cell = gridPanel.getSelectionModel().getSelections();
		if(cell.length==0){
			Ext.Msg.alert("提示","请选择要修改的应急预案！");
			return;
		} 
		url = "editEmergencyPlan.jsp?emergeTypeName="+typeRenderer(cell[0].get('EP_TYPE'))+"&emergeTypeValue="+cell[0].get('EP_TYPE')+"&FAULT_EP_ID="+cell[0].get('FAULT_EP_ID')+"&editType="+type; 
	}

	var editWindow=new Ext.Window({
        id:'editWindow',
        title:title,
        width : Ext.getBody().getWidth()*0.6,      
        height : Ext.getBody().getHeight()-100, 
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


//查询应急预案
function searchEmergencyPlan(){
	//查询条件
    var jsonData = {
    	"emergencyPlanName":Ext.getCmp('emergencyPlanName').getValue(),
    	"keyWord":Ext.getCmp('keyWord').getValue(),
    	"emergencyType":Ext.getCmp('emergencyType').getValue()
	};
    var jsonString = Ext.encode(jsonData);
    store.baseParams = {"jsonString":jsonString,"limit" : myPageSize},
    Ext.getBody().mask();
    store.load({
		callback : function(r, options, success) {// 回调函数
			 Ext.getBody().unmask();
			if (success) {

			} else {
				Ext.Msg.alert("错误", '查询失败，请重新查询！');
			};
		}
	});
}


//删除应急预案
function deleteEmergencyPlan(){
	var ids = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length<1){
		Ext.Msg.alert("提示","请选择需要删除的应急预案！");
		return;
	} 
	Ext.Msg.confirm('提示', '确认删除？', function(btn) {
		if (btn == "yes") {
		    for(var i = 0; i< cell.length;i++){
		    	ids.push(cell[i].get("FAULT_EP_ID"));
		    }
		    var jsonData = {
				"emergercyIds":ids
			};
		    var jsonString = Ext.encode(jsonData);
			Ext.getBody().mask('正在执行，请稍候...');
		    Ext.Ajax.request({ 
				url: 'emergency-plan!deleteEmergencyPlanByIds.action',
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
 

Ext.onReady(function (){
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Ajax.timeout = 90000000;
	var win = new Ext.Viewport({
		id:'win',
		layout:'border',
		items:[searchPanel,gridPanel]
	});
	win.show();
});