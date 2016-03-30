package com.fujitsu.manager.gisManager.serviceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fujitsu.IService.IWSManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.GisManagerMapper;
import com.fujitsu.dao.mysql.TransSystemMapper;
import com.fujitsu.manager.faultManager.service.AlarmManagementService;
import com.fujitsu.manager.gisManager.service.GisManagerService;
import com.fujitsu.util.SpringContextUtil;

@Service
public class GisManagerServiceImpl extends GisManagerService{
	
	@Resource
	public GisManagerMapper gisManagerMapper;
	@Resource
	public AlarmManagementService alarmManagementService;
	@Resource
	public TransSystemMapper transSystemMapper;
	@Resource
	public CommonManagerMapper commonMapper;
	
	public IWSManagerService service;

	
	//记录当前可视区域下的地图的相关信息
	private Map<String,Object> baiduMapInfo = new HashMap<String,Object>();
	//记录当前地图中显示的光缆段，局站信息

	
	private void setBaiduMapInfo(Map<String, Object> map){
		if(!map.isEmpty()){		
			baiduMapInfo.put("zoom", map.get("zoom").toString());
			baiduMapInfo.put("maxLat", map.get("maxLat").toString());
			baiduMapInfo.put("maxLng", map.get("maxLng").toString());
			//System.out.println(bounds.getMaxLng()+"   "+bounds.getMaxLat());
			baiduMapInfo.put("minLat", map.get("minLat").toString());
			baiduMapInfo.put("minLng", map.get("minLng").toString());
			baiduMapInfo.put("strategy", map.get("strategy").toString());
			if(map.get("transSysId")!="" && map.get("transSysId") != null)
				baiduMapInfo.put("transSysId", map.get("transSysId").toString());
			//System.out.println(bounds.getMinLng()+"   "+bounds.getMinLat());
		}
	}
	
	@Override
	public Map<String, Object> getTransSystems(Map<String, Object> map) {		
		Map<String,Object> data = new HashMap<String,Object>();
		if(map.isEmpty()){
			return data;
		}
		String area = map.get("area").toString();
		List<Map<String, Object>> resultList = gisManagerMapper.getTransSystemsByArea(area);
		for(Map<String, Object> obj:resultList){
			String name = obj.get("SYS_NAME").toString();
			int netLevel = Integer.valueOf(obj.get("NET_LEVEL").toString())-1;
			String netLevelDisplay = CommonDefine.RESOURCE.TRANS_SYS.NET_LEVEL_DISPLAY[netLevel];
			obj.put("SYS_NAME", netLevelDisplay+":"+name);
		}		
		data.put("data", resultList);
		data.put("total", resultList.size());
		return data;
	}
	
	//获取GIS数据	
	public List<Map<String,Object>> getGisResourceData(Map<String, Object> map){
		this.setBaiduMapInfo(map);
		//1. 从数据库取数据
		List<Map<String,Object>> cableSections = gisManagerMapper.getCableSections();
		//2. 过滤不在当前显示区域的数据
		Iterator<Map<String,Object>> iterator = cableSections.iterator();
	    while(iterator.hasNext()){
	    	Map<String,Object> e = iterator.next();
	        if(!isCableVisible(e)){
	        	iterator.remove();
	        }
	    }
	    //3. 按传输系统过滤
	    String transSysId = null;
	    if(map.get("transSysId") !="" && map.get("transSysId") != null){
	    	transSysId = map.get("transSysId").toString();
	    }

	    if(transSysId != null && !transSysId.equals("")){
	    	Map<String,Object> stationsInSys = getTransSysStations(transSysId);
	    	iterator = cableSections.iterator();
	    	while(iterator.hasNext()){
		    	Map<String,Object> e = iterator.next();
		        if(!isCableSectionInTransSys(e,stationsInSys)){
		        	iterator.remove();
		        }
		    }
	    }
	    //4. 处理多条光缆段映射到一个line，传递到前台的数据中两点确定唯一一条line
		return filterDataForGis(cableSections);		
	}
	
