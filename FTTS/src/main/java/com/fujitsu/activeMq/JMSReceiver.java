package com.fujitsu.activeMq;

//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.listener.SessionAwareMessageListener;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.planManager.TestManagement;
import com.fujitsu.manager.planManager.TriggerTestManager;
import com.fujitsu.model.MessageModel;

/**
 * 
 * 消息接收监听器。
 */
public class JMSReceiver implements SessionAwareMessageListener {
//	private ISysInterfaceManageService interfaceManageService;
//	private ICutoverManagerService cutoverManagerService;
	@Resource
	private TestManagement testManagement;
	
	@Resource
	private IAlarmManagementService alarmManagementService;
	@Resource
	private TriggerTestManager triger;
	
	public void onMessage(Message message, Session session) throws JMSException {

		System.out.println("JMSReceiver onMessage");
		
		try {
			// 1.接收报文
			MessageModel messageModel = (MessageModel) ((ObjectMessage) message)
					.getObject();
			
			// 判断消息类型
			switch (messageModel.getMessageType()) {
			case CommonDefine.MESSAGE_TYPE_ALARM:
				//消息处理
				Map alarmMap = (Map) messageModel.getMessage();
				// 告警触发测试处理
//				TriggerTestManager triger = new TriggerTestManager();
				triger.call(alarmMap);
        		break;
			case MessageCodeDefine.ALARM_JMS_CODE :
				alarmManagementService.handleRTUPushAlarm((Map)messageModel.getMessage());
				break;
//			case CommonDefine.MESSAGE_TYPE_ALARM_COM:
				//告警推送
//				JSONObject jsonObject = JSONObject.fromObject(messageModel.getMessage().toString());
//				// 将JSON对象转成Map对象
//				Map<String, Object> paramMap = new HashMap<String, Object>();
//				paramMap = (Map<String, Object>) jsonObject;
//				handlerAlarmToWS(paramMap);
//				break;
			}

			// 2.解析报文，建议此处使用工具类解析

			// 3.调用接口，处理业务，建议此处使用“抽象工厂模式”调用内部接口，以使代码符合“开-闭原则”

			// 4.发送返回消息(可省略)
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
}
