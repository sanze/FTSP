package com.fujitsu.manager.planManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.planManager.service.TestResultManagementService;

/**
 * @Description: 测试结果管理Action
 * @author liuXin
 * @date 2014-05-08
 * @version V1.0
 */
public class TestResultAction extends AbstractAction{

	@Resource
	public TestResultManagementService testResultManageService;

	private String jsonString;

	/**
	 * 查询测试结果
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String queryTestResults(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			// 添加分页参数
			map.put("start", start);
			
			Map testResult = testResultManageService.queryTestResults(map);
			resultObj = JSONObject.fromObject(testResult);
		} catch (Exception e) {
			e.printStackTrace();
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 查询测试事件
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String queryTestEvents(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> data = testResultManageService.queryTestEvents(map);
			Map rmap = new HashMap();
			rmap.put("rows", data);
			resultObj = JSONObject.fromObject(rmap);
		} catch (Exception e) {
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}


	public String getResultById(){
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			Map resultMap = testResultManageService.getResultById(map);
			
			resultObj = JSONObject.fromObject(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("rawtypes")
	public String getRouteList(){
		try {
			
			List<Map> resultMap = testResultManageService.getRouteList();
			Map rmap = new HashMap();
			rmap.put("rows", resultMap);
			resultObj = JSONObject.fromObject(rmap);
		} catch (Exception e) {
			e.printStackTrace();
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	public String saveToBase() {
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;

			testResultManageService.saveToBase(map);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	
	/**
	 * 导出测试结果
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String exportInfo(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			Map data = testResultManageService.exportInfo(map);
			resultObj = JSONObject.fromObject(data);
		} catch (Exception e) {
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 删除测试结果
	 * @return
	 */
	public String deleteResult() {
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			testResultManageService.deleteResult(map);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(map);
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
	
}
