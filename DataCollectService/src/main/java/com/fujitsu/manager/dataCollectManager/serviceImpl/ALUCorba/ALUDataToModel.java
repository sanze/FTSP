package com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba;

import globaldefs.NVSList_THelper;
import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributes_THelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omg.CORBA.TCKind;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.StructuredEvent;

import ALU.emsMgr.EMS_T;
import ALU.equipment.EquipmentHolder_T;
import ALU.equipment.EquipmentOrHolder_T;
import ALU.equipment.Equipment_T;
import ALU.flowDomain.FlowDomain_T;
import ALU.flowDomainFragment.FlowDomainFragment_T;
import ALU.managedElement.CommunicationState_T;
import ALU.managedElement.CommunicationState_THelper;
import ALU.managedElement.ManagedElement_T;
import ALU.notifications.AcknowledgeIndication_T;
import ALU.notifications.AcknowledgeIndication_THelper;
import ALU.notifications.NVList_THelper;
import ALU.notifications.NameAndAnyValue_T;
import ALU.notifications.ObjectType_T;
import ALU.notifications.ObjectType_THelper;
import ALU.notifications.PerceivedSeverity_T;
import ALU.notifications.PerceivedSeverity_THelper;
import ALU.notifications.ProposedRepairActionList_THelper;
import ALU.notifications.ServiceAffecting_T;
import ALU.notifications.ServiceAffecting_THelper;
import ALU.notifications.SpecificProblemList_THelper;
import ALU.performance.PMData_T;
import ALU.performance.PMMeasurement_T;
import ALU.performance.PMThresholdType_THelper;
import ALU.protection.EProtectionGroup_T;
import ALU.protection.ProtectionGroup_T;
import ALU.protection.ProtectionType_THelper;
import ALU.protection.SwitchReason_THelper;
import ALU.subnetworkConnection.CrossConnect_T;
import ALU.subnetworkConnection.SubnetworkConnection_T;
import ALU.subnetworkConnection.TPData_T;
import ALU.terminationPoint.TerminationPoint_T;
import ALU.topologicalLink.TopologicalLink_T;
import ALU.transmissionParameters.LayeredParameters_T;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EmsDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentOrHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FdfrModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FlowDomainModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.LayeredParametersModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionSwtichDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.SubnetworkConnectionModel;
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
public class ALUDataToModel extends DataToModel{

	public ALUDataToModel(String encode) {
		super(encode);
	}
	public static void main(String[] args){
		System.out.println(toCommonHolderName("rack=1/shelf=1/slot=16/sub-slot=10"));
		System.out.println(toCommonHolderName("1-1-1-1"));
		System.out.println(toCommonPtpName("r1sr1sl19/ETHLocPort#1"));
		System.out.println(toCommonPtpName("r1sr1sl19/ETHLocPort#1#1"));
		System.out.println(toCommonPtpName("HY-GuoTuZiYuanJu-B004/r01s1b01p01"));
		System.out.println(toCommonPtpName("HY-GuoTuZiYuanJu-B004/r01s1b01p01c01"));
		System.out.println(toCommonPtpName("HY-GuoTuZiYuanJu-B004/r01s1b01p01-GAU"));
	}

