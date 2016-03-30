<%@ page language="java" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=9">
	<title>FTSP3.2 传输网络维护支撑平台</title>
	<style type="text/css">
	.margin-for-button {
	    margin:10px 0px 0px 0px;
	}
	</style>
	<link rel="stylesheet" type="text/css" href="<%=path%>/resource/ext/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="<%=path%>/resource/expandExt/css/expand.css" />
	<link rel="stylesheet" type="text/css" href="<%=path%>/resource/css/common.css" />

	<script type="text/javascript" src="<%=path%>/resource/ext/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="<%=path%>/resource/ext/ext-all-debug.js"></script>
	<script type="text/javascript" src="<%=path%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
	
	<script type="text/javascript" src="<%=path%>/resource/expandExt/js/timeFormat.js"></script>
	<script type="text/javascript" src="<%=path%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
	<script language="JavaScript"  src="<%=path%>/resource/FusionCharts/JS/FusionCharts.js"></script>
	<script language="JavaScript"  src="<%=path%>/resource/FusionCharts/JS/FusionChartsExportComponent.js"></script>

    <script type="text/javascript">
        var testRouteId = "<%=request.getParameter("testRouteId")%>";
    </script>

    <script type="text/javascript" src="resultDiagram.js"></script>

</head>
<body>
</body>
</html>