package com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributes_THelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omg.CORBA.TCKind;

import HW.CosNotification.Property;
import HW.CosNotification.StructuredEvent;
import HW.HW_mstpInventory.HW_ForwardEndPoint_T;
import HW.HW_mstpInventory.HW_MSTPBindingPath_T;
import HW.HW_mstpInventory.HW_MSTPEndPoint_T;
import HW.HW_mstpInventory.HW_VirtualBridge_T;
import HW.HW_mstpInventory.HW_VirtualLAN_T;
import HW.HW_mstpService.HW_EthService_T;
import HW.emsMgr.ClockSourceStatus_T;
import HW.emsMgr.EMS_T;
import HW.equipment.EquipmentHolder_T;
import HW.equipment.EquipmentOrHolder_T;
import HW.equipment.Equipment_T;
import HW.managedElement.CommunicationState_T;
import HW.managedElement.CommunicationState_THelper;
import HW.managedElement.ManagedElement_T;
import HW.notifications.NVList_THelper;
import HW.notifications.NameAndAnyValue_T;
import HW.notifications.ObjectType_T;
import HW.notifications.ObjectType_THelper;
import HW.notifications.PerceivedSeverity_THelper;
import HW.notifications.ServiceAffecting_THelper;
import HW.performance.PMData_T;
import HW.performance.PMMeasurement_T;
import HW.performance.PMThresholdType_THelper;
import HW.protection.EProtectionGroup_T;
import HW.protection.ProtectionGroup_T;
import HW.protection.ProtectionType_THelper;
import HW.protection.SwitchReason_THelper;
import HW.protection.WDMProtectionGroup_T;
import HW.subnetworkConnection.CrossConnect_T;
import HW.terminationPoint.TerminationPoint_T;
import HW.topologicalLink.TopologicalLink_T;
import HW.transmissionParameters.LayeredParameters_T;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ClockSourceStatusModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EmsDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentOrHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EthServiceModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ForwardEndPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.LayeredParametersModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.MSTPBindingPathModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionSwtichDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TCADataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualLANModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.service.DataToModel;
import com.fujitsu.model.PtpDomainModel;
import com.fujitsu.util.NameAndStringValueUtil;
import com.fujitsu.util.XmlUtil;

/**
 * @author xuxiaojun
 * 
 */
public class HWDataToModel extends DataToModel{
	
	public HWDataToModel(String encode) {
		super(encode);
	}

	/**
	 * 将网管信息数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public EmsDataModel EmsDataToModel(EMS_T data) {

		EmsDataModel model = new EmsDataModel();
		
		model.setAdditionalInfo(data.additionalInfo);
		model.setEmsVersion(data.emsVersion);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setType(data.type);
		model.setUserLabel(data.userLabel);
		
		//extend
		model.setInternalEmsName(data.name[0].value);
		//InterfaceVersion
		for(NameAndStringValue_T info:data.additionalInfo){
			if("InterfaceVersion".equals(info.name)){
				model.setInterfaceVersion(info.value);
				break;
			}
		}

		return model;
	}
	

	/**
	 * 将告警数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public AlarmDataModel AlarmDataToModel(Object event) {

		StructuredEvent data = (StructuredEvent)event;
		
		AlarmDataModel model = new AlarmDataModel();
		
		try {
			// NotificationId
			model.setNotificationId(data
					.filterable_data[DataCollectDefine.HW.ALARM_NOTIFICATION_ID].value.extract_string());
			// ObjectName
			model.setObjectName(NamingAttributes_THelper.read(data
					.filterable_data[DataCollectDefine.HW.ALARM_OBJECT_NAME].value.create_input_stream()));
			// NativeEMSName
			String nativeEmsName = data.filterable_data[DataCollectDefine.HW.ALARM_NATIVE_EMS_NAME].value.extract_string();
			model.setNativeEmsName(NameAndStringValueUtil.Stringformat(nativeEmsName,encode));
			// NateiveProbableCause
			String nateiveProbableCause = data.filterable_data[DataCollectDefine.HW.ALARM_NATIVE_PROBABLE_CAUSE].value.extract_string();
			model.setNativeProbableCause(NameAndStringValueUtil.Stringformat(nateiveProbableCause,encode));
			// ObjectType
			model.setObjectType(ObjectType_THelper.read(data
					.filterable_data[DataCollectDefine.HW.ALARM_OBJECT_TYPE].value.create_input_stream()).value());
			// EmsTime
			model.setEmsTime(DateStrFormatForAlarm(data
					.filterable_data[DataCollectDefine.HW.ALARM_EMS_TIME].value.extract_string()));
			// NeTime
			model.setNeTime(DateStrFormatForAlarm(data
					.filterable_data[DataCollectDefine.HW.ALARM_NE_TIME].value.extract_string()));
			// Clearable
			model.setClearable(data
					.filterable_data[DataCollectDefine.HW.ALARM_IS_CLEARABLE].value.extract_boolean());
			// LayerRate
			model.setLayerRate(data
					.filterable_data[DataCollectDefine.HW.ALARM_LAYER_RATE].value.extract_short());
			// ProbableCause
			model.setProbableCause(NameAndStringValueUtil.Stringformat(data
					.filterable_data[DataCollectDefine.HW.ALARM_PROBABLE_CAUSE].value.extract_string(), encode));
			// ProbableCauseQualifier
			model.setProbableCauseQualifier(NameAndStringValueUtil.Stringformat(data
					.filterable_data[DataCollectDefine.HW.ALARM_PROBABLE_CAUSE_QUALIFIER].value.extract_string(), encode));
			// PerceivedSeverity
			model.setPerceivedSeverity(PerceivedSeverity_THelper.read(data
					.filterable_data[DataCollectDefine.HW.ALARM_PERCEIVED_SEVERITY].value.create_input_stream()).value());
			// ServiceAffecting
			model.setServiceAffecting(ServiceAffecting_THelper.read(data
					.filterable_data[DataCollectDefine.HW.ALARM_SERVICE_AFFECTING].value.create_input_stream()).value());
			// AffectedTPList
			// FIXME AffectedTPList太大内存溢出,暂时注释
//			model.setAffectedTPList(NamingAttributes_THelper.read(data
//					.filterable_data[DataCollectDefine.HW.AFFECTED_TPLIST].value.create_input_stream()));
			// AdditionalInfo
			model.setAdditionalInfo(NamingAttributes_THelper.read(data
					.filterable_data[DataCollectDefine.HW.ALARM_ADDITIONAL_INFO].value.create_input_stream()));
			// EventType-->alarmType
			String alarmType = data.filterable_data[DataCollectDefine.HW.ALARM_X733_EVENTTYPE].value.extract_string();
			/*"communicationsAlarm"
			"qualityofServiceAlarm"
			"equipmentAlarm"
			"processingErrorAlarm"
			"environmentalAlarm"
			"securityAlarm"*/
			model.setAlarmType(getAlarmType(alarmType,DataCollectDefine.FACTORY_HW_FLAG));
		
