package com.fujitsu.manager.faultDiagnoseManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fujitsu.common.FaultDiagnoseDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.FaultDiagnoseMapper;
import com.fujitsu.util.BeanUtil;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class FaultDiagnoseRule {

	private Mongo mongo = (Mongo) BeanUtil.getBean("mongo");
	private static DBCollection conn = null;

	private FaultDiagnoseMapper faultDiagnoseMapper = (FaultDiagnoseMapper) BeanUtil
		.getBean("faultDiagnoseMapper");

	// 故障诊断规则ID
	private int id;
	// 故障诊断规则名称
	private String name;
	// 故障诊断规则的启用状态
	private int useStatus;
	// 故障诊断的范围
	private List<Integer> scope = new ArrayList<Integer>();
	// 故障诊断的设备类型
	private List<String> equipmentNames = new ArrayList<String>();
	// 故障诊断规则的执行条件
	private List<FaultDiagnoseCondition> conditions = new ArrayList<FaultDiagnoseCondition>();
	// 故障诊断规则的执行动作
	private List<FaultDiagnoseAction> actions = new ArrayList<FaultDiagnoseAction>();
	// 故障诊断规则的运行状态
	private int operStatus = FaultDiagnoseDefine.FAULT_DIAGNOSE_RUNTIME_IDLE;
	
	public FaultDiagnoseRule(Map<String, Object> paramMap) throws CommonException {
		id = (Integer) paramMap.get("DIAGNOSE_ID");
		name = paramMap.get("RULE_NAME").toString();
		useStatus = (Integer) paramMap.get("STATUS");

		// 初始化告警数据库链接
		if (conn == null) {
			conn = mongo.getDB(FaultDiagnoseDefine.MONGODB_NAME).getCollection(FaultDiagnoseDefine.T_CURRENT_ALARM);
		}

		// 获取故障诊断范围
		String tableName = "t_fault_diagnose_scope";
		String idName = "DIAGNOSE_ID";
		int idValue = id;
		List<Map<String, Object>> scopeList = faultDiagnoseMapper.selectTableListById(tableName, idName, idValue);
		if (scopeList != null && scopeList.size() > 0) {
			for (Map<String, Object> item : scopeList) {
				int emsId = (Integer) item.get("EMS_ID");
				scope.add(emsId);
			}
		}
		// 获取故障诊断设备
		tableName = "t_fault_diagnose_equipment";
		List<Map<String, Object>> eqptList = faultDiagnoseMapper.selectTableListById(tableName, idName, idValue);
		if (eqptList != null && eqptList.size() > 0) {
			for (Map<String, Object> item : eqptList) {
				String productName = item.get("PRODUCT_NAME").toString();
				equipmentNames.add(productName);
			}
		}
		// 获取故障诊断条件
		tableName = "t_fault_diagnose_condition";
		List<Map<String, Object>> condList = faultDiagnoseMapper.selectTableListById(tableName, idName, idValue);
		if (condList != null && condList.size() > 0) {
			for (Map<String, Object> item : condList) {
				FaultDiagnoseCondition cond = new FaultDiagnoseCondition(item);
				conditions.add(cond);
			}
		}
		// 获取故障诊断执行动作
		tableName = "t_fault_diagnose_action";
		List<Map<String, Object>> actionList = faultDiagnoseMapper.selectTableListById(tableName, idName, idValue);
		if (actionList != null && actionList.size() > 0) {
			for (Map<String, Object> item : actionList) {
				FaultDiagnoseAction action = new FaultDiagnoseAction(item);
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
	public List<FaultDiagnoseCondition> getConditions() {
		return this.conditions;
	}
	public void setConditions(List<FaultDiagnoseCondition> value) {
		this.conditions = value;
	}
	public List<FaultDiagnoseAction> getActions() {
		return this.actions;
	}
	public void setActions(List<FaultDiagnoseAction> value) {
		this.actions = value;
	}
	public int getOperStatus() {
		return this.operStatus;
	}
	public void setOperStatus(int value) {
		this.operStatus = value;
	}

}
