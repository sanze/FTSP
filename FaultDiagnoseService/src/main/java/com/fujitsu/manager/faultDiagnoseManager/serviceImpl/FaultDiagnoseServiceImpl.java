package com.fujitsu.manager.faultDiagnoseManager.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.fujitsu.IService.IFaultDiagnoseService;
import com.fujitsu.common.FaultDiagnoseDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.FaultDiagnoseMapper;
import com.fujitsu.manager.faultDiagnoseManager.model.FaultDiagnoseAction;
import com.fujitsu.manager.faultDiagnoseManager.model.FaultDiagnoseCondition;
import com.fujitsu.manager.faultDiagnoseManager.model.FaultDiagnoseRule;
import com.fujitsu.manager.faultDiagnoseManager.service.FaultDiagnose;

public class FaultDiagnoseServiceImpl implements IFaultDiagnoseService {

	@Resource
	private FaultDiagnoseMapper faultDiagnoseMapper;
	// 故障诊断规则列表
	private List<FaultDiagnoseRule> faultDiagnoseList = new ArrayList<FaultDiagnoseRule>();
	// 故障诊断线程池
	private ScheduledExecutorService pool = Executors
			.newScheduledThreadPool(FaultDiagnoseDefine.DEFAULT_THREAD_POOL_SIZE);
	// 故障诊断时延
	private int faultDiagnoseTimer = FaultDiagnoseDefine.DEFAULT_FAULT_DIAGNOSE_TIMER;
	// 故障诊断初始延迟
	private int initialDelay = 1;
	
	private FaultDiagnose fault;
	
	@Override
	public void init() throws CommonException {
		System.out.println("故障诊断服务初始化...");
		// 获取所有故障诊断规则信息
		String tableName = "t_fault_diagnose";
		List<Map<String, Object>> ruleList = faultDiagnoseMapper.selectTable(tableName);
		if (ruleList != null && ruleList.size() > 0) {
			for (Map<String, Object> rule : ruleList) {
				FaultDiagnoseRule item = new FaultDiagnoseRule(rule);
				faultDiagnoseList.add(item);
			}
		}
		// 获取系统参数配置信息
		if (faultDiagnoseMapper.getSystemParam("FAULT_DIAGNOSE_TIMER") != null) {
			String value = faultDiagnoseMapper.getSystemParam("FAULT_DIAGNOSE_TIMER").get("PARAM_VALUE").toString();
			faultDiagnoseTimer = Integer.valueOf(value);
		}
		
		// 启动故障诊断处理线程
		fault = new FaultDiagnose(faultDiagnoseList);
		ScheduledFuture<?> future = pool.
				scheduleWithFixedDelay(fault, initialDelay, faultDiagnoseTimer, TimeUnit.SECONDS);
		fault.setSchResult(future);

		System.out.println("故障诊断服务初始化完成...");
	}

	@Override
	public void startFaultDiagnoseById(int[] ruleIds) throws CommonException {
		for (int ruleId : ruleIds) {
			// 获取指定ID的告警收敛规则启用状态
			int status = getFaultDiagnoseUseStatus(ruleId);
			
			// 如果状态为挂起则执行启动操作
			if (FaultDiagnoseDefine.FAULT_DIAGNOSE_DISABLE == status) {
				// 获取指定ID的故障诊断规则
				FaultDiagnoseRule faultDiagnose = getFaultDiagnose(ruleId);
				// 更新数据库中的规则启用状态
				faultDiagnoseMapper.updateFaultDiagnoseById(ruleId, FaultDiagnoseDefine.FAULT_DIAGNOSE_ENABLE);				
				// 设置故障诊断规则状态
				faultDiagnose.setUseStatus(FaultDiagnoseDefine.FAULT_DIAGNOSE_ENABLE);
				System.out.println("【"+faultDiagnose.getName()+"】规则已经正常启用.");		
			}			
		}		
	}

	@Override
	public void stopFaultDiagnoseById(int[] ruleIds) throws CommonException {
		for (int ruleId : ruleIds) {
			// 获取指定ID的告警收敛规则启用状态
			int status = getFaultDiagnoseUseStatus(ruleId);
			
			// 如果状态为执行则执行停止操作
			if (FaultDiagnoseDefine.FAULT_DIAGNOSE_ENABLE == status) {
				// 获取指定ID的故障诊断规则
				FaultDiagnoseRule faultDiagnose = getFaultDiagnose(ruleId);
				// 更新数据库中的规则启用状态
				faultDiagnoseMapper.updateFaultDiagnoseById(ruleId, FaultDiagnoseDefine.FAULT_DIAGNOSE_DISABLE);				
				// 设置故障诊断规则状态
				faultDiagnose.setUseStatus(FaultDiagnoseDefine.FAULT_DIAGNOSE_DISABLE);
				System.out.println("【"+faultDiagnose.getName()+"】规则已经正常挂起.");		
			}			
		}
		
	}

