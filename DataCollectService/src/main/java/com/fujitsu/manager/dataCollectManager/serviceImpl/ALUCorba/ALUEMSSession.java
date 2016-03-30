package com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesIterator_IHolder;
import globaldefs.NamingAttributesList_THolder;
import globaldefs.ProcessingFailureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.omg.CORBA.IntHolder;
import org.omg.CosEventChannelAdmin.AlreadyConnected;
import org.omg.CosEventChannelAdmin.TypeError;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;
import org.omg.CosNotifyChannelAdmin.AdminNotFound;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannelHolder;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator;
import org.omg.CosNotifyChannelAdmin.ProxySupplier;
import org.omg.CosNotifyChannelAdmin.SequenceProxyPushSupplier;
import org.omg.CosNotifyChannelAdmin.SequenceProxyPushSupplierHelper;
import org.omg.CosNotifyComm.SequencePushConsumer;
import org.omg.CosNotifyComm.SequencePushConsumerHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import ALU.common.Common_IHolder;
import ALU.emsMgr.EMSMgr_I;
import ALU.emsMgr.EMSMgr_IHelper;
import ALU.emsMgr.EMS_T;
import ALU.emsMgr.EMS_THolder;
import ALU.emsSession.EmsSession_I;
import ALU.emsSession.EmsSession_IHolder;
import ALU.emsSession.EmsSession_IPackage.managerNames_THolder;
import ALU.emsSessionFactory.EmsSessionFactory_I;
import ALU.emsSessionFactory.EmsSessionFactory_IHelper;
import ALU.equipment.EquipmentInventoryMgr_I;
import ALU.equipment.EquipmentInventoryMgr_IHelper;
import ALU.equipment.EquipmentOrHolderIterator_IHolder;
import ALU.equipment.EquipmentOrHolderList_THolder;
import ALU.equipment.EquipmentOrHolder_T;
import ALU.extendServiceMgr.ExtendServiceMgr_I;
import ALU.extendServiceMgr.ExtendServiceMgr_IHelper;
import ALU.flowDomain.FDIterator_IHolder;
import ALU.flowDomain.FDList_THolder;
import ALU.flowDomain.FlowDomainMgr_I;
import ALU.flowDomain.FlowDomainMgr_IHelper;
import ALU.flowDomain.FlowDomain_T;
import ALU.flowDomainFragment.FDFrIterator_IHolder;
import ALU.flowDomainFragment.FDFrList_THolder;
import ALU.flowDomainFragment.FlowDomainFragment_T;
import ALU.maintenanceOps.MaintenanceMgr_I;
import ALU.maintenanceOps.MaintenanceMgr_IHelper;
import ALU.managedElement.ManagedElementIterator_IHolder;
import ALU.managedElement.ManagedElementList_THolder;
import ALU.managedElement.ManagedElement_T;
import ALU.managedElement.ManagedElement_THolder;
import ALU.managedElementManager.ManagedElementMgr_I;
import ALU.managedElementManager.ManagedElementMgr_IHelper;
import ALU.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import ALU.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import ALU.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import ALU.multiLayerSubnetwork.SubnetworkIterator_IHolder;
import ALU.multiLayerSubnetwork.SubnetworkList_THolder;
import ALU.notifications.AlarmAndTCAIDList_THolder;
import ALU.notifications.AlarmOrTCAIdentifier_T;
import ALU.notifications.EventIterator_IHolder;
import ALU.notifications.EventList_THolder;
import ALU.notifications.PerceivedSeverity_T;
import ALU.performance.HoldingTime_T;
import ALU.performance.HoldingTime_THolder;
import ALU.performance.PMDataIterator_IHolder;
import ALU.performance.PMDataList_THolder;
import ALU.performance.PMData_T;
import ALU.performance.PMParameterList_THolder;
import ALU.performance.PMParameter_T;
import ALU.performance.PMTPSelect_T;
import ALU.performance.PerformanceManagementMgr_I;
import ALU.performance.PerformanceManagementMgr_IHelper;
import ALU.protection.EProtectionGroupIterator_IHolder;
import ALU.protection.EProtectionGroupList_THolder;
import ALU.protection.EProtectionGroup_T;
import ALU.protection.EProtectionGroup_THolder;
import ALU.protection.ESwitchDataList_THolder;
import ALU.protection.ESwitchData_T;
import ALU.protection.ProtectionGroupIterator_IHolder;
import ALU.protection.ProtectionGroupList_THolder;
import ALU.protection.ProtectionGroup_T;
import ALU.protection.ProtectionGroup_THolder;
import ALU.protection.ProtectionMgr_I;
import ALU.protection.ProtectionMgr_IHelper;
import ALU.protection.SwitchDataList_THolder;
import ALU.protection.SwitchData_T;
import ALU.subnetworkConnection.CCIterator_IHolder;
import ALU.subnetworkConnection.CrossConnectList_THolder;
import ALU.subnetworkConnection.CrossConnect_T;
import ALU.subnetworkConnection.Route_THolder;
import ALU.subnetworkConnection.SNCIterator_IHolder;
import ALU.subnetworkConnection.SubnetworkConnectionList_THolder;
import ALU.subnetworkConnection.SubnetworkConnection_T;
import ALU.terminationPoint.TerminationPointIterator_IHolder;
import ALU.terminationPoint.TerminationPointList_THolder;
import ALU.terminationPoint.TerminationPoint_T;
import ALU.topologicalLink.TopologicalLinkIterator_IHolder;
import ALU.topologicalLink.TopologicalLinkList_THolder;
import ALU.topologicalLink.TopologicalLink_T;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.service.CorbaThread;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.service.EMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.IALUEMSSession;
import com.fujitsu.util.NameAndStringValueUtil;

