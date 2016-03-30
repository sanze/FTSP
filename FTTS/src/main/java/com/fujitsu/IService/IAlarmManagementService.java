package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonResult;

public interface IAlarmManagementService {
	
	/**
	 * 查询当前告警
	 * @param map 查询条件
	 * @param start
	 * @param limit
	 * @return
	 */
	public Map<String, Object> queryCurrentAlarm(Map<String, Object> map, int start, int limit);
	
	/**
	 * 获取设备告警
	 * 输出rcId,
	 * shelfNo,
	 * slotNo,
	 * severity
	 * @param rcId
	 * @return
	 */
	public List<Map<String, Object>> getEquipAlarm(int rcId);
	
	
	/**
	 * 获取首页显示告警数据
	 * @param userId
	 * @return
	 */
//	public Map<String, Integer> getAlarmCountForFP(int userId);
	
	/**
	 * 查询历史告警
	 * @param map
	 * @param start
	 * @param limit
	 * @return
	 */
	public Map<String, Object> queryHistoryAlarm(Map<String, Object> map, int start, int limit);
	
	/**
	 * 告警确认
	 * @param map
	 * @return
	 */
	public CommonResult confirmAlarm(Map<String, Object> map);
	
	/**
	 * 同步告警
	 * @param map
	 * @param userId
	 * @return
	 */
	public Map<String, Object> syncAlarm(Map<String, Object> map);
	
	/**
	 * 处理rtu推送告警
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public void handleRTUPushAlarm(Map map);
	
	/**
	 * 设定告警屏蔽规则
	 * @param map
	 * @param userId
	 * @return
	 */
//	public CommonResult setAlarmShieldRules(Map<String, Object> map, int userId);
	
	/**
	 * 为导出报表查询当前告警
	 * @param map
	 * @param userId
	 * @return
	 */
//	public List<Map<String, Object>> queryCurrentAlarmForReport(Map<String, Object> map, int userId);
	
	/**
	 * 为导出报表查询历史告警
	 * @param map
	 * @param userId
	 * @return
	 */
//	public List<Map<String, Object>> queryHistoryAlarmForReport(Map<String, Object> map, int userId);
	
	/**
	 * 获取告警屏蔽规则
	 * @param map
	 * @param userId
	 * @return
	 */
//	public Map<String, Object> getELShieldRule(Map<String, Object> map, int userId);
	
	
	/**
	 * 获取网管告警屏蔽规则
	 * @param userId
	 * @return
	 */
//	public Map<String, Object> getEMSShieldRule(int userId);
	
	/**
	 * 获取区域内的机房信息
	 * @param map
	 * @param userId
	 * @return
	 */
//	public Map<String, Object> getStationsInRegion(Map<String, Object> map, int userId);
	
	/**
	 * 获取前台combox用的区域信息
	 * @return
	 */
//	public Map<String, Object> queryRegionForCombox(int userId);
	
	/**
	 * 获取设备名称
	 * @param map
	 * @return
	 */
//	public Map<String, Object> getEqptName(Map<String, Object> map, int userId);
	
	/**
	 * 获取前台同步告警时所需的设备信息
	 * @param map
	 * @return
	 */
	public Map<String, Object> getAlarmSyncEquip(Map<String, Object> map);
	
	/**
	 * 通过测试结果ID获取测试计划ID
	 * @param map
	 * @return
	 */
//	public Map<String, Object> getPlanIdByTestResultId(Map<String, Object> map);
	
	//----------------------- 设备推送告警接口 --------------------------------
	//解析RTU告警
//	public String analyzeRTUAlarm(List<CMDRTUAlarm> alarmMapList);
	//解析SFM通道告警
//	public String analyzeSFMChannelAlarm(CMDSFMChannel channel);
	//解析SFM设备告警
//	public String analyzeSFMAlarm(CMDSFMChannel channel);
	
	//----------------------- 拓扑图获取告警接口 --------------------------------
	
	/**
	 * 获取区域告警统计数据
	 * @param regionId
	 * @return
	 */
//	public AlarmCountModel getRegionAlarmCount(int regionId);
	
	/**
	 * 获取机房告警统计数据
	 * @param stationId
	 * @return
	 */
//	public AlarmCountModel getStationAlarmCount(int stationId);
	
	/**
	 * 获取设备的告警统计数据
	 * @param eqptType
	 * @param eqptId
	 * @return
	 */
//	public AlarmCountModel getEqptAlarmCount(int eqptType, int eqptId);
	
	/**
	 * 获取连接线告警数
	 * @param linkList
	 * @return
	 */
//	public AlarmCountModel getLineAlarmCount(List<TopoLinkModel> linkList);
	
	//断点分析
//	public JSONObject breakPointAnalyze(String testResultId);
	
	//增加CPU使用率超门限告警
//	public void addCpuAlarm();
	
	//消除CPU使用率超门限告警
//	public void removeCpuAlarm();
	
	//增加内存使用率超门限告警
//	public void addMemoryAlarm();
	
	//消除内存使用率超门限告警
//	public void removeMemoryAlarm();
	
	//增加硬盘使用率超门限告警
//	public void addDiskAlarm();
	
	//消除硬盘使用率超门限告警
//	public void removeDiskAlarm();
	
	/**
	 * 增加设备离线告警
	 * @param map
	 * EQPT_ID 设备ID
	 * EQPT_IP 设备IP
	 * EQPT_TYPE 设备类型
	 * STATION_ID 机房ID
	 * REGION_ID 区域ID
	 */
//	public void addEqptOfflineAlarm(Map<String, Object> map);
	
	/**
	 * 移除设备离线告警
	 * @param map
	 * EQPT_ID 设备ID
	 * EQPT_IP 设备IP
	 * EQPT_TYPE 设备类型
	 * STATION_ID 机房ID
	 * REGION_ID 区域ID
	 */
//	public void removeEqptOfflineAlarm(Map<String, Object> map);
	
	/**
	 * 增加机房离线告警
	 * @param stationId
	 * @param regionId
	 */
//	public void addStationOfflineAlarm(int stationId, int regionId);
	
	/**
	 * 增加区域离线告警
	 * @param regionId
	 */
//	public void addRegionOfflineAlarm(int regionId);
	
	/**
	 * 增加所有设备离线告警
	 */
//	public void addORTSOfflineAlarm();
	
	/**
	 * 移除被删掉设备的当前告警和历史告警
	 * @param eqptType
	 * @param eqptId
	 */
//	public void removeDataOfDeletedEqpt(int eqptType, int eqptId);
	
	//新增测试链路告警
//	public void addTestAlarm(int testResultId,String breakInfo);
	
	
//	public void test();
}
