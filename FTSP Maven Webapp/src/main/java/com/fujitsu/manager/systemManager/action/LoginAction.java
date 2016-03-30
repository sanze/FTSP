package com.fujitsu.manager.systemManager.action;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISystemManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;


public class LoginAction extends AbstractAction{

	@Resource
	public ISystemManagerService systemManagerService;
	public String userName;
	public String password;

	/**
	 * 登入
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "登录")
	public String login(){

		Map map;
		try {
			CommonResult result = systemManagerService.login(userName,password);
			
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("");
		}
		return RESULT_OBJ;
	}
	/**
	 * 用户锁定
	 * @throws CommonException 
	 */
	public String lock(){
		try {
			systemManagerService.lock();
			
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("");
		}
		return RESULT_OBJ;
	}
	/**
	 * 用户解锁
	 * @throws CommonException 
	 */
	public String unlock(){
		try {
			systemManagerService.unlock(userName,password);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("unlock success");
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("unlock failed");
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	/**
	 * 用户Session保持
	 * @throws Exception 
	 */
	public String hello(){
		try {
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("I'm alive...");
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("You're dead...");
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	/**
	 * 心跳
	 * 保持用户在线
	 */
	@IMethodLog(desc = "心跳")
	public String heartBeat(){
		result.setReturnResult(CommonDefine.SUCCESS);
		result.setReturnMessage("Heart beating~");
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}

	/**
	 * 用户注销
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "用户注销")
	public String logout(){
		try {
			result=systemManagerService.logout(sysUserId, new Date());
			ServletActionContext.getRequest().getSession().removeAttribute("SYS_USER_ID");
			ServletActionContext.getRequest().getSession().invalidate();
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	
	/**
	 * 获取首页数据
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "获取首页统计数据")
	public String getParamFP() {
		try {
			Map<String, Object> data = systemManagerService.getParamFP(sysUserId.intValue());
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	/**
	 * 获取首页数据
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "获取首页性能统计数据")
	public String getIndexPmInfo() {
		try {
			Map<String, Object> data = systemManagerService.getIndexPmInfo(sysUserId.intValue());
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
