<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
  <head>
    
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script type="text/javascript" src="../../jsp/gis/apiv1.3.min.js"></script> 
    <base href="<%=basePath%>">
    
    <link rel="stylesheet" type="text/css" href="resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="resource/ext/resources/css/xtheme-blue.css" />
    
    <script type="text/javascript" src="resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="resource/expandExt/js/ext-lang-zh_CN.js"></script>  
	
	<script type="text/javascript" src="jsp/gis/Map.js"></script> 
	<script type="text/javascript" src="jsp/gis/address.js"></script> 
	<link rel="stylesheet" type="text/css" href="jsp/gis/bmap.css"/> 
	<script type='text/javascript' src='dwr/engine.js'></script>
	<script type='text/javascript' src='dwr/util.js'></script>
	<script type="text/javascript" src="jsp/gis/gisMap.js"></script>
	
	<script type="text/javascript">
		var logonUserLevel = "<%=session.getAttribute("USER_LEVEL")%>"
	</script>
	
	<style type="text/css">
	<!--
		#container {position: absolute; margin:0px auto; width:100%; height:100% ; }
		#position { position: relative; float:right; width:250px; height:100px; opacity: 0.6; background-color:white;  }
	-->	
	</style>

  </head>
  
  <body onload="dwr.engine.setActiveReverseAjax(true);dwr.engine.setNotifyServerOnPageUnload(true);">

    <div id="container" > 	 
	</div>
	
  </body> 
</html>
