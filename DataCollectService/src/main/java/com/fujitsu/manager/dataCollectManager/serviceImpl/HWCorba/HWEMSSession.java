package com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesIterator_IHolder;
import globaldefs.NamingAttributesList_THolder;
import globaldefs.ProcessingFailureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.IntHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import HW.CosEventChannelAdmin.AlreadyConnected;
import HW.CosEventChannelAdmin.TypeError;
import HW.CosNotification.EventTypeSeqHolder;
import HW.CosNotification.StructuredEvent;
import HW.CosNotification._EventType;
import HW.CosNotifyChannelAdmin.AdminLimitExceeded;
import HW.CosNotifyChannelAdmin.AdminNotFound;
import HW.CosNotifyChannelAdmin.ClientType;
import HW.CosNotifyChannelAdmin.ConsumerAdmin;
import HW.CosNotifyChannelAdmin.EventChannelHolder;
import HW.CosNotifyChannelAdmin.InterFilterGroupOperator;
import HW.CosNotifyChannelAdmin.ProxySupplier;
import HW.CosNotifyChannelAdmin.StructuredProxyPushSupplier;
import HW.CosNotifyChannelAdmin.StructuredProxyPushSupplierHelper;
import HW.CosNotifyComm.InvalidEventType;
import HW.CosNotifyComm.StructuredPushConsumer;
import HW.CosNotifyComm.StructuredPushConsumerHelper;
import HW.HW_controlPlane.HW_controlPlaneMgr_I;
import HW.HW_controlPlane.HW_controlPlaneMgr_IHelper;
import HW.HW_mstpInventory.HW_MSTPBindingPathList_THolder;
import HW.HW_mstpInventory.HW_MSTPBindingPath_T;
import HW.HW_mstpInventory.HW_MSTPEndPointIterator_IHolder;
import HW.HW_mstpInventory.HW_MSTPEndPointList_THolder;
import HW.HW_mstpInventory.HW_MSTPEndPointType_T;
import HW.HW_mstpInventory.HW_MSTPEndPoint_T;
import HW.HW_mstpInventory.HW_MSTPInventoryMgr_I;
import HW.HW_mstpInventory.HW_MSTPInventoryMgr_IHelper;
import HW.HW_mstpInventory.HW_VirtualBridgeIterator_IHolder;
import HW.HW_mstpInventory.HW_VirtualBridgeList_THolder;
import HW.HW_mstpInventory.HW_VirtualBridge_T;
import HW.HW_mstpInventory.HW_VirtualLANIterator_IHolder;
import HW.HW_mstpInventory.HW_VirtualLANList_THolder;
import HW.HW_mstpInventory.HW_VirtualLAN_T;
import HW.HW_mstpProtection.HW_MSTPProtectionMgr_I;
import HW.HW_mstpProtection.HW_MSTPProtectionMgr_IHelper;
import HW.HW_mstpService.HW_EthServiceIterator_IHolder;
import HW.HW_mstpService.HW_EthServiceList_THolder;
import HW.HW_mstpService.HW_EthServiceType_T;
import HW.HW_mstpService.HW_EthService_T;
import HW.HW_mstpService.HW_EthService_THolder;
import HW.HW_mstpService.HW_MSTPServiceMgr_I;
import HW.HW_mstpService.HW_MSTPServiceMgr_IHelper;
import HW.TopoManagementManager.NodeIterator_IHolder;
import HW.TopoManagementManager.NodeList_THolder;
import HW.TopoManagementManager.Node_T;
import HW.TopoManagementManager.TopoMgr_I;
import HW.TopoManagementManager.TopoMgr_IHelper;
import HW.common.Common_IHolder;
import HW.emsMgr.AlarmSerialNoList_THolder;
import HW.emsMgr.ClockSourceStatusList_THolder;
import HW.emsMgr.ClockSourceStatus_T;
import HW.emsMgr.EMSMgr_I;
import HW.emsMgr.EMSMgr_IHelper;
import HW.emsMgr.EMS_T;
import HW.emsMgr.EMS_THolder;
import HW.emsSession.EmsSession_I;
import HW.emsSession.EmsSession_IHolder;
import HW.emsSession.EmsSession_IPackage.managerNames_THolder;
import HW.emsSessionFactory.EmsSessionFactory_I;
import HW.emsSessionFactory.EmsSessionFactory_IHelper;
import HW.encapsulationLayerLink.EncapsulationLayerLinkMgr_I;
import HW.encapsulationLayerLink.EncapsulationLayerLinkMgr_IHelper;
import HW.equipment.EquipmentInventoryMgr_I;
import HW.equipment.EquipmentInventoryMgr_IHelper;
import HW.equipment.EquipmentOrHolderIterator_IHolder;
import HW.equipment.EquipmentOrHolderList_THolder;
import HW.equipment.EquipmentOrHolder_T;
import HW.equipment.PhysicalLocationInfoList_THolder;
import HW.equipment.PhysicalLocationInfo_T;
import HW.flowDomain.FlowDomainMgr_I;
import HW.flowDomain.FlowDomainMgr_IHelper;
import HW.guiCutThrough.GuiCutThroughMgr_I;
import HW.guiCutThrough.GuiCutThroughMgr_IHelper;
import HW.maintenanceOps.MaintenanceMgr_I;
import HW.maintenanceOps.MaintenanceMgr_IHelper;
import HW.managedElement.ManagedElementIterator_IHolder;
import HW.managedElement.ManagedElementList_THolder;
import HW.managedElement.ManagedElement_T;
import HW.managedElement.ManagedElement_THolder;
import HW.managedElementManager.ManagedElementMgr_I;
import HW.managedElementManager.ManagedElementMgr_IHelper;
import HW.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import HW.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import HW.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import HW.multiLayerSubnetwork.SubnetworkIterator_IHolder;
import HW.multiLayerSubnetwork.SubnetworkList_THolder;
import HW.notifications.EventIterator_IHolder;
import HW.notifications.EventList_THolder;
import HW.notifications.PerceivedSeverity_T;
import HW.performance.HoldingTime_T;
import HW.performance.HoldingTime_THolder;
import HW.performance.PMDataIterator_IHolder;
import HW.performance.PMDataList_THolder;
import HW.performance.PMData_T;
import HW.performance.PMParameterList_THolder;
import HW.performance.PMParameter_T;
import HW.performance.PMTPSelect_T;
import HW.performance.PerformanceManagementMgr_I;
import HW.performance.PerformanceManagementMgr_IHelper;
import HW.protection.EProtectionGroupIterator_IHolder;
import HW.protection.EProtectionGroupList_THolder;
import HW.protection.EProtectionGroup_T;
import HW.protection.EProtectionGroup_THolder;
import HW.protection.ESwitchDataList_THolder;
import HW.protection.ESwitchData_T;
import HW.protection.ProtectionGroupIterator_IHolder;
import HW.protection.ProtectionGroupList_THolder;
import HW.protection.ProtectionGroup_T;
import HW.protection.ProtectionGroup_THolder;
import HW.protection.ProtectionMgr_I;
import HW.protection.ProtectionMgr_IHelper;
import HW.protection.SwitchDataList_THolder;
import HW.protection.SwitchData_T;
import HW.protection.WDMProtectionGroupIterator_IHolder;
import HW.protection.WDMProtectionGroupList_THolder;
import HW.protection.WDMProtectionGroup_T;
import HW.protection.WDMProtectionGroup_THolder;
import HW.protection.WDMSwitchDataList_THolder;
import HW.protection.WDMSwitchData_T;
import HW.subnetworkConnection.CCIterator_IHolder;
import HW.subnetworkConnection.CrossConnectList_THolder;
import HW.subnetworkConnection.CrossConnect_T;
import HW.terminationPoint.TerminationPointIterator_IHolder;
import HW.terminationPoint.TerminationPointList_THolder;
import HW.terminationPoint.TerminationPoint_T;
import HW.topologicalLink.TopologicalLinkIterator_IHolder;
import HW.topologicalLink.TopologicalLinkList_THolder;
import HW.topologicalLink.TopologicalLink_T;
import HW.trafficDescriptor.TrafficDescriptorMgr_I;
import HW.trafficDescriptor.TrafficDescriptorMgr_IHelper;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.service.CorbaThread;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.service.EMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.IHWEMSSession;
import com.fujitsu.util.NameAndStringValueUtil;

