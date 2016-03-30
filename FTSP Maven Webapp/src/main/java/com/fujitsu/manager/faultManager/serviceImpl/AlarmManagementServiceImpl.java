package com.fujitsu.manager.faultManager.serviceImpl;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.ILogManagerService;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.activeMq.JMSSender;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.AlarmManagementMapper;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.KeyAccountMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.job.AlarmAutoConfirmJob;
import com.fujitsu.job.AlarmAutoShiftJob;
import com.fujitsu.job.AlarmSyncJob;
import com.fujitsu.manager.faultManager.service.AlarmManagementService;
import com.fujitsu.manager.faultManager.util.MongodbGroupUtil;
import com.fujitsu.model.AlarmAutoSynchModel;
import com.fujitsu.model.AlarmFilterModel;
import com.fujitsu.model.AlarmShieldModel;
import com.fujitsu.model.CurrentAlarmModel;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;
import com.fujitsu.util.WebMsgPush;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@Scope("prototype")
@Service
@Transactional(rollbackFor = Exception.class)
public class AlarmManagementServiceImpl extends AlarmManagementService {
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;
	@Resource
	private AlarmManagementMapper alarmManagementMapper;
	@Resource
	private CommonManagerMapper commonManagerMapper;
	@Resource
	private ICommonManagerService commonManagerService;
	@Resource
	public ILogManagerService iLogManagerService;
	private IDataCollectServiceProxy dataCollectService;
	@Resource
	private KeyAccountMapper kAMap;
	@Autowired
	private Mongo mongo;
	@Resource
	private IQuartzManagerService quartzManagerService;
	
//	@Override
//	public Map<String, Object> getAllEmsGroups() {		
//		Map<String, Object> valueMap = new HashMap<String, Object>();
//		// 查询所有网管分组
//		List<Map<String, Object>> emsGroupList = faultManagerMapper.getAllEmsGroups();
//		// 封装前台表格数据->列表信息
//		valueMap.put("rows", emsGroupList);
//		return valueMap;
//	}
//	@Override
//	public Map<String, Object> getAllEmsGroupsNoAll() {		
//		Map<String, Object> valueMap = new HashMap<String, Object>();
//		// 查询所有网管分组
//		List<Map<String, Object>> emsGroupList = faultManagerMapper.getAllEmsGroupsNoAll();
//		// 封装前台表格数据->列表信息
//		valueMap.put("rows", emsGroupList);
//		return valueMap;
//	}

//	@Override
//	public Map<String, Object> getAllEmsByEmsGroupId(Map<String, Object> paramMap) throws CommonException {
//		Map<String, Object> valueMap = new HashMap<String, Object>();
//		// 查询某个网管分组下的所有网管
//		List<Map<String, Object>> emsList = faultManagerMapper.getAllEmsByEmsGroupId(paramMap);
//		// 封装前台表格数据->列表信息
//		valueMap.put("rows", emsList);
//		return valueMap;
//	}
	
//	@Override
//	public Map<String, Object> getAllEmsByEmsGroupIdNoAll(Map<String, Object> paramMap) throws CommonException {
//		Map<String, Object> valueMap = new HashMap<String, Object>();
//		// 查询某个网管分组下的所有网管
//		List<Map<String, Object>> emsList = faultManagerMapper.getAllEmsByEmsGroupIdNoAll(paramMap);
//		// 封装前台表格数据->列表信息
//		valueMap.put("rows", emsList);
//		return valueMap;
//	}

//	@Override
//	public Map<String, Object> getAllNeByEmsIdAndNename(Map<String, Object> paramMap) throws CommonException {
//		Map<String, Object> valueMap = new HashMap<String, Object>();
//		// 模糊查询某个网管下的所有网元
//		List<Map<String, Object>> neList = faultManagerMapper.getAllNeByEmsIdAndNename(paramMap);
//		// 封装前台表格数据->列表信息
//		valueMap.put("rows", neList);
//		return valueMap;
//	}
	
	@Override
	public Map<String, Object> getCurrentAlarms(Map<String, Object> paramMap, int start, int limit, Integer userId)throws CommonException {
		Date begin = new Date();
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 获取当前用户ID
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Integer userId = (Integer)request.getSession().getAttribute("SYS_USER_ID");
		// 封装查询条件
		BasicDBObject conditionQuery = new BasicDBObject ();
		
		if ("".equals(paramMap.get("emsGroupId")) &&
				"".equals(paramMap.get("emsId")) &&
				"".equals(paramMap.get("subnetId")) &&
				"".equals(paramMap.get("neId")) &&
				"".equals(paramMap.get("unitId")) &&
				"".equals(paramMap.get("ptpId")) &&
				"".equals(paramMap.get("stationId"))) {
			// 显示非管理员用户的所有告警
			if (userId != CommonDefine.USER_ADMIN_ID) {
				int[] emsArray = null; int[] subnetArray = null; int[] neArray = null;
				BasicDBList devDomain = new BasicDBList ();				
				List<Map<String, Object>> emsList = alarmManagementMapper.getEmsIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (emsList != null && emsList.size() > 0) {
					emsArray = new int[emsList.size()];
					for (int i = 0; i < emsArray.length; i++) {
						emsArray[i]=Integer.parseInt(emsList.get(i).get("BASE_EMS_CONNECTION_ID").toString());
					}
					devDomain.add(new BasicDBObject("EMS_ID", new BasicDBObject("$in", emsArray)));
				}
				List<Map<String, Object>> subnetList = alarmManagementMapper.getSubnetIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (subnetList != null && subnetList.size() > 0) {
					subnetArray = new int[subnetList.size()];
					for (int i = 0; i < subnetArray.length; i++) {
						subnetArray[i]=Integer.parseInt(subnetList.get(i).get("BASE_SUBNET_ID").toString());
					}
					devDomain.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$in", subnetArray)));
				}
				List<Map<String, Object>> neList = alarmManagementMapper.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (neList != null && neList.size() > 0) {
					neArray = new int[neList.size()];
					for (int i = 0; i < neArray.length; i++) {
						neArray[i]=Integer.parseInt(neList.get(i).get("BASE_NE_ID").toString());
					}
					devDomain.add(new BasicDBObject("NE_ID", new BasicDBObject("$in", neArray)));
				}
				
				if (devDomain.size() > 1) {
					conditionQuery.put("$or", devDomain);
				} else {
					if (emsList.size() > 0) {
						conditionQuery.put("EMS_ID", new BasicDBObject("$in", emsArray));
					} else if (subnetList.size() > 0) {
						conditionQuery.put("SUBNET_ID", new BasicDBObject("$in", subnetArray));
					} else if (neList.size() > 0) {
						conditionQuery.put("NE_ID", new BasicDBObject("$in", neArray));
					}
				}
				// 用户的设备域为空时的处理
				if (emsList.isEmpty() && subnetList.isEmpty() && neList.isEmpty()) {
					System.out.println("该用户没有任何设备域权限");
					// 封装J前台表格数据->总记录数
					valueMap.put("total", 0);
					// 封装前台表格数据->列表信息
					valueMap.put("rows", new ArrayList<DBObject>());
					return valueMap;
				}
			}

		} else { // 显示指定目标的告警		
			// 端口
			if (!"".equals(paramMap.get("ptpId").toString())) {
				String[] ptpArr = formatCondition(paramMap.get("ptpId").toString()).split(",");
				int[] ptpArrI = new int[ptpArr.length];
				for (int i = 0; i < ptpArr.length; i++) {
					ptpArrI[i]=Integer.parseInt(ptpArr[i].trim());
				}
				conditionQuery.put("PTP_ID", new BasicDBObject("$in",ptpArrI));
			// 单元盘
			} else if (!"".equals(paramMap.get("unitId").toString())) {
				conditionQuery.put("UNIT_ID", paramMap.get("unitId"));
			// 网元
			} else if (!"".equals(paramMap.get("neId").toString())) {
				String[] neIdArrS = formatCondition(paramMap.get("neId").toString()).split(",");
				int[] neIdArrI = new int[neIdArrS.length];
				for (int i = 0; i < neIdArrS.length; i++) {
					neIdArrI[i]=Integer.parseInt(neIdArrS[i]);
				}
				conditionQuery.put("NE_ID", new BasicDBObject("$in",neIdArrI));
			// 子网
			} else if (paramMap.get("subnetId")!=null && !"".equals(paramMap.get("subnetId"))) {
				String[] subnetIdArrS = formatCondition(paramMap.get("subnetId").toString()).split(",");
				List<Integer> subnetList = new ArrayList<Integer>();
				for (String id : subnetIdArrS) {
					subnetList.addAll(getSubnetIds(id));
				}
				conditionQuery.put("SUBNET_ID", new BasicDBObject("$in",subnetList));
			// 网管
			} else if (!"".equals(paramMap.get("emsId"))) {
				String[] emsIdArrS = formatCondition(paramMap.get("emsId").toString()).split(",");
				int[] emsIdArrI = new int[emsIdArrS.length];
				for (int i = 0; i < emsIdArrS.length; i++) {
					emsIdArrI[i]=Integer.parseInt(emsIdArrS[i]);
				}
				conditionQuery.put("EMS_ID", new BasicDBObject("$in",emsIdArrI));
			// 网管分组
			} else if (!"".equals(paramMap.get("emsGroupId"))) {
				String[] emsGrpIdArrS = formatCondition(paramMap.get("emsGroupId").toString()).split(",");
				int[] emsGrpIdArrI = new int[emsGrpIdArrS.length];
				for (int i = 0; i < emsGrpIdArrS.length; i++) {
					emsGrpIdArrI[i]=Integer.parseInt(emsGrpIdArrS[i]);
				}
				conditionQuery.put("BASE_EMS_GROUP_ID", new BasicDBObject("$in",emsGrpIdArrI));
			// 局站
			} else if(!"".equals(paramMap.get("stationId"))) {
				conditionQuery.put("STATION_ID", paramMap.get("stationId"));
			}			
		}
		// 指定告警级别
		if (!("all".equals(paramMap.get("alarmLv"))||"".equals(paramMap.get("alarmLv")))) {
			// 网管分组
			if (!"".equals(paramMap.get("emsGroupId"))) {
				String[] emsGrpIdArrS = formatCondition(paramMap.get("emsGroupId").toString()).split(",");
				int[] emsGrpIdArrI = new int[emsGrpIdArrS.length];
				for (int i = 0; i < emsGrpIdArrS.length; i++) {
					emsGrpIdArrI[i]=Integer.parseInt(emsGrpIdArrS[i]);
				}
				conditionQuery.put("BASE_EMS_GROUP_ID", new BasicDBObject("$in",emsGrpIdArrI));
			}
			// 网管
			if (!"".equals(paramMap.get("emsId"))) {
				String[] emsIdArrS = formatCondition(paramMap.get("emsId").toString()).split(",");
				int[] emsIdArrI = new int[emsIdArrS.length];
				for (int i = 0; i < emsIdArrS.length; i++) {
					emsIdArrI[i]=Integer.parseInt(emsIdArrS[i]);
				}
				conditionQuery.put("EMS_ID", new BasicDBObject("$in",emsIdArrI));
			}
			// 子网
			if (paramMap.get("subnetId")!=null && !"".equals(paramMap.get("subnetId"))) {
				String[] subnetIdArrS = formatCondition(paramMap.get("subnetId").toString()).split(",");
				List<Integer> subnetList = new ArrayList<Integer>();
				for (String id : subnetIdArrS) {
					subnetList.addAll(getSubnetIds(id));
				}
				conditionQuery.put("SUBNET_ID", new BasicDBObject("$in",subnetList));
			}
			// 网元
			if (!"".equals(paramMap.get("neId"))) {
				String[] neIdArrS = formatCondition(paramMap.get("neId").toString()).split(",");
				int[] neIdArrI = new int[neIdArrS.length];
				for (int i = 0; i < neIdArrS.length; i++) {
					neIdArrI[i]=Integer.parseInt(neIdArrS[i]);
				}
				conditionQuery.put("NE_ID", new BasicDBObject("$in",neIdArrI));
			}
			// 单元盘
			if (!"".equals(paramMap.get("unitId").toString())) {
				conditionQuery.put("UNIT_ID", paramMap.get("unitId"));
			}
			// 端口
			if (!"".equals(paramMap.get("ptpId").toString())) {
				String[] ptpArr = formatCondition(paramMap.get("ptpId").toString()).split(",");
				int[] ptpArrI = new int[ptpArr.length];
				for (int i = 0; i < ptpArr.length; i++) {
					ptpArrI[i]=Integer.parseInt(ptpArr[i]);
				}
				conditionQuery.put("PTP_ID", new BasicDBObject("$in",ptpArrI));
			}
			// 告警级别
			if (Integer.parseInt(paramMap.get("alarmLv").toString())==5) {
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
			} else {
				conditionQuery.put("PERCEIVED_SEVERITY", paramMap.get("alarmLv"));
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
			}
		}
		// 指定告警状态
		int statusId = !"".equals(paramMap.get("statusId")) ? Integer.parseInt(paramMap.get("statusId").toString())
				: CommonDefine.ALARM_ALL;
		switch (statusId) {
			case CommonDefine.ALARM_ALL:	// 全部
				break;
			case CommonDefine.ALARM_CLEARED:	// 已清除
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
				break;
			case CommonDefine.ALARM_NOT_CLEARED:	// 未清除
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
				break;
			case CommonDefine.ALARM_ACKNOWLEDGED:	// 已确认
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_YES);
				break;
			case CommonDefine.ALARM_ACKNOWLEDGED_CLEARED:	// 已确认已清除
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_YES);
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
				break;
			case CommonDefine.ALARM_ACKNOWLEDGED_NOT_CLEARED:	// 已确认未清除
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_YES);
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
				break;
			case CommonDefine.ALARM_NOT_ACKNOWLEDGED:	// 未确认
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_NO);
				break;
			case CommonDefine.ALARM_CLEARED_NOT_ACKNOWLEDGED:	// 已清除未确认
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_NO);
				break;
			case CommonDefine.ALARM_NOT_ACKNOWLEDGED_NOT_CLEARED:	// 未确认未清除
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_NO);
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
				break;
			default:
				break;
		}
		
		// 当天新增告警统计
		if (paramMap.get("isAddAtToday")!=null && (Boolean) paramMap.get("isAddAtToday")) {			
			Date first,end;
			SimpleDateFormat dateTime = new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
			SimpleDateFormat dateWithoutTime = new SimpleDateFormat(CommonDefine.COMMON_SIMPLE_FORMAT);
			try {
				end = new Date();
				first = dateTime.parse(dateWithoutTime.format(end)+ " 00:00:00");
				conditionQuery.put("FIRST_TIME", new BasicDBObject("$gte",first).append("$lte", end));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		// 指定告警反转状态
		conditionQuery.put("REVERSAL", false);
		// 告警收敛显示标志
		if ((Boolean)paramMap.get("isConverge")) {
			conditionQuery.put("CONVERGE_FLAG", new BasicDBObject("$ne", CommonDefine.ALARM_CONVERGE_DERIVATIVE_ALARM));
		}

		// 封装过滤器条件
//		BasicDBObject conditionFilter = getCurrentAlarmFilterCondition(userId);
		int filterId = !"".equals(paramMap.get("filterId")) ? Integer.parseInt(paramMap.get("filterId").toString())
				: CommonDefine.VALUE_NONE;
		BasicDBObject conditionFilter = getCurrentAlarmFilterConditionByFilterId(filterId);
		
		// 封装子查询条件
		BasicDBList child = new BasicDBList ();
		child.add(conditionQuery);
		child.add(conditionFilter);
		// 合并子查询条件
		BasicDBObject condition = new BasicDBObject ();
		condition.put("$and", child);
		// 根据网元ID，查询当前告警总数
		Date a = new Date();
		BasicDBObject key = new BasicDBObject();
		key.put("IS_CLEAR", 1);
		key.put("_id", 0);
		int count = conn.find(condition, key).count();
		System.out.println("当前告警条件："+ condition);
		CommonUtil.timeDif("当前告警总数：", a, new Date());
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		a = new Date();
		if(count>0){
			// 根据网元ID、分页参数，查询当前告警信息
			DBCursor currentAlarm = conn.find(condition).sort(new BasicDBObject("NE_TIME",-1)).skip(start).limit(limit);
			// 持续时间
			String duration;
			// 最近持续时间
			String recentDuration;
			while (currentAlarm.hasNext()) {
				duration = "";
				recentDuration = "";
				DBObject dbo = currentAlarm.next();
				dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
				dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
				dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
				dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
				dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
				dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
				dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
				// 计算告警持续时间
				if ((Integer)dbo.get("IS_CLEAR") == CommonDefine.IS_CLEAR_YES) {
					duration = getTimeDif(dbo.get("CLEAR_TIME").toString(), dbo.get("FIRST_TIME").toString());
					recentDuration = getTimeDif(dbo.get("CLEAR_TIME").toString(), dbo.get("NE_TIME").toString());
				} else {
					duration = getTimeDif(sf.format(new Date()), dbo.get("FIRST_TIME").toString());
					recentDuration = getTimeDif(sf.format(new Date()), dbo.get("NE_TIME").toString());
				}
				dbo.put("DURATION", duration);
				dbo.put("RECENT_DURATION", recentDuration);
				list.add(dbo);
			}
		}
		CommonUtil.timeDif("当前告警组装：", a, new Date());
		// 封装J前台表格数据->总记录数
		valueMap.put("total", count);
		// 封装前台表格数据->列表信息
		valueMap.put("rows", list);
		CommonUtil.timeDif("当前告警处理小计：", begin, new Date());
		return valueMap;
	}
	
	//格式化查询条件，测试发现传过来的参数中含有"["需要去除
	private String formatCondition(String condition){
		
		condition = condition.trim();
		
		if(condition.startsWith("[")){
			condition = condition.substring(1);
		}
		if(condition.endsWith("]")){
			condition = condition.substring(0, condition.length()-1);
		}
		return condition;
	}
	
	/*
	 * 按指定格式返回两个日期字符串之间的时间差值字符串
	 */
	private String getTimeDif(String beginTime, String endTime) {
		String result = "";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat nf = new DecimalFormat("00");
		try {
			Date beginDate = sf.parse(beginTime);
			Date endDate = sf.parse(endTime);
			long l = beginDate.getTime() - endDate.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			//返回格式"2天 HH:MM:SS"
			result = day + "天 " + nf.format(hour) + ":" + nf.format(min) + ":" + nf.format(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			result = "";
			e.printStackTrace();
		}
		return result;
	}

	private BasicDBObject getCurrentAlarmFilterCondition(Integer userId){
		// 查询过滤器设置
		Map<String, Object> filterParamMap = new HashMap<String, Object>();
		// 过滤器创建人ID
		filterParamMap.put("sysUserId", userId);
		// 过滤器参数不是综告
		filterParamMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_NO);
		filterParamMap.put("status", CommonDefine.ALARM_FILTER_STATUS_ENABLE);
		List<Map<String, Object>> filterMainList = alarmManagementMapper.getAlarmFilterEnableByUserId(filterParamMap);
		// 封装过滤器条件
		BasicDBList conditionFilter = new BasicDBList ();
		if(!filterMainList.isEmpty()){
			// 已选告警名称
			List<Map<String, Object>> nameList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmNameById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
			if(!nameList.isEmpty()){
				// 需要用告警名称和厂家的联合匹配字段
				String[] nameAndFactoryArr = new String[nameList.size()];
				for (int i = 0; i < nameList.size(); i++) {
					nameAndFactoryArr[i] = nameList.get(i).get("NATIVE_PROBABLE_CAUSE").toString() +","+ nameList.get(i).get("FACTORY").toString();
				}
				conditionFilter.add(new BasicDBObject("FILTER_ALARM_NAME_FACTORY", new BasicDBObject("$nin",nameAndFactoryArr)));
			}
			// 告警类型
			List<Map<String, Object>> typeList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmTypeById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
			if(!typeList.isEmpty()){
				int[] typeArr = new int[typeList.size()];
				for (int i = 0; i < typeList.size(); i++) {
					typeArr[i] = Integer.parseInt(typeList.get(i).get("ALARM_TYPE").toString());
				}
				conditionFilter.add(new BasicDBObject("ALARM_TYPE", new BasicDBObject("$nin",typeArr)));
			}
			// 告警级别
			List<Map<String, Object>> levelList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmLevelById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
			if(!levelList.isEmpty()){
				int[] levelArr = new int[levelList.size()];
				for (int i = 0; i < levelList.size(); i++) {
					levelArr[i] = Integer.parseInt(levelList.get(i).get("ALARM_LEVEL").toString());
				}
				conditionFilter.add(new BasicDBObject("PERCEIVED_SEVERITY", new BasicDBObject("$nin",levelArr)));
			}
			// 影响业务
			List<Map<String, Object>> affectList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmAffectlById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
			if(!affectList.isEmpty()){
				int[] affectArr = new int[affectList.size()];
				for (int i = 0; i < affectList.size(); i++) {
					affectArr[i] = Integer.parseInt(affectList.get(i).get("ALARM_AFFECTING").toString());
				}
				conditionFilter.add(new BasicDBObject("SERVICE_AFFECTING", new BasicDBObject("$nin",affectArr)));
			}
			// 告警源
			List<Map<String, Object>> resourceList = alarmManagementMapper.getAlarmFilterSecondtDetailById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
			if(!resourceList.isEmpty()){
				List<Integer> emsGroupList = new ArrayList<Integer>();
				List<Integer> emsList = new ArrayList<Integer>();
				List<Integer> subnetList = new ArrayList<Integer>();
				List<Integer> neList = new ArrayList<Integer>();
				List<Integer> sheifList = new ArrayList<Integer>();
				List<Integer> unitList = new ArrayList<Integer>();
				List<Integer> subunitList = new ArrayList<Integer>();
				List<Integer> ptpList = new ArrayList<Integer>();
				for (int i = 0; i < resourceList.size(); i++) {
					if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.EMSGROUP){
						emsGroupList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.EMS){
						emsList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.SUBNET){
						subnetList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.NE){
						neList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.SHELF){
						sheifList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.UNIT){
						unitList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.SUBUNIT){
						subunitList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.PTP){
						ptpList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					}
				}
				if(!emsGroupList.isEmpty()){
					int[] emsGroupArr = new int[emsGroupList.size()];
					for (int i = 0; i < emsGroupList.size(); i++) {
						emsGroupArr[i] = emsGroupList.get(i);
					}
					conditionFilter.add(new BasicDBObject("BASE_EMS_GROUP_ID", new BasicDBObject("$nin",emsGroupArr)));
				}
				if(!emsList.isEmpty()){
					int[] emsArr = new int[emsList.size()];
					for (int i = 0; i < emsList.size(); i++) {
						emsArr[i] = emsList.get(i);
					} 
					conditionFilter.add(new BasicDBObject("EMS_ID", new BasicDBObject("$nin",emsArr)));
				}
				if(!subnetList.isEmpty()){
					int[] subnetArr = new int[subnetList.size()];
					for (int i = 0; i < subnetList.size(); i++) {
						subnetArr[i] = subnetList.get(i);
					}
					conditionFilter.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$nin",subnetArr)));
				}
				if(!neList.isEmpty()){
					int[] neArr = new int[neList.size()];
					for (int i = 0; i < neList.size(); i++) {
						neArr[i] = neList.get(i);
					}
					conditionFilter.add(new BasicDBObject("NE_ID", new BasicDBObject("$nin",neArr)));
				}
				if(!sheifList.isEmpty()){
					int[] sheifArr = new int[sheifList.size()];
					for (int i = 0; i < sheifList.size(); i++) {
						sheifArr[i] = sheifList.get(i);
					}
					conditionFilter.add(new BasicDBObject("SHELF_ID", new BasicDBObject("$nin",sheifArr)));
				}
				if(!unitList.isEmpty()){
					int[] unitArr = new int[unitList.size()];
					for (int i = 0; i < unitList.size(); i++) {
						unitArr[i] = unitList.get(i);
					}
					conditionFilter.add(new BasicDBObject("UNIT_ID", new BasicDBObject("$nin",unitArr)));
				}
				if(!subunitList.isEmpty()){
					int[] subunitArr = new int[subunitList.size()];
					for (int i = 0; i < subunitList.size(); i++) {
						subunitArr[i] = subunitList.get(i);
					}
					conditionFilter.add(new BasicDBObject("SUB_UNIT_ID", new BasicDBObject("$nin",subunitArr)));
				}
				if(!ptpList.isEmpty()){
					int[] ptpArr = new int[ptpList.size()];
					for (int i = 0; i < ptpList.size(); i++) {
						ptpArr[i] = ptpList.get(i);
					}
					conditionFilter.add(new BasicDBObject("PTP_ID", new BasicDBObject("$nin",ptpArr)));
				}
			}
			// 网元型号
			List<Map<String, Object>> neModelList = alarmManagementMapper.getAlarmFilterThirdNeModelById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
			if(!neModelList.isEmpty()){
				String[] neModelArr = new String[neModelList.size()];
				for (int i = 0; i < neModelList.size(); i++) {
					neModelArr[i] = neModelList.get(i).get("PRODUCT_NAME").toString() +"," +neModelList.get(i).get("FACTORY").toString();
				}
				conditionFilter.add(new BasicDBObject("FILTER_NE_MODEL_FACTORY", new BasicDBObject("$nin",neModelArr)));
			}
			// 端口型号
			List<Map<String, Object>> ptpModelList = alarmManagementMapper.getAlarmFilterThirdPtpModelById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
			List<String> ptpylist = new ArrayList<String>();
			for (int i = 0; i < ptpModelList.size(); i++) {
				if("STM-64~STM-256".equals(ptpModelList.get(i).get("PTP_MODEL").toString())){
					ptpylist.add("STM-64");
					ptpylist.add("STM-256");
				}else if("OSC&OSCNI".equals(ptpModelList.get(i).get("PTP_MODEL").toString())){
					ptpylist.add("OSC");
					ptpylist.add("OSCNI");
				}else{
					ptpylist.add(ptpModelList.get(i).get("PTP_MODEL").toString());
				}
				
			}
			if(!ptpylist.isEmpty()){
				String[] ptpModelArr = new String[ptpylist.size()];
				for (int j = 0; j < ptpylist.size(); j++) {
					ptpModelArr[j] = ptpylist.get(j);
				}
				conditionFilter.add(new BasicDBObject("PTP_TYPE", new BasicDBObject("$nin",ptpModelArr)));
			}
			// 通道告警
			if(Integer.parseInt(filterMainList.get(0).get("CTP_ALARM_FLAG").toString())==CommonDefine.CTP_ALARM_YES){
				conditionFilter.add(new BasicDBObject("OBJECT_TYPE", new BasicDBObject("$ne",CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT)));
			}
		}
		return conditionFilter.isEmpty()?new BasicDBObject():new BasicDBObject("$or",conditionFilter);
	}
	/**
	 * 根据告警过滤器ID，获取告警过滤条件
	 * @param filterId
	 * @return
	 */
	private BasicDBObject getCurrentAlarmFilterConditionByFilterId(int filterId){
		// 查询过滤器设置
		Map<String, Object> filterParamMap = new HashMap<String, Object>();
		// 过滤器为“无”
		if (filterId == CommonDefine.VALUE_NONE)
			return new BasicDBObject();
		
		// 封装过滤器条件
		BasicDBList conditionFilter = new BasicDBList ();
		// 已选告警名称
		List<Map<String, Object>> nameList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmNameById(filterId);
		if(!nameList.isEmpty()){
			// 需要用告警名称和厂家的联合匹配字段
			String[] nameAndFactoryArr = new String[nameList.size()];
			for (int i = 0; i < nameList.size(); i++) {
				nameAndFactoryArr[i] = nameList.get(i).get("NATIVE_PROBABLE_CAUSE").toString() +","+ nameList.get(i).get("FACTORY").toString();
			}
			conditionFilter.add(new BasicDBObject("FILTER_ALARM_NAME_FACTORY", new BasicDBObject("$nin",nameAndFactoryArr)));
		}
		// 告警类型
		List<Map<String, Object>> typeList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmTypeById(filterId);
		if(!typeList.isEmpty()){
			int[] typeArr = new int[typeList.size()];
			for (int i = 0; i < typeList.size(); i++) {
				typeArr[i] = Integer.parseInt(typeList.get(i).get("ALARM_TYPE").toString());
			}
			conditionFilter.add(new BasicDBObject("ALARM_TYPE", new BasicDBObject("$nin",typeArr)));
		}
		// 告警级别
		List<Map<String, Object>> levelList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmLevelById(filterId);
		if(!levelList.isEmpty()){
			int[] levelArr = new int[levelList.size()];
			for (int i = 0; i < levelList.size(); i++) {
				levelArr[i] = Integer.parseInt(levelList.get(i).get("ALARM_LEVEL").toString());
			}
			conditionFilter.add(new BasicDBObject("PERCEIVED_SEVERITY", new BasicDBObject("$nin",levelArr)));
		}
		// 影响业务
		List<Map<String, Object>> affectList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmAffectlById(filterId);
		if(!affectList.isEmpty()){
			int[] affectArr = new int[affectList.size()];
			for (int i = 0; i < affectList.size(); i++) {
				affectArr[i] = Integer.parseInt(affectList.get(i).get("ALARM_AFFECTING").toString());
			}
			conditionFilter.add(new BasicDBObject("SERVICE_AFFECTING", new BasicDBObject("$nin",affectArr)));
		}
		// 告警源
		List<Map<String, Object>> resourceList = alarmManagementMapper.getAlarmFilterSecondtDetailById(filterId);
		if(!resourceList.isEmpty()){
			List<Integer> emsGroupList = new ArrayList<Integer>();
			List<Integer> emsList = new ArrayList<Integer>();
			List<Integer> subnetList = new ArrayList<Integer>();
			List<Integer> neList = new ArrayList<Integer>();
			List<Integer> sheifList = new ArrayList<Integer>();
			List<Integer> unitList = new ArrayList<Integer>();
			List<Integer> subunitList = new ArrayList<Integer>();
			List<Integer> ptpList = new ArrayList<Integer>();
			for (int i = 0; i < resourceList.size(); i++) {
				if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.EMSGROUP){
					emsGroupList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.EMS){
					emsList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.SUBNET){
					subnetList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.NE){
					neList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.SHELF){
					sheifList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.UNIT){
					unitList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.SUBUNIT){
					subunitList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.TREE.NODE.PTP){
					ptpList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
				}
			}
			if(!emsGroupList.isEmpty()){
				int[] emsGroupArr = new int[emsGroupList.size()];
				for (int i = 0; i < emsGroupList.size(); i++) {
					emsGroupArr[i] = emsGroupList.get(i);
				}
				conditionFilter.add(new BasicDBObject("BASE_EMS_GROUP_ID", new BasicDBObject("$nin",emsGroupArr)));
			}
			if(!emsList.isEmpty()){
				int[] emsArr = new int[emsList.size()];
				for (int i = 0; i < emsList.size(); i++) {
					emsArr[i] = emsList.get(i);
				} 
				conditionFilter.add(new BasicDBObject("EMS_ID", new BasicDBObject("$nin",emsArr)));
			}
			if(!subnetList.isEmpty()){
				int[] subnetArr = new int[subnetList.size()];
				for (int i = 0; i < subnetList.size(); i++) {
					subnetArr[i] = subnetList.get(i);
				}
				conditionFilter.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$nin",subnetArr)));
			}
			if(!neList.isEmpty()){
				int[] neArr = new int[neList.size()];
				for (int i = 0; i < neList.size(); i++) {
					neArr[i] = neList.get(i);
				}
				conditionFilter.add(new BasicDBObject("NE_ID", new BasicDBObject("$nin",neArr)));
			}
			if(!sheifList.isEmpty()){
				int[] sheifArr = new int[sheifList.size()];
				for (int i = 0; i < sheifList.size(); i++) {
					sheifArr[i] = sheifList.get(i);
				}
				conditionFilter.add(new BasicDBObject("SHELF_ID", new BasicDBObject("$nin",sheifArr)));
			}
			if(!unitList.isEmpty()){
				int[] unitArr = new int[unitList.size()];
				for (int i = 0; i < unitList.size(); i++) {
					unitArr[i] = unitList.get(i);
				}
				conditionFilter.add(new BasicDBObject("UNIT_ID", new BasicDBObject("$nin",unitArr)));
			}
			if(!subunitList.isEmpty()){
				int[] subunitArr = new int[subunitList.size()];
				for (int i = 0; i < subunitList.size(); i++) {
					subunitArr[i] = subunitList.get(i);
				}
				conditionFilter.add(new BasicDBObject("SUB_UNIT_ID", new BasicDBObject("$nin",subunitArr)));
			}
			if(!ptpList.isEmpty()){
				int[] ptpArr = new int[ptpList.size()];
				for (int i = 0; i < ptpList.size(); i++) {
					ptpArr[i] = ptpList.get(i);
				}
				conditionFilter.add(new BasicDBObject("PTP_ID", new BasicDBObject("$nin",ptpArr)));
			}
		}
		// 网元型号
		List<Map<String, Object>> neModelList = alarmManagementMapper.getAlarmFilterThirdNeModelById(filterId);
		if(!neModelList.isEmpty()){
			String[] neModelArr = new String[neModelList.size()];
			for (int i = 0; i < neModelList.size(); i++) {
				neModelArr[i] = neModelList.get(i).get("PRODUCT_NAME").toString() +"," +neModelList.get(i).get("FACTORY").toString();
			}
			conditionFilter.add(new BasicDBObject("FILTER_NE_MODEL_FACTORY", new BasicDBObject("$nin",neModelArr)));
		}
		// 端口型号
		List<Map<String, Object>> ptpModelList = alarmManagementMapper.getAlarmFilterThirdPtpModelById(filterId);
		List<String> ptpylist = new ArrayList<String>();
		for (int i = 0; i < ptpModelList.size(); i++) {
			if("STM-64~STM-256".equals(ptpModelList.get(i).get("PTP_MODEL").toString())){
				ptpylist.add("STM-64");
				ptpylist.add("STM-256");
			}else if("OSC&OSCNI".equals(ptpModelList.get(i).get("PTP_MODEL").toString())){
				ptpylist.add("OSC");
				ptpylist.add("OSCNI");
			}else{
				ptpylist.add(ptpModelList.get(i).get("PTP_MODEL").toString());
			}
			
		}
		if(!ptpylist.isEmpty()){
			String[] ptpModelArr = new String[ptpylist.size()];
			for (int j = 0; j < ptpylist.size(); j++) {
				ptpModelArr[j] = ptpylist.get(j);
			}
			conditionFilter.add(new BasicDBObject("PTP_TYPE", new BasicDBObject("$nin",ptpModelArr)));
		}
		// 通道告警
		Map filterDetail = commonManagerMapper.selectTableById("T_ALARM_FILTER_DETAIL", "FILTER_ID", filterId);
		if(Integer.parseInt(filterDetail.get("CTP_ALARM_FLAG").toString())==CommonDefine.CTP_ALARM_YES){
			conditionFilter.add(new BasicDBObject("OBJECT_TYPE", new BasicDBObject("$ne",CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT)));
		}

		return conditionFilter.isEmpty()?new BasicDBObject():new BasicDBObject("$or",conditionFilter);
	}
