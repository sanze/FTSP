package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceMapper { 
	/**
	 * 获取查询列表的总条数
	 * @param map
	 * @return
	 */
	public int queryRCCount(@Param(value = "map")Map<String,Object> map,
			@Param(value = "ids")String ids,
			@Param(value = "level")int level); 
	/**
	 * 获取查询列表
	 * @param map,start,limit
	 * @return
	 */
	public List<Map<String,Object>> queryRC(@Param(value = "map")Map<String,Object> map,
			@Param(value = "ids")String ids,
			@Param(value = "level")int level,
			@Param(value = "start")int start,
			@Param(value = "limit") int limit); 
	/**
	 * 新增测试单元设备表
	 * @param map
	 * @return
	 */
	public void addRC(@Param(value = "map")Map<String,Object> map);
	/**
	 * 新增测试计划
	 * @param map
	 * @return
	 */
	public void addTestPlan(@Param(value = "map")Map<String,Object> map);
	/**
	 * 判断测试单元表是否存在
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> RCExists(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 获取测试设备属性
	 * @param rcId
	 * @return
	 */
	public Map<String, Object> getRCInfo(@Param(value = "rcId")int rcId);
	/**
	 * 判断修改的测试单元表是否存在
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> modRCCheck(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 修改测试设备表
	 * @param map
	 * @return
	 */
	public void modRC(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 根据编号修改测试设备表
	 * @param map
	 * @return
	 */
	public void modRCByNumber(@Param(value = "map")Map<String,Object> map);
	/**
	 * 根据id获取测试设备属性
	 * @param int
	 * @return
	 */
	public Map<String, Object> getRcById(@Param(value = "rcId")int rcId);
	/**
	 * 根据rcid获取板卡属性
	 * @param int
	 * @return
	 */
	public List<Map<String, Object>> getUnitById(@Param(value = "rcId")int rcId);
	/**
	 * 根据unitid获取板卡属性
	 * @param int
	 * @return
	 */
	public Map<String, Object> getTestEquipAttr(@Param(value = "unitId")int unitId);
	/**
	 * 根据id获取shelf,unit
	 * @param int
	 * @return
	 */
	public List<Map<String, Object>> getCardById(@Param(value = "rcId")int rcId); 
	/**
	 * 更新子架表
	 * @param map
	 * @return
	 */
	public void updateShelf(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 更新板卡表
	 * @param map
	 * @return
	 */
	public void updateUnit(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 新增子架表
	 * @param map
	 * @return
	 */
	public void addShelf(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 新增板卡表
	 * @param map
	 * @return
	 */
	public void addUnit(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 新增端口表
	 * @param map
	 * @return
	 */
	public void addOSWPort(@Param(value = "list")List<Map<String,Object>> list); 
	/**
	 * 更新OSW-port
	 * @param int,String
	 * @return
	 */
	public void updateOSWUnit(@Param(value = "unitId")Integer unitId,@Param(value = "unitName")String unitName); 
	
	/**
	 * 新增槽道表
	 * @param map
	 * @return
	 */
	public void addSlot(@Param(value = "list")List<Map<String,Object>>  list); 
	/**
	 * 获取槽道Id
	 * @param map
	 * @return
	 */
	public int getSlotId(@Param(value = "map")Map<String,Object> map); 
	/**
	 * 设备端口是否被使用
	 * @param rcId
	 * @return
	 */
	public List<Map<String, Object>> isPortUsed(@Param(value = "rcId")int rcId);
	/**
	 * 删除设备
	 * @param rcId
	 */
	public void deleteRC(@Param(value = "rcId")int rcId);
	/**
	 * 删除测试计划
	 * @param rcId
	 */
	public void deleteTestPlan(@Param(value = "rcId")int rcId);
	
	/**
	 * 获取系统表
	 * @param paramKey
	 */
	public String selectSysParam(@Param(value = "paramKey") String paramKey);
	/**
	 * 设备是否存在测试路由
	 * @param rcId
	 * @return
	 */
	public List<Map<String, Object>> testRouteExist(@Param(value = "rcId")int rcId);
	
	/**
	 * 删除DB中多余板卡
	 * @param rcId
	 */
	public void deleteUnit(@Param(value = "rcId")int rcId,@Param(value = "slotNo")int slotNo);
	/** 
	 * @param rcId
	 * @return
	 */
	public List<String> getRouteById(@Param(value = "rcId")int rcId);
	/**
	 * 更新板卡的status
	 * @param rcId
	 */
	public void updateUnitStatus(@Param(value = "rcId")int rcId,@Param(value = "slotNo")int slotNo,
			@Param(value = "status")int status);
	
}
