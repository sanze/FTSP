package com.fujitsu.serviceImpl.LUCENTCorba;

//import java.util.ArrayList;
import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;

import LUCENT.CosNotification.EventType;
import LUCENT.CosEventComm.Disconnected;
import LUCENT.CosNotification.StructuredEvent;
import LUCENT.CosNotifyComm.InvalidEventType;
import LUCENT.CosNotifyComm.StructuredPushConsumerPOA;

//import com.fujitsu.IService.IDataCollectService;
//import com.fujitsu.IService.IFaultManagerService;
import com.fujitsu.common.DataCollectDefine;
//import com.fujitsu.dao.mysql.DataCollectMapper;
//import com.fujitsu.handler.ExceptionHandler;
//import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
//import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
//import com.fujitsu.manager.dataCollectManager.service.AutoCheckConnection;
//import com.fujitsu.util.BeanUtil;

public class LUCENTConsumerImpl extends StructuredPushConsumerPOA{

	private String corbaIp;
//	private static IDataCollectService dataCollectService;
//	private static IFaultManagerService faultManagerService;
//	private static DataCollectMapper dataCollectMapper;
//	private static List<AlarmDataModel> alarmList = new ArrayList<AlarmDataModel>();

//	private LUCENTDataToModel lucentDataToModel;

	public LUCENTConsumerImpl(String corbaIp,String encode) {
		this.corbaIp = corbaIp;
//		lucentDataToModel=new LUCENTDataToModel(encode);
	}

	public void push_structured_event(StructuredEvent notification)
			throws Disconnected {

		String head = notification.header.fixed_header.event_type.type_name;
		//归一化消息名称
		head = DataCollectDefine.COMMON.getEventTypeName(head);
		System.out.println(new Date() + " 【" + corbaIp + "】 " + head);
		main.GetAndPrintData_Stub.ExportNotification(notification);
		// 心跳推送通知
		if (DataCollectDefine.COMMON.NT_HEARTBEAT.equals(head)) {

			// 更新心跳接收时间
//			AutoCheckConnection.updateReceiveTime(corbaIp,AutoCheckConnection.CHECK_HEART_BEATING);
		}
		
		// 告警推送通知
		else if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
			/*if (faultManagerService == null) {
				faultManagerService = (IFaultManagerService) BeanUtil
						.getBean("faultManagerService");
			}
			try {
				AlarmDataModel model = lucentDataToModel
						.AlarmDataToModel(notification);

				if (dataCollectMapper == null) {
					dataCollectMapper = (DataCollectMapper) BeanUtil
							.getBean("dataCollectMapper");
				}
				// 获取ems信息
				Map connection = dataCollectMapper.selectEmsConnectionByIP(
						corbaIp, DataCollectDefine.FALSE);

				model.setEmsId(Integer.valueOf(connection.get(
						"BASE_EMS_CONNECTION_ID").toString()));
				model.setFactory(DataCollectDefine.FACTORY_LUCENT_FLAG);

				alarmList.clear();
				alarmList.add(model);
				// 告警模块入库
				faultManagerService.alarmDataToMongodb(alarmList, Integer
						.valueOf(connection.get("BASE_EMS_CONNECTION_ID")
								.toString()), null,
						DataCollectDefine.ALARM_TO_DB_TYPE_PUSH);

			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}*/
		
		}
		
		// 推送文件传送完成标志，上传文件传送完成标志文件
		else if (DataCollectDefine.COMMON.NT_FILE_TRANSFER_STATUS.equals(head)) {

		}
		
		// 状态推送通知
		else if (DataCollectDefine.COMMON.NT_STATE_CHANGE.equals(head)) {
			/*if (dataCollectService == null) {
				dataCollectService = (IDataCollectService) BeanUtil
						.getBean("dataCollectService");
			}
			try {
				StateDataModel model=lucentDataToModel.StateDataToModel(notification);
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
			}*/
		}
	}
		
	public void disconnect_structured_push_consumer() {
		System.out.println("LUCENTConsumerImpl--disconnect_structured_push_consumer");
	
	}
	
	public void offer_change(EventType[] added, EventType[] removed)
		throws InvalidEventType {
	}

}
