package com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba;

import java.util.Date;

import ZTE_U31.CosEventComm.Disconnected;
import ZTE_U31.CosNotification.StructuredEvent;
import ZTE_U31.CosNotification._EventType;
import ZTE_U31.CosNotifyComm.InvalidEventType;
import ZTE_U31.CosNotifyComm.StructuredPushConsumerPOA;

import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.service.ConsumerUtil;
public class ZTEU31ConsumerImpl extends StructuredPushConsumerPOA {

	private String corbaIp;
	private String encode;

	public ZTEU31ConsumerImpl(String corbaIp,String encode) {
		this.corbaIp = corbaIp;
		this.encode = encode;
	}
	
	public void push_structured_event(StructuredEvent notification)
			throws Disconnected {
		//消息处理
		ConsumerUtil.handleNotification(notification, DataCollectDefine.FACTORY_ZTE_FLAG, corbaIp, encode);
	}

	public void disconnect_structured_push_consumer() {
		System.out.println(new Date() + " 【" + corbaIp + "】disconnect_structured_push_consumer");
	}

	public void offer_change(_EventType[] added, _EventType[] removed)
			throws InvalidEventType {
		System.out.println(new Date() + " 【" + corbaIp + "】offer_change");
	}

}
