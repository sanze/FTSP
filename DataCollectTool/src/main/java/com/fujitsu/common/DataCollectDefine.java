package com.fujitsu.common;

import java.util.HashMap;

/**
 * @author xuxiaojun
 * 
 */
public class DataCollectDefine {

	public final static int SUCCESS = 1;
	public final static int FAILED = 0;
	
	//
	public final static String FTP_IP = "ftpIp";
	public final static String FTP_USERNAME = "ftpUserName";
	public final static String FTP_PASSWORD = "ftpPassword";
	public final static String FTP_PORT = "ftpPort";
	public final static String AUTO_CONNECT_CHECK_INTERVAL_TIME ="autoConnectCheckIntervalTime";
	
	public final static String  HISTORY_PM_COMMAND_INTERVAL_TIME = "historyPmCommandIntervalTime";

	// 标识 true or false
	public final static int FALSE = 0;
	public final static int TRUE = 1;
	// 网元表中标记删除
	public final static int DELETE_FLAG = 2;

	// 同步状态--已同步
	public final static int SYNC_ALREADY_FLAG = 1;
	// 同步状态--未同步
	public final static int SYNC_NOT_FLAG = 2;
	// 同步状态--同步失败
	public final static int SYNC_FAILED_FLAG = 3;

	// 网元采集等级 1.重点采集 2.循环采集 3.不采集
	public final static int COLLECT_NE_LV_1_FLAG = 1;
	//
	public final static int COLLECT_NE_LV_2_FLAG = 2;
	//
	public final static int COLLECT_NE_LV_3_FLAG = 3;
	
	// 自动 连接模式
	public final static int CONNECT_MODE_AUTO = 0;
	// 手动 连接模式
	public final static int CONNECT_MODE_MANUAL = 1;
	
	//采集等级
	//	第一优先级：人工采集告警、人工采集性能、人工同步资源数据
	//	第二优先级：自动采集性能，自动同步告警
	//	第三优先级：巡检同步（包括资源数据、性能、告警） 
	//	第四优先级：自动同步基础数据
	public static final int COLLECT_LEVEL_1 = 1;
	public static final int COLLECT_LEVEL_2 = 2;
	public static final int COLLECT_LEVEL_3 = 3;
	public static final int COLLECT_LEVEL_4 = 4;
	
	//网管采集状态
	public static final int COLLECT_STATUS_WAITING = 1;
	public static final int COLLECT_STATUS_EXECUTING = 2;
	public static final int COLLECT_STATUS_PAUSE = 3;
	public static final int COLLECT_STATUS_FORBIDDEN = 4;
	//网元通信状态
	public static final int NE_COMMUNICATION_STATE_AVAILABLE= 0;
	public static final int NE_COMMUNICATION_STATE_UNAVAILABLE = 1;

	public static final String SYSTEM_CONFIG_FILE = "systemConfig";
	public static final String MESSAGE_CONFIG_FILE = "messageResource";

	// 编码格式
	public static final String ENCODE_ISO = "ISO-8859-1";
	public static final String ENCODE_UTF_8 = "UTF-8";
	public static final String ENCODE_GBK = "GBK";

	// corba连接状态 1.连接正常 2.连接异常 3.网络中断  4.连接中断
	public final static int CONNECT_STATUS_NORMAL_FLAG = 1;
	public final static int CONNECT_STATUS_EXCEPTION_FLAG = 2;
	public final static int CONNECT_STATUS_INTERRUPT_FLAG = 3;
	public final static int CONNECT_STATUS_DISCONNECT_FLAG = 4;

	// 任务采集性能数据类型 3.性能采集--当前性能 4.性能采集--历史性能
	public static final Long PM_COLLECT_TYPE_CURRENT_FLAG = 3L;
	public static final Long PM_COLLECT_TYPE_HISTORY_FLAG = 4L;

	//1.外部link 2.内部link
	public final static int LINK_TYPE_EXTERNAL_FLAG= 1;
	public final static int LINK_TYPE_INTERNAL_FLAG = 2;
	
	// 网管类型
	public final static int NMS_TYPE_T2000_FLAG = 11;
	public final static int NMS_TYPE_U2000_FLAG = 12;
	public final static int NMS_TYPE_E300_FLAG = 21;
	public final static int NMS_TYPE_U31_FLAG = 22;
	public final static int NMS_TYPE_LUCENT_OMS_FLAG = 31;
	public final static int NMS_TYPE_OTNM2000_FLAG = 41;
	public final static int NMS_TYPE_ALU_FLAG = 51;
	public final static int NMS_TYPE_FUJITSU_FLAG = 91;
//	public final static int NMS_TYPE_VEMS_FLAG = 99;
	
