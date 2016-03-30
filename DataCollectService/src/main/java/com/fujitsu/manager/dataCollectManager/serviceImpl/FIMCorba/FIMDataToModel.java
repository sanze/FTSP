package com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba;

import globaldefs.NVSList_THelper;
import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributes_THelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omg.CORBA.TCKind;

import FENGHUO.CosNotification.Property;
import FENGHUO.CosNotification.StructuredEvent;
import FENGHUO.emsMgr.EMS_T;
import FENGHUO.equipment.EquipmentHolder_T;
import FENGHUO.equipment.EquipmentOrHolder_T;
import FENGHUO.equipment.Equipment_T;
import FENGHUO.managedElement.CommunicationState_T;
import FENGHUO.managedElement.CommunicationState_THelper;
import FENGHUO.managedElement.ManagedElement_T;
import FENGHUO.notifications.AcknowledgeIndication_T;
import FENGHUO.notifications.AcknowledgeIndication_THelper;
import FENGHUO.notifications.NVList_THelper;
import FENGHUO.notifications.NameAndAnyValue_T;
import FENGHUO.notifications.ObjectType_T;
import FENGHUO.notifications.ObjectType_THelper;
import FENGHUO.notifications.PerceivedSeverity_T;
import FENGHUO.notifications.PerceivedSeverity_THelper;
import FENGHUO.notifications.ProposedRepairActionList_THelper;
import FENGHUO.notifications.ServiceAffecting_T;
import FENGHUO.notifications.ServiceAffecting_THelper;
import FENGHUO.notifications.SpecificProblemList_THelper;
import FENGHUO.performance.PMData_T;
import FENGHUO.performance.PMMeasurement_T;
import FENGHUO.protection.EProtectionGroup_T;
import FENGHUO.protection.ProtectionGroup_T;
import FENGHUO.subnetworkConnection.CrossConnect_T;
import FENGHUO.terminationPoint.TerminationPoint_T;
import FENGHUO.topologicalLink.TopologicalLink_T;
import FENGHUO.transmissionParameters.LayeredParameters_T;
import FENGHUO.performance.PMThresholdType_THelper;
import FENGHUO.protection.ProtectionType_THelper;
import FENGHUO.protection.SwitchReason_THelper;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EmsDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentOrHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.LayeredParametersModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionSwtichDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TCADataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.service.DataToModel;
import com.fujitsu.model.PtpDomainModel;
import com.fujitsu.util.NameAndStringValueUtil;
import com.fujitsu.util.XmlUtil;

/**
 * @author zhuangjieliang
 * 
 */
public class FIMDataToModel extends DataToModel{

	public FIMDataToModel(String encode) {
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
		//NIVersion
		for(NameAndStringValue_T info:data.additionalInfo){
			if("NIVersion".equals(info.name)){
				model.setInterfaceVersion(info.value);
				break;
			}
		}

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
		model.setLocation(NameAndStringValueUtil.Stringformat(data.location,encode));
		model.setName(data.name);
		model.setNativeEMSNameOri(data.nativeEMSName);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setProductName(data.productName);
		model.setSupportedRates(data.supportedRates);
		model.setUserLabel(data.userLabel);
		model.setVersion(NameAndStringValueUtil.Stringformat(data.version,encode));
		if(data.additionalInfo!=null){
			for(NameAndStringValue_T item:data.additionalInfo){
				if("NeType".equals(item.name)){
					model.setMeType(item.value);
				}else if("maxPerceivedSeverity".equals(item.name)){
					model.setAlarmStatus(DataCollectDefine.COMMON.PerceivedSeverity.toValue(item.value));
				}else if("ObjectIP".equals(item.name)){
					model.setNetAddress(item.value);
				}
			}
		}
		// model.setConnectionRates(connectionRates);
		// model.setDescriptionInfo(descriptionInfo);
		// model.setHardwareVersion(hardwareVersion);
		// model.setOperationalStatus(operationalStatus);
		// model.setSoftwareVersion(softwareVersion);
		// model.setVendorName(vendorName);

		// extend
		model.setNeSerialNo(nameUtil.getNeSerialNo(model.getName()));

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
		
		for(NameAndStringValue_T item:data.additionalInfo){
			if("installedSerialNumber".equals(item.name))
				model.setSerialNo(item.value);
		}
		// model.setHardwareVersion(hardwareVersion);
		// model.setLocation(location);
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
		model.setExpectedBoardType(data.expectedEquipmentObjectType);
		model.setInstalledEquipmentObjectType(data.installedEquipmentObjectType);
		model.setInstalledBoardType(data.installedEquipmentObjectType);
		model.setInstalledPartNumber(data.installedPartNumber);
		model.setInstalledSerialNumber(data.installedSerialNumber);
		model.setInstalledVersion(data.installedVersion);
		model.setSoftwareVersion(data.installedVersion);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setServiceState(data.serviceState.value());
		model.setUserLabel(data.userLabel);

		// model.setHardwareVersion(hardwareVersion);
		// model.setHasProtection(hasProtection);
		// 

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
	 * 将LayeredParameters数据转化成统一的数据模型
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

		model.setLayerRateString(
				getLayRateSrting(model.getTransmissionParams()));
		
		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.PORT);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setPortNo(portNo);
		
