<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8">
<base />
<title>FTSP 3000 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/css/common.css" />

<script type="text/javascript">
	var userId="<%=session.getAttribute("SYS_USER_ID")%>";
</script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<!-- <script type="text/javascript" -->
<!-- 	src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script> -->
<!-- <script type="text/javascript" -->
<!-- 	src="<%=request.getContextPath()%>/resource/expandExt/js/mySessionProvider.js"></script> -->
<script type="text/javascript"
	src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/js/util.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/jsp/commonManager/equipTree.js"></script>
<script type="text/javascript">
	var FACTORY=<%=CommonDefine.toJsonArray(CommonDefine.FACTORY)%>;
</script>
<!-- <script type="text/javascript" src="../common/renderersDefine.js"></script> -->
<script type="text/javascript" src="waveDirManger.js"></script>

<script type="text/javascript">
	
</script>
</head>


<body>
</body>

</html>