/*
 //supported manager names
 //"EMS" (mandatory)
 //"ManagedElement" (mandatory)
 //"MultiLayerSubnetwork" (mandatory)
 //"TrafficDescriptor"
 //"PerformanceManagement"
 //"Protection"
 //"EquipmentInventory"
 //"Maintenance"
 //"GuiCutThrough" (mandatory)
 //"CORBA_MSTP_INV" (defined by Huawei)
 //"CORBA_MSTP_PRO" (defined by Huawei)
 //"CORBA_MSTP_SVC" (defined by Huawei)
 //"CORBA_MSTP_TD" (defined by Huawei)
 //"ControlPlane" (defined by Huawei)
 //"TopoManagement" (defined by Huawei)
 //"ELLManagement"
 //"FlowdomainManagement"
 *
 */
/**
 * @author xuxiaojun
 * 
 */
public class HWEMSSession extends EMSSession implements IHWEMSSession{

	// T2000Emssession
//	private EmsSession_I emsSession = null;
//	private HWNmsSessionImpl nmsSession = null;

	// 各服务接口
	// "EMS" (mandatory)
	private EMSMgr_I EMSMgr = null;
	// "TopoManagement" (defined by Huawei)
	private TopoMgr_I TopoManagementMgr = null;
	// "Protection"
	private ProtectionMgr_I ProtectionMgr = null;
	// "FlowdomainManagement"
	private FlowDomainMgr_I FlowdomainManagementMgr = null;
	// "Maintenance"
	private MaintenanceMgr_I MaintenanceMgr = null;
	// "GuiCutThrough" (mandatory)
	private GuiCutThroughMgr_I GuiCutThroughMgr = null;
	// "TrafficDescriptor" || "CORBA_MSTP_TD" (defined by Huawei)
	private TrafficDescriptorMgr_I CORBA_MSTP_TDMgr = null;
	// "ManagedElement" (mandatory)
	private ManagedElementMgr_I ManagedElementMgr = null;
	// "EquipmentInventory"
	private EquipmentInventoryMgr_I EquipmentInventoryMgr = null;
	// "MultiLayerSubnetwork" (mandatory)
	private MultiLayerSubnetworkMgr_I MultiLayerSubnetworkMgr = null;
	// "ELLManagement"
	private EncapsulationLayerLinkMgr_I ELLManagementMgr = null;
	// "PerformanceManagement"
	private PerformanceManagementMgr_I PerformanceManagementMgr = null;
	// "ControlPlane" (defined by Huawei)
	private HW_controlPlaneMgr_I ControlPlaneMgr = null;
	// "CORBA_MSTP_INV" (defined by Huawei)
	private HW_MSTPInventoryMgr_I CORBA_MSTP_INVMgr = null;
	// "CORBA_MSTP_PRO" (defined by Huawei)
	private HW_MSTPProtectionMgr_I CORBA_MSTP_PROMgr = null;
	// "CORBA_MSTP_SVC" (defined by Huawei)
	private HW_MSTPServiceMgr_I CORBA_MSTP_SVCMgr = null;

	public synchronized static IHWEMSSession newInstance(String corbaName, String corbaPassword, String corbaIp,
			String corbaPort, String emsName,String encode,int iteratorNum){
		IEMSSession oldSession=EMSCollectService.sessionMap.get(corbaIp);
		if(oldSession!=null&&!(oldSession instanceof HWEMSSession)){
			try {oldSession.endSession();} catch (Exception e) {}
			oldSession=null;
		}
		if(oldSession==null){
			EMSCollectService.sessionMap.put(corbaIp, new HWEMSSession(
					corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode,iteratorNum));
		}else{
			oldSession.updateParams(corbaName, corbaPassword, corbaIp, corbaPort, emsName, encode, iteratorNum);
		}
		return (IHWEMSSession)EMSCollectService.sessionMap.get(corbaIp);
	}

	private HWEMSSession(String corbaName, String corbaPassword, String corbaIp,
			String corbaPort, String emsName,String encode,int iteratorNum) {
		super(corbaName, corbaPassword,
				corbaIp, corbaPort, emsName,
				encode, iteratorNum);
	}
	public HWNmsSessionImpl newNmsSession(){
		nmsSession = new HWNmsSessionImpl(corbaIp);
		return (HWNmsSessionImpl)nmsSession;
	}
	
	// 取得emssession
	protected EmsSession_I getInstance() throws CommonException {
		startUpCorbaConnect();
		return (EmsSession_I)emsSession;
	}

	// 初始化corba连接参数
	private void initCorbaComponent(boolean force) throws CommonException {
		if(!force&&rootpoa!=null&&factoryObj!=null){
			return;
		}
		// EmsSessionFactory_I
		NameComponent tmfEmsInstance = null;
		NameComponent tmfEntity = null;
		tmfEmsInstance = new NameComponent(emsName, "EmsInstance");
		tmfEntity = new NameComponent(emsName, "EmsSessionFactory_I");
		NameComponent tmfClass = new NameComponent("TMF_MTNM", "Class");
		NameComponent tmfVendor = new NameComponent("HUAWEI", "Vendor");
		NameComponent tmfVersion = new NameComponent("2.0", "Version");
		NameComponent[] entityPath = { tmfClass, tmfVendor, tmfEmsInstance,
				tmfVersion, tmfEntity };

		HashMap inputParams = new HashMap();
		// ip
		inputParams.put(CorbaThread.MAP_KEY_IP, corbaIp);
		// port
		inputParams.put(CorbaThread.MAP_KEY_PORT, corbaPort);
		// 命名树
		inputParams.put(CorbaThread.MAP_KEY_ENTITYPATH, entityPath);

		// 创建corba连接各输出参数进程
		CorbaThread corbaThread = new CorbaThread(inputParams,
				CorbaThread.THEAD_TYPE_CONNECT);
		// 获取输出参数
		HashMap outputParams = (HashMap) (EMSCollectService
				.getDataThread(corbaThread));

		// orb
		orb = (org.omg.CORBA.ORB) outputParams.get(CorbaThread.MAP_KEY_ORB);

		// namingContext
		namingContext = (NamingContextExt) outputParams
				.get(CorbaThread.MAP_KEY_NAMINGCONTEXT);
		// rootpoa
		rootpoa = (POA) outputParams.get(CorbaThread.MAP_KEY_ROOTPOA);
		// factoryObj
		factoryObj = (org.omg.CORBA.Object) outputParams
				.get(CorbaThread.MAP_KEY_FACTORYOBJECT);
	}
	private void initMgr(){
		EMSMgr = null;
		TopoManagementMgr = null;
		ProtectionMgr = null;
		FlowdomainManagementMgr = null;
		MaintenanceMgr = null;
		GuiCutThroughMgr = null;
		CORBA_MSTP_TDMgr = null;
		ManagedElementMgr = null;
		EquipmentInventoryMgr = null;
		MultiLayerSubnetworkMgr = null;
		ELLManagementMgr = null;
		PerformanceManagementMgr = null;
		ControlPlaneMgr = null;
		CORBA_MSTP_INVMgr = null;
		CORBA_MSTP_PROMgr = null;
		CORBA_MSTP_SVCMgr = null;
	}

