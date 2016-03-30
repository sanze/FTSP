<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <link  rel="SHORTCUT ICON" href="../../resource/images/otherImages/favicon.ico" />
  <title>FTSP3.0 传输网络维护支撑平台</title>
    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/xtheme-blue.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/expandExt/css/login-all.css" />
    
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="login.js"></script>
    
    <style type="text/css">
        .x-check-group-alt {
            background: #D1DDEF;
            border-top:1px dotted #B5B8C8;
            border-bottom:1px dotted #B5B8C8;
        }
    </style>
    <script type="text/javascript">
    	AUTO_LOGIN=<%=CommonDefine.AUTO_LOGIN%>;
    	if(AUTO_LOGIN){
    		Ext.getBody().hide();
    		var jsonData = {
   	 			"userName":"admin",
   	 			"password":"admin"
   			};
   			Ext.Ajax.request({
   			    url: 'login!login.action', 
   			    method : 'POST',
   			    params: jsonData,
   			    success: function(response) {
   			    	var obj = Ext.decode(response.responseText);
   			    	if(obj.returnResult == 1){
   			    		window.open('../main/main.jsp',"_parent");
   		            }else{
   		            	Ext.getBody().show();
   		            }
   			    },
   			    error:function(response) {
   			    	Ext.getBody().show();
   			    },
   			    failure:function(response) {
   			    	Ext.getBody().show();
   			    }
   			});
    	}
    </script>
  </head>
<body class="login" >
<div class="welcome"><img src="../../resource/images/otherImages/welcome.png" width="654px" height="66" ></div>
<div class="formPanel">
<div id="ddd"></div>
<div id="btn"></div>
</div>
<!--<img src="../../resource/icons/loginbg1.jpg" width="100%" height="100%"/ >*//  -->
</body>
</html>