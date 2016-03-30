package com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesList_THelper;
import globaldefs.NamingAttributes_THelper;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.TCKind;

import ZTE_U31.CosNotification.Property;
import ZTE_U31.CosNotification.StructuredEvent;
import ZTE_U31.alarmMgr.AlarmType_T;
import ZTE_U31.alarmMgr.AlarmType_THelper;
import ZTE_U31.alarmMgr.PerceivedSeverity_T;
import ZTE_U31.alarmMgr.PerceivedSeverity_THelper;
import ZTE_U31.clocksource.ClockSource_T;
import ZTE_U31.emsMgr.EMS_T;
import ZTE_U31.equipment.EquipmentHolder_T;
import ZTE_U31.equipment.EquipmentOrHolder_T;
import ZTE_U31.equipment.Equipment_T;
import ZTE_U31.ethernet.VB_T;
import ZTE_U31.managedElement.CommunicationState_T;
import ZTE_U31.managedElement.CommunicationState_THelper;
import ZTE_U31.managedElement.ManagedElement_T;
import ZTE_U31.managedElement.NetAddress_T;
import ZTE_U31.mstpcommon.EthernetService_T;
import ZTE_U31.mstpcommon.VCGBinding_T;
import ZTE_U31.notifications.NVList_THelper;
import ZTE_U31.notifications.NameAndAnyValue_T;
import ZTE_U31.notifications.ObjectType_T;
import ZTE_U31.notifications.ObjectType_THelper;
import ZTE_U31.performance.PMData_T;
import ZTE_U31.performance.PMMeasurement_T;
import ZTE_U31.performance.PMMeasurement_THelper;
import ZTE_U31.performance.PMThresholdType_THelper;
import ZTE_U31.protection.EProtectionGroup_T;
import ZTE_U31.protection.ProtectionGroup_T;
import ZTE_U31.protection.ProtectionRelation_T;
import ZTE_U31.protection.ProtectionType_THelper;
import ZTE_U31.protection.ProtectionUnit_T;
import ZTE_U31.protection.SwitchReason_THelper;
import ZTE_U31.subnetworkConnection.CrossConnect_T;
import ZTE_U31.subnetworkConnection.SubnetworkConnection_T;
import ZTE_U31.subnetworkConnection.TPData_T;
import ZTE_U31.terminationPoint.TerminationPoint_T;
import ZTE_U31.topologicalLink.TopologicalLink_T;
import ZTE_U31.transmissionParameters.LayeredParameters_T;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ClockSourceStatusModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EmsDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentOrHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EthServiceModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.LayeredParametersModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.MSTPBindingPathModel;
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
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.service.DataToModel;
import com.fujitsu.model.PtpDomainModel;
import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.NameAndStringValueUtil;
import com.fujitsu.util.XmlUtil;

/**
 * @author xuxiaojun
 * 
 */
public class ZTEU31DataToModel extends DataToModel{
	
	private static List alarmTransferList = null;
	private static DataCollectMapper dataCollectMapper = null;
	private static Map<String,String> alarmCodeCache = new HashMap<String,String>();
	
	public static int ROUTE_TYPE_NORMAL = 0;
	public static int ROUTE_TYPE_SRC_OR_DEST = 1;
	public static int ROUTE_TYPE_PW = 2;
	