	// 启动corba服务
	public void startUpCorbaConnect() throws CommonException {
		// 连接还可用
		if(!isEmsSessionInvalid())
			return;
		// 初始化连接中需要的各组件
		initCorbaComponent(true);
		initMgr();

		EmsSessionFactory_I emsSessionFactory = EmsSessionFactory_IHelper
				.narrow(factoryObj);
		try {
			rootpoa.activate_object((HWNmsSessionImpl)nmsSession);
		} catch (ServantAlreadyActive e) {
//			throw new CommonException(e,
//					MessageCodeDefine.CORBA_ROOT_POA_FAILED_EXCEPTION);
		} catch (WrongPolicy e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_ROOT_POA_FAILED_EXCEPTION);
		}
		
		EmsSession_IHolder emsSessionHolder = new EmsSession_IHolder();
		try {
			emsSessionFactory.getEmsSession(corbaName, corbaPassword,
					((HWNmsSessionImpl)nmsSession)._this(), emsSessionHolder);
			emsSession = emsSessionHolder.value;

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		// 加入sessionMap中
		//EMSCollectService.corbaSessionMap.put(this.corbaIp, emsSession);
	}

	// 启动通知服务
	public void startUpNotification() throws CommonException {
		// 初始化连接中需要的各组件
		initCorbaComponent(false);
		
		// 获取EventChannel 的引用
		EventChannelHolder eventChannel = new EventChannelHolder();
		try {
			getInstance().getEventChannel(eventChannel);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		ConsumerAdmin consumerAdmin = null;
		StructuredProxyPushSupplier structuredProxyPushSupplier = null;
		// 取得通知发布者
		if (EMSCollectService.pushSupplierMap.containsKey(this.corbaIp)) {
			structuredProxyPushSupplier = (StructuredProxyPushSupplier)EMSCollectService.pushSupplierMap.get(this.corbaIp);
			try {
				consumerAdmin = structuredProxyPushSupplier.MyAdmin();
				eventChannel.value.get_consumeradmin(
						consumerAdmin.MyID());
			} catch (AdminNotFound e){
				structuredProxyPushSupplier.disconnect_structured_push_supplier();
				structuredProxyPushSupplier = null;
				consumerAdmin.destroy();
				consumerAdmin = null;
			}catch(Exception e){
				structuredProxyPushSupplier = null;
				consumerAdmin = null;
			}
		}
		if (consumerAdmin == null) {
			// 获取ConsumerAdmin 的对象引用
			IntHolder id = new IntHolder();
			
			consumerAdmin = eventChannel.value.new_for_consumers(
					InterFilterGroupOperator.OR_OP, id);
			
			// // 设置qos属性best_effort(尽力而为) persistent(永久保持)
			// // 事件可靠性
			// Property eventReliability = new Property(
			// EventReliability.value, orb.create_any());
			// eventReliability.value.insert_short((short) 1);
			// // 连接可靠性
			// Property connectionReliability = new Property(
			// ConnectionReliability.value, orb.create_any());
			// connectionReliability.value.insert_short((short) 1);
			//
			// try {
			// consumerAdmin.set_qos(new Property[] {
			// eventReliability, connectionReliability });
			// } catch (UnsupportedQoS e1) {
			// e1.printStackTrace();
			// }
			// Property[] xx = consumerAdmin.get_qos();
			// for(Property p:xx){
			// System.out.println(p.name);
			// System.out.println(p.value);
			// }
	
			// 订阅事件
			EventTypeSeqHolder added = new EventTypeSeqHolder();
			EventTypeSeqHolder removed = new EventTypeSeqHolder();
			added.value = new _EventType[1];
			removed.value = new _EventType[0];
			added.value[0] = new _EventType("*", "*");
			// added.value[0] = new _EventType("T2000-R7","NT_HEARTBEAT");
			try {
				consumerAdmin.subscription_change(added.value, removed.value);
			} catch (InvalidEventType e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_NOTIFICATION_START_FAILED_EXCEPTION);
			}
		}
		
		// 取得通知发布者
		if (structuredProxyPushSupplier!=null){
			int[] suppliers=consumerAdmin.push_suppliers();
			if(suppliers==null||suppliers.length==0){
				structuredProxyPushSupplier.disconnect_structured_push_supplier();
				structuredProxyPushSupplier=null;
			}
		}
		
		if (structuredProxyPushSupplier==null){
			// 连接事件通道
			ProxySupplier proxySupplier = null;
			IntHolder proxy_id = new IntHolder();
			try {
				proxySupplier = consumerAdmin.obtain_notification_push_supplier(
						ClientType.STRUCTURED_EVENT, proxy_id);
				structuredProxyPushSupplier = StructuredProxyPushSupplierHelper
						.narrow(proxySupplier);
				EMSCollectService.pushSupplierMap.put(this.corbaIp, 
						structuredProxyPushSupplier);
			} catch (AdminLimitExceeded e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_NOTIFICATION_CONNECT_LIMITE_EXCEPTION);
			} 
		}
		
