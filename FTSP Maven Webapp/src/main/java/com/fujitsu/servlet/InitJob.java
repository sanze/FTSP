package com.fujitsu.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.quartz.CronTrigger;
import org.quartz.Trigger;

import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.AlarmManagementMapper;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.dao.mysql.ConnectionManagerMapper;
import com.fujitsu.dao.mysql.NetworkManagerMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.dao.mysql.ResourceSystemCorrelationMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.job.AlarmAutoConfirmJob;
import com.fujitsu.job.AlarmAutoShiftJob;
import com.fujitsu.job.AlarmDataConvergeJob;
import com.fujitsu.job.AlarmSyncJob;
import com.fujitsu.job.AutoCreateResourceJob;
import com.fujitsu.job.CheckServerStatus;
import com.fujitsu.job.NeEarlyAlarmJob;
import com.fujitsu.job.NotificationJob;
import com.fujitsu.job.PerformanceDataConvergeJob;
import com.fujitsu.job.PtnReportDataSourceJob;
import com.fujitsu.job.ResourceCorrelationJob;
import com.fujitsu.job.SyncLinkPmJob;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

/*cron 表达式
 * 一个cron表达式有至少6个（也可能是7个）由空格分隔的时间元素。从左至右，这些元素的定义如下：

 1．秒（0–59） 2．分钟（0–59） 3．小时（0–23） 4．月份中的日期（1–31） 5．月份（1–12或JAN–DEC） 6．星期中的日期（1–7或SUN–SAT） 7．年份（1970–2099）

 * 每一个元素都可以显式地规定一个值（如6），一个区间（如9-12），
 一个列表（如9，11，13）或一个通配符（如*）。“月份中的日期”和“星期中的日期”
 这两个元素是互斥的，因此应该通过设置一个问号（？）来表明你不想设置的那个字段。

 *是一个通配符，表示任何值，用在Minutes字段中表示每分钟。
 ?只可以用在day-of-month或者Day-of-Week字段中，用来表示不指定特殊的值。
 -用来表示一个范围，比如10-12用在Month中表示10到12月。
 ,用来表示附加的值，比如MON,WED,FRI在day-of-week字段中表示礼拜一和礼拜三和礼拜五。
 /用来表示增量，比如0/15用在Minutes字段中表示从0分开始0和15和30和45分。
 L只可以用在day-of-month或者Day-of-Week字段中，如果用在Day-of-month中，表示某个月
 的最后一天，1月则是表示31号，2月则表示28号（非闰年），如果用在Day-of-Week中表示礼
 拜六（数字7）；但是如果L与数字组合在一起用在Day-of-month中，比如6L，则表示某个月
 的最后一个礼拜六；
 * 
 */

public class InitJob extends HttpServlet {
	
	private IQuartzManagerService quartzManagerService;
	private AlarmManagementMapper alarmManagementMapper;
	private PerformanceManagerMapper performanceManagerMapper;
	private CircuitManagerMapper circuitManagerMapper;
	private ConnectionManagerMapper connectionManagerMapper;
	private NetworkManagerMapper networkManagerMapper;
	private ResourceSystemCorrelationMapper resourceCorrelationMapper;
	
