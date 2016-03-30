package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceStatisticManagerMapper {
	
	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> getSubNeByIDs(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList, 
            @Param(value = "start") int start,
            @Param(value = "limit") int limit);
	
	@SuppressWarnings("rawtypes")
	public List<Map> getSubNeByIDsChart(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList,
            @Param(value = "text") String text);
	
	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> getSubUnitByIDs(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList, 
            @Param(value = "start") int start,
            @Param(value = "limit") int limit);
	
	@SuppressWarnings("rawtypes")
	public List<Map> getSubUnitByIDsChart(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList,
            @Param(value = "text") String text);
	
	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> getSubPortByIDs(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList, 
            @Param(value = "start") int start,
            @Param(value = "limit") int limit);
	
	@SuppressWarnings("rawtypes")
	public List<Map> getSubPortByIDsChart(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList,
            @Param(value = "text") String text);
	
	public Map<String,Object> getSubPortByIDsCount(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList);
	public Map<String,Object> getSubUnitByIDsCount(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList);
	public Map<String,Object> getSubNeByIDsCount(
			@Param(value = "parentIds") String parentIds,
            @Param(value = "emsIdList") String emsIdList);
}