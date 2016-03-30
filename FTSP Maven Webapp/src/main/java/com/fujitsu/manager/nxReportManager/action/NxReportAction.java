package com.fujitsu.manager.nxReportManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.INxReportManagerService;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.dao.mysql.bean.ResourceUnitManager;


@SuppressWarnings("serial")
public class NxReportAction extends AbstractAction { 
	@Resource
	public INxReportManagerService nxReportManagerService;
	@Resource
	public IPerformanceManagerService performanceManagerService;
	
	private Map<String,String> paramMap=new HashMap<String,String>(); 
	private List<String> modifyList;
	private List<Integer> intList;
	private String jsonString;
	private int reportType;
	private List<ResourceUnitManager> manages;
	private int manageId;
	private int taskId;
	private int unitId;
	private int factoryId;
	private List<Integer> unitIds;
	private static JsonConfig cfg = new JsonConfig();
	static {
		cfg.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				return value == null;
			}
		});
	}
	
	//--------------------------THJ-----------------------------
	@IMethodLog(desc = "板卡管理：TEST------")
	public String test() {
		String rv = null;
		try {
			List<Map> targetList = new ArrayList<Map>();
			Map m = new HashMap();
			m.put("targetId", 18L);
			targetList.add(m);
			rv = nxReportManagerService.getReport_WaveTransOUT(targetList, paramMap, sysUserId, 0);
//			rv = nxReportManagerService.getExcelPreview(paramMap);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject("{file:'" + rv + "'}");
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：获取网元下的板卡")
	public String getUnitList() {
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			Map<String, Object> data = nxReportManagerService.searchUnitInterfaceByNeList(nodeList,paramMap,start,limit);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：获取网元下的板卡")
	public String getPtpByUnit() {
		try {
			Map<String, Object> data = nxReportManagerService
							.getPtpByUnit(jsonString);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：获取板卡已使用的接口")
	public String getUsedPtp() {
		try {
			Map<String, Object> data = nxReportManagerService
							.getUsedPtp(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：获取网元下的板卡")
	public String getUnitInterface() {
		try {
			Map<String, Object> data = nxReportManagerService
							.getUnitInterface(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "板卡管理：保存板卡接口", type = IMethodLog.InfoType.MOD)
	public String saveUnitInterface() {
		try {
			System.out.println(paramMap.toString());
//			paramMap = null;
			int resultCode = nxReportManagerService.saveUnitInterface(paramMap, sysUserId);
			if (resultCode == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("保存失败！");
			}
			if (resultCode > 0) {
				result.setReturnResult(resultCode);
				result.setReturnMessage("保存成功！");
			}
			if (resultCode < 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("该板卡已被使用！");
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：删除板卡接口", type = IMethodLog.InfoType.DELETE)
	public String delUnitInterface() {
		try {
			nxReportManagerService.delUnitInterface(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：更新板卡接口", type = IMethodLog.InfoType.MOD)
	public String updateUnitInterface() {
		try {
			nxReportManagerService.updateUnitInterface(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：关联光口标准到板卡接口")
	public String relateOpticalStandardValue() {
		try {
			nxReportManagerService.relateOpticalStandardValue(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("关联成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "板卡管理：判断板卡接口是否存在")
	public String isUnitInterfaceExist() {
		try {
			System.out.println(paramMap.toString());
//			paramMap = null;
			int resultCode = nxReportManagerService.isUnitInterfaceExist(paramMap, sysUserId);
			if (resultCode == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("同一网元内方向不能重复！");
			}
			if (resultCode == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("保存成功！");
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "光开关盘管理：保存光开关盘接口", type = IMethodLog.InfoType.MOD)
	public String saveOptSwitch() {
		try {
			System.out.println(paramMap.toString());
//			paramMap = null;
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			int resultCode = nxReportManagerService.saveOptSwitch(paramMap, nodeList);
			if (resultCode < 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("该板卡已被使用！");
			}
			if (resultCode == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("保存失败！");
			}
			if (resultCode > 0) {
				result.setReturnResult(resultCode);
				result.setReturnMessage("保存成功！");
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "光开关盘管理：删除光开关盘接口", type = IMethodLog.InfoType.DELETE)
	public String delOptSwitch() {
		try {
			nxReportManagerService.delUnitInterface(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "光开关盘管理：更新光开关盘接口", type = IMethodLog.InfoType.MOD)
	public String updateOptSwitch() {
		try {
			nxReportManagerService.updateUnitInterface(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "光开关盘：获取保存的业务板卡信息", type = IMethodLog.InfoType.MOD)
	public String getSavedBusinessPtpInfo() {
		try {
			Map<String, Object> data = nxReportManagerService
							.getSavedBusinessPtpInfo(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "光开关盘：获取业务板卡信息")
	public String getBusinessPtpInfo() {
		try {
			Map<String, Object> data = nxReportManagerService
							.getBusinessPtpInfo(unitId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	//--------------------------THJ-----------------------------
	
	
	
	//--------------------------WSS-----------------------------
	
	@IMethodLog(desc = "方向管理：获取网元下的板卡")
	public String getUnitByNe() {
		
		try {
			Map<String, Object> data = nxReportManagerService
							.getUnitByNeOrWaveDirId(paramMap);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "方向管理：保存波分方向", type = IMethodLog.InfoType.MOD)
	public String saveWaveDir() {
		try {
			List<Map> unitList = ListStringtoListMap(this.modifyList);
			int resultCode = nxReportManagerService.saveWaveDir(
					unitList, paramMap, sysUserId);
			if (resultCode == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("同一网元内方向不能重复！");
			}
			if (resultCode == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("保存成功！");
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "方向管理：查找波分方向")
	public String searchWaveDir() {
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			Map<String, Object> data = nxReportManagerService
							.searchWaveDir(nodeList,start,limit);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "方向管理：修改波分方向", type = IMethodLog.InfoType.MOD)
	public String editWaveDir() {
		try {
			List<Map> unitList = ListStringtoListMap(this.modifyList);
			int resultCode = nxReportManagerService.editWaveDir(
					unitList, paramMap, sysUserId);
			if (resultCode == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("同一网元内方向不能重复！");
			}
			if (resultCode == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("保存成功！");
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "方向管理：删除波分方向", type = IMethodLog.InfoType.DELETE)
	public String deleteWaveDir() {
		try {
			List<Map> unitList = ListStringtoListMap(this.modifyList);
			nxReportManagerService.deleteWaveDir(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "宁夏报表：任务名判重")
	public String checkTaskNameDuplicate() {
		try {
			Long r = 0L;
			if(paramMap.get("taskType")!=null){
				int[] taskTypes = {Integer.parseInt(paramMap.get("taskType").toString())};
				r = performanceManagerService.checkTaskNameDuplicate(
						paramMap, sysUserId, taskTypes);
			}else{
				r = performanceManagerService.checkTaskNameDuplicate(
						paramMap, sysUserId, null);
			}
			if (r == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
			} else {
				result.setReturnMessage("您已创建过相同任务名的任务，请勿重复！");
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
	
	
	@IMethodLog(desc = "宁夏报表：保存报表任务", type = IMethodLog.InfoType.MOD)
	public String saveReportTask() {
		try {
			List<Map> targetList = ListStringtoListMap(this.modifyList);
			nxReportManagerService.saveReportTask(targetList, paramMap, sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功!");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：保存报表任务-AMP", type = IMethodLog.InfoType.MOD)
	// stop using
	public String saveReportTaskAMP() {
		try {
			nxReportManagerService.saveReportTaskAMP(manages, paramMap, sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功!");
			resultObj = JSONObject.fromObject(result);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	

	@IMethodLog(desc = "宁夏报表：查询任务名下拉框数据")
	public String getTaskNameComboValue() {
		try {
			Map<String, Object> data = nxReportManagerService
					.getTaskNameComboValue(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：查询任务名下拉框数据")
	public String getTaskNameComboValuePrivilege() {
		try {
			Map<String, Object> data = nxReportManagerService
					.getTaskNameComboValuePrivilege(paramMap, sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：查询报表任务")
	public String searchReportTask() {
		try {
			Map<String, Object> data = nxReportManagerService
					.searchReportTask(paramMap,start,limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：初始化报表任务")
	public String initReportTaskInfo() {
		try {
			Map<String, Object> data = nxReportManagerService
					.initReportTaskInfo(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：查找波分方向（ID）")
	public String searchWaveDirById() {
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			Map<String, Object> data = nxReportManagerService
							.searchWaveDirById(nodeList);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "宁夏报表：更新报表任务", type = IMethodLog.InfoType.MOD)
	public String updateReportTask() {
		try {
			List<Map> targetList = ListStringtoListMap(this.modifyList);
			nxReportManagerService.updateReportTask(targetList, paramMap,intList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功!");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：查询创建人下拉框数据(privilege)")
	public String getCreatorComboValuePrivilege() {
		try {
			Map<String, Object> data = nxReportManagerService
					.getCreatorComboValuePrivilege(sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：定制报表即时生成", type = IMethodLog.InfoType.MOD)
	public String getReportInstantly() {
		try {
			List<Map> targetList = ListStringtoListMap(this.modifyList);
			List<Integer> targetIdList = new ArrayList<Integer>();
			for(Map m:targetList){
				targetIdList.add(Integer.valueOf(m.get("targetId").toString()));
			}
			switch(reportType){
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH:
				nxReportManagerService.getReport_WaveTransOUT(targetList, paramMap,sysUserId,CommonDefine.REPORT.REPORT_INSTANT);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_AMP:
				nxReportManagerService.getAmplifierDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,false,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_SWITCH:
				nxReportManagerService.getSwitchDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,false,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_DIV:
				paramMap.put("unitType",String.valueOf(CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV));
				nxReportManagerService.getWaveDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,false,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_JOIN:
				paramMap.put("unitType",String.valueOf(CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN));
				nxReportManagerService.getWaveDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,false,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_SDH_PM:
				break;
			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("生成成功!");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "宁夏报表：空表预览")
	public String getReportPreview() {
		try {
			List<Map> targetList = ListStringtoListMap(this.modifyList);
			String path = "";
			List<Integer> targetIdList = new ArrayList<Integer>();
			for(Map m:targetList){
				if(m.get("targetId")!=null)
					targetIdList.add(Integer.valueOf(m.get("targetId").toString()));
			}
			
			switch(reportType){
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH:
				path = nxReportManagerService.getReportPreview_WaveTransOUT(targetList, paramMap);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_AMP:
				
				path = nxReportManagerService.getAmplifierDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_SCHEDULE,true,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_SWITCH:
				path = nxReportManagerService.getSwitchDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_SCHEDULE,true,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_DIV:
				paramMap.put("unitType",String.valueOf(CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV));
				path = nxReportManagerService.getWaveDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,true,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_JOIN:
				paramMap.put("unitType",String.valueOf(CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN));
				path = nxReportManagerService.getWaveDataForReport(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,true,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_SDH_PM:
				path = nxReportManagerService.getReportPreview_WaveTransOUT(targetList, paramMap);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_IPRAN:
				path = nxReportManagerService.getReport_PTN_IPRAN(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,true,sysUserId);
				break;
			case CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK:
				path = nxReportManagerService.getReport_PTN_FlowPeak(targetIdList, paramMap,CommonDefine.REPORT.REPORT_INSTANT,true,sysUserId);
				break;
			}
			
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(path);
			resultObj = JSONObject.fromObject(result);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "宁夏报表：预览Excel报表")
	public String getExcelPreview() {
		try {
			String path = nxReportManagerService.getExcelPreview(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(path);
			resultObj = JSONObject.fromObject(result);
	
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：批量删除manage", type = IMethodLog.InfoType.DELETE)
	public String deleteUnitManageByManageIdList(){
		try{
			result = (CommonResult)nxReportManagerService.deleteUnitReportByManageId(intList);
		}catch(CommonException e){
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "宁夏报表：获取节点信息")
	public String getNodeInfo() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			Map<String, Object> data = nxReportManagerService
					.getNodeInfo(nodeList);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：删除报表任务", type = IMethodLog.InfoType.DELETE)
	public String deleteReportTask() {
		try {
			nxReportManagerService.deleteReportTask(taskId, reportType);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "PTN系统：保存PTN系统", type = IMethodLog.InfoType.MOD)
	public String savePtnSys() {
		try {
			int resultCode = nxReportManagerService.savePtnSys(
					intList, paramMap, sysUserId);
			if (resultCode == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("系统名称不能重复！");
			}
			if (resultCode == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("保存成功！");
			}
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：查找PTN系统")
	public String getPtnSysList() {
		try {
			Map<String, Object> data = nxReportManagerService
							.getPtnSysList(paramMap,intList,start, limit);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：根据ptpId作为A端查找link信息")
	public String getLinkByAEnd(){
		try{
			List<Map> map = nxReportManagerService.getLinkByAEnd(intList);
			Map <String , Object> result = new HashMap<String, Object>();
			result.put("returnResult",CommonDefine.SUCCESS);
			result.put("links",map);
			resultObj = JSONObject.fromObject(result);
		}catch(CommonException e){
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			e.printStackTrace();
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "宁夏报表：查找系统下的ptp供查看")
	public String getLinksBySysId(){
		try{
			List<Map> map = nxReportManagerService.getLinksBySysId(paramMap);
			Map <String , Object> result = new HashMap<String, Object>();
			result.put("returnResult",CommonDefine.SUCCESS);
			result.put("links",map);
			resultObj = JSONObject.fromObject(result);
		}catch(CommonException e){
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			e.printStackTrace();
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "板卡管理：删除板卡接口", type = IMethodLog.InfoType.DELETE)
	public String delPtnSys() {
		try {
			nxReportManagerService.delPtnSys(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功！");
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	//--------------------------WSS-----------------------------
	//-------------------------DHJ-----------------------------
	@IMethodLog(desc = "宁夏报表：删除manage", type = IMethodLog.InfoType.DELETE)
	public String deleteUnitManage(){
		try{
			result = (CommonResult)nxReportManagerService.deleteUnitReport(manages);
		}catch(CommonException e){
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
//	@IMethodLog(desc = "宁夏报表：更新单元盘", type = IMethodLog.InfoType.MOD)
//	public String UpdateUnits(){
//		return RESULT_OBJ;
//	}
	@IMethodLog(desc = "宁夏报表：插入manage和单元盘信息")
	public String insertManageInfo(){
		try {
			result = (CommonResult)nxReportManagerService.insertManageWithUnit(manages);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("插入失败");
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	public String getUnitInfoByManageId(){
		Map <String , Object> map = new HashMap<String,Object>(); 
		try{
			map = nxReportManagerService.getUnitInfoByManageId(manageId,reportType);
		}catch(CommonException e){
			map.put("total", 0);
			map.put("rows", new ArrayList<String>());
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(map,cfg);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "宁夏报表：修改manage和单元盘信息", type = IMethodLog.InfoType.MOD)
	public String updateManageInfo(){
		try {
			if(manages.size()>0){
				result = (CommonResult)nxReportManagerService.updateManageInfo(manages.get(0));
			}else{
				result.setReturnMessage("更新失败");
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("插入失败");
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "宁夏报表：根据taskId获取manage信息")
	public String getManageInfoByTaskId(){
		Map <String , Object> map = new HashMap<String,Object>(); 
		int type = 0;
		switch(reportType){
		case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_AMP: type = CommonDefine.NX_REPORT.UNIT_TYPE.AMP;break;
		case CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_SWITCH: type = CommonDefine.NX_REPORT.UNIT_TYPE.SWITCH;break;
		case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_JOIN:type = CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN;break;
		case CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_DIV : type = CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV;break;
		default :type = CommonDefine.NX_REPORT.UNIT_TYPE.AMP;
		}
		try{
			map = nxReportManagerService.getManageInfoByTaskId(taskId,type);
		}catch(CommonException e){
			map.put("total", 0);
			map.put("rows", new ArrayList<String>());
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(map,cfg);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "宁夏报表：根据uintId获取端口信息")
	public String getPortByUnitId(){
		Map <String , Object> map = new HashMap<String,Object>(); 
		try{
			map = nxReportManagerService.getPortByUnitId(unitId);
		}catch(CommonException e){
			map.put("total", 0);
			map.put("rows", new ArrayList<String>());
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "宁夏报表：根据factoryId获取设备型号信息(不包括sdh)")
	public String getProductNameByFactoryIdNoSDH(){
		Map <String , Object> map = new HashMap<String,Object>(); 
		try{
			map = nxReportManagerService.getProductNameByFactoryIdNoSDH(factoryId);
		}catch(CommonException e){
			map.put("total", 0);
			map.put("rows", new ArrayList<String>());
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "宁夏报表：根据unitId获取节点详细信息")
	public String getNodeInfoByUnitId(){
		Map <String , Object> map = new HashMap<String,Object>(); 
		try{
			map = nxReportManagerService.getNodeInfoByUnitId(unitIds);
		}catch(CommonException e){
			map.put("total", 0);
			map.put("rows", new ArrayList<String>());
			e.printStackTrace();
		}
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
	
	

	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	public List<String> getModifyList() {
		return modifyList;
	}
	public void setModifyList(List<String> modifyList) {
		this.modifyList = modifyList;
	}
	public List<Integer> getIntList() {
		return intList;
	}
	public void setIntList(List<Integer> intList) {
		this.intList = intList;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public List<ResourceUnitManager> getManages() {
		return manages;
	}
	public void setManages(List<ResourceUnitManager> manages) {
		this.manages = manages;
	}
	public int getReportType() {
		return reportType;
	}
	public void setReportType(int reportType) {
		this.reportType = reportType;
	}
	public int getManageId() {
		return manageId;
	}
	public void setManageId(int manageId) {
		this.manageId = manageId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public int getFactoryId() {
		return factoryId;
	}
	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}
	public List<Integer> getUnitIds() {
		return unitIds;
	}
	public void setUnitIds(List<Integer> unitIds) {
		this.unitIds = unitIds;
	}
	
}
