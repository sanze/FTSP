package com.fujitsu.serviceImpl.ALUCorba;

import java.util.Date;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

import ALU.nmsSession.NmsSession_IPOA;
import ALU.session.Session_I;
//import com.fujitsu./*manager.dataCollectManager.*/service.EMSCollectService;

public class ALUNmsSessionImpl extends NmsSession_IPOA {

	private String corbaIp;

	public ALUNmsSessionImpl(String corbaIp) {
		this.corbaIp = corbaIp;
	}

	public void ping() {
		System.out.println(new Date() + " 【" + corbaIp + "】 ping");
		// 更新ping接收时间

	}

	public void eventLossCleared(String endTime) {
		System.out.println("FIMNmsSessionImpl--eventLossCleared");
	}

	public void eventLossOccurred(String startTime, String notificationId) {
		System.out.println("FIMNmsSessionImpl--eventLossOccurred");
	}

	public Session_I associatedSession() {
		System.out.println("FIMNmsSessionImpl--associatedSession");
		return null;
	}

	public void endSession() {
		System.out.println("FIMNmsSessionImpl--endSession");
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
