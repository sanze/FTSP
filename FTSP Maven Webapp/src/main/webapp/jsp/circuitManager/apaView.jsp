<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=8">
		<title>FTSP APA View</title>
		<meta charset="utf-8">
		<script type="text/javascript">
        var	vCircuit="<%=request.getParameter("vCircuit")%>";
        var	infoId="<%=request.getParameter("infoId")%>";
        var serviceType="<%=request.getParameter("serviceType")%>";
       var userId="<%=session.getAttribute("SYS_USER_ID")%>";
	    </script>
		<link rel="stylesheet" type="text/css"
			href="../../resource/ext/resources/css/ext-all.css" />
		<script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base-debug.js"></script>
		<script type="text/javascript" src="../../resource/ext/ext-all-debug.js"></script>
		 <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
		<script type="text/javascript" src="innerRoute.js"></script>
		<script type="text/javascript" src="../viewManager/Flex.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />
	<script type="text/javascript" src="../../resource/expandExt/ux/LockingGridView.js"></script>
        
		<script type="text/javascript"
			src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
        <style type=text/css>
			.AlarmCss_INDETERMINATE {
				background-color: red;
			}
			.AlarmCss_CRITICAL {
				background-color: fuchsia;
			}
			.AlarmCss_MAJOR {
				background-color: orangered;
			}
			.AlarmCss_MINOR {
				background-color: yellow;
			}
			.AlarmCss_WARNING {
				background-color: cyan;
			}
			.AlarmCss_CLEARED {
				background-color: lime;
			}
			 .label_css {
				font: 14px  sans-serif;
			}
		}
		</style>
		<script type="text/javascript" src="../commonManager/commonAuthDomian.js"></script>
		<script type="text/javascript" src="apaView.js"></script>
		<!-- Common Styles for the examples -->
	</head>
	<body>
	</body>
</html>