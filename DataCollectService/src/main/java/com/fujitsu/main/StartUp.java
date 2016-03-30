package com.fujitsu.main;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.remoting.rmi.RmiServiceExporter;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.service.AutoCheckConnection;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.CommonUtil;
import com.log4j.Stdout2Log4j;

public class StartUp{
	static List<String> registryHosts=null;
	static String registryPort=null;
	public static void main(String[] args) throws CommonException{
		try{
			startUp();
			addShutdownHook();//程序关闭时断开Corba连接
		}catch (Exception e){
			ExceptionHandler.handleException(e);
		}finally{
		}
    }
	
	public static void startUp() throws CommonException {
		System.out.println("启动采集服务开始。。。");
		//修改log4j配置文件地址
		PropertyConfigurator.configure(ClassLoader.getSystemResource("resourceConfig/logConfig/log4j.properties"));
		new Stdout2Log4j();
		init();
		System.out.println("启动采集服务成功。。。");
		
	}
	public static void shutDown(){
		List<Map> emsList=getManagedEmsList();
		IDataCollectService dataCollectService = (IDataCollectService) BeanUtil
				.getBean("dataCollectService");
		for(Map ems:emsList){
			try{
				dataCollectService.disCorbaConnect(ems);
			}catch (Exception e) {
			}
		}
	}
	public static void addShutdownHook(){
		Thread shutdownHook = new Thread() {
			public void run() {
				shutDown();
			}
		};
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	//初始化
	public static void init() {
		String ip = CommonUtil.getSystemConfigProperty("rmi.ip");
		
		if(ip!=null){
			System.out.println("RMI服务绑定ip地址为："+ip);
			System.setProperty("java.rmi.server.hostname",ip);
		}
		
		RmiServiceExporter exp = (RmiServiceExporter) BeanUtil
				.getBean("service");
		// 获取rmi注册端口和host信息
		registryPort = getRegistPort(exp);
		registryHosts = getRegistHost(exp);
		//添加定时器
		addTimer(registryPort,registryHosts);
		//添加巡检定时器
		Timer timer = new Timer();
		// 5分钟巡检一次，是否有新增的连接
		timer.schedule(new CheckAddedConnection(
				registryPort, registryHosts), 0,
				300 * 1000);
	}
	
	//取得注册端口
	private static String getRegistPort(RmiServiceExporter exp) {
		Integer registryPort = null;
		// 获取rmi注册端口和host信息
		try {
			Field field = exp.getClass().getDeclaredField("registryPort");
			field.setAccessible(true);
			registryPort = (Integer) field.get(exp);
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
		if (registryPort == null) {
			registryPort = 1099;
		}
		return registryPort.toString();
	}
	
	//取得注册地址
	private static List<String> getRegistHost(RmiServiceExporter exp) {
		String registryHost=null;
		//获取rmi注册端口和host信息
		try {
			Field field = exp.getClass().getDeclaredField("registryHost");
			field.setAccessible(true);
			registryHost=(String)field.get(exp);
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
		List<String> registryHosts=new ArrayList<String>();
		//未配置rmi注册host信息，取本机所有地址
		if(registryHost==null){
			try {
				InetAddress localHost=InetAddress.getLocalHost();
				InetAddress[] locals = InetAddress.getAllByName(localHost.getHostName());
				for(InetAddress local:locals){
					registryHosts.add(local.getHostAddress());
				}
			} catch (UnknownHostException e) {
			}
		}else{
			registryHosts.add(registryHost);
		}
		return registryHosts;
	}
	
	//添加定时器
	private static void addTimer(String registryPort,List<String> registryHosts){
		// ems 列表
		List<Map> emsList = getManagedEmsList();
		for (Map ems : emsList) {
			
			Integer emsConnectionId = Integer.valueOf(ems.get("BASE_EMS_CONNECTION_ID").toString());
			String corbaIp = ems.get("IP").toString();
			
			if (EMSCollectService.pingTimerMap.get(corbaIp) == null) {
				Timer timerPing = new Timer();
				// 1分钟巡检一次，连接是否可用
				timerPing.schedule(new AutoCheckConnection(
						AutoCheckConnection.CHECK_PING, corbaIp,Integer.valueOf(ems.get("SVC_RECORD_ID").toString())), 0,
						AutoCheckConnection.SCHEDULE_TIME * 1000);
				EMSCollectService.pingTimerMap.put(corbaIp, timerPing);
			}

			if (EMSCollectService.heartBeatTimerMap.get(corbaIp) == null) {
				Timer timerHeartBeat = new Timer();
				// 1分钟巡检一次，通知服务是否可用
				timerHeartBeat.schedule(new AutoCheckConnection(
						AutoCheckConnection.CHECK_HEART_BEATING, corbaIp,Integer.valueOf(ems.get("SVC_RECORD_ID").toString())), 
						AutoCheckConnection.SCHEDULE_TIME * 500,
						AutoCheckConnection.SCHEDULE_TIME * 1000);//与ping检测岔开
				EMSCollectService.heartBeatTimerMap
						.put(corbaIp, timerHeartBeat);
			}
			
			//初始化网元同步状态--接入服务器异常关闭可能导致网元同步状态一直为正在同步，需要初始化为同步失败
			DataCollectMapper mapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
			//更新同步状态为同步失败
			mapper.initNeSyncStatusByEmsConnectionId(emsConnectionId, DataCollectDefine.SYNC_FAILED_FLAG);
			
			
		}
	}
	
	
	//自动检测添加的ems
	static class CheckAddedConnection extends TimerTask{

		// 获取rmi注册端口和host信息
		String registryPort;
		List<String> registryHosts;
		
		public CheckAddedConnection(String registryPort,List<String> registryHosts){
			this.registryPort = registryPort;
			this.registryHosts = registryHosts;
		}
		
		public void run() {
			addTimer(registryPort,registryHosts);
		}
		
	}
	public static List<Map> getManagedEmsList(){
		List<Map> emsList = new ArrayList<Map>();
		// 查询类
		DataCollectMapper mapper = (DataCollectMapper) BeanUtil
				.getBean("dataCollectMapper");
		// 查询本接入服务器主机下的ems
		for (String host : registryHosts) {
			emsList.addAll(mapper.selectEmsListByIpAndPort(host, registryPort));
		}
		return emsList;
	}
}
