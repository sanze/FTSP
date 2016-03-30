package com.fujitsu.serviceImpl.VEMS;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesList_THelper;
import globaldefs.StringList_THelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import HW.subnetworkConnection.SubnetworkConnection_THelper;
import HW.subnetworkConnection.SubnetworkConnectionList_THelper;
import HW.subnetworkConnection.SubnetworkConnection_T;
import HW.CosNotification.StructuredEventHelper;
import HW.CosNotification.StructuredEvent;
import HW.HW_mstpInventory.HW_MSTPBindingPathList_THelper;
import HW.HW_mstpInventory.HW_MSTPBindingPath_T;
import HW.HW_mstpInventory.HW_MSTPEndPointList_THelper;
import HW.HW_mstpInventory.HW_MSTPEndPoint_T;
import HW.HW_mstpInventory.HW_VirtualBridgeList_THelper;
import HW.HW_mstpInventory.HW_VirtualBridge_T;
import HW.HW_mstpInventory.HW_VirtualLANList_THelper;
import HW.HW_mstpInventory.HW_VirtualLAN_T;
import HW.HW_mstpService.HW_EthServiceList_THelper;
import HW.HW_mstpService.HW_EthService_T;
import HW.emsMgr.ClockSourceStatusList_THelper;
import HW.emsMgr.ClockSourceStatus_T;
import HW.emsMgr.EMS_T;
import HW.emsMgr.EMS_THelper;
import HW.emsSession.EmsSession_I;
import HW.equipment.EquipmentOrHolderList_THelper;
import HW.equipment.EquipmentOrHolder_T;
import HW.managedElement.ManagedElementList_THelper;
import HW.managedElement.ManagedElement_T;
import HW.managedElement.ManagedElement_THelper;
import HW.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import HW.multiLayerSubnetwork.SubnetworkList_THelper;
import HW.notifications.EventList_THelper;
import HW.performance.HoldingTime_T;
import HW.performance.HoldingTime_THelper;
import HW.performance.PMDataList_THelper;
import HW.performance.PMData_T;
import HW.performance.PMParameterList_THelper;
import HW.performance.PMParameter_T;
import HW.performance.PMTPSelect_T;
import HW.protection.EProtectionGroupList_THelper;
import HW.protection.EProtectionGroup_T;
import HW.protection.ESwitchDataList_THelper;
import HW.protection.ESwitchData_T;
import HW.protection.ProtectionGroupList_THelper;
import HW.protection.ProtectionGroup_T;
import HW.protection.SwitchDataList_THelper;
import HW.protection.SwitchData_T;
import HW.protection.WDMProtectionGroupList_THelper;
import HW.protection.WDMProtectionGroup_T;
import HW.protection.WDMSwitchDataList_THelper;
import HW.protection.WDMSwitchData_T;
import HW.session.Session_I;
import HW.subnetworkConnection.CrossConnectList_THelper;
import HW.subnetworkConnection.CrossConnect_T;
import HW.terminationPoint.TerminationPointList_THelper;
import HW.terminationPoint.TerminationPoint_T;
import HW.topologicalLink.TopologicalLinkList_THelper;
import HW.topologicalLink.TopologicalLink_T;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu./*manager.dataCollectManager.*/service.EMSCollectService;

public class HWVEMSSession extends VEMSSession implements IHWEMSSession,EmsSession_I{
	
