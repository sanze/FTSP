package com.fujitsu.common;

import java.util.HashMap;

public class FaultDiagnoseDefine {
	
	// 故障诊断规则的启用状态
	public final static int FAULT_DIAGNOSE_ENABLE = 1;	//启用
	public final static int FAULT_DIAGNOSE_DISABLE = 2;	//挂起
	
	// 故障诊断规则的运行状态
	public final static int FAULT_DIAGNOSE_RUNTIME_UNKNOWN = 0;	//未知
	public final static int FAULT_DIAGNOSE_RUNTIME_IDLE = 1;	//空闲 
	public final static int FAULT_DIAGNOSE_RUNTIME_RUNNING = 2;	//正在运行
	public final static int FAULT_DIAGNOSE_RUNTIME_ERROR = 3;	//运行异常
	
	// 故障诊断的默认线程池大小
	public final static int DEFAULT_THREAD_POOL_SIZE = 500;
	
	// 默认的故障诊断时延
	public final static int DEFAULT_FAULT_DIAGNOSE_TIMER = 60;
	
	// 故障诊断执行动作类型
	public final static int FAULT_DIAGNOSE_ACT_OBJ_SELF = 1;		//对象本身
	public final static int FAULT_DIAGNOSE_ACT_ADJACENT_PORT = 2;	//对象相邻端口
	public final static int FAULT_DIAGNOSE_ACT_TRANSMISSION_SYS = 3;//对象所属传输系统网元
	public final static int FAULT_DIAGNOSE_ACT_PEER_CABLE_PORT = 4;	//对象同缆端口
	
	// 故障诊断执行动作状态
	public final static int FAULT_DIAGNOSE_ACT_ENABLE = 1;	//启用
	public final static int FAULT_DIAGNOSE_ACT_DISABLE = 2; //挂起
	
	// 故障诊断标志
	public final static int FAULT_DIAGNOSE_UNKNOWN_ALARM = 1;		//未分析告警
	public final static int FAULT_DIAGNOSE_MAIN_ALARM = 2;			//主告警
	public final static int FAULT_DIAGNOSE_DERIVATIVE_ALARM = 3;	//衍生告警
	
	// 故障诊断动作执行的告警名称
	public final static String FAULT_DIAGNOSE_ACT_ALL_ALARM = "所有";
	// 故障源
	public final static int SOURCE_AUTO = 1;
	// 故障类型
	public final static int FAULT_TYPE_EQPT = 1;
	public final static int FAULT_TYPE_LINE = 2;
	
	// 故障判定结果
	public final static int DIAG_UNKNOWN = 0; 					//未知故障
    public final static int DIAG_LOCAL_EQPT = 1; 				//本端设备故障
    public final static int DIAG_LOCAL_EQPT_CONNECT = 2; 		//本端端口连接故障
    public final static int DIAG_REMOTE_EQPT = 3; 				//远端设备故障
    public final static int DIAG_LINE = 4; 					//线路故障
    public final static int DIAG_LINE_ONE_WAY = 5; 			//光缆单向中断
    public final static int DIAG_LINE_ATT = 6; 				//线路故障，衰耗过大
    public final static int DIAG_LOCAL_RCV_OPT_UNKNOWN = 7; 	//本端收光不可知
    public final static int DIAG_REMOTE_SEND_OPT_UNKNOWN = 8; 	//远端发光不可知
    
 // 故障处理状态
 	public final static int FAULT_PROC_STATUS_NOACK = 1;
 	public final static int FAULT_PROC_STATUS_PROCESSING = 2;
 	public final static int FAULT_PROC_STATUS_RETRIEVAL = 3;
 	public final static int FAULT_PROC_STATUS_ARCHIVE = 4;
	
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
	
	/** mongoDB数据库常量定义 */
	// mongoDB数据库库名
	public final static String MONGODB_NAME = "test";
	// 当前告警表名
	public final static String T_CURRENT_ALARM = "T_CURRENT_ALARM";
	
	// 告警清除  2：未清除 1：已清除
	public final static int IS_CLEAR_NO = 2;
	public final static int IS_CLEAR_YES = 1;
	
