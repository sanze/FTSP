package com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba;

import java.util.Date;

import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification.EventType;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.SequencePushConsumerPOA;

import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.manager.dataCollectManager.service.ConsumerUtil;

public class ALUConsumerImpl extends SequencePushConsumerPOA {
	
	private String corbaIp;
	private String encode;
	
	public ALUConsumerImpl(String corbaIp,String encode) {
		this.corbaIp = corbaIp;
		this.encode = encode;
	}

	public void push_structured_events(StructuredEvent[] notifications)
			throws Disconnected {
		//消息处理
		for(StructuredEvent notification:notifications){
			ConsumerUtil.handleNotification(notification, DataCollectDefine.FACTORY_ALU_FLAG, corbaIp, encode);
		}
	} // push_structured_event

	public void disconnect_sequence_push_consumer() {
		System.out.println(new Date() + " 【" + corbaIp + "】 disconnect_structured_push_consumer");

	} // disconnect_structured_push_consumer

	public void offer_change(EventType[] added, EventType[] removed)
			throws InvalidEventType {
		System.out.println(new Date() + " 【" + corbaIp + "】 offer_change");

	} // offer_change

}
