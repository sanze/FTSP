<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>FTSP3.0 传输网络维护支撑平台</title>
	<%--使用Extjs的主要文件,每个jsp页面必须引用--%>
    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript" src="../../resource/js/util.js"></script>
    <script type="text/javascript" src="../commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript" src="store.js"></script>
    <!--引入dwr的js脚本-->
    <script type='text/javascript' src='../../dwr/engine.js'></script>
    <script type='text/javascript' src='../../dwr/interface/WebMsgPush.js'></script>
    <script type='text/javascript' src='../../dwr/util.js' /></script>
    <%--该页面的参数 --%>
    <script type="text/javascript">
	<%--登录用户ID,需要保存grid列状态时用到--%>
    var userId = "<%=session.getAttribute("SYS_USER_ID")%>";
    <%--设置唯一的列状态保存Id,需要保存grid列状态时用到--%>
    var myStateId = "faultManagementListGrid";
    <%--指定使用alarmConvergenceStore--%>
    var myStore = faultManagementStore;
    <%--指定使用alarmConvergencePageTool--%>
    var myPageTool = faultManagementPageTool;
    </script>
    <%--使用gridPanel时建议引用的文件--%>
    <script src="../../resource/expandExt/js/mySessionProvider.js"></script>
 	<script src="../commonManager/LockingGridMethods.js"></script>
    <link rel="stylesheet" type="text/css" href="../../resource/expandExt/css/LockingGridView.css" />
	<script type="text/javascript" src="../../resource/expandExt/ux/LockingGridView.js"></script>
	<%--整个页面用到的js文件--%>
	<script type="text/javascript" src="faultManagementDefine.js"></script>
	<script type="text/javascript" src="showPage.js"></script>
	<script type="text/javascript" src="../../resource/expandExt/js/timeFormat.js"></script>
	<script type="text/javascript" src="../../resource/expandExt/js/timeComponent/WdatePicker.js"></script>
	<script type="text/javascript" src="toolbarFun.js"></script>
	<script type="text/javascript" src="addAlarm.js"></script>
	<script type="text/javascript" src="faultInfoWindow.js"></script>
	<script type="text/javascript" src="faultManagement.js"></script>
	
	<style type="text/css">
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
</body>
</html>