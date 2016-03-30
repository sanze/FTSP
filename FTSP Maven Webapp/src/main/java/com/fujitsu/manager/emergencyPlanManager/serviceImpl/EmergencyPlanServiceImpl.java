package com.fujitsu.manager.emergencyPlanManager.serviceImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.EmergencyPlanManagerMapper;
import com.fujitsu.manager.emergencyPlanManager.service.EmergencyPlanService;
import com.fujitsu.util.CommonUtil;
@Service
public class EmergencyPlanServiceImpl extends EmergencyPlanService {
	
	@Resource
	private ICommonManagerService commonManagerService;
	@Resource
	private EmergencyPlanManagerMapper emergencyPlanManagerMapper;
	@Resource
	private CommonManagerMapper commonManagerMapper;
	
	/**
	 * 获取厂家联系方式列表
	 * @return
	 */
	@IMethodLog(desc = " 获取厂家联系方式列表")
	@Override
	public Map<String, Object> getFactoryContactList(int start, int limit) throws CommonException {

		Map returnMap = new HashMap();
		List<Map> data = new ArrayList<Map>();
		data = commonManagerMapper.selectTable("T_FAULT_FACTORY_CONTACT",start,limit);
		
		int total = commonManagerMapper.selectTableCount("T_FAULT_FACTORY_CONTACT");

		returnMap.put("rows", data);
		returnMap.put("total", total);
		return returnMap;
	}
	
	/**
	 * 初始化厂家联系数据
	 * @return
	 */
	@IMethodLog(desc = " 初始化厂家联系数据")
	@Override
	public Map initFactoryContact(int factoryContactId) throws CommonException {
		Map returnMap = new HashMap();
		returnMap = commonManagerMapper.selectTableById(
				"T_FAULT_FACTORY_CONTACT", "FAULT_FACTORY_CONTACT_ID",
				factoryContactId);
		return returnMap;
	}
	
