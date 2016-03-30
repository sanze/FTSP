package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceSystemCorrelationMapper {
	
	/**
	 * 获取资源关联任务列表
	 * @return
	 */
	public List<Map<String,Object>> getResourceCorrelationTaskList();
	
	/**
	 * 获取特定资源关联任务信息
	 * @param taskId
	 * @return
	 */
	public Map<String,Object> getResourceCorrelationTaskInfo(@Param(value="taskId") Integer taskId);
	
	/**
	 * 更新资源关联任务状态
	 * @param paramMap
	 */
	public void updateResourceCorrelationTaskStatus(@Param(value="map") Map<String, Object> paramMap);
	
	/**
	 * 获取kettle job运行日志
	 * @param jobName
	 * @return
	 */
	public Map<String,Object> getKettleJobLog(@Param(value="jobName") String jobName);
	
}
