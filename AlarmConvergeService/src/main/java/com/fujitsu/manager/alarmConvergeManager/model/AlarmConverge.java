package com.fujitsu.manager.alarmConvergeManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.fujitsu.common.AlarmConvergeDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.dao.mysql.AlarmConvergeMapper;
import com.fujitsu.manager.alarmConvergeManager.model.AlarmConvergeAction;
import com.fujitsu.manager.alarmConvergeManager.model.AlarmConvergeCondition;
import com.fujitsu.util.BeanUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class AlarmConverge implements Runnable {

	private Mongo mongo = (Mongo) BeanUtil.getBean("mongo");
	private static DBCollection conn = null;

	private AlarmConvergeMapper alarmConvergeMapper = (AlarmConvergeMapper) BeanUtil
		.getBean("alarmConvergeMapper");

	// 告警收敛规则ID
	private int id;
	// 告警收敛规则名称
	private String name;
	// 告警收敛规则的启用状态
	private int useStatus;
	// 告警收敛的范围
	private List<Integer> scope = new ArrayList<Integer>();
	// 告警收敛的设备类型
	private List<String> equipmentNames = new ArrayList<String>();
	// 告警收敛规则的执行条件
	private List<AlarmConvergeCondition> conditions = new ArrayList<AlarmConvergeCondition>();
	// 告警收敛规则的执行动作
	private List<AlarmConvergeAction> actions = new ArrayList<AlarmConvergeAction>();
	// 告警收敛规则的调度结果
	private ScheduledFuture<?> schResult;
	// 告警收敛规则的运行状态
	private int operStatus = AlarmConvergeDefine.ALARM_CONVERGE_RUNTIME_IDLE;
	
	public AlarmConverge(Map<String, Object> paramMap) throws CommonException {
		id = (Integer) paramMap.get("CONVERGE_ID");
		name = paramMap.get("RULE_NAME").toString();
		useStatus = (Integer) paramMap.get("STATUS");
		String tableName = "t_alarm_converge_scope";
		String idName = "CONVERGE_ID";
		int idValue = id;
		
		// 初始化告警数据库链接
		if (conn == null) {
			conn = mongo.getDB(AlarmConvergeDefine.MONGODB_NAME).getCollection(AlarmConvergeDefine.T_CURRENT_ALARM);
		}

		// 获取告警收敛范围
		List<Map<String, Object>> scopeList = alarmConvergeMapper.selectTableListById(tableName, idName, idValue);
		if (scopeList != null && scopeList.size() > 0) {
			for (Map<String, Object> item : scopeList) {
				int emsId = (Integer) item.get("EMS_ID");
				scope.add(emsId);
			}
		}
		// 获取告警收敛设备
		tableName = "t_alarm_converge_equipment";
		List<Map<String, Object>> eqptList = alarmConvergeMapper.selectTableListById(tableName, idName, idValue);
		if (eqptList != null && eqptList.size() > 0) {
			for (Map<String, Object> item : eqptList) {
				String productName = item.get("PRODUCT_NAME").toString();
				equipmentNames.add(productName);
			}
		}
		// 获取告警收敛条件
		tableName = "t_alarm_converge_condition";
		List<Map<String, Object>> condList = alarmConvergeMapper.selectTableListById(tableName, idName, idValue);
		if (condList != null && condList.size() > 0) {
			for (Map<String, Object> item : condList) {
				AlarmConvergeCondition cond = new AlarmConvergeCondition(item);
				conditions.add(cond);
			}
		}
		// 获取告警收敛执行动作
		tableName = "t_alarm_converge_action";
		List<Map<String, Object>> actionList = alarmConvergeMapper.selectTableListById(tableName, idName, idValue);
		if (actionList != null && actionList.size() > 0) {
			for (Map<String, Object> item : actionList) {
				AlarmConvergeAction action = new AlarmConvergeAction(item);
				actions.add(action);
			}
		}
	}
	
	public int getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String value) {
		this.name = value;
	}
	public int getUseStatus() {
		return this.useStatus;
	}
	public void setUseStatus(int value) {
		this.useStatus = value;
	}
	public List<Integer> getScope() {
		return this.scope;
	}
	public void setScope(List<Integer> value) {
		this.scope = value;
	}
	public List<String> getEquipmentNames() {
		return this.equipmentNames;
	}
	public void setEquipment(List<String> value) {
		this.equipmentNames = value;
	}
	public List<AlarmConvergeCondition> getConditions() {
		return this.conditions;
	}
	public void setConditions(List<AlarmConvergeCondition> value) {
		this.conditions = value;
	}
	public List<AlarmConvergeAction> getActions() {
		return this.actions;
	}
	public void setActions(List<AlarmConvergeAction> value) {
		this.actions = value;
	}
	public ScheduledFuture<?> getSchResult() {
		return this.schResult;
	}
	public void setSchResult(ScheduledFuture<?> value) {
		this.schResult = value;
	}
	public int getOperStatus() {
		return this.operStatus;
	}
	
	// 告警收敛规则的执行部分
	@Override
	public void run() {
		// 告警收敛处理
		try {
			if (useStatus == AlarmConvergeDefine.ALARM_CONVERGE_DISABLE)
				return;
			operStatus = AlarmConvergeDefine.ALARM_CONVERGE_RUNTIME_RUNNING;
			System.out.println("【"+name+"@"+id+"】开始执行。");
			// 组织查询条件
			BasicDBObject condition = getQueryCondition();
			System.out.println("【"+name+"@"+id+"】主告查询条件："+condition);
			int count = conn.find(condition).count();
			System.out.println("【"+name+"@"+id+"】符合条件的主告警："+count);
			if (count > 0) {
				// 更新告警收敛标志为主告警
				conn.update(condition, new BasicDBObject("$set", new BasicDBObject("CONVERGE_FLAG",
						AlarmConvergeDefine.ALARM_CONVERGE_MAIN_ALARM)));
				// 组织主告警列表
				List<DBObject> almList = conn.find(condition).toArray();

				// 按告警对象分类处理
				switch (conditions.get(0).getAlarmObjectType()) {
				case AlarmConvergeDefine.ALARM_OBJECT_TYPE_MANAGED_ELEMENT:
					// 网元对象告警收敛处理
					alarmConvergeForNE(almList);
					break;
				case AlarmConvergeDefine.ALARM_OBJECT_TYPE_EQUIPMENT:
					// 板卡对象告警收敛处理
					alarmConvergeForUnit(almList);
					break;
				case AlarmConvergeDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT:
					// 端口对象告警收敛处理
					alarmConvergeForPtp(almList);
					break;
				case AlarmConvergeDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT:
					// 通道对象告警收敛处理
					alarmConvergeForCtp(almList);
					break;
					default:
				}
			}
			operStatus = AlarmConvergeDefine.ALARM_CONVERGE_RUNTIME_IDLE;
			System.out.println("【"+name+"@"+id+"】结束执行。");
			
		} catch (Exception e) {
			this.operStatus = AlarmConvergeDefine.ALARM_CONVERGE_RUNTIME_ERROR;
			e.printStackTrace();
		}
	}
	
	private void alarmConvergeForNE(List<DBObject> almList) {
		// 循环处理所有收敛动作
		for (AlarmConvergeAction act : actions) {
			// 判定收敛动作的使能状态
			if (act.getActionStatus() == AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE) {
				// 根据告警对象类型分类处理
				switch (act.getActionType()) {
				
				// 对象本身（主告警网元：NE对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_OBJ_SELF:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						BasicDBObject condition = new BasicDBObject();
						// 隐含条件：告警收敛只针对未清除告警
						condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
						condition.put("NE_ID", neId);
						condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_MANAGED_ELEMENT);
						condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$ne",alm.get("NATIVE_PROBABLE_CAUSE")));
						if (!act.getAlarms().isEmpty()) {
							if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
								condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
							}
						}
						// 处理待收敛告警
						processConvergeResult(id, condition);
					}
					break;
					
				// 对象的下属对象（主告警网元以下对象：EQUIPMENT、PTP、CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_UNDERLING:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						BasicDBObject condition = new BasicDBObject();
						// 隐含条件：告警收敛只针对未清除告警
						condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
						condition.put("NE_ID", neId);
						Integer[] objArray = new Integer[]{ AlarmConvergeDefine.ALARM_OBJECT_TYPE_EQUIPMENT,
								AlarmConvergeDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT,
								AlarmConvergeDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT }; 
						condition.put("OBJECT_TYPE", new BasicDBObject("$in", objArray));
						if (!act.getAlarms().isEmpty()) {
							if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
								condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
							}
						}
						// 处理待收敛告警
						processConvergeResult(id, condition);
					}
					break;
					
				// 对象的相邻端口（主告警网元相邻网元的端口：PTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_ADJACENT_PORT:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						// 获取相邻网元的端口
						List<Map<String, Object>> ptps = alarmConvergeMapper.getLinkPtpByNeId(neId);
						
						if (ptps != null && !ptps.isEmpty()) {
							List<Integer> ptpList = new ArrayList<Integer>();
							for (Map<String, Object> link : ptps) {
								int ptpId = (Integer) link.get("PTP_ID");
								ptpList.add(ptpId);
							}
							// 相邻网元端口不为空时处理告警收敛
							if (!ptpList.isEmpty()) {
								BasicDBObject condition = new BasicDBObject();
								// 隐含条件：告警收敛只针对未清除告警
								condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
								condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
								condition.put("PTP_ID", new BasicDBObject("$in", ptpList.toArray()));
								if (!act.getAlarms().isEmpty()) {
									if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
										condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
									}
								}
								// 处理待收敛告警
								processConvergeResult(id, condition);
							}
						}
					}
					break;
					
				// 对象所属传输系统网元（传输系统内网元，无告警对象类型限制）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_TRANSMISSION_SYS:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						// 获取所属传输系统网元
						List<Map<String, Object>> list = alarmConvergeMapper.getNeListFromTransSysByNeId(neId);
						if (list != null && !list.isEmpty()) {
							int[] neArray = new int[list.size()];
							for (int i=0; i<neArray.length; i++) {
								neArray[i] = (Integer) list.get(i).get("NE_ID");
							}
							BasicDBObject condition = new BasicDBObject();
							// 隐含条件：告警收敛只针对未清除告警
							condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
							condition.put("NE_ID", new BasicDBObject("$in", neArray));
							if (!act.getAlarms().isEmpty()) {
								if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
									condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
								}
							}
							// 处理待收敛告警
							processConvergeResult(id, condition);							
						}
					}
					break;
					
				// 对象相关电路通道（电路相关性查询，取得的所有通道：CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_CIRCUIT:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						int neType = (Integer) alm.get("NE_TYPE");
						if (neType == DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG ||
								neType == DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG) {
							// 按网元获取电路相关CTP
							List<Map<String, Object>>list = alarmConvergeMapper.getCtpFromCircuit(neId,null,null,null,neType);
							if (list != null && !list.isEmpty()) {
								int[] ctpArray = new int[list.size()];
								for (int i=0; i<ctpArray.length; i++) {
									ctpArray[i] = (Integer) list.get(i).get("CTP_ID");
								}
								BasicDBObject condition = new BasicDBObject();
								// 隐含条件：告警收敛只针对未清除告警
								condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
								condition.put("NE_ID", new BasicDBObject("$ne", neId));
								condition.put("CTP_ID", new BasicDBObject("$in", ctpArray));
								if (!act.getAlarms().isEmpty()) {
									if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
										condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
									}
								}
								// 处理待收敛告警
								processConvergeResult(id, condition);
							}
						}
					}
					break;
					default:
				}
			}
		}
	}
	
	private void alarmConvergeForUnit(List<DBObject> almList) {
		// 循环处理所有收敛动作
		for (AlarmConvergeAction act : actions) {
			// 判定收敛动作的使能状态
			if (act.getActionStatus() == AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE) {
				// 根据告警对象类型分类处理
				switch (act.getActionType()) {
				
				// 对象本身（主告警板卡：EQUIPMENT对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_OBJ_SELF:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int unitId = (Integer) alm.get("UNIT_ID");
						int id = (Integer) alm.get("_id");
						BasicDBObject condition = new BasicDBObject();
						// 隐含条件：告警收敛只针对未清除告警
						condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
						condition.put("UNIT_ID", unitId);
						condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_EQUIPMENT);
						condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$ne",alm.get("NATIVE_PROBABLE_CAUSE")));
						if (!act.getAlarms().isEmpty()) {
							if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
								condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
							}
						}
						// 处理待收敛告警
						processConvergeResult(id, condition);
					}
					break;
					
				// 对象的下属对象（主告警板卡以下的对象：PTP、CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_UNDERLING:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int unitId = (Integer) alm.get("UNIT_ID");
						int id = (Integer) alm.get("_id");
						BasicDBObject condition = new BasicDBObject();
						// 隐含条件：告警收敛只针对未清除告警
						condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
						condition.put("UNIT_ID", unitId);
						Integer[] objArray = new Integer[]{	AlarmConvergeDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT,
								AlarmConvergeDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT}; 
						condition.put("OBJECT_TYPE", new BasicDBObject("$in", objArray));
						if (!act.getAlarms().isEmpty()) {
							if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
								condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
							}
						}
						// 处理待收敛告警
						processConvergeResult(id, condition);
					}
					break;
					
				// 对象的相邻端口（主告警板卡，相邻网元端口：PTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_ADJACENT_PORT:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int unitId = (Integer) alm.get("UNIT_ID");
						int id = (Integer) alm.get("_id");
						// 获取相邻网元的端口
						List<Map<String, Object>> ptps = alarmConvergeMapper.getLinkPtpByUnitId(unitId);
						
						if (ptps != null && !ptps.isEmpty()) {
							List<Integer> ptpList = new ArrayList<Integer>();
							for (Map<String, Object> ptp : ptps) {
								int ptpId = (Integer) ptp.get("PTP_ID");
								ptpList.add(ptpId);
							}
							// 相邻网元端口不为空时处理告警收敛
							if (!ptpList.isEmpty()) {
								BasicDBObject condition = new BasicDBObject();
								// 隐含条件：告警收敛只针对未清除告警
								condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
								condition.put("PTP_ID", new BasicDBObject("$in", ptpList.toArray()));
								condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
								if (!act.getAlarms().isEmpty()) {
									if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
										condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
									}
								}
								// 处理待收敛告警
								processConvergeResult(id, condition);
							}
						}
					}
					break;
					
				// 对象所属传输系统网元（传输系统内网元，无告警对象类型限制）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_TRANSMISSION_SYS:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						// 获取所属传输系统网元
						List<Map<String, Object>> list = alarmConvergeMapper.getNeListFromTransSysByNeId(neId);
						if (list != null && !list.isEmpty()) {
							int[] neArray = new int[list.size()];
							for (int i=0; i<neArray.length; i++) {
								neArray[i] = (Integer) list.get(i).get("NE_ID");
							}
							BasicDBObject condition = new BasicDBObject();
							// 隐含条件：告警收敛只针对未清除告警
							condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
							condition.put("NE_ID", new BasicDBObject("$in", neArray));
							if (!act.getAlarms().isEmpty()) {
								if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
									condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
								}
							}
							// 处理待收敛告警
							processConvergeResult(id, condition);							
						}
					}
					break;
					
				// 对象相关电路通道（电路相关性查询，取得的所有通道：CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_CIRCUIT:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int unitId = (Integer) alm.get("UNIT_ID");
						int id = (Integer) alm.get("_id");
						int neType = (Integer) alm.get("NE_TYPE");
						if (neType == DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG ||
								neType == DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG) {
							// 按板卡获取电路相关CTP
							List<Map<String, Object>>list = alarmConvergeMapper.getCtpFromCircuit(null, unitId, null, null, neType);
							if (list != null && !list.isEmpty()) {
								int[] ctpArray = new int[list.size()];
								for (int i=0; i<ctpArray.length; i++) {
									ctpArray[i] = (Integer) list.get(i).get("CTP_ID");
								}
								BasicDBObject condition = new BasicDBObject();
								// 隐含条件：告警收敛只针对未清除告警
								condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
								condition.put("UNIT_ID", new BasicDBObject("$ne", unitId));
								condition.put("CTP_ID", new BasicDBObject("$in", ctpArray));
								if (!act.getAlarms().isEmpty()) {
									if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
										condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
									}
								}
								// 处理待收敛告警
								processConvergeResult(id, condition);
							}
						}
					}
					break;
					default:
				}
			}
		}
	}
	
	private void alarmConvergeForPtp(List<DBObject> almList) {
		// 循环处理所有收敛动作
		for (AlarmConvergeAction act : actions) {
			// 判定收敛动作的使能状态
			if (act.getActionStatus() == AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE) {
				// 根据告警对象类型分类处理
				switch (act.getActionType()) {
				
				// 对象本身（主告警端口：PTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_OBJ_SELF:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int ptpId = (Integer) alm.get("PTP_ID");
						int id = (Integer) alm.get("_id");
						BasicDBObject condition = new BasicDBObject();
						// 隐含条件：告警收敛只针对未清除告警
						condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
						condition.put("PTP_ID", ptpId);
						condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
						condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$ne",alm.get("NATIVE_PROBABLE_CAUSE")));
						if (!act.getAlarms().isEmpty()) {
							if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
								condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
							}
						}
						// 处理待收敛告警
						processConvergeResult(id, condition);
					}
					break;
					
				// 对象的下属对象（主告警端口以下的对象：CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_UNDERLING:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int ptpId = (Integer) alm.get("PTP_ID");
						int id = (Integer) alm.get("_id");
						BasicDBObject condition = new BasicDBObject();
						// 隐含条件：告警收敛只针对未清除告警
						condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
						condition.put("PTP_ID", ptpId);
						condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
						if (!act.getAlarms().isEmpty()) {
							if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
								condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
							}
						}
						// 处理待收敛告警
						processConvergeResult(id, condition);
					}
					break;
					
				// 对象的相邻端口（主告警端口，相邻网元端口：PTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_ADJACENT_PORT:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int ptpId = (Integer) alm.get("PTP_ID");
						int id = (Integer) alm.get("_id");
						// 获取相邻网元的端口
						Map<String, Object> ptp = alarmConvergeMapper.getLinkPtpByPtpId(ptpId);
						// 相邻网元端口不为空时处理告警收敛						
						if (ptp != null) {
							int farEndPtpId = (Integer) ptp.get("PTP_ID");
							BasicDBObject condition = new BasicDBObject();
							// 隐含条件：告警收敛只针对未清除告警
							condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
							condition.put("PTP_ID", farEndPtpId);
							condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT);
							if (!act.getAlarms().isEmpty()) {
								if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
									condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
								}
							}
							// 处理待收敛告警
							processConvergeResult(id, condition);
						}
					}
					break;
					
				// 对象所属传输系统网元（传输系统内网元，无告警对象类型限制）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_TRANSMISSION_SYS:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						// 获取所属传输系统网元
						List<Map<String, Object>> list = alarmConvergeMapper.getNeListFromTransSysByNeId(neId);
						if (list != null && !list.isEmpty()) {
							int[] neArray = new int[list.size()];
							for (int i=0; i<neArray.length; i++) {
								neArray[i] = (Integer) list.get(i).get("NE_ID");
							}
							BasicDBObject condition = new BasicDBObject();
							// 隐含条件：告警收敛只针对未清除告警
							condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
							condition.put("NE_ID", new BasicDBObject("$in", neArray));
							if (!act.getAlarms().isEmpty()) {
								if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
									condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
								}
							}
							// 处理待收敛告警
							processConvergeResult(id, condition);							
						}
					}
					break;
					
				// 对象相关电路通道（电路相关性查询，取得的所有通道：CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_CIRCUIT:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int ptpId = (Integer) alm.get("PTP_ID");
						int id = (Integer) alm.get("_id");
						int neType = (Integer) alm.get("NE_TYPE");
						if (neType == DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG ||
								neType == DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG) {
							// 按端口获取电路相关CTP
							List<Map<String, Object>>list = alarmConvergeMapper.getCtpFromCircuit(null, null, ptpId, null, neType);
							if (list != null && !list.isEmpty()) {
								int[] ctpArray = new int[list.size()];
								for (int i=0; i<ctpArray.length; i++) {
									ctpArray[i] = (Integer) list.get(i).get("CTP_ID");
								}
								BasicDBObject condition = new BasicDBObject();
								// 隐含条件：告警收敛只针对未清除告警
								condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
								condition.put("PTP_ID", new BasicDBObject("$ne", ptpId));
								condition.put("CTP_ID", new BasicDBObject("$in", ctpArray));
								if (!act.getAlarms().isEmpty()) {
									if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
										condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
									}
								}
								// 处理待收敛告警
								processConvergeResult(id, condition);
							}
						}
					}
					break;
					default:
				}
			}
		}
	}
	
	private void alarmConvergeForCtp(List<DBObject> almList) {
		// 循环处理所有收敛动作
		for (AlarmConvergeAction act : actions) {
			// 判定收敛动作的使能状态
			if (act.getActionStatus() == AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE) {
				// 根据告警对象类型分类处理
				switch (act.getActionType()) {
				
				// 对象本身（主告警通道：CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_OBJ_SELF:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int ctpId = (Integer) alm.get("CTP_ID");
						int id = (Integer) alm.get("_id");
						BasicDBObject condition = new BasicDBObject();
						// 隐含条件：告警收敛只针对未清除告警
						condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
						condition.put("CTP_ID", ctpId);
						condition.put("OBJECT_TYPE", AlarmConvergeDefine.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT);
						condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$ne",alm.get("NATIVE_PROBABLE_CAUSE")));
						if (!act.getAlarms().isEmpty()) {
							if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
								condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
							}
						}
						// 处理待收敛告警
						processConvergeResult(id, condition);
					}
					break;
					
				// 对象的下属对象（无）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_UNDERLING:
					break;
					
				// 对象的相邻端口（无）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_ADJACENT_PORT:
					break;
					
				// 对象所属传输系统网元（传输系统内网元，无告警对象类型限制）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_TRANSMISSION_SYS:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int neId = (Integer) alm.get("NE_ID");
						int id = (Integer) alm.get("_id");
						// 获取所属传输系统网元
						List<Map<String, Object>> list = alarmConvergeMapper.getNeListFromTransSysByNeId(neId);
						if (list != null && !list.isEmpty()) {
							int[] neArray = new int[list.size()];
							for (int i=0; i<neArray.length; i++) {
								neArray[i] = (Integer) list.get(i).get("NE_ID");
							}
							BasicDBObject condition = new BasicDBObject();
							// 隐含条件：告警收敛只针对未清除告警
							condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
							condition.put("NE_ID", new BasicDBObject("$in", neArray));
							if (!act.getAlarms().isEmpty()) {
								if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
									condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
								}
							}
							// 处理待收敛告警
							processConvergeResult(id, condition);							
						}
					}
					break;
					
				// 对象相关电路通道（电路相关性查询，取得的所有通道：CTP对象）
				case AlarmConvergeDefine.ALARM_CONVERGE_ACT_CIRCUIT:
					// 循环处理所有主告警
					for (DBObject alm : almList) {
						int ctpId = (Integer) alm.get("CTP_ID");
						int id = (Integer) alm.get("_id");
						int neType = (Integer) alm.get("NE_TYPE");
						if (neType == DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG ||
								neType == DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG) {
							// 按通道获取电路相关CTP
							List<Map<String, Object>>list = alarmConvergeMapper.getCtpFromCircuit(null, null, null, ctpId, neType);
							if (list != null && !list.isEmpty()) {
								int[] ctpArray = new int[list.size()];
								for (int i=0; i<ctpArray.length; i++) {
									ctpArray[i] = (Integer) list.get(i).get("CTP_ID");
								}
								BasicDBObject condition = new BasicDBObject();
								// 隐含条件：告警收敛只针对未清除告警
								condition.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
								condition.put("CTP_ID", new BasicDBObject("$ne", ctpId));
								condition.put("CTP_ID", new BasicDBObject("$in", ctpArray));
								if (!act.getAlarms().isEmpty()) {
									if (!act.getAlarms().contains(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ALL_ALARM)) {
										condition.put("NATIVE_PROBABLE_CAUSE", new BasicDBObject("$in", act.getAlarms().toArray()));
									}
								}
								// 处理待收敛告警
								processConvergeResult(id, condition);
							}
						}
					}
					break;
					default:
				}
			}
		}
	}
	
	/**
	 * 处理符合收敛动作条件的待收敛告警
	 * @param id
	 * @param condition
	 */
	private void processConvergeResult(int id, BasicDBObject condition) {
		System.out.println("【"+name+"@"+id+"】符合收敛动作的条件："+condition);
		// 查询结果计数
		int count = conn.find(condition).count();
		// 计数大于零时，说明有待收敛告警
		if (count > 0) {
			// 获取待收敛告警
			List<DBObject> convergeAlms = conn.find(condition).toArray();
			// 循环处理待收敛告警
			for (DBObject dbo : convergeAlms) {
				int flag = (Integer) dbo.get("CONVERGE_FLAG");
				int parentId = (Integer) dbo.get("PARENT_ID");
				// 主告警，将其收敛标志设为衍生告警，更新自身父告警ID，且更新其关联的衍生告警的父告警ID
				if (flag == AlarmConvergeDefine.ALARM_CONVERGE_MAIN_ALARM) {
					dbo.put("CONVERGE_FLAG", AlarmConvergeDefine.ALARM_CONVERGE_DERIVATIVE_ALARM);
					dbo.put("PARENT_ID", id);
					int curId = (Integer) dbo.get("_id");
					System.out.println("【"+name+"@"+id+"】收敛了一条主告警："+dbo.get("NATIVE_PROBABLE_CAUSE").toString());
					conn.save(dbo);
					// 将关联的下属衍生告警的父告警ID进行更新
					BasicDBObject cond = new BasicDBObject();
					cond.put("PARENT_ID", curId);
					int n = conn.find(cond).count();
					if (n > 0) {
						conn.update(cond, new BasicDBObject("$set", new BasicDBObject("PARENT_ID", id)));
						System.out.println("【"+name+"@"+id+"】收敛了主告警<+"+dbo.get("NATIVE_PROBABLE_CAUSE").toString()+">下属的衍生告警");
					}
				// 未处理告警，将其收敛标志设为衍生告警并更新父告警ID
				} else if (flag == AlarmConvergeDefine.ALARM_CONVERGE_UNKNOWN_ALARM) {
					dbo.put("CONVERGE_FLAG", AlarmConvergeDefine.ALARM_CONVERGE_DERIVATIVE_ALARM);
					dbo.put("PARENT_ID", id);
					System.out.println("【"+name+"@"+id+"】收敛了一条未处理告警："+dbo.get("NATIVE_PROBABLE_CAUSE").toString());
					conn.save(dbo);
				// 衍生告警，判断是否需更新其父告警ID
				} else if (flag == AlarmConvergeDefine.ALARM_CONVERGE_DERIVATIVE_ALARM) {
					if (parentId != id) {
						dbo.put("PARENT_ID", id);
						conn.save(dbo);
						System.out.println("【"+name+"@"+id+"】收敛了一条衍生告警："+dbo.get("NATIVE_PROBABLE_CAUSE").toString());
					}
				}
			}
			// 更新主告警的拥有子告警标志
			conn.update(new BasicDBObject("_id", id), new BasicDBObject("$set", new BasicDBObject("HAVE_CHILD", true)));
		}
	}

	/**
	 * 获取告警收敛规则的执行条件
	 * @return
	 */
	private BasicDBObject getQueryCondition() {
		// 建立符合适用范围、适用设备、符合告警对象类别和等级的主告警的查询条件
		BasicDBObject result = new BasicDBObject();
		// 隐含条件：告警收敛只针对未清除告警
		result.put("IS_CLEAR", AlarmConvergeDefine.IS_CLEAR_NO);
		// 适用范围条件
		result.put("EMS_ID", new BasicDBObject("$in", scope.toArray()));
		// 适用设备条件
		if (!equipmentNames.isEmpty()) {
			result.put("PRODUCT_NAME", new BasicDBObject("$in", equipmentNames.toArray()));
		}
		// 主告警名条件（主告警名、告警对象类型、对象级别：PTP/CTP类型）
		if (conditions.size() > 1) {
			BasicDBList almCond = new BasicDBList();
			for (AlarmConvergeCondition con : conditions) {
				BasicDBObject item = new BasicDBObject();
				// 告警名称
				item.put("NATIVE_PROBABLE_CAUSE", con.getAlarmName());
				if (!con.getObjectLevel().isEmpty()) {
					// 告警对象类型
					item.put("OBJECT_TYPE", con.getAlarmObjectType());
					// PTP
					if (con.getAlarmObjectType() == AlarmConvergeDefine
							.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT) {
						// 对象级别：PTP类型
						item.put("PTP_TYPE", new BasicDBObject("$in", con.getObjectLevel().toArray()));
					// CTP
					} else if (con.getAlarmObjectType() == AlarmConvergeDefine
							.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT) {
						// 对象级别：CTP类型
						item.put("CTP_TYPE", new BasicDBObject("$in", con.getObjectLevel().toArray()));
					}
				} else {
					// 告警对象类型
					item.put("OBJECT_TYPE", con.getAlarmObjectType());
				}
				almCond.add(item);
			}	
			result.put("$or", almCond);
		
		} else { // 单条主告警
			result.put("NATIVE_PROBABLE_CAUSE", conditions.get(0).getAlarmName());
			result.put("OBJECT_TYPE", conditions.get(0).getAlarmObjectType());
			if (!conditions.get(0).getObjectLevel().isEmpty()) {
				// PTP
				if (conditions.get(0).getAlarmObjectType() == AlarmConvergeDefine
						.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT) {
					// 对象级别：PTP类型
					result.put("PTP_TYPE", new BasicDBObject("$in", conditions.get(0).getObjectLevel().toArray()));
				// CTP
				} else if (conditions.get(0).getAlarmObjectType() == AlarmConvergeDefine
						.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT) {
					// 对象级别：CTP类型
					result.put("CTP_TYPE", new BasicDBObject("$in", conditions.get(0).getObjectLevel().toArray()));
				}
			}
		}
		
		return result;
	}

}