	public final static String EMS_NAME_VEMS = "VEMS";
	
	// 华为
	public final static int FACTORY_HW_FLAG = 1;
	// 中兴
	public final static int FACTORY_ZTE_FLAG = 2;
	// 朗讯
	public final static int FACTORY_LUCENT_FLAG = 3;
	// 烽火
	public final static int FACTORY_FIBERHOME_FLAG = 4;
	// 阿尔卡特朗讯
	public final static int FACTORY_ALU_FLAG = 5;
	// 富士通
	public final static int FACTORY_FUJITSU_FLAG = 9;

	// 性能分析异常等级定义
	public final static int EXCEPTION_LEVLE_NORMAL = 0;
	public final static int EXCEPTION_LEVLE_CR = 3;
	public final static int EXCEPTION_LEVLE_MN = 2;
	public final static int EXCEPTION_LEVLE_WR = 1;
	
	// 性能值类型
	public final static String PM_PHYSICS_VALUE = "1";
	public final static String PM_COUNTER_VALUE = "2";
	
	
	//link
	//最近新增
	public final static int LATEST_ADD = 1;
	//最近删除
	public final static int LATEST_DEL = 2;
	//以前删除
	public final static int DEL_BEFORE = 3;
	//以前新增
	public final static int ADD_BEFORE = 4;
	
	/** mongoDB数据库常量定义 */
	// mongoDB数据库库名
	public final static String MONGODB_NAME = "test";
	// 生成自增主键的表名(sequence)
	public final static String SEQUENCE = "SEQUENCE";
	// 当前告警表名
	public final static String T_CURRENT_ALARM = "T_CURRENT_ALARM";
	// 历史告警表名
	public final static String T_HISTORY_ALARM = "T_HISTORY_ALARM";
	// 日志表名
	public final static String T_JOURNAL = "T_JOURNAL";
	/** 故障模块常量定义 */
	// 告警确认 2：未确认 1：已确认
	public final static int IS_ACK_NO = 2;
	public final static int IS_ACK_YES = 1;
	// 告警清除  2：未清除 1：已清除
	public final static int IS_CLEAR_NO = 2;
	public final static int IS_CLEAR_YES = 1;
	// 派单 2 ：不派单 1 ：派单
	public final static int IS_ORDER_NO = 2;
	public final static int IS_ORDER_YES = 1;
	// 表主键默认值(从几开始)
	public final static int _ID = 1;
	// 闪告次数默认值AMOUNT
	public final static int AMOUNT = 1;
	// 新增屏蔽器->状态 1: 启用 2：挂起
	public final static int ALARM_SHIELD_STATUS_ENABLE = 1;
	public final static int ALARM_SHIELD_STATUS_PENDING = 2;
	// 告警及事件重定义->状态 1: 启用 2：挂起
	public final static int ALARM_REDEFINE_STATUS_ENABLE = 1;
	public final static int ALARM_REDEFINE_STATUS_PENDING = 2;
	// 告警级别
	public final static int ALARM_PS_INDETERMINATE = 0;// 未知
	public final static int ALARM_PS_CRITICAL = 1;// 紧急
	public final static int ALARM_PS_MAJOR = 2;// 重要
	public final static int ALARM_PS_MINOR = 3;// 次要
	public final static int ALARM_PS_WARNING = 4;// 提示
	public final static int ALARM_PS_CLEARED = 5;// 清除
	// 告警状体 1:清除告警 2：发生告警
	public final static int ALARM_STATUS_CLEARED = 1;
	public final static int ALARM_STATUS_OCCUR = 2;
	// 告警自动确认->确认方式 1:不确认 2:立即确认 3:定时确认
	public final static int ALARM_NO_CONFIRM = 1;
	public final static int ALARM_IMMEDIATELY_CONFIRM = 2;
	public final static int ALARM_TIMING_CONFIRM = 3;
	// 告警推送设置，参数对应数据库中的key
	public final static String ALARM_PUSH_PARAM_KEY = "ALARM_PUSH";
	// 告警自动确认时间设置，参数对应数据库中的key
	public final static String ALARM_CONFIRM_PARAM_KEY = "ALARM_CONFIRM";
	// 告警转移时间设置，参数对应数据库中的key
	public final static String ALARM_SHIFT_PARAM_KEY = "ALARM_SHIFT";
	// 综告推送-> 告警产生设置  1：每次推送 2：首次推送
	public final static int COMREPORT_PUSH_OCCUR_EVERY = 1;
	public final static int COMREPORT_PUSH_OCCUR_FIRST = 2;
	// 综告推送-> 告警清除设置  1：每次推送 2：已清除、已确认推送 3：当前告警转移历史告警推送
	public final static int COMREPORT_PUSH_CLEAR_EVERY = 1;
	public final static int COMREPORT_PUSH_CLEAR_HAS_CLEAR_CONFIRM = 2;
	public final static int COMREPORT_PUSH_CLEAR_SHIFT = 3;
	// 告警入库类型 -> 1：同步 2：推送
	public final static int ALARM_TO_DB_TYPE_SYNCH = 1;
	public final static int ALARM_TO_DB_TYPE_PUSH = 2;
	//共通映射定义
	public static class COMMON {
		
