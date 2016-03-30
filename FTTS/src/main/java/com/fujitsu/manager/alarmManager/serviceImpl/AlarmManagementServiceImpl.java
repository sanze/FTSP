package com.fujitsu.manager.alarmManager.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;

import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.AlarmManagementMapper;
import com.fujitsu.dao.mysql.PlanMapper;
import com.fujitsu.manager.alarmManager.service.AlarmManagementService;
import com.fujitsu.manager.equipmentTestManager.service.EquipmentTestManagerService;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUAlarm;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.model.AlarmModel;
import com.fujitsu.model.PushAlarmModel;
import com.fujitsu.util.dwr.MyScriptSessionListener;


public class AlarmManagementServiceImpl extends AlarmManagementService {

	@Resource
	public AlarmManagementMapper alarmManagementMapper;
//	@Autowired
//	public GisManagementMapper gisManagementMapper;
//	@Autowired
//	private CommonService commonServiceImpl;
//	@Autowired
//	public SecurityManagementService securityManagementService;

	@Resource
	private EquipmentTestManagerService equipmentTestManagerService;
	
	@Resource
	private PlanMapper planMapper;
	
	@Resource
	public IAreaManagerService areaManagerService;

	/**
	 * 查询当前告警
	 * @param map 查询条件
	 * @param start
	 * @param limit
	 * @return
	 */
	public Map<String, Object> queryCurrentAlarm(Map<String, Object> map, 
			int start, int limit) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			int count = alarmManagementMapper.queryCurrentAlarmCount(map);
			List<Map<String, Object>> alarmList = alarmManagementMapper.queryCurrentAlarm(map, start, limit);
			List<AlarmModel> resultList = new ArrayList<AlarmModel>();
			
			//将无效的测试告警移到历史告警表
//			removeInvalidData();
			for(Map<String, Object> alarm : alarmList){
				resultList.add(transCurrentAlarm(alarm));
			}

			//增加测试链路信息
//			addTestLinkInfo(resultList);

			//封装结果数据
			resultMap.put("total", count);
			resultMap.put("rows", resultList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultMap;
	}
	
	public List<Map<String, Object>> getEquipAlarm(int rcId) {
		return alarmManagementMapper.getEquipAlarm(rcId);
	}
	
