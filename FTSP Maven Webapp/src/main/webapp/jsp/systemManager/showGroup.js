var note=new Ext.form.TextField({
    id:'note',
    name: 'note',

    //labelWidth:30, //标签宽
    labelAlign:'right', //标签对齐体例
    fieldLabel: '描述',
    //emptyText:'请输入描述',
    width: 500,
    height : 20,
    maxLength: 100,
    maxLengthText:'组名最大长度不能超过100个字符!',
    anchor: '95%'
});


var groupName=new Ext.form.TextField({
	xtype: 'textfield',
    id:'groupName',
    name: 'groupName',
    fieldLabel: '组名',
    sideText : '<font color=red>*</font>',
    emptyText:'请输入用户组名',
    allowBlank:false,
    maxLength: 30,
    width: 260,
    height : 20,
    maxLengthText:'组名最大长度不能超过30个字符!',
    width: 90,
    anchor: '95%'
});


function isExistAtStore(dId,userList){
	for(var i=0;i<userList.length;i++){
		if(userList[i].sys_user_id==dId){
			return true;
		}
	}
	return false;
}

var allGroupUserStore =new Ext.data.Store({
	url : 'user-group-management!getAllGroupUserList.action',
	baseParams: {"limit":200,"sysUserGroupId":sysUserGroupId,'saveType':saveType},
	reader : new Ext.data.JsonReader({
		root : "rows"
	},["SYS_USER_ID","USER_NAME", "LOGIN_NAME","JOB_NUMBER","TELEPHONE","DEPARTMENT"])
});

allGroupUserStore.load({
	   callback : function(r,options, success) {
	       if (success) {
	    	   if(saveType==1){
		    	   Ext.Ajax.request({
		 		      url:'user-group-management!getCurrentGroupDetail.action',
		 		      method:'Post',
		 		      params: {"sysUserGroupId":sysUserGroupId,'saveType':saveType},
		 		      success: function(response) {
		 				    	var obj = Ext.decode(response.responseText);
		 				    	var userList= obj.currentGroupUser;	
		 				    	var userCount = allGroupUserStore.getCount();
		 				    	if(saveType == 1){
		 					    	for(var j=0;j<userCount;j++){
		 					    		for(var i=0; i<userList.length;i++){
		 					    			if(userList[i].sys_user_id==allGroupUserStore.getAt(j).get("SYS_USER_ID")){
		 					    				allGroupUserPanel.getSelectionModel().selectRow(j,true);
		 					    			}
		 					    		}
		 					    	}
		 				    	}else if(saveType == 2){
//		 				    		var arr=[];
//			 				   		for(var i=0;i<userCount;i++){
//		 				    			var isE=isExistAtStore(allGroupUserStore.getAt(i).get("SYS_USER_ID"),userList);//判断数据源中数据在用户列表中是否存在
//		 				    			if(!isE){
//		 				    				arr.push(allGroupUserStore.getAt(i));
//		 				    			}
//		 				    		}
//			 				   		for(var j=0;j<arr.length;j++){
//			 				   			allGroupUserStore.remove(arr[j]);
//			 				   		}
		 				    	}
		 		      		},
				 		    error:function(response) {
				 		    	Ext.Msg.alert("错误",response.responseText);
				 		    },
				 		    failure:function(response) {
				 		    	Ext.Msg.alert("错误",response.responseText);
				 		    }
				 	   })
	    	   }
	    	   
	       } else {
	            Ext.Msg.alert('错误', '查询失败！请重新查询');
        	}
        }
 });


var csm = new Ext.grid.CheckboxSelectionModel({
   singleSelect : false,
   renderer:function(v,c,r){
       if(saveType==2){
           return " ";//不显示checkbox
       }else{
           return  '<div class="x-grid3-row-checker">&#160;</div>';//显示checkbox
      }
   }
});

