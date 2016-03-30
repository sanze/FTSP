package com.fujitsu.manager.dataCollectManager.service;

import globaldefs.NameAndStringValue_T;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.DataCollectDefine.COMMON;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.LayeredParametersModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionSwtichDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TCADataModel;
import com.fujitsu.model.PtpDomainModel;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.NameAndStringValueUtil;

/**
 * @author xuxiaojun
 * 
 */
public abstract class DataToModel{
	
	protected String encode;
	
	protected static List<PtpDomainModel> ptpDomainList = null;

	protected NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();
	
	public DataToModel(String encode){
		this.encode = encode;
	}
	
	//告警数据转换
	public abstract AlarmDataModel AlarmDataToModel(Object event);
	//TCA数据转换
	public abstract TCADataModel TCADataToModel(Object event);
	
	//TCA数据转换 中兴专用 当告警处理
	public abstract AlarmDataModel TCADataToAlarmModel(Object event);
	
	//保护倒换数据转换
	public abstract ProtectionSwtichDataModel ProtectionSwitchDataToModel(Object event);
	//状态更改数据模型转换
	public abstract StateDataModel StateDataToModel(Object event);
	
	// 获取ptp速率字符串
	protected String getLayRateSrting(
			List<LayeredParametersModel> transmissionParams) {
		String layRateSrting = "";
		StringBuilder tempString = new StringBuilder();
		for (int i = 0; i < transmissionParams.size(); i++) {
			tempString.append(String.valueOf(transmissionParams.get(i)
					.getLayer()));
			if (i != transmissionParams.size() - 1) {
				tempString.append(":");
			}
		}
		layRateSrting = tempString.toString();
		return layRateSrting;
	}
	
	// 获取可接受板卡字符串
	protected String getAcceptableEquipmentTypeSrting(
				String[] acceptableEquipmentTypeList) {
			String acceptableEquipmentTypeSrting = "";
			StringBuilder tempString = new StringBuilder();
			for (int i = 0; i < acceptableEquipmentTypeList.length; i++) {
				tempString.append(acceptableEquipmentTypeList[i]);
				if (i != acceptableEquipmentTypeList.length - 1) {
					tempString.append(":");
				}
			}
			acceptableEquipmentTypeSrting = tempString.toString();
			return acceptableEquipmentTypeSrting;
		}
	