	private List<Map<String,Object>> filterDataForGis(List<Map<String,Object>> cableSections){
		int lineId = 0;
		List<Map<String,Object>> lines = new ArrayList<Map<String,Object>>();
		List<String> routes = new ArrayList<String>();
		Map<String,String> routeMap = new HashMap<String,String>();
		Map<String,String> breakpointMap = new HashMap<String,String>();
		Map<String,String> cableNameMap = new HashMap<String,String>();
		for(Map<String,Object> cableSection : cableSections){
			String cableSectionId = cableSection.get("CABLE_SECTION_ID").toString();
			String cableSectionName = cableSection.get("CABLE_SECTION_NAME").toString();
			String start = cableSection.get("A_END").toString();
			String end = cableSection.get("Z_END").toString();
			// 防止中断点信息为空值
			String breakpoint = cableSection.get("BREAKPOINT")==null ? "0" : cableSection.get("BREAKPOINT").toString();
			// 在有效断点标记后附加光缆段ID信息，以便区分断点
			if (!breakpoint.equals("0"))
				breakpoint = breakpoint + "," + cableSectionId;
			String route = start+"-"+end;
			boolean existed = false; 
			for(String r:routes){
				if((start+"-"+end).equals(r) || (end+"-"+start).equals(r)){
					route = r;
					existed = true;
					break;
				}			
			}
			if(existed){
				String value = routeMap.get(route)+","+cableSectionId;
				if(!breakpoint.equals("0")){
					String breakpoints = breakpointMap.get(route)+"/"+breakpoint;
					breakpointMap.put(route, breakpoints);
				}
				String name = cableNameMap.get(route)+","+cableSectionName;
					
				routeMap.put(route, value);
				cableNameMap.put(route, name);
				continue;
			}else{
				routeMap.put(route, cableSectionId);
				breakpointMap.put(route, breakpoint);
				routes.add(route);
				cableNameMap.put(route, cableSectionName);
			}
			lineId++;
			Map<String,Object> line = new HashMap<String,Object>();
			line.put("lineId",lineId);
			List<Map<String,Object>> startAndEnd = gisManagerMapper.getStartAndEndLngLat(cableSectionId);
			line.put("start",startAndEnd.get(0));
			line.put("end",startAndEnd.get(1));
			line.put("route",route);
			lines.add(line);
		}
		
		for(Map<String,Object> line:lines){
			line.put("cableSectionIds", routeMap.get(line.get("route").toString()));
			line.put("breakpoints", breakpointMap.get(line.get("route").toString()));
			line.put("cableSectionNames", cableNameMap.get(line.get("route").toString()));
//			int strategy = Integer.valueOf(baiduMapInfo.get("strategy").toString());
//			int state = 0;
//			if(strategy == 0){
//				state = this.getLineTestingState(line.get("cableSectionIds").toString());
//			}else if(strategy == 1){
//				state = this.getLineAlarmState(line.get("cableSectionIds").toString());
//			}else if(strategy == 2){
//				state = this.getLineRouteCoverState(line.get("cableSectionIds").toString());
//			}
//			line.put("state", state);
		}
		return lines;
	}
	
	private Map<String,Object> getTransSysStations(String transSysId){
		List<Map> list = transSystemMapper.getRelStationByTranSysId(Integer.valueOf(transSysId));
		Map<String,Object> stationMap = new HashMap<String,Object>();
		for(Map o:list){
			String stationId = o.get("RESOURCE_STATION_ID").toString();
			stationMap.put(stationId, transSysId);
		}
		return stationMap;
	}
	
	//判断光缆段是否在传输系统内
	//有改进需要，一次查询数据库，比较所有在传输系统内的光缆段
	private boolean isCableSectionInTransSys(Map<String,Object> cableSection, Map<String, Object> stationsInSys){
		boolean result = false;
		String A_END = cableSection.get("A_END").toString(),
				Z_END = cableSection.get("Z_END").toString();
		if(stationsInSys.containsKey(A_END) && stationsInSys.containsKey(Z_END)){
			result = true;
		}
		return result;
	}
	
