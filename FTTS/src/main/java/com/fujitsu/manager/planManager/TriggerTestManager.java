package com.fujitsu.manager.planManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.GisManagerMapper;
import com.fujitsu.dao.mysql.PlanMapper;

/**
 * @Description：触发测试
 * @author cao senrong
 * @date 2015-1-21
 * @version V1.0
 */
@Service
public class TriggerTestManager {
	
	@Resource
	private PlanMapper planMapper;
	@Resource
	private TestManagement testManagement;
	@Resource
	private GisManagerMapper gisManagerMapper;
	
	static List<String> testRouteIdList = new ArrayList<String>();
	
	public TriggerTestManager() {
//		planMapper = (PlanMapper) SpringContextUtil.getBean("planMapper");
//		testManagement = (TestManagement) SpringContextUtil.getBean("testManagement");
		// 创建测试线程
		Runnable r = new Runnable() {
			public void run() {
				while (true) {
					try {
						//有路由才测试
						while (testRouteIdList.size() > 0) {
							String id = testRouteIdList.get(0);
							testManagement.runTest(id, null, CommonDefine.COLLECT_LEVEL_2);
							System.out.println("触发测试正在测试"+id+"路由");
							testRouteIdList.remove(id);
							//随机延时7秒以内
							Random random = new Random();
							Thread.sleep((random.nextInt(5)+2)*1000);
						}
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
	}
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private String ftspAlarmAnalyse(Map alarmMap){
//		String routeId = "";
//		if(alarmMap.get("PTP_ID") != null){
//			String ptp_id = alarmMap.get("PTP_ID").toString();
//			String ne_name = alarmMap.get("NE_NAME").toString();
//			String rack_no = alarmMap.get("RACK_NO").toString();
//			String shelf_no = alarmMap.get("SHELF_NO").toString();
//			String slot_no = alarmMap.get("SLOT_NO").toString();
//			String port_no = alarmMap.get("PORT_NO").toString();
//			String alarm_name = alarmMap.get("NATIVE_PROBABLE_CAUSE").toString();
//			
//			if(!"".equals(ptp_id) && !"".equals(ne_name) 
//					&& !"".equals(slot_no) && !"".equals(port_no) 
//					&& !"".equals(alarm_name)){
//				
//				Map map = new HashMap();
//				map.put("PTP_ID", ptp_id);
//				map.put("NE_NAME", ne_name);
//				map.put("RACK_NO", rack_no);
//				map.put("SHELF_NO", shelf_no);
//				map.put("SLOT_NO", slot_no);
//				map.put("PORT_NO", port_no);
//				map.put("ALARM_NAME", alarm_name);
//				
//				Map routeMap = planMapper.getRouteByAlarmInfo(map);
//				if(routeMap != null && routeMap.get("ROUTE_ID") != null ){
//					routeId = routeMap.get("ROUTE_ID").toString();
//				}
//			}
//		}
//		
//		return routeId;
//	}
	
	private void alarmMsgProc(Map almMap) {
		int alarmType = (Integer) almMap.get("ALARM_TYPE");
		// 判断是否是PTP相关告警
		if (alarmType != CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT &&
				almMap.get("PTP_ID") != null) {
			
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("PTP_ID", almMap.get("PTP_ID").toString());
			param.put("ALARM_NAME", almMap.get("NATIVE_PROBABLE_CAUSE").toString());
			
			Map routeMap = planMapper.getRouteByAlarmInfo(param);
			if(routeMap != null && routeMap.get("CABLE_ID") != null ){
				Map<String,String> testMap = new HashMap<String,String>();
				String cableId = routeMap.get("CABLE_ID").toString();
				// 以CableId查询测试路由表，返回一个或多个测试路由
				List<Map<String,Object>> testRouteLst = gisManagerMapper.getTestRoutesByCsId(cableId);
				for (Map<String,Object> testRoute : testRouteLst) {
					String[]cableIdArray = testRoute.get("CABLE_IDS").toString().split(",");
					String[] routeNameArray = testRoute.get("ROUTE_NAME").toString().split("-");
					String routeId = testRoute.get("TEST_ROUTE_ID").toString();
					String routeName="";
					for (int i=0; i<cableIdArray.length; i++) {
						if (cableIdArray[i].equals(cableId)) {
							routeName = routeNameArray[i]; 
						}
					}
					// routeName的局部格式为“光缆段名(光纤号)”
					String fiberNo = routeName.substring(routeName.indexOf("(")+1,routeName.length()-1);
					
					StringBuilder sb = new StringBuilder();
					// 以“光缆段ID-光纤号”的格式组成key存储测试路由ID
					sb.append(cableId).append("-").append(fiberNo);
					testMap.put(sb.toString(), routeId);
				}
				// 将需要进行测试的路由放入列表（已经去除重复路由）
				for (String value : testMap.values()) {
					addTestRouteId(value);
				}
			}
		}
	}
	
	public void addTestRouteId(String id) {
		synchronized (testRouteIdList) {
			if (!testRouteIdList.contains(id)) {
				testRouteIdList.add(id);
			}			
		}
	}
	
	public void call(Map alarmMap){

		//判断是否测试			
		Map alarmListenMap = planMapper.getSysParam("ALARM_TRIGGER_TEST");
		if (alarmListenMap.get("PARAM_VALUE") == null)
			return;	
		
		int alarmListen = Integer.parseInt(alarmListenMap.get("PARAM_VALUE").toString());
		if (CommonDefine.ALARM_TRIGGER_ON == alarmListen) {
			//解析告警
			alarmMsgProc(alarmMap);
		}
	}
	
}
