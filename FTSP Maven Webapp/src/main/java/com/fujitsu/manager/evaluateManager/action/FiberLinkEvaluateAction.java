package com.fujitsu.manager.evaluateManager.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import com.fujitsu.IService.IEvaluateManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class FiberLinkEvaluateAction extends AbstractAction {

	@Resource
	private IEvaluateManagerService evaluateManagerService;

	private Map<String, Object> param = new HashMap<String, Object>();

	@IMethodLog(desc = "光纤链路评估详情：查询")
	public String searchFiberLink() {

		try {
			// 查询光路衰耗信息
			Map<String, Object> object = evaluateManagerService
					.searchFiberLink(param, start, limit);
			resultObj = JSONObject.fromObject(object);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "光纤链路评估设置：获取偏差值信息", type = IMethodLog.InfoType.MOD)
	public String getOffsetValue() {

		// 修改att属性
		Map offsetValue;
		try {
			offsetValue = evaluateManagerService.getOffsetValue();

			resultObj = JSONObject.fromObject(offsetValue);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "光纤链路评估设置：修改偏差值信息", type = IMethodLog.InfoType.MOD)
	public String modifyOffsetValue() {
		try {
			evaluateManagerService.modifyOffsetValue(param.get("upperOffset")
					+ "", param.get("middleOffset") + "",
					param.get("downOffset") + "");

			result.setReturnResult(CommonDefine.SUCCESS);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "光纤链路评估详情：趋势图")
	public String generateDiagram() {
		try {
			Map<String, Object> returnResult = evaluateManagerService
					.generateDiagram(param);
			returnResult.put("returnResult",CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(returnResult);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "光纤链路评估设置：查询", type = IMethodLog.InfoType.MOD)
	public String getAllResourceLink() {

		try {
			// 查询光路衰耗信息
			Map<String, Object> object = evaluateManagerService
					.getAllResourceLink(param, start, limit);
			resultObj = JSONObject.fromObject(object);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "光纤链路评估设置：查询", type = IMethodLog.InfoType.MOD)
	public String deleteResourceLink() {
		try {
			// 查询光路衰耗信息
			evaluateManagerService
					.deleteResourceLink(param);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "光纤链路评估设置：设置", type = IMethodLog.InfoType.MOD)
	public String setResourceLink() {
		try {
			// 查询光路衰耗信息
			evaluateManagerService
					.setResourceLink(param);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	public void setDownOffset(Double offset) {
		param.put("downOffset", offset);
	}

	public void setMiddleOffset(Double offset) {
		param.put("middleOffset", offset);
	}

	public void setUpperOffset(Double offset) {
		param.put("upperOffset", offset);
	}

	public void setNetLevel(Integer val){
		param.put("NET_LEVEL", val);
	}
	
	public void setTransSysId(Integer id) {
		param.put("RESOURCE_TRANS_SYS_ID", id);
	}

	public void setCollectDate(String dateStr) {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
				CommonDefine.COMMON_SIMPLE_FORMAT);
		Date date = null;
		try {
			date = df.parse(dateStr);
			param.put("START_DATE", date);
		} catch (java.text.ParseException e) {
		}
	}
	public void setLinkId(Integer id) {
		param.put("RESOURCE_LINK_ID", id);
	}
	public void setEndDate(String dateStr) {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
				CommonDefine.COMMON_SIMPLE_FORMAT);
		Date date = null;
		try {
			date = df.parse(dateStr);
			param.put("END_DATE", date);
		} catch (java.text.ParseException e) {
		}
	}
	public void setNendOrFend(String direct) {
		param.put("nendOrFend", direct);
	}
	public void setDisplayItems(String displayItems) {
		param.put("displayItems", displayItems);
	}
	
	//评估设置:设置
	public void setFEND_LINK_ID(Integer id) {
		param.put("FEND_LINK_ID", id);
	}
	public void setRESOURCE_FIBER_ID(Integer id) {
		param.put("RESOURCE_FIBER_ID", id);
	}
	public void setA_PTP_MAIN(Integer id) {
		param.put("A_PTP_MAIN", id);
	}
	public void setZ_PTP_MAIN(Integer id) {
		param.put("Z_PTP_MAIN", id);
	}
	public void setA_PTP_OSC(Integer id) {
		param.put("A_PTP_OSC", id);
	}
	public void setZ_PTP_OSC(Integer id) {
		param.put("Z_PTP_OSC", id);
	}
	public void setATT_MAIN(Double id) {
		param.put("ATT_MAIN", id);
	}
	public void setATT_OSC(Double id) {
		param.put("ATT_OSC", id);
	}
}
