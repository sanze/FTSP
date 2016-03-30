package com.fujitsu.main;

import java.util.List;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.remoting.rmi.RmiServiceExporter;

import com.fujitsu.IService.IFaultDiagnoseService;
import com.fujitsu.common.CommonException;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.BeanUtil;
import com.log4j.Stdout2Log4j;

public class StartUp{
	static List<String> registryHosts=null;
	static String registryPort=null;
	public static void main(String[] args) throws CommonException{
		try{
			startUp();
		}catch (Exception e){
			ExceptionHandler.handleException(e);
		}finally{
		}
    }
	
	public static void startUp() throws CommonException {
		System.out.println("启动故障诊断服务开始。。。");
		//修改log4j配置文件地址
		PropertyConfigurator.configure(ClassLoader.getSystemResource("resourceConfig/logConfig/log4j.properties"));
		new Stdout2Log4j();
		init();
		System.out.println("启动故障诊断服务成功。。。");

	}
	
	//初始化
	public static void init() throws CommonException{
		
		// 以下方法必须执行，否则RMI服务好像不可用
		RmiServiceExporter exp = (RmiServiceExporter) BeanUtil
				.getBean("serviceExporter");
		
		IFaultDiagnoseService faultDiagnoseService = (IFaultDiagnoseService) BeanUtil
				.getBean("faultDiagnoseService");
		// 告警收敛服务初始化
		faultDiagnoseService.init();
	}
	
}
