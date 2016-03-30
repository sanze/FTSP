package com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba;

import globaldefs.NVSList_THolder;
import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesList_THolder;
import globaldefs.ProcessingFailureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.omg.CORBA.IntHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import ZTE_U31.CosEventChannelAdmin.AlreadyConnected;
import ZTE_U31.CosEventChannelAdmin.TypeError;
import ZTE_U31.CosNotification.EventTypeSeqHolder;
import ZTE_U31.CosNotification.StructuredEvent;
import ZTE_U31.CosNotification._EventType;
import ZTE_U31.CosNotifyChannelAdmin.AdminLimitExceeded;
import ZTE_U31.CosNotifyChannelAdmin.AdminNotFound;
import ZTE_U31.CosNotifyChannelAdmin.ClientType;
import ZTE_U31.CosNotifyChannelAdmin.ConsumerAdmin;
import ZTE_U31.CosNotifyChannelAdmin.EventChannelHolder;
import ZTE_U31.CosNotifyChannelAdmin.InterFilterGroupOperator;
import ZTE_U31.CosNotifyChannelAdmin.ProxySupplier;
import ZTE_U31.CosNotifyChannelAdmin.StructuredProxyPushSupplier;
import ZTE_U31.CosNotifyChannelAdmin.StructuredProxyPushSupplierHelper;
import ZTE_U31.CosNotifyComm.InvalidEventType;
import ZTE_U31.CosNotifyComm.StructuredPushConsumer;
import ZTE_U31.CosNotifyComm.StructuredPushConsumerHelper;
import ZTE_U31.alarmMgr.PerceivedSeverity_T;
import ZTE_U31.clocksource.ClockSourceList_THolder;
import ZTE_U31.clocksource.ClockSource_I;
import ZTE_U31.clocksource.ClockSource_IHelper;
import ZTE_U31.clocksource.ClockSource_T;
import ZTE_U31.common.Common_IHolder;
import ZTE_U31.emsMgr.EMSMgr_I;
import ZTE_U31.emsMgr.EMSMgr_IHelper;
import ZTE_U31.emsMgr.EMS_T;
import ZTE_U31.emsMgr.EMS_THolder;
import ZTE_U31.emsSession.EmsSession_I;
import ZTE_U31.emsSession.EmsSession_IHolder;
import ZTE_U31.emsSession.EmsSession_IPackage.managerNames_THolder;
import ZTE_U31.emsSessionFactory.EmsSessionFactory_I;
import ZTE_U31.emsSessionFactory.EmsSessionFactory_IHelper;
import ZTE_U31.equipment.EquipmentInventoryMgr_I;
import ZTE_U31.equipment.EquipmentInventoryMgr_IHelper;
import ZTE_U31.equipment.EquipmentOrHolderList_THolder;
import ZTE_U31.equipment.EquipmentOrHolder_T;
import ZTE_U31.ethernet.Ethernet_I;
import ZTE_U31.ethernet.Ethernet_IHelper;
import ZTE_U31.ethernet.VBIterator_IHolder;
import ZTE_U31.ethernet.VBList_THolder;
import ZTE_U31.ethernet.VB_T;
import ZTE_U31.ethernet.VLANIterator_IHolder;
import ZTE_U31.ethernet.VLANList_THolder;
import ZTE_U31.ethernet.VLAN_T;
import ZTE_U31.fileTransfer.FileTransferMgr_I;
import ZTE_U31.fileTransfer.FileTransferMgr_IHelper;
import ZTE_U31.guiCutThrough.GuiCutThroughMgr_I;
import ZTE_U31.guiCutThrough.GuiCutThroughMgr_IHelper;
import ZTE_U31.maintenanceOps.MaintenanceMgr_I;
import ZTE_U31.maintenanceOps.MaintenanceMgr_IHelper;
import ZTE_U31.managedElement.ManagedElementList_THolder;
import ZTE_U31.managedElement.ManagedElement_T;
import ZTE_U31.managedElement.ManagedElement_THolder;
import ZTE_U31.managedElementManager.MEConfigData_T;
import ZTE_U31.managedElementManager.MEConfigData_THolder;
import ZTE_U31.managedElementManager.ManagedElementMgr_I;
import ZTE_U31.managedElementManager.ManagedElementMgr_IHelper;
import ZTE_U31.mstpcommon.EthernetServiceIterator_IHolder;
import ZTE_U31.mstpcommon.EthernetServiceList_THolder;
import ZTE_U31.mstpcommon.EthernetService_T;
import ZTE_U31.mstpcommon.MSTPCommon_I;
import ZTE_U31.mstpcommon.MSTPCommon_IHelper;
import ZTE_U31.mstpcommon.VCGBinding_T;
import ZTE_U31.mstpcommon.VCGBinding_THolder;
import ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import ZTE_U31.multiLayerSubnetwork.SubnetworkList_THolder;
import ZTE_U31.notifications.EventIterator_IHolder;
import ZTE_U31.notifications.EventList_THolder;
import ZTE_U31.performance.PMDataIterator_IHolder;
import ZTE_U31.performance.PMDataList_THolder;
import ZTE_U31.performance.PMData_T;
import ZTE_U31.performance.PMTPSelect_T;
import ZTE_U31.performance.PerformanceManagementMgr_I;
import ZTE_U31.performance.PerformanceManagementMgr_IHelper;
import ZTE_U31.performance.QueryPMFilter_T;
import ZTE_U31.protection.EProtectionGroupIterator_IHolder;
import ZTE_U31.protection.EProtectionGroupList_THolder;
import ZTE_U31.protection.EProtectionGroup_T;
import ZTE_U31.protection.EProtectionGroup_THolder;
import ZTE_U31.protection.ESwitchDataList_THolder;
import ZTE_U31.protection.ESwitchData_T;
import ZTE_U31.protection.ProtectionMgr_I;
import ZTE_U31.protection.ProtectionMgr_IHelper;
import ZTE_U31.subnetworkConnection.CCIterator_IHolder;
import ZTE_U31.subnetworkConnection.CrossConnectList_THolder;
import ZTE_U31.subnetworkConnection.CrossConnect_T;
import ZTE_U31.subnetworkConnection.Route_THolder;
import ZTE_U31.subnetworkConnection.SNCIterator_IHolder;
import ZTE_U31.subnetworkConnection.SubnetworkConnectionList_THolder;
import ZTE_U31.subnetworkConnection.SubnetworkConnection_T;
import ZTE_U31.terminationPoint.TerminationPointIterator_IHolder;
import ZTE_U31.terminationPoint.TerminationPointList_THolder;
import ZTE_U31.terminationPoint.TerminationPoint_T;
import ZTE_U31.topologicalLink.TopologicalLinkList_THolder;
import ZTE_U31.topologicalLink.TopologicalLink_T;
import ZTE_U31.wdmConfig.ProtectInfoList_THolder;
import ZTE_U31.wdmConfig.ProtectInfo_T;
import ZTE_U31.wdmConfig.WdmConfig_I;
import ZTE_U31.wdmConfig.WdmConfig_IHelper;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.service.CorbaThread;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.service.EMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.IZTEEMSSession;
import com.fujitsu.util.NameAndStringValueUtil;

