package com.fujitsu.IService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IAreaManagerService {
	//获取 区域树的子节点
	public List<Map<String, Object>> getSubArea(int parentId, int parentLevel, int maxLevel)
			throws CommonException;
	
	public List getSubAreaIds(int parentId)throws CommonException;
	public List getSubStationIDs(String areaIds)throws CommonException;
	public List getSubRoomIDs(String stationIds)throws CommonException;
	
	//获取 区域树的子节点
	public Map<String, Object> getAreaGrid(int parentId, int parentLevel)
			throws CommonException;
	//获取 区域树的层级属性
	public List<String> getAreaProperty()
			throws CommonException;
	//设置 区域树的层级属性
	public void setAreaProperty(String newLevelName)
			throws CommonException;
	
	//新增 区域
	public void addArea(HashMap<String, Object> map) 
			throws CommonException;
	//新增 局站
	public void addStation(HashMap<String, Object> map) 
			throws CommonException;
	//新增 机房
	public void addRoom(HashMap<String, Object> map) 
			throws CommonException;
	//设置 区域树的信息
	public void modArea(HashMap<String, Object> map) 
			throws CommonException;
	//删除 区域
	public void delArea(HashMap<String, Object> map) 
			throws CommonException;
	//删除 局站
	public void delStation(HashMap<String, Object> map) 
			throws CommonException;
	//删除 机房
	public void delRoom(HashMap<String, Object> map) 
			throws CommonException;
	//获取已关联网元
	public Map<String, Object> getRelatedNE(int roomId) 
			throws CommonException; 
	/**
	 * @@@分权分域到网元@@@
	 */
	public Map<String, Object> getRelatedNEAuth(Integer userId,int roomId) 
			throws CommonException;
	//更新 关联网元
	public void updateRelatedNE(Map<String, Object> map) 
			throws CommonException;
	//清除 关联网元
	/**
	 * @@@分权分域到网元@@@
	 */
	public void clearRelatedNE(Integer userId,Map<String, Object> map) 
			throws CommonException;
	// 获取局站/机房信息
	public Map<String, Object> getAreaInfo(int nodeId, int level) 
			throws CommonException;
	//设置局站的信息
	public void modStation(HashMap<String, Object> map) 
			throws CommonException;
	//设置机房的信息
	public void modRoom(HashMap<String, Object> map) 
			throws CommonException;
	//检测局站是否存在
	public boolean stationExists(HashMap<String, Object> map)throws CommonException;
	//检测机房是否存在
	public boolean roomExists(HashMap<String, Object> map)throws CommonException;
	public List<Map<String, Object>> getSubRoom(int nodeId)throws CommonException;

	public List<Map<String, Object>> getAreaInfoByParentIds(String areaIds)throws CommonException;
	public Map<String, Object> getRoomGrid(int parentId, int parentLevel, boolean showAll)throws CommonException;
	//获取区域的 子局站 Grid列表
	public Map<String, Object> getStationGrid(int parentId, boolean showAll,String name,int start,int limit)throws CommonException; 
	/**
	 * @@@分权分域到网元@@@
	 */
	public Map<String, Object> getNeGrid(Integer userId,int parentId, int parentLevel, boolean showAll)throws CommonException;
	public List<Map<String, Object>> getCable(int nodeId)throws CommonException;
	public boolean areaExists(HashMap<String, Object> map)throws CommonException;
//	public CommonResult exportInfo(List<Map> dat, int exportType, String[] columns)throws CommonException;

	boolean modRoomCheck(HashMap<String, Object> map) throws CommonException;

	boolean modStationCheck(HashMap<String, Object> map) throws CommonException;
	
	
	/**
	 * 通过区域id获取下属的所有局站，为null时获取所有
	 * @param areaId
	 * @return
	 * @throws CommonException
	 */
	public List<Map<String, Object>> getStationListByAreaId(Integer areaId) throws CommonException;

}
