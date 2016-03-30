<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
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
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
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
	var	ptpId="<%=request.getParameter("ptpId")%>";
</script>
<script type="text/javascript">
	var	ctpId="<%=request.getParameter("ctpId")%>";
</script>
<script type="text/javascript">
	var	pmStdIndex="<%=request.getParameter("pmStdIndex")%>";
</script>
<script type="text/javascript">
	var	emsConnectionId="<%=request.getParameter("emsConnectionId")%>";
</script>
<script type="text/javascript">
	var	type="<%=request.getParameter("type")%>";
	var	name="<%=request.getParameter("name")%>";
	var num = "<%=request.getParameter("num")%>";
	var	starttime="";
	
</script>
<script type="text/javascript" src="performanceDiagram.js"></script>

</head>
<body>

</body>
</html>