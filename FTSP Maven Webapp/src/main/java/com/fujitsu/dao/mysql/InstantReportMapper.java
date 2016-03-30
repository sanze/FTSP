package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface InstantReportMapper {

	/**
	 * 光路误码监测表
	 * 
	 * @param tableNodesList
	 * @param condMap
	 * @return
	 */
	public List<Map> searchPM4BitErrReporty(
			@Param(value = "tableNodesList") List<Map<String, Object>> tableNodesList,
			@Param(value = "map") Map<String, String> condMap,
			@Param(value="start") int start);
	/**
	 * 光路误码监测表count
	 * 
	 * @param tableNodesList
	 * @param condMap
	 * @return
	 */
	public int searchPM4BitErrReportyCount(
			@Param(value = "tableNodesList") List<Map<String, Object>> tableNodesList,
			@Param(value = "map") Map<String, String> condMap);
	
	/**
	 * 节点换算成NE
	 * @param conditionMap
	 * @param Define
	 * @param treeDefine
	 * @return
	 */
	public List<Map> getNeUnderThisNode(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "TREE") Map<String, Object> treeDefine
			);
	
	
	/**
	 * SDH发送、接收光功率记录表（每月）
	 * @param tableNodesList
	 * @param condMap
	 * @return
	 */
	public List<Map> generateSDHLightPowerReport(
			@Param(value = "tableNodesList") List<Map<String, Object>> tableNodesList,
			@Param(value = "map") Map<String, String> condMap);
}
