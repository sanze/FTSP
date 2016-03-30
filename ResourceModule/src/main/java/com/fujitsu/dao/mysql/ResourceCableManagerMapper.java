package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceCableManagerMapper { 
	
 	/**
	 * 查询光缆段信息 
	 */
	public List<Map<String,Object>> getCableList(@Param(value = "map")Map<String,Object> map,
			@Param(value = "start") int start,
			@Param(value = "limit")int limit);
	
	/**
	 * 查询光缆段信息条数
	 */
	public int getCableListCount(@Param(value = "map")Map<String,Object> map);
	
	
	/**
	 * 获取指定光缆段的光纤信息列表（FTTS用）
	 */
	public List<Map<String,Object>> getFiberListByCableId(
			@Param(value = "cableId")Integer cableId,
			@Param(value = "start")int start,
			@Param(value = "limit")int limit);
	
	
	/**
	 * 获取指定光缆段的光纤计数
	 */
	public int countFiberList(@Param(value = "cableId")Integer cableId);
	
}