//	@Override
//	public Map<String, Object> getCurrentAlarmCount(Map<String, Object> paramMap)throws CommonException {
//		// 获取数据库连接
//		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
//		// 定义查询结果集
//		Map<String, Object> valueMap = new HashMap<String, Object>();
//		// 封装查询条件
//		BasicDBObject conditionQuery = new BasicDBObject (); 
//		if(!(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId").toString())||"".equals(paramMap.get("emsGroupId")))){
//			if(String.valueOf(CommonDefine.VALUE_NONE).equals(paramMap.get("emsGroupId").toString())){
//				conditionQuery.put("BASE_EMS_GROUP_ID", "");
//			}else{
//				conditionQuery.put("BASE_EMS_GROUP_ID", paramMap.get("emsGroupId"));
//			}
//		}else{
//			conditionQuery.put("BASE_EMS_GROUP_ID", new BasicDBObject("$in",getEmsGroupIdListBySysID()));
//		}
//		if(!(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsId").toString())||"".equals(paramMap.get("emsId")))){
//			conditionQuery.put("EMS_ID", paramMap.get("emsId"));
//		}else{
//			conditionQuery.put("EMS_ID", new BasicDBObject("$in",getEmsIdListByGroupAndSysID("".equals(paramMap.get("emsGroupId").toString())?-99:Integer.parseInt(paramMap.get("emsGroupId").toString()))));
//		}
//		if(!(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("neId").toString())||"".equals(paramMap.get("neId")))){
//			conditionQuery.put("NE_ID", paramMap.get("neId"));
//		}
//		if(Integer.parseInt(paramMap.get("alarmLv").toString())==5){
//			conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
//		}else{
//			conditionQuery.put("PERCEIVED_SEVERITY", paramMap.get("alarmLv"));
//			conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
//		}
//		// 查询过滤器设置
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Map<String, Object> filterParamMap = new HashMap<String, Object>();
//		// 过滤器创建人ID
//		filterParamMap.put("sysUserId", request.getSession().getAttribute("SYS_USER_ID"));
//		// 过滤器参数不是综告
//		filterParamMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_NO);
//		filterParamMap.put("status", CommonDefine.ALARM_FILTER_STATUS_ENABLE);
//		List<Map<String, Object>> filterMainList = faultManagerMapper.getAlarmFilterEnableByUserId(filterParamMap);
//		// 封装过滤器条件
//		BasicDBObject conditionFilter = new BasicDBObject (); 
//		if(!filterMainList.isEmpty()){
//			// 已选告警名称
//			List<Map<String, Object>> nameList = faultManagerMapper.getAlarmFilterFirstDetail_AlarmNameById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
//			if(!nameList.isEmpty()){
//				// 需要用告警名称和厂家的联合匹配字段
//				String[] nameAndFactoryArr = new String[nameList.size()];
//				for (int i = 0; i < nameList.size(); i++) {
//					nameAndFactoryArr[i] = nameList.get(i).get("NATIVE_PROBABLE_CAUSE").toString() + nameList.get(i).get("FACTORY").toString();
//				}
//				conditionFilter.put("FILTER_ALARM_NAME_FACTORY", new BasicDBObject("$nin",nameAndFactoryArr));
//			}
//			// 告警类型
//			List<Map<String, Object>> typeList = faultManagerMapper.getAlarmFilterFirstDetail_AlarmTypeById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
//			if(!typeList.isEmpty()){
//				int[] typeArr = new int[typeList.size()];
//				for (int i = 0; i < typeList.size(); i++) {
//					typeArr[i] = Integer.parseInt(typeList.get(i).get("ALARM_TYPE").toString());
//				}
//				conditionFilter.put("ALARM_TYPE", new BasicDBObject("$nin",typeArr));
//			}
//			// 告警级别
//			List<Map<String, Object>> levelList = faultManagerMapper.getAlarmFilterFirstDetail_AlarmLevelById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
//			
//			if(!levelList.isEmpty()){
//				int[] levelArr = new int[levelList.size()];
//				for (int i = 0; i < levelList.size(); i++) {
//					levelArr[i] = Integer.parseInt(levelList.get(i).get("ALARM_LEVEL").toString());
//				}
//				conditionFilter.put("PERCEIVED_SEVERITY", new BasicDBObject("$nin",levelArr));
//			}
//			// 影响业务
//			List<Map<String, Object>> affectList = faultManagerMapper.getAlarmFilterFirstDetail_AlarmAffectlById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
//			if(!affectList.isEmpty()){
//				int[] affectArr = new int[affectList.size()];
//				for (int i = 0; i < affectList.size(); i++) {
//					affectArr[i] = Integer.parseInt(affectList.get(i).get("ALARM_AFFECTING").toString());
//				}
//				conditionFilter.put("SERVICE_AFFECTING", new BasicDBObject("$nin",affectArr));
//			}
//			// 告警源
//			List<Map<String, Object>> resourceList = faultManagerMapper.getAlarmFilterSecondtDetailById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
//			if(!resourceList.isEmpty()){
//				List<Integer> emsGroupList = new ArrayList<Integer>();
//				List<Integer> emsList = new ArrayList<Integer>();
//				List<Integer> subnetList = new ArrayList<Integer>();
//				List<Integer> neList = new ArrayList<Integer>();
//				List<Integer> sheifList = new ArrayList<Integer>();
//				List<Integer> unitList = new ArrayList<Integer>();
//				List<Integer> subunitList = new ArrayList<Integer>();
//				List<Integer> ptpList = new ArrayList<Integer>();
//				for (int i = 0; i < resourceList.size(); i++) {
//					if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_EMSGROUP){
//						emsGroupList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_EMS){
//						emsList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_SUBNET){
//						subnetList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_NE){
//						neList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_SHELF){
//						sheifList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_UNIT){
//						unitList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_SUBUNIT){
//						subunitList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}else if(Integer.parseInt(resourceList.get(i).get("DEVICE_TYPE").toString())==CommonDefine.ALARM_RESOURCE_PTP){
//						ptpList.add(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
//					}
//				}
//				if(!emsGroupList.isEmpty()){
//					int[] emsGroupArr = new int[emsGroupList.size()];
//					for (int i = 0; i < emsGroupList.size(); i++) {
//						emsGroupArr[i] = emsGroupList.get(i);
//					}
//					conditionFilter.put("BASE_EMS_GROUP_ID", new BasicDBObject("$nin",emsGroupArr));
//				}
//				if(!emsList.isEmpty()){
//					int[] emsArr = new int[emsList.size()];
//					for (int i = 0; i < emsList.size(); i++) {
//						emsArr[i] = emsList.get(i);
//					} 
//					conditionFilter.put("EMS_ID", new BasicDBObject("$nin",emsArr));
//				}
//				if(!subnetList.isEmpty()){
//					int[] subnetArr = new int[subnetList.size()];
//					for (int i = 0; i < subnetList.size(); i++) {
//						subnetArr[i] = subnetList.get(i);
//					}
//					conditionFilter.put("SUBNET_ID", new BasicDBObject("$nin",subnetArr));
//				}
//				if(!neList.isEmpty()){
//					int[] neArr = new int[neList.size()];
//					for (int i = 0; i < neList.size(); i++) {
//						neArr[i] = neList.get(i);
//					}
//					conditionFilter.put("NE_ID", new BasicDBObject("$nin",neArr));
//				}
//				if(!sheifList.isEmpty()){
//					int[] sheifArr = new int[sheifList.size()];
//					for (int i = 0; i < sheifList.size(); i++) {
//						sheifArr[i] = sheifList.get(i);
//					}
//					conditionFilter.put("SHELF_ID", new BasicDBObject("$nin",sheifArr));
//				}
//				if(!unitList.isEmpty()){
//					int[] unitArr = new int[unitList.size()];
//					for (int i = 0; i < unitList.size(); i++) {
//						unitArr[i] = unitList.get(i);
//					}
//					conditionFilter.put("UNIT_ID", new BasicDBObject("$nin",unitArr));
//				}
//				if(!subunitList.isEmpty()){
//					int[] subunitArr = new int[subunitList.size()];
//					for (int i = 0; i < subunitList.size(); i++) {
//						subunitArr[i] = subunitList.get(i);
//					}
//					conditionFilter.put("SUB_UNIT_ID", new BasicDBObject("$nin",subunitArr));
//				}
//				if(!ptpList.isEmpty()){
//					int[] ptpArr = new int[ptpList.size()];
//					for (int i = 0; i < ptpList.size(); i++) {
//						ptpArr[i] = ptpList.get(i);
//					}
//					conditionFilter.put("PTP_ID", new BasicDBObject("$nin",ptpArr));
//				}
//			}
//			// 网元型号
//			List<Map<String, Object>> neModelList = faultManagerMapper.getAlarmFilterThirdNeModelById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
//			if(!neModelList.isEmpty()){
//				String[] neModelArr = new String[neModelList.size()];
//				for (int i = 0; i < neModelList.size(); i++) {
//					neModelArr[i] = neModelList.get(i).get("PRODUCT_NAME").toString() + neModelList.get(i).get("FACTORY").toString();
//				}
//				conditionFilter.put("FILTER_NE_MODEL_FACTORY", new BasicDBObject("$nin",neModelArr));
//			}
//			// 端口型号
//			List<Map<String, Object>> ptpModelList = faultManagerMapper.getAlarmFilterThirdPtpModelById(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString()));
//			int[] ptpModelArr = new int[ptpModelList.size()];
//			for (int i = 0; i < ptpModelList.size(); i++) {
//				ptpModelArr[i] = Integer.parseInt(ptpModelList.get(i).get("PTP_MODEL").toString());
//			}
//			conditionFilter.put("PTP_TYPE", new BasicDBObject("$nin",ptpModelArr));
//			// 通道告警
//			if(Integer.parseInt(filterMainList.get(0).get("FILTER_ID").toString())==CommonDefine.CTP_ALARM_YES){
//				conditionFilter.put("OBJECT_TYPE", new BasicDBObject("$ne",CommonDefine.ALARM_CTP));
//			}
//		}
//		// 封装子查询条件
//		BasicDBList child = new BasicDBList ();
//		child.add(conditionQuery);
//		child.add(conditionFilter);
//		// 合并子查询条件
//		BasicDBObject condition = new BasicDBObject ();
//		condition.put("$and", child);
//		// 根据网管分组ID、网管ID、网元ID、告警级别，查询当前告警总数
//		int count = conn.find(condition).count();
//		// 封装J前台告警灯数据->总记录数
//		valueMap.put("total", count);
//		return valueMap;
//	}
	
	@Override
	public Map<String, Object> getAllCurrentAlarmCount(
			Map<String, Object> paramMap) {
		Date begin = new Date();
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);

		// 获取当前用户ID
		HttpServletRequest request = ServletActionContext.getRequest();
		Integer userId = (Integer)request.getSession().getAttribute("SYS_USER_ID");
		
		// 封装查询条件
		BasicDBObject conditionQuery = new BasicDBObject (); 

		if ("".equals(paramMap.get("emsGroupId")) &&
				"".equals(paramMap.get("emsId")) &&
				"".equals(paramMap.get("subnetId")) &&
				"".equals(paramMap.get("neId")) &&
				"".equals(paramMap.get("unitId")) &&
				"".equals(paramMap.get("ptpId")) && 
				"".equals(paramMap.get("stationId"))) {
			// 显示非管理员用户的所有告警
			if (userId != CommonDefine.USER_ADMIN_ID) {
				int[] emsArray = null; int[] subnetArray = null; int[] neArray = null;
				BasicDBList devDomain = new BasicDBList ();				
				List<Map<String, Object>> emsList = alarmManagementMapper.getEmsIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (emsList != null && emsList.size() > 0) {
					emsArray = new int[emsList.size()];
					for (int i = 0; i < emsArray.length; i++) {
						emsArray[i]=Integer.parseInt(emsList.get(i).get("BASE_EMS_CONNECTION_ID").toString());
					}
					devDomain.add(new BasicDBObject("EMS_ID", new BasicDBObject("$in", emsArray)));
				}
				List<Map<String, Object>> subnetList = alarmManagementMapper.getSubnetIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (subnetList != null && subnetList.size() > 0) {
					subnetArray = new int[subnetList.size()];
					for (int i = 0; i < subnetArray.length; i++) {
						subnetArray[i]=Integer.parseInt(subnetList.get(i).get("BASE_SUBNET_ID").toString());
					}
					devDomain.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$in", subnetArray)));
				}
				List<Map<String, Object>> neList = alarmManagementMapper.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (neList != null && neList.size() > 0) {
					neArray = new int[neList.size()];
					for (int i = 0; i < neArray.length; i++) {
						neArray[i]=Integer.parseInt(neList.get(i).get("BASE_NE_ID").toString());
					}
					devDomain.add(new BasicDBObject("NE_ID", new BasicDBObject("$in", neArray)));
				}
				
				if (devDomain.size() > 1) {
					conditionQuery.put("$or", devDomain);
				} else {
					if (emsList.size() > 0) {
						conditionQuery.put("EMS_ID", new BasicDBObject("$in", emsArray));
					} else if (subnetList.size() > 0) {
						conditionQuery.put("SUBNET_ID", new BasicDBObject("$in", subnetArray));
					} else if (neList.size() > 0) {
						conditionQuery.put("NE_ID", new BasicDBObject("$in", neArray));
					}
				}
				// 用户的设备域为空时的处理
				if (emsList.isEmpty() && subnetList.isEmpty() && neList.isEmpty()) {
					System.out.println("该用户没有任何设备域权限");
					Map<String,Object> remap = new HashMap<String, Object>();
					remap.put("PS_CRITICAL", 0);
					remap.put("PS_MAJOR", 0);
					remap.put("PS_MINOR", 0);
					remap.put("PS_WARNING", 0);
					remap.put("PS_CLEARED", 0);
					return remap;
				}
			}

		} else { // 显示指定目标的告警
			// 端口
			if (!"".equals(paramMap.get("ptpId").toString())) {
				String[] ptpArr = paramMap.get("ptpId").toString().split(",");
				int[] ptpArrI = new int[ptpArr.length];
				for (int i = 0; i < ptpArr.length; i++) {
					ptpArrI[i]=Integer.parseInt(ptpArr[i]);
				}
				conditionQuery.put("PTP_ID", new BasicDBObject("$in",ptpArrI));
			// 单元盘
			} else if (!"".equals(paramMap.get("unitId").toString())) {
				conditionQuery.put("UNIT_ID", paramMap.get("unitId"));
			// 网元
			} else if (!"".equals(paramMap.get("neId"))) {
				String[] neIdArrS = paramMap.get("neId").toString().split(",");
				int[] neIdArrI = new int[neIdArrS.length];
				for (int i = 0; i < neIdArrS.length; i++) {
					neIdArrI[i]=Integer.parseInt(neIdArrS[i]);
				}
				conditionQuery.put("NE_ID", new BasicDBObject("$in",neIdArrI));
			// 子网
			} else if (paramMap.get("subnetId")!=null && !"".equals(paramMap.get("subnetId"))) {
				String[] subnetIdArrS = paramMap.get("subnetId").toString().split(",");
				List<Integer> subnetList = new ArrayList<Integer>();
				for (String id : subnetIdArrS) {
					subnetList.addAll(getSubnetIds(id));
				}
				conditionQuery.put("SUBNET_ID", new BasicDBObject("$in",subnetList));
			// 网管
			} else if (!"".equals(paramMap.get("emsId"))) {
				String[] emsIdArrS = paramMap.get("emsId").toString().split(",");
				int[] emsIdArrI = new int[emsIdArrS.length];
				for (int i = 0; i < emsIdArrS.length; i++) {
					emsIdArrI[i]=Integer.parseInt(emsIdArrS[i]);
				}
				conditionQuery.put("EMS_ID", new BasicDBObject("$in",emsIdArrI));
			// 网管分组
			} else if (!"".equals(paramMap.get("emsGroupId"))) {
				String[] emsGrpIdArrS = paramMap.get("emsGroupId").toString().split(",");
				int[] emsGrpIdArrI = new int[emsGrpIdArrS.length];
				for (int i = 0; i < emsGrpIdArrS.length; i++) {
					emsGrpIdArrI[i]=Integer.parseInt(emsGrpIdArrS[i]);
				}
				conditionQuery.put("BASE_EMS_GROUP_ID", new BasicDBObject("$in",emsGrpIdArrI));
			// 局站
			} else if(!"".equals(paramMap.get("stationId"))) {
				conditionQuery.put("STATION_ID", paramMap.get("stationId"));
			}
		}
		
		// 当天新增告警统计
		if ((Boolean) paramMap.get("isAddAtToday")) {			
			Date start,end;
			SimpleDateFormat dateTime = new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
			SimpleDateFormat dateWithoutTime = new SimpleDateFormat(CommonDefine.COMMON_SIMPLE_FORMAT);
			try {
				end = new Date();
				start = dateTime.parse(dateWithoutTime.format(end)+ " 00:00:00");
				conditionQuery.put("FIRST_TIME", new BasicDBObject("$gte",start).append("$lte", end));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// 指定告警状态
		int statusId = !"".equals(paramMap.get("statusId")) ? Integer.parseInt(paramMap.get("statusId").toString())
				: CommonDefine.ALARM_ALL;
		switch (statusId) {
			case CommonDefine.ALARM_ALL:	// 全部
				break;
			case CommonDefine.ALARM_CLEARED:	// 已清除
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
				break;
			case CommonDefine.ALARM_NOT_CLEARED:	// 未清除
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
				break;
			case CommonDefine.ALARM_ACKNOWLEDGED:	// 已确认
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_YES);
				break;
			case CommonDefine.ALARM_ACKNOWLEDGED_CLEARED:	// 已确认已清除
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_YES);
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
				break;
			case CommonDefine.ALARM_ACKNOWLEDGED_NOT_CLEARED:	// 已确认未清除
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_YES);
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
				break;
			case CommonDefine.ALARM_NOT_ACKNOWLEDGED:	// 未确认
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_NO);
				break;
			case CommonDefine.ALARM_CLEARED_NOT_ACKNOWLEDGED:	// 已清除未确认
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_NO);
				break;
			case CommonDefine.ALARM_NOT_ACKNOWLEDGED_NOT_CLEARED:	// 未确认未清除
				conditionQuery.put("IS_ACK", CommonDefine.IS_ACK_NO);
				conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
				break;
			default:
				break;
		}
		// 指定告警反转状态
		conditionQuery.put("REVERSAL", false);
		// 封装过滤器条件
//		BasicDBObject conditionFilter = getCurrentAlarmFilterCondition(userId); 
		int filterId = !"".equals(paramMap.get("filterId")) ? Integer.parseInt(paramMap.get("filterId").toString())
				:CommonDefine.VALUE_NONE;
		BasicDBObject conditionFilter = getCurrentAlarmFilterConditionByFilterId(filterId);
		
		// 限定返回数据的key
		BasicDBObject key = new BasicDBObject();
		key.put("IS_CLEAR_YES", 1);
		key.put("PERCEIVED_SEVERITY", 1);
		key.put("_id", 0);
		// 清除告警计数
		BasicDBList child = new BasicDBList ();
		child.add(conditionQuery);
		child.add(conditionFilter);
		child.add(new BasicDBObject("IS_CLEAR", CommonDefine.IS_CLEAR_YES));
		Date a = new Date();		
		long clNo = conn.find(new BasicDBObject("$and", child), key).count();
//		System.out.println("CL条件："+new BasicDBObject("$and", child));
//		CommonUtil.timeDif("CL计数：", a, new Date());
		// CR告警计数
		child.remove(new BasicDBObject("IS_CLEAR", CommonDefine.IS_CLEAR_YES));
		child.add(new BasicDBObject("IS_CLEAR", CommonDefine.IS_CLEAR_NO));
		child.add(new BasicDBObject("PERCEIVED_SEVERITY", CommonDefine.ALARM_PS_CRITICAL));
		a = new Date();
		long crNo = conn.find(new BasicDBObject("$and", child), key).count();
//		System.out.println("CR条件："+new BasicDBObject("$and", child));
//		CommonUtil.timeDif("CR计数：", a, new Date());
		// MJ告警计数
		child.remove(new BasicDBObject("PERCEIVED_SEVERITY", CommonDefine.ALARM_PS_CRITICAL));
		child.add(new BasicDBObject("PERCEIVED_SEVERITY", CommonDefine.ALARM_PS_MAJOR));
		a = new Date();
		long mjNo = conn.find(new BasicDBObject("$and", child), key).count();
//		System.out.println("MJ条件："+new BasicDBObject("$and", child));
//		CommonUtil.timeDif("MJ计数：", a, new Date());
		// MN告警计数
		child.remove(new BasicDBObject("PERCEIVED_SEVERITY", CommonDefine.ALARM_PS_MAJOR));
		child.add(new BasicDBObject("PERCEIVED_SEVERITY", CommonDefine.ALARM_PS_MINOR));
		a = new Date();
		long mnNo = conn.find(new BasicDBObject("$and", child), key).count();
//		System.out.println("MN条件："+new BasicDBObject("$and", child));
//		CommonUtil.timeDif("MN计数：", a, new Date());
		// WR告警计数
		child.remove(new BasicDBObject("PERCEIVED_SEVERITY", CommonDefine.ALARM_PS_MINOR));
		child.add(new BasicDBObject("PERCEIVED_SEVERITY", CommonDefine.ALARM_PS_WARNING));
		a = new Date();
		long wrNo = conn.find(new BasicDBObject("$and", child), key).count();
