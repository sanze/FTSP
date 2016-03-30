package com.fujitsu.manager.faultManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IFaultDiagnoseManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

public class FaultDiagnoseAction extends DownloadAction {
	
	private static final long serialVersionUID = 4726220571571265220L;

	@Resource
	public IFaultDiagnoseManagerService faultDiagnoseService;
	
	private Map<String,String> paramMap;
	private List<Integer> ids;
	private String jsonString;
	
	@IMethodLog(desc = "获取故障诊断规则列表")
	public String getFaultDiagnoseRules() {
		
		try {
			
			Map<String, Object> data = faultDiagnoseService.getFaultDiagnoseRules(start, limit);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "通过ID获取故障诊断规则详情")
	public String getFaultDiagnoseDetailById() {
		
		try {
			
			Map<String, Object> data = faultDiagnoseService.getFaultDiagnoseDetailById(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取诊断规则的适用范围")
	public String getApplyScope() {
		
		try {
			
			Map<String, Object> data = faultDiagnoseService.getApplyScope(paramMap);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "改变故障诊断规则的状态", type = IMethodLog.InfoType.MOD)
	public String changeFaultDiagnoseRuleStatus() {
		
		try {
			
			CommonResult data = faultDiagnoseService.changeFaultDiagnoseRuleStatus(paramMap, ids);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	public String manualActionRules() {
		
		try {
			
			CommonResult data = faultDiagnoseService.manualActionRules(ids);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "判断服务是否启动")
	public String isServerStarted() {
		
		try {
			CommonResult data = faultDiagnoseService.isServerStarted();
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "取指定网管下的网元型号和工厂信息")
	public String getApplyEquips(){
		try { 
			
			Map<String, Object> data = faultDiagnoseService.getApplyEquips(ids);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改故障诊断规则", type = IMethodLog.InfoType.MOD)
	public String modifyFaultDiagnoseRule() {
		try { 
			JSONObject jsonObject = JSONObject.fromObject(jsonString); 
			Map<String, Object> param = new HashMap<String, Object>();
			param = (Map<String, Object>) jsonObject; 
			faultDiagnoseService.modifyFaultDiagnoseRule(param, ids);
			
			/** 调用告警收敛服务接口，启用或挂起一条或多条告警收敛规则 **/
//			IAlarmConvergeServiceProxy alarmConvergeService = SpringContextUtil.getAlarmConvergeServiceProxy();
//			int ruleId = Integer.valueOf(param.get("CONVERGE_ID").toString());
//			alarmConvergeService.updateAlarmConverge(ruleId);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			if("apply".equals(param.get("flagStr").toString())){
				result.setReturnMessage("保存成功！");
			}else{
				result.setReturnMessage("修改成功！");
			}
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "设置故障诊断参数", type = IMethodLog.InfoType.MOD)
	public String setFaultDiagnoseParam() {
		
		try {
			CommonResult data = faultDiagnoseService.setFaultDiagnoseParam(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取故障诊断参数")
	public String getFaultDiagnoseParam() {
		
		try { 
			
			Map<String, Object> data = faultDiagnoseService.getFaultDiagnoseParam();
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