		//
		int ptpOrFtp = DataCollectDefine.COMMON.PTP_FLAG;
		if (model.getName()[2].name.equals(DataCollectDefine.COMMON.FTP)) {
			ptpOrFtp = DataCollectDefine.COMMON.FTP_FLAG;
		}
		model.setPtpOrFtp(ptpOrFtp);
		
		Integer domain = DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG;
		String ptpType = null;
		String rate = null;
		if(DataCollectDefine.COMMON.PTP_FLAG == ptpOrFtp){
			//BD corba数据入库整理文档 -- domain规律
			//获取xml文件中配置内容
			if(ptpDomainList == null){
				ptpDomainList = XmlUtil.parserXmlForPtpDomain("FIM_PTP_DOMAIN.xml"); 
			}
			//循环配置项 根据layer匹配填充domain、ptpType、rate
			for(PtpDomainModel temp:ptpDomainList){
				//配置项中的层速率是否在ptp层速率列表中
				if(layerRateList.containsAll(temp.getLayerList())){
					domain=temp.getDomainFlag();
					ptpType = temp.getPtpType();
					rate = temp.getRate();
					break;
				}
			}
		}
		if(domain != null){
			model.setDomain(domain);
		}
		
		//设置ptpType和rate
		if(ptpType!=null){
			model.setPtpType(ptpType.toUpperCase());
		}else{
			model.setPtpType(ptpType);
		}
		model.setRate(rate);
		
		//for ctp
		if(model.getName().length == 4&&DataCollectDefine.COMMON.CTP.equals(model.getName()[3].name)){
			
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
			else if(name.name.equals("SPRINGProtocol")){
				model.setSpringProtocol(name.value);
			}
			else if(name.name.equals("SwitchPosition")){
				model.setSwitchPosition(name.value);
			}
		}
		
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
		
