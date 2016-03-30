package com.fujitsu.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.fujitsu.model.ProcessModel;
import com.fujitsu.util.CommonUtil;


/**
 * @author xuxiaojun
 * 
 */
public class CommonDefine extends BaseDefine{
	
	/*=========================GIS资源状态=====================*/
	//光缆段cableSection
	public final static int ALL_COVERED_BY_ROUTE = 0;
	public final static int SOME_COVERED_BY_ROUTE = 1;
	public final static int NO_COVERD_BY_ROUTE = 2;
	public final static int LINE_ORDINARY = 3;
	public final static int LINE_TESTING = 7;
	public final static int LINE_WITH_ALARM = 4;
	//机房station
	public final static int STATION_NOALARM = 5;
	
	/*=========================GIS资源状态=====================*/
	
	/*=========================光缆测试========================*/
	//路由占用状态：0空闲，1占用
	public final static int ROUTE_STATUS_FREE = 0;
	public final static int ROUTE_STATUS_OCCUPY = 1;
	/*=========================光缆测试========================*/
	
	// 名称分割符
	public final static char NameSeparator = '：';
	public final static char PathSeparator = '/';
	
	public final static String PASSWORD = "888888";

	public final static int FAILED = 0;
	public final static int SUCCESS = 1;
	public final static int FIRST_CHECK = 2;

	//license项目存储map
	public static HashMap<String,String> LICENSE;
	// license相关参数
	public final static String LICENSE_KEY_START_TIME = "startTime";
	public final static String LICENSE_KEY_SUPPORT_NMS_NUMBER = "supportNMSNumber";
//	public final static String LICENSE_KEY_SUPPORT_NMS_TYPE = "supportNMSType";
	public final static String LICENSE_KEY_VALIDATE_TIME = "validateTime";
	public final static String LICENSE_KEY_LAST_MODIFIED = "lastModified";

	public static final String DATA_BACKUP_CONFIG_FILE = "databack";
	//excel配置表头文件名
	public static final String EXCELHEADER_CONFIG_FILE="excelHeaderResource";
	//光复用段告警性能表头文件名
	public static final String PMSEC_CONFIG_FILE="pmConfig";

	// 日期格式
	public static final String STANDARD_TIME_FORMAT_HW = "yyyyMMddHHmmss'.0Z'Z";

	public static final String STANDARD_TIME_FORMAT_LUCENT = "yyyyMMddHHmmss'.0'Z";

	public static final String STANDARD_TIME_FORMAT_ZTE = "yyyyMMddHHmmss'.000Z'Z";

	public static final String MS_REPORT_DAILY_FORMAT = "yyyyMMdd";
	public static final String MS_REPORT_MONTHLY_FORMAT = "yyyyMM";
	public static final String REPORT_CN_FORMAT = "yyyy年M月d日";
	public static final String REPORT_CN_FORMAT_MONTH = "yyyy年M月";

	public static final String REPORT_CN_FORMAT_24H = "yyyy年MM月dd日 HH时mm分ss秒";

	//jms消息类型定义 -- start
	// EMS上设备告警
	public static final int MESSAGE_TYPE_ALARM = 1;
	// 故障
	public static final int MESSAGE_TYPE_FAULT = 2;	
	// 推送给综告接口告警信息的类型
	//public static final int MESSAGE_TYPE_ALARM_COM = 2;
	// 网管连接信息
	public static final int MESSAGE_TYPE_EMS_CONN_STATUS = 3;
	// 光缆测试起停消息
	public static final int MESSAGE_TYPE_CABLE_TEST = 4;
	// 光缆断点更新消息
	public static final int MESSAGE_TYPE_BREAK_POINT = 5;
	// 综合告警接口过滤器状态改变消息（启用/挂起）
	public static final int MESSAGE_TYPE_ALARM_FILTER_STATUS_CHANGE = 6;
		//待补充
	//jms消息类型定义 -- end

	// 网管类型
	public final static int NMS_TYPE_T2000_FLAG = 11;
	public final static int NMS_TYPE_U2000_FLAG = 12;
	public final static int NMS_TYPE_E300_FLAG = 21;
	public final static int NMS_TYPE_U31_FLAG = 22;
	public final static int NMS_TYPE_LUCENT_OMS_FLAG = 31;
	public final static int NMS_TYPE_OTNM2000_FLAG = 41;
	public final static int NMS_TYPE_ALU_FLAG = 51;
	public final static int NMS_TYPE_FUJITSU_FLAG = 91;
	//public final static int NMS_TYPE_VEMS_FLAG = 99;
	public final static Map<Integer,String> NMS_TYPE=new HashMap<Integer,String>();
	static{
		NMS_TYPE.put(NMS_TYPE_T2000_FLAG, "T2000");
		NMS_TYPE.put(NMS_TYPE_U2000_FLAG, "U2000");
		NMS_TYPE.put(NMS_TYPE_E300_FLAG, "E300");
		NMS_TYPE.put(NMS_TYPE_U31_FLAG, "U31");
		NMS_TYPE.put(NMS_TYPE_LUCENT_OMS_FLAG, "LUCENT_OMS");
		NMS_TYPE.put(NMS_TYPE_OTNM2000_FLAG, "OTNM2000");
		NMS_TYPE.put(NMS_TYPE_ALU_FLAG, "ALU");
		//NMS_TYPE.put(NMS_TYPE_FUJITSU_FLAG, "FUJITSU");
	}

	public final static int FACTORY_HW_FLAG = 1;
	public final static int FACTORY_ZTE_FLAG = 2;
	public final static int FACTORY_LUCENT_FLAG = 3;
	public final static int FACTORY_FIBERHOME_FLAG = 4;
	public final static int FACTORY_ALU_FLAG = 5;
	public final static int FACTORY_FUJITSU_FLAG = 9;
	
	//设备厂家名称
	public final static String FACTORY_HW_NAME = "华为";
	public final static String FACTORY_ZTE_NAME = "中兴";
	public final static String FACTORY_LUCENT_NAME = "朗讯";
	public final static String FACTORY_FIBERHOME_NAME = "烽火";
	public final static String FACTORY_ALU_NAME = "贝尔";
	public final static String FACTORY_FUJITSU_NAME = "富士通";
	
	public final static Map<Integer,String> FACTORY=new HashMap<Integer,String>();
	static{
		FACTORY.put(FACTORY_HW_FLAG, FACTORY_HW_NAME);
		FACTORY.put(FACTORY_ZTE_FLAG, FACTORY_ZTE_NAME);
		FACTORY.put(FACTORY_LUCENT_FLAG, FACTORY_LUCENT_NAME);
		FACTORY.put(FACTORY_FIBERHOME_FLAG, FACTORY_FIBERHOME_NAME);
		FACTORY.put(FACTORY_ALU_FLAG, FACTORY_ALU_NAME);
		FACTORY.put(FACTORY_FUJITSU_FLAG, FACTORY_FUJITSU_NAME);
	}
	
	//获取设备厂家对应的数字
	public static Integer getFactoryFlag(String factoryName) {
		Integer code = null;
		for(Entry<Integer,String> set:FACTORY.entrySet()){
			if(set.getValue().equals(factoryName.trim())){
				code = set.getKey();
				break;
	}
		}
		return code;
	}
	
	// ***************板卡保护类型****************
	public final static int UNIT_PROMODE_CTP_FLAG = 1;
	public final static int UNIT_PROMODE_MS_FLAG = 2;
	
	//板卡保护类型名称
	public final static String UNIT_PROMODE_CTP = "光通道1+1保护组";
	public final static String UNIT_PROMODE_MS = "光复用段1+1保护组";
	
	public final static Map<Integer,String> UNIT_PROMODE=new HashMap<Integer,String>();
	static{
		UNIT_PROMODE.put(UNIT_PROMODE_CTP_FLAG, UNIT_PROMODE_CTP);
		UNIT_PROMODE.put(UNIT_PROMODE_MS_FLAG, UNIT_PROMODE_MS);
	}
	
	//获取板卡保护类型对应的数字
	public static Integer getUnitProModeFlag(String proModeName) {
		Integer code = null;
		for(Entry<Integer,String> set:UNIT_PROMODE.entrySet()){
			if(set.getValue().equals(proModeName.trim())){
				code = set.getKey();
				break;
			}
		}
		return code;
	}
	// ***************宁夏报表类型****************
	
