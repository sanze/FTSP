package com.fujitsu.serviceImpl.ZTEU31Corba;

import java.util.Date;
//import java.util.Timer;

import ZTE_U31.CosEventComm.Disconnected;
import ZTE_U31.CosNotification.StructuredEvent;
import ZTE_U31.CosNotification._EventType;
import ZTE_U31.CosNotifyComm.InvalidEventType;
import ZTE_U31.CosNotifyComm.StructuredPushConsumerPOA;

import com.fujitsu.common.DataCollectDefine;
//import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
//import com.fujitsu./*manager.dataCollectManager.*/service.EMSCollectService;
//import com.fujitsu./*manager.dataCollectManager.*/service.AutoCheckConnection;
public class ZTEU31ConsumerImpl extends StructuredPushConsumerPOA {

	private String corbaIp;

//	private static ZTEU31DataToModel zteU31DataToModel = new ZTEU31DataToModel();

	public ZTEU31ConsumerImpl(String corbaIp,String encode) {
		this.corbaIp = corbaIp;
//		zteU31DataToModel = new ZTEU31DataToModel(encode);
	}
	
	public void push_structured_event(StructuredEvent notification)
			throws Disconnected {
		String head = notification.header.fixed_header.event_type.type_name;
		System.out.println(new Date() + " 【" + corbaIp + "】 " + head);
		main.GetAndPrintData_Stub.ExportNotification(notification);
		// 心跳推送通知
		if (DataCollectDefine.COMMON.NT_HEARTBEAT.equals(head)) {

			// 更新心跳接收时间
//			EMSCollectService.heartBeatReceiveTime.put(corbaIp, new Date());
//		
//			//如果corbaIP为空
//			if (EMSCollectService.heartBeatTimerMap.get(corbaIp) == null) {
//				timer = new Timer();
//				// 1分钟检查一次，通知服务是否可用
//				timer.schedule(new AutoCheckConnection(
//						AutoCheckConnection.CHECK_HEART_BEATING, corbaIp), 0,
//						AutoCheckConnection.SCHEDULE_TIME * 1000);
//				EMSCollectService.heartBeatTimerMap.put(corbaIp, timer);
//			}
		}
		//推送文件传送完成标志，上传文件传送完成标志文件
		else if (DataCollectDefine.COMMON.NT_FILE_TRANSFER_STATUS.equals(head)) {
			
		}
		// 告警推送通知
		else if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
			//模式转换
//			AlarmDataModel model = zteU31DataToModel.AlarmDataToModel(notification);
			
			//告警推送
			/*缺少jms接口推送通知*/		
		}
}

	public void disconnect_structured_push_consumer() {

	}

	public void offer_change(_EventType[] added, _EventType[] removed)
			throws InvalidEventType {
	}
	
