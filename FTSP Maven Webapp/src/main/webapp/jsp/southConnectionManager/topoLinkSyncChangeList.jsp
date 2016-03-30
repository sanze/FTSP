<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/expandExt/css/expand.css" />   
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    
    <link rel="stylesheet" type="text/css" href="../../resource/expandExt/ux/treegrid/treegrid.css" rel="stylesheet" />
    <script type="text/javascript" src="../../resource/expandExt/ux/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/ux/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/ux/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/ux/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/ux/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/ux/treegrid/TreeGrid.js"></script>
    <link rel="stylesheet" type="text/css" href="../../resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="../../resource/expandExt/js/TreeCheckNodeUI.js"></script>
        
    <script type="text/javascript" src="../../resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/timeFormat.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/timeComponent/WdatePicker.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script>
    <script type="text/javascript">
		Ext.BLANK_IMAGE_URL="<%=request.getContextPath()%>/resource/ext/resources/images/default/s.gif";
		var emsConnectionId="<%=request.getParameter("emsConnectionId")%>";
		var emsGroupId="<%=request.getParameter("emsGroupId")%>";
    </script>
    <script type="text/javascript" src="topoLinkSyncChangeList.js"></script>
  </head>
<body>
    <div id="form"></div>
    <div id="win"></div>
</body>