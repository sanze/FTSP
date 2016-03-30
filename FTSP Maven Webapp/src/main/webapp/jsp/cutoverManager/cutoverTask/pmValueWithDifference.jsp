<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<title>FTSP3.0 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />

<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/Ext.ux.PanelCollapsedTitle.js"></script>
<script type="text/javascript">
		var userId=<%=session.getAttribute("SYS_USER_ID")%>;
</script>
<script type="text/javascript">
		var saveType="<%=request.getParameter("saveType")%>";
		var cutoverTaskId="<%=request.getParameter("cutoverTaskId")%>";
</script>
<script type="text/javascript" src="pmValueWithDifference.js"></script>


</head>
<body>

</body>
</html>