/*	
	//打印告警信息
	private void printAlarm(String head, StructuredEvent notification) {
		if (DataCollectDefine.NT_ALARM.equals(head)) {
			// varible
			NameAndStringValue_T[] objectName;
			ObjectType_T objectType;
			PerceivedSeverity_T perceivedSeverity;
			ServiceAffecting_T serviceAffecting;
			NameAndStringValue_T[] affectedTPList;
			NameAndStringValue_T[] EventType;

			System.out.println("*******************************HW  NT_ALARM  ******************************");
			// notificationId
			System.out.println("notificationId is:"
					+ notification.filterable_data[0].value.extract_string());
			// name
			objectName = NamingAttributes_THelper
					.read(notification.filterable_data[1].value
							.create_input_stream());
			for (int j = 0; j < objectName.length; j++) {
				System.out.println("objectName.name is:" + objectName[j].name);
				System.out.println("objectName.value is:" + objectName[j].value);
			}
			// nativeEMSName
			String nativeEMSName = notification.filterable_data[2].value.extract_string();
			try {
				nativeEMSName = new String(nativeEMSName.getBytes("ISO-8859-1"), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("nativeEMSName is:"
					+ nativeEMSName);
			// nativeProbableCause
			System.out.println("nativeProbableCause is:"
					+ notification.filterable_data[3].value.extract_string());
			// objectType
			objectType = ObjectType_THelper
					.read(notification.filterable_data[4].value
							.create_input_stream());
			System.out.println("objectType is:" + objectType.value());
			// emsTime
			System.out.println("emsTime is:"
					+ notification.filterable_data[5].value.extract_string());
			// neTime
			System.out.println("neTime is:"
					+ notification.filterable_data[6].value.extract_string());
			// isClearable
			System.out.println("isClearable is:"
					+ notification.filterable_data[7].value.extract_boolean());
			// layerRate
			System.out.println("layerRate is:"
					+ notification.filterable_data[8].value.extract_short());
			// probableCause
			System.out.println("probableCause is:"
					+ notification.filterable_data[9].value.extract_string());
			// probableCauseQualifier
			System.out.println("probableCauseQualifier is:"
					+ notification.filterable_data[10].value.extract_string());
			// perceivedSeverity
			perceivedSeverity = PerceivedSeverity_THelper
					.read(notification.filterable_data[11].value
							.create_input_stream());
			System.out.println("perceivedSeverity is:"
					+ perceivedSeverity.value());
			// serviceAffecting
//			System.out.println();
			serviceAffecting = ServiceAffecting_THelper
					.read(notification.filterable_data[12].value
							.create_input_stream());
			System.out.println("serviceAffecting is:"
					+ serviceAffecting.value());
			// affectedTPList
//			System.out.println();
			affectedTPList = NamingAttributes_THelper
					.read(notification.filterable_data[13].value
							.create_input_stream());
			for (int j = 0; j < affectedTPList.length; j++) {
				System.out.println("affectedTPList is:"
						+ affectedTPList[j].value);
			}
			// additionalInfo
			System.out.println("additionalInfo is:"
					+ notification.filterable_data[14].value.extract_string());
			// EventType
			EventType = NamingAttributes_THelper
					.read(notification.filterable_data[15].value
							.create_input_stream());
			for (int j = 0; j < EventType.length; j++) {
				try {
					System.out.println("EventType is:" + new String(EventType[j].value.getBytes("ISO-8859-1"), "GBK"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			// objectTypeQualifier
			System.out.println("objectTypeQualifier is:"
					+ notification.filterable_data[16].value.extract_string());
			System.out.println();
		}

		if (DataCollectDefine.NT_TCA.equals(head)) {
			// varible
			NameAndStringValue_T[] objectName;
			ObjectType_T objectType;
			PerceivedSeverity_T perceivedSeverity;
			PMThresholdType_T thresholdType;

			System.out
					.println("*******************************HW  NT_TCA  ******************************");
			// notificationId
			System.out.println();
			System.out.println("notificationId is:"
					+ notification.filterable_data[0].value.extract_string());
			// name
			objectName = NamingAttributes_THelper
					.read(notification.filterable_data[1].value
							.create_input_stream());
			for (int j = 0; j < objectName.length; j++) {
				System.out.println("name.name is:" + objectName[j].name);
				System.out.println("name.value is:" + objectName[j].value);
			}
			// nativeEMSName
			System.out.println("nativeEMSName is:"
					+ notification.filterable_data[2].value.extract_string());
			// objectType
			objectType = ObjectType_THelper
					.read(notification.filterable_data[3].value
							.create_input_stream());
			System.out.println("objectType is:" + (objectType.value()));
			// emsTime
			System.out.println("emsTime is:"
					+ notification.filterable_data[4].value.extract_string());
			// neTime
			System.out.println("neTime is:"
					+ notification.filterable_data[5].value.extract_string());
			// isClearable
			System.out.println("isClearable is:"
					+ notification.filterable_data[6].value.extract_boolean());
			// perceivedSeverity
			perceivedSeverity = PerceivedSeverity_THelper
					.read(notification.filterable_data[7].value
							.create_input_stream());
			System.out.println("perceivedSeverity is:"
					+ perceivedSeverity.value());
			// layerRate
			System.out.println("layerRate is:"
					+ notification.filterable_data[8].value.extract_short());
			// granularity
			System.out.println("granularity is:"
					+ notification.filterable_data[9].value.extract_string());
			// pmParameterName
			System.out.println("pmParameterName is:"
					+ notification.filterable_data[10].value.extract_string());
			// pmLocation
			System.out.println("pmLocation is:"
					+ notification.filterable_data[11].value.extract_string());
			// thresholdType
			thresholdType = PMThresholdType_THelper
					.read(notification.filterable_data[12].value
							.create_input_stream());
			System.out.println("thresholdType is:" + thresholdType.value());
			// value
			System.out.println("value is:"
					+ notification.filterable_data[13].value.extract_float());
			// unit
			System.out.println("unit is:"
					+ notification.filterable_data[14].value.extract_string());
			// additionalInfo
			objectName = NamingAttributes_THelper
					.read(notification.filterable_data[15].value
							.create_input_stream());
			for (int j = 0; j < objectName.length; j++) {
				System.out.println("additionalInfo is:" + objectName[j].value);
			}
		}
	}
*/
	public static void main(String args[]) {

	}
}
