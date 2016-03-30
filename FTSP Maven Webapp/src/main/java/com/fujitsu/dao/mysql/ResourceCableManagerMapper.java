package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceCableManagerMapper { 
	/**
	 * 获取光缆信息
	 */ 
	public List<Map<String,Object>> getCables(@Param(value = "map")Map<String,Object> map,
			@Param(value = "start")int start,
			@Param(value = "limit")int limit); 
	/**
	 * 获取光缆条数 
	 */
	public int getCablesCount(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 查询光缆名称是否重复
	 */ 
	public List<Map<String,Object>> cablesExist(@Param(value = "map")Map<String,Object> map);   
	/**
	 * 新增一条光缆信息 
	 */
	public void addCables(@Param(value = "map")Map<String,Object> map);  
	/** 
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> modCablesCheck(@Param(value = "map")Map<String,Object> map);
	/**
	 * 查询光缆属性 
	 */
	public Map<String, Object> getCablesInfoById(@Param(value = "cablesId")int cablesId);   
	/**
	 * 修改光缆 
	 */
	public void modCables(@Param(value = "map")Map<String,Object> map);
	/**
	 * 查询该光缆包含的光缆段信息
	 */
	public List<Map<String,Object>> getSubCable(@Param(value = "cablesId") int cablesId);
	/**
	 * 删除光缆 
	 */
	public void delCables(@Param(value = "cablesId")int cablesId);
	
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
	 * 查询名称和代号信息
	 */
	public List<Map<String,Object>> getAllCodeNames();
	
	/**
	 * 查询光缆段名称是否重复
	 */ 
	public List<Map<String,Object>> cableExist(@Param(value = "map")Map<String,Object> map);     
	/**
	 * 新增一条光缆段信息
	 */
	public void addCable(@Param(value = "map")Map<String,Object> map);
	/**
	 * 获取光缆段信息
	 */
	public Map<String,Object> getCableInfo(@Param(value = "cableId")int cableId);	 
	/**
	 * 修改光缆段时，查询是否重复
	 */
	public List<Map<String,Object>> modCableCheck(@Param(value = "map")Map<String,Object> map);   
	/**
	 * 修改光缆段信息
	 */
	public void modifyCable(@Param(value = "map")Map<String,Object> map);
	/**
	 * 查询光缆段所含链路
	 */
	public List<Map<String,Object>> getLinkById(@Param(value = "cableId")int cableId);   
	/**
	 * 查询光缆段所含ODF
	 */
	public List<Map<String,Object>> getOdfById(@Param(value = "cableId")int cableId);   
	/**
	 * 删除光缆段信息 
	 */
	public void deleteCable(@Param(value = "cableId")int cableId);
	
	/**
	 * 删除光缆段信息 
	 */
	public void deleteFiber(@Param(value = "cableId")int cableId); 
	
	/**
	 * 新增一条光纤信息
	 */
	public void addFiber(@Param(value = "cableId")int cableId,
			@Param(value = "cableFiberNumber")String cableFiberNumber,
			@Param(value = "map")Map<String,Object> map);  
	/**
	 * 获取光纤列表 
	 */
	public List<Map<String,Object>> getFiberList(@Param(value = "cableId")Integer cableId,
			@Param(value = "limit")int limit,@Param(value = "start")int start);
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
	/**
	 * 修改光纤信息
	 */
	public void modifyFiberResource(@Param(value = "map")Map map); 
	/**
	 * 获取光纤关联链路的个数
	 */
	public Integer countFiberRelateLink(@Param(value = "cableId")Integer cableId,
			@Param(value = "fiberNo")Integer fiberNo); 
	/**
	 *删除部分无关联的光纤
	 */
	public void deleteFiberList(@Param(value = "cableId")Integer cableId,
			@Param(value = "fiberNo")Integer fiberNo); 
	
}
