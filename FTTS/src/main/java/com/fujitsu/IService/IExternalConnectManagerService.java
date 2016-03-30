package com.fujitsu.IService;

import java.util.Map;

import com.fujitsu.common.CommonException;
 
public interface IExternalConnectManagerService { 
	/**
	 * 获取指定局站的相关光缆段信息列表
	 * @param stationId
	 * @return Map<String,Object> [Key:total,rows  rowkey:CABLE_ID,CABLE_NAME_FTTS]
	 * @throws CommonException
	 */
	public Map<String, Object> getCableList(int stationId) throws CommonException;

	/**
	 * 获取指定光缆段的光纤信息列表
	 * @param cableId
	 * @return Map<String,Object> [Key:total,rows  rowkey:RESOURCE_FIBER_ID,FIBER_NO,FIBER_NAME,NOTE]
	 * @throws CommonException
	 */
	public Map<String, Object> getFiberListByCableId(int cableId) throws CommonException;
	
	/**
	 * 获取指定局站的测试设备信息列表
	 * @param stationId
	 * @return Map<String,Object> [Key:rows  rowkey:RC_ID,NUMBER,NAME]
	 * @throws CommonExceptioin
	 */
	public Map<String, Object> getRcListByStationId(int stationId) throws CommonException;
	
	/**
	 * 获取指定测试设备的单板信息（OTDR和OSW）
	 * @param rcId
	 * @return Map<String,Object> 
	 * @throws CommonExctption
	 */
	public Map<String, Object> getUnitListByRcId(int rcId) throws CommonException;
	
	/**
	 * 获取指定局站的外部连接信息
	 * @param stationId
	 * @return Map<String,Object> [Key:total,rows rowkey:CONNECT_ID,STATION_ID,A_END_ID,Z_END_ID,CONN_TYPE,FIBER_INFO]
	 */
	public Map<String,Object> getConnectInfoByStationId(int stationId) throws CommonException;;
	/**
	 * 增加一条外部连接
	 * @param connectData
	 * @return void
	 * @throws CommonException
	 */
	public void addOneExternalConnect(Map<String,Object> connectData) throws CommonException;
	
	/**
	 * 删除一条外部连接
	 * @param connectData
	 * @return void
	 * @throws CommonException
	 */
	public void delOneExternalConnect(Map<String,Object> connectData) throws CommonException;
	
	/**
	 * 获取外部连接管理用的局站列表
	 * @return Map<String,Object> [Key:total,rows]
	 * 		RowMapKey: t_resource_station表字段 + AREA_NAME
	 * @throws CommonException
	 */
	public Map<String, Object> getStationList(int parentId, int parentLevel, boolean showAll, String name,
			int start, int limit) throws CommonException;
}
