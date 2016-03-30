package com.fujitsu.manager.imptProtectManager.serviceImpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.ImptProtectManagerMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.imptProtectManager.service.ImptProtectManagerService;
import com.fujitsu.model.LinkAlarmModel;
import com.fujitsu.model.TopoLineModel;
import com.fujitsu.model.TopoNodeModel;
import com.fujitsu.util.SpringContextUtil;
import com.mongodb.DBObject;

@Service
@Transactional(rollbackFor=Exception.class)
public class ImptProtectManagerServiceImpl extends ImptProtectManagerService {
	
	@Resource
	private ImptProtectManagerMapper imptProtectManagerMapper;
	@Resource
	public ICommonManagerService commonManagerService;
	@Resource
	public ICircuitManagerService circuitManagerService;
	@Resource
	public IQuartzManagerService quartzManagerService;
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;
	@Resource
	public IPerformanceManagerService performanceManagerService;
	@Resource
	public IAlarmManagementService alarmManagementService;
	
	@Override
	public boolean checkTaskNameExist(Map<String,Object> param) throws CommonException {
		try{
			int count = imptProtectManagerMapper.checkTaskName(param);
			return  count>0;
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}
	
	@Override
	public Integer editTask(Map<String, Object> param) throws CommonException {
		
		List<Map<String,Object>> taskParam=new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> equipList=new ArrayList<Map<String,Object>>();
		Map<String,Object> paramMap;
		Integer oldId=null;
		if(param.get("SYS_TASK_ID")!=null){
			oldId=Integer.valueOf(""+param.get("SYS_TASK_ID"));
		}
		/*if(param.get("SYS_TASK_ID")==null&&param.get("TASK_STATUS")==null){
			//默认状态:等待
			param.put("TASK_STATUS", CommonDefine.IMPT_PROTECT.TASK_STATUS.WAITTING);
		}*/
		if(param.get("CATEGORY")!=null){
			paramMap=new HashMap<String, Object>();
			paramMap.put("PARAM_NAME", "CATEGORY");
			paramMap.put("PARAM_VALUE", param.get("CATEGORY").toString());
			taskParam.add(paramMap);
		}
		if(param.get("TARGET_TYPE")!=null){
			paramMap=new HashMap<String, Object>();
			paramMap.put("PARAM_NAME", "TARGET_TYPE");
			paramMap.put("PARAM_VALUE", param.get("TARGET_TYPE").toString());
			taskParam.add(paramMap);
		}
		if(param.get("privilegeList")!=null){
			paramMap=new HashMap<String, Object>();
			paramMap.put("PARAM_NAME", "privilegeList");
			paramMap.put("PARAM_VALUE", JSONArray.fromObject(param.get("privilegeList")).toString());
			taskParam.add(paramMap);
		}
		if(param.get("equipList")!=null&&(param.get("equipList") instanceof List)){
			equipList=(List)param.get("equipList");
		}
		Integer taskId=editTask(param, equipList, taskParam);
		if(!taskId.equals(oldId)){
			param.put("TASK_STATUS", CommonDefine.QUARTZ.JOB_RESUME);
			changeTaskStatus(param);
		}
		
		return taskId;
	}
	private Integer editTask(Map<String, Object> task,List<Map<String, Object>> taskInfo,List<Map<String, Object>> taskParam) throws CommonException {
		try{
			//保存任务基本信息
			if(task.get("SYS_TASK_ID")==null){
				task.put("CREATE_TIME", new Date());
			}
			task.put("UPDATE_TIME", new Date());
			imptProtectManagerMapper.saveTask(task);
			if(task.get("SYS_TASK_ID")==null){
				throw new CommonException(new Exception(), MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
			}
			Integer taskId=Integer.valueOf(task.get("SYS_TASK_ID").toString());
			imptProtectManagerMapper.delTaskInfo(taskId);
			imptProtectManagerMapper.delTaskParam(taskId);
			if(taskInfo!=null&&!taskInfo.isEmpty())
			imptProtectManagerMapper.saveTaskInfo(taskId,taskInfo);
			if(taskParam!=null&&!taskParam.isEmpty())
			imptProtectManagerMapper.saveTaskParam(taskId,taskParam);
			return taskId;
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}
	
	@Override
	public void changeTaskStatus(Map<String,Object> param) throws CommonException {
		try{
			int taskId=Integer.valueOf(""+param.get("SYS_TASK_ID"));
			int taskStatus=Integer.valueOf(""+param.get("TASK_STATUS"));
			int taskType=CommonDefine.QUARTZ.JOB_IMPT_PROTECT;
			Class jobClass=com.fujitsu.job.ImptProtectJob.class;
			String cronExpression="";
			Map<String, Object> jobParam = new HashMap<String, Object>();
		    jobParam.put("taskId", taskId);
		    Date startTime=null;
		    Map<String, Object> taskMap=null;
			if(param.containsKey("START_TIME")){
				startTime=(Date)param.get("START_TIME");
			}else{
				Map<String, Object> queryMap=new HashMap<String, Object>();
				queryMap.put("SYS_TASK_ID", taskId);
				taskMap=getTask(queryMap, CommonDefine.USER_ADMIN_ID);
				startTime=(Date)taskMap.get("START_TIME");
			}
			int nextStatus=CommonDefine.IMPT_PROTECT.TASK_STATUS.WAITTING;
			
			if(startTime==null||startTime.getTime()<=new Date().getTime()){
				startTime=new Date(System.currentTimeMillis()+1000);
				nextStatus=CommonDefine.IMPT_PROTECT.TASK_STATUS.RUNNING;
			}
			SimpleDateFormat cronFormat=new SimpleDateFormat("s m H d M ? yyyy");
			cronExpression=cronFormat.format(startTime);
			//System.out.println(cronExpression);
			if(!quartzManagerService.IsJobExist(taskType, taskId)){
				quartzManagerService.addJob(taskType,taskId,jobClass,cronExpression,jobParam);
			}
			switch(taskStatus){
			case CommonDefine.QUARTZ.JOB_DELETE:
				delTask(taskId);
				break;
			case CommonDefine.QUARTZ.JOB_RESUME:
				quartzManagerService.modifyJobTime(taskType, taskId, cronExpression);
				param.put("TASK_STATUS", nextStatus);
				imptProtectManagerMapper.changeTaskStatus(param);
				break;
			case CommonDefine.QUARTZ.JOB_PAUSE:
				if(taskMap==null){
					Map<String, Object> queryMap=new HashMap<String, Object>();
					queryMap.put("SYS_TASK_ID", taskId);
					taskMap=getTask(queryMap, CommonDefine.USER_ADMIN_ID);
				}
				if(taskMap==null||taskMap.get("TASK_STATUS")==null||
					((Integer.valueOf(taskMap.get("TASK_STATUS")+"")&
						(CommonDefine.IMPT_PROTECT.TASK_STATUS.COMPLETED|
						 CommonDefine.IMPT_PROTECT.TASK_STATUS.STOPED))==0)){
					param.put("TASK_STATUS", CommonDefine.IMPT_PROTECT.TASK_STATUS.STOPED);
					imptProtectManagerMapper.changeTaskStatus(param);
				}
				break;
			}
			quartzManagerService.ctrlJob(taskType, taskId, taskStatus);
			/*if(taskStatus==CommonDefine.QUARTZ.JOB_RESUME&&
				startTime.getTime()<=new Date().getTime()){
				taskStatus=CommonDefine.QUARTZ.JOB_ACTIVATE;
			}*/
		}catch(CommonException e){
			throw e;
		}catch(Exception e){
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}
	
	private void delTask(Integer taskId) throws CommonException {
		try{
			imptProtectManagerMapper.delTaskRunDetail(taskId);
			imptProtectManagerMapper.delTaskInfo(taskId);
			imptProtectManagerMapper.delTaskParam(taskId);
			imptProtectManagerMapper.delTask(taskId);
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}

	@Override
	public Map<String,Object> getTaskList(Map<String, Object> param, int userId, int start,
			int limit) throws CommonException {
		try{
			int total=imptProtectManagerMapper.cntTaskList(param, userId);
			List<Map<String,Object>> rows=imptProtectManagerMapper.getTaskList(param, userId, start, limit);
			Map<String,Object> resultMap=new HashMap<String, Object>();
			resultMap.put("total", total);
			resultMap.put("rows", rows);
			return resultMap;
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}

	@Override
	public Map<String,Object> getTask(Map<String, Object> param, int userId) throws CommonException {
		try{
			Map<String,Object> task = imptProtectManagerMapper.getTask(param,userId);
			return task;
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}

	@Override
	public Map<String, Object> getTaskInfo(Integer taskId, boolean hasName)
			throws CommonException {
		try{
			List<Map<String,Object>> rows=imptProtectManagerMapper.getTaskInfo(taskId);
			if(hasName){
				for(Map<String,Object> row:rows){
					//组装设备名，用于前台显示
					Map<String, Object> node = new HashMap<String, Object>();
					//节点列表信息
					List nodeList = new ArrayList();
					if(row.get("TARGET_TYPE")==null||row.get("TARGET_ID")==null){
						continue;
					}
					int targetType = Integer.valueOf(String.valueOf(row.get("TARGET_TYPE"))).intValue();
					int targetId = Integer.valueOf(String.valueOf(row.get("TARGET_ID"))).intValue();
					
					switch (targetType) {
					case CommonDefine.TASK_TARGET_TYPE.SDH_CIRCUIT:
					case CommonDefine.TASK_TARGET_TYPE.ETH_CIRCUIT:
					case CommonDefine.TASK_TARGET_TYPE.WDM_CIRCUIT:
						Map<String,Object> paramMap = new HashMap<String,Object>();
						paramMap.put("flag",2);
						//paramMap.put("clientName",clientName);
						//paramMap.put("circuitName",circuitName);
						//paramMap.put("useFor",useFor);
						paramMap.put("serviceType",CommonDefine.targetType2svcType(targetType));
						paramMap.put("circuitId",targetId);
						//paramMap.put("systemSourceNo",systemSourceNo);
						//paramMap.put("sourceNo",sourceNo);
						//paramMap.put("connectRate",connectRate);
						//paramMap.put("circuitState",circuitState);
						//paramMap.put("linkId",linkId);
						//paramMap.put("nodes",nodes);
						//paramMap.put("nodeLevel",nodeLevel);
						//paramMap.put("aLocationId",aLocationId);
						//paramMap.put("aLocationLevel",aLocationLevel);
						//paramMap.put("advancedCon",advancedCon);
						//paramMap.put("displayName",displayName);
						paramMap.put("start",0);
						paramMap.put("limit",1);
						node = circuitManagerService.selectCircuitAbout(paramMap);
						nodeList = (ArrayList)node.get("rows");
						if(nodeList!=null&&!nodeList.isEmpty()){
							row.putAll((Map)nodeList.get(0));
						}
						break;
					default:
						if(targetType>CommonDefine.TREE.NODE.LEAFMAX||
							targetType<CommonDefine.TREE.NODE.ROOT){
							continue;
						}
						Map nodeInfo = new HashMap();
						node = commonManagerService.treeGetNodesByKey(null,targetId,targetType,0,0,true,0,0,CommonDefine.USER_ADMIN_ID);
						nodeList = (ArrayList)node.get("rows");
						String displayName = "";
						for(int j = nodeList.size()-2; j >= 0; j--){
							nodeInfo = (Map)nodeList.get(j);
							displayName = displayName+ CommonDefine.NameSeparator + String.valueOf(nodeInfo.get("text"));	
						}
						displayName=displayName.replaceFirst(""+CommonDefine.NameSeparator, "");
						row.put("DISPLAY_NAME",displayName);
						break;
					}
				}
			}
			Map<String,Object> resultMap=new HashMap<String, Object>();
			resultMap.put("total", rows.size());
			resultMap.put("rows", rows);
			return resultMap;
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}

	
	/*-------------------------------------监测--------------------------------------------*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map> getTaskTargetNe(Integer taskId) throws CommonException {
		try{
		List<Map<String,Object>> rows=imptProtectManagerMapper.getTaskInfo(taskId);
		List<Map> nodeList = new ArrayList<Map>();
		for(Map<String,Object> row:rows){
			Map node = new HashMap();
			if(row.get("TARGET_TYPE")==null||row.get("TARGET_ID")==null){
				continue;
			}
			node.put("nodeLevel", row.get("TARGET_TYPE"));
			node.put("nodeId", row.get("TARGET_ID"));
			nodeList.add(node);
		}
		
		List<Integer> neIdList =  performanceManagerService.getNeIdsFromNodes(nodeList);
		List<Map> result = imptProtectManagerMapper.processNe(neIdList,TREE_DEFINE);
		return result;
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}
	
	@Override
	public Map searchCurrentDataIntoTempTableByPtp(List<Integer> ptpList,
			Map<String, String> paramMap, Integer sysUserId) throws CommonException{
		try{
			// 先获取网管ID
			List<Integer> emsIdList = imptProtectManagerMapper.getEmsIdFromPtps(ptpList);
			// 用来从临时表查询的
			int searchTag = getSearchTag();
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap.put("searchTag", searchTag);
			// 要把之前的查询结果先清掉
			performanceManagerMapper.deleteTempPm(
					CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA,
					sysUserId,CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH);
			// 这是最后用来保存的List
			List<Map<String, Object>> pmMapList = new ArrayList<Map<String, Object>>();
			//开始每个网管采了
			for(Integer emsId : emsIdList){
				IDataCollectServiceProxy dataCollectService = SpringContextUtil
						.getDataCollectServiceProxy(emsId);
				int granularity = Integer.parseInt(paramMap.get("granularity"));
				//采吧采吧
//				List<PmDataModel> pmDataList = dataCollectService.getCurrentPmData_PtpList(
//						ptpList, new short[] {}, new int[] {},
//						new int[] { granularity }, true, true, false,
//						CommonDefine.COLLECT_LEVEL_1);
				List<PmDataModel> pmDataList = dataCollectService.getCurrentPmData_PtpList(
						ptpList, new short[] {}, new int[] {
								CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG,
								CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG },
						new int[] { CommonDefine.PM.GRANULARITY_15MIN_FLAG },
						false, true, false, CommonDefine.COLLECT_LEVEL_1);
				// 处理保存数据
				for (PmDataModel pmDataModel: pmDataList){
					List<PmMeasurementModel> pmMeasurementList = pmDataModel
							.getPmMeasurementList();
					for (PmMeasurementModel pmMeasurementModel : pmMeasurementList){
						Map<String, Object> aPm = new HashMap<String, Object>();
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
						aPm.put("SYS_USER_ID", sysUserId);
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
			if (pmMapList.size() > 0) {
					performanceManagerMapper.insertCurrentTempPm(pmMapList,
							CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA);
			}
			return returnMap;
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public List<Map> plusEmsIdToNodeList(List<Map> nodeList)
			throws CommonException {
		List<Map> result = new ArrayList<Map>();
		try {
			List<Integer> idList = new ArrayList<Integer>();
			for (Map m : nodeList) {
				idList.add(Integer.parseInt(m.get("nodeId").toString()));
			}
			int nodeLevel = Integer.parseInt(nodeList.get(0).get("nodeLevel").toString());
			if ( nodeLevel == CommonDefine.TREE.NODE.NE) {
				result = imptProtectManagerMapper.processNe(idList,TREE_DEFINE);
			} else if (nodeLevel == CommonDefine.TREE.NODE.PTP) {
				result = imptProtectManagerMapper.processPtp(idList,TREE_DEFINE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return result;
	}
	
	@Override
	public Map<String, Object> getTopoDataEquip(List<Map> neList,
			List<Map> linkList) throws CommonException {
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			//提取网元ID
			List<Integer> neIdList = new ArrayList<Integer>();
			for(int i=0;i<neList.size();i++){
				if(neList.get(i).get("nodeId")!=null)
					neIdList.add(Integer.valueOf(neList.get(i).get("nodeId").toString()));
	      	}
			//查出告警
			Map<String, Object> alarmData = alarmManagementService.getCurrentAlarmByNeIdListForCutover(neIdList);
			List<DBObject> alarmList = (List<DBObject>)alarmData.get("rows");
			//两个部分的数据
			List<Object> rows = new ArrayList<Object>();
			List<TopoLineModel> line = new ArrayList<TopoLineModel>();
			List<TopoNodeModel> node = new ArrayList<TopoNodeModel>();
			//生成link部分的数据
			processLinkDataToTopo(linkList, line, alarmList);
			
			//下面开始处理网元了
			processNeDataToTopo(neList, node, alarmList);
			rows.addAll(node);
			rows.addAll(circuitManagerService.deleteRepeatLink(line));
			//把处理好的数据加进主干部分
			result.put("total", rows.size());
			result.put("rows", rows);
			result.put("layout", "round");
			result.put("title", "网络拓扑");
			result.put("currentTopoType", "EMS");
			result.put("privilege" ,"all");
			result.put("isFirstTopo" ,"yes");
			result.put("parentType" ,"-1");
			result.put("parentId" ,"-1");
			result.putAll(getAlarmColorSet());
		}catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, -1);
		}
		return result;
		 
	}
	/**
	 * 获取系统使用的告警颜色
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getAlarmColorSet() throws CommonException {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Map<String, Object> colorMap = alarmManagementService.getAlarmColorSet();
		result.put("colorCR", colorMap.get("PS_CRITICAL_IMAGE"));
		result.put("colorMJ", colorMap.get("PS_MAJOR_IMAGE"));
		result.put("colorMN", colorMap.get("PS_MINOR_IMAGE"));
		result.put("colorWR", colorMap.get("PS_WARNING_IMAGE"));
		result.put("colorCL", colorMap.get("PS_CLEARED_IMAGE"));
		
		return result;
	}
	/**
	 * 处理link部分的数据成为拓扑图格式
	 * @param linkList
	 * @param rows
	 * @param alarmList
	 */
	private void processLinkDataToTopo(List<Map> linkList,List rows,List<DBObject> alarmList){
		for(Map link : linkList){
			// 标明LINK的网元部分
			TopoLineModel line = new TopoLineModel();
			line.setFromNode(link.get("aNeId").toString());
			line.setToNode(link.get("zNeId").toString());
			line.setFromNodeType("3");
			line.setToNodeType("3");
			line.setNodeOrLine("line");
			line.setLineType("neLine");
			// 设置LINK的端口和告警信息
				//本来两个网元中应该有多LINK所以用一个LIST来装，这里就不讲究了，一个LINK写一次网元信息吧
			List<LinkAlarmModel> linkAlarms = new ArrayList<LinkAlarmModel>();
			// 一个LINK的信息
			LinkAlarmModel linkAlarm = new LinkAlarmModel();
			linkAlarm.setaEndPTP(link.get("aPtpId").toString());
			linkAlarm.setzEndPTP(link.get("zPtpId").toString());
			linkAlarm.setaNeId(link.get("aNeId").toString());
			linkAlarm.setzNeId(link.get("zNeId").toString());
			for(DBObject a:alarmList){
				Map alarm = a.toMap(); 
				if(alarm.get("PTP_ID").toString().equals(link.get("aPtpId").toString())){
					if(alarm.get("PERCEIVED_SEVERITY").toString().equals("1")){
						linkAlarm.setaCRCount(linkAlarm.getaCRCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("2")){
						linkAlarm.setaMJCount(linkAlarm.getaMJCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("3")){
						linkAlarm.setaMNCount(linkAlarm.getaMNCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("4")){
						linkAlarm.setaWRCount(linkAlarm.getaWRCount()+1);
					}
				}else if(alarm.get("PTP_ID").toString().equals(link.get("zPtpId").toString())){
					if(alarm.get("PERCEIVED_SEVERITY").toString().equals("1")){
						linkAlarm.setzCRCount(linkAlarm.getzCRCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("2")){
						linkAlarm.setzMJCount(linkAlarm.getzMJCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("3")){
						linkAlarm.setzMNCount(linkAlarm.getzMNCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("4")){
						linkAlarm.setzWRCount(linkAlarm.getzWRCount()+1);
					}
				}
			}
			linkAlarms.add(linkAlarm);
			line.setLinkAlarm(linkAlarms);
			rows.add(line);
		} 
	}
	/**
	 * 处理网元部分的数据成为拓扑图格式
	 * @param linkList
	 * @param rows
	 * @param alarmList
	 */
	private void processNeDataToTopo(List<Map> neList,List rows,List<DBObject> alarmList){
		for(Map ne : neList){
			TopoNodeModel neNode = new TopoNodeModel();
			neNode.setDisplayName(ne.get("displayName").toString());
			neNode.setNodeId(ne.get("nodeId").toString());
			neNode.setNeType("3");
			neNode.setNodeType("3");
			neNode.setNodeOrLine("node");
			for(DBObject a:alarmList){
				Map alarm = a.toMap(); 
				if(alarm.get("NE_ID").toString().equals(ne.get("nodeId").toString())){
					if(alarm.get("PERCEIVED_SEVERITY").toString().equals("1")){
						neNode.setCrCount(neNode.getCrCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("2")){
						neNode.setMjCount(neNode.getMjCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("3")){
						neNode.setMnCount(neNode.getMnCount()+1);
					}else if(alarm.get("PERCEIVED_SEVERITY").toString().equals("4")){
						neNode.setWrCount(neNode.getWrCount()+1);
					}
				}
			}
			rows.add(neNode);
		}
	}
	
	@Override
	public List<Integer> getUnitListByPtpList(List<Integer> ptpList)
			throws CommonException {
		try {
			List<Integer> unitList = new ArrayList<Integer>();
			if(ptpList!=null&&ptpList.size()>0)
				unitList = imptProtectManagerMapper.getUnitListByPtpList(ptpList);
			return unitList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, -1);
		}
	}
	@Override
	public List<Map> getPtpInfo(List<Integer> ptpList) throws CommonException {
		try {
			List<Map> PtpInfoList = new ArrayList<Map>();
			if(ptpList!=null&&ptpList.size()>0)
				PtpInfoList = imptProtectManagerMapper.getPtpInfo(ptpList);
			return PtpInfoList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, -1);
		}
	}
	/*_____________________________________监测____________________________________________*/
	@SuppressWarnings("rawtypes")
	@Override
	public List<Map<String, Object>> getPmExceedData(
			Map<String, Object> paramMap, int start, int limit)
			throws CommonException {
//		String pmStdIndexIdString = "";
//		List pm = (List) paramMap.get("pmStdIndexs");
//		pmStdIndexIdString = pm.toString();
//		pmStdIndexIdString = pmStdIndexIdString.substring(1, pmStdIndexIdString.length() - 1);
//		boolean maxMin = (Boolean) paramMap.get("maxMinFlag");
		return imptProtectManagerMapper.getPmExceedData(paramMap, start, limit);
	}

	@Override
	public Map getAPAPosition(Map<String, Object> param)
			throws CommonException {
		List<Map>data = imptProtectManagerMapper.getAPAPosition(param);
		if(data!=null && data.size()>0){
			return data.get(0);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void saveAPAPosition(HashMap<String, Object> param)
			throws CommonException {
		List<Map>data = imptProtectManagerMapper.getAPAPosition(param);
		if(data!=null && data.size()>0){
			imptProtectManagerMapper.updateAPAPosition(param);
		}else{
			imptProtectManagerMapper.saveAPAPosition(param);
		}
	}

}
