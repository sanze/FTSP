<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=8">
		<base />
		<title>FTSP3.0 传输网络维护支撑平台</title>
		<script type="text/javascript">   	
	var	virName="<%=request.getParameter("virName")%>";
	var	caculateDown="<%=request.getParameter("caculateDown")%>";
	var	idss="<%=request.getParameter("ids")%>";
	var	type="<%=request.getParameter("type")%>";
	var portType = "<%=request.getParameter("portType")%>";
	var virType = "<%=request.getParameter("virType")%>";
	var neId = "<%=request.getParameter("neId")%>";	 	
	</script>
		<link rel="stylesheet" type="text/css"
			href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css"
			href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
		<link rel="stylesheet" type="text/css"
			href="<%=request.getContextPath()%>/resource/expandExt/css/MultiSelect.css" />
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/MultiSelect.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/ItemSelector.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
		<script type="text/javascript" src="modifyVirPort.js"></script>

	</head>
	<body>
		<div id="form"></div>
	</body>
</html>