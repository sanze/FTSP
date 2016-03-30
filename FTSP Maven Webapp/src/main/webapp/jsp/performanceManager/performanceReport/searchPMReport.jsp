<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/MultiSelect.css" />
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all-debug.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/MultiSelect.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ItemSelector.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
    <script type="text/javascript">
		var	taskTypeMS="<%=CommonDefine.QUARTZ.JOB_REPORT_MS%>";
		var	taskTypeNE="<%=CommonDefine.QUARTZ.JOB_REPORT_NE%>";
		var reportIdMark = null;
		var exportFilenameAnalysis = null;
		var exportFilenameAnalysisCFNE = null;
		var exportFilenameAnalysisCFMS = null;
		var exportFilenameAnalysisCFPTP = null;
		var reportTypeMark = null;
	</script>
	<script type="text/javascript">
		var NMS_TYPE=<%=CommonDefine.toJsonArray(CommonDefine.NMS_TYPE)%>;
	</script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
	
    <script type="text/javascript" src="collectFailedNe.js"></script>
    <script type="text/javascript" src="collectFailedPtp.js"></script>
    <script type="text/javascript" src="collectFailedMS.js"></script>
    <script type="text/javascript" src="searchPMReport.js"></script>

  </head>
  
  <body>
  </body>
</html>