			// ObjectTypeQualifier
			model.setObjectTypeQualifier(NameAndStringValueUtil.Stringformat(data
					.filterable_data[DataCollectDefine.HW.ALARM_OBJECT_TYPE_QUALIFIER].value.extract_string(), encode));
			
			// AdditionalInfo相关内容解析
			for(NameAndStringValue_T tmp:model.getAdditionalInfo()){
				// Direction
				if (tmp.name.equals("Direction")) {
					model.setDirection(NameAndStringValueUtil.Stringformat(tmp.value, encode));
				}
				// Location
				else if (tmp.name.equals("Location")) {
					model.setLocation(NameAndStringValueUtil.Stringformat(tmp.value, encode));
				}
				// LocationInfo
				else if (tmp.name.equals("LocationInfo")) {
				model.setLocationInfo(NameAndStringValueUtil.Stringformat(tmp.value, encode));
				}
				// ConfirmStatusOri
				else if (tmp.name.equals("AffirmState")) {
					model.setConfirmStatusOri(NameAndStringValueUtil.Stringformat(tmp.value, encode));
				}
				// HandlingSuggestion
				else if (tmp.name.equals("HandlingSuggestion")) {
				model.setHandlingSuggestion(NameAndStringValueUtil.Stringformat(tmp.value,encode));
				}
				// AlarmSerialNo
				else if (tmp.name.equals("AlarmSerialNo")) {
					model.setAlarmSerialNo(tmp.value);
				}
				// AlarmReason
				else if (tmp.name.equals("AlarmReason")) {
				model.setAlarmReason(NameAndStringValueUtil.Stringformat(tmp.value,encode));
				}
			}
			
			// ClearTime(华为设备无此对应属性，BD要求使用EmsTime)
			model.setClearTime(DateStrFormatForAlarm(data.filterable_data[DataCollectDefine.HW.ALARM_EMS_TIME].value.extract_string()));
			
			String originalInfo=com.fujitsu.util.SerializerUtil.toJSON(event, encode, false);
			model.setOriginalInfo(originalInfo);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * 将交叉连接数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public CrossConnectModel CCDataToModel(CrossConnect_T data) {

		CrossConnectModel model = new CrossConnectModel();

		model.setActive(data.active);
		model.setAdditionalInfo(data.additionalInfo);
		model.setaEndNameList(data.aEndNameList);
		model.setCcType(data.ccType.value());
		model.setDirection(data.direction.value());
		model.setzEndNameList(data.zEndNameList);
		
		//extend
		if(data.additionalInfo!=null&&data.additionalInfo.length>0){
			for(NameAndStringValue_T name:data.additionalInfo){
				if(name.name.equals("ClientType")){
					model.setClientType(name.value);
				}
				else if(name.name.equals("ClientRate")){
					model.setClientRate(name.value);
				}
				else if(name.name.equals("Fixed")){
					boolean isFixed = name.value!=null?Boolean.valueOf(name.value):false;
					model.setFixed(isFixed);
				}
			}
		}
		return model;
	}

