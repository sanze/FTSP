package com.fujitsu.dao.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface AreaManagerMapper {
	/**
	 * 获取区域级别名称
	 * @param map 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getAreaProperty(@Param(value = "map")Map map);
	/**
	 * 添加区域级别名称
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void addAreaProperty(@Param(value = "map")Map map);
	/**
	 * 修改区域级别名称
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void modAreaProperty(@Param(value = "map")Map map);
	/**
	 * 判断区域是否存在
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> areaExists(@Param(value = "map")Map map);
	/**
	 * 获取区域树的子节点（ T_RESOURCE_AREA 部分）
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> getSubArea(
			@Param(value = "parentId") int parentId);
	/**
	 * 根据父节点（多个）获取子区域
	 * @param parentIds
	 * @return
	 */
	public List<Map<String, Object>> getSubAreaByParentIds(
			@Param(value = "parentIds") String parentIds);
	/**
	 * 根据父节点（多个）获取子区域信息
	 * @param parentIds
	 * @return
	 */
	public List<Map<String, Object>> getAreaInfoByParentIds(
			@Param(value = "parentIds") String parentIds);
	/**
	 * 获取区域树的子节点（ T_RESOURCE_STATION 部分）
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> getSubStation(
			@Param(value = "parentId") int parentId);
	/**
	 * 根据父节点（多个）获取局站信息
	 * @param parentIds
	 * @return
	 */
	public List<Map<String, Object>> getSubStationByIDs(
			@Param(value = "parentIds") String parentIds,
			@Param(value = "name") String name,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit); 
	public int countSubStationByIDs(
			@Param(value = "parentIds") String parentIds,
			@Param(value = "name") String name); 
	/**
	 * 根据父节点（多个）获取局站ID
	 * @param parentIds
	 * @return
	 */
	public List<Map<String, Object>> getSubStationIDs(
			@Param(value = "parentIds") String parentIds);
	/**
	 * 根据父节点（多个）获取机房信息
	 * @param parentIds
	 * @return
	 */
	public List<Map<String, Object>> getSubRoomByIDs(
			@Param(value = "parentIds") String parentIds);
	/**
	 * 根据父节点（多个）获取机房ID
	 * @param parentIds
	 * @return
	 */
	public List<Map<String, Object>> getSubRoomIDs(
			@Param(value = "parentIds") String parentIds);
	/**
	 * @@@分权分域到网元@@@
	 * 根据父节点（多个）获取网元信息
	 * @param parentIds
	 * @return
	 */
	public List<Map<String, Object>> getSubNeByIDs(
			@Param(value = "parentIds") String parentIds,
//            @Param(value = "emsIdList") String emsIdList);
            @Param(value = "LEVEL_MAX") int LEVEL_MAX,
			@Param(value = "userId")Integer userId,
			@Param(value = "Define") Map define);
	/**
	 * 检测局站是否存在
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map<String,Object>> getStationByIdName(
            @Param(value = "map")Map map);
	/**
	 * 检测机房是否存在
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map<String,Object>> getRoomByIdName(
            @Param(value = "map")Map map);
	/**
	 * 获取区域树的子节点（ T_RESOURCE_ROOM 部分）
	 * @param parentId
	 * @return
	 */
	public List<Map<String,Object>> getSubRoom(
			@Param(value = "parentId") int parentId);
	/**
	 * 新增区域
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void addArea(@Param(value = "map")Map map);
	/**
	 * 新增局站
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void addStation(@Param(value = "map")Map map);
	/**
	 * 新增机房
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void addRoom(@Param(value = "map")Map map);
	/**
	 * 删除区域
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void delArea(@Param(value = "map")Map map);
	/**
	 * 删除区域
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void delStation(@Param(value = "map")Map map);
	/**
	 * 删除机房
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void delRoom(@Param(value = "map")Map map);
	/**
	 * 修改区域
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void modArea(@Param(value = "map")Map map);
	/**
	 * 修改局站
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void modStation(@Param(value = "map")Map map);
	/**
	 * 修改机房
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void modRoom(@Param(value = "map")Map map);
	/**
	 * 获取区域树的父节点 
	 * @param parentId
	 * @return
	 */
	public Map<String,Object> getParentArea(@Param(value = "parentId") int parentId);
	/**
	 * 获取局站信息
	 * @param nodeId
	 * @return
	 */
	public Map<String,Object> getStationInfo(@Param(value = "nodeId") int nodeId);
	/**
	 * 获取机房信息 
	 * @param nodeId
	 * @return
	 */
	public Map<String,Object> getRoomInfo(@Param(value = "nodeId") int nodeId);
	/**
	 * 获取机房关联的网元
	 * @param roomId 机房ID
	 * @return
	 */
	public List<Map<String, Object>> getRelatedNE(
			@Param(value = "roomId") int roomId);
	/**
	 * @@@分权分域到网元@@@
	 * 获取机房关联的网元
	 * @param roomId 机房ID
	 * @return
	 */
	public List<Map<String, Object>> getRelatedNEAuth(
			@Param(value = "roomId") int roomId,
			@Param(value = "userId")Integer userId,
			@Param(value = "Define") Map define);
	/**
	 * 获取机房关联的网元(网管部分)
	 * @param emsIds 网管ID列表
	 * @return
	 */
	public List<Map<String, Object>> getRelatedGroup(
			@Param(value = "emsIds") String emsIds);
	/**
	 * 清除机房关联的网元 
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void clearRelatedNE(@Param(value = "map")Map map,
			@Param(value = "userId")Integer userId,
			@Param(value = "Define") Map define);
	/**
	 * 给机房关联网元
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void updateRelatedNE(@Param(value = "map")Map map);
	// 获取局站下属光缆
	public List<Map<String, Object>> getCable(
			@Param(value = "stationId") int stationId);
	/**
	 * mod room校验
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> modRoomCheck(@Param(value = "map")HashMap<String, Object> map);
	/**
	 * 修改校验
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> modStationCheck(@Param(value = "map")HashMap<String, Object> map);
	
	
	/**
	 * 获取所有的局站信息
	 * @return
	 */
	public List<Map<String, Object>> getAllStation();
	
	/**
	 * 通过区域id获取下属的所有局站
	 * @param areaIds
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> getStationListByAreaIds(@Param(value = "areaIds") List areaIds);
	
}
