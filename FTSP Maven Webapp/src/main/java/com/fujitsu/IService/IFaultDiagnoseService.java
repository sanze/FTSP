package com.fujitsu.IService;

import com.fujitsu.common.CommonException;

public interface IFaultDiagnoseService {

	/**
	 * 初始化故障诊断
	 * @throws CommonException
	 */
	public void init() throws CommonException;
	
	/**
	 * 启用指定的故障诊断规则
	 * @param ruleId
	 */
	public void startFaultDiagnoseById(int[] ruleIds) throws CommonException;
	
	/**
	 * 挂起指定的故障诊断规则
	 * @param ruleId
	 */
	public void stopFaultDiagnoseById(final int[] ruleIds) throws CommonException;
	
	/**
	 * 增加指定的故障诊断规则
	 * @param ruleId
	 */
	public void addFaultDiagnose(int ruleId) throws CommonException;
	
	/**
	 * 删除指定的故障诊断规则
	 * @param ruleId
	 */
	public void deleteFaultDiagnose(int[] ruleIds) throws CommonException;
	
	/**
	 * 更新指定的故障诊断规则
	 * @param ruleId
	 * @throws CommonException
	 */
	public void updateFaultDiagnose(int ruleId) throws CommonException;
	
	/**
	 * 获取指定故障诊断规则的启用状态
	 * @param ruleId
	 */
	public int getFaultDiagnoseUseStatus(int ruleId) throws CommonException;
	
	/**
	 * 获取指定故障诊断规则的运行状态
	 * @param ruleId
	 * @return
	 */
	public int getFaultDiagnoseOperStatus(int ruleId) throws CommonException;
	
	/**
	 * 手动立即执行指定的故障诊断规则
	 * @param ruleId
	 * @throws CommonException
	 */
	public boolean manualSatarFaultDiagnose(int ruleId) throws CommonException;

}
