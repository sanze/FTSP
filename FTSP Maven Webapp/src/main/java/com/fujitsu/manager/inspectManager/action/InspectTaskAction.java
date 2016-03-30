package com.fujitsu.manager.inspectManager.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IInspectManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;

/**
 * 
 * @author WangXiaoye 巡检任务web接口
 */
public class InspectTaskAction extends AbstractAction {

	private static final long serialVersionUID = 7252401476028719189L;

	@Resource
	public IInspectManagerService inspectionManagerService;
	public String inspectTaskId;
	public String taskName;
	public String taskDescription;
	public List<String> inspectItemList;
	public int periodType;
	public String periodTime;
	public String startTime;
	public String nextTime;
	public int handUp;
	public List<String> privilegeList;
	public List<String> inspectEquipList = null;
	public List<String> inspectEquipNameList;
	public List<Integer> taskIdList;

	public int statusFlag;
	public String inspectItemParamId;
	public String privilegeParamId;
	
	

	/**
	 * 巡检任务查询:查询所有巡检任务的信息
	 * 
	 * @param params
	 *            查询参数
	 * @return SUCCESS resultObj - Map<String,Object> 返回数据列表 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取巡检任务列表")
	public String getInspectTaskList() {
		try {
			Map<String, Object> data = inspectionManagerService
					.getInspectTaskList(start, limit);
			resultObj = JSONObject.fromObject(data);
			System.out.println(resultObj);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_LIST_GET_FAILED));
			resultObj = JSONObject.fromObject(result);
		}catch(Exception e){
			e.printStackTrace();
		}

		return RESULT_OBJ;
	}

	/**
	 * 获取操作权限组列表:查询所有操作权限组的信息
	 * 
	 * @param params
	 *            查询参数
	 * @return SUCCESS resultObj - Map<String,Object> 返回数据列表 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取操作权限组列表")
	public String getPrivilegeGroupList() {
		try {
			Map<String, Object> data = inspectionManagerService
					.getPrivilegeList();
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_PRIVILEGE_GET_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 获取当前登录用户所在组ID
	 * 
	 * @param params
	 *            查询参数
	 * @return SUCCESS resultObj - Map<String,Object> 返回用户组ID ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取当前登录用户所在组ID")
	public String getCurrentUserGroup() {
		try {
			Map<String, Object> data = inspectionManagerService
					.getCurrentUserGroup(sysUserId);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.CURRENT_USER_GROUP_GET_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 判断巡检任务名是否重复
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回判断通过信息 ERROR resultObj -
	 *         CommonResult 返回判断失败信息
	 */
	@IMethodLog(desc = "判断巡检任务名是否重复")
	public String checkTaskNameExist() {
		try {
			// String utf8TaskName=new String(taskName.getBytes(),"utf8");
			Map<String, Object> returnMap = new HashMap<String, Object>();

			// 需要保存的巡检任务信息整合在map中
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("taskId", inspectTaskId);
			map.put("taskName", taskName);

			Boolean data = inspectionManagerService.checkTaskNameExist(map);
			returnMap.put("exit", data);
			resultObj = JSONObject.fromObject(returnMap);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("判断失败！");
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return RESULT_OBJ;
	}

	/**
	 * 新增巡检任务 返回的String,新增巡检任务是否成功
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回保存成功提示信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "新增巡检任务", type = IMethodLog.InfoType.MOD)
	public String addInspectTask() {

		try {
			// 时间转换
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			// 获取当前时间
			Calendar cal = Calendar.getInstance();
			long currentDate = cal.getTimeInMillis();
			Date createTime = new Date(currentDate);

			//
			Date start = new Date();
			Date next = new Date();
			Date startNull = new Date();
			Date nextNull = new Date();
			if(startTime.equals("")){
				start = startNull;
			}else{
				start = date.parse(startTime);
			}
			
			if(nextTime.equals("")){
				next = nextNull;
			}else{
				next = date.parse(nextTime);
			}

			// 需要保存的巡检任务信息整合在map中
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("taskId", null);
			map.put("taskName", taskName);
			map.put("taskType", CommonDefine.QUARTZ.JOB_INSPECT);
			map.put("parentTask", 0);
			map.put("taskDescription", taskDescription);
			map.put("periodType", periodType);
			map.put("period", periodTime);
			map.put("startTime", start);
			map.put("endTime", null);
			map.put("taskStatus", handUp);
			map.put("nextTime", next);
			map.put("createPerson", sysUserId);
			map.put("isDel", 0);
			map.put("createTime", createTime);
			map.put("updateTime", createTime);
			map.put("inspectItemList", inspectItemList);
			map.put("privilegeList", privilegeList);

			// map.put("inspectEquipList", inspectEquipList);
			//inspectEquipList = null;
			
			System.out.println(inspectEquipList);
			System.out.println(inspectEquipList.size());

			inspectionManagerService.addInspectTask(map, inspectEquipList,
					inspectEquipNameList);
			//新增巡检任务返回的taskId
			String taskId = String.valueOf(map.get("taskId"));
			//获取巡检任务privilegeParamId和inspectItemParamId
			Map<String, Object> paramInfoReturn = inspectionManagerService
					.getInspectTaskInfo(Integer.valueOf(taskId));
			 
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_ADD_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			resultObj.put("inspectTaskId", taskId);
			resultObj.putAll(paramInfoReturn);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_ADD_FAILED));
			resultObj = JSONObject.fromObject(result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.TIME_PARSE_ERROR));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 修改巡检任务页面初始化 返回的List<Map>是巡检设备信息
	 * 
	 * @return SUCCESS resultObj - List<Map> 返回数据列表 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取巡检设备列表")
	public String initInspectEquip() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();

			List<Map> data = inspectionManagerService.getInspectEquipList(
					Integer.valueOf(inspectTaskId), CommonDefine.INSPECT_TASK);

			map.put("rows", data);
			map.put("total", data.size());

			resultObj = JSONObject.fromObject(map);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_EQUIP_GET_FAILED));
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}

	/**
	 * 修改巡检任务页面初始化 返回的Map是巡检任务基本信息
	 * 
	 * @return SUCCESS resultObj - Map 返回数据列表 ERROR resultObj - CommonResult
	 *         返回异常信息
	 */
	@IMethodLog(desc = "获取巡检任务基本信息")
	public String initTaskInfo() {
		try {
			Map<String, Object> map = inspectionManagerService
					.getInspectTaskInfo(Integer.valueOf(inspectTaskId));
			resultObj = JSONObject.fromObject(map);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_INFO_GET_FAILED));
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}

