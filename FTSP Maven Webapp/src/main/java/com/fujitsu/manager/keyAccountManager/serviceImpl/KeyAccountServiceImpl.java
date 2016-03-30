package com.fujitsu.manager.keyAccountManager.serviceImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.KeyAccountMapper;
import com.fujitsu.dao.mysql.bean.Contact;
import com.fujitsu.manager.keyAccountManager.service.KeyAccountService;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Service
@Transactional(rollbackFor = Exception.class)
public class KeyAccountServiceImpl extends KeyAccountService {
	@Autowired
	private Mongo mongo;
	@Resource
	private KeyAccountMapper kAMap;
	@Resource
	public IAlarmManagementService alarmManagementService;

	@Override
	public List<Contact> selectContact(Integer userId) throws CommonException {
		return selectContact(null, null);
	}

	@Override
	public List<Contact> selectContact(Integer start, Integer limit)
			throws CommonException {
		List<Contact> list = new ArrayList<Contact>();
		try {
			// 检查参数
			/*
			 * if(userId == null){ throw new Exception(); }
			 */if (start != null && limit != null) {
				if (start < 0 || limit < 0) {
					throw new Exception();
				}
			}
			if ((start == null & limit != null)
					|| (start != null & limit == null)) {
				throw new Exception();
			}
			// 参数异常检查结束,开始查询数据库
			list = kAMap.selectContact(start, limit);
		} catch (SQLException e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}

		return list;
	}

	@Override
	public Map<String, Object> getContactInfo(Integer userId)
			throws CommonException {
		return getContactInfo(null, null);
	}

	@Override
	public Map<String, Object> getContactInfo(Integer start, Integer limit)
			throws CommonException {
		// 获取联系人信息
		List<Contact> contacts = this.selectContact(start, limit);
		JSONArray array = JSONArray.fromObject(contacts);
		// 获取联系人总数
		int total = kAMap.selectContactTotal();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", total);
		map.put("rows", array);
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IKeyAccountManagerService#getVIPInfo(java.lang.Integer
	 * , java.lang.Integer)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> getVIPInfo(Integer start, Integer limit)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 检查参数
			// if(start!=null&&limit!=null){
			// if(start <0 || limit< 0){
			// throw new Exception();
			// }
			// }else if((start == null&&limit !=null)||(start != null&&limit
			// ==null)){
			// throw new Exception();
			// }
			// 获取大客户总数
			int total = kAMap.getVIPInfoCount();

			List<Map> vipInfo = kAMap.getVIPInfo();
			for (int i = 0, len = vipInfo.size(); i < len; i++) {
				Map vip = vipInfo.get(i);
				// 该大客户相关的告警总数
				Integer alarmCount = 0;
				// 该大客户最高告警等级,初始值为6表示没有告警
				int alarmLevel = 6;
				String clientName = vip.get("CLIENT_NAME").toString();
				List<Map> circuitInfo = kAMap.getCircuitInfo(clientName, null,
						null);
				for (int j = 0, len1 = circuitInfo.size(); j < len1; j++) {
					Map circuit = circuitInfo.get(j);
					Map<String, Object> alarmMap = new HashMap<String, Object>();
					if (circuit.get("CIRCUIT_TYPE").equals("1")) {
						alarmMap = alarmManagementService
								.getCurrentAlarmForCircuit((Integer) circuit
										.get("CIR_CIRCUIT_INFO_ID"), 1, -1, -1,
										false, true, false, true, true);
					} else if (circuit.get("CIRCUIT_TYPE").equals("2")) {
						alarmMap = alarmManagementService
								.getCurrentAlarmForCircuit((Integer) circuit
										.get("CIR_CIRCUIT_INFO_ID"), 2, -1, -1,
										false, true, false, true, true);

					}
					alarmCount += (Integer) alarmMap.get("total");

					List alarmList = (List) alarmMap.get("rows");

					for (int k = 0, len2 = alarmList.size(); k < len2; k++) {
						Map alarm = (Map) alarmList.get(k);
						if (Integer.parseInt(alarm.get("IS_CLEAR").toString()) == CommonDefine.IS_CLEAR_YES) {
							if (alarmLevel == 6)
								alarmLevel = 5;
						}
						int level = Integer.parseInt(alarm.get(
								"PERCEIVED_SEVERITY").toString());
						if (level == CommonDefine.ALARM_PS_CRITICAL) {
							alarmLevel = 1;
							break;
						} else if (level == CommonDefine.ALARM_PS_MAJOR) {
							if (alarmLevel == CommonDefine.ALARM_PS_CRITICAL)
								break;
							else if (level <= alarmLevel)
								alarmLevel = level;
						} else if (level == CommonDefine.ALARM_PS_MINOR) {
							if (alarmLevel == CommonDefine.ALARM_PS_CRITICAL)
								break;
							else if (level <= alarmLevel)
								alarmLevel = level;
						} else if (level == CommonDefine.ALARM_PS_WARNING) {
							if (alarmLevel == CommonDefine.ALARM_PS_CRITICAL)
								break;
							else if (level <= alarmLevel)
								alarmLevel = level;
						}
					}
				}
				vip.put("ALARM_COUNT", alarmCount);
				vip.put("ALARM_LEVEL", alarmLevel);
			}

