package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.ListResult;
import com.fujitsu.manager.faultManager.model.AlarmQueryCondition;
import com.fujitsu.manager.faultManager.model.EquipNameModel;
import com.fujitsu.manager.faultManager.model.FaultQueryCondition;
import com.fujitsu.manager.faultManager.model.StationQueryCondition;
import com.fujitsu.model.FaultAnalysisModel;

public interface IFaultStatisticsService {
	
	/**
	 * 获取当前告警列表
	 * @return FaultAnalysisModel列表
	 */
	public Map<String, Object> getCurAlmList(int start, int limit) throws CommonException;
	
	/**
	 * 获取指定故障的相关告警内容的最新信息（从历史告警列表）
	 * @param  faultId
	 * @return FaultAnalysisModel列表
	 */
	public List<FaultAnalysisModel> getAlmsByFaultIdFromHisAlm(int faultId) throws CommonException;
	
	/**
	 * 根据前台传来的参数查询符合条件的故障记录
	 * @param jsonString
	 * @return ListResult
	 * @throws CommonException
	 */
	public ListResult getFaultList(String jsonString,int start, int limit)throws CommonException;
	/**
	 * 根据查询对象查询符合条件的故障记录
	 * @param con
	 * @return ListResult
	 * @throws CommonException
	 */
	public ListResult getFaultList(FaultQueryCondition con)throws CommonException;
	/**
	 * 获取一级故障列表
	 * @return ListResult
	 * @throws CommonException
	 */
	public ListResult getFaultReason()throws CommonException;
	/**
	 * 根据一级故障id获取二级故障列表
	 * @param id
	 * @return
	 * @throws CommonException
	 */
	public ListResult getSubFaultReason(int id)throws CommonException;
	/**
	 * 删除指定Id的故障记录
	 * @param id
	 * @return
	 * @throws CommonException
	 */
	public CommonResult deleteFaultRecord(int faultId)throws CommonException;
	/**
	 * 获取传输系统列表
	 * @return
	 * @throws CommonException
	 */
	public ListResult getTransformSystem()throws CommonException;
	/**
	 * 根据faultId查询相关告警记录
	 * @param id
	 * @return
	 * @throws CommonException
	 */
	public ListResult getAlarmByFaultId(int id)throws CommonException;
	/**
	 * 根据AlarmQueryCondition对象查询告警记录
	 * @param con
	 * @return
	 * @throws CommonException
	 */
	public ListResult getAlarmByFaultId(AlarmQueryCondition con)throws CommonException;
	/**
	 * 根据传输系统Id获取台站
	 * @param id
	 * @return
	 * @throws CommonException
	 */
	public ListResult getStateBySysId(int id)throws CommonException;
	/**
	 * 根据传输系统查询对象获取台站
	 * @param con
	 * @return
	 * @throws CommonException
	 */
	public ListResult getStateBySysId(StationQueryCondition con)throws CommonException;
	/**
	 * 保存故障信息
	 * @param jsonString
	 * @return
	 * @throws CommonException
	 */
	public ListResult save(String jsonString)throws CommonException;
	/**
	 * 故障处理
	 * @param faultId
	 * @param processType
	 * @return
	 * @throws CommonException
	 */
	public CommonResult faultProcess(int faultId,int processType)throws CommonException;
	/**
	 * 删除故障下的告警 
	 * @param faultId
	 * @param alarmId
	 * @return
	 * @throws CommonException
	 */
	public CommonResult alarmDelete(int faultId ,int alarmId)throws CommonException;
	/**
	 * 根据板卡Id获取设备名称
	 * @param unitId
	 * @return
	 * @throws CommonException
	 */
	public EquipNameModel getEquipName(int unitId)throws CommonException;
	public CommonResult alarmAdd(int faultId,String neName,int alarmId,String alarmName,int severity,String startTime,String clearTime)throws CommonException;
	 
	//-----333--------
	
	/**
	 * 故障总计的饼图
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> getFaultStatisticsTotal(Map<String, String> paramMap) throws CommonException;

	/**
	 * 故障分类统计
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getFaultStatisticsClassify(Map<String, String> paramMap)throws CommonException;
}
