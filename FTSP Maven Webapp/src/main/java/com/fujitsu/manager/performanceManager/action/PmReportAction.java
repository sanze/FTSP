package com.fujitsu.manager.performanceManager.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IMultipleSectionManagerService;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.ExportResult;
import com.fujitsu.common.MessageCodeDefine;

public class PmReportAction extends DownloadAction {
	private Map<String, String> searchCond;
	private List<String> modifyList;
	private List<Long> condList;
	private String reportSearchJsonString;
	private List<String> filePathList;
	private List<Integer> pmReportIdList;

	@Resource
	public IPerformanceManagerService performanceManagerService;
	@Resource
	public IMultipleSectionManagerService pmMultipleSectionManagerService;

	@IMethodLog(desc = "性能报表定制：获取操作权限组列表")
	public String getPrivilegeGroupList() {
		try {
			Map<String, Object> data = performanceManagerService
					.getPrivilegeList();
			List<Integer> initValue = performanceManagerService
					.myUserGroup(sysUserId);
			data.put("initValue", initValue);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：添加网元时补完信息")
	public String getNodeInfo() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			Map<String, Object> data = performanceManagerService
					.getNodeInfo(nodeList);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：保存报表任务（网元）", type = IMethodLog.InfoType.MOD)
	public String saveNeReportTask() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			int resultCode = performanceManagerService.saveNeReportTask(
					nodeList, searchCond, sysUserId);
			if (resultCode == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_OUT_OF_LIMIT));
			}
			if (resultCode == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询报表任务")
	public String searchReportTask() {
		try {
			if (searchCond == null) {
				searchCond = new HashMap<String, String>();
			}
			searchCond.put("start", start + "");
			searchCond.put("limit", limit + "");
			Map<String, Object> data = performanceManagerService
					.searchReportTask(searchCond, sysUserId);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询创建人下拉框数据")
	public String getCreatorComboValue() {
		try {
			Map<String, Object> data = performanceManagerService
					.getCreatorComboValue(sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询创建人下拉框数据(privilege)")
	public String getCreatorComboValuePrivilege() {
		try {
			Map<String, Object> data = performanceManagerService
					.getCreatorComboValuePrivilege(sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询任务名下拉框数据")
	public String getTaskNameComboValue() {
		try {
			Map<String, Object> data = performanceManagerService
					.getTaskNameComboValue(searchCond);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询任务名下拉框数据")
	public String getTaskNameComboValuePrivilege() {
		try {
			Map<String, Object> data = performanceManagerService
					.getTaskNameComboValuePrivilege(searchCond, sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：删除报表任务", type = IMethodLog.InfoType.DELETE)
	public String deleteReportTask() {
		try {
			performanceManagerService.deleteReportTask(searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：报表任务名判重")
	public String checkTaskNameDuplicate() {
		try {
			Long r = performanceManagerService.checkTaskNameDuplicate(
					searchCond, sysUserId);
			if (r == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
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

	@IMethodLog(desc = "性能报表定制：获取网管分组")
	public String getEmsGroup() {
		try {
			Map<String, Object> data = performanceManagerService.getEmsGroup();
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：获取网管")
	public String getEms() {
		try {
			Map<String, Object> data = performanceManagerService
					.getEms(searchCond);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：获取干线")
	public String getTrunkLine() {
		try {
			Map<String, Object> data = performanceManagerService
					.getTrunkLine(searchCond);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：获取复用段信息")
	public String searchMS() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchMS(searchCond);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：获取干线信息")
	public String searchTL() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchTL(searchCond);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：保存报表任务（复用段）", type = IMethodLog.InfoType.MOD)
	public String saveMSReportTask() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			performanceManagerService.saveMSReportTask(nodeList, searchCond,
					sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：获得修改的报表任务信息（MS）", type = IMethodLog.InfoType.MOD)
	public String initMSReportTaskInfo() {
		try {
			Map<String, Object> data = performanceManagerService
					.initMSReportTaskInfo(searchCond);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：获得修改的报表任务信息（NE）", type = IMethodLog.InfoType.MOD)
	public String initNEReportTaskInfo() {
		try {
			Map<String, Object> data = performanceManagerService
					.initNEReportTaskInfo(searchCond);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：更新报表任务（复用段）", type = IMethodLog.InfoType.MOD)
	public String updateMSReportTask() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			performanceManagerService.updateMSReportTask(nodeList, searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：更新报表任务（网元）", type = IMethodLog.InfoType.MOD)
	public String updateNeReportTask() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			performanceManagerService.updateNeReportTask(nodeList, searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：网元日报（临时）")
	public String searchPMForReportNeDaily() {
		try {
			performanceManagerService.searchPMForReportNeDaily(searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	public String getPmFromTaskId() {
		try {
			pmMultipleSectionManagerService
					.getExportPmInfo(pmMultipleSectionManagerService
							.getPmFromTaskId(Integer.parseInt(searchCond
									.get("taskId"))));
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：网元月报（临时）")
	public String searchPMForReportNeMonthly() {
		try {
			performanceManagerService.searchPMForReportNeMonthly(searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_SAVE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询报表统计信息NE")
	public String searchNeReportAnalysis() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchNeReportAnalysis(searchCond);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询报表统计信息MS")
	public String searchMSReportAnalysis() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchMSReportAnalysis(searchCond);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询统计失败网元")
	public String searchCollectFailedNeInfo() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchCollectFailedNeInfo(searchCond);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询统计失败端口")
	public String searchCollectFailedPtpInfo() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchCollectFailedPtpInfo(searchCond);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：检查修改全部报表任务的执行时间", type = IMethodLog.InfoType.MOD)
	public String controlReportTaskTime() {
		try {
			performanceManagerService.controlReportTaskTime();
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询统计失败复用段")
	public String searchCollectFailedMSInfo() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchCollectFailedMSInfo(searchCond);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询报表详细信息")
	public String searchReportDetailNePm() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchReportDetailNePm(searchCond, start, limit);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：查询报表详细信息(复用段)")
	public String searchReportDetailMSPm() {
		try {
			Map<String, Object> data = performanceManagerService
					.searchReportDetailMSPm(searchCond, start, limit);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：导出数据")
	public String exportAndDownloadPmAnalysisInfo() {
		try {
			JSONArray jArray = JSONArray.fromObject(this.modifyList.get(0));
			List<String> sList = JSONArray.toList(jArray, new String(),
					new JsonConfig());
			List list = new ArrayList();
			for(String s :sList){
				list.add(JSONObject.fromObject(s));
			}
			// List<Map> exportData = ListStringtoListMap(this.modifyList);
			String filePath = performanceManagerService
					.exportAndDownloadPmAnalysisInfo(searchCond, list);
			setFilePath(filePath);
			return RESULT_DOWNLOAD;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}

	}

	@SuppressWarnings("rawtypes")
	@IMethodLog(desc = "性能报表定制：立即生成报表（网元）", type = IMethodLog.InfoType.MOD)
	public String generateNeReportImmediately() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			ExportResult result = performanceManagerService
					.generateNeReportImmediately(nodeList, searchCond,
							sysUserId);
			if (result == null) {
				result = new ExportResult();
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_OUT_OF_LIMIT));
				result.setFileName("");
				result.setFilePath("");
				result.setExportTime(new Date());
			} else {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表定制：立即生成报表（复用段）", type = IMethodLog.InfoType.MOD)
	public String generateMsReportImmediately() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			CommonResult result = pmMultipleSectionManagerService
					.exportMsPmReportInstant(nodeList, searchCond, sysUserId);
			if (result == null) {
				result = new CommonResult();
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_OUT_OF_LIMIT));
			} else {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(result.getReturnMessage());
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	// TODO
	// --------------- MeiKai Start ------------------------

	@IMethodLog(desc = "获取性能报表列表")
	public String getReportInfoList() {

		try {
			JSONObject jsonObject = JSONObject
					.fromObject(reportSearchJsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> map = (Map) JSONObject.toBean(jsonObject,
					Map.class);
			map.put("userId", sysUserId);
			map.put("limit", limit);
			map.put("start", start);
			Map<String, Object> data = performanceManagerService
					.getReportInfoList(map, sysUserId);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "打包性能报表")
	public String zipReport() {

		try {

			CommonResult data = performanceManagerService
					.zipReport(filePathList,reportSearchJsonString);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "判断用户是否有删除性能报表的权限", type = IMethodLog.InfoType.DELETE)
	public String preDeleteReport() {

		try {
			CommonResult data = performanceManagerService.preDeleteReport(
					pmReportIdList, sysUserId);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "删除性能报表", type = IMethodLog.InfoType.DELETE)
	public String deleteReport() {

		try {
			CommonResult data = performanceManagerService.deleteReport(
					pmReportIdList, sysUserId);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

//	public String test() {
//		try {
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			Map data = performanceManagerService.calculateReportCountInfo(198,
//					format.parse("2014-05-15"), format.parse("2014-05-16"));
//			resultObj = JSONObject.fromObject(data);
//
//		} catch (CommonException e) {
//			// TODO Auto-generated catch block
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return RESULT_OBJ;
//	}

	public String getReportSearchJsonString() {
		return reportSearchJsonString;
	}

	public void setReportSearchJsonString(String reportSearchJsonString) {
		this.reportSearchJsonString = reportSearchJsonString;
	}

	public List<String> getFilePathList() {
		return filePathList;
	}

	public void setFilePathList(List<String> filePathList) {
		this.filePathList = filePathList;
	}

	public List<Integer> getPmReportIdList() {
		return pmReportIdList;
	}

	public void setPmReportIdList(List<Integer> pmReportIdList) {
		this.pmReportIdList = pmReportIdList;
	}

	// --------------- MeiKai End ------------------------

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

}
