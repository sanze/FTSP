package com.fujitsu.serviceImpl.ZTEU31Corba;

import java.util.Date;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

import ZTE_U31.nmsSession.NmsSession_IPOA;
import ZTE_U31.session.Session_I;

//import com.fujitsu./*manager.dataCollectManager.*/service.AutoCheckConnection;
//import com.fujitsu./*manager.dataCollectManager.*/service.EMSCollectService;

public class ZTEU31NmsSessionImpl extends NmsSession_IPOA {

	private String corbaIp;

	public ZTEU31NmsSessionImpl(String corbaIp) {
		this.corbaIp = corbaIp;
	}

	public void ping() {
		System.out.println(new Date() + " 【" + corbaIp + "】 ping");
		// 更新ping接收时间
//		EMSCollectService.pingReceiveTime.put(corbaIp, new Date());
//
//		if (EMSCollectService.pingTimerMap.get(corbaIp) == null) {
//			timer = new Timer();
//			// 1分钟巡检一次，连接是否可用
//			timer.schedule(new AutoCheckConnection(
//					AutoCheckConnection.CHECK_PING, corbaIp), 0,
//					AutoCheckConnection.SCHEDULE_TIME * 1000);
//			EMSCollectService.pingTimerMap.put(corbaIp, timer);
//		}
	}

	public void eventLossCleared(String endTime) {
		System.out.println("ZTE_U31NmsSessionImpl--eventLossCleared");
	}

	public void eventLossOccurred(String startTime, String notificationId) {
		System.out.println("ZTE_U31NmsSessionImpl--eventLossOccurred");
	}

	public Session_I associatedSession() {
		System.out.println("ZTE_U31NmsSessionImpl--associatedSession");
		return null;
	}

	public void endSession() {
		System.out.println("ZTE_U31NmsSessionImpl--endSession");
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

	@Override
	public void endSession(int sessionID) {
		// TODO Auto-generated method stub
		
	}
}
