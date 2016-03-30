package com.fujitsu.manager.planManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IPlanManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

/**
 * @Description：
 * @author cao senrong
 * @date 2015-1-15
 * @version V1.0
 */
public class PlanAction extends AbstractAction {
	@Resource
	private IPlanManagerService planManagerService;
	
	private String jsonString;
	private List<String> modifyList;
	private Map<String, String> displayCond;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getPlanList(){
		try {
			Map map = new HashMap();
			map.put("RESOURCE_AREA_ID", jsonString);
			
			List<Map> dataList = planManagerService.getPlanList(map);
			// 将返回的结果转成JSON对象，返回前台
			Map rmap = new HashMap();
			rmap.put("rows", dataList);
			rmap.put("total", dataList.size());
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getRouteListByPlanId(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> dataList = planManagerService.getRouteListByPlanId(map);
			// 将返回的结果转成JSON对象，返回前台
			Map rmap = new HashMap();
			rmap.put("rows", dataList);
			rmap.put("total", dataList.size());
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@SuppressWarnings("rawtypes")
	public String modifyTestRoutePara(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			planManagerService.modifyTestRoutePara(map);
			
			
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("rawtypes")
	public String startUpPlan(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			planManagerService.updateTestPlanStatusStartUp(map);
			
			
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("rawtypes")
	public String pendingPlan(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			planManagerService.updateTestPlanStatusPending(map);
			
			
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("rawtypes")
	public String modifyTestRouteValue(){
		try {

			List<Map> testRouteValueList = ListStringtoListMap(this.modifyList);
			planManagerService.modifyTestRouteValueBatch(testRouteValueList);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	
	
	
	@SuppressWarnings({ "rawtypes" })
	public String getTriggerAlarmStatus(){
		try {
			
			Map map = planManagerService.getAlarmTriggerStatus();
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public String modifyAlarmTriggerStstus(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			planManagerService.modifyAlarmTriggerStstus(map);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getTriggerAlarm(){
		try {
			
			List<Map> dataList = planManagerService.getTriggerAlarm();
			// 将返回的结果转成JSON对象，返回前台
			Map map = new HashMap();
			map.put("rows", dataList);
			map.put("total", dataList.size());
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("rawtypes")
	public String modifyTriggerAlarm(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			planManagerService.modifyTriggerAlarm(map);
			
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	public String InitAlarm2Route(){
		try {
			planManagerService.InitAlarm2Route();
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getWaveLengthList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> data = planManagerService.getWaveLengthList(map);
			
//			int otdrType = planManagerService.getOTDRType(map);
			
			Map rmap = new HashMap();
			rmap.put("rows", data);
//			rmap.put("otdrType", otdrType);
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getRangeList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> data = planManagerService.getRangeList(map);
			Map rmap = new HashMap();
			rmap.put("rows", data);
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getPluseWidthList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> data = planManagerService.getPluseWidthList(map);
			Map rmap = new HashMap();
			rmap.put("rows", data);
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "趋势图:测试结果趋势图")
	public String generateDiagram() {
		String returnString = RESULT_OBJ;
		try {
			String xmlString = planManagerService.generateDiagram(displayCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(xmlString);
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
	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<String> getModifyList() {
		return modifyList;
	}

	public void setModifyList(List<String> modifyList) {
		this.modifyList = modifyList;
	}
	
	public Map<String, String> getDisplayCond() {
		return displayCond;
	}
	
	public void setDisplayCond(Map<String, String> displayCond) {
		this.displayCond = displayCond;
	}
}
