<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script>
		$(document).ready(function(){
			$("#page").fadeIn("slow");
		  	$("#net").fadeIn("slow");
		  	$("#business").fadeIn("slow");
		  	$("#report").fadeIn("slow");
		  	$("#contactor").fadeIn("slow");

		})
</script>
<a id="page" class="box" href="mainCenter.jsp" style="display:none;">
	<span>主页</span>
	<img class="icon" src="../images/mail.png" alt="">
</a>
<a id="net" class="box" href="network.jsp" style="background: #43b51f;display:none;">
	<span>网络监测</span>
	<img class="icon" src="../images/phone.png" alt="">
</a>
<a id="business" class="box" href="business.jsp" style="background: #3c5b9b;display:none;">
	<span>业务管理</span>
	<img class="icon" src="../images/facebook.png" alt="">
</a>
<a id="report" class="box" href="report.jsp" style="background: #ffc808;display:none;">
	<span>服务报告</span>
	<img class="icon" src="../images/winamp.png" alt="">
</a>
<a id="contactor" class="box" href="contactor.jsp" style="background: #f874a4;display:none;">
	<span>联系人</span>
	<img class="icon" src="../images/dribbble.png" alt="">
</a>