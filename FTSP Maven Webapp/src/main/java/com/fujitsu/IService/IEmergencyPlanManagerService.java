package com.fujitsu.IService;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;


/**
 * @author xuxiaojun
 *
 */
public interface IEmergencyPlanManagerService {

	/**
	 *  获取厂家联系方式列表
	 * 
	 * @return
	 */
	public Map<String, Object> getFactoryContactList(int start, int limit) throws CommonException;
	
	/**
	 * @param factoryContactId
	 * @throws CommonException
	 */
	public Map initFactoryContact(int factoryContactId) throws CommonException;
	
	/**
	 * @param data
	 * @throws CommonException
	 */
	public void modifyFactoryContact(Map data) throws CommonException;
	
	/**
	 * @param factoryContactIds
	 * @throws CommonException
	 */
	public void deleteFactoryContactByIds(List<Integer> factoryContactIds) throws CommonException;
	

	/**
	 * 获取应急预案列表
	 * @param emergencyPlanName
	 * @param keyWord
	 * @param emergencyType
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getEmergencyPlanList(String emergencyPlanName,
			String keyWord, Integer emergencyType, int start, int limit)
			throws CommonException;
	
	/**
	 * @param factoryContactId
	 * @throws CommonException
	 */
	public Map initEmergencyPlan(int emergercyId) throws CommonException;
	
	/**
	 * @param data
	 * @param contents
	 * @throws CommonException
	 */
	public void modifyEmergencyPlan(Map data, List<Map> contents) throws CommonException;
	
	/**
	 * @param factoryContactIds
	 * @throws CommonException
	 */
	public void deleteEmergencyPlanByIds(List<Integer> emergercyIds) throws CommonException;
	
	/**
	 *  获取演习列表
	 * 
	 * @return
	 */
	public Map<String, Object> getExerciseList(int emergercyId, int start, int limit) throws CommonException;
	
	/**
	 * @param exerciseId
	 * @throws CommonException
	 */
	public Map initExercise(int exerciseId) throws CommonException;
	
	/**
	 * @param data
	 * @throws CommonException
	 */
	public void modifyExercise(Map data) throws CommonException;
	
	/**
	 * @param exerciseIds
	 * @throws CommonException
	 */
	public void deleteExerciseByIds(List<Integer> exerciseIds) throws CommonException;
	
	/**
	 *  获取演习步骤列表
	 * 
	 * @return
	 */
	public Map<String, Object> getExerciseDetailList(int exerciseId, int start, int limit) throws CommonException;
	
	/**
	 * @param data
	 * @throws CommonException
	 */
	public void modifyExerciseDetail(int exerciseId, List<Map> data) throws CommonException;
	
	/**
	 * @param exerciseDetailIds
	 * @throws CommonException
	 */
	public void deleteExerciseDetailByIds(List<Integer> exerciseDetailIds) throws CommonException;
	
	/**
	 *  获取预案列表
	 * 
	 * @return
	 */
	public Map<String, Object> getEmergencyPlanContentList(int emergercyId, int start, int limit) throws CommonException;
	
	/**
	 * 上传预案文件，并返回路径
	 * 
	 * @return
	 */
	public Map<String, String> importFile(File file, String fileName,
			String uploadPath) throws CommonException;
	
	/**
	 * 导出演习详情
	 * 
	 * @return
	 */
	public String exportExercise(int exerciseId) throws CommonException;
}
