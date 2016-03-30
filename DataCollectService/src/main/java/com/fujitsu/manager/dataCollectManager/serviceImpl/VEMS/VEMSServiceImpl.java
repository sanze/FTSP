package com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS;

import globaldefs.NameAndStringValue_T;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ClockSourceStatusModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EmsDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentOrHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EthServiceModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.MSTPBindingPathModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.SubnetworkConnectionModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;


/**
 * @author xuxiaojun
 *  虚拟网管
 */
public class VEMSServiceImpl extends EMSCollectService {
	
	@Override
	public void initParameter(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String internalEmsName, String encode,int iteratorNum) throws CommonException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int startCorbaConnect() throws CommonException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int startTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean disCorbaConnect() throws CommonException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EmsDataModel getEMS() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameAndStringValue_T[][] getAllManagedElementNames()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ManagedElementModel> getAllManagedElements()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EquipmentOrHolderModel> getAllEquipment(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TerminationPointModel> getAllPTPs(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TerminationPointModel> getAllMstpEndPoints(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PmDataModel> getCurrentPmData_Ne(String neName,
			short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PmDataModel> getHistoryPmData_Ne(String targetDisplayName,
			String neName, String startTime, String endTime,
			short[] layerRateList, String[] pmLocationList,
			String[] pmGranularityList, String ip, int port,
			String userName, String password,int emsType,boolean needAnalysisPm) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PmDataModel> getCurrentPmData_Ptp(String neName,
			String ptpName, short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<PmDataModel>> getCurrentPmData_PtpList(
			List<String> ptpNameList, short[] layerRateList,
			String[] pmLocationList, String[] granularityList)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AlarmDataModel> getAllEMSAndMEActiveAlarms()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AlarmDataModel> getAllEMSSystemActiveAlarms()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AlarmDataModel> getAllActiveAlarms(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VirtualBridgeModel> getAllVBs(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EProtectionGroupModel> getAllEProtectionGroups(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProtectionGroupModel> getAllProtectionGroups(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WDMProtectionGroupModel> getAllWDMProtectionGroups(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ClockSourceStatusModel> getObjectClockSourceStatus(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TopologicalLinkModel> getAllTopologicalLinks()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TopologicalLinkModel> getAllInternalTopologicalLinks(
			String neName) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CrossConnectModel> getCRS(String neName,
			short[] connectionRateList) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TerminationPointModel> getContainedPotentialTPs(
			NameAndStringValue_T[] ptpName) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EthServiceModel> getAllEthService(String neName)
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MSTPBindingPathModel> getBindingPath(
			NameAndStringValue_T[] ptpName) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SubnetworkConnectionModel> getAllSubnetworkConnections()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

}
