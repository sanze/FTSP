package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.bean.Contact;

public interface IKeyAccountManagerService {
	/**
	 * 根据客户id,查询该客户下所有联系人
	 * 
	 * @param userId
	 * @return
	 */
	List<Contact> selectContact(Integer userId) throws CommonException;

	/**
	 * 根据客户id,分页获取查询该客户下所有联系人
	 * 
	 * @param userId
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Contact> selectContact(Integer start, Integer limit)
			throws CommonException;

	/**
	 * 根据客户id,查询该客户下所有联系人,Map中封装记录总数:total和记录:records
	 * 
	 * @param userId
	 * @return
	 */
	Map<String, Object> getContactInfo(Integer userId) throws CommonException;

	/**
	 * 根据客户id,分页获取查询该客户下所有联系人,Map中封装记录总数:total和记录:records
	 * 
	 * @param userId
	 * @param start
	 * @param limit
	 * @return
	 */
	Map<String, Object> getContactInfo(Integer start, Integer limit)
			throws CommonException;

	/**
	 * 分页查询大客户信息
	 * 
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getVIPInfo(Integer start, Integer limit)
			throws CommonException;
	/**
	 * 查询大客户信息（不带告警信息）
	 * 
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getVIPInfoWithoutAlarmInfo(Integer start, Integer limit)
			throws CommonException;
	/**
	 * 根据大客户名称查询电路信息
	 * 
	 * @param clientName
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getCircuitsByVIPName(String clientName, Integer start,
			Integer limit) throws CommonException;
	
	/**根据大客户名称查询电路信息(不带告警信息)
	 * @param clientName
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getCircuitsByVIPNameWithoutAlarmInfo(String clientName,
			Integer start, Integer limit) throws CommonException;
	/**
	 * 根据大客户名称查询不同速率电路的条数
	 * 
	 * @param clientName
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getGroupedCircuitsByVIPName(String clientName)
			throws CommonException;
	
	/**查询大客户相关的割接任务及相关信息
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getCutoverInfoByVIPName() throws CommonException;
	
}
