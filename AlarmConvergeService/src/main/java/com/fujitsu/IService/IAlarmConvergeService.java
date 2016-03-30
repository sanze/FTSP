package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IAlarmConvergeService {

	/**
	 * 初始化告警收敛
	 * @throws CommonException
	 */
	public void init() throws CommonException;
	
	/**
	 * 启用指定的告警收敛规则
	 * @param ruleId
	 */
	public void startAlarmConvergeById(int[] ruleIds) throws CommonException;
	
	/**
	 * 挂起指定的告警收敛规则
	 * @param ruleId
	 */
	public void stopAlarmConvergeById(final int[] ruleIds) throws CommonException;
	
	/**
	 * 增加指定的告警收敛规则
	 * @param ruleId
	 */
	public void addAlarmConverge(int ruleId) throws CommonException;
	
	/**
	 * 删除指定的告警收敛规则
	 * @param ruleId
	 */
	public void deleteAlarmConverge(int[] ruleIds) throws CommonException;
	
	/**
	 * 更新指定的告警收敛规则
	 * @param ruleId
	 * @throws CommonException
	 */
	public void updateAlarmConverge(int ruleId) throws CommonException;
	
	/**
	 * 获取指定告警收敛规则的启用状态
	 * @param ruleId
	 */
	public int getAlarmConvergeUseStatus(int ruleId) throws CommonException;
	
	/**
	 * 获取指定告警收敛规则的运行状态
	 * @param ruleId
	 * @return
	 */
	public int getAlarmConvergeOperStatus(int ruleId) throws CommonException;
	
	/**
	 * 手动立即执行指定的告警收敛规则
	 * @param ruleId
	 * @throws CommonException
	 */
	public boolean manualSatarAlarmConverge(int ruleId) throws CommonException;

	/**
	 * 获取当前告警收敛服务信息
	 * 
	 */
	public List<Map<String, Object>> getAlarmConvergeServiceInfo() throws CommonException;
}
