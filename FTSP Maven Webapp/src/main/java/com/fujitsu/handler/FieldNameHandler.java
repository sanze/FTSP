package com.fujitsu.handler;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class FieldNameHandler{
	//获取字段信息
	public static final String getFieldName(String fieldCode){
		String message = "";
		ResourceBundle bundle = ResourceBundle.getBundle("resourceConfig.i18n."+
				"fieldName");
		try {
			message = bundle.getString(fieldCode);
		} catch (MissingResourceException e) {
			message = fieldCode;
		}
		return message;
	}
	
	public static void main(String args[]){
		System.out.println(getFieldName("areaName"));
	}
}
