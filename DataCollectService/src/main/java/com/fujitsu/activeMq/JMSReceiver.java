package com.fujitsu.activeMq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.listener.SessionAwareMessageListener;


/**
 * 
 * 消息接收监听器。
 */
public class JMSReceiver implements SessionAwareMessageListener {

	public void onMessage(Message message, Session session) throws JMSException {

//		System.out.println("JMSReceiver onMessage");

	}
}