	//判断光缆段是否可见
	private boolean isCableVisible(Map<String,Object> cableSection){
		boolean flag = false;
		String cableSectionId = cableSection.get("CABLE_SECTION_ID").toString();
		if(cableSectionId == null){
			return false;
		}
		List<Map<String,Object>> startAndEnd = gisManagerMapper.getStartAndEndLngLat(cableSectionId);
		if(containsPoint(startAndEnd.get(0)) || containsPoint(startAndEnd.get(1))){
			flag = true;
		}
		return flag;
	}
	
	//判断点是否在可视区域内
	private boolean containsPoint(Map<String,Object> point){
		boolean visible = false;
		// 防止局站未设置经纬度信息导致的异常
		if (point.get("LNG")==null || point.get("LAT")==null) {
			return visible;
		}
		double lng = Double.valueOf(point.get("LNG").toString()), 
				lat = Double.valueOf(point.get("LAT").toString());
		double minLat = Double.valueOf(baiduMapInfo.get("minLat").toString()),
				maxLat = Double.valueOf(baiduMapInfo.get("maxLat").toString()),
				minLng = Double.valueOf(baiduMapInfo.get("minLng").toString()),
				maxLng = Double.valueOf(baiduMapInfo.get("maxLng").toString());
		if(lng >= minLng && lng <= maxLng &&
				lat >= minLat && lat<= maxLat){
			visible = true;
		}
		return visible;
	}

