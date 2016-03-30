package com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesIterator_IHolder;
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

import LUCENT.CosEventChannelAdmin.AlreadyConnected;
import LUCENT.CosEventChannelAdmin.TypeError;
import LUCENT.CosNotification.EventType;
import LUCENT.CosNotification.EventTypeSeqHolder;
import LUCENT.CosNotification.StructuredEvent;
import LUCENT.CosNotifyChannelAdmin.AdminLimitExceeded;
import LUCENT.CosNotifyChannelAdmin.AdminNotFound;
import LUCENT.CosNotifyChannelAdmin.ClientType;
import LUCENT.CosNotifyChannelAdmin.ConsumerAdmin;
import LUCENT.CosNotifyChannelAdmin.EventChannelHolder;
import LUCENT.CosNotifyChannelAdmin.InterFilterGroupOperator;
import LUCENT.CosNotifyChannelAdmin.ProxySupplier;
import LUCENT.CosNotifyChannelAdmin.StructuredProxyPushSupplier;
import LUCENT.CosNotifyChannelAdmin.StructuredProxyPushSupplierHelper;
import LUCENT.CosNotifyComm.InvalidEventType;
import LUCENT.CosNotifyComm.StructuredPushConsumer;
import LUCENT.CosNotifyComm.StructuredPushConsumerHelper;
import LUCENT.common.Common_IHolder;
import LUCENT.emsMgr.EMSMgr_I;
import LUCENT.emsMgr.EMSMgr_IHelper;
import LUCENT.emsMgr.EMS_T;
import LUCENT.emsMgr.EMS_THolder;
import LUCENT.emsSession.EmsSession_I;
import LUCENT.emsSession.EmsSession_IHolder;
import LUCENT.emsSession.EmsSession_IPackage.managerNames_THolder;
import LUCENT.emsSessionFactory.EmsSessionFactory_I;
import LUCENT.emsSessionFactory.EmsSessionFactory_IHelper;
import LUCENT.equipment.EquipmentInventoryMgr_I;
import LUCENT.equipment.EquipmentInventoryMgr_IHelper;
import LUCENT.equipment.EquipmentOrHolderIterator_IHolder;
import LUCENT.equipment.EquipmentOrHolderList_THolder;
import LUCENT.equipment.EquipmentOrHolder_T;
import LUCENT.guiCutThrough.GuiCutThroughMgr_I;
import LUCENT.guiCutThrough.GuiCutThroughMgr_IHelper;
import LUCENT.maintenanceOps.MaintenanceMgr_I;
import LUCENT.maintenanceOps.MaintenanceMgr_IHelper;
import LUCENT.managedElement.ManagedElementIterator_IHolder;
import LUCENT.managedElement.ManagedElementList_THolder;
import LUCENT.managedElement.ManagedElement_T;
import LUCENT.managedElement.ManagedElement_THolder;
import LUCENT.managedElementManager.ManagedElementMgr_I;
import LUCENT.managedElementManager.ManagedElementMgr_IHelper;
import LUCENT.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import LUCENT.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import LUCENT.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import LUCENT.multiLayerSubnetwork.SubnetworkIterator_IHolder;
import LUCENT.multiLayerSubnetwork.SubnetworkList_THolder;
import LUCENT.notifications.EventIterator_IHolder;
import LUCENT.notifications.EventList_THolder;
import LUCENT.notifications.PerceivedSeverity_T;
import LUCENT.performance.HoldingTime_T;
import LUCENT.performance.HoldingTime_THolder;
import LUCENT.performance.PMDataIterator_IHolder;
import LUCENT.performance.PMDataList_THolder;
import LUCENT.performance.PMData_T;
import LUCENT.performance.PMParameterList_THolder;
import LUCENT.performance.PMParameter_T;
import LUCENT.performance.PMTPSelect_T;
import LUCENT.performance.PerformanceManagementMgr_I;
import LUCENT.performance.PerformanceManagementMgr_IHelper;
import LUCENT.protection.ProtectionGroupIterator_IHolder;
import LUCENT.protection.ProtectionGroupList_THolder;
import LUCENT.protection.ProtectionGroup_T;
import LUCENT.protection.ProtectionGroup_THolder;
import LUCENT.protection.ProtectionMgr_I;
import LUCENT.protection.ProtectionMgr_IHelper;
import LUCENT.protection.SwitchDataList_THolder;
import LUCENT.protection.SwitchData_T;
import LUCENT.subnetworkConnection.CCIterator_IHolder;
import LUCENT.subnetworkConnection.CrossConnectList_THolder;
import LUCENT.subnetworkConnection.CrossConnect_T;
import LUCENT.subnetworkConnection.SNCIterator_IHolder;
import LUCENT.subnetworkConnection.SubnetworkConnectionList_THolder;
import LUCENT.subnetworkConnection.SubnetworkConnection_T;
import LUCENT.terminationPoint.TerminationPointIterator_IHolder;
import LUCENT.terminationPoint.TerminationPointList_THolder;
import LUCENT.terminationPoint.TerminationPoint_T;
import LUCENT.topologicalLink.TopologicalLinkIterator_IHolder;
import LUCENT.topologicalLink.TopologicalLinkList_THolder;
import LUCENT.topologicalLink.TopologicalLink_T;
import LUCENT.trafficDescriptor.TrafficDescriptorMgr_I;
import LUCENT.trafficDescriptor.TrafficDescriptorMgr_IHelper;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.service.CorbaThread;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.service.EMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.ILUCENTEMSSession;
import com.fujitsu.util.NameAndStringValueUtil;

