<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>FTSP3.0 传输网络维护支撑平台</title>
<style type="text/css">
.margin-for-button {
    margin:10px 0px 0px 0px;
}
</style>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />

<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/ext-all-debug.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/ux/Ext.ux.PanelCollapsedTitle.js"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/resource/FusionCharts/JS/FusionCharts.js"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/resource/FusionCharts/JS/FusionChartsExportComponent.js"></script>
<script type="text/javascript">
 	var	linkId=<%=request.getParameter("linkId")%>;
 	var endDate="<%=request.getParameter("endDate")%>";
 	var startDate=Date.parseDate(endDate,"Y-m-d").add(Date.MONTH,-1).format("yyyy-MM-dd");
</script>
<script type="text/javascript" src="performanceDiagram.js"></script>

</head>
<body>

</body>
</html>