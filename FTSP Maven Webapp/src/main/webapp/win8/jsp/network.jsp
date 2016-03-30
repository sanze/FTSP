<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- <html lang="en"> -->
<html>
<head> 
	<meta charset="utf-8">
	<title>大客户</title> 
	<meta name="description" content="">
	<meta name="keywords" content="">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
	<!-- <link rel="stylesheet" type="text/css" media="all" href="../../bootstrap/css/bootstrap.min.css"> -->
	
	<link rel="stylesheet" type="text/css" media="all" href="../css/metro.css">
	<link rel="stylesheet" type="text/css" media="all" href="../css/style.css">
	
	<script src="../js/jquery.min.js"></script>
	<script src="../js/jquery.plugins.min.js"></script>
	<script src="../js/metro.js"></script>
	<script src="../../bootstrap/js/bootstrap.min.js"></script>
	<script>
		$(document).ready(function(){
			
			var start = 0;
		  	var limit = 10;
		  	var clientName;
		  	var critical= "#d32c2c";
			var major= "#f58d00";
			var minor= "#ffc808";
			var warning= "#8B3626";
			var cleared= "#43b51f";
			
			function getCircuits(start,clientName){
		  	  $("tbody").empty();
		  	  $.post("key-account!getCircuitsByVIPName.action",
			  {
			    "start":start,
			    "limit":limit,
	            "clientName":clientName
			  },
			  function(data,status){
			    var head = "<tr class=\"br-lblue\">"+
			    		   "<th nowrap>序号</th>"+
			    		   "<th nowrap>电路名称</th>"+
			    		   "<th nowrap>电路状态</th>"+
			    		   "<th nowrap>性能预警</th>"+
			    		   "<th nowrap>业务等级</th>"+
			    		   "<th nowrap>告警等级</th>"+
			    		   "<th nowrap>业务类型</th>"+
			    		   "<th nowrap>起始机房</th>"+
			    		   "<th nowrap>终止机房</th>"+
			    		   "</tr>";
				var tr="";
				var alarmLevel;	
				var pmAlarm;
				var pmAlarmColor;	
				var circuitStatus;
				var circuitStatusColor;	
				var businessLevel;	
				var ARoom;
				var ZRoom;	
				for(var i=0;i<data.rows.length;i++){					
					switch (data.rows[i].ALARM_LEVEL){
							case 1:
								alarmLevel = "紧急";
								break;
							case 2:
								alarmLevel = "重要";
								break;
							case 3:
								alarmLevel = "次要";
								break;
							case 4:
								alarmLevel = "提示";
								break;
							case 5:
								alarmLevel = "消除";
								break;
							default:
								alarmLevel = "无";
						};
					if(data.rows[i].ALARM_TCA == 0){
						pmAlarm = "正常";
						pmAlarmColor = cleared;
					}else{
						pmAlarm = "越限";
						pmAlarmColor = minor;
					}					
					if(data.rows[i].CIRCUIT_STATUS == 1){
						circuitStatus = "正常";
						circuitStatusColor = cleared;
					}else{
						circuitStatus = "异常";
						circuitStatusColor = minor;
					}
					
					if(typeof(data.rows[i].BUSINESS_LEVEL) == "undefined"){
	  					businessLevel = "";
	  				}
	  				if(typeof(data.rows[i].A_ROOM) == "undefined"){
	  					ARoom = "";
	  				}
	  				if(typeof(data.rows[i].Z_ROOM) == "undefined"){
	  					ZRoom = "";
	  				}
					
					tr = tr + "<tr>" +
							  "<td nowrap>" + (i+1) + "</td>" +
							  "<td nowrap>" + data.rows[i].CIR_NAME + "</td>" +
							  "<td style='background-color:"+circuitStatusColor+";'>" + circuitStatus + "</td>" +
							  "<td style='background-color:"+pmAlarmColor+";'>" + pmAlarm + "</td>" +
							  "<td nowrap>" + businessLevel + "</td>" +
							  "<td nowrap>" + alarmLevel + "</td>" +
							  "<td nowrap>" + data.rows[i].SVC_TYPE + "</td>"+
							  "<td nowrap>" + ARoom + "</td>"+
							  "<td nowrap>" + ZRoom + "</td>"+
							  "</tr>";
				}				
				$("tbody").append(head,tr);	
				var divisor = data.total;
				var dividend = limit;
				var manufacturers = 0;
				var remainder = 0;
				
				manufacturers = Math.ceil(divisor/dividend);
				
				$("#total").html("共&nbsp;"+manufacturers+"&nbsp;页"); 								
			});
		  }
			
			function getVIPAlarmInfo(){
				$.post("key-account!getVIPInfo.action",{"start":0,"limit":0},function(data,status){
				var box = "";
				
				var color = cleared;
				for(var i=0;i<data.rows.length;i++){
					switch (data.rows[i].ALARM_LEVEL){
						case 1:
							color = critical;
							break;
						case 2:
							color = major;
							break;
						case 3:
							color = minor;
							break;
						case 4:
							color = warning;
							break;
						case 5:
							color = cleared;
							break;
						default:
							color = "#00aeef";
					};
					if(data.rows[i].ALARM_COUNT == 0){
						data.rows[i].ALARM_COUNT = "";
					}
					box = box + "<a class=\"box\" href=\"#\" style=\"background:"+ color + ";\">" + 
								data.rows[i].SERVICE_LEVEL +"<span>" + 
								data.rows[i].CLIENT_NAME + "</span>" +
								"<div style=\"margin-top:45px;text-align:right;\">"+
								data.rows[i].ALARM_COUNT + "</div>" + 
								"</a>";
				}
				$(".customer").empty();
				$(".customer").append(box);
				$(".box").each(function(){
					$(this).on("click",function(){
						clientName = $(this).find("span").text();
						getCircuits(0,clientName);
					})
				});
				
			});
			}
			
			$.post("key-account!getVIPInfoWithoutAlarm.action",{"start":0,"limit":0},function(data,status){
				var box = "";
				for(var i=0;i<data.rows.length;i++){
					box = box + "<a class=\"box\" href=\"#\">" + 
								data.rows[i].SERVICE_LEVEL +"<span>" + 
								data.rows[i].CLIENT_NAME + "</span>" +
								"</a>";
				}
				$(".customer").empty();
				$(".customer").append(box);
				getCircuits(0,data.rows[0].CLIENT_NAME);
				$(".box").each(function(){
					$(this).on("click",function(){
						clientName = $(this).find("span").text();						
						getCircuits(0,clientName);
					})
				});
				getVIPAlarmInfo();
			})
		  	
		  	$("#next").on("click",function pagingNext(){
		  		var page = $("#current").text();
		  		if(page >= 1 && page < manufacturers){
			  		start = page*limit;
			  		getCircuits(start,clientName);
			  		page = eval(page+"+"+1);
			  		$("#current").text(page);
		  		}
		  	});
		  	
		  	$("#Previous").on("click",function pagingPrevious(){
		  		var page = $("#current").text();
		  		if(page > 1 && page <= manufacturers){
		  			start = (page-2)*limit;
			  		getCircuits(start,clientName);
			  		page = eval(page+"-"+1);
			  		$("#current").text(page);
		  		}else{
		  			//$("#Previous").parent().atrr()
		  		}
		  	});
		  	
		  	$("#go").on("click",function pagingSelect(){
		  		var page = $("#pages").val();
		  		if(page >= 1 && page <= manufacturers){
		  			start = (page-1)*limit;
		  			$("#current").text(page);
		  			getCircuits(start,clientName);
		  		}
		  	});	
		});
	</script>
