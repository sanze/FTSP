package com.fujitsu.common;

public class AlarmConvergeDefine {
	
	// 告警收敛规则的启用状态
	public final static int ALARM_CONVERGE_ENABLE = 1;	//启用
	public final static int ALARM_CONVERGE_DISABLE = 2;	//挂起
	
	// 告警收敛规则的运行状态
	public final static int ALARM_CONVERGE_RUNTIME_UNKNOWN = 0;	//未知
	public final static int ALARM_CONVERGE_RUNTIME_IDLE = 1;	//空闲 
	public final static int ALARM_CONVERGE_RUNTIME_RUNNING = 2;	//正在运行
	public final static int ALARM_CONVERGE_RUNTIME_ERROR = 3;	//运行异常
	
	// 告警收敛的默认线程池大小
	public final static int DEFAULT_THREAD_POOL_SIZE = 500;
	
	// 默认的告警收敛时延
	public final static int DEFAULT_ALARM_CONVERGE_TIMER = 60;
	
	// 告警收敛执行动作类型
	public final static int ALARM_CONVERGE_ACT_OBJ_SELF = 1;		//对象本身
	public final static int ALARM_CONVERGE_ACT_UNDERLING = 2;		//对象的下属对象
	public final static int ALARM_CONVERGE_ACT_ADJACENT_PORT = 3;	//对象相邻端口
	public final static int ALARM_CONVERGE_ACT_TRANSMISSION_SYS = 4;//对象所属传输系统网元
	public final static int ALARM_CONVERGE_ACT_CIRCUIT = 5;			//对象相关电路通道
	
	// 告警收敛执行动作状态
	public final static int ALARM_CONVERGE_ACT_ENABLE = 1;	//启用
	public final static int ALARM_CONVERGE_ACT_DISABLE = 2; //挂起
	
	// 告警收敛标志
	public final static int ALARM_CONVERGE_UNKNOWN_ALARM = 1;		//未分析告警
	public final static int ALARM_CONVERGE_MAIN_ALARM = 2;			//主告警
	public final static int ALARM_CONVERGE_DERIVATIVE_ALARM = 3;	//衍生告警
	
	// 告警收敛动作执行的告警名称
	public final static String ALARM_CONVERGE_ACT_ALL_ALARM = "所有";
	
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
}