		// 连接速率
		public static final String CONNECT_RATE_VC12 = "VC12";
		public static final String CONNECT_RATE_VC4 = "VC4";
		public static final String CONNECT_RATE_VC3 = "VC3";
		public static final String CONNECT_RATE_VC4_4C = "VC4-4C";
		public static final String CONNECT_RATE_VC4_8C = "VC4-8C";
		public static final String CONNECT_RATE_VC4_16C = "VC4-16C";
		public static final String CONNECT_RATE_VC4_64C = "VC4-64C";
		
		// 端口属性 1.边界点 2.连接点,3是网元内部连接点
		public static final int PORT_TYPE_EDGE_POINT = 1;
		public static final int PORT_TYPE_LINK_POINT = 2;
		public final static int PORT_TYPE_INTERNAL_LINK_POINT = 3;
		
		public final static int EQT_FLAG = 0;
		public final static int EQT_HOLDER_FLAG = 1;
		// ptp标志
		public final static int PTP_FLAG = 0;
		// ftp标志
		public final static int FTP_FLAG = 1;
		
		//网元通信状态 0.在线 1.离线 2.未知
		public static final int NE_COMM_IN_SERVICE = 1;
		public static final int NE_COMM_OUT_OF_SERVICE = 2;
		public static final int NE_COMM_UNKNOW = 3;
		
		//周期定义
		public static final int GRANULARITY_15MIN_FLAG = 1;
		public static final int GRANULARITY_24HOUR_FLAG = 2;
		public static final int GRANULARITY_NA_FLAG = 3;
		
		public static final String GRANULARITY_15MIN_STRING = "15min";
		public static final String GRANULARITY_24HOUR_STRING = "24h";
		public static final String GRANULARITY_NA_STRING = "NA";
		
		public static HashMap<Integer, String> GRANULARITY = new HashMap<Integer, String>();
		static{
			GRANULARITY.put(COMMON.GRANULARITY_15MIN_FLAG , GRANULARITY_15MIN_STRING);
			GRANULARITY.put(COMMON.GRANULARITY_24HOUR_FLAG , GRANULARITY_24HOUR_STRING);
			GRANULARITY.put(COMMON.GRANULARITY_NA_FLAG , GRANULARITY_NA_STRING);
		}
		
		// 网元类型1.SDH 2.WDM 3.OTN 4.PTN 5.微波 6.FTTX 9.虚拟网元 99.未知
		public final static int NE_TYPE_SDH_FLAG = 1;
		public final static int NE_TYPE_WDM_FLAG = 2;
		public final static int NE_TYPE_OTN_FLAG = 3;
		public final static int NE_TYPE_PTN_FLAG = 4;
		public final static int NE_TYPE_MICROWAVE_FLAG = 5;
		public final static int NE_TYPE_FTTX_FLAG = 6;
		public final static int NE_TYPE_VIRTUAL_NE_FLAG = 9;
		public final static int NE_TYPE_UNKNOW_FLAG = 99;
		
		// 业务类型 1.SDH 2.WDM 3.ETH 4.ATM 99 unknow
		public final static int DOMAIN_SDH_FLAG = 1;
		public final static int DOMAIN_WDM_FLAG = 2;
		public final static int DOMAIN_ETH_FLAG = 3;
		public final static int DOMAIN_ATM_FLAG = 4;
		public final static int DOMAIN_UNKNOW_FLAG= 99;
		
		public final static String DOMAIN_SDH_  = "sdh";
		public final static String DOMAIN_WDM_  = "wdm";
		public final static String DOMAIN_ETH_  = "eth";
		public final static String DOMAIN_ATM_  = "atm";
		public final static String DOMAIN_UNKNOW_  = "unknow";
		
