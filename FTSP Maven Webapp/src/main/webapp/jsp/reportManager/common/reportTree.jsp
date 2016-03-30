<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
	<!-- 不显示节点图标 -->
    <style type="text/css">
/* 	    .x-tree-node-icon{ */
/* 			display:none; */
/* 		} */
	</style>
    <script type="text/javascript">
   		var type = "<%=request.getParameter("type")%>";
   		var parentIds = "<%=request.getParameter("parentIds")%>";
   		var ids = "<%=request.getParameter("ids")%>";
   		var rootVisible=false;
    </script>
    <script type="text/javascript" src="reportTree.js"></script>
  </head>
<body>
<div id="tree-div" style="height:'100%';width:'100%'"></div>
</body>
</html>