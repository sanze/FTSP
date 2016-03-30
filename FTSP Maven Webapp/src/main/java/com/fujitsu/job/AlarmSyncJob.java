package com.fujitsu.job;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;

public class AlarmSyncJob implements Job {

	private IAlarmManagementService alarmManagementService;
	
	public AlarmSyncJob() {
		alarmManagementService =(IAlarmManagementService) SpringContextUtil
				.getBean("alarmManagementServiceImpl");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//System.out.println("进入quartz，进行告警自动同步。。。。");
		Map<String,Object> map = new HashMap<String,Object>();
		// 网管ID
		int emsConnectionId = Integer.parseInt(context.getJobDetail()
						.getJobDataMap().get("BASE_EMS_CONNECTION_ID").toString());
		//自增ID
		int id = Integer.parseInt(context.getJobDetail()
				.getJobDataMap().get("ID").toString());
		map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
		map.put("ID", id);
		try {
			alarmManagementService.alarmAutoSynch(map);
		} catch (Exception e) {
			//更新最近一次同步时间
			map.put("EXECUTE_STATUS", 2);
			alarmManagementService.updateAlarmAutoSynchExcuteStatus(map);
			System.out.println("告警自动同步失败！网管Id："+emsConnectionId);
			ExceptionHandler.handleException(e);
		}
	}

}
