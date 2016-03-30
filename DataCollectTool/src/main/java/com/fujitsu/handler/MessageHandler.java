package com.fujitsu.handler;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.fujitsu.common.DataCollectDefine;


/**
 * @author xuxiaojun
 * 
 */
public class MessageHandler{
	
	
	//获取错误信息
	public static final String getErrorMessage(int errorCode){
		String message = "";
		ResourceBundle bundle = ResourceBundle.getBundle("resourceConfig.i18n."+DataCollectDefine.MESSAGE_CONFIG_FILE);
		try {
			message = bundle.getString(String.valueOf(errorCode));
		} catch (MissingResourceException e) {
			message = "（"+errorCode+"）未知错误！";
		}
		return message;
	}
	
	public static void main(String args[]){
		System.out.println(getErrorMessage(1));
	}
}
