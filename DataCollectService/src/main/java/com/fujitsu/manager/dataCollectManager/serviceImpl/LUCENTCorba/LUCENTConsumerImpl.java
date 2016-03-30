package com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba;

//import java.util.ArrayList;
import java.util.Date;

import LUCENT.CosEventComm.Disconnected;
import LUCENT.CosNotification.EventType;
import LUCENT.CosNotification.StructuredEvent;
import LUCENT.CosNotifyComm.InvalidEventType;
import LUCENT.CosNotifyComm.StructuredPushConsumerPOA;

import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.service.ConsumerUtil;

public class LUCENTConsumerImpl extends StructuredPushConsumerPOA{

	private String corbaIp;
	private String  encode;

	public LUCENTConsumerImpl(String corbaIp,String encode) {
		this.corbaIp = corbaIp;
		this.encode = encode;
	}

	public void push_structured_event(StructuredEvent notification)
			throws Disconnected {
		//消息处理
		ConsumerUtil.handleNotification(notification, DataCollectDefine.FACTORY_LUCENT_FLAG, corbaIp, encode);
	}
		
	public void disconnect_structured_push_consumer() {
		System.out.println(new Date() + " 【" + corbaIp + "】disconnect_structured_push_consumer");
	}
	
	public void offer_change(EventType[] added, EventType[] removed)
		throws InvalidEventType {
		System.out.println(new Date() + " 【" + corbaIp + "】offer_change");
	}

}
