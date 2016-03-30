package com.fujitsu.serviceImpl.FIMCorba;

import java.util.Date;

import FENGHUO.CosEventComm.Disconnected;
import FENGHUO.CosNotification.StructuredEvent;
import FENGHUO.CosNotification._EventType;
import FENGHUO.CosNotifyComm.InvalidEventType;
import FENGHUO.CosNotifyComm.StructuredPushConsumerPOA;

import com.fujitsu.common.DataCollectDefine;

public class FIMConsumerImpl extends StructuredPushConsumerPOA {
	
	private String corbaIp;

	
	public FIMConsumerImpl(String corbaIp,String encode) {
		this.corbaIp = corbaIp;
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

//			if (EMSCollectService.heartBeatTimerMap.get(corbaIp) == null) {
//				timer = new Timer();
//				// 1分钟巡检一次，通知服务是否可用
//				timer.schedule(new AutoCheckConnection(
//						AutoCheckConnection.CHECK_HEART_BEATING, corbaIp), 0,
//						AutoCheckConnection.SCHEDULE_TIME * 1000);
//				EMSCollectService.heartBeatTimerMap.put(corbaIp, timer);
//			}
		}
		
		// 告警推送通知
		else if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
//			AlarmDataModel model = fimDataToModel.AlarmDataToModel(notification);
			// FIXME 缺少jms接口推送通知
		}
		
		//推送文件传送完成标志，上传文件传送完成标志文件
		else if (DataCollectDefine.COMMON.NT_FILE_TRANSFER_STATUS.equals(head)) {
			
		}

	} // push_structured_event

	public void disconnect_structured_push_consumer() {
		System.out.println("disconnect_structured_push_consumer");

	} // disconnect_structured_push_consumer

	public void offer_change(_EventType[] added, _EventType[] removed)
			throws InvalidEventType {

	} // offer_change

}
