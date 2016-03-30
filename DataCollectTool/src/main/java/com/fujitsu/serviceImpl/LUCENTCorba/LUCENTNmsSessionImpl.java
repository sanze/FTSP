package com.fujitsu.serviceImpl.LUCENTCorba;

import java.util.Date;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

import LUCENT.nmsSession.NmsSession_IPOA;
import LUCENT.session.Session_I;

//import com.fujitsu.manager.dataCollectManager.service.AutoCheckConnection;
//import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;

public class LUCENTNmsSessionImpl extends NmsSession_IPOA {

	private String corbaIp;

	public LUCENTNmsSessionImpl(String corbaIp) {
		this.corbaIp = corbaIp;
	}
	
	public void ping() {

		System.out.println(new Date() + " 【" + corbaIp + "】 ping");
		// 更新心跳接收时间
//		AutoCheckConnection.updateReceiveTime(corbaIp,AutoCheckConnection.CHECK_PING);
	}
	
	public void eventLossCleared(String endTime) {
		System.out.println("LUCENTNmsSessionImpl--eventLossCleared");
	}

	public void eventLossOccurred(String startTime, String notificationId) {
		System.out.println("LUCENTNmsSessionImpl--eventLossOccurred");
	}

	public Session_I associatedSession() {
		System.out.println("LUCENTNmsSessionImpl--associatedSession");
		return null;
	}

	public void endSession() {
		System.out.println("LUCENTNmsSessionImpl--endSession");
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

	public String getCorbaIp() {
		return corbaIp;
	}

	public void setCorbaIp(String corbaIp) {
		this.corbaIp = corbaIp;
	}
}