public class ALUEMSSession extends EMSSession implements IALUEMSSession{

//	private EmsSession_I emsSession = null;
//	private ALUNmsSessionImpl nmsSession;

	/**
	 * 支持的Manager名称 EMS ManagedElement EquipmentInventory MultiLayerSubnetwork
	 * PerformanceManagement Maintenance Protection ExtenedEMS
	 * ExtenedPerformance ExtendedManagedElement ExtendedMLSN TCProfile
	 * ExtendedMaintenance FlowDomain ExtendedFlowDomain GuiCutThrough
	 */
	// 各管理器接口
	/** "EMS" 网管系统管理功能 */
	private EMSMgr_I EMSMgr = null;
	/** "ManagedElement" 网元配置管理功能 */
	private ManagedElementMgr_I ManagedElementMgr = null;
	/** "EquipmentInventory" 设备配置管理功能 */
	private EquipmentInventoryMgr_I EquipmentInventoryMgr = null;
	/** "MultiLayerSubnetwork" 子网管理功能 */
	private MultiLayerSubnetworkMgr_I MultiLayerSubnetworkMgr = null;
	/** "PerformanceManagement" 性能管理功能 */
	private PerformanceManagementMgr_I PerformanceManagementMgr = null;
	/** "Maintenance" 维护管理功能 */
	private MaintenanceMgr_I MaintenanceMgr = null;
	/** "Protection" 保护管理功能 */
	private ProtectionMgr_I ProtectionMgr = null;
	/** "ExtenedEMS" 网管系统管理扩展功能 */
	// private ExtendedEMSMgr_I ExtendedEMSMgr = null;
	/** "FlowDomain" 流域管理功能 */
	private FlowDomainMgr_I FlowDomainMgr = null;
	/** "ExtendedMaintenance" 维护管理扩展功能 */
	/** "ExtenedEMS" 网管系统管理扩展功能 */
	 private ExtendServiceMgr_I ExtendServiceMgr = null;
	// private extendedMaintenanceMgr_I extendedMaintenanceMgr = null;
	/** "ExtendedFlowDomain" 流域管理扩展功能 */
	// private ExtendedFlowDomainMgr_I ExtendedFlowDomainMgr = null;

	/** 单例取得emssession */
	protected EmsSession_I getInstance() throws CommonException{
		startUpCorbaConnect();
		return (EmsSession_I)emsSession;
	}

