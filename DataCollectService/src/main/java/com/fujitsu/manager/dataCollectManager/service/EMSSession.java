package com.fujitsu.manager.dataCollectManager.service;

import java.util.List;

import org.omg.CosNaming.NamingContextExt;
import org.omg.PortableServer.POA;

import globaldefs.NameAndStringValue_T;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.ExceptionHandler;

/**
 * @author zhuangjieliang
 * 
 */
public abstract class EMSSession extends org.omg.CORBA.portable.ObjectImpl implements IEMSSession{

	// 连接参数
	protected String corbaName;
	protected String corbaPassword;
	protected String corbaIp;
	protected String corbaPort;
	protected String emsName;
	protected String encode;
	protected int iteratorNum = 200;
	
	protected Object emsSession = null;
	protected Object nmsSession;
	
	protected org.omg.CORBA.ORB orb;
	protected NamingContextExt namingContext;
	protected POA rootpoa;
	protected org.omg.CORBA.Object factoryObj;

	protected EMSSession(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String encode,int iteratorNum) {
		System.setProperty("com.sun.CORBA.transport.ORBTCPReadTimeouts",
				"100:300000:180000:20");
		updateParams(corbaName, corbaPassword, corbaIp, corbaPort, emsName,encode,iteratorNum);
	}
	public Object getEmsSession(){
		return emsSession;
	}
	public Object getNmsSession(){
		return nmsSession;
	}
	public abstract Object newNmsSession();
	