	public static HashMap<String, Class> CmdObjectHelper = new HashMap<String, Class>();
	static{
		CmdObjectHelper.put(notificationFlag,StructuredEventHelper.class);
		CmdObjectHelper.put(VEMSSession.getSupportedManagers, StringList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getEms, EMS_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllEMSAndMEActiveAlarms,EventList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllEMSSystemActiveAlarms,EventList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllTopLevelSubnetworks,SubnetworkList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllTopLevelSubnetworkNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllTopLevelTopologicalLinks,TopologicalLinkList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllManagedElements,ManagedElementList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllManagedElementNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllTopologicalLinks,TopologicalLinkList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getHoldingTime,HoldingTime_THelper.class);
		CmdObjectHelper.put(VEMSSession.getManagedElement,ManagedElement_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllActiveAlarms,EventList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllInternalTopologicalLinks,TopologicalLinkList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllEquipment,EquipmentOrHolderList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllPTPs,TerminationPointList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllPTPNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getContainedPotentialTPs,TerminationPointList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllCrossConnections,CrossConnectList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllCurrentPMData_Ne.split("_")[0],PMDataList_THelper.class);
		//CmdObjectHelper.put(VEMSSession.getAllCurrentPMData_Ptp,PMDataList_THelper.class);
		//CmdObjectHelper.put(VEMSSession.getAllCurrentPMData_Equip,PMDataList_THelper.class);
		//getHistoryPMData
		CmdObjectHelper.put(VEMSSession.getMEPMcapabilities,PMParameterList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllProtectionGroups,ProtectionGroupList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllEProtectionGroups,EProtectionGroupList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllWDMProtectionGroups,WDMProtectionGroupList_THelper.class);
		CmdObjectHelper.put(VEMSSession.retrieveSwitchData,SwitchDataList_THelper.class);
		CmdObjectHelper.put(VEMSSession.retrieveESwitchData,ESwitchDataList_THelper.class);
		CmdObjectHelper.put(VEMSSession.retrieveWDMSwitchData,WDMSwitchDataList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getObjectClockSourceStatus,ClockSourceStatusList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllMstpEndPointNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllMstpEndPoints,HW_MSTPEndPointList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllEthService,HW_EthServiceList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllVBNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllVBs,HW_VirtualBridgeList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getBindingPath,HW_MSTPBindingPathList_THelper.class);
		CmdObjectHelper.put(VEMSSession.getAllVLANs,HW_VirtualLANList_THelper.class);
		
		CmdObjectHelper.put(getAllSubnetworkConnections,SubnetworkConnectionList_THelper.class);
		CmdObjectHelper.put(getSNC,SubnetworkConnection_THelper.class);
		CmdObjectHelper.put(getRoute,CrossConnectList_THelper.class);
		CmdObjectHelper.put(getSNC,SubnetworkConnection_THelper.class);
	}
	
	public void getManager (String managerName, HW.common.Common_IHolder managerInterface)
	throws globaldefs.ProcessingFailureException{
	} // getManager
	public Session_I associatedSession (){
		return null;
	} // associatedSession
	public void getEventChannel (HW.CosNotifyChannelAdmin.EventChannelHolder eventChannel)
		throws globaldefs.ProcessingFailureException{
	} // getEventChannel
	public void getSupportedManagers (HW.emsSession.EmsSession_IPackage.managerNames_THolder supportedManagerList)
		throws globaldefs.ProcessingFailureException{
	} // getSupportedManagers
	
	public static IHWEMSSession newInstance(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName, String encode){
		if(!EMSCollectService.sessionMap.containsKey(corbaIp)){
			EMSCollectService.sessionMap.put(corbaIp, new HWVEMSSession(
					corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode));
		}
		return (IHWEMSSession)EMSCollectService.sessionMap.get(corbaIp);
	}
	private HWVEMSSession(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName, String encode) {
		super(corbaName, corbaPassword, corbaIp, corbaPort, emsName, encode, DataCollectDefine.FACTORY_HW_FLAG);
	}
	
	/**	########EmsMgr######*/
	/** 获取支持的管理器 */
	public String[] getSupportedManagers() throws CommonException{
		return (String[])getData(getSupportedManagers, null);
	}
	
	/** #查询网管信息# */
	public EMS_T getEMS() throws CommonException{
		
		return (EMS_T)getData(getEms, null);
	}
	
	/** #查询当前所有告警# */
	public StructuredEvent[] getAllEMSAndMEActiveAlarms() throws CommonException{
		Object object=getData(getAllEMSAndMEActiveAlarms, null);
		if(object==null)
			object=new StructuredEvent[]{};
		return (StructuredEvent[])object;
	}
	
	public StructuredEvent[] getAllEMSSystemActiveAlarms() throws CommonException{
		Object object=getData(getAllEMSSystemActiveAlarms, null);
		if(object==null)
			object=new StructuredEvent[]{};
		return (StructuredEvent[])object;
	}
	
	/** #查询EMS下顶层子网# */
	public MultiLayerSubnetwork_T[] getAllTopLevelSubnetworks() throws CommonException{
		Object object=getData(getAllTopLevelSubnetworks, null);
		if(object==null)
			object=new MultiLayerSubnetwork_T[]{};
		return (MultiLayerSubnetwork_T[])object;
	}
	
