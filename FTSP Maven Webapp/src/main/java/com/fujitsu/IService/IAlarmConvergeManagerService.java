package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IAlarmConvergeManagerService { 
	/**
	 * 获取网管集下的设备信息
	 * @param ids
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getApplyEquips(List<Integer> ids)throws CommonException;  
	/**
	 * 查询告警收敛规则
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> searchAlarmConverge(int start,int limit)throws CommonException; 
	@SuppressWarnings("rawtypes")
	/**
	 * 获取sys表中设置的收敛时间
	 * @return
	 * @throws CommonException
	 */
	public Map getConvergeTime()throws CommonException;  
	/**
	 * 设置sys表中收敛时间
	 * @param map
	 * @throws CommonException
	 */
	public void setConvergeTime(Map<String,String> map)throws CommonException; 
	/**
	 * 新增告警收敛规则
	 * @param map
	 * @param ids
	 * @throws CommonException
	 */
	public int addAlarmConvergeRules(Map<String,Object> map,List<Integer>ids)throws CommonException;  
	/**
	 * 删除告警收敛规则
	 * @param convergeIds
	 * @throws CommonException
	 */
	public void deleteConvergeRules(String convergeIds)throws CommonException;    
	/**
	 * 修改或详情收敛规则初始化
	 * @param convergeId
	 * @return
	 * @throws CommonException
	 */
	public  Map<String,Object>  getAlarmConvergeDetailById(int convergeId)throws CommonException;  
	/**
	 * 修改告警收敛规则
	 * @param map
	 * @param ids
	 * @throws CommonException
	 */
	public void modifyAlarmConvergeRules(Map<String,Object> map,List<Integer>ids)throws CommonException;
	/**
	 * 更改规则状态，启用或挂起
	 * @param STATUS
	 * @param convergeIds
	 * @throws CommonException
	 */
	public void changeConvergeRuleStatus(int STATUS,List<Integer> convergeIds)throws CommonException;
  }
