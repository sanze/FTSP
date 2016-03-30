package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.model.PushAlarmModel;

//import com.fujitsu.common.Define;
//import com.fujitsu.model.PushAlarmModel;

public interface AlarmManagementMapper {

	
	/**
	 * 获取当前告警条数
	 * @param map 查询条件
	 * @return
	 */
	public int queryCurrentAlarmCount(@Param(value = "map") Map<String, Object> map);
	
//	/**
//	 * 获取跳转时当前告警条数
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public int queryCurrentAlarmCountForSkip(@Param(value = "map") Map<String, Object> map,
//								@Param(value = "userId") int userId);
//	
//	/**
//	 * 获取拓扑连线上的告警数量
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public int queryCurrAlarmCountForTopoLine(@Param(value = "map") Map<String, Object> map,
//								@Param(value = "userId") int userId);
//	
//	/**
//	 * 获取拓扑连线上的告警
//	 * @param map
//	 * @param userId
//	 * @param start
//	 * @param limit
//	 * @return
//	 */
//	public List<Map<String, Object>> queryCurrAlarmForTopoLine(
//								@Param(value = "map") Map<String, Object> map,
//								@Param(value = "userId") int userId, 
//								@Param(value = "start") int start, 
//								@Param(value = "limit") int limit);
//	
	/**
	 * 获取当前告警记录
	 * @param map 查询条件
	 * @param userId 当前用户ID
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> queryCurrentAlarm(
							@Param(value = "map") Map<String, Object> map,
							@Param(value = "start") int start, 
							@Param(value = "limit") int limit);

	/**
	 * 获取设备告警
	 * 输出rcId,
	 * shelfNo,
	 * slotNo,
	 * severity
	 * @param rcId
	 * @return
	 */
	public List<Map<String, Object>> getEquipAlarm(@Param(value = "rcId") int rcId);
	
//	/**
//	 * 获取首页显示的告警统计数据
//	 * @param userId
//	 * @return
//	 */
//	public List<Map<String, Object>> getAlarmCountForFP(
//			@Param(value = "map") Map<String, Object> map,
//			@Param(value = "userId") int userId);
//	
//	/**
//	 * 获取未确认的告警数量
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public int getUnConfirmAlarmCount(
//			@Param(value = "map") Map<String, Object> map,
//			@Param(value = "userId") int userId);
//	
//	/**
//	 * 获取跳转时的当前告警记录
//	 * @param map
//	 * @param userId
//	 * @param start
//	 * @param limit
//	 * @return
//	 */
//	public List<Map<String, Object>> queryCurrentAlarmForSkip(
//							@Param(value = "map") Map<String, Object> map,
//							@Param(value = "userId") int userId, 
//							@Param(value = "start") int start, 
//							@Param(value = "limit") int limit);
	
	/**
	 * 告警确认
	 * @param map
	 * @param userId
	 */
	public void confirmAlarm(@Param(value = "map") Map<String, Object> map);
	
//	/**
//	 * 删除设备、线路告警屏蔽规则
//	 * @param regionId
//	 * @param userId
//	 */
//	public void deleteELAlarmShieldRule(@Param(value = "regionId") int regionId, 
//												@Param(value = "userId") int userId);
//	
//	/**
//	 * 删除网管告警屏蔽规则
//	 * @param userId
//	 */
//	public void deleteEMSAlarmShieldRule(@Param(value = "userId") int userId);
//	
//	/**
//	 * 设置设备告警、线路告警屏蔽规则
//	 * @param map
//	 * @param regionId
//	 * @param userId
//	 */
//	public void setELAlarmShieldRule(@Param(value = "map") Map<String, Object> map, 
//			@Param(value = "regionId") int regionId, @Param(value = "userId") int userId);
//	
//	/**
//	 * 设置网管告警屏蔽规则
//	 * @param map
//	 * @param userId
//	 */
//	public void setEMSAlarmShieldRule(@Param(value = "map") Map<String, Object> map, 
//											@Param(value = "userId") int userId);
	
	/**
	 * 查询历史告警数量
	 * @param map
	 * @return
	 */
	public int queryHistoryAlarmCount(@Param(value = "map") Map<String, Object> map);
	
	
	/**
	 * 查询历史告警
	 * @param map
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> queryHistoryAlarm(
							@Param(value = "map") Map<String, Object> map,
							@Param(value = "start") int start, 
							@Param(value = "limit") int limit);
	
//	/**
//	 * 获取测试链路信息
//	 * @param testResultId
//	 * @return
//	 */
//	public Map<String, Object> getTestLinkInfo(@Param(value = "testResultId") 
//																	int testResultId);
	
	/**
	 * 获取当前告警
	 * @param alarm
	 * @param eqptId/alarmName/alarmType/eqptType/slotNo/portNo/cardType
	 * @return
	 */
	public Map<String, Object> getRTUCurrentAlarm(@Param(value = "alarm") PushAlarmModel alarm);
	
//	/**
//	 * 获取OSM当前告警
//	 * @param alarm
//	 * @return
//	 */
//	public List<Map<String, Object>> getOSMCurrAlarmList(@Param(value = "alarm") 
//															PushAlarmModel alarm);
//	
//	/**
//	 * 获取OSM当前告警
//	 * @param alarm
//	 * @return
//	 */
//	public Map<String, Object> getOSMCurrAlarm(@Param(value = "alarm") PushAlarmModel alarm);
//	
//	/**
//	 * 获取线路当前告警
//	 * @param alarm
//	 * @return
//	 */
//	public Map<String, Object> getSFMCurAlarm(@Param(value = "alarm") PushAlarmModel alarm);
//	
//	/**
//	 * 获取SFM设备告警
//	 * @param alarm
//	 * @return
//	 */
//	public List<Map<String, Object>> getSFMCurAlarmList(@Param(value = "alarm") 
//																PushAlarmModel alarm);
//	
//	/**
//	 * 获取SFM通道告警
//	 * @param alarm
//	 * @return
//	 */
//	public List<Map<String, Object>> getSFMChannelAlarmList(@Param(value = "alarm") 
//																PushAlarmModel alarm);
//	
//	/**
//	 * 获取SFM通道告警
//	 * @param alarm
//	 * @return
//	 */
//	public Map<String, Object> getSFMChannelAlarm(@Param(value = "alarm") 
//														PushAlarmModel alarm);
	
	/**
	 * 新增历史告警
	 * @param map
	 */
	public void addHistoryAlarm(@Param(value = "map") Map<String, Object> map);
	
	/**
	 * 删除当前告警
	 * @param alarmId
	 */
	public void deleteCurAlarm(@Param(value = "alarmId") int alarmId);
	
	/**
	 * 新增RTU当前告警
	 * @param alarm
	 */
	public void addRTUCurAlarm(@Param(value = "alarm") PushAlarmModel alarm);
	
//	/**
//	 * 新增OSM当前告警
//	 * @param alarm
//	 */
//	public void addOSMCurrAlarm(@Param(value = "alarm") PushAlarmModel alarm);
//	
//	/**
//	 * 新增线路当前告警
//	 * @param alarm
//	 */
//	public void addSFMCurAlarm(@Param(value = "alarm") PushAlarmModel alarm);
//	
//	/**
//	 * 新增SFM通道告警
//	 * @param alarm
//	 */
//	public void addSFMChannelCurAlarm(@Param(value = "alarm") PushAlarmModel alarm);
//	
//	/**
//	 * 获取告警屏蔽规则
//	 * @param regionId
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Object> getELShieldRule(@Param(value = "regionId") int regionId, 
//														@Param(value = "userId") int userId);
//	
//	/**
//	 * 获取网管告警屏蔽规则
//	 * @param regionId
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Object> getEMSShieldRule(@Param(value = "userId") int userId);
//	
//	/**
//	 * 获取用户可见的regionIds
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Object> getRegionIds(@Param(value = "userId") int userId);
//	
//	/**
//	 * 获取前台combox用的区域信息
//	 * @param regionIdList
//	 * @return
//	 */
//	public List<Map<String, Object>> queryRegionForCombox(
//					@Param(value = "regionIdList") List<Integer> regionIdList);
//	
//	/**
//	 * 获取区域内的机房信息
//	 * @param map
//	 * @return
//	 */
//	public List<Map<String, Object>> getStationsInRegion(@Param(value = "map") 
//															Map<String, Object>map);
//	
//	/**
//	 * 通过regionIds获取设备的名称
//	 * @param map
//	 * @return
//	 */
//	public List<Map<String, Object>> getEqptNameByRegionIds(@Param(value = "map") 
//																Map<String, Object> map);
//	
//	/**
//	 * 通过stationIds获取设备的名称
//	 * @param map
//	 * @return
//	 */
//	public List<Map<String, Object>> getEqptNameByStationIds(@Param(value = "map") 
//																Map<String, Object> map);
	
	/**
	 * 通获取告警同步时的设备信息
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getAlarmSyncEquip(@Param(value = "map")
																Map<String, Object> map);
	
//	public List<Map<String, Object>> getRTUInfoList(@Param(value = "rtuNoList") 
//																List<String> rtuNoList);
	
	/**
	 * 通过设备编号获取设备信息
	 * @param rcNoList
	 * @param rcType
	 * @return
	 */
	public List<Map<String, Object>> getRCListByNo(@Param(value = "rcNoList") 
			List<String> rcNoList, @Param(value = "type") int rcType);
	
	
//	/**
//	 * 通过rcNo获取RTU/CTU信息
//	 * @param rcNo
//	 * @return
//	 */
//	public Map<String, Object> getRCByRCNo(@Param(value = "rcNo") String rcNo);
	
	/**
	 * 通过编号获取RTU的信息
	 * @param rtuNo
	 * @return
	 */
	public Map<String, Object> getRTUByNo(@Param(value = "rtuNo") String rtuNo);
	
	/**
	 * 获取机盘型号
	 * @param rcId
	 * @param slotNo
	 * @return
	 */
	public Map<String, Object> getCardType(@Param(value = "rcId") int rcId, 
											@Param(value = "slotNo") int slotNo);
	
//	/**
//	 * 通过sfmNo获取SFM信息
//	 * @param sfmNo
//	 * @return
//	 */
//	public Map<String, Object> getSFMBySFMNo(@Param(value = "sfmNo") String sfmNo);
//	
//	
//	public List<Map<String, Object>> getCTUInfoList(@Param(value = "ctuNoList") 
//																List<String> ctuNoList);
//	
//	public List<Map<String, Object>> getOSMInfoList(@Param(value = "osmNoList") 
//																List<String> osmNoList);
//	
//	public List<Map<String, Object>> getSFMInfoList(@Param(value = "sfmNoList") 
//																List<String> sfmNoList);
//	
//	/**
//	 * 通过ID获取RTU/CTU信息
//	 * @param rcId
//	 * @return
//	 */
//	public Map<String, Object> getRCByRCId(@Param(value = "rcId") int rcId);
//	
//	/**
//	 * 通过ID获取OSM信息
//	 * @param osmId
//	 * @return
//	 */
//	public Map<String, Object> getOSMByOSMId(@Param(value = "osmId") int osmId);
//	
//	/**
//	 * 通过ID获取SFM信息
//	 * @param sfmId
//	 * @return
//	 */
//	public Map<String, Object> getSFMBySFMId(@Param(value = "sfmId") int sfmId);
//	
//	/**
//	 * 获取机房中在线的设备
//	 * @param stationId
//	 * @return
//	 */
//	public List<Map<String, Object>> getOnLineEqptInStation(@Param(value = "stationId") int stationId);
//	
//	/**
//	 * 获取机房中所有在线设备的告警
//	 * @param stationId
//	 * @return
//	 */
//	public List<Map<String, Object>> getAllOnlineEqptAlarmInStation(@Param(value = "stationId") int stationId);
//	
//	/**
//	 * 获取区域中不同级别告警的个数
//	 * @param regionId
//	 * @return
//	 */
//	public int getRegionAlarmCount(@Param(value = "regionId") int regionId, 
//			@Param(value = "alarmLevel") int alarmLevel);
//	
//	/**
//	 * 获取区域离线告警
//	 * @param regionId
//	 * @return
//	 */
//	public List<Map<String, Object>> getRegionOutlineAlarm(@Param(value = "regionId") int regionId);
//	
//	/**
//	 * 获取机房告警个数
//	 * @param stationId
//	 * @param alarmLevel
//	 * @return
//	 */
//	public int getStationAlarmCount(@Param(value = "stationId") int stationId, @Param(value = "alarmLevel") int alarmLevel);
//	
//	/**
//	 * 获取机房cr级别告警个数
//	 * @param stationId
//	 * @return
//	 */
////	public int getStationCRAlarmCount(@Param(value = "stationId") int stationId);
//	
//	/**
//	 * 获取机房mj级别告警个数
//	 * @param stationId
//	 * @return
//	 */
////	public int getStationMJAlarmCount(@Param(value = "stationId") int stationId);
//	
//	/**
//	 * 获取机房mn级别告警个数
//	 * @param stationId
//	 * @return
//	 */
////	public int getStationMNAlarmCount(@Param(value = "stationId") int stationId);
//	
//	/**
//	 * 获取机房wr级别告警个数
//	 * @param stationId
//	 * @return
//	 */
////	public int getStationWRAlarmCount(@Param(value = "stationId") int stationId);
//	
//	/**
//	 * 获取机房内所有设备状态（在线或离线）
//	 * @param stationId
//	 * @return
//	 */
//	public List<Map<String, Object>> getAllEqptStatusInStation(
//									@Param(value = "stationId") int stationId);
//	
//	/**
//	 * 获取设备不同级别告警个数
//	 * @param eqptType
//	 * @param eqptId
//	 * @return
//	 */
//	public int getEqptAlarmCount(@Param(value = "eqptType") int eqptType,
//			@Param(value = "eqptId") int eqptId, @Param(value = "alarmLevel") int alarmLevel);
//	
//	/**
//	 * 获取端口告警信息
//	 * @param eqptType
//	 * @param cardType
//	 * @param eqptId
//	 * @param slotNo
//	 * @param portNo
//	 * @param alarmLevel
//	 * @return
//	 */
//	public int getPortAlarm(@Param(value = "eqptType") int eqptType, 
//			@Param(value = "cardType") int cardType, 
//			@Param(value = "eqptId") int eqptId, 
//			@Param(value = "slotNo") int slotNo, 
//			@Param(value = "portNo") int portNo, 
//			@Param(value = "alarmLevel") int alarmLevel);
//	
//	/**
//	 * 通过告警名称获取服务器性能告警
//	 * @param alarmName
//	 * @return
//	 */
//	public Map<String, Object> getServerPMAlarm(@Param(value = "alarmName") String alarmName);
//	
//	/**
//	 * 增加服务器性能告警
//	 * @param alarmName
//	 * @param severity
//	 * @param alarmOccurDate
//	 */
//	public void addServerPMAlarm(@Param(value = "alarmName") String alarmName, 
//					@Param(value = "alarmType") int alarmType, 
//					@Param(value = "severity") int severity, 
//					@Param(value = "alarmOccurDate") Date alarmOccurDate);
//	
//	/**
//	 * 通过告警内容删除服务器性能告警
//	 * @param alarmName
//	 */
//	public void deleteServerPMAlarm(@Param(value = "alarmName") String alarmName);
//	
//	//获取所有一级区域
//	public List<Map<String, Object>> getAllFirstLevelRegion();
//	
//	//获取区域内的所有机房
//	public List<Map<String, Object>> getAllStationInRegion(@Param(value = "regionId") int regionId);
//	
//	//获取机房内的所有设备
//	public List<Map<String, Object>> getAllEqptInStation(@Param(value = "stationId") int stationId);
//	
//	//通过ID获取机房信息
//	public Map<String, Object> getStationById(@Param(value = "stationId") int stationId);
//	
//	//获取区域内的所有设备
//	public List<Map<String, Object>> getAllEqptInRegion(@Param(value = "regionId") int regionId);
//	
//	/**
//	 * 获取系统中所有的设备RTU/CTU/OSM/SFM
//	 * @return
//	 */
//	public List<Map<String, Object>> getAllEqptInfo();
//	
//	//通过设备类型和设备ID删除当前告警表中的记录
//	public void dltCurAlarmByTypeAndId(@Param(value = "eqptType") int eqptType, @Param(value = "eqptId") int eqptId);
//	
//	//通过设备类型和设备ID删除历史告警表中的记录
//	public void dltHisAlarmByTypeAndId(@Param(value = "eqptType") int eqptType, @Param(value = "eqptId") int eqptId);
//	
//	//获取区域内所有的可见告警
//	public List<Map<String, Object>> getAllVisibleAlarmInRegion(@Param(value = "regionId") int regionId);
//	
//	//获取所有的离线告警
//	public List<Map<String, Object>> getAllOfflineAlarm();
//	
//	/**
//	 * 增加设备离线告警
//	 * @param map
//	 * EQPT_ID 设备ID
//	 * EQPT_IP 设备IP
//	 * EQPT_TYPE 设备类型
//	 * STATION_ID 机房ID
//	 * REGION_ID 区域ID
//	 * ALARM_NAME 告警名称
//	 * ALARM_TYPE 告警类型
//	 * ALARM_LEVEL 告警级别
//	 * ALARM_VISIBLE_FLAG 告警可见标记
//	 * ACK_STATUS 告警确认状态
//	 * ALARM_OCCUR_DATE 告警发生时间
//	 */
//	public void addNodeOfflineAlarm(@Param(value = "map") Map<String, Object> map);
//	
//	/**
//	 * 获取根节点
//	 * @return
//	 */
//	public Map<String, Object> getRoot();
//	
//	/**
//	 * 获取"所有设备离线告警"
//	 * @param eqptType
//	 * @return
//	 */
//	public Map<String, Object> getORTSOfflineAlarm(@Param(value = "eqptType") int eqptType);
//	
//	/**
//	 * 通过测试结果ID获取测试链路信息
//	 * @param testResultId
//	 * @return
//	 */
//	public List<Map<String, Object>> getLinkByTestResultId(@Param(value = "testResultId") int testResultId);
//	
//	/**
//	 * 通过测试结果ID获取设备信息
//	 * @param testResultId
//	 * @return
//	 */
//	public Map<String, Object> getEqptInfoByTestResultId(@Param(value = "testResultId") int testResultId);
//	
//	/**
//	 * 通过linkId获取设备信息
//	 * @param linkId
//	 * @return
//	 */
//	public Map<String, Object> getEqptInfoByLinkId(@Param(value = "linkId") int linkId);
//	
//	/**
//	 * 通过测试结果ID获取测试计划信息
//	 * @param testResultId
//	 * @return
//	 */
//	public Map<String, Object> getTestPlanByTestResultId(@Param(value = "testResultId") int testResultId);
//	
//	/**
//	 * 新增当前告警
//	 * @param map
//	 */
//	public void addCurrentAlarm(@Param(value = "map") Map<String, Object> map);
//	
//	/**
//	 * 通过测试结果ID获取测试结果记录
//	 * @param testResultId
//	 * @return
//	 */
//	public Map<String, Object> getTestResultById(@Param(value = "testResultId") int testResultId);
//	
//	/**
//	 * 使设备的非离线告警不可见
//	 * @param map
//	 * EQPT_ID 设备ID
//	 * EQPT_IP 设备IP
//	 * EQPT_TYPE 设备类型
//	 * STATION_ID 机房ID
//	 * REGION_ID 区域ID
//	 * ALARM_VISIBLE_FLAG 告警可见标记
//	 */
//	public void changeVisibleStatusOfEqptAlarm(@Param(value = "map") Map<String, Object> map);
//	
//	/**
//	 * 获取设备离线告警
//	 * @param eqptType
//	 * @param eqptId
//	 * @return
//	 */
//	public Map<String, Object> getEqptOfflineAlarm(@Param(value = "eqptType") int eqptType,
//												@Param(value = "eqptId") int eqptId);
//	
//	/**
//	 * 移除设备离线告警
//	 * @param eqptType
//	 * @param eqptId
//	 */
//	public void removeEqptOfflineAlarm(@Param(value = "eqptType") int eqptType,
//			@Param(value = "eqptId") int eqptId);
//	
//	/**
//	 * 获取当前测试告警
//	 * @param map
//	 */
//	public List<Map<String, Object>> getCurrentTestAlarm(@Param(value = "map") Map<String, Object> map);
//	
//	/**
//	 * 通过参数名获取参数信息
//	 * @param configName
//	 * @return
//	 */
//	public List<Map<String, Object>> getSysConfigByConfigName(@Param(value = "configName") String configName);
//	
	
	/**
	 * 获取设备的当前告警
	 * @param eqptType
	 * @param eqptId
	 * @return
	 */
	public List<Map<String, Object>> getEqptCurrAlarm(@Param(value = "eqptType") int eqptType, @Param(value = "eqptId") int eqptId);
	
//	/**
//	 * 使设备告警可见
//	 * @param eqptType
//	 * @param eqptId
//	 */
//	public void visibleEqptCurrAlarm(@Param(value = "eqptType") int eqptType, @Param(value = "eqptId") int eqptId);
	
//	/**
//	 * 获取测试链路信息
//	 * @return
//	 */
//	public Map<String, Object> getLinkById(@Param(value = "linkId") String linkId);
//	
//	/**
//	 * 获取光路信息
//	 * @return
//	 */
//	public List<Map<String, Object>> getOpticalCableSections(@Param(value = "opticalId") String opticalId);
//
//	public Map<String, Object> getTestInfoForAnalyze(@Param(value = "testResultId") String testResultId);
//	
//	/**
//	 * 通过linkId获取测试计划信息
//	 * @param linkId
//	 * @return
//	 */
//	public List<Map<String, Object>> getTestPlanByLinkId(@Param(value = "linkId") int linkId);
//	
//	/**
//	 * 通过opmPortId获取opm的信息
//	 * @param portId
//	 * @return
//	 */
//	public Map<String, Object> getOPMByOPMPortId(@Param(value = "portId") int portId);
//	
//	/**
//	 * 获取所有的测试告警
//	 * @return
//	 */
//	public List<Map<String, Object>> getAllTestAlarm();
//	
//	/**
//	 * 获取测试结果记录
//	 * @param testResultIds
//	 * @return
//	 */
//	public List<Map<String, Object>> getTestResultList(@Param(value = "testResultIds") List<Integer> testResultIds);
	
}
