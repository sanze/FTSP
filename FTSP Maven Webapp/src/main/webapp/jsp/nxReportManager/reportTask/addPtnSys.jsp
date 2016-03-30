<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
    
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
   	<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/deviceAuthCombox.js"></script>
    <script type="text/javascript">
    	var userId=<%=session.getAttribute("SYS_USER_ID")%>;
		//var targetId = "<%=request.getParameter("targetId")%>"; 
		//var isMod = (targetId >= 0); 
		var NX_SYS_TYPE=<%=CommonDefine.toJsonArray(CommonDefine.NX_SYS_TYPE)%>;
		var SYS_TYPE_DEFINE = {};
		 SYS_TYPE_DEFINE.DECENTRALIZED_RING = <%=CommonDefine.NX_REPORT.SYS_TYPE.DECENTRALIZED_RING%>;
		 SYS_TYPE_DEFINE.CENTRALIZED_RING = <%=CommonDefine.NX_REPORT.SYS_TYPE.CENTRALIZED_RING%>;
		 SYS_TYPE_DEFINE.CENTALIZED_CHAIN = <%=CommonDefine.NX_REPORT.SYS_TYPE.CENTALIZED_CHAIN%>;
	</script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/equipTree.js"></script>
    <script type="text/javascript" src="../common/ptnSysCommon.js"></script>
    <script type="text/javascript" src="addPtnSys.js"></script>
  </head>
  
<body>
</body>
</html>