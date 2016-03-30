package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IResourceSystemCorrelationService {
	/**
	 * 获取资源关联任务列表
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> getAllResourceCorrelationTask() throws CommonException;
	
	/**
	 * 启用特定资源关联任务
	 * @param taskIds
	 * @throws CommonException
	 */
	public void enableResourceCorrelationTask(List<Integer> taskIds) throws CommonException;
	
	/**
	 * 禁用特定资源关联任务
	 * @param taskIds
	 * @throws CommonException
	 */
	public void disableResourceCorrelationTask(List<Integer> taskIds) throws CommonException;
	
	/**
	 * 人工执行特定资源关联任务
	 * @param taskId
	 * @throws CommonException
	 */
	public void manualStartResourceCorrelationTask(int taskId) throws CommonException;
	
	/**
	 * 获取特定资源关联任务执行状态
	 * @param taskId
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> getResourceCorrelationTaskStatus(int taskId) throws CommonException;
	
	/**
	 * 获取特定资源管理任务信息
	 * @param taskId
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> getResourceCorrelatioinTaskInfo(int taskId) throws CommonException;
	
	/**
	 * 设置特定资源关联任务
	 * @param taskId
	 * @throws CommonException
	 */
	public void setResourceCorrelationTask(Map<String,Object> param) throws CommonException;

}