	public void init() throws ServletException {

		quartzManagerService = (IQuartzManagerService) SpringContextUtil.getBean("quartzManagerService");
		alarmManagementMapper = (AlarmManagementMapper) SpringContextUtil.getBean("alarmManagementMapper");
		performanceManagerMapper = (PerformanceManagerMapper) SpringContextUtil.getBean("performanceManagerMapper");
		circuitManagerMapper = (CircuitManagerMapper) SpringContextUtil.getBean("circuitManagerMapper");
		connectionManagerMapper = (ConnectionManagerMapper) SpringContextUtil.getBean("connectionManagerMapper");
		networkManagerMapper = (NetworkManagerMapper) SpringContextUtil.getBean("networkManagerMapper");
		resourceCorrelationMapper = (ResourceSystemCorrelationMapper) SpringContextUtil.getBean("resourceSystemCorrelationMapper");
		//初始化trigger错时触发策略
		intiAllTriggerMisfireInstruction();
		// 每日收集link相关性能数据，保存至t_base_link_pm表中
		initSyncLinkPmJob();
		// 每天都检查报表任务所含网管的采集结束时间并且据此修改任务定时
//		initPmReportModifyJob();
		//webService发送心跳--暂不启用
//		Thread thread = new Thread(new HeartBeatThread());
//		thread.start();

		initPerformanceSPJob();
		
		//自动确认job初始化
		initAutoConfirmJob();
		//自动告警转移job初始化
		initAutoShiftJob();
		
		//网元分析的定时任务
		initAutoNetAnalyJob();
		//定时检查接入服务器状态job初始化
		checkserverstatus();
		
		// 修改quartz任务端点重启修复状态
		updateCirstate();
		
		//自动同步job初始化
		initAutoAlarmSynch();
		
		// 增量更新job初始化
		initAddNotificationJob();
		
		//生成告警报表数据到mysql数据库
		generateAlarmDataFromMongodbJob();
		
		//删除性能临时表数据
		deletePmTempTableDate();
		
		//初始化给地图模块创建的临时表
		initTempTableForGis();
		//运行kettle job，可用率统计
		startKettle_availability();
		//自动生成稽核数据CSV初始化
		initAutoCreateResourceJob();
		//ptn峰值报表数据采集定时任务
		initPtnReportDataSourceJob();
		//资源关联定时任务
		ResourceCorrelationJob();
	
	}
	
//	public class Inner extends TimerTask { 
//		@Override
//		public void run() {
////			System.out.println("发送消息！");
//			Map message = new HashMap();
//			JMSSender.sendMessage(10, message);
//		} 
//    }
	