	/**
	 * 性能常量定义
	 */
	// 周期定义
	public static final int GRANULARITY_15MIN_FLAG = 1;
	public static final int GRANULARITY_24HOUR_FLAG = 2;
	
	public static final String GRANULARITY_15MIN_STRING = "15min";
	public static final String GRANULARITY_24HOUR_STRING = "24h";
	// 发送光功率--标准代号
	public static final String STD_INDEX_TPL_CUR = "TPL_CUR";
	// 接收光功率--标准代号
	public static final String STD_INDEX_RPL_CUR = "RPL_CUR";
	// 发送光功率--标准代号
	public static final String STD_INDEX_TPL_AVG = "TPL_AVG";
	// 发送光功率--标准代号
	public static final String STD_INDEX_RPL_AVG = "RPL_AVG";
	// 发送光功率最大值--标准代号
	public static final String STD_INDEX_TPL_MAX = "TPL_MAX";
	// 接收光功率最大值--标准代号
	public static final String STD_INDEX_RPL_MAX = "RPL_MAX";
	
	public static HashMap<Integer, String> GRANULARITY = new HashMap<Integer, String>();
	static {
		GRANULARITY.put(GRANULARITY_15MIN_FLAG, GRANULARITY_15MIN_STRING);
		GRANULARITY.put(GRANULARITY_24HOUR_FLAG, GRANULARITY_24HOUR_STRING);
	}
	// location 定义
	public final static int PM_LOCATION_NEAR_END_RX_FLAG = 1;
	public final static int PM_LOCATION_FAR_END_RX_FLAG = 2;
	public final static int PM_LOCATION_NEAR_END_TX_FLAG = 3;
	public final static int PM_LOCATION_FAR_END_TX_FLAG = 4;
	public final static int PM_LOCATION_NA_FLAG = 5;
	
	public static class DOMAIN {
		public static final int DOMAIN_SDH_FLAG = 1;
		public static final int DOMAIN_WDM_FLAG = 2;
		public static final int DOMAIN_ETH_FLAG = 3;
		public static final int DOMAIN_ATM_FLAG = 4;
		public final static int DOMAIN_UNKNOW_FLAG= 99;
	}
	
	public static final String DOMAIN_SDH_DISPLAY ="SDH";
	public static final String DOMAIN_WDM_DISPLAY = "WDM";
	public static final String DOMAIN_ETH_DISPLAY ="ETH";
	public static final String DOMAIN_ATM_DISPLAY ="ATM";
	public static final String DOMAIN_UNKNOW_DISPLAY ="未知";
    	

	// 采集等级
	// 第一优先级：人工采集告警、人工采集性能、人工同步资源数据
	// 第二优先级：自动采集性能，自动同步告警
	// 第三优先级：巡检同步（包括资源数据、性能、告警）
	// 第四优先级：自动同步基础数据
	public static final int COLLECT_LEVEL_1 = 1;
	public static final int COLLECT_LEVEL_2 = 2;
	public static final int COLLECT_LEVEL_3 = 3;
	public static final int COLLECT_LEVEL_4 = 4;
	
	// 光功率判定结果
	public static final int NORMAL = 1;	//正常
	public static final int ABNORMAL = 2;	//异常
	public static final int UNKNOWN =3;	//未知
	// 接收、发送
	public static final int TX = 1;
	public static final int RX = 2;
	
	// 告警诊断参数在系统参数表中的Key名称
	public final static String DIAGNOSE_TIMER = "FAULT_DIAGNOSE_TIMER";
	public final static String DIAGNOSE_PUSH = "FAULT_DIAGNOSE_PUSH";
	public final static String DIAGNOSE_LOS_ALARM = "FAULT_DIAGNOSE_LOS_ALARM";
	public final static String DIAGNOSE_BER_ALARM = "FAULT_DIAGNOSE_BER_ALARM";
	
	//jms消息类型定义
	public static final int MESSAGE_TYPE_ALARM = 1;
	public static final int MESSAGE_TYPE_FAULT = 2;

	public final static int NEGATIVE = -1;
}
