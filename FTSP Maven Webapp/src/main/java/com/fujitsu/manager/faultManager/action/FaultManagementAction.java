package com.fujitsu.manager.faultManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IFaultManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;



public class FaultManagementAction extends DownloadAction {
	
	private static final long serialVersionUID = 8607341493391226840L;
	
	@Resource
	public IFaultManagementService faultManagementService;
	
	private Map<String,String> paramMap;
	
	private List<String> paramMapList;
	
	@IMethodLog(desc = "查询故障列表")
	public String getFaultList() {
		
		try {
			
			Map<String, Object> data = faultManagementService.getFaultList(paramMap, start, limit);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "删除故障信息", type = IMethodLog.InfoType.DELETE)
	public String deleteFaultRecord() {
		
		try {
			
			CommonResult data = faultManagementService.deleteFaultRecord(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "通过faultId和type获取故障信息")
	public String getFaultInfoByFaultIdAndType() {
		
		try {
			
			Map<String, Object> data = faultManagementService.
							getFaultInfoByFaultIdAndType(paramMap);
			
			resultObj = JSONObject.fromObject(data);
//			result.setReturnResult(CommonDefine.SUCCESS);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取故障相关的告警列表")
	public String getFaultAlarmList() {
		
		try {
			
			Map<String, Object> data = faultManagementService.getFaultAlarmList(paramMap, start, limit);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取设备故障定位的相关信息")
	public String getEquipFaultLocationInfo() {
		
		try {
			
			Map<String, Object> data = faultManagementService.
					getEquipFaultLocationInfo(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "删除故障相关的告警", type = IMethodLog.InfoType.DELETE)
	public String deleteFaultAlarm() {
		
		try {
			
			CommonResult data = faultManagementService.deleteFaultAlarm(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
//	@SuppressWarnings("unchecked")
//	@IMethodLog(desc = "增加故障相关的告警", type = IMethodLog.InfoType.MOD)
//	public String addFaultAlarm() {
//		
//		try {
//			
//			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//			
//			for(String s : paramMapList){
//				Map<String, Object> map = (Map<String, Object>)JSON.parse(s);
//				list.add(map);
//			}
//			
//			CommonResult data = faultManagementService.addFaultAlarm(list);
//			resultObj = JSONObject.fromObject(data);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
//		
//		return RESULT_OBJ;
//	}
	
	@IMethodLog(desc = "刷新故障关联告警")
	public String refreshFaultAlarm() {
		
		try{
			Map<String, Object> data = faultManagementService.refreshFaultAlarm(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取传输系统列表")
	public String getTransformSystemList(){
		
		try{
			Map<String, Object> data = faultManagementService.getTransformSystemList();
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取光缆列表")
	public String getCableList() {
		
		try{
			Map<String, Object> data = faultManagementService.getCableList();
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取光缆段信息")
	public String getCableSectionList() {
		
		try{
			Map<String, Object> data = faultManagementService.getCableSectionList(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取故障原因列表")
	public String getFaultReasonList() {
		
		try{
			Map<String, Object> data = faultManagementService.getFaultReasonList(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "保存故障信息", type = IMethodLog.InfoType.MOD)
	public String saveFaultInfo() {
		
		try{
			CommonResult data = faultManagementService.saveFaultInfo(paramMap, paramMapList);
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "更新首页故障信息", type = IMethodLog.InfoType.MOD)
	public String updateFaultInfo_Main() {
		
		try{
			
			faultManagementService.updateFaultInfo_Main();
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "故障确认", type = IMethodLog.InfoType.MOD)
	public String faultConfirm() {
		
		try{
			
			CommonResult data = faultManagementService.faultConfirm(paramMap, sysUserId);
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "故障恢复", type = IMethodLog.InfoType.MOD)
	public String faultRecovery() {
		
		try{
			CommonResult data = faultManagementService.faultRecovery(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "故障归档")
	public String faultArchive() {
		
		try{
			CommonResult data = faultManagementService.faultArchive(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取首页显示的故障信息")
	public String getFaultInfoForFP() {
		
		try{
			CommonResult data = faultManagementService.getFaultInfoForFP();
			
			resultObj = JSONObject.fromObject(data);
		}catch(CommonException e){
			
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public List<String> getParamMapList() {
		return paramMapList;
	}

	public void setParamMapList(List<String> paramMapList) {
		this.paramMapList = paramMapList;
	}

	
}
