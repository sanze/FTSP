package com.fujitsu.manager.keyAccountManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IKeyAccountManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class KeyAccountAction extends AbstractAction{

	private static final long serialVersionUID = 4690242135278305702L;
	private Integer userId;
	private String clientName;
	private String circuitType;
	private String circuitInfoId;
	@Resource
	private IKeyAccountManagerService keyAccountManagerService;
	@Resource
	public IAlarmManagementService alarmManagementService;
	/**
	 * 获取联系人
	 * @return
	 */
	public String getContactInfo(){
		try {
			Map<String, Object> info = keyAccountManagerService.getContactInfo(start, limit);
			resultObj = JSONObject.fromObject(info);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取所有的大客户信息
	 * @return
	 */
	public String getVIPInfo()
	{
		try {
			Map<String, Object> vipInfo = keyAccountManagerService.getVIPInfo(start, limit);
			resultObj = JSONObject.fromObject(vipInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取所有的大客户信息(不带告警信息)
	 * @return
	 */
	public String getVIPInfoWithoutAlarm() {
		try {
			Map<String, Object> vipInfo = keyAccountManagerService
					.getVIPInfoWithoutAlarmInfo(start, limit);
			resultObj = JSONObject.fromObject(vipInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 根据大客户名称查询相关电路（分页）
	 * @return
	 */
	public String getCircuitsByVIPName()
	{
		try {
			Map<String, Object> circuitInfo = keyAccountManagerService.getCircuitsByVIPName(clientName,start, limit);
			resultObj = JSONObject.fromObject(circuitInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 根据大客户名称查询相关电路（分页、不带告警信息）
	 * 
	 * @return
	 */
	public String getCircuitsByVIPNameWithoutAlarmInfo() {
		try {
			Map<String, Object> circuitInfo = keyAccountManagerService
					.getCircuitsByVIPNameWithoutAlarmInfo(clientName, start,
							limit);
			resultObj = JSONObject.fromObject(circuitInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 根据大客户名称查询不同速率电路的条数
	 * @return
	 */
	public String getGroupedCircuitsByVIPName()
	{
		try {
			Map<String, Object> circuitInfo = keyAccountManagerService.getGroupedCircuitsByVIPName(clientName);
			resultObj = JSONObject.fromObject(circuitInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 查询大客户相关的割接任务和相关业务影响
	 * @return
	 */
	public String getCutoverInfoByVIPName() {
		try {
			Map<String, Object> vipInfo = keyAccountManagerService
					.getCutoverInfoByVIPName();
			resultObj = JSONObject.fromObject(vipInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 查询电路相关告警
	 * @return
	 */
	public String getAlarmByCircuit() {
		try {
			Map<String, Object> alarmMap = new HashMap<String,Object>();
			if (circuitType.equals("1")) {
				alarmMap = alarmManagementService
						.getCurrentAlarmForCircuit(Integer.valueOf(circuitInfoId), 1, -1, -1,
								false, true, false, true, true);
			} else if (circuitType.equals("2")) {
				alarmMap = alarmManagementService
						.getCurrentAlarmForCircuit(Integer.valueOf(circuitInfoId), 2, -1, -1,
								false, true, false, true, true);

			}
			resultObj = JSONObject.fromObject(alarmMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getCircuitType() {
		return circuitType;
	}
	public void setCircuitType(String circuitType) {
		this.circuitType = circuitType;
	}
	public String getCircuitInfoId() {
		return circuitInfoId;
	}
	public void setCircuitInfoId(String circuitInfoId) {
		this.circuitInfoId = circuitInfoId;
	}
	

}
