package com.fujitsu.activeMq;

//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import net.sf.json.JSONObject;

import org.springframework.jms.listener.SessionAwareMessageListener;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.faultManager.serviceImpl.AlarmManagementServiceImpl;
import com.fujitsu.model.AlarmMessageModel;
import com.fujitsu.model.MessageModel;
import com.fujitsu.util.WebMsgPush;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

/**
 * 
 * 消息接收监听器。
 */
public class JMSReceiver implements SessionAwareMessageListener {
//	private ISysInterfaceManageService interfaceManageService;
//	private ICutoverManagerService cutoverManagerService;
	
	@Resource
	public WebMsgPush webMsgPush; 
	@Resource
	private CommonManagerMapper commonMapper;
	@Resource
	private AlarmManagementServiceImpl alarmManagement;
	
	private static MessageBroker messageBroker;
	
	public void onMessage(Message message, Session session) throws JMSException {

//		System.out.println("JMSReceiver onMessage");
		
		try {
			// 1.接收报文
			MessageModel messageModel = (MessageModel) ((ObjectMessage) message)
					.getObject();
			
			// 判断消息类型
			switch (messageModel.getMessageType()) {
			case CommonDefine.MESSAGE_TYPE_ALARM:
				//消息处理
        		AlarmMessageModel alarm = AlarmMessageModel.from((Map)messageModel.getMessage());
        		//推送topo前台
        		handlerAlarmMessageForTopo(alarm);
        		// GIS地图中的光缆段告警推送处理
            	alarmManagement.analyseCableAlarmForGis((Map)messageModel.getMessage());
        		break;
			case CommonDefine.MESSAGE_TYPE_EMS_CONN_STATUS:
				//消息处理
        		AlarmMessageModel emsConnStatus = AlarmMessageModel.from((Map)messageModel.getMessage());
        		//推送topo前台
        		handlerEMSConnStatusForTopo(emsConnStatus);
        		break;
			case CommonDefine.MESSAGE_TYPE_FAULT:
				webMsgPush.updateFaultMsg();
				break;
			case CommonDefine.MESSAGE_TYPE_CABLE_TEST:
				handlerTestMessage((Map) messageModel.getMessage());
				break;
			case CommonDefine.MESSAGE_TYPE_BREAK_POINT:
				handlerBreakPointMsg((Map) messageModel.getMessage());
				break;
//			case CommonDefine.MESSAGE_TYPE_ALARM_COM:
				//告警推送
//				JSONObject jsonObject = JSONObject.fromObject(messageModel.getMessage().toString());
//				// 将JSON对象转成Map对象
//				Map<String, Object> paramMap = new HashMap<String, Object>();
//				paramMap = (Map<String, Object>) jsonObject;
//				handlerAlarmToWS(paramMap);
//				break;
			}

			// 2.解析报文，建议此处使用工具类解析

			// 3.调用接口，处理业务，建议此处使用“抽象工厂模式”调用内部接口，以使代码符合“开-闭原则”

			// 4.发送返回消息(可省略)
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	//处理告警消息
    public static void handlerAlarmMessageForTopo(AlarmMessageModel alarm){
    	
    	if(messageBroker == null){
    	//获取消息代理，此处的参数就是Spring配置文件中配置的messagebroker的id
    		messageBroker = MessageBroker.getMessageBroker("_messageBroker");
		if(messageBroker == null){
			return;
		}
    	}
		String clientID = UUIDUtils.createUUID();
		AsyncMessage asynMsg = new AsyncMessage();
		//设置消息的地址，这个必须跟Spring配置文件中信道的destination一致
		asynMsg.setDestination("market-data-feed");
//		asynMsg.setHeader("message", "new");
		asynMsg.setClientId(clientID);
		asynMsg.setMessageId(UUIDUtils.createUUID());
		asynMsg.setTimestamp(System.currentTimeMillis());
		asynMsg.setBody(alarm);
		messageBroker.routeMessageToService(asynMsg, null);
    }
    
    //处理告警消息
    public static void handlerEMSConnStatusForTopo(AlarmMessageModel alarm){
    	
    	if(messageBroker == null){
    	//获取消息代理，此处的参数就是Spring配置文件中配置的messagebroker的id
    		messageBroker = MessageBroker.getMessageBroker("_messageBroker");
		if(messageBroker == null){
			return;
		}
    	}
		String clientID = UUIDUtils.createUUID();
		AsyncMessage asynMsg = new AsyncMessage();
		//设置消息的地址，这个必须跟Spring配置文件中信道的destination一致
		asynMsg.setDestination("ems-connection-status");
//		asynMsg.setHeader("message", "new");
		asynMsg.setClientId(clientID);
		asynMsg.setMessageId(UUIDUtils.createUUID());
		asynMsg.setTimestamp(System.currentTimeMillis());
		asynMsg.setBody(alarm);
		messageBroker.routeMessageToService(asynMsg, null);
    }
    
    public void handlerTestMessage(Map<String,Object> msgMap) {
    	// 测试路由ID和状态
		int routeId = Integer.valueOf(msgMap.get("TEST_ROUTE_ID").toString());
		int state = Integer.valueOf(msgMap.get("STATUS").toString());
		// 获取测试路由的光缆段ID
		Map<String,Object> testMap = commonMapper.selectTableById("t_ftts_test_route", "TEST_ROUTE_ID", routeId);
		String cableIds = testMap.get("CABLE_IDS").toString();
		Map<String,Object> map = new HashMap<String,Object>();
		// 组织推送信息
		map.put("dataType", CommonDefine.Gis.TEST_CABLE_GIS);
		map.put("id", cableIds);
		if (state == CommonDefine.ROUTE_STATUS_OCCUPY) {
			map.put("state", CommonDefine.LINE_TESTING);
		} else {
			map.put("state", CommonDefine.LINE_ORDINARY);
		}
		System.out.println("接收到光缆测试的JMS消息..."+state+"@"+cableIds);
		WebMsgPush.updateGisMap(JSONObject.fromObject(map));
    }
    
    // 处理光缆测试中的断点信息消息
    public void handlerBreakPointMsg(Map<String,Object> msgMap) {
    	String cableIds = msgMap.get("CABLE_IDS").toString();
		// 需要检查的光缆段
		String[] ids = cableIds.split(",");
		// 循环推送所有光缆的断点状态
		for (String csId : ids) {
			Map<String,Object> cs = commonMapper.selectTableById("t_resource_cable", "RESOURCE_CABLE_ID", Integer.valueOf(csId));
			String bp = cs.get("BREAKPOINT").toString();
			Map<String,Object> breakpoint = new HashMap<String,Object>();
			breakpoint.put("dataType", 0);
			breakpoint.put("id", csId);
			if (bp.equals("0")) {
				// 正常
				breakpoint.put("state", false);
			} else {
				// 有断点，添加断点座标信息
				breakpoint.put("state", true);
				breakpoint.put("lng", bp.substring(0, bp.indexOf(',')));
				breakpoint.put("lat", bp.substring(bp.indexOf(',')+1,bp.length()));
			}
			// 推送断点信息
			WebMsgPush.updateGisMap(JSONObject.fromObject(breakpoint));
		}			
    }
    
    //处理告警消息，推送到综告系统
/*    public void handlerAlarmToWS1(Map<String, Object> paramMap)
			throws CommonException, ParseException {
    	boolean flag =true;
    	if(!"".equals(paramMap.get("NE_ID").toString())){
			cutoverManagerService =(ICutoverManagerService) SpringContextUtil.getBean("cutoverManagerServiceImpl");
			List<FilterAlarmParametersModel> list = cutoverManagerService.getFilterAlarmParameters(Integer.parseInt(paramMap.get("NE_ID").toString()));
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date =new Date();
			for(FilterAlarmParametersModel ml:list){
				if(ml.getStartTime()!=null&&!"".equals(ml.getStartTime())){//开始时间不为空
					if(date.getTime()>sf.parse(ml.getStartTime()).getTime()){//当前时间大于开始时间
						if(ml.getEndTime()!=null&&!"".equals(ml.getEndTime())){//结束时间不为空
							if(date.getTime()<sf.parse(ml.getEndTime()).getTime()){//结束时间大于当前时间
								flag=false;
								break;
							}
						}else{//结束时间为空
							flag=false;
							break;
						}
					}
				}else{//开始时间为空
					if(ml.getEndTime()!=null&&!"".equals(ml.getEndTime())){//结束时间不为空
						if(date.getTime()<sf.parse(ml.getEndTime()).getTime()){//结束时间大于当前时间
							flag=false;
							break;
						}
					}else{
						flag=false;
						break;
					}
				}
			}
    	}
		if(flag){
			interfaceManageService =(ISysInterfaceManageService) SpringContextUtil.getBean("sysInterfaceManageServiceImpl");
			interfaceManageService.AlarmSend(paramMap);
		};
	}*/
}
