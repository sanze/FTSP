package com.fujitsu.listener;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

/**
 * @author xuxiaojun
 * 
 */
public class QuartzTriggerListener implements TriggerListener {
	
	Logger logger  =  Logger.getLogger(QuartzJobListener. class );
	
	private String name;
	
	public QuartzTriggerListener(String name){
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	/*
	 * 当与监听器相关联的 Trigger 被触发，Job 上的 execute() 方法将要被执行时，Scheduler 就调用这个方法。在全局
	 * TriggerListener 情况下，这个方法为所有 Trigger 被调用。
	 */
	public void triggerFired(Trigger trigger, JobExecutionContext context) {

		String triggerName = trigger.getName();
		logger.info(("【trigger】:"+triggerName + " 被触发！"));
	}

	@Override
	/*
	 * 在 Trigger 触发后，Job 将要被执行时由 Scheduler 调用这个方法。TriggerListener 给了一个选择去否决 Job
	 * 的执行。假如这个方法返回 true，这个 Job 将不会为此次 Trigger 触发而得到执行。
	 */
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {

		String triggerName = trigger.getName();
		logger.info("【trigger】:"+triggerName + " was not vetoed");
		return false;
	}

	@Override
	/*
	 * Scheduler 调用这个方法是在 Trigger 错过触发时。如这个方法的 JavaDoc
	 * 所指出的，你应该关注此方法中持续时间长的逻辑：在出现许多错过触发的 Trigger 时，长逻辑会导致骨牌效应。你应当保持这上方法尽量的小。
	 */
	public void triggerMisfired(Trigger trigger) {
		String triggerName = trigger.getName();
		logger.info("【trigger】:"+triggerName + "错过触发！");
	}

	@Override
	/*
	 * Trigger 被触发并且完成了 Job 的执行时，Scheduler 调用这个方法。这不是说这个 Trigger 将不再触发了，而仅仅是当前
	 * Trigger 的触发(并且紧接着的 Job 执行) 结束时。这个 Trigger 也许还要在将来触发多次的。
	 */
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			int triggerInstructionCode) {

		String triggerName = trigger.getName();
		logger.info("【trigger】:"+triggerName + " 完成！");
	}

}
