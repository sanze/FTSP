package com.fujitsu.model;

import java.io.Serializable;

public class MessageModel implements Serializable{
	
	private int messageType;
	private Object message;
	
	public MessageModel(int messageType,Object message){
		this.messageType = messageType;
		this.message = message;
	}
	
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
}
