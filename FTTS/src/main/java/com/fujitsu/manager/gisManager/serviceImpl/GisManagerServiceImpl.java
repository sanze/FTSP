package com.fujitsu.manager.gisManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.springframework.stereotype.Service;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.GisManagerMapper;
import com.fujitsu.manager.gisManager.service.GisManagerService;
import com.fujitsu.util.dwr.MyScriptSessionListener;

@Service
public class GisManagerServiceImpl extends GisManagerService{
	
	@Resource
	public GisManagerMapper gisManagerMapper;
	
	private Map<String,Double> bounds = new HashMap<String,Double>();
	
	private void setBounds(Map<String, Object> map){
		if(!map.isEmpty()){		
			bounds.put("zoom", Double.valueOf(map.get("zoom").toString()));
			bounds.put("maxLat", Double.valueOf(map.get("maxLat").toString()));
			bounds.put("maxLng", Double.valueOf(map.get("maxLng").toString()));
			//System.out.println(bounds.getMaxLng()+"   "+bounds.getMaxLat());
			bounds.put("minLat", Double.valueOf(map.get("minLat").toString()));
			bounds.put("minLng", Double.valueOf(map.get("minLng").toString()));
			//System.out.println(bounds.getMinLng()+"   "+bounds.getMinLat());
		}
	}
	
	//获取GIS数据
	
	public List<Map<String,Object>> getGisResourceData(Map<String, Object> map){
		this.setBounds(map);
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
	    //3. 处理多条光缆段映射到一个line，传递到前台的数据中两点确定唯一一条line
		return filterDataForGis(cableSections);		
	}
	
