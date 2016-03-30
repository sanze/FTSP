package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


public interface EmergencyPlanManagerMapper {
	
	/**
	 * @param data
	 */
	public void insertFactoryContact(Map data);

	/**
	 * @param data
	 */
	public void updateFactoryContactById(Map data);
	
	/**
	 * @param ids
	 */
	public void deleteFactoryContactByIds(@Param(value = "ids") List<Integer> ids);
	
	/**
	 * @param emergencyPlanName
	 * @param keyWord
	 * @param emergencyType
	 * @param startNumber
	 * @param pageSize
	 * @return
	 */
	public List getEmergencyPlanList(
			@Param(value = "emergencyPlanName") String emergencyPlanName,
			@Param(value = "keyWord") String keyWord,
			@Param(value = "emergencyType") Integer emergencyType,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize);
	
	/**
	 * @param emergencyPlanName
	 * @param keyWord
	 * @param emergencyType
	 * @return
	 */
	public int getEmergencyPlanListCount(
			@Param(value = "emergencyPlanName") String emergencyPlanName,
			@Param(value = "keyWord") String keyWord,
			@Param(value = "emergencyType") Integer emergencyType);
	
	/**
	 * @param data
	 */
	public int insertEmergencyPlan(Map data);

	/**
	 * @param data
	 */
	public void updateEmergencyPlanById(Map data);
	
	/**
	 * @param ids
	 */
	public void deleteEmergencyPlanContentById(@Param(value = "epId") Integer epId);
	
	/**
	 * @param data
	 */
	public void insertEmergencyPlanContentBatch(List<Map> data);
	
	
	/**
	 * @param ids
	 */
	public void deleteEmergencyPlanByIds(@Param(value = "ids") List<Integer> ids);
	
	/**
	 * @param data
	 */
	public void insertExercise(Map data);

	/**
	 * @param data
	 */
	public void updateExerciseById(Map data);
	
	/**
	 * @param ids
	 */
	public void deleteExerciseByIds(@Param(value = "ids") List<Integer> ids);
	
	/**
	 * @param data
	 */
	public void insertExerciseDetailBatch(List<Map> data);

	/**
	 * @param data
	 */
	public void updateExerciseDetailById(Map data);
	
	/**
	 * @param ids
	 */
	public void deleteExerciseDetailById(@Param(value = "exerciseId") Integer exerciseId);
	
	/**
	 * @param ids
	 */
	public void deleteExerciseDetailByIds(@Param(value = "ids") List<Integer> ids);
	
}
