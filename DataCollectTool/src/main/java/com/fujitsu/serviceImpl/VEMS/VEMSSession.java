package com.fujitsu.serviceImpl.VEMS;

import globaldefs.NameAndStringValue_T;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.service.EMSCollectService;
import com.fujitsu.service.EMSSession;
import com.fujitsu.serviceImpl.ALUCorba.ALUConsumerImpl;
import com.fujitsu.serviceImpl.ALUCorba.ALUNmsSessionImpl;
import com.fujitsu.serviceImpl.FIMCorba.FIMConsumerImpl;
import com.fujitsu.serviceImpl.FIMCorba.FIMNmsSessionImpl;
import com.fujitsu.serviceImpl.HWCorba.HWConsumerImpl;
import com.fujitsu.serviceImpl.HWCorba.HWNmsSessionImpl;
import com.fujitsu.serviceImpl.LUCENTCorba.LUCENTConsumerImpl;
import com.fujitsu.serviceImpl.LUCENTCorba.LUCENTNmsSessionImpl;
import com.fujitsu.serviceImpl.ZTEU31Corba.ZTEU31ConsumerImpl;
import com.fujitsu.serviceImpl.ZTEU31Corba.ZTEU31NmsSessionImpl;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.FtpUtils;
import com.fujitsu.util.IDLSerializableUtil;

public class VEMSSession extends EMSSession {

	public static final String EMS_NAME_VEMS="VEMS";
	public static String DIR_VEMS="../VEMS";
	public static final String DIR_NOTIFY="notification";
	public static final String NE_DIR="nelist";
    
	public final static String FACTORY_HW = "HW";
	public final static String FACTORY_ZTE = "ZTE";
	public final static String FACTORY_LUCENT = "LUCENT";
	public final static String FACTORY_FIBERHOME = "FIM";
	public final static String FACTORY_ALU = "ALU";
	public final static String FACTORY_FUJITSU = "FUJITSU";
	public static String transFactory(int FACTORY){
		switch(FACTORY){
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			return FACTORY_FIBERHOME;
		case DataCollectDefine.FACTORY_HW_FLAG:
			return FACTORY_HW;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			return FACTORY_ZTE;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			return FACTORY_LUCENT;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			return FACTORY_ALU;
		}
		return FACTORY_HW;
	}
    
    public static final String FTP_DIR="ftpfile";
    
    public int FACTORY;
    
    protected HashMap<String, Class> IDLObjectHelper;
    
    public static Map<String,Timer> notifyTimerMap = new HashMap<String,Timer>();
	
	public static Map<String,Timer> pingTimerMap = new HashMap<String,Timer>();
    
    public void ping(){
    }
	
