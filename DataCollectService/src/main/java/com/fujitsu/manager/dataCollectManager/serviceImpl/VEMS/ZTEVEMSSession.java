package com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesList_THelper;
import globaldefs.StringList_THelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ZTE_U31.CosNotification.StructuredEvent;
import ZTE_U31.CosNotification.StructuredEventHelper;
import ZTE_U31.clocksource.ClockSourceList_THelper;
import ZTE_U31.clocksource.ClockSource_T;
import ZTE_U31.emsMgr.EMS_T;
import ZTE_U31.emsMgr.EMS_THelper;
import ZTE_U31.emsSession.EmsSession_I;
import ZTE_U31.equipment.EquipmentOrHolderList_THelper;
import ZTE_U31.equipment.EquipmentOrHolder_T;
import ZTE_U31.ethernet.VBList_THelper;
import ZTE_U31.ethernet.VB_T;
import ZTE_U31.ethernet.VLANList_THelper;
import ZTE_U31.ethernet.VLAN_T;
import ZTE_U31.managedElement.ManagedElementList_THelper;
import ZTE_U31.managedElement.ManagedElement_T;
import ZTE_U31.managedElement.ManagedElement_THelper;
import ZTE_U31.managedElementManager.MEConfigData_T;
import ZTE_U31.managedElementManager.MEConfigData_THelper;
import ZTE_U31.mstpcommon.EthernetServiceList_THelper;
import ZTE_U31.mstpcommon.EthernetService_T;
import ZTE_U31.mstpcommon.VCGBinding_T;
import ZTE_U31.mstpcommon.VCGBinding_THelper;
import ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import ZTE_U31.multiLayerSubnetwork.SubnetworkList_THelper;
import ZTE_U31.notifications.EventList_THelper;
import ZTE_U31.performance.PMDataList_THelper;
import ZTE_U31.performance.PMData_T;
import ZTE_U31.performance.PMTPSelect_T;
import ZTE_U31.protection.EProtectionGroupList_THelper;
import ZTE_U31.protection.EProtectionGroup_T;
import ZTE_U31.protection.ESwitchDataList_THelper;
import ZTE_U31.protection.ESwitchData_T;
import ZTE_U31.subnetworkConnection.CrossConnectList_THelper;
import ZTE_U31.subnetworkConnection.CrossConnect_T;
import ZTE_U31.subnetworkConnection.SubnetworkConnectionList_THelper;
import ZTE_U31.subnetworkConnection.SubnetworkConnection_T;
import ZTE_U31.terminationPoint.TerminationPointList_THelper;
import ZTE_U31.terminationPoint.TerminationPoint_T;
import ZTE_U31.topologicalLink.TopologicalLinkList_THelper;
import ZTE_U31.topologicalLink.TopologicalLink_T;
import ZTE_U31.wdmConfig.ProtectInfoList_THelper;
import ZTE_U31.wdmConfig.ProtectInfo_T;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;

public class ZTEVEMSSession extends VEMSSession implements IZTEEMSSession,EmsSession_I{
	
