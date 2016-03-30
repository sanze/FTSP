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
	<link rel="stylesheet" type="text/css" media="all" href="../css/jquery.jqplot.min.css">
	
	<script src="../js/jquery.min.js"></script>
	<script src="../js/jquery.plugins.min.js"></script>
	<script src="../js/metro.js"></script>
	<!-- <script src="../js/jquery.jqplot.min.js"></script>
	<script type="text/javascript" src="../js/jqplot.pieRenderer.min.js"></script>
	<script type="text/javascript" src="../js/jqplot.donutRenderer.min.js"></script> -->
	<!-- <script src="../../bootstrap/js/bootstrap.min.js"></script> -->
	<!-- 来自百度CDN -->
    <script src="http://s1.bdstatic.com/r/www/cache/ecom/esl/1-6-10/esl.js"></script>
	<!-- <script type="text/javascript">		
		$(document).ready(function() {
			var data = [
			    ['Heavy Industry', 12],['Retail', 9], ['Light Industry', 14], 
			    ['Out of home', 16],['Commuting', 7], ['Orientation', 9]
			  ];
			var plot1 = jQuery.jqplot ('chart', [data], 
			    { 
			      seriesDefaults: {
			        // Make this a pie chart.
			        fill: true, 
			        showMarker: false, 
			        shadow: false,
			        renderer: jQuery.jqplot.PieRenderer, 
			        rendererOptions: {
			            diameter: undefined, 
			            padding: 20,        
			            sliceMargin: 6,     
			            fill:true,     
			            shadow:true,       
			            shadowOffset: 2,    
			            shadowDepth: 5,     
			            shadowAlpha: 0.07  
			        }
			      }, 
			      legend: {
				     show: true,
			         location: 'e', 
			         xoffset: 12,     
			         yoffset: 12,  
			    }
			  })
			});
	</script> -->
	<script text="text/javascript">		
        
        var seriesData;
        var legendData;
        
        function pieGenerate(){
        	// 路径配置
	        require.config({
	            paths:{ 
	                'echarts' : 'http://echarts.baidu.com/build/echarts',
	                'echarts/chart/pie' : 'http://echarts.baidu.com/build/echarts'
	            }
	        });
	         // 使用
	        require(
	            [
	                'echarts',
	                'echarts/chart/pie' 
	            ],
	            function (ec) {
	                var myChart = ec.init(document.getElementById('chart')); 
	                option = {
					    title : {
					        text: '电路统计',
					        subtext: '电路速率',
					        x:'center'
					    },
					    backgroundColor :"#fff",
					    tooltip : {
					        trigger: 'item',
					        formatter: "{a} <br/>{b} : {c} ({d}%)"
					    },
					    legend: {
					        orient : 'vertical',
					        x : 'left',
					        data:legendData
					    },
					    toolbox: {
					        show : true,
					        feature : {
					            mark : {show: true},
					            dataView : {show: true, readOnly: false},
					            restore : {show: true},
					            saveAsImage : {show: true}
					        }
					    },
					    calculable : true,
					    series : [
					        {
					            name:'电路速率',
					            type:'pie',
					            radius : '55%',
					            center: ['50%', '60%'],
					            data:seriesData
					        }
					    ]
					};
					
	                myChart.setOption(option); 
	            }
	        );
	     }
        
        $(document).ready(function(){
        	
        	 var manufacturers = 3;
        	 var head = "<tr class=\"br-lblue\">"+
			    		   "<th>序号</th>"+
			    		   "<th>业务等级</th>"+
			    		   "<th>历史故障数</th>"+
			    		   "<th>新增故障数</th>"+
			    		   "<th>处理故障数</th>"+
			    		   "<th>遗留故障数</th>"+
			    		   "<th>超时故障数</th>"+
			    		   "</tr>";
			 var data1=[{level:"A",history:0,add:1,done:1,leave:0,beyond:0},
			 			{level:"A",history:1,add:1,done:2,leave:0,beyond:1},
			 			{level:"A",history:8,add:4,done:1,leave:6,beyond:0}
			 		];
        	 var data2=[{level:"A",history:5,add:1,done:7,leave:0,beyond:0},
			 			{level:"C",history:0,add:8,done:1,leave:10,beyond:0},
			 			{level:"C",history:3,add:1,done:8,leave:0,beyond:9}
			 		];
			 var data3=[{level:"C",history:6,add:1,done:1,leave:8,beyond:0},
			 			{level:"B",history:9,add:1,done:1,leave:9,beyond:0},
			 			{level:"A",history:0,add:5,done:1,leave:0,beyond:9}
			 		];
			 		
			 function alarmTotalTable(data){
			 	var tr="";
			 	for(var i = 0; i<data.length;i++){
			 		tr = tr + "<tr>" +
							  "<td>" + (i+1) + "</td>" +
							  "<td>" + data[i].level + "</td>" +
							  "<td>" + data[i].history + "</td>" +
							  "<td>" + data[i].add + "</td>" +
							  "<td>" + data[i].done + "</td>"+
							  "<td>" + data[i].leave + "</td>"+
							  "<td>" + data[i].beyond + "</td>"+
							  "</tr>";
			 	}
			 	$("tbody").empty();				
				$("tbody").append(head,tr);	
			 }
			 		
        	alarmTotalTable(data1);
        	
        	$("#next").on("click",function pagingNext(){
		  		/* var page = $("#current").text();
		  		if(page >= 1 && page < manufacturers){
			  		start = page*limit;
			  		getContactList(start);
			  		page = eval(page+"+"+1);
			  		$("#current").text(page);
		  		}else{
		  			
		  		} */
		  	});
		  	
		  	$("#Previous").on("click",function pagingPrevious(){
		  		/* var page = $("#current").text();		  		
		  		if(page > 1 && page <= manufacturers){
		  			start = (page-2)*limit;
			  		getContactList(start);
			  		page = eval(page+"-"+1);
			  		$("#current").text(page);
		  		}else{
		  			//$("#Previous").parent().atrr()
		  		} */
		  	});
		  	
		  	$("#go").on("click",function pagingSelect(){
		  		/* var page = $("#pages").val();
		  		if(page >= 1 && page <= manufacturers){
		  			start = (page-1)*limit;
		  			$("#current").text(page);
		  			getContactList(start);
		  		} */
		  		
		  	});       	
        	
        	function getVIPAlarmInfo(){
				$.post("key-account!getVIPInfo.action",{"start":null,"limit":null},function(data,status){
					var box = "";
					var critical= "#d32c2c";
					var major= "#f58d00";
					var minor= "#ffc808";
					var warning= "#8B3626";
					var cleared= "#43b51f";
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
						var name = data.rows[i].CLIENT_NAME;
						if(data.rows[i].ALARM_COUNT == 0){
							data.rows[i].ALARM_COUNT = "";
						}
						/* $(".box") */
						box = box + "<a class=\"box\" href=\"#\" style=\"background:"+ color + ";\">" + 
									data.rows[i].SERVICE_LEVEL +"<span>" + 
									data.rows[i].CLIENT_NAME + "</span>" +
									"<div style=\"margin-top:45px;text-align:right;\">"+
									data.rows[i].ALARM_COUNT + "</div>" + 
									"</a>";
					}
					$(".customer").empty(box);
					$(".customer").append(box);
					$(".box").each(function(){
						$(this).on("click",function(){
							clientName = $(this).find("span").text();
							$("#customerName").html("客户名称&nbsp;:&nbsp;" + clientName);
							getCircuitsGroupCount(clientName);
						})
					});
					
				});
			}
			
			function getCircuitsGroupCount(clientName){
				$.post("key-account!getGroupedCircuitsByVIPName.action",
	        		{
	        			"clientName":clientName
	        		},
	        		function(data,status){
		        		seriesData = new Array();
        				legendData = new Array();
		        		for(var i = 0; i<data.rows.length;i++){
		        			var item = {value:data.rows[i].NUM,name:data.rows[i].RATE};
		        			seriesData.push(item);
		        			legendData.push(data.rows[i].RATE);
		        		}
		        		/* $("#chart").empty(); */
		        		pieGenerate();
        	    })
			}
        	
        	$.post("key-account!getVIPInfoWithoutAlarm.action",{"start":null,"limit":null},function(data,status){
				var box = "";
				for(var i=0;i<data.rows.length;i++){
					box = box + "<a class=\"box\" href=\"#\">" + 
								data.rows[i].SERVICE_LEVEL +"<span>" + 
								data.rows[i].CLIENT_NAME + "</span>" +
								"</a>";
				}
				$(".customer").empty();
				$(".customer").append(box);
				clientName = data.rows[0].CLIENT_NAME;
				getCircuitsGroupCount(clientName);
				$("#customerName").html("客户名称&nbsp;:&nbsp;" + clientName);
				$(".box").each(function(){
					$(this).on("click",function(){
						clientName = $(this).find("span").text();
						$("#customerName").html("客户名称&nbsp;:&nbsp;" + clientName);
						getCircuitsGroupCount(clientName);						
					})
				});
				getVIPAlarmInfo();
			})
			
        })
        
        
        
        
	</script>
	