		public final static String PTP_TYPE_SDH_E1 = "E1";
		public final static String PTP_TYPE_SDH_E3 = "E3";
		public final static String PTP_TYPE_SDH_E4 = "E4";
		public final static String PTP_TYPE_SDH_STM1 = "STM-1";
		public final static String PTP_TYPE_SDH_STM4 = "STM-4";
		public final static String PTP_TYPE_SDH_STM16 = "STM-16";
		public final static String PTP_TYPE_SDH_STM64 = "STM-64";
		public final static String PTP_TYPE_SDH_STM256 = "STM-256";
		public final static String PTP_TYPE_WDM_OCH = "OCH";
		public final static String PTP_TYPE_WDM_OMS = "OMS ";
		public final static String PTP_TYPE_WDM_OTS = "OTS";
		public final static String PTP_TYPE_WDM_OSCNI = "OSCNI";
		public final static String PTP_TYPE_WDM_OTS_OMS = "OTS&OMS";
		public final static String PTP_TYPE_OTHER_MP = "MP";
		public final static String PTP_TYPE_OTHER_MAC = "MAC";
		
		public final static String RATE_2M = "2M";
		public final static String RATE_34M = "34M ";
		public final static String RATE_140M = "140M";
		public final static String RATE_155M = "155M";
		public final static String RATE_622M = "622M";
		public final static String RATE_2_5G = "2.5G";
		public final static String RATE_10G = "10G";
		public final static String RATE_40G = "40G";
		
		
		//corba命名常量定义
		public final static String EMS = "EMS";
		public final static String MANAGED_ELEMENT = "ManagedElement";
		public final static String EQUIPMENT_HOLDER = "EquipmentHolder";
		public final static String EQUIPMENT = "Equipment";
		public final static String PTP = "PTP";
		public final static String FTP = "FTP";
		public final static String CTP = "CTP";
		public final static String CTP_PARAM = "CTP_PARAM";
		public final static String MULTI_LAYER_SUBNETWORK = "MultiLayerSubnetwork";
		public final static String TOPOLOGICAL_LINK = "TopologicalLink";
		public final static String SUBNETWORK_CONNECTION = "SubnetworkConnection";
		public final static String WDMPG = "WDMPG";
		public final static String PGP = "PGP";
		public final static String EPGP = "EPGP";
		public final static String AID = "AID";
		public final static String TP_POOL = "TPPool";
		
		public final static String RACK = "rack";
		public final static String SHELF = "shelf";
		public final static String SLOT = "slot";
		public final static String SUB_SLOT = "sub_slot";
		public final static String UNIT = "unit";
		public final static String SUB_UNIT = "sub_unit";
		public final static String PORT = "port";
		
		//目标类型定义
		public final static int TARGET_TYPE_EMSGROUP_FLAG  = 1;
		public final static int TARGET_TYPE_EMS_FLAG  = 2;
		public final static int TARGET_TYPE_SUBNET_FLAG  = 3;
		public final static int TARGET_TYPE_NE_FLAG  = 4;
		public final static int TARGET_TYPE_SHELF_FLAG  = 5;
		public final static int TARGET_TYPE_EQUIPMENT_FLAG  = 6;
		public final static int TARGET_TYPE_PTP_FLAG  = 7;
		public final static int TARGET_TYPE_SDH_CTP_FLAG  = 8;
		public final static int TARGET_TYPE_OTN_CTP_FLAG  = 9;
		public final static int TARGET_TYPE_CTP_FLAG  = 10;
		
		//通知定义事件
		public final static String NT_ALARM = "NT_ALARM";
		public final static String NT_TCA = "NT_TCA";
		public final static String NT_HEARTBEAT = "NT_HEARTBEAT";
		public final static String NT_FILE_TRANSFER_STATUS = "NT_FILE_TRANSFER_STATUS";
		public final static String NT_STATE_CHANGE = "NT_STATE_CHANGE";
		public static String getEventTypeName(String typeName){
			if("NT_HEALTH".equals(typeName))//朗讯心跳
				return NT_HEARTBEAT;
			return typeName;
		}
		
		//location 定义
		public final static int PM_LOCATION_NEAR_END_RX_FLAG  = 1;
		public final static int PM_LOCATION_FAR_END_RX_FLAG = 2;
		public final static int PM_LOCATION_NEAR_END_TX_FLAG = 3;
		public final static int PM_LOCATION_FAR_END_TX_FLAG = 4;
		public final static int PM_LOCATION_NA_FLAG = 5;
		
		
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
		
		/*EquipHolder告警用*/
		public final static int ALARM_OBJECT_TYPE_RACK = 100;
		public final static int ALARM_OBJECT_TYPE_SHELF = 101;
		public final static int ALARM_OBJECT_TYPE_SLOT = 102;
		
