package com.fujitsu.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IImptProtectManagerService;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.ImptProtectManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;
public class ImptProtectJob implements Job {

	private IImptProtectManagerService imptProtectManagerService;
	private IQuartzManagerService quartzManagerService;
	
	private ImptProtectManagerMapper imptProtectManagerMapper;
	
	public ImptProtectJob(){
		imptProtectManagerService = (IImptProtectManagerService) SpringContextUtil
				.getBean("imptProtectManagerServiceImpl");
		quartzManagerService = (IQuartzManagerService) SpringContextUtil
				.getBean("quartzManagerService");
		imptProtectManagerMapper = (ImptProtectManagerMapper) SpringContextUtil
				.getBean("imptProtectManagerMapper");
	}
	
	@Override
	public void execute(JobExecutionContext context)throws JobExecutionException {
		try{
			Integer taskId = context.getJobDetail().getJobDataMap().getIntValue("taskId");
			int taskType=CommonDefine.QUARTZ.JOB_IMPT_PROTECT;
			
			int userId=CommonDefine.USER_ADMIN_ID;
			Map<String, Object> param=new HashMap<String, Object>();
			param.put("SYS_TASK_ID", taskId);
			param.put("TASK_TYPE", taskType);
			Map<String, Object> task=imptProtectManagerService.getTask(param, userId);
//			int taskStatus=CommonDefine.IMPT_PROTECT.TASK_STATUS.WAITTING;
			Date endTime=(Date)task.get("END_TIME");
			
			/*if(task.get("TASK_STATUS")!=null){
				taskStatus=Integer.valueOf(task.get("TASK_STATUS")+"");
			}*/
			if(endTime!=null&&endTime.getTime()<=new Date().getTime()){
				param.put("TASK_STATUS", CommonDefine.IMPT_PROTECT.TASK_STATUS.COMPLETED);
				imptProtectManagerMapper.changeTaskStatus(param);
			}else{
				param.put("TASK_STATUS", CommonDefine.IMPT_PROTECT.TASK_STATUS.RUNNING);
				// 任务状态： 更新任务状态为执行中
				imptProtectManagerMapper.changeTaskStatus(param);
			    // 设置结束任务
				if(endTime!=null&&endTime.getTime()>new Date().getTime()){
					SimpleDateFormat cronFormat=new SimpleDateFormat("s m H d M ? yyyy");
					String cronExpression=cronFormat.format(endTime);
					//System.out.println(cronExpression);
					quartzManagerService.modifyJobTime(taskType, taskId, cronExpression);
				}
			}
		}catch(Exception e){
			//taskResult = CommonDefine.QUARTZ.UNCOMPLETED;
			ExceptionHandler.handleException(e);
		}
	}
}
