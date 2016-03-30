package com.fujitsu.serviceImpl.VEMS;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesList_THelper;
import globaldefs.StringList_THelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import FENGHUO.CosNotification.StructuredEvent;
import FENGHUO.CosNotification.StructuredEventHelper;
import FENGHUO.emsMgr.EMS_T;
import FENGHUO.emsMgr.EMS_THelper;
import FENGHUO.emsSession.EmsSession_I;
import FENGHUO.equipment.EquipmentOrHolderList_THelper;
import FENGHUO.equipment.EquipmentOrHolder_T;
import FENGHUO.extendedManagedElementManager.EquipmentClockList_THelper;
import FENGHUO.extendedManagedElementManager.EquipmentClock_T;
import FENGHUO.managedElement.ManagedElementList_THelper;
import FENGHUO.managedElement.ManagedElement_T;
import FENGHUO.managedElement.ManagedElement_THelper;
import FENGHUO.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import FENGHUO.multiLayerSubnetwork.SubnetworkList_THelper;
import FENGHUO.notifications.EventList_THelper;
import FENGHUO.performance.HoldingTime_T;
import FENGHUO.performance.HoldingTime_THelper;
import FENGHUO.performance.PMDataList_THelper;
import FENGHUO.performance.PMData_T;
import FENGHUO.performance.PMParameterList_THelper;
import FENGHUO.performance.PMParameter_T;
import FENGHUO.performance.PMTPSelect_T;
import FENGHUO.protection.EProtectionGroupList_THelper;
import FENGHUO.protection.EProtectionGroup_T;
import FENGHUO.protection.ESwitchData_T;
import FENGHUO.protection.ProtectionGroupList_THelper;
import FENGHUO.protection.ProtectionGroup_T;
import FENGHUO.protection.SwitchDataList_THelper;
import FENGHUO.protection.SwitchData_T;
import FENGHUO.subnetworkConnection.CrossConnectList_THelper;
import FENGHUO.subnetworkConnection.CrossConnect_T;
import FENGHUO.subnetworkConnection.RouteList_THelper;
import FENGHUO.subnetworkConnection.SubnetworkConnectionList_THelper;
import FENGHUO.subnetworkConnection.SubnetworkConnection_T;
import FENGHUO.terminationPoint.TerminationPointList_THelper;
import FENGHUO.terminationPoint.TerminationPoint_T;
import FENGHUO.topologicalLink.TopologicalLinkList_THelper;
import FENGHUO.topologicalLink.TopologicalLink_T;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.service.EMSCollectService;

public class FIMVEMSSession extends VEMSSession implements IFIMEMSSession,EmsSession_I{
	
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
		CmdObjectHelper.put(getHoldingTime,HoldingTime_THelper.class);
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
		//getHistoryPMData
		CmdObjectHelper.put(getMEPMcapabilities,PMParameterList_THelper.class);
		CmdObjectHelper.put(getAllProtectionGroups,ProtectionGroupList_THelper.class);
		CmdObjectHelper.put(getAllEProtectionGroups,EProtectionGroupList_THelper.class);
		//getAllWDMProtectionGroups
		CmdObjectHelper.put(retrieveSwitchData,SwitchDataList_THelper.class);
		//CmdObjectHelper.put(retrieveESwitchData,ESwitchDataList_THelper.class);
		//retrieveWDMSwitchData
		CmdObjectHelper.put(getObjectClockSourceStatus,EquipmentClockList_THelper.class);
		//getAllMstpEndPointNames
		//getAllMstpEndPoints
		//getAllEthService
		//getAllVBNames
		//getAllVBs
		//getBindingPath
		CmdObjectHelper.put(getAllSubnetworkConnections,SubnetworkConnectionList_THelper.class);
		CmdObjectHelper.put(getSNC,SubnetworkConnectionList_THelper.class);
		CmdObjectHelper.put(getRoute,RouteList_THelper.class);
	}
	
	public void getManager (String managerName, FENGHUO.common.Common_IHolder managerInterface)
		throws globaldefs.ProcessingFailureException{
	} // getManager
	public FENGHUO.session.Session_I associatedSession (){
		return null;
	} // associatedSession
	public void getEventChannel (FENGHUO.CosNotifyChannelAdmin.EventChannelHolder eventChannel)
		throws globaldefs.ProcessingFailureException{
	} // getEventChannel
	public void getSupportedManagers (FENGHUO.emsSession.EmsSession_IPackage.managerNames_THolder supportedManagerList)
		throws globaldefs.ProcessingFailureException{
	} // getSupportedManagers

	public static IFIMEMSSession newInstance(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName, String encode){
		if(!EMSCollectService.sessionMap.containsKey(corbaIp)){
			EMSCollectService.sessionMap.put(corbaIp, new FIMVEMSSession(
					corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode));
		}
		return (IFIMEMSSession)EMSCollectService.sessionMap.get(corbaIp);
	}
	private FIMVEMSSession(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName, String encode) {
		super(corbaName, corbaPassword, corbaIp, corbaPort, emsName, encode, DataCollectDefine.FACTORY_FIBERHOME_FLAG);
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
		
		return new ESwitchData_T[]{};
	}
	
	/** #查询EMS下所有设备时钟源# */
	public EquipmentClock_T[] getObjectClockSourceStatus(NameAndStringValue_T[] name) throws CommonException{
		Object object=getData(getObjectClockSourceStatus, name);
		if(object==null)
			object=new EquipmentClock_T[]{};
		return (EquipmentClock_T[])object;
	}
	
	@Override
	public SubnetworkConnection_T[] getAllSubnetworkConnections(NameAndStringValue_T[] subnetName)
			throws CommonException {
		Object object=getData(getAllSubnetworkConnections,null);
		if(object==null)
			object=new SubnetworkConnection_T[]{};
		return (SubnetworkConnection_T[])object;
	}
	
	@Override
	public CrossConnect_T[][] getRoute(NameAndStringValue_T[] sncName) throws CommonException {
			Object object=getData(getRoute,null);
			if(object==null)
				object=new CrossConnect_T[]{};
			return (CrossConnect_T[][])object;
	}
	
	@Override
	public Object[] getAllEthernetSubnetworkConnections()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}
}