			map.put("total", total);
			map.put("rows", vipInfo);

		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IKeyAccountManagerService#getVIPInfoWithoutAlarmInfo(java.lang.Integer, java.lang.Integer)
	 */
	public Map<String, Object> getVIPInfoWithoutAlarmInfo(Integer start, Integer limit)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map> vipInfo = kAMap.getVIPInfo();
			map.put("rows", vipInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return map;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IKeyAccountManagerService#getCircuitsByVIPName(java
	 * .lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getCircuitsByVIPName(String clientName,
			Integer start, Integer limit) throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
//			// 检查参数
//			if (start != null && limit != null) {
//				if (start < 0 || limit < 0) {
//					throw new Exception();
//				}
//			} else if ((start == null && limit != null)
//					|| (start != null && limit == null)) {
//				throw new Exception();
//			}
			// 获取电路总数
			int total = kAMap.getCircuitInfoCount(clientName);
			List<Map> circuitInfo = kAMap.getCircuitInfo(clientName, start,
					limit);
			for (int i = 0, len = circuitInfo.size(); i < len; i++) {
				Map circuit = circuitInfo.get(i);
				if (circuit.get("SVC_TYPE").toString().equals("1"))
					circuit.put("SVC_TYPE", "SDH");
				else if (circuit.get("SVC_TYPE").toString().equals("2"))
					circuit.put("SVC_TYPE", "以太网");
				else
					circuit.put("SVC_TYPE", "OTN");
				Map<String, Object> alarmMap = new HashMap<String, Object>();
				if (circuit.get("CIRCUIT_TYPE").equals("1")) {
					alarmMap = alarmManagementService
							.getCurrentAlarmForCircuit((Integer) circuit
									.get("CIR_CIRCUIT_INFO_ID"), 1, -1, -1,
									false, true, false, true, true);
				} else if (circuit.get("CIRCUIT_TYPE").equals("2")) {
					alarmMap = alarmManagementService
							.getCurrentAlarmForCircuit((Integer) circuit
									.get("CIR_CIRCUIT_INFO_ID"), 2, -1, -1,
									false, true, false, true, true);

				}
				List alarmList = (List) alarmMap.get("rows");
				int alarmLevel = 6;
				for (int j = 0, len1 = alarmList.size(); j < len1; j++) {
					Map alarm = (Map) alarmList.get(j);
					if (Integer.parseInt(alarm.get("IS_CLEAR").toString()) == CommonDefine.IS_CLEAR_YES) {
						if (alarmLevel == 6)
							alarmLevel = 5;
					}
					int level = Integer.parseInt(alarm
							.get("PERCEIVED_SEVERITY").toString());
					if (level == CommonDefine.ALARM_PS_CRITICAL) {
						alarmLevel = 1;
						break;
					} else if (level == CommonDefine.ALARM_PS_MAJOR) {
						if (alarmLevel == CommonDefine.ALARM_PS_CRITICAL)
							break;
						else if (level <= alarmLevel)
							alarmLevel = level;
					} else if (level == CommonDefine.ALARM_PS_MINOR) {
						if (alarmLevel == CommonDefine.ALARM_PS_CRITICAL)
							break;
						else if (level <= alarmLevel)
							alarmLevel = level;
					} else if (level == CommonDefine.ALARM_PS_WARNING) {
						if (alarmLevel == CommonDefine.ALARM_PS_CRITICAL)
							break;
						else if (level <= alarmLevel)
							alarmLevel = level;
					}
				}
				circuit.put("ALARM_LEVEL", alarmLevel);
				Map tcaQueryCondition = new HashMap();
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				Date time = cal.getTime();
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				String timeString = timeFormat.format(time);
				tcaQueryCondition.put("oneDayAgoTime", timeString);
				int tcaCount = 0;
				if (circuit.get("CIRCUIT_TYPE").equals("1")) {
					List<Integer> ptpIdList = queryPtpInCircuit((Integer) circuit
							.get("CIR_CIRCUIT_INFO_ID"));
					List<Integer> ctpIdList = queryCtpInCircuit((Integer) circuit
							.get("CIR_CIRCUIT_INFO_ID"));
					ptpIdList.add(0);
					ctpIdList.add(0);
					tcaQueryCondition.put("circuitType", 1);
					tcaQueryCondition.put("ptpIdList",
							ptpIdList);
					tcaQueryCondition.put("ctpIdList",
							ctpIdList);
					tcaCount = kAMap.selectTcaCount(tcaQueryCondition);
				} else {
					List<Integer> ptpIdList = queryPtpInOtnCircuit((Integer) circuit
							.get("CIR_CIRCUIT_INFO_ID"));
					List<Integer> ctpIdList = queryCtpInOtnCircuit((Integer) circuit
							.get("CIR_CIRCUIT_INFO_ID"));
					ptpIdList.add(0);
					ctpIdList.add(0);
					tcaQueryCondition.put("circuitType", 2);
					tcaQueryCondition.put("ptpIdList",
							ptpIdList);
					tcaQueryCondition.put("ctpIdList",
							ctpIdList);
					tcaCount = kAMap.selectTcaCount(tcaQueryCondition);
				}
				circuit.put("ALARM_TCA", tcaCount);
				if (alarmLevel != 6 || tcaCount != 0) {
					circuit.put("CIRCUIT_STATUS", 0);
				} else {
					circuit.put("CIRCUIT_STATUS", 1);
				}

				// circuit.put("CIRCUIT_STATUS", value);
				// circuit.put("ALARM_TCA", value);
				// circuit.put("ALARM_LEVEL", value);
			}
			map.put("total", total);
			map.put("rows", circuitInfo);

		} catch (SQLException e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IKeyAccountManagerService#getCircuitsByVIPNameWithoutAlarmInfo(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	public Map<String, Object> getCircuitsByVIPNameWithoutAlarmInfo(String clientName,
			Integer start, Integer limit) throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			// 获取电路总数
			int total = kAMap.getCircuitInfoCount(clientName);
			List<Map> circuitInfo = kAMap.getCircuitInfo(clientName, start,
					limit);
			map.put("total", total);
			map.put("rows", circuitInfo);

		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return map;
	}
	/**
	 * 根据大客户名称查询不同速率电路的条数
	 * 
	 * @param clientName
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getGroupedCircuitsByVIPName(String clientName)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map> groupedCircuitList = kAMap
					.getGroupedCircuitsByVIPName(clientName);
			map.put("rows", groupedCircuitList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		}
		// Integer otnCircuitCount = kAMap.getOtnCircuitCount(clientName);
		return map;
	}

	/**
	 * 查询大客户相关的割接任务及相关信息
	 * 
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public Map<String, Object> getCutoverInfoByVIPName() throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 定义时间格式转换器
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Map> vipWithCircuitInfo = kAMap.searchVIPWithCircuitInfo();

			List<Map> cutoverTaskWithCircuitInfo = kAMap
					.searchCutoverTaskWithCircuitInfo();
			for (int i = vipWithCircuitInfo.size() - 1; i >= 0; i--) {
				Map vipCircuit = vipWithCircuitInfo.get(i);
				Integer cirInfoId = (Integer) vipCircuit
						.get("CIR_CIRCUIT_INFO_ID");
				String cirType = vipCircuit.get("CIR_TYPE").toString();
				// Integer svcType = (Integer)vipCircuit.get("SVC_TYPE");
				boolean hasSameCircuit = false;
				for (int j = 0,len1 = cutoverTaskWithCircuitInfo.size(); j < len1; j++) {
					Map cutoverCircuit = cutoverTaskWithCircuitInfo.get(j);
					Integer cirInfoIdCutover = (Integer) cutoverCircuit
							.get("CIR_CIRCUIT_INFO_ID");
					String cirTypeCutover = cutoverCircuit.get("CIRCUIT_TYPE")
							.toString();
					if (cirInfoIdCutover.compareTo(cirInfoId) == 0
							&& cirTypeCutover.equals(cirType)) {
						vipCircuit.put("TASK_NAME",
								cutoverCircuit.get("TASK_NAME"));
						vipCircuit.put("START_TIME_ESTIMATE",
								cutoverCircuit.get("START_TIME_ESTIMATE"));
						vipCircuit.put("END_TIME_ESTIMATE",
								cutoverCircuit.get("END_TIME_ESTIMATE"));
						hasSameCircuit = true;
						break;
					} else {
						
					}

				}
				if(!hasSameCircuit)
				{
					vipWithCircuitInfo.remove(i);
				}
			}
			// 循环之后剩下的为所有电路，带大客户名称，电路类型，割接任务名称，割接任务起止时间
			List<Map> resultMapList = new ArrayList<Map>();
			for (int i = vipWithCircuitInfo.size() - 1; i >= 0; i--) {
				Map vipCircuit = vipWithCircuitInfo.get(i);
				if (resultMapList.size() == 0) {
					Map resultMap = new HashMap();
					StringBuilder sb = new StringBuilder();
					sb.append(vipCircuit.get("CLIENT_NAME").toString())
							.append(",")
							.append(vipCircuit.get("TASK_NAME").toString());
					String clientNamePlusCutoverName = sb.toString();
					resultMap.put(
							"clientNamePlusCutoverName",
							clientNamePlusCutoverName);
					if (1 == Integer.valueOf(vipCircuit.get("SVC_TYPE")
							.toString()))
						resultMap.put("sdhCount", 1);
					else if (2 == Integer.valueOf(vipCircuit.get("SVC_TYPE")
							.toString()))
						resultMap.put("ethCount", 1);
					else
						resultMap.put("otnCount", 1);
					resultMap.put("startTime",
							vipCircuit.get("START_TIME_ESTIMATE"));
					resultMap.put("endTime",
							vipCircuit.get("END_TIME_ESTIMATE"));
					Date startTime = sf.parse(vipCircuit.get(
							"START_TIME_ESTIMATE").toString());
					Date endTime = sf.parse(vipCircuit.get("END_TIME_ESTIMATE")
							.toString());
					Long timeDifferenceInMill = endTime.getTime()
							- startTime.getTime();
					BigDecimal timeDifference = new BigDecimal(
							timeDifferenceInMill).divide(
							new BigDecimal(3600000), 2,
							BigDecimal.ROUND_HALF_UP);
					resultMap.put("timeDifference", timeDifference);
					resultMapList.add(resultMap);
				} else {
					boolean alreadyInList = false;
					for (int j = 0, len = resultMapList.size(); j < len; j++) {
						Map resultMap = resultMapList.get(j);
						StringBuilder sb = new StringBuilder();
						sb.append(vipCircuit.get("CLIENT_NAME").toString())
								.append(",")
								.append(vipCircuit.get("TASK_NAME").toString());
						String clientNamePlusCutoverName = sb.toString();
						if (resultMap.get("clientNamePlusCutoverName").equals(clientNamePlusCutoverName)) {
							if (1 == Integer.valueOf(vipCircuit.get("SVC_TYPE")
									.toString())) {
								Integer sdhCountNow = (Integer) resultMap
										.get("sdhCount");
								resultMap.put("sdhCount", sdhCountNow + 1);
							}

							else if (2 == Integer.valueOf(vipCircuit.get(
									"SVC_TYPE").toString())) {
								Integer ethCountNow = (Integer) resultMap
										.get("ethCount");
								resultMap.put("ethCount", ethCountNow + 1);
							}

							else {
								Integer otnCountNow = (Integer) resultMap
										.get("otnCount");
								resultMap.put("otnCount", otnCountNow + 1);
							}
							alreadyInList = true;
							break;
						}
						// else
						// {
						// resultMap.put("clientNamePlusCutoverName",
						// vipCircuit.get("CLIENT_NAME")+","+vipCircuit.get("TASK_NAME"));
						// if(1==Integer.valueOf(vipCircuit.get("SVC_TYPE").toString()))
						// resultMap.put("sdhCount", 1);
						// else
						// if(2==Integer.valueOf(vipCircuit.get("SVC_TYPE").toString()))
						// resultMap.put("ethCount", 1);
						// else
						// resultMap.put("otnCount", 1);
						// resultMap.put("startTime",
						// vipCircuit.get("START_TIME_ESTIMATE"));
						// resultMap.put("endTime",
						// vipCircuit.get("END_TIME_ESTIMATE"));
						// resultMapList.add(resultMap);
						// }
					}
					if (!alreadyInList) {
						Map resultMap = new HashMap();
						StringBuilder sb = new StringBuilder();
						sb.append(vipCircuit.get("CLIENT_NAME").toString())
								.append(",")
								.append(vipCircuit.get("TASK_NAME").toString());
						String clientNamePlusCutoverName = sb.toString();
						resultMap.put("clientNamePlusCutoverName",
								clientNamePlusCutoverName);
						if (1 == Integer.valueOf(vipCircuit.get("SVC_TYPE")
								.toString()))
							resultMap.put("sdhCount", 1);
						else if (2 == Integer.valueOf(vipCircuit
								.get("SVC_TYPE").toString()))
							resultMap.put("ethCount", 1);
						else
							resultMap.put("otnCount", 1);
						resultMap.put("startTime",
								vipCircuit.get("START_TIME_ESTIMATE"));
						resultMap.put("endTime",
								vipCircuit.get("END_TIME_ESTIMATE"));
						Date startTime = sf.parse(vipCircuit.get(
								"START_TIME_ESTIMATE").toString());
						Date endTime = sf.parse(vipCircuit.get("END_TIME_ESTIMATE")
								.toString());
						Long timeDifferenceInMill = endTime.getTime()
								- startTime.getTime();
						BigDecimal timeDifference = new BigDecimal(
								timeDifferenceInMill).divide(
								new BigDecimal(3600000), 2,
								BigDecimal.ROUND_HALF_UP);
						resultMap.put("timeDifference", timeDifference);
						resultMapList.add(resultMap);
					}
				}
			}
			map.put("rows", resultMapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		}
		// Integer otnCircuitCount = kAMap.getOtnCircuitCount(clientName);
		return map;
	}

	/**
	 * 查询电路经过的ptpId
	 * 
	 * @param circuitInfoId
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private List<Integer> queryPtpInCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> ptpIdList = new ArrayList();
		ptpIdList = kAMap.queryPtpInCircuit(circuitInfoId);
		return ptpIdList;
	}

	/**
	 * @param circuitInfoId
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private List<Integer> queryCtpInCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> ctpIdList = new ArrayList();
		ctpIdList = kAMap.queryCtpInCircuit(circuitInfoId);
		return ctpIdList;
	}

	/**
	 * @param circuitInfoId
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private List<Integer> queryNeInCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> neIdList = new ArrayList();
		neIdList = kAMap.queryNeInCircuit(circuitInfoId);
		return neIdList;
	}

	/**
	 * @param circuitInfoId
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
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
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
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
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
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
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
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
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private List<Integer> queryEquipInOtnCircuit(Integer circuitInfoId)
			throws SQLException {
		List<Integer> equipIdList = new ArrayList();
		equipIdList = kAMap.queryEquipInOtnCircuit(circuitInfoId);
		return equipIdList;
	}
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private Map<String, Object> getCurrentAlarmForCircuit(
			List<Integer> neIdList, List<Integer> ptpIdList,
			List<Integer> equipIdList, boolean isConverge, int start, int limit) {
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(
					CommonDefine.T_CURRENT_ALARM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject();
		// 定义多字段or连接对象
		BasicDBList child = new BasicDBList();
		BasicDBList child1 = new BasicDBList();

		// 查询过滤器设置
		HttpServletRequest request = ServletActionContext.getRequest();
		// 封装过滤器条件
		// BasicDBObject conditionFilter =
		// getCurrentAlarmFilterCondition((Integer)request.getSession().getAttribute("SYS_USER_ID"));

		// 第一个or条件,仅包含PTP的告警（不含CTP告警）
		BasicDBObject childOne = new BasicDBObject();
		childOne.put("PTP_ID", new BasicDBObject("$in", ptpIdList));
		childOne.put("OBJECT_TYPE",
				CommonDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
		// 第二个or条件，仅包含PTP(含CTP)以外的所有告警
		BasicDBObject childTwo = new BasicDBObject();
		childTwo.put("NE_ID", new BasicDBObject("$in", neIdList));
		childTwo.put("PTP_ID", "");
		// 第三个or条件，仅包含PTP(含CTP)以外的所有告警
		BasicDBObject childThree = new BasicDBObject();
		childThree.put("UNIT_ID", new BasicDBObject("$in", equipIdList));
		childThree.put("OBJECT_TYPE", CommonDefine.ALARM_OBJECT_TYPE_EQUIPMENT);
		child.add(childOne);
		child.add(childTwo);
		child.add(childThree);
		// 将多字段or连接条件添加的总的查询条件
		// child1.add(conditionFilter);
		child1.add(new BasicDBObject("$or", child));
		// 指定告警反转状态
		//child1.add(new BasicDBObject("REVERSAL", false));
		// 告警收敛显示标志 conditionQuery.put("REVERSAL", false);
		if (isConverge) {
			BasicDBObject converge = new BasicDBObject();
			converge.put("CONVERGE_FLAG", new BasicDBObject("$ne",
					CommonDefine.ALARM_CONVERGE_DERIVATIVE_ALARM));
			child1.add(converge);
		}
		condition.put("$and", child1);
		// 告警总数
		int count = conn.find(condition).count();
		// 查询符合条件的告警数据
		// DBCursor alarm = conn.find(condition).skip(start).limit(limit);
		DBCursor alarm = conn.find(condition);
		// 因DBCursor对象无法转成JSON对象，所以在此先转成List对象
		List<DBObject> list = new ArrayList<DBObject>();
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			dbo.put("FIRST_TIME",
					"".equals(dbo.get("FIRST_TIME")) ? "" : sf.format(dbo
							.get("FIRST_TIME")));
			dbo.put("UPDATE_TIME",
					"".equals(dbo.get("UPDATE_TIME")) ? "" : sf.format(dbo
							.get("UPDATE_TIME")));
			dbo.put("CLEAR_TIME",
					"".equals(dbo.get("CLEAR_TIME")) ? "" : sf.format(dbo
							.get("CLEAR_TIME")));
			dbo.put("ACK_TIME",
					"".equals(dbo.get("ACK_TIME")) ? "" : sf.format(dbo
							.get("ACK_TIME")));
			dbo.put("NE_TIME",
					"".equals(dbo.get("NE_TIME")) ? "" : sf.format(dbo
							.get("NE_TIME")));
			dbo.put("EMS_TIME",
					"".equals(dbo.get("EMS_TIME")) ? "" : sf.format(dbo
							.get("EMS_TIME")));
			dbo.put("CREATE_TIME",
					"".equals(dbo.get("CREATE_TIME")) ? "" : sf.format(dbo
							.get("CREATE_TIME")));
			list.add(dbo);
		}
		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("total", count);
		valueMap.put("rows", list);
		return valueMap;
	}
}
