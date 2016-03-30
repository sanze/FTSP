package com.fujitsu.IService;

import com.fujitsu.common.CommonException;


/**
 * @author xuxiaojun
 * 
 */
public interface IEMSSession {

	public Object getNmsSession();
	public Object getEmsSession();
	public boolean isEmsSessionInvalid();
	public void updateParams(String corbaName, String corbaPassword, String corbaIp,
			String corbaPort, String emsName,String encode,int iteratorNum);
	/**
	 * 启动corba服务和通知服务
	 * @throws CommonException
	 */
	public boolean connect() throws CommonException;
	/**
	 * 启动corba服务
	 * @throws CommonException
	 */
	public void startUpCorbaConnect() throws CommonException;
	
	/**
	 *  启动通知服务
	 * @throws CommonException
	 */
	public void startUpNotification() throws CommonException;

	/** 关闭corba连接 */
	public void endSession() throws CommonException;

}
