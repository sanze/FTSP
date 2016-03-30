
function isExistAuthAtStore(dId,userList){
	for(var i=0;i<userList.length;i++){
		if(userList[i].SYS_AUTH_DOMAIN_ID==dId){
			return true;
		}
	}
	return false;
}

function isExistDeviceAtStore(dId,userList){
	for(var i=0;i<userList.length;i++){
		if(userList[i].SYS_DEVICE_DOMAIN_ID==dId){
			return true;
		}
	}
	return false;
}


var authDomainStore=new Ext.data.ArrayStore({
	data: [
	       ],
	fields:[
		   {name:"SYS_AUTH_DOMAIN_ID",mapping:"SYS_AUTH_DOMAIN_ID"},
		   {name:"AUTH_DOMAIN_NAME",mapping:"AUTH_DOMAIN_NAME"}
   ]
});



var deviceDomainStore=new Ext.data.ArrayStore({
	data: [
	       ],
	fields:[
		   {name:"SYS_DEVICE_DOMAIN_ID",mapping:"SYS_DEVICE_DOMAIN_ID"},
		   {name:"DEVICE_DOMAIN_NAME",mapping:"DEVICE_DOMAIN_NAME"}
   ]
});


var userName=new Ext.form.TextField({
	xtype: 'textfield',
    id:'userName',
    name: 'userName',
    fieldLabel: '用户名',
    emptyText:'请输入用户名',
    disabled:true,
    allowBlank:false,
    maxLength: 10,
    maxLengthText:'姓名最大长度不能超过10个字符!',
    anchor: '95%'
});


var loginName=new Ext.form.TextField({
	xtype: 'textfield',
    id:'loginName',
    name: 'loginName',
    bodyStyle : 'padding:10px 50px 100px',
    fieldLabel: '登录名',
    emptyText:'请输入登录名',
    disabled:true,
//    width: 120,
//	height : 20,
    allowBlank:false,
    maxLength: 10,
    maxLengthText:'登陆名最大长度不能超过10个字符!',
    anchor: '95%'
});

var jobNumber=new Ext.form.TextField({
	xtype: 'textfield',
    id:'jobNumber',
    name: 'jobNumber',
    fieldLabel: '工号',
    emptyText:'请输入工号',
    allowBlank:false,
    disabled:true,
//    width: 120,
//	height : 20,
    maxLength: 20,
    maxLengthText:'工号最大长度不能超过20个字符!',
    anchor: '95%'
});

var telephone=new Ext.form.TextField({
	xtype: 'textfield',
    id:'telephone',
    name: 'telephone',
    fieldLabel: '电话号码',
    emptyText:'请输入电话号码',
    allowBlank:false,
    maxLength:24,
    disabled:true,
//    width: 120,
//	height : 20,
    regex : /^[\d]{4,24}$/,
    regexText: '请输入正确的电话号码!',
    anchor: '95%'
});

var email=new Ext.form.TextField({
	xtype: 'textfield',
    id:'email',
    name: 'email',
    fieldLabel: '邮箱',
    disabled:true,
    //emptyText:'请输入邮箱',
    allowBlank:true,
//    width: 120,
//	height : 20,
    maxLength: 50,
    anchor: '95%',
    vtype:"email",//email格式验证
    vtypeText:"不是有效的邮箱地址",//错误提示信息,默认值我就不说了
});

var note=new Ext.form.TextField({
	 id : 'note',
     name : 'note',
//     width:450,
//     height : 20,
     anchor:'95%',
     fieldLabel : '备注',
     emptyText : '请输入备注',
     disabled:true,
     allowBlank : true,
     maxLength : 200
});

var department=new Ext.form.TextField({  
    id:'department',
    name: 'department',
    fieldLabel: '部门',
	disabled:true,
    width: 160
});

var position=new Ext.form.TextField({  
    id:'position',
    name: 'position',
    fieldLabel: '职务',
    disabled:true,
    anchor:'88%'
});

var timeOut = new Ext.form.TextField({
    id : 'timeout',
    name : 'timeout',
    fieldLabel : '超时时间',
    allowBlank : false,
    disabled:true,
});



var deviceDomainCM = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		forceFit : false
	},
	columns : [{
		id : 'SYS_DEVICE_DOMAIN_ID',
		header : 'id',
		dataIndex : 'SYS_DEVICE_DOMAIN_ID',
		hidden : true
	}, {
		id : 'DEVICE_DOMAIN_NAME',
		header : '设备域名称',
		width : 140,
		dataIndex : 'DEVICE_DOMAIN_NAME',
		editor : new Ext.form.TextField({
			allowBlank : false,
			maxValue : 40,
			minValue : 1
		})
	} ]
});


var deviceDomainPanel=new Ext.grid.GridPanel({
	id : "deviceDomainPanel",
	height :  (Ext.getBody().getHeight()-160)*0.7, 
	width : 400,
    height:280,
	border:true,
	autoScroll : true,
	frame : false,
	cm : deviceDomainCM,
	store : deviceDomainStore,
	loadMask : true,
	viewConfig : {
		forceFit : true
	}
}
);


