package com.fujitsu.IService;

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
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FdfrModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FlowDomainModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.MSTPBindingPathModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.SubnetworkConnectionModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;

/**
 * @author xuxiaojun
 * 
 */
public interface IEMSCollect {

	/**
	 * 初始化连接参数
	 * @param corbaName
	 * @param corbaPassword
	 * @param corbaIp
	 * @param corbaPort
	 * @param emsName
	 * @param internalEmsName
	 * @param encode
	 * @param iteratorNum
	 * @throws CommonException
	 */
	public void initParameter(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String internalEmsName,String encode,int iteratorNum) throws CommonException;
	
	/**
	 * 启动corba连接
	 * 
	 * @return
	 */
	public int startCorbaConnect() throws CommonException;

	/**
	 * @param neId
	 * @param isGateWayNe
	 * @return
	 */
	public int startTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException;

	/**
	 * @return
	 */
	public boolean disCorbaConnect() throws CommonException;

	/**
	 * @param neId
	 * @param isGateWayNe
	 * @return
	 */
	public boolean disTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException;
	
	
	/**
	 * 获取网管信息
	 * @return
	 * @throws CommonException
	 */
	public EmsDataModel getEMS() throws CommonException;

	/**
	 * 获取网元名列表
	 * @return
	 * @throws CommonException
	 */
	public NameAndStringValue_T[][] getAllManagedElementNames()
			throws CommonException;

	/**
	 * 获取网元列表
	 * @return
	 * @throws CommonException
	 */
	public List<ManagedElementModel> getAllManagedElements()
			throws CommonException;

	/**
	 * 获取网元下所有设备信息
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<EquipmentOrHolderModel> getAllEquipment(String neName)
			throws CommonException;

	// /**
	// * @param neId
	// * @return
	// * @throws CommonException
	// */
	// public NameAndStringValue_T[][] getAllPTPNames(String neName) throws
	// CommonException;

