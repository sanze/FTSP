package com.fujitsu.common;

import java.util.HashMap;
import java.util.Map;


/**
 * @author xuxiaojun
 * 
 */
public class CommonDefine extends BaseDefine{

	/*=========================GIS资源状态=====================*/
	//光缆段cableSection
	public final static int ALL_COVERED_BY_ROUTE = 0;
	public final static int SOME_COVERED_BY_ROUTE = 1;
	public final static int LINE_ORDINARY = 2;
	public final static int LINE_TESTING = 7;
	public final static int LINE_WITH_ALARM = 4;
	//机房station
	public final static int STATION_NOALARM = 3;
	
	/*=========================GIS资源状态=====================*/

	//11 昕天卫 12 中博
	public static final int EQUIP_FACTORY_XTW = 11;
	public static final int EQUIP_FACTORY_ZB = 12;
	//设备类型
	public static final int EQUIP_TYPE_RTU = 111;
	public static final int EQUIP_TYPE_CTU = 112;
	//otdr类型
	public static final int OTDR_TYPE_CHINA = 1111;
	public static final int OTDR_TYPE_ANRITUS = 1112;
	//采集等级
	public static final int COLLECT_LEVEL_1 = 1;
	public static final int COLLECT_LEVEL_2 = 2;
	public static final int COLLECT_LEVEL_3 = 3;
	public static final int COLLECT_LEVEL_4 = 4;
	
	//1.连接正常 2.连接异常 3.网络中断  4.连接中断
	public final static int CONNECT_STATUS_NORMAL_FLAG = 1;
	public final static int CONNECT_STATUS_EXCEPTION_FLAG = 2;
	public final static int CONNECT_STATUS_INTERRUPT_FLAG = 3;
	public final static int CONNECT_STATUS_DISCONNECT_FLAG = 4;
	
	//告警监听打开
	public static final int ALARM_TRIGGER_ON = 1;
	public static final int ALARM_TRIGGER_OFF = 0;
	//计划挂起
	public static final int PLAN_STARTUP = 0;
	public static final int PLAN_PENDING = 1;
	//测试类型
	public static final int TEST_TYPE_TRGGER = 1;
	public static final int TEST_TYPE_MANUAL = 2;
	public static final int TEST_TYPE_REGULAR = 3;
	//路由占用状态：0空闲，1占用
	public final static int ROUTE_STATUS_FREE = 0;
	public final static int ROUTE_STATUS_OCCUPY = 1;
	
	//测量结果评估
	public final static int EVALUATION_NORMAL = 0;  //正常
	public final static int EVALUATION_MINOR = 1;   //一般预警
	public final static int EVALUATION_MAJOR = 2;   //重要预警
	public final static int EVALUATION_INVALID = 9; //异常数据
	
	//测试结果中距离值的小数位保留个数
	public final static int DISTANCE_NUMBER_AFTER_POINT = 3;
	
