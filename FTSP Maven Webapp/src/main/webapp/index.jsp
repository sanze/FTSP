<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base href="<%=basePath%>">
    
    <title>FTSP3.0 传输网络维护支撑平台</title>
     <link  rel="SHORTCUT ICON" href="../../resource/images/otherImages/favicon.ico" />
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="resource/ext/ext-all.js"></script>
	<script type="text/javascript" charset="utf-8">
	    Ext.onReady(function(){
	    	window.open("<%=path%>"+"/jsp/login/login.jsp","_parent");
	    });
    </script>
  </head>
  
  <body>
  </body>
</html>