//		System.out.println("WR条件："+new BasicDBObject("$and", child));
//		CommonUtil.timeDif("WR计数：", a, new Date());
		
		Map<String,Object> remap = new HashMap<String, Object>();
		remap.put("PS_CRITICAL", crNo);
		remap.put("PS_MAJOR", mjNo);
		remap.put("PS_MINOR", mnNo);
		remap.put("PS_WARNING", wrNo);
		remap.put("PS_CLEARED", clNo);
		CommonUtil.timeDif("告警计数器处理：", begin, new Date());
		return remap;
	}
	
	@Override
	public Map<String, Object> getCurrentAlarmCountForFP() throws CommonException {
		Map<String,Object> result = new HashMap<String, Object>();
		Map<String,Object> params = new HashMap<String, Object>();
		Date start = new Date();
		// 获取告警颜色配置
		Map<String, Object> colorMap = getAlarmColorSet();
		result.put("colorCR", colorMap.get("PS_CRITICAL_IMAGE"));
		result.put("colorMJ", colorMap.get("PS_MAJOR_IMAGE"));
		result.put("colorMN", colorMap.get("PS_MINOR_IMAGE"));
		result.put("colorWR", colorMap.get("PS_WARNING_IMAGE"));
		// 获取当前用户ID
		HttpServletRequest request = ServletActionContext.getRequest();
		int userId = (Integer)request.getSession().getAttribute("SYS_USER_ID");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 过滤器参数不是综告
		paramMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_NO);
		paramMap.put("sysUserId", userId);
		List<Map<String, Object>> filterList = alarmManagementMapper.getAlarmFilterSummaryByUserId(paramMap);
		// 获取当前用户创建的目前处于激活状态的过滤器，如无激活状态过滤器则不设置过滤器
		int filterId = CommonDefine.VALUE_NONE;
		for (Map<String, Object> filter : filterList) {
			if (Integer.valueOf(filter.get("STATUS").toString())==CommonDefine.ALARM_FILTER_STATUS_ENABLE)
				filterId = (Integer)filter.get("FILTER_ID");
		}
		// 设置获取告警计数的默认参数
		params.put("emsGroupId", "");
		params.put("emsId", "");
		params.put("subnetId", "");
		params.put("neId", "");
		params.put("alarmLv", "");
		params.put("ptpId", "");
		params.put("unitId", "");
		params.put("stationId", "");
		params.put("isAddAtToday", false);
		params.put("filterId", filterId);
		params.put("statusId", CommonDefine.ALARM_ALL);
		// 获取告警计数（总数）
		Map<String,Object> remap = getAllCurrentAlarmCount(params);
		result.put("totalCR", remap.get("PS_CRITICAL"));
		result.put("totalMJ", remap.get("PS_MAJOR"));
		result.put("totalMN", remap.get("PS_MINOR"));
		result.put("totalWR", remap.get("PS_WARNING"));
		// 获取告警计数（当天新增数）
		params.put("isAddAtToday", true);
		remap = getAllCurrentAlarmCount(params);
		result.put("newAddCR", remap.get("PS_CRITICAL"));
		result.put("newAddMJ", remap.get("PS_MAJOR"));
		result.put("newAddMN", remap.get("PS_MINOR"));
		result.put("newAddWR", remap.get("PS_WARNING"));		
		CommonUtil.timeDif("首页告警统计处理：", start, new Date());
		return result;
	}
	
	@Override
	public Map<String, Object> getHistoryAlarms(Map<String, Object> paramMap,int start, int limit) throws CommonException, ParseException {
		Date begin = new Date();
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 获取当前用户ID
		HttpServletRequest request = ServletActionContext.getRequest();
		Integer userId = (Integer)request.getSession().getAttribute("SYS_USER_ID");
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject (); 

		if ("".equals(paramMap.get("emsGroupId")) &&
				"".equals(paramMap.get("emsId")) &&
				"".equals(paramMap.get("subnetId")) &&
				"".equals(paramMap.get("neId"))) {
			// 显示非管理员用户的所有告警
			if (userId != CommonDefine.USER_ADMIN_ID) {
				int[] emsArray = null; int[] subnetArray = null; int[] neArray = null;
				BasicDBList devDomain = new BasicDBList ();				
				List<Map<String, Object>> emsList = alarmManagementMapper.getEmsIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (emsList != null && emsList.size() > 0) {
					emsArray = new int[emsList.size()];
					for (int i = 0; i < emsArray.length; i++) {
						emsArray[i]=Integer.parseInt(emsList.get(i).get("BASE_EMS_CONNECTION_ID").toString());
					}
					devDomain.add(new BasicDBObject("EMS_ID", new BasicDBObject("$in", emsArray)));
				}
				List<Map<String, Object>> subnetList = alarmManagementMapper.getSubnetIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (subnetList != null && subnetList.size() > 0) {
					subnetArray = new int[subnetList.size()];
					for (int i = 0; i < subnetArray.length; i++) {
						subnetArray[i]=Integer.parseInt(subnetList.get(i).get("BASE_SUBNET_ID").toString());
					}
					devDomain.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$in", subnetArray)));
				}
				List<Map<String, Object>> neList = alarmManagementMapper.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (neList != null && neList.size() > 0) {
					neArray = new int[neList.size()];
					for (int i = 0; i < neArray.length; i++) {
						neArray[i]=Integer.parseInt(neList.get(i).get("BASE_NE_ID").toString());
					}
					devDomain.add(new BasicDBObject("NE_ID", new BasicDBObject("$in", neArray)));
				}
				
				if (devDomain.size() > 1) {
					condition.put("$or", devDomain);
				} else {
					if (emsList.size() > 0) {
						condition.put("EMS_ID", new BasicDBObject("$in", emsArray));
					} else if (subnetList.size() > 0) {
						condition.put("SUBNET_ID", new BasicDBObject("$in", subnetArray));
					} else if (neList.size() > 0) {
						condition.put("NE_ID", new BasicDBObject("$in", neArray));
					}
				}
				// 用户的设备域为空时的处理
				if (emsList.isEmpty() && subnetList.isEmpty() && neList.isEmpty()) {
					System.out.println("该用户没有任何设备域权限");
					// 封装J前台表格数据->总记录数
					valueMap.put("total", 0);
					// 封装前台表格数据->列表信息
					valueMap.put("rows", new ArrayList<DBObject>());
					return valueMap;
				}
			}
		} else { // 显示指定目标的告警		
			// 网元
			if (!"".equals(paramMap.get("neId"))) {
				String[] neIdArrS = paramMap.get("neId").toString().split(",");
				int[] neIdArrI = new int[neIdArrS.length];
				for (int i = 0; i < neIdArrS.length; i++) {
					neIdArrI[i]=Integer.parseInt(neIdArrS[i]);
				}
				condition.put("NE_ID", new BasicDBObject("$in",neIdArrI));
			// 子网
			} else if (paramMap.get("subnetId")!=null && !"".equals(paramMap.get("subnetId"))) {
				String[] subnetIdArrS = paramMap.get("subnetId").toString().split(",");
				List<Integer> subnetList = new ArrayList<Integer>();
				for (String id : subnetIdArrS) {
					subnetList.addAll(getSubnetIds(id));
				}
				condition.put("SUBNET_ID", new BasicDBObject("$in",subnetList));
			// 网管
			} else if (!"".equals(paramMap.get("emsId"))) {
				String[] emsIdArrS = paramMap.get("emsId").toString().split(",");
				int[] emsIdArrI = new int[emsIdArrS.length];
				for (int i = 0; i < emsIdArrS.length; i++) {
					emsIdArrI[i]=Integer.parseInt(emsIdArrS[i]);
				}
				condition.put("EMS_ID", new BasicDBObject("$in",emsIdArrI));
			// 网管分组
			} else if (!"".equals(paramMap.get("emsGroupId"))) {
				String[] emsGrpIdArrS = paramMap.get("emsGroupId").toString().split(",");
				int[] emsGrpIdArrI = new int[emsGrpIdArrS.length];
				for (int i = 0; i < emsGrpIdArrS.length; i++) {
					emsGrpIdArrI[i]=Integer.parseInt(emsGrpIdArrS[i]);
				}
				condition.put("BASE_EMS_GROUP_ID", new BasicDBObject("$in",emsGrpIdArrI));
			}						
		}

		if(!"".equals(paramMap.get("firstStart"))){
			condition.put("FIRST_TIME", new BasicDBObject("$gte",sf.parse(paramMap.get("firstStart").toString())).append("$lte",sf.parse(paramMap.get("firstEnd").toString())));
		}
		if(!"".equals(paramMap.get("clearStart"))){
			condition.put("CLEAR_TIME", new BasicDBObject("$gte",sf.parse(paramMap.get("clearStart").toString())).append("$lte",sf.parse(paramMap.get("clearEnd").toString())));//>=
		}
		
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		// 根据网元ID、查询历史告警总数
		Date a = new Date();
		BasicDBObject key = new BasicDBObject();
		key.put("FIRST_TIME", 1);
		key.put("_id", 0);
		int count = conn.find(condition, key).count();
		System.out.println("历史告警条件：" + condition);
		CommonUtil.timeDif("历史告警总数：", a, new Date());
		if(count>0){
			// 根据网元ID、分页参数，查询历史告警信息
			DBCursor historyAlarm = conn.find(condition).sort(new BasicDBObject("NE_TIME",-1)).skip(start).limit(limit);
			a = new Date();
			String duration;
			while (historyAlarm.hasNext()) {
				DBObject dbo = historyAlarm.next();
				dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
				dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
				dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
				dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
				dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
				// 计算告警持续时间
				duration = getTimeDif(dbo.get("CLEAR_TIME").toString(), dbo.get("FIRST_TIME").toString());
				dbo.put("DURATION", duration);
				list.add(dbo);
			}
			CommonUtil.timeDif("历史告警组装：", a, new Date());
		}
		// 封装J前台表格数据->总记录数
		valueMap.put("total", count);
		// 封装前台表格数据->列表信息
		valueMap.put("rows", list);
		CommonUtil.timeDif("历史告警处理小计：", begin, new Date());
		return valueMap;
	}
	
	@Override
	public JSONObject getAlarmDetail(Map<String, Object> paramMap)throws CommonException {
		// 获取数据库连接
		DBCollection conn = null;
		if("current".equals(paramMap.get("type"))){
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		}else{
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		}
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 定义查询结果集
		JSONObject json = new JSONObject();
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject (); 
		// 这里可能是alarm在跳jsp时变成的字符串，所以需类型转换
		condition.put("_id",Integer.parseInt(paramMap.get("alarmId").toString()));
		// 根据告警ID、告警种类(当前、历史)，查询告警详情
		DBObject currentAlarm = conn.findOne(condition);
		// 告警时间格式化
		currentAlarm.put("FIRST_TIME", "".equals(currentAlarm.get("FIRST_TIME"))?"":sf.format(currentAlarm.get("FIRST_TIME")));
		currentAlarm.put("UPDATE_TIME", "".equals(currentAlarm.get("UPDATE_TIME"))?"":sf.format(currentAlarm.get("UPDATE_TIME")));
		currentAlarm.put("CLEAR_TIME", "".equals(currentAlarm.get("CLEAR_TIME"))?"":sf.format(currentAlarm.get("CLEAR_TIME")));
		currentAlarm.put("ACK_TIME", "".equals(currentAlarm.get("ACK_TIME"))?"":sf.format(currentAlarm.get("ACK_TIME")));
		currentAlarm.put("NE_TIME", "".equals(currentAlarm.get("NE_TIME"))?"":sf.format(currentAlarm.get("NE_TIME")));
		currentAlarm.put("EMS_TIME", "".equals(currentAlarm.get("EMS_TIME"))?"":sf.format(currentAlarm.get("EMS_TIME")));
		currentAlarm.put("CREATE_TIME", "".equals(currentAlarm.get("CREATE_TIME"))?"":sf.format(currentAlarm.get("CREATE_TIME")));
		// 计算告警持续时间
		String duration=""; //持续时间
		String recentDuration = ""; //最近持续时间
		if ((Integer)currentAlarm.get("IS_CLEAR") == CommonDefine.IS_CLEAR_YES) {
			duration = getTimeDif(currentAlarm.get("CLEAR_TIME").toString(), currentAlarm.get("FIRST_TIME").toString());
			recentDuration = getTimeDif(currentAlarm.get("CLEAR_TIME").toString(), currentAlarm.get("NE_TIME").toString());
		} else {
			duration = getTimeDif(sf.format(new Date()), currentAlarm.get("FIRST_TIME").toString());
			recentDuration = getTimeDif(sf.format(new Date()), currentAlarm.get("NE_TIME").toString());
		}
		currentAlarm.put("DURATION", duration);
		currentAlarm.put("RECENT_DURATION", recentDuration);
		// 计算告警入库时间(告警入库时间-网管发送时间)
		String passTime = "";
		try {
			if (!"".equals(currentAlarm.get("CREATE_TIME")) && !"".equals(currentAlarm.get("EMS_TIME"))) {
				Date createTime = sf.parse(currentAlarm.get("CREATE_TIME").toString());
				Date emsTime = sf.parse(currentAlarm.get("EMS_TIME").toString());
				passTime = "" + (createTime.getTime() - emsTime.getTime()) / 1000 + "秒";				
			} else {
				passTime = "未知";
			}
		} catch (ParseException e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		currentAlarm.put("PASS_TIME", passTime);
		
		Object o=currentAlarm.get("ALARM_REASON");
		StringBuffer sbf=new StringBuffer();
		if(o!=null && !"".equals(o.toString()) && o.toString().contains("\\")){
			String[] ss;
			if(o.toString().contains("\\\\")){
				ss=o.toString().replaceAll("\\\\\\\\","zyyuuyyz").split("zyyuuyyz");
			}else{
				ss=o.toString().replaceAll("\\\\","zyyuuyyz").split("zyyuuyyz");
			}
			
			sbf.append("<table style='font-size:12px'>");
			for(int i=0;i<ss.length;i++){
				if(!"".equals(ss[i])){
					sbf.append("<tr><td>"+ss[i]+"</td></tr>");
				}
			}
			sbf.append("</table>");
			currentAlarm.put("ALARM_REASON",sbf.toString());
		}
		
		
		Object s=currentAlarm.get("HANDLING_SUGGESTION");
		StringBuffer sb=new StringBuffer();
		if(s!=null && !"".equals(s.toString()) && s.toString().contains("\\")){
			String[] ss;
			if(s.toString().contains("\\\\")){
				ss=s.toString().replaceAll("\\\\\\\\","zyyuuyyz").split("zyyuuyyz");
			}else{
				ss=s.toString().replaceAll("\\\\","zyyuuyyz").split("zyyuuyyz");
			}
			sb.append("<table style='font-size:12px'>");
			for(int i=0;i<ss.length;i++){
				if(!"".equals(ss[i])){
					sb.append("<tr><td>"+ss[i]+"</td></tr>");
				}
			}
			sb.append("</table>");
			currentAlarm.put("HANDLING_SUGGESTION",sb.toString());
		}
		// 类型转换
		json = JSONObject.fromObject(currentAlarm.toMap());
		return json;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getAlarmNameByFactory(Map<String, Object> paramMap) throws CommonException {
		// 获取数据库连接
		DBCollection conn = null;
		if("current".equals(paramMap.get("type"))){
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		}else{
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		if(!"-1".equals(paramMap.get("factory").toString())){
			condition.put("FACTORY", Integer.parseInt(paramMap.get("factory").toString()));
		}
		// 实现SQL语法 like的条件 
		Pattern pattern = Pattern.compile("^.*" + paramMap.get("alarmName") + ".*$", Pattern.CASE_INSENSITIVE); 
		condition.put("NATIVE_PROBABLE_CAUSE", pattern);
		// 查询某厂家的所有告警名称 distinct查法
		List<String> alarmNameList = conn.distinct("NATIVE_PROBABLE_CAUSE",condition);
		return alarmNameList;
	}
	
	/**
	 * Method name: stringToInt <BR>
	 * Description: 将List<String>集合转换成int[]数组<BR>
	 * Remark: 2013-12-18<BR>
	 * @author CaiJiaJia
	 * @return int[]<BR>
	 */
	public int[] stringToInt(List<String> list){
		int[] arr = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = Integer.parseInt(list.get(i));
		}
		return arr;
	}
	
	@Override
	@SuppressWarnings("static-access")
	public Map<String, Object> getAlarms_High(Map<String, Object> paramMap, int start, int limit)
			throws CommonException, ParseException {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = null;
		if("current".equals(paramMap.get("type").toString())){
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		}else{
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		}
		// 获取当前用户ID
		HttpServletRequest request = ServletActionContext.getRequest();
		Integer userId = (Integer)request.getSession().getAttribute("SYS_USER_ID");
		// 封装查询条件
		BasicDBObject conditionQuery = new BasicDBObject ();
		BasicDBObject conditionQueryChild = new BasicDBObject ();
		// 告警源树选择(需要将8中级别的条件or连接)
		if(!"".equals(paramMap.get("nodeIds").toString())){
			// 定义多字段or连接对象
			BasicDBList child = new BasicDBList ();
			// 选择的告警源ID
			String[] nodeIdsArr = paramMap.get("nodeIds").toString().split(",");
			// 选择的告警源级别
			String[] nodeLevelsArr = paramMap.get("nodeLevels").toString().split(",");
			// 定义8个List分别用来存8中级别选择的ID
			List<String> emsGroupList = new ArrayList<String>();
			List<String> emsList = new ArrayList<String>();
			List<String> subnetList = new ArrayList<String>();
			List<String> neList = new ArrayList<String>();
			List<String> shelfList = new ArrayList<String>();
			List<String> unitList = new ArrayList<String>();
			List<String> subunitList = new ArrayList<String>();
//			List<String> ptpList = new ArrayList<String>();
			// 判断告警源ID的值，分别填充8个List
			for (int i = 0; i < nodeLevelsArr.length; i++) {
				if("1".equals(nodeLevelsArr[i])){
					emsGroupList.add(nodeIdsArr[i]);
				}else if("2".equals(nodeLevelsArr[i])){
					emsList.add(nodeIdsArr[i]);
				}else if("3".equals(nodeLevelsArr[i])){
					subnetList.add(nodeIdsArr[i]);
				}else if("4".equals(nodeLevelsArr[i])){
					neList.add(nodeIdsArr[i]);
				}else if("5".equals(nodeLevelsArr[i])){
					shelfList.add(nodeIdsArr[i]);
				}else if("6".equals(nodeLevelsArr[i])){
					unitList.add(nodeIdsArr[i]);
				}else if("7".equals(nodeLevelsArr[i])){
					subunitList.add(nodeIdsArr[i]);
				}
//				else if("8".equals(nodeLevelsArr[i])){
//					ptpList.add(nodeIdsArr[i]);
//				}
			}
			// 封装多字段or连接条件
			if(emsGroupList.size()>0){
				child.add(new BasicDBObject("BASE_EMS_GROUP_ID", new BasicDBObject("$in",stringToInt(emsGroupList))));
			}
			if(emsList.size()>0){
				child.add(new BasicDBObject("EMS_ID", new BasicDBObject("$in",stringToInt(emsList))));
			}
			if(subnetList.size()>0){
				List<Integer> subnetListI = new ArrayList<Integer>();
				for (String id : subnetList) {
					subnetListI.addAll(getSubnetIds(id));
				}
				child.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$in",subnetListI)));
			}
			if(neList.size()>0){
				child.add(new BasicDBObject("NE_ID", new BasicDBObject("$in",stringToInt(neList))));
			}
			if(shelfList.size()>0){
				child.add(new BasicDBObject("SHELF_ID", new BasicDBObject("$in",stringToInt(shelfList))));
			}
			if(unitList.size()>0){
				child.add(new BasicDBObject("UNIT_ID", new BasicDBObject("$in",stringToInt(unitList))));
			}
			if(subunitList.size()>0){
				child.add(new BasicDBObject("SUB_UNIT_ID", new BasicDBObject("$in",stringToInt(subunitList))));
			}
//			if(ptpList.size()>0){
//				child.add(new BasicDBObject("PTP_ID", new BasicDBObject("$in",stringToInt(ptpList))));
//			}
			// 将多字段or连接条件添加的总的查询条件
			conditionQueryChild.put("$or", child);

		// 没有在告警源树上选择对象
		} else {
			// 显示非管理员用户的所有告警
			if (userId != CommonDefine.USER_ADMIN_ID) {
				int[] emsArray = null; int[] subnetArray = null; int[] neArray = null;
				BasicDBList devDomain = new BasicDBList ();				
				List<Map<String, Object>> emsList = alarmManagementMapper.getEmsIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (emsList != null && emsList.size() > 0) {
					emsArray = new int[emsList.size()];
					for (int i = 0; i < emsArray.length; i++) {
						emsArray[i]=Integer.parseInt(emsList.get(i).get("BASE_EMS_CONNECTION_ID").toString());
					}
					devDomain.add(new BasicDBObject("EMS_ID", new BasicDBObject("$in", emsArray)));
				}
				List<Map<String, Object>> subnetList = alarmManagementMapper.getSubnetIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (subnetList != null && subnetList.size() > 0) {
					subnetArray = new int[subnetList.size()];
					for (int i = 0; i < subnetArray.length; i++) {
						subnetArray[i]=Integer.parseInt(subnetList.get(i).get("BASE_SUBNET_ID").toString());
					}
					devDomain.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$in", subnetArray)));
				}
				List<Map<String, Object>> neList = alarmManagementMapper.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (neList != null && neList.size() > 0) {
					neArray = new int[neList.size()];
					for (int i = 0; i < neArray.length; i++) {
						neArray[i]=Integer.parseInt(neList.get(i).get("BASE_NE_ID").toString());
					}
					devDomain.add(new BasicDBObject("NE_ID", new BasicDBObject("$in", neArray)));
				}
				
				if (devDomain.size() > 1) {
					conditionQueryChild.put("$or", devDomain);
				} else {
					if (emsList.size() > 0) {
						conditionQueryChild.put("EMS_ID", new BasicDBObject("$in", emsArray));
					} else if (subnetList.size() > 0) {
						conditionQueryChild.put("SUBNET_ID", new BasicDBObject("$in", subnetArray));
					} else if (neList.size() > 0) {
						conditionQueryChild.put("NE_ID", new BasicDBObject("$in", neArray));
					}
				}
				// 用户的设备域为空时的处理
				if (emsList.isEmpty() && subnetList.isEmpty() && neList.isEmpty()) {
					System.out.println("该用户没有任何设备域权限");
					Map<String, Object> valueMap = new HashMap<String, Object>();
					valueMap.put("total", 0);
					valueMap.put("rows", new ArrayList<DBObject>());
					return valueMap;
				}
			}
		}
		BasicDBList parent = new BasicDBList();
		if(!conditionQueryChild.isEmpty()){
			parent.add(conditionQueryChild);
		}
		// 告警源过滤条件(端口类型)
		if(!"all".equals(paramMap.get("alarmSource").toString())){
//			conditionQuery.put("PTP_TYPE", paramMap.get("alarmSource"));
			parent.add(new BasicDBObject("PTP_TYPE", new BasicDBObject("$in",getAlarmSourceByType(paramMap.get("alarmSource").toString()))));
		}
		// 告警级别
		if(!"".equals(paramMap.get("alarmLevel").toString())){
			String[] alarmLevelArr = paramMap.get("alarmLevel").toString().substring(0,paramMap.get("alarmLevel").toString().lastIndexOf(",")).split(",");
			int[] alarmLevelArrI = new int[alarmLevelArr.length];
			for (int i = 0; i < alarmLevelArr.length; i++) {
				alarmLevelArrI[i]=Integer.parseInt(alarmLevelArr[i]);
			}
//			conditionQuery.put("PERCEIVED_SEVERITY", new BasicDBObject("$in",alarmLevelArrI));
			parent.add(new BasicDBObject("PERCEIVED_SEVERITY", new BasicDBObject("$in",alarmLevelArrI)));
		}
		// 确认状态
		if(!"".equals(paramMap.get("confirmStatus").toString())){
			String[] confirmStatusArrS = paramMap.get("confirmStatus").toString().substring(0,paramMap.get("confirmStatus").toString().lastIndexOf(",")).split(",");
			// String数组转int数组
			int[] confirmStatusArrI = new int[confirmStatusArrS.length];
			for (int i = 0; i < confirmStatusArrS.length; i++) {
				confirmStatusArrI[i]=Integer.parseInt(confirmStatusArrS[i]);
			}
//			conditionQuery.put("IS_ACK", new BasicDBObject("$in",confirmStatusArrI));
			parent.add(new BasicDBObject("IS_ACK", new BasicDBObject("$in",confirmStatusArrI)));
		}
		// 清除状态
		if(!"".equals(paramMap.get("clearStatus").toString())){
			String[] clearStatusArrS = paramMap.get("clearStatus").toString().substring(0,paramMap.get("clearStatus").toString().lastIndexOf(",")).split(",");
			// String数组转int数组
			int[] clearStatusArrI = new int[clearStatusArrS.length];
			for (int i = 0; i < clearStatusArrS.length; i++) {
				clearStatusArrI[i]=Integer.parseInt(clearStatusArrS[i]);
			}
//			conditionQuery.put("IS_CLEAR", new BasicDBObject("$in",clearStatusArrI));
			parent.add(new BasicDBObject("IS_CLEAR", new BasicDBObject("$in",clearStatusArrI)));
		}
		// 告警名称->厂家
//		conditionQuery.put("FACTORY", paramMap.get("factory"));
		if(!"-1".equals(paramMap.get("factory").toString())){
			parent.add(new BasicDBObject("FACTORY", Integer.parseInt(paramMap.get("factory").toString())));
		}
		// 告警名称->告警名称
		if(!"".equals(paramMap.get("alarmName").toString())){
//			conditionQuery.put("NATIVE_PROBABLE_CAUSE", paramMap.get("alarmName"));
			parent.add(new BasicDBObject("NATIVE_PROBABLE_CAUSE", paramMap.get("alarmName")));
		}
		// 告警类型
		if(!"".equals(paramMap.get("alarmType").toString())){
			String[] alarmTypeArr = paramMap.get("alarmType").toString().substring(0,paramMap.get("alarmType").toString().lastIndexOf(",")).split(",");
			int[] alarmTypeArrI = new int[alarmTypeArr.length];
			for (int i = 0; i < alarmTypeArr.length; i++) {
				alarmTypeArrI[i]=Integer.parseInt(alarmTypeArr[i]);
			}
//			conditionQuery.put("ALARM_TYPE", new BasicDBObject("$in",alarmTypeArrI));
			parent.add(new BasicDBObject("ALARM_TYPE", new BasicDBObject("$in",alarmTypeArrI)));
		}
		// 首次发生时间 
		if(!"true".equals(paramMap.get("firstOneStatus").toString())){
			// 时间范围  从-到
			if("true".equals(paramMap.get("firstTwoStatus").toString())){
//				conditionQuery.put("FIRST_TIME", new BasicDBObject("$gte",sf.parse(paramMap.get("firstStart").toString())).append("$lte",sf.parse(paramMap.get("firstEnd").toString())));
				parent.add(new BasicDBObject("FIRST_TIME", new BasicDBObject("$gte",sf.parse(paramMap.get("firstStart").toString())).append("$lte",sf.parse(paramMap.get("firstEnd").toString()))));
			}
			// 时间范围 最近(当前时间之前几天几时几分到现在)
			if("true".equals(paramMap.get("firstThreeStatus").toString())){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(calendar.DATE,- Integer.parseInt(paramMap.get("firstDay").toString()));
				calendar.add(calendar.HOUR,- Integer.parseInt(paramMap.get("firstHour").toString()));
				calendar.add(calendar.MINUTE,- Integer.parseInt(paramMap.get("firstMinute").toString()));
//				conditionQuery.put("FIRST_TIME", new BasicDBObject("$gte",calendar.getTime()));
				parent.add(new BasicDBObject("FIRST_TIME", new BasicDBObject("$gte",calendar.getTime())));
			}
		}
		// 清除时间
		if(!"true".equals(paramMap.get("clearOneStatus").toString())){
			// 时间范围  从-到
			if("true".equals(paramMap.get("clearTwoStatus").toString())){
//				conditionQuery.put("CLEAR_TIME", new BasicDBObject("$gte",sf.parse(paramMap.get("clearStart").toString())).append("$lte",sf.parse(paramMap.get("clearEnd").toString())));
				parent.add(new BasicDBObject("CLEAR_TIME", new BasicDBObject("$gte",sf.parse(paramMap.get("clearStart").toString())).append("$lte",sf.parse(paramMap.get("clearEnd").toString()))));
			}
		}
		// 持续时间
		if("true".equals(paramMap.get("continueTimeStatus").toString())){
			long continueDay = "".equals(paramMap.get("continueDay").toString())?0:Integer.parseInt(paramMap.get("continueDay").toString());
			long continueHour = "".equals(paramMap.get("continueHour").toString())?0:Integer.parseInt(paramMap.get("continueHour").toString());
			long continueMinute = "".equals(paramMap.get("continueMinute").toString())?0:Integer.parseInt(paramMap.get("continueMinute").toString());
			long continueTime = continueDay*24*60 + continueHour*60 + continueMinute;
			
		    Date d=new Date();   
		    long contiue=continueDay*24*60*60 * 1000+continueHour*60*60*1000+continueMinute*60*1000;
		    String time=sf.format(new Date(d.getTime() -contiue));//现在的时间减去持续时间
			BasicDBObject queryCondition = new BasicDBObject ();
		    //(IS_CLEAR=true and CONTINUE_TIME > 60) OR (IS_CLEAR=false and FIRST_TIME < 2014/4/24 11:00)   
		    BasicDBList values = new BasicDBList(); 
		    DBObject c1=new BasicDBObject();
		    DBObject c2=new BasicDBObject();
			if("1".equals(paramMap.get("continueTimeRange").toString())){//<=
				c1.put("IS_CLEAR",1);
				c1.put("CONTINUE_TIME", new BasicDBObject("$lte", continueTime));
				c2.put("IS_CLEAR",2);
				c2.put("FIRST_TIME", new BasicDBObject("$gte",sf.parse(time)));
				values.add(c1);
				values.add(c2);
				queryCondition.put("$or",values);
			}else{
				c1.put("IS_CLEAR",1);
				c1.put("CONTINUE_TIME", new BasicDBObject("$gte",continueTime));
				c2.put("IS_CLEAR",2);
				c2.put("FIRST_TIME",new BasicDBObject("$lte",sf.parse(time)));
				values.add(c1);
				values.add(c2);
				queryCondition.put("$or",values);
			}
			if(!queryCondition.isEmpty()){
				parent.add(queryCondition);
			}
		}
		
		// 频次
		if("true".equals(paramMap.get("frequencyStatus").toString())){
			if("1".equals(paramMap.get("frequencyRange").toString())){//<=
//				conditionQuery.put("AMOUNT", new BasicDBObject("$lte", paramMap.get("frequencyCount")));
				parent.add(new BasicDBObject("AMOUNT", new BasicDBObject("$lte", paramMap.get("frequencyCount"))));
			}else{
//				conditionQuery.put("AMOUNT", new BasicDBObject("$gte",paramMap.get("frequencyCount")));
				parent.add(new BasicDBObject("AMOUNT", new BasicDBObject("$gte",paramMap.get("frequencyCount"))));
			}
		}
		// 业务影响
		if(!"".equals(paramMap.get("affectType"))){
			String[] affectTypeArrS = paramMap.get("affectType").toString().substring(0,paramMap.get("affectType").toString().lastIndexOf(",")).split(",");
			// String数组壮伟int数组
			int[] affectTypeArrI = new int[affectTypeArrS.length];
			for (int i = 0; i < affectTypeArrS.length; i++) {
				affectTypeArrI[i]=Integer.parseInt(affectTypeArrS[i]);
			}
//			conditionQuery.put("SERVICE_AFFECTING", new BasicDBObject("$in",affectTypeArrI));
			parent.add(new BasicDBObject("SERVICE_AFFECTING", new BasicDBObject("$in",affectTypeArrI)));
		}
		// 告警反转
		if(!"".equals(paramMap.get("almReversal"))){
			String[] almReversalArrS = paramMap.get("almReversal").toString().substring(0,paramMap.get("almReversal").toString().lastIndexOf(",")).split(",");
			// String数组转为boolean数组
			boolean[] almReversalArrB = new boolean[almReversalArrS.length];
			for (int i = 0; i < almReversalArrS.length; i++) {
				if ("1".equals(almReversalArrS[i])) {
					almReversalArrB[i] = true;
				} else {
					almReversalArrB[i] = false;
				}
			}
			parent.add(new BasicDBObject("REVERSAL", new BasicDBObject("$in",almReversalArrB)));
		}
		if(!parent.isEmpty())
			conditionQuery.put("$and", parent);
		// 封装过滤器条件
		BasicDBObject conditionFilter = new BasicDBObject ();
		if("current".equals(paramMap.get("type").toString())){
			// 查询过滤器设置
			conditionFilter=getCurrentAlarmFilterCondition((Integer)ServletActionContext.getRequest().getSession().getAttribute("SYS_USER_ID"));
		}
		// 封装子查询条件
		BasicDBList childAll = new BasicDBList ();
		childAll.add(conditionQuery);
		childAll.add(conditionFilter);
		// 合并子查询条件
		BasicDBObject condition = new BasicDBObject ();
		condition.put("$and", childAll);
		System.out.println(condition);
		// 高级查询的告警总数
		int count = conn.find(condition).count();
		// 高级查询的告警详细信息
		DBCursor alarm = conn.find(condition).sort(new BasicDBObject("NE_TIME",-1)).skip(start).limit(limit);
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		String duration;
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
			dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
			dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
			dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
			dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
			// 计算告警持续时间			
			if ("current".equals(paramMap.get("type").toString())) {
				// 当前告警高级查询
				if((Integer)dbo.get("IS_CLEAR") == CommonDefine.IS_CLEAR_YES) {
					duration = getTimeDif(dbo.get("CLEAR_TIME").toString(), dbo.get("FIRST_TIME").toString());
				} else {
					duration = getTimeDif(sf.format(new Date()), dbo.get("FIRST_TIME").toString());
				}				
			} else {
				// 历史告警高级查询
				duration = getTimeDif(dbo.get("CLEAR_TIME").toString(), dbo.get("FIRST_TIME").toString());
			}
			dbo.put("DURATION", duration);
			list.add(dbo);
		}
		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("total", count);
		valueMap.put("rows", list);
		return valueMap;
	}

	private List<String> getAlarmSourceByType(String type) {
		List<String> lists=new ArrayList<String>();
		if("OSC,OSCNI".equals(type)){
			lists.add("OSC");
			lists.add("OSCNI");
			return lists;
		}else if("STM-1~STM-256".equals(type)){
			for(int i=1;i<=256;i++){
				lists.add("STM-"+i);
			}
			return lists;
		}else if("STM-64~STM-256".equals(type)){
			for(int i=64;i<=256;i++){
				lists.add("STM-"+i);
			}
			return lists;
		}else if("PDH".equals(type)){
			lists.add("E1");
			lists.add("E3");
			lists.add("E4");
			return lists;
		}else{
			lists.add(type);
		}
		return lists;
	}

	@Override
	public Map<String, Object> getAlarmFiltersByUserId(Map<String, Object> paramMap, int start, int limit)throws CommonException, ParseException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 过滤器参数不是综告
		paramMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_NO);
		// 获取当前人创建的告警过滤器总数
		Map<String, Object> alarmFilterCount = alarmManagementMapper.getAlarmFilterCountByUserId(paramMap);
		// 获取当前人创建的告警过滤器信息
		List<Map<String, Object>> alarmFilterList = alarmManagementMapper.getAlarmFilterByUserId(paramMap, start, limit);
		// 封装结果集
		valueMap.put("total", alarmFilterCount.get("total"));
		valueMap.put("rows", alarmFilterList);
		return valueMap;
	}
	
	@Override
	public Map<String, Object> getAlarmFiltersSummaryByUserId(Map<String, Object> paramMap) throws CommonException{
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 过滤器参数不是综告
		paramMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_NO);
		// 获取当前人创建的告警过滤器摘要信息
		List<Map<String, Object>> alarmFilterList = alarmManagementMapper.getAlarmFilterSummaryByUserId(paramMap);
		//加入无选项
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("FILTER_ID", CommonDefine.VALUE_NONE);
		map.put("FILTER_NAME", "无");
		map.put("STATUS", CommonDefine.ALARM_SHIELD_STATUS_PENDING);
		alarmFilterList.add(map);
		// 封装结果集
		valueMap.put("rows", alarmFilterList);
		return valueMap;
	}
	
	@Override
	public Map<String, Object> getAlarmFiltersComReportByUserId(Map<String, Object> paramMap, int start, int limit)throws CommonException, ParseException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 过滤器参数是综告
		paramMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_YES);
		// 获取当前人创建的综告接口过滤器总数
		Map<String, Object> alarmFilterCount = alarmManagementMapper.getAlarmFilterCountByUserId(paramMap);
		// 获取当前人创建的综告接口过滤器信息
		List<Map<String, Object>> alarmFilterList = alarmManagementMapper.getAlarmFilterByUserId(paramMap, start, limit);
		if(!alarmFilterList.isEmpty()){
			for (int i = 0; i < alarmFilterList.size(); i++) {
				if(Integer.parseInt(alarmFilterList.get(i).get("FILTER_TYPE").toString())==CommonDefine.ALARM_FILTER_TYPE_CUSTOM){
					alarmFilterList.get(i).put("EFFECTIVE_TIME", "");
				}else{
					alarmFilterList.get(i).put("EFFECTIVE_TIME", alarmFilterList.get(i).get("START_TIME")+" 到 "+alarmFilterList.get(i).get("END_TIME"));
				}
			}
		}
		// 封装结果集
		valueMap.put("total", alarmFilterCount.get("total"));
		valueMap.put("rows", alarmFilterList);
		return valueMap;
	}
	
	@Override
	public void addAlarmFilter(Map<String, Object> paramMap) throws CommonException {
		/** 告警过滤器  **/
		AlarmFilterModel alarmFiltermodel = new AlarmFilterModel();
		// 过滤器名称
		alarmFiltermodel.setFilterName(paramMap.get("filterName").toString());
		// 过滤器描述
		alarmFiltermodel.setDescription(paramMap.get("filterDesc").toString());
		// 不是综告过滤器
		alarmFiltermodel.setFilterFlag(CommonDefine.ALARM_FILTER_COM_REPORT_NO);
		// 创建者ID
		HttpServletRequest request = ServletActionContext.getRequest();
		alarmFiltermodel.setSysUserId(Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString()));
		// 创建者名称
		alarmFiltermodel.setCreator(request.getSession().getAttribute("USER_NAME").toString());
		// 告警源选择标签 1:告警源 2:告警源类型
		if("true".equals(paramMap.get("alarmSourceStatus").toString())){
			alarmFiltermodel.setAlarmSourceFlag(CommonDefine.ALARM_SOURCE_SELECT);
			// 不是通道告警
			alarmFiltermodel.setCtpAlarmFlag(CommonDefine.CTP_ALARM_NO);
		}else{
			alarmFiltermodel.setAlarmSourceFlag(CommonDefine.ALARM_SOURCE_TYPE_SELECT);
			if("true".equals(paramMap.get("ptpAlarmStatus").toString())){
				// 是通道告警
				alarmFiltermodel.setCtpAlarmFlag(CommonDefine.CTP_ALARM_YES);
			}else{
				// 不是通道告警
				alarmFiltermodel.setCtpAlarmFlag(CommonDefine.CTP_ALARM_NO);
			}
		}
		// 过滤器状态 1:启用;2:挂起(创建时默认挂起)
		alarmFiltermodel.setStatus(CommonDefine.ALARM_FILTER_STATUS_PENDING);
		// 创建时间
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		alarmFiltermodel.setCreateTime(sf.format(new Date()));
		alarmManagementMapper.addAlarmFilter(alarmFiltermodel);
		/** 告警过滤器名称关联表  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 过滤器id
		modelMap.put("filterId", alarmFiltermodel.getFilterId());
		// 创建时间
		modelMap.put("createTime", alarmFiltermodel.getCreateTime());
		// 告警名称
		if(!"".equals(paramMap.get("alarmName").toString())){
			String[] alarmNameArr= paramMap.get("alarmName").toString().split("=");
			for (int i = 0; i < alarmNameArr.length; i++) {
				// 厂家
				modelMap.put("factory", Integer.parseInt(alarmNameArr[i].split(",")[0]));
				// 告警名称
				modelMap.put("nativeProbableCause", alarmNameArr[i].split(",")[1]);
				alarmManagementMapper.addAlarmFilterRelation(modelMap);
			}
		}
		/** 告警过滤器类型关联表  **/
		if(!"".equals(paramMap.get("alarmType").toString())){
			String[] alarmTypeArr= paramMap.get("alarmType").toString().split(",");
			for (int i = 0; i < alarmTypeArr.length; i++) {
				// 告警类型
				modelMap.put("alarmType", alarmTypeArr[i]);
				alarmManagementMapper.addAlarmFilterTypeRelation(modelMap);
			}
		}
		/** 告警过滤器级别关联表  **/
		if(!"".equals(paramMap.get("alarmLevel").toString())){
			String[] alarmLevelArr= paramMap.get("alarmLevel").toString().split(",");
			for (int i = 0; i < alarmLevelArr.length; i++) {
				// 告警类型
				modelMap.put("alarmLevel", alarmLevelArr[i]);
				alarmManagementMapper.addAlarmFilterLevelRelation(modelMap);
			}
		}
		/** 告警过滤器业务影响关联表  **/
		if(!"".equals(paramMap.get("affectType").toString())){
			String[] affectTypeArr= paramMap.get("affectType").toString().split(",");
			for (int i = 0; i < affectTypeArr.length; i++) {
				// 告警类型
				modelMap.put("affectType", affectTypeArr[i]);
				alarmManagementMapper.addAlarmFilterAffectRelation(modelMap);
			}
		}
		if("true".equals(paramMap.get("alarmSourceStatus").toString())){// 告警源
			/** 告警过滤器源(设备)关联表 **/
			if(!"".equals(paramMap.get("resourceSelectIds").toString())){
				String[] resourceSelectIds = paramMap.get("resourceSelectIds").toString().split(",");
				String[] resourceSelectLvs = paramMap.get("resourceSelectLvs").toString().split(",");
				for (int i = 0; i < resourceSelectIds.length; i++) {
					// 告警源ID
					modelMap.put("id", resourceSelectIds[i]);
					// 告警源级别
					modelMap.put("lv", resourceSelectLvs[i]);
					alarmManagementMapper.addAlarmFilterResourceRelation(modelMap);
				}
			}
		}else{// 告警源类型
			/** 告警过滤器源类型(网元型号)关联表 **/
			if(!"".equals(paramMap.get("neModelSelect").toString())){
				String[] neModelSelect = paramMap.get("neModelSelect").toString().split(";");
				for (int i = 0; i < neModelSelect.length; i++) {
					// 网元型号
					modelMap.put("neModelId", neModelSelect[i].split(",")[3]);
					alarmManagementMapper.addAlarmFilterResourceTypeRelation(modelMap);
				}
			}
			/** 告警过滤器端口类型关联表 **/
			if(!"".equals(paramMap.get("portModelSelect").toString())){
				String[] portModelSelect = paramMap.get("portModelSelect").toString().split(",");
				for (int i = 0; i < portModelSelect.length; i++) {
					// 端口型号
					modelMap.put("ptpModel", portModelSelect[i]);
					alarmManagementMapper.addAlarmFilterPtpModelRelation(modelMap);
				}
			}
		}
	}

	@Override
	public void addAlarmFilterComReport(Map<String, Object> paramMap) throws CommonException {
		/** 告警过滤器  **/
		AlarmFilterModel alarmFiltermodel = new AlarmFilterModel();
		// 过滤器名称
		alarmFiltermodel.setFilterName(paramMap.get("filterName").toString());
		// 过滤器描述
		alarmFiltermodel.setDescription(paramMap.get("filterDesc").toString());
		// 过滤器类型
		alarmFiltermodel.setFilterType(CommonDefine.ALARM_FILTER_TYPE_CUSTOM);
		// 综告过滤器
		alarmFiltermodel.setFilterFlag(CommonDefine.ALARM_FILTER_COM_REPORT_YES);
		// 创建者ID
		HttpServletRequest request = ServletActionContext.getRequest();
		alarmFiltermodel.setSysUserId(Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString()));
		// 创建者名称
		alarmFiltermodel.setCreator(request.getSession().getAttribute("USER_NAME").toString());
		// 过滤器状态 1:启用;2:挂起(创建时默认挂起)
		alarmFiltermodel.setStatus(CommonDefine.ALARM_FILTER_STATUS_PENDING);
		// 创建时间
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		alarmFiltermodel.setCreateTime(sf.format(new Date()));
		alarmManagementMapper.addAlarmFilterComReport(alarmFiltermodel);
		/** 告警过滤器名称关联表  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 过滤器id
		modelMap.put("filterId", alarmFiltermodel.getFilterId());
		// 创建时间
		modelMap.put("createTime", alarmFiltermodel.getCreateTime());
		// 告警名称
		if(!"".equals(paramMap.get("alarmName").toString())){
			String[] alarmNameArr= paramMap.get("alarmName").toString().split("=");
			for (int i = 0; i < alarmNameArr.length; i++) {
				// 厂家
				modelMap.put("factory", Integer.parseInt(alarmNameArr[i].split(",")[0]));
				// 告警名称
				modelMap.put("nativeProbableCause", alarmNameArr[i].split(",")[1]);
				alarmManagementMapper.addAlarmFilterRelation(modelMap);
			}
		}
		/** 告警过滤器类型关联表  **/
		if(!"".equals(paramMap.get("alarmType").toString())){
			String[] alarmTypeArr= paramMap.get("alarmType").toString().split(",");
			for (int i = 0; i < alarmTypeArr.length; i++) {
				// 告警类型
				modelMap.put("alarmType", alarmTypeArr[i]);
				alarmManagementMapper.addAlarmFilterTypeRelation(modelMap);
			}
		}
		/** 告警过滤器级别关联表  **/
		if(!"".equals(paramMap.get("alarmLevel").toString())){
			String[] alarmLevelArr= paramMap.get("alarmLevel").toString().split(",");
			for (int i = 0; i < alarmLevelArr.length; i++) {
				// 告警类型
				modelMap.put("alarmLevel", alarmLevelArr[i]);
				alarmManagementMapper.addAlarmFilterLevelRelation(modelMap);
			}
		}
		/** 告警过滤器业务影响关联表  **/
		if(!"".equals(paramMap.get("affectType").toString())){
			String[] affectTypeArr= paramMap.get("affectType").toString().split(",");
			for (int i = 0; i < affectTypeArr.length; i++) {
				// 告警类型
				modelMap.put("affectType", affectTypeArr[i]);
				alarmManagementMapper.addAlarmFilterAffectRelation(modelMap);
			}
		}
		/** 告警过滤器源(设备)关联表 **/
		if(!"".equals(paramMap.get("resourceSelectIds").toString())){
			String[] resourceSelectIds = paramMap.get("resourceSelectIds").toString().split(",");
			String[] resourceSelectLvs = paramMap.get("resourceSelectLvs").toString().split(",");
			for (int i = 0; i < resourceSelectIds.length; i++) {
				// 告警源ID
				modelMap.put("id", resourceSelectIds[i]);
				// 告警源级别
				modelMap.put("lv", resourceSelectLvs[i]);
				alarmManagementMapper.addAlarmFilterResourceRelation(modelMap);
			}
		}
	}
	
	@Override
	public Map<String, Object> getDetailByNodeLevel(Map<String, Object> paramMap)throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 选择的告警源ID
		String[] nodeIdsArr = paramMap.get("nodeIds").toString().split(",");
		// 选择的告警源级别
		String[] nodeLevelsArr = paramMap.get("nodeLevels").toString().split(",");
		List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
		// 根据不同的node级别去查询详细信息
		for (int i = 0; i < nodeLevelsArr.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			if(String.valueOf(CommonDefine.TREE.NODE.EMSGROUP).equals(nodeLevelsArr[i])){
				map= alarmManagementMapper.getDetailByNode_emsGroupId(Integer.parseInt(nodeIdsArr[i]));
			}else if(String.valueOf(CommonDefine.TREE.NODE.EMS).equals(nodeLevelsArr[i])){
				map= alarmManagementMapper.getDetailByNode_emsId(Integer.parseInt(nodeIdsArr[i]));
			}else if(String.valueOf(CommonDefine.TREE.NODE.SUBNET).equals(nodeLevelsArr[i])){
				map = alarmManagementMapper.getDetailByNode_subnetId(Integer.parseInt(nodeIdsArr[i]));
			}else if(String.valueOf(CommonDefine.TREE.NODE.NE).equals(nodeLevelsArr[i])){
				map = alarmManagementMapper.getDetailByNode_neId(Integer.parseInt(nodeIdsArr[i]));
			}else if(String.valueOf(CommonDefine.TREE.NODE.SHELF).equals(nodeLevelsArr[i])){
				map = alarmManagementMapper.getDetailByNode_shelfId(Integer.parseInt(nodeIdsArr[i]));
			}else if(String.valueOf(CommonDefine.TREE.NODE.UNIT).equals(nodeLevelsArr[i])){
				map = alarmManagementMapper.getDetailByNode_unitId(Integer.parseInt(nodeIdsArr[i]));
			}else if(String.valueOf(CommonDefine.TREE.NODE.SUBUNIT).equals(nodeLevelsArr[i])){
				map = alarmManagementMapper.getDetailByNode_subunitId(Integer.parseInt(nodeIdsArr[i]));
			}else if(String.valueOf(CommonDefine.TREE.NODE.PTP).equals(nodeLevelsArr[i])){
				map = alarmManagementMapper.getDetailByNode_ptpId(Integer.parseInt(nodeIdsArr[i]));
			}
			// 有的网管没有网管分组
			if(map.get("BASE_EMS_GROUP_ID")==null){
				map.put("BASE_EMS_GROUP_ID", String.valueOf(CommonDefine.VALUE_NONE));
			}
			// 添加id和级别
			map.put("ID", nodeIdsArr[i]);
			map.put("LV", nodeLevelsArr[i]);
			// 根据网管分组、网管过滤
			if(map.get("BASE_EMS_CONNECTION_ID")==null){
				if(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId").toString())||"".equals(paramMap.get("emsGroupId"))){
					detailList.add(map);
				}else{
					if(map.get("BASE_EMS_GROUP_ID").toString().equals(paramMap.get("emsGroupId").toString())){
						detailList.add(map);
					}
				}
			}else{
				if(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId").toString())||"".equals(paramMap.get("emsGroupId"))){
					if(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsId").toString())||"".equals(paramMap.get("emsId"))){
						detailList.add(map);
					}else{
						if(map.get("BASE_EMS_CONNECTION_ID").toString().equals(paramMap.get("emsId").toString())){
							detailList.add(map);
						}
					}
				}else{
					if(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsId").toString())||"".equals(paramMap.get("emsId"))){
						if(map.get("BASE_EMS_GROUP_ID").toString().equals(paramMap.get("emsGroupId").toString())){
							detailList.add(map);
						}
					}else{
						if(map.get("BASE_EMS_GROUP_ID").toString().equals(paramMap.get("emsGroupId").toString())&&map.get("BASE_EMS_CONNECTION_ID").toString().equals(paramMap.get("emsId").toString())){
							detailList.add(map);
						}
					}
				}
			}
		}
		// 将没有的字段赋空值
		for (int i = 0; i < detailList.size(); i++) {
			if(detailList.get(i).get("PTP_NAME")==null){// 端口
				detailList.get(i).put("PTP_NAME", "");
			}
			if(detailList.get(i).get("UNIT_NAME")==null){// 板卡
				detailList.get(i).put("UNIT_NAME", "");
			}
			if(detailList.get(i).get("NE_NAME")==null){// 网元
				detailList.get(i).put("NE_NAME", "");
			}
			if(detailList.get(i).get("EMS_NAME")==null){// 网管
				detailList.get(i).put("EMS_NAME", "");
			}
			if(detailList.get(i).get("GROUP_NAME")==null){// 网管
				detailList.get(i).put("GROUP_NAME", "无");
			}
			// 拼接槽道
			String slotName = "";
			if(detailList.get(i).get("SLOT_NAME")!=null){// 槽道
				slotName = detailList.get(i).get("RACK_NAME")+"-"+detailList.get(i).get("SHELF_NAME")+"-"+detailList.get(i).get("SLOT_NAME");
				detailList.get(i).put("SLOT_NAME", slotName);
			}else{
				detailList.get(i).put("SLOT_NAME", "");
			}
		}
		valueMap.put("rows", detailList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAllNeModelByFactory(Map<String, Object> paramMap) throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 获取某厂家的所有网元型号
		List<Map<String, Object>> neModelList = alarmManagementMapper.getAllNeModelByFactory(paramMap);
		// 封装结果集
		valueMap.put("rows", neModelList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmFilterFirstDetailById(Map<String, Object> paramMap) throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 过滤器名称、描述 、选择源
		Map<String, Object> mainMap = alarmManagementMapper.getAlarmFilterFirstDetail_MainById(Integer.parseInt(paramMap.get("filterId").toString()));
		// 已选告警名称
		List<Map<String, Object>> nameList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmNameById(Integer.parseInt(paramMap.get("filterId").toString()));
		// 告警类型
		List<Map<String, Object>> typeList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmTypeById(Integer.parseInt(paramMap.get("filterId").toString()));
		// 告警级别
		List<Map<String, Object>> levelList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmLevelById(Integer.parseInt(paramMap.get("filterId").toString()));
		// 影响业务
		List<Map<String, Object>> affectList = alarmManagementMapper.getAlarmFilterFirstDetail_AlarmAffectlById(Integer.parseInt(paramMap.get("filterId").toString()));
		valueMap.put("main", mainMap);
		valueMap.put("name", nameList);
		valueMap.put("type", typeList);
		valueMap.put("level", levelList);
		valueMap.put("affect", affectList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmFilterSecondDetailById(Map<String, Object> paramMap) throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 告警源
		List<Map<String, Object>> resourceList = alarmManagementMapper.getAlarmFilterSecondtDetailById(Integer.parseInt(paramMap.get("filterId").toString()));
		valueMap.put("resource", resourceList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmFilterThirdDetailById(Map<String, Object> paramMap) throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 网元型号
		List<Map<String, Object>> neModelList = alarmManagementMapper.getAlarmFilterThirdNeModelById(Integer.parseInt(paramMap.get("filterId").toString()));
		// 端口型号
		List<Map<String, Object>> ptpModelList = alarmManagementMapper.getAlarmFilterThirdPtpModelById(Integer.parseInt(paramMap.get("filterId").toString()));
		// 通道告警状态
		Map<String, Object> ptpAlarmStatusMap = alarmManagementMapper.getAlarmFilterThirdPtpAlarmStatusById(Integer.parseInt(paramMap.get("filterId").toString()));
		valueMap.put("neModel", neModelList);
		valueMap.put("ptpModel", ptpModelList);
		valueMap.put("ptpAlarmStatus", ptpAlarmStatusMap);
		return valueMap;
	}

	@Override
	public void modifyAlarmFilter(Map<String, Object> paramMap)throws CommonException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = sf.format(new Date());
		Map<String, Object> alarmFilterMap = new HashMap<String, Object>();
		alarmFilterMap.put("filterId", paramMap.get("filterId"));
		// 更新过滤器主表
		if("true".equals(paramMap.get("alarmSourceStatus").toString())){
			alarmFilterMap.put("alarmSourceStatus", CommonDefine.ALARM_SOURCE_SELECT);
			alarmFilterMap.put("ptpAlarmStatus", CommonDefine.CTP_ALARM_NO);
		}else{
			alarmFilterMap.put("alarmSourceStatus", CommonDefine.ALARM_SOURCE_TYPE_SELECT);
			if("true".equals(paramMap.get("ptpAlarmStatus").toString())){
				// 是通道告警
				alarmFilterMap.put("ptpAlarmStatus", CommonDefine.CTP_ALARM_YES);
			}else{
				// 不是通道告警
				alarmFilterMap.put("ptpAlarmStatus", CommonDefine.CTP_ALARM_NO);
			}
		}
		alarmFilterMap.put("filterName", paramMap.get("filterName").toString());
		alarmFilterMap.put("filterDesc", paramMap.get("filterDesc").toString());
		alarmFilterMap.put("createTime", createTime);
		alarmManagementMapper.updateAlarmFilter(alarmFilterMap);
		// 删除关联关系
		alarmManagementMapper.deleteAlarmFilterRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterTypeRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterLevelRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterAffectRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterResourceRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterResourceTypeRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterPtpModelRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		// 重新插入关联关系
		/** 告警过滤器名称关联表  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 过滤器id
		modelMap.put("filterId", paramMap.get("filterId"));
		// 创建时间
		modelMap.put("createTime", createTime);
		// 告警名称
		if(!"".equals(paramMap.get("alarmName").toString())){
			String[] alarmNameArr= paramMap.get("alarmName").toString().split("=");
			for (int i = 0; i < alarmNameArr.length; i++) {
				// 厂家
				modelMap.put("factory", Integer.parseInt(alarmNameArr[i].split(",")[0]));
				// 告警名称
				modelMap.put("nativeProbableCause", alarmNameArr[i].split(",")[1]);
				alarmManagementMapper.addAlarmFilterRelation(modelMap);
			}
		}
		/** 告警过滤器类型关联表  **/
		if(!"".equals(paramMap.get("alarmType").toString())){
			String[] alarmTypeArr= paramMap.get("alarmType").toString().split(",");
			for (int i = 0; i < alarmTypeArr.length; i++) {
				// 告警类型
				modelMap.put("alarmType", alarmTypeArr[i]);
				alarmManagementMapper.addAlarmFilterTypeRelation(modelMap);
			}
		}
		/** 告警过滤器级别关联表  **/
		if(!"".equals(paramMap.get("alarmLevel").toString())){
			String[] alarmLevelArr= paramMap.get("alarmLevel").toString().split(",");
			for (int i = 0; i < alarmLevelArr.length; i++) {
				// 告警类型
				modelMap.put("alarmLevel", alarmLevelArr[i]);
				alarmManagementMapper.addAlarmFilterLevelRelation(modelMap);
			}
		}
		/** 告警过滤器业务影响关联表  **/
		if(!"".equals(paramMap.get("affectType").toString())){
			String[] affectTypeArr= paramMap.get("affectType").toString().split(",");
			for (int i = 0; i < affectTypeArr.length; i++) {
				// 告警类型
				modelMap.put("affectType", affectTypeArr[i]);
				alarmManagementMapper.addAlarmFilterAffectRelation(modelMap);
			}
		}
		if("true".equals(paramMap.get("alarmSourceStatus").toString())){// 告警源
			/** 告警过滤器源(设备)关联表 **/
			if(!"".equals(paramMap.get("resourceSelectIds").toString())){
				String[] resourceSelectIds = paramMap.get("resourceSelectIds").toString().split(",");
				String[] resourceSelectLvs = paramMap.get("resourceSelectLvs").toString().split(",");
				for (int i = 0; i < resourceSelectIds.length; i++) {
					// 告警源ID
					modelMap.put("id", resourceSelectIds[i]);
					// 告警源级别
					modelMap.put("lv", resourceSelectLvs[i]);
					alarmManagementMapper.addAlarmFilterResourceRelation(modelMap);
				}
			}
		}else{// 告警源类型
			/** 告警过滤器源类型(网元型号)关联表 **/
			if(!"".equals(paramMap.get("neModelSelect").toString())){
				String[] neModelSelect = paramMap.get("neModelSelect").toString().split(";");
				for (int i = 0; i < neModelSelect.length; i++) {
					// 网元型号
					modelMap.put("neModelId", neModelSelect[i].split(",")[3]);
					modelMap.put("factory", neModelSelect[i].split(",")[0]);
					alarmManagementMapper.addAlarmFilterResourceTypeRelation(modelMap);
				}
			}
			/** 告警过滤器端口类型关联表 **/
			if(!"".equals(paramMap.get("portModelSelect").toString())){
				String[] portModelSelect = paramMap.get("portModelSelect").toString().split(",");
				for (int i = 0; i < portModelSelect.length; i++) {
					// 端口型号
					modelMap.put("ptpModel", portModelSelect[i]);
					alarmManagementMapper.addAlarmFilterPtpModelRelation(modelMap);
				}
			}
		}
	}
	
	@Override
	public void modifyAlarmFilterComReport(Map<String, Object> paramMap)throws CommonException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = sf.format(new Date());
		Map<String, Object> alarmFilterMap = new HashMap<String, Object>();
		alarmFilterMap.put("filterId", paramMap.get("filterId"));
		// 更新过滤器主表
		alarmFilterMap.put("filterName", paramMap.get("filterName").toString());
		alarmFilterMap.put("filterDesc", paramMap.get("filterDesc").toString());
		alarmFilterMap.put("createTime", createTime);
		alarmManagementMapper.updateAlarmFilterComReport(alarmFilterMap);
		// 删除关联关系
		alarmManagementMapper.deleteAlarmFilterRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterTypeRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterLevelRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterAffectRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		alarmManagementMapper.deleteAlarmFilterResourceRelation(Integer.parseInt(paramMap.get("filterId").toString()));
		// 重新插入关联关系
		/** 告警过滤器名称关联表  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 过滤器id
		modelMap.put("filterId", paramMap.get("filterId"));
		// 创建时间
		modelMap.put("createTime", createTime);
		// 告警名称
		if(!"".equals(paramMap.get("alarmName").toString())){
			String[] alarmNameArr= paramMap.get("alarmName").toString().split("=");
			for (int i = 0; i < alarmNameArr.length; i++) {
				// 厂家
				modelMap.put("factory", Integer.parseInt(alarmNameArr[i].split(",")[0]));
				// 告警名称
				modelMap.put("nativeProbableCause", alarmNameArr[i].split(",")[1]);
				alarmManagementMapper.addAlarmFilterRelation(modelMap);
			}
		}
		/** 告警过滤器类型关联表  **/
		if(!"".equals(paramMap.get("alarmType").toString())){
			String[] alarmTypeArr= paramMap.get("alarmType").toString().split(",");
			for (int i = 0; i < alarmTypeArr.length; i++) {
				// 告警类型
				modelMap.put("alarmType", alarmTypeArr[i]);
				alarmManagementMapper.addAlarmFilterTypeRelation(modelMap);
			}
		}
		/** 告警过滤器级别关联表  **/
		if(!"".equals(paramMap.get("alarmLevel").toString())){
			String[] alarmLevelArr= paramMap.get("alarmLevel").toString().split(",");
			for (int i = 0; i < alarmLevelArr.length; i++) {
				// 告警类型
				modelMap.put("alarmLevel", alarmLevelArr[i]);
				alarmManagementMapper.addAlarmFilterLevelRelation(modelMap);
			}
		}
		/** 告警过滤器业务影响关联表  **/
		if(!"".equals(paramMap.get("affectType").toString())){
			String[] affectTypeArr= paramMap.get("affectType").toString().split(",");
			for (int i = 0; i < affectTypeArr.length; i++) {
				// 告警类型
				modelMap.put("affectType", affectTypeArr[i]);
				alarmManagementMapper.addAlarmFilterAffectRelation(modelMap);
			}
		}
		/** 告警过滤器源(设备)关联表 **/
		if(!"".equals(paramMap.get("resourceSelectIds").toString())){
			String[] resourceSelectIds = paramMap.get("resourceSelectIds").toString().split(",");
			String[] resourceSelectLvs = paramMap.get("resourceSelectLvs").toString().split(",");
			for (int i = 0; i < resourceSelectIds.length; i++) {
				// 告警源ID
				modelMap.put("id", resourceSelectIds[i]);
				// 告警源级别
				modelMap.put("lv", resourceSelectLvs[i]);
				alarmManagementMapper.addAlarmFilterResourceRelation(modelMap);
			}
		}
	}

	@Override
	public void deleteAlarmFilter(Map<String, Object> paramMap) throws CommonException {
		String[] filterIdArr = paramMap.get("filterIds").toString().split(",");
		for (int i = 0; i < filterIdArr.length; i++) {
			alarmManagementMapper.deleteAlarmFilter(Integer.parseInt(filterIdArr[i]));
			alarmManagementMapper.deleteAlarmFilterRelation(Integer.parseInt(filterIdArr[i]));
			alarmManagementMapper.deleteAlarmFilterTypeRelation(Integer.parseInt(filterIdArr[i]));
			alarmManagementMapper.deleteAlarmFilterLevelRelation(Integer.parseInt(filterIdArr[i]));
			alarmManagementMapper.deleteAlarmFilterAffectRelation(Integer.parseInt(filterIdArr[i]));
			alarmManagementMapper.deleteAlarmFilterResourceRelation(Integer.parseInt(filterIdArr[i]));
			alarmManagementMapper.deleteAlarmFilterResourceTypeRelation(Integer.parseInt(filterIdArr[i]));
			alarmManagementMapper.deleteAlarmFilterPtpModelRelation(Integer.parseInt(filterIdArr[i]));
		}
	}

	@Override
	public void updateAlarmFilterStatus(Map<String, Object> paramMap) throws CommonException {
		String[] filterIdArr = paramMap.get("filterIds").toString().split(",");
		for (int i = 0; i < filterIdArr.length; i++) {
			paramMap.put("filterId", Integer.parseInt(filterIdArr[i]));
			if(Integer.parseInt(paramMap.get("flag").toString())==CommonDefine.ALARM_FILTER_STATUS_ENABLE){
				// 查询数据库，判断是否已经存在启用的过滤器
				// 过滤器参数不是综告
				paramMap.put("filterFlag", CommonDefine.ALARM_FILTER_COM_REPORT_NO);
				paramMap.put("status", CommonDefine.ALARM_FILTER_STATUS_ENABLE);
				List<Map<String, Object>> list = alarmManagementMapper.getAlarmFilterEnableByUserId(paramMap);
				if(!list.isEmpty()){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("filterId", Integer.parseInt(list.get(0).get("FILTER_ID").toString()));
					alarmManagementMapper.updateAlarmFilterPending(map);
				}
				alarmManagementMapper.updateAlarmFilterEnable(paramMap);
			}else{
				alarmManagementMapper.updateAlarmFilterPending(paramMap);
			}
		}
		boolean isIntegratedAlarmFilter = Boolean.valueOf(String.valueOf(paramMap.get("isIntegratedAlarm")));
		if (isIntegratedAlarmFilter) {
    		// 发送综告过滤器状态变更消息
    		JMSSender.sendMessage(CommonDefine.MESSAGE_TYPE_ALARM_FILTER_STATUS_CHANGE,
    				new HashMap<String, Object>());			
		}

	}
	
	@Override
	public Map<String, Object> getAllAlarmShield(Map<String, Object> paramMap,int start, int limit) throws CommonException, ParseException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 获取告警屏蔽器总数
		Map<String, Object> alarmShieldCount = new HashMap<String, Object>();
		// 获取告警屏蔽器信息
		List<Map<String, Object>> alarmShieldList = new ArrayList<Map<String,Object>>();
		// 获取用户ID
		HttpSession session = ServletActionContext.getRequest().getSession();
		int userId = (Integer) session.getAttribute("SYS_USER_ID");

		if("all".equals(paramMap.get("flag").toString()) ||
				("".equals(paramMap.get("factory")) &&
				 "".equals(paramMap.get("alarmName")) &&
				 "".equals(paramMap.get("emsId")) &&
				 "".equals(paramMap.get("neId")) &&
				 ("".equals(paramMap.get("emsGroupId")) || String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId"))))) {
			// 非管理员用户
			if (userId != CommonDefine.USER_ADMIN_ID) {
				List<Integer> emsArray = new ArrayList<Integer>();
				List<Integer> neArray = new ArrayList<Integer>();
				List<Map<String, Object>> emsList = alarmManagementMapper.getEmsIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (emsList != null && emsList.size() > 0) {
					for (Map<String, Object> ems : emsList) {
						emsArray.add((Integer) ems.get("BASE_EMS_CONNECTION_ID"));
					}
					paramMap.put("emsList", emsArray);
				}
				List<Map<String, Object>> neList = alarmManagementMapper.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (neList != null && neList.size() > 0) {
					for (Map<String, Object> ne : neList) {
						neArray.add((Integer) ne.get("BASE_NE_ID"));
					}
					paramMap.put("neList", neArray);
				}
				// 用户的设备域为空时的处理
				if (emsArray.isEmpty() &&  neArray.isEmpty()) {
					System.out.println("该用户没有任何设备域权限");
					// 封装结果集
					valueMap.put("total", 0);
					valueMap.put("rows", new ArrayList<Map<String,Object>>());
					return valueMap;
				}
				alarmShieldCount = alarmManagementMapper.getAlarmShieldCounts(paramMap);
				alarmShieldList = alarmManagementMapper.getAllAlarmShield(paramMap, start, limit);				
			// 管理员
			} else {
				String tableName = "t_alarm_shield_detail";
				int count = commonManagerMapper.selectTableCount(tableName);
				alarmShieldCount.put("total", count);
				alarmShieldList = commonManagerMapper.selectTable(tableName, start, limit);				
			}

		}else{// 根据条件查询
			// 网管分组，网管和网元筛选条件为空
			if ("".equals(paramMap.get("emsId")) &&
				"".equals(paramMap.get("neId")) &&
				("".equals(paramMap.get("emsGroupId")) || CommonDefine.VALUE_ALL==(Integer)paramMap.get("emsGroupId"))) {
				paramMap.put("emsGroupId", "");
				alarmShieldCount = alarmManagementMapper.getAlarmShieldCountsByCondition(paramMap);
				alarmShieldList = alarmManagementMapper.getAllAlarmShieldByCondition(paramMap, start, limit);

			// 网管分组、网管或网元作为筛选条件
			} else {
				if("".equals(paramMap.get("emsId").toString()) || String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsId").toString())) {
					if("".equals(paramMap.get("emsGroupId").toString()) || CommonDefine.VALUE_ALL==(Integer)paramMap.get("emsGroupId")) {
						List<Integer> list = getEmsGroupIdListBySysID();
						for (Integer integer : list) {
							if(integer==-1){
								paramMap.put("emsGroupIdNull", -1);
							}
						}
						paramMap.put("emsGroupId", toStringFromList(list));
						paramMap.put("emsId", toStringFromList(getEmsIdListByGroupAndSysID(-99)));
					}else{
						List<Integer> emsgrouplist=new ArrayList<Integer>();
						emsgrouplist.add(Integer.parseInt(paramMap.get("emsGroupId").toString()));
						if(String.valueOf(CommonDefine.VALUE_NONE).equals(paramMap.get("emsGroupId").toString())){
							paramMap.put("emsGroupIdNull", -1);
						}
						paramMap.put("emsId", toStringFromList(getEmsIdListByGroupAndSysID(Integer.parseInt(paramMap.get("emsGroupId").toString()))));
						paramMap.put("emsGroupId", toStringFromList(emsgrouplist));
					}
				}else{
					List<Integer> emslist=new ArrayList<Integer>();
					emslist.add(Integer.parseInt(paramMap.get("emsId").toString()));
					paramMap.put("emsId", toStringFromList(emslist));
					List<Integer> list = getEmsGroupIdListBySysID();
					for (Integer integer : list) {
						if(integer==-1){
							paramMap.put("emsGroupIdNull", -1);
						}
					}
					paramMap.put("emsGroupId", toStringFromList(list));
				}
				alarmShieldCount = alarmManagementMapper.getAlarmShieldCountsByCondition(paramMap);
				alarmShieldList = alarmManagementMapper.getAllAlarmShieldByCondition(paramMap, start, limit);				
			}

		}
		// 封装结果集
		valueMap.put("total", alarmShieldCount.get("total"));
		valueMap.put("rows", alarmShieldList);
		return valueMap;
	}

	@Override
	public void addAlarmShield(Map<String, Object> paramMap) throws CommonException {
		/** 告警屏蔽器  **/
		AlarmShieldModel alarmShieldModel = new AlarmShieldModel();
		// 屏蔽器名称
		alarmShieldModel.setShieldName(paramMap.get("shieldName").toString());
		// 屏蔽器描述
		alarmShieldModel.setDescription(paramMap.get("shieldDesc").toString());
		// 创建者ID
		HttpServletRequest request = ServletActionContext.getRequest();
		alarmShieldModel.setSysUserId(Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString()));
		// 创建者名称
		alarmShieldModel.setCreator(request.getSession().getAttribute("USER_NAME").toString());
		// 屏蔽器状态 1:启用;2:挂起(创建时默认挂起)
		alarmShieldModel.setStatus(CommonDefine.ALARM_SHIELD_STATUS_PENDING);
		// 创建时间
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		alarmShieldModel.setCreateTime(sf.format(new Date()));
		alarmManagementMapper.addAlarmShield(alarmShieldModel);
		/** 告警屏蔽器名称关联表  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 屏蔽器id
		modelMap.put("shieldId", alarmShieldModel.getShieldId());
		// 创建时间
		modelMap.put("createTime", alarmShieldModel.getCreateTime());
		// 告警名称
		if(!"".equals(paramMap.get("alarmName").toString())){
			String[] alarmNameArr= paramMap.get("alarmName").toString().split("=");
			for (int i = 0; i < alarmNameArr.length; i++) {
				// 厂家
				modelMap.put("factory", Integer.parseInt(alarmNameArr[i].split(",")[0]));
				// 告警名称
				modelMap.put("nativeProbableCause", alarmNameArr[i].split(",")[1]);
				alarmManagementMapper.addAlarmShieldRelation(modelMap);
			}
		}
		/** 告警屏蔽器类型关联表  **/
		if(!"".equals(paramMap.get("alarmType").toString())){
			String[] alarmTypeArr= paramMap.get("alarmType").toString().split(",");
			for (int i = 0; i < alarmTypeArr.length; i++) {
				// 告警类型
				modelMap.put("alarmType", alarmTypeArr[i]);
				alarmManagementMapper.addAlarmShieldTypeRelation(modelMap);
			}
		}
		/** 告警屏蔽器级别关联表  **/
		if(!"".equals(paramMap.get("alarmLevel").toString())){
			String[] alarmLevelArr= paramMap.get("alarmLevel").toString().split(",");
			for (int i = 0; i < alarmLevelArr.length; i++) {
				// 告警类型
				modelMap.put("alarmLevel", alarmLevelArr[i]);
				alarmManagementMapper.addAlarmShieldLevelRelation(modelMap);
			}
		}
		/** 告警屏蔽器业务影响关联表  **/
		if(!"".equals(paramMap.get("affectType").toString())){
			String[] affectTypeArr= paramMap.get("affectType").toString().split(",");
			for (int i = 0; i < affectTypeArr.length; i++) {
				// 告警类型
				modelMap.put("affectType", affectTypeArr[i]);
				alarmManagementMapper.addAlarmShieldAffectRelation(modelMap);
			}
		}
		/** 告警屏蔽器源(设备)关联表 **/
		if(!"".equals(paramMap.get("resourceSelectIds").toString())){
			String[] resourceSelectIds = paramMap.get("resourceSelectIds").toString().split(",");
			String[] resourceSelectLvs = paramMap.get("resourceSelectLvs").toString().split(",");
			for (int i = 0; i < resourceSelectIds.length; i++) {
				// 告警源ID
				modelMap.put("id", resourceSelectIds[i]);
				// 告警源级别
				modelMap.put("lv", resourceSelectLvs[i]);
				alarmManagementMapper.addAlarmShieldResourceRelation(modelMap);
			}
		}
	}
	
	@Override
	public Map<String, Object> getSimpleByNodeLevel(Map<String, Object> paramMap)throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 选择的告警源ID
		String nodeId = paramMap.get("nodeId").toString();
		// 选择的告警源级别
		String nodeLevel = paramMap.get("nodeLevel").toString();
		// 根据不同的node级别去查询详细信息
		Map<String, Object> map = new HashMap<String, Object>();
		if(String.valueOf(CommonDefine.TREE.NODE.EMSGROUP).equals(nodeLevel)){
			map= alarmManagementMapper.getDetailByNode_emsGroupId(Integer.parseInt(nodeId));
		}else if(String.valueOf(CommonDefine.TREE.NODE.EMS).equals(nodeLevel)){
			map= alarmManagementMapper.getDetailByNode_emsId(Integer.parseInt(nodeId));
		}else if(String.valueOf(CommonDefine.TREE.NODE.SUBNET).equals(nodeLevel)){
			map = alarmManagementMapper.getDetailByNode_subnetId(Integer.parseInt(nodeId));
		}else if(String.valueOf(CommonDefine.TREE.NODE.NE).equals(nodeLevel)){
			map = alarmManagementMapper.getDetailByNode_neId(Integer.parseInt(nodeId));
		}else if(String.valueOf(CommonDefine.TREE.NODE.SHELF).equals(nodeLevel)){
			map = alarmManagementMapper.getDetailByNode_shelfId(Integer.parseInt(nodeId));
		}else if(String.valueOf(CommonDefine.TREE.NODE.UNIT).equals(nodeLevel)){
			map = alarmManagementMapper.getDetailByNode_unitId(Integer.parseInt(nodeId));
		}else if(String.valueOf(CommonDefine.TREE.NODE.SUBUNIT).equals(nodeLevel)){
			map = alarmManagementMapper.getDetailByNode_subunitId(Integer.parseInt(nodeId));
		}else if(String.valueOf(CommonDefine.TREE.NODE.PTP).equals(nodeLevel)){
			map = alarmManagementMapper.getDetailByNode_ptpId(Integer.parseInt(nodeId));
		}
		// 如果有的字段为null，则赋空值，用于前台展示
		if(map.get("GROUP_NAME")==null){
			map.put("GROUP_NAME", "无");
		}
		if(map.get("EMS_NAME")==null){
			map.put("EMS_NAME", "");
		}
		if(map.get("NE_NAME")==null){
			map.put("NE_NAME", "");
		}
		// 添加id和级别
		map.put("ID", nodeId);
		map.put("LV", nodeLevel);
		valueMap.put("rows", map);
		return valueMap;
	}

	@Override
	public void deleteAlarmShield(Map<String, Object> paramMap) throws CommonException {
		String[] shieldIdArr = paramMap.get("shieldIds").toString().split(",");
		for (int i = 0; i < shieldIdArr.length; i++) {
			alarmManagementMapper.deleteAlarmShield(Integer.parseInt(shieldIdArr[i]));
			alarmManagementMapper.deleteAlarmShieldRelation(Integer.parseInt(shieldIdArr[i]));
			alarmManagementMapper.deleteAlarmShieldTypeRelation(Integer.parseInt(shieldIdArr[i]));
			alarmManagementMapper.deleteAlarmShieldLevelRelation(Integer.parseInt(shieldIdArr[i]));
			alarmManagementMapper.deleteAlarmShieldAffectRelation(Integer.parseInt(shieldIdArr[i]));
			alarmManagementMapper.deleteAlarmShieldResourceRelation(Integer.parseInt(shieldIdArr[i]));
		}
	}

	@Override
	public Map<String, Object> getAlarmShieldFirstDetailById(Map<String, Object> paramMap) throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 屏蔽器名称、描述 
		Map<String, Object> mainMap = alarmManagementMapper.getAlarmShieldFirstDetail_MainById(Integer.parseInt(paramMap.get("shieldId").toString()));
		// 已选告警名称
		List<Map<String, Object>> nameList = alarmManagementMapper.getAlarmShieldFirstDetail_AlarmNameById(Integer.parseInt(paramMap.get("shieldId").toString()));
		// 告警类型
		List<Map<String, Object>> typeList = alarmManagementMapper.getAlarmShieldFirstDetail_AlarmTypeById(Integer.parseInt(paramMap.get("shieldId").toString()));
		// 告警级别
		List<Map<String, Object>> levelList = alarmManagementMapper.getAlarmShieldFirstDetail_AlarmLevelById(Integer.parseInt(paramMap.get("shieldId").toString()));
		// 影响业务
		List<Map<String, Object>> affectList = alarmManagementMapper.getAlarmShieldFirstDetail_AlarmAffectlById(Integer.parseInt(paramMap.get("shieldId").toString()));
		// 告警源
		List<Map<String, Object>> resourceList = alarmManagementMapper.getAlarmShieldSecondtDetailById(Integer.parseInt(paramMap.get("shieldId").toString()));
		// 告警源名称
		List<Map<String, Object>> resourceNameList = new ArrayList<Map<String,Object>>();
		if(!resourceList.isEmpty()){
			if (Integer.parseInt(resourceList.get(0).get("DEVICE_TYPE").toString()) == 4) {	
				for (int i = 0; i < resourceList.size(); i++) {
					Map<String, Object> map = alarmManagementMapper.getDetailByNode_neId(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("DEVICE_NAME", map.get("NE_NAME"));
					resourceNameList.add(map1);
				}
			} else {
				for (int i = 0; i < resourceList.size(); i++) {
					Map<String, Object> map = alarmManagementMapper.getDetailByNode_emsId(Integer.parseInt(resourceList.get(i).get("DEVICE_ID").toString()));
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("DEVICE_NAME", map.get("EMS_NAME"));
					resourceNameList.add(map1);
				}
			}
		}
		valueMap.put("main", mainMap);
		valueMap.put("name", nameList);
		valueMap.put("type", typeList);
		valueMap.put("level", levelList);
		valueMap.put("affect", affectList);
		valueMap.put("resourceName", resourceNameList);
		return valueMap;
	}
	
	@Override
	public Map<String, Object> getAlarmShieldSecondDetailById(Map<String, Object> paramMap) throws CommonException {
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 告警源
		List<Map<String, Object>> resourceList = alarmManagementMapper.getAlarmShieldSecondtDetailById(Integer.parseInt(paramMap.get("shieldId").toString()));
		valueMap.put("resource", resourceList);
		return valueMap;
	}
	
	@Override
	public void modifyAlarmShield(Map<String, Object> paramMap)throws CommonException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = sf.format(new Date());
		Map<String, Object> alarmShieldMap = new HashMap<String, Object>();
		alarmShieldMap.put("shieldId", paramMap.get("shieldId"));
		// 更新屏蔽器主表
		alarmShieldMap.put("shieldName", paramMap.get("shieldName").toString());
		alarmShieldMap.put("shieldDesc", paramMap.get("shieldDesc").toString());
		alarmShieldMap.put("createTime", createTime);
		alarmManagementMapper.updateAlarmShield(alarmShieldMap);
		// 删除关联关系
		alarmManagementMapper.deleteAlarmShieldRelation(Integer.parseInt(paramMap.get("shieldId").toString()));
		alarmManagementMapper.deleteAlarmShieldTypeRelation(Integer.parseInt(paramMap.get("shieldId").toString()));
		alarmManagementMapper.deleteAlarmShieldLevelRelation(Integer.parseInt(paramMap.get("shieldId").toString()));
		alarmManagementMapper.deleteAlarmShieldAffectRelation(Integer.parseInt(paramMap.get("shieldId").toString()));
		alarmManagementMapper.deleteAlarmShieldResourceRelation(Integer.parseInt(paramMap.get("shieldId").toString()));
		// 重新插入关联关系
		/** 告警屏蔽器名称关联表  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 屏蔽器id
		modelMap.put("shieldId", paramMap.get("shieldId"));
		// 创建时间
		modelMap.put("createTime", createTime);
		// 告警名称
		if(!"".equals(paramMap.get("alarmName").toString())){
			String[] alarmNameArr= paramMap.get("alarmName").toString().split("=");
			for (int i = 0; i < alarmNameArr.length; i++) {
				// 厂家
				modelMap.put("factory", Integer.parseInt(alarmNameArr[i].split(",")[0]));
				// 告警名称
				modelMap.put("nativeProbableCause", alarmNameArr[i].split(",")[1]);
				alarmManagementMapper.addAlarmShieldRelation(modelMap);
			}
		}
		/** 告警屏蔽器类型关联表  **/
		if(!"".equals(paramMap.get("alarmType").toString())){
			String[] alarmTypeArr= paramMap.get("alarmType").toString().split(",");
			for (int i = 0; i < alarmTypeArr.length; i++) {
				// 告警类型
				modelMap.put("alarmType", alarmTypeArr[i]);
				alarmManagementMapper.addAlarmShieldTypeRelation(modelMap);
			}
		}
		/** 告警屏蔽器级别关联表  **/
		if(!"".equals(paramMap.get("alarmLevel").toString())){
			String[] alarmLevelArr= paramMap.get("alarmLevel").toString().split(",");
			for (int i = 0; i < alarmLevelArr.length; i++) {
				// 告警类型
				modelMap.put("alarmLevel", alarmLevelArr[i]);
				alarmManagementMapper.addAlarmShieldLevelRelation(modelMap);
			}
		}
		/** 告警屏蔽器业务影响关联表  **/
		if(!"".equals(paramMap.get("affectType").toString())){
			String[] affectTypeArr= paramMap.get("affectType").toString().split(",");
			for (int i = 0; i < affectTypeArr.length; i++) {
				// 告警类型
				modelMap.put("affectType", affectTypeArr[i]);
				alarmManagementMapper.addAlarmShieldAffectRelation(modelMap);
			}
		}
		/** 告警屏蔽器源(设备)关联表 **/
		if(!"".equals(paramMap.get("resourceSelectIds").toString())){
			String[] resourceSelectIds = paramMap.get("resourceSelectIds").toString().split(",");
			String[] resourceSelectLvs = paramMap.get("resourceSelectLvs").toString().split(",");
			for (int i = 0; i < resourceSelectIds.length; i++) {
				// 告警源ID
				modelMap.put("id", resourceSelectIds[i]);
				// 告警源级别
				modelMap.put("lv", resourceSelectLvs[i]);
				alarmManagementMapper.addAlarmShieldResourceRelation(modelMap);
			}
		}
	}
	
	@Override
	public void updateAlarmShieldStatus(Map<String, Object> paramMap) throws CommonException {
		if(Integer.parseInt(paramMap.get("flag").toString())==CommonDefine.ALARM_SHIELD_STATUS_ENABLE){
			alarmManagementMapper.updateAlarmShieldEnable(paramMap);
		}else{
			alarmManagementMapper.updateAlarmShieldPending(paramMap);
		}
	}
	
	@Override
	public Map<String, Object> getAlarmNameByFactoryFromShield(Map<String, Object> paramMap) throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> alarmNameList = alarmManagementMapper.getAlarmNameByFactoryFromShield(paramMap);
		valueMap.put("rows", alarmNameList);
		return valueMap;
	}
	

	@Override
	public void alarmSynch(Map<String, Object> paramMap) throws CommonException {
		// 定义同步告警接口所需参数
		int[] objectType = {};
		int[] perceivedSeverity = {};
		int commandLevel = CommonDefine.COLLECT_LEVEL_1;
		// 如果网元为全部
		if(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("neId").toString())||"".equals(paramMap.get("neId").toString())){
			// 如果网管为全部
			if(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsId").toString())||"".equals(paramMap.get("emsId").toString())){
				// 定义需要同步的网管集合
				List<Integer> emsList = new ArrayList<Integer>();
				if(String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId").toString())||"".equals(paramMap.get("emsGroupId").toString())){
					// 查询所有的网管
					//emsList = faultManagerMapper.getAllEms();
					emsList = getEmsIdListByGroupAndSysID(-99);
				}else{
					// 根据网管分组ID，查询网管
					emsList = getEmsIdListByGroupAndSysID(Integer.parseInt(paramMap.get("emsGroupId").toString()));
				}
				if(!emsList.isEmpty()){
					for (int i = 0; i < emsList.size(); i++) {
						// 连接需要同步的网管
						dataCollectService = SpringContextUtil.getDataCollectServiceProxy(emsList.get(i));
						// 同步网管的告警
						dataCollectService.syncAllEMSAndMEActiveAlarms(objectType, perceivedSeverity, commandLevel);
					}
				}
			}else{
				// 连接需要同步的网管
				dataCollectService = SpringContextUtil.getDataCollectServiceProxy(Integer.parseInt(paramMap.get("emsId").toString()));
				// 同步网管的告警
				dataCollectService.syncAllEMSAndMEActiveAlarms(objectType, perceivedSeverity, commandLevel);
			}
		}else{
			// 获取需要同步网元的网管ID
			Map<String, Object> emsMap = alarmManagementMapper.getEmsIdByneId(Integer.parseInt(paramMap.get("neId").toString()));
			// 连接需要同步网元的网管
			dataCollectService = SpringContextUtil.getDataCollectServiceProxy(Integer.parseInt(emsMap.get("BASE_EMS_CONNECTION_ID").toString()));
			// 同步网元的告警
			dataCollectService.syncAllActiveAlarms(Integer.parseInt(paramMap.get("neId").toString()), perceivedSeverity, commandLevel);
		}
	}

	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public Map<String, Object> getAlarmAutoConfirmByEmsGroup(Integer sysUserId,Map<String, Object> paramMap,int start,int limit) throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 查询网管的告警自动确认设置
		if("".equals(paramMap.get("emsGroupId").toString())||String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId").toString())){
//			List<Integer> list = getEmsGroupIdListBySysID();
//			for (Integer integer : list) {
//				if(integer==-1){
//					paramMap.put("emsGroupIdNull", -1);
//				}
//			}
			paramMap.remove("emsGroupId");
		}else{
			if(String.valueOf(CommonDefine.VALUE_NONE).equals(paramMap.get("emsGroupId").toString())){
				paramMap.remove("emsGroupId");
				paramMap.put("emsGroupIdNull", -1);
			}else{
				List<Integer> emsgrouplist=new ArrayList<Integer>();
				emsgrouplist.add(Integer.parseInt(paramMap.get("emsGroupId").toString()));
				paramMap.put("emsGroupId", toStringFromList(emsgrouplist));
			}
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		int id=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
//		paramMap.put("userId",id);
		
		Map<String, Object> alarmAutoConfirmCount = alarmManagementMapper.getAlarmAutoConfirmCountByEmsGroup(paramMap,id,CommonDefine.TREE.TREE_DEFINE);
		List<Map<String, Object>> alarmAutoConfirmList  = alarmManagementMapper.getAlarmAutoConfirmByEmsGroup(paramMap,id,CommonDefine.TREE.TREE_DEFINE,start,limit);
		if(!alarmAutoConfirmList.isEmpty()){
			for (int i = 0; i < alarmAutoConfirmList.size(); i++) {
				if(alarmAutoConfirmList.get(i).get("GROUP_NAME")==null){
					alarmAutoConfirmList.get(i).put("GROUP_NAME", "无");
				}
				// 如果该网管没有设置，则初始化值，用于页面显示
				if(alarmAutoConfirmList.get(i).get("AUTO_CONFIRM_ID")==null){
					alarmAutoConfirmList.get(i).put("AUTO_CONFIRM_ID", CommonDefine.ALARM_AUTO_CONFIRM_DEFAULT_ID);
					alarmAutoConfirmList.get(i).put("PS_CRITICAL_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
					alarmAutoConfirmList.get(i).put("PS_MAJOR_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
					alarmAutoConfirmList.get(i).put("PS_MINOR_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
					alarmAutoConfirmList.get(i).put("PS_WARNING_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
					alarmAutoConfirmList.get(i).put("TIMING_TIME", CommonDefine.ALARM_AUTO_CONFIRM_DEFAULT_TIME);
				}
			}
		}
		valueMap.put("rows", alarmAutoConfirmList);
		valueMap.put("total", alarmAutoConfirmCount.get("total"));
		return valueMap;
	}

	@Override
	public void modifyAlarmAutoConfirm(List<Map<String, Object>> list) throws CommonException {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 遍历所有修改的记录
		for (int i = 0; i < list.size(); i++) {
			// 表示之前没有设置，此处需要插入数据
			if(Integer.parseInt(list.get(i).get("AUTO_CONFIRM_ID").toString())==CommonDefine.ALARM_AUTO_CONFIRM_DEFAULT_ID){
				list.get(i).put("CREATE_TIME", sf.format(new Date()));
				alarmManagementMapper.addAlarmAutoConfirm(list.get(i));
			}else{// 表示之前有设置，此处需要更新数据
				alarmManagementMapper.updateAlarmAutoConfirm(list.get(i));
			}
		}
	}

	@Override
	public void confirmSet(Map<String, Object> paramMap) throws CommonException {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String[] emsIds = paramMap.get("emsIds").toString().split(",");
		String[] autoConformIds = paramMap.get("autoConformIds").toString().split(",");
		String[] timingTimes = paramMap.get("timingTimes").toString().split(",");
		// 遍历所有需要设置的记录
		for (int i = 0; i < autoConformIds.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("BASE_EMS_CONNECTION_ID", Integer.parseInt(emsIds[i]));
			map.put("TIMING_TIME", Integer.parseInt(timingTimes[i]));
			// 设置为不确认
			if("no".equals(paramMap.get("flag").toString())){
				map.put("PS_CRITICAL_CONFIRM", CommonDefine.ALARM_NO_CONFIRM);
				map.put("PS_MAJOR_CONFIRM", CommonDefine.ALARM_NO_CONFIRM);
				map.put("PS_MINOR_CONFIRM", CommonDefine.ALARM_NO_CONFIRM);
				map.put("PS_WARNING_CONFIRM", CommonDefine.ALARM_NO_CONFIRM);
			}else if("immediately".equals(paramMap.get("flag").toString())){// 设置为立即确认
				map.put("PS_CRITICAL_CONFIRM", CommonDefine.ALARM_IMMEDIATELY_CONFIRM);
				map.put("PS_MAJOR_CONFIRM", CommonDefine.ALARM_IMMEDIATELY_CONFIRM);
				map.put("PS_MINOR_CONFIRM", CommonDefine.ALARM_IMMEDIATELY_CONFIRM);
				map.put("PS_WARNING_CONFIRM", CommonDefine.ALARM_IMMEDIATELY_CONFIRM);
			}else if("timing".equals(paramMap.get("flag").toString())){// 设置为定时确认
				map.put("PS_CRITICAL_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
				map.put("PS_MAJOR_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
				map.put("PS_MINOR_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
				map.put("PS_WARNING_CONFIRM", CommonDefine.ALARM_TIMING_CONFIRM);
			}
			// 表示之前没有设置，此处需要插入数据
			if(Integer.parseInt(autoConformIds[i])==CommonDefine.ALARM_AUTO_CONFIRM_DEFAULT_ID){
				map.put("CREATE_TIME", sf.format(new Date()));
				alarmManagementMapper.addAlarmAutoConfirm(map);
			}else{// 表示之前有设置，此处需要更新数据
				map.put("AUTO_CONFIRM_ID", Integer.parseInt(autoConformIds[i]));
				alarmManagementMapper.updateAlarmAutoConfirm(map);
			}
		}
	}

	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public Map<String, Object> getAlarmRedefineByEmsGroup(Integer sysUserId, Map<String, Object> paramMap,int start,int limit) throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 根据网管分组ID,查询告警重定义设置
		if("".equals(paramMap.get("emsGroupId").toString())||String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId").toString())){
			List<Integer> list = getEmsGroupIdListBySysID();
			for (Integer integer : list) {
				if(integer==-1){
					paramMap.put("emsGroupIdNull", -1);
				}
			}
			paramMap.put("emsGroupId", toStringFromList(list));
		}else{
			List<Integer> emsgrouplist=new ArrayList<Integer>();
			emsgrouplist.add(Integer.parseInt(paramMap.get("emsGroupId").toString()));
			if(String.valueOf(CommonDefine.VALUE_NONE).equals(paramMap.get("emsGroupId").toString())){
				paramMap.put("emsGroupIdNull", -1);
			}
			paramMap.put("emsGroupId", toStringFromList(emsgrouplist));
		}
		
		
		Map<String, Object> count = alarmManagementMapper.getAlarmRedefineCountByEmsGroup(paramMap,sysUserId,CommonDefine.TREE.TREE_DEFINE);
		List<Map<String, Object>> alarmRedefineList = alarmManagementMapper.getAlarmRedefineByEmsGroup(paramMap,start,limit,sysUserId,CommonDefine.TREE.TREE_DEFINE);
		valueMap.put("rows", alarmRedefineList);
		valueMap.put("total", count.get("total"));
		return valueMap;
	}

	@Override
	public void addAlarmRedefine(Map<String, Object> paramMap) throws CommonException {
		// 状态默认挂起
		paramMap.put("status", CommonDefine.ALARM_REDEFINE_STATUS_PENDING);
		// 插入告警重定义设置
		alarmManagementMapper.addAlarmRedefine(paramMap);
	}

	@Override
	public void deleteAlarmRedefine(Map<String, Object> paramMap) throws CommonException {
		// 需要删除的ID
		String[] redefineIdsArr = paramMap.get("redefineIds").toString().split(",");
		for (int i = 0; i < redefineIdsArr.length; i++) {
			alarmManagementMapper.deleteAlarmRedefine(Integer.parseInt(redefineIdsArr[i]));
		}
	}

	@Override
	public Map<String, Object> getAlarmRedefineById(Map<String, Object> paramMap) throws CommonException {
		Map<String, Object> alarmRedefineMap = alarmManagementMapper.getAlarmRedefineById(Integer.parseInt(paramMap.get("redefineId").toString()));
		return alarmRedefineMap;
	}
	
	@Override
	public void modifyAlarmRedefine(Map<String, Object> paramMap) throws CommonException {
		alarmManagementMapper.modifyAlarmRedefine(paramMap);
	}

	@Override
	public void updateAlarmRedefineStatus(Map<String, Object> paramMap) throws CommonException {
		if(Integer.parseInt(paramMap.get("flag").toString())==CommonDefine.ALARM_REDEFINE_STATUS_ENABLE){
			alarmManagementMapper.updateAlarmRedefineEnable(paramMap);
		}else{
			alarmManagementMapper.updateAlarmRedefinePending(paramMap);
		}
	}

	@Override
	public void modifyAlarmPush(Map<String, Object> paramMap) throws CommonException {
		paramMap.put("paramKey", CommonDefine.ALARM_PUSH_PARAM_KEY);
		alarmManagementMapper.modifyAlarmParam(paramMap);
	}

	@Override
	public Map<String, Object> getAlarmPush() throws CommonException {
		Map<String, Object> alarmPushMap = alarmManagementMapper.getSystemParam(CommonDefine.ALARM_PUSH_PARAM_KEY);
		return alarmPushMap;
	}

	@Override
	public void modifyAlarmConfirmShift(Map<String, Object> paramMap) throws CommonException {
		if("confirm".equals(paramMap.get("flag").toString())){
			// 告警自动确认设置
			paramMap.put("description","告警自动确认时间设置");
			paramMap.put("paramKey", CommonDefine.ALARM_CONFIRM_PARAM_KEY);
			paramMap.put("alarmParam", paramMap.get("alarmConfirmStatus")+","+paramMap.get("alarmConfirm"));
			alarmManagementMapper.deleteAlarmParam(paramMap);
			alarmManagementMapper.modifyAlarmParam(paramMap);
			if(paramMap.get("alarmConfirmStatus").toString().equals("true")){
				String cron1 ="0 0 0/"+paramMap.get("alarmConfirm")+" * * ?";
				if(quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null)){
					quartzManagerService.modifyJobTime(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null,cron1);
				}else{
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null, AlarmAutoConfirmJob.class, cron1);
				}
				//quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZIDONGQUEREN,null,CommonDefine.QUARTZ.JOB_ACTIVATE);
			}else{
				alarmImmediateShift();
			}
		}else{
			// 告警自动转移设置
			paramMap.put("description","告警自动转移时间设置");
			paramMap.put("paramKey", CommonDefine.ALARM_SHIFT_PARAM_KEY);
			paramMap.put("alarmParam", paramMap.get("alarmShiftStatus")+","+paramMap.get("alarmShiftOne")+","+paramMap.get("alarmShiftSecond"));
			alarmManagementMapper.deleteAlarmParam(paramMap);
			alarmManagementMapper.modifyAlarmParam(paramMap);
			if(paramMap.get("alarmShiftStatus").toString().equals("true")){
				String cron2 ="0 0 0/"+paramMap.get("alarmShiftOne")+" * * ?";
				if(quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ZHUANYI,null)){
					quartzManagerService.modifyJobTime(CommonDefine.QUARTZ.JOB_ZHUANYI,null,cron2);
				}else{
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ZHUANYI,null, AlarmAutoShiftJob.class, cron2);
				}
				//quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ZHUANYI,null,CommonDefine.QUARTZ.JOB_ACTIVATE);
			}else{//立即执行
				alarmImmediateShift();
			}
		
		}
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	public void alarmAutoConfirm() throws CommonException {
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> confirmList = alarmManagementMapper.getAlarmAutoConfirm();
		for (int i = 0; i < confirmList.size(); i++) {
			// 封装查询条件
			BasicDBObject condition = new BasicDBObject ();
			// 定义一个网管要确认的告警级别集合
			List<Integer> lvList = new ArrayList<Integer>();
			// 紧急告警需要立即确认
			if(Integer.parseInt(confirmList.get(i).get("PS_CRITICAL_CONFIRM").toString())==CommonDefine.ALARM_TIMING_CONFIRM){
				lvList.add(CommonDefine.ALARM_PS_CRITICAL);
			}
			// 重要告警需要立即确认
			if(Integer.parseInt(confirmList.get(i).get("PS_MAJOR_CONFIRM").toString())==CommonDefine.ALARM_TIMING_CONFIRM){
				lvList.add(CommonDefine.ALARM_PS_MAJOR);
			}
			// 次要告警需要立即确认
			if(Integer.parseInt(confirmList.get(i).get("PS_MINOR_CONFIRM").toString())==CommonDefine.ALARM_TIMING_CONFIRM){
				lvList.add(CommonDefine.ALARM_PS_MINOR);
			}
			// 提示告警需要立即确认
			if(Integer.parseInt(confirmList.get(i).get("PS_WARNING_CONFIRM").toString())==CommonDefine.ALARM_TIMING_CONFIRM){
				lvList.add(CommonDefine.ALARM_PS_WARNING);
			}
			int[] lvArr = new int[lvList.size()];
			for (int j = 0; j < lvList.size(); j++) {
				lvArr[j] = lvList.get(j);
			}
			// 条件->告警级别
			condition.put("PERCEIVED_SEVERITY", new BasicDBObject("$in",lvArr));
			// 条件->告警清除状态(确认只针对已清除、未确认告警)
			condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
			// 条件->告警确认状态(确认只针对已清除、未确认告警)
			condition.put("IS_ACK", CommonDefine.IS_ACK_NO);
			// 条件->网管ID
			condition.put("EMS_ID", Integer.parseInt(confirmList.get(i).get("BASE_EMS_CONNECTION_ID").toString()));
			// 条件->清除时间达到多少分钟(距离当前时间过了多少分钟)
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(calendar.MINUTE,- Integer.parseInt(confirmList.get(i).get("TIMING_TIME").toString()));
			condition.put("CLEAR_TIME", new BasicDBObject("$lte",calendar.getTime()));
			// 查询符合条件的告警数据
			DBCursor alarm = conn.find(condition);
			while (alarm.hasNext()) {
				DBObject dbo = alarm.next();
				dbo.put("IS_ACK", CommonDefine.IS_ACK_YES);
				dbo.put("ACK_TIME", new Date());
				dbo.put("UPDATE_TIME", new Date());
//				if(ActionContext.getContext()!=null&&ActionContext.getContext().getSession()!=null){
//					Map<String,Object> session = ActionContext.getContext().getSession();
//					if(session.get("USER_NAME")!=null){
//						dbo.put("ACK_USER", session.get("USER_NAME").toString());
//					}
//				}else{
				//后台自动指定任务，无web会话信息，取消无用功操作
				dbo.put("ACK_USER", "系统");
//				}
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
				// 更新表中数据
				conn.save(dbo);
				// 需要根据综告推送的告警发生设置，判断该发生告警是否要给综告接口推送
//				Map<String, Object> pushMap = faultManagerMapper.getSystemParam(CommonDefine.ALARM_PUSH_PARAM_KEY);
//				String[] paramArr = pushMap.get("PARAM_VALUE").toString().split(",");
				// 如果清除告警设置不为'当前告警转为历史告警推送',则推送
//				if(Integer.parseInt(paramArr[1])!=CommonDefine.COMREPORT_PUSH_CLEAR_SHIFT){
				dbo.put("status", CommonDefine.ALARM_STATUS_ACK);	
				pushAlarmMessage(dbo.toMap());
//				}
				// 查询生命周期信息(告警转移设置)，判断是否需要立即转移到历史告警
				boolean lifeCycleFlag = judgeIsNeedShift();
				// 表示没有生命周期，立即将该条告警转移到历史表
				if(lifeCycleFlag){
					conn.remove(dbo);
					try {
						// 如果清除告警设置为'当前告警转为历史告警推送',则推送
//						if(Integer.parseInt(paramArr[1])==CommonDefine.COMREPORT_PUSH_CLEAR_SHIFT){
//							pushAlarmMessageToCopReport(dbo.toMap());
//						}
						conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
						// 获取自增id
						//int id = mongodbCommonService.getSequenceId(CommonDefine.T_HISTORY_ALARM);
						//dbo.put("_id", id);
						
						//转入历史库时，保留原始的入库时间和更新时间
						//dbo.put("UPDATE_TIME", "");
						//dbo.put("CREATE_TIME", new Date());
						conn.insert(dbo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	//立即把告警转入历史告警
	public void alarmImmediateShift() throws CommonException {
		// 获取数据库连接
		DBCollection conn = null;
		DBCollection connForHistoryAlm = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			connForHistoryAlm = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 查询需要满足的时间
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		// 条件->告警清除状态
		condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
		// 条件->告警清除状态
		condition.put("IS_ACK", CommonDefine.IS_ACK_YES);
		// 条件->清除时间和确认时间达到多少分钟(距离当前时间过了多少分钟)
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition);
		System.out.println("告警转移查询条件：" + condition.toString());
		System.out.println("告警转移查询结果计数：" + alarm.count());
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			// 转移到历史表
			conn.remove(dbo);
			try {
				// 获取自增id
				//int id = mongodbCommonService.getSequenceId(CommonDefine.T_HISTORY_ALARM);
				//dbo.put("IS_CLEAR", (int)Double.parseDouble(dbo.get("IS_CLEAR").toString()));
				//dbo.put("_id", id);
				
				//转入历史库时，保留原始的入库时间和更新时间
				//dbo.put("UPDATE_TIME", "");
				//dbo.put("CREATE_TIME", new Date());
				connForHistoryAlm.insert(dbo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	

	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	public void alarmAutoShift() throws CommonException {
		// 获取数据库连接
		DBCollection conn = null;
		DBCollection connForHistoryAlm = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			connForHistoryAlm = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 查询需要满足的时间
		Map<String, Object> alarmShiftMap = alarmManagementMapper.getSystemParam(CommonDefine.ALARM_SHIFT_PARAM_KEY);
		int minute = Integer.parseInt(alarmShiftMap.get("PARAM_VALUE").toString().split(",")[2]);
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		// 条件->告警清除状态
		condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_YES);
		// 条件->告警清除状态
		condition.put("IS_ACK", CommonDefine.IS_ACK_YES);
		// 条件->清除时间和确认时间达到多少分钟(距离当前时间过了多少分钟)
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(calendar.MINUTE,- minute);
		condition.put("CLEAR_TIME", new BasicDBObject("$lte",calendar.getTime()));
		condition.put("ACK_TIME", new BasicDBObject("$lte",calendar.getTime()));
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition);
		System.out.println("告警转移查询条件：" + condition.toString());
		System.out.println("告警转移查询结果计数：" + alarm.count());
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			// 转移到历史表
			conn.remove(dbo);
			try {
				// 需要根据综告推送的告警发生设置，判断该发生告警是否要给综告接口推送
//				Map<String, Object> pushMap = faultManagerMapper.getSystemParam(CommonDefine.ALARM_PUSH_PARAM_KEY);
//				String[] paramArr = pushMap.get("PARAM_VALUE").toString().split(",");
//				// 如果清除告警设置为'当前告警转为历史告警推送',则推送
//				if(Integer.parseInt(paramArr[1])==CommonDefine.COMREPORT_PUSH_CLEAR_SHIFT){
//					pushAlarmMessageToCopReport(dbo.toMap());
//				}

				// 获取自增id
				//int id = mongodbCommonService.getSequenceId(CommonDefine.T_HISTORY_ALARM);
				//dbo.put("IS_CLEAR", (int)Double.parseDouble(dbo.get("IS_CLEAR").toString()));
				//dbo.put("_id", id);
				//转入历史库时，保留原始的入库时间和更新时间
				//dbo.put("UPDATE_TIME", "");
				//dbo.put("CREATE_TIME", new Date());
				connForHistoryAlm.insert(dbo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Map<String, Object> getAlarmConfirmShift(Map<String, Object> paramMap) throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 告警自动确认
		if("confirm".equals(paramMap.get("flag").toString())){
			Map<String, Object> alarmConfirmMap = alarmManagementMapper.getSystemParam(CommonDefine.ALARM_CONFIRM_PARAM_KEY);
			if(alarmConfirmMap==null){
				alarmConfirmMap = new HashMap<String, Object>();
				alarmConfirmMap.put("PARAM_VALUE", CommonDefine.ALARM_CONFIRM_PARAM_DEFAULT_VALUE);
			}
			valueMap.put("confirm", alarmConfirmMap);
		}else{// 告警自动转移
			Map<String, Object> alarmShiftMap = alarmManagementMapper.getSystemParam(CommonDefine.ALARM_SHIFT_PARAM_KEY);
			if(alarmShiftMap==null){
				alarmShiftMap = new HashMap<String, Object>();
				alarmShiftMap.put("PARAM_VALUE", CommonDefine.ALARM_SHIFT_PARAM_DEFAULT_VALUE);
			}
			valueMap.put("shift", alarmShiftMap);
		}
		return valueMap;
	}
	
	@Override
	public void modifyAlarmAutoSynch(List<Map<String, Object>> list)
			throws CommonException {

		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 遍历所有修改的记录
		for (int i = 0; i < list.size(); i++) {
			int id=Integer.parseInt(list.get(i).get("ID").toString());
			//* * */20 * * ?
			String cronExpression ="0 0 0/"+list.get(i).get("SYNCHRONIZATION_CIRCLE").toString()+" * * ?";
			// 表示之前没有设置，此处需要插入数据
			if(id==CommonDefine.ALARM_AUTO_CONFIRM_DEFAULT_ID){
				//list.get(i).put("CREATE_TIME", sf.format(new Date()));
				AlarmAutoSynchModel alarmAutoSynchModel =new AlarmAutoSynchModel();
				alarmAutoSynchModel.setBASE_EMS_CONNECTION_ID(Integer.parseInt(list.get(i).get("BASE_EMS_CONNECTION_ID").toString()));
				if("".equals(list.get(i).get("SYNCHRONIZATION_CIRCLE").toString())){
					continue;
				}else{
					alarmAutoSynchModel.setSYNCHRONIZATION_CIRCLE(Integer.parseInt(list.get(i).get("SYNCHRONIZATION_CIRCLE").toString()));
					alarmAutoSynchModel.setTASK_STATUS(list.get(i).get("TASK_STATUS").toString());
					alarmAutoSynchModel.setSYNCHRONIZATION_FLAG(Integer.parseInt(list.get(i).get("SYNCHRONIZATION_FLAG").toString()));
					alarmAutoSynchModel.setDELAY_TIME(Integer.parseInt(list.get(i).get("DELAY_TIME").toString()));
					alarmAutoSynchModel.setCREATE_TIME(sf.format(new Date()));
					alarmManagementMapper.addAlarmAutoSynch(alarmAutoSynchModel);
					id=alarmAutoSynchModel.getID();
				}
			}else{// 表示之前有设置，此处需要更新数据
				alarmManagementMapper.updateAlarmAutoSynch(list.get(i));
			}
			//不存在qrtz任务情况
			if(!quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
					id)){
				// 添加一个quartz任务
				Map<String, Object> map =new HashMap<String, Object>();
				map.put("ID", id);
				map.put("BASE_EMS_CONNECTION_ID", list.get(i).get("BASE_EMS_CONNECTION_ID"));
				quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
						id,
						AlarmSyncJob.class, cronExpression, map);
				
				if(list.get(i).get("TASK_STATUS").equals(String.valueOf(CommonDefine.QUARTZ.JOB_ACTIVATE))){
					//启用job
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							id,
							CommonDefine.QUARTZ.JOB_RESUME);
				}else{
					//挂起任务
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							id,CommonDefine.QUARTZ.JOB_PAUSE);
				}
			}else{
				if(list.get(i).get("TASK_STATUS").equals(String.valueOf(CommonDefine.QUARTZ.JOB_ACTIVATE))){
					//启用job
					//(1)删除job
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							id,CommonDefine.QUARTZ.JOB_DELETE);
					//(2)新增job
					// 添加一个quartz任务
					Map<String, Object> map =new HashMap<String, Object>();
					map.put("ID", id);
					map.put("BASE_EMS_CONNECTION_ID", list.get(i).get("BASE_EMS_CONNECTION_ID"));
					quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							id,
							AlarmSyncJob.class, cronExpression, map);
					//(3)启用job
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							id,
							CommonDefine.QUARTZ.JOB_RESUME);
				}else{
					quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
							id,
							CommonDefine.QUARTZ.JOB_PAUSE);
				}
			}
		}
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	@Override
	public Map<String, Object> getAlarmAutoSynchByEmsGroup(
			Integer sysUserId,Map<String, Object> paramMap, int start, int limit) throws CommonException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 查询网管的告警自动同步
		if("".equals(paramMap.get("emsGroupId").toString())||String.valueOf(CommonDefine.VALUE_ALL).equals(paramMap.get("emsGroupId").toString())){
			List<Integer> list = getEmsGroupIdListBySysID();
			for (Integer integer : list) {
				if(integer==-1){
					paramMap.put("emsGroupIdNull", -1);
				}
			}
			paramMap.put("emsGroupId", toStringFromList(list));
		}else{
			List<Integer> emsgrouplist=new ArrayList<Integer>();
			emsgrouplist.add(Integer.parseInt(paramMap.get("emsGroupId").toString()));
			if(String.valueOf(CommonDefine.VALUE_NONE).equals(paramMap.get("emsGroupId").toString())){
				paramMap.put("emsGroupIdNull", -1);
			}
			paramMap.put("emsGroupId", toStringFromList(emsgrouplist));
		}
		List<Map<String, Object>> alarmAutoSynchList  = alarmManagementMapper.getAlarmAutoSynchByEmsGroup(paramMap,start, limit,sysUserId,CommonDefine.TREE.TREE_DEFINE);
		Map<String, Object> alarmAutoSynchCount  = alarmManagementMapper.getAlarmAutoSynchCountByEmsGroup(paramMap,sysUserId,CommonDefine.TREE.TREE_DEFINE);
		
		if(!alarmAutoSynchList.isEmpty()){
			for (int i = 0; i < alarmAutoSynchList.size(); i++) {
				if(alarmAutoSynchList.get(i).get("GROUP_NAME")==null){
					alarmAutoSynchList.get(i).put("GROUP_NAME", "无");
				}
				// 如果该网管没有设置，则初始化值，用于页面显示
				if(alarmAutoSynchList.get(i).get("ID")==null){
					alarmAutoSynchList.get(i).put("ID", CommonDefine.ALARM_AUTO_CONFIRM_DEFAULT_ID);
					alarmAutoSynchList.get(i).put("TASK_STATUS",2);
					alarmAutoSynchList.get(i).put("SYNCHRONIZATION_FLAG",2);
					alarmAutoSynchList.get(i).put("DELAY_TIME",10);
				}
				if(alarmAutoSynchList.get(i).get("LATEST_SYNCHRONIZATION_TIME")!=null){
					alarmAutoSynchList.get(i).put("LATEST_SYNCHRONIZATION_TIME", sf.format(alarmAutoSynchList.get(i).get("LATEST_SYNCHRONIZATION_TIME")));
					alarmAutoSynchList.get(i).put("NEXT_SYNCHRONIZATION_TIME", sf.format(alarmAutoSynchList.get(i).get("NEXT_SYNCHRONIZATION_TIME")));
				}else{
					alarmAutoSynchList.get(i).put("LATEST_SYNCHRONIZATION_TIME", "");
					alarmAutoSynchList.get(i).put("NEXT_SYNCHRONIZATION_TIME", "");
				}
				if(alarmAutoSynchList.get(i).get("SYNCHRONIZATION_CIRCLE")==null||"".equals(alarmAutoSynchList.get(i).get("SYNCHRONIZATION_CIRCLE").toString())){
					alarmAutoSynchList.get(i).put("SYNCHRONIZATION_CIRCLE",24);
				}
				
			}
		}
		valueMap.put("rows", alarmAutoSynchList);
		// 封装结果集
		valueMap.put("total", alarmAutoSynchCount.get("total"));
		return valueMap;
	
	}
	
	@Override
	public Map<String, Object> getAlarmNormlizedByFactory( Map<String, Object> paramMap, int start, int limit) throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> count = alarmManagementMapper.getAlarmNormlizedCountByFactory(paramMap);
		// 根据网管分组ID,查询告警重定义设置
		List<Map<String, Object>> alarmRedefineList = alarmManagementMapper.getAlarmNormlizedByFactory(paramMap,start,limit);
		for(Map<String, Object> map :alarmRedefineList){
			map.put("UPDATE_TIME", sf.format(map.get("UPDATE_TIME")));
		}
		
		valueMap.put("rows", alarmRedefineList);
		valueMap.put("total", count.get("total"));
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmNormlizedById(
			Map<String, Object> paramMap) throws CommonException {
		Map<String, Object> alarmNormlizedMap = alarmManagementMapper.getAlarmNormlizedById(Integer.parseInt(paramMap.get("redefineId").toString()));
		return alarmNormlizedMap;
	}

	@Override
	public void addAlarmNormlized(Map<String, Object> paramMap)
			throws CommonException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = sf.format(new Date());
		paramMap.put("createTime", createTime);
		alarmManagementMapper.addAlarmNormlized(paramMap);
		
	}

	@Override
	public void deleteAlarmNormlized(Map<String, Object> paramMap)
			throws CommonException {
		String[] redefineIdsArr = paramMap.get("redefineIds").toString().split(",");
		for (int i = 0; i < redefineIdsArr.length; i++) {
			alarmManagementMapper.deleteAlarmNormlized(Integer.parseInt(redefineIdsArr[i]));
		}
		
	}

	@Override
	public void modifyAlarmNormlized(Map<String, Object> paramMap)
			throws CommonException {
		alarmManagementMapper.modifyAlarmNormlized(paramMap);
	}
	
	/**
	 * Method name: judgeIsNeedShift <BR>
	 * Description: 判断是否需要立即转移到历史告警<BR>
	 * Remark: 2014-02-11<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public boolean judgeIsNeedShift(){
		Map<String, Object> shiftMap = alarmManagementMapper.getSystemParam(CommonDefine.ALARM_SHIFT_PARAM_KEY);
		String[] paramArr = shiftMap.get("PARAM_VALUE").toString().split(",");
		// 表示没有生命周期，需要立即转移到历史告警表
		if("false".equals(paramArr[0])){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Method name: pushAlarmMessageToCopReport <BR>
	 * Description: 推送实时告警信息给综告接口<BR>
	 * Remark: 2014-02-12<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void pushAlarmMessage(Map<String, Object> map){
		// 将参数专程JSON对象
//		JSONObject jsonObject = JSONObject.fromObject(map);
		
		JMSSender.sendMessage(CommonDefine.MESSAGE_TYPE_ALARM, map);
	}
	
	@Override
	public void alarmAutoSynch(Map<String, Object> paramMap) throws CommonException
			 {
		// 定义同步告警接口所需参数
		int[] objectType = {};
		int[] perceivedSeverity = {};
		int commandLevel = 1;
		// 连接需要同步的网管
			dataCollectService = SpringContextUtil.getDataCollectServiceProxy(Integer.parseInt(paramMap.get("BASE_EMS_CONNECTION_ID").toString()));
			// 同步网管的告警
			dataCollectService.syncAllEMSAndMEActiveAlarms(objectType, perceivedSeverity, commandLevel);
		
		//更新最近一次同步时间
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String latesttime = sf.format(new Date());
		paramMap.put("LATEST_SYNCHRONIZATION_TIME", latesttime);
		paramMap.put("EXECUTE_STATUS", 1);
		alarmManagementMapper.updateLatestAlarmAutoSynchTime(paramMap);
		
	}

	@Override
	public List<Map<String, String>> getDistinctDelayTime() {
		List<Map<String, String>> list =alarmManagementMapper.getDistinctDelayTime();
		return list;
	}

	@Override
	public List<Map<String, Object>> getCanDelaySync(int i) {
		List<Map<String, Object>> list =alarmManagementMapper.getCanDelaySync(i);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void alarmManualConfirm(Map<String, Object> paramMap) throws CommonException {
		String[] idsArrS = paramMap.get("ids").toString().split(",");
		int[] idsArrI = new int[idsArrS.length];
		for (int i = 0; i < idsArrS.length; i++) {
			idsArrI[i] = Integer.parseInt(idsArrS[i]);
		}
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		condition.put("_id", new BasicDBObject("$in",idsArrI));
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			dbo.put("IS_ACK", CommonDefine.IS_ACK_YES);
			dbo.put("ACK_TIME", new Date());
			dbo.put("UPDATE_TIME", new Date());
			HttpSession session = ServletActionContext.getRequest().getSession();
			dbo.put("ACK_USER", session.getAttribute("USER_NAME").toString());
			// 更新表中数据
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			conn.save(dbo);
			if(Integer.parseInt(dbo.get("IS_CLEAR").toString())==CommonDefine.IS_CLEAR_YES){
				// 需要根据综告推送的告警发生设置，判断该发生告警是否要给综告接口推送
//				Map<String, Object> pushMap = faultManagerMapper.getSystemParam(CommonDefine.ALARM_PUSH_PARAM_KEY);
//				String[] paramArr = pushMap.get("PARAM_VALUE").toString().split(",");
//				// 如果清除告警设置不为'当前告警转为历史告警推送',则推送
//				if(Integer.parseInt(paramArr[1])!=CommonDefine.COMREPORT_PUSH_CLEAR_SHIFT){
					dbo.put("status", CommonDefine.ALARM_STATUS_ACK);
					pushAlarmMessage(dbo.toMap());
//				}
				// 查询生命周期信息(告警转移设置)，判断是否需要立即转移到历史告警
				boolean lifeCycleFlag = judgeIsNeedShift();
				// 表示没有生命周期，立即将该条告警转移到历史表
				if(lifeCycleFlag){
					conn.remove(dbo);
					try {
						// 如果清除告警设置为'当前告警转为历史告警推送',则推送
//						if(Integer.parseInt(paramArr[1])==CommonDefine.COMREPORT_PUSH_CLEAR_SHIFT){
//							pushAlarmMessage(dbo.toMap());
//						}
						conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
						// 获取自增id
						//int id = mongodbCommonService.getSequenceId(CommonDefine.T_HISTORY_ALARM);
						//dbo.put("_id", id);
						
						//转入历史库时，保留原始的入库时间和更新时间
						//dbo.put("UPDATE_TIME", "");
						//dbo.put("CREATE_TIME", new Date());
						conn.insert(dbo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				//推送确认状态消息给视图模块
				dbo.put("status", CommonDefine.ALARM_STATUS_ACK);
				pushAlarmMessage(dbo.toMap());
			}
		}
	}
	
	@Override
	public void alarmAntiConfirm(Map<String, Object> paramMap) throws CommonException {
		String[] idsArrS = paramMap.get("ids").toString().split(",");
		int[] idsArrI = new int[idsArrS.length];
		for (int i = 0; i < idsArrS.length; i++) {
			idsArrI[i] = Integer.parseInt(idsArrS[i]);
		}
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		condition.put("_id", new BasicDBObject("$in",idsArrI));
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			dbo.put("IS_ACK", CommonDefine.IS_ACK_NO);
			dbo.put("ACK_TIME", "");
			dbo.put("ACK_USER", "");
			dbo.put("UPDATE_TIME", new Date());
			// 更新表中数据
			conn.save(dbo);
			if(Integer.parseInt(dbo.get("IS_CLEAR").toString())==CommonDefine.IS_CLEAR_NO){
				//推送反确认状态消息给视图模块
				dbo.put("status", CommonDefine.ALARM_STATUS_DENY);
				pushAlarmMessage(dbo.toMap());
			}			
		}
	}
	
	public List<Integer> getEmsGroupIdListBySysID(){
		HttpServletRequest request = ServletActionContext.getRequest();
		int id=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
		List<Integer> grouplist =new ArrayList<Integer>();
		try {
			List<Map> list = commonManagerService.getAllEmsGroups(id,false,true, false);
			for (Map map : list) {
				grouplist.add(Integer.parseInt(map.get("BASE_EMS_GROUP_ID").toString()));
			}
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return grouplist;
	}
	public List<Integer> getEmsIdListBySysID(int userId){
		List<Integer> emslist = new ArrayList<Integer>();
		List<Map> emsMapList = new ArrayList<Map>();
		try {
			List<Map> emsGroupMapList=commonManagerService.getAllEmsGroups(userId, false, true, false);
			for(Map map:emsGroupMapList){
				emsMapList.addAll(commonManagerService.getAllEmsByEmsGroupId(userId,Integer.parseInt(map.get("BASE_EMS_GROUP_ID").toString()),false, false));
			}
		} catch (CommonException e) {
			e.printStackTrace();
		}
		for (Map map : emsMapList) {
			emslist.add(Integer.parseInt(map.get("BASE_EMS_CONNECTION_ID").toString()));
		}
		return emslist;
	}
	public List<Integer> getEmsIdListByGroupAndSysID(int groupId){
		HttpServletRequest request = ServletActionContext.getRequest();
		int id=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
		List<Integer> emslist = new ArrayList<Integer>();
		List<Map> list = new ArrayList<Map>();
		try {
			list = commonManagerService.getAllEmsByEmsGroupId(id,groupId,false, false);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		for (Map map : list) {
			emslist.add(Integer.parseInt(map.get("BASE_EMS_CONNECTION_ID").toString()));
		}
		return emslist;
	}
	public List getEmsGroupIdListNullBySysID(){
		HttpServletRequest request = ServletActionContext.getRequest();
		int id=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
		List grouplist =new ArrayList();
		try {
			List<Map> list = commonManagerService.getAllEmsGroups(id,false,true, false);
			for (Map map : list) {
				if(String.valueOf(CommonDefine.VALUE_NONE).equals(map.get("BASE_EMS_GROUP_ID").toString())){
					grouplist.add("");
				}else{
					grouplist.add(Integer.parseInt(map.get("BASE_EMS_GROUP_ID").toString()));
				}
				
			}
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return grouplist;
	}
	
	public String toStringFromList(List<Integer> list){
		String str="(";
		//String str="";
		for(int i=0;i<list.size();i++){
			if(i!=(list.size()-1)){
				str+=list.get(i)+",";
			}else{
				str+=list.get(i);
			}
		}
		str+= ")";
		return str;
	}
	
	@Override
	public Map<String, Object> getReportAlarmByEms(int emsGroupId,
			int[] emsArr, String tableName, int start, int limit, int time) {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		if(emsArr!=null){
			// 网管
			condition.put("EMS_ID", new BasicDBObject("$in",emsArr));
		}else{
			// 网管分组不为全部
			if(emsGroupId!=-1 && emsGroupId!=0){
				condition.put("BASE_EMS_GROUP_ID", emsGroupId);
			}else if(emsGroupId==-1){
				condition.put("BASE_EMS_GROUP_ID","");
			}
		}
		Date startTime=null;
		Date endTime=null;
		try{
			if((time+"").length()==4){//2014
				startTime=sf.parse(time+"-01-01 00:00:00");
				endTime=sf.parse(time+1+"-01-01 00:00:00");
				condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lt",endTime));
			}else if((time+"").length()==6){//201401
				int year=Integer.parseInt((time+"").substring(0,4));
				int month=Integer.parseInt((time+"").substring(4));
				startTime=sf.parse(year+"-"+month+"-01 00:00:00");
				if(month==12){
					year++;
					month=1;
				}else{
					month++;
				}
				endTime=sf.parse(year+"-"+month+"-01 00:00:00");
				condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lt",endTime));
			}else{//20140101
				int year=Integer.parseInt((time+"").substring(0,4));
				int month=Integer.parseInt((time+"").substring(4,6));
				int day=Integer.parseInt((time+"").substring(6));
				startTime=sf.parse(year+"-"+month+"-"+day+" 00:00:00");
				endTime=sf.parse(year+"-"+month+"-"+day+" 23:59:59");
				condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lte",endTime));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		DBObject initial = new BasicDBObject();
		initial.put("count", 0);
	    String reduce = "function (doc,out) {out.count++; }";
		BasicDBList basicDBList =MongodbGroupUtil.group(conn,new String[]{"EMS_NAME"},condition,initial,reduce,null);
		Map<String, Object> valueMap = new HashMap<String, Object>();
		int count=0;
		List<DBObject> list = new ArrayList<DBObject>();
		if(basicDBList!=null && basicDBList.size()>0){
			// 告警总数
			count = basicDBList.size();
			// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
			for(int i=0;i<basicDBList.size();i++){
				DBObject dbo =(DBObject)basicDBList.get(i);
				list.add(dbo);
			}
		}
//		Collections.sort(list,new Comparator() {
//			@Override
//			public int compare(Object o1, Object o2) {
//				DBObject db1=(DBObject)o1;
//				DBObject db2=(DBObject)o2;
//				return db1.get("EMS_GROUP_NAME").toString().compareTo(db2.get("EMS_GROUP_NAME").toString());
//			}
//		});
		valueMap.put("total", count);
		valueMap.put("rows", list);
		return valueMap;
	}
	
	@Override
	public List<CurrentAlarmModel> getCurrentAlarmByNeIdForView(int neId) {
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		// 网元ID
		condition.put("NE_ID", neId);
		// 告警为发生告警
		condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
		// 指定告警反转状态
		condition.put("REVERSAL", false);
		
		// 查询过滤器设置
		HttpServletRequest request = ServletActionContext.getRequest();
		// 封装过滤器条件
		BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 

		// 封装子查询条件
		BasicDBList child = new BasicDBList ();
		child.add(condition);
		child.add(conditionFilter);
		// 合并子查询条件
		BasicDBObject allCondition = new BasicDBObject ();
		allCondition.put("$and", child);
		
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(allCondition);

		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<CurrentAlarmModel> list = new ArrayList<CurrentAlarmModel>();
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			// 定义告警对象
			CurrentAlarmModel alarmModel = new CurrentAlarmModel();
			// 封装告警信息
			alarmModel.setEmsId(Integer.parseInt(dbo.get("EMS_ID").toString()));// 网管ID
			alarmModel.setSubnetId("".equals(dbo.get("SUBNET_ID").toString())?-1:Integer.parseInt(dbo.get("SUBNET_ID").toString()));// 子网ID
			alarmModel.setNeId("".equals(dbo.get("NE_ID").toString())?-1:Integer.parseInt(dbo.get("NE_ID").toString()));// 网元ID
			alarmModel.setNeType("".equals(dbo.get("NE_TYPE").toString())?-1:Integer.parseInt(dbo.get("NE_TYPE").toString()));// 网元类型(网元的设备类型)
			alarmModel.setRackNo(dbo.get("RACK_NO").toString());// 机架标识
			alarmModel.setShelfNo(dbo.get("SHELF_NO").toString());// 子架标识
			alarmModel.setSlotNo(dbo.get("SLOT_NO").toString());// 槽道标识
			alarmModel.setPortNo(dbo.get("PORT_NO").toString());// 端口标识
			alarmModel.setDomain(dbo.get("DOMAIN").toString());// 端口类型标识
			alarmModel.setPerceivedSeverity(dbo.get("PERCEIVED_SEVERITY").toString());// 告警级别
			alarmModel.setObjectType("".equals(dbo.get("OBJECT_TYPE").toString())?-1:Integer.parseInt(dbo.get("OBJECT_TYPE").toString())); // 告警类型
			alarmModel.setAckState("".equals(dbo.get("IS_ACK").toString())?2:Integer.parseInt(dbo.get("IS_ACK").toString()));
			list.add(alarmModel);
		}
		return list;
	}
	
	@Override
	public List<CurrentAlarmModel> getCurrentAlarmByEmsIdForView(int emsId) {
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		// 网管ID
		condition.put("EMS_ID", emsId);
		// 告警为发生告警
		condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
		// 指定告警反转状态
		condition.put("REVERSAL", false);
		
		// 查询过滤器设置
		HttpServletRequest request = ServletActionContext.getRequest();
		// 封装过滤器条件
		BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 

		// 封装子查询条件
		BasicDBList child = new BasicDBList ();
		child.add(condition);
		child.add(conditionFilter);
		// 合并子查询条件
		BasicDBObject allCondition = new BasicDBObject ();
		allCondition.put("$and", child);
		
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(allCondition);
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<CurrentAlarmModel> list = new ArrayList<CurrentAlarmModel>();
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			// 定义告警对象
			CurrentAlarmModel alarmModel = new CurrentAlarmModel();
			// 封装告警信息
			alarmModel.setEmsId(Integer.parseInt(dbo.get("EMS_ID").toString()));// 网管ID
			alarmModel.setSubnetId("".equals(dbo.get("SUBNET_ID").toString())?-1:Integer.parseInt(dbo.get("SUBNET_ID").toString()));// 子网ID
			alarmModel.setNeId("".equals(dbo.get("NE_ID").toString())?-1:Integer.parseInt(dbo.get("NE_ID").toString()));// 网元ID
			alarmModel.setNeType("".equals(dbo.get("NE_TYPE").toString())?-1:Integer.parseInt(dbo.get("NE_TYPE").toString()));// 网元类型(网元的设备类型)
			alarmModel.setRackNo(dbo.get("RACK_NO").toString());// 机架标识
			alarmModel.setShelfNo(dbo.get("SHELF_NO").toString());// 子架标识
			alarmModel.setSlotNo(dbo.get("SLOT_NO").toString());// 槽道标识
			alarmModel.setPortNo(dbo.get("PORT_NO").toString());// 端口标识
			alarmModel.setDomain(dbo.get("DOMAIN").toString());// 端口类型标识
			alarmModel.setPerceivedSeverity(dbo.get("PERCEIVED_SEVERITY").toString());// 告警级别
			alarmModel.setObjectType("".equals(dbo.get("OBJECT_TYPE").toString())?-1:Integer.parseInt(dbo.get("OBJECT_TYPE").toString())); // 告警类型
			alarmModel.setAckState("".equals(dbo.get("IS_ACK").toString())?2:Integer.parseInt(dbo.get("IS_ACK").toString()));
			list.add(alarmModel);
		}
		return list;
	}
	
	@Override
	public List<CurrentAlarmModel> getCurrentAlarmBySubnetForView(List<Integer> subnetIdList) {
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		// 网管ID
		condition.put("SUBNET_ID", new BasicDBObject("$in",subnetIdList));
		// 告警为发生告警
		condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
		// 指定告警反转状态
		condition.put("REVERSAL", false);
		
		// 查询过滤器设置
		HttpServletRequest request = ServletActionContext.getRequest();
		// 封装过滤器条件
		BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 

		// 封装子查询条件
		BasicDBList child = new BasicDBList ();
		child.add(condition);
		child.add(conditionFilter);
		// 合并子查询条件
		BasicDBObject allCondition = new BasicDBObject ();
		allCondition.put("$and", child);
		
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(allCondition);		

		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<CurrentAlarmModel> list = new ArrayList<CurrentAlarmModel>();
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			// 定义告警对象
			CurrentAlarmModel alarmModel = new CurrentAlarmModel();
			// 封装告警信息
			alarmModel.setEmsId(Integer.parseInt(dbo.get("EMS_ID").toString()));// 网管ID
			alarmModel.setSubnetId("".equals(dbo.get("SUBNET_ID").toString())?-1:Integer.parseInt(dbo.get("SUBNET_ID").toString()));// 子网ID
			alarmModel.setNeId("".equals(dbo.get("NE_ID").toString())?-1:Integer.parseInt(dbo.get("NE_ID").toString()));// 网元ID
			alarmModel.setNeType("".equals(dbo.get("NE_TYPE").toString())?-1:Integer.parseInt(dbo.get("NE_TYPE").toString()));// 网元类型(网元的设备类型)
			alarmModel.setRackNo(dbo.get("RACK_NO").toString());// 机架标识
			alarmModel.setShelfNo(dbo.get("SHELF_NO").toString());// 子架标识
			alarmModel.setSlotNo(dbo.get("SLOT_NO").toString());// 槽道标识
			alarmModel.setPortNo(dbo.get("PORT_NO").toString());// 端口标识
			alarmModel.setDomain(dbo.get("DOMAIN").toString());// 端口类型标识
			alarmModel.setPerceivedSeverity(dbo.get("PERCEIVED_SEVERITY").toString());// 告警级别
			alarmModel.setObjectType("".equals(dbo.get("OBJECT_TYPE").toString())?-1:Integer.parseInt(dbo.get("OBJECT_TYPE").toString())); // 告警类型
			alarmModel.setAckState("".equals(dbo.get("IS_ACK").toString())?2:Integer.parseInt(dbo.get("IS_ACK").toString()));
			list.add(alarmModel);
		}
		return list;
	}
	@Override
	public Map<String, Object> getCurrentAlarmForCircuit(List<Integer> neIdList, List<Integer> ptpIdList, int start,int limit) {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		// 定义多字段or连接对象
		BasicDBList child = new BasicDBList ();
		BasicDBList child1 = new BasicDBList();
		
		// 查询过滤器设置
		HttpServletRequest request = ServletActionContext.getRequest();		
		// 封装过滤器条件
		BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 
		
		// 第一个or条件,仅包含PTP的告警（不含CTP告警）
		BasicDBObject childOne = new BasicDBObject ();
		childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
		// 防止某些AID级别的PTP告警出现，将查询条件由OBJECT_TYPE变成CTP_ID为空串
		//childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
		childOne.put("CTP_ID", "");	
		// 第二个or条件，仅包含PTP(含CTP)以外的所有告警
		BasicDBObject childTwo = new BasicDBObject ();
		childTwo.put("NE_ID", new BasicDBObject("$in",neIdList));
		childTwo.put("PTP_ID", "");
		child.add(childOne);
		child.add(childTwo);
		// 将多字段or连接条件添加的总的查询条件
		child1.add(conditionFilter);
		child1.add(new BasicDBObject("$or", child));
		// 指定告警反转状态
		child1.add(new BasicDBObject("REVERSAL", false));
		condition.put("$and", child1);
		// 告警总数
		int count = conn.find(condition).count();
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition).skip(start).limit(limit);
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
			dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
			dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
			dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
			dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
			dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
			dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
			list.add(dbo);
		}
		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("total", count);
		valueMap.put("rows", list);
		return valueMap;
	}

	@Override
	public Map<String,Object> getCurrentAlarmByNeIdListForCutover(List<Integer> neIdList) {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		condition.put("NE_ID", new BasicDBObject("$in",neIdList));
		// 指定告警反转状态
		condition.put("REVERSAL", false);
		//只返回未清除的告警
		condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
		// 告警总数
		int count = conn.find(condition).count();
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition);
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
			dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
			dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
			dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
			dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
			dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
			dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
			list.add(dbo);
		}
		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("rows", list);
		valueMap.put("total", count);
		return valueMap;
	}

	@Override
	public Map<String,Object> getCurrentAlarmByNeIdListAndTimeForCutover(List<Integer> neIdList, Date startTime,Date endTime,int start,int limit) {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		BasicDBList parent = new BasicDBList ();
		// 网元ID
		parent.add(new BasicDBObject("NE_ID", new BasicDBObject("$in",neIdList)));
		// 首次发生时间
		parent.add(new BasicDBObject("FIRST_TIME", new BasicDBObject("$lte",endTime)));
		// 指定告警反转状态
		parent.add(new BasicDBObject("REVERSAL", false));
		// 告警清楚时间
		BasicDBList child = new BasicDBList ();// 定义多字段or连接对象
		child.add(new BasicDBObject("CLEAR_TIME", new BasicDBObject("$gte",startTime)));
		child.add(new BasicDBObject("CLEAR_TIME", ""));
		BasicDBObject thrid = new BasicDBObject ();
		thrid.put("$or", child);
		parent.add(thrid);
		condition.put("$and", parent);
		// 告警总数
		int count = conn.find(condition).count();
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition).skip(start).limit(limit);
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
			dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
			dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
			dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
			dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
			dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
			dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
			list.add(dbo);
		}
		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("rows", list);
		valueMap.put("total", count);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmColorSet() throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 告警灯参数信息
		List<Map<String, Object>> colorList = alarmManagementMapper.getAlarmColor();
		String alarmLightHidden = "";
		if(!colorList.isEmpty()){
			for (int i = 0; i < colorList.size(); i++) {
				// 背景颜色
				if(Integer.parseInt(colorList.get(i).get("ALARM_TYPE").toString())==CommonDefine.ALARM_PS_CRITICAL){
					valueMap.put("PS_CRITICAL_IMAGE", colorList.get(i).get("COLOR_CODE").toString());
					valueMap.put("PS_CRITICAL_FONT", colorList.get(i).get("CHARACTER_COLOR_CODE").toString());
				}else if(Integer.parseInt(colorList.get(i).get("ALARM_TYPE").toString())==CommonDefine.ALARM_PS_MAJOR){
					valueMap.put("PS_MAJOR_IMAGE", colorList.get(i).get("COLOR_CODE").toString());
					valueMap.put("PS_MAJOR_FONT", colorList.get(i).get("CHARACTER_COLOR_CODE").toString());
				}else if(Integer.parseInt(colorList.get(i).get("ALARM_TYPE").toString())==CommonDefine.ALARM_PS_MINOR){
					valueMap.put("PS_MINOR_IMAGE", colorList.get(i).get("COLOR_CODE").toString());
					valueMap.put("PS_MINOR_FONT", colorList.get(i).get("CHARACTER_COLOR_CODE").toString());
				}else if(Integer.parseInt(colorList.get(i).get("ALARM_TYPE").toString())==CommonDefine.ALARM_PS_WARNING){
					valueMap.put("PS_WARNING_IMAGE", colorList.get(i).get("COLOR_CODE").toString());
					valueMap.put("PS_WARNING_FONT", colorList.get(i).get("CHARACTER_COLOR_CODE").toString());
				}else if(Integer.parseInt(colorList.get(i).get("ALARM_TYPE").toString())==CommonDefine.ALARM_PS_CLEARED){
					valueMap.put("PS_CLEARED_IMAGE", colorList.get(i).get("COLOR_CODE").toString());
					valueMap.put("PS_CLEARED_FONT", colorList.get(i).get("CHARACTER_COLOR_CODE").toString());
				}
				// 是否显示
				if(Integer.parseInt(colorList.get(i).get("DISPLAY_FLAG").toString())==CommonDefine.FALSE){
					alarmLightHidden += colorList.get(i).get("ALARM_TYPE").toString() + ",";
				}
			}
		}else{
			// 告警灯背景默认颜色
			valueMap.put("PS_CRITICAL_IMAGE", CommonDefine.PS_CRITICAL_IMAGE);
			valueMap.put("PS_MAJOR_IMAGE", CommonDefine.PS_MAJOR_IMAGE);
			valueMap.put("PS_MINOR_IMAGE", CommonDefine.PS_MINOR_IMAGE);
			valueMap.put("PS_WARNING_IMAGE", CommonDefine.PS_WARNING_IMAGE);
			valueMap.put("PS_CLEARED_IMAGE", CommonDefine.PS_CLEARED_IMAGE);
			// 告警字体默认颜色
			valueMap.put("PS_CRITICAL_FONT", CommonDefine.PS_CRITICAL_FONT);
			valueMap.put("PS_MAJOR_FONT", CommonDefine.PS_MAJOR_FONT);
			valueMap.put("PS_MINOR_FONT", CommonDefine.PS_MINOR_FONT);
			valueMap.put("PS_WARNING_FONT", CommonDefine.PS_WARNING_FONT);
			valueMap.put("PS_CLEARED_FONT", CommonDefine.PS_CLEARED_FONT);
		}
		valueMap.put("alarmLightHidden", alarmLightHidden);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmByIds(Map<String, Object> paramMap)
			throws CommonException {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn;
		if(paramMap.get("tabFlag").equals("current")){
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		}else{
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		}
		
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 封装查询条件
		BasicDBObject conditionQuery = new BasicDBObject ();
		
		if(!"".equals(paramMap.get("alarmIds").toString())){
			String[] alarArr = paramMap.get("alarmIds").toString().split(",");
			int[] alarArrI = new int[alarArr.length];
			for (int i = 0; i < alarArr.length; i++) {
				alarArrI[i]=Integer.parseInt(alarArr[i]);
			}
			conditionQuery.put("_id", new BasicDBObject("$in",alarArrI));
		}
		// 根据网元ID，查询当前告警总数
		int count = conn.find(conditionQuery).count();
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		if(count>0){
			// 根据网元ID、分页参数，查询当前告警信息
			DBCursor currentAlarm = conn.find(conditionQuery).sort(new BasicDBObject("NE_TIME",-1));
			while (currentAlarm.hasNext()) {
				DBObject dbo = currentAlarm.next();
				dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
				dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
				dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
				dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
				dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
				dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
				dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
				list.add(dbo);
			}
		}
		// 封装前台表格数据->列表信息
		valueMap.put("rows", list);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmMonthDataByEms(Map<String, Object> paramMap) {
		int time=Integer.parseInt(paramMap.get("time").toString().replaceAll("-", ""));
		String tableName=(String)paramMap.get("table_name");
		String ems_name=paramMap.get("EMS_NAME")==null?null:paramMap.get("EMS_NAME").toString();
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		if(ems_name!=null && !"".equals(ems_name)){
			// 网管
			condition.put("EMS_NAME",ems_name);
		}
		Date startTime=null;
		Date endTime=null;
		try{
			if((time+"").length()==4){//2014
				startTime=sf.parse(time+"-01-01 00:00:00");
				endTime=sf.parse(time+1+"-01-01 00:00:00");
				condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lt",endTime));
			}else if((time+"").length()==6){//201401
				int year=Integer.parseInt((time+"").substring(0,4));
				int month=Integer.parseInt((time+"").substring(4));
				startTime=sf.parse(year+"-"+month+"-01 00:00:00");
				if(month==12){
					year++;
					month=1;
				}else{
					month++;
				}
				endTime=sf.parse(year+"-"+month+"-01 00:00:00");
				condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lt",endTime));
			}else{//20140101
				int year=Integer.parseInt((time+"").substring(0,4));
				int month=Integer.parseInt((time+"").substring(4,6));
				int day=Integer.parseInt((time+"").substring(6));
				startTime=sf.parse(year+"-"+month+"-"+day+" 00:00:00");
				endTime=sf.parse(year+"-"+month+"-"+day+" 23:59:59");
				condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lte",endTime));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		DBObject initial = new BasicDBObject();
		initial.put("count", 0);
	    String reduce = "function (doc,out) {out.count++; }";
		BasicDBList basicDBList =MongodbGroupUtil.group(conn,new String[]{"EMS_NAME"},condition,initial,reduce,null);
		Map<String, Object> valueMap = new HashMap<String, Object>();
		int count=0;
		List<DBObject> list = new ArrayList<DBObject>();
		if(basicDBList!=null && basicDBList.size()>0){
			// 告警总数
			count = basicDBList.size();
			// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
			for(int i=0;i<basicDBList.size();i++){
				DBObject dbo =(DBObject)basicDBList.get(i);
				list.add(dbo);
			}
		}
//				Collections.sort(list,new Comparator() {
//					@Override
//					public int compare(Object o1, Object o2) {
//						DBObject db1=(DBObject)o1;
//						DBObject db2=(DBObject)o2;
//						return db1.get("EMS_GROUP_NAME").toString().compareTo(db2.get("EMS_GROUP_NAME").toString());
//					}
//				});
		valueMap.put("total", count);
		valueMap.put("rows", list);
		return valueMap;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	public List<Map> generateAlarmFromMonodb(Map param) {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn= null;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map> list = new ArrayList<Map>();
		try {
			// 封装查询条件
			BasicDBObject condition = new BasicDBObject ();
			BasicDBObject returnkey = new BasicDBObject ();
			int time=Integer.parseInt(param.get("day").toString().replaceAll("-", ""));
			int year=Integer.parseInt((time+"").substring(0,4));
			int month=Integer.parseInt((time+"").substring(4,6));
			int day=Integer.parseInt((time+"").substring(6));
			Date startTime=null;
			Date endTime=null;
			startTime=sf.parse(year+"-"+month+"-"+day+" 00:00:00");
			endTime=sf.parse(year+"-"+month+"-"+day+" 23:59:59");
			String firsttime=sf.format(startTime).substring(0,10);
			condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lte",endTime));
			returnkey.put("BASE_EMS_GROUP_ID", 1);
			returnkey.put("EMS_ID", 1);
			returnkey.put("PERCEIVED_SEVERITY", 1);
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			int count = conn.find(condition,new BasicDBObject()).count();
			if(count>0){
				DBCursor currentAlarm = conn.find(condition,returnkey);
				while (currentAlarm.hasNext()) {
					DBObject dbo = currentAlarm.next();
					Map<String,Object> map =new HashMap<String, Object>();
					map.put("FIRST_TIME", firsttime);
					//dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")).substring(0,10));
					if(dbo.get("BASE_EMS_GROUP_ID")==null || "".equals(dbo.get("BASE_EMS_GROUP_ID").toString()) || "null".equals(dbo.get("BASE_EMS_GROUP_ID").toString())){
						map.put("BASE_EMS_GROUP_ID",null);
					}else{
						map.put("BASE_EMS_GROUP_ID", dbo.get("BASE_EMS_GROUP_ID"));
					}
					if(dbo.get("EMS_ID")==null || "".equals(dbo.get("EMS_ID").toString()) || "null".equals(dbo.get("EMS_ID").toString())){
						map.put("EMS_ID",null);
					}else{
						map.put("EMS_ID", dbo.get("EMS_ID"));
					}
					if(dbo.get("PERCEIVED_SEVERITY")==null || "".equals(dbo.get("PERCEIVED_SEVERITY").toString()) || "null".equals(dbo.get("PERCEIVED_SEVERITY").toString())){
						map.put("PERCEIVED_SEVERITY",null);
					}else{
						map.put("PERCEIVED_SEVERITY", dbo.get("PERCEIVED_SEVERITY"));
					}
					list.add(map);
				}
			}
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
			count = conn.find(condition,new BasicDBObject()).count();
			if(count>0){
				DBCursor currentAlarm = conn.find(condition,returnkey);
				while (currentAlarm.hasNext()) {
					DBObject dbo = currentAlarm.next();
					Map<String,Object> map =new HashMap<String, Object>();
					map.put("FIRST_TIME", firsttime);
					//dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")).substring(0,10));
					if(dbo.get("BASE_EMS_GROUP_ID")==null || "".equals(dbo.get("BASE_EMS_GROUP_ID").toString()) || "null".equals(dbo.get("BASE_EMS_GROUP_ID").toString())){
						map.put("BASE_EMS_GROUP_ID",null);
					}else{
						map.put("BASE_EMS_GROUP_ID", dbo.get("BASE_EMS_GROUP_ID"));
					}
					if(dbo.get("EMS_ID")==null || "".equals(dbo.get("EMS_ID").toString()) || "null".equals(dbo.get("EMS_ID").toString())){
						map.put("EMS_ID",null);
					}else{
						map.put("EMS_ID", dbo.get("EMS_ID"));
					}
					if(dbo.get("PERCEIVED_SEVERITY")==null || "".equals(dbo.get("PERCEIVED_SEVERITY").toString()) || "null".equals(dbo.get("PERCEIVED_SEVERITY").toString())){
						map.put("PERCEIVED_SEVERITY",null);
					}else{
						map.put("PERCEIVED_SEVERITY", dbo.get("PERCEIVED_SEVERITY"));
					}
					list.add(map);
				}
			}
//			valueMap.put("total", count);
//			valueMap.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	@Override
	public Map<String, Object> getAllCurrentAlarmCountForCircuit(List<Integer> neIdList, List<Integer> ptpIdList) {
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		// 定义多字段and和or连接对象
		BasicDBList child = new BasicDBList ();
		BasicDBList child1 = new BasicDBList();
		
		// 查询过滤器设置
		HttpServletRequest request = ServletActionContext.getRequest();		
		// 封装过滤器条件
		BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 
		
		// 第一个or条件,仅包含PTP的告警（不含CTP告警）
		BasicDBObject childOne = new BasicDBObject ();
		childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
		// 防止某些AID级别的PTP告警出现，将查询条件由OBJECT_TYPE变成CTP_ID为空串
		//childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
		childOne.put("CTP_ID", "");		
		// 第二个or条件，仅包含PTP(含CTP)以外的所有告警
		BasicDBObject childTwo = new BasicDBObject ();
		childTwo.put("NE_ID", new BasicDBObject("$in",neIdList));
		childTwo.put("PTP_ID", "");
		child.add(childOne);
		child.add(childTwo);
		// 将过滤条件和查询条件进行and连接，添加的总的查询条件
		child1.add(conditionFilter);
		child1.add(new BasicDBObject("$or", child));
		// 指定告警反转状态
		child1.add(new BasicDBObject("REVERSAL", false));
		condition.put("$and", child1);

		Map<String,Object> remap = new HashMap<String, Object>();
		remap.put("PS_CRITICAL", 0);//1
		remap.put("PS_MAJOR", 0);//2
		remap.put("PS_MINOR", 0);//3
		remap.put("PS_WARNING", 0);//4
		remap.put("PS_CLEARED", 0);//5
		
		DBCursor alarm = conn.find(condition);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			if (Integer.parseInt(dbo.get("IS_CLEAR").toString()) == CommonDefine.IS_CLEAR_YES) {
				remap.put("PS_CLEARED", Integer.parseInt(remap.get("PS_CLEARED").toString()) + 1);
			} else {
				int level = Integer.parseInt(dbo.get("PERCEIVED_SEVERITY").toString());
				if (level == CommonDefine.ALARM_PS_CRITICAL){
					remap.put("PS_CRITICAL", Integer.parseInt(remap.get("PS_CRITICAL").toString())+1);
				} else if (level == CommonDefine.ALARM_PS_MAJOR) {
					remap.put("PS_MAJOR", Integer.parseInt(remap.get("PS_MAJOR").toString()) + 1);
				} else if (level == CommonDefine.ALARM_PS_MINOR) {
					remap.put("PS_MINOR", Integer.parseInt(remap.get("PS_MINOR").toString()) + 1);
				} else if (level == CommonDefine.ALARM_PS_WARNING) {
					remap.put("PS_WARNING", Integer.parseInt(remap.get("PS_WARNING").toString()) + 1);
				}
			}
		}	
		return remap;
	}


	@Override
	public boolean deleteAlarmByEmsIds(List<Integer> emsIds) {
		try{
			// 获取数据库连接
			DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			BasicDBObject condition = new BasicDBObject ();
			condition.put("EMS_ID", new BasicDBObject("$in",emsIds));
			conn.remove(condition);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void deleteAlarmAutoSynchSetting(int emsConnectionId)
			throws CommonException {
		List<Map<String,Object>> list = alarmManagementMapper.findAlarmAutoSynch(emsConnectionId);
		if(list!=null&&list.size()>0){
			for (Map<String, Object> map : list) {
				String ids=map.get("ID").toString();
				int id=Integer.parseInt(ids);
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
						id,
						CommonDefine.QUARTZ.JOB_PAUSE);
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_ALARMSYNCH,
						id,
						CommonDefine.QUARTZ.JOB_DELETE);
			}
			alarmManagementMapper.deleteAlarmAutoSynch(emsConnectionId);
		}
	}
	
	
	/*
	 * 补数据
	 */
	@Override
	public Map<String, Object> generateData(Map param) {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn= null;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			// 封装查询条件
			BasicDBObject condition = new BasicDBObject ();
			int time=Integer.parseInt(param.get("day").toString().replaceAll("-", ""));
			int endT=Integer.parseInt(param.get("endDay").toString().replaceAll("-", ""));
			int year=Integer.parseInt((time+"").substring(0,4));
			int month=Integer.parseInt((time+"").substring(4,6));
			int day=Integer.parseInt((time+"").substring(6));
			int endYear=Integer.parseInt((endT+"").substring(0,4));
			int endMonth=Integer.parseInt((endT+"").substring(4,6));
			int endDay=Integer.parseInt((endT+"").substring(6));
			Date startTime=null;
			Date endTime=null;
			startTime=sf.parse(year+"-"+month+"-"+day+" 00:00:00");
			endTime=sf.parse(endYear+"-"+endMonth+"-"+endDay+" 23:59:59");
			condition.put("FIRST_TIME", new BasicDBObject("$gte",startTime).append("$lte",endTime));
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			int count = conn.find(condition).count();
			if(count>0){
				DBCursor currentAlarm = conn.find(condition);
				while (currentAlarm.hasNext()) {
					DBObject dbo = currentAlarm.next();
					dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")).substring(0,10));
					if(dbo.get("BASE_EMS_GROUP_ID")==null || "".equals(dbo.get("BASE_EMS_GROUP_ID").toString()) || "null".equals(dbo.get("BASE_EMS_GROUP_ID").toString())){
						dbo.put("BASE_EMS_GROUP_ID",null);
					}
					if(dbo.get("EMS_ID")==null || "".equals(dbo.get("EMS_ID").toString()) || "null".equals(dbo.get("EMS_ID").toString())){
						dbo.put("EMS_ID",null);
					}
					if(dbo.get("PERCEIVED_SEVERITY")==null || "".equals(dbo.get("PERCEIVED_SEVERITY").toString()) || "null".equals(dbo.get("PERCEIVED_SEVERITY").toString())){
						dbo.put("PERCEIVED_SEVERITY",null);
					}
					list.add(dbo);
				}
			}
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
			count = conn.find(condition).count();
			if(count>0){
				DBCursor currentAlarm = conn.find(condition);
				while (currentAlarm.hasNext()) {
					DBObject dbo = currentAlarm.next();
					dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")).substring(0,10));
					if(dbo.get("BASE_EMS_GROUP_ID")==null || "".equals(dbo.get("BASE_EMS_GROUP_ID").toString()) || "null".equals(dbo.get("BASE_EMS_GROUP_ID").toString())){
						dbo.put("BASE_EMS_GROUP_ID",null);
					}
					if(dbo.get("EMS_ID")==null || "".equals(dbo.get("EMS_ID").toString()) || "null".equals(dbo.get("EMS_ID").toString())){
						dbo.put("EMS_ID",null);
					}
					if(dbo.get("PERCEIVED_SEVERITY")==null || "".equals(dbo.get("PERCEIVED_SEVERITY").toString()) || "null".equals(dbo.get("PERCEIVED_SEVERITY").toString())){
						dbo.put("PERCEIVED_SEVERITY",null);
					}
					list.add(dbo);
				}
			}
			valueMap.put("total", count);
			valueMap.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return valueMap;
	}


	@Override
	public void updateAlarmAutoSynchExcuteStatus(Map<String, Object> map) {
		alarmManagementMapper.updateLatestAlarmAutoSynchTime(map);
	}


	@Override
	public void updateDBbackup(Map<String,Object> manuValue) {
		alarmManagementMapper.updateDBbackup(manuValue);
	}


	@Override
	public List<Map<String,Object>> getAutoAlarmSyncByEmsId(List<Integer> emsIds) {
		return alarmManagementMapper.getAutoAlarmSyncByEmsId(emsIds);
	}
	
	@Override
	public void alarmReversal(Map<String, Object> paramMap) throws CommonException {
		String[] idsArrS = paramMap.get("ids").toString().split(",");
		int[] idsArrI = new int[idsArrS.length];
		for (int i = 0; i < idsArrS.length; i++) {
			idsArrI[i] = Integer.parseInt(idsArrS[i]);
		}
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		condition.put("_id", new BasicDBObject("$in",idsArrI));
		condition.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			// 检查确认状态，如未确认则对告警进行确认操作
			if (Integer.parseInt(dbo.get("IS_ACK").toString()) == CommonDefine.IS_ACK_NO) {
				dbo.put("IS_ACK", CommonDefine.IS_ACK_YES);
				dbo.put("ACK_TIME", new Date());
				HttpSession session = ServletActionContext.getRequest().getSession();
				dbo.put("ACK_USER", session.getAttribute("USER_NAME").toString());
			}
			// 设置告警反转状态
			dbo.put("REVERSAL", true);
			dbo.put("UPDATE_TIME",new Date());
			// 更新表中数据
			conn.save(dbo);
			
			// 设置视图模块的清除状态
			dbo.put("status", CommonDefine.ALARM_STATUS_CLEARED);
			// 设置告警的清除时间和状态
			dbo.put("CLEAR_TIME", new Date());
			dbo.put("IS_CLEAR",CommonDefine.IS_CLEAR_YES);

			// 推送告警清除消息给视图模块和综告接口
			pushAlarmMessage(dbo.toMap());		
		}
	}
	
	@Override
	public void antiAlarmReversal(Map<String, Object> paramMap) throws CommonException {
		String[] idsArrS = paramMap.get("ids").toString().split(",");
		int[] idsArrI = new int[idsArrS.length];
		for (int i = 0; i < idsArrS.length; i++) {
			idsArrI[i] = Integer.parseInt(idsArrS[i]);
		}
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject ();
		condition.put("_id", new BasicDBObject("$in",idsArrI));
		condition.put("REVERSAL", true);
		// 查询符合条件的告警数据
		DBCursor alarm = conn.find(condition);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			if ((Boolean)dbo.get("REVERSAL")) {
				// 设置告警反转状态
				dbo.put("REVERSAL", false);	
				dbo.put("NE_TIME", new Date());
				dbo.put("EMS_TIME", new Date());
				dbo.put("IS_ACK", CommonDefine.IS_ACK_NO);
				dbo.put("ACK_USER", "");
				dbo.put("ACK_TIME", "");
				
				// 更新表中数据
				conn.save(dbo);
				// 推送告警产生消息给视图模块
				dbo.put("status", CommonDefine.ALARM_STATUS_OCCUR);
				pushAlarmMessage(dbo.toMap());					
			}		
		}
	}
	
	@Override
	public Map<String, Object> getProtectionSwitch(
			Map<String, Object> paramMap, int start, int limit)
			throws CommonException {
		Map<String, Object> rv = new HashMap<String, Object>();
		int total = alarmManagementMapper.getProtectionSwitchCount(paramMap);
		List<Map<String, Object>>data = alarmManagementMapper.getProtectionSwitch(paramMap, start, limit);
		rv.put("total", total);
		rv.put("rows", data);
		return rv;
	}



	@SuppressWarnings("rawtypes")
	@Override
	public List<Map<String, Object>> getPmExceedData(
			Map<String, Object> paramMap, int start, int limit)
			throws CommonException {
		String pmStdIndexIdString = "";
		List pm = (List) paramMap.get("pmStdIndexs");
		pmStdIndexIdString = pm.toString();
		pmStdIndexIdString = pmStdIndexIdString.substring(1, pmStdIndexIdString.length() - 1);
		boolean maxMin = (Boolean) paramMap.get("maxMinFlag");
		List<String> pmStdIndex = performanceManagerMapper.getPmStdIndexes(
				pmStdIndexIdString, maxMin);
		String PM_STD_INDEX = "";
		for (String s : pmStdIndex) {
			PM_STD_INDEX += "'" + s + "',";
		}
		PM_STD_INDEX = "(" + PM_STD_INDEX.substring(0, PM_STD_INDEX.length()-1) + ")";
		paramMap.put("pmStdIndex", PM_STD_INDEX);
		return alarmManagementMapper.getPmExceedData(paramMap, start, limit);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getAlarmHavingConverge(int _id) throws CommonException{ 
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		// 定义查询结果集
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 获取当前用户ID
		HttpServletRequest request = ServletActionContext.getRequest();
		Integer userId = (Integer)request.getSession().getAttribute("SYS_USER_ID");
		// 封装查询条件
		BasicDBObject conditionQuery = new BasicDBObject (); 
		// 显示非管理员用户的所有告警
		if (userId != CommonDefine.USER_ADMIN_ID) {
			int[] emsArray = null; int[] subnetArray = null; int[] neArray = null;
			BasicDBList devDomain = new BasicDBList ();				
			List<Map<String, Object>> emsList = alarmManagementMapper.getEmsIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
			if (emsList != null && emsList.size() > 0) {
				emsArray = new int[emsList.size()];
				for (int i = 0; i < emsArray.length; i++) {
					emsArray[i]=Integer.parseInt(emsList.get(i).get("BASE_EMS_CONNECTION_ID").toString());
				}
				devDomain.add(new BasicDBObject("EMS_ID", new BasicDBObject("$in", emsArray)));
			}
			List<Map<String, Object>> subnetList = alarmManagementMapper.getSubnetIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
			if (subnetList != null && subnetList.size() > 0) {
				subnetArray = new int[subnetList.size()];
				for (int i = 0; i < subnetArray.length; i++) {
					subnetArray[i]=Integer.parseInt(subnetList.get(i).get("BASE_SUBNET_ID").toString());
				}
				devDomain.add(new BasicDBObject("SUBNET_ID", new BasicDBObject("$in", subnetArray)));
			}
			List<Map<String, Object>> neList = alarmManagementMapper.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
			if (neList != null && neList.size() > 0) {
				neArray = new int[neList.size()];
				for (int i = 0; i < neArray.length; i++) {
					neArray[i]=Integer.parseInt(neList.get(i).get("BASE_NE_ID").toString());
				}
				devDomain.add(new BasicDBObject("NE_ID", new BasicDBObject("$in", neArray)));
			}
			
			if (devDomain.size() > 1) {
				conditionQuery.put("$or", devDomain);
			} else {
				if (emsList.size() > 0) {
					conditionQuery.put("EMS_ID", new BasicDBObject("$in", emsArray));
				} else if (subnetList.size() > 0) {
					conditionQuery.put("SUBNET_ID", new BasicDBObject("$in", subnetArray));
				} else if (neList.size() > 0) {
					conditionQuery.put("NE_ID", new BasicDBObject("$in", neArray));
				}
			}
		} 
		conditionQuery.put("PARENT_ID",_id);  
		int count = conn.find(conditionQuery).count(); 
		List<DBObject> list = new ArrayList<DBObject>();
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		if(count>0){
			// 根据网元ID、分页参数，查询当前告警信息
			DBCursor currentAlarm = conn.find(conditionQuery).sort(new BasicDBObject("NE_TIME",-1));
			String duration = "";
			while (currentAlarm.hasNext()) {
				DBObject dbo = currentAlarm.next();
				dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
				dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
				dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
				dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
				dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
				dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
				dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
				// 计算告警持续时间
				if ((Integer)dbo.get("IS_CLEAR") == CommonDefine.IS_CLEAR_YES) {
					duration = getTimeDif(dbo.get("CLEAR_TIME").toString(), dbo.get("FIRST_TIME").toString());
				} else {
					duration = getTimeDif(sf.format(new Date()), dbo.get("FIRST_TIME").toString());
				}
				dbo.put("DURATION", duration);
				list.add(dbo);
			}
		}  
		// 封装前台表格数据->列表信息
		valueMap.put("rows", list); 
		return valueMap;
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IAlarmManagementService#getCurrentAlarmForCircuit(int, int, int, int, boolean, boolean, boolean, boolean)
	 */
	@Override
	public Map<String, Object> getCurrentAlarmForCircuit(int circuitInfoId,
			int circuitType, int start,int limit,boolean isConverge,boolean needPtpLevel, boolean needCtpLevel,
			boolean needNeLevel, boolean needEquipLevel) throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Integer> ptpIdList = new ArrayList();
		List<Integer> ctpIdList = new ArrayList();
		List<Integer> neIdList = new ArrayList();
		List<Integer> equipIdList = new ArrayList();
		try {
			//电路类型为普通电路
			if(1==circuitType)
			{
				if(needPtpLevel)
					ptpIdList = queryPtpInCircuit(circuitInfoId);
				if(needCtpLevel)
					ctpIdList = queryCtpInCircuit(circuitInfoId);
				if(needNeLevel)
					neIdList = queryNeInCircuit(circuitInfoId);
//				if(needEquipLevel)
//					equipIdList = queryEquipInCircuit(circuitInfoId);
			}
			//电路类型为OTN电路
			else if(2==circuitType)
			{
				if(needPtpLevel)
					ptpIdList = queryPtpInOtnCircuit(circuitInfoId);
				if(needCtpLevel)
					ctpIdList = queryCtpInOtnCircuit(circuitInfoId);
				if(needNeLevel)
					neIdList = queryNeInOtnCircuit(circuitInfoId);
//				if(needEquipLevel)
//					equipIdList = queryEquipInOtnCircuit(circuitInfoId);
			}
			// 定义时间格式转换器
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 获取数据库连接
			DBCollection conn = null;
			try {
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 封装查询条件
			BasicDBObject condition = new BasicDBObject ();
			// 定义多字段or连接对象
			BasicDBList child = new BasicDBList ();
			BasicDBList child1 = new BasicDBList();
			
			// 查询过滤器设置
			HttpServletRequest request = ServletActionContext.getRequest();		
			// 封装过滤器条件
//				BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 
			//添加一个肯定不成立的条件，防止与后面的进行or操作
			BasicDBObject childZero = new BasicDBObject ();
			childZero.put("_id", new BasicDBObject("$lte",-1));
			child.add(childZero);
			if(needPtpLevel)
			{
				// 第一个or条件,仅包含PTP的告警（不含CTP告警）
				BasicDBObject childOne = new BasicDBObject ();
				childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
				childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
				child.add(childOne);
			}
			if(needCtpLevel)
			{
				// 第4个or条件，ctp级别告警
				BasicDBObject childFour = new BasicDBObject ();
				childFour.put("CTP_ID", new BasicDBObject("$in",ctpIdList));
				childFour.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
				child.add(childFour);
			}
//			else if(!needPtpLevel && needCtpLevel)
//			{
//				BasicDBObject childOne = new BasicDBObject ();
//				childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
//				childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
//				child.add(childOne);
//			}
			if(needNeLevel)
			{
				// 第二个or条件，网元级别告警
				BasicDBObject childTwo = new BasicDBObject ();
				childTwo.put("NE_ID", new BasicDBObject("$in",neIdList));
				childTwo.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_MANAGED_ELEMENT);
				child.add(childTwo);
			}
			if(needEquipLevel)
			{
				// 第三个or条件，板卡级别告警
				BasicDBObject childThree = new BasicDBObject ();
				childThree.put("NE_ID", new BasicDBObject("$in",neIdList));
				childThree.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_EQUIPMENT);
				child.add(childThree);
			}
			
			
			// 将多字段or连接条件添加的总的查询条件
//				child1.add(conditionFilter);
			child1.add(new BasicDBObject("$or", child));
			// 指定告警反转状态
			child1.add(new BasicDBObject("REVERSAL", false));
			// 告警收敛显示标志 conditionQuery.put("REVERSAL", false);
			if (isConverge) {
				BasicDBObject converge = new BasicDBObject ();
				converge.put("CONVERGE_FLAG", new BasicDBObject("$ne", CommonDefine.ALARM_CONVERGE_DERIVATIVE_ALARM));
				child1.add(converge);
			}
			condition.put("$and", child1);
			// 告警总数
			int count = conn.find(condition).count();
			// 查询符合条件的告警数据
			DBCursor alarm = null;
			if(start!=-1 && limit!=-1)
			{
				alarm = conn.find(condition).skip(start).limit(limit);
			}
			else
			{
				alarm = conn.find(condition);
			}
				
			// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
			List<DBObject> list = new ArrayList<DBObject>();
			while (alarm.hasNext()) {
				DBObject dbo = alarm.next();
				dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
				dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
				dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
				dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
				dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
				dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
				dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
				list.add(dbo);
			}
			valueMap.put("total", count);
			valueMap.put("rows", list);
		} catch (MongoException e) {
			
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		} catch (SQLException e) {
			
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		}
		return valueMap;
	}
	
	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IAlarmManagementService#getCurrentAlarmForCircuit(java.util.List, int, int, boolean, boolean, boolean, boolean, boolean)
	 */
	public Map<String, Object> getCurrentAlarmForCircuit(List<Map> circuitList,
			int start, int limit, boolean isConverge, boolean needPtpLevel,
			boolean needCtpLevel, boolean needNeLevel, boolean needEquipLevel)
			throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Integer> ptpIdList = new ArrayList();
		List<Integer> ctpIdList = new ArrayList();
		List<Integer> neIdList = new ArrayList();
		List<Integer> equipIdList = new ArrayList();
		try {
			for(int i=0,len=circuitList.size();i<len;i++)
			{
				Integer circuitType = Integer.valueOf(circuitList.get(i).get("circuitType").toString());
				Integer circuitInfoId = Integer.valueOf(circuitList.get(i).get("circuitInfoId").toString());
				//电路类型为普通电路
				if(1==circuitType)
				{
					if(needPtpLevel)
						ptpIdList.addAll(queryPtpInCircuit(circuitInfoId));
					if(needCtpLevel)
						ctpIdList.addAll(queryCtpInCircuit(circuitInfoId));
					if(needNeLevel)
						neIdList.addAll(queryNeInCircuit(circuitInfoId));
//					if(needEquipLevel)
//						equipIdList.addAll(queryEquipInCircuit(circuitInfoId));
				}
				//电路类型为OTN电路
				else if(2==circuitType)
				{
					if(needPtpLevel)
						ptpIdList.addAll(queryPtpInOtnCircuit(circuitInfoId));
					if(needCtpLevel)
						ctpIdList.addAll(queryCtpInOtnCircuit(circuitInfoId));
					if(needNeLevel)
						neIdList.addAll(queryNeInOtnCircuit(circuitInfoId));
//					if(needEquipLevel)
//						equipIdList.addAll(queryEquipInOtnCircuit(circuitInfoId));
				}
			}
			
			// 定义时间格式转换器
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 获取数据库连接
			DBCollection conn = null;
			try {
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 封装查询条件
			BasicDBObject condition = new BasicDBObject ();
			// 定义多字段or连接对象
			BasicDBList child = new BasicDBList ();
			BasicDBList child1 = new BasicDBList();
			
			// 查询过滤器设置
			HttpServletRequest request = ServletActionContext.getRequest();		
			// 封装过滤器条件
//				BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 
			//添加一个肯定不成立的条件，防止与后面的进行or操作
			BasicDBObject childZero = new BasicDBObject ();
			childZero.put("_id", new BasicDBObject("$lte",-1));
			child.add(childZero);
			if(needPtpLevel )
			{
				// 第一个or条件,仅包含PTP的告警（不含CTP告警）
				BasicDBObject childOne = new BasicDBObject ();
				childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
				childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
				child.add(childOne);
			}
			if(needCtpLevel)
			{
				// 第4个or条件，ctp级别告警
				BasicDBObject childFour = new BasicDBObject ();
				childFour.put("CTP_ID", new BasicDBObject("$in",ctpIdList));
				childFour.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
				child.add(childFour);
			}
//			else if(!needPtpLevel && needCtpLevel)
//			{
//				BasicDBObject childOne = new BasicDBObject ();
//				childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
//				childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
//				child.add(childOne);
//			}
			if(needNeLevel)
			{
				// 第二个or条件，网元级别告警
				BasicDBObject childTwo = new BasicDBObject ();
				childTwo.put("NE_ID", new BasicDBObject("$in",neIdList));
				childTwo.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_MANAGED_ELEMENT);
				child.add(childTwo);
			}
			if(needEquipLevel)
			{
				// 第三个or条件，板卡级别告警
				BasicDBObject childThree = new BasicDBObject ();
				childThree.put("NE_ID", new BasicDBObject("$in",neIdList));
				childThree.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_EQUIPMENT);
				child.add(childThree);
			}
			
			// 将多字段or连接条件添加的总的查询条件
