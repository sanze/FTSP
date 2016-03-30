var MAP_LINES = new Map();
var MAP_BAIDU_POLYLINE = new Map();
var MAP_MARKER = new Map();
var color = ["#8968CD","#87CEFA","green","red","orange","yellow","blue","#FF6EB4"];
var displayStrategy = 0;

var Choosed_Marker = [];
var Choosed_Polylines = [];

var RANGE = 0.05;  //单位象素
var MAX_Bounds = null;

var BaiduMap = null;

function getMapZoom(){
	return BaiduMap.getZoom();
}

function getMapCenter(){
	return BaiduMap.getCenter();
}

//默认刷新，以当前区域中心当前层级
function refresh(){
	BaiduMap.centerAndZoom(BaiduMap.getCenter(),BaiduMap.getZoom());
	reloadGisData();
}

//指定条件刷新
function refreshWithCenter(center,zoom){
	var currentZoom = BaiduMap.getZoom();
	var viewport = {center:center,zoom:zoom};
	BaiduMap.setViewport(viewport);
	//层级发生改变时centerAndZoom会触发zoomend的监听，
	//所以只有在同层级进行刷新的时候重新load地图上的Gis资源
	if(parseInt(currentZoom) == zoom){
		reloadGisData();
	}
}

//获取真实的边界，比页面可视区域要大
function getRealBound(bounds){
	var swPoint = bounds.getSouthWest();//可视区域左下角坐标
	var nePoint = bounds.getNorthEast();//可视区域右上角坐标
	var new_swPoint = new BMap.Point(swPoint.lng-RANGE,swPoint.lat-RANGE);
	var new_nePoint = new BMap.Point(nePoint.lng+RANGE,nePoint.lat+RANGE);
	var realBounds = new BMap.Bounds(new_swPoint,new_nePoint);
	return realBounds;
}

function reloadGisData(){
	var bounds = BaiduMap.getBounds();
	//需要重新获取数据时，要重设Boundary
	var realBounds = getRealBound(bounds);
	MAX_Bounds = realBounds;
	clearMap();
	getRoutePathes(realBounds);
}

//拼装jsonString传后台
function getJsonString(bounds){
	var sw = bounds.getSouthWest();
	var ne = bounds.getNorthEast();
	var zoom = BaiduMap.getZoom();
	var regionId = parent.region_combo.getValue();
	var jsonString = {"jsonString":Ext.encode({"minLng":sw.lng,"minLat":sw.lat,
			"maxLng":ne.lng,"maxLat":ne.lat,"zoom":zoom})};
	return jsonString;
}

//后台获取路线数据
function getRoutePathes(bounds){
	var jsonString = getJsonString(bounds);
	Ext.Ajax.request({
		url:'gis!getGisResourceData.action',
		type: 'post',
		params: jsonString,
		success: function(response) {
			var obj = Ext.decode(response.responseText);
			//清除缓存
			MAP_LINES.clear();
			for(var i=0;i<obj.length;i++){
				var line = obj[i];				
				addPolyline(line);
			}
		},
		error:function(response) {
			alert('error');
		},
		failure:function(response) {
			alert('failure');
		}
	});	
	
//	Ext.Ajax.request({
//		url:'gis!getStationsNotInCable.action',
//		type: 'post',
//		params: jsonString,
//		success: function(response) {
//			var obj = Ext.decode(response.responseText);
//			for(var i=0;i<obj.length;i++){
//				addMarker(obj[i]);
//			}
//		},
//		error:function(response) {
//			alert('error');
//		},
//		failure:function(response) {
//			alert('failure');
//		}
//	});	
}

//后台获取路线数据
function getTestRoutes(cableSection){
		
	
}

