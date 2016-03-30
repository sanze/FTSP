/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
 
var sysUserId;
var userName = new Ext.form.TextField({
        id:'userName',
        name: 'userName',
        fieldLabel: '用户名',
        allowBlank:false,
        width:'150px'
});

var password = new Ext.form.TextField({
        id:'password',
        name: 'password',
        inputType: 'password',
        fieldLabel: '密    码',
        allowBlank:false
});

var button = new Ext.Button({
text:"登 录",
//type:"button",
//applyTo:"btn",
handler:function(){
   okButtonPress();
}
});


Ext.onReady(function(){

	var view = new Ext.Viewport({ 
		id:'view',
	    layout:'form',
	    applyTo:"ddd",
	    labelWidth: 50,
	    items:[userName,password,button]
	});
	view.show();
});

//修改密码
function modifyPassword() {
    var url = 'modifyPass.jsp';
    var passWindow = new Ext.Window({
            id : 'passWindow',
            title : '修改密码',
            width : 420,
            height : 280,
            isTopContainer : true,
            modal : true,
            autoScroll : true,
            html : '<iframe  id="modifyUserPass_panel" name = "modifyUserPass_panel"  src = ' + url
             + ' height="100%" width="100%" frameBorder=0 border=0/>'
        });
    passWindow.show();

}
 
 function okButtonPress(){
		var userName = Ext.getCmp('userName').getValue();
		var password = Ext.getCmp('password').getValue();
		var jsonData = {
 			"userName":userName,
 			"password":password
		};
		Ext.Ajax.request({
		    url: 'login!login.action', 
		    method : 'POST',
		    params: jsonData,
		    success: function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	if(obj.returnResult == 0){
		    		Ext.Msg.alert("信息",obj.returnMessage);
	             }
	         	if(obj.returnResult == 1){
	         		Ext.Msg.alert("信息",obj.returnMessage);
	         	}
	         	if(obj.returnResult == 2){
	         		sysUserId = obj.returnMessage;
	         		modifyPassword();
	         	}
		    },
		    error:function(response) {
		    	Ext.Msg.alert("错误",response.responseText);
		    },
		    failure:function(response) {
		    	Ext.Msg.alert("错误",response.responseText);
		    }
		}); 
};