<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<base />
<title>FTSP3.0 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"
	href="../../../resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="../../../resource/expandExt/css/expand.css" />
<link rel="stylesheet" type="text/css"
	href="../../../resource/css/common.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/MultiSelect.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/LovCombo.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
	
	
<script type="text/javascript"
	src="../../../resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript"
	src="../../../resource/ext/ext-all-debug.js"></script>
<script type="text/javascript"
	src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="../../../resource/js/util.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/LovCombo.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/Ext.ux.PanelCollapsedTitle.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
<script type="text/javascript">
	var userId=<%=session.getAttribute("SYS_USER_ID")%>;
</script>
<script type="text/javascript"
	src="../../../jsp/commonManager/commonAuthDomian.js"></script>
<script
	src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/jsp/commonManager/equipTree.js"></script>	
<script type="text/javascript"	src="instantTaskInfoDefine.js"></script>
<script type="text/javascript" src="../common/pmFieldsetAndGetterDefine.js"></script>
<script type="text/javascript" src="../reportTask/addWaveDir.js"></script>
<script type="text/javascript">
	var repType = <%=CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH%>;
	var userId=<%=session.getAttribute("SYS_USER_ID")%>;
	var FACTORY=<%=CommonDefine.toJsonArray(CommonDefine.FACTORY)%>;
</script>
<script type="text/javascript" src="WDMWaveLengthInstant.js"></script>

<style type="text/css">
</style>

</head>
<body>
</body>
</html>