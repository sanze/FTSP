package com.fujitsu.manager.performanceManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IMultipleSectionManagerService;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;

public class MsCutoverAction extends DownloadAction{
	private Map<String, String> searchCond;
	private List<String> modifyList;
	private List<Long> condList;
	private int[] taskTypes;

	@Resource
	public IPerformanceManagerService performanceManagerService;
	@Resource
	public IMultipleSectionManagerService pmMultipleSectionManagerService;
	
	@IMethodLog(desc = "WDM复用段割接：任务名判重")
	public String checkTaskNameDuplicate() {
		try {
			Long r = performanceManagerService.checkTaskNameDuplicate(
					searchCond, null, taskTypes);
			if (r == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
//				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			} else {
				result.setReturnMessage(getText(MessageCodeDefine.PM_REPORT_TASK_NAME_DUPLICATE));
				result.setReturnResult(CommonDefine.FAILED);
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}


	@IMethodLog(desc = "WDM复用段割接：保存复用段割接任务", type = IMethodLog.InfoType.MOD)
	public String saveMSCutoverTask() {
		try {
			performanceManagerService.saveMSCutoverTask(condList, searchCond, sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_WDMMS_CUTOVER_TASK_SAVE_SUCCESS));//TODO
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.PM_WDMMS_CUTOVER_TASK_SAVE_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "WDM复用段割接：查询WDM复用段割接")
	public String searchCutoverTask() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchCutoverTask(searchCond, sysUserId,limit,start);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "WDM复用段割接：加载任务名的下拉框")
	public String loadTaskNameCombo() {
		try {
			Map<String, Object> data = performanceManagerService
					.loadTaskNameCombo(searchCond, sysUserId);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "WDM复用段割接：获取任务中包含的复用段")
	public String getMSById() {
		try {
			Map<String, Object> data = performanceManagerService
					.getMSById(searchCond);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "WDM复用段割接：修改复用段割接任务", type = IMethodLog.InfoType.MOD)
	public String updateMSTask() {
		try {
			performanceManagerService.updateMSTask(searchCond,condList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_WDMMS_CUTOVER_TASK_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.PM_WDMMS_CUTOVER_TASK_UPDATE_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "WDM复用段割接：删除复用段割接任务", type = IMethodLog.InfoType.DELETE)
	public String deleteCutoverTask() {
		try {
			performanceManagerService.deleteCutoverTask(condList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_WDMMS_CUTOVER_TASK_DELETE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.PM_WDMMS_CUTOVER_TASK_DELETE_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "WDM复用段割接：查找任务复用段信息")
	public String searchMultiplexSection() {
		try {
			Map<String, Object> data = performanceManagerService.searchMultiplexSection(searchCond);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
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

	public int[] getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(int[] taskTypes) {
		this.taskTypes = taskTypes;
	}

	public IPerformanceManagerService getPerformanceManagerService() {
		return performanceManagerService;
	}

	public void setPerformanceManagerService(
			IPerformanceManagerService performanceManagerService) {
		this.performanceManagerService = performanceManagerService;
	}

	public IMultipleSectionManagerService getPmMultipleSectionManagerService() {
		return pmMultipleSectionManagerService;
	}

	public void setPmMultipleSectionManagerService(
			IMultipleSectionManagerService pmMultipleSectionManagerService) {
		this.pmMultipleSectionManagerService = pmMultipleSectionManagerService;
	}
	
}