	private List<Map<String,Object>> filterDataForGis(List<Map<String,Object>> cableSections){
		int lineId = 0;
		List<Map<String,Object>> lines = new ArrayList<Map<String,Object>>();
		List<String> routes = new ArrayList<String>();
		Map<String,String> routeMap = new HashMap<String,String>();
		Map<String,String> breakpointMap = new HashMap<String,String>();
		for(Map<String,Object> cableSection : cableSections){
			String cableSectionId = cableSection.get("CABLE_SECTION_ID").toString();
			String start = cableSection.get("A_END").toString();
			String end = cableSection.get("Z_END").toString();
			String breakpoint = cableSection.get("BREAKPOINT").toString();
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
					
				routeMap.put(route, value);
				continue;
			}else{
				routeMap.put(route, cableSectionId);
				breakpointMap.put(route, breakpoint);
				routes.add(route);
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
			int state = this.getLineTestingState(line.get("cableSectionIds").toString());
			line.put("state", state);
		}
		return lines;
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
			//局站在可视区域内，则去获取该局站的告警级别
//			int alarmLevel = 0;
//			startAndEnd.get(0).put("alarmLevel", alarmLevel);
//			startAndEnd.get(1).put("alarmLevel", alarmLevel);
//			//如果该光缆段在可视区域内，则将光缆段中的起止点标志ID换成经纬度
//			cableSection.put("A_END", startAndEnd.get(0));
//			cableSection.put("Z_END", startAndEnd.get(1));
		}
		return flag;
	}
	
	//判断点是否在可视区域内
	private boolean containsPoint(Map<String,Object> point){
		boolean visible = false;
		double lng = Double.valueOf(point.get("LNG").toString()), 
				lat = Double.valueOf(point.get("LAT").toString());
		double minLat = bounds.get("minLat"),maxLat = bounds.get("maxLat"),
				minLng = bounds.get("minLng"),maxLng = bounds.get("maxLng");
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
//		List<Map<String,Object>> testRoutes = gisManagerMapper.getTestRoutes();
		
		List<Map<String,Object>> datalist = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> cableSection : cableSections){
			String cableSectionId = cableSection.get("CABLE_SECTION_ID").toString();
			List<Map<String,Object>> testRoutes = gisManagerMapper.getTestRoutesByCsId(cableSectionId);
			Map<String,Object> routeInfo = new HashMap<String,Object>();
			routeInfo.put("testRouteId", "");
			if(testRoutes.size() != 0){
				routeInfo.put("testRouteId", testRoutes.get(0).get("TEST_ROUTE_ID"));
			}
			routeInfo.putAll(cableSection);
			datalist.add(routeInfo);			
		}
		return datalist;
	}

	private int getLineRouteCoverState(String cableSectionIds){
		int state = CommonDefine.LINE_ORDINARY;		
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
		}else if(cs_num > num){
			state = CommonDefine.SOME_COVERED_BY_ROUTE;
		}
		return state;
	}
	
	private int getCableSectionAlarmState(String cableSectionId){
		return 4;
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
	
	private int getLineAlarmState(String line){
		int state = CommonDefine.LINE_ORDINARY;
		String[] cableSectionIds = line.substring(line.indexOf(":")+1).split(",");
		for(String csId:cableSectionIds){
			if(this.getCableSectionAlarmState(csId) == CommonDefine.LINE_WITH_ALARM){
				state = CommonDefine.LINE_WITH_ALARM;
				break;
			}
		}
		return state;
	}
	
	@Override
	public Map<String, Object> displayDataByStrategy(Map<String, Object> map) {
//		testResultAnalysisOnBreakpoint("1");
		return null;
//		if(map.isEmpty()){
//			return null;
//		}
//		int strategy = Integer.valueOf(map.get("strategy").toString());
//		String[] lines = map.get("lines").toString().split("/");
//		String[] stationIds = map.get("stationIds").toString().replace("[","").replace("]","").split(",");
//		map.clear();
//		List<Map<String, Object>> lineStateList = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> stationStateList = new ArrayList<Map<String, Object>>();
//		if(strategy == 0){
//			for(String line:lines){
//				String lineId = line.substring(0, line.indexOf(":"));
//				String cableSectionIds = line.substring(line.indexOf(":")+1);
//				int state = getLineTestingState(cableSectionIds);
//				Map<String, Object> _map = new HashMap<String, Object>();
//				_map.put("lineId", lineId);
//				_map.put("state", state);
//				lineStateList.add(_map);
//			}
//						
//			for(String stationId:stationIds){
//				Map<String, Object> _map = new HashMap<String, Object>();
//				_map.put("stationId", stationId);
//				_map.put("state", 0);
//				stationStateList.add(_map);
//			}
//			
//		}else if(strategy == 1){
//			Random random = new Random();
//			for(String stationId:stationIds){
//				Map<String, Object> _map = new HashMap<String, Object>();
//				_map.put("stationId", stationId);
//				_map.put("state", random.nextInt(3));
//				stationStateList.add(_map);
//			}
//						
//			for(String line:lines){
//				int state = getLineAlarmState(line);
//				Map<String, Object> _map = new HashMap<String, Object>();
//				String lineId = line.substring(0, line.indexOf(":"));
//				_map.put("lineId", lineId);
//				_map.put("state", state);
//				lineStateList.add(_map);
//			}
//		}else{
//			for(String stationId:stationIds){
//				Map<String, Object> _map = new HashMap<String, Object>();
//				_map.put("stationId", stationId);
//				_map.put("state", 0);
//				stationStateList.add(_map);
//			}
//			
//			for(String line:lines){
//				String lineId = line.substring(0, line.indexOf(":"));
//				String cableSectionIds = line.substring(line.indexOf(":")+1);
//				int state = getLineRouteCoverState(cableSectionIds);
//				Map<String, Object> _map = new HashMap<String, Object>();
//				_map.put("lineId", lineId);
//				_map.put("state", state);
//				lineStateList.add(_map);
//			}
//		}
//		map.put("lineStates", lineStateList);
//		map.put("stationStates", stationStateList);
//		return map;
	}
	
	//推送
	public boolean updateGisMap(JSONObject info){
		boolean result = true;    	
		try {          	
			ScriptBuffer script = new ScriptBuffer();
			script.appendScript("updateGisMap(").appendData(info).appendScript(")");
			//得到登录此页面的scriptSession的集合
			String currentPage = "/FTTS/jsp/gis/gisMap.jsp";
			Set<String> keys = MyScriptSessionListener.scriptSessionMap.keySet();
			for(String key : keys){
				if(key.contains(currentPage)){
					ScriptSession curSS = MyScriptSessionListener.scriptSessionMap.get(key);			
					if(curSS != null){
						curSS.addScript(script);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}        
		return result;
	}
	
//	//测试结果分析之断点分析
//	public JSONObject testResultAnalysisOnBreakpoint(String testResultId){
//		testResultId = "1";
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
//		gisManagerMapper.addBreakpointToCableSection(lng, breakCSid);
//		JSONObject result = JSONObject.fromObject(breakpoint);
//		updateGisMap(result);
//		return result;
//	}
	
}
