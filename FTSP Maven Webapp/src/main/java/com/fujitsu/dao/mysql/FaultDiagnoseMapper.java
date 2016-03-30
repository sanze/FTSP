package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface FaultDiagnoseMapper {

	/**
	 * 获取故障诊断规则记录数
	 * @return
	 */
	public int getFaultDiagnoseRulesCount();
	
	/**
	 * 获取故障诊断规则记录
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> getFaultDiagnoseRules(@Param(value = "start") int start, @Param(value = "limit") int limit);
	
	/**
	 * 通过ID获取故障诊断规则详情
	 * @param diagnoseId
	 * @return
	 */
	public Map<String, Object> getFaultDiagnoseDetailById(@Param(value = "diagnoseId") int diagnoseId);
	
	/**通过diagnoseId获取故障诊断条件
	 * @param diagnoseId
	 * @return
	 */
	public List<Map<String, Object>> getCondByDiagnoseId(@Param(value = "diagnoseId") int diagnoseId);
	
	/**
	 * 通过diagnoseId获取故障诊断执行动作
	 * @param diagnoseId
	 * @return
	 */
	public List<Map<String, Object>> getActionByDiagnoseId(@Param(value = "diagnoseId") int diagnoseId);
	
	/**
	 * 获取故障诊断规则的适用范围
	 * @param diagnoseId
	 * @return
	 */
	public List<Map<String, Object>> getApplyScope(@Param(value = "diagnoseId") int diagnoseId);
	
	/**
	 * 取指定网管下的网元型号和工厂信息
	 * @param emsIds
	 * @return
	 */
	public List<Map<String, Object>> getApplyEquips(@Param(value = "emsIds") List<Integer> emsIds);
	
	/**
	 * 获取指定故障诊断规则的适用范围
	 * @param diagnoseId
	 * @return
	 */
	public List<Map<String, Object>> getFaultDiagnoseEms(@Param(value = "diagnoseId") int diagnoseId);
	
	/**
	 * 获取指定故障诊断规则的适用设备
	 * @param diagnoseId
	 * @return
	 */
	public List<Map<String, Object>> getFaultDiagnoseEquips(@Param(value = "diagnoseId") int diagnoseId);
	
	public void deleteTableById(@Param (value="map") Map<String, Object> map);
	
	public void addFaultDiagnoseScope(@Param(value="map") Map<String, Object> map);
	
	public void addFaultDiagnoseEquipment(@Param(value="map") Map<String, Object> map);
	
	public void addFaultDiagnoseCondition(@Param(value="param") List<Map<String, Object>> param);
	
	public void setSysParam(@Param(value = "key") String key, @Param(value = "value") String value);
	
	public List<Map<String, Object>> getSysParam(@Param(value = "key") String key);
}
