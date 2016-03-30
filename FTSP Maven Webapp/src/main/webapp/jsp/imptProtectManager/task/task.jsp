<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<base />
<title>FTSP3.0 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
<script type="text/javascript" src="../../commonManager/commonAuthDomian.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script>

<script type="text/javascript" src="../common/taskCommon.js"></script>
<script type="text/javascript">
	Ext.BLANK_IMAGE_URL = "<%=request.getContextPath()%>/resource/ext/resources/images/default/s.gif";
	var today=new Date();
 	var startDate=today.add(Date.MONTH,-1).format("yyyy-MM-dd 00:00:00");
	var endDate=today.add(Date.MONTH,1).format("yyyy-MM-dd 00:00:00");
	var authSequence="<%=request.getParameter("authSequence")%>";
</script>
<script type="text/javascript" src="task.js"></script>
</head>
<body>
</body>
</html>