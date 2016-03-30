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
    <script type="text/javascript" src="../commonManager/deviceAuthCombox.js"></script>
    <script type="text/javascript">
		var saveType="<%=request.getParameter("saveType")%>";
		var cutoverTaskId="<%=request.getParameter("cutoverTaskId")%>";
    </script>
    <script type="text/javascript" src="alarmDuringCutover.js"></script>
       <style type="text/css">
/*         .button-red { */
/* 			background-image: url(../resource/images/otherImages/loginbg2.png)!important;width:60!imporant;height:30!imporant;  */
/* 		} */
		.button-jinji{
/*  			background-color: #FF0000;  */
 			background-image: url(../../resource/images/01.png)!important; 
		}
		.button-zhongyao{
/* 			background-color: #FF8000;  */
			background-image: url(../../resource/images/02.png)!important;
		}
		.button-ciyao{
/* 			background-color: #FFFF00;  */
			background-image: url(../../resource/images/03.png)!important;
		}
		.button-tishi{
/* 			background-color: #800000;  */
			background-image: url(../../resource/images/04.png)!important;
		}
		.button-cleared{
			background-image: url(../../resource/images/05.png)!important;
		}
		.button-zhengchang{
			background-color: #00FF00; 
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

</body>
</html>