	public ZTEU31DataToModel(String encode) {
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
		// model.setOwner(data.owner);
		// model.setType(data.type);
		model.setUserLabel(data.userLabel);

		// extend
		model.setInternalEmsName(data.name[0].value);
		//InterfaceVersion
		for(NameAndStringValue_T info:data.additionalInfo){
			if("InterfaceVersion".equals(info.name)||
				"InterfaceVerion".equals(info.name)){//兼容E300的拼写错误
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
			Integer objectTypeValue=null;
			for(Property property:data.filterable_data){
				if(property.name.equals("notificationId")){
					model.setNotificationId(property.value.extract_string());
				}else if(property.name.equals("Confirm Status")){
					model.setConfirmStatusOri(property.value.extract_string());
				}else if(property.name.equals("Clear Status")){
					model.setClearStatus(property.value.extract_string());
				}else if(property.name.equals("clearTime")){
					model.setClearTime(DateStrFormatForAlarm(property.value.extract_string()));
				}else if(property.name.equals("EMSTime")){
					model.setEmsTime(DateStrFormatForAlarm(property.value.extract_string()));
				}else if(property.name.equals("raiseTime")){
					model.setNeTime(DateStrFormatForAlarm(property.value.extract_string()));
				}else if(property.name.equals("alarmType")){
					AlarmType_T alarmType_T = AlarmType_THelper.read(property.value.create_input_stream());
					model.setAlarmType(alarmType_T.value());
				}else if(property.name.equals("serviceAffecting")){
					model.setServiceAffecting(DataCollectDefine.ZTE.U31_ServiceAffect.toValue(property.value.extract_string()));
				}else if(property.name.equals("perceivedSeverity")){
					PerceivedSeverity_T perceivedSeverity = PerceivedSeverity_THelper.read(property.value.create_input_stream());
					model.setPerceivedSeverity(perceivedSeverity.value());
				}else if(property.name.equals("probableCause")){
					model.setProbableCause(NameAndStringValueUtil.Stringformat(property.value.extract_string(), encode));
				}else if(property.name.equals("layerRate")){
					model.setLayerRate(property.value.extract_short());
				}else if(property.name.equals("objectType")){
					ObjectType_T objectType = ObjectType_THelper.read(property.value.create_input_stream());
					model.setObjectType(objectType.value());
					objectTypeValue=objectType.value();
				}else if(property.name.equals("vendorProbableCause")){
					model.setNativeProbableCause(NameAndStringValueUtil.Stringformat(property.value.extract_string(), encode));
				}else if(property.name.equals("objectFilterName")){
					model.setNativeEmsName(NameAndStringValueUtil.Stringformat(property.value.extract_string(), encode));
				}else if(property.name.equals("objectName")){
					model.setObjectName(NamingAttributes_THelper.read(property.value.create_input_stream()));		
				}else if(property.name.equals("isClearable")){
					model.setClearable("true".equals(property.value.extract_string())?true:false);
				}else if(property.name.equals("Description")){
					model.setAlarmReason(NameAndStringValueUtil.Stringformat(property.value.extract_string(), encode));
				}else if(property.name.equals("additionalInfo")){
					NameAndStringValue_T[][] additionalInfo = NamingAttributesList_THelper.read(property.value.create_input_stream());
					model.setAdditionalInfo(additionalInfo[0]);
					// AdditionalInfo相关内容解析
					for(NameAndStringValue_T tmp:model.getAdditionalInfo()) {
						// confirmStatusOri
						// clearStatus
						if ("AlarmStatus".equals(tmp.name)) {
							model.setConfirmStatusOri(NameAndStringValueUtil.Stringformat(tmp.value, encode));
							model.setClearStatus(NameAndStringValueUtil.Stringformat(tmp.value, encode));
						}
						// emsTime
						else if ("EMSTime".equals(tmp.name)) {
							model.setEmsTime(DateStrFormatForAlarm(tmp.value));
						}		
						// serviceAffecting
						else if ("serviceAffecting".equals(tmp.name)) {
							model.setServiceAffecting(DataCollectDefine.ZTE.E300_ServiceAffect.toValue(tmp.value));
						}
						// nativeProbableCause
						else if ("VendorProbableCause".equals(tmp.name)) {
							model.setNativeProbableCause(NameAndStringValueUtil.Stringformat(tmp.value, encode));
						}
						// alarmSerialNo
						else if ("emsAlarmId".equals(tmp.name)) {
							model.setAlarmSerialNo(tmp.value);
						}
					}
				}else{
					//System.out.println("StructuredEvent unknow Property:"+property.name);
				}
			}
			if(objectTypeValue==null){//无objectType，通过objectName解析
				model.setObjectType(getObjectTypeByName(model.getObjectName()));
			}
			
			//判断probableCause是否为数字，U31有告警为数字，需要转换
			if(!model.getProbableCause().isEmpty()){
				try{
					//测试是否正常告警
					Integer.parseInt(model.getProbableCause());
					//初始化数据
					if(dataCollectMapper == null){
						dataCollectMapper = (DataCollectMapper) BeanUtil
								.getBean("dataCollectMapper");
					}
					if(alarmTransferList == null){
						alarmTransferList = dataCollectMapper.selectTable("T_ALARM_TRANSFER");
					}
					String alarmCode = model.getProbableCause();
					
					//缓存中查找存不存在
					if(alarmCodeCache.containsKey(alarmCode)){
						//PROBABLE_CAUSE 赋值
						model.setProbableCause(alarmCodeCache.get(alarmCode).toString().split("::")[0]);
						//NATIVE_PROBABLE_CAUSE 赋值
						if(model.getNativeProbableCause().isEmpty()){
							model.setNativeProbableCause(alarmCodeCache.get(alarmCode).toString().split("::")[1]);
						}
					}else{
						Map temp = null;
						for(Object alarmTransfer:alarmTransferList){
							temp = (Map)alarmTransfer;
							if(temp.get("ALARM_CODE").toString().equals(alarmCode)){
								//PROBABLE_CAUSE 赋值
								model.setProbableCause(temp.get("PROBABLE_CAUSE").toString());
								//NATIVE_PROBABLE_CAUSE 赋值
								if(model.getNativeProbableCause().isEmpty()){
									model.setNativeProbableCause(temp.get("NATIVE_PROBABLE_CAUSE").toString());
								}
								//加入缓存
								alarmCodeCache.put(alarmCode, temp.get("PROBABLE_CAUSE").toString()+"::"+temp.get("NATIVE_PROBABLE_CAUSE").toString());
								//跳出循环
								break;
							}
						}
					}
					
					
				}catch(Exception e){
					//正常告警不做处理
				}
				
			}
			
			
/*			String name = data.filterable_data[DataCollectDefine.ZTE.E300_OBJECT_NAME].name;
			if ("objectName".equals(name)) { // E300
				// clearTime-6
				model.setClearTime(DateStrFormatForAlarm(data
						.filterable_data[DataCollectDefine.ZTE.E300_CLEAR_TIME].value.extract_string(),
						DataCollectDefine.FACTORY_ZTE_FLAG));
				// neTime-5
				model.setNeTime(DateStrFormatForAlarm(data
						.filterable_data[DataCollectDefine.ZTE.E300_RAISE_TIME].value.extract_string(),
						DataCollectDefine.FACTORY_ZTE_FLAG));
				// alarmType-3
				AlarmType_T alarmType_T = AlarmType_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.E300_ALARM_TYPE].value.create_input_stream());
				model.setAlarmType(alarmType_T.value());
				// perceivedSeverity-4
				PerceivedSeverity_T perceivedSeverity = PerceivedSeverity_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.E300_PERCEIVED_SEVERITY].value.create_input_stream());
				model.setPerceivedSeverity(perceivedSeverity.value());
				// probableCause-2
				model.setProbableCause(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.E300_PROBABLE_CAUSE].value.extract_string(),	encode));
				// layerRate-8
				model.setLayerRate(data
						.filterable_data[DataCollectDefine.ZTE.E300_LAYER_RATE].value.extract_short());
				// objectType-9  E300告警长度可能只有8 现场测试获得数据
				if(data.filterable_data.length == DataCollectDefine.ZTE.E300_OBJECT_TYPE+1){
				model.setObjectType(data
						.filterable_data[DataCollectDefine.ZTE.E300_OBJECT_TYPE].value.extract_long());
				}
				// nativeEmsName-10
				if(data.filterable_data.length == DataCollectDefine.ZTE.E300_OBJECT_FILTER_NAME+1){
				model.setNativeEmsName(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.E300_OBJECT_FILTER_NAME].value.extract_string(), encode));
				}
				// objectName-0
				model.setObjectName(NamingAttributes_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.E300_OBJECT_NAME].value.create_input_stream()));
				// additionalInfo-7
				NameAndStringValue_T[][] additionalInfo = NamingAttributesList_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.E300_ADDITIONAL_INFO].value.create_input_stream());
				model.setAdditionalInfo(additionalInfo[0]);
				
				// AdditionalInfo相关内容解析
				for(NameAndStringValue_T tmp:model.getAdditionalInfo()) {
					// confirmStatusOri
					// clearStatus
					if ("AlarmStatus".equals(tmp.name)) {
						model.setConfirmStatusOri(NameAndStringValueUtil.Stringformat(tmp.value, encode));
						model.setClearStatus(NameAndStringValueUtil.Stringformat(tmp.value, encode));
					}
					// emsTime
					if ("EMSTime".equals(tmp.name)) {
						model.setEmsTime(DateStrFormatForAlarm(tmp.value,
								DataCollectDefine.FACTORY_ZTE_FLAG));
					}		
					// serviceAffecting
					if ("serviceAffecting".equals(tmp.name)) {
						model.setServiceAffecting(DataCollectDefine.ZTE.E300_ServiceAffect.toValue(tmp.value));
					}
					// nativeProbableCause
					if ("VendorProbableCause".equals(tmp.name)) {
						model.setNativeProbableCause(NameAndStringValueUtil.Stringformat(tmp.value, encode));
					}
					// alarmSerialNo
					if ("emsAlarmId".equals(tmp.name)) {
						model.setAlarmSerialNo(tmp.value);
					}
				}
			}
			else { // U31
				//notificationId
				model.setNotificationId(data
						.filterable_data[DataCollectDefine.ZTE.U31_NOTIFICATION_ID].value.extract_string());
				//confirmStatusOri
				model.setConfirmStatusOri(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.U31_CONFIRM_STATUS].value.extract_string(), encode));
				//clearStatus
				model.setClearStatus(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.U31_CLEAR_STATUS].value.extract_string(), encode));
				//clearTime
				model.setClearTime(DateStrFormatForAlarm(data
						.filterable_data[DataCollectDefine.ZTE.U31_CLEAR_TIME].value.extract_string(),
						DataCollectDefine.FACTORY_ZTE_FLAG));
				//emsTime
				model.setEmsTime(DateStrFormatForAlarm(data
						.filterable_data[DataCollectDefine.ZTE.U31_EMS_TIME].value.extract_string(),
						DataCollectDefine.FACTORY_ZTE_FLAG));
				//neTime
				model.setNeTime(DateStrFormatForAlarm(data
						.filterable_data[DataCollectDefine.ZTE.U31_RAISE_TIME].value.extract_string(),
						DataCollectDefine.FACTORY_ZTE_FLAG));
				//alarmType
				AlarmType_T alarmType_T = AlarmType_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.U31_ALARM_TYPE].value.create_input_stream());
				model.setAlarmType(alarmType_T.value());
				//serviceAffecting
				model.setServiceAffecting(DataCollectDefine.ZTE.U31_ServiceAffect.toValue(data
						.filterable_data[DataCollectDefine.ZTE.U31_SERVICE_AFFECTING].value.extract_string()));
				//perceivedSeverity
				PerceivedSeverity_T perceivedSeverity = PerceivedSeverity_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.U31_PERCEIVED_SEVERITY].value.create_input_stream());
				model.setPerceivedSeverity(perceivedSeverity.value());
				//probableCause
				model.setProbableCause(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.U31_PROBABLE_CAUSE].value.extract_string(), encode));
				//layerRate
				model.setLayerRate(data
						.filterable_data[DataCollectDefine.ZTE.U31_LAYER_RATE].value.extract_short());
				//objectType
				ObjectType_T objectType = ObjectType_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.U31_OBJECT_TYPE].value.create_input_stream());
				model.setObjectType(objectType.value());
				//nativeProbableCause
				model.setNativeProbableCause(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.U31_VENDOR_PROBABLE_CAUSE].value.extract_string(),
						encode));
				//nativeEmsName
				model.setNativeEmsName(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.U31_OBJECT_FILTER_NAME].value.extract_string(), encode));
				//objectName
				model.setObjectName(NamingAttributes_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.U31_OBJECT_NAME].value.create_input_stream()));		
				//isClearable
				model.setClearable("true".equals(data.filterable_data[DataCollectDefine.ZTE.U31_IS_CLEARABLE]
						.value.extract_string())?true:false);
				//alarmReason
				model.setAlarmReason(NameAndStringValueUtil.Stringformat(data
						.filterable_data[DataCollectDefine.ZTE.U31_DESCRIPTION].value.extract_string(), encode));
				//additionalInfo
				NameAndStringValue_T[][] additionalInfo = NamingAttributesList_THelper.read(data
						.filterable_data[DataCollectDefine.ZTE.U31_ADDITIONAL_INFO].value.create_input_stream());
				model.setAdditionalInfo(additionalInfo[0]);
				
				// AdditionalInfo相关内容解析
				for(NameAndStringValue_T tmp:model.getAdditionalInfo()) {
					// alarmSerialNo
					if ("emsAlarmId".equals(tmp.name)) {
						model.setAlarmSerialNo(tmp.value);
					}
				}
			}*/
			
			String originalInfo=com.fujitsu.util.SerializerUtil.toJSON(event, encode, false);
			model.setOriginalInfo(originalInfo);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return model;
	}
	public String DisposeNetAdress(NetAddress_T addr){
			StringBuilder address=new StringBuilder();
			address.append(addr.ipAddr.a);
			address.append(".");
			address.append(addr.ipAddr.b);
			address.append(".");
			address.append(addr.ipAddr.c);
			address.append(".");
			address.append(addr.ipAddr.d);
			address.append(":");
			address.append(addr.port);
			return address.toString();
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
		//Notice:中兴无此属性
		//model.setClientType(null);
		//model.setClientRate(null);
		//extend
		if(data.additionalInfo!=null&&data.additionalInfo.length>0){
			for(NameAndStringValue_T name:data.additionalInfo){
				if(name.name.equals("IsFix")){
					boolean isFixed = name.value!=null?Boolean.valueOf(name.value):false;
					model.setFixed(isFixed);
				}else if(name.name.equals("LSPType")){
					model.setLSPType(name.value);
				}else if(name.name.equals("PWType")){
					model.setPWType(name.value);
				}else if(name.name.equals("SrcInLabel")){
					model.setSrcInLabel(name.value);
				}else if(name.name.equals("SrcOutLabel")){
					model.setSrcOutLabel(name.value);
				}else if(name.name.equals("DestInLabel")){
					model.setDestInLabel(name.value);
				}else if(name.name.equals("DestOutLabel")){
					model.setDestOutLabel(name.value);
				}else if(name.name.equals("SrcNextHopIP")){
					model.setSrcNextHopIP(name.value);
				}else if(name.name.equals("DestNextHopIP")){
					model.setDestNextHopIP(name.value);
				}else if(name.name.equals("SrcIP")){
					model.setSrcIP(name.value);
				}else if(name.name.equals("DestIP")){
					model.setDestIP(name.value);
				}else if(name.name.equals("BelongedTrail")){
					String belongedTrail = name.value;
					if(belongedTrail!=null&&!belongedTrail.isEmpty()&&belongedTrail.split("SubnetworkConnection").length>0){
						belongedTrail = name.value.split("SubnetworkConnection")[1];
						belongedTrail = belongedTrail.substring(1,belongedTrail.length()-1);
					}
					model.setBelongedTrail(belongedTrail);
				}else if(name.name.equals("LayerRate")){
					model.setLayerRate(name.value);
				}else if(name.name.equals("UserLabel")){
					model.setUserLabel(name.value);
				}else if(name.name.equals("NativeEMSName")){
					model.setNativeEMSName(name.value);
				}
			}
		}
		if(model.getSrcNextHopIPNum()!=null && model.getDestNextHopIPNum() == null){
			model.setRouteType(ROUTE_TYPE_SRC_OR_DEST);
		}else if(model.getSrcNextHopIPNum() ==null && model.getDestNextHopIPNum() == null){
			model.setRouteType(ROUTE_TYPE_PW);
		}else{
			model.setRouteType(ROUTE_TYPE_NORMAL);
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
		//Notice:中兴无
		model.setEmsInSyncState(false);//BD要求不填
		model.setLocation(data.location);
		model.setName(data.name);
		model.setNativeEMSNameOri(data.nativeEMSName);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		//model.setOwner(data.vendorName);//BD要求不填
		model.setMeType(data.meType);
		model.setProductName(data.productName);
		model.setSupportedRates(data.connectionRates);
		model.setUserLabel(NameAndStringValueUtil.Stringformat(data.userLabel,encode));
		model.setVersion(data.hardwareVersion);//BD要求填此字段
		model.setAlarmStatus(data.alarmStatus.value());
		//Notice:中兴特有
		model.setVendorName(data.vendorName);
		model.setSoftwareVersion(data.softwareVersion);
		model.setOperationalStatus(data.operationalStatus);
		model.setDescriptionInfo(data.descriptionInfo);
		model.setNetAddress(DisposeNetAdress(data.netAddress));
		model.setConnectionRates(data.connectionRates);
		model.setHardwareVersion(data.hardwareVersion);
		// extend
		model.setNeSerialNo(nameUtil.getNeSerialNo(model.getName()));
		
		//设置routeId
		for(NameAndStringValue_T name:data.additionalInfo){
			if(name.name.equals("RouteId")){
				if(!name.value.isEmpty()&&name.value.split("=").length>0){
					model.setRouteId(name.value.split("=")[1]);
				}
			}
		}

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
		// 网元级PM
		else if (model.getTpName().length == 2) {
			if (model.getTpName()[1].name.equals(DataCollectDefine.COMMON.MANAGED_ELEMENT)) {
				model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_NE_FLAG);
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
		model.setRetrievalTimeDisplay(DateStrFormatForPM(data.retrievalTime, DataCollectDefine.FACTORY_ZTE_FLAG));

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
		model.setAdditionalInfo(null);//(data.additionalInfo);BD无additionalInfo字段

		model.setConnectionState(data.connectionState.value());
		model.setDirection(data.direction.value());
		model.setEdgePoint(data.edgePoint);
		model.setEgressTrafficDescriptorName(null);// (data.egressTrafficDescriptorName);
		model.setIngressTrafficDescriptorName(null);// (data.ingressTrafficDescriptorName);
		model.setName(data.name);
		//设置nativeEmsName
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.userLabel,encode));// 与userLabel一致,无此参数传入(data.nativeEMSName);
		if(model.getNativeEMSName().isEmpty() || model.getNativeEMSName() ==null){
			if(data.additionalInfo!=null&&data.additionalInfo.length>0){
				for(NameAndAnyValue_T name:data.additionalInfo){
					if(name.name.equals("nativeEMSName")){
						model.setNativeEMSName(NameAndStringValueUtil.Stringformat(name.value.extract_string(),encode));
						break;
					}
				}
			}
		}
		model.setOwner(data.owner);
		model.setTpMappingMode(data.tpMappingMode.value());
		model.setTpProtectionAssociation(data.tpProtectionAssociation.value());

		//以下循环获取速率等参数LIST
		List<LayeredParametersModel> layeredParametersModelList = 
			new ArrayList<LayeredParametersModel>();
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
		//设置ptn参数
		for (LayeredParametersModel parameterModel : model
				.getTransmissionParams()) {
			for (NameAndStringValue_T name : parameterModel
					.getTransmissionParams()) {
				if(name.name.equals("PTNTP_PWId")){
					model.setPTNTP_PWId(name.value);
				}else if(name.name.equals("PTNTP_PWMode")){
					model.setPTNTP_PWMode(name.value);
				}else if(name.name.equals("PTNTP_PWType")){
					model.setPTNTP_PWType(name.value);
				}else if(name.name.equals("PTNTP_InLabel")){
					model.setPTNTP_InLabel(name.value);
				}else if(name.name.equals("PTNTP_OutLabel")){
					model.setPTNTP_OutLabel(name.value);
				}else if(name.name.equals("PTNTP_PSNType")){
					model.setPTNTP_PSNType(name.value);
				}else if(name.name.equals("PTNTP_VCId")){
					model.setPTNTP_VCId(name.value);
				}
			}
		}
		
		//将数字转意成字符串1=rack,2=shelf,3=slot,4=unit
		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.PORT);
		String ptpType = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.ZTE.ZTE_PTP_TYPE);
		String physicalPort = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.ZTE.ZTE_PHYSICAL_PORT);
		String channelNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.ZTE.ZTE_CHANEL_NO);

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
		
