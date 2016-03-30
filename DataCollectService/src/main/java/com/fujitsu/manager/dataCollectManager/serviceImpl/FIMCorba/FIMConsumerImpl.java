package com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba;

import java.util.Date;

import FENGHUO.CosEventComm.Disconnected;
import FENGHUO.CosNotification.StructuredEvent;
import FENGHUO.CosNotification._EventType;
import FENGHUO.CosNotifyComm.InvalidEventType;
import FENGHUO.CosNotifyComm.StructuredPushConsumerPOA;

import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.service.ConsumerUtil;

public class FIMConsumerImpl extends StructuredPushConsumerPOA {
	
	private String corbaIp;
	private String encode;
	
	public FIMConsumerImpl(String corbaIp,String encode) {
		this.corbaIp = corbaIp;
		this.encode = encode;
	}

	public void push_structured_event(StructuredEvent notification)
			throws Disconnected {
		//消息处理
		ConsumerUtil.handleNotification(notification, DataCollectDefine.FACTORY_FIBERHOME_FLAG, corbaIp, encode);
	} // push_structured_event

	public void disconnect_structured_push_consumer() {
		System.out.println(new Date() + " 【" + corbaIp + "】disconnect_structured_push_consumer");
	} // disconnect_structured_push_consumer

	public void offer_change(_EventType[] added, _EventType[] removed)
			throws InvalidEventType {
		System.out.println(new Date() + " 【" + corbaIp + "】offer_change");
	} // offer_change

}
