<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">   
	<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">   
	<META HTTP-EQUIV="Expires" CONTENT="0"> 

    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/xtheme-blue.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/expandExt/css/main.css" />
    
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/Portal.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/PortalColumn.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/Portlet.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="../../resource/js/util.js"></script>
     
    
    <style type="text/css">
	    .btn-blue {
	    	background-image: url(../../resource/images/blue.png)!important;
	    }
	    
	    .my-window-no-border {
	    	border-style:none;
	    }
	    
	    .my-label-style {
	    	font-size:12px;
	    /*	background-color:#d9e4f4; */
	    }
	    
	    .x-fieldset{
	    	border:1px solid #B5B8C8;
	    	padding:10px;
	    	margin-bottom:10px;
	    	display:block;
	    /*	border-bottom-color:Red;
	    	border-left-color:Green;
	    	border-right-color:Blue;
	    	border-top-color:Purple;
	    	text-decoration:underline; 
	    	background-color:#d9e4f4; */
	    }
	    
	    .my-fieldset-style {
	    	border:1px solid #B5B8C8;
	    	padding:10px;
	    	margin-bottom:10px;
	    	display:block;
	    	border-bottom-color:Red;
	    	border-left-color:Green;
	    	border-right-color:Blue;
	    	border-top-color:Purple;
	    	text-decoration:underline;
	    }
    </style>
<script>
  function move(id,object) {
    var mm = document.getElementById(id);
	mm.src = "../../resource/images/otherImages/"+object+".png";
  }
