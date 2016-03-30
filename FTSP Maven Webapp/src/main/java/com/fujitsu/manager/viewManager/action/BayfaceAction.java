package com.fujitsu.manager.viewManager.action;
 
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IBayfaceManagerService;
import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;


public class BayfaceAction extends AbstractAction{
	

	@Resource
	public IBayfaceManagerService  bayfaceManagerService; 
	@Resource
	private IAlarmManagementService  alarmManagementService;
	
	private String unitId; 
	private String neId; 
	private Map <String,String> map;
	private String speShelfNo;
	
	/**
	 * 右键菜单，板卡属性
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "板卡右键菜单：属性！！")
	public String getUnitAttribute()throws CommonException{
 
		try{
			Map<String,Object> data =  bayfaceManagerService.getUnitAttribute(unitId); 
			resultObj = JSONObject.fromObject(data);	 
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		
		System.out.printf(resultObj.toString());  
		return RESULT_OBJ;
	} 
 
	/**
	 * 面板图初始化数据
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "面板图页面初始化数据")
	public String getBayfaceData()throws CommonException{ 
		try{
			Map<String,Object> data =  bayfaceManagerService.getBayfaceDataFromNE(neId, speShelfNo); 
			
			resultObj = JSONObject.fromObject(data);			

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		System.out.println(resultObj.toString());
		return RESULT_OBJ;
	} 
	 
	/** 
	 * 面板图：取颜色颜色 
	 * @throws CommonException
	 */
	@IMethodLog(desc = "取告警颜色")
	public String getAlarmColorSet() throws CommonException{ 
		try{
			Map<String,Object> data = alarmManagementService.getAlarmColorSet();
			data.put("returnResult", CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		}catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	}
	/**
	 * 取端口信息
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "端口信息")
	public String getPortDomain() throws CommonException{
		try {   
			Map<String,Object> data = bayfaceManagerService.getPortDomain(map);
			data.put("returnResult", CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	}
	/**
	 * 取端口信息
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "板卡信息")
	public String getBayfaceUintId() throws CommonException{
		try {   
			Map<String,Object> data = bayfaceManagerService.getBayfaceUintId(map);
			data.put("returnResult", CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	}
	
	/**
	 * 取网元相关信息
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "网元相关的信息")
	public String getNeRelate() throws CommonException{
		try {   
			Map<String,Object> data = bayfaceManagerService.getNeRelate(neId);
			data.put("returnResult", CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	}
	
	
	public IBayfaceManagerService getBayfaceManagerService() {
		return bayfaceManagerService;
	} 
	public void setBayfaceManagerService(
			IBayfaceManagerService bayfaceManagerService) {
		this.bayfaceManagerService = bayfaceManagerService;
	} 
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	} 
	public String getNeId() {
		return neId;
	} 
	public void setNeId(String neId) {
		this.neId = neId;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	
	public String getSpeShelfNo() {
		return speShelfNo;
	}
	
	public void setSpeShelfNo(String speShelfNo) {
		this.speShelfNo = speShelfNo;
	}
	
}