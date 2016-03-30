package com.fujitsu.manager.alarmConvergeManager.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.fujitsu.IService.IAlarmConvergeService;
import com.fujitsu.common.AlarmConvergeDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.AlarmConvergeMapper;
import com.fujitsu.manager.alarmConvergeManager.model.AlarmConverge;
import com.fujitsu.manager.alarmConvergeManager.model.AlarmConvergeAction;
import com.fujitsu.manager.alarmConvergeManager.model.AlarmConvergeCondition;

public class AlarmConvergeServiceImpl implements IAlarmConvergeService {

	@Resource
	private AlarmConvergeMapper alarmConvergeMapper;
	// 告警收敛规则列表
	private List<AlarmConverge> alarmConvergeList = new ArrayList<AlarmConverge>();
	// 告警收敛线程池
	private ScheduledExecutorService pool = Executors
			.newScheduledThreadPool(AlarmConvergeDefine.DEFAULT_THREAD_POOL_SIZE);
	// 告警收敛时延
	private int alarmConvergeTimer = AlarmConvergeDefine.DEFAULT_ALARM_CONVERGE_TIMER;
	// 告警收敛初始延迟
	private int initialDelay = 1;
	
	private StringBuilder sb = new StringBuilder();
	
	@Override
	public void init() throws CommonException {
		System.out.println("告警收敛服务初始化...");
		// 获取所有告警收敛规则信息
		String tableName = "t_alarm_converge";
		List<Map<String, Object>> ruleList = alarmConvergeMapper.selectTable(tableName);
		if (ruleList != null && ruleList.size() > 0) {
			for (Map<String, Object> rule : ruleList) {
				AlarmConverge item = new AlarmConverge(rule);
				alarmConvergeList.add(item);
			}
		}
		// 获取系统参数配置信息
		if (alarmConvergeMapper.getSystemParam("ALARM_CONVERGE_TIMER") != null) {
			String value = alarmConvergeMapper.getSystemParam("ALARM_CONVERGE_TIMER").get("PARAM_VALUE").toString();
			alarmConvergeTimer = Integer.valueOf(value);
		}
		
		// 启动告警收敛规则中状态为“启用”的规则
		if (!alarmConvergeList.isEmpty()) {
			for (AlarmConverge alm : alarmConvergeList) {
				if (alm.getUseStatus() == AlarmConvergeDefine.ALARM_CONVERGE_ENABLE) {
					ScheduledFuture<?> future = pool.
							scheduleWithFixedDelay(alm, initialDelay, alarmConvergeTimer, TimeUnit.SECONDS);
					alm.setSchResult(future);
				}
			}
		}
		System.out.println("告警收敛服务初始化完成...");
	}

	@Override
	public void startAlarmConvergeById(int[] ruleIds) throws CommonException {
		for (int ruleId : ruleIds) {
			// 获取指定ID的告警收敛规则启用状态
			int status = getAlarmConvergeUseStatus(ruleId);
			sb.setLength(0);
			
			// 如果状态为挂起则执行启动操作
			if (AlarmConvergeDefine.ALARM_CONVERGE_DISABLE == status) {
				// 获取指定ID的告警收敛规则
				AlarmConverge almConverge = getAlarmConverge(ruleId);
				if (almConverge != null && almConverge.getSchResult() == null) {
					// 启用该告警收敛规则
					ScheduledFuture<?> future = pool.
						scheduleWithFixedDelay(almConverge, initialDelay, alarmConvergeTimer, TimeUnit.SECONDS);
					// 设置调度操作结果
					almConverge.setSchResult(future);
					// 设置告警收敛规则状态
					almConverge.setUseStatus(AlarmConvergeDefine.ALARM_CONVERGE_ENABLE);
					// 更新数据库中的规则启用状态
					alarmConvergeMapper.updateAlarmConvergeById(ruleId, AlarmConvergeDefine.ALARM_CONVERGE_ENABLE);
					sb.append("【").append(almConverge.getName()).append("@").append(almConverge.getId());
					sb.append("】任务正常启用完成.");
					System.out.println(sb.toString());
				} else if (almConverge != null && almConverge.getSchResult() != null) {
					almConverge.setUseStatus(AlarmConvergeDefine.ALARM_CONVERGE_ENABLE);
					sb.append("【").append(almConverge.getName()).append("@").append(almConverge.getId());
					sb.append("】任务已经处于启用中.");
					System.out.println(sb.toString());
				}
			}			
		}
	}

