package com.fujitsu.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class QuartzJobListener implements JobListener {
	
	 Logger logger  =  Logger.getLogger(QuartzJobListener. class );
	 
	 private static List<String> quartzPool = new ArrayList<String>();
	
	private String name;
	//操作类型
	private static final int OPERATE_START = 1;
	private static final int OPERATE_VETOED= 2;
	private static final int OPERATE_END = 3;
	
	public QuartzJobListener(String name){
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	/* Scheduler 在 JobDetail 将要被执行时调用这个方法。 */
	public void jobToBeExecuted(JobExecutionContext context) {
		String jobName = context.getJobDetail().getName();
		logger.info("【job】:"+jobName + "将要执行！");
		handlerJobPrint(jobName,OPERATE_START);
	}

	@Override
	/* Scheduler 在 JobDetail 即将被执行，但又被 TriggerListener 否决了时调用这个方法。 */
	public void jobExecutionVetoed(JobExecutionContext context) {
		String jobName = context.getJobDetail().getName();
		logger.info("【job】:"+jobName + "被 TriggerListener 否决");
		handlerJobPrint(jobName,OPERATE_VETOED);
	}

	@Override
	/* Scheduler 在 JobDetail 被执行之后调用这个方法。 */
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {

		String jobName = context.getJobDetail().getName();
		logger.info("【job】:"+jobName + "执行完成！");
		handlerJobPrint(jobName,OPERATE_END);
	}
	
	private void handlerJobPrint(String jobName,int operate){
		switch(operate){
		case OPERATE_START:
			quartzPool.add(jobName);
			break;
		case OPERATE_VETOED:
			break;
		case OPERATE_END:
			quartzPool.remove(jobName);
			break;
		}
		if(quartzPool.size()<50){
			logger.info("【job】:正在运行的任务数："+quartzPool.size());
		}else{
			String jobList = "";
			for(String name:quartzPool){
				jobList = jobList+name+"||";
			}
			logger.warn("【job】:危险！！！正在运行的任务数："+quartzPool.size());
			logger.warn("【job】:任务详细"+jobList);
		}
	}

}
