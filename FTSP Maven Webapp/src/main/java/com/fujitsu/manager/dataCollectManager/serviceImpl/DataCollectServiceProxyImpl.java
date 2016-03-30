package com.fujitsu.manager.dataCollectManager.serviceImpl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.SystemManagerMapper;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.service.DataCollectServiceProxy;
import com.fujitsu.model.LinkAlterModel;
import com.fujitsu.model.LinkAlterResultModel;
import com.fujitsu.model.NeAlterModel;
import com.fujitsu.util.CommonUtil;

@Scope("prototype")
@Service
@Transactional(rollbackFor = Exception.class)
public class DataCollectServiceProxyImpl extends DataCollectServiceProxy {
	
	private IDataCollectService service;
	
	private Map paramter;
	
	@Resource
	private CommonManagerMapper commonManagerMapper;
	@Resource
	private SystemManagerMapper systemManagerMapper;

	@Override
	public void initParameter(int emsConnectionId) throws CommonException {
		
		Map connection = commonManagerMapper.selectTableById(
				"T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID",
				emsConnectionId);
		this.paramter = connection;
		this.service = getDataCollectService(emsConnectionId);
	}

	@Override
	public int startCorbaConnect() throws CommonException {
		return service.startCorbaConnect(paramter);
	}

	@Override
	public boolean disCorbaConnect() throws CommonException {
		return service.disCorbaConnect(paramter);
	}

	@Override
	public int startTelnetConnect() throws CommonException {
		return service.startTelnetConnect(paramter);
	}

	@Override
	public boolean disTelnetConnect() throws CommonException {
		return service.disTelnetConnect(paramter);
	}

	@Override
	public int logonTelnetNe(int neId) {
		return service.logonTelnetNe(paramter, neId);
	}

	@Override
	public int logoutTelnetNe(int neId) {
		return service.logoutTelnetNe(paramter, neId);
	}

	@Override
	public void syncEmsInfo(int commandLevel) throws CommonException {
		service.syncEmsInfo(paramter, commandLevel);
	}

	@Override
	public List<NeAlterModel> getNeAlertList(int commandLevel)
			throws CommonException {
		return service.getNeAlertList(paramter, commandLevel);
	}

	@Override
	public void syncNeList(int commandLevel) throws CommonException {
		service.syncNeList(paramter, commandLevel);
	}

