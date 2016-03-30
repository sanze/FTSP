package com.fujitsu.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.handler.ExceptionHandler;

public class ResourceTaskTestJob2 implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			System.out.println("任务2....." );

		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

}