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
		$(document).ready(function getTableData(){
		  	var limit = 7;
		  	var start = 0;
		  	function getCutoverInfoByVIPName(){
		  		var array;
		  		var arrayTime;
		  		var arrayDate;
		  		var arrayMin;
		  		var content="";
	  			var startTime;
	  			var year;
	  			var month;
	  			var day;
	  			var hour;
	  			var min;
		  		
		  		$.post("key-account!getCutoverInfoByVIPName.action",{},function(data,status){
		  			
		  			for(var i = 0;i<data.rows.length;i++){
		  				array = new Array();
		  				arrayTime = new Array();
		  				array = data.rows[i].clientNamePlusCutoverName.split(",");
		  				arrayTime = data.rows[i].startTime.split(" ");
		  				arrayDate = arrayTime[0].split("-");
		  				arrayMin = arrayTime[1].split(":");
		  				var str1 = "<p>" + array[0] + "&nbsp;:&nbsp;" + arrayDate[0] + "年" + arrayDate[1] + "月" + 
		  									arrayDate[2] + "日" + arrayMin[0] + "时" + arrayMin[1] +"分,"+
		  									array[1] + "," ;
		  				var str3 = "中断时间预计为"+ data.rows[i].timeDifference +"小时。</p><br>";
		  				var str2 = "";
		  				if(typeof(data.rows[i].sdhCount) != "undefined"){
		  					str2 = str2 + "影响SDH电路" + data.rows[i].sdhCount +
		  									"条,";
		  				}
		  				if(typeof(data.rows[i].ethCount) != "undefined"){
		  					str2 = str2 + "影响ETH电路" + data.rows[i].ethCount +
		  									"条,";
		  				}
		  				if(typeof(data.rows[i].otnCount) != "undefined"){
		  					str2 = str2 + "影响OTN电路" + data.rows[i].otnCount +
		  									"条,";
		  				}
		  				content = content + str1 + str2 + str3;
		  			}
		  			$(".note").empty(); 
		  			$(".note").append(content);
		  		})
		  	
		  	}
		  	getCutoverInfoByVIPName();
		  	
		  	var head = "<tr class=\"br-lblue\">"+
			    		   "<th nowrap>序号</th>"+
			    		   "<th nowrap>客户名称</th>"+
			    		   "<th nowrap>项目名称</th>"+
			    		   "<th nowrap>项目编号</th>"+
			    		   "<th nowrap>项目类型</th>"+
			    		   "<th nowrap>状态</th>"+
			    		   "<th nowrap>超时倒计时</th>"+
			    		   "<th nowrap>责任人</th>"+
			    		   "<th nowrap>操作</th>"+
			    		   "<th nowrap>备注</th>"+
			    		   "</tr>";
			    		   
			 var data1=[{customerName:"省政府",itemName:"1号至5号楼100M光路专线",itemNo:"20140316008",
			 			 itemType:"工单",status:"处理中",beyond:"20小时50分钟",principal:"王文龙/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"市一中",itemName:"光路倒换",itemNo:"20140316009",
			 			 itemType:"故障",status:"处理中",beyond:"1天50分钟",principal:"刘飞/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"中国银行",itemName:"2014年3月服务报告",itemNo:"20140316010",
			 			 itemType:"服务报告",status:"等待处理",beyond:"22小时",principal:"王鹏/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			 {customerName:"省政府",itemName:"1号至5号楼100M光路专线",itemNo:"20140316008",
			 			 itemType:"工单",status:"处理中",beyond:"20小时50分钟",principal:"王文龙/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"市一中",itemName:"光路倒换",itemNo:"20140316009",
			 			 itemType:"故障",status:"处理中",beyond:"1天50分钟",principal:"刘飞/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"中国银行",itemName:"2014年3月服务报告",itemNo:"20140316010",
			 			 itemType:"服务报告",status:"等待处理",beyond:"22小时",principal:"王鹏/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			 {customerName:"省政府",itemName:"1号至5号楼100M光路专线",itemNo:"20140316008",
			 			 itemType:"工单",status:"处理中",beyond:"20小时50分钟",principal:"王文龙/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"市一中",itemName:"光路倒换",itemNo:"20140316009",
			 			 itemType:"故障",status:"处理中",beyond:"1天50分钟",principal:"刘飞/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"中国银行",itemName:"2014年3月服务报告",itemNo:"20140316010",
			 			 itemType:"服务报告",status:"等待处理",beyond:"22小时",principal:"王鹏/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"}
			 		   ];
        	 var data2=[{customerName:"省政府",itemName:"1号至5号楼100M光路专线",itemNo:"20140316008",
			 			 itemType:"工单",status:"处理中",beyond:"20小时50分钟",principal:"王文龙/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"市一中",itemName:"光路倒换",itemNo:"20140316009",
			 			 itemType:"故障",status:"处理中",beyond:"1天50分钟",principal:"刘飞/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"中国银行",itemName:"2014年3月服务报告",itemNo:"20140316010",
			 			 itemType:"服务报告",status:"等待处理",beyond:"22小时",principal:"王鹏/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"}
			 		   ];
			 var data3=[{customerName:"省政府",itemName:"1号至5号楼100M光路专线",itemNo:"20140316008",
			 			 itemType:"工单",status:"处理中",beyond:"20小时50分钟",principal:"王文龙/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"市一中",itemName:"光路倒换",itemNo:"20140316009",
			 			 itemType:"故障",status:"处理中",beyond:"1天50分钟",principal:"刘飞/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"},
			 			{customerName:"中国银行",itemName:"2014年3月服务报告",itemNo:"20140316010",
			 			 itemType:"服务报告",status:"等待处理",beyond:"22小时",principal:"王鹏/18205142429",
			 			 operation:"查看  转派 挂单",note:"暂无备注"}
			 		   ];
			 		
			 function getTimeoutRemind(data){
			 	var tr="";
			 	for(var i = 0; i<data.length;i++){
			 		tr = tr + "<tr>" +
							  "<td>" + (i+1) + "</td>" +
							  "<td nowrap>" + data[i].customerName + "</td>" +
							  "<td nowrap>" + data[i].itemName + "</td>" +
							  "<td nowrap>" + data[i].itemNo + "</td>" +
							  "<td nowrap>" + data[i].itemType + "</td>" +
							  "<td nowrap>" + data[i].status + "</td>" +
							  "<td nowrap>" + data[i].beyond + "</td>" +
							  "<td nowrap>" + data[i].principal + "</td>" +
							  "<td nowrap>" + data[i].operation + "</td>" +
							  "<td nowrap>" + data[i].note + "</td>" +
							  "</tr>";
			 	}
			 	$("tbody").empty();				
				$("tbody").append(head,tr);	
				
				$("#total").html("共&nbsp;1&nbsp;页");
			 }
			 		
        	getTimeoutRemind(data1);
		  	/* function getTimeoutRemind(start){
		  	$.post("inspect-report!getInspectReportList.action",
			  {
			  	"start":start,
			    "limit":limit,
	            "inspectTime":"一年内",
	            "userId":-1
			  },
			  function(data,status){
			    var head = "<tr class=\"br-lblue\">"+
			    		   "<th nowrap>序号</th>"+
			    		   "<th nowrap>客户名称</th>"+
			    		   "<th nowrap>项目名称</th>"+
			    		   "<th nowrap>项目编号</th>"+
			    		   "<th nowrap>项目类型</th>"+
			    		   "<th nowrap>状态</th>"+
			    		   "<th nowrap>超时倒计时</th>"+
			    		   "<th nowrap>责任人</th>"+
			    		   "<th nowrap>操作</th>"+
			    		   "<th nowrap>备注</th>"+
			    		   "</tr>";
				var tr="";					
				for(var i=0;i<data.rows.length;i++){
					tr = tr + "<tr>" +
							  "<td>" + (i+1) + "</td>" +
							  "<td nowrap>" + data.rows[i].INSPECT_REPORT_ID + "</td>" +
							  "<td nowrap>" + data.rows[i].REPORT_NAME + "</td>" +
							  "<td nowrap>" + data.rows[i].CREATE_TIME + "</td>" +
							  "<td nowrap>" + data.rows[i].RESULT + "</td>" +
							  "<td nowrap>" + data.rows[i].NOTE + "</td>" +
							  "<td nowrap>" + data.rows[i].CREATE_USER + "</td>" +
							  "<td nowrap>" + data.rows[i].CREATE_USER + "</td>" +
							  "<td nowrap>" + data.rows[i].CREATE_USER + "</td>" +
							  "<td nowrap>" + data.rows[i].CREATE_USER + "</td>" +
							  "</tr>";
				}
				$("tbody").empty();						
				$("tbody").append(head,tr);
				var divisor = data.total;
				var dividend = limit;
				var manufacturers = 0;
				var remainder = 0;
				
				manufacturers = Math.ceil(divisor/dividend);
				
				$("#total").html("共&nbsp;"+manufacturers+"&nbsp;页"); 	
				
										
			});
			}
			
			getTimeoutRemind(0); */
			
			$("#next").click(function pagingNext(){
		  		/* var page = $("#current").text();
		  		start = page*limit;
		  		getTimeoutRemind(start);
		  		page = eval(page+"+"+1);
		  		$("#current").text(page); */
		  	});
		  	
		  	$("#Previous").click(function pagingPrevious(){
		  		/* var page = $("#current").text();
		  		start = (page-2)*limit;
		  		getTimeoutRemind(start);
		  		page = eval(page+"-"+1);
		  		$("#current").text(page); */
		  	});
		  	
		  	$("#go").click(function pagingSelect(){
		  		/* var page = $("#pages").val();
		  		start = (page-1)*limit;
		  		getTimeoutRemind(start); */
		  	});
			
		});
		
	</script>
