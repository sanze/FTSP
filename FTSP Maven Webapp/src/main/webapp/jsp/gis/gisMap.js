var MAP_LINES = new Map();
var MAP_BAIDU_POLYLINE = new Map();
var MAP_MARKER = new Map();
var MAP_BREAK_POINT = new Map();
var color = ["#8968CD","#87CEFA","#606060","green","red","green","green","#FF6EB4"];
var displayStrategy = 0;

var Choosed_Marker = [];
var Choosed_Polylines = [];

var RANGE = 0.05;  //单位象素
var MAX_Bounds = null;

var BaiduMap = null;
routeId = null;
var routeIdList = null;

var labelOnTheLine = null;

var unselectedLineWidth = 2;
var selectedLineWidth = 5;
var iconWidthOfMap = 17;

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

function refreshWithCity(city){
	var end = city.indexOf("(");
	var _city = city.substring(0,end);
	var zoom = parseInt(10);
	if(!ADDRESS_MAP.containsKey(_city)){
		_city = '北京';
		zoom = parseInt(7);
	}
	var center = ADDRESS_MAP.get(_city);
	var viewport = {center:center,zoom:zoom};
	BaiduMap.setViewport(viewport);
	reloadGisData();
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
	var jsonString = {"jsonString":Ext.encode({"minLng":sw.lng,"minLat":sw.lat,
			"maxLng":ne.lng,"maxLat":ne.lat,"zoom":zoom,"strategy":displayStrategy,
			"transSysId":parent.sys_combo.getValue()})};
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
			if(obj.length != 0)
				displayDataByStrategy(displayStrategy);
		},
		error:function(response) {
			alert('error');
		},
		failure:function(response) {
			alert('failure');
		}
	});	
	
	Ext.Ajax.request({
		url:'gis!getStationsNotInCable.action',
		type: 'post',
		params: jsonString,
		success: function(response) {
			var obj = Ext.decode(response.responseText);
			for(var i=0;i<obj.length;i++){
				addMarker(obj[i]);
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
			var point = {};
			point.lng = lngLat[0];
			point.lat = lngLat[1];
			var cableId = lngLat[2];
			addBreakPoint(cableId,point);
		}
	}
	var polylineOptions = {strokeColor:color[3], strokeWeight:unselectedLineWidth, strokeOpacity:0.7};
	var polyline = new BMap.Polyline(pathArray,polylineOptions);
	BaiduMap.addOverlay(polyline);	
	MAP_BAIDU_POLYLINE.put(line.lineId, polyline);
//	setLineState(line.lineId,line.state);
	//添加折线监听
	polyline.addEventListener("mouseover", function(e) {
		// 光缆测试路由信息窗口打开时，不显示线路的提示标签
		if (BaiduMap.getInfoWindow() == null){
			var ops = {position : e.point,
					   offset : new BMap.Size(30,-30)};
			var names = line.cableSectionNames.replace(/,/ig, "<br>");
			var label = new BMap.Label(names, ops);
			label.setStyle({
				fontSize : "12px",
				backgroundColor : "#FFFFBB"
			});
			if (labelOnTheLine != null && labelOnTheLine != label)
			{
				BaiduMap.removeOverlay(labelOnTheLine);
			}
			labelOnTheLine = label;
			BaiduMap.addOverlay(labelOnTheLine);			
		}
		e.target.setStrokeWeight(selectedLineWidth);
	});
	
	polyline.addEventListener("mousedown", function(e) {
		// 打开光缆测试路由信息窗口时，移除线路的提示标签
		if (labelOnTheLine != null) {
			BaiduMap.removeOverlay(labelOnTheLine);			
		}
		// 准备打开光缆测试路由信息窗口
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
		if (labelOnTheLine != null)
			BaiduMap.removeOverlay(labelOnTheLine);
		e.target.setStrokeWeight(unselectedLineWidth);
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
			text:'<font face="黑体" color="black" size="2"><b>外部光纤连接管理</b></font>',
			callback:function(p){
//				parent.parent.window.addTabPage(
//						"http://localhost:8080/FTTS/jsp/tl/tl.jsp?stationId="+point.ID, 
//								"机房","",false);
				var href = window.location.protocol+"//"+window.location.host+"/FTTS/jsp/tl/tl.jsp?stationId="+point.ID;
				parent.parent.window.addTabPage(href, "外部光纤连接详情","",false);

			}
		},
		{
			text:'<font face="黑体" color="black" size="2"><b>查看站内告警</b></font>',
			callback:function(p){
				var href = window.location.protocol+"//"+window.location.host+"/FTSP/jsp/faultManager/currentAlarm.jsp?gis_stationId="+point.ID;
				parent.parent.window.addTabPage(href, "当前告警","all",false);
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
	var iconPath = getStationIconPath(point.TYPE,0);
	var icon = new BMap.Icon(iconPath,new BMap.Size(iconWidthOfMap, iconWidthOfMap));
	var markerOptions = {
			icon: icon,
			offset:new BMap.Size(0, 0),
			enableDragging: false,
			draggingCursor: "move",
			title: "局站名称： "+point.NAME+"\n类型："+getStationType(point.TYPE)
		};
	var marker = new BMap.Marker(BPoint, markerOptions);
	marker.setAnimation(BMAP_ANIMATION_DROP);
	BaiduMap.addOverlay(marker);
	addMarkerMenu(marker,point);
	MAP_MARKER.put(point.ID,marker);
}

function initInfoWindow(obj){
	// CSS样式
	var style = '<style type="text/css"> table{border-collapse:collapse;boder:none;width:400px;font-size:12px} td{border:solid #C0C0C0 1px;padding:2px;text-align:center}</style>';
	// 标题
	var title = '<h><font face="黑体" color="black" size="2"><b>测试路由信息</b></font></h>';
	// 分割线
	var line = '<hr width=100% size=3 color=#5151A2 style="FILTER: alpha(opacity=100,finishopacity=0,style=3)">';
	var table_start = '<table>',table_end = '</table>';
	var tr_start = '<tr>',tr_end = '</tr>';
	// 表格首行
	var head = '<tr bgcolor="#d1def1"><td color="white" width="45%"><font color="#2f4f4f"><b>光缆段名</b></font></td><td width="40%"><font color="#2f4f4f"><b>光缆段代号</b></font></td><td width="15%"><font color="#2f4f4f"><b>操作</b></font></td></tr>';
	var td1,td2,td3;
	var table = table_start + head;
	for(var i=0;i<obj.length;i++){
		table += tr_start;
		var cell = obj[i];
		td1 = '<td width="45%">'+cell.CABLE_SECTION_NAME+'</td>';
		table += td1;
		td2 = '<td width="40%">'+cell.CABLE_SECTION_NO+'</td>';
		table += td2;
		if(cell.testRouteList.length > 0){
			routeIdList = cell.testRouteList;
			td3 = '<td width="15%">'+
			'<a href="javascript:void(0);" onclick="doTest()">测试</a></td>';
		}else{
			td3 = '<td width="15%">无路由</td>';
		}
		table += td3;
		table += tr_end;
	}
	table += table_end;
	var content = style+title+line+table;
	infoWindow.setContent(content);
}

function doTest() {
	routeId = routeIdList[0].TEST_ROUTE_ID;
	var jsonString = {"jsonString":Ext.encode({"ROUTE_ID":routeId})};
	Ext.Ajax.timeout = 10000;
	Ext.Ajax.request({
		url: 'gis!getTestParamById.action',
		type: 'post',
		params: jsonString,
		success: function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult==1) {
				// 测试路由下拉框列表数据构建
				testRouteStore.removeAll();
				var routeRecord = new Ext.data.Record.create(
					{name:'testRouteId', type:'int'},
					{name:'routeName', type:'string'}
				);
				for(var i=0;i<routeIdList.length;i++){
					var record = new routeRecord();
					record.set("testRouteId", routeIdList[i].TEST_ROUTE_ID);
					record.set("routeName", routeIdList[i].ROUTE_NAME);					
					testRouteStore.add(record);
				}
				if(routeIdList.length > 1){
					var record = new routeRecord();
					record.set("testRouteId", -1);
					record.set("routeName", "所有");					
					testRouteStore.add(record);
				}
				// 波长下拉框列表数据构建
		  		waveLengthStore.load({
	  		  		params: {
	  		  			"jsonString": Ext.encode({
	  		  				'routeId': routeId
	  		  			})
	  		  		},
	  			    callback: function(response) {
	  			    }
		  		});
		  		// 量程下拉框列表数据构建
			  	rangeStore.load({
	  		  		params: {
	  		  			"jsonString": Ext.encode({
	  		  				'routeId': routeId
	  		  			})
	  		  		},
	  			    callback: function(records, operation, success) {
	  			    }
		  		});
		  		// 脉宽下拉框列表数据构建
		  		pluseWidthStore.load({
	  		  		params: {
	  		  			"jsonString": Ext.encode({
		  					'routeId': routeId,
		  					'TEST_RANGE': obj.OTDR_RANGE
	  		  			})
	  		  		},
	  			    callback: function(records, operation, success) {
	  			    }
		  		});
		  		// 设置各项参数默认值
		  		Ext.getCmp("testRoute").setValue(routeIdList[0].TEST_ROUTE_ID);
				Ext.getCmp("otdrWaveLength").setValue(obj.OTDR_WAVE_LENGTH);
	  		  	Ext.getCmp("otdrRange").setValue(obj.OTDR_RANGE);
	  		  	Ext.getCmp("otdrPluseWidth").setValue(obj.OTDR_PLUSE_WIDTH);
	  		  	Ext.getCmp("testDuration").setValue(obj.OTDR_TEST_DURATION);
	  		  	Ext.getCmp("otdrRefractCoefficient").setValue(obj.OTDR_REFRACT_COEFFICIENT);
	  		  	// 默认启用各项参数控件
	  		  	Ext.getCmp("otdrWaveLength").enable();
	  		  	Ext.getCmp("otdrRange").enable();
	  		  	Ext.getCmp("otdrPluseWidth").enable();
	  		  	Ext.getCmp("testDuration").enable();
	  		  	Ext.getCmp("otdrRefractCoefficient").enable();
	  		  	confirmParaWin.show();
			} else {
				Ext.Msg.alert("错误",obj.returnMessage);
			}
		},
		error: function(response) {
			alert('error');
		},
		failure: function(response) {
			alert('failure');
		}
	});	
}

//清空覆盖物，同时清空所有全局变量
function clearMap(){
	BaiduMap.clearOverlays();
	MAP_LINES.clear();
	MAP_BAIDU_POLYLINE.clear();
	MAP_MARKER.clear();
	Choosed_Marker = [];
	Choosed_Polylines = [];
	MAP_BREAK_POINT.clear();
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
		if(data.state){
			// 增加断点标注
			var breakPoint = new BMap.Point(data.lng, data.lat); 
			addBreakPoint(data.id,breakPoint);
//			console.log("增加了一个断点标识："+MAP_BREAK_POINT.size());
		}else{
			// 去除断点标注
			if(MAP_BREAK_POINT.containsKey(data.id)){
				var marker = MAP_BREAK_POINT.get(data.id);
				BaiduMap.removeOverlay(marker);
				MAP_BREAK_POINT.remove(data.id);
//				console.log("删除了一个断点标识："+MAP_BREAK_POINT.size());
			}
		}
		
	}else if(data.dataType == "2"){
		//更新光缆
		if(displayStrategy != 1)
			return;
		if(MAP_BAIDU_POLYLINE.containsKey(data.id))
			setCableSectionState(data.id,data.state);
		
	}else if(data.dataType == "1"){
		//更新机房
		if(displayStrategy != 1)
			return;
		if(MAP_MARKER.containsKey(data.id))
			setStationState(data.id,data.state,data.type);
		
	}else if(data.dataType == "3"){ // 光缆测试状态推送信息
		var cableIds = data.id.split(',');
		for ( var i = 0; i < cableIds.length; i++) {
			var id = cableIds[i];
			var lineId = getLineIdFromCableId(id);
			setLineState(lineId, data.state);
		}
	}
}

function getLineIdFromCableId(cableId) {
	var data = getCachedGisData();
	var lines = data.lines;
	var cableIds;
	for (i = 0; i < MAP_LINES.size(); i++) {
		var line = MAP_LINES.elements[i];
		cableIds = line.value.cableSectionIds;
		var idArray = cableIds.split(','); 
		for (j = 0; j < idArray.length; j++) {
			if (idArray[j] == cableId)
				return line.key;
		}
    }
	return -1;
}

//添加断点
function addBreakPoint(id,breakPoint){	
	var iconPath = rootDir+'resource/images/GisImages/breakPoint.png';
	var icon = new BMap.Icon(iconPath,new BMap.Size(iconWidthOfMap, iconWidthOfMap));
	var markerOptions = {
			icon: icon,
			offset:new BMap.Size(0, 0),
			enableDragging: false,
			draggingCursor: "move"
		};
	var marker = new BMap.Marker(breakPoint, markerOptions);
	marker.setAnimation(BMAP_ANIMATION_DROP);
	
	if(MAP_BREAK_POINT.containsKey(id)){
		//JS的Map中可以放置相同Key的对象，如遇到相同id的Marker时需清除原来的Marker
		BaiduMap.removeOverlay(MAP_BREAK_POINT.get(id));
		MAP_BREAK_POINT.remove(id);
		MAP_BREAK_POINT.put(id, marker);		
	}else{
		MAP_BREAK_POINT.put(id, marker);	
	}
	BaiduMap.addOverlay(marker);

//	marker.addEventListener("mouseover", function(e) {
//		document.getElementById("position").style.visibility="visible";
//		document.getElementById("position").innerHTML = "<FONT face=黑体 color=blue size=2>经度： "
//			+e.point.lng+"<br>纬度： "+e.point.lat+"</FONT>";		
//	});	
}

// 更新光缆段上的告警
function setCableSectionState(id,state){
	if (id < 0)
		return;
	MAP_BAIDU_POLYLINE.get(id).setStrokeColor(color[state]);
}

//更新机房告警等级
function setStationState(stationId,state,type){
	if(!MAP_MARKER.containsKey(stationId)){
		return;
	}
	var marker = MAP_MARKER.get(stationId);	
	var iconPath = getStationIconPath(type,state)
	var icon = new BMap.Icon(iconPath,new BMap.Size(iconWidthOfMap, iconWidthOfMap));
	marker.setIcon(icon);
}

function setLineState(id,state){
	if (id < 0) 
		return;
	if(state == "7"){
		MAP_BAIDU_POLYLINE.get(id).setStrokeStyle("dashed");
	}else{
		MAP_BAIDU_POLYLINE.get(id).setStrokeStyle("solid");
		MAP_BAIDU_POLYLINE.get(id).setStrokeColor(color[state]);
	}
}

function getStationIconPath(type,state){
	var iconPath = null;
	switch (type) {
	case 1: //调度大楼
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_diaoddl_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_diaoddl_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_diaoddl_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_diaoddl_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_diaoddl_na.png';
			break;
		}
		break;
	case 2: //微波站
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_weibz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_weibz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_weibz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_weibz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_weibz_na.png';
			break;
		}
		break;
	case 3: //光缆分支
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_glfz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_glfz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_glfz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_glfz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_glfz_na.png';
			break;
		}
		break;		
	case 4: //生产基地
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_shengcjd_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_shengcjd_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_shengcjd_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_shengcjd_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_shengcjd_na.png';
			break;
		}
		break;
	case 5: //供电营业所
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_gongdyys_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_gongdyys_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_gongdyys_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_gongdyys_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_gongdyys_na.png';
			break;
		}
		break;
	case 6: //牵引站
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_qianyz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_qianyz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_qianyz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_qianyz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_qianyz_na.png';
			break;
		}
		break;		
	case 7: //火电厂
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_huodc_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_huodc_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_huodc_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_huodc_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_huodc_na.png';
			break;
		}
		break;
	case 8:	//水电厂
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_shuidc_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_shuidc_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_shuidc_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_shuidc_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_shuidc_na.png';
			break;
		}
		break;
	case 9: //抽水蓄能站
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_chousxnz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_chousxnz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_chousxnz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_chousxnz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_chousxnz_na.png';
			break;
		}
		break;
	case 10: //核电厂
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_hedc_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_hedc_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_hedc_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_hedc_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_hedc_na.png';
			break;
		}
		break;		
	case 11:	//500kv变电站
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_500bdz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_500bdz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_500bdz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_500bdz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_500bdz_na.png';
			break;
		}
		break;
	case 12:	//220kv变电站
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_220bdz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_220bdz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_220bdz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_220bdz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_220bdz_na.png';
			break;
		}
		break;
	case 13:	//110kv变电站
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_110bdz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_110bdz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_110bdz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_110bdz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_110bdz_na.png';
			break;
		}
		break;
	case 14:	//35kv变电站
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_35bdz_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_35bdz_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_35bdz_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_35bdz_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_35bdz_na.png';
			break;
		}
		break;
	default:
		switch(state) {
		case 1:
			iconPath = rootDir+'resource/images/GisImages/station_unknown_cr.png';
			break;
		case 2:
			iconPath = rootDir+'resource/images/GisImages/station_unknown_mj.png';
			break;
		case 3:
			iconPath = rootDir+'resource/images/GisImages/station_unknown_mn.png';
			break;
		case 4:
			iconPath = rootDir+'resource/images/GisImages/station_unknown_wr.png';
			break;
		default:
			iconPath = rootDir+'resource/images/GisImages/station_unknown_na.png';
			break;
		}
		break;
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
	// 缺省的默认区域为北京
	var city = '北京';
	var zoom = parseInt(7);
	if(ADDRESS_MAP.containsKey(info)){
		city = info;
		zoom = parseInt(10);
	}

	var initPoint = ADDRESS_MAP.get(city);    // 创建点坐标
	