	/**
	 * 同步snc数据
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncSNC(int commandLevel) throws CommonException{
		service.syncSNC(paramter, commandLevel);
	}
	
	/**
	 * 同步route数据
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncRoute(int commandLevel) throws CommonException{
		service.syncRoute(paramter, commandLevel);
	}

	@Override
	public void syncSingleNeData(int neId, boolean isSyncPtp,
			boolean isSyncMstp, boolean isSyncInternalLink,
			boolean isSyncEprotection, boolean isSyncProtection,
			boolean isSyncWdmProtection, boolean isSyncClock,
			boolean isSyncCtp, int commandLevel) throws CommonException {
		service.syncSingleNeData(paramter, neId, isSyncPtp, isSyncMstp, isSyncInternalLink, isSyncEprotection, isSyncProtection, isSyncWdmProtection, isSyncClock, isSyncCtp, commandLevel);
	}

	@Override
	public void syncNeEquipmentOrHolder(int neId, int commandLevel)
			throws CommonException {
		service.syncNeEquipmentOrHolder(paramter, neId, commandLevel);
	}

	@Override
	public void syncNePtp(int neId, int commandLevel) throws CommonException {
		service.syncNePtp(paramter, neId, commandLevel);
	}

	@Override
	public void syncNeInternalLink(int neId, int commandLevel)
			throws CommonException {
		service.syncNeInternalLink(paramter, neId, commandLevel);
	}

	@Override
	public void syncNeEProtectionGroup(int neId, int commandLevel)
			throws CommonException {
		service.syncNeEProtectionGroup(paramter, neId, commandLevel);
	}

	@Override
	public void syncNeProtectionGroup(int neId, int commandLevel)
			throws CommonException {
		service.syncNeProtectionGroup(paramter, neId, commandLevel);
	}

	@Override
	public void syncNeWDMProtectionGroup(int neId, int commandLevel)
			throws CommonException {
		service.syncNeWDMProtectionGroup(paramter, neId, commandLevel);
	}

	@Override
	public void syncNeCtp(int neId, int commandLevel) throws CommonException {
		service.syncNeCtp(paramter, neId, commandLevel);
	}

	@Override
	public void syncExtendInfo_NE(int neId, int commandLevel) throws CommonException {
		service.syncExtendInfo_NE(paramter, neId, commandLevel);
	}

	@Override
	public void syncNeCRS(int neId, short[] layerRateList, int commandLevel)
			throws CommonException {
		service.syncNeCRS(paramter, neId, layerRateList, commandLevel);
	}

	@Override
	public LinkAlterResultModel getLinkAlterList(int commandLevel)
			throws CommonException {
		return service.getLinkAlterList(paramter, commandLevel);
	}

	@Override
	public void syncLink(List<LinkAlterModel> syncList,int commandLevel) throws CommonException {
		service.syncLink(paramter, syncList, commandLevel);
	}

	@Override
	public void syncNeEthService(int neId, int commandLevel)
			throws CommonException {
		service.syncNeEthService(paramter, neId, commandLevel);
	}

	@Override
	public void syncNeBindingPath(int neId, int commandLevel)
			throws CommonException {
		service.syncNeBindingPath(paramter, neId, commandLevel);
	}

	@Override
	public void syncPtpBindingPath(int neId, int ptpId, String ptpNameString,
			String internalEmsName, String neSerialNo, int commandLevel)
			throws CommonException {
		service.syncPtpBindingPath(paramter, neId, ptpId, ptpNameString, internalEmsName, neSerialNo, commandLevel);
	}

	@Override
	public void syncNeClock(int neId, int commandLevel) throws CommonException {
		service.syncNeClock(paramter, neId, commandLevel);
	}
	
	@Override
	public void syncNeVBs(int neId, int commandLevel)
			throws CommonException {
		service.syncNeVBs(paramter, neId, commandLevel);
	}

	@Override
	public List<PmDataModel> getCurrentPmData_Ne(int neId,
			short[] layerRateList, int[] pmLocationList, int[] granularityList,
			boolean collectNumbic, boolean collectPhysical, boolean collectCtp,
			int commandLevel) throws CommonException {
		return service.getCurrentPmData_Ne(paramter, neId, layerRateList, pmLocationList, granularityList, collectNumbic, collectPhysical, collectCtp, commandLevel);
	}

	@Override
	public List<PmDataModel> getCurrentPmData_PtpList(List<Integer> ptpIdList,
			short[] layerRateList, int[] pmLocationList, int[] granularityList,
			boolean collectNumbic, boolean collectPhysical, boolean collectCtp,
			int commandLevel) throws CommonException {
		return service.getCurrentPmData_PtpList(paramter,ptpIdList, layerRateList, pmLocationList, granularityList, collectNumbic, collectPhysical, collectCtp, commandLevel);
	}

	@Override
	public List<PmDataModel> getHistoryPmData_Ne(int neId, String time,
			short[] layerRateList, int[] pmLocationList,
			int[] pmGranularityList, boolean collectNumbic,
			boolean collectPhysical, boolean collectCtp, int commandLevel)
			throws CommonException {

		return service.getHistoryPmData_Ne(paramter,neId, time, layerRateList,
				pmLocationList, pmGranularityList, collectNumbic,
				collectPhysical, collectCtp, commandLevel);
	}

	@Override
	public void syncAllEMSAndMEActiveAlarms(int[] objectType,
			int[] perceivedSeverity, int commandLevel) throws CommonException {
		
		service.syncAllEMSAndMEActiveAlarms(paramter, objectType, perceivedSeverity, commandLevel);
	}

	@Override
	public void syncAllActiveAlarms(int neId, int[] perceivedSeverity,
			int commandLevel) throws CommonException {
		
		service.syncAllActiveAlarms(paramter, neId, perceivedSeverity, commandLevel);
		
	}
	
	@Override
	public void getPtnReportData(String ptnSysId, String ptnSysName,
			List<Integer> neList, String time, int commandLevel)
			throws CommonException {
		service.getPtnReportData(paramter, ptnSysId, ptnSysName, neList, time, commandLevel);
	}
	
	@Override
	public void syncAllFdfrs(int commandLevel) throws CommonException {
		service.syncAllFdfrs(paramter, commandLevel);
	}

	@Override
	public void syncLinkOfFdfrs(int commandLevel) throws CommonException {
		service.syncLinkOfFdfrs(paramter, commandLevel);
	}
	
	/**
	 * 获取数据采集服务
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException 
	 */
	private  IDataCollectService getDataCollectService(int emsConnectionId)
			throws CommonException {
		
		Map svcRecord = systemManagerMapper.selectSvcRecordByEmsconnectionId(emsConnectionId);
		
		if(svcRecord == null){
			throw new CommonException(new NullPointerException(),MessageCodeDefine.DATA_COLLECT_SERVICE_UNCONFIG);
		}
		
		String ip = svcRecord.get("IP").toString();
		//检查是否能ping通
		if(!CommonUtil.isReachable(ip)){
			throw new CommonException(new NullPointerException(),MessageCodeDefine.DATA_COLLECT_SERVICE_FAILED);
		}
		 String address = svcRecord.get("ADDRESS").toString();

		 IDataCollectService service = null;

		// 获取rmi对象
		RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
		rmiProxyFactoryBean.setServiceInterface(IDataCollectService.class);
		// 例"rmi://localhost:1021/dataCollectService"
		rmiProxyFactoryBean.setServiceUrl(address);
		try {
			rmiProxyFactoryBean.afterPropertiesSet(); //
			// 更改ServiceInterface或ServiceUrl之后必须调用该方法，来获取远程调用桩
		} catch (Exception ex) {
			throw new CommonException(new NullPointerException(),MessageCodeDefine.DATA_COLLECT_SERVICE_FAILED);
		}
		service = (IDataCollectService) rmiProxyFactoryBean.getObject();
		return service;
	}
	


}