/**
 * @author xuxiaojun
 * 
 */
public class ZTEU31EMSSession extends EMSSession implements IZTEEMSSession {

	// E300 Emssession
//	private EmsSession_I emsSession = null;
//	private ZTEU31NmsSessionImpl nmsSession;

	// 各服务接口
	// "EMS" (mandatory)
	private EMSMgr_I EMSMgr = null;
	// "Protection"
	private ProtectionMgr_I ProtectionMgr = null;
	// "Maintenance"
	private MaintenanceMgr_I MaintenanceMgr = null;
	// "GuiCutThrough" (mandatory)
	private GuiCutThroughMgr_I GuiCutThroughMgr = null;
	// "ManagedElement" (mandatory)
	private ManagedElementMgr_I ManagedElementMgr = null;
	// "EquipmentInventory"
	private EquipmentInventoryMgr_I EquipmentInventoryMgr = null;
	// "MultiLayerSubnetwork" (mandatory)
	private MultiLayerSubnetworkMgr_I MultiLayerSubnetworkMgr = null;
	// "PerformanceManagement"
	private PerformanceManagementMgr_I PerformanceManagementMgr = null;
	
	private FileTransferMgr_I FileTransferMgr = null;

	private ClockSource_I ClockSourceMgr = null;
	
	// "WdmConfig"
	private WdmConfig_I WdmConfig = null;

