package com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba;

import java.util.Date;

import HW.CosEventComm.Disconnected;
import HW.CosNotification.StructuredEvent;
import HW.CosNotification._EventType;
import HW.CosNotifyComm.InvalidEventType;
import HW.CosNotifyComm.StructuredPushConsumerPOA;

import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.service.ConsumerUtil;

public class HWConsumerImpl extends StructuredPushConsumerPOA {


	private String corbaIp;
	private String encode;

	public HWConsumerImpl(
			String corbaIp,String encode) {
		this.corbaIp = corbaIp;
		this.encode = encode;
	}

	public void push_structured_event(StructuredEvent notification)
			throws Disconnected {

		//消息处理
		ConsumerUtil.handleNotification(notification, DataCollectDefine.FACTORY_HW_FLAG, corbaIp, encode);

	}

	public void disconnect_structured_push_consumer() {
		System.out.println(new Date() + " 【" + corbaIp + "】disconnect_structured_push_consumer");
	}

	public void offer_change(_EventType[] added, _EventType[] removed)
			throws InvalidEventType {
		System.out.println(new Date() + " 【" + corbaIp + "】offer_change");
	}
	
	
}
