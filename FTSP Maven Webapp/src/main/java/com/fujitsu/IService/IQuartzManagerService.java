package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import org.quartz.Trigger;

import com.fujitsu.common.CommonException;

public interface IQuartzManagerService {
	/*
	 * cron 表达式 一个cron表达式有至少6个（也可能是7个）由空格分隔的时间元素。从左至右，这些元素的定义如下：
	 * 
	 * 1．秒（0–59） 
	 * 2．分钟（0–59）
	 * 3．小时（0–23） 
	 * 4．月份中的日期（1–31） 
	 * 5．月份（1–12或JAN–DEC）
	 * 6．星期中的日期（1–7或SUN–SAT） 
	 * 7．年份（1970–2099）
	 * 
	 * 每一个元素都可以显式地规定一个值（如6），一个区间（如9-12），
	 * 一个列表（如9，11，13）或一个通配符（如*）。“月份中的日期”和“星期中的日期”
	 * 这两个元素是互斥的，因此应该通过设置一个问号（？）来表明你不想设置的那个字段。
	 * 
	 * 是一个通配符，表示任何值，用在Minutes字段中表示每分钟。
	 * ?只可以用在day-of-month或者Day-of-Week字段中，用来表示不指定特殊的值。
	 * -用来表示一个范围，比如10-12用在Month中表示10到12月。
	 * ,用来表示附加的值，比如MON,WED,FRI在day-of-week字段中表示礼拜一和礼拜三和礼拜五。
	 * /用来表示增量，比如0/15用在Minutes字段中表示从0分开始0和15和30和45分。
	 * L只可以用在day-of-month或者Day-of-Week字段中，如果用在Day-of-month中，表示某个月
	 * 的最后一天，1月则是表示31号，2月则表示28号（非闰年），如果用在Day-of-Week中表示礼
	 * 拜六（数字7）；但是如果L与数字组合在一起用在Day-of-month中，比如6L，则表示某个月 的最后一个礼拜六；
	 */

	// cron表达式 示例
	// "0 0 12 * * ?" 			每天中午12点执行
	// "0 15 10 ? * *" 			每天上午10:15执行
	// "0 15 10 * * ?" 			每天上午10:15执行
	// "0 15 10 * * ? *" 		每天上午10:15执行
	// "0 15 10 * * ? 			2005" 2005年内每天上午10:15执行
	// "0 * 14 * * ?" 			每天下午2:00到2:59，每分钟执行
	// "0 0/5 14 * * ?" 		每天下午2:00到2:55，每5分钟执行
	// "0 0/5 14,18 * * ?" 		每天下午2:00到2:55 以及6:00到6:55  ，每5分钟执行
	// "0 0-5 14 * * ?" 		每天下午2:00到2:05，每分钟执行
	// "0 10,44 14 ? 3 WED" 	每年三月的周三的下午2:10以及2:44执行
	// "0 15 10 ? * MON-FRI" 	每周1-5的上午10:15执行
	// "0 15 10 15 * ?" 		每个月的第15天的上午10:15执行
	// "0 15 10 L * ?" 			每个月的最后一天的上午10:15执行
	// "0 15 10 ? * 6L" 		每个月的最后一个周五的上午10:15执行
	// "0 15 10 ? * 6L 2002-2005" 	2002-2005年的每个月的最后一个周五的上午10:15执行
	// "0 15 10 ? * 6#3" 		每个月的第3个周五的上午10:15执行
	/**
	 * 添加一个定时任务，使用默认的任务组名，触发器组名
	 * 
	 * @param taskType 			任务类型
	 * @param taskID			任务ID 为null时使用CommonDefine.QUARTZ.SYSTEM_TASK_ID常量
	 * @param jobClass			任务对应Class，必须实现Job接口
	 * @param cronExpression	cron表达式
	 */
	@SuppressWarnings("rawtypes")
	public void addJob(int taskType, Integer taskID, Class jobClass,
			String cronExpression) throws CommonException;	
	/**
	 * 添加一个定时任务，使用默认的任务组名，触发器组名
	 * 
	 * @param taskType 			任务类型
	 * @param taskID			任务ID 为null时使用CommonDefine.QUARTZ.SYSTEM_TASK_ID常量
	 * @param jobClass			任务对应Class，必须实现Job接口
	 * @param cronExpression	cron表达式
	 * @param jobDecription	任务描述，可以用来存储参数
	 */
	@SuppressWarnings("rawtypes")
	public void addJob(int taskType, Integer taskID, Class jobClass,
			String cronExpression, Map jobParam) throws CommonException;
	/**
	 * 任务控制方法
	 * @param taskType	任务类型
	 * @param taskID	任务ID 为null时使用CommonDefine.QUARTZ.SYSTEM_TASK_ID常量
	 * @param ctrlFlag	控制标记<br>
	 * 　　　　CommonDefine.QUARTZ.ACTIVITE: 立刻激活<br>
	 * 　　　　CommonDefine.QUARTZ.JOB_PAUSE: 暂停-不能停止正在进行中的任务，停止的是下一次的计划任务<br>
	 * 　　　　CommonDefine.QUARTZ.JOB_DELETE: 删除<br>
	 * 　　　　CommonDefine.QUARTZ.JOB_RESUME: 继续下一次的计划任务
	 */
	public void ctrlJob(int taskType, Integer taskID, int ctrlFlag) throws CommonException;
	/**
	 * 直接根据名称和触发器控制任务
	 * @param jobName
	 * @param triggerName
	 * @param jobFlag
	 * @throws CommonException
	 */
	public void ctrlJob(String jobName, String triggerName, int jobFlag)
			throws CommonException;
	
