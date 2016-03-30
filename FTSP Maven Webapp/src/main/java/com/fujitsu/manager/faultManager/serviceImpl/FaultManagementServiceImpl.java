package com.fujitsu.manager.faultManager.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.dao.mysql.FaultManagementMapper;
import com.fujitsu.manager.faultManager.service.FaultManagementService;
import com.fujitsu.util.WebMsgPush;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class FaultManagementServiceImpl extends FaultManagementService {
	
	@Resource
	public FaultManagementMapper faultManagementMapper;
	
	@Autowired
	private Mongo mongo;
	
	@Resource
	public WebMsgPush webMsgPush;
	
	public Map<String, Object> getFaultList(Map<String, String> paramMap, int start, int limit) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		Map<String, Object> map = arrangeQueryFaultConds(paramMap);
		int total = faultManagementMapper.getFaultCount(map);
		List<Map<String, Object>> resultList = faultManagementMapper.getFaultList(map, start, limit);
		
		data.put("total", total);
		data.put("rows", resultList);
		
		return data;
	}
	
	/**
	 * 整理故障查询条件
	 * @param paramMap
	 * @return
	 */
	private Map<String, Object> arrangeQueryFaultConds(Map<String, String> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		Date startTime = null;
		Date endTime = null;
		Integer status = null;
		Integer faultGenerate = null;
		
		if(paramMap.get("startTime") != null && !"".equals(paramMap.get("startTime").toString())){
			startTime = dateStringToDate(paramMap.get("startTime").toString());
		}
		
		if(paramMap.get("endTime") != null && !"".equals(paramMap.get("endTime").toString())){
			endTime = dateStringToDate(paramMap.get("endTime").toString());
		}
		
//		if((paramMap.get("startTime") == null || "".equals(paramMap.get("startTime").toString())) &&
//				(paramMap.get("endTime") == null || "".equals(paramMap.get("endTime").toString()))){
//			
//			startTime = getDateBefore(new Date(), 7);
//			endTime = new Date();
//		}
		
		if(paramMap.get("status") != null && !"".equals(paramMap.get("status").toString())){
			status = Integer.parseInt(paramMap.get("status").toString());
			if(status == CommonDefine.FAULT_MANAGEMENT.FAULT_PROC_STATUS_ALL) status = null;
		}
		
		if(paramMap.get("faultGenerate") != null && !"".equals(paramMap.get("faultGenerate").toString())){
			faultGenerate = Integer.parseInt(paramMap.get("faultGenerate").toString());
			if(faultGenerate == CommonDefine.FAULT_MANAGEMENT.FAULT_SOURCE_ALL) faultGenerate = null;
		}
		
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("status", status);
		map.put("faultGenerate", faultGenerate);
		
		return map;
	}
	
	/**
	 * 得到几天前的时间
	 * @param d
	 * @param day
	 * @return
	 */
