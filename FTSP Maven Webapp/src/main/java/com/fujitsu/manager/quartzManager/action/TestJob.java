package com.fujitsu.manager.quartzManager.action;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 一个简单的quartz调用job
 * @author 田洪俊
 *
 */
public class TestJob implements Job {

    public TestJob() {
    	System.out.println("---------->>>" + new Date() + "<<<----------");
		System.out.println("------------------------------------------------");
    }

    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        // 获取任务名称
        String jobName = jobDetail.getName();
        //JobDataMap dat = jobDetail.getJobDataMap();
        //int len = dat.getInt("len");
        // 记录任务开始执行的时间   
        System.out.println("[" + new Date()+"] -> " + jobName);
        //任务所配置的数据映射表        
        JobDataMap dataMap = jobDetail.getJobDataMap();
		System.out.println("--------------------------------");
    	try {
    		for (int i = 0; i < 5; i++) {
	    		for (int j = 0; j < Math.random()*10; j++) {
					System.out.print("*");
				}
	    		System.out.println();
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("--------------------------------");
    }

}