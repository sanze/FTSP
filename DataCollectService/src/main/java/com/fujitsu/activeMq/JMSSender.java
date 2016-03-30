package com.fujitsu.activeMq;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.model.MessageModel;
import com.fujitsu.util.BeanUtil;

public class JMSSender {
	
	private static JmsTemplate template;
	private static Destination destination;
	
	/**
	 * 发送消息
	 * @param messageType 消息类型
	 * @param message 消息对象
	 */
	public static void sendMessage(final int messageType,final Map message){
		//获取消息模板
		if(template==null){
			template = (JmsTemplate)BeanUtil.getBean("jmsTemplate");
		}
		if(destination == null){
			destination = (Destination) BeanUtil   
	                .getBean("destination");
		}
		try{
		//发送消息
		template.send(destination,new MessageCreator(){
            @Override  
            public Message createMessage(Session session) throws JMSException {
            	MessageModel messageModel = new MessageModel(messageType,message);
                return session.createObjectMessage(messageModel);
            }
        }); 
		}catch(Exception e){
			ExceptionHandler.handleException(e);
		}
		
	}
	
	/** 
     * @param args 
     */  
    public static void main(String[] args) {  
        for(int i=0;i<100;i++){
        		Map alarm = new HashMap();
        		alarm.put("NATIVE_PROBABLE_CAUSE", "告警"+i);
            	sendMessage(1,alarm);
            	System.out.println("发送告警："+i);
        	try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

}
