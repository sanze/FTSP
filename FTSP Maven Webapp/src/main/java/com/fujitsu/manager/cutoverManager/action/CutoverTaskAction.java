package com.fujitsu.manager.cutoverManager.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.ICutoverManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;

public class CutoverTaskAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3085405044373312541L;
	private int emsGroupId;
	private List<String> cutoverEquipList;
	private List<String> cutoverEquipNameList;
	private List<String> privilegeList;
	private Map<String, String> searchCondition;
	private int factory;
	private int templateId;
	private int needAll;
	private List<Long> condList;
	public List<Integer> taskIdList;
	private String jsonString;
	private int cutoverTaskId;
	/**
	 * 业务层对象
	 */
	@Resource
	public ICutoverManagerService cutoverManagerService;
	
	/**
	 * 获取割接任务列表
	 */
	@IMethodLog(desc = "割接管理:获取割接任务列表")
	public String getCutoverTask()
	{
		String returnString = RESULT_OBJ;
		try {
			String startTime ="";
			String endTime = "";
			if(!(((String)searchCondition
					.get("startTime")).isEmpty()))
			{
				startTime = (String)searchCondition
				.get("startTime")+" 00:00:00";
			}
			if(!(((String)searchCondition
					.get("endTime")).isEmpty()))
			{
				endTime = (String)searchCondition
				.get("endTime")+" 23:59:59";
			}
			Map result = cutoverManagerService.getCutoverTask(startTime, endTime, (String)searchCondition
					.get("status"), (String)searchCondition.get("cutoverTaskName"), (String)searchCondition.get("currentUserId"),
					start, limit);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}
	/**
	 * 判断割接任务名是否重复
	 * @return
	 *	SUCCESS resultObj - CommonResult 返回判断通过信息
	 *	ERROR resultObj - CommonResult 返回判断失败信息
	 */
	@IMethodLog(desc = "判断割接任务名是否重复")
	public String checkTaskNameExist(){
		try {
		//	String utf8TaskName=new String(taskName.getBytes(),"utf8");
			Map<String, Object> returnMap = new HashMap<String, Object>();
			
			//任务id为空时，说明是新增任务的页面，查询是否重名时不需要考虑id
			//任务id不为空时，说明是修改任务的页面，需要考虑是否与该任务以外的任务重名
			Map<String, Object> map = new HashMap<String, Object>();
			int taskId = (searchCondition.get("cutoverTaskId") == null || ((String)searchCondition
					.get("cutoverTaskId")).isEmpty() ? -1 : Integer
					.parseInt((String)searchCondition.get("cutoverTaskId")));
			map.put("cutoverTaskId", taskId);
			map.put("taskName", searchCondition.get("taskName"));
			
			Boolean data = cutoverManagerService.checkTaskNameExist(map);
			returnMap.put("exit", data);
			resultObj = JSONObject.fromObject(returnMap);			

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());		
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	/**
	 * 修改割接任务页面，割接设备信息初始化
	 * 返回的List<Map>是割接设备信息
	 * @return
	 *	SUCCESS resultObj - List<Map> 返回数据列表
	 *	ERROR resultObj - CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取割接设备列表")
	public String initCutoverEquip() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();

			List<Map> data = cutoverManagerService.getCutoverEquipList(Integer
					.valueOf((String)searchCondition.get("cutoverTaskId")));

			map.put("rows", data);
			map.put("total", data.size());

			resultObj = JSONObject.fromObject(map);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}
	/**
	 * 新增割接任务前验证
	 * 
	 * @return
	 *	SUCCESS resultObj - CommonResult 返回保存成功提示信息
	 *	ERROR resultObj - CommonResult 返回异常信息
	 */
//	@IMethodLog(desc = "新增割接任务前验证", type = IMethodLog.InfoType.MOD)
//	public String addCutoverTaskPreCheck() {
//		try {
//
//			Map<String, Object> data = cutoverManagerService.addCutoverTaskPreCheck(cutoverEquipList);
//			resultObj = JSONObject.fromObject(data);
//
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result
//					.setReturnMessage(MessageHandler
//							.getErrorMessage(e.getErrorCode()));
//			resultObj = JSONObject.fromObject(result);
//		}
//
//		return RESULT_OBJ;
//	}	
	/**
	 * 新增割接任务
	 * 返回的String,新增割接任务是否成功
	 * @return
	 *	SUCCESS resultObj - CommonResult 返回保存成功提示信息
	 *	ERROR resultObj - CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "新增割接任务", type = IMethodLog.InfoType.MOD)
	public String addCutoverTask() {
		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		long currentDate = cal.getTimeInMillis();
		Date createTime = new Date(currentDate);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		try {
			String cutoverTaskId = "";
//			String cutoverTaskId = (String) searchCondition
//					.get("cutoverTaskId");
			String taskName = (String) searchCondition.get("taskName");
			String taskDescription = (String) searchCondition
					.get("taskDescription");
			String startTime = (String) searchCondition.get("startTime");
			String endTime = (String) searchCondition.get("endTime");
			String status = (String) searchCondition.get("status");
			//taskStatus保存割接任务类型：1：按网元端口割接，2：按链路割接，3：按复用段割接，4：按光缆割接
			String taskStatus = (String) searchCondition.get("taskStatus");
			String filterAlarm = (String) searchCondition.get("filterAlarm");
			String snapshot = (String) searchCondition.get("snapshot");
			String autoUpdateCompareValue = (String) searchCondition.get("autoUpdateCompareValue");
			// map.put("inspectEquipList", inspectEquipList);

			Map map = cutoverManagerService.addCutoverTask(createTime,cutoverTaskId, taskName,
					taskDescription, startTime, endTime, status, taskStatus,filterAlarm,
					autoUpdateCompareValue,snapshot, cutoverEquipList, cutoverEquipNameList,privilegeList);
			if(map.get("conflict")!=null && 1==(Integer)map.get("conflict"))
				resultObj = JSONObject.fromObject(map);
			else
			{
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnName(String.valueOf((Long)map.get("cutoverTaskId")));
				result
						.setReturnMessage(MessageHandler
								.getErrorMessage(MessageCodeDefine.CUTOVER_TASK_ADD_SUCCESS));
				resultObj = JSONObject.fromObject(result);
			}

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result
					.setReturnMessage(MessageHandler
							.getErrorMessage(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "初始化割接任务信息")
	public String initTaskInfo(){
		try {
		//	String utf8TaskName=new String(taskName.getBytes(),"utf8");
			Map<String, Object> returnMap = new HashMap<String, Object>();
			
			//任务id为空时，说明是新增任务的页面，查询是否重名时不需要考虑id
			//任务id不为空时，说明是修改任务的页面，需要考虑是否与该任务以外的任务重名
			Map<String, Object> map = new HashMap<String, Object>();
			int taskId = (searchCondition.get("cutoverTaskId") == null || ((String)searchCondition
					.get("cutoverTaskId")).isEmpty() ? -1 : Integer
					.parseInt((String)searchCondition.get("cutoverTaskId")));
			map.put("taskId", taskId);
			List<Map> data = cutoverManagerService.initTaskInfo(map);
			
			returnMap.put("taskId", data.get(0).get("SYS_TASK_ID"));
			returnMap.put("taskName", data.get(0).get("TASK_NAME"));
			returnMap.put("taskDescription", data.get(0).get("DESCRIPTION"));
			returnMap.put("startTime", data.get(0).get("START_TIME_ESTIMATE"));
			returnMap.put("endTime", data.get(0).get("END_TIME_ESTIMATE"));
			returnMap.put("taskStatus", data.get(0).get("taskStatus"));
			returnMap.put("filterAlarm", data.get(0).get("filterAlarm"));
			returnMap.put("snapshot", data.get(0).get("snapshotTime"));
			returnMap.put("privilegeList", data.get(0).get("privilegeList"));
			returnMap.put("autoUpdateCompareValue", data.get(0).get("autoUpdateCompareValue"));
			resultObj = JSONObject.fromObject(returnMap);			

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());		
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "修改割接任务", type = IMethodLog.InfoType.MOD)
	public String modifyCutoverTask() {
		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		long currentDate = cal.getTimeInMillis();
		Date createTime = new Date(currentDate);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		try {
//			String cutoverTaskId = "";
			String cutoverTaskId = (String) searchCondition
					.get("cutoverTaskId");
			String taskName = (String) searchCondition.get("taskName");
			String taskDescription = (String) searchCondition
					.get("taskDescription");
			String startTime = (String) searchCondition.get("startTime");
			String endTime = (String) searchCondition.get("endTime");
			String status = (String) searchCondition.get("status");
			//taskStatus保存割接任务类型：1：按网元端口割接，2：按链路割接，3：按复用段割接，4：按光缆割接
			String taskStatus = (String) searchCondition.get("taskStatus");
			String filterAlarm = (String) searchCondition.get("filterAlarm");
			String snapshot = (String) searchCondition.get("snapshot");
			String autoUpdateCompareValue = (String) searchCondition.get("autoUpdateCompareValue");

			// map.put("inspectEquipList", inspectEquipList);

			cutoverManagerService.modifyCutoverTask(createTime,cutoverTaskId, taskName,
					taskDescription, startTime, endTime, status, taskStatus,filterAlarm,
					autoUpdateCompareValue,snapshot, cutoverEquipList, cutoverEquipNameList,privilegeList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(MessageHandler
							.getErrorMessage(MessageCodeDefine.CUTOVER_TASK_MODIFY_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result
					.setReturnMessage(MessageHandler
							.getErrorMessage(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	/**
	 * 删除任务
	 * @return
	 */
	@IMethodLog(desc = "删除任务", type = IMethodLog.InfoType.DELETE)
	public String deleteTask() {
		try {

			cutoverManagerService.deleteTask(taskIdList);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "查询链路信息")
	public String getLink() {
		
		try {
			// String cutoverTaskId = "";
			String emsGroupId = (String) searchCondition.get("emsGroupId");
			String emsId = (String) searchCondition.get("emsId");
			String linkType = (String) searchCondition.get("linkType");
			String linkNameOrId = (String) searchCondition.get("linkNameOrId");
			Map linkMap = cutoverManagerService.getLink(emsId, emsGroupId,linkType, linkNameOrId,sysUserId,start,limit);
			resultObj = JSONObject.fromObject(linkMap);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result
					.setReturnMessage(MessageHandler
							.getErrorMessage(MessageCodeDefine.CUTOVER_TASK_ADD_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	
	/**
	 * 修改割接任务页面，割接链路信息初始化
	 * 返回的List<Map>是割接设备信息
	 * @return
	 *	SUCCESS resultObj - List<Map> 返回数据列表
	 *	ERROR resultObj - CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取割接链路列表")
	public String initCutoverLink() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();

			List<Map> data = cutoverManagerService.getCutoverLinkList(Integer
					.valueOf((String)searchCondition.get("cutoverTaskId")));

			map.put("rows", data);
			map.put("total", data.size());

			resultObj = JSONObject.fromObject(map);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "影响电路查询")
	public String searchCircuitsInfluenced() {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		map = (Map) jsonObject;
		map.remove("serviceType");
		map.put("start", start);
		try {
			
			Map data = cutoverManagerService.searchCircuitsInfluenced(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "端口性能值查询")
	public String searchPmValue() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {

			Map data = cutoverManagerService.searchPmValue(Integer
					.valueOf((String)searchCondition.get("cutoverTaskId")),start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "割接前快照")
	public String snapshotBefore() {
		try {
			String cutoverTaskId = (String) searchCondition
					.get("cutoverTaskId");	
			cutoverManagerService.snapshotBefore(cutoverTaskId,sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage("操作成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result
					.setReturnMessage("操作失败！");
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询相关告警")
	public String getCurrentAlarms() {
		try {

			Map data = cutoverManagerService.getCurrentAlarms(
					(String) searchCondition.get("cutoverTaskId"),
					searchCondition.get("alarmType"), start, limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询割接期间告警")
	public String getAlarmsDuringCutover() {
		try {
			Map data = cutoverManagerService.getAlarmsDuringCutover((String)searchCondition.get("cutoverTaskId"),sysUserId,start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "割接后快照")
	public String snapshotAfter() {
		try {

			String cutoverTaskId = (String) searchCondition
					.get("cutoverTaskId");	
			cutoverManagerService.snapshotAfter(cutoverTaskId,sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage("操作成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result
					.setReturnMessage("操作失败！");
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "割接完成")
	public String cutoverComplete() {
		try {
			cutoverManagerService.cutoverComplete((String) searchCondition
					.get("cutoverTaskId"),sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage("操作成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("操作失败！");
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "过滤告警")
	public String filterAlarm() {
		try {
			cutoverManagerService.filterAlarm((String)searchCondition.get("cutoverTaskId"),sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
			.setReturnMessage("操作成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result
			.setReturnMessage("操作失败！");
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "初始化割接任务进度状态")
	public String initCutoverTaskProcess() {
		try {
			
			Map processFlagMap = cutoverManagerService.initCutoverTaskProcess((String)searchCondition.get("cutoverTaskId"));
//			Map processFlagMap = cutoverManagerService.initCutoverTaskProcess("12");
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(MessageHandler
							.getErrorMessage(MessageCodeDefine.CUTOVER_TASK_ADD_SUCCESS));
			resultObj = JSONObject.fromObject(processFlagMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "导出查询结果")
	public String downloadResult() {
		
		try {
			String destination = cutoverManagerService.exportExcel(cutoverTaskId, Integer.valueOf(searchCondition.get("flag")).intValue());
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "割接报告导出")
	public String downLoadReport() {
		try {
			CommonResult data = cutoverManagerService.downLoadReport(cutoverTaskId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "割接报告生成", type = IMethodLog.InfoType.MOD)
	public String generateReport() {
		try {
			int cutoverTaskId = 225;
			int userId = -1;
			cutoverManagerService.generateReport(cutoverTaskId,userId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("报告生成成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "查询割接前异常性能（割接前评估用）")
	public String searchPmValueBefore()
	{
		try {

			Map data = cutoverManagerService.searchPmValueBefore(Integer.valueOf((String)searchCondition.get("cutoverTaskId")),start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "查询割接前异常告警（割接前评估用）")
	public String searchAlarmBefore()
	{
		try {

			Map data = cutoverManagerService.searchAlarmBefore(Integer.valueOf((String)searchCondition.get("cutoverTaskId")),start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "查询割接前异常倒换（割接前评估用）")
	public String searchEventBefore()
	{
		//TODO dummy function
		try {

			Map data = cutoverManagerService.searchEventBefore(Integer.valueOf((String)searchCondition.get("cutoverTaskId")),start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询割接后异常性能（割接后评估用）")
	public String searchPmValueAfter()
	{
		try {

			Map data = cutoverManagerService.searchPmValueAfter(Integer.valueOf((String)searchCondition.get("cutoverTaskId")),start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "查询割接后异常告警（割接后评估用）")
	public String searchAlarmAfter()
	{
		try {

			Map data = cutoverManagerService.searchAlarmAfter(Integer.valueOf((String)searchCondition.get("cutoverTaskId")),start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "查询割接后异常倒换（割接后评估用）")
	public String searchEventAfter()
	{
		//TODO dummy function
		try {

			Map data = cutoverManagerService.searchEventAfter(Integer.valueOf((String)searchCondition.get("cutoverTaskId")),start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "查询评估参数")
	public String getEvaluationConfig()
	{
		try {

			Map data = cutoverManagerService.getEvaluationConfig();
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "修改评估参数", type = IMethodLog.InfoType.MOD)
	public String modifyEvaluationConfig()
	{
		try {

			Map data = cutoverManagerService.modifyEvaluationConfig(searchCondition);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "割接前后存在差值端口性能值查询")
	public String searchPmValueWithDifference() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {

			Map data = cutoverManagerService.searchPmValueWithDifference(Integer
					.valueOf((String)searchCondition.get("cutoverTaskId")));
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "更新基准值", type = IMethodLog.InfoType.MOD)
	public String updateCompareValue()
	{
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Map data = cutoverManagerService.updateCompareValue(taskIdList);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "评估割接结果")
	public String evaluate()
	{
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Map data = cutoverManagerService.evaluate(searchCondition);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	public int getEmsGroupId() {
		return emsGroupId;
	}

	public void setEmsGroupId(int emsGroupId) {
		this.emsGroupId = emsGroupId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public int getFactory() {
		return factory;
	}

	public void setFactory(int factory) {
		this.factory = factory;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}


	public int getNeedAll() {
		return needAll;
	}

	public void setNeedAll(int needAll) {
		this.needAll = needAll;
	}

	public Map<String, String> getSearchCondition() {
		return searchCondition;
	}
	public void setSearchCondition(Map<String, String> searchCondition) {
		this.searchCondition = searchCondition;
	}
	public List<Long> getCondList() {
		return condList;
	}

	public void setCondList(List<Long> condList) {
		this.condList = condList;
	}
	public List<String> getCutoverEquipList() {
		return cutoverEquipList;
	}
	public void setCutoverEquipList(List<String> cutoverEquipList) {
		this.cutoverEquipList = cutoverEquipList;
	}
	public List<String> getCutoverEquipNameList() {
		return cutoverEquipNameList;
	}
	public void setCutoverEquipNameList(List<String> cutoverEquipNameList) {
		this.cutoverEquipNameList = cutoverEquipNameList;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public List<Integer> getTaskIdList() {
		return taskIdList;
	}
	public void setTaskIdList(List<Integer> taskIdList) {
		this.taskIdList = taskIdList;
	}
	public int getCutoverTaskId() {
		return cutoverTaskId;
	}
	public void setCutoverTaskId(int cutoverTaskId) {
		this.cutoverTaskId = cutoverTaskId;
	}
	public List<String> getPrivilegeList() {
		return privilegeList;
	}

	public void setPrivilegeList(List<String> privilegeList) {
		this.privilegeList = privilegeList;
	}
}
