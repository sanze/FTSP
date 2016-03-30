package com.fujitsu.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.alibaba.druid.pool.DruidDataSource;
import com.fujitsu.common.BaseDefine;

public class BeanUtil {

	private static ApplicationContext context;

	public static Object getBean(String beanName) {

		if (context == null) {
			if (new File(
					"../resourceConfig/springConfig/applicationContext.xml")
					.exists()) {// 存在生产环境配置文件
				String[] configFileList = new String[] {
						"../resourceConfig/springConfig/applicationContext.xml",
						"../resourceConfig/springConfig/applicationContext-service.xml",
						"../resourceConfig/springConfig/applicationContext-persistence.xml",
						"../resourceConfig/springConfig/applicationContext-database.xml" };
				context = new FileSystemXmlApplicationContext(configFileList);
			} else {
				String[] configFileList = new String[] {
						"resourceConfig/springConfig/applicationContext.xml",
						"resourceConfig/springConfig/applicationContext-service.xml",
						"resourceConfig/springConfig/applicationContext-persistence.xml",
						"resourceConfig/springConfig/applicationContext-database.xml" };
				context = new ClassPathXmlApplicationContext(configFileList);
			}
		}
		return context.getBean(beanName);
	}

	/**
	 * 获取配置数据库名
	 * 
	 * @return
	 */
	public static String getDataBaseName() {

		DruidDataSource ds = (DruidDataSource) getBean("dataSource-mysql");

		String urlWithoutParam = ds.getUrl().split("[?]")[0];

		return urlWithoutParam.split("[/]")[urlWithoutParam.split("[/]").length - 1];

	}
	
	public static Map getDataBaseParam(){
		
		DruidDataSource ds = (DruidDataSource) getBean("dataSource-mysql");
		
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
