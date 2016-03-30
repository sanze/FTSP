package com.fujitsu.manager.performanceManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;

public class PmSearchAction extends DownloadAction {
	private List<String> modifyList;
	private List<String> stringList;
	private List<String> rateList;
	private Map<String, String> searchCond;
	private List<Long> condList;
	private Integer userId;

	@Resource
	public IPerformanceManagerService performanceManagerService;

	@IMethodLog(desc = "当前性能查询:SDH当前性能查询")
	public String searchCurrentSdhPmDate() {
		try {
			if (userId != null && userId != 0) {
				List<Map> nodeList = ListStringtoListMap(this.modifyList);
				Map result = performanceManagerService.getCurrentPmData(
						Boolean.parseBoolean(searchCond.get("maxMin")), true,
						nodeList,
						Integer.parseInt(searchCond.get("granularity")),
						rateList, stringList, userId,CommonDefine.PM_SEARCH_TYPE.PM_SEARCH);
				result.put("returnResult", CommonDefine.SUCCESS);
				resultObj = JSONObject.fromObject(result);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "当前性能查询:分页获取SDH当前性能")
	public String getCurrentSdhPmDate() {
		try {
			if (userId != null && userId != 0) {
				Map pmResult;
				Integer searchTag = null;
				if (searchCond.get("searchTag") != null) {
					searchTag = Integer.parseInt(searchCond.get("searchTag"));
				}

				pmResult = performanceManagerService.getTempPmDataByPage(
						CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA,
						Integer.parseInt(searchCond.get("exception")), userId,
						searchTag, start, limit);
				resultObj = JSONObject.fromObject(pmResult);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "当前性能查询:WDM当前性能查询")
	public String searchCurrentWDMPmDate() {
		try {
			if (userId != null && userId != 0) {
				List<Map> nodeList = ListStringtoListMap(this.modifyList);
				Map result = performanceManagerService.getCurrentPmData(
						Boolean.parseBoolean(searchCond.get("maxMin")), false,
						nodeList,
						Integer.parseInt(searchCond.get("granularity")),
						rateList, stringList, userId, CommonDefine.PM_SEARCH_TYPE.PM_SEARCH);
				result.put("returnResult", CommonDefine.SUCCESS);
				resultObj = JSONObject.fromObject(result);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "当前性能查询:分页获取WDM当前性能")
	public String getCurrentWdmPmDate() {
		try {
			if (userId != null && userId != 0) {
				Map pmResult;
				pmResult = performanceManagerService.getTempPmDataByPage(
						CommonDefine.PM.PM_TABLE_NAMES.CURRENT_WDM_DATA,
						Integer.parseInt(searchCond.get("exception")), userId,
						Integer.parseInt(searchCond.get("searchTag")), start,
						limit);
				resultObj = JSONObject.fromObject(pmResult);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "当前性能查询:获取模板信息")
	public String getCurrentPmTempleteInfo() {
		try {
			Map resultMap = performanceManagerService.getCurrentPmTempleteInfo(
					Integer.parseInt(searchCond.get("templateId")),
					searchCond.get("pmStdIndex"),
					Integer.parseInt(searchCond.get("domain")));
			resultObj = JSONObject.fromObject(resultMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "历史性能查询:SDH历史性能查询")
	public String searchHistorySdhPmDate() {
		try {
			if (userId != null && userId != 0) {
				List<Map> nodeList = ListStringtoListMap(this.modifyList);
				int searchTag = performanceManagerService
						.getHistoryPmData(
								Boolean.parseBoolean(searchCond.get("maxMin")),
								true, nodeList, searchCond.get("startTime"),
								searchCond.get("endTime"), rateList,
								stringList, userId,CommonDefine.PM_SEARCH_TYPE.PM_SEARCH);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(String.valueOf(searchTag));
				resultObj = JSONObject.fromObject(result);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "历史性能查询:分页获取SDH历史性能")
	public String getHistorySdhPmDate() {
		try {
			if (userId != null && userId != 0) {
				Map pmResult;
				pmResult = performanceManagerService.getTempPmDataByPage(
						CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA,
						Integer.parseInt(searchCond.get("exception")), userId,
						Integer.parseInt(searchCond.get("searchTag")), start,
						limit);
				resultObj = JSONObject.fromObject(pmResult);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "历史性能查询:WDM历史性能查询")
	public String searchHistoryWdmPmDate() {
		try {
			if (userId != null && userId != 0) {
				List<Map> nodeList = ListStringtoListMap(this.modifyList);
				int searchTag = performanceManagerService
						.getHistoryPmData(
								Boolean.parseBoolean(searchCond.get("maxMin")),
								false, nodeList, searchCond.get("startTime"),
								searchCond.get("endTime"), rateList,
								stringList, userId,CommonDefine.PM_SEARCH_TYPE.PM_SEARCH);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(String.valueOf(searchTag));
				resultObj = JSONObject.fromObject(result);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "历史性能查询:分页获取WDM历史性能")
	public String getHistoryWdmPmDate() {
		try {
			if (userId != null && userId != 0) {
				Map pmResult;
				pmResult = performanceManagerService.getTempPmDataByPage(
						CommonDefine.PM.PM_TABLE_NAMES.HISTORY_WDM_DATA,
						Integer.parseInt(searchCond.get("exception")), userId,
						Integer.parseInt(searchCond.get("searchTag")), start,
						limit);
				resultObj = JSONObject.fromObject(pmResult);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能查询:将选中性能值设为基准值")
	public String setCompareValueFromPm() {
		try {
			List<Map> pmList = ListStringtoListMap(this.modifyList);
			performanceManagerService.setCompareValueFromPm(pmList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_SET_COMPARE_VALUE_FROM_PM_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能查询:导出查询结果")
	public String downloadPmResult() {
		int searchTag = Integer.parseInt(searchCond.get("searchTag"));
		int exceptionLevel = Integer.parseInt(searchCond.get("exception"));
		int tableTag = Integer.parseInt(searchCond.get("tempTableName"));
		String tableName = null;
		switch (tableTag) {
		case 1:
			tableName = CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA;
			break;
		case 2:
			tableName = CommonDefine.PM.PM_TABLE_NAMES.CURRENT_WDM_DATA;
			break;
		case 3:
			tableName = CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA;
			break;
		case 4:
			tableName = CommonDefine.PM.PM_TABLE_NAMES.HISTORY_WDM_DATA;
			break;
		}

		String destination;
		destination = performanceManagerService.getPmExportedExcelPath(
				tableName, exceptionLevel, userId, searchTag);
		setFilePath(destination);

		return RESULT_DOWNLOAD;
	}

	public String getIndexPagePmInfo() {
		try {
			Map indexPmInfoMap = performanceManagerService
					.getIndexPagePmInfo(-1);
			indexPmInfoMap.put("returnResult", CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(indexPmInfoMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	// ***********************************咯咯咯咯咯咯***************************************
	@IMethodLog(desc = "趋势图:趋势图本端")
	public String generateDiagramNend() {
		String returnString = RESULT_OBJ;
		try {
			String xmlString = performanceManagerService
					.generateDiagramNend(searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(xmlString);
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

	@IMethodLog(desc = "趋势图:趋势图远端")
	public String generateDiagramFend() {
		String returnString = RESULT_OBJ;
		try {
			String xmlString = performanceManagerService
					.generateDiagramFend(searchCond);
			if (xmlString != null) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(xmlString);
			} else {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.PM_NO_Z_END_PTP));
			}
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

	@IMethodLog(desc = "趋势图:获取性能分组")
	public String getPmStdIndexType() {
		String returnString = RESULT_OBJ;
		try {
			String pmType = performanceManagerService
					.getPmStdIndexType(searchCond);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(pmType);
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
	// **************************************************************************************
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

	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<String> getRateList() {
		return rateList;
	}

	public void setRateList(List<String> rateList) {
		this.rateList = rateList;
	}

}
