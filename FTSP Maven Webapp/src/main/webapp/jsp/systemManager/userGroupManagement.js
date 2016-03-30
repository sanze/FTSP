
var store=new  Ext.data.Store({
	url:'user-group-management!getUserGroup.action',
	baseParams : {
		"limit" : 200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "SYS_USER_GROUP_ID","GROUP_NAME","CREATE_TIME","NOTE"])
});

store.load({
	callback : function(r, options, success) {
		if (success) {
			
		} else {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

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
		align:'left',
		sortable : true,
		forceFit : false
	},
	columns:[ new Ext.grid.RowNumberer({
		width : 26
	}),
	          checkboxSelectionModel,
				{
					id : 'SYS_USER_GROUP_ID',
					header : 'sysUserGroupId',
					dataIndex : 'SYS_USER_GROUP_ID',
					width : 150,
					hidden : true
				},
				{
					id:'GROUP_NAME',
					header:'用户组名',
					dataIndex:'GROUP_NAME',
					width:100
				},
				{
					id:'CREATE_TIME',
					header:'创建时间',
					dataIndex:'CREATE_TIME',
					width:150
				},         
				{
					id:'NOTE',
					header:'描述',
					dataIndex:'NOTE',
					width:200
				}
				]}		
);

var groupListPanel = new Ext.grid.GridPanel({
	id : "groupListPanel",
	region : "center",
	stripeRows : true,
	autoScroll : true,
	// collapsible: true,
	forceFit : true,
	frame : false,
	cm : columnModel,
	store : store,
	loadMask : true,
	columnLines:true,
	clicksToEdit : 2,//设置点击几次才可编辑  
	selModel : checkboxSelectionModel, //必须加不然不能选checkbox 
	bbar : pageTool,
	tbar : ['-',
	    	{
				text:'新增',
				privilege : addAuth,
				icon : '../../resource/images/btnImages/group_add.png',
				handler : createGroup
			},{
	    		text:'删除',
	    		privilege : delAuth,
	    		icon : '../../resource/images/btnImages/group_delete.png',
	    		handler :  function(){
	                var selectRecord =groupListPanel.getSelectionModel().getSelections(); 
	                if(selectRecord.length == 0){
	    			      Ext.Msg.alert("提示","请选择要删除的用户组!");
	                }else{
	    			      var sysUserGroupIdList = new Array();
	    			      for(var i = 0; i< selectRecord.length;i++){
	    			    	  sysUserGroupIdList.push(selectRecord[i].get("SYS_USER_GROUP_ID"));
	    			      }
	    			      deleteGroup(sysUserGroupIdList);
    			   }
    			}	
	    	},
	    	{
	    		text:'修改',
	    		privilege : modAuth,
	    		icon : '../../resource/images/btnImages/group_edit.png',
	    		handler :modifyGroup		
	    	},
	    	'-',
	    	{
	    		text:'详情',
	    		privilege : viewAuth,
	    		icon : '../../resource/images/btnImages/group.png',
	    		handler : getCurrentDetail		
	    	}
	    ]

}		
);

function getCurrentDetail(){
	var cell = groupListPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var sysUserGroupId = cell[0].get("SYS_USER_GROUP_ID");
		var url = 'showGroup.jsp?sysUserGroupId='+ sysUserGroupId+'&saveType=2';
		var createGroupWindow = new Ext.Window({
			id : 'createGroupWindow',
			title : '用户详情',
			width:Ext.getBody().getWidth()*0.55,  
			height:Ext.getBody().getHeight()-20,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			style:"margin-top:0",
			html : '<iframe  id="modifyUser_panel" name = "modifyUser_panel"  src = ' + url
					+ ' height="100%" width="100%" frameBorder=0 border=0/>'
		});
		createGroupWindow.show();
	} else
		Ext.Msg.alert("提示", "请选择需要查看的用户组，每次选择一条！");
}

function createGroup(){
	var createGroupWindow=new Ext.Window({
		id:'createGroupWindow',
		title:'新增组',
		width:Ext.getBody().getWidth()*0.55,  
		height:Ext.getBody().getHeight()-20,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		style:"margin-top:0",
		html : '<iframe src = "addGroup.jsp?sysUserGroupId=0&saveType=0" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	createGroupWindow.show();
}

function modifyGroup() {
		var cell = groupListPanel.getSelectionModel().getSelections();
		if(cell.length!=1){
			Ext.Msg.alert("提示", "请选择一行记录进行！");
			return;
		}
	
		var sysUserGroupId = cell[0].get("SYS_USER_GROUP_ID");
		var url = 'addGroup.jsp?sysUserGroupId='+ sysUserGroupId+'&saveType=1';
		var createUserGroupWindow = new Ext.Window({
			id : 'createGroupWindow',
			title : '修改组',
			width:Ext.getBody().getWidth()*0.55,  
			height:Ext.getBody().getHeight(),
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			style:"margin-top:0",
			html : '<iframe  id="modifyGroup_panel1" name = "modifyGroup_panel1"  src = ' + url
					+ ' height="100%" width="100%" frameBorder=0 border=0/>'
		});
		createUserGroupWindow.show();
}

function deleteGroup(sysUserGroupIdList){
	Ext.Msg.confirm('提示','删除用户组，不会删除所属用户，但会导致属于此用户组的用户失去部分权限：<br/>（1）无法查看被赋予权限的巡检报告。<br/>（2）无法查看被赋予权限的割接任务和报告。<br/>（3）无法查看被赋予权限的性能定制报表。',
		function(btn){
			if(btn=='yes'){
				  var jsonData = {"sysUserGroupIdList":sysUserGroupIdList}
				  Ext.Ajax.request({
				      url:'user-group-management!deleteGroup.action',
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
				  }) 
			}
		});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	Ext.Msg = top.Ext.Msg;
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [groupListPanel]
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