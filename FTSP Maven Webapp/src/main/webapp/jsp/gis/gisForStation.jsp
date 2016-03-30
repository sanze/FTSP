<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
  <head>
    
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../jsp/gis/apiv1.3.min.js"></script>       	
	<script type="text/javascript" src="Map.js"></script> 
	<script type="text/javascript" src="address.js"></script> 
	<link rel="stylesheet" type="text/css" href="bmap.css"/> 
	
	<script type="text/javascript">
		var lng="<%=request.getParameter("lng")%>";
		var lat="<%=request.getParameter("lat")%>";
	</script> 
	
	<style type="text/css">  
	    html{height:100%}    
	    body{height:100%;margin:0px;padding:0px}    
	    #container{height:100%}    
	</style>			
  </head>
  
  <body>
    <div id="container"> </div>
    <script type="text/javascript">
    		var map = new BMap.Map("container");      //设置卫星图为底图BMAP_PERSPECTIVE_MAP
    		var initPoint;
    		if(lng != "" && lat != ""){
    			initPoint = new BMap.Point(lng, lat); 
    		}else 
    			initPoint = ADDRESS_MAP.get("南京");    // 创建点坐标
    		//alert(initPoint.lng);
    		var zoom = 10;    		
    		map.centerAndZoom(initPoint,10);                    // 初始化地图,设置中心点坐标和地图级别。
    		map.enableScrollWheelZoom();                  // 启用滚轮放大缩小。
    		map.enableKeyboard();                         // 启用键盘操作。  
    		map.enableContinuousZoom();										//启用连续缩放效果
    		
    		// ----- control -----
//    		map.addControl(new BMap.NavigationControl()); //地图平移缩放控件
    		map.addControl(new BMap.ScaleControl()); //显示比例尺在右下角	
    		
    		var iconPath ="../../resource/images/GisImages/Station_NoAlarm.png";

    		var icon = new BMap.Icon(iconPath,new BMap.Size(32, 32));
    		var markerOptions = {
    				icon: icon,
    				offset:new BMap.Size(0, -20),
    				enableDragging: true,
    				draggingCursor: "move"
    			}
    		var marker = new BMap.Marker(initPoint, markerOptions);
    		map.addOverlay(marker);
    		marker.addEventListener("dragend", function(e) {		
    			parent.window.setLngLat(e.point);
    		});
    </script> 	
  </body> 
</html>