	/**
	 * 修改巡检任务保存 返回的String,修改巡检任务是否成功
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回保存成功提示信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "修改巡检任务", type = IMethodLog.InfoType.MOD)
	public String updateInspectTask() {

		try {
			// 时间转换
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			// 获取当前时间
			Calendar cal = Calendar.getInstance();
			long currentDate = cal.getTimeInMillis();
			Date updateTime = new Date(currentDate);

			//
			Date start = date.parse(startTime);
			Date next = date.parse(nextTime);

			// 需要保存的巡检任务信息整合在map中
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("taskId", inspectTaskId);
			map.put("taskName", taskName);
			map.put("taskType", CommonDefine.QUARTZ.JOB_INSPECT);
			map.put("parentTask", 0);
			map.put("taskDescription", taskDescription);
			map.put("periodType", periodType);
			map.put("period", periodTime);
			map.put("startTime", start);
			//map.put("endTime", start);
			map.put("taskStatus", handUp);
			map.put("nextTime", next);
			map.put("createPerson", sysUserId);
			map.put("isDel", 0);
			// map.put("createTime", null);
			map.put("updateTime", updateTime);
			map.put("inspectItemList", inspectItemList);
			map.put("privilegeList", privilegeList);
			map.put("privilegeParamId",
					Integer.valueOf(privilegeParamId));
			map.put("inspectItemParamId",
					Integer.valueOf(inspectItemParamId));

			// map.put("inspectEquipList", inspectEquipList);

			inspectionManagerService.updateInspectTask(map, inspectEquipList,
					inspectEquipNameList);
			String taskId = String.valueOf(map.get("taskId")).toString();
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			resultObj.put("inspectTaskId", taskId);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_UPDATE_FAILED));
			resultObj = JSONObject.fromObject(result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.TIME_PARSE_ERROR));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 删除巡检任务 返回删除巡检任务是否成功信息
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回删除成功信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "删除巡检任务", type = IMethodLog.InfoType.DELETE)
	public String deleteInspectTask() {
		try {

			inspectionManagerService.deleteInspectTask(taskIdList);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_DELETE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_DELETE_FAILED));
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}

	/**
	 * 立即执行巡检任务 返回立即执行巡检任务是否成功信息
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回立即执行成功信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "立即执行巡检任务", type = IMethodLog.InfoType.MOD)
	public String startTaskImmediately() {
		try {

			inspectionManagerService.startTaskImmediately(inspectTaskId);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_START_IMMEDIATELY_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_START_IMMEDIATELY_FAILED));
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}

	/**
	 * 巡检任务启用、挂起、删除 返回启用、挂起、删除巡检任务是否成功信息
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回启用、挂起成功信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "启用、挂起、删除巡检任务", type = IMethodLog.InfoType.MOD)
	public String changeInspectTaskStatus() {
		try {

			inspectionManagerService.changeInspectTaskStatus(taskIdList,
					statusFlag);

			result.setReturnResult(CommonDefine.SUCCESS);
			if (CommonDefine.QUARTZ.JOB_ACTIVATE == statusFlag) {
				result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_START_SUCCESS));
			} else if (CommonDefine.QUARTZ.JOB_PAUSE == statusFlag) {
				result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_HANGUP_SUCCESS));
			} else if (CommonDefine.QUARTZ.JOB_DELETE == statusFlag) {
				result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_DELETE_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (CommonDefine.QUARTZ.JOB_ACTIVATE == statusFlag) {
				result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_START_FAILED));
			} else if (CommonDefine.QUARTZ.JOB_PAUSE == statusFlag) {
				result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_HANGUP_FAILED));
			} else if (CommonDefine.QUARTZ.JOB_DELETE == statusFlag) {
				result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_DELETE_FAILED));
			}
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}

	/**
	 * 任务执行情况信息加载
	 * 
	 * @param params
	 *            查询参数
	 * @return SUCCESS resultObj - Map<String,Object> 返回数据列表 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取任务执行情况列表", type = IMethodLog.InfoType.MOD)
	public String getTaskRunDetial() {
		try {
			Map<String, Object> data = inspectionManagerService
					.getTaskRunDetial(Integer.valueOf(inspectTaskId).intValue());
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_TASK_DETIAL_GET_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	public String getInspectTaskId() {
		return inspectTaskId;
	}

	public void setInspectTaskId(String inspectTaskId) {
		this.inspectTaskId = inspectTaskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public List<String> getInspectItemList() {
		return inspectItemList;
	}

	public void setInspectItemList(List<String> inspectItemList) {
		this.inspectItemList = inspectItemList;
	}

	public int getPeriodType() {
		return periodType;
	}

	public void setPeriodType(int periodType) {
		this.periodType = periodType;
	}

	public String getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(String periodTime) {
		this.periodTime = periodTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getNextTime() {
		return nextTime;
	}

	public void setNextTime(String nextTime) {
		this.nextTime = nextTime;
	}

	public int getHandUp() {
		return handUp;
	}

	public void setHandUp(int handUp) {
		this.handUp = handUp;
	}

	public List<String> getInspectEquipList() {
		return inspectEquipList;
	}

	public void setInspectEquipList(List<String> inspectEquipList) {
		this.inspectEquipList = inspectEquipList;
	}

	public List<String> getInspectEquipNameList() {
		return inspectEquipNameList;
	}

	public void setInspectEquipNameList(List<String> inspectEquipNameList) {
		this.inspectEquipNameList = inspectEquipNameList;
	}

	public List<Integer> getTaskIdList() {
		return taskIdList;
	}

	public void setTaskIdList(List<Integer> taskIdList) {
		this.taskIdList = taskIdList;
	}

	public List<String> getPrivilegeList() {
		return privilegeList;
	}

	public void setPrivilegeList(List<String> privilegeList) {
		this.privilegeList = privilegeList;
	}

	public long getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(int statusFlag) {
		this.statusFlag = statusFlag;
	}

	public String getInspectItemParamId() {
		return inspectItemParamId;
	}

	public void setInspectItemParamId(String inspectItemParamId) {
		this.inspectItemParamId = inspectItemParamId;
	}

	public String getPrivilegeParamId() {
		return privilegeParamId;
	}

	public void setPrivilegeParamId(String privilegeParamId) {
		this.privilegeParamId = privilegeParamId;
	}

}