	private void initTempTableForGis() {
		
		int count = alarmManagementMapper.isTempTableExistForGis(
					"t_temp_alarm_gis", SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
		if(count > 0) {
			//删除临时表
			alarmManagementMapper.dropGisTempTable();
		}
		
		//创建临时表
		alarmManagementMapper.createGisTempTable();
	}

	// 初始化自动采集任务，并连接corba接口
	private void initSyncLinkPmJob() {

		boolean syncLinkPmJob = false;
		// 获取配置
		if (CommonUtil.getSystemConfigProperty("SyncLinkPmJob") != null) {
			syncLinkPmJob = Boolean.parseBoolean(CommonUtil
					.getSystemConfigProperty("SyncLinkPmJob"));
		}
		try {
			if (syncLinkPmJob) {
				// 每天凌晨6点执行
				String cronExpression = "0 0 6 * * ?";
				// 特殊任务类型，无需管理界面 ID设为 -10
				if (!quartzManagerService.IsJobExist(
						CommonDefine.QUARTZ.JOB_SYNC_LINK_PM, null)) {
					quartzManagerService.addJob(
							CommonDefine.QUARTZ.JOB_SYNC_LINK_PM, null,
							SyncLinkPmJob.class, cronExpression);
				}
			} else {
				// 删除任务
				if (quartzManagerService.IsJobExist(
						CommonDefine.QUARTZ.JOB_SYNC_LINK_PM, null)) {
					quartzManagerService.ctrlJob(
							CommonDefine.QUARTZ.JOB_SYNC_LINK_PM, null,
							CommonDefine.QUARTZ.JOB_DELETE);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	
	// 初始化性能报表job
	private void initPerformanceSPJob() {
       boolean isExecuteJob = false;
	   // 获取配置
	   if (CommonUtil.getSystemConfigProperty("PerformanceSPJob") != null) {
		  isExecuteJob = Boolean.parseBoolean(CommonUtil.getSystemConfigProperty("PerformanceSPJob"));
	   }
	   try {
		if (isExecuteJob) {
			Map param=new HashMap();
			//param.put("day","2014-04-04");//不设值默认传递当天日期
			String cronExpression = "0 0 7 * * ?";// 每天凌晨7点执行
			// 特殊任务类型，无需管理界面 ID设为 -10
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_PERFORMANCE_SP,null)) {
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_PERFORMANCE_SP,null,CommonDefine.QUARTZ.JOB_PAUSE);
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_PERFORMANCE_SP, null, CommonDefine.QUARTZ.JOB_DELETE);
			}
			quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_PERFORMANCE_SP,null,PerformanceDataConvergeJob.class,cronExpression,param);
		} else {
			// 删除任务
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_PERFORMANCE_SP,null)){
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_PERFORMANCE_SP,null,CommonDefine.QUARTZ.JOB_DELETE);
			}
		}
	   } catch (Exception e) {
			ExceptionHandler.handleException(e);
	   }
	}

	//初始化自动确认job
	private void initAutoConfirmJob() {
			// 告警自动确认设置
			Map<String, Object> paramMap = alarmManagementMapper.getSystemParam(CommonDefine.ALARM_CONFIRM_PARAM_KEY);
			if(paramMap==null){
				return;
			}
			int minute = Integer.parseInt(paramMap.get("PARAM_VALUE").toString().split(",")[1]);
			String bool=paramMap.get("PARAM_VALUE").toString().split(",")[0];
			//quartz
			try {
				String cron1 ="0 0 0/"+minute+" * * ?";
				//删除job
				if(quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null)){
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null,CommonDefine.QUARTZ.JOB_DELETE);
				}
				//添加job
				quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null, AlarmAutoConfirmJob.class, cron1);
				
				if(bool.equals("false")){
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null,CommonDefine.QUARTZ.JOB_PAUSE);
				}else{
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null,CommonDefine.QUARTZ.JOB_ACTIVATE);
				}
			 } catch (Exception e) {
					ExceptionHandler.handleException(e);
			 }
		
	}
	
	//初始化告警自动转移job
	private void initAutoShiftJob(){
		// 告警自动转移设置
		Map<String, Object> paramMap = alarmManagementMapper.getSystemParam(CommonDefine.ALARM_SHIFT_PARAM_KEY);
		if(paramMap==null){
			return;
		}
		int minute = Integer.parseInt(paramMap.get("PARAM_VALUE").toString().split(",")[1]);
		String bool=paramMap.get("PARAM_VALUE").toString().split(",")[0];
		//quartz
		try {
			String cron2 ="0 0 0/"+minute+" * * ?";
			//删除job
			if(quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ZHUANYI,null)){
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZHUANYI,null,CommonDefine.QUARTZ.JOB_DELETE);
			}
			//添加job
			quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ZHUANYI,null, AlarmAutoShiftJob.class, cron2);
			if(bool.equals("false")){
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZHUANYI,null,CommonDefine.QUARTZ.JOB_PAUSE);
			}else{
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZHUANYI,null,CommonDefine.QUARTZ.JOB_ACTIVATE);
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
	 }
	}
	
	//自动同步初始化
	private void initAutoAlarmSynch() {
		List<Map<String, Object>> list =alarmManagementMapper.getAlarmAutoSynchExist();
		try {
			for (Map<String, Object> map : list) {
				String cron2 ="0 0 0/"+map.get("SYNCHRONIZATION_CIRCLE").toString()+" * * ?";
				if(quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ALARMSYNCH,Integer.valueOf(map.get("ID").toString()))){
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							Integer.valueOf(map.get("ID").toString()),
							CommonDefine.QUARTZ.JOB_PAUSE);
					//修改quartz时间
					quartzManagerService.modifyJobTime(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							Integer.valueOf(map.get("ID").toString()),
							cron2);
				}else{
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							Integer.valueOf(map.get("ID").toString()),
							AlarmSyncJob.class, cron2, map);
				}
				if(map.get("TASK_STATUS").toString().equals("1")){//启用
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							Integer.valueOf(map.get("ID").toString()),
							CommonDefine.QUARTZ.JOB_ACTIVATE);
				}else{//挂起
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							Integer.valueOf(map.get("ID").toString()),
							CommonDefine.QUARTZ.JOB_PAUSE);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	//网元分析
	private void initAutoNetAnalyJob() {
		
	   boolean isExecuteJob = false;
	    //判断是否需要执行任务
	   isExecuteJob=true;
	   try {
		if (isExecuteJob) { 
			String cronExpression = "0 0 22 * * ?";
			// 特殊任务类型，无需管理界面 ID设为 -10
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_NE_EARLY_ALARM,null)) {
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NE_EARLY_ALARM,null,CommonDefine.QUARTZ.JOB_PAUSE);
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NE_EARLY_ALARM, null, CommonDefine.QUARTZ.JOB_DELETE);
			}
			quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_NE_EARLY_ALARM,null,NeEarlyAlarmJob.class,cronExpression); 
		} else {
			// 删除任务
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_NE_EARLY_ALARM,null)){
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NE_EARLY_ALARM,null,CommonDefine.QUARTZ.JOB_DELETE);
			}
		}
	   } catch (Exception e) {
			ExceptionHandler.handleException(e);
	   } 
	}
	
	// 初始化增量更新job
	private void initAddNotificationJob(){
		 boolean isExecuteJob = false;
		   // 获取配置
		   if (CommonUtil.getSystemConfigProperty("AddNotificationJob") != null) {
			  isExecuteJob = Boolean.parseBoolean(CommonUtil.getSystemConfigProperty("AddNotificationJob"));
		   }
		   try {
			if (isExecuteJob) {
				Map param=new HashMap();
				//param.put("day","2014-04-04");//不设值默认传递当天日期
				String cronExpression = "0 0 0/2 * * ?";// 每天凌晨7点执行
				// 特殊任务类型，无需管理界面 ID设为 -10
				if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_NOTIFICATION,null)) {
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NOTIFICATION,null,CommonDefine.QUARTZ.JOB_PAUSE);
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NOTIFICATION, null, CommonDefine.QUARTZ.JOB_DELETE);
				}
				quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_NOTIFICATION,null,NotificationJob.class,cronExpression,param);
			} else {
				// 删除任务
				if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_NOTIFICATION,null)){
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NOTIFICATION,null,CommonDefine.QUARTZ.JOB_DELETE);
				}
			}
		   } catch (Exception e) {
				ExceptionHandler.handleException(e);
		   }
	}
	
	
	//生成告警报表数据到mysql数据库
	private void generateAlarmDataFromMongodbJob() {
       boolean isExecuteJob = false;
	   // 获取配置
	   if (CommonUtil.getSystemConfigProperty("PerformanceSPJob") != null) {
		  isExecuteJob = Boolean.parseBoolean(CommonUtil.getSystemConfigProperty("PerformanceSPJob"));
	   }
	   try {
		if (isExecuteJob) {
			Map param=new HashMap();
			//param.put("day","2014-01-21");//不设值默认传递昨天日期
			String cronExpression = "0 0 6 * * ?";// 每天凌晨6点执行
			//String cronExpression = "0 14 10 * * ?";// 每天凌晨6点执行
			// 特殊任务类型，无需管理界面 ID设为 -10
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ALARM_GENERATE,null)) {
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARM_GENERATE,null,CommonDefine.QUARTZ.JOB_PAUSE);
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARM_GENERATE, null, CommonDefine.QUARTZ.JOB_DELETE);
			}
			quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ALARM_GENERATE,null,AlarmDataConvergeJob.class,cronExpression,param);
			//告警数据报表任务存在bug,会导致内存溢出，置为暂停状态
