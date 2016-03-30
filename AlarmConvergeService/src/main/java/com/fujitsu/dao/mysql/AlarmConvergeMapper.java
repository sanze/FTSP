package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface AlarmConvergeMapper {

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
	 * Method name: getSystemParam <BR>
	 * Description: 查询系统参数<BR>
	 * @param paramKey 参数名
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getSystemParam(@Param(value = "paramKey")String paramKey);
	
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
	 *  更新指定告警收敛规则的启用状态
	 * @param ruleId
	 */
	public void updateAlarmConvergeById(
			@Param(value = "ruleId") int ruleId,
			@Param(value = "status") int status);
	
}