	public final static Map<Integer,String> NX_REPORT_TYPE=new HashMap<Integer,String>();
	static{
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH, QUARTZ.TYPE_NAMES[QUARTZ.JOB_NX_REPORT_WDM_WAVELENGTH]);
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_WDM_AMP, QUARTZ.TYPE_NAMES[QUARTZ.JOB_NX_REPORT_WDM_AMP]);
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_WDM_SWITCH, QUARTZ.TYPE_NAMES[QUARTZ.JOB_NX_REPORT_WDM_SWITCH]);
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_WAVE_JOIN, QUARTZ.TYPE_NAMES[QUARTZ.JOB_NX_REPORT_WAVE_JOIN]);
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_WAVE_DIV, QUARTZ.TYPE_NAMES[QUARTZ.JOB_NX_REPORT_WAVE_DIV]);
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_SDH_PM, "SDH性能作业计划");//这里虽然是使用的原来网元报表的任务，但是名字不一样
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_PTN_IPRAN, QUARTZ.TYPE_NAMES[QUARTZ.JOB_NX_REPORT_PTN_IPRAN]);
		NX_REPORT_TYPE.put(QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK, QUARTZ.TYPE_NAMES[QUARTZ.JOB_NX_REPORT_PTN_FLOW_PEAK]);
	}
	
	//获取宁夏报表类型对应的数字
	public static Integer getReportTypeFlag(String type) {
		Integer code = null;
		for(Entry<Integer,String> set:NX_REPORT_TYPE.entrySet()){
			if(set.getValue().equals(type.trim())){
				code = set.getKey();
				break;
			}
		}
		return code;
	}
	
	public class NX_REPORT{
		public final class UNIT_TYPE {
			public static final int AMP = 1;
			public static final int SWITCH = 2;
			public static final int WAVE_JOIN = 3;
			public static final int WAVE_DIV = 4;
		}
		public static final int ADDEND = 100;
		
		public final class PTP_TYPE {
			public static final int HE_LU_KOU = 3;
			public static final int FEN_LU_KOU = 4;
			public static final int BAN_KA_JIE_KOU = 5;
		}
		public final class PTP_DIRECTION {
			public static final int NA = 0;
			public static final int BIDIRECTIONAL = 1;
			public static final int SOURCE = 2;
			public static final int SINK = 3;
		}
		
		public final class SYS_TYPE {
			public static final int DECENTRALIZED_RING = 1;
			public static final int CENTRALIZED_RING = 2;
			public static final int CENTALIZED_CHAIN = 3;
		}
		
	}
	public final static Map<Integer,String> NX_SYS_TYPE=new HashMap<Integer,String>();
	static{
		NX_SYS_TYPE.put(NX_REPORT.SYS_TYPE.DECENTRALIZED_RING, "非业务集中型环网系统");
		NX_SYS_TYPE.put(NX_REPORT.SYS_TYPE.CENTRALIZED_RING, "业务集中型环网系统");
		NX_SYS_TYPE.put(NX_REPORT.SYS_TYPE.CENTALIZED_CHAIN, "业务集中型单链系统");
	}
	//---------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static net.sf.json.JSONArray toJsonArray(Map map){
		ArrayList<Map> list=new ArrayList<Map>();
		for(Object key:map.keySet()){
			Map jsonMap=new HashMap();
			jsonMap.put("key",key);
			jsonMap.put("value",map.get(key));
			list.add(jsonMap);
		}
		return net.sf.json.JSONArray.fromObject(list);
	}

	/****************************************************************************/

	// 网元类型1.SDH 2.WDM 3.OTN 4.PTN 5.微波 6.FTTX 9 虚拟网元 99 未知
	public final static int NE_TYPE_SDH_FLAG = 1;
	public final static int NE_TYPE_WDM_FLAG = 2;
	public final static int NE_TYPE_OTN_FLAG = 3;
	public final static int NE_TYPE_PTN_FLAG = 4;
	public final static int NE_TYPE_MICROWAVE_FLAG = 5;
	public final static int NE_TYPE_FTTX_FLAG = 6;
	public final static int NE_TYPE_VIRTUAL_NE_FLAG = 9;
	public final static int NE_TYPE_UNKNOW_FLAG = 99;

	// 网元同步操作类型 1.网元基础数据同步 2.交叉连接同步 3.以太网同步
	public final static int NE_BASIC_SYNC = 1;
	public final static int NE_CROSS_SYNC = 2;
	public final static int NE_MSTP_SYNC = 3;
	
	// 网管拓扑链路同步操作类型 
	public final static int EMS_TOPO_LINK_SYNC = 4;
	
	// 网管类型 1.corba类型连接 2.telnet类型连接
	public final static int CONNETION_TYPE_CORBA = 1;
	public final static int CONNETION_TYPE_TELNET = 2;

	// 网元同步结果 1.已同步 2.未同步 3.同步失败 4.需要同步 5.正在同步
	public final static int NE_SYNC_HAD = 1;
	public final static int NE_SYNC_NOT = 2;
	public final static int NE_SYNC_FAILED = 3;
	public final static int NE_SYNC_NEED = 4;
	public final static int NE_SYNC_DOING = 5;

	// 链路同步结果 1.已同步 2.未同步 3.同步失败 4.需要同步 5.正在同步
	public final static int LINK_SYNC_HAD = 1;
	public final static int LINK_SYNC_GO = 2;
	public final static int LINK_SYNC_FAILED = 3;
	public final static int LINK_SYNC_NEED = 4;
	public final static int LINK_SYNC_DOING = 5;

	// 网管同步结果 任务执行结果 1.执行成功 2.执行失败 3.执行中 4.执行中止 5.暂停 6 部分成功
	public final static int EMS_SYNC_SUCCESS = 1;
	public final static int EMS_SYNC_FAILED = 2;
	public final static int EMS_SYNC_ING = 3;
	public final static int EMS_SYNC_INGSTOP = 4;
	public final static int EMS_SYNC_PAUSE = 5;
	public final static int EMS_SYNC_PARTSUCESS = 6;

	// COLLECT_STATUS 1.等待执行、2.正常采集、3.暂停采集 4.禁止采集
	public final static int WAIT_COLLECT_STATUS = 1;
	public final static int NORMAL_COLLECT_STATUS = 2;
	public final static int PAUSE_COLLECT_STATUS = 3;
	public final static int PROHIBIT_COLLECT_STATUS = 4;

	// COLLECT_SOURCE 数据采集源 1.当前性能 2.历史性能 property
	public final static int CURRENT_PROPERTY = 1;
	public final static int HISTORY_PROPERTY = 2;
	
	// COLLEC_START_TIME 允许采集开始时间 
	public final static String COLLEC_START_TIME = "17:30";
	// COLLEC_END_TIME 允许采集结束时间 
	public final static String COLLEC_END_TIME = "07:30";
	
	// CHANGE_TYPE 1.新增、2.删除
	public final static int CHANGE_TYPE_ADD = 1;
	public final static int CHANGE_TYPE_DELETE = 2;

	// 自动 连接模式
	public final static int CONNECT_MODE_AUTO = 0;
	// 手动 连接模式
	public final static int CONNECT_MODE_MANUAL = 1;

	// corba连接状态 连接正常
	public final static int CONNECT_STATUS_NORMAL_FLAG = 1;
	// corba连接状态 连接异常
	public final static int CONNECT_STATUS_EXCEPTION_FLAG = 2;
	// corba连接状态 网络中断
	public final static int CONNECT_STATUS_INTERRUPT_FLAG = 3;
	// corba连接状态 连接中断
	public final static int CONNECT_STATUS_DISCONNECT_FLAG = 4;


	// 采集等级
	// 第一优先级：人工采集告警、人工采集性能、人工同步资源数据
	// 第二优先级：自动采集性能，自动同步告警
	// 第三优先级：巡检同步（包括资源数据、性能、告警）
	// 第四优先级：自动同步基础数据
	public static final int COLLECT_LEVEL_1 = 1;
	public static final int COLLECT_LEVEL_2 = 2;
	public static final int COLLECT_LEVEL_3 = 3;
	public static final int COLLECT_LEVEL_4 = 4;

	/** mongoDB数据库常量定义 */
	// 当前告警表名
	public final static String T_CURRENT_ALARM = "T_CURRENT_ALARM";
	// 历史告警表名
	public final static String T_HISTORY_ALARM = "T_HISTORY_ALARM";
	/** 故障模块常量定义 */
	// 告警确认 2：未确认 1：已确认
	public final static int IS_ACK_NO = 2;
	public final static int IS_ACK_YES = 1;
	// 告警清除 2：未清除 1：已清除
	public final static int IS_CLEAR_NO = 2;
	public final static int IS_CLEAR_YES = 1;
	// 派单 2 ：不派单 1 ：派单
	public final static int IS_ORDER_NO = 2;
	public final static int IS_ORDER_YES = 1;
	// 告警级别
	public final static int ALARM_PS_INDETERMINATE = 0;// 未知
	public final static int ALARM_PS_CRITICAL = 1;// 紧急
	public final static int ALARM_PS_MAJOR = 2;// 重要
	public final static int ALARM_PS_MINOR = 3;// 次要
	public final static int ALARM_PS_WARNING = 4;// 提示
	public final static int ALARM_PS_CLEARED = 5;// 清除
	// 闪告次数默认值AMOUNT
	public final static int AMOUNT = 0;
	// 新增当前告警过滤器->告警源 1: 告警源选择 2：告警源类型选择
	public final static int ALARM_SOURCE_SELECT = 1;
	public final static int ALARM_SOURCE_TYPE_SELECT = 2;
	// 是否是综告过滤器 1:是 2:不是
	public final static int ALARM_FILTER_COM_REPORT_YES = 1;
	public final static int ALARM_FILTER_COM_REPORT_NO = 2;
	// 过滤器类型 1:自定义 2:割接
	public final static int ALARM_FILTER_TYPE_CUSTOM = 1;
	public final static int ALARM_FILTER_TYPE_CUTOVER = 2;
	// 新增当前告警过滤器->是否为通道告警 1: 是 2：不是
	public final static int CTP_ALARM_YES = 1;
	public final static int CTP_ALARM_NO = 2;
	// 新增当前告警过滤器->状态 1: 启用 2：挂起
	public final static int ALARM_FILTER_STATUS_ENABLE = 1;
	public final static int ALARM_FILTER_STATUS_PENDING = 2;
	// 新增屏蔽器->状态 1: 启用 2：挂起
	public final static int ALARM_SHIELD_STATUS_ENABLE = 1;
	public final static int ALARM_SHIELD_STATUS_PENDING = 2;
	// 告警自动确认->确认方式 1:不确认 2:立即确认 3:定时确认
	public final static int ALARM_NO_CONFIRM = 1;
	public final static int ALARM_IMMEDIATELY_CONFIRM = 2;
	public final static int ALARM_TIMING_CONFIRM = 3;
	// 告警自动确认->默认ID -1
	public final static int ALARM_AUTO_CONFIRM_DEFAULT_ID = -1;
	// 告警自动确认->默认定时时间
	public final static int ALARM_AUTO_CONFIRM_DEFAULT_TIME = 60;
	// 告警及事件重定义->状态 1: 启用 2：挂起
	public final static int ALARM_REDEFINE_STATUS_ENABLE = 1;
	public final static int ALARM_REDEFINE_STATUS_PENDING = 2;
	// 告警推送设置，参数对应数据库中的key
	public final static String ALARM_PUSH_PARAM_KEY = "ALARM_PUSH";
	// 告警自动确认时间设置，参数对应数据库中的key
	public final static String ALARM_CONFIRM_PARAM_KEY = "ALARM_CONFIRM";
	// 告警转移时间设置，参数对应数据库中的key
	public final static String ALARM_SHIFT_PARAM_KEY = "ALARM_SHIFT";
	// 告警自动确认时间设置，默认值
	public final static String ALARM_CONFIRM_PARAM_DEFAULT_VALUE = "false,24";
	// 告警转移时间设置，默认值
	public final static String ALARM_SHIFT_PARAM_DEFAULT_VALUE = "true,1,60";
	// 综告推送-> 告警清除设置  1：每次推送 2：已清除、已确认推送 3：当前告警转移历史告警推送
	
	// 新增告警收敛规则->状态 1: 启用 2：挂起
	public final static int ALARM_COMVERGE_STATUS_ENABLE = 1;
	public final static int ALARM_COMVERGE_STATUS_PENDING = 2;
	// 告警收敛标志
	public final static int ALARM_CONVERGE_UNKNOWN_ALARM = 1;		//未分析告警
	public final static int ALARM_CONVERGE_MAIN_ALARM = 2;			//主告警
	public final static int ALARM_CONVERGE_DERIVATIVE_ALARM = 3;	//衍生告警
	
	// 立即备份对应的参数设置，参数对应数据库中的key
	public final static String MANU_SETTING_VALUE = "MANU_SETTING_VALUE";
	// 自动备份对应的参数设置，参数对应数据库中的key
	public final static String AUTO_SETTING_VALUE_PRI = "AUTO_SETTING_VALUE_PRI";
	public final static String AUTO_SETTING_VALUE_SEC = "AUTO_SETTING_VALUE_SEC";
	
	public final static int COMREPORT_PUSH_CLEAR_EVERY = 1;
	public final static int COMREPORT_PUSH_CLEAR_HAS_CLEAR_CONFIRM = 2;
	public final static int COMREPORT_PUSH_CLEAR_SHIFT = 3;
	// 告警源级别 统一定义 使用TREE.NODE
//	public static final int ALARM_RESOURCE_EMSGROUP = 1;
//	public static final int ALARM_RESOURCE_EMS = 2;
//	public static final int ALARM_RESOURCE_SUBNET = 3;
//	public static final int ALARM_RESOURCE_NE = 4;
//	public static final int ALARM_RESOURCE_SHELF = 5;
//	public static final int ALARM_RESOURCE_UNIT = 6;
//	public static final int ALARM_RESOURCE_SUBUNIT = 7;
//	public static final int ALARM_RESOURCE_PTP = 8;
//	public static final int ALARM_RESOURCE_LEAFMAX = 8;
	// 通道告警 使用ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT
//	public static final int ALARM_CTP = 6;
	// 告警灯是否显示 0:不显示 1:显示 使用 TRUE与FALSE
//	public static final int ALARM_LIGHT_YES = 1;
//	public static final int ALARM_LIGHT_NO = 0;
	// 告警灯默认背景颜色
	public static final String PS_CRITICAL_IMAGE = "#ff0000";
	public static final String PS_MAJOR_IMAGE = "#ff8000";
	public static final String PS_MINOR_IMAGE = "#ffff00";
	public static final String PS_WARNING_IMAGE = "#800000";
	public static final String PS_CLEARED_IMAGE = "#00ff00";
	// 告警字体默认颜色
	public static final String PS_CRITICAL_FONT = "#ffffff";
	public static final String PS_MAJOR_FONT = "#000000";
	public static final String PS_MINOR_FONT = "#000000";
	public static final String PS_WARNING_FONT = "#000000";
	public static final String PS_CLEARED_FONT = "#ffffff";
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
	// 故障诊断状态
	public final static int IS_ANALYSIS_NO = -1;