	@Override
	public void addFaultDiagnose(int ruleId) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteFaultDiagnose(int[] ruleIds) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFaultDiagnose(int ruleId) throws CommonException {
		// 获取缓存的故障诊断规则
		FaultDiagnoseRule fault = getFaultDiagnose(ruleId);
		int index = faultDiagnoseList.indexOf(fault);

		String tableName = "t_fault_diagnose_scope";
		String idName = "DIAGNOSE_ID";

		// 获取故障诊断范围
		List<Map<String, Object>> scopeList = faultDiagnoseMapper.selectTableListById(tableName, idName, ruleId);
		List<Integer> scopes = new ArrayList<Integer>();
		if (scopeList != null && scopeList.size() > 0) {
			for (Map<String, Object> item : scopeList) {
				int emsId = (Integer) item.get("EMS_ID");
				scopes.add(emsId);
			}
			// 更新故障诊断范围
			fault.setScope(scopes);
		}
		// 获取故障诊断设备
		tableName = "t_fault_diagnose_equipment";
		List<Map<String, Object>> eqptList = faultDiagnoseMapper.selectTableListById(tableName, idName, ruleId);
		List<String> equipmentNames = new ArrayList<String>();
		if (eqptList != null && eqptList.size() > 0) {
			for (Map<String, Object> item : eqptList) {
				String productName = item.get("PRODUCT_NAME").toString();
				equipmentNames.add(productName);
			}
			// 更新故障诊断设备
			fault.setEquipment(equipmentNames);
		}
		// 获取故障诊断条件
		tableName = "t_fault_diagnose_condition";
		List<FaultDiagnoseCondition> conditions = new ArrayList<FaultDiagnoseCondition>();
		List<Map<String, Object>> condList = faultDiagnoseMapper.selectTableListById(tableName, idName, ruleId);
		if (condList != null && condList.size() > 0) {
			for (Map<String, Object> item : condList) {
				FaultDiagnoseCondition cond = new FaultDiagnoseCondition(item);
				conditions.add(cond);
			}
			// 更新故障诊断条件
			fault.setConditions(conditions);
		}
		// 获取故障诊断执行动作
		tableName = "t_fault_diagnose_action";
		List<Map<String, Object>> actionList = faultDiagnoseMapper.selectTableListById(tableName, idName, ruleId);
		List<FaultDiagnoseAction> actions = new ArrayList<FaultDiagnoseAction>();
		if (actionList != null && actionList.size() > 0) {
			for (Map<String, Object> item : actionList) {
				FaultDiagnoseAction action = new FaultDiagnoseAction(item);
				actions.add(action);
			}
			// 更新故障诊断执行动作
			fault.setActions(actions);
		}
		// 更新缓存的故障诊断规则
		faultDiagnoseList.set(index, fault);
		System.out.println("更新了一条故障诊断规则。");
	}

	// 获取指定ID的故障诊断规则
	private FaultDiagnoseRule getFaultDiagnose(int ruleId) throws CommonException {
		FaultDiagnoseRule result = null;
		if (!faultDiagnoseList.isEmpty()) {
			for (FaultDiagnoseRule fault : faultDiagnoseList) {
				if (ruleId == fault.getId()) {
					result = fault;
				}
			}
		}
		return result;
	}
	
	@Override
	public int getFaultDiagnoseUseStatus(int ruleId) throws CommonException {
		int result = FaultDiagnoseDefine.FAULT_DIAGNOSE_DISABLE;
		if (!faultDiagnoseList.isEmpty()) {
			for (FaultDiagnoseRule fault : faultDiagnoseList) {
				if (ruleId == fault.getId()) {
					result = fault.getUseStatus();
				}
			}
		}
		return result;
	}

	@Override
	public int getFaultDiagnoseOperStatus(int ruleId) throws CommonException {
		int result = FaultDiagnoseDefine.FAULT_DIAGNOSE_RUNTIME_UNKNOWN;
		if (!faultDiagnoseList.isEmpty()) {
			for (FaultDiagnoseRule fault : faultDiagnoseList) {
				if (ruleId == fault.getId()) {
					result = fault.getOperStatus();
				}
			}
		}
		return result;
	}

	@Override
	public boolean manualSatarFaultDiagnose(int ruleId) throws CommonException {
		System.out.println("人工启用故障诊断规则开始("+ruleId+")。");
		
		// 获取指定ID的故障诊断规则
		FaultDiagnoseRule faultRule = getFaultDiagnose(ruleId);
		// 取消当前的故障诊断
		boolean flag = fault.getSchResult().cancel(true); 
		if (flag) {
			// 立即运行一次故障诊断规则
			fault.faultDiagnoseProcess(faultRule, true);
		}
		
		// 恢复原先定期的故障诊断
		ScheduledFuture<?> future = pool.
				scheduleWithFixedDelay(fault, initialDelay, faultDiagnoseTimer, TimeUnit.SECONDS);
		fault.setSchResult(future);
		
		System.out.println("人工启用故障诊断规则结束("+ruleId+")。");
		return true;
	}

}