		for(NameAndStringValue_T name:data.ePgpParameters){
			if(name.name.equals("wtrTime")){
				model.setWtrTime(name.value);
			}
		}
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
		for(NameAndStringValue_T name:data.additionalInfo){
			if(name.value.equals("AEndClientType")){
				model.setClientType(name.value);
			}/*else if(name.value.equals("ZEndClientType")){
				model.setClientType(name.value);
			}*/
			else if(name.value.equals("LayerRate")){
				model.setClientRate(name.value);
			}
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
		model.setUnit(NameAndStringValueUtil.Stringformat(data.unit,encode));
		model.setValue(String.valueOf(data.value));
		model.setLocationFlag(getPmLocationFlag(data.pmLocation,DataCollectDefine.FACTORY_FIBERHOME_FLAG));

		return model;
	}
	/**
	 * 将性能数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public PmDataModel PMDataToModel(PMData_T data) throws CommonException{

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
		model.setRetrievalTimeDisplay(DateStrFormatForPM(data.retrievalTime, DataCollectDefine.FACTORY_FIBERHOME_FLAG));

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
		String notificationId = null;
		// 目标名称
		NameAndStringValue_T[] objectName = null;
		String nativeEMSName = null;
		String nativeProbableCause = null;
		String probableCauseQualifier= null;
		// 目标类型
		ObjectType_T objectType = null;
		String emsTime = null;
		String neTime = null;
		String emsEndTime = null;
		String neEndTime = null;
		boolean isClearable = false;
		short layerRate = 0;
		String probableCause = null;
		// 是否对业务造成影响
		ServiceAffecting_T serviceAffecting = null;
		// 告警级别
		PerceivedSeverity_T perceivedSeverity = null;
		String[] SpecificProblems = null;
		String additionalText = null;
		// 事件类型
		String EventType = null;
		AcknowledgeIndication_T acknowledgeIndication = null;
		NameAndStringValue_T[] additionalInfo = null;
		String[] ProposedRepairActions = null;
		for(Property property:data.filterable_data){
			if(property.name.equals("notificationId")){
				notificationId = property.value.extract_string();
			}else if(property.name.equals("objectName")){
				objectName = NamingAttributes_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("nativeEMSName")){
				nativeEMSName = property.value.extract_string();
			}else if(property.name.equals("nativeProbableCause")){
				nativeProbableCause = property.value.extract_string();
				//数据样例 RS_TIM;100
				if(nativeProbableCause.contains(";")){
					nativeProbableCause = nativeProbableCause.split(";")[0];
				}
			}else if(property.name.equals("probableCauseQualifier")){
				probableCauseQualifier= property.value.extract_string();
			}else if(property.name.equals("objectType")){
				objectType = ObjectType_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("emsTime")){
				emsTime = property.value.extract_string();
			}else if(property.name.equals("neTime")){
				neTime = property.value.extract_string();
			}else if(property.name.equals("emsEndTime")){
				emsEndTime = property.value.extract_string();
			}else if(property.name.equals("neEndTime")){
				neEndTime = property.value.extract_string();
			}else if(property.name.equals("isClearable")){
				isClearable = property.value.extract_boolean();
			}else if(property.name.equals("layerRate")){
				layerRate = property.value.extract_short();
			}else if(property.name.equals("probableCause")){
				probableCause = property.value.extract_string();
			}else if(property.name.equals("serviceAffecting")){
				serviceAffecting = ServiceAffecting_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("perceivedSeverity")){
				perceivedSeverity = PerceivedSeverity_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("X.733::SpecificProblems")){
				SpecificProblems = SpecificProblemList_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("X.733::ProposedRepairActions")){
				ProposedRepairActions = ProposedRepairActionList_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("additionalText")){
				additionalText = property.value.extract_string();
			}else if(property.name.equals("X.733::EventType")){
				EventType = property.value.extract_string();
			}else if(property.name.equals("acknowledgeIndication")){
				acknowledgeIndication = AcknowledgeIndication_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("additionalInfo")){
				additionalInfo = NVSList_THelper.read(
						property.value.create_input_stream());
			}else{
				System.out.println("StructuredEvent unknow Property:"+property.name);
			}
		}
		
		try {
			// NotificationId
			model.setNotificationId(notificationId);
			// confirmStatusOri
			model.setConfirmStatusOri(Integer.valueOf(acknowledgeIndication.value()).toString());
			// clearTime
			model.setClearTime(DateStrFormatForAlarm(neEndTime));
			// handlingSuggestion
			model.setHandlingSuggestion((ProposedRepairActions != null &&ProposedRepairActions.length>0)?
					NameAndStringValueUtil.Stringformat(ProposedRepairActions[0], encode):"");
			// ObjectName
			model.setObjectName(objectName);
			// NativeEMSName
			model.setNativeEmsName(NameAndStringValueUtil.Stringformat(nativeEMSName, encode));
			// NateiveProbableCause
			model.setNativeProbableCause(NameAndStringValueUtil.Stringformat(nativeProbableCause, encode));
			// ObjectType
			if(objectType==null){//无objectType，通过objectName解析
				model.setObjectType(getObjectTypeByName(model.getObjectName()));
			}else{
				model.setObjectType(objectType.value());
			}
			// EmsTime
			model.setEmsTime(DateStrFormatForAlarm(emsTime));
			// NeTime
			model.setNeTime(DateStrFormatForAlarm(neTime));
			// Clearable
			model.setClearable(isClearable);
			// LayerRate
			model.setLayerRate(layerRate);
			// ProbableCause
			model.setProbableCause(NameAndStringValueUtil.Stringformat(probableCause, encode));
			// ProbableCauseQualifier
			model.setProbableCauseQualifier(NameAndStringValueUtil.Stringformat(probableCauseQualifier, encode));
			// PerceivedSeverity
			model.setPerceivedSeverity(perceivedSeverity.value());
			// ServiceAffecting
			model.setServiceAffecting(serviceAffecting.value());
			// AdditionalInfo
			model.setAdditionalInfo(additionalInfo);
			//EventType -->alarmType
			model.setAlarmType(getAlarmType(EventType,DataCollectDefine.FACTORY_FIBERHOME_FLAG));
			// alarmReason
			model.setAlarmReason((SpecificProblems != null &&SpecificProblems.length>0)?
					NameAndStringValueUtil.Stringformat(SpecificProblems[0], encode):"");

			// AdditionalInfo相关内容解析
			for(NameAndStringValue_T tmp:additionalInfo) {
				// alarmSerialNo
				if (tmp.name.equals("AlarmMatchID")) {
					model.setAlarmSerialNo(tmp.value);
				}
			}
			
			String originalInfo=com.fujitsu.util.SerializerUtil.toJSON(event, encode, false);
			model.setOriginalInfo(originalInfo);
		} catch (CommonException e) {
			e.printStackTrace();
		}
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
						}else{
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


	@Override
	//FIXME 无真实数据 复制华为
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
			
		} catch (CommonException e) {
			e.printStackTrace();
		}
		
		return model;
	}

	@Override
	//FIXME 无真实数据 复制华为
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
							model.setLayerRate(p.value.extract_long());
						}else{
							model.setLayerRate(p.value.extract_short());
						}
					}catch(Exception e){
					}
				}else if("groupName".toUpperCase().equals(p.name.toUpperCase())){
					//groupName
					try{
						if(p.value.type().content_type().kind().value() == TCKind._tk_string){
							model.setGroupName(p.value.extract_string());
						}else{
							NameAndStringValue_T[] groupName = NamingAttributes_THelper.read(p.value.create_input_stream());
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