		//告警级别定义
		public static class PerceivedSeverity{
			
			public static final String PS_INDETERMINATE_STRING = "PS_INDETERMINATE";
			public static final String PS_CRITICAL_STRING = "PS_CRITICAL";
			public static final String PS_MAJOR_STRING = "PS_MAJOR";
			public static final String PS_MINOR_STRING = "PS_MINOR";
			public static final String PS_WARNING_STRING = "PS_WARNING";
			public static final String PS_CLEARED_STRING = "PS_CLEARED";
			
			public static String toString(int value){
				switch (value) {
					case ALARM_PS_INDETERMINATE: return PS_INDETERMINATE_STRING;
					case ALARM_PS_CRITICAL: return PS_CRITICAL_STRING;
					case ALARM_PS_MAJOR: return PS_MAJOR_STRING;
					case ALARM_PS_MINOR: return PS_MINOR_STRING;
					case ALARM_PS_WARNING: return PS_WARNING_STRING;
					case ALARM_PS_CLEARED: return PS_CLEARED_STRING;
					default: return PS_INDETERMINATE_STRING;
				}
			}
			
			public static int toValue(String string){
				if(PS_INDETERMINATE_STRING.equals(string))
					return ALARM_PS_INDETERMINATE;
				else if(PS_CRITICAL_STRING.equals(string))
					return ALARM_PS_CRITICAL;
				else if(PS_MAJOR_STRING.equals(string))
					return ALARM_PS_MAJOR;
				else if(PS_MINOR_STRING.equals(string))
					return ALARM_PS_MINOR;
				else if(PS_WARNING_STRING.equals(string))
					return ALARM_PS_WARNING;
				else if(PS_CLEARED_STRING.equals(string))
					return ALARM_PS_CLEARED;
				else
					return ALARM_PS_INDETERMINATE;
			}
		}
	}
	
	
	//HW 映射定义
	public static class HW {
		
		public final static String HW_DOMAIN = "domain";
		public final static String HW_TYPE = "type";
		
		public final static String HW_CTP_OS = "os";
		public final static String HW_CTP_OTS = "ots";
		public final static String HW_CTP_OMS = "oms";
		public final static String HW_CTP_OCH = "och";
		public final static String HW_CTP_ODU0 = "odu0";
		public final static String HW_CTP_ODU1 = "odu1";
		public final static String HW_CTP_ODU2 = "odu2";
		public final static String HW_CTP_ODU3 = "odu3";
		public final static String HW_CTP_OTU0 = "otu0";
		public final static String HW_CTP_OTU1 = "otu1";
		public final static String HW_CTP_OTU2 = "otu2";
		public final static String HW_CTP_OTU3 = "otu3";
		public final static String HW_CTP_DSR = "dsr";
		
		// hw pmLocation描述
		public static final String HW_PM_LOCATION_NEAR_END_RX = "PML_NEAR_END_Rx";
		public static final String HW_PM_LOCATION_FAR_END_RX = "PML_FAR_END_Rx";
		public static final String HW_PM_LOCATION_NEAR_END_TX = "PML_NEAR_END_Tx";
		public static final String HW_PM_LOCATION_FAR_END_TX= "PML_FAR_END_Tx";
		public static final String HW_PM_LOCATION_NA = "PML_BIDIRECTIONAL";
		
		public static HashMap<Integer, String> LOCATION = new HashMap<Integer, String>();
		static{
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_RX_FLAG , HW_PM_LOCATION_NEAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_RX_FLAG , HW_PM_LOCATION_FAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_TX_FLAG , HW_PM_LOCATION_NEAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_TX_FLAG , HW_PM_LOCATION_FAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_NA_FLAG , HW_PM_LOCATION_NA);
		}
		
