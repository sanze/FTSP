package com.fujitsu.job;

import java.util.Date;
import java.util.Map;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.job.JobMeta;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IResourceAuditService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

public class AutoCreateResourceJob implements Job {

	//private IResourceAuditService resourceAuditService;
	
	public AutoCreateResourceJob() {
		//resourceAuditService =(IResourceAuditService) SpringContextUtil.getBean("resourceAuditServiceImpl");
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			/* 改用ETL工具生成资源稽核数据
			//网元
			resourceAuditService.requestEmsData(null, null, 1, null);
			//子架
			resourceAuditService.requestEmsData(null, null, 2, null);
			//板卡
			resourceAuditService.requestEmsData(null, null, 3, null);
			//端口
			resourceAuditService.requestEmsData(null, null, 4, null);
			//SDH交叉连接
			resourceAuditService.requestEmsData(null, null, 5, null);
			//OTN交叉连接
			resourceAuditService.requestEmsData(null, null, 6, null);
			*/
			Map param = SpringContextUtil.getDataBaseParam();
			String ftpIp = CommonUtil.getSystemConfigProperty("ftpIp");
			String ftpPort = CommonUtil.getSystemConfigProperty("ftpPort");
			String ftpUser = CommonUtil.getSystemConfigProperty("ftpUserName");
			String ftpPassword = CommonUtil.getSystemConfigProperty("ftpPassword");
			String ftpDirectory = CommonUtil.getSystemConfigProperty("ftpDirForResourceAudit");

			StringBuilder sb = new StringBuilder();
			sb.append(CommonDefine.PATH_ROOT).append("WEB-INF/classes/kettle/resourceCorrelation/Resource Audit.kjb");
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
			job.start();
			job.waitUntilFinished();
			
			if (job.getErrors() > 0) {
				throw new Exception(
						"There are errors during job exception!(执行job发生异常)");
			} else {
				System.out.println("Resource audit data files generated successfully!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}