</head> 
<body>
	<div class="metro-layout horizontal">
		<div class="header">
			<h1>大客户业务管理系统</h1>
			<div class="controls">
				<!-- <span class="down" title="Scroll down"></span>
				<span class="up" title="Scroll up"></span>
				<span class="next" title="Scroll left"></span> -->
				<a href="mainCenter.jsp"><span class="prev" title="Scroll right"></span></a>
				<!-- <span class="toggle-view" title="Toggle layout"></span> -->
			</div>
		</div>
		<div class="content clearfix">
			<div class="customer">
				<!-- 重要告警 -->
				<!-- <a class="box" href="#" style="background: #f58d00;">
					A
					<span>中国银行</span>
					<div style="margin-top:45px;text-align:right;">B</div>
				</a> -->
				<!-- 清除告警 -->
				<!-- <a class="box" href="#" style="background: #43b51f;">
					B
					<span>公安局</span>
				</a> -->
				<!-- 紧急告警 -->
				<!-- <a class="box" href="#" style="background: #d32c2c;">
					C
					<span>招商银行</span>
				</a> -->
				<!-- 次要告警 -->
				<!-- <a class="box" href="#" style="background: #ffc808;">
					B
					<span>省政府</span>
				</a> -->
				<!-- 提示告警 -->
				<!-- <a class="box" href="#" style="background: #8B3626;">
					B
					<span>工商银行</span>
				</a> -->
			</div>
			<div class="centerNetwork">
				<div class="large-box">
					<h5 style="padding-left:15px;"><i class="fa fa-table"></i> &nbsp;网络监测</h5>
					<div class="pad">
						<!-- <div class="ptable table-responsive"> -->							
						<table class="table table-bordered">
							<tbody>
							</tbody>
						</table>
						<!-- </div> -->
						
					</div>
					<div id="pageTool">
						<div style="text-align:center;width:50%;float:left;">
								<ul class="pagination">
								  <li><a id="Previous" href="#">上一页</a></li>
								  <li><a id="current">1</a></li>
								  <!-- <li><a href="#">&raquo;</a></li> -->
								  <li><a id="next" href="#">下一页</a></li> 
								  <li><a id="total">共1页</a></li>
								</ul>
							
						</div>
						<div class="input-group">
							  <span class="input-group-addon">第</span>
							  <input id="pages" type="text" class="form-control" placeholder="页数">
							  <span class="input-group-addon">页</span>
							  <span class="input-group-addon"><a id="go" href="#">GO</a></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body></html>