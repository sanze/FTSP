package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TransSystemMapper {
	public List<Map<String, Object>> getAllTransmissionSystem(
			@Param(value = "param") Map<String, Object> param,
			@Param(value = "start") Integer start,
			@Param(value = "limit") Integer limit);
	public List<Map<String, Object>> getAllLink(
			@Param(value = "param") Map<String, Object> param,
			@Param(value = "start") Integer start,
			@Param(value = "limit") Integer limit);
	
	/**
	 * 获取用户可见的所有网元
	 * @param sysUserId
	 * @param tREE_DEFINE
	 * @return
	 */
	public List<Map<String, Object>> getAllVisibleNe(
				@Param(value = "userId")Integer sysUserId, 
				@Param(value = "Define") Map<String, Object> tREE_DEFINE);
	
	/**
	 * 查询传输系统记录数
	 * @param paramMap
	 * @return
	 */
	public int queryTransmissionSystemCount(
			@Param(value = "paramMap") Map<String, Object> paramMap);
	
	/**
	 * 查询传输系统记录
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> queryTransmissionSystem(
				@Param(value = "paramMap") Map<String, Object> paramMap, 
				@Param(value = "start") int start, 
				@Param(value = "limit") int limit);

	/**
	 * 删除传输系统时更新T_BASE_LINK表RESOURCE_TRANS_SYS_ID字段
	 * @param transSysId
	 */
	public void updateTransSysIdInTBaseLink(@Param(value = "transSysId") int transSysId);
	
	/**
	 * 删除传输系统时同时删除T_RESOURCE_TRANS_SYS_NE表中的记录
	 * @param transSysId
	 */
	public void dltTransSysNeByTransSysId(@Param(value = "transSysId") int transSysId);
	
	/**
	 * 删除T_RESOURCE_TRANS_SYS表中的记录
	 * @param transSysId
	 */
	public void dltTransSysByTransSysId(@Param(value = "transSysId") int transSysId);
	
	//----------------------333333333333333333333333333333333333333-------------
	/**
	 * 获得带区域信息的网元信息
	 * @param nodeList
	 * @param tREE_DEFINE 
	 * @param sysUserId 
	 * @return
	 */
	List<Map> getNeInfoWithArea(
			@Param(value = "nodeList") List<Map> nodeList,
			@Param(value = "userId")Integer sysUserId, 
			@Param(value = "Define") Map<String, Object> tREE_DEFINE,
			@Param(value = "idList") List<Integer> idList);
	/**
	 * 获得网元范围内的LINK
	 * @param idList
	 * @param paramMap 
	 * @return
	 */
	List<Map> getLinkBetweenNe(
			@Param(value = "idList") List<Integer> idList, @Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 检查系统名称是否重复
	 * @param paramMap
	 * @return
	 */
	public int checkIfSameSysName(@Param(value = "map") Map<String, String> paramMap);
	/**
	 * 检查系统代号是否重复
	 * @param paramMap
	 * @return
	 */
	public int checkIfSameSysCode(@Param(value = "map") Map<String, String> paramMap);

	/**
	 * 新建传输系统
	 * @param paramMap
	 * @param idMap
	 */
	public void newTransSystem(@Param(value = "paramMap") Map<String, String> paramMap,
			@Param(value = "idMap") Map<String, Long> idMap);

	/**
	 * 保存传输系统-网元
	 * @param intList
	 * @param paramMap
	 */
	public void saveTransSystemNe(@Param(value = "neList")  List intList,
			@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 保存传输系统-Link
	 * @param intList
	 * @param paramMap
	 */
	public void saveTransSystemLink(@Param(value = "paramMap") Map<String, String> paramMap);
	/**
	 * 查询双向Link的另一半
	 * @param paramMap
	 */
	public List<Map> getTheOtherLink(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 获取一个传输系统的信息
	 * @param paramMap
	 */
	public List<Map> getTransSystem(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 获取一个传输系统网元的信息
	 * @param paramMap
	 * @return
	 */
	public List<Long> getTransSystemNe(@Param(value = "paramMap")Map<String, String> paramMap);

	/**
	 * 获取一个传输系统的链路信息
	 * @param paramMap
	 * @return
	 */
	public List<Map> getTransSysLink(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 修改传输系统
	 * @param paramMap
	 */
	public void updateTransSystem(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 维护nodeCount
	 * @param paramMap
	 */
	public void updateNodeCount(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 检查ne是否可以删除，因为会有相关link
	 * @param intList
	 * @param paramMap
	 * @return
	 */
	public Integer checkIfNeDeletable(@Param(value = "idList") List<Integer> intList,
			@Param(value = "paramMap") Map<String, String> paramMap);
	
	/**
	 * 找到保护组及包含的ptp
	 * @param neList
	 * @return
	 */
	public List<Map> getProListByNe(@Param(value = "neList") List<Integer> neList);

	/** 
	 * 从一个ptp找到另一端的ptp
	 * @param ptpId
	 * @return
	 */
	public Map getZEnd(@Param(value = "aEndPtp") String ptpId);

	/**
	 * 找出一个保护类型的系统的最大系统名
	 * @param sysNameprefix
	 * @return
	 */
	public String getSystemLastName(@Param(value = "sysNameprefix") String sysNameprefix);

	/**
	 * 找到同组内的另一个ptp
	 * @param ptpId
	 * @return
	 */
	public String getProListByPtp(@Param(value = "ptpId") String ptpId);

	
	/**
	 * 通过传输系统Id查询出关联的局站集合
	 * @param tranSysId
	 * @return
	 */
	public List<Map> getRelStationByTranSysId(@Param(value = "tranSysId") Integer tranSysId);

}