	public void updateParams(String corbaName, String corbaPassword, String corbaIp,
			String corbaPort, String emsName,String encode,int iteratorNum){
		if(!((this.corbaIp==corbaIp||(this.corbaIp!=null&&this.corbaIp.equals(corbaIp)))&&
			(this.corbaName==corbaName||(this.corbaName!=null&&this.corbaName.equals(corbaName)))&&
			(this.corbaPassword==corbaPassword||(this.corbaPassword!=null&&this.corbaPassword.equals(corbaPassword)))&&
			(this.corbaPort==corbaPort||(this.corbaPort!=null&&this.corbaPort.equals(corbaPort)))&&
			(this.emsName==emsName||(this.emsName!=null&&this.emsName.equals(emsName))))){//关键属性变更,需重新连接corba
			try { endSession(false); } catch (Exception e) {}
		}
		if(!(this.corbaIp==corbaIp||(this.corbaIp!=null&&this.corbaIp.equals(corbaIp)))){
			this.corbaIp=corbaIp;
			newNmsSession();
		}
		this.corbaName=corbaName;
		this.corbaPassword=corbaPassword;
		this.corbaPort=corbaPort;
		this.emsName=emsName;
		this.encode=encode;
		this.iteratorNum=iteratorNum;
	}
	/** EmsSession是否有效 */
	public boolean isEmsSessionInvalid() {
		boolean invalid = true;
		IEMSSession session = EMSCollectService.sessionMap.get(this.corbaIp);
		emsSession=session==null?null:session.getEmsSession();
		if (emsSession != null) {
			try{
			emsSession.getClass().getMethod("ping").invoke(emsSession);
			invalid = false;
			}catch(Exception e){}
		}
		return invalid;
	}
	public void endSession(boolean full) throws CommonException {
		// 断开通知
		endNotificationConnect();
		// 断开session
		endCorbaConnect();
		if(full)EMSCollectService.sessionMap.remove(this.corbaIp);
	}
	/** 关闭corba连接 */
	public void endSession() throws CommonException {
		endSession(true);
	}
	// 断开session
	private void endCorbaConnect() throws CommonException {
		try {
			if (emsSession != null) {
				try{
					java.lang.reflect.Method method=emsSession.getClass().getMethod("endSession");
					method.invoke(emsSession);
				}catch(java.lang.NoSuchMethodException e){
					java.lang.reflect.Method method=emsSession.getClass().getMethod("endSession",int.class);
					method.invoke(emsSession,0);
				}
			}
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_DISCONNECT_CONNECTION_FAILED_EXCEPTION);
		} finally {
			
		}
	}
	// 断开通知
	private void endNotificationConnect() throws CommonException {
		// 断开通知
		try {
			if (EMSCollectService.pushSupplierMap.containsKey(this.corbaIp)) {
				Object structuredProxyPushSupplier = 
					EMSCollectService.pushSupplierMap.get(this.corbaIp);
				Object consumerAdmin = structuredProxyPushSupplier.getClass().getMethod("MyAdmin").invoke(structuredProxyPushSupplier);
				try{
				structuredProxyPushSupplier.getClass().getMethod("disconnect_structured_push_supplier").invoke(structuredProxyPushSupplier);
				}catch(java.lang.NoSuchMethodException e){
					structuredProxyPushSupplier.getClass().getMethod("disconnect_sequence_push_supplier").invoke(structuredProxyPushSupplier);
				}
				consumerAdmin.getClass().getMethod("destroy").invoke(consumerAdmin);
			}
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_DISCONNECT_NOTIFY_FAILED_EXCEPTION);
		} finally {
			EMSCollectService.pushSupplierMap.remove(this.corbaIp);
		}
	}
	/** 连接corba 获取EmsSession */
	public boolean connect() throws CommonException {
		// 启动corba连接
		startUpCorbaConnect();
		// 启动通知服务
		// 烽火实测，连接正常，通知服务起不来，导致时钟连接失败，继而导致internal_ems_name不能正确获取，再而导致不能同步网元基础数据
		try {
		startUpNotification();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return true;
	}
	// 启动corba服务
	public abstract void startUpCorbaConnect() throws CommonException;
	/** 启动通知服务 */
	public abstract void startUpNotification() throws CommonException;
	
	
	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException {
		return new String[0];
	}
	public Object[] getAllInternalTopologicalLinks(
			NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}

	public Object[] getHistoryPMData(NameAndStringValue_T[] name,
			String startTime, String endTime,short[] layerRateList,
			String[] pmLocationList,String[] pmGranularityList)
			throws CommonException{
		return new Object[0];
	}

	/** #查询EMS性能的保持时间# */
	public Object getHoldingTime() throws CommonException{
		return new Object();
	}

	/** #查询网元性能的能力# */
	public Object[] getMEPMcapabilities(NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}

	/**	########ProtectionMgr##### */
	/** #查询ME下所有保护组信息# --中兴特有*/
	public Object getMEconfigData(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}
	
	
	/** #查询ME下所有保护组信息# */
	public Object[] getAllProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}

	/** #查询所有设备保护组信息# */
	public Object[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}

	public Object[] getAllWDMProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}
	
	/** #查询指定保护组信息# */
	public Object getProtectionGroup(NameAndStringValue_T[] pgName) throws CommonException{
		return new Object();
	}

	/** #查询指定设备保护组信息# */
	public Object getEProtectionGroup(NameAndStringValue_T[] epgName) throws CommonException{
		return new Object();
	}

	public Object getWDMProtectionGroup(NameAndStringValue_T[] wpgpName) throws CommonException{
		return new Object();
	}
	
	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public Object[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException{
		return new Object[0];
	}

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public Object[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException{
		return new Object[0];
	}
	
	public Object[] retrieveWDMSwitchData(NameAndStringValue_T[] wpgpName) throws CommonException{
		return new Object[0];
	}

	public Object[] getAllMstpEndPointNames(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	public Object[] getAllMstpEndPoints(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	
	public Object[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	
	public Object[] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	public Object[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	public Object getBindingPath(NameAndStringValue_T[] ptpName) throws CommonException{
		return new Object[0];
	}
	public Object[] getAllVLANs(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	
	/** #查询时钟源# */
	public Object[] getObjectClockSourceStatus(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	@Override
	public String[] _ids() {
		return new String[]{
				"IDL:mtnm.tmforum.org/service/EMSSession:1.0"
		};
	}
}
