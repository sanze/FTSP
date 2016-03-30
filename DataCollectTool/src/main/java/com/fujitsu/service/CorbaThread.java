package com.fujitsu.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

//import org.mybatis.spring.SqlSessionTemplate;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
//import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.CommonUtil;

public class CorbaThread implements Callable<Object> {

	public static final int THEAD_TYPE_CONNECT = 0;

	public static String MAP_KEY_IP = "corbaIp";
	public static String MAP_KEY_PORT = "corbaPort";
	public static String MAP_KEY_ENTITYPATH = "entityPath";
	public static String MAP_KEY_NAMEROOT = "nameRoot";

	public static String MAP_KEY_ORB = "orb";
	public static String MAP_KEY_NAMINGCONTEXT = "namingContext";
	public static String MAP_KEY_FACTORYOBJECT = "factoryObj";
	public static String MAP_KEY_ROOTPOA = "rootPoa";

	private HashMap inputParams;
	private HashMap outputParams = new HashMap();

	private int theadType;

	public CorbaThread(HashMap inputParams, int theadType) {
		this.theadType = theadType;
		this.inputParams = inputParams;
	}

	public HashMap call() throws CommonException {

		switch (theadType) {
		case THEAD_TYPE_CONNECT:
			//ip地址，端口
			String corbaIp = (String) inputParams.get(MAP_KEY_IP);
			String corbaPort = (String) inputParams.get(MAP_KEY_PORT);
			String nameRoot=(String) inputParams.get(MAP_KEY_NAMEROOT);
			String key = corbaIp+":"+corbaPort;

			org.omg.CORBA.ORB orb = (org.omg.CORBA.ORB) outputParams
					.get(MAP_KEY_ORB);
			if (orb == null) {
				try {

					if(EMSCollectService.orbMap.containsKey(key))
						orb=(org.omg.CORBA.ORB)EMSCollectService.orbMap.get(key);
					else {
						orb=getORB(corbaIp,corbaPort,nameRoot);
						EMSCollectService.orbMap.put(key, orb);
					}
					outputParams.put(MAP_KEY_ORB, orb);
				} catch (Exception e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_ORBINIT_FAILED_EXCEPTION);
				}
			}
			
			// rootpoa
			POA rootpoa = (POA) outputParams.get(MAP_KEY_ROOTPOA);
			if (rootpoa == null) {
				try {
					rootpoa = POAHelper.narrow(orb
							.resolve_initial_references("RootPOA"));
					rootpoa.the_POAManager().activate();
					outputParams.put(MAP_KEY_ROOTPOA, rootpoa);
				} catch (Exception e) {
					//rootpoa获取失败情况，可能orb已经失效，需要重新获取，现场实测发现
					//使用已有的orb获取rootpoa失败，重启采集服务后正常
					//重新获取orb
					orb=getORB(corbaIp,corbaPort,nameRoot);
					try{
						rootpoa = POAHelper.narrow(orb
								.resolve_initial_references("RootPOA"));
						rootpoa.the_POAManager().activate();
						//没异常出现，重置参数
						EMSCollectService.orbMap.put(key, orb);
						outputParams.put(MAP_KEY_ORB, orb);
						outputParams.put(MAP_KEY_ROOTPOA, rootpoa);
					}catch(Exception e1){
						throw new CommonException(e1,
								MessageCodeDefine.CORBA_ROOT_POA_FAILED_EXCEPTION);
					}
				}
			}
			
			// NameService
			NamingContextExt namingContext = (NamingContextExt) outputParams
					.get(MAP_KEY_NAMINGCONTEXT);
			if (namingContext == null) {
				try {
					org.omg.CORBA.Object objRef = orb
							.resolve_initial_references("NameService");
					namingContext = NamingContextExtHelper.narrow(objRef);
					outputParams.put(MAP_KEY_NAMINGCONTEXT, namingContext);
				} catch (Exception e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_NAME_SERVICE_FAILED_EXCEPTION);
				}
			}
			// factoryObj
			NameComponent[] entityPath = (NameComponent[]) inputParams
					.get(MAP_KEY_ENTITYPATH);
			org.omg.CORBA.Object factoryObj = null;
			Exception exception = null;
			try {
				factoryObj = namingContext.resolve(entityPath);
			} catch (Exception e) {
				exception = e;
				factoryObj = getFactoryObj(namingContext);
			}
			if (factoryObj != null) {
				outputParams.put(MAP_KEY_FACTORYOBJECT, factoryObj);
			} else {
				throw new CommonException(exception,
						MessageCodeDefine.CORBA_EMS_SESSION_FACTORY_FAILED_EXCEPTION);
			}
			break;
		}
		return outputParams;
	}

	// 获取factoryObj
	private org.omg.CORBA.Object getFactoryObj(NamingContextExt namingContext) {
		org.omg.CORBA.Object factoryObj = null;
		// 开始遍历命名树查找可用的实例
		ArrayList<NameComponent[]> entityPathList = EMSCollectService
				.getAllEntityPathWithContext(namingContext, null);
		boolean find = false;
		if (entityPathList != null) {
			for (NameComponent[] entityPath : entityPathList) {
				try {
					factoryObj = namingContext.resolve(entityPath);
					find = true;
					// 修改连接表中的emsName字段
					// 各厂家命名树EmsSessionFactory绑定kind不同, 取最后一个
					NameComponent nc=entityPath[entityPath.length-1];
//					for (NameComponent nc : entityPath) {
//						if ("EmsSessionFactory_I".equals(nc.kind)) {
//							SqlSessionTemplate sqlSession = (SqlSessionTemplate) BeanUtil
//									.getBean("sqlSession");
//							Map emsConnection = new HashMap();
//							emsConnection.put("IP",
//									(String) inputParams.get(MAP_KEY_IP));
//							emsConnection.put("EMS_NAME", nc.id);
//							sqlSession
//									.update("com.fujitsu.dao.mysql.DataCollectMapper.updateEmsConnectionByIP",
//											emsConnection);
//							break;
//						}
//					}
					break;//取得factory对象,跳出循环
				} catch (Exception e) {
					if(find) break;//更新数据库出错但已取得factory对象,跳出循环
					continue;
				}
			}
		}
		if (!find) {
			factoryObj = null;
		}
		return factoryObj;
	}
	private synchronized org.omg.CORBA.ORB getORB(String corbaIp,String corbaPort,String nameRoot){
		if(nameRoot==null)nameRoot="NameService";
		//优先配置文件中获取，如果配置文件中没有，自动适配
		String ipKey = "FTP_"+corbaIp;
		String localHostString = CommonUtil.getFtpIpMappingConfigProperty(ipKey);
		if(localHostString == null){
			InetAddress host=CommonUtil.getLocalHost(corbaIp);
			if(host!=null){
				//获取ip地址后写入配置文件
				localHostString = host.getHostAddress();
				CommonUtil.writeFtpIpMappingConfigProperty(ipKey, localHostString);
			}
		}
		//设置并获取orb
		String[] args = new String[2];
		args[0] = "-ORBInitRef";
		args[1] = "NameService=corbaloc::" + corbaIp + ":"
				+ corbaPort + "/"+nameRoot;
		try {
			java.lang.reflect.Field field = com.sun.corba.se.impl.orb.ORBImpl.class
					.getDeclaredField("localHostString");
			field.setAccessible(true);
			if(localHostString!=null&&localHostString.isEmpty()){
				localHostString=null;
			}
			System.out.println("default local address:"+java.net.InetAddress.getLocalHost().getHostAddress());
			System.out.println("initial local address:"+localHostString);
			field.set(com.sun.corba.se.impl.orb.ORBImpl.class.getClass(), localHostString);
		} catch (Exception e) {
		}
		java.util.Properties properties = new java.util.Properties();
		properties.setProperty("org.omg.CORBA.ORBClass", 
				com.sun.corba.se.impl.orb.ORBImpl.class.getName());
		return org.omg.CORBA.ORB.init(args, properties);
	}
}
