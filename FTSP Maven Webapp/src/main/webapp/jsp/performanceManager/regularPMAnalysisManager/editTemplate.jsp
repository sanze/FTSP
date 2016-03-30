<%@ page language="java" pageEncoding="utf-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<title>FTSP 3000 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />
	
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
<script type="text/javascript">
	var	templateId="<%=request.getParameter("templateId")%>";
	var	factory="<%=request.getParameter("factory")%>";
</script>
   	<script type="text/javascript">
		var FACTORY=<%=CommonDefine.toJsonArray(CommonDefine.FACTORY)%>;
	</script>
<script type="text/javascript" src="editTemplate.js"></script>

</head>
<body>

</body>
</html>