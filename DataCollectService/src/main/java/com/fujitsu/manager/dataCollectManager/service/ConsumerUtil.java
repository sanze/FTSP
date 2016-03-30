package com.fujitsu.manager.dataCollectManager.service;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributes_THelper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import HW.CosNotification.Property;
import HW.notifications.ObjectTypeQualifier_THelper;
import HW.notifications.ObjectType_THelper;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.IService.IFaultManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionSwtichDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TCADataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba.ALUDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba.FIMDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba.HWDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba.LUCENTDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba.ZTEU31DataToModel;
import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.NameAndStringValueUtil;

public class ConsumerUtil {

	private static IDataCollectService dataCollectService;
	private static IFaultManagerService faultManagerService;
	private static DataCollectMapper dataCollectMapper;
	private static NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();
	private static HWDataToModel hwDataToModel;
	private static ZTEU31DataToModel zteu31DataToModel;
	
	private static Logger logger  =  Logger.getLogger(ConsumerUtil. class );
	
	//消息处理方法
	public static void handleNotification(Object notification, int factory,String corbaIp,String encode){
		
		String head = "";
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
			head = ((HW.CosNotification.StructuredEvent)notification).header.fixed_header.event_type.type_name;
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			head = ((ZTE_U31.CosNotification.StructuredEvent)notification).header.fixed_header.event_type.type_name;
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			head = ((LUCENT.CosNotification.StructuredEvent)notification).header.fixed_header.event_type.type_name;
			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			head = ((FENGHUO.CosNotification.StructuredEvent)notification).header.fixed_header.event_type.type_name;
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			head = ((org.omg.CosNotification.StructuredEvent)notification).header.fixed_header.event_type.type_name;
			break;
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			//FIXME
			break;
		}
		System.out.println(new Date() + " 【" + corbaIp + "】 " + head);
		//转换--朗讯与其他厂家不同
		head = DataCollectDefine.COMMON.getEventTypeName(head);
		
		//记录通知事件
		if (!DataCollectDefine.COMMON.NT_HEARTBEAT.equals(head)
				&& !DataCollectDefine.COMMON.NT_STATE_CHANGE.equals(head)
				&& !DataCollectDefine.COMMON.NT_FILE_TRANSFER_STATUS.equals(head)
				&& !DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
			//根据配置是否记录推送信息
			if(CommonUtil.getSystemConfigProperty("writeNotify") != null &&
					Boolean.valueOf(CommonUtil.getSystemConfigProperty("writeNotify"))){
				writeNotify(notification, factory,corbaIp);
			}
		}

