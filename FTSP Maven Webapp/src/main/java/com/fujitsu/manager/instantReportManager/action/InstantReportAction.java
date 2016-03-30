package com.fujitsu.manager.instantReportManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IInstantReportService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;

public class InstantReportAction extends DownloadAction {
	@Resource
	public IInstantReportService instantReportService;

	private Map<String, String> condMap;
	private List<String> sList;
	private List<Long> lList;

	// FIELDS ***************************

	@IMethodLog(desc = "性能报表：光路误码监测记录表生成", type = IMethodLog.InfoType.MOD)
	public String generateOptPathBitErrReport() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.sList);

			String filePath = instantReportService.generateOptPathBitErrReport(
					condMap, nodeList, sysUserId);
//			setFilePath(filePath);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(filePath);
			resultObj = JSONObject.fromObject(result);
//			return RESULT_DOWNLOAD;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@SuppressWarnings("rawtypes")
	@IMethodLog(desc = "性能报表：光功率记录表生成", type = IMethodLog.InfoType.MOD)
	public String generateSDHLightPowerReport() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.sList);
			
			String filePath = instantReportService.generateSDHLightPowerReport(
					condMap, nodeList, sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(filePath);
			resultObj = JSONObject.fromObject(result);
//			setFilePath(filePath);
//			return RESULT_DOWNLOAD;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "性能报表：检测网元数量是否超标")
	public String neCountCheck() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.sList);
//			目前未考虑到是否SDH
			int resultCode = instantReportService.neCountCheck(nodeList);

			result.setReturnResult(resultCode);
			if (resultCode == 0)
				result.setReturnMessage(getText(MessageCodeDefine.PM_NE_REPORT_NE_OUT_OF_LIMIT));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	
	@IMethodLog(desc = "性能报表：下载")
	public String download() {
			setFilePath(this.filePath);
			return RESULT_DOWNLOAD;
	}
	
	// GETTERS&SETTERS *************************

	public Map<String, String> getCondMap() {
		return condMap;
	}

	public void setCondMap(Map<String, String> condMap) {
		this.condMap = condMap;
	}

	public List<String> getSList() {
		return sList;
	}

	public void setSList(List<String> sList) {
		this.sList = sList;
	}

	public List<Long> getLList() {
		return lList;
	}

	public void setLList(List<Long> lList) {
		this.lList = lList;
	}
}
