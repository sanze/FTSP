package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.manager.faultManager.model.AlarmQueryCondition;
import com.fujitsu.manager.faultManager.model.EquipNameModel;
import com.fujitsu.manager.faultManager.model.FaultAlarmModel;
import com.fujitsu.manager.faultManager.model.FaultInfoModel;
import com.fujitsu.manager.faultManager.model.FaultProcessModel;
import com.fujitsu.manager.faultManager.model.FaultQueryCondition;
import com.fujitsu.manager.faultManager.model.StationQueryCondition;

public interface FaultStatisticsMapper {

	/**
	 * 故障统计
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<Map> getFaultStatisticsTotal(
			@Param(value = "param") Map<String, String> paramMap);

	/**
	 * 故障分类统计
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<Map> getFaultStatisticsByType(
			@Param(value = "param") Map<String, String> paramMap);
	
	/**
	 * 故障按厂家统计
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<Map> getFaultStatisticsByFactory(
			@Param(value = "param") Map<String, String> paramMap);
	
	/**
	 * 故障按原因统计
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<Map> getFaultStatisticsByReason(
			@Param(value = "param") Map<String, String> paramMap);
	
	/**
	 * 故障按网元型号统计
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<Map> getFaultStatisticsByNe(
			@Param(value = "param") Map<String, String> paramMap);
	
	/**
	 * 故障按网元型号统计
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<Map> getFaultStatisticsByUnit(
			@Param(value = "param") Map<String, String> paramMap);
	/**
	 * 获取故障列表
	 * @param conn
	 * @return
	 */
	public List<Map> getFaultList(FaultQueryCondition conn);
	/**
	 * 获取一级故障原因列表
	 * @return
	 */
	public List<Map> getFaultReason();
	/**
	 * 获取一级故障下的二级故障原因列表
	 * @param id
	 * @return
	 */
	public List<Map> getSubFaultReason(int id);
	/**
	 * 获取传输系统列表
	 * @return
	 */
	public List<Map> getTransformSystem();
	/**
	 * 根据告警查询对象查询指定faultId信息下的告警数据
	 * @param conn
	 * @return
	 */
	public List<Map> getAlarmByFaultId(AlarmQueryCondition conn);
	/**
	 * 根据传输系统查询对象获取台站
	 * @param conn
	 * @return
	 */
	public List<Map> getStateBySysId(StationQueryCondition conn);
	/**
	 * 保存故障信息
	 * @param con
	 */
	public void save(FaultInfoModel con);
	/**
	 * 故障处理
	 * @param model
	 */
	public void faultProcess(FaultProcessModel model);
	/**
	 *  删除故障下的告警
	 * @param model
	 */
	public void alarmDelete(FaultAlarmModel model);
	/**
	 * 根据板卡Id获取设备名称
	 * @param model
	 */
	public List<EquipNameModel> getEquipName(EquipNameModel model);
	/**
	 * 删除故障记录
	 * @param model
	 */
	public void faultDelete(FaultInfoModel model);
	public void alarmAdd(@Param(value = "param")Map map);
}
