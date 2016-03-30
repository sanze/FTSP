package com.fujitsu.job;

import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJob implements Job {
//	private IDataCollectService dataCollectService = (IDataCollectService) SpringContextUtil.getDataCollectService(emsConnectionId);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		System.out.println(new Date()+"我在执行");
		
	}

}
