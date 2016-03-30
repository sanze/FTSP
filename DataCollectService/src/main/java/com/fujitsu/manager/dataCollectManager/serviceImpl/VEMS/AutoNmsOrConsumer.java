package com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba.ALUConsumerImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba.ALUNmsSessionImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba.FIMConsumerImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba.FIMNmsSessionImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba.HWConsumerImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba.HWNmsSessionImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba.LUCENTConsumerImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba.LUCENTNmsSessionImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba.ZTEU31NmsSessionImpl;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba.ZTEU31ConsumerImpl;
import com.fujitsu.util.BeanUtil;

/**
 * @author zhuangjieliang
 * 
 */
public class AutoNmsOrConsumer extends TimerTask {

	public static int CHECK_NOTIFY = 0;
	public static int CHECK_PING = 1;
	
	private static DataCollectMapper dataCollectMapper;

	// 定时器 循环间隔时间 单位：秒
	public static int SCHEDULE_TIME = 30;
	public static int DEALY_NOTIFY_TIME = 5;
	public static int DEALY_PING_TIME = 10;

	private int FACTORY;
	private int type;
	private String corbaName;
	private String corbaPassword;
	private String corbaIp;
	private String corbaPort;
	private String emsName;
	private String encode;

	public AutoNmsOrConsumer(int FACTORY, int type,String corbaIp) {
		this.FACTORY = FACTORY;
		this.type = type;
		this.corbaIp = corbaIp;
	}

	public void run() {
		
		if(dataCollectMapper == null){
			dataCollectMapper = (DataCollectMapper) BeanUtil
					.getBean("dataCollectMapper");
		}
		//获取ems信息
		Map connection = dataCollectMapper.selectEmsConnectionByIP(corbaIp, DataCollectDefine.FALSE);
		//如果连接已删除
		if(connection == null){
			//取消心跳通知定时器
			if(VEMSSession.notifyTimerMap.get(corbaIp)!=null){
				VEMSSession.notifyTimerMap.get(corbaIp).cancel();
			}
			//移除心跳通知定时器
			VEMSSession.notifyTimerMap.remove(corbaIp);
			
			//取消ping通知定时器
			if(VEMSSession.pingTimerMap.get(corbaIp)!=null){
				VEMSSession.pingTimerMap.get(corbaIp).cancel();
			}
			//移除ping通知定时器
			VEMSSession.pingTimerMap.remove(corbaIp);

			return;
		}
		
		this.emsName = (String) connection.get("EMS_NAME");
		this.corbaName = (String) connection.get("USER_NAME");
		this.corbaPassword = (String) connection.get("PASSWORD");
		this.corbaPort = (String) connection.get("PORT");
		//编码信息
		this.encode = (String) connection.get("ENCODE");
		if (type == CHECK_NOTIFY) {
			Object consumer = EMSCollectService.pushSupplierMap.get(this.corbaIp);
			if(consumer==null){
				VEMSSession.notifyTimerMap.get(corbaIp).cancel();
				VEMSSession.notifyTimerMap.remove(corbaIp);
				return;
			}
			push_structured_event(consumer);
		} else if (type == CHECK_PING) {
			System.out.println(new Date() + " VEMS ping连接！" + " 【" + corbaIp + "】 ");
			Object nmsSession = EMSCollectService.sessionMap.get(this.corbaIp).getNmsSession();
			if(nmsSession == null){
				VEMSSession.pingTimerMap.get(corbaIp).cancel();
				VEMSSession.pingTimerMap.remove(corbaIp);
				return;
			}
			ping(nmsSession);
		}
	}
	
	private void push_structured_event(Object consumer){
		VEMSSession session = new VEMSSession(corbaName, corbaPassword, corbaIp,
				corbaPort, emsName, encode, FACTORY);
		try {
			ArrayList<Object> events = session.getNotifications();
			if(events.size()>0){
				System.out.println(new Date() + " VEMS 推送通知！" + " 【" + corbaIp + "】 ");
			}
			if (DataCollectDefine.FACTORY_FIBERHOME_FLAG==FACTORY) {
				for(Object notification:events){
					((FIMConsumerImpl)consumer).push_structured_event(
							(FENGHUO.CosNotification.StructuredEvent)notification);
				}
			} else if (DataCollectDefine.FACTORY_HW_FLAG==FACTORY) {
				for(Object notification:events){
					((HWConsumerImpl)consumer).push_structured_event(
							(HW.CosNotification.StructuredEvent)notification);
				}
			} else if (DataCollectDefine.FACTORY_ZTE_FLAG==FACTORY) {
				for(Object notification:events){
					((ZTEU31ConsumerImpl)consumer).push_structured_event(
							(ZTE_U31.CosNotification.StructuredEvent)notification);
				}
			} else if (DataCollectDefine.FACTORY_LUCENT_FLAG==FACTORY) {
				for(Object notification:events){
					((LUCENTConsumerImpl)consumer).push_structured_event(
							(LUCENT.CosNotification.StructuredEvent)notification);
				}
			} else if (DataCollectDefine.FACTORY_ALU_FLAG==FACTORY) {
				for(Object notification:events){
					((ALUConsumerImpl)consumer).push_structured_events(
						new org.omg.CosNotification.StructuredEvent[]{
							(org.omg.CosNotification.StructuredEvent)notification
						});
				}
			} else {
				return;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void ping(Object nmsSession) {
		if (DataCollectDefine.FACTORY_FIBERHOME_FLAG==FACTORY) {
			((FIMNmsSessionImpl)nmsSession).ping();
		} else if (DataCollectDefine.FACTORY_HW_FLAG==FACTORY) {
			((HWNmsSessionImpl)nmsSession).ping();
		} else if (DataCollectDefine.FACTORY_ZTE_FLAG==FACTORY) {
			((ZTEU31NmsSessionImpl)nmsSession).ping();
		} else if (DataCollectDefine.FACTORY_LUCENT_FLAG==FACTORY) {
			((LUCENTNmsSessionImpl)nmsSession).ping();
		} else if (DataCollectDefine.FACTORY_ALU_FLAG==FACTORY) {
			((ALUNmsSessionImpl)nmsSession).ping();
		} else {
			return;
		}
	}
}
