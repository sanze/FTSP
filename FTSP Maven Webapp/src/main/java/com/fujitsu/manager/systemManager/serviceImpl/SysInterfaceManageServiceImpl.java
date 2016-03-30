package com.fujitsu.manager.systemManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.SysInterfaceManageMapper;
import com.fujitsu.manager.systemManager.service.SysInterfaceManageService;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
@Service
@Transactional(rollbackFor = Exception.class)
public class SysInterfaceManageServiceImpl extends SysInterfaceManageService{
	@Resource
	private SysInterfaceManageMapper interfaceManageMapper;
	@Autowired
	private Mongo mongo;
	/**
	 * 
	 * Method name: getSysInterfaceManageData <BR>
	 * Description: 查询系统接口 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@Override
	public Map<String, Object> getSysInterfaceManageData(int start, int limit)
			throws CommonException {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> returnMap=new HashMap<String, Object>();
		List<Map<String, Object>> enigneerList = new ArrayList<Map<String, Object>>();
		int total=interfaceManageMapper.countInterfaceDataList(map);
		map.put("startNumber", start);
		map.put("pageSize", limit);
		enigneerList=interfaceManageMapper.selectInterfaceDataList(map);
		returnMap.put("rows", enigneerList);
		returnMap.put("total", total);
		return returnMap;
	}

	@Override
	public void addInterface(Map<String, Object> paramMap)
			throws CommonException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = sf.format(new Date());
		paramMap.put("CREATE_TIME", createTime);
		interfaceManageMapper.addInterface(paramMap);
		
	}

	@Override
	public void modifyInterface(Map<String, Object> paramMap)
			throws CommonException {
		interfaceManageMapper.modifyInterface(paramMap);
		
	}

	@Override
	public void deleteInterface(Map<String, Object> paramMap)
			throws CommonException {
		String[] InterfaceArr = paramMap.get("sysInterfaceIds").toString().split(",");
		for (int i = 0; i < InterfaceArr.length; i++) {
			interfaceManageMapper.deleteInterface(Integer.parseInt(InterfaceArr[i]));
		}
		
	}

	@Override
	public Map<String, Object> getDetailById(Map<String, Object> paramMap)
			throws CommonException {
		Map<String, Object> alarmNormlizedMap = interfaceManageMapper.getDetailById(Integer.parseInt(paramMap.get("ID").toString()));
		return alarmNormlizedMap;
	}

	@Override
	public int checkConnection(Map<String, Object> map) {
		int count =  interfaceManageMapper.checkConnection(map);
		return count;
	}


/*	@Override
	public void AlarmSend(Map<String, Object> alarm) {
		if(!CommonDefine.IP_MAP.isEmpty()){//IP非空要推送
			List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
			list.add(alarm);
			String xmls =mapToXml(list);
	        System.out.println(xmls);
			Set<Map.Entry<String, Integer>> set = CommonDefine.IP_MAP.entrySet();
	        for (Iterator<Map.Entry<String, Integer>> it = set.iterator(); it.hasNext();) {
	            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
	            System.out.println(entry.getKey() + "--->" + entry.getValue());
	            AlarmSendThread sendThread = new AlarmSendThread(entry.getKey(), entry.getValue(),xmls);
	            Thread thread = new Thread(sendThread);
	            thread.start();
	        }
	    }else{
	    	System.out.println("没有处于连接状态的源IP！不推送综告！");
	    }
	
		
	}*/

	
	