	@Override
	public void stopAlarmConvergeById(final int[] ruleIds) throws CommonException {
		for (int i=0; i<ruleIds.length; i++) {
			final int ruleId = ruleIds[i];
			// 获取指定ID的告警收敛规则
			final AlarmConverge alm = getAlarmConverge(ruleId);
			
			// 状态为启用的规则才能执行挂起操作
			if (AlarmConvergeDefine.ALARM_CONVERGE_ENABLE == alm.getUseStatus() &&
					alm.getSchResult() != null) {
				ScheduledFuture<?> future = pool.schedule(new Callable<String>(){
					public String call() {
						String result = "【"+alm.getName() + "@" + alm.getId() + "】任务没有正常挂起.";
						boolean flag = alm.getSchResult().cancel(true);
						if (flag) {
							alm.setUseStatus(AlarmConvergeDefine.ALARM_CONVERGE_DISABLE);
							alm.setSchResult(null);
							// 更新数据库中的规则启用状态
							alarmConvergeMapper.updateAlarmConvergeById(ruleId, AlarmConvergeDefine.ALARM_CONVERGE_DISABLE);
							result = "【"+alm.getName() + "@" + alm.getId() + "】任务已经正常挂起.";						
						}
						return result;
					}
				}, (long) 1, TimeUnit.SECONDS);
				// 输出执行结果
				try {
					System.out.println(future.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}			
		}
	}

	@Override
	public void addAlarmConverge(int ruleId) throws CommonException {
		// 获取指定ID的告警收敛规则
		String tableName = "t_alarm_converge";
		String idName = "CONVERGE_ID";
		int idValue = ruleId;
		List<Map<String, Object>> ruleList = alarmConvergeMapper.selectTableListById(tableName, idName, idValue);
		if (ruleList != null && !ruleList.isEmpty()) {
			AlarmConverge alm = new AlarmConverge(ruleList.get(0));
			alarmConvergeList.add(alm);
			System.out.println("新增了一个告警收敛规则("+ruleId+")。");
			// 如初始状态为启用，则启动该规则
			if (alm.getUseStatus() == AlarmConvergeDefine.ALARM_CONVERGE_ENABLE) {
				ScheduledFuture<?> future = pool.
						scheduleWithFixedDelay(alm, initialDelay, alarmConvergeTimer, TimeUnit.SECONDS);
				alm.setSchResult(future);
				System.out.println("启用了一条新增的告警收敛规则("+ruleId+")。");
			}
		}
	}

	@Override
	public void deleteAlarmConverge(int[] ruleIds) throws CommonException {
		for (int ruleId : ruleIds) {
			// 获取告警收敛规则启用状态
			int status = getAlarmConvergeUseStatus(ruleId);
			// 如是启用状态则先进行停止操作
			if (status == AlarmConvergeDefine.ALARM_CONVERGE_ENABLE) {
				stopAlarmConvergeById(new int[]{ruleId});
			}
			// 获取指定的告警收敛规则
			AlarmConverge almConverge = getAlarmConverge(ruleId);
			// 从收敛规则列表中删除指定规则
			if (almConverge != null) {
				alarmConvergeList.remove(almConverge);
				System.out.println("删除了一条告警收敛规则("+ruleId+")。");
			}			
		}
	}
	
	@Override
	public void updateAlarmConverge(int ruleId) throws CommonException {
		// 获取缓存的告警收敛规则
		AlarmConverge alm = getAlarmConverge(ruleId);
		int index = alarmConvergeList.indexOf(alm);

		// 获取告警收敛规则名称
		String tableName = "t_alarm_converge";
		String idName = "CONVERGE_ID";
		List<Map<String, Object>> rules = alarmConvergeMapper.selectTableListById(tableName, idName, ruleId);
		if (rules !=null && rules.size() > 0) {
			String ruleName = rules.get(0).get("RULE_NAME").toString();
			// 更新告警收敛规则名称
			alm.setName(ruleName);
		}
		// 获取告警收敛范围
		tableName = "t_alarm_converge_scope";		
		List<Map<String, Object>> scopeList = alarmConvergeMapper.selectTableListById(tableName, idName, ruleId);
		List<Integer> scopes = new ArrayList<Integer>();
		if (scopeList != null && scopeList.size() > 0) {
			for (Map<String, Object> item : scopeList) {
				int emsId = (Integer) item.get("EMS_ID");
				scopes.add(emsId);
			}
			// 更新告警收敛范围
			alm.setScope(scopes);
		}
		// 获取告警收敛设备
		tableName = "t_alarm_converge_equipment";
		List<Map<String, Object>> eqptList = alarmConvergeMapper.selectTableListById(tableName, idName, ruleId);
		List<String> equipmentNames = new ArrayList<String>();
		if (eqptList != null && eqptList.size() > 0) {
			for (Map<String, Object> item : eqptList) {
				String productName = item.get("PRODUCT_NAME").toString();
				equipmentNames.add(productName);
			}
			// 更新告警收敛设备
			alm.setEquipment(equipmentNames);
		}
		// 获取告警收敛条件
		tableName = "t_alarm_converge_condition";
		List<AlarmConvergeCondition> conditions = new ArrayList<AlarmConvergeCondition>();
		List<Map<String, Object>> condList = alarmConvergeMapper.selectTableListById(tableName, idName, ruleId);
		if (condList != null && condList.size() > 0) {
			for (Map<String, Object> item : condList) {
				AlarmConvergeCondition cond = new AlarmConvergeCondition(item);
				conditions.add(cond);
			}
			// 更新告警收敛条件
			alm.setConditions(conditions);
		}
		// 获取告警收敛执行动作
		tableName = "t_alarm_converge_action";
		List<Map<String, Object>> actionList = alarmConvergeMapper.selectTableListById(tableName, idName, ruleId);
		List<AlarmConvergeAction> actions = new ArrayList<AlarmConvergeAction>();
		if (actionList != null && actionList.size() > 0) {
			for (Map<String, Object> item : actionList) {
				AlarmConvergeAction action = new AlarmConvergeAction(item);
				actions.add(action);
			}
			// 更新告警收敛执行动作
			alm.setActions(actions);
		}
		// 更新缓存的告警收敛规则
		alarmConvergeList.set(index, alm);
		System.out.println("更新了一条告警收敛规则("+ruleId+")");
	}
	
	// 获取指定ID的告警收敛规则
	private AlarmConverge getAlarmConverge(int ruleId) throws CommonException {
		AlarmConverge result = null;
		if (!alarmConvergeList.isEmpty()) {
			for (AlarmConverge alm : alarmConvergeList) {
				if (ruleId == alm.getId()) {
					result = alm;
				}
			}
		}
		return result;
	}

	@Override
	public int getAlarmConvergeUseStatus(int ruleId) throws CommonException {
		int result = AlarmConvergeDefine.ALARM_CONVERGE_DISABLE;
		if (!alarmConvergeList.isEmpty()) {
			for (AlarmConverge alm : alarmConvergeList) {
				if (ruleId == alm.getId()) {
					result = alm.getUseStatus();
				}
			}
		}
		return result;
	}

	@Override
	public int getAlarmConvergeOperStatus(int ruleId) throws CommonException {
		int result = AlarmConvergeDefine.ALARM_CONVERGE_RUNTIME_UNKNOWN;
		if (!alarmConvergeList.isEmpty()) {
			for (AlarmConverge alm : alarmConvergeList) {
				if (ruleId == alm.getId()) {
					result = alm.getOperStatus();
				}
			}
		}
		return result;
	}

	@Override
	public boolean manualSatarAlarmConverge(int ruleId) throws CommonException {
		System.out.println("人工启用告警收敛规则开始("+ruleId+")。");
		// 获取指定ID的告警收敛规则
		AlarmConverge alm = getAlarmConverge(ruleId);

		int useStatus = alm.getUseStatus();
		if (useStatus == AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE &&
				alm.getOperStatus() == AlarmConvergeDefine.ALARM_CONVERGE_RUNTIME_RUNNING) {
			System.out.println("人工启用告警收敛规则结束("+ruleId+"：此收敛规正按原计划执行中)。");
			return true;
		} else {
			if (useStatus == AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE) {
				alm.setUseStatus(AlarmConvergeDefine.ALARM_CONVERGE_ACT_DISABLE);				
			}
    		// 立即运行一次告警收敛规则
    		ScheduledFuture<?> result = pool.schedule(alm, 100, TimeUnit.MILLISECONDS);
    		long start = new Date().getTime();
    		long cur = 0;
    		do {
    			cur = new Date().getTime();
    		} while (result.isDone() || (cur-start)/1000 > alarmConvergeTimer);
			if (useStatus == AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE) {
				alm.setUseStatus(AlarmConvergeDefine.ALARM_CONVERGE_ACT_ENABLE);				
			}
    		System.out.println("人工启用告警收敛规则结束("+ruleId+")。");
		}

		return true;
	}

	@Override
	public List<Map<String, Object>> getAlarmConvergeServiceInfo()
			throws CommonException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (AlarmConverge alm : alarmConvergeList) {
			Map<String, Object> almRule = new HashMap<String, Object>();
			almRule.put("ID", alm.getId());
			almRule.put("NAME", alm.getName());
			almRule.put("USE_STATUS", alm.getUseStatus());
			almRule.put("OPR_STATUS", alm.getOperStatus());
			result.add(almRule);
		}
		return result;
	}

}