	// "MSTPCommon"
	private MSTPCommon_I MstpMgr = null;

	// "ethNet"
	private Ethernet_I EhtnetMgr = null;

	public synchronized static IZTEEMSSession newInstance(String corbaName, String corbaPassword, String corbaIp,
			String corbaPort, String emsName,String encode,int iteratorNum){
		IEMSSession oldSession=EMSCollectService.sessionMap.get(corbaIp);
		if(oldSession!=null&&!(oldSession instanceof ZTEU31EMSSession)){
			try {oldSession.endSession();} catch (Exception e) {}
			oldSession=null;
		}
		if(oldSession==null){
			EMSCollectService.sessionMap.put(corbaIp, new ZTEU31EMSSession(
					corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode,iteratorNum));
		}else{
			oldSession.updateParams(corbaName, corbaPassword, corbaIp, corbaPort, emsName, encode, iteratorNum);
		}
		return (IZTEEMSSession)EMSCollectService.sessionMap.get(corbaIp);
	}
	private ZTEU31EMSSession(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName, String encode,int iteratorNum) {

		super(corbaName, corbaPassword,
				corbaIp, corbaPort, emsName,
				encode, iteratorNum);
	}
	public ZTEU31NmsSessionImpl newNmsSession(){
		nmsSession = new ZTEU31NmsSessionImpl(corbaIp);
		return (ZTEU31NmsSessionImpl)nmsSession;
	}
	
	// 取得emssession
	public EmsSession_I getInstance() throws CommonException {
		startUpCorbaConnect();
		return (EmsSession_I)emsSession;
	}