	/**
	 * 移除无效数据
	 */
//	public void removeInvalidData() {
//		
//		List<Map<String, Object>> alarmList = alarmManagementMapper.getAllTestAlarm();
//		List<Integer> resultIds = new ArrayList<Integer>();
//		List<Integer> dltIds = new ArrayList<Integer>();
//		
//		for(Map<String, Object> m : alarmList){
//			resultIds.add(Integer.parseInt(m.get("TEST_RESULT_ID").toString()));
//		}
//		
//		if(resultIds.size() > 0){
//			List<Map<String, Object>> testResultList = alarmManagementMapper.getTestResultList(resultIds);
//			for(Integer i : resultIds){
//				boolean isDlt = true;
//				for(Map<String, Object> m : testResultList){
//					if(i.intValue() == Integer.parseInt(m.get("TEST_RESULT_ID").toString())){
//						isDlt = false;
//						break;
//					}
//				}
//				if(isDlt){
//					dltIds.add(i);
//				}
//			}
//			
//			List<Map<String, Object>> dltAlarms = new ArrayList<Map<String, Object>>();
//			
//			if(dltIds.size() > 0){
//				for(Integer i : dltIds){
//					for(Map<String, Object> m : alarmList){
//						if(i.intValue() == Integer.parseInt(m.get("TEST_RESULT_ID").toString())){
//							dltAlarms.add(m);
//						}
//					}
//				}
//				
//				if(dltAlarms.size() > 0){
//					for(Map<String, Object> m : dltAlarms){
//						m.put("TEST_RESULT_ID", null);
//						m.put("BREAK_POINT_INFO", null);
//						this.addHistoryAlarm(m);
//						alarmManagementMapper.deleteCurAlarm(Integer.parseInt(m.get("ALARM_ID").toString()));
//					}
//					//首页
//					this.updateMainPageAlarmCount();
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 获取首页显示告警数据
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Integer> getAlarmCountForFP(int userId) {
//		
//		Map<String, Integer> resultMap = new HashMap<String, Integer>();
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		int cr = 0, mj = 0, mn = 0, wr = 0, offline = 0;
//		
//		//加入用户可见区域
//		List<Integer> regionIdList = null;
//		Map<String, Object> regionIdsMap = alarmManagementMapper.getRegionIds(userId);
//		if(regionIdsMap.get("REGION_ID") != null 
//				&& !"".equals(regionIdsMap.get("REGION_ID").toString())){
//			regionIdList = transNumStr(regionIdsMap.get("REGION_ID").toString());
//		}
//		//加入根节点ID
//		Map<String, Object> root = alarmManagementMapper.getRoot();
//		if(root != null){
//			if(regionIdList == null){
//				regionIdList = new ArrayList<Integer>();
//			}
//			regionIdList.add(Integer.parseInt(root.get("REGION_ID").toString()));
//		}
//		paramMap.put("regionIds", regionIdList);
//		
//		List<Map<String, Object>> alarmCountMap = alarmManagementMapper.
//								getAlarmCountForFP(paramMap, userId);
//		for(Map<String, Object> countMap : alarmCountMap){
//			int alarmLevel = Integer.parseInt(countMap.get("ALARM_LEVEL").toString());
//			int count = Integer.parseInt(countMap.get("ALARM_COUNT").toString());
//			switch(alarmLevel){
//			case Define.ALARM_LEVEL_CR:
//				cr = count;
//				break;
//			case Define.ALARM_LEVEL_MJ:
//				mj = count;
//				break;
//			case Define.ALARM_LEVEL_MN:
//				mn = count;
//				break;
//			case Define.ALARM_LEVEL_WR:
//				wr = count;
//				break;
//			case Define.ALARM_LEVEL_OFFLINE:
//				offline = count;
//				break;
//			default:
//				break;
//			}
//		}
//		
//		int unConfirmAlarmCount = alarmManagementMapper.
//				getUnConfirmAlarmCount(paramMap, userId);
//		
//		resultMap.put("CR", cr);
//		resultMap.put("MJ", mj + offline);
//		resultMap.put("MN", mn);
//		resultMap.put("WR", wr);
//		resultMap.put("unConfirmAlarmCount", unConfirmAlarmCount);
//		
//		return resultMap;
//	}
//
//	/**
//	 * 增加测试链路信息
//	 * @param list
//	 */
//	private void addTestLinkInfo(List<AlarmModel> list){
//
//		for(AlarmModel model : list){
//			int testResultId = model.getTEST_RESULT_ID();
//			if(testResultId != 0){
//				Map<String, Object> testLinkInfo = alarmManagementMapper.
//						getTestLinkInfo(testResultId);
//				if(testLinkInfo != null){
//					if(testLinkInfo.get("LINK_NAME") != null){
//						model.setTestLinkInfo(testLinkInfo.get("LINK_NAME").toString());
//					}
//					if(testLinkInfo.get("OPTICAL1_NAME") != null){
//						model.setLightPathInfo1(testLinkInfo.get("OPTICAL1_NAME").toString());
//					}
//					if(testLinkInfo.get("OPTICAL2_NAME") != null){
//						model.setLightPathInfo2(testLinkInfo.get("OPTICAL2_NAME").toString());
//					}
//				}
//			}
//		}
//
//	}

	/**
	 * 查询历史告警
	 * @param map
	 * @param start
	 * @param limit
	 * @return
	 */
	public Map<String, Object> queryHistoryAlarm(Map<String, Object> map, int start, int limit) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try{
			//整理查询条件
			arrangeCondsOfHistory(map);

			// 根据查询条件 查询总数
			int count = alarmManagementMapper.queryHistoryAlarmCount(map);

			// 分页查询符合条件数据
			List<Map<String, Object>> historyAlarm = alarmManagementMapper.queryHistoryAlarm(map, start, limit);
			List<AlarmModel> result = new ArrayList<AlarmModel>();
			for(Map<String, Object> alarm : historyAlarm){
				result.add(transHistoryAlarm(alarm));
			}

			//增加测试链路信息
//			addTestLinkInfo(resultList);

			//封装结果数据
			resultMap.put("total", count);
			resultMap.put("rows", result);

		}catch(Exception e){
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 整理历史告警查询条件
	 * @param map
	 */
	private void arrangeCondsOfHistory(Map<String, Object> map) {
		
		//告警发生时间 起
		if(map.get("startTimeFrom") != null) {
			map.put("startTimeFrom", dateStringToDate(map.get("startTimeFrom").toString()));
		}
		//告警发生时间 止
		if(map.get("startTimeTo") != null) {
			map.put("startTimeTo", dateStringToDate(map.get("startTimeTo").toString()));
		}
		//告警结束时间 起
		if(map.get("endTimeFrom") != null) {
			map.put("endTimeFrom", dateStringToDate(map.get("endTimeFrom").toString()));
		}
		//告警结束时间 止
		if(map.get("endTimeTo") != null) {
			map.put("endTimeTo", dateStringToDate(map.get("endTimeTo").toString()));
		}
	}

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

	/* 
	 * 告警确认
	 */
	public CommonResult confirmAlarm(Map<String, Object> map) {

		CommonResult result = new CommonResult();

		try{
			map.put("currentAlarmIds", transNumStr(map.get("currentAlarmIds").toString()));
			alarmManagementMapper.confirmAlarm(map);
			
			result.setReturnResult(CommonDefine.SUCCESS);

		}catch(Exception e){
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
		}

		return result;
	}
	
	/**
	 * 通过前台参数，分析出需要同步的RTU设备编号
	 * @param map
	 * @return
	 */
	private List<String> getSyncRtuNoList(Map<String, Object> map) {
		
		List<String> list = null;
		String eqptTypes = "(" + CommonDefine.EQUIP_TYPE_RTU + ")";
		map.put("eqptTypes", eqptTypes);
		List<Map<String, Object>> equipList = alarmManagementMapper.getAlarmSyncEquip(map);
		if(equipList != null && equipList.size() > 0) {
			list = new ArrayList<String>();
			for(Map<String, Object> o : equipList) {
				if(o.get("NUMBER") != null){
					list.add(o.get("NUMBER").toString());
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 同步告警
	 * @param map
	 * @return
	 */
	public Map<String, Object> syncAlarm(Map<String, Object> map) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<String> rtuNoList = getSyncRtuNoList(map);
		List<Map<String, Object>> detailMapList = new ArrayList<Map<String, Object>>();
		
		try{
			
			if(rtuNoList != null && rtuNoList.size() > 0) {
				detailMapList.addAll(syncRTUAlarm(rtuNoList));
			}
			
			result.put("returnResult", CommonDefine.SUCCESS);
			result.put("details", detailMapList);
			
		}catch(Exception e){
			e.printStackTrace();
			result.put("returnResult", CommonDefine.FAILED);
		}

		return result;
	}
	
//	//同步网管告警
//	public void syncEMSAlarm() throws SigarException {
//		
//		PingEqptThread te = new PingEqptThread();
//		//获取所有的离线告警
//		List<Map<String, Object>> offlineAlarmList = alarmManagementMapper.getAllOfflineAlarm();
//		
//		boolean flag = te.pingAll(offlineAlarmList);
//		boolean hasOfflineAlarm = false;
//		//判断数据库中是否已经存在所有设备离线告警
//		for(Map<String, Object> alarmMap : offlineAlarmList){
//			if(Integer.parseInt(alarmMap.get("EQPT_TYPE").toString()) == 7){
//				hasOfflineAlarm = true;
//				break;
//			}
//		}
//		if(flag){//所有设备离线
//			if(!hasOfflineAlarm){
//				addORTSOfflineAlarm();
//			}
//		}
//		
//		ServerPMThread ts = new ServerPMThread();
//		if(ts.isCpuAlarm()){
//			addCpuAlarm();
//		}else{
//			removeCpuAlarm();
//		}
//		
//		if(ts.isMemoryAlarm()){
//			addMemoryAlarm();
//		}else{
//			removeMemoryAlarm();
//		}
//		
//		if(ts.isDiskAlarm()){
//			addDiskAlarm();
//		}else{
//			removeDiskAlarm();
//		}
//	}
	
	private EqptInfoModel transRTU(Map<String, Object> rtu) {
		
		EqptInfoModel model = new EqptInfoModel();
		model.setFactory(rtu.get("FACTORY").toString());
		model.setRtuIp(rtu.get("IP").toString());
		model.setRtuPort(Integer.parseInt(rtu.get("PORT").toString()));
		model.setRcode(rtu.get("NUMBER").toString());
		
		return model;
	}
	
	public SysInfoModel getSysInfo() throws CommonException{
		SysInfoModel sysInfo = new SysInfoModel();
		
		try {
			
			Map sys_map = planMapper.getSysParam("SYS_IP");
			sysInfo.setNip(sys_map.get("PARAM_VALUE").toString());
			sys_map = planMapper.getSysParam("SYS_CODE");
			sysInfo.setNcode(sys_map.get("PARAM_VALUE").toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PLAN_DB_INFO_ERROR);
		}
		return sysInfo;
	}
	
	//同步RTU告警
	public List<Map<String, Object>> syncRTUAlarm(List<String> rtuNoList) throws CommonException {
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> detail = null;
		List<Map<String, Object>> rtuInfoList = alarmManagementMapper.getRCListByNo(rtuNoList, CommonDefine.EQUIP_TYPE_RTU);
		SysInfoModel sys = getSysInfo();
		EqptInfoModel eqpt = null;
		
		for(Map<String, Object> rtu : rtuInfoList){
			int rtuId = Integer.parseInt(rtu.get("RC_ID").toString());
			eqpt = transRTU(rtu);
			
			try {
				List<RTUAlarm> rtuAlarmList = equipmentTestManagerService.loadRTUAlarm(eqpt, sys, CommonDefine.COLLECT_LEVEL_1);
				List<Map<String, Object>> databaseAlarm = alarmManagementMapper.getEqptCurrAlarm(CommonDefine.EQUIP_TYPE_RTU, rtuId);
				for(RTUAlarm rtuAlarm : rtuAlarmList) {
					PushAlarmModel alarm = analyzeRTUAlarm(rtuAlarm);
					if(alarm == null) continue;
					
					boolean isAlarmExist = isRTUAlarmExist(alarm, databaseAlarm);
					if(isAlarmExist) {//老告警
						removeRTUAlarmInList(alarm, databaseAlarm);
					} else {//新告警
						alarmManagementMapper.addRTUCurAlarm(alarm);
					}
				}
				
				if(databaseAlarm != null && databaseAlarm.size() > 0) {//数据库中有脏数据
					for(Map<String, Object> map : databaseAlarm) {
						int alarmId = Integer.parseInt(map.get("ALARM_ID").toString());
						alarmManagementMapper.deleteCurAlarm(alarmId);
						addHistoryAlarm(map);
					}
				}
			} catch (CommonException e) {
				detail = new HashMap<String, Object>();
				detail.put("EQPT_NO", rtu.get("NUMBER").toString());
				detail.put("EQPT_NAME", rtu.get("NAME").toString());
				detail.put("MSG", e.getMessage());
				resultList.add(detail);
			}
		}
		
		return resultList;
	}
	
	//增加历史告警
	private void addHistoryAlarm(Map<String, Object> map) {
		
		map.put("ALARM_CLEAR_DATE", new Date());
		alarmManagementMapper.addHistoryAlarm(map);
	}
	
	//移除list中的指定RTU告警
	private void removeRTUAlarmInList(PushAlarmModel alarm, List<Map<String, Object>> alarmMapList) {
		
		for(Map<String, Object> map : alarmMapList){
			String alarmName = map.get("ALARM_NAME").toString();
			int slotNo = Integer.parseInt(map.get("SLOT_NO").toString());
			int portNo = Integer.parseInt(map.get("CARD_PORT").toString());
			int cardType = Integer.parseInt(map.get("CARD_TYPE").toString());
//			Date alarmOccurDate = (Date)map.get("ALARM_OCCUR_DATE");
			if(alarmName.equals(alarm.getAlarmName()) && 
			   slotNo == alarm.getSlotNo() && 
			   portNo == alarm.getPortNo() && 
			   cardType == alarm.getCardType()){
				alarmMapList.remove(map);//移除该条记录
				break;
			}
		}
	}
	
	//RTU告警是否存在
	private boolean isRTUAlarmExist(PushAlarmModel alarm, List<Map<String, Object>> alarmMapList) {
		
		boolean result = false;
		for(Map<String, Object> map : alarmMapList){
			String alarmName = map.get("ALARM_NAME").toString();
			int slotNo = Integer.parseInt(map.get("SLOT_NO").toString());
			int portNo = Integer.parseInt(map.get("CARD_PORT").toString());
			int cardType = Integer.parseInt(map.get("CARD_TYPE").toString());
//			Date alarmOccurDate = (Date)map.get("ALARM_OCCUR_DATE");
			if(alarmName.equals(alarm.getAlarmName()) && 
			   slotNo == alarm.getSlotNo() && 
			   portNo == alarm.getPortNo() && 
			   cardType == alarm.getCardType()){
				result = true;
				break;
			}
		}
		
		return result;
	}

	//分析RTU告警
	public PushAlarmModel analyzeRTUAlarm(RTUAlarm original) {
		
		PushAlarmModel alarm = null;
		//获取设备信息
		Map<String, Object> rtu = alarmManagementMapper.getRTUByNo(original.getRcode());
		if(rtu != null) {
			int rtuId = Integer.parseInt(rtu.get("RC_ID").toString());
			Map<String, Object> cardTypeMap = alarmManagementMapper.getCardType(rtuId, 
					Integer.parseInt(original.getAlarmSlot().trim()));
			if(cardTypeMap != null){
				int cardType = Integer.parseInt(cardTypeMap.get("CARD_TYPE").toString());
				String alarmName = analyzeRTUAlarmName(cardType, 
						Long.parseLong(original.getAlarmContent().trim()));
				if(alarmName != null){
					alarm = new PushAlarmModel();
					//设备ID
					alarm.setEqptId(Integer.parseInt(rtu.get("RC_ID").toString()));
					//机盘型号
					alarm.setCardType(cardType);
					//告警名称
					alarm.setAlarmName(alarmName);
					//告警类型
//					alarm.setAlarmType(getRCAlarmType(cardType, 
//								Integer.parseInt(original.getAlarmContent().trim())));
					//告警级别
					alarm.setAlarmLevel(getRTUAlarmLevel(cardType, 
							Integer.parseInt(original.getAlarmContent().trim())));
					//设备类型
//					int eqptType = type == 0 ? Define.ALARM_EQPT_TYPE_RTU : Define.ALARM_EQPT_TYPE_CTU;
					alarm.setEqptType(CommonDefine.EQUIP_TYPE_RTU);
					//槽道号
					alarm.setSlotNo(Integer.parseInt(original.getAlarmSlot().trim()));
					//端口号
					alarm.setPortNo(Integer.parseInt(original.getAlarmPort().trim()));
					//告警发生时间
					alarm.setAlarmOccurTime(transEqptDate(original.getAlarmTime()));
					//区域ID
//					alarm.setRegionId(Integer.parseInt(rc.get("REGION_ID").toString()));
					//产生消除标记
					alarm.setAlarmFlag(Integer.parseInt(original.getAlarmLevel().trim()));
				}
			}
		}
		
		return alarm;
	}
	
//	//获取RTU/CTU告警类型
//	public int getRCAlarmType(int cardType, int key){
//		
//		int alarmType = 0;
//		switch(cardType){
//		case Define.CARD_TYPE_PWR:
//			alarmType = AlarmParamsDefine.PWR_ALARM_TYPE.get(Long.valueOf(key)).intValue();
//			break;
//		case Define.CARD_TYPE_MCU:
//			alarmType = AlarmParamsDefine.MCU_ALARM_TYPE.get(Long.valueOf(key)).intValue();
//			break;
//		case Define.CARD_TYPE_OTDR:
//			alarmType = AlarmParamsDefine.OTDR_ALARM_TYPE.get(Long.valueOf(key)).intValue();
//			break;
//		case Define.CARD_TYPE_OSW:
//			alarmType = AlarmParamsDefine.OSW_ALARM_TYPE.get(Long.valueOf(key)).intValue();
//			break;
//		case Define.CARD_TYPE_OPM:
//			alarmType = AlarmParamsDefine.OPM_ALARM_TYPE.get(Long.valueOf(key)).intValue();
//			break;
//		case Define.CARD_TYPE_OLS:
//			alarmType = AlarmParamsDefine.OLS_ALARM_TYPE.get(Long.valueOf(key)).intValue();
//			break;
//		default:
//			break;
//		}
//		
//		return alarmType;
//	}
	
	//获取RTU告警级别
	public int getRTUAlarmLevel(int cardType, int key){
		
		int alarmLevel = 0;
		switch(cardType){
		case CommonDefine.CARD_TYPE_PWR:
			alarmLevel = CommonDefine.PWR_ALARM_LEVEL.get(Long.valueOf(key)).intValue();
			break;
		case CommonDefine.CARD_TYPE_MCU:
			alarmLevel = CommonDefine.MCU_ALARM_LEVEL.get(Long.valueOf(key)).intValue();
			break;
		case CommonDefine.CARD_TYPE_OTDR:
			alarmLevel = CommonDefine.OTDR_ALARM_LEVEL.get(Long.valueOf(key)).intValue();
			break;
		case CommonDefine.CARD_TYPE_OSW:
			alarmLevel = CommonDefine.OSW_ALARM_LEVEL.get(Long.valueOf(key)).intValue();
			break;
		default:
			break;
		}
		
		return alarmLevel;
	}
	
	/**
	 * 转换设备的时间
	 * @param dateString
	 * @return
	 */
	public Date transEqptDate(String dateString) {

		Date date = null;
		SimpleDateFormat spf = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			date = spf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	//分析RTU的告警名称
	public String analyzeRTUAlarmName(int cardType , Long key) {

		String alarmName = null;
		
		switch(cardType) {
		case CommonDefine.CARD_TYPE_PWR:
			alarmName = CommonDefine.PWR_ALARM_CONTENT.get(key);
			break;
		case CommonDefine.CARD_TYPE_MCU:
			alarmName = CommonDefine.MCU_ALARM_CONTENT.get(key);
			break;
		case CommonDefine.CARD_TYPE_OTDR:
			alarmName = CommonDefine.OTDR_ALARM_CONTENT.get(key);
			break;
		case CommonDefine.CARD_TYPE_OSW:
			alarmName = CommonDefine.OSW_ALARM_CONTENT.get(key);
			break;
		default:
			break;
		}

		return alarmName;
	}

//	//同步CTU告警
//	public List<Map<String, Object>> syncCTUAlarm(List<String> ctuNoList) {
//		
//		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> detail = null;
//		List<Map<String, Object>> ctuInfoList = alarmManagementMapper.
//												getCTUInfoList(ctuNoList);
//
//		for(Map<String, Object> ctu : ctuInfoList){
//			int ctuId = Integer.parseInt(ctu.get("RC_ID").toString());
//			String rtuIp = ctu.get("RC_IP").toString();
//			int rtuPort = Integer.parseInt(ctu.get("RC_PORT").toString());
//			String Rcode = ctu.get("RC_NO").toString();
//			
//			List<Map<String, Object>> sysConfigList = 
//					alarmManagementMapper.getSysConfigByConfigName("Nip");
//			
//			String Nip = sysConfigList.get(0).get("CONFIG_VALUE").toString();
//			Nip = CommonMethod.ipToDecString(Nip);
//			
//			String Amode = Define.ALARM_MODE_CURRENT;
//			Map<String, Object> currentMap = baseCmdExeService.loadRTUAlarm(rtuIp, 
//					rtuPort, Rcode, Nip, Amode);
//			boolean cmdReturn = (Boolean)currentMap.get("return");
//			if(cmdReturn){
//				List<CMDRTUAlarm> ctuAlarmList = (List<CMDRTUAlarm>)currentMap.get("result");
//				
//				List<Map<String, Object>> ctuAlarmInDB = alarmManagementMapper.
//						getEqptCurrAlarm(Define.ALARM_EQPT_TYPE_CTU, ctuId);
//				
//				for(CMDRTUAlarm ctuAlarm : ctuAlarmList){
//					PushAlarmModel alarm = analyzeRCAlarm(ctuAlarm);
//					if(alarm == null){
//						securityManagementService.log(Define.LOG_TYPE_ALARM_INFO, "未处理CTU告警", "CTU编号：" + ctuAlarm.getRcode());
//						continue;
//					}
//					
//					boolean isAlarmExist = isRCAlarmExist(alarm, ctuAlarmInDB);
//					if(isAlarmExist){//老告警
//						removeRCAlarmInList(alarm, ctuAlarmInDB);
//					}else{//新告警
//						alarmManagementMapper.addRCCurAlarm(alarm);
//					}
//				}
//				
//				if(ctuAlarmInDB != null && ctuAlarmInDB.size() > 0){//数据库中有脏数据
//					boolean offline = false;
//					for(Map<String, Object> map : ctuAlarmInDB){
//						int alarmId = Integer.parseInt(map.get("ALARM_ID").toString());
//						int alarmLevel = Integer.parseInt(map.get("ALARM_LEVEL").toString());
//						if(alarmLevel == Define.ALARM_LEVEL_OFFLINE){//有离线告警
//							offline = true;
//						}
//						alarmManagementMapper.deleteCurAlarm(alarmId);
//						addHistoryAlarm(map);
//					}
//					if(!offline){
//						alarmManagementMapper.visibleEqptCurrAlarm(Define.ALARM_EQPT_TYPE_CTU, ctuId);
//					}
//				}
//			}else{
//				detail = new HashMap<String, Object>();
//				detail.put("EQPT_NO", ctu.get("RC_NO").toString());
//				detail.put("EQPT_NAME", ctu.get("RC_NAME").toString());
//				detail.put("msg", currentMap.get("msg").toString());
//				resultList.add(detail);
//			}
//		}
//		
//		return resultList;
//	}
//
//	//同步OSM告警
//	public List<Map<String, Object>> syncOSMAlarm(List<String> osmNoList) {
//		
//		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> detail = null;
//		List<Map<String, Object>> osmInfoList = alarmManagementMapper.
//										getOSMInfoList(osmNoList);
//		
//		for(Map<String, Object> osm : osmInfoList){
//			int regionId = Integer.parseInt(osm.get("REGION_ID").toString());
//			int eqptId = Integer.parseInt(osm.get("OSM_ID").toString());
//			String osmIp = osm.get("OSM_IP").toString();
//			int port = Integer.parseInt(osm.get("OSM_PORT").toString());
//
//			Map<String, Object> currentMap = baseCmdExeService.
//								loadSpecificLightChannel(osmIp, port);
//			
//			boolean cmdReturn = (Boolean)currentMap.get("return");
//			if(cmdReturn){
//				List<CMDOSMLightChannel> osmAlarmList = 
//						(List<CMDOSMLightChannel>)currentMap.get("result");
//				//获取当前osm在数据库中的告警
//				List<Map<String, Object>> osmAlarmInDB = alarmManagementMapper.
//						getEqptCurrAlarm(Define.ALARM_EQPT_TYPE_OSM, eqptId);
//				
//				for(CMDOSMLightChannel osmAlarm : osmAlarmList){
//					PushAlarmModel alarm = OSMAlarm2PushAlarmModel(osmAlarm, regionId, eqptId);
//					if(alarm == null){
//						securityManagementService.log(Define.LOG_TYPE_ALARM_INFO, "未处理OSM告警", "OSM编号：" + osm.get("OSM_NO").toString());
//						continue;
//					}
//					//删除缓存中的osm告警
//					if(alarm.getAlarmValue() > 0 
//						&& alarm.getAlarmValue() < 7 
//						&& osmAlarmInDB != null){
//						deleteOSMAlarmInCache(alarm, osmAlarmInDB);
//					}
//					processOSMAlarm(alarm);
//				}
//				
//				//数据库中有脏数据
//				if(osmAlarmInDB != null && osmAlarmInDB.size() > 0){
//					boolean offline = false;
//					for(Map<String, Object> map : osmAlarmInDB){
//						int alarmId = Integer.parseInt(map.get("ALARM_ID").toString());
//						int alarmLevel = Integer.parseInt(map.get("ALARM_LEVEL").toString());
//						if(alarmLevel == Define.ALARM_LEVEL_OFFLINE){//有离线告警
//							offline = true;
//						}
//						alarmManagementMapper.deleteCurAlarm(alarmId);
//						addHistoryAlarm(map);
//					}
//					if(!offline){
//						alarmManagementMapper.visibleEqptCurrAlarm(Define.ALARM_EQPT_TYPE_OSM, eqptId);
//					}
//				}
//			}else{
//				detail = new HashMap<String, Object>();
//				detail.put("EQPT_NO", osm.get("OSM_NO").toString());
//				detail.put("EQPT_NAME", osm.get("OSM_NAME").toString());
//				detail.put("msg", currentMap.get("msg").toString());
//				resultList.add(detail);
//			}
//		}
//		
//		return resultList;
//	}
//	
//	//删除缓存中的osm告警
//	public void deleteOSMAlarmInCache(PushAlarmModel alarm, List<Map<String, Object>> osmAlarmList) {
//		
//		for(Map<String, Object> map : osmAlarmList){
//			int portNo = Integer.parseInt(map.get("CARD_PORT").toString());
//			String alarmName = map.get("ALARM_NAME").toString();
//			if(alarm.getPortNo() == portNo && 
//					alarm.getAlarmName().equals(alarmName)){
//				osmAlarmList.remove(map);
//				break;
//			}
//		}
//	}
//
//	//处理OSM告警
//	public void processOSMAlarm(PushAlarmModel model) {
//
//		List<Map<String, Object>> alarmMapList = null;
//		Map<String, Object> alarmMap = null;
//		int alarmValue = model.getAlarmValue();
//		switch(alarmValue){
//		case 0:
//			alarmMapList = alarmManagementMapper.getOSMCurrAlarmList(model);
//			if(alarmMapList != null && alarmMapList.size() > 0){
//				for(Map<String, Object> map : alarmMapList){
//					//将告警移动到历史告警表
//					addHistoryAlarm(map);
//					//删除当前告警表中的记录
//					alarmManagementMapper.deleteCurAlarm(Integer.
//							parseInt(map.get("ALARM_ID").toString()));
//					if(map.get("EQPT_TYPE") != null 
//						&& !"".equals(map.get("EQPT_TYPE").toString())
//						&&map.get("EQPT_ID") != null 
//						&& !"".equals(map.get("EQPT_ID").toString())){
//						dispatchAlarm(Integer.parseInt(map.get("EQPT_TYPE").toString()), 
//								Integer.parseInt(map.get("EQPT_ID").toString()));
//					}
//				}
//			}
//			break;
//		case 1:
//		case 2:
//		case 3:
//		case 4:
//		case 5:
//		case 6:
//			alarmMap = alarmManagementMapper.getOSMCurrAlarm(model);
//			if(alarmMap == null){
//				model.setAlarmOccurTime(new Date());
//				alarmManagementMapper.addOSMCurrAlarm(model);
//				dispatchAlarm(model.getEqptType(), model.getEqptId());
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//
//	/**
//	 * 将当前告警转换为PushAlarmModel
//	 * @param alarmMap
//	 * @return
//	 */
//	public PushAlarmModel transCurrAlarmMap2PushAlarmModel(Map<String, Object> alarmMap) {
//
//		PushAlarmModel result = new PushAlarmModel();
//
//		//设备类型
//		if(alarmMap.get("EQPT_TYPE") != null && !alarmMap.get("EQPT_TYPE").toString().equals("")){
//			result.setEqptType(Integer.parseInt(alarmMap.get("EQPT_TYPE").toString()));
//		}
//
//		//设备ID
//		if(alarmMap.get("EQPT_ID") != null && !alarmMap.get("EQPT_ID").toString().equals("")){
//			result.setEqptId(Integer.parseInt(alarmMap.get("EQPT_ID").toString()));
//		}
//
//		//槽道号
//		if(alarmMap.get("SLOT_NO") != null && !alarmMap.get("SLOT_NO").toString().equals("")){
//			result.setSlotNo(Integer.parseInt(alarmMap.get("SLOT_NO").toString()));
//		}
//
//		//端口号
//		if(alarmMap.get("CARD_PORT") != null && !alarmMap.get("CARD_PORT").toString().equals("")){
//			result.setPortNo(Integer.parseInt(alarmMap.get("CARD_PORT").toString()));
//		}
//
//		//机盘类型
//		if(alarmMap.get("CARD_TYPE") != null && !alarmMap.get("CARD_TYPE").toString().equals("")){
//			result.setCardType(Integer.parseInt(alarmMap.get("CARD_TYPE").toString()));
//		}
//
//		//告警级别
//		if(alarmMap.get("ALARM_LEVEL") != null && !alarmMap.get("ALARM_LEVEL").toString().equals("")){
//			result.setAlarmLevel(Integer.parseInt(alarmMap.get("ALARM_LEVEL").toString()));
//		}
//
//		return result;
//	}
//
//	//解析OSM告警
//	public PushAlarmModel OSMAlarm2PushAlarmModel(CMDOSMLightChannel channel, 
//			int regionId, int eqptId) {
//
//		PushAlarmModel alarm = null;
//		
//		//告警名称
//		String alarmName = AlarmParamsDefine.OSM_ALARM_CONTENT.get(channel.getCurrStatus());
//		if(alarmName != null){
//			alarm = new PushAlarmModel();
//			//设备ID
//			alarm.setEqptId(eqptId);
//			//告警值
//			alarm.setAlarmValue(channel.getCurrStatus().intValue());
//			//告警名称
//			alarm.setAlarmName(alarmName);
//			//告警类型
//			alarm.setAlarmType(AlarmParamsDefine.OSM_ALARM_TYPE.
//								get(channel.getCurrStatus()).intValue());
//			//告警级别
//			alarm.setAlarmLevel(AlarmParamsDefine.OSM_ALARM_LEVEL.
//								get(channel.getCurrStatus()).intValue());
//			//设备类型
//			alarm.setEqptType(Define.ALARM_EQPT_TYPE_OSM);
//			//槽道号
//			alarm.setSlotNo(14);
//			//端口号
//			alarm.setPortNo(Integer.parseInt(channel.getSlot()));
//			//区域ID
//			alarm.setRegionId(regionId);
//		}
//
//		return alarm;
//	}
//
//	//同步SFM告警
//	public List<Map<String, Object>> syncSFMAlarm(List<String> sfmNoList) {
//		
//		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> detail = null;
//		List<Map<String, Object>> sfmInfoList = alarmManagementMapper.
//									getSFMInfoList(sfmNoList);
//
//		for(Map<String, Object> sfm : sfmInfoList){
//			String Ip = sfm.get("SFM_IP").toString();
//			int Port = Integer.parseInt(sfm.get("SFM_PORT").toString());
//			String Scode = sfm.get("SFM_NO").toString();
//
//			Map<String, Object> resultMap = baseCmdExeService.loadSFMAlarm(Ip, Port, Scode);
//			boolean cmdReturn = (Boolean)resultMap.get("return");
//			if(!cmdReturn){
//				detail = new HashMap<String, Object>();
//				detail.put("EQPT_NO", sfm.get("SFM_NO").toString());
//				detail.put("EQPT_NAME", sfm.get("SFM_NAME").toString());
//				String msg = "";
//				if(resultMap.get("msg") != null){
//					msg = resultMap.get("msg").toString();
//				}
//				detail.put("msg", msg);
//				resultList.add(detail);
//			}
//		}
//		
//		return resultList;
//	}
//
//	/**
//	 * 设定告警屏蔽规则
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public CommonResult setAlarmShieldRules(Map<String, Object>map, int userId) {
//
//		CommonResult result = new CommonResult();
//
//		try{
//
//			if(map.get("regionId") != null){//设置设备告警、线路告警屏蔽规则
//				int regionId = Integer.parseInt(map.get("regionId").toString());
//				String eqptAlarmLevels = map.get("eqptAlarmLevels").toString();
//				if(eqptAlarmLevels.equals("")){
//					map.put("eqptAlarmLevels", null);
//				}
//				String linkAlarmLevels = map.get("linkAlarmLevels").toString();
//				if(linkAlarmLevels.equals("")){
//					map.put("linkAlarmLevels", null);
//				}
//
//				alarmManagementMapper.deleteELAlarmShieldRule(regionId, userId);
//				if(map.get("eqptAlarmLevels") != null ||
//						map.get("linkAlarmLevels") != null){
//					alarmManagementMapper.setELAlarmShieldRule(map, regionId, userId);
//				}
//			}
//
//
//			String emsAlarmLevels = map.get("emsAlarmLevels").toString();
//			alarmManagementMapper.deleteEMSAlarmShieldRule(userId);
//			if(!emsAlarmLevels.equals("")){
//				alarmManagementMapper.setEMSAlarmShieldRule(map, userId);
//			}
//
//			result.setReturnResult(Define.SUCCESS);
//		}catch(Exception e){
//			e.printStackTrace();
//			result.setReturnResult(Define.FAILED);
//		}
//
//		return result;
//	}
//
//	/**
//	 * 为导出报表查询当前告警
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public List<Map<String, Object>> queryCurrentAlarmForReport(Map<String, Object> map, int userId) {
//
//		List<Map<String, Object>> result = null;
//		try{
//			//整理查询条件
//			arrangeConds(map, userId);
//
//			int count = alarmManagementMapper.queryCurrentAlarmCount(map, userId);
//			result = alarmManagementMapper.queryCurrentAlarm(map, userId, 0, count);
//			for(Map<String, Object> alarm : result){
//				transAlarmForReport(alarm);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//
//	/**
//	 * 处理导出报表的告警数据
//	 * @param alarm
//	 */
//	private void transAlarmForReport(Map<String, Object> alarm) {
//
//		//确认状态
//		if(alarm.get("ACK_STATUS") != null && !alarm.get("ACK_STATUS").toString().equals("")){
//			int confirmStatus = Integer.parseInt(alarm.get("ACK_STATUS").toString());
//			if(confirmStatus == Define.ALARM_CONFIRM_STATUS_YES){
//				alarm.put("ACK_STATUS", "已确认");
//			}else if(confirmStatus == Define.ALARM_CONFIRM_STATUS_NO){
//				alarm.put("ACK_STATUS", "未确认");
//			}
//		}
//
//		//告警级别
//		if(alarm.get("ALARM_LEVEL") != null && !alarm.get("ALARM_LEVEL").toString().equals("")){
//			int alarmLevel = Integer.parseInt(alarm.get("ALARM_LEVEL").toString());
//			if(alarmLevel == Define.ALARM_LEVEL_CR){
//				alarm.put("ALARM_LEVEL", "紧急");
//			}else if(alarmLevel == Define.ALARM_LEVEL_MJ){
//				alarm.put("ALARM_LEVEL", "严重");
//			}else if(alarmLevel == Define.ALARM_LEVEL_MN){
//				alarm.put("ALARM_LEVEL", "一般");
//			}else if(alarmLevel == Define.ALARM_LEVEL_WR){
//				alarm.put("ALARM_LEVEL", "提示");
//			}else if(alarmLevel == Define.ALARM_LEVEL_OFFLINE){
//				alarm.put("ALARM_LEVEL", "离线");
//			}
//		}
//
//		//告警类型
//		if(alarm.get("ALARM_TYPE") != null && !alarm.get("ALARM_TYPE").toString().equals("")){
//			int alarmType = Integer.parseInt(alarm.get("ALARM_TYPE").toString());
//			if(alarmType == Define.ALARM_TYPE_EQPT){
//				alarm.put("ALARM_TYPE", "设备告警");
//			}else if(alarmType == Define.ALARM_TYPE_ROUTE){
//				alarm.put("ALARM_TYPE", "线路告警");
//			}else if(alarmType == Define.ALARM_TYPE_EMS){
//				alarm.put("ALARM_TYPE", "网管告警");
//			}
//		}
//
//		//设备类型
//		if(alarm.get("EQPT_TYPE") != null && !alarm.get("EQPT_TYPE").toString().equals("")){
//			int eqptType = Integer.parseInt(alarm.get("EQPT_TYPE").toString());
//			if(eqptType == Define.ALARM_EQPT_TYPE_RTU){
//				alarm.put("EQPT_TYPE", "RTU");
//			}else if(eqptType == Define.ALARM_EQPT_TYPE_CTU){
//				alarm.put("EQPT_TYPE", "CTU");
//			}else if(eqptType == Define.ALARM_EQPT_TYPE_OSM){
//				alarm.put("EQPT_TYPE", "OSM");
//			}else if(eqptType == Define.ALARM_EQPT_TYPE_SFM){
//				alarm.put("EQPT_TYPE", "SFM");
//			}
//		}
//
//		//机盘型号
//		if(alarm.get("CARD_TYPE") != null && !alarm.get("CARD_TYPE").toString().equals("")){
//			int cardType = Integer.parseInt(alarm.get("CARD_TYPE").toString());
//			if(cardType == Define.CARD_TYPE_PWR){
//				alarm.put("CARD_TYPE", "PWR");
//			}else if(cardType == Define.CARD_TYPE_MCU){
//				alarm.put("CARD_TYPE", "MCU");
//			}else if(cardType == Define.CARD_TYPE_OTDR){
//				alarm.put("CARD_TYPE", "OTDR");
//			}else if(cardType == Define.CARD_TYPE_OSW){
//				alarm.put("CARD_TYPE", "OSW");
//			}else if(cardType == Define.CARD_TYPE_OPM){
//				alarm.put("CARD_TYPE", "OPM");
//			}else if(cardType == Define.CARD_TYPE_OLS){
//				alarm.put("CARD_TYPE", "OLS");
//			}
//		}
//	}
//
//	/**
//	 * 为导出报表查询历史告警
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public List<Map<String, Object>> queryHistoryAlarmForReport(Map<String, Object> map, int userId) {
//
//		List<Map<String, Object>> result = null;
//
//		try{
//			//整理查询条件
//			arrangeCondsOfHistory(map, userId);
//
//			// 根据查询条件 查询总数
//			int count = alarmManagementMapper.queryHistoryAlarmCount(map);
//
//			result = alarmManagementMapper.queryHistoryAlarm(map, 0, count);
//
//			for(Map<String, Object> alarm : result){
//				transAlarmForReport(alarm);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//	/**
//	 * 获取告警屏蔽规则
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Object> getELShieldRule(Map<String, Object> map, int userId) {
//
//		Map<String, Object> rule = null;
//		try{
//			int regionId = Integer.parseInt(map.get("regionId").toString());
//
//			rule = alarmManagementMapper.getELShieldRule(regionId, userId);
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return rule;
//	}
//
//	/**
//	 * 获取网管告警屏蔽规则
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Object> getEMSShieldRule(int userId) {
//
//		Map<String, Object> rule = null;
//		try{
//
//			rule = alarmManagementMapper.getEMSShieldRule(userId);
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return rule;
//	}
//
//	/**
//	 * 获取区域内的机房信息
//	 * @param map
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Object> getStationsInRegion(Map<String, Object> map, int userId) {
//
//		Map<String, Object> result = new HashMap<String, Object>();
//
//		try{
//
//			String regionIds = map.get("regionIds").toString();
//			if(regionIds.equals("")){//查看所有机房
//				Map<String, Object> regionIdsInDB = alarmManagementMapper.getRegionIds(userId);
//				if(regionIdsInDB.get("REGION_ID") != null){
//					regionIds = alarmManagementMapper.getRegionIds(userId).
//							get("REGION_ID").toString();
//				}
//			}
//
//			List<Integer> regionIdList = transNumStr(regionIds);
//			List<Map<String, Object>> stations = null;
//
//			if(regionIdList != null && regionIdList.size() > 0){
//				map.put("regionIds", regionIdList);
//				stations = alarmManagementMapper.getStationsInRegion(map);
//			}
//
//			result.put("data", stations);
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//	/**
//	 * 获取前台combox用的区域信息
//	 * @return
//	 */
//	public Map<String, Object> queryRegionForCombox(int userId) {
//
//		Map<String, Object> result = new HashMap<String, Object>();
//
//		try{
//			List<Map<String, Object>> regionInfo = null;
//			Map<String, Object> regionIdMap = alarmManagementMapper.
//					getRegionIds(userId);
//			if(regionIdMap.get("REGION_ID") != null && 
//					!regionIdMap.get("REGION_ID").toString().equals("")){
//				String regionId = regionIdMap.get("REGION_ID").toString();
//				List<Integer> regionIdList = transNumStr(regionId);
//				regionInfo = alarmManagementMapper.queryRegionForCombox(regionIdList);
//			}
//
//			result.put("data", regionInfo);
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//	/**
//	 * 获取设备名称
//	 * @param map
//	 * @return
//	 */
//	public Map<String, Object> getEqptName(Map<String, Object> map, int userId) {
//
//		Map<String, Object> result = new HashMap<String, Object>();
//
//		try{
//			String regionIds = map.get("regionIds").toString();
//			String stationIds = map.get("stationIds").toString();
//			List<Map<String, Object>> eqptNames = null;
//
//			if(stationIds.equals("")){//未指定机房
//
//				if(regionIds.equals("")){//查看所有的设备名称
//					Map<String, Object> regionIdsInDB = alarmManagementMapper.
//							getRegionIds(userId);
//					if(regionIdsInDB.get("REGION_ID") != null){
//						regionIds = regionIdsInDB.get("REGION_ID").toString();
//					}
//				}
//				List<Integer> regionIdList = transNumStr(regionIds);
//
//				if(regionIdList != null && regionIdList.size() > 0){
//					map.put("regionIds", regionIdList);
//					eqptNames = alarmManagementMapper.getEqptNameByRegionIds(map);
//				}
//			}else{//指定了机房
//				List<Integer> stationIdList = transNumStr(stationIds);
//				if(stationIdList != null && stationIdList.size() > 0){
//					map.put("stationIds", stationIdList);
//					eqptNames = alarmManagementMapper.getEqptNameByStationIds(map);
//				}
//			}
//
//			result.put("data", eqptNames);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return result;
//	}

	/**
	 * 获取前台同步告警时所需的设备信息
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> getAlarmSyncEquip(Map<String, Object> map) {

		Map<String, Object> result = new HashMap<String, Object>();

		try{
			String eqptTypes = "(" + CommonDefine.EQUIP_TYPE_RTU + ")";
			map.put("eqptTypes", eqptTypes);
			if(map.get("areaId") != null) {
				int areaId = Integer.parseInt(map.get("areaId").toString());
				List areaIds = areaManagerService.getSubAreaIds(areaId);
				if(areaIds == null) {
					areaIds = new ArrayList();
				}
				areaIds.add(areaId);
				map.put("areaIds", areaIds);
			}
			
			List<Map<String, Object>> equipList = alarmManagementMapper.getAlarmSyncEquip(map);
			result.put("rows", equipList);
			
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}
	
//	/**
//	 * 通过测试结果ID获取测试计划ID
//	 * @param map
//	 * @return
//	 */
//	public Map<String, Object> getPlanIdByTestResultId(Map<String, Object> map) {
//		
//		Map<String, Object> result = new HashMap<String, Object>();
//		int testResultId = Integer.parseInt(map.get("testResultId").toString());
//		
//		Map<String, Object> testResultMap = alarmManagementMapper.
//										getTestResultById(testResultId);
//		
//		if(testResultMap == null){
//			result.put("result", Define.FAILED);
//			result.put("message", "测试结果不存在！");
//		}else{
//			int planId = 0;
//			if(testResultMap.get("TEST_PLAN_ID") != null 
//					&& !testResultMap.get("TEST_PLAN_ID").toString().equals("")){
//				planId = Integer.parseInt(testResultMap.get("TEST_PLAN_ID").toString());
//				result.put("result", Define.SUCCESS);
//				result.put("planId", planId);
//			}else{
//				result.put("result", Define.FAILED);
//				result.put("message", "测试计划不存在！");
//			}
//		}
//		
//		return result;
//	}
//
//	/**
//	 * 整理查询条件
//	 * @param map
//	 */
//	private void arrangeConds(Map<String, Object> map, int userId) {
//		
//		List<Integer> regionIdList = null;
//		String regionIds = map.get("regionIds").toString();
//		if("".equals(regionIds)){
//			Map<String, Object> regionIdsMap = alarmManagementMapper.getRegionIds(userId);
//			if(regionIdsMap.get("REGION_ID") != null){
//				regionIds = regionIdsMap.get("REGION_ID").toString();
//			}
//		}
//		regionIdList = transNumStr(regionIds);
//		if(regionIdList == null){
//			regionIdList = new ArrayList<Integer>();
//		}
//		//加入根节点ID
//		Map<String, Object> root = alarmManagementMapper.getRoot();
//		if(root != null){
//			regionIdList.add(Integer.parseInt(root.get("REGION_ID").toString()));
//		}
//		
//		map.put("regionIds", regionIdList);
//
//		String eqptNames = map.get("eqptNames").toString();
//		String[] eqptNameArr = null;
//		if(!eqptNames.equals("")){
//			eqptNameArr = eqptNames.split(",");
//		}
//		map.put("eqptNames", eqptNameArr);
//		
//		String alarmLevels = map.get("alarmLevels").toString();
//		if(alarmLevels.contains(String.valueOf(Define.ALARM_LEVEL_MJ))){
//			alarmLevels = alarmLevels + "," +  Define.ALARM_LEVEL_OFFLINE;
//		}
//		map.put("alarmLevels", transNumStr(alarmLevels));
//		map.put("stationIds", transNumStr(map.get("stationIds").toString()));
//		map.put("eqptTypes", transNumStr(map.get("eqptTypes").toString()));
//		map.put("confirmStates", transNumStr(map.get("confirmStates").toString()));
//		map.put("alarmTypes", transNumStr(map.get("alarmTypes").toString()));
//	}
//	
//	/**
//	 * 整理跳转时的查询条件
//	 * @param map
//	 */
//	private void arrangeCondsForSkip(Map<String, Object> map) {
//		
//		if(map.get("externalNodeType") != null && 
//				!map.get("externalNodeType").toString().equals("")){
//			int nodeType = Integer.parseInt(map.get("externalNodeType").toString());
//			int nodeId = Integer.parseInt(map.get("externalNodeId").toString());
//			switch(nodeType){
//			case Define.TOPO_NODE_TYPE_REGION:
//				map.put("regionId", nodeId);
//				break;
//			case Define.TOPO_NODE_TYPE_STATION:
//				map.put("stationId", nodeId);
//				break;
//			case Define.TOPO_NODE_TYPE_RTU:
//				map.put("eqptId", nodeId);
//				map.put("eqptType", Define.ALARM_EQPT_TYPE_RTU);
//				break;
//			case Define.TOPO_NODE_TYPE_CTU:
//				map.put("eqptId", nodeId);
//				map.put("eqptType", Define.ALARM_EQPT_TYPE_CTU);
//				break;
//			case Define.TOPO_NODE_TYPE_OSM:
//				map.put("eqptId", nodeId);
//				map.put("eqptType", Define.ALARM_EQPT_TYPE_OSM);
//				break;
//			case Define.TOPO_NODE_TYPE_SFM:
//				map.put("eqptId", nodeId);
//				map.put("eqptType", Define.ALARM_EQPT_TYPE_SFM);
//				break;
//			default:
//				break;
//			}
//		}else if(map.get("externalLinkInfo") != null && 
//				!map.get("externalLinkInfo").toString().equals("")){
//			String[] linkInfoArray = map.get("externalLinkInfo").toString().split(";");
//			List<String> linkInfoList = new ArrayList<String>();
//			for(String linkInfo : linkInfoArray){
//				processLinkInfo(linkInfoList, linkInfo);
//			}
//			
//			map.put("linkInfoList", linkInfoList);
//		}
//	}
//	
//	//处理拓扑图上传来的link字符串
//	private void processLinkInfo(List<String> linkInfoList, String linkInfo) {
//		
//		String[] array = linkInfo.split(",");
//		String str1 = "";
//		String str2 = "";
//		for(int i=0;i<array.length;i++){
//			if(i<5){
//				str1 = str1 + array[i] + ",";
//			}else{
//				str2 = str2 + array[i] + ",";
//			}
//		}
//		str1 = str1.substring(0, str1.length()-1);
//		str2 = str2.substring(0, str2.length()-1);
//		
//		linkInfoList.add(processCardType(str1));
//		linkInfoList.add(processCardType(str2));
//	}
//	
//	//查看拓扑连线告警时，将cardType为0的值转为-1
//	private String processCardType(String original) {
//		
//		String[] array = original.split(",");
//		String result = "";
//		for(int i=0;i<array.length;i++){
//			if(i==2 && array[2].equals("0")){
//				result = result + "-1,";
//			}else{
//				result = result + array[i] + ",";
//			}
//		}
//		result = result.substring(0, result.length()-1);
//		
//		return result;
//	}
	
	/**
	 * 将前台传来的逗号分隔的数字转换为list
	 * @param numStr
	 * @return
	 */
	private List<Integer> transNumStr(String numStr) {

		List<Integer> result = null;

		if(!numStr.equals("")){
			String[] numArr = numStr.split(",");
			result = new ArrayList<Integer>();
			for(int i=0;i<numArr.length;i++){
				result.add(Integer.parseInt(numArr[i]));
			}
		}

		return result;
	}

	/**
	 * 转换当前告警
	 * @param map
	 * @return
	 */
	private AlarmModel transCurrentAlarm(Map<String, Object> map) {

		AlarmModel model = new AlarmModel();

		//alarmId
		model.setAlarmId(Integer.parseInt(map.get("ALARM_ID").toString()));

		//确认状态
		if(map.get("ACK_STATUS") != null && !map.get("ACK_STATUS").toString().equals("")){
			model.setACK_STATUS(Integer.parseInt(map.get("ACK_STATUS").toString()));
		}

		//测试结果ID
		if(map.get("TEST_RESULT_ID") != null){
			model.setTEST_RESULT_ID(Integer.parseInt(map.get("TEST_RESULT_ID").toString()));
		}

		//告警级别
		if(map.get("ALARM_LEVEL") != null && !map.get("ALARM_LEVEL").toString().equals("")){
			model.setALARM_LEVEL(Integer.parseInt(map.get("ALARM_LEVEL").toString()));
		}

		//告警内容
		if(map.get("ALARM_NAME") != null && !map.get("ALARM_NAME").toString().equals("")){
			model.setContent(map.get("ALARM_NAME").toString());
		}

		//告警类型
		if(map.get("ALARM_TYPE") != null && !map.get("ALARM_TYPE").toString().equals("")){
			model.setALARM_TYPE(Integer.parseInt(map.get("ALARM_TYPE").toString()));
		}

		//区域
		if(map.get("AREA_NAME") != null && !map.get("AREA_NAME").toString().equals("")){
			model.setREGION_NAME(map.get("AREA_NAME").toString());
		}
		
		//局站
		if(map.get("STATION_NAME") != null && !map.get("STATION_NAME").toString().equals("")) {
			model.setStationName(map.get("STATION_NAME").toString());
		}

		//设备编号
		if(map.get("EQPT_NO") != null && !map.get("EQPT_NO").toString().equals("")){
			model.setEQPT_NO((map.get("EQPT_NO").toString()));
		}

		//设备名称
		if(map.get("EQPT_NAME") != null && !map.get("EQPT_NAME").toString().equals("")){
			model.setEQPT_NAME(map.get("EQPT_NAME").toString());
		}

		//设备类型
		if(map.get("EQPT_TYPE") != null && !map.get("EQPT_TYPE").toString().equals("")){
			model.setEQPT_TYPE(Integer.parseInt(map.get("EQPT_TYPE").toString()));
		}

		//槽道号
		if(map.get("SLOT_NO") != null && !map.get("SLOT_NO").toString().equals("")){
			model.setSLOT_NO(map.get("SLOT_NO").toString());
		}

		//机盘型号
		if(map.get("CARD_TYPE") != null && !map.get("CARD_TYPE").toString().equals("")){
			model.setCARD_TYPE(map.get("CARD_TYPE").toString());
		}

		//设备端口
		if(map.get("CARD_PORT") != null && !map.get("CARD_PORT").toString().equals("")){
			model.setCARD_PORT(map.get("CARD_PORT").toString());
		}

		//测试链路信息
		if(map.get("TEST_PLAN_ID") != null && !map.get("TEST_PLAN_ID").toString().equals("")){
			model.setTestLinkInfo(map.get("TEST_PLAN_ID").toString());
		}

		//发生时间
		if(map.get("ALARM_OCCUR_DATE") != null && 
				!map.get("ALARM_OCCUR_DATE").toString().equals("")){
			String alarmOccurDate = map.get("ALARM_OCCUR_DATE").toString();
			model.setALARM_OCCUR_DATE(alarmOccurDate.substring(0, alarmOccurDate.length()-2));
		}

		//确认时间
		if(map.get("ACK_DATE") != null && !map.get("ACK_DATE").toString().equals("")){
			String ackDate = map.get("ACK_DATE").toString();
			model.setACK_DATE(ackDate.substring(0, ackDate.length()-2));
		}

		//确认者
		if(map.get("USER_NAME") != null && !map.get("USER_NAME").toString().equals("")){
			model.setUSER_NAME(map.get("USER_NAME").toString());
		}
		
		//断点信息
		if(map.get("BREAK_POINT_INFO") != null && !map.get("BREAK_POINT_INFO").toString().equals("")){
			model.setBreakPointInfo(map.get("BREAK_POINT_INFO").toString());
		}

		return model;
	}

	/**
	 * 将历史告警信息由map转为model
	 * @param map
	 * @return
	 */
	private AlarmModel transHistoryAlarm(Map<String, Object> map) {

		AlarmModel model = new AlarmModel();

		//alarmId
		model.setAlarmId(Integer.parseInt(map.get("ALARM_ID").toString()));
		
		//测试结果ID
		if(map.get("TEST_RESULT_ID") != null && !map.get("TEST_RESULT_ID").toString().equals("")){
			model.setTEST_RESULT_ID(Integer.parseInt(map.get("TEST_RESULT_ID").toString()));
		}

		//确认状态
		if(map.get("ACK_STATUS") != null && !map.get("ACK_STATUS").toString().equals("")){
			model.setACK_STATUS(Integer.parseInt(map.get("ACK_STATUS").toString()));
		}

		//告警级别
		if(map.get("ALARM_LEVEL") != null && !map.get("ALARM_LEVEL").toString().equals("")){
			model.setALARM_LEVEL(Integer.parseInt(map.get("ALARM_LEVEL").toString()));
		}

		//告警内容
		if(map.get("ALARM_NAME") != null && !map.get("ALARM_NAME").toString().equals("")){
			model.setContent(map.get("ALARM_NAME").toString());
		}

		//告警类型
		if(map.get("ALARM_TYPE") != null && !map.get("ALARM_TYPE").toString().equals("")){
			model.setALARM_TYPE(Integer.parseInt(map.get("ALARM_TYPE").toString()));
		}

		//区域
		if(map.get("AREA_NAME") != null && !map.get("AREA_NAME").toString().equals("")){
			model.setREGION_NAME(map.get("AREA_NAME").toString());
		}
		
		//局站
		if(map.get("STATION_NAME") != null && !map.get("STATION_NAME").toString().equals("")) {
			model.setStationName(map.get("STATION_NAME").toString());
		}

		//设备编号
		if(map.get("EQPT_NO") != null && !map.get("EQPT_NO").toString().equals("")){
			model.setEQPT_NO((map.get("EQPT_NO").toString()));
		}
		
		//设备名称
		if(map.get("EQPT_NAME") != null){
			model.setEQPT_NAME(map.get("EQPT_NAME").toString());
		}

		//设备类型
		if(map.get("EQPT_TYPE") != null && !map.get("EQPT_TYPE").toString().equals("")){
			model.setEQPT_TYPE(Integer.parseInt(map.get("EQPT_TYPE").toString()));
		}

		//槽道号
		if(map.get("SLOT_NO") != null && !map.get("SLOT_NO").toString().equals("")){
			model.setSLOT_NO(map.get("SLOT_NO").toString());
		}

		//机盘型号
		if(map.get("CARD_TYPE") != null && !map.get("CARD_TYPE").toString().equals("")){
			model.setCARD_TYPE(map.get("CARD_TYPE").toString());
		}

		//设备端口
		if(map.get("CARD_PORT") != null && !map.get("CARD_PORT").toString().equals("")){
			model.setCARD_PORT(map.get("CARD_PORT").toString());
		}

		//测试链路信息
		if(map.get("TEST_PLAN_ID") != null && !map.get("TEST_PLAN_ID").toString().equals("")){
			model.setTestLinkInfo(map.get("TEST_PLAN_ID").toString());
		}

		//发生时间
		if(map.get("ALARM_OCCUR_DATE") != null && 
				!map.get("ALARM_OCCUR_DATE").toString().equals("")){
			String alarmOccurDate = map.get("ALARM_OCCUR_DATE").toString();
			model.setALARM_OCCUR_DATE(alarmOccurDate.substring(0, alarmOccurDate.length()-2));
		}

		//确认时间
		if(map.get("ACK_DATE") != null && !map.get("ACK_DATE").toString().equals("")){
			String ackDate = map.get("ACK_DATE").toString();
			model.setACK_DATE(ackDate.substring(0, ackDate.length()-2));
		}

		//结束时间
		if(map.get("ALARM_CLEAR_DATE") != null && 
				!map.get("ALARM_CLEAR_DATE").toString().equals("")){
			String alarmClearDate = map.get("ALARM_CLEAR_DATE").toString();
			model.setALARM_CLEAR_DATE(alarmClearDate.substring(0, alarmClearDate.length()-2));
		}

		//确认者
		if(map.get("USER_NAME") != null && !map.get("USER_NAME").toString().equals("")){
			model.setUSER_NAME(map.get("USER_NAME").toString());
		}
		
		//断点信息
		if(map.get("BREAK_POINT_INFO") != null && !map.get("BREAK_POINT_INFO").toString().equals("")){
			model.setBreakPointInfo(map.get("BREAK_POINT_INFO").toString());
		}

		return model;
	}
	
//	public List<CMDRTUAlarm> addAlarm() {
//		
//		List<CMDRTUAlarm> list = new ArrayList<CMDRTUAlarm>();
//		
//		CMDRTUAlarm alarm = new CMDRTUAlarm();
//		alarm.setRcode("RTU0000001");
//		alarm.setAlarmSlot("1");
//		alarm.setAlarmContent("1");
//		alarm.setAlarmPort("1");
//		alarm.setAlarmTime("20150115102030");
//		alarm.setAlarmLevel("1");
//		
//		list.add(alarm);
//		
//		return list;
//	}
//	
//	public List<CMDRTUAlarm> minusAlarm() {
//		
//		List<CMDRTUAlarm> list = new ArrayList<CMDRTUAlarm>();
//		
//		CMDRTUAlarm alarm = new CMDRTUAlarm();
//		alarm.setRcode("RTU0000001");
//		alarm.setAlarmSlot("1");
//		alarm.setAlarmContent("1");
//		alarm.setAlarmPort("1");
//		alarm.setAlarmTime("20150115102030");
//		alarm.setAlarmLevel("0");
//		
//		list.add(alarm);
//		
//		return list;
//	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handleRTUPushAlarm(Map map) {
		
		List<RTUAlarm> alarmList = (ArrayList<RTUAlarm>)map.get("alarmMapList");
		for(RTUAlarm rtuAlarm : alarmList) {
			//先分析出当前告警
			PushAlarmModel alarm = analyzeRTUAlarm(rtuAlarm);
			if(alarm == null) {
				continue;
			}
			if(alarm.getAlarmFlag() == CommonDefine.ALARM_FLAG_REMOVE) {
				removeRTUAlarm(alarm);
			} else if(alarm.getAlarmFlag() == CommonDefine.ALARM_FLAG_PRODUCE) {
				produceRTUAlarm(alarm);
			}
		}
	}

	//产生RTU告警
	private void produceRTUAlarm(PushAlarmModel alarm) {

		Map<String, Object> currentAlarm = null;
		currentAlarm = alarmManagementMapper.getRTUCurrentAlarm(alarm);

		if(currentAlarm == null) {
			alarmManagementMapper.addRTUCurAlarm(alarm);
			this.updateCurrentAlarmPage();
		}
	}

	//消除RTU告警
	private void removeRTUAlarm(PushAlarmModel alarm) {

		Map<String, Object> currentAlarm = null;
		currentAlarm = alarmManagementMapper.getRTUCurrentAlarm(alarm);
		
		if(currentAlarm != null){
			//将告警移动到历史告警表
			addHistoryAlarm(currentAlarm);
			//删除当前告警表中的记录
			alarmManagementMapper.deleteCurAlarm(Integer.parseInt(currentAlarm.get("ALARM_ID").toString()));
			this.updateCurrentAlarmPage();
		}
	}

//	//向地图、拓扑和告警模块派发告警
//	public void dispatchAlarm(int eqptType, int eqptId) {
//		
//		//地图模块
//		Map<String, Object> eqpt = null;
//		switch(eqptType){
//		case Define.ALARM_EQPT_TYPE_RTU:
//		case Define.ALARM_EQPT_TYPE_CTU:
//			eqpt = alarmManagementMapper.getRCByRCId(eqptId);
//			break;
//		case Define.ALARM_EQPT_TYPE_OSM:
//			eqpt = alarmManagementMapper.getOSMByOSMId(eqptId);
//			break;
//		case Define.ALARM_EQPT_TYPE_SFM:
//			eqpt = alarmManagementMapper.getSFMBySFMId(eqptId);
//			break;
//		default:
//			break;
//		}
//		if(eqpt != null && 
//			eqpt.get("STATION_ID") != null 
//			&& !eqpt.get("STATION_ID").toString().equals("")){
//			int stationId = Integer.parseInt(eqpt.get("STATION_ID").toString());
//			int alarmLevel = calStationAlarmLevel(stationId);
//			Map<String, Object> gisAlarm = new HashMap<String, Object>();
//			gisAlarm.put("stationId", stationId);
//			gisAlarm.put("alarmLevel", alarmLevel);
//			gisAlarm.put("type", Define.ALARM_LEVEL);
//			this.updateGisMap(JSONObject.fromObject(gisAlarm));
//		}
//
//		//告警模块
//		this.updateCurrentAlarmPage();
//		
//		//首页
//		this.updateMainPageAlarmCount();
//	}
//
//
//	/**
//	 * 计算机房的告警级别
//	 * @param stationId
//	 * @return
//	 */
//	public int calStationAlarmLevel(int stationId) {
//
//		int alarmLevel = Define.ALARM_LEVEL_CL;
//		AlarmCountModel alarmCount = getStationAlarmCount(stationId);
//		if(alarmCount.isOffline()){
//			alarmLevel = Define.ALARM_LEVEL_OFFLINE;
//		}else{
//			alarmLevel = calMaxAlarmLevel(alarmCount);
//		}
//
//		return alarmLevel;
//	}
//
//	/**
//	 * 计算最高级别告警
//	 * @param model
//	 * @return
//	 */
//	public int calMaxAlarmLevel(AlarmCountModel model) {
//
//		int maxAlarmLevel = Define.ALARM_LEVEL_CL;
//
//		if(model.getCr() > 0){
//			maxAlarmLevel = Define.ALARM_LEVEL_CR;
//		}else if(model.getCr() == 0 && model.getMj() > 0){
//			maxAlarmLevel = Define.ALARM_LEVEL_MJ;
//		}else if(model.getCr() == 0 && model.getMj() == 0 && model.getMn() > 0){
//			maxAlarmLevel = Define.ALARM_LEVEL_MN;
//		}else if(model.getCr() == 0 && model.getMj() == 0 && model.getMn() == 0 && model.getWr() > 0){
//			maxAlarmLevel = Define.ALARM_LEVEL_WR;
//		}
//
//		return maxAlarmLevel;
//	}
//
//	/**
//	 * 将SFM通道告警转换为PushAlarmModel
//	 * @param channel
//	 * @param alarmValue
//	 * @param transRecvFlag 1-发送 2-接收
//	 * @return
//	 */
//	public PushAlarmModel SFMChannelAlarm2PushAlarmModel(CMDSFMChannel channel, 
//			Long alarmValue, int transRecvFlag) {
//		
//		PushAlarmModel alarm = null;
//		//获取SFM信息
//		Map<String, Object> sfm = alarmManagementMapper.getSFMBySFMNo(channel.getScode());
//		if(sfm != null){
//			String alarmName = null;
//			if(transRecvFlag == Define.ALARM_FLAG_TRANS){
//				alarmName = AlarmParamsDefine.SFM_CHANNEL_TRANS_ALARM_CONTENT.get(alarmValue);
//			}else if(transRecvFlag == Define.ALARM_FLAG_RECV){
//				alarmName = AlarmParamsDefine.SFM_CHANNEL_RECV_ALARM_CONTENT.get(alarmValue);
//			}
//			
//			if(alarmName != null){
//				alarm = new PushAlarmModel();
//				//设备ID
//				alarm.setEqptId(Integer.parseInt(sfm.get("SFM_ID").toString()));
//				//告警值
//				alarm.setAlarmValue(alarmValue.intValue());
//				//发送接收标记
//				alarm.setTransRecvFlag(transRecvFlag);
//				//告警名称
//				alarm.setAlarmName(alarmName);
//				//告警类型
//				int alarmType = AlarmParamsDefine.SFM_CHANNEL_ALARM_TYPE.get(alarmValue).intValue();
//				alarm.setAlarmType(alarmType);
//				//告警级别
//				int alarmLevel = AlarmParamsDefine.SFM_CHANNEL_ALARM_LEVEL.get(alarmValue).intValue();
//				alarm.setAlarmLevel(alarmLevel);
//				//设备类型
//				alarm.setEqptType(Define.ALARM_EQPT_TYPE_SFM);
//				//槽道号
//				alarm.setSlotNo(Integer.parseInt(channel.getSlot().trim()));
//				//端口号
//				alarm.setPortNo(Integer.parseInt(channel.getPort().trim()));
//				//告警发生时间
//				alarm.setAlarmOccurTime(transEqptDate(channel.getOccurTime()));
//				//区域ID
//				alarm.setRegionId(Integer.parseInt(sfm.get("REGION_ID").toString()));
//			}
//		}
//		
//		return alarm;
//	}
//
//	//解析SFM通道告警
//	public String analyzeSFMChannelAlarm(CMDSFMChannel channel)
//	{
//		//先分析出当前告警
//		PushAlarmModel transAlarm = SFMChannelAlarm2PushAlarmModel(channel, 
//				Long.parseLong(channel.getSendConStatus().trim()), 1);
//		PushAlarmModel recvAlarm = SFMChannelAlarm2PushAlarmModel(channel, 
//				Long.parseLong(channel.getReceiveConStatus().trim()), 2);
//
//		if(transAlarm != null){
//			processSFMChannelAlarm(transAlarm);
//		}else{
//			securityManagementService.log(1, Define.LOG_TYPE_ALARM_INFO, "未处理SFM告警", "SFM编号：" + channel.getScode());
//		}
//		if(recvAlarm != null){
//			processSFMChannelAlarm(recvAlarm);
//		}else{
//			securityManagementService.log(1, Define.LOG_TYPE_ALARM_INFO, "未处理SFM告警", "SFM编号：" + channel.getScode());
//		}
//
//		return null;
//	}
//
//	//处理SFM通道告警
//	public void processSFMChannelAlarm(PushAlarmModel alarm) {
//
//		List<Map<String, Object>> alarmMapList = null;
//		Map<String, Object> alarmMap = null;
//		int alarmValue = alarm.getAlarmValue();
//		switch(alarmValue){
//		case 0:
//			alarmMapList = alarmManagementMapper.getSFMChannelAlarmList(alarm);
//			if(alarmMapList != null && alarmMapList.size() > 0){
//				for(Map<String, Object> map : alarmMapList){
//					//将告警移动到历史告警表
//					addHistoryAlarm(map);
//					//删除当前告警表中的记录
//					alarmManagementMapper.deleteCurAlarm(Integer.
//							parseInt(map.get("ALARM_ID").toString()));
//					if(map.get("EQPT_TYPE") != null 
//							&& !"".equals(map.get("EQPT_TYPE").toString())
//							&&map.get("EQPT_ID") != null 
//							&& !"".equals(map.get("EQPT_ID").toString())){
//							dispatchAlarm(Integer.parseInt(map.get("EQPT_TYPE").toString()), 
//									Integer.parseInt(map.get("EQPT_ID").toString()));
//					}
//				}
//			}
//			break;
//		case 1:
//		case 2:
//		case 3:
//		case 4:
//		case 5:
//			alarmMap = alarmManagementMapper.getSFMChannelAlarm(alarm);
//			if(alarmMap == null){
//				alarmManagementMapper.addSFMChannelCurAlarm(alarm);
//				dispatchAlarm(alarm.getEqptType(), alarm.getEqptId());
//			}
//			break;
//		case 6:
//			alarmMapList = alarmManagementMapper.getSFMChannelAlarmList(alarm);
//			if(alarmMapList != null && alarmMapList.size() > 0){
//				for(Map<String, Object> map : alarmMapList){
//					int value = Integer.parseInt(map.get("ALARM_VALUE").toString());
//					if(value != 5){
//						//将告警移动到历史告警表
//						addHistoryAlarm(map);
//						//删除当前告警表中的记录
//						alarmManagementMapper.deleteCurAlarm(Integer.
//								parseInt(map.get("ALARM_ID").toString()));
//						if(map.get("EQPT_TYPE") != null 
//								&& !"".equals(map.get("EQPT_TYPE").toString())
//								&&map.get("EQPT_ID") != null 
//								&& !"".equals(map.get("EQPT_ID").toString())){
//								dispatchAlarm(Integer.parseInt(map.get("EQPT_TYPE").toString()), 
//										Integer.parseInt(map.get("EQPT_ID").toString()));
//						}
//					}
//				}
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//	//解析SFM设备告警
//	public PushAlarmModel SFMAlarm2PushAlarmModel(CMDSFMChannel channel) {
//
//		PushAlarmModel alarm = null;
//		//获取SFM信息
//		Map<String, Object> sfm = alarmManagementMapper.getSFMBySFMNo(channel.getScode());
//		if(sfm != null){
//			String alarmName = AlarmParamsDefine.SFM_ALARM_CONTENT.
//					get(Long.parseLong(channel.getAlarmContent()));
//			if(alarmName != null){
//				alarm = new PushAlarmModel();
//				//设备ID
//				alarm.setEqptId(Integer.parseInt(sfm.get("SFM_ID").toString()));
//				//告警值
//				alarm.setAlarmValue(Integer.parseInt(channel.getAlarmContent()));
//				//告警名称
//				alarm.setAlarmName(alarmName);
//				//告警类型
//				alarm.setAlarmType(AlarmParamsDefine.SFM_ALARM_TYPE.get(Long.parseLong(channel.getAlarmContent())).intValue());
//				//告警级别
//				alarm.setAlarmLevel(AlarmParamsDefine.SFM_ALARM_LEVEL.get(Long.parseLong(channel.getAlarmContent())).intValue());
//				//设备类型
//				alarm.setEqptType(Define.ALARM_EQPT_TYPE_SFM);
//				//槽道号
//				alarm.setSlotNo(Integer.parseInt(channel.getSlot()));
//				//端口号
//				alarm.setPortNo(Integer.parseInt(channel.getPort()));
//				//发生时间
//				alarm.setAlarmOccurTime(transEqptDate(channel.getOccurTime()));
//				//区域ID
//				alarm.setRegionId(Integer.parseInt(sfm.get("REGION_ID").toString()));
//			}
//		}
//		
//		return alarm;
//	}
//
//	//解析SFM设备告警
//	public String analyzeSFMAlarm(CMDSFMChannel channel)
//	{
//		PushAlarmModel alarm = SFMAlarm2PushAlarmModel(channel);
//		if(alarm != null){
//			processSFMAlarm(alarm);
//		}else{
//			securityManagementService.log(1, Define.LOG_TYPE_ALARM_INFO, "未处理SFM告警", "SFM编号：" + channel.getScode());
//		}
//
//		return null;
//	}
//
//
//	//处理SFM设备告警
//	public void processSFMAlarm(PushAlarmModel model) {
//
//		List<Map<String, Object>> alarmMapList = null;
//		Map<String, Object> alarmMap = null;
//		int alarmValue = model.getAlarmValue();
//		switch(alarmValue){
//		case 0:
//			alarmMapList = alarmManagementMapper.getSFMCurAlarmList(model);
//			if(alarmMapList != null && alarmMapList.size() > 0){
//				for(Map<String, Object> map : alarmMapList){
//					//将告警移动到历史告警表
//					addHistoryAlarm(map);
//					//删除当前告警表中的记录
//					alarmManagementMapper.deleteCurAlarm(Integer.
//							parseInt(map.get("ALARM_ID").toString()));
//					if(map.get("EQPT_TYPE") != null 
//							&& !"".equals(map.get("EQPT_TYPE").toString())
//							&&map.get("EQPT_ID") != null 
//							&& !"".equals(map.get("EQPT_ID").toString())){
//							dispatchAlarm(Integer.parseInt(map.get("EQPT_TYPE").toString()), 
//									Integer.parseInt(map.get("EQPT_ID").toString()));
//					}
//				}
//			}
//			break;
//		case 1:
//		case 2:
//		case 3:
//		case 4:
//			alarmMap = alarmManagementMapper.getSFMCurAlarm(model);
//			if(alarmMap == null){
//				alarmManagementMapper.addSFMCurAlarm(model);
//				dispatchAlarm(model.getEqptType(), model.getEqptId());
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * 判断是否需要刷新当前告警页面
//	 * @param alarm
//	 * @return
//	 */
//	//	public boolean refreshCurrentAlarmPage(PushAlarmModel alarm) {
//	//		
//	//		boolean flag = false;
//	//		Map<String, Object> regionIdsMap = null;
//	//		String regionIds = null;
//	//		Map<String, Object> shieldRuleMap = null;
//	//		int userId = 1;
//	//		
//	//		if(alarm.getAlarmType() == Define.ALARM_TYPE_EQPT){
//	//			regionIdsMap = alarmManagementMapper.getRegionIds(userId);
//	//			if(regionIdsMap != null){
//	//				regionIds = regionIdsMap.get("REGION_ID").toString();
//	//				if(regionIds.contains(String.valueOf(alarm.getRegionId()))){
//	//					shieldRuleMap = alarmManagementMapper.
//	//							getELShieldRule(alarm.getRegionId(), userId);
//	//					if(shieldRuleMap != null){
//	//						String eqptRule = shieldRuleMap.get("BLOCK_EQPT").toString();
//	//						if(eqptRule != null && 
//	//								!eqptRule.contains(String.valueOf(alarm.getAlarmLevel()))){
//	//							flag = true;
//	//						}
//	//					}
//	//				}
//	//			}
//	//		}else if(alarm.getAlarmType() == Define.ALARM_TYPE_ROUTE){
//	//			regionIdsMap = alarmManagementMapper.getRegionIds(userId);
//	//			if(regionIdsMap != null){
//	//				regionIds = regionIdsMap.get("REGION_ID").toString();
//	//				if(regionIds.contains(String.valueOf(alarm.getRegionId()))){
//	//					shieldRuleMap = alarmManagementMapper.
//	//							getELShieldRule(alarm.getRegionId(), userId);
//	//					if(shieldRuleMap != null){
//	//						String lineRule = shieldRuleMap.get("BLOCK_LINE").toString();
//	//						if(lineRule != null && 
//	//								!lineRule.contains(String.valueOf(alarm.getAlarmLevel()))){
//	//							flag = true;
//	//						}
//	//					}
//	//				}
//	//			}
//	//		}else if(alarm.getAlarmType() == Define.ALARM_TYPE_EMS){
//	//			flag = true;
//	//		}
//	//		
//	//		return flag;
//	//	}
//
//	//----------------------- 拓扑图获取告警接口 --------------------------------
//	
//	/**
//	 * 判断区域是否离线
//	 * @param allEqpt
//	 * @param allVisibleAlarm
//	 * @return
//	 */
//	public boolean isRegionOffline(List<Map<String, Object>> allEqpt, List<Map<String, Object>> allVisibleAlarm) {
//		
//		boolean offline = true;
//		
//		for(Map<String, Object> eqpt : allEqpt){
//			int eqptType = Integer.parseInt(eqpt.get("EQPT_TYPE").toString());
//			int eqptId = Integer.parseInt(eqpt.get("EQPT_ID").toString());
//			boolean match = false;
//			boolean online = false;
//			for(Map<String, Object> alarm : allVisibleAlarm){
//				if(alarm.get("EQPT_TYPE") == null || 
//				   "".equals(alarm.get("EQPT_TYPE").toString()) ||
//				   alarm.get("EQPT_ID") == null ||
//				   "".equals(alarm.get("EQPT_ID").toString()) ||
//				   alarm.get("ALARM_LEVEL") == null ||
//				   "".equals(alarm.get("ALARM_LEVEL").toString())
//					){
//					continue;
//				}
//				
//				int eT = Integer.parseInt(alarm.get("EQPT_TYPE").toString());
//				int eI = Integer.parseInt(alarm.get("EQPT_ID").toString());
//				int alarmLevel = Integer.parseInt(alarm.get("ALARM_LEVEL").toString());
//				if(eqptType == eT && eqptId == eI){
//					match = true;
//					if(alarmLevel != Define.ALARM_LEVEL_OFFLINE){
//						online = true;
//						break;
//					}
//				}
//			}
//			if(online || !match){
//				offline = false;
//				break;
//			}
//		}
//		
//		return offline;
//	}
//	
//	/**
//	 * 获取区域告警统计数据
//	 * @param regionId
//	 * @return
//	 */
//	public AlarmCountModel getRegionAlarmCount(int regionId) {
//
//		AlarmCountModel result = new AlarmCountModel();
//		
//		//获取区域内的所有设备
//		List<Map<String, Object>> allEqpt = alarmManagementMapper.getAllEqptInRegion(regionId);
//		//获取区域内所有的可见告警
//		List<Map<String, Object>> allVisibleAlarm = alarmManagementMapper.
//											getAllVisibleAlarmInRegion(regionId);
//
//		int cr = alarmManagementMapper.getRegionAlarmCount(regionId, Define.ALARM_LEVEL_CR);
//		int mj = alarmManagementMapper.getRegionAlarmCount(regionId, Define.ALARM_LEVEL_MJ);
//		int mn = alarmManagementMapper.getRegionAlarmCount(regionId, Define.ALARM_LEVEL_MN);
//		int wr = alarmManagementMapper.getRegionAlarmCount(regionId, Define.ALARM_LEVEL_WR);
//		boolean offline = false;
//		
//		result.setCr(cr);
//		result.setMj(mj);
//		result.setMn(mn);
//		result.setWr(wr);
//		if(allEqpt.size() > 0){
//			offline = isRegionOffline(allEqpt, allVisibleAlarm);
//		}
//		result.setOffline(offline);
//		
//		return result;
//	}
//
//	/**
//	 * 获取机房告警统计数据
//	 * @param stationId
//	 * @return
//	 */
//	public AlarmCountModel getStationAlarmCount(int stationId) {
//
//		AlarmCountModel result = new AlarmCountModel();
//		
//		//获取机房内所有设备
//		List<Map<String, Object>> allEqpt = alarmManagementMapper.getAllEqptInStation(stationId);
//		//机房
//		Map<String, Object> stationMap = alarmManagementMapper.getStationById(stationId);
//		int regionId = Integer.parseInt(stationMap.get("STATION_ID").toString());
//		//获取区域内所有的可见告警
//		List<Map<String, Object>> allVisibleAlarm = alarmManagementMapper.
//													getAllVisibleAlarmInRegion(regionId);
//		
//		int cr = alarmManagementMapper.getStationAlarmCount(stationId, Define.ALARM_LEVEL_CR);
//		int mj = alarmManagementMapper.getStationAlarmCount(stationId, Define.ALARM_LEVEL_MJ);
//		int mn = alarmManagementMapper.getStationAlarmCount(stationId, Define.ALARM_LEVEL_MN);
//		int wr = alarmManagementMapper.getStationAlarmCount(stationId, Define.ALARM_LEVEL_WR);
//		boolean offline = false;
//
//		result.setCr(cr);
//		result.setMj(mj);
//		result.setMn(mn);
//		result.setWr(wr);
//		if(allEqpt.size() > 0){
//			offline = isStationOffline(allEqpt, allVisibleAlarm);
//		}
//		result.setOffline(offline);
//
//		return result;
//	}
//
//	
//	/**
//	 * 机房是否离线
//	 * @param allEqpt 机房内所有设备 RTU CTU OSM SFM
//	 * @param allVisibleAlarm 该机房所属区域内的所有设备告警和线路告警
//	 * @return
//	 */
//	public boolean isStationOffline(List<Map<String, Object>> allEqpt, List<Map<String, Object>> allVisibleAlarm) {
//
//		boolean offline = true;
//		
//		for(Map<String, Object> eqpt : allEqpt){
//			int eqptType = Integer.parseInt(eqpt.get("EQPT_TYPE").toString());
//			int eqptId = Integer.parseInt(eqpt.get("EQPT_ID").toString());
//			boolean match = false;
//			boolean online = false;
//			for(Map<String, Object> alarm : allVisibleAlarm){
//				int eT = Integer.parseInt(alarm.get("EQPT_TYPE").toString());
//				int eI = Integer.parseInt(alarm.get("EQPT_ID").toString());
//				int alarmLevel = Integer.parseInt(alarm.get("ALARM_LEVEL").toString());
//				if(eqptType == eT && eqptId == eI){
//					match = true;
//					if(alarmLevel != Define.ALARM_LEVEL_OFFLINE){
//						online = true;
//						break;
//					}
//				}
//			}
//			if(online || !match){
//				offline = false;
//				break;
//			}
//		}
//		
//		return offline;
//	}
//
//	/**
//	 * 获取设备的告警统计数据
//	 * @param eqptType
//	 * @param eqptId
//	 * @return
//	 */
//	public AlarmCountModel getEqptAlarmCount(int eqptType, int eqptId) {
//
//		AlarmCountModel result = new AlarmCountModel();
//
//		int cr = alarmManagementMapper.getEqptAlarmCount(eqptType, eqptId, Define.ALARM_LEVEL_CR);
//		int mj = alarmManagementMapper.getEqptAlarmCount(eqptType, eqptId, Define.ALARM_LEVEL_MJ);
//		int mn = alarmManagementMapper.getEqptAlarmCount(eqptType, eqptId, Define.ALARM_LEVEL_MN);
//		int wr = alarmManagementMapper.getEqptAlarmCount(eqptType, eqptId, Define.ALARM_LEVEL_WR);
//		int outline = alarmManagementMapper.getEqptAlarmCount(eqptType, eqptId, Define.ALARM_LEVEL_OFFLINE);
//
//		result.setCr(cr);
//		result.setMj(mj);
//		result.setMn(mn);
//		result.setWr(wr);
//		if(outline > 0){
//			result.setOffline(true);
//		}else{
//			result.setOffline(false);
//		}
//
//		return result;
//	}
//
//	/**
//	 * 获取连接线告警数
//	 * @param linkList
//	 * @return
//	 */
//	public AlarmCountModel getLineAlarmCount(List<TopoLinkModel> linkList) {
//
//		AlarmCountModel result = new AlarmCountModel();
//		int eqptType = 0;
//		int eqptId = 0;
//		int cardType = 0;
//		int slotNo = 0;
//		int portNo = 0;
//		int cr = 0;
//		int mj = 0;
//		int mn = 0;
//		int wr = 0;
//
//		for(TopoLinkModel link : linkList){
//			eqptType = link.getFromEqptType();
//			eqptId = link.getFromEqptId();
//			cardType = link.getFromCardType();
//			slotNo = link.getFromSlotNo();
//			portNo = link.getFromPortNo();
//
//			cr += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_CR);
//			mj += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_MJ);
//			mn += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_MN);
//			wr += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_WR);
//
//			eqptType = link.getToEqptType();
//			eqptId = link.getToEqptId();
//			cardType = link.getToCardType();
//			slotNo = link.getToSlotNo();
//			portNo = link.getToPortNo();
//			cr += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_CR);
//			mj += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_MJ);
//			mn += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_MN);
//			wr += alarmManagementMapper.getPortAlarm(eqptType, cardType, 
//					eqptId, slotNo, portNo, Define.ALARM_LEVEL_WR);
//		}
//
//		result.setCr(cr);
//		result.setMj(mj);
//		result.setMn(mn);
//		result.setWr(wr);
//
//		return result;
//	}
//
//	//推送
//	public boolean updateGisMap(JSONObject info){
//		boolean result = true;    	
//		try {          	
//			ScriptBuffer script = new ScriptBuffer();
//			script.appendScript("updateGisMap(").appendData(info).appendScript(")");
//			//得到登录此页面的scriptSession的集合
//			String currentPage = "/ORTS/jsp/gisMap/gisMap.jsp";
//			Set<String> keys = MyScriptSessionListener.scriptSessionMap.keySet();
//			for(String key : keys){
//				if(key.contains(currentPage)){
//					ScriptSession curSS = MyScriptSessionListener.scriptSessionMap.get(key);			
//					if(curSS != null){
//						curSS.addScript(script);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			result = false;
//		}        
//		return result;
//	}
//	
//	//从告警页面切换到gis页面显示断点位置
//	public boolean showBreakpoint(JSONObject info){
//		boolean result = true;    	
//		try {          	
//			ScriptBuffer script = new ScriptBuffer();
//			script.appendScript("updateGisMap(").appendData(info).appendScript(")");
//			String sessionId = info.getString("sessionId");
//			String currentPage = "/ORTS/jsp/gisMap/gisMap.jsp";
//			String key = sessionId+currentPage;
//			ScriptSession curSS = MyScriptSessionListener.scriptSessionMap.get(key);			
//			if(curSS != null){
//				curSS.addScript(script);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			result = false;
//		}        
//		return result;
//	}
//	
//	//增加CPU使用率超门限告警
//	public void addCpuAlarm() {
//		
//		Map<String, Object> map = alarmManagementMapper.
//				getServerPMAlarm(Define.CPU_USE_RATIO_SUPER_THRESHOLD);
//		if(map == null){
//			alarmManagementMapper.addServerPMAlarm(Define.CPU_USE_RATIO_SUPER_THRESHOLD,
//					Define.ALARM_TYPE_EMS, 
//					Define.CPU_USE_RATIO_SUPER_THRESHOLD_SEVERITY,
//					new Date());
//		}
//	}
//	
//	//消除CPU使用率超门限告警
//	public void removeCpuAlarm() {
//		
//		Map<String, Object> map = alarmManagementMapper.
//				getServerPMAlarm(Define.CPU_USE_RATIO_SUPER_THRESHOLD);
//		if(map != null){
//			alarmManagementMapper.deleteServerPMAlarm(Define.CPU_USE_RATIO_SUPER_THRESHOLD);
//		}
//	}
//	
//	//增加内存使用率超门限告警
//	public void addMemoryAlarm() {
//		
//		Map<String, Object> map = alarmManagementMapper.
//				getServerPMAlarm(Define.MEM_USE_RATIO_SUPER_THRESHOLD);
//		if(map == null){
//			alarmManagementMapper.addServerPMAlarm(Define.MEM_USE_RATIO_SUPER_THRESHOLD,
//					Define.ALARM_TYPE_EMS, 
//					Define.MEM_USE_RATIO_SUPER_THRESHOLD_SEVERITY,
//					new Date());
//		}
//	}
//	
//	//消除内存使用率超门限告警
//	public void removeMemoryAlarm() {
//		
//		Map<String, Object> map = alarmManagementMapper.
//				getServerPMAlarm(Define.MEM_USE_RATIO_SUPER_THRESHOLD);
//		if(map != null){
//			alarmManagementMapper.deleteServerPMAlarm(Define.MEM_USE_RATIO_SUPER_THRESHOLD);
//		}
//	}
//	
//	//增加硬盘使用率超门限告警
//	public void addDiskAlarm() {
//		
//		Map<String, Object> map = alarmManagementMapper.
//				getServerPMAlarm(Define.DISK_USE_RATIO_SUPER_THRESHOLD);
//		if(map == null){
//			alarmManagementMapper.addServerPMAlarm(Define.DISK_USE_RATIO_SUPER_THRESHOLD,
//					Define.ALARM_TYPE_EMS, 
//					Define.DISK_USE_RATIO_SUPER_THRESHOLD_SEVERITY,
//					new Date());
//		}
//	}
//	
//	//消除硬盘使用率超门限告警
//	public void removeDiskAlarm() {
//		
//		Map<String, Object> map = alarmManagementMapper.
//				getServerPMAlarm(Define.DISK_USE_RATIO_SUPER_THRESHOLD);
//		if(map != null){
//			alarmManagementMapper.deleteServerPMAlarm(Define.DISK_USE_RATIO_SUPER_THRESHOLD);
//		}
//	}
//	
//	/**
//	 * 增加设备离线告警
//	 * @param map
//	 * EQPT_ID 设备ID
//	 * EQPT_IP 设备IP
//	 * EQPT_TYPE 设备类型
//	 * STATION_ID 机房ID
//	 * REGION_ID 区域ID
//	 */
//	public void addEqptOfflineAlarm(Map<String, Object> map) {
//		
//		//新增离线告警时先判断数据库中是否有离线告警记录
//		int eqptType = Integer.parseInt(map.get("EQPT_TYPE").toString());
//		int eqptId = Integer.parseInt(map.get("EQPT_ID").toString());
//		Map<String, Object> offlineAlarm = alarmManagementMapper.
//				getEqptOfflineAlarm(eqptType, eqptId);
//		
//		if(offlineAlarm == null){
//			//将该设备的其它告警设置为不可见
//			map.put("ALARM_VISIBLE_FLAG", Define.ALARM_INVISIBLE);
//			alarmManagementMapper.changeVisibleStatusOfEqptAlarm(map);
//			
//			//增加设备离线告警
//			map.put("ALARM_NAME", "设备离线");
//			map.put("ALARM_TYPE", Define.ALARM_TYPE_EQPT);
//			map.put("ALARM_LEVEL", Define.ALARM_LEVEL_OFFLINE);
//			map.put("ALARM_VISIBLE_FLAG", Define.ALARM_VISIBLE);
//			map.put("ACK_STATUS", Define.ALARM_CONFIRM_STATUS_NO);
//			map.put("ALARM_OCCUR_DATE", new Date());
//			alarmManagementMapper.addNodeOfflineAlarm(map);
//			this.dispatchAlarm(eqptType, eqptId);
//		}
//	}
//	
//	/**
//	 * 移除设备离线告警
//	 * @param map
//	 * EQPT_ID 设备ID
//	 * EQPT_IP 设备IP
//	 * EQPT_TYPE 设备类型
//	 * STATION_ID 机房ID
//	 * REGION_ID 区域ID
//	 */
//	public void removeEqptOfflineAlarm(Map<String, Object> map) {
//		
//		//将该设备的其它告警设置为可见
//		map.put("ALARM_VISIBLE_FLAG", Define.ALARM_VISIBLE);
//		alarmManagementMapper.changeVisibleStatusOfEqptAlarm(map);
//		
//		int eqptType = Integer.parseInt(map.get("EQPT_TYPE").toString());
//		int eqptId = Integer.parseInt(map.get("EQPT_ID").toString());
//		
//		//移除该设备离线告警
//		Map<String, Object> eqptOfflineAlarm = alarmManagementMapper.
//				getEqptOfflineAlarm(eqptType, eqptId);
//		if(eqptOfflineAlarm != null){
//			alarmManagementMapper.removeEqptOfflineAlarm(
//					Integer.parseInt(map.get("EQPT_TYPE").toString()),
//						Integer.parseInt(map.get("EQPT_ID").toString()));
//			addHistoryAlarm(eqptOfflineAlarm);
//			
//			//移除所有设备离线告警
//			Map<String, Object> allEqptOfflineAlarm = alarmManagementMapper.
//					getEqptOfflineAlarm(Define.ALARM_EQPT_TYPE_ORTS, Define.ALARM_ORTS_ID);
//			if(allEqptOfflineAlarm != null){
//				alarmManagementMapper.removeEqptOfflineAlarm(Define.ALARM_EQPT_TYPE_ORTS, Define.ALARM_ORTS_ID);
//				addHistoryAlarm(allEqptOfflineAlarm);
//			}
//			
//			this.dispatchAlarm(eqptType, eqptId);
//		}
//	}
	
	//推送
	public boolean updateCurrentAlarmPage(){
		boolean result = true;    	
		try {          	
			ScriptBuffer script = new ScriptBuffer();
			script.appendScript("updateCurrentAlarmPage()");
			//得到登录此页面的scriptSession的集合
			String currentPage = "/FTTS/jsp/alarm/currentAlarm.jsp";
			Set<String> keys = MyScriptSessionListener.scriptSessionMap.keySet();
			for(String key : keys){
				if(key.contains(currentPage)){
					ScriptSession curSS = MyScriptSessionListener.scriptSessionMap.get(key);			
					if(curSS != null){
						curSS.addScript(script);
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
//	//推送
//	public boolean updateMainPageAlarmCount(){
//		System.out.println("更新首页告警计数！");
//		boolean result = true;    	
//		try {          	
//			ScriptBuffer script = new ScriptBuffer();
//			script.appendScript("getAlarmCount()");
//			//得到登录此页面的scriptSession的集合
//			String currentPage = "/ORTS/jsp/main/main.jsp";
//			Set<String> keys = MyScriptSessionListener.scriptSessionMap.keySet();
//			for(String key : keys){
//				if(key.contains(currentPage)){
//					ScriptSession curSS = MyScriptSessionListener.scriptSessionMap.get(key);			
//					if(curSS != null){
//						curSS.addScript(script);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			result = false;
//		}        
//		return result;
//	}
//
//	/**
//	 * 增加机房离线告警
//	 * @param stationId
//	 * @param regionId
//	 */
//	public void addStationOfflineAlarm(int stationId, int regionId) {
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("EQPT_TYPE", Define.ALARM_EQPT_TYPE_STATION);
//		map.put("EQPT_ID", stationId);
//		map.put("ALARM_NAME", "机房离线");
//		map.put("ALARM_TYPE", Define.ALARM_TYPE_EMS);
//		map.put("ALARM_LEVEL", Define.ALARM_LEVEL_OFFLINE);
//		map.put("ALARM_VISIBLE_FLAG", 1);
//		map.put("ACK_STATUS", Define.ALARM_CONFIRM_STATUS_NO);
//		map.put("ALARM_OCCUR_DATE", new Date());
//		map.put("REGION_ID", regionId);
//		alarmManagementMapper.addNodeOfflineAlarm(map);
//	}
//	
//	/**
//	 * 增加区域离线告警
//	 * @param regionId
//	 */
//	public void addRegionOfflineAlarm(int regionId) {
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("EQPT_TYPE", Define.ALARM_EQPT_TYPE_REGION);
//		map.put("EQPT_ID", regionId);
//		map.put("ALARM_NAME", "区域离线");
//		map.put("ALARM_TYPE", Define.ALARM_TYPE_EMS);
//		map.put("ALARM_LEVEL", Define.ALARM_LEVEL_OFFLINE);
//		map.put("ALARM_VISIBLE_FLAG", 1);
//		map.put("ACK_STATUS", Define.ALARM_CONFIRM_STATUS_NO);
//		map.put("ALARM_OCCUR_DATE", new Date());
//		map.put("REGION_ID", regionId);
//		alarmManagementMapper.addNodeOfflineAlarm(map);
//	}
//	
//	/**
//	 * 增加所有设备离线告警
//	 */
//	public void addORTSOfflineAlarm() {
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		Map<String, Object> root = alarmManagementMapper.getRoot();
//		
//		if(root != null){
//			//先判断数据库中有没有"所有设备离线的告警"
//			Map<String, Object> ortsOfflineAlarm = alarmManagementMapper.getORTSOfflineAlarm(Define.ALARM_EQPT_TYPE_ORTS);
//			if(ortsOfflineAlarm == null){
//				map.put("ALARM_NAME", "所有设备离线");
//				map.put("ALARM_TYPE", Define.ALARM_TYPE_EMS);
//				map.put("ALARM_LEVEL", Define.ALARM_LEVEL_OFFLINE);
//				map.put("ALARM_VISIBLE_FLAG", Define.ALARM_VISIBLE);
//				map.put("ACK_STATUS", Define.ALARM_CONFIRM_STATUS_NO);
//				map.put("EQPT_TYPE", Define.ALARM_EQPT_TYPE_ORTS);
//				map.put("EQPT_ID", Define.ALARM_ORTS_ID);
//				map.put("ALARM_OCCUR_DATE", new Date());
//				map.put("REGION_ID", Integer.parseInt(root.get("REGION_ID").toString()));
//				alarmManagementMapper.addNodeOfflineAlarm(map);
//				this.dispatchAlarm(Define.ALARM_EQPT_TYPE_ORTS, Define.ALARM_ORTS_ID);
//			}
//		}
//	}
//	
//	public void removeDataOfDeletedEqpt(int eqptType, int eqptId) {
//		
//		alarmManagementMapper.dltCurAlarmByTypeAndId(eqptType, eqptId);
//		alarmManagementMapper.dltHisAlarmByTypeAndId(eqptType, eqptId);
//		this.dispatchAlarm(eqptType, eqptId);
//	}
//	
//	//新增测试线路告警
//	public void addTestAlarm(int testResultId,String breakInfo) {
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		List<Map<String, Object>> linkInfoList = alarmManagementMapper.
//											getLinkByTestResultId(testResultId);
//		Map<String, Object> linkInfo = null;
//		if(linkInfoList != null && linkInfoList.size() > 0){
//			linkInfo = linkInfoList.get(0);
//		}
//		
//		map.put("ALARM_NAME", "OTDR测试：光缆断");
//		map.put("TEST_RESULT_ID", testResultId);
//		Map<String, Object> testPlan = alarmManagementMapper.getTestPlanByTestResultId(testResultId);
//		
//		if(testPlan.get("TEST_PLAN_TYPE") != null && !"".equals(testPlan.get("TEST_PLAN_TYPE").toString())){
//			Map<String, Object> eqptInfo = null;
//			int testPlanType = Integer.parseInt(testPlan.get("TEST_PLAN_TYPE").toString());
//			
//			if(testPlanType == Define.PLAN_TYPE_PERIOD 
//					|| testPlanType == Define.PLAN_TYPE_APPOINTED){
//				int linkId = Integer.parseInt(testPlan.get("LINK_ID").toString());
//				eqptInfo = alarmManagementMapper.getEqptInfoByLinkId(linkId);
//				map.put("CARD_TYPE", Define.CARD_TYPE_OSW);
//			}else if(testPlanType == Define.PLAN_TYPE_TRIG){
//				eqptInfo = alarmManagementMapper.
//						getEqptInfoByTestResultId(testResultId);
//				map.put("CARD_TYPE", Define.CARD_TYPE_OPM);
//			}
//			
//			if(eqptInfo != null){
//				
//				if(eqptInfo.get("RC_TYPE") != null && !"".equals(eqptInfo.get("RC_TYPE").toString())){
//					int type = 0;
//					type = Integer.parseInt(eqptInfo.get("RC_TYPE").toString());
//					//设备类型
//					int eqptType = type == 0 ? Define.ALARM_EQPT_TYPE_RTU : Define.ALARM_EQPT_TYPE_CTU;
//					map.put("EQPT_TYPE", eqptType);
//				}
//				
//				if(eqptInfo.get("RC_ID") != null && !"".equals(eqptInfo.get("RC_ID").toString())){
//					int eqptId = Integer.parseInt(eqptInfo.get("RC_ID").toString());
//					map.put("EQPT_ID", eqptId);
//				}
//				
//				if(eqptInfo.get("SLOT_NO") != null && !"".equals(eqptInfo.get("SLOT_NO").toString())){
//					int slotNo = Integer.parseInt(eqptInfo.get("SLOT_NO").toString());
//					map.put("SLOT_NO", slotNo);
//				}
//				
//				if(eqptInfo.get("PORT_NO") != null && !"".equals(eqptInfo.get("PORT_NO").toString())){
//					int portNo = Integer.parseInt(eqptInfo.get("PORT_NO").toString());
//					map.put("CARD_PORT", portNo);
//				}
//			}
//		}
//		
//		map.put("ALARM_TYPE", Define.ALARM_TYPE_ROUTE);
//		map.put("ALARM_LEVEL", Define.ALARM_LEVEL_CR);
//		map.put("ALARM_VISIBLE_FLAG", Define.ALARM_VISIBLE);
//		map.put("ACK_STATUS", Define.ALARM_CONFIRM_STATUS_NO);
//		map.put("ALARM_OCCUR_DATE", new Date());
//		if(linkInfo != null){
//			if(linkInfo.get("REGION_ID") != null && !linkInfo.get("REGION_ID").toString().equals("")){
//				map.put("REGION_ID", Integer.parseInt(linkInfo.get("REGION_ID").toString()));
//			}
//		}
//		map.put("BREAK_POINT_INFO", breakInfo);
//		alarmManagementMapper.addCurrentAlarm(map);
//		
//		this.updateMainPageAlarmCount();
//		this.updateCurrentAlarmPage();
//	}
//	
//	private void removeTestLinkBreakpoints(int testResultId){
//		List<String> cableSectionIds = getCableSectionIdsFromLink(String.valueOf(testResultId));
//		for(String id : cableSectionIds){
//			gisManagementMapper.removeBreakpointByCsId(id);
//		}
//	}
//	
//	//清除测试线路告警 --->当前告警转历史告警
//	private void removeTestAlarm(int testResultId) {		
//		Map<String, Object> map_osw = new HashMap<String, Object>();
//		Map<String, Object> map_opm = new HashMap<String, Object>();
//		
//		Map<String, Object> eqptInfo_osw = null;
//		Map<String, Object> eqptInfo_opm = null;
//		
//		List<Map<String, Object>> linkInfoList = alarmManagementMapper.
//											getLinkByTestResultId(testResultId);
//		Map<String, Object> linkInfo = null;
//		if(linkInfoList != null && linkInfoList.size() > 0){
//			linkInfo = linkInfoList.get(0);
//			
//			Map<String, Object> testPlan = alarmManagementMapper.getTestPlanByTestResultId(testResultId);
//			int linkId = Integer.parseInt(testPlan.get("LINK_ID").toString());
//			eqptInfo_osw = alarmManagementMapper.getEqptInfoByLinkId(linkId);
//			if(eqptInfo_osw != null){
//				map_osw.put("ALARM_NAME", "OTDR测试：光缆断");
//				map_osw.put("CARD_TYPE", Define.CARD_TYPE_OSW);
//				if(eqptInfo_osw.get("RC_TYPE") != null && !"".equals(eqptInfo_osw.get("RC_TYPE").toString())){
//					int type = 0;
//					type = Integer.parseInt(eqptInfo_osw.get("RC_TYPE").toString());
//					//设备类型
//					int eqptType = type == 0 ? Define.ALARM_EQPT_TYPE_RTU : Define.ALARM_EQPT_TYPE_CTU;
//					map_osw.put("EQPT_TYPE", eqptType);
//				}
//				
//				if(eqptInfo_osw.get("RC_ID") != null && !"".equals(eqptInfo_osw.get("RC_ID").toString())){
//					int eqptId = Integer.parseInt(eqptInfo_osw.get("RC_ID").toString());
//					map_osw.put("EQPT_ID", eqptId);
//				}
//				
//				if(eqptInfo_osw.get("SLOT_NO") != null && !"".equals(eqptInfo_osw.get("SLOT_NO").toString())){
//					int slotNo = Integer.parseInt(eqptInfo_osw.get("SLOT_NO").toString());
//					map_osw.put("SLOT_NO", slotNo);
//				}
//				
//				if(eqptInfo_osw.get("PORT_NO") != null && !"".equals(eqptInfo_osw.get("PORT_NO").toString())){
//					int portNo = Integer.parseInt(eqptInfo_osw.get("PORT_NO").toString());
//					map_osw.put("CARD_PORT", portNo);
//				}
//				
//				map_osw.put("ALARM_TYPE", Define.ALARM_TYPE_ROUTE);
//				map_osw.put("ALARM_LEVEL", Define.ALARM_LEVEL_CR);
//				
//				if(linkInfo.get("REGION_ID") != null && !linkInfo.get("REGION_ID").toString().equals("")){
//					map_osw.put("REGION_ID", Integer.parseInt(linkInfo.get("REGION_ID").toString()));
//				}
//				
//				if(map_osw.get("EQPT_TYPE") != null && 
//				   map_osw.get("EQPT_ID") != null && 
//				   map_osw.get("SLOT_NO") != null && 
//				   map_osw.get("CARD_PORT") != null && 
//				   map_osw.get("REGION_ID") != null){
//					
//					List<Map<String, Object>> alarmList_osw = alarmManagementMapper.getCurrentTestAlarm(map_osw);
//					if(alarmList_osw != null && alarmList_osw.size() > 0){
//						for(Map<String, Object> testAlarm_osw : alarmList_osw){
//							int alarmId_osw = Integer.parseInt(testAlarm_osw.get("ALARM_ID").toString());
//							alarmManagementMapper.deleteCurAlarm(alarmId_osw);
//							addHistoryAlarm(testAlarm_osw);
//						}
//					}
//				}
//			}
//			
//			List<Map<String, Object>> testPlanList = alarmManagementMapper.getTestPlanByLinkId(linkId);
//			if(testPlanList != null && testPlanList.size() > 0){
//				for(Map<String, Object> plan : testPlanList){
//					if(plan.get("OPM_ID") != null && plan.get("OPM_PORT_ID") != null){
//						eqptInfo_opm = alarmManagementMapper.getOPMByOPMPortId(Integer.parseInt(plan.get("OPM_PORT_ID").toString()));
//						if(eqptInfo_opm != null){
//							
//							map_opm.put("ALARM_NAME", "OTDR测试：光缆断");
//							map_opm.put("CARD_TYPE", Define.CARD_TYPE_OPM);
//							
//							if(eqptInfo_opm.get("RC_TYPE") != null && !"".equals(eqptInfo_opm.get("RC_TYPE").toString())){
//								int type = 0;
//								type = Integer.parseInt(eqptInfo_opm.get("RC_TYPE").toString());
//								//设备类型
//								int eqptType = type == 0 ? Define.ALARM_EQPT_TYPE_RTU : Define.ALARM_EQPT_TYPE_CTU;
//								map_opm.put("EQPT_TYPE", eqptType);
//							}
//							
//							if(eqptInfo_opm.get("RC_ID") != null && !"".equals(eqptInfo_opm.get("RC_ID").toString())){
//								int eqptId = Integer.parseInt(eqptInfo_opm.get("RC_ID").toString());
//								map_opm.put("EQPT_ID", eqptId);
//							}
//							
//							if(eqptInfo_opm.get("SLOT_NO") != null && !"".equals(eqptInfo_opm.get("SLOT_NO").toString())){
//								int slotNo = Integer.parseInt(eqptInfo_opm.get("SLOT_NO").toString());
//								map_opm.put("SLOT_NO", slotNo);
//							}
//							
//							if(eqptInfo_opm.get("PORT_NO") != null && !"".equals(eqptInfo_opm.get("PORT_NO").toString())){
//								int portNo = Integer.parseInt(eqptInfo_opm.get("PORT_NO").toString());
//								map_opm.put("CARD_PORT", portNo);
//							}
//							
//							map_opm.put("ALARM_TYPE", Define.ALARM_TYPE_ROUTE);
//							map_opm.put("ALARM_LEVEL", Define.ALARM_LEVEL_CR);
//							if(linkInfo.get("REGION_ID") != null && !linkInfo.get("REGION_ID").toString().equals("")){
//								map_opm.put("REGION_ID", Integer.parseInt(linkInfo.get("REGION_ID").toString()));
//							}
//							
//							if(map_opm.get("EQPT_TYPE") != null && 
//							   map_opm.get("EQPT_ID") != null && 
//							   map_opm.get("SLOT_NO") != null && 
//							   map_opm.get("CARD_PORT") != null && 
//							   map_opm.get("REGION_ID") != null) {
//								
//								List<Map<String, Object>> alarmList_opm = alarmManagementMapper.getCurrentTestAlarm(map_opm);
//								if(alarmList_opm != null && alarmList_opm.size() > 0){
//									for(Map<String, Object> testAlarm_opm : alarmList_opm){
//										int alarmId_opm = Integer.parseInt(testAlarm_opm.get("ALARM_ID").toString());
//										alarmManagementMapper.deleteCurAlarm(alarmId_opm);
//										addHistoryAlarm(testAlarm_opm);
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		removeTestLinkBreakpoints(testResultId);
//		this.updateMainPageAlarmCount();
//		this.updateCurrentAlarmPage();
//		Map<String, Object> info = new HashMap<String, Object>();
//		info.put("REFRESH_FLAG", Define.REFRESH_FLAG);
//		this.updateGisMap(JSONObject.fromObject(info));
//		System.out.println("---清除测试链路告警方法 完成 --");
//	}
//	
//	//推送
////	public boolean updateTopo(JSONObject info){    	
////		boolean result = true;    	
////        try {          	
////			ScriptBuffer script = new ScriptBuffer();
////			script.appendScript("update(").appendData(info).appendScript(")");
////			//得到登录此页面的scriptSession的集合
////			String currentPage = "/ORTS/jsp/topoManage/Topo.jsp";
////			
////			ServerContext wctx = ServerContextFactory.get();
////			Collection<ScriptSession> sessions = wctx.getScriptSessionsByPage(currentPage);
////			for (ScriptSession session : sessions) {
////				//判定目标用户推信息
////				session.addScript(script);
////			}
////		} catch (Exception e) {
////			e.printStackTrace();
////			result = false;
////		}        
////        return result;
////    }
//	
//	public void test(){
//		
//		this.analyzeRTUAlarm(this.minusAlarm());
//		
////		breakPointAnalyze("178");
////		Map<String, Object> gisAlarm = new HashMap<String, Object>();
////		gisAlarm.put("stationId", "1000000125");
////		gisAlarm.put("alarmLevel", 1);
////		gisAlarm.put("type", Define.ALARM_LEVEL);
////		this.updateGisMap(JSONObject.fromObject(gisAlarm));
////		this.updateCurrentAlarmPage();
//		
//	}
//
//	private Map<String,Object> getTestInfoForAnalyze(String testResultId){
//		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			map = alarmManagementMapper.getTestInfoForAnalyze(testResultId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	
//	//断点分析
//	//
//	public JSONObject breakPointAnalyze(String testResultId){
//		Map<String,Object> breakpointInfo = getNecessaryBreakpointInfo(testResultId);
//		double location = Double.valueOf(breakpointInfo.get("location").toString());
//		double linkLength = Double.valueOf(breakpointInfo.get("linkLength").toString());
//		double preLength = Double.valueOf(breakpointInfo.get("preLength").toString());				
//		
//		//没有断点的测试链路！！
////		if(breakCableSection.isEmpty()){
////			removeTestAlarm(Integer.valueOf(testResultId));
////			return null;
////		}	
//		Map<String, Object> info = new HashMap<String, Object>();
//		if((location-linkLength) > Define.TEST_ALLOWABLE_ERROR){
//			//在误差范围外
//			info.put("warningMessage", "测试链路数据失真，请校验后重新测试！");
//			updateGisMap(JSONObject.fromObject(info));
//			return null;
//		}else if(Math.abs(location-linkLength) <= Define.TEST_ALLOWABLE_ERROR){
//			//在误差范围内，则认为测试正常
//			removeTestAlarm(Integer.valueOf(testResultId));
//			return null;			
//		}
//		
//		Map<String, Object> breakCableSection = (Map<String, Object>)breakpointInfo.get("breakCableSection");
//		String breakCableSectionId = breakCableSection.get("CABLE_SECTION_ID").toString();
//		StringBuffer infoStr = new StringBuffer();//断点信息
//		List<Map<String, Object>> towerTubes = gisManagementMapper.
//				getTowerTubesByCableSection(breakCableSectionId);
//		//光缆段终点非杆塔管井
//		double lastLen = Double.valueOf(breakCableSection.get("PRE_TT_DISTANCE").toString());
//		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.000000");
//		java.text.DecimalFormat df1 = new java.text.DecimalFormat("#0.00");
//		if(lastLen <= 0){
//			
//		}else{							
//			double breakToStartLen = location-preLength;//断点距离该光缆段起点的距离
//			//记录前一个杆塔管井
//			Map<String, Object> preTowerTube = null;
//			if(breakToStartLen <= 0){
//				//断点在起点处
//				int startType = (Integer)breakCableSection.get("START_TYPE");
//				String startId = breakCableSection.get("START_ID").toString();
//				Point start = this.getResourceLngLat(startType, startId);
//				infoStr.append("断点位于盘纤点"+start.getName()+"处");
//				infoStr.append(","+start.getLng()).append(","+start.getLat());
//				info.put("type", Define.BREAKPOINT);
//				info.put("point",start);
//			}else{
//				for(Map<String, Object> tt: towerTubes){
//					double pq_length = Double.valueOf(tt.get("PQ_LENGTH").toString());
//					double pre_distance = Double.valueOf(tt.get("PRE_TT_DISTANCE").toString());
//					preLength = preLength+pre_distance;
//					//非盘纤点断点
//					if(preLength > location){
//						//double ratio = 1-(minLength-length)/pre_distance;
//						Point end = new Point();
//						end.setName(tt.get("TOWER_TUBE_NAME").toString());
//						if(tt.get("LNG") == null){
//							end.setLng(null);
//						}else
//							end.setLng(Double.valueOf(tt.get("LNG").toString()));
//						if(tt.get("LAT") == null){
//							end.setLat(null);
//						}else
//							end.setLat(Double.valueOf(tt.get("LAT").toString()));
//						end.setId(tt.get("TOWER_TUBE_ID").toString());
//	
//						Point start = new Point();
//						//断点在光缆段起点和第一个杆塔管井之间
//						if(preTowerTube == null){						
//							int startType = (Integer)breakCableSection.get("START_TYPE");
//							String startId = breakCableSection.get("START_ID").toString();
//							start = this.getResourceLngLat(startType, startId);
//						}else{
//							start.setName(preTowerTube.get("TOWER_TUBE_NAME").toString());
//							if(preTowerTube.get("LNG") == null){
//								start.setLng(null);
//							}else
//								start.setLng(Double.valueOf(preTowerTube.get("LNG").toString()));
//							if(preTowerTube.get("LAT") == null){
//								start.setLat(null);
//							}else
//								start.setLat(Double.valueOf(preTowerTube.get("LAT").toString()));
//							start.setId(preTowerTube.get("TOWER_TUBE_ID").toString());
//						}
//	
//						infoStr.append("断点位于"+start.getName()+"和"+end.getName()+"之间");					
//						
//						double preToStartLen = getLengthFromResourceToStart(start.getId(), breakCableSectionId);
//						String breakLocation = df1.format((breakToStartLen-preToStartLen)*1000);
//						infoStr.append(breakLocation).append("米处");
//						//非盘纤断点时需要减去终点盘纤距离
//						double nextToStartLen = getLengthFromResourceToStart(end.getId(), breakCableSectionId)-pq_length;
//						if(start.getLng() == null || start.getLat() == null){
//							start = getPreTowerTubeLngLat(start.getId(), breakCableSectionId);
//							preToStartLen = getLengthFromResourceToStart(start.getId(), breakCableSectionId);
//						}
//						if(end.getLng() == null || end.getLat() == null){
//							end = getNextTowerTubeLngLat(end.getId(), breakCableSectionId);
//							nextToStartLen = getLengthFromResourceToStart(end.getId(), breakCableSectionId);
//						}
//	
//						double ratio = (breakToStartLen - preToStartLen)/(nextToStartLen-preToStartLen);
//	
//						double lng = (1-ratio)*start.getLng() + ratio*end.getLng();
//						double lat = (1-ratio)*start.getLat() + ratio*end.getLat();
//						infoStr.append(","+lng).append(","+lat);
//						Point p = new Point();
//						p.setLng(Double.valueOf(df.format(lng)));
//						p.setLat(Double.valueOf(df.format(lat)));
//						info.put("type", Define.BREAKPOINT);
//						info.put("point",p);
//						if(!info.isEmpty()){
//							break;
//						}
//	
//					}
//					preLength = preLength+pq_length;
//					//盘纤点断点
//					if(preLength >= location){
//						Point point = new Point();
//						point.setName(tt.get("TOWER_TUBE_NAME").toString());
//						if(tt.get("LNG") == null){
//							point.setLng(null);
//						}else
//							point.setLng(Double.valueOf(tt.get("LNG").toString()));
//						if(tt.get("LAT") == null){
//							point.setLat(null);
//						}else
//							point.setLat(Double.valueOf(tt.get("LAT").toString()));
//						point.setId(tt.get("TOWER_TUBE_ID").toString());
//						infoStr.append("断点位于盘纤点"+point.getName()+"处");
//						infoStr.append(","+point.getLng()).append(","+point.getLat());
//						info.put("type", Define.BREAKPOINT);
//						info.put("point",point);
//						if(point.getLng() == null || point.getLat() == null){
//							info.clear();
//							double preToStartLen = 0;
//							double nextToStartLen = 0;
//	
//							Point start = getPreTowerTubeLngLat(point.getId(), breakCableSectionId);
//							preToStartLen = getLengthFromResourceToStart(start.getId(), breakCableSectionId);
//	
//							Point end = getNextTowerTubeLngLat(point.getId(), breakCableSectionId);
//							nextToStartLen = getLengthFromResourceToStart(end.getId(), breakCableSectionId);
//	
//							double ratio = (breakToStartLen - preToStartLen)/(nextToStartLen-preToStartLen);
//	
//							double lng = (1-ratio)*start.getLng() + ratio*end.getLng();
//							double lat = (1-ratio)*start.getLat() + ratio*end.getLat();
//							Point p = new Point();
//							p.setLng(Double.valueOf(df.format(lng)));
//							p.setLat(Double.valueOf(df.format(lat)));
//							info.put("type", Define.BREAKPOINT);
//							info.put("point",p);
//						}
//					}
//					preTowerTube = tt;
//					if(!info.isEmpty()){
//						break;
//					}
//				}
//			}
//			if(info.isEmpty()){
//				Point start = new Point();				
//				//断点在lastLen上
//				if(preTowerTube == null){
//					//如果该光缆段没有穿管的杆塔管井
//					int startType = (Integer)breakCableSection.get("START_TYPE");
//					String startId = breakCableSection.get("START_ID").toString();
//					start = this.getResourceLngLat(startType, startId);
//				}else{
//					start.setName(preTowerTube.get("TOWER_TUBE_NAME").toString());
//					if(preTowerTube.get("LNG") == null){
//						start.setLng(null);
//					}else
//						start.setLng(Double.valueOf(preTowerTube.get("LNG").toString()));
//					if(preTowerTube.get("LAT") == null){
//						start.setLat(null);
//					}else
//						start.setLat(Double.valueOf(preTowerTube.get("LAT").toString()));
//					start.setId(preTowerTube.get("TOWER_TUBE_ID").toString());
//				}
//				Point end = new Point();
//				int end_Type = (Integer)breakCableSection.get("END_TYPE");
//				String end_Id = breakCableSection.get("END_ID").toString();
//				end = this.getResourceLngLat(end_Type, end_Id);
//
//				infoStr.append("断点位于"+start.getName()+"和"+end.getName()+"之间");
//				
//				double preToStartLen = getLengthFromResourceToStart(start.getId(), breakCableSectionId);
//				infoStr.append(df1.format((breakToStartLen-preToStartLen)*1000)).append("米处");
//				double nextToStartLen = getLengthFromResourceToStart(end.getId(), breakCableSectionId);
//				if(start.getLng() == null || start.getLat() == null){
//					start = getPreTowerTubeLngLat(start.getId(), breakCableSectionId);
//					preToStartLen = getLengthFromResourceToStart(start.getId(), breakCableSectionId);
//				}
//
//				double ratio = (breakToStartLen - preToStartLen)/(nextToStartLen-preToStartLen);
//				
//				//测试用
//				double lng = (1-ratio)*start.getLng() + ratio*end.getLng();
//				double lat = (1-ratio)*start.getLat() + ratio*end.getLat();
//				infoStr.append(","+df.format(lng)).append(","+df.format(lat));
//				Point p = new Point();
//				p.setLng(Double.parseDouble(df.format(lng)));
//				p.setLat(Double.parseDouble(df.format(lat)));
//				info.put("type", Define.BREAKPOINT);
//				info.put("point",p);
//			}				
//		}
//		JSONObject result = JSONObject.fromObject(info);
//		System.out.println("推送结果   ----------> "+result);
//		System.out.println(infoStr.toString());
//		Point breakpoint = (Point)info.get("point");
//		String breakpointStr = df.format(breakpoint.getLng())+","+df.format(breakpoint.getLat());
//		gisManagementMapper.addBreakPointToCableSection(breakpointStr, breakCableSectionId);
//		this.addTestAlarm(Integer.valueOf(testResultId),infoStr.toString());
//		updateGisMap(result);
//		return result;
//	}
//
//	private List<String> getCableSectionIdsFromLink(String testResultId){
//		Map<String, Object> testInfo = getTestInfoForAnalyze(testResultId);
//		if(testInfo.isEmpty()){
//			return null;
//		}
//		String linkId = testInfo.get("LINK_ID").toString();
//		
//		Map<String, Object> link = null;
//		List<String> cableSectionIds = new ArrayList<String>();
//		try {
//			link = alarmManagementMapper.getLinkById(linkId);
//		
//			//测试链路中的所有光缆段
//			if(link.get("OPTICAL1_ID") != null){
//				List<Map<String, Object>> optical1 = alarmManagementMapper.
//						getOpticalCableSections(link.get("OPTICAL1_ID").toString());
//				for (Map<String, Object> cableSection : optical1) {
//					if(cableSection.get("CABLE_SECTION_ID") != null){
//						cableSectionIds.add(cableSection.get("CABLE_SECTION_ID").toString());
//					}
//				}
//			}
//			if(link.get("OPTICAL2_ID") != null){
//				List<Map<String, Object>> optical2 = alarmManagementMapper.
//						getOpticalCableSections(link.get("OPTICAL2_ID").toString());
//				for (Map<String, Object> cableSection : optical2) {
//					if(cableSection.get("CABLE_SECTION_ID") != null){
//						cableSectionIds.add(cableSection.get("CABLE_SECTION_ID").toString());
//					}
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return cableSectionIds;
//	}
//	
//	private Map<String,Object> getNecessaryBreakpointInfo(String testResultId){
//		Map<String,Object> map = new HashMap<String,Object>();
//		System.out.println("breakPointAnalyze() testResultId："+testResultId+"-->start!");
//		Map<String, Object> testInfo = getTestInfoForAnalyze(testResultId);
//		if(testInfo.isEmpty()){
//			return null;
//		}
//		double location = (Double.valueOf(testInfo.get("LOCATION").toString())) / 1000;
//		
//		List<String> cableSectionIds = getCableSectionIdsFromLink(testResultId);
//
//		Map<String, Object> breakCableSection = new HashMap<String, Object>();
//		double _length = 0; //累加长度
//		double linkLength = 0; //测试链路长度
//		double preLength = 0; //假设断点在第三条光缆段上，此值为第一条和第二条光缆段的长度和
//		for(String id : cableSectionIds){
//			Map<String, Object> cableSection = gisManagementMapper.getCableSectionById(id);
//			if(cableSection ==null){
//				System.out.println("###########********cableSection null !");
//			}
//			if(cableSection.get("CABLE_SECTION_LEN") != null){
//				double len = Double.valueOf(cableSection.get("CABLE_SECTION_LEN").toString());
//				linkLength = linkLength + len;
//				if(_length <= location){
//					_length = _length + len;
//				}
//				if(_length >= location){
//					breakCableSection = cableSection;
//					preLength = _length - len;
//					break;
//				}
//			}
//		}
//		
//		map.put("location", location);
//		map.put("linkLength", linkLength);
//		map.put("preLength", preLength);
//		map.put("breakCableSection", breakCableSection);
//		System.out.println("breakpoint location : "+location+" testLink length : "+linkLength);
//		return map;
//	}
//	
//	private String constructBreakPointInfo(Point start,Point end,double length,double preLength){
//		double len = (length - preLength)*1000;
//		String infoStr = "断点位于"+start.getName()+"和"+end.getName()+"之间"+len+"米处";
//		
//		return infoStr;
//	}
//	
//	//获取光缆段起止点
//	private Point getResourceLngLat(int type, String id){
//		Point point = new Point();	
//		Double lng = null, lat = null;		
//		switch(type){
//		case Define.TYPE_STATION:
//			Map<String,Object> station = gisManagementMapper.getStationById(id);
//			if(station.get("LNG") != null){
//				lng = (Double)station.get("LNG");
//			}
//			if(station.get("LAT") != null){
//				lat = (Double)station.get("LAT");
//			}
//			if(station.get("STATION_NAME") != null){
//				point.setName(station.get("STATION_NAME").toString());
//			}
//			if(station.get("STATION_ID") != null){
//				point.setId(station.get("STATION_ID").toString());
//			}
//			break;
//		case Define.TYPE_DISTRIBUTION_BOX:
//			Map<String,Object> box = gisManagementMapper.getDistributionBoxById(id);
//			if(box.get("LNG") != null){
//				lng = (Double)box.get("LNG");
//			}
//			if(box.get("LAT") != null){
//				lat = (Double)box.get("LAT");
//			}
//			if(box.get("BOX_NAME") != null){
//				point.setName(box.get("BOX_NAME").toString());
//			}
//			if(box.get("BOX_ID") != null){
//				point.setId(box.get("BOX_ID").toString());
//			}
//			break;
//		default:
//			Map<String,Object> towerTube = gisManagementMapper.getTowerTubeById(id);
//			if(towerTube.get("LNG") != null){
//				lng = (Double)towerTube.get("LNG");
//			}
//			if(towerTube.get("LAT") != null){
//				lat = (Double)towerTube.get("LAT");
//			}
//			if(towerTube.get("TOWER_TUBE_NAME") != null){
//				point.setName(towerTube.get("TOWER_TUBE_NAME").toString());
//			}
//			if(towerTube.get("TOWER_TUBE_ID") != null){
//				point.setId(towerTube.get("TOWER_TUBE_ID").toString());
//			}
//		}	
//		point.setLng(lng);
//		point.setLat(lat);
//		return point;
//	}
//
//	//光缆段中资源到光缆段起点的距离
//	private double getLengthFromResourceToStart(String resourceId,String cableSectionId){
//		double length = 0;
//		Map<String, Object> cableSection = gisManagementMapper.getCableSectionById(cableSectionId);
//		if(cableSection.get("START_ID").toString().equals(resourceId)){
//			return length;
//		}
//		if(cableSection.get("END_ID").toString().equals(resourceId)){
//			length = Double.valueOf(cableSection.get("CABLE_SECTION_LEN").toString());
//			return length;
//		}
//		List<Map<String, Object>> towerTubes = gisManagementMapper.
//				getTowerTubesByCableSection(cableSectionId);
//		for(Map<String, Object> tt : towerTubes){
//			double pq_length = Double.valueOf(tt.get("PQ_LENGTH").toString());
//			double pre_distance = Double.valueOf(tt.get("PRE_TT_DISTANCE").toString());
//			length += pre_distance+pq_length;
//			if(tt.get("TOWER_TUBE_ID").toString().equals(resourceId)){
//				break;
//			}
//		}
//		return length;
//	}
//
//	private Point getPreTowerTubeLngLat(String towerTubeId,String cableSectionId){
//		Point pre = new Point();
//		Map<String, Object> towerTubePq = gisManagementMapper
//				.getTowerTubePqById(towerTubeId, cableSectionId);
//
//		int index = Integer.valueOf(towerTubePq.get("PQ_SORT_NO").toString());
//
//		List<Map<String, Object>> towerTubes = gisManagementMapper.
//				getTowerTubesByCableSection(cableSectionId);
//		for(int i = index-1;i >= 0;i--){
//			Map<String, Object> tt = towerTubes.get(i);
//			if(tt.get("LNG") != null && tt.get("LAT") != null){
//				pre.setName(tt.get("TOWER_TUBE_NAME").toString());
//				pre.setLng(Double.valueOf(tt.get("LNG").toString()));
//				pre.setLat(Double.valueOf(tt.get("LAT").toString()));
//				pre.setId(tt.get("TOWER_TUBE_ID").toString());
//				break;
//			}
//		}
//		if(pre.getLng() == null || pre.getLat() == null){
//			Map<String, Object> cableSection = gisManagementMapper.getCableSectionById(cableSectionId);
//			int type = (Integer)cableSection.get("START_TYPE");
//			String id = cableSection.get("START_ID").toString();
//			pre = this.getResourceLngLat(type, id);
//		}
//		return pre;
//	}
//
//	private Point getNextTowerTubeLngLat(String towerTubeId,String cableSectionId){
//		Point next = new Point();
//		Map<String, Object> towerTubePq = gisManagementMapper
//				.getTowerTubePqById(towerTubeId, cableSectionId);
//
//		int index = Integer.valueOf(towerTubePq.get("PQ_SORT_NO").toString());
//
//		List<Map<String, Object>> towerTubes = gisManagementMapper.
//				getTowerTubesByCableSection(cableSectionId);
//		for(int i = index+1;i<towerTubes.size();i--){
//			Map<String, Object> tt = towerTubes.get(i);
//			if(tt.get("TOWER_TUBE_ID").equals(towerTubeId)){
//				if(tt.get("LNG") != null && tt.get("LAT") != null){
//					next.setName(tt.get("TOWER_TUBE_NAME").toString());
//					next.setLng(Double.valueOf(tt.get("LNG").toString()));
//					next.setLat(Double.valueOf(tt.get("LAT").toString()));
//					next.setId(tt.get("TOWER_TUBE_ID").toString());
//					break;
//				}
//			}	
//		}
//
//		if(next.getLng() == null || next.getLat() == null){
//			Map<String, Object> cableSection = gisManagementMapper.getCableSectionById(cableSectionId);
//			int type = (Integer)cableSection.get("END_TYPE");
//			String id = cableSection.get("END_ID").toString();
//			next = this.getResourceLngLat(type, id);
//		}
//		return next;
//	}

}
