package com.fujitsu.servlet;

import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fujitsu.manager.equipmentTestManager.service.AutoCheckConnection;
import com.fujitsu.manager.planManager.RegularTestThread;
import com.fujitsu.util.SpringContextUtil;

public class MyContextLoaderListener extends ContextLoaderListener {
	
	@Override  
    public void contextInitialized(ServletContextEvent event) {  
        ServletContext context = event.getServletContext();   
        super.contextInitialized(event);   
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);   
        SpringContextUtil.setApplicationContext(ctx);
        //初始化配置信息
        initConfig(context);
    }
	
	private void initConfig(ServletContext context){
		//初始化其他共通信息
//        com.fujitsu.common.CommonDefine.PATH_ROOT = context.getRealPath("/");
		String debugStr=com.fujitsu.util.CommonUtil.getSystemConfigProperty("debug");
		if("true".equals(debugStr)){
//			com.fujitsu.common.CommonDefine.DEBUG=true;
			new com.log4j.Stdout2Log4j();//将System.out和System.err信息输出到log4j文件中.
		}
//		//初始化数据库备份配置文件
////		initDBbackupConfigFile();
		//设备连接状态监测启动
		startAutoConnectionCheck();
		//启动周期测试线程
		startRegularTestThread();
	}
	
	private void startAutoConnectionCheck(){
		Timer timer = new Timer();
		// 5分钟检测一次，延迟1分钟启动
		timer.schedule(new AutoCheckConnection(), 2*60*1000,3*60*1000);
	}

	//启动周期测试线程
	private void startRegularTestThread(){
		new Thread(new RegularTestThread()).start();
	}

	/**
	 * 数据库备份配置与Spring配置同步
	 */
//	private void initDBbackupConfigFile(){
//		try {
//			String fileURL = "resourceConfig/systemConfig/"
//					+ com.fujitsu.common.CommonDefine.DATA_BACKUP_CONFIG_FILE;
//			DruidDataSource mysql=
//					(DruidDataSource)
//					SpringContextUtil.getBean("dataSource-mysql");
//			String mysql_user=mysql.getUsername();
//			String mysql_password=mysql.getPassword();
//			String jdbcUrlStr=mysql.getUrl().replaceFirst("^jdbc:", "");
//			java.net.URI jdbcUrl = new java.net.URI(jdbcUrlStr);
//			String mysql_host=jdbcUrl.getHost();
//			String mysql_port=""+jdbcUrl.getPort();
//			String mysql_db =jdbcUrl.getPath().replaceFirst("^\\/", "");
//			java.util.Properties properties=new java.util.Properties();
//			properties.setProperty("mysql.username", mysql_user);
//			properties.setProperty("mysql.password", mysql_password);
//			properties.setProperty("mysql.host", mysql_host);
//			properties.setProperty("mysql.port", mysql_port);
//			properties.setProperty("mysql.databaseName", mysql_db);
//			com.fujitsu.util.ConfigUtil.setProperties(properties, fileURL);
//			properties.clear();
//			com.mongodb.Mongo mongo=(com.mongodb.Mongo)SpringContextUtil.getBean("mongo");
//			String mongo_host=mongo.getAddress().getHost();
//			String mongo_port=""+mongo.getAddress().getPort();
//			String mongo_db=com.fujitsu.common.CommonDefine.MONGODB_NAME;
//			properties.setProperty("mogodb.host", mongo_host);
//			properties.setProperty("mogodb.port", mongo_port);
//			properties.setProperty("mogodb.databaseName", mongo_db);
//			com.fujitsu.util.ConfigUtil.setProperties(properties, fileURL);
//		} catch (Exception e) {
//			com.fujitsu.handler.ExceptionHandler.handleException(e);
//		}
//	}
}