	/**
	 * 新增/更新厂家联系数据
	 * @return
	 */
	@IMethodLog(desc = " 新增/更新厂家联系数据")
	@Override
	public void modifyFactoryContact(Map data) throws CommonException {
		//修改类型
		int editType = Integer.valueOf(data.get("editType").toString());
		
		switch(editType){
		//新增
		case 0:
			data.put("FAULT_FACTORY_CONTACT_ID", null);
			data.put("CREATE_TIME", new Date());
			emergencyPlanManagerMapper.insertFactoryContact(data);
			break;
		//修改
		case 1:
			emergencyPlanManagerMapper.updateFactoryContactById(data);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 删除厂家联系数据
	 * @return
	 */
	@IMethodLog(desc = " 删除厂家联系数据")
	@Override
	public void deleteFactoryContactByIds(List<Integer> factoryContactIds) throws CommonException {
		emergencyPlanManagerMapper.deleteFactoryContactByIds(factoryContactIds);
	}
	
	
	/**
	 *  获取应急预案列表
	 * @return
	 */
	@IMethodLog(desc = "  获取应急预案列表")
	@Override
	public Map<String, Object> getEmergencyPlanList(String emergencyPlanName,
			String keyWord, Integer emergencyType, int start, int limit) throws CommonException {

		Map returnMap = new HashMap();
		List<Map> data = new ArrayList<Map>();
		//查询数据
		data = emergencyPlanManagerMapper.getEmergencyPlanList(
				emergencyPlanName, keyWord, emergencyType, start, limit);
		
		int total = emergencyPlanManagerMapper.getEmergencyPlanListCount(
				emergencyPlanName, keyWord, emergencyType);

		returnMap.put("rows", data);
		returnMap.put("total", total);
		return returnMap;
	}
	
	/**
	 * 初始化应急预案数据
	 * @return
	 */
	@IMethodLog(desc = " 初始化应急预案数据")
	@Override
	public Map initEmergencyPlan(int emergercyId) throws CommonException {
		Map returnMap = new HashMap();
		returnMap = commonManagerMapper.selectTableById(
				"T_FAULT_EP", "FAULT_EP_ID",
				emergercyId);
		return returnMap;
	}
	
	/**
	 * 新增/更新应急预案数据
	 * @return
	 */
	@IMethodLog(desc = " 新增/更新应急预案数据")
	@Override
	public void modifyEmergencyPlan(Map data,List<Map> contents) throws CommonException {
		//修改类型
		int editType = Integer.valueOf(data.get("editType").toString());
		
		Integer epId = null;
		
		List<Map> records = new ArrayList<Map>();
		
		switch(editType){
		//新增
		case 0:
			data.put("FAULT_EP_ID", null);
			data.put("CREATE_TIME", new Date());
			emergencyPlanManagerMapper.insertEmergencyPlan(data);
			epId = Integer.valueOf(data.get("FAULT_EP_ID").toString());
//			//删除预案列表
//			emergencyPlanManagerMapper.deleteEmergencyPlanContentById(epId);
			//插入预案数据
			for(Map xxx:contents){
				xxx.put("FAULT_EP_CONTENT_ID", null);
				xxx.put("FAULT_EP_ID", epId);
				xxx.put("CREATE_TIME", new Date());
				records.add(xxx);
			}
			if(records.size()>0){
				emergencyPlanManagerMapper.insertEmergencyPlanContentBatch(records);
			}
			
			break;
		//修改
		case 1:
			//更新数据
			emergencyPlanManagerMapper.updateEmergencyPlanById(data);
			
			epId = Integer.valueOf(data.get("FAULT_EP_ID").toString());
			//删除预案列表
			emergencyPlanManagerMapper.deleteEmergencyPlanContentById(epId);
			//插入预案数据
			//由于map未完全解析，需要重新解析一遍
			for(Map xxx:contents){
				xxx.put("FAULT_EP_CONTENT_ID", null);
				xxx.put("FAULT_EP_ID", epId);
				xxx.put("CREATE_TIME", new Date());
				records.add(xxx);
			}
			if(records.size()>0){
				emergencyPlanManagerMapper.insertEmergencyPlanContentBatch(records);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 删除应急预案数据
	 * @return
	 */
	@IMethodLog(desc = " 删除应急预案数据")
	@Override
	public void deleteEmergencyPlanByIds(List<Integer> emergercyIds) throws CommonException {
		emergencyPlanManagerMapper.deleteEmergencyPlanByIds(emergercyIds);
	}
	
	
	/**
	 * 获取演习列表
	 * @return
	 */
	@IMethodLog(desc = " 获取演习列表")
	@Override
	public Map<String, Object> getExerciseList(int emergercyId, int start, int limit) throws CommonException {

		Map returnMap = new HashMap();
		List<Map> data = new ArrayList<Map>();
		data = commonManagerMapper.selectTableListById("T_FAULT_EP_EXERCISE", "FAULT_EP_ID", emergercyId,start,limit);
		
		int total = commonManagerMapper.selectTableListCountById("T_FAULT_EP_EXERCISE", "FAULT_EP_ID", emergercyId);

		returnMap.put("rows", data);
		returnMap.put("total", total);
		return returnMap;
	}
	
	/**
	 * 初始化演习数据
	 * @return
	 */
	@IMethodLog(desc = " 初始化演习数据")
	@Override
	public Map initExercise(int exerciseId) throws CommonException {
		Map returnMap = new HashMap();
		returnMap = commonManagerMapper.selectTableById(
				"T_FAULT_EP_EXERCISE", "FAULT_EP_EXERCISE_ID",
				exerciseId);
		return returnMap;
	}
	
	/**
	 * 新增/更新演习数据
	 * @return
	 */
	@IMethodLog(desc = " 新增/更新演习数据")
	@Override
	public void modifyExercise(Map data) throws CommonException {
		//修改类型
		int editType = Integer.valueOf(data.get("editType").toString());
		
		switch(editType){
		//新增
		case 0:
			data.put("FAULT_EP_EXERCISE_ID", null);
			data.put("CREATE_TIME", new Date());
			emergencyPlanManagerMapper.insertExercise(data);
			break;
		//修改
		case 1:
			emergencyPlanManagerMapper.updateExerciseById(data);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 删除演习数据
	 * @return
	 */
	@IMethodLog(desc = " 删除演习数据")
	@Override
	public void deleteExerciseByIds(List<Integer> exerciseIds) throws CommonException {
		emergencyPlanManagerMapper.deleteExerciseByIds(exerciseIds);
	}
	
	/**
	 * 获取演习步骤列表
	 * @return
	 */
	@IMethodLog(desc = " 获取演习步骤列表")
	@Override
	public Map<String, Object> getExerciseDetailList(int exerciseId, int start, int limit) throws CommonException {

		Map returnMap = new HashMap();
		List<Map> data = new ArrayList<Map>();
		data = commonManagerMapper.selectTableListById("T_FAULT_EP_EXERCISE_DETAIL", "FAULT_EP_EXERCISE_ID", exerciseId,start,limit);
		
		int total = commonManagerMapper.selectTableListCountById("T_FAULT_EP_EXERCISE_DETAIL", "FAULT_EP_EXERCISE_ID", exerciseId);

		returnMap.put("rows", data);
		returnMap.put("total", total);
		return returnMap;
	}
	
	/**
	 * 新增/更新演习步骤数据
	 * @return
	 */
	@IMethodLog(desc = " 新增/更新演习步骤数据")
	@Override
	public void modifyExerciseDetail(int exerciseId, List<Map> data) throws CommonException {
		//删除原有级联
		emergencyPlanManagerMapper.deleteExerciseDetailById(exerciseId);
		
		for(Map map:data){
			map.put("FAULT_EP_EXERCISE_DETAIL_ID", null);
			map.put("FAULT_EP_EXERCISE_ID", exerciseId);
			map.put("CREATE_TIME", new Date());
		}
		//插入新增记录
		emergencyPlanManagerMapper.insertExerciseDetailBatch(data);
	}
	
	/**
	 * 删除演习数据
	 * @return
	 */
	@IMethodLog(desc = " 删除演习数据")
	@Override
	public void deleteExerciseDetailByIds(List<Integer> exerciseIds) throws CommonException {
		emergencyPlanManagerMapper.deleteExerciseDetailByIds(exerciseIds);
	}
	
	/**
	 * 获取预案列表
	 * @return
	 */
	@IMethodLog(desc = " 获取预案列表")
	@Override
	public Map<String, Object> getEmergencyPlanContentList(int emergercyId, int start, int limit) throws CommonException {

		Map returnMap = new HashMap();
		List<Map> data = new ArrayList<Map>();
		data = commonManagerMapper.selectTableListById("T_FAULT_EP_CONTENT", "FAULT_EP_ID", emergercyId,start,limit);
		
		int total = commonManagerMapper.selectTableListCountById("T_FAULT_EP_CONTENT", "FAULT_EP_ID", emergercyId);

		returnMap.put("rows", data);
		returnMap.put("total", total);
		return returnMap;
	}
	
	/**
	 * 上传预案文件，并返回路径
	 * @return
	 */
	@IMethodLog(desc = " 上传预案文件")
	@Override
	public Map<String, String> importFile(File file, String fileName,
			String uploadPath) throws CommonException {
		
		Map<String, String> map = new HashMap();
		Boolean isUpload = new Boolean(false);
		// 将本地的xls文件上传至服务器指定位置
		try {
			isUpload = commonManagerService.uploadFile(file, fileName,
					uploadPath);

		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (CommonException e) {
			throw e;
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		}
		//设置路径
		uploadPath = uploadPath +"\\"+fileName;
		//返回文件名，返回文件路径
		map.put("DISPALY_NAME", fileName);
		map.put("FILE_PATH", uploadPath);
		return map;
	}
	
	/**
	 * 导出演习详情
	 * @return
	 */
	@IMethodLog(desc = "导出演习详情")
	@Override
	public String exportExercise(int exerciseId) throws CommonException {
		String fileName = null;
		try{
			//演习数据
			Map exercise = commonManagerMapper.selectTableById(
					"T_FAULT_EP_EXERCISE", "FAULT_EP_EXERCISE_ID",
					exerciseId);
			//预案Id
			Integer emergercyId = Integer.valueOf(exercise.get("FAULT_EP_ID").toString());
			//应急预案数据
			Map emergencyPlan = commonManagerMapper.selectTableById(
					"T_FAULT_EP", "FAULT_EP_ID",
					emergercyId);
			//演习列表数据
			List<Map> exerciseDetailList = commonManagerMapper.selectTableListById("T_FAULT_EP_EXERCISE_DETAIL", "FAULT_EP_EXERCISE_ID", exerciseId, 0, 0);
			
			Map data  = new HashMap();
			
			String sheetName = emergencyPlan.get("DISPALY_NAME")+"预案"+exercise.get("DISPALY_NAME")+"演习详情";
			//sheet名称
			data.put("sheetName", sheetName);
			//预案名称
			data.put("EP_DISPALY_NAME", emergencyPlan.get("DISPALY_NAME"));
			//预案类型
			String epTypeDisplay = "其他预案";
			switch(Integer.valueOf(emergencyPlan.get("EP_TYPE").toString())){
			case 1:
				epTypeDisplay = "设备预案";
				break;
			case 2:
				epTypeDisplay = "网管预案";
				break;
			case 3:
				epTypeDisplay = "电路预案";
				break;
			case 4:
				epTypeDisplay = "传输系统预案";
				break;
			case 5:
				epTypeDisplay = "机房环境预案";
				break;
			case 6:
				epTypeDisplay = "后勤保障预案";
				break;
			case 99:
				epTypeDisplay = "其他预案";
				break;
			}
			data.put("EP_TYPE", epTypeDisplay);
			//演习名称
			data.put("EXERCISE_DISPALY_NAME", exercise.get("DISPALY_NAME"));
			//演习开始时间
			data.put("START_TIME", exercise.get("START_TIME"));
			//演习结束时间
			data.put("END_TIME", exercise.get("END_TIME"));
			//演习参与人员
			data.put("PARTICIPANTS", exercise.get("PARTICIPANTS"));
			//演习结果
			String resultDisplay = "";
			if(exercise.get("RESULT") != null){
				switch(Integer.valueOf(exercise.get("RESULT").toString())){
				case 0:
					resultDisplay = "失败";
					break;
				case 1:
					resultDisplay = "成功";
					break;
				}
			}
			data.put("RESULT", resultDisplay);
			//演习评估
			data.put("ASSESSMENT", exercise.get("ASSESSMENT"));
			//演习步骤
			data.put("exerciseDetailList", exerciseDetailList);
			
			SimpleDateFormat sf = CommonUtil
					.getDateFormatter((CommonDefine.RETRIEVAL_TIME_FORMAT));
			
			fileName = System.getProperty("java.io.tmpdir")+"/"+sf.format(new Date())+"_"+sheetName+".xlsx";
			
			EmergencyExcelUtil.getInstance(fileName).writeData(data);

		}catch(Exception e){
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		}
		return fileName;
	}
	
}