	// 初始化corb连接参数
	private void initCorbaComponent(boolean force) throws CommonException {
		if(!force&&rootpoa!=null&&factoryObj!=null){
			return;
		}
		// EmsSessionFactory_I
		NameComponent[] entityPath = new NameComponent[]{
			new NameComponent("alu", ""),
			new NameComponent("nbi", ""),
			new NameComponent("EmsSessionFactory_I", "")
		};

		HashMap inputParams = new HashMap();
		// ip
		inputParams.put(CorbaThread.MAP_KEY_IP, corbaIp);
		// port
		inputParams.put(CorbaThread.MAP_KEY_PORT, corbaPort);
		// 命名根路径
		inputParams.put(CorbaThread.MAP_KEY_NAMEROOT, "DefaultNamingContext");
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
		ManagedElementMgr = null;
		EquipmentInventoryMgr = null;
		MultiLayerSubnetworkMgr = null;
		PerformanceManagementMgr = null;
		MaintenanceMgr = null;
		ProtectionMgr = null;
		FlowDomainMgr = null;
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
			rootpoa.activate_object((ALUNmsSessionImpl)nmsSession);
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
					((ALUNmsSessionImpl)nmsSession)._this(), emsSessionHolder);
			emsSession = emsSessionHolder.value;
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		// 加入sessionMap中
//		EMSCollectService.corbaSessionMap.put(this.corbaIp, emsSession);
	}
	/** 启动通知服务 */
	public void startUpNotification() throws CommonException{
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
		SequenceProxyPushSupplier structuredProxyPushSupplier = null;
		// 取得通知发布者
		if (EMSCollectService.pushSupplierMap.containsKey(this.corbaIp)) {
			structuredProxyPushSupplier = (SequenceProxyPushSupplier)EMSCollectService.pushSupplierMap.get(this.corbaIp);
			try {
				consumerAdmin = structuredProxyPushSupplier.MyAdmin();
				eventChannel.value.get_consumeradmin(
						consumerAdmin.MyID());
			} catch (AdminNotFound e){
				structuredProxyPushSupplier.disconnect_sequence_push_supplier();
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
	
			/* 贝尔不支持subscription_change 未实现
			// 订阅事件
			EventTypeSeqHolder added = new EventTypeSeqHolder();
			EventTypeSeqHolder removed = new EventTypeSeqHolder();
			added.value = new EventType[1];
			removed.value = new EventType[0];
			added.value[0] = new EventType("*", "*");
			// added.value[0] = new EventType("T2000-R7","NT_HEARTBEAT");
			try {
				consumerAdmin.subscription_change(added.value, removed.value);
			} catch (InvalidEventType e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_NOTIFICATION_START_FAILED_EXCEPTION);
			}*/
		}
		
		// 取得通知发布者
		if (structuredProxyPushSupplier!=null){
			int[] suppliers=consumerAdmin.push_suppliers();
			if(suppliers==null||suppliers.length==0){
				structuredProxyPushSupplier.disconnect_sequence_push_supplier();
				structuredProxyPushSupplier=null;
			}
		}
		
		if (structuredProxyPushSupplier==null){
			// 连接事件通道
			ProxySupplier proxySupplier = null;
			IntHolder proxy_id = new IntHolder();
			try {
				proxySupplier = consumerAdmin.obtain_notification_push_supplier(
						ClientType.SEQUENCE_EVENT, proxy_id);
				structuredProxyPushSupplier = SequenceProxyPushSupplierHelper
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
			Servant servant = new ALUConsumerImpl(corbaIp,encode);
			org.omg.CORBA.Object refo = rootpoa.servant_to_reference(servant);
			SequencePushConsumer serverObj = SequencePushConsumerHelper.narrow(refo);

			// 连接时间通知处理类
			structuredProxyPushSupplier
					.connect_sequence_push_consumer(serverObj);

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

	// #########Managers######
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

	/** 网管管理器 */
	private EMSMgr_I getEMSManager() throws CommonException{
		if (EMSMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("EMS", common_IHolder);
				EMSMgr = EMSMgr_IHelper.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return EMSMgr;
	}

	/** 网元配置管理器 */
	private ManagedElementMgr_I getManagedElementManager() throws CommonException{
		if (ManagedElementMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("ManagedElement", common_IHolder);
				ManagedElementMgr = ManagedElementMgr_IHelper
						.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return ManagedElementMgr;
	}

	/** 网元配置拓展管理器 */

	/** 设备配置管理器 */
	private EquipmentInventoryMgr_I getEquipmentInventoryManager() throws CommonException{
		if (EquipmentInventoryMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("EquipmentInventory", common_IHolder);
				EquipmentInventoryMgr = EquipmentInventoryMgr_IHelper
						.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return EquipmentInventoryMgr;
	}

	/** 子网管理器 */
	private MultiLayerSubnetworkMgr_I getMultiLayerSubnetworkManager() throws CommonException{
		if (MultiLayerSubnetworkMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance()
						.getManager("MultiLayerSubnetwork", common_IHolder);
				MultiLayerSubnetworkMgr = MultiLayerSubnetworkMgr_IHelper
						.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return MultiLayerSubnetworkMgr;
	}

	/** 性能管理器 */
	private PerformanceManagementMgr_I getPerformanceManagementManager() throws CommonException{
		if (PerformanceManagementMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("PerformanceManagement",
						common_IHolder);
				PerformanceManagementMgr = PerformanceManagementMgr_IHelper
						.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return PerformanceManagementMgr;
	}

	/** 维护管理器 */
	private MaintenanceMgr_I getMaintenanceManager() throws CommonException{
		if (MaintenanceMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("Maintenance", common_IHolder);
				MaintenanceMgr = MaintenanceMgr_IHelper
						.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return MaintenanceMgr;
	}

	/** 保护管理器 */
	private ProtectionMgr_I getProtectionManager() throws CommonException{
		if (ProtectionMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("Protection", common_IHolder);
				ProtectionMgr = ProtectionMgr_IHelper
						.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return ProtectionMgr;
	}

	/** 网管系统扩展管理器 */
	/*
	 * private ExtendedEMSMgr_I getExtendedEMSManager() {
	 * if(ExtendedEMSMgr==null||isEmsSessionInvalid()){ log.info("start corbaIp
	 * is:" + corbaIp); Common_IHolder common_IHolder = new Common_IHolder();
	 * try { getInstance().getManager("ExtendedEMS", common_IHolder);
	 * ExtendedEMSMgr = ExtendedEMSMgr_IHelper.narrow(common_IHolder.value);
	 *   } catch
	 * (ProcessingFailureException e) { log.error("failed corbaIp is:" +
	 * corbaIp);
	 * log.error(NameAndStringValueUtil.Stringformat(e.errorReason,"")); } catch
	 * (Exception e) { log.error("failed corbaIp is:" + corbaIp);
	 * log.error(NameAndStringValueUtil.Stringformat(e.errorReason,"")); } }
	 * return ExtendedEMSMgr; }
	 */
	/** 流域管理器 */
	private FlowDomainMgr_I getFlowDomainManager() throws CommonException{
		if (FlowDomainMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("FlowDomain", common_IHolder);
				FlowDomainMgr = FlowDomainMgr_IHelper
						.narrow(common_IHolder.value);
				
				
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return FlowDomainMgr;
	}

	/** 维护扩展管理器 */
	/*
	 * private extendedMaintenanceMgr_I getextendedMaintenanceManager() {
	 * if(extendedMaintenanceMgr==null||isEmsSessionInvalid()){ log.info("start
	 * corbaIp is:" + corbaIp); Common_IHolder common_IHolder = new
	 * Common_IHolder(); try { getInstance().getManager("ExtendedMaintenance",
	 * common_IHolder); extendedMaintenanceMgr =
	 * extendedMaintenanceMgr_IHelper.narrow(common_IHolder.value);
	 *   } catch
	 * (ProcessingFailureException e) { log.error("failed corbaIp is:" +
	 * corbaIp);
	 * log.error(NameAndStringValueUtil.Stringformat(e.errorReason,"")); } catch
	 * (Exception e) { log.error("failed corbaIp is:" + corbaIp);
	 * log.error(NameAndStringValueUtil.Stringformat(e.errorReason,"")); }
	 *  log.info("end"); } return
	 * extendedMaintenanceMgr; }
	 *//** 流域扩展管理器 */
	/** 流域管理器 */
	private ExtendServiceMgr_I getExtendServiceMgr() throws CommonException{
		if (ExtendServiceMgr == null || isEmsSessionInvalid()) {
			
			Common_IHolder common_IHolder = new Common_IHolder();
			try {
				getInstance().getManager("ExtendService", common_IHolder);
				ExtendServiceMgr = ExtendServiceMgr_IHelper
						.narrow(common_IHolder.value);
			} catch (ProcessingFailureException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						NameAndStringValueUtil.Stringformat(e.errorReason, encode));
			}
		}
		return ExtendServiceMgr;
	}
	/*
	 * private ExtendedFlowDomainMgr_I getExtendedFlowDomainManager() {
	 * if(ExtendedFlowDomainMgr==null||isEmsSessionInvalid()){ log.info("start
	 * corbaIp is:" + corbaIp); Common_IHolder common_IHolder = new
	 * Common_IHolder(); try { getInstance().getManager("ExtendedFlowDomain",
	 * common_IHolder); ExtendedFlowDomainMgr =
	 * ExtendedFlowDomainMgr_IHelper.narrow(common_IHolder.value);
	 *   } catch
	 * (ProcessingFailureException e) { log.error("failed corbaIp is:" +
	 * corbaIp);
	 * log.error(NameAndStringValueUtil.Stringformat(e.errorReason,"")); } catch
	 * (Exception e) { log.error("failed corbaIp is:" + corbaIp);
	 * log.error(NameAndStringValueUtil.Stringformat(e.errorReason,"")); }
	 *  log.info("end"); } return
	 * ExtendedFlowDomainMgr; }
	 */

	/**	#########EMSElementMgr######
	 *	# #查询当前所有告警#
	 *	##getAllEMSAndMEActiveAlarms		Y
	 *	# #查询EMS系统告警#
	 *	##getAllEMSSystemActiveAlarms		N
	 *	# #查询EMS下顶层子网#
	 *	##getAllTopLevelSubnetworks			Y
	 *	# #查询所有跨EMS间拓扑连接#
	 *	##getAllTopLevelTopologicalLinks	Y
	 *	# #查询EMS#
	 *	##getEms							N
	 *	# #查询指定跨EMS间拓扑连接#
	 *	##getTopLevelTopologicalLink		N
	 *	# #确认当前告警#
	 *	##acknowledgeAlarms					N
	 *	# #创建告警级别模板#
	 *	##createASAP						N
	 *	# #删除告警级别模板#
	 *	##deleteASAP						N
	 *	# #分配告警级别模板#
	 *	##assignASAP						N
	 *	# #去分配告警级别模板#
	 *	##deassignASAP						N
	 *	# #修改告警级别模板#
	 *	##modifyASAP						N
	 *	# #查询告警级别模板#
	 *	##getAllASAPs						N
	 *	# #查询所有告警级别模板名称#
	 *	##getAllASAPNames					N
	 *	# #查询指定告警级别模板#
	 *	##getASAP = false					N
	 *	# #查询指定网元下的告警级别模板#
	 *	##getASAPbyResource					N
	 *	# #查询有分配ASAP模板的网元名称#
	 *	##getASAPAssociatedResourceNames	N
	 */
	/** #查询网管信息# */
	public EMS_T getEMS() throws CommonException{
		EMS_THolder holder = new EMS_THolder();
		try {
			getEMSManager().getEMS(holder);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
	}

	/** #查询当前所有告警# */
	public StructuredEvent[] getAllEMSAndMEActiveAlarms() throws CommonException{
		StructuredEvent[] dataModel = null;
		try {
			EventList_THolder list_holder = new EventList_THolder();
			EventIterator_IHolder it_holder = new EventIterator_IHolder();
			getEMSManager().getAllEMSAndMEActiveAlarms(new String[] {},
					new PerceivedSeverity_T[] {}, iteratorNum, list_holder,
					it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			int total = (hasvalue ? list_holder.value.length : 0)
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

	public StructuredEvent[] getAllEMSSystemActiveAlarms() throws CommonException{
		StructuredEvent[] dataModel = null;
		try {
			EventList_THolder list_holder = new EventList_THolder();
			EventIterator_IHolder it_holder = new EventIterator_IHolder();
			getEMSManager().getAllEMSSystemActiveAlarms(
					new PerceivedSeverity_T[] {}, iteratorNum, list_holder,
					it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			int total = (hasvalue ? list_holder.value.length : 0)
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

	/** #查询EMS下顶层子网# */
	public MultiLayerSubnetwork_T[] getAllTopLevelSubnetworks() throws CommonException{
		MultiLayerSubnetwork_T[] dataModel = null;
		try {
			SubnetworkList_THolder list_holder = new SubnetworkList_THolder();
			SubnetworkIterator_IHolder it_holder = new SubnetworkIterator_IHolder();
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

	/** #查询EMS下顶层子网名# */
	public NameAndStringValue_T[][] getAllTopLevelSubnetworkNames() throws CommonException{
		NameAndStringValue_T[][] dataModel = null;
		try {
			NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
			NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
			getEMSManager().getAllTopLevelSubnetworkNames(iteratorNum, list_holder,
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

	/** #查询所有跨EMS间拓扑连接# */
	public TopologicalLink_T[] getAllTopLevelTopologicalLinks() throws CommonException{
		TopologicalLink_T[] dataModel = null;
		try {
			TopologicalLinkList_THolder list_holder = new TopologicalLinkList_THolder();
			TopologicalLinkIterator_IHolder it_holder = new TopologicalLinkIterator_IHolder();
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
	
	/** #确认当前告警# */
	public AlarmOrTCAIdentifier_T[] acknowledgeAlarms(AlarmOrTCAIdentifier_T[] acknowledgeIDList)throws CommonException{
		AlarmAndTCAIDList_THolder failedIDList = new AlarmAndTCAIDList_THolder();
		try {
			getEMSManager().acknowledgeAlarms(acknowledgeIDList, new NameAndStringValue_T[]{},
					failedIDList);
			boolean hasvalue = (failedIDList == null || failedIDList.value == null) ? false
					: true;
			long total = (hasvalue ? failedIDList.value.length : 0);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return failedIDList.value;
	}
	
	/**	########ManagedElementMgr######
	 *  # #同步当前告警数据#
	 *	#getAllActiveAlarms				Y
	 *	# #查询单网元路由的子交叉#
	 *	#getAllCrossConnections			Y
	 *	# #查询所有网元信息#
	 *	#getAllManagedElements			Y
	 *	# #查询物理终端点配置信息#
	 *	#getAllPTPs						Y
	 *	# #查询浮动终端点配置信息#
	 *	#getAllFTPs						N
	 *	# #查询浮动终端点名称#
	 *	#getAllFTPNames					N
	 *	# #查询物理终端点配置信息-不包含FTP#
	 *	#getAllPTPsWithoutFTPs			N
	 *	# #查询物理终端点名称-不包含FTP#
	 *	#getAllPTPNamesWithoutFTPs		N
	 *	# #查询连接终端点配置信息#
	 *	#getContainedCurrentTPs			N
	 *	# #查询被包含在SNC中被使用的CTP#
	 *	#getContainedInUseTPs			N
	 *	# #查询指定速率下包含的潜在CTP#
	 *	#getContainedPotentialTPs		Y
	 *	# #查询管理单元所属的子网名称#
	 *	#getContainingSubnetworkNames	N
	 *	# #查询所属TP信息#
	 *	#getContainingTPs				N
	 *	# #查询指定网元信息#
	 *	#getManagedElement				Y
	 *	# #查询指定TP信息#
	 *	#getTP							N
	 *	# #设置终端点层参数#
	 *	#setTPData						N
	 */
	/** #同步当前告警数据# */
	public StructuredEvent[] getAllActiveAlarms(NameAndStringValue_T[] neName) throws CommonException{
		StructuredEvent[] dataModel = null;
		try {
			EventList_THolder list_holder = new EventList_THolder();
			EventIterator_IHolder it_holder = new EventIterator_IHolder();
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

	/** #查询单网元路由的子交叉# */
	public CrossConnect_T[] getAllCrossConnections(
			NameAndStringValue_T[] neName, short[] connectionRateList) throws CommonException{
		CrossConnect_T[] dataModel = null;
		try {
			CrossConnectList_THolder list_holder = new CrossConnectList_THolder();
			CCIterator_IHolder it_holder = new CCIterator_IHolder();
			getManagedElementManager().getAllCrossConnections(neName,
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

	/** #查询所有网元信息# */
	public ManagedElement_T[] getAllManagedElements() throws CommonException{
		ManagedElement_T[] dataModel = null;
		try {
			ManagedElementList_THolder list_holder = new ManagedElementList_THolder();
			ManagedElementIterator_IHolder it_holder = new ManagedElementIterator_IHolder();
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

	/** #查询物理终端点配置信息# */
	public TerminationPoint_T[] getAllPTPs(NameAndStringValue_T[] neName) throws CommonException{
		TerminationPoint_T[] dataModel = null;
		try {
			TerminationPointList_THolder list_holder = new TerminationPointList_THolder();
			TerminationPointIterator_IHolder it_holder = new TerminationPointIterator_IHolder();
			getManagedElementManager().getAllPTPs(neName, new short[] {},
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

	/** #查询物理终端点名# */
	public NameAndStringValue_T[][] getAllPTPNames(NameAndStringValue_T[] neName) throws CommonException{
		NameAndStringValue_T[][] dataModel = null;
		try {
			NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
			NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
			getManagedElementManager().getAllPTPNames(neName, new short[] {},
					new short[] {}, iteratorNum, list_holder, it_holder);
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

	/** #查询指定速率下包含的潜在CTP# */
	public TerminationPoint_T[] getContainedPotentialTPs(
			NameAndStringValue_T[] tpName) throws CommonException{
		TerminationPoint_T[] dataModel = null;
		try {
			TerminationPointList_THolder list_holder = new TerminationPointList_THolder();
			TerminationPointIterator_IHolder it_holder = new TerminationPointIterator_IHolder();
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
			//贝尔出现异常返回空数据
			dataModel = new TerminationPoint_T[]{};
/*			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));*/
		}catch (Exception e) {
			//贝尔出现异常返回空数据
			dataModel = new TerminationPoint_T[]{};
		}
		return dataModel;
	}

	/** #查询指定网元信息# */
	public ManagedElement_T getManagedElement(NameAndStringValue_T[] neName) throws CommonException{
		ManagedElement_THolder holder = new ManagedElement_THolder();
		try {
			getManagedElementManager().getManagedElement(neName, holder);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
	}

	/** #查询所有网元名# */
	public NameAndStringValue_T[][] getAllManagedElementNames() throws CommonException{
		NameAndStringValue_T[][] dataModel = null;
		try {
			NamingAttributesList_THolder list_holder = new NamingAttributesList_THolder();
			NamingAttributesIterator_IHolder it_holder = new NamingAttributesIterator_IHolder();
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

	/**	########EquipmentMgr#####
	 *	# #查询指定实体下所有设备#
	 *	#getAllEquipment			Y
	 *	# #查询指定设备的端口信息#
	 *	#getAllSupportedPTPs		N
	 *	# #查询支持指定PTP的所有设备#
	 *	#getAllSupportingEquipment	N
	 *	# #查询指定容器下包含的所有设备#
	 *	#getContainedEquipment		Y
	 *	# #查询指定设备信息#
	 *	#getEquipment				N
	 *	# #预创建ONU#
	 *	#provisionEquipment			N
	 *	# #取消设备的告警上报#
	 *	#setAlarmReportingOff 		N
	 *	# #开启设备的告警上报#
	 *	#setAlarmReportingOn 		N
	 *	# #去预创建ONU#
	 * #unprovisionEquipment N
	 */
	/** #查询指定实体下所有设备# */
	public EquipmentOrHolder_T[] getAllEquipment(
			NameAndStringValue_T[] meOrHoderName) throws CommonException{
		EquipmentOrHolder_T[] dataModel = null;
		try {
			EquipmentOrHolderList_THolder list_holder = new EquipmentOrHolderList_THolder();
			EquipmentOrHolderIterator_IHolder it_holder = new EquipmentOrHolderIterator_IHolder();
			getEquipmentInventoryManager().getAllEquipment(meOrHoderName,
					iteratorNum, list_holder, it_holder);
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

	/**	########MultilayerSubnetworkMgr#####
	 *  # #激活子网交叉#
	 *  #activateSNC						N
	 *  # #检查SNC创建数据是否有效#
	 *  #checkValidSNC						N
	 *  # #创建并激活SNC#
	 *  #createAndActivateSNC				N
	 *  # #创建SNC#
	 *  #createSNC							N
	 *  # #修改SNC#
	 *  #createModifiedSNC					N
	 *  # #去激活并删除SNC#
	 *  #deactivateAndDeleteSNC				N
	 *  # #去激活SNC#
	 *  #deactivateSNC						N
	 *  # #删除SNC#
	 *  #deleteSNC							N
	 *	# #查询所有边界点#
	 *  #getAllEdgePoints 					N
	 *  # #查询子网下所有网元信息#
	 *  #getAllManagedElements 				N
	 *  # #查询所有的SNC#
	 *  #getAllSubnetworkConnections 		N
	 *  # #查询指定TP下的所有SNC#
	 *  #getAllSubnetworkConnectionsWithTP	N 
	 *  # #查询子网下所有拓扑连接#
	 *  #getAllTopologicalLinks 			Y
	 *  # #取TP池#
	 *  #getAllTPPools 						N
	 *  # #查询保护相关TP#
	 *  #getAssociatedTP 					N
	 *  # #查询指定子网信息#
	 *  #getMultiLayerSubnetwork 			N
	 *  # #查询SNC路由信息#
	 *  #getRoute 							N
	 *  # #查询SNC#
	 *  #getSNC 							N
	 *  # #通过用户标签查询SNC#
	 *  #getSNCsByUserLabel 				N
	 *  # #查询指定的拓扑连接#
	 *  #getTopologicalLink 				N
	 *  # ##
	 *  #getTPGroupingRelationships 		N
	 *  # #取子子网#
	 *  #getAllSubordinateMLSNs 			N
	 *  # #查询TELink#
	 *  #getAllMLSNPPLinks					N
	 */
	/** #查询子网下所有拓扑连接# */
	public TopologicalLink_T[] getAllTopologicalLinks(
			NameAndStringValue_T[] subnetName) throws CommonException{
		TopologicalLink_T[] dataModel = null;
		try {
			TopologicalLinkList_THolder list_holder = new TopologicalLinkList_THolder();
			TopologicalLinkIterator_IHolder it_holder = new TopologicalLinkIterator_IHolder();
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
	
	//FIXME 采集getAllFlowDomains
	public FlowDomain_T[] getAllFlowDomains() throws CommonException{
		FlowDomain_T[] dataModel = null;
		try {
			FDList_THolder list_holder = new FDList_THolder();
			
			FDIterator_IHolder it_holder = new FDIterator_IHolder();
			
			getFlowDomainManager().getAllFlowDomains(iteratorNum, list_holder, it_holder);

			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			
			if (hasnext) {
				List<FlowDomain_T> dataList=new ArrayList<FlowDomain_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new FlowDomain_T[dataList.size()];
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
	
	//FIXME 采集getFDFr
	public FlowDomainFragment_T[] getAllFDFrs(NameAndStringValue_T[] flowDomainName) throws CommonException{

		FlowDomainFragment_T[] dataModel = null;
		try {
			
			List<FlowDomainFragment_T> dataList=new ArrayList<FlowDomainFragment_T>();
			
			FDFrList_THolder list_holder = new FDFrList_THolder();
			FDFrIterator_IHolder it_holder = new FDFrIterator_IHolder();
			getFlowDomainManager().getAllFDFrs(flowDomainName, iteratorNum, new short[]{}, list_holder, it_holder);
			
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			
			if (hasnext) {
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
			} else {
				dataList.addAll(Arrays.asList(list_holder.value));
			}
			dataModel=new FlowDomainFragment_T[dataList.size()];
			dataModel = dataList.toArray(dataModel);
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}
	
	public TopologicalLink_T[] getTopologicalLinksOfFDFr(
			NameAndStringValue_T[] fdfrName) throws CommonException {

		TopologicalLink_T[] dataModel = null;
		try {
			List<TopologicalLink_T> dataList = new ArrayList<TopologicalLink_T>();

			TopologicalLinkList_THolder list_holder = new TopologicalLinkList_THolder();
			TopologicalLinkIterator_IHolder it_holder = new TopologicalLinkIterator_IHolder();

			getExtendServiceMgr().getTopologicalLinksOfFDFr(fdfrName,
					iteratorNum, list_holder, it_holder);

			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;

			if (hasnext) {
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext = it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
			} else {
				dataList.addAll(Arrays.asList(list_holder.value));
			}
			dataModel = new TopologicalLink_T[dataList.size()];
			dataModel = dataList.toArray(dataModel);

		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	/**	########PerformanceMgr#####
	 *  # #清网元性能#
	 *  #clearPMData				N
	 *  # #关闭性能采集任务#
	 *  #disablePMData				N
	 *  # #关闭性能门限开关#
	 *  #disableTCA					N
	 *  # #使能性能采集任务#
	 *  #enablePMData				N
	 *  # #打开性能门限开关#
	 *  #enableTCA					N
	 *	# #查询当前性能#
	 *	#getAllCurrentPMData 		Y
	 *	# #查询历史性能#
	 *	#getHistoryPMData 			Y
	 *	# #查询EMS性能的保持时间#
	 *	#getHoldingTime 			Y
	 *	# #查询网元性能的能力#
	 *	#getMEPMcapabilities 		Y
	 *	# #查询指定TP点上设置的TCA参数#
	 *	#getTCATPParameter 			N
	 *  # #设置TCA参数#
	 *  #setTCATPParameter			N
	 *	# #查询TP点的历史性能#
	 *	#getTPHistoryPMData 		N
	 *	# #查询所有性能监测点#
	 *	#getAllPMPs 				N
	 */
	/** #查询指定ptp当前性能# */
	public PMData_T[] getAllCurrentPMData(List<PMTPSelect_T> selectTPList) throws CommonException{
		PMData_T[] dataModel = null;
		try {
			PMDataList_THolder list_holder = new PMDataList_THolder();
			PMDataIterator_IHolder it_holder = new PMDataIterator_IHolder();
			PMTPSelect_T[] selectTPs = new PMTPSelect_T[selectTPList.size()];
			selectTPs = selectTPList.toArray(selectTPs);

			getPerformanceManagementManager().getAllCurrentPMData(selectTPs,
					new String[] {}, iteratorNum, list_holder, it_holder);
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

	/** #查询指定网元当前性能# */
	public PMData_T[] getAllCurrentPMData(NameAndStringValue_T[] name,
			short[] _layerRateList, String[] _pMLocationList,
			String[] _granularityList) throws CommonException{
		PMData_T[] dataModel = null;
		try {
			//当前性能采集 只支持按照me查询 LayerRateList－只支持空 pMLocationList－只支持空
			PMDataList_THolder list_holder = new PMDataList_THolder();
			PMDataIterator_IHolder it_holder = new PMDataIterator_IHolder();
			PMTPSelect_T[] selectTPs = new PMTPSelect_T[1];
			selectTPs[0] = new PMTPSelect_T(name, _layerRateList,
					_pMLocationList, _granularityList);
			getPerformanceManagementManager().getAllCurrentPMData(selectTPs,
					new String[] {}, iteratorNum, list_holder, it_holder);
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

	/** #查询历史性能# 
	 * startTime <= t < endTime */
	public void getHistoryPMData(NameAndStringValue_T[] name,
			String ftpIpAndFileName, String userName, String password, 
			String startTime, String endTime, short[] layerRateList, 
			String[] pmLocationList, String[] _granularityList)
			throws CommonException{
		try {
			PMTPSelect_T[] selectTPs = new PMTPSelect_T[1];
			selectTPs[0] = new PMTPSelect_T(name, layerRateList,
					pmLocationList, _granularityList);
			getPerformanceManagementManager().getHistoryPMData(
					ftpIpAndFileName, userName, password, selectTPs,
					new String[] {}, startTime, endTime, true);
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
	}

	/** #查询EMS性能的保持时间# */
	public HoldingTime_T getHoldingTime() throws CommonException{
		HoldingTime_THolder holder = new HoldingTime_THolder();
		try {
			getPerformanceManagementManager().getHoldingTime(holder);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
	}

	/** #查询网元性能的能力# */
	public PMParameter_T[] getMEPMcapabilities(NameAndStringValue_T[] neName) throws CommonException{
		long allTotal = 0;
		long currentIndex = 0;
		PMParameter_T[] dataModel = null;
		PMParameterList_THolder list_holder = new PMParameterList_THolder();
		try {
			ManagedElement_T managedElement = getManagedElement(neName);
			short[] layerRate = managedElement.supportedRates;
			if (layerRate != null) {
				List<PMParameter_T> paramList=new ArrayList<PMParameter_T>();
				for (short item : layerRate) {
					getPerformanceManagementManager().getMEPMcapabilities(
							neName, item, list_holder);
					long total = 0;
					if (list_holder != null && list_holder.value != null) {
						total = list_holder.value.length;
						allTotal += total;
						paramList.addAll(Arrays.asList(list_holder.value));
					}
					currentIndex++;
					
				}
				dataModel = new PMParameter_T[paramList.size()];
				dataModel = paramList.toArray(dataModel);
			}
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	/**
	 * ########MaintenamceMgr##### // #getActiveMaintenanceOperations = false
	 * #查询当前环回状态# // #performMaintenanceOperation = false #执行环回命令#
	 */

	/**	########ProtectionMgr#####
//	#getAllNUTTPNames = false #查询所有不可抢占的终端点名称#
//	#getAllPreemptibleTPNames = false #查询保护组中可配置为抢占式的额外路径的终端点#
//	#getAllProtectedTPNames = false #查询所有被保护的终端点名称#
//	#getAllProtectionGroups = false #查询ME下所有保护组信息#
//	#getProtectionGroup = false #查询指定的保护组信息#
//	#performProtectionCommand = false #执行保护命令#
//	#retrieveSwitchData = false #获取指定对象的保护数据#
//	#getContainingPGNames = false #查询指定实体下包含的保护组名称#
//	#getAllEProtectionGroups = false #查询所有设备保护组信息#
//	#getEProtectionGroup = false #查询指定设备保护组信息#
//	#*/
	/** #查询ME下所有保护组信息# */
	public ProtectionGroup_T[] getAllProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		ProtectionGroup_T[] dataModel = null;
		try {
			ProtectionGroupList_THolder list_holder = new ProtectionGroupList_THolder();
			ProtectionGroupIterator_IHolder it_holder = new ProtectionGroupIterator_IHolder();
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

	/** #查询所有设备保护组信息# */
	public EProtectionGroup_T[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		EProtectionGroup_T[] dataModel = null;
		try {
			EProtectionGroupList_THolder list_holder = new EProtectionGroupList_THolder();
			EProtectionGroupIterator_IHolder it_holder = new EProtectionGroupIterator_IHolder();
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

	/** #查询指定保护组信息# */
	public ProtectionGroup_T getProtectionGroup(NameAndStringValue_T[] pgName) throws CommonException{
		ProtectionGroup_THolder holder = new ProtectionGroup_THolder();
		try {
			getProtectionManager().getProtectionGroup(pgName, holder);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
	}

	/** #查询指定设备保护组信息# */
	public EProtectionGroup_T getEProtectionGroup(
			NameAndStringValue_T[] epgName) throws CommonException{
		EProtectionGroup_THolder holder = new EProtectionGroup_THolder();
		try {
			getProtectionManager().getEProtectionGroup(epgName, holder);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return holder.value;
	}

	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public SwitchData_T[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException{
		SwitchDataList_THolder list_holder = new SwitchDataList_THolder();
		try {
			getProtectionManager().retrieveSwitchData(pgName, list_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return list_holder.value;
	}

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public ESwitchData_T[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException{
		//return new ESwitchData_T[]{};
		ESwitchDataList_THolder list_holder = new ESwitchDataList_THolder();
		try {
			getProtectionManager().retrieveESwitchData(epgName, list_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0);
			
			
		} catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return list_holder.value;
	}

	public synchronized static IALUEMSSession newInstance(String corbaName, String corbaPassword, String corbaIp,
			String corbaPort, String emsName,String encode,int iteratorNum){
		IEMSSession oldSession=EMSCollectService.sessionMap.get(corbaIp);
		if(oldSession!=null&&!(oldSession instanceof ALUEMSSession)){
			try {oldSession.endSession();} catch (Exception e) {}
			oldSession=null;
		}
		if(oldSession==null){
			EMSCollectService.sessionMap.put(corbaIp, new ALUEMSSession(
					corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode,iteratorNum));
		}else{
			oldSession.updateParams(corbaName, corbaPassword, corbaIp, corbaPort, emsName, encode, iteratorNum);
		}
		return (IALUEMSSession)EMSCollectService.sessionMap.get(corbaIp);
	}
	private ALUEMSSession(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String encode,int iteratorNum) {
		super(corbaName, corbaPassword,
				corbaIp, corbaPort, emsName,
				encode, iteratorNum);
	}
	public ALUNmsSessionImpl newNmsSession(){
		nmsSession = new ALUNmsSessionImpl(corbaIp);
		return (ALUNmsSessionImpl)nmsSession;
	}
}