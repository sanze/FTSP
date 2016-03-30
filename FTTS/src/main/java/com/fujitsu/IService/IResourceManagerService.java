package com.fujitsu.IService;

import java.util.Map;

import com.fujitsu.common.CommonException;
 
public interface IResourceManagerService { 
	/**
	 * 查询设备列表
	 * @param map
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> queryRC(Map<String, Object> map,int nodeId,int level,int start,int limit)
			throws CommonException;
	/**
	 * 判断设备是否存在
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public boolean RCExists(Map<String, Object> map) throws CommonException;
	/**
	 * 新增设备
	 * @param map
	 * @throws CommonException
	 */
	public void addRC(Map<String, Object> map) throws CommonException; 
	/**
	 * 获取一条设备属性
	 * @param rcId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getRCInfo(int rcId)throws CommonException; 
	/**
	 * 修改后判定设备是否存在
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public boolean modRCCheck(Map<String, Object> map) throws CommonException;
	/**
	 * 修改设备
	 * @param map
	 * @throws CommonException
	 */
	public void modRC(Map<String, Object> map) throws CommonException; 
	/**
	 * 查询面板数据
	 * @param rcId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> queryRCCard(int rcId)throws CommonException; 
	/**
	 * 获取面板板卡属性
	 * @param unitId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTestEquipAttr(int unitId)throws CommonException; 
	/**
	 * 同步设备数据到数据库
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public boolean syncRtuAllCardInfo(Map<String, Object> map) throws CommonException; 
	/**
	 * 获取系统和设备时间
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getEqptAndServerTime(Map<String, Object> map)throws CommonException; 
	/**
	 * 判断设备端口是否被使用
	 * @param cellId
	 * @return
	 * @throws CommonException
	 */
	public boolean isPortUsed(int cellId)throws CommonException; 
	/**
	 * 删除设备
	 * @param cellId
	 * @throws CommonException
	 */
	public void deleteRC(int cellId)throws CommonException; 
	/**
	 * 判断是否存在测试路由
	 * @param cellId
	 * @return
	 * @throws CommonException
	 */
	public boolean testRouteExist(int cellId)throws CommonException; 
	
}
