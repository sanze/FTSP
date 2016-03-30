package com.fujitsu.manager.resourceManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IResourceSystemCorrelationService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;

@SuppressWarnings("serial")
public class AssociateResourceAction extends AbstractAction {

	/**
	 * @author fanguangming 2015/10/06
	 */

	@Resource
	public IResourceSystemCorrelationService resourceCorrelationService;
	@Resource
	public IAlarmManagementService alarmManagementService;

	protected int rcTaskId;
	protected String jsonString;
	protected List<Integer> resourceTaskList;
	/** 
	 * 获取资源关联任务的初始信息
	 * 
	 */
	@IMethodLog(desc = "获取资源关联任务列表")
	public String resourceTaskInit() {
		try {
			Map<String,Object> data = resourceCorrelationService
					.getAllResourceCorrelationTask();
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/** 
	 * 根据Id获取单个任务信息
	 * 
	 */
	@IMethodLog(desc = "获取资源关联任务信息")
	public String getResourceTaskById() {
		
		try {
			Map<String,Object> status = resourceCorrelationService
					.getResourceCorrelatioinTaskInfo(rcTaskId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(status);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}
	
	@IMethodLog(desc = "获取资源关联任务执行状态")
	public String getResourceTaskStatus() {
		try {
			Map<String,Object> status = resourceCorrelationService
					.getResourceCorrelationTaskStatus(rcTaskId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(status);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 设置资源任务
	 * 
	 * @return
	 */
	@IMethodLog(desc = "设置资源关联任务")
	public String setResourceTask() {
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String,Object> map = (Map<String,Object>) jsonObject;

			resourceCorrelationService.setResourceCorrelationTask(map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}
	
	/**
	 * 启用任务
	 * 
	 * @return
	 */
	@IMethodLog(desc = "启用资源关联任务")
	public String startTask() {
		try {
			//启用任务
			resourceCorrelationService.enableResourceCorrelationTask(resourceTaskList);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}
	
	/**
	 * 挂起任务
	 * 
	 * @return
	 */
	@IMethodLog(desc = "挂起资源关联任务")
	public String holdOn() {
		try {
			//挂起任务
			resourceCorrelationService.disableResourceCorrelationTask(resourceTaskList);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}
	
	@IMethodLog(desc = "手动执行资源关联任务")
	public String manualStart() {
		try {
			//手动执行任务
			resourceCorrelationService.manualStartResourceCorrelationTask(rcTaskId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	public int getRcTaskId() {
		return rcTaskId;
	}

	public void setRcTaskId(int rcTaskId) {
		this.rcTaskId = rcTaskId;
	}
	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	public List<Integer> getResourceTaskList() {
		return resourceTaskList;
	}

	public void setResourceTaskList(List<Integer> resourceTaskList) {
		this.resourceTaskList = resourceTaskList;
	}
}