// 端口类型
	public final static String[] PTP_TYPE_LIST = {"STM-1", "STM-4", "STM-16", "STM-64","STM-256"};
		// 可分析告警名称
	public final static String[] LOS_ALARMS = {"R_LOS","LOS"};
	public final static String[] BER_ALARMS = {"B1_EXC","B1_SD","B2_EXC","B2_SD","B1B_EXC"};
	public static List<String> ANALYSIS_ALARM_LIST = new ArrayList<String>();
		static {
			for (String s : LOS_ALARMS) {
				ANALYSIS_ALARM_LIST.add(s);
			}
			for (String s : BER_ALARMS) {
				ANALYSIS_ALARM_LIST.add(s);
			}
		}
	// 故障处理状态
	public final static int FAULT_PROC_STATUS_NOACK = 1;
	public final static int FAULT_PROC_STATUS_PROCESSING = 2;
	public final static int FAULT_PROC_STATUS_RETRIEVAL = 3;
	public final static int FAULT_PROC_STATUS_COMPLETION = 4;
	public final static int FAULT_PROC_STATUS_ARCHIVE = 5;
	
	//故障管理
	public static class FAULT_MANAGEMENT {
		
		// 故障处理状态
		public final static int FAULT_PROC_STATUS_ALL = 0;
		public final static int FAULT_PROC_STATUS_UNCONFIRMED = 1;
		public final static int FAULT_PROC_STATUS_CONFIRMED = 2;
		public final static int FAULT_PROC_STATUS_RECOVERY = 3;
		public final static int FAULT_PROC_STATUS_ARCHIVE = 4;
		
		//故障源
		public final static int FAULT_SOURCE_ALL = 0;
		public final static int FAULT_SOURCE_AUTOCREATE = 1;
		public final static int FAULT_SOURCE_MANUALCREATE = 2;
		
		// 故障类型
		public final static int FAULT_TYPE_EQPT = 1;
		public final static int FAULT_TYPE_LINE = 2;
		
		//收敛状态
		public final static int FAULT_ALARM_FLAG_UNCONVERGE = 0;
		public final static int FAULT_ALARM_FLAG_MAIN = 1;
		public final static int FAULT_ALARM_FLAG_DERIVE = 2;
	}
	
	//故障诊断
	public static class FAULT_DIAGNOSE_MANAGEMENT {
		
		// 故障诊断规则状态 1: 启用 2：挂起
		public final static int FAULT_DIAGNOSE_STATUS_ENABLE = 1;
		public final static int FAULT_DIAGNOSE_STATUS_PENDING = 2;
	}
	

	/*EquipHolder告警用*/
	public final static int ALARM_OBJECT_TYPE_RACK = 100;
	public final static int ALARM_OBJECT_TYPE_SHELF = 101;
	public final static int ALARM_OBJECT_TYPE_SLOT = 102;
	// 当前告警状态显示过滤定义
	public final static int ALARM_ALL = 1;	// 全部
	public final static int ALARM_CLEARED = 2;	// 已清除
	public final static int ALARM_NOT_CLEARED = 3;	// 未清除
	public final static int ALARM_ACKNOWLEDGED = 4;	// 已确认
	public final static int ALARM_ACKNOWLEDGED_CLEARED = 5;	// 已确认已清除
	public final static int ALARM_ACKNOWLEDGED_NOT_CLEARED = 6;	// 已确认未清除
	public final static int ALARM_NOT_ACKNOWLEDGED = 7;	// 未确认
	public final static int ALARM_CLEARED_NOT_ACKNOWLEDGED = 8;	// 已清除未确认
	public final static int ALARM_NOT_ACKNOWLEDGED_NOT_CLEARED = 9;	// 未确认未清除
	// 告警状态 1:清除告警 2：发生告警 3:确认 4：反确认
	public final static int ALARM_STATUS_CLEARED = 1;
	public final static int ALARM_STATUS_OCCUR = 2;
	public final static int ALARM_STATUS_ACK = 3;
	public final static int ALARM_STATUS_DENY = 4;
	/** *********************************电路模块开始 *******************************************/
	// 电路类型 1.sdh 2 eth 3 OTN 4 PTN
	public final static int CIR_TYPE_SDH = 1;
	public final static int CIR_TYPE_ETH = 2;
	public final static int CIR_TYPE_OTN = 3;
	public final static int CIR_TYPE_PTN = 4;
	
	// 以太网ctp选择 t_base_binding_path表
	public final static int CIR_BINDING_ALL = 0;
	public final static int CIR_BINDING_USE = 1;
	
	//未知元素
	public final static String UNKNOW_ELEMENT="-999";
	// 电路表中查询类型 1. 显示电路 2. 子电路，不显示
	public final static int CIR_SELECT_YES = 1;
	public final static int CIR_SELECT_NO = 2;

	// 任务执行状态 1 成功 2 失败 3 执行中 4 执行中止
	public final static int TASK_SUCCESS = 1;
	public final static int TASK_FAILED = 2;
	public final static int TASK_ON = 3;
	public final static int TASK_HOLD = 4;

	// 端口属性 1.边界点 2.连接点,3是网元内部连接点
	public final static int PORT_TYPE_EDGE_POINT = 1;
	public final static int PORT_TYPE_LINK_POINT = 2;
	public final static int PORT_TYPE_INTERNAL_LINK_POINT = 3;

	// 路由表chain类型 1.sdh交叉连接 2.otn交叉连接 3.内部link 4.外部link
	public final static int CHAIN_TYPE_SDH_CRS = 1;
	public final static int CHAIN_TYPE_OTN_CRS = 2;
	public final static int CHAIN_TYPE_IN_LINK = 3;
	public final static int CHAIN_TYPE_OUT_LINK = 4;

	// 电路表FALG标记
	public final static int FLAG_NORMAL = 1;
	public final static int FLAG_LATEST = 2;
	public final static int FLAG_TEMP = 3;

	// 电路方向 1 单向 2 双向
	public final static int DIRECTION_ONE = 1;
	public final static int DIRECTION_TWO = 2;

	// 链路类型 1 外部 2 内部
	public final static int LINK_OUT = 1;
	public final static int LINK_IN = 2;

	// 是否是电路查询类型： 1. SDH，以太网查询电路，2.以太网子电路
	public final static int CIR_SELECT = 1;
	public final static int CIR_SUB = 2;

	// 交叉连接或链路更新状态： 1. 最近一次新增，2.最近一次删除 ，3 以前删除，4以前新增
	public final static int STATE_ADD_LATEST = 1;
	public final static int STATE_DELETE_LATEST = 2;
	public final static int STATE_DELETE_BEFORE = 3;
	public final static int STATE_ADD_BEFORE = 4;

	// 端口属性 0 ptp 1 ftp
	public final static int CIR_PTP = 0;
	public final static int CIR_FTP = 1;

	// otn 交叉连接 2 不完整虚拟交叉
	public final static int CIR_VIR_NOTFULL = 2;

	// 任务1.启用 2 挂起 3 删除
	public final static int CIR_TASK_ON = 1;
	public final static int CIR_TASK_HOLD = 2;
	public final static int CIR_TASK_DELETE = 3;

	// 任务执行周期 1.日 2.周 3.月 4.季 5.年
	public final static int CIR_TASK_CYCLE_DAY = 1;
	public final static int CIR_TASK_CYCLE_WEEK = 2;
	public final static int CIR_TASK_CYCLE_MONTH = 3;
	public final static int CIR_TASK_CYCLE_SEASON = 4;
	public final static int CIR_TASK_CYCLE_YEAR = 5;
	public static int targetType2svcType(int v){
		switch(v){
		case TASK_TARGET_TYPE.SDH_CIRCUIT:
			return CIR_TYPE_SDH;
		case TASK_TARGET_TYPE.ETH_CIRCUIT:
			return CIR_TYPE_ETH;
		case TASK_TARGET_TYPE.WDM_CIRCUIT:
			return CIR_TYPE_OTN;
		}
		return 0;
	}
	
	/** *********************************电路模块结束 *******************************************/

	/** *********************************资源模块开始 *******************************************/

	// 电路比对三种状态 0 未比对 1 相同 2 不相同
	public final static int RES_CIR_UNCOM = 0;
	public final static int RES_CIR_YES = 1;
	public final static int RES_CIR_NO = 2;
	public final static String RES_AUDIT_PARAMETER_KEY = "RESOURCE_AUDIT";
	public final static String RES_FILE_OUTPUT_KEY = "RESOURCE_FILE_OUTPUT";

	/** *********************************资源模块结束 *******************************************/

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通树部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	public static class TREE {
		public static final int CHILD_MAX = 5000;
		public static final int ROOT_ID = 0;
		public static final String ROOT_TEXT = "FTSP";
		public static final String CHECKED_ALL = "all";
		public static final String CHECKED_PART = "part";
		public static final String CHECKED_NONE = "none";

		// 节点包含信息
		public static final String PROPERTY_NODE_ID = "nodeId";
		public static final String PROPERTY_NODE_LEVEL = "nodeLevel";
		public static final String PROPERTY_TEXT = "text";

		public static Map<String, Object> TREE_DEFINE = new HashMap<String, Object>();
		static {
			TREE_DEFINE.put("FALSE", CommonDefine.FALSE);
			TREE_DEFINE.put("CHILD_MAX", CommonDefine.TREE.CHILD_MAX);
			TREE_DEFINE.put("ROOT_ID", CommonDefine.TREE.ROOT_ID);
			TREE_DEFINE.put("ROOT_TEXT", CommonDefine.TREE.ROOT_TEXT);
			TREE_DEFINE.put("NODE_ROOT", CommonDefine.TREE.NODE.ROOT);
			TREE_DEFINE.put("NODE_EMSGROUP", CommonDefine.TREE.NODE.EMSGROUP);
			TREE_DEFINE.put("NODE_EMS", CommonDefine.TREE.NODE.EMS);
			TREE_DEFINE.put("NODE_SUBNET", CommonDefine.TREE.NODE.SUBNET);
			TREE_DEFINE.put("NODE_NE", CommonDefine.TREE.NODE.NE);
			TREE_DEFINE.put("NODE_SHELF", CommonDefine.TREE.NODE.SHELF);
			TREE_DEFINE.put("NODE_UNIT", CommonDefine.TREE.NODE.UNIT);
			TREE_DEFINE.put("NODE_SUBUNIT", CommonDefine.TREE.NODE.SUBUNIT);
			TREE_DEFINE.put("NODE_PTP", CommonDefine.TREE.NODE.PTP);
			TREE_DEFINE.put("USER_ADMIN_ID", CommonDefine.USER_ADMIN_ID);
			TREE_DEFINE.put("VALUE_ALL", CommonDefine.VALUE_ALL);
			TREE_DEFINE.put("VALUE_NONE", CommonDefine.VALUE_NONE);
			TREE_DEFINE.put("AUTH_ALL", CommonDefine.TRUE);
			TREE_DEFINE.put("AUTH_VIEW", CommonDefine.FALSE);
			TREE_DEFINE.put("DISPLAY_MODE_NONE", RESOURCE_STOCK.NONE_MODE);
		}

		public static class NODE {
			public static final int ROOT = 0;
			public static final int EMSGROUP = 1;
			public static final int EMS = 2;
			public static final int SUBNET = 3;
			public static final int NE = 4;
			public static final int SHELF = 5;
			public static final int UNIT = 6;
			public static final int SUBUNIT = 7;
			public static final int PTP = 8;
			public static final int LEAFMAX = 8;
		}

		public static HashMap<Integer, Integer[]> Childs = new HashMap<Integer, Integer[]>();
		static {
			Childs.put(NODE.ROOT, new Integer[] { NODE.EMSGROUP, NODE.EMS });
			Childs.put(NODE.EMSGROUP, new Integer[] { NODE.EMS });
			Childs.put(NODE.EMS, new Integer[] { NODE.SUBNET, NODE.NE });
			Childs.put(NODE.SUBNET, new Integer[] { NODE.SUBNET, NODE.NE });
			Childs.put(NODE.NE, new Integer[] { NODE.SHELF });
			Childs.put(NODE.SHELF, new Integer[] { NODE.UNIT });
			Childs.put(NODE.UNIT, new Integer[] { NODE.SUBUNIT, NODE.PTP });
			Childs.put(NODE.SUBUNIT, new Integer[] { NODE.PTP });
		}
		public static String[] LEVEL_NAME = { "FTSP", "", "", "", "","","","","","","",
			FieldNameDefine.STATION_NAME, "机房" };
	}

	/** _______________________________ 共通树部分 _______________________________ **/

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 性能部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	/**
	 * 性能常量定义
	 * 
	 * @author ZhongLe
	 * 
	 */
	public static class PM {
		// 光复用段告警
		public static class MUL {
			// 正常
			public static final int SEC_PM_ZC = 0;
			// 一般告警
			public static final int SEC_PM_YB = 1;
			// 次要告警
			public static final int SEC_PM_CY = 2;
			// 重要告警
			public static final int SEC_PM_ZY = 3;
		}
		
		public static class NE_LEVEL {
			public static final int KEY_COLLECT = 1;
			public static final int CYCLE_COLLECT = 2;
			public static final int NO_COLLECT = 3;
		}

		public static class COLLECT_SOURCE {
			public static final int CURRENT_PM = 1;
			public static final int HISTORY_PM = 2;
		}

		public static class TASK_STATUS {
			public static final int INUSE = 1;
			public static final int SUSPEND = 2;
			public static final int DELETEDED = 3;
		}

		public static class COLLECT_STATUS {
			public static final int SUCCESS = 1;
			public static final int FAILED = 2;
			public static final int EXECUTING = 3;
			public static final int BREAKING = 4;
			public static final int PAUSE = 5;
			public static final int PARTLY = 6;
		}
		public static class PM_DATA_KIND {
			public static final int ALL_DATA = 1;
			public static final int EXCEPTION_DATA = 2;
		}

		public static class PM_TYPE {
			public static final int PHYSICAL = 1;
			public static final int COUNT_VALUE = 2;
		}

		public static class PM_TABLE_NAMES {
			public static final String CURRENT_SDH_DATA = "t_pm_temp_sdh_current_data";
			public static final String CURRENT_WDM_DATA = "t_pm_temp_wdm_current_data";
			public static final String HISTORY_SDH_DATA = "T_PM_TEMP_SDH_HISTORY_DATA";
			public static final String HISTORY_WDM_DATA = "T_PM_TEMP_WDM_HISTORY_DATA";
			public static final String ORIGINAL_DATA = "t_pm_origi_data";
		}
		
		public static class PM_EXCEPTION_LEVEL{
			public static final int NORMAL = 0;
			public static final int EXCEPTION_1 = 1;
			public static final int EXCEPTION_2 = 2;
			public static final int EXCEPTION_3 = 3;
		}

		public static class TARGET_TYPE {
			public static final int EMS_GROUP = 1;
			public static final int EMS = 2;
			public static final int UNIT = 6;
			public static final int PTP = 7;
			public static final int CTP_SDH = 8;
			public static final int CTP_OTN = 9;
		}
		public static class CUSTOM_REPORT_TYPE {
			public static final int NE = 0;
			public static final int MULTI_SEC = 1;
		}

		public static class CUSTOM_REPORT {
			public static final int COMBO_NONE = 0;
			public static final int COMBO_AUTO = 1;
			public static final int COMBO_KEY = 2;
			public static final int COMBO_SUBKEY = 3;
			public static final int COMBO_DATE = 4;
			public static final int COMBO_VALUE = 5;
		}

		// 需要等待一段时间再采集的网管异常定义
		public static Set<Integer> CORBA_ERROR_CODE_Set = new HashSet<Integer>();
		static {
			CORBA_ERROR_CODE_Set.add(99022);
			CORBA_ERROR_CODE_Set.add(99021);
		}

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

		public static final int PM_TASK_TYPE = QUARTZ.JOB_PM;

		public static class PM_DIAGRAM {
			public static final String[] lineColor = new String[] { "#366092",
					"#7F7F7F", "#76933C", "#974706", "#7030A0", "#E10000",
					"#00B050" };
		}

		public static class LINK_TYPE {
			public static final int OUTER_LINK = 1;
			public static final int INNER_LINK = 2;
			public static final int MANUAL_LINK = 3;
		}

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
    	
    	public static Map<Integer, Object> DOMAIN_TYPE = new HashMap<Integer, Object>();
		static {
			DOMAIN_TYPE.put(PM.DOMAIN.DOMAIN_SDH_FLAG, PM.DOMAIN_SDH_DISPLAY);
			DOMAIN_TYPE.put(PM.DOMAIN.DOMAIN_WDM_FLAG, PM.DOMAIN_WDM_DISPLAY);
			DOMAIN_TYPE.put(PM.DOMAIN.DOMAIN_ETH_FLAG, PM.DOMAIN_ETH_DISPLAY);
			DOMAIN_TYPE.put(PM.DOMAIN.DOMAIN_ATM_FLAG, PM.DOMAIN_ATM_DISPLAY);
			DOMAIN_TYPE.put(PM.DOMAIN.DOMAIN_UNKNOW_FLAG, PM.DOMAIN_UNKNOW_DISPLAY);
		}

		public static class STM1_INTERVAL {
			public static class MAX_OUT_BLOCK {
				public static final float RANGE1_START = -16;
				public static final float RANGE1_END = -7;
				public static final float RANGE2_START = -7;
				public static final float RANGE2_END = 1;
			}

			public static class MAX_IN_BLOCK {
				public static final float RANGE1_START = -34;
				public static final float RANGE1_END = -25;
				public static final float RANGE2_START = -25;
				public static final float RANGE2_END = -16;
				public static final float RANGE3_START = -16;
				public static final float RANGE3_END = -8;
			}
		}

		public static class STM4_INTERVAL {
			public static class MAX_OUT_BLOCK {
				public static final float RANGE1_START = -16;
				public static final float RANGE1_END = -7;
				public static final float RANGE2_START = -4;
				public static final float RANGE2_END = 3;
			}

			public static class MAX_IN_BLOCK {
				public static final float RANGE1_START = -34;
				public static final float RANGE1_END = -25;
				public static final float RANGE2_START = -25;
				public static final float RANGE2_END = -16;
				public static final float RANGE3_START = -16;
				public static final float RANGE3_END = -8;
			}
		}

		public static class STM16_INTERVAL {
			public static class MAX_OUT_BLOCK {
				public static final float RANGE1_START = -11;
				public static final float RANGE1_END = -5;
				public static final float RANGE2_START = -5;
				public static final float RANGE2_END = -2;
				public static final float RANGE3_START = -2;
				public static final float RANGE3_END = 0;
				public static final float RANGE4_START = 0;
				public static final float RANGE4_END = 4;
			}

			public static class MAX_IN_BLOCK {
				public static final float RANGE1_START = -29;
				public static final float RANGE1_END = -14;
				public static final float RANGE2_START = -14;
				public static final float RANGE2_END = 0;
			}
		}

		public static class STM64_INTERVAL {
			public static class MAX_OUT_BLOCK {
				public static final float RANGE1_START = -6;
				public static final float RANGE1_END = -1;
				public static final float RANGE2_START = -1;
				public static final float RANGE2_END = 2;
				public static final float RANGE3_START = 3;
				public static final float RANGE3_END = 8;
				public static final float RANGE4_START = 10;
				public static final float RANGE4_END = 15;
			}

			public static class MAX_IN_BLOCK {
				public static final float RANGE1_START = -27;
				public static final float RANGE1_END = -14;
				public static final float RANGE2_START = -14;
				public static final float RANGE2_END = 0;
				public static final float RANGE3_START = -27;
				public static final float RANGE3_END = -13;
				public static final float RANGE4_START = -13;
				public static final float RANGE4_END = -2;
			}
		}

		// 缺省光口标准，所以ID确定，且不能删除。NAME:ID
		public static Map<String, Integer> defaultOptStd = new HashMap<String, Integer>();
		static {
			defaultOptStd.put("GB I-1", -1);
			defaultOptStd.put("GB S-1.1", -2);
			defaultOptStd.put("GB S-1.2", -3);
			defaultOptStd.put("GB L-1.1", -4);
			defaultOptStd.put("GB L-1.2", -5);
			defaultOptStd.put("GB L-1.3", -6);
			defaultOptStd.put("GB I-4", -7);
			defaultOptStd.put("GB S-4.1", -8);
			defaultOptStd.put("GB S-4.2", -9);
			defaultOptStd.put("GB L-4.1", -10);
			defaultOptStd.put("GB L-4.2", -11);
			defaultOptStd.put("GB L-4.3", -12);
			defaultOptStd.put("GB I-16", -13);
			defaultOptStd.put("GB S-16.1", -14);
			defaultOptStd.put("GB S-16.2", -15);
			defaultOptStd.put("GB L-16.1", -16);
			defaultOptStd.put("GB L-16.2", -17);
			defaultOptStd.put("GB L-16.3", -18);
			defaultOptStd.put("GB I-64.1r", -19);
			defaultOptStd.put("GB I-64.1", -20);
			defaultOptStd.put("GB I-64.2r", -21);
			defaultOptStd.put("GB I-64.2", -22);
			defaultOptStd.put("GB I-64.3", -23);
			defaultOptStd.put("GB I-64.5", -24);
			defaultOptStd.put("GB S-64.1", -25);
			defaultOptStd.put("GB S-64.2a", -26);
			defaultOptStd.put("GB S-64.2b", -27);
			defaultOptStd.put("GB S-64.3a", -28);
			defaultOptStd.put("GB S-64.3b", -29);
			defaultOptStd.put("GB S-64.5a", -30);
			defaultOptStd.put("GB S-64.5b", -31);
			defaultOptStd.put("GB L-64.1", -32);
			defaultOptStd.put("GB L-64.2a", -33);
			defaultOptStd.put("GB L-64.2b", -34);
			defaultOptStd.put("GB L-64.2c", -35);
			defaultOptStd.put("GB L-64.3a", -36);
			defaultOptStd.put("GB L-64.3b", -37);
			defaultOptStd.put("GB V-64.2a", -38);
			defaultOptStd.put("GB V-64.2b", -39);
			defaultOptStd.put("GB V-64.3", -40);
			defaultOptStd.put("GB V-64.5", -41);
		}

		public static class PM_REPORT {
			// 报表目标类型
			public static class REPORT_TYPE{
				public static final int NE_REPORT = 1;
				public static final int TRUNK_LINE_REPORT = 2;
				public static final int MULTI_SEC_REPORT = 3;
			}
			
			public static final int OTHER_CHECKED = 1;
			public static final int OTHER_UNCHECKED = 0;
			public static final int NEED_EXPORT_CHECKED = 1;
			public static final int NEED_EXPORT_UNCHECKED = 0;

			// 数据源
			public static class DATA_SOURCE {
				// 正常数据
				public static final int NORMAL = 0;
				// 异常数据
				public static final int ABNORMAL = 1;
			}

			// 报表类型
			public static class PERIOD {
				// 正常数据
				public static final int DAILY = 0;
				// 异常数据
				public static final int MONTHLY = 1;
			}

			

		}
		// 光复用段端口类型
		// 路由类型：1.ptp口，2.虚拟端口，3.衰耗器，4.段衰耗，5.光缆，6.其他(空行)
		public static class SECTON_ROUTE_TYPE {
			// ptp口
			public static final int PTP = 1;
			// 虚拟端口
			public static final int VIR_PORT = 2;
			// 衰耗器
			public static final int DOWN = 3;
			// 段衰耗
			public static final int PART_DOWN = 4;
			// 光缆
			public static final int FIBER = 5;
			// 其他(空行)
			public static final int OTHER = 6;
			// 自定义
			public static final int OWN = 7;
		}
		public static class PORT_TYPE {
			//IN
			public static final int PORT_IN = 1;
			// OUT
			public static final int PORT_OUT = 2;
		}
		
		public static class DIRECTION {
			// 正向
			public static final int FORWARD = 1;
			// 反向
			public static final int OPPOSITE = 2;
			
		}
		
		public static final int EXPORT_TYPE_NE_ANALYSIS = 1;
		public static final int EXPORT_TYPE_MS_ANALYSIS = 2;
	}

	/** _______________________________ 性能部分 _______________________________ **/

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 重保部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	public static class IMPT_PROTECT {
		public static class TASK_STATUS	{
			public static final int WAITTING=1<<0;
			public static final int RUNNING	=1<<1;
			public static final int STOPED 	=1<<2;
			public static final int COMPLETED=1<<3;
		}
	}
	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 巡检部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	// 巡检设备初始化加载Flag标记
	public final static int INSPECT_ENGINEER = 1;
	public final static int INSPECT_TASK = 2;

	// 巡检报告查询时间数据
	public final static String ONE_MONTH = "一个月内";
	public final static String SIX_MONTH = "六个月内";
	public final static String ONE_YEAR = "一年内";
	public final static String ALL = "全部";
	// 巡检报告查询
	public final static int INSPECT_ALL = 0;
	public final static int INSPECT_ONE_MONTH = 1;
	public final static int INSPECT_SIX_MONTH = 2;
	public final static int INSPECT_ONE_YEAR = 3;
	public final static int INSPECT_CURRENT_YEAR = 4;

	// 巡检任务周期是否改变标记，用于Quartz
	public final static int PERIOD_CHANGE = 1;
	public final static int PERIOD_NO_CHANGE = 0;

	public final static String TIME_CLOCK = "00";
	public final static String TIME_HALF = "30";

	public final static String PRIVILEGE = "操作权限组";
	public final static String INSPECT_ITEM = "巡检项目";

	public final static int YEAR = 5;
	public final static int QUARTER = 4;
	public final static int MONTH = 3;
	
	//巡检执行结果
