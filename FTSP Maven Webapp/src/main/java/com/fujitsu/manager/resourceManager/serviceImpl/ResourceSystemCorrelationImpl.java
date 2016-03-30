package com.fujitsu.manager.resourceManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.IService.IResourceSystemCorrelationService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ResourceSystemCorrelationMapper;
import com.fujitsu.job.AlarmSyncJob;
import com.fujitsu.job.ResourceCorrelationJob;
import com.fujitsu.manager.resourceManager.service.ResourceSystemCorrelationService;
import com.fujitsu.util.BeanUtil;

public class ResourceSystemCorrelationImpl extends ResourceSystemCorrelationService {

	@Resource
	private ResourceSystemCorrelationMapper rsCorrelationMapper;
	@Resource
	private IQuartzManagerService quartzManagerService;
	
	@Override
	public Map<String, Object> getAllResourceCorrelationTask() throws CommonException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> dataList = rsCorrelationMapper.getResourceCorrelationTaskList();
		for (Map<String,Object> data : dataList) {
			if (data.get("LATEST_EXECUTE_TIME") != null) {
				data.put("LATEST_EXECUTE_TIME", sf.format(data.get("LATEST_EXECUTE_TIME")));
			}
			if (data.get("NEXT_EXECUTE_TIME") != null) {
				data.put("NEXT_EXECUTE_TIME", sf.format(data.get("NEXT_EXECUTE_TIME")));
			}
		}
		result.put("rows", dataList);
		return result;
	}
	
	@Override
	public void enableResourceCorrelationTask(List<Integer> taskIds) throws CommonException {
		Map<String,Object> paramMap;
		for (Integer taskId : taskIds) {
			// 启用任务前再次确认当前任务状态
			Map<String,Object> taskInfo = rsCorrelationMapper.getResourceCorrelationTaskInfo(taskId);
			String status = taskInfo.get("TASK_STATUS").toString();
			if (TASK_STATUS_DISABLE.equals(status)) {
    			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
    					taskId, CommonDefine.QUARTZ.JOB_RESUME);
    			// 更新任务状态
    			paramMap = new HashMap<String,Object>();
    			paramMap.put("RC_TASK_ID", taskId);
    			paramMap.put("TASK_STATUS", "1");
    			rsCorrelationMapper.updateResourceCorrelationTaskStatus(paramMap);				
			}
		}
	}

	@Override
	public void disableResourceCorrelationTask(List<Integer> taskIds) throws CommonException {
		Map<String,Object> paramMap;
		for (Integer taskId : taskIds) {
			// 挂起任务前再次确认当前任务状态
			Map<String,Object> taskInfo = rsCorrelationMapper.getResourceCorrelationTaskInfo(taskId);
			String status = taskInfo.get("TASK_STATUS").toString();
			if (TASK_STATUS_ENABLE.equals(status)) {
    			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
    					taskId, CommonDefine.QUARTZ.JOB_PAUSE);
    			// 更新任务状态
    			paramMap = new HashMap<String,Object>();
    			paramMap.put("RC_TASK_ID", taskId);
    			paramMap.put("TASK_STATUS", "0");
    			rsCorrelationMapper.updateResourceCorrelationTaskStatus(paramMap);
			}
		}
	}
	
	@Override
	public void manualStartResourceCorrelationTask(int taskId) throws CommonException {
		quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
				taskId, CommonDefine.QUARTZ.JOB_ACTIVATE);
	}

	@Override
	public Map<String,Object> getResourceCorrelationTaskStatus(int taskId) throws CommonException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String jobName="";
		switch (taskId) {
		case RESOURCE_CORRELATION_AREA:
			jobName = "Resource Correlation - Area";
			break;
		case RESOURCE_CORRELATION_STATION:
			jobName = "Resource Correlation - Station";
			break;
		case RESOURCE_CORRELATION_ROOM:
			jobName = "Resource Correlation - Room";
			break;
		case RESOURCE_CORRELATION_TRANS_SYS:
			jobName = "Resource Correlation - Transmission system";
			break;
		case RESOURCE_CORRELATION_NE:
			jobName = "Resource Correlation - Ne";
			break;
		case RESOURCE_CORRELATION_PORT:
			jobName = "Resource Correlation - Port";
			break;
		case RESOURCE_CORRELATION_TRANS_SEGMENT:
			jobName = "Resource Correlation - Transmission segment";
			break;
		case RESOURCE_CORRELATION_CIRCUIT:
			jobName = "Resource Correlation - Circuit";
			break;
		case RESOURCE_CORRELATION_OPTICAL_PATH:
			jobName = "Resource Correlation - Optical Path";
			break;
		}
		Map<String,Object> map = rsCorrelationMapper.getKettleJobLog(jobName);
		if (map !=null) {
    		String logfield = map.get("LOG_FIELD").toString();
    		String[] rowArray = logfield.substring(0, logfield.indexOf("\r\n\r\nEND")).split("\r\n");
    		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    		for (int i=0; i<rowArray.length; i++) {
    			Map<String,Object> item = new HashMap<String,Object>();
    			String time = rowArray[i].split(" - ", 2)[0];
    			String action = rowArray[i].split(" - ", 2)[1];
    			item.put("TIME", time);
    			item.put("ACTION", action);
    			list.add(item);
    		}
    		resultMap.put("rows", list);
		} else {
			resultMap.put("rows", new ArrayList<Map<String,Object>>());
		}
		
		return resultMap;
	}
	
	@Override
	public Map<String, Object> getResourceCorrelatioinTaskInfo(int taskId) throws CommonException {
		return rsCorrelationMapper.getResourceCorrelationTaskInfo(taskId);
	}
	
	@Override
	public void setResourceCorrelationTask(Map<String,Object> param) throws CommonException {
		int taskId = Integer.valueOf(param.get("RC_TASK_ID").toString());
		int period = (Integer) param.get("PERIOD");
		String status = param.get("TASK_STATUS").toString();
		Map<String,Object> map = rsCorrelationMapper.getResourceCorrelationTaskInfo(taskId);
		int oldPeriod = (Integer)map.get("PERIOD");
		String oldStatus = map.get("TASK_STATUS").toString();
		Map<String,Object> quartzJob = null;
		
		if (period != oldPeriod || !status.equals(oldStatus)) {
    		map.put("PERIOD", period);
    		map.put("TASK_STATUS", status);
    		
    		if (period != oldPeriod) {
        		int hour = period / 60;
        		int minute = period % 60;
        		StringBuilder sb = new StringBuilder();
        		sb.append("0 ").append(String.valueOf(minute)).append(" ");
        		sb.append(String.valueOf(hour)).append(" * * ?");
        		String cron =sb.toString();
        		// 暂停任务
        		quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
        				taskId, CommonDefine.QUARTZ.JOB_PAUSE);
        		// 修改任务时间
        		quartzManagerService.modifyJobTime(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
        				taskId, cron);
        		// 获取任务的下次执行时间
        		quartzJob = quartzManagerService.getJobInfo(
    					CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION, taskId);
    		}
    		// 设置job状态
    		if (TASK_STATUS_ENABLE.equals(status)) {
        		quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
        				taskId, CommonDefine.QUARTZ.JOB_RESUME);			
    		} else {
    			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
        				taskId, CommonDefine.QUARTZ.JOB_PAUSE);
    		}
    		// 更新设置参数
    		Map<String,Object> paramMap = new HashMap<String,Object>();
    		paramMap.put("RC_TASK_ID", taskId);
    		paramMap.put("PERIOD", period);
    		paramMap.put("TASK_STATUS", status);
    		if (quartzJob != null)
    			paramMap.put("NEXT_EXECUTE_TIME", quartzJob.get("nextFireTime"));
    		try {
    			rsCorrelationMapper.updateResourceCorrelationTaskStatus(paramMap);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
		}
	}

	
	
	public static void main(String[] args) {

//		List<String> a = new ArrayList<String>();
//		a.add("000001");
//		a.add("000002");
//		List<String> rs = rsCorrelationMapper.selectAreaIds(ids);
		
		System.out.println("Completed...");
	}


}
