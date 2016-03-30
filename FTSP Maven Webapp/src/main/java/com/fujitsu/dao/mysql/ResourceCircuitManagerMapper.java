package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceCircuitManagerMapper {
	/**
	 * @author wangjian 
	 */
	/** *************************查询******************************* */

	/**
	 * 查询资源网元的总数
	 * 
	 * @param map
	 * @return
	 */
	public Map getResourceNeTotal(@Param(value = "map")
	Map map);

	/**
	 * 查询资源网元
	 * 
	 * @param map
	 * @param startNumber
	 * @param pageSize
	 * @return
	 */
	public List<Map> getResourceNe(@Param(value = "map")
	Map map, @Param(value = "start")
	int start, @Param(value = "limit")
	int limit);

	/** *************************新建******************************* */
	
	

	/**
	 * 新增网元关系对应表
	 * 
	 * @param map
	 */
	public void addResourceNe(@Param(value = "map")
	Map map);

	/**
	 * 插入稽核结果的绑定关系（稽核id和电路infoID）
	 * @param map
	 */
	public void insertResCompare(@Param(value = "map")
			Map map);

	/**
	 * 插入稽核路由信息
	 * @param map
	 */
	public void insertResRoute(@Param(value = "map")
			Map map);
	
	
	/**
	 * 获取资源电路的数目
	 * 
	 * @param name
	 * @return
	 */
	public int getResourceCircuitTotal(@Param(value = "name")
	String name);

	/**
	 * 获取资源电路
	 * 
	 * @param name
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map> getResourceCircuit(@Param(value = "name")
	String name, @Param(value = "start")
	int start, @Param(value = "limit")
	int limit);

	/**
	 * 根据id查询出ftsp电路的路径数
	 * @param map
	 * @return
	 */
	public List<Map> getFtspRouteNumber(@Param(value = "resCirId")
			String resCirId);
	
	/**
	 * 根据组合条件关联查询出时隙id
	 * @param map
	 * @return
	 */
	public List<Map> getCtpId(@Param(value = "map")
			Map map);
	/** *************************更新******************************* */

	/**
	 * 更新网元关系对应表
	 * 
	 * @param map
	 */
	public void updateResourceNe(@Param(value = "map")
	Map map);

	/**
	 * 更新稽核电路表
	 * @param map
	 */
	public void updateResCir(@Param(value = "map")
			Map map);
	
	/**
	 * 插入稽核电路表
	 * @param map
	 */
	public void insertResCir(@Param(value = "map")
			Map map);
	
	/** *************************删除******************************* */

	/**
	 * 删除网元关系
	 * 
	 * @param map
	 */
	public void deleteResourceNe(@Param(value = "map")
	Map map);
	
	/**
	 * 根据网元名称获取网元Id
	 * @param neName
	 * @return
	 */
	public List<Integer> getNeIdByName(@Param(value = "neName")String neName,@Param(value = "emsIp")String emsIp);
	
	/**
	 * 根据指定ctp查询到相关电路,并更新电路信息
	 * @param ctpId 时隙Id
	 * @param record resource等信息
	 */
	public void updateCircuitResource(@Param(value = "map")Map map);
}