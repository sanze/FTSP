package com.fujitsu.manager.emergencyPlanManager.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IEmergencyPlanManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

//@Results({
//	@Result(name = "onlinePreview", type="stream", params = {
//		"contentType", "application/pdf" ,
//		"inputName", "inputStream",
//		"contentDisposition", 
////		"attachment;filename=\"${fileName}\"",
//		"inline;filename=xxxxx",
//		"bufferSize", "4096"})
//})

public class EmergencyPlanAction extends AbstractAction {
	
	private String jsonString;
	private File uploadFile;
	


	@Resource
	public IEmergencyPlanManagerService emergencyPlanManagerService;
	
	/*************************** 厂家联系方式 开始  **********************************/
	/**
	 * 获取厂家联系方式列表
	 * @return
	 */
	@IMethodLog(desc = "获取厂家联系方式列表")
	public String getFactoryContactList(){
		try {
			Map<String, Object> data = emergencyPlanManagerService.getFactoryContactList(start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}

	/**
	 * 初始化厂家联系表
	 * @return
	 */
	@IMethodLog(desc = "初始化厂家联系表")
	public String initFactoryContact(){
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int factoryContactId = Integer.valueOf(data.get("FAULT_FACTORY_CONTACT_ID").toString());
			
			data = emergencyPlanManagerService.initFactoryContact(factoryContactId);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}
	
	/**
	 * 修改厂家联系表
	 * @return
	 */
	@IMethodLog(desc = "修改厂家联系表", type = IMethodLog.InfoType.MOD)
	public String modifyFactoryContact(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			emergencyPlanManagerService.modifyFactoryContact(data);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 删除厂家联系表
	 * @return
	 */
	@IMethodLog(desc = "删除厂家联系表", type = IMethodLog.InfoType.DELETE)
	public String deleteFactoryContactByIds(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			List factoryContactIds = (List)(data.get("factoryContactIds"));
			
			emergencyPlanManagerService.deleteFactoryContactByIds(factoryContactIds);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}
	/*************************** 厂家联系方式 结束  **********************************/

	
	/*************************** 应急预案 开始  **********************************/
	/**
	 * 获取应急预案列表
	 * @return
	 */
	@IMethodLog(desc = "获取应急预案列表")
	public String getEmergencyPlanList(){
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> searchCondition = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			String emergencyPlanName = null;
			String keyWord = null;
			Integer emergencyType = null;
			if (searchCondition != null) {
				// 预案名称
				emergencyPlanName = (searchCondition.get("emergencyPlanName") != null && !searchCondition
						.get("emergencyPlanName").toString().isEmpty()) ? searchCondition
						.get("emergencyPlanName").toString() : null;
				// 关键字
				keyWord = (searchCondition.get("keyWord") != null && !searchCondition
						.get("keyWord").toString().isEmpty()) ? searchCondition
						.get("keyWord").toString() : null;
				// 预案类型
				emergencyType = (searchCondition.get("emergencyType") != null && !searchCondition
						.get("emergencyType").toString().isEmpty()) ? Integer
						.valueOf(searchCondition.get("emergencyType")
								.toString()) : null;
			}
			//查询数据
			Map<String, Object> data = emergencyPlanManagerService
					.getEmergencyPlanList(emergencyPlanName, keyWord,
							emergencyType, start, limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 初始化应急预案
	 * @return
	 */
	@IMethodLog(desc = "初始化应急预案")
	public String initEmergencyPlan(){
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int emergercyId = Integer.valueOf(data.get("FAULT_EP_ID").toString());
			
			data = emergencyPlanManagerService.initEmergencyPlan(emergercyId);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}
	
	/**
	 * 修改应急预案
	 * @return
	 */
	@IMethodLog(desc = "修改应急预案", type = IMethodLog.InfoType.MOD)
	public String modifyEmergencyPlan(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			//创建人
			data.put("CREATE_USER", sysUserId);
			//预案列表
			List<Map> records = (List<Map>)data.get("records");
			//类型转换,json未解析完全，内部对象为MorphDynaBean
			for(int i = 0;i<records.size();i++){
				records.set(i, (Map)JSONObject.toBean(JSONObject.fromObject(records.get(i)),Map.class));
			}
			emergencyPlanManagerService.modifyEmergencyPlan(data,records);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 删除应急预案
	 * @return
	 */
	@IMethodLog(desc = "删除应急预案", type = IMethodLog.InfoType.DELETE)
	public String deleteEmergencyPlanByIds(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			List emergercyIds = (List)(data.get("emergercyIds"));
			
			emergencyPlanManagerService.deleteEmergencyPlanByIds(emergercyIds);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}
	/*************************** 应急预案 结束  **********************************/
	
	/*************************** 演习列表 开始  **********************************/
	/**
	 * 获取演习列表
	 * @return
	 */
	@IMethodLog(desc = "获取演习列表")
	public String getExerciseList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> searchCondition = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int emergercyId = Integer.valueOf(searchCondition.get("FAULT_EP_ID").toString());
			//查询数据
			Map<String, Object> data = emergencyPlanManagerService
					.getExerciseList(emergercyId,start, limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 初始化演习信息
	 * @return
	 */
	@IMethodLog(desc = "初始化演习信息")
	public String initExercise(){
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int emergercyId = Integer.valueOf(data.get("FAULT_EP_EXERCISE_ID").toString());
			
			data = emergencyPlanManagerService.initExercise(emergercyId);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}
	
	/**
	 * 修改演习信息
	 * @return
	 */
	@IMethodLog(desc = "修改演习信息", type = IMethodLog.InfoType.MOD)
	public String modifyExercise(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);

			emergencyPlanManagerService.modifyExercise(data);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 删除演习信息
	 * @return
	 */
	@IMethodLog(desc = "删除演习信息", type = IMethodLog.InfoType.DELETE)
	public String deleteExerciseByIds(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			List ids = (List)(data.get("ids"));
			
			emergencyPlanManagerService.deleteExerciseByIds(ids);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}
	/*************************** 演习列表 结束  **********************************/
	
	/*************************** 演习步骤列表 开始  **********************************/
	/**
	 * 获取演习步骤列表
	 * @return
	 */
	@IMethodLog(desc = "获取演习步骤列表")
	public String getExerciseDetailList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> searchCondition = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int id = Integer.valueOf(searchCondition.get("FAULT_EP_EXERCISE_ID").toString());
			//查询数据
			Map<String, Object> data = emergencyPlanManagerService
					.getExerciseDetailList(id,-1, -1);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}


	/**
	 * 修改演习步骤信息
	 * @return
	 */
	@IMethodLog(desc = "修改演习步骤信息", type = IMethodLog.InfoType.MOD)
	public String modifyExerciseDetail(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int exerciseId = Integer.valueOf(data.get("FAULT_EP_EXERCISE_ID").toString());
			
			List records = (List)data.get("records");
			//由于map未完全解析，需要重新解析一遍
			List<Map> datas = new ArrayList<Map>();
			
			for(Object obj:records){
				Map xxx = (Map)JSONObject.toBean(JSONObject.fromObject(obj),Map.class);
				datas.add(xxx);
			}

			emergencyPlanManagerService.modifyExerciseDetail(exerciseId,datas);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 删除演习步骤信息
	 * @return
	 */
	@IMethodLog(desc = "删除演习步骤信息", type = IMethodLog.InfoType.DELETE)
	public String deleteExerciseDetailByIds(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			List ids = (List)(data.get("ids"));
			
			emergencyPlanManagerService.deleteExerciseDetailByIds(ids);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;		
	}
	/*************************** 演习步骤列表 结束  **********************************/
	
	/*************************** 预案列表 开始  **********************************/
	/**
	 * 获取预案列表
	 * @return
	 */
	@IMethodLog(desc = "获取预案列表")
	public String getEmergencyPlanContentList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> searchCondition = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int id = Integer.valueOf(searchCondition.get("FAULT_EP_ID").toString());
			//查询数据
			Map<String, Object> data = emergencyPlanManagerService
					.getEmergencyPlanContentList(id,-1, -1);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 将文件上传至服务器
	 * 
	 * @return
	 */
	public String importFile() {
		try {
			Map map = emergencyPlanManagerService.importFile(uploadFile, jsonString, CommonDefine.PATH_ROOT+ CommonDefine.EXCEL.UPLOAD_PATH_EP_CONTENT);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	}
	
	
	/**
	 * 导出演习详情
	 * 
	 * @return
	 */
	public String exportExercise() {
		Map result = new HashMap();
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> data = (Map)JSONObject.toBean(jsonObject,Map.class);
			
			int exerciseId = Integer.valueOf(data.get("FAULT_EP_EXERCISE_ID").toString());

			String fileName = emergencyPlanManagerService.exportExercise(exerciseId);
			
			result.put("fileName", fileName);
			result.put("returnResult", CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.put("returnResult", CommonDefine.FAILED);
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}


	/*************************** 预案列表 结束  **********************************/
	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

}