	/**
	 * 
	 * @param taskType			任务类型
	 * @param taskID			任务ID 为null时使用CommonDefine.QUARTZ.SYSTEM_TASK_ID常量
	 * @param cronExpression	新的cron表达式
	 */
	public void modifyJobTime(int taskType, Integer taskID, String cronExpression) throws CommonException;
	/**
	 * 修改任务时间
	 * @param jobName
	 * @param triggerName
	 * @param jobTime
	 * @throws CommonException
	 */
	public void modifyJobTime(String jobName, String triggerName, String jobTime) throws CommonException;

	/**
	 * 获取所有的任务名称
	 * @return
	 */
	public List<String> getAllJobs() throws CommonException;
	
	
	/**
	 * 获取所有触发器
	 * @return
	 */
	public List<Trigger> getAllTrigger() throws CommonException;
	
	/**
	 * 获取任务信息
	 * @param taskType			任务类型
	 * @param taskID			任务ID 为null时使用CommonDefine.QUARTZ.SYSTEM_TASK_ID常量
	 * @return Map字段：<br>
	 *　　nextFireTime        下一次触发时间<br>
	 *　　prevFireTime        上一次触发时间<br>
	 *　　startTime           任务启动时间<br>
	 *　　cronExpresssion     cron表达式<br>
	 *　　jobName             任务名称<br>
	 *　　triggerName         触发器名称
	 */
	public Map<String, Object> getJobInfo(int taskType, Integer taskID) throws CommonException;
	
	
	/**
	 * 判断任务是否存在
	 * @param taskType			任务类型
	 * @param taskID			任务ID 为null时使用CommonDefine.QUARTZ.SYSTEM_TASK_ID常量
	 * @return
	 */
	public boolean IsJobExist(int taskType, Integer taskID) throws CommonException;
	/**
	 * 获取所有Cron任务的信息
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getAllJobInfo() throws CommonException;
	
	
	/**
	 * 重新调度任务
	 */
	public void rescheduleJob(String triggerName, String groupName, Trigger newTrigger,Integer triggerState) throws CommonException;
	
	
//	/**
//	 * id为String的添加job方法
//	 * @param taskType
//	 * @param taskID
//	 * @param jobClass
//	 * @param cronExpression
//	 * @param jobParam
//	 * @throws CommonException
//	 */
//	public void addJob(int taskType, String taskID, Class jobClass,
//			String cronExpression,  Map jobParam)  throws CommonException;
}
