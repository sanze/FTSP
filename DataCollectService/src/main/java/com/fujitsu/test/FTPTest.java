package com.fujitsu.test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.PropertyConfigurator;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.CommonUtil;

public class FTPTest{
	
	private static IDataCollectService dataCollectService;
	
	private static final Pattern IPV4_PATTERN = 
	        Pattern.compile(
	                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");	

	public static void main(String[] args) throws CommonException{
//			startUp();
//			
//			dataCollectService = (IDataCollectService) BeanUtil.getBean("dataCollectService");
//			
//			//获取配置ip地址
//			String ftpIp = CommonUtil.getSystemConfigProperty(DataCollectDefine.FTP_IP);
//			System.out.println(ftpIp);
//			if(ftpIp.equals(CommonUtil.getLocalHostName())){
//				InetAddress host=CommonUtil.getLocalHost("10.41.23.2");
//				if(host!=null)
//					ftpIp = host.getHostAddress();
//			}
//			System.out.println("FTP连接IP地址："+ftpIp);
//			
//			int ftpPort=Integer.parseInt(CommonUtil
//					.getSystemConfigProperty(DataCollectDefine.FTP_PORT));
//			
//			FtpUtils ftpUtils = new FtpUtils(ftpIp,ftpPort,"admin","admin");
//			
//			ftpUtils.uploadFile("c:/historyPm.csv", "test/test", "historyPm111.csv");
			
//			Map connection = commonManagerMapper.selectTableById(
//					"T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID",
//					emsConnectionId);
//			
//			dataCollectService.getHistoryPmData_Ne(paramter, neId, time, layerRateList, pmLocationList, pmGranularityList, collectNumbic, collectPhysical, collectCtp, commandLevel)
		
//		try{
//			String ip = "192.3.1.16";
//			InetAddress target=InetAddress.getByName(ip);
//			Enumeration<NetworkInterface> eInfs=NetworkInterface.getNetworkInterfaces();
//			while(eInfs!=null&&eInfs.hasMoreElements()){
//				System.out.println("循环取NetworkInterface组件");
//				NetworkInterface inf=eInfs.nextElement();
//				if(inf.isUp()&&!inf.isVirtual()&&!inf.isLoopback()){
//					inf=NetworkInterface.getByInetAddress(inf.getInetAddresses().nextElement());
//					if(target.isReachable(inf, 0, 10000)){
//						List<InetAddress> adresses = new ArrayList<InetAddress>();
//						Enumeration<InetAddress> eAddrs=inf.getInetAddresses();
//						while(eAddrs.hasMoreElements()){
//							adresses.add(eAddrs.nextElement());
//						}
//						if(adresses.size()>1){
//							Field addrsField=inf.getClass().getDeclaredField("addrs");
//							Field bindsField=inf.getClass().getDeclaredField("bindings");
//							addrsField.setAccessible(true);
//							bindsField.setAccessible(true);
//							List<InterfaceAddress> iAddrs= inf.getInterfaceAddresses();
//							for(InetAddress addr:adresses){
//								addrsField.set(inf, new InetAddress[]{addr});
//								for(InterfaceAddress iAddr:iAddrs){
//									if(iAddr.getAddress().equals(addr)){
//										bindsField.set(inf, new InterfaceAddress[]{iAddr});
//										break;
//									}
//								}
//								if(target.isReachable(inf, 0, 10000)){
//									System.out.println("yaoxi,找到了"+addr.getHostAddress());
//								}else{
//									System.out.println("未找到："+addr.getHostAddress());
//								}
//							}
//						}else{
//							System.out.println("yaoxi,找到了"+adresses.get(0).getHostAddress());
////							return adresses.get(0);
//						}
//					}else{
//						System.out.println("不能ping到"+ip);
//					}
//				}
//			}
//		}catch(UnknownHostException e){
//			System.out.println(e+"at CommonUtil.getLocalHost(targetIp);");
//		}catch(IOException e){
//			System.out.println(e+"at CommonUtil.getLocalHost(targetIp);");
//		}catch (IllegalArgumentException e) {
//			System.out.println(e+"at CommonUtil.getLocalHost(targetIp);");
//		} catch (IllegalAccessException e) {
//			System.out.println(e+"at CommonUtil.getLocalHost(targetIp);");
//		} catch (SecurityException e) {
//			System.out.println(e+"at CommonUtil.getLocalHost(targetIp);");
//		} catch (NoSuchFieldException e) {
//			System.out.println(e+"at CommonUtil.getLocalHost(targetIp);");
//		}
//		InetAddress local=null;
//		try {
//			local = InetAddress.getLocalHost();
//		} catch (UnknownHostException e) {
//			System.out.println(e+"at CommonUtil.getLocalHost(targetIp);");
//		}
//		System.out.println("。。。。默认："+local.getHostAddress());
//		try {
//			Thread.sleep(10*1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String targetIp = "192.168.51.2";
		String sourceIp = CommonUtil.getLocalHost(targetIp).getHostAddress();
		System.out.println("对端IP:"+targetIp+"------本端IP:"+sourceIp);
		targetIp = "192.168.50.2";
		sourceIp = CommonUtil.getLocalHost(targetIp).getHostAddress();
		System.out.println("对端IP:"+targetIp+"------本端IP:"+sourceIp);
		targetIp = "10.41.23.2";
		sourceIp = CommonUtil.getLocalHost(targetIp).getHostAddress();
		System.out.println("对端IP:"+targetIp+"------本端IP:"+sourceIp);
		targetIp = "192.3.1.6";
		sourceIp = CommonUtil.getLocalHost(targetIp).getHostAddress();
		System.out.println("对端IP:"+targetIp+"------本端IP:"+sourceIp);
		targetIp = "192.5.1.16";
		sourceIp = CommonUtil.getLocalHost(targetIp).getHostAddress();
		System.out.println("对端IP:"+targetIp+"------本端IP:"+sourceIp);
		targetIp = "192.1.1.16";
		sourceIp = CommonUtil.getLocalHost(targetIp).getHostAddress();
		System.out.println("对端IP:"+targetIp+"------本端IP:"+sourceIp);
		
		try {
			Thread.sleep(60*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
    }
	
	public static void startUp() throws CommonException {

		System.out.println("启动采集服务开始11111。。。");
		//修改log4j配置文件地址
		PropertyConfigurator.configure(ClassLoader.getSystemResource("resourceConfig/logConfig/log4j.properties"));
		BeanUtil.getBean("service");
		System.out.println("启动采集服务成功11111。。。");
	}
}
