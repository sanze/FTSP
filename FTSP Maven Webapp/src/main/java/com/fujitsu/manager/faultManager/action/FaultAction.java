package com.fujitsu.manager.faultManager.action;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jxl.format.Colour;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.reportManager.util.ReportExportExcel;
import com.mongodb.DBObject;

public class FaultAction extends DownloadAction {

	private static final long serialVersionUID = -218037914366763644L;
	@Resource
	public IAlarmManagementService alarmManagementService;
	// 查询参数
	private String jsonString;
	public String query;
	
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Method name: getAllEmsGroups <BR>
	 * Description: 查询所有网管分组<BR>
	 * Remark: 2013-11-15<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
//	@IMethodLog(desc = "查询所有网管分组")
//	public String getAllEmsGroups(){
//		try {
//			// 查询所有网管分组
//			Map<String, Object> emsGroupMap = faultManagerService.getAllEmsGroups();
//			// 将返回的结果转成JSON对象，返回前台
//			resultObj = JSONObject.fromObject(emsGroupMap);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
//		return RESULT_OBJ;
//	}
	
	/**
	 * Method name: getAllEmsGroupsNoAll <BR>
	 * Description: 查询所有网管分组(不包括全部)<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
//	@IMethodLog(desc = "查询所有网管分组(不包括全部)")
//	public String getAllEmsGroupsNoAll(){
//		try {
//			// 查询所有网管分组
//			Map<String, Object> emsGroupMap = faultManagerService.getAllEmsGroupsNoAll();
//			// 将返回的结果转成JSON对象，返回前台
//			resultObj = JSONObject.fromObject(emsGroupMap);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
//		return RESULT_OBJ;
//	}
//	
	/**
	 * Method name: getAllEmsByEmsGroupId <BR>
	 * Description: 查询某个网管分组的所有网管<BR>
	 * Remark: 2013-11-15<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
//	@SuppressWarnings("unchecked")
//	@IMethodLog(desc = "查询某个网管分组下的所有网管")
//	public String getAllEmsByEmsGroupId(){
//		try {
//			// 将参数专程JSON对象
//			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			// 将JSON对象转成Map对象
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap = (Map<String, Object>) jsonObject;
//			// 定义一个Map接受查询返回的值
//			Map<String, Object> emsMap = faultManagerService.getAllEmsByEmsGroupId(paramMap);
//			// 将返回的结果转成JSON对象，返回前台
//			resultObj = JSONObject.fromObject(emsMap);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
//		return RESULT_OBJ;
//	}
	
	/**
	 * Method name: getAllEmsByEmsGroupIdNoAll <BR>
	 * Description: 查询某个网管分组的所有网管(不包括全部)<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
//	@SuppressWarnings("unchecked")
//	@IMethodLog(desc = "查询某个网管分组下的所有网管(不包括全部)")
//	public String getAllEmsByEmsGroupIdNoAll(){
//		try {
//			// 将参数专程JSON对象
//			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			// 将JSON对象转成Map对象
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap = (Map<String, Object>) jsonObject;
//			// 定义一个Map接受查询返回的值
//			Map<String, Object> emsMap = faultManagerService.getAllEmsByEmsGroupIdNoAll(paramMap);
//			// 将返回的结果转成JSON对象，返回前台
//			resultObj = JSONObject.fromObject(emsMap);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
//		return RESULT_OBJ;
//	}
	
	/**
	 * Method name: getAllEmsByEmsGroupId <BR>
	 * Description: 模糊查询某个网管下的所有网元<BR>
	 * Remark: 2013-11-19<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
//	@SuppressWarnings("unchecked")
//	@IMethodLog(desc = "模糊查询某个网管下的所有网元")
//	public String getAllNeByEmsIdAndNename(){
//		try {
//			// 将参数专程JSON对象
//			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			// 将JSON对象转成Map对象
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap = (Map<String, Object>) jsonObject;
//			// 定义一个Map接受查询返回的值
//			Map<String, Object> neMap = faultManagerService.getAllNeByEmsIdAndNename(paramMap);
//			// 将返回的结果转成JSON对象，返回前台
//			resultObj = JSONObject.fromObject(neMap);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
//		return RESULT_OBJ;
//	}
	
	/**
	 * Method name: getCurrentAlarms <BR>
	 * Description: 根据网管分组ID、网管ID、网元ID、告警级别、分页信息、过滤器信息，查询当前告警信息<BR>
	 * Remark: 2013-11-22<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询当前告警")
	public String getCurrentAlarms(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> currentAlarmMap = alarmManagementService.getCurrentAlarms(paramMap, start, limit,sysUserId);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(currentAlarmMap,cfg);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getCurrentAlarmCount <BR>
	 * Description: 根据网管分组ID、网管ID、网元ID、告警级别，查询某告警级别的当前告警数<BR>
	 * Remark: 2013-11-26<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
//	@SuppressWarnings("unchecked")
//	@IMethodLog(desc = "查询某告警级别的当前告警数")
//	public String getCurrentAlarmCount(){
//		try {
//			// 将参数专程JSON对象
//			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			// 将JSON对象转成Map对象
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap = (Map<String, Object>) jsonObject;
//			// 定义一个Map接受查询返回的值
//			Map<String, Object> currentAlarmCountMap = faultManagerService.getCurrentAlarmCount(paramMap);
//			// 将返回的结果转成JSON对象，返回前台
//			resultObj = JSONObject.fromObject(currentAlarmCountMap);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
//		return RESULT_OBJ;
//	}
	
	/**
	 * Method name: getAllCurrentAlarmCount <BR>
	 * Description: 查询所有告警级别的当前告警数<BR>
	 * Remark: 2014-02-24<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询所有告警级别的当前告警数")
	public String getAllCurrentAlarmCount(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> currentAlarmCountMap = alarmManagementService.getAllCurrentAlarmCount(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(currentAlarmCountMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmDetail <BR>
	 * Description: 根据告警ID、告警种类(当前、历史)，查询告警详情<BR>
	 * Remark: 2013-11-26<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询告警详情")
	public String getAlarmDetail(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 将返回的结果转成JSON对象，返回前台
			resultObj = alarmManagementService.getAlarmDetail(paramMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getHistoryAlarms <BR>
	 * Description: 根据网元ID、首次发生时间、清除时间、分页信息，查询历史告警信息<BR>
	 * Remark: 2013-11-20<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询历史告警")
	public String getHistoryAlarms() throws ParseException{
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> neMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			neMap = alarmManagementService.getHistoryAlarms(paramMap, start, limit);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(neMap,cfg);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmNameByFactory <BR>
	 * Description: 查询某厂家的所有告警名称<BR>
	 * Remark: 2013-12-12<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询某厂家的所有告警名称")
	public String getAlarmNameByFactory(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 查询某厂家的所有告警名称
			List<String> alarmNameList = alarmManagementService.getAlarmNameByFactory(paramMap);
			//将查询结果转成JSON格式
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
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
	
	/**
	 * Method name: getAlarmNameByFactoryAndShield <BR>
	 * Description: 查询屏蔽器中某厂家的所有告警名称<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询屏蔽器中某厂家的所有告警名称")
	public String getAlarmNameByFactoryFromShield(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 查询某厂家的所有告警名称
			Map<String, Object> valueMap = alarmManagementService.getAlarmNameByFactoryFromShield(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(valueMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	/**
	 * Method name: getAlarms_High <BR>
	 * Description: 高级查询当前告警信息<BR>
	 * Remark: 2013-12-16<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "高级查询当前告警")
	public String getAlarms_High() throws ParseException{
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmMap = alarmManagementService.getAlarms_High(paramMap, start, limit);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmMap, cfg);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmFiltersByUserId <BR>
	 * Description: 根据创建人ID,查询告警过滤器信息<BR>
	 * Remark: 2013-12-24<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 * @throws ParseException 
	 */
	@IMethodLog(desc = "根据创建人ID,查询告警过滤器信息")
	public String getAlarmFiltersByUserId() throws ParseException{
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmFilterMap = new HashMap<String, Object>();
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 登录人ID
			paramMap.put("sysUserId", sysUserId);
			alarmFilterMap = alarmManagementService.getAlarmFiltersByUserId(paramMap, start, limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmFilterMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 根据创建人ID，获取过滤器ID和名称信息
	 */
	@IMethodLog(desc = "根据创建人ID,查询告警过滤器摘要信息")
	public String getAlarmFiltersSummaryByUserId() {
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmFilterMap = new HashMap<String, Object>();
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 登录人ID
			paramMap.put("sysUserId", sysUserId);
			alarmFilterMap = alarmManagementService.getAlarmFiltersSummaryByUserId(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmFilterMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		System.out.println(resultObj);
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmFiltersComReportByUserId <BR>
	 * Description: 根据创建人ID,查询综告接口过滤器信息<BR>
	 * Remark: 2014-01-26<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 * @throws ParseException 
	 */
	@IMethodLog(desc = "根据创建人ID,查询综告接口过滤器信息")
	public String getAlarmFiltersComReportByUserId() throws ParseException{
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmFilterMap = new HashMap<String, Object>();
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 登录人ID
			paramMap.put("sysUserId", sysUserId);
			alarmFilterMap = alarmManagementService.getAlarmFiltersComReportByUserId(paramMap, start, limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmFilterMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: addAlarmFilter <BR>
	 * Description: 新增当前告警过滤器<BR>
	 * Remark: 2013-12-16<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "新增当前告警过滤器", type = IMethodLog.InfoType.MOD)
	public String addAlarmFilter(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.addAlarmFilter(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: addAlarmFilterComReport <BR>
	 * Description: 新增综告接口过滤器<BR>
	 * Remark: 2014-01-26<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "新增综告接口过滤器", type = IMethodLog.InfoType.MOD)
	public String addAlarmFilterComReport(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.addAlarmFilterComReport(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getDetailByNodeLevel <BR>
	 * Description: 根据不同节点级别查询详细信息<BR>
	 * Remark: 2014-01-06<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据不同节点级别查询详细信息")
	public String getDetailByNodeLevel(){
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> detailMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			detailMap = alarmManagementService.getDetailByNodeLevel(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(detailMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAllNeModelByFactory <BR>
	 * Description: 查询某厂家的所有网元型号<BR>
	 * Remark: 2014-01-08<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询某厂家的所有网元型号")
	public String getAllNeModelByFactory(){
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> neModelMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			neModelMap = alarmManagementService.getAllNeModelByFactory(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(neModelMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmFilterDetailById <BR>
	 * Description: 根据ID，查询过滤器详细信息<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据ID，查询过滤器详细信息")
	public String getAlarmFilterDetailById(){
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> filterDetailMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			if("first".equals(paramMap.get("flag").toString())){
				filterDetailMap = alarmManagementService.getAlarmFilterFirstDetailById(paramMap);
			}else if("second".equals(paramMap.get("flag").toString())){
				filterDetailMap = alarmManagementService.getAlarmFilterSecondDetailById(paramMap);
			}else{
				filterDetailMap = alarmManagementService.getAlarmFilterThirdDetailById(paramMap);
			}
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(filterDetailMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: modifyAlarmFilter <BR>
	 * Description: 修改当前告警过滤器<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改当前告警过滤器", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmFilter(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.modifyAlarmFilter(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: modifyAlarmFilter <BR>
	 * Description: 修改综告接口过滤器<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改综告接口过滤器", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmFilterComReport(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.modifyAlarmFilterComReport(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: deleteAlarmFilter <BR>
	 * Description: 删除当前告警过滤器<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "删除当前告警过滤器", type = IMethodLog.InfoType.DELETE)
	public String deleteAlarmFilter(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.deleteAlarmFilter(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: updateAlarmFilterStatus <BR>
	 * Description: 更新当前告警过滤器状态<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "更新当前告警过滤器状态", type = IMethodLog.InfoType.MOD)
	public String updateAlarmFilterStatus(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			paramMap.put("sysUserId", sysUserId);
			alarmManagementService.updateAlarmFilterStatus(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("更新状态成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAllAlarmShields <BR>
	 * Description: 查询所有告警屏蔽<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询所有告警屏蔽")
	public String getAllAlarmShield() throws ParseException{
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmShieldrMap = new HashMap<String, Object>();
			alarmShieldrMap = alarmManagementService.getAllAlarmShield(paramMap,start, limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmShieldrMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: addAlarmShield <BR>
	 * Description: 新增告警屏蔽器<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "新增告警屏蔽器", type = IMethodLog.InfoType.MOD)
	public String addAlarmShield(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.addAlarmShield(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getSimpleByNodeLevel <BR>
	 * Description: 根据不同节点级别查询简单信息<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据不同节点级别查询简单信息")
	public String getSimpleByNodeLevel(){
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> detailMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			detailMap = alarmManagementService.getSimpleByNodeLevel(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(detailMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: deleteAlarmShield <BR>
	 * Description: 删除告警屏蔽器<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "删除告警屏蔽器", type = IMethodLog.InfoType.DELETE)
	public String deleteAlarmShield(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.deleteAlarmShield(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmShieldDetailById <BR>
	 * Description: 根据ID，查询屏蔽器详细信息<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据ID，查询过滤器详细信息")
	public String getAlarmShieldDetailById(){
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> shieldDetailMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			if("first".equals(paramMap.get("flag").toString())){
				shieldDetailMap = alarmManagementService.getAlarmShieldFirstDetailById(paramMap);
			}else{
				shieldDetailMap = alarmManagementService.getAlarmShieldSecondDetailById(paramMap);
			}
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(shieldDetailMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: modifyShieldFilter <BR>
	 * Description: 修改告警屏蔽器<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改告警屏蔽器", type = IMethodLog.InfoType.MOD)
	public String modifyShieldFilter(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.modifyAlarmShield(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: updateAlarmFilterStatus <BR>
	 * Description: 更新告警屏蔽器状态<BR>
	 * Remark: 2014-01-17<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "更新告警屏蔽器状态", type = IMethodLog.InfoType.MOD)
	public String updateAlarmShieldStatus(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.updateAlarmShieldStatus(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("更新状态成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: alarmSynch <BR>
	 * Description: 告警同步<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "告警同步")
	public String alarmSynch(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.alarmSynch(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("同步成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * Method name: getAlarmAutoConfirmByEmsGroup <BR>
	 * Description: 根据网管分组ID,查询告警自动确认设置<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据网管分组ID,查询告警自动确认设置", type = IMethodLog.InfoType.MOD)
	public String getAlarmAutoConfirmByEmsGroup(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmAutoConfirmMap = alarmManagementService.getAlarmAutoConfirmByEmsGroup(sysUserId,paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmAutoConfirmMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: modifyAlarmAutoConfirm <BR>
	 * Description: 修改告警自动确认<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改告警自动确认", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmAutoConfirm(){
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);
			List<Map<String, Object>> recordsList = new ArrayList<Map<String, Object>>();
			for (Object obj : jsonArray) {
				// 将JSON对象转成Map对象
				JSONObject jsonObject = (JSONObject) obj;
				Map<String, Object> map = (Map<String, Object>) jsonObject;
				recordsList.add(map);
			}
			alarmManagementService.modifyAlarmAutoConfirm(recordsList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: confirmSet <BR>
	 * Description: 确认设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "确认设置", type = IMethodLog.InfoType.MOD)
	public String confirmSet(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.confirmSet(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 *  @@@分权分域到网元@@@
	 * Method name: getAlarmRedefineByEmsGroup <BR>
	 * Description: 根据网管分组ID,查询告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据网管分组ID,查询告警重定义设置", type = IMethodLog.InfoType.MOD)
	public String getAlarmRedefineByEmsGroup(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmRedefineMap = alarmManagementService.getAlarmRedefineByEmsGroup(sysUserId,paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmRedefineMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: addAlarmRedefine <BR>
	 * Description: 新增告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "新增告警重定义设置", type = IMethodLog.InfoType.MOD)
	public String addAlarmRedefine(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.addAlarmRedefine(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("新增成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: deleteAlarmRedefine <BR>
	 * Description: 删除告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "删除告警重定义设置", type = IMethodLog.InfoType.MOD)
	public String deleteAlarmRedefine(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.deleteAlarmRedefine(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmRedefineById <BR>
	 * Description: 根据ID,查询告警及事件重定义<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据ID,查询告警及事件重定义")
	public String getAlarmRedefineById(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmRedefineMap = alarmManagementService.getAlarmRedefineById(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmRedefineMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: modifyAlarmRedefine <BR>
	 * Description: 修改告警重定义设置<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改告警重定义设置", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmRedefine(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.modifyAlarmRedefine(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: updateAlarmRedefineStatus <BR>
	 * Description: 更新告警重定义状态<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "更新告警重定义状态", type = IMethodLog.InfoType.MOD)
	public String updateAlarmRedefineStatus(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.updateAlarmRedefineStatus(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("更新状态成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	
	
	
	
	
	
	
	
	//--------------
	
	
	/**
	 * 
	 * Method name: modifyAlarmAutoSynch <BR>
	 * Description: 修改告警自动同步 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改告警自动同步", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmAutoSynch(){
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);
			List<Map<String, Object>> recordsList = new ArrayList<Map<String, Object>>();
			for (Object obj : jsonArray) {
				// 将JSON对象转成Map对象
				JSONObject jsonObject = (JSONObject) obj;
				Map<String, Object> map = (Map<String, Object>) jsonObject;
				recordsList.add(map);
			}
		//TODO	
			alarmManagementService.modifyAlarmAutoSynch(recordsList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * Method name: getAlarmAutoSynchByEmsGroup <BR>
	 * Description: 根据网管分组ID,查询告警自动同步 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据网管分组ID,查询告警自动同步")
	public String getAlarmAutoSynchByEmsGroup(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			//TODO
			Map<String, Object> alarmAutoConfirmMap = alarmManagementService.getAlarmAutoSynchByEmsGroup(sysUserId,paramMap,start, limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmAutoConfirmMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功");
			//System.out.println(resultObj);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据厂家ID,查询告警归一化设置", type = IMethodLog.InfoType.MOD)
	public String getAlarmNormlizedByEmsGroup(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmRedefineMap = alarmManagementService.getAlarmNormlizedByFactory(paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmRedefineMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 
	 * Method name: getAlarmNormlizedById <BR>
	 * Description: 根据ID,查询归一化设置 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据ID,查询归一化设置", type = IMethodLog.InfoType.MOD)
	public String getAlarmNormlizedById(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmRedefineMap = alarmManagementService.getAlarmNormlizedById(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmRedefineMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 
	 * Method name: addAlarmNormlized <BR>
	 * Description: 新增归一化设置 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "新增归一化设置", type = IMethodLog.InfoType.MOD)
	public String addAlarmNormlized(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.addAlarmNormlized(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("新增成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 
	 * Method name: deleteAlarmNormlized <BR>
	 * Description: 删除归一化设置 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "删除归一化设置", type = IMethodLog.InfoType.MOD)
	public String deleteAlarmNormlized(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.deleteAlarmNormlized(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	
	/**
	 * 
	 * Method name: modifyAlarmNormlized <BR>
	 * Description: 修改告归一化设置 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "修改告归一化设置", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmNormlized(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.modifyAlarmNormlized(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	

	
	/**
	 * Method name: modifyalarmPush <BR>
	 * Description: 告警推送设置<BR>
	 * Remark: 2014-01-27<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "更新告警推送设置", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmPush(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.modifyAlarmPush(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("设置成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmPush <BR>
	 * Description: 查询告警推送设置<BR>
	 * Remark: 2014-01-28<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@IMethodLog(desc = "查询告警推送设置", type = IMethodLog.InfoType.MOD)
	public String getAlarmPush(){
		try {
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmPushMap = alarmManagementService.getAlarmPush();
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmPushMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: modifyAlarmConfirmShift <BR>
	 * Description: 更新告警自动确认、转移设置<BR>
	 * Remark: 2014-01-28<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "更新告警自动确认、转移设置", type = IMethodLog.InfoType.MOD)
	public String modifyAlarmConfirmShift(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			alarmManagementService.modifyAlarmConfirmShift(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("设置成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmConfirmShift <BR>
	 * Description: 查询告警自动确认、转移设置<BR>
	 * Remark: 2014-01-28<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询告警自动确认、转移设置", type = IMethodLog.InfoType.MOD)
	public String getAlarmConfirmShift(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> alarmConfirmShiftMap = alarmManagementService.getAlarmConfirmShift(paramMap);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(alarmConfirmShiftMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: alarmManualConfirm <BR>
	 * Description: 告警手动确认<BR>
	 * Remark: 2014-02-14<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "告警手动确认", type = IMethodLog.InfoType.MOD)
	public String alarmManualConfirm(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 执行告警手动确认
			alarmManagementService.alarmManualConfirm(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("确认成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: alarmAntiConfirm <BR>
	 * Description: 告警反确认<BR>
	 * Remark: 2014-02-14<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "告警反确认", type = IMethodLog.InfoType.MOD)
	public String alarmAntiConfirm(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 执行告警手动确认
			alarmManagementService.alarmAntiConfirm(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("反确认成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAlarmColorSet <BR>
	 * Description: 查询告警颜色设置<BR>
	 * Remark: 2014-03-06<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@IMethodLog(desc = "查询告警颜色设置", type = IMethodLog.InfoType.MOD)
	public String getAlarmColorSet(){
		try {
			Map<String, Object> alarmColorMap = alarmManagementService.getAlarmColorSet();
			resultObj = JSONObject.fromObject(alarmColorMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	 public static Colour getNearestColour(String strColor) {  
	        Color cl = Color.decode(strColor);  
	        Colour color = null;  
	        Colour[] colors = Colour.getAllColours();  
	        if ((colors != null) && (colors.length > 0)) {  
	           Colour crtColor = null;  
	           int[] rgb = null;  
	           int diff = 0;  
	           int minDiff = 999;  
	           for (int i = 0; i < colors.length; i++) {  
	                crtColor = colors[i];  
	                rgb = new int[3];  
	                rgb[0] = crtColor.getDefaultRGB().getRed();  
	                rgb[1] = crtColor.getDefaultRGB().getGreen();  
	                rgb[2] = crtColor.getDefaultRGB().getBlue();  
	      
	                diff = Math.abs(rgb[0] - cl.getRed())  
	                  + Math.abs(rgb[1] - cl.getGreen())  
	                  + Math.abs(rgb[2] - cl.getBlue());  
	                if (diff < minDiff) {  
	                 minDiff = diff;  
	                 color = crtColor;  
	                }  
	           }  
	        }  
	        if (color == null)  
	           color = Colour.BLACK;  
	        return color;  
	    }  

	
	
	@IMethodLog(desc = "导出当前告警")
	@SuppressWarnings("unchecked")
	public String downloadCurrentAlarmResult() throws CommonException{
			 // 将参数专程JSON对象
			 JSONObject jsonObject = JSONObject.fromObject(jsonString);
			 // 将JSON对象转成Map对象
			 Map<String, Object> paramMap = new HashMap<String, Object>();
			 paramMap = (Map<String, Object>) jsonObject;
			 // 定义一个Map接受查询返回的值
			 Map<String, Object> currentAlarmMap = alarmManagementService.getCurrentAlarms(paramMap,0,limit,sysUserId);
			 List<DBObject> list = (List<DBObject>)currentAlarmMap.get("rows");
			 Map<String, Object> colors=alarmManagementService.getAlarmColorSet();
			 
			 //获取隐藏的列
			 Object hiddenColumns=paramMap.get("hiddenColoumms");
			 List<String> hColumns=new ArrayList<String>();
			 if(hiddenColumns!=null && !"".equals((String)hiddenColumns)){
				String[] columns=((String)hiddenColumns).split(",");
				for(String c:columns){
					hColumns.add(c);
				}
			 }
			 
			 String destination = null;
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			 String myFlieName="current_alarm"+sdf.format(new Date());
			 ReportExportExcel ex=null;
			 ex = new ReportExportExcel(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,myFlieName,"当前告警",hColumns);
			 destination = ex.writeExcel(list,colors,CommonDefine.EXCEL.CURRENT_ALRAM, false);
			 setFilePath(destination);
			 return RESULT_DOWNLOAD;
	}
	
	
	@IMethodLog(desc = "导出历史告警")
	@SuppressWarnings("unchecked")
	public String downloadHistoryAlarmResult(){
			// 定义一个Map接受查询返回的值
			Map<String, Object> neMap = new HashMap<String, Object>();
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			try {
				neMap = alarmManagementService.getHistoryAlarms(paramMap,0,limit);
			} catch (CommonException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			List<Map> list = (List<Map>)neMap.get("rows");
			Map<String, Object> colors=null;
			try {
				colors = alarmManagementService.getAlarmColorSet();
			} catch (CommonException e) {
				e.printStackTrace();
			}
			
			//获取隐藏的列
			Object hiddenColumns=paramMap.get("hiddenColoumms");
			List<String> hColumns=new ArrayList<String>();
			if(hiddenColumns!=null && !"".equals((String)hiddenColumns)){
				String[] columns=((String)hiddenColumns).split(",");
				for(String c:columns){
					hColumns.add(c);
				}
			}
			
			String destination = null;
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String myFlieName="history_alarm"+sdf.format(new Date());
			ReportExportExcel ex=null;
			ex = new ReportExportExcel(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,myFlieName,"历史告警",hColumns);
			//IExportExcel ex=new ExportExcelUtil(CommonDefine.PATH_ROOT+ CommonDefine.EXCEL.TEMP_DIR,myFlieName);
			destination = ex.writeExcel(list,colors,CommonDefine.EXCEL.HISTORY_ALRAM, false);
			setFilePath(destination);
			return RESULT_DOWNLOAD;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "根据ID找告警")
	public String getAlarmByIds(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> currentAlarmMap = alarmManagementService.getAlarmByIds(paramMap);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(currentAlarmMap,cfg);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	
	}
	@IMethodLog(desc = "导出告警高级查询")
	@SuppressWarnings("unchecked")
	public String downloadDetailAlarmResult() throws CommonException{
		// 定义一个Map接受查询返回的值
		Map<String, Object> alarmMap = new HashMap<String, Object>();
		// 将参数专程JSON对象
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		// 将JSON对象转成Map对象
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = (Map<String, Object>) jsonObject;
		try {
			alarmMap = alarmManagementService.getAlarms_High(paramMap, start, limit);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<DBObject> list = (List<DBObject>)alarmMap.get("rows");
		Map<String, Object> colors=alarmManagementService.getAlarmColorSet();
		 
		//获取隐藏的列
		Object hiddenColumns=paramMap.get("hiddenColoumms");
		List<String> hColumns=new ArrayList<String>();
		if(hiddenColumns!=null && !"".equals((String)hiddenColumns)){
			String[] columns=((String)hiddenColumns).split(",");
			for(String c:columns){
				hColumns.add(c);
			}
		}
		
		String destination = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		if ("current".equals(paramMap.get("type").toString())) {
			String myFlieName="current_alarm"+sdf.format(new Date());
			ReportExportExcel ex=null;
			ex = new ReportExportExcel(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,myFlieName,"当前告警",hColumns);
			destination = ex.writeExcel(list,colors,CommonDefine.EXCEL.CURRENT_ALRAM, false);			
		} else {
			String myFlieName="history_alarm"+sdf.format(new Date());
			ReportExportExcel ex=null;
			ex = new ReportExportExcel(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,myFlieName,"历史告警",hColumns);
			destination = ex.writeExcel(list,colors,CommonDefine.EXCEL.HISTORY_ALRAM, false);	
		}
		setFilePath(destination);
		return RESULT_DOWNLOAD;
	}
	/**
	 * 告警反转
	 */
	@IMethodLog(desc = "执行告警反转操作", type = IMethodLog.InfoType.MOD)
	@SuppressWarnings("unchecked")
	public String alarmReversal(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 执行告警反转操作
			alarmManagementService.alarmReversal(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("告警反转成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 取消告警反转
	 */
	@IMethodLog(desc = "执行取消告警反转操作", type = IMethodLog.InfoType.MOD)
	@SuppressWarnings("unchecked")
	public String antiAlarmReversal(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 执行取消告警反转操作
			alarmManagementService.antiAlarmReversal(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("取消告警反转成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
		@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询保护倒换信息")
	public String getProtectionSwitch(){
		try {
//			System.out.println("jsonString = "  + jsonString);
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			System.out.println("jsonObject = " + jsonObject.toString());
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
//			System.out.println("emsGroup = " + paramMap.get("emsGroup"));
//			System.out.println("ems = " + paramMap.get("ems"));
//			System.out.println("subnet = " + paramMap.get("subnet"));
//			System.out.println("ne = " + paramMap.get("ne"));
			// 定义一个Map接受查询返回的值
			Map<String, Object> protectionSwitchInfo = alarmManagementService.getProtectionSwitch(paramMap, start, limit);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			// 将返回的结果转成JSON对象，返回前台
			Map<String, Object> rltMap = new HashMap<String, Object>();
			resultObj = JSONObject.fromObject(protectionSwitchInfo, cfg);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询性能越限信息")
	public String getPmExceedData(){
		try {
//			System.out.println("jsonString = "  + jsonString);
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			System.out.println("jsonObject = " + jsonObject.toString());
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
//			System.out.println("emsGroup = " + paramMap.get("emsGroup"));
//			System.out.println("ems = " + paramMap.get("ems"));
//			System.out.println("subnet = " + paramMap.get("subnet"));
//			System.out.println("ne = " + paramMap.get("ne"));
			// 定义一个Map接受查询返回的值
			List<Map<String, Object>> protectionSwitchInfo = alarmManagementService.getPmExceedData(paramMap, start, limit);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			// 将返回的结果转成JSON对象，返回前台
			Map<String, Object> rltMap = new HashMap<String, Object>();
			rltMap.put("total", 1);
			rltMap.put("rows", protectionSwitchInfo);
			resultObj = JSONObject.fromObject(rltMap,cfg);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@SuppressWarnings("unchecked") 
	@IMethodLog(desc = "查询性能越限信息")
	public String exportPmExceedData(){
		// 将参数专程JSON对象
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		System.out.println("jsonObject = " + jsonObject.toString());
		// 将JSON对象转成Map对象
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = (Map<String, Object>) jsonObject;
		// 定义一个Map接受查询返回的值
		try {
			List<Map<String, Object>> protectionSwitchInfo = alarmManagementService.getPmExceedData(paramMap, start, limit);
			 String destination = null;
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			 String myFlieName="性能越限_"+sdf.format(new Date());
			 ReportExportExcel ex=null;
			 ex = new ReportExportExcel(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,myFlieName,"性能越限");
			 destination = ex.writeExcel(protectionSwitchInfo,CommonDefine.EXCEL.PM_EXCEED, false);
			 setFilePath(destination);

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return RESULT_DOWNLOAD;
	}
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询主告警展开收敛告警")
	public String getAlarmHavingConverge(){
		try { 
			// 定义一个Map接受查询返回的值
			Map<String, Object> dataRtn = alarmManagementService.getAlarmHavingConverge(Integer.valueOf(jsonString));
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(dataRtn);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
}