	// 将PM Location字符串转换成数字
	protected int getPmLocationFlag(String pmLocation,int factory) {
		int locFlag = COMMON.PM_LOCATION_NA_FLAG;
		if(pmLocation!=null){
			switch(factory){
			case DataCollectDefine.FACTORY_ZTE_FLAG:
				if (pmLocation.equals(DataCollectDefine.ZTE.ZTE_PM_LOCATION_NEAR_END_RX)) {
					locFlag = COMMON.PM_LOCATION_NEAR_END_RX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.ZTE.ZTE_PM_LOCATION_FAR_END_RX)) {
					locFlag = COMMON.PM_LOCATION_FAR_END_RX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.ZTE.ZTE_PM_LOCATION_NEAR_END_TX)) {
					locFlag = COMMON.PM_LOCATION_NEAR_END_TX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.ZTE.ZTE_PM_LOCATION_FAR_END_TX)) {
					locFlag = COMMON.PM_LOCATION_FAR_END_TX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.ZTE.ZTE_PM_LOCATION_NA)) {
					locFlag = COMMON.PM_LOCATION_NA_FLAG;
				}
				break;
			case DataCollectDefine.FACTORY_FUJITSU_FLAG:
				
				break;
			case DataCollectDefine.FACTORY_HW_FLAG:
			case DataCollectDefine.FACTORY_LUCENT_FLAG:
			case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			case DataCollectDefine.FACTORY_ALU_FLAG:
			default:
				if (pmLocation.equals(DataCollectDefine.COMMON.PM_LOCATION_NEAR_END_RX)) {
					locFlag = COMMON.PM_LOCATION_NEAR_END_RX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.COMMON.PM_LOCATION_FAR_END_RX)) {
					locFlag = COMMON.PM_LOCATION_FAR_END_RX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.COMMON.PM_LOCATION_NEAR_END_TX)) {
					locFlag = COMMON.PM_LOCATION_NEAR_END_TX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.COMMON.PM_LOCATION_FAR_END_TX)) {
					locFlag = COMMON.PM_LOCATION_FAR_END_TX_FLAG;
				} else if (pmLocation.equals(DataCollectDefine.COMMON.PM_LOCATION_NA)) {
					locFlag = COMMON.PM_LOCATION_NA_FLAG;
				}
				break;
			}
		}
		return locFlag;
	}
	
	// 将时间转换成统一格式
	protected Date DateStrFormatForPM(String time,int factory) throws CommonException {
		if(time==null||time.isEmpty())
			return null;
		Date timeDisplay = null;
		SimpleDateFormat retrievalTimeFormat = CommonUtil
				.getDateFormatter((DataCollectDefine.RETRIEVAL_TIME_FORMAT));
		boolean isUtcTime = false;
		//性能时间处理
		switch(factory){
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
		case DataCollectDefine.FACTORY_HW_FLAG:
		default:
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			// 贝尔历史性能文件中的日期格式为"2015/04/19 08:00"
			if (time.contains("/")) {
				time = time.trim();
				time = time.replaceAll("/", "");
				time = time.replaceAll(":", "");
				time = time + "00";
			}
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			String _15MIN = "0000000000";
			String _24H   = "00000000";
			if(time.startsWith(_24H)){
				int start=_24H.length();
				int hour=Integer.valueOf(time.substring(start,start+2));
				start+=2;
				int minute=Integer.valueOf(time.substring(start,start+2));
				start+=2;
				int second=Integer.valueOf(time.substring(start,start+2));

				Calendar curCal=Calendar.getInstance();
				curCal.add(Calendar.HOUR_OF_DAY, -hour);
				curCal.add(Calendar.MINUTE, -minute);
				curCal.add(Calendar.SECOND, -second);
				minute = curCal.get(Calendar.MINUTE);
				switch (minute/15) {
				case 0:
					curCal.set(Calendar.MINUTE, 0);
					break;
				case 1:
					curCal.set(Calendar.MINUTE, 15);
					break;
				case 2:
					curCal.set(Calendar.MINUTE, 30);
					break;
				case 3:
					curCal.set(Calendar.MINUTE, 45);
					break;
				default:
					curCal.set(Calendar.MINUTE, 0);
					break;
				}
				// PM开始时间的秒固定设为00
				curCal.set(Calendar.SECOND, 0);
				// 24小时PM开始时间，小时/分钟固定设为00
				if (!time.startsWith(_15MIN)) {
					if (curCal.get(Calendar.HOUR_OF_DAY) > 18) {
						curCal.add(Calendar.DAY_OF_MONTH, 1);
					}
					curCal.set(Calendar.HOUR_OF_DAY, 0);
					curCal.set(Calendar.MINUTE, 0);
				}
				time=retrievalTimeFormat.format(curCal.getTime());
			}
			break;
		}
		if (!time.isEmpty()) {
			if(time.toUpperCase().contains("Z")||time.contains("+")){
				//UTC时间，需要加8小时
				isUtcTime = true;
			}
			if(time.contains(".")){
				//需要转义"."
				time = time.split("\\.")[0];
			}
			try {
				Date specifiedDay = retrievalTimeFormat.parse(time);
				//时区转换+8小时
				if(isUtcTime){
					Calendar c = Calendar.getInstance();
					c.setTime(specifiedDay);
					int hour = c.get(Calendar.HOUR);
					c.set(Calendar.HOUR, hour + 8);
					specifiedDay = c.getTime();
				}
				timeDisplay = specifiedDay;
			}
			catch (ParseException e) {
				System.out.println(time);
				throw new CommonException(e, MessageCodeDefine.CORBA_PARSE_EXCEPTION);
			}
		}
		return timeDisplay;
	}

	
	/**
	 * 将采集数据中的日期格式统一转换成标准时间格式
	 * @param dateStr
	 * @return 标准时间格式字符串 （yyyy-MM-dd HH:mm:ss）
	 * @throws CommonException 
	 */
	protected String DateStrFormatForAlarm(String dateStr) throws CommonException {
		SimpleDateFormat commonFormat = CommonUtil.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
		SimpleDateFormat retrievalTimeFormat = CommonUtil.getDateFormatter(DataCollectDefine.RETRIEVAL_TIME_FORMAT);
		String time = "";
		boolean isUtcTime = false;
//		//设置timeZone
//		switch(factory){
//		case DataCollectDefine.FACTORY_HW_FLAG:
//			break;
//		case DataCollectDefine.FACTORY_ZTE_FLAG:
//			break;
//		case DataCollectDefine.FACTORY_LUCENT_FLAG:
//			timeZone="+0800";
//			break;
//		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
//			break;
//		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
//			break;
//		}
		if (!dateStr.isEmpty()) {
			if(dateStr.toUpperCase().contains("Z")||dateStr.contains("+")){
				//UTC时间，需要加8小时
				isUtcTime = true;
			}
			/* 贝尔告警的时间格式为：20150617094236.0+0800，它是非UTC时间 */
			if (dateStr.matches("[0-9]{14}[.][0][+][0-9]{4}")) {
				isUtcTime = false;
			}
			if(dateStr.contains(".")){
				//需要转义"."
				dateStr = dateStr.split("\\.")[0];
			}
			try {
				Date specifiedDay = retrievalTimeFormat.parse(dateStr);
				//时区转换+8小时
				if(isUtcTime){
					Calendar c = Calendar.getInstance();
					c.setTime(specifiedDay);
					int hour = c.get(Calendar.HOUR);
					c.set(Calendar.HOUR, hour + 8);
					specifiedDay = c.getTime();
				}
				time = commonFormat.format(specifiedDay);
			}
			catch (ParseException e) {
				System.out.println(dateStr);
				throw new CommonException(e, MessageCodeDefine.CORBA_PARSE_EXCEPTION);
			}
		}
		return time;
	}
	
	//EventType -->alarmType
	protected int getAlarmType(String EventType,int factory) {
		/*"communicationsAlarm"
		"qualityofServiceAlarm"
		"equipmentAlarm"
		"processingErrorAlarm"
		"environmentalAlarm"
		"securityAlarm"*/
		int alarmType=-1;
		if("communicationsAlarm".equals(EventType)){
			alarmType = ZTE_U31.alarmMgr.AlarmType_T._AT_COMMUNICATIONSALARM;
		}else if("qualityofServiceAlarm".equals(EventType)){
			alarmType = ZTE_U31.alarmMgr.AlarmType_T._AT_QOSALARM;
		}else if("equipmentAlarm".equals(EventType)){
			alarmType = ZTE_U31.alarmMgr.AlarmType_T._AT_EQUIPMENTALARM;
		}else if("processingErrorAlarm".equals(EventType)){
			alarmType = ZTE_U31.alarmMgr.AlarmType_T._AT_PROCESSINGERRORALARM;
		}else if("environmentalAlarm".equals(EventType)){
			alarmType = ZTE_U31.alarmMgr.AlarmType_T._AT_ENVIRONMENTALALARM;
		}else if("securityAlarm".equals(EventType)){
			alarmType = ZTE_U31.alarmMgr.AlarmType_T._AT_NETWORKSECURITYALARM;
		}else if("connectionAlarm".equals(EventType)){
			alarmType = ZTE_U31.alarmMgr.AlarmType_T._AT_CONNECTIONALARM;
		}
		return alarmType;
	}
	
	//objectName -->objectType
	protected int getObjectTypeByName(NameAndStringValue_T[] objectName) {
		int objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EMS;
		if(objectName!=null&&objectName.length>0){
			String lastName=objectName[objectName.length-1].name;
			if(DataCollectDefine.COMMON.EMS.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EMS;
			}else if(DataCollectDefine.COMMON.MANAGED_ELEMENT.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MANAGED_ELEMENT;
			}else if(DataCollectDefine.COMMON.EQUIPMENT_HOLDER.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT_HOLDER;
			}else if(DataCollectDefine.COMMON.EQUIPMENT.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT;
			}else if(DataCollectDefine.COMMON.PTP.equals(lastName)||
					DataCollectDefine.COMMON.FTP.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT;
			}else if(DataCollectDefine.COMMON.MULTI_LAYER_SUBNETWORK.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MULTILAYER_SUBNETWORK;
			}else if(DataCollectDefine.COMMON.TOPOLOGICAL_LINK.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_TOPOLOGICAL_LINK;
			}else if(DataCollectDefine.COMMON.SUBNETWORK_CONNECTION.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_SUBNETWORK_CONNECTION;
			}else if(DataCollectDefine.COMMON.WDMPG.equals(lastName)||
					DataCollectDefine.COMMON.PGP.equals(lastName)||
					DataCollectDefine.COMMON.EPGP.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PROTECTION_GROUP;
			}else if(DataCollectDefine.COMMON.AID.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_AID;
			}else if(DataCollectDefine.COMMON.TP_POOL.equals(lastName)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_TERMINATION_POINT_POOL;
			}//中兴存在NonWorkCtp情况，采用包含形式
			else if(lastName.toUpperCase().contains(DataCollectDefine.COMMON.CTP)){
				objectType=DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT;
			}else{
				System.out.println("[WARN]: getObjectTypeByName - unknown type, set to ALARM_OBJECT_TYPE_EMS.");
			}
		}
		return objectType;
	}
}