	private String mapToXml(List<Map<String,Object>> list) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><alarmList>");
		for (Map<String, Object> alarm : list) {
			buffer.append("<alarm>");
			buffer.append("<_id>"+alarm.get("_id")+"</_id>");
			buffer.append("<INTERFACE_RATE>"+alarm.get("INTERFACE_RATE")+"</INTERFACE_RATE>");
			buffer.append("<DIRECTION>"+alarm.get("DIRECTION")+"</DIRECTION>");
			buffer.append("<SLOT_NO>"+alarm.get("SLOT_NO")+"</SLOT_NO>");
			buffer.append("<ALARM_TYPE>"+alarm.get("ALARM_TYPE")+"</ALARM_TYPE>");
			buffer.append("<NATIVE_PROBABLE_CAUSE>"+alarm.get("NATIVE_PROBABLE_CAUSE")+"</NATIVE_PROBABLE_CAUSE>");
			buffer.append("<UNIT_NAME>"+alarm.get("UNIT_NAME")+"</UNIT_NAME>");
			buffer.append("<PROBABLE_CAUSE>"+alarm.get("PROBABLE_CAUSE")+"</PROBABLE_CAUSE>");
			buffer.append("<PTP_ID>"+alarm.get("PTP_ID")+"</PTP_ID>");
			buffer.append("<CREATE_TIME>"+alarm.get("CREATE_TIME")+"</CREATE_TIME>");
			buffer.append("<PROBABLE_CAUSE_QUALIFIER>"+alarm.get("PROBABLE_CAUSE_QUALIFIER")+"</PROBABLE_CAUSE_QUALIFIER>");
			buffer.append("<IS_CLEARABLE>"+alarm.get("IS_CLEARABLE")+"</IS_CLEARABLE>");
			buffer.append("<LAYER_RATE>"+alarm.get("LAYER_RATE")+"</LAYER_RATE>");
			buffer.append("<FILTER_ALARM_NAME_FACTORY>"+alarm.get("FILTER_ALARM_NAME_FACTORY")+"</FILTER_ALARM_NAME_FACTORY>");
			buffer.append("<CONFIRM_STATUS_ORI>"+alarm.get("CONFIRM_STATUS_ORI")+"</CONFIRM_STATUS_ORI>");
			buffer.append("<RESOURCE_ROOM_ID>"+alarm.get("RESOURCE_ROOM_ID")+"</RESOURCE_ROOM_ID>");
			buffer.append("<NE_ID>"+alarm.get("NE_ID")+"</NE_ID>");
			buffer.append("<ACK_TIME>"+alarm.get("ACK_TIME")+"</ACK_TIME>");
			buffer.append("<CLEAR_ALARM_ID>"+alarm.get("CLEAR_ALARM_ID")+"</CLEAR_ALARM_ID>");
			buffer.append("<PERCEIVED_SEVERITY>"+alarm.get("PERCEIVED_SEVERITY")+"</PERCEIVED_SEVERITY>");
			buffer.append("<OBJECT_NAME>"+alarm.get("OBJECT_NAME")+"</OBJECT_NAME>");
			buffer.append("<SHELF_ID>"+alarm.get("SHELF_ID")+"</SHELF_ID>");
			buffer.append("<STATION_ID>"+alarm.get("STATION_ID")+"</STATION_ID>");
			buffer.append("<NE_NAME>"+alarm.get("NE_NAME")+"</NE_NAME>");
			buffer.append("<DISPLAY_STATION>"+alarm.get("DISPLAY_STATION")+"</DISPLAY_STATION>");
			buffer.append("<CLEAR_TIME>"+alarm.get("CLEAR_TIME")+"</CLEAR_TIME>");
			buffer.append("<UPDATE_TIME>"+alarm.get("UPDATE_TIME")+"</UPDATE_TIME>");
			buffer.append("<FIRST_TIME>"+alarm.get("FIRST_TIME")+"</FIRST_TIME>");
			buffer.append("<AMOUNT>"+alarm.get("AMOUNT")+"</AMOUNT>");
			buffer.append("<SUBNET_NAME>"+alarm.get("SUBNET_NAME")+"</SUBNET_NAME>");
			buffer.append("<IS_ACK>"+alarm.get("IS_ACK")+"</IS_ACK>");
			buffer.append("<NOTIFICATION_ID>"+alarm.get("NOTIFICATION_ID")+"</NOTIFICATION_ID>");
			buffer.append("<IS_ORDER>"+alarm.get("IS_ORDER")+"</IS_ORDER>");
			buffer.append("<SLOT_DISPLAY_NAME>"+alarm.get("SLOT_DISPLAY_NAME")+"</SLOT_DISPLAY_NAME>");
			buffer.append("<NE_TYPE>"+alarm.get("NE_TYPE")+"</NE_TYPE>");
			buffer.append("<OBJECT_TYPE_QUALIFIER>"+alarm.get("OBJECT_TYPE_QUALIFIER")+"</OBJECT_TYPE_QUALIFIER>");
			buffer.append("<ALARM_REASON>"+alarm.get("ALARM_REASON")+"</ALARM_REASON>");
			buffer.append("<CTP_NAME>"+alarm.get("CTP_NAME")+"</CTP_NAME>");
			buffer.append("<ACK_USER>"+alarm.get("ACK_USER")+"</ACK_USER>");
			buffer.append("<CONTINUE_TIME>"+alarm.get("CONTINUE_TIME")+"</CONTINUE_TIME>");
			buffer.append("<SHELF_NO>"+alarm.get("SHELF_NO")+"</SHELF_NO>");
			buffer.append("<SUBNET_ID>"+alarm.get("SUBNET_ID")+"</SUBNET_ID>");
			buffer.append("<EMS_TIME>"+alarm.get("EMS_TIME")+"</EMS_TIME>");
			buffer.append("<PRODUCT_NAME>"+alarm.get("PRODUCT_NAME")+"</PRODUCT_NAME>");
			buffer.append("<FLASH_ALARM_ID>"+alarm.get("FLASH_ALARM_ID")+"</FLASH_ALARM_ID>");
			buffer.append("<EMS_NAME>"+alarm.get("EMS_NAME")+"</EMS_NAME>");
			buffer.append("<INSPECT_ENGINEER_ID>"+alarm.get("INSPECT_ENGINEER_ID")+"</INSPECT_ENGINEER_ID>");
			buffer.append("<OBJECT_TYPE>"+alarm.get("OBJECT_TYPE")+"</OBJECT_TYPE>");
			buffer.append("<AREA_ID>"+alarm.get("AREA_ID")+"</AREA_ID>");
			buffer.append("<BASE_EMS_GROUP_ID>"+alarm.get("BASE_EMS_GROUP_ID")+"</BASE_EMS_GROUP_ID>");
			buffer.append("<FACTORY>"+alarm.get("FACTORY")+"</FACTORY>");
			buffer.append("<INSPECT_ENGINEER>"+alarm.get("INSPECT_ENGINEER")+"</INSPECT_ENGINEER>");
			buffer.append("<DOMAIN>"+alarm.get("DOMAIN")+"</DOMAIN>");
			buffer.append("<LOCATION_INFO>"+alarm.get("LOCATION_INFO")+"</LOCATION_INFO>");
			buffer.append("<VENDOR_PROBABLE_CAUSE>"+alarm.get("VENDOR_PROBABLE_CAUSE")+"</VENDOR_PROBABLE_CAUSE>");
			buffer.append("<DISPLAY_AREA>"+alarm.get("DISPLAY_AREA")+"</DISPLAY_AREA>");
			buffer.append("<EMS_ID>"+alarm.get("EMS_ID")+"</EMS_ID>");
			buffer.append("<PTP_TYPE>"+alarm.get("PTP_TYPE")+"</PTP_TYPE>");
			buffer.append("<NORMAL_CAUSE>"+alarm.get("NORMAL_CAUSE")+"</NORMAL_CAUSE>");
			buffer.append("<SERVICE_AFFECTING>"+alarm.get("SERVICE_AFFECTING")+"</SERVICE_AFFECTING>");
			buffer.append("<HANDLING_SUGGESTION>"+alarm.get("HANDLING_SUGGESTION")+"</HANDLING_SUGGESTION>");
			buffer.append("<FILTER_NE_MODEL_FACTORY>"+alarm.get("FILTER_NE_MODEL_FACTORY")+"</FILTER_NE_MODEL_FACTORY>");
			buffer.append("<SUB_UNIT_ID>"+alarm.get("SUB_UNIT_ID")+"</SUB_UNIT_ID>");
			buffer.append("<UNIT_ID>"+alarm.get("UNIT_ID")+"</UNIT_ID>");
			buffer.append("<NATIVE_EMS_NAME>"+alarm.get("NATIVE_EMS_NAME")+"</NATIVE_EMS_NAME>");
			buffer.append("<IS_CLEAR>"+alarm.get("IS_CLEAR")+"</IS_CLEAR>");
			buffer.append("<NE_TIME>"+alarm.get("NE_TIME")+"</NE_TIME>");
			buffer.append("<RESOURCE_ROOM>"+alarm.get("RESOURCE_ROOM")+"</RESOURCE_ROOM>");
			buffer.append("<LOCATION>"+alarm.get("LOCATION")+"</LOCATION>");
			buffer.append("<PORT_NO>"+alarm.get("PORT_NO")+"</PORT_NO>");
			buffer.append("<EMS_GROUP_NAME>"+alarm.get("EMS_GROUP_NAME")+"</EMS_GROUP_NAME>");
			buffer.append("<RACK_NO>"+alarm.get("RACK_NO")+"</RACK_NO>");
			buffer.append("<CLEAR_STATUS>"+alarm.get("CLEAR_STATUS")+"</CLEAR_STATUS>");
			buffer.append("</alarm>");
		}
		buffer.append("</alarmList></request>");	
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCurrentAlarms(Map<String, Object> map) {

		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		DBCursor currentAlarm = conn.find();
		while (currentAlarm.hasNext()) {
			DBObject dbo = currentAlarm.next();
			dbo.put("FIRST_TIME", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
			dbo.put("UPDATE_TIME", "".equals(dbo.get("UPDATE_TIME"))?"":sf.format(dbo.get("UPDATE_TIME")));
			dbo.put("CLEAR_TIME", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
			dbo.put("ACK_TIME", "".equals(dbo.get("ACK_TIME"))?"":sf.format(dbo.get("ACK_TIME")));
			dbo.put("NE_TIME", "".equals(dbo.get("NE_TIME"))?"":sf.format(dbo.get("NE_TIME")));
			dbo.put("EMS_TIME", "".equals(dbo.get("EMS_TIME"))?"":sf.format(dbo.get("EMS_TIME")));
			dbo.put("CREATE_TIME", "".equals(dbo.get("CREATE_TIME"))?"":sf.format(dbo.get("CREATE_TIME")));
			list.add(dbo.toMap());
		}
		return mapToXml(list);
	
	}

	@Override
	public Map<String,Object> checkInterface(Map<String, Object> paramMap)
			throws CommonException {
		int flag=1;
		int count=0;
		String message="";
		if(paramMap.get("ID")!=null&&!"".equals(paramMap.get("ID").toString())){//修改
			//判断接口名称
			count = interfaceManageMapper.checkInterfaceName(paramMap);
			if(count>0){
				flag=0;
				message+="接口名称不唯一,";
			}
			//判断IP+端口
			count = interfaceManageMapper.checkInterfaceIPPort(paramMap);
			if(count>0){
				flag=0;
				message+="IP和端口不唯一,";
			}
			//判断peer
			count = interfaceManageMapper.checkInterfacePeer(paramMap);
			if(count>0){
				flag=0;
				message+="对端IP不唯一,";
			}
		}else{//新增
			//判断接口名称
			count = interfaceManageMapper.checkInterfaceName(paramMap);
			if(count>0){
				flag=0;
				message+="接口名称不唯一,";
			}
			//判断IP+端口
			count = interfaceManageMapper.checkInterfaceIPPort(paramMap);
			if(count>0){
				flag=0;
				message+="IP和端口不唯一,";
			}
			//判断peer
			count = interfaceManageMapper.checkInterfacePeer(paramMap);
			if(count>0){
				flag=0;
				message+="对端IP不唯一,";
			}
		}
		message+="请修改";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("FLAG", flag);
		map.put("MESSAGE", message);
		return map;
	}

}
