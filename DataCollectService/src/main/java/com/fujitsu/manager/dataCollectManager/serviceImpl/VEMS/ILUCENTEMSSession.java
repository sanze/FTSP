package com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS;

import java.util.List;

import globaldefs.NameAndStringValue_T;

import LUCENT.CosNotification.StructuredEvent;
import LUCENT.emsMgr.EMS_T;
import LUCENT.equipment.EquipmentOrHolder_T;
import LUCENT.performance.HoldingTime_T;
import LUCENT.performance.PMData_T;
import LUCENT.performance.PMParameter_T;
import LUCENT.performance.PMTPSelect_T;
import LUCENT.protection.ProtectionGroup_T;
import LUCENT.protection.SwitchData_T;
import LUCENT.subnetworkConnection.CrossConnect_T;
import LUCENT.managedElement.ManagedElement_T;
import LUCENT.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import LUCENT.terminationPoint.TerminationPoint_T;
import LUCENT.topologicalLink.TopologicalLink_T;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;

public interface ILUCENTEMSSession extends IEMSSession{

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

	/** #查询指定保护组信息# */
	public ProtectionGroup_T getProtectionGroup(NameAndStringValue_T[] pgName) throws CommonException;

	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public SwitchData_T[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException;

	public String[] acknowledgeAlarms(List<String> alarmList) throws CommonException;
}