	/**
	 * 获取网元下所有端口信息
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<TerminationPointModel> getAllPTPs(String neName)
			throws CommonException;

	/**
	 * 获取网元下所有mstp端口信息
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<TerminationPointModel> getAllMstpEndPoints(String neName)
			throws CommonException;


	/**
	 * 获取网元当前性能
	 * @param neName
	 * @param layerRateList
	 * @param pmLocationList
	 * @param granularityList
	 * @return
	 * @throws CommonException
	 */
	public List<PmDataModel> getCurrentPmData_Ne(String neName,
			short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException;
	
	/**
	 * @param targetDisplayName
	 * @param neName
	 * @param historyPmStartTime
	 * @param belongToDate
	 * @return
	 * @throws CommonException
	 */
	public List<PmDataModel> getHistoryPmData_Ne(String targetDisplayName,
			String neName, String startTime,String endTime,short[] layerRateList, 
			String[] pmLocationList,String[] pmGranularityList,String ip,
			int port, String userName,String password,int emsType,boolean needAnalysisPm)
			throws CommonException;

	
	/**
	 * 获取端口当前性能
	 * @param neName
	 * @param ptpName
	 * @param layerRateList
	 * @param pmLocationList
	 * @param granularityList
	 * @return
	 * @throws CommonException
	 */
	public List<PmDataModel> getCurrentPmData_Ptp(String neName,
			String ptpName, short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException;

	/**
	 * 获取端口列表当前性能
	 * @param ptpNameList
	 *            ptpName由neName_ptpName形式组成，例589874_PTP:/rack=1/shelf=1/slot=1/
	 *            domain=sdh/port=1
	 * @param layerRateList
	 * @param pmLocationList
	 * @param granularityList
	 * @return
	 * @throws CommonException
	 */
	public  Map<String, List<PmDataModel>> getCurrentPmData_PtpList(
			List<String> ptpNameList, short[] layerRateList,
			String[] pmLocationList, String[] granularityList)
			throws CommonException;

	/**
	 * 查询网管系统中所有未结束的告警和未结束的TCA事件。
	 * 
	 * @return
	 * @throws CommonException
	 */
	public List<AlarmDataModel> getAllEMSAndMEActiveAlarms()
			throws CommonException;

	/**
	 * 查询网管系统自身的未结束告警事件
	 * 
	 * @return
	 * @throws CommonException
	 */
	public List<AlarmDataModel> getAllEMSSystemActiveAlarms()
			throws CommonException;

	/**
	 * 查询指定网元内所有符合条件的未结束的告警（包括未结束的TCA事件）。
	 * 
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<AlarmDataModel> getAllActiveAlarms(String neName)
			throws CommonException;

	/**
	 * 确认告警
	 * @param alarmList
	 * @return
	 * @throws CommonException
	 */
	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException;

	
	/**
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<VirtualBridgeModel> getAllVBs(String neName)
			throws CommonException;
	
	/**
	 * 获取网元板卡保护信息
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<EProtectionGroupModel> getAllEProtectionGroups(String neName)
			throws CommonException;
	
	/**
	 * 获取网元环保护
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<ProtectionGroupModel> getAllProtectionGroups(String neName)
			throws CommonException;
	
	/**
	 * 获取网元wdm环保护
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<WDMProtectionGroupModel> getAllWDMProtectionGroups(String neName)
			throws CommonException;
	
	/**
	 * 获取网元时钟信息
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<ClockSourceStatusModel> getObjectClockSourceStatus(String neName)
			throws CommonException;

//	/**
//	 * @param neName
//	 * @param ethServiceName serviceName格式 EMS Huawei/T2000 ManagedElement 3146812 EthService 1/1/0/8
//	 * @return
//	 * @throws CommonException
//	 */
//	public HWEthServiceModel getEthService(String neName, String ethServiceName)
//			throws CommonException;
	
	/**
	 * 获取网管link信息
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException
	 */
//	public List<TopologicalLinkModel> getAllTopLevelTopologicalLinks()
//			throws CommonException;
	

	/**
	 * 获取网管link信息
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException
	 */
	public List<TopologicalLinkModel> getAllTopologicalLinks()
			throws CommonException;

	/**
	 * 获取网元内部link信息
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<TopologicalLinkModel> getAllInternalTopologicalLinks(
			String neName) throws CommonException;

	/**
	 * 获取网元交叉连接信息
	 * @param neName
	 * @param connectionRateList
	 * @return
	 * @throws CommonException
	 */
	public List<CrossConnectModel> getCRS(String neName,
			short[] connectionRateList) throws CommonException;

	/**
	 * 获取ptp端口下的ctp信息
	 * @param ptpName
	 * @return
	 * @throws CommonException
	 */
	public List<TerminationPointModel> getContainedPotentialTPs(
			NameAndStringValue_T[] ptpName) throws CommonException;
	
	/**
	 * 获取网元以太网信息
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<EthServiceModel> getAllEthService(String neName)
			throws CommonException;
	
	/**
	 * 获取ptp端口下bindingPath信息
	 * @param ptpName
	 * @return
	 * @throws CommonException
	 */
	public List<MSTPBindingPathModel> getBindingPath(
			NameAndStringValue_T[] ptpName) throws CommonException;
	
	
	
	/**
	 * 获取snc数据
	 * @return
	 * @throws CommonException
	 */
	public List<SubnetworkConnectionModel> getAllSubnetworkConnections() throws CommonException;

	/**
	 * 获取route数据
	 * @return
	 * @throws CommonException
	 */
	public List<CrossConnectModel> getRoute(boolean needSort) throws CommonException;
	
	/**
	 * 获取flowDomains数据
	 * @return
	 * @throws CommonException
	 */
	public List<FlowDomainModel> getAllFlowDomains() throws CommonException;
	
	/**
	 * 获取fdfr数据
	 * @return
	 * @throws CommonException
	 */
	public List<FdfrModel> getAllFDFrs() throws CommonException;
	
	/**
	 * 获取linkoffdfr数据
	 * @return
	 * @throws CommonException
	 */
	public List<TopologicalLinkModel> getAllLinkOfFDFrs() throws CommonException;
	
}
