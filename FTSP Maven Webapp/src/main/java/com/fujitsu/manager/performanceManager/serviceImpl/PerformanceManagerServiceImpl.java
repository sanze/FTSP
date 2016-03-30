package com.fujitsu.manager.performanceManager.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.CsvUtil;
import com.fujitsu.common.ExportResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.common.PMDataUtil;
import com.fujitsu.common.poi.ColumnMap;
import com.fujitsu.common.poi.CoverGenerator;
import com.fujitsu.common.poi.PushExcelUtil;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.InstantReportMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.job.CollectDataJob;
import com.fujitsu.job.PmReportJob;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.performanceManager.service.PerformanceManagerService;
import com.fujitsu.model.OpticalPathMonitorModel;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.ExportExcelUtil;
import com.fujitsu.util.SpringContextUtil;
import com.fujitsu.util.ZipUtil;

@Scope("prototype")
@Service
//@Transactional(rollbackFor = Exception.class)
public class PerformanceManagerServiceImpl extends PerformanceManagerService {
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;
	@Resource
	private CommonManagerMapper commonManagerMapper;
	@Resource
	private ICommonManagerService commonManagerService;
	@Resource
	private IQuartzManagerService quartzManagerService;
	@Resource
	private InstantReportMapper instantReportMapper;

