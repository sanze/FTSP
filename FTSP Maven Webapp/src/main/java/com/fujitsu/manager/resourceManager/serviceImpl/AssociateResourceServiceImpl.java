package com.fujitsu.manager.resourceManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.AssociateResourceMapper;
import com.fujitsu.manager.resourceManager.service.AssociateResourceService;


@Scope("prototype")
@Service
@Transactional(rollbackFor = Exception.class)
public class AssociateResourceServiceImpl extends AssociateResourceService {
	@Resource
	private AssociateResourceMapper associateResourceMapper;
	@Resource
	private IQuartzManagerService quartzManagerService;

	@Override
	public List<Map> getAllResourceTask() throws CommonException {
		List<Map> resourceTaskList = new ArrayList<Map>();
		List<Map> returnList = new ArrayList<Map>();
		//获取资源关联任务的初始信息
		resourceTaskList = associateResourceMapper.getAllResourceTask();
		//将查询结果中的上次执行时间和下次执行时间转换成String型
		String nextExecuteTime = null;
		String lastestExecuteTime = null;
		//巡检时间转换
	    SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    if (resourceTaskList != null && resourceTaskList.size() > 0) {
	    	for (int i = 0; i < resourceTaskList.size(); i++) {
	    		Map resourceTask = resourceTaskList.get(i);
	    		//上次执行时间的转换
	    		if (resourceTask.get("LATEST_EXECUTE_TIME") != null) {
	    			lastestExecuteTime = time.format(resourceTask.get("LATEST_EXECUTE_TIME"));	    			
	    		} else {
	    			lastestExecuteTime = "";
	    		}
	    		resourceTask.put("LATEST_EXECUTE_TIME", lastestExecuteTime);
	    		//下次执行时间的转换
	    		if (resourceTask.get("NEXT_EXECUTE_TIME") != null) {
	    			nextExecuteTime = time.format(resourceTask.get("NEXT_EXECUTE_TIME"));
	    		} else {
	    			nextExecuteTime = "";
	    		}
	    		resourceTask.put("NEXT_EXECUTE_TIME", nextExecuteTime);
	    		returnList.add(resourceTask);
	    	}
	    }
	    return returnList;
	}
	
	@Override
	public Map<String, Object> getResourceTaskById(int rcTaskId) throws CommonException {
		return associateResourceMapper.getResourceTask(rcTaskId);
	}
	
	@Override
	public void setResourceTask(Map map) throws CommonException {
		String hours = null;
		String minutes = null;
		String cronExpression = "";
		//任务Id
		int rcTaskId = Integer.parseInt(String.valueOf(map.get("RC_TASK_ID")));
		//从数据库中查询现在的任务
		Map nowMap = getResourceTaskById(rcTaskId);
		
		Map updateMap = new HashMap();
		updateMap.put("RC_TASK_ID", map.get("RC_TASK_ID"));
		updateMap.put("TASK_STATUS", null);
		updateMap.put("PERIOD", null);
		//前台传入的同步周期和数据库中的同步周期不相同的情况下，修改和保存任务时间
		if (Integer.parseInt(String.valueOf(map.get("PERIOD")))
				!= Integer.parseInt(String.valueOf(nowMap.get("PERIOD")))) {
			int nowPeriod = Integer.parseInt(String.valueOf(map.get("PERIOD")));
			//小时
			hours = String.valueOf(nowPeriod/60);
			//分钟
			minutes = String.valueOf(nowPeriod%60);
			//同步周期
			cronExpression = "0 " + minutes + " " + hours + " * * ?";
			//修改时间
			quartzManagerService.modifyJobTime(rcTaskId,rcTaskId,cronExpression);
			//更新t_resource_correlation_task
			updateMap.put("PERIOD", map.get("PERIOD"));
			associateResourceMapper.updateResourceTask(updateMap);
		}
		//当传入任务状态后数据库中不一致的情况下，启用或挂起任务并保存。
		if (!String.valueOf(map.get("TASK_STATUS"))
				.equals(String.valueOf(nowMap.get("TASK_STATUS")))) {
			//对任务进行启用或者挂起操作
			List resourceTaskIdList = new ArrayList<Integer>();
			resourceTaskIdList.add(rcTaskId);
			//启用任务
			if ("启用".equals(map.get("TASK_STATUS"))) {
				startTask(resourceTaskIdList);
			}
			//挂起任务
			else if ("挂起".equals(map.get("TASK_STATUS"))) {
				holdOn(resourceTaskIdList);
			}
		}
	}
	
	@Override
	public void startTask(List<Integer> resourceTaskList) throws CommonException {
		Map nowMap = new HashMap();
		List startTaskIdList = new ArrayList<Integer>();
		//启用所选择的任务
		if (resourceTaskList != null && resourceTaskList.size() > 0) {
			for (int i = 0; i < resourceTaskList.size(); i++) {
				//访问数据库，如果要启用的任务已经被启用则不再挂起,任务状态为挂起的情况下启用
				nowMap = getResourceTaskById(resourceTaskList.get(i));
				if ("挂起".equals(nowMap.get("TASK_STATUS"))) {	
					quartzManagerService.ctrlJob(resourceTaskList.get(i),
							resourceTaskList.get(i),
							CommonDefine.QUARTZ.JOB_RESUME);
					startTaskIdList.add(resourceTaskList.get(i));
				}	
			}
			//更新任务状态为启用
			if (startTaskIdList != null && startTaskIdList.size() > 0) {
				updateStatus(startTaskIdList, "启用");
			}
		}
	}

	@Override
	public void holdOn(List<Integer> resourceTaskList) throws CommonException {
		Map nowMap = new HashMap();
		List holdOnIdList = new ArrayList<Integer>();
		//挂起所选择的任务
		if (resourceTaskList != null && resourceTaskList.size() > 0) {
			for (int i = 0; i < resourceTaskList.size(); i++) {
				//访问数据库，如果要挂起的任务已经被挂起则不再挂起,任务状态为启用的情况下挂起
				nowMap = getResourceTaskById(resourceTaskList.get(i));
				if ("启用".equals(nowMap.get("TASK_STATUS"))) {	
					quartzManagerService.ctrlJob(resourceTaskList.get(i),
							resourceTaskList.get(i),
							CommonDefine.QUARTZ.JOB_PAUSE);
					holdOnIdList.add(resourceTaskList.get(i));
				}
			}
			//更新任务状态为挂起
			if (holdOnIdList != null && holdOnIdList.size() > 0) {
				updateStatus(holdOnIdList, "挂起");			
			}
		}
	}
	
	private void updateStatus(List<Integer> resourceTaskList, String taskStatus) throws CommonException {
		//更新任务状态为启用或者挂起
		associateResourceMapper.updateStatus(resourceTaskList, taskStatus);
		
	}
}

