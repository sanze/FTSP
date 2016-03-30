/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */


var store = new Ext.data.Store({
	//1代表查
	url : 'interface-manage!getAllInterface.action',
	baseParams : {
		"limit":200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["ID","INTERFACE_NAME","OWN_IP","PORT","PEER_IP","USERNAME","PASSWORD",
	    "REMARK"])
});

store.load();

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,//每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});

var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		 sortable : true,
		 forceFit : true,
		 align:'left',
		 renderer: function (data, metadata, record, rowIndex, columnIndex, store) {       
	       	    metadata.attr = 'ext:qtip="' +data+'"';   //关键  
	       	    return data ;     
       	 } 
	},
	columns:[ new Ext.grid.RowNumberer(),
	          checkboxSelectionModel,
				{
					id : 'ID',
					header : 'id',
					dataIndex : 'ID',
					width : 80,
					hidden : true
				},
				{
					id : 'INTERFACE_NAME',
					header : '接口名称',
					dataIndex : 'INTERFACE_NAME',
					width : 60
				},
				{
					id:'OWN_IP',
					header:'IP',
					dataIndex:'OWN_IP',
					width:60					
				},
				{
					id:'PORT',
					header:'端口',
					dataIndex:'PORT',
					width:40					
				},         
				{
					id:'PEER_IP',
					header:'对端IP',
					dataIndex:'PEER_IP',
					width:60					
				}, 
				{
					id:'USERNAME',
					header:'用户名',
					dataIndex:'USERNAME',
					width:60					
				}, 
				{
					id:'PASSWORD',
					header:'密码',
					dataIndex:'PASSWORD',
					width:60					
				}, 
				{
					id:'REMARK',
					header:'备注',
					dataIndex:'REMARK',
					width:100					
				}
]}		
);

var intefaceListPanel = new Ext.grid.GridPanel({
	id : "intefaceListPanel",
	region : "center",
	stripeRows : true,
	autoScroll : true,
	frame : false,
	cm : columnModel,
	store : store,
	loadMask : true,
	columnLines:true,
	multiSelect:true,
	clicksToEdit : 2,//设置点击几次才可编辑  
	selModel : checkboxSelectionModel, //必须加不然不能选checkbox 
	viewConfig : {
		forceFit : true
	},
	bbar : pageTool,
	tbar : [  	          
	{xtype: 'tbspacer', width:20,shadow:false},
	{
		privilege:addAuth,
		text:'新增',
		icon : '../../../resource/images/btnImages/add.png',
		handler : createInterface
	},
	{xtype: 'tbspacer', width:20,shadow:false},
	{
		privilege:modAuth,
		text:'修改',
		icon : '../../../resource/images/btnImages/modify.png',
		handler : modifyInterface		
	},
	{xtype: 'tbspacer', width:20,shadow:false},
	{
		privilege:delAuth,
		text:'删除',
		icon : '../../../resource/images/btnImages/delete.png',
		handler :  function(){
            var selectRecord =intefaceListPanel.getSelectionModel().getSelections(); 
			   if(selectRecord.length == 0){
			      Ext.Msg.alert("提示","请选择需要删除的接口");
			   }else{
			      var sysInterfaceIds = "";
			      for(var i = 0; i< selectRecord.length;i++){
			    	  if(i==(selectRecord.length-1)){
			    		  sysInterfaceIds+=selectRecord[i].get("ID");
			    	  }else{
			    		  sysInterfaceIds+=selectRecord[i].get("ID")+","; 
			    	  }
			    	  
			      }
			      deleteInterface(sysInterfaceIds);
			   }
			}		
	}
]
}		
);




function createInterface(){
	var createInterfaceWindow=new Ext.Window({
		id:'createInterfaceWindow',
		title:'新增接口',
		width:window.screen.width*0.6,
		height:window.screen.height*0.3,
		resizable:true, 
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		style:"margin-top:0",
		html : '<iframe src = "addSysInterface.jsp?saveType=0&sysInterfaceId=0" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	createInterfaceWindow.show();
}

function modifyInterface() {
	var cell = intefaceListPanel.getSelectionModel().getSelections();
	if (cell.length !=1) {
		Ext.Msg.alert("提示", "请选择需要修改的接口,每次选择一条！");
		return;
	}
	var sysInterfaceId = cell[0].get("ID");
	var url = 'addSysInterface.jsp?sysInterfaceId='+ sysInterfaceId+'&saveType=1';
	var createInterfaceWindow = new Ext.Window({
		id : 'createInterfaceWindow',
		title : '修改接口信息',
		width:window.screen.width*0.8,
		height:window.screen.height*0.6,
		isTopContainer : true,
		modal : true,
		autoScroll : false,
		style:"margin-top:0",
		html : '<iframe  id="modifyInterface_panel" name = "modifyInterface_panel"  src = ' + url
				+ ' height="100%" width="100%" frameBorder=0 border=0/>'
	});
	createInterfaceWindow.show();
}

function deleteInterface(sysInterfaceIds){
	Ext.Msg.confirm('提示','删除接口.</br></br>确认删除?',
		function(btn){
			if(btn=='yes'){
			  var jsonData = {'jsonString':Ext.encode({
		       "sysInterfaceIds":sysInterfaceIds})
		      };			
			  Ext.Ajax.request({
		      url:'interface-manage!deleteInterface.action',
		      method:'Post',
		      params:jsonData,
		      success: function(response) {
				    	store.reload();
				    },
		    error:function(response) {
		    	Ext.Msg.alert("错误",response.responseText);
		    },
		    failure:function(response) {
		    	Ext.Msg.alert("错误",response.responseText);
		    }		   
			   }); 			
				}
			});
		}





Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init(); // 开启悬停提示
	Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ intefaceListPanel ]
	});

	//放最后才能显示遮罩效果
	store.load({
		callback : function(r, options, success) {
			if (success) {

			} else {
				Ext.Msg.alert('错误', '加载失败！');
			}
		}
	});
});