	private IDataCollectServiceProxy dataCollectService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getBaseEmsGroups() throws CommonException {
		Map returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			Map all = new HashMap<String, Object>();
			all.put("BASE_EMS_GROUP_ID", 0);
			all.put("GROUP_NAME", "全部");
			returnList = performanceManagerMapper.getBaseEmsGroups();
			returnList.add(0, all);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, 6000);
		}
		return returnData;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getEmsList(int emsGroupId, int userId, int startNumber,
			int pageSize) throws CommonException {
		Map returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		int total = 0;
		try {
			// 获取该用户有权限的网管和网管分组列表
			// StringBuffer sb = new StringBuffer();
			// List<Map> allowedEmses = commonManagerService
			// .getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL,
			// false);
			// sb = new StringBuffer();
			// for (Map map : allowedEmses) {
			// sb.append(map.get("BASE_EMS_CONNECTION_ID"));
			// sb.append(",");
			// }
			// String ems = sb.toString();
			// if (ems.length() > 0) {
			// ems = ems.substring(0, ems.length() - 1);
			// }
			// if ("".equals(ems)) {
			// total = 0;
			// } else {
			if (emsGroupId == CommonDefine.VALUE_ALL) {
				// 选择了全部
				returnList = performanceManagerMapper.getEmsList(null,
						startNumber, pageSize, RegularPmAnalysisDefine, userId,
						CommonDefine.TREE.TREE_DEFINE);
				total = performanceManagerMapper.getEmsCount(null,
						RegularPmAnalysisDefine, userId,
						CommonDefine.TREE.TREE_DEFINE);
			} else {
				// 选择了某一个网管分组或无
				returnList = performanceManagerMapper.getEmsList(emsGroupId,
						startNumber, pageSize, RegularPmAnalysisDefine, userId,
						CommonDefine.TREE.TREE_DEFINE);
				total = performanceManagerMapper.getEmsCount(emsGroupId,
						RegularPmAnalysisDefine, userId,
						CommonDefine.TREE.TREE_DEFINE);
			}
			// }
			returnData.put("total", total);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateEmsList(List<Map> emsList) throws CommonException {
		try {
			for (Map ems : emsList) {
				performanceManagerMapper.modifyEmsInfo(
						(String) ems.get("COLLEC_START_TIME"),
						(String) ems.get("COLLEC_END_TIME"),
						((Long) ems.get("COLLECT_SOURCE")).intValue(),
						((Long) ems.get("BASE_EMS_CONNECTION_ID")).intValue());
				String startTime = (String) ems.get("COLLEC_START_TIME");
				String[] time = startTime.split(":");
				// 添加quartz任务控制代码
				// String times[] = ((String)
				// ems.get("COLLEC_START_TIME")).split(":");
				// String cronExpression = "0 " + times[0] + " " + times[1]
				// + " * * ?";
				quartzManagerService.modifyJobTime(
						CommonDefine.PM.PM_TASK_TYPE,
						((Long) ems.get("SYS_TASK_ID")).intValue(), "0 "
								+ time[1] + " " + time[0] + " * * ?");
				// controlReportTaskTime();
			}
		} catch (ClassCastException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
		} catch (NullPointerException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getNeList(int emsId, int type, String productName,String subIds,
			int startNumber, int pageSize) throws CommonException {
		Map returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		int total;
		try {
			if(subIds !=null && !"".equals(subIds)){
				subIds = getSubIds(subIds);
			}
			returnList = performanceManagerMapper
					.getNeList(emsId, type, productName, subIds,startNumber, pageSize,
							RegularPmAnalysisDefine);
			total = performanceManagerMapper.getNeCount(emsId, type,
					productName,subIds, RegularPmAnalysisDefine);
			returnData.put("total", total);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}
	
	public String getSubIds(String subIds){
		List <Map> mapIds = new ArrayList<Map>();
		String tmpIds="("+subIds+")";
		mapIds = performanceManagerMapper.getSubIds(tmpIds);
		tmpIds="("+subIds;
		if(mapIds!=null){
			for(Map id:mapIds){
				tmpIds+=","+id.get("BASE_SUBNET_ID");
			}
		}
		tmpIds+=")";
		return tmpIds; 
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateNeList(List<Map> neList) throws CommonException {
		try {
			for (Map ne : neList) {
				performanceManagerMapper.modifyNeInfo(
						((Long) ne.get("BASE_NE_ID")).intValue(),
						((Long) ne.get("NE_LEVEL")).intValue(),
						((Long) ne.get("COLLECT_NUMBIC")).intValue(),
						((Long) ne.get("COLLECT_PHYSICAL")).intValue(),
						((Long) ne.get("COLLECT_CTP")).intValue());
			}
		} catch (ClassCastException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
		} catch (NullPointerException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getProductNames(int emsId, int type) throws CommonException {
		Map returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			Map all = new HashMap<String, Object>();
			all.put("PRODUCT_NAME", "全部");
			returnList = performanceManagerMapper.getProductNames(emsId, type);
			returnList.add(0, all);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Map getNeStateListMulti(int emsId,Integer type, String productName,
				String subnetIdStr,String startTime,String endTime,
				int startNumber, int pageSize) throws CommonException {
			Map returnData = new HashMap<String, Object>();
			String HOVER_STR = "hover";
			List<Map> returnList = new ArrayList<Map>(); 
			try {
				startTime+=" 00:00:00";
				endTime+=" 23:59:59";
				//只能用网元做分页了
				List<Map> neList = new ArrayList<Map>();
//				getSubnetChildren();
				if(subnetIdStr!=null&&!subnetIdStr.isEmpty()){
					List<Map> subnetIdList = new ArrayList<Map>();
					for(String id :subnetIdStr.split(",")){
						HashMap<String, Integer> m = new HashMap<String, Integer>();
						m.put("nodeId", Integer.valueOf(id));
						subnetIdList.add(m);
					}
					subnetIdStr = getAllSubnets(subnetIdList);
				}
				Integer count = performanceManagerMapper.getNeByEmsByPageCount(emsId,productName,subnetIdStr,type,RegularPmAnalysisDefine);
				if(count==0){
					returnData.put("total", count);
					returnData.put("rows", returnList);
					return returnData;
				}
				neList = performanceManagerMapper.getNeByEmsByPage(emsId,productName,subnetIdStr,type,startNumber,pageSize,RegularPmAnalysisDefine);
				
				// 之后用不到分页了
				startNumber = 0;
				pageSize = 0;
				// 这是二维数据的一维展开形式！
				List<Map> recMultiList = performanceManagerMapper.getNeStateListMulti(neList,startTime,endTime);
				
				//把不同的neId的记录规整一下
				Map<String,Map> neIdVsNeInfo = new HashMap<String, Map>();
				for(Map m : neList){
					neIdVsNeInfo.put(m.get("BASE_NE_ID").toString(),m);
				}
				// @neIdVsNeInfo 此时已经包含了NE的信息，之后只要把采集记录一天一天的附加上去就行了
				for(Map m : recMultiList){
					String neKey = m.get("NE_ID").toString();
					if(neIdVsNeInfo.containsKey(neKey)){
						Map neInfo = neIdVsNeInfo.get(neKey);
						// @dCount 表示这是第几天的数据的计数
						
						neInfo.put(m.get("BELONG_TO_DATE").toString(), m.get("ACTION_RESULT"));
						
						String hoverInfo = hoverInfoGen(
								m.get("ACTION_RESULT").toString(),// ACTION_RESULT-理论上不为null
								m.get("FAILED_REASON").toString(),// FAILED_REASON-理论上不为null
								m.get("COLLECT_TIME")==null?null:m.get("COLLECT_TIME").toString(),
								Integer.parseInt(m.get("COLLECT_TYPE").toString()));
						neInfo.put(m.get("BELONG_TO_DATE").toString()+HOVER_STR, hoverInfo);
					}
					// 在网元MAP之外的网元就不放了，不然影响分页，理论上不可能有
				}
				Iterator it = neIdVsNeInfo.keySet().iterator();
				while(it.hasNext()){
					returnList.add(neIdVsNeInfo.get(it.next()));
				}
				//处理完了之后应该就是可以用的数据了
				returnData.put("total", count);
				returnData.put("rows", returnList);
			} catch (Exception e) {
				e.printStackTrace();
				throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
			}
			return returnData;
		}

	private String hoverInfoGen(String ACTION_RESULT,String FAILED_REASON,String COLLECT_TIME,int COLLECT_TYPE){
		StringBuffer hoverInfo = new StringBuffer();
		String collectType = null;
		if(COLLECT_TYPE==CommonDefine.PM.NE_LEVEL.KEY_COLLECT){
			collectType = "重点采";
		}else if(COLLECT_TYPE==CommonDefine.PM.NE_LEVEL.CYCLE_COLLECT){
			collectType = "循环采";
		}else if(COLLECT_TYPE==CommonDefine.PM.NE_LEVEL.NO_COLLECT){
			collectType = "无计划";
		}
		if(ACTION_RESULT.equals("1")){
			return hoverInfo.append("采集方式:").append(collectType).append("<br/>采集时间:").append(COLLECT_TIME).append("<br/>项数:").append(FAILED_REASON).toString();
		}else if(ACTION_RESULT.equals("0")){
			return hoverInfo.append("采集方式:").append(collectType).append("<br/>采集时间:").append(COLLECT_TIME).append("<br/>失败原因:").append(FAILED_REASON).toString();
		}else{
			return collectType;
		}
		
	} 
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getNeStateList(Integer neId, String startTime, String endTime,
			int startNumber, int pageSize) throws CommonException {
		Map returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			if(neId==null)
				return returnData;
			List<Map> neList = new ArrayList<Map>();
			startTime += " 00:00:00";
			endTime += " 23:59:59";
			Map m = new HashMap();
			m.put("BASE_NE_ID", neId);
			neList.add(m);
			returnList = performanceManagerMapper.getNeStateList(neList,
					 startTime, endTime, startNumber,pageSize, RegularPmAnalysisDefine);
			int count = performanceManagerMapper.getNeStateCount(neList,startTime, endTime, RegularPmAnalysisDefine);
			returnData.put("total", count);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void changeTaskStatus(List<Map> taskInfoList) throws CommonException {
		try {
			for (Map taskInfo : taskInfoList) {
				Integer taskStatus;
				// 修改网管执行状态
				int taskId = Integer.parseInt(taskInfo.get("SYS_TASK_ID")
						.toString());
				int pageTaskStatus = Integer.parseInt(taskInfo.get(
						"TASK_STATUS").toString());
				taskStatus = performanceManagerMapper
						.getTaskCollectStatus(taskId);
				if (pageTaskStatus == CommonDefine.PM.TASK_STATUS.SUSPEND) {
					if (taskStatus != CommonDefine.PM.TASK_STATUS.SUSPEND) {
						// 添加quartz任务控制代码
						quartzManagerService.ctrlJob(
								CommonDefine.PM.PM_TASK_TYPE, taskId,
								CommonDefine.QUARTZ.JOB_PAUSE);
						// for test
						// quartzManagerService.ctrlJob(CommonDefine.PM.PM_TASK_TYPE,
						// taskId, CommonDefine.QUARTZ.JOB_DELETE);
						// 需挂起任务,且任务本身未被挂起
						performanceManagerMapper.updateEmsJobStatus(taskId,
								CommonDefine.PM.TASK_STATUS.SUSPEND);
					}
				} else if (pageTaskStatus == CommonDefine.PM.TASK_STATUS.INUSE) {
					if (taskStatus != CommonDefine.PM.TASK_STATUS.INUSE) {
						// for test
						quartzManagerService.ctrlJob(
								CommonDefine.PM.PM_TASK_TYPE, taskId,
								CommonDefine.QUARTZ.JOB_DELETE);

						int emsId = (Integer) performanceManagerMapper
								.getTaskTargetIds(taskId).get(0)
								.get("TARGET_Id");
						Map map = new HashMap();
						map.put("BASE_EMS_CONNECTION_ID", emsId);
						List<Map> ems = performanceManagerMapper
								.getEmsCollectInfo(emsId, CommonDefine.TRUE);
						String startTime = (String) ems.get(0).get(
								"COLLEC_START_TIME");
						String times[] = startTime.split(":");
						String cronExpression = "0 " + times[1] + " "
								+ times[0] + " * * ?";
						quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_PM,
								taskId, CollectDataJob.class, cronExpression,
								map);
						// 添加quartz任务控制代码
						quartzManagerService.ctrlJob(
								CommonDefine.PM.PM_TASK_TYPE, taskId,
								CommonDefine.QUARTZ.JOB_RESUME);
						Map info = quartzManagerService.getJobInfo(6, taskId);
						// 需启用任务,且任务本身未被启用
						performanceManagerMapper.updateEmsJobStatus(taskId,
								CommonDefine.PM.TASK_STATUS.INUSE);
					}
				}
			}
		} catch (CommonException e) {
			throw e;
		} catch (ClassCastException e) {

			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public Integer getTaskStatus(int taskId) throws CommonException {
		try {
			return performanceManagerMapper.getTaskCollectStatus(taskId);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public Integer getTaskCollectResult(int taskId) throws CommonException {
		try {
			return performanceManagerMapper.getTaskCollectResult(taskId);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public Timestamp getForbiddenTimeLimit(int taskId) throws CommonException {
		try {
			String s = performanceManagerMapper.getTaskForbiddenTime(taskId);
			if (s != null) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						CommonDefine.COMMON_FORMAT);
				Date forbiddenDate = formatter.parse(s);
				Timestamp fobiddenTime = new Timestamp(forbiddenDate.getTime());
				return fobiddenTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public void pauseTask(int taskId, int pauseTime) throws CommonException {
		try {
			Integer taskStatus = getTaskCollectResult(taskId);
			Timestamp forbiddenTimeLimit = getForbiddenTimeLimit(taskId);
			if (taskStatus == CommonDefine.PM.COLLECT_STATUS.EXECUTING
					&& forbiddenTimeLimit == null) {
				Date now = new Date(System.currentTimeMillis());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(now);
				calendar.add(Calendar.MINUTE, pauseTime);
				Timestamp forbiddenTime = new Timestamp(calendar.getTime()
						.getTime());
				SimpleDateFormat format = new SimpleDateFormat(
						CommonDefine.COMMON_FORMAT);
				performanceManagerMapper.insertTaskForbiddenTime(taskId,
						format.format(forbiddenTime));
			} else {
				throw new CommonException(new Exception(),
						MessageCodeDefine.PM_PAUSE_ERROR);
			}
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public void resumeTask(int taskId) throws CommonException {
		try {
			Integer taskStatus = getTaskCollectResult(taskId);
			Timestamp forbiddenTimeLimit = getForbiddenTimeLimit(taskId);
			if (taskStatus == CommonDefine.PM.COLLECT_STATUS.PAUSE
					&& forbiddenTimeLimit != null) {
				performanceManagerMapper.deleteTaskForbiddenTime(taskId);
			} else {
				throw new CommonException(new Exception(),
						MessageCodeDefine.PM_RESUME_ERROR);
			}
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public void insertPmTask(int emsId, String displayName)
			throws CommonException {
		try {
			Map<String, Object> task = new HashMap<String, Object>();
			task.put("taskName", displayName + "数据采集");
			performanceManagerMapper.insertTask(task, RegularPmAnalysisDefine);
			task.put("emsId", emsId);
			performanceManagerMapper.insertTaskTarget(task,
					RegularPmAnalysisDefine);
			// 添加一个quartz任务
			Map map = new HashMap();
			map.put("BASE_EMS_CONNECTION_ID", emsId);
			List<Map> ems = performanceManagerMapper.getEmsCollectInfo(emsId,
					CommonDefine.TRUE);
			String startTime = (String) ems.get(0).get("COLLEC_START_TIME");
			if (startTime == null || startTime.isEmpty())
				startTime = CommonDefine.COLLEC_START_TIME;
			String times[] = startTime.split(":");
			String cronExpression = "0 " + times[1] + " " + times[0] + " * * ?";
			quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_PM,
					Integer.parseInt(task.get("SYS_TASK_ID").toString()),
					CollectDataJob.class, cronExpression, map);
			// 将任务挂起
			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_PM,
					Integer.parseInt(task.get("SYS_TASK_ID").toString()),
					CommonDefine.QUARTZ.JOB_PAUSE);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public void deletePmTask(int emsId) throws CommonException {
		try {
			Integer taskId = performanceManagerMapper.getTaskIdFromEmsId(emsId,
					PerformanceManagerService.RegularPmAnalysisDefine);
			if (taskId != null) {
				if (quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_PM,
						taskId)) {
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_PM,
							taskId, CommonDefine.QUARTZ.JOB_PAUSE);
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_PM,
							taskId, CommonDefine.QUARTZ.JOB_DELETE);
				}
				performanceManagerMapper.deleteTaskTarget(taskId);
				performanceManagerMapper.deleteTask(taskId);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map getCurrentPmTempleteInfo(int templateId, String pmStdIndex,
			int domain) throws CommonException {
		try {
			Map returnMap = performanceManagerMapper.getCurrentPmTempleteInfo(
					templateId, pmStdIndex, domain);
			returnMap.put("returnResult", CommonDefine.SUCCESS);
			return returnMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Map<String, Object> getCurrentPmData(boolean maxMin, boolean isSDH,
			List<Map> nodeList, int granularity, List<String> tpLevel,
			List<String> pmStdIndexTypes, int currentUserId,int searchType)
			throws CommonException {
		// 查询唯一标记
		int searchTag = getSearchTag();
		if(searchType==CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH)
			searchTag+=10000;
		// 获取性能标准参数
		List<String> pmStdIndex = null;
		if(pmStdIndexTypes==null || pmStdIndexTypes.size() == 0){
			pmStdIndex = performanceManagerMapper.getPmStdIndexes_new(
					null, maxMin);
		}else if (pmStdIndexTypes.size() > 0) {
//			String pmStdIndexIdString = pmStdIndexIds.toString();
//			pmStdIndexIdString = pmStdIndexIdString.substring(1,
//					pmStdIndexIdString.length() - 1);
			pmStdIndex = performanceManagerMapper.getPmStdIndexes_new(
					pmStdIndexTypes, maxMin);
		}
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("searchTag", searchTag);
		StringBuffer sb = new StringBuffer();
		try {
			Map<String, String> conditionMap = getSearchCondition(isSDH, null,
					nodeList, tpLevel,searchType);
			// 先获取所有PtpId
			Map<String, List<Map>> idsMap = getPmSearchNeAndPtps(conditionMap);
			List<Map> neIds = idsMap.get("neMap");
			List<Map> unitIds = idsMap.get("unitMap");
			List<Map> ptpIds = idsMap.get("ptpMap");
			// 清除临时表中该用户当前性能
			if (isSDH) {
				performanceManagerMapper.deleteTempPm(
						CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA,
						currentUserId,searchType);
			} else {
				performanceManagerMapper.deleteTempPm(
						CommonDefine.PM.PM_TABLE_NAMES.CURRENT_WDM_DATA,
						currentUserId,searchType);
			}

			// 数据采集与保存,先判端口是否没有
			if (ptpIds.size() > 0) {
				// 下面这些东西只有我和上帝知道是什么意思
				Map<Integer, List<Integer>> ptpIdsMap = new HashMap<Integer, List<Integer>>();
				List<Integer> ptpIdGroupByEms = new ArrayList<Integer>();
				// 初始化为第一个端口的网管ID
				int emsConnectionId = (Integer) ptpIds.get(0).get(
						"BASE_EMS_CONNECTION_ID");
				for (Map m : ptpIds) {
					if ((Integer) m.get("BASE_EMS_CONNECTION_ID") != emsConnectionId) {
						// 记录旧网管端口
						ptpIdsMap.put(emsConnectionId, ptpIdGroupByEms);
						// 记录新网管端口信息
						ptpIdGroupByEms = new ArrayList<Integer>();
						emsConnectionId = (Integer) m
								.get("BASE_EMS_CONNECTION_ID");
					}
					ptpIdGroupByEms.add((Integer) m.get("BASE_PTP_ID"));
				}
				// 处理最后一个网管的端口
				ptpIdsMap.put(emsConnectionId, ptpIdGroupByEms);

				Map<Integer, List<Integer>> unitIdsMap = new HashMap<Integer, List<Integer>>();
				List<Integer> unitIdGroupByEms = new ArrayList<Integer>();
				// 初始化为第一个端口的网管ID
				emsConnectionId = (Integer) unitIds.get(0).get(
						"BASE_EMS_CONNECTION_ID");
				for (Map m : unitIds) {
					if ((Integer) m.get("BASE_EMS_CONNECTION_ID") != emsConnectionId) {
						// 记录旧网管端口
						unitIdsMap.put(emsConnectionId, unitIdGroupByEms);
						// 记录新网管端口信息
						unitIdGroupByEms = new ArrayList<Integer>();
						emsConnectionId = (Integer) m
								.get("BASE_EMS_CONNECTION_ID");
					}
					unitIdGroupByEms.add((Integer) m.get("BASE_UNIT_ID"));
				}
				// 处理最后一个网管的端口
				unitIdsMap.put(emsConnectionId, unitIdGroupByEms);

				// 初始化为第一个端口的网管ID
				emsConnectionId = (Integer) neIds.get(0).get(
						"BASE_EMS_CONNECTION_ID");
				// List<Integer> neIdGroupByEms = new ArrayList<Integer>();
				for (Map m : neIds) {
					// if ((Integer) m.get("BASE_EMS_CONNECTION_ID") !=
					// emsConnectionId) {
					// // 采集旧网管性能数据
					try {
						emsConnectionId = (Integer) m
								.get("BASE_EMS_CONNECTION_ID");
						collectAndSavePmDate(isSDH, emsConnectionId,
								(Integer) m.get("BASE_NE_ID"), granularity,
								pmStdIndex, currentUserId, searchTag,
								ptpIdsMap.get(emsConnectionId),
								unitIdsMap.get(emsConnectionId));
					} catch (CommonException e) {
						e.printStackTrace();
						sb.append(m.get("DISPLAY_NAME")
								+ ":"
								+ MessageHandler.getErrorMessage(e
										.getErrorCode()) + "<br>");
					}

					// // 记录新网管端口信息
					// neIdGroupByEms = new ArrayList<Integer>();
					// emsConnectionId = (Integer) m
					// .get("BASE_EMS_CONNECTION_ID");
					// }
					// neIdGroupByEms.add((Integer) m.get("BASE_NE_ID"));
				}
				returnMap.put("returnMessage", sb.toString());
				// // 处理最后一个网管的性能
				// collectAndSavePmDate(isSDH, emsConnectionId, neIdGroupByEms,
				// granularity, pmStdIndex, currentUserId, searchTag,
				// ptpIdsMap.get(emsConnectionId));
			} else {
				conditionMap = getSearchCondition(!isSDH, null, nodeList,
						tpLevel,searchType);
				// 先获取所有PtpId
				idsMap = getPmSearchNeAndPtps(conditionMap);
				ptpIds = idsMap.get("ptpMap");
				if (ptpIds.size() > 0) {
					if (isSDH) {
						// SDH查询选择了WDM网元
						throw new CommonException(new Exception(),
								MessageCodeDefine.PM_DONT_SELECT_WDM);
					} else if (!isSDH) {
						// WDM查询选择了SDH网元
						throw new CommonException(new Exception(),
								MessageCodeDefine.PM_DONT_SELECT_SDH);
					}
				} else {
					return returnMap;
				}
			}
		} catch (CommonException e) {
			ExceptionHandler.handleException(e);
			throw e;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

		return returnMap;
	}

	// 当前性能采集&&保存
	private void collectAndSavePmDate(boolean isSDH, int emsConnectionId,
			Integer neId, int granularity, List<String> pmStdIndex,
			int currentUserId, int searchTag, List<Integer> ptpIdList,
			List<Integer> unitIdList) throws CommonException {
		// 采集性能
		List<PmDataModel> pmDataList = new ArrayList<PmDataModel>();
		IDataCollectServiceProxy dataCollectService = SpringContextUtil
				.getDataCollectServiceProxy(emsConnectionId);
		// for (Integer neId : neIdGroupByEms) {
		pmDataList.addAll(dataCollectService.getCurrentPmData_Ne(neId,
				new short[] {}, new int[] {}, new int[] { granularity }, true,
				true, true, CommonDefine.COLLECT_LEVEL_1));
		// }
		// 过滤并保存性能,循环处理性能数据
		List<Map<String, Object>> pmMapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> aPm;
		List<PmMeasurementModel> pmMeasurementList;
		for (PmDataModel pmDataModel : pmDataList) {
			if ((pmDataModel.getPtpId() == null && unitIdList.contains(pmDataModel.getUnitId())) ||
				(pmDataModel.getPtpId() != null && ptpIdList.contains(pmDataModel.getPtpId())) ||
				// Fix for 10240。如果要取网元级性能应当先判断ptpId和unitId为空
				// 否则所有有网元Id的记录都为true了。wss
				pmDataModel.getPtpId() == null&&pmDataModel.getUnitId()==null&&pmDataModel.getNeId() != null) {
				pmMeasurementList = pmDataModel.getPmMeasurementList();
				for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
					if (pmStdIndex != null
							&& pmStdIndex.contains(pmMeasurementModel
									.getPmStdIndex())) {
						aPm = new HashMap<String, Object>();
						aPm.put("BASE_EMS_CONNECTION_ID",
								pmDataModel.getEmsConnectionId());
						aPm.put("BASE_NE_ID", pmDataModel.getNeId());
						aPm.put("BASE_RACK_ID", pmDataModel.getRackId());
						aPm.put("BASE_SHELF_ID", pmDataModel.getShelfId());
						aPm.put("BASE_SLOT_ID", pmDataModel.getSlotId());
						aPm.put("BASE_SUB_SLOT_ID", pmDataModel.getSubSlotId());
						aPm.put("BASE_UNIT_ID", pmDataModel.getUnitId());
						aPm.put("BASE_SUB_UNIT_ID", pmDataModel.getSubUnitId());
						aPm.put("BASE_PTP_ID", pmDataModel.getPtpId());
						aPm.put("BASE_OTN_CTP_ID", pmDataModel.getOtnCtpId());
						aPm.put("BASE_SDH_CTP_ID", pmDataModel.getSdhCtpId());
						aPm.put("TARGET_TYPE", pmDataModel.getTargetType());
						aPm.put("LAYER_RATE", pmDataModel.getLayerRate());
						aPm.put("PM_STD_INDEX",
								pmMeasurementModel.getPmStdIndex());
						aPm.put("PM_INDEX",
								pmMeasurementModel.getPmParameterName());
						aPm.put("PM_VALUE", pmMeasurementModel.getValue());
						aPm.put("PM_COMPARE_VALUE",
								pmMeasurementModel.getPmCompareValue());
						aPm.put("PM_COMPARE_VALUE_DISPLAY",
								pmMeasurementModel.getDisplayCompareValue());
						aPm.put("TYPE", pmMeasurementModel.getType());
						aPm.put("THRESHOLD_1",
								pmMeasurementModel.getThreshold1());
						aPm.put("THRESHOLD_2",
								pmMeasurementModel.getThreshold2());
						aPm.put("THRESHOLD_3",
								pmMeasurementModel.getThreshold3());
						aPm.put("FILTER_VALUE",
								pmMeasurementModel.getFilterValue());
						aPm.put("OFFSET", pmMeasurementModel.getOffset());
						aPm.put("UPPER_VALUE",
								pmMeasurementModel.getUpperValue());
						aPm.put("UPPER_OFFSET",
								pmMeasurementModel.getUpperOffset());
						aPm.put("LOWER_VALUE",
								pmMeasurementModel.getLowerValue());
						aPm.put("LOWER_OFFSET",
								pmMeasurementModel.getLowerOffset());
						aPm.put("PM_DESCRIPTION",
								pmMeasurementModel.getPmdescription());
						aPm.put("LOCATION",
								pmMeasurementModel.getLocationFlag());
						aPm.put("UNIT", pmMeasurementModel.getUnit());
						aPm.put("GRANULARITY", pmDataModel.getGranularityFlag());
						aPm.put("EXCEPTION_LV",
								pmMeasurementModel.getExceptionLv());
						aPm.put("EXCEPTION_COUNT",
								pmMeasurementModel.getExceptionCount());
						aPm.put("RETRIEVAL_TIME", new Timestamp(pmDataModel
								.getRetrievalTimeDisplay().getTime()));
						aPm.put("DISPLAY_EMS_GROUP",
								pmDataModel.getDisplayEmsGroup());
						aPm.put("DISPLAY_EMS", pmDataModel.getDisplayEms());
						aPm.put("DISPLAY_SUBNET",
								pmDataModel.getDisplaySubnet());
						aPm.put("DISPLAY_NE", pmDataModel.getDisplayNe());
						aPm.put("DISPLAY_AREA", pmDataModel.getDisplayArea());
						aPm.put("DISPLAY_STATION",
								pmDataModel.getDisplayStation());
						aPm.put("DISPLAY_PRODUCT_NAME",
								pmDataModel.getDisplayProductName());
						aPm.put("DISPLAY_PORT_DESC",
								pmDataModel.getDisplayPortDesc());
						aPm.put("DISPLAY_CTP", pmDataModel.getDisplayCtp());
						aPm.put("DISPLAY_TEMPLATE_NAME",
								pmDataModel.getDisplayTemplateName());
						aPm.put("SYS_USER_ID", currentUserId);
						// ==================================
						aPm.put("RATE", pmDataModel.getRate());
						aPm.put("DOMAIN", pmDataModel.getDomain());
						aPm.put("TEMPLATE_ID", pmDataModel.getPmTemplateId());
						aPm.put("BASE_SUBNET_ID", pmDataModel.getSubnetId());
						aPm.put("PTP_TYPE", pmDataModel.getPtpType());
						aPm.put("SEARCH_TAG", searchTag);
						pmMapList.add(aPm);
					}
				}
			}
		}
		// for test
		// Map<String, Object> aPm1 = new HashMap<String, Object>();
		// Map<String, Object> aPm2 = new HashMap<String, Object>();
		// aPm1.put("SYS_USER_ID", 1);
		// aPm1.put("SEARCH_TAG", searchTag);
		// aPm1.put("TEMPLATE_ID", 1);
		// aPm1.put("DISPLAY_TEMPLATE_NAME", "华为模板");
		// aPm1.put("TYPE", 2);
		// aPm1.put("PM_STD_INDEX", "aa");
		// for (int i = 0; i < 200; i++) {
		// pmMapList.add(aPm1);
		// }
		if (pmMapList.size() > 0) {
			if (isSDH) {
				performanceManagerMapper.insertCurrentTempPm(pmMapList,
						CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA);
			} else {
				performanceManagerMapper.insertCurrentTempPm(pmMapList,
						CommonDefine.PM.PM_TABLE_NAMES.CURRENT_WDM_DATA);
			}
		}

	}

	// 获取查询条件
	private Map<String, String> getSearchCondition(boolean isSDH,
			List<Map> subnetList, List<Map> nodeList, List<String> tpLevel,int searchType) {
		Map<String, String> conditionMap = new HashMap<String, String>();
		// 添加子网列表
		if (subnetList != null && subnetList.size() > 0) {
			conditionMap.put("NODE_SUBNET", getAllSubnets(subnetList));
		}
		for (Map m : nodeList) {
			String key = "";
			// 把选中的node按照nodeLevel归类，nodeId组成一个以逗号隔开的字符串，并放进Map中
			key = changeNodeLevelFromIntToString(Integer.parseInt(m.get(
					"nodeLevel").toString()));
			String nodeId = String.valueOf(m.get("nodeId"));
			if (conditionMap.containsKey(key)) {
				conditionMap.put(key, conditionMap.get(key) + ',' + nodeId);
			} else {
				conditionMap.put(key, nodeId);
			}
		}
		if (isSDH == true) {
			conditionMap.put("DOMAIN",
					String.valueOf(CommonDefine.NE_TYPE_SDH_FLAG));
		} else {
			conditionMap.put("DOMAIN",
					String.valueOf(CommonDefine.NE_TYPE_WDM_FLAG));
		}
		// tpLevel条件
		String rate = "";
		if (tpLevel!=null&&tpLevel.size() > 1) {
			if ("not_in".equals(tpLevel.get(0))) {
				// tpLevel.remove(0);
				for (int i = 1; i < tpLevel.size(); i++) {
					String s = tpLevel.get(i);
					rate = rate + "'" + s + "',";
				}
				conditionMap.put("PTP_TYPE_NOT_IN",
						rate.substring(0, rate.length() - 1));
			} else if ("in".equals(tpLevel.get(0))) {
				// tpLevel.remove(0);
				for (int i = 1; i < tpLevel.size(); i++) {
					String s = tpLevel.get(i);
					rate = rate + "'" + s + "',";
				}
				conditionMap.put("PTP_TYPE_IN",
						rate.substring(0, rate.length() - 1));
			}
		}
		conditionMap.put("searchType", String.valueOf(searchType));
		return conditionMap;
	}

	// 获取端口列表
	@SuppressWarnings("rawtypes")
	private Map<String, List<Map>> getPmSearchNeAndPtps(
			Map<String, String> conditionMap) {

		List<Map> neMap = performanceManagerMapper.getNeId(conditionMap,
				RegularPmAnalysisDefine);
		List<Map> unitMap = performanceManagerMapper.getUnitId(conditionMap,
				RegularPmAnalysisDefine);
		List<Map> ptpMap = performanceManagerMapper.getPtpId(conditionMap,
				RegularPmAnalysisDefine);
		Map<String, List<Map>> returnMap = new HashMap<String, List<Map>>();
		returnMap.put("neMap", neMap);
		returnMap.put("unitMap", unitMap);
		returnMap.put("ptpMap", ptpMap);
		return returnMap;
	}

	// 获取所有子网
	@SuppressWarnings("rawtypes")
	private String getAllSubnets(List<Map> subnetList) {
		List<Integer> allSubnetList = new ArrayList<Integer>();
		for (Map m : subnetList) {
			allSubnetList.addAll(getSubnetChildren(Integer.parseInt(m.get(
					"nodeId").toString())));
		}
		String subnetString = allSubnetList.toString();
		// 转化为字符串
		return subnetString.substring(1, subnetString.length() - 1);
	}

	// 子网查询方法
	private List<Integer> getSubnetChildren(int subnetId) {
		List<Integer> subnetList = new ArrayList<Integer>();
		subnetList.add(subnetId);
		List<Integer> children = performanceManagerMapper
				.getSubnetList(subnetId);
		if (children.size() > 0) {
			for (Integer id : children) {
				subnetList.addAll(getSubnetChildren(id));
			}
		}
		return subnetList;
	}

	// 历史性能
	@SuppressWarnings({ "rawtypes" })
	@Override
	public int getHistoryPmData(boolean maxMin, boolean isSDH,
			List<Map> nodeList, String startTime, String endTime,
			List<String> tpLevel, List<String> pmStdIndexTypes, int currentUserId,int searchType)
			throws CommonException {
		// 获取性能标准参数
		List<String> pmStdIndex = null;
		if(pmStdIndexTypes==null || pmStdIndexTypes.size() == 0){
			pmStdIndex = performanceManagerMapper.getPmStdIndexes_new(
					null, maxMin);
		}else if (pmStdIndexTypes.size() > 0) {
//			String pmStdIndexIdString = pmStdIndexTypes.toString();
//			pmStdIndexIdString = pmStdIndexIdString.substring(1,
//					pmStdIndexIdString.length() - 1);
			pmStdIndex = performanceManagerMapper.getPmStdIndexes_new(
					pmStdIndexTypes, maxMin);
		}
		int searchTag = getSearchTag();
		if(searchType==CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH)
			searchTag+=10000;
		try {
			// 清除临时表中该用户当前性能
			if (isSDH) {
				performanceManagerMapper.deleteTempPm(
						CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA,
						currentUserId,searchType);
			} else {
				performanceManagerMapper.deleteTempPm(
						CommonDefine.PM.PM_TABLE_NAMES.HISTORY_WDM_DATA,
						currentUserId,searchType);
			}
			// 一些条件处理
			int domain;
			if (isSDH == true) {
				domain = CommonDefine.NE_TYPE_SDH_FLAG;
			} else {
				domain = CommonDefine.NE_TYPE_WDM_FLAG;
			}
			// 是否选了不该选的网元
			Map<String, String> condMap = getSearchCondition(isSDH, null,
					nodeList, tpLevel,searchType);
			// 先获取所有PtpId
			Map<String, List<Map>> idsMap = getPmSearchNeAndPtps(condMap);
			List<Map> ptpIds = idsMap.get("ptpMap");
			if (ptpIds.size() == 0) {
				condMap = getSearchCondition(!isSDH, null, nodeList, tpLevel,searchType);
				// 先获取所有PtpId
				idsMap = getPmSearchNeAndPtps(condMap);
				ptpIds = idsMap.get("ptpMap");
				if (ptpIds.size() > 0) {
					if (isSDH) {
						// SDH查询选择了WDM网元
						throw new CommonException(new Exception(),
								MessageCodeDefine.PM_DONT_SELECT_WDM);
					} else if (!isSDH) {
						// WDM查询选择了SDH网元
						throw new CommonException(new Exception(),
								MessageCodeDefine.PM_DONT_SELECT_SDH);
					}
				} else {
					return searchTag;
				}
			}
			// 处理时间获取表名
			SimpleDateFormat formatToMonth = new SimpleDateFormat("yyyy-MM");
			Date startDate = formatToMonth.parse(startTime.substring(0, 7));
			Date endDate = formatToMonth.parse(endTime.substring(0, 7));
			// 获取年月
			SimpleDateFormat formatForTableName = new SimpleDateFormat(
					"yyyy_MM");
			List<String> yearAndMonthes = new ArrayList<String>();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			while (calendar.getTimeInMillis() <= endDate.getTime()) {
				String yearAndMonth = formatForTableName.format(calendar
						.getTime());
				yearAndMonthes.add(yearAndMonth);
				calendar.add(Calendar.MONTH, 1);
			}

			// ========节点分网管处理========
			// 节点条件分层级筛选至各个EMS
			Map<Integer, Map<String, Object>> conditionMaps = getConditionsFromNodesGroupByEmsIds(nodeList);

			// 分表查询并插入临时表
			Map<String, Object> conditionMap;
			for (Iterator<Integer> it = conditionMaps.keySet().iterator(); it
					.hasNext();) {
				Integer key = it.next();
				conditionMap = conditionMaps.get(key);
				// conditionMap.put("emsId", key);
				conditionMap.put("DOMAIN", domain);
				conditionMap.put("startTime", startTime);
				conditionMap.put("endTime", endTime);
				// tpLevel条件
				String rate = "";
				if (tpLevel!=null&&tpLevel.size() > 1) {
					if ("not_in".equals(tpLevel.get(0))) {
						tpLevel.remove(0);
						for (String s : tpLevel) {
							rate += "'" + s + "',";
						}
						conditionMap.put("PTP_TYPE_NOT_IN",
								rate.substring(0, rate.length() - 1));
					} else if ("in".equals(tpLevel.get(0))) {
						tpLevel.remove(0);
						for (String s : tpLevel) {
							rate += "'" + s + "',";
						}
						conditionMap.put("PTP_TYPE_IN",
								rate.substring(0, rate.length() - 1));
					}
				}
				// PM_STD_INDEX
				String PM_STD_INDEX = "";
				if (pmStdIndex != null && pmStdIndex.size() > 0) {
					for (String s : pmStdIndex) {
						PM_STD_INDEX += "'" + s + "',";
					}
					conditionMap.put("PM_STD_INDEX", PM_STD_INDEX.substring(0,
							PM_STD_INDEX.length() - 1));
					for (String s : yearAndMonthes) {
						String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
								+ "_" + key.toString() + "_" + s;
						Integer existance = performanceManagerMapper
								.getPmTableExistance(tableName,
										SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
						if (existance != null && existance == 1) {
							if (isSDH) {
								performanceManagerMapper
										.insertHistoryTempPm(
												currentUserId,
												tableName,
												CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA,
												searchTag, conditionMap);
							} else {
								performanceManagerMapper
										.insertHistoryTempPm(
												currentUserId,
												tableName,
												CommonDefine.PM.PM_TABLE_NAMES.HISTORY_WDM_DATA,
												searchTag, conditionMap);
							}
						}
						// else {
						// // 表不存在
						// throw new CommonException(new Exception(),
						// MessageCodeDefine.PM_TABLE_NOT_EXIST);
						// }
					}
				}
			}
		} catch (CommonException e) {
			throw e;
		} catch (ParseException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return searchTag;
	}

	// 将节点分网管筛选
	@SuppressWarnings({ "rawtypes" })
	public Map<Integer, Map<String, Object>> getConditionsFromNodesGroupByEmsIds(
			List<Map> nodeList) {
		Map<Integer, Map<String, Object>> nodesGroupByEmsIds = new HashMap<Integer, Map<String, Object>>();
		Map<String, Object> conditionMap;
		for (Map m : nodeList) {
			int nodeLevel = Integer.parseInt(m.get("nodeLevel").toString());
			if (nodeLevel == CommonDefine.TREE.NODE.EMSGROUP) {
				int nodeId = Integer.parseInt(m.get("nodeId").toString());
				// 查询网管分组下所有网管并插入nodesGroupByEmsIds
				List<Integer> emsIds = performanceManagerMapper
						.getEmsIdsFromEmsGroupId(nodeId);
				for (Integer emsId : emsIds) {
					assert (!nodesGroupByEmsIds.containsKey(emsId));
					conditionMap = new HashMap<String, Object>();
					conditionMap
							.put(changeNodeLevelFromIntToString(CommonDefine.TREE.NODE.EMS),
									emsId.toString());
					nodesGroupByEmsIds.put(emsId, conditionMap);
				}
			} else {
				int emsId = Integer.parseInt(m.get("emsId").toString());
				String conditionKey = changeNodeLevelFromIntToString(nodeLevel);
				String nodeId = m.get("nodeId").toString();
				String conditionValue;
				if (nodeLevel == CommonDefine.TREE.NODE.SUBNET) {
					// 子网递归处理获取其所有子子网节点
					List<Integer> allSubnetList = getSubnetChildren(Integer
							.parseInt(nodeId));
					String subnetString = allSubnetList.toString();
					conditionValue = subnetString.substring(1,
							subnetString.length() - 1);
				} else {
					// 其余层级直接使用id
					conditionValue = nodeId;
				}

				if (nodesGroupByEmsIds.containsKey(emsId)) {
					// 此网管下节点非初次出现
					conditionMap = nodesGroupByEmsIds.get(emsId);
					if (conditionMap.containsKey(conditionKey)) {
						// 此层级节点非初次出现
						conditionMap.put(conditionKey,
								conditionMap.get(conditionKey).toString() + ','
										+ conditionValue);
					} else {
						// 此层级节点初次出现
						conditionMap.put(conditionKey, conditionValue);
					}
				} else {
					// 此网管下节点初次出现
					conditionMap = new HashMap<String, Object>();
					conditionMap.put(conditionKey, conditionValue);
					nodesGroupByEmsIds.put(emsId, conditionMap);
				}
			}
		}
		return nodesGroupByEmsIds;
	}

	// 从临时表中获取性能
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getTempPmDataByPage(String tableName, int exception, int userId,
			Integer searchTag, int startNumber, int pageSize)
			throws CommonException {
		Map returnData = new HashMap<String, Object>();
		try {
			List list = performanceManagerMapper.getTempPmList(tableName,
					exception, userId, searchTag, startNumber, pageSize);
			int total = performanceManagerMapper.getTempPmCount(tableName,
					exception, userId, searchTag);
			returnData.put("total", total);
			returnData.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

		return returnData;
	}

	@Override
	public String getPmExportedExcelPath(String tableName, int exception,
			int userId, Integer searchTag) {
		String destination = null;
		List<Map> list = performanceManagerMapper.getTempPmList(tableName,
				exception, userId, searchTag, 0, 0);
		SimpleDateFormat format = new SimpleDateFormat(
				CommonDefine.COMMON_FORMAT);
		for (Map m : list) {
			m.put("LOCATION", getDisplayLocation((Integer) m.get("LOCATION")));
			m.put("RETRIEVAL_TIME",
					format.format((Date) m.get("RETRIEVAL_TIME")));
			
			m.put("DOMAIN", getDisplayDomain(m.get("DOMAIN")!=null?Integer.parseInt(m.get("DOMAIN")
					.toString()):-1));
			
		}
		IExportExcel ex = new ExportExcelUtil(CommonDefine.PATH_ROOT
				+ CommonDefine.EXCEL.TEMP_DIR, "DEAULT");
		destination = ex.writeExcel(list,
				CommonDefine.EXCEL.PM_SEARCH_HEADER_CODE, false);
		return destination;
	}

	private String getDisplayDomain(int domain) {
		switch (domain) {
		case 1:
			return "SDH";
		case 2:
			return "WDM";
		case 3:
			return "ETH";
		case 4:
			return "ATM";
		default:
			return "";
		}
	}

	private String getDisplayLocation(int location) {
		switch (location) {
		case 1:
			return "近端接收";
		case 2:
			return "远端接收";
		case 3:
			return "近端发送";
		case 4:
			return "远端发送";
		case 5:
			return "双向";
		default:
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getCompareValueByPage(List<Map> nodeList, int startNumber,
			int pageSize) throws CommonException {
		Map returnData = new HashMap<String, Object>();
		try {
			Map<String, String> conditionMap = new HashMap<String, String>();
			for (Map m : nodeList) {
				String key = "";
				// 把选中的node按照nodeLevel归类，nodeId组成一个以逗号隔开的字符串，并放进Map中
				key = changeNodeLevelFromIntToString(Integer.parseInt(m.get(
						"nodeLevel").toString()));
				String nodeId = String.valueOf(m.get("nodeId"));
				if (conditionMap.containsKey(key)) {
					conditionMap.put(key, conditionMap.get(key) + ',' + nodeId);
				} else {
					conditionMap.put(key, nodeId);
				}
			}
			List list = performanceManagerMapper.getCompareValueList(
					conditionMap, startNumber, pageSize);
			int total = performanceManagerMapper
					.getCompareValueListCount(conditionMap);
			returnData.put("total", total);
			returnData.put("rows", list);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

		return returnData;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void updateCompareValueList(List<Map> compareValueList)
			throws CommonException {
		try {
			Map map = new HashMap<String, Object>();
			for (Map compareValue : compareValueList) {
				map = new HashMap<String, Object>();
				map.put("PM_COMPARE_ID", compareValue.get("PM_COMPARE_ID"));
				map.put("PM_COMPARE_VALUE",
						compareValue.get("PM_COMPARE_VALUE"));
				map.put("UPDATE_TIME", new Date());
				performanceManagerMapper.modifyPmCompare(map);
			}
		} catch (ClassCastException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
		} catch (NullPointerException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String generateCompareValue(List<Map> nodeList, int overwrite,
			String processKey) throws CommonException {
		StringBuffer sb = new StringBuffer();
		try {
			// 分类条件,查询NE列表
			List<Integer> neIds = getNeIdsFromNodes(nodeList);
			// 查询当前性能
			int emsConnectionId = Integer.parseInt(nodeList.get(0).get("emsId")
					.toString());
			IDataCollectServiceProxy dataCollectService = SpringContextUtil
					.getDataCollectServiceProxy(emsConnectionId);
			List<PmDataModel> pmList;
			for (int i = 0; i < neIds.size(); i++) {
				int neId = neIds.get(i);
				/*
				 * // 进度描述信息更改--此处修改 String text = "当前进度" + (i + 1) + "/" +
				 * neIds.size(); // 加入进度值 setProcessParameter(getSessionId(),
				 * processKey, text, (i + 1d) / neIds.size());
				 */
				CommonDefine.setProcessParameter(getSessionId(), processKey,
						(i + 1), neIds.size(), null);
				try {
					pmList = dataCollectService
							.getCurrentPmData_Ne(
									neId,
									new short[] {},
									new int[] {},
									new int[] { CommonDefine.PM.GRANULARITY_24HOUR_FLAG },
									true, true, true,
									CommonDefine.COLLECT_LEVEL_2);
					for (PmDataModel pmDataModel : pmList) {
						// 处理并保存基准值
						savePmDataModelAsCompareValue(pmDataModel, overwrite);
					}
				} catch (CommonException e) {
					sb.append(performanceManagerMapper.getNeDisplayName(neId)
							+ ":"
							+ MessageHandler.getErrorMessage(e.getErrorCode())
							+ "\n");
				}
			}
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		if (sb.length() > 0) {
			return sb.toString();
		} else {
			return null;
		}

	}

	// 处理并保存基准值
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void savePmDataModelAsCompareValue(PmDataModel pmDataModel,
			int overwrite) {
		List<PmMeasurementModel> pmMeasurementList = pmDataModel
				.getPmMeasurementList();
		// List<Map<String, Object>> toAddCompareValueList = new
		// ArrayList<Map<String, Object>>();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		int targetType = pmDataModel.getTargetType();
		conditionMap.put("TARGET_TYPE", targetType);
		if (targetType == CommonDefine.PM.TARGET_TYPE.UNIT) {
			conditionMap.put("BASE_UNIT_ID", pmDataModel.getUnitId());
		} else if (targetType == CommonDefine.PM.TARGET_TYPE.CTP_OTN) {
			conditionMap.put("BASE_OTN_CTP_ID", pmDataModel.getOtnCtpId());
		} else if (targetType == CommonDefine.PM.TARGET_TYPE.CTP_SDH) {
			conditionMap.put("BASE_SDH_CTP_ID", pmDataModel.getSdhCtpId());
		} else if (targetType == CommonDefine.PM.TARGET_TYPE.PTP) {
			conditionMap.put("BASE_PTP_ID", pmDataModel.getPtpId());
		}
		for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
			if (Integer.parseInt(pmMeasurementModel.getType()) == 1) {
				// 只处理物理量
				conditionMap.put("PM_STD_INDEX",
						pmMeasurementModel.getPmStdIndex());
				List<Map> compareValueList = performanceManagerMapper
						.getCompareValue(conditionMap);
				if (compareValueList.size() > 0) {
					Map compareValue = compareValueList.get(0);
					double pmCompareValue = Double.parseDouble(compareValue
							.get("PM_COMPARE_VALUE").toString());
					if (pmCompareValue == -60.00) {
						// 无论如何都覆盖原基准值
						compareValue.put("PM_COMPARE_VALUE",
								pmMeasurementModel.getValue());
						compareValue.put("UPDATE_TIME",
								new Timestamp(System.currentTimeMillis()));
						performanceManagerMapper.modifyPmCompare(compareValue);
						continue;
					} else {
						if (overwrite == CommonDefine.TRUE) {
							compareValue.put("PM_COMPARE_VALUE",
									pmMeasurementModel.getValue());
							compareValue.put("UPDATE_TIME", new Timestamp(
									System.currentTimeMillis()));
							performanceManagerMapper
									.modifyPmCompare(compareValue);
							continue;
						}
					}
				}
				// toAddCompareValueList.add(newACompareValue(pmDataModel,
				// pmMeasurementModel, targetType));
			}
		}
		// if (toAddCompareValueList.size() > 0) {
		// performanceManagerMapper.insertPmCompare(toAddCompareValueList);
		// }
	}

	// 新比较值对象
	private Map<String, Object> newACompareValue(PmDataModel pmDataModel,
			PmMeasurementModel pmMeasurementModel, int targetType) {
		Map<String, Object> aNewCompareValue = new HashMap<String, Object>();
		aNewCompareValue.put("TARGET_TYPE", targetType);
		aNewCompareValue
				.put("PM_STD_INDEX", pmMeasurementModel.getPmStdIndex());
		aNewCompareValue.put("PM_DESCRIPTION",
				pmMeasurementModel.getPmdescription());
		aNewCompareValue.put("BASE_NE_ID", pmDataModel.getNeId());
		aNewCompareValue.put("UPDATE_TIME",
				new Timestamp(System.currentTimeMillis()));
		aNewCompareValue.put("PM_COMPARE_VALUE", pmMeasurementModel.getValue());
		if (targetType == CommonDefine.PM.TARGET_TYPE.UNIT) {
			aNewCompareValue.put("BASE_UNIT_ID", pmDataModel.getUnitId());
		} else if (targetType == CommonDefine.PM.TARGET_TYPE.CTP_OTN) {
			aNewCompareValue.put("BASE_UNIT_ID", pmDataModel.getUnitId());
			aNewCompareValue.put("BASE_OTN_CTP_ID", pmDataModel.getOtnCtpId());
			aNewCompareValue.put("DISPLAY_CTP", pmDataModel.getDisplayCtp());
		} else if (targetType == CommonDefine.PM.TARGET_TYPE.CTP_SDH) {
			aNewCompareValue.put("BASE_UNIT_ID", pmDataModel.getUnitId());
			aNewCompareValue.put("BASE_SDH_CTP_ID", pmDataModel.getSdhCtpId());
			aNewCompareValue.put("DISPLAY_CTP", pmDataModel.getDisplayCtp());
		} else if (targetType == CommonDefine.PM.TARGET_TYPE.PTP) {
			aNewCompareValue.put("BASE_UNIT_ID", pmDataModel.getUnitId());
			aNewCompareValue.put("BASE_PTP_ID", pmDataModel.getPtpId());
		}
		return aNewCompareValue;
	}

	// 获取网元ID列表
	@SuppressWarnings("rawtypes")
	public List<Integer> getNeIdsFromNodes(List<Map> nodeList) {
		Map<String, String> conditionMap = new HashMap<String, String>();
		for (Map m : nodeList) {
			int nodeLevel = Integer.parseInt(m.get("nodeLevel").toString());

			String conditionKey = changeNodeLevelFromIntToString(nodeLevel);
			String nodeId = m.get("nodeId").toString();
			String conditionValue;
			if (nodeLevel == CommonDefine.TREE.NODE.SUBNET) {
				// 子网递归处理获取其所有子子网节点
				List<Integer> allSubnetList = getSubnetChildren(Integer
						.parseInt(nodeId));
				String subnetString = allSubnetList.toString();
				conditionValue = subnetString.substring(1,
						subnetString.length() - 1);
			} else {
				// 其余层级直接使用id
				conditionValue = nodeId;
			}
			if (conditionMap.containsKey(conditionKey)) {
				conditionMap.put(conditionKey, conditionMap.get(conditionKey)
						+ ',' + conditionValue);
			} else {
				conditionMap.put(conditionKey, conditionValue);
			}
		}
		return performanceManagerMapper.getNeIdsFromNodes(conditionMap);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setCompareValueFromPm(List<Map> pmList) throws CommonException {
		try {
			List<Map<String, Object>> toAddCompareValueList = new ArrayList<Map<String, Object>>();
			Integer targetType;
			for (Map pm : pmList) {
				// 设置更新时间
				pm.put("UPDATE_TIME", new Timestamp(System.currentTimeMillis()));
				// 查询是否存在比较值
				targetType = Integer.parseInt(pm.get("TARGET_TYPE").toString());
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("TARGET_TYPE", targetType);
				if (targetType == CommonDefine.PM.TARGET_TYPE.UNIT) {
					conditionMap
							.put("BASE_UNIT_ID", Integer.parseInt(pm.get(
									"BASE_UNIT_ID").toString()));
				} else if (targetType == CommonDefine.PM.TARGET_TYPE.CTP_OTN) {
					conditionMap.put("BASE_OTN_CTP_ID", Integer.parseInt(pm
							.get("BASE_OTN_CTP_ID").toString()));
				} else if (targetType == CommonDefine.PM.TARGET_TYPE.CTP_SDH) {
					conditionMap.put("BASE_SDH_CTP_ID", Integer.parseInt(pm
							.get("BASE_SDH_CTP_ID").toString()));
				} else if (targetType == CommonDefine.PM.TARGET_TYPE.PTP) {
					conditionMap.put("BASE_PTP_ID",
							Integer.parseInt(pm.get("BASE_PTP_ID").toString()));
				}
				conditionMap.put("PM_STD_INDEX", pm.get("PM_STD_INDEX")
						.toString());
				List<Map> compareValueList = performanceManagerMapper
						.getCompareValue(conditionMap);
				if (compareValueList.size() > 0) {
					// 存在比较值,更新
					Map compareValue = compareValueList.get(0);
					// 无论如何都覆盖原基准值
					compareValue.put("PM_COMPARE_VALUE", pm.get("PM_VALUE"));
					performanceManagerMapper.modifyPmCompare(compareValue);
					continue;
				} else {
					// 不存在比较值,新增
					pm.put("PM_COMPARE_VALUE", pm.get("PM_VALUE"));
					toAddCompareValueList.add(pm);
				}
			}
			if (toAddCompareValueList.size() > 0) {
				performanceManagerMapper.insertPmCompare(toAddCompareValueList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getIndexPagePmInfo(int userId) throws CommonException {
		Map returnMap = new HashMap();
		try {
			// 获得昨日日期
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			Timestamp now = getDateWithoutTime(calendar.getTime());
			// 日期格式化
			SimpleDateFormat startFormat = new SimpleDateFormat(
					CommonDefine.COMMON_START_FORMAT);
			SimpleDateFormat endFormat = new SimpleDateFormat(
					CommonDefine.COMMON_END_FORMAT);
			String yearAndMonth = new SimpleDateFormat("yyyy_MM").format(now);
			String start = startFormat.format(now);
			String end = endFormat.format(now);
			// String yearAndMonth = "2014_01";
			// String start = "2014-01-01 00:00:00";
			// String end = "2014-01-31 00:00:00";
			// 返回变量
			int collectFailedNe = 0, collectSucceedNe = 0, pmException1 = 0, pmException2 = 0, pmException3 = 0;
			List<Map> allowedEmses = commonManagerService
					.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL,
							false, true);
			StringBuffer sb = new StringBuffer();
			for (Map ems : allowedEmses) {
				Integer emsId = (Integer) ems.get("BASE_EMS_CONNECTION_ID");
				sb.append(emsId);
				sb.append(",");
				String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
						+ "_" + emsId + "_" + yearAndMonth;
				// 表存在？
				Integer existance = performanceManagerMapper
						.getPmTableExistance(tableName,
								SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
				if (existance != null && existance == 1) {
					// 异常
					pmException1 += performanceManagerMapper.getTaskPmCount(
							tableName, null,
							CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_1,
							start, end, null, null);
					pmException2 += performanceManagerMapper.getTaskPmCount(
							tableName, null,
							CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_2,
							start, end, null, null);
					pmException3 += performanceManagerMapper.getTaskPmCount(
							tableName, null,
							CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_3,
							start, end, null, null);
				}
			}
			String emsIds = sb.toString();
			if (emsIds.length() > 0) {
				emsIds = emsIds.substring(0, emsIds.length() - 1);
				collectFailedNe = performanceManagerMapper
						.getCollectResultNeCountWithAuthority(emsIds,
								CommonDefine.FALSE, now);
				collectSucceedNe = performanceManagerMapper
						.getCollectResultNeCountWithAuthority(emsIds,
								CommonDefine.TRUE, now);
			}
			returnMap.put("collectedNe", collectFailedNe + collectSucceedNe);
			returnMap.put("collectSucceedNe", collectSucceedNe);
			returnMap.put("pmException1", pmException1);
			returnMap.put("pmException2", pmException2);
			returnMap.put("pmException3", pmException3);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnMap;
	}

	public Map calculateReportCountInfo(int taskId, Date startTime, Date endTime)
			throws CommonException {

		return calculateReportCountInfo(taskId, startTime, endTime,
				Calendar.DAY_OF_YEAR);
	}

	@Override
	public Map calculateReportCountInfo(int taskId, Date startTime,
			Date endTime, int stepPath) throws CommonException {
		// for test only
		// SimpleDateFormat format = new SimpleDateFormat(
		// CommonDefine.COMMON_FORMAT);
		// try {
		// startTime = format.parse("2014-01-01 00:00:00");
		// endTime = format.parse("2014-01-01 00:00:00");
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// 返回对象
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try {
			// 先获取任务ID和类型
			List<Map> targetList = performanceManagerMapper
					.getTaskTargetIds(taskId);
			Integer taskType = (Integer) targetList.get(0).get("TARGET_TYPE");
			// 设置日历对象
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startTime);
			// 日期格式化
			SimpleDateFormat startFormat = new SimpleDateFormat(
					CommonDefine.COMMON_START_FORMAT);
			SimpleDateFormat endFormat = new SimpleDateFormat(
					CommonDefine.COMMON_END_FORMAT);
			SimpleDateFormat yearAndMonth = new SimpleDateFormat("yyyy_MM");
			// 小数格式化
			DecimalFormat decimalFormatter = new DecimalFormat("0%");
			if (taskType == CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE) {
				// 目标是干线
				String turnkLineIdString = getTargetIds(targetList);
				List<Integer> multiSecIdList = performanceManagerMapper
						.getMultiSecIds(turnkLineIdString);
				if (multiSecIdList.size() > 0) {
					String multiSecIdString = multiSecIdList.toString();
					multiSecIdString = multiSecIdString.substring(1,
							multiSecIdString.length() - 1);
					// 按照复用段统计
					returnResult = analysisMultiSecCountInfo(startFormat,
							endFormat, decimalFormatter, yearAndMonth,
							calendar, endTime, multiSecIdString,
							multiSecIdList.size(), stepPath);

				} else {
					returnResult.put("PM_EXCEPTION_LV1", 0);
					returnResult.put("PM_EXCEPTION_LV2", 0);
					returnResult.put("PM_EXCEPTION_LV3", 0);
					returnResult.put("PM_ABNORMAL_RATE",
							decimalFormatter.format(0));
					returnResult.put("COLLECT_SUCCESS_RATE_PTP",
							decimalFormatter.format(0));
					returnResult.put("COLLECT_SUCCESS_RATE_MULTISEC",
							decimalFormatter.format(0));
					returnResult.put("FAILED_ID_PTP", null);
					returnResult.put("FAILED_ID_MULTI_SEC", null);
				}
				returnResult
						.put("REPORT_TYPE",
								CommonDefine.PM.PM_REPORT.REPORT_TYPE.TRUNK_LINE_REPORT);

			} else if (taskType == CommonDefine.TASK_TARGET_TYPE.MULTI_SEC) {
				// 目标是复用段
				// 复用段ID列表
				String multiSecIdString = getTargetIds(targetList);
				// 按照复用段统计
				returnResult = analysisMultiSecCountInfo(startFormat,
						endFormat, decimalFormatter, yearAndMonth, calendar,
						endTime, multiSecIdString, targetList.size(), stepPath);
				// 任务目标类型
				returnResult
						.put("REPORT_TYPE",
								CommonDefine.PM.PM_REPORT.REPORT_TYPE.TRUNK_LINE_REPORT);

			} else {
				// 目标是网元
				// Map<Integer, Map<String, Object>> conditionMaps =
				// getConditionsFromNodesGroupByEmsIds(targetList);
				List<Integer> emsIdList = new ArrayList<Integer>();
				List<Map> neIdList = getNeIds(targetList);
				String neIdString = analysisNeList(neIdList, emsIdList);
				Map pmIndex = performanceManagerMapper.getTaskPmIndexes(taskId);
				StringBuffer sdhPmSb = new StringBuffer();
				StringBuffer wdmPmSb = new StringBuffer();
				String sdhPm = null, wdmPm = null;
				if (pmIndex != null && pmIndex.get("SDH_PM") != null
						&& !"".equals(pmIndex.get("SDH_PM"))) {
					String[] sdhArray = pmIndex.get("SDH_PM").toString()
							.split(",");
					for (String s : sdhArray) {
						sdhPmSb.append("'").append(s).append("',");
					}
					sdhPm = sdhPmSb.deleteCharAt(sdhPmSb.length() - 1)
							.toString();
				}
				if (pmIndex != null && pmIndex.get("WDM_PM") != null
						&& !"".equals(pmIndex.get("WDM_PM"))) {
					String[] wdmArray = pmIndex.get("WDM_PM").toString()
							.split(",");
					for (String s : wdmArray) {
						wdmPmSb.append("'").append(s).append("',");
					}
					wdmPm = wdmPmSb.deleteCharAt(wdmPmSb.length() - 1)
							.toString();
				}

				// 统计用变量
				double neSucceessRate = 1d, pmSucceessRate = 1d;
				Set<Integer> failedNeIdSet = new HashSet<Integer>();
				int pmOk = 0, pmException1 = 0, pmException2 = 0, pmException3 = 0;
				int neCount = 0, failedNeCount = 0, collectedNeCount = 0;
				// 按天循环
				while (calendar.getTimeInMillis() <= endTime.getTime()) {
					Timestamp now = getDateWithoutTime(calendar.getTime());
					// 采集网元总数
					neCount += neIdList.size();
					collectedNeCount += performanceManagerMapper
							.getCollectResultNeCount(neIdString, now);
					// 采集失败网元列表
					List<Integer> failedNeIdList = performanceManagerMapper
							.getCollectResultNeIds(neIdString,
									CommonDefine.FALSE, now);
					failedNeCount += failedNeIdList.size();
					failedNeIdSet.addAll(failedNeIdList);
					// 性能
					String start = startFormat.format(now);
					String end = endFormat.format(now);
					for (int emsId : emsIdList) {
						String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
								+ "_" + emsId + "_" + yearAndMonth.format(now);
						// 表存在？
						Integer existance = performanceManagerMapper
								.getPmTableExistance(tableName,
										SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
						if (existance != null && existance == 1) {
							// 正常
							pmOk += performanceManagerMapper.getTaskPmCount(
									tableName, neIdString,
									CommonDefine.PM.PM_EXCEPTION_LEVEL.NORMAL,
									start, end, sdhPm, wdmPm);
							// 异常
							pmException1 += performanceManagerMapper
									.getTaskPmCount(
											tableName,
											neIdString,
											CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_1,
											start, end, sdhPm, wdmPm);
							pmException2 += performanceManagerMapper
									.getTaskPmCount(
											tableName,
											neIdString,
											CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_2,
											start, end, sdhPm, wdmPm);
							pmException3 += performanceManagerMapper
									.getTaskPmCount(
											tableName,
											neIdString,
											CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_3,
											start, end, sdhPm, wdmPm);
						}
					}
					// 增加至下一天
					calendar.add(stepPath, 1);
				}
				if (neCount > 0) {
					neSucceessRate = (((double) (collectedNeCount - failedNeCount)) / neCount);
				}
				double pmCount = pmException1 + pmException2 + pmException3
						+ pmOk;
				Integer pmCountInt = pmException1 + pmException2 + pmException3
						+ pmOk;
				if (pmCount > 0) {
					pmSucceessRate = 1.0d
							- ((double) pmException1 + pmException2 + pmException3)
							/ pmCount;
				}
				// 添加正确率数值格式转化与统计信息的保存
				returnResult.put("REPORT_TYPE",
						CommonDefine.PM.PM_REPORT.REPORT_TYPE.NE_REPORT);
				returnResult.put("PM_EXCEPTION_LV1", pmException1);
				returnResult.put("PM_EXCEPTION_LV2", pmException2);
				returnResult.put("PM_EXCEPTION_LV3", pmException3);
				returnResult.put(
						"PM_ABNORMAL_RATE",
						decimalFormatter.format(pmSucceessRate) + "("
								+ String.valueOf(pmOk) + "/"
								+ pmCountInt.toString() + ")");
				returnResult.put("COLLECT_SUCCESS_RATE",
						decimalFormatter.format(neSucceessRate) + "("
								+ (collectedNeCount - failedNeCount) + "/"
								+ neCount + ")");
				String failedId = failedNeIdSet.toString();
				returnResult.put("FAILED_ID",
						failedId.substring(1, failedId.length() - 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

		return returnResult;
	}

	// 按照复用段分析统计结果
	private Map<String, Object> analysisMultiSecCountInfo(
			SimpleDateFormat startFormat, SimpleDateFormat endFormat,
			DecimalFormat decimalFormatter, SimpleDateFormat yearAndMonth,
			Calendar calendar, Date endTime, String multiSecIdString,
			Integer targetListSize, int stepPath) throws ParseException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		// 统计用变量
		double multiSecSuccessRate = 1d, ptpSucceessRate = 1d, pmSucceessRate = 1d;
		String failedPtpString, failedMultiSecString;
		// Set<Integer> succeedPtpIdSet = new HashSet<Integer>();
		Set<Integer> failedMultiSecIdSet = new HashSet<Integer>();
		Set<Integer> succeedMultiSecIdSet = new HashSet<Integer>();
		Integer pmOk = 0, pmException1 = 0, pmException2 = 0, pmException3 = 0;
		Integer ptpCount = 0, succeedPtpCount = 0, collectedPtpCount = 0;
		// 任务指定的目标端口
		List<Map> ptpIdList = performanceManagerMapper
				.getTaskTargetPtpIds(multiSecIdString);
		// 筛选出网管ID信息与PTP列表
		List<Integer> emsIdList = new ArrayList<Integer>();
		Set<Integer> allPtpIdSet = new HashSet<Integer>();
		for (Map ptp : ptpIdList) {
			allPtpIdSet.add(Integer.valueOf(ptp.get("PTP_ID").toString()));
		}
		String ptpIdString = analysisPtpList(ptpIdList, emsIdList);
		// 查询性能用的输入光功率/输出光功率端口列表
		List<Integer> inPtps = performanceManagerMapper
				.getTaskTargetPtpIdsForPM(multiSecIdString, 1);
		List<Integer> outPtps = performanceManagerMapper
				.getTaskTargetPtpIdsForPM(multiSecIdString, 2);
		String inPtpString = null, outPtpString = null;
		if (inPtps.size() > 0) {
			inPtpString = inPtps.toString();
			inPtpString = inPtpString.substring(1, inPtpString.length() - 1);
		}
		if (outPtps.size() > 0) {
			outPtpString = outPtps.toString();
			outPtpString = outPtpString.substring(1, outPtpString.length() - 1);
		}
		// 按天循环
		while (calendar.getTimeInMillis() <= endTime.getTime()) {
			Timestamp now = getDateWithoutTime(calendar.getTime());
			// 采集端口总数
			ptpCount += ptpIdList.size();
			collectedPtpCount += performanceManagerMapper
					.getCollectResultPtpCount(multiSecIdString, ptpIdString,
							now);
			// 采集成功复用段ID列表
			succeedMultiSecIdSet.addAll(performanceManagerMapper
					.getFailedMultiSecIds(multiSecIdString, CommonDefine.TRUE,
							now));
			// 采集失败复用段ID列表
			failedMultiSecIdSet.addAll(performanceManagerMapper
					.getFailedMultiSecIds(multiSecIdString, CommonDefine.FALSE,
							now));
			// 采集成功端口ID列表
			List<Integer> succeedPtpList = performanceManagerMapper
					.getCollectResultPtpIds(multiSecIdString, ptpIdString,
							CommonDefine.TRUE, now);
			succeedPtpCount += succeedPtpList.size();
			allPtpIdSet.removeAll(succeedPtpList);
			// succeedPtpIdSet.addAll(succeedPtpList);
			// 性能
			String start = startFormat.format(now);
			String end = endFormat.format(now);
			for (int emsId : emsIdList) {
				String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
						+ "_" + emsId + "_" + yearAndMonth.format(now);
				// 表存在？
				Integer existance = performanceManagerMapper
						.getPmTableExistance(tableName,
								SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
				if (existance != null && existance == 1) {
					if (inPtpString != null) {
						// 正常
						pmOk += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										inPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.NORMAL,
										start, end, "RPL_CUR");
						// 异常
						pmException1 += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										inPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_1,
										start, end, "RPL_CUR");
						pmException2 += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										inPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_2,
										start, end, "RPL_CUR");
						pmException3 += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										inPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_3,
										start, end, "RPL_CUR");
					}
					if (outPtpString != null) {
						// 正常
						pmOk += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										outPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.NORMAL,
										start, end, "TPL_CUR");
						// 异常
						pmException1 += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										outPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_1,
										start, end, "TPL_CUR");
						pmException2 += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										outPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_2,
										start, end, "TPL_CUR");
						pmException3 += performanceManagerMapper
								.getMultiSecTaskPmCount(
										tableName,
										outPtpString,
										CommonDefine.PM.PM_EXCEPTION_LEVEL.EXCEPTION_3,
										start, end, "TPL_CUR");
					}

				}
			}
			// 增加至下一天
			calendar.add(stepPath, 1);
		}
		// 处理统计结果
		if (targetListSize > 0) {
			multiSecSuccessRate = (((double) succeedMultiSecIdSet.size()) / ((double) targetListSize));
		}
		if (ptpCount > 0) {
			ptpSucceessRate = (succeedPtpCount.doubleValue() / ptpCount);
		}
		double pmCount = pmOk + pmException1 + pmException2 + pmException3;
		Integer pmCountInt = pmOk + pmException1 + pmException2 + pmException3;
		if (pmCount > 0) {
			pmSucceessRate = 1.0d
					- (pmException1.doubleValue() + pmException2 + pmException3)
					/ pmCount;
		}
		failedPtpString = allPtpIdSet.toString();
		failedMultiSecString = failedMultiSecIdSet.toString();
		returnResult.put("PM_EXCEPTION_LV1", pmException1);
		returnResult.put("PM_EXCEPTION_LV2", pmException2);
		returnResult.put("PM_EXCEPTION_LV3", pmException3);
		returnResult.put("PM_ABNORMAL_RATE",
				decimalFormatter.format(pmSucceessRate) + "(" + pmOk.toString()
						+ "/" + pmCountInt.toString() + ")");
		returnResult.put("COLLECT_SUCCESS_RATE_PTP",
				decimalFormatter.format(ptpSucceessRate) + "("
						+ (succeedPtpCount) + "/" + ptpCount + ")");
		returnResult.put("COLLECT_SUCCESS_RATE_MULTISEC",
				decimalFormatter.format(multiSecSuccessRate) + "("
						+ (succeedMultiSecIdSet.size()) + "/" + targetListSize
						+ ")");
		returnResult.put("FAILED_ID_PTP",
				failedPtpString.substring(1, failedPtpString.length() - 1));
		returnResult.put("FAILED_ID_MULTI_SEC", failedMultiSecString.substring(
				1, failedMultiSecString.length() - 1));
		return returnResult;
	}

	// 由端口获取查询用条件
	private String analysisPtpList(List<Map> ptpList, List<Integer> emsIdList) {
		StringBuffer ptpSb = new StringBuffer();
		Integer lastEmsId = -1;
		for (Map ptp : ptpList) {
			Integer currentEmsId = (Integer) ptp.get("BASE_EMS_CONNECTION_ID");
			if (!currentEmsId.equals(lastEmsId)) {
				emsIdList.add(currentEmsId);
				lastEmsId = currentEmsId;
			}
			if (!"".equals(ptp.get("PTP_ID"))) {
				ptpSb.append(ptp.get("PTP_ID"));
				ptpSb.append(",");
			}

		}
		if (ptpSb.length() > 0) {
			return ptpSb.toString().substring(0, ptpSb.length() - 1);
		} else {
			return "";
		}
	}

	// 将目标ID转化为in条件字符串
	private String getTargetIds(List<Map> targetList) {
		StringBuffer sb = new StringBuffer();
		for (Map target : targetList) {
			sb.append(target.get("TARGET_Id"));
			sb.append(",");
		}
		String targetIds = sb.toString();
		return targetIds.substring(0, targetIds.length() - 1);
	}

	// 由任务target取得网元
	private List<Map> getNeIds(List<Map> targetList) {
		Map<String, String> conditionMap = new HashMap<String, String>();
		for (Map target : targetList) {
			int nodeLevel = (Integer) target.get("TARGET_TYPE");
			String conditionKey = changeNodeLevelFromIntToString(nodeLevel);
			String nodeId = target.get("TARGET_Id").toString();
			String conditionValue;
			if (nodeLevel == CommonDefine.TREE.NODE.SUBNET) {
				// 子网递归处理获取其所有子子网节点
				List<Integer> allSubnetList = getSubnetChildren(Integer
						.parseInt(nodeId));
				String subnetString = allSubnetList.toString();
				conditionValue = subnetString.substring(1,
						subnetString.length() - 1);
			} else {
				// 其余层级直接使用id
				conditionValue = nodeId;
			}
			if (conditionMap.containsKey(conditionKey)) {
				conditionMap.put(conditionKey, conditionMap.get(conditionKey)
						+ ',' + conditionValue);
			} else {
				conditionMap.put(conditionKey, conditionValue);
			}
		}
		return performanceManagerMapper.getTaskTargetNeIds(REPORT_DEFINE,
				conditionMap);
	}

	// 由网元获取查询用条件
	private String analysisNeList(List<Map> neList, List<Integer> emsIdList) {
		StringBuffer neSb = new StringBuffer();
		Integer lastEmsId = -1;
		for (Map ne : neList) {
			Integer currentEmsId = (Integer) ne.get("BASE_EMS_CONNECTION_ID");
			if (!currentEmsId.equals(lastEmsId)) {
				lastEmsId = currentEmsId;
				emsIdList.add(currentEmsId);
			}
			neSb.append(ne.get("BASE_NE_ID"));
			neSb.append(",");
		}
		if (neSb.length() > 0) {
			return neSb.toString().substring(0, neSb.length() - 1);
		} else {
			return "";
		}
	}

	// 消灭时分秒
	private Timestamp getDateWithoutTime(Date d) throws ParseException {
		SimpleDateFormat dayFormat = new SimpleDateFormat(
				CommonDefine.COMMON_FORMAT);// 计算上次采集间隔时间用
		SimpleDateFormat toStringFormat = new SimpleDateFormat(
				CommonDefine.COMMON_SIMPLE_FORMAT);// 计算上次采集间隔时间用
		return new Timestamp(dayFormat.parse(
				toStringFormat.format(d) + " 00:00:00").getTime());
	}

	// ***********************************咯咯咯咯咯咯***************************************

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> getTemplates(Map condMap) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		int factory = Integer.parseInt(condMap.get("factory").toString());
		try {
			returnList = performanceManagerMapper.getTemplates(factory,
					RegularPmAnalysisDefine);
			if ("1".equals(condMap.get("needNull"))) {
				Map nullRecord = new HashMap<String, Object>();
				nullRecord.put("PM_TEMPLATE_ID", -99);
				nullRecord.put("TEMPLATE_NAME", "无模板");
				nullRecord.put("FACTORY", 0);
				returnList.add(0, nullRecord);
			}
			if ("1".equals(condMap.get("needAll"))) {
				Map allRecord = new HashMap<String, Object>();
				allRecord.put("PM_TEMPLATE_ID", 0);
				allRecord.put("TEMPLATE_NAME", "全部");
				allRecord.put("FACTORY", 0);
				returnList.add(0, allRecord);
			}

			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
			// returnData.put("msg", "test");
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchPtpTemplate(List<Map> nodeList,
			Map<String, String> searchCond) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap = processNodesToConditions(null, searchCond, nodeList);

		try {
			if (Integer.parseInt(searchCond.get("searchLevel").toString()) == 1) {
			returnList = performanceManagerMapper.searchPtpTemplate(
					conditionMap, RegularPmAnalysisDefine);
			int count = performanceManagerMapper.searchPtpTemplateCount(
					conditionMap, RegularPmAnalysisDefine);
			returnData.put("total", count);
			returnData.put("rows", returnList);
			} else {
				returnList = performanceManagerMapper.searchUnitTemplate(
						conditionMap, RegularPmAnalysisDefine);
				int count = performanceManagerMapper.searchUnitTemplateCount(
						conditionMap, RegularPmAnalysisDefine);
				returnData.put("total", count);
				returnData.put("rows", returnList);
			}

		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	/**
	 * 将nodeLevel从数字变为表示名称的字符串
	 * 
	 * @param nodeLevel
	 * @return
	 */
	private String changeNodeLevelFromIntToString(int nodeLevel) {
		String returnStr = "";
		switch (nodeLevel) {
		case CommonDefine.TREE.NODE.ROOT:
			returnStr = "NODE_ROOT";
			break;
		case CommonDefine.TREE.NODE.EMSGROUP:
			returnStr = "NODE_EMSGROUP";
			break;
		case CommonDefine.TREE.NODE.EMS:
			returnStr = "NODE_EMS";
			break;
		case CommonDefine.TREE.NODE.SUBNET:
			returnStr = "NODE_SUBNET";
			break;
		case CommonDefine.TREE.NODE.NE:
			returnStr = "NODE_NE";
			break;
		case CommonDefine.TREE.NODE.SHELF:
			returnStr = "NODE_SHELF";
			break;
		case CommonDefine.TREE.NODE.UNIT:
			returnStr = "NODE_UNIT";
			break;
		case CommonDefine.TREE.NODE.SUBUNIT:
			returnStr = "NODE_SUBUNIT";
			break;
		case CommonDefine.TREE.NODE.PTP:
			returnStr = "NODE_PTP";
			break;
		}
		return returnStr;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void savePtpTemplate(List<Map> list, int level)
			throws CommonException {
		try {
			if (level == 1) {
			for (Map m : list) {
				if (m.get("templateId").toString().equals("-99"))
					m.remove("templateId");
				performanceManagerMapper.savePtpTemplate(m);
			}
			} else if (level == 2) {
				for (Map m : list) {
					if (m.get("templateId").toString().equals("-99"))
						m.remove("templateId");
					performanceManagerMapper.saveUnitTemplate(m);
				}
			}

		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@Override
	public void cancelPtpTemplate(List<Long> list, int level)
			throws CommonException {
		try {
			if (level == 1) {
			for (Long ptpId : list) {
				performanceManagerMapper.cancelPtpTemplate(ptpId);
			}
			} else if (level == 2) {
				for (Long unitId : list) {
					performanceManagerMapper.cancelUnitTemplate(unitId);
				}
			}

		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getTemplatesInfo(int factory, int start,
			int limit) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getTemplatesInfo(factory,
					RegularPmAnalysisDefine, start, limit);
			int count = performanceManagerMapper.getTemplatesInfoCount(factory,
					RegularPmAnalysisDefine);
			returnData.put("total", count);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean applyTemplate(List<Map> list, Map<String, String> searchCond)
			throws CommonException {
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap = processNodesToConditions(null, searchCond, list);
		try {
			List<Integer> factoryList = performanceManagerMapper.getFactory(
					conditionMap, RegularPmAnalysisDefine);
			if (factoryList != null) {
				for (Integer i : factoryList) {
					if (!i.equals(new Integer(searchCond.get("factory"))))
						return false;
				}
			}
			performanceManagerMapper.applyTemplate(conditionMap,
					RegularPmAnalysisDefine);
			performanceManagerMapper.applyTemplateForUnit(conditionMap,
					RegularPmAnalysisDefine);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void cancelTemplateBatch(List<Map> list, Map searchCond)
			throws CommonException {
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap = processNodesToConditions(null, searchCond, list);
		try {
			if (searchCond.get("searchLevel") == null) {
				List<Map> ptpId = performanceManagerMapper
						.getPtpIdForBatchDetach(conditionMap,
								RegularPmAnalysisDefine);
				performanceManagerMapper.cancelTemplateBatch(ptpId);

				List<Map> unitId = performanceManagerMapper
						.getUnitIdForBatchDetach(conditionMap,
								RegularPmAnalysisDefine);
				performanceManagerMapper.cancelTemplateBatchUnit(unitId);
			} else if (searchCond.get("searchLevel").toString().equals("1")) {
				List<Map> ptpId = performanceManagerMapper
						.getPtpIdForBatchDetach(conditionMap,
								RegularPmAnalysisDefine);
			performanceManagerMapper.cancelTemplateBatch(ptpId);
			} else if (searchCond.get("searchLevel").toString().equals("2")) {
				List<Map> unitId = performanceManagerMapper
						.getUnitIdForBatchDetach(conditionMap,
								RegularPmAnalysisDefine);
				performanceManagerMapper.cancelTemplateBatchUnit(unitId);
			}

		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getNumberic(int templateId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getNumberic(templateId,
					CommonDefine.PM.PM_TYPE.COUNT_VALUE);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getPhysical(int templateId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getPhysical(templateId,
					CommonDefine.PM.PM_TYPE.PHYSICAL);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public String newTemplate(int templateId, String templateName)
			throws CommonException {
		// 返回的新Id
		Map<String, Long> idMap = new HashMap<String, Long>();
		idMap.put("newId", null);
		String result;
		try {
			int count = performanceManagerMapper.isTemplateNameExist(
					templateName, RegularPmAnalysisDefine);
			if (count != 0)
				return "duplicate";
			performanceManagerMapper.newTemplate(templateId, templateName,
					RegularPmAnalysisDefine, idMap);
			performanceManagerMapper.newTemplateDetail(templateId,
					idMap.get("newId"));
			result = idMap.get("newId").toString();
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void saveNumberic(List<Map> list) throws CommonException {
		try {
			for (Map m : list) {
				performanceManagerMapper.saveNumberic(m);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void savePhysical(List<Map> list) throws CommonException {
		try {
			for (Map m : list) {
				performanceManagerMapper.savePhysical(m);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@Override
	public boolean deleteTemplate(List<Long> templateIdList)
			throws CommonException {
		try {
			for (Long id : templateIdList) {
				int applied = performanceManagerMapper.checkIfTemplateApplied(
						id, RegularPmAnalysisDefine);
				if (applied != 0) {
					return false;
				}
				performanceManagerMapper.deleteTemplateDetail(id);
				performanceManagerMapper.deleteTemplate(id);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return true;
	}

	@Override
	public void detachTemplate(List<Long> templateIdList, Integer userId)
			throws CommonException {
		try {
			for (Long id : templateIdList) {
				performanceManagerMapper.detachTemplate(id, userId,
						CommonDefine.TREE.TREE_DEFINE);
				performanceManagerMapper.detachTemplateUnit(id, userId,
						CommonDefine.TREE.TREE_DEFINE);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	@Override
	public String generateDiagramNend(Map<String, String> searchCond)
			throws CommonException {
		// HttpServletRequest request = ServletActionContext.getRequest();
		List<Map> returnList = new ArrayList<Map>();
		String[] pmStdIndex = searchCond.get("pmStdIndex").split(",");
		Map map = new HashMap<String, Object>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat sdfEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> tableNameListTemp = new ArrayList();
		List<String> tableNameList = new ArrayList();
		// 性能查询开始时间
		Date startTime = new Date();
		Date endTime = new Date();
		try {
			startTime = sdf.parse(searchCond.get("startTime"));
			cal.setTime(startTime);
			switch (Integer.valueOf(searchCond.get("timeRange"))) {
			case 1:// 前10天
				cal.add(Calendar.DAY_OF_YEAR, -9);
				endTime = cal.getTime();
				searchCond.put("endTime", sdfEnd.format(startTime));
				searchCond.put("startTime", sdfStart.format(endTime));
				break;
			case 2:// 前20天
				cal.add(Calendar.DAY_OF_YEAR, -19);
				endTime = cal.getTime();
				searchCond.put("endTime", sdfEnd.format(startTime));
				searchCond.put("startTime", sdfStart.format(endTime));
				break;
			case 3:// 前30天
				cal.add(Calendar.DAY_OF_YEAR, -29);
				endTime = cal.getTime();
				searchCond.put("endTime", sdfEnd.format(startTime));
				searchCond.put("startTime", sdfStart.format(endTime));
				break;
			case 4:// 后10天
				cal.add(Calendar.DAY_OF_YEAR, 9);
				endTime = cal.getTime();
				searchCond.put("startTime", sdfStart.format(startTime));
				searchCond.put("endTime", sdfEnd.format(endTime));
				break;
			case 5:// 后20天
				cal.add(Calendar.DAY_OF_YEAR, 19);
				endTime = cal.getTime();
				searchCond.put("startTime", sdfStart.format(startTime));
				searchCond.put("endTime", sdfEnd.format(endTime));
				break;
			case 6:// 后30天
				cal.add(Calendar.DAY_OF_YEAR, 29);
				endTime = cal.getTime();
				searchCond.put("startTime", sdfStart.format(startTime));
				searchCond.put("endTime", sdfEnd.format(endTime));
				break;
			}
			tableNameListTemp = getPmTableName(startTime, endTime,
					searchCond.get("emsConnectionId"));

			for (String tableName : tableNameListTemp) {
				Integer existance = performanceManagerMapper
						.getPmTableExistance(tableName,
								SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
				if (existance != null && existance == 1) {
					tableNameList.add(tableName);
				}
			}
			String title = "";
			Document document = DocumentHelper.createDocument();
			// graph标签
			Element graphElement = document.addElement("graph");
			graphElement.addAttribute("numdivlines", "0");
			graphElement.addAttribute("exportEnabled", "1");
			graphElement.addAttribute("exportAtClient", "1");
			graphElement.addAttribute("canvasPadding", "20");
			graphElement.addAttribute("formatNumberScale", "0");
			graphElement.addAttribute("showAboutMenuItem", "0");
			graphElement.addAttribute("showPrintMenuItem", "0");
			// graphElement.addAttribute("chartLeftMargin", "20");
			graphElement.addAttribute("labelDisplay", "WRAP");
			if (searchCond.get("fend") != null)
				graphElement.addAttribute("exportHandler", "fendExporter");
			else
				graphElement.addAttribute("exportHandler", "nendExporter");
			graphElement.addAttribute("exportDialogMessage", "正在生成,请稍候...");
			graphElement.addAttribute("exportFormats",
					"JPG=生成JPG图片|PDF=生成PDF文件");
			graphElement.addAttribute("connectNullData", "1");
			// categories标签（横坐标）
			Element catsElement = graphElement.addElement("categories");
			// 查找所有有数据的日期，用作横坐标
			List<String> categoriesList = performanceManagerMapper
					.getDiagramCategories(searchCond, pmStdIndex, tableNameList);

			if (categoriesList.size() > 0) {
				for (String c : categoriesList) {
					Element catElement = catsElement.addElement("category");
					catElement.addAttribute("name", c);
				}
			}
			Float maxPmValue = 0F;
			Float minPmValue = 0F;
			// 查询性能值
			for (int i = 0; i < pmStdIndex.length; i++) {
				// 这里也许会到不同的表里查询
				returnList = performanceManagerMapper.generateDiagramNend(
						searchCond, pmStdIndex[i].trim(), tableNameList);
				if (returnList.size() != 0) {
					if (title.isEmpty())
						title = returnList.get(0).get("neName") + "-"
								+ returnList.get(0).get("portDesc");

					Element datasetElement = graphElement.addElement("dataset");
					datasetElement.addAttribute("seriesName",
							String.valueOf(returnList.get(0).get("pmDesc")));

					datasetElement.addAttribute("color",
							CommonDefine.PM.PM_DIAGRAM.lineColor[i]);
					datasetElement.addAttribute("anchorBorderColor",
							CommonDefine.PM.PM_DIAGRAM.lineColor[i]);
					datasetElement.addAttribute("anchorBgColor",
							CommonDefine.PM.PM_DIAGRAM.lineColor[i]);
					Map<String, String> dateValue = new HashMap();
					for (Map m : returnList) {
						try{
							Float.valueOf(m.get("pmValue").toString());
						}catch(NumberFormatException e){
							continue;
						}
						dateValue.put(m.get("retrievalTime").toString(),
								m.get("pmValue").toString());

						if (Float.valueOf(m.get("pmValue").toString()) >= 0F)
							maxPmValue = maxPmValue < Float.valueOf(m.get(
									"pmValue").toString()) ? Float.valueOf(m
									.get("pmValue").toString()) : maxPmValue;
						if (Float.valueOf(m.get("pmValue").toString()) < 0F)
							minPmValue = minPmValue > Float.valueOf(m.get(
									"pmValue").toString()) ? Float.valueOf(m
									.get("pmValue").toString()) : minPmValue;
					}
					for (String c : categoriesList) {
						Element setElement = datasetElement.addElement("set");
						if (dateValue.containsKey(c)) {
							setElement.addAttribute("value", dateValue.get(c)
									.toString());
						} else {
							setElement.addAttribute("value", "null");
						}
					}
				} else {
					// 没查到的话
					String pmDesc = performanceManagerMapper.getPmDescription(
							pmStdIndex[i].trim(), DEFINE);
					Element datasetElement = graphElement.addElement("dataset");
					datasetElement.addAttribute("seriesName", pmDesc);

					datasetElement.addAttribute("color",
							CommonDefine.PM.PM_DIAGRAM.lineColor[i]);
					datasetElement.addAttribute("anchorBorderColor",
							CommonDefine.PM.PM_DIAGRAM.lineColor[i]);
					datasetElement.addAttribute("anchorBgColor",
							CommonDefine.PM.PM_DIAGRAM.lineColor[i]);
					Element setElement = datasetElement.addElement("set");
				}
				// 只有选择一个性能事件的时候，才允许显示上限值下限值
				if (searchCond.get("needLimit").equals("1") && i == 0) {
					Element datasetUpper = graphElement.addElement("dataset");
					datasetUpper.addAttribute("seriesName", "上限值");
					datasetUpper.addAttribute("color",
							CommonDefine.PM.PM_DIAGRAM.lineColor[6]);
					// datasetUpper.addAttribute("drawAnchors", "0");
					Element datasetLower = graphElement.addElement("dataset");
					datasetLower.addAttribute("seriesName", "下限值");
					datasetLower.addAttribute("color",
							CommonDefine.PM.PM_DIAGRAM.lineColor[6]);
					// datasetLower.addAttribute("drawAnchors", "0");
					Map<String, Map<String, String>> upperLowerMap = new HashMap<String, Map<String, String>>();
					for (Map m : returnList) {
						Map<String, String> upperLower = new HashMap<String, String>();
						if (m.get("upperValue") != null)
							upperLower.put("upperValue", m.get("upperValue")
									.toString());
						else
							upperLower.put("upperValue", "null");
						if (m.get("lowerValue") != null)
							upperLower.put("lowerValue", m.get("lowerValue")
									.toString());
						else
							upperLower.put("lowerValue", "null");

						upperLowerMap.put(m.get("retrievalTime").toString(),
								upperLower);
					}

					for (String c : categoriesList) {
						Element setUpper = datasetUpper.addElement("set");
						Element setLower = datasetLower.addElement("set");
						if (upperLowerMap.containsKey(c)) {
							setUpper.addAttribute("value", upperLowerMap.get(c)
									.get("upperValue").toString());
							setLower.addAttribute("value", upperLowerMap.get(c)
									.get("lowerValue").toString());
						} else {
							setUpper.addAttribute("value", "null");
							setLower.addAttribute("value", "null");
						}
					}
				}
			}
			if (searchCond.get("pmType").toString().equals("1")) {
				// 计数值
				graphElement.addAttribute("decimalPrecision", "0");
				graphElement.addAttribute("forceDecimals", "0");
			} else {
				graphElement.addAttribute("decimalPrecision", "2");
				graphElement.addAttribute("forceDecimals", "1");
			}
			if (maxPmValue == 0F && minPmValue == 0F) {
				graphElement.addAttribute("yAxisMaxValue", "10.00");
			} else if (maxPmValue == 0F && minPmValue != 0F) {
				graphElement.addAttribute("yAxisMaxValue", "0.00");
				graphElement.addAttribute("yAxisMinValue",
						String.valueOf(minPmValue * 2));
			} else if (maxPmValue != 0F && minPmValue == 0F) {
				graphElement.addAttribute("yAxisMaxValue",
						String.valueOf(maxPmValue * 2));
				graphElement.addAttribute("yAxisMinValue", "0.00");
			}
			// 设置图片标题
			graphElement.addAttribute("caption", title);
			String text = document.asXML();

			return text;
		} catch (java.text.ParseException e1) {
			throw new CommonException(e1,
					MessageCodeDefine.PM_TIME_FORMART_ERROR);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> getPmTableName(Date date1, Date date2,
			String emsConnectionId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
		List<String> tableName = new ArrayList();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		if (date1.compareTo(date2) < 0) {
			cal1.setTime(date1);
			cal2.setTime(date2);
		} else {
			cal1.setTime(date2);
			cal2.setTime(date1);
		}
		cal2.add(Calendar.MONTH, 1);
		while (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)) {
			String table = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA + "_"
					+ emsConnectionId + "_" + sdf.format(cal1.getTime());
			tableName.add(table);
			cal1.add(Calendar.MONTH, 1);
		}

		return tableName;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public String generateDiagramFend(Map<String, String> searchCond)
			throws CommonException {
		Integer ptpId = Integer.valueOf(searchCond.get("ptpId"));
		Map ptpLink = performanceManagerMapper.getPtpIdFend(ptpId,
				Integer.valueOf(CommonDefine.PM.LINK_TYPE.OUTER_LINK));
		if (ptpLink != null) {
			if (!ptpLink.get("aPtp").equals(ptpId)) {
				searchCond.put("ptpId", ptpLink.get("aPtp").toString());
			} else {
				searchCond.put("ptpId", ptpLink.get("zPtp").toString());
			}
			String xmlStr = generateDiagramNend(searchCond);
			return xmlStr;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getOptStdComboValue(
			Map<String, String> searchCond) throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			if("MAC".equals(searchCond.get("ptpType"))){
				searchCond.put("ptpType","");
			}
				
			returnList = performanceManagerMapper
					.getOptStdComboValue(searchCond);
			if (searchCond.get("needNull").equals("1")) {
				Map<String, String> noRecord = new HashMap<String, String>();
				noRecord.put("pmStdOptPortId", "-999");
				noRecord.put("model", "无光口标准");
				returnList.add(0, noRecord);
			}
			if (searchCond.get("needAll").equals("1")) {
				Map<String, String> allRecord = new HashMap<String, String>();
				allRecord.put("pmStdOptPortId", "0");
				allRecord.put("model", "全部");
				returnList.add(0, allRecord);
			}

			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getOptModelComboValue(
			Map<String, String> searchCond) throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			returnList = performanceManagerMapper.getOptModelComboValue(
					RegularPmAnalysisDefine, searchCond);
			if (searchCond.get("needNull").equals("1")) {
				Map<String, String> noRecord = new HashMap<String, String>();
				noRecord.put("optModel", "无光模块");
				returnList.add(0, noRecord);
			}
			if (searchCond.get("needAll").equals("1")) {
				Map<String, String> allRecord = new HashMap<String, String>();
				allRecord.put("optModel", "全部");
				returnList.add(0, allRecord);
			}
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchPtpOptModelInfo(List<Map> nodeList,
			List<Long> emsIds, Map<String, String> searchCond)
			throws CommonException {
		Map<String, String> conditionMap = processNodesToConditions(emsIds,
				searchCond, nodeList);
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			returnList = performanceManagerMapper.searchPtpOptModelInfo(
					RegularPmAnalysisDefine, conditionMap);
			int count = performanceManagerMapper.searchPtpOptModelInfoCount(
					RegularPmAnalysisDefine, conditionMap);
			returnData.put("rows", returnList);
			returnData.put("total", count);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

		return returnData;
	}

	/**
	 * 处理nodes以及其他的查询条件
	 * 
	 * @param emsIds
	 *            分区查询所需要的emsID
	 * @param searchCond
	 *            除了节点信息的其他查询条件
	 * @param nodeList
	 *            节点信息
	 * @return conditionMap 处理完以上条件之后综合成的一个条件Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, String> processNodesToConditions(List<Long> emsIds,
			Map<String, String> searchCond, List<Map> nodeList) {
		Map<String, String> conditionMap = new HashMap<String, String>();
		if (nodeList != null)
			conditionMap = nodeListClassify(nodeList);
		if (emsIds != null) {
			// 对EMSID做去重复处理
			List<Long> noDuplicateId = new ArrayList();
			for (Long emsId : emsIds) {
				if (!noDuplicateId.contains(emsId)) {
					noDuplicateId.add(emsId);
				}
			}
			// 对nodeList处理完毕之后，将EMSID列表中的ID加进map里
			// 单独作为一条map，以作为分区索加快查询速度
			for (Long emsId : noDuplicateId) {
				if (conditionMap.containsKey("INDEX_EMS")) {
					conditionMap.put("INDEX_EMS", conditionMap.get("INDEX_EMS")
							+ ',' + emsId.toString());
				} else {
					conditionMap.put("INDEX_EMS", emsId.toString());
				}
			}
		}
		if (searchCond != null)
			conditionMap.putAll(searchCond);

		return conditionMap;

	}

	@SuppressWarnings("rawtypes")
	public Map<String, String> nodeListClassify(List<Map> nodeList) {
		Map<String, String> conditionMap = new HashMap<String, String>();
		for (Map m : nodeList) {
			String key = "";
			// 对子网的处理,因为子网有嵌套的情况
			if (TREE_DEFINE.get("NODE_SUBNET").equals(m.get("nodeLevel"))) {
				// 获得所有最下层子网ID
				List<Integer> subnetIds = getSubnetChildren(Integer.parseInt(m
						.get("nodeId").toString()));
				key = "NODE_SUBNET";
				for (Integer id : subnetIds) {
					if (conditionMap.containsKey(key)) {
						conditionMap.put(key,
								conditionMap.get(key) + ',' + id.toString());
					} else {
						conditionMap.put(key, id.toString());
					}
				}
			} else {
				// 把选中的node按照nodeLevel归类，nodeId组成一个以逗号隔开的字符串，并放进Map中
				key = changeNodeLevelFromIntToString(Integer.parseInt(m.get(
						"nodeLevel").toString()));
				String nodeId = String.valueOf(m.get("nodeId"));
				if (conditionMap.containsKey(key)) {
					conditionMap.put(key, conditionMap.get(key) + ',' + nodeId);
				} else {
					conditionMap.put(key, nodeId);
				}
			}
		}
		return conditionMap;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void savePtpOptStdApplication(List<Map> list) throws CommonException {
		try {
			for (Map m : list) {
				if (m.get("optStdId").toString().equals("-999"))
					m.remove("optStdId");
				performanceManagerMapper.savePtpOptStdApplication(m);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map searchOptStdDetail(Map<String, String> searchCond)
			throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			returnList = performanceManagerMapper
					.searchOptStdDetail(searchCond);
			returnData.put("rows", returnList);
			int total = performanceManagerMapper
					.searchOptStdDetailCount(searchCond);
			returnData.put("total", total);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void saveOptStdDetail(List<Map> optStdDetailList)
			throws CommonException {
		try {
			for (Map m : optStdDetailList) {
				performanceManagerMapper.saveOptStdDetail(m);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@Override
	public void saveNewOptStd(Map<String, String> searchCond)
			throws CommonException {
		try {
			performanceManagerMapper.saveNewOptStd(searchCond);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@Override
	public void deleteOptStd(List<Long> condList, Map<String, String> searchCond)
			throws CommonException {
		try {
			for (Long optStdId : condList) {
				if (searchCond.get("isApplied").equals("1"))
					performanceManagerMapper.detachOptStd(optStdId,
							RegularPmAnalysisDefine);
				performanceManagerMapper.deleteOptStd(optStdId);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getOptModelFromNodes(List<Map> nodeList,
			Map<String, String> searchCond, List<Long> emsIds)
			throws CommonException {
		Map<String, String> conditionMap = new HashMap<String, String>();
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		if (emsIds.get(0) == null)
			emsIds = null;
		conditionMap = processNodesToConditions(emsIds, searchCond, nodeList);
		try {
			returnList = performanceManagerMapper.getOptModelFromNodes(
					conditionMap, RegularPmAnalysisDefine);
			Map<String, String> nullRecord = new HashMap<String, String>();
			nullRecord.put("optModel", "无光模块");
			returnList.add(0, nullRecord);
			// Map<String, String> allRecord = new HashMap<String, String>();
			// allRecord.put("optModel", "全部");
			// returnList.add(0, allRecord);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getOptStdInfo(Map<String, String> searchCond)
			throws CommonException {
		Map result = new HashMap();
		try {
			result = performanceManagerMapper.getOptStdInfo(searchCond);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void applyPtpOptStdBatch(List<Map> nodeList, List<Long> emsIds,
			Map<String, String> searchCond) throws CommonException {
		Map<String, String> conditionMap = new HashMap<String, String>();
		if (emsIds.get(0) == null)
			emsIds = null;
		conditionMap = processNodesToConditions(emsIds, searchCond, nodeList);
		try {
			List<Long> ptpIds = performanceManagerMapper
					.getPtpIdForBatchOptStdApply(conditionMap,
							RegularPmAnalysisDefine);
			if (ptpIds.size() > 0)
				performanceManagerMapper.applyPtpOptStdBatch(ptpIds,
						conditionMap);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public boolean checkOptStdName(Map<String, String> searchCond)
			throws CommonException {
		boolean isDuplicate = false;
		try {
			int count = performanceManagerMapper.checkOptStdName(searchCond);
			if (count != 0)
				isDuplicate = true;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return isDuplicate;

	}

	@Override
	public int checkIfStdApplied(Map<String, String> searchCond)
			throws CommonException {
		try {
			// 判断是否默认标准
			if (CommonDefine.PM.defaultOptStd.containsValue(Integer
					.valueOf(searchCond.get("optStdId").toString())))
				return 2;
			// 判断是否已经应用
			int count = performanceManagerMapper.checkIfStdApplied(searchCond);
			if (count != 0)
				return 3;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String autoApplyPtpOptStd(List<Map> nodeList, List<Long> emsIds,
			String processBarKey) throws CommonException {
		Map<String, String> conditionMap = new HashMap<String, String>();
		if (emsIds.get(0) == null)
			emsIds = null;
		conditionMap = processNodesToConditions(emsIds, null, nodeList);
		String returnResult = "";
		try {
			List<Long> ptpIds = performanceManagerMapper.getPtpIdListByDomain(
					conditionMap, RegularPmAnalysisDefine,
					CommonDefine.PM.DOMAIN.DOMAIN_SDH_FLAG);
			if (ptpIds.size() == 0) {
				/*
				 * // 进度描述信息更改--此处修改 String text = "当前进度" + 0 + "/" + 0; //
				 * 加入进度值 setProcessParameter(getSessionId(), processBarKey,
				 * text, 1d);
				 */
				CommonDefine.setProcessParameter(getSessionId(), processBarKey,
						0, 0, null);
				return returnResult;
			}
			List<Map> outMaxList = performanceManagerMapper.getOutMaxList(
					ptpIds, CommonDefine.PM.TARGET_TYPE.PTP, "TPL_MAX",
					"RPL_MAX");
			List<Map<String, Object>> dataList = processDataForAutoOptStd(outMaxList);

			if (CommonDefine.getIsCanceled(getSessionId(), processBarKey)) {
				CommonDefine.respCancel(getSessionId(), processBarKey);
				returnResult = "cancel";
				return returnResult;
			}
			if (dataList.size() == 0) {
				/*
				 * // 进度描述信息更改--此处修改 String text = "当前进度" + 0 + "/" + 0; //
				 * 加入进度值 setProcessParameter(getSessionId(), processBarKey,
				 * text, 1d);
				 */
				CommonDefine.setProcessParameter(getSessionId(), processBarKey,
						0, 0, null);
			}
			for (int i = 0; i < dataList.size(); i++) {
				Map m = dataList.get(i);
				if (m.get("optStdId") != null) {
					System.out.println(CommonDefine.getIsCanceled(
							getSessionId(), processBarKey));
					if (CommonDefine.getIsCanceled(getSessionId(),
							processBarKey)) {
						CommonDefine.respCancel(getSessionId(), processBarKey);
						returnResult = "cancel";
						break;
					}
					performanceManagerMapper.autoApplyOptStd(m);
				}
				// 进度描述信息更改--此处修改
				/*
				 * String text = "当前进度" + (i + 1) + "/" + dataList.size(); //
				 * 加入进度值 setProcessParameter(getSessionId(), processBarKey,
				 * text, Double.valueOf((i + 1) / ((double)
				 * (dataList.size()))));
				 */
				CommonDefine.setProcessParameter(getSessionId(), processBarKey,
						(i + 1), dataList.size(), null);
			}
		} catch (Exception e) {
			CommonDefine.respCancel(getSessionId(), processBarKey);
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnResult;
	}

	@SuppressWarnings("rawtypes")
	private List<Map<String, Object>> processDataForAutoOptStd(List<Map> list) {
		// 对数据进行合并操作
		List<Map<String, Object>> dataList = classifyDataForAutoOptStd(list);
		if (dataList != null) {
			for (int i = 0; i < dataList.size(); i++) {
				Map<String, Object> map = dataList.get(i);
				// 判断应该使用哪个光口标准
				Integer optStdId = whichOptStdShouldItUse(map);
				dataList.get(i).put("optStdId", optStdId);
			}
		}
		return dataList;
	}

	// 判断应该使用哪个光口标准
	private Integer whichOptStdShouldItUse(Map<String, Object> map) {
		Float maxOut = 999F;
		Float maxIn = 999F;
		if (map.get("maxOut") != null)
			maxOut = Float.valueOf(map.get("maxOut").toString());
		if (map.get("maxIn") != null)
			maxIn = Float.valueOf(map.get("maxIn").toString());
		Integer optStdId = null;
		if ("STM-1".equals(map.get("ptpType"))) {
			// maxOut [-16,-7]
			if (maxOut
					.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_OUT_BLOCK.RANGE1_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_OUT_BLOCK.RANGE1_END) <= 0) {
				// maxIn (-34,-25]
				if (maxIn
						.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_IN_BLOCK.RANGE1_START) > 0
						&& maxIn.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_IN_BLOCK.RANGE1_END) <= 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB S-1.2");
					return optStdId;
				} else
				// maxIn (-25,-16]
				if (maxIn
						.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_IN_BLOCK.RANGE2_START) > 0
						&& maxIn.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_IN_BLOCK.RANGE2_END) <= 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB S-1.1");
					return optStdId;
				} else
				// maxIn (-16,-8]
				if (maxIn
						.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_IN_BLOCK.RANGE3_START) > 0
						&& maxIn.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_IN_BLOCK.RANGE3_END) <= 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB I-1");
					return optStdId;
				}
			} else
			// maxOut (-7,1}
			if (maxOut
					.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_OUT_BLOCK.RANGE2_START) > 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM1_INTERVAL.MAX_OUT_BLOCK.RANGE2_END) <= 0) {
				optStdId = CommonDefine.PM.defaultOptStd.get("GB L-1.2");
				return optStdId;
			}
		} else if ("STM-4".equals(map.get("ptpType"))) {
			// maxOut [-16,-7]
			if (maxOut
					.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_OUT_BLOCK.RANGE1_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_OUT_BLOCK.RANGE1_END) <= 0) {
				// maxIn (-34,-25]
				if (maxIn
						.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_IN_BLOCK.RANGE1_START) > 0
						&& maxIn.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_IN_BLOCK.RANGE1_END) <= 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB S-4.2");
					return optStdId;
				} else
				// maxIn (-25,-16]
				if (maxIn
						.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_IN_BLOCK.RANGE2_START) > 0
						&& maxIn.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_IN_BLOCK.RANGE2_END) <= 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB S-4.1");
					return optStdId;
				} else
				// maxIn (-16,-8]
				if (maxIn
						.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_IN_BLOCK.RANGE3_START) > 0
						&& maxIn.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_IN_BLOCK.RANGE3_END) <= 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB I-4");
					return optStdId;
				}
			} else
			// maxOut (-4,3}
			if (maxOut
					.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_OUT_BLOCK.RANGE2_START) > 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM4_INTERVAL.MAX_OUT_BLOCK.RANGE2_END) <= 0) {
				optStdId = CommonDefine.PM.defaultOptStd.get("GB L-4.2");
				return optStdId;
			}
		} else if ("STM-16".equals(map.get("ptpType"))) {
			// maxOut [-11,-5)
			if (maxOut
					.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE1_START) > 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE1_END) <= 0) {
				optStdId = CommonDefine.PM.defaultOptStd.get("GB I-16");
				return optStdId;
			} else
			// maxOut [-5,-2)
			if (maxOut
					.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE2_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE2_END) < 0) {
				optStdId = CommonDefine.PM.defaultOptStd.get("GB S-16.2");
				return optStdId;
			} else
			// maxOut [-2,0)
			if (maxOut
					.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE3_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE3_END) < 0) {
				// maxIn [-29,-14)
				if (maxIn
						.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_IN_BLOCK.RANGE1_START) >= 0
						&& maxIn.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_IN_BLOCK.RANGE1_END) < 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB L-16.2");
					return optStdId;
				} else
				// maxIn [-14,0)
				if (maxIn
						.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_IN_BLOCK.RANGE2_START) >= 0
						&& maxIn.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_IN_BLOCK.RANGE2_END) < 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB S-16.2");
					return optStdId;
				}
			} else
			// maxOut [0,4]
			if (maxOut
					.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE4_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM16_INTERVAL.MAX_OUT_BLOCK.RANGE4_END) <= 0) {
				optStdId = CommonDefine.PM.defaultOptStd.get("GB L-16.2");
				return optStdId;
			}
		} else if ("STM-64".equals(map.get("ptpType"))) {
			// maxOut [-6,-1)
			if (maxOut
					.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE1_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE1_END) < 0) {
				optStdId = CommonDefine.PM.defaultOptStd.get("GB I-64.1");
				return optStdId;
			} else
			// maxOut [-1,2)
			if (maxOut
					.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE2_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE2_END) < 0) {
				// maxIn [-27,-14)
				if (maxIn
						.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE1_START) >= 0
						&& maxIn.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE1_END) < 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB L-64.2c");
					return optStdId;
				} else
				// maxIn [-14,0)
				if (maxIn
						.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE2_START) >= 0
						&& maxIn.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE2_END) < 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB S-64.2b");
					return optStdId;
				}
			} else
			// maxOut [3,8)
			if (maxOut
					.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE3_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE3_END) < 0) {
				optStdId = CommonDefine.PM.defaultOptStd.get("GB L-64.1");
				return optStdId;
			} else
			// maxOut [10,15]
			if (maxOut
					.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE4_START) >= 0
					&& maxOut
							.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_OUT_BLOCK.RANGE4_END) <= 0) {
				// maxIn [-27,-13)
				if (maxIn
						.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE3_START) >= 0
						&& maxIn.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE3_END) < 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB V-64.2b");
					return optStdId;
				} else
				// maxIn [-13,-2)
				if (maxIn
						.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE4_START) >= 0
						&& maxIn.compareTo(CommonDefine.PM.STM64_INTERVAL.MAX_IN_BLOCK.RANGE4_END) < 0) {
					optStdId = CommonDefine.PM.defaultOptStd.get("GB L-64.2b");
					return optStdId;
				}
			}
		}
		// 不在范围内的返回null
		return optStdId;
	}

	// 整理数据-自动光口标准应用
	@SuppressWarnings("rawtypes")
	private List<Map<String, Object>> classifyDataForAutoOptStd(List<Map> list) {
		List<Map<String, Object>> listAfterClassify = new ArrayList<Map<String, Object>>();
		List<Long> ptpList = new ArrayList<Long>();
		for (Map map : list) {
			if (!ptpList.contains(Long.valueOf(map.get("ptpId").toString())))
				ptpList.add(Long.valueOf(map.get("ptpId").toString()));
		}
		// 整理数据。将同Id的两条数据合并为一条数据。
		for (Long id : ptpList) {
			Map<String, Object> rec = new HashMap<String, Object>();
			rec.put("ptpId", id);
			for (Map m : list) {

				if (id.equals(Long.valueOf(m.get("ptpId").toString()))) {
					if (!rec.containsKey("ptpType"))
						rec.put("ptpType", m.get("ptpType").toString());
					if ("TPL_MAX".equals(m.get("pmStdIndex").toString())) {
						rec.put("maxOut",
								Float.valueOf(m.get("pmValue").toString()));
					}
					if ("RPL_MAX".equals(m.get("pmStdIndex").toString())) {
						rec.put("maxIn",
								Float.valueOf(m.get("pmValue").toString()));
					}
				}
				if (rec.containsKey("maxOut") && rec.containsKey("maxIn")) {
					break;
				}
			}
			listAfterClassify.add(rec);
		}
		return listAfterClassify;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getPrivilegeList() throws CommonException {

		Map<String, Object> returnMap = new HashMap();

		List<Map> returnList = performanceManagerMapper.getPrivilegeList();
		returnMap.put("rows", returnList);
		returnMap.put("total", returnList.size());
		return returnMap;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getNodeInfo(List<Map> nodeList)
			throws CommonException {
		Map<String, String> conditionMap = nodeListClassify(nodeList);
		Iterator it = conditionMap.keySet().iterator();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			while (it.hasNext()) {
				String level = (String) it.next();
				if ("NODE_EMS".equals(level)) {
					List<Map<String, String>> emsNodeInfo = performanceManagerMapper
							.getEmsNodeInfo(conditionMap,
									RegularPmAnalysisDefine, TREE_DEFINE);
					resultList.addAll(emsNodeInfo);
				} else if ("NODE_SUBNET".equals(level)) {
					List<Map<String, String>> subnetNodeInfo = performanceManagerMapper
							.getSubnetNodeInfo(conditionMap,
									RegularPmAnalysisDefine, TREE_DEFINE);
					resultList.addAll(subnetNodeInfo);

				} else if ("NODE_NE".equals(level)) {
					List<Map<String, String>> neNodeInfo = performanceManagerMapper
							.getNeNodeInfo(conditionMap,
									RegularPmAnalysisDefine, TREE_DEFINE);
					resultList.addAll(neNodeInfo);
				}
			}
			// int count = performanceManagerMapper.getCountOfNeUnderThisNode(
			// conditionMap, RegularPmAnalysisDefine);
			returnData.put("returnResult", CommonDefine.SUCCESS);
			// returnData.put("count", count);
			returnData.put("info", resultList);
		} catch (Exception e) {
			returnData.put("returnResult", CommonDefine.FAILED);
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	// TODO
	@SuppressWarnings("rawtypes")
	@Override
	public int saveNeReportTask(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException {
		try {
			Map<String, String> conditionMap = nodeListClassify(nodeList);
			// 返回的新Id
			Map<String, Long> idMap = new HashMap<String, Long>();
			// 检查一下列表中存在的网元数量，超过500个将不进行保存
			int count = performanceManagerMapper.getCountOfNeUnderThisNode(
					conditionMap, RegularPmAnalysisDefine);
			if (count > 500)
				return 0;
			// 保存任务主要信息到t_sys_task表
			performanceManagerMapper.saveNeSysTask(searchCond, currentUserId,
					CommonDefine.QUARTZ.JOB_REPORT_NE, idMap);
			// 保存任务节点信息到t_sys_task_info
			performanceManagerMapper.saveNeSysTaskInfo(nodeList, idMap);
			// 保存其他一些信息到param表中
			performanceManagerMapper.saveNeTaskParam(searchCond, idMap);
			
			// 建立定时任务
			int taskId = idMap.get("newId").intValue();
			int hour = Integer.parseInt(searchCond.get("hour"));
			int periodType = Integer.parseInt(searchCond.get("period"));
			int taskType = CommonDefine.QUARTZ.JOB_REPORT_NE;
			int delay = Integer.parseInt(searchCond.get("delay"));
			addOrEditReportQuartzTask(taskId, hour, periodType, taskType, delay);
			// 立即执行
			// quartzManagerService.ctrlJob(taskType, taskId,
			// CommonDefine.QUARTZ.JOB_ACTIVATE);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return 1;
	}

	@Override
	public Map<String, Object> searchReportTask(Map<String, String> searchCond,
			Integer userId) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
		try {
			searchCond.put("userId", userId.toString());
			returnList = performanceManagerMapper.searchReportTask(searchCond,
					REPORT_DEFINE);
			int count = performanceManagerMapper.searchReportTaskCount(
					searchCond, REPORT_DEFINE);
			returnData.put("total", count);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public int getPMReportParamForFP(int flag, int userId)
			throws CommonException {
		Map<String, String> searchCond = new HashMap<String, String>();
		try {
			searchCond.put("userId", String.valueOf(userId));
			if (flag == CommonDefine.SUCCESS_TASK_FIRST_PAGE) {
				searchCond.put("result", "1");
			}
			int count = performanceManagerMapper.getPMReportParamForFP(
					searchCond, REPORT_DEFINE);
			return count;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getCreatorComboValue(Integer userId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getCreatorComboValue(userId,
					REPORT_DEFINE);
			Map<String, String> allMap = new HashMap<String, String>();
			allMap.put("userId", "0");
			allMap.put("userName", "全部");
			returnList.add(0, allMap);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getCreatorComboValuePrivilege(Integer userId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			List<Map> userGrps = performanceManagerMapper.getUserGroupByUserId(
					userId, REPORT_DEFINE);

			returnList = performanceManagerMapper
					.getCreatorComboValuePrivilege(userGrps, REPORT_DEFINE);
			Map<String, String> allMap = new HashMap<String, String>();
			allMap.put("userId", "0");
			allMap.put("userName", "全部");
			returnList.add(0, allMap);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getTaskNameComboValue(
			Map<String, String> searchCond) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getTaskNameComboValue(
					searchCond, REPORT_DEFINE);
			if ("1".equals(searchCond.get("needAll"))) {
				Map<String, String> allMap = new HashMap<String, String>();
				allMap.put("taskId", "0");
				allMap.put("taskName", "全部");
				returnList.add(0, allMap);
			}
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getTaskNameComboValuePrivilege(
			Map<String, String> searchCond, Integer userId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			List<Map> userGrps = performanceManagerMapper.getUserGroupByUserId(
					userId, REPORT_DEFINE);
			returnList = performanceManagerMapper
					.getTaskNameComboValuePrivilege(searchCond, REPORT_DEFINE,
							userGrps, userId);
			if ("1".equals(searchCond.get("needAll"))) {
				Map<String, String> allMap = new HashMap<String, String>();
				allMap.put("taskId", "0");
				allMap.put("taskName", "全部");
				returnList.add(0, allMap);
			}
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public void deleteReportTask(Map<String, String> searchCond)
			throws CommonException {
		try {
			performanceManagerMapper
					.deleteReportTask(searchCond, REPORT_DEFINE);
			// 删除对应的定时任务
			quartzManagerService.ctrlJob(
					Integer.parseInt(searchCond.get("taskType")),
					Integer.parseInt(searchCond.get("taskId")),
					CommonDefine.QUARTZ.JOB_DELETE);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	// 报表用
	public Long checkTaskNameDuplicate(Map<String, String> searchCond,
			Integer userId) throws CommonException {
		Long result;
		try {
			searchCond.put("userId", userId.toString());
			int[] taskTypes = { CommonDefine.QUARTZ.JOB_REPORT_MS,
					CommonDefine.QUARTZ.JOB_REPORT_NE };
			int count = performanceManagerMapper.checkTaskNameDuplicate(
					searchCond, REPORT_DEFINE, taskTypes);
			if (count > 0)
				result = 0L;
			else
				result = 1L;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return result;
	}

	@Override
	// 通用
	public Long checkTaskNameDuplicate(Map<String, String> searchCond,
			Integer userId, int[] taskTypes) throws CommonException {
		Long result;
		try {
			if (userId != null)
				searchCond.put("userId", userId.toString());
			int count = performanceManagerMapper.checkTaskNameDuplicate(
					searchCond, REPORT_DEFINE, taskTypes);
			if (count > 0)
				result = 0L;
			else
				result = 1L;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getEmsGroup() throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getEmsGroup(REPORT_DEFINE);
			Map<String, String> allMap = new HashMap<String, String>();
			allMap.put("emsGroupId", "0");
			allMap.put("emsGroupName", "全部");
			returnList.add(0, allMap);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getEms(Map<String, String> searchCond)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getEms(searchCond,
					REPORT_DEFINE);
			Map<String, String> allMap = new HashMap<String, String>();
			allMap.put("emsId", "0");
			allMap.put("emsName", "全部");
			returnList.add(0, allMap);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getTrunkLine(Map<String, String> searchCond)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.getTrunkLine(searchCond,
					REPORT_DEFINE);
			Map<String, String> allMap = new HashMap<String, String>();
			allMap.put("trunkLineId", "0");
			allMap.put("trunkLineName", "全部");
			returnList.add(0, allMap);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchMS(Map<String, String> searchCond)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.searchMS(searchCond,
					REPORT_DEFINE);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchTL(Map<String, String> searchCond)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		List<Map> returnListAfter = new ArrayList<Map>();
		try {
			returnList = performanceManagerMapper.searchTL(searchCond,
					REPORT_DEFINE);
			if (returnList.size() > 0) {
				List<Map> TLMS = performanceManagerMapper.searchTLMS(
						returnList, REPORT_DEFINE);
				returnListAfter = TLInfoTransformer(returnList, TLMS);
				returnData.put("rows", returnListAfter);
				returnData.put("total", returnListAfter.size());
			} else {
				returnData.put("rows", returnList);
				returnData.put("total", returnList.size());
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	// 为了在提示中显示复用段信息，需要组装数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Map> TLInfoTransformer(List<Map> list, List<Map> TLMS) {
		List<Map> returnList = new ArrayList<Map>();
		for (Map mapTL : list) {
			for (Map mapMS : TLMS) {
				if (mapTL.get("TLId").equals(mapMS.get("TLId"))) {
					String MSNameList = mapMS.get("MSNameList").toString();
					String[] listMS = MSNameList.split(",");
					StringBuffer MSNameTag = new StringBuffer();
					if (listMS.length > 0) {
						for (int i = 0; i < listMS.length; i++) {
							MSNameTag.append(listMS[i]);
							if (i != listMS.length - 1)
								MSNameTag.append(",");
							if ((i + 1) % 5 == 0) {
								MSNameTag.append("\r\n");
							}
						}
					}
					mapTL.put("MSNameList", MSNameList);
					mapTL.put("MSNameTag", MSNameTag.toString());
				}
			}
			returnList.add(mapTL);
		}
		return returnList;
	}

	// TODO
	@SuppressWarnings("rawtypes")
	@Override
	public void saveMSReportTask(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException {
		List<Map<String, String>> target = new ArrayList<Map<String, String>>();
		for (Map m : nodeList) {
			Map<String, String> tar = new HashMap<String, String>();
			tar.put("targetId", m.get("targetId").toString());
			tar.put("targetType", m.get("targetType").toString());
			target.add(tar);
		}
		try {
			// 返回的新Id
			Map<String, Long> idMap = new HashMap<String, Long>();
			searchCond.put("dataSrc", String
					.valueOf(CommonDefine.PM.PM_REPORT.DATA_SOURCE.NORMAL));
			// 保存任务主要信息到t_sys_task表
			performanceManagerMapper.saveMSSysTask(searchCond, currentUserId,
					CommonDefine.QUARTZ.JOB_REPORT_MS, idMap);
			// 保存任务节点信息到t_sys_task_info
			performanceManagerMapper.saveMSSysTaskInfo(target, idMap);
			// 保存其他一些信息到param表中
			performanceManagerMapper.saveMSTaskParam(searchCond, idMap);

			// 建立定时任务
			int taskId = idMap.get("newId").intValue();
			int hour = Integer.parseInt(searchCond.get("hour"));
			int periodType = Integer.parseInt(searchCond.get("period"));
			int taskType = CommonDefine.QUARTZ.JOB_REPORT_MS;
			int delay = Integer.parseInt(searchCond.get("delay"));
			addOrEditReportQuartzTask(taskId, hour, periodType, taskType, delay);

			// for test only
			// quartzManagerService.ctrlJob(taskType, taskId,
			// CommonDefine.QUARTZ.JOB_ACTIVATE);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> initMSReportTaskInfo(
			Map<String, String> searchCond) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		Map<String, Object> taskNodesInfoData = new HashMap<String, Object>();
		List<Map<String, String>> taskInfo = new ArrayList<Map<String, String>>();
		List<Map> taskNodes = new ArrayList<Map>();
		List<Map> taskNodesInfo = new ArrayList<Map>();
		try {
			taskInfo = performanceManagerMapper
					.searchMSTaskInfoForEdit(searchCond);
			taskNodes = performanceManagerMapper
					.searchTaskNodesForEdit(searchCond);
			int targetType = Integer.parseInt(taskNodes.get(0).get("nodeLevel")
					.toString());
			if (REPORT_DEFINE.get("NODE_MS").equals(targetType))
				taskNodesInfo = performanceManagerMapper.searchMSNodesInfo(
						taskNodes, REPORT_DEFINE);
			if (REPORT_DEFINE.get("NODE_TL").equals(targetType)) {
				List<Map> TLInfoList = performanceManagerMapper
						.searchTLNodesInfo(taskNodes, REPORT_DEFINE);
				List<Map> TLMS = performanceManagerMapper.searchTLMS(
						TLInfoList, REPORT_DEFINE);
				taskNodesInfo = TLInfoTransformer(TLInfoList, TLMS);
			}
			returnData.put("taskInfo", taskInfo);
			taskNodesInfoData.put("rows", taskNodesInfo);
			taskNodesInfoData.put("total", taskNodesInfo.size());
			returnData.put("taskNodesInfo", taskNodesInfoData);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateMSReportTask(List<Map> nodeList,
			Map<String, String> searchCond) throws CommonException {
		List<Map<String, String>> target = new ArrayList<Map<String, String>>();
		for (Map m : nodeList) {
			Map<String, String> tar = new HashMap<String, String>();
			tar.put("targetId", m.get("targetId").toString());
			tar.put("targetType", m.get("targetType").toString());
			target.add(tar);
		}
		try {
			Map<String, Long> idMap = new HashMap<String, Long>();
			// 保存任务主要信息到t_sys_task表
			performanceManagerMapper.updateMSSysTask(searchCond);
			// 保存其他一些信息到param表中
			performanceManagerMapper.updateMSTaskParam(searchCond);
			// 更新之前删除节点信息
			performanceManagerMapper.deleteNodesForUpdate(searchCond);
			// 保存任务节点信息到t_sys_task_info
			idMap.put("newId", Long.valueOf(searchCond.get("taskId")));
			performanceManagerMapper.saveMSSysTaskInfo(target, idMap);

			addOrEditReportQuartzTask(idMap.get("newId").intValue(),
					Integer.parseInt(searchCond.get("hour")),
					Integer.parseInt(searchCond.get("period")),
					CommonDefine.QUARTZ.JOB_REPORT_MS,
					Integer.parseInt(searchCond.get("delay")));
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> initNEReportTaskInfo(
			Map<String, String> searchCond) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		Map<String, Object> taskNodesInfoData = new HashMap<String, Object>();
		List<Map<String, Object>> taskInfo = new ArrayList<Map<String, Object>>();
		List<Map> taskNodes = new ArrayList<Map>();
		try {
			taskInfo = performanceManagerMapper
					.searchNETaskInfoForEdit(searchCond);
			taskNodes = performanceManagerMapper
					.searchTaskNodesForEdit(searchCond);
			// 获取网元信息
			Map<String, String> conditionMap = nodeListClassify(taskNodes);
			Iterator it = conditionMap.keySet().iterator();
			List<Map<String, String>> nodeInfo = new ArrayList<Map<String, String>>();
			while (it.hasNext()) {
				String level = (String) it.next();
				if ("NODE_EMS".equals(level)) {
					List<Map<String, String>> emsNodeInfo = performanceManagerMapper
							.getEmsNodeInfo(conditionMap,
									RegularPmAnalysisDefine, TREE_DEFINE);
					nodeInfo.addAll(emsNodeInfo);
				} else if ("NODE_SUBNET".equals(level)) {
					List<Map<String, String>> subnetNodeInfo = performanceManagerMapper
							.getSubnetNodeInfo(conditionMap,
									RegularPmAnalysisDefine, TREE_DEFINE);
					nodeInfo.addAll(subnetNodeInfo);
				} else if ("NODE_NE".equals(level)) {
					List<Map<String, String>> neNodeInfo = performanceManagerMapper
							.getNeNodeInfo(conditionMap,
									RegularPmAnalysisDefine, TREE_DEFINE);
					nodeInfo.addAll(neNodeInfo);
				}
			}
			// -------------
			returnData.put("taskInfo", taskInfo);
			taskNodesInfoData.put("rows", nodeInfo);
			taskNodesInfoData.put("total", nodeInfo.size());
			returnData.put("taskNodesInfo", taskNodesInfoData);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateNeReportTask(List<Map> nodeList,
			Map<String, String> searchCond) throws CommonException {
		List<Map<String, String>> target = new ArrayList<Map<String, String>>();
		for (Map m : nodeList) {
			Map<String, String> tar = new HashMap<String, String>();
			tar.put("targetId", m.get("nodeId").toString());
			tar.put("targetType", m.get("nodeLevel").toString());
			target.add(tar);
		}
		try {
			Map<String, Long> idMap = new HashMap<String, Long>();
			// 保存任务主要信息到t_sys_task表
			performanceManagerMapper.updateNESysTask(searchCond);
			// 保存其他一些信息到param表中
			performanceManagerMapper.updateNETaskParam(searchCond);
			// 更新之前删除节点信息
			performanceManagerMapper.deleteNodesForUpdate(searchCond);
			// 保存任务节点信息到t_sys_task_info
			idMap.put("newId", Long.valueOf(searchCond.get("taskId")));
			performanceManagerMapper.saveMSSysTaskInfo(target, idMap);
			addOrEditReportQuartzTask(idMap.get("newId").intValue(),
					Integer.parseInt(searchCond.get("hour")),
					Integer.parseInt(searchCond.get("period")),
					CommonDefine.QUARTZ.JOB_REPORT_NE,
					Integer.parseInt(searchCond.get("delay")));
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	// NE的日报
	public void searchPMForReportNeDaily(Map<String, String> searchCond)
			throws CommonException {
		try {
			// 一些主要信息，如任务名，数据源
			Map<String, Object> taskInfo = performanceManagerMapper
					.searchNETaskInfoForEdit(searchCond).get(0);
			// ========@
			// 查询出该任务下的所有节点信息（nodeId，nodeLevel）包括EMS,NE,SUBNET
			List<Map> taskNodes = performanceManagerMapper
					.searchTaskNodesForEdit(searchCond);
			Map<String, String> conditionMap = nodeListClassify(taskNodes);
			// 转换为[nodeId，nodeLevel，emsId]格式的数据
			// List<Map> nodeCond = processAllNodesToStdFormat(taskNodes);
			// 换算为NE
			taskNodes = instantReportMapper.getNeUnderThisNode(conditionMap,
					DEFINE, TREE_DEFINE);
			// 处理性能时间参数，为当天日期往前推delay的天数
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			Object d = taskInfo.get("delay");
			Integer delay = Integer.valueOf(d.toString());
			calendar.add(Calendar.DAY_OF_YEAR, -delay);
			Date time = calendar.getTime();
			// 为了让sql加上日期条件
			taskInfo.put("retrivalTime", "1");

			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonDefine.COMMON_START_FORMAT);
			SimpleDateFormat shortSdf = new SimpleDateFormat(
					CommonDefine.MS_REPORT_DAILY_FORMAT);
			SimpleDateFormat colSdf = new SimpleDateFormat(
					CommonDefine.COMMON_SIMPLE_FORMAT);
			taskInfo.put("retrivalTimeStart", sdf.format(time));
			sdf.applyPattern(CommonDefine.COMMON_END_FORMAT);
			taskInfo.put("retrivalTimeEnd", sdf.format(time));

			// ========@测试用retrivalTime@========
			// taskInfo.put("retrivalTimeStart", "2013-12-01 00:00:00");
			// taskInfo.put("retrivalTimeEnd", "2013-12-02 23:59:59");
			// 由于是日报，获取表名
			StringBuffer tableName = new StringBuffer();

			sdf = new SimpleDateFormat("yyyy_MM");
			tableName.append(CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA);
			tableName.append('_');
			tableName.append(sdf.format(time));
			taskInfo = processPmStdIndex(taskInfo);
			taskInfo.put("nendRx", CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG);
			taskInfo.put("nendTx", CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG);
			taskInfo.put("nendNA", CommonDefine.PM.PM_LOCATION_NA_FLAG);
			// 文件名！[报表任务名称]([报表周期]-[对象数据])_[实际性能事件日期]
			String taskName = taskInfo.get("taskName").toString();
			String taskPeriod = "日报";
			String taskDate = shortSdf.format(time);
			String yearmonth = taskInfo.get("retrivalTimeStart").toString()
					.substring(0, 7);
			String fileNameOrig = taskName + "(" + taskPeriod + "-原始数据)_"
					+ taskDate;
			String fileNameException = taskName + "(" + taskPeriod + "-异常数据)_"
					+ taskDate;
			String fileName = "0".equals(taskInfo.get("dataSrc").toString()) ? fileNameOrig
					: fileNameException;
			// 首先生成导出所用的ColumnMap（吧日期作为字段加进去）
			List<String> dates = new ArrayList<String>();
			dates.add(colSdf.format(time));
			ColumnMap[] neDailyCsvHeader = neCsvSrcHeader;
			ColumnMap[] neDailyXlsHeader = genColumn(neXlsBaseHeader, dates);
			// 生成导出对象
			// CsvUtil csvSrc = new CsvUtil("[" + taskName + "]([" + taskPeriod
			// + "]-[Src])_[" + taskDate + "].csv", neCsvSrcHeader, true);
			CsvUtil csvOrig = new CsvUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "/"
					+ CommonDefine.EXCEL.PM_BASE + "/" + yearmonth + "/"
					+ CommonDefine.EXCEL.PM_CSV + "/" + fileNameOrig + ".csv",
					neDailyCsvHeader);
			ExportResult erOrig = null;
			// then 导出异常数据
			CsvUtil csvEx = new CsvUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "/"
					+ CommonDefine.EXCEL.PM_BASE + "/" + yearmonth + "/"
					+ CommonDefine.EXCEL.PM_CSV + "/" + fileNameException
					+ ".csv", neDailyCsvHeader);
			ExportResult erEx = null;
			// 再导出到Excel
			String rptTpl = "网元"
					+ ("0".equals(taskInfo.get("dataSrc").toString()) ? "原始数据"
							: "异常数据") + "日报";
			PushExcelUtil xls = new PushExcelUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "/"
					+ CommonDefine.EXCEL.PM_BASE + "/" + yearmonth + "/"
					+ CommonDefine.EXCEL.PM_EXCEL + "/" + fileName + ".xlsx",
					neDailyXlsHeader, rptTpl);
			ExportResult erXls = null;
			int pageCnt = 1;
			if (taskNodes.size() <= 0) {
				xls.push(new ArrayList<Map>(), 0);
			}
			List<Map> pmList = null;

			int startNe = 0, countNe = taskNodes.size(), endNe;
			while (countNe > 0) {
				// 判断一下到不到了
				endNe = countNe - NE_INTERVAL > 0 ? startNe + NE_INTERVAL
						: startNe + countNe;
				// 一次处理N个Ne
				List<Map> partNe = taskNodes.subList(startNe, endNe);
				// 节点条件分层级筛选至各个EMS,然后将emsId拎出来变成key：[emsId，Map<nodeLvl,nodeIdStr>]
				Map<Integer, Map<String, Object>> emsNodeMaps = getConditionsFromNodesGroupByEmsIds(partNe);
				// [emsid]
				List<Map<String, Object>> tableNodesList = processTableNameIntoNodeCondition(
						emsNodeMaps, tableName);
				if (tableNodesList.size() > 0) {
					pmList = performanceManagerMapper.searchPMForNeDaily(
							tableNodesList, taskInfo, -1L);
				} else {
					pmList = new ArrayList<Map>();
				}
				// TODO 关于无数据的情况？
				// csvSrc.append(pmList);
				// 先导出原始数据到Csv
				csvOrig.append(pmList);
				// then 导出异常数据
				csvEx.append(pmList, true);
				// JSONArray o = JSONArray.fromObject(pmList);
				// System.out.println(o.toString());
				// 最后把数据转化下，日期作为关键字，性能值作为值
				pmList = PMDataUtil.combineNeData(pmList, neCsvSrcHeader);
				// o = JSONArray.fromObject(pmList);
				// System.out.println(o.toString());
				// 再导出到Excel
				xls.push(pmList, endNe - startNe);

				startNe += NE_INTERVAL;
				countNe -= NE_INTERVAL;
			}
			xls.close();
			// 获取导出结果
			erOrig = csvOrig.getResult();
			erEx = csvEx.getResult();
			erXls = xls.getResult();
			SimpleDateFormat dateParser = new SimpleDateFormat(
					CommonDefine.COMMON_FORMAT);
			// 导出结果信息入库-> t_pm_report_info
			Map<String, Object> exportInfo = new HashMap<String, Object>();
			exportInfo.put("SYS_TASK_ID", searchCond.get("taskId"));
			exportInfo.put("PRIVILEGE", taskInfo.get("privilege"));
			exportInfo.put("REPORT_NAME", fileName + ".xlsx");
			exportInfo.put("EXPORT_TIME", dateParser.format(new Date()));
			exportInfo.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_REPORT_NE);
			exportInfo.put("PERIOD", 0);// 0是日报
			exportInfo.put("DATA_SRC", taskInfo.get("dataSrc"));
			exportInfo.put("CREATOR", taskInfo.get("creator"));
			exportInfo.put("SIZE", erXls.getSize());
			exportInfo.put("EXCEL_URL", erXls.getFilePath());
			exportInfo.put("NORMAL_CSV_PATH", erOrig.getFilePath());
			exportInfo.put("ABNORMAL_CSV_PATH", erEx.getFilePath());
			Date startDate, endDate;
			startDate = dateParser.parse(taskInfo.get("retrivalTimeStart")
					.toString());
			endDate = dateParser.parse(taskInfo.get("retrivalTimeEnd")
					.toString());
			int taskId = Integer.parseInt(searchCond.get("taskId"));
			Map statInfo = calculateReportCountInfo(taskId, startDate, endDate);
			exportInfo
					.put("PM_EXCEPTION_LV1", statInfo.get("PM_EXCEPTION_LV1"));
			exportInfo
					.put("PM_EXCEPTION_LV2", statInfo.get("PM_EXCEPTION_LV2"));
			exportInfo
					.put("PM_EXCEPTION_LV3", statInfo.get("PM_EXCEPTION_LV3"));
			exportInfo
					.put("PM_ABNORMAL_RATE", statInfo.get("PM_ABNORMAL_RATE"));
			exportInfo.put("COLLECT_SUCCESS_RATE_NE",
					statInfo.get("COLLECT_SUCCESS_RATE"));
			exportInfo.put("FAILED_ID_NE", statInfo.get("FAILED_ID"));

			Map idMap = new HashMap();
			performanceManagerMapper.savePmExportInfo(exportInfo, idMap);
			performanceManagerMapper.savePmAnalysisInfo(exportInfo,
					CommonDefine.TARGET_TYPE_MAP, idMap);

		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	// NE的月报
	public void searchPMForReportNeMonthly(Map<String, String> searchCond)
			throws CommonException {
		try {
			Long start = 0L;
			// 一些主要信息，如任务名，数据源
			Map<String, Object> taskInfo = performanceManagerMapper
					.searchNETaskInfoForEdit(searchCond).get(0);
			// ========@
			// 查询出该任务下的所有节点信息（nodeId，nodeLevel）包括EMS,NE,SUBNET
			List<Map> taskNodes = performanceManagerMapper
					.searchTaskNodesForEdit(searchCond);
			// 转换为[nodeId，nodeLevel，emsId]格式的数据
			Map<String, String> conditionMap = nodeListClassify(taskNodes);
			// 换算为NE
			taskNodes = instantReportMapper.getNeUnderThisNode(conditionMap,
					DEFINE, TREE_DEFINE);
			// 处理性能时间参数,回到上个月
			Calendar calendar = Calendar.getInstance();
			// System.out.println((new Date()).toString());
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1);
			Date time = calendar.getTime();
			// System.out.println(time.toString());
			// 为了造一个开始和结束时间啊啊啊啊啊
			Calendar wtf = Calendar.getInstance();
			wtf.setTime(new Date());
			wtf.set(Calendar.DAY_OF_MONTH, 1);
			wtf.add(Calendar.DAY_OF_MONTH, -1);
			wtf.set(Calendar.HOUR_OF_DAY, 23);
			wtf.set(Calendar.MINUTE, 59);
			wtf.set(Calendar.SECOND, 59);
			Date to = calendar.getTime();
			wtf.set(Calendar.DAY_OF_MONTH, 1);
			wtf.set(Calendar.HOUR_OF_DAY, 0);
			wtf.set(Calendar.MINUTE, 0);
			wtf.set(Calendar.SECOND, 0);
			Date from = wtf.getTime();
			SimpleDateFormat fml = new SimpleDateFormat(
					CommonDefine.COMMON_START_FORMAT);
			// ======end啊啊啊啊
			StringBuffer tableName = new StringBuffer();
			// ------！！！！！！！！！！
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
			SimpleDateFormat shortSdf = new SimpleDateFormat(
					CommonDefine.MS_REPORT_MONTHLY_FORMAT);
			SimpleDateFormat shortSdfPath = new SimpleDateFormat("yyyy-MM");
			SimpleDateFormat fullSdf = new SimpleDateFormat(
					CommonDefine.COMMON_FORMAT);
			tableName.append(CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA);
			tableName.append('_');
			tableName.append(sdf.format(time));

			// ========@测试用tableName@========
			// tableName.append(CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA);
			// tableName.append("_2013_12");
			// [emsid]
			taskInfo = processPmStdIndex(taskInfo);
			taskInfo.put("nendRx", CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG);
			taskInfo.put("nendTx", CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG);
			taskInfo.put("nendNA", CommonDefine.PM.PM_LOCATION_NA_FLAG);
			// 文件名！[报表任务名称]([报表周期]-[对象数据])_[实际性能事件日期]
			String taskName = taskInfo.get("taskName").toString();
			String taskPeriod = "月报";

			// 首先生成导出所用的ColumnMap（吧日期作为字段加进去）
			String taskDate = shortSdf.format(time);
			List<String> dates = new ArrayList<String>();
			// 把这个月的都加进去
			int month = time.getMonth(), year = time.getYear();
			boolean isLeapYear = (year % 100 == 0) ? (year % 400 == 0)
					: (year % 4 == 0);
			int days[] = { 31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31,
					30, 31, 30, 31 };
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-");
			for (int i = 0; i < 9; i++) {
				dates.add(sdf2.format(time) + "0" + (i + 1));
			}
			for (int i = 10; i < days[month]; i++) {
				dates.add(sdf2.format(time) + (i + 1));
			}
			ColumnMap[] neDailyCsvHeader = genColumn(neCsvBaseHeader, dates);
			ColumnMap[] neDailyXlsHeader = genColumn(neXlsBaseHeader, dates);
			String fileNameOrig = taskName + "(" + taskPeriod + "-原始数据)_"
					+ taskDate;
			String fileNameException = taskName + "(" + taskPeriod + "-异常数据)_"
					+ taskDate;
			String fileName = "0".equals(taskInfo.get("dataSrc").toString()) ? fileNameOrig
					: fileNameException;
			// 生成导出对象
			CsvUtil csvOrig = new CsvUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "/"
					+ CommonDefine.EXCEL.PM_BASE + "/"
					+ shortSdfPath.format(time) + "/"
					+ CommonDefine.EXCEL.PM_CSV + "/" + fileNameOrig + ".csv",
					neDailyCsvHeader);
			ExportResult erOrig = null;
			// then 导出异常数据
			CsvUtil csvEx = new CsvUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "/"
					+ CommonDefine.EXCEL.PM_BASE + "/"
					+ shortSdfPath.format(time) + "/"
					+ CommonDefine.EXCEL.PM_CSV + "/" + fileNameException
					+ ".csv", neDailyCsvHeader);
			ExportResult erEx = null;
			// 再导出到Excel
			String rptTpl = "网元"
					+ ("0".equals(taskInfo.get("dataSrc").toString()) ? "原始数据"
							: "异常数据") + "月报";
			PushExcelUtil xls = new PushExcelUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "/"
					+ CommonDefine.EXCEL.PM_BASE + "/"
					+ shortSdfPath.format(time) + "/"
					+ CommonDefine.EXCEL.PM_EXCEL + "/" + fileName + ".xlsx",
					neDailyXlsHeader, rptTpl);
			ExportResult erXls = null;

			int pageCnt = 1;

			if (taskNodes.size() <= 0) {
				xls.push(new ArrayList<Map>(), 0);
			}
			int startNe = 0, countNe = taskNodes.size(), endNe;
			List<Map> pmList = null;
			while (countNe > 0) {
				// 判断一下到不到了
				endNe = countNe - NE_INTERVAL > 0 ? startNe + NE_INTERVAL
						: startNe + countNe;
				// 一次处理N个Ne
				List<Map> partNe = taskNodes.subList(startNe, endNe);
				// 节点条件分层级筛选至各个EMS,然后将emsId拎出来变成key：[emsId，Map<nodeLvl,nodeIdStr>]
				Map<Integer, Map<String, Object>> emsNodeMaps = getConditionsFromNodesGroupByEmsIds(partNe);
				// [emsid]
				List<Map<String, Object>> tableNodesList = processTableNameIntoNodeCondition(
						emsNodeMaps, tableName);
				if (tableNodesList.size() > 0) {
					pmList = performanceManagerMapper.searchPMForNeMonthly(
							tableNodesList, taskInfo, -1L);
				} else {
					pmList = new ArrayList<Map>();
				}
				// TODO 田博士这就是你要导的东西pmList
				// 先导出原始数据到Csv
				csvOrig.append(pmList);
				// then 导出异常数据
				csvEx.append(pmList, true);
				// 最后把数据转化下，日期作为关键字，性能值作为值
				pmList = PMDataUtil.combineNeData(pmList, neCsvSrcHeader);
				// 再导出到Excel
				xls.push(pmList, endNe - startNe);

				startNe += NE_INTERVAL;
				countNe -= NE_INTERVAL;
			}
			xls.close();
			// 获取导出结果
			erOrig = csvOrig.getResult();
			erEx = csvEx.getResult();
			erXls = xls.getResult();
			// 导出结果信息入库-> t_pm_report_info
			Map<String, Object> exportInfo = new HashMap<String, Object>();
			exportInfo.put("SYS_TASK_ID", searchCond.get("taskId"));
			exportInfo.put("PRIVILEGE", taskInfo.get("privilege"));
			exportInfo.put("REPORT_NAME", fileName + ".xlsx");
			exportInfo
					.put("EXPORT_TIME", fullSdf.format(erXls.getExportTime()));
			exportInfo.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_REPORT_NE);
			exportInfo.put("PERIOD", 1);// 1是月报
			exportInfo.put("DATA_SRC", taskInfo.get("dataSrc"));
			exportInfo.put("CREATOR", taskInfo.get("creator"));
			exportInfo.put("SIZE", erXls.getSize());
			exportInfo.put("EXCEL_URL", erXls.getFilePath());
			exportInfo.put("NORMAL_CSV_PATH", erOrig.getFilePath());
			exportInfo.put("ABNORMAL_CSV_PATH", erEx.getFilePath());
			// FOR TEST
			// Date startDate, endDate;
			// SimpleDateFormat dateParser = new SimpleDateFormat(
			// "yyyy-MM-dd hh:mm:ss");
			// startDate = dateParser.parse("2013-12-01 00:00:00");
			// endDate = dateParser.parse("2013-12-31 23:59:59");
			// FOR TEST
			int taskId = Integer.parseInt(searchCond.get("taskId"));
			Map statInfo = calculateReportCountInfo(taskId, from, to);
			exportInfo
					.put("PM_EXCEPTION_LV1", statInfo.get("PM_EXCEPTION_LV1"));
			exportInfo
					.put("PM_EXCEPTION_LV2", statInfo.get("PM_EXCEPTION_LV2"));
			exportInfo
					.put("PM_EXCEPTION_LV3", statInfo.get("PM_EXCEPTION_LV3"));
			exportInfo
					.put("PM_ABNORMAL_RATE", statInfo.get("PM_ABNORMAL_RATE"));
			// 下面的貌似没用，先留着吧
			exportInfo.put("COLLECT_SUCCESS_RATE_NE",
					statInfo.get("COLLECT_SUCCESS_RATE"));
			exportInfo.put("FAILED_ID_NE", statInfo.get("FAILED_ID"));
			Map idMap = new HashMap();
			performanceManagerMapper.savePmExportInfo(exportInfo, idMap);
			performanceManagerMapper.savePmAnalysisInfo(exportInfo,
					CommonDefine.TARGET_TYPE_MAP, idMap);
			// ...
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	/**
	 * 合并所有的性能数据
	 * 
	 * @param taskInfo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	Map processPmStdIndex(Map taskInfo) {
		if (taskInfo.get("sdhPm") != null
				&& !taskInfo.get("sdhPm").toString().isEmpty()) {
			String sdhPm = taskInfo.get("sdhPm").toString()
					.replaceAll(",", "','");
			taskInfo.put("sdhPm", "'" + sdhPm + "'");
		}
		if (taskInfo.get("wdmPm") != null
				&& !taskInfo.get("wdmPm").toString().isEmpty()) {
			String wdmPm = taskInfo.get("wdmPm").toString()
					.replaceAll(",", "','");
			taskInfo.put("wdmPm", "'" + wdmPm + "'");
		}
		if (taskInfo.get("wdmTp") != null
				&& !taskInfo.get("wdmTp").toString().isEmpty()) {
			String wdmTp = taskInfo.get("wdmTp").toString()
					.replaceAll(",", "','");
			taskInfo.put("wdmTp", "'" + wdmTp + "'");
		}
		if (taskInfo.get("sdhTp") != null
				&& !taskInfo.get("sdhTp").toString().isEmpty()) {
			String sdhTp = taskInfo.get("sdhTp").toString()
					.replaceAll(",", "','");
			taskInfo.put("sdhTp", "'" + sdhTp + "'");
		}
		return taskInfo;
	}

	// 处理网管节点信息，变成[nodeId，nodeLevel，emsId]的格式
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Map> processEmsToStdFormat(List<Map> taskNodes) {
		List<Map> emsNode = new ArrayList<Map>();
		if (taskNodes != null) {
			for (Map m : taskNodes) {
				if (m.get("nodeLevel").toString()
						.equals(TREE_DEFINE.get("NODE_EMS").toString())) {
					m.put("emsId", m.get("nodeId"));
					emsNode.add(m);
				}
			}
		}
		return emsNode;
	}

	// 变成[nodeId，nodeLevel，emsId]的格式
	@SuppressWarnings("rawtypes")
	public List<Map> processAllNodesToStdFormat(List<Map> taskNodes) {
		// 分类节点信息以供查询
		Map<String, String> conditionMap = nodeListClassify(taskNodes);
		List<Map> node = new ArrayList<Map>();
		// [nodeId，nodeLevel，emsId]格式的数据
		List<Map> nodeCond = new ArrayList<Map>();
		// EMS类型的节点转为标准格式
		if (conditionMap.get("NODE_EMS") != null) {
			node = processEmsToStdFormat(taskNodes);
			nodeCond.addAll(node);
		}
		// 将SUBNET转为NE(nodeLevel,nodeId,emsId)
		if (conditionMap.get("NODE_SUBNET") != null) {
			node = performanceManagerMapper.processSubnetToNe(TREE_DEFINE,
					conditionMap, REPORT_DEFINE);
			nodeCond.addAll(node);
		}
		// 将NE加上emsId(nodeLevel,nodeId,emsId)
		if (conditionMap.get("NODE_NE") != null) {
			node = performanceManagerMapper.processNe(TREE_DEFINE,
					conditionMap, REPORT_DEFINE);
			nodeCond.addAll(node);
		}
		return nodeCond;
	}

	// 将表名结合进node条件里
	List<Map<String, Object>> processTableNameIntoNodeCondition(
			Map<Integer, Map<String, Object>> emsNodeMaps,
			StringBuffer tableName) {
		List<Map<String, Object>> returnResult = new ArrayList<Map<String, Object>>();
		for (Iterator<Integer> it = emsNodeMaps.keySet().iterator(); it
				.hasNext();) {
			Integer key = it.next();
			Map<String, Object> nodeMap = emsNodeMaps.get(key);
			StringBuffer name = new StringBuffer(tableName);
			name.insert(CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA.length(),
					'_' + key.toString());
			Integer existance = performanceManagerMapper.getPmTableExistance(
					name.toString(), SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
			if (existance != 1) {
				continue;
			}
			nodeMap.put("tableName", name);
			returnResult.add(nodeMap);
		}
		return returnResult;
	}

	// Map<Integer, Map<String, Object>> processTableNameIntoNodeCondition(
	// Map<Integer, Map<String, Object>> emsNodeMaps,
	// StringBuffer tableName) {
	// for (Iterator<Integer> it = emsNodeMaps.keySet().iterator(); it
	// .hasNext();) {
	// Integer key = it.next();
	// Map<String, Object> nodeMap = emsNodeMaps.get(key);
	// tableName.insert(
	// CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA.length(),
	// '_' + key.toString());
	// nodeMap.put("tableName", tableName);
	// emsNodeMaps.put(key, nodeMap);
	// }
	// return emsNodeMaps;
	// }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> searchNeReportAnalysis(
			Map<String, String> searchCond) throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			String startTime = searchCond.get("startTime");
			String endTime = searchCond.get("endTime");
			searchCond.put("startTime", startTime + " 00:00:00");
			searchCond.put("endTime", endTime + " 23:59:59");
			// 网元报表只可能有一个监视统计
			returnList = performanceManagerMapper
					.searchReportAnalysis(searchCond);
			for (int i = 0; i < returnList.size(); i++) {
				Map m = returnList.get(i);
				StringBuffer abnormal = new StringBuffer();
				abnormal.append("<font color=blue>一般：</font>");
				abnormal.append(m.get("lv1"));
				abnormal.append("<font color=orange> 次要：</font>");
				abnormal.append(m.get("lv2"));
				abnormal.append("<font color=red> 重要：</font>");
				abnormal.append(m.get("lv3"));
				m.put("abnormal", abnormal.toString());
				returnList.set(i, m);
			}
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

		return returnData;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> searchMSReportAnalysis(
			Map<String, String> searchCond) throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			String startTime = searchCond.get("startTime");
			String endTime = searchCond.get("endTime");
			searchCond.put("startTime", startTime + " 00:00:00");
			searchCond.put("endTime", endTime + " 23:59:59");
			returnList = performanceManagerMapper
					.searchReportAnalysis(searchCond);
			List<Map> list = new ArrayList<Map>();
			for (int i = 0; i < returnList.size(); i += 2) {
				// 每个复用段报表必有2个统计信息
				Map m1 = returnList.get(i);
				Map m2 = returnList.get(i + 1);

				StringBuffer abnormal = new StringBuffer();
				abnormal.append("<font color=blue>一般：</font>");
				abnormal.append(m1.get("lv1"));
				abnormal.append("<font color=orange> 次要：</font>");
				abnormal.append(m1.get("lv2"));
				abnormal.append("<font color=red> 重要：</font>");
				abnormal.append(m1.get("lv3"));
				m1.put("abnormal", abnormal.toString());
				if (Integer.parseInt(m1.get("targetType").toString()) == CommonDefine.TASK_TARGET_TYPE.MULTI_SEC) {
					m1.put("MSCollectSuccessRate", m1.get("collectSuccessRate"));
					m1.put("MSFailedId", m1.get("failedId"));
				} else if (Integer.parseInt(m1.get("targetType").toString()) == CommonDefine.TASK_TARGET_TYPE.PTP) {
					m1.put("ptpCollectSuccessRate",
							m1.get("collectSuccessRate"));
					m1.put("ptpFailedId", m1.get("failedId"));
				}
				if (Integer.parseInt(m2.get("targetType").toString()) == CommonDefine.TASK_TARGET_TYPE.MULTI_SEC) {
					m1.put("MSCollectSuccessRate", m2.get("collectSuccessRate"));
					m1.put("MSFailedId", m2.get("failedId"));
				} else if (Integer.parseInt(m2.get("targetType").toString()) == CommonDefine.TASK_TARGET_TYPE.PTP) {
					m1.put("ptpCollectSuccessRate",
							m2.get("collectSuccessRate"));
					m1.put("ptpFailedId", m2.get("failedId"));
				}
				list.add(m1);
			}
			returnData.put("rows", list);
			returnData.put("total", list.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchCollectFailedNeInfo(
			Map<String, String> searchCond) throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			returnList = performanceManagerMapper.searchCollectFailedNeInfo(
					searchCond, REPORT_DEFINE);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchCollectFailedPtpInfo(
			Map<String, String> searchCond) throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			returnList = performanceManagerMapper.searchCollectFailedPtpInfo(
					searchCond, REPORT_DEFINE);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchCollectFailedMSInfo(
			Map<String, String> searchCond) throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			returnList = performanceManagerMapper.searchCollectFailedMSInfo(
					searchCond, REPORT_DEFINE);
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchReportDetailNePm(
			Map<String, String> searchCond, int start, int limit)
			throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		LineIterator it;
		try {
			String filePath = null;
			if (searchCond.get("dataSrc").equals("normal"))
				filePath = performanceManagerMapper
						.getCSVFilePathByReportId(searchCond);
			else if (searchCond.get("dataSrc").equals("abnormal"))
				filePath = performanceManagerMapper
						.getFailedCSVFilePathByReportId(searchCond);
			File file = new File(filePath);
			it = FileUtils.lineIterator(file, "GB2312");
			try {
				int count = 0;
				List<String> list = new ArrayList<String>();
				while (it.hasNext()) {
					String line = it.nextLine();
					if (count >= start && count < start + limit) {
						list.add(line);
					}
					count++;
				}
				for (String record : list) {
					Map<String, String> rec = new HashMap<String, String>();
					String[] recSplit = record.split(",");
					for (int i = 0; i < recSplit.length; i++) {
						rec.put(PM_REPORT_NE_DETAIL_HEADER[i], recSplit[i]);
					}
					returnList.add(rec);
				}
				returnData.put("rows", returnList);
				returnData.put("total", count);
			} finally {
				it.close();
			}
		} catch (IOException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_REPORT_FILE_READ_FAILED);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_REPORT_FILE_READ_FAILED);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> searchReportDetailMSPm(
			Map<String, String> searchCond, int start, int limit)
			throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		LineIterator it = null;
		try {
			try {
				int count = 0;
				List<String> list = new ArrayList<String>();
				String filePath = null;
				if (searchCond.get("dataSrc").equals("normal")) {
					filePath = performanceManagerMapper
							.getCSVFilePathByReportId(searchCond);

				}
				if (searchCond.get("dataSrc").equals("abnormal")) {

					filePath = performanceManagerMapper
							.getFailedCSVFilePathByReportId(searchCond);

				}
				File file = new File(filePath);
				it = FileUtils.lineIterator(file, "GB2312");
				while (it.hasNext()) {
					String line = it.nextLine();
					if (count >= start && count < start + limit) {
						list.add(line);
					}
					count++;
				}
				for (String record : list) {
					Map<String, String> rec = new HashMap<String, String>();
					String[] recSplit = record.split(",");
					for (int i = 0; i < recSplit.length; i++) {
						rec.put(PM_REPORT_MS_DETAIL_HEADER[i], recSplit[i]);
					}
					returnList.add(rec);
				}
				returnData.put("rows", returnList);
				returnData.put("total", count);
			} finally {
				it.close();
			}
		} catch (IOException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_REPORT_FILE_READ_FAILED);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_REPORT_FILE_READ_FAILED);
		}
		return returnData;
	}

	// TODO
	@SuppressWarnings("rawtypes")
	@Override
	public String exportAndDownloadPmAnalysisInfo(
			Map<String, String> searchCond, List<Map> exportData)
			throws CommonException {
		String resultMessage = "";
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		String fileName = searchCond.get("filename");
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",
				1000);
		try {
			// 导出数据
			if (Integer.parseInt(searchCond.get("exportType")) == ANALYSIS_EXPORT_TYPE_NE)
				resultMessage = ex2.writeExcel(exportData,
						CommonDefine.EXCEL.NE_ANALYSIS_HEADER_CODE, false);
			if (Integer.parseInt(searchCond.get("exportType")) == ANALYSIS_EXPORT_TYPE_MS)
				resultMessage = ex2.writeExcel(exportData,
						CommonDefine.EXCEL.MS_ANALYSIS_HEADER_CODE, false);
			if (Integer.parseInt(searchCond.get("exportType")) == ANALYSIS_EXPORT_TYPE_CFMS)
				resultMessage = ex2.writeExcel(exportData,
						CommonDefine.EXCEL.CFMS_ANALYSIS_HEADER_CODE, false);
			if (Integer.parseInt(searchCond.get("exportType")) == ANALYSIS_EXPORT_TYPE_CFPTP)
				resultMessage = ex2.writeExcel(exportData,
						CommonDefine.EXCEL.CFPTP_ANALYSIS_HEADER_CODE, false);
			if (Integer.parseInt(searchCond.get("exportType")) == ANALYSIS_EXPORT_TYPE_CFNE)
				resultMessage = ex2.writeExcel(exportData,
						CommonDefine.EXCEL.CFNE_ANALYSIS_HEADER_CODE, false);
		} catch (Exception e) {
			e.printStackTrace();
			ex2.close();
			return resultMessage;
		}
		return resultMessage;
	}

	/* wwwwwwwwwwwwwwwwwwwwwwwwwwwwww */
	@SuppressWarnings("rawtypes")
	@Override
	public void controlReportTaskTime() throws CommonException {
		List<Map> taskList = performanceManagerMapper.getAllReportTask(DEFINE);
		if (taskList == null)
			return;
		for (Map m : taskList) {
			try {
				int taskId = Integer.parseInt(m.get("taskId").toString());
				int hour = Integer.parseInt(m.get("hourAfter").toString());
				int periodType = Integer.parseInt(m.get("periodType")
						.toString());
				int taskType = Integer.parseInt(m.get("taskType").toString());
				int delay = Integer.parseInt(m.get("delay").toString());
				addOrEditReportQuartzTask(taskId, hour, periodType, taskType,
						delay);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new CommonException(e,
						MessageCodeDefine.PM_REPORT_ADD_QUARTZ_TASK_FAILED);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addOrEditReportQuartzTask(int taskId, int hour,int minute,
			int periodType, int taskType, int delay,Class job) throws CommonException {
		// 处理任务执行时间中的小时和分钟
		try {
			Calendar time = Calendar.getInstance();
			time.set(Calendar.HOUR_OF_DAY, hour);
			time.set(Calendar.MINUTE, minute);
			// 生成cron表达式
			String cronExpression = getCronExpression(periodType, delay,
					time);
			Map jobParam = new HashMap();
			if (periodType == CommonDefine.PM.PM_REPORT.PERIOD.DAILY)
				jobParam.put("IS_MONTHLY_REPORT", false);
			else if (periodType == CommonDefine.PM.PM_REPORT.PERIOD.MONTHLY)
				jobParam.put("IS_MONTHLY_REPORT", true);
			if (quartzManagerService.IsJobExist(taskType, taskId)) {
				// 如果任务存在，就修改它的执行时间
				quartzManagerService.modifyJobTime(taskType, taskId,
						cronExpression);
			} else {
				// 如果不存在，则新建
				// TODO job待修改
				quartzManagerService.addJob(taskType, taskId,
						job, cronExpression, jobParam);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.PM_REPORT_ADD_QUARTZ_TASK_FAILED);
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addOrEditReportQuartzTask(int taskId, int hour,
			int periodType, int taskType, int delay) throws CommonException {
		// 处理任务执行时间中的小时和分钟
		Calendar collectEnd;
		try {
			collectEnd = checkEmsCollectEndTimeUnderThisTask(taskId, taskType);
			if (collectEnd == null)
				return;
			collectEnd.add(Calendar.HOUR_OF_DAY, hour);
			// 生成cron表达式
			String cronExpression = getCronExpression(periodType, delay,
					collectEnd);
			Map jobParam = new HashMap();
			if (periodType == CommonDefine.PM.PM_REPORT.PERIOD.DAILY)
				jobParam.put("IS_MONTHLY_REPORT", false);
			else if (periodType == CommonDefine.PM.PM_REPORT.PERIOD.MONTHLY)
				jobParam.put("IS_MONTHLY_REPORT", true);

			if (quartzManagerService.IsJobExist(taskType, taskId)) {
				// 如果任务存在，就修改它的执行时间
				quartzManagerService.modifyJobTime(taskType, taskId,
						cronExpression);
			} else {
				// 如果不存在，则新建
				// TODO job待修改
				if (taskType == CommonDefine.QUARTZ.JOB_REPORT_NE)
					quartzManagerService.addJob(taskType, taskId,
							PmReportJob.class, cronExpression, jobParam);
				else if (taskType == CommonDefine.QUARTZ.JOB_REPORT_MS)
					quartzManagerService.addJob(taskType, taskId,
							PmReportJob.class, cronExpression, jobParam);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.PM_REPORT_ADD_QUARTZ_TASK_FAILED);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Calendar checkEmsCollectEndTimeUnderThisTask(int taskId,
			int taskType) throws ParseException {
		String collectEndTime = null;
		// 查询出该任务下的所有节点信息（nodeId，nodeLevel）包括EMS,NE,SUBNET
		Map searchCond = new HashMap();
		searchCond.put("taskId", taskId);
		List<Map> nodeList = performanceManagerMapper
				.searchTaskNodesForEdit(searchCond);

		if (taskType == CommonDefine.QUARTZ.JOB_REPORT_NE) {
			Map<String, String> taskNodes = nodeListClassify(nodeList);
			collectEndTime = performanceManagerMapper.getCollectEndTimeNe(
					DEFINE, taskNodes);
		}
		if (taskType == CommonDefine.QUARTZ.JOB_REPORT_MS) {
			int nodeType = Integer.parseInt(nodeList.get(0).get("nodeLevel")
					.toString());
			collectEndTime = performanceManagerMapper.getCollectEndTimeMS(
					DEFINE, nodeType, nodeList);
		}
		if (collectEndTime == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date collectEndDate = sdf.parse(collectEndTime);
		Calendar collectEndCld = Calendar.getInstance();
		collectEndCld.setTime(collectEndDate);
		return collectEndCld;
	}

	private String getCronExpression(int periodType, int delay,
			Calendar collectEnd) {
		StringBuffer cronExpression = new StringBuffer();
		/* 日报 */if (periodType == CommonDefine.PM.PM_REPORT.PERIOD.DAILY) {
			cronExpression.append("0");
			cronExpression.append(" ");
			cronExpression.append(collectEnd.get(Calendar.MINUTE));
			cronExpression.append(" ");
			cronExpression.append(collectEnd.get(Calendar.HOUR_OF_DAY));
			cronExpression.append(" * * ? *");
		}
		/* 月报 */else if (periodType == CommonDefine.PM.PM_REPORT.PERIOD.MONTHLY) {
			cronExpression.append("0");
			cronExpression.append(" ");
			cronExpression.append(collectEnd.get(Calendar.MINUTE));
			cronExpression.append(" ");
			cronExpression.append(collectEnd.get(Calendar.HOUR_OF_DAY));
			cronExpression.append(" ");
			cronExpression.append(delay);
			cronExpression.append(" * ? *");
		}
		return cronExpression.toString();
	}

	/* wwwwwwwwwwwwwwwwwwwwwwwwwwwwww */

	@SuppressWarnings("rawtypes")
	@Override
	public ExportResult generateNeReportImmediately(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException {
		ExportResult erXls = null;
		try {
			Map<String, String> conditionMap = nodeListClassify(nodeList);

			// 检查一下列表中存在的网元数量，超过500个将不进行保存
			int neCount = performanceManagerMapper.getCountOfNeUnderThisNode(
					conditionMap, RegularPmAnalysisDefine);
			if (neCount > 500)
				return null;
			List<Map<String, String>> nodes = procNodes(nodeList);
			Date startDate = new Date();
			Date endDate = new Date();

			// 现在开始把条件处理成查询性能报表的样子
			// 先将节点们处理成[nodeId，nodeLevel，emsId]格式的数据
			List<Map> nodeCond = processAllNodesToStdFormat(nodeList);
			// 换算为NE
			nodeList = instantReportMapper.getNeUnderThisNode(conditionMap,
					DEFINE, TREE_DEFINE);

			// 然后将emsId拎出来变成key，变成一个[emsId，Map<nodeLvl,nodeIdStr>]的map
			// Map<Integer, Map<String, Object>> emsNodeMaps =
			// getConditionsFromNodesGroupByEmsIds(nodeCond);
			String period = searchCond.get("period");

			// 合并性能事件
			searchCond = processPmStdIndex(searchCond);
			// 方向条件
			searchCond.put("nendRx", String
					.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG));
			searchCond.put("nendTx", String
					.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG));
			searchCond.put("nendNA", String
					.valueOf(CommonDefine.PM.PM_LOCATION_NA_FLAG));

			// 日期字段
			List<String> dates = new ArrayList<String>();
			String timeRnge = "";
			List<Map<String, Object>> tableNodesList = null;
			SimpleDateFormat shortSdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat cnSdfYmd = new SimpleDateFormat("yyyy年M月d日");
			SimpleDateFormat cnSdfYm = new SimpleDateFormat("yyyy年M月");
			// 先处理按天查询的报表
			Long count = 0L;
			if ("0".equals(period)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				startDate = sdf.parse(searchCond.get("start"));
				endDate = sdf.parse(searchCond.get("end"));
				// 为了让sql加上日期条件
				searchCond.put("retrivalTime", "1");
				// 设置日期参数
				sdf.applyPattern(CommonDefine.COMMON_START_FORMAT);
				searchCond.put("retrivalTimeStart", sdf.format(startDate));
				sdf.applyPattern(CommonDefine.COMMON_END_FORMAT);
				searchCond.put("retrivalTimeEnd", sdf.format(endDate));

				// // 将tablename放到条件中去，和他们的ems下的node放在一起
				// tableNodesList = putNodesUndertableName(startDate, endDate,
				// emsNodeMaps);
				//
				// // 先数出一共有多少条记录
				// count = performanceManagerMapper.searchPMForNeDailyCount(
				// tableNodesList, searchCond);

				// 获取时间范围字符串
				timeRnge = cnSdfYmd.format(startDate) + "-"
						+ cnSdfYmd.format(endDate) + "-每日";
				// 获取时间范围内的所有天数
				Long startTime = startDate.getTime();
				Long endTime = endDate.getTime();
				Long timeStep = 24 * 3600L * 1000;
				while (startTime <= endTime) {
					Date someDay = new Date(startTime);
					dates.add(sdf.format(someDay).substring(0, 10));
					// System.out.println("Add -> "
					// + sdf.format(someDay).substring(0, 10));
					startTime += timeStep;
				}
			} else
			// 按月-就是每月几号抽样生成的那种
			if ("1".equals(period)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				startDate = sdf.parse(searchCond.get("start"));
				endDate = sdf.parse(searchCond.get("end"));
				// 将tablename放到条件中去，和他们的ems下的node放在一起
				// tableNodesList = putNodesUndertableName(startDate, endDate,
				// emsNodeMaps);

				// 按月的报表，其实只需要一个DAY作为条件就行，因为分表的缘故，每张表会自动分月
				Calendar pmDate = Calendar.getInstance();
				int pmDay = Integer.parseInt(searchCond.get("pmDate"));
				if (pmDay < 10) {
					searchCond.put("pmDate", "0" + pmDay);
				}
				pmDate.set(Calendar.DAY_OF_YEAR, pmDay);
				sdf.applyPattern(CommonDefine.COMMON_SIMPLE_FORMAT);
				String retrievalTimePmDate = sdf.format(pmDate.getTime());
				searchCond.put("retrievalTimePmDate", retrievalTimePmDate);

				// 数数有多少条记录啊
				// count = performanceManagerMapper.searchPMForNeDailyCount(
				// tableNodesList, searchCond);

				// TODO 这之后也就是和日报一样的性能查询了！
				timeRnge = cnSdfYm.format(startDate) + "-"
						+ cnSdfYm.format(endDate) + "-每月" + pmDay + "日";
				// 获取时间范围内的所有天数
				Long startTime = startDate.getTime();
				Long endTime = endDate.getTime();
				Long timeStep = 24 * 3600L * 1000;
				while (startTime <= endTime) {
					Date someDay = new Date(startTime);
					if (someDay.getDate() == pmDay) {
						dates.add(sdf.format(someDay).substring(0, 10));
					}
					startTime += timeStep;
				}
				dates.add(searchCond.get("end") + "-"
						+ (pmDay > 9 ? pmDay : "0" + pmDay));
			}
			// 拼装文件名！省网四期（2013年10月1号-2013年12月5日-每月2号-异常数据）_即时报表_2013年12月28日
			String taskName = searchCond.get("taskName").toString();
			String[] dataSrc = { "原始数据", "异常数据" };
			String taskDate = shortSdf.format(new Date());
			String fileName = taskName
					+ "("
					+ timeRnge
					+ "-"
					+ dataSrc[Integer.parseInt(searchCond.get("dataSrc")
							.toString())] + ")_" + "即时报表" + "_" + taskDate
					+ ".xlsx";
			String rptTpl = "网元"
					+ dataSrc[Integer.parseInt(searchCond.get("dataSrc")
							.toString())] + "即时报表";
			// 导出到Excel
			// 首先生成导出所用的ColumnMap（吧日期作为字段加进去）
			ColumnMap[] neDailyXlsHeader = genColumn(neXlsBaseHeader, dates);
			PushExcelUtil xls = new PushExcelUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.INSTANT_REPORT_DIR + "\\" + fileName,
					neDailyXlsHeader, rptTpl);

			int pageCnt = 1;
			Long start = 0L;
			// Map<String, Object> taskInfo = new HashMap<String, Object>();
			if (neCount <= 0) {
				xls.push(new ArrayList<Map>(), 1);
			}
			List<Map> pmList = null;

			int startNe = 0, countNe = nodeList.size(), endNe;
			while (countNe > 0) {
				// 判断一下到不到了
				endNe = countNe - NE_INTERVAL > 0 ? startNe + NE_INTERVAL
						: startNe + countNe;
				// 一次处理N个Ne
				List<Map> partNe = nodeList.subList(startNe, endNe);
				// 节点条件分层级筛选至各个EMS,然后将emsId拎出来变成key：[emsId，Map<nodeLvl,nodeIdStr>]
				Map<Integer, Map<String, Object>> emsNodeMaps = getConditionsFromNodesGroupByEmsIds(partNe);
				// 将tablename放到条件中去，和他们的ems下的node放在一起
				tableNodesList = putNodesUndertableName(startDate, endDate,
						emsNodeMaps);
				if (tableNodesList.size() > 0) {
					pmList = performanceManagerMapper.searchPMForNeDaily(
							tableNodesList, searchCond, -1L);
				} else {
					pmList = new ArrayList<Map>();
				}
				// 把数据转化下，日期作为关键字，性能值作为值
				pmList = PMDataUtil.combineNeData(pmList, neCsvSrcHeader);

				// 再导出到Excel
				xls.push(pmList, endNe - startNe);

				startNe += NE_INTERVAL;
				countNe -= NE_INTERVAL;
			}
			CoverGenerator cov = new CoverGenerator(xls.getXls(), searchCond,
					nodes);
			cov.genNeCover();
			xls.close();
			// 获取导出结果
			erXls = xls.getResult();
			// 导出结果信息入库-> t_pm_report_info
			Map<String, Object> exportInfo = new HashMap<String, Object>();
			exportInfo.put("SYS_TASK_ID", searchCond.get("taskId"));
			exportInfo.put("REPORT_NAME", fileName);
			exportInfo.put("EXPORT_TIME", erXls.getExportTime());
			exportInfo.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_REPORT_NE);
			exportInfo.put("PERIOD", searchCond.get("period"));// 0是日报,1是月报
			exportInfo.put("DATA_SRC",
					Integer.parseInt(searchCond.get("dataSrc")));
			exportInfo.put("CREATOR", currentUserId);
			exportInfo.put("SIZE", erXls.getSize());
			exportInfo.put("EXCEL_URL", erXls.getFilePath());
			exportInfo.put("NORMAL_CSV_PATH", "");
			exportInfo.put("ABNORMAL_CSV_PATH", "");
			exportInfo.put("PRIVILEGE", searchCond.get("privilege"));
			Map idMap = new HashMap();
			performanceManagerMapper.savePmExportInfo(exportInfo, idMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return erXls;
	}

	// 将表名结合进node条件里
	private List<Map<String, Object>> putNodesUndertableName(Date start,
			Date end, Map<Integer, Map<String, Object>> emsNodeMaps) {
		List<Map<String, Object>> returnResult = new ArrayList<Map<String, Object>>();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(start);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(end);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
		for (Iterator<Integer> it = emsNodeMaps.keySet().iterator(); it
				.hasNext();) {
			Integer key = it.next();
			Map<String, Object> nodeMap = emsNodeMaps.get(key);

			endDate.add(Calendar.MONTH, 1);
			while (startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)) {
				Map<String, Object> e = new HashMap<String, Object>();
				e.putAll(nodeMap);
				String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
						+ "_" + key.toString() + "_"
						+ sdf.format(startDate.getTime());
				Integer existance = performanceManagerMapper
						.getPmTableExistance(tableName,
								SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
				startDate.add(Calendar.MONTH, 1);
				if (existance != 1) {
					continue;
				}
				e.put("tableName", tableName);
				returnResult.add(e);
			}
		}
		return returnResult;
	}

	@Override
	public void saveMSCutoverTask(List<Long> condList,
			Map<String, String> searchCond, Integer userId)
			throws CommonException {
		try {
			// 返回的新Id
			Map<String, Long> idMap = new HashMap<String, Long>();
			// t_sys_task
			performanceManagerMapper.saveMSCutoverTask(searchCond, userId,
					CommonDefine.QUARTZ.JOB_WDMMS_CUTOVER, idMap);
			// t_sys_task_info
			performanceManagerMapper.saveMSCutoverTaskNodesInfo(condList,
					idMap, CommonDefine.TASK_TARGET_TYPE.MULTI_SEC);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@Override
	public Map<String, Object> searchCutoverTask(
			Map<String, String> searchCond, Integer userId, int limit, int start)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			SimpleDateFormat sdfCommon = new SimpleDateFormat(
					CommonDefine.COMMON_SIMPLE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat();
			if (searchCond.get("start") != null
					&& !searchCond.get("start").isEmpty()) {
				Date startDate = sdfCommon.parse(searchCond.get("start"));
				sdf.applyPattern(CommonDefine.COMMON_START_FORMAT);
				searchCond.put("start", sdf.format(startDate));
			}
			if (searchCond.get("end") != null
					&& !searchCond.get("end").isEmpty()) {
				Date endDate = sdfCommon.parse(searchCond.get("end"));
				sdf.applyPattern(CommonDefine.COMMON_END_FORMAT);
				searchCond.put("end", sdf.format(endDate));
			}
			returnList = performanceManagerMapper.searchCutoverTask(searchCond,
					userId, start, limit, DEFINE);
			int count = performanceManagerMapper.searchCutoverTaskCount(
					searchCond, userId, DEFINE);
			returnData.put("rows", returnList);
			returnData.put("total", count);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public Map<String, Object> loadTaskNameCombo(
			Map<String, String> searchCond, Integer userId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			// 同一用户组
			returnList = performanceManagerMapper.loadTaskNameCombo(searchCond,
					DEFINE, userId);
			Map<String, String> allMap = new HashMap<String, String>();
			allMap.put("taskId", "0");
			allMap.put("taskName", "全部");
			returnList.add(0, allMap);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public Map<String, Object> getMSById(Map<String, String> searchCond)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			// 同一用户组
			returnList = performanceManagerMapper.getMSById(searchCond, DEFINE);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public void updateMSTask(Map<String, String> searchCond, List<Long> condList)
			throws CommonException {
		try {

			performanceManagerMapper.updateMSCutoverTask(searchCond, DEFINE);
			performanceManagerMapper.deleteMSCutoverTaskInfo(searchCond);
			// t_sys_task_info
			Map<String, Long> idMap = new HashMap<String, Long>();
			idMap.put("newId", Long.valueOf(searchCond.get("taskId")));
			performanceManagerMapper.saveMSCutoverTaskNodesInfo(condList,
					idMap, CommonDefine.TASK_TARGET_TYPE.MULTI_SEC);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public void deleteCutoverTask(List<Long> condList) throws CommonException {
		try {
			Map<String, String> map = new HashMap<String, String>();
			for (Long id : condList) {
				map.put("taskId", id.toString());
				performanceManagerMapper.deleteMSCutoverTaskInfo(map);
				performanceManagerMapper.deleteMSCutoverTask(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchMultiplexSection(
			Map<String, String> searchCond) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {

			List<Map> nodeList = performanceManagerMapper
					.searchTaskNodesForEdit(searchCond);
			String targetType = nodeList.get(0).get("nodeLevel").toString();
			returnList = performanceManagerMapper.searchMultiplexSection(
					nodeList, targetType, DEFINE);
			for (int i = 0; i < returnList.size(); i++) {
				if (returnList.get(i).get("SEC_STATE_CUTOVER") == null) {
					returnList.get(i).put("SEC_STATE_CUTOVER", -1);
				}
			}
			returnData.put("rows", returnList);
			returnData.put("total", returnList.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;

	}

	// TODO
	// **************************************咯咯咯咯咯咯*********************************

	@SuppressWarnings("rawtypes")
	@Override
	public Map searchOpticalPath(
			OpticalPathMonitorModel opticalPathMonitorModel, int start,
			int limit) throws CommonException {

		// 获取选择目标
		List<Map> selectTargets = opticalPathMonitorModel.getSelectTargetsMap() != null ? opticalPathMonitorModel
				.getSelectTargetsMap() : new ArrayList<Map>();

		int total = 0;

		// 链路编号
		Integer linkId = opticalPathMonitorModel.getLinkId();
		// 链路名称
		String linkName = opticalPathMonitorModel.getLinkName();
		// 采集日期
		String collectDate = opticalPathMonitorModel.getCollectDate();
		if (collectDate == null || collectDate.isEmpty()) {
			SimpleDateFormat sf = new SimpleDateFormat(
					CommonDefine.COMMON_SIMPLE_FORMAT);
			collectDate = sf.format(CommonUtil.getSpecifiedDay(new Date(), -1,
					0));
		}
		// 目标类型--网管
		Integer targetType_ems = 1;
		// 目标类型--网元
		Integer targetType_nes = 2;
		// ems集合
		List<Integer> emsConnectionIds = new ArrayList<Integer>();
		// 网元集合
		List<Integer> neIds = new ArrayList<Integer>();

		Integer[] linkType = new Integer[] { CommonDefine.LINK_OUT };
		// 查询结果
		List<Map> rows = new ArrayList<Map>();

		// 目标Id分类
		for (Map target : selectTargets) {
			int nodeLevel = Integer.valueOf(target.get(
					CommonDefine.TREE.PROPERTY_NODE_LEVEL).toString());
			int nodeId = Integer.valueOf(target.get(
					CommonDefine.TREE.PROPERTY_NODE_ID).toString());

			switch (nodeLevel) {
			// 获取网管Id
			case CommonDefine.TREE.NODE.EMSGROUP:
				List<Map> emsList = performanceManagerMapper
						.selectEmsListByEmsGroupId(nodeId, CommonDefine.FALSE);
				for (Map ems : emsList) {
					emsConnectionIds.add(Integer.valueOf(ems.get(
							"BASE_EMS_CONNECTION_ID").toString()));
				}
				break;
			case CommonDefine.TREE.NODE.EMS:
				emsConnectionIds.add(nodeId);
				break;
			// 获取网元Id集合
			case CommonDefine.TREE.NODE.SUBNET:
				// 获取所有子网id
				List<Integer> subnets = getSubnetChildren(nodeId);
				subnets.add(nodeId);
				List<Map> neList = performanceManagerMapper
						.selectNeListBySubnetIds(subnets, CommonDefine.FALSE);
				for (Map ne : neList) {
					neIds.add(Integer.valueOf(ne.get("BASE_NE_ID").toString()));
				}
				break;
			case CommonDefine.TREE.NODE.NE:
				neIds.add(nodeId);
				break;
			}
		}
		// 设置非法id
		if (emsConnectionIds.size() == 0) {
			emsConnectionIds.add(-999);
		}
		if (neIds.size() == 0) {
			neIds.add(-999);
		}

		if (selectTargets.size() == 0) {

			total = performanceManagerMapper
					.selectExternalLinkCountByConditions(null, null, linkId,
							linkName, linkType, CommonDefine.TRUE,
							CommonDefine.FALSE);

			rows.addAll(performanceManagerMapper
					.selectExternalLinkListByConditions(null, null, linkId,
							linkName, linkType, start, limit,
							CommonDefine.TRUE, CommonDefine.FALSE));
		} else {
			// 获取链路信息总数,分页用
			int linkCountByEms = performanceManagerMapper
					.selectExternalLinkCountByConditions(targetType_ems,
							emsConnectionIds, linkId, linkName, linkType,
							CommonDefine.TRUE, CommonDefine.FALSE);

			int linkCountByNe = performanceManagerMapper
					.selectExternalLinkCountByConditions(targetType_nes, neIds,
							linkId, linkName, linkType, CommonDefine.TRUE,
							CommonDefine.FALSE);
			// List<Map> rows = new ArrayList<Map>();
			// ems获取链路数与startNumber差值
			int offsetEms = linkCountByEms - start;
			// 取ems获取链路数据
			if (offsetEms > 0) {
				if (offsetEms > limit || offsetEms == limit) {
					// 取ems获取链路数据start = start,limit = limit;
					rows.addAll(performanceManagerMapper
							.selectExternalLinkListByConditions(targetType_ems,
									emsConnectionIds, linkId, linkName,
									linkType, start, limit, CommonDefine.TRUE,
									CommonDefine.FALSE));
				} else if (offsetEms < limit) {
					// 取部分ems获取链路数据，部分ne获取链路数据
					// ems: start = start,limit = linkCountByEms;
					rows.addAll(performanceManagerMapper
							.selectExternalLinkListByConditions(targetType_ems,
									emsConnectionIds, linkId, linkName,
									linkType, start, linkCountByEms,
									CommonDefine.TRUE, CommonDefine.FALSE));
					// ne: start = 0,limit = limit-offsetEms;
					rows.addAll(performanceManagerMapper
							.selectExternalLinkListByConditions(targetType_nes,
									neIds, linkId, linkName, linkType, 0, limit
											- offsetEms, CommonDefine.TRUE,
									CommonDefine.FALSE));
				}
			}
			// 取ne获取链路数据
			else if (offsetEms < 0 || offsetEms == 0) {
				// 取ne获取链路数据
				// ne: startNumber = start-linkCountByEms,limit = limit;
				rows.addAll(performanceManagerMapper
						.selectExternalLinkListByConditions(targetType_nes,
								neIds, linkId, linkName, linkType, start
										- linkCountByEms, limit,
								CommonDefine.TRUE, CommonDefine.FALSE));
			}
			total = linkCountByEms + linkCountByNe;
		}
		// 下偏差
		Double downOffset = Double.valueOf(opticalPathMonitorModel
				.getDownOffset().toString());
		// 上偏差
		Double upperOffset = Double.valueOf(opticalPathMonitorModel
				.getUpperOffset().toString());
		// 获取link附加信息
		rows = getLinkRelationInfo(rows, collectDate, downOffset, upperOffset,
				opticalPathMonitorModel.getUserCurrentPmLinkIds());

		Map result = new HashMap();
		result.put("rows", rows);
		result.put("total", total);
		return result;
	}

	// 获取link相关信息
	private List<Map> getLinkRelationInfo(List<Map> linkList,
			String collectDate, Double downOffset, Double upperOffset,
			List<Integer> userCurrentPmLinkIds) {

		// 发送光功率
		int PM_TPL = 0;
		// 接收光功率
		int PM_RPL = 1;

		List<Map> resultList = new ArrayList<Map>();

		// 端口Id
		Integer ptpId = null;
		// 子网Id
		Integer subnetId = null;
		// 区域Id
		Integer areaId = null;
		// ptp相关信息对象
		Map ptpInfo = null;

		for (Map link : linkList) {

			Map result = new HashMap();
			// 发光功率
			Double sendOP = null;
			// 收光功率
			Double recOP = null;
			// z端端口比较值
			Double zPmCompareValue = null;
			// a端端口比较值
			Double aPmCompareValue = null;
			// 光路衰耗
			Double attenuationValue = null;
			// 衰耗值基准值
			Double standardAttenuation = null;
			// 偏差值
			Double offsetAttenuation = null;
			// 链路衰耗
			Double att = 0d;
			// 偏差等级
			Integer offsetLevel = 0;

			// 补全link相关信息
			Integer linkId = Integer.valueOf(link.get("linkId").toString());
			result.put("linkId", linkId);
			result.put("linkName",
					link.get("linkName") != null ? link.get("linkName") : "");
			result.put("aEmsConnectionId", link.get("A_EMS_CONNECTION_ID"));
			result.put("zEmsConnectionId", link.get("Z_EMS_CONNECTION_ID"));

			// 当前性能
			if (userCurrentPmLinkIds != null
					&& userCurrentPmLinkIds.contains(linkId)) {

			} else {
				// 历史性能
				// 获取发光功率，收光功率，采集日期
				Map linkPm = performanceManagerMapper
						.selectLinkPmByLinkIdAndCollectDate(linkId, collectDate);
				if (linkPm != null) {
					link.put(
							"sendOP",
							linkPm.get("SEND_OP") != null ? linkPm
									.get("SEND_OP") : "");
					link.put("recOP",
							linkPm.get("REC_OP") != null ? linkPm.get("REC_OP")
									: "");
					link.put(
							"collectDate",
							linkPm.get("COLLECT_DATE") != null ? linkPm
									.get("COLLECT_DATE") : "");
				} else {
					link.put("sendOP", "");
					link.put("recOP", "");
					link.put("collectDate", "");
				}
			}
			// 采集日期
			result.put("collectDate",
					link.get("collectDate") != null ? link.get("collectDate")
							: "");
			BigDecimal bigDecimal = null;
			// 格式化发光功率
			if (link.get("sendOP") != null) {
				try {
					bigDecimal = new BigDecimal(Double.valueOf(link.get(
							"sendOP").toString()));
					bigDecimal = bigDecimal.setScale(2,
							BigDecimal.ROUND_HALF_UP);
					sendOP = bigDecimal.doubleValue();
				} catch (Exception e) {

				}
			}
			// 格式化收光功率
			if (link.get("recOP") != null) {
				try {
					bigDecimal = new BigDecimal(Double.valueOf(link
							.get("recOP").toString()));
					bigDecimal = bigDecimal.setScale(2,
							BigDecimal.ROUND_HALF_UP);
					recOP = bigDecimal.doubleValue();
				} catch (Exception e) {

				}
			}
			// 格式化链路衰耗
			if (link.get("att") != null) {
				try {
					bigDecimal = new BigDecimal(Double.valueOf(link.get("att")
							.toString()));
					bigDecimal = bigDecimal.setScale(2,
							BigDecimal.ROUND_HALF_UP);
					att = bigDecimal.doubleValue();
				} catch (Exception e) {

				}
			}
			// 计算衰耗
			if (sendOP != null && recOP != null) {
				// attenuationValue 光路衰耗 A端口PMP_TPL_MAX性能值-att-Z端口PMP_RPL_MAX性能值
				attenuationValue = sendOP - att - recOP;
			}
			// 获取Z端ptpId ********************************************
			ptpId = Integer.valueOf(link.get("Z_END_PTP").toString());
			// 取得Z端ptp相关信息,接收光功率
			ptpInfo = performanceManagerMapper.selectPtpRelatedInfoByPtpId(
					ptpId, PM_RPL);
			// 补全link相关信息
			result.put("zGroupName",
					ptpInfo.get("groupName") != null ? ptpInfo.get("groupName")
							: "");
			result.put("zEmsName",
					ptpInfo.get("emsName") != null ? ptpInfo.get("emsName")
							: "");
			result.put("zNe",
					ptpInfo.get("neName") != null ? ptpInfo.get("neName") : "");
			result.put(
					"zProductName",
					ptpInfo.get("productName") != null ? ptpInfo
							.get("productName") : "");
			result.put("zPtpId", ptpId);
			result.put("zPtp",
					ptpInfo.get("ptpName") != null ? ptpInfo.get("ptpName")
							: "");
			result.put("zPtpType",
					ptpInfo.get("ptpType") != null ? ptpInfo.get("ptpType")
							: "");
			result.put("zRate",
					ptpInfo.get("rate") != null ? ptpInfo.get("rate") : "");
			result.put(
					"zStationName",
					ptpInfo.get("stationName") != null ? ptpInfo
							.get("stationName") : "");
			// 获取子网完整路径信息,默认不需要填写
			if (ptpInfo.get("PARENT_SUBNET") != null) {
				subnetId = ptpInfo.get("BASE_SUBNET_ID") != null ? Integer
						.valueOf(ptpInfo.get("BASE_SUBNET_ID").toString())
						: null;
				result.put("zSubnetName", commonManagerService
						.getMulitLevelFullName(subnetId, "T_BASE_SUBNET",
								"BASE_SUBNET_ID", "PARENT_SUBNET",
								"DISPLAY_NAME"));
			}
			// 获取区域完整路径信息,默认不需要填写
			if (ptpInfo.get("AREA_PARENT_ID") != null) {
				areaId = ptpInfo.get("RESOURCE_AREA_ID") != null ? Integer
						.valueOf(ptpInfo.get("RESOURCE_AREA_ID").toString())
						: null;
				result.put("zAreaName", commonManagerService
						.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
								"RESOURCE_AREA_ID", "AREA_PARENT_ID",
								"AREA_NAME"));
			}
			// 格式化z端pmCompareValue
			if (ptpInfo.get("compareValue") != null) {
				try {
					bigDecimal = new BigDecimal(Double.valueOf(ptpInfo.get(
							"compareValue").toString()));
					bigDecimal = bigDecimal.setScale(2,
							BigDecimal.ROUND_HALF_UP);
					zPmCompareValue = bigDecimal.doubleValue();
				} catch (Exception e) {

				}
			}
			// 获取A端ptpId ********************************************
			ptpId = Integer.valueOf(link.get("A_END_PTP").toString());
			// 取得A端ptp相关信息 发送光功率信息
			ptpInfo = performanceManagerMapper.selectPtpRelatedInfoByPtpId(
					ptpId, PM_TPL);

			// 补全link相关信息
			result.put("aGroupName",
					ptpInfo.get("groupName") != null ? ptpInfo.get("groupName")
							: "");
			result.put("aEmsName",
					ptpInfo.get("emsName") != null ? ptpInfo.get("emsName")
							: "");
			result.put("aNe",
					ptpInfo.get("neName") != null ? ptpInfo.get("neName") : "");
			result.put(
					"aProductName",
					ptpInfo.get("productName") != null ? ptpInfo
							.get("productName") : "");

			result.put("aPtpId", ptpId);
			result.put("aPtp",
					ptpInfo.get("ptpName") != null ? ptpInfo.get("ptpName")
							: "");
			result.put("aPtpType",
					ptpInfo.get("ptpType") != null ? ptpInfo.get("ptpType")
							: "");
			result.put("aRate",
					ptpInfo.get("rate") != null ? ptpInfo.get("rate") : "");
			result.put(
					"aStationName",
					ptpInfo.get("stationName") != null ? ptpInfo
							.get("stationName") : "");
			// 获取子网完整路径信息,默认不需要填写
			if (ptpInfo.get("PARENT_SUBNET") != null) {
				subnetId = ptpInfo.get("BASE_SUBNET_ID") != null ? Integer
						.valueOf(ptpInfo.get("BASE_SUBNET_ID").toString())
						: null;
				result.put("aSubnetName", commonManagerService
						.getMulitLevelFullName(subnetId, "T_BASE_SUBNET",
								"BASE_SUBNET_ID", "PARENT_SUBNET",
								"DISPLAY_NAME"));
			}
			// 获取区域完整路径信息,默认不需要填写
			if (ptpInfo.get("AREA_PARENT_ID") != null) {
				areaId = ptpInfo.get("RESOURCE_AREA_ID") != null ? Integer
						.valueOf(ptpInfo.get("RESOURCE_AREA_ID").toString())
						: null;
				result.put("aAreaName", commonManagerService
						.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
								"RESOURCE_AREA_ID", "AREA_PARENT_ID",
								"AREA_NAME"));
			}
			// 格式化a端pmCompareValue
			if (ptpInfo.get("compareValue") != null) {
				try {
					bigDecimal = new BigDecimal(Double.valueOf(ptpInfo.get(
							"compareValue").toString()));
					bigDecimal = bigDecimal.setScale(2,
							BigDecimal.ROUND_HALF_UP);
					aPmCompareValue = bigDecimal.doubleValue();
				} catch (Exception e) {

				}
			}

			// standardAttenuation
			// A端口T_PM_COMPARE表的PM_VALUE-Z端口T_PM_COMPARE表的PM_VALUE-att
			if (aPmCompareValue != null && zPmCompareValue != null) {
				standardAttenuation = aPmCompareValue - zPmCompareValue - att;
				// offsetAttenuation 光路衰耗和衰耗基准值的差值，取绝对值
				if (attenuationValue != null) {
					offsetAttenuation = Math.abs(standardAttenuation
							- attenuationValue);
					// 偏差分级
					//
					if (offsetAttenuation < downOffset) {
						offsetLevel = 1;
					} else if (offsetAttenuation > upperOffset
							|| offsetAttenuation == upperOffset) {
						offsetLevel = 3;
					} else {
						offsetLevel = 2;
					}
				}
			}

			// 补全link相关信息
			DecimalFormat df = new DecimalFormat("0.00");
			// 发送光功率
			result.put("sendOP", sendOP != null ? df.format(sendOP) : "-");
			// 接收光功率
			result.put("recOP", recOP != null ? df.format(recOP) : "-");
			// 衰耗基准值
			result.put(
					"standardAttenuation",
					standardAttenuation != null ? df
							.format(standardAttenuation) : "-");
			// 光路衰耗（dB）
			result.put("attenuationValue",
					attenuationValue != null ? df.format(attenuationValue)
							: "-");
			// 偏差值（dB）
			result.put("offsetAttenuation",
					offsetAttenuation != null ? df.format(offsetAttenuation)
							: "-");
			// 偏差值等级
			result.put("offsetLevel", offsetLevel);
			// 链路衰耗值
			result.put("att", df.format(att));

			resultList.add(result);
		}
		return resultList;
	}

	/**
	 * 修改链路att属性
	 * 
	 * @param modifyList
	 * @throws CommonException
	 */
	public void modifyAttForLink(List<OpticalPathMonitorModel> modifyList) {

		Map link = null;

		for (OpticalPathMonitorModel model : modifyList) {
			link = new HashMap();

			link.put("BASE_LINK_ID", model.getLinkId());
			link.put("ATT", model.getAtt());
			performanceManagerMapper.updateLinkById(link);
		}
	}

	/**
	 * 刷新当前值
	 * 
	 * @param list
	 * @throws CommonException
	 */
	public void fulshCurrentPm(List<OpticalPathMonitorModel> list)
			throws CommonException {

		Map<Integer, List<Integer>> ptpIdListMap = new HashMap<Integer, List<Integer>>();

		SimpleDateFormat sf = CommonUtil
				.getDateFormatter(CommonDefine.COMMON_SIMPLE_FORMAT);

		String collectDate = sf.format(new Date());

		for (OpticalPathMonitorModel model : list) {

			Integer aEmsConnectionId = model.getaEmsConnectionId();
			Integer zEmsConnectionId = model.getzEmsConnectionId();
			// ptpId与emsId分类
			// a端端口处理
			if (ptpIdListMap.containsKey(aEmsConnectionId)) {

				if (ptpIdListMap.get(aEmsConnectionId).contains(
						model.getaPtpId())) {

				} else {
					ptpIdListMap.get(aEmsConnectionId).add(model.getaPtpId());
				}
			} else {
				List<Integer> ptpIdListTemp = new ArrayList<Integer>();
				ptpIdListTemp.add(model.getaPtpId());
				ptpIdListMap.put(aEmsConnectionId, ptpIdListTemp);
			}
			// z端端口处理
			if (ptpIdListMap.containsKey(zEmsConnectionId)) {

				if (ptpIdListMap.get(zEmsConnectionId).contains(
						model.getzPtpId())) {

				} else {
					ptpIdListMap.get(zEmsConnectionId).add(model.getzPtpId());
				}
			} else {
				List<Integer> ptpIdListTemp = new ArrayList<Integer>();
				ptpIdListTemp.add(model.getzPtpId());
				ptpIdListMap.put(zEmsConnectionId, ptpIdListTemp);
			}
		}
		// 获取实时性能数据
		List<PmDataModel> pmDataList = new ArrayList<PmDataModel>();
		Set<Integer> it = ptpIdListMap.keySet();
		for (Integer emsConnectionId : it) {
			dataCollectService = SpringContextUtil
					.getDataCollectServiceProxy(emsConnectionId);
			// 采集性能数据
			List<PmDataModel> pmData = dataCollectService
					.getCurrentPmData_PtpList(
							ptpIdListMap.get(emsConnectionId),
							new short[] {},
							new int[] {
									CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG,
									CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG },
							new int[] { CommonDefine.PM.GRANULARITY_15MIN_FLAG },
							false, true, false, CommonDefine.COLLECT_LEVEL_1);
			pmDataList.addAll(pmData);
		}
		// 筛选数据
		Map link = null;
		for (OpticalPathMonitorModel model : list) {
			Integer linkId = model.getLinkId();
			Integer aPtpId = model.getaPtpId();
			Integer zPtpId = model.getzPtpId();
			String sendOp = "";
			String recOp = "";
			for (PmDataModel pm : pmDataList) {
				// 获取发光功率当前值
				if (aPtpId.intValue() == pm.getPtpId() && sendOp.isEmpty()) {
					for (PmMeasurementModel pmMeasure : pm
							.getPmMeasurementList()) {
						if (CommonDefine.PM.STD_INDEX_TPL_CUR.equals(pmMeasure
								.getPmStdIndex())
								|| CommonDefine.PM.STD_INDEX_TPL_AVG
										.equals(pmMeasure.getPmStdIndex())) {
							sendOp = pmMeasure.getValue();
						}
					}
				}
				// 获取收光光功率当前值
				if (zPtpId.intValue() == pm.getPtpId() && recOp.isEmpty()) {
					for (PmMeasurementModel pmMeasure : pm
							.getPmMeasurementList()) {
						if (CommonDefine.PM.STD_INDEX_RPL_CUR.equals(pmMeasure
								.getPmStdIndex())
								|| CommonDefine.PM.STD_INDEX_RPL_AVG
										.equals(pmMeasure.getPmStdIndex())) {
							recOp = pmMeasure.getValue();
						}
					}
				}
			}
			// 更新发送光功率，接收光功率值
			link = new HashMap();
			link.put("BASE_LINK_ID", linkId);
			link.put("SEND_OP", sendOp);
			link.put("REC_OP", recOp);
			if (sendOp.isEmpty() && recOp.isEmpty()) {
				link.put("COLLECT_DATE", "");
			} else {
				link.put("COLLECT_DATE", collectDate);
			}
			performanceManagerMapper.updateLinkById(link);
		}

	}

	/**
	 * 获取偏差值信息
	 * 
	 * @throws CommonException
	 */
	public Map getOffsetValue() throws CommonException {

		Map offsetParam = null;

		offsetParam = commonManagerMapper.selectTableById(
				"T_BASE_OFFSET_PARAM", "BASE_OFFSET_PARAM_ID", 1);

		if (offsetParam == null) {
			offsetParam = new HashMap();
			offsetParam.put("BASE_OFFSET_PARAM_ID", 1);
			offsetParam.put("UPPER_OFFSET", 8);
			offsetParam.put("DOWN_OFFSET", 1);
			performanceManagerMapper.insertOffsetParam(offsetParam);
		}
		return offsetParam;
	}

	/**
	 * 修改偏差值信息
	 * 
	 * @throws CommonException
	 */
	public void modifyOffsetValue(String upperOffset, String downOffset)
			throws CommonException {

		Map offsetParam = new HashMap();
		offsetParam.put("BASE_OFFSET_PARAM_ID", 1);
		offsetParam.put("UPPER_OFFSET", upperOffset);
		offsetParam.put("DOWN_OFFSET", downOffset);

		performanceManagerMapper.updateOffsetParamById(offsetParam);
	}

	// ------------------------- MeiKai Start -------------------------

	/**
	 * 获取性能报表列表
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getReportInfoList(Map<String, Object> map,
			int userId) throws CommonException {

		Map<String, Object> return_map = new HashMap<String, Object>();

		List<Map> userGrps = performanceManagerMapper.getUserGroupByUserId(
				userId, REPORT_DEFINE);
		Map<String, Object> invalidUserId = new HashMap<String, Object>();
		invalidUserId.put("userGrpId", 0);
		userGrps.add(invalidUserId);// 加入无效的userId，防止数据库查询出错
		if (map.get("startDate") != null
				&& !map.get("startDate").toString().isEmpty()) {
			map.put("startDate", map.get("startDate").toString() + " 00:00:00");
		}
		if (map.get("endDate") != null
				&& !map.get("endDate").toString().isEmpty()) {
			map.put("endDate", map.get("endDate").toString() + " 23:59:59");
		}
		List<Map> result = performanceManagerMapper.getReportInfoList(map,
				userGrps);

		Map<String, Object> total = performanceManagerMapper
				.getReportInfoListCount(map, userGrps);

		return_map.put("total", total.get("total"));
		return_map.put("rows", result);

		return return_map;
	}

	/**
	 * 打包性能报表
	 * 
	 * @param filePathList
	 * @return
	 * @throws CommonException
	 */
	public CommonResult zipReport(List<String> filePathList,String fileName)
			throws CommonException {

		CommonResult result = new CommonResult();
		//默认值
		if(fileName == null){
			fileName = "-report";
		}
		List<String> filePathListExist = new ArrayList<String>();
		for(String path:filePathList){
			File f = new File(path);
			if(f.exists()){
				filePathListExist.add(path);
			}
		}
		String zipPath = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.TEMP_DIR + "\\";
		String zipFilePath = ZipUtil.getInstance().zipFiles(filePathListExist,
				fileName,zipPath);
		result.setReturnResult(CommonDefine.SUCCESS);
		result.setReturnMessage(zipFilePath);

		return result;
	}

	/**
	 * 判断用户是否有删除性能报表的权限
	 * 
	 * @param pmReportIdList
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public CommonResult preDeleteReport(List<Integer> pmReportIdList, int userId)
			throws CommonException {

		CommonResult result = new CommonResult();

		if (userId == -1) {// 超级用户
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("allPrivilege");
		} else {
			// 查出用户勾选的报表记录
			List<Map> reportList = performanceManagerMapper
					.getReportListByReportIds(pmReportIdList);
			int creator;
			boolean flagYes = false, flagNo = false;
			for (Map map : reportList) {
				creator = Integer.parseInt(map.get("CREATOR").toString());
				if (creator == userId) {
					flagYes = true;
				} else {
					flagNo = true;
				}
			}

			if (flagYes && flagNo) {
				result.setReturnMessage("partPrivilege");
			} else if (flagYes && !flagNo) {
				result.setReturnMessage("allPrivilege");
			} else if (!flagYes && flagNo) {
				result.setReturnMessage("noPrivilege");
			} else {
				result.setReturnMessage("Database has modified!");
			}

			result.setReturnResult(CommonDefine.SUCCESS);
		}

		return result;
	}

	/**
	 * 删除性能报表
	 * 
	 * @param filePathList
	 * @return
	 * @throws CommonException
	 */
	public CommonResult deleteReport(List<Integer> pmReportIdList, int userId)
			throws CommonException {

		CommonResult result = new CommonResult();

		// 判断用户是否有权限删除
		List<Map> reportList = performanceManagerMapper
				.getReportListByReportIds(pmReportIdList);

		int pmReportId, creator;
		for (Map pmReportRecord : reportList) {
			pmReportId = Integer.parseInt(pmReportRecord.get("PM_REPORT_ID")
					.toString());
			creator = Integer
					.parseInt(pmReportRecord.get("CREATOR").toString());
			if (userId == -1 || userId == creator) {
				// 删除相关文件
				if (pmReportRecord.get("EXCEL_URL") != null
						&& !pmReportRecord.get("EXCEL_URL").toString()
								.equals("")) {
					deleteFile(pmReportRecord.get("EXCEL_URL").toString());
				}
				if (pmReportRecord.get("NORMAL_CSV_PATH") != null
						&& !pmReportRecord.get("NORMAL_CSV_PATH").toString()
								.equals("")) {
					deleteFile(pmReportRecord.get("NORMAL_CSV_PATH").toString());
				}
				if (pmReportRecord.get("ABNORMAL_CSV_PATH") != null
						&& !pmReportRecord.get("ABNORMAL_CSV_PATH").toString()
								.equals("")) {
					deleteFile(pmReportRecord.get("ABNORMAL_CSV_PATH")
							.toString());
				}
				// 删除t_pm_report_analysis表中的记录
				performanceManagerMapper
						.deletePMReportAnalysisByPMReportId(pmReportId);
				// 删除t_pm_report_info表中的记录
				performanceManagerMapper.deletePMReportByPMReportId(pmReportId);
			}
		}
		result.setReturnResult(CommonDefine.SUCCESS);
		result.setReturnMessage("报表删除成功！");

		return result;
	}

	/**
	 * 删除指定路径的文件
	 * 
	 * @param filePath
	 */
	private void deleteFile(String filePath) {

		File temp = new File(filePath);
		temp.delete();
	}

	// ------------------------- MeiKai End -------------------------

	/**
	 * 为导出所用的ColumnMap加上日期字段
	 * 
	 * @param baseColumn
	 *            基础列
	 * @param dates
	 *            日期列的List集合
	 * @return
	 */
	private ColumnMap[] genColumn(ColumnMap[] baseColumn, List<String> dates) {
		int baseLen = baseColumn.length;
		ColumnMap[] rv = new ColumnMap[baseLen + dates.size()];
		for (int i = 0; i < baseLen; i++) {
			rv[i] = baseColumn[i];
		}
		for (int i = 0; i < dates.size(); i++) {
			rv[baseLen + i] = new ColumnMap(dates.get(i), dates.get(i),
					CommonDefine.PM.CUSTOM_REPORT.COMBO_VALUE, 12);
		}
		return rv;
	}

	@Override
	public List<Integer> myUserGroup(Integer userId) throws CommonException {
		List<Integer> result = new ArrayList<Integer>();
		try {
			result = performanceManagerMapper.myUserGroup(userId);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return result;
	}

	private List<Map<String, String>> procNodes(List<Map> taskNodes) {
		Map<String, String> conditionMap = nodeListClassify(taskNodes);
		Iterator it = conditionMap.keySet().iterator();
		List<Map<String, String>> nodeInfo = new ArrayList<Map<String, String>>();
		while (it.hasNext()) {
			String level = (String) it.next();
			if ("NODE_EMS".equals(level)) {
				List<Map<String, String>> emsNodeInfo = performanceManagerMapper
						.getEmsNodeInfo(conditionMap, RegularPmAnalysisDefine,
								TREE_DEFINE);
				nodeInfo.addAll(emsNodeInfo);
			} else if ("NODE_SUBNET".equals(level)) {
				List<Map<String, String>> subnetNodeInfo = performanceManagerMapper
						.getSubnetNodeInfo(conditionMap,
								RegularPmAnalysisDefine, TREE_DEFINE);
				nodeInfo.addAll(subnetNodeInfo);
			} else if ("NODE_NE".equals(level)) {
				List<Map<String, String>> neNodeInfo = performanceManagerMapper
						.getNeNodeInfo(conditionMap, RegularPmAnalysisDefine,
								TREE_DEFINE);
				nodeInfo.addAll(neNodeInfo);
			}
		}
		return nodeInfo;
	}

	public int getPMCollParamForFP(int flag, int userId) throws CommonException {
		int returnInt = 0;
		// 获取该用户有权限的网管和网管分组列表
		StringBuffer sb = new StringBuffer();
		List<Map> allowedEmses = commonManagerService.getAllEmsByEmsGroupId(
				userId, CommonDefine.VALUE_ALL, false, true);
		sb = new StringBuffer();
		for (Map map : allowedEmses) {
			sb.append(map.get("BASE_EMS_CONNECTION_ID"));
			sb.append(",");
		}
		String ems = sb.toString();
		if (ems.length() > 0) {
			ems = ems.substring(0, ems.length() - 1);
		}
		if ("".equals(ems)) {
			returnInt = 0;
		} else {
			switch (flag) {
			case CommonDefine.ALL_TASK_FIRST_PAGE:
				returnInt = performanceManagerMapper.getEmsCountForIndex(ems,
						RegularPmAnalysisDefine, null, null);
				break;
			case CommonDefine.START_TASK_FIRST_PAGE:
				returnInt = performanceManagerMapper.getEmsCountForIndex(ems,
						RegularPmAnalysisDefine, 1, null);
				break;
			case CommonDefine.SUCCESS_TASK_FIRST_PAGE:
				returnInt = performanceManagerMapper.getEmsCountForIndex(ems,
						RegularPmAnalysisDefine, null, "1 , 4, 6");
				break;
			default:
				break;
			}
		}
		return returnInt;
	}

	@Override
	public Map searchCurrentDataIntoTempTableCir(List<Map> ptpInfoList,
			List<Map> neInfoList, Integer sysUserId, Integer granularity,
			boolean needUnitPm, boolean needPtpPm, boolean needCtpPm,int searchType)
			throws CommonException {
		try{
			// TODO Auto-generated method stub]
			Map<String,List> ptpByNe = new HashMap<String, List>();
			Map<String,List> unitByNe = new HashMap<String, List>();
			if(ptpInfoList.size()>0){
				if(needPtpPm)
					ptpByNe = key1ByKey2(ptpInfoList,"BASE_PTP_ID","BASE_NE_ID",1);
				
				if(needUnitPm)
					unitByNe = key1ByKey2(ptpInfoList,"BASE_UNIT_ID","BASE_NE_ID",1);
			}
			// 查询唯一标记
			int searchTag = getSearchTag();
			if(searchType==CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH)
				searchTag+=10000;
			// 先清临时表
			performanceManagerMapper.deleteTempPm(
					CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA,
					sysUserId,searchType);
			//然后开采
			List<Map<String, Object>> pmMapList = new ArrayList<Map<String,Object>>();
			for(Map ne:neInfoList){
				// 采集性能START,一个网元一个网元的采
				List<PmDataModel> pmDataList = new ArrayList<PmDataModel>();
				Integer emsId = Integer.valueOf(ne.get("BASE_EMS_CONNECTION_ID").toString());
				Integer neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
				IDataCollectServiceProxy dataCollectService = SpringContextUtil
						.getDataCollectServiceProxy(emsId);
				pmDataList.addAll(dataCollectService.getCurrentPmData_Ne(neId,
						new short[] {}, new int[] {}, new int[] { granularity }, true,
						true, true, CommonDefine.COLLECT_LEVEL_1));
				//筛选需要的性能
				
				pmMapList.addAll(filterPmList((List<Integer>)ptpByNe.get(neId.toString()),(List<Integer>)unitByNe.get(neId.toString()),pmDataList,sysUserId,searchTag));
			}
			//存入临时表
			if (pmMapList.size() > 0) {
					performanceManagerMapper.insertCurrentTempPm(pmMapList,
							CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA);
			}
			Map returnMap = new HashMap<String,Object>();
			returnMap.put("searchTag", searchTag);
			return returnMap;
		}catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 0, "当前性能查询失败！");
		}
	}
	
	/**
	 * 筛选需要的性能
	 * @param ptpByNe  需要的端口
	 * @param ptpByUnit  需要的板卡
	 * @param pmDataList   原始数据
	 * @param exceptionLv  异常等级
	 * @return
	 */
	private List<Map<String, Object>> filterPmList(List<Integer> ptpList,
			List<Integer> unitList,List<PmDataModel> pmDataList,Integer currentUserId,int searchTag) {
		List<Map<String, Object>> pmMapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> aPm;
		List<PmMeasurementModel> pmMeasurementList;
		for (PmDataModel pmDataModel : pmDataList) {
			boolean isMatch = true;
			if(pmDataModel.getTargetType()>=7){//也包含了CTP级别的
				// ptp性能
				if(!(ptpList.size()>0&&ptpList.contains(pmDataModel.getPtpId()))){
					isMatch = false;
				}
			}else if(pmDataModel.getTargetType().equals(6)){
				//板卡性能
				if(!(unitList.size()>0&&unitList.contains(pmDataModel.getUnitId()))){
						isMatch = false;
				}
			}
			if(!isMatch)
				continue;
			pmMeasurementList = pmDataModel.getPmMeasurementList();
			for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
				aPm = new HashMap<String, Object>();
				aPm.put("BASE_EMS_CONNECTION_ID",
						pmDataModel.getEmsConnectionId());
				aPm.put("BASE_NE_ID", pmDataModel.getNeId());
				aPm.put("BASE_RACK_ID", pmDataModel.getRackId());
				aPm.put("BASE_SHELF_ID", pmDataModel.getShelfId());
				aPm.put("BASE_SLOT_ID", pmDataModel.getSlotId());
				aPm.put("BASE_SUB_SLOT_ID", pmDataModel.getSubSlotId());
				aPm.put("BASE_UNIT_ID", pmDataModel.getUnitId());
				aPm.put("BASE_SUB_UNIT_ID", pmDataModel.getSubUnitId());
				aPm.put("BASE_PTP_ID", pmDataModel.getPtpId());
				aPm.put("BASE_OTN_CTP_ID", pmDataModel.getOtnCtpId());
				aPm.put("BASE_SDH_CTP_ID", pmDataModel.getSdhCtpId());
				aPm.put("TARGET_TYPE", pmDataModel.getTargetType());
				aPm.put("LAYER_RATE", pmDataModel.getLayerRate());
				aPm.put("PM_STD_INDEX",
						pmMeasurementModel.getPmStdIndex());
				aPm.put("PM_INDEX",
						pmMeasurementModel.getPmParameterName());
				aPm.put("PM_VALUE", pmMeasurementModel.getValue());
				aPm.put("PM_COMPARE_VALUE",
						pmMeasurementModel.getPmCompareValue());
				aPm.put("PM_COMPARE_VALUE_DISPLAY",
						pmMeasurementModel.getDisplayCompareValue());
				aPm.put("TYPE", pmMeasurementModel.getType());
				aPm.put("THRESHOLD_1",
						pmMeasurementModel.getThreshold1());
				aPm.put("THRESHOLD_2",
						pmMeasurementModel.getThreshold2());
				aPm.put("THRESHOLD_3",
						pmMeasurementModel.getThreshold3());
				aPm.put("FILTER_VALUE",
						pmMeasurementModel.getFilterValue());
				aPm.put("OFFSET", pmMeasurementModel.getOffset());
				aPm.put("UPPER_VALUE",
						pmMeasurementModel.getUpperValue());
				aPm.put("UPPER_OFFSET",
						pmMeasurementModel.getUpperOffset());
				aPm.put("LOWER_VALUE",
						pmMeasurementModel.getLowerValue());
				aPm.put("LOWER_OFFSET",
						pmMeasurementModel.getLowerOffset());
				aPm.put("PM_DESCRIPTION",
						pmMeasurementModel.getPmdescription());
				aPm.put("LOCATION",
						pmMeasurementModel.getLocationFlag());
				aPm.put("UNIT", pmMeasurementModel.getUnit());
				aPm.put("GRANULARITY", pmDataModel.getGranularityFlag());
				aPm.put("EXCEPTION_LV",
						pmMeasurementModel.getExceptionLv());
				aPm.put("EXCEPTION_COUNT",
						pmMeasurementModel.getExceptionCount());
				aPm.put("RETRIEVAL_TIME", new Timestamp(pmDataModel
						.getRetrievalTimeDisplay().getTime()));
				aPm.put("DISPLAY_EMS_GROUP",
						pmDataModel.getDisplayEmsGroup());
				aPm.put("DISPLAY_EMS", pmDataModel.getDisplayEms());
				aPm.put("DISPLAY_SUBNET",
						pmDataModel.getDisplaySubnet());
				aPm.put("DISPLAY_NE", pmDataModel.getDisplayNe());
				aPm.put("DISPLAY_AREA", pmDataModel.getDisplayArea());
				aPm.put("DISPLAY_STATION",
						pmDataModel.getDisplayStation());
				aPm.put("DISPLAY_PRODUCT_NAME",
						pmDataModel.getDisplayProductName());
				aPm.put("DISPLAY_PORT_DESC",
						pmDataModel.getDisplayPortDesc());
				aPm.put("DISPLAY_CTP", pmDataModel.getDisplayCtp());
				aPm.put("DISPLAY_TEMPLATE_NAME",
						pmDataModel.getDisplayTemplateName());
				aPm.put("SYS_USER_ID", currentUserId);
				// ==================================
				aPm.put("RATE", pmDataModel.getRate());
				aPm.put("DOMAIN", pmDataModel.getDomain());
				aPm.put("TEMPLATE_ID", pmDataModel.getPmTemplateId());
				aPm.put("BASE_SUBNET_ID", pmDataModel.getSubnetId());
				aPm.put("PTP_TYPE", pmDataModel.getPtpType());
				aPm.put("SEARCH_TAG", searchTag);
				pmMapList.add(aPm);
			}
		}
		return pmMapList;
	}

	/**
	 * @param mapList 数据集
	 * @param key1 需要被分类的字段
	 * @param key2 分类依据的字段
	 * @param c  1:Integer,2:Long,3:String
	 * @return
	 */
	public Map<String, List> key1ByKey2(List<Map> mapList,String key1,String key2,int c) {
		Map<String,List> result = new HashMap<String, List>();
		for(Map map : mapList){
			if(!result.containsKey(map.get(key2).toString())){
				result.put(map.get(key2).toString(), new ArrayList());
			}
			if(c==1)
				result.get(map.get(key2).toString()).add(Integer.valueOf(map.get(key1).toString()));
			else if(c==2)
				result.get(map.get(key2).toString()).add(Long.valueOf(map.get(key1).toString()));
			else if(c==3)
				result.get(map.get(key2).toString()).add(map.get(key1).toString());
			else
				result.get(map.get(key2).toString()).add(map.get(key1));
		}
		return result;
	}
	// 历史性能
		@SuppressWarnings({ "rawtypes" })
		@Override
		public int getHistoryPmDataCir(List<Map> ptpInfoList,
				List<Map> neInfoList, String startTime, String endTime,
				int currentUserId,int searchType,boolean needNeLevelPm,boolean needUnitLevelPm,boolean needPtpLevelPm)
				throws CommonException {
			
			// 获取searchTag
			int searchTag = getSearchTag();
			if(searchType==CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH)
				searchTag+=10000;
			
			try {
				List<Map> nodeList = new ArrayList<Map>();
				if(ptpInfoList.size()>0){
					for(Map info:ptpInfoList){
						if(info.get("BASE_PTP_ID")!=null){
							Map node = new HashMap();
							node.put("nodeId", info.get("BASE_PTP_ID"));
							node.put("nodeLevel", CommonDefine.TREE.NODE.PTP);
							node.put("emsId", info.get("BASE_EMS_CONNECTION_ID"));
							nodeList.add(node);
						}
						if(info.get("BASE_UNIT_ID")!=null){
							Map node = new HashMap();
							node.put("nodeId", info.get("BASE_UNIT_ID"));
							node.put("nodeLevel", CommonDefine.TREE.NODE.UNIT);
							node.put("emsId", info.get("BASE_EMS_CONNECTION_ID"));
							nodeList.add(node);
						}
					}
				}
				for(Map ne : neInfoList){
					Map node = new HashMap();
					node.put("nodeId", ne.get("BASE_NE_ID"));
					node.put("nodeLevel", CommonDefine.TREE.NODE.NE);
					node.put("emsId", ne.get("BASE_EMS_CONNECTION_ID"));
					nodeList.add(node);
				}
				
				//清表
				performanceManagerMapper.deleteTempPm(
						CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA,
						currentUserId,searchType);
				
				
				// 处理时间获取表名
				SimpleDateFormat formatToMonth = new SimpleDateFormat("yyyy-MM");
				Date startDate = formatToMonth.parse(startTime.substring(0, 7));
				Date endDate = formatToMonth.parse(endTime.substring(0, 7));
				// 获取年月
				SimpleDateFormat formatForTableName = new SimpleDateFormat(
						"yyyy_MM");
				List<String> yearAndMonthes = new ArrayList<String>();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				while (calendar.getTimeInMillis() <= endDate.getTime()) {
					String yearAndMonth = formatForTableName.format(calendar
							.getTime());
					yearAndMonthes.add(yearAndMonth);
					calendar.add(Calendar.MONTH, 1);
				}

				
				// ========节点分网管处理========
				// 节点条件分层级筛选至各个EMS
				Map<Integer, Map<String, Object>> conditionMaps = getConditionsFromNodesGroupByEmsIds(nodeList);

				// 分表查询并插入临时表
				Map<String, Object> conditionMap;
				for (Iterator<Integer> it = conditionMaps.keySet().iterator(); it
						.hasNext();) {
					Integer key = it.next();
					conditionMap = conditionMaps.get(key);
					// conditionMap.put("emsId", key);
					conditionMap.put("startTime", startTime);
					conditionMap.put("endTime", endTime);
					if(needNeLevelPm)
						conditionMap.put("needNeLevelPm", 1);
					if(needUnitLevelPm)
						conditionMap.put("needUnitLevelPm", 1);
					if(needPtpLevelPm)
						conditionMap.put("needPtpLevelPm", 1);
						for (String s : yearAndMonthes) {
							String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
									+ "_" + key.toString() + "_" + s;
							Integer existance = performanceManagerMapper
									.getPmTableExistance(tableName,
											SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
							if (existance != null && existance == 1) {
									performanceManagerMapper
											.insertHistoryTempPmCircuit(
													currentUserId,
													tableName,
													CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA,
													searchTag, conditionMap);
							}
							// else {
							// // 表不存在
							// throw new CommonException(new Exception(),
							// MessageCodeDefine.PM_TABLE_NOT_EXIST);
							// }
						}
				}
			} catch (ParseException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (Exception e) {
				e.printStackTrace();
				throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
			}
			return searchTag;
		}
		
		public String getPmStdIndexType(Map<String, String> searchCond) throws CommonException{
			try{
				String pmStdIndex = searchCond.get("pmStdIndex");
				Map r = performanceManagerMapper.getPmStdIndexType(pmStdIndex);
				String pmStdIndexType = r.get("PM_STD_INDEX_TYPE").toString();
				return pmStdIndexType;
			} catch (Exception e) {
				e.printStackTrace();
				throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
			}
			
		} 
}