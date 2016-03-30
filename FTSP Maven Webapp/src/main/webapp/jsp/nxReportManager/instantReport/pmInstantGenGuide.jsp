<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/css/common.css" />
    <script type="text/javascript">
		 var userId="<%=session.getAttribute("SYS_USER_ID")%>";
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all-debug.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript">
	var REPORT_TYPE_ORIN=<%=CommonDefine.toJsonArray(CommonDefine.NX_REPORT_TYPE)%>;
	var REPORT_TYPE = [];
	var NONE_INSTANT = [];
	NONE_INSTANT.push(<%=CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK%>);
	NONE_INSTANT.push(<%=CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_IPRAN%>);
	for (var i=0;i<REPORT_TYPE_ORIN.length;i++){
		if(NONE_INSTANT.indexOf(REPORT_TYPE_ORIN[i].key)==-1){
			REPORT_TYPE.push(REPORT_TYPE_ORIN[i]);
		}
	}
	var typeDefine = new Object();
	typeDefine.WAVELENGTH = <%=CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH%>;
	typeDefine.AMP = <%=CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_AMP%>;
	typeDefine.SWITCH = <%=CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_SWITCH%>;
	typeDefine.WAVE_DIV = <%=CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_DIV%>;
	typeDefine.WAVE_JOIN = <%=CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_JOIN%>;
	typeDefine.SDH_PM = <%=CommonDefine.QUARTZ.JOB_NX_REPORT_SDH_PM%>;
	</script>
    <script type="text/javascript" src="pmInstantGenGuide.js"></script>
    
    <style type="text/css">
    </style>

  </head>
<body>
</body>
</html>