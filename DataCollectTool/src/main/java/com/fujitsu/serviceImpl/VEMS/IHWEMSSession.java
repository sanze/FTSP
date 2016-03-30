package com.fujitsu.serviceImpl.VEMS;

import java.util.List;

import globaldefs.NameAndStringValue_T;

import HW.CosNotification.StructuredEvent;
import HW.HW_mstpInventory.HW_MSTPBindingPath_T;
import HW.HW_mstpInventory.HW_MSTPEndPoint_T;
import HW.HW_mstpInventory.HW_VirtualBridge_T;
import HW.HW_mstpInventory.HW_VirtualLAN_T;
import HW.HW_mstpService.HW_EthService_T;
import HW.emsMgr.ClockSourceStatus_T;
import HW.emsMgr.EMS_T;
import HW.equipment.EquipmentOrHolder_T;
import HW.performance.HoldingTime_T;
import HW.performance.PMData_T;
import HW.performance.PMParameter_T;
import HW.performance.PMTPSelect_T;
import HW.protection.EProtectionGroup_T;
import HW.protection.ESwitchData_T;
import HW.protection.ProtectionGroup_T;
import HW.protection.SwitchData_T;
import HW.protection.WDMProtectionGroup_T;
import HW.protection.WDMSwitchData_T;
import HW.subnetworkConnection.CrossConnect_T;
import HW.managedElement.ManagedElement_T;
import HW.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import HW.terminationPoint.TerminationPoint_T;
import HW.topologicalLink.TopologicalLink_T;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;

public interface IHWEMSSession extends IEMSSession{

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

	/** #查询EMS性能的保持时间# */
	public HoldingTime_T getHoldingTime() throws CommonException;

	/** #查询网元性能的能力# */
	public PMParameter_T[] getMEPMcapabilities(NameAndStringValue_T[] neName) throws CommonException;

	/**	########ProtectionMgr##### */
	/** #查询ME下所有保护组信息# */
	public ProtectionGroup_T[] getAllProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException;

	/** #查询所有设备保护组信息# */
	public EProtectionGroup_T[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException;

	/** #查询指定保护组信息# */
	public ProtectionGroup_T getProtectionGroup(NameAndStringValue_T[] pgName) throws CommonException;

	/** #查询指定设备保护组信息# */
	public EProtectionGroup_T getEProtectionGroup(
			NameAndStringValue_T[] epgName) throws CommonException;

	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public SwitchData_T[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException;

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public ESwitchData_T[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException;

	public TopologicalLink_T[] getAllInternalTopologicalLinks(
			NameAndStringValue_T[] meName) throws CommonException;
	
	public WDMProtectionGroup_T[] getAllWDMProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException;
	public WDMSwitchData_T[] retrieveWDMSwitchData(NameAndStringValue_T[] wpgpName) throws CommonException;

	public NameAndStringValue_T[][] getAllMstpEndPointNames(NameAndStringValue_T[] meName) throws CommonException;
	public HW_MSTPEndPoint_T[] getAllMstpEndPoints(NameAndStringValue_T[] meName) throws CommonException;
	
	public HW_EthService_T[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException;
	
	public NameAndStringValue_T[][] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException;
	public HW_VirtualBridge_T[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException;
	public HW_MSTPBindingPath_T[] getBindingPath(NameAndStringValue_T[] ptpName) throws CommonException;
	public HW_VirtualLAN_T[] getAllVLANs(NameAndStringValue_T[] meName) throws CommonException;
	
	/** #查询EMS下所有设备时钟源# */
	public ClockSourceStatus_T[] getObjectClockSourceStatus(NameAndStringValue_T[] name) throws CommonException;

	public String[] acknowledgeAlarms(List<String> alarmList) throws CommonException;
}