package com.fujitsu.manager.nxReportManager.serviceImpl;

import java.io.File;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.common.Result;
import com.fujitsu.common.poi.MultiColumnMap;
import com.fujitsu.common.poi.MultiHearderExcelUtil;
import com.fujitsu.dao.mysql.NxReportManagerMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.dao.mysql.bean.ResourceUnitManageRelUnit;
import com.fujitsu.dao.mysql.bean.ResourceUnitManager;
import com.fujitsu.job.NxReportJob;
import com.fujitsu.manager.nxReportManager.service.NxReportManagerService;
import com.fujitsu.manager.nxReportManager.serviceImpl.util.Conveter;
import com.fujitsu.manager.nxReportManager.serviceImpl.util.OptAmpExcel;
import com.fujitsu.manager.nxReportManager.serviceImpl.util.OptSwitchExcel;
import com.fujitsu.manager.nxReportManager.serviceImpl.util.WaveUnitExcel;
import com.fujitsu.manager.nxReportManager.serviceImpl.util.WavelengthTransformationExcel;
import com.fujitsu.util.SpringContextUtil;
import com.fujitsu.util.TimeUtil;
@Service
@Transactional(rollbackFor = Exception.class)
public class NxReportManagerImpl extends NxReportManagerService {
	@Resource
	private NxReportManagerMapper nxReportManagerMapper;
	@Resource
	private IPerformanceManagerService performanceManagerService;
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;

	// --------------------------THJ-----------------------------

	@Override
	public Map<String, Object> getUnitInterface(Map<String, String> paramMap)
			throws CommonException {
		Map<String, Object> rv = nxReportManagerMapper
				.getUnitInterface(paramMap);
		return rv;
	}

	@Override
	public Map<String, Object> getUsedPtp(Map<String, String> paramMap)
			throws CommonException {
		// TODO Auto-generated method stub
		Map<String, Object> rv = new HashMap<String, Object>();
		List<Map> lst = nxReportManagerMapper.getUsedPtpInfo(paramMap);
		rv.put("total", lst.size());
		rv.put("rows", lst);
		return rv;
	}

	@Override
	public Map<String, Object> searchUnitInterfaceByNeList(List<Map> nodeList,
			Map<String, String> paramMap, int start, int limit)
			throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try {
			List<Integer> neList = performanceManagerService
					.getNeIdsFromNodes(nodeList);
			// String neIds = "(" + neList.get(0);
			// for (int i = 1; i < neList.size(); i++) {
			// neIds += "," + neList.get(i);
			// }
			// neIds += ")";
			// System.out.println("neIds = "+neIds);
			List<Map> dirList = new ArrayList<Map>();
			Integer dirListCount = 0;
			if (neList != null && neList.size() > 0) {
				dirList = nxReportManagerMapper.searchUnitInterfaceByNeList(
						neList, paramMap, start, limit);
				dirListCount = nxReportManagerMapper
						.searchUnitInterfaceByNeListCount(neList, paramMap);
			}
			returnResult.put("rows", dirList);
			returnResult.put("total", dirListCount);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return returnResult;
	}

	@Override
	public int isUnitInterfaceExist(Map<String, String> paramMap,
			Integer sysUserId) throws CommonException {
		return nxReportManagerMapper.isUnitInterfaceExist(paramMap);
	}

