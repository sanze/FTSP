package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IAssociateResourceService {

	/**
	 * @author fanguangming
	 */


	/**
	 * 获取资源关联任务的初始信息
	 * 
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getAllResourceTask() throws CommonException;
	
	/**
	 * 根据任务Id获取任务信息
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getResourceTaskById(int scTaskId) throws CommonException;
	
	/**
	 * 设置资源任务
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public void setResourceTask(Map map) throws CommonException;
	
	/**
	 * 启用任务
	 * 
	 * @param List<Integer>
	 * @throws CommonException
	 */
	public void startTask(List<Integer> resourceTaskList) throws CommonException;
	
	/**
	 * 启用任务
	 * 
	 * @param List<Integer>
	 * @throws CommonException
	 */
	public void holdOn(List<Integer> resourceTaskList) throws CommonException;
	
}
