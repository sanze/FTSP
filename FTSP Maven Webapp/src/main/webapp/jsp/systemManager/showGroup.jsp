<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
    
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/timeComponent/WdatePicker.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
   <script type="text/javascript">
  	 	var sysUserGroupId="<%=request.getParameter("sysUserGroupId")%>"
		var saveType="<%=request.getParameter("saveType")%>"
    </script>
    <script type="text/javascript" src="showGroup.js"></script>
<body>
	<div id="form-userInfo"></div>
</body>
</html>