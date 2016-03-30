package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;

import com.fujitsu.common.CommonException;
public interface IReportManagerService {
	/**
	 * Method name: getResourceChartByStationAndNeModel <BR>
	 * Description: 按网元型号查询某些局站的资源统计信息<BR>
	 * Remark: 2013-12-05<BR>
	 * @author CaiJiaJia
	 * @return List<Map><BR>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getResourceChartByStationAndNeModel(Map<String, Object> map) throws CommonException;
	/**
	 * Method name: getAllNeModelsByStation <BR>
	 * Description: 查询某些局站下的所有网元型号<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getAllNeModelsByStation(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getResourceChartByNeTypeAndStation <BR>
	 * Description: 按局站查询某网元型号的资源统计信息<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return List<Map><BR>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getResourceChartByNeModelAndStation(Map<String, Object> map) throws CommonException;
	/**
	 * Method name: getResourceDetailByStation <BR>
	 * Description: 按局站查询资源详细信息<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><Map><BR>
	 */
	public Map<String, Object> getResourceDetailByStation(Map<String, Object> map,int start,int limit) throws CommonException;
	
	/**
	 * Method name: getEmsGroupInfo_Resource <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 
	 * @param start:
	 * @param limit:
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsGroupInfo_Resource(Map<String, Object> map,int start,int limit) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Resource <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Resource(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupInfo_Performance <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 
	 * @param start:
	 * @param limit:
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsGroupInfo_Performance(Map<String, Object> map,int start,int limit) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_1 <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_1(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_2 <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<ListOrderedMap> getEmsGroupFusionChart_Performance_2(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_LV <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_LV(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_3 <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_3(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupInfo_Resource <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 
	 * @param start:
	 * @param limit:
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsInfo_Resource(Map<String, Object> map,int start,int limit) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Resource <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsFusionChart_Resource(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsInfo_Performance <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @param map: 
	 * @param start:
	 * @param limit:
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsInfo_Performance(Map<String, String> map,int start,int limit) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_1 <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsFusionChart_Performance_1(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Performance_2 <BR>
	 * Description: 性能报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsFusionChart_Performance_2(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupInfo_Circuit <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-23<BR>
	 * @param map: 
	 * @param start:
	 * @param limit:
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsGroupInfo_Circuit(Map<String, Object> map,int start,int limit) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Circuit_1 <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-23<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Circuit_1(Map<String, Object> map) throws CommonException;
	
	/**
	 * Method name: getEmsGroupFusionChart_Circuit_2 <BR>
	 * Description: 资源报表--按网管分组统计查询<BR>
	 * Remark: 2013-12-23<BR>
	 * @author YuanJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getEmsGroupFusionChart_Circuit_2(Map<String, Object> map) throws CommonException;
	
	public List<Map<String, Object>> getEmsFusionChart_Alarm_test(Map<String, Object> map) throws CommonException;
	/*
	 * 调用性能存储过程汇聚数据
	 * 
	 */
	public Map callPerformaceSP(Map param)  throws CommonException;
	public Map callPerformanceSPJob(Map param) throws CommonException;
	public Map<String, Object> getPMMonthDataByEmsGroup(Map<String, Object> paramMap);
	public Map<String, Object> getPMDayDataByEmsGroup(Map<String, Object> paramMap);
	public Map<String, Object> getPMDayDataByQueryDayAndGroup(Map<String, Object> paramMap);
	public void insertAlarmDataFromMonodb(List<Map> datas);
	public Map<String, Object> getReportAlarmByEms(Map<String, Object> paramMap);
	public Map<String, Object> getAlarmMonthDataByEms(Map<String, Object> paramMap);
	public Map<String, Object> getAlarmDayDataByEms(Map<String, Object> paramMap);
	public Map<String, Object> getAlarmDayDataByQueryDayAndEms(Map<String, Object> paramMap);
	
	//533
	/**
	 * 获取某一网管该年中每个月的性能数据
	 * @param condMap
	 * @return
	 */
	public Map<String, Object> getPMDataPerMonthByEmsId(Map<String, String> condMap);
	/**
	 * 获取某一网管某月中每天的性能数据
	 * @param condMap
	 * @return
	 */
	public Map<String, Object> getPMDataPerDayEms(Map<String, String> condMap);

	/**
	 * 获取单网管某月内每天的数据
	 * @param condMap
	 * @return
	 */
	public Map<String, Object> getPMDataPerDayByEmsId(
			Map<String, String> condMap);

	/**
	 * 根据ID获取EMS名称
	 * @param condMap
	 * @return
	 */
	public String getEmsName(Map<String, String> condMap);
}
