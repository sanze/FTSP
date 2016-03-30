package com.fujitsu.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.job.JobMeta;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.ResourceSystemCorrelationMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

public class ResourceCorrelationJob implements Job {
	private ResourceSystemCorrelationMapper resourceCorrelationMapper;
	private IQuartzManagerService quartzManagerService;
	private CommonManagerMapper commonManagerMapper;
	
	public ResourceCorrelationJob() {
		resourceCorrelationMapper = (ResourceSystemCorrelationMapper) SpringContextUtil.getBean(
				"resourceSystemCorrelationMapper");
		quartzManagerService = (IQuartzManagerService) SpringContextUtil.getBean(
				"quartzManagerService");
		commonManagerMapper = (CommonManagerMapper) SpringContextUtil.getBean(
				"commonManagerMapper");
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<String,Object> status = new HashMap<String,Object>();
		Integer taskId =(Integer) context.getJobDetail().getJobDataMap().get("RC_TASK_ID");
		String jobName = context.getJobDetail().getJobDataMap().get("KETTLE_JOB_NAME").toString();		
		try {
			Map param = SpringContextUtil.getDataBaseParam();
			String ftpIp = CommonUtil.getSystemConfigProperty("ftpIp");
			String ftpPort = CommonUtil.getSystemConfigProperty("ftpPort");
			String ftpUser = CommonUtil.getSystemConfigProperty("ftpUserName");
			String ftpPassword = CommonUtil.getSystemConfigProperty("ftpPassword");
			String ftpDirectory = CommonUtil.getSystemConfigProperty("ftpDirForResorceCorrelation");
			String fileOutputFlag = "";
			Map<String, Object> paramMap = commonManagerMapper.selectSysParam(CommonDefine.RES_FILE_OUTPUT_KEY);
			if (paramMap != null &&
					paramMap.get("PARAM_VALUE").toString().matches("^[0-9]{4}[0-9,a-f,A-F]{3}")){
				fileOutputFlag = paramMap.get("PARAM_VALUE").toString();
			}
			StringBuilder sb = new StringBuilder();
			sb.append(CommonDefine.PATH_ROOT).append("WEB-INF/classes/kettle/resourceCorrelation/").append(jobName);
			//job路径
			String path = sb.toString();
			KettleEnvironment.init();
			// jobname 是Job脚本的路径及名称
			JobMeta jobMeta = new JobMeta(path, null);

			org.pentaho.di.job.Job job = new org.pentaho.di.job.Job(null, jobMeta);
			// 向Job 脚本传递参数，脚本中获取参数值：${参数名}
			job.setVariable("host", param.get(CommonDefine.DB_HOST).toString());
			job.setVariable("sid", param.get(CommonDefine.DB_SID).toString());
			job.setVariable("port", param.get(CommonDefine.DB_PORT).toString());
			job.setVariable("username", param.get(CommonDefine.DB_USERNAME).toString());
			job.setVariable("password", param.get(CommonDefine.DB_PASSWORD).toString());
			job.setVariable("ftpIp", ftpIp);
			job.setVariable("ftpPort", ftpPort);
			job.setVariable("ftpUsername", ftpUser);
			job.setVariable("ftpPassword", ftpPassword);
			job.setVariable("ftpDir", ftpDirectory);
			job.setVariable("resourceFileOutputFlag", fileOutputFlag);
			job.start();
			job.waitUntilFinished();
			
			Map<String,Object> quartzJob = quartzManagerService.getJobInfo(
					CommonDefine.QUARTZ.JOB_RESOURCE_CORRELATION, taskId);
			status.put("RC_TASK_ID", taskId);
			status.put("LATEST_EXECUTE_TIME", new Date());
			status.put("NEXT_EXECUTE_TIME", quartzJob.get("nextFireTime"));
			if (job.getErrors() > 0) {
				status.put("LATEST_EXECUTE_RESULT", "执行失败");
			} else {
				status.put("LATEST_EXECUTE_RESULT", "执行成功");
			}
			resourceCorrelationMapper.updateResourceCorrelationTaskStatus(status);

		} catch (Exception e) {
			status.put("RC_TASK_ID", taskId);
			status.put("LATEST_EXECUTE_TIME", new Date());
			status.put("LATEST_EXECUTE_RESULT", "执行失败");
			resourceCorrelationMapper.updateResourceCorrelationTaskStatus(status);
			ExceptionHandler.handleException(e);
		}
	}

}