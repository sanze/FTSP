package com.fujitsu.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IMultipleSectionManagerService;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;

/**
 * @author TianHongjun
 * 
 */
public class PmReportJob implements Job {
	private IPerformanceManagerService performanceManagerService;
	private IMultipleSectionManagerService multipleSectionManagerService;
	private PerformanceManagerMapper performanceManagerMapper;

	public PmReportJob() {
		// System.out.println("Creating bean~~");
		performanceManagerService = (IPerformanceManagerService) SpringContextUtil
				.getBean("performanceManagerServiceImpl");
		multipleSectionManagerService = (IMultipleSectionManagerService) SpringContextUtil
				.getBean("multipleSectionManagerServiceImpl");
		performanceManagerMapper = (PerformanceManagerMapper) SpringContextUtil
				.getBean("performanceManagerMapper");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
//		if(1>0)
//			throw new JobExecutionException();
		// Pm报告
		// 首先根据context内的数据判断导出类型
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap jobMap = jobDetail.getJobDataMap();

		int jobType = 0;
		// 导出查询所用的参数
		String jobName = jobDetail.getName();
		String typeStr = jobName.substring(0, (jobName.indexOf("_")));
		// 创建映射，方便查询ID
		Map<String, Integer> map = new HashMap<String, Integer>();
		String jobNeStr = CommonDefine.QUARTZ.TYPE_NAMES[CommonDefine.QUARTZ.JOB_REPORT_NE];
		String jobMsStr = CommonDefine.QUARTZ.TYPE_NAMES[CommonDefine.QUARTZ.JOB_REPORT_MS];
		map.put(jobNeStr, CommonDefine.QUARTZ.JOB_REPORT_NE);
		map.put(jobMsStr, CommonDefine.QUARTZ.JOB_REPORT_MS);
		// 查询ID
		jobType = map.get(typeStr);
		// 获取taskId
		String taskId = jobName.substring((jobName.indexOf("_") + 1));
		// 判断是日报还是月报
		boolean isMonthlyReport = false;
		isMonthlyReport = (jobMap.getBooleanValue("IS_MONTHLY_REPORT"));
//		if(!needExcute(taskId,isMonthlyReport))
//			return;
		
		//存下任务开始时间
		Map runStatus = new HashMap();
		runStatus.put("taskId", taskId);
		runStatus.put("flag", "start");
		performanceManagerMapper.taskStatusUpdate(runStatus);
		// 拼装查询参数
		Map<String, String> searchCond = new HashMap<String, String>();
		searchCond.put("taskId", taskId);
		
		// 然后根据导出类型决定执行的内容
		try {
			switch (jobType) {
			case CommonDefine.QUARTZ.JOB_REPORT_NE:
				// System.out.println("Processing Ne Report @ " + jobType +
				// " - " + taskId);
				if(checkDeviceDomain(searchCond)){
					// 进行了检查，可以正常执行
					if (isMonthlyReport) {
						performanceManagerService
								.searchPMForReportNeMonthly(searchCond);
					} else {
						performanceManagerService
								.searchPMForReportNeDaily(searchCond);
					}
				}else{
					// 没有对象了，执行失败
					runStatus.put("flag", "end");
					runStatus.put("result", 2);
					performanceManagerMapper.taskStatusUpdate(runStatus);
					return;
				}
				break;
			case CommonDefine.QUARTZ.JOB_REPORT_MS:
				// System.out.println("Processing MultiSec Report @ " + jobType
				// + " - " + taskId);
				multipleSectionManagerService.exportMsPmReport(
						Integer.parseInt(taskId), false);
				break;
			}
			//哟，成功了嘛
			runStatus.put("flag", "end");
			runStatus.put("result", 1);
			performanceManagerMapper.taskStatusUpdate(runStatus);
		} catch (Exception e) {
			//这里嘛任务肯定是失败了
			runStatus.put("flag", "end");
			runStatus.put("result", 2);
			performanceManagerMapper.taskStatusUpdate(runStatus);
			ExceptionHandler.handleException(e);
		}
	}

	private boolean needExcute(String taskId,boolean isMonthly){
		Date updateTime = performanceManagerMapper.getTaskUpdateTime(Integer.parseInt(taskId));
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat();
		if(isMonthly){
			sdf.applyPattern("yyyy-MM");
		}else{
			sdf.applyPattern("yyyy-MM-dd");
		}
		if(sdf.format(updateTime).equals(sdf.format(today)))
			return false;
		else
			return true;
	}
	
	public boolean checkDeviceDomain(Map<String, String> map){
		//对象权限检查
		Integer userId = performanceManagerMapper.getCreatorByTaskId(map);
		List<Map> outOfDomain = performanceManagerMapper.findNotInUserDomain(userId,map,CommonDefine.TREE.TREE_DEFINE); 
		if(outOfDomain!=null && outOfDomain.size()>0){
			performanceManagerMapper.deleteNodesOutOfDomain(map, outOfDomain);
		}
		Integer count = performanceManagerMapper.checkNodeCount(map);
		if(count==0)
			return false;
		else
			return true;
	}
}
