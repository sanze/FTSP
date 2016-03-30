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
	
	<link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
		<script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base-debug.js"></script>
		<script type="text/javascript" src="../../resource/ext/ext-all-debug.js"></script>
	<script type="text/javascript" src="../../jsp/viewManager/Flex.js"></script>
	<script src="../js/jquery.min.js"></script>
	<script src="../js/jquery.plugins.min.js"></script>
	<script src="../js/metro.js"></script>
	<script src="../../bootstrap/js/bootstrap.min.js"></script>
	
	<!-- <script type="text/javascript" src="../js/business.js"></script> -->
	<!-- 来自百度CDN -->
    <script src="http://s1.bdstatic.com/r/www/cache/ecom/esl/1-6-10/esl.js"></script>
	<script type="text/javascript">
		var nodes = [
		                {
		                    category:0, name: '网元1(Optix)紧急告警:0',
		                    /* symbol: 'image://../images/ne.jpg',
		                    symbolSize: 40, */
		                    draggable: true 
		                },
		                {   category:0, name: '网元2',
		                	/* symbol: 'image://../images/ne.jpg',
		                    symbolSize: 40, */
		                    draggable: true
		                },
		                {   category:0, name: '网元3',
		                	/* symbol: 'image://../images/ne.jpg',
		                    symbolSize: 40, */
		                    draggable: true
		                },
		                {   category:0, name: '网元4',
		                	/* symbol: 'image://../images/ne.jpg',
		                    symbolSize: 40, */
		                    draggable: true
		                },
		                {   category:0, name: '网元5',
		                	/* symbol: 'image://../images/ne.jpg',
		                    symbolSize: 40, */
		                    draggable: true
		                },
		                {   category:0, name: '网元6',
		                	/* symbol: 'image://../images/ne.jpg',
		                    symbolSize: 40, */
		                    draggable: true
		                },
		                {   category:0, name: '网元7',
		                	/* symbol: 'image://../images/ne.jpg',
		                    symbolSize: 40, */
		                    draggable: true
		                }
				            ];
	    var links = [
		                {source : '网元2', target : '网元1', weight : 1,itemStyle: {
		                    normal: {
		                        lineWidth: 7
		                    }
		                }},
		                {source : '网元3', target : '网元2', weight : 1,itemStyle: {
		                    normal: {
		                        lineWidth: 7
		                    }
		                }}
		            ];
		
		function topoGenerate(){
			// 路径配置
	        require.config({
	            paths:{ 
	                'echarts' : 'http://echarts.baidu.com/build/echarts',
	                'echarts/chart/force' : 'http://echarts.baidu.com/build/echarts'
	            }
	        }); 
	        // 使用
	        require(
	        	[
	         		'echarts',
	                'echarts/chart/force'
	        	],
	        	function(ec){
	        	var myChart = ec.init(document.getElementById('topo'));
	        	myChart.showLoading({
				    text: '正在努力的读取数据中...',    //loading话术
				});
	        	
	        	var option = {
				    title : {
				        text: '电路拓扑图 ',
				        subtext: '',
				        x:'right',
				        y:'bottom'
				    },
				    backgroundColor :"#fff",
				    tooltip : {
				        trigger: 'item',
				        formatter: '{a} : {b}'
				    },
				    toolbox: {
				        show : true,
				        feature : {
				            restore : {show: true},
				            saveAsImage : {show: true}
				        }
				    },
				    legend: {
				        x: 'left',
				        data:[]
				    },
				    series : [
				        {
				            type:'force',
				            name : "电路拓扑",
				            categories : [
				                {
				                    name: '网元',
				                    symbol: 'image://../images/ne3.png',
				                    symbolSize: 40
				                }
				            ],
				            itemStyle: {
				                normal: {
				                    label: {
				                        show: false,
				                        textStyle: {
				                            color: '#333',
				                            align: 'left',
				                            baseline: 'bottom'
				                        },
				                        label: {
				                                show: false,
				                                position:'outer'
				                            }
				                    },
				                    nodeStyle : {
				                        brushType : 'both',
				                        strokeColor : 'rgba(255,215,0,0.4)',
				                        lineWidth : 1
				                    }
				                },
				                emphasis: {
				                    label: {
				                        show: false
				                        // textStyle: null      // 默认使用全局文本样式，详见TEXTSTYLE
				                    },
				                    nodeStyle : {
				                        //r: 30
				                    },
				                    linkStyle : {}
				                }
				            },
				            minRadius : 15,
				            maxRadius : 25,
				            gravity: 1.1,
				            scaling: 1.2,
				            draggable: false,
				            linkSymbol: 'arrow',
				            steps: 10,
				            coolDown: 0.9,
				            nodes: nodes,
				            links : links
				        }
			    	]
				};
				myChart.setOption(option);
				myChart.hideLoading();
	        })
		
		}

		/* var ecConfig = require('echarts/config');
		function focus(param) {
		    var data = param.data;
		    var links = option.series[0].links;
		    var nodes = option.series[0].nodes;
		    if (
		        data.source !== undefined
		        && data.target !== undefined
		    ) { //点击的是边
		        var sourceNode = nodes[data.source];
		        var targetNode = nodes[data.target];
		        console.log("选中了边 " + sourceNode.name + ' -> ' + targetNode.name + ' (' + data.weight + ')');
		    } else { // 点击的是点
		        console.log("选中了" + data.name + '(' + data.value + ')');
		    }
		    console.log(param);
		}
		myChart.on(ecConfig.EVENT.CLICK, focus); */
		 
		
		
		$(document).ready(function(){
		  	
		  	var limit = 7;
		  	var start = 0;
		  	var manufacturers = 0;
		  	var critical= "#d32c2c";
			var major= "#f58d00";
			var minor= "#ffc808";
			var warning= "#8B3626";
			var cleared= "#43b51f";
			var color;
			var clientName;
			var circuitsName;
			var circuitsArray;
			var circuitsString;
			var circuitsNo;
			var circuitsId;
			var circuitsType;
			var domain;

			//EXT电路拓扑图引用
			/* var topoDisplayUrl='circuit!getRouteTopo.action';
			var href = location.href;
			var index = href.indexOf("win8");
			var preUrl = href.substr(0, index);
			Ext.Flex.APA_URL = preUrl + "jsp/viewManager/APA.swf";
			var canvasPanel = new Ext.Panel({
				id : 'canvasPanel',
				height : 300,
				width:'80%',
				renderTo:'topo',
				
				items : [ {
					xtype : "flex",
					id : "flex",
					type : "apa"
				} ]
			}); */			
			function FlexInitializeExt(vCircuit){
				 /* Ext.getCmp("flex").on("initialize", function() {  */
					var jsonDataTopo1 = {
							"vCircuit" : vCircuit
						};
						Ext.Ajax.request({
							url : topoDisplayUrl,
							type : 'post',
							params : jsonDataTopo1,
							success : function(response) {
								var obj = Ext.decode(response.responseText);
								/*alert(obj);*/
								Ext.getCmp('flex').loadData(obj);
							},
							error : function(response) {
								Ext.Msg.alert("错误", response.responseText);
							},
							failure : function(response) {
								Ext.Msg.alert("错误", response.responseText);
							}
						});
				 /* }); */ 
				
			}
			
			function FlexInitializeEchart(vCircuit){
				$.post("circuit!getRouteTopo.action",{"vCircuit" : vCircuit},function(data,status){
					nodes = new Array();
					links = new Array();
					var map = new Object();
					var nodeOrLine;
					for(var i=0;i<data.rows.length;i++){
						nodeOrLine = data.rows[i].nodeOrLine;
						if(nodeOrLine=="node"){
							var node = {category:0, index:data.rows[i].nodeId,name:data.rows[i].displayName+data.rows[i].productName,draggable: true };
							map[data.rows[i].nodeId] = data.rows[i].displayName+data.rows[i].productName;
							nodes.push(node);
						}else{
							var fromNode = map[data.rows[i].fromNode];
							var toNode= map[data.rows[i].toNode];
							var line = {source:fromNode, target:toNode,weight:1,itemStyle:{normal:{lineWidth:7}}};
							links.push(line);
						}
					}
					topoGenerate();
				})
			}
			
			function getCircuits(clientName){
		  	  $.post("key-account!getCircuitsByVIPName.action",
				  {
				    "start":0,
				    "limit":"-1",
		            "clientName":clientName
				  },
				  function(data,status){
				    var circuits = "";
				    color = cleared;
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
								color = "#ea66a6;";
						};
						circuits = circuits + "<a class=\"box\" href=\"#\" style=\"background:"+ color + ";\">" + 
												"<span id=\""+ data.rows[i].CIR_NO+"_"+data.rows[i].CIR_CIRCUIT_INFO_ID+"_"+data.rows[i].CIRCUIT_TYPE +
												"\">" + 
												data.rows[i].CIR_NO + "_" +
												data.rows[i].CIR_NAME + "</span></a>";
				    }
				    $(".linkDiv").empty();
				    $(".linkDiv").append(circuits);
				    circuitsName = data.rows[0].CIR_NO+"_"+data.rows[0].CIR_NAME;
				    /* $("#circuitsName").html("电路名称&nbsp;:&nbsp;" + circuitsName); */
					$(".linkDiv").children().each(function(){
						$(this).on("click",function(){
							circuitsName = $(this).find("span").text();
						    $("#circuitsName").html("电路名称&nbsp;:&nbsp;" + circuitsName);
						    circuitsArray = new Array();
						    circuitsString = $(this).find("span").attr("id");
						    circuitsArray = circuitsString.split("_");
						    circuitsNo = circuitsArray[0];
						    circuitsId = circuitsArray[1];
						    circuitsType = circuitsArray[2];						    
						    FlexInitializeEchart(circuitsNo);
						    getAlarmByCircuit(circuitsId,circuitsType,start);
						})
					});							
				});
		  	}
		  	
		  	function getVIPAlarmInfo(){
		  		$.post("key-account!getVIPInfo.action",{"start":null,"limit":null},function(data,status){
					var box = "";				
					color = cleared;
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
									data.rows[i].SERVICE_LEVEL +
									"<span>" + data.rows[i].CLIENT_NAME + "</span>" +
									"<div style=\"margin-top:45px;text-align:right;\">"+
									data.rows[i].ALARM_COUNT + "</div>" + 
									"</a>";
					}
					$(".customer").empty();
					$(".customer").append(box);
					$(".customer").children().each(function(){
						$(this).on("click",function(){
							clientName = $(this).find("span").text();
							$("#customerName").html("客户名称&nbsp;:&nbsp;" + clientName);
							getCircuitInfoWithoutAlarm(clientName);
						})
					});
				});
		  	}
		  	
		  	function getCircuitInfoWithoutAlarm(clientName){
		  		$.post("key-account!getCircuitsByVIPNameWithoutAlarmInfo.action",
				  {
				    "start":0,
				    "limit":"-1",
		            "clientName":clientName
				  },
				  function(data,status){
				    var circuits = "";
				    for(var i=0;i<data.rows.length;i++){
						circuits = circuits + "<a class=\"box\" href=\"#\" style=\"background:#ea66a6;\">" + 
												"<span id=\""+ data.rows[i].CIR_NO+"_"+data.rows[i].CIR_CIRCUIT_INFO_ID+"_"+data.rows[i].CIRCUIT_TYPE +
												"\">"  + data.rows[i].CIR_NO + "_" +
												data.rows[i].CIR_NAME + "</span></a>";
				    }
				    $(".linkDiv").empty();
				    $(".linkDiv").append(circuits);
				    circuitsName = data.rows[0].CIR_NO+"_"+data.rows[0].CIR_NAME;
				    $("#circuitsName").html("电路名称&nbsp;:&nbsp;" + circuitsName);
				    //加载拓扑图
				    FlexInitializeEchart(data.rows[0].CIR_NO);
				    /* FlexInitializeExt(data.rows[0].CIR_NO); */
				    //加载电路告警列表
				    getAlarmByCircuit(data.rows[0].CIR_CIRCUIT_INFO_ID,data.rows[0].CIRCUIT_TYPE,start);
					//加载告警信息
					getCircuits(clientName);
					$(".linkDiv").children().each(function(){
						$(this).on("click",function(){
							circuitsName = $(this).find("span").text();
						    $("#circuitsName").html("电路名称&nbsp;:&nbsp;" + circuitsName);
						    circuitsArray = new Array();
						    circuitsString = $(this).find("span").attr("id");
						    circuitsArray = circuitsString.split("_");
						    circuitsNo = circuitsArray[0];
						    circuitsId = circuitsArray[1];
						    circuitsType = circuitsArray[2];						    
						    FlexInitializeEchart(circuitsNo);
						    getAlarmByCircuit(circuitsId,circuitsType,start);
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
				clientName = data.rows[0].CLIENT_NAME;
				$("#customerName").html("客户名称&nbsp;:&nbsp;" + clientName);
				//加载电路名称
				getCircuitInfoWithoutAlarm(clientName);
				//加载告警信息
				getVIPAlarmInfo();
				$(".box").each(function(){
					$(this).on("click",function(){
						clientName = $(this).find("span").text();
						$("#customerName").html("客户名称&nbsp;:&nbsp;" + clientName);
						getCircuitInfoWithoutAlarm(clientName);
					})
				});
			})
			
			/* FlexInitialize(); */
		  	
		  	function getAlarmByCircuit(circuitsId,circuitsType,start){
		  	    var head = "<tr class=\"br-lblue\">"+
			    		   "<th nowrap>序号</th>"+
			    		   "<th nowrap>告警名称</th>"+
			    		   "<th nowrap>网元名</th>"+
			    		   "<th nowrap>网元类型</th>"+
			    		   "<th nowrap>业务类型</th>"+
			    		   "<th nowrap>端口类型</th>"+
			    		   "<th nowrap>槽道</th>"+
			    		   "<th nowrap>端口</th>"+
			    		   "<th nowrap>通道</th>"+
			    		   "<th nowrap>首次告警时间</th>"+
			    		   "<th nowrap>告警时间</th>"+
			    		   "</tr>";
			  	$("tbody").empty();					
				$("tbody").append(head);
			  	$.post("key-account!getAlarmByCircuit.action",
				  {
				  	"start":start,
				    "limit":limit,
		            "circuitInfoId":circuitsId,
		            "circuitType":circuitsType
				  },
				  function(data,status){
				   
					var tr="";					
					for(var i=0;i<data.rows.length;i++){
						var trCir_Status = "<td style='background-color:#ffc808;'>" + data.rows[i].RESULT + "</td>" ;
						var trPM_Alarm = "<td style='background-color:#43b51f;'>" + data.rows[i].RESULT + "</td>" ;
						if(data.rows[i].DOMAIN == 1){
							domain = "SDH";
						}else if(data.rows[i].DOMAIN == 2){
							domain = "ETH";
						}else if(data.rows[i].DOMAIN == 3){
							domain = "OTN";
						}
						tr = tr + "<tr>" +
								  "<td nowrap>" + (i+1) + "</td>" +
								  "<td nowrap style='background-color:#ffc808;'>" + data.rows[i].ALARM_REASON + "</td>" +
								  "<td nowrap>" + data.rows[i].NE_NAME + "</td>" +
								  "<td nowrap>" + data.rows[i].PRODUCT_NAME + "</td>" +
								  "<td nowrap>" + domain + "</td>" +
								  "<td nowrap>" + data.rows[i].PTP_TYPE + "</td>" +
								  "<td nowrap>" + data.rows[i].SLOT_DISPLAY_NAME + "</td>"+
								  "<td nowrap>" + data.rows[i].PORT_NO + "</td>"+
								  "<td nowrap>" + data.rows[i].CTP_NAME + "</td>"+
								  "<td nowrap>" + data.rows[i].FIRST_TIME + "</td>"+
								  "<td nowrap>" + data.rows[i].FIRST_TIME + "</td>"+
								  "</tr>";
					}
					$("tbody").empty();					
					$("tbody").append(head,tr);	
					
					var divisor = data.total;
					var dividend = limit;
					var remainder = 0;
					
					manufacturers = Math.ceil(divisor/dividend);
					
					$("#total").html("共&nbsp;"+manufacturers+"&nbsp;页"); 								
				});
		  	}
		  	
		  	$("#next").on("click",function pagingNext(){
		  		var page = $("#current").text();
		  		if(page >= 1 && page < manufacturers){
			  		start = page*limit;
			  		getAlarmByCircuit(circuitsId,circuitsType,start);
			  		page = eval(page+"+"+1);
			  		$("#current").text(page);
		  		}else{
		  			
		  		}
		  	});
		  	
		  	$("#Previous").on("click",function pagingPrevious(){
		  		var page = $("#current").text();		  		
		  		if(page > 1 && page <= manufacturers){
		  			start = (page-2)*limit;
			  		getAlarmByCircuit(circuitsId,circuitsType,start);
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
		  			getAlarmByCircuit(circuitsId,circuitsType,start);
		  		}
		  		
		  	});
		  	
		  	
		});
	</script>
</head> 
<body>
	<div class="metro-layout horizontal">
		<div class="header">
			<h1>大客户业务管理系统</h1>
			<div di="controls" class="controls">
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
			<div class="linkDiv">
				<!-- <a class="box" href="#" style="background: #3c5b9b;">
					<span>LINK1</span>
				</a>
				<a class="box" href="#" style="background: #ffc808;">
					<span>LINK2</span>
				</a>
				<a class="box" href="#" style="background: #d32c2c;">
					<span>LINK3</span>
				</a>
				<a class="box" href="#" style="background: #ffc808;">
					<span>LINK4</span>
				</a>
				<a class="box" href="#" style="background: #43b51f;">
					<span>LINK5</span>
				</a> -->
			</div>
			<div class="centerBusiness">
				<div class="topoFlex">
					<h5 style="padding-left:15px;padding-top:15px;color:#31708f;"><i class="fa fa-table"></i> &nbsp;业务监控</h5>
					<div id="panel-23">
		              
		              <div class="alert alert-info" role="alert">
		              	<span id="customerName" class="label label-primary">客户名称 &nbsp;:</span>
		              	<span id="circuitsName" class="label label-primary" style="margin-left:50px;">电路名称&nbsp;:</span>
		              </div>
	                </div>
	                <div id="topo" class="content" style="height: 600px; width: 850px; position: relative;background-color:#fff;">
		            </div>
	            </div>
			  
				
				<div class="large-box">
					<!-- <h5 style="padding-left:15px;"><i class="fa fa-table"></i> &nbsp;Pricing Table</h5> -->
					<div class="pad">
						<table class="table table-bordered">
							<tbody>
							</tbody>
						</table>
					</div>
					<div id="pageTool">
						<div style="text-align:center;width:50%;float:left;">
								<ul class="pagination">
								  <li><a href="#">上一页</a></li>
								  <li><a>1</a></li>
								  <!-- <li><a href="#">&raquo;</a></li> -->
								  <li><a href="#">下一页</a></li> 
								  <li><a id="total">共1页</a></li>
								</ul>
							
						</div>
						<div class="input-group">
							  <span class="input-group-addon">第</span>
							  <input type="text" class="form-control" placeholder="页数">
							  <span class="input-group-addon">页</span>
							  <span id="go" class="input-group-addon">GO</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</body></html>