//画百度折线
function addPolyline(line){
	//缓存光缆段信息
	MAP_LINES.put(line.lineId,line);
	
	var pathArray = getPolylinePath(line);
	var state = 2;
	if(line.breakpoints != 0){
		var lngLats = line.breakpoints.split('/');
		for(var i=0;i<lngLats.length;i++){
			var lngLat = lngLats[i].split(',');
			var point = {}
			point.lng = lngLat[0];
			point.lat = lngLat[1];
			addBreakPoint(point);
		}
	}
	var polylineOptions = {strokeColor:color[2], strokeWeight:5, strokeOpacity:0.7};
	var polyline = new BMap.Polyline(pathArray,polylineOptions);
	BaiduMap.addOverlay(polyline);	
	MAP_BAIDU_POLYLINE.put(line.lineId, polyline);
	setLineState(line.lineId,line.state);
	//添加折线监听
	polyline.addEventListener("mouseover", function(e) {
		e.target.setStrokeWeight(8);
//		e.target.setStrokeColor(color[4]);
	});
	polyline.addEventListener("mousedown", function(e) {
		var jsonString = {"jsonString":Ext.encode({"A_END":line.start.ID,
			"Z_END":line.end.ID})};
		Ext.Ajax.request({
			url:'gis!getTestRoutesByAZ.action',
			type: 'post',
			params: jsonString,
			success: function(response) {
				var obj = Ext.decode(response.responseText);
				initInfoWindow(obj);
				BaiduMap.openInfoWindow(infoWindow,e.point);
			},
			error:function(response) {
				alert('error');
			},
			failure:function(response) {
				alert('failure');
			}
		});
	});
	polyline.addEventListener("mouseout", function(e) {
		e.target.setStrokeWeight(5);
	});
}

//组装光缆段
function getPolylinePath(line){
	var points = [line.start,line.end];
	var cableSectionArray = new Array();//折点数组
	for ( var i = 0; i < 2; i++) {
		//遍历折点，准备光缆段折点数组cableSectionPath
		var BPoint = new BMap.Point(points[i].LNG,points[i].LAT);
		cableSectionArray.push(BPoint);
		if(MAP_MARKER.containsKey(points[i].ID)){
			continue;
		}
		//遍历折点，并添加各种标记物
		addMarker(points[i]); 			
	}
	return cableSectionArray;
}

//----- marker menu -----
function addMarkerMenu(marker,point){
	var markerContextMenu = new BMap.ContextMenu();
	var markerMenuItem = [	  	    
		{
			text:'<font face="黑体" color="black" size="2"><b>查看详细信息</b></font>',
			callback:function(p){
				//marker.enableDragging();
			}
		},
		{
			text:'<font face="黑体" color="black" size="2"><b>查看站内告警</b></font>',
			callback:function(p){
				//marker.enableDragging();
			}
		}
	];
	for(var i=0; i < markerMenuItem.length; i++){
		markerContextMenu.addItem(new BMap.MenuItem(markerMenuItem[i].text,markerMenuItem[i].callback,{width:120}));
		markerContextMenu.addSeparator();
 	}
	marker.addContextMenu(markerContextMenu);
}

//添加折点资源
function addMarker(point){
	var BPoint = new BMap.Point(point.LNG, point.LAT); 
	var iconPath = 'resource/images/GisImages/Station_NoAlarm.png';

	var icon = new BMap.Icon(iconPath,new BMap.Size(32, 32));
	var markerOptions = {
			icon: icon,
			offset:new BMap.Size(0, -20),
			enableDragging: false,
			draggingCursor: "move",
			title: "局站ID:"+point.ID
		}
	var marker = new BMap.Marker(BPoint, markerOptions);
	marker.setAnimation(BMAP_ANIMATION_DROP);
	BaiduMap.addOverlay(marker);
	addMarkerMenu(marker,point);
	MAP_MARKER.put(point.ID,marker);
}

function addLabel(content,point){
	var opts = new BMap.LabelOptions({
		offset:new BMap.Size(0, -1),
		position:point,
		enableMassClear: false
	});
	label = new BMap.Label(content,point);
	BaiduMap.addOverlay(myLabel);
}

function initInfoWindow(obj){
	var title = '<h><font face="黑体" color="black" size="2"><b>测试路由信息</b></font></h>';
	var line = '<hr width=100% size=3 color=#5151A2 style="FILTER: alpha(opacity=100,finishopacity=0,style=3)">';
	var table_start = '<table border="1">',table_end = '</table>';
	var tr_start = '<tr>',tr_end = '</tr>';
	var head = '<tr><td align="center" width="200px">光缆段名</td><td align="center" width="110px">光缆段代号</td><td align="center" width="90px">操作</td></tr>';
	var td1,td2,td3;
	var table = table_start + head;
	for(var i=0;i<obj.length;i++){
		table += tr_start;
		var cell = obj[i];
		td1 = '<td align="center" width="200px">'+cell.CABLE_SECTION_NAME+'</td>';
		table += td1;
		td2 = '<td align="center" width="110px">'+cell.CABLE_SECTION_NO+'</td>';
		table += td2;
		if(cell.testRouteId == ""){
			td3 = '<td align="center" width="90px">无路由</td>';
		}else{
			td3 = '<td align="center" width="90px">'+
			'<a href="javascript:void(0);" onclick="doTest('+cell.testRouteId+')">执行</a></td>';
		}
		table += td3;
		table += tr_end;
	}
	table += table_end;
	var content = title+line+table;
	infoWindow.setContent(content);
}

