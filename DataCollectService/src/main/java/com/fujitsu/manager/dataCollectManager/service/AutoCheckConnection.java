package com.fujitsu.manager.dataCollectManager.service;

import globaldefs.NameAndStringValue_T;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.IService.IEMSSession;
import com.fujitsu.IService.IFaultManagerService;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba.ALUEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba.FIMEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba.HWEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba.LUCENTEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.ALUVEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.FIMVEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.HWVEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.LUCENTVEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.ZTEVEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba.ZTEU31EMSSession;
import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.CommonUtil;

/**
 * @author xuxiaojun
 * 
 */
public class AutoCheckConnection extends TimerTask {

	public static int CHECK_HEART_BEATING = 0;
	public static int CHECK_PING = 1;
	
	private static DataCollectMapper dataCollectMapper;
	private static IFaultManagerService faultManagerService;
	private static IDataCollectService dataCollectService;

	// 定时器 循环间隔时间 单位：秒
	public static int SCHEDULE_TIME = Integer.valueOf(CommonUtil
			.getSystemConfigProperty(DataCollectDefine.AUTO_CONNECT_CHECK_INTERVAL_TIME));
	// 心跳，ping判断超时时间 单位：分钟
	public static int CHECK_TIME_OUT = 1;

	private int svcRecordId;
	private int nmsType;
	private int type;
	private String corbaName;
	private String corbaPassword;
	private String corbaIp;
	private String corbaPort;
	private String emsName;
	private String encode;
	private int collectMode;
	private int connectStatus;
	private int iteratorNum;

	public AutoCheckConnection(int type,String corbaIp,int svcRecordId) {
		this.type = type;
		this.corbaIp = corbaIp;
		this.svcRecordId = svcRecordId;
	}

