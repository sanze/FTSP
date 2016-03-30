<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" /> 
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script>
   	<script type="text/javascript">
   		var isCurrent="<%=request.getParameter("isCurrent")%>";
   		var TYPE="<%=request.getParameter("TYPE")%>";
   		if(isCurrent == 1){
   			var TEMPLATE_ID="<%=request.getParameter("TEMPLATE_ID")%>";
   			var PM_STD_INDEX="<%=request.getParameter("PM_STD_INDEX")%>";
   			var domain = <%=request.getParameter("domain")%>;
   		}else{
   			var infos='<%=request.getParameter("infos")%>';
   		}
   		
    </script>
    <script type="text/javascript" src="templateInfo.js"></script>
    
  </head>
<body>
    <div id="form-connectInfo"></div>
</body>
</html>