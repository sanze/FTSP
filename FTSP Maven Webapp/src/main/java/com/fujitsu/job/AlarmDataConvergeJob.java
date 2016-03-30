package com.fujitsu.job;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IReportManagerService;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;
public class AlarmDataConvergeJob implements Job {
private IAlarmManagementService alarmManagementService;
private IReportManagerService reportManagerService;
	public AlarmDataConvergeJob() {
		alarmManagementService =(IAlarmManagementService) SpringContextUtil.getBean("alarmManagementServiceImpl");
		reportManagerService =(IReportManagerService) SpringContextUtil.getBean("reportManagerServiceImpl");
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map param=context.getJobDetail().getJobDataMap();
		Object o=param.get("day");
		if(o==null || "".equals((String)o)){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal=Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_YEAR,-1);
			sdf.format(cal.getTime());
			param.put("day",sdf.format(cal.getTime()));
		}
		
//		for(int i=1;i<=31;i++){
//			String day="2014-01-";
//			if(i<10){
//				day=day+"0"+i;
//			}else{
//				day=day+i;
//			}
//			param.put("day",day);
//			System.out.println("AlarmDataConvergeJob:"+param.get("day").toString());
//			Map returnParam;
//			try {
//				returnParam = faultManagerService.generateAlarmFromMonodb(param);
//				List<Map> datas=(List<Map>)returnParam.get("rows");
//				reportManagerService.insertAlarmDataFromMonodb(datas);
//			} catch (CommonException e) {
//				e.printStackTrace();
//			}
//		}
		
		try {
			//Map returnParam=faultManagerService.generateAlarmFromMonodb(param);
			List<Map> datas=alarmManagementService.generateAlarmFromMonodb(param);
			reportManagerService.insertAlarmDataFromMonodb(datas);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
		
}