package com.fujitsu.manager.evaluateManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IEvaluateManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class EvaluateStatisticAction extends AbstractAction {

	@Resource
	private IEvaluateManagerService evaluateManagerService;

	private Map<String, Object> param = new HashMap<String, Object>();

 	@IMethodLog(desc = "光纤链路评估：趋势图")
	public String generateDiagramLine() {
		try {
			Map<String, Object> data = evaluateManagerService.generateDiagramLine(param);
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ;
	} 

 	@IMethodLog(desc = "光纤链路评估：趋势图")
	public String generateDiagramTable() {
		try {
			Map<String, Object> data = evaluateManagerService.generateDiagramTable(param);
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ;
	} 

 	public void setNetLevel(Integer val){
		param.put("NET_LEVEL", val);
	}
	
	public void setTransSysId(Integer id) {
		param.put("RESOURCE_TRANS_SYS_ID", id);
	}

	public void setMonth(String month) {
		param.put("month", month);
	} 
}
