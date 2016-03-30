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
		  	var manufacturers = 0;
		  	
		  	function getContactList(start){
		  	  var head = "<tr class=\"br-lblue\">"+
			    		   "<th nowrap>序号</th>"+
			    		   "<th nowrap>联系人</th>"+
			    		   "<th nowrap>大客户名称</th>"+
			    		   "<th nowrap>联系电话</th>"+
			    		   "<th nowrap>部门</th>"+
			    		   "<th nowrap>工号</th>"+
			    		   "<th nowrap>邮箱</th>"+
			    		   "<th nowrap>备注</th>"+
			    		   "</tr>";
			  $("tbody").empty();					
			  $("tbody").append(head);
		  	 
		  	  $.post("key-account!getContactInfo.action",
			  {
			    "start":start,
			    "limit":limit
			  },
			  function(data,status){
			    /* alert("Data: " + data.rows[0].CREATE_USER + "\nStatus: " + status);
			    alert(data.rows.length); */
			    
				var tr="";					
				for(var i=0;i<data.rows.length;i++){
					tr = tr + "<tr>" +
							  "<td nowrap>" + (start+i+1) + "</td>" +
							  "<td nowrap>" + data.rows[i].name + "</td>" +
							  "<td nowrap>" + data.rows[i].clientName + "</td>" +
							  "<td nowrap>" + data.rows[i].tel + "</td>" +
							  "<td nowrap>" + data.rows[i].department + "</td>" +
							  "<td nowrap>" + data.rows[i].staffNo + "</td>" +
							  "<td nowrap>" + data.rows[i].email + "</td>" +
							  "<td nowrap>" + data.rows[i].note + "</td>"+
							  "</tr>";
				}
				$("tbody").empty();					
				$("tbody").append(head,tr);
				var divisor = data.total;
				var dividend = limit;
				
				
				manufacturers = Math.ceil(divisor/dividend);
				
				$("#total").html("共&nbsp;"+manufacturers+"&nbsp;页");								
			});
		  	}
		  	getContactList(0);
		  	
		  	$("#next").on("click",function pagingNext(){
		  		var page = $("#current").text();
		  		if(page >= 1 && page < manufacturers){
			  		start = page*limit;
			  		getContactList(start);
			  		page = eval(page+"+"+1);
			  		$("#current").text(page);
		  		}else{
		  			
		  		}
		  	});
		  	
		  	$("#Previous").on("click",function pagingPrevious(){
		  		var page = $("#current").text();		  		
		  		if(page > 1 && page <= manufacturers){
		  			start = (page-2)*limit;
			  		getContactList(start);
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
		  			getContactList(start);
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
			<%-- <div class="items">
				<jsp:include page="leftMenu.jsp"></jsp:include>
			</div> --%>
			<div class="center" style="width:90%;margin-left:15px;">
				
				<div class="large-box">
					<div class="pad">
						<h5><i class="fa fa-table"></i> &nbsp;联系人</h5>
						<table class="table table-bordered">
							<tbody>
							</tbody>
						</table>
					</div>
					<div id="pageTool">
						<div style="text-align:center;width:50%;float:left;">
								<ul class="pagination">
								  <li><a id="Previous" href="#" style="">上一页</a></li>
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
</body>
</html>