	/** #查询EMS下顶层子网名# */
	public NameAndStringValue_T[][] getAllTopLevelSubnetworkNames() throws CommonException{
		Object object=getData(getAllTopLevelSubnetworkNames, null);
		if(object==null)
			object=new NameAndStringValue_T[][]{};
		return (NameAndStringValue_T[][])object;
	}
	
	/** #查询所有跨EMS间拓扑连接# */
	public TopologicalLink_T[] getAllTopLevelTopologicalLinks() throws CommonException{
		Object object=getData(getAllTopLevelTopologicalLinks, null);
		if(object==null)
			object=new TopologicalLink_T[]{};
		return (TopologicalLink_T[])object;
	}
	
	/**	########ManagedElementMgr###### */
	/** #同步当前告警数据# */
	public StructuredEvent[] getAllActiveAlarms(NameAndStringValue_T[] neName) throws CommonException{
		Object object=getData(getAllActiveAlarms, neName);
		if(object==null)
			object=new StructuredEvent[]{};
		return (StructuredEvent[])object;
	}
	
	/** #查询单网元路由的子交叉# */
	public CrossConnect_T[] getAllCrossConnections(
			NameAndStringValue_T[] neName, short[] connectionRateList) throws CommonException{
		Object object=getData(getAllCrossConnections, neName);
		if(object==null)
			object=new CrossConnect_T[]{};
		return (CrossConnect_T[])object;
	}
	
	/** #查询所有网元信息# */
	public ManagedElement_T[] getAllManagedElements() throws CommonException{
		Object object=getData(getAllManagedElements, null);
		if(object==null)
			object=new ManagedElement_T[]{};
		return (ManagedElement_T[])object;
	}
	
	/** #查询物理终端点配置信息# */
	public TerminationPoint_T[] getAllPTPs(NameAndStringValue_T[] neName) throws CommonException{
		Object object=getData(getAllPTPs, neName);
		if(object==null)
			object=new TerminationPoint_T[]{};
		return (TerminationPoint_T[])object;
	}
	
	/** #查询物理终端点名# */
	public NameAndStringValue_T[][] getAllPTPNames(NameAndStringValue_T[] neName) throws CommonException{
		Object object=getData(getAllPTPNames, neName);
		if(object==null)
			object=new NameAndStringValue_T[][]{};
		return (NameAndStringValue_T[][])object;
	}
	
	/** #查询指定速率下包含的潜在CTP# */
	public TerminationPoint_T[] getContainedPotentialTPs(
			NameAndStringValue_T[] tpName) throws CommonException{
		Object object=getData(getContainedPotentialTPs, tpName);
		if(object==null)
			object=new TerminationPoint_T[]{};
		return (TerminationPoint_T[])object;
	}
	
	/** #查询指定网元信息# */
	public ManagedElement_T getManagedElement(NameAndStringValue_T[] neName) throws CommonException{
		
		return (ManagedElement_T)getData(getManagedElement, neName);
	}
	
	/** #查询所有网元名# */
	public NameAndStringValue_T[][] getAllManagedElementNames() throws CommonException{
		Object object=getData(getAllManagedElementNames, null);
		if(object==null)
			object=new TerminationPoint_T[]{};
		return (NameAndStringValue_T[][])object;
	}
	