</script>
<script type="text/javascript" src="centerPanel.js"></script> 
</head>
<body class="mainBody">
<!-- <div id="mainBody" class="mainBody"> -->
  <!--<div id="left" class="left">
       <img id="netWork" onclick="javascript:showNewTab(1)" onmouseover="move('netWork','networkOn');" onmouseout="move('netWork','networkTitle');" src="../../resource/images/otherImages/networkTitle.png"/>
       <img id="alarm" onclick="javascript:showNewTab(2)" onmouseover="move('alarm','alarmOn');" onmouseout="move('alarm','alarmTitle');" src="../../resource/images/otherImages/alarmTitle.png"/>
       <img id="SDHPm" onclick="javascript:showNewTab(3)" onmouseover="move('SDHPm','SDHPMOn');" onmouseout="move('SDHPm','SDHPMTitle');" src="../../resource/images/otherImages/SDHPMTitle.png"/>
       <img id="WDMPm" onclick="javascript:showNewTab(4)" onmouseover="move('WDMPm','WDMPMOn');" onmouseout="move('WDMPm','WDMPMTitle');" src="../../resource/images/otherImages/WDMPMTitle.png"/>
       <img id="pmReport" onclick="javascript:showNewTab(5)" onmouseover="move('pmReport','pmReportOn');" onmouseout="move('pmReport','pmReportTitle');" src="../../resource/images/otherImages/pmReportTitle.png"/>
       <img id="WDMOptical" onclick="javascript:showNewTab(6)" onmouseover="move('WDMOptical','WDMOpticalOn');" onmouseout="move('WDMOptical','WDMOpticalTitle');" src="../../resource/images/otherImages/WDMOpticalTitle.png"/>
  </div>
  <!-- <div class="border">
  	<img src="../../resource/images/otherImages/border.png"/>
  </div> -->
  <div id="left" class="left">
       <!-- <img id="netWork"  onclick="javascript:showNewTab(1)" onmouseover="move('netWork','networkOn');" onmouseout="move('netWork','networkTitle');" src="../../resource/images/otherImages/networkTitle.png"/>
       <img id="alarm" style="margin-top:5px;" onclick="javascript:showNewTab(2)" onmouseover="move('alarm','alarmOn');" onmouseout="move('alarm','alarmTitle');" src="../../resource/images/otherImages/alarmTitle.png"/>
       <img id="SDHPm" style="margin-top:5px;" onclick="javascript:showNewTab(3)" onmouseover="move('SDHPm','SDHPMOn');" onmouseout="move('SDHPm','SDHPMTitle');" src="../../resource/images/otherImages/SDHPMTitle.png"/>
       <img id="WDMPm" style="margin-top:5px;" onclick="javascript:showNewTab(4)" onmouseover="move('WDMPm','WDMPMOn');" onmouseout="move('WDMPm','WDMPMTitle');" src="../../resource/images/otherImages/WDMPMTitle.png"/>
       <img id="pmReport" style="margin-top:5px;" onclick="javascript:showNewTab(5)" onmouseover="move('pmReport','pmReportOn');" onmouseout="move('pmReport','pmReportTitle');" src="../../resource/images/otherImages/pmReportTitle.png"/>
       <img id="WDMOptical" style="margin-top:5px;" onclick="javascript:showNewTab(6)" onmouseover="move('WDMOptical','WDMOpticalOn');" onmouseout="move('WDMOptical','WDMOpticalTitle');" src="../../resource/images/otherImages/WDMOpticalTitle.png"/> -->
  </div>
  <div class="right">
      <!-- <div style="width:800px;">
	  <img src="../../resource/images/otherImages/gridTop.png" width="1120px;"/>
	  </div> -->
	   <div class="rightGrid">
	     <div id = "userPanel" class="summary">
		     <div class="title">
			     <img src="../../resource/images/otherImages/2.png" width="32px" height="32px"/>
			     <span style="margin-top:50px;">摘要</span>
		     </div>
		     <table class="summaryTable" border="1px solid black">
		       <tr class="alarmTableTr">
			      <th class="summaryTableTh">系统当前时间</th>
			      <td class="summaryTableTd" id="currentTime"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="summaryTableTh">上次登录时间</th>
			      <td class="summaryTableTd" id="loginTimeLabel"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="summaryTableTh">上次退出时间</th>
			      <td class="summaryTableTd" id="logoutTimeLabel"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="summaryTableTh">南向连接</th>
			      <td class="summaryTableTd" id="southConnectCount"></td>
			   </tr>
		       <tr class="alarmTableTr">
			      <th class="summaryTableTh">连接正常</th>
			      <td class="summaryTableTd" id="connectNormalCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="summaryTableTh">连接中断</th>
			      <td class="summaryTableTd" id="connectDisconnectCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="summaryTableTh">连接异常</th>
			      <td class="summaryTableTd" id="connectExceptionCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="summaryTableTh">网络中断</th>
			      <td class="summaryTableTd" id="connectInterruptCount"></td>
			   </tr>
		     </table>
	     </div>
	     <div id = "taskPanel" class="task">
		     <div class="title">
			     <img src="../../resource/images/otherImages/7.png" width="32px" height="32px"/>
			     任务
		     </div>
		     <table class="taskTable" border="1px solid black">
		       <tr class="alarmTableTr">
			      <th class="taskTableTh"></th>
			      <td class="taskTableTd">全部任务</td>
			      <td class="taskTableTd">启用状态</td>
			      <td class="taskTableTd">执行完成</td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="taskTableTh">网管自动同步任务</th>
			      <td class="taskTableTd" id="EMSAutoSYNCTaskNum"></td>
			      <td class="taskTableTd" id="EMSAutoSYNCStartStatus"></td>
			      <td class="taskTableTd" id="EMSAutoSYNCSuccess"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="taskTableTh">告警自动同步任务</th>
			      <td class="taskTableTd" id="alarmAutoSYNCTaskNum"></td>
			      <td class="taskTableTd" id="alarmAutoSYNCStartStatus"></td>
			      <td class="taskTableTd" id="alarmAutoSYNCSuccess"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="taskTableTh">性能采集任务</th>
			      <td class="taskTableTd" id="pmCollTaskNum"></td>
			      <td class="taskTableTd" id="pmCollStartStatus"></td>
			      <td class="taskTableTd" id="pmCollSuccess"></td>
			   </tr>
		       <tr class="alarmTableTr">
			      <th class="taskTableTh">性能报表生成任务</th>
			      <td class="taskTableTd" id="pmReportTaskNum"></td>
			      <td class="taskTableTd" id="pmReportStartStatus"></td>
			      <td class="taskTableTd" id="pmReportSuccess"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="taskTableTh">割接任务</th>
			      <td class="taskTableTd" id="cutOverTaskNum"></td>
			      <td class="taskTableTd" id="cutOverStartStatus"></td>
			      <td class="taskTableTd" id="cutOverSuccess"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="taskTableTh">电路自动生成</th>
			      <td class="taskTableTd" id="circuitAutoNewTaskNum"></td>
			      <td class="taskTableTd" id="circuitAutoNewStartStatus"></td>
			      <td class="taskTableTd" id="circuitAutoNewSuccess"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="taskTableTh">数据库备份</th>
			      <td class="taskTableTd" id="dbBackupTaskNum"></td>
			      <td class="taskTableTd" id="dbBackupStartStatus"></td>
			      <td class="taskTableTd" id="dbBackupSuccess"></td>
			   </tr>
		     </table>
	     </div>
	     	
	     <div class="topGrid">
	     <div id = "pmPanel" class="performance">
	         <div class="title">
		     <img src="../../resource/images/otherImages/9.png" width="32px" height="32px"/>
			     性能
		     </div>
		     <table class="performanceTable" border="1px solid black">
		       <tr class="alarmTableTr">
			      <th class="performanceTableTh">昨天采集网元数</th>
			      <td class="performanceTableTd" id="yesCollNeCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="performanceTableTh">昨天成功采集网元数</th>
			      <td class="performanceTableTd" id="yesSucCollNeCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="performanceTableTh">重要预警</th>
			      <td class="performanceTableTd" id="importantWarning"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="performanceTableTh">次要预警</th>
			      <td class="performanceTableTd" id="secondaryWarning"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="performanceTableTh">一般预警</th>
			      <td class="performanceTableTd" id="commonlyWarning"></td>
			   </tr>
		     </table>
	     </div>
	     <div id = "almPanel" class="alarm">
		     <div class="title">
			     <img src="../../resource/images/otherImages/4.png" width="32px" height="32px"/>
			     告警
		     </div>
		     <table class="alarmTable" border="1px solid black">
		       <tr class="alarmTableTr">
			      <th class="alarmTableTh"></th>
			      <td class="alarmTableTd">当前告警总数</td>
			      <td class="alarmTableTd">今日新增告警数</td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="alarmTableTh">紧急告警</th>
			      <td class="alarmTableTd" id="totalCRCount"></td>
			      <td class="alarmTableTd" id="newAddCRCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="alarmTableTh">重要告警</th>
			      <td class="alarmTableTd" id="totalMJCount"></td>
			      <td class="alarmTableTd" id="newAddMJCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="alarmTableTh">次要告警</th>
			      <td class="alarmTableTd" id="totalMNCount"></td>
			      <td class="alarmTableTd" id="newAddMNCount"></td>
			   </tr>
			   <tr class="alarmTableTr">
			      <th class="alarmTableTh">提示告警</th>
			      <td class="alarmTableTd" id="totalWRCount"></td>
			      <td class="alarmTableTd" id="newAddWRCount"></td>
			   </tr>
		     </table>
		 </div>
	     
	     </div>
	          
	  </div>
  </div>
<!-- </div> -->
</body>
</html>