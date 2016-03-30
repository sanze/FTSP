/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";

Ext.onReady(function(){
	var userName = new Ext.form.TextField({
	            id:'userName',
	            name: 'userName',
	            fieldLabel: '用户名',
	            allowBlank:false,
	            cls:"password"
	});

	var password = new Ext.form.TextField({
	            id:'password',
	            name: 'password',
	            inputType: 'password',
	            fieldLabel: '密     码',
	            allowBlank:false,
	            cls:"password"
	});

	var button = new Ext.BoxComponent({
	    //text:"登  录",
	    //type:"button",
	    //applyTo:"btn",
	    cls:"button",
	    overCls:"buttonHover",
	    listeners: {
	    	render: function(component){
                component.getEl().on('click', function(e){
                    okButtonPress();
                });
	    	}
	    }
	});

	var view = new Ext.Viewport({
	    layout:'form',
	    applyTo:"ddd",
	    cls:"userName",
	    items:[userName, password, button]
	});
	Ext.getCmp('userName').focus();
	//处理键盘事件,当超时锁定的时候 禁止F5
	function noF5(e) {
		var ev = e || window.event; //获取event对象 
	    var obj = ev.target || ev.srcElement; //获取事件源 
	    var type = obj.type || obj.getAttribute('type'); //获取事件源类型 
	    var key = ev.keyCode || ev.which;
	    //判断 事件目标是不是只读/被禁用
	    var vReadOnly = !!obj.readOnly;
	    var vDisabled = !!obj.disabled;
//		console.log(String.format("key<{0}> @ <{1}> -> ReadOnly【{2}】    Disabled【{3}】", key, type, vReadOnly, vDisabled));
		if (key == 116 && window.isLocked){
			return false;
		}else if (key == 8) {
			if (type != 'text' && type != 'textarea' && type != 'submit' && type != 'password')  
				return false;
		}else if (key == 13 && type != 'button') {
			okButtonPress();
		}else if (key == 9 && type == 'password') {
			Ext.getCmp("password").focus(false, 100);
			e.stopPropagation();
			e.preventDefault();
			return false;
		}
	}
	//鼠标移动/按键事件注册
	(function(){
		document.onkeydown = noF5;
	})();
});
 
//修改密码
 function modifyPassword() {
     var url = '../systemManager/modifyPass.jsp';
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
	 if(Ext.getCmp('userName').isValid() && Ext.getCmp('password').isValid()){
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
		    	if(obj.returnResult == 1){
		    		window.open('../main/main.jsp',"_parent");
	             }
	         	if(obj.returnResult == 0){
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
		 
	 }
};