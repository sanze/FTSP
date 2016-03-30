package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface GisManagerMapper {
	
	/**
	 * 获取光缆包含的光缆段
	 * @param 
	 * @return List
	 */
	public List<Map<String,Object>> getCableSections();
	
	/**
	 * 获取光缆段
	 * @param 
	 * @return List
	 */
	public Map<String,Object> getCableSectionById(@Param(value = "cableSectionId") String cableSectionId);
	
	/**
	 * 获取光缆段起止点的经纬度
	 * @param 
	 * @return List
	 */
	public List<Map<String,Object>> getStartAndEndLngLat(@Param(value = "cableSectionId") String cableSectionId);
	
	/**
	 * 获取测试路由
	 * @param 
	 * @return List
	 */
	public List<Map<String,Object>> getTestRoutesByCsId(@Param(value = "cableSectionId") String cableSectionId);
	
	/**
	 * 获取正在测试的测试路由
	 * @param 
	 * @return List
	 */
	public List<Map<String,Object>> getTestingRoutesByCsId(@Param(value = "cableSectionId") String cableSectionId);
	
	/**
	 * 获取系统中全部正在测试的测试路由
	 * @param 
	 * @return List
	 */
	public List<Map<String,Object>> getAllTestingRoutes();
	
	/**
	 * 获取测试路由
	 * @param 
	 * @return List
	 */
	public List<Map<String,Object>> getCableSectionsThroughAZ(@Param(value = "map") Map<String, Object> map);

	public List<Map<String, Object>> getTransSystemsByArea(String area);

	public List<Map<String, Object>> getStationsNotInCable(@Param(value = "map") Map<String, Object> map);
	
	public List<Map> getRangeList(@Param(value = "map")Map map);
	
	public List<Map> getPluseWidthList(@Param(value = "map")Map map);
}
