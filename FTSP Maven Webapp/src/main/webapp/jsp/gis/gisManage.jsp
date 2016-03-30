<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
 
    <link rel="stylesheet" type="text/css" href="resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="resource/expandExt/css/TreeCheckNodeUI.css" />
    
    <script type="text/javascript" src="resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="resource/ext/ext-all-debug.js"></script>
    <script type="text/javascript" src="resource/expandExt/js/ext-lang-zh_CN.js"></script> 
    
    <script type="text/javascript" src="resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="jsp/commonManager/areaTree.js"></script>
    <script type="text/javascript" src="jsp/commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript" src="resource/js/util.js"></script>
    <script type="text/javascript" src="jsp/resourceManager/area/areaDefine.js"></script>  		 
	<script type="text/javascript" src="jsp/gis/gisManage.js"></script>

  </head>
  
  <body>
  
  </body>
</html>
