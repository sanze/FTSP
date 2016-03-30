package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

import com.fujitsu.dao.mysql.bean.Contact;

public interface KeyAccountMapper {
	/**
	 * 根据客户id,分页获取查询该客户下所有联系人
	 * @param accountId
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Contact> selectContact(@Param("start")Integer start,@Param("limit")Integer limit);
	/**
	 * 根据客户id查询该客户下所有联系人总数
	 * @param accountId
	 * @return
	 */
	int selectContactTotal();
	
	/**
	 * 查询24小时内电路的设备性能越限数据的数量
	 * @param accountId
	 * @return
	 */
	int selectTcaCount(@Param("map")Map map);
	/**
	 * 查询大客户信息
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Map> getVIPInfo();
	/**
	 * 查询大客户总数
	 * @return
	 */
	int getVIPInfoCount();
	
	/**
	 * 分页查询大客户名称对应的电路信息
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Map> getCircuitInfo(@Param("clientName") String clientName,
			@Param("start") Integer start, @Param("limit") Integer limit);
	/**
	 * 查询大客户名称对应的电路总数
	 * @param clientName
	 * @return
	 */
	int getCircuitInfoCount(@Param("clientName")String clientName);
	
	/**查询电路经过的端口
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryPtpInCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	
	/**查询电路经过的CTP
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryCtpInCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	
	/**查询电路经过的NE
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryNeInCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	/**查询电路经过的板卡
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryEquipInCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	
	/**查询电路经过的端口
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryPtpInOtnCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	
	/**查询电路经过的CTP
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryCtpInOtnCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	
	/**查询电路经过的NE
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryNeInOtnCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	/**查询电路经过的板卡
	 * @param circuitInfoId
	 * @return
	 */
	List<Integer> queryEquipInOtnCircuit(@Param("circuitInfoId") Integer circuitInfoId);
	
	/**根据大客户名称查询不同速率电路的条数
	 * @param clientName
	 * @return
	 */
	List<Map> getGroupedCircuitsByVIPName(@Param("clientName")String clientName);
	/**
	 * 查询所有大客户相关的电路信息，信息中包含大客户的名称信息
	 * @return
	 */
	List<Map> searchVIPWithCircuitInfo();
	/**
	 * 查询所有没有完成的割接任务的影响电路的信息，信息中包括割接任务名称，开始、结束时间
	 * @return
	 */
	List<Map> searchCutoverTaskWithCircuitInfo();
	/**根据大客户名称查询otn电路条数
	 * @param clientName
	 * @return
	 */
//	Integer getOtnCircuitCount(@Param("clientName")String clientName);
}