//				child1.add(conditionFilter);
			child1.add(new BasicDBObject("$or", child));
			// 指定告警反转状态
			child1.add(new BasicDBObject("REVERSAL", false));
			// 告警收敛显示标志 conditionQuery.put("REVERSAL", false);
			if (isConverge) {
				BasicDBObject converge = new BasicDBObject ();
				converge.put("CONVERGE_FLAG", new BasicDBObject("$ne", CommonDefine.ALARM_CONVERGE_DERIVATIVE_ALARM));
				child1.add(converge);
			}
			condition.put("$and", child1);
			// 告警总数
			int count = conn.find(condition).count();
			// 查询符合条件的告警数据
			DBCursor alarm = null;
			if(start!=-1 && limit!=-1)
			{
				alarm = conn.find(condition).skip(start).limit(limit);
			}
			else
			{
				alarm = conn.find(condition);
			}
				
			// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
			List<DBObject> list = new ArrayList<DBObject>();
			while (alarm.hasNext()) {
				DBObject dbo = alarm.next();
				dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
				dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
				dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
				dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
				dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
				dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
				dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
				list.add(dbo);
			}
			valueMap.put("total", count);
			valueMap.put("rows", list);
		} catch (MongoException e) {
			
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		} catch (SQLException e) {
			
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		}
		return valueMap;
	}
	
	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IAlarmManagementService#getCurrentAlarmForCircuit(java.util.List, java.util.List, java.util.List, java.util.List, int, int, boolean, boolean, boolean, boolean, boolean)
	 */
	public Map<String, Object> getCurrentAlarmForCircuit(
			List<Integer> neIdList, List<Integer> ptpIdList,
			List<Integer> ctpIdList, List<Integer> equipIdList, int start,
			int limit, boolean isConverge, boolean needPtpLevel,
			boolean needCtpLevel, boolean needNeLevel, boolean needEquipLevel)
			throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		
		try {
			
			// 定义时间格式转换器
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 获取数据库连接
			DBCollection conn = null;
			try {
				conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 封装查询条件
			BasicDBObject condition = new BasicDBObject ();
			// 定义多字段or连接对象
			BasicDBList child = new BasicDBList ();
			BasicDBList child1 = new BasicDBList();
			
			// 查询过滤器设置
			HttpServletRequest request = ServletActionContext.getRequest();		
			// 封装过滤器条件
//				BasicDBObject conditionFilter = getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID")); 
			//添加一个肯定不成立的条件，防止与后面的进行or操作
//			BasicDBObject childZero = new BasicDBObject ();
//			childZero.put("_id", new BasicDBObject("$lte",-1));
//			child.add(childZero);
			if(needPtpLevel )
			{
				// 第一个or条件,仅包含PTP的告警（不含CTP告警）
				BasicDBObject childOne = new BasicDBObject ();
				childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
				childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
				child.add(childOne);
			}
			if(needCtpLevel)
			{
				// 第4个or条件，ctp级别告警
				BasicDBObject childFour = new BasicDBObject ();
				childFour.put("CTP_ID", new BasicDBObject("$in",ctpIdList));
				childFour.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
				child.add(childFour);
			}
//			else if(!needPtpLevel && needCtpLevel)
//			{
//				BasicDBObject childOne = new BasicDBObject ();
//				childOne.put("PTP_ID", new BasicDBObject("$in",ptpIdList));
//				childOne.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
//				child.add(childOne);
//			}
			if(needNeLevel)
			{
				// 第二个or条件，网元级别告警
				BasicDBObject childTwo = new BasicDBObject ();
				childTwo.put("NE_ID", new BasicDBObject("$in",neIdList));
				childTwo.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_MANAGED_ELEMENT);
				child.add(childTwo);
			}
			if(needEquipLevel)
			{
				// 第三个or条件，板卡级别告警
				BasicDBObject childThree = new BasicDBObject ();
				childThree.put("NE_ID", new BasicDBObject("$in",neIdList));
				childThree.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_EQUIPMENT);
				child.add(childThree);
			}
			
			// 将多字段or连接条件添加的总的查询条件
