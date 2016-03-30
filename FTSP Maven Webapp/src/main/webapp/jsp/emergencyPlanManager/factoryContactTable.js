var myPageSize = 200;
var store;
(function() {
/*	//测试数据
	var data = [ [ '1', '省网三四期_华为',  'admin','2010/01/22', '2010/01/22', 'tt', '3' ],
			[ '2', '省网三四期_华为', 'admin','2010/01/22', '2010/01/22', 'tt', '3' ]];*/
	store = new Ext.data.Store({
		url : 'emergency-plan!getFactoryContactList.action',
		baseParams : {
			"limit" : myPageSize
		},
	    reader: new Ext.data.JsonReader({
	        totalProperty: 'total',
					root : "rows"
			},[
				"FAULT_FACTORY_CONTACT_ID","FACTORY","CONTACT_PERSON",
				"TEL","AREA","ADDRESS","HOT_LINE",
				"NOTE","CREATE_TIME",
				"UPDATE_TIME"
		])
	});
/*	store = new Ext.data.ArrayStore({
		fields : ["FAULT_FACTORY_CONTACT_ID","FACTORY","CONTACT_PERSON","TEL",
		  	    "AREA","ADDRESS","HOT_LINE","NOTE"]
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
				id : 'FACTORY',
				header : '设备厂家',
				dataIndex : 'FACTORY',
				width : 200
			},{
				id : 'CONTACT_PERSON',
				header : '技术支持联系人',
				dataIndex : 'CONTACT_PERSON',
				width : 150 
			},{
				id : 'TEL',
				header : '联系方式',
				dataIndex : 'TEL',
				width : 150 
			},{
				id : 'AREA',
				header : '服务区域',
				dataIndex : 'AREA',
				width : 200
			},{
				id : 'ADDRESS',
				header : '厂家地址',
				dataIndex : 'ADDRESS',
				width : 200 
			}, {
				id : 'HOT_LINE',
				header : '厂家服务热线',
				dataIndex : 'HOT_LINE',
				width : 150  
			} , {
				id : 'NOTE',
				header : '备注',
				dataIndex : 'NOTE',
				width : 250 
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
		text : '新增',
		icon : '../../resource/images/btnImages/add.png',
		privilege : addAuth,
		handler : function(){
			editFactoryInfo(0);
		} 
	},{
		text : '删除',
		icon : '../../resource/images/btnImages/delete.png',
		privilege : delAuth,
		handler : function(){
			deleteFactoryContact();
		}
	},{
		text : '修改',
		icon : '../../resource/images/btnImages/modify.png',
		privilege : modAuth,
		handler :function (){ 
			editFactoryInfo(1);
		}
	}],
	bbar : pageTool
});   
 
//新增或修改预案
function editFactoryInfo(type){
	var title="",url="";
	if(type==0){
		title="新增厂家联系方式";
		url= "editFactory.jsp?editType="+type; 
	}else if(type==1){ 
		var cell = gridPanel.getSelectionModel().getSelections();
		if(cell.length!=1){
			Ext.Msg.alert("提示","请选择一条要修改的厂家！");
			return;
		} 
		title="修改厂家联系方式";
		url = "editFactory.jsp?factoryContactId="+cell[0].get('FAULT_FACTORY_CONTACT_ID')+"&editType="+type; 
	}

	var editWindow=new Ext.Window({
        id:'editWindow',
        title:title,
        width : Ext.getBody().getWidth()*0.35,      
        height : Ext.getBody().getHeight()-300, 
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


//新增或修改预案
function deleteFactoryContact(){
	var ids = new Array();
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length<1){
		Ext.Msg.alert("提示","请选择需要删除的厂家！");
		return;
	} 
	Ext.Msg.confirm('提示', '确认删除？', function(btn) {
		if (btn == "yes") {
		    for(var i = 0; i< cell.length;i++){
		    	ids.push(cell[i].get("FAULT_FACTORY_CONTACT_ID"));
		    }
		    var jsonData = {
				"factoryContactIds":ids
			};
		    var jsonString = Ext.encode(jsonData);
			Ext.getBody().mask('正在执行，请稍候...');
		    Ext.Ajax.request({ 
				url: 'emergency-plan!deleteFactoryContactByIds.action',
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
//	Ext.Ajax.timeout = 90000000;
	var win = new Ext.Viewport({
		id:'win',
		layout:'border',
		items:[gridPanel]
	});
	win.show();
});