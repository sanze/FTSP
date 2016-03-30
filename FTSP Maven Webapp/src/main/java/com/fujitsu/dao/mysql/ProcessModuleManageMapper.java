package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ProcessModuleManageMapper {
	/**
	 * 
	 * Method name: selectProcessModuleDataList <BR>
	 * Description: 查询所有模块进程 <BR>
	 * Remark: <BR>
	 * @param map
	 * @return  List<Map><BR>
	 */
	public List<Map<String, Object>> selectProcessModuleDataList(@Param(value = "map")Map<String, Object> map);
	/**
	 * 
	 * Method name: countProcessModuleDataList <BR>
	 * Description: 查询所有模块进程的数量 <BR>
	 * Remark: <BR>
	 * @param map
	 * @return  int<BR>
	 */
	public int countProcessModuleDataList(@Param(value = "map")Map map);
	/**
	 * 
	 * Method name: updateState <BR>
	 * Description: 更新模块进程状态 <BR>
	 * Remark: <BR>
	 * @param map  void<BR>
	 */
	public void updateState(@Param(value = "map")Map<String, Object> map);
}