	/**归一化名称
	 */
	private static String toCommonHolderName(String nameString){
		List<String> tmpList=NameAndStringValueUtil.match(nameString,DataCollectDefine.ALU.HOLDER_REGEX);
		if(tmpList!=null&&!tmpList.isEmpty()){
//			nameString="";
//		}else{
			nameString=tmpList.get(0);
			nameString=DataCollectDefine.COMMON.RACK+"="+nameString;
			nameString=nameString.replaceFirst(
				"-", "/"+DataCollectDefine.COMMON.SHELF+"=");
			nameString=nameString.replaceFirst(
				"-", "/"+DataCollectDefine.COMMON.SLOT+"=");
			nameString=nameString.replaceFirst(
				"-", "/"+DataCollectDefine.COMMON.SUB_SLOT+"=");
		}
		nameString=nameString.replaceFirst(DataCollectDefine.ALU.SUB_SLOT, DataCollectDefine.COMMON.SUB_SLOT);
		return nameString;
	}
	/**归一化名称
	 */
	public static String toCommonPtpName(String nameString){
		List<String> tmpList=NameAndStringValueUtil.match(nameString,DataCollectDefine.ALU.PTP_REGEX);
		if(tmpList!=null&&!tmpList.isEmpty()){
//			nameString="";
//		}else{
			nameString=tmpList.get(0);
			nameString=nameString.replaceFirst(
					DataCollectDefine.ALU.PTP_PORT+"0*", "/"+DataCollectDefine.COMMON.PORT+"=");
			nameString=nameString.replaceFirst(
				DataCollectDefine.ALU.PTP_RACK+"0*", DataCollectDefine.COMMON.RACK+"=");
			nameString=nameString.replaceFirst(
				DataCollectDefine.ALU.PTP_SHELF+"0*", "/"+DataCollectDefine.COMMON.SHELF+"=");
			nameString=nameString.replaceFirst(
				DataCollectDefine.ALU.PTP_SLOT+"0*", "/"+DataCollectDefine.COMMON.SLOT+"=");
			
			tmpList=NameAndStringValueUtil.match(nameString,DataCollectDefine.ALU.SUB_PTP_REGEX);
			if(tmpList!=null&&!tmpList.isEmpty()){
				tmpList=NameAndStringValueUtil.match(tmpList.get(0),"#[0-9]+");
				String subSlotNo=tmpList.get(0).replaceFirst("#", "");
				nameString=nameString.replaceFirst("/"+DataCollectDefine.COMMON.PORT+"=", 
					"/"+DataCollectDefine.COMMON.SUB_SLOT+"="+subSlotNo+"/"+DataCollectDefine.COMMON.PORT+"=");
			}
		}
		return nameString;
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
		//NIVersion unsupport ALU
		/*for(NameAndStringValue_T info:data.additionalInfo){
			if("NIVersion".equals(info.name)){
				model.setInterfaceVersion(info.value);
				break;
			}
		}*/

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
		/**
		 *	additionalInfo[0]:	
				name is:manufacturer
				value is:ALU
			additionalInfo[1]:	
				name is:serverName
				value is:zwsdh

		 */

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
		if(DataCollectDefine.ALU.HolderType.SUB_SLOT.equals(data.holderType)){
			model.setHolderType(DataCollectDefine.COMMON.SUB_SLOT);
		}else{
			model.setHolderType(data.holderType);
		}
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setUserLabel(data.userLabel);
		
		// model.setHardwareVersion(hardwareVersion);
		// model.setLocation(location);
		// model.setVendorName(vendorName)

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));

		String nameString=toCommonHolderName(model.getName()[2].value);
				
		String rackNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SLOT);
		String subSlotNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SUB_SLOT);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setSubSlotNo(subSlotNo);
		
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
		
		String nameString=toCommonHolderName(model.getName()[2].value);

		String rackNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SLOT);
		String subSlotNo = nameUtil.getEquipmentNoFromTargetName(
				nameString, DataCollectDefine.COMMON.SUB_SLOT);
		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setSubSlotNo(subSlotNo);

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
		
		String nameString=toCommonPtpName(model.getName()[2].value);
		
		String rackNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SLOT);
		String subSlotNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.SUB_SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(nameString,
				DataCollectDefine.COMMON.PORT);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setSubSlotNo(subSlotNo);
		model.setPortNo(portNo);
		
		//extend
		for(NameAndStringValue_T name:data.additionalInfo){
			if(name.name.equals("InLabel")){
				model.setSrcInLabel(name.value);
			}else if(name.name.equals("OutLabel")){
				model.setSrcOutLabel(name.value);
			}
		}
		
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
			// BD corba数据入库整理文档 -- domain规律
			//获取xml文件中配置内容
			if(ptpDomainList == null){
				ptpDomainList = XmlUtil.parserXmlForPtpDomain("ALU_PTP_DOMAIN.xml"); 
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
	/**FIXME 贝尔保护归一化待入库解析
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
	/**FIXME 贝尔保护归一化待入库解析
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
		
		//extend
		if(data.additionalInfo!=null&&data.additionalInfo.length>0){
			for(NameAndStringValue_T name:data.additionalInfo){
				if(name.name.equals("underlyingType")){
					model.setUnderlyingType(name.value);
				}else if(name.name.equals("protocolType")){
					model.setProtocolType(name.value);
				}else if(name.name.equals("serverObj")){
					model.setServerObj(name.value);
				}
			}
		}

		return model;
	}
	/**FIXME 贝尔交叉归一化待入库解析
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
			if(name.name.equals("AEndClientType")){
				model.setClientType(name.value);
			}else if(name.name.equals("ZEndClientType")){
				model.setClientType(name.value);
			}
			else if(name.name.toLowerCase().equals("layerrate")){
				model.setLayerRate(name.value);
			}
		}
		return model;
	}
	
	/**
	 * 将交叉连接数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public SubnetworkConnectionModel SNCDataToModel(SubnetworkConnection_T data) {

		SubnetworkConnectionModel model = new SubnetworkConnectionModel();
		
		model.setSncSerialNo(data.name[2].value);
		
		model.setName(data.name);
		model.setUserLabel(NameAndStringValueUtil.Stringformat(data.userLabel, encode));
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName, encode));
		model.setOwner(data.owner);
		model.setSncState(data.sncState.value());
		model.setDirection(data.direction.value());
		model.setRate(data.rate);
		model.setStaticProtectionLevel(data.staticProtectionLevel.value());
		model.setSncType(data.sncType.value());
		model.setaEndTP(data.aEnd[0].tpName);
		model.setzEndTP(data.zEnd[0].tpName);
		model.setAdditionalInfo(data.additionalInfo);
		//extend
		if(data.additionalInfo!=null&&data.additionalInfo.length>0){
			for(NameAndStringValue_T name:data.additionalInfo){
				if(name.name.equals("LSPType")){
					model.setLSPType(name.value);
				}else if(name.name.equals("CreateTime")){
					model.setCreateTime(name.value);
				}else if(name.name.equals("ServiceState")){
					model.setServiceState(name.value);
				}else if(name.name.equals("serverID")){
					model.setBelong_snc(name.value);
				}
			}
		}
		return model;
	}

	/**
	 * 将FlowDomain数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public FlowDomainModel FlowDomainDataToModel(FlowDomain_T data) {

		FlowDomainModel model = new FlowDomainModel();
		
		model.setName(data.name);
		model.setUserLabel(NameAndStringValueUtil.Stringformat(data.userLabel, encode));
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName, encode));
		model.setOwner(data.owner);
		model.setNetworkAccessDomain(data.networkAccessDomain);
		model.setfDConnectivityState(data.fDConnectivityState.value());
		model.setFdType(data.fdType);
		model.setAdditionalInfo(data.additionalInfo);
		//extend
		if(data.additionalInfo!=null&&data.additionalInfo.length>0){
			for(NameAndStringValue_T name:data.additionalInfo){
				if(name.name.equals("domainType")){
					model.setDomainType(name.value);
				}else if(name.name.equals("bridgeType")){
					model.setBridgeType(name.value);
				}else if(name.name.equals("stpType")){
					model.setStpType(name.value);
				}
			}
		}
		return model;
	}
	
	/**
	 * 将Fdfr数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public FdfrModel FdfrDataToModel(FlowDomainFragment_T data) {

		FdfrModel model = new FdfrModel();

		model.setName(data.name);
		model.setUserLabel(NameAndStringValueUtil.Stringformat(data.userLabel, encode));
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName, encode));
		model.setOwner(data.owner);
		model.setDirection(data.direction.value());
		model.setLayer(data.transmissionParams.layer);
		
		model.setNetworkAccessDomain(data.networkAccessDomain);
		model.setFlexible(data.flexible?DataCollectDefine.TRUE:DataCollectDefine.FALSE);
		model.setAdministrativeState(data.administrativeState.value());
		model.setFdfrState(data.fdfrState.value());
		model.setFdfrType(data.fdfrType);
		model.setAdditionalInfo(data.additionalInfo);
		//extend
		if(data.additionalInfo!=null&&data.additionalInfo.length>0){
			for(NameAndStringValue_T name:data.additionalInfo){
				if(name.name.equals("Comments")){
					model.setComments(name.value);
				}else if(name.name.equals("Customer")){
					model.setCustomer(name.value);
				}else if(name.name.equals("igmpSnoopingState")){
					model.setIgmpSnoopingState(name.value);
				}else if(name.name.equals("serviceType")){
					model.setServiceType(name.value);
				}else if(name.name.equals("serviceState")){
					model.setServiceState(name.value);
				}else if(name.name.equals("trafficType")){
					model.setTrafficType(name.value);
				}else if(name.name.equals("oamEnabled")){
					model.setOamEnabled(name.value);
				}
			}
		}
		List<TerminationPointModel> aEnd=new ArrayList<TerminationPointModel>();
		for(TPData_T tpData:data.aEnd){
			TerminationPointModel tp = new TerminationPointModel();
			tp.setName(tpData.tpName);
			List<LayeredParametersModel> layeredParametersModelList = new ArrayList<LayeredParametersModel>();
			List<Short> layerRateList = new ArrayList<Short>();
			for (LayeredParameters_T layeredParameters : tpData.transmissionParams) {
				layerRateList.add(layeredParameters.layer);
				LayeredParametersModel layeredParametersModel = new LayeredParametersModel();
				layeredParametersModel = LayeredParametersDataToModel(layeredParameters);
				layeredParametersModelList.add(layeredParametersModel);
			}
			tp.setTransmissionParams(layeredParametersModelList);
			aEnd.add(tp);
		}
		List<TerminationPointModel> zEnd=new ArrayList<TerminationPointModel>();
		for(TPData_T tpData:data.zEnd){
			TerminationPointModel tp = new TerminationPointModel();
			tp.setName(tpData.tpName);
			List<LayeredParametersModel> layeredParametersModelList = new ArrayList<LayeredParametersModel>();
			List<Short> layerRateList = new ArrayList<Short>();
			for (LayeredParameters_T layeredParameters : tpData.transmissionParams) {
				layerRateList.add(layeredParameters.layer);
				LayeredParametersModel layeredParametersModel = new LayeredParametersModel();
				layeredParametersModel = LayeredParametersDataToModel(layeredParameters);
				layeredParametersModelList.add(layeredParametersModel);
			}
			tp.setTransmissionParams(layeredParametersModelList);
			zEnd.add(tp);
		}
		model.setaEnd(aEnd);
		model.setzEnd(zEnd);
		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));

		return model;
	}
	
	/**贝尔性能归一化待入库解析
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
		model.setLocationFlag(getPmLocationFlag(data.pmLocation,DataCollectDefine.FACTORY_ALU_FLAG));

		return model;
	}
	/**FIXME 贝尔性能归一化待入库解析
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
		model.setRetrievalTimeDisplay(DateStrFormatForPM(data.retrievalTime, DataCollectDefine.FACTORY_ALU_FLAG));

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

		boolean isClearable = false;
		short layerRate = 0;
		String probableCause = null;
		// 是否对业务造成影响
		ServiceAffecting_T serviceAffecting = ServiceAffecting_T.SA_UNKNOWN;
		// 告警级别
		PerceivedSeverity_T perceivedSeverity = null;
		String[] SpecificProblems = null;
		String[] ProposedRepairActions = null;

		// 事件类型
		String EventType = null;
		AcknowledgeIndication_T acknowledgeIndication = null;
		NameAndStringValue_T[] additionalInfo = null;

		boolean rcaiIndicator = false;
		String currentAlarmId = null;
		String AlarmType = null;
		String serverName = null;
		for(Property property:data.filterable_data){
			if(property.name.equals("notificationId")){
				notificationId = property.value.extract_string();
			}else if(property.name.equals("objectName")){
				objectName = NamingAttributes_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("objectType")){
				objectType = ObjectType_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("emsTime")){
				//20140725140203.0+0800
				emsTime = property.value.extract_string();
			}else if(property.name.equals("rcaiIndicator")){
				rcaiIndicator = property.value.extract_boolean();
			}else if(property.name.equals("currentAlarmId")){
				currentAlarmId = property.value.extract_string();
			}else if(property.name.equals("X.733::EventType")){
				//equipmentAlarm
				EventType = property.value.extract_string();
			}else if(property.name.equals("AlarmType")){
				//equipmentAlarm
				AlarmType = property.value.extract_string();
			}else if(property.name.equals("neTime")){
				//20140725140203.0+0800
				neTime = property.value.extract_string();
			}else if(property.name.equals("perceivedSeverity")){
				perceivedSeverity = PerceivedSeverity_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("probableCauseQualifier")){
				probableCauseQualifier= property.value.extract_string();
			}else if(property.name.equals("nativeProbableCause")){
				nativeProbableCause = property.value.extract_string();
			}else if(property.name.equals("probableCause")){
				probableCause = property.value.extract_string();
			}else if(property.name.equals("nativeEMSName")){
				nativeEMSName = property.value.extract_string();
			}else if(property.name.equals("acknowledgeIndication")){
				acknowledgeIndication = AcknowledgeIndication_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("isClearable")){
				isClearable = property.value.extract_boolean();
			}else if(property.name.equals("serverName")){
				serverName = property.value.extract_string();
			}else if(property.name.equals("layerRate")){
				layerRate = property.value.extract_short();
			}else if(property.name.equals("X.733::AdditionalInfo")){
				additionalInfo = NVSList_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("X.733::SpecificProblems")){
				SpecificProblems = SpecificProblemList_THelper.read(
						property.value.create_input_stream());
			}else if(property.name.equals("serviceAffact")||//贝尔拼写错误，仅推送告警有此字段
					property.name.equals("serviceAffect")||
					property.name.equals("serviceAffecting")){
				if(property.value.type().kind().value()==8){//boolean
					boolean value=property.value.extract_boolean();
					serviceAffecting=value?
						ServiceAffecting_T.SA_SERVICE_AFFECTING:
						ServiceAffecting_T.SA_NON_SERVICE_AFFECTING;
				}else{
				serviceAffecting = ServiceAffecting_THelper.read(
						property.value.create_input_stream());
				}
			}
			//以下数据可能没有
			else if(property.name.equals("X.733::ProposedRepairActions")){
				ProposedRepairActions = ProposedRepairActionList_THelper.read(
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
			//model.setClearTime(DateStrFormatForAlarm(neEndTime, DataCollectDefine.FACTORY_ALU_FLAG));
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
			model.setAlarmType(getAlarmType(AlarmType!=null?AlarmType:EventType,DataCollectDefine.FACTORY_ALU_FLAG));
			// alarmReason
			model.setAlarmReason((SpecificProblems != null &&SpecificProblems.length>0)?
					NameAndStringValueUtil.Stringformat(SpecificProblems[0], encode):nativeProbableCause);
			model.setAlarmSerialNo(currentAlarmId);
			
			String originalInfo=com.fujitsu.util.SerializerUtil.toJSON(event, encode, false);
			model.setOriginalInfo(originalInfo);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	/**FIXME 贝尔状态变更信息暂时不支持
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