	/**	########EquipmentMgr##### */
	/** #查询指定实体下所有设备# */
	public EquipmentOrHolder_T[] getAllEquipment(
			NameAndStringValue_T[] meOrHoderName) throws CommonException{
		Object object=getData(getAllEquipment, meOrHoderName);
		if(object==null)
			object=new EquipmentOrHolder_T[]{};
		return (EquipmentOrHolder_T[])object;
		//重新设置__discriminator
		/*try{
			Field field = EquipmentOrHolder_T.class.getDeclaredField("__discriminator");
			field.setAccessible(true);
			for(int i=0;datas!=null&&i<datas.length;i++){
				if(datas[i].discriminator().value()==
					HW.equipment.EquipmentTypeQualifier_T._EQT)
					field.set(datas[i], 
							HW.equipment.EquipmentTypeQualifier_T.EQT);
				else if(datas[i].discriminator().value()==
					HW.equipment.EquipmentTypeQualifier_T._EQT_HOLDER)
					field.set(datas[i], 
							HW.equipment.EquipmentTypeQualifier_T.EQT_HOLDER);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	/**	########MultilayerSubnetworkMgr##### */
	/** #查询子网下所有拓扑连接# */
	public TopologicalLink_T[] getAllTopologicalLinks(
			NameAndStringValue_T[] subnetName) throws CommonException{
		Object object=getData(getAllTopologicalLinks, subnetName);
		if(object==null)
			object=new TopologicalLink_T[]{};
		return (TopologicalLink_T[])object;
	}
	
	/**	########PerformanceMgr##### */
	/** #查询指定ptp当前性能# */
	public PMData_T[] getAllCurrentPMData(List<PMTPSelect_T> selectTPList) throws CommonException{
		PMData_T[] datas = null;
		List<PMData_T> dataList = new ArrayList<PMData_T>();
		for(PMTPSelect_T item:selectTPList){
			String[] _granularityList = item.granularityList;
			if(_granularityList==null||_granularityList.length==0){
				_granularityList = new String[]{DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING};
			}
			PMData_T[] tmpData=(PMData_T[])getData(getAllCurrentPMData_Ne.split("_")[0]+"_"+_granularityList[0], item.name);
			if(tmpData!=null){
				dataList.addAll(Arrays.asList(tmpData));
			}
		}
		datas = new PMData_T[dataList.size()];
		datas = (PMData_T[])dataList.toArray(datas);
		return datas;
	}
	
	/** #查询指定网元当前性能# */
	public PMData_T[] getAllCurrentPMData(NameAndStringValue_T[] name,
			short[] _layerRateList, String[] _pMLocationList,
			String[] _granularityList) throws CommonException{
		if(_granularityList==null||_granularityList.length==0){
			_granularityList = new String[]{DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING};
		}
		Object object=getData(getAllCurrentPMData_Ne.split("_")[0]+"_"+_granularityList[0], name);
		if(object==null)
			object=new PMData_T[]{};
		return (PMData_T[])object;
	}
	
	/** #查询历史性能# */
	public void getHistoryPMData(NameAndStringValue_T[] name,
			String ftpIpAndFileName, String userName, String password, 
			String startTime, String endTime, short[] _layerRateList, 
			String[] _pMLocationList, String[] _granularityList)
			throws CommonException{
		getHistoryPMFile(ftpIpAndFileName,userName,password);
	}
	
	/** #查询历史性能# */
	public void getHistoryPMData_NEs(List<NameAndStringValue_T[]> nameList,
			String ftpIpAndFileName, String userName, String password, 
			String startTime, String endTime, short[] _layerRateList, 
			String[] _pMLocationList, String[] _granularityList)
			throws CommonException{
		getHistoryPMFile(ftpIpAndFileName,userName,password);
	}
	
	/** #查询EMS性能的保持时间# */
	public HoldingTime_T getHoldingTime() throws CommonException{
		
		return (HoldingTime_T)getData(getHoldingTime, null);
	}
	
	/** #查询网元性能的能力# */
	public PMParameter_T[] getMEPMcapabilities(NameAndStringValue_T[] neName) throws CommonException{
		Object object=getData(getMEPMcapabilities, neName);
		if(object==null)
			object=new PMParameter_T[]{};
		return (PMParameter_T[])object;
	}
	
	/**	########ProtectionMgr##### */
	/** #查询ME下所有保护组信息# */
	public ProtectionGroup_T[] getAllProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		Object object=getData(getAllProtectionGroups, neName);
		if(object==null)
			object=new ProtectionGroup_T[]{};
		return (ProtectionGroup_T[])object;
	}
	
	/** #查询所有设备保护组信息# */
	public EProtectionGroup_T[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		Object object=getData(getAllEProtectionGroups, neName);
		if(object==null)
			object=new EProtectionGroup_T[]{};
		return (EProtectionGroup_T[])object;
	}
	
	/** #查询指定保护组信息# */
	public ProtectionGroup_T getProtectionGroup(NameAndStringValue_T[] pgName) throws CommonException{
		
		return (ProtectionGroup_T)getData(getProtectionGroup, pgName);
	}
	
