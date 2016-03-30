<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/expand.css" />
    
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>

	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeFormat.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/timeComponent/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
	<script type="text/javascript" src="../../commonManager/commonAuthDomian.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script>
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/mySessionProvider.js"></script>
 	<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/LockingGridMethods.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script>

	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/equipTree.js"></script>
	
    <script type="text/javascript" src="../common/taskCommon.js"></script>
    <script type="text/javascript">
    	Ext.BLANK_IMAGE_URL = "<%=request.getContextPath()%>/resource/ext/resources/images/default/s.gif";
    	var ROOT_PATH="<%=request.getContextPath()%>";
    	var authSequence="<%=request.getParameter("authSequence")%>";
    	var today=new Date();
 		var startDate=today.format(dateFmt);
		var endDate=today.add(Date.MONTH,1).format(dateFmt);
		
		var editType="<%=request.getParameter("editType")%>";
		var taskType="<%=request.getParameter("taskType")%>";
		var taskId=<%=request.getParameter("taskId")==""?null:
			request.getParameter("taskId")%>;
    </script>
    <script type="text/javascript" src="editTask.js"></script>
    
  </head>
<body>
    <div id="form"></div>
</body>
</html>