	@Override
	public List<Map<String,Object>> getTestRoutesByAZ(Map<String, Object> map) {
		if(map.isEmpty()){
			return null;
		}
		List<Map<String,Object>> cableSections = gisManagerMapper.getCableSectionsThroughAZ(map);
		
		List<Map<String,Object>> datalist = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> cableSection : cableSections){
			String cableSectionId = cableSection.get("CABLE_SECTION_ID").toString();
			List<Map<String,Object>> testRoutes = gisManagerMapper.getTestRoutesByCsId(cableSectionId);
			Map<String,Object> routeInfo = new HashMap<String,Object>();
			routeInfo.put("testRouteList",testRoutes);
			routeInfo.putAll(cableSection);
			datalist.add(routeInfo);			
		}
		return datalist;
	}
	
	private int getLineRouteCoverState(String cableSectionIds){
		int state = CommonDefine.NO_COVERD_BY_ROUTE;		
		int num = 0; //统计有测试路由的光缆段数
		String[] csIds = cableSectionIds.split(",");
		for(String csId : csIds){
			List<Map<String,Object>> testRoutes = gisManagerMapper.getTestRoutesByCsId(csId);
			if(testRoutes.size() != 0){
				num++;
			}		
		}
		int cs_num = csIds.length;
		if(num == cs_num){
			state = CommonDefine.ALL_COVERED_BY_ROUTE;
		}else if(num < cs_num && num != 0){
			state = CommonDefine.SOME_COVERED_BY_ROUTE;
		}
		return state;
	}
	
	private int getCableSectionAlarmState(String cableSectionId){
		return 4;
	}
	
	private List<Map<String, Object>> getStationsAlarmState(List<String> stationIds){
		
		return null;
	}
	
	private int getStationAlarmState(String stationId){
		return 3;
	}
	
	/**
	 * 获取gis上一条光缆段的测试状态，需要检测经过A_END和Z_END
	 * 之间所有光缆段的测试状态。因为地图上只是标记两点之间有光缆
	 * 并不能唯一确定光缆段，多条光缆段共用一条line的情况是很
	 * 有可能的。
	 */
	private int getLineTestingState(String cableSectionIds){
		int state = CommonDefine.LINE_ORDINARY;
		String[] csIds = cableSectionIds.split(",");
		for(String csId : csIds){
			List<Map<String,Object>> testingRoutes = gisManagerMapper.getTestingRoutesByCsId(csId);	
			if(testingRoutes.size() != 0){
				state = CommonDefine.LINE_TESTING;				
				break;
			}
		}
		return state;
	}
	
	private List<Map<String, Object>> getLinesTestingState(String[] lines){
		List<Map<String, Object>> lineStateList = new ArrayList<Map<String, Object>>();				
		List<Map<String,Object>> testingRoutes = gisManagerMapper.getAllTestingRoutes();
		//组合hash
		Map<String,String> testingCSmap = new HashMap<String,String>();
		for(Map<String,Object> r:testingRoutes){
			String routeId = r.get("TEST_ROUTE_ID").toString();
			String[] cableIds= r.get("CABLE_IDS").toString().split(",");
			for(String id :cableIds){
				testingCSmap.put(id, routeId);
			}
		}
		
		for(String line:lines){
			String lineId = line.substring(0, line.indexOf(":"));
			String[] csIds = line.substring(line.indexOf(":")+1).split(",");
			Map<String,Object> _map = new HashMap<String,Object>();
			int state = CommonDefine.LINE_ORDINARY;
			_map.put("lineId", lineId);
			_map.put("state", state);
			for(String csId : csIds){
				if(testingCSmap.containsKey(csId)){
					state = CommonDefine.LINE_TESTING;
					_map.put("state", state);
					break;
				}
			}
			lineStateList.add(_map);
		}
		return lineStateList;
	}
	
	private List<Map<String, Object>> getLinesAlarmState(String[] lines) throws CommonException{
		List<Map<String, Object>> lineStateList = new ArrayList<Map<String, Object>>();
		String allCsIds = lines[0].substring(lines[0].indexOf(":")+1);
		for(String line:lines){
			allCsIds += ","+line.substring(line.indexOf(":")+1);
		}
		List<String> csIdsList = Arrays.asList(allCsIds.split(","));
		Map<String,Integer> csAlarmStates = alarmManagementService.getCableSectionAlarmState(csIdsList);
		for(String line:lines){
			int state = getLineAlarmState(line,csAlarmStates);
			Map<String, Object> _map = new HashMap<String, Object>();
			String lineId = line.substring(0, line.indexOf(":"));
			_map.put("lineId", lineId);
			_map.put("state", state);
			lineStateList.add(_map);
		}
		return lineStateList;
	}	

	private int getLineAlarmState(String line,Map<String,Integer> csAlarmStates){
		int state = CommonDefine.LINE_ORDINARY;
		String[] cableSectionIds = line.substring(line.indexOf(":")+1).split(",");
		// 防止告警状态Map为空时后续代码出现异常
		if (csAlarmStates.isEmpty()) {
			return state;
		}
		for(String csId:cableSectionIds){
			if(csAlarmStates.get(csId) != null &&
					csAlarmStates.get(csId) == CommonDefine.LINE_WITH_ALARM){
				state = CommonDefine.LINE_WITH_ALARM;
				break;
			}
		}
		return state;
	}
	
	@Override
	public Map<String, Object> displayDataByStrategy(Map<String, Object> map) {
		if(map.isEmpty()){
			return null;
		}
		int strategy = Integer.valueOf(map.get("strategy").toString());
		String[] lines = map.get("lines").toString().split("/");
		String[] stationIds = map.get("stationIds").toString().replace("[","").replace("]","").split(",");
		map.clear();
		List<Map<String, Object>> lineStateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> stationStateList = new ArrayList<Map<String, Object>>();
		if(strategy == 0){
//			for(String line:lines){
//				String lineId = line.substring(0, line.indexOf(":"));
//				String cableSectionIds = line.substring(line.indexOf(":")+1);
//				int state = getLineTestingState(cableSectionIds);
//				Map<String, Object> _map = new HashMap<String, Object>();
//				_map.put("lineId", lineId);
//				_map.put("state", state);
//				lineStateList.add(_map);
//			}
			
			lineStateList = getLinesTestingState(lines);
						
			for(String stationId:stationIds){
				Map<String, Object> _map = new HashMap<String, Object>();
				_map.put("stationId", stationId);
				_map.put("state", 0);
				_map.put("type", getStationType(stationId));
				stationStateList.add(_map);
			}
			
		}else if(strategy == 1){
			try {
					Map<String, Integer> stationStates = 
							alarmManagementService.getStationAlarmState(Arrays.asList(stationIds));
					for(String stationId:stationIds){
						Map<String, Object> _map = new HashMap<String, Object>();
						_map.put("stationId", stationId);
						_map.put("state", stationStates.get(stationId)==null ? 0 : stationStates.get(stationId));
						_map.put("type", getStationType(stationId));
						stationStateList.add(_map);
					}							
					lineStateList = this.getLinesAlarmState(lines);
			} catch (CommonException e) {
				e.printStackTrace();
			} 
		}else{
			for(String stationId:stationIds){
				Map<String, Object> _map = new HashMap<String, Object>();
				_map.put("stationId", stationId);
				_map.put("state", 0);
				_map.put("type", getStationType(stationId));
				stationStateList.add(_map);
			}
			
			for(String line:lines){
				String lineId = line.substring(0, line.indexOf(":"));
				String cableSectionIds = line.substring(line.indexOf(":")+1);
				int state = getLineRouteCoverState(cableSectionIds);
				Map<String, Object> _map = new HashMap<String, Object>();
				_map.put("lineId", lineId);
				_map.put("state", state);
				lineStateList.add(_map);
			}
		}
		map.put("lineStates", lineStateList);
		map.put("stationStates", stationStateList);
		return map;
	}
	
