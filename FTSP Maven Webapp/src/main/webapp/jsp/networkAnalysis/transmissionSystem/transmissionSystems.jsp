<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base />
<title>FTSP3.0 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/ColumnHeaderGroup.css" />
<link rel="stylesheet" type="text/css"
  href="<%=request.getContextPath()%>/resource/css/common.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" /> 
<script> var userId="<%=session.getAttribute("SYS_USER_ID")%>";</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/areaTree.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/deviceAuthCombox.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonProcessBar.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/ux/ColumnHeaderGroup.js"></script>  
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/mySessionProvider.js"></script> 
<!-- <script type="text/javascript" src="<%=request.getContextPath()%>/jsp/resourceManager/common/projectCommon.js"></script>  -->
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/networkAnalysis/commonNetwork.js"></script> 
<script type="text/javascript">
		var NET_LEVEL=<%=CommonDefine.toJsonArray(CommonDefine.RESOURCE.TRANS_SYS.NET_LEVEL)%>;
		var PRO_GROUP_TYPE=<%=CommonDefine.toJsonArray(CommonDefine.RESOURCE.TRANS_SYS.PRO_GROUP_TYPE)%>;
</script>
<script type="text/javascript" src="transSystemDefine.js"></script>
<script type="text/javascript" src="transmissionSystems.js"></script>
</head>
<body>
</body>

</html>