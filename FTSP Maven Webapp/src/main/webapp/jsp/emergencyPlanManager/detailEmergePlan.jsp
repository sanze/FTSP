<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base/>
<title>FTSP3.0 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
	
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>  
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script>   
<script type="text/javascript" src="commonEmergePlan.js"></script>
<script type="text/javascript" src="detailEmergePlan.js"></script>

<script type="text/javascript">  
	var emergeName = "<%=request.getParameter("emergeName")%>";	
	var emergeTypeName = "<%=request.getParameter("emergeTypeName")%>";	
	var emergeTypeValue = <%=request.getParameter("emergeTypeValue")%>;	 
	var FAULT_EP_ID = <%=request.getParameter("FAULT_EP_ID")%>;	
</script>
</head> 
<body>
</body>

</html>