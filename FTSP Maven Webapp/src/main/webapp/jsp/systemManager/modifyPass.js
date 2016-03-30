


var passwordValidity=new Ext.data.Store({
	url : 'user-management!getPasswordValidity.action',
	baseParams : {
		
	},
    reader: new Ext.data.JsonReader({
				root : "rows"
		},["id","validity"])
 });

passwordValidity.load({
   callback : function(r, options, success) {
       if (success) {
            				
         } else {
            Ext.Msg.alert('错误', '查询失败！请重新查询');
            	}
            }
     });

passwordValidity.on('load',function(store,record,opts){ 
	  Ext.getCmp('passValidity').setValue('0');
	  //Ext.getCmp('passValidity').setRawValue('长期');

});


var pv=new Ext.form.ComboBox({
    id:'passValidity',
    name: 'passValidity',
    fieldLabel: '密码有效期',
    selectOnFoucs:true,
    mode:"local",	
    //value:'长期',
    valueField:'id',
    displayField:'validity',
	labelWidth: 80,
	width: 300,
    editable:false,
    store:passwordValidity,
    allowBlank:false,
    triggerAction: 'all',
    anchor: '95%',
	resizable: true,
    listeners:{
		select:function(combo,record,index){
			
		},
      afterRender: function(combo) {
      }
	}
});



var passDetail= new Ext.FormPanel({
	id : "passDetail",
	frame : false,
	border : false,
	layout: 'form',
	frame : false,
	width:'100%',
	height:'230',
	buttonAlign : 'center', 
	bodyStyle : 'padding:20px 20px 20px 20px;',
	items : [
			{
			    fieldLabel: '原始密码',
			    name: 'oldPass',
			    id: 'oldPass',
			    labelWidth: 80,
			    msgTarget: 'side',
			    autoFitErrors: false,
			    width: 150,
			    inputType: 'password',
			    xtype: 'textfield',
			    allowBlank:false,
			    maxLength: 20,
			    maxLengthText:'最大长度不能超过20位!'
			  },{ xtype: 'tbspacer', height: 20,shadow:false },
			  {
                fieldLabel: '新密码',
                name: 'newPass',
                id: 'newPass',
                labelWidth: 80,
                msgTarget: 'side',
                autoFitErrors: false,
                width: 150,
                inputType: 'password',
                xtype: 'textfield',
                allowBlank:false,
                minLength: 6,
			    minLengthText:'最小长度不能低于6位!',
                maxLength: 20,
			    maxLengthText:'最大长度不能超过20位!'
			  },{ xtype: 'tbspacer', height: 20,shadow:false },
			  {
                fieldLabel: '密码校验',
                name: 'pass-cfrm',
                vtype: 'password',
                initialPassField: 'newPass', // id of the initial password field
                labelWidth: 80,
                msgTarget: 'side',
                autoFitErrors: false,
                width: 150,
                allowBlank:false,
                inputType: 'password',
                xtype: 'textfield',
                maxLength: 20,
			    maxLengthText:'最大长度不能超过20位!'
			  },{ xtype: 'tbspacer', height: 20,shadow:false },
			  {
				    layout : "form",
				    labelWidth: 100,
					width: 250,
				    border:false,
					items : pv
				}
	          ],
	buttons: [{
		id:'ok',
	    text: '是',
	    handler: function(){
	    	saveModifyPass();
		}
	 },
	 { xtype: 'tbspacer', width: 60,shadow:false },
	 {
	    text: '否',
	    handler: function(){
	        //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('passWindow');
			if(win){
				win.close();
			}
	    }
	}]
});



function saveModifyPass(){
	if(!passDetail.getForm().isValid()){
		return ;
	}
	var oldPass = Ext.getCmp("oldPass").getValue();
	var newPass = Ext.getCmp("newPass").getValue();
	var passVal=Ext.getCmp("passValidity").getValue();
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
	    url: 'user-management!saveModifyPass.action', 
	    method : 'POST',
	    params: {// 请求参数
	    	  'sysUserId':parent.sysUserId,
			  'oldPass':oldPass,
			  'newPass':newPass,
			  'passVal':passVal
		},
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			if(obj.success){//新增成功
				var win = parent.Ext.getCmp('passWindow');
				if(win){
					win.close();
				}
			}else{
				Ext.Msg.alert("错误",obj.msg);
			}
	    },
	    error:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误","添加失败,请联系管理员!");
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误","添加失败,请联系管理员!");
	    }
	}); 	
	
}


Ext.onReady(function(){
	Ext.apply(Ext.form.VTypes,{
        password: function(val, field) {
            if (field.initialPassField) {
                var pwd =passDetail.form.findField('newPass');
                return (val == pwd.getValue().trim());
            }
            return true;
        },
        passwordText: '两次密码输入不一致'
	});
	Ext.BLANK_IMAGE_URL= "../../resource/ext/resources/images/default/s.gif";
 	Ext.Msg = top.Ext.Msg; 
 	Ext.QuickTips.init();
 	Ext.form.Field.prototype.msgTarget = 'title';
	var view = new Ext.Viewport({ 
		id:'view',
		width:'100%',
		height:'230',
	    items:passDetail
	});
});
 
 