		// 告警数据数组索引定义
		public static final short NOTIFICATION_ID = 0;
		public static final short OBJECT_NAME = 1;
		public static final short NATIVE_EMS_NAME = 2;
		public static final short NATIVE_PROBABLE_CAUSE = 3;
		public static final short OBJECT_TYPE = 4;
		public static final short EMS_TIME = 5;
		public static final short NE_TIME = 6;
		public static final short IS_CLEARABLE = 7;
		public static final short LAYER_RATE = 8;
		public static final short PROBABLE_CAUSE = 9;
		public static final short PROBABLE_CAUSE_QUALIFIER = 10;
		public static final short PERCEIVED_SEVERITY = 11;
		public static final short SERVICE_AFFECTING = 12;
		public static final short AFFECTED_TPLIST = 13;
		public static final short ADDITIONAL_TEXT = 14;
		public static final short ADDITIONAL_INFO = 15;
		public static final short X733_EVENTTYPE = 16;
		public static final short OBJECT_TYPE_QUALIFIER = 17;
		public static final short RCAIINDICATOR = 18;					// 仅U2000
		public static final short X733_CORRELATED_NOTIFICATIONS = 19;   // 仅U2000
		

	}
	
	//ZTE 映射定义
	public static class ZTE {
		
		public final static String ZTE_CTP_OS = "os";
		public final static String ZTE_CTP_OTS = "ots";
		public final static String ZTE_CTP_OMS = "oms";
		public final static String ZTE_CTP_OCH = "och";
		public final static String ZTE_CTP_ODU0 = "odu0";
		public final static String ZTE_CTP_ODU1 = "odu1";
		public final static String ZTE_CTP_ODU2 = "odu2";
		public final static String ZTE_CTP_ODU3 = "odu3";
		public final static String ZTE_CTP_OTU0 = "otu0";
		public final static String ZTE_CTP_OTU1 = "otu1";
		public final static String ZTE_CTP_OTU2 = "otu2";
		public final static String ZTE_CTP_OTU3 = "otu3";
		public final static String ZTE_CTP_DSR = "dsr";
		
		public final static short  HOLD_TYPE_RACK = 1;
		public final static short  HOLD_TYPE_SHELF = 2;
		public final static short  HOLD_TYPE_SLOT = 3;
		public final static short  HOLD_TYPE_UNIT = 4;
		
		public final static String ZTE_PHYSICAL_PORT = "PhysicalPort";
		public final static String ZTE_CHANEL_NO = "ChannelNo";
		public final static String ZTE_PTP_TYPE = "ptptype";
		public final static String ZTE_PTP_LAYERRATE = "layerrate";
		public final static String ZTE_DIRECTION = "direction";
		// zte pmLocation描述
		public static final String ZTE_PM_LOCATION_NEAR_END_RX = "Near_End";
		public static final String ZTE_PM_LOCATION_FAR_END_RX= "PML_FAR_END_Rx";
		public static final String ZTE_PM_LOCATION_NEAR_END_TX = "PML_NEAR_END_Tx";
		public static final String ZTE_PM_LOCATION_FAR_END_TX= "PML_FAR_END_Tx";
		public static final String ZTE_PM_LOCATION_NA  = "PML_BIDIRECTIONAL";
		
		public static HashMap<Integer, String> LOCATION = new HashMap<Integer, String>();
		static{
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_RX_FLAG , ZTE_PM_LOCATION_NEAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_RX_FLAG , ZTE_PM_LOCATION_FAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_TX_FLAG , ZTE_PM_LOCATION_NEAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_TX_FLAG , ZTE_PM_LOCATION_FAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_NA_FLAG , ZTE_PM_LOCATION_NA);
		}
		
		// 告警数据数组索引定义
		// E300
		public static final short E300_OBJECT_NAME = 0;
		public static final short E300_ALARM_DETECT_INFO = 1;
		public static final short E300_PROBABLE_CAUSE = 2;
		public static final short E300_ALARM_TYPE = 3;
		public static final short E300_PERCEIVED_SEVERITY = 4;
		public static final short E300_RAISE_TIME = 5;
		public static final short E300_CLEAR_TIME = 6;
		public static final short E300_ADDITIONAL_INFO = 7;
		public static final short E300_LAYER_RATE = 8;
		public static final short E300_OBJECT_TYPE = 9;
		public static final short E300_OBJECT_FILTER_NAME = 10;
		// U31
		public static final short U31_NOTIFICATION_ID = 0;
		public static final short U31_OBJECT_NAME = 1;
		public static final short U31_OBJECT_FILTER_NAME = 2;
		public static final short U31_ALARM_DETECT_INFO = 3;
		public static final short U31_PROBABLE_CAUSE = 4;
		public static final short U31_ALARM_TYPE = 5;
		public static final short U31_PERCEIVED_SEVERITY = 6;
		public static final short U31_RAISE_TIME = 7;
		public static final short U31_CLEAR_TIME = 8;
		public static final short U31_ADDITIONAL_INFO = 9;
		public static final short U31_LAYER_RATE = 10;
		public static final short U31_OBJECT_TYPE =11;
		public static final short U31_EMS_TIME = 12;
		public static final short U31_CORRELATED_ALARM_IDS = 13;
		public static final short U31_DESCRIPTION = 14;
		public static final short U31_SERVICE_AFFECTING = 15;
		public static final short U31_CONFIRM_STATUS = 16;
		public static final short U31_CLEAR_STATUS = 17;
		public static final short U31_IS_CLEARABLE = 18;
		public static final short U31_ACK_USER = 19;
		public static final short U31_ACK_TIME = 20;
		public static final short U31_ACK_INFO = 21;
		public static final short U31_VENDOR_PROBABLE_CAUSE = 22;
		public static final short U31_ALARM_STATUS =23;
		public static final short U31_CUSTOMER_NAME = 24;
		public static final short U31_DIAGNOSE_INFO = 25;
		
		public static class E300_ServiceAffect {
			public static final short SA_UNKNOWN = 0;
			public static final short SA_AFFECTING = 1;
			public static final short SA_NON_AFFECTING = 2;
			public static final String SA_UNKNOWN_STR = "UNKNOWN";
			public static final String SA_AFFECTING_STR = "SERVICE_AFFECTING";
			public static final String SA_NON_AFFECTING_STR = "NON_SERVICE_AFFECTING";
			public static String toString(int value) {
				switch (value) {
					case SA_UNKNOWN: return SA_UNKNOWN_STR;
					case SA_AFFECTING: return SA_AFFECTING_STR;
					case SA_NON_AFFECTING: return SA_NON_AFFECTING_STR;
					default: return SA_UNKNOWN_STR;
				}
			}
			public static int toValue(String str) {
				if (SA_UNKNOWN_STR.equals(str))
					return SA_UNKNOWN;
				else if (SA_AFFECTING_STR.equals(str))
					return SA_AFFECTING;
				else if (SA_NON_AFFECTING_STR.equals(str))
					return SA_NON_AFFECTING;
				else
					return SA_UNKNOWN;
			}
		}
		
		public static class U31_ServiceAffect {
			public static final short SA_UNKNOWN = 0;
			public static final short SA_AFFECTING = 1;
			public static final short SA_NON_AFFECTING = 2;
			public static final String SA_UNKNOWN_STR = "SA_UNKNOWN";
			public static final String SA_AFFECTING_STR = "SA_SERVICE_AFFECTING";
			public static final String SA_NON_AFFECTING_STR = "SA_NON_SERVICE_AFFECTING";
			public static String toString(int value) {
				switch (value) {
					case SA_UNKNOWN: return SA_UNKNOWN_STR;
					case SA_AFFECTING: return SA_AFFECTING_STR;
					case SA_NON_AFFECTING: return SA_NON_AFFECTING_STR;
					default: return SA_UNKNOWN_STR;
				}
			}
			public static int toValue(String str) {
				if (SA_UNKNOWN_STR.equals(str))
					return SA_UNKNOWN;
				else if (SA_AFFECTING_STR.equals(str))
					return SA_AFFECTING;
				else if (SA_NON_AFFECTING_STR.equals(str))
					return SA_NON_AFFECTING;
				else
					return SA_UNKNOWN;
			}
		}
	}
	
	//LUCENT 映射定义
	public static class LUCENT {
		// lucent pmLocation描述
		public static final String LUCENT_PM_LOCATION_NEAR_END_RX = "PML_NEAR_END_Rx";
		public static final String LUCENT_PM_LOCATION_FAR_END_RX = "PML_FAR_END_Rx";
		public static final String LUCENT_PM_LOCATION_NEAR_END_TX = "PML_NEAR_END_Tx";
		public static final String LUCENT_PM_LOCATION_FAR_END_TX= "PML_FAR_END_Tx";
		public static final String LUCENT_PM_LOCATION_NA = "PML_BIDIRECTIONAL";

		
		public static HashMap<Integer, String> LOCATION = new HashMap<Integer, String>();
		static{
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_RX_FLAG , LUCENT_PM_LOCATION_NEAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_RX_FLAG , LUCENT_PM_LOCATION_FAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_TX_FLAG , LUCENT_PM_LOCATION_NEAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_TX_FLAG , LUCENT_PM_LOCATION_NEAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_NA_FLAG , LUCENT_PM_LOCATION_NA);
		}
	}
	
	//FIM 映射定义
	public static class FIM {
		
		public final static String FIM_CTP_OS = "os";
		public final static String FIM_CTP_OTS = "ots";
		public final static String FIM_CTP_OMS = "oms";
		public final static String FIM_CTP_OCH = "och";
		public final static String FIM_CTP_ODU0 = "odu0";
		public final static String FIM_CTP_ODU1 = "odu1";
		public final static String FIM_CTP_ODU2 = "odu2";
		public final static String FIM_CTP_ODU3 = "odu3";
		public final static String FIM_CTP_OTU0 = "otu0";
		public final static String FIM_CTP_OTU1 = "otu1";
		public final static String FIM_CTP_OTU2 = "otu2";
		public final static String FIM_CTP_OTU3 = "otu3";
		public final static String FIM_CTP_DSR = "dsr";

		// fim pmLocation描述 烽火corba参数testSample.txt中描述:该参数只支持空
		public static final String FIM_PM_LOCATION_NEAR_END_RX = "PML_NEAR_END_Rx";
		public static final String FIM_PM_LOCATION_FAR_END_RX = "PML_FAR_END_Rx";
		public static final String FIM_PM_LOCATION_NEAR_END_TX = "PML_NEAR_END_Tx";
		public static final String FIM_PM_LOCATION_FAR_END_TX= "PML_FAR_END_Tx";
		public static final String FIM_PM_LOCATION_NA = "PML_BIDIRECTIONAL";

		
		public static HashMap<Integer, String> LOCATION = new HashMap<Integer, String>();
		static{
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_RX_FLAG , FIM_PM_LOCATION_NEAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_RX_FLAG , FIM_PM_LOCATION_FAR_END_RX);
			LOCATION.put(COMMON.PM_LOCATION_NEAR_END_TX_FLAG , FIM_PM_LOCATION_NEAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_FAR_END_TX_FLAG , FIM_PM_LOCATION_FAR_END_TX);
			LOCATION.put(COMMON.PM_LOCATION_NA_FLAG , FIM_PM_LOCATION_NA);
		}
		
		// 告警数据数组索引定义
		public static final short NOTIFICATION_ID = 0;
		public static final short OBJECT_NAME = 1;
		public static final short NATIVE_EMS_NAME = 2;
		public static final short NATIVE_PROBABLE_CAUSE = 3;
		public static final short PROBABLE_CAUSE_QUALIFIER = 4;
		public static final short OBJECT_TYPE = 5;
		public static final short EMS_TIME = 6;
		public static final short NE_TIME = 7;
		public static final short EMS_END_TIME = 8;
		public static final short NE_END_TIME = 9;
		public static final short IS_CLEARABLE = 10;
		public static final short LAYER_RATE = 11;
		public static final short PROBABLE_CAUSE = 12;
		public static final short SERVICE_AFFECTING = 13;
		public static final short PERCEIVED_SEVERITY = 14;
		public static final short X733_SPECIFIC_PROBLEMS = 15;
		public static final short X733_PROPOSED_REPAIR_ACTIONS = 16;
		public static final short ADDITIONAL_TEXT = 17;
		public static final short X733_EVENT_TYPE = 18;
		public static final short ACKNOWLEDGE_INDICATION = 19;
		public static final short ADDITIONAL_INFO = 20;
	}

	//jms消息类型定义 -- start
	public static final int MESSAGE_TYPE_ALARM = 1;
	// 推送给综告接口告警信息的类型
	public static final int MESSAGE_TYPE_ALARM_COM = 2;
	//待补充
	//jms消息类型定义 -- end
	
	
	// 日期格式
	public static final String COMMON_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String COMMON_SIMPLE_FORMAT = "yyyy-MM-dd";

	public static final String COMMON_START_FORMAT = "yyyy-MM-dd 00:00:00";

	public static final String COMMON_END_FORMAT = "yyyy-MM-dd 23:59:59";

	public static final String RETRIEVAL_TIME_FORMAT = "yyyyMMddHHmmss";

	public static final String DAILY_FORMAT = "yyyy-MM-dd 00:30:00";

	public static final String WEEKLY_FORMAT = "yyyy-MM-dd 00:35:00";

	public static final String MONTHLY_FORMAT = "yyyy-MM-dd 00:40:00";

	public static final String SEASONLY_FORMAT = "yyyy-MM-dd 00:45:00";

	public static final String YEARLY_FORMAT = "yyyy-MM-dd 00:45:00";

//	public static final String STANDARD_TIME_FORMAT_HW = "yyyyMMddHHmmss'.0Z'Z";
//
//	public static final String STANDARD_TIME_FORMAT_LUCENT = "yyyyMMddHHmmss'.0'Z";
//	//仅E300
//	public static final String STANDARD_TIME_FORMAT_ZTE = "yyyyMMddHHmmss'.000Z'Z";
//	
//	public static final String STANDARD_TIME_FORMAT_FIM = "yyyyMMddHHmmss'.0'Z";

	public static final String GROUP_FORMAT = "yyyy-MM";

}