//			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARM_GENERATE,null,CommonDefine.QUARTZ.JOB_PAUSE);
		} else {
			// 删除任务
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ALARM_GENERATE,null)){
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARM_GENERATE,null,CommonDefine.QUARTZ.JOB_DELETE);
			}
		}
	   } catch (Exception e) {
			ExceptionHandler.handleException(e);
	   }
	}
	
	//删除性能临时表数据
	private void deletePmTempTableDate(){
		performanceManagerMapper.deleteTempPmForInit(CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA);
		performanceManagerMapper.deleteTempPmForInit(CommonDefine.PM.PM_TABLE_NAMES.CURRENT_WDM_DATA);
		performanceManagerMapper.deleteTempPmForInit(CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA);
		performanceManagerMapper.deleteTempPmForInit(CommonDefine.PM.PM_TABLE_NAMES.HISTORY_WDM_DATA);
	}
	
	private void updateCirstate(){
		Map update = new HashMap();
		update.put("NAME", "t_sys_task");
		update.put("ID_NAME", "TASK_TYPE");
		update.put("ID_VALUE", CommonDefine.QUARTZ.JOB_CIRCUIT);
		update.put("ID_NAME_", "RESULT");
		update.put("ID_VALUE_", CommonDefine.EMS_SYNC_ING);
		update.put("ID_NAME_2", "RESULT");
		update.put("ID_VALUE_2", CommonDefine.EMS_SYNC_FAILED);
		circuitManagerMapper.updateByParameter(update);
	}
	
	//初始化所有的trigger错过触发时间对策---MISFIRE_INSTRUCTION_DO_NOTHING
	/*	CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;
		CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
		CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY*/
	private void intiAllTriggerMisfireInstruction() {

		try {
			List<Trigger> allTriggers = quartzManagerService.getAllTrigger();
			for (Trigger trigger : allTriggers) {
				if (CronTrigger.class.isInstance(trigger)) {
					CronTrigger xxx = (CronTrigger) trigger;
					if(xxx.getMisfireInstruction()!=CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING){
						xxx.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
						quartzManagerService.rescheduleJob(trigger.getName(),
								trigger.getGroup(), trigger,null);
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	private void checkserverstatus() {  
			Timer timer = new Timer();
			TimerTask task = new CheckServerStatus();             
			timer.schedule(task, 0, 5*60*1000); //5分钟      
		}
	
	private void startKettle_availability(){
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {

					@Override
					public String call() throws Exception {
		Map param = SpringContextUtil.getDataBaseParam();
		//jod路径
		String path = CommonDefine.PATH_ROOT +"WEB-INF/classes/kettle/availability.kjb";
		//运行kettle job
		runJob(param,path);
						
						return "startKettle_availability Completed";
					}
				});
		new Thread(task).start();
	}
	
	
	
/*	private void runTransfer(Map params, String ktrPath) {
		Trans trans = null;
		try {
			// // 初始化
			// 转换元对象
			KettleEnvironment.init();// 初始化
			EnvUtil.environmentInit();
			TransMeta transMeta = new TransMeta(ktrPath);
			// 转换
			trans = new Trans(transMeta);
			trans.setVariable("host", params.get(CommonDefine.DB_HOST).toString());
			trans.setVariable("sid", params.get(CommonDefine.DB_SID).toString());
			trans.setVariable("port", params.get(CommonDefine.DB_PORT).toString());
			trans.setVariable("username", params.get(CommonDefine.DB_USERNAME).toString());
			trans.setVariable("password", params.get(CommonDefine.DB_PASSWORD).toString());
			// 执行转换
			trans.execute(new String[]{});
			// 等待转换执行结束
			trans.waitUntilFinished();
			// 抛出异常
			if (trans.getErrors() > 0) {
				throw new Exception(
						"There are errors during transformation exception!(传输过程中发生异常)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * java 调用 kettle 的job
	 * 
	 * @param jobname
	 *            如： String fName= "D:\\kettle\\informix_to_am_4.ktr";
	 */
	private void runJob(Map params, String jobPath) {
		try {
			KettleEnvironment.init();
			// jobname 是Job脚本的路径及名称
			JobMeta jobMeta = new JobMeta(jobPath, null);

			Job job = new Job(null, jobMeta);
			// 向Job 脚本传递参数，脚本中获取参数值：${参数名}
			job.setVariable("host", params.get(CommonDefine.DB_HOST).toString());
			job.setVariable("sid", params.get(CommonDefine.DB_SID).toString());
			job.setVariable("port", params.get(CommonDefine.DB_PORT).toString());
			job.setVariable("username", params.get(CommonDefine.DB_USERNAME).toString());
			job.setVariable("password", params.get(CommonDefine.DB_PASSWORD).toString());
			job.start();
			job.waitUntilFinished();
			if (job.getErrors() > 0) {
				throw new Exception(
						"There are errors during job exception!(执行job发生异常)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 每天规定时间自动生成资源稽核数据CSV文件
	 */
	private void initAutoCreateResourceJob(){
		try {
    		// 资源稽核数据生成设置
    		Map<String, Object> paramMap = alarmManagementMapper.getSystemParam(CommonDefine.RES_AUDIT_PARAMETER_KEY);
    		if(paramMap==null){
    			return;
    		}
    		boolean isExecuteJob = Boolean.parseBoolean(paramMap.get("PARAM_VALUE").toString().split(",")[0]);
    		int time = Integer.parseInt(paramMap.get("PARAM_VALUE").toString().split(",")[1]);
    		int hour = time/60;
    		int minute = time%60;

			if (isExecuteJob) { 
                String cronExpression = "0 " + minute + " " + hour + " * * ?" ;
                // 特殊任务类型，无需管理界面 ID设为 -10
                if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ATUO_CREATE_RESOURCE_CSV,null)) {
                	quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ATUO_CREATE_RESOURCE_CSV, 
                			null, CommonDefine.QUARTZ.JOB_PAUSE);
                	quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ATUO_CREATE_RESOURCE_CSV,
                			null, CommonDefine.QUARTZ.JOB_DELETE);
                }
                quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ATUO_CREATE_RESOURCE_CSV,
                		null, AutoCreateResourceJob.class, cronExpression); 
            } else {
            	if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ATUO_CREATE_RESOURCE_CSV,null)) {
            		quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ATUO_CREATE_RESOURCE_CSV, 
                			null, CommonDefine.QUARTZ.JOB_PAUSE);
            	}
            }
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
	   } 
	}

	//ptn峰值报表数据采集定时任务
	private void initPtnReportDataSourceJob() {
		
	   boolean isExecuteJob = false;
	    //判断是否需要执行任务
	   isExecuteJob=true;
	   try {
		if (isExecuteJob) { 
			String cronExpression = "0 0 6 * * ?";// 每天凌晨6点执行
			// 特殊任务类型，无需管理界面 ID设为 -10
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_DATA_SOURCE,null)) {
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_DATA_SOURCE,null,CommonDefine.QUARTZ.JOB_PAUSE);
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_DATA_SOURCE, null, CommonDefine.QUARTZ.JOB_DELETE);
			}
			quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_DATA_SOURCE,null,PtnReportDataSourceJob.class,cronExpression); 
		} else {
			// 删除任务
			if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_DATA_SOURCE,null)){
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_DATA_SOURCE,null,CommonDefine.QUARTZ.JOB_DELETE);
			}
		}
	   } catch (Exception e) {
			ExceptionHandler.handleException(e);
	   } 
	}

	/**
	 * 每天规定时间资源系统数据同步关联
	 */
	private void ResourceCorrelationJob(){
		List<Map<String, Object>> list =resourceCorrelationMapper.getResourceCorrelationTaskList();
		try {
			for (Map<String, Object> map : list) {
				int period = (Integer) map.get("PERIOD");
				int hour = period / 60;
				int minute = period % 60;
				StringBuilder sb = new StringBuilder();
				sb.append("0 ").append(String.valueOf(minute)).append(" ");
				sb.append(String.valueOf(hour)).append(" * * ?");
				String cron =sb.toString();
				Integer taskId = (Integer) map.get("RC_TASK_ID");
				
				if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION, taskId)) {
					
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
							taskId,	CommonDefine.QUARTZ.JOB_PAUSE);
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
							taskId,	CommonDefine.QUARTZ.JOB_DELETE);
				}
				quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
						taskId, ResourceCorrelationJob.class, cron, map);
				
				if (map.get("TASK_STATUS").toString().equals("1")) {//启用
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
							taskId, CommonDefine.QUARTZ.JOB_RESUME);
				} else {//挂起
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION,
							taskId, CommonDefine.QUARTZ.JOB_PAUSE);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
}