function doTest(testRouteId){
	alert(testRouteId);
}

//清空覆盖物，同时清空所有全局变量
function clearMap(){
	BaiduMap.clearOverlays();
	MAP_LINES.clear();
	MAP_BAIDU_POLYLINE.clear();
	MAP_MARKER.clear();
	Choosed_Marker = [];
	Choosed_Polylines = [];
}

//重置地图中心点
function resetLocation(type,id){
	var jsonString = {"jsonString":Ext.encode({"type":type,"id":id})};
	Ext.Ajax.request({
		url:'gis!getLocationCenterPoint.action',
		type: 'post',
		params: jsonString,
		success: function(response) {
			var obj = Ext.decode(response.responseText);
			var center = new BMap.Point(obj.lng,obj.lat);
			var zoom;
			if(type == 5){
				zoom = 10;
			}else
				zoom = 16;
			refreshWithCenter(center,zoom);
		},
		error:function(response) {
			alert('error');
		},
		failure:function(response) {
			alert('failure');
		}
	});	
}

//重置
function reset(){
	if(Choosed_Marker.length != 0){
		for(var i=0;i<Choosed_Marker.length;i++){
			Choosed_Marker[i].setAnimation(BMAP_ANIMATION_DROP);
		}
		Choosed_Marker = [];
	}
	if(Choosed_Polylines.length != 0){
		for(var i=0;i<Choosed_Polylines.length;i++){
			Choosed_Polylines[i].setStrokeColor("green");
		}
		Choosed_Polylines = [];
	}
}

//更新地图信息
function updateGisMap(data) {
	if(data.dataType == "0"){
		//断点信息
		var breakPoint = new BMap.Point(data.lng, data.lat); 
		addBreakPoint(breakPoint);
	}else if(data.type == "1"){
		//更新光缆
		if(displayStrategy != 0)
			return;
		if(MAP_BAIDU_POLYLINE.containsKey(data.lineId))
			setCableSectionState(data.lineId,data.state);
		
	}else if(data.type == "2"){
		//更新机房
		if(displayStrategy != 1)
			return;
		if(MAP_MARKER.containsKey(data.stationId))
			setStationState(data.stationId,data.state);
	}
}

//添加断点
function addBreakPoint(breakPoint){	
	var iconPath = 'resource/images/GisImages/breakMarker.png';
	var icon = new BMap.Icon(iconPath,new BMap.Size(16, 16));
	var markerOptions = {
			icon: icon,
			offset:new BMap.Size(0, 0),
			enableDragging: false,
			draggingCursor: "move"
		};
	var marker = new BMap.Marker(breakPoint, markerOptions);
	marker.setAnimation(BMAP_ANIMATION_DROP);
	BaiduMap.addOverlay(marker);
//	marker.addEventListener("mouseover", function(e) {
//		document.getElementById("position").style.visibility="visible";
//		document.getElementById("position").innerHTML = "<FONT face=黑体 color=blue size=2>经度： "
//			+e.point.lng+"<br>纬度： "+e.point.lat+"</FONT>";		
//	});	
}

//更新机房告警等级
function setStationState(stationId,state){
	if(!MAP_MARKER.containsKey(stationId)){
		return;
	}
	var marker = MAP_MARKER.get(stationId);	
	var iconPath = getStationIconPath(state)
	var icon = new BMap.Icon(iconPath,new BMap.Size(32, 32));
	marker.setIcon(icon);
}

function setLineState(id,state){
	if(state == "7"){
		MAP_BAIDU_POLYLINE.get(id).setStrokeStyle("dashed");
	}else
		MAP_BAIDU_POLYLINE.get(id).setStrokeStyle("solid");
	MAP_BAIDU_POLYLINE.get(id).setStrokeColor(color[state]);
}

function getStationIconPath(state){
	var iconPath = null;
	if(state == 0){
		iconPath = 'resource/images/GisImages/Station_NoAlarm.png';
	}else if(state == 1){
		iconPath = 'resource/images/GisImages/Station_Prompt.png';
	}else if(state == 2){
		iconPath = 'resource/images/GisImages/Station_Serious.png';
	}else if(state == 3){
		iconPath = 'resource/images/GisImages/Station_Urgent.png';
	}else{
		iconPath = 'resource/images/GisImages/Station_Invalid.png';
	}
	return iconPath;
}