//	if(info.zoom == 1){
//		zoom = 8;
//	}else
//		zoom = 13;
	
	map.centerAndZoom(initPoint,parseInt(zoom));  // 初始化地图,设置中心点坐标和地图级别。
	map.enableScrollWheelZoom();                  // 启用滚轮放大缩小。
	map.enableKeyboard();                         // 启用键盘操作。  
	map.enableContinuousZoom();					  //启用连续缩放效果
	
	// ----- control -----
	map.addControl(new BMap.NavigationControl()); //地图平移缩放控件
	map.addControl(new BMap.ScaleControl()); //显示比例尺在右下角		
	
	var opts = {
		  width : 0,     // 信息窗口宽度
		  height: 0,     // 信息窗口高度
		  maxWidth: 600,
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
				setStationState(station.stationId,station.state,station.type);				
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

function getStationType(typeId) {
	switch (typeId) {
	case 1:
		return "调度大楼";
	case 2:
		return "微波站";
	case 3:
		return "光缆分支";
	case 4:
		return "生产基地";
	case 5:
		return "供电营业所";
	case 6:
		return "牵引站";
	case 7:
		return "火电厂";
	case 8:
		return "水电厂";
	case 9:
		return "抽水蓄能站";
	case 10:
		return "核电厂";
	case 11:
		return "500Kv变电站";
	case 12:
		return "220Kv变电站";
	case 13:
		return "110Kv变电站";
	case 14:
		return "35Kv变电站";
	default:
		return "未知("+typeId+")";
	}
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
	var end = parent.area.indexOf("(");
	var city = parent.area.substring(0,end);
	initMap(city);
});
