package com.fujitsu.manager.equipmentTestManager.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Service;

import com.fujitsu.IService.IDeviceTest;
import com.fujitsu.activeMq.JMSSender;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonMapper;
import com.fujitsu.dao.mysql.PlanMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.equipmentTestManager.service.EquipmentTestManagerService;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest.XTWSocketServerThread;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUAlarm;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUConfiguration;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RoutePointInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.TestParaInfoModel;
import com.fujitsu.model.CommandModel;
import com.fujitsu.model.CommandPriorityModel;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

@Service
public class EquipmentTestManagerServiceImpl extends
		EquipmentTestManagerService {

	@Resource
	private PlanMapper planMapper;
	@Resource
	private CommonMapper commonMapper;

	public static Map<String, CommandPriorityModel> commandPriorityMap = new HashMap<String, CommandPriorityModel>();

	private static ExecutorService executorService;
	//监听进程是否启动标志
	private static boolean isListenerThreadStarted = false;
	
	public EquipmentTestManagerServiceImpl(){
		//启动监听
		startListenerThread();
	}
	
	/**
	 * @param collectType
	 * @return
	 * @throws CommonException
	 */
	private Object getDataFromEquip(Map param, int collectType,
			int collectLevel) throws CommonException {
		//设备信息
		EqptInfoModel eqpt = param.containsKey(PARAM_KEY_EQPT)?(EqptInfoModel)param.get(PARAM_KEY_EQPT):null;
		// //检查设备是否可以正常
		IsRcVolid(eqpt.getRcode());
		// //检查设备是否在线
		// if(ne!=null){
		// IsNeCanCollect(ne,collectType);
		// }
		// 包装命令
		CommandModel commandModel = new CommandModel(String.valueOf(RandomUtils
				.nextDouble()), collectType, collectLevel, new Date());

		Object data = null;

		// 检查是否最优先级命令
		while (!IsHighestCommand(eqpt.getRcode(), commandModel)) {
			try {
				System.out.println("sleep...................");
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				throw new CommonException(e,
						MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}

		try {
			// 获取数据
			data = getData(param, collectType);
		} catch (CommonException e) {
			throw e;
		} finally {
			// 移除命令
			CommandPriorityModel model = commandPriorityMap.get(eqpt.getRcode());
			model.removeCmd(commandModel);
		}

		return data;
	}

	/**
	 * @param collectType
	 * @return
	 * @throws CommonException
	 */
	private Object getData(final Map param,final int collectType)
			throws CommonException {

		Callable<Object> thread = new Callable<Object>() {
			public Object call() throws CommonException {
				// 设备信息
				EqptInfoModel eqpt = param.containsKey(PARAM_KEY_EQPT) ? (EqptInfoModel) param
						.get(PARAM_KEY_EQPT) : null;
				// 系统信息
				SysInfoModel sys = param.containsKey(PARAM_KEY_SYS) ? (SysInfoModel) param
						.get(PARAM_KEY_SYS) : null;
				//获取各厂家设备实现类
				IDeviceTest service = getInstance(Integer.valueOf(eqpt.getFactory()));

				Object data = null;

				switch (collectType) {

				case COLLECT_TYPE_ALARM:
					 data = service.loadRTUAlarm(eqpt, sys);
					break;
				case COLLECT_TYPE_CONFIG:
					 data = service.loadRTUConfiguration(eqpt, sys);
					break;
				case COLLECT_TYPE_OTDR_TEST:
					// 路由信息
					List<RoutePointInfoModel> routePointList = param
							.containsKey(PARAM_KEY_ROUTE_POINT) ? (List<RoutePointInfoModel>) param
							.get(PARAM_KEY_ROUTE_POINT) : null;
					// 测试参数
					TestParaInfoModel testPara = param
							.containsKey(PARAM_KEY_TEST_PARA) ? (TestParaInfoModel) param
							.get(PARAM_KEY_TEST_PARA) : null;
					//开始路由测试
					 data = service.startTest(eqpt, sys, routePointList, testPara);
					break;
				default:
				}
				return data;
			}
		};

		if (executorService == null) {
			executorService = Executors.newCachedThreadPool();
		}

		// 添加采集进程
		FutureTask<Object> future = new FutureTask<Object>(thread);
		// 执行采集进程
		executorService.submit(future);

		Object data = null;
		// 固定超时时间60s
		int timeOut = 300;

		try {
			data = future.get(timeOut, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			throw new CommonException(e, MessageCodeDefine.MESSAGE_CODE_999999);
		} catch (Exception e) {
			// 记录log信息，此处记录比较详细
			ExceptionHandler.handleException(e);
			if (CommonException.class.isInstance(e.getCause())) {
				throw new CommonException(e,
						((CommonException) e.getCause()).getErrorCode(),
						((CommonException) e.getCause()).getErrorMessage());
			} else {
				throw new CommonException(e,
						MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}
		return data;
	}

	/**
	 * 获取实现类
	 * 
	 * @param type
	 * @return
	 * @throws CommonException
	 */
	private IDeviceTest getInstance(int factory) throws CommonException {
		IDeviceTest service = null;

		switch (factory) {
		case CommonDefine.EQUIP_FACTORY_ZB:
			//中博实现类
			service = (IDeviceTest) SpringContextUtil
					.getBean("zbService");
			break;
		case CommonDefine.EQUIP_FACTORY_XTW:
			//昕天卫实现类
			service = (IDeviceTest) SpringContextUtil
					.getBean("xtwService");
			break;
		default:
			//抛出厂家不支持异常
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CMD_RETURN_UNKNOW_FACTORY);
		}
		return service;
	}

	/**
	 * 命令优先级判断 规则 命令优先级最高，时间最小的优先执行
	 * 
	 * @param commandModel
	 * @return
	 */
	private synchronized boolean IsHighestCommand(String Rcode,
			CommandModel commandModel) {
		// 固定命令数为1
		int maxThreads = 1;
		// 取得命令优先级对象
		CommandPriorityModel commandPriorityModel = commandPriorityMap.get(Rcode);

		// 第一次进入
		if (commandPriorityModel == null) {
			// 创建对象
			commandPriorityModel = new CommandPriorityModel(maxThreads);
			commandPriorityMap.put(Rcode, commandPriorityModel);
		}
		commandPriorityModel.setMaxThreads(maxThreads);
		// 命令列表中加入命令
		commandPriorityModel.addCmd(commandModel);
		return commandPriorityModel.activeCmd(commandModel);
	}

	/**
	 * OTDR测试服务统一入口
	 * 
	 * @param eqpt
	 * @param sys
	 * @param routeList
	 * @param testPara
	 * @return
	 * @throws CommonException
	 */
	public String otdrTestCentralizeEntrance(EqptInfoModel eqpt,
			SysInfoModel sys, List<RoutePointInfoModel> routePointList,
			TestParaInfoModel testPara, int collectLevel) throws CommonException {
		//修改路由状态为占用
		modifyRouteStatus(testPara.getRouteId(),CommonDefine.ROUTE_STATUS_OCCUPY);

		//组装参数
		Map param = new HashMap();
		
		param.put(PARAM_KEY_EQPT, eqpt);
		param.put(PARAM_KEY_SYS, sys);
		param.put(PARAM_KEY_ROUTE_POINT, routePointList);
		param.put(PARAM_KEY_TEST_PARA, testPara);
		
		String testResult = (String) getDataFromEquip(param, COLLECT_TYPE_OTDR_TEST ,collectLevel);

		//修改路由状态为空闲
		modifyRouteStatus(testPara.getRouteId(),CommonDefine.ROUTE_STATUS_FREE);

		return testResult;
	}

	/**
	 * @function:查询设备配置
	 * @data:2015-1-8
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return
	 * @throws CommonException
	 *             List<RTUConfiguration>
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<RTUConfiguration> loadRTUConfiguration(EqptInfoModel eqpt,
			SysInfoModel sys, int collectLevel) throws CommonException {

		List<RTUConfiguration> rtuConfigList = new ArrayList();

		//组装参数
		Map param = new HashMap();
		
		param.put(PARAM_KEY_EQPT, eqpt);
		param.put(PARAM_KEY_SYS, sys);
		
		rtuConfigList = (List<RTUConfiguration>) getDataFromEquip(param, COLLECT_TYPE_CONFIG ,collectLevel);
		
		return rtuConfigList;
	}

	/**
	 * @function:查询设备告警
	 * @data:2015-1-8
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return
	 * @throws CommonException
	 *             List<RTUAlarm>
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<RTUAlarm> loadRTUAlarm(EqptInfoModel eqpt, SysInfoModel sys, int collectLevel)
			throws CommonException {

		List<RTUAlarm> rtuAlarmList = new ArrayList();

		//组装参数
		Map param = new HashMap();
		
		param.put(PARAM_KEY_EQPT, eqpt);
		param.put(PARAM_KEY_SYS, sys);
		
		rtuAlarmList = (List<RTUAlarm>) getDataFromEquip(param, COLLECT_TYPE_ALARM ,collectLevel);

		return rtuAlarmList;
	}

	//修改路由状态
	public void modifyRouteStatus(int routeId , int status){
		Map route  = new HashMap();
		route.put("TEST_ROUTE_ID", routeId);
		route.put("STATUS", status);
		planMapper.updateTestRouteById(route);
		// 发送表示测试开始结束的JMS通知消息
		JMSSender.sendMessage(CommonDefine.MESSAGE_TYPE_CABLE_TEST, route);
	}
	
	/**
	 * 判断设备是否可以运行
	 * @param number 唯一,非空,RTU/CTU 编号
	 * @return
	 * @throws CommonException
	 */
	private void IsRcVolid(String number) throws CommonException {
		// 判断网元通信状态
		Map rc = commonMapper.selectTableByColumn("t_ftts_rc", "NUMBER", number);
		// 通信状态
		Integer status = rc.get("STATUS") != null ? Integer
				.valueOf(rc.get("STATUS").toString()) : null;
		if (status != null
				&& status.intValue() == CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG) {
			// 再次联机检测一次
			String ip = rc.get("IP").toString();
			boolean isReachable = CommonUtil.isReachable(ip);
			if (!isReachable) {
				throw new CommonException(
						new NullPointerException(),
						MessageCodeDefine.CMD_CONNECT_ERROR);
			}
		}
	}
	
	//启动监听进程
	private synchronized void startListenerThread(){
		if(!isListenerThreadStarted){
			try {
				new Thread(new XTWSocketServerThread()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			isListenerThreadStarted = true;
		}
	}

}
