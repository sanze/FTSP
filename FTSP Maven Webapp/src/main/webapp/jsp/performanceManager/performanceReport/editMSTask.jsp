<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<base />

<title>FTSP 3000 传输网络维护支撑平台</title>

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/MultiSelect.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/LovCombo.css" />


<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/ext-all-debug.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/LovCombo.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
<script type="text/javascript"
	src="../../commonManager/deviceAuthCombox.js"></script>
		<script type="text/javascript" src="../../commonManager/commonAuthDomian.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/MultiSelect.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ItemSelector.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/Ext.ux.PanelCollapsedTitle.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
<script type="text/javascript">
	var	targetTrunkLine="<%=CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE%>";
	var	targetMultiSec="<%=CommonDefine.TASK_TARGET_TYPE.MULTI_SEC%>";
	var taskId = "<%=request.getParameter("taskId")%>";
	var creatorId = "<%=request.getParameter("creatorId")%>";
	var userId=<%=session.getAttribute("SYS_USER_ID")%>;
</script>
<script type="text/javascript" src="addMS.js"></script>
<script type="text/javascript" src="addTL.js"></script>
<script type="text/javascript" src="pmReportSearchDefine.js"></script>
<script type="text/javascript" src="editMSTask.js"></script>

</head>

<body>
</body>
</html>
