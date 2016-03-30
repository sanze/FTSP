package com.fujitsu.job;

import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.ICutoverManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;

public class CutoverfilterAlarmJob implements Job {


	private ICutoverManagerService cutoverManagerService;

	public CutoverfilterAlarmJob() {
		cutoverManagerService = (ICutoverManagerService) SpringContextUtil
				.getBean("cutoverManagerServiceImpl");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		// 获取任务id
		String[] jobName = context.getJobDetail().getName().split("_");
		System.out.println(context.getJobDetail().getName());
		//因为一条割接任务需要创建三个不同时间的定时任务，不能用割接任务id
		//直接同时作为三个定时任务的id，所以建立规则：创建定时任务时，
		//将割接任务id取相反数：-cutoverTaskId*100,
		//割接前快照的id为-cutoverTaskId*100-1；
		//过滤告警的id为：-cutoverTask*100-2;
		//割接完成的id为-cutoverTask*100-3；
		//该id为上述规则转换之后的id
		int cutoverTaskIdTransferd = Integer.parseInt(jobName[1]);
		Integer cutoverTaskIdActual = -(cutoverTaskIdTransferd+2)/100;
		int userId = (Integer)context.getJobDetail().getJobDataMap().get("userId");
		
		try {
			List  paramList = cutoverManagerService.getCutoverTaskParameter(cutoverTaskIdActual);
			boolean taskExecuted = false;
			for(int i = 0,len = paramList.size();i<len;i++)
			{
				Map param = (Map)paramList.get(i);
				if (((String) param.get("PARAM_NAME"))
						.equals(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.FILTER_ALARM_FLAG)) {
					taskExecuted = true;
				}
			}
			if(!taskExecuted)
			{
				cutoverManagerService.filterAlarm(cutoverTaskIdActual.toString(), userId);
			}
			
			
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
}
