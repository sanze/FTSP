<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />
     <script type="text/javascript">
     var userId = "<%=session.getAttribute("SYS_USER_ID")%>";
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/commonAuthDomian.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/deviceAuthCombox.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/commonManager/LockingGridMethods.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/Beeper.js"></script> 
     <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/mySessionProvider.js"></script>
    <script  src="<%=request.getContextPath()%>/resource/expandExt/js/mySessionProvider.js"></script>
    <script type="text/javascript">
    	var staticEmsGroupId = '';
   		var staticEmsId = '';
   		var staticSubNeId = '';
   		var staticNeId = '';
   		var staticPtpId = '';
   		var view_ptpId = '';
   		var view_unitId = '';
   		var emsInfo = '';
   		var neInfo = '';
   		
   		/**监测用**/
   		var taskType="<%=request.getParameter("taskType")%>";
		var taskId=<%=request.getParameter("taskId")%>;
		var stateIdvar = "imptProAlarmStateId";
		
		var NodeDefine = {};
		NodeDefine.EMSGROUP=<%=CommonDefine.TREE.NODE.EMSGROUP%>;
		NodeDefine.EMS=<%=CommonDefine.TREE.NODE.EMS%>;
		NodeDefine.SUBNET=<%=CommonDefine.TREE.NODE.SUBNET%>;
		NodeDefine.NE=<%=CommonDefine.TREE.NODE.NE%>;
		NodeDefine.PTP=<%=CommonDefine.TREE.NODE.PTP%>;
    </script>
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/RowExpander.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jsp/faultManager/alarmConvergeShow.js"></script>
    <script type="text/javascript" src="currentAlarm.js"></script>
    
    
       <style type="text/css">
		.button1{
 			background-image: url(../../resource/images/01.png)!important; 
		}
		.button2{
			background-image: url(../../resource/images/02.png)!important;
		}
		.button3{
			background-image: url(../../resource/images/03.png)!important;
		}
		.button4{
			background-image: url(../../resource/images/04.png)!important;
		}
		.button5{
			background-image: url(../../resource/images/05.png)!important;
		}
		.alarm-color1{
			background: #FF0000;
		}	
		.alarm-color2{
			background: #FF8000;
		}
		.alarm-color3{
			background: #FFFF00;
		}
		.alarm-color4{
			background: #800000;
		}
		.alarm-color5{
			background: #00FF00;
		}
        
    </style>
  </head>
<body>
<div style="display: none">
<form id="exportExcel" name="exportExcel" action="" target="export" method="post" accept-charset="UTF-8"></form>
<iframe name="export" id="export"></iframe>
</div>
</body>
</html>