/*		BUG #1396
		现状：
		中兴E300 PTP入库时，PTPTYPE=OPM的端口，解析错误。
		PTP:/direction=sink/layerrate=1/ptptype=OPM/rack=0/shelf=3/slot=10/PhysicalPort=1/ChannelNo=23
		以上例子，无法正确识别入库。
		修改为：
		对PTPTYPE=OPM的端口,独立逻辑处理：
		1.PORT_NO=截取PhysicalPort-CHChannelNo 实例：1-CH1 1-CH2 1-CH3 1-CH80
		2.DISPLAY_NAME 不变 实例：1-CH1 1-CH2 1-CH3 1-CH80
		3.PTP_FTP 设为FTP （因为不能在树上显示）
		4.IS_SYNC_CTP 设为已经同步 (因为太多，而且底下不会有CTP)*/
		if(!physicalPort.isEmpty()&&!channelNo.isEmpty()){
			portNo = physicalPort+"-CH"+channelNo;
			model.setPortNo(portNo);
			model.setPtpOrFtp(DataCollectDefine.COMMON.FTP_FLAG);
			model.setIsSyncCtp(DataCollectDefine.TRUE);
		}

		Integer domain = DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG;

		String rate = null;
		
		if(ptpType!=null&&!ptpType.isEmpty()){
			domain = DataCollectDefine.COMMON.DOMAIN_WDM_FLAG;
		} else {
			//BD corba数据入库整理文档 -- domain规律
			//获取xml文件中配置内容
			if(ptpDomainList == null){
				ptpDomainList = XmlUtil.parserXmlForPtpDomain("ZTE_PTP_DOMAIN.xml"); 
			}
			//循环配置项 根据layer匹配填充domain、ptpType、rate
			for(PtpDomainModel temp:ptpDomainList){
				//配置项中的层速率是否在ptp层速率列表中
				if(layerRateList.containsAll(temp.getLayerList())){
					domain=temp.getDomainFlag();
					ptpType = temp.getPtpType();
					rate = temp.getRate();
				}
			}
		}
		
		// 从transmissionParams中获取速率
		if (domain == DataCollectDefine.COMMON.DOMAIN_ETH_FLAG) {
			if(ptpOrFtp == DataCollectDefine.COMMON.FTP_FLAG)
				ptpType = DataCollectDefine.COMMON.PTP_TYPE_OTHER_MP;
			else
				ptpType = DataCollectDefine.COMMON.PTP_TYPE_OTHER_MAC;
			for (LayeredParametersModel parameterModel : model
					.getTransmissionParams()) {
				for (NameAndStringValue_T name : parameterModel
						.getTransmissionParams()) {
					if (name.name.equals("RateLimit")) {
						if (!name.value.isEmpty() && !name.value.endsWith("M")) {
							rate = name.value + "M";
						} else {
							rate = name.value;
						}
					}
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
		
		// for ctp
		if (model.getName().length == 4
				&& model.getName()[3].name.equals(DataCollectDefine.COMMON.CTP)) {

			// 设置name
			model.setNameString(nameUtil.decompositionCtpName(model.getName()));

			// 设置ctp Value
			String ctpValue = model.getName()[3].value;

			if (ctpValue.startsWith("/")) {
				ctpValue = ctpValue.substring(1);
			}
			model.setCtpValue(ctpValue);
		}
		return model;
	}
	
	
	
	/**
	 * 将TPData_T数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 * @throws CommonException 
	 */
	private TerminationPointModel TPDataToModel(
			TPData_T data) {

		TerminationPointModel model = new TerminationPointModel();
		
		model.setName(data.tpName);
		model.setTpMappingMode(data.tpMappingMode.value());
		//以下循环获取速率等参数LIST
		List<LayeredParametersModel> layeredParametersModelList = 
			new ArrayList<LayeredParametersModel>();
		List<Short> layerRateList = new ArrayList<Short>();
		for (LayeredParameters_T layeredParameters : data.transmissionParams) {
			layerRateList.add(layeredParameters.layer);
			LayeredParametersModel layeredParametersModel = new LayeredParametersModel();
			layeredParametersModel = LayeredParametersDataToModel(layeredParameters);
			layeredParametersModelList.add(layeredParametersModel);
		}
		model.setTransmissionParams(layeredParametersModelList);
		model.setIngressTrafficDescriptorName(data.ingressTrafficDescriptorName);
		model.setEgressTrafficDescriptorName(data.egressTrafficDescriptorName);
		
		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		//
		model.setLayerRateString(getLayRateSrting(model.getTransmissionParams()));

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
		model.setUserLabel(NameAndStringValueUtil.Stringformat(data.userLabel,encode));
		model.setzEndTP(data.zEndTP);
		
		for(NameAndStringValue_T info:data.additionalInfo){
			if(info.name.equals("owner")){//中兴无此属性,但仍按BD来
				model.setOwner(info.value);
			}
			else if("nativeEMSName".equals(info.name)){
				model.setNativeEMSName(NameAndStringValueUtil.Stringformat(info.value,encode));
			}
		}

		//extend
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
			if(name.name.equals("type")){//中兴无此属性,但仍按BD来
				model.setType(name.value);
			}
			else if(name.name.equals("Wtrtime")||
					name.name.equals("wtrTime")){
				model.setWtrTime(name.value);
			}
		}
		return model;
	}

	/**
	 * 将MEConfigData_T数据转化成统一的数据模型
	 * 使用192.5.1.16\ZXMP S385\508(P)_大弯-QBJ-ZD301(2)-D1网元测试
	 * @param data
	 * @return
	 */
	public ProtectionGroupModel ProtectionGroupDataToModel(
			ProtectionGroup_T data) {
		
		ProtectionGroupModel model = new ProtectionGroupModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.userLabel,encode));
		model.setOwner(null);
		model.setUserLabel(model.getNativeEMSName());

		model.setProtectionGroupType(data.pgType.value());
		model.setProtectionSchemeState(data.protectionSchemeState.value());
		model.setReversionMode(data.reversionMode.value());
		model.setRate(data.layerRate);
		
		//tpList转换
		List<NameAndStringValue_T[]> nameList = new ArrayList<NameAndStringValue_T[]>();
		for(ProtectionRelation_T pr:data.pgpTPList){
			nameList.add(pr.protectUnit.lmsOrmsName);
			for(ProtectionUnit_T pu:pr.workUnits){
				nameList.add(pu.lmsOrmsName);
			}
		}
		NameAndStringValue_T[][] pgpTPList = new NameAndStringValue_T[nameList.size()][];
		pgpTPList = nameList.toArray(pgpTPList);
		model.setPgpTPList(pgpTPList);
		model.setPgpParameters(data.pgpParameters);
		
		//extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		
