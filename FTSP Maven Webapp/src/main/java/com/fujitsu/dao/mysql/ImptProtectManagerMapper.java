package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ImptProtectManagerMapper {
	/*-------------------------------------任务--------------------------------------------*/
	public int checkTaskName(@Param(value = "map")Map<String,Object> param);
	public void changeTaskStatus(@Param(value = "map")Map<String,Object> param);
	public void saveTask(@Param(value = "map")Map<String,Object> param);
	public void saveTaskInfo(
			@Param(value = "SYS_TASK_ID")int taskId,
			@Param(value = "list")List<Map<String,Object>> list);
	public void saveTaskParam(
			@Param(value = "SYS_TASK_ID")int taskId,
			@Param(value = "list")List<Map<String,Object>> list);
	public void delTask(@Param(value = "SYS_TASK_ID")int taskId);
	public void delTaskInfo(@Param(value = "SYS_TASK_ID")int taskId);
	public void delTaskParam(@Param(value = "SYS_TASK_ID")int taskId);
	public void delTaskRunDetail(@Param(value = "SYS_TASK_ID")int taskId);
	public int cntTaskList(
			@Param(value = "map")Map<String, Object> param, 
			@Param(value = "userId")int userId);
	public List<Map<String, Object>> getTaskList(
			@Param(value = "map")Map<String, Object> param, 
			@Param(value = "userId")int userId, 
			@Param(value = "start")Integer start,
			@Param(value = "limit")Integer limit);
	public Map<String, Object> getTask(
			@Param(value = "map")Map<String, Object> param, 
			@Param(value = "userId")int userId);
	public List<Map<String, Object>> getTaskInfo(
			@Param(value = "SYS_TASK_ID")int taskId);
	public List<Map<String, Object>> getTaskParam(@Param(value = "SYS_TASK_ID")int taskId);
	/*_____________________________________任务____________________________________________*/
	/*_____________________________________监测____________________________________________*/
	/**
	 * 为ne加上emsId
	 * @param neIdList
	 * @return
	 */
	public List<Map> processNe(@Param(value = "neIdList") List<Integer> neIdList,@Param(value = "TREE")Map<String, Object> TREE);
	
	/**
	 * 获取这堆ptp的网管ID
	 * @param ptpList
	 * @return
	 */
	public List<Integer> getEmsIdFromPtps(@Param(value = "ptpList") List<Integer> ptpList);
	
	/**
	 * 为PTP加上emsId
	 * @param idList
	 * @param TREE 
	 * @return
	 */
	public List<Map> processPtp(@Param(value = "idList") List<Integer> idList, @Param(value = "TREE")Map<String, Object> TREE);

	
	/**
	 * 根据ptpIdList获取unitIdList
	 * @param ptpList
	 * @return
	 */
	public List<Integer> getUnitListByPtpList(@Param(value = "ptpList") List<Integer> ptpList);  
	/**
	 * 端口的一系列信息-电路任务（neid，unitid，ptpid）
	 * @param ptpList
	 * @return
	 */
	public List<Map> getPtpInfo(@Param(value = "ptpList") List<Integer> ptpList);
	/*_____________________________________监测____________________________________________*/
	/**
	 * 获取性能越限数据
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> getPmExceedData(
			@Param(value="map") Map<String, Object> paramMap,
			@Param(value = "start")  int start,
			@Param(value = "limit") int limit);
	/**
	 * 获取APA坐标
	 * @param param
	 * @return
	 */
	public List<Map> getAPAPosition(
			@Param(value="map") Map<String, Object> paramMap);
	/**
	 * 保存APA坐标
	 * @param param
	 * @return
	 */
	public void saveAPAPosition(
			@Param(value="map") Map<String, Object> paramMap);
	/**
	 * 获取APA坐标
	 * @param param
	 * @return
	 */
	public void updateAPAPosition(
			@Param(value="map") Map<String, Object> paramMap);
	
	
}