package com.fujitsu.manager.alarmManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonResult;
import com.fujitsu.dao.mysql.AreaManagerMapper;

public class AlarmAction extends AbstractAction {
	
	private String jsonString;
	
	@Resource
	public IAlarmManagementService alarmManagementService;
	@Resource
	public IAreaManagerService areaManagerService;
	@Resource
	public AreaManagerMapper areaManagerMapper;
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询当前告警")
	public String queryCurrentAlarm() {
		
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, Object> map = (Map<String, Object>) jsonObject;
			
			Map<String, Object> resultMap = alarmManagementService.
									queryCurrentAlarm(map, start, limit);
			
			resultObj = JSONObject.fromObject(resultMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询历史告警")
	public String queryHistoryAlarm() {
		
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, Object> map = (Map<String, Object>) jsonObject;
			
			Map<String, Object> resultMap = alarmManagementService.
									queryHistoryAlarm(map, start, limit);
			resultObj = JSONObject.fromObject(resultMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取告警同步时的区域信息")
	public String getAlarmSyncArea() {
		
		try {
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("rows", areaManagerMapper.getSubAreaByParentIds("(1)"));
			resultObj = JSONObject.fromObject(resultMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "获取告警同步时的局站信息")
	public String getAlarmSyncStation() {
		
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, Object> map = (Map<String, Object>) jsonObject;
			int areaId = Integer.parseInt(map.get("areaId").toString());
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("rows", areaManagerService.getStationListByAreaId(areaId));
			resultObj = JSONObject.fromObject(resultMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "获取告警同步时的设备信息")
	public String getAlarmSyncEquip() {
		
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, Object> map = (Map<String, Object>) jsonObject;
			Map<String, Object> resultMap = alarmManagementService.getAlarmSyncEquip(map);
			resultObj = JSONObject.fromObject(resultMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "同步告警")
	public String syncAlarm() {
		
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, Object> map = (Map<String, Object>) jsonObject;
			
			Map<String, Object> resultMap = alarmManagementService.syncAlarm(map);
			resultObj = JSONObject.fromObject(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "告警确认", type = IMethodLog.InfoType.MOD)
	public String confirmAlarm() {
		
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, Object> map = (Map<String, Object>) jsonObject;
			CommonResult resultMap = alarmManagementService.confirmAlarm(map);
			resultObj = JSONObject.fromObject(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	/**
	 * 获取首页告警统计数据
	 * @return
	 */
//	public String getAlarmCountForFP() {
//		
//		try {
//			
//			Map<String, Integer> resultMap = alarmManagementService.
//					getAlarmCountForFP(getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		} catch (Exception e) {
//			
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 设置告警屏蔽规则
	 * @return
	 */
//	public String setAlarmShieldRule(){
//		
//		try {
//			Map<String, Object> map = new HashMap<String, Object>();
//			//获取sort和参数封装后的map
//			map = getParameterMap(jsonString, sort);
//			
//			CommonResult resultMap = alarmManagementService.
//						setAlarmShieldRules(map, getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 获取设备告警、线路告警屏蔽规则
	 * @return
	 */
//	public String getELShieldRule(){
//		
//		try{
//			Map<String, Object> map = new HashMap<String, Object>();
//			//获取sort和参数封装后的map
//			map = getParameterMap(jsonString, sort);
//			
//			Map<String, Object> resultMap = alarmManagementService.
//												getELShieldRule(map, getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 获取网管告警屏蔽规则
	 * @return
	 */
//	public String getEMSShieldRule(){
//		
//		try{
//			
//			Map<String, Object> resultMap = alarmManagementService.
//										getEMSShieldRule(getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 获取机房信息
	 * @return
	 */
//	public String getStationsInRegion(){
//		
//		try{
//			//获取sort和参数封装后的map
//			Map<String, Object> map = getParameterMap(jsonString, sort);
//			
//			Map<String, Object> resultMap = alarmManagementService.
//											getStationsInRegion(map, getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		}catch(Exception e){
//			
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 获取前台combox用的区域信息
	 * @return
	 */
//	public String queryRegionForCombox() {
//		
//		try{
//			
//			Map<String, Object> resultMap = alarmManagementService.
//										queryRegionForCombox(getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 获取设备名称
	 * @return
	 */
//	public String getEqptName(){
//		
//		try{
//			//获取sort和参数封装后的map
//			Map<String, Object> map = getParameterMap(jsonString, sort);
//			
//			Map<String, Object> resultMap = alarmManagementService.
//										getEqptName(map, getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 获取同步的设备信息（用于前台的combox显示）
	 * @return
	 */
//	public String getSyncEqptInfo() {
//		
//		try{
//			//获取sort和参数封装后的map
//			Map<String, Object> map = getParameterMap(jsonString, sort);
//			
//			Map<String, Object> resultMap = alarmManagementService.
//												getSyncEqptInfo(map, getCurrentUserId());
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return RESULT_OBJ;
//	}
	
	/**
	 * 通过测试结果ID获取测试计划ID
	 * @return
	 */
//	public String getPlanIdByTestResultId() {
//		
//		try{
//			//获取sort和参数封装后的map
//			Map<String, Object> map = getParameterMap(jsonString, sort);
//			
//			Map<String, Object> resultMap = alarmManagementService.
//												getPlanIdByTestResultId(map);
//			
//			resultObj = JSONObject.fromObject(resultMap);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return RESULT_OBJ;
//	}
	
//	public String test() {
//		
//		alarmManagementService.test();
//		
//		return RESULT_OBJ;
//	}
	
	
	
	
	
	
	
}