//				child1.add(conditionFilter);
			child1.add(new BasicDBObject("$or", child));
			// 指定告警反转状态
			child1.add(new BasicDBObject("REVERSAL", false));
			// 告警收敛显示标志 conditionQuery.put("REVERSAL", false);
			if (isConverge) {
				BasicDBObject converge = new BasicDBObject ();
				converge.put("CONVERGE_FLAG", new BasicDBObject("$ne", CommonDefine.ALARM_CONVERGE_DERIVATIVE_ALARM));
				child1.add(converge);
			}
			condition.put("$and", child1);
			// 告警总数
			int count = conn.find(condition).count();
			// 查询符合条件的告警数据
			DBCursor alarm = null;
			if(start!=-1 && limit!=-1)
			{
				alarm = conn.find(condition).skip(start).limit(limit);
			}
			else
			{
				alarm = conn.find(condition);
			}
				
			// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
			List<DBObject> list = new ArrayList<DBObject>();
			while (alarm.hasNext()) {
				DBObject dbo = alarm.next();
				dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
				dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
				dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
				dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
				dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
				dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
				dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
				list.add(dbo);
			}
			valueMap.put("total", count);
			valueMap.put("rows", list);
		} catch (MongoException e) {
			
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		}
		return valueMap;
	}
	/**
	 * 查询电路经过的ptpId
	 * 
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryPtpInCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> ptpIdList = new ArrayList();
		ptpIdList = kAMap.queryPtpInCircuit(circuitInfoId);
		return ptpIdList;
	}

	/**查询电路经过的ctpId
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryCtpInCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> ctpIdList = new ArrayList();
		ctpIdList = kAMap.queryCtpInCircuit(circuitInfoId);
		return ctpIdList;
	}

	/**查询电路经过的neId
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryNeInCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> neIdList = new ArrayList();
		neIdList = kAMap.queryNeInCircuit(circuitInfoId);
		return neIdList;
	}

	/**查询电路经过的equipId
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryEquipInCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> equipIdList = new ArrayList();
		equipIdList = kAMap.queryNeInCircuit(circuitInfoId);
		return equipIdList;
	}

	/**
	 * 查询otn电路经过的ptpId
	 * 
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryPtpInOtnCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> ptpIdList = new ArrayList();
		ptpIdList = kAMap.queryPtpInOtnCircuit(circuitInfoId);
		return ptpIdList;
	}

	/**
	 * 查询otn电路经过的ctpId
	 * 
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryCtpInOtnCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> ctpIdList = new ArrayList();
		ctpIdList = kAMap.queryCtpInOtnCircuit(circuitInfoId);
		return ctpIdList;
	}

	/**
	 * 查询otn电路经过的网元Id
	 * 
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryNeInOtnCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> neIdList = new ArrayList();
		neIdList = kAMap.queryNeInOtnCircuit(circuitInfoId);
		return neIdList;
	}

	/**
	 * 查询otn电路经过的板卡Id
	 * 
	 * @param circuitInfoId
	 * @return
	 */
	private List<Integer> queryEquipInOtnCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> equipIdList = new ArrayList();
		equipIdList = kAMap.queryEquipInOtnCircuit(circuitInfoId);
		return equipIdList;
	}
	// 返回指定子网ID下所有嵌套的子网ID列表，其中包含指定子网ID
	private List<Integer> getSubnetIds(String subnetId) {
		List <Integer> result = new ArrayList<Integer>();
		int id = Integer.valueOf(subnetId);
		result.add(id);
		// 查询子网信息
		String tblName = "t_base_subnet";
		String idName = "PARENT_SUBNET";
		List<Map> subnetList = commonManagerMapper.selectTableListById(tblName, idName, id, null, null);
		if (subnetList != null && subnetList.size() > 0) {
			for (Map subnet : subnetList) {
				result.addAll(getSubnetIds(subnet.get("BASE_SUBNET_ID").toString()));
			}
		}
		return result;
	}
	
	public Map<String, Integer> getCableSectionAlarmState(List<String> cableIds) throws CommonException {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<Map<String, Object>> ptpList = alarmManagementMapper.getPtpListByCableIds(cableIds);
		List<Integer> ptpIds = new ArrayList<Integer>();
		for(Map<String, Object> ptp : ptpList) {
			if(ptp.get("PTP_ID") != null) {
				ptpIds.add((Integer)ptp.get("PTP_ID"));
			}
		}
		
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).
				getCollection(CommonDefine.T_CURRENT_ALARM);
		BasicDBObject condition = new BasicDBObject ();
		condition.put("PTP_ID",  new BasicDBObject("$in", ptpIds));
		Pattern pattern = Pattern.compile("los?", Pattern.CASE_INSENSITIVE);
		condition.put("NATIVE_PROBABLE_CAUSE", pattern);
		BasicDBObject keys = new BasicDBObject();
		keys.put("PTP_ID", 1);
		keys.put("_id", 0);
		DBCursor alarm = conn.find(condition, keys);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			String ptp_dbo = dbo.get("PTP_ID").toString();
			for(Map<String, Object> ptp : ptpList) {
				if(ptp.get("PTP_ID") != null && ptp_dbo.equals(ptp.get("PTP_ID").toString())) {
					if(ptp.get("RESOURCE_CABLE_ID") != null) {
						String cableId = ptp.get("RESOURCE_CABLE_ID").toString();
						if(map.get(cableId) == null) {
							map.put(cableId, CommonDefine.LINE_WITH_ALARM);
						}
					}
				}
			}
		}
		//更新临时表
		refreshTempAlarmForGis(map, CommonDefine.Gis.ALARM_CABLE_GIS);
		
		return map;
	}
	
	public Map<String, Integer> getStationAlarmState(List<String> stationIdList) throws CommonException {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		List<Integer> ids = new ArrayList<Integer>();
		for(String id : stationIdList) {
			ids.add(Integer.parseInt(id));
		}
		
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).
				getCollection(CommonDefine.T_CURRENT_ALARM);
		BasicDBObject condition = new BasicDBObject ();
		condition.put("STATION_ID",  new BasicDBObject("$in", ids));
		BasicDBObject keys = new BasicDBObject();
		keys.put("STATION_ID", 1);
		keys.put("PERCEIVED_SEVERITY", 1);
		keys.put("_id", 0);
		
		DBCursor alarm = conn.find(condition, keys);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			if(dbo.get("STATION_ID") != null) {
				if(dbo.get("PERCEIVED_SEVERITY") != null) {
					int severity_dbo = Integer.parseInt(dbo.get("PERCEIVED_SEVERITY").toString());
					if(severity_dbo >= CommonDefine.ALARM_PS_CRITICAL && 
							severity_dbo <= CommonDefine.ALARM_PS_WARNING) {
						if(map.get(dbo.get("STATION_ID").toString()) != null) {
							int severity_map = map.get(dbo.get("STATION_ID").toString());
							if(severity_dbo < severity_map) {
								map.put(dbo.get("STATION_ID").toString(), severity_dbo);
							}
						}else {
							map.put(dbo.get("STATION_ID").toString(), (Integer)dbo.get("PERCEIVED_SEVERITY"));
						}
					}
				}
			}
		}
		//更新临时表
		refreshTempAlarmForGis(map, CommonDefine.Gis.ALARM_STATION_GIS);
		
		return map;
	}
	
	private void refreshTempAlarmForGis(Map<String, Integer> map, int type) {
		
		List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
		if(type == CommonDefine.Gis.ALARM_STATION_GIS) {
			for(Map.Entry<String, Integer> entry : map.entrySet()) {
				Map<String, Integer> station = new HashMap<String, Integer>();
				station.put("id", Integer.parseInt(entry.getKey()));
				station.put("type", CommonDefine.Gis.ALARM_STATION_GIS);
				station.put("severity", entry.getValue());
				list.add(station);
			}
		} else if(type == CommonDefine.Gis.ALARM_CABLE_GIS) {
			for(Map.Entry<String, Integer> entry : map.entrySet()) {
				Map<String, Integer> cable = new HashMap<String, Integer>();
				cable.put("id", Integer.parseInt(entry.getKey()));
				cable.put("type", CommonDefine.Gis.ALARM_CABLE_GIS);
				cable.put("severity", CommonDefine.ALARM_PS_CRITICAL);
				list.add(cable);
			}
		}
		
		if(list.size() > 0) {
			alarmManagementMapper.deleteTempAlarmListForGis(list);
			alarmManagementMapper.addTempAlarmListForGis(list);
		}
	}
	
	private int getStationSeverity(int stationId) {
		
		int severity = CommonDefine.ALARM_PS_CLEARED;
		
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).
				getCollection(CommonDefine.T_CURRENT_ALARM);
		BasicDBObject condition = new BasicDBObject ();
		condition.put("STATION_ID",  stationId);
		condition.put("PERCEIVED_SEVERITY", new BasicDBObject("$gt", CommonDefine.ALARM_PS_INDETERMINATE));
		condition.put("PERCEIVED_SEVERITY", new BasicDBObject("$lt", CommonDefine.ALARM_PS_CLEARED));
		
		BasicDBObject keys = new BasicDBObject();
		keys.put("PERCEIVED_SEVERITY", 1);
		keys.put("_id", 0);
		BasicDBObject sort = new BasicDBObject ();
		sort.put("PERCEIVED_SEVERITY", 1);
		
		DBCursor alarm = conn.find(condition, keys).sort(sort).limit(1);
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			if(dbo.get("PERCEIVED_SEVERITY") != null) {
				severity = Integer.parseInt(dbo.get("PERCEIVED_SEVERITY").toString());
			}
		}
		
		return severity;
	}
	
	@SuppressWarnings("rawtypes")
	public void analyseStationAlarmForGis(Map alarm) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		boolean push = false;
		
		if(alarm.get("STATION_ID") != null) {
			int stationId = Integer.parseInt(alarm.get("STATION_ID").toString());
			int severityMysql = CommonDefine.Gis.NO_ALARM;
			Map<String,Object> staMap =commonManagerMapper.selectTableById("t_resource_station", "RESOURCE_STATION_ID", stationId);
			int type = staMap.get("TYPE")!=null?Integer.valueOf(staMap.get("TYPE").toString()):0;
			Map<String, Object> tempAlarm = alarmManagementMapper.
					getTempAlarmForGis(CommonDefine.Gis.ALARM_STATION_GIS, stationId);
			if(tempAlarm != null) {
				severityMysql = Integer.parseInt(tempAlarm.get("SEVERITY").toString());
			}
			int severityMongo = getStationSeverity(stationId);
			
			if(severityMongo == CommonDefine.Gis.NO_ALARM) {
				if(severityMysql != CommonDefine.Gis.NO_ALARM) {
					alarmManagementMapper.deleteTempAlarmForGis(
							CommonDefine.Gis.ALARM_STATION_GIS, stationId);
					//更新地图
					map.put("dataType", CommonDefine.Gis.ALARM_STATION_GIS);
					map.put("id", stationId);
					map.put("state", CommonDefine.Gis.NO_ALARM);
					map.put("type",type);
					push = true;
				}
			} else {
				if(severityMysql == CommonDefine.Gis.NO_ALARM) {
					alarmManagementMapper.addTempAlarmForGis(CommonDefine.Gis.ALARM_STATION_GIS, 
							stationId, severityMongo);
					//更新地图
					map.put("dataType", CommonDefine.Gis.ALARM_STATION_GIS);
					map.put("id", stationId);
					map.put("state", severityMongo);
					map.put("type",type);
					push = true;
				} else {
					if(severityMysql != severityMongo) {
						alarmManagementMapper.updateTempAlarmForGis(CommonDefine.Gis.ALARM_STATION_GIS, 
								stationId, severityMongo);
						//更新地图
						map.put("dataType", CommonDefine.Gis.ALARM_STATION_GIS);
						map.put("id", stationId);
						map.put("state", severityMongo);
						map.put("type",type);
						push = true;
					}
				}
			}
		}
		
		if(push) {
			WebMsgPush.updateGisMap(JSONObject.fromObject(map));
		}
	}
	
	private int getCableSeveirty(int cableId) {
		
		int severity = CommonDefine.Gis.NO_ALARM;
		List<String> params = new ArrayList<String>();
		params.add(String.valueOf(cableId));
		List<Map<String, Object>> ptpList = alarmManagementMapper.getPtpListByCableIds(params);
		List<Integer> ptpIds = new ArrayList<Integer>();
		for(Map<String, Object> ptp : ptpList) {
			ptpIds.add(Integer.valueOf(ptp.get("PTP_ID").toString()));
		}
		
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).
				getCollection(CommonDefine.T_CURRENT_ALARM);
		BasicDBObject condition = new BasicDBObject ();
		condition.put("PTP_ID",  new BasicDBObject("$in", ptpIds.toArray()));
		Pattern pattern = Pattern.compile("los?", Pattern.CASE_INSENSITIVE);
		condition.put("NATIVE_PROBABLE_CAUSE", pattern);
