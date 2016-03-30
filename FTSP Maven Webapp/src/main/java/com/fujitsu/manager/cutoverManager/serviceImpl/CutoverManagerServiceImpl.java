package com.fujitsu.manager.cutoverManager.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.struts2.ServletActionContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.FieldNameDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.AlarmManagementMapper;
import com.fujitsu.dao.mysql.CutoverManagerMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.handler.PmMessageHandler;
import com.fujitsu.job.CutoverCompleteTaskJob;
import com.fujitsu.job.CutoverSnapshotBeforeJob;
import com.fujitsu.job.CutoverfilterAlarmJob;
import com.fujitsu.manager.cutoverManager.service.CutoverManagerService;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.model.AlarmFilterModel;
import com.fujitsu.model.FilterAlarmParametersModel;
import com.fujitsu.util.SpringContextUtil;
import com.fujitsu.util.ExportExcelUtil;

@Scope("prototype")
@Service
@SuppressWarnings( { "unchecked", "rawtypes" })
//@Transactional(rollbackFor = Exception.class)
public class CutoverManagerServiceImpl extends CutoverManagerService {
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;
	@Resource
	private CutoverManagerMapper cutoverManagerMapper;
	@Resource
	private ICommonManagerService commonManagerService;
	@Resource
	private IPerformanceManagerService performanceManagerService;
	@Resource
	private IQuartzManagerService quartzManagerService;
	@Resource
	private IAlarmManagementService alarmManagementService;
	@Resource
	private ICircuitManagerService circuitManagerService;
	@Resource
	private AlarmManagementMapper alarmManagementMapper;
	
	/**
	 * 分页获取割接任务列表
	 * 
	 * @param startTime
	 *            割接任务开始时间
	 * @param endTime
	 *            割接任务结束时间
	 * @param status
	 *            割接任务状态
	 * @param cutoverTaskName
	 *            割接任务名称
	 * @param startNumber
	 *            开始页码
	 * @param pageSize
	 *            页面大小
	 * @return "SYS_TASK_ID", "TASK_NAME", "START_TIME_ESTIMATE",
	 *         "END_TIME_ESTIMATE", "STATUS", "CREATE_PERSON", "CREATE_TIME",
	 *         "DISCRIPTION", "START_TIME_ACTUAL","END_TIME_ACTUAL"
	 * @throws CommonException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public Map getCutoverTask(String startTime, String endTime, String status,
			String cutoverTaskName, String currentUserId,int startNumber, int pageSize)
			throws CommonException {
		Map returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		int total;
		try {
			List<Map> groups = performanceManagerMapper.getUserGroupByUserId(Integer.valueOf(currentUserId),null);
			returnList = cutoverManagerMapper.getCutoverTask(startTime,
					endTime, status, cutoverTaskName, groups,Integer.valueOf(currentUserId),startNumber, pageSize);
			for (int i = 0, len = returnList.size(); i < len; i++) {
				Map cutoverTask = returnList.get(i);
				List<Map> parameters = cutoverManagerMapper
						.getCutoverTaskParameter((Integer) cutoverTask
								.get("SYS_TASK_ID"));
				Iterator iterator = parameters.iterator();
				while (iterator.hasNext()) {
					Map parameter = (Map) iterator.next();
					if (parameter.get("PARAM_NAME").equals("startTimeActual"))
						cutoverTask.put("START_TIME_ACTUAL", parameter
								.get("PARAM_VALUE"));
					else if (parameter.get("PARAM_NAME")
							.equals("endTimeActual"))
						cutoverTask.put("END_TIME_ACTUAL", parameter
								.get("PARAM_VALUE"));
				}
			}
			total = cutoverManagerMapper.getCutoverTaskCount(startTime,
					endTime, status, cutoverTaskName,groups,Integer.valueOf(currentUserId));
			returnData.put("total", total);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return returnData;
	}

	/**
	 * 判断割接任务名是否重复 返回是否存在信息：true 存在/false 不存在
	 * 
	 * @param taskName -
	 *            割接任务名 taskId - 割接任务Id
	 * @return Boolean true/false
	 * @throws CommonException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public Boolean checkTaskNameExist(Map map) throws CommonException {
		String taskName = (String) map.get("taskName");
		int taskId = (Integer) map.get("cutoverTaskId");
		Boolean taskDuplicate = false;
		try {
			int duplicateTaskCount = cutoverManagerMapper
					.getcutoverTaskExitList(taskId, taskName);
			if (duplicateTaskCount != 0)
				taskDuplicate = true;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return taskDuplicate;
	}

	/**
	 * 根据割接任务id查询割接任务的设备列表
	 * 
	 * @param cutOverTaskId
	 *            割接任务id
	 * @return TARGET_TYPE，TARGET_ID，DISPLAY_NAME
	 * @throws CommonException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public List<Map> getCutoverEquipList(int cutOverTaskId)
			throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = cutoverManagerMapper
					.getCutoverEquipList(cutOverTaskId);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return returnList;
	}
	/**
	 * 新增割接任务电路冲突检测
	 * @param cutoverEquipList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> addCutoverTaskPreCheck(List<String> cutoverEquipList,List<Map> currentCircuitList)throws CommonException
	{
		Map duplicateCircuitIdMap = cutoverPreCheck(cutoverEquipList,currentCircuitList);
		List duplicateCircuitIdList = (List)duplicateCircuitIdMap.get("duplicateCircuitIdList");
		List duplicateOTNCircuitIdList = (List)duplicateCircuitIdMap.get("duplicateOTNCircuitIdList");
		List<Map> duplicateCircuitList = new ArrayList<Map>();
		List<Map> duplicateOTNCircuitList = new ArrayList<Map>();
		List<Map> returnList = new ArrayList<Map>();
		Integer total1 = duplicateCircuitIdList.size();
		Integer total2 = duplicateOTNCircuitIdList.size();
		if(total1!=0)
		{
			duplicateCircuitList = cutoverManagerMapper.getCircuitsByIdList(duplicateCircuitIdList);
			returnList.addAll(duplicateCircuitList);
		}
			
		if(total2!=0)
		{
			
			duplicateOTNCircuitList = cutoverManagerMapper.getOTNCircuitsByIdList(duplicateOTNCircuitIdList);
			returnList.addAll(duplicateOTNCircuitList);
		}
			
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("rows", returnList);
		Integer total = duplicateCircuitList.size()+duplicateOTNCircuitList.size();
		resultMap.put("total", total);
		if(total==0)
			resultMap.put("conflict", 0);
		else
			resultMap.put("conflict", 1);
		return resultMap;
	}
	/**
	 * 添加割接任务
	 * 
	 * @param cutoverTaskId
	 *            割接任务id （因为与修改共用参数，此处id为空）
	 * @param taskName
	 *            割接任务名称
	 * @param taskDescription
	 *            任务描述
	 * @param startTime
	 *            割接预计开始时间
	 * @param endTime
	 *            割接预计结束时间
	 * @param status
	 *            割接任务状态
	 * @param filterAlarm
	 *            是否过滤告警
	 * @param snapshot
	 *            是否自动采集快照，提前多少小时采集快照
	 * @param cutoverEquipList
	 *            割接设备列表
	 * @param cutoverEquipNameList
	 *            割接设备名称列表
	 * @throws CommonException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public Map addCutoverTask(Date createTime, String cutoverTaskId,
			String taskName, String taskDescription, String startTime,
			String endTime, String status, String taskStatus,
			String filterAlarm, String autoUpdateCompareValue,String snapshot, List<String> cutoverEquipList,
			List<String> cutoverEquipNameList,List<String> privilegeList) throws CommonException {
		//先查询出这次割接影响的电路，用于判断是否有冲突的，没有冲突的话就把这些电路id保存到割接影响电路表
		String[] firstEquip = cutoverEquipList.get(0).split("_");
		String equipType = firstEquip[0];
		List nodeList = new ArrayList();
		List linkIdList = new ArrayList();
		//添加一个零，以防list为空时用in查询报错
		nodeList.add(0);
		for (int i = 0; i < cutoverEquipList.size(); i++) {
			String[] equipInfo = new String[2];
			equipInfo = cutoverEquipList.get(i).split("_");
//			if (!equipType.equals("99")) {
				nodeList.add(equipInfo[1]);
//			} else {
//				linkIdList.add(equipInfo[1]);
//			}

		}
//		if (equipType.equals("99")) {
//			equipType = "8";
//			List<Map> linkList = cutoverManagerMapper
//					.getLinkListByLinkIdList(linkIdList);
//			for (int i = 0; i < linkList.size(); i++) {
//				nodeList.add(linkList.get(i).get("A_END_PTP"));
//				nodeList.add(linkList.get(i).get("Z_END_PTP"));
//			}
//		}
		Map map = new HashMap();
//		map.put("start", 0);
//		map.put("limit", 999999999);
		map.put("nodeLevel", equipType);
		map.put("nodeList", nodeList);
		List<Map> currentCircuitList = new ArrayList();
//		Map currentCircuitMap = circuitManagerService
//				.selectAllCircuitAbout(map);
//		if (currentCircuitMap.get("rows") != null
//				&& ((List) currentCircuitMap.get("rows")).size() != 0)
//			currentCircuitList = (List) currentCircuitMap.get("rows");
		currentCircuitList = cutoverManagerMapper.selectAllCircuitAbout(map);
		Map precheckResult = addCutoverTaskPreCheck(cutoverEquipList,currentCircuitList);
		//如果有冲突电路，不再继续执行新增割接任务
		if(1==(Integer)precheckResult.get("conflict"))
			return precheckResult;
		Map resultMap = new HashMap();
		// 时间转换
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			// 获取用户ID
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();

			Integer userId = session.getAttribute("SYS_USER_ID") != null ? Integer
					.valueOf(session.getAttribute("SYS_USER_ID").toString())
					: null;
			// 预计开始时间
			Date start = timeFormat.parse(startTime);
			// 预计结束时间
			Date end = timeFormat.parse(endTime);
			//判断是否重复
			boolean taskDuplicate = false;
			int duplicateTaskCount = cutoverManagerMapper
			.getcutoverTaskExitList(-1, taskName);
			if (duplicateTaskCount != 0)
				taskDuplicate = true;
			if(taskDuplicate)
			{
				throw new CommonException(new Exception(),
						MessageCodeDefine.CUTOVER_TASK_NAME_DUPLICATE);
			}
			else
			{
				Map<String, Object> task = new HashMap<String, Object>();
				task.put("createTime", createTime);
				task.put("cutoverTaskId", null);
				task.put("taskName", taskName);
				task.put("taskDescription", taskDescription);
				task.put("start", start);
				task.put("end", end);
				task.put("createPerson", userId);
				task.put("taskStatus", taskStatus);
				// 保存割接任务
				cutoverManagerMapper.saveCutoverTask(task);
				//保存割接影响电路信息
				List<Map> cutoverCircuitList = new ArrayList<Map>(); 		
				for(int i = 0,len = currentCircuitList.size();i<len;i++)
				{
					Map circuit = new HashMap();
					Map c = currentCircuitList.get(i);
					circuit.put("cutoverTaskId", task.get("cutoverTaskId"));
					if(c.get("CIR_CIRCUIT_INFO_ID")!=null && Integer.valueOf(c.get("CIR_CIRCUIT_INFO_ID").toString())!=0)
					{
						circuit.put("circuitId", c.get("CIR_CIRCUIT_INFO_ID"));
						//电路类型 非OTN
						circuit.put("circuitType", 1);
					}
					if(c.get("CIR_OTN_CIRCUIT_INFO_ID")!=null && Integer.valueOf(c.get("CIR_OTN_CIRCUIT_INFO_ID").toString())!=0)
					{
						circuit.put("circuitId", c.get("CIR_OTN_CIRCUIT_INFO_ID"));
						//电路类型 OTN
						circuit.put("circuitType", 2);
					}
					cutoverCircuitList.add(circuit);
				}
				//保存割接影响电路信息的方法
				if(cutoverCircuitList.size()!=0)
					cutoverManagerMapper.saveCutoverCircuit(cutoverCircuitList);
				resultMap.put("cutoverTaskId", task.get("cutoverTaskId"));
				// 保存割接任务设备信息
				List<Map> equipList = new ArrayList<Map>();
				String[] equipInfo;
				if (cutoverEquipList.size() == cutoverEquipNameList.size()) {
					for (int i = 0; i < cutoverEquipList.size(); i++) {
						equipInfo = cutoverEquipList.get(i).split("_");
						// 需要保存的割接设备信息整合在map中
						Map<String, Object> cutoverEquipInfo = new HashMap<String, Object>();

						cutoverEquipInfo.put("taskInfoId", null);
						cutoverEquipInfo.put("taskId", task.get("cutoverTaskId"));
						cutoverEquipInfo.put("equipType", equipInfo[0]);
						cutoverEquipInfo.put("equipId", equipInfo[1]);
						cutoverEquipInfo.put("equipDisplayName",
								cutoverEquipNameList.get(i));
						cutoverEquipInfo.put("isSuccess", 0);
						cutoverEquipInfo.put("isComplete", 0);

						equipList.add(cutoverEquipInfo);
					}
				}
				cutoverManagerMapper.saveCutoverTaskInfo(equipList);
				
				/** 保存告警过滤器  **/
				AlarmFilterModel alarmFiltermodel = new AlarmFilterModel();
				// 过滤器名称
				alarmFiltermodel.setFilterName(taskName+"_告警过滤器");
				// 过滤器描述
				alarmFiltermodel.setDescription(taskName+"_告警过滤器");
				// 过滤器类型
				alarmFiltermodel.setFilterType(CommonDefine.ALARM_FILTER_TYPE_CUTOVER);
				// 综告过滤器
				alarmFiltermodel.setFilterFlag(CommonDefine.ALARM_FILTER_COM_REPORT_YES);
				// 创建者ID
				alarmFiltermodel.setSysUserId(Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString()));
				// 创建者名称
				alarmFiltermodel.setCreator(request.getSession().getAttribute("USER_NAME").toString());
				//有效开始时间
				alarmFiltermodel.setStartTime(startTime);
				//有效结束时间
				alarmFiltermodel.setEndTime(endTime);
				// 过滤器状态 1:启用;2:挂起
				if(filterAlarm.equals("0"))
					alarmFiltermodel.setStatus(CommonDefine.ALARM_FILTER_STATUS_PENDING);
				else
					alarmFiltermodel.setStatus(CommonDefine.ALARM_FILTER_STATUS_ENABLE);
				// 创建时间
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				alarmFiltermodel.setCreateTime(sf.format(new Date()));
				alarmManagementMapper.addAlarmFilterComReport(alarmFiltermodel);
				/** 告警过滤器名称关联表  **/
				Map<String, Object> modelMap = new HashMap<String, Object>();
				// 过滤器id
				modelMap.put("filterId", alarmFiltermodel.getFilterId());
				// 创建时间
				modelMap.put("createTime", alarmFiltermodel.getCreateTime());
				
				/** 告警过滤器源(设备)关联表 **/
				if (cutoverEquipList.size() == cutoverEquipNameList.size()) {
					
//					String[] firstEquip = cutoverEquipList.get(0).split("_");
//					String equipType = firstEquip[0];
//					List nodeList = new ArrayList();
//					List linkIdList = new ArrayList();
					for (int i = 0; i < cutoverEquipList.size(); i++) {
						
						equipInfo = cutoverEquipList.get(i).split("_");
						if (!equipType.equals("99")) {
							nodeList.add(equipInfo[1]);
						} else {
							linkIdList.add(equipInfo[1]);
						}

					}
					if (equipType.equals("99")) {
						equipType = "8";
						List<Map> linkList = cutoverManagerMapper
								.getLinkListByLinkIdList(linkIdList);
						for (int i = 0; i < linkList.size(); i++) {
							nodeList.add(linkList.get(i).get("A_END_PTP"));
							nodeList.add(linkList.get(i).get("Z_END_PTP"));
						}
						for (int i = 0; i < nodeList.size(); i++) {
							modelMap.put("id", nodeList.get(i));
							// 告警源级别
							modelMap.put("lv", equipType);
							alarmManagementMapper.addAlarmFilterResourceRelation(modelMap);
						}
					}
					else
					{
						for (int i = 0; i < cutoverEquipList.size(); i++) {
							equipInfo = cutoverEquipList.get(i).split("_");
							
							modelMap.put("id", equipInfo[1]);
							// 告警源级别
							modelMap.put("lv", equipInfo[0]);
							alarmManagementMapper.addAlarmFilterResourceRelation(modelMap);
						}
					}
					
				}
				// 保存割接任务参数信息
				List<Map> taskParamList = new ArrayList<Map>();
				// 一共有status，filterAlarm，snapshot 共三个参数
				//20140326 还要加上权限组的信息
				//20140912 还要加上是否自动更新基准值的参数
				//操作权限组保存
				Map taskParamMap=new HashedMap();
				String privilegeListString = "";
//				if(privilegeList.size()!=0)
//				{
					for(int i = 0; i<privilegeList.size();i++){
						if(i==0){
							privilegeListString = privilegeList.get(i);
						}else{
							privilegeListString = privilegeListString + "," +  privilegeList.get(i);
						}
					}
					taskParamMap.put("taskParamId", null);
					taskParamMap.put("taskId", task.get("cutoverTaskId"));
					taskParamMap.put("paramName",CommonDefine.PRIVILEGE);
					taskParamMap.put("paramValue",privilegeListString);
					taskParamList.add(taskParamMap);