		// 心跳推送通知
		if (DataCollectDefine.COMMON.NT_HEARTBEAT.equals(head)) {
			// 更新心跳接收时间
			AutoCheckConnection.updateReceiveTime(corbaIp,AutoCheckConnection.CHECK_HEART_BEATING);
		}
		// 告警推送通知
		else if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
			handleAlarm(notification,factory, corbaIp, encode);
		}
		// 推送文件传送完成标志，上传文件传送完成标志文件
		else if (DataCollectDefine.COMMON.NT_FILE_TRANSFER_STATUS.equals(head)) {
			
		}
		// 状态推送通知
		else if (DataCollectDefine.COMMON.NT_STATE_CHANGE.equals(head)) {
			handleStatusChange(notification,factory, corbaIp, encode);
		}
		
		// TCA越限告警处理方法
		else if (DataCollectDefine.COMMON.NT_TCA.equals(head)) {
			handleTCA(notification,factory, corbaIp, encode);
		}
		
		// 保护倒换入库
		else if (DataCollectDefine.COMMON.NT_PROTECTION_SWITCH.equals(head)||
				DataCollectDefine.COMMON.NT_WDMPROTECTION_SWITCH.equals(head)) {
			handleProtectionSwitch(notification,factory, corbaIp, encode, head);
		}
		// 增量更新，if条件修改
		else if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(head)||
				DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(head)||
				DataCollectDefine.COMMON.NT_REQUEST_SYNCHRONIZATION.equals(head)){
			handleIncrementalupdate(notification,factory, corbaIp, encode,head);
		}
	}
	
	
	/**
	 * 告警处理方法
	 * @param notification
	 */
	private static void handleAlarm(Object notification, int factory,String corbaIp,String encode){
		
		if (dataCollectMapper == null) {
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		
		DataToModel dataToModel = null;
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
			dataToModel = new HWDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			dataToModel = new ZTEU31DataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			dataToModel = new LUCENTDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			dataToModel = new FIMDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			dataToModel = new ALUDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			//FIXME
			break;
		}
		try {
			AlarmDataModel model = dataToModel
					.AlarmDataToModel(notification);
			//记录告警信息
			logAlarmMessage(model);
			//入库
			alarmDataToMongodb(factory,corbaIp,model);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	//告警模型入库
	private static void alarmDataToMongodb(int factory,String corbaIp,AlarmDataModel model) throws NumberFormatException, CommonException{
		
		if (faultManagerService == null) {
			faultManagerService = (IFaultManagerService) BeanUtil
					.getBean("faultManagerService");
		}
		
		List<AlarmDataModel> alarmList = new ArrayList<AlarmDataModel>();
		
		if(model.getObjectName().length>0){//2014-04-21 添加原因:北京 华为T2000原始数据发现ObjectType=6但ObjectName为空
			// 获取ems信息
			Map connection = dataCollectMapper.selectEmsConnectionByIP(
					corbaIp, DataCollectDefine.FALSE);

			model.setEmsId(Integer.valueOf(connection.get(
					"BASE_EMS_CONNECTION_ID").toString()));
			model.setFactory(factory);

			alarmList.clear();
			alarmList.add(model);
			// 告警模块入库
			faultManagerService.alarmDataToMongodb(alarmList, Integer
					.valueOf(connection.get("BASE_EMS_CONNECTION_ID")
							.toString()), null,
					DataCollectDefine.ALARM_TO_DB_TYPE_PUSH);
		}
	}
	
	/**
	 * TCA越限告警处理方法
	 * @param notification
	 */
	private static void handleTCA(Object notification, int factory,String corbaIp,String encode){

		if (dataCollectService == null) {
			dataCollectService = (IDataCollectService) BeanUtil
					.getBean("dataCollectService");
		}
		if (dataCollectMapper == null) {
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		
		// 获取ems信息
		Map connection = dataCollectMapper.selectEmsConnectionByIP(
				corbaIp, DataCollectDefine.FALSE);
		
		DataToModel dataToModel = null;
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
			dataToModel = new HWDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			dataToModel = new ZTEU31DataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			dataToModel = new LUCENTDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			dataToModel = new FIMDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			dataToModel = new ALUDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			//FIXME
			break;
		}
		try {
			//中兴tca当作告警处理，其他正常按tca处理
			if(DataCollectDefine.FACTORY_ZTE_FLAG == factory){
				//转化为告警模型
				AlarmDataModel model = dataToModel
						.TCADataToAlarmModel(notification);
				//入库
				alarmDataToMongodb(factory,corbaIp,model);
			}else{
				TCADataModel model = dataToModel
						.TCADataToModel(notification);
				//设置网管Id
				model.setEmsConnectionId(Integer.valueOf(connection.get(
						"BASE_EMS_CONNECTION_ID").toString()));
				//设置网管类型
				model.setEmsType(Integer.valueOf(connection.get(
						"TYPE").toString()));
				//设置厂家
				model.setFactory(factory);
				//唯一性标识
				String filterForClear = model.getFactory() + ":"
						+ model.getObjectNameFullString() + ":"
						+ model.getLayerRate() + ":" + model.getGranularityFlag() + ":"
						+ model.getPmParameterName();
				model.setFilterForClear(filterForClear);
				//插入tca数据
				dataCollectService.insertTCAData(model);
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * 保护倒换处理方法
	 * @param notification
	 */
	private static void handleProtectionSwitch(Object notification, int factory,String corbaIp,String encode, String head){
		
		if (dataCollectService == null) {
			dataCollectService = (IDataCollectService) BeanUtil
					.getBean("dataCollectService");
		}
		if (dataCollectMapper == null) {
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		
		// 获取ems信息
		Map connection = dataCollectMapper.selectEmsConnectionByIP(
				corbaIp, DataCollectDefine.FALSE);
		
		DataToModel dataToModel = null;
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
			dataToModel = new HWDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			dataToModel = new ZTEU31DataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			dataToModel = new LUCENTDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			dataToModel = new FIMDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			dataToModel = new ALUDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			//FIXME
			break;
		}
		try {
			ProtectionSwtichDataModel model = dataToModel
					.ProtectionSwitchDataToModel(notification);
			
			//设置PROTECT_CATEGORY
/*			设备保护:NT_EPROTECTION_SWITCH
			线路保护:NT_PROTECTION_SWITCH
			ATM保护:NT_ATMPROTECTION_SWITCH
			RPR保护:NT_RPRPROTECTION_SWITCH
			WDM保护:NT_WDMPROTECTION_SWITCH*/
			//前台转换
/*			if(DataCollectDefine.COMMON.NT_PROTECTION_SWITCH.equals(head)){
				model.setProtectCategory("线路保护");
			}else if(DataCollectDefine.COMMON.NT_WDMPROTECTION_SWITCH.equals(head)){
				model.setProtectCategory("WDM保护");
			}else if(DataCollectDefine.COMMON.NT_EPROTECTION_SWITCH.equals(head)){
				model.setProtectCategory("设备保护");
			}else if(DataCollectDefine.COMMON.NT_ATMPROTECTION_SWITCH.equals(head)){
				model.setProtectCategory("ATM保护");
			}else if(DataCollectDefine.COMMON.NT_RPRPROTECTION_SWITCH.equals(head)){
				model.setProtectCategory("RPR保护");
			}else{
				model.setProtectCategory(head);
			}*/
			model.setProtectCategory(head);
			//设置网管Id
			model.setEmsConnectionId(Integer.valueOf(connection.get(
					"BASE_EMS_CONNECTION_ID").toString()));
			
			//插入保护倒换数据
			dataCollectService.insertProtectionSwitchData(model);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * 状态更改处理方法
	 * @param notification
	 */
	private static void handleStatusChange(Object notification, int factory,String corbaIp,String encode){
		if (dataCollectService == null) {
			dataCollectService = (IDataCollectService) BeanUtil
					.getBean("dataCollectService");
		}
		if(dataCollectMapper == null){
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		
		DataToModel dataToModel = null;
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
			dataToModel = new HWDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			dataToModel = new ZTEU31DataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			dataToModel = new LUCENTDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			dataToModel = new FIMDataToModel(encode);
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			//FIXME 贝尔暂时不支持状态处理
			dataToModel = new ALUDataToModel(encode);
			return;
//			break;
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			//FIXME
			break;
		}
		
		try {
			StateDataModel model=dataToModel.StateDataToModel(notification);
			// 获取ems信息
			Map connection = dataCollectMapper.selectEmsConnectionByIP(
					corbaIp, DataCollectDefine.FALSE);
			if(connection!=null&&
					connection.get("BASE_EMS_CONNECTION_ID")!=null){
				model.setEmsId(Integer.valueOf(connection.get(
					"BASE_EMS_CONNECTION_ID").toString()));
				dataCollectService.updateState(model);
			}
		} catch (Exception e){
			ExceptionHandler.handleException(e);
		}
	}
	
	
	/**
	 * 处理增量更新
	 * @param notification
	 */
	private static void handleIncrementalupdate(Object notification, int factory,String corbaIp,String encode,String head){
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
			handleHWIncrementalupdate(notification,corbaIp,encode,head);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			handleZTEIncrementalupdate(notification,corbaIp,encode,head);
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:

			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:

			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:

			break;
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:

			break;
		}
	}
	
	/**
	 * 处理华为增量更新
	 * @param notification
	 */
	private static void handleHWIncrementalupdate(Object notification, String corbaIp,String encode,String head){

		// 获取表头名，判断是新增删除或状态改变
		int objectType=-1;
		String objectTypeQualifier="";
		
		// 获取objectType的值
		for(Property pro:((HW.CosNotification.StructuredEvent)notification).filterable_data){
			if("objectType".equals(pro.name)){
				objectType=ObjectType_THelper.read(pro.value.create_input_stream()).value();
				break;
			}
		}
		// objectType = 12时表示其他类型，需要再获取objectTypeQualifier来区分类型
		for(Property pro:((HW.CosNotification.StructuredEvent)notification).filterable_data){
			if("objectTypeQualifier".equals(pro.name)){
				objectTypeQualifier=ObjectTypeQualifier_THelper.read(pro.value.create_input_stream());
				break;
			}
		}
		// 
		
			switch(objectType){
			case HW.notifications.ObjectType_T._OT_EMS:
				// 网管
				break;
			case HW.notifications.ObjectType_T._OT_MANAGED_ELEMENT:
				if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(head)
						||DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(head)){
					processNe(head,corbaIp);
				}
				// 网元
				break;
			case HW.notifications.ObjectType_T._OT_MULTILAYER_SUBNETWORK:
				// 多层子网
				break;
			case HW.notifications.ObjectType_T._OT_TOPOLOGICAL_LINK:
				// link增删
				HW.topologicalLink.TopologicalLink_T link= null;
				if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(head)){
					link=HW.topologicalLink.TopologicalLink_THelper.read(((HW.CosNotification.StructuredEvent)notification).remainder_of_body.create_input_stream());
					TopologicalLinkModel model = hwDataToModel.TopologicalLinkDataToModel(link);
					processLink(model,head,corbaIp);
				}else if(DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(head)){
					// 获取link的name
					String linkName = "";
					NameAndStringValue_T[] objectName = null;
					for(Property property:((HW.CosNotification.StructuredEvent)notification).filterable_data){
						if(property.name.equals("objectName")){
							objectName = NamingAttributes_THelper.read(
									property.value.create_input_stream());
							break;
						}
					}
					linkName = nameUtil.decompositionName(objectName);
					processDeleteLink(linkName,head,corbaIp);
				}
				
				break;
			case HW.notifications.ObjectType_T._OT_SUBNETWORK_CONNECTION:
				// 子网
				break;
			case HW.notifications.ObjectType_T._OT_PHYSICAL_TERMINATION_POINT:
				// 端口 未找到
				NameAndStringValue_T[] objectNamePtp= null;
				for(Property pro:((HW.CosNotification.StructuredEvent)notification).filterable_data){
					if("objectName".equals(pro.name)){
						objectNamePtp = NamingAttributes_THelper.read(pro.value.create_input_stream());
						break;
					}
				}
				// 获取网元 需要验证是否正确
				String neNamePtp = nameUtil.getNeSerialNo(objectNamePtp);
				// 标记需要更新的网元
				flagNe(neNamePtp,corbaIp);
				break;
			case HW.notifications.ObjectType_T._OT_CONNECTION_TERMINATION_POINT:
				// 时隙 未找到
				break;
			case HW.notifications.ObjectType_T._OT_TERMINATION_POINT_POOL:
				// 不知
				break;
			case HW.notifications.ObjectType_T._OT_EQUIPMENT_HOLDER:
				// 不知
				break;
			case HW.notifications.ObjectType_T._OT_EQUIPMENT:
				// 板卡 标记一下 更新
				NameAndStringValue_T[] objectName= null;
				for(Property pro:((HW.CosNotification.StructuredEvent)notification).filterable_data){
					if("objectName".equals(pro.name)){
						objectName = NamingAttributes_THelper.read(pro.value.create_input_stream());
						break;
					}
				}
				// 获取网元 需要验证是否正确
				String neName = nameUtil.getNeSerialNo(objectName);
				// 标记需要更新的网元
				flagNe(neName,corbaIp);
				break;
			case HW.notifications.ObjectType_T._OT_PROTECTION_GROUP:
				// 保护 标记一下 更新
				NameAndStringValue_T[] objectNameGroup= null;
				for(Property pro:((HW.CosNotification.StructuredEvent)notification).filterable_data){
					if("objectName".equals(pro.name)){
						objectNameGroup = NamingAttributes_THelper.read(pro.value.create_input_stream());
						break;
					}
				}
				// 获取网元 需要验证是否正确
				String neNameGroup = nameUtil.getNeSerialNo(objectNameGroup);
				// 标记需要更新的网元
				flagNe(neNameGroup,corbaIp);
				break;
			case HW.notifications.ObjectType_T._OT_TRAFFIC_DESCRIPTOR:
				// 不知
				break;
			case HW.notifications.ObjectType_T._OT_AID:
				// 其他
				if("OT_CROSSCONNECT".endsWith(objectTypeQualifier)){
					HW.subnetworkConnection.CrossConnect_T cc = null;
					if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(head)
							||DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(head)){
						cc =HW.subnetworkConnection.CrossConnect_THelper.read(((HW.CosNotification.StructuredEvent)notification).remainder_of_body.create_input_stream());
					}
					processCrossConnect(cc,head,corbaIp);
				}else if("OT_ETH_SERVICE".endsWith(objectTypeQualifier)){
					// 保护 标记一下 更新
					NameAndStringValue_T[] objectNameEth= null;
					for(Property pro:((HW.CosNotification.StructuredEvent)notification).filterable_data){
						if("objectName".equals(pro.name)){
							objectNameEth = NamingAttributes_THelper.read(pro.value.create_input_stream());
							break;
						}
					}
					// 获取网元 需要验证是否正确
					String neNameEth = nameUtil.getNeSerialNo(objectNameEth);
					// 标记需要更新的网元
					flagNe(neNameEth,corbaIp);
				}else if("OT_VB".endsWith(objectTypeQualifier)){
					System.out.println("类型不支持！");
				}else if("OT_VLAN".endsWith(objectTypeQualifier)){
					System.out.println("类型不支持！");
				}else if("OT_WDM_PROTECTION_GROUP".endsWith(objectTypeQualifier)){
					System.out.println("类型不支持！");
				}else if("OT_EPROTECTION_GROUP".endsWith(objectTypeQualifier)){
					// 有此类型数据，未解析
					System.out.println("类型不支持！");
				}else if("OT_SNPP_LINK".endsWith(objectTypeQualifier)){
					// 有数据，无需解析
					System.out.println("类型不支持！");
				}else{
					System.out.println("类型不支持！");
				}
				break;
			default:
				System.out.println("类型不支持！");
				break;
			}
		
	
	}
	
	/**
	 * 处理中兴增量更新
	 * @param notification
	 */
	private static void handleZTEIncrementalupdate(Object notification, String corbaIp,String encode,String head){

		// 中兴时发送同步请求，所以不要做具体更新，只需要在数据库标记为需要同步
		Map select = null;
		System.out.println("head&&&&&"+head);
		if(DataCollectDefine.COMMON.NT_REQUEST_SYNCHRONIZATION.equals(head)){
			NameAndStringValue_T[] objectName= null;
			for(ZTE_U31.CosNotification.Property pro:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
				if("meName".equals(pro.name)){
					objectName = NamingAttributes_THelper.read(pro.value.create_input_stream());
					break;
				}
			}
			// 获取网元 需要验证是否正确
			String neName = nameUtil.getNeSerialNo(objectName);
			// 标记需要更新的网元
			flagNe(neName,corbaIp);
		}else if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(head)||
				DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(head)){
			int objectType=-1;
			String objectTypeQualifier="";
			
			for(ZTE_U31.CosNotification.Property pro:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
				if("objectTypeQualifier".equals(pro.name)){
					objectTypeQualifier=ObjectTypeQualifier_THelper.read(pro.value.create_input_stream());
					break;
				}
			}
			// 获取objectType的值
			for(ZTE_U31.CosNotification.Property pro:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
				if("objectType".equals(pro.name)){
					objectType=ZTE_U31.notifications.ObjectType_THelper.read(pro.value.create_input_stream()).value();
					break;
				}
			}
			switch(objectType){
				case ZTE_U31.notifications.ObjectType_T._OT_EMS:
					// 网管
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_MANAGED_ELEMENT:
					if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(head)
							||DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(head)){
						processNe(head,corbaIp);
					}
					// 网元
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_MULTILAYER_SUBNETWORK:
					// 多层子网 
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_TOPOLOGICAL_LINK:
					//处理link
					ZTE_U31.topologicalLink.TopologicalLink_T link= null;
					if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(head)){
						link=ZTE_U31.topologicalLink.TopologicalLink_THelper.read(((ZTE_U31.CosNotification.StructuredEvent)notification).remainder_of_body.create_input_stream());
						TopologicalLinkModel model = zteu31DataToModel.TopologicalLinkDataToModel(link);
						processLink(model,head,corbaIp);
					}else if(DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(head)){
						// 获取link的name
						// 格式不一致，不做处理
						String linkName = "";
						NameAndStringValue_T[] objectName = null;
						for(ZTE_U31.CosNotification.Property property:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
							if(property.name.equals("objectName")){
								objectName = NamingAttributes_THelper.read(
										property.value.create_input_stream());
								break;
							}
						}
						linkName = nameUtil.decompositionName(objectName);
						processDeleteZTELink(linkName,head,corbaIp);
					}
					// 网元
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_SUBNETWORK_CONNECTION:
					// 子网
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_PHYSICAL_TERMINATION_POINT:
					//端口
					//板卡
					NameAndStringValue_T[] objectNamePtp= null;
					for(ZTE_U31.CosNotification.Property pro:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
						if("objectName".equals(pro.name)){
							objectNamePtp = NamingAttributes_THelper.read(pro.value.create_input_stream());
							break;
						}
					}
					// 获取网元 需要验证是否正确
					String neNamePtp = nameUtil.getNeSerialNo(objectNamePtp);
					// 标记需要更新的网元
					flagNe(neNamePtp,corbaIp);
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_CONNECTION_TERMINATION_POINT:
					// 时隙
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_TERMINATION_POINT_POOL:
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_EQUIPMENT_HOLDER:
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_EQUIPMENT:
					//板卡
					NameAndStringValue_T[] objectName= null;
					for(ZTE_U31.CosNotification.Property pro:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
						if("objectName".equals(pro.name)){
							objectName = NamingAttributes_THelper.read(pro.value.create_input_stream());
							break;
						}
					}
					// 获取网元 需要验证是否正确
					String neName = nameUtil.getNeSerialNo(objectName);
					// 标记需要更新的网元
					flagNe(neName,corbaIp);
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_PROTECTION_GROUP:
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_TRAFFIC_DESCRIPTOR:
					break;
				case ZTE_U31.notifications.ObjectType_T._OT_AID:
					if("OT_CROSSCONNECT".endsWith(objectTypeQualifier)){
						// 保护 标记一下 更新
						NameAndStringValue_T[] objectNameCrs= null;
						for(ZTE_U31.CosNotification.Property pro:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
							if("objectName".equals(pro.name)){
								objectNameCrs = NamingAttributes_THelper.read(pro.value.create_input_stream());
								break;
							}
						}
						// 获取网元 需要验证是否正确
						String neNameCrs = nameUtil.getNeSerialNo(objectNameCrs);
						// 标记需要更新的网元
						flagNe(neNameCrs,corbaIp);
					}else if("OT_ETH_SERVICE".endsWith(objectTypeQualifier)){
						// 保护 标记一下 更新
						NameAndStringValue_T[] objectNameEth= null;
						for(ZTE_U31.CosNotification.Property pro:((ZTE_U31.CosNotification.StructuredEvent)notification).filterable_data){
							if("objectName".equals(pro.name)){
								objectNameEth = NamingAttributes_THelper.read(pro.value.create_input_stream());
								break;
							}
						}
						// 获取网元 需要验证是否正确
						String neNameEth = nameUtil.getNeSerialNo(objectNameEth);
						// 标记需要更新的网元
						flagNe(neNameEth,corbaIp);
					}else if("OT_VB".endsWith(objectTypeQualifier)){
						System.out.println("类型不支持！");
					}else if("OT_VLAN".endsWith(objectTypeQualifier)){
						System.out.println("类型不支持！");
					}else if("OT_WDM_PROTECTION_GROUP".endsWith(objectTypeQualifier)){
						System.out.println("类型不支持！");
					}else if("OT_EPROTECTION_GROUP".endsWith(objectTypeQualifier)){
						// 有此类型数据，未解析
						System.out.println("类型不支持！");
					}else if("OT_SNPP_LINK".endsWith(objectTypeQualifier)){
						// 有数据，无需解析
						System.out.println("类型不支持！");
					}else{
						System.out.println("类型不支持！");
					}
					break;
				default:
					System.out.println("类型不支持！");
					break;
			}
		}
		
	
	}
	// 处理link逻辑
	private static void processLink(TopologicalLinkModel model ,String name,String corbaIp){
		Map select = null;
		Map update = null;
		Map insert = null;
		Map delete  = null;
		Map aEndPtp = null;
		Map zEndPtp = null;
		List<Map> linkList = new ArrayList<Map>();
		
			if (dataCollectMapper == null) {
				dataCollectMapper = (DataCollectMapper) BeanUtil
						.getBean("dataCollectMapper");
			}
			
		
			// 判断是否是内部link
			select = new HashMap();
			select.put("NAME", "t_base_ems_connection");
			select.put("ID_NAME", "IP");
			select.put("ID_VALUE", corbaIp);
			List<Map> listEms = dataCollectMapper.getByParameter(select);
		if(listEms!=null&&listEms.size()>0){
			int isMain = DataCollectDefine.TRUE;
			// a端端口
			aEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
					getEmsConnectionId(listEms.get(0)), model.getaEndNESerialNo(),
					model.getaEndPtpName());
			if(aEndPtp==null){
				// 表示a端网元未同步
				System.out.println("a端端口不存在！");
				return;
			}
			System.out.println(model.getaEndNESerialNo()+"=="+"--"+model.getaEndPtpName());
			// z端端口
			zEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
					getEmsConnectionId(listEms.get(0)), model.getzEndNESerialNo(),
					model.getzEndPtpName());
			if(zEndPtp==null){
				// 表示z端网元未同步
				System.out.println("z端端口不存在！");
				return;
			}
			System.out.println(model.getzEndNESerialNo()+"=="+"--"+model.getzEndPtpName());
			// 判断az端网元是否相同，如果相同则是内部link。
			if(aEndPtp!=null&&aEndPtp.get("BASE_NE_ID")!=null&&zEndPtp!=null&&zEndPtp.get("BASE_NE_ID")!=null
					&&aEndPtp.get("BASE_NE_ID").toString().equals(zEndPtp.get("BASE_NE_ID").toString())){
				update = new HashMap();
				update.put("NAME", "t_base_ne");
				update.put("ID_NAME_2", "BASIC_SYNC_STATUS");
				update.put("ID_VALUE_2",DataCollectDefine.SYNC_NEED_FLAG );
				update.put("ID_NAME", "BASE_NE_ID");
				update.put("ID_VALUE", aEndPtp.get("BASE_NE_ID"));
				dataCollectMapper.updateByParameter(update);
			}else{
				// 更新网管链路
				update = new HashMap();
				update.put("NAME", "t_base_ems_connection");
				update.put("ID_NAME_2", "LINK_SYNC_STATUS");
				update.put("ID_VALUE_2",DataCollectDefine.SYNC_NEED_FLAG );
				update.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
				update.put("ID_VALUE", listEms.get(0).get("BASE_EMS_CONNECTION_ID"));
				dataCollectMapper.updateByParameter(update);
			}
			
			// 判断是新增还是删除
			if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(name)){
				// 组织link表数据
				Map linkMap = topologicalLinkModelToTable(model,
						getEmsConnectionId(listEms.get(0)), Integer.valueOf(aEndPtp.get(
								"BASE_NE_ID").toString()),
						Integer.valueOf(aEndPtp.get("BASE_PTP_ID")
								.toString()), Integer.valueOf(zEndPtp.get(
								"BASE_NE_ID").toString()),
						Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
								.toString()),
						12,
						false);
				//设置是否主电路
				linkMap.put("IS_MAIN", isMain);
				// 判断端口是否已被占用
				select = new HashMap();
				select.put("NAME", "t_base_link");
				select.put("ID_NAME", "A_END_PTP");
				select.put("ID_VALUE", aEndPtp.get("BASE_PTP_ID"));
				List<Map> listPtpA = dataCollectMapper.getByParameter(select);
				if(listPtpA==null||listPtpA.size()<1){
					select = new HashMap();
					select.put("NAME", "t_base_link");
					select.put("ID_NAME", "Z_END_PTP");
					select.put("ID_VALUE", zEndPtp.get("BASE_PTP_ID"));
					List<Map> listPtpZ = dataCollectMapper.getByParameter(select);
					if(listPtpZ==null||listPtpZ.size()<1){
						linkList.add(linkMap);
						//设置a,z端口为连接点
						aEndPtp.put("PORT_TYPE", DataCollectDefine.COMMON.PORT_TYPE_LINK_POINT);
						dataCollectMapper.updatePtpById(aEndPtp);
					}
				}
				
				if(model.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
					isMain = DataCollectDefine.FALSE;
					Map _linkMap = topologicalLinkModelToTable(model,
							getEmsConnectionId(listEms.get(0)), Integer.valueOf(zEndPtp.get(
									"BASE_NE_ID").toString()),
							Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
									.toString()), Integer.valueOf(aEndPtp.get(
									"BASE_NE_ID").toString()),
							Integer.valueOf(aEndPtp.get("BASE_PTP_ID")
									.toString()),
							12,
							false);
					//设置是否主电路
					_linkMap.put("IS_MAIN", isMain);
					select = new HashMap();
					select.put("NAME", "t_base_link");
					select.put("ID_NAME", "A_END_PTP");
					select.put("ID_VALUE", zEndPtp.get("BASE_PTP_ID"));
					List<Map> _listPtpA = dataCollectMapper.getByParameter(select);
					if(_listPtpA==null||_listPtpA.size()<1){
						select = new HashMap();
						select.put("NAME", "t_base_link");
						select.put("ID_NAME", "Z_END_PTP");
						select.put("ID_VALUE", aEndPtp.get("BASE_PTP_ID"));
						List<Map> _listPtpZ = dataCollectMapper.getByParameter(select);
						if(_listPtpZ==null||_listPtpZ.size()<1){
							linkList.add(_linkMap);
							zEndPtp.put("PORT_TYPE", DataCollectDefine.COMMON.PORT_TYPE_LINK_POINT);
							dataCollectMapper.updatePtpById(zEndPtp);
						}
					}
				}
				
				// 插入link数据
				if (linkList.size() > 0) {
					dataCollectMapper.insertLinkBatch(linkList);
				}
			}
			
		}
			
			
		
	}
	
	// 处理网元逻辑
	private static void processNe(String name,String corbaIp){
		Map select = null;
		if (dataCollectService == null) {
			dataCollectService = (IDataCollectService) BeanUtil
					.getBean("dataCollectService");
		}
		select = new HashMap();
		select.put("NAME", "t_base_ems_connection");
		select.put("ID_NAME", "IP");
		select.put("ID_VALUE", corbaIp);
		List<Map> listEms = dataCollectMapper.getByParameter(select);
		if(listEms!=null&&listEms.size()>0){
			try {
				dataCollectService.syncNeList(listEms.get(0), DataCollectDefine.COLLECT_LEVEL_1);
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 标记网元需要更新
	 * @param name
	 * @param corbaIp
	 */
	private static void flagNe(String neName,String corbaIp){
			Map select = null;
			Map update = null;
			if (dataCollectService == null) {
				dataCollectService = (IDataCollectService) BeanUtil
						.getBean("dataCollectService");
			}
			select = new HashMap();
			select.put("NAME", "t_base_ems_connection");
			select.put("ID_NAME", "IP");
			select.put("ID_VALUE", corbaIp);
			List<Map> listEms = dataCollectMapper.getByParameter(select);
			if(listEms!=null&&listEms.size()>0){
				update  = new HashMap();
				update.put("NAME", "t_base_ne");
				update.put("ID_NAME_2", "BASIC_SYNC_STATUS");
				update.put("ID_VALUE_2",DataCollectDefine.SYNC_NEED_FLAG );
				update.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
				update.put("ID_VALUE", listEms.get(0).get("BASE_EMS_CONNECTION_ID"));
				update.put("ID_NAME_", "NAME");
				update.put("ID_VALUE_", neName);
				dataCollectMapper.updateByParameter(update);
			}
			
		}
		
	// 删除link
	private static void processDeleteLink(String linkName ,String name,String corbaIp){
		Map update = null;
		if(DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(name)){
			update = new HashMap();
			System.out.println("model.getNameString()=="+linkName);
			update.put("NAME", "t_base_link");
			update.put("ID_NAME_2", "IS_DEL");
			update.put("ID_VALUE_2",DataCollectDefine.TRUE );
			update.put("ID_NAME", "NAME");
			update.put("ID_VALUE", linkName);
			dataCollectMapper.updateByParameter(update);
		}
	}
			
	// 删除link
	private static void processDeleteZTELink(String linkName ,String name,String corbaIp){
		Map update = null;
		if(DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(name)){
			System.out.println("1======1");
			update = new HashMap();
			System.out.println("model.getNameString()=="+linkName);
			///managedelement=519(P)/rack=1/shelf=1/slot=5/port=1_/managedelement=502(P)/rack=1/shelf=1/slot=6/port=2
			// 将此数据解析
			List<Map> list = getLinkid(linkName,corbaIp);
			for(Map map :list){
				update.put("NAME", "t_base_link");
				update.put("ID_NAME_2", "IS_DEL");
				update.put("ID_VALUE_2",DataCollectDefine.TRUE );
				update.put("ID_NAME", "BASE_LINK_ID");
				update.put("ID_VALUE", map.get("BASE_LINK_ID"));
				dataCollectMapper.updateByParameter(update);
			}
			
		}
	}
	public static List<Map> getLinkid(String linkName,String corbaIp){
		List<Map> list = new ArrayList<Map>();
		Map select = null;
		Map update = null;
		if (dataCollectService == null) {
			dataCollectService = (IDataCollectService) BeanUtil
					.getBean("dataCollectService");
		}
		String [] az = linkName.split("_/");
		if(az.length==2){
			String [] aptp = az[0].split("/rack");
			if(aptp.length==2){
				String [] ane = aptp[0].split("managedelement=");
				select = new HashMap();
				select.put("NAME", "t_base_ems_connection");
				select.put("ID_NAME", "IP");
				select.put("ID_VALUE", corbaIp);
				List<Map> listEms = dataCollectMapper.getByParameter(select);
				if(listEms!=null&&listEms.size()>0){
					select = new HashMap();
					select.put("NAME", "t_base_ne");
					select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
					select.put("ID_VALUE", corbaIp);
					select.put("ID_NAME_2", "NAME");
					select.put("ID_VALUE_2", ane[1]);
					List<Map> listaNe = dataCollectMapper.getByParameter(select);
					if(listaNe!=null&&listaNe.size()>0){
						// 查看z端
						String [] zptp = az[1].split("/rack");
						if(zptp.length == 2){
							String [] zne = aptp[0].split("managedelement=");
							select = new HashMap();
							select.put("NAME", "t_base_ne");
							select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
							select.put("ID_VALUE", corbaIp);
							select.put("ID_NAME_2", "NAME");
							select.put("ID_VALUE_2", zne[1]);
							List<Map> listzNe = dataCollectMapper.getByParameter(select);
							if(listzNe!=null&&listzNe.size()>0){
								if(listaNe.get(0).get("BASE_NE_ID").toString().equals(listzNe.get(0).get("BASE_NE_ID").toString())){
									//  内部link
									update = new HashMap();
									update.put("NAME", "t_base_ne");
									update.put("ID_NAME_2", "BASIC_SYNC_STATUS");
									update.put("ID_VALUE_2",DataCollectDefine.SYNC_NEED_FLAG );
									update.put("ID_NAME", "BASE_NE_ID");
									update.put("ID_VALUE", listaNe.get(0).get("BASE_NE_ID"));
									dataCollectMapper.updateByParameter(update);
								}else{
									// 外部link
									update = new HashMap();
									update.put("NAME", "t_base_ems_connection");
									update.put("ID_NAME_2", "LINK_SYNC_STATUS");
									update.put("ID_VALUE_2",DataCollectDefine.SYNC_NEED_FLAG );
									update.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
									update.put("ID_VALUE", listEms.get(0).get("BASE_EMS_CONNECTION_ID"));
									dataCollectMapper.updateByParameter(update);
								}
								select = new HashMap();
								select.put("NAME", "t_base_ptp");
								select.put("ID_NAME", "BASE_NE_ID");
								select.put("ID_VALUE", listaNe.get(0).get("BASE_NE_ID"));
								select.put("ID_NAME_2", "NAME");
								select.put("ID_VALUE_2", "PTP:/rack"+aptp[1]);
								List<Map> listaPtp = dataCollectMapper.getByParameter(select);
								if(listaPtp!=null&&listaPtp.size()>0){
									select = new HashMap();
									select.put("NAME", "t_base_ptp");
									select.put("ID_NAME", "BASE_NE_ID");
									select.put("ID_VALUE", listzNe.get(0).get("BASE_NE_ID"));
									select.put("ID_NAME_2", "NAME");
									select.put("ID_VALUE_2", "PTP:/rack"+zptp[1]);
									List<Map> listzPtp = dataCollectMapper.getByParameter(select);
									if(listzPtp!=null&&listzPtp.size()>0){
										select = new HashMap();
										select.put("NAME", "t_base_link");
										select.put("ID_NAME", "A_END_PTP");
										select.put("ID_VALUE", listaPtp.get(0).get("BASE_PTP_ID"));
										select.put("ID_NAME_2", "Z_END_PTP");
										select.put("ID_VALUE_2", listaPtp.get(0).get("BASE_PTP_ID"));
										List<Map> ptpList = dataCollectMapper.getByParameter(select);
										list.addAll(ptpList);
										
										select = new HashMap();
										select.put("NAME", "t_base_link");
										select.put("ID_NAME", "A_END_PTP");
										select.put("ID_VALUE", listzPtp.get(0).get("BASE_PTP_ID"));
										select.put("ID_NAME_2", "Z_END_PTP");
										select.put("ID_VALUE_2", listaPtp.get(0).get("BASE_PTP_ID"));
										List<Map> ptpList_ = dataCollectMapper.getByParameter(select);
										list.addAll(ptpList_);
									}
									
								}
							}
							
						}
						
					}
				}
				
			}
		}
		return list;
	}
	
	// 处理link逻辑
	private static void processCrossConnect(HW.subnetworkConnection.CrossConnect_T cc ,String name,String corbaIp){
		CrossConnectModel model = null;
		Map select = null;
		Map update = null;
		Map insert = null;
		Map delete  = null;
		if(cc!=null){
			model = hwDataToModel.CCDataToModel(cc);
			
			
			Map aEndEms = null;
			Map aEndNe = null;
			
			Map aEndPtp = null;
			Map aEndCtp = null;
			
			Map zEndPtp = null;
			Map zEndCtp = null;
			
			String aEndNeName = null;
			// a端ptp名
			String aEndPtpName = null;
			// a端ctp名
			String aEndCtpName = null;
			// z端ptp名
			String zEndPtpName = null;
			// z端ctp名
			String zEndCtpName = null;
			
			aEndNeName = nameUtil
					.getNeSerialNo(model.getaEndNameList()[0]);

			// a端 ptp名称
			aEndPtpName = nameUtil.decompositionName(nameUtil
					.getPtpNameFromCtpName(model.getaEndNameList()[0]));
			// a端 ctp名称
			aEndCtpName = nameUtil.decompositionCtpName(model.getaEndNameList()[0]);
			
			// z端 ptp名称
			zEndPtpName = nameUtil.decompositionName(nameUtil
					.getPtpNameFromCtpName(model.getzEndNameList()[0]));
			// z端 ctp名称
			zEndCtpName = nameUtil.decompositionCtpName(model.getzEndNameList()[0]);
			
			System.out.println("aEndNeName==="+aEndNeName);
			
			System.out.println("aEndPtpName==="+aEndPtpName);
			System.out.println("aEndCtpName==="+aEndCtpName);
			
			System.out.println("zEndPtpName==="+zEndPtpName);
			System.out.println("zEndCtpName==="+zEndCtpName);

			if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(name)||
						DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(name)){
				if (dataCollectMapper == null) {
					dataCollectMapper = (DataCollectMapper) BeanUtil
							.getBean("dataCollectMapper");
				}	
				//insert  = new HashMap();
				// 获取网管id
				select = new HashMap();
				select.put("NAME", "t_base_ems_connection");
				select.put("ID_NAME", "IP");
				select.put("ID_VALUE", corbaIp);
				List<Map> listEms = dataCollectMapper.getByParameter(select);
				if(listEms!=null&&listEms.size()>0){
					//insert.put("BASE_EMS_CONNECTION_ID", listEms.get(0).get("BASE_EMS_CONNECTION_ID"));
					// 确定交叉连接所在网元
					select = new HashMap();
					select.put("NAME", "t_base_ne");
					select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
					select.put("ID_VALUE", listEms.get(0).get("BASE_EMS_CONNECTION_ID"));
					select.put("ID_NAME_2", "NAME");
					select.put("ID_VALUE_2", aEndNeName);
					List<Map> listNe = dataCollectMapper.getByParameter(select);
					if(listNe!=null&&listNe.size()>0){
						
						// 更改网元状态，变成需要同步
						Map ne = listNe.get(0);
						//insert.put("BASE_NE_ID", ne.get("BASE_NE_ID"));
						ne.put("BASIC_SYNC_STATUS", DataCollectDefine.SYNC_NEED_FLAG);
						dataCollectMapper.updateNeById(ne);
						
						select = new HashMap();
						select.put("NAME", "t_base_ptp");
						select.put("ID_NAME", "BASE_NE_ID");
						select.put("ID_VALUE", ne.get("BASE_NE_ID"));
						select.put("ID_NAME_2", "NAME");
						select.put("ID_VALUE_2", aEndPtpName);
						List<Map> listAPtp = dataCollectMapper.getByParameter(select);
						if(listAPtp!=null&&listAPtp.size()>0){
							Map aPtp = listAPtp.get(0);
							//insert.put("A_END_PTP", aPtp.get("BASE_PTP_ID"));
							
							select = new HashMap();
							select.put("NAME", "t_base_sdh_ctp");
							select.put("ID_NAME", "BASE_PTP_ID");
							select.put("ID_VALUE", aPtp.get("BASE_PTP_ID"));
							select.put("ID_NAME_2", "NAME");
							select.put("ID_VALUE_2", aEndCtpName);
							List<Map> listACtp = dataCollectMapper.getByParameter(select);
							if(listACtp!=null&&listACtp.size()>0){
								Map aCtp = listACtp.get(0);
								//insert.put("A_END_CTP", aCtp.get("BASE_SDH_CTP_ID"));
								
								select = new HashMap();
								select.put("NAME", "t_base_ptp");
								select.put("ID_NAME", "BASE_NE_ID");
								select.put("ID_VALUE", ne.get("BASE_NE_ID"));
								select.put("ID_NAME_2", "NAME");
								select.put("ID_VALUE_2", zEndPtpName);
								List<Map> listZPtp = dataCollectMapper.getByParameter(select);
								if(listZPtp!=null&&listZPtp.size()>0){
									Map zPtp = listZPtp.get(0);
									//insert.put("Z_END_PTP", zPtp.get("BASE_PTP_ID"));
									
									select = new HashMap();
									select.put("NAME", "t_base_sdh_ctp");
									select.put("ID_NAME", "BASE_PTP_ID");
									select.put("ID_VALUE", zPtp.get("BASE_PTP_ID"));
									select.put("ID_NAME_2", "NAME");
									select.put("ID_VALUE_2", zEndCtpName);
									List<Map> listZCtp = dataCollectMapper.getByParameter(select);
									if(listZCtp!=null&&listZCtp.size()>0){
										Map zCtp = listZCtp.get(0);
										// 判断是新增还是删除
										if(DataCollectDefine.COMMON.NT_OBJECT_CREATION.equals(name)){
											List<Map> list = new ArrayList<Map>();
											// 判断是否已存在交叉连接
											select = new HashMap();
											select.put("NAME", "t_base_sdh_crs");
											select.put("ID_NAME", "A_END_CTP");
											select.put("ID_VALUE", aCtp.get("BASE_SDH_CTP_ID"));
											select.put("ID_NAME_2", "Z_END_CTP");
											select.put("ID_VALUE_2", zCtp.get("BASE_SDH_CTP_ID"));
											List<Map> listIsExist = dataCollectMapper.getByParameter(select);
											System.out.println(aCtp.get("BASE_SDH_CTP_ID")+"----"+zCtp.get("BASE_SDH_CTP_ID"));
											if(listIsExist==null||listIsExist.size()<1){
												insert=crossConnectModelToSdhTable(model, listEms.get(0).get("BASE_EMS_CONNECTION_ID"), ne.get("BASE_NE_ID"), aPtp.get("BASE_PTP_ID"), aCtp.get("BASE_SDH_CTP_ID"), zPtp.get("BASE_PTP_ID"), zCtp.get("BASE_SDH_CTP_ID"), aCtp.get("CONNECT_RATE").toString(), false);
												list.add(insert);
											}else{
												System.out.println("fanxiang===2");
											}
											if(model.getDirection()>1){
												// 判断是否已存在交叉连接
												select = new HashMap();
												select.put("NAME", "t_base_sdh_crs");
												select.put("ID_NAME", "Z_END_CTP");
												select.put("ID_VALUE", aCtp.get("BASE_SDH_CTP_ID"));
												select.put("ID_NAME_2", "A_END_CTP");
												select.put("ID_VALUE_2", zCtp.get("BASE_SDH_CTP_ID"));
												List<Map> _listIsExist = dataCollectMapper.getByParameter(select);
												System.out.println("fanxiang===");
												if(_listIsExist==null||_listIsExist.size()<1){
													Map _insert=crossConnectModelToSdhTable(model, listEms.get(0).get("BASE_EMS_CONNECTION_ID"), ne.get("BASE_NE_ID"),  zPtp.get("BASE_PTP_ID"), zCtp.get("BASE_SDH_CTP_ID"),aPtp.get("BASE_PTP_ID"), aCtp.get("BASE_SDH_CTP_ID"), aCtp.get("CONNECT_RATE").toString(), false);
													list.add(_insert);
												}else{
													System.out.println("fanxiang===1");
												}
											}
											
											if(list!=null&&list.size()>0){
												dataCollectMapper.insertSdhCrsBatch(list);
											}
										}else if(DataCollectDefine.COMMON.NT_OBJECT_DELETION.equals(name)){
											System.out.println(aCtp.get("BASE_SDH_CTP_ID")+"----"+zCtp.get("BASE_SDH_CTP_ID"));

											update = new HashMap();
											update.put("NAME", "t_base_sdh_crs");
											update.put("ID_NAME_2", "IS_DEL");
											update.put("ID_VALUE_2",DataCollectDefine.TRUE );
											update.put("ID_NAME", "A_END_CTP");
											update.put("ID_VALUE", aCtp.get("BASE_SDH_CTP_ID"));
											update.put("ID_NAME_", "Z_END_CTP");
											update.put("ID_VALUE_", zCtp.get("BASE_SDH_CTP_ID"));
											dataCollectMapper.updateByParameter(update);
											
											update = new HashMap();
											update.put("NAME", "t_base_sdh_crs");
											update.put("ID_NAME_2", "IS_DEL");
											update.put("ID_VALUE_2",DataCollectDefine.TRUE );
											update.put("ID_NAME", "Z_END_CTP");
											update.put("ID_VALUE", aCtp.get("BASE_SDH_CTP_ID"));
											update.put("ID_NAME_", "A_END_CTP");
											update.put("ID_VALUE_", zCtp.get("BASE_SDH_CTP_ID"));
											dataCollectMapper.updateByParameter(update);
										}
										
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：Sdh Crs信息表对象构建")
	private static Map crossConnectModelToSdhTable(CrossConnectModel model,
			Object emsConnectionId, Object neId, Object aEndPtpId, Object aEndCtpId,
			Object zEndPtpId, Object zEndCtpId, String rate,boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("ACTIVE", model.isActive() ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);
		map.put("CC_TYPE", model.getCcType());
		map.put("CC_NAME", "");
		map.put("DIRECTION", model.getDirection());
		map.put("A_END_PTP", aEndPtpId);
		map.put("A_END_CTP", aEndCtpId);
		map.put("Z_END_PTP", zEndPtpId);
		map.put("Z_END_CTP", zEndCtpId);
		map.put("RATE", rate);
		map.put("IS_IN_CIRCUIT", DataCollectDefine.FALSE);
		map.put("CHANGE_STATE", null);
		//设置默认值为0
		map.put("CIRCUIT_COUNT", 0);
		map.put("IS_VIRTUAL", DataCollectDefine.FALSE);
		map.put("IS_USE_CREATE", null);
		map.put("PARENT_ID", null);

		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_SDH_CRS_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	//获取网管Id
	private static Integer getEmsConnectionId(Map paramter){
		Integer target = Integer.valueOf(paramter.get("BASE_EMS_CONNECTION_ID").toString());
		return target;
	}
	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：Link信息表对象构建")
	private static Map topologicalLinkModelToTable(TopologicalLinkModel model,
			int emsConnectionId, Integer aEndNeId, int aEndPtpId,
			Integer zEndNeId, int zEndPtpId, int type,
			boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		// USER_LABEL用作规范名称
		// map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
		map.put("OWNER", model.getOwner());
		map.put("DIRECTION", model.getDirection());
		map.put("A_EMS_CONNECTION_ID", emsConnectionId);
		map.put("A_NE_ID", aEndNeId);
		map.put("A_END_PTP", aEndPtpId);
		map.put("Z_EMS_CONNECTION_ID", emsConnectionId);
		map.put("Z_NE_ID", zEndNeId);
		map.put("Z_END_PTP", zEndPtpId);
		int linkType = aEndNeId.intValue() == zEndNeId.intValue() ? DataCollectDefine.LINK_TYPE_INTERNAL_FLAG
				: DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG;
		map.put("LINK_TYPE", linkType);
		map.put("CHANGE_STATE", DataCollectDefine.LATEST_ADD);
		// map.put("IS_MANUAL", null);

		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("DISPLAY_NAME"));
			map.put("BASE_LINK_ID", null);
			map.put("IS_MAIN", null);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	
	/**
	 * 从告警的JMS消息或AlarmDataModel中提取用于记入日志的信息
	 * @param alm Map对象或AlarmDataModel对象
	 * @return 含有分行标记的信息字符串
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static String lineSeparator = System.getProperty("line.separator");
	private static String space = "    ";
	
    @SuppressWarnings("unchecked")
	private static void logAlarmMessage(AlarmDataModel alarm) {
    	
		StringBuilder sb = new StringBuilder();

		try {
			// 获取所有属性值
			Field[] fields = alarm.getClass().getDeclaredFields();
			sb.append(lineSeparator);
			for (Field field : fields) {
				PropertyDescriptor pd = null;
				// 获得get方法
				Method get = null;
				// 获得set方法
				Method set = null;
				
				try{
					pd = new PropertyDescriptor(field.getName(),
							alarm.getClass());
					get = pd.getReadMethod();
					set = pd.getWriteMethod();
				}catch(Exception e){
					continue;
				}
				// get属性值
				Object getValue = get.invoke(alarm, new Object[] {});
				
				sb.append(space).append(field.getName()).append(" : ");
				
				if(getValue == null){
					sb.append("null").append(lineSeparator);
				}else{
					if (NameAndStringValue_T[].class.isInstance(getValue)) {
						NameAndStringValue_T[] temp = (NameAndStringValue_T[]) getValue;

						for (NameAndStringValue_T tempName : temp) {
							sb.append(tempName.name);
							sb.append("=");
							sb.append(tempName.value);
							sb.append("||");
						}
						sb.append(lineSeparator);
					}else{
						sb.append(getValue.toString()).append(lineSeparator);
					}
				}
			}
		} catch (Exception e) {
			logger.error("告警信息打印错误："+e.getMessage());
		}
		logger.info(sb.toString());
	}
	
	private static void writeNotify(Object notification, int factory,String corbaIp){
		if (dataCollectMapper == null) {
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		// 获取ems信息
		Map connection = dataCollectMapper.selectEmsConnectionByIP(
				corbaIp, DataCollectDefine.FALSE);
		com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.VEMSSession.
		writeNotify(corbaIp,factory,connection,notification);
	}
	
}
