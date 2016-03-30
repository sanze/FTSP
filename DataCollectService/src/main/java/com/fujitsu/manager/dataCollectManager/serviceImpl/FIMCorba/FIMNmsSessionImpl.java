package com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba;

import java.util.Date;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

import FENGHUO.nmsSession.NmsSession_IPOA;
import FENGHUO.session.Session_I;

import com.fujitsu.manager.dataCollectManager.service.AutoCheckConnection;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;

public class FIMNmsSessionImpl extends NmsSession_IPOA {

	private String corbaIp;

	public FIMNmsSessionImpl(String corbaIp) {
		this.corbaIp = corbaIp;
	}

	public void ping() {
		System.out.println(new Date() + " 【" + corbaIp + "】 ping");
		// 更新心跳接收时间
		AutoCheckConnection.updateReceiveTime(corbaIp,AutoCheckConnection.CHECK_PING);
		
		//跟网管互动下，忽视异常
		try {
			EMSCollectService.sessionMap
					.get(this.corbaIp).isEmsSessionInvalid();
		} catch (Exception e) {

		}
	}

	public void eventLossCleared(String endTime) {
		System.out.println(new Date() + " 【" + corbaIp + "】 eventLossCleared");
	}

	public void eventLossOccurred(String startTime, String notificationId) {
		System.out.println(new Date() + " 【" + corbaIp + "】 eventLossOccurred");
	}

	public Session_I associatedSession() {
		System.out.println(new Date() + " 【" + corbaIp + "】 associatedSession");
		return null;
	}

	public void endSession() {
		System.out.println(new Date() + " 【" + corbaIp + "】 endSession");
	}

	public OutputStream _request(String operation, boolean responseExpected) {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	public InputStream _invoke(OutputStream output)
			throws ApplicationException, RemarshalException {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	public void _releaseReply(InputStream input) {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}
	
	@Override
	public void alarmLossOccurred(String startTime, String notificationId) {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	public String getCorbaIp() {
		return corbaIp;
	}

	public void setCorbaIp(String corbaIp) {
		this.corbaIp = corbaIp;
	}
}
