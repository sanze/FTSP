<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP 3000 传输网络维护支撑平台</title>  
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/expand.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resource/expandExt/css/LockingGridView.css" />
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/ext/ext-all.js"></script>
	<script type="text/javascript"
	src="<%=request.getContextPath()%>/resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/ux/LockingGridView.js"></script>
	
    <script type="text/javascript" src="cutoverTaskOperationTab.js"></script>
    <style type="text/css">
        .x-check-group-alt {
            background: #D1DDEF;
            border-top:1px dotted #B5B8C8;
            border-bottom:1px dotted #B5B8C8;
        }
    </style>
		<style type="text/css">
		
		#div_topo{ position:relative; height: 90px;px;background-color:white;}
		/*#div_topo .frag1{position:absolute; left:0px;  max-height:90px; color:#CE0000;font-size:12px; }*/
		/*#div_topo .frag2{position:absolute; left:232.5px;  max-height:90px; color:#CE0000;font-size:12px; }*/
		/*#div_topo .frag3{position:absolute; left:334.5px;   max-height:90px;color:#CE0000;font-size:12px; }*/
		/*#div_topo .frag4{position:absolute; left:441.5px;   max-height:90px;color:#CE0000;font-size:12px; }*/
		/*#div_topo .frag5{position:absolute; left:655px;   max-height:90px;color:#CE0000;font-size:12px; }*/
		
		#div_topo .frag1{ float:left;  max-height:90px; color:#CE0000;font-size:12px; }
		#div_topo .frag2{ float:left;  max-height:90px; color:#CE0000;font-size:12px; }
		#div_topo .frag3{ float:left;  max-height:90px;color:#CE0000;font-size:12px; }
		#div_topo .frag4{ float:left; max-height:90px;color:#CE0000;font-size:12px; }
		#div_topo .frag5{ float:left;  max-height:90px;color:#CE0000;font-size:12px; }
		#div_topo .buttonDiv1{position:absolute; left:0px;  top: 90px;width: 100px  }
		#div_topo .buttonDiv2{position:absolute; left:117px;  top: 90px;width: 100px  }
		#div_topo .buttonDiv3{position:absolute; left:245px;  top: 90px;width: 100px  }
		#div_topo .buttonDiv4{position:absolute; left:380px;  top: 90px;width: 80px  }
		#div_topo .buttonDiv5{position:absolute; left:515px;  top: 93px;width: 100px  }
		#div_topo .buttonDiv6{position:absolute; left:635px;  top: 90px;width: 80px  }
		#div_topo .buttonDiv7{position:absolute; left:768px;  top: 90px;width: 80px  }
		#div_topo .page{position:absolute; left:0px;  top: 125px;width: 100%  }
		
		
		#div_topo .con{ width:850px; height:400px; background-color:#FFF; color:#000;}
		#div_topo .otdrEquipTips{position:absolute; left:18px; top:113px; color:#CE0000;font-size:12px; }
		
		#div_topo .OTDR_Equip{ width:100px;height:73px;  position:absolute; left:13px; top:30px;color:#FFF; }
		#div_topo .Line1{  width:140px; height:6px; position:absolute; left:109px; top:65px; color:#FFF; }
		
		#div_topo .OpticalSwitch1{  width:144px; height:200px; position:absolute; left:198px; top:45px; color:#FFF; }
		#div_topo .OpticalSwitch1Tips{position:absolute; left:210px; top:135px; color:#EA0000;font-size:12px; }
		#div_topo .PortLeft1{ position:absolute; left:203px; top:55px; color:#FFF; }
		#div_topo .PortRightUp1{ position:absolute; left:287px; top:55px; color:#FFF; }
		#div_topo .PortRightDown1{position:absolute; left:287px; top:93px; color:#FFF; }

		#div_topo .PortLeft1Tips{position:absolute; left:185px; top:50px; color:#EA0000;font-size:12px; }
		#div_topo .PortRightUp1Tips{position:absolute; left:341px; top:50px; color:#CE0000;font-size:12px; }
		#div_topo .PortRightDown1Tips{position:absolute; left:341px; top:91px; color:#CE0000;font-size:12px; }
		
		#div_topo .ConnUp1{ position:absolute; left:246px; top:65px; color:#FFF; }
		
		#div_topo .LineUp2{  width:100px; height:25px; position:absolute; left:340px; top:65px; color:#FFF; }
		#div_topo .LineDown2{  width:100px; height:25px; position:absolute; left:340px; top:103px; color:#FFF; }
				
		#div_topo .PortLeft2{  position:absolute; left:445px; top:93px; color:#FFF; }
		#div_topo .PortRightUp2{  position:absolute; left:530px; top:93px; color:#FFF; }
		#div_topo .PortRightDown2{ position:absolute; left:530px; top:130px; color:#FFF; }

		#div_topo .PortLeft2Tips{position:absolute; left:418px; top:90px; color:#EA0000;font-size:12px; }
		#div_topo .PortRightUp2Tips{position:absolute; left:580px; top:90px; color:#CE0000;font-size:12px; }
		#div_topo .PortRightDown2Tips{position:absolute; left:580px; top:130px; color:#CE0000;font-size:12px; }
		
		#div_topo .NeEquip1{  width:136px; height:162px; position:absolute; left:440px; top:50px; color:#FFF; }
		#div_topo .NeEquip1Tips{position:absolute; left:447px; top:36px; color:#CE0000;font-size:12px; }
		
		#div_topo .OpticalSwitch2{  height:162px; position:absolute; left:440px; top:85px; color:#FFF; }
		#div_topo .NeEquip2{  width:136px; height:162px; position:absolute; left:660px; top:86px; color:#FFF; }
		#div_topo .NeEquip2Tips{position:absolute; left:732px; top:70px; color:#CE0000;font-size:12px; }
		
		#div_topo .ConnUp2{  width:43px; height:50px; position:absolute; left:487px; top:102px; color:#FFF; }
		
		#div_topo .Line3{  width:150px; height:16px; position:absolute; left:580px; top:102px; color:#FFF; }
		
		#div_topo .OK{  position:absolute; left:800px; top:10px; color:#000; }
		#div_topo .Cancel{position:absolute; left:760px; top:86px; color:#FFF; }
		
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
		
		<script type="text/javascript">
			var saveType="<%=request.getParameter("saveType")%>";
			var cutoverTaskId="<%=request.getParameter("cutoverTaskId")%>";
			var taskStatus="<%=request.getParameter("taskStatus")%>";
    	</script>
  </head>
  
<body>
<div id="div_topo" align="center">
<div id="div_1"  class="frag1"></div>
<div id="div_2"  class="frag2"></div>
<div id="div_3"  class="frag3"></div>
<div id="div_4"  class="frag4"></div>
<div id="div_5"  class="frag5"></div>
<div id="buttonDiv1"  class="buttonDiv1"></div>
<div id="buttonDiv2"  class="buttonDiv2"></div>
<div id="buttonDiv3"  class="buttonDiv3"></div>
<div id="buttonDiv4"  class="buttonDiv4"></div>
<div id="buttonDiv5"  class="buttonDiv5"></div>
<div id="buttonDiv6"  class="buttonDiv6"></div>
<div id="buttonDiv7"  class="buttonDiv7"></div>
<div id="div10"  class="page" align="left"></div>
</div>
</body>
</html>