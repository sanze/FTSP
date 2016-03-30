package com.fujitsu.manager.performanceManager.action;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;

public class RegularPmAnalysisAction extends AbstractAction {
	private int emsGroupId;
	private List<String> modifyList;
	private int userId;
	private Map<String, String> searchCond;
	private int factory;
	private int templateId;
	private int needAll;
	private List<Long> condList;

	/**
	 * 业务层对象
	 */
	@Resource
	public IPerformanceManagerService performanceManagerService;

	@IMethodLog(desc = "性能采集管理:获取网管分组列表")
	public String getBaseEmsGroups() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService.getBaseEmsGroups();
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

	@IMethodLog(desc = "性能采集管理:获取网管列表")
	public String getBaseEmses() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService.getEmsList(emsGroupId,
					userId, start, limit);
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

	@IMethodLog(desc = "性能采集管理:修改网管任务信息", type = IMethodLog.InfoType.MOD)
	public String modifyEmses() {
		String returnString = RESULT_OBJ;
		List<Map> emsList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.updateEmsList(emsList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_MODIFY_TASK_TIME));
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

	@IMethodLog(desc = "性能采集管理:获取网元型号列表")
	public String getProductNames() {
		String returnString = RESULT_OBJ;
		try {
			try {
				Map result = performanceManagerService.getProductNames(
						Integer.valueOf(searchCond.get("emsId")),
						Integer.valueOf(searchCond.get("type")));
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	@IMethodLog(desc = "性能采集管理:获取网元列表")
	public String getNeList() {
		String returnString = RESULT_OBJ;
		try {
			try {
				int emsId = 0, type = 0; 
				if (searchCond.containsKey("emsId")) {
					emsId = Integer.parseInt(searchCond.get("emsId"));
				}
				if (searchCond.containsKey("type")) {
					type = Integer.parseInt(searchCond.get("type"));
				} 
				Map result = performanceManagerService.getNeList(emsId, type,
						searchCond.get("productName"), searchCond.get("subIds"),start, limit);
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	@IMethodLog(desc = "性能采集管理:修改网元任务信息", type = IMethodLog.InfoType.MOD)
	public String modifyNes() {
		String returnString = RESULT_OBJ;
		List<Map> neList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.updateNeList(neList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_MODIFY_TASK_TIME));
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
	@IMethodLog(desc = "性能采集管理:获取单网元采集状态列表")
	public String getNeStateList() {
		String returnString = RESULT_OBJ;
		try {
			try {
				Integer neId = null;
				if(searchCond.containsKey("neId"))
					neId = Integer.valueOf(searchCond.get("neId"));
				Map result = performanceManagerService.getNeStateList(neId,
						searchCond.get("startTime"),searchCond.get("endTime"),start, limit);
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}
	
	@IMethodLog(desc = "性能采集管理:获取多网元采集状态列表")
	public String getNeStateListMulti() {
		String returnString = RESULT_OBJ;
		try {
			try {
				int emsId = 0, type = 0;
				if (searchCond.containsKey("emsId")) {
					emsId = Integer.parseInt(searchCond.get("emsId"));
				}
				if (searchCond.containsKey("type")) {
					type = Integer.parseInt(searchCond.get("type"));
				}
				Map result = performanceManagerService.getNeStateListMulti(emsId,
						type, searchCond.get("productName"),searchCond.get("subnetIdStr"),
						searchCond.get("startTime"),searchCond.get("endTime"),start, limit);
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	@IMethodLog(desc = "性能采集管理:修改网元执行状态", type = IMethodLog.InfoType.MOD)
	public String changeTaskStatus() {
		String returnString = RESULT_OBJ;
		List<Map> emsList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.changeTaskStatus(emsList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_MODIFY_TASK_STATUS));
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

	@IMethodLog(desc = "性能采集管理:查询任务执行状态", type = IMethodLog.InfoType.MOD)
	public String getTasksIsRunning() {
		String returnString = RESULT_OBJ;
		try {
			try {
				Map result = new HashMap();
				result.put("returnResult", CommonDefine.TRUE);
				result.put("isRunning", CommonDefine.FALSE);
				for (String idString : modifyList) {
					Integer taskCollectResult = performanceManagerService
							.getTaskCollectResult(Integer.valueOf(idString));
					if (taskCollectResult != null
							&& taskCollectResult == CommonDefine.PM.COLLECT_STATUS.EXECUTING) {
						result.put("isRunning", CommonDefine.TRUE);
					}
				}
				resultObj = JSONObject.fromObject(result);
			} catch (NumberFormatException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return returnString;
	}

	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "性能采集管理:获取任务执行状态", type = IMethodLog.InfoType.MOD)
	public String getTaskStatus() {
		String returnString = RESULT_OBJ;
		try {
			try {
				Map result = new HashMap();
				result.put("returnResult", CommonDefine.TRUE);

				Integer taskCollectResult = performanceManagerService
						.getTaskCollectResult(Integer.valueOf(searchCond
								.get("taskId")));
				if (taskCollectResult == null) {
					result.put("COLLECT_STATUS", "null");
				} else {
					result.put("COLLECT_STATUS", taskCollectResult);
				}

				Timestamp fobiddenTimeLimit = performanceManagerService
						.getForbiddenTimeLimit(Integer.valueOf(searchCond
								.get("taskId")));
				if (fobiddenTimeLimit == null) {
					result.put("FORBIDDEN_TIME_LIMIT", "null");
				} else {
					result.put("FORBIDDEN_TIME_LIMIT", fobiddenTimeLimit);
				}
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	@IMethodLog(desc = "性能采集管理:暂停任务", type = IMethodLog.InfoType.MOD)
	public String pauseTask() {
		String returnString = RESULT_OBJ;
		try {
			try {
				int taskId = 0, pauseTime = 0;
				taskId = Integer.parseInt(searchCond.get("taskId"));
				pauseTime = Integer.parseInt(searchCond.get("pauseTime"));
				result.setReturnMessage(getText(MessageCodeDefine.PM_PAUSE_SUCCESS));
				performanceManagerService.pauseTask(taskId, pauseTime);
				result.setReturnResult(CommonDefine.TRUE);
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "性能采集管理:恢复任务", type = IMethodLog.InfoType.MOD)
	public String resumeTask() {
		String returnString = RESULT_OBJ;
		try {
			try {
				int taskId = 0;
				taskId = Integer.parseInt(searchCond.get("taskId"));
				performanceManagerService.resumeTask(taskId);
				result.setReturnResult(CommonDefine.TRUE);
				result.setReturnMessage(getText(MessageCodeDefine.PM_RESUME_SUCCESS));
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "比较值管理:获取比较值列表")
	public String getCompareValueByPage() {
		String returnString = RESULT_OBJ;
		try {
			try {
				List<Map> nodeList = ListStringtoListMap(this.modifyList);
				Map result = performanceManagerService.getCompareValueByPage(
						nodeList, start, limit);
				resultObj = JSONObject.fromObject(result);
				returnString = RESULT_OBJ;
			} catch (ClassCastException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
			} catch (NullPointerException e) {
				throw new CommonException(e,
						MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	@IMethodLog(desc = "比较值管理:修改比较值", type = IMethodLog.InfoType.MOD)
	public String modifyCompareValues() {
		String returnString = RESULT_OBJ;
		List<Map> compareValueList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.updateCompareValueList(compareValueList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_MODIFY_TASK));
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

	@IMethodLog(desc = "比较值管理:批量更新比较值", type = IMethodLog.InfoType.MOD)
	public String generateCompareValue() {
		String returnString = RESULT_OBJ;
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			String resultString = performanceManagerService.generateCompareValue(nodeList,
					Integer.valueOf(searchCond.get("overwrite")),
					searchCond.get("processKey"));
			result.setReturnResult(CommonDefine.SUCCESS);
			if(resultString == null){
				result.setReturnMessage(getText(MessageCodeDefine.PM_MODIFY_TASK));
			}else{
				result.setReturnMessage(resultString);
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

	// ***********************************咯咯咯咯咯咯***************************************

	@IMethodLog(desc = "性能分析管理:获取模板列表")
	public String getTemplates() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService.getTemplates(searchCond);
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

	@IMethodLog(desc = "性能分析管理:获取模板详细列表")
	public String getTemplatesInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService.getTemplatesInfo(factory,
					start, limit);
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

	@IMethodLog(desc = "性能分析管理:查询端口模板信息")
	public String searchPtpTemplate() {
		String returnString = RESULT_OBJ;
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			if(searchCond==null)
				searchCond = new HashMap<String, String>();
			searchCond.put("start", String.valueOf(start));
			searchCond.put("limit", String.valueOf(limit));
			Map result = performanceManagerService.searchPtpTemplate(nodeList,
					searchCond);
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

	@IMethodLog(desc = "性能分析管理:保存修改的端口模板信息", type = IMethodLog.InfoType.MOD)
	public String savePtpTemplate() {
		String returnString = RESULT_OBJ;
		List<Map> ptpTemplateList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.savePtpTemplate(ptpTemplateList,
					Integer.parseInt(searchCond.get("searchLevel").toString()));
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_SEARCH_AND_SET_TEMPLATE));
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

	@IMethodLog(desc = "性能分析管理:解除端口模板设置", type = IMethodLog.InfoType.MOD)
	public String cancelPtpTemplate() {
		String returnString = RESULT_OBJ;
		try {
			performanceManagerService.cancelPtpTemplate(condList,
					Integer.parseInt(searchCond.get("searchLevel").toString()));
			result.setReturnMessage(getText(MessageCodeDefine.PM_CANCEL_TEMPLATE));
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

	@IMethodLog(desc = "性能分析管理:单个模板应用到选中的节点", type = IMethodLog.InfoType.MOD)
	public String applyTemplate() {
		String returnString = RESULT_OBJ;
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			boolean factoryFit = performanceManagerService.applyTemplate(
					nodeList, searchCond);
			if (factoryFit) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.PM_APPLY_TEMPLATE));
				resultObj = JSONObject.fromObject(result);
			} else {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.PM_TEMPLATE_FACTORY_UNFIT));
				resultObj = JSONObject.fromObject(result);
			}
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "性能分析管理:批量解除模板", type = IMethodLog.InfoType.MOD)
	public String cancelTemplateBatch() {
		String returnString = RESULT_OBJ;
		List<Map> nodeList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.cancelTemplateBatch(nodeList, searchCond);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_CANCEL_TEMPLATE));
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

	@IMethodLog(desc = "性能分析管理:查询模板计数值详情")
	public String getNumberic() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService.getNumberic(templateId);
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

	@IMethodLog(desc = "性能分析管理:查询模板物理量详情")
	public String getPhysical() {
		String returnString = RESULT_OBJ;
		try {
			Map result = performanceManagerService.getPhysical(templateId);
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

	@IMethodLog(desc = "性能分析管理:新增模板之拷贝参考模板", type = IMethodLog.InfoType.MOD)
	public String newTemplate() {
		String returnString = RESULT_OBJ;
		String newId = "";
		Map<String, String> idResult = new HashMap<String, String>();
		try {
			newId = performanceManagerService.newTemplate(
					Integer.parseInt(searchCond.get("templateId")),
					searchCond.get("templateName"));
			if (newId.equals("duplicate")) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.PM_TEMPLATE_NAME_EXIST));
				resultObj = JSONObject.fromObject(result);
			} else {
				idResult.put("newId", newId);
				resultObj = JSONObject.fromObject(idResult);
			}
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "性能分析管理:保存新增模板计数值", type = IMethodLog.InfoType.MOD)
	public String saveNumberic() {
		String returnString = RESULT_OBJ;
		List<Map> numbericList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.saveNumberic(numbericList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_SAVE_NUMBERIC));
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

	@IMethodLog(desc = "性能分析管理:保存新增模板物理量", type = IMethodLog.InfoType.MOD)
	public String savePhysical() {
		String returnString = RESULT_OBJ;
		List<Map> physicalList = ListStringtoListMap(this.modifyList);
		try {
			performanceManagerService.savePhysical(physicalList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_SAVE_PHYSICAL));
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

	@IMethodLog(desc = "性能分析管理:删除模板", type = IMethodLog.InfoType.DELETE)
	public String deleteTemplate() {
		String returnString = RESULT_OBJ;
		try {
			boolean r = performanceManagerService.deleteTemplate(condList);
			if (r) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.PM_DELETE_TEMPLATE));
				resultObj = JSONObject.fromObject(result);
			} else {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.PM_TEMPLATE_APPLIED));
				resultObj = JSONObject.fromObject(result);
			}
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "性能分析管理:性能模板应用之解除模板", type = IMethodLog.InfoType.MOD)
	public String detachTemplate() {
		String returnString = RESULT_OBJ;
		try {
			performanceManagerService.detachTemplate(condList, sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.PM_CANCEL_TEMPLATE));
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

	public int getEmsGroupId() {
		return emsGroupId;
	}

	public void setEmsGroupId(int emsGroupId) {
		this.emsGroupId = emsGroupId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public int getFactory() {
		return factory;
	}

	public void setFactory(int factory) {
		this.factory = factory;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public List<String> getModifyList() {
		return modifyList;
	}

	public void setModifyList(List<String> modifyList) {
		this.modifyList = modifyList;
	}

	public int getNeedAll() {
		return needAll;
	}

	public void setNeedAll(int needAll) {
		this.needAll = needAll;
	}

	public Map<String, String> getSearchCond() {
		return searchCond;
	}

	public void setSearchCond(Map<String, String> searchCond) {
		this.searchCond = searchCond;
	}

	public List<Long> getCondList() {
		return condList;
	}

	public void setCondList(List<Long> condList) {
		this.condList = condList;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
