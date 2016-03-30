<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.fujitsu.common.CommonDefine;"%>
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<base />

<title>FTSP 3000 传输网络维护支撑平台</title>
<style type="text/css">
.x-item-disabled-modified {
    cursor: default;
    color:#0B66B0;
    opacity: 0.6;
}
</style>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/MultiSelect.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/LovCombo.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/css/common.css" />

<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/ext-all-debug.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/LovCombo.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/MultiSelect.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ItemSelector.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/Ext.ux.PanelCollapsedTitle.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/jsp/commonManager/equipTree.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
<script type="text/javascript">
	var FACTORY=<%=CommonDefine.toJsonArray(CommonDefine.FACTORY)%>;
	var TASK_TYPE=<%=request.getParameter("TASK_TYPE")%>;
	var taskId = <%=request.getParameter("taskId")%>;
	var creatorId = <%=request.getParameter("creatorId")%>;
	var userId=<%=session.getAttribute("SYS_USER_ID")%>;
	var frameId="<%=request.getParameter("frameId")%>";
	var NodeDefine = {};
	NodeDefine.EMSGROUP=<%=CommonDefine.TREE.NODE.EMSGROUP%>;
	NodeDefine.EMS=<%=CommonDefine.TREE.NODE.EMS%>;
	NodeDefine.SUBNET=<%=CommonDefine.TREE.NODE.SUBNET%>;
	NodeDefine.NE=<%=CommonDefine.TREE.NODE.NE%>;
	NodeDefine.PTP=<%=CommonDefine.TREE.NODE.PTP%>;
</script>
<script type="text/javascript" src="../common/utils.js"></script>
<script type="text/javascript" src="../common/addPtp.js"></script>
<script type="text/javascript" src="taskInfoDefine.js"></script>
<script type="text/javascript"	src="preview.js"></script>
<script type="text/javascript" src="../common/delete.js"></script>
<script type="text/javascript" src="../common/validationOverride.js"></script>
<script type="text/javascript" src="../common/taskInfoGetter.js"></script>
<script type="text/javascript" src="../common/saveUtil.js"></script>
<script type="text/javascript" src="PTN_IPRAN.js"></script>

</head>

<body>
</body>
</html>