</head> 
<body>
	<div class="metro-layout horizontal">
		<div class="header">
			<h1>大客户业务管理系统</h1>
			<div class="controls">
				<a href="mainCenter.jsp"><span class="prev" title="Scroll right"></span></a>
			</div>
		</div>
		<div class="content clearfix">
			<div class="customer">
				<!-- <a class="box" href="#">
					<span>中国银行</span>
				</a>
				<a class="box" href="#" style="background: #43b51f;">
					<span>公安局</span>
				</a>
				<a class="box" href="#" style="background: #3c5b9b;">
					<span>招商银行</span>
				</a>
				<a class="box" href="#" style="background: #ffc808;">
					<span>省政府</span>
				</a>
				<a class="box" href="#" style="background: #f874a4;">
					<span>工商银行</span>
				</a> -->
			</div>
			<div class="centerReport">
				
				<div class="topoFlex">
					<h5 style="padding-left:12px;padding-top:15px;color:#31708f;"><i class="fa fa-table"></i> &nbsp;服务报告</h5>
					<div id="panel-23">
		              
		              <div class="alert alert-info" role="alert">
		              	<span class="label label-primary" id="customerName"></span>
		              </div>
	                </div>
		            <div id="chart" style="height: 400px; width: 850px; position: relative;background-color:#fff;"><!--  class="jqplot-target" -->
		            	
		            </div>
	            </div>
				
				<div class="large-box">
					<h5 style="padding-left:15px;"><i class="fa fa-table"></i> &nbsp;告警统计</h5>
					<div class="pad">
						<!-- <div class="ptable table-responsive"> -->							
						<table class="table table-bordered" style="width:800px;">
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