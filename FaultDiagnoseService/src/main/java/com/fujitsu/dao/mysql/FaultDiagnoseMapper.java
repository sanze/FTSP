package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.model.FaultModel;

public interface FaultDiagnoseMapper {

	/**
	 * 	获取整表数据
	 * @param tableName 表名
	 * @return
	 */
	public List<Map<String, Object>> selectTable(
			@Param(value = "tableName") String tableName);
	
	/**
	 * 	按id获取数据列表
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public List<Map<String, Object>> selectTableListById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	/**
	 * 	按id获取一条数据
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public Map selectTableById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	/**
	 * Method name: getSystemParam <BR>
	 * Description: 查询系统参数<BR>
	 * @param paramKey 参数名
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getSystemParam(@Param(value = "paramKey")String paramKey);
	
	/**
	 * 获取接入服务参数
	 * @param emsConnectionId
	 * @return
	 */
	public Map selectSvcRecordByEmsconnectionId(@Param(value = "emsConnectionId")int emsConnectionId);
	
	/**
	 * 获取特定网元与相邻网元间的Ptp
	 * @param neId
	 * @return
	 */
	public List<Map<String, Object>> getLinkPtpByNeId(@Param(value = "neId") int neId);
	
	/**
	 * 获取指定网元所属传输系统网元
	 * @param neId
	 * @return
	 */
	public List<Map<String, Object>> getNeListFromTransSysByNeId(@Param(value = "neId") int neId);
	
	/**
	 * 获取特定板卡与相邻网元间的Ptp
	 * @param unitId
	 * @return
	 */
	public List<Map<String, Object>> getLinkPtpByUnitId(@Param(value = "unitId") int unitId);
	
	/**
	 * 获取特定端口与相邻网元间的Ptp
	 * @param ptpId
	 * @return
	 */
	public Map<String, Object> getLinkPtpByPtpId(@Param(value = "ptpId") int ptpId);
	
	/**
	 * 通过电路相关性查询获取网元/板卡/端口/通道相关电路的CTP
	 * @param neId
	 * @param unitId
	 * @param ptpId
	 * @param ctpId
	 * @param neType
	 * @return
	 */
	public List<Map<String, Object>> getCtpFromCircuit(@Param(value = "neId") Integer neId,
			@Param(value = "unitId") Integer unitId,
			@Param(value = "ptpId") Integer ptpId,
			@Param(value = "ctpId") Integer ctpId,
			@Param(value = "neType") Integer neType);
	
	/**
	 *  更新指定故障诊断规则的启用状态
	 * @param ruleId
	 */
	public void updateFaultDiagnoseById(
			@Param(value = "ruleId") int ruleId,
			@Param(value = "status") int status);
	
	/**
	 * 获取特定端口的同缆端口
	 * @param ptpId
	 * @return
	 */
	public List<Map<String, Object>> getPeerCablePortByPtpId(@Param(value = "ptpId") int ptpId);
	
	/**
	 * 获取特定端口的光缆ID
	 * @param ptpId
	 * @return
	 */
	public Map<String, Object> getPeerCableIdByPtpId(@Param(value = "ptpId") int ptpId);
	
	/**
	 * 获取同缆故障ID
	 * @param cableId
	 * @return
	 */
	public Map<String, Object> getFaultIdByPeerCableId(@Param(value = "cableId") int cableId);
	
	/**
	 * 获取指定端口的光口标准内容(T_PM_STD_OPT_PORT)
	 * @param ptpId
	 */
	public Map<String, Object> getPmStdOptPortByPtpId(@Param(value="ptpId") int ptpId);
	
	/**
	 * 通过指定端口获取其它额外信息（站名/网元名/板卡名等）
	 * @param ptpId
	 * @return
	 */
	public Map<String, Object> getExtraInfoByPtpId(@Param(value="ptpId") int ptpId);
	
	/**
	 * 获取指定端口的光缆信息
	 * @param ptpId
	 * @return
	 */
	public Map<String, Object> getCableInfoByPtpId(@Param(value="ptpId") int ptpId);
	
	/**
	 * 通过机房ID获取站名
	 * @param roomId
	 * @return
	 */
	public Map<String, Object> getStationInfoByRoomId(@Param(value="roomId") int roomId);
	
	/**
	 * 获取指定网元的系统名称
	 * @param neId
	 * @return
	 */
	public List<Map<String, Object>> getSysNameByNeId(@Param(value="neId") int neId);
	
	/**
	 * 插入故障记录
	 */
	public void addFault(@Param(value="faultModel") FaultModel alm);
	
	/**
	 * 插入故障的告警信息
	 * 
	 * @param alarmList
	 */
	public void addFaultAlarmInfo(@Param(value = "faultAlarmInfoList")
					List<Map<String, Object>> faultAlarmInfoList);
	
	/**
	 * 获取故障表最近的流水号
	 * @return
	 */
	public List<Map<String, Object>> getFaultSerialNo();
}
