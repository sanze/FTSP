package com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS;

import globaldefs.NameAndStringValue_T;

import java.util.List;

import ZTE_U31.CosNotification.StructuredEvent;
import ZTE_U31.clocksource.ClockSource_T;
import ZTE_U31.emsMgr.EMS_T;
import ZTE_U31.equipment.EquipmentOrHolder_T;
import ZTE_U31.ethernet.VB_T;
import ZTE_U31.ethernet.VLAN_T;
import ZTE_U31.managedElement.ManagedElement_T;
import ZTE_U31.managedElementManager.MEConfigData_T;
import ZTE_U31.mstpcommon.EthernetService_T;
import ZTE_U31.mstpcommon.VCGBinding_T;
import ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import ZTE_U31.performance.PMData_T;
import ZTE_U31.performance.PMTPSelect_T;
import ZTE_U31.protection.EProtectionGroup_T;
import ZTE_U31.protection.ESwitchData_T;
import ZTE_U31.subnetworkConnection.CrossConnect_T;
import ZTE_U31.subnetworkConnection.SubnetworkConnection_T;
import ZTE_U31.terminationPoint.TerminationPoint_T;
import ZTE_U31.topologicalLink.TopologicalLink_T;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;

public interface IZTEEMSSession extends IEMSSession{

	/**	########EmsMgr######*/
	/** 获取支持的管理器 */
	public String[] getSupportedManagers() throws CommonException;

	/** #查询网管信息# */
	public EMS_T getEMS() throws CommonException;

	/** #查询当前所有告警# */
	public StructuredEvent[] getAllEMSAndMEActiveAlarms() throws CommonException;

	public StructuredEvent[] getAllEMSSystemActiveAlarms() throws CommonException;

	/** #查询EMS下顶层子网# */
	public MultiLayerSubnetwork_T[] getAllTopLevelSubnetworks() throws CommonException;

	/** #查询EMS下顶层子网名# */
	public NameAndStringValue_T[][] getAllTopLevelSubnetworkNames() throws CommonException;

	/** #查询所有跨EMS间拓扑连接# */
	public TopologicalLink_T[] getAllTopLevelTopologicalLinks() throws CommonException;
	
	/** #获取所有snc数据# */
	public SubnetworkConnection_T[] getAllSubnetworkConnections(NameAndStringValue_T[] subnetName) throws CommonException;
	
	/** #获取所有route数据# */
	public CrossConnect_T[] getRoute(NameAndStringValue_T[] sncName) throws CommonException;
	/**	########ManagedElementMgr###### */
	/** #同步当前告警数据# */
	public StructuredEvent[] getAllActiveAlarms(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询单网元路由的子交叉# */
	public CrossConnect_T[] getAllCrossConnections(
			NameAndStringValue_T[] neName, short[] connectionRateList) throws CommonException;

	/** #查询所有网元信息# */
	public ManagedElement_T[] getAllManagedElements() throws CommonException;

	/** #查询物理终端点配置信息# */
	public TerminationPoint_T[] getAllPTPs(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询物理终端点名# */
	public NameAndStringValue_T[][] getAllPTPNames(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询指定速率下包含的潜在CTP# */
	public TerminationPoint_T[] getContainedPotentialTPs(
			NameAndStringValue_T[] tpName) throws CommonException;

	/** #查询指定网元信息# */
	public ManagedElement_T getManagedElement(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询所有网元名# */
	public NameAndStringValue_T[][] getAllManagedElementNames() throws CommonException;

	/**	########EquipmentMgr##### */
	/** #查询指定实体下所有设备# */
	public EquipmentOrHolder_T[] getAllEquipment(
			NameAndStringValue_T[] meOrHoderName) throws CommonException;

	/**	########MultilayerSubnetworkMgr##### */
	/** #查询子网下所有拓扑连接# */
	public TopologicalLink_T[] getAllTopologicalLinks(
			NameAndStringValue_T[] subnetName) throws CommonException;

	/**	########PerformanceMgr##### */
	/** #查询指定ptp当前性能# */
	public PMData_T[] getAllCurrentPMData(List<PMTPSelect_T> selectTPList) throws CommonException;
	/** #查询指定网元当前性能# */
	public PMData_T[] getAllCurrentPMData(NameAndStringValue_T[] name,
			short[] _layerRateList, String[] _pMLocationList,
			String[] _granularityList) throws CommonException;

	/** #查询历史性能# */
	public void getHistoryPMData(NameAndStringValue_T[] name,
			String ftpIpAndFileName, String userName, String password, 
			String startTime, String endTime, short[] _layerRateList, 
			String[] _pMLocationList, String[] _granularityList)
			throws CommonException;
	/** #查询历史性能# */
	public PMData_T[] getHistoryPMData(NameAndStringValue_T[] name,
			String startTime, String endTime,short[] layerRateList,
			String[] pmLocationList,String[] pmGranularityList) throws CommonException;

	/**	########ProtectionMgr##### */
	/** #查询所有设备保护组信息# */
	public EProtectionGroup_T[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException;

	/** #查询指定设备保护组信息# */
	public EProtectionGroup_T getEProtectionGroup(
			NameAndStringValue_T[] epgName) throws CommonException;

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public ESwitchData_T[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException;

	/** #查询EMS下所有设备时钟源# */
	public ClockSource_T[] getObjectClockSourceStatus(NameAndStringValue_T[] name) throws CommonException;
	
	public VB_T[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException;
	public NameAndStringValue_T[][] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException;
	public EthernetService_T[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException;
	public VLAN_T[] getAllVLANs(NameAndStringValue_T[] meName) throws CommonException;
	public VCGBinding_T getBindingPath(NameAndStringValue_T[] vcgTpName) throws CommonException;
	public MEConfigData_T getMEconfigData(NameAndStringValue_T[] meName) throws CommonException;
}