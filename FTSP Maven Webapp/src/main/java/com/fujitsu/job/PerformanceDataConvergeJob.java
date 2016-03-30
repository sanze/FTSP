package com.fujitsu.job;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.reportManager.service.ReportManagerService;
import com.fujitsu.util.SpringContextUtil;
public class PerformanceDataConvergeJob implements Job {
	private ReportManagerService reportManagerService;
	public PerformanceDataConvergeJob() {
		reportManagerService =(ReportManagerService) SpringContextUtil.getBean("reportManagerServiceImpl");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map param=context.getJobDetail().getJobDataMap();
		Object o=param.get("day");
		if(o==null || "".equals((String)o)){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal=Calendar.getInstance();
			cal.setTime(new Date());
			//cal.add(Calendar.DAY_OF_YEAR,-1);
			sdf.format(cal.getTime());
			param.put("day",sdf.format(cal.getTime()));
		}
		try {
			Map returnParam=reportManagerService.callPerformaceSP(param);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		
		
	}
		
}