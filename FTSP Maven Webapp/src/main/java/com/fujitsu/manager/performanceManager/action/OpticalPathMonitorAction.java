package com.fujitsu.manager.performanceManager.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.model.OpticalPathMonitorModel;

public class OpticalPathMonitorAction extends AbstractAction {

	@Resource
	private IPerformanceManagerService performanceManagerService;

	private OpticalPathMonitorModel opticalPathMonitorModel;
	
	private String jsonString;

	@IMethodLog(desc = "光路衰耗监测：查询")
	public String searchOpticalPath() {

		List<Map> transferTarget = ListStringtoListMap(opticalPathMonitorModel.getSelectTargets());
		//选中目标格式转换
		opticalPathMonitorModel.setSelectTargetsMap(transferTarget);
		
		Map object = null;

		try {
			//查询光路衰耗信息
			object = performanceManagerService.searchOpticalPath(opticalPathMonitorModel,start,
					limit);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(object);
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "光路衰耗监测：修改ATT", type = IMethodLog.InfoType.MOD)
	public String modifyAttForLink() {
		
		List<OpticalPathMonitorModel> list = new ArrayList<OpticalPathMonitorModel>();
		OpticalPathMonitorModel model = null;
		// 转化成JSONArray对象
		JSONArray jsonArray = JSONArray.fromObject(jsonString);

		for (Object obj : jsonArray) {
			// 转成OpticalPathMonitorModel对象
			JSONObject jsonObject = (JSONObject) obj;
			model = (OpticalPathMonitorModel) JSONObject.toBean(
					jsonObject, OpticalPathMonitorModel.class);
			list.add(model);
		}
		try {
			//修改att属性
			performanceManagerService.modifyAttForLink(list);
			
			result.setReturnResult(CommonDefine.SUCCESS);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "光路衰耗监测：当前值刷新")
	public String fulshCurrentPm() {
		
		List<OpticalPathMonitorModel> list = new ArrayList<OpticalPathMonitorModel>();
		OpticalPathMonitorModel model = null;
		// 转化成JSONArray对象
		JSONArray jsonArray = JSONArray.fromObject(jsonString);

		for (Object obj : jsonArray) {
			// 转成OpticalPathMonitorModel对象
			JSONObject jsonObject = (JSONObject) obj;
			model = (OpticalPathMonitorModel) JSONObject.toBean(
					jsonObject, OpticalPathMonitorModel.class);
			list.add(model);
		}
		try {
			//修改att属性
			performanceManagerService.fulshCurrentPm(list);
			
			result.setReturnResult(CommonDefine.SUCCESS);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "光路衰耗监测：获取偏差值信息")
	public String getOffsetValue() {
		
		//修改att属性
		Map offsetValue;
		try {
			offsetValue = performanceManagerService.getOffsetValue();
			
			resultObj = JSONObject.fromObject(offsetValue);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "光路衰耗监测：修改偏差值信息", type = IMethodLog.InfoType.MOD)
	public String modifyOffsetValue() {
		try {
			performanceManagerService.modifyOffsetValue(opticalPathMonitorModel.getUpperOffset(),opticalPathMonitorModel.getDownOffset());
		
			result.setReturnResult(CommonDefine.SUCCESS);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);
		
		return RESULT_OBJ;
	}

	public OpticalPathMonitorModel getOpticalPathMonitorModel() {
		return opticalPathMonitorModel;
	}

	public void setOpticalPathMonitorModel(
			OpticalPathMonitorModel opticalPathMonitorModel) {
		this.opticalPathMonitorModel = opticalPathMonitorModel;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	

}
