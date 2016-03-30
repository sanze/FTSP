package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.ibatis.annotations.Param;

public interface ReportManagerMapper {
	/**
	 * Method name: getResourceChartByStationAndNeModel <BR>
	 * Description: 按局站查询资源统计信息<BR>
	 * Remark: 2013-12-05<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getResourceChartByStationAndNeModel(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getAllNeModelsByStation <BR>
	 * Description: 查询某些局站下的所有网元型号<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getAllNeModelsByStation(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getResourceChartByNeModelAndStation <BR>
	 * Description: 按局站查询某网元型号的资源统计信息<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return List<Map><BR>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getResourceChartByNeModelAndStation(@Param(value = "map") Map<String, Object> map);
	/**
	 * Method name: getResourceCountByStation <BR>
	 * Description: 按局站查询资源总数<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getResourceCountByStation(@Param(value = "map") Map<String, Object> map);
	/**
	 * Method name: getResourceDetailByStation <BR>
	 * Description: 按局站查询资源详细信息<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getResourceDetailByStation(@Param(value = "map") Map<String, Object> map,@Param(value = "start") int start,@Param(value = "limit") int limit);
	/**
	 * Method name: getEmsGroupTotal_Resource <BR>
	 * Description: 资源报表--按网管分组统计查询总数<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 年份
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsGroupTotal_Resource(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupInfo_Resource <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map
	 * @param start
	 * @param limit
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupInfo_Resource(@Param(value = "map") Map<String, Object> map,@Param(value = "start") int start,@Param(value = "limit") int limit);
	
	/**
	 * Method name: getEmsGroupFusionChart_Resource <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Resource(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupTotal_Performance <BR>
	 * Description: 资源报表--按网管分组统计查询总数<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 年份
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsGroupTotal_Performance(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupInfo_Performance <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map
	 * @param start
	 * @param limit
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupInfo_Performance(@Param(value = "map") Map<String, Object> map,@Param(value = "start") int start,@Param(value = "limit") int limit);
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_1 <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_1(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_2 <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<ListOrderedMap> getEmsGroupFusionChart_Performance_2(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_LV <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_LV(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_3 <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_3(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupTotal_Resource <BR>
	 * Description: 资源报表--按网管统计查询总数<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 年份
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsTotal_Resource(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupInfo_Resource <BR>
	 * Description: 资源报表--按网管统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map
	 * @param start
	 * @param limit
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsInfo_Resource(@Param(value = "map") Map<String, Object> map,@Param(value = "start") int start,@Param(value = "limit") int limit);
	
	/**
	 * Method name: getEmsGroupFusionChart_Resource <BR>
	 * Description: 资源报表--按网管统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsFusionChart_Resource(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupTotal_Performance <BR>
	 * Description: 资源报表--按网管统计查询总数<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 年份
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsTotal_Performance(@Param(value = "map") Map<String, String> map);
	
	/**
	 * Method name: getEmsGroupInfo_Performance <BR>
	 * Description: 资源报表--按网管统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map
	 * @param start
	 * @param limit
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsInfo_Performance(@Param(value = "map") Map<String, String> map,@Param(value = "start") int start,@Param(value = "limit") int limit);
	
	/**
	 * Method name: getEmsFusionChart_Performance_1 <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsFusionChart_Performance_1(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsFusionChart_Performance_2 <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsFusionChart_Performance_2(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupTotal_Circuit <BR>
	 * Description: 资源报表--按网管分组统计查询总数<BR>
	 * Remark: 2013-12-23<BR>
	 * @param map: 年份
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsGroupTotal_Circuit(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupInfo_Circuit <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-23<BR>
	 * @param map
	 * @param start
	 * @param limit
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupInfo_Circuit(@Param(value = "map") Map<String, Object> map,@Param(value = "start") int start,@Param(value = "limit") int limit);
	
	/**
	 * Method name: getEmsGroupFusionChart_Circuit_1 <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-23<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Circuit_1(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * Method name: getEmsGroupFusionChart_Circuit_2 <BR>
	 * Description: 资源报表--按网管分组统计fusionChart查询<BR>
	 * Remark: 2013-12-23<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Circuit_2(@Param(value = "map") Map<String, Object> map);
	
	public List<Map<String, Object>> getEmsFusionChart_Alarm_test(@Param(value = "map") Map<String, Object> map);

	public void callPerformaceSP(@Param(value = "map") Map<String, Object> map);

	public List<Map<String, Object>> getPMMonthDataByEmsGroup(@Param(value = "map") Map<String, Object> map);

	public List<Map<String, Object>> getPMDayDataByEmsGroup(@Param(value = "map") Map<String, Object> map);

	public List<Map<String, Object>> getPMDayDataByQueryDayAndGroup(@Param(value = "map") Map<String, Object> map);

	public void insertAlarmDataFromMonodb(@Param(value = "lists") List<Map> datas);

	public void deleteAlarmDataByDay(@Param(value = "day") String day);

	public List<Map<String, Object>> getReportAlarmByEms(@Param(value = "map") Map<String, Object> map);
	public List<Map<String, Object>> getAlarmMonthDataByEms(@Param(value = "map") Map<String, Object> map);
	public List<Map<String, Object>> getAlarmDayDataByEms(@Param(value = "map") Map<String, Object> map);
	public List<Map<String, Object>> getAlarmDayDataByQueryDayAndEms(@Param(value = "map") Map<String, Object> map);

	public List<Map<String, Object>> getPMDataPerMonthByEmsId(
			@Param(value = "map") Map<String, String> condMap);

	public List<Map<String, Object>> getPMDataPerDayEms(
			@Param(value = "map") Map<String, String> condMap);
	
	public List<Map<String, Object>> getPMDataPerDayByEmsId(
			@Param(value = "map") Map<String, String> condMap);

	public String getEmsName(@Param(value = "map") Map<String, String> condMap);

	public void insertAlarmAnaData(@Param(value = "firstTime") String firstTime);

	public void deleteAlarmDataByDayForever(@Param(value = "day") String day);	

	
	
}
