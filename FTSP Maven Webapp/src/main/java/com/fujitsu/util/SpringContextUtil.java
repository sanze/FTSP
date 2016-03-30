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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fujitsu.IService.IAlarmConvergeServiceProxy;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.IFaultDiagnoseServiceProxy;
import com.fujitsu.common.CommonException;

/**
 * @author jftt
 * 
 */
public class SpringContextUtil extends BaseSpringContextUtil{
	
	public SpringContextUtil(boolean isDebug) {
		//如果是debug
		if (isDebug) {
			ApplicationContext applicationContext = null;
			if (applicationContext == null) {
				String[] configFileList = new String[] { 
						"resourceConfig/springConfig/applicationContext.xml",
						"resourceConfig/springConfig/applicationContext-service.xml",
						"resourceConfig/springConfig/applicationContext-persistence.xml",
						"resourceConfig/springConfig/applicationContext-database.xml" ,
						"resourceConfig/springConfig/applicationContext-quartz.xml" };

				applicationContext = new ClassPathXmlApplicationContext(configFileList);
			}
			setApplicationContext(applicationContext);
		} else {

		}
	}
	
	/**
	 * 获取数据采集服务代理
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException 
	 */
	public static IDataCollectServiceProxy getDataCollectServiceProxy(int emsConnectionId)
			throws CommonException {

		IDataCollectServiceProxy proxy = (IDataCollectServiceProxy) SpringContextUtil.getBean("dataCollectServiceProxyImpl");
		
		proxy.initParameter(emsConnectionId);
		
		return proxy;
	}
	
	/**
	 * 获取告警收敛服务代理
	 * @return
	 * @throws CommonException
	 */
	public static IAlarmConvergeServiceProxy getAlarmConvergeServiceProxy()
			throws CommonException {

		IAlarmConvergeServiceProxy proxy = (IAlarmConvergeServiceProxy) SpringContextUtil.getBean("alarmConvergeServiceProxyImpl");
		
		proxy.initParameter();
		
		return proxy;
	}

	/**
	 * 获取故障诊断服务代理
	 * @return
	 * @throws CommonException
	 */
	public static IFaultDiagnoseServiceProxy getFaultDiagnoseServiceProxy()
			throws CommonException {

		IFaultDiagnoseServiceProxy proxy = (IFaultDiagnoseServiceProxy) SpringContextUtil.getBean("faultDiagnoseServiceProxyImpl");
		
		proxy.initParameter();
		
		return proxy;
	}

}