function moveTo(point){
	var centerPoint = new BMap.Point(parseFloat(point.lng),parseFloat(point.lat));	
	window.setTimeout(function(){
		var viewport = {center:centerPoint,zoom:parseInt(16)};
		BaiduMap.setViewport(viewport);
	},500);
	
}

function initMap(info){	
	var mapOptions = {
		minZoom: 4, 
		maxZoom: 18, 
		mapType: BMAP_NORMAL_MAP
	}
	var map = new BMap.Map("container", mapOptions);      //设置卫星图为底图BMAP_PERSPECTIVE_MAP
	
	var initPoint = ADDRESS_MAP.get("南京");    // 创建点坐标
	
	var zoom = 10;
//	if(info.zoom == 1){
//		zoom = 8;
//	}else
//		zoom = 13;
	
	map.centerAndZoom(initPoint,parseInt(zoom));                    // 初始化地图,设置中心点坐标和地图级别。
	map.enableScrollWheelZoom();                  // 启用滚轮放大缩小。
	map.enableKeyboard();                         // 启用键盘操作。  
	map.enableContinuousZoom();										//启用连续缩放效果
	
	// ----- control -----
	map.addControl(new BMap.NavigationControl()); //地图平移缩放控件
	map.addControl(new BMap.ScaleControl()); //显示比例尺在右下角		
	
	var opts = {
		  width : 400,     // 信息窗口宽度
		  height: 200,     // 信息窗口高度
		  title : "" , // 信息窗口标题
		  enableMessage:false 
	}
	infoWindow = new BMap.InfoWindow("", opts); 
 	//------------------ listeners ----------------------
 	map.addEventListener("zoomend",function(){
 		//BaiduMap = map;
 		reloadGisData();
 	});
 	
 	map.addEventListener("dragend",function(){
 		var bounds = map.getBounds();
 		if(MAX_Bounds.containsBounds(bounds)){
 			return;
 		}
 		//需要重新获取数据时
 		reloadGisData();
 	});
 	
 	BaiduMap = map;
 	
	//请求后台	
	var bounds = map.getBounds();
	var realBounds = getRealBound(bounds);
	//初始化最大可移动区域
	MAX_Bounds = realBounds;	
	//获取初始线路数据
	getRoutePathes(realBounds);
}	

function clickURL(tab,url,val){
	var htmlUrl =  '<iframe src="'+url+'?selectId='+ val +'" frameborder="0" width="100%" height="100%"/>';
	//调用父页面的TabPanel
	window.parent.parent.showTab(tab, htmlUrl);
}

//functions for gisManage page
function getCachedGisData(){
	var data = {};
	data.lines = '';
	for (i = 0; i < MAP_LINES.size(); i++) {
		var line = MAP_LINES.elements[i];
		if(i == MAP_LINES.size()-1){
			data.lines += line.key+':'+line.value.cableSectionIds;
		}else	
			data.lines += line.key+':'+line.value.cableSectionIds+'/';
    }
	data.stationIds = MAP_MARKER.keys();
	return data;
}

function displayDataByStrategy(strategy){
	displayStrategy = strategy;
	var data = getCachedGisData();
	var jsonString = {"jsonString":Ext.encode({"strategy":strategy,"lines":data.lines,
					"stationIds":data.stationIds})};
	Ext.Ajax.request({
		url:'gis!displayDataByStrategy.action',
		type: 'post',
		params: jsonString,
		success: function(response) {
			var obj = Ext.decode(response.responseText);
			var lineStates = obj.lineStates;
			var stationStates = obj.stationStates;
			for(var i=0;i<lineStates.length;i++){
				var info = lineStates[i];
//				if(MAP_CABLE_SECTIONS.get(cs.cableSectionId).BREAKPOINT == '0')
					setLineState(info.lineId,info.state);
			}
			for(var i=0;i<stationStates.length;i++){
				var station = stationStates[i];
				setStationState(station.stationId,station.state);				
			}
		},
		error:function(response) {
			alert('error');
		},
		failure:function(response) {
			alert('failure');
		}
	});
}

Ext.onReady(function(){
//	Ext.Ajax.request({
//		url:'gis!getMapInitInfo.action',
//		type: 'post',
//		success: function(response) {
//			var info = Ext.decode(response.responseText);
//			initMap("");
//		},
//		error:function(response) {
//			alert('error');
//		},
//		failure:function(response) {
//			alert('failure');
//		}
//	});
	initMap("");
});
