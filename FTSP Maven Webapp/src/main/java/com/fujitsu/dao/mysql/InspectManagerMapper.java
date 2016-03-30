package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface InspectManagerMapper {
	/*------------------------------------- 包机人 --------------------------------------------*/
	public List<Map> selectEngineerList(@Param(value = "map")Map map);
	public int countEngineerList(@Param(value = "map")Map map);
	public List<Map> selectAreaList(@Param(value = "level")int level);
	public List<Map> getJobNoExitList(@Param(value = "map")Map map);
	public void storeInspectEngineer(@Param(value = "map")Map map);
	public void storeInspectEquip(@Param(value = "list")List<Map> equipList);
	public List<Map> getInspectEquipList(@Param(value = "engineerId")int engineerId);
	public List<Map> getResourceAreaInfo(@Param(value = "resourceId")int resourceId);
	public List<Map> getInspectEngineerInfo(@Param(value = "engineerId")int engineerId);
	public void updateInspectEngineer(@Param(value = "map")Map map);
	public void deleteInspectEngineerEquip(@Param(value = "map")Map map);
	public void deleteInspectEngineer(@Param(value = "map")Map map);
	public List<Map> selectEngineerListByIdList(@Param(value = "map")Map map);
	
	/*-------------------------------------巡检报告--------------------------------------------*/	
	public List<Map> selectYearListFromReport();
	public List<Map> getUserGroupId(@Param(value = "userId")int userId);
	public List<Map> selectReportList(@Param(value = "map")Map map);
	public int countReportList(@Param(value = "map")Map map);
	public void deleteInspectReport(@Param(value = "map")Map map);
	public void insertInspectReport(@Param(value = "map")Map map);
	public Map selectReport(@Param(value = "reportId")int reportId);
	public Map getUserInfo(@Param(value = "userId")int userId);
	
	/*------------------------------------ 巡检任务 -------------------------------------------*/
	public List<Map> selectTaskList(@Param(value = "map")Map map);
	public int countTaskList(@Param(value = "map")Map map);
	public List<Map> getPrivilegeList();
	public List<Map> getCurrentUserGroup(@Param(value = "userId")int userId);
	public List<Map> getInspectTaskExitList(@Param(value = "map")Map map);
	public void updateInspectTask(@Param(value = "map")Map map);
	public void updateTaskParam(@Param(value = "map")Map map);
	public void storeInspectTask(@Param(value = "map")Map map);
	public void storeTaskInfo(@Param(value = "list")List<Map> equipList);
	public void storeTaskParam(@Param(value = "list")List<Map> taskParamList);
	public List<Map> getInspectTask(@Param(value = "inspectTaskId")int inspectTaskId);
	public List<Map> getInspectTaskInfo(@Param(value = "inspectTaskId")int inspectTaskId);
	public List<Map> getInspectTaskParam(@Param(value = "inspectTaskId")int inspectTaskId);
	public void deleteTaskRunDetial(@Param(value = "map")Map map);
	public void deleteTaskParam(@Param(value = "map")Map map);
	public void deleteTaskInfo(@Param(value = "map")Map map);
	public void deleteTask(@Param(value = "map")Map map);
	public List<Map> getInspectTaskList(@Param(value = "list")List<Integer> taskIdList);
	public void updateTaskStatus(@Param(value = "map")Map map);
	public List<Map> getInspectTaskItem(@Param(value = "inspectTaskId")int inspectTaskId);
	public int countInspectEquip(@Param(value = "inspectTaskId")int inspectTaskId);
	public int countCompletedEquip(@Param(value = "inspectTaskId")int inspectTaskId);
	
	public void updateEquipStatus(@Param(value = "map")Map map);
	public void addInspectItemStatus(@Param(value = "list")List<Map> list);
	public void updateInspectItemStatus(@Param(value = "map")Map map);
	public void delInspectItemStatus(@Param(value = "list")List<Map> list);
	
	/*------------------------------------ 巡检任务数据采集 -------------------------------------------*/
	public void updateTaskInfo(@Param(value = "map")Map map);
	
	public List<Map> getProtectGroups(
			@Param(value = "neId")int neId,
			@Param(value = "SCHEMA_STATE")List<Integer> SCHEMA_STATE, 
			@Param(value = "Define") Map Define);
	public List<Map> getEProtectGroups(
			@Param(value = "neId")int neId,
			@Param(value = "SCHEMA_STATE")List<Integer> SCHEMA_STATE, 
			@Param(value = "Define") Map Define);
	public List<Map> getWDMProtectGroups(
			@Param(value = "neId")int neId,
			@Param(value = "SCHEMA_STATE")List<Integer> SCHEMA_STATE, 
			@Param(value = "Define") Map Define);
	public List<Map<String, Object>> getProtectedList(
			@Param(value = "category")int category,
			@Param(value = "pgId")Integer pgId,
			@Param(value = "Define") Map Define);
	public List<Map> getClockSources(
			@Param(value = "neId")int neId, 
			@Param(value = "Define") Map Define);
	public Map getResourceInfoByRoom(
			@Param(value = "roomId")int roomId);
	public List<Map> getEngineerByNodes(
			@Param(value = "nodes")List<Map> nodes);
	public List<Map> getPtpTypeByNe(
			@Param(value = "neId")int neId, 
			@Param(value = "Define") Map Define);
	public int CountNePtpByType(
			@Param(value = "neId")int neId, 
			@Param(value = "ptpType")String ptpType, 
			@Param(value = "Define") Map Define);
	public int CountNePtpHasCrs(
			@Param(value = "neId")int neId, 
			@Param(value = "Define") Map Define);
	public int CountNePtp(
			@Param(value = "neId")int neId, 
			@Param(value = "Define") Map Define);
	public int CountNeCtpHasCrs(
			@Param(value = "neId")int neId, 
			@Param(value = "Define") Map Define);
	public int CountNeCtp(
			@Param(value = "neId")int neId, 
			@Param(value = "Define") Map Define);
}