package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface AssociateResourceMapper {
	/**
	 * @author fanguangming
	 */
	/** *************************查询******************************* */


	/**
	 * 获取资源关联任务的初始信息
	 * @return
	 */
	public List<Map> getAllResourceTask();
	
	/**
	 * 根据Id获取任务信息
	 * @return
	 */
	public Map<String, Object> getResourceTask(@Param(value = "rcTaskId") int rcTaskId);
	
	/**
	 * 更新资源任务
	 * 
	 * @param map
	 */
	public void updateResourceTask(@Param(value = "map") Map map);

	/**
	 * 更新任务状态为启用或者挂起
	 * 
	 * @param List<Integer> resourceTaskList
	 * @param String taskStatus
	 */
	public void updateStatus(@Param(value = "resourceTaskList") List<Integer> resourceTaskList, 
			@Param(value = "taskStatus") String taskStatus);
}