//		//BD要求不填
//		String[] param = model.getNameString().split("/");
//		if(param.length == 3){
//			model.setPgpGroup(Integer.valueOf(param[1]));
//			model.setPgpLocation(Integer.valueOf(param[2]));
//		}
		
		//需要整理后才能确定 BD不明
		for(NameAndStringValue_T name:data.pgpParameters){
			if(name.name.equals("SwitchMode")){
				model.setSwitchMode(name.value);
			}
			else if(name.name.equals("WtrTime")){
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
			ProtectionGroup_T data) {
		//Notice:中兴无WDMProtectionGroup数据
		WDMProtectionGroupModel model = new WDMProtectionGroupModel();
		return model;
	}

	/**
	 * 将ClockSource_T数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public ClockSourceStatusModel ClockSourceDataToModel(ClockSource_T data) {

		ClockSourceStatusModel model = new ClockSourceStatusModel();

		model.setAdditionalInfo(data.additionalInfo);
		model.setName(data.name);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		//Notice:中兴无
		//model.setStatus(null);
		//model.setTimingMode(null);
		//model.setQuality(null);
		//model.setWorkingMode(null);
		
		model.setNameString(nameUtil.decompositionName(model.getName()));
		
		model.setCurrent(data.ifCurrentClockSource);
		//
		model.setTimingModeFlag(data.type.value());
		// 时钟质量
		model.setQualityFlag(data.qualityLevel.value());
		//Notice:中兴无
		//model.setWorkingModeFlag(0);
		
		//Notice:中兴特有
		model.setFrame(data.ifExtClkSupportFrame);
		model.setSsm(data.ifUseSSM);
		model.setSyncStatus(data.syncState.value());
		model.setPriority(data.priority);

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
		model.setAlarmReportingIndicator(false);// (data.alarmReportingIndicator);
		model.setExpectedOrInstalledEquipment(data.expectedOrInstalledEquipment);

		model.setHolderState(data.holderState.value());

		model.setName(data.name);
		for(NameAndStringValue_T info:data.additionalInfo){
			if(info.name.equals("owner")){//中兴无此属性,但仍按BD来
				model.setOwner(info.value);
			}
			else if("nativeEMSName".equals(info.name)){
				model.setNativeEMSName(NameAndStringValueUtil.Stringformat(info.value,encode));
			} else if (info.name.equals("ShelfType")) {
				model.setShelfType(info.value);
			}
		}
		model.setUserLabel(data.userLabel);

		model.setHardwareVersion(data.hardwareVersion);
		model.setLocation(data.location);
		model.setSerialNo(data.serialNo);
		model.setVendorName(data.vendorName);

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));

		//根据holdertype的类型，将整形变成字符串表示1=rack,2=shelf,3=slot,4=unit
		if (DataCollectDefine.ZTE.HOLD_TYPE_RACK == data.holderType) {
			model.setHolderType(DataCollectDefine.COMMON.RACK);
		} else if (DataCollectDefine.ZTE.HOLD_TYPE_SHELF == data.holderType) {
			model.setHolderType(DataCollectDefine.COMMON.SHELF);
		} else if (DataCollectDefine.ZTE.HOLD_TYPE_SLOT == data.holderType) {
			if (model.getNameString().contains(
					DataCollectDefine.COMMON.SUB_SLOT)) {
				model.setHolderType(DataCollectDefine.COMMON.SUB_SLOT);
			} else {
				model.setHolderType(DataCollectDefine.COMMON.SLOT);
			}
		} else if (DataCollectDefine.ZTE.HOLD_TYPE_UNIT == data.holderType) {
			if (model.getNameString().contains(
					DataCollectDefine.COMMON.SUB_UNIT)) {
				model.setHolderType(DataCollectDefine.COMMON.SUB_UNIT);
			} else {
				model.setHolderType(DataCollectDefine.COMMON.UNIT);
			}
		} else
			model.setHolderType(String.format("%d", data.holderType));

		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);
		String subSlotNo = nameUtil.getEquipmentNoFromTargetName(
				model.getName(), DataCollectDefine.COMMON.SUB_SLOT);
		String UnitNo = nameUtil.getEquipmentNoFromTargetName(
				model.getName(), DataCollectDefine.COMMON.UNIT);
//		String subUnit = nameUtil.getEquipmentNoFromTargetName(
//				model.getName(), DataCollectDefine.COMMON.SUB_UNIT);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setSubSlotNo(subSlotNo);
		model.setUnitNo(UnitNo);
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
		model.setAlarmReportingIndicator(false);// (data.alarmReportingIndicator);
		model.setExpectedEquipmentObjectType(data.expectedBoardType);// (data.expectedEquipmentObjectType);
		model.setInstalledEquipmentObjectType(data.installedBoardType);// (data.installedEquipmentObjectType);
		model.setInstalledPartNumber("");// (data.installedPartNumber);

		model.setInstalledVersion(data.softwareVersion);//BD要求填此字段
		model.setName(data.name);

		model.setServiceState(data.serviceState.value());
		model.setUserLabel(data.userLabel);
		for(NameAndStringValue_T info:data.additionalInfo){
			if(info.name.equals("owner")){//中兴无此属性,但仍按BD来
				model.setOwner(info.value);
			}else if("nativeEMSName".equals(info.name)){
				//中兴此属性可能无值，此情况填写installedBoardType属性值
				if(info.value.isEmpty()){
					model.setNativeEMSName(NameAndStringValueUtil.Stringformat(model.getInstalledEquipmentObjectType(),encode));
				}else{
				model.setNativeEMSName(NameAndStringValueUtil.Stringformat(info.value,encode));
				}
			}else if("installedSerialNumber".equals(info.name)){
				model.setInstalledSerialNumber(info.value);
			}else if("Transparency".equals(info.value)){
				//设置是否透传板，默认为非透传板
				model.setTransparency(true);
			}
		}
		
		model.setHardwareVersion(data.hardwareVersion);// (hardwareVersion);
		model.setHasProtection(data.hasProtection);// (hasProtection);
		model.setSoftwareVersion(data.softwareVersion);// (softwareVersion);
		model.setInstalledBoardType(data.installedBoardType);// (installedBoardType);
		model.setExpectedBoardType(data.expectedBoardType);// (expectedBoardType);

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));

		if (model.getNameString().contains(DataCollectDefine.COMMON.SUB_SLOT)) {
			model.setSubUnit(true);
		}

		String rackNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.SLOT);
		String subSlotNo = nameUtil.getEquipmentNoFromTargetName(
				model.getName(), DataCollectDefine.COMMON.SUB_SLOT);
		String UnitNo = nameUtil.getEquipmentNoFromTargetName(model.getName(),
				DataCollectDefine.COMMON.UNIT);
		String subUnitNo = nameUtil.getEquipmentNoFromTargetName(
				model.getName(), DataCollectDefine.COMMON.SUB_UNIT);

		model.setRackNo(rackNo);
		model.setShelfNo(shelfNo);
		model.setSlotNo(slotNo);
		model.setSubSlotNo(subSlotNo);
		model.setUnitNo(UnitNo);
		model.setSubUnitNo(subUnitNo);

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
	 * 将EthService数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public EthServiceModel EthServiceDataToModel(EthernetService_T data) {

		EthServiceModel model = new EthServiceModel();

		model.setName(data.name);
		model.setUserLabel(data.userLabel);
		model.setNativeEMSName(NameAndStringValueUtil.Stringformat(data.nativeEMSName,encode));
		model.setOwner(data.owner);
		model.setServiceType(Integer.parseInt(data.serviceType));
		model.setDirection(data.direction.value());
		model.setActiveState(data.active);

		model.setAdditionalInfo(data.additionalInfo);

		// aEndPoint属性
		model.setaEndPoint(data.aEnd[0].tpName);
		model.setaEndPointVlanID(0);// (data.aEnd.vlanID);
		model.setaEndPointTunnel(0);// (data.aEndPoint.tunnel);
		model.setaEndPointVc(0);// (data.aEndPoint.vc);
		model.setaEndPointAdditionalInfo(null);// (data.aEndPoint.additionalInfo);
		
		// zEndPoint属性
		model.setzEndPoint(data.zEnd[0].tpName);
		model.setzEndPointVlanID(0);// (data.zEndPoint.vlanID);
		model.setzEndPointTunnel(0);// (data.zEndPoint.tunnel);
		model.setzEndPointVc(0);// (data.zEndPoint.vc);
		model.setzEndPointAdditionalInfo(null);// (data.zEndPoint.additionalInfo);

		// extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		model.setaEndNESerialNo(nameUtil.getNeSerialNo(model.getaEndPoint()));
		model.setaEndPointName(nameUtil.decompositionName(model.getaEndPoint()));
		model.setzEndNESerialNo(nameUtil.getNeSerialNo(model.getzEndPoint()));
		model.setzEndPointName(nameUtil.decompositionName(model.getzEndPoint()));

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
		model.setPmParameterName(data.pmParameter);
		model.setUnit(NameAndStringValueUtil.Stringformat(data.unit,encode));
		model.setValue(String.valueOf(data.value));
		model.setLocationFlag(getPmLocationFlag(data.pmLocation,
				DataCollectDefine.FACTORY_ZTE_FLAG));
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
					}else if("AlarmState".equals(attribute.name)){
						state.add(model.new State(attribute.name,
								PerceivedSeverity_THelper.read(attribute.value.create_input_stream()).value()));
					}/*else if("connectionState".equals(attribute.name)){
						state.add(model.new State(attribute.name,
								TPConnectionState_THelper.read(attribute.value.create_input_stream()).value()));
					}else if("serviceState".equals(attribute.name)){
						state.add(model.new State(attribute.name,
								ServiceState_THelper.read(attribute.value.create_input_stream()).value()));
					}else if("holderState".equals(attribute.name)){
						state.add(model.new State(attribute.name,
								HolderState_THelper.read(attribute.value.create_input_stream()).value()));
					}else if("active".equals(attribute.name)){
						if(attribute.value.extract_boolean()){
							state.add(model.new State(attribute.name,
									DataCollectDefine.TRUE));
						}else{
							state.add(model.new State(attribute.name,
									DataCollectDefine.FALSE));
						}
					}else if("sncState".equals(attribute.name)){
						
					}else if("ActiveState".equals(attribute.name)){
						
					}*/
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
	 * 将MSTPBindingPath数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public MSTPBindingPathModel MSTPBindingPathToModel(
			VCGBinding_T data) {
		//FIXME 数据转换 没有转换文档
		MSTPBindingPathModel model = new MSTPBindingPathModel();

		model.setVcgTpName(data.vcgTpName);
		model.setDirection(data.direction.value());
		model.setAllPathList(data.bindingTpNameList);
		model.setUsedPathList(data.withPayloadTpNameList);
//		model.setAdditionalInfo(data.additionalInfo);

		return model;
	}
	
	/**
	 * 将VirtualBridge数据转化成统一的数据模型
	 * 
	 * @param data
	 * @return
	 */
	public VirtualBridgeModel VirtualBridgeDataToModel(
			VB_T data) {
		VirtualBridgeModel model = new VirtualBridgeModel();

		model.setName(data.name);
		model.setUserLabel(NameAndStringValueUtil.Stringformat(data.userLabel,encode));
		model.setNativeEMSName(model.getUserLabel());
		model.setOwner(data.owner);

		List<TerminationPointModel> lpList = new ArrayList<TerminationPointModel>();
		for (TPData_T logicalTP : data.lpList) {
			TerminationPointModel point = TPDataToModel(logicalTP);
			lpList.add(point);
		}
		model.setLogicalTPList(lpList);
		
		model.setParameterList(data.parameterList);
		model.setAdditionalInfo(data.additionalInfo);
		
		//extend
		model.setNameString(nameUtil.decompositionName(model.getName()));
		for(NameAndStringValue_T name:data.parameterList){
			if(name.name.toUpperCase().equals("VID".toUpperCase())){
				model.setVid(name.value);
			}
			if(name.name.toUpperCase().equals("STPMode".toUpperCase())){
				model.setStpMode(name.value);
			}
			if(name.name.toUpperCase().equals("BridgePriority".toUpperCase())){
				model.setBridgePriority(name.value);
			}
			if(name.name.toUpperCase().equals("MACAging".toUpperCase())){
				model.setMacAging(name.value);
			}
			if(name.name.toUpperCase().equals("HelloTime".toUpperCase())){
				model.setHelloTime(name.value);
			}
			if(name.name.toUpperCase().equals("MaxAge".toUpperCase())){
				model.setMaxAge(name.value);
			}
			if(name.name.toUpperCase().equals("ForwardDelay".toUpperCase())){
				model.setForwardDelay(name.value);
			}
		}
		return model;
	}

	@Override
	public TCADataModel TCADataToModel(Object event) {
		
		StructuredEvent data = (StructuredEvent)event;
		
		TCADataModel model = new TCADataModel();
		
		try {
			Integer objectTypeValue=null;
			for(Property p:data.filterable_data){
				//notificationId
				if("notificationId".toUpperCase().equals(p.name.toUpperCase())){
					model.setNotificationId(p.value.extract_string());
				}else if("objectName".toUpperCase().equals(p.name.toUpperCase())){
					//objectName
					model.setObjectName(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}else if("objectFilterName".toUpperCase().equals(p.name.toUpperCase())){
					//objectFilterName
					model.setObjectFilterName(p.value.extract_string());
				}else if("alarmDetectInfo".toUpperCase().equals(p.name.toUpperCase())){
					//alarmDetectInfo
					model.setAlarmDetectInfo(NamingAttributes_THelper.read(p.value.create_input_stream()));
				}else if("granularity".toUpperCase().equals(p.name.toUpperCase())){
					//granularity;
					model.setGranularity(p.value.extract_string());
				}else if("pmParameter".toUpperCase().equals(p.name.toUpperCase())){
					//pmParameter;
					model.setPmParameterName(p.value.extract_string());
				}else if("performanceValue".toUpperCase().equals(p.name.toUpperCase())){
					PMMeasurement_T pm = PMMeasurement_THelper.read(p.value.create_input_stream());
					//performanceValue
					model.setValue(pm.value);
					//pmLocation
					model.setPmLocation(pm.pmLocation);
				}else if("thresholdType".toUpperCase().equals(p.name.toUpperCase())){
					//thresholdType;
					model.setThresholdType(PMThresholdType_THelper.read(p.value.create_input_stream()).value());	
				}else if("perceivedSeverity".toUpperCase().equals(p.name.toUpperCase())){
					//perceivedSeverity;
					model.setPerceivedSeverity(PerceivedSeverity_THelper.read(p.value.create_input_stream()).value());
				}else if("raiseTime".toUpperCase().equals(p.name.toUpperCase())){
					// raiseTime->归类至neTime
					model.setNeTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("clearTime".toUpperCase().equals(p.name.toUpperCase())){
					// clearTime
					model.setClearTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("additionalInfo".toUpperCase().equals(p.name.toUpperCase())){
					// additionalInfo NamingAttributesList_T[][]类型，与华为不同 不使用
//					model.setAdditionalInfo(NamingAttributesList_THelper.read(p.value.create_input_stream()));
				}else if("layerRate".toUpperCase().equals(p.name.toUpperCase())){
					//layerRate;
					model.setLayerRate(p.value.extract_short());
				}else if("objectType".toUpperCase().equals(p.name.toUpperCase())){
					// objectType
					objectTypeValue = ObjectType_THelper.read(p.value.create_input_stream()).value();
					model.setObjectType(objectTypeValue);
				}else if("alarmType".toUpperCase().equals(p.name.toUpperCase())){
					// alarmType
					model.setAlarmType(AlarmType_THelper.read(p.value.create_input_stream()).value());
				}else if("EMSTime".toUpperCase().equals(p.name.toUpperCase())){
					// EMSTime
					model.setEmsTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("CorrelatedAlarmIds".toUpperCase().equals(p.name.toUpperCase())){
					//CorrelatedAlarmIds
					model.setCorrelatedAlarmIds(p.value.extract_string());
				}else if("Description".toUpperCase().equals(p.name.toUpperCase())){
					// Description
					String description = NameAndStringValueUtil.Stringformat(p.value.extract_string(),encode);
					model.setDescription(description);
					model.setNativeEMSName(description);
				}else if("serviceAffecting".toUpperCase().equals(p.name.toUpperCase())){
					//serviceAffecting
					model.setServiceAffecting(DataCollectDefine.ZTE.U31_ServiceAffect.toValue(p.value.extract_string()));
				}else if("Confirm Status".toUpperCase().equals(p.name.toUpperCase())){
					//Confirm Status
					model.setConfirmStatusOri(p.value.extract_string());
				}else if("Clear Status".toUpperCase().equals(p.name.toUpperCase())){
					//Clear Status
					model.setClearStatusOri(p.value.extract_string());
				}else if("isClearable".toUpperCase().equals(p.name.toUpperCase())){
					//isClearable
					model.setClearable("true".equals(p.value.extract_string())?true:false);
				}else if("ackUser".toUpperCase().equals(p.name.toUpperCase())){
					// ackUser
					model.setAckUser(p.value.extract_string());
				}else if("ackTime".toUpperCase().equals(p.name.toUpperCase())){
					// ackTime
					model.setAckTime(p.value.extract_string());
				}else if("ackInfo".toUpperCase().equals(p.name.toUpperCase())){
					// ackInfo
					model.setAckInfo(p.value.extract_string());
				}else if("vendorProbableCause".toUpperCase().equals(p.name.toUpperCase())){
					// vendorProbableCause
					model.setVendorProbableCause(NameAndStringValueUtil.Stringformat(p.value.extract_string(),encode));
				}else if("AlarmStatus".toUpperCase().equals(p.name.toUpperCase())){
					// AlarmStatus
					model.setAlarmStatus(p.value.extract_string());
				}else if("probableCause".toUpperCase().equals(p.name.toUpperCase())){
					// probableCause
					model.setProbableCause(p.value.extract_string());
				}else if("CustomerName".toUpperCase().equals(p.name.toUpperCase())){
					// CustomerName
					model.setCustomerName(p.value.extract_string());
				}else if("DiagnoseInfo".toUpperCase().equals(p.name.toUpperCase())){
					// DiagnoseInfo
					model.setDiagnoseInfo(p.value.extract_string());
				}
			}
			if(objectTypeValue==null){//无objectType，通过objectName解析
				model.setObjectType(getObjectTypeByName(model.getObjectName()));
			}
			//位置标识
			model.setLocationFlag(getPmLocationFlag(model.getPmLocation(),DataCollectDefine.FACTORY_ZTE_FLAG));
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
			
//			//测试
//			model.setPmParameterName("BER_BEFORE_FEC");
//			model.setPerceivedSeverity(5);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	//TCA数据转换 中兴专用 当告警处理
	public AlarmDataModel TCADataToAlarmModel(Object event) {
		StructuredEvent data = (StructuredEvent) event;

		TCADataModel tacModel = TCADataToModel(data);

		AlarmDataModel model = new AlarmDataModel();

		model.setNotificationId(tacModel.getNotificationId());
		//
		model.setConfirmStatusOri(tacModel.getConfirmStatusOri());
		model.setClearStatus(String.valueOf(tacModel.getClearStatusOri()));

		model.setClearTime(tacModel.getClearTime());
		model.setEmsTime(tacModel.getEmsTime());
		model.setNeTime(tacModel.getNeTime());
		model.setAlarmType(tacModel.getAlarmType());
		model.setServiceAffecting(tacModel.getServiceAffecting());
		model.setPerceivedSeverity(tacModel.getPerceivedSeverity());
		
		model.setProbableCause(tacModel.getProbableCause());
		model.setLayerRate(tacModel.getLayerRate());
		model.setObjectType(tacModel.getObjectType());
		model.setNativeProbableCause(tacModel.getVendorProbableCause());
		model.setObjectName(tacModel.getObjectName());
		model.setClearable(tacModel.isClearable());
		model.setAlarmReason(tacModel.getDescription());
		model.setAdditionalInfo(tacModel.getAdditionalInfo());
//		// AdditionalInfo相关内容解析
//		for (NameAndStringValue_T tmp : model.getAdditionalInfo()) {
//			// alarmSerialNo
//			if ("emsAlarmId".equals(tmp.name)) {
//				model.setAlarmSerialNo(tmp.value);
//			}
//		}
		String originalInfo=com.fujitsu.util.SerializerUtil.toJSON(event, encode, false);
		model.setOriginalInfo(originalInfo);
		return model;
	}

	@Override
	//FIXME WDM 无真实数据，复制华为
	public ProtectionSwtichDataModel ProtectionSwitchDataToModel(Object event) {

		StructuredEvent data = (StructuredEvent)event;
		
		ProtectionSwtichDataModel model = new ProtectionSwtichDataModel();
		
		try {
			for(Property p:data.filterable_data){
				if("emsTime".toUpperCase().equals(p.name.toUpperCase())){
					// emsTime
					model.setEmsTime(DateStrFormatForAlarm(p.value.extract_string()));
				}else if("ProtectionType".toUpperCase().equals(p.name.toUpperCase())){
					//ProtectionType
					model.setProtectType(ProtectionType_THelper.read(p.value.create_input_stream()).value());
				}else if("protectionGroupType".toUpperCase().equals(p.name.toUpperCase())){
					//wdm有此属性---复制华为，真实数据未知
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
}