	// 设备序列号编排规则
	//0:代表此值无效或者设备没有这个盘、槽道等，仅用来占位。
	public static HashMap<String, String> EQUIP_SERIAL_NO_MAP = new HashMap<String, String>();
	static {
		EQUIP_SERIAL_NO_MAP.put("12000", "SHELF:1:0");   
		EQUIP_SERIAL_NO_MAP.put("17000", "PWR:1:0");  
		EQUIP_SERIAL_NO_MAP.put("11000", "MCU:1:0");  
		EQUIP_SERIAL_NO_MAP.put("15104", "OSW:2:4");  
		EQUIP_SERIAL_NO_MAP.put("15108", "OSW:2:8");  
		EQUIP_SERIAL_NO_MAP.put("15116", "OSW:2:16"); 
		EQUIP_SERIAL_NO_MAP.put("15132", "OSW:3:32"); 
		EQUIP_SERIAL_NO_MAP.put("15164", "OSW:4:64"); 
		EQUIP_SERIAL_NO_MAP.put("15204", "OSW:2:4");  
		EQUIP_SERIAL_NO_MAP.put("15208", "OSW:2:8");  
		EQUIP_SERIAL_NO_MAP.put("15216", "OSW:2:16"); 
		EQUIP_SERIAL_NO_MAP.put("15232", "OSW:3:32"); 
		EQUIP_SERIAL_NO_MAP.put("15264", "OSW:4:64"); 
		EQUIP_SERIAL_NO_MAP.put("15304", "OSW:2:4");  
		EQUIP_SERIAL_NO_MAP.put("15308", "OSW:2:8");  
		EQUIP_SERIAL_NO_MAP.put("15316", "OSW:2:16"); 
		EQUIP_SERIAL_NO_MAP.put("15332", "OSW:3:32"); 
		EQUIP_SERIAL_NO_MAP.put("15364", "OPM:4:64"); 
		EQUIP_SERIAL_NO_MAP.put("16002", "OPM:1:2");  
		EQUIP_SERIAL_NO_MAP.put("16004", "OPM:1:4");  
		EQUIP_SERIAL_NO_MAP.put("16008", "OPM:1:8"); 
		EQUIP_SERIAL_NO_MAP.put("14101", "OTDR:2:1:Anritsu  MW9077:1310"); 
		EQUIP_SERIAL_NO_MAP.put("14201", "OTDR:2:1:Anritsu  MW9077:1550"); 
		EQUIP_SERIAL_NO_MAP.put("14301", "OTDR:2:1:Anritsu  MW9077:1625"); 
		EQUIP_SERIAL_NO_MAP.put("14401", "OTDR:2:1:Anritsu  MW9077:1310/1550"); 
		EQUIP_SERIAL_NO_MAP.put("24101", "OTDR:2:1:国产34所:1310"); 
		EQUIP_SERIAL_NO_MAP.put("24201", "OTDR:2:1:国产34所:1550"); 
		EQUIP_SERIAL_NO_MAP.put("24301", "OTDR:2:1:国产34所:1625"); 
		EQUIP_SERIAL_NO_MAP.put("24401", "OTDR:2:1:国产34所:1310/1550"); 
		EQUIP_SERIAL_NO_MAP.put("19001", "OLS:2:1");  
		EQUIP_SERIAL_NO_MAP.put("19002", "OLS:1:2");  
		EQUIP_SERIAL_NO_MAP.put("19003", "OLS:1:3"); 
		EQUIP_SERIAL_NO_MAP.put("19004", "OLS:1:4");  
		EQUIP_SERIAL_NO_MAP.put("19008", "OLS:1:8");  
		EQUIP_SERIAL_NO_MAP.put("20001", "OSM:0:1");  
		EQUIP_SERIAL_NO_MAP.put("20002", "OSM:0:2");  
		EQUIP_SERIAL_NO_MAP.put("20003", "OSM:0:3");  
		EQUIP_SERIAL_NO_MAP.put("20004", "OSM:0:4");  
		EQUIP_SERIAL_NO_MAP.put("210012", "SFM:0:12");
	}

	public final static int FAILED = 0;
	public final static int SUCCESS = 1;
	
	//=================Alarm Start=============
	
	//告警模式
	public final static String ALARM_MODE_CURRENT = "1";
	public final static String ALARM_MODE_HISTORY = "2";
	
	//机盘型号
	public final static int CARD_TYPE_PWR = 1;
	public final static int CARD_TYPE_MCU = 2;
	public final static int CARD_TYPE_OTDR = 3;
	public final static int CARD_TYPE_OSW = 4;
	
	//告警级别 0-正常 1-提示 2-一般 3-严重 4-紧急 5-离线
	public final static int ALARM_LEVEL_CL = 0;
	public final static int ALARM_LEVEL_WR = 1;
	public final static int ALARM_LEVEL_MN = 2;
	public final static int ALARM_LEVEL_MJ = 3;
	public final static int ALARM_LEVEL_CR = 4;
	
	//告警标记
	public final static int ALARM_FLAG_REMOVE = 0;
	public final static int ALARM_FLAG_PRODUCE = 1;
	
	//PWR告警内容
	public static Map<Long, String> PWR_ALARM_CONTENT = new HashMap<Long, String>();
	static{
		PWR_ALARM_CONTENT.put(1L, "单板拨出");
		PWR_ALARM_CONTENT.put(2L, "插入未配置卡");
		PWR_ALARM_CONTENT.put(3L, "-48V告警");
		PWR_ALARM_CONTENT.put(4L, "+5V告警");
		PWR_ALARM_CONTENT.put(5L, "-5V告警");
		PWR_ALARM_CONTENT.put(6L, "+12V告警");
		PWR_ALARM_CONTENT.put(9L, "插错板卡");
	}
	
	//MCU告警内容
	public static Map<Long, String> MCU_ALARM_CONTENT = new HashMap<Long, String>();
	static{
		MCU_ALARM_CONTENT.put(1L, "单板拨出");
		MCU_ALARM_CONTENT.put(2L, "插入未配置卡");
		MCU_ALARM_CONTENT.put(9L, "插错板卡");
	}
	
