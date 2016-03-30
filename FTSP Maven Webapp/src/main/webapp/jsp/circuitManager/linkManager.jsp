<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<title>FTSP3.0 传输网络维护支撑平台</title>
<link rel="stylesheet" type="text/css"	href="../../resource/ext/resources/css/ext-all.css" />
<script type="text/javascript"	src="../../resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../../resource/ext/ext-all-debug.js"></script>
<link rel="stylesheet" type="text/css"	href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />
<script type="text/javascript"	src="../../resource/expandExt/ux/LockingGridView.js"></script>
<script type="text/javascript"	src="../../resource/expandExt/js/TreeCheckNodeUI.js"></script>
<script type="text/javascript"	src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
<link rel="stylesheet" type="text/css"	href="../../resource/expandExt/css/fileuploadfield.css" />
<style type=text/css>
.uploader {
	background: url('../../resource/images/buttonImages/add.png') no-repeat 0
		0 !important;
}
</style>
<script type="text/javascript" src="../commonManager/commonAuthDomian.js"></script>  
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/comboGrid.js"></script>

<script type="text/javascript"	src="../../resource/expandExt/js/FileUploadField.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
<script type="text/javascript">
	userId="<%=session.getAttribute("SYS_USER_ID")%>";
	displayName="<%=session.getAttribute("USER_NAME")%>";
	userName="<%=session.getAttribute("LOGIN_NAME")%>";
	var passedLinkId="<%=request.getParameter("linkId")%>";
</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/resourceManager/common/cableCommon.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/resourceManager/common/cable1Common.js"></script>
<script type="text/javascript" src="linkManager.js"></script>
</head>

<body>
</body>
</html>