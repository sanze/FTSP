package com.fujitsu.manager.performanceManager.action;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

public class OpticalUnitConfigAction extends AbstractAction {
	private Map<String, String> searchCond;
	private List<String> modifyList;
	private List<Long> condList;
	private String processBarKey;

	@Resource
	public IPerformanceManagerService performanceManagerService;

	// ***********************************咯咯咯咯咯咯***************************************
	@IMethodLog(desc = "光口设置管理:获取光口标准列表", type = IMethodLog.InfoType.MOD)
	public String getOptStdComboValue() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService
					.getOptStdComboValue(searchCond);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:获取光模块列表", type = IMethodLog.InfoType.MOD)
	public String getOptModelComboValue() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService
					.getOptModelComboValue(searchCond);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:查询ptp列表以及光口标准应用情况", type = IMethodLog.InfoType.MOD)
	public String searchPtpOptModelInfo() {
		String returnString = RESULT_OBJ;
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			if(searchCond==null)
				searchCond = new HashMap<String, String>();
			searchCond.put("start", String.valueOf(start));
			searchCond.put("limit", String.valueOf(limit));
			Map result = performanceManagerService.searchPtpOptModelInfo(
					nodeList, condList, searchCond);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:保存修改的光口标准信息", type = IMethodLog.InfoType.MOD)
	public String savePtpOptStdApplication() {
		String returnString = RESULT_OBJ;
		List<Map> ptpOptStdList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.savePtpOptStdApplication(ptpOptStdList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:查询光口标准详细", type = IMethodLog.InfoType.MOD)
	public String searchOptStdDetail() {
		String returnString = RESULT_OBJ;
		try {
			if(searchCond==null)
				searchCond = new HashMap<String, String>();
			searchCond.put("start", String.valueOf(start));
			searchCond.put("limit", String.valueOf(limit));
			
			Map result = performanceManagerService
					.searchOptStdDetail(searchCond);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:获取选择节点上的光模块信息", type = IMethodLog.InfoType.MOD)
	public String getOptModelFromNodes() {
		String returnString = RESULT_OBJ;
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			Map result = performanceManagerService.getOptModelFromNodes(
					nodeList, searchCond, condList);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:修改光口标准内容", type = IMethodLog.InfoType.MOD)
	public String saveOptStdDetail() {
		String returnString = RESULT_OBJ;
		List<Map> optStdDetailList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.saveOptStdDetail(optStdDetailList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_MODIFY_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_MODIFY_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:新增光口标准", type = IMethodLog.InfoType.MOD)
	public String saveNewOptStd() {
		String returnString = RESULT_OBJ;
		try {
			performanceManagerService.saveNewOptStd(searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_NEW_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_NEW_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:删除光口标准信息", type = IMethodLog.InfoType.MOD)
	public String deleteOptStd() {
		String returnString = RESULT_OBJ;
		try {
			performanceManagerService.deleteOptStd(condList, searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_DEL_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:获取光口标准值", type = IMethodLog.InfoType.MOD)
	public String getOptStdInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService.getOptStdInfo(searchCond);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}

	@IMethodLog(desc = "光口设置管理:批量应用光口标准信息", type = IMethodLog.InfoType.MOD)
	public String applyPtpOptStdBatch() {
		String returnString = RESULT_OBJ;
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.applyPtpOptStdBatch(nodeList, condList, searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}
	
	@IMethodLog(desc = "光口设置管理:自动应用光口标准信息", type = IMethodLog.InfoType.MOD)
	public String autoApplyPtpOptStd() {
		String returnString = RESULT_OBJ;
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			String returnResult = performanceManagerService.autoApplyPtpOptStd(nodeList, condList,processBarKey);
			result.setReturnResult(CommonDefine.SUCCESS);
			if(returnResult.isEmpty())
				result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_SAVE_SUCCESS));
			else
				result.setReturnMessage(getText(MessageCodeDefine.PM_AUTO_APPLY_OPT_STD_CANCELED));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return returnString;
	}
	
	@IMethodLog(desc = "光口设置管理:检测新的标准名是否存在", type = IMethodLog.InfoType.MOD)
	public String checkOptStdName() {
		String returnString = RESULT_OBJ;
		try {
			boolean isDuplicate = performanceManagerService.checkOptStdName(searchCond);
			if(isDuplicate)
				result.setReturnResult(CommonDefine.FAILED);
			else
				result.setReturnResult(CommonDefine.SUCCESS);
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
	
	@IMethodLog(desc = "光口设置管理:检测标准是否已应用", type = IMethodLog.InfoType.MOD)
	public String checkIfStdApplied() {
		String returnString = RESULT_OBJ;
		try {
			int returnType = performanceManagerService.checkIfStdApplied(searchCond);
			if(returnType==3){
				result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_DEL_DULP));
				result.setReturnResult(returnType);
			}else
			if(returnType==2){
				result.setReturnMessage(getText(MessageCodeDefine.PM_OPTICAL_STD_DEL_DEFAULT));
				result.setReturnResult(returnType);
			}
			else
				result.setReturnResult(CommonDefine.SUCCESS);
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
	// **************************************************************************
	public Map<String, String> getSearchCond() {
		return searchCond;
	}

	public void setSearchCond(Map<String, String> searchCond) {
		this.searchCond = searchCond;
	}

	public List<String> getModifyList() {
		return modifyList;
	}

	public void setModifyList(List<String> modifyList) {
		this.modifyList = modifyList;
	}

	public List<Long> getCondList() {
		return condList;
	}

	public void setCondList(List<Long> condList) {
		this.condList = condList;
	}

	public IPerformanceManagerService getPerformanceManagerService() {
		return performanceManagerService;
	}

	public void setPerformanceManagerService(
			IPerformanceManagerService performanceManagerService) {
		this.performanceManagerService = performanceManagerService;
	}

	public String getProcessBarKey() {
		return processBarKey;
	}

	public void setProcessBarKey(String processBarKey) {
		this.processBarKey = processBarKey;
	}
}