	//OTDR告警内容
	public static Map<Long, String> OTDR_ALARM_CONTENT = new HashMap<Long, String>();
	static{
		OTDR_ALARM_CONTENT.put(1L, "单板拔出");
		OTDR_ALARM_CONTENT.put(2L, "插入未配置卡");
		OTDR_ALARM_CONTENT.put(3L, "OTDR模块故障");
		OTDR_ALARM_CONTENT.put(9L, "插错板卡");
	}
	
	//OSW告警内容
	public static Map<Long, String> OSW_ALARM_CONTENT = new HashMap<Long, String>();
	static{
		OSW_ALARM_CONTENT.put(1L, "单板拔出");
		OSW_ALARM_CONTENT.put(2L, "插入未配置卡");
		OSW_ALARM_CONTENT.put(3L, "OSW模块故障");
		OSW_ALARM_CONTENT.put(9L, "插错板卡");
	}
	
	//PWR告警级别
	public static Map<Long, Long> PWR_ALARM_LEVEL = new HashMap<Long, Long>();
	static{
		PWR_ALARM_LEVEL.put(1L, 3L);
		PWR_ALARM_LEVEL.put(2L, 1L);
		PWR_ALARM_LEVEL.put(3L, 3L);
		PWR_ALARM_LEVEL.put(4L, 3L);
		PWR_ALARM_LEVEL.put(5L, 3L);
		PWR_ALARM_LEVEL.put(6L, 3L);
		PWR_ALARM_LEVEL.put(9L, 3L);
	}
	
	//MCU告警级别
	public static Map<Long, Long> MCU_ALARM_LEVEL = new HashMap<Long, Long>();
	static{
		MCU_ALARM_LEVEL.put(1L, 3L);
		MCU_ALARM_LEVEL.put(2L, 1L);
		MCU_ALARM_LEVEL.put(9L, 3L);
	}
	
	//OTDR告警级别
	public static Map<Long, Long> OTDR_ALARM_LEVEL = new HashMap<Long, Long>();
	static{
		OTDR_ALARM_LEVEL.put(1L, 3L);
		OTDR_ALARM_LEVEL.put(2L, 1L);
		OTDR_ALARM_LEVEL.put(3L, 3L);
		OTDR_ALARM_LEVEL.put(9L, 3L);
	}
	
	//OSW告警级别
	public static Map<Long, Long> OSW_ALARM_LEVEL = new HashMap<Long, Long>();
	static{
		OSW_ALARM_LEVEL.put(1L, 3L);
		OSW_ALARM_LEVEL.put(2L, 1L);
		OSW_ALARM_LEVEL.put(3L, 3L);
		OSW_ALARM_LEVEL.put(9L, 3L);
	}
	
	//=================Alarm End=============
	
	/* jms消息类型定义 -- start */
	// EMS告警
	public static final int MESSAGE_TYPE_ALARM = 1;
	// 故障
	public static final int MESSAGE_TYPE_FAULT = 2;
	// 网管连接信息
	public static final int MESSAGE_TYPE_EMS_CONN_STATUS = 3;
	// 光缆测试起停消息
	public static final int MESSAGE_TYPE_CABLE_TEST = 4;
	// 光缆断点更新消息
	public static final int MESSAGE_TYPE_BREAK_POINT = 5;
	/* jms消息类型定义 -- end */
	
	//告警类型
	public final static int ALARM_OBJECT_TYPE_EMS = 0;
	public final static int ALARM_OBJECT_TYPE_MANAGED_ELEMENT = 1;
	public final static int ALARM_OBJECT_TYPE_MULTILAYER_SUBNETWORK = 2;
	public final static int ALARM_OBJECT_TYPE_TOPOLOGICAL_LINK = 3;
	public final static int ALARM_OBJECT_TYPE_SUBNETWORK_CONNECTION = 4;
	public final static int ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT = 5;
	public final static int ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT = 6;
	public final static int ALARM_OBJECT_TYPE_TERMINATION_POINT_POOL = 7;
	public final static int ALARM_OBJECT_TYPE_EQUIPMENT_HOLDER = 8;
	public final static int ALARM_OBJECT_TYPE_EQUIPMENT = 9;
	public final static int ALARM_OBJECT_TYPE_PROTECTION_GROUP = 10;
	public final static int ALARM_OBJECT_TYPE_TRAFFIC_DESCRIPTOR = 11;
	public final static int ALARM_OBJECT_TYPE_AID = 12;
}