//	public static class TASK_RESULT {
//		public final static int INSPECT_SUCCESS = 1;
//		public final static int INSPECT_FAILD = 2;
//		public final static int INSPECT_ING = 3;
//		public final static int INSPECT_END = 4;
//	}
	
	//巡检设备执行状态
//	public static class IS_SUCCESS {
//		public final static int SUCCESS = 1;
//		public final static int FAILD = 0;
//	}
	
	//巡检采集结果
//	public static class IS_COMPLETE {
//		public final static int UNCOMPLETE = 0;
//		public final static int COMPLETE = 1;
//	}

	public static class INSPECT {
		public static final String REPORT_NAME_DATE_FORMAT = "yyyyMMdd_HHmmss";

		public static class CONST {
			public final static int UNDEFINE = 0;

			public final static String valueToString(int value) {
				switch (value) {
				case UNDEFINE:
					return "未定义";
				default:
					return null;
				}
			}
		}

		public static class TASK_ITEM {
			public final static int BASE = 1;
			public final static int PM_COUNT = 2;
//			public final static int PM_PHYSICAL = 3;
			public final static int ALARM = 4;
			public final static int TIME = 5;
			public final static int PROTECT = 6;
			public final static int CLOCK = 7;
			public final static String valueToString(int value) {
				switch (value) {
				case BASE: return "设备基础数据";
				case PM_COUNT: return "性能参数";
//				case PM_PHYSICAL: return "物理量";
				case ALARM: return "设备告警";
				case TIME: return "网元时间检查";
				case PROTECT: return "保护设置和状态检查";
				case CLOCK: return "时钟设置和状态检查";
				default: return null;
				}
			}
			public static class BASE_SUB {
				public final static int CONNECT_STATE = 11;
				public final static int COMMUNICATION_STATE = 12;
				public final static int SYNC_STATE = 13;
				public final static String valueToString(int value) {
					switch (value) {
					case CONNECT_STATE: return "网元连接";
					case COMMUNICATION_STATE: return "网元在线";
					case SYNC_STATE: return "网元基础数据";
					default: return null;
					}
				}
			}
			public static class PM_SUB {
				public final static int COUNT = 21;
				public final static int PHYSICAL = 32;
				public final static String valueToString(int value) {
					switch (value) {
					case COUNT: return "计数值";
					case PHYSICAL: return "物理量";
					default: return null;
					}
				}
			}
			public static class ALARM_SUB {
				public final static int ALARM = 41;
				public final static String valueToString(int value) {
					switch (value) {
					case ALARM: return "网元告警";
					default: return null;
					}
				}
			}
			public static class TIME_SUB {
				public final static int TIME = 51;
				public final static String valueToString(int value) {
					switch (value) {
					case TIME: return "网元时间";
					default: return null;
					}
				}
			}
			public static class PROTECT_SUB {
				public final static int PROTECT = 61;
				public final static String valueToString(int value) {
					switch (value) {
					case PROTECT: return "保护状态";
					default: return null;
					}
				}
			}
			public static class CLOCK_SUB {
				public final static int SETTING = 71;
				public final static int CURRENT = 72;
				public final static int QUALITY = 73;
				public final static String valueToString(int value) {
					switch (value) {
					case SETTING: return "时钟设置";
					case CURRENT: return "当前时钟";
					case QUALITY: return "时钟质量";
					default: return null;
					}
				}
			}
		}

		public static class BASE_SYNC_STATE {
			public final static String valueToString(Integer value) {
				if(value==null)return "未同步";
				switch (value) {
				case NE_SYNC_HAD:
					return "已同步";
				case NE_SYNC_NOT:
					return "未同步";
				case NE_SYNC_FAILED:
					return "同步失败";
				case NE_SYNC_NEED:
					return "需要同步";
				case NE_SYNC_DOING:
					return "正在同步";	
				default:
					return "异常";
				}
			}
		}
		public static class IS_DEL {
			public final static String valueToString(Integer value) {
				if(value==null)return "正常";
				switch (value) {
				case TRUE:
				case DELETE_FLAG:
					return "已删除";
				case FALSE:
					return "正常";
				default:
					return "异常";
				}
			}
		}
		public static class CLOCK_QUALITY {
//			0：CSQ_LEVELUNKNOWN——表示未知。
//            1：CSQ_G811——表示G.811。
//            2：CSQ_G812TRANSIT——表示G.812Transit。
//            3：CSQ_G812LOCAL——表示G.812Local。
//            4：CSQ_G813——表示G.813。
//            5：CSQ_NOTFORSYNCLK——表示非同步时钟源。
			public final static int LEVELUNKNOWN=0;
			public final static int G811=1;
			public final static int G812TRANSIT=2;
			public final static int G812LOCAL=3;
			public final static int G813=4;
			public final static int NOTFORSYNCLK=5;
			public final static String valueToString(Integer value) {
				if(value==null)return "未知";
				switch (value) {
				case LEVELUNKNOWN:
					return "未知";
				case G811:
					return "G.811";
				case G812TRANSIT:
					return "G.812Transit";
				case G812LOCAL:
					return "G.812Local";
				case G813:
					return "G.813";
				case NOTFORSYNCLK:
					return "非同步时钟源";
				default:
					return "未知";
				}
			}
		}
		public static class SCHEMA_STATE {
			public final static int UNKNOWN = 0;
			public final static int AUTOMATIC = 1;
			public final static int FORCED_OR_LOCKED_OUT = 2;

			public final static String valueToString(int value) {
				switch (value) {
				case UNKNOWN:
					return "未知/正常";
				case AUTOMATIC:
					return "自动倒换";
				case FORCED_OR_LOCKED_OUT:
					return "强制(人工)倒换/锁定";
				default:
					return null;
				}
			}
		}
		public static class PRO_GROUP_CATEGORY {
			public final static int PROTECTION = 1;
			public final static int EPROTECTION = 2;
			public final static int WDMPROTECTION = 3;
			public final static int ATMPROTECTION = 4;
			public final static int RPRPROTECTION = 5;

			public final static String valueToString(int value) {
				switch (value) {
				case PROTECTION:
					return "线路保护";
				case EPROTECTION:
					return "设备保护";
				case WDMPROTECTION:
					return "WDM保护";
				case ATMPROTECTION:
					return "ATM保护";
				case RPRPROTECTION:
					return "RPR保护";
				default:
					return null;
				}
			}
		}
		public static class PRO_EQUIP_TYPE {
			public final static int PROTECTING = 0;
			public final static int PROTECTED = 1;

			public final static String valueToString(int value) {
				switch (value) {
				case PROTECTING:
					return "保护对象";
				case PROTECTED:
					return "被保护对象";
				default:
					return null;
				}
			}
		}

		public static class SEVERITY {
			public static final int NORMAL = 0;
			public static final int MINOR = 1;
			public static final int MAJOR = 2;
			public static final int CRITICAL = 3;

			public final static String valueToString(int value) {
				switch (value) {
				case NORMAL:
					return "正常";
				case CRITICAL:
					return "严重";
				case MAJOR:
					return "重要";
				case MINOR:
					return "一般";
				default:
					return null;
				}
			}
			public final static int fromAlarmSeverity(Integer value) {
				if(value==null) return NORMAL;
				switch (value) {
				case ALARM_PS_CRITICAL:
					return CRITICAL;
				case ALARM_PS_MAJOR:
					return MAJOR;
				case ALARM_PS_MINOR:
				case ALARM_PS_WARNING:
					return MINOR;
				default:
					return NORMAL;
				}
			}
		}

		public static final int EXCEPTION_COUNT_CR = 1;

		// 性能异常等级 0：正常 1：告警等级1 2：告警等级2 3：告警等级3
		public static class EXCEPTION_LV {
			public static final int NORMAL = 0;
			public static final int MINOR = 1;
			public static final int MAJOR = 2;
			public static final int CRITICAL = 3;
		}
		
		public static class LAYER_RATE {
			public static final int _2M = 5;
			public static final int _VC12 = 11;
			public static final int _VC3 = 13;
		}

		//发光功率
		public static final int GROUP_TPL = 1;
		//收光功率
		public static final int GROUP_RPL = 2;
		//工作温度
		public static final int GROUP_LTEMP = 3;
		//工作电流
		public static final int GROUP_LBIAS = 4;
		//再生段误码(B1)
		public static final int GROUP_RS = 5;
		//复用段误码(B2)
		public static final int GROUP_MS = 6;
		//VC4通道误码(B3)
		public static final int GROUP_VC4 = 7;
		//VC3/VC12通道误码(B3/V5)
		public static final int GROUP_VC3_VC12 = 8;
		//信道功率
		public static final int GROUP_PCLSOP = 9;
		//信道中心波长/偏移
		public static final int GROUP_PCLSWL= 10;
		//信道信噪比
		public static final int GROUP_PCLSSNR = 11;
		//光监控信道误码
		public static final int GROUP_OSC = 12;
		//FEC误码率
		public static final int GROUP_FEC = 13;
		//OTU误码
		public static final int GROUP_OTU = 14;
		//ODU误码
		public static final int GROUP_ODU = 15;
		//物理量其他
		public static final int GROUP_PHYSICAL_OTHERS = 16;
		//计数值其他
		public static final int GROUP_COUNT_OTHERS = 17;
		
		//发光功率,收光功率
		public static final String[] pmSdhPhysicalStdIndex = {
			String.valueOf(GROUP_TPL),String.valueOf(GROUP_RPL)
		};
		//再生段误码(B1),复用段误码(B2),VC4通道误码(B3),VC3/VC12通道误码(B3/V5)
		public static final String[] pmSdhCountStdIndex = {
			String.valueOf(GROUP_RS),String.valueOf(GROUP_MS),
			String.valueOf(GROUP_VC4),String.valueOf(GROUP_VC3_VC12)
		};
		//发光功率,收光功率，信道信噪比，信道中心波长/偏移，信道光功率
		/*public static final String[] pmWdmPhysicalStdIndex = {
			String.valueOf(GROUP_TPL),String.valueOf(GROUP_RPL),
			String.valueOf(GROUP_PCLSSNR),String.valueOf(GROUP_PCLSWL),
			String.valueOf(GROUP_PCLSOP)
		};*/
		//光监控信道误码,FEC误码率,OTU误码,ODU误码
		public static final String[] pmWdmCountStdIndex = {
			String.valueOf(GROUP_OSC),String.valueOf(GROUP_FEC),
			String.valueOf(GROUP_OTU),String.valueOf(GROUP_ODU),
			//物理量
			String.valueOf(GROUP_TPL),String.valueOf(GROUP_RPL),
			String.valueOf(GROUP_PCLSSNR),String.valueOf(GROUP_PCLSWL),
			String.valueOf(GROUP_PCLSOP)
		};
	}

	/** _______________________________ 巡检部分 _______________________________ **/

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 视图部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	// 特殊节点ID（FTSP节点ID）
	public final static int FTSP_NODE = -1;
	
	// 节点类型
	public final static int VIEW_TYPE_FTSP = -1;
	public final static int VIEW_TYPE_EMSGROUP = 1;
	public final static int VIEW_TYPE_EMS = 2;
	public final static int VIEW_TYPE_SUBNET = 3;
	public final static int VIEW_TYPE_NE = 4;

	// 告警级别定义
	public static final int PS_CRITICAL = 1;
	public static final int PS_MAJOR = 2;
	public static final int PS_MINOR = 3;
	public static final int PS_WARNING = 4;
	
	public final static int INVALID_VALUE = -1;
	/** _______________________________ 视图部分 _______________________________ **/

	
	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 数据采集部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	public static class BASE {
		public static class COMMUNICATION_STATE {
			public static final int AVAILABLE = 0;
			public static final int UNAVAILABLE = 1;
			public static final int UNKNOW = 2;
			public final static String valueToString(Integer value) {
				if(value==null) return null;
				switch (value) {
				case AVAILABLE:
					return "在线";
				case UNAVAILABLE:
					return "离线";
				default:
					return "未知";
				}
			}
		}

		public static class SYNC_STATUS {
			public static final int YES = 0;
			public static final int NOT = 1;
			public static final int FAILED = 2;
		}
	}

	/** _______________________________ 数据采集部分 _______________________________ **/

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 割接部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	public static class CUTOVER {
		public static class CUTOVER_PARAM_NAME {
			public static final String STATUS = "任务状态";
			public static final String FILTER_ALARM = "过滤告警";
			public static final String SNAPSHOT = "快照时间";
			public static final String START_TIME_ACTUAL = "startTimeActual"; 
			public static final String END_TIME_ACTUAL = "endTimeActual"; 
			//割接前快照标记
			public static final String SNAPSHOT_BEFORE_FLAG = "snapshotBeforeFlag"; 
			//过滤告警标记
			public static final String FILTER_ALARM_FLAG = "filterAlarmFlag"; 
			//割接后快照标记
			public static final String SNAPSHOT_AFTER_FLAG = "snapshotAfterFlag"; 
			//割接完成标记
			public static final String COMPLETE_CUTOVER_TASK_FLAG = "completeCutoverTaskFlag"; 
			public static final String AUTO_UPDATE_COMPARE_VALUE = "autoUpdateCompareValue";
			public static final String SNAPSHOT_NO = "0";
			public static final String SNAPSHOT_IMMEDIATELY = "-1";
		}
		
		public static class CUTOVER_PARAM_VALUE {
			public static final String STATUS = "任务状态";
			public static final String FILTER_ALARM = "过滤告警";
			public static final String SNAPSHOT = "快照时间";
			public static final String START_TIME_ACTUAL = "startTimeActual"; 
			public static final String END_TIME_ACTUAL = "endTimeActual"; 
			//割接前快照标记
			public static final int SNAPSHOT_BEFORE_NO = 0; 
			public static final int SNAPSHOT_BEFORE_YES = 1; 
			//过滤告警标记
			public static final int FILTER_ALARM_NO = 0; 
			public static final int FILTER_ALARM_YES = 1; 
			//割接后快照标记
			public static final int SNAPSHOT_AFTER_NO = 0; 
			public static final int SNAPSHOT_AFTER_YES = 1; 
			
			//割接完成标记
			public static final int COMPLETE_CUTOVER_TASK_NO = 0; 
			public static final int COMPLETE_CUTOVER_TASK_YES = 1;
			
			//自动更新基准值标记
			public static final int AUTO_UPDATE_NO = 0; 
			public static final int AUTO_UPDATE_YES = 1; 
		}
		
		public static class CUTOVER_LEVEL_VALUE {
			public static final String URGENT = "紧急";
			public static final String IMPORTANT = "重要";
			public static final String MINOR = "次要";
			public static final String PROMPT = "提示"; 
			//割接前快照标记
			public static final String URGENT_VALUE = "1"; 
			public static final String IMPORTANT_VALUE = "2"; 
			public static final String MINOR_VALUE = "3"; 
			public static final String PROMPT_VALUE = "4"; 
			
			
		}
		
		public static class CUTOVER_CATEGORY_VALUE {
			public static final String UNCHANGED = "不变";
			public static final String CLEAR = "消除";
			public static final String ADD = "新增";
			//割接前快照标记
			public static final String UNCHANGED_VALUE = "1"; 
			public static final String CLEAR_VALUE = "2"; 
			public static final String ADD_VALUE = "3";
	
		}
		
		public static class CUTOVER_TYPE_VALUE {
			public static final String COMMUNICATION = "通信";
			public static final String SERVICE = "服务";
			public static final String EQUIPMENT = "设备";
			public static final String HANDLE = "处理";
			public static final String ENVIRONMENT = "环境";
			public static final String SAFETY = "安全";
			public static final String CONNECTION = "连接";

			public static final String COMMUNICATION_VALUE = "0"; 
			public static final String SERVICE_VALUE = "1"; 
			public static final String EQUIPMENT_VALUE = "2";
			public static final String HANDLE_VALUE = "3"; 
			public static final String ENVIRONMENT_VALUE = "4"; 
			public static final String SAFETY_VALUE = "5";
			public static final String CONNECTION_VALUE = "6";
	
		}
	}

	/** _______________________________ 割接部分 _______________________________ **/
	
	/** _______________________________ 网络分析 _______________________________ **/
	public static HashMap<String, Integer[]> VCRATE = new HashMap<String, Integer[]>();
	static {
		VCRATE.put("2M", new Integer[] {0, 1});
		VCRATE.put("155M", new Integer[] {1,63});
		VCRATE.put("622M", new Integer[] {4,252});
		VCRATE.put("2.5G", new Integer[] {16,1008});
		VCRATE.put("10G", new Integer[] {64,4032});
		VCRATE.put("40G", new Integer[] {128,8064});
		VCRATE.put("100G", new Integer[] {256,16128}); 
	}

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	public static boolean DEBUG = false;//在servlet初始化时设置值
	public static String PATH_ROOT = null;//在servlet初始化时设置值
	public static boolean AUTO_LOGIN="true".equals(CommonUtil.getSystemConfigProperty("autoLogin"));
	public static class EXCEL {
		public static final String TEMP_DIR = "ExportTemp";
		public static final String REPORT_DIR = "Report";
		public static final String INSTANT_REPORT_DIR = "InstantReport";
		public static final String NX_REPORT_DIR = "NxReport";
		public static final String NX_INSTANT_REPORT_DIR = "NxInstantReport";
		public static final String INSPECT_BASE = "Inspect";
		public static final String 	CUTOVER_BASE = "cutover";
		public static final String 	PM_BASE = "PerformanceManager";
		public static final String 	PM_EXCEL = "Excel";
		public static final String 	PM_CSV = "Csv";
		// 导入路径配置
		public final static String UPLOAD_PATH = "uploadedFiles";
		// 导入路径配置
		public final static String UPLOAD_PATH_EP_CONTENT = "emergencyContent";
		public final static String NC_CIRCUIT_RESOURCE = "ncCircuitResource";
	
		public static final int HEADER_DEFAULT = 1000;
		//新增链路导出
		public static final int LAST_CIRCUIT=7005;
		//子电路清单导出
		public static final int SUB_CIRCUIT_EXPORT=7004;
		//交叉连接导出
		public static final int CROSSCONNECT_EXPORT=7003;

		//sdh电路路由详情导出
		public static final int SDH_ROUTE=7006;
		public static final int SDH_INFO=7007;

		//光复用段详细信息导出
		public static final int SEC_DETAIL_EXPORT=6111;
		// 光复用段全部信息导出
		public static final int SEC_ALL_EXPORT=6113;
		
		//光复用段详细信息导出(割接)
		public static final int SEC_DETAIL_CUTOVER_EXPORT=6112;

		//链路导出
		public static final int LINK_EXPORT=9001;
		public static final int LINK_SHOW_EXPORT = 9100;
		//光缆导出
		public static final int CABLE_EXPORT=9004;
		//光纤导出
		public static final int FIBLE_EXPORT=9005;
		//区域资源导出 基地址(4种不同的导出类型对应基地址+偏移量)
		public static final int AREA_RESOURCE_BASE = 9010;
		//相关性导出
		public static final int ABOUTQUERY_EXPORT=7001;
		//端到端导出
		public static final int PTP_EXPORT=7002;
		//odf导出
		public static final int ODF_EXPORT=9002;
		//ddf导出
		public static final int DDF_EXPORT=9003;
		//最大导出记录个数
		public static final int MAX_EXPORT_SIZE=2000;
		//南向连接导出
		public static final int SOUTH_CONNECTION_EXPORT=12001;
		//巡检包机人导出
		public static final int INSPECT_ENGINEER_EXPORT=11001;
		//网元变更列表导出
		public static final int NE_ALTER_LIST_EXPORT=12002;
		
		//资源存量管理列表导出（网元）
		public static final int NE_RESOURCES_TOCK_LIST_EXPORT=12003;
		//资源存量管理列表导出（子架）
		public static final int SHELF_RESOURCES_TOCK_LIST_EXPORT=12004;
		//资源存量管理列表导出（板卡）
		public static final int UNIT_RESOURCES_TOCK_LIST_EXPORT=12005;
		//资源存量管理列表导出（端口）
		public static final int PTP_RESOURCES_TOCK_LIST_EXPORT=12006;
		
		public static final int NE_ANALYSIS_HEADER_CODE = 6003;
		public static final int MS_ANALYSIS_HEADER_CODE = 6002;
		public static final int CFMS_ANALYSIS_HEADER_CODE = 6004;
		public static final int CFPTP_ANALYSIS_HEADER_CODE = 6005;
		public static final int CFNE_ANALYSIS_HEADER_CODE = 6001;
		public static final int PM_SEARCH_HEADER_CODE = 6006;
		//割接性能导出
		public static final int CUTOVER_PERFORMANCE_HEADER=10001;
		//割接电路导出
		public static final int CUTOVER_CIRCUIT_HEADER=10002;
		//割接告警导出
		public static final int CUTOVER_ALARM_HEADER=10003;
		
		//性能按网管统计导出
		public static final int REPORT_PERFROMANCE_EMS=5004;
		//资源按网管统计导出
		public static final int REPORT_RESOURCE_EMS=5003;
		//告警按网管统计导出
		public static final int REPORT_ALARM_EMS=5001;
		//当前告警导出
		public static final int CURRENT_ALRAM=5005;
		//历史告警导出
		public static final int HISTORY_ALRAM=5006;
		
		//日志管理导出
		public static final int LOG_MANAGE=5008;
		//性能越限
		public static final int PM_EXCEED=5009;
		//网络分析部分的导出
		//网元资源预警分析
		public static final int NE_EARLY_WARN=2001; 
		//复用段资源预警分析
		public static final int MULTI_SEC_EARLY_WARN=2002;
		//超大环
		public static final int SUPER_BIG=2003;
		//多环节点
		public static final int MULTI_CIRCLE=2004;
		//长单链
		public static final int LONG_SINGLE=2005;
		//大汇聚点
		public static final int BIG_GATHER=2006;
		//无保护环
		public static final int WITHOUT_PROTECTION=2007; 
		//未成环网元
		public static final int NONE_CIRCLE=2008; 
		//复用率预警信息的详细内容
		public static final int TRANS_SYS=2009;  
		//槽道可用率
		public static final int SLOT_AVAILABILITY=8001; 
		//端口可用率
		public static final int PORT_AVAILABILITY=8002; 
		//时隙可用率
		public static final int CTP_AVAILABILITY=8003;
		//端口可用率（资源）
		public static final int PORT_AVAILABILITY_ROUTE=8004; 
		
		public static final int LINK_AVAILABILITY_VC12=8098;
		public static final int LINK_AVAILABILITY_VC4=8099;
	}
	public static class REPORT {
		//计划任务报表
		public static final int REPORT_SCHEDULE=0;
		//即时生成报表
		public static final int REPORT_INSTANT=1;
		//计划任务报表
		public static final int REPORT_PREVIEW=2;
	}
	public final static String SUCCESS_DISPLAY = "成功";
	public final static String FAILED_DISPLAY = "失败";
	//下拉框显示all
	public final static int VALUE_ALL = -99;
	//下拉框显示无
	public final static int VALUE_NONE = -1;
	public final static int USER_ADMIN_ID = -1;//系统级用户ID,用于绕过权限验证
	public final static int USER_SYSTEM_ID = 0;//自动任务性能查询时临时表需要
	
	/** _______________________________ 共通部分 _______________________________ **/
	public static class TASK_TARGET_TYPE {
		public static final int EMSGROUP = TREE.NODE.EMSGROUP;
		public static final int EMS = TREE.NODE.EMS;
		public static final int SUBNET = TREE.NODE.SUBNET;
		public static final int NE = TREE.NODE.NE;
		public static final int SHELF = TREE.NODE.SHELF;
		public static final int UNIT = TREE.NODE.UNIT;
		public static final int SUBUNIT = TREE.NODE.SUBUNIT;
		public static final int PTP = TREE.NODE.PTP;
		public static final int SDH_CTP = 12;
		public static final int OTN_CTP = 13;
		public static final int TRUNK_LINE = 10;
		public static final int MULTI_SEC = 11;
		public static final int SDH_CIRCUIT = 70;
		public static final int WDM_CIRCUIT = 71;
		public static final int ETH_CIRCUIT = 72;
		public static final int AMP = NX_REPORT.ADDEND+NX_REPORT.UNIT_TYPE.AMP;//101
		public static final int SWITCH = NX_REPORT.ADDEND+NX_REPORT.UNIT_TYPE.SWITCH;//102
		public static final int WAVE_JOIN = NX_REPORT.ADDEND+NX_REPORT.UNIT_TYPE.WAVE_JOIN;//103
		public static final int WAVE_DIV = NX_REPORT.ADDEND+NX_REPORT.UNIT_TYPE.WAVE_DIV;//104
	}
	
	public static Map<String, Object> TARGET_TYPE_MAP = new HashMap<String, Object>();
	static {
		TARGET_TYPE_MAP.put("EMSGROUP",CommonDefine.TASK_TARGET_TYPE.EMSGROUP);
		TARGET_TYPE_MAP.put("EMS",CommonDefine.TASK_TARGET_TYPE.EMS);
		TARGET_TYPE_MAP.put("SUBNET",CommonDefine.TASK_TARGET_TYPE.SUBNET);
		TARGET_TYPE_MAP.put("NE",CommonDefine.TASK_TARGET_TYPE.NE);
		TARGET_TYPE_MAP.put("SHELF",CommonDefine.TASK_TARGET_TYPE.SHELF);
		TARGET_TYPE_MAP.put("EQUIPMENT",CommonDefine.TASK_TARGET_TYPE.UNIT);
		TARGET_TYPE_MAP.put("SUBUNIT",CommonDefine.TASK_TARGET_TYPE.SUBUNIT);
		TARGET_TYPE_MAP.put("PTP",CommonDefine.TASK_TARGET_TYPE.PTP);
		TARGET_TYPE_MAP.put("SDH_CTP",CommonDefine.TASK_TARGET_TYPE.SDH_CTP);
		TARGET_TYPE_MAP.put("OTN_CTP",CommonDefine.TASK_TARGET_TYPE.OTN_CTP);
		TARGET_TYPE_MAP.put("TRUNK_LINE",CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE);
		TARGET_TYPE_MAP.put("MULTI_SEC",CommonDefine.TASK_TARGET_TYPE.MULTI_SEC);
		TARGET_TYPE_MAP.put("SDH_CIRCUIT",CommonDefine.TASK_TARGET_TYPE.SDH_CIRCUIT);
		TARGET_TYPE_MAP.put("WDM_CIRCUIT",CommonDefine.TASK_TARGET_TYPE.WDM_CIRCUIT);
		TARGET_TYPE_MAP.put("ETH_CIRCUIT",CommonDefine.TASK_TARGET_TYPE.ETH_CIRCUIT);
	}
	
	public static class QUARTZ {

		//系统级的任务，定义为统一的taskId,通过taskType来进行区分
		public static final int SYSTEM_TASK_ID = -10;
		// 任务类型 5.自动同步link性能数据 6.性能采集 7.电路自动生成 8.定制报表（网元）9.定制报表（复用段） 10.割接任务 11.巡检任务 12.网管基础信息同步
		public static final int JOB_SYNC_LINK_PM = 5;
		public static final int JOB_PM = 6;
		public static final int JOB_CIRCUIT = 7;
		public static final int JOB_REPORT_NE = 8;
		public static final int JOB_REPORT_MS = 9;
		public static final int JOB_CUTOVER = 10;
		public static final int JOB_INSPECT = 11;
		public static final int JOB_BASE = 12;
		public static final int JOB_TESTQUARTZ = 14;
		public static final int JOB_ALARMSYNCH = 15;
		public static final int JOB_ZIDONGQUEREN = 16;
		public static final int JOB_ZHUANYI = 17;
		public static final int JOB_DATABACKUP = 18;
		public static final int JOB_WDMMS_CUTOVER = 19;
		public static final int JOB_PERFORMANCE_SP = 20;
		public static final int JOB_ALARM_GENERATE= 21;
		public static final int JOB_NE_EARLY_ALARM= 22;
		public static final int JOB_IMPT_PROTECT= 23;
		public static final int JOB_NOTIFICATION= 30;
		public static final int JOB_ATUO_CREATE_RESOURCE_CSV= 31;
		//---宁夏报表用---
		public static final int JOB_NX_REPORT_WDM_WAVELENGTH= 24;
		public static final int JOB_NX_REPORT_WDM_AMP= 25;
		public static final int JOB_NX_REPORT_WDM_SWITCH= 26;
		public static final int JOB_NX_REPORT_WAVE_JOIN= 27;
		public static final int JOB_NX_REPORT_WAVE_DIV= 28;
		public static final int JOB_NX_REPORT_SDH_PM = JOB_REPORT_NE;
		public static final int JOB_NX_REPORT_PTN_IPRAN = 29;
		public static final int JOB_NX_REPORT_PTN_FLOW_PEAK = 32;
		public static final int JOB_NX_REPORT_PTN_DATA_SOURCE = 33;
		//--宁夏资源关联用--
		public static final int JOB_RESOURCE_CORRELATION= 34;
		
		public static final String[] TYPE_NAMES = { "FLD0",// 0
				"FLD1",// 1
				"FLD2",// 2
				"FLD3",// 3
				"FLD4",// 4
				"自动同步link性能数据",// 5
				"性能采集",// 6
				"电路自动生成",// 7
				"定制报表(网元)",// 8
				"定制报表(复用段)",// 9
				"割接任务",// 10
				"巡检任务",// 11
				"网管基础信息同步",// 12
				"FLD13", // 13
				"Quartz测试", // 14
				"告警自动同步",//15
				"告警自动确认", // 16
				"告警转移", // 17
				"数据备份",//18
				"WDM复用段割接任务",//19
				"性能报表数据汇聚",//20
				"告警报表数据抽取", //21
				"网元资源预警分析",//22
				"重要保障任务",//23
				"WDM波长转换盘作业计划",//24
				"WDM光放大器作业计划",//25
				"WDM光开关盘作业计划",//26
				"WDM合波盘作业计划",//27
				"WDM分波盘作业计划",//28
//				"SDH性能作业计划",//同8
				"PTN端口作业计划",//29
				"增量消息更新",//30
				"自动生成资源稽核数据CSV",//31

				"PTN端口流量峰值作业计划",//32
				"PTN端口流量峰值数据采集",//33
				"资源数据关联",//34
		};
		// job状态值
		public static final int JOB_ACTIVATE = 1;//立即执行
		public static final int JOB_PAUSE = 2;//挂起
		public static final int JOB_DELETE = 3;//删除
		public static final int JOB_RESUME = 4;//启用
		// task状态值
		public static class TASK{
			public static class STATUS{
				public static final int START_UP = 1;//启用
				public static final int HANG_UP = 2;//挂起
				public static final int DELETE = 3;//删除
				public final static String valueToString(int value) {
					switch (value) {
					case START_UP:
						return "启用";
					case HANG_UP:
						return "挂起";
					case DELETE:
						return "删除";
					default:
						return null;
					}
				}
				public final static int toJobStatus(int value) {
					switch (value) {
					case START_UP:
						return JOB_RESUME;
					case HANG_UP:
						return JOB_PAUSE;
					case DELETE:
						return JOB_DELETE;
					default:
						return JOB_PAUSE;
					}
				}
			}
			public static class ACTION_STATUS{
				// 任务执行情况
				public static final int COMPLETED = 1;
				public static final int PARTLY_COMPLETED = 2;
				public static final int RUNNING = 3;
				public static final int WAITING = 4;
				public static final int UNCOMPLETED = 5;
				public final static String valueToString(int value) {
					switch (value) {
					case COMPLETED:
						return "完成";
					case PARTLY_COMPLETED:
						return "部分完成";
					case RUNNING:
						return "运行中";
					case WAITING:
						return "等待";
					case UNCOMPLETED:
						return "失败";
					default:
						return null;
					}
				}
			}
		}
	}

	
	public static class PM_SEARCH_TYPE {
		public static final int PM_SEARCH = 1;
		public static final int IMPT_PRO_SEARCH = 2;
	}
	
	//用于长时间操作流程的进度百分比的保存
	//key由sessionId+操作时间戳组成
	public static HashMap<String, ProcessModel> PROCESS_MAP = new HashMap<String, ProcessModel>();
	//取得进度信息
	public static ProcessModel getProcessParameter(String sessionId,String processKey){

		String key = sessionId + "_" + processKey;
		
		ProcessModel processModel = CommonDefine.PROCESS_MAP.get(key);
		if(processModel == null){//首次获取进度信息,且程序尚未初始化进度
			processModel = new ProcessModel(null,null,null,false);
			CommonDefine.PROCESS_MAP.put(key, processModel);
		}else{
//				if(processModel.isCanceled()){
//					processModel = new ProcessModel("用户取消",1,true);
//				}
		}
		
		return processModel;
	}

	//设置是否取消操作参数
	public static void setIsCanceledParameter(String sessionId,String processKey,boolean isCanceled){
		String key = sessionId + "_" + processKey;
		if(CommonDefine.PROCESS_MAP.containsKey(key)){
			CommonDefine.PROCESS_MAP.get(key).setCanceled(isCanceled);
		}
	}
	
	//移除进度信息
	public static void removeProcessParameter(String sessionId,String processKey){
		String key = sessionId + "_" + processKey;
		if(CommonDefine.PROCESS_MAP.containsKey(key)){
			CommonDefine.PROCESS_MAP.remove(key);
		}
	}

	// 添加进度信息
	public static void setProcessParameter(String sessionId, String processKey,
			Integer cur, Integer total, String additionalText) {
		if(processKey==null)
			return;
		
		String key = sessionId + "_" + processKey;
		
		ProcessModel processModel = CommonDefine.PROCESS_MAP.get(key);
		if (processModel == null) {
			processModel = new ProcessModel(cur,total,additionalText,false);
			CommonDefine.PROCESS_MAP.put(key, processModel);
		}else{
			if(total!=null)
				processModel.setTotal(total);
			if(cur!=null)
				processModel.setCur(cur);
			if(additionalText!=null)
				processModel.setAdditionalText(additionalText);
		}
	}
	
	// 取得是否取消操作信息
	public static boolean getIsCanceled(String sessionId, String processKey) {
		String key = sessionId + "_" + processKey;
		ProcessModel processModel = CommonDefine.PROCESS_MAP.get(key);
		boolean isCanceled = false;
		if (processModel != null) {
			isCanceled = processModel.isCanceled();
		}
		return isCanceled;
	}
	//设置是否取消操作参数
	public static void respCancel(String sessionId,String processKey){
		String key = sessionId + "_" + processKey;
		if(CommonDefine.PROCESS_MAP.containsKey(key)){
			CommonDefine.PROCESS_MAP.get(key).respCancel();
		}
	}
	
	//用于管理连接到平台的综告接口源IP
	public  static Map<String,Integer> IP_MAP=new HashMap<String, Integer>();
	//告警同步确认计时器
	public  static Map<String,Integer> GJ_SYNC_MAP=new HashMap<String, Integer>();
	//webService 交互失效时间
	public  static int TIME_OUT=10000;
	//webService 消息连续推送次数认定失败
    public  static int SEND_TIME=3;
    
    // 首页
    public static final int ALL_TASK_FIRST_PAGE = 1;
    public static final int START_TASK_FIRST_PAGE = 2;
    public static final int SUCCESS_TASK_FIRST_PAGE = 3;