	// 解析FTP文件完整路径为ip port path参数
	public static Map<String,String> disposeFtpDestination(String ftpDestination, int factory){
		HashMap<String,String> parameters = new HashMap<String,String>();
		String [] tmp;
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			tmp=ftpDestination.split(":");
			parameters.put(DataCollectDefine.FTP_IP, tmp[0]);
			parameters.put(DataCollectDefine.FTP_PORT, tmp.length>2?tmp[1]:"21");
			parameters.put("fileName", tmp[tmp.length-1].substring(1));
			return parameters;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			tmp=ftpDestination.split("/");
			parameters.put(DataCollectDefine.FTP_IP, tmp[0]);
			parameters.put(DataCollectDefine.FTP_PORT, "21");
			parameters.put("fileName", tmp[tmp.length-1]);
			return parameters;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			tmp=ftpDestination.split("\\|");
			parameters.put(DataCollectDefine.FTP_IP, tmp[0]);
			parameters.put("fileName", tmp[1].substring(1));
			parameters.put(DataCollectDefine.FTP_PORT, tmp.length>2?tmp[2]:"21");
			return parameters;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			tmp=ftpDestination.split(":");
			parameters.put(DataCollectDefine.FTP_IP, tmp[0]);
			parameters.put(DataCollectDefine.FTP_PORT, tmp.length>2?tmp[1]:"21");
			parameters.put("fileName", tmp[tmp.length-1].substring(2));
			return parameters;
		default:
			tmp=ftpDestination.split(":");
			parameters.put(DataCollectDefine.FTP_IP, tmp[0]);
			parameters.put(DataCollectDefine.FTP_PORT, tmp.length>2?tmp[1]:"21");
			parameters.put("fileName", tmp[tmp.length-1].substring(1));
			return parameters;
		}
	}
	public static Object getField(Object obj,String filedString){
		try {
			String[] filedStrings=filedString.split("\\.");
			java.lang.reflect.Field field;
			for(String tmp:filedStrings){
				field = obj.getClass().getDeclaredField(tmp);
				obj=field.get(obj);
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String getNotifyName(Object notification){
		Object object=getField(notification,"header.fixed_header.event_type.type_name");
		if(object==null) object="";
		return (String)object;
		
		/*if(FENGHUO.CosNotification.StructuredEvent.class.isInstance(notification))
			return ((FENGHUO.CosNotification.StructuredEvent)notification)
			.header.fixed_header.event_type.type_name;
		else if(HW.CosNotification.StructuredEvent.class.isInstance(notification))
			return ((HW.CosNotification.StructuredEvent)notification)
			.header.fixed_header.event_type.type_name;
		else if(ZTE_U31.CosNotification.StructuredEvent.class.isInstance(notification))
			return ((ZTE_U31.CosNotification.StructuredEvent)notification)
			.header.fixed_header.event_type.type_name;
		else if(LUCENT.CosNotification.StructuredEvent.class.isInstance(notification))
			return ((LUCENT.CosNotification.StructuredEvent)notification)
			.header.fixed_header.event_type.type_name;
		else if(org.omg.CosNotification.StructuredEvent.class.isInstance(notification))
			return ((org.omg.CosNotification.StructuredEvent)notification)
			.header.fixed_header.event_type.type_name;
		else 
			return "";*/
	}
	
    public String getPathByName(NameAndStringValue_T[] name){
		StringBuilder tempString = new StringBuilder();
		tempString.append(DIR_VEMS);
		tempString.append(File.separator);
		tempString.append(transFactory(FACTORY));
		tempString.append(File.separator);
		tempString.append(corbaIp);
		tempString.append(File.separator);
		tempString.append(getRelationPathByName(name));
		return tempString.toString();
	}
    public static String getRelationPathByName(NameAndStringValue_T[] name){
    	StringBuilder tempString = new StringBuilder();
    	for (int i = 1; name!=null&&i < name.length; i++) {
			tempString.append(name[i].name);
			tempString.append("_");
			tempString.append(name[i].value.replaceAll("/", ";").replaceAll(":", ""));
//			if (i != name.length - 1) {
				tempString.append(File.separator);
//			}
		}
    	return tempString.toString();
    }
	/** 关闭corba连接 */
	public void endSession() {
		// 断开通知
		endNotificationConnect();
		// 断开session
		endCorbaConnect();

	}
	/** 关闭corba连接 */
	public void endSession(int sessionID) {
		// 断开通知
		endNotificationConnect();
		// 断开session
		endCorbaConnect();

	}
	// 断开session
	private void endCorbaConnect() {
		if(pingTimerMap.containsKey(corbaIp)){
			pingTimerMap.get(corbaIp).cancel();
			pingTimerMap.remove(corbaIp);
		}
		EMSCollectService.sessionMap.remove(this.corbaIp);
	}
	// 断开通知
	private void endNotificationConnect() {
		if(notifyTimerMap.containsKey(corbaIp)){
			notifyTimerMap.get(corbaIp).cancel();
			notifyTimerMap.remove(corbaIp);
		}
		EMSCollectService.pushSupplierMap.remove(this.corbaIp);
	}
	/** 连接corba 获取EmsSession */
	public boolean connect() throws CommonException {
		// 启动corba连接
		startUpCorbaConnect();
		// 启动通知服务
		startUpNotification();

		return true;
	}

	/** EmsSession是否有效 */
	public boolean isEmsSessionInvalid() {
		return !EMSCollectService.sessionMap.containsKey(corbaIp);
	}
	// 初始化corb连接参数
	private void initCorbaComponent() throws CommonException {
	}
	// 启动corba服务
	public void startUpCorbaConnect() throws CommonException {
		// 连接还可用
		if(!isEmsSessionInvalid())
			return;
		// 初始化连接中需要的各组件
		initCorbaComponent();
		
		switch(FACTORY){
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			nmsSession = new FIMNmsSessionImpl(corbaIp);
			break;
		case DataCollectDefine.FACTORY_HW_FLAG:
			nmsSession = new HWNmsSessionImpl(corbaIp);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			nmsSession = new ZTEU31NmsSessionImpl(corbaIp);
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			nmsSession = new LUCENTNmsSessionImpl(corbaIp);
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			nmsSession = new ALUNmsSessionImpl(corbaIp);
			break;
		}

//		EMSCollectService.sessionMap.put(this.corbaIp, nmsSession);
		Timer timer = new Timer();
		// 1分钟巡检一次，连接是否可用
		timer.schedule(new AutoNmsOrConsumer(
				FACTORY,
				AutoNmsOrConsumer.CHECK_PING, corbaIp), 
				AutoNmsOrConsumer.DEALY_PING_TIME * 1000,
				AutoNmsOrConsumer.SCHEDULE_TIME * 1000);
		pingTimerMap.put(corbaIp, timer);
	}
	/** 启动通知服务 */
	public void startUpNotification() throws CommonException{
		if(!EMSCollectService.pushSupplierMap.containsKey(corbaIp)){
			Object consumer = null;
			switch(FACTORY){
			case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
				consumer = new FIMConsumerImpl(corbaIp,encode);
				break;
			case DataCollectDefine.FACTORY_HW_FLAG:
				consumer = new HWConsumerImpl(corbaIp,encode);
				break;
			case DataCollectDefine.FACTORY_ZTE_FLAG:
				consumer = new ZTEU31ConsumerImpl(corbaIp,encode);
				break;
			case DataCollectDefine.FACTORY_LUCENT_FLAG:
				consumer = new LUCENTConsumerImpl(corbaIp,encode);
				break;
			case DataCollectDefine.FACTORY_ALU_FLAG:
				consumer = new ALUConsumerImpl(corbaIp,encode);
				break;
			}
			
			EMSCollectService.pushSupplierMap.put(this.corbaIp, consumer);
		}
		if(!notifyTimerMap.containsKey(corbaIp)){
			Timer timer = new Timer();
			// 1分钟巡检一次，连接是否可用
			timer.schedule(new AutoNmsOrConsumer(
				FACTORY,
				AutoNmsOrConsumer.CHECK_NOTIFY, corbaIp), 
				AutoNmsOrConsumer.DEALY_NOTIFY_TIME * 1000,
				AutoNmsOrConsumer.SCHEDULE_TIME * 1000);
			notifyTimerMap.put(corbaIp, timer);
		}
	}

	public ArrayList<Object> getNotifications() throws CommonException{
		File dir = new File(getPathByName(null) + DIR_NOTIFY);
		if (dir.exists()){
			ArrayList<Object> events = new ArrayList<Object>();
			File[] files = dir.listFiles();
			for(File file:files){
				if(file.isFile()&&file.getName().startsWith(notificationFlag)){
					Object event = IDLSerializableUtil.readObject(file.getParent()+File.separator, file.getName(), IDLObjectHelper.get(notificationFlag));
					if(event!=null)
						events.add(event);
				}
			}
			return events;
		} else {
			return new ArrayList<Object>();
		}
	}
	
	public Object getData(String CMD, NameAndStringValue_T[] name) throws CommonException{
		System.out.println(CMD);
		
		/*if(CMD.contains("Alarms")){
			return IDLSerializableUtil.readObject(getPathByName(name), CMD, EventList_THelper.class);
		}
		return IDLSerializableUtil.readObject(getPathByName(name), CMD);*/
		return IDLSerializableUtil.readObject(getPathByName(name), CMD, IDLObjectHelper.get(CMD.split("_")[0]));

	}
	
	/*public Object getData(String CMD, NameAndStringValue_T[] name, Class helper) throws CommonException{
		System.out.println(CMD);
		return IDLSerializableUtil.readObject(getPathByName(name), CMD, helper);

	}*/

	public void getHistoryPMFile(String ftpIpAndFileName,String userName,String password) throws CommonException {
		System.out.println(getHistoryPMData);
		//将VEMS历史性能文件上传至ftp
		// ftp工具操作对象
		Map<String,String> params=disposeFtpDestination(ftpIpAndFileName,FACTORY);
		FtpUtils ftpUtils = new FtpUtils(
				params.get(DataCollectDefine.FTP_IP),
				Integer.parseInt(params.get(DataCollectDefine.FTP_PORT)),
				userName,password);
		String fileName = params.get("fileName");
		String targetDisplayName;
		try {
			targetDisplayName = new String(
					fileName.getBytes(DataCollectDefine.ENCODE_ISO),
					DataCollectDefine.ENCODE_GBK);
		} catch (UnsupportedEncodingException e) {
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_UNSUPPORTED_ENCODING_EXCEPTION);
		}
		
		ftpUtils.uploadFile(getPathByName(null)+FTP_DIR+File.separator+targetDisplayName, null, fileName);
	}
	
	public VEMSSession(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String encode, int FACTORY) {
		System.setProperty("com.sun.CORBA.transport.ORBTCPReadTimeouts",
				"100:60000:180000:20");
		this.corbaName = corbaName;
		this.corbaPassword = corbaPassword;
		this.corbaIp = corbaIp;
		this.corbaPort = corbaPort;
		this.emsName = emsName;
		this.encode = encode;
		this.FACTORY = FACTORY;
		switch(FACTORY){
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			this.IDLObjectHelper = FIMVEMSSession.CmdObjectHelper;
			break;
		case DataCollectDefine.FACTORY_HW_FLAG:
			this.IDLObjectHelper = HWVEMSSession.CmdObjectHelper;
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			this.IDLObjectHelper = ZTEVEMSSession.CmdObjectHelper;
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			this.IDLObjectHelper = LUCENTVEMSSession.CmdObjectHelper;
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			this.IDLObjectHelper = ALUVEMSSession.CmdObjectHelper;
			break;
		}
		if(CommonUtil.getSystemConfigProperty("VEMS_location")!=null){
			DIR_VEMS = CommonUtil.getSystemConfigProperty("VEMS_location");
		}
		
	}

	// 连接参数
	protected String corbaName;
	protected String corbaPassword;
	protected String corbaIp;
	protected String corbaPort;
	protected String emsName;
	protected String encode;
	
	protected Object nmsSession;
	public Object getNmsSession() {
		return nmsSession;
	}
	public Object getEmsSession() {
		return this;
	}

	public final static String notificationFlag = "NT";
	public final static String getNotification = "getNotification";
	public final static String getSupportedManagers = "getSupportedManagers";
	public final static String getEms = "getEms";
	public final static String getAllEMSAndMEActiveAlarms = "getAllEMSAndMEActiveAlarms";
	public final static String getAllEMSSystemActiveAlarms = "getAllEMSSystemActiveAlarms";
	public final static String getAllTopLevelSubnetworks = "getAllTopLevelSubnetworks";
	public final static String getAllTopLevelSubnetworkNames = "getAllTopLevelSubnetworkNames";
	public final static String getAllTopLevelTopologicalLinks = "getAllTopLevelTopologicalLinks";
	public final static String getAllManagedElements = "getAllManagedElements";
	public final static String getAllManagedElementNames = "getAllManagedElementNames";
	public final static String getAllTopologicalLinks = "getAllTopologicalLinks";
	public final static String getHoldingTime = "getHoldingTime";
	public final static String getManagedElement = "getManagedElement";
	public final static String getAllActiveAlarms = "getAllActiveAlarms";
	public final static String getAllInternalTopologicalLinks = "getAllInternalTopologicalLinks";
	public final static String getAllEquipment = "getAllEquipment";
	public final static String getAllPTPs = "getAllPTPs";
	public final static String getAllPTPNames = "getAllPTPNames";
	public final static String getContainedPotentialTPs = "getContainedPotentialTPs";
	public final static String getAllCrossConnections = "getAllCrossConnections";
	public final static String getAllCurrentPMData_Ne = "getAllCurrentPMData_Ne";
	public final static String getAllCurrentPMData_Ptp = "getAllCurrentPMData_Ptp";
	public final static String getAllCurrentPMData_Equip = "getAllCurrentPMData_Equip";
	public final static String getHistoryPMData = "getHistoryPMData";
	public final static String getHistoryPMData_NEs = "getHistoryPMData_NEs";
	public final static String getMEPMcapabilities = "getMEPMcapabilities";
	public final static String getAllProtectionGroups = "getAllProtectionGroups";
	public final static String getAllEProtectionGroups = "getAllEProtectionGroups";
	public final static String getAllWDMProtectionGroups = "getAllWDMProtectionGroups";
	public final static String getProtectionGroup = "getProtectionGroup";
	public final static String getEProtectionGroup = "getEProtectionGroup";
	public final static String getWDMProtectionGroup = "getWDMProtectionGroup";
	public final static String retrieveSwitchData = "retrieveSwitchData";
	public final static String retrieveESwitchData = "retrieveESwitchData";
	public final static String retrieveWDMSwitchData = "retrieveWDMSwitchData";
	public final static String getObjectClockSourceStatus = "getObjectClockSourceStatus";
	public final static String getAllMstpEndPointNames = "getAllMstpEndPointNames";
	public final static String getAllMstpEndPoints = "getAllMstpEndPoints";
	public final static String getAllEthService = "getAllEthService";
	public final static String getAllVBNames = "getAllVBNames";
	public final static String getAllVBs = "getAllVBs";
	public final static String getBindingPath = "getBindingPath";
	public final static String getAllVLANs = "getAllVLANs";
	public final static String getMEconfigData = "getMEconfigData";
//	public final static String  = "";
	public final static String getAllSubnetworkConnections = "getAllSubnetworkConnections";
	public final static String getSNC = "getSNC";
	public final static String getRoute = "getRoute";
	public final static String getAllEthernetSubnetworkConnections = "getAllEthSNC";
//	public final static String getMEconfigData = "getMEconfigData";
//	public final static String getMEconfigData = "getMEconfigData";
//	public final static String getMEconfigData = "getMEconfigData";
}