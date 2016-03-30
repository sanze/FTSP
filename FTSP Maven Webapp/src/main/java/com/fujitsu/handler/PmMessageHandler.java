package com.fujitsu.handler;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.fujitsu.common.CommonDefine;


/**
 * @author wangjian
 * 
 */
public class PmMessageHandler{
	
	
	//获取错误信息
	public static final double getPmMessage(String level){
		double message = 0.0;
		ResourceBundle bundle = ResourceBundle.getBundle("resourceConfig.systemConfig."+CommonDefine.PMSEC_CONFIG_FILE);
		try {
			message = Double.parseDouble(bundle.getString(level));
		} catch (MissingResourceException e) {
			message = 0.0;
		}
		return message;
	}
	
	public static void main(String args[]){
		
	}
}
