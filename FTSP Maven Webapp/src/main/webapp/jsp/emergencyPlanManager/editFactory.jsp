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
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
<script type="text/javascript" src="editFactory.js"></script>

<script type="text/javascript">  
	var editType = <%=request.getParameter("editType")%>;	
	var factoryContactId = -99;
		if(editType==1){ 
		//修改
			var factoryContactId = <%=request.getParameter("factoryContactId")%>;	
		}
</script>
</head> 
<body>
</body>

</html>