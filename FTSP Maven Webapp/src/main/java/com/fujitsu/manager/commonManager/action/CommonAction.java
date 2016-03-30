package com.fujitsu.manager.commonManager.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.model.ProcessModel;

public class CommonAction extends AbstractAction{

	@Resource
	public ICommonManagerService commonManagerService;
	//authDomain = false 显示全部包含部分权限数据 authDomain = true 只显示全部权限数据
	private boolean authDomain;
	private boolean displayAll;
	private boolean displayNone;
	private int emsGroupId;
	private int emsId;

	@IMethodLog(desc = "查询所有网管分组")
	public String getAllEmsGroups(){
		try {
			// 查询所有网管分组
			List<Map> dataList = commonManagerService.getAllEmsGroups(sysUserId,displayAll,displayNone,authDomain);
			// 将返回的结果转成JSON对象，返回前台
			Map map = new HashMap();
			map.put("rows", dataList);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "查询所有网管")
	public String getAllEmsByEmsGroupId() {

		try {
			// 查询所有网管
			List<Map> dataList = commonManagerService.getAllEmsByEmsGroupId(
					sysUserId, emsGroupId, displayAll,authDomain);
			// 将返回的结果转成JSON对象，返回前台
			Map map = new HashMap();
			map.put("rows", dataList);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询所有网元")
	public String getAllNeByEmsId(){
		try {
			// 查询所有网元
			List<Map> dataList = commonManagerService.getAllNeByEmsId(sysUserId,
					emsId, displayAll, null );
			// 将返回的结果转成JSON对象，返回前台
			Map map = new HashMap();
			map.put("rows", dataList);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**获取用户设备域列表
	 * @param userId
	 * @return List<Map> 设备域列表
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "共通:获取用户设备域列表")
	public String getUserDeviceDomainDetail() {
		String returnString = RESULT_OBJ;
		try {
			List<Map> nodes = commonManagerService.getUserDeviceDomainDetail(sysUserId);
			resultArray = JSONArray.fromObject(nodes);
			returnString = RESULT_ARRAY;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	@IMethodLog(desc = "共通:初始化进度")
	public String initProcessPercent(){
		//设置百分比参数
		HttpServletRequest request = ServletActionContext.getRequest();
		
		String sessionId = request.getSession().getId();
		
		String processKey = request.getParameter("processKey");
		
		ProcessModel model = CommonDefine.getProcessParameter(sessionId,processKey);
		
		double processPercent = model.getProcessPercent();
		
		//移除百分比参数
		if(processPercent>=1){
			CommonDefine.removeProcessParameter(sessionId,processKey);
		}
		resultObj = JSONObject.fromObject(model);

		return RESULT_OBJ;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	@IMethodLog(desc = "共通:获取进度")
	public String getProcessPercent(){
		//设置百分比参数
		HttpServletRequest request = ServletActionContext.getRequest();
		
		String sessionId = request.getSession().getId();
		
		String processKey = request.getParameter("processKey");
		
		ProcessModel model = CommonDefine.getProcessParameter(sessionId,processKey);
		
		double processPercent = model.getProcessPercent();
		
		//移除百分比参数
		if(processPercent>=1){
			CommonDefine.removeProcessParameter(sessionId,processKey);
		}
		resultObj = JSONObject.fromObject(model);

		return RESULT_OBJ;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String cancelOperation() {
		//设置百分比参数
		HttpServletRequest request = ServletActionContext.getRequest();
		
		String sessionId = request.getSession().getId();

		String processKey = request.getParameter("processKey");

		CommonDefine.setIsCanceledParameter(sessionId, processKey, true);

		return RESULT_OBJ;
	}
	
	//获取系统时间
	public String getCurrentTime() {

		result.setReturnMessage(String.valueOf((new Date()).getTime()));
		
		result.setReturnResult(CommonDefine.SUCCESS);

		resultObj = JSONObject.fromObject(result);
		
		return RESULT_OBJ;
	}
	
	public String getPermissionInfo(){
		
		Map result = commonManagerService.getPermissionInfo();
		
		resultObj = JSONObject.fromObject(result);
		
		return RESULT_OBJ;
	}
	
	public String getAboutInfo(){
		
		Map result = commonManagerService.getAboutInfo();
		
		resultObj = JSONObject.fromObject(result);
		
		return RESULT_OBJ;
	}
	
	public boolean isDisplayAll() {
		return displayAll;
	}

	public void setDisplayAll(boolean displayAll) {
		this.displayAll = displayAll;
	}

	public boolean isDisplayNone() {
		return displayNone;
	}

	public void setDisplayNone(boolean displayNone) {
		this.displayNone = displayNone;
	}


	public int getEmsGroupId() {
		return emsGroupId;
	}


	public void setEmsGroupId(int emsGroupId) {
		this.emsGroupId = emsGroupId;
	}


	public int getEmsId() {
		return emsId;
	}


	public void setEmsId(int emsId) {
		this.emsId = emsId;
	}


	/**
	 * @return the authDomain
	 */
	public boolean isAuthDomain() {
		return authDomain;
	}


	/**
	 * @param authDomain the authDomain to set
	 */
	public void setAuthDomain(boolean authDomain) {
		this.authDomain = authDomain;
	}
	
	
}