//				}
				for (int i = 0; i < 4; i++) {
					Map parameterMap = new HashMap();
					parameterMap.put("taskParamId", null);
					parameterMap.put("taskId", task.get("cutoverTaskId"));
					if (0 == i) {
						parameterMap.put("paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.STATUS);
						parameterMap.put("paramValue", status);
					} else if (1 == i) {
						parameterMap
								.put(
										"paramName",
										CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.FILTER_ALARM);
						parameterMap.put("paramValue", filterAlarm);
					} else if (2 == i) {
						parameterMap.put("paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT);
						parameterMap.put("paramValue", snapshot);
					} else if (3 == i) {
						parameterMap.put("paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.AUTO_UPDATE_COMPARE_VALUE);
						parameterMap.put("paramValue", autoUpdateCompareValue);
					}
					taskParamList.add(parameterMap);
				}
				cutoverManagerMapper.saveCutoverTaskParameter(taskParamList);
				// 创建定时任务
				// 因为一条割接任务需要创建三个不同时间的定时任务，不能直接用割接任务id
				// 同时作为三个定时任务的id，所以建立规则：创建定时任务时，
				// 将割接任务id取相反数：-cutoverTaskId*100,
				// 割接前快照的id为-cutoverTaskId*100-1；
				// 过滤告警的id为：-cutoverTask*100-2;
				// 割接完成的id为-cutoverTask*100-3；
				// 该id为上述规则转换之后的id

				// snapshot:是否自动采集快照，预计开始时间之前几小时开始采集快照
				// 0：不自动采集，-1：立即采集，1~8：预计开始时间之前x小时开始采集
				Calendar Cal = Calendar.getInstance();
				Date currentTime = Cal.getTime();
				Cal.setTime(start);
				// 任务预计开始时间的时间分解数据
				int planStartMinute = Cal.get(Calendar.MINUTE);
				int planStartHour = Cal.get(Calendar.HOUR_OF_DAY);
				int planStartDay = Cal.get(Calendar.DAY_OF_MONTH);
				int planStartMonth = Cal.get(Calendar.MONTH) + 1;
				int planStartYear = Cal.get(Calendar.YEAR);
				String filterAlarmCron = "0 " + planStartMinute + " "
						+ planStartHour + " " + planStartDay + " " + planStartMonth
						+ " " + "?" + " " + planStartYear;
				if(Integer.valueOf(snapshot)!=-1)
					Cal.add(java.util.Calendar.HOUR_OF_DAY, -Integer.valueOf(snapshot));
				else 
				{
					Cal.setTime(currentTime);
					Cal.add(java.util.Calendar.MINUTE, 2);
				}
					
				// 割接前快照时间的时间分解数据
				int snapshotBeforeMinute = 0;
				snapshotBeforeMinute = Cal.get(Calendar.MINUTE);
				int snapshotBeforeHour = Cal.get(Calendar.HOUR_OF_DAY);
				int snapshotBeforeDay = Cal.get(Calendar.DAY_OF_MONTH);
				int snapshotBeforeMonth = Cal.get(Calendar.MONTH) + 1;
				int snapshotBeforeYear = Cal.get(Calendar.YEAR);
				String snapshotBeforeCron = "0 " + snapshotBeforeMinute + " "
						+ snapshotBeforeHour + " " + snapshotBeforeDay + " "
						+ snapshotBeforeMonth + " " + "?" + " "
						+ snapshotBeforeYear;
				Cal.setTime(end);
				// 任务预计结束时间的时间分解数据
				int planEndMinute = Cal.get(Calendar.MINUTE);
				int planEndHour = Cal.get(Calendar.HOUR_OF_DAY);
				int planEndDay = Cal.get(Calendar.DAY_OF_MONTH);
				int planEndMonth = Cal.get(Calendar.MONTH) + 1;
				int planEndYear = Cal.get(Calendar.YEAR);
				String planEndCron = "0 " + planEndMinute + " " + planEndHour + " "
						+ planEndDay + " " + planEndMonth + " " + "?" + " "
						+ planEndYear;
				Long id = (Long) task.get("cutoverTaskId");
				int idValue = id.intValue() * 100;
				// 割接前快照任务
				if (!snapshot.equals("0"))// 为0 说明不自动快照
				{
					Map snapshotParam = new HashMap();
					snapshotParam.put("userId", userId);
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_CUTOVER,
							-idValue - 1, CutoverSnapshotBeforeJob.class,
							snapshotBeforeCron, snapshotParam);
					// 测试 立即执行
					// quartzManagerService.ctrlJob(
					// CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1,
					// CommonDefine.QUARTZ.JOB_ACTIVATE);

					if (status.equals("4"))// status=4，挂起任务
						quartzManagerService.ctrlJob(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1,
								CommonDefine.QUARTZ.JOB_PAUSE);
				}

				// 过滤告警任务
				if (filterAlarm.equals("1"))// 为1才自动过滤告警
				{
					Map filterAlarmParam = new HashMap();
					filterAlarmParam.put("userId", userId);
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_CUTOVER,
							-idValue - 2, CutoverfilterAlarmJob.class,
							filterAlarmCron, filterAlarmParam);
					if (status.equals("4"))// status=4，挂起任务
						quartzManagerService.ctrlJob(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
								CommonDefine.QUARTZ.JOB_PAUSE);
				}

				// 割接完成任务,注意：如果snapshot不为0，则割接完成任务包含了本类中的割接后快照和割接完成两个方法。
				Map completeTaskParam = new HashMap();
				completeTaskParam.put("userId", userId);
				completeTaskParam.put("snapshot", snapshot);
				quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_CUTOVER,
						-idValue - 3, CutoverCompleteTaskJob.class, planEndCron,
						completeTaskParam);
				if (status.equals("4"))// status=4，挂起任务
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_CUTOVER,
							-idValue - 3, CommonDefine.QUARTZ.JOB_PAUSE);
			}
			return resultMap;
		} catch (ParseException e) {
			throw new CommonException(e,
					MessageCodeDefine.CUTOVER_TASK_TIME_PARSE_FAILURE);
		} catch (CommonException e) {
			throw new CommonException(e, e.getErrorCode());
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}

	}

	/**
	 * 初始化任务信息
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> initTaskInfo(Map<String, Object> map)
			throws CommonException {

		List<Map> returnList = new ArrayList<Map>();
		Integer cutoverTaskId = (Integer) map.get("taskId");
		try {
			returnList = cutoverManagerMapper.initTaskInfo(cutoverTaskId);
			Map task = returnList.get(0);
			if (String.valueOf(task.get("privilegeString")).equals("null")
					|| String.valueOf(task.get("privilegeString")).equals("")) {
				String[] privilegeList = new String[0];
				task.put("privilegeList",privilegeList );
			}
			else
			{
				String[] privilegeList = String.valueOf(task.get("privilegeString")).split(",");
				task.put("privilegeList",privilegeList );
			}
			
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return returnList;
	}

	/**
	 * 修改割接任务
	 * 
	 * @param cutoverTaskId
	 *            割接任务id
	 * @param taskName
	 *            割接任务名称
	 * @param taskDescription
	 *            任务描述
	 * @param startTime
	 *            割接预计开始时间
	 * @param endTime
	 *            割接预计结束时间
	 * @param status
	 *            割接任务状态
	 * @param filterAlarm
	 *            是否过滤告警
	 * @param snapshot
	 *            是否自动采集快照，提前多少小时采集快照
	 * @param cutoverEquipList
	 *            割接设备列表
	 * @param cutoverEquipNameList
	 *            割接设备名称列表
	 * @throws CommonException
	 */
	
	@Override
	public void modifyCutoverTask(Date createTime, String cutoverTaskId,
			String taskName, String taskDescription, String startTime,
			String endTime, String status, String taskStatus,String filterAlarm, String autoUpdateCompareValue,String snapshot,
			List<String> cutoverEquipList, List<String> cutoverEquipNameList,
			List<String> privilegeList)
			throws CommonException {
		// 时间转换
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			// 获取用户ID
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();

			Integer userId = session.getAttribute("SYS_USER_ID") != null ? Integer
					.valueOf(session.getAttribute("SYS_USER_ID").toString())
					: null;
			// 预计开始时间
			Date start = timeFormat.parse(startTime);
			// 预计结束时间
			Date end = timeFormat.parse(endTime);
			//判断是否重复
			boolean taskDuplicate = false;
			int duplicateTaskCount = cutoverManagerMapper
			.getcutoverTaskExitList(Integer.valueOf(cutoverTaskId), taskName);
			if (duplicateTaskCount != 0)
				taskDuplicate = true;
			if(taskDuplicate)
			{
				throw new CommonException(new Exception(),
						MessageCodeDefine.CUTOVER_TASK_NAME_DUPLICATE);
			}
			else
			{
				Map<String, Object> task = new HashMap<String, Object>();
				task.put("createTime", createTime);
				task.put("createPerson", userId);
				task.put("cutoverTaskId", Integer.parseInt(cutoverTaskId));
				task.put("taskName", taskName);
				task.put("taskDescription", taskDescription);
				task.put("start", start);
				task.put("end", end);
				// 保存割接任务
				cutoverManagerMapper.updateCutoverTask(task);
				// 先删除原有的割接任务设备信息
				List<Integer> taskIdList = new ArrayList<Integer>();
				taskIdList.add(Integer.parseInt(cutoverTaskId));
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("taskIdList", taskIdList);
				cutoverManagerMapper.deleteTaskInfo(map);
				// 再保存割接任务设备信息
				List<Map> equipList = new ArrayList<Map>();
				String[] equipInfo;
				if (cutoverEquipList.size() == cutoverEquipNameList.size()) {
					for (int i = 0; i < cutoverEquipList.size(); i++) {
						equipInfo = cutoverEquipList.get(i).split("_");
						// 需要保存的割接设备信息整合在map中
						Map<String, Object> cutoverEquipInfo = new HashMap<String, Object>();

						cutoverEquipInfo.put("taskInfoId", null);
						cutoverEquipInfo.put("taskId", task.get("cutoverTaskId"));
						cutoverEquipInfo.put("equipType", equipInfo[0]);
						cutoverEquipInfo.put("equipId", equipInfo[1]);
						cutoverEquipInfo.put("equipDisplayName",
								cutoverEquipNameList.get(i));
						cutoverEquipInfo.put("isSuccess", 0);
						cutoverEquipInfo.put("isComplete", 0);

						equipList.add(cutoverEquipInfo);
					}
				}
				cutoverManagerMapper.saveCutoverTaskInfo(equipList);

				// 先删除原有的割接任务参数信息
				cutoverManagerMapper.deleteTaskParamSingle(Integer
						.parseInt(cutoverTaskId));
				// 再保存割接任务参数信息
				List<Map> taskParamList = new ArrayList<Map>();
				// 一共有status，filterAlarm，snapshot 共三个参数\
				//还要加上权限组参数
				//操作权限组保存
				Map taskParamMap=new HashedMap();
				String privilegeListString = "";
//				if(privilegeList.size()!=0)
//				{
					for(int i = 0; i<privilegeList.size();i++){
						if(i==0){
							privilegeListString = privilegeList.get(i);
						}else{
							privilegeListString = privilegeListString + "," +  privilegeList.get(i);
						}
					}
					taskParamMap.put("taskParamId", null);
					taskParamMap.put("taskId", task.get("cutoverTaskId"));
					taskParamMap.put("paramName",CommonDefine.PRIVILEGE);
					taskParamMap.put("paramValue",privilegeListString);
					taskParamList.add(taskParamMap);
//				}
				for (int i = 0; i < 4; i++) {
					Map parameterMap = new HashMap();
					parameterMap.put("taskParamId", null);
					parameterMap.put("taskId", task.get("cutoverTaskId"));
					if (0 == i) {
						parameterMap.put("paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.STATUS);
						parameterMap.put("paramValue", status);
					} else if (1 == i) {
						parameterMap
								.put(
										"paramName",
										CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.FILTER_ALARM);
						parameterMap.put("paramValue", filterAlarm);
					} else if (2 == i) {
						parameterMap.put("paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT);
						parameterMap.put("paramValue", snapshot);
					} else if (3 == i) {
						parameterMap.put("paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.AUTO_UPDATE_COMPARE_VALUE);
						parameterMap.put("paramValue", autoUpdateCompareValue);
					}
					taskParamList.add(parameterMap);
				}
				cutoverManagerMapper.saveCutoverTaskParameter(taskParamList);

				// 创建定时任务
				// 因为一条割接任务需要创建三个不同时间的定时任务，不能直接用割接任务id
				// 同时作为三个定时任务的id，所以建立规则：创建定时任务时，
				// 将割接任务id取相反数：-cutoverTaskId*100,
				// 割接前快照的id为-cutoverTaskId*100-1；
				// 过滤告警的id为：-cutoverTask*100-2;
				// 割接完成的id为-cutoverTask*100-3；
				// 该id为上述规则转换之后的id

				// snapshot:是否自动采集快照，预计开始时间之前几小时开始采集快照
				// 0：不自动采集，-1：立即采集，1~8：预计开始时间之前x小时开始采集
				Calendar Cal = Calendar.getInstance();
				Date currentTime = Cal.getTime();
				Cal.setTime(start);
				// 任务预计开始时间的时间分解数据
				int planStartMinute = Cal.get(Calendar.MINUTE);
				int planStartHour = Cal.get(Calendar.HOUR_OF_DAY);
				int planStartDay = Cal.get(Calendar.DAY_OF_MONTH);
				int planStartMonth = Cal.get(Calendar.MONTH) + 1;
				int planStartYear = Cal.get(Calendar.YEAR);
				String filterAlarmCron = "0 " + planStartMinute + " "
						+ planStartHour + " " + planStartDay + " " + planStartMonth
						+ " " + "?" + " " + planStartYear;
				Cal.add(java.util.Calendar.HOUR_OF_DAY, -Integer.valueOf(snapshot));

				// 割接前快照时间的时间分解数据
				int snapshotBeforeMinute = Cal.get(Calendar.MINUTE);
				int snapshotBeforeHour = Cal.get(Calendar.HOUR_OF_DAY);
				int snapshotBeforeDay = Cal.get(Calendar.DAY_OF_MONTH);
				int snapshotBeforeMonth = Cal.get(Calendar.MONTH) + 1;
				int snapshotBeforeYear = Cal.get(Calendar.YEAR);
				String snapshotBeforeCron = "0 " + snapshotBeforeMinute + " "
						+ snapshotBeforeHour + " " + snapshotBeforeDay + " "
						+ snapshotBeforeMonth + " " + "?" + " "
						+ snapshotBeforeYear;
				Cal.setTime(end);
				// 任务预计结束时间的时间分解数据
				int planEndMinute = Cal.get(Calendar.MINUTE);
				int planEndHour = Cal.get(Calendar.HOUR_OF_DAY);
				int planEndDay = Cal.get(Calendar.DAY_OF_MONTH);
				int planEndMonth = Cal.get(Calendar.MONTH) + 1;
				int planEndYear = Cal.get(Calendar.YEAR);
				String planEndCron = "0 " + planEndMinute + " " + planEndHour + " "
						+ planEndDay + " " + planEndMonth + " " + "?" + " "
						+ planEndYear;
				Integer id = (Integer) task.get("cutoverTaskId");
				int idValue = id.intValue() * 100;
				if (status.equals("4"))// 将任务状态修改为挂起
				{
					// 设置为不自动采集快照，如果有割接前快照任务，将其删除
					if (snapshot.equals("0")) {
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1)) {
							quartzManagerService.ctrlJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1,
									CommonDefine.QUARTZ.JOB_DELETE);
						}

					} else if (snapshot.equals("-1")) {
						// TODO
					} else {
						// 割接前快照任务存在=》修改执行时间，将之挂起
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1)) {
							quartzManagerService.modifyJobTime(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1,
									snapshotBeforeCron);
							quartzManagerService.ctrlJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1,
									CommonDefine.QUARTZ.JOB_PAUSE);
						}

					}
					// 不过滤告警,如果以前有，则删除
					if (filterAlarm.equals("0")) {
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2)) {
							quartzManagerService.ctrlJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									CommonDefine.QUARTZ.JOB_DELETE);
						}
					}
					// 过滤告警
					else {
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2))// 任务已经存在
						{
							quartzManagerService.modifyJobTime(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									filterAlarmCron);
							quartzManagerService.ctrlJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									CommonDefine.QUARTZ.JOB_PAUSE);
						} else// 任务不存在
						{
							Map filterAlarmParam = new HashMap();
							filterAlarmParam.put("userId", userId);
							quartzManagerService.addJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									CutoverfilterAlarmJob.class, filterAlarmCron,
									filterAlarmParam);
							quartzManagerService.ctrlJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									CommonDefine.QUARTZ.JOB_PAUSE);
						}
					}
					// 割接完成任务，如果有，删掉重建
					if (quartzManagerService.IsJobExist(
							CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 3))// 任务已经存在
					{
						quartzManagerService.ctrlJob(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 3,
								CommonDefine.QUARTZ.JOB_DELETE);
					}
					// 割接完成任务,注意：如果snapshot不为0，则割接完成任务包含了本类中的割接后快照和割接完成两个方法。
					Map completeTaskParam = new HashMap();
					completeTaskParam.put("userId", userId);
					completeTaskParam.put("snapshot", snapshot);
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_CUTOVER,
							-idValue - 3, CutoverCompleteTaskJob.class,
							planEndCron, completeTaskParam);
					if (status.equals("4"))// status=4，挂起任务
						quartzManagerService.ctrlJob(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 3,
								CommonDefine.QUARTZ.JOB_PAUSE);

				}
				// 没有选择挂起

				else {
					// 设置为不自动采集快照，如果有割接前快照任务，将其删除
					if (snapshot.equals("0")) {
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1)) {
							quartzManagerService.ctrlJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1,
									CommonDefine.QUARTZ.JOB_DELETE);
						}

					} else if (snapshot.equals("-1")) {
						// TODO
					} else {
						// 割接前快照任务存在=》修改执行时间
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1)) {
							quartzManagerService.modifyJobTime(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 1,
									snapshotBeforeCron);
						}

					}
					// 不过滤告警,如果以前有，则删除
					if (filterAlarm.equals("0")) {
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2)) {
							quartzManagerService.ctrlJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									CommonDefine.QUARTZ.JOB_DELETE);
						}
					}
					// 过滤告警
					else {
						if (quartzManagerService.IsJobExist(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2))// 任务已经存在
						{
							quartzManagerService.modifyJobTime(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									filterAlarmCron);
						} else// 任务不存在
						{
							Map filterAlarmParam = new HashMap();
							filterAlarmParam.put("userId", userId);
							quartzManagerService.addJob(
									CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 2,
									CutoverfilterAlarmJob.class, filterAlarmCron,
									filterAlarmParam);
						}
					}
					// 割接完成任务，如果有，删掉重建
					if (quartzManagerService.IsJobExist(
							CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 3))// 任务已经存在
					{
						quartzManagerService.ctrlJob(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 3,
								CommonDefine.QUARTZ.JOB_DELETE);
					}
					// 割接完成任务,注意：如果snapshot不为0，则割接完成任务包含了本类中的割接后快照和割接完成两个方法。
					Map completeTaskParam = new HashMap();
					completeTaskParam.put("userId", userId);
					completeTaskParam.put("snapshot", snapshot);
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_CUTOVER,
							-idValue - 3, CutoverCompleteTaskJob.class,
							planEndCron, completeTaskParam);
					if (status.equals("4"))// status=4，挂起任务
						quartzManagerService.ctrlJob(
								CommonDefine.QUARTZ.JOB_CUTOVER, -idValue - 3,
								CommonDefine.QUARTZ.JOB_PAUSE);
				}
			}
			
		} catch (ParseException e) {
			throw new CommonException(e,
					MessageCodeDefine.CUTOVER_TASK_TIME_PARSE_FAILURE);
		}  catch (CommonException e) {
			throw new CommonException(e, e.getErrorCode());
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}

	}

	/**
	 * 删除割接任务
	 */
	public void deleteTask(List<Integer> taskIdList) throws CommonException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taskIdList", taskIdList);
			cutoverManagerMapper.deleteTaskParam(map);
			cutoverManagerMapper.deleteTaskInfo(map);
			cutoverManagerMapper.deleteTask(map);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
	}

	/**
	 * 查询链路信息
	 * 
	 * @param emsId
	 *            网管id
	 * @param linkType
	 *            链路类型：1外部链路，2内部链路
	 * @param linkNameOrId
	 *            链路名称或链路号
	 * @return
	 */
	public Map getLink(String emsId, String emsGroupId, String linkType,
			String linkNameOrId, int userId, int startNumber, int pageSize)
			throws CommonException {
		Map map = new HashMap();
		List<Map> emsList = new ArrayList<Map>();
		if (!emsGroupId.isEmpty()) {
			emsList = commonManagerService.getAllEmsByEmsGroupId(userId,
					Integer.parseInt(emsGroupId), true, false);

		}
		List<Integer> emsIdList = new ArrayList<Integer>();
		for (int i = 0, len = emsList.size(); i < len; i++) {
			Integer emsIdValue = (Integer) emsList.get(i).get(
					"BASE_EMS_CONNECTION_ID");
			if (emsIdValue != -99)
				emsIdList.add(emsIdValue);
		}
		try {
			int emsgroup = -2;
			try {
				emsgroup = Integer.parseInt(emsGroupId);
			} catch (RuntimeException e1) {

				emsgroup = -2;
			}
			int emsConnectionId = -1;
			int type = -1;
			int id = -1;
			try {
				id = Integer.parseInt(linkNameOrId);
			} catch (Exception e) {
				id = -1;
			}
			try {
				emsConnectionId = Integer.parseInt(emsId);
			} catch (RuntimeException e) {
				emsConnectionId = -1;
			}
			try {
				type = Integer.parseInt(linkType);
			} catch (RuntimeException e) {
				type = -1;
			}
			List dataList = cutoverManagerMapper.getLink(emsConnectionId,
					emsgroup, emsIdList, type, id, linkNameOrId, startNumber,
					pageSize);
			int total = cutoverManagerMapper.getLinkCount(emsConnectionId,
					emsgroup, emsIdList, type, id, linkNameOrId);
			map.put("rows", dataList);
			map.put("total", total);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return map;
	}

	/**
	 * 根据割接任务id查询割接任务的link列表
	 * 
	 * @param cutOverTaskId
	 *            割接任务id
	 * @return BASE_LINK_ID,DISPLAY_NAME
	 * @throws CommonException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public List<Map> getCutoverLinkList(int cutOverTaskId)
			throws CommonException {
		List<Map> returnList = new ArrayList<Map>();
		try {
			returnList = cutoverManagerMapper
					.getCutoverEquipList(cutOverTaskId);
			for (int i = returnList.size() - 1; i >= 0; i--) {
				Map map = returnList.get(i);
				if ((Integer) map.get("TARGET_TYPE") != 99) {
					returnList.remove(i);
					continue;
				}
				String displayName = (String) map.get("TARGET_NAME");
				// displayNameArray 三个元素：linkName，A端网元端口名称，Z端网元端口名称
				String displayNameArray[] = displayName.split("@");
				map.put("LINK_NAME", displayNameArray[0]);
				
				map.put("A_NAME", displayNameArray[1]);
				map.put("Z_NAME", displayNameArray[2]);
				map.put("BASE_LINK_ID", map.get("TARGET_ID"));
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return returnList;
	}

	/**
	 * 影響電路查詢
	 */
	@Override
	public Map<String, Object> searchCircuitsInfluenced(Map map)
			throws CommonException {
		int cutoverTaskId = Integer.valueOf((String) map.get("cutoverTaskId"));
		Map conditionMap = new HashMap();
		Map resultMap = new HashMap();
		try {
			conditionMap.put("VALUE", "*");
			conditionMap.put("NAME", "T_SYS_TASK_INFO");
			conditionMap.put("ID_NAME", "SYS_TASK_ID");
			conditionMap.put("ID_VALUE", cutoverTaskId);
			List<Map> equipList = new ArrayList<Map>();
			equipList = cutoverManagerMapper.getByParameter(conditionMap);
			Map firstEquip = equipList.get(0);
			String equipType = ((Integer) firstEquip.get("TARGET_TYPE"))
					.toString();
			List nodeList = new ArrayList();
			if (!equipType.equals("99")) {
				for (int i = 0; i < equipList.size(); i++) {
					nodeList.add(equipList.get(i).get("TARGET_ID"));
				}
			} else {
				equipType = "8";
				List<Map> linkList = cutoverManagerMapper.getCutoverEqptLinkList(cutoverTaskId);
				for (int i = 0; i < linkList.size(); i++) {
					nodeList.add(linkList.get(i).get("A_END_PTP"));
					nodeList.add(linkList.get(i).get("Z_END_PTP"));
				}
			}
			Map searchMap = new HashMap();
			searchMap.put("start", map.get("start"));
			searchMap.put("limit", map.get("limit"));
			searchMap.put("nodeLevel", equipType);
			searchMap.put("nodeList", nodeList);
			resultMap = circuitManagerService.selectAllCircuitAbout(searchMap);
		}  catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_UNENCODING);
		}
		return resultMap;
	}

	/**
	 * 端口性能值查询
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> searchPmValue(int cutoverTaskId,
			int startNumber, int pageSize) throws CommonException {
		Map map = new HashMap();
		try {
			
			List dataList = cutoverManagerMapper.searchPmValue(cutoverTaskId,
					startNumber, pageSize);
			int total = cutoverManagerMapper.getPmValueCount(cutoverTaskId);
			for(int i=0;i<dataList.size();i++)
			{
				Map oneRow = new HashMap();
				int level = CommonDefine.PM.MUL.SEC_PM_ZC;
				oneRow = (Map)dataList.get(i);
				if(oneRow.get("VALUE_BEFORE")!=null && (String)oneRow.get("VALUE_BEFORE")!=""
					&& oneRow.get("VALUE_AFTER")!=null && (String)oneRow.get("VALUE_AFTER")!="")
				{
					BigDecimal valueBefore = new BigDecimal((String)oneRow.get("VALUE_BEFORE"));
					BigDecimal valueAfter = new BigDecimal((String)oneRow.get("VALUE_AFTER"));
					BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
					oneRow.put("DIFFERENCE", difference);
					double differenceDouble = difference.doubleValue();
					int levelTemp = 0;
					double value = Math.abs(differenceDouble);
					// 判断级别
					levelTemp = getLevel(level, value);
					if (levelTemp > level) {
						level = levelTemp;
					}
				}
				oneRow.put("level", level);
			}
			map.put("rows", dataList);
			map.put("total", total);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return map;
	}

	/**
	 * 割接前快照
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public void snapshotBefore(String cutoverTaskId, int userId)
			throws CommonException {

		//调用统一方法
		snapshot(cutoverTaskId,userId,1);
	}
	
	/**
	 * 割接后快照
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public void snapshotAfter(String cutoverTaskId, int userId)
			throws CommonException {
		//调用统一方法
		snapshot(cutoverTaskId,userId,2);
	}
	
	//处理
	private void handleSnapshot_performance(List<Map> data,Map emsIdMap,String cutoverTaskId,int snapshotTimeFlag,Date currentTime) throws CommonException{

		try {
			int targetType = (Integer) data.get(0).get("TARGET_TYPE");
			
			// 该map的所有key为相关端口id,value为该端口所属的网管id
			Map portsMap = new HashMap();

			List ports = new ArrayList();
			// 该list存放所有需要查询性能值的端口，以及对应的网管id,新加上网元id
			List<Map> portsInLink = new ArrayList<Map>();
			// targetType=8,设备类型为端口
			if (CommonDefine.TREE.NODE.PTP == targetType) {
				// 将根据这个list中的端口id查询link表，从而查出所有本端端口id及对端端口的id，同时包含了网管信息，网元信息
				// 可能有冗余信息，通过加到map中用containsKey方法去除重复值
				List cutoverPortIds = new ArrayList();
				List equipIds = new ArrayList();
				
				for (int i = 0, len = data.size(); i < len; i++) {
					//cutoverPortIds.add(data.get(i).get("TARGET_ID"));
					Map item = new HashMap();
					item.put("BASE_PTP_ID", data.get(i).get("TARGET_ID"));
					cutoverPortIds.add(item);
					equipIds.add(data.get(i).get("TARGET_ID"));
					// if(!map.containsKey(data.get(i).get("TARGET_ID")))
					// map.put(data.get(i).get("TARGET_ID"), 0);
				}
				ports = cutoverManagerMapper.searchPorts(targetType,
						equipIds);
				portsInLink = cutoverManagerMapper
						.searchPortsInLink(cutoverPortIds);
	
			}
			// targetType=99,设备类型为链路
			else if (99 == targetType) {
				List linkIds = new ArrayList();
				for (int i = 0, len = data.size(); i < len; i++) {
					linkIds.add(data.get(i).get("TARGET_ID"));
				}
				portsInLink = cutoverManagerMapper.searchPortsByLink(linkIds);
	
			}
			// 割接设备为网元，shelf，unit,subunit
			else if (CommonDefine.TREE.NODE.NE == targetType
					|| CommonDefine.TREE.NODE.SHELF == targetType
					|| CommonDefine.TREE.NODE.UNIT == targetType
					|| CommonDefine.TREE.NODE.SUBUNIT == targetType) {
				List equipIds = new ArrayList();
				for (int i = 0, len = data.size(); i < len; i++) {
					equipIds.add(data.get(i).get("TARGET_ID"));
				}
				ports = cutoverManagerMapper.searchPorts(targetType,
						equipIds);
				portsInLink = cutoverManagerMapper.searchPortsInLink(ports);
			}
			
			for (int i = 0, len = portsInLink.size(); i < len; i++) {
				if (!portsMap.containsKey(portsInLink.get(i).get("A_END_PTP"))) {
					if (emsIdMap.containsKey(portsInLink.get(i).get(
							"A_EMS_CONNECTION_ID"))) {
						portsMap.put(portsInLink.get(i).get("A_END_PTP"),
								portsInLink.get(i).get("A_EMS_CONNECTION_ID"));
					}
	
				}
	
				if (!portsMap.containsKey(portsInLink.get(i).get("Z_END_PTP"))) {
					if (emsIdMap.containsKey(portsInLink.get(i).get(
							"Z_EMS_CONNECTION_ID")))
						portsMap.put(portsInLink.get(i).get("Z_END_PTP"),
								portsInLink.get(i).get("A_EMS_CONNECTION_ID"));
				}
	
			}
			for (int i = 0, len = ports.size(); i < len; i++) {
				if (!portsMap.containsKey(((Map) ports.get(i))
						.get("BASE_PTP_ID"))) {
					portsMap.put(((Map) ports.get(i)).get("BASE_PTP_ID"),
							((Map) ports.get(i)).get("BASE_EMS_CONNECTION_ID"));
				}
			}
			// 根据端口列表取性能值
			List<PmDataModel> pmDataList = null;
	
			List emsIdList = new ArrayList();
			Iterator iter = portsMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object ptpIdObj = entry.getKey();
				Object emsObj = entry.getValue();
				int emsId = (Integer) emsObj;
				if (!emsIdList.contains(emsId))
					emsIdList.add((Integer) emsObj);
			}
			
			// 先删除原有的割接任务性能信息（如果存在）
			List<Integer> taskIdList = new ArrayList<Integer>();
			taskIdList.add(Integer.parseInt(cutoverTaskId));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taskIdList", taskIdList);
			//
			map.put("snapshotTimeFlag", snapshotTimeFlag);
			if(snapshotTimeFlag == 1){
				cutoverManagerMapper.deleteTaskPerformance(map);
			}else{
				//现在修改为割接前割接后保存在同一条数据，多次保存不再需要删除之前保存的数据
				//只要对相应的值再次进行更新即可
			}
			for (int i = 0, len = emsIdList.size(); i < len; i++) {
				// 采集性能
				IDataCollectServiceProxy dataCollectService = SpringContextUtil
						.getDataCollectServiceProxy((Integer) emsIdList.get(i));
				List<Integer> ptpIdList = new ArrayList<Integer>();
				iter = portsMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					int ptpId = (Integer) entry.getKey();
					int emsId = (Integer) entry.getValue();
					if (emsId == (Integer) emsIdList.get(i)) {
						ptpIdList.add(ptpId);
	//								portsMap.remove(entry.getKey());
					}
				}
				pmDataList = dataCollectService.getCurrentPmData_PtpList(
						ptpIdList, new short[] {}, new int[] {
								CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG,
								CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG },
						new int[] { CommonDefine.PM.GRANULARITY_15MIN_FLAG }, false, true, false,
						CommonDefine.COLLECT_LEVEL_1);
				List<Map> pmValueList = new ArrayList<Map>();
				//专门用来保存中兴的平均值
				List<Map> pmValueList_AVG = new ArrayList<Map>();
				for (int j = 0; j < pmDataList.size(); j++) {
					PmDataModel pmData = pmDataList.get(j);
					List<PmMeasurementModel> pmMeasurementList = pmData
							.getPmMeasurementList();
					for (int k = 0; k < pmMeasurementList.size(); k++) {
						// 需要保存的性能值信息整合在map中
						Map<String, Object> pmValueInfo = new HashMap<String, Object>();
	
						pmValueInfo.put("cutoverTaskId", Integer
								.valueOf(cutoverTaskId));
						pmValueInfo.put("neId", pmData.getNeId());
						pmValueInfo.put("portId", pmData.getPtpId());
						pmValueInfo.put("pmName", pmMeasurementList.get(k)
								.getPmdescription());
						pmValueInfo.put("pmValue", pmMeasurementList.get(k)
								.getValue());
						pmValueInfo.put("targetType", pmData.getTargetType());
						pmValueInfo.put("pmStdIndex", pmMeasurementList.get(k)
								.getPmStdIndex());
						pmValueInfo.put("type", pmMeasurementList.get(k).getType());
						pmValueInfo.put("snapshotTime", currentTime);
						// snapshotTimeFlag=1 ：割接前快照
						pmValueInfo.put("snapshotTimeFlag", snapshotTimeFlag);
						
						//现在可以直接获得异常等级了
						pmValueInfo.put("exceptionLevel",pmMeasurementList.get(k).getExceptionLv());
						if (pmMeasurementList.get(k).getPmStdIndex().contains(
								"MAX")
								|| pmMeasurementList.get(k).getPmStdIndex()
										.contains("MIN")){
							//不保存最大最小平均值
						}
						else
						{
							if(pmMeasurementList.get(k).getPmStdIndex()
									.contains("AVG")){
								pmValueList_AVG.add(pmValueInfo);
							}else{
								pmValueList.add(pmValueInfo);
							}
						}
					}
				}
				//保存性能信息
				if(snapshotTimeFlag == 1){
					if(!pmValueList.isEmpty()){
						cutoverManagerMapper.savePmValueBefore(pmValueList);
					}else{
						if(!pmValueList_AVG.isEmpty()){
							cutoverManagerMapper.savePmValueBefore(pmValueList_AVG);
						}
					}
				}else{
					if(!pmValueList.isEmpty()){
						cutoverManagerMapper.savePmValueAfter(pmValueList);
					}else{
						if(!pmValueList_AVG.isEmpty()){
							cutoverManagerMapper.savePmValueAfter(pmValueList_AVG);
						}
					}
				}
			}
		} catch (CommonException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
	}
	
	//处理割接告警相关
	private void handleSnapshot_alarm(List<Map> data,Map emsIdMap,String cutoverTaskId,int snapshotTimeFlag,Date currentTime) throws CommonException{
		try {
			int targetType = (Integer) data.get(0).get("TARGET_TYPE");

			// 该map所有key为网元id，value为网元所属ems的id
			Map neMap = new HashMap();

			List ports = new ArrayList();
			//**************** cutoverTask alarm*************************************
			// 计算相关网元，供查询告警用
			List<Map> portsWithNeInfo = new ArrayList<Map>();
			// targetType=8,设备类型为端口
			if (CommonDefine.TREE.NODE.PTP == targetType) {
				// 将根据这个list中的端口id查询link表，从而查出所有本端端口id及对端端口的id，同时包含了网管信息，网元信息
				// 可能有冗余信息，通过加到map中用containsKey方法去除重复值
				List cutoverPortIds = new ArrayList();
				List equipIds = new ArrayList();
				
				for (int i = 0, len = data.size(); i < len; i++) {
					//cutoverPortIds.add(data.get(i).get("TARGET_ID"));
					Map item = new HashMap();
					item.put("BASE_PTP_ID", data.get(i).get("TARGET_ID"));
					cutoverPortIds.add(item);
					equipIds.add(data.get(i).get("TARGET_ID"));
					// if(!map.containsKey(data.get(i).get("TARGET_ID")))
					// map.put(data.get(i).get("TARGET_ID"), 0);
				}
				ports = cutoverManagerMapper.searchPorts(targetType,
						equipIds);
				portsWithNeInfo = cutoverManagerMapper
						.searchPortsInLink(cutoverPortIds);

			}
			// targetType=99,设备类型为链路
			else if (99 == targetType) {
				List linkIds = new ArrayList();
				for (int i = 0, len = data.size(); i < len; i++) {
					linkIds.add(data.get(i).get("TARGET_ID"));
				}
				portsWithNeInfo = cutoverManagerMapper
						.searchPortsByLink(linkIds);

			}
			// 割接设备为网元，shelf，unit,subunit
			else if (CommonDefine.TREE.NODE.NE == targetType
					|| CommonDefine.TREE.NODE.SHELF == targetType
					|| CommonDefine.TREE.NODE.UNIT == targetType
					|| CommonDefine.TREE.NODE.SUBUNIT == targetType) {
				List equipIds = new ArrayList();
				for (int i = 0, len = data.size(); i < len; i++) {
					equipIds.add(data.get(i).get("TARGET_ID"));
				}
				ports = cutoverManagerMapper.searchPorts(targetType,
						equipIds);
				portsWithNeInfo = cutoverManagerMapper.searchPortsInLink(ports);
			}

			for (int i = 0, len = portsWithNeInfo.size(); i < len; i++) {
				if (!neMap.containsKey(portsWithNeInfo.get(i).get("A_NE_ID"))) {
					if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
							"A_EMS_CONNECTION_ID"))) {
						neMap.put(portsWithNeInfo.get(i).get("A_NE_ID"),
								portsWithNeInfo.get(i).get(
										"A_EMS_CONNECTION_ID"));
					}

				}

				if (!neMap.containsKey(portsWithNeInfo.get(i).get("Z_NE_ID"))) {
					if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
							"Z_EMS_CONNECTION_ID"))) {
						neMap.put(portsWithNeInfo.get(i).get("Z_NE_ID"),
								portsWithNeInfo.get(i).get(
										"Z_EMS_CONNECTION_ID"));
					}

				}

			}
			for (int i = 0, len = ports.size(); i < len; i++) {
				if (!neMap.containsKey(((Map) ports.get(i))
						.get("BASE_NE_ID"))) {
					neMap.put(((Map) ports.get(i)).get("BASE_NE_ID"),
							((Map) ports.get(i)).get("BASE_EMS_CONNECTION_ID"));
				}
			}
			List<Integer> neIdList = new ArrayList<Integer>();
			Iterator ite = neMap.entrySet().iterator();
			while (ite.hasNext()) {
				Map.Entry entry = (Map.Entry) ite.next();
				Integer neId = (Integer) entry.getKey();
				neIdList.add(neId);
			}
			Map<String, Object> alarmsMap = alarmManagementService
					.getCurrentAlarmByNeIdListForCutover(neIdList);
			List alarmList = (List) alarmsMap.get("rows");
			int total = (Integer)alarmsMap.get("total");
			for(int i=0;i<total;i++)
			{
				String roomId = "";
				String stationId = "";
				String emsGroupId = "";
				String emsId = "";
				String neId = "";
				String unitId = "";
				String slotId = "";
				String ptpId = "";
				Map alarm = (Map)alarmList.get(i);
				if(((Map)alarmList.get(i)).get("RESOURCE_ROOM_ID").getClass().getName().contains("String"))
				{
					roomId = (String)alarm.get("RESOURCE_ROOM_ID");
					if(roomId.equals(""))
						alarm.put("RESOURCE_ROOM_ID", null);
				}
				if(((Map)alarmList.get(i)).get("STATION_ID").getClass().getName().contains("String"))
				{
					stationId = (String)alarm.get("STATION_ID");
					if(stationId.equals(""))
						alarm.put("STATION_ID", null);
				}
				if(((Map)alarmList.get(i)).get("BASE_EMS_GROUP_ID").getClass().getName().contains("String"))
				{
					emsGroupId = (String)alarm.get("BASE_EMS_GROUP_ID");
					if(emsGroupId.equals(""))
						alarm.put("BASE_EMS_GROUP_ID", null);
				}
				if(((Map)alarmList.get(i)).get("EMS_ID").getClass().getName().contains("String"))
				{
					emsId = (String)alarm.get("EMS_ID");
					if(emsId.equals(""))
						alarm.put("EMS_ID", null);
				}
				if(((Map)alarmList.get(i)).get("NE_ID").getClass().getName().contains("String"))
				{
					neId = (String)alarm.get("NE_ID");
					if(neId.equals(""))
						alarm.put("NE_ID", null);
				}
				if(((Map)alarmList.get(i)).get("PTP_ID").getClass().getName().contains("String"))
				{
					ptpId = (String)alarm.get("PTP_ID");
					if(ptpId.equals(""))
						alarm.put("PTP_ID", null);
				}
				if(((Map)alarmList.get(i)).get("UNIT_ID").getClass().getName().contains("String"))
				{
					unitId = (String)alarm.get("UNIT_ID");
					if(unitId.equals(""))
						alarm.put("UNIT_ID", null);
				}
			}
			// 先删除原有的割接任务告警信息
			List<Integer> taskIdListAlarm = new ArrayList<Integer>();
			taskIdListAlarm.add(Integer.parseInt(cutoverTaskId));
			Map<String, Object> mapAlarm = new HashMap<String, Object>();
			mapAlarm.put("taskIdList", taskIdListAlarm);
			//3代表删除所有告警
			if(snapshotTimeFlag == 1){
				mapAlarm.put("snapshotTimeFlag",3);
				cutoverManagerMapper.deleteTaskAlarm(mapAlarm);
				
				if(alarmList.size()!=0){
					/*1表示割接前快照*/
					cutoverManagerMapper.saveCutoverAlarms(Integer.valueOf(cutoverTaskId),currentTime,snapshotTimeFlag,alarmList);
				}
					
			}else{
				List<Map> alarmListBefore = cutoverManagerMapper.getCurrentAlarms(
						Integer.valueOf(cutoverTaskId), 2, 0, 999999999);
				//这边要删除之前存的割接前的告警
				// 先删除原有的割接后任务告警信息
				mapAlarm.put("snapshotTimeFlag",1);
				cutoverManagerMapper.deleteTaskAlarm(mapAlarm);
				
				DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				for (int i = alarmListBefore.size() - 1; i >= 0; i--) {
					for (int j = 0, length = alarmList.size(); j < length; j++) {
						if (alarmEqual(alarmListBefore.get(i),
								(Map) alarmList.get(j))) {
							Date snapShotTimeBefore = fmt.parse((String)alarmListBefore.get(i)
									.get("SNAPSHOT_TIME_BEFORE"));
							alarmListBefore.remove(i);
							Map alarm = (Map) alarmList.get(j);
							alarm.put("ALARM_CATEGORY", 1);
							alarm.put("SNAPSHOT_TIME_AFTER", currentTime);
							alarm.put("SNAPSHOT_TIME_BEFORE", snapShotTimeBefore);
							break;
						}
					}
				}
				// 循环完之后，前告警list中还剩的就是消除的，后告警list中没有alarm_category属性的就是新增的
				for (int i = alarmListBefore.size() - 1; i >= 0; i--) {
					Map alarm = (Map) alarmListBefore.get(i);
					alarm.put("ALARM_CATEGORY", 2);
				}
				for (int i = alarmList.size() - 1; i >= 0; i--) {
					Map alarm = (Map) alarmList.get(i);
					
					if (!alarm.containsKey("ALARM_CATEGORY"))
					{
						alarm.put("ALARM_CATEGORY", 3);
						alarm.put("SNAPSHOT_TIME_BEFORE", null);
						alarm.put("SNAPSHOT_TIME_AFTER", currentTime);
					}
						
				}
				List alarmListAll = new ArrayList();
				alarmListAll.addAll(alarmListBefore);
				alarmListAll.addAll(alarmList);
				// 先删除原有的割接后任务告警信息
				mapAlarm.put("snapshotTimeFlag",snapshotTimeFlag);
				cutoverManagerMapper.deleteTaskAlarm(mapAlarm);
				//再保存
				if(alarmListAll.size()!=0){
					/* 2表示割接后快照 */
					cutoverManagerMapper.saveCutoverAlarms(Integer
							.valueOf(cutoverTaskId), null, snapshotTimeFlag,
							alarmListAll);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
	}
	
	//处理割接任务记录相关
	private void handleSnapshot_taskRecord(String cutoverTaskId,int snapshotTimeFlag) throws CommonException{
		
		try{
			// 保存在割接任务参数信息中:割接快照完成
			// 不需要多次保存，保存前把之前的删掉
			List<String> paramNameList = new ArrayList<String>();
			if(snapshotTimeFlag == 1){
				paramNameList.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_BEFORE_FLAG);
			}else{
				paramNameList.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_AFTER_FLAG);
			}
			
			cutoverManagerMapper.deleteSpecifiedParam(Integer
					.parseInt(cutoverTaskId), paramNameList);
			List<Map> taskParamList = new ArrayList<Map>();
			for (int i = 0; i < 1; i++) {
				Map parameterMap = new HashMap();
				parameterMap.put("taskParamId", null);
				parameterMap.put("taskId", Integer.parseInt(cutoverTaskId));
	
				if (0 == i) {
					if(snapshotTimeFlag == 1){
						parameterMap
							.put(
								"paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_BEFORE_FLAG);
						parameterMap
							.put(
								"paramValue",
								CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.SNAPSHOT_BEFORE_YES);
					}else{
						parameterMap
							.put(
								"paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_AFTER_FLAG);
						parameterMap
							.put(
								"paramValue",
								CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.SNAPSHOT_AFTER_YES);
					}
				}
				taskParamList.add(parameterMap);
			}
			cutoverManagerMapper.saveCutoverTaskParameter(taskParamList);
	
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
	}

	//snapshotTimeFlag 1:割接前快照 2:割接后快照
	private void snapshot(String cutoverTaskId, int userId,int snapshotTimeFlag)
			throws CommonException {

		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		long currentDate = cal.getTimeInMillis();
		Date currentTime = new Date(currentDate);
		
		// 该用户拥有权限的ems列表，用于过滤ptp
		List<Map> emsListAvailable = commonManagerService
				.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, true, false);
		Map emsIdMap = new HashMap();
		for (int i = 0, len = emsListAvailable.size(); i < len; i++) {
			emsIdMap.put(emsListAvailable.get(i).get(
					"BASE_EMS_CONNECTION_ID"), 0);
		}
		
		// 查询出割接设备
		List<Map> data = getCutoverEquipList(Integer.valueOf(cutoverTaskId));
		//处理性能相关信息
		handleSnapshot_performance(data,emsIdMap,cutoverTaskId,snapshotTimeFlag,currentTime);
		//处理告警相关信息
		handleSnapshot_alarm(data,emsIdMap,cutoverTaskId,snapshotTimeFlag,currentTime);
		//处理割接任务记录相关
		handleSnapshot_taskRecord(cutoverTaskId,snapshotTimeFlag);

	}
	
	

	/**
	 * 查询相关告警
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public Map getCurrentAlarms(String cutoverTaskId, String alarmType,
			int startNumber, int pageSize) throws CommonException {
		Map map = new HashMap();
		try {

			List<Map> dataList = cutoverManagerMapper.getCurrentAlarms(Integer
					.valueOf(cutoverTaskId), Integer.valueOf(alarmType),
					startNumber, pageSize);
			int total = cutoverManagerMapper.getCurrentAlarmsCount(Integer
					.valueOf(cutoverTaskId), Integer.valueOf(alarmType));
			map.put("rows", dataList);
			map.put("total", total);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}

		return map;
	}

	/**
	 * 判断两个告警是否为同一告警
	 */
	private boolean alarmEqual(Map alarmBefore, Map alarmAfter) {
//		int neIdBefore = (Integer) alarmBefore.get("NE_ID");
//		int slotIdBefore = (Integer) alarmBefore.get("SLOT_NO");
//		int unitIdBefore = (Integer) alarmBefore.get("UNIT_ID");
//		int ptpIdBefore = (Integer) alarmBefore.get("PTP_ID");
		String neNameBefore = (String) alarmBefore.get("NE_NAME");
		String slotNameBefore = (String) alarmBefore.get("SLOT_DISPLAY_NAME");
		String unitNameBefore = (String) alarmBefore.get("UNIT_NAME");
		String ptpNameBefore = (String) alarmBefore.get("PORT_NO");
		String alarmLevelBefore = (String) alarmBefore
				.get("PERCEIVED_SEVERITY");
		String alarmNameBefore = (String) alarmBefore
				.get("NATIVE_PROBABLE_CAUSE");

//		int neIdAfter = (Integer) alarmAfter.get("NE_ID");
//		int slotIdAfter = (Integer) alarmAfter.get("SLOT_ID");
//		int unitIdAfter = (Integer) alarmAfter.get("UNIT_ID");
//		int ptpIdAfter = (Integer) alarmAfter.get("PTP_ID");
		String neNameAfter = (String) alarmAfter.get("NE_NAME");
		String slotNameAfter = (String) alarmAfter.get("SLOT_DISPLAY_NAME");
		String unitNameAfter = (String) alarmAfter.get("UNIT_NAME");
		String ptpNameAfter = (String) alarmAfter.get("PORT_NAME");
		String alarmLevelAfter = ((Integer) alarmAfter.get("PERCEIVED_SEVERITY")).toString();
		String alarmNameAfter = (String) alarmAfter
				.get("NATIVE_PROBABLE_CAUSE");
//		if (neIdBefore == neIdAfter 
////				&& slotIdBefore == slotIdAfter
//				&& unitIdBefore == unitIdAfter && ptpIdBefore == ptpIdAfter
//				&& alarmLevelBefore.equals(alarmLevelAfter)
//				&& alarmNameBefore.equals(alarmNameAfter))
		
		//if(neNameBefore.contains("REM_SF")&&neNameAfter.contains("REM_SF"))
//		{
//			
//			System.out.println(neNameBefore+'%'+neNameAfter);
//			System.out.println(slotNameBefore+'%'+slotNameAfter);
//			System.out.println(unitNameBefore+'%'+unitNameAfter);
//			System.out.println(ptpNameBefore+'%'+ptpNameAfter);
//			System.out.println(alarmLevelBefore+'%'+alarmLevelAfter);
//			System.out.println(alarmNameBefore+'%'+alarmNameAfter);
//			System.out.println(neNameBefore.equals(neNameAfter)
//					&& slotNameBefore.equals(slotNameAfter)
//					&& unitNameBefore.equals(unitNameAfter)
//					&& ptpNameBefore.equals(ptpNameAfter)
//					&& alarmLevelBefore.equals(alarmLevelAfter)
//					&& alarmNameBefore.equals(alarmNameAfter));
//		}
		
		if(neNameBefore.equals(neNameAfter)
				&& slotNameBefore.equals(slotNameAfter)
				&& unitNameBefore.equals(unitNameAfter)
				&& ptpNameBefore.equals(ptpNameAfter)
				&& alarmLevelBefore.equals(alarmLevelAfter)
				&& alarmNameBefore.equals(alarmNameAfter))
			return true;
		return false;
	}

	/**
	 * 割接任务完成，需要1.标示当前时间为割接任务完成时间。 2.释放过滤告警。（如果有过滤告警）
	 * 3.将割接后采集的端口物理量更新为基准值。（如果点击“割接完成“按钮时有割接后快照值） 4.生成割接报告。
	 * 
	 * @param cutOverTaskId
	 *            割接任务id
	 * 
	 * @throws CommonException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public void cutoverComplete(String cutOverTaskId, Integer userId)
			throws CommonException {
		// 查询出割接设备
		List<Map> data = getCutoverEquipList(Integer.valueOf(cutOverTaskId));
		// 由于割接设备只允许选择同等级的设备，第一条的设备类型就是该任务所有割接设备的设备类型
		int targetType = (Integer) data.get(0).get("TARGET_TYPE");
		// 该map所有key为网元id，value为网元所属ems的id
		Map neMap = new HashMap();
		// 该用户拥有权限的ems列表，用于过滤ptp
		List<Map> emsListAvailable = commonManagerService
				.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, true, false);
		Map emsIdMap = new HashMap();
		for (int i = 0, len = emsListAvailable.size(); i < len; i++) {
			emsIdMap.put(emsListAvailable.get(i).get("BASE_EMS_CONNECTION_ID"),
					0);
		}
		List<Map> portsWithNeInfo = new ArrayList<Map>();
		// targetType=8,设备类型为端口
		if (CommonDefine.TREE.NODE.PTP == targetType) {
			// 将根据这个list中的端口id查询link表，从而查出所有本端端口id及对端端口的id，同时包含了网管信息，网元信息
			// 可能有冗余信息，通过加到map中用containsKey方法去除重复值
			List cutoverPortIds = new ArrayList();
			for (int i = 0, len = data.size(); i < len; i++) {
				//cutoverPortIds.add(data.get(i).get("TARGET_ID"));
				Map item = new HashMap();
				item.put("BASE_PTP_ID", data.get(i).get("TARGET_ID"));
				cutoverPortIds.add(item);
				// if(!map.containsKey(data.get(i).get("TARGET_ID")))
				// map.put(data.get(i).get("TARGET_ID"), 0);
			}
			portsWithNeInfo = cutoverManagerMapper
					.searchPortsInLink(cutoverPortIds);

		}
		// targetType=99,设备类型为链路
		else if (99 == targetType) {
			List linkIds = new ArrayList();
			for (int i = 0, len = data.size(); i < len; i++) {
				linkIds.add(data.get(i).get("TARGET_ID"));
			}
			portsWithNeInfo = cutoverManagerMapper.searchPortsByLink(linkIds);

		}
		// 割接设备为网元，shelf，unit,subunit
		else if (CommonDefine.TREE.NODE.NE == targetType
				|| CommonDefine.TREE.NODE.SHELF == targetType
				|| CommonDefine.TREE.NODE.UNIT == targetType
				|| CommonDefine.TREE.NODE.SUBUNIT == targetType) {
			List equipIds = new ArrayList();
			for (int i = 0, len = data.size(); i < len; i++) {
				equipIds.add(data.get(i).get("TARGET_ID"));
			}
			List ports = cutoverManagerMapper.searchPorts(targetType, equipIds);
			portsWithNeInfo = cutoverManagerMapper.searchPortsInLink(ports);
		}

		for (int i = 0, len = portsWithNeInfo.size(); i < len; i++) {
			if (!neMap.containsKey(portsWithNeInfo.get(i).get("A_NE_ID"))) {
				if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
						"A_EMS_CONNECTION_ID"))) {
					neMap.put(portsWithNeInfo.get(i).get("A_NE_ID"),
							portsWithNeInfo.get(i).get("A_EMS_CONNECTION_ID"));
				}

			}

			if (!neMap.containsKey(portsWithNeInfo.get(i).get("Z_NE_ID"))) {
				if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
						"Z_EMS_CONNECTION_ID"))) {
					neMap.put(portsWithNeInfo.get(i).get("Z_NE_ID"),
							portsWithNeInfo.get(i).get("Z_EMS_CONNECTION_ID"));
				}

			}

		}

		try {
			//增加步骤：1.如果没有进行割接后快照，先进行割接后快照
			//        2.割接后评估,即把动态评分添加到参数中去
			//        3.读取是否自动更新基准值参数，并检查是否有非零差值，从而判断是否需要自动更新基准值
			List<Map> parameters = cutoverManagerMapper
			.getCutoverTaskParameter(Integer.parseInt(cutOverTaskId));
			Iterator iterator = parameters.iterator();
			int snapshotAfterFlag = 0;
			int autoUpdateCompareValue = 0;
			if (parameters.size() != 0) {
				while (iterator.hasNext()) {
					Map parameter = (Map) iterator.next();
					if (parameter
							.get("PARAM_NAME")
							.equals(
									CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_AFTER_FLAG)
							&& Integer.valueOf((String) parameter
									.get("PARAM_VALUE")) == CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.SNAPSHOT_AFTER_YES) {
						snapshotAfterFlag = 1;
//						break;
					}
					else if(parameter
							.get("PARAM_NAME")
							.equals("autoUpdateCompareValue"))
					{
						autoUpdateCompareValue = Integer.valueOf((String) parameter
								.get("PARAM_VALUE"));
					}
		
				}
			}
			if(0==snapshotAfterFlag)
			{
				snapshotAfter(cutOverTaskId,userId);
			}
			//在这里就计算每条性能数据的评分，并保存
			List<Map> pmList = cutoverManagerMapper.searchPmValue(Integer.valueOf(cutOverTaskId),-1,-1);
			Map<String, Object> config = getEvaluationConfig();
			Integer pmAlarm1 =0;
			Integer pmAlarm2 =0;
			Integer pmAlarm3 =0;
			Integer pmImprove =0;
			Integer pmWorse =0;
		
			Integer event =0;
			Double SDHDifferent = 0d;
			Double WDMDifferent = 0d;
			SDHDifferent = Double.valueOf((String)config.get("SDHDifferent"));
			WDMDifferent = Double.valueOf((String)config.get("WDMDifferent"));
			for(int i=0,len=((List)config.get("rows")).size();i<len;i++)
			{
				Map map = (Map)((List)config.get("rows")).get(i);
				String key = (String)map.get("key");
				String value = (String)map.get("value");
				if(key.equals("一个性能项重要预警"))
					pmAlarm3 = Integer.valueOf(value);
				else if(key.equals("一个性能项次要预警"))
					pmAlarm2 = Integer.valueOf(value);
				else if(key.equals("一个性能项一般预警"))
					pmAlarm1 = Integer.valueOf(value);
				else if(key.equals("一个性能差值改善判定"))
					pmImprove = Integer.valueOf(value);
				else if(key.equals("一个性能差值劣化判定"))
					pmWorse = Integer.valueOf(value);
				
				else if(key.equals("一个割接期未恢复的倒换事件"))
					event = Integer.valueOf(value);
			}
			for(int i=0,len=pmList.size();i<len;i++)
			{
				Map pm = pmList.get(i);
				// pm值检查，避免空值出错
				if (pm.get("EXCEPTION_LV_AFTER") == null ||
						pm.get("DOMAIN") == null ||
						pm.get("VALUE_BEFORE") == null ||
						pm.get("VALUE_AFTER") == null) {
					continue;
				}
				
				if((Integer)pm.get("EXCEPTION_LV_AFTER")==3)
				{
					pm.put("evaluationScore", pmAlarm3);
				}
				else if((Integer)pm.get("EXCEPTION_LV_AFTER")==2)
				{
					pm.put("evaluationScore", pmAlarm2);
				}
				else if((Integer)pm.get("EXCEPTION_LV_AFTER")==1)
				{
					pm.put("evaluationScore", pmAlarm1);
				}
				else if((Integer)pm.get("EXCEPTION_LV_AFTER")==0)
				{
					//SDH性能
					if((Integer)pm.get("DOMAIN")==1)
					{
						if(pm.get("VALUE_BEFORE")!=null && (String)pm.get("VALUE_BEFORE")!="")
						{
							BigDecimal valueBefore = new BigDecimal((String)pm.get("VALUE_BEFORE"));
							BigDecimal valueAfter = new BigDecimal((String)pm.get("VALUE_AFTER"));
							BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
							Double differenceDoble = difference.doubleValue();
							if(differenceDoble-SDHDifferent>0)
								pm.put("evaluationScore", pmImprove);
							else if(differenceDoble+SDHDifferent<0)
								pm.put("evaluationScore", pmWorse);
							else
								pm.put("evaluationScore", 0);
						}
						
					}
					//WDM性能
					else if((Integer)pm.get("DOMAIN")==2)
					{
						if(pm.get("VALUE_BEFORE")!=null && (String)pm.get("VALUE_BEFORE")!="")
						{
							BigDecimal valueBefore = new BigDecimal((String)pm.get("VALUE_BEFORE"));
							BigDecimal valueAfter = new BigDecimal((String)pm.get("VALUE_AFTER"));
							BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
							Double differenceDoble = difference.doubleValue();
							if(differenceDoble-WDMDifferent>0)
								pm.put("evaluationScore", pmImprove);
							else if(differenceDoble+WDMDifferent<0)
								pm.put("evaluationScore", pmWorse);
							else
								pm.put("evaluationScore", 0);
						}
					}
				}
			}
			//设置性能条目的评分
			if(pmList.size()!=0)
				cutoverManagerMapper.setPMEvaluationScore(pmList);
			Integer score = evaluate(cutOverTaskId);
			Boolean needUpdateCompareValue = needUpdateCompareValue(cutOverTaskId);
			String taskStatus = "";
			if(!needUpdateCompareValue)
			{
				taskStatus ="3";
			}
			else
			{
				if(0==autoUpdateCompareValue)
					taskStatus ="7";//基准值需要更新
				else
				{
					List<Map> pmValueList = cutoverManagerMapper.searchPmValue(
							Integer.valueOf(cutOverTaskId), 0, -1);
					for(int i=pmValueList.size()-1;i>=0;i--)
					{
						Map oneRow = new HashMap();
						int level = CommonDefine.PM.MUL.SEC_PM_ZC;
						oneRow = (Map)pmValueList.get(i);
						oneRow.put("PM_VALUE", oneRow.get("VALUE_AFTER"));
						if(oneRow.get("VALUE_BEFORE")!=null && (String)oneRow.get("VALUE_BEFORE")!="")
						{
							BigDecimal valueBefore = new BigDecimal((String)oneRow.get("VALUE_BEFORE"));
							BigDecimal valueAfter = new BigDecimal((String)oneRow.get("VALUE_AFTER"));
							BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
							Double differenceDoble = difference.doubleValue();
							oneRow.put("DIFFERENCE", difference);
							
							if(differenceDoble!=0)
							{
							}
							else
								pmValueList.remove(oneRow);
						}
						
					}
					performanceManagerService.setCompareValueFromPm(pmValueList);
					taskStatus ="8";//基准值已经更新
				}
			}
			// 1） 标示当前时间为割接任务完成时间。
			// 不需要多次保存，保存前把之前的删掉
			List<String> paramNameList = new ArrayList<String>();
			paramNameList
					.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.END_TIME_ACTUAL);
			paramNameList
					.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.COMPLETE_CUTOVER_TASK_FLAG);
			paramNameList
			.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.STATUS);
			paramNameList.add("evaluationScore");
			cutoverManagerMapper.deleteSpecifiedParam(Integer
					.parseInt(cutOverTaskId), paramNameList);
			// 获取当前时间
			Calendar cal = Calendar.getInstance();
			long currentDate = cal.getTimeInMillis();
			Date currentTime = new Date(currentDate);
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 保存在割接任务参数信息中
			List<Map> taskParamList = new ArrayList<Map>();
			for (int i = 0; i < 4; i++) {
				Map parameterMap = new HashMap();
				parameterMap.put("taskParamId", null);
				parameterMap.put("taskId", Integer.parseInt(cutOverTaskId));
				if (0 == i) {
					parameterMap
							.put(
									"paramName",
									CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.END_TIME_ACTUAL);
					parameterMap.put("paramValue", timeFormat
							.format(currentTime));
				} else if (1 == i) {
					parameterMap
							.put(
									"paramName",
									CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.COMPLETE_CUTOVER_TASK_FLAG);
					parameterMap
							.put(
									"paramValue",
									CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.COMPLETE_CUTOVER_TASK_YES);
				} else if (2 == i) {
					parameterMap.put("paramName",
							CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.STATUS);
					parameterMap.put("paramValue", taskStatus);
				} else if (3 == i) {
					parameterMap.put("paramName",
							"evaluationScore");
					parameterMap.put("paramValue", score);
				}

				taskParamList.add(parameterMap);
			}
			cutoverManagerMapper.saveCutoverTaskParameter(taskParamList);
			// 2） 释放过滤告警。（如果有过滤告警）
			// //所谓过滤告警，现在只是需要设置一个任务实际结束时间，在第一步中已经完成
			//20150918 告警过滤器应该不会到达结束时间的时候自动挂起，所以在任务完成的时候执行一下挂起任务
			//查出割接任务名称，来获取对应的告警过滤器名称
			Map cutoverTaskIdMap = new HashMap();
			//表名
			cutoverTaskIdMap.put("NAME", "T_SYS_TASK");
			//查询字段名
			cutoverTaskIdMap.put("VALUE", "TASK_NAME");
			//条件字段名
			cutoverTaskIdMap.put("ID_NAME", "SYS_TASK_ID");
			//条件字段值
			cutoverTaskIdMap.put("ID_VALUE", cutOverTaskId);
			List<Map> cutoverTaskList = cutoverManagerMapper.getByParameter(cutoverTaskIdMap);
			String cutoverTaskNameString = ((String) cutoverTaskList.get(0).get("TASK_NAME")).trim();
			String filterNameString = cutoverTaskNameString+"_告警过滤器";
			Map userIdMap = new HashedMap();
			userIdMap.put("sysUserId", userId);
			userIdMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_YES);
			List<Map<String, Object>>  filterList = alarmManagementMapper.getAlarmFilterSummaryByUserId(userIdMap);
			for (Map<String, Object> map : filterList) {
				//名称一致，则把这一条filter挂起
				if(map.get("FILTER_NAME").equals(filterNameString)){
					Map taskIdMap = new HashMap();
					taskIdMap.put("filterId", map.get("FILTER_ID"));
					alarmManagementMapper.updateAlarmFilterPending(taskIdMap);
					break;
				}
			}
			// 3） 将割接后采集的端口物理量更新为基准值。（如果点击“割接完成“按钮时有割接后快照值）
			//这一步已经放到前面完成
//			List<Map> parameters = cutoverManagerMapper
//					.getCutoverTaskParameter(Integer.parseInt(cutOverTaskId));
//			Iterator iterator = parameters.iterator();
//			// 是否将割接后采集的端口物理量更新为基准值的标记，默认为0，如果点击“割接完成“按钮时有割接后快照值为1
//			int setValueFlag = 0;
//			if (parameters.size() != 0) {
//				while (iterator.hasNext()) {
//					Map parameter = (Map) iterator.next();
//					if (parameter
//							.get("PARAM_NAME")
//							.equals(
//									CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_AFTER_FLAG)
//							&& Integer.valueOf((String) parameter
//									.get("PARAM_VALUE")) == CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.SNAPSHOT_AFTER_YES) {
//						setValueFlag = 1;
//						break;
//					}
//
//				}
//				if (1 == setValueFlag) {
//					// 更新基准值
//					List<Map> pmList = cutoverManagerMapper.searchPmValue(
//							Integer.valueOf(cutOverTaskId), 0, -1);
//					performanceManagerService.setCompareValueFromPm(pmList);
//				}
//			}
			// 4） 生成割接报告。
			generateReport(Integer.valueOf(cutOverTaskId), userId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CommonException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 过滤告警
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public void filterAlarm(String cutoverTaskId, int userId)
			throws CommonException {

		// // 查询出割接设备
		// List<Map> data = getCutoverEquipList(Integer.valueOf(cutoverTaskId));
		// // 由于割接设备只允许选择同等级的设备，第一条的设备类型就是该任务所有割接设备的设备类型
		// int targetType = (Integer) data.get(0).get("TARGET_TYPE");
		// // 该map所有key为网元id，value为网元所属ems的id
		// Map neMap = new HashMap();
		// // 该用户拥有权限的ems列表，用于过滤ptp
		// List<Map> emsListAvailable =
		// commonManagerService.getAllEmsByEmsGroupId(userId,
		// CommonDefine.VALUE_ALL, true);
		// Map emsIdMap = new HashMap();
		// for (int i = 0, len = emsListAvailable.size(); i < len; i++) {
		// emsIdMap.put(emsListAvailable.get(i).get("BASE_EMS_CONNECTION_ID"),
		// 0);
		// }
		// List<Map> portsWithNeInfo = new ArrayList<Map>();
		// // targetType=8,设备类型为端口
		// if (CommonDefine.TREE.NODE.PTP == targetType) {
		// // 将根据这个list中的端口id查询link表，从而查出所有本端端口id及对端端口的id，同时包含了网管信息，网元信息
		// //可能有冗余信息，通过加到map中用containsKey方法去除重复值
		// List cutoverPortIds = new ArrayList();
		// for (int i = 0, len = data.size(); i < len; i++) {
		// cutoverPortIds.add(data.get(i).get("TARGET_ID"));
		// // if(!map.containsKey(data.get(i).get("TARGET_ID")))
		// // map.put(data.get(i).get("TARGET_ID"), 0);
		// }
		// portsWithNeInfo = cutoverManagerMapper
		// .searchPortsInLink(cutoverPortIds);
		//
		// }
		// // targetType=99,设备类型为链路
		// else if (99 == targetType) {
		// List linkIds = new ArrayList();
		// for (int i = 0, len = data.size(); i < len; i++) {
		// linkIds.add(data.get(i).get("TARGET_ID"));
		// }
		// portsWithNeInfo = cutoverManagerMapper.searchPortsByLink(linkIds);
		//
		// }
		// // 割接设备为网元，shelf，unit,subunit
		// else if (CommonDefine.TREE.NODE.NE == targetType
		// || CommonDefine.TREE.NODE.SHELF == targetType
		// || CommonDefine.TREE.NODE.UNIT == targetType
		// || CommonDefine.TREE.NODE.SUBUNIT == targetType) {
		// List equipIds = new ArrayList();
		// for (int i = 0, len = data.size(); i < len; i++) {
		// equipIds.add(data.get(i).get("TARGET_ID"));
		// }
		// List ports = cutoverManagerMapper.searchPorts(targetType, equipIds);
		// portsWithNeInfo = cutoverManagerMapper.searchPortsInLink(ports);
		// }
		//		
		// for (int i = 0, len = portsWithNeInfo.size(); i < len; i++) {
		// if (!neMap.containsKey(portsWithNeInfo.get(i).get("A_NE_ID"))) {
		// if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
		// "A_EMS_CONNECTION_ID"))) {
		// neMap.put(portsWithNeInfo.get(i).get("A_NE_ID"),
		// portsWithNeInfo.get(i).get("A_EMS_CONNECTION_ID"));
		// }
		//
		// }
		//
		// if (!neMap.containsKey(portsWithNeInfo.get(i).get("Z_NE_ID"))) {
		// if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
		// "Z_EMS_CONNECTION_ID"))) {
		// neMap.put(portsWithNeInfo.get(i).get("Z_NE_ID"),
		// portsWithNeInfo.get(i).get("Z_EMS_CONNECTION_ID"));
		// }
		//
		// }
		//
		// }
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 所谓过滤告警，现在只是需要设置一个任务实际开始时间
		// 不需要多次保存，保存前把之前的删掉
		List<String> paramNameList = new ArrayList<String>();
		paramNameList
				.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.FILTER_ALARM_FLAG);
		paramNameList
				.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.START_TIME_ACTUAL);
		paramNameList
		.add(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.STATUS);
		cutoverManagerMapper.deleteSpecifiedParam(Integer
				.parseInt(cutoverTaskId), paramNameList);
		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		long currentDate = cal.getTimeInMillis();
		Date currentTime = new Date(currentDate);
		// 保存在割接任务参数信息中:过滤告警完成
		List<Map> taskParamList = new ArrayList<Map>();
		for (int i = 0; i < 3; i++) {
			Map parameterMap = new HashMap();
			parameterMap.put("taskParamId", null);
			parameterMap.put("taskId", Integer.parseInt(cutoverTaskId));

			if (0 == i) {
				parameterMap
						.put(
								"paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.FILTER_ALARM_FLAG);
				parameterMap
						.put(
								"paramValue",
								CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.FILTER_ALARM_YES);
			}
			if (1 == i) {
				parameterMap
						.put(
								"paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.START_TIME_ACTUAL);
				parameterMap.put("paramValue", timeFormat.format(currentTime));
			}
			if (2 == i) {
				parameterMap
						.put(
								"paramName",
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.STATUS);
				parameterMap.put("paramValue", "2");
			}
			taskParamList.add(parameterMap);
		}
		cutoverManagerMapper.saveCutoverTaskParameter(taskParamList);
	}

	/**
	 * 初始化割接任务进度状态
	 * 
	 * @param cutoverTaskId
	 * @return
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public Map initCutoverTaskProcess(String cutoverTaskId)
			throws CommonException {
		Map map = new HashMap();
		try {
			Integer id = Integer.parseInt(cutoverTaskId);
			List<Map> parameters = new ArrayList<Map>();
			parameters = cutoverManagerMapper.getCutoverTaskParameter(id);
			Iterator iterator = parameters.iterator();
			while (iterator.hasNext()) {
				Map parameter = (Map) iterator.next();
				if (parameter
						.get("PARAM_NAME")
						.equals(
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.FILTER_ALARM_FLAG))
					map.put("filterAlarm", parameter.get("PARAM_VALUE"));
				if (parameter
						.get("PARAM_NAME")
						.equals(
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_BEFORE_FLAG))
					map.put("snapshotBefore", parameter.get("PARAM_VALUE"));
				if (parameter
						.get("PARAM_NAME")
						.equals(
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_AFTER_FLAG))
					map.put("snapshotAfter", parameter.get("PARAM_VALUE"));
				if (parameter
						.get("PARAM_NAME")
						.equals(
								CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.COMPLETE_CUTOVER_TASK_FLAG))
					map
							.put("completeCutoverTask", parameter
									.get("PARAM_VALUE"));
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return map;
	}

	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public String downloadPmResult(String cutoverTaskId) throws CommonException {
		String resultMessage = "";
		String path =CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		String fileName = "割接性能_" + cutoverTaskId;
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",
				1000);
		List dataList = cutoverManagerMapper.searchPmValue(Integer
				.valueOf(cutoverTaskId), 0, -1);
		try {
			// 导出数据
			resultMessage = ex2.writeExcel(dataList,
					CommonDefine.EXCEL.CUTOVER_PERFORMANCE_HEADER, false);

		} catch (Exception e) {
			e.printStackTrace();
			ex2.close();
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return resultMessage;
	}

	/**
	 * 查询所有割接任务对应的过滤告警时间，释放告警时间，网元id列表
	 * 
	 * @return 返回的list中每一条数据都包含： startTime 过滤告警时间 endTime 释放告警时间 neIdList 网元id列表
	 * @throws CommonException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public List<FilterAlarmParametersModel> getFilterAlarmParameters(
			Integer neId) throws CommonException {

		List<Map> cutoverTimes = cutoverManagerMapper
				.getFilterAlarmParameters();
		List<FilterAlarmParametersModel> returnList = new ArrayList<FilterAlarmParametersModel>();
		for (int i = 0, len = cutoverTimes.size(); i < len; i++) {
			List<Integer> neIdList = new ArrayList<Integer>();
			int userId = (Integer) cutoverTimes.get(i).get("CREATE_PERSON");
			FilterAlarmParametersModel filterAlarmParametersModel = new FilterAlarmParametersModel();
			filterAlarmParametersModel.setCutoverTaskId((Integer) cutoverTimes
					.get(i).get("SYS_TASK_ID"));
			String startTime = "";
			String endTime = "";
			startTime = (cutoverTimes.get(i).get("startTimeActual") != null)
					&& !((String) cutoverTimes.get(i).get("startTimeActual"))
							.isEmpty() ? (String) cutoverTimes.get(i).get(
					"startTimeActual") : (String) cutoverTimes.get(i).get(
					"startTime");
			filterAlarmParametersModel.setStartTime(startTime);
			endTime = (cutoverTimes.get(i).get("endTimeActual") != null)
					&& !((String) cutoverTimes.get(i).get("endTimeActual"))
							.isEmpty() ? ((String) cutoverTimes.get(i).get(
					"endTimeActual")) : (String) cutoverTimes.get(i).get(
					"endTime");
			filterAlarmParametersModel.setEndTime(endTime);

			// 查询出割接设备
			List<Map> data = getCutoverEquipList((Integer) cutoverTimes.get(i)
					.get("SYS_TASK_ID"));
			// 由于割接设备只允许选择同等级的设备，第一条的设备类型就是该任务所有割接设备的设备类型
			int targetType = (Integer) data.get(0).get("TARGET_TYPE");
			// 该map所有key为网元id，value为网元所属ems的id
			Map neMap = new HashMap();
			List ports = new ArrayList();
			// 该用户拥有权限的ems列表，用于过滤ptp
			List<Map> emsListAvailable = commonManagerService
					.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, true, false);
			Map emsIdMap = new HashMap();
			for (int j = 0, length = emsListAvailable.size(); j < length; j++) {
				emsIdMap.put(emsListAvailable.get(j).get(
						"BASE_EMS_CONNECTION_ID"), 0);
			}
			List<Map> portsWithNeInfo = new ArrayList<Map>();
			// targetType=8,设备类型为端口
			if (CommonDefine.TREE.NODE.PTP == targetType) {
				// 将根据这个list中的端口id查询link表，从而查出所有本端端口id及对端端口的id，同时包含了网管信息，网元信息
				// 可能有冗余信息，通过加到map中用containsKey方法去除重复值
				List cutoverPortIds = new ArrayList();
				for (int k = 0, size = data.size(); k < size; k++) {
					cutoverPortIds.add(data.get(k).get("TARGET_ID"));
					// if(!map.containsKey(data.get(i).get("TARGET_ID")))
					// map.put(data.get(i).get("TARGET_ID"), 0);
				}
				portsWithNeInfo = cutoverManagerMapper
						.searchPortsInLink(cutoverPortIds);

			}
			// targetType=99,设备类型为链路
			else if (99 == targetType) {
				List linkIds = new ArrayList();
				for (int k = 0, size = data.size(); k < size; k++) {
					linkIds.add(data.get(k).get("TARGET_ID"));
				}
				portsWithNeInfo = cutoverManagerMapper
						.searchPortsByLink(linkIds);

			}
			// 割接设备为网元，shelf，unit,subunit
			else if (CommonDefine.TREE.NODE.NE == targetType
					|| CommonDefine.TREE.NODE.SHELF == targetType
					|| CommonDefine.TREE.NODE.UNIT == targetType
					|| CommonDefine.TREE.NODE.SUBUNIT == targetType) {
				List equipIds = new ArrayList();
				for (int k = 0, size = data.size(); k < size; k++) {
					equipIds.add(data.get(k).get("TARGET_ID"));
				}
				ports = cutoverManagerMapper.searchPorts(targetType,
						equipIds);
				portsWithNeInfo = cutoverManagerMapper.searchPortsInLink(ports);
			}

			for (int k = 0, size = portsWithNeInfo.size(); k < size; k++) {
				if (!neMap.containsKey(portsWithNeInfo.get(k).get("A_NE_ID"))) {
					if (emsIdMap.containsKey(portsWithNeInfo.get(k).get(
							"A_EMS_CONNECTION_ID"))) {
						neMap.put(portsWithNeInfo.get(k).get("A_NE_ID"),
								portsWithNeInfo.get(k).get(
										"A_EMS_CONNECTION_ID"));
					}

				}

				if (!neMap.containsKey(portsWithNeInfo.get(k).get("Z_NE_ID"))) {
					if (emsIdMap.containsKey(portsWithNeInfo.get(k).get(
							"Z_EMS_CONNECTION_ID"))) {
						neMap.put(portsWithNeInfo.get(k).get("Z_NE_ID"),
								portsWithNeInfo.get(k).get(
										"Z_EMS_CONNECTION_ID"));
					}

				}

			}
			for (int j = 0, length = ports.size(); j < length; j++) {
				if (!neMap.containsKey(((Map) ports.get(j))
						.get("BASE_NE_ID"))) {
					neMap.put(((Map) ports.get(j)).get("BASE_NE_ID"),
							((Map) ports.get(j)).get("BASE_EMS_CONNECTION_ID"));
				}
			}
			Iterator iter = neMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object neObj = entry.getKey();
				if (neId == (Integer) (neObj))
					neIdList.add((Integer) (neObj));
			}
			if (neIdList.size() != 0) {
				filterAlarmParametersModel.setNeIdList(neIdList);
				returnList.add(filterAlarmParametersModel);
			}

		}
		return returnList;
	}
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public void generateReport(int cutoverTaskId, int userId) {

		// String filePath = "C:\\Documents and Settings\\Duck2011\\My
		// Documents\\dumps\\EETI\\Book1.xls";

		try {

			/************************************ 综述表生成 *********************************************/
			String reportName = "";
			String reportTime = "";

			String taskName = "";
			String createUser = "";
			String taskNote = "";
			String planStart = "";
			String planEnd = "";
			String startTime = "";
			String endTime = "";
			String planStartCopy = "";
			String planEndCopy = "";
			String startTimeCopy = "";
			String endTimeCopy = "";
			Integer score = 0;
			String evaluation = "";
			String autoUpdateCompareValue = "";
			String cutoverSetAlarm = "";
			String cutoverSetSnapshot = "";

			// 读取割接任务信息
			List<Map> cutoverTaskList = new ArrayList<Map>();
			cutoverTaskList = cutoverManagerMapper.getCutoverTaskList(cutoverTaskId);

			SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			if (cutoverTaskList.size() != 0) {
				Map task = cutoverTaskList.get(0);
				taskName = String.valueOf(task.get("TASK_NAME"));
				reportName = taskName + "报告";
				createUser = String.valueOf(task.get("USER_NAME"));
				taskNote = String.valueOf(task.get("TASK_DESCRIPTION"));
				//planStart = String.valueOf(task.get("START_TIME"));
				planStartCopy = String.valueOf(task.get("START_TIME"));
				planStart = date.format(date.parse(String.valueOf(task.get("START_TIME"))));
				//planEnd = String.valueOf(task.get("END_TIME"));
				planEndCopy = String.valueOf(task.get("END_TIME"));
				planEnd = date.format(date.parse(String.valueOf(task.get("END_TIME"))));
			}

			// 读取割接任务Param信息
			List<Map> taskParamList = new ArrayList<Map>();
			taskParamList = cutoverManagerMapper.getCutoverTaskParamList(cutoverTaskId);

			
			if (taskParamList.size() != 0) {
				for (int i = 0; i < taskParamList.size(); i++) {
					Map taskParam = taskParamList.get(i);
					String key = String.valueOf(taskParam.get("PARAM_NAME"));
					String value = String.valueOf(taskParam.get("PARAM_VALUE"));
					
					if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.START_TIME_ACTUAL.equals(key)) 
					{
						//startTime = date.format(taskParam.get("PARAM_VALUE"));
						startTimeCopy = String.valueOf(taskParam.get("PARAM_VALUE"));
						startTime = date.format(date.parse(String.valueOf(taskParam.get("PARAM_VALUE"))));
						String[] timeList = value.substring(0, 10).split("-");
						reportTime = timeList[0] + "年" + timeList[1] + "月"
								+ timeList[2] + "日";
					} else if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.END_TIME_ACTUAL.equals(key)) 
					{
						//endTime = date.format(taskParam.get("PARAM_VALUE"));
						endTimeCopy = String.valueOf(taskParam.get("PARAM_VALUE"));
						endTime = date.format(date.parse(String.valueOf(taskParam.get("PARAM_VALUE"))));
						
					} else if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.FILTER_ALARM.equals(key))
					{
						int alarmValue = Integer.valueOf(value).intValue();
						if (CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.FILTER_ALARM_YES == alarmValue) {
							cutoverSetAlarm = "割接期间对综合接口自动过滤告警。";
						} else {
							cutoverSetAlarm = "";
						}

					} else if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT
							.equals(key)) {
						if(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_NO
								.equals(value)){
							cutoverSetSnapshot = "不自动采集性能值和告警快照。";
						}else if(CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.SNAPSHOT_IMMEDIATELY
								.equals(value)){
							cutoverSetSnapshot = "立即采集性能值和告警快照。";
						}else{
							cutoverSetSnapshot = "割接前" + value + "小时自动采集性能值和告警快照。";
						}
					} else if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.AUTO_UPDATE_COMPARE_VALUE
							.equals(key)) {
						if(CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.AUTO_UPDATE_YES == Integer.valueOf(value)
								){
							autoUpdateCompareValue = "割接完成后自动更新基准值。";
						}else if(CommonDefine.CUTOVER.CUTOVER_PARAM_VALUE.AUTO_UPDATE_NO == Integer.valueOf(value)){
							autoUpdateCompareValue = "割接完成后手动更新基准值。";
						}
					}else if ("evaluationScore".equals(key))
					{
						Map<String, Object> config = getEvaluationConfig();
						Integer perfectUpperBound = Integer.valueOf((String)config.get("perfectUpperBound"));
						Integer excellentLowerBound = Integer.valueOf((String)config.get("excellentLowerBound"));
						Integer excellentUpperBound = Integer.valueOf((String)config.get("excellentUpperBound"));
						Integer goodLowerBound = Integer.valueOf((String)config.get("goodLowerBound"));
						Integer goodUpperBound = Integer.valueOf((String)config.get("goodUpperBound"));
						Integer averageLowerBound = Integer.valueOf((String)config.get("averageLowerBound"));
						Integer averageUpperBound = Integer.valueOf((String)config.get("averageUpperBound"));
						Integer badLowerBounder = Integer.valueOf((String)config.get("badLowerBounder"));
						
						score = Integer.valueOf(value);
						if(score<=perfectUpperBound)
							evaluation = "完美";
						else if(score>=excellentLowerBound && score<=excellentUpperBound)
							evaluation = "优秀";
						else if(score>=goodLowerBound && score<=goodUpperBound)
							evaluation = "良好";
						else if(score>=averageLowerBound && score<=averageUpperBound)
							evaluation = "一般";
						else if(score>=badLowerBounder)
							evaluation = "差劲";
						
					}
				}
			}

			if (startTime.equals("")) {
				startTime = planStart;
			}
			SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
			// String fileName = reportName +"_"+ time.format(startTime);

			Date start = date.parse(startTime);
			String fileName = reportName + "_" + time.format(start);
			System.out.println("割接报告名称时间："+time.format(start));
			String filePath = CommonDefine.PATH_ROOT
			+ CommonDefine.EXCEL.REPORT_DIR + "/"
			+ CommonDefine.EXCEL.CUTOVER_BASE;
			File file =new File(filePath);  
			//如果文件夹不存在则创建  
			if  (!file.exists()  && !file.isDirectory())    
			{     
			    file.mkdirs();
			} 
			String fileFullName = filePath+ "/" + fileName + ".xlsx";
			/*// 生成割接报告
			WritableWorkbook workbook = Workbook.createWorkbook(new File(
					filePath));
			// 综述表
			WritableSheet sheetOne = workbook.createSheet("综述表", 0);
			// 正文内容格式
			WritableCellFormat format = new WritableCellFormat();
			format.setAlignment(Alignment.LEFT);
			// 正文标题格式
			WritableCellFormat formatTitle = new WritableCellFormat();
			formatTitle.setAlignment(Alignment.LEFT);
			formatTitle.setBackground(Colour.GRAY_25);
			Label cell = new Label(0, 0, "测试文件", format);*/

			//生成07版Excel
			Workbook book = new SXSSFWorkbook(8); //缓存128M内存。
			//综述表
			Sheet sheetOne = book.createSheet();
			book.setSheetName(0,"综述表");
			//正文内容格式
			CellStyle contentStyle = book.createCellStyle();
			Font contentFont =  book.createFont();
			contentFont.setFontHeightInPoints((short)10);
			contentFont.setColor(HSSFColor.BLACK.index);
			contentFont.setFontName("微软雅黑");
			
			contentStyle.setFont(contentFont);
			contentStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			//正文标题格式
			CellStyle titleStyle = book.createCellStyle();
			Font titleFont =  book.createFont();
			titleFont.setColor(HSSFColor.BLACK.index);
			titleFont.setFontName("微软雅黑");
			
			titleStyle.setFont(titleFont);
			titleStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			//titleStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			titleStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
			titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			//正文标题格式
			CellStyle subTitleStyle = book.createCellStyle();			
			subTitleStyle.setFont(titleFont);
			subTitleStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			//titleStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			subTitleStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
			subTitleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			// 设定行高、列距
			sheetOne.setDefaultColumnWidth((short)3);
			sheetOne.setDefaultRowHeight((short)300);
			sheetOne.setColumnWidth(0, 600);
			
			
			/*// 设定行高、列距
			for (int i = 0; i <= 30; i++) {
				sheetOne.setColumnView(i, 5);
				sheetOne.setRowView(i, 300);
			}
			sheetOne.setColumnView(0, 3);*/

			/*// 用于报告标题
			jxl.write.WritableFont titleFont = new jxl.write.WritableFont(
					WritableFont.createFont("宋体"), 13, WritableFont.BOLD);
			jxl.write.WritableCellFormat titleFormat = new jxl.write.WritableCellFormat(
					titleFont);
			titleFormat.setBackground(Colour.SKY_BLUE);
			titleFormat.setAlignment(Alignment.CENTRE);

			sheetOne.mergeCells(1, 1, 30, 1);
			sheetOne.mergeCells(1, 2, 30, 2);
			sheetOne.mergeCells(1, 3, 30, 3);

			cell = new Label(1, 1, reportName, titleFormat);
			sheetOne.addCell(cell);
			cell = new Label(1, 2, reportTime, titleFormat);
			sheetOne.addCell(cell);*/
			
			//综合报表标题设定
			//大标题格式
			CellStyle titleFormat = book.createCellStyle();
			Font Font =  book.createFont();
			
			Font.setColor(HSSFColor.BLACK.index);
			Font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			Font.setFontName("微软雅黑");
			
			titleFormat.setFont(Font);
			titleFormat.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			//titleFormat.setFillBackgroundColor(HSSFColor.SKY_BLUE.index);
			System.out.println(IndexedColors.LIGHT_GREEN.getIndex());
			//titleFormat.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			//titleFormat.setFillPattern(CellStyle.SOLID_FOREGROUND);
			
			sheetOne.addMergedRegion(new CellRangeAddress(1, 1, 1, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(2, 2, 1, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(3, 3, 1, 30));
			
			Row row = sheetOne.createRow((short) 1);
		    Cell cell = row.createCell((short) 1);
            cell.setCellStyle(titleFormat);
            cell.setCellValue(reportName);
            
            row = sheetOne.createRow((short) 2);
            cell = row.createCell((short) 1);
            cell.setCellStyle(titleFormat);
            cell.setCellValue(reportTime);
            

			/*// 割接任务综述
			sheetOne.mergeCells(1, 4, 30, 4);
			sheetOne.mergeCells(1, 5, 4, 5);
			sheetOne.mergeCells(1, 6, 4, 6);
			sheetOne.mergeCells(1, 7, 4, 7);
			sheetOne.mergeCells(1, 8, 4, 8);
			sheetOne.mergeCells(1, 9, 4, 9);
			sheetOne.mergeCells(1, 10, 4, 10);
			sheetOne.mergeCells(1, 11, 4, 11);

			sheetOne.mergeCells(5, 5, 30, 5);
			sheetOne.mergeCells(5, 6, 30, 6);
			sheetOne.mergeCells(5, 7, 30, 7);
			sheetOne.mergeCells(5, 8, 30, 8);
			sheetOne.mergeCells(5, 9, 30, 9);
			sheetOne.mergeCells(5, 10, 30, 10);
			sheetOne.mergeCells(5, 11, 30, 11);

			cell = new Label(0, 4, "1", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(1, 4, "割接任务综述", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(1, 5, "割接任务名称:", format);
			sheetOne.addCell(cell);
			cell = new Label(1, 6, "创建人:", format);
			sheetOne.addCell(cell);
			cell = new Label(1, 7, "任务描述:", format);
			sheetOne.addCell(cell);
			cell = new Label(1, 8, "割接计划开始时间:", format);
			sheetOne.addCell(cell);
			cell = new Label(1, 9, "割接计划结束时间:", format);
			sheetOne.addCell(cell);
			cell = new Label(1, 10, "割接实际开始时间:", format);
			sheetOne.addCell(cell);
			cell = new Label(1, 11, "割接实际结束时间:", format);
			sheetOne.addCell(cell);*/

            // 割接任务综述
            sheetOne.addMergedRegion(new CellRangeAddress(4, 4, 1, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(5, 5, 1, 4));
			sheetOne.addMergedRegion(new CellRangeAddress(6, 6, 1, 4));
			sheetOne.addMergedRegion(new CellRangeAddress(7, 7, 1, 4));
			sheetOne.addMergedRegion(new CellRangeAddress(8, 8, 1, 4));
			sheetOne.addMergedRegion(new CellRangeAddress(9, 9, 1, 4));
			sheetOne.addMergedRegion(new CellRangeAddress(10, 10, 1, 4));
			sheetOne.addMergedRegion(new CellRangeAddress(11, 11, 1, 4));
			
			sheetOne.addMergedRegion(new CellRangeAddress(5, 5, 5, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(6, 6, 5, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(7, 7, 5, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(8, 8, 5, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(9, 9, 5, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(10, 10, 5, 30));
			sheetOne.addMergedRegion(new CellRangeAddress(11, 11, 5, 30));
			
			row = sheetOne.createRow((short) 4);
            cell = row.createCell(0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("1");
            cell = row.createCell((short) 1);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("割接任务综述");
            
            row = sheetOne.createRow((short) 5);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("割接任务名称:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(taskName);
            
            row = sheetOne.createRow((short) 6);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("创建人:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(createUser);
            
            row = sheetOne.createRow((short) 7);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("任务描述:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(taskNote);
            
            row = sheetOne.createRow((short) 8);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("割接计划开始时间:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(planStart);
            
            row = sheetOne.createRow((short) 9);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("割接计划结束时间:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(planEnd);
            
            row = sheetOne.createRow((short) 10);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("割接实际开始时间:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(startTime);
            
            row = sheetOne.createRow((short) 11);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("割接实际结束时间:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(endTime);
			
            row = sheetOne.createRow((short) 12);
            cell = row.createCell((short) 1);
            cell.setCellStyle(contentStyle);
            cell.setCellValue("割接任务评估:");
            cell = row.createCell((short) 5);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(evaluation);
			/*// 内容
			cell = new Label(5, 5, taskName, format);
			sheetOne.addCell(cell);
			cell = new Label(5, 6, createUser, format);
			sheetOne.addCell(cell);
			cell = new Label(5, 7, taskNote, format);
			sheetOne.addCell(cell);
			cell = new Label(5, 8, planStart, format);
			sheetOne.addCell(cell);
			cell = new Label(5, 9, planEnd, format);
			sheetOne.addCell(cell);
			cell = new Label(5, 10, startTime, format);
			sheetOne.addCell(cell);
			cell = new Label(5, 11, endTime, format);
			sheetOne.addCell(cell);*/

			/*// 割接设备
			sheetOne.mergeCells(1, 13, 30, 13);
			cell = new Label(0, 13, "2", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(1, 13, "割接设备", formatTitle);
			sheetOne.addCell(cell);*/

			// 割接设备
			sheetOne.addMergedRegion(new CellRangeAddress(13, 13, 1, 30));
			row = sheetOne.createRow((short) 13);
            cell = row.createCell((short) 0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("2");
            cell = row.createCell((short) 1);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("割接设备");
			
			// 合并单元格
			/*sheetOne.mergeCells(2, 14, 12, 14);
			sheetOne.mergeCells(13, 14, 20, 14);
			sheetOne.mergeCells(21, 14, 30, 14);*/

			/*cell = new Label(2, 14, "网管", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(13, 14, "网元", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(21, 14, "割接设备", formatTitle);
			sheetOne.addCell(cell);*/

			// 读取割接设备、Link等信息
			List<Map> cutoverEquipList = new ArrayList<Map>();
			List<Map> cutoverLinkList = new ArrayList<Map>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("linkType", 99);
			map.put("cutoverTaskId", cutoverTaskId);

			cutoverEquipList = cutoverManagerMapper
					.getCutoverEquipmentList(map);
			cutoverLinkList = cutoverManagerMapper.getCutoverLinkList(map);
			
			int equipRow;
			
			if(cutoverEquipList.size() != 0){
				
			// 合并单元格
			sheetOne.addMergedRegion(new CellRangeAddress(14, 14, 2, 12));
			sheetOne.addMergedRegion(new CellRangeAddress(14, 14, 13, 20));
			sheetOne.addMergedRegion(new CellRangeAddress(14, 14, 21, 30));
			
			row = sheetOne.createRow((short) 14);
            cell = row.createCell((short) 2);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("网管");
            cell = row.createCell((short) 13);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("网元");
            cell = row.createCell((short) 21);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("端口");

			String emsName = "";
			String neName = "";
			String ptpName = "";
			for (int i = 0; i < cutoverEquipList.size(); i++) {// 行
				List<Map> equipList = new ArrayList<Map>();
				Map equip = cutoverEquipList.get(i);
				int targetType = Integer.valueOf(
						String.valueOf(equip.get("TARGET_TYPE"))).intValue();
				int targetId = Integer.valueOf(
						String.valueOf(equip.get("TARGET_ID"))).intValue();
				if (targetType == 2) { // 网管
					equipList = cutoverManagerMapper
							.getEquipListByEmsId(targetId);
					if (equipList.size() != 0) {
						emsName = String.valueOf(equipList.get(0).get(
								"DISPLAY_NAME"));
					}
					/*cell = new Label(2, 15 + i, emsName, format);
					sheetOne.addCell(cell);*/
					row = sheetOne.createRow((short) 15 + i);
		            cell = row.createCell((short) 2);
		            cell.setCellStyle(contentStyle);
		            cell.setCellValue(emsName);
				} else if (targetType == 4) {// 网元
					equipList = cutoverManagerMapper
							.getEquipListByNeId(targetId);
					if (equipList.size() != 0) {
						emsName = String.valueOf(equipList.get(0)
								.get("emsName"));
						neName = String.valueOf(equipList.get(0).get("neName"));
					}
					/*cell = new Label(2, 15 + i, emsName, format);
					sheetOne.addCell(cell);
					cell = new Label(13, 15 + i, neName, format);
					sheetOne.addCell(cell);*/
					row = sheetOne.createRow((short) 15 + i);
		            cell = row.createCell((short) 2);
		            cell.setCellStyle(contentStyle);
		            cell.setCellValue(emsName);
		            cell = row.createCell((short) 13);
		            cell.setCellStyle(contentStyle);
		            cell.setCellValue(neName);
				} else if (targetType == 8) {// 端口
					equipList = cutoverManagerMapper
							.getEquipListByPtpId(targetId);
					if (equipList.size() != 0) {
						emsName = String.valueOf(equipList.get(0)
								.get("emsName"));
						neName = String.valueOf(equipList.get(0).get("neName"));
						ptpName = String.valueOf(equipList.get(0)
								.get("ptpName"));
					}
					/*cell = new Label(2, 15 + i, emsName, format);
					sheetOne.addCell(cell);
					cell = new Label(13, 15 + i, neName, format);
					sheetOne.addCell(cell);
					cell = new Label(21, 15 + i, ptpName, format);
					sheetOne.addCell(cell);*/
					row = sheetOne.createRow((short) 15 + i);
		            cell = row.createCell((short) 2);
		            cell.setCellStyle(contentStyle);
		            cell.setCellValue(emsName);
		            cell = row.createCell((short) 13);
		            cell.setCellStyle(contentStyle);
		            cell.setCellValue(neName);
		            cell = row.createCell((short) 21);
		            cell.setCellStyle(contentStyle);
		            cell.setCellValue(ptpName);
				}
			}
			equipRow = 15 + cutoverEquipList.size()+1;
			}else{

			// 链路名
			//int LinkTitleRow = 15 + cutoverEquipList.size()+1;
			int LinkTitleRow = 14;
			equipRow = 15;

			/*sheetOne.mergeCells(2, LinkTitleRow, 6, LinkTitleRow);
			sheetOne.mergeCells(7, LinkTitleRow, 18, LinkTitleRow);
			sheetOne.mergeCells(19, LinkTitleRow, 30, LinkTitleRow);

			cell = new Label(2, LinkTitleRow, "链路名", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(7, LinkTitleRow, "A端", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(19, LinkTitleRow, "Z端", formatTitle);
			sheetOne.addCell(cell);*/
            System.out.println(cutoverLinkList.size());
			if(cutoverLinkList.size() != 0){
			sheetOne.addMergedRegion(new CellRangeAddress(LinkTitleRow, LinkTitleRow, 2, 6));
			sheetOne.addMergedRegion(new CellRangeAddress(LinkTitleRow, LinkTitleRow, 7, 18));
			sheetOne.addMergedRegion(new CellRangeAddress(LinkTitleRow, LinkTitleRow, 19, 30));
			
			row = sheetOne.createRow((short) LinkTitleRow);
            cell = row.createCell((short) 2);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("链路名");
            cell = row.createCell((short) 7);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("A端");
            cell = row.createCell((short) 19);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("Z端");

			int LinkContentRow = LinkTitleRow + 1;
			String linkName = "";
			String AName = "";
			String ZName = "";
			System.out.println("链路列表："+cutoverLinkList);
			for (int i = 0; i < cutoverLinkList.size(); i++) {
				List<Map> linkList = new ArrayList<Map>();
				Map link = cutoverLinkList.get(i);
				int targetType = Integer.valueOf(
						String.valueOf(link.get("TARGET_TYPE"))).intValue();
				int targetId = Integer.valueOf(
						String.valueOf(link.get("TARGET_ID"))).intValue();
				linkList = cutoverManagerMapper.getLinkListByLinkId(targetId);
				if (linkList.size() != 0) {
					linkName = String.valueOf(linkList.get(0).get("linkName"));
					AName = String.valueOf(linkList.get(0).get("aNeName"))
							+ "-"
							+ String.valueOf(linkList.get(0).get("aPortDesc"));
					ZName = String.valueOf(linkList.get(0).get("zNeName"))
							+ "-"
							+ String.valueOf(linkList.get(0).get("zPortDesc"));
				
				
				}

				/*sheetOne.mergeCells(2, LinkTitleRow + i + 1, 6, LinkTitleRow+ i + 1);
				sheetOne.mergeCells(7, LinkTitleRow + i + 1, 18, LinkTitleRow+ i + 1);
				sheetOne.mergeCells(19, LinkTitleRow + i + 1, 30, LinkTitleRow+ i + 1);

				cell = new Label(2, LinkContentRow + i, linkName, format);
				sheetOne.addCell(cell);
				cell = new Label(7, LinkContentRow + i, AName, format);
				sheetOne.addCell(cell);
				cell = new Label(19, LinkContentRow + i, ZName, format);
				sheetOne.addCell(cell);*/
				
				sheetOne.addMergedRegion(new CellRangeAddress(LinkTitleRow + i + 1, LinkTitleRow+ i + 1, 2, 6));
				sheetOne.addMergedRegion(new CellRangeAddress(LinkTitleRow + i + 1, LinkTitleRow+ i + 1, 7, 18));
				sheetOne.addMergedRegion(new CellRangeAddress(LinkTitleRow + i + 1, LinkTitleRow+ i + 1, 19, 30));
				
				row = sheetOne.createRow((short) LinkContentRow + i);
	            cell = row.createCell((short) 2);
	            cell.setCellStyle(contentStyle);
	            cell.setCellValue(linkName);
	            cell = row.createCell((short) 7);
	            cell.setCellStyle(contentStyle);
	            cell.setCellValue(AName);
	            cell = row.createCell((short) 19);
	            cell.setCellStyle(contentStyle);
	            cell.setCellValue(ZName);
	            
	            
	            equipRow = equipRow + linkList.size();

			}
			}
			}

			// 光缆名
			//int cableTitleRow = LinkTitleRow + cutoverLinkList.size() + 1;
			/*cell = new Label(2, cableTitleRow, "光缆名", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(7, cableTitleRow, "A端", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(19, cableTitleRow, "Z端", formatTitle);
			sheetOne.addCell(cell);*/
			
			//暂时没有光缆部分，故先省略。。。
			// 合并单元格
			/*sheetOne.addMergedRegion(new CellRangeAddress(cableTitleRow, cableTitleRow, 2, 6));
			sheetOne.addMergedRegion(new CellRangeAddress(cableTitleRow, cableTitleRow, 7, 18));
			sheetOne.addMergedRegion(new CellRangeAddress(cableTitleRow, cableTitleRow, 19, 30));
			
			row = sheetOne.createRow((short) cableTitleRow);
            cell = row.createCell((short) 2);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("光缆名");
            cell = row.createCell((short) 7);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("A端");
            cell = row.createCell((short) 19);
            cell.setCellStyle(subTitleStyle);
            cell.setCellValue("Z端");*/

			// 割接设置
			//int setTitleRow = cableTitleRow + 2;
			int setTitleRow = equipRow + 2;

			/*sheetOne.mergeCells(1, setTitleRow, 30, setTitleRow);

			cell = new Label(0, setTitleRow, "3", formatTitle);
			sheetOne.addCell(cell);
			cell = new Label(1, setTitleRow, "割接设置", formatTitle);
			sheetOne.addCell(cell);

			cell = new Label(2, setTitleRow + 1, cutoverSetAlarm, format);
			sheetOne.addCell(cell);
			cell = new Label(2, setTitleRow + 2, cutoverSetSnapshot, format);
			sheetOne.addCell(cell);*/
			
            
			sheetOne.addMergedRegion(new CellRangeAddress(setTitleRow, setTitleRow, 1, 30));
			
			row = sheetOne.createRow((short) setTitleRow);
            cell = row.createCell((short) 0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("3");
            cell = row.createCell((short) 1);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("割接设置");
            
            row = sheetOne.createRow((short) setTitleRow + 1);
            cell = row.createCell((short) 2);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(cutoverSetAlarm);
            
            row = sheetOne.createRow((short) setTitleRow + 2);
            cell = row.createCell((short) 2);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(cutoverSetSnapshot);
            
            row = sheetOne.createRow((short) setTitleRow + 3);
            cell = row.createCell((short) 2);
            cell.setCellStyle(contentStyle);
            cell.setCellValue(autoUpdateCompareValue);
			/**************************************** 电路表生成*****************************************/
			/*// 添加一个工作表
			WritableSheet sheetSec = workbook.createSheet("电路表", 1);

			// 表格标题格式
			WritableCellFormat gridTitle = new WritableCellFormat();
			gridTitle.setAlignment(Alignment.LEFT);
			gridTitle.setBackground(Colour.BLUE);
			gridTitle.setBorder(Border.ALL, BorderLineStyle.THIN);

			// 表格内容格式
			WritableCellFormat gridFormat = new WritableCellFormat();
			gridFormat.setAlignment(Alignment.LEFT);
			gridFormat.setBorder(Border.ALL, BorderLineStyle.THIN);*/
            
            Sheet sheetSec = book.createSheet();
            book.setSheetName(1,"电路表");
            // 表格标题格式
			CellStyle gridTitle = book.createCellStyle();
			Font gridTitleFont = book.createFont();
			gridTitleFont.setColor(HSSFColor.WHITE.index);
			gridTitleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			gridTitleFont.setFontName("微软雅黑");
			
			gridTitle.setFont(gridTitleFont);
			gridTitle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			gridTitle.setFillForegroundColor(HSSFColor.BLUE.index);
			gridTitle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			gridTitle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			gridTitle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			gridTitle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			gridTitle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			// 表格内容格式
			CellStyle gridFormat = book.createCellStyle();
			//gridFormat.setFillPattern(CellStyle.SOLID_FOREGROUND);
			gridFormat.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			gridFormat.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			gridFormat.setBorderRight(XSSFCellStyle.BORDER_THIN);
			gridFormat.setBorderTop(XSSFCellStyle.BORDER_THIN);
			
			// 设定行高、列距
			sheetSec.setDefaultColumnWidth((short)4);
			sheetSec.setDefaultRowHeight((short)300);

			/*// 设定行高、列距
			for (int i = 0; i <= 30; i++) {
				sheetSec.setColumnView(i, 5);
				sheetSec.setRowView(i, 300);
			}*/

			/*sheetSec.mergeCells(1, 0, 5, 0);
			sheetSec.mergeCells(6, 0, 10, 0);
			sheetSec.mergeCells(11, 0, 21, 0);
			sheetSec.mergeCells(22, 0, 27, 0);

			cell = new Label(0, 0, "", gridTitle);
			sheetSec.addCell(cell);
			cell = new Label(1, 0, "电路编号", gridTitle);
			sheetSec.addCell(cell);
			cell = new Label(6, 0, "资源系统编号", gridTitle);
			sheetSec.addCell(cell);
			cell = new Label(11, 0, "A端端口", gridTitle);
			sheetSec.addCell(cell);
			cell = new Label(22, 0, "A端时隙", gridTitle);
			sheetSec.addCell(cell);*/

			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 6, 10));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 11, 17));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 18, 28));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 29, 33));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 34, 40));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 41, 51));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 52, 57));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 58, 61));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 62, 66));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 67, 72));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 73, 78));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 79, 84));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 85, 90));
			sheetSec.addMergedRegion(new CellRangeAddress(0, 0, 91, 96));
			
			row = sheetSec.createRow((short) 0);
            cell = row.createCell((short) 0);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("");
            cell = row.createCell((short) 1);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("电路编号");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 1, 5), sheetSec,book);
            cell = row.createCell((short) 6);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("资源系统编号");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 6, 10), sheetSec,book);
            cell = row.createCell((short) 11);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("A端网元");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 11, 17), sheetSec,book);
            cell = row.createCell((short) 18);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("A端端口");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 18, 28), sheetSec,book);
            cell = row.createCell((short) 29);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("A端时隙");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 29, 33), sheetSec,book);
            cell = row.createCell((short) 34);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("Z端网元");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 34, 40), sheetSec,book);
            cell = row.createCell((short) 41);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("Z端端口");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 41, 51), sheetSec,book);
            cell = row.createCell((short) 52);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("Z端时隙");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 52, 57), sheetSec,book);
            cell = row.createCell((short) 58);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("电路类别");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 58, 61), sheetSec,book);
            cell = row.createCell((short) 62);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("电路速率");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 62, 66), sheetSec,book);
            cell = row.createCell((short) 67);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("路由名称");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 67, 72), sheetSec,book);
            cell = row.createCell((short) 73);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("客户名称");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 73, 78), sheetSec,book);
            cell = row.createCell((short) 79);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("用途");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 79, 84), sheetSec,book);
            cell = row.createCell((short) 85);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("A端用户");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 85, 90), sheetSec,book);
            cell = row.createCell((short) 91);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("Z端用户");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 91, 96), sheetSec,book);
			
			String circuitNo = "";
			String sourceNo = "";
			String ANe = "";
			String APort = "";
			String ASlot = "";
			String ZNe = "";
			String ZPort = "";
			String ZSlot = "";
			String rate = "";
			String circuitType = "";
			String routeName = "";
			String clientName = "";
			String usedFor = "";
			String AEndUserName = "";
			String ZEndUserName = "";

			Map<String, Object> circuitMap = new HashMap<String, Object>();
			circuitMap.put("cutoverTaskId", cutoverTaskId);

			Map conditionMap = new HashMap();
			Map resultMap = new HashMap();
			
			conditionMap.put("VALUE", "*");
			conditionMap.put("NAME", "T_SYS_TASK_INFO");
			conditionMap.put("ID_NAME", "SYS_TASK_ID");
			conditionMap.put("ID_VALUE", cutoverTaskId);
			List<Map> equipList = new ArrayList<Map>();
			equipList = cutoverManagerMapper.getByParameter(conditionMap);
			Map firstEquip = equipList.get(0);
			String equipType = ((Integer) firstEquip.get("TARGET_TYPE"))
					.toString();
			List nodeList = new ArrayList();
			if (!equipType.equals("99")) {
				for (int i = 0; i < equipList.size(); i++) {
					nodeList.add(equipList.get(i).get("TARGET_ID"));
				}
			} else {
				equipType = "8";
				List<Map> linkList = cutoverManagerMapper.getCutoverEqptLinkList(cutoverTaskId);
				for (int i = 0; i < linkList.size(); i++) {
					nodeList.add(linkList.get(i).get("A_END_PTP"));
					nodeList.add(linkList.get(i).get("Z_END_PTP"));
				}
			}
			Map searchMap = new HashMap();
			searchMap.put("start", 0);
			searchMap.put("limit", 1000000000);
			searchMap.put("nodeLevel", equipType);
			searchMap.put("nodeList", nodeList);
			System.out.println("jiedian"+nodeList);
			resultMap = circuitManagerService.selectAllCircuitAbout(searchMap);
			System.out.println(resultMap);
			System.out.println(resultMap.get("total"));
			
			if(resultMap.get("rows")!=null){
			
			List<Map> ciucuitsList = new ArrayList<Map>();
			ciucuitsList = (List<Map>)resultMap.get("rows");

			if (ciucuitsList.size() != 0) {
				for (int i = 0; i < ciucuitsList.size(); i++) {
					Map ciucuits = ciucuitsList.get(i);
					circuitNo = String.valueOf(ciucuits.get("cir_no"));
					if(ciucuits.get("source_no") == null){
						sourceNo = "";
					}else{
						sourceNo = String.valueOf(ciucuits.get("source_no"));
					}
					if(ciucuits.get("a_end_ne") == null){
						ANe = "";
					}else{
						ANe = String.valueOf(ciucuits.get("a_end_ne"));
					}
					if(ciucuits.get("a_end_port") == null){
						APort = "";
					}else{
						APort = String.valueOf(ciucuits.get("a_end_port"));
					}
					
					if(ciucuits.get("a_end_ctp") == null){
						ASlot = "";
					}else{
						ASlot = String.valueOf(ciucuits.get("a_end_ctp"));
					}
					if(ciucuits.get("z_end_ne") == null){
						ZNe = "";
					}else{
						ZNe = String.valueOf(ciucuits.get("z_end_ne"));
					}
					if(ciucuits.get("z_end_port") == null){
						ZPort = "";
					}else{
						ZPort = String.valueOf(ciucuits.get("z_end_port"));
					}
					
					if(ciucuits.get("z_end_ctp") == null){
						ZSlot = "";
					}else{
						ZSlot = String.valueOf(ciucuits.get("z_end_ctp"));
					}
					
					if(ciucuits.get("rate") == null){
						rate = "";
					}else{
						rate = String.valueOf(ciucuits.get("rate"));
					}
					
					if(ciucuits.get("IS_COMPLETE_CIR") == null){
						circuitType = "";
					}else{
						circuitType = String.valueOf(ciucuits.get("IS_COMPLETE_CIR"));
						if(circuitType.equals("0"))
							circuitType = "不完整";
						else
							circuitType = "完整";
					}
					
					if(ciucuits.get("cir_name") == null){
						routeName = "";
					}else{
						routeName = String.valueOf(ciucuits.get("cir_name"));
					}
					
					if(ciucuits.get("client_name") == null){
						clientName = "";
					}else{
						clientName = String.valueOf(ciucuits.get("client_name"));
					}
					
					if(ciucuits.get("USED_FOR") == null){
						usedFor = "";
					}else{
						usedFor = String.valueOf(ciucuits.get("USED_FOR"));
					}
					
					if(ciucuits.get("a_end_user_name") == null){
						AEndUserName = "";
					}else{
						AEndUserName = String.valueOf(ciucuits.get("a_end_user_name"));
					}
					
					if(ciucuits.get("z_end_user_name") == null){
						ZEndUserName = "";
					}else{
						ZEndUserName = String.valueOf(ciucuits.get("z_end_user_name"));
					}

					/*sheetSec.mergeCells(1, i + 1, 5, i + 1);
					sheetSec.mergeCells(6, i + 1, 10, i + 1);
					sheetSec.mergeCells(11, i + 1, 21, i + 1);
					sheetSec.mergeCells(22, i + 1, 27, i + 1);

					cell = new Label(0, i + 1, String.valueOf(i + 1),
							gridFormat);
					sheetSec.addCell(cell);
					cell = new Label(1, i + 1, circuitNo, gridFormat);
					sheetSec.addCell(cell);
					cell = new Label(6, i + 1, sourceNo, gridFormat);
					sheetSec.addCell(cell);
					cell = new Label(11, i + 1, APort, gridFormat);
					sheetSec.addCell(cell);
					cell = new Label(22, i + 1, ASlot, gridFormat);
					sheetSec.addCell(cell);*/
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 1, 5));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 6, 10));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 11, 17));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 18, 28));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 29, 33));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 34, 40));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 41, 51));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 52, 57));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 58, 61));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 62, 66));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 67, 72));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 73, 78));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 79, 84));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 85, 90));
					sheetSec.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 91, 96));
					
					
					
					row = sheetSec.createRow((short) i + 1);
		            cell = row.createCell((short) 0);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(String.valueOf(i + 1));
		            cell = row.createCell((short) 1);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(circuitNo);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 1, 5), sheetSec,book);
		            cell = row.createCell((short) 6);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(sourceNo);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 6, 10), sheetSec,book);
		            cell = row.createCell((short) 11);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ANe);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 11, 17), sheetSec,book);
		            cell = row.createCell((short) 18);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(APort);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 18, 28), sheetSec,book);
		            cell = row.createCell((short) 29);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ASlot);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 29, 33), sheetSec,book);
		            cell = row.createCell((short) 34);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ZNe);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 34, 40), sheetSec,book);
		            cell = row.createCell((short) 41);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ZPort);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 41, 51), sheetSec,book);
		            cell = row.createCell((short) 52);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ZSlot);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 52, 57), sheetSec,book);
		            cell = row.createCell((short) 58);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(circuitType);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 58, 61), sheetSec,book);
		            cell = row.createCell((short) 62);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(rate);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 62, 66), sheetSec,book);
		            cell = row.createCell((short) 67);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(routeName);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 67, 72), sheetSec,book);
		            cell = row.createCell((short) 73);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(clientName);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 73, 78), sheetSec,book);
		            cell = row.createCell((short) 79);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(usedFor);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 79, 84), sheetSec,book);
		            cell = row.createCell((short) 85);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(AEndUserName);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 85, 90), sheetSec,book);
		            cell = row.createCell((short) 91);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ZEndUserName);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 91, 96), sheetSec,book);
				}
			}
			}
			/**************************************** 性能值表生成*****************************************/
			// 添加一个工作表
			Sheet sheetThir = book.createSheet();
			book.setSheetName(2,"性能值表");

			/*// 设定行高、列距
			for (int i = 0; i <= 30; i++) {
				sheetThir.setColumnView(i, 5);
				sheetThir.setRowView(i, 300);
			}

			sheetThir.mergeCells(1, 0, 5, 0);
			sheetThir.mergeCells(6, 0, 10, 0);
			sheetThir.mergeCells(11, 0, 14, 0);
			sheetThir.mergeCells(15, 0, 19, 0);
			sheetThir.mergeCells(20, 0, 24, 0);
			sheetThir.mergeCells(25, 0, 27, 0);

			cell = new Label(0, 0, "", gridTitle);
			sheetThir.addCell(cell);
			cell = new Label(1, 0, "网元", gridTitle);
			sheetThir.addCell(cell);
			cell = new Label(6, 0, "端口", gridTitle);
			sheetThir.addCell(cell);
			cell = new Label(11, 0, "端口性能名称", gridTitle);
			sheetThir.addCell(cell);
			cell = new Label(15, 0, "割接前快照值", gridTitle);
			sheetThir.addCell(cell);
			cell = new Label(20, 0, "割接后快照值", gridTitle);
			sheetThir.addCell(cell);
			cell = new Label(25, 0, "差值", gridTitle);
			sheetThir.addCell(cell);*/
			
			// 设定行高、列距
			sheetThir.setDefaultColumnWidth((short)4);
			sheetThir.setDefaultRowHeight((short)300);
			
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 6, 10));
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 11, 14));
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 15, 19));
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 20, 24));
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 25, 27));
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 28, 32));
			sheetThir.addMergedRegion(new CellRangeAddress(0, 0, 33, 37));
			
			row = sheetThir.createRow((short) 0);
            cell = row.createCell((short) 0);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("");
            cell = row.createCell((short) 1);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("网元");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 1, 5), sheetThir,book); 
            cell = row.createCell((short) 6);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("端口");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 6, 10), sheetThir,book);
            cell = row.createCell((short) 11);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("端口性能名称");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 11, 14), sheetThir,book);
            cell = row.createCell((short) 15);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("割接前快照值");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 15, 19), sheetThir,book);
            cell = row.createCell((short) 20);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("割接后快照值");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 20, 24), sheetThir,book);
            cell = row.createCell((short) 25);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("差值");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 25, 27), sheetThir,book);
            cell = row.createCell((short) 28);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("割接前快照时间");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 28, 32), sheetThir,book);
            cell = row.createCell((short) 33);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("割接后快照时间");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 33, 37), sheetThir,book);
			
            

			String pmNe = "";
			String pmPtp = "";
			String pmDescription = "";
			String pmBeforeValue = "";
			String pmAfterValue = "";
			String difference = "";
			String sanpshotTimeBefore = "";
			String sanpshotTimeAfter = "";

			// 读取割接性能值信息
			
			 /* List<Map> cutoverPMList = new ArrayList<Map>(); cutoverPMList =
			 * cutoverManagerMapper.getCutoverPMList(cutoverTaskId);
			 * 
			 * if(cutoverPMList.size() != 0){ for(int i=0;i<cutoverPMList.size();i++){
			 * Map pm = cutoverPMList.get(i); pmNe =
			 * String.valueOf(pm.get("neName")); pmPtp =
			 * String.valueOf(pm.get("ptpName")); pmDescription =
			 * String.valueOf(pm.get("PERFORMANCE_NAME")); pmBeforeValue =
			 * String.valueOf(pm.get("PERFORMANCE_VALUE"));
			 * 
			 * map = new HashMap<String, Object>(); map.put("cutoverTaskId",
			 * cutoverTaskId); map.put("ptpId", pm.get("PORT_ID"));
			 * map.put("pmDescription", pm.get("PERFORMANCE_NAME"));
			 * 
			 * List<Map> cutoverAfterList = new ArrayList<Map>();
			 * cutoverAfterList = cutoverManagerMapper.getCutoverAfterList(map);
			 * if(cutoverAfterList.size() != 0){ Map cutoverAfterPM =
			 * cutoverAfterList.get(0); pmAfterValue =
			 * String.valueOf(cutoverAfterPM.get("PERFORMANCE_VALUE")); }
			 * difference =
			 * String.valueOf(Float.valueOf(pmAfterValue)-Float.valueOf(pmBeforeValue));
			 * 
			 * cell=new Label(0,i+1,String.valueOf(i+1),format);
			 * sheetThir.addCell(cell); cell=new Label(1,i+1,pmNe,format);
			 * sheetThir.addCell(cell); cell=new Label(6,i+1,pmPtp,format);
			 * sheetThir.addCell(cell); cell=new
			 * Label(11,i+1,pmDescription,format); sheetThir.addCell(cell);
			 * cell=new Label(15,i+1,pmBeforeValue,format);
			 * sheetThir.addCell(cell); cell=new
			 * Label(20,i+1,pmAfterValue,format); sheetThir.addCell(cell);
			 * cell=new Label(25,i+1,difference,format);
			 * sheetThir.addCell(cell); } }*/
			 

			List<Map> cutoverPMList = new ArrayList<Map>();
			cutoverPMList = cutoverManagerMapper.searchPmValue(cutoverTaskId,
					-1, -1);

			if (cutoverPMList.size() != 0) {
				for (int i = 0; i < cutoverPMList.size(); i++) {
					Map pm = cutoverPMList.get(i);
					pmNe = String.valueOf(pm.get("DISPLAY_NE"));
					pmPtp = String.valueOf(pm.get("DISPLAY_PORT_DESC"));
					pmDescription = String.valueOf(pm.get("PM_DESCRIPTION"));
					pmBeforeValue = String.valueOf(pm.get("VALUE_BEFORE"));
					pmAfterValue = String.valueOf(pm.get("VALUE_AFTER"));
					if (!pmBeforeValue.equals("null")) {
						difference = new BigDecimal(pmAfterValue).subtract(new BigDecimal(pmBeforeValue)).toString();
					}
					sanpshotTimeBefore = String.valueOf(pm.get("TIME_BEFORE"));
					sanpshotTimeAfter = String.valueOf(pm.get("TIME_AFTER"));

					/*sheetThir.mergeCells(1, i + 1, 5, i + 1);
					sheetThir.mergeCells(6, i + 1, 10, i + 1);
					sheetThir.mergeCells(11, i + 1, 14, i + 1);
					sheetThir.mergeCells(15, i + 1, 19, i + 1);
					sheetThir.mergeCells(20, i + 1, 24, i + 1);
					sheetThir.mergeCells(25, i + 1, 27, i + 1);

					cell = new Label(0, i + 1, String.valueOf(i + 1),
							gridFormat);
					sheetThir.addCell(cell);
					cell = new Label(1, i + 1, pmNe, gridFormat);
					sheetThir.addCell(cell);
					cell = new Label(6, i + 1, pmPtp, gridFormat);
					sheetThir.addCell(cell);
					cell = new Label(11, i + 1, pmDescription, gridFormat);
					sheetThir.addCell(cell);
					cell = new Label(15, i + 1, pmBeforeValue, gridFormat);
					sheetThir.addCell(cell);
					cell = new Label(20, i + 1, pmAfterValue, gridFormat);
					sheetThir.addCell(cell);
					cell = new Label(25, i + 1, difference, gridFormat);
					sheetThir.addCell(cell);*/
					
		            sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 1, 5));
					sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 6, 10));
					sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 11, 14));
					sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 15, 19));
					sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 20, 24));
					sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 25, 27));
					sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 28, 32));
					sheetThir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 33, 37));
					
					row = sheetThir.createRow((short) i + 1);
		            cell = row.createCell((short) 0);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(String.valueOf(i + 1));
		            cell = row.createCell((short) 1);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(pmNe);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 1, 5), sheetThir,book);
		            cell = row.createCell((short) 6);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(pmPtp);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 6, 10), sheetThir,book);
		            cell = row.createCell((short) 11);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(pmDescription);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 11, 14), sheetThir,book);
		            cell = row.createCell((short) 15);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(pmBeforeValue);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 15, 19), sheetThir,book);
		            cell = row.createCell((short) 20);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(pmAfterValue);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 20, 24), sheetThir,book);
		            cell = row.createCell((short) 25);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(difference);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 25, 27), sheetThir,book);
		            cell = row.createCell((short) 28);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(sanpshotTimeBefore);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 28, 32), sheetThir,book);
		            cell = row.createCell((short) 33);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(sanpshotTimeAfter);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 33, 37), sheetThir,book);
		            

				}
			}

			/**************************************** 告警表生成*****************************************/
			// 添加一个工作表
			Sheet sheetFor = book.createSheet();
			book.setSheetName(3,"告警表");

			/*// 设定行高、列距
			for (int i = 0; i <= 34; i++) {
				sheetFor.setColumnView(i, 5);
				sheetFor.setRowView(i, 300);
			}

			sheetFor.mergeCells(1, 0, 5, 0);
			sheetFor.mergeCells(6, 0, 8, 0);
			sheetFor.mergeCells(9, 0, 11, 0);
			sheetFor.mergeCells(12, 0, 16, 0);
			sheetFor.mergeCells(17, 0, 21, 0);
			sheetFor.mergeCells(22, 0, 28, 0);
			sheetFor.mergeCells(29, 0, 34, 0);

			cell = new Label(0, 0, "", gridTitle);
			sheetFor.addCell(cell);
			cell = new Label(1, 0, "网元", gridTitle);
			sheetFor.addCell(cell);
			cell = new Label(6, 0, "类型", gridTitle);
			sheetFor.addCell(cell);
			cell = new Label(9, 0, "重要度", gridTitle);
			sheetFor.addCell(cell);
			cell = new Label(12, 0, "告警名称", gridTitle);
			sheetFor.addCell(cell);
			cell = new Label(17, 0, "AID", gridTitle);
			sheetFor.addCell(cell);
			cell = new Label(22, 0, "割接前告警快照时间", gridTitle);
			sheetFor.addCell(cell);
			cell = new Label(29, 0, "割接后告警快照时间", gridTitle);
			sheetFor.addCell(cell);*/

			// 设定行高、列距
			sheetFor.setDefaultColumnWidth((short)4);
			sheetFor.setDefaultRowHeight((short)300);
			
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 6, 8));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 9, 11));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 12, 16));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 17, 21));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 22, 26));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 27, 31));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 32, 36));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 37, 41));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 42, 46));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 47, 51));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 52, 56));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 57, 61));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 62, 66));
			sheetFor.addMergedRegion(new CellRangeAddress(0, 0, 67, 71));
			
			row = sheetFor.createRow((short) 0);
            cell = row.createCell((short) 0);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("");
            cell = row.createCell((short) 1);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("网元");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 1, 5), sheetFor,book);
            cell = row.createCell((short) 6);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("类型");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 6, 8), sheetFor,book);
            cell = row.createCell((short) 9);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("重要度");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 9, 11), sheetFor,book);
            cell = row.createCell((short) 12);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警名称");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 12, 16), sheetFor,book);
            cell = row.createCell((short) 17);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("槽道");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 17, 21), sheetFor,book);
            cell = row.createCell((short) 22);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("板卡");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 22, 26), sheetFor,book);
            cell = row.createCell((short) 27);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("端口");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 27, 31), sheetFor,book);
            cell = row.createCell((short) 32);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("速率");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 32, 36), sheetFor,book);
            cell = row.createCell((short) 37);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警类型");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 37, 41), sheetFor,book);
            /*cell = row.createCell((short) 42);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警描述");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 42, 46), sheetFor,book);
            cell = row.createCell((short) 47);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警状态");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 47, 51), sheetFor,book);*/
            cell = row.createCell((short) 42);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("网管分组");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 42, 46), sheetFor,book);
            cell = row.createCell((short) 47);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("网管");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 47, 51), sheetFor,book);
            cell = row.createCell((short) 52);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("机房");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 52, 56), sheetFor,book);
            cell = row.createCell((short) 57);
            cell.setCellStyle(gridTitle);
            cell.setCellValue(FieldNameDefine.STATION_NAME);
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 57, 61), sheetFor,book);
            cell = row.createCell((short) 62);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("割接前告警快照时间");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 62, 66), sheetFor,book);
            cell = row.createCell((short) 67);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("割接后告警快照时间");  
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 67, 71), sheetFor,book);
			
			String ne = "";
			String category = "";
			String level = "";
			String alarmName = "";
			String AID = "";
			String equip = "";
			String port = "";
			String alarmRate = "";
			String alarmType = "";
			String alarmDescription = "";
			String alarmStatus = "";
			String emsGroup = "";
			String ems = "";
			String room = "";
			String station = "";
			String cutoverBeforeTime = "";
			String cutoverAfterTime = "";

			List<Map> cutoverAlarmList = new ArrayList<Map>();
			cutoverAlarmList = cutoverManagerMapper.getCurrentAlarms(
					cutoverTaskId, 0, -1, -1);

			if (cutoverAlarmList.size() != 0) {
				for (int i = 0; i < cutoverAlarmList.size(); i++) {
					Map alarm = cutoverAlarmList.get(i);
					ne = String.valueOf(alarm.get("NE_NAME"));
					category = String.valueOf(alarm.get("ALARM_CATEGORY"));
					// 告警类型前台显示转换
					if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.UNCHANGED_VALUE
							.equals(category)) {
						category = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.UNCHANGED;
					} else if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.CLEAR_VALUE
							.equals(category)) {
						category = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.CLEAR;
					} else if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.ADD_VALUE
							.equals(category)) {
						category = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.ADD;
					}
					level = String.valueOf(alarm.get("PERCEIVED_SEVERITY"));
					// 告警等级前台显示转换
					if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT;
					}
					
					alarmName = String.valueOf(alarm.get("NATIVE_PROBABLE_CAUSE"));
					AID = String.valueOf(alarm.get("SLOT_DISPLAY_NAME"));
					equip = String.valueOf(alarm.get("UNIT_NAME"));
					port = String.valueOf(alarm.get("PORT_NO"));
					alarmRate = String.valueOf(alarm.get("INTERFACE_RATE"));
					
					alarmType = String.valueOf(alarm.get("ALARM_TYPE"));
					//告警类型前台显示转换
					if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION;
					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE;
					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT;
					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE;
					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT;
					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY;
					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION;
					}
					//alarmDescription = String.valueOf(alarm.get("ALARM_DESCRIPTION"));
					// 告警状态前台显示转换
					//alarmStatus = String.valueOf(alarm.get("ALARM_STATUS"));
					
					emsGroup = String.valueOf(alarm.get("EMS_GROUP_NAME"));
					ems = String.valueOf(alarm.get("NATIVE_EMS_NAME"));
					room = String.valueOf(alarm.get("ROOM"));
					station = String.valueOf(alarm.get("STATION"));
					cutoverBeforeTime = String.valueOf(alarm.get("SNAPSHOT_TIME_BEFORE"));
					cutoverAfterTime = String.valueOf(alarm.get("SNAPSHOT_TIME_AFTER"));

					/*sheetFor.mergeCells(1, i + 1, 5, i + 1);
					sheetFor.mergeCells(6, i + 1, 8, i + 1);
					sheetFor.mergeCells(9, i + 1, 11, i + 1);
					sheetFor.mergeCells(12, i + 1, 16, i + 1);
					sheetFor.mergeCells(17, i + 1, 21, i + 1);
					sheetFor.mergeCells(22, i + 1, 28, i + 1);
					sheetFor.mergeCells(29, i + 1, 34, i + 1);

					cell = new Label(0, i + 1, String.valueOf(i + 1),
							gridFormat);
					sheetFor.addCell(cell);
					cell = new Label(1, i + 1, ne, gridFormat);
					sheetFor.addCell(cell);
					cell = new Label(6, i + 1, category, gridFormat);
					sheetFor.addCell(cell);
					cell = new Label(9, i + 1, level, gridFormat);
					sheetFor.addCell(cell);
					cell = new Label(12, i + 1, alarmName, gridFormat);
					sheetFor.addCell(cell);
					cell = new Label(17, i + 1, AID, gridFormat);
					sheetFor.addCell(cell);
					cell = new Label(22, i + 1, cutoverBeforeTime, gridFormat);
					sheetFor.addCell(cell);
					cell = new Label(29, i + 1, cutoverAfterTime, gridFormat);
					sheetFor.addCell(cell);*/
					
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 1, 5));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 6, 8));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 9, 11));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 12, 16));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 17, 21));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 22, 26));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 27, 31));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 32, 36));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 37, 41));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 42, 46));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 47, 51));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 52, 56));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 57, 61));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 62, 66));
					sheetFor.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 67, 71));
					
					row = sheetFor.createRow((short) i + 1);
		            cell = row.createCell((short) 0);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(String.valueOf(i + 1));
		            cell = row.createCell((short) 1);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ne);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 1, 5), sheetFor,book);
		            cell = row.createCell((short) 6);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(category);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 6, 8), sheetFor,book);
		            cell = row.createCell((short) 9);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(level);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 9, 11), sheetFor,book);
		            cell = row.createCell((short) 12);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmName);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 12, 16), sheetFor,book);
		            cell = row.createCell((short) 17);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(AID);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 17, 21), sheetFor,book);
		            cell = row.createCell((short) 22);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(equip);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 22, 26), sheetFor,book);
		            cell = row.createCell((short) 27);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(port);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 27, 31), sheetFor,book);
		            cell = row.createCell((short) 32);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmRate);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 32, 36), sheetFor,book);
		            cell = row.createCell((short) 37);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmType);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 37, 41), sheetFor,book);
		            /*cell = row.createCell((short) 42);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmDescription);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 42, 46), sheetFor,book);
		            cell = row.createCell((short) 47);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmStatus);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 47, 51), sheetFor,book);*/
		            cell = row.createCell((short) 42);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(emsGroup);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 42, 46), sheetFor,book);
		            cell = row.createCell((short) 47);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ems);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 47, 51), sheetFor,book);
		            cell = row.createCell((short) 52);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(room);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 52, 56), sheetFor,book);
		            cell = row.createCell((short) 57);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(station);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 57, 61), sheetFor,book);
		            cell = row.createCell((short) 62);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(cutoverBeforeTime);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 62, 66), sheetFor,book);
		            cell = row.createCell((short) 67);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(cutoverAfterTime);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 67, 71), sheetFor,book);

				}
			}

			/**************************************** 割接期间告警表生成*****************************************/
			// 添加一个工作表
			Sheet sheetFir = book.createSheet();
			book.setSheetName(4,"割接期间告警表");

			/*// 设定行高、列距
			for (int i = 0; i <= 32; i++) {
				sheetFir.setColumnView(i, 5);
				sheetFir.setRowView(i, 300);
			}

			sheetFir.mergeCells(1, 0, 5, 0);
			sheetFir.mergeCells(6, 0, 8, 0);
			sheetFir.mergeCells(9, 0, 13, 0);
			sheetFir.mergeCells(14, 0, 18, 0);
			sheetFir.mergeCells(19, 0, 25, 0);
			sheetFir.mergeCells(26, 0, 32, 0);

			cell = new Label(1, 0, "网元", gridTitle);
			sheetFir.addCell(cell);
			cell = new Label(6, 0, "重要度", gridTitle);
			sheetFir.addCell(cell);
			cell = new Label(9, 0, "告警名称", gridTitle);
			sheetFir.addCell(cell);
			cell = new Label(14, 0, "AID", gridTitle);
			sheetFir.addCell(cell);
			cell = new Label(19, 0, "割接前告警快照时间", gridTitle);
			sheetFir.addCell(cell);
			cell = new Label(26, 0, "割接后告警快照时间", gridTitle);
			sheetFir.addCell(cell);*/

			// 设定行高、列距
			sheetFir.setDefaultColumnWidth((short)4);
			sheetFir.setDefaultRowHeight((short)300);
			
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 6, 8));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 9, 13));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 14, 18));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 19, 23));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 24, 28));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 29, 33));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 34, 38));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 39, 43));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 44, 48));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 49, 53));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 54, 58));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 59, 63));
			sheetFir.addMergedRegion(new CellRangeAddress(0, 0, 64, 68));
			
			row = sheetFir.createRow((short) 0);
            cell = row.createCell((short) 0);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("");
            cell = row.createCell((short) 1);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("网元");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 1, 5), sheetFir,book);
            cell = row.createCell((short) 6);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("重要度");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 6, 8), sheetFir,book);
            cell = row.createCell((short) 9);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警名称");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 9, 13), sheetFir,book);
            cell = row.createCell((short) 14);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("槽道");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 14, 18), sheetFir,book);
            cell = row.createCell((short) 19);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("板卡");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 19, 23), sheetFir,book);
            cell = row.createCell((short) 24);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("端口");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 24, 28), sheetFir,book);
            cell = row.createCell((short) 29);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("速率");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 29, 33), sheetFir,book);
            cell = row.createCell((short) 34);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警类型");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 34, 38), sheetFir,book);
            /*cell = row.createCell((short) 39);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警描述");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 39, 43), sheetFir,book);
            cell = row.createCell((short) 44);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警状态");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 44, 48), sheetFir,book);*/
            cell = row.createCell((short) 39);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("网管分组");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 39, 43), sheetFir,book);
            cell = row.createCell((short) 44);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("网管");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 44, 48), sheetFir,book);
            cell = row.createCell((short) 49);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("机房");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 49, 53), sheetFir,book);
            cell = row.createCell((short) 54);
            cell.setCellStyle(gridTitle);
            cell.setCellValue(FieldNameDefine.STATION_NAME);
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 54, 58), sheetFir,book);
            cell = row.createCell((short) 59);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警发生时间时间");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 59, 63), sheetFir,book);
            cell = row.createCell((short) 64);
            cell.setCellStyle(gridTitle);
            cell.setCellValue("告警消除时间");
            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(0, 0, 64, 68), sheetFir,book);
			
			DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date cutoverStartTime;
			Date cutoverEndTime;

			if (!startTimeCopy.equals("")) {
				cutoverStartTime = fmt.parse(startTimeCopy);
			} else {
				cutoverStartTime = fmt.parse(planStartCopy);
			}

			if (!endTimeCopy.equals("")) {
				cutoverEndTime = fmt.parse(endTimeCopy);
			} else {
				cutoverEndTime = fmt.parse(planEndCopy);
			}

			List<Integer> neIdList = new ArrayList<Integer>();
			// 查询出割接设备
			List<Map> data = getCutoverEquipList(Integer.valueOf(cutoverTaskId));
			// 由于割接设备只允许选择同等级的设备，第一条的设备类型就是该任务所有割接设备的设备类型
			int targetType = (Integer) data.get(0).get("TARGET_TYPE");
			// 该map所有key为网元id，value为网元所属ems的id
			Map neMap = new HashMap();
			// 该用户拥有权限的ems列表，用于过滤ptp
			List<Map> emsListAvailable = commonManagerService
					.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, true, false);
			Map emsIdMap = new HashMap();
			List ports = new ArrayList();
			for (int i = 0, len = emsListAvailable.size(); i < len; i++) {
				emsIdMap.put(emsListAvailable.get(i).get(
						"BASE_EMS_CONNECTION_ID"), 0);
			}
			List<Map> portsWithNeInfo = new ArrayList<Map>();
			// targetType=8,设备类型为端口
			if (CommonDefine.TREE.NODE.PTP == targetType) {
				// 将根据这个list中的端口id查询link表，从而查出所有本端端口id及对端端口的id，同时包含了网管信息，网元信息
				// 可能有冗余信息，通过加到map中用containsKey方法去除重复值
				List cutoverPortIds = new ArrayList();
				for (int i = 0, len = data.size(); i < len; i++) {
					//cutoverPortIds.add(data.get(i).get("TARGET_ID"));
					Map item = new HashMap();
					item.put("BASE_PTP_ID", data.get(i).get("TARGET_ID"));
					cutoverPortIds.add(item);
					// if(!map.containsKey(data.get(i).get("TARGET_ID")))
					// map.put(data.get(i).get("TARGET_ID"), 0);
				}
				portsWithNeInfo = cutoverManagerMapper
						.searchPortsInLink(cutoverPortIds);

			}
			// targetType=99,设备类型为链路
			else if (99 == targetType) {
				List linkIds = new ArrayList();
				for (int i = 0, len = data.size(); i < len; i++) {
					linkIds.add(data.get(i).get("TARGET_ID"));
				}
				portsWithNeInfo = cutoverManagerMapper
						.searchPortsByLink(linkIds);

			}
			// 割接设备为网元，shelf，unit,subunit
			else if (CommonDefine.TREE.NODE.NE == targetType
					|| CommonDefine.TREE.NODE.SHELF == targetType
					|| CommonDefine.TREE.NODE.UNIT == targetType
					|| CommonDefine.TREE.NODE.SUBUNIT == targetType) {
				List equipIds = new ArrayList();
				for (int i = 0, len = data.size(); i < len; i++) {
					equipIds.add(data.get(i).get("TARGET_ID"));
				}
				ports = cutoverManagerMapper.searchPorts(targetType,
						equipIds);
				portsWithNeInfo = cutoverManagerMapper.searchPortsInLink(ports);
			}

			int neId;

			for (int i = 0, len = portsWithNeInfo.size(); i < len; i++) {
				if (!neMap.containsKey(portsWithNeInfo.get(i).get("A_NE_ID"))) {
					if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
							"A_EMS_CONNECTION_ID"))) {
						neMap.put(portsWithNeInfo.get(i).get("A_NE_ID"),
								portsWithNeInfo.get(i).get(
										"A_EMS_CONNECTION_ID"));
						System.out.println(String.valueOf(neMap.keySet()));
					}

				}

				if (!neMap.containsKey(portsWithNeInfo.get(i).get("Z_NE_ID"))) {
					if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
							"Z_EMS_CONNECTION_ID"))) {
						neMap.put(portsWithNeInfo.get(i).get("Z_NE_ID"),
								portsWithNeInfo.get(i).get(
										"Z_EMS_CONNECTION_ID"));
					}
				}

			}
			for (int j = 0, length = ports.size(); j < length; j++) {
				if (!neMap.containsKey(((Map) ports.get(j))
						.get("BASE_NE_ID"))) {
					neMap.put(((Map) ports.get(j)).get("BASE_NE_ID"),
							((Map) ports.get(j)).get("BASE_EMS_CONNECTION_ID"));
				}
			}
			Iterator ite = neMap.entrySet().iterator();
			while (ite.hasNext()) {
				Map.Entry entry = (Map.Entry) ite.next();
				Integer neIdIntValue = (Integer) entry.getKey();
				neIdList.add(neIdIntValue);
			}
			Map<String, Object> currentAlarm = alarmManagementService
					.getCurrentAlarmByNeIdListAndTimeForCutover(neIdList,cutoverStartTime, cutoverEndTime, 0, 999999999);
			List cutoverCurrentAlarmList = (List) currentAlarm.get("rows");
			
			if (cutoverCurrentAlarmList.size() != 0) {
				for (int i = 0; i < cutoverCurrentAlarmList.size(); i++) {
					Map alarm = (Map) cutoverCurrentAlarmList.get(i);
					ne = String.valueOf(alarm.get("NE_NAME"));
					category = String.valueOf(alarm.get("ALARM_CATEGORY"));
					level = String.valueOf(alarm.get("PERCEIVED_SEVERITY"));
					// 告警等级前台显示转换
					if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT;
					}
					alarmName = String.valueOf(alarm.get("NATIVE_PROBABLE_CAUSE"));
				    AID = String.valueOf(alarm.get("SLOT_DISPLAY_NAME"));
				    equip = String.valueOf(alarm.get("UNIT_NAME"));
				    port = String.valueOf(alarm.get("PORT_NAME"));
				    alarmRate = String.valueOf(alarm.get("INTERFACE_RATE"));
				    alarmType = String.valueOf(alarm.get("ALARM_TYPE"));
				   //告警类型前台显示转换
					if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION;
					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE;
					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT;
					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE;
					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT;
					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY;
					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION_VALUE
							.equals(alarmType)) {
						alarmType = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION;
					}
				    /*alarmDescription = String.valueOf(alarm.get("PORT_NO"));
				    alarmStatus = String.valueOf(alarm.get("PORT_NO"));*/
				    emsGroup = String.valueOf(alarm.get("EMS_GROUP_NAME"));
				    ems = String.valueOf(alarm.get("EMS_NAME"));
				    room = String.valueOf(alarm.get("RESOURCE_ROOM"));
				    station = String.valueOf(alarm.get("DISPLAY_STATION"));
					cutoverBeforeTime = String.valueOf(alarm.get("FIRST_TIME"));
					cutoverAfterTime = String.valueOf(alarm.get("CLEAR_TIME"));
					
				    
				    sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 1, 5));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 6, 8));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 9, 13));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 14, 18));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 19, 23));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 24, 28));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 29, 33));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 34, 38));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 39, 43));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 44, 48));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 49, 53));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 54, 58));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 59, 63));
					sheetFir.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 64, 68));
					
					row = sheetFir.createRow((short) i + 1);
		            cell = row.createCell((short) 0);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(String.valueOf(i + 1));
		            cell = row.createCell((short) 1);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ne);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 1, 5), sheetFir,book);
		            cell = row.createCell((short) 6);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(level);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 6, 8), sheetFir,book);
		            cell = row.createCell((short) 9);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmName);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 9, 13), sheetFir,book);
		            cell = row.createCell((short) 14);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(AID);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 14, 18), sheetFir,book);
		            cell = row.createCell((short) 19);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(equip);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 19, 23), sheetFir,book);
		            cell = row.createCell((short) 24);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(port);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 24, 28), sheetFir,book);
		            cell = row.createCell((short) 29);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmRate);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 29, 33), sheetFir,book);
		            cell = row.createCell((short) 34);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(alarmType);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 34, 38), sheetFir,book);
		            cell = row.createCell((short) 39);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(emsGroup);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 39, 43), sheetFir,book);
		            cell = row.createCell((short) 44);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(ems);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 44, 48), sheetFir,book);
		            cell = row.createCell((short) 49);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(room);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 49, 53), sheetFir,book);
		            cell = row.createCell((short) 54);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(station);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 54, 58), sheetFir,book);
		            cell = row.createCell((short) 59);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(cutoverBeforeTime);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 59, 63), sheetFir,book);
		            cell = row.createCell((short) 64);
		            cell.setCellStyle(gridFormat);
		            cell.setCellValue(cutoverAfterTime);
		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 1, i + 1, 64, 68), sheetFir,book);

				}
			}
			/**************************************** 评估表生成*****************************************/
			// 添加一个工作表
			Sheet sheetEvaluation = book.createSheet();
			int currentRow =0;
			book.setSheetName(5,"评估结果表");
			Integer pmBeforeCount = cutoverManagerMapper
					.searchPmValueBeforeCount(cutoverTaskId);
			Integer alarmBeforeCount = cutoverManagerMapper
					.searchAlarmBeforeCount(cutoverTaskId);
			Integer pmAfterCount = cutoverManagerMapper
			.searchPmValueAfterCount(cutoverTaskId);
			Integer alarmAfterCount = cutoverManagerMapper
			.searchAlarmAfterCount(cutoverTaskId);
			String commentBefore = "";
			String commentAfter = "";
			//TODO现在只判断性能，告警，以后还会增加倒换事件的判断
			if(pmBeforeCount==0 && alarmBeforeCount==0)
			{
				commentBefore = "割接前评估结果：未发现异常，可以正常进行割接。";
			}
			else if(pmBeforeCount==0 && alarmBeforeCount!=0)
			{
				commentBefore = "割接前评估结果：发现告警异常，请对异常状态进行确认，谨慎进行割接。";
			}
			else if(pmBeforeCount!=0 && alarmBeforeCount==0)
			{
				commentBefore = "割接前评估结果：发现性能异常，请对异常状态进行确认，谨慎进行割接。";
			}
			else if(pmBeforeCount!=0 && alarmBeforeCount!=0)
			{
				commentBefore = "割接前评估结果：发现性能异常、告警异常，请对异常状态进行确认，谨慎进行割接。";
			}
			
			commentAfter = "割接后评估结果：经过检测，本次割接的量化评分为";
			commentAfter = commentAfter+score+"分，";
			commentAfter = commentAfter+"评估为"+evaluation;
			// 设定行高、列距
			sheetEvaluation.setDefaultColumnWidth((short)4);
			sheetEvaluation.setDefaultRowHeight((short)300);
			//割接前评估评语
			row = sheetEvaluation.createRow((short) 0);
			sheetEvaluation.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
            cell = row.createCell((short) 0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(commentBefore);
            currentRow = currentRow+1;
            if(pmBeforeCount!=0)
            {
            	//性能异常标题
                row = sheetEvaluation.createRow((short) 1);
                cell = row.createCell((short) 0);
//                cell.setCellStyle(titleStyle);
                cell.setCellValue("性能异常");
                currentRow = currentRow+1;
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 1, 5));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 6, 10));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 11, 14));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 15, 19));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 20, 24));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 25, 27));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 28, 32));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(2, 2, 33, 37));
    			
    			row = sheetEvaluation.createRow((short) 2);
    			
                cell = row.createCell((short) 0);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("");
                cell = row.createCell((short) 1);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网元");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 1, 5), sheetEvaluation,book); 
                cell = row.createCell((short) 6);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("端口");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 6, 10), sheetEvaluation,book);
                cell = row.createCell((short) 11);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("端口性能名称");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 11, 14), sheetEvaluation,book);
                cell = row.createCell((short) 15);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接前快照值");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 15, 19), sheetEvaluation,book);
                cell = row.createCell((short) 20);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接后快照值");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 20, 24), sheetEvaluation,book);
                cell = row.createCell((short) 25);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("差值");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 25, 27), sheetEvaluation,book);
                cell = row.createCell((short) 28);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接前快照时间");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 28, 32), sheetEvaluation,book);
                cell = row.createCell((short) 33);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接后快照时间");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(2, 2, 33, 37), sheetEvaluation,book);
                currentRow = currentRow+1;
                String pmNeBefore = "";
    			String pmPtpBefore = "";
    			String pmDescriptionBefore = "";
    			String pmBeforeValueBefore = "";
    			String pmAfterValueBefore = "";
    			String differenceBefore = "";
    			String sanpshotTimeBeforeBefore = "";
    			String sanpshotTimeAfterBefore = "";

    			// 读取割接前异常性能值信息
    			
    			List<Map> cutoverPMBeforeList = new ArrayList<Map>();
    			cutoverPMBeforeList = cutoverManagerMapper.searchPmValueBefore(cutoverTaskId,
    					-1, -1);

    			if (cutoverPMBeforeList.size() != 0) {
    				for (int i = 0; i < cutoverPMBeforeList.size(); i++) {
    					Map pm = cutoverPMBeforeList.get(i);
    					pmNeBefore = String.valueOf(pm.get("DISPLAY_NE"));
    					pmPtpBefore = String.valueOf(pm.get("DISPLAY_PORT_DESC"));
    					pmDescriptionBefore = String.valueOf(pm.get("PM_DESCRIPTION"));
    					pmBeforeValueBefore = String.valueOf(pm.get("VALUE_BEFORE"));
    					
    					sanpshotTimeBeforeBefore = String.valueOf(pm.get("TIME_BEFORE"));
    					

    		            sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 1, 5));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 6, 10));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 11, 14));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 15, 19));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 20, 24));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 25, 27));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 28, 32));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 33, 37));
    					
    					row = sheetEvaluation.createRow((short) i + 3);
    		            cell = row.createCell((short) 0);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(String.valueOf(i + 1));
    		            cell = row.createCell((short) 1);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmNeBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 1, 5), sheetEvaluation,book);
    		            cell = row.createCell((short) 6);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmPtpBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 6, 10), sheetEvaluation,book);
    		            cell = row.createCell((short) 11);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmDescriptionBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 11, 14), sheetEvaluation,book);
    		            cell = row.createCell((short) 15);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmBeforeValueBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 15, 19), sheetEvaluation,book);
    		            cell = row.createCell((short) 20);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmAfterValueBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 20, 24), sheetEvaluation,book);
    		            cell = row.createCell((short) 25);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(differenceBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 25, 27), sheetEvaluation,book);
    		            cell = row.createCell((short) 28);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(sanpshotTimeBeforeBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 28, 32), sheetEvaluation,book);
    		            cell = row.createCell((short) 33);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(sanpshotTimeAfterBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + 3, i + 3, 33, 37), sheetEvaluation,book);
    		            
    		            currentRow = currentRow+1;

    				}
    			}
            }
            if(alarmBeforeCount!=0)
            {
            	//告警异常标题
                row = sheetEvaluation.createRow(currentRow);
                cell = row.createCell((short) 0);
//                cell.setCellStyle(titleStyle);
                cell.setCellValue("告警异常");
                currentRow = currentRow+1;
                
                sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 1, 5));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 6, 10));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 11, 14));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 15, 19));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 20, 24));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 25, 27));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 28, 32));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 33, 37));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 38, 42));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 43, 47));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 48, 52));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 53, 57));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 58, 62));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 63, 67));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 68, 72));
    			
    			row = sheetEvaluation.createRow(currentRow);
    			
    			cell = row.createCell((short) 0);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("");
                cell = row.createCell((short) 1);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网元");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 1, 5), sheetEvaluation,book);
                cell = row.createCell((short) 6);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("类型");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 6, 10), sheetEvaluation,book);
                cell = row.createCell((short) 11);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("重要度");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 11, 14), sheetEvaluation,book);
                cell = row.createCell((short) 15);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("告警名称");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 15, 19), sheetEvaluation,book);
                cell = row.createCell((short) 20);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("槽道");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 20, 24), sheetEvaluation,book);
                cell = row.createCell((short) 25);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("板卡");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 25, 27), sheetEvaluation,book);
                cell = row.createCell((short) 28);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("端口");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 28, 32), sheetEvaluation,book);
                cell = row.createCell((short) 33);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("速率");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 33, 37), sheetEvaluation,book);
                cell = row.createCell((short) 38);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("告警类型");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 38, 42), sheetEvaluation,book);
               
                cell = row.createCell((short) 43);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网管分组");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 43, 47), sheetEvaluation,book);
                cell = row.createCell((short) 48);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网管");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 48, 52), sheetEvaluation,book);
                cell = row.createCell((short) 53);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("机房");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 53, 57), sheetEvaluation,book);
                cell = row.createCell((short) 58);
                cell.setCellStyle(gridTitle);
                cell.setCellValue(FieldNameDefine.STATION_NAME);
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 58, 62), sheetEvaluation,book);
                cell = row.createCell((short) 63);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接前告警快照时间");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 63, 67), sheetEvaluation,book);
                cell = row.createCell((short) 68);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接后告警快照时间");  
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 68, 72), sheetEvaluation,book);
                currentRow = currentRow+1;
                
                
            	String neAlarmBefore = "";
    			String categoryAlarmBefore = "";
    			String levelAlarmBefore = "";
    			String alarmNameAlarmBefore = "";
    			String AIDAlarmBefore = "";
    			String equipAlarmBefore = "";
    			String portAlarmBefore = "";
    			String alarmRateAlarmBefore = "";
    			String alarmTypeAlarmBefore = "";
    			String alarmDescriptionAlarmBefore = "";
    			String alarmStatusAlarmBefore = "";
    			String emsGroupAlarmBefore = "";
    			String emsAlarmBefore = "";
    			String roomAlarmBefore = "";
    			String stationAlarmBefore = "";
    			String cutoverBeforeTimeAlarmBefore = "";
    			String cutoverAfterTimeAlarmBefore = "";

    			List<Map> cutoverAlarmBeforeList = new ArrayList<Map>();
    			cutoverAlarmBeforeList = cutoverManagerMapper.searchAlarmBefore(
    					cutoverTaskId,-1, -1);

    			if (cutoverAlarmBeforeList.size() != 0) {
    				int currentRowNow = currentRow;
    				for (int i = 0; i < cutoverAlarmBeforeList.size(); i++) {
    					
    					Map alarm = cutoverAlarmBeforeList.get(i);
    					neAlarmBefore = String.valueOf(alarm.get("NE_NAME"));
    					categoryAlarmBefore = String.valueOf(alarm.get("ALARM_CATEGORY"));
    					// 告警类型前台显示转换
    					if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.UNCHANGED_VALUE
    							.equals(categoryAlarmBefore)) {
    						categoryAlarmBefore = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.UNCHANGED;
    					} else if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.CLEAR_VALUE
    							.equals(categoryAlarmBefore)) {
    						categoryAlarmBefore = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.CLEAR;
    					} else if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.ADD_VALUE
    							.equals(categoryAlarmBefore)) {
    						categoryAlarmBefore = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.ADD;
    					}
    					levelAlarmBefore = String.valueOf(alarm.get("PERCEIVED_SEVERITY"));
    					// 告警等级前台显示转换
    					if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT_VALUE
    							.equals(levelAlarmBefore)) {
    						levelAlarmBefore = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT;
    					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT_VALUE
    							.equals(levelAlarmBefore)) {
    						levelAlarmBefore = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT;
    					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR_VALUE
    							.equals(levelAlarmBefore)) {
    						levelAlarmBefore = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR;
    					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT_VALUE
    							.equals(levelAlarmBefore)) {
    						levelAlarmBefore = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT;
    					}
    					
    					alarmNameAlarmBefore = String.valueOf(alarm.get("NATIVE_PROBABLE_CAUSE"));
    					//保存槽道显示名称
    					AIDAlarmBefore = String.valueOf(alarm.get("SLOT_DISPLAY_NAME"));
    					equipAlarmBefore = String.valueOf(alarm.get("UNIT_NAME"));
    					portAlarmBefore = String.valueOf(alarm.get("PORT_NO"));
    					alarmRateAlarmBefore = String.valueOf(alarm.get("INTERFACE_RATE"));
    					
    					alarmTypeAlarmBefore = String.valueOf(alarm.get("ALARM_TYPE"));
    					//告警类型前台显示转换
    					if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION_VALUE
    							.equals(alarmTypeAlarmBefore)) {
    						alarmTypeAlarmBefore = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION;
    					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE_VALUE
    							.equals(alarmTypeAlarmBefore)) {
    						alarmTypeAlarmBefore = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE;
    					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT_VALUE
    							.equals(alarmTypeAlarmBefore)) {
    						alarmTypeAlarmBefore = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT;
    					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE_VALUE
    							.equals(alarmTypeAlarmBefore)) {
    						alarmTypeAlarmBefore = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE;
    					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT_VALUE
    							.equals(alarmTypeAlarmBefore)) {
    						alarmTypeAlarmBefore = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT;
    					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY_VALUE
    							.equals(alarmTypeAlarmBefore)) {
    						alarmTypeAlarmBefore = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY;
    					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION_VALUE
    							.equals(alarmTypeAlarmBefore)) {
    						alarmTypeAlarmBefore = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION;
    					}
    					//alarmDescription = String.valueOf(alarm.get("ALARM_DESCRIPTION"));
    					// 告警状态前台显示转换
    					//alarmStatus = String.valueOf(alarm.get("ALARM_STATUS"));
    					
    					emsGroupAlarmBefore = String.valueOf(alarm.get("EMS_GROUP_NAME"));
    					emsAlarmBefore = String.valueOf(alarm.get("NATIVE_EMS_NAME"));
    					roomAlarmBefore = String.valueOf(alarm.get("ROOM"));
    					stationAlarmBefore = String.valueOf(alarm.get("STATION"));
    					cutoverBeforeTimeAlarmBefore = String.valueOf(alarm.get("SNAPSHOT_TIME_BEFORE"));
    					cutoverAfterTimeAlarmBefore = String.valueOf(alarm.get("SNAPSHOT_TIME_AFTER"));

    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 1, 5));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 6, 10));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 11, 14));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 15, 19));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 20, 24));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 25, 27));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 28, 32));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 33, 37));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 38, 42));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 43, 47));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 48, 52));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 53, 57));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 58, 62));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 63, 67));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 68, 72));
    					
    					row = sheetEvaluation.createRow((short) i + currentRowNow);
    		            cell = row.createCell((short) 0);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(String.valueOf(i + 1));
    		            cell = row.createCell((short) 1);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(neAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 1, 5), sheetEvaluation,book);
    		            cell = row.createCell((short) 6);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(categoryAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 6, 10), sheetEvaluation,book);
    		            cell = row.createCell((short) 11);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(levelAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 11, 14), sheetEvaluation,book);
    		            cell = row.createCell((short) 15);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(alarmNameAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 15, 19), sheetEvaluation,book);
    		            cell = row.createCell((short) 20);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(AIDAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 20, 24), sheetEvaluation,book);
    		            cell = row.createCell((short) 25);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(equipAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 25, 27), sheetEvaluation,book);
    		            cell = row.createCell((short) 28);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(portAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 28, 32), sheetEvaluation,book);
    		            cell = row.createCell((short) 33);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(alarmRateAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 33, 37), sheetEvaluation,book);
    		            cell = row.createCell((short) 38);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(alarmTypeAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 38, 42), sheetEvaluation,book);
    		            cell = row.createCell((short) 43);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(emsGroupAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 43, 47), sheetEvaluation,book);
    		            cell = row.createCell((short) 48);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(emsAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 48, 52), sheetEvaluation,book);
    		            cell = row.createCell((short) 53);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(roomAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 53, 57), sheetEvaluation,book);
    		            cell = row.createCell((short) 58);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(stationAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 58, 62), sheetEvaluation,book);
    		            cell = row.createCell((short) 63);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(cutoverBeforeTimeAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 63, 67), sheetEvaluation,book);
    		            cell = row.createCell((short) 68);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(cutoverAfterTimeAlarmBefore);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 68, 72), sheetEvaluation,book);
    		            currentRow = currentRow+1;
    				}
    			}
            }
            //前评估后后评估之间空一行
            currentRow = currentRow+1;
          //割接后评估评语
			row = sheetEvaluation.createRow(currentRow);
			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 12));
            cell = row.createCell((short) 0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(commentAfter);
            currentRow = currentRow+1;
            if(pmAfterCount!=0)
            {
            	//性能异常标题
                row = sheetEvaluation.createRow(currentRow);
                cell = row.createCell((short) 0);
//                cell.setCellStyle(titleStyle);
                cell.setCellValue("性能异常");
                currentRow = currentRow+1;
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 1, 5));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 6, 10));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 11, 14));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 15, 19));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 20, 24));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 25, 27));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 28, 32));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 33, 37));
    			
    			row = sheetEvaluation.createRow(currentRow);
    			
                cell = row.createCell((short) 0);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("");
                cell = row.createCell((short) 1);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网元");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 1, 5), sheetEvaluation,book); 
                cell = row.createCell((short) 6);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("端口");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 6, 10), sheetEvaluation,book);
                cell = row.createCell((short) 11);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("端口性能名称");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 11, 14), sheetEvaluation,book);
                cell = row.createCell((short) 15);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接前快照值");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 15, 19), sheetEvaluation,book);
                cell = row.createCell((short) 20);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接后快照值");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 20, 24), sheetEvaluation,book);
                cell = row.createCell((short) 25);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("差值");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 25, 27), sheetEvaluation,book);
                cell = row.createCell((short) 28);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接前快照时间");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 28, 32), sheetEvaluation,book);
                cell = row.createCell((short) 33);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接后快照时间");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 33, 37), sheetEvaluation,book);
                currentRow = currentRow+1;
                String pmNeAfter = "";
    			String pmPtpAfter = "";
    			String pmDescriptionAfter = "";
    			String pmBeforeValueAfter = "";
    			String pmAfterValueAfter = "";
    			String differenceAfter = "";
    			String sanpshotTimeBeforeAfter = "";
    			String sanpshotTimeAfterAfter = "";

    			// 读取割接后异常性能值信息
    			
    			List<Map> cutoverPMAfterList = new ArrayList<Map>();
    			cutoverPMAfterList = cutoverManagerMapper.searchPmValueAfter(cutoverTaskId,
    					-1, -1);

    			if (cutoverPMAfterList.size() != 0) {
    				int currentRowNow = currentRow;
    				for (int i = 0; i < cutoverPMAfterList.size(); i++) {
    					
    					Map pm = cutoverPMAfterList.get(i);
    					pmNeAfter = String.valueOf(pm.get("DISPLAY_NE"));
    					pmPtpAfter = String.valueOf(pm.get("DISPLAY_PORT_DESC"));
    					pmDescriptionAfter = String.valueOf(pm.get("PM_DESCRIPTION"));
    					pmBeforeValueAfter = String.valueOf(pm.get("VALUE_BEFORE"));
    					pmAfterValueAfter = String.valueOf(pm.get("VALUE_AFTER"));
    					if (!pmBeforeValueAfter.equals("null")) {
    						differenceAfter = new BigDecimal(pmAfterValueAfter).subtract(new BigDecimal(pmBeforeValueAfter)).toString();

						}
    					sanpshotTimeBeforeAfter = String.valueOf(pm.get("TIME_BEFORE"));
    					sanpshotTimeAfterAfter = String.valueOf(pm.get("TIME_AFTER"));

    		            sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 1, 5));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 6, 10));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 11, 14));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 15, 19));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 20, 24));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 25, 27));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 28, 32));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 33, 37));
    					
    					row = sheetEvaluation.createRow((short) i + currentRowNow);
    		            cell = row.createCell((short) 0);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(String.valueOf(i + 1));
    		            cell = row.createCell((short) 1);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmNeAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 1, 5), sheetEvaluation,book);
    		            cell = row.createCell((short) 6);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmPtpAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 6, 10), sheetEvaluation,book);
    		            cell = row.createCell((short) 11);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmDescriptionAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 11, 14), sheetEvaluation,book);
    		            cell = row.createCell((short) 15);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmBeforeValueAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 15, 19), sheetEvaluation,book);
    		            cell = row.createCell((short) 20);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(pmAfterValueAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 20, 24), sheetEvaluation,book);
    		            cell = row.createCell((short) 25);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(differenceAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 25, 27), sheetEvaluation,book);
    		            cell = row.createCell((short) 28);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(sanpshotTimeBeforeAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 28, 32), sheetEvaluation,book);
    		            cell = row.createCell((short) 33);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(sanpshotTimeAfterAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 33, 37), sheetEvaluation,book);
    		            
    		            currentRow = currentRow+1;

    				}
    			}
            }
            if(alarmAfterCount!=0)
            {
            	//告警异常标题
                row = sheetEvaluation.createRow(currentRow);
                cell = row.createCell((short) 0);
//                cell.setCellStyle(titleStyle);
                cell.setCellValue("告警异常");
                currentRow = currentRow+1;
                
                sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 1, 5));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 6, 10));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 11, 14));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 15, 19));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 20, 24));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 25, 27));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 28, 32));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 33, 37));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 38, 42));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 43, 47));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 48, 52));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 53, 57));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 58, 62));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 63, 67));
    			sheetEvaluation.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 68, 72));
    			
    			row = sheetEvaluation.createRow(currentRow);
    			
    			cell = row.createCell((short) 0);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("");
                cell = row.createCell((short) 1);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网元");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 1, 5), sheetEvaluation,book);
                cell = row.createCell((short) 6);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("类型");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 6, 10), sheetEvaluation,book);
                cell = row.createCell((short) 11);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("重要度");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 11, 14), sheetEvaluation,book);
                cell = row.createCell((short) 15);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("告警名称");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 15, 19), sheetEvaluation,book);
                cell = row.createCell((short) 20);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("槽道");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 20, 24), sheetEvaluation,book);
                cell = row.createCell((short) 25);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("板卡");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 25, 27), sheetEvaluation,book);
                cell = row.createCell((short) 28);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("端口");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 28, 32), sheetEvaluation,book);
                cell = row.createCell((short) 33);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("速率");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 33, 37), sheetEvaluation,book);
                cell = row.createCell((short) 38);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("告警类型");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 38, 42), sheetEvaluation,book);
               
                cell = row.createCell((short) 43);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网管分组");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 43, 47), sheetEvaluation,book);
                cell = row.createCell((short) 48);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("网管");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 48, 52), sheetEvaluation,book);
                cell = row.createCell((short) 53);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("机房");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 53, 57), sheetEvaluation,book);
                cell = row.createCell((short) 58);
                cell.setCellStyle(gridTitle);
                cell.setCellValue(FieldNameDefine.STATION_NAME);
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 58, 62), sheetEvaluation,book);
                cell = row.createCell((short) 63);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接前告警快照时间");
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 63, 67), sheetEvaluation,book);
                cell = row.createCell((short) 68);
                cell.setCellStyle(gridTitle);
                cell.setCellValue("割接后告警快照时间");  
                setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(currentRow, currentRow, 68, 72), sheetEvaluation,book);
                currentRow = currentRow+1;
                
                
            	String neAlarmAfter = "";
    			String categoryAlarmAfter = "";
    			String levelAlarmAfter = "";
    			String alarmNameAlarmAfter = "";
    			String AIDAlarmAfter = "";
    			String equipAlarmAfter = "";
    			String portAlarmAfter = "";
    			String alarmRateAlarmAfter = "";
    			String alarmTypeAlarmAfter = "";
    			String alarmDescriptionAlarmAfter = "";
    			String alarmStatusAlarmAfter = "";
    			String emsGroupAlarmAfter = "";
    			String emsAlarmAfter = "";
    			String roomAlarmAfter = "";
    			String stationAlarmAfter = "";
    			String cutoverBeforeTimeAlarmAfter = "";
    			String cutoverAfterTimeAlarmAfter = "";

    			List<Map> cutoverAlarmAfterList = new ArrayList<Map>();
    			cutoverAlarmAfterList = cutoverManagerMapper.searchAlarmAfter(
    					cutoverTaskId,-1, -1);

    			if (cutoverAlarmAfterList.size() != 0) {
    				int currentRowNow = currentRow;
    				for (int i = 0; i < cutoverAlarmAfterList.size(); i++) {
    					
    					Map alarm = cutoverAlarmAfterList.get(i);
    					neAlarmAfter = String.valueOf(alarm.get("NE_NAME"));
    					categoryAlarmAfter = String.valueOf(alarm.get("ALARM_CATEGORY"));
    					// 告警类型前台显示转换
    					if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.UNCHANGED_VALUE
    							.equals(categoryAlarmAfter)) {
    						categoryAlarmAfter = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.UNCHANGED;
    					} else if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.CLEAR_VALUE
    							.equals(categoryAlarmAfter)) {
    						categoryAlarmAfter = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.CLEAR;
    					} else if (CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.ADD_VALUE
    							.equals(categoryAlarmAfter)) {
    						categoryAlarmAfter = CommonDefine.CUTOVER.CUTOVER_CATEGORY_VALUE.ADD;
    					}
    					levelAlarmAfter = String.valueOf(alarm.get("PERCEIVED_SEVERITY"));
    					// 告警等级前台显示转换
    					if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT_VALUE
    							.equals(levelAlarmAfter)) {
    						levelAlarmAfter = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT;
    					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT_VALUE
    							.equals(levelAlarmAfter)) {
    						levelAlarmAfter = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT;
    					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR_VALUE
    							.equals(levelAlarmAfter)) {
    						levelAlarmAfter = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR;
    					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT_VALUE
    							.equals(levelAlarmAfter)) {
    						levelAlarmAfter = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT;
    					}
    					
    					alarmNameAlarmAfter = String.valueOf(alarm.get("NATIVE_PROBABLE_CAUSE"));
    					//保存槽道显示名称
    					AIDAlarmAfter = String.valueOf(alarm.get("SLOT_DISPLAY_NAME"));
    					equipAlarmAfter = String.valueOf(alarm.get("UNIT_NAME"));
    					portAlarmAfter = String.valueOf(alarm.get("PORT_NO"));
    					alarmRateAlarmAfter = String.valueOf(alarm.get("INTERFACE_RATE"));
    					
    					alarmTypeAlarmAfter = String.valueOf(alarm.get("ALARM_TYPE"));
    					//告警类型前台显示转换
    					if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION_VALUE
    							.equals(alarmTypeAlarmAfter)) {
    						alarmTypeAlarmAfter = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.COMMUNICATION;
    					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE_VALUE
    							.equals(alarmTypeAlarmAfter)) {
    						alarmTypeAlarmAfter = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SERVICE;
    					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT_VALUE
    							.equals(alarmTypeAlarmAfter)) {
    						alarmTypeAlarmAfter = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.EQUIPMENT;
    					} else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE_VALUE
    							.equals(alarmTypeAlarmAfter)) {
    						alarmTypeAlarmAfter = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.HANDLE;
    					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT_VALUE
    							.equals(alarmTypeAlarmAfter)) {
    						alarmTypeAlarmAfter = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.ENVIRONMENT;
    					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY_VALUE
    							.equals(alarmTypeAlarmAfter)) {
    						alarmTypeAlarmAfter = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.SAFETY;
    					}else if (CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION_VALUE
    							.equals(alarmTypeAlarmAfter)) {
    						alarmTypeAlarmAfter = CommonDefine.CUTOVER.CUTOVER_TYPE_VALUE.CONNECTION;
    					}
    					//alarmDescription = String.valueOf(alarm.get("ALARM_DESCRIPTION"));
    					// 告警状态前台显示转换
    					//alarmStatus = String.valueOf(alarm.get("ALARM_STATUS"));
    					
    					emsGroupAlarmAfter = String.valueOf(alarm.get("EMS_GROUP_NAME"));
    					emsAlarmAfter = String.valueOf(alarm.get("NATIVE_EMS_NAME"));
    					roomAlarmAfter = String.valueOf(alarm.get("ROOM"));
    					stationAlarmAfter = String.valueOf(alarm.get("STATION"));
    					cutoverBeforeTimeAlarmAfter = String.valueOf(alarm.get("SNAPSHOT_TIME_BEFORE"));
    					cutoverAfterTimeAlarmAfter = String.valueOf(alarm.get("SNAPSHOT_TIME_AFTER"));

    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 1, 5));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 6, 10));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 11, 14));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 15, 19));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 20, 24));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 25, 27));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 28, 32));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 33, 37));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 38, 42));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 43, 47));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 48, 52));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 53, 57));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 58, 62));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 63, 67));
    					sheetEvaluation.addMergedRegion(new CellRangeAddress(i + currentRowNow, i + currentRowNow, 68, 72));
    					
    					row = sheetEvaluation.createRow((short) i + currentRowNow);
    		            cell = row.createCell((short) 0);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(String.valueOf(i + 1));
    		            cell = row.createCell((short) 1);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(neAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 1, 5), sheetEvaluation,book);
    		            cell = row.createCell((short) 6);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(categoryAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 6, 10), sheetEvaluation,book);
    		            cell = row.createCell((short) 11);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(levelAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 11, 14), sheetEvaluation,book);
    		            cell = row.createCell((short) 15);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(alarmNameAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 15, 19), sheetEvaluation,book);
    		            cell = row.createCell((short) 20);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(AIDAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 20, 24), sheetEvaluation,book);
    		            cell = row.createCell((short) 25);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(equipAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 25, 27), sheetEvaluation,book);
    		            cell = row.createCell((short) 28);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(portAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 28, 32), sheetEvaluation,book);
    		            cell = row.createCell((short) 33);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(alarmRateAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 33, 37), sheetEvaluation,book);
    		            cell = row.createCell((short) 38);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(alarmTypeAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 38, 42), sheetEvaluation,book);
    		            cell = row.createCell((short) 43);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(emsGroupAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 43, 47), sheetEvaluation,book);
    		            cell = row.createCell((short) 48);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(emsAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 48, 52), sheetEvaluation,book);
    		            cell = row.createCell((short) 53);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(roomAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 53, 57), sheetEvaluation,book);
    		            cell = row.createCell((short) 58);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(stationAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 58, 62), sheetEvaluation,book);
    		            cell = row.createCell((short) 63);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(cutoverBeforeTimeAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 63, 67), sheetEvaluation,book);
    		            cell = row.createCell((short) 68);
    		            cell.setCellStyle(gridFormat);
    		            cell.setCellValue(cutoverAfterTimeAlarmAfter);
    		            setRegionBorder(XSSFCellStyle.BORDER_THIN, new CellRangeAddress(i + currentRowNow, i + currentRowNow, 68, 72), sheetEvaluation,book);
    		            currentRow = currentRow+1;
    				}
    			}
            }
			/*workbook.write();
			workbook.close(); // 一定要关闭, 否则没有保存Excel
			 */			
			FileOutputStream out = new FileOutputStream(fileFullName);
			book.write(out);
			out.close();

		} /*catch (RowsExceededException e) {
			System.out.println("jxl write RowsExceededException: "
					+ e.getMessage());
		} catch (WriteException e) {
			System.out.println("jxl write WriteException: " + e.getMessage());
		}*/ catch (IOException e) {
			System.out.println("jxl write file i/o exception!, cause by: "
					+ e.getMessage());
		}/* catch (CommonException e) {
			System.out.println("jxl write CommonException!, cause by: "
					+ e.getMessage());
		} */catch (ParseException e) {
			System.out.println("jxl write ParseException!, cause by: "
					+ e.getMessage());
		} catch (Exception e) {
			System.out.println("jxl write ParseException!, cause by: "
					+ e.getMessage());
		}

	}

	public CommonResult downLoadReport(int cutoverTaskId)
			throws CommonException {
		CommonResult result = new CommonResult();
		try {
			String taskName = "";
			String reportName = "";
			String planStart = "";
			String startTime = "";

			// 读取割接任务信息
			List<Map> cutoverTaskList = new ArrayList<Map>();
			cutoverTaskList = cutoverManagerMapper
					.getCutoverTaskList(cutoverTaskId);

			if (cutoverTaskList.size() != 0) {
				Map task = cutoverTaskList.get(0);
				taskName = String.valueOf(task.get("TASK_NAME"));
				reportName = taskName + "报告";
				planStart = String.valueOf(task.get("START_TIME"));
			}

			// 读取割接任务Param信息
			List<Map> taskParamList = new ArrayList<Map>();
			taskParamList = cutoverManagerMapper
					.getCutoverTaskParamList(cutoverTaskId);

			if (taskParamList.size() != 0) {
				for (int i = 0; i < taskParamList.size(); i++) {
					Map taskParam = taskParamList.get(i);
					String key = String.valueOf(taskParam.get("PARAM_NAME"));
					String value = String.valueOf(taskParam.get("PARAM_VALUE"));
					if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.START_TIME_ACTUAL
							.equals(key)) {
						startTime = value;
					}

				}
			}

			if (startTime.equals("")) {
				startTime = planStart;
			}

			// SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd
			// HH:mm:ss");
			SimpleDateFormat dbtime = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
			Date start = dbtime.parse(startTime);
			// reportName = new String(reportName.getBytes(), "ISO8859_1");
			String fileName = reportName + "_" + time.format(start);
			// String fileName = reportName +"_"+ startTime;
			String filePath = CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "/"
					+ CommonDefine.EXCEL.CUTOVER_BASE + "/" + fileName + ".xlsx";

			// filePath = new String(filePath.getBytes(), "utf8");
			if (filePath != null&&new File(filePath).exists()&&new File(filePath).isFile()) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(filePath);
			} else {
				result.setReturnMessage(filePath);
				result.setReturnResult(CommonDefine.FAILED);
			}

		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			System.out.println("jxl write ParseException!, cause by: "
					+ e.getMessage());
		} /*
			 * catch (UnsupportedEncodingException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */
		return result;
	}

	/**
	 * 查询割接期间告警
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public Map getAlarmsDuringCutover(String cutoverTaskId, int userId,
			int start, int limit) throws CommonException {
		List<Integer> neIdList = new ArrayList<Integer>();
		// 查询出割接设备
		List<Map> data = getCutoverEquipList(Integer.valueOf(cutoverTaskId));
		// 由于割接设备只允许选择同等级的设备，第一条的设备类型就是该任务所有割接设备的设备类型
		int targetType = (Integer) data.get(0).get("TARGET_TYPE");
		// 该map所有key为网元id，value为网元所属ems的id
		Map neMap = new HashMap();
		// 该用户拥有权限的ems列表，用于过滤ptp
		List<Map> emsListAvailable = commonManagerService
				.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, true, false);
		Map emsIdMap = new HashMap();
		List ports = new ArrayList();
		for (int i = 0, len = emsListAvailable.size(); i < len; i++) {
			emsIdMap.put(emsListAvailable.get(i).get("BASE_EMS_CONNECTION_ID"),
					0);
		}
		List<Map> portsWithNeInfo = new ArrayList<Map>();
		// targetType=8,设备类型为端口
		if (CommonDefine.TREE.NODE.PTP == targetType) {
			// 将根据这个list中的端口id查询link表，从而查出所有本端端口id及对端端口的id，同时包含了网管信息，网元信息
			// 可能有冗余信息，通过加到map中用containsKey方法去除重复值
			List cutoverPortIds = new ArrayList();
			for (int i = 0, len = data.size(); i < len; i++) {
				//cutoverPortIds.add(data.get(i).get("TARGET_ID"));
				Map item = new HashMap();
				item.put("BASE_PTP_ID", data.get(i).get("TARGET_ID"));
				cutoverPortIds.add(item);
				
				// if(!map.containsKey(data.get(i).get("TARGET_ID")))
				// map.put(data.get(i).get("TARGET_ID"), 0);
			}
			portsWithNeInfo = cutoverManagerMapper
					.searchPortsInLink(cutoverPortIds);

		}
		// targetType=99,设备类型为链路
		else if (99 == targetType) {
			List linkIds = new ArrayList();
			for (int i = 0, len = data.size(); i < len; i++) {
				linkIds.add(data.get(i).get("TARGET_ID"));
			}
			portsWithNeInfo = cutoverManagerMapper.searchPortsByLink(linkIds);

		}
		// 割接设备为网元，shelf，unit,subunit
		else if (CommonDefine.TREE.NODE.NE == targetType
				|| CommonDefine.TREE.NODE.SHELF == targetType
				|| CommonDefine.TREE.NODE.UNIT == targetType
				|| CommonDefine.TREE.NODE.SUBUNIT == targetType) {
			List equipIds = new ArrayList();
			for (int i = 0, len = data.size(); i < len; i++) {
				equipIds.add(data.get(i).get("TARGET_ID"));
			}
			ports = cutoverManagerMapper.searchPorts(targetType, equipIds);
			portsWithNeInfo = cutoverManagerMapper.searchPortsInLink(ports);
		}
		for (int i = 0, len = portsWithNeInfo.size(); i < len; i++) {
			if (!neMap.containsKey(portsWithNeInfo.get(i).get("A_NE_ID"))) {
				if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
						"A_EMS_CONNECTION_ID"))) {
					neMap.put(portsWithNeInfo.get(i).get("A_NE_ID"),
							portsWithNeInfo.get(i).get("A_EMS_CONNECTION_ID"));
					System.out.println(String.valueOf(neMap.keySet()));
				}

			}

			if (!neMap.containsKey(portsWithNeInfo.get(i).get("Z_NE_ID"))) {
				if (emsIdMap.containsKey(portsWithNeInfo.get(i).get(
						"Z_EMS_CONNECTION_ID"))) {
					neMap.put(portsWithNeInfo.get(i).get("Z_NE_ID"),
							portsWithNeInfo.get(i).get("Z_EMS_CONNECTION_ID"));
				}
			}

		}
		for (int j = 0, length = ports.size(); j < length; j++) {
			if (!neMap.containsKey(((Map) ports.get(j))
					.get("BASE_NE_ID"))) {
				neMap.put(((Map) ports.get(j)).get("BASE_NE_ID"),
						((Map) ports.get(j)).get("BASE_EMS_CONNECTION_ID"));
			}
		}
		Iterator ite = neMap.entrySet().iterator();
		while (ite.hasNext()) {
			Map.Entry entry = (Map.Entry) ite.next();
			Integer neIdIntValue = (Integer) entry.getKey();
			neIdList.add(neIdIntValue);
		}
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 读取割接任务信息
		List<Map> cutoverTaskList = new ArrayList<Map>();
		cutoverTaskList = cutoverManagerMapper.getCutoverTaskList(Integer
				.valueOf(cutoverTaskId));
		String planStart = "";
		String planEnd = "";
		String startTime = "";
		String endTime = "";
		if (cutoverTaskList.size() != 0) {
			Map task = cutoverTaskList.get(0);

			planStart = String.valueOf(task.get("START_TIME"));
			planEnd = String.valueOf(task.get("END_TIME"));
		}

		// 读取割接任务Param信息
		List<Map> taskParamList = new ArrayList<Map>();
		taskParamList = cutoverManagerMapper.getCutoverTaskParamList(Integer
				.valueOf(cutoverTaskId));
		if (taskParamList.size() != 0) {
			for (int i = 0; i < taskParamList.size(); i++) {
				Map taskParam = taskParamList.get(i);
				String key = String.valueOf(taskParam.get("PARAM_NAME"));
				String value = String.valueOf(taskParam.get("PARAM_VALUE"));
				if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.START_TIME_ACTUAL
						.equals(key)) {
					startTime = value;

				} else if (CommonDefine.CUTOVER.CUTOVER_PARAM_NAME.END_TIME_ACTUAL
						.equals(key)) {
					endTime = value;
				}
			}
		}
			Date cutoverStartTime = new Date();
			Date cutoverEndTime = new Date();

			try {
				if (!startTime.equals("")) {
					cutoverStartTime = fmt.parse(startTime);
				} else {
					cutoverStartTime = fmt.parse(planStart);
				}

				if (!endTime.equals("")) {
					cutoverEndTime = fmt.parse(endTime);
				} else {
					cutoverEndTime = fmt.parse(planEnd);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, Object> currentAlarm = alarmManagementService
					.getCurrentAlarmByNeIdListAndTimeForCutover(neIdList,
							cutoverStartTime, cutoverEndTime, start, limit);
			int total = (Integer) currentAlarm.get("total");
			List cutoverCurrentAlarmList = (List) currentAlarm.get("rows");
			if (total != 0) {
				for (int i = 0; i < total; i++) {
					Map alarm = (Map) cutoverCurrentAlarmList.get(i);
					String ne = String.valueOf(alarm.get("NE_NAME"));
					// category = String.valueOf(alarm.get("ALARM_CATEGORY"));
					String level = String.valueOf(alarm
							.get("PERCEIVED_SEVERITY"));
					// 告警等级前台显示转换
					if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.URGENT;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.IMPORTANT;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.MINOR;
					} else if (CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT_VALUE
							.equals(level)) {
						level = CommonDefine.CUTOVER.CUTOVER_LEVEL_VALUE.PROMPT;
					}
					String alarmName = String.valueOf(alarm
							.get("NATIVE_PROBABLE_CAUSE"));
					// AID = String.valueOf(alarm.get("PORT_NO"));
					String cutoverBeforeTime = String.valueOf(alarm
							.get("FIRST_TIME"));
					String cutoverAfterTime = String.valueOf(alarm
							.get("CLEAR_TIME"));
					
				}
			}
		
		return currentAlarm;
	}
	
	private static void setRegionBorder(int border, CellRangeAddress region, Sheet sheet,Workbook wb){  
        RegionUtil.setBorderBottom(border,region, sheet, wb);  
        RegionUtil.setBorderLeft(border,region, sheet, wb);  
        RegionUtil.setBorderRight(border,region, sheet, wb);  
        RegionUtil.setBorderTop(border,region, sheet, wb);  
    }  

	@SuppressWarnings("unchecked")
	public String exportExcel(int cutoverTaskId, int flag)
			throws CommonException {
		String resultMessage = "";
		String name = "";
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

		switch (flag) {
		case 1: {
			flag = CommonDefine.EXCEL.CUTOVER_PERFORMANCE_HEADER;
			name = "性能清单";
		}
			break;
		case 2: {
			flag = CommonDefine.EXCEL.CUTOVER_ALARM_HEADER;
			name = "告警清单";
		}
			break;
		case 3: {
			flag = CommonDefine.EXCEL.CUTOVER_CIRCUIT_HEADER;
			name = "电路清单";
		}
			break;
		}
		String fileName = "割接任务" + "_" + name + "_"
				+ formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",
				1000);
		Map map = new HashMap();
		try {
			List dataList = new ArrayList();
			if (flag == CommonDefine.EXCEL.CUTOVER_PERFORMANCE_HEADER)
				dataList = cutoverManagerMapper.searchPmValue(Integer
						.valueOf(cutoverTaskId), -1, -1);
			else if (flag == CommonDefine.EXCEL.CUTOVER_ALARM_HEADER)
			{
				dataList = cutoverManagerMapper.getCurrentAlarms(Integer
						.valueOf(cutoverTaskId), 1/* 1表示全部告警 */, -1, -1);
				for(int i=0,len=dataList.size();i<len;i++)
				{
					Map alarm = (Map)dataList.get(i);
					if(alarm.get("ALARM_CATEGORY")!=null &&((String)alarm.get("ALARM_CATEGORY")).equals("1"))
					{
						alarm.remove("ALARM_CATEGORY");
						alarm.put("ALARM_CATEGORY", "不变");
					}
					else if(alarm.get("ALARM_CATEGORY")!=null &&((String)alarm.get("ALARM_CATEGORY")).equals("2"))
					{
						alarm.remove("ALARM_CATEGORY");
						alarm.put("ALARM_CATEGORY", "消除");
					}
					else if(alarm.get("ALARM_CATEGORY")!=null &&((String)alarm.get("ALARM_CATEGORY")).equals("3"))
					{
						alarm.remove("ALARM_CATEGORY");
						alarm.put("ALARM_CATEGORY", "新增");
					}
					if(((String)alarm.get("PERCEIVED_SEVERITY")).equals("1"))
					{
						alarm.remove("PERCEIVED_SEVERITY");
						alarm.put("PERCEIVED_SEVERITY", "紧急");
					}
					else if(((String)alarm.get("PERCEIVED_SEVERITY")).equals("2"))	
					{
						alarm.remove("PERCEIVED_SEVERITY");
						alarm.put("PERCEIVED_SEVERITY", "重要");
					}
					else if(((String)alarm.get("PERCEIVED_SEVERITY")).equals("3"))	
					{
						alarm.remove("PERCEIVED_SEVERITY");
						alarm.put("PERCEIVED_SEVERITY", "次要");
					}
					else if(((String)alarm.get("PERCEIVED_SEVERITY")).equals("4"))	
					{
						alarm.remove("PERCEIVED_SEVERITY");
						alarm.put("PERCEIVED_SEVERITY", "提示");
					}
					
					if(alarm.get("ALARM_TYPE")!=null &&((String)alarm.get("ALARM_TYPE")).equals("0"))
					{
						alarm.remove("ALARM_TYPE");
						alarm.put("ALARM_TYPE", "通信");
					}
					else if(alarm.get("ALARM_TYPE")!=null &&((String)alarm.get("ALARM_TYPE")).equals("1"))
					{
						alarm.remove("ALARM_TYPE");
						alarm.put("ALARM_TYPE", "服务");
					}
					else if(alarm.get("ALARM_TYPE")!=null &&((String)alarm.get("ALARM_TYPE")).equals("2"))
					{
						alarm.remove("ALARM_TYPE");
						alarm.put("ALARM_TYPE", "设备");
					}
					else if(alarm.get("ALARM_TYPE")!=null &&((String)alarm.get("ALARM_TYPE")).equals("3"))
					{
						alarm.remove("ALARM_TYPE");
						alarm.put("ALARM_TYPE", "处理");
					}
					else if(alarm.get("ALARM_TYPE")!=null &&((String)alarm.get("ALARM_TYPE")).equals("4"))
					{
						alarm.remove("ALARM_TYPE");
						alarm.put("ALARM_TYPE", "环境");
					}
					else if(alarm.get("ALARM_TYPE")!=null &&((String)alarm.get("ALARM_TYPE")).equals("5"))
					{
						alarm.remove("ALARM_TYPE");
						alarm.put("ALARM_TYPE", "安全");
					}
					else if(alarm.get("ALARM_TYPE")!=null &&((String)alarm.get("ALARM_TYPE")).equals("6"))
					{
						alarm.remove("ALARM_TYPE");
						alarm.put("ALARM_TYPE", "连接");
					}

				}
			}
				
			else if (flag == CommonDefine.EXCEL.CUTOVER_CIRCUIT_HEADER) {
				Map conditionMap = new HashMap();
				conditionMap.put("VALUE", "*");
				conditionMap.put("NAME", "T_SYS_TASK_INFO");
				conditionMap.put("ID_NAME", "SYS_TASK_ID");
				conditionMap.put("ID_VALUE", cutoverTaskId);
				List<Map> equipList = new ArrayList<Map>();
				equipList = cutoverManagerMapper.getByParameter(conditionMap);
				Map firstEquip = equipList.get(0);
				String equipType = ((Integer) firstEquip.get("TARGET_TYPE"))
						.toString();
				List nodeList = new ArrayList();
				if (!equipType.equals("99")) {
					for (int i = 0; i < equipList.size(); i++) {
						nodeList.add(equipList.get(i).get("TARGET_ID"));
					}
				} else {
					equipType = "8";
					List<Map> linkList = cutoverManagerMapper.getCutoverEqptLinkList(cutoverTaskId);
					for (int i = 0; i < linkList.size(); i++) {
						nodeList.add(linkList.get(i).get("A_END_PTP"));
						nodeList.add(linkList.get(i).get("Z_END_PTP"));
					}
				}

				map.put("start", 0);
				map.put("limit", 999999999);
				map.put("nodeLevel", equipType);
				map.put("nodeList", nodeList);
				dataList = (List) circuitManagerService.selectAllCircuitAbout(map)
				.get("rows");
				for(int i=0,len=dataList.size();i<len;i++)
				{
					Map circuit = (Map)dataList.get(i);
					if(circuit.get("IS_COMPLETE_CIR")!=null && 0 == (Integer)circuit.get("IS_COMPLETE_CIR"))
					{
						circuit.remove("IS_COMPLETE_CIR");
						circuit.put("IS_COMPLETE_CIR", "不完整");
					}
					else if(circuit.get("IS_COMPLETE_CIR")!=null && 1 == (Integer)circuit.get("IS_COMPLETE_CIR"))
					{
						circuit.remove("IS_COMPLETE_CIR");
						circuit.put("IS_COMPLETE_CIR", "完整");
					}
				}
			}
			
			// 导出数据
			resultMessage = ex2.writeExcel(dataList, flag, false);
		} catch (Exception e) {
			e.printStackTrace();
			ex2.close();
			return resultMessage;
		}
		return resultMessage;
	}
	/**
	 * 查询割接任务参数
	 * @param cutoverTaskId
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map> getCutoverTaskParameter(int cutoverTaskId) throws CommonException
	{
		List taskParameterList = new ArrayList();
		try {
			taskParameterList = cutoverManagerMapper.getCutoverTaskParameter(cutoverTaskId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskParameterList;
	}
	
	private int getLevel(int level, double value) {
		double level1 = PmMessageHandler.getPmMessage("level1");
		double level2 = PmMessageHandler.getPmMessage("level2");
		double level3 = PmMessageHandler.getPmMessage("level3");
		if (value < level1) {
			return CommonDefine.PM.MUL.SEC_PM_ZC;
		} else if (value < level2) {
			return CommonDefine.PM.MUL.SEC_PM_YB;
		} else if (value < level3) {
			return CommonDefine.PM.MUL.SEC_PM_CY;
		} else {
			return CommonDefine.PM.MUL.SEC_PM_ZY;
		}

	}

	//割接任务创建时的冲突检测
	@SuppressWarnings("unchecked")
	private Map cutoverPreCheck(List<String> cutoverEquipList,List<Map> currentCircuitList)
			throws CommonException {
		Map returnMap = new HashMap();
		List<Map> existingCircuitList = new ArrayList();
		existingCircuitList = cutoverManagerMapper.searchCircuitOfUnfinishedTask();
		//将两个list中重复的电路id通过下面的方法放入一个新的list
		//电路分为普通电路和OTN电路，需要两个list来存放
		List duplicateCircuitIdList = new ArrayList();
		List duplicateOTNCircuitIdList = new ArrayList();
		//普通电路
		Map<Integer, Integer> compareMap = new HashMap<Integer, Integer>();
		for (Map m : existingCircuitList) {
			if(1==(Integer)m.get("CIRCUIT_TYPE"))
				compareMap.put((Integer) m.get("CIRCUIT_ID"), 1);
		}
		for (Map m : currentCircuitList) {
			if (compareMap.get(Integer.valueOf(m.get("CIR_CIRCUIT_INFO_ID").toString())) != null) {
				compareMap.put(Integer.valueOf(m.get("CIR_CIRCUIT_INFO_ID").toString()), 2);
			}
		}
		for (Map.Entry<Integer, Integer> entry : compareMap.entrySet()) {
			if (entry.getValue() == 2) {
				duplicateCircuitIdList.add(entry.getKey());
			}
		}
		//OTN电路
		Map<Integer, Integer> compareMap1 = new HashMap<Integer, Integer>();
		for (Map m : existingCircuitList) {
			if(2==(Integer)m.get("CIRCUIT_TYPE"))
				compareMap1.put((Integer) m.get("CIRCUIT_ID"), 1);
		}
		for (Map m : currentCircuitList) {
			if (compareMap1.get(Integer.valueOf(m.get("CIR_OTN_CIRCUIT_INFO_ID").toString()) ) != null) {
				compareMap1.put(Integer.valueOf(m.get("CIR_OTN_CIRCUIT_INFO_ID").toString()) , 2);
			}
		}
		for (Map.Entry<Integer, Integer> entry : compareMap1.entrySet()) {
			if (entry.getValue() == 2) {
				duplicateOTNCircuitIdList.add(entry.getKey());
			}
		}
		returnMap.put("duplicateCircuitIdList", duplicateCircuitIdList);
		returnMap.put("duplicateOTNCircuitIdList", duplicateOTNCircuitIdList);
		return returnMap;
	}
	
	/**
	 * 查询割接前异常性能（割接前评估用）
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public Map<String, Object> searchPmValueBefore(int cutoverTaskId,
			int start, int limit) throws CommonException {
		Map resultMap = new HashMap();
		List pmList = cutoverManagerMapper
		.searchPmValueBefore(cutoverTaskId, start, limit);
		int total = cutoverManagerMapper.searchPmValueBeforeCount(cutoverTaskId);
		resultMap.put("rows", pmList);
		resultMap.put("total", total);
		return resultMap;
	}

	/**
	 * 查询割接前异常告警（割接前评估用）
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public Map<String, Object> searchAlarmBefore(int cutoverTaskId, int start,
			int limit) throws CommonException {
		Map resultMap = new HashMap();
		List alarmList = cutoverManagerMapper
		.searchAlarmBefore(cutoverTaskId, start, limit);
		int total = cutoverManagerMapper.searchAlarmBeforeCount(cutoverTaskId);
		resultMap.put("rows", alarmList);
		resultMap.put("total", total);
		return resultMap;
	}

	/**
	 * 查询割接前异常倒换（割接前评估用）
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public Map<String, Object> searchEventBefore(int cutoverTaskId, int start,
			int limit) throws CommonException {
		Map resultMap = new HashMap();
//		List eventList = cutoverManagerMapper
//		.searchEventBefore(cutoverTaskId, start, limit);
//		int total = cutoverManagerMapper.searchEventBeforeCount(cutoverTaskId);
//		resultMap.put("rows", eventList);
//		resultMap.put("total", total);
		return resultMap;
	}
	
	/**
	 * 查询割接后异常性能（割接后评估用）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchPmValueAfter(int cutoverTaskId, int start,
			int limit) throws CommonException {
		Map resultMap = new HashMap();
		List pmList = cutoverManagerMapper
		.searchPmValueAfter(cutoverTaskId, start, limit);
		int total = cutoverManagerMapper.searchPmValueAfterCount(cutoverTaskId);
		resultMap.put("rows", pmList);
		resultMap.put("total", total);
		return resultMap;
	}

	/**
	 * 查询割接后异常告警（割接后评估用）
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public Map<String, Object> searchAlarmAfter(int cutoverTaskId, int start,
			int limit) throws CommonException {
		Map resultMap = new HashMap();
		List<Map> alarmList = cutoverManagerMapper
		.searchAlarmAfter(cutoverTaskId, start, limit);
		Map<String, Object> config = getEvaluationConfig();
		for(int i=0,len=alarmList.size();i<len;i++)
		{
			Map alarm = alarmList.get(i);
			int score = calculateSingleAlarmScore(alarm,config);
			alarm.put("EVALUATION_SCORE", score);
		}
		
		int total = cutoverManagerMapper.searchAlarmAfterCount(cutoverTaskId);
		resultMap.put("rows", alarmList);
		resultMap.put("total", total);
		return resultMap;
	}

	/**
	 * 查询割接后异常倒换事件（割接后评估用）
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public Map<String, Object> searchEventAfter(int cutoverTaskId, int start,
			int limit) throws CommonException {
		Map resultMap = new HashMap();
//		List eventList = cutoverManagerMapper
//		.searchEventAfter(cutoverTaskId, start, limit);
//		int total = cutoverManagerMapper.searchEventAfterCount(cutoverTaskId);
//		resultMap.put("rows", eventList);
//		resultMap.put("total", total);
		return resultMap;
	}
	/**
	 * 查询评估参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getEvaluationConfig() throws CommonException {
		Map<String, Object> resultMap = new HashMap();
		List<Map> rows = new ArrayList();
//		HttpServletRequest request = ServletActionContext.getRequest();
        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try {
        	filePath = java.net.URLDecoder.decode(filePath,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
//		File inputXml = new File(filePath+"WEB-INF/classes/resourceConfig/systemConfig/cutoverEvaluation.xml");
		File inputXml = new File(filePath+"resourceConfig/systemConfig/cutoverEvaluation.xml");
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(inputXml);
			Element items = document.getRootElement();
			for (Iterator i = items.elementIterator(); i.hasNext();) {
				Element item = (Element) i.next();
				String key = "";
				String value = "";
				// 每个对象
				for (Iterator j = item.elementIterator(); j.hasNext();) {
					Element node = (Element) j.next();
					if(node.getName().equals("key"))
						key= node.getText();
					if(node.getName().equals("value"))
						value= node.getText();
					
				}
				if(!key.contains("一个"))
				{
					resultMap.put(key, value);
				}
				else
				{
					Map map = new HashMap();
					map.put("key", key);
					map.put("value", value);
					rows.add(map);
				}
		    }
			System.out.print(11);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultMap.put("rows", rows);
		return resultMap;
	}
	/**
	 * 修改评估参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> modifyEvaluationConfig(Map<String, String> searchCondition) throws CommonException {
		Map<String, Object> resultMap = new HashMap();
		HttpServletRequest request = ServletActionContext.getRequest();
		String filePath = CommonDefine.PATH_ROOT;
		File inputXml = new File(filePath+"WEB-INF/classes/resourceConfig/systemConfig/cutoverEvaluation.xml");
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(inputXml);
			Element items = document.getRootElement();
			for (Iterator i = items.elementIterator(); i.hasNext();) {
				Element item = (Element) i.next();
				String key = "";
				String value = "";
				// 每个对象
				for (Iterator j = item.elementIterator(); j.hasNext();) {
					Element node = (Element) j.next();
					if(node.getName().equals("key"))
						key= node.getText();
					if(node.getName().equals("value"))
						value= node.getText();
					
				}
				if(key.equals("perfectUpperBound"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("perfectUpperBound"));
						
					}
				}
				else if(key.equals("excellentLowerBound"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("excellentLowerBound"));
						
					}
				}
				else if(key.equals("excellentUpperBound"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("excellentUpperBound"));
						
					}
				}
				else if(key.equals("goodLowerBound"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("goodLowerBound"));
						
					}
				}
				else if(key.equals("goodUpperBound"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("goodUpperBound"));
						
					}
				}
				else if(key.equals("averageLowerBound"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("averageLowerBound"));
						
					}
				}
				else if(key.equals("averageUpperBound"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("averageUpperBound"));
						
					}
				}
				else if(key.equals("badLowerBounder"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("badLowerBounder"));
						
					}
				}
				else if(key.equals("SDHDifferent"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("SDHDifferent"));
						
					}
				}
				else if(key.equals("WDMDifferent"))
				{
					for (Iterator j = item.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						if(node.getName().equals("value"))
							node.setText(searchCondition.get("WDMDifferent"));
						
					}
				}
				
		    }
			java.io.Writer wr=new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath+"WEB-INF/classes/resourceConfig/systemConfig/cutoverEvaluation.xml"),"UTF-8");   
		    document.write(wr);   
		    wr.close();
			System.out.print(11);
			resultMap.put("returnResult",CommonDefine.SUCCESS );
			resultMap.put("returnMessage", "修改成功！");
		} catch (Exception e) {
			resultMap.put("returnResult",CommonDefine.FAILED );
			resultMap.put("returnMessage", "修改失败！");
			e.printStackTrace();
		}
		return resultMap;
	}
	/**
	 * 获取割接评估得分
	 * @param cutoverTaskId
	 * @return
	 * @throws CommonException 
	 */
	private Integer evaluate(String cutoverTaskId) throws CommonException
	{
		Integer score = 0;
		//1.查出所有性能项目，查询出评估参数，用评估参数判断每一条性能值的得分
		//2.查出所有告警，用评估参数判断每一条新增告警值的得分
		List<Map> pmList = cutoverManagerMapper.searchPmValue(Integer.valueOf(cutoverTaskId),-1,-1);
		List<Map> alarmList = cutoverManagerMapper.getCurrentAlarms(Integer.valueOf(cutoverTaskId),1,-1,-1);
		Map<String, Object> config = getEvaluationConfig();
		Integer pmAlarm1 =0;
		Integer pmAlarm2 =0;
		Integer pmAlarm3 =0;
		Integer pmImprove =0;
		Integer pmWorse =0;
		Integer alarmCRSTM =0;
		Integer alarmMJSTM =0;
		Integer alarmMNSTM =0;
		Integer alarmNTSTM =0;
		Integer alarmCRNONSTM =0;
		Integer alarmMJNONSTM =0;
		Integer alarmMNNONSTM =0;
		Integer alarmNTNONSTM =0;
		Integer event =0;
		Double SDHDifferent = 0d;
		Double WDMDifferent = 0d;
		SDHDifferent = Double.valueOf((String)config.get("SDHDifferent"));
		WDMDifferent = Double.valueOf((String)config.get("WDMDifferent"));
		for(int i=0,len=((List)config.get("rows")).size();i<len;i++)
		{
			Map map = (Map)((List)config.get("rows")).get(i);
			String key = (String)map.get("key");
			String value = (String)map.get("value");
			if(key.equals("一个性能项重要预警"))
				pmAlarm3 = Integer.valueOf(value);
			else if(key.equals("一个性能项次要预警"))
				pmAlarm2 = Integer.valueOf(value);
			else if(key.equals("一个性能项一般预警"))
				pmAlarm1 = Integer.valueOf(value);
			else if(key.equals("一个性能差值改善判定"))
				pmImprove = Integer.valueOf(value);
			else if(key.equals("一个性能差值劣化判定"))
				pmWorse = Integer.valueOf(value);
			else if(key.equals("一个割接期间紧急告警（STM端口）"))
				alarmCRSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间重要告警（STM端口）"))
				alarmMJSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间次要告警（STM端口）"))
				alarmMNSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间提示告警（STM端口）"))
				alarmNTSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间紧急告警（非STM端口）"))
				alarmCRNONSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间重要告警（非STM端口）"))
				alarmMJNONSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间次要告警（非STM端口）"))
				alarmMNNONSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间提示告警（非STM端口）"))
				alarmNTNONSTM = Integer.valueOf(value);
			else if(key.equals("一个割接期间未恢复的倒换事件"))
				event = Integer.valueOf(value);
		}
		for(int i=0,len=pmList.size();i<len;i++)
		{
			Map pm = pmList.get(i);
			if((Integer)pm.get("EXCEPTION_LV_AFTER")==3)
			{
				score = score+pmAlarm3;
			}
			else if((Integer)pm.get("EXCEPTION_LV_AFTER")==2)
			{
				score = score+pmAlarm2;
			}
			else if((Integer)pm.get("EXCEPTION_LV_AFTER")==1)
			{
				score = score+pmAlarm1;
			}
			else if((Integer)pm.get("EXCEPTION_LV_AFTER")==0)
			{
				//SDH性能
				if((Integer)pm.get("DOMAIN")==1)
				{
					if(pm.get("VALUE_BEFORE")!=null && (String)pm.get("VALUE_BEFORE")!="")
					{
						BigDecimal valueBefore = new BigDecimal((String)pm.get("VALUE_BEFORE"));
						BigDecimal valueAfter = new BigDecimal((String)pm.get("VALUE_AFTER"));
						BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
						Double differenceDoble = difference.doubleValue();
						if(differenceDoble-SDHDifferent>0)
							score = score+pmImprove;
						else if(differenceDoble+SDHDifferent<0)
							score = score+pmWorse;
					}
					
					
					
				}
				//WDM性能
				else if((Integer)pm.get("DOMAIN")==2)
				{
					if(pm.get("VALUE_BEFORE")!=null && (String)pm.get("VALUE_BEFORE")!="")
					{
						BigDecimal valueBefore = new BigDecimal((String)pm.get("VALUE_BEFORE"));
						BigDecimal valueAfter = new BigDecimal((String)pm.get("VALUE_AFTER"));
						BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
						Double differenceDoble = difference.doubleValue();
						if(differenceDoble-WDMDifferent>0)
							score = score+pmImprove;
						else if(differenceDoble+WDMDifferent<0)
							score = score+pmWorse;
					}
				}
			}
		}
		for(int i=0,len=alarmList.size();i<len;i++)
		{
			Map alarm = alarmList.get(i);
			if(alarm.get("ALARM_CATEGORY")!=null && alarm.get("ALARM_CATEGORY").equals("3"))
			{
				if(alarm.get("PTP_TYPE")!=null && ((String)alarm.get("PTP_TYPE")).contains("STM"))
				{
					if(alarm.get("PERCEIVED_SEVERITY").equals("1"))
						score = score + alarmCRSTM;
					else if(alarm.get("PERCEIVED_SEVERITY").equals("2"))
						score = score + alarmMJSTM;
					else if(alarm.get("PERCEIVED_SEVERITY").equals("3"))
						score = score + alarmMNSTM;
					else if(alarm.get("PERCEIVED_SEVERITY").equals("4"))
						score = score + alarmNTSTM;
				}
				else
				{
					if(alarm.get("PERCEIVED_SEVERITY").equals("1"))
						score = score + alarmCRNONSTM;
					else if(alarm.get("PERCEIVED_SEVERITY").equals("2"))
						score = score + alarmMJNONSTM;
					else if(alarm.get("PERCEIVED_SEVERITY").equals("3"))
						score = score + alarmMNNONSTM;
					else if(alarm.get("PERCEIVED_SEVERITY").equals("4"))
						score = score + alarmNTNONSTM;
				}
			}
		}
		return score;
	}
	/**
	 * 计算单条pm评估得分
	 * @param pm
	 * @param config
	 * @return
	 */
	private Integer calculateSinglePMScore(Map pm,Map config)
	{
		Integer score = 0;
		Integer pmAlarm1 =0;
		Integer pmAlarm2 =0;
		Integer pmAlarm3 =0;
		Integer pmImprove =0;
		Integer pmWorse =0;
//		Integer alarmCRSTM =0;
//		Integer alarmMJSTM =0;
//		Integer alarmMNSTM =0;
//		Integer alarmNTSTM =0;
//		Integer alarmCRNONSTM =0;
//		Integer alarmMJNONSTM =0;
//		Integer alarmMNNONSTM =0;
//		Integer alarmNTNONSTM =0;
//		Integer event =0;
		Double SDHDifferent = 0d;
		Double WDMDifferent = 0d;
		SDHDifferent = Double.valueOf((String)config.get("SDHDifferent"));
		WDMDifferent = Double.valueOf((String)config.get("WDMDifferent"));
		for(int i=0,len=((List)config.get("rows")).size();i<len;i++)
		{
			Map map = (Map)((List)config.get("rows")).get(i);
			String key = (String)map.get("key");
			String value = (String)map.get("value");
			if(key.equals("一个性能项重要预警"))
				pmAlarm3 = Integer.valueOf(value);
			else if(key.equals("一个性能项次要预警"))
				pmAlarm2 = Integer.valueOf(value);
			else if(key.equals("一个性能项一般预警"))
				pmAlarm1 = Integer.valueOf(value);
			else if(key.equals("一个性能差值改善判定"))
				pmImprove = Integer.valueOf(value);
			else if(key.equals("一个性能差值劣化判定"))
				pmWorse = Integer.valueOf(value);
//			else if(key.equals("一个割接期紧急告警(STM端口)"))
//				alarmCRSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期重要告警(STM端口)"))
//				alarmMJSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期次要告警(STM端口)"))
//				alarmMNSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期提示告警(STM端口)"))
//				alarmNTSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期紧急告警(非STM端口)"))
//				alarmCRNONSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期重要告警(非STM端口)"))
//				alarmMJNONSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期次要告警(非STM端口)"))
//				alarmMNNONSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期提示告警(非STM端口)"))
//				alarmNTNONSTM = Integer.valueOf(value);
//			else if(key.equals("一个割接期未恢复的倒换事件"))
//				event = Integer.valueOf(value);
		}
		if((Integer)pm.get("EXCEPTION_LV_AFTER")==3)
		{
			score = pmAlarm3;
		}
		else if((Integer)pm.get("EXCEPTION_LV_AFTER")==2)
		{
			score = pmAlarm2;
		}
		else if((Integer)pm.get("EXCEPTION_LV_AFTER")==1)
		{
			score = pmAlarm1;
		}
		else if((Integer)pm.get("EXCEPTION_LV_AFTER")==0)
		{
			//SDH性能
			if((Integer)pm.get("DOMAIN")==1)
			{
				if(pm.get("VALUE_BEFORE")!=null && (String)pm.get("VALUE_BEFORE")!="")
				{
					BigDecimal valueBefore = new BigDecimal((String)pm.get("VALUE_BEFORE"));
					BigDecimal valueAfter = new BigDecimal((String)pm.get("VALUE_AFTER"));
					BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
					Double differenceDoble = difference.doubleValue();
					if(differenceDoble-SDHDifferent>0)
						score = pmImprove;
					else if(differenceDoble+SDHDifferent<0)
						score = pmWorse;
				}
				
			}
			//WDM性能
			else if((Integer)pm.get("DOMAIN")==2)
			{
				if(pm.get("VALUE_BEFORE")!=null && (String)pm.get("VALUE_BEFORE")!="")
				{
					BigDecimal valueBefore = new BigDecimal((String)pm.get("VALUE_BEFORE"));
					BigDecimal valueAfter = new BigDecimal((String)pm.get("VALUE_AFTER"));
					BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
					Double differenceDoble = difference.doubleValue();
					if(differenceDoble-WDMDifferent>0)
						score = pmImprove;
					else if(differenceDoble+WDMDifferent<0)
						score = pmWorse;
				}
			}
		}
		return score;
	}
	//计算单条告警的评估分值
	private Integer calculateSingleAlarmScore(Map alarm,Map config)
	{
		Integer score = 0;
		Integer alarmCRSTM = 0;
		Integer alarmMJSTM = 0;
		Integer alarmMNSTM = 0;
		Integer alarmNTSTM = 0;
		Integer alarmCRNONSTM = 0;
		Integer alarmMJNONSTM = 0;
		Integer alarmMNNONSTM = 0;
		Integer alarmNTNONSTM = 0;
		for (int i = 0, len = ((List) config.get("rows")).size(); i < len; i++) {
			Map map = (Map) ((List) config.get("rows")).get(i);
			String key = (String) map.get("key");
			String value = (String) map.get("value");
			if (key.equals("一个割接期紧急告警(STM端口)"))
				alarmCRSTM = Integer.valueOf(value);
			else if (key.equals("一个割接期重要告警(STM端口)"))
				alarmMJSTM = Integer.valueOf(value);
			else if (key.equals("一个割接期次要告警(STM端口)"))
				alarmMNSTM = Integer.valueOf(value);
			else if (key.equals("一个割接期提示告警(STM端口)"))
				alarmNTSTM = Integer.valueOf(value);
			else if (key.equals("一个割接期紧急告警(非STM端口)"))
				alarmCRNONSTM = Integer.valueOf(value);
			else if (key.equals("一个割接期重要告警(非STM端口)"))
				alarmMJNONSTM = Integer.valueOf(value);
			else if (key.equals("一个割接期次要告警(非STM端口)"))
				alarmMNNONSTM = Integer.valueOf(value);
			else if (key.equals("一个割接期提示告警(非STM端口)"))
				alarmNTNONSTM = Integer.valueOf(value);
		}

		if (alarm.get("ALARM_CATEGORY") != null
				&& alarm.get("ALARM_CATEGORY").equals("3")) {
			if (alarm.get("PTP_TYPE") != null
					&& ((String) alarm.get("PTP_TYPE")).contains("STM")) {
				if (alarm.get("PERCEIVED_SEVERITY").equals("1"))
					score =  alarmCRSTM;
				else if (alarm.get("PERCEIVED_SEVERITY").equals("2"))
					score =  alarmMJSTM;
				else if (alarm.get("PERCEIVED_SEVERITY").equals("3"))
					score =  alarmMNSTM;
				else if (alarm.get("PERCEIVED_SEVERITY").equals("4"))
					score =  alarmNTSTM;
			} else {
				if (alarm.get("PERCEIVED_SEVERITY").equals("1"))
					score =  alarmCRNONSTM;
				else if (alarm.get("PERCEIVED_SEVERITY").equals("2"))
					score =  alarmMJNONSTM;
				else if (alarm.get("PERCEIVED_SEVERITY").equals("3"))
					score =  alarmMNNONSTM;
				else if (alarm.get("PERCEIVED_SEVERITY").equals("4"))
					score =  alarmNTNONSTM;
			}
		}
		
		return score;
	}
	/**
	 * 检查是否有非零的性能差值
	 * @return
	 */
	private Boolean needUpdateCompareValue(String cutoverTaskId)
	{
		Boolean needUpdateCompareValue = false;
		List<Map> pmList = cutoverManagerMapper.searchPmValue(Integer.valueOf(cutoverTaskId),-1,-1);
		for(int i=0,len=pmList.size();i<len;i++)
		{
			Map pm = pmList.get(i);
			if(pm.get("VALUE_BEFORE")!=null && (String)pm.get("VALUE_BEFORE")!="")
			{
				BigDecimal valueBefore = new BigDecimal((String)pm.get("VALUE_BEFORE"));
				BigDecimal valueAfter = new BigDecimal((String)pm.get("VALUE_AFTER"));
				BigDecimal difference = valueAfter.subtract(valueBefore).setScale(0,BigDecimal.ROUND_HALF_UP);
				if(!difference.equals(new BigDecimal("0")))
				{
					needUpdateCompareValue = true;
					break;
				}
			}
			
		}
		return needUpdateCompareValue;
	}
	
	/**
	 * 存在差值端口性能值查询
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchPmValueWithDifference(int cutoverTaskId)
			throws CommonException {
		Map map = new HashMap();
		try {
			
			List dataList = cutoverManagerMapper.searchPmValue(cutoverTaskId,
					-1, -1);
			int total = cutoverManagerMapper.getPmValueCount(cutoverTaskId);
			Map<String, Object> config = getEvaluationConfig();
			for(int i=dataList.size()-1;i>=0;i--)
			{
				Map oneRow = new HashMap();
				int level = CommonDefine.PM.MUL.SEC_PM_ZC;
				int score = 0;
				oneRow = (Map)dataList.get(i);
				if(oneRow.get("VALUE_BEFORE")!=null && (String)oneRow.get("VALUE_BEFORE")!="")
				{
					BigDecimal valueBefore = new BigDecimal((String)oneRow.get("VALUE_BEFORE"));
					BigDecimal valueAfter = new BigDecimal((String)oneRow.get("VALUE_AFTER"));
					BigDecimal difference = valueAfter.subtract(valueBefore).setScale(2,BigDecimal.ROUND_HALF_UP);
					Double differenceDouble = difference.doubleValue();
					oneRow.put("DIFFERENCE", differenceDouble);
					int levelTemp = 0;
					double value = Math.abs(differenceDouble);
					// 判断级别
					levelTemp = getLevel(level, value);
					if (levelTemp > level) {
						level = levelTemp;
					}
					if(differenceDouble!=0)
					{
					}
					else
						dataList.remove(oneRow);
				}
				oneRow.put("level", level);
				score = calculateSinglePMScore(oneRow,config);
				oneRow.put("score", score);
				
			}
			map.put("rows", dataList);
			map.put("total", total);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return map;
	}
	
	/**
	 * 更新基准值
	 * @param pmIdList
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> updateCompareValue(List<Integer> pmIdList) throws CommonException
	{
		Map map = new HashMap();
		try {
			List<Map> pmList = cutoverManagerMapper.searchPmValueByIdList(pmIdList);
			
			performanceManagerService.setCompareValueFromPm(pmList);
			map.put("returnResult", CommonDefine.SUCCESS);
			map.put("returnMessage", "更新成功！");
		} catch (RuntimeException e) {
			throw new CommonException(e, MessageCodeDefine.CUTOVER_DB_ERROR);
		}
		return map;
	}
	
	/**
	 * 评估割接结果
	 * @param pmIdList
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> evaluate(Map searchCondition) throws CommonException
	{
		Map<String,Object> map = new HashMap();
		List<Map> taskParamList = cutoverManagerMapper.getCutoverTaskParamList(Integer.valueOf((String)searchCondition.get("cutoverTaskId")));
		if (taskParamList.size() != 0) {
			for (int i = 0; i < taskParamList.size(); i++) {
				Map taskParam = taskParamList.get(i);
				String key = String.valueOf(taskParam.get("PARAM_NAME"));
				String value = String.valueOf(taskParam.get("PARAM_VALUE"));
				String evaluation = "";
				if ("evaluationScore".equals(key))
				{
					Map<String, Object> config = getEvaluationConfig();
					Integer perfectUpperBound = Integer.valueOf((String)config.get("perfectUpperBound"));
					Integer excellentLowerBound = Integer.valueOf((String)config.get("excellentLowerBound"));
					Integer excellentUpperBound = Integer.valueOf((String)config.get("excellentUpperBound"));
					Integer goodLowerBound = Integer.valueOf((String)config.get("goodLowerBound"));
					Integer goodUpperBound = Integer.valueOf((String)config.get("goodUpperBound"));
					Integer averageLowerBound = Integer.valueOf((String)config.get("averageLowerBound"));
					Integer averageUpperBound = Integer.valueOf((String)config.get("averageUpperBound"));
					Integer badLowerBounder = Integer.valueOf((String)config.get("badLowerBounder"));
					
					Integer score = Integer.valueOf(value);
					if(score<=perfectUpperBound)
						evaluation = "完美";
					else if(score>=excellentLowerBound && score<=excellentUpperBound)
						evaluation = "优秀";
					else if(score>=goodLowerBound && score<=goodUpperBound)
						evaluation = "良好";
					else if(score>=averageLowerBound && score<=averageUpperBound)
						evaluation = "一般";
					else if(score>=badLowerBounder)
						evaluation = "差劲";
					map.put("score", score);
					map.put("evaluation", evaluation);
					break;
				}
				
			}
		}
		return map;
	}
}
