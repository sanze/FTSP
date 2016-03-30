/**
 * All rights Reserved, Copyright (C) JFTT 2011<BR>
 * 
 * FileName: SpringContextUtil.java <BR>
 * Version: $Id: SpringContextUtil.java, v 1.00 2011-9-22 $<BR>
 * Modify record: <BR>
 * NO. |     Date         |    Name                  |      Content <BR>
 * 1   |    2011-9-22       | JFTT)Cheng Yingqi        | original version <BR>
 */
package com.fujitsu.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.alibaba.druid.pool.DruidDataSource;
import com.fujitsu.common.BaseDefine;

/**
 * @author jftt
 * 
 */
public class BaseSpringContextUtil{

	private static ApplicationContext applicationContext;
	
	public BaseSpringContextUtil(){

//		if(applicationContext == null){
//			String[] configFileList = new String[] { 
//					"resourceConfig/springConfig/applicationContext.xml",
//					"resourceConfig/springConfig/applicationContext-service.xml",
//					"resourceConfig/springConfig/applicationContext-persistence.xml",
//					"resourceConfig/springConfig/applicationContext-database.xml" ,
//					"resourceConfig/springConfig/applicationContext-quartz.xml" };
//			
//			applicationContext = new ClassPathXmlApplicationContext(configFileList);
//		}
	}

	public static void setApplicationContext(ApplicationContext applicationContext){

		BaseSpringContextUtil.applicationContext = applicationContext;

	}

	public ApplicationContext getApplicationContext() {

		return applicationContext;

	}

	public static Object getBean(String name) throws BeansException {

		return applicationContext.getBean(name);

	}

	public Object getBean(String name, Class requiredType){

		return applicationContext.getBean(name, requiredType);

	}

	public boolean containsBean(String name) {

		return applicationContext.containsBean(name);

	}

	public boolean isSingleton(String name)
			throws NoSuchBeanDefinitionException {

		return applicationContext.isSingleton(name);

	}

	public Class getType(String name)
			throws NoSuchBeanDefinitionException {

		return applicationContext.getType(name);

	}

	public String[] getAliases(String name)
			throws NoSuchBeanDefinitionException {

		return applicationContext.getAliases(name);

	}
	
	public static String getDataBaseParam(String param){
		return (String) getDataBaseParam().get(param);
	}
	
	public static Map getDataBaseParam(){
		
		DruidDataSource ds = (DruidDataSource) BaseSpringContextUtil.getBean("dataSource-mysql");
		
		String urlWithoutParam = ds.getUrl().split("[?]")[0];
		
		String sid = urlWithoutParam.split("[/]")[urlWithoutParam.split("[/]").length-1];;
		String host = (urlWithoutParam.split("[/]")[urlWithoutParam.split("[/]").length-2]).split(":")[0];
		String port = (urlWithoutParam.split("[/]")[urlWithoutParam.split("[/]").length-2]).split(":")[1];
		String userName = ds.getUsername();
		String password = ds.getPassword();

		Map param = new HashMap();
		
		param.put(BaseDefine.DB_SID, sid);
		param.put(BaseDefine.DB_HOST, host);
		param.put(BaseDefine.DB_PORT, port);
		param.put(BaseDefine.DB_USERNAME, userName);
		param.put(BaseDefine.DB_PASSWORD, password);
		
		return param;
	}
}