	/** #查询指定设备保护组信息# */
	public EProtectionGroup_T getEProtectionGroup(
			NameAndStringValue_T[] epgName) throws CommonException{
		
		return (EProtectionGroup_T)getData(getEProtectionGroup, epgName);
	}
	
	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public SwitchData_T[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException{
		Object object=getData(retrieveSwitchData, pgName);
		if(object==null)
			object=new SwitchData_T[]{};
		return (SwitchData_T[])object;
	}
	
	/** #获取指定对象的设备保护数据#参数ePGPName */
	public ESwitchData_T[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException{
		Object object=getData(retrieveESwitchData, epgName);
		if(object==null)
			object=new ESwitchData_T[]{};
		return (ESwitchData_T[])object;
	}
	
	/** #查询EMS下所有设备时钟源# */
	public ClockSourceStatus_T[] getObjectClockSourceStatus(NameAndStringValue_T[] name) throws CommonException{
		Object object=getData(getObjectClockSourceStatus, name);
		if(object==null)
			object=new ClockSourceStatus_T[]{};
		return (ClockSourceStatus_T[])object;
	}
	
	public TopologicalLink_T[] getAllInternalTopologicalLinks(
			NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllInternalTopologicalLinks, meName);
		if(object==null)
			object=new TopologicalLink_T[]{};
		return (TopologicalLink_T[])object;
	}
	
	public WDMProtectionGroup_T[] getAllWDMProtectionGroups(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllWDMProtectionGroups, meName);
		if(object==null)
			object=new WDMProtectionGroup_T[]{};
		return (WDMProtectionGroup_T[])object;
	}
	
	public WDMProtectionGroup_T getWDMProtectionGroup(NameAndStringValue_T[] wpgpName) throws CommonException {
		return (WDMProtectionGroup_T)getData(getWDMProtectionGroup, wpgpName);
	}
	
	public WDMSwitchData_T[] retrieveWDMSwitchData(NameAndStringValue_T[] wpgpName) throws CommonException {
		Object object=getData(retrieveWDMSwitchData, wpgpName);
		if(object==null)
			object=new WDMSwitchData_T[]{};
		return (WDMSwitchData_T[])object;
	}
	
	public NameAndStringValue_T[][] getAllMstpEndPointNames(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllMstpEndPointNames, meName);
		if(object==null)
			object=new NameAndStringValue_T[][]{};
		return (NameAndStringValue_T[][])object;
	}
	public HW_MSTPEndPoint_T[] getAllMstpEndPoints(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllMstpEndPoints, meName);
		if(object==null)
			object=new HW_MSTPEndPoint_T[]{};
		return (HW_MSTPEndPoint_T[])object;
	}
	
	public HW_EthService_T[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllEthService, meName);
		if(object==null)
			object=new HW_EthService_T[]{};
		return (HW_EthService_T[])object;
	}
	
	public NameAndStringValue_T[][] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllVBNames, meName);
		if(object==null)
			object=new NameAndStringValue_T[][]{};
		return (NameAndStringValue_T[][])object;
	}
	public HW_VirtualBridge_T[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllVBs, meName);
		if(object==null)
			object=new HW_VirtualBridge_T[]{};
		return (HW_VirtualBridge_T[])object;
	}
	public HW_MSTPBindingPath_T[] getBindingPath(NameAndStringValue_T[] ptpName) throws CommonException {
		Object object=getData(getBindingPath, ptpName);
		if(object==null)
			object=new HW_MSTPBindingPath_T[]{};
		return (HW_MSTPBindingPath_T[])object;
	}
	public HW_VirtualLAN_T[] getAllVLANs(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllVLANs, meName);
		if(object==null)
			object=new HW_VirtualLAN_T[]{};
		return (HW_VirtualLAN_T[])object;
	}
	public String[] acknowledgeAlarms(List<String> alarmList) throws CommonException {
		return new String[]{};
	}
	
	@Override
	public SubnetworkConnection_T[] getAllSubnetworkConnections(NameAndStringValue_T[] subnetName)
			throws CommonException {
		Object object=getData(getAllSubnetworkConnections,subnetName);
		if(object==null)
			object=new SubnetworkConnection_T[]{};
		return (SubnetworkConnection_T[])object;
	}
	@Override
	public Object[] getRoute(NameAndStringValue_T[] sncName) throws CommonException{
		Object object=getData(getRoute,sncName);
		if(object==null)
			object=new CrossConnect_T[]{};
		return (CrossConnect_T[])object;
	}
	
	@Override
	public Object[] getAllEthernetSubnetworkConnections()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}
}