	public static HashMap<String, Class> CmdObjectHelper = new HashMap<String, Class>();
	static{
		CmdObjectHelper.put(notificationFlag,StructuredEventHelper.class);
		CmdObjectHelper.put(getSupportedManagers, StringList_THelper.class);
		CmdObjectHelper.put(getEms, EMS_THelper.class);
		CmdObjectHelper.put(getAllEMSAndMEActiveAlarms,EventList_THelper.class);
		CmdObjectHelper.put(getAllEMSSystemActiveAlarms,EventList_THelper.class);
		CmdObjectHelper.put(getAllTopLevelSubnetworks,SubnetworkList_THelper.class);
		CmdObjectHelper.put(getAllTopLevelSubnetworkNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(getAllTopLevelTopologicalLinks,TopologicalLinkList_THelper.class);
		CmdObjectHelper.put(getAllManagedElements,ManagedElementList_THelper.class);
		CmdObjectHelper.put(getAllManagedElementNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(getAllTopologicalLinks,TopologicalLinkList_THelper.class);
		//CmdObjectHelper.put(getHoldingTime,HoldingTime_THelper.class);
		CmdObjectHelper.put(getManagedElement,ManagedElement_THelper.class);
		CmdObjectHelper.put(getAllActiveAlarms,EventList_THelper.class);
		//CmdObjectHelper.put(getAllInternalTopologicalLinks,TopologicalLinkList_THelper.class);
		CmdObjectHelper.put(getAllEquipment,EquipmentOrHolderList_THelper.class);
		CmdObjectHelper.put(getAllPTPs,TerminationPointList_THelper.class);
		CmdObjectHelper.put(getAllPTPNames,NamingAttributesList_THelper.class);
		CmdObjectHelper.put(getContainedPotentialTPs,TerminationPointList_THelper.class);
		CmdObjectHelper.put(getAllCrossConnections,CrossConnectList_THelper.class);
		CmdObjectHelper.put(getAllCurrentPMData_Ne.split("_")[0],PMDataList_THelper.class);
		//CmdObjectHelper.put(getAllCurrentPMData_Ptp,PMDataList_THelper.class);
		//CmdObjectHelper.put(getAllCurrentPMData_Equip,PMDataList_THelper.class);
		CmdObjectHelper.put(getHistoryPMData,PMDataList_THelper.class);
		//CmdObjectHelper.put(getMEPMcapabilities,PMParameterList_THelper.class);
		//CmdObjectHelper.put(getAllProtectionGroups,ProtectionGroupList_THelper.class);
		CmdObjectHelper.put(getAllEProtectionGroups,EProtectionGroupList_THelper.class);
		CmdObjectHelper.put(getAllWDMProtectionGroups,ProtectInfoList_THelper.class);
		//CmdObjectHelper.put(retrieveSwitchData,SwitchDataList_THelper.class);
		CmdObjectHelper.put(retrieveESwitchData,ESwitchDataList_THelper.class);
		//retrieveWDMSwitchData
		CmdObjectHelper.put(getObjectClockSourceStatus,ClockSourceList_THelper.class);
		//getAllMstpEndPointNames
		//getAllMstpEndPoints
		CmdObjectHelper.put(getAllEthService, EthernetServiceList_THelper.class);
		CmdObjectHelper.put(getAllVBNames, NamingAttributesList_THelper.class);
		CmdObjectHelper.put(getAllVBs, VBList_THelper.class);
		CmdObjectHelper.put(getBindingPath, VCGBinding_THelper.class);
		CmdObjectHelper.put(getAllVLANs,VLANList_THelper.class);
		CmdObjectHelper.put(getMEconfigData,MEConfigData_THelper.class);
		CmdObjectHelper.put(getAllSubnetworkConnections,SubnetworkConnectionList_THelper.class);
		CmdObjectHelper.put(getRoute,CrossConnectList_THelper.class);
	}

	/** 关闭corba连接 */
	public void endSession(int sessionID) {
		super.endSession();
	}
	public void getManager (String managerName, ZTE_U31.common.Common_IHolder managerInterface)
		throws globaldefs.ProcessingFailureException{
	} // getManager
	public ZTE_U31.session.Session_I associatedSession (){
		return null;
	} // associatedSession
	public void getEventChannel (ZTE_U31.CosNotifyChannelAdmin.EventChannelHolder eventChannel)
		throws globaldefs.ProcessingFailureException{
	} // getEventChannel
	public void getSupportedManagers (ZTE_U31.emsSession.EmsSession_IPackage.managerNames_THolder supportedManagerList)
		throws globaldefs.ProcessingFailureException{
	} // getSupportedManagers

	public static IZTEEMSSession newInstance(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName, String encode){
		if(!EMSCollectService.sessionMap.containsKey(corbaIp)
				||!ZTEVEMSSession.class.isInstance(EMSCollectService.sessionMap.get(corbaIp))){
			EMSCollectService.sessionMap.put(corbaIp, new ZTEVEMSSession(
					corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode));
		}
		return (IZTEEMSSession)EMSCollectService.sessionMap.get(corbaIp);
	}
	private ZTEVEMSSession(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName, String encode) {
		super(corbaName, corbaPassword, corbaIp, corbaPort, emsName, encode, DataCollectDefine.FACTORY_ZTE_FLAG);
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
			object=new NameAndStringValue_T[][]{};
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
					ZTE_U31.equipment.EquipmentTypeQualifier_T._EQT)
					field.set(datas[i], 
							ZTE_U31.equipment.EquipmentTypeQualifier_T.EQT);
				else if(datas[i].discriminator().value()==
					ZTE_U31.equipment.EquipmentTypeQualifier_T._EQT_HOLDER)
					field.set(datas[i], 
							ZTE_U31.equipment.EquipmentTypeQualifier_T.EQT_HOLDER);
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
	public PMData_T[] getHistoryPMData(NameAndStringValue_T[] name,
			String startTime, String endTime, short[] layerRateList,
			String[] pmLocationList,String[] pmGranularityList)
			throws CommonException {
		Object object=getData(getHistoryPMData, name);
		if(object==null)
			object=new ClockSource_T[]{};
		return (PMData_T[])object;
	}
	
	/** #查询EMS下所有设备时钟源# */
	public ClockSource_T[] getObjectClockSourceStatus(NameAndStringValue_T[] name) throws CommonException{
		Object object=getData(getObjectClockSourceStatus, name);
		if(object==null)
			object=new ClockSource_T[]{};
		return (ClockSource_T[])object;
	}
	
	/** #查询所有设备保护组信息# */
	public EProtectionGroup_T[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		Object object=getData(getAllEProtectionGroups, neName);
		if(object==null)
			object=new EProtectionGroup_T[]{};
		return (EProtectionGroup_T[])object;
	}
	public ProtectInfo_T[] getAllWDMProtectionGroups(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllWDMProtectionGroups, meName);
		if(object==null)
			object=new ProtectInfo_T[]{};
		return (ProtectInfo_T[])object;
	}
	
	public EthernetService_T[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllEthService, meName);
		if(object==null)
			object=new EthernetService_T[]{};
		return (EthernetService_T[])object;
	}
	
