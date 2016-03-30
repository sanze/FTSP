package com.fujitsu.manager.alarmConvergeManager.serviceImpl;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import com.fujitsu.IService.IAlarmConvergeService;
import com.fujitsu.IService.IAlarmConvergeServiceProxy;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.util.CommonUtil;

public class AlarmConvergeServiceProxyImpl implements IAlarmConvergeServiceProxy {
	
	private IAlarmConvergeService service;
	
	public void initParameter() throws CommonException {
		this.service = getAlarmConvergeService();
	}
	
	@Override
	public void startAlarmConvergeById(int[] ruleIds) throws CommonException {
		service.startAlarmConvergeById(ruleIds);
	}

	@Override
	public void stopAlarmConvergeById(int[] ruleIds) throws CommonException {
		service.stopAlarmConvergeById(ruleIds);
	}

	@Override
	public void addAlarmConverge(int ruleId) throws CommonException {
		service.addAlarmConverge(ruleId);
	}

	@Override
	public void deleteAlarmConverge(int[] ruleIds) throws CommonException {
		service.deleteAlarmConverge(ruleIds);
	}
	
	@Override
	public void updateAlarmConverge(int ruleId) throws CommonException {
		service.updateAlarmConverge(ruleId);
	}

	@Override
	public int getAlarmConvergeUseStatus(int ruleId) throws CommonException {
		return service.getAlarmConvergeUseStatus(ruleId);
	}

	@Override
	public int getAlarmConvergeOperStatus(int ruleId) throws CommonException {
		return service.getAlarmConvergeOperStatus(ruleId);
	}

	private  IAlarmConvergeService getAlarmConvergeService()
			throws CommonException {
		
		String ip = CommonUtil.getSystemConfigProperty("AlarmConvergeHostIp");
		//检查是否能ping通
		if(!CommonUtil.isReachable(ip)){
			throw new CommonException(new NullPointerException(),MessageCodeDefine.ALARM_CONVERGE_SERVICE_FAILED);
		}

		String port = CommonUtil.getSystemConfigProperty("AlarmConvergeServicePort");
		
		String address = "rmi://"+ ip + ":" + port + "/alarmConvergeService";

		IAlarmConvergeService service = null;

		// 获取rmi对象
		RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
		rmiProxyFactoryBean.setServiceInterface(IAlarmConvergeService.class);
		// 例"rmi://localhost:1021/dataCollectService"
		rmiProxyFactoryBean.setServiceUrl(address);
		try {
			rmiProxyFactoryBean.afterPropertiesSet(); //
			// 更改ServiceInterface或ServiceUrl之后必须调用该方法，来获取远程调用桩
		} catch (Exception ex) {
			throw new CommonException(new NullPointerException(),MessageCodeDefine.ALARM_CONVERGE_SERVICE_FAILED);
		}
		service = (IAlarmConvergeService) rmiProxyFactoryBean.getObject();
		return service;
	}

	@Override
	public boolean manualSatarAlarmConverge(int ruleId) throws CommonException {
		return service.manualSatarAlarmConverge(ruleId);
	}

}