</head> 
<body>
	<div class="metro-layout horizontal">
		<div class="header">
			<h1>大客户业务管理系统</h1>
		    <a href="mainContent.jsp"><span class="prev" title="Scroll right"></span></a>
			<div class="controls">
				<!-- <span class="down" title="Scroll down"></span>
				<span class="up" title="Scroll up"></span>
				<span class="next" title="Scroll left"></span> -->
				<a href="mainContent.jsp"><span class="prev" title="Scroll right"></span></a>
				<!-- <span class="toggle-view" title="Toggle layout"></span> -->
			</div>
		</div>
		<div class="content clearfix">
			<div class="items">
				<jsp:include page="leftMenu.jsp"></jsp:include>
			</div>
			<div class="center">
				<div class="team">
				<!-- <h4>业务提醒:</h4>
				  <p>省政府：4月1日0点30分，市话网环2光缆割接，影响SDH电路5条，中断时间预计为3个小时。</p>
				  <br>
				  <p>中国银行：4月2日0点30分，网络优化，影响ETH电路5条，中断时间预计为2个小时。</p> -->
				  <!-- <p><a class="btn btn-primary btn-lg">Learn more</a></p> -->
				  <h4>业务提醒:</h4><div class="note"></div>
				</div>
				<div class="large-box">
					<h5 style="padding-left:15px;"><i class="fa fa-table"></i> &nbsp;超时提醒汇总</h5>
					<div class="pad">
						<table class="table table-bordered">
							<tbody>
							</tbody>
						</table>
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
				<!-- <div class="jumbotron">
				  <h4>业务提醒:</h4>
				  <p>This is a simple hero unit, a simple jumbotron-style component for calling extra attention to featured content or information.</p>
				  <p><a class="btn btn-primary btn-lg">Learn more</a></p>
				</div> -->
				<!-- <div class="panel panel-primary">
				<div class="gridTitle">
				<div class="panel-heading">
				超时提醒汇总:
				</div>
				<div class="gridContent">
					<table class="table">
					   <caption>基本的表格布局</caption>
					   <thead>
					      <tr>
					         <th>客户名称</th>
					         <th>项目名称</th>
					         <th>项目编号</th>
					         <th>项目类型</th>
					         <th>状态</th>
					         <th>超时倒计时</th>
					      </tr>
					   </thead>
					   <tbody>
					      <tr>
					         <td>Tanmay</td>
					         <td>Bangalore</td>
					      </tr>
					      <tr>
					         <td>Sachin</td>
					         <td>Mumbai</td>
					      </tr>
					   </tbody>
					</table>
				</div>
				<div class="pageTool">
				<ul class="pagination">
				  <li class="disabled"><a href="#">«</a></li>
				  <li class="active"><a href="#">1</a></li>
				  <li><a href="#">2</a></li>
				  <li><a href="#">3</a></li>
				  <li><a href="#">4</a></li>
				  <li><a href="#">5</a></li>
				  <li><a href="#">»</a></li>
				</ul>
				</div>
			</div> -->
			</div>
		</div>
	</div>


</body></html>