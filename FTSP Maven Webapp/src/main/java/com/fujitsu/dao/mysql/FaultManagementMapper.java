package com.fujitsu.dao.mysql;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface FaultManagementMapper {
	
	/**
	 * 获取故障记录数
	 * @param map
	 * @return
	 */
	public int getFaultCount(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * 获取故障信息
	 * @param map
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> getFaultList(
			@Param(value = "map") Map<String, Object> map, 
			@Param(value = "start") int start, 
			@Param(value = "limit") int limit);
	
	/**
	 * 通过faultId删除故障记录
	 * @param faultId
	 */
	public void deleteFaultByFaultId(@Param(value = "faultId") int faultId);

	/**
	 * 通过faultId删除与故障相关的告警
	 * @param faultId
	 */
	public void deleteFaultAlarmByFaultId(@Param(value = "faultId") int faultId);
	
	/**
	 * 通过faultId获取设备故障
	 * @param faultId
	 */
	public Map<String, Object> getEqptFaultInfoById(@Param(value = "faultId") int faultId);
	
	/**
	 * 通过faultId获取线路故障
	 * @param faultId
	 * @return
	 */
	public Map<String, Object> getLineFaultInfoById(@Param(value = "faultId") int faultId);
	
	/**
	 * 获取与故障相关的告警数量
	 * @param faultId
	 * @return
	 */
	public int getFaultAlarmCount(@Param(value = "faultId") int faultId);
	
	/**
	 * 获取与故障相关的告警记录
	 * @param faultId
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> getFaultAlarmList(
			@Param(value = "faultId") int faultId, 
			@Param(value = "start") int start, 
			@Param(value = "limit") int limit);
	
	
	/**
	 * 通过unitId获取传输系统名称
	 * @param unitId
	 * @return
	 */
	public List<Map<String, Object>> getSysNameByUnitId(@Param(value = "unitId") int unitId);
	
	/**
	 * 通过neId获取传输系统名称
	 * @param unitId
	 * @return
	 */
	public List<Map<String, Object>> getSysNameByNeId(@Param(value = "neId") int neId);
	
	/**
	 * 通过neId获取网元信息
	 * @param neId
	 * @return
	 */
	public Map<String, Object> getNeInfoByNeId(@Param(value = "neId") int neId);
	
	/**
	 * 删除故障相关的告警
	 * @param alarmId
	 */
	public void deleteFaultAlarmById(@Param(value = "alarmId") int alarmId);
	
	/**
	 * 增加故障相关的告警
	 * @param list
	 */
	public void addFaultAlarm(@Param(value = "faultId") int faultId, @Param(value = "list") List<Map<String, Object>> list);
	
	/**
	 * 获取传输系统列表
	 * @return
	 */
	public List<Map<String, Object>> getTransformSystemList();
	
	/**
	 * 获取光缆信息列表
	 * @return
	 */
	public List<Map<String, Object>> getCableList();
	
	/**
	 * 获取所有的故障告警
	 * @param faultId
	 * @return
	 */
	public List<Map<String, Object>> getAllFaultAlarm(@Param(value = "faultId") int faultId);
	
	/**
	 * 获取光缆段信息列表
	 * @return
	 */
	public List<Map<String, Object>> getCableSectionList(@Param(value = "cablesId") int cablesId);
	
	
	/**
	 * 获取故障原因列表
	 * @return
	 */
	public List<Map<String, Object>> getFaultReasonList(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * 更新故障信息
	 * @param map
	 */
	public void updateFaultInfo(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * 新增故障信息
	 * @param map
	 */
	public void addFaultInfo(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * 修改故障状态
	 * @param faultId
	 */
	public void updateFaultStatus(@Param(value = "faultId") int faultId, @Param(value = "status") int status);
	
	/**
	 * 故障恢复
	 * @param faultId
	 * @param status
	 */
	public void faultRecovery(@Param(value = "faultId") int faultId, 
			@Param(value = "status") int status, @Param(value = "recoveryTime") Date recoveryTime);
	
	/**
	 * 获取故障的主告警
	 * @param faultId
	 * @return
	 */
	public List<Map<String, Object>> getFaultMainAlarm(@Param(value = "faultId") int faultId);
	
	/**
	 * 通过faultId获取故障信息
	 * @param faultId
	 * @return
	 */
	public Map<String, Object> getFaultInfoByFaultId(@Param(value = "faultId") int faultId);
	
	/**
	 * 获取今天最大的人工故障序列号
	 * @param from
	 * @param to
	 * @return
	 */
	public Map<String, Object> getMaxManualSerialNoToday(@Param(value = "from") Date from, @Param(value = "to") Date to);
	
	/**
	 * 获取所有未确认的故障
	 * @return
	 */
	public List<Map<String, Object>> getAllUnconfirmedFault();
	
	/**
	 * 通过alarmId获取故障相关的告警
	 * @param alarmId
	 * @return
	 */
	public Map<String, Object> getFaultAlarmById(@Param(value = "alarmId") int alarmId);
	
	public List<Map<String, Object>> getFaultAlarmByIds(@Param(value = "ids") List<Integer> ids);
	
	/**
	 * 通过ID获取用户信息
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getUserById(@Param(value = "userId") int userId);
	
	/**
	 * 故障信息中加入确认者名字
	 * @param faultId
	 * @param userName
	 */
	public void addAckUser(@Param(value = "faultId") int faultId, @Param(value = "userName") String userName);
	
	/**
	 * 更新故障告警开始和结束时间
	 * @param faultId
	 * @param time
	 */
	public void updateAlarmStartAndEndTime(@Param(value = "faultId") int faultId, @Param(value = "time") Map<String, Object> time);
	
	
	
	
	
	
	
	
}