//	//测试结果分析之断点分析
//	public JSONObject testResultAnalysisOnBreakpoint(String testResultId){
//		Map<String,Object> info = gisManagerMapper.getBreakpointInfo(testResultId);	
//		double location = Double.valueOf(info.get("LOCATION").toString());
//		String route = info.get("CABLE_SECTION_IDS").toString(); 
//		String[] cableSectionIds = route.split(",");
//		double _length = 0; //测试路由由起点到所在光缆段的长度
//		double cs_length = 0; //断点所在光缆段的长度
//		String breakCSid = "";
//		for(String csId : cableSectionIds){
//			Map<String,Object> cs = gisManagerMapper.getCableSectionById(csId);
//			cs_length = Double.valueOf(cs.get("CABLE_SECTION_LENGTH").toString());
//			_length += cs_length;
//			if(_length >= location){
//				breakCSid = csId;
//				break;
//			}			
//		}
//		double ratio = (cs_length-(_length-location))/cs_length ;
//		List<Map<String,Object>> startAndEnd = gisManagerMapper.getStartAndEndLngLat(breakCSid);
//		double startLng = Double.valueOf(startAndEnd.get(0).get("LNG").toString()),
//			   startLat = Double.valueOf(startAndEnd.get(0).get("LAT").toString()),
//			   endLng = Double.valueOf(startAndEnd.get(1).get("LNG").toString()),
//			   endLat = Double.valueOf(startAndEnd.get(1).get("LAT").toString());
//		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.0000");
//		String lng = df.format((1-ratio)*startLng + ratio*endLng),
//				lat = df.format((1-ratio)*startLat + ratio*endLat);
//		
//		Map<String,Object> breakpoint = new HashMap<String,Object>();
//		breakpoint.put("dataType", 0);
//		breakpoint.put("lng", lng);
//		breakpoint.put("lat", lat);
//		breakpoint.put("msg", "我是断点");
//		//保存断点到该光缆段
//		String breakpointStr = lng+","+lat;
//		gisManagerMapper.addBreakpointToCableSection(breakpointStr, breakCSid);
//		JSONObject result = JSONObject.fromObject(breakpoint);
//		WebMsgPush.updateGisMap(result);
//		return result;
//	}

	@Override
	public List<Map<String, Object>> getStationsNotInCable(Map<String, Object> map) {
		this.setBaiduMapInfo(map);
		List<Map> list = new ArrayList<Map>();
		List<Map<String, Object>> stationList = gisManagerMapper.getStationsNotInCable(map);
		//3. 按传输系统过滤
	    String transSysId = null;
	    if(map.get("transSysId") !="" && map.get("transSysId") != null){
	    	transSysId = map.get("transSysId").toString();
	    }

	    if(transSysId != null && !transSysId.equals("")){
	    	Map<String,Object> stationsInSys = getTransSysStations(transSysId);
	    	Iterator<Map<String, Object>> iterator = stationList.iterator();
	    	while(iterator.hasNext()){
		    	Map<String,Object> e = iterator.next();
		        if(!stationsInSys.containsKey(e.get("ID").toString())){
		        	iterator.remove();
		        }
		    }
	    }
		return stationList;
	}

	@Override
	public CommonResult doTest(Map<String, Object> map) {
		
		if(service == null){
			service = (IWSManagerService) SpringContextUtil.getBean("fttsWsClient");
		}
		CommonResult result = null;
		CommonResult tempRlt = null;
		try {		
			String routeId = map.get("ROUTE_ID").toString();
			String[] ids = routeId.split(",");
			// 多个测试路由
			if (ids.length > 1) {
				for (int i=0; i<ids.length;i++) {
					// 调用接口方法
					result = service.runTest(ids[i], null, CommonDefine.COLLECT_LEVEL_3);
					if (result.getReturnResult()==CommonDefine.FAILED) {
						tempRlt = result;
					}
					// 增加10S间隔
					Thread.sleep(10*1000);
				}
				if (tempRlt != null)
					result = tempRlt;
			// 单个测试路由
			} else {
				//填入参数
				StringBuilder sb = new StringBuilder();
				sb.append("<XML>");
				sb.append("<OTDR_WAVE_LENGTH>");
				sb.append(map.get("OTDR_WAVE_LENGTH").toString());
				sb.append("</OTDR_WAVE_LENGTH>");
				sb.append("<OTDR_RANGE>");
				sb.append(map.get("OTDR_RANGE").toString());
				sb.append("</OTDR_RANGE>");
				sb.append("<OTDR_PLUSE_WIDTH>");
				sb.append(map.get("OTDR_PLUSE_WIDTH").toString());
				sb.append("</OTDR_PLUSE_WIDTH>");
				sb.append("<OTDR_TEST_DURATION>");
				sb.append(map.get("OTDR_TEST_DURATION").toString());
				sb.append("</OTDR_TEST_DURATION>");
				sb.append("<OTDR_REFRACT_COEFFICIENT>");
				sb.append(map.get("OTDR_REFRACT_COEFFICIENT").toString());
				sb.append("</OTDR_REFRACT_COEFFICIENT>");
				sb.append("</XML>");
				// 调用接口方法
				result = service.runTest(routeId, sb.toString(), CommonDefine.COLLECT_LEVEL_3);			
			}

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Map<String, Object> getTestParamById(Map<String, Object> map) {
		Map<String, Object> testRoute = null;
		
		int testRouteId = Integer.valueOf(map.get("ROUTE_ID").toString());
		testRoute = commonMapper.selectTableById("t_ftts_test_route", "TEST_ROUTE_ID", testRouteId);
		
		return testRoute;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> getWaveLengthList(Map map) throws CommonException 
	{
		List waveLengthList = new ArrayList();
		int routeId = Integer.valueOf(map.get("routeId").toString());
		// 获取测试设备ID
		int	rcId = getRcId(routeId);
		
		// 获取OTDR单元盘信息
		Map<String,Object> otdrUnit = getOtdrUnitInfo(rcId);
		
		// 获取波长信息
		String waveLength = otdrUnit.get("WAVE_LEN").toString();
		
		// 目前多波长的分隔字符符为"/"
		if (waveLength.indexOf("/") > 0) {
			String[] waveLengthArr = waveLength.split("/");
			for(String wavelen : waveLengthArr){
				Map w_map = new HashMap();
				w_map.put("WAVE_LENGTH", wavelen);
				waveLengthList.add(w_map);
			}
		} else {
			Map w_map = new HashMap();
			w_map.put("WAVE_LENGTH", waveLength);
			waveLengthList.add(w_map);
		}
		
		return waveLengthList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> getRangeList(Map map) throws CommonException 
	{
		List<Map> rangeMapList = new ArrayList();
		
		int routeId = Integer.valueOf(map.get("routeId").toString());
		// 获取测试设备ID
		int	rcId = getRcId(routeId);
		
		// 获取OTDR单元盘信息
		Map<String,Object> otdrUnit = getOtdrUnitInfo(rcId);
		
		String otdrType = otdrUnit.get("OTDR_TYPE").toString();
		if ("国产34所".equals(otdrType)) {
			Map w_map = new HashMap();
			w_map.put("OTDR_TYPE", 2);
			rangeMapList = gisManagerMapper.getRangeList(w_map);
		} else {
			Map w_map = new HashMap();
			w_map.put("OTDR_TYPE", 1);
			rangeMapList = gisManagerMapper.getRangeList(w_map);
		}
		
		return rangeMapList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> getPluseWidthList(Map map) throws CommonException 
	{
		List<Map> pluseWidthMapList = new ArrayList();
		
		int routeId = Integer.valueOf(map.get("routeId").toString());
		// 获取测试设备ID
		int	rcId = getRcId(routeId);
		
		// 获取OTDR单元盘信息
		Map<String,Object> otdrUnit = getOtdrUnitInfo(rcId);
		
		String otdrType = otdrUnit.get("OTDR_TYPE").toString();
		if ("国产34所".equals(otdrType)) {
			map.put("OTDR_TYPE", 2);
			pluseWidthMapList = gisManagerMapper.getPluseWidthList(map);
		} else {
			map.put("OTDR_TYPE", 1);
			pluseWidthMapList = gisManagerMapper.getPluseWidthList(map);
		}
		
		return pluseWidthMapList;
	}
	
	// 获取特定测试路由的测试设备ID
	private int getRcId(int testRouteId) {
		int rcId = 0;		
		// 获取测试路由
		Map routeMap = commonMapper.selectTableById("t_ftts_test_route", "TEST_ROUTE_ID", testRouteId);
		// 测试设备信息（格式：测试设备ID-槽位号-端口号）
		String testInfo = routeMap.get("TEST_EQPT_INFO").toString();

		if (testInfo.indexOf("-") > 0){
			rcId = Integer.valueOf(testInfo.split("-")[0]);
		}
		return rcId;
	}
	
	// 获取指定测试设备的OTDR单元盘信息
	private Map<String,Object> getOtdrUnitInfo(int rcId) {
		Map<String,Object> unitMap = new HashMap<String,Object>();
		// 获取指定测试设备的单元盘信息
		List<Map> unitList = commonMapper.selectTableListById("t_ftts_unit", "RC_ID", rcId, 0, null);
		
		if (unitList != null && unitList.size()>0){
			for (Map<String, Object> unit : unitList) {
				// 通过OTDR单元盘获取波长信息
				if ("OTDR".equals(unit.get("NAME"))) {
					unitMap = unit;
					break;
				}
			}
		}
		return unitMap;
	}
	
	// 获取指定局站的类型
	private int getStationType(String stationId) {
		int id = Integer.valueOf(stationId);
		Map<String,Object> staMap = commonMapper.selectTableById("t_resource_station", "RESOURCE_STATION_ID", id);
		int type = staMap.get("TYPE")!=null?Integer.valueOf(staMap.get("TYPE").toString()):0;
		return type;
	}
}
