package com.fujitsu.listener;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

public class QuartzSchedulerListener implements SchedulerListener {
	
	Logger logger  =  Logger.getLogger(QuartzJobListener. class );

	@Override
	public void schedulerInStandbyMode() {
		logger.info("【scheduler】:待命状态！");
	}

	@Override
	public void schedulerShuttingdown() {
		logger.info("【scheduler】:正在关闭！");
	}

	@Override
	public void schedulerStarted() {
		logger.info("【scheduler】:启动！");
	}
	@Override
	/*Scheduler 在有新的 JobDetail 部署或卸载时调用这两个中的相应方法。*/
	public void jobScheduled(Trigger trigger) {
		String jobName = trigger.getJobName();
		logger.info("【scheduler】:"+jobName + "被部署！");
	}
	@Override
	/*Scheduler 在有新的 JobDetail 部署或卸载时调用这两个中的相应方法。*/
	public void jobUnscheduled(String triggerName, String triggerGroup) {

		if (triggerName == null) {
			// triggerGroup is being unscheduled
			logger.info("【scheduler】:"+triggerGroup + "被卸载！");
		} else {
			logger.info("【scheduler】:"+triggerName + "被卸载！");
		}
	}
	@Override
	/*当一个 Trigger 来到了再也不会触发的状态时调用这个方法。除非这个 Job 已设置成了持久性，否则它就会从 Scheduler 中移除。*/
	public void triggerFinalized(Trigger trigger) {
		String jobName = trigger.getJobName();
		logger.info("【scheduler】:"+jobName + "的触发器结束！");
	}
	@Override
	/*Scheduler 调用这个方法是发生在一个 Trigger 或 Trigger 组被暂停时。假如是 Trigger 组的话，triggerName 参数将为 null。*/
	public void triggersPaused(String triggerName, String triggerGroup) {

		if (triggerName == null) {
			// triggerGroup is being unscheduled
			logger.info("【scheduler】:"+triggerGroup + "暂停！");
		} else {
			logger.info("【scheduler】:"+triggerName + "暂停！");
		}
	}
	@Override
	/*Scheduler 调用这个方法是发生成一个 Trigger 或 Trigger 组从暂停中恢复时。假如是 Trigger 组的话，triggerName 参数将为 null。*/
	public void triggersResumed(String triggerName, String triggerGroup) {

		if (triggerName == null) {
			// triggerGroup is being unscheduled
			logger.info("【scheduler】:"+triggerGroup + "恢复！");
		} else {
			logger.info("【scheduler】:"+triggerName + "恢复！");
		}
	}
	
	@Override
	public void jobAdded(JobDetail job) {
		String jobName = job.getFullName();
		logger.info("【scheduler】:"+jobName + "添加！");

	}

	@Override
	public void jobDeleted(String jobName, String jobGroup) {
		if (jobName == null) {
			logger.info("【scheduler】:"+jobGroup + "删除！");
		} else {
			logger.info("【scheduler】:"+jobName + "删除！");
		}
	}
	@Override
	/*当一个或一组 JobDetail 暂停时调用这个方法。*/
	public void jobsPaused(String jobName, String jobGroup) {
		if (jobName == null) {
			logger.info("【scheduler】:"+jobGroup + "暂停！");
		} else {
			logger.info("【scheduler】:"+jobName + "暂停！");
		}
	}
	@Override
	/*当一个或一组 Job 从暂停上恢复时调用这个方法。假如是一个 Job 组，jobName 参数将为 null。*/
	public void jobsResumed(String jobName, String jobGroup) {
		if (jobName == null) {
			logger.info("【scheduler】:"+jobGroup + "恢复！");
		} else {
			logger.info("【scheduler】:"+jobName + "恢复！");
		}
	}
	@Override
/*	在 Scheduler 的正常运行期间产生一个严重错误时调用这个方法。错误的类型会各式的，但是下面列举了一些错误例子：

    ·初始化 Job 类的问题

    ·试图去找到下一 Trigger 的问题

    ·JobStore 中重复的问题

    ·数据存储连接的问题

	你可以使用 SchedulerException 的 getErrorCode() 或者 getUnderlyingException() 方法或获取到特定错误的更详尽的信息。
*/
	public void schedulerError(String msg, SchedulerException cause) {
		logger.info("【scheduler】:"+msg + cause.getUnderlyingException());
	}
	@Override
	/*Scheduler 调用这个方法用来通知 SchedulerListener Scheduler 将要被关闭。*/
	public void schedulerShutdown() {
		logger.info("【scheduler】:将要被关闭！");
	}

}