	@Override
	public Map<String, Object> getPtpByUnit(String unitId)
			throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try {
			List<Map> resultList = nxReportManagerMapper
					.getPtpByUnitId(unitId);
			returnResult.put("rows", resultList);
			returnResult.put("total", resultList.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return returnResult;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int saveUnitInterface(Map<String, String> paramMap, Integer sysUserId)
			throws CommonException {
		int resultCode = 1;
		try {
			int nameCount = nxReportManagerMapper
					.isOptSwitchExist(paramMap);
			if (nameCount > 0) {
				return -1;
			}
			if(paramMap.containsKey("UNIT_INTERFACE_ID")){
				return updateUnitInterface(paramMap);
			}
			nameCount = nxReportManagerMapper
					.isUnitInterfaceExist(paramMap);
			if (nameCount > 0) {
				return -1;
			}
			Map idMap = new HashMap();
			nxReportManagerMapper.saveUnitInterface(paramMap, idMap);
			String ids = paramMap.get("BUSINESS_LIST");
			if (ids == null)
				ids = "";
			String[] idList = ids.split(",");
			Map map = new HashMap();
			map.put("RESOURCE_UNIT_INTERFACE_ID", idMap.get("newId"));
			map.put("PTP_TYPE", 1);
			for (String id : idList) {
				map.put("BASE_PTP_ID", id);
				if (id.length() > 0)
					nxReportManagerMapper.saveUnitInterfacePtp(map, idMap);
			}
			ids = paramMap.get("WDM_LIST");
			if (ids == null)
				ids = "";
			idList = ids.split(",");
			map.put("PTP_TYPE", 2);
			for (String id : idList) {
				map.put("BASE_PTP_ID", id);
				if (id.length() > 0)
					nxReportManagerMapper.saveUnitInterfacePtp(map, idMap);
			}
			// for(Map unit : unitList){
			// unit.put("dirId", idMap.get("newId"));
			// nxReportManagerMapper.saveUnitInfo(unit);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return resultCode;
	}

	@Override
	public void delUnitInterface(Map<String, String> paramMap)
			throws CommonException {
		try {
			nxReportManagerMapper.deleteUnitInterface(paramMap);
			nxReportManagerMapper.clearUnitInterfaceInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int updateUnitInterface(Map<String, String> paramMap)
			throws CommonException {
		try {
			// paramMap多出 RESOURCE_UNIT_INTERFACE_ID 字段
			// 更新波分侧信息
			Map idMap = new HashMap();
			nxReportManagerMapper.updateUnitInterface(paramMap);
			// 首先清空业务侧和波分侧信息
			nxReportManagerMapper.delUnitInterfacePtp(paramMap);
			// save 业务侧 & 波分侧
			// 业务侧
			String ids = paramMap.get("BUSINESS_LIST");
			if (ids == null)
				ids = "";
			String[] idList = ids.split(",");
			Map map = new HashMap();
			map.put("RESOURCE_UNIT_INTERFACE_ID",
					paramMap.get("RESOURCE_UNIT_INTERFACE_ID"));
			map.put("PTP_TYPE", 1);
			for (String id : idList) {
				map.put("BASE_PTP_ID", id);
				if (id.length() > 0)
					nxReportManagerMapper.saveUnitInterfacePtp(map, idMap);
			}
			// 波分侧
			ids = paramMap.get("WDM_LIST");
			if (ids == null)
				ids = "";
			idList = ids.split(",");
			map.put("PTP_TYPE", 2);
			for (String id : idList) {
				map.put("BASE_PTP_ID", id);
				if (id.length() > 0)
					nxReportManagerMapper.saveUnitInterfacePtp(map, idMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	@Override
	public void relateOpticalStandardValue(Map<String, String> paramMap)
			throws CommonException {
		try {
			nxReportManagerMapper.relateOpticalStandardValue(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int saveOptSwitch(Map<String, String> paramMap, List<Map> modifyList)
			throws CommonException {
		int resultCode = 1;
		try {
			int nameCount = nxReportManagerMapper
					.isUnitInterfaceExist(paramMap);
			if (nameCount > 0) {
				return -1;
			}
			if(paramMap.containsKey("UNIT_INTERFACE_ID")){
				return updateOptSwitch(paramMap, modifyList);
			}
			nameCount = nxReportManagerMapper
					.isOptSwitchExist(paramMap);
			if (nameCount > 0) {
				return -1;
			}
			Map idMap = new HashMap();
			nxReportManagerMapper.saveOptSwitch(paramMap, idMap);
			Map map = new HashMap();
			map.put("RESOURCE_UNIT_INTERFACE_ID", idMap.get("newId"));
			for(Map ptp : modifyList){
				//{"GROUP_NUM":0,"PTP_TYPE":4,"BASE_PTP_ID":61253,"POWER_BUDGET":"-8.00"}
				map.put("GROUP_NUM", ptp.get("GROUP_NUM"));
				map.put("PTP_TYPE", ptp.get("PTP_TYPE"));
				map.put("BASE_PTP_ID", ptp.get("BASE_PTP_ID"));
				map.put("POWER_BUDGET", ptp.get("POWER_BUDGET"));
				map.put("BUSSINESS_NAME", ptp.get("BUSSINESS_NAME"));
				map.put("SWITCH_THRESHOLD", ptp.get("SWITCH_THRESHOLD"));
				map.put("DIRECTION", ptp.get("DIRECTION"));
				map.put("WAVE_LENGTH", ptp.get("WAVE_LENGTH"));
				nxReportManagerMapper.saveOptSwitchPtp(map, idMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return resultCode;
	}

	@Override
	public void delOptSwitch(Map<String, String> paramMap)
			throws CommonException {
		try {
			nxReportManagerMapper.deleteUnitInterface(paramMap);
			nxReportManagerMapper.clearUnitInterfaceInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int updateOptSwitch(Map<String, String> paramMap, List<Map> modifyList)
			throws CommonException {
		try {
			// paramMap多出 RESOURCE_UNIT_INTERFACE_ID 字段
			// 更新波分侧信息
			Map idMap = new HashMap();
			nxReportManagerMapper.updateUnitInterface(paramMap);
			// 首先清空业务侧和波分侧信息
			nxReportManagerMapper.delUnitInterfacePtp(paramMap);
			// save 业务侧 & 波分侧
			Map map = new HashMap();
			map.put("RESOURCE_UNIT_INTERFACE_ID",
					paramMap.get("RESOURCE_UNIT_INTERFACE_ID"));
			for(Map ptp : modifyList){
				//{"GROUP_NUM":0,"PTP_TYPE":4,"BASE_PTP_ID":61253,"POWER_BUDGET":"-8.00"}
				map.put("GROUP_NUM", ptp.get("GROUP_NUM"));
				map.put("PTP_TYPE", ptp.get("PTP_TYPE"));
				map.put("BASE_PTP_ID", ptp.get("BASE_PTP_ID"));
				map.put("POWER_BUDGET", ptp.get("POWER_BUDGET"));
				map.put("BUSSINESS_NAME", ptp.get("BUSSINESS_NAME"));
				map.put("SWITCH_THRESHOLD", ptp.get("SWITCH_THRESHOLD"));
				map.put("DIRECTION", ptp.get("DIRECTION"));
				map.put("WAVE_LENGTH", ptp.get("WAVE_LENGTH"));
				nxReportManagerMapper.saveOptSwitchPtp(map, idMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	

	@Override
	public Map<String, Object> getBusinessPtpInfo(int unitId)
			throws CommonException {
		try {
			List<Map> dat = nxReportManagerMapper.getBusinessPtpInfo(unitId+"");
			Map rv = new HashMap();
			rv.put("total", dat.size());
			rv.put("rows", dat);
			return rv;
		} catch (Exception e) {
			throw new CommonException(e,-1, "查询业务板卡信息出错！");
		}
	}
	

	@Override
	public Map<String, Object> getSavedBusinessPtpInfo(
			Map<String, String> paramMap) throws CommonException {
		try {
			List<Map> dat = nxReportManagerMapper.getSavedBusinessPtpInfo(paramMap);
			Map rv = new HashMap();
			rv.put("total", dat.size());
			rv.put("rows", dat);
			return rv;
		} catch (Exception e) {
			throw new CommonException(e,-1, "查询业务板卡信息出错！");
		}
	}
	// --------------------------THJ-----------------------------

	// --------------------------WSS-----------------------------
	@Override
	public Map<String, Object> getUnitByNeOrWaveDirId(
			Map<String, String> paramMap) throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try {
			List<Map> resultList = nxReportManagerMapper
					.getUnitByNeOrWaveDirId(paramMap);
			returnResult.put("rows", resultList);
			returnResult.put("total", resultList.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return returnResult;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int saveWaveDir(List<Map> unitList, Map<String, String> paramMap,
			Integer sysUserId) throws CommonException {
		int resultCode = 1;
		try {
			int nameCount = nxReportManagerMapper.isDirNameExist(paramMap);
			if (nameCount > 0)
				return 0;
			Map idMap = new HashMap();
			nxReportManagerMapper.saveWaveDir(paramMap, idMap);
			for (Map unit : unitList) {
				unit.put("dirId", idMap.get("newId"));
				nxReportManagerMapper.saveUnitInfo(unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return resultCode;
	}

	@Override
	public Map<String, Object> searchWaveDir(List<Map> nodeList, int start,
			int limit) throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try {
			List<Integer> neList = performanceManagerService
					.getNeIdsFromNodes(nodeList);
			if (neList.size() > 0) {
				List<Map> dirList = nxReportManagerMapper
						.searchWaveDirByNeList(neList, start, limit);
				Integer dirListCount = nxReportManagerMapper
						.searchWaveDirByNeListCount(neList);
				returnResult.put("rows", dirList);
				returnResult.put("total", dirListCount);
			} else {
				returnResult.put("rows", "");
				returnResult.put("total", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return returnResult;
	}

	@Override
	public int editWaveDir(List<Map> unitList, Map<String, String> paramMap,
			Integer sysUserId) throws CommonException {
		int resultCode = 1;
		try {
			int nameCount = nxReportManagerMapper.isDirNameExist(paramMap);
			if (nameCount > 0)
				return 0;
			nxReportManagerMapper.updateWaveDir(paramMap);
			for (Map unit : unitList) {
				unit.put("dirId", paramMap.get("waveDirId"));
				nxReportManagerMapper.saveUnitInfo(unit);
			}
			if (paramMap.get("unitIdRemoved") != null
					&& !paramMap.get("unitIdRemoved").isEmpty()) {
				nxReportManagerMapper.clearUnitDirInfo(paramMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return resultCode;
	}

	@Override
	public void deleteWaveDir(Map<String, String> paramMap)
			throws CommonException {
		try {
			nxReportManagerMapper.deleteWaveDir(paramMap);
			nxReportManagerMapper.clearUnitDirInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
	}

	@Override
	public void saveReportTask(List<Map> targetList,
			Map<String, String> paramMap, Integer sysUserId)
			throws CommonException {
		try {
			Map idMap = new HashMap();
			if (paramMap.get("WdmPm") != null) {
				List<String> pmStdIndex = performanceManagerMapper
						.getPmStdIndexes(paramMap.get("WdmPm").toString(),
								false);
				String wdmPm = pmStdIndex.toString();
				paramMap.put("WdmPm", wdmPm.substring(1, wdmPm.length() - 1));
			}

			nxReportManagerMapper.saveReportSysTask(paramMap, sysUserId, idMap);
			nxReportManagerMapper.saveReportSysTaskInfo(targetList, idMap);
			nxReportManagerMapper.saveReportTaskParam(paramMap, idMap);

			// 建立定时任务
			int taskId = Integer.parseInt(idMap.get("newId").toString());
			String[] time = paramMap.get("hour").split(":");

			int hour = Integer.parseInt(time[0]);
			int minute = Integer.parseInt(time[1]);
			int periodType = Integer.parseInt(paramMap.get("period"));
			int taskType = Integer
					.parseInt(paramMap.get("taskType").toString());
			int delay = Integer.parseInt(paramMap.get("delay"));
			// 等待JOB
			performanceManagerService.addOrEditReportQuartzTask(taskId, hour,
					minute, periodType, taskType, delay, NxReportJob.class);

			if (taskType != CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH) {
				nxReportManagerMapper.setIsDelFalseUnitManage(targetList);
				nxReportManagerMapper.setIsDelFalseUnitManageRel(targetList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getTaskNameComboValue(
			Map<String, String> paramMap) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			paramMap.put("allNxTaskType", NX_ALL_TASKTYPE.toString());
			returnList = nxReportManagerMapper.getTaskNameComboValue(paramMap);
			if ("1".equals(paramMap.get("needAll"))) {
				Map<String, String> allMap = new HashMap<String, String>();
				allMap.put("taskId", "0");
				allMap.put("taskName", "全部");
				returnList.add(0, allMap);
			}
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getTaskNameComboValuePrivilege(
			Map<String, String> paramMap, Integer userId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			List<Map> userGrps = performanceManagerMapper.getUserGroupByUserId(
					userId, REPORT_DEFINE);
			paramMap.put("allNxTaskType", NX_ALL_TASKTYPE.toString());
			returnList = nxReportManagerMapper.getTaskNameComboValuePrivilege(
					paramMap, REPORT_DEFINE, userGrps, userId);
			if ("1".equals(paramMap.get("needAll"))) {
				Map<String, String> allMap = new HashMap<String, String>();
				allMap.put("taskId", "0");
				allMap.put("taskName", "全部");
				returnList.add(0, allMap);
			}
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public Map<String, Object> searchReportTask(Map<String, String> paramMap,
			int start, int limit) throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			paramMap.put("allNxTaskType", NX_ALL_TASKTYPE.toString());
			returnList = nxReportManagerMapper.searchReportTask(paramMap,
					start, limit);
			int count = nxReportManagerMapper.searchReportTaskCount(paramMap);
			returnData.put("total", count);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> initReportTaskInfo(Map<String, String> paramMap)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map<String, Object>> taskInfo = new ArrayList<Map<String, Object>>();
		List<Map> taskNodes = new ArrayList<Map>();
		try {
			taskInfo = nxReportManagerMapper.searchTaskInfoForEdit(paramMap);
			taskNodes = performanceManagerMapper
					.searchTaskNodesForEdit(paramMap);
			// -------------
			returnData.put("taskInfo", taskInfo);
			returnData.put("taskNodes", taskNodes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@Override
	public Map<String, Object> searchWaveDirById(List<Map> nodeList)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			List<Map> dirList = nxReportManagerMapper
					.searchWaveDirById(nodeList);
			returnData.put("rows", dirList);
			returnData.put("total", dirList.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateReportTask(List<Map> targetList,
			Map<String, String> paramMap,List<Integer> intList) throws CommonException {
		try {
			Map<String, Long> idMap = new HashMap<String, Long>();
			if(paramMap.get("WdmPm")!=null){
				List<String> pmStdIndex = performanceManagerMapper.getPmStdIndexes(
						paramMap.get("WdmPm").toString(), false);
				String wdmPm = pmStdIndex.toString();
				paramMap.put("WdmPm", wdmPm.substring(1, wdmPm.length() - 1));
			}
			// 保存任务主要信息到t_sys_task表
			performanceManagerMapper.updateNESysTask(paramMap);
			// 保存其他一些信息到param表中
			nxReportManagerMapper.updateReportTaskParam(paramMap);
			// 更新之前删除节点信息
			performanceManagerMapper.deleteNodesForUpdate(paramMap);
			// 保存任务节点信息到t_sys_task_info
			idMap.put("newId", Long.valueOf(paramMap.get("taskId")));
			nxReportManagerMapper.saveReportSysTaskInfo(targetList, idMap);
			if(intList!=null&&intList.size()>0){
				nxReportManagerMapper.deleteUnitManageByManageId(intList);
			}
			int taskType = Integer
					.parseInt(paramMap.get("taskType").toString());
			int taskId = Integer.parseInt(idMap.get("newId").toString());
			String[] time = paramMap.get("hour").split(":");
			performanceManagerService.addOrEditReportQuartzTask(
					idMap.get("newId").intValue(), Integer.parseInt(time[0]),
					Integer.parseInt(time[1]),
					Integer.parseInt(paramMap.get("period")), taskType,
					Integer.parseInt(paramMap.get("delay")), NxReportJob.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	@Deprecated
	public void saveReportTaskAMP(List<ResourceUnitManager> manages,
			Map<String, String> paramMap, Integer sysUserId) {
		// TODO Auto-generated method stub
		List<Map> umList = new ArrayList();
		try {
			umList = insertManageWithUnitReturnIds(manages);
			for (Map um : umList) {
				um.put("targetType", CommonDefine.NX_REPORT.UNIT_TYPE.AMP);
			}
			saveReportTask(umList, paramMap, sysUserId);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getNodeInfo(List<Map> nodeList)
			throws CommonException {
		Map<String, String> conditionMap = performanceManagerService
				.nodeListClassify(nodeList);
		Iterator it = conditionMap.keySet().iterator();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			while (it.hasNext()) {
				String level = (String) it.next();
				if ("NODE_EMS".equals(level)) {
					List<Map<String, String>> emsNodeInfo = nxReportManagerMapper
							.getEmsNodeInfo(conditionMap);
					resultList.addAll(emsNodeInfo);
				} else if ("NODE_SUBNET".equals(level)) {
					List<Map<String, String>> subnetNodeInfo = nxReportManagerMapper
							.getSubnetNodeInfo(conditionMap);
					resultList.addAll(subnetNodeInfo);

				} else if ("NODE_NE".equals(level)) {
					List<Map<String, String>> neNodeInfo = nxReportManagerMapper
							.getNeNodeInfo(conditionMap);
					resultList.addAll(neNodeInfo);
				}
			}
			returnData.put("returnResult", CommonDefine.SUCCESS);
			returnData.put("info", resultList);
		} catch (Exception e) {
			returnData.put("returnResult", CommonDefine.FAILED);
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}
	

	@Override
	public void deleteReportTask(int taskId, int reportType) throws CommonException {
		// TODO Auto-generated method stub
		try{
			Map<String,String> map = new HashMap<String, String>();
			map.put("taskId", String.valueOf(taskId));
			map.put("taskType", String.valueOf(reportType));
			// 包含了对QUARTZ任务的删除
			performanceManagerService.deleteReportTask(map);
			//处理后续删除
			if(reportType!=CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH){
				List<Integer> targetList = nxReportManagerMapper.getTaskTargetIdList(taskId);
				nxReportManagerMapper.deleteUnitManageByManageId(targetList);
			}
			List<Integer> keyList = new ArrayList<Integer>();
			keyList.add(taskId);
			nxReportManagerMapper.deleteRecordByKey("t_sys_task_info","SYS_TASK_ID",keyList);
			nxReportManagerMapper.deleteRecordByKey("t_pm_report_task_param","SYS_TASK_ID",keyList);
		}catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}
	
	/**
	 * 获取上个月的今天，如果没有则返回null
	 * @param date
	 * @return
	 */
	private String getDateInLastMonth(String day){
		
		String result = null;
		SimpleDateFormat sf = new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
		try {
			Calendar cld = Calendar.getInstance();
			cld.setTime(sf.parse(day));
			cld.add(Calendar.MONTH, -1);
			result = sf.format(cld.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getCreatorComboValuePrivilege(Integer userId)
			throws CommonException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map> returnList = new ArrayList<Map>();
		try {
			List<Map> userGrps = null;
			if(userId != -1)
			userGrps = performanceManagerMapper.getUserGroupByUserId(
					userId, REPORT_DEFINE);

			returnList = nxReportManagerMapper
					.getCreatorComboValuePrivilege(userGrps, REPORT_DEFINE);
			Map<String, String> allMap = new HashMap<String, String>();
			allMap.put("userId", "0");
			allMap.put("userName", "全部");
			returnList.add(0, allMap);
			returnData.put("total", returnList.size());
			returnData.put("rows", returnList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return returnData;
	}
	
	@Override
	public List<Map> getLinkByAEnd(List<Integer> intList) throws CommonException{
		// TODO Auto-generated method stub
		try { 
			 List<Map> links = nxReportManagerMapper.getLinkByAEnd(intList);
			 return links;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}
	
	
	@Override
	public int savePtnSys(List<Integer> intList, Map<String, String> paramMap,
			Integer sysUserId) throws CommonException {
		int resultCode = 1;
		try {
			int nameCount = nxReportManagerMapper.isPtnSysNameExist(paramMap);
			if (nameCount > 0)
				return 0;
			Map idMap = new HashMap();
			nxReportManagerMapper.savePtnSys(paramMap, idMap);
			nxReportManagerMapper.savePtnSysPorts(intList,idMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return resultCode;
	}


	@Override
	public Map<String, Object> getPtnSysList(Map<String, String> paramMap,
			List<Integer> intList, int start, int limit) throws CommonException {
		try{
		Map<String, Object> rv = new HashMap<String, Object>();
		List<Map> sysList = nxReportManagerMapper.getPtnSysList(paramMap,
				intList, start, limit);
		int count = nxReportManagerMapper.getPtnSysListCount(paramMap);
		rv.put("rows", sysList);
		rv.put("total", count);
		return rv;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
	}
	
	
	@Override
	public List<Map> getLinksBySysId(Map<String, String> paramMap)
			throws CommonException {
		try { 
			 List<Map> links = nxReportManagerMapper.getLinksBySysId(paramMap);
			 return links;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}
	
	
	@Override
	public void delPtnSys(Map<String, String> paramMap) throws CommonException {
		try {
			nxReportManagerMapper.deletePtnSys(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		
	}

	
	@Override
	public String getReport_PTN_FlowPeak(List<Integer> targetIds,
			Map<String, String> condMap, int genType,
			boolean isPreview, Integer sysUserId) throws CommonException {
		try {
			
			String date = condMap.get("start");
			if (isPreview) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Calendar c = Calendar.getInstance();
				date =  sdf.format(c.getTime());
			}
			List<Map> dataList = new ArrayList<Map>();
			List<Map> deletion = new ArrayList<Map>();
//			date = "2016-03-21";
			//***********
				
				dataList = nxReportManagerMapper.getSysDataForPreview(targetIds);
				for(Map m : dataList){
					// 去除非集中型
					if(m.get("SYS_TYPE").toString().equals(String.valueOf(CommonDefine.NX_REPORT.SYS_TYPE.DECENTRALIZED_RING))){
						if(m.get("A_END_PTP").toString().equals(m.get("Z_END_PTP_L").toString())){
							deletion.add(m);
						}
					}
					m.put("SYS_TYPE_DISPLAY", CommonDefine.NX_SYS_TYPE.get(Integer.valueOf(m.get("SYS_TYPE").toString())));
				}
				dataList.removeAll(deletion);
			//**********
			if (!isPreview){
				List<Map> realDataList = nxReportManagerMapper.getDataForPTN_FlowPeak(targetIds,date);
				int i=0;
				for(Map m : dataList){
					if(i==realDataList.size())
						break;
					if(m.get("A_END_PTP").equals(realDataList.get(i).get("A_END_PTP"))||m.get("A_END_PTP").equals(realDataList.get(i).get("Z_END_PTP"))){
						m.putAll(realDataList.get(i));
						i++;
					}
				}
			}
			String fileName = "";
			String taskType = CommonDefine.NX_REPORT_TYPE.get(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK);
			if (isPreview)
				fileName = CommonDefine.PATH_ROOT
						+ CommonDefine.EXCEL.NX_REPORT_DIR + "\\" + "preview"
						+ TimeUtil.parseDate2String(new Date(),
								CommonDefine.REPORT_CN_FORMAT_24H) + ".xlsx";
			else
				fileName = createFileName(condMap, genType, taskType);

			MultiHearderExcelUtil xls = new MultiHearderExcelUtil(fileName);
			xls.setReplaceEmpty("-");
			List<List<MultiColumnMap>> header = get_PTN_FlowPeak_header();
			xls.setHeader(header);
			xls.writeSheet(taskType,dataList, false);
			xls.close();
			Map<String, Object> exportInfo = xls.getResultMap();
			String filePath = exportInfo.get("EXCEL_URL").toString();
			if (!isPreview) {
				exportInfo.put("SYS_TASK_ID", condMap.get("taskId"));
				exportInfo.put("TASK_TYPE",
						CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK);
				exportInfo.put("PERIOD", condMap.get("period"));// 0是日报,1是月报
				exportInfo.put("DATA_SRC", condMap.get("dataSrc"));
				exportInfo.put("CREATOR", sysUserId);
				exportInfo.put("NORMAL_CSV_PATH", "");
				exportInfo.put("ABNORMAL_CSV_PATH", "");
				exportInfo.put("PRIVILEGE", condMap.get("privilege"));
				performanceManagerMapper.savePmExportInfo(exportInfo,
						new HashMap());
			} else {
				String outputPath = getMD5FileName(filePath);
				File f = new File(outputPath);
				if (!f.exists()) {
					Conveter.e2h(filePath, outputPath);
				}
				int pos = outputPath.indexOf("webapps");
				outputPath = outputPath.substring(pos + 8).replace("\\", "/");
				filePath = outputPath;
			}
			// ExportResult result = xls.getResult();
			return filePath.replace('\\', '/');
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		
	}

	private List<List<MultiColumnMap>> get_PTN_FlowPeak_header() {
		List<List<MultiColumnMap>> multiHearder = new ArrayList<List<MultiColumnMap>>();
		// title PTN/IPRAN端口作业计划
		List<MultiColumnMap> title = new ArrayList<MultiColumnMap>();
		title.add(new MultiColumnMap("title", CommonDefine.NX_REPORT_TYPE
				.get(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK), 13, 1));
		multiHearder.add(title);
		// COLUMNS
	    List<MultiColumnMap> cols = new ArrayList<MultiColumnMap>();
	    MultiColumnMap sysName = new MultiColumnMap("SYS_NAME","系统名称",1,1,30,true);
	    cols.add(sysName.addRetinue(1).addRetinue(2).addRetinue(3).addRetinue(4));
	    cols.add(new MultiColumnMap("SYS_CAPACITY","系统容量(G)",1,1));
	    cols.add(new MultiColumnMap("SYS_TYPE_DISPLAY","系统类型",1,1,21));
	    cols.add(new MultiColumnMap("CAPACITY_TOP","系统峰值流量(G)",1,1));
	    cols.add(new MultiColumnMap("CAPACITY_TOP_RATE","系统占用率(%)",1,1));
	    cols.add(new MultiColumnMap("A_NE_DISPLAY_NAME","中继端A端网元",1,1,45));
	    cols.add(new MultiColumnMap("A_PTP_DISPLAY_NAME","中继端A端端口名称",1,1,45));
	    cols.add(new MultiColumnMap("Z_NE_DISPLAY_NAME","中继端Z端网元",1,1,45));
	    cols.add(new MultiColumnMap("Z_PTP_DISPLAY_NAME","中继端Z端端口名称",1,1,45));
	    cols.add(new MultiColumnMap("RECV_SEND_FLAG","方向",1,1));
	    cols.add(new MultiColumnMap("CAPACITY_TOP_PORT","峰值",1,1));
	    cols.add(new MultiColumnMap("MONITORED_TIME","峰值时间点",1,1,12));
	    cols.add(new MultiColumnMap("MONITORED_DATE","日期",1,1,12));
	    multiHearder.add(cols);
	    return multiHearder;
	}
	
	// --------------------------WSS-----------------------------
	@Override
	public String getReport_PTN_IPRAN(List<Integer> targetList,
			Map<String, String> condMap, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException {
		try {
			// TODO Auto-generated method stub
			Map<String, List> targetMap = groupPtpByEms(targetList);
			Iterator<String> it = targetMap.keySet().iterator();
			List<Map<String, String>> tableList = new ArrayList<Map<String, String>>();
			String date = condMap.get("start");
			if (isPreview) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						CommonDefine.COMMON_SIMPLE_FORMAT);
				Calendar c = Calendar.getInstance();
				date =  sdf.format(c.getTime());
			}
//			 date = "2015-04-01";
			 
				while (it.hasNext()) {
					String emsId = it.next();
					List ptpList = targetMap.get(emsId);
					StringBuffer sb = new StringBuffer();
					for (Object obj : ptpList) {
						sb.append(obj.toString()).append(",");
					}
					sb.deleteCharAt(sb.lastIndexOf(","));
					String tableName = "t_pm_origi_data_"
							+ emsId
							+ "_"
							+ date.substring(0, date.length() - 3).replace('-',
									'_');
					
					Integer existance = performanceManagerMapper
							.getPmTableExistance(tableName,
									SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
					if (existance != null && existance == 1) {
						// 是否需要判断表存在？
						Map<String, String> tableThing = new HashMap<String, String>();
						tableThing.put("tableName", tableName);
						tableThing.put("tableNodes", sb.toString());
						tableList.add(tableThing);
					}
					/*
					 * Integer existance = performanceManagerMapper
					 * .getPmTableExistance(tableName,
					 * SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
					 */
					
				}
			String pmStdIndex = "'TPL_CUR','RPL_CUR','RCV_EFC','RCV_FC','RCV_CEFC','RCV_DFC'";
			List<Map> pmDataList = new ArrayList<Map>();
			// 无性能表时就不去查找性能，生成空表
			if (!isPreview&&tableList.size()>0) {
				pmDataList = nxReportManagerMapper.getPmDataForPTN_IPRAN(
						tableList, date, pmStdIndex);
			}
			List<Map> optStdDataList = nxReportManagerMapper
					.getOptStdDataForPTN_IPRAN(targetList);
			List<Map> dataList = processDataForPTN_IPRAN(pmDataList,
					optStdDataList, date);
			String fileName = "";
			if (isPreview)
				fileName = CommonDefine.PATH_ROOT
						+ CommonDefine.EXCEL.NX_REPORT_DIR + "\\" + "preview"
						+ TimeUtil.parseDate2String(new Date(),
								CommonDefine.REPORT_CN_FORMAT_24H) + ".xlsx";
			else
				fileName = createFileName(condMap, genType, "PTN/IPRAN端口作业计划");

			MultiHearderExcelUtil xls = new MultiHearderExcelUtil(fileName);
			xls.setReplaceEmpty("-");
			List<List<MultiColumnMap>> header = get_PTN_IPRAN_header();
			xls.setHeader(header);
			xls.writeSheet("PTN端口作业计划", dataList, false);
			xls.close();
			Map<String, Object> exportInfo = xls.getResultMap();
			String filePath = exportInfo.get("EXCEL_URL").toString();
			if (!isPreview) {
				exportInfo.put("SYS_TASK_ID", condMap.get("taskId"));
				exportInfo.put("TASK_TYPE",
						CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_IPRAN);
				exportInfo.put("PERIOD", condMap.get("period"));// 0是日报,1是月报
				exportInfo.put("DATA_SRC", condMap.get("dataSrc"));
				exportInfo.put("CREATOR", sysUserId);
				exportInfo.put("NORMAL_CSV_PATH", "");
				exportInfo.put("ABNORMAL_CSV_PATH", "");
				exportInfo.put("PRIVILEGE", condMap.get("privilege"));
				performanceManagerMapper.savePmExportInfo(exportInfo,
						new HashMap());
			} else {
				String outputPath = getMD5FileName(filePath);
				File f = new File(outputPath);
				if (!f.exists()) {
					Conveter.e2h(filePath, outputPath);
				}
				int pos = outputPath.indexOf("webapps");
				outputPath = outputPath.substring(pos + 8).replace("\\", "/");
				filePath = outputPath;
			}
			// ExportResult result = xls.getResult();
			return filePath.replace('\\', '/');
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}
	
	
	private List<List<MultiColumnMap>> get_PTN_IPRAN_header() {
		List<List<MultiColumnMap>> multiHearder = new ArrayList<List<MultiColumnMap>>();
		// title PTN/IPRAN端口作业计划
		List<MultiColumnMap> title = new ArrayList<MultiColumnMap>();
		title.add(new MultiColumnMap("title", CommonDefine.NX_REPORT_TYPE.get(CommonDefine.QUARTZ.JOB_NX_REPORT_PTN_IPRAN),10,1));
		multiHearder.add(title);
		// COLUMNS
	    List<MultiColumnMap> cols = new ArrayList<MultiColumnMap>();
	    cols.add(new MultiColumnMap("DISPLAY_SUBNET","网络名称",1,1));
	    MultiColumnMap ne = new MultiColumnMap("DISPLAY_NE","节点名称",1,1,35,true); // 指定width
	    ne.addRetinue(0); // 把网络名称设置为节点名称的随从，随其一起合并。
	    cols.add(ne);
	    cols.add(new MultiColumnMap("PORT_DESC","端口名称",1,1,40)); // 指定width
	    cols.add(new MultiColumnMap("RPL_CUR","接收光功率",1,1));
	    cols.add(new MultiColumnMap("TPL_CUR","发送光功率",1,1));
	    cols.add(new MultiColumnMap("MAX_IN","过载点",1,1));
	    cols.add(new MultiColumnMap("MIN_IN","灵敏度",1,1));
	    cols.add(new MultiColumnMap("ERR_RATE","误码率/丢包率",1,1));
	    cols.add(new MultiColumnMap("CONCLUSION","光功率结论",1,1));
	    cols.add(new MultiColumnMap("DATE","日期",1,1,12));
	    multiHearder.add(cols);
	    return multiHearder;
	}

	// 处理成报表数据行的格式
	private List<Map> processDataForPTN_IPRAN(
			List<Map> pmDataList, List<Map> optStdDataList, String date) {
		// TODO Auto-generated method stub
		int i=0;
		for(Map opt : optStdDataList){
			while(i<pmDataList.size()&&pmDataList.get(i).get("BASE_PTP_ID").toString().equals(opt.get("BASE_PTP_ID").toString())){
				Map pmData = pmDataList.get(i);
				String pmStdIndex = pmData.get("PM_STD_INDEX").toString();
				opt.put(pmStdIndex, pmData.get("PM_VALUE"));
				i++;
			}
			opt.put("DATE", date);
			// If no error frame , we consider it as 0
			if(!opt.containsKey("RCV_EFC")){
				opt.put("RCV_EFC", 0);
			}
			if(!opt.containsKey("RCV_CEFC")){
				opt.put("RCV_CEFC", 0);
			}
			if(!opt.containsKey("RCV_DFC")){
				opt.put("RCV_DFC", 0);
			}
			if(opt.containsKey("RCV_FC")){
				Float efc = Float.parseFloat(opt.get("RCV_EFC").toString());
				Float cefc = Float.parseFloat(opt.get("RCV_CEFC").toString());
				Float dfc = Float.parseFloat(opt.get("RCV_DFC").toString());
				Float fc = Float.parseFloat(opt.get("RCV_FC").toString());
				Float rate = (efc+cefc+dfc)/fc;
				java.text.NumberFormat percentFormat =java.text.NumberFormat.getPercentInstance(); 
				opt.put("ERR_RATE", percentFormat.format(rate));
			}
			//数据不全是不进行判断结论
			if(opt.containsKey("RPL_CUR")&&opt.containsKey("MAX_IN")&&opt.containsKey("MIN_IN")){
				opt.put("CONCLUSION", getConclusionForP(opt.get("RPL_CUR").toString(),opt.get("MAX_IN").toString(),opt.get("MIN_IN").toString()));
			}
		}
		
		return optStdDataList;
	}


	private String getConclusionForP(String rpl, String maxIn,
			String minIn) {
		// TODO Auto-generated method stub
		Float d = 2f;
		Float RPL = Float.parseFloat(rpl);
		String conclusion = "";
		if(RPL>Float.parseFloat(maxIn))
			conclusion = "过载";
		else if (RPL > Float.parseFloat(maxIn)-2f)
			conclusion = "临近过载点";
		else if (RPL < Float.parseFloat(minIn))
			conclusion = "低于灵敏度";
		else if (RPL < Float.parseFloat(minIn)+2f)
			conclusion = "低于灵敏度";
		else
			conclusion = "正常";
		return conclusion;
	}

	private Map<String, List> groupPtpByEms(List<Integer> targetList){
		List<Map> PtpIdsWithEmsId = nxReportManagerMapper.getPtpIdsWithEmsId(targetList);
		return performanceManagerService.key1ByKey2(PtpIdsWithEmsId, "ptpId", "emdId", 3);
	}
	
	
	
	

	@Override
	public String getReport_WaveTransOUT(List<Map> targetList,
			Map<String, String> searchCond, Integer sysUserId, int genType)
			throws CommonException {
		Date start, end;
		String fileName = "";
		searchCond.put("genType", genType + "");
		try {
			if (genType == CommonDefine.REPORT.REPORT_INSTANT) {
				List<String> pmStdIndex = performanceManagerMapper
						.getPmStdIndexes(searchCond.get("wdmPm").toString(),
								true);
				String wdmPm = pmStdIndex.toString();
				searchCond.put("wdmPm", wdmPm.substring(1, wdmPm.length() - 1));
			}
			searchCond
					.put("wdmPm", processCommaString(searchCond.get("wdmPm")));
			searchCond
					.put("wdmTp", processCommaString(searchCond.get("wdmTp")));

			String[] format = { CommonDefine.COMMON_SIMPLE_FORMAT,
					CommonDefine.GROUP_FORMAT };
			int period = Integer.parseInt(searchCond.get("period"));
			// 省网四期干线（2013年11月1号-2013年12月5日-原始数据）_即时报表_2013年12月28日
			start = TimeUtil.parseString2Date(searchCond.get("start"),
					format[period]);
			// System.out.println("Start = " +
			// TimeUtil.parseDate2String(start));
			end = TimeUtil.parseString2Date(searchCond.get("end"),
					format[period]);
			// System.out.println("End = " + TimeUtil.parseDate2String(end));
			int dataSrc = Integer.parseInt(searchCond.get("dataSrc"));
			String[] rptFormat = { CommonDefine.REPORT_CN_FORMAT,
					CommonDefine.REPORT_CN_FORMAT_MONTH };
			String range = TimeUtil.parseDate2String(start, rptFormat[period])
					+ "-" + TimeUtil.parseDate2String(end, rptFormat[period]);
			String[] dataType = { "原始数据", "异常数据" };
			String[] genTypeStr = { "计划任务", "即时报表" };
			fileName = searchCond.get("taskName")
					+ "（"
					+ range
					+ "-"
					+ dataType[dataSrc]
					+ "）_"
					+ genTypeStr[genType]
					+ "_"
					+ TimeUtil.parseDate2String(new Date(),
							CommonDefine.REPORT_CN_FORMAT_24H) + ".xlsx";
		} catch (ParseException e) {
			e.printStackTrace();
			fileName = "波长转换报表"
					+ TimeUtil.parseDate2String(new Date(),
							CommonDefine.REPORT_CN_FORMAT_24H) + ".xlsx";
		}
		searchCond.put("REPORT_NAME", fileName);
		Map<String, Object> exportInfo = getReport_WaveTransOUT_Stub(
				targetList, searchCond, false);
		// NxReportExcelUtil.getInstance("d:\\a.xlsx").writeData(dat);
		// 导出结果信息入库-> t_pm_report_info
		System.out.println(exportInfo.toString());
		exportInfo.put("SYS_TASK_ID", searchCond.get("taskId"));
		exportInfo.put("TASK_TYPE",
				CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH);
		exportInfo.put("PERIOD", searchCond.get("period"));// 0是日报,1是月报
		exportInfo.put("DATA_SRC", searchCond.get("dataSrc"));
		exportInfo.put("CREATOR", sysUserId);
		exportInfo.put("NORMAL_CSV_PATH", "");
		exportInfo.put("ABNORMAL_CSV_PATH", "");
		exportInfo.put("PRIVILEGE", searchCond.get("privilege"));
		Map idMap = new HashMap();
		performanceManagerMapper.savePmExportInfo(exportInfo, idMap);
		return exportInfo.get("EXCEL_URL").toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> getReport_WaveTransOUT_Stub(
			List<Map> targetList, Map<String, String> searchCond,
			boolean isPreview) throws CommonException {
		List<Integer> idList = null;

		// 时间归一化
		processTime(searchCond);
		String fileName = searchCond.get("REPORT_NAME").toString();
		String fn = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.NX_REPORT_DIR
				+ "\\" + fileName;
		WavelengthTransformationExcel xls = WavelengthTransformationExcel
				.getInstance(fn);
		List<Map> waveTransOUTData = new ArrayList<Map>();
		// 根据id列表取得所有方向信息
		for (int i = 0; targetList != null && i < targetList.size(); i++) {
			idList = new ArrayList<Integer>();
			Map map = targetList.get(i);
			idList.add(Integer.valueOf(map.get("targetId").toString()));
			String id = map.get("targetId").toString();
			// System.out.println("Searching - " + map.get("targetId"));
			Map waveDirInfo = nxReportManagerMapper.getWaveDirInfo(id);
			// System.out.println("waveDirInfo = " + waveDirInfo.toString());
			List<Map> ptpInfo = nxReportManagerMapper
					.searchWaveTransOUT_BasePtp(id);
			
			List<String> ptpList = new ArrayList<String>();
			for (Map m : ptpInfo) {
				Object v = m.get("BASE_PTP_ID");
				if (v != null) {
					ptpList.add(v.toString());
				}
				// System.out.println("ptpInfo = " + m.toString());
			}
			List<Map> pms = new ArrayList<Map>();

			List<String> tableNames = getPmTableName(id + "", searchCond);
			
			List<Map> pm;
			searchCond.put("nendRx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG));
			searchCond.put("nendTx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG));
			for (String tableName : tableNames) {
				System.out.println(tableName);
				searchCond.put("TABLE_NAME", tableName);
				System.out.println("searchCond = \n" + searchCond.toString());
				if (!isPreview && pmExists(tableName) && ptpList.size() > 0) {
					pm = nxReportManagerMapper.searchWaveTransOUT_PM(ptpList,
							searchCond);
					// pm = transData(pm);
				} else {
					pm = new ArrayList<Map>();
				}
				pms.addAll(pm);
			}
			// TODO For now just ZTE would do the combination
			if(ptpInfo.size()>0&&ptpInfo.get(0).get("FACTORY").equals(CommonDefine.FACTORY_ZTE_FLAG)){
					List<Map> removeThings = new ArrayList<Map>();
					java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+_(\\d+)\\(*.*\\)*");
						for(Map ptp:ptpInfo){
							String s = ptp.get("PORT_NO").toString();
							java.util.regex.Matcher matcher = pattern.matcher(s);
							if(matcher.matches()){
								ptp.put("PORT_NUMBER",matcher.group(1));
							}
						}
						for(Map ptp:ptpInfo){
							if(ptp.get("DIRECTION").toString().equals(String.valueOf(CommonDefine.NX_REPORT.PTP_DIRECTION.SOURCE))){
								for(Map ptp_sink:ptpInfo){
									if( ptp.get("PTP_TYPE")==ptp_sink.get("PTP_TYPE")
											&& ptp_sink.get("PORT_NUMBER").equals(ptp.get("PORT_NUMBER"))
											&& ptp_sink.get("DIRECTION").toString().equals(String.valueOf(CommonDefine.NX_REPORT.PTP_DIRECTION.SINK))){
										ptp_sink.put("SKIP_FLAG", 1);
										for(Map p : pms){
											if(p.get("BASE_PTP_ID").toString().equals(ptp_sink.get("BASE_PTP_ID").toString())){
												p.put("PAIR_PTP_ID", ptp.get("BASE_PTP_ID"));
											}
										}
										break;
									}
								}
							}
						}
//					ptpInfo.removeAll(removeThings);	
			}
			waveDirInfo.put("ptp", ptpInfo);			
			waveDirInfo.put("pm", pms);
			waveTransOUTData.add(waveDirInfo);
		}
		// System.out.println("waveTransOUTData.size() = " +
		// waveTransOUTData.size());
		xls.writeData(waveTransOUTData, searchCond);
		// 获取导出结果
		Map<String, Object> exportInfo = xls.getResult();
		return exportInfo;
	}

	/**
	 * 处理 "aaaa,bbbb,cccc" -> "''aaaa','bbbb','cccc''" 这样在sql里就可以被正确识别了
	 * 
	 * @param taskInfo
	 * @return
	 */
	String processCommaString(String oriStr) {
		String resultStr = oriStr;
		if (oriStr != null && !oriStr.isEmpty()) {
			resultStr = "'" + oriStr.replaceAll(", ", "','") + "'";
		}
		return resultStr;
	}

	/**
	 * 吧参数中的时间进行归一化 <br>
	 * 　　　　对于日报，直接加后缀 <br>
	 * 　　　　对于月报，start + “ -01 00:00:00” <br>
	 * 　　　　　　　　end + “ -<最后一天> 23:59:59”
	 * 
	 * @param searchCond
	 */
	private void processTime(Map<String, String> searchCond) {
		try {
			String startTime = (String) searchCond.get("start");
			String endTime = (String) searchCond.get("end");
			// System.out.println("Time = <" + startTime + "  ->  " + endTime +
			// ">");
			int period = Integer.parseInt(searchCond.get("period"));
			searchCond.put("retrivalTime", "1");
			
			if (startTime.length() < 8) {
				startTime += "-01";
				endTime += "-01";
				searchCond.put("retrievalTimePmDate", "1");
				Calendar c = Calendar.getInstance();
				c.setTime(TimeUtil.parseString2Date(endTime,
						CommonDefine.COMMON_SIMPLE_FORMAT));
				c.add(Calendar.MONTH, 1);
				c.add(Calendar.DATE, -1);
				endTime = TimeUtil.parseCalendar2String(c,
						CommonDefine.COMMON_SIMPLE_FORMAT);
			}
			// System.out.println("Time = <" + startTime + "  ->  " + endTime +
			// ">");
			startTime += " 00:00:00";
			endTime += " 23:59:59";
			// System.out.println("Time = <" + startTime + "  ->  " + endTime +
			// ">");
			//如果是月报
			if (period == 1) {
			}
			searchCond.put("start", startTime);
			searchCond.put("end", endTime);
			// System.out.println("Time = <" + startTime + "  ->  " + endTime +
			// ">");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 判断PM表是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	private boolean pmExists(String tableName) {
		Integer existance = performanceManagerMapper.getPmTableExistance(
				tableName, SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
		return existance > 0;
	}

	/**
	 * 数据格式转换
	 * 
	 * @param dat
	 * @return
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	private List<Map> transData(List<Map> dat) {
		List<Map> rv = new ArrayList<Map>();
		Map m = null;
		long ptpId = -44944;
		for (Map v : dat) {
			// System.out.println(v.toString());
			long tmpPtpId = Long.valueOf(v.get("BASE_PTP_ID").toString());
			System.out.println("tmpPtpId = " + tmpPtpId + "\tPM index = "
					+ v.get("PM_STD_INDEX").toString());
			// m.put(v.get("PM_STD_INDEX").toString(), v.get("PM_VALUE")
			// .toString());
		}
		for (Map v : rv) {
			// System.out.println(v.toString());
		}
		return rv;
	}

	/**
	 * 生成PM表名称
	 * 
	 * @param id
	 *            网管ID
	 * @param time
	 *            采集时间
	 * @return
	 */
	private List<String> getPmTableName(String id,
			Map<String, String> searchCond) {
		// 获取基本信息
		String startTime = (String) searchCond.get("start");
		String endTime = (String) searchCond.get("end");
		// int period = Integer.parseInt(searchCond.get("period"));

		List<String> rv = new ArrayList<String>();
		List<Map> ptpInfo = nxReportManagerMapper.selectEq("t_base_unit",
				"RESOURCE_WAVE_DIR_ID", id);
		String emsId = ptpInfo.get(0).get("BASE_EMS_CONNECTION_ID").toString();
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		try {
			start.setTime(TimeUtil.parseString2Date(startTime,
					CommonDefine.COMMON_FORMAT));
			end.setTime(TimeUtil.parseString2Date(endTime,
					CommonDefine.COMMON_FORMAT));
			while (start.before(end)) {
				int month = (start.get(Calendar.MONTH) + 1);
				String tableName = "t_pm_origi_data_" + emsId + "_"
						+ start.get(Calendar.YEAR) + "_"
						+ (month > 9 ? month : "0" + month);
				rv.add(tableName);
				start.add(Calendar.MONTH, 1);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rv;
	}

	@Override
	public String getReportPreview_WaveTransOUT(List<Map> targetList,
			Map<String, String> searchCond) throws CommonException {
		// searchCond = new HashMap<String, Object>();
		// searchCond.put("targetList", targetList);
		// searchCond.put("start", "2014-06-03 00:00:00");
		// searchCond.put("end", "2014-06-03 23:59:59");
		Map<String, Object> dat = getReport_WaveTransOUT_Stub(targetList,
				searchCond, true);
		String xlsPath = dat.get("REPORT_NAME").toString();
		String outputPath = getMD5FileName(xlsPath);
		File f = new File(outputPath);
		if (!f.exists()) {
			Conveter.e2h(xlsPath, outputPath);
		}
		int pos = outputPath.indexOf("webapps");
		outputPath = outputPath.substring(pos + 8).replace("\\", "/");
		return outputPath;
	}

	private String getMD5FileName(String path){
		path = path.replace("/", "\\");
		int p = path.lastIndexOf("\\");
//		System.out.println("path="+path.substring(0,p+1));
		String rv = path.substring(0,p+1);
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = path.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            String fn = new String(str);
            rv += fn+ ".html";
            return rv;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	@Override
	public String getExcelPreview(Map<String, String> paramMap)
			throws CommonException {
		String xlsPath = paramMap.get("EXCEL_URL");
//		System.out.println("xlsPath = " + xlsPath);
		String outputPath = getMD5FileName(xlsPath);
//		System.out.println("outputPath = " + outputPath);
		try {
			File f = new File(outputPath);
			if (!f.exists()) {
				// 如果电脑装的是wps，则使用下面的一句；
				// Conveter.setTool("wps");
				Conveter.e2h(xlsPath, outputPath);
			}
			// System.out.println(outputPath);
			int pos = outputPath.indexOf("webapps");
			// System.out.println(outputPath.substring(pos+8));
			outputPath = outputPath.substring(pos + 8).replace("\\", "/");
		} catch (Exception e) {
			throw new CommonException(e, 1);
		}
		return outputPath;
	}

	@Override
	public Result deleteUnitReportByManageId(int manageId)
			throws CommonException {
		List<Integer> list = new ArrayList<Integer>();
		list.add(manageId);
		return deleteUnitReportByManageId(list);
	}

	@Override
	public Result deleteUnitReport(List<ResourceUnitManager> res)
			throws CommonException {
		Result r = new CommonResult();
		if (res == null) {
			r.setReturnMessage("参数为空");
			r.setReturnResult(CommonDefine.FALSE);
		} else {
			List<Integer> ids = new ArrayList<Integer>();
			for (int i = 0; i < res.size(); i++) {
				ids.add(res.get(i).getRESOURCE_UNIT_MANAGE_ID());
			}
			r = deleteUnitReportByManageId(ids);
		}
		return r;
	}

	@Override
	public Result deleteUnitReportByManageId(List<Integer> manageIds)
			throws CommonException {
		Result r = new CommonResult();
		try {
			// 判断参数是否合法
			if (manageIds == null) {
				r.setReturnMessage("参数为空");
				r.setReturnResult(CommonDefine.FALSE);
			} else if (manageIds.size() < 1) {
				r.setReturnMessage("manageId值不存在");
				r.setReturnResult(CommonDefine.FALSE);
			}
			// 删除manage信息
			nxReportManagerMapper.deleteUnitManageByManageId(manageIds);
			r.setReturnMessage("success");
			r.setReturnResult(CommonDefine.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return r;
	}

	@Override
	public Result deleteUnitInfo(int manageId, int unitId)
			throws CommonException {
		Result r = new CommonResult();
		try {
			nxReportManagerMapper.deleteUnitInfo(manageId, unitId);
			r.setReturnMessage("success");
			r.setReturnResult(CommonDefine.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return r;
	}

	@Override
	public Result updateManageInfo(ResourceUnitManager manage) {
		nxReportManagerMapper.updateManageInfo(manage);
		return upDateUnitInfo(manage.getRESOURCE_UNIT_MANAGE_ID(),
				manage.getTYPE(), manage.getUnits());
	}

	public Result upDateUnitInfo(int manageId, int type,
			List<ResourceUnitManageRelUnit> units) {
		Result r = new CommonResult();
		ResourceUnitManager ru = nxReportManagerMapper
				.getUnitInfoByUnitManageId(manageId, type, true);
		List<ResourceUnitManageRelUnit> unitList = ru.getUnits();
		if (unitList != null) {
			// 删除原有记录
			for (int i = 0; i < unitList.size(); i++) {
				nxReportManagerMapper.deleteUnitInfo(unitList.get(i)
						.getRESOURCE_UNIT_MANAGE_ID(), unitList.get(i)
						.getBASE_UNIT_ID());
			}
		}
		// 插入所有记录
		if (units != null) {
			for (int i = 0; i < units.size(); i++) {
				units.get(i).setRESOURCE_UNIT_MANAGE_ID(manageId);
				nxReportManagerMapper.insertUnitInfo(units.get(i));
			}
		}
		r.setReturnMessage("success");
		r.setReturnResult(CommonDefine.SUCCESS);
		return r;
	}

	@Override
	public Result insertManageWithUnit(List<ResourceUnitManager> manageList)
			throws CommonException {
		Result r = new CommonResult();
		if (null != manageList) {
			for (int i = 0; i < manageList.size(); i++) {
				nxReportManagerMapper.insertManageInfo(manageList.get(i));
				if (null != manageList.get(i).getUnits()) {
					int manageId = manageList.get(i)
							.getRESOURCE_UNIT_MANAGE_ID();
					for (int j = 0; j < manageList.get(i).getUnits().size(); j++) {
						ResourceUnitManageRelUnit unit = manageList.get(i)
								.getUnits().get(j);
						// 将RESOURCE_UNIT_MANAGE_ID的值赋值给unit对象
						unit.setRESOURCE_UNIT_MANAGE_ID(manageId);
						nxReportManagerMapper.insertUnitInfo(unit);
					}
				}
			}
			r.setReturnResult(CommonDefine.SUCCESS);
			r.setReturnMessage(new Integer(manageList.get(0)
					.getRESOURCE_UNIT_MANAGE_ID()).toString());
		} else {
			r.setReturnMessage("插入失败");
		}
		return r;
	}

	@SuppressWarnings("rawtypes")
	@Override
	@Deprecated
	public List<Map> insertManageWithUnitReturnIds(
			List<ResourceUnitManager> manageList) throws CommonException {
		List<Map> unitManages = new ArrayList();
		for (int i = 0; i < manageList.size(); i++) {
			nxReportManagerMapper.insertManageInfo(manageList.get(i));
			if (null != manageList.get(i).getUnits()) {
				int manageId = manageList.get(i).getRESOURCE_UNIT_MANAGE_ID();
				for (int j = 0; j < manageList.get(i).getUnits().size(); j++) {
					ResourceUnitManageRelUnit unit = manageList.get(i)
							.getUnits().get(j);
					// 将RESOURCE_UNIT_MANAGE_ID的值赋值给unit对象
					unit.setRESOURCE_UNIT_MANAGE_ID(manageId);
					nxReportManagerMapper.insertUnitInfo(unit);
				}
				Map<String, Integer> um = new HashMap<String, Integer>();
				um.put("targetId", manageId);
				unitManages.add(um);
			}
		}
		return unitManages;
	}

	@Override
	public Map<String, Object> getUnitInfoByManageId(int manageId,
			int reportType) throws CommonException {
		ResourceUnitManager manage = nxReportManagerMapper
				.getUnitInfoByUnitManageId(manageId, reportType, true);
		Map<String, Object> map = new HashMap<String, Object>();
		if (manage != null) {
			List<ResourceUnitManageRelUnit> list = manage.getUnits();
			if (list != null) {
				map.put("total", list.size());
				map.put("rows", list);
			} else {
				map.put("total", 0);
				map.put("rows", new ArrayList<Object>());
			}
		} else {
			map.put("total", 0);
			map.put("rows", new ArrayList<Object>());
		}
		return map;
	}

	@Override
	public Map<String, Object> getManageInfoByTaskId(int taskId, int reportType)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ResourceUnitManager> list = nxReportManagerMapper
				.getManageInfoByTaskId(taskId, reportType, false);
		if (list != null) {
			map.put("total", list.size());
			map.put("rows", list);
		} else {
			map.put("total", 0);
			map.put("rows", new ArrayList<Object>());
		}
		return map;
	}

	@Override
	public Map<String, Object> getPortByUnitId(int unitId)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map> list = nxReportManagerMapper.getPortByUnitId(unitId);
		if (list != null) {
			map.put("total", list.size());
			map.put("rows", list);
		} else {
			map.put("total", 0);
			map.put("rows", new ArrayList<Object>());
		}
		return map;
	}

	@Override
	public Map<String, Object> getProductNameByFactoryIdNoSDH(int factoryId)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map> list = nxReportManagerMapper
				.getProductNameByFactoryIdNoSDH(factoryId);
		if (list != null) {
			map.put("total", list.size());
			map.put("rows", list);
		} else {
			map.put("total", 0);
			map.put("rows", new ArrayList<Object>());
		}
		return map;
	}

	@Override
	public Map<String, Object> getNodeInfoByUnitId(List<Integer> unitIds)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map> list = nxReportManagerMapper.getNodeInfoByUnitId(unitIds);
			if (list != null) {
				map.put("total", list.size());
				map.put("rows", list);
			} else {
				map.put("total", 0);
				map.put("rows", new ArrayList<Object>());
			}
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return map;
	}

	// --------------------------MeiK START-----------------------------

	@SuppressWarnings("rawtypes")
	public String getAmplifierDataForReport(List<Integer> targetIds,
			Map<String, String> searchCond, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException {

		String filePath = "";

		try {
			Map<String, Object> data = new HashMap<String, Object>();
			
			if(isPreview) {
				addPreviewDate(searchCond);
			}
			OptAmpExcel excel = new OptAmpExcel(createFileName(searchCond,
					genType, "光放大器报表"));

			// 时间归一化
			processTime(searchCond);
			int period = Integer.parseInt(searchCond.get("period"));
			if (period == CommonDefine.PM.PM_REPORT.PERIOD.MONTHLY
					&& genType == CommonDefine.REPORT.REPORT_SCHEDULE) {
				searchCond.put("retrievalTimePmDate", null);
			}
			
			//增加查询条件
			searchCond.put("nendRx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG));
			searchCond.put("nendTx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG));

			List<Map<String, Object>> neRows = new ArrayList<Map<String, Object>>();

			for (Integer i : targetIds) {
				Map<String, Object> baseInfo = nxReportManagerMapper
						.getBaseInfoForAmp(i);
				if (baseInfo != null) {
					List<Map<String, Object>> unitRows = nxReportManagerMapper
							.getUnitInfoForAmp(i);
					if (unitRows != null && unitRows.size() > 0) {
						baseInfo.put("unitRows", unitRows);

						if (baseInfo.get("EMS_ID") != null) {
							int emsId = Integer.parseInt(baseInfo.get("EMS_ID")
									.toString());
							List<String> tableNames = getPmTableName_common(emsId,
									searchCond);

							List<Integer> tPtpIds = new ArrayList<Integer>();
							List<Integer> rPtpIds = new ArrayList<Integer>();
							for (Map<String, Object> u : unitRows) {
								if (u.get("T_PTP_ID") != null) {
									tPtpIds.add((Integer) u.get("T_PTP_ID"));
								}
								if (u.get("R_PTP_ID") != null) {
									rPtpIds.add((Integer) u.get("R_PTP_ID"));
								}
							}

							List<Map<String, Object>> tpms = new ArrayList<Map<String, Object>>();
							List<Map<String, Object>> rpms = new ArrayList<Map<String, Object>>();
							for (String tableName : tableNames) {
								searchCond.put("TABLE_NAME", tableName);
								List<Map<String, Object>> tpm = null;
								List<Map<String, Object>> rpm = null;
								if (pmExists(tableName)) {
									if (tPtpIds.size() > 0)
										tpm = nxReportManagerMapper
												.getTranPMDataForAMP(tPtpIds,
														searchCond);
									if (rPtpIds.size() > 0)
										rpm = nxReportManagerMapper
												.getRecvPMDataForAMP(rPtpIds,
														searchCond);
								}
								if (tpm != null && tpm.size() > 0)
									tpms.addAll(tpm);
								if (rpm != null && rpm.size() > 0)
									rpms.addAll(rpm);
							}

							arrangePMForAMP(unitRows, tpms, rpms);
						}
					}

					neRows.add(baseInfo);
				}
			}

			data.put("neRows", neRows);
			data.put("searchCond", searchCond);
			data.put("genType", genType);
			excel.writeData(data);
			Map<String, Object> exportInfo = excel.getResult();
			filePath = exportInfo.get("EXCEL_URL").toString();

			exportInfo.put("SYS_TASK_ID", searchCond.get("taskId"));
			exportInfo.put("TASK_TYPE",
					CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_AMP);
			exportInfo.put("PERIOD", searchCond.get("period"));// 0是日报,1是月报
			exportInfo.put("DATA_SRC", searchCond.get("dataSrc"));
			exportInfo.put("CREATOR", sysUserId);
			exportInfo.put("NORMAL_CSV_PATH", "");
			exportInfo.put("ABNORMAL_CSV_PATH", "");
			exportInfo.put("PRIVILEGE", searchCond.get("privilege"));
			performanceManagerMapper
					.savePmExportInfo(exportInfo, new HashMap());

		} catch (Exception e) {
			throw new CommonException(e, 1);
		}
		
		if(isPreview) {
			String outputPath = getMD5FileName(filePath);
			File f = new File(outputPath);
			if (!f.exists()) {
				Conveter.e2h(filePath, outputPath);
			}
			int pos = outputPath.indexOf("webapps");
			outputPath = outputPath.substring(pos + 8).replace("\\", "/");
			filePath = outputPath;
		}

		return filePath;
	}

	public String createFileName(Map<String, String> searchCond, int genType, String exceptionFileName) {

		Date start, end;
		String fileName = "";
		try {

			String[] format = { CommonDefine.COMMON_SIMPLE_FORMAT,
					CommonDefine.GROUP_FORMAT };
			int period = Integer.parseInt(searchCond.get("period"));
			start = TimeUtil.parseString2Date(searchCond.get("start"),
					format[period]);
			end = TimeUtil.parseString2Date(searchCond.get("end"),
					format[period]);
			int dataSrc = Integer.parseInt(searchCond.get("dataSrc"));
			String[] rptFormat = { CommonDefine.REPORT_CN_FORMAT,
					CommonDefine.REPORT_CN_FORMAT_MONTH };
			String range = TimeUtil.parseDate2String(start, rptFormat[period])
					+ "-" + TimeUtil.parseDate2String(end, rptFormat[period]);
			String[] dataType = { "原始数据", "异常数据" };
			String[] genTypeStr = { "计划任务", "即时报表" };
			fileName = searchCond.get("taskName")
					+ "（"
					+ range
					+ "-"
					+ dataType[dataSrc]
					+ "）_"
					+ genTypeStr[genType]
					+ "_"
					+ TimeUtil.parseDate2String(new Date(),
							CommonDefine.REPORT_CN_FORMAT_24H) + ".xlsx";
		} catch (ParseException e) {
			fileName = exceptionFileName
					+ TimeUtil.parseDate2String(new Date(),
							CommonDefine.REPORT_CN_FORMAT_24H) + ".xlsx";
		}

		return CommonDefine.PATH_ROOT + CommonDefine.EXCEL.NX_REPORT_DIR
				+ "\\" + fileName;
	}

	public void arrangePMForAMP(List<Map<String, Object>> unitRows,
			List<Map<String, Object>> tpms, List<Map<String, Object>> rpms) {

		if (unitRows != null && unitRows.size() > 0) {

			for (Map<String, Object> u : unitRows) {
				if (u.get("BASE_UNIT_ID") != null) {
					List<Map<String, Object>> tpms_u = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> rpms_u = new ArrayList<Map<String, Object>>();
					int unitId = Integer.parseInt(u.get("BASE_UNIT_ID")
							.toString());
					if (tpms != null && tpms.size() > 0) {
						for (Map<String, Object> tpm : tpms) {
							if (tpm.get("BASE_UNIT_ID") != null
									&& Integer.parseInt(tpm.get("BASE_UNIT_ID")
											.toString()) == unitId) {
								tpms_u.add(tpm);
							}
						}
					}

					if (rpms != null && rpms.size() > 0) {
						for (Map<String, Object> rpm : rpms) {
							if (rpm.get("BASE_UNIT_ID") != null
									&& Integer.parseInt(rpm.get("BASE_UNIT_ID")
											.toString()) == unitId) {
								rpms_u.add(rpm);
							}
						}
					}

					u.put("tpms_u", tpms_u);
					u.put("rpms_u", rpms_u);
				}
			}
		}
	}

	public List<String> getPmTableName_common(int emsId,
			Map<String, String> searchCond) {

		// 获取基本信息
		String startTime = searchCond.get("start");
		String endTime = searchCond.get("end");

		List<String> rv = new ArrayList<String>();
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		try {
			start.setTime(TimeUtil.parseString2Date(startTime,
					CommonDefine.COMMON_FORMAT));
			end.setTime(TimeUtil.parseString2Date(endTime,
					CommonDefine.COMMON_FORMAT));
			while (start.before(end)) {
				int month = (start.get(Calendar.MONTH) + 1);
				String tableName = "t_pm_origi_data_" + emsId + "_"
						+ start.get(Calendar.YEAR) + "_"
						+ (month > 9 ? month : "0" + month);
				rv.add(tableName);
				start.add(Calendar.MONTH, 1);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return rv;
	}
	
	@SuppressWarnings("rawtypes")
	public String getSwitchDataForReport(List<Integer> targetIds, 
			Map<String, String> searchCond, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException {
		
		String filePath = "";
		
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			if(isPreview) {
				addPreviewDate(searchCond);
			}
			OptSwitchExcel excel = new OptSwitchExcel(createFileName(searchCond, genType, "光开关盘报表"));
			
			//时间归一化
			processTime(searchCond);
			int period = Integer.parseInt(searchCond.get("period"));
			if(period == CommonDefine.PM.PM_REPORT.PERIOD.MONTHLY 
					&& genType == CommonDefine.REPORT.REPORT_SCHEDULE){
				searchCond.put("retrievalTimePmDate", null);
			}
			
			//增加查询条件
			searchCond.put("nendRx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG));
			searchCond.put("nendTx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG));
			
			if(targetIds.size() > 0){
				List<Map<String, Object>> neRows = nxReportManagerMapper.getBaseInfoListForSwitch(targetIds);
				List<Map<String, Object>> unitRows = nxReportManagerMapper.getUnitInfoListForSwitch(targetIds);
				
				List<Integer> unitIds = new ArrayList<Integer>();
				for(Map<String, Object> m : unitRows){
					if(m.get("BASE_UNIT_ID") != null){
						unitIds.add(Integer.parseInt(m.get("BASE_UNIT_ID").toString()));
					}
				}
				
				List<Map<String, Object>> unitRows_pro = null;
				List<Map<String, Object>> proGrpCountList = null;
				if(unitIds.size() > 0){
					unitRows_pro = nxReportManagerMapper.getProUnitInfoListForSwitch(unitIds);
					proGrpCountList = nxReportManagerMapper.getProGrpCountList(unitIds);
					addProGrpCountForUnit(unitRows, proGrpCountList);
				}
				data = unifyInfoSwitch(neRows, unitRows, unitRows_pro);
				
				List<String> tableNames = new ArrayList<String>();
				List<Integer> ptpIds = new ArrayList<Integer>();
				if(unitRows_pro != null) {
					for(Map<String, Object> m : unitRows_pro){
						if(m.get("BASE_PTP_ID") != null) ptpIds.add(Integer.parseInt(m.get("BASE_PTP_ID").toString()));
					}
				}
				List<Map<String, Object>> pms = new ArrayList<Map<String, Object>>();
				
				for(Map<String, Object> m : neRows){
					if(m.get("EMS_ID") != null){
						tableNames.addAll(getPmTableName_common(Integer.parseInt(m.get("EMS_ID").toString()), searchCond));
					}
				}
				//移除重复的表名
				tableNames = removeRepeatString(tableNames);
				
				if(ptpIds.size() > 0){
					for(String tableName : tableNames){
						searchCond.put("TABLE_NAME", tableName);
						if(pmExists(tableName)){
							pms.addAll(nxReportManagerMapper.getPMDataForSwitchReport(ptpIds, searchCond));
						}
					}
				}
				
				if(pms.size() > 0){
					data.put("PM_DATA", pms);
				}
			}
			
			data.put("searchCond", searchCond);
			data.put("genType", genType);
			excel.writeData(data);
			
			Map<String, Object> exportInfo = excel.getResult();
			filePath = exportInfo.get("EXCEL_URL").toString();
			
			exportInfo.put("SYS_TASK_ID", searchCond.get("taskId"));
			exportInfo.put("TASK_TYPE",
					CommonDefine.QUARTZ.JOB_NX_REPORT_WDM_SWITCH);
			exportInfo.put("PERIOD", searchCond.get("period"));// 0是日报,1是月报
			exportInfo.put("DATA_SRC", searchCond.get("dataSrc"));
			exportInfo.put("CREATOR", sysUserId);
			exportInfo.put("NORMAL_CSV_PATH", "");
			exportInfo.put("ABNORMAL_CSV_PATH", "");
			exportInfo.put("PRIVILEGE", searchCond.get("privilege"));
			performanceManagerMapper.savePmExportInfo(exportInfo, new HashMap());
			
		}catch (Exception e) {
			throw new CommonException(e, 1);
		}
		
		if(isPreview) {
			String outputPath = getMD5FileName(filePath);
			File f = new File(outputPath);
			if (!f.exists()) {
				Conveter.e2h(filePath, outputPath);
			}
			int pos = outputPath.indexOf("webapps");
			outputPath = outputPath.substring(pos + 8).replace("\\", "/");
			filePath = outputPath;
		}
		
		return filePath;
	}
	
	/**
	 * 给报表预览加入开始和结束时间
	 * @param searchCond
	 */
	private void addPreviewDate(Map<String, String> searchCond) {
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cld = Calendar.getInstance();
		cld.setTime(new Date());
		cld.add(Calendar.DAY_OF_YEAR, -2);
		
		searchCond.put("start", sf.format(cld.getTime()));
		searchCond.put("end", sf.format(new Date()));
	}
	
	@SuppressWarnings("rawtypes")
	public String getWaveDataForReport(List<Integer> targetIds, 
			Map<String, String> searchCond, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException {
		
		String filePath = "";
		
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			String exceptionFileName = "";
			int unitType = 0;
			if(searchCond.get("unitType") != null){
				unitType = Integer.parseInt(searchCond.get("unitType"));
			}
			if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN){
				exceptionFileName = "合波盘报表";
			}else if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV){
				exceptionFileName = "分波盘报表";
			}
			if(isPreview) {
				addPreviewDate(searchCond);
			}
			WaveUnitExcel excel = new WaveUnitExcel(createFileName(searchCond, genType, exceptionFileName));
			
			//时间归一化
			processTime(searchCond);
			int period = Integer.parseInt(searchCond.get("period"));
			if(period == CommonDefine.PM.PM_REPORT.PERIOD.MONTHLY 
					&& genType == CommonDefine.REPORT.REPORT_SCHEDULE){
				searchCond.put("retrievalTimePmDate", null);
			}
			
			//增加查询条件
			searchCond.put("nendRx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG));
			searchCond.put("nendTx", String.valueOf(CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG));
			
			if(targetIds.size() > 0){
				List<Map<String, Object>> baseInfoRows = nxReportManagerMapper.getBaseInfoListForWave(targetIds);
				List<Map<String, Object>> unitRows = nxReportManagerMapper.getUnitInfoListForWave(targetIds, unitType);
				
				List<String> tableNames = new ArrayList<String>();
				List<Integer> ptpIds = new ArrayList<Integer>();
				if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN){
					for(Map<String, Object> m : unitRows){
						if(m.get("T_PTP_ID") != null){
							ptpIds.add(Integer.parseInt(m.get("T_PTP_ID").toString()));
							m.put("PTP_ID", m.get("T_PTP_ID").toString());
						}
					}
				}else if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV){
					for(Map<String, Object> m : unitRows){
						if(m.get("R_PTP_ID") != null){
							ptpIds.add(Integer.parseInt(m.get("R_PTP_ID").toString()));
							m.put("PTP_ID", m.get("R_PTP_ID").toString());
						}
					}
				}
				
				List<Map<String, Object>> pms = new ArrayList<Map<String, Object>>();
				
				Map<String, String> lastMonth = new HashMap<String, String>();
				lastMonth.put("start", getDateInLastMonth(searchCond.get("start")));
				lastMonth.put("end", getDateInLastMonth(searchCond.get("end")));
				
				searchCond.put("lastMonthStart", lastMonth.get("start"));
				searchCond.put("lastMonthEnd", lastMonth.get("end"));
				
				for(Map<String, Object> m : unitRows){
					if(m.get("EMS_ID") != null){
						tableNames.addAll(getPmTableName_common(Integer.parseInt(m.get("EMS_ID").toString()), searchCond));
						tableNames.addAll(getPmTableName_common(Integer.parseInt(m.get("EMS_ID").toString()), lastMonth));
					}
				}
				
				//移除重复的表名
				tableNames = removeRepeatString(tableNames);
				
				if(ptpIds.size() > 0){
					for(String tableName : tableNames){
						searchCond.put("TABLE_NAME", tableName);
						if(pmExists(tableName)){
							if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN){
								pms.addAll(nxReportManagerMapper.getPMDataForWaveJoinReport(ptpIds, searchCond));
								pms.addAll(nxReportManagerMapper.getLastMonthPMDataForWaveJoinReport(ptpIds, searchCond));
							}else if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV){
								pms.addAll(nxReportManagerMapper.getPMDataForWaveDivReport(ptpIds, searchCond));
								pms.addAll(nxReportManagerMapper.getLastMonthPMDataForWaveDivReport(ptpIds, searchCond));
							}
						}
					}
				}
				
				if(pms.size() > 0){
					data.put("PM_DATA", pms);
				}
				
				unifyInfoWave(baseInfoRows, unitRows);
				if(baseInfoRows.size() > 0){
					data.put("BASE_INFO_ROWS", baseInfoRows);
				}
			}
			
			data.put("searchCond", searchCond);
			data.put("genType", genType);
			excel.writeData(data);
			Map<String, Object> exportInfo = excel.getResult();
			filePath = exportInfo.get("EXCEL_URL").toString();
			
			exportInfo.put("SYS_TASK_ID", searchCond.get("taskId"));
			if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_JOIN){
				exportInfo.put("TASK_TYPE",
						CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_JOIN);
			}else if(unitType == CommonDefine.NX_REPORT.UNIT_TYPE.WAVE_DIV){
				exportInfo.put("TASK_TYPE",
						CommonDefine.QUARTZ.JOB_NX_REPORT_WAVE_DIV);
			}
			exportInfo.put("PERIOD", searchCond.get("period"));// 0是日报,1是月报
			exportInfo.put("DATA_SRC", searchCond.get("dataSrc"));
			exportInfo.put("CREATOR", sysUserId);
			exportInfo.put("NORMAL_CSV_PATH", "");
			exportInfo.put("ABNORMAL_CSV_PATH", "");
			exportInfo.put("PRIVILEGE", searchCond.get("privilege"));
			performanceManagerMapper.savePmExportInfo(exportInfo, new HashMap());
			
		}catch (Exception e) {
			throw new CommonException(e, 1);
		}
		if(isPreview) {
			String outputPath = getMD5FileName(filePath);
			File f = new File(outputPath);
			if (!f.exists()) {
				Conveter.e2h(filePath, outputPath);
			}
			int pos = outputPath.indexOf("webapps");
			outputPath = outputPath.substring(pos + 8).replace("\\", "/");
			filePath = outputPath;
		}
		
		return filePath;
	}
	
	public void unifyInfoWave(List<Map<String, Object>> baseInfoRows, List<Map<String, Object>> unitRows) {
		
		if(baseInfoRows != null && unitRows != null){
			for(Map<String, Object> base : baseInfoRows){
				if(base.get("TARGET_ID") != null){
					List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> unit : unitRows){
						if(unit.get("TARGET_ID") != null && 
							Integer.parseInt(base.get("TARGET_ID").toString()) == Integer.parseInt(unit.get("TARGET_ID").toString())){
							temp.add(unit);
						}
					}
					if(temp.size() > 0){
						base.put("UNIT_ROWS", temp);
					}
				}
			}
		}
	}
	
	public void addProGrpCountForUnit(List<Map<String, Object>> unitRows, 
			List<Map<String, Object>> proGrpCountList){
		
		if(unitRows != null){
			for(Map<String, Object> u : unitRows){
				if(proGrpCountList != null){
					for(Map<String, Object> p : proGrpCountList){
						if(u.get("BASE_UNIT_ID") != null 
						   && p.get("BASE_UNIT_ID") != null 
						   && Integer.parseInt(u.get("BASE_UNIT_ID").toString()) == Integer.parseInt(p.get("BASE_UNIT_ID").toString())
						   && p.get("PROTECT_GROUP_COUNT") != null){
							u.put("PROTECT_GROUP_COUNT", p.get("PROTECT_GROUP_COUNT").toString());
						}
					}
				}
			}
		}
	}
	
	/**
	 * 字符型list去重
	 * @param data
	 */
	public List<String> removeRepeatString(List<String> data) {
		List<String> rv = new ArrayList<String>();
		if(data != null && data.size() > 0){
			for(String v:data){
				if(rv.indexOf(v)<0){
					rv.add(v);
				}
			}
		}
		return rv;
	}
	
	/**
	 * 整理光开关盘信息
	 * @param neRows
	 * @param unitRows
	 * @param protectGroupRows
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> unifyInfoSwitch(List<Map<String, Object>> neRows, 
			List<Map<String, Object>> unitRows, List<Map<String, Object>> protectGroupRows) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		List<Map<String, Object>> proAfterUnify = unifyUnitRowSwitch(protectGroupRows);
		if(unitRows != null){
			for(Map<String, Object> u : unitRows){
				if(proAfterUnify != null){
					for(Map<String, Object> p : proAfterUnify){
						if(u.get("BASE_UNIT_ID") != null 
						   && p.get("BASE_UNIT_ID") != null 
						   && Integer.parseInt(u.get("BASE_UNIT_ID").toString()) == Integer.parseInt(p.get("BASE_UNIT_ID").toString())
						   && p.get("PROTECT_GROUPS") != null){
							u.put("PROTECT_GROUPS", p.get("PROTECT_GROUPS"));
						}
					}
				}
				
				if(u.get("PROTECT_GROUP_COUNT") != null){
					int count = Integer.parseInt(u.get("PROTECT_GROUP_COUNT").toString());
					if(u.get("PROTECT_GROUPS") != null){
						int size = ((List<Map<String, Object>>)u.get("PROTECT_GROUPS")).size();
						if(size < count){
							for(int i=0; i<count-size;i++){
								((List<Map<String, Object>>)u.get("PROTECT_GROUPS")).add(new HashMap<String, Object>());
							}
						}
					}
				}
			}
		}
		
		if(neRows != null){
			for(Map<String, Object> ne : neRows){
				List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
				for(Map<String, Object> u : unitRows){
					if(ne.get("TARGET_ID") != null 
					   && u.get("TARGET_ID") != null 
					   && Integer.parseInt(ne.get("TARGET_ID").toString()) == Integer.parseInt(u.get("TARGET_ID").toString())){
						temp.add(u);
					}
				}
				if(temp.size() > 0) ne.put("UNIT_ROWS", temp);
			}
			
			data.put("NE_ROWS", neRows);
		}
		
		return data;
	}
	
	/**
	 * 按照uintId统一excel的行记录
	 * @param list 保护组记录
	 * @return
	 */
	public List<Map<String, Object>> unifyUnitRowSwitch(List<Map<String, Object>> list) {
		
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		
		if(list != null){
			//先获取所有的unitId
			List<Integer> unitIds = new ArrayList<Integer>();
			for(Map<String, Object> m : list){
				unitIds.add(Integer.parseInt(m.get("BASE_UNIT_ID").toString()));
			}
			//去重unitIds
			unitIds = removeRepeatInteger(unitIds);
			
			for(Integer i : unitIds){
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("BASE_UNIT_ID", i);
				
				List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
				for(Map<String, Object> map : list){
					if(map.get("BASE_UNIT_ID") != null 
					   && Integer.parseInt(map.get("BASE_UNIT_ID").toString()) == i.intValue()){
						temp.add(map);
					}
				}
				
				row.put("PROTECT_GROUPS", unifyProtectGroupSwitch(temp));
				rows.add(row);
			}
		}
		
		return rows;
	}
	
	/**
	 * 按照unitId来统一保护组
	 * @param list
	 * @return
	 */
	public List<Map<String, Object>> unifyProtectGroupSwitch(List<Map<String, Object>> list) {
		
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		List<Integer> groupNum = new ArrayList<Integer>();
		if(list != null && list.size() > 0){
			for(Map<String, Object> map : list){
				if(map.get("GROUP_NUM") != null){
					groupNum.add(Integer.parseInt(map.get("GROUP_NUM").toString()));
				}
			}
		}
		//去重
		groupNum = removeRepeatInteger(groupNum);
		for(Integer i : groupNum){
			Map<String, Object> row = new HashMap<String, Object>();
			boolean alreadyPutOne_fen = false;
			for(Map<String, Object> map : list){
				if(map.get("GROUP_NUM") != null 
				   && Integer.parseInt(map.get("GROUP_NUM").toString()) == i.intValue()){
					row.put("GROUP_NUM", map.get("GROUP_NUM"));
					if(map.get("PTP_TYPE") != null){
						int ptpType = Integer.parseInt(map.get("PTP_TYPE").toString());
						switch(ptpType){
						case CommonDefine.NX_REPORT.PTP_TYPE.HE_LU_KOU://和路口
							if(map.get("POWER_BUDGET") != null){
								row.put("powerBudget_he", map.get("POWER_BUDGET").toString());
							}
							if(map.get("BUSSINESS_NAME") != null){
								row.put("bussinessName_he", map.get("BUSSINESS_NAME").toString());
							}
							if(map.get("BASE_PTP_ID") != null){
								row.put("ptpId1_he", map.get("BASE_PTP_ID").toString());
							}
							if(map.get("SWITCH_THRESHOLD") != null){
								row.put("SWITCH_THRESHOLD", map.get("SWITCH_THRESHOLD").toString());
							}
							break;
						case CommonDefine.NX_REPORT.PTP_TYPE.FEN_LU_KOU://分路口
							if(alreadyPutOne_fen){
								if(map.get("POWER_BUDGET") != null){
									row.put("powerBudget2_fen", map.get("POWER_BUDGET").toString());
								}
								if(map.get("DIRECTION") != null){
									row.put("direction2_fen", map.get("DIRECTION").toString());
								}
								if(map.get("BASE_PTP_ID") != null){
									row.put("ptpId2_fen", map.get("BASE_PTP_ID").toString());
								}
							}else{
								if(map.get("POWER_BUDGET") != null){
									row.put("powerBudget1_fen", map.get("POWER_BUDGET").toString());
								}
								if(map.get("DIRECTION") != null){
									row.put("direction1_fen", map.get("DIRECTION").toString());
								}
								if(map.get("BASE_PTP_ID") != null){
									row.put("ptpId1_fen", map.get("BASE_PTP_ID").toString());
								}
								alreadyPutOne_fen = true;
							}
							break;
						case CommonDefine.NX_REPORT.PTP_TYPE.BAN_KA_JIE_KOU://业务侧板卡接口
							if(map.get("UNIT_NAME") != null){
								row.put("UNIT_NAME_COR", map.get("UNIT_NAME").toString());
							}
							if(map.get("SLOT_NO") != null){
								row.put("SLOT_NO_COR", map.get("SLOT_NO").toString());
							}
							if(map.get("SENSITIVITY") != null){
								row.put("SENSITIVITY", map.get("SENSITIVITY").toString());
							}
							if(map.get("WAVE_LENGTH") != null){
								row.put("WAVE_LENGTH", map.get("WAVE_LENGTH").toString());
							}
							break;
						default:
							break;
						}
					}
				}
			}
			rows.add(row);
		}
		
		return rows;
	}
	
	/**
	 * 整型list去重
	 * @param data
	 */
	public List<Integer> removeRepeatInteger(List<Integer> data) {
		List<Integer> rv = new ArrayList<Integer>();
		if(data != null && data.size() > 0){
			for(Integer v:data){
				if(rv.indexOf(v)<0){
					rv.add(v);
				}
			}
		}
		return rv;
	}



	//--------------------------MeiK END-----------------------------
}