	public NameAndStringValue_T[][] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllVBNames, meName);
		if(object==null)
			object=new NameAndStringValue_T[][]{};
		return (NameAndStringValue_T[][])object;
	}
	public VB_T[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllVBs, meName);
		if(object==null)
			object=new VB_T[]{};
		return (VB_T[])object;
	}
	public VLAN_T[] getAllVLANs(NameAndStringValue_T[] meName) throws CommonException {
		Object object=getData(getAllVLANs, meName);
		if(object==null)
			object=new VLAN_T[]{};
		return (VLAN_T[])object;
	}

	// getMEconfigData  获取网元保护组信息
	public MEConfigData_T getMEconfigData(
			NameAndStringValue_T[] meName) throws CommonException{
		
		return (MEConfigData_T)getData(getMEconfigData, meName);
	}

	/**	########ProtectionMgr##### */
	/** #查询指定设备保护组信息# */
	public EProtectionGroup_T getEProtectionGroup(
			NameAndStringValue_T[] epgName) throws CommonException{
		
		return (EProtectionGroup_T)getData(getEProtectionGroup, epgName);
	}

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public ESwitchData_T[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException{
		
		return new ESwitchData_T[]{};
	}

	public VCGBinding_T getBindingPath(NameAndStringValue_T[] ptpName) throws CommonException {
		return (VCGBinding_T)getData(getBindingPath, ptpName);
	}
	
	public TopologicalLink_T[] getAllInternalTopologicalLinks(
			NameAndStringValue_T[] meName) throws CommonException {
		return new TopologicalLink_T[]{};
		//return (TopologicalLink_T[])getData(getAllInternalTopologicalLinks, meName);
	}
	@Override
	public SubnetworkConnection_T[] getAllSubnetworkConnections(
			NameAndStringValue_T[] subnetName) throws CommonException {
		
		return (SubnetworkConnection_T[] )getData(getAllSubnetworkConnections, subnetName);
	}
	@Override
	public CrossConnect_T[] getRoute(NameAndStringValue_T[] sncName) throws CommonException{
		
		return (CrossConnect_T[] )getData(getRoute, sncName);
	}
}