//	private Date getDateBefore(Date d, int day) {
//		
//		Calendar now = Calendar.getInstance();
//		now.setTime(d);
//		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
//		String dateStr = now.get(Calendar.YEAR) + "-" + 
//						 (now.get(Calendar.MONTH) + 1) + "-" + 
//						 now.get(Calendar.DAY_OF_MONTH) + " 00:00:00";
//		
//		return dateStringToDate(dateStr);
//	}
	
	/**
	 * 将时间字符串转换为时间类型
	 * @param dateString
	 * @return
	 */
	private Date dateStringToDate(String dateString) {

		Date date = null;

		if(dateString != null && !dateString.equals("")){
			SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				date = spf.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return date;
	}
	
	public CommonResult deleteFaultRecord(Map<String, String> paramMap) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			
			int faultId = Integer.parseInt(paramMap.get("faultId").toString());
			//删除故障信息
			faultManagementMapper.deleteFaultByFaultId(faultId);
			//删除与故障相关的告警
			faultManagementMapper.deleteFaultAlarmByFaultId(faultId);
			result.setReturnResult(CommonDefine.SUCCESS);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	
	public Map<String, Object> getFaultInfoByFaultIdAndType(Map<String, String> paramMap) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			
			int type = Integer.parseInt(paramMap.get("type"));
			int faultId = Integer.parseInt(paramMap.get("faultId"));
			if(type == CommonDefine.FAULT_MANAGEMENT.FAULT_TYPE_EQPT) {
				data = faultManagementMapper.getEqptFaultInfoById(faultId);
			}else if(type == CommonDefine.FAULT_MANAGEMENT.FAULT_TYPE_LINE) {
				data = faultManagementMapper.getLineFaultInfoById(faultId);
			}
			
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> getFaultAlarmList(Map<String, String> paramMap, int start, int limit) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			
			int faultId = Integer.parseInt(paramMap.get("faultId"));
			int total = faultManagementMapper.getFaultAlarmCount(faultId);
			List<Map<String, Object>> resultList = faultManagementMapper.
										getFaultAlarmList(faultId, start, limit);
			
			data.put("total", total);
			data.put("rows", resultList);
		
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> getEquipFaultLocationInfo(Map<String, String> paramMap) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			
			int neId = Integer.parseInt(paramMap.get("neId"));
			int unitId = Integer.parseInt(paramMap.get("unitId"));
			String sysName = null;
			String stationName = null;
			String factory = null;
			
			List<Map<String, Object>> sysInfoByUnitId = faultManagementMapper.getSysNameByUnitId(unitId);
			if(sysInfoByUnitId != null && sysInfoByUnitId.size() > 0){
				Map<String, Object> sysMapByUnitId = sysInfoByUnitId.get(0);
				if(sysMapByUnitId.get("SYS_NAME") != null){
					sysName = sysMapByUnitId.get("SYS_NAME").toString();
				}
			}
			
			if(sysName == null){
				List<Map<String, Object>> sysInfoByNeId = faultManagementMapper.getSysNameByNeId(neId);
				if(sysInfoByNeId != null && sysInfoByNeId.size() > 0){
					Map<String, Object> sysMapByNeId = sysInfoByNeId.get(0);
					if(sysMapByNeId.get("SYS_NAME") != null){
						sysName = sysMapByNeId.get("SYS_NAME").toString();
					}
				}
			}
			
			Map<String, Object> neInfo = faultManagementMapper.getNeInfoByNeId(neId);
			if(neInfo.get("STATION_NAME") != null){
				stationName = neInfo.get("STATION_NAME").toString();
			}
			if(neInfo.get("FACTORY") != null){
				factory = neInfo.get("FACTORY").toString();
			}
			
			sysName = (sysName == null) ? "" : sysName;
			stationName = (stationName == null) ? "" : stationName;
			factory = (factory == null) ? "" : factory;
			
			data.put("sysName", sysName);
			data.put("stationName", stationName);
			data.put("factory", factory);
		
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public CommonResult deleteFaultAlarm(Map<String, String> paramMap) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			
			int alarmId = Integer.parseInt(paramMap.get("alarmId").toString());
			
			faultManagementMapper.deleteFaultAlarmById(alarmId);
			result.setReturnResult(CommonDefine.SUCCESS);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
//	public CommonResult addFaultAlarm(List<Map<String, Object>> list) throws CommonException {
//		
//		CommonResult result = new CommonResult();
//		
//		try {
//			
//			arrangeFaultAlarm(list);
//			faultManagementMapper.addFaultAlarm(list);
//			result.setReturnResult(CommonDefine.SUCCESS);
//			
//		} catch (Exception e){
//			throw new CommonException(e,1);
//		}
//		
//		return result;
//	}
	
	public void arrangeFaultAlarm(List<Map<String, Object>> list) {
		
		for(Map<String, Object> map : list){
			Integer alarmId = null, convergeFlag = null, severity = null;
			String alarmName = null, neName = null;
			Date startTime = null, cleanTime = null;
			if(map.get("alarmId") != null && !"".equals(map.get("alarmId").toString())){
				alarmId = Integer.parseInt(map.get("alarmId").toString());
			}
			if(map.get("convergeFlag") != null && !"".equals(map.get("convergeFlag").toString())){
				convergeFlag = Integer.parseInt(map.get("convergeFlag").toString());
			}
			if(map.get("severity") != null && !"".equals(map.get("severity").toString())){
				severity = Integer.parseInt(map.get("severity").toString());
			}
			if(map.get("alarmName") != null && !"".equals(map.get("alarmName").toString())){
				alarmName = map.get("alarmName").toString();
			}
			if(map.get("neName") != null && !"".equals(map.get("neName").toString())){
				neName = map.get("neName").toString();
			}
			if(map.get("startTime") != null && !"".equals(map.get("startTime").toString())){
				startTime = this.dateStringToDate(map.get("startTime").toString());
			}
			if(map.get("cleanTime") != null && !"".equals(map.get("cleanTime").toString())){
				cleanTime = this.dateStringToDate(map.get("cleanTime").toString());
			}
			map.put("alarmId", alarmId);
			map.put("convergeFlag", convergeFlag);
			map.put("severity", severity);
			map.put("alarmName", alarmName);
			map.put("neName", neName);
			map.put("startTime", startTime);
			map.put("cleanTime", cleanTime);
		}
	}
	
	public Map<String, Object> getTransformSystemList() throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			
			List<Map<String, Object>> transformSystemList = faultManagementMapper.getTransformSystemList();
			data.put("rows", transformSystemList);
		
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> refreshFaultAlarm(Map<String, String> paramMap) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			int faultId = Integer.parseInt(paramMap.get("faultId"));
			//获取故障相关的告警
			List<Map<String, Object>> faultAlarmList_old = faultManagementMapper.getAllFaultAlarm(faultId);
			
			// 因DBCursor对象无法转成JSON对象，所以先转成List对象
			List<DBObject> faultAlarmList_new = new ArrayList<DBObject>();
			
			List<Integer> alarmIds = null;
			List<Integer> mainAlarmIds = null;
			List<Integer> deriveAlarmIds = null;
			List<Integer> unConvergeAlarmIds = null;
			
			// 封装查询条件
			BasicDBObject conditionQuery = null;
			// 获取数据库连接
			DBCollection conn = null;
			DBCursor currentAlarm = null;
			DBCursor historyAlarm = null;
			
			for(Map<String, Object> map : faultAlarmList_old){
				if(alarmIds == null){
					alarmIds = new ArrayList<Integer>();
				}
				alarmIds.add(Integer.parseInt(map.get("ALARM_ID").toString()));
			}
			
			if(alarmIds != null && alarmIds.size() > 0){
				// 封装查询条件
				conditionQuery = new BasicDBObject ();
				conditionQuery.put("_id", new BasicDBObject("$in", alarmIds));
				// 获取数据库连接
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
				currentAlarm = conn.find(conditionQuery);
				
				while (currentAlarm.hasNext()) {
					DBObject dbo = currentAlarm.next();
					faultAlarmList_new.add(dbo);
				}
				
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
				historyAlarm = conn.find(conditionQuery);
				
				while (historyAlarm.hasNext()) {
					DBObject dbo = historyAlarm.next();
					faultAlarmList_new.add(dbo);
				}
				
				for(DBObject dbo : faultAlarmList_new){
					int convergeFlag = -1;
					int _id = 0;
					if(dbo.get("CONVERGE_FLAG") != null && !"".equals(dbo.get("CONVERGE_FLAG").toString())){
						convergeFlag = Integer.parseInt(dbo.get("CONVERGE_FLAG").toString());
						_id = Integer.parseInt(dbo.get("_id").toString());
						if(convergeFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_MAIN){
							if(mainAlarmIds == null){
								mainAlarmIds = new ArrayList<Integer>();
							}
							mainAlarmIds.add(_id);
						}else if(convergeFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_DERIVE){
							if(deriveAlarmIds == null){
								deriveAlarmIds = new ArrayList<Integer>();
							}
							deriveAlarmIds.add(_id);
						}else if(convergeFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_UNCONVERGE){
							if(unConvergeAlarmIds == null){
								unConvergeAlarmIds = new ArrayList<Integer>();
							}
							unConvergeAlarmIds.add(_id);
						}
					}
				}
			}
			
			//清除faultAlarmList_new中主告警不在其中的衍生告警
			List<DBObject> faultAlarmList_temp = new ArrayList<DBObject>();
			for(DBObject o : faultAlarmList_new){
				int parentId = 0;
				int cFlag = 0;
				if(o.get("CONVERGE_FLAG") != null && !"".equals(o.get("CONVERGE_FLAG").toString())){
					if(o.get("PARENT_ID") != null && !"".equals(o.get("PARENT_ID").toString())){
						cFlag = Integer.parseInt(o.get("CONVERGE_FLAG").toString());
						parentId = Integer.parseInt(o.get("PARENT_ID").toString());
						if(cFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_DERIVE){
							boolean addTemp = false;
							for(Integer i : mainAlarmIds){
								if(parentId == i.intValue()){
									addTemp = true;
								}
							}
							if(addTemp){
								faultAlarmList_temp.add(o);
							}
						}
						
						if((cFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_MAIN || 
								cFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_UNCONVERGE) && 
								parentId == 0){
							faultAlarmList_temp.add(o);
						}
					}else{
						cFlag = Integer.parseInt(o.get("CONVERGE_FLAG").toString());
						if(cFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_MAIN || 
								cFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_UNCONVERGE){
							faultAlarmList_temp.add(o);
						}
					}
				}
			}
			faultAlarmList_new = faultAlarmList_temp;
			
			faultAlarmList_temp = new ArrayList<DBObject>();
			if(mainAlarmIds != null && mainAlarmIds.size() > 0){
			
				//获取所有主告警的衍生告警
				// 封装查询条件
				conditionQuery = new BasicDBObject ();
				conditionQuery.put("PARENT_ID", new BasicDBObject("$in", mainAlarmIds));
				conditionQuery.put("CONVERGE_FLAG", CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_DERIVE);
				// 获取数据库连接
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
				currentAlarm = conn.find(conditionQuery);
				
				while (currentAlarm.hasNext()) {
					DBObject dbo = currentAlarm.next();
					boolean isNew = true;
					for(DBObject o : faultAlarmList_new){
						if(Integer.parseInt(o.get("_id").toString()) == Integer.parseInt(dbo.get("_id").toString())){
							isNew = false;
							break;
						}
					}
					if(isNew){
						faultAlarmList_temp.add(dbo);
					}
				}
				
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
				historyAlarm = conn.find(conditionQuery);
				
				while (historyAlarm.hasNext()) {
					DBObject dbo = historyAlarm.next();
					boolean isNew = true;
					for(DBObject o : faultAlarmList_new){
						if(Integer.parseInt(o.get("_id").toString()) == Integer.parseInt(dbo.get("_id").toString())){
							isNew = false;
							break;
						}
					}
					if(isNew){
						faultAlarmList_temp.add(dbo);
					}
				}
				
				faultAlarmList_new.addAll(faultAlarmList_temp);
			}
			
			//更新当前告警表的ANALYSIS_STATUS
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			BasicDBObject cond_c = new BasicDBObject();
			cond_c.put("_id", new BasicDBObject("$in", alarmIds));
			conn.update(cond_c, 
					new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", CommonDefine.IS_ANALYSIS_NO)),
					false,
					true);
			//更新历史告警表的ANALYSIS_STATUS
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
			BasicDBObject cond_h = new BasicDBObject();
			cond_h.put("_id", new BasicDBObject("$in", alarmIds));
			conn.update(cond_h, 
					new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", CommonDefine.IS_ANALYSIS_NO)),
					false,
					true);
			
			faultManagementMapper.deleteFaultAlarmByFaultId(faultId);
			List<Map<String, Object>> faultAlarmAdd = new ArrayList<Map<String, Object>>();
			List<Integer> alarmIds_new = new ArrayList<Integer>();
			for(DBObject o : faultAlarmList_new){
				faultAlarmAdd.add(transDBOToMap(o));
				alarmIds_new.add(Integer.parseInt(o.get("_id").toString()));
			}
			if(faultAlarmList_new != null && faultAlarmList_new.size() > 0){
				faultManagementMapper.addFaultAlarm(faultId, faultAlarmAdd);
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
				BasicDBObject condUpdate_c = new BasicDBObject();
				condUpdate_c.put("_id", new BasicDBObject("$in", alarmIds_new));
				conn.update(condUpdate_c, 
						new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", faultId)),
						false,
						true);
				
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
				BasicDBObject condUpdate_h = new BasicDBObject();
				condUpdate_h.put("_id", new BasicDBObject("$in", alarmIds_new));
				conn.update(condUpdate_h, 
						new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", faultId)),
						false,
						true);
			}
			
			//更新故障时间
			Map<String, Object> time = calAlarmTime(faultId);
			faultManagementMapper.updateAlarmStartAndEndTime(faultId, time);
			
			data.put("time", time);
			
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> transDBOToMap(DBObject o) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		Integer alarmId = null, convergeFlag = null, severity = null;
		String alarmName = null, neName = null;
		Date startTime = null, cleanTime = null;
		
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(o.get("_id") != null && !"".equals(o.get("_id").toString())){
			alarmId = Integer.parseInt(o.get("_id").toString());
		}
		
		if(o.get("CONVERGE_FLAG") != null && !"".equals(o.get("CONVERGE_FLAG").toString())){
			convergeFlag = Integer.parseInt(o.get("CONVERGE_FLAG").toString());
		}
		if(o.get("PERCEIVED_SEVERITY") != null && !"".equals(o.get("PERCEIVED_SEVERITY").toString())){
			severity = Integer.parseInt(o.get("PERCEIVED_SEVERITY").toString());
		}
		if(o.get("NATIVE_PROBABLE_CAUSE") != null && !"".equals(o.get("NATIVE_PROBABLE_CAUSE").toString())){
			alarmName = o.get("NATIVE_PROBABLE_CAUSE").toString();
		}
		if(o.get("NE_NAME") != null && !"".equals(o.get("NE_NAME").toString())){
			neName = o.get("NE_NAME").toString();
		}
		if(o.get("FIRST_TIME") != null && !"".equals(o.get("FIRST_TIME").toString())){
			startTime = this.dateStringToDate(sf.format(o.get("FIRST_TIME")));
		}
		if(o.get("CLEAR_TIME") != null && !"".equals(o.get("CLEAR_TIME").toString())){
			cleanTime = this.dateStringToDate(sf.format(o.get("CLEAR_TIME")));
		}
		
		map.put("alarmId", alarmId);
		map.put("convergeFlag", convergeFlag);
		map.put("severity", severity);
		map.put("alarmName", alarmName);
		map.put("neName", neName);
		map.put("startTime", startTime);
		map.put("cleanTime", cleanTime);
		
		return map;
	}
	
	public Map<String, Object> calAlarmTime(int faultId) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		List<Map<String, Object>> alarmList = faultManagementMapper.getAllFaultAlarm(faultId);
		
		Date startTimeMain = null;
		Date startTimeSub = null;
		Date endTimeMain = null;
		Date endTimeSub = null;
		Date tempTime = null;
		boolean hasMain = false;
		boolean isEndMain = true;
		boolean isEndSub = true;
		
		Date start = null;
		Date end = null;
		
		for(Map<String, Object> map : alarmList){
			String startTimeStr = null;
			if(map.get("START_TIME") != null && !"".equals(map.get("START_TIME").toString())){
				startTimeStr = map.get("START_TIME").toString();
			}
			String endTimeStr = null;
			if(map.get("CLEAN_TIME") != null && !"".equals(map.get("CLEAN_TIME").toString())){
				endTimeStr = map.get("CLEAN_TIME").toString();
			}

			int convergeFlag = -1;
			if(map.get("CONVERGE_FLAG") != null && !"".equals(map.get("CONVERGE_FLAG").toString())){
				convergeFlag = Integer.parseInt(map.get("CONVERGE_FLAG").toString());
			}
			
			if(convergeFlag == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_MAIN){//主告警
				hasMain = true;
				if(startTimeStr != null && startTimeStr != ""){
					if(startTimeMain == null){
						startTimeMain = dateStringToDate(startTimeStr);
					}else{
						tempTime = dateStringToDate(startTimeStr);
						startTimeMain = (startTimeMain.after(tempTime)) ? tempTime : startTimeMain;
					}
				}
				
				if(endTimeStr != null && endTimeStr != ""){
					if(endTimeMain == null){
						endTimeMain = dateStringToDate(endTimeStr);
					}else{
						tempTime = dateStringToDate(endTimeStr);
						endTimeMain = (endTimeMain.before(tempTime)) ? tempTime : endTimeMain;
					}
				}else{
					isEndMain = false;
				}
			}else{
				if(startTimeStr != null && startTimeStr != ""){
					if(startTimeSub == null){
						startTimeSub = dateStringToDate(startTimeStr);
					}else{
						tempTime = dateStringToDate(startTimeStr);
						startTimeSub = (startTimeSub.after(tempTime)) ? tempTime : startTimeSub;
					}
				}
				
				if(endTimeStr != null && endTimeStr != ""){
					if(endTimeSub == null){
						endTimeSub = dateStringToDate(endTimeStr);
					}else{
						tempTime = dateStringToDate(endTimeStr);
						endTimeSub = (endTimeSub.before(tempTime)) ? tempTime : endTimeSub;
					}
				}else{
					isEndSub = false;
				}
			}
		}
		
		if(hasMain){
			if(startTimeMain != null){
				start = startTimeMain;
			}
			
			if(isEndMain){
				if(endTimeMain != null){
					end = endTimeMain;
				}
			}
		}else{
			if(startTimeSub != null){
				start = startTimeSub;
			}
			
			if(isEndSub){
				if(endTimeSub != null){
					end = endTimeSub;
				}
			}
		}
		
		if(start != null){
			data.put("START_TIME", start);
		}
		if(end != null){
			data.put("ALM_CLEAR_TIME", end);
		}
		
		return data;
	}
	
	public Map<String, Object> getCableList() throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			
			List<Map<String, Object>> cableList = faultManagementMapper.getCableList();
			data.put("rows", cableList);
		
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> getCableSectionList(Map<String, String> paramMap) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			int cablesId = 0;
			if(paramMap != null){
				if(paramMap.get("cablesId") != null && !"".equals(paramMap.get("cablesId").toString())){
					cablesId = Integer.parseInt(paramMap.get("cablesId"));
				}
			}
			
			List<Map<String, Object>> cableSectionList = faultManagementMapper.
							getCableSectionList(cablesId);
			data.put("rows", cableSectionList);
		
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> getFaultReasonList(Map<String, String> paramMap) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			
			Map<String, Object> map = new HashMap<String, Object>();
			Integer level = null;
			Integer parentId = null;
			Integer reasonType = null;
			
			if(paramMap.get("level") != null && !"".equals(paramMap.get("level"))){
				level = Integer.parseInt(paramMap.get("level"));
				map.put("level", level);
			}
			if(paramMap.get("parentId") != null && !"".equals(paramMap.get("parentId"))){
				parentId = Integer.parseInt(paramMap.get("parentId"));
				map.put("parentId", parentId);
			}
			if(paramMap.get("reasonType") != null && !"".equals(paramMap.get("reasonType"))){
				reasonType = Integer.parseInt(paramMap.get("reasonType"));
				map.put("reasonType", reasonType);
			}
			
			List<Map<String, Object>> faultReasonList = faultManagementMapper.getFaultReasonList(map);
			data.put("rows", faultReasonList);
		
		} catch(Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public CommonResult saveFaultInfo(Map<String, String> paramMap, List<String> paramMapList) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			
			List<Integer> alarmIdList_Q = null;
			
			if(paramMapList != null && paramMapList.size() > 0){
				for(String s : paramMapList){
					Map<String, Object> alarmMap = (Map<String, Object>)JSON.parse(s);
					int alarmId = Integer.parseInt(alarmMap.get("alarmId").toString());
					if(alarmIdList_Q == null){
						alarmIdList_Q = new ArrayList<Integer>();
					}
					alarmIdList_Q.add(alarmId);
				}
			}
			
			boolean alarmConflict = false;
			String msg = "";
			int faultId = 0;
			if(paramMap.get("faultId") != null){//更新故障信息
				faultId = Integer.parseInt(paramMap.get("faultId").toString());
			}
			
			if(alarmIdList_Q != null && alarmIdList_Q.size() > 0){
				Map<String, Object> conflictMap = isAlarmConflict(faultId, alarmIdList_Q);
				alarmConflict = (Boolean)conflictMap.get("result");
				msg = conflictMap.get("msg").toString();
			}
			
			if(!alarmConflict){
				
				Map<String, Object> map = arrangeFaultInfo(paramMap);
				boolean isAdd = true;
				
				if(paramMap.get("faultId") != null){
					faultManagementMapper.updateFaultInfo(map);
					faultManagementMapper.deleteFaultAlarmByFaultId(faultId);
					isAdd = false;
				}else{
					map.put("status", CommonDefine.FAULT_MANAGEMENT.FAULT_PROC_STATUS_UNCONFIRMED);
					createFaultNo(map);
					map.put("createTime", new Date());
					faultManagementMapper.addFaultInfo(map);
				}
				
				if(map.get("faultId") != null && !"".equals(map.get("faultId").toString())){
					faultId = Integer.parseInt(map.get("faultId").toString());
					
					// 获取数据库连接
					DBCollection conn_c = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
					DBCollection conn_h = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
					if(!isAdd){//更新故障信息的情形
						//更新当前告警表
						conn_c.update(new BasicDBObject("ANALYSIS_STATUS", faultId),
								new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", CommonDefine.IS_ANALYSIS_NO)),
								false,
								true);
						//更新历史告警表
						conn_h.update(new BasicDBObject("ANALYSIS_STATUS", faultId),
								new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", CommonDefine.IS_ANALYSIS_NO)),
								false,
								true);
						faultManagementMapper.deleteFaultAlarmByFaultId(faultId);
					}
					
					if(alarmIdList_Q != null && alarmIdList_Q.size() > 0){
						
						//查询条件
						BasicDBObject conditionQuery = null;
						DBCursor currentAlarm = null;
						DBCursor historyAlarm = null;
						
						// 封装查询条件
						conditionQuery = new BasicDBObject ();
						conditionQuery.put("_id", new BasicDBObject("$in", alarmIdList_Q));
						currentAlarm = conn_c.find(conditionQuery);
						
						List<Map<String, Object>> faultAlarmList_add = new ArrayList<Map<String, Object>>();
						
						while (currentAlarm.hasNext()) {
							DBObject dbo = currentAlarm.next();
							faultAlarmList_add.add(this.transDBOToMap(dbo));
						}
						
						historyAlarm = conn_h.find(conditionQuery);
						
						while (historyAlarm.hasNext()) {
							DBObject dbo = historyAlarm.next();
							faultAlarmList_add.add(this.transDBOToMap(dbo));
						}
						
						if(faultAlarmList_add.size() > 0){
							//更新当前告警表
							BasicDBObject condUpdate = new BasicDBObject();
							condUpdate.put("_id", new BasicDBObject("$in", alarmIdList_Q));
							conn_c.update(condUpdate, 
									new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", faultId)),
									false,
									true);
							//更新历史告警表
							conn_h.update(condUpdate, 
									new BasicDBObject("$set", new BasicDBObject("ANALYSIS_STATUS", faultId)),
									false,
									true);
							faultManagementMapper.addFaultAlarm(faultId, faultAlarmList_add);
						}
					}
				}
				
				//更新故障时间
				Map<String, Object> time = calAlarmTime(faultId);
				faultManagementMapper.updateAlarmStartAndEndTime(faultId, time);
				
				result.setReturnResult(CommonDefine.SUCCESS);
				if(isAdd){
					result.setReturnMessage("故障创建成功！");
				}else{
					result.setReturnMessage("故障信息保存成功！");
				}
				
			}else{
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(msg);
			}
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	/**
	 * 更新首页故障信息
	 */
	public void updateFaultInfo_Main() throws CommonException {
		
		webMsgPush.updateFaultMsg();
	}
	
	/**
	 * 在新增或修改故障信息时判断告警是否有冲突
	 * @param faultId
	 * @param alalrmIds
	 * @return
	 */
	public Map<String, Object> isAlarmConflict(int faultId, List<Integer> alarmIds) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		boolean flag = false;
		String msg = "";
		
		//查询条件
		BasicDBObject conditionQuery = null;
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		DBCursor currentAlarm = null;
		DBCursor historyAlarm = null;
		
		// 封装查询条件
		conditionQuery = new BasicDBObject ();
		conditionQuery.put("_id", new BasicDBObject("$in", alarmIds));
		currentAlarm = conn.find(conditionQuery);
		
		// 因DBCursor对象无法转成JSON对象，所以先转成List对象
		List<DBObject> faultAlarmList_new = new ArrayList<DBObject>();
		
		while (currentAlarm.hasNext()) {
			DBObject dbo = currentAlarm.next();
			faultAlarmList_new.add(dbo);
		}
		
		conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		historyAlarm = conn.find(conditionQuery);
		
		while (historyAlarm.hasNext()) {
			DBObject dbo = historyAlarm.next();
			faultAlarmList_new.add(dbo);
		}
		
		if(faultId == 0){//新增故障的情形
			for(DBObject o : faultAlarmList_new){
				if(o.get("ANALYSIS_STATUS") != null && 
				   Integer.parseInt(o.get("ANALYSIS_STATUS").toString()) != -1){
					flag = true;
					msg = "所选告警中存在已关联故障的告警！";
					break;
				}
			}
		}else{//更新故障的情形
			for(DBObject o : faultAlarmList_new){
				if(o.get("ANALYSIS_STATUS") != null && 
				   Integer.parseInt(o.get("ANALYSIS_STATUS").toString()) != CommonDefine.IS_ANALYSIS_NO &&
				   Integer.parseInt(o.get("ANALYSIS_STATUS").toString()) != faultId){
					flag = true;
					msg = "所选告警中存在已关联故障的告警！";
					break;
				}
			}
		}
		
		if(!flag){
			List<Integer> mainIds = new ArrayList<Integer>();
			for(DBObject o : faultAlarmList_new){
				mainIds.add(Integer.parseInt(o.get("_id").toString()));
			}
			
			for(DBObject o : faultAlarmList_new){
				
				if(o.get("CONVERGE_FLAG") != null && 
					Integer.parseInt(o.get("CONVERGE_FLAG").toString()) == CommonDefine.FAULT_MANAGEMENT.FAULT_ALARM_FLAG_DERIVE){
					
					boolean temp = false;
					for(Integer i : mainIds){
						if(o.get("PARENT_ID") != null &&
						   Integer.parseInt(o.get("PARENT_ID").toString()) == i.intValue()){
							temp = true;
							break;
						}
					}
					
					if(!temp){
						flag = true;
						msg = "所选告警中有衍生告警的主告警不在所选之列！";
						break;
					}
				}
			}
		}
		
		data.put("result", flag);
		data.put("msg", msg);
		
		return data;
	}
	
	public void createFaultNo(Map<String, Object> map) {
		
		SimpleDateFormat df_from = new SimpleDateFormat("yyyy-MM-dd 00:00:00");//设置日期格式
		SimpleDateFormat df_to = new SimpleDateFormat("yyyy-MM-dd 23:59:59");//设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
		
		try {
			Date from = df.parse(df_from.format(new Date()));
			Date to = df.parse(df_to.format(new Date()));
			
			Map<String, Object> serialNoMap = faultManagementMapper.getMaxManualSerialNoToday(from, to);
			String faultNo = simple.format(new Date()) + "_M";
			int serialNo = 0;
			
			if(serialNoMap != null){
				serialNo = Integer.parseInt(serialNoMap.get("MAX_SERIAL_NO").toString());
			}
			serialNo += 1;
			map.put("serialNo", serialNo);
			String serialNoString = serialNo + "";
			if(serialNoString.length() == 1){
				serialNoString = "00" + serialNoString;
			}else if(serialNoString.length() == 2){
				serialNoString = "0" + serialNoString;
			}
			
			map.put("faultNo", faultNo + serialNoString);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		
//		SimpleDateFormat df_from = new SimpleDateFormat("yyyy-MM-dd 00:00:00");//设置日期格式
//		SimpleDateFormat df_to = new SimpleDateFormat("yyyy-MM-dd 23:59:59");//设置日期格式
//		
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		
//		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
//		
//		System.out.println(simple.format(new Date()));
//		System.out.println(df_to.format((new Date())));
//		try {
//			
//			System.out.println(df.parse(df_from.format(new Date())));
//			
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}
	
	private Map<String, Object> arrangeFaultInfo(Map<String, String> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(paramMap.get("faultId") != null && !"".equals(paramMap.get("faultId").toString())){
			int faultId = Integer.parseInt(paramMap.get("faultId").toString());
			map.put("faultId", faultId);
		}
		
		//故障生成
		if(paramMap.get("source") != null && !"".equals(paramMap.get("source").toString())){
			int source = Integer.parseInt(paramMap.get("source").toString());
			map.put("source", source);
		}
		
		//故障类别
		if(paramMap.get("type") != null && !"".equals(paramMap.get("type").toString())){
			int type = Integer.parseInt(paramMap.get("type").toString());
			map.put("type", type);
		}
		
		//故障原因
		if(paramMap.get("faultReason1") != null && !"".equals(paramMap.get("faultReason1").toString())){
			int faultReason1 = Integer.parseInt(paramMap.get("faultReason1").toString());
			map.put("reasonId1", faultReason1);
		}
		
		if(paramMap.get("faultReason2") != null && !"".equals(paramMap.get("faultReason2").toString())){
			int faultReason2 = Integer.parseInt(paramMap.get("faultReason2").toString());
			map.put("reasonId2", faultReason2);
		}
		
		//系统阻断
		if(paramMap.get("isBroken") != null && !"".equals(paramMap.get("isBroken").toString())){
			if("true".equals(paramMap.get("isBroken").toString())){
				map.put("isBroken", 1);
			}else if("false".equals(paramMap.get("isBroken").toString())){
				map.put("isBroken", 0);
			}
		}
		
		//传输系统
		if(paramMap.get("transSystem") != null && !"".equals(paramMap.get("transSystem").toString())){
			map.put("systemName", paramMap.get("transSystem").toString());
		}
		
		//网管
		if(paramMap.get("ems") != null && !"".equals(paramMap.get("ems").toString())){
			map.put("emsName", paramMap.get("ems").toString());
		}
		
		//台站
		if(paramMap.get("station") != null && !"".equals(paramMap.get("station").toString())){
			map.put("stationName", paramMap.get("station").toString());
		}
		
		//网元
		if(paramMap.get("ne") != null && !"".equals(paramMap.get("ne").toString())){
			map.put("neName", paramMap.get("ne").toString());
		}
		
		//板卡
		if(paramMap.get("unit") != null && !"".equals(paramMap.get("unit").toString())){
			map.put("unitName", paramMap.get("unit").toString());
		}
		
		//厂家
		if(paramMap.get("factory") != null && !"".equals(paramMap.get("factory").toString())){
			map.put("factory", CommonDefine.getFactoryFlag(paramMap.get("factory").toString()));
		}
		
		//准确度
		if(paramMap.get("accuracy") != null && !"".equals(paramMap.get("accuracy").toString())){
			int accuracy = Integer.parseInt(paramMap.get("accuracy").toString());
			map.put("accuracy", accuracy);
		}
		
		//故障描述
		if(paramMap.get("description") != null && !"".equals(paramMap.get("description").toString())){
			map.put("description", paramMap.get("description").toString());
		}
		
		//光缆
		if(paramMap.get("cable") != null && !"".equals(paramMap.get("cable").toString())){
			map.put("cableName", paramMap.get("cable").toString());
		}
		
		//光缆段
		if(paramMap.get("cableSection") != null && !"".equals(paramMap.get("cableSection").toString())){
			map.put("cableSectionName", paramMap.get("cableSection").toString());
		}
		
		//维护单位
		if(paramMap.get("maintenancer") != null && !"".equals(paramMap.get("maintenancer").toString())){
			map.put("maintenancer", paramMap.get("maintenancer").toString());
		}
		
		//起始局站
		if(paramMap.get("aStation") != null && !"".equals(paramMap.get("aStation").toString())){
			map.put("aStation", paramMap.get("aStation").toString());
		}
		
		//终点局站
		if(paramMap.get("zStation") != null && !"".equals(paramMap.get("zStation").toString())){
			map.put("zStation", paramMap.get("zStation").toString());
		}
		
		//距离
		if(paramMap.get("nearStation") != null && !"".equals(paramMap.get("nearStation").toString())){
			map.put("nearStation", paramMap.get("nearStation").toString());
		}
		
		//公里
		if(paramMap.get("distance") != null && !"".equals(paramMap.get("distance").toString())){
			int distance = Integer.parseInt(paramMap.get("distance").toString());
			map.put("distance", distance);
		}
		
		//经度
		if(paramMap.get("longitude") != null && !"".equals(paramMap.get("longitude").toString())){
			int longitude = Integer.parseInt(paramMap.get("longitude").toString());
			map.put("longitude", longitude);
		}
		
		//纬度
		if(paramMap.get("latitude") != null && !"".equals(paramMap.get("latitude").toString())){
			int latitude = Integer.parseInt(paramMap.get("latitude").toString());
			map.put("latitude", latitude);
		}
		
		//开始时间
		if(paramMap.get("startTime") != null && !"".equals(paramMap.get("startTime").toString())){
			map.put("startTime", dateStringToDate(paramMap.get("startTime").toString()));
		}
		
		//告警清除时间
		if(paramMap.get("alarmClearTime") != null && !"".equals(paramMap.get("alarmClearTime").toString())){
			map.put("alarmClearTime", dateStringToDate(paramMap.get("alarmClearTime").toString()));
		}
		
		return map;
	}

	public CommonResult faultConfirm(Map<String, String> paramMap, int userId) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			int faultId = Integer.parseInt(paramMap.get("faultId").toString());
			Map<String, Object> user = faultManagementMapper.getUserById(userId);
			faultManagementMapper.updateFaultStatus(faultId, 
					CommonDefine.FAULT_MANAGEMENT.FAULT_PROC_STATUS_CONFIRMED);
			if(user != null){
				if(user.get("USER_NAME") != null && !"".equals(user.get("USER_NAME").toString())){
					faultManagementMapper.addAckUser(faultId, user.get("USER_NAME").toString());
				}
			}
			
			result.setReturnResult(CommonDefine.SUCCESS);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	public CommonResult faultRecovery(Map<String, String> paramMap) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			int faultId = Integer.parseInt(paramMap.get("faultId").toString());
			boolean recoveryFlag = true;
			
			List<Map<String, Object>> mainAlarmList = faultManagementMapper.getFaultMainAlarm(faultId);
			for(Map<String, Object> map : mainAlarmList){
				if(map.get("CLEAN_TIME") == null){
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage("告警未清除，如确认告警已经清除，请刷新告警！");
					recoveryFlag = false;
					break;
				}
			}
			
			if(recoveryFlag){
				faultManagementMapper.faultRecovery(faultId, 
						CommonDefine.FAULT_MANAGEMENT.FAULT_PROC_STATUS_RECOVERY, new Date());
				result.setReturnResult(CommonDefine.SUCCESS);
			}
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	public CommonResult faultArchive(Map<String, String> paramMap) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			int faultId = Integer.parseInt(paramMap.get("faultId").toString());
			Map<String, Object> faultMap = this.faultManagementMapper.getFaultInfoByFaultId(faultId);
			if(faultMap.get("SOURCE") != null && !"".equals(faultMap.get("SOURCE").toString())){
				int source = Integer.parseInt(faultMap.get("SOURCE").toString());
				if(source == CommonDefine.FAULT_MANAGEMENT.FAULT_SOURCE_AUTOCREATE){
					if(faultMap.get("ACCURACY") != null && !"".equals(faultMap.get("ACCURACY").toString())){
						faultManagementMapper.updateFaultStatus(faultId, 
								CommonDefine.FAULT_MANAGEMENT.FAULT_PROC_STATUS_ARCHIVE);
						result.setReturnResult(CommonDefine.SUCCESS);
					}else{
						result.setReturnResult(CommonDefine.FAILED);
						result.setReturnMessage("请评价分析准确性！");
					}
				}else if(source == CommonDefine.FAULT_MANAGEMENT.FAULT_SOURCE_MANUALCREATE){
					faultManagementMapper.updateFaultStatus(faultId, 
							CommonDefine.FAULT_MANAGEMENT.FAULT_PROC_STATUS_ARCHIVE);
					result.setReturnResult(CommonDefine.SUCCESS);
				}else{
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage("此故障生成方式不明！");
				}
			}else{
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("此故障生成方式不明！");
			}
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	public CommonResult getFaultInfoForFP() throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			String faultInfo = "";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			List<Map<String, Object>> list = faultManagementMapper.getAllUnconfirmedFault();
			for(Map<String, Object> map : list){
				if(map.get("TYPE") != null && !"".equals(map.get("TYPE").toString())){
					int type = Integer.parseInt(map.get("TYPE").toString());
					if(type == CommonDefine.FAULT_MANAGEMENT.FAULT_TYPE_EQPT){
						if(map.get("SYSTEM_NAME") != null && !"".equals(map.get("SYSTEM_NAME").toString())){
							faultInfo += map.get("SYSTEM_NAME").toString();
						}
						if(map.get("STATION_NAME") != null && !"".equals(map.get("STATION_NAME").toString())){
							faultInfo += map.get("STATION_NAME").toString();
						}
						if(map.get("NE_NAME") != null && !"".equals(map.get("NE_NAME").toString())){
							faultInfo += map.get("NE_NAME").toString();
						}
						faultInfo += "设备故障";
						if(map.get("START_TIME") != null && !"".equals(map.get("START_TIME").toString())){
							faultInfo += df.format(map.get("START_TIME"));
						}
						faultInfo += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					}else if(type == CommonDefine.FAULT_MANAGEMENT.FAULT_TYPE_LINE){
						if(map.get("SYSTEM_NAME") != null && !"".equals(map.get("SYSTEM_NAME").toString())){
							faultInfo += map.get("SYSTEM_NAME").toString();
						}
						if(map.get("A_STATION") != null && !"".equals(map.get("A_STATION").toString())){
							faultInfo += map.get("A_STATION").toString();
						}
						if(map.get("Z_STATION") != null && !"".equals(map.get("Z_STATION").toString())){
							faultInfo += map.get("Z_STATION").toString();
						}
						faultInfo += "之间线路故障";
						if(map.get("START_TIME") != null && !"".equals(map.get("START_TIME").toString())){
							faultInfo += df.format(map.get("START_TIME"));
						}
						faultInfo += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					}
				}
			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(faultInfo);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
}