	//每网管一个AutoCheckConnection实例,应该不会冲突.
	public void run() {
		
		if(dataCollectMapper == null){
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		
		//获取ems信息 此处取得的connection均为未删除的，只能通过判空判断是否isDel
		Map connection = dataCollectMapper.selectEmsConnectionByIP(corbaIp, DataCollectDefine.FALSE);

		boolean isDel = (connection==null||connection.isEmpty());
		int svcRecordIdRealTime = (isDel||connection.get("SVC_RECORD_ID")==null) ? -99999:
			(Integer) connection.get("SVC_RECORD_ID");

		//如果是网管已经删除,或网管已不属于此采集服务器管理
		if(isDel || svcRecordId!=svcRecordIdRealTime){
			//取消心跳通知定时器
			if(EMSCollectService.heartBeatTimerMap.get(corbaIp)!=null){
				EMSCollectService.heartBeatTimerMap.get(corbaIp).cancel();
			}
			//移除心跳通知定时器
			EMSCollectService.heartBeatTimerMap.remove(corbaIp);
			
			//取消ping定时器
			if(EMSCollectService.pingTimerMap.get(corbaIp)!=null){
				EMSCollectService.pingTimerMap.get(corbaIp).cancel();
			}
			//移除ping定时器
			EMSCollectService.pingTimerMap.remove(corbaIp);
			
			return;
		}
		
		this.nmsType = (Integer) connection.get("TYPE");
		this.emsName = (String) connection.get("EMS_NAME");
		this.corbaName = (String) connection.get("USER_NAME");
		this.corbaPassword = (String) connection.get("PASSWORD");
		this.corbaPort = (String) connection.get("PORT");
		this.iteratorNum = connection.get("ITERATOR_NUM")==null?100:(Integer) connection.get("ITERATOR_NUM");
		this.collectMode = connection.get("CONNECTION_MODE") != null ? (Integer) connection
				.get("CONNECTION_MODE") : DataCollectDefine.CONNECT_MODE_AUTO;
		this.connectStatus = connection.get("CONNECT_STATUS") != null ? (Integer) connection
				.get("CONNECT_STATUS") : DataCollectDefine.CONNECT_STATUS_DISCONNECT_FLAG;
		//编码信息
		this.encode = (String) connection.get("ENCODE");
		
		IEMSSession session = null;

		Date date = null;

		if (type == CHECK_HEART_BEATING) {
			System.out.println(new Date() + " 我在巡查通知服务！" + " 【" + corbaIp + "】 ");
			date = EMSCollectService.heartBeatReceiveTime.get(corbaIp);
		} else if (type == CHECK_PING) {
			System.out.println(new Date() + " 我在巡查corba连接！" + " 【" + corbaIp + "】 ");
			date = EMSCollectService.pingReceiveTime.get(corbaIp);
		}
		// 上次ping时间+CHECK_TIME_OUT分钟
		if(date == null){
			//设置初始时间为当前时间前1分钟
			date = CommonUtil.getSpecifiedDay(new Date(), 0, -1);
		}else{
			date = CommonUtil.getSpecifiedDay(date, 0, CHECK_TIME_OUT);
		}
		boolean isVirtualEms = DataCollectDefine.EMS_NAME_VEMS.equals(emsName);
		// ems对象获取
		switch (nmsType) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			session = isVirtualEms?
					HWVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
							corbaPort, emsName, encode):
					HWEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
					corbaPort, emsName,encode,iteratorNum);
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			session = isVirtualEms?
					ZTEVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
							corbaPort, emsName,encode):
					ZTEU31EMSSession.newInstance(corbaName, corbaPassword, corbaIp,
					corbaPort, emsName,encode,iteratorNum);
			break;
			
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			session = isVirtualEms?
					LUCENTVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
							corbaPort, emsName, encode):
					LUCENTEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
					corbaPort, emsName,encode,iteratorNum);
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			session = isVirtualEms?
					FIMVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
							corbaPort, emsName, encode):
					FIMEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
					corbaPort, emsName,encode,iteratorNum);
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			session = isVirtualEms?
					ALUVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
							corbaPort, emsName, encode):
					ALUEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
					corbaPort, emsName,encode,iteratorNum);
			break;
		}
		// 小于当前时间 CHECK_TIME_OUT分钟未收到信息重启服务
		if (date.before(new Date())) {
			//手动连接 则更新为连接断开
			if(collectMode == DataCollectDefine.CONNECT_MODE_MANUAL){
				if(connectStatus != DataCollectDefine.CONNECT_STATUS_DISCONNECT_FLAG){
				//更新网管连接状态--连接中断
				Map emsConnection = new HashMap();
				emsConnection.put("IP",corbaIp);
				emsConnection.put("CONNECT_STATUS", DataCollectDefine.CONNECT_STATUS_DISCONNECT_FLAG);
				dataCollectMapper.updateEmsConnectionByIP(emsConnection);
				}
				
				//如果之前连接状态为正常，产生网管连接中断告警
				if(connectStatus == DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG){
				//产生网络中断告警
				occouredEmsAlarm(connection,DataCollectDefine.CONNECT_STATUS_INTERRUPT_FLAG);
				}
				
			}else if(collectMode == DataCollectDefine.CONNECT_MODE_AUTO){
				int connectStatus = DataCollectDefine.CONNECT_STATUS_INTERRUPT_FLAG;
				String errorMessage = "";
				try {
//					//接入服务器状态
//					int status = connection.get("STATUS") != null ? (Integer) connection
//							.get("STATUS") : DataCollectDefine.CONNECT_STATUS_EXCEPTION_FLAG;
//					
//					if(status != DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG){
//						connectStatus = DataCollectDefine.CONNECT_STATUS_EXCEPTION_FLAG;
//						errorMessage = "接入服务器异常！";
//					}else{
						//连接服务器正常状态下处理流程
					boolean isReachable = isVirtualEms||
						CommonUtil.isReachable(corbaIp);
					if(isReachable){
						connectStatus=DataCollectDefine.CONNECT_STATUS_EXCEPTION_FLAG;
	
						if (type == CHECK_HEART_BEATING) {
							session.startUpNotification();
						} else if (type == CHECK_PING) {
							session.startUpCorbaConnect();
						}
						connectStatus=DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG;
					}
//					}
				} catch (Exception e) {
					if(CommonException.class.isInstance(e)){
						errorMessage = ((CommonException)e).getErrorMessage();
					}
				} finally {
					switch(connectStatus){
					case DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG:
						recoverOperation(corbaIp,type);
						break;
					case DataCollectDefine.CONNECT_STATUS_EXCEPTION_FLAG:
					case DataCollectDefine.CONNECT_STATUS_INTERRUPT_FLAG:
					default:
						//更新网管连接状态
						Map emsConnection = new HashMap();
						emsConnection.put("IP",corbaIp);
						//更新网管连接状态--连接异常
						emsConnection.put("CONNECT_STATUS", connectStatus);
						//如果之前连接状态为正常，产生网管连接中断告警
						if(this.connectStatus == DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG){
							//产生网络中断告警
						occouredEmsAlarm(connection,connectStatus);
						}
						emsConnection.put("EXCEPTION_REASON", errorMessage);
						try{
							dataCollectMapper
									.updateEmsConnectionByIP(emsConnection);
						}catch(Exception e){
							
						}
						break;
					}
				}
			}
		}
	}
	
	public static void recoverOperation(String corbaIp,int type){
		if(dataCollectMapper == null){
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		//获取ems信息
		Map connection = dataCollectMapper.selectEmsConnectionByIP(corbaIp, DataCollectDefine.FALSE);
		boolean isDel = (connection==null||connection.isEmpty());
		if(isDel) return;
		//更新网管状态为正常
		if(Integer.valueOf(connection.get("CONNECT_STATUS").toString()).intValue() != DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG){
			//更新网管连接状态
			Map emsConnection = new HashMap();
			emsConnection.put("IP",corbaIp);
			//更新网管连接状态--连接正常
			emsConnection.put("CONNECT_STATUS", DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG);
			
			dataCollectMapper.updateEmsConnectionByIP(emsConnection);
		}
		//清除告警
		occouredEmsAlarm(connection,DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG);
		
		if (dataCollectService == null) {
			dataCollectService = (IDataCollectService) BeanUtil
					.getBean("dataCollectService");
		}
		
		try {
			// 更新网元信息(主:在线状态)
			dataCollectService.getNeAlertList(connection,
					DataCollectDefine.COLLECT_LEVEL_1);
			// 如果内部emsName为空 即没有手动同步过，需要同步一次获取内部ems名称
			String internalEmsName = connection.get("INTERNAL_EMS_NAME") == null ? ""
					: connection.get("INTERNAL_EMS_NAME").toString();
			if (internalEmsName.isEmpty()) {
				dataCollectService.syncEmsInfo(connection,
						DataCollectDefine.COLLECT_LEVEL_1);
			}
		}catch(Exception e){
			ExceptionHandler.handleException(e);
		}
	}
	
	public static void updateReceiveTime(String corbaIp,int type) {
		Map<String,Date> timeMap=null;
		if(type==CHECK_PING){
			timeMap=EMSCollectService.pingReceiveTime;
		}else {
			timeMap=EMSCollectService.heartBeatReceiveTime;
		}
		Date date=timeMap.get(corbaIp);
		// 更新接收时间
		if(type==CHECK_PING){
			EMSCollectService.pingReceiveTime.put(corbaIp, new Date());
		}else{
			EMSCollectService.heartBeatReceiveTime.put(corbaIp, new Date());
		}
//		timeMap.put(corbaIp, new Date());
		if(date!=null){
			date = CommonUtil.getSpecifiedDay(date, 0, AutoCheckConnection.CHECK_TIME_OUT);
			if(date.before(new Date())){
				// 手动连接后检测超时, 进行恢复操作
				recoverOperation(corbaIp,type);
			}
		}else{
			recoverOperation(corbaIp,type);
		}
	}
	
	//产生网管中断告警
	private static void occouredEmsAlarm(Map connection,Integer exceptionType){
		//网管连接异常告警入库
		if (faultManagerService == null) {
			faultManagerService = (IFaultManagerService) BeanUtil
					.getBean("faultManagerService");
		}
		List<AlarmDataModel> alarmList = constructEmsAlarm((Integer) connection
				.get("BASE_EMS_CONNECTION_ID"),
				(Integer) connection.get("FACTORY"),exceptionType);
		// 告警模块入库
		try {
			faultManagerService.alarmDataToMongodb(alarmList, Integer
					.valueOf(connection.get("BASE_EMS_CONNECTION_ID")
							.toString()), null,
					DataCollectDefine.ALARM_TO_DB_TYPE_PUSH);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	//生成网管连接告警
	private static List<AlarmDataModel> constructEmsAlarm(Integer emsConnectionId,Integer factory,Integer exceptionType){
		List<AlarmDataModel> alarmList = new ArrayList<AlarmDataModel>();
		
		//是否需要产生告警参数
		boolean needOccouredAlarm = true;
		//FIXME 暂时注释用来测试corba重连概率
		if(EMSCollectService.needOccouredAlarmMap.containsKey(emsConnectionId)){
			needOccouredAlarm = EMSCollectService.needOccouredAlarmMap.get(emsConnectionId);
		}
		
		AlarmDataModel model = new AlarmDataModel();
		SimpleDateFormat format = CommonUtil.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
		model.setEmsId(emsConnectionId);
		model.setAlarmSerialNo("EMS_COMMU_BREAK");
		model.setObjectType(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EMS);
		model.setObjectName(new NameAndStringValue_T[]{});
		model.setFactory(factory);
		switch(exceptionType.intValue()){
		case DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG:
			model.setPerceivedSeverity(DataCollectDefine.ALARM_PS_CLEARED);
			EMSCollectService.needOccouredAlarmMap.put(emsConnectionId, true);
			break;
		case DataCollectDefine.CONNECT_STATUS_INTERRUPT_FLAG:
		case DataCollectDefine.CONNECT_STATUS_EXCEPTION_FLAG:
			model.setPerceivedSeverity(DataCollectDefine.ALARM_PS_CRITICAL);
			EMSCollectService.needOccouredAlarmMap.put(emsConnectionId, false);
			break;
		}
		model.setAlarmReason("网管连接中断/异常！");
		model.setNativeProbableCause("网管连接中断/异常！");
		model.setProbableCauseQualifier("网管连接中断/异常！");
		model.setEmsTime(format.format(new Date()));
		model.setNeTime(format.format(new Date()));
		model.setClearTime(format.format(new Date()));
		
		if(needOccouredAlarm){
		alarmList.add(model);
		}
		return alarmList;
	}
}