var authDomainCM = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		forceFit : false
	},
	columns : [{
		id : 'SYS_AUTH_DOMAIN_ID',
		header : 'id',
		dataIndex : 'SYS_AUTH_DOMAIN_ID',
		hidden : true
	}, {
		id : 'AUTH_DOMAIN_NAME',
		header : '权限域名称',
		width : 140,
		dataIndex : 'AUTH_DOMAIN_NAME',
		editor : new Ext.form.TextField({
			allowBlank : false,
			maxValue : 40,
			minValue : 1
		})
	} ]
});


var authDomainPanel=new Ext.grid.GridPanel({
	id : "authDomainPanel",
	//region : "center",
	height : (Ext.getBody().getHeight()-160)*0.7, 
	width : 400,
    height:280,
	border:true,
	autoScroll : true,
	frame : false,
	cm : authDomainCM,
	store : authDomainStore,
	loadMask : true,
	viewConfig : {
		forceFit : true
	}
}
);

var height = Ext.getBody().getHeight()-40;
var baseDetail= new Ext.FormPanel({
	id : "base",
    labelAlign: 'left',
    width:990,
    height:420,
    autoScroll : true,
    border:false,
	bodyStyle : 'padding:20px 20px 0px 20px;',
	items : [{
            layout : "column",
            border : false,
            items : [{
                    columnWidth : .25,
                    layout : "form",
                    labelWidth : 60,
                    border : false,
                    items : [userName,jobNumber]
                }, {
                    columnWidth : .25,
                    layout : "form",
                    labelWidth : 60,
                    border : false,
                    items : [loginName,email,position]
                }, {
                    columnWidth : .25,
                    layout : "form",
                    labelWidth : 60,
                    border : false,
                    items : [department,telephone]
                }, {
                    columnWidth : .25,
                    layout : "form",
                    labelWidth : 60,
                    border : false,
                    items : [position,timeOut]
                }
            ]
        },{
			layout : "form",
			//bodyStyle : 'padding:1px 50px',
			border : false,
			items : [{
                layout : "form",
                id:'noteCon',
                labelWidth : 60,
                width : 970 ,  
                border : false,
                items : note
            }]
		},{
			layout: {
	            type: 'column'
	            //columns: 2
	        },
	        border:false,
	        bodyStyle:'padding:20px 0 0 65px',
	        items: [{
	            baseCls:'x-plain',
	            border:true,
	            bodyStyle:'padding:0 55px 0px 0',
	            items:deviceDomainPanel
	        },{
	            baseCls:'x-plain',
	            border:true,
	            items:authDomainPanel
	        }]
	    }]
});


function close() {
	var win = parent.Ext.getCmp('createUserWindow');
	if (win) {
		win.close();
	}
}






function initData(){
	   var jsonData = {
 		       "sysUserId":sysUserId
	   }
	   var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"加载中..."});
	   myMask.show();
	   Ext.Ajax.request({
		      url:'user-management!getDetailByUserId.action',
		      method:'Post',
		      params:jsonData,
		      success: function(response) {
				    	var obj = Ext.decode(response.responseText);
				    	var baseDetail = obj.currentBaseDetail;
				    	var currentDeviceDomain=obj.currentDeviceDomain;
				    	var currentAuthDomain=obj.currentAuthDomain;
				    	authDomainStore.loadData(currentAuthDomain);
				    	deviceDomainStore.loadData(currentDeviceDomain);
				    	
				    	Ext.getCmp('userName').setValue(baseDetail[0].USER_NAME);
				    	Ext.getCmp('loginName').setValue(baseDetail[0].LOGIN_NAME);
				    	Ext.getCmp('jobNumber').setValue(baseDetail[0].JOB_NUMBER);
				        Ext.getCmp('telephone').setValue(baseDetail[0].TELEPHONE);
				    	Ext.getCmp('email').setValue(baseDetail[0].EMAIL);
				    	Ext.getCmp('note').setValue(baseDetail[0].NOTE);
				    	Ext.getCmp('department').setValue(baseDetail[0].DEPARTMENT);
				    	Ext.getCmp('position').setValue(baseDetail[0].POSITION);		
				    	Ext.getCmp('timeout').setValue(baseDetail[0].TIME_OUT);		
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

var panel = new Ext.Panel({
    region:"center",
    frame : false,
    layout:'form',
    autoScroll : true,
    border : false,
    items:baseDetail,
    buttons : [{
        text : '取消',
        handler : function () {
            // 关闭修改任务信息窗口
            var win = parent.Ext.getCmp('createUserWindow');
            if (win) {
                win.close();
            }
        }
    }
]
});
Ext.onReady(function(){
 	Ext.BLANK_IMAGE_URL= "../../resource/ext/resources/images/default/s.gif";
 	Ext.Msg = top.Ext.Msg; 
 	Ext.QuickTips.init();
 	Ext.form.Field.prototype.msgTarget = 'title';
 	
  	var win = new Ext.Viewport({
        id:'win',
        layout: 'border',
        loadMask : true,
		items : [panel],
		renderTo : Ext.getBody()
	});
  	
  	initData();
 });





