<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>FTTS</title>
<head>
		<meta http-equiv="X-UA-Compatible" content="IE=9">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    	<link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
	    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/xtheme-blue.css" />
	    
	    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
	    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    
		<script language="javascript" type="text/javascript" src="../../resource/jquery/jquery-1.4.2.min.js"></script>
		<script language="javascript" type="text/javascript" src="../../resource/highcharts/js/highcharts.js"></script>
		<script language="javascript" type="text/javascript" src="../../resource/highcharts/js/modules/exporting.js"></script>
   	    <script language="javascript" type="text/javascript" src="../../resource/highcharts/js/highslide-full.min.js"></script>
   	    <script language="javascript" type="text/javascript" src="../../resource/highcharts/js/highslide.config.js" charset="utf-8"></script>
   	    <script type="text/javascript" src="resultDetal.js"></script>
   	   	<link rel="stylesheet" type="text/css" href="../../resource/highcharts/js/highslide.css" />
		<script type="text/javascript">
		resultId = "<%=request.getParameter("resultId")%>"
		 </script>
        <script type="text/javascript">
		    var strEvent='';
		    var X1=0.0;
		    var Y1=0.0;
		    var X2=0.0;
		    var Y2=0.0;
		      
		    var flag = 1;
        </script>
         <script type="text/javascript">
         hs.width =700;
         </script> 
         
<script type="text/javascript">
$(function () {
    var chart;
    $(document).ready(function() {
    
        // define the options
        var options = {
    
            chart: {
                renderTo: 'container',
                zoomType: 'xy',
                spacingRight: 20,
                type: 'area'
            },
            title: {
                text: '折线图'
            },
            exporting:{ 
                     enabled:false  //用来设置是否显示‘打印’,'导出'等功能按钮，不设置时默认为显示 
                },
   		   colors: ['#CE0000', '#50B432', '#ED561B', '#DDDF00'],
    
            subtitle: {
                text: ''
            },
            
            xAxis: {
                title: {
                    text: null
                },
                gridLineColor:['#50B432'],
                lineColor:['#50B432'],
                gridLineWidth:1
                
            },
    
       		 yAxis: {
                title: {
                    text: ''
                },
                showFirstLabel: false
            },
    
		   credits: 
		   {
		         enabled : false
		    },
		   tooltip: {
                shared: true,
                crosshairs: true,
                snap: 0, 
                crosshairs: {                 //交叉点是否显示的一条纵线
	            width: 1,
	            color: '#99CC66',
	            dashStyle: 'shortdot'
		        },		        
                borderWidth: 1              //边框宽度(大小)
            },    
			plotOptions: {
                area: {
                    fillColor: {
                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                        stops: [
                            [0, 'rgba(0,0,0,0)'],
                            [1, 'rgba(0,0,0,0)']
                        ]
                    },
                    lineWidth: 1,
                    marker: {
                        enabled: false,
                        states: {
                            hover: {
                                enabled: true,
                                radius: 5
                            }
                        }
                    },
                    shadow: false,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    threshold: null
                },
                 series: {
                    cursor: 'pointer',
                    point: {
                        events: {
                            click: function() {
                            	// 奇数

                            	var ABX = 0;
                            	var ABY = 0;
                            	if(flag%2 !=0)
                            	{
                            	    if(flag ==1)
                            	    {
                            	    	X1= this.x;
                            	    	Y1= this.y;
                            	    	X2= 0;
                            	    	Y2= 0;
                            	    }else
                            	    {
										X1 = X2;
                            	    	Y1 = Y2;
                            	    	X2 = this.x;
                            	    	Y2 = this.y;
                            	    }

		                        }else
		                        {
		                             if(flag ==2)
                            	    {
                            	    	X2 = this.x;
                            	    	Y2 = this.y;
                            	   	}else
                            	   	{
										X1 = X2;
                            	    	Y1 = Y2;
                            	    	X2 = this.x;
                            	    	Y2 = this.y;
                            	   	}
		                        }
		                        
		                        if(flag !=1){
			                        ABX = Math.floor((X2-X1) * 1000) / 1000;
			                        ABY = Math.floor((Y2-Y1) * 100) / 100;
		                        }
		                        
		                        Ext.fly(APointX1.getEl()).update('A X: '+X1+' Km ');
		                        Ext.fly(APointY1.getEl()).update('A Y: '+Y1 + ' db');
		                        Ext.fly(APointX2.getEl()).update('B X: '+X2+' Km ');
		                        Ext.fly(APointY2.getEl()).update('B Y: '+Y2 + ' db');
		                        Ext.fly(APointX.getEl()).update('A-B 距离: '+ ABX + ' Km');
		                        Ext.fly(APointY.getEl()).update('A-B 差值: '+ ABY+ ' db');
		                        //Ext.getCmp('word-status').doLayout( );                      	
                            	flag++;
                            }
                        }
                    },
                    marker: {
                        lineWidth: 0
                    }
                }
            },
    
            series: [{
                name: 'Result',
                lineWidth: 1,
                marker: {
                    radius: 1
                }
            }]
        };
    
    
        // Load data asynchronously using jQuery. On success, add the data
        // to the options and initiate the chart.
        // This data is obtained by exporting a GA custom report to TSV.
        // http://api.jquery.com/jQuery.get/
        var serverFilePathAddr = "123.tsv";
        var path;
        var pathArray = [];

        path = "test-result!getResultById.action";
		//console.info('path:'+path);
        jQuery.get(path, {
			"jsonString":Ext.encode({
				'TEST_RESULT_ID':resultId
			})
		}, function(tsv, state, xhr) {
            var lines = [],
            	lines2 = [],
           		linesTemp = [],
                listen = true,
                date,
                allVisits = [],
                lineEvent = [],
                newVisitors = [];
    
            // inconsistency
            if (typeof tsv !== 'string') {
                tsv = xhr.responseText;
            }
    
            // split the data return into lines and parse them
            var obj = eval('('+tsv+')');
            if(obj.RESULT_POINT!= null && obj.RESULT_POINT.indexOf("\n") >= 0 ){
            	linesTemp = obj.RESULT_POINT.split('\n');
            }
            jQuery.each(linesTemp, function(i, line) {

                if (listen) {
                	line = line.split(/,/);
       				if(line[0] !='' && line[1] != ''){
       					//console.info('========='+line[0]+'  ,  '+line[1]);
	                    allVisits.push([
                        parseFloat(Math.floor(line[0] * 1000) / 1000),
                        parseFloat(Math.floor(line[1] * 100) / 100)
	                    ]);
                     }
                }
            });
    
         	options.series[0].data = allVisits;  
            chart = new Highcharts.Chart(options);
        });
        
        
        
        
    });
    
});
</script>

<script type="text/javascript">
	
</script>
	
	</head>
	<style type="text/css">
	* {
	    font-family: Verdana, Helvetica;
	    font-size: 8pt;
	}
	.highslide-caption {
    display: none;
    border: 5px solid white;
    border-top: none;
    padding: 5px;
    background-color: white;
	}
	
	</style>

	<body>
	
	<div id="container" style="width: 780px;height: 430px"></div>
	<div id="chartPanel" style="width: 780px">
	</div>
	</body>
</html>