var usercm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		forceFit : true,
		align:'center'
	},
	columns:[
				{
					id : 'SYS_USER_ID',
					header : 'id',
					dataIndex : 'SYS_USER_ID',
					width : 80,
					hidden : true
				},
				{
					id:'USER_NAME',
					header:'用户名',
					dataIndex:'USER_NAME',
					width:80					
				},
				{
					id:'LOGIN_NAME',
					header:'登录名',
					dataIndex:'LOGIN_NAME',
					width:80					
				},         
				{
					id:'JOB_NUMBER',
					header:'工号',
					dataIndex:'JOB_NUMBER',
					width:100					
				},
				{
					id:'DEPARTMENT',
					header:'部门',
					dataIndex:'DEPARTMENT',
					width:120					
				},
				{
					id:'TELEPHONE',
					header:'手机号码',
					dataIndex:'TELEPHONE',
					width:120					
				}
				]}		
);




var allGroupUserPanel=new Ext.grid.GridPanel({
	id : "allGroupUserPanel",
	region : "center",
	autoScroll : true,
	frame : false,
	cm :usercm,
	store : allGroupUserStore,
	loadMask : true,
	width:'100%',
	height :Ext.getBody().getHeight()*0.6, 
	border:false,
	clicksToEdit : 2,// 设置点击几次才可编辑
	selModel : csm, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : false
	},
	tbar : ['配置用户'
	        
	        ]
}
);


var baseDetail=new Ext.FormPanel({
	id:"baseDetail",
//	title:'区域',
	region:"center",
    frame:false,
    border:false,
    autoScroll : true,
    layout : "form",
    bodyStyle: 'padding:10px 12px 0;',
    items: [
			{
				layout : "form",
				labelWidth : 60,
				border : false,
				items :groupName,
				width:300
			},
			{
				layout : "form",
				labelWidth : 60,
				border : false,
				items :note,
				width:700
			},
			{
				layout : "form",
				border : true,
				autoScroll : true,
				items :allGroupUserPanel,
				style:"margin-top:20px;padding-left:65px",
				width:670
				//height : 300
			}
            
    ],
    buttons: [{
        text: '取消',
        handler: function(){
			var win = parent.Ext.getCmp('createGroupWindow');
			if(win){
				win.close();
			}
        }
    }]
});










function initData(){
	   var jsonData = {
	       "sysUserGroupId":sysUserGroupId
	   }
	   //top.Ext.getBody().mask('正在初始化，请稍候...');
	   var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"加载中..."});
	   myMask.show();
	   Ext.Ajax.request({
		      url:'user-group-management!getCurrentGroupDetail.action',
		      method:'Post',
		      params:jsonData,
		      success: function(response) {
				    	var obj = Ext.decode(response.responseText);
				    	var baseDetail = obj.currentBaseDetail;
				    	Ext.getCmp('groupName').setValue(baseDetail[0].GROUP_NAME);
				    	Ext.getCmp('note').setValue(baseDetail[0].NOTE);		 
				    	Ext.getCmp('groupName').disable(true);
			    		Ext.getCmp('groupName').disable(true);
			    		Ext.getCmp('note').disable(true);
			    		myMask.hide();
		      },
		    error:function(response) {
		    	Ext.Msg.alert("错误",response.responseText);
		    	myMask.hide();
		    },
		    failure:function(response) {
		    	Ext.Msg.alert("错误",response.responseText);
		    	myMask.hide();
		    }
	   })
}

function close() {
	var win = parent.Ext.getCmp('createGroupWindow');
	if (win) {
		win.close();
	}
}



Ext.onReady(function(){
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
 	Ext.Msg = top.Ext.Msg; 
	Ext.QuickTips.init();
 	Ext.form.Field.prototype.msgTarget = 'title';
 	initData();
  	var win = new Ext.Viewport({
        id:'win',
        layout : 'border',
        autoScroll:true,
		items : [baseDetail],
		renderTo : Ext.getBody()
	});
 });





