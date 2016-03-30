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

<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all-debug.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/areaTree.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/deviceAuthCombox.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonProcessBar.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/viewManager/Flex.js"></script>
<script type="text/javascript">
		var id="<%=request.getParameter("id")%>";
		var type="<%=request.getParameter("type")%>";
		var NET_LEVEL=<%=CommonDefine.toJsonArray(CommonDefine.RESOURCE.TRANS_SYS.NET_LEVEL)%>;
		var PRO_GROUP_TYPE=<%=CommonDefine.toJsonArray(CommonDefine.RESOURCE.TRANS_SYS.PRO_GROUP_TYPE)%>;
		
</script>
<script type="text/javascript" src="transSystemDefine.js"></script>
<script type="text/javascript" src="treeAndTopo.js"></script>
<script type="text/javascript" src="stepOne.js"></script>
<script type="text/javascript" src="stepTwo.js"></script>
<script type="text/javascript" src="stepThree.js"></script>
<script type="text/javascript" src="editTransSys.js"></script>
</head>
<body>
</body>

</html>