package com.fujitsu.manager.imptProtectManager.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.IService.IImptProtectManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.IService.ITransSystemService;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.reportManager.util.ReportExportExcel;
import com.fujitsu.model.TopoNodeModel;

public class ImptProtectTaskAction extends DownloadAction {

	private static final long serialVersionUID = -6402498399929349L;
	
	@Resource
	public IImptProtectManagerService imptProtectManagerService;
	@Resource
	public IPerformanceManagerService performanceManagerService;
	@Resource
	public ICircuitManagerService circuitManagerService;
	@Resource
	public IAlarmManagementService alarmManagementService;
	@Resource
	public ITransSystemService transSystemService;
	private List<String> objList;
	private List<Integer> intList;
	private Map<String, String> paramMap;
	private Integer taskId;
	private String jsonString;
	private boolean convergeChecked;
	private HashMap<String,Object> param=new HashMap<String,Object>();

	@IMethodLog(desc = "重保:任务重名检查")
	public String checkTaskNameExist() {
		try {
			Map<String, Object> returnMap = new HashMap<String, Object>();
			param.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_IMPT_PROTECT);
			boolean data = imptProtectManagerService.checkTaskNameExist(param);
			returnMap.put("exit", data);
			resultObj = JSONObject.fromObject(returnMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("判断失败！");
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "重保:新增/修改任务", type = IMethodLog.InfoType.MOD)
	public String editTask() {
		try {
			param.put("CREATE_PERSON", sysUserId);
			param.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_IMPT_PROTECT);
			Map<String,Object> data=new HashMap<String,Object>();
			data.put("SYS_TASK_ID", imptProtectManagerService.editTask(param));
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "重保:修改任务状态", type = IMethodLog.InfoType.MOD)
	public String changeTaskStatus() {
		try {
			imptProtectManagerService.changeTaskStatus(param);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "重保:查询任务信息")
	public String getTask() {
		try {
			param.put("taskParam", new String[]{"TARGET_TYPE","CATEGORY","privilegeList"});
			param.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_IMPT_PROTECT);
			resultObj = JSONObject.fromObject(
					imptProtectManagerService.getTask(param,sysUserId));
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "重保:查询任务对象列表")
	public String getTaskInfo() {
		try {
			resultObj = JSONObject.fromObject(
					imptProtectManagerService.getTaskInfo(taskId,true));
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "重保:查询任务列表")
	public String getTaskList() {
		try {
			param.put("taskParam", new String[]{"TARGET_TYPE","CATEGORY"});
			param.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_IMPT_PROTECT);
			resultObj = JSONObject.fromObject(
					imptProtectManagerService.getTaskList(param,sysUserId,start,limit));
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "重保:查询任务对象列表")
	public String getTaskTargetNe() {
		try {
			List<Map> neList = imptProtectManagerService.getTaskTargetNe(taskId);
			Map<String,Object> resultObject = new HashMap<String, Object>();
			resultObject.put("neList", neList);
			resultObj = JSONObject.fromObject(resultObject);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	/**-------------------------监控--------------------------------------*/
	@IMethodLog(desc = "重保监控:历史性能查询")
	public String searchHistoryDataIntoTempTable() {
		try {
			if (sysUserId != null && sysUserId != 0) {
				List<Map> nodeList = ListStringtoListMap(this.objList);
				//----算日期----
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DATE,-7);
				SimpleDateFormat sdf = new SimpleDateFormat(CommonDefine.COMMON_END_FORMAT); 
				String endTime = sdf.format(new Date());
				sdf.applyPattern(CommonDefine.COMMON_START_FORMAT);
				String startTime = sdf.format(cld.getTime());
				//----
//				startTime = "2014-07-01 00:00:00";
//				endTime = "2014-07-08 23:59:59";
				List<Map> doneList = imptProtectManagerService.plusEmsIdToNodeList(nodeList);
				int searchTag = performanceManagerService
						.getHistoryPmData(true,
								true, doneList, startTime,
								endTime, null,
								null, sysUserId.intValue(),CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH);
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
	
	@IMethodLog(desc = "重保监控:当前性能查询")
	public String searchCurrentDataIntoTempTable() {		
		try {
		if (sysUserId != null && sysUserId != 0) {
			List<Map> nodeList = ListStringtoListMap(this.objList);
			Map result = performanceManagerService.getCurrentPmData(
					true, true,
					nodeList,
					Integer.parseInt(paramMap.get("granularity")),
					null, null, sysUserId,CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH);
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
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "重保监控:当前性能查询-电路")
	public String searchCurrentDataIntoTempTableCir() {		
			try {
				if (sysUserId != null && sysUserId != 0) {
					List<Map> ptpInfoList = new ArrayList<Map>();
					List<Map> neInfoList = new  ArrayList<Map>();
					if(param.get("ptpInfoList")!=null)
						ptpInfoList.addAll(ListStringtoListMap((List<String>)param.get("ptpInfoList")));
					if(param.get("neInfoList")!=null)
						neInfoList.addAll(ListStringtoListMap((List<String>)param.get("neInfoList")));
					Integer granularity = (Integer)param.get("granularity");
					boolean needUnitPm = true,needPtpPm = true, needCtpPm = false;
					Map result = performanceManagerService.searchCurrentDataIntoTempTableCir(
								ptpInfoList,neInfoList, sysUserId,granularity,needUnitPm,needPtpPm,needCtpPm, 
								CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH);
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
	
	@IMethodLog(desc = "重保监控:当前性能查询PTP")
	public String searchCurrentDataIntoTempTableByPtp() {
		try {
			if (sysUserId != null && sysUserId != 0) {
				List<Integer> idList = new ArrayList<Integer>();
				for(Object id:intList){
					idList.add((Integer)id);
				}
				
				Map result = imptProtectManagerService
						.searchCurrentDataIntoTempTableByPtp(idList, paramMap,
								sysUserId);
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
	
	
	@IMethodLog(desc = "重保监控:分页获取当前性能")
	public String getCurrentPmDate() {
		try {
			if (sysUserId != null && sysUserId != 0) {
				Map pmResult;
				Integer searchTag = null;
				if (paramMap.get("searchTag") != null) {
					searchTag = Integer.parseInt(paramMap.get("searchTag"));
				}

				pmResult = performanceManagerService.getTempPmDataByPage(
						CommonDefine.PM.PM_TABLE_NAMES.CURRENT_SDH_DATA,
						Integer.parseInt(paramMap.get("exception")), sysUserId,
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
	

	/**
	 * 获取电路路由拓扑图（otn）
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "重保监控:电路任务加载拓扑图")
	public String getRouteTopoCircuit() {
		try {
			Map<String, Object> rm = imptProtectManagerService.getTaskInfo(taskId,true);
			List<Map<String,Object>> rows= (List<Map<String,Object>>)rm.get("rows");
			if(rows.size()>0){
				Map<String, Object> data = circuitManagerService
						.getRouteTopoOtnAndSdh(rows);
				Map pos = imptProtectManagerService.getAPAPosition(param);
				//添加坐标数据
				if(pos!=null){
					List nodeOrLines = (List) data.get("rows");
					String posStr = (String) pos.get("PARAM_VALUE");
					JSONArray posObj = JSONArray.fromObject(posStr);
					Map<String, Map> posMap = new HashMap();
					for (int i = 0; i < posObj.size(); i++) {
						Map o = posObj.getJSONObject(i);
						posMap.put(o.get("id").toString(), o);
					}
					for (int i = 0; i < nodeOrLines.size(); i++) {
						TopoNodeModel o = null;
						try {
							o = (TopoNodeModel) nodeOrLines.get(i);
							Map tmp = posMap.get(o.getNodeId());
							if(tmp!=null){
								o.setPosition_X(tmp.get("x").toString());
								o.setPosition_Y(tmp.get("y").toString());
							}
						} catch (Exception e) {
						}
					}
				}
				data.put("cirInfoList", rows);
				List<Map> ctpInfoList = new ArrayList<Map>();
				for(Map<String,Object> c : rows){
					if(c.get("cir_no")!=null&&c.get("svc_type")!=null){
						Map<String, Object> littleItems = circuitManagerService.getNeAndPortAndCtpByCirNo(
								Integer.valueOf(c.get("cir_no").toString()),
								Integer.valueOf(c.get("svc_type").toString()));
						if(littleItems.get("ctpInfoList")!=null){
							ctpInfoList.addAll((List<Map>)littleItems.get("ctpInfoList"));
						}
					}
				}
				List<Integer> ptpIdList = (List<Integer>)data.get("ptpIdList");
				List<Map> ptpInfoList = imptProtectManagerService.getPtpInfo(ptpIdList);
				data.put("ptpInfoList", ptpInfoList);
				data.put("ctpInfoList", ctpInfoList);
				resultObj = JSONObject.fromObject(data);
				
//				out = JSONObject.fromObject(resultObj);
//				System.out.println("getRouteTopoOtnAndSdh = " + out.toString());
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取电路路由拓扑图（otn）
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "重保监控:设备任务加载拓扑图")
	public String getTopoDataEquip() {
		try {
			// 获得网元
			List<Map> neList = imptProtectManagerService.getTaskTargetNe(taskId);
			// 获得LINK
			List<Integer> neIdList = new ArrayList<Integer>();
			for(int i=0;i<neList.size();i++){
				if(neList.get(i).get("nodeId")!=null)
					neIdList.add(Integer.valueOf(neList.get(i).get("nodeId").toString()));
          	}
			Map<String, Object> linkData = transSystemService
					.getLinkBetweenNe(neIdList,null);
			List<Map> linkList = (List<Map>)linkData.get("rows");
			// 获得拓扑图数据
			Map<String, Object> topoData = imptProtectManagerService
					.getTopoDataEquip(neList,linkList);
			System.out.println("topo = " + JSONObject.fromObject(topoData));
			Map pos = imptProtectManagerService.getAPAPosition(param);
			//添加坐标数据
			if(pos!=null){
				List nodeOrLines = (List) topoData.get("rows");
				String posStr = (String) pos.get("PARAM_VALUE");
				JSONArray posObj = JSONArray.fromObject(posStr);
				Map<String, Map> posMap = new HashMap();
				for (int i = 0; i < posObj.size(); i++) {
					Map o = posObj.getJSONObject(i);
					posMap.put(o.get("id").toString(), o);
				}
				for (int i = 0; i < nodeOrLines.size(); i++) {
					TopoNodeModel o = null;
					try {
						o = (TopoNodeModel) nodeOrLines.get(i);
						Map tmp = posMap.get(o.getNodeId());
						if(tmp!=null){
							o.setPosition_X(tmp.get("x").toString());
							o.setPosition_Y(tmp.get("y").toString());
						}
					} catch (Exception e) {
					}
				}
			}
			resultObj = JSONObject.fromObject(topoData);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 初始化当前告警-电路任务
	 * （暂停使用）
	 * @return
	 */
	@IMethodLog(desc = "重保监控:电路任务的告警加载")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String initAlarmCir() {
		try {
			List<Map> cirList = ListStringtoListMap(this.objList);
			Map<String, Object> alm  = alarmManagementService.getCurrentAlarmForCircuit(cirList,
						start,limit,convergeChecked,true,true,true,true);
			resultObj = JSONObject.fromObject(alm);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 右键当前告警-电路任务
	 * 
	 * @return
	 */
	@IMethodLog(desc = "重保监控:右键当前告警-电路任务")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getAlarmForNodesOfCir() {
		try {
			List<Integer> ctpList = new ArrayList<Integer>();
			List<Integer> ptpList = new ArrayList<Integer>();
			List<Integer> neList = new ArrayList<Integer>();
			if(param.get("ptpList")!=null){
				for(Object id : (List<Object>)param.get("ptpList")){
					ptpList.add(Integer.valueOf(id.toString()));
				}
			}
			if(param.get("ctpList")!=null){
				for(Object id : (List<Object>)param.get("ctpList")){
					ctpList.add(Integer.valueOf(id.toString()));
				}
			}
			if(param.get("neList")!=null){
				for(Object id : (List<Object>)param.get("neList")){
					neList.add(Integer.valueOf(id.toString()));
				}
			}
//			List<Integer> unitList = imptProtectManagerService.getUnitListByPtpList(ptpList);
			
			boolean needPtpLevel = true,needCtpLevel=true,needNeLevel=true,needEquipLevel=true;
//			if(neList!=null&&neList.size()>0)
//				needNeLevel = true;
//			if(ptpList!=null&&ptpList.size()>0)
//				needPtpLevel = true;
//			if(unitList!=null&&unitList.size()>0)
//				needEquipLevel = true;
			Map<String, Object> alm  = alarmManagementService.getCurrentAlarmForCircuit(neList,ptpList,ctpList,null,
					start,limit,convergeChecked,needPtpLevel,needCtpLevel,needNeLevel,needEquipLevel);
			resultObj = JSONObject.fromObject(alm);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 获取电路任务的各种部件
	 * 
	 * @return
	 */
	@IMethodLog(desc = "重保监控:获取设备任务下对象")
	@SuppressWarnings("unchecked")
	public String getItemsOfEquipTask() {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map> neList = imptProtectManagerService.getTaskTargetNe(taskId);
			List<Integer> neIdList = new ArrayList<Integer>();
			for(int i=0;i<neList.size();i++){
				if(neList.get(i).get("nodeId")!=null)
					neIdList.add(Integer.valueOf(neList.get(i).get("nodeId").toString()));
          	}
			Map<String, Object> linkData = transSystemService
					.getLinkBetweenNe(neIdList,null);
			List<Map> linkList = (List<Map>)linkData.get("rows"); 
			// 返回任务下的网元和link
			data.put("neIdList", neIdList);
			data.put("linkList", linkList);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取电路任务的各种部件
	 * 
	 * @return
	 */
	@IMethodLog(desc = "重保监控:获取电路任务下对象")
	@SuppressWarnings("unchecked")
	public String getItemsOfCircuitTask() {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Map<String, Object> rm = imptProtectManagerService.getTaskInfo(taskId,true);
			List<Map<String,Object>> cirInfoList= (List<Map<String,Object>>)rm.get("rows");
			// 电路中所有的端口
			Set<Integer> ptpList = new HashSet<Integer>();
			// 电路中所有的网元
			Set<Integer> neList = new HashSet<Integer>();
			// 电路中所有的板卡
			Set<Integer> unitList = new HashSet<Integer>();
			List<Map> ptpInfoList = new ArrayList<Map>();
			List<Map> ctpInfoList = new ArrayList<Map>();
			for(Map<String,Object> c : cirInfoList){
				if(c.get("cir_no")!=null&&c.get("svc_type")!=null){
					Map<String, Object> littleItems = circuitManagerService.getNeAndPortAndCtpByCirNo(
							Integer.valueOf(c.get("cir_no").toString()),
							Integer.valueOf(c.get("svc_type").toString()));
					if(littleItems.get("neList")!=null)
						neList.addAll((Set<Integer>)littleItems.get("neList"));
					if(littleItems.get("ptpList")!=null){
						ptpList.addAll((Set<Integer>)littleItems.get("ptpList"));
						unitList.addAll(imptProtectManagerService.getUnitListByPtpList(new ArrayList(ptpList)));
						ptpInfoList = imptProtectManagerService.getPtpInfo(new ArrayList(ptpList));
					}
					if(littleItems.get("ctpInfoList")!=null){
						ctpInfoList.addAll((List<Map>)littleItems.get("ctpInfoList"));
					}
				}
			}
			data.put("cirInfoList", cirInfoList);
			data.put("ptpList", ptpList);
			data.put("neList", neList);
			data.put("unitList", unitList);
			data.put("ptpInfoList", ptpInfoList);
			data.put("ctpInfoList", ctpInfoList);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@IMethodLog(desc = "重保监控: 历史性能-电路任务")
	public String getHistoryPmDataCir() {		
		try {
			List<Map> ptpInfoList = new ArrayList<Map>();
			List<Map> neInfoList = new  ArrayList<Map>();
			if(param.get("ptpInfoList")!=null)
				ptpInfoList.addAll(ListStringtoListMap((List<String>)param.get("ptpInfoList")));
			if(param.get("neInfoList")!=null)
				neInfoList.addAll(ListStringtoListMap((List<String>)param.get("neInfoList")));
			
			//----算日期----
			Calendar cld = Calendar.getInstance();
			cld.setTime(new Date());
			cld.add(Calendar.DATE,-7);
			SimpleDateFormat sdf = new SimpleDateFormat(CommonDefine.COMMON_END_FORMAT); 
			String endTime = sdf.format(new Date());
			sdf.applyPattern(CommonDefine.COMMON_START_FORMAT);
			String startTime = sdf.format(cld.getTime());
			//----
//			startTime = "2014-07-01 00:00:00";
//			endTime = "2014-07-08 23:59:59";
			Map result = new HashMap();
			int searchTag = performanceManagerService.getHistoryPmDataCir(ptpInfoList,neInfoList,
					startTime, endTime,	sysUserId,
					CommonDefine.PM_SEARCH_TYPE.IMPT_PRO_SEARCH,true,true,true);
			result.put("searchTag", searchTag);
			result.put("returnResult", CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
	} catch (CommonException e) {
		result.setReturnResult(CommonDefine.FAILED);
		result.setReturnMessage(e.getErrorMessage());
		resultObj = JSONObject.fromObject(result);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return RESULT_OBJ;
	}
	/**-------------------------监控--------------------------------------*/
		
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "查询性能越限信息")
	public String getPmExceedData(){
		try {
//			System.out.println("jsonString = "  + jsonString);
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			System.out.println("jsonObject = " + jsonObject.toString());
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
//			System.out.println("unit = " + paramMap.get("unit"));
//			System.out.println("ne = " + paramMap.get("ne"));
//			System.out.println("ptp = " + paramMap.get("ptp"));
			// 定义一个Map接受查询返回的值
			List<Map<String, Object>> protectionSwitchInfo = imptProtectManagerService.getPmExceedData(paramMap, start, limit);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			// 将返回的结果转成JSON对象，返回前台
			Map<String, Object> rltMap = new HashMap<String, Object>();
			rltMap.put("total", protectionSwitchInfo.size());
			rltMap.put("rows", protectionSwitchInfo);
			resultObj = JSONObject.fromObject(rltMap, cfg);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings("unchecked") 
	@IMethodLog(desc = "查询性能越限信息")
	public String exportPmExceedData(){
		// 将参数专程JSON对象
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		System.out.println("jsonObject = " + jsonObject.toString());
		// 将JSON对象转成Map对象
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = (Map<String, Object>) jsonObject;
		// 定义一个Map接受查询返回的值
		try {
			List<Map<String, Object>> pmExceedInfo = imptProtectManagerService.getPmExceedData(paramMap, start, limit);
			 String destination = null;
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			 String myFlieName="性能越限_"+sdf.format(new Date());
			 ReportExportExcel ex=null;
			 ex = new ReportExportExcel(CommonDefine.PATH_ROOT + CommonDefine.EXCEL.TEMP_DIR, myFlieName, "性能越限", "性能");
			 destination = ex.writeExcel(pmExceedInfo, CommonDefine.EXCEL.PM_EXCEED, false);
			 setFilePath(destination);

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return RESULT_DOWNLOAD;
	}
	@IMethodLog(desc = "重保:获取APA坐标")
	public String getAPAPosition() {
		try {
			Map data = imptProtectManagerService.getAPAPosition(param);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("判断失败！");
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "重保:获取APA坐标")
	public String saveAPAPosition() {
		try {
			System.out.println(param.toString());
			imptProtectManagerService.saveAPAPosition(param);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {//catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("保存失败！");
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	public void setTaskId(Integer attr){
		taskId=attr;
		param.put("SYS_TASK_ID", attr);
	}
	public void setTaskName(String attr){
		param.put("TASK_NAME", attr);
	}
	public void setTaskStatus(Integer attr){
		param.put("TASK_STATUS", attr);
	}
	public void setTaskDescription(String attr){
		param.put("TASK_DESCRIPTION", attr);
	}
	public void setStartTime(String attr){
		SimpleDateFormat df=new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
		Date time=null;
		try {
			time=df.parse(attr);
			param.put("START_TIME",  time);
		} catch (ParseException e) {
		}
	}
	public void setEndTime(String attr){
		SimpleDateFormat df=new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
		Date time=null;
		try {
			time=df.parse(attr);
			param.put("END_TIME",  time);
		} catch (ParseException e) {
		}
	}
	public void setCategory(Integer attr){
		param.put("CATEGORY",  attr);
	}
	public void setTaskType(String attr){
		param.put("TARGET_TYPE",  attr);
	}
	public void setPrivilegeList(List<Integer> attrs){
		param.put("privilegeList", attrs);
	}
	public void setNeList(List<Integer> attrs){
		if(!new ArrayList(attrs).get(0).toString().isEmpty())
			param.put("neList", attrs);
	}
	public void setPtpList(List<Integer> attrs){
		if(!new ArrayList(attrs).get(0).toString().isEmpty())
			param.put("ptpList", attrs);
	}
	public void setCtpList(List<Integer> attrs){
		if(!new ArrayList(attrs).get(0).toString().isEmpty())
			param.put("ctpList", attrs);
	}
	public void setPtpInfoList(List<String> attrs){
		if(!new ArrayList(attrs).get(0).toString().isEmpty())
			param.put("ptpInfoList", attrs);
	}
	public void setNeInfoList(List<String> attrs){
		if(!new ArrayList(attrs).get(0).toString().isEmpty())
			param.put("neInfoList", attrs);
	}
	public void setGranularity(Integer attrs){
		param.put("granularity", attrs);
	}
	public void setEquipList(List<String> attrs){
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		if(attrs!=null){
			for(String attr:attrs){
				if(attr==null)continue;
				String[] info = attr.split("_");
				if(info.length!=2)continue;
				Map<String,Object> item = new HashMap<String, Object>();
				if(info[0]==null||!info[0].matches("^[+-]?[0-9]+$"))
					continue;
				if(info[1]==null||!info[1].matches("^[+-]?[0-9]+$"))
					continue;
				item.put("TARGET_TYPE", Integer.valueOf(info[0]));
				item.put("TARGET_ID", Integer.valueOf(info[1]));
				list.add(item);
			}
		}
		param.put("equipList", list);
	}
	public List<String> getObjList() {
		return objList;
	}
	public void setObjList(List<String> objList) {
		this.objList = objList;
	}
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	public HashMap<String, Object> getParam() {
		return param;
	}
	public void setParam(HashMap<String, Object> param) {
		this.param = param;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public List<Integer> getIntList() {
		return intList;
	}
	public void setIntList(List<Integer> intList) {
		this.intList = intList;
	}
	public boolean isConvergeChecked() {
		return convergeChecked;
	}
	public void setConvergeChecked(boolean convergeChecked) {
		this.convergeChecked = convergeChecked;
	}
}
