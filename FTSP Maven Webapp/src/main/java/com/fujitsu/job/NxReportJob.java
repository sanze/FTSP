package com.fujitsu.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.INxReportManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.NxReportManagerMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;

/**
 * @author TianHongjun
 * 
 */
public class NxReportJob implements Job {
	private INxReportManagerService nxReportManagerService;
	private NxReportManagerMapper nxReportManagerMapper;
	private PerformanceManagerMapper performanceManagerMapper;
	public NxReportJob() {
		// System.out.println("Creating bean~~");
		
		nxReportManagerMapper = (NxReportManagerMapper) SpringContextUtil
				.getBean("nxReportManagerMapper");
		nxReportManagerService = (INxReportManagerService) SpringContextUtil
				.getBean("nxReportManagerImpl");
		performanceManagerMapper = (PerformanceManagerMapper) SpringContextUtil
				.getBean("performanceManagerMapper");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Map runStatus = new HashMap();
		try {
			// 首先根据context内的数据判断导出类型
			JobDetail jobDetail = context.getJobDetail();
			JobDataMap jobMap = jobDetail.getJobDataMap();
			// 导出查询所用的参数
			String jobName = jobDetail.getName();
			String typeStr = jobName.substring(0, (jobName.indexOf("_")));
			// 获取taskId
			String taskId = jobName.substring((jobName.indexOf("_") + 1));
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("taskId", taskId);
			//存下任务开始时间
			runStatus.put("taskId", taskId);
			runStatus.put("flag", "start");
			performanceManagerMapper.taskStatusUpdate(runStatus);
			// 获取任务信息
			List<Map<String, Object>> taskInfo = new ArrayList<Map<String, Object>>();
			taskInfo = nxReportManagerMapper.searchTaskInfoForEdit(paramMap);
			// 获取内容节点信息
			List<Map> taskNodes = new ArrayList<Map>();
			taskNodes = performanceManagerMapper.searchTaskNodesForEdit(paramMap);
			Map<String, Object> taskInfoMap = taskInfo.get(0);
			String period = taskInfoMap.get("period").toString();
			
			SimpleDateFormat sdf = new SimpleDateFormat(CommonDefine.COMMON_SIMPLE_FORMAT);
			Calendar c = Calendar.getInstance();
			if(Integer.parseInt(period) == CommonDefine.PM.PM_REPORT.PERIOD.MONTHLY){
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.DAY_OF_MONTH, -1);
				taskInfoMap.put("end", sdf.format(c.getTime()));
				c.set(Calendar.DAY_OF_MONTH, 1);
				taskInfoMap.put("start", sdf.format(c.getTime()));
			}else{
				c.add(Calendar.DAY_OF_MONTH, 0-Integer.valueOf(taskInfoMap.get("delay").toString()));
				taskInfoMap.put("start", sdf.format(c.getTime()));
				taskInfoMap.put("end", sdf.format(c.getTime()));
			}
			for(String key:taskInfoMap.keySet()){
				paramMap.put(key,taskInfoMap.get(key).toString());
			}
			// 这步似乎然并卵
			for(Map tar:taskNodes){
				tar.put("targetId", tar.get("nodeId"));
			}
			Integer creator = Integer.valueOf(taskInfoMap.get("creator").toString());
			
			List<Integer> targetIds = new ArrayList<Integer>();
			for(Map tar : taskNodes){
				if(tar.get("nodeId") != null){
					targetIds.add(Integer.parseInt(tar.get("nodeId").toString()));
				}
			}
			switch(Integer.parseInt(paramMap.get("taskType"))){
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH:
				 nxReportManagerService.getReport_WaveTransOUT(taskNodes, paramMap, creator,CommonDefine.REPORT.REPORT_SCHEDULE);
				 break;
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_AMP:
	    		nxReportManagerService.getAmplifierDataForReport(targetIds, paramMap, 
	    								CommonDefine.REPORT.REPORT_SCHEDULE, false, creator);
	    		break;
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_SWITCH:
	    		nxReportManagerService.getSwitchDataForReport(targetIds, paramMap, 
	    								CommonDefine.REPORT.REPORT_SCHEDULE, false, creator);
	    		break;
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_JOIN:
	    		paramMap.put("unitType",String.valueOf(CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN));
	    		nxReportManagerService.getWaveDataForReport(targetIds, paramMap, 
	    								CommonDefine.REPORT.REPORT_SCHEDULE, false, creator);
	    		break;
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_DIV:
	    		paramMap.put("unitType",String.valueOf(CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV));
	    		nxReportManagerService.getWaveDataForReport(targetIds, paramMap, 
										CommonDefine.REPORT.REPORT_SCHEDULE, false, creator);
	    		break;
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_SDH_PM:
	    		break;
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_IPRAN:
	    		nxReportManagerService.getReport_PTN_IPRAN(targetIds, paramMap, 
						CommonDefine.REPORT.REPORT_SCHEDULE, false, creator);
	    		break;
	    	case CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK:
	    		nxReportManagerService.getReport_PTN_FlowPeak(targetIds, paramMap, 
	    				CommonDefine.REPORT.REPORT_SCHEDULE, false, creator);
	    		break;
	    	}
			//哟，成功了嘛
			runStatus.put("flag", "end");
			runStatus.put("result", 1);
			performanceManagerMapper.taskStatusUpdate(runStatus);
		}catch (CommonException e) {
			//这里嘛任务肯定是失败了
			runStatus.put("flag", "end");
			runStatus.put("result", 2);
			performanceManagerMapper.taskStatusUpdate(runStatus);
			e.printStackTrace();
			ExceptionHandler.handleException(e);
		}
	}

	private boolean needExcute(String taskId,boolean isMonthly){
//		Date updateTime = performanceManagerMapper.getTaskUpdateTime(Integer.parseInt(taskId));
//		Date today = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat();
//		if(isMonthly){
//			sdf.applyPattern("yyyy-MM");
//		}else{
//			sdf.applyPattern("yyyy-MM-dd");
//		}
//		if(sdf.format(updateTime).equals(sdf.format(today)))
//			return false;
//		else
			return true;
	}
	
	public boolean checkDeviceDomain(Map<String, String> map){
//		//对象权限检查
//		Integer userId = performanceManagerMapper.getCreatorByTaskId(map);
//		List<Map> outOfDomain = performanceManagerMapper.findNotInUserDomain(userId,map,CommonDefine.TREE.TREE_DEFINE); 
//		if(outOfDomain!=null && outOfDomain.size()>0){
//			performanceManagerMapper.deleteNodesOutOfDomain(map, outOfDomain);
//		}
//		Integer count = performanceManagerMapper.checkNodeCount(map);
//		if(count==0)
//			return false;
//		else
			return true;
	}
}
