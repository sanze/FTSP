package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.model.TestResultModel;

/**
 * @Description：
 * @author cao senrong
 * @date 2015-1-15
 * @version V1.0
 */
public interface PlanMapper {
	
	public List<Map> getPlanList(@Param(value = "map")Map map);
	
	public List<Map> getRouteListByPlanId(@Param(value = "map")Map map);
	
	public void updateTestRoutePara(@Param(value = "map")Map map);
	
	public void updateTestPlanStatusStartUp(@Param(value = "planIdList")List planIdList);
	
	public void updateTestPlanStatusPending(@Param(value = "planIdList")List planIdList);
	
	public void updateTestRoutePeriod(@Param(value = "map")Map map);
	
	public Map getSysParam(@Param(value = "key")String key);
	
	public void updateSysParam(@Param(value = "map")Map map);
	
	public Map getNextPlan();
	
	public Map getFirstPlan();
	
	public void updateRouteScanTimes(@Param(value = "map")Map map);
	
	public Map getRcById(@Param(value = "rcId")String rcId);
	
	public List<Map> getUnitInfo(@Param(value = "map")Map map);
	
	public List<Map> getRangeList(@Param(value = "map")Map map);
	
	public List<Map> getPluseWidthList(@Param(value = "map")Map map);
	
	
	
	public List<Map> getTriggerAlarmList();
	
	public Map getRouteByAlarmInfo(@Param(value = "map")Map map);
	
	public Map getRouteById(@Param(value = "id")String id);
	
	public Map getPlanById(@Param(value = "id")String id);
	
	public int addTestResult(@Param(value = "testRlt")TestResultModel result);
	
	public void addTestEvent(@Param(value = "map")Map map);
	
	//Create Data
	public List<Map> getLinkList();
	
	public Map getPtpInfoById(@Param(value = "id")String id);
	
	public Map getCablesByfiberId(@Param(value = "id")String id);
	
	public Map getRouteByCablesId(@Param(value = "id")String id);
	
	public void addAlarmRouteMapping(@Param(value = "map")Map map);
	
	public void clearAlarmRouteMapping();
	
	//修改路由信息
	public void updateTestRouteById(Map<String,Object> map); 
	
	//测试结果
	public List<Map> getResultList(@Param(value = "map")Map map);
	
	public List<Map> getEventListByResult(@Param(value = "map")Map map);
	
	public Map getResultById(@Param(value = "map")Map map);
	
	public Map getResultInfoById(@Param(value = "map")Map map);
	
	public List<Map> getRouteList();
	
	public Map getLastTestResult(@Param(value = "map")Map map);
	
	// 断点分析相关
	public Map<String, Object> getBreakpointInfo(@Param(value = "testResultId") String testResultId);
	
	public void updateBreakpointToCableSection(@Param(value = "breakpointStr") String breakpointStr,
			@Param(value = "breakCSid") String breakCSid);
	
	// 保存基准值
	public void updateBaseValueById(@Param(value = "map")Map map);
	
	// 获取测试结果趋势图数据
	public List<Map> getDiagramData(@Param(value = "map")Map map);
	
	public void deleteEvent(@Param(value = "resultIds")List<Integer> resultIds);
	
	public void deleteResult(@Param(value = "resultIds")List<Integer> resultIds);
	
	// 通过测试路由ID获取关联信息（测试设备名称，测试路由名称，测试周期）
	public Map<String,Object> getRelatedInfoByRouteId(@Param(value="routeId")String routeId);
}
