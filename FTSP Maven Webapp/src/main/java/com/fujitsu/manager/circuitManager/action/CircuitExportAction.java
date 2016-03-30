package com.fujitsu.manager.circuitManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class CircuitExportAction extends CircuitAction {
	private static final long serialVersionUID = 1L;
	private String displayName;
	// 相关性，端到端，链路查询导出
	public String exportExcel() {
		Map<String, Object> toMap = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map map = (Map) jsonObject;
		map.put("start", 0);
		try {

			String destination = circuitManagerService.exportExcel(map, toMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 导出页面链路信息
	 * @return
	 */
	public String linksOnPageExport(){
		Map<String, Object> toMap = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map map = (Map) jsonObject;
		map.put("start", 0);
		try {

			String destination = circuitManagerService.linksOnPageExport(map, toMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	// 交叉连接导出
	public String exportCrossConnectExcel() {
		// 定义一个map类型变量用来存储查询条件
		Map<String, Object> map = new HashMap<String, Object>();
		List<Integer> list = new ArrayList();
		list.add(1);
		// 将传递过来的jsonString转化成一个map对象
		try {
			// 转化成JSONArray对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map toMap = (Map) jsonObject;
			// 给map赋值
			map.put("connectRate", connectRate);
			map.put("circuitState", circuitState);
			map.put("crossChange", crossChange);
			map.put("isFix", isFix);
			map.put("limit", limit);
			map.put("displayName", displayName);
			map.put("start", 0);
			map.put("flag", 3);

			// 调用方法查询符合条件的记录
			String destination = circuitManagerService.exportExcel(map, toMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 路由详情导出
	 * @return
	 */
	public String exportRoute(){
		Map<String, Object> toMap = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map map = (Map) jsonObject;
		map.put("start", 0);
		try {

			String destination = circuitManagerService.exportRoute(map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
