package com.fujitsu.serviceImpl.VEMS;

import globaldefs.NameAndStringValue_T;

import java.util.List;

import org.omg.CosNotification.StructuredEvent;
import ALU.emsMgr.EMS_T;
import ALU.equipment.EquipmentOrHolder_T;
import ALU.performance.HoldingTime_T;
import ALU.performance.PMData_T;
import ALU.performance.PMParameter_T;
import ALU.performance.PMTPSelect_T;
import ALU.protection.EProtectionGroup_T;
import ALU.protection.ESwitchData_T;
import ALU.protection.ProtectionGroup_T;
import ALU.protection.SwitchData_T;
import ALU.subnetworkConnection.CrossConnect_T;
import ALU.managedElement.ManagedElement_T;
import ALU.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import ALU.terminationPoint.TerminationPoint_T;
import ALU.topologicalLink.TopologicalLink_T;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;

public interface IALUEMSSession extends IEMSSession{

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

}