	// 初始化corb连接参数
	private void initCorbaComponent(boolean force) throws CommonException {
		if(!force&&rootpoa!=null&&factoryObj!=null){
			return;
		}
		// EmsSessionFactory_I
		NameComponent tmfEmsInstance = null;
		tmfEmsInstance = new NameComponent(emsName, "EMSFactory");
		NameComponent[] entityPath = { tmfEmsInstance };

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
		ProtectionMgr = null;
		MaintenanceMgr = null;
		GuiCutThroughMgr = null;
		ManagedElementMgr = null;
		EquipmentInventoryMgr = null;
		MultiLayerSubnetworkMgr = null;
		PerformanceManagementMgr = null;
		FileTransferMgr = null;
		ClockSourceMgr = null;
		WdmConfig = null;
		MstpMgr = null;
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
			rootpoa.activate_object((ZTEU31NmsSessionImpl)nmsSession);
		} catch (ServantAlreadyActive e) {
//			throw new CommonException(e,
//					MessageCodeDefine.CORBA_ROOT_POA_FAILED_EXCEPTION);
		} catch (WrongPolicy e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_ROOT_POA_FAILED_EXCEPTION);
		}

		ZTE_U31.emsSession.EmsSession_IHolder emsSessionHolder = new EmsSession_IHolder();
		try {
			emsSessionFactory.getEmsSession(corbaName, corbaPassword,
					((ZTEU31NmsSessionImpl)nmsSession)._this(), emsSessionHolder);
			emsSession = emsSessionHolder.value;
			// ZTE_U31.nmsSession.NmsSession_I client,
			// ZTE_U31.emsSession.EmsSession_IHolder emsSessionInterface) throws
			// globaldefs.ProcessingFailureException;
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		// 加入sessionMap中
//		EMSCollectService.corbaSessionMap.put(this.corbaIp, emsSession);
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
			Servant servant = new ZTEU31ConsumerImpl(corbaIp,encode);
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

	private MSTPCommon_I getMstpMgr() throws CommonException {
		if (MstpMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("MSTPCommon", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			MstpMgr = MSTPCommon_IHelper.narrow(common_IHolder.value);
		}
		return MstpMgr;
	}
	
	private Ethernet_I getEhtnetMgr() throws CommonException {
		if (EhtnetMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("Ethernet", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			EhtnetMgr = Ethernet_IHelper.narrow(common_IHolder.value);
		}
		return EhtnetMgr;
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
	
	private WdmConfig_I getWdmConfig() throws CommonException {
		if (WdmConfig == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("WdmConfig", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,e.errorReason);
			}
			WdmConfig = WdmConfig_IHelper.narrow(common_IHolder.value);
		}
		return WdmConfig;
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
	
	private FileTransferMgr_I getFileTransferManager() throws CommonException {
		if (FileTransferMgr == null || isEmsSessionInvalid()) {
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("FileTransfer", common_IHolder);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
			FileTransferMgr = FileTransferMgr_IHelper
					.narrow(common_IHolder.value);
		}
		return FileTransferMgr;
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
	 * com.fujitsu.serviceImpl.ZTE_U31Corba.ICorbaService#getAllTopLevelSubnetworks
	 * (long)
	 */
	public MultiLayerSubnetwork_T[] getAllTopLevelSubnetworks()
			throws CommonException {
		SubnetworkList_THolder subnetworkList = new SubnetworkList_THolder();
		try {
			getEMSManager().getAllTopLevelSubnetworks(subnetworkList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return subnetworkList.value;
	}

	// getAllTopLevelSubnetworkNames
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.serviceImpl.ZTE_U31Corba.ICorbaService#getAllTopLevelSubnetworkNames
	 * (long)
	 */
	public NameAndStringValue_T[][] getAllTopLevelSubnetworkNames() throws CommonException {
		NamingAttributesList_THolder namingAttributesList = new NamingAttributesList_THolder();
		try {
			getEMSManager().getAllTopLevelSubnetworkNames(namingAttributesList);
		 } catch (ProcessingFailureException e) {
			 throw new CommonException(e,
					 MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					 NameAndStringValueUtil.Stringformat(e.errorReason, encode)); }
		 return namingAttributesList.value;
	}
	
	
	//getAllSubnetworkConnections
	public SubnetworkConnection_T[] getAllSubnetworkConnections(NameAndStringValue_T[] subnetName) throws CommonException{
		SubnetworkConnection_T[] dataModel = null;
		try {
			List<SubnetworkConnection_T> dataList=new ArrayList<SubnetworkConnection_T >();
			
			SubnetworkConnectionList_THolder list_holder = new SubnetworkConnectionList_THolder();
			SNCIterator_IHolder it_holder = new SNCIterator_IHolder();
			getMultiLayerSubnetworkManager().getAllSubnetworkConnections(
					subnetName, new short[]{}, iteratorNum,list_holder, it_holder);
			
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			if (hasvalue) {
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
			}
			dataModel=new SubnetworkConnection_T[dataList.size()];
			dataModel = dataList.toArray(dataModel);
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}
	//getRoute
	public CrossConnect_T[] getRoute(NameAndStringValue_T[] subnetName) throws CommonException{
		CrossConnect_T[] dataModel = null;
		try {
			
			Route_THolder list_holder = new Route_THolder();
			getMultiLayerSubnetworkManager().getRoute(subnetName, true,list_holder);
			dataModel=list_holder.value;
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getAllTopologicalLinks
	public TopologicalLink_T[] getAllTopologicalLinks(
			NameAndStringValue_T[] subnetName) throws CommonException {
		TopologicalLinkList_THolder topoList = new TopologicalLinkList_THolder();

		try {
			getMultiLayerSubnetworkManager().getAllTopologicalLinks(subnetName,
					topoList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return topoList.value;
	}
	public TopologicalLink_T[] getAllTopLevelTopologicalLinks() throws CommonException {
		TopologicalLinkList_THolder topoList = new TopologicalLinkList_THolder();

		try {
			getEMSManager().getAllTopLevelTopologicalLinks(topoList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return topoList.value;
	}

	public CrossConnect_T[] getAllCrossConnections(NameAndStringValue_T[] name,
			short[] connectionRateList) throws CommonException {

		CrossConnect_T[] dataModel = null;
		CrossConnectList_THolder list_holder = new CrossConnectList_THolder();
		CCIterator_IHolder it_holder = new CCIterator_IHolder();
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

	public NameAndStringValue_T[][] getAllManagedElementNames()
			throws CommonException {
		NamingAttributesList_THolder namingAttributesList = new NamingAttributesList_THolder();
		try {
			getManagedElementManager().getAllManagedElementNames(
					namingAttributesList);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return namingAttributesList.value;
	}

	public ManagedElement_T[] getAllManagedElements() throws CommonException {
		ManagedElementList_THolder managedElementList = new ManagedElementList_THolder();
		try {
			getManagedElementManager()
					.getAllManagedElements(managedElementList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return managedElementList.value;
	}

	// getAllEthService
	public EthernetService_T[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException {
		EthernetService_T[] dataModel = null;
		EthernetServiceList_THolder list_holder = new EthernetServiceList_THolder();
		EthernetServiceIterator_IHolder it_holder = new EthernetServiceIterator_IHolder();

		try {
			getMstpMgr().getAllEthernetService(iteratorNum, list_holder,
					it_holder);

			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<EthernetService_T> dataList=new ArrayList<EthernetService_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new EthernetService_T[dataList.size()];
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

	public TerminationPoint_T[] getAllPTPs(NameAndStringValue_T[] name)
			throws CommonException {
		TerminationPointList_THolder TerminationPointList = new TerminationPointList_THolder();
		try {
			getManagedElementManager().getAllPTPs(name, new short[] {},
					new short[] {}, TerminationPointList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return TerminationPointList.value;
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

//			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
//					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
//			long total = (hasvalue ? list_holder.value.length : 0)
//					+ (hasnext ? it_holder.value.getLength() : 0);
			
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
		}catch(Exception e){
			// 中兴实测 getContainedPotentialTPs报错，导致同步网元失败
			//捕获异常，返回空数据
			ExceptionHandler.handleException(e);
		}
		if(dataModel == null){
			dataModel = new TerminationPoint_T[]{};
		}
		return dataModel;
	}

	// getAllEProtectionGroups
	public ESwitchData_T[] retrieveESwitchData(
			NameAndStringValue_T[] epg) throws CommonException {
		ESwitchDataList_THolder holder = new ESwitchDataList_THolder();
		try {
			getProtectionManager().retrieveESwitchData(epg,holder);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
	}
	
	// getMEconfigData  获取网元保护组信息
	public MEConfigData_T getMEconfigData(
			NameAndStringValue_T[] meName) throws CommonException {
		MEConfigData_THolder  meConfigDatas = new MEConfigData_THolder();
		try {
			getManagedElementManager().getMEconfigData(meName, new String[]{"MSP"}, meConfigDatas);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return meConfigDatas.value;
	}
	
	// getAllEProtectionGroups
	public EProtectionGroup_T getEProtectionGroup(
			NameAndStringValue_T[] epg) throws CommonException {
		EProtectionGroup_THolder holder = new EProtectionGroup_THolder();
		try {
			getProtectionManager().getEProtectionGroup(epg,holder);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
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

	public ClockSource_I getClockSourceMgr() throws CommonException {
		Common_IHolder common_IHolder = new Common_IHolder();
		try {
			getInstance().getManager("ClockSource", common_IHolder);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		ClockSourceMgr = ClockSource_IHelper.narrow(common_IHolder.value);
		return ClockSourceMgr;
	}

	public ClockSource_T[] getObjectClockSourceStatus(NameAndStringValue_T[] meName)
		throws CommonException {
		ClockSourceList_THolder clockSourceList = new ClockSourceList_THolder();
		try {
			getClockSourceMgr().getMEClockSource(meName, clockSourceList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return clockSourceList.value;
	}

	public NameAndStringValue_T[][] getAllPTPNames(
			NameAndStringValue_T[] name) throws CommonException {
		List<NameAndStringValue_T[]> list = new ArrayList<NameAndStringValue_T[]>();
		TerminationPoint_T[] tmps = getAllPTPs(name);
		for(TerminationPoint_T tmp:tmps){
			list.add(tmp.name);
		}
		NameAndStringValue_T[][] datas = new NameAndStringValue_T[list.size()][];
		datas = list.toArray(datas);
		return datas;
	}

	// GetProtectInfo
	public ProtectInfo_T[] getAllWDMProtectionGroups(NameAndStringValue_T[] me) throws CommonException {

		ProtectInfoList_THolder protectInfoList = new ProtectInfoList_THolder();
		try {
			getWdmConfig().GetProtectInfo(protectInfoList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return protectInfoList.value;
	}
	
	public VCGBinding_T getBindingPath(NameAndStringValue_T[] vcgTpName)
		throws CommonException {
		
		VCGBinding_THolder vcgBinding = new VCGBinding_THolder();
		try {
			getMstpMgr().getVCGBinding(vcgTpName, vcgBinding);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return vcgBinding.value;
	}

	public NameAndStringValue_T[] getVCGParameter(
			NameAndStringValue_T[] vcgTpName, Long nmsType) throws CommonException {
		
		NVSList_THolder parameterList = new NVSList_THolder();
		try {
			getMstpMgr().getVCGParameter(vcgTpName, parameterList);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return parameterList.value;
	}

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

	public StructuredEvent[] getAllEMSAndMEActiveAlarms() throws CommonException {

		StructuredEvent[] dataModel = null;
		String[] excludeProbCauseList = new String[] {};
		PerceivedSeverity_T[] excludeSeverityList = new PerceivedSeverity_T[] {};
		EventList_THolder list_holder = new EventList_THolder();
		EventIterator_IHolder it_holder = new EventIterator_IHolder();

		try {
			getEMSManager().getAllActiveAlarms(excludeProbCauseList,
					excludeSeverityList, iteratorNum, list_holder, it_holder);
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
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
	
	public StructuredEvent[] getAllEMSSystemActiveAlarms() throws CommonException {

		StructuredEvent[] dataModel = null;
		String[] excludeProbCauseList = new String[] {};
		PerceivedSeverity_T[] excludeSeverityList = new PerceivedSeverity_T[] {};
		EventList_THolder list_holder = new EventList_THolder();
		EventIterator_IHolder it_holder = new EventIterator_IHolder();

		try {
			getEMSManager().getAllEMSAndMEUnacknowledgedActiveAlarms(excludeProbCauseList,
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

	public EquipmentOrHolder_T[] getAllEquipment(NameAndStringValue_T[] name)
			throws CommonException {
		EquipmentOrHolderList_THolder equipmentOrHolderList = new EquipmentOrHolderList_THolder();
		try {
			getEquipmentInventoryManager().getAllEquipment(name,
					equipmentOrHolderList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return equipmentOrHolderList.value;
	}

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

	public EquipmentOrHolder_T[] getContainedEquipment(
			NameAndStringValue_T[] name) throws CommonException {
		EquipmentOrHolderList_THolder equipmentOrHolderList = new EquipmentOrHolderList_THolder();
		try {
			getEquipmentInventoryManager().getContainedEquipment(name,
					equipmentOrHolderList);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return equipmentOrHolderList.value;
	}

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

	public void getHistoryPMData(NameAndStringValue_T[] name,
			String ftpIpAndFileName, String userName, String password,
			String startTime, String endTime, short[] layerRateList,
			String[] pmLocationList, String[] pmGranularityList) throws CommonException {
		if(pmGranularityList==null||pmGranularityList.length==0){
			pmGranularityList = new String[]{DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING};
		}
		NameAndStringValue_T[][] pmSourceSelect = new NameAndStringValue_T[][]{name};
		
		QueryPMFilter_T queryCondition = new QueryPMFilter_T(pmSourceSelect,
				new String[] {},pmGranularityList[0],layerRateList,pmLocationList,
				startTime,endTime);

		try {
			//ftpIpAndFileName="133.37.99.90|/history.txt";
			getFileTransferManager().requireHistoryPMFileTransfer(
					ftpIpAndFileName, userName, password, queryCondition);
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
	}
	
	
	public PMData_T[] getHistoryPMData(NameAndStringValue_T[] name,
			String startTime, String endTime,short[] layerRateList,
			String[] pmLocationList,String[] pmGranularityList) throws CommonException {
		
		ArrayList<PMTPSelect_T> selectedTpList = new ArrayList<PMTPSelect_T>();
		PMTPSelect_T selectedTp = null;
		String[] pmParameters = new String[] {};
		PMData_T[] pmDatas = null;
		int length = 0;
		ArrayList<PMData_T> pmDataTemp = new ArrayList<PMData_T>();

		PMDataList_THolder pmDataList = new PMDataList_THolder();
		PMDataIterator_IHolder pmIt = new PMDataIterator_IHolder();
		try {
			selectedTp = new PMTPSelect_T(name, layerRateList,
					pmLocationList,
					pmGranularityList);
			selectedTpList.add(selectedTp);
			PMTPSelect_T[] selectTPs = new PMTPSelect_T[selectedTpList.size()];
			selectTPs = (PMTPSelect_T[]) selectedTpList.toArray(selectTPs);

			getPerformanceManagementManager()
					.getTPHistoryPMData(selectTPs, pmParameters, startTime,
							endTime, iteratorNum, pmDataList, pmIt);

			for (int i = 0; i < pmDataList.value.length; i++) {
				pmDataTemp.add(pmDataList.value[i]);
			}

			try {
				length = pmIt.value.getLength();
			} catch (Exception e) {

			}
			while (length > 0) {
				pmIt.value.next_n(iteratorNum, pmDataList);
				for (int i = 0; i < pmDataList.value.length; i++) {
					pmDataTemp.add(pmDataList.value[i]);
				}
				length = length - iteratorNum;
			}
			pmDatas = new PMData_T[pmDataTemp.size()];
			pmDatas = (PMData_T[]) pmDataTemp.toArray(pmDatas);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return pmDatas;
	}

	/** 获取支持的管理器 */
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

	public NameAndStringValue_T[][] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException {
		ArrayList<NameAndStringValue_T[]> list = new ArrayList<NameAndStringValue_T[]>();
		VB_T[] tmps = getAllVBs(meName);
		for(VB_T tmp:tmps){
			list.add(tmp.name);
		}
		NameAndStringValue_T[][] datas = new NameAndStringValue_T[list.size()][];
		return list.toArray(datas);
	}

	public VB_T[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException {
		VB_T[] dataModel = null;
		VBList_THolder list_holder = new VBList_THolder();
		VBIterator_IHolder it_holder = new VBIterator_IHolder();

		try {
			getEhtnetMgr().getAllVBs(meName, iteratorNum, list_holder, it_holder);

			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			
			if (hasnext) {
				List<VB_T> dataList=new ArrayList<VB_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new VB_T[dataList.size()];
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

	public VLAN_T[] getAllVLANs(NameAndStringValue_T[] me) throws CommonException {
		VLAN_T[] dataModel = null;
		VLANList_THolder list_holder = new VLANList_THolder();
		VLANIterator_IHolder it_holder = new VLANIterator_IHolder();

		try {
			getEhtnetMgr().getAllVLANs(iteratorNum, list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<VLAN_T> dataList=new ArrayList<VLAN_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new VLAN_T[dataList.size()];
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

}