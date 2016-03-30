package com.fujitsu.manager.faultManager.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IAlarmConvergeManagerService;
import com.fujitsu.IService.IAlarmConvergeServiceProxy;
import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.util.SpringContextUtil;
public class AlarmConvergeAction extends AbstractAction {
	private static final long serialVersionUID = -218037914366763644L;
	private String jsonString; 
	private Map<String,String> paramMap=new HashMap<String,String>(); 
	private List<Integer> ids;
	@Resource
	public IAlarmConvergeManagerService alarmConvergeManagerService;   
	@Resource
	public IAlarmManagementService alarmManagementService;   
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "取指定网管下的网元型号和工厂信息")
	public String getApplyEquips(){
		try { 
			List<Map> dataRtn = alarmConvergeManagerService.getApplyEquips(ids);  
			Map returnMap=new HashMap();
			returnMap.put("rows", dataRtn);
			returnMap.put("returnResult",CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(returnMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询收敛规则")
	public String searchAlarmConverge(){
		try { 
			Map<String,Object> data=alarmConvergeManagerService.searchAlarmConverge(start,limit);
			data.put("returnResult",CommonDefine.SUCCESS);
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "获取收敛设置时间", type = IMethodLog.InfoType.MOD)
	public String getConvergeTime(){
		try { 
			Map dataRtn = alarmConvergeManagerService.getConvergeTime();  
			Map returnMap=new HashMap();
			returnMap.put("result", dataRtn);
			returnMap.put("returnResult",CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(returnMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
	 
	@IMethodLog(desc = "设置收敛时间", type = IMethodLog.InfoType.MOD)
	public String setConvergeTime(){
		try{ 
			paramMap.put("PARAM_KEY","ALARM_CONVERGE_TIMER"); 
			alarmConvergeManagerService.setConvergeTime(paramMap);  
			result.setReturnResult(CommonDefine.SUCCESS); 
			result.setReturnMessage("设置成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ; 
	} 
	
	@IMethodLog(desc = "新增收敛规则", type = IMethodLog.InfoType.MOD)
	public String addAlarmConvergeRules(){
		try{  
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap = (Map<String, Object>) jsonObject;
			int ruleId = alarmConvergeManagerService.addAlarmConvergeRules(paramsMap,ids);
			
			/** 调用告警收敛接口，增加一条告警收敛规则 **/
			IAlarmConvergeServiceProxy alarmConvergeService = SpringContextUtil.getAlarmConvergeServiceProxy();
			alarmConvergeService.addAlarmConverge(ruleId);
			
			result.setReturnResult(CommonDefine.SUCCESS);  
			result.setReturnMessage("新增成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("新增失败！");
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ; 
	} 
	 
	@IMethodLog(desc = "删除告警规则", type = IMethodLog.InfoType.DELETE)
	public String deleteConvergeRules(){
		try { 
			alarmConvergeManagerService.deleteConvergeRules(jsonString);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	 
	@IMethodLog(desc = "根据ID，查询过规则详细信息")
	public String getAlarmConvergeDetailById(){
		try { 
			Map<String, Object> dataRtn = new HashMap<String, Object>(); 
			dataRtn = alarmConvergeManagerService.getAlarmConvergeDetailById(Integer.valueOf(jsonString)); 
			dataRtn.put("returnResult",CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(dataRtn);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改收敛规则", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmConvergeRules(){
		try { 
			JSONObject jsonObject = JSONObject.fromObject(jsonString); 
			Map<String, Object> param = new HashMap<String, Object>();
			param = (Map<String, Object>) jsonObject; 
			alarmConvergeManagerService.modifyAlarmConvergeRules(param,ids);
			
			/** 调用告警收敛服务接口，启用或挂起一条或多条告警收敛规则 **/
			IAlarmConvergeServiceProxy alarmConvergeService = SpringContextUtil.getAlarmConvergeServiceProxy();
			int ruleId = Integer.valueOf(param.get("CONVERGE_ID").toString());
			alarmConvergeService.updateAlarmConverge(ruleId);
			
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
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "设置收敛规则的状态", type = IMethodLog.InfoType.MOD)
	public String changeConvergeRuleStatus(){
		try {  
			alarmConvergeManagerService.changeConvergeRuleStatus(Integer.valueOf(jsonString),ids);
			result.setReturnResult(CommonDefine.SUCCESS); 
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "模糊查询执行动作中的告警名称", type = IMethodLog.InfoType.MOD)
	public String getAlarmNameByFactory(){
		try {
			Map<String, Object> param = new HashMap<String, Object>(); 
			param.put("factory",-1);
			param.put("alarmName",jsonString);
			param.put("type","current"); 
			List<String> alarmNameList = alarmManagementService.getAlarmNameByFactory(param);
			//将查询结果转成JSON格式
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>(); 
			if("所有".contains(jsonString) || "".equals(jsonString)){
				Map<String, Object> data =new HashMap<String, Object>(); 
				data.put("key","所有");
				list.add(data);
			} 
			for (int i = 0; i < alarmNameList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("key", alarmNameList.get(i));
				list.add(map);
			}
			Map<String, Object> valueMap = new HashMap<String, Object>();
			valueMap.put("rows", list);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(valueMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "判定服务是否启动")
	public String isServerStarted(){ 
		result.setReturnResult(CommonDefine.SUCCESS);  
 		try{
			SpringContextUtil.getAlarmConvergeServiceProxy();
		}catch (Exception ex) { 
			result.setReturnResult(CommonDefine.FAILED);  
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	} 
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "手动执行", type = IMethodLog.InfoType.MOD)
	public String manualActionRules(){ 
		try {  
			/** 调用告警收敛服务接口，手动执行一条或多条告警收敛规则 **/
			IAlarmConvergeServiceProxy alarmConvergeService = SpringContextUtil.getAlarmConvergeServiceProxy();
			//传递到此处的参数为List<Integer> ids;
			boolean rlt = true;
			for (int ruleId : ids) {
				rlt &= alarmConvergeService.manualSatarAlarmConverge(ruleId);
			}
			if (rlt) {
				result.setReturnResult(CommonDefine.SUCCESS);
			} else {
				result.setReturnResult(CommonDefine.FAILED);
			}
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
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
	
}
