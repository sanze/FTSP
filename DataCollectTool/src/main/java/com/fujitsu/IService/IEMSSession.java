package com.fujitsu.IService;

import java.util.List;

import globaldefs.NameAndStringValue_T;

import ALU.subnetworkConnection.SubnetworkConnection_T;

import com.fujitsu.common.CommonException;


/**
 * @author xuxiaojun
 * 
 */
public interface IEMSSession {
	/**	########EmsMgr######*/
	/** 获取支持的管理器 */
	public Object[] getSupportedManagers() throws CommonException;
	/** #查询网管信息# */
	public Object getEMS() throws CommonException;
	/** #查询当前所有告警# */
	public Object[] getAllEMSAndMEActiveAlarms() throws CommonException;
	
	public Object[] getAllEMSSystemActiveAlarms() throws CommonException;
	/** #查询EMS下顶层子网# */
	public Object[] getAllTopLevelSubnetworks() throws CommonException;
	/** #查询EMS下顶层子网名# */
	public Object[] getAllTopLevelSubnetworkNames() throws CommonException;
	/** #查询所有跨EMS间拓扑连接# */
	public Object[] getAllTopLevelTopologicalLinks() throws CommonException;
	
	/**	########ManagedElementMgr###### */
	/** #同步当前告警数据# */
	public Object[] getAllActiveAlarms(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询单网元路由的子交叉# */
	public Object[] getAllCrossConnections(
			NameAndStringValue_T[] neName, short[] connectionRateList) throws CommonException;

	/** #查询所有网元信息# */
	public Object[] getAllManagedElements() throws CommonException;

	/** #查询指定网元信息# */
	public Object getManagedElement(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询所有网元名# */
	public Object[] getAllManagedElementNames() throws CommonException;

	/** #查询物理终端点配置信息# */
	public Object[] getAllPTPs(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询物理终端点名# */
	public Object[] getAllPTPNames(NameAndStringValue_T[] neName) throws CommonException;

	/** #查询指定速率下包含的潜在CTP# */
	public Object[] getContainedPotentialTPs(
			NameAndStringValue_T[] tpName) throws CommonException;

	/**	########EquipmentMgr##### */
	/** #查询指定实体下所有设备# */
	public Object[] getAllEquipment(
			NameAndStringValue_T[] meOrHoderName) throws CommonException;

	/**	########MultilayerSubnetworkMgr##### */
	/** #查询子网下所有拓扑连接# */
	public Object[] getAllTopologicalLinks(
			NameAndStringValue_T[] subnetName) throws CommonException;

	public Object[] getAllInternalTopologicalLinks(
			NameAndStringValue_T[] meName) throws CommonException;
	
	/** #查询所有子网连接# */
	public Object[] getAllSubnetworkConnections(NameAndStringValue_T[] subnetName) throws CommonException;
//	public Object[] getSNC() throws CommonException;
	public Object[] getRoute(NameAndStringValue_T[] sncName) throws CommonException;
//	public Object[] getAllEthernetSubnetworkConnections() throws CommonException;
	
	
	
	/**	########PerformanceMgr##### */
	/** #查询指定ptp当前性能# */
	//public PMData_T[] getAllCurrentPMData(List<PMTPSelect_T> selectTPList) throws CommonException;
	/** #查询指定网元当前性能# */
	public Object[] getAllCurrentPMData(NameAndStringValue_T[] name,
			short[] _layerRateList, String[] _pMLocationList,
			String[] _granularityList) throws CommonException;

	/** #查询历史性能# */
	public void getHistoryPMData(NameAndStringValue_T[] name,
			String ftpIpAndFileName, String userName, String password, 
			String startTime, String endTime, short[] _layerRateList, 
			String[] _pMLocationList, String[] _granularityList)
			throws CommonException;
	
	public void getHistoryPMData_NEs(List<NameAndStringValue_T[]> nameList,
			String ftpIpAndFileName, String userName, String password, 
			String startTime, String endTime, short[] _layerRateList, 
			String[] _pMLocationList, String[] _granularityList)
			throws CommonException;
	
	public Object[] getHistoryPMData(NameAndStringValue_T[] name,
			String startTime, String endTime, short[] _layerRateList, 
			String[] _pMLocationList, String[] _granularityList)
			throws CommonException;

	/** #查询EMS性能的保持时间# */
	public Object getHoldingTime() throws CommonException;

	/** #查询网元性能的能力# */
	public Object[] getMEPMcapabilities(NameAndStringValue_T[] neName) throws CommonException;

	
	/**	########ProtectionMgr##### */
	/** #查询ME下所有保护组信息# --中兴特有*/
	public Object getMEconfigData(
			NameAndStringValue_T[] neName) throws CommonException;
	
	/**	########ProtectionMgr##### */
	/** #查询ME下所有保护组信息# */
	public Object[] getAllProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException;

	/** #查询所有设备保护组信息# */
	public Object[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException;

	public Object[] getAllWDMProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException;
	
	/** #查询指定保护组信息# */
	public Object getProtectionGroup(NameAndStringValue_T[] pgName) throws CommonException;

	/** #查询指定设备保护组信息# */
	public Object getEProtectionGroup(NameAndStringValue_T[] epgName) throws CommonException;

	public Object getWDMProtectionGroup(NameAndStringValue_T[] wpgpName) throws CommonException;
	
	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public Object[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException;

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public Object[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException;
	
	public Object[] retrieveWDMSwitchData(NameAndStringValue_T[] wpgpName) throws CommonException;

	public Object[] getAllMstpEndPointNames(NameAndStringValue_T[] meName) throws CommonException;
	public Object[] getAllMstpEndPoints(NameAndStringValue_T[] meName) throws CommonException;
	
	public Object[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException;
	
	public Object[] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException;
	public Object[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException;
	public Object getBindingPath(NameAndStringValue_T[] ptpName) throws CommonException;
	public Object[] getAllVLANs(NameAndStringValue_T[] meName) throws CommonException;
	
	/** #查询时钟源# */
	public Object[] getObjectClockSourceStatus(NameAndStringValue_T[] meName) throws CommonException;
	
	
	
	
	
	public Object getNmsSession();
	public Object getEmsSession();
	public boolean isEmsSessionInvalid();
	/**
	 * 启动corba服务和通知服务
	 * @throws CommonException
	 */
	public boolean connect() throws CommonException;
	/**
	 * 启动corba服务
	 * @throws CommonException
	 */
	public void startUpCorbaConnect() throws CommonException;
	
	/**
	 *  启动通知服务
	 * @throws CommonException
	 */
	public void startUpNotification() throws CommonException;

	/** 关闭corba连接 */
	public void endSession() throws CommonException;

}
