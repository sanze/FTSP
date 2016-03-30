package com.fujitsu.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;

public class AlarmAutoShiftJob implements Job {
	private IAlarmManagementService alarmManagementService;
	
	public AlarmAutoShiftJob() {
		alarmManagementService =(IAlarmManagementService) SpringContextUtil
				.getBean("alarmManagementServiceImpl");
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			alarmManagementService.alarmAutoShift();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

}