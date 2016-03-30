package com.fujitsu.manager.quartzManager.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.QuartzManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.listener.QuartzJobListener;
import com.fujitsu.manager.quartzManager.service.QuartzManagerService;
import com.fujitsu.util.CommonUtil;

/**
 * @author tianhongjun
 * 
 */
public class QuartzManagerServiceImpl extends QuartzManagerService {
	@Resource
	QuartzManagerMapper quartzManagerMapper;
	public QuartzManagerServiceImpl(Scheduler scheduler) {
		QuartzManagerServiceImpl.scheduler = scheduler;
		init();
	}
	
	private void init(){
		try {
			QuartzManagerServiceImpl.scheduler.addGlobalJobListener(new QuartzJobListener("QuartzJobListener"));
			QuartzManagerServiceImpl.scheduler.addGlobalJobListener(new QuartzJobListener("QuartzJobListener"));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	
	private static Scheduler scheduler ;

	private String DEFAULT_JOB_GROUP_NAME = "defaultJobGroup";
	private String DEFAULT_TRIGGER_GROUP_NAME = "defaultTriggerGroup";

	@SuppressWarnings("rawtypes")
	@Override
	public void addJob(int taskType, Integer taskID, Class jobClass,
			String cronExpression) throws CommonException{

		//taskId转换，如果是null的话设置Id为SYSTEM_TASK_ID
		taskID = transTaskId(taskID);
		//任务类型 6.性能采集 7.电路自动生成 8.定制报表 10.割接任务 11.巡检任务 12.网管基础信息同步
		if(taskType<0 || taskType>CommonDefine.QUARTZ.TYPE_NAMES.length)
			return;
		String jobName = getJobName(taskType,taskID);
		String triggerName = getTriggerName(taskType,taskID);
		
		JobDetail jobDetail = new JobDetail(jobName, DEFAULT_JOB_GROUP_NAME, jobClass);// 任务名，任务组，任务执行类
		//不需要任务恢复
		jobDetail.setDurability(true);   //如果这个值为false，每次任务没有活动的触发器关联时都将从Scheduler中删除。
		jobDetail.setRequestsRecovery(false);
		// 触发器
		CronTrigger trigger = new CronTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);// 触发器名,触发器组
		try {
			// 触发器时间设定
			trigger.setCronExpression(cronExpression);
			//设置过时不再触发
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
			scheduler.scheduleJob(jobDetail, trigger);
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
			
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		} catch (ParseException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_PARSE_EXCEPTION);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IQuartzManagerService#addJob(int, int, java.lang.Class, java.lang.String, int)
	 */
	@Override
	public void addJob(int taskType, Integer taskID, Class jobClass,
			String cronExpression,  Map jobParam)  throws CommonException{
		//taskId转换，如果是null的话设置Id为SYSTEM_TASK_ID
		taskID = transTaskId(taskID);
		
		///任务类型 6.性能采集 7.电路自动生成 8.定制报表 10.割接任务 11.巡检任务 12.网管基础信息同步
		if(taskType<0 || taskType>CommonDefine.QUARTZ.TYPE_NAMES.length-1)
			return;
		String jobName = getJobName(taskType,taskID);
		String triggerName = getTriggerName(taskType,taskID);

		JobDetail jobDetail = new JobDetail(jobName, DEFAULT_JOB_GROUP_NAME, jobClass);// 任务名，任务组，任务执行类
		//不需要任务恢复
		jobDetail.setDurability(true);   //如果这个值为false，每次任务没有活动的触发器关联时都将从Scheduler中删除。
		jobDetail.setRequestsRecovery(false);
		JobDataMap jobDataMap = new JobDataMap(jobParam);
		jobDetail.setJobDataMap(jobDataMap);
		// 触发器
		CronTrigger trigger = new CronTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);// 触发器名,触发器组
		try {
			// 触发器时间设定
			trigger.setCronExpression(cronExpression);
			//设置过时不再触发
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
			scheduler.scheduleJob(jobDetail, trigger);
			if (scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		} catch (ParseException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_PARSE_EXCEPTION);
		}
	}

	@Override
	public void ctrlJob(int taskType, Integer taskID, int jobFlag)  throws CommonException{
		//taskId转换，如果是null的话设置Id为SYSTEM_TASK_ID
		taskID = transTaskId(taskID);
		
		String jobName = getJobName(taskType,taskID);
		String triggerName = getTriggerName(taskType,taskID);
		ctrlJob(jobName, triggerName, jobFlag);
	}
	@Override
	public void ctrlJob(String jobName,String triggerName, int jobFlag)  throws CommonException{
		try {
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
	//		//取得任务
	//		JobDetail jobDetail = scheduler.getJobDetail(jobName, jobName);
			switch(jobFlag){
			case CommonDefine.QUARTZ.JOB_ACTIVATE:
//				System.out.println("JOB_ACTIVATE");
				//立刻执行
				scheduler.triggerJob(jobName, DEFAULT_JOB_GROUP_NAME);
				//CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
				//数据库中记录上次执行时间
				trigger.setPreviousFireTime(new Date());
				trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
				int triggerState = scheduler.getTriggerState(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
				rescheduleJob(triggerName, DEFAULT_TRIGGER_GROUP_NAME, trigger,triggerState);
				break;
			case CommonDefine.QUARTZ.JOB_PAUSE:
//				System.out.println("JOB_PAUSE");
				scheduler.pauseJob(jobName, DEFAULT_JOB_GROUP_NAME);
				scheduler.pauseTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
				break;
			case CommonDefine.QUARTZ.JOB_RESUME:
//				System.out.println("JOB_RESUME");				
				scheduler.resumeJob(jobName, DEFAULT_JOB_GROUP_NAME);
				scheduler.resumeTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
				break;
			case CommonDefine.QUARTZ.JOB_DELETE:
//				System.out.println("JOB_DELETE");
				// 停止触发器
				scheduler.pauseTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
				// 移除触发器
				scheduler.unscheduleJob(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
				// 删除任务
				scheduler.deleteJob(jobName, DEFAULT_JOB_GROUP_NAME);
				break;
			}		
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		}

	}

	@Override
	public void modifyJobTime(int taskType, Integer taskID, String cronExpression)  throws CommonException{
		//taskId转换，如果是null的话设置Id为SYSTEM_TASK_ID
		taskID = transTaskId(taskID);
		String jobName = getJobName(taskType,taskID);
		String triggerName = getTriggerName(taskType,taskID);
		modifyJobTime(jobName, triggerName, cronExpression);

	}
	@Override
	public void modifyJobTime(String jobName, String triggerName, String cronExpression)  throws CommonException{
		Trigger trigger;
		try {
			trigger = scheduler.getTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
			int triggerState = scheduler.getTriggerState(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
			if (trigger != null) {
				CronTrigger ct = (CronTrigger) trigger;
				ct.setCronExpression(cronExpression);
				//设置过时不再触发
				trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
				scheduler.resumeTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
				rescheduleJob(triggerName, DEFAULT_TRIGGER_GROUP_NAME,
						trigger,triggerState);
			}else{
				try {
					JobDetail jobDetail=scheduler.getJobDetail(jobName,DEFAULT_JOB_GROUP_NAME);
					scheduler.deleteJob(jobName, DEFAULT_JOB_GROUP_NAME);
					if(jobDetail!=null){
						// 触发器
						trigger = new CronTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);// 触发器名,触发器组
						// 触发器时间设定
						((CronTrigger)trigger).setCronExpression(cronExpression);
						//设置过时不再触发
						((CronTrigger)trigger).setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
						scheduler.scheduleJob(jobDetail, trigger);
						rescheduleJob(triggerName, DEFAULT_TRIGGER_GROUP_NAME,
								trigger,Trigger.STATE_PAUSED);
					}
				} catch (SchedulerException e) {
					ExceptionHandler.handleException(e);
					throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
				} catch (ParseException e) {
					ExceptionHandler.handleException(e);
					throw new CommonException(e,MessageCodeDefine.QUARTZ_PARSE_EXCEPTION);
				}
			}
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		} catch (ParseException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_PARSE_EXCEPTION);
		}

	}

	@Override
	public List<String> getAllJobs()  throws CommonException{
		List<String> jobs = new ArrayList<String>();
		try {
			String[] jobGroupNames = scheduler.getJobGroupNames();
			for (String jobGroup : jobGroupNames) {
				String[] jobNames = scheduler.getJobNames(jobGroup);
				for (String jobName : jobNames) {
					JobDetail job = scheduler.getJobDetail(jobName, jobGroup);
					jobs.add(job.getFullName());
				}
			}
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		}
		return jobs;
	}

	@Override
	public boolean IsJobExist(int taskType, Integer taskID) throws CommonException{
		//taskId转换，如果是null的话设置Id为SYSTEM_TASK_ID
		taskID = transTaskId(taskID);
		
		String jobName = getJobName(taskType,taskID);
//		String triggerName = getTriggerName(taskType,taskID);
		JobDetail job;
		try {
			job = scheduler.getJobDetail(jobName, DEFAULT_JOB_GROUP_NAME);
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		}
		if(job!=null){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Map<String, Object> getJobInfo(int taskType, Integer taskID)  throws CommonException{
		//taskId转换，如果是null的话设置Id为SYSTEM_TASK_ID
		taskID = transTaskId(taskID);
		
//		String jobName = getJobName(taskType,taskID);
		String triggerName = getTriggerName(taskType,taskID);
		Map<String, Object> rv = new HashMap<String, Object>();
		try {
//			JobDetail job = scheduler.getJobDetail(jobName, DEFAULT_JOB_GROUP_NAME);
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
			rv.put("nextFireTime", trigger.getNextFireTime());
			rv.put("prevFireTime", trigger.getPreviousFireTime());
			rv.put("startTime", trigger.getStartTime());
			rv.put("cronExpresssion", trigger.getCronExpression());
//			rv.put("description", trigger.getDescription());
//			rv.put("endTime", trigger.getEndTime());
			rv.put("jobName", trigger.getFullJobName());
			rv.put("triggerName", trigger.getFullName());
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		}
		return rv;
	}
	
	
	/**
	 * 取得所有job任务信息
	 * 
	 */
	public Map<String, Object> getAllJobInfo() throws CommonException{

		Map<String, Object> rlt = new HashMap<String, Object>();
		List<Map<String, Object>> jobModelList = new ArrayList<Map<String, Object>>();
		try {
			SimpleDateFormat formatter = CommonUtil.getDateFormatter(CommonDefine.COMMON_FORMAT);

			Map jobModel = null;
			
			List<Trigger> allTrigger = getAllTrigger();
			
			for(Trigger trigger:allTrigger){
				if(trigger == null){
					continue;
				}
				//取得触发器状态
				int status = scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
				
				jobModel = new HashMap();
				
				switch (status){
				case Trigger.STATE_BLOCKED:
					jobModel.put("TRIGGER_STATE", "阻塞");
					break;
				case Trigger.STATE_COMPLETE:
					jobModel.put("TRIGGER_STATE", "完成");
					break;
				case Trigger.STATE_ERROR:
					jobModel.put("TRIGGER_STATE", "错误");
					break;
				case Trigger.STATE_NONE:
					jobModel.put("TRIGGER_STATE", "无");
					break;
				case Trigger.STATE_NORMAL:
					jobModel.put("TRIGGER_STATE", "正常");
					break;
				case Trigger.STATE_PAUSED:
					jobModel.put("TRIGGER_STATE", "暂停");
					break;
				}
				
				jobModel.put("TRIGGER_NAME", trigger.getName()!=null?trigger.getName():"");
				jobModel.put("TRIGGER_GROUP", trigger.getGroup()!=null?trigger.getGroup():"");
				jobModel.put("JOB_NAME", trigger.getJobName()!=null?trigger.getJobName():"");
				jobModel.put("JOB_GROUP", trigger.getJobGroup()!=null?trigger.getJobGroup():"");
				jobModel.put("IS_VOLATILE", trigger.isVolatile());
				jobModel.put("DESCRIPTION", trigger.getDescription()!=null?trigger.getJobGroup():"");
				//设置返回数据
				if(trigger.getPreviousFireTime()!=null){
					jobModel.put("PREV_FIRE_TIME", formatter.format(trigger.getPreviousFireTime()));
				}
				if(trigger.getNextFireTime()!=null){
					jobModel.put("NEXT_FIRE_TIME", formatter.format(trigger.getNextFireTime()));
		}
				if(trigger.getStartTime()!=null){
					jobModel.put("START_TIME", formatter.format(trigger.getStartTime()));
	}
				if(trigger.getEndTime()!=null){
					jobModel.put("END_TIME", formatter.format(trigger.getEndTime()));
		}
				jobModel.put("PRIORITY", trigger.getPriority());
				if(CronTrigger.class.isInstance(trigger)){
					CronTrigger xxx = (CronTrigger)trigger;
					jobModel.put("TRIGGER_TYPE", "CRON");
					jobModel.put("CRON_EXPRESSION", xxx.getCronExpression()!=null?xxx.getCronExpression():"");
					switch (trigger.getMisfireInstruction()){
					case 0:
						jobModel.put("MISFIRE_INSTR", "智能策略");
						break;
					case 1:
						jobModel.put("MISFIRE_INSTR", "立即触发一次");
						break;
					case 2:
						jobModel.put("MISFIRE_INSTR", "什么都不干");
						break;
	}
		}else{
					jobModel.put("TRIGGER_TYPE", "SIMPLE");
					jobModel.put("CRON_EXPRESSION", "无");
					switch (trigger.getMisfireInstruction()){
					case 0:
						jobModel.put("MISFIRE_INSTR", "智能策略");
						break;
					}
			}
				jobModel.put("CALENDAR_NAME", trigger.getCalendarName()!=null?trigger.getCalendarName():"");
//				jobModel.put("JOB_DATA", trigger.getName());
				jobModelList.add(jobModel);
		}
		} catch (Exception e){
			ExceptionHandler.handleException(e);
	}
		rlt.put("total", jobModelList.size());
		rlt.put("rows", jobModelList);
		return rlt;
	}
		

	//取得所有触发器
	public List<Trigger> getAllTrigger() throws CommonException{
		
		List<Trigger> allTrigger = new ArrayList<Trigger>();
		//取得组名字列表
		String[] triggerGroupNameList;
		try {
			triggerGroupNameList = scheduler.getTriggerGroupNames();
			
			for(String triggerGroupName:triggerGroupNameList){
				//取得任务名称列表
				String[] triggerNameList = scheduler.getTriggerNames(triggerGroupName);
				
				for(String triggerName:triggerNameList){
					
					//取得触发器对象
					Trigger trigger = scheduler.getTrigger(triggerName, triggerGroupName);
					
					if(trigger == null){
						continue;
					}
					allTrigger.add(trigger);
				}
			}
			
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		}
		return allTrigger;
			}
	
	
	/**
	 * 重新调度任务
	 * 实现过程是删除旧trigger,添加新trigger,新trigger中需要包含job
	 * @throws SchedulerException 
	 */
	@Override
	public void rescheduleJob(String triggerName, String groupName, Trigger newTrigger,Integer triggerState) throws CommonException{
		try {
			if(triggerState == null){
				triggerState = scheduler.getTriggerState(triggerName, groupName);
			}
			scheduler.rescheduleJob(triggerName, groupName, newTrigger);
			//rescheduleJob后会将trigger状态修改为waiting，所以此处需要判断旧trigger状态，如果是暂停需要回复原状
			switch (triggerState){
			case Trigger.STATE_BLOCKED:
				break;
			case Trigger.STATE_COMPLETE:
				break;
			case Trigger.STATE_ERROR:
				break;
			case Trigger.STATE_NONE:
				break;
			case Trigger.STATE_NORMAL:
				break;
			case Trigger.STATE_PAUSED:
				scheduler.pauseTrigger(newTrigger.getName(), newTrigger.getGroup());
				break;
			}
		} catch (SchedulerException e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
		}
	}
	
	//获取jobName
	private String getJobName(int taskType, Integer taskID){
		String jobName = CommonDefine.QUARTZ.TYPE_NAMES[taskType] + "_" + taskID;
		return jobName;
	}
	//获取triggerName
	private String getTriggerName(int taskType, int taskID){
		String jobName = CommonDefine.QUARTZ.TYPE_NAMES[taskType] + "_" + taskID;
		String triggerName = jobName + "_Trigger";
		return triggerName;
	}
	
	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IQuartzManagerService#addJob(int, int, java.lang.Class, java.lang.String, int)
	 */
//	@Override
//	public void addJob(int taskType, String taskID, Class jobClass,
//			String cronExpression,  Map jobParam)  throws CommonException{
//		///任务类型 6.性能采集 7.电路自动生成 8.定制报表 10.割接任务 11.巡检任务 12.网管基础信息同步
//		if(taskType<0 || taskType>CommonDefine.QUARTZ.TYPE_NAMES.length-1)
//			return;
//		String jobName = getJobName(taskType,taskID);
//		String triggerName = getTriggerName(taskType,taskID);
//
//		JobDetail jobDetail = new JobDetail(jobName, DEFAULT_JOB_GROUP_NAME, jobClass);// 任务名，任务组，任务执行类
//		//不需要任务恢复
//		jobDetail.setDurability(true);   //如果这个值为false，每次任务没有活动的触发器关联时都将从Scheduler中删除。
//		jobDetail.setRequestsRecovery(false);
//		JobDataMap jobDataMap = new JobDataMap(jobParam);
//		jobDetail.setJobDataMap(jobDataMap);
//		// 触发器
//		CronTrigger trigger = new CronTrigger(triggerName, DEFAULT_TRIGGER_GROUP_NAME);// 触发器名,触发器组
//		try {
//			// 触发器时间设定
//			trigger.setCronExpression(cronExpression);
//			scheduler.scheduleJob(jobDetail, trigger);
//			if (!scheduler.isShutdown()) {
//				scheduler.start();
//			}
//		} catch (SchedulerException e) {
//			throw new CommonException(e,MessageCodeDefine.QUARTZ_SCHEDULER_EXCEPTION);
//		} catch (ParseException e) {
//			throw new CommonException(e,MessageCodeDefine.QUARTZ_PARSE_EXCEPTION);
//		}
//	}
	
	//获取jobName
	private String getJobName(int taskType, String taskID){
		String jobName = CommonDefine.QUARTZ.TYPE_NAMES[taskType] + "_" + taskID;
		return jobName;
	}
	//获取triggerName
	private String getTriggerName(int taskType, String taskID){
		String jobName = CommonDefine.QUARTZ.TYPE_NAMES[taskType] + "_" + taskID;
		String triggerName = jobName + "_Trigger";
		return triggerName;
	}
	
	//任务Id转换
	private int transTaskId(Integer taskID){
		//taskId转换，如果是null的话设置Id为SYSTEM_TASK_ID
		taskID = taskID!=null?taskID:CommonDefine.QUARTZ.SYSTEM_TASK_ID;
		return taskID;
	}
	
	public static void main(String args[]){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date(1402926300000L)));
		
//		SpringContextUtil util = new SpringContextUtil();
//		
//		IQuartzManagerService xxx = (IQuartzManagerService) SpringContextUtil.getBean("quartzManagerService");
//		
//		try {
//			for(String name:xxx.getAllJobs()){
//				System.out.println(name);
//			}
//			System.out.println(xxx.IsJobExist(7, 3435));
//		} catch (CommonException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		}
		
	
}
