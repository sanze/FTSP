package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ExternalConnectMapper {
	
	/**
	 * 获取特定局站的测试设备信息列表
	 * @param tableName
	 * @return
	 */
	public List<Map<String,Object>> getRcInfoListByStationId(@Param(value = "stationId")int roomIds);

	/**
	 * 获取指定测试设备的单板信息（OTDR和OSW）
	 */
	public List<Map<String, Object>> getUnitListByRcId(@Param(value = "rcId")int rcId);

	/**
	 * 增加一条外部连接
	 */
	public void addOneExternalConnect(@Param(value = "map")Map<String,Object> map);
	
	/**
	 * 增加外部光纤连接对应的测试路由
	 * @param map [key:CONNECT_ID,STATION_ID,A_END_ID,Z_END_ID,CONN_TYPE]
	 */
	public void addTestRoute(@Param(value = "map")Map<String,Object> map);
	
	/**
	 * 获取符合指定条件的外部光纤连接
	 */
	public Map<String,Object> getExternalConnectByParam(@Param(value = "param")Map<String,Object> param);
	
	/**
	 * 删除一条指定的外部光纤连接
	 * @param map [key:CONNECT_ID,STATION_ID,A_END_ID,Z_END_ID,CONN_TYPE]
	 */
	public void delOneExternalConnect(@Param(value = "map")Map<String,Object> map);
	
	/**
	 * 删除外部光纤连接对应的测试路由
	 * @param map [key:CONNECT_ID,STATION_ID,A_END_ID,Z_END_ID,CONN_TYPE]
	 */
	public void delTestRoute(@Param(value = "map")Map<String,Object> map);
	
	/**
	 * 获取拥有测试设备的局站ID列表
	 * @return
	 * 		MapKey:RESOURCE_STATION_ID
	 */
    public List<Map<String, Object>> getStationIdWithRC();
}
