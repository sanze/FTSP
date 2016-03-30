package com.fujitsu.manager.faultDiagnoseManager.serviceImpl;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import com.fujitsu.IService.IFaultDiagnoseService;
import com.fujitsu.IService.IFaultDiagnoseServiceProxy;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.util.CommonUtil;

public class FaultDiagnoseServiceProxyImpl implements IFaultDiagnoseServiceProxy {
	
	private IFaultDiagnoseService service;
	
	public void initParameter() throws CommonException {
		this.service = getFaultDiagnoseService();
	}
	
	@Override
	public void startFaultDiagnoseById(int[] ruleIds) throws CommonException {
		service.startFaultDiagnoseById(ruleIds);
	}

	@Override
	public void stopFaultDiagnoseById(int[] ruleIds) throws CommonException {
		service.stopFaultDiagnoseById(ruleIds);
	}

	@Override
	public void addFaultDiagnose(int ruleId) throws CommonException {
		service.addFaultDiagnose(ruleId);
	}

	@Override
	public void deleteFaultDiagnose(int[] ruleIds) throws CommonException {
		service.deleteFaultDiagnose(ruleIds);
	}
	
	@Override
	public void updateFaultDiagnose(int ruleId) throws CommonException {
		service.updateFaultDiagnose(ruleId);
	}

	@Override
	public int getFaultDiagnoseUseStatus(int ruleId) throws CommonException {
		return service.getFaultDiagnoseUseStatus(ruleId);
	}

	@Override
	public int getFaultDiagnoseOperStatus(int ruleId) throws CommonException {
		return service.getFaultDiagnoseOperStatus(ruleId);
	}

	private  IFaultDiagnoseService getFaultDiagnoseService()
			throws CommonException {
		
		String ip = CommonUtil.getSystemConfigProperty("FaultDiagnoseHostIp");
		//检查是否能ping通
		if(!CommonUtil.isReachable(ip)){
			throw new CommonException(new NullPointerException(),MessageCodeDefine.ALARM_CONVERGE_SERVICE_FAILED);
		}

		String port = CommonUtil.getSystemConfigProperty("FaultDiagnoseServicePort");
		
		String address = "rmi://"+ ip + ":" + port + "/faultDiagnoseService";

		IFaultDiagnoseService service = null;

		// 获取rmi对象
		RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
		rmiProxyFactoryBean.setServiceInterface(IFaultDiagnoseService.class);
		// 例"rmi://localhost:1021/dataCollectService"
		rmiProxyFactoryBean.setServiceUrl(address);
		try {
			rmiProxyFactoryBean.afterPropertiesSet(); //
			// 更改ServiceInterface或ServiceUrl之后必须调用该方法，来获取远程调用桩
		} catch (Exception ex) {
			throw new CommonException(new NullPointerException(),MessageCodeDefine.FAULT_DIAGNOSE_SERVICE_FAILED);
		}
		service = (IFaultDiagnoseService) rmiProxyFactoryBean.getObject();
		return service;
	}

	@Override
	public boolean manualSatarFaultDiagnose(int ruleId) throws CommonException {
		return service.manualSatarFaultDiagnose(ruleId);
	}

}