	/**
	 * 将设备数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public EquipmentOrHolderModel EquipmentOrHolderDataToModel(
			EquipmentOrHolder_T data) {

		EquipmentOrHolderModel model = new EquipmentOrHolderModel();

		model.setDiscriminator(data.discriminator().value());
		if (model.getDiscriminator() == DataCollectDefine.COMMON.EQT_HOLDER_FLAG) {
			model.setHolder(EquipmentHolderDataToModel(data.holder()));
		} else if (model.getDiscriminator() == DataCollectDefine.COMMON.EQT_FLAG) {
			model.setEquip(EquipmentDataToModel(data.equip()));
		}
		// model.setUninitialized(data.);
		return model;
	}

	/**
	 * 将网元数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public ManagedElementModel ManagedElementDataToModel(ManagedElement_T data) {

		ManagedElementModel model = new ManagedElementModel();

		// base
		model.setAdditionalInfo(data.additionalInfo);
		model.setCommunicationState(data.communicationState.value());
		model.setEmsInSyncState(data.emsInSyncState);
		model.setLocation(data.location);
		model.setName(data.name);
		model.setNativeEMSNameOri(data.nativeEMSName);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setProductName(data.productName);
		model.setSupportedRates(data.supportedRates);
		model.setUserLabel(data.userLabel);
		model.setVersion(data.version);

		for(NameAndStringValue_T info:data.additionalInfo){
			if("IPAddress".equals(info.name)){
				model.setNetAddress(info.value);
			}else if("AlarmSeverity".equals(info.name)){
				model.setAlarmStatus(DataCollectDefine.COMMON.PerceivedSeverity.toValue(info.value));
			}
		}
		//华为无
		// model.setDescriptionInfo(descriptionInfo);
		// model.setHardwareVersion(hardwareVersion);
		// model.setMeType(meType);
		// model.setOperationalStatus(operationalStatus);
		// model.setSoftwareVersion(softwareVersion);
		// model.setVendorName(vendorName);

		// extend
		model.setNeSerialNo(nameUtil.getNeSerialNo(model.getName()));

		return model;
	}

	/**
	 * 将性能数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 * @throws CommonException
	 */
	public PmDataModel PMDataToModel(PMData_T data) throws CommonException {

		PmDataModel model = new PmDataModel();
		model.setGranularity(data.granularity);
		model.setLayerRate(data.layerRate);

		List<PmMeasurementModel> pmMeasurementModelList = new ArrayList<PmMeasurementModel>();
		for (PMMeasurement_T pmMeasurement : data.pmMeasurementList) {
			PmMeasurementModel pmMeasurementModel = new PmMeasurementModel();
			pmMeasurementModel = PmMeasurementDataToModel(pmMeasurement);
			pmMeasurementModelList.add(pmMeasurementModel);
		}
		model.setPmMeasurementList(pmMeasurementModelList);
		model.setRetrievalTime(data.retrievalTime);
		model.setTpName(data.tpName);

		// extend
		model.setTpNameString(nameUtil.decompositionName(data.tpName));

		// 设置targetType
		// 名称长度为3 可能为ptp,ftp,equipmentHolder
		if (model.getTpName().length == 3) {
			// ptp类型
			if (model.getTpName()[2].name.equals(DataCollectDefine.COMMON.PTP)
					|| model.getTpName()[2].name
							.equals(DataCollectDefine.COMMON.FTP)) {
				model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG);
			}
		}
		// 名称长度为4 可能为equipment,ctp
		else if (model.getTpName().length == 4) {
			// ctp类型
			if (model.getTpName()[3].name.equals(DataCollectDefine.COMMON.CTP)) {
				//设置name
				model.setTpNameString(nameUtil.decompositionCtpName(model.getTpName()));
				model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_CTP_FLAG);
				// model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG);

			}
			// equipment类型
			else if (model.getTpName()[3].name
					.equals(DataCollectDefine.COMMON.EQUIPMENT)) {
				model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_EQUIPMENT_FLAG);
			}
		}
		// 设置周期
		if (model.getGranularity().equals(
				DataCollectDefine.COMMON.GRANULARITY_15MIN_STRING)) {
			model.setGranularityFlag(DataCollectDefine.COMMON.GRANULARITY_15MIN_FLAG);
		} else if (model.getGranularity().equals(
				DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING)) {
			model.setGranularityFlag(DataCollectDefine.COMMON.GRANULARITY_24HOUR_FLAG);
		}

		// 设置接收时间
		model.setRetrievalTimeDisplay(DateStrFormatForPM(data.retrievalTime, DataCollectDefine.FACTORY_HW_FLAG));

		return model;
	}

	/**
	 * 将ptp数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 * @throws CommonException 
	 */
	public TerminationPointModel TerminationPointDataToModel(
			TerminationPoint_T data) throws CommonException {

		TerminationPointModel model = new TerminationPointModel();
		//是否同步过ctp，默认false
		model.setIsSyncCtp(DataCollectDefine.FALSE);
		model.setAdditionalInfo(data.additionalInfo);
		model.setConnectionState(data.connectionState.value());
		model.setDirection(data.direction.value());
		model.setEdgePoint(data.edgePoint);
		model.setEgressTrafficDescriptorName(data.egressTrafficDescriptorName);
		model.setIngressTrafficDescriptorName(data.ingressTrafficDescriptorName);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setTpMappingMode(data.tpMappingMode.value());
		model.setTpProtectionAssociation(data.tpProtectionAssociation.value());

		List<LayeredParametersModel> layeredParametersModelList = new ArrayList<LayeredParametersModel>();
		
		List<Short> layerRateList = new ArrayList<Short>();
		for (LayeredParameters_T layeredParameters : data.transmissionParams) {
			layerRateList.add(layeredParameters.layer);
			LayeredParametersModel layeredParametersModel = new LayeredParametersModel();
			layeredParametersModel = LayeredParametersDataToModel(layeredParameters);
			layeredParametersModelList.add(layeredParametersModel);
		}
		model.setTransmissionParams(layeredParametersModelList);

		model.setType(data.type.value());
		model.setUserLabel(data.userLabel);

		// model.setIncludeTPList(includeTPList);

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		
		//
		model.setLayerRateString(getLayRateSrting(model.getTransmissionParams()));
		//
		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.PORT);
		String domain = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.HW.HW_DOMAIN);
		String ptpType = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.HW.HW_TYPE);

		
		String rate = "";
		
		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setPortNo(portNo);
		//BD corba数据入库整理文档 -- domain规律
		//获取xml文件中配置内容
		if(ptpDomainList == null){
			ptpDomainList = XmlUtil.parserXmlForPtpDomain("HW_PTP_DOMAIN.xml"); 
		}

		//初始化domain为未知
		model.setDomain(DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG);
		//循环配置项
		for(PtpDomainModel temp:ptpDomainList){
			//domain相同继续
			if(temp.getDomain().toLowerCase().equals(domain.toLowerCase())){
				
				model.setDomain(temp.getDomainFlag());
				
				//判断ptp type是否为空
				if(ptpType == null || ptpType.isEmpty()){
					//配置项中的层速率是否在ptp层速率列表中
					if(layerRateList.containsAll(temp.getLayerList())){
						ptpType = temp.getPtpType();
						rate = temp.getRate();
						break;
					}
				}else{
					break;
				}
			}
		}
		//设置ptpType和rate
		if(ptpType!=null){
			model.setPtpType(ptpType.toUpperCase());
		}else{
			model.setPtpType(ptpType);
		}
		model.setRate(rate);

		//
		int ptpOrFtp = DataCollectDefine.COMMON.PTP_FLAG;
		if (model.getName()[2].name.equals(DataCollectDefine.COMMON.FTP)) {
			ptpOrFtp = DataCollectDefine.COMMON.FTP_FLAG;
		}

		model.setPtpOrFtp(ptpOrFtp);

		//for ctp
		if(model.getName().length == 4&&model.getName()[3].name.equals(DataCollectDefine.COMMON.CTP)){
			
			//设置name
			model.setNameString(nameUtil.decompositionCtpName(model.getName()));
			
			//设置ctp Value
			String ctpValue = model.getName()[3].value;
			
			if(ctpValue.startsWith("/")){
				ctpValue = ctpValue.substring(1);
			}
			model.setCtpValue(ctpValue);
		}
		return model;
	}

	/**
	 * 将mstp数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public TerminationPointModel MstpEndPointDataToModel(
			HW_MSTPEndPoint_T data) {

		TerminationPointModel model = new TerminationPointModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setDirection(data.direction.value());
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		List<LayeredParametersModel> layeredParametersModelList = new ArrayList<LayeredParametersModel>();
		for (LayeredParameters_T layeredParameters : data.transmissionParams) {
			LayeredParametersModel layeredParametersModel = new LayeredParametersModel();
			layeredParametersModel = LayeredParametersDataToModel(layeredParameters);
			layeredParametersModelList.add(layeredParametersModel);
		}
		model.setTransmissionParams(layeredParametersModelList);

		model.setType(data.type.value());
		model.setUserLabel(data.userLabel);

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		//
		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.PORT);
		String domain = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.HW.HW_DOMAIN);
		String ptpType = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.HW.HW_TYPE);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setPortNo(portNo);
		//
		if (DataCollectDefine.COMMON.DOMAIN_SDH_.equals(domain.toLowerCase())) {
			model.setDomain(DataCollectDefine.COMMON.DOMAIN_SDH_FLAG);
		} else if (DataCollectDefine.COMMON.DOMAIN_WDM_.equals(domain)) {
			model.setDomain(DataCollectDefine.COMMON.DOMAIN_WDM_FLAG);
		} else if (DataCollectDefine.COMMON.DOMAIN_ETH_.equals(domain)) {
			model.setDomain(DataCollectDefine.COMMON.DOMAIN_ETH_FLAG);
		} else if (DataCollectDefine.COMMON.DOMAIN_ATM_.equals(domain)) {
			model.setDomain(DataCollectDefine.COMMON.DOMAIN_ATM_FLAG);
		} else {
			model.setDomain(DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG);
		}

		//设置ptpType
		if(ptpType!=null){
			model.setPtpType(ptpType.toUpperCase());
		}else{
			model.setPtpType(ptpType);
		}

		//
		int ptpOrFtp = DataCollectDefine.COMMON.PTP_FLAG;
		if (model.getName()[2].name.equals(DataCollectDefine.COMMON.FTP)) {
			ptpOrFtp = DataCollectDefine.COMMON.FTP_FLAG;
		}

		model.setPtpOrFtp(ptpOrFtp);

		//
		model.setLayerRateString(getLayRateSrting(model.getTransmissionParams()));
		
		String rate = "";
		
		//从transmissionParams中获取速率
		if(model.getDomain() == DataCollectDefine.COMMON.DOMAIN_ETH_FLAG
				&& model.getPtpType().equals(DataCollectDefine.COMMON.PTP_TYPE_OTHER_MAC)){
			for(LayeredParametersModel parameterModel :model.getTransmissionParams()){
				for(NameAndStringValue_T name:parameterModel.getTransmissionParams()){
					if(name.name.equals("WorkingMode")){
						if(name.value.contains("M")){
							rate = name.value.substring(0, name.value.indexOf("M")+1);
							break;
						}
					}
				}
			}
		}
		model.setRate(rate);
		
		return model;
	}

	/**
	 * 将HWForwardEndPoint数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public ForwardEndPointModel ForwardEndPointDataToModel(
			HW_ForwardEndPoint_T data) {

		ForwardEndPointModel model = new ForwardEndPointModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setLogicTPName(data.logicTPName);
		model.setParaList(data.paraList);

		return model;
	}

	/**
	 * 将EthService数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public EthServiceModel EthServiceDataToModel(HW_EthService_T data) {

		EthServiceModel model = new EthServiceModel();

		model.setName(data.name);
		model.setUserLabel(data.userLabel);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setServiceType(data.serviceType.value());
		model.setDirection(data.direction.value());
		model.setActiveState(data.activeState);
		
		model.setAdditionalInfo(data.additionalInfo);

		//aEndPoint属性
		model.setaEndPoint(data.aEndPoint.name);
		model.setaEndPointVlanID(data.aEndPoint.vlanID);
		model.setaEndPointTunnel(data.aEndPoint.tunnel);
		model.setaEndPointVc(data.aEndPoint.vc);
		model.setaEndPointAdditionalInfo(data.aEndPoint.additionalInfo);
		//zEndPoint属性
		model.setzEndPoint(data.zEndPoint.name);
		model.setzEndPointVlanID(data.zEndPoint.vlanID);
		model.setzEndPointTunnel(data.zEndPoint.tunnel);
		model.setzEndPointVc(data.zEndPoint.vc);
		model.setzEndPointAdditionalInfo(data.zEndPoint.additionalInfo);

		//extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		model.setaEndNESerialNo(nameUtil.getNeSerialNo(model.getaEndPoint()));
		model.setaEndPointName(nameUtil.decompositionName(model.getaEndPoint()));
		model.setzEndNESerialNo(nameUtil.getNeSerialNo(model.getzEndPoint()));
		model.setzEndPointName(nameUtil.decompositionName(model.getzEndPoint()));

		return model;
	}

	/**
	 * 将HWVirtualBridge数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public VirtualBridgeModel VirtualBridgeDataToModel(
			HW_VirtualBridge_T data) {

		VirtualBridgeModel model = new VirtualBridgeModel();

		model.setName(data.name);
		model.setUserLabel(data.userLabel);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);

		List<TerminationPointModel> logicalTPList = new ArrayList<TerminationPointModel>();
		for (HW_MSTPEndPoint_T logicalTP : data.logicalTPList) {
			TerminationPointModel point = MstpEndPointDataToModel(logicalTP);
			logicalTPList.add(point);
		}
		model.setLogicalTPList(logicalTPList);
		model.setAdditionalInfo(data.additionalInfo);

		return model;
	}

	/**
	 * 将 HWVirtualLAN数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public VirtualLANModel VirtualLANDataToModel(HW_VirtualLAN_T data) {

		VirtualLANModel model = new VirtualLANModel();

		model.setName(data.name);
		model.setUserLabel(data.userLabel);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);

		model.setParaList(data.paraList);
		List<ForwardEndPointModel> forwardTPList = new ArrayList<ForwardEndPointModel>();
		for (HW_ForwardEndPoint_T forwardTP : data.forwardTPList) {
			ForwardEndPointModel point = ForwardEndPointDataToModel(forwardTP);
			forwardTPList.add(point);
		}
		model.setForwardTPList(forwardTPList);
		model.setAdditionalInfo(data.additionalInfo);

		return model;
	}

	/**
	 * 将MSTPBindingPath数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public MSTPBindingPathModel MSTPBindingPathToModel(
			HW_MSTPBindingPath_T data) {

		MSTPBindingPathModel model = new MSTPBindingPathModel();

		model.setDirection(data.direction.value());
		model.setAllPathList(data.allPathList);
		model.setUsedPathList(data.usedPathList);
		model.setAdditionalInfo(data.additionalInfo);

		return model;
	}

	/**
	 * 将link数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public TopologicalLinkModel TopologicalLinkDataToModel(
			TopologicalLink_T data) {

		TopologicalLinkModel model = new TopologicalLinkModel();
		model.setAdditionalInfo(data.additionalInfo);
		model.setaEndTP(data.aEndTP);
		model.setDirection(data.direction.value());
		model.setName(data.name);
		model.setRate(data.rate);
		model.setUserLabel(data.userLabel);
		model.setzEndTP(data.zEndTP);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		model.setaEndNESerialNo(nameUtil.getNeSerialNo(model.getaEndTP()));
		model.setaEndPtpName(nameUtil.decompositionName(model.getaEndTP()));
		model.setzEndNESerialNo(nameUtil.getNeSerialNo(model.getzEndTP()));
		model.setzEndPtpName(nameUtil.decompositionName(model.getzEndTP()));

		return model;
	}

	/**
	 * 将EProtectionGroup_T数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public EProtectionGroupModel EProtectionGroupDataToModel(
			EProtectionGroup_T data) {

		EProtectionGroupModel model = new EProtectionGroupModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setUserLabel(data.userLabel);

		model.seteProtectionGroupType(data.eProtectionGroupType);
		model.setProtectionSchemeState(data.protectionSchemeState.value());
		model.setReversionMode(data.reversionMode.value());
		model.setProtectedList(data.protectedList);
		model.setProtectingList(data.protectingList);
		model.setePgpParameters(data.ePgpParameters);
		
		//extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		
		String[] param = model.getNameString().split("/");
		if(param.length == 4){
			model.setEpgpGroup(Integer.valueOf(param[2]));
			model.setEpgpLocation(Integer.valueOf(param[3]));
		}
		
		for(NameAndStringValue_T name:data.ePgpParameters){
			if(name.name.equals("type")){
				model.setType(name.value);
			}
			else if(name.name.equals("wtrTime")){
				model.setWtrTime(name.value);
			}
		}

		return model;
	}

	/**
	 * 将ProtectionGroup_T数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public ProtectionGroupModel ProtectionGroupDataToModel(
			ProtectionGroup_T data) {

		ProtectionGroupModel model = new ProtectionGroupModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setUserLabel(data.userLabel);

		model.setProtectionGroupType(data.protectionGroupType.value());
		model.setProtectionSchemeState(data.protectionSchemeState.value());
		model.setReversionMode(data.reversionMode.value());
		model.setRate(data.rate);
		model.setPgpTPList(data.pgpTPList);
		model.setPgpParameters(data.pgpParameters);
		
		//extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		
		String[] param = model.getNameString().split("/");
		if(param.length == 3){
			model.setPgpGroup(Integer.valueOf(param[1]));
			model.setPgpLocation(Integer.valueOf(param[2]));
		}
		
		for(NameAndStringValue_T name:data.pgpParameters){
			if(name.name.equals("SwitchMode")){
				model.setSwitchMode(name.value);
			}
			else if(name.name.equals("wtrTime")){
				model.setWtrTime(name.value);
			}
			else if(name.name.equals("HoldOffTime")){
				model.setHoldOffTime(name.value);
			}
			else if(name.name.equals("LODNumSwitches")){
				model.setLodNumSwitches(name.value);
			}
			else if(name.name.equals("LODDuration")){
				model.setLodDuration(name.value);
			}
			else if(name.name.equals("SPRINGProtocol")){
				model.setSpringProtocol(name.value);
			}
			else if(name.name.equals("SPRINGNodeId")){
				model.setSpringNodeId(name.value);
			}
			else if(name.name.equals("SwitchPosition")){
				model.setSwitchPosition(name.value);
			}
			else if(name.name.equals("nonPre-EmptibleTraffic")){
				model.setNonPreEmptibleTraffic(name.value);
			}
		}

		return model;
	}

	/**
	 * 将WDMProtectionGroup_T数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public WDMProtectionGroupModel WDMProtectionGroupDataToModel(
			WDMProtectionGroup_T data) {

		WDMProtectionGroupModel model = new WDMProtectionGroupModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setUserLabel(data.userLabel);

		model.setProtectionGroupType(data.protectionGroupType);
		model.setProtectionSchemeState(data.protectionSchemeState.value());
		model.setReversionMode(data.reversionMode.value());
		model.setPgpTPList(data.pgpTPList);
		model.setPgpParameters(data.pgpParameters);
		
		//extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		
//		String[] param = model.getNameString().split("/");
//		if(param.length == 4){
//			model.setPgpGroup(Integer.valueOf(param[2]));
//			model.setPgpLocation(Integer.valueOf(param[3]));
//		}
		
		for(NameAndStringValue_T name:data.pgpParameters){
			if(name.name.equals("wtrTime")){
				model.setWtrTime(name.value);
			}
			else if(name.name.equals("HoldOffTime")){
				model.setHoldOffTime(name.value);
			}
		}

		return model;
	}

	/**
	 * 将ClockSourceStatus_T数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public ClockSourceStatusModel ClockSourceStatusDataToModel(
			ClockSourceStatus_T data) {

		ClockSourceStatusModel model = new ClockSourceStatusModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));

		model.setStatus(data.status);
		model.setTimingMode(data.timingMode);
		model.setQuality(data.quality);
		model.setWorkingMode(data.workingMode);
		//
		model.setNameString(nameUtil.decompositionName(model.getName()));
		//
		if ("CURRENT".equals(model.getStatus().trim())) {
			model.setCurrent(true);
		} else {
			model.setCurrent(false);
		}

		//
		// 外时钟
		if ("External Source".equals(model.getTimingMode().trim())) {
			model.setTimingModeFlag(0);
		}
		// 抽时钟

		// 内时钟
		else if ("Internal Clock Source".equals(model.getTimingMode().trim())) {
			model.setTimingModeFlag(2);
		}
		// 时钟保持
		// 抽支路时钟
		// NA
		else {
			model.setTimingModeFlag(5);
		}

		// 时钟质量
		// 未知
		if ("Unknown Synchronization Quality".equals(model.getQuality().trim())) {
			model.setQualityFlag(0);
		}
		// G.811
		else if (model.getQuality().trim().contains("G.811")) {
			model.setQualityFlag(1);
		}
		// G.812Transit
		else if (model.getQuality().trim().contains("G.812Transit")) {
			model.setQualityFlag(2);
		}
		// G.812Local
		else if (model.getQuality().trim().contains("G.812Local")) {
			model.setQualityFlag(3);
		}
		// G.813
		else if (model.getQuality().trim().contains("G.813")) {
			model.setQualityFlag(4);
		}
		// 非同步时钟源
		else if ("Do Not Use For Synchronization".equals(model.getQuality()
				.trim())) {
			model.setQualityFlag(5);
		}

		// 工作模式
		if (!model.getWorkingMode().isEmpty()) {
			model.setWorkingModeFlag(0);
		} else {
			model.setWorkingModeFlag(1);
		}

		return model;
	}

	/**
	 * 将性能具体数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	private PmMeasurementModel PmMeasurementDataToModel(PMMeasurement_T data) {

		PmMeasurementModel model = new PmMeasurementModel();

		model.setIntervalStatus(data.intervalStatus);
		model.setPmLocation(data.pmLocation);
		model.setPmParameterName(data.pmParameterName);
		// Notice: 单位仅为 UTF-8, 非网管编码
		model.setUnit(NameAndStringValueUtil.Stringformat(data.unit,encode));
		model.setValue(String.valueOf(data.value));
		model.setLocationFlag(getPmLocationFlag(data.pmLocation,DataCollectDefine.FACTORY_HW_FLAG));
		return model;
	}

	/**
	 * 将承载设备数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	private EquipmentHolderModel EquipmentHolderDataToModel(
			EquipmentHolder_T data) {

		EquipmentHolderModel model = new EquipmentHolderModel();

		model.setAcceptableEquipmentTypeList(Arrays.asList(
				data.acceptableEquipmentTypeList));

		model.setAcceptableEquipmentTypeSrting(getAcceptableEquipmentTypeSrting(data.acceptableEquipmentTypeList));

		model.setAdditionalInfo(data.additionalInfo);
		model.setAlarmReportingIndicator(data.alarmReportingIndicator);
		model.setExpectedOrInstalledEquipment(data.expectedOrInstalledEquipment);

		model.setHolderState(data.holderState.value());
		model.setHolderType(data.holderType);

		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setUserLabel(data.userLabel);

		// model.setHardwareVersion(hardwareVersion);
		// model.setLocation(location);
		// model.setSerialNo(serialNo);
		// model.setVendorName(vendorName)

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));

		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);

		// AdditionalInfo相关内容解析
		for(NameAndStringValue_T tmp:model.getAdditionalInfo()){
			// ProductName
			if (tmp.name.equals("ProductName")) {
				model.setVendorName(NameAndStringValueUtil.Stringformat(tmp.value, encode));
			}
			// ShelfType
			if (tmp.name.equals("ShelfType")) {
				model.setShelfType(NameAndStringValueUtil.Stringformat(tmp.value, encode));
			}
		}

		return model;
	}

	/**
	 * 将设备数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	private EquipmentModel EquipmentDataToModel(Equipment_T data) {

		EquipmentModel model = new EquipmentModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setAlarmReportingIndicator(data.alarmReportingIndicator);
		model.setExpectedEquipmentObjectType(data.expectedEquipmentObjectType);
		model.setInstalledEquipmentObjectType(data.installedEquipmentObjectType);
		model.setInstalledPartNumber(data.installedPartNumber);
		model.setInstalledSerialNumber(data.installedSerialNumber);
		model.setInstalledVersion(data.installedVersion);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setServiceState(data.serviceState.value());
		model.setUserLabel(data.userLabel);

		// model.setHardwareVersion(hardwareVersion);
		// model.setHasProtection(hasProtection);
		// model.setInstalledBoardType(installedBoardType);
		// model.setSoftwareVersion(softwareVersion);
		// model.setExpectedBoardType(expectedBoardType);

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));

		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);

		return model;
	}

	/**
	 * 将设备数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	private LayeredParametersModel LayeredParametersDataToModel(
			LayeredParameters_T data) {

		LayeredParametersModel model = new LayeredParametersModel();

		model.setLayer(data.layer);
		model.setTransmissionParams(data.transmissionParams);

		return model;
	}

	/**
	 * 将NT_STATE_CHANGE消息数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public StateDataModel StateDataToModel(Object event) {

		StructuredEvent data = (StructuredEvent)event;
		StateDataModel model = new StateDataModel();
		String notificationId = null;
		// 目标名称
		NameAndStringValue_T[] objectName = null;
		// 目标类型
		ObjectType_T objectType = null;
		String objectTypeQualifier = null;
		String emsTime = null;
		String neTime = null;
		boolean edgePointRelated = false;
		List<StateDataModel.State> state = new ArrayList<StateDataModel.State>();

		for(Property property:data.filterable_data){
			if(property.name.equals("notificationId")){
				notificationId = property.value.extract_string();
			}else if(property.name.equals("objectName")){
				objectName = NamingAttributes_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("objectType")){
				objectType = ObjectType_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("objectTypeQualifier")){
				objectTypeQualifier = property.value.extract_string();
			}else if(property.name.equals("emsTime")){
				emsTime = property.value.extract_string();
			}else if(property.name.equals("neTime")){
				neTime = property.value.extract_string();
			}else if(property.name.equals("edgePointRelated")){
				edgePointRelated = property.value.extract_boolean();
			}else if(property.name.equals("attributeList")){
				NameAndAnyValue_T[] attributeList = NVList_THelper.read(
						property.value.create_input_stream());
				for(NameAndAnyValue_T attribute:attributeList){
					if("communicationState".equals(attribute.name)){
						int stateValue=CommunicationState_T._CS_UNAVAILABLE;
						//U2000返回类型为String
						if(attribute.value.type().kind().value()==18){//_tk_string
						if("CS_AVAILABLE".equals(attribute.value.extract_string())){
								stateValue=CommunicationState_T._CS_AVAILABLE;
						}else{
								stateValue=CommunicationState_T._CS_UNAVAILABLE;
						}
						}else if(attribute.value.type().kind().value()==8){//_tk_boolean
							if(attribute.value.extract_boolean()){
								stateValue=CommunicationState_T._CS_AVAILABLE;
							}else{
								stateValue=CommunicationState_T._CS_UNAVAILABLE;
							}
						}else{//T2000返回类型为CommunicationState_T
							stateValue=CommunicationState_THelper.read(attribute.value.create_input_stream()).value();
						}
						state.add(model.new State(attribute.name,stateValue));
					}
				}
			}else{
				System.out.println("StructuredEvent unknow Property:"+property.name);
			}
		}
		model.setNotificationId(notificationId);
		model.setObjectName(objectName);
		if(objectType!=null)
			model.setObjectType(objectType.value());
		model.setObjectTypeQualifier(objectTypeQualifier);
		model.setEmsTime(emsTime);
		model.setNeTime(neTime);
		model.setEdgePointRelated(edgePointRelated);
		model.setState(state.toArray(new StateDataModel.State[state.size()]));
		
		return model;
	}

	/**
	 * 将TCA数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public TCADataModel TCADataToModel(Object event) {
		
		StructuredEvent data = (StructuredEvent)event;
		
		TCADataModel model = new TCADataModel();
		
		try {
			for(Property p:data.filterable_data){
				//notificationId
				if("notificationId".toUpperCase().equals(p.name.toUpperCase())){
					model.setNotificationId(p.value.extract_string());
				}else if("objectName".toUpperCase().equals(p.name.toUpperCase())){
					//objectName
					model.setObjectName(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}else if("nativeEMSName".toUpperCase().equals(p.name.toUpperCase())){
					// NativeEMSName
					String nativeEmsName = p.value.extract_string();
					model.setNativeEMSName(NameAndStringValueUtil.Stringformat(nativeEmsName,encode));
				}else if("objectType".toUpperCase().equals(p.name.toUpperCase())){
					// objectType
					model.setObjectType(ObjectType_THelper.read(p.value.create_input_stream()).value());
				}else if("emsTime".toUpperCase().equals(p.name.toUpperCase())){
					// EmsTime
					model.setEmsTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("neTime".toUpperCase().equals(p.name.toUpperCase())){
					// NeTime
					model.setNeTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("isClearable".toUpperCase().equals(p.name.toUpperCase())){
					//isClearable;
					model.setClearable(p.value.extract_boolean());
				}else if("perceivedSeverity".toUpperCase().equals(p.name.toUpperCase())){
					//perceivedSeverity;
					model.setPerceivedSeverity(PerceivedSeverity_THelper.read(p.value.create_input_stream()).value());
				}else if("layerRate".toUpperCase().equals(p.name.toUpperCase())){
					//layerRate;
					model.setLayerRate(p.value.extract_short());
				}else if("granularity".toUpperCase().equals(p.name.toUpperCase())){
					//granularity;
					model.setGranularity(p.value.extract_string());
				}else if("pmParameterName".toUpperCase().equals(p.name.toUpperCase())){
					//pmParameterName;
					model.setPmParameterName(p.value.extract_string());
				}else if("pmLocation".toUpperCase().equals(p.name.toUpperCase())){
					//pmLocation;
					model.setPmLocation(p.value.extract_string());
				}else if("thresholdType".toUpperCase().equals(p.name.toUpperCase())){
					//thresholdType;
					model.setThresholdType(PMThresholdType_THelper.read(p.value.create_input_stream()).value());
				}else if("value".toUpperCase().equals(p.name.toUpperCase())){
					//value;
					model.setValue(p.value.extract_float());
				}else if("unit".toUpperCase().equals(p.name.toUpperCase())){
					//unit;
					model.setUnit(p.value.extract_string());
				}else if("additionalInfo".toUpperCase().equals(p.name.toUpperCase())){
					// AdditionalInfo
					model.setAdditionalInfo(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}
			}
			//位置标识
			model.setLocationFlag(getPmLocationFlag(model.getPmLocation(),DataCollectDefine.FACTORY_HW_FLAG));
			// 设置周期
			if (model.getGranularity().equals(
					DataCollectDefine.COMMON.GRANULARITY_15MIN_STRING)) {
				model.setGranularityFlag(DataCollectDefine.COMMON.GRANULARITY_15MIN_FLAG);
			} else if (model.getGranularity().equals(
					DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING)) {
				model.setGranularityFlag(DataCollectDefine.COMMON.GRANULARITY_24HOUR_FLAG);
			}
			// 设置targetType
			// 名称长度为3 可能为ptp,ftp,equipmentHolder
			if (model.getObjectName().length == 3) {
				// ptp类型
				if (model.getObjectName()[2].name.equals(DataCollectDefine.COMMON.PTP)
						|| model.getObjectName()[2].name
								.equals(DataCollectDefine.COMMON.FTP)) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG);
				}
			}
			// 名称长度为4 可能为equipment,ctp
			else if (model.getObjectName().length == 4) {
				// ctp类型
				if (model.getObjectName()[3].name.equals(DataCollectDefine.COMMON.CTP)) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_CTP_FLAG);
					// model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG);
				}
				// equipment类型
				else if (model.getObjectName()[3].name
						.equals(DataCollectDefine.COMMON.EQUIPMENT)) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_EQUIPMENT_FLAG);
				}
			}
			
			//对象名字符串
			model.setObjectNameFullString(nameUtil.getNeSerialNo(model
					.getObjectName())
					+ ":"
					+ nameUtil.decompositionName(model.getObjectName()));
			
			//测试
//			model.setPerceivedSeverity(5);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * 将保护倒换数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public ProtectionSwtichDataModel ProtectionSwitchDataToModel(Object event) {

		StructuredEvent data = (StructuredEvent)event;
		
		ProtectionSwtichDataModel model = new ProtectionSwtichDataModel();
		
		try {
			for(Property p:data.filterable_data){
				//notificationId
				if("notificationId".toUpperCase().equals(p.name.toUpperCase())){
					model.setNotificationId(p.value.extract_string());
				}else if("neTime".toUpperCase().equals(p.name.toUpperCase())){
					// NeTime
					model.setNeTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("emsTime".toUpperCase().equals(p.name.toUpperCase())){
					// EmsTime
					model.setEmsTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("ProtectionType".toUpperCase().equals(p.name.toUpperCase())){
					model.setProtectType(ProtectionType_THelper.read(p.value.create_input_stream()).value());
				}else if("protectionGroupType".toUpperCase().equals(p.name.toUpperCase())){
					//wdm有此属性
					String protectionType = p.value.extract_string();
					//保存原始值
					model.setProtectTypeOri(protectionType);
					/*0. 1+1 MSP
					1. 1:N MSP
					2. 2F BLSR
					3. 4F BLSR
					4. 1+1 ATM
					5. 1:N ATM*/
					//目前只获取到1P1数据，其余未知 用-1填充，后期完善
					if("1P1".equals(protectionType.toUpperCase())){
						model.setProtectType(0);
					}else{
						model.setProtectType(-1);
					}
				}else if("switchReason".toUpperCase().equals(p.name.toUpperCase())){
					//switchReason
					model.setSwtichReason(SwitchReason_THelper.read(p.value.create_input_stream()).value());
				}else if("layerRate".toUpperCase().equals(p.name.toUpperCase())){
					// LayerRate
					try{
						if(p.value.type().content_type().kind().value() == TCKind._tk_long){
							//U2000 为long类型
							model.setLayerRate(p.value.extract_long());
						}else{
							//T2000 为short类型
							model.setLayerRate(p.value.extract_short());
						}
					}catch(Exception e){
						
					}
				}else if("groupName".toUpperCase().equals(p.name.toUpperCase())){
					//groupName
					try{
						if(p.value.type().content_type().kind().value() == TCKind._tk_string){
							//T2000 为String类型
							model.setGroupName(p.value.extract_string());
						}else{
							NameAndStringValue_T[] groupName = NamingAttributes_THelper.read(p.value.create_input_stream());
							//U2000 为NameAndStringValue_T类型
							model.setGroupName(nameUtil.decompositionName(groupName));
						}
					}catch(Exception e){
						
					}
				}else if("protectedTP".toUpperCase().equals(p.name.toUpperCase())){
					// protectedTP
					model.setProtectedTP(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}else if("switchAwayFromTP".toUpperCase().equals(p.name.toUpperCase())){
					// switchAwayFromTP
					model.setSwitchAwayFromTP(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}else if("switchToTP".toUpperCase().equals(p.name.toUpperCase())){
					// switchToTP
					model.setSwitchToTP(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}else if("nativeEMSName".toUpperCase().equals(p.name.toUpperCase())){
					// NativeEMSName
					String nativeEmsName = p.value.extract_string();
					model.setNativeEMSName(NameAndStringValueUtil.Stringformat(nativeEmsName,encode));
				}else if("additionalInfo".toUpperCase().equals(p.name.toUpperCase())){
					// AdditionalInfo
					model.setAdditionalInfo(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}
			}
			// 设置targetType
			// 名称长度为3 可能为ptp,ftp,equipmentHolder
			if (model.getProtectedTP().length == 3) {
				// ptp类型
				if (model.getProtectedTP()[2].name.equals(DataCollectDefine.COMMON.PTP)
						|| model.getProtectedTP()[2].name
								.equals(DataCollectDefine.COMMON.FTP)) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG);
				}
			}
			// 名称长度为4 可能为equipment,ctp
			else if (model.getProtectedTP().length == 4) {
				// ctp类型
				if (model.getProtectedTP()[3].name.equals(DataCollectDefine.COMMON.CTP)) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_CTP_FLAG);
					// model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG);
				}
				// equipment类型
				else if (model.getProtectedTP()[3].name
						.equals(DataCollectDefine.COMMON.EQUIPMENT)) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_EQUIPMENT_FLAG);
				}
			}
			model.setNeSerialNo(nameUtil.getNeSerialNo(model.getProtectedTP()));
//			//统一设置
//			model.setProtectCategory(head);
			
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	//TCA数据转换 中兴专用 当告警处理
	public AlarmDataModel TCADataToAlarmModel(Object event) {

		AlarmDataModel model = new AlarmDataModel();
		
		return model;
	}
}