public class LUCENTEMSSession extends EMSSession implements ILUCENTEMSSession {

	// LucentEmssession
//	private EmsSession_I emsSession = null;
//	private LUCENTNmsSessionImpl nmsSession = null;

	// 各服务接口
	// "EMS" (mandatory)
	private EMSMgr_I EMSMgr = null;
	// "Protection"
	private ProtectionMgr_I ProtectionMgr = null;
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
	// "PerformanceManagement"
	private PerformanceManagementMgr_I PerformanceManagementMgr = null;

	public synchronized static ILUCENTEMSSession newInstance(String corbaName, String corbaPassword, String corbaIp,
			String corbaPort, String emsName,String encode,int iteratorNum){
		IEMSSession oldSession=EMSCollectService.sessionMap.get(corbaIp);
		if(oldSession!=null&&!(oldSession instanceof LUCENTEMSSession)){
			try {oldSession.endSession();} catch (Exception e) {}
			oldSession=null;
		}
		if(oldSession==null){
			EMSCollectService.sessionMap.put(corbaIp, new LUCENTEMSSession(
					corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode,iteratorNum));
		}else{
			oldSession.updateParams(corbaName, corbaPassword, corbaIp, corbaPort, emsName, encode, iteratorNum);
		}
		return (ILUCENTEMSSession)EMSCollectService.sessionMap.get(corbaIp);
	}
	private LUCENTEMSSession(String corbaName, String corbaPassword,	String corbaIp, 
			String corbaPort, String emsName, String encode,int iteratorNum) {

		super(corbaName, corbaPassword,
				corbaIp, corbaPort, emsName,
				encode, iteratorNum);
	}
	public LUCENTNmsSessionImpl newNmsSession(){
		nmsSession = new LUCENTNmsSessionImpl(corbaIp);
		return (LUCENTNmsSessionImpl)nmsSession;
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
		NameComponent[] entityPath = new NameComponent[]{
			new NameComponent("Services", ""),
			new NameComponent("NBI", ""),
			new NameComponent("TMF", ""),
			new NameComponent("TMF_EmsSessionFactory_I", "")
		};

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
		CORBA_MSTP_TDMgr = null;
		ManagedElementMgr = null;
		EquipmentInventoryMgr = null;
		MultiLayerSubnetworkMgr = null;
		PerformanceManagementMgr = null;
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
			rootpoa.activate_object((LUCENTNmsSessionImpl)nmsSession);
		} catch (ServantAlreadyActive e) {
			//throw new CommonException(e,
			//		MessageCodeDefine.CORBA_ROOT_POA_FAILED_EXCEPTION);
		} catch (WrongPolicy e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_ROOT_POA_FAILED_EXCEPTION);
		}

		EmsSession_IHolder emsSessionHolder = new EmsSession_IHolder();
		try {
			emsSessionFactory.getEmsSession(corbaName, corbaPassword,
					((LUCENTNmsSessionImpl)nmsSession)._this(), emsSessionHolder);
			emsSession = emsSessionHolder.value;

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
			}
		}
		if (consumerAdmin == null) {
			// 获取ConsumerAdmin 的对象引用
			IntHolder id = new IntHolder();
			
			consumerAdmin = eventChannel.value.new_for_consumers(
					InterFilterGroupOperator.OR_OP, id);
	
			// 订阅事件
			EventTypeSeqHolder added = new EventTypeSeqHolder();
			EventTypeSeqHolder removed = new EventTypeSeqHolder();
			added.value = new EventType[1];
			removed.value = new EventType[0];
			added.value[0] = new EventType("*", "*");
			
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
			Servant servant = new LUCENTConsumerImpl(corbaIp,encode);
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

	private ManagedElementMgr_I getManagedElementManager() throws CommonException {
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

	private TrafficDescriptorMgr_I getTrafficDescriptorManager() throws CommonException{
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
		throws CommonException{
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

	private ProtectionMgr_I getProtectionManager() throws CommonException{
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

	private EquipmentInventoryMgr_I getEquipmentInventoryManager() throws CommonException{
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

	private MaintenanceMgr_I getMaintenanceManager() throws CommonException{
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

	private GuiCutThroughMgr_I getGuiCutThroughManager()
		throws CommonException{
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
	public MultiLayerSubnetwork_T[] getAllTopLevelSubnetworks() throws CommonException{
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
	public NameAndStringValue_T[][] getAllTopLevelSubnetworkNames()throws CommonException {
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

	public TopologicalLink_T[] getAllTopLevelTopologicalLinks() throws CommonException{
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

	// getAllManagedElementNames
	public NameAndStringValue_T [][] getAllManagedElementNames() throws CommonException{
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

	// getManagedElement
	public ManagedElement_T getManagedElement(NameAndStringValue_T[] name)
			throws CommonException{
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
	
	// getAllEquipment
	public EquipmentOrHolder_T[] getAllEquipment(NameAndStringValue_T[] name) 
			throws CommonException{
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
	
	// getAllPTPs
	public TerminationPoint_T[] getAllPTPs(NameAndStringValue_T[] name) 
			throws CommonException{

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

	// getAllEMSAndMEActiveAlarms
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
	
	// getHoldingTime
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
	
	// getAllCurrentPMData
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
	public PMData_T[] getAllCurrentPMData(NameAndStringValue_T[] name,
			short[] _layerRateList, String[] _pMLocationList,
			String[] _granularityList) throws CommonException {
		PMData_T[] dataModel = null;

		String[] pmParameters = new String[] {};
		//朗讯比较坑爹 只支持ptp颗粒的采集
		PMDataList_THolder list_holder = new PMDataList_THolder();
		PMDataIterator_IHolder it_holder = new PMDataIterator_IHolder();
		
		ArrayList<PMTPSelect_T> selectedTpList = new ArrayList<PMTPSelect_T>();
		PMTPSelect_T selectedTp = null;
		try{
			// 只接受ptp名称
			NameAndStringValue_T[][] ptpNamesList = getAllPTPNames(name);
			
			for (int n = 0; n < ptpNamesList.length; n++) {
				selectedTp = new PMTPSelect_T(ptpNamesList[n],
						_layerRateList, _pMLocationList, _granularityList);
				 selectedTpList.add(selectedTp);
			}
				 
			PMTPSelect_T[] selectTPs = new PMTPSelect_T[selectedTpList
					.size()];
			selectTPs = (PMTPSelect_T[]) selectedTpList.toArray(selectTPs);
			
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
		}catch (ProcessingFailureException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					NameAndStringValueUtil.Stringformat(e.errorReason, encode));
		}
		return dataModel;
	}

	// getHistoryPMData
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

	// getAllEdgePoints
	public TerminationPoint_T[] getAllEdgePoints(
			NameAndStringValue_T[] subnetName, short[] tpLayerRateList,
			short[] connectionLayerRateList) throws CommonException {
		TerminationPoint_T[] dataModel = null;
		TerminationPointList_THolder list_holder = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder it_holder = new TerminationPointIterator_IHolder();
		try {
			getMultiLayerSubnetworkManager().getAllEdgePoints(
					subnetName, tpLayerRateList, connectionLayerRateList, iteratorNum,
					list_holder, it_holder);
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

	// getAllSubnetworkConnections
	public SubnetworkConnection_T[] getAllSubnetworkConnections(
			NameAndStringValue_T[] subnetName, short[] connectionRateList ) 
					throws CommonException{
		SubnetworkConnection_T[] dataModel = null;
		SubnetworkConnectionList_THolder list_holder = new SubnetworkConnectionList_THolder();
		SNCIterator_IHolder it_holder = new SNCIterator_IHolder();
		try {
			getMultiLayerSubnetworkManager()
					.getAllSubnetworkConnections(subnetName,
							connectionRateList, iteratorNum, list_holder, it_holder);
			boolean hasvalue = (list_holder == null || list_holder.value == null) ? false
					: true;
			boolean hasnext = (it_holder == null || it_holder.value == null) ? false
					: true;
			long total = (hasvalue ? list_holder.value.length : 0)
					+ (hasnext ? it_holder.value.getLength() : 0);
			
			if (hasnext) {
				List<SubnetworkConnection_T> dataList=new ArrayList<SubnetworkConnection_T>();
				dataList.addAll(Arrays.asList(list_holder.value));
				while (hasnext) {
					hasnext=it_holder.value.next_n(iteratorNum, list_holder);
					dataList.addAll(Arrays.asList(list_holder.value));
				}
				dataModel=new SubnetworkConnection_T[dataList.size()];
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
	

}