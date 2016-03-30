package com.fujitsu.manager.planManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonMapper;
import com.fujitsu.dao.mysql.PlanMapper;
import com.fujitsu.manager.planManager.service.PlanManagerService;


@Service
@Transactional(rollbackFor = Exception.class)
public class PlanManagerServiceImpl extends PlanManagerService {
	@Resource
	private PlanMapper planMapper;
	@Resource
	private CommonMapper commonMapper;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> getPlanList(Map map) throws CommonException {
		List<Map> planList = planMapper.getPlanList(map);
		for(int i=0;i<planList.size();i++){
			Map planMap = planList.get(i);
			String time = planMap.get("START_TIME").toString();
			planMap.put("START_TIME", time);
		}
		return planList;
	}

	public List<Map> getRouteListByPlanId(Map map) throws CommonException {
		List<Map> routeList = planMapper.getRouteListByPlanId(map);
		
		return routeList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateTestPlanStatusStartUp(Map map) throws CommonException {
		String planIds = map.get("planIds").toString();
		List planIdList = new ArrayList();
		String[] planIdArr = planIds.split(",");
		for(int i=0;i<planIdArr.length;i++){
			planIdList.add(planIdArr[i]);
		}
		planMapper.updateTestPlanStatusStartUp(planIdList);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateTestPlanStatusPending(Map map) throws CommonException {
		String planIds = map.get("planIds").toString();
		List planIdList = new ArrayList();
		String[] planIdArr = planIds.split(",");
		for(int i=0;i<planIdArr.length;i++){
			planIdList.add(planIdArr[i]);
		}
		planMapper.updateTestPlanStatusPending(planIdList);
		
	}
	
	public void modifyTestRoutePara(Map map) throws CommonException {
		planMapper.updateTestRoutePara(map);
	}

	public void modifyTestRouteValueBatch(List<Map> routeList) throws CommonException {
		for (Map routeMap : routeList) {
			planMapper.updateTestRoutePeriod(routeMap);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> getWaveLengthList(Map map) throws CommonException 
	{
		List waveLengthList = new ArrayList();
		String routeId = map.get("routeId").toString();
		Map routeMap = planMapper.getRouteById(routeId);
		
		String testInfo = routeMap.get("TEST_EQPT_INFO").toString();
		String rcId = "";
		
		if(testInfo.indexOf("-")>0){
			rcId = testInfo.split("-")[0];
			map.put("RC_ID", rcId);
			List<Map> unitList = planMapper.getUnitInfo(map);
			
			if(unitList.size()>0){
				Map unitMap = unitList.get(0);
				String waveLength = unitMap.get("WAVE_LEN").toString();
				if(waveLength.indexOf("/")>0){
					String[] waveLengthArr = waveLength.split("/");
					for(String wavelen : waveLengthArr){
						Map w_map = new HashMap();
						w_map.put("WAVE_LENGTH", wavelen);
						waveLengthList.add(w_map);
					}
				}else{
					Map w_map = new HashMap();
					w_map.put("WAVE_LENGTH", waveLength);
					waveLengthList.add(w_map);
				}
			}
		}
		
		return waveLengthList;
	}
	
	public int getOTDRType(Map map) throws CommonException 
	{
		int type = 0;
		String routeId = map.get("routeId").toString();
		Map routeMap = planMapper.getRouteById(routeId);
		
		String testInfo = routeMap.get("TEST_EQPT_INFO").toString();
		String rcId = "";
		
		if(testInfo.indexOf("-")>0){
			rcId = testInfo.split("-")[0];
			map.put("RC_ID", rcId);
			List<Map> unitList = planMapper.getUnitInfo(map);
			
			if(unitList.size()>0){
				Map unitMap = unitList.get(0);
				String otdrType = unitMap.get("OTDR_TYPE").toString();
				if("国产34所".equals(otdrType)){
					type = 2;
				}else{
					type = 1;
				}
			}
		}
		return type;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> getRangeList(Map map) throws CommonException 
	{
		List<Map> rangeMapList = new ArrayList();
		
		String routeId = map.get("routeId").toString();
		Map routeMap = planMapper.getRouteById(routeId);
		
		String testInfo = routeMap.get("TEST_EQPT_INFO").toString();
		String rcId = "";
		
		if(testInfo.indexOf("-")>0){
			rcId = testInfo.split("-")[0];
			map.put("RC_ID", rcId);
			List<Map> unitList = planMapper.getUnitInfo(map);
			
			if(unitList.size()>0){
				Map unitMap = unitList.get(0);
				String otdrType = unitMap.get("OTDR_TYPE").toString();
				if("国产34所".equals(otdrType)){
					Map w_map = new HashMap();
					w_map.put("OTDR_TYPE", 2);
					rangeMapList = planMapper.getRangeList(w_map);
				}else{
					Map w_map = new HashMap();
					w_map.put("OTDR_TYPE", 1);
					rangeMapList = planMapper.getRangeList(w_map);
				}
			}
		}
		
		return rangeMapList;
	}
	
	@SuppressWarnings("rawtypes")
	public List<Map> getPluseWidthList(Map map) throws CommonException 
	{
		List<Map> pluseWidthMapList = new ArrayList();
		
		String routeId = map.get("routeId").toString();
		Map routeMap = planMapper.getRouteById(routeId);
		
		String testInfo = routeMap.get("TEST_EQPT_INFO").toString();
		String rcId = "";
		
		if(testInfo.indexOf("-")>0){
			rcId = testInfo.split("-")[0];
			map.put("RC_ID", rcId);
			List<Map> unitList = planMapper.getUnitInfo(map);
			
			if(unitList.size()>0){
				Map unitMap = unitList.get(0);
				String otdrType = unitMap.get("OTDR_TYPE").toString();
				if("国产34所".equals(otdrType)){
					map.put("OTDR_TYPE", 2);
					pluseWidthMapList = planMapper.getPluseWidthList(map);
				}else{
					map.put("OTDR_TYPE", 1);
					pluseWidthMapList = planMapper.getPluseWidthList(map);
				}
			}
		}
		
		return pluseWidthMapList;
	}
	
	
	/*******************************************/
	/************触发测试***********************/
	@SuppressWarnings({ "rawtypes" })
	public Map getAlarmTriggerStatus() throws CommonException {
		Map rmap = planMapper.getSysParam("ALARM_TRIGGER_TEST");
		return rmap;
	}
	
	
	public void modifyAlarmTriggerStstus(Map map) throws CommonException {
		map.put("PARAM_KEY", "ALARM_TRIGGER_TEST");
		planMapper.updateSysParam(map);
	}
	
	public List<Map> getTriggerAlarm() throws CommonException {
		List<Map> alarmList = new ArrayList();
		Map rmap = planMapper.getSysParam("ALARM_TRIGGER_NAME");
		String alarmTrigger = rmap.get("PARAM_VALUE").toString();
		String[] alarmTriggers = alarmTrigger.split(",");
		for(int i=0;i<alarmTriggers.length;i++){
			Map alarmMap = new HashMap();
			alarmMap.put("ALARM_NAME", alarmTriggers[i]);
			alarmList.add(alarmMap);
		}
		return alarmList;

	}

	
	public void modifyTriggerAlarm(Map map) throws CommonException {
		map.put("PARAM_KEY", "ALARM_TRIGGER_NAME");
		planMapper.updateSysParam(map);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void InitAlarm2Route() throws CommonException {
		//先清空表数据
		try {
			planMapper.clearAlarmRouteMapping();
			
			List<Map> linkList = planMapper.getLinkList();
			Set ptpIdSet = new HashSet();
			for(int i=0;i<linkList.size();i++){
				Map linkMap = linkList.get(i);
				
				if(linkMap.get("RESOURCE_FIBER_ID") != null){
					String fiberId = linkMap.get("RESOURCE_FIBER_ID").toString();
					Map cablesInfoMap = planMapper.getCablesByfiberId(fiberId);
					
					if(cablesInfoMap.get("RESOURCE_CABLE_ID") != null){
						String cableId = cablesInfoMap.get("RESOURCE_CABLE_ID").toString();
						
						if(linkMap.get("A_END_PTP") != null && !"".equals(linkMap.get("A_END_PTP"))){
							ptpIdSet.add(linkMap.get("A_END_PTP").toString()+"#"+cableId);
						}
						if(linkMap.get("Z_END_PTP") != null && !"".equals(linkMap.get("Z_END_PTP"))){
							ptpIdSet.add(linkMap.get("Z_END_PTP").toString()+"#"+cableId);
						}
					}
					
				}
				
				
			}
			
			Map alarmNameMap = planMapper.getSysParam("ALARM_TRIGGER_NAME");
			String alarmName = alarmNameMap.get("PARAM_VALUE").toString();
			String[] alarmNameArr = alarmName.split(",");
			
			Iterator iterator = ptpIdSet.iterator();
			while(iterator.hasNext()){
				String ptp_cables = iterator.next().toString();
				
				System.out.println(ptp_cables);
				
				String ptpId = ptp_cables.split("#")[0];
				String cablesId = ptp_cables.split("#")[1];
				
				Map ptpInfoMap = planMapper.getPtpInfoById(ptpId);
				String neName = ptpInfoMap.get("NAME_FOR_FTTS").toString();
				String rackNo = ptpInfoMap.get("RACK_NO").toString();
				String shelfNo = ptpInfoMap.get("SHELF_NO").toString();
				String slotNo = ptpInfoMap.get("SLOT_NO").toString();
				String portNo = ptpInfoMap.get("PORT_NO").toString();
				
				Map routeMap = planMapper.getRouteByCablesId(cablesId);
				if(routeMap != null){
					if(routeMap.get("TEST_ROUTE_ID") != null && !"".equals(routeMap.get("TEST_ROUTE_ID"))){
						String routeId = routeMap.get("TEST_ROUTE_ID").toString();
						Map map = new HashMap();
						map.put("PTP_ID", ptpId);
						map.put("NE_NAME", neName);
						map.put("RACK_NO", rackNo);
						map.put("SHELF_NO", shelfNo);
						map.put("SLOT_NO", slotNo);
						map.put("PORT_NO", portNo);
						map.put("ROUTE_ID", routeId);
						map.put("CABLE_ID", cablesId);
						for(int i=0;i<alarmNameArr.length;i++){
							map.put("ALARM_NAME", alarmNameArr[i]);
							planMapper.addAlarmRouteMapping(map);
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String generateDiagram(Map<String, String> displayCond)
			throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map map = new HashMap<String, Object>();
		
		SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat sdfEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf_md = new SimpleDateFormat("M/d");
		List<String> tableNameListTemp = new ArrayList();
		List<String> tableNameList = new ArrayList();
		// 性能查询开始时间
		Date startTime = new Date();
		Date endTime = new Date();
		try {
			startTime = sdf.parse(displayCond.get("startTime"));
			endTime = sdf.parse(displayCond.get("endTime"));
			displayCond.put("startTime", sdfStart.format(startTime));			
			displayCond.put("endTime", sdfEnd.format(endTime));

			String title = "";
			Document document = DocumentHelper.createDocument();

			// graph标签
			Element graphElement = document.addElement("graph");
			
			// 获取标题信息
			int testRouteId = Integer.valueOf(displayCond.get("testRouteId"));
			Map<String,Object> testRoute = commonMapper.selectTableById("t_ftts_test_route",
					"TEST_ROUTE_ID", testRouteId);
			StringBuilder sb = new StringBuilder();
			sb.append("光缆段：").append(testRoute.get("ROUTE_NAME")).append("        ");
			sb.append("局站：").append(testRoute.get("NAME"));			
			// 标题内容（光缆段路由，局站路由，所属光缆）
			graphElement.addAttribute("caption", sb.toString());

//			graphElement.addAttribute("numdivlines", "0");
//			graphElement.addAttribute("exportEnabled", "1");
//			graphElement.addAttribute("exportAtClient", "1");
			graphElement.addAttribute("canvasPadding", "20");
//			graphElement.addAttribute("formatNumberScale", "0");
			graphElement.addAttribute("showAboutMenuItem", "0");
			graphElement.addAttribute("showPrintMenuItem", "0");
//			graphElement.addAttribute("chartLeftMargin", "20");
			graphElement.addAttribute("labelDisplay", "WRAP");
			
			// 显示类型：1.全程传输衰耗  2.全程光学距离  3.事件计数
			int displayType = Integer.valueOf(displayCond.get("type").toString());
			if (displayType == 3) {
				graphElement.addAttribute("decimalPrecision", "0");
			} else {
				graphElement.addAttribute("decimalPrecision", "2");
			}
			
			// 获取显示数据
			List<Map> testResultLst = planMapper.getDiagramData(displayCond);
			if (testResultLst.size() > 0) {
				double maxValue = 0;
				graphElement.addElement("set");
				for (Map result : testResultLst) {
					String value = "";
					switch (displayType) {
					case 1: //全程传输衰耗
						value = result.get("TRANS_ATTENUATION").toString();
						break;
					case 2: //全程光学距离
						value = result.get("TRANS_OPTICAL_DISTANCE").toString();
						break;
					case 3: //事件计数
						value = result.get("EVENT_COUNT").toString();
						break;
					}
					// set标签
					Element setElement = graphElement.addElement("set");
					setElement.addAttribute("name", sdf_md.format(result.get("EXE_TIME")));
					setElement.addAttribute("value", value);
					// 统计最大值
					if (Double.valueOf(value) > maxValue)
						maxValue = Double.valueOf(value);
				}
				graphElement.addElement("set");
				if (maxValue > 0) {
					int max = Double.valueOf(maxValue * 1.6).intValue();
					graphElement.addAttribute("yAxisMaxValue", String.valueOf(max));				
				}
			}
			
			String text = document.asXML();

			return text;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CMD_EXECUTE_FAIL);
		}

	}

}
