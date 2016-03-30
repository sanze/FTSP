<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="../commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript" src="../commonManager/deviceAuthCombox.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/Beeper.js"></script> 
   <%--  <script type="text/javascript">
     var userId = "<%=session.getAttribute("SYS_USER_ID")%>";
    </script>
     <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/mySessionProvider.js"></script> --%>
    <script  src="<%=request.getContextPath()%>/resource/expandExt/js/mySessionProvider.js"></script>
    <script type="text/javascript">
    	// 地图模块传的局站ID
    	var staticStationId = <%=request.getParameter("gis_stationId")%>;
    	if(staticStationId==null){
    		staticStationId = '';
    	}
    	
 		// 视图模块传的网管分组ID
    	var staticEmsGroupId = <%=request.getParameter("view_emsGroupId")%>;
    	if(staticEmsGroupId==null){
    		staticEmsGroupId = '';
   		}
     	// 视图模块传的网管ID
   		var staticEmsId = <%=request.getParameter("view_emsId")%>;
   		if(staticEmsId==null){
   			staticEmsId = '';
   		}
   		
   		var staticSubNeId = '';
   		
   		// 视图模块传的网元ID
   		var staticNeId = <%=request.getParameter("view_neId")%>;
   		if(staticNeId==null){
   			staticNeId = '';
   		}
   		// 视图模块传的端口  ID
   		var view_ptpId = "<%=request.getParameter("view_ptpId")%>";
   		if(view_ptpId=="null"){
   			view_ptpId = '';
   		}
   		// 视图模块传的板卡  ID
   		var view_unitId = <%=request.getParameter("view_unitId")%>;
   		if(view_unitId==null){
   			view_unitId = '';
   		}
   		// 视图模块EMS选择信息
   		var emsInfo = <%=request.getParameter("view_emsInfo")%>;
   		// 视图模块NE选择信息
   		var neInfo = <%=request.getParameter("view_neInfo")%>;
   		
   		var userId=<%=session.getAttribute("SYS_USER_ID")%>;
   		
   		// 首页告警跳转新增参数
   		var severity = <%=request.getParameter("fp_severity")%>;
   		if (severity==null) { severity = 5; }
   		var isToday = <%=request.getParameter("fp_isToday")%>;
   		if (isToday==null) { isToday = false; }
   		
    </script>
    <script type="text/javascript" src="../../resource/expandExt/js/RowExpander.js"></script>
    <script type="text/javascript" src="alarmConvergeShow.js"></script>
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