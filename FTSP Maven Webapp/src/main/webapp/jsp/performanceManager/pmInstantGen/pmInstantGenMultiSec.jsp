<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/LovCombo.css" />
    
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all-debug.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/LovCombo.js"></script>
    <script type="text/javascript" src="../../../resource/js/util.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/ux/Ext.ux.PanelCollapsedTitle.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
    <script type="text/javascript" src="../../../jsp/commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript">
        var	targetIdTrunkLine="<%=CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE%>";
        var	targetIdMultiSec="<%=CommonDefine.TASK_TARGET_TYPE.MULTI_SEC%>";
    </script>
    <script type="text/javascript" src="../performanceReport/addMS.js"></script>
    <script type="text/javascript" src="../performanceReport/addTL.js"></script>
    <script type="text/javascript" src="pmInstantGenMultiSec.js"></script>
    
    <style type="text/css">
    </style>

  </head>
<body>
</body>
</html>