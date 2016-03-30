<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.fujitsu.common.FieldNameDefine;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=9">
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">   
	<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">   
	<META HTTP-EQUIV="Expires" CONTENT="0">
	<!--引入dwr的js脚本-->
    <script type='text/javascript' src='../../dwr/engine.js'></script>
    <script type='text/javascript' src='../../dwr/util.js' /></script>
     
    <script type="text/javascript">
    	FieldNameDefine={
   			AREA_ROOT_NAME:"<%=FieldNameDefine.AREA_ROOT_NAME%>",
			AREA_NAME:"<%=FieldNameDefine.AREA_NAME%>",
			STATION_NAME:"<%=FieldNameDefine.STATION_NAME%>",
			NET_LEVEL_NAME:"<%=FieldNameDefine.NET_LEVEL_NAME%>"
		};
    
		userId="<%=session.getAttribute("SYS_USER_ID")%>";
		displayName="<%=session.getAttribute("USER_NAME")%>";
		userName="<%=session.getAttribute("LOGIN_NAME")%>";
		isLockedStr = "<%=session.getAttribute("IS_LOCKED")%>";
		isLocked = (isLockedStr==null || isLockedStr=="null" || isLockedStr == "true");
		lockTime = "<%=session.getAttribute("TIME_OUT")%>";
        lockTime *= 60000;
		// 权限域
		authDomain="<%=session.getAttribute("authDomain")%>";
		// 设备域
		deviceDomain = "<%=session.getAttribute("deviceDomain")%>";
		if(userId == null || userId == "null"){
			window.open('../login/login.jsp', "_parent");
		}
    </script>

  <title>FTSP3.0传输网络维护支撑平台</title>
    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/css/common.css"/>
    
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/TabCloseMenu.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="../../resource/js/util.js"></script>
    <script type="text/javascript" src="faultInfoControl.js"></script>
    
    <!-- 引入TTS对象 -->
    <script type='text/javascript' src='speak0.js' /></script>
    
    <script type="text/javascript" src="main.js"></script>
    
    
    
    <style type="text/css">
    html, body {
        font:normal 15px verdana;
        margin:0;
        padding:0;
        border:0 none;
        overflow:hidden;
        height:100%;
    }
    </style>
    
    
    <style type="text/css">
	#drag {
		border:1px solid blue;
		width:400px;
		height:30px;
		position:absolute;
		background-color:#476269;
		top:0px;
		right:0px;
		display:none;
	}
	</style>
    
</head>
<body id="main" onload="dwr.engine.setActiveReverseAjax(true);">
	<div id="drag">
		<span style="height:30px; line-height:30px;">
			<font color="#00ff00" valign="middle">
				<marquee id="FaultMessageBox" direction="left" scrollamount="3"></marquee>
			</font>
		</span>
	</div>

    <div id="west" class="x-hide-display">
        <IFRAME name="west" frameBorder=0 scrolling=no width="100%" height="100%">
		</IFRAME>
    </div>
    <div id="east" class="x-hide-display">
    	<IFRAME name="east" frameBorder=0 scrolling=no width="100%" height="100%">
		</IFRAME>
    </div>
    <div id="center" class="x-hide-display">
    	<IFRAME name="center" frameBorder=0 scrolling=no width="100%" height="100%">
		</IFRAME>
    </div>
    <div id="south" class="x-hide-display">
        <IFRAME name="south" frameBorder=0 scrolling=no width="100%" height="100%">
		</IFRAME>
    </div>
    <div id="north" class="x-hide-display">
    	<IFRAME name="north" frameBorder=0 scrolling=no width="100%" height="100%">
		</IFRAME>
    </div>
    
</body>
</html>