		try {
			// 绑定事件通知处理类
			Servant servant = new HWConsumerImpl(corbaIp,encode);
			org.omg.CORBA.Object refo = rootpoa.servant_to_reference(servant);
			StructuredPushConsumer serverObj = StructuredPushConsumerHelper.narrow(refo);

			// 连接时间通知处理类
			structuredProxyPushSupplier
					.connect_structured_push_consumer(serverObj);

		} catch (AlreadyConnected e) {
//			throw new CommonException(e,
//					MessageCodeDefine.CORBA_NOTIFICATION_ALERDY_CONNECT_EXCEPTION);
		} catch (TypeError e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_NOTIFICATION_START_FAILED_EXCEPTION);
		} catch (ServantNotActive e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_NOTIFICATION_START_FAILED_EXCEPTION);
		} catch (WrongPolicy e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_NOTIFICATION_START_FAILED_EXCEPTION);
		}
	}

	private EMSMgr_I getEMSManager() throws CommonException {
		if (EMSMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("EMS", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			EMSMgr = EMSMgr_IHelper.narrow(common_IHolder.value);
		}
		return EMSMgr;
	}

	private ManagedElementMgr_I getManagedElementManager()
			throws CommonException {
		if (ManagedElementMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("ManagedElement", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			ManagedElementMgr = ManagedElementMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return ManagedElementMgr;
	}

	private MultiLayerSubnetworkMgr_I getMultiLayerSubnetworkManager()
			throws CommonException {
		if (MultiLayerSubnetworkMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("MultiLayerSubnetwork", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			MultiLayerSubnetworkMgr = MultiLayerSubnetworkMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return MultiLayerSubnetworkMgr;
	}

	private TrafficDescriptorMgr_I getTrafficDescriptorManager()
			throws CommonException {
		if (CORBA_MSTP_TDMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("CORBA_MSTP_TD", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			CORBA_MSTP_TDMgr = TrafficDescriptorMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return CORBA_MSTP_TDMgr;
	}

	private PerformanceManagementMgr_I getPerformanceManagementManager()
			throws CommonException {
		if (PerformanceManagementMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("PerformanceManagement", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			PerformanceManagementMgr = PerformanceManagementMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return PerformanceManagementMgr;
	}

	private ProtectionMgr_I getProtectionManager() throws CommonException {
		if (ProtectionMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("Protection", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			ProtectionMgr = ProtectionMgr_IHelper.narrow(common_IHolder.value);
		}
		return ProtectionMgr;
	}

	private EquipmentInventoryMgr_I getEquipmentInventoryManager()
			throws CommonException {
		if (EquipmentInventoryMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("EquipmentInventory", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			EquipmentInventoryMgr = EquipmentInventoryMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return EquipmentInventoryMgr;
	}

	private MaintenanceMgr_I getMaintenanceManager() throws CommonException {
		if (MaintenanceMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("Maintenance", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			MaintenanceMgr = MaintenanceMgr_IHelper.narrow(common_IHolder.value);
		}
		return MaintenanceMgr;
	}

	private GuiCutThroughMgr_I getGuiCutThroughManager() throws CommonException {
		if (GuiCutThroughMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("GuiCutThrough", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			GuiCutThroughMgr = GuiCutThroughMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return GuiCutThroughMgr;
	}

	private HW_MSTPInventoryMgr_I getCORBA_MSTP_INVManager()
			throws CommonException {
		if (CORBA_MSTP_INVMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("CORBA_MSTP_INV", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			CORBA_MSTP_INVMgr = HW_MSTPInventoryMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return CORBA_MSTP_INVMgr;
	}

	private HW_MSTPProtectionMgr_I getCORBA_MSTP_PROManager()
			throws CommonException {
		if (CORBA_MSTP_PROMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("CORBA_MSTP_PRO", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			CORBA_MSTP_PROMgr = HW_MSTPProtectionMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return CORBA_MSTP_PROMgr;
	}

	private HW_MSTPServiceMgr_I getCORBA_MSTP_SVCManager()
			throws CommonException {
		if (CORBA_MSTP_SVCMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("CORBA_MSTP_SVC", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			CORBA_MSTP_SVCMgr = HW_MSTPServiceMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return CORBA_MSTP_SVCMgr;
	}

	private HW_controlPlaneMgr_I getControlPlaneManager()
			throws CommonException {
		if (ControlPlaneMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("ControlPlane", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			ControlPlaneMgr = HW_controlPlaneMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return ControlPlaneMgr;
	}

	private TopoMgr_I getTopoManagementManager() throws CommonException {
		if (TopoManagementMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("TopoManagement", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			TopoManagementMgr = TopoMgr_IHelper.narrow(common_IHolder.value);
		}
		return TopoManagementMgr;
	}

	private EncapsulationLayerLinkMgr_I getELLManagementManager()
			throws CommonException {
		if (ELLManagementMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("ELLManagement", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			ELLManagementMgr = EncapsulationLayerLinkMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return ELLManagementMgr;
	}

	private FlowDomainMgr_I getFlowdomainManagementManager()
			throws CommonException {
		if (FlowdomainManagementMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("FlowdomainManagement", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			FlowdomainManagementMgr = FlowDomainMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return FlowdomainManagementMgr;
	}

	/**
	 * 获取网管信息
	 * 
	 * @return
	 * @throws CommonException
	 */
	public EMS_T getEMS() throws CommonException {
		EMS_THolder emsInfo = new EMS_THolder();
		try {
			getEMSManager().getEMS(emsInfo);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return emsInfo.value;
	}

	// getAllTopLevelSubnetworks
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllTopLevelSubnetworks
	 * (long)
	 */
	public MultiLayerSubnetwork_T[] getAllTopLevelSubnetworks()
			throws CommonException {
		MultiLayerSubnetwork_T[] dataModel = null;
		SubnetworkList_THolder list_holder = new SubnetworkList_THolder();
		SubnetworkIterator_IHolder it_holder = new SubnetworkIterator_IHolder();
		try {
			getEMSManager().getAllTopLevelSubnetworks(iteratorNum, list_holder,
					it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<MultiLayerSubnetwork_T> dataList=new ArrayList<MultiLayerSubnetwork_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new MultiLayerSubnetwork_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllTopLevelSubnetworkNames
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllTopLevelSubnetworkNames
	 * (long)
	 */
	public NameAndStringValue_T[][] getAllTopLevelSubnetworkNames() throws CommonException {
		NameAndStringValue_T[][] dataModel = null;
		NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
		try {
			getEMSManager().getAllTopLevelSubnetworkNames(iteratorNum,
					list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<NameAndStringValue_T[]> dataList=new ArrayList<NameAndStringValue_T[]>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new NameAndStringValue_T[dataList.size()][];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllTopologicalLinks
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllTopologicalLinks(
	 * long)
	 */
	// getAllTopologicalLinks
	public TopologicalLink_T[] getAllTopologicalLinks(
			NameAndStringValue_T[] subnetName) throws CommonException {
		TopologicalLink_T[] dataModel = null;
		TopologicalLinkList_THolder list_holder = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder it_holder = new TopologicalLinkIterator_IHolder();

		try {
			getMultiLayerSubnetworkManager().getAllTopologicalLinks(subnetName,
					iteratorNum, list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<TopologicalLink_T> dataList=new ArrayList<TopologicalLink_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new TopologicalLink_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllTopLevelTopologicalLinks
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllTopLevelTopologicalLinks
	 * (long)
	 */
	// getAllTopologicalLinks
	public TopologicalLink_T[] getAllTopLevelTopologicalLinks()
			throws CommonException {
		TopologicalLink_T[] dataModel = null;
		TopologicalLinkList_THolder list_holder = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder it_holder = new TopologicalLinkIterator_IHolder();

		try {
			getEMSManager().getAllTopLevelTopologicalLinks(iteratorNum,
					list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<TopologicalLink_T> dataList=new ArrayList<TopologicalLink_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new TopologicalLink_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllInternalTopologicalLinks
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllInternalTopologicalLinks
	 * (long)
	 */
	// getAllInternalTopologicalLinks
	//
	public TopologicalLink_T[] getAllInternalTopologicalLinks(
			NameAndStringValue_T[] meName) throws CommonException {
		TopologicalLink_T[] dataModel = null;
		TopologicalLinkList_THolder list_holder = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder it_holder = new TopologicalLinkIterator_IHolder();
		try {
			getMultiLayerSubnetworkManager().getAllInternalTopologicalLinks(
					meName, iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<TopologicalLink_T> dataList=new ArrayList<TopologicalLink_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new TopologicalLink_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllCrossConnections
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllCrossConnections(
	 * long)
	 */
	public CrossConnect_T[] getAllCrossConnections(NameAndStringValue_T[] name,
			short[] connectionRateList) throws CommonException {
		CrossConnect_T[] dataModel = null;
		CrossConnectList_THolder list_holder = new CrossConnectList_THolder();
		CCIterator_IHolder it_holder = new CCIterator_IHolder();
		ArrayList<CrossConnect_T> dataTemp = new ArrayList<CrossConnect_T>();
		try {
			getManagedElementManager().getAllCrossConnections(name,
					connectionRateList, iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<CrossConnect_T> dataList=new ArrayList<CrossConnect_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new CrossConnect_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getTopoSubnetworkViewInfo
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getTopoSubnetworkViewInfo
	 * (long)
	 */
	public Node_T[] getTopoSubnetworkViewInfo() throws CommonException {
		Node_T[] dataModel = null;
		NodeList_THolder list_holder = new NodeList_THolder();
		NodeIterator_IHolder it_holder = new NodeIterator_IHolder();
		try {
			getTopoManagementManager().getTopoSubnetworkViewInfo(iteratorNum,
					list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<Node_T> dataList=new ArrayList<Node_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new Node_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllManagedElementNames
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllManagedElementNames
	 * (long)
	 */
	public NameAndStringValue_T[][] getAllManagedElementNames()
			throws CommonException {
		NameAndStringValue_T[][] dataModel = null;
		NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
		try {
			getManagedElementManager().getAllManagedElementNames(iteratorNum,
					list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<NameAndStringValue_T[]> dataList=new ArrayList<NameAndStringValue_T[]>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new NameAndStringValue_T[dataList.size()][];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllManagedElements
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllManagedElements(long)
	 */
	public ManagedElement_T[] getAllManagedElements() throws CommonException {
		ManagedElement_T[] dataModel = null;
		ManagedElementList_THolder list_holder = new ManagedElementList_THolder();
		ManagedElementIterator_IHolder it_holder = new ManagedElementIterator_IHolder();
		try {
			getManagedElementManager().getAllManagedElements(iteratorNum,
					list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<ManagedElement_T> dataList=new ArrayList<ManagedElement_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new ManagedElement_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllMstpEndPoints
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllMstpEndPoints(long)
	 */
	public HW_MSTPEndPoint_T[] getAllMstpEndPoints(NameAndStringValue_T[] meName)
			throws CommonException {
		HW_MSTPEndPoint_T[] dataModel = null;
		HW_MSTPEndPointList_THolder list_holder = new HW_MSTPEndPointList_THolder();
		HW_MSTPEndPointIterator_IHolder it_holder = new HW_MSTPEndPointIterator_IHolder();

		try {
			getCORBA_MSTP_INVManager().getAllMstpEndPoints(meName,
					new HW_MSTPEndPointType_T[] {}, iteratorNum, list_holder,
					it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<HW_MSTPEndPoint_T> dataList=new ArrayList<HW_MSTPEndPoint_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new HW_MSTPEndPoint_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllEthService
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllEthService()
	 */
	public HW_EthService_T[] getAllEthService(NameAndStringValue_T[] meName)
			throws CommonException {
		HW_EthService_T[] dataModel = null;
		HW_EthServiceType_T[] typeList = new HW_EthServiceType_T[] {};
		HW_EthServiceList_THolder list_holder = new HW_EthServiceList_THolder();
		HW_EthServiceIterator_IHolder it_holder = new HW_EthServiceIterator_IHolder();

		try {
			getCORBA_MSTP_SVCManager().getAllEthService(meName, typeList,
					iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<HW_EthService_T> dataList=new ArrayList<HW_EthService_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new HW_EthService_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllVBs
	public HW_VirtualBridge_T[] getAllVBs(NameAndStringValue_T[] meName)
			throws CommonException {

		HW_VirtualBridge_T[] dataModel = null;
		HW_VirtualBridgeList_THolder list_holder = new HW_VirtualBridgeList_THolder();
		HW_VirtualBridgeIterator_IHolder it_holder = new HW_VirtualBridgeIterator_IHolder();

		try {
			getCORBA_MSTP_INVManager().getAllVBs(meName, iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<HW_VirtualBridge_T> dataList=new ArrayList<HW_VirtualBridge_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new HW_VirtualBridge_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllVLANs
	public HW_VirtualLAN_T[] getAllVLANs(NameAndStringValue_T[] vbName)
			throws CommonException {
		HW_VirtualLAN_T[] dataModel = null;
		HW_VirtualLANList_THolder list_holder = new HW_VirtualLANList_THolder();
		HW_VirtualLANIterator_IHolder it_holder = new HW_VirtualLANIterator_IHolder();

		try {
			getCORBA_MSTP_INVManager().getAllVLANs(vbName, iteratorNum, list_holder,
					it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<HW_VirtualLAN_T> dataList=new ArrayList<HW_VirtualLAN_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new HW_VirtualLAN_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getEthService
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.serviceImpl.HWCorba.ICorbaService#getEthService()
	 */
	public HW_EthService_T getEthService(NameAndStringValue_T[] serviceName)
			throws CommonException {
		HW_EthService_THolder ethService = new HW_EthService_THolder();
		try {
			getCORBA_MSTP_SVCManager().getEthService(serviceName, ethService);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return ethService.value;
	}

	// getBindingPath
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.serviceImpl.HWCorba.ICorbaService#getBindingPath()
	 */
	public HW_MSTPBindingPath_T[] getBindingPath(
			NameAndStringValue_T[] endPointName) throws CommonException {

		HW_MSTPBindingPathList_THolder bindingPathList = new HW_MSTPBindingPathList_THolder();

		try {
			getCORBA_MSTP_INVManager().getBindingPath(endPointName,
					bindingPathList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return bindingPathList.value;
	}

	// getContainingSubnetworkNames
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getContainingSubnetworkNames
	 * (long)
	 */
	public void getContainingSubnetworkNames() throws CommonException {
		ArrayList<NamingAttributesList_THolder> subnetNamesList = new ArrayList<NamingAttributesList_THolder>();
		NamingAttributesList_THolder subnetNames = new NamingAttributesList_THolder();
		try {
			for (int i = 0; i < getAllManagedElementNames().length; i++) {
				getManagedElementManager().getContainingSubnetworkNames(
						getAllManagedElementNames()[i], subnetNames);
				subnetNamesList.add(subnetNames);
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
	}

	// getAllPTPs
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllPTPs(long)
	 */
	public void getAllPTPs() throws CommonException {
		int length = 0;
		TerminationPoint_T[] tpts = null;
		ArrayList<TerminationPoint_T> ptpTemp = new ArrayList<TerminationPoint_T>();

		TerminationPointList_THolder TerminationPointList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder TerminationPointIterator = new TerminationPointIterator_IHolder();
		try {
			for (int i = 0; i < getAllManagedElementNames().length; i++) {
				getManagedElementManager().getAllPTPs(
						getAllManagedElementNames()[i], new short[] {},
						new short[] {}, iteratorNum, TerminationPointList,
						TerminationPointIterator);

				for (int j = 0; j < TerminationPointList.value.length; j++) {
					ptpTemp.add(TerminationPointList.value[j]);
				}
				try {
					length = TerminationPointIterator.value.getLength();
				} catch (Exception e) {
				}
				while (length > 0) {
					TerminationPointIterator.value.next_n(iteratorNum,
							TerminationPointList);
					for (int j = 0; j < TerminationPointList.value.length; j++) {
						ptpTemp.add(TerminationPointList.value[j]);
					}
					length = length - iteratorNum;
				}
				tpts = new TerminationPoint_T[ptpTemp.size()];
				tpts = (TerminationPoint_T[]) ptpTemp.toArray(tpts);
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
	}

	// getAllPTPs
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllPTPs(HW.globaldefs
	 * .NameAndStringValue_T[], long)
	 */
	public TerminationPoint_T[] getAllPTPs(NameAndStringValue_T[] name)
			throws CommonException {

		TerminationPoint_T[] dataModel = null;

		TerminationPointList_THolder list_holder = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder it_holder = new TerminationPointIterator_IHolder();
		try {
			getManagedElementManager().getAllPTPs(name, new short[] {},
					new short[] {}, iteratorNum, list_holder,
					it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<TerminationPoint_T> dataList=new ArrayList<TerminationPoint_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new TerminationPoint_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// 取得所有時隙信息
	public TerminationPoint_T[] getContainedPotentialTPs(
			NameAndStringValue_T[] tpName) throws CommonException {
		TerminationPoint_T[] dataModel = null;
		TerminationPointList_THolder list_holder;
		TerminationPointIterator_IHolder it_holder;
		try {
			list_holder = new TerminationPointList_THolder();
			it_holder = new TerminationPointIterator_IHolder();

			getManagedElementManager().getContainedPotentialTPs(tpName,
					new short[] {}, iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<TerminationPoint_T> dataList=new ArrayList<TerminationPoint_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new TerminationPoint_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}

		return dataModel;
	}

	// getAllEProtectionGroups
	public EProtectionGroup_T[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException {
		EProtectionGroup_T[] dataModel = null;
		EProtectionGroupList_THolder list_holder = new EProtectionGroupList_THolder();
		EProtectionGroupIterator_IHolder it_holder = new EProtectionGroupIterator_IHolder();
		try {
			getProtectionManager().getAllEProtectionGroups(neName, iteratorNum,
					list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<EProtectionGroup_T> dataList=new ArrayList<EProtectionGroup_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new EProtectionGroup_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		
		return dataModel;
	}

	// getAllProtectionGroups
	public ProtectionGroup_T[] getAllProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException {
		ProtectionGroup_T[] dataModel = null;
		ProtectionGroupList_THolder list_holder = new ProtectionGroupList_THolder();
		ProtectionGroupIterator_IHolder it_holder = new ProtectionGroupIterator_IHolder();
		try {
			getProtectionManager().getAllProtectionGroups(neName, iteratorNum,
					list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<ProtectionGroup_T> dataList=new ArrayList<ProtectionGroup_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new ProtectionGroup_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}

		return dataModel;
	}

	// getAllWDMProtectionGroups
	public WDMProtectionGroup_T[] getAllWDMProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException {
		WDMProtectionGroup_T[] dataModel = null;
		WDMProtectionGroupList_THolder list_holder = new WDMProtectionGroupList_THolder();
		WDMProtectionGroupIterator_IHolder it_holder = new WDMProtectionGroupIterator_IHolder();
		try {
			getProtectionManager().getAllWDMProtectionGroups(neName, iteratorNum,
					list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<WDMProtectionGroup_T> dataList=new ArrayList<WDMProtectionGroup_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new WDMProtectionGroup_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}

		return dataModel;
	}

	// getObjectClockSourceStatus
	public ClockSourceStatus_T[] getObjectClockSourceStatus(
			NameAndStringValue_T[] managedElementName) throws CommonException {
		ClockSourceStatusList_THolder clockSourceStatusList = new ClockSourceStatusList_THolder();

		try {
			getEMSManager().getObjectClockSourceStatus(managedElementName,
					clockSourceStatusList);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return clockSourceStatusList.value;
	}

	// getAllPTPNames
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllPTPNames(HW.globaldefs
	 * .NameAndStringValue_T[], long)
	 */
	public NameAndStringValue_T[][] getAllPTPNames(
			NameAndStringValue_T[] name) throws CommonException {
		NameAndStringValue_T[][] dataModel = null;
		NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
		try {
			getManagedElementManager().getAllPTPNames(name, new short[] {},
					new short[] {}, iteratorNum, list_holder,
					it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<NameAndStringValue_T[]> dataList=new ArrayList<NameAndStringValue_T[]>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new NameAndStringValue_T[dataList.size()][];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getPhysicalLocationInfo
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getPhysicalLocationInfo
	 * (long)
	 */
	public PhysicalLocationInfo_T[] getPhysicalLocationInfo() throws CommonException {
		PhysicalLocationInfoList_THolder phyLocationInfoList = new PhysicalLocationInfoList_THolder();
		try {
			getEquipmentInventoryManager().getPhysicalLocationInfo(
					phyLocationInfoList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return phyLocationInfoList.value;
	}

	// getHoldingTime
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.serviceImpl.HWCorba.ICorbaService#getHoldingTime(long)
	 */
	public HoldingTime_T getHoldingTime() throws CommonException {
		HoldingTime_THolder holdingTime = new HoldingTime_THolder();
		try {
			getPerformanceManagementManager().getHoldingTime(holdingTime);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holdingTime.value;
	}

	// getAllEMSAndMEActiveAlarms
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllEMSAndMEActiveAlarms
	 * (long)
	 */
	public StructuredEvent[] getAllEMSAndMEActiveAlarms()
			throws CommonException {

		StructuredEvent[] dataModel = null;
		String[] excludeProbCauseList = new String[] {};
		PerceivedSeverity_T[] excludeSeverityList = new PerceivedSeverity_T[] {};
		EventList_THolder list_holder = new EventList_THolder();
		EventIterator_IHolder it_holder = new EventIterator_IHolder();

		try {
			getEMSManager().getAllEMSAndMEActiveAlarms(excludeProbCauseList,
					excludeSeverityList, iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<StructuredEvent> dataList=new ArrayList<StructuredEvent>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new StructuredEvent[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllEMSSystemActiveAlarms
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllEMSSystemActiveAlarms
	 * (long)
	 */
	public StructuredEvent[] getAllEMSSystemActiveAlarms()
			throws CommonException {

		StructuredEvent[] dataModel = null;
		// String[] excludeProbCauseList = new String[] {}
		PerceivedSeverity_T[] excludeSeverityList = new PerceivedSeverity_T[] {};
		EventList_THolder list_holder = new EventList_THolder();
		EventIterator_IHolder it_holder = new EventIterator_IHolder();

		try {
			getEMSManager().getAllEMSSystemActiveAlarms(excludeSeverityList,
					iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<StructuredEvent> dataList=new ArrayList<StructuredEvent>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new StructuredEvent[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllActiveAlarms
	public StructuredEvent[] getAllActiveAlarms(NameAndStringValue_T[] neName)
			throws CommonException {
		EventList_THolder list_holder = new EventList_THolder();
		EventIterator_IHolder it_holder = new EventIterator_IHolder();
		StructuredEvent[] dataModel = null;
		try {
			getManagedElementManager().getAllActiveAlarms(neName,
					new String[] {}, new PerceivedSeverity_T[] {}, iteratorNum,
					list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<StructuredEvent> dataList=new ArrayList<StructuredEvent>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new StructuredEvent[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}

		return dataModel;
	}

	/**
	 * @param alarmList
	 * @return
	 * @throws CommonException
	 */
	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException {
		AlarmSerialNoList_THolder failedAlarmList = new AlarmSerialNoList_THolder();

		String[] alarmListTemp = new String[alarmList.size()];
		alarmListTemp = (String[]) alarmList.toArray(alarmListTemp);
		try {
			getEMSManager().acknowledgeAlarms(alarmListTemp, failedAlarmList);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return failedAlarmList.value;
	}

	// getAllEquipment
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllEquipment(HW.globaldefs
	 * .NameAndStringValue_T[], long)
	 */
	public EquipmentOrHolder_T[] getAllEquipment(NameAndStringValue_T[] name)
			throws CommonException {
		EquipmentOrHolder_T[] dataModel = null;
		EquipmentOrHolderList_THolder list_holder = new EquipmentOrHolderList_THolder();
		EquipmentOrHolderIterator_IHolder it_holder = new EquipmentOrHolderIterator_IHolder();
		try {
			getEquipmentInventoryManager().getAllEquipment(name, iteratorNum,
					list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<EquipmentOrHolder_T> dataList=new ArrayList<EquipmentOrHolder_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new EquipmentOrHolder_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getManagedElement
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getManagedElement(HW.globaldefs
	 * .NameAndStringValue_T[], long)
	 */
	public ManagedElement_T getManagedElement(NameAndStringValue_T[] name)
			throws CommonException {
		ManagedElement_THolder managedElement = new ManagedElement_THolder();
		try {
			getManagedElementManager().getManagedElement(name, managedElement);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return managedElement.value;
	}

	// getContainedEquipment
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getContainedEquipment(HW
	 * .globaldefs.NameAndStringValue_T[], long)
	 */
	public EquipmentOrHolder_T[] getContainedEquipment(
			NameAndStringValue_T[] name) throws CommonException {
		EquipmentOrHolderList_THolder list_holder = new EquipmentOrHolderList_THolder();
		try {
			getEquipmentInventoryManager().getContainedEquipment(name,
					list_holder);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return list_holder.value;
	}

	// getMEPMcapabilities
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getMEPMcapabilities(HW.
	 * globaldefs.NameAndStringValue_T[], long)
	 */
	public PMParameter_T[] getMEPMcapabilities(
			NameAndStringValue_T[] name) throws CommonException {
		PMParameter_T[] dataModel = null;
		PMParameterList_THolder list_holder = new PMParameterList_THolder();
		ManagedElement_T managedElement = getManagedElement(name);
		short[] layerRate = managedElement.supportedRates;
		try {
			List<PMParameter_T> paramList=new ArrayList<PMParameter_T>();
			for (int i = 0; i < layerRate.length; i++) {
				getPerformanceManagementManager().getMEPMcapabilities(name,
						layerRate[i], list_holder);
				paramList.addAll(Arrays.asList(list_holder.value));
			}
			dataModel = new PMParameter_T[paramList.size()];
			dataModel = paramList.toArray(dataModel);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}

		return dataModel;
	}

	// getAllCurrentPMData
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllCurrentPMData(HW.
	 * performance.PMTPSelect_T[], long)
	 */
	public PMData_T[] getAllCurrentPMData(List<PMTPSelect_T> selectTPList)
			throws CommonException {
		PMData_T[] dataModel = null;

		PMDataList_THolder list_holder = new PMDataList_THolder();
		PMDataIterator_IHolder it_holder = new PMDataIterator_IHolder();
		String[] pmParameters = new String[] {};

		PMTPSelect_T[] selectTPs = new PMTPSelect_T[selectTPList.size()];
		selectTPs = (PMTPSelect_T[]) selectTPList.toArray(selectTPs);

		try {
			getPerformanceManagementManager().getAllCurrentPMData(selectTPs,
					pmParameters, iteratorNum, list_holder, it_holder);

			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			if (hasnext) {
				List<PMData_T> dataList=new ArrayList<PMData_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new PMData_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllCurrentPMData
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getAllCurrentPMData(HW.
	 * globaldefs.NameAndStringValue_T[], short[], java.lang.String[],
	 * java.lang.String[], long)
	 */
	public PMData_T[] getAllCurrentPMData(NameAndStringValue_T[] name,
			short[] _layerRateList, String[] _pMLocationList,
			String[] _granularityList) throws CommonException {
		PMData_T[] dataModel = null;

		PMDataList_THolder list_holder = new PMDataList_THolder();
		PMDataIterator_IHolder it_holder = new PMDataIterator_IHolder();
		ArrayList<PMTPSelect_T> selectedTpList = new ArrayList<PMTPSelect_T>();
		PMTPSelect_T selectedTp = null;
		String[] pmParameters = new String[] {};
		try {
			selectedTp = new PMTPSelect_T(name, _layerRateList,
					_pMLocationList, _granularityList);
			selectedTpList.add(selectedTp);
			PMTPSelect_T[] selectTPs = new PMTPSelect_T[selectedTpList.size()];
			selectTPs = (PMTPSelect_T[]) selectedTpList.toArray(selectTPs);

			getPerformanceManagementManager().getAllCurrentPMData(selectTPs,
					pmParameters, iteratorNum, list_holder, it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<PMData_T> dataList=new ArrayList<PMData_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new PMData_T[dataList.size()];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getHistoryPMData
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#getHistoryPMData(HW.globaldefs
	 * .NameAndStringValue_T[], java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, long)
	 */
	public void getHistoryPMData(NameAndStringValue_T[] name,
			String ftpIpAndFileName, String userName, String password,
			String startTime, String endTime, short[] layerRateList,
			String[] pmLocationList, String[] pmGranularityList)
			throws CommonException {
		ArrayList<PMTPSelect_T> selectedTpList = new ArrayList<PMTPSelect_T>();
		PMTPSelect_T selectedTp = null;
		String[] pmParameters = new String[] {};
		try {
			selectedTp = new PMTPSelect_T(name, layerRateList, pmLocationList,
					pmGranularityList);
			selectedTpList.add(selectedTp);
			PMTPSelect_T[] selectTPs = new PMTPSelect_T[selectedTpList.size()];
			selectTPs = (PMTPSelect_T[]) selectedTpList.toArray(selectTPs);
			getPerformanceManagementManager().getHistoryPMData(
					ftpIpAndFileName, userName, password, selectTPs,
					pmParameters, startTime, endTime, true);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
	}

	// createCrossConnections
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#createCrossConnections()
	 */
	public Map<Boolean, CrossConnect_T[]> createCrossConnections(
			CrossConnect_T[] ccList) throws CommonException {
		Map<Boolean, CrossConnect_T[]> ccMap = new HashMap<Boolean, CrossConnect_T[]>();
		CrossConnect_T[] datas = null;
		CrossConnectList_THolder successedCCList = new CrossConnectList_THolder();
		CrossConnectList_THolder failedCCList = new CrossConnectList_THolder();
		ArrayList<CrossConnect_T> dataTemp = new ArrayList<CrossConnect_T>();
		try {
			getManagedElementManager().createCrossConnections(ccList,
					successedCCList, failedCCList);

			// 成功交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < successedCCList.value.length; i++) {
				dataTemp.add(successedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(true, datas);

			// 失败交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < failedCCList.value.length; i++) {
				dataTemp.add(failedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(false, datas);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return ccMap;
	}

	// activateCrossConnections
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#activateCrossConnections()
	 */
	public Map<Boolean, CrossConnect_T[]> activateCrossConnections(
			CrossConnect_T[] ccList) throws CommonException {
		Map<Boolean, CrossConnect_T[]> ccMap = new HashMap<Boolean, CrossConnect_T[]>();
		CrossConnect_T[] datas = null;
		CrossConnectList_THolder successedCCList = new CrossConnectList_THolder();
		CrossConnectList_THolder failedCCList = new CrossConnectList_THolder();
		ArrayList<CrossConnect_T> dataTemp = new ArrayList<CrossConnect_T>();
		try {
			getManagedElementManager().activateCrossConnections(ccList,
					successedCCList, failedCCList);

			// 成功交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < successedCCList.value.length; i++) {
				dataTemp.add(successedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(true, datas);

			// 失败交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < failedCCList.value.length; i++) {
				dataTemp.add(failedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(false, datas);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return ccMap;
	}

	// deactivateCrossConnections
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#deactivateCrossConnections
	 * ()
	 */
	public Map<Boolean, CrossConnect_T[]> deactivateCrossConnections(
			CrossConnect_T[] ccList) throws CommonException {
		Map<Boolean, CrossConnect_T[]> ccMap = new HashMap<Boolean, CrossConnect_T[]>();
		CrossConnect_T[] datas = null;
		CrossConnectList_THolder successedCCList = new CrossConnectList_THolder();
		CrossConnectList_THolder failedCCList = new CrossConnectList_THolder();
		ArrayList<CrossConnect_T> dataTemp = new ArrayList<CrossConnect_T>();
		try {
			getManagedElementManager().deactivateCrossConnections(ccList,
					successedCCList, failedCCList);

			// 成功交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < successedCCList.value.length; i++) {
				dataTemp.add(successedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(true, datas);

			// 失败交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < failedCCList.value.length; i++) {
				dataTemp.add(failedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(false, datas);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return ccMap;
	}

	// deleteCrossConnections
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.HWCorba.ICorbaService#deleteCrossConnections()
	 */
	public Map<Boolean, CrossConnect_T[]> deleteCrossConnections(
			CrossConnect_T[] ccList) throws CommonException {
		Map<Boolean, CrossConnect_T[]> ccMap = new HashMap<Boolean, CrossConnect_T[]>();
		CrossConnect_T[] datas = null;
		CrossConnectList_THolder successedCCList = new CrossConnectList_THolder();
		CrossConnectList_THolder failedCCList = new CrossConnectList_THolder();
		ArrayList<CrossConnect_T> dataTemp = new ArrayList<CrossConnect_T>();
		try {
			getManagedElementManager().deleteCrossConnections(ccList,
					successedCCList, failedCCList);

			// 成功交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < successedCCList.value.length; i++) {
				dataTemp.add(successedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(true, datas);

			// 失败交叉连接数组
			dataTemp.clear();
			for (int i = 0; i < failedCCList.value.length; i++) {
				dataTemp.add(failedCCList.value[i]);
			}
			datas = new CrossConnect_T[dataTemp.size()];
			datas = (CrossConnect_T[]) dataTemp.toArray(datas);
			ccMap.put(false, datas);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return ccMap;
	}

	public EProtectionGroup_T getEProtectionGroup(
			NameAndStringValue_T[] neName) throws CommonException {
		EProtectionGroup_THolder epgpList = new EProtectionGroup_THolder();

		try {
			getProtectionManager().getEProtectionGroup(neName, epgpList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return epgpList.value;
	}
	public ProtectionGroup_T getProtectionGroup(
			NameAndStringValue_T[] neName) throws CommonException {
		ProtectionGroup_THolder epgpList = new ProtectionGroup_THolder();

		try {
			getProtectionManager().getProtectionGroup(neName, epgpList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return epgpList.value;
	}
	public WDMProtectionGroup_T getWDMProtectionGroup(
			NameAndStringValue_T[] neName) throws CommonException {
		WDMProtectionGroup_THolder epgpList = new WDMProtectionGroup_THolder();

		try {
			getProtectionManager().getWDMProtectionGroup(neName, epgpList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return epgpList.value;
	}

	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public SwitchData_T[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException{

		SwitchDataList_THolder list_holder = new SwitchDataList_THolder();
		try {
			getProtectionManager().retrieveSwitchData(pgName, list_holder);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return list_holder.value;
	}

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public ESwitchData_T[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException{
		
		ESwitchDataList_THolder list_holder = new ESwitchDataList_THolder();
		try {
			getProtectionManager().retrieveESwitchData(epgName, list_holder);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return list_holder.value;
	}
	
	public WDMSwitchData_T[] retrieveWDMSwitchData(NameAndStringValue_T[] epgName) throws CommonException{
		
		WDMSwitchDataList_THolder list_holder = new WDMSwitchDataList_THolder();
		try {
			getProtectionManager().retrieveWDMSwitchData(epgName, list_holder);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return list_holder.value;
	}
	public String[] getSupportedManagers() throws CommonException{

		managerNames_THolder holder = new managerNames_THolder();
		try {
			getInstance().getSupportedManagers(holder);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
	}
	public NameAndStringValue_T[][] getAllMstpEndPointNames(NameAndStringValue_T[] meName)
			throws CommonException {

		NameAndStringValue_T[][] dataModel = null;
		NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
		
		try {
			getCORBA_MSTP_INVManager().getAllMstpEndPointNames(meName,
					new HW_MSTPEndPointType_T[] {}, iteratorNum, list_holder,
					it_holder);
		
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<NameAndStringValue_T[]> dataList=new ArrayList<NameAndStringValue_T[]>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new NameAndStringValue_T[dataList.size()][];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}
	public NameAndStringValue_T[][] getAllVBNames(NameAndStringValue_T[] meName)
			throws CommonException {

		NameAndStringValue_T[][] dataModel = null;
		NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
		
		try {
			getCORBA_MSTP_INVManager().getAllVBNames(meName, iteratorNum, list_holder, it_holder);
		
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<NameAndStringValue_T[]> dataList=new ArrayList<NameAndStringValue_T[]>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new NameAndStringValue_T[dataList.size()][];
				dataModel = dataList.toArray(dataModel);
			} else {
				dataModel = list_holder.value;
			}
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
		}

	// // 构造交叉连接时隙名称
	// private NameAndStringValue_T[] creatCCEndName(String emsName,
	// String neSerialNo, String ptpname, String ctpname) {
	// NameAndStringValue_T[] ccEndName = null;
	// NameAndStringValue_T name1 = new NameAndStringValue_T();
	// NameAndStringValue_T name2 = new NameAndStringValue_T();
	// NameAndStringValue_T name3 = new NameAndStringValue_T();
	// NameAndStringValue_T name4 = new NameAndStringValue_T();
	// name1.name = DataCollectDefine.COMMON.EMS;
	// name1.value = emsName;
	// name2.name = DataCollectDefine.COMMON.MANAGED_ELEMENT;
	// name2.value = neSerialNo;
	// name3.name = DataCollectDefine.COMMON.PTP;
	// name3.value = ptpname;
	// name4.name = DataCollectDefine.COMMON.CTP;
	// name4.value = ctpname;
	// ccEndName = new NameAndStringValue_T[] { name1, name2, name3, name4 }
	// return ccEndName;
	// }

	public static void main(String args[]) throws CommonException {
		// // 取得emsSession
		// HWEMSSession emsSession = new HWEMSSession("admin", "T2000",
		// "10.167.28.99", "12001", "Huawei/T2000");
		// try {
		// // 源名字组
		// NameAndStringValue_T[] aEndName1 = emsSession.creatCCEndName(
		// "Huawei/T2000", "3145729",
		// "/rack=1/shelf=1/slot=3/domain=sdh/port=1",
		// "/sts3c_au4-j=1/vt2_tu12-k=2-l=1-m=2");
		// NameAndStringValue_T[] aEndName2 = emsSession.creatCCEndName(
		// "Huawei/T2000", "3145729",
		// "/rack=1/shelf=1/slot=3/domain=sdh/port=2",
		// "/sts3c_au4-j=1/vt2_tu12-k=1-l=1-m=1");
		// NameAndStringValue_T[][] aEndNameList = new NameAndStringValue_T[][]
		// { aEndName1 }
		//
		// // 宿名字组
		// NameAndStringValue_T[] zEndName1 = emsSession.creatCCEndName(
		// "Huawei/T2000", "3145729",
		// "/rack=1/shelf=1/slot=6/domain=sdh/port=2", "/vt2_tu12=1");
		// NameAndStringValue_T[] zEndName2 = emsSession.creatCCEndName(
		// "Huawei/T2000", "3145729",
		// "/rack=1/shelf=1/slot=3/domain=sdh/port=2",
		// "/sts3c_au4-j=1/vt2_tu12-k=1-l=1-m=1");
		// NameAndStringValue_T[][] zEndNameList = new NameAndStringValue_T[][]
		// { zEndName1 }
		//
		// // 交叉连接数组
		// CrossConnect_T cc = new CrossConnect_T(false,
		// ConnectionDirection_T.from_int(0), SNCType_T.from_int(0),
		// aEndNameList, zEndNameList, new NameAndStringValue_T[] {});
		// CrossConnect_T[] ccList = new CrossConnect_T[] { cc }
		//
		// // 创建交叉连接map
		// Map<Boolean, CrossConnect_T[]> ccCreateMap = emsSession
		// .createCrossConnections(ccList);
		// CrossConnect_T[] successedCreateCCList = ccCreateMap.get(true);
		// CrossConnect_T[] failedCreateCCList = ccCreateMap.get(false);
		//
		// System.out.println("success create cc list size:"
		// + successedCreateCCList.length);
		// System.out.println("failed create cc list size:"
		// + failedCreateCCList.length);
		//
		// // 激活交叉连接map
		// Map<Boolean, CrossConnect_T[]> ccActiveMap = emsSession
		// .activateCrossConnections(ccList);
		// CrossConnect_T[] successedActiveCCList = ccActiveMap.get(true);
		// CrossConnect_T[] failedActiveCCList = ccActiveMap.get(false);
		//
		// System.out.println("success active cc list size:"
		// + successedActiveCCList.length);
		// System.out.println("failed active cc list size:"
		// + failedActiveCCList.length);
		//
		// } catch (Exception e) {
		//
		// } finally {
		// emsSession.endSession();
		// }
	}
}