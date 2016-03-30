<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP3.0 传输网络维护支撑平台</title>  
     <script type="text/javascript">
		var	mul_id="<%=request.getParameter("mul_id")%>";
		var	emsId="<%=request.getParameter("emsId")%>";
		var	direction="<%=request.getParameter("direction")%>";
		var	factory="<%=request.getParameter("factory")%>";
		
    	</script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/MultiSelect.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all-debug.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/MultiSelect.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ItemSelector.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>       
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
    <script type="text/javascript" src="../../commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript" src="commonMaun.js"></script>
    <script type="text/javascript" src="addRouteManu.js"></script>

    
  </head>
<body>
    <div id="form"></div>
</body>
</html>