//		Pattern pattern = Pattern.compile("^.*los.*$");
//		condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$regex", pattern));
		BasicDBObject keys = new BasicDBObject();
		keys.put("IS_CLEAR", 1);
		keys.put("_id", 0);
		DBCursor alarm = conn.find(condition, keys);
		if (alarm.hasNext()) {
			DBObject alm = alarm.next();
			int clear = Integer.valueOf(alm.get("IS_CLEAR").toString());
			if (clear == 2) {
				severity = CommonDefine.ALARM_PS_CRITICAL;				
			}
		}
		
		return severity;
	}
	
	@SuppressWarnings("rawtypes")
	public void analyseCableAlarmForGis(Map alarm) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		boolean push = false;
		
		if(alarm.get("PTP_ID") != null && !"".equals(alarm.get("PTP_ID"))) {
			int ptpId = Integer.parseInt(alarm.get("PTP_ID").toString());
			List<Integer> cableIds = alarmManagementMapper.getCableIdByPtpId(ptpId);
			if(cableIds != null && cableIds.size() > 0) {
				for(Integer cableId : cableIds) {
					Map<String, Object> tempAlarm = alarmManagementMapper.
							getTempAlarmForGis(CommonDefine.Gis.ALARM_CABLE_GIS, cableId);
					int severityMysql = CommonDefine.Gis.NO_ALARM;
					if(tempAlarm != null) {
						severityMysql = Integer.parseInt(tempAlarm.get("SEVERITY").toString());
					}
					int severityMongo = getCableSeveirty(cableId);
					if(severityMongo == CommonDefine.Gis.NO_ALARM) {
						if(severityMysql == CommonDefine.ALARM_PS_CRITICAL) {
							alarmManagementMapper.deleteTempAlarmForGis(
									CommonDefine.Gis.ALARM_CABLE_GIS, cableId);
							//更新地图
							map.put("dataType", CommonDefine.Gis.ALARM_CABLE_GIS);
							map.put("id", cableId);
							map.put("state", CommonDefine.LINE_ORDINARY);
							push = true;
						}
					} else {
						if(severityMysql == CommonDefine.Gis.NO_ALARM) {
							alarmManagementMapper.addTempAlarmForGis(CommonDefine.Gis.ALARM_CABLE_GIS, 
									cableId, CommonDefine.ALARM_PS_CRITICAL);
							//更新地图
							map.put("dataType", CommonDefine.Gis.ALARM_CABLE_GIS);
							map.put("id", cableId);
							map.put("state", CommonDefine.LINE_WITH_ALARM);
							push = true;
						}
					}
				}
			}
		}
		
		if(push) {
			WebMsgPush.updateGisMap(JSONObject.fromObject(map));
		}
	}
}