/******************************资源存量管理************************************/
    public static class RESOURCE_STOCK{
    	//资源类别
    	public static final String RESOURCE_NE = "0";
    	public static final String RESOURCE_SHELF = "1";
    	public static final String RESOURCE_UNIT = "2";
    	public static final String RESOURCE_PTP = "3";
    	
    	//显示方式
    	public static final int DEFAULT_MODE = 1;
    	public static final int MANUAL_MODE = 2;
    	public static final int NONE_MODE = 3;//仅用于贝尔控制subunit不显示
    	
    	public static final String DEFAULT_MODE_DISPLAY ="原始名称";
    	public static final String MANUAL_MODE_DISPLAY = "自定义名称";
    	
    	public static Map<Integer, Object> DISPLAY_MODE = new HashMap<Integer, Object>();
		static {
			DISPLAY_MODE.put(RESOURCE_STOCK.DEFAULT_MODE, RESOURCE_STOCK.DEFAULT_MODE_DISPLAY);
			DISPLAY_MODE.put(RESOURCE_STOCK.MANUAL_MODE, RESOURCE_STOCK.MANUAL_MODE_DISPLAY);
		}
		/*********************网元**********************/
		//管理类别
		public static final int MAINTAIN = 1;
    	public static final int ENGINEERING = 2;
    	public static final int NOMAINTAIN = 3;
    	
    	public static final String MAINTAIN_DISPLAY ="维护";
    	public static final String ENGINEERING_DISPLAY = "工程";
    	public static final String NOMAINTAIN_DISPLAY ="退网";
    	
    	public static Map<Integer, Object> MGMT_CATEGORY = new HashMap<Integer, Object>();
		static {
			MGMT_CATEGORY.put(RESOURCE_STOCK.MAINTAIN, RESOURCE_STOCK.MAINTAIN_DISPLAY);
			MGMT_CATEGORY.put(RESOURCE_STOCK.ENGINEERING, RESOURCE_STOCK.ENGINEERING_DISPLAY);
			MGMT_CATEGORY.put(RESOURCE_STOCK.NOMAINTAIN, RESOURCE_STOCK.NOMAINTAIN_DISPLAY);
		}
		//服务状态
		public static final int SERVICE = 0;
    	public static final int NOSERVICE = 1;
    	public static final int MAINTANCE = 2;
    	public static final int UNKNOW = 3;
    	
    	public static final String SERVICE_DISPLAY ="服务中";
    	public static final String NOSERVICE_DISPLAY = "退出服务";
    	public static final String MAINTANCE_DISPLAY ="维护状态";
    	public static final String UNKNOW_DISPLAY ="未知";
    	
    	public static Map<Integer, Object> SERVICE_STATE = new HashMap<Integer, Object>();
		static {
			SERVICE_STATE.put(RESOURCE_STOCK.SERVICE, RESOURCE_STOCK.SERVICE_DISPLAY);
			SERVICE_STATE.put(RESOURCE_STOCK.NOSERVICE, RESOURCE_STOCK.NOSERVICE_DISPLAY);
			SERVICE_STATE.put(RESOURCE_STOCK.MAINTANCE, RESOURCE_STOCK.MAINTANCE_DISPLAY);
			SERVICE_STATE.put(RESOURCE_STOCK.UNKNOW, RESOURCE_STOCK.UNKNOW_DISPLAY);
		}
    }
    /******************************网络隐患预警************************************/
    public static class NETWORK{
		public static final int CIRCLE = 1;
		public static final int LINK = 2;
		public static final String LOCATION_LINK ="链";
    	public static final String LOCATION_SINGLE = "独立";
    	
    	public static final int NWA_SLOT = 1;
    	public static final int NWA_SLOT_ZONGHE = 11;
    	public static final int NWA_SLOT_SUB = 12;
    	public static final int NWA_PORT =2;
    	public static final int NWA_PORT_ZONGHE = 21;
    	public static final int NWA_PORT_SUB = 22;
    	public static final int NWA_CTP = 3;
    	public static final int NWA_CTP_ZONGHE = 31;
    	public static final int NWA_CTP_SUB = 32;
    	
    	public static final int NWA_PORT_ROUTE =4;
    	public static final int NWA_PORT_ZONGHE_ROUTE = 41;
    	public static final int NWA_PORT_SUB_ROUTE = 42;
    	
    }

    /******************************资源************************************/
    public static class RESOURCE{
    	//<<<<<<<<<传输系统>>>>>>>>>>>>>>>//
    	public static class TRANS_SYS{
	    	//保护组类型
			public static final int MSP1 = 0;
	    	public static final int MSPN = 1;
	    	public static final int BLSR2F = 2;
	    	public static final int BLSR4F = 3;
	    	public static final int ATM1 = 4;
	    	public static final int ATMN = 5;
	    	public static final int SNCP = 6;
	    	public static final int NONE =99;
	    	
	    	public static final String MSP1_DISPLAY ="1+1 MSP";
	    	public static final String MSPN_DISPLAY = "1:N MSP";
	    	public static final String BLSR2F_DISPLAY ="2F BLSR";
	    	public static final String BLSR4F_DISPLAY ="4F BLSR";
	    	public static final String ATM1_DISPLAY ="1+1 ATM";
	    	public static final String ATMN_DISPLAY ="1:N ATM";
	    	public static final String SNCP_DISPLAY ="SNCP";
	    	public static final String NONE_DISPLAY ="无";
	    	
	    	public static Map<Integer, String> PRO_GROUP_TYPE = new HashMap<Integer, String>();
			static {
				PRO_GROUP_TYPE.put(MSP1, MSP1_DISPLAY);
				PRO_GROUP_TYPE.put(MSPN, MSPN_DISPLAY);
				PRO_GROUP_TYPE.put(BLSR2F, BLSR2F_DISPLAY);
				PRO_GROUP_TYPE.put(BLSR4F, BLSR4F_DISPLAY);
				PRO_GROUP_TYPE.put(ATM1, ATM1_DISPLAY);
				PRO_GROUP_TYPE.put(ATMN, ATMN_DISPLAY);
				PRO_GROUP_TYPE.put(SNCP, SNCP_DISPLAY);
				PRO_GROUP_TYPE.put(NONE, NONE_DISPLAY);
			}
			
			//级别
//			public static final int BACKBONE = 1;
//	    	public static final int AGGEGATION = 2;
//	    	public static final int ACCESS = 3;
//	    	public static final int TRUNK1 = 4;
//	    	public static final int TRUNK2 = 5;
	    	
	    	public static final String[] NET_LEVEL_DISPLAY=FieldNameDefine.NET_LEVEL_DISPLAY.split(",");
	    	
	    	public static Map<Integer, Object> NET_LEVEL = new HashMap<Integer, Object>();
			static {
				for(int i=0;i<NET_LEVEL_DISPLAY.length;i++){
					NET_LEVEL.put(i+1, NET_LEVEL_DISPLAY[i]);
				}
			}
    	}
    }
    
    public class Gis {
    	
    	public static final int ALARM_STATION_GIS = 1;
    	public static final int ALARM_CABLE_GIS = 2;
    	public static final int TEST_CABLE_GIS = 3;
    	public static final int NO_ALARM = -1;
    }
}
