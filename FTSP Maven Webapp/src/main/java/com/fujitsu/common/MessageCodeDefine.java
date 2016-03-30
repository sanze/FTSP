package com.fujitsu.common;

/**
 * @author xuxiaojun
 * 
 */
public class MessageCodeDefine extends BaseMessageCodeDefine{

	/** *************************数据采集模块********************************** */
	// 新增 CorbaConnection
	public final static int CORBA_CONNECTION_ADD_SUCCESS = 12001;
	// 新增 TelnetConnection
	public final static int TELNET_CONNECTION_ADD_SUCCESS = 12002; 
	//  getConnectionListByGroupId
	public final static int GET_CONNECTION_BY_GROUPID_ERROR = 12003;
	// 新增 TelnetConnection
	public final static int GET_CONNECTION_ERROR = 12004;
	//  getConnectGroup
	public final static int GET_CONNECTION_GROUPID_ERROR = 12005;
	// 获取  GATEWAY_NE_ID
	public final static int GATEWAY_NE_ERROR = 12006;
	// 新增  addCorbaConnection
	public final static int CORBA_CONNECTION_ADD_ERROR = 12007;
	// 新增 addTelnetConnection 
	public final static int TELNET_CONNECTION_ADD_ERROR = 12008;
	// 获取 getNeInfoByNativeName 
	public final static int GET_NEINFO_ERROR = 12009;
	// modifyConnection
	public final static int MODIFY_CONNECTION_SUCCESS = 12010;
	// deleteConnection
	public final static int DELETE_CONNECTION_SUCCESS = 12011;
	// 新增网管分组addEmsGroup
	public final static int ADD_EMS_GROUP_SUCCESS = 12012;
	// 删除网管分组EmsGroup
	public final static int DELETE_EMS_GROUP_SUCCESS = 12013;
	// 修改网管分组EmsGroup
	public final static int MODIFY_EMS_GROUP_SUCCESS = 12014;
	// 新增子网
	public final static int ADD_SUBNET_SUCCESS = 12015;
	//修改子网
	public final static int MODIFY_SUBNET_SUCCESS = 12016;
	//删除子网
	public final static int DELETE_SUBNET_SUCCESS = 12017;
	// 新增TELNET 网元
	public final static int ADD_TELNET_NE_SUCCESS = 12018;
	//修改TELNET 网元
	public final static int MODIFY_TELNET_NE_SUCCESS = 12019;
	//删除TELNET 网元
	public final static int DELETE_TELNET_NE_SUCCESS = 12020;
	
	//登录 网元
	public final static int LOG_ON_NE_SUCCESS = 12042;
	//退出 网元
	public final static int LOG_OUT_NE_SUCCESS = 12043;
	
	//保存已分组网元 saveClassifiedNe
	public final static int SAVE_CLASSIFIED_NE = 12021;
	// 新增接入管理器成功
	public final static int ADD_SYS_SERVICE_SUCCESS = 12022;
	// 修改接入管理器成功
	public final static int MODIFY_SYS_SERVICE_SUCCESS = 12023;
	// 删除接入管理器成功
	public final static int DELETE_SYS_SERVICE_SUCCESS = 12024;
	// 接入管理器重命名
	public final static int RENAME_SYS_SERVICE = 12025;
	// 链路同步没变化  LinkSync
	public final static int LINK_SYNC_NO_CHANGE = 12026;
	//修改corba 网元
	public final static int MODIFY_CORBA_NE_SUCCESS = 12027;
	// 修改网管同步成功
	public final static int MODIFY_EMS_SYNC_SUCCESS = 12028;
	// 网管分组重命名
	public final static int RENAME_EMS_GROUP = 12029;
	// 网管名称重命名
	public final static int REPEAT_EMS_CONNECTION_IP_NAME = 12030;
	// 子网名称重命名
	public final static int RENAME_SUBNET = 12031;
	// INTERRUPTED_EXCPT 进程休眠过程异常
	public final static int INTERRUPTED_EXCPT = 12032; 
	// INTERRUPTED_EXCPT
	public final static int PARSE_EXCPT = 12033; 
	// 网管Ip地址重复
	public final static int REPEAT_EMS_CONNECTION_IP = 12034;
	// 网管名称重命名
	public final static int RENAME_EMS_CONNECTION = 12035;
	// 网元更新成功
	public final static int UPDATE_NE_SUCCESS = 12036;
	// 网元同步完成
	public final static int SYNC_NE_SUCCESS = 12037;
	// 拓扑链路同步操作完成
	public final static int TOPO_LINK_SYNC_SUCCESS = 12038;
	// 网管手动同步操作完成manualSyncEms
	public final static int EMS_MANUAL_SYNC_SUCCESS = 12039;
	// 网管同步任务  继续 proceedTaskSetting
	public final static int EMS_SYNC_TASK_PROCEED_SUCCESS = 12040;
	// 网管同步任务  停止stopTaskSetting
	public final static int EMS_SYNC_TASK_STOP_SUCCESS = 12041;	
	// 网元名称重命名
	public final static int RENAME_NE_NAME = 12044;
	// 接入管理器重命名
	public final static int RENAME_SYS_IP_ADDRESS = 12045;
	/** *********************************************************** */
	/** ****************************资源存量管理******************************* */
	public final static int CHANGE_DISPLAYMODE_SUCCESS = 12046;
	public final static int CHANGE_DISPLAYMODE_FAILED = 12047;
	
	
	/** ******************************************************************* */

	/** *************************性能管理模块********************************** */
	public final static int PM_DB_ERROR = 6000;
	public final static int PM_PARAMETER_TYPE_ERROR = 6001;
	public final static int PM_PARAMETER_NULL_ERROR = 6002;
	public final static int PM_MODIFY_TASK = 6003;
	public final static int PM_SEARCH_AND_SET_TEMPLATE = 6004;
	public final static int PM_APPLY_TEMPLATE = 6005;
	public final static int PM_CANCEL_TEMPLATE = 6006;
	public final static int PM_MODIFY_TASK_STATUS = 6007;
	public final static int PM_MODIFY_TASK_STATUS_ERROR = 6008;
	public final static int PM_PAUSE_ERROR = 6009;
	public final static int PM_RESUME_ERROR = 6010;
	public final static int PM_PAUSE_SUCCESS = 6011;
	public final static int PM_RESUME_SUCCESS = 6012;
	public final static int PM_SAVE_NUMBERIC = 6013;
	public final static int PM_SAVE_PHYSICAL = 6014;
	public final static int PM_DELETE_TEMPLATE = 6015;
	public final static int PM_TEMPLATE_APPLIED = 6016;
	public final static int PM_TEMPLATE_APPLIED_FAILED = 6017;
	public final static int PM_TIME_FORMART_ERROR = 6018;
	public final static int PM_NO_Z_END_PTP = 6019;
	public final static int PM_TEMPLATE_FACTORY_UNFIT = 6020;
	public final static int PM_OPTICAL_STD_SAVE_SUCCESS = 6021;
	public final static int PM_OPTICAL_STD_MODIFY_SUCCESS = 6022;
	public final static int PM_OPTICAL_STD_MODIFY_FAILED = 6023;
	public final static int PM_OPTICAL_STD_NEW_SUCCESS = 6024;
	public final static int PM_OPTICAL_STD_NEW_FAILED = 6025;
	public final static int PM_OPTICAL_STD_DEL_SUCCESS = 6026;
	public final static int PM_OPTICAL_STD_DEL_FAILED = 6027;
	public final static int PM_OPTICAL_STD_DEL_DULP = 6028;
	public final static int PM_OPTICAL_STD_DEL_DEFAULT = 6029;
	public final static int PM_TEMPLATE_NAME_EXIST = 6030;
	public final static int PM_SET_COMPARE_VALUE_FROM_PM_SUCCESS = 6031;
	public final static int PM_NE_REPORT_NE_SAVE_SUCCESS = 6032;
	public final static int PM_NE_REPORT_NE_OUT_OF_LIMIT = 6033;
	public final static int PM_TABLE_NOT_EXIST = 6034;
	public final static int PM_REPORT_TASK_NAME_DUPLICATE = 6035;
	public final static int PM_REPORT_FILE_READ_FAILED = 6036;
	public final static int PM_REPORT_ADD_QUARTZ_TASK_FAILED = 6037;
	public final static int PM_COLLECT_MODULE_ERROR = 6038;
	public final static int PM_WDMMS_CUTOVER_TASK_SAVE_SUCCESS = 6039;
	public final static int PM_WDMMS_CUTOVER_TASK_SAVE_FAILED = 6040;
	public final static int PM_WDMMS_CUTOVER_TASK_UPDATE_SUCCESS = 6041;
	public final static int PM_WDMMS_CUTOVER_TASK_UPDATE_FAILED = 6042;
	public final static int PM_WDMMS_CUTOVER_TASK_DELETE_SUCCESS = 6043;
	public final static int PM_WDMMS_CUTOVER_TASK_DELETE_FAILED = 6044;
	public final static int PM_MODIFY_TASK_TIME = 6045;
	public final static int PM_DONT_SELECT_SDH = 6046;
	public final static int PM_DONT_SELECT_WDM = 6047;
	public final static int PM_NE_REPORT_SUCCESS = 6099;
	public final static int PM_AUTO_APPLY_OPT_STD_CANCELED = 6100;
	/** *************************性能管理模块********************************** */

	/** ***********************电路模块定义开始******************************** */
	// 修改成功
	public final static int CIRCUIT_UPDATE_SUCCESS = 7001;
	
	//更新失败
	public final static int CIRCUIT_UPDATE_FAILED=7009;

	// 插入成功
	public final static int CIRCUIT_INSERT_SUCCESS = 7002;

	// 删除成功
	public final static int CIRCUIT_DELETE_SUCCESS = 7007;
	
	// 同步成功
	public final static int CIRCUIT_SYC_SUCCESS = 7019;
	// 同步失败
	public final static int CIRCUIT_SYC_FAILED = 7018;
	
	// 删除失败
	public final static int CIRCUIT_DELETE_FAILED = 7008;

	// 生成成功
	public final static int CIRCUIT_CREATE_SUCCESS = 7003;

	// 控制选择数量不超过五个
	public final static int CIRCUIT__NUMBER_LIMIT = 7004;

	// 控制选择数量不超过500个
	public final static int CIRCUIT__NUMBER500_LIMIT = 7010;

	// 控制同一级别选择
	public final static int CIRCUIT__LEVEL_LIMIT = 7005;

	// 选择不能为空
	public final static int CIRCUIT__NULL_LIMIT = 7006;
	
	// 特定字符串转换异常
	public final static int CIR_EXCPT_PARSE = 7101;
	
	//文件找不到异常
	public final static int CIR_EXCPT_NOTFOUND = 7102;
		
	// 文件读写异常
	public final static int CIR_EXCPT_IO = 7103;
	
	// 读取excel异常
	public final static int CIR_EXCPT_BIFF = 7104;
	
	// 读取excel异常
	public final static int CIR_EXCPT_ERROR = 7120;

	// 不支持编码异常
	public final static int CIR_EXCPT_UNENCODING = 7105;
	
	// 查询成功
	public final static int SELECT_SUCCESS = 7121;
	// 查询失败
	public final static int SELECT_FAILED = 7122;
	// 新增异常
	public final static int CIR_EXCPT_ADD = 7106;
	// 更新异常
	public final static int CIR_EXCPT_UPDATE = 7107;
	// 查询异常
	public final static int CIR_EXCPT_SELECT = 7108;
	// 删除异常
	public final static int CIR_EXCPT_DELETE = 7109;
	// 生成异常
	public final static int CIR_EXCPT_NEW = 7110;
	
	// 操作异常
	public final static int CIR_EXCPT_DO = 7111;
	
	// 导入异常
	public final static int CIR_EXCPT_IMPORT = 7112;
	
	// 干线名称已被使用
	public final static int PM_EXIST = 7113;
	
	// 光复用段名称已被使用
	public final static int PM_SECTION_EXIST = 7115;
	
	// 光放型号已被使用
	public final static int PM_STANDOPTVAL_EXIST = 7116;
	
	// 光放型号被光复段引用，请先修改光复用段
	public final static int PM_STANDOPTVAL_IN_SECTION = 7117;
	
	//选择的干线下包含复用段信息，请先删除复用段！
	public final static int PM_EXIST_SECTION = 7114;
	//新增链路成功
	public final static int LINK_ADD_SUCCESS=7200;
	
	/** ***********************电路模块定义结束********************************* */
	/** ***********************资源模块定义开始******************************** */
	// 修改成功
	public final static int RES_COMPARE_SUCCESS = 9001;
	
	// z资源系统网元标识
	public final static int RES_EXIST = 9002;
	// 网管网元标识
	public final static int FTSP_EXIST = 9003;
	// 文件不存在
	public final static int FILE_NOT_FOUND_EXCEPTION = 9004;
	// 文件读写异常
	public final static int IO_EXCEPTION = 9005;
	
	/** ***********************资源模块定义结束********************************* */

	/** ***********************巡检模块 start ******************************** */
	// 获取包机人信息列表失败
	public final static int INSPECT_ENGINEER_LIST_GET_FAILED = 11001;
	// 获取包机人所属区域信息失败
	public final static int AREA_LIST_GET_FAILED = 11002;
	// 新增包机人成功
	public final static int INSPECT_ENGINEER_ADD_SUCCESS = 11003;
	// 新增包机人失败
	public final static int INSPECT_ENGINEER_ADD_FAILED = 11004;
	// 更新包机人信息成功
	public final static int INSPECT_ENGINEER_UPDATE_SUCCESS = 11005;
	// 更新包机人信息失败
	public final static int INSPECT_ENGINEER_UPDATE_FAILED = 11006;
	// 获取巡检设备列表失败
	public final static int INSPECT_EQUIP_GET_FAILED = 11007;
	// 获取包机人基本信息失败
	public final static int INSPECT_ENGINEER_INFO_GET_FAILED = 11008;
	// 删除包机人成功
	public final static int INSPECT_ENGINEER_DELETE_SUCCESS = 11009;
	// 删除包机人失败
	public final static int INSPECT_ENGINEER_DELETE_FAILED = 11010;
	
	// 获取时间限制列表失败
	public final static int IDATE_LIMIT_LIST_GET_FAILED = 11029;
	// 获取巡检报告列表失败
	public final static int INSPECT_REPORT_LIST_GET_FAILED = 11011;
	// 删除巡检报告成功
	public final static int INSPECT_REPORT_DELETE_SUCCESS = 11012;
	// 删除巡检报告失败
	public final static int INSPECT_REPORT_DELETE_FAILED = 11013;
	
	// 获取巡检任务列表失败
	public final static int INSPECT_TASK_LIST_GET_FAILED = 11014;
	// 获取操作权限组列表失败
	public final static int INSPECT_TASK_PRIVILEGE_GET_FAILED = 11015;
	// 获取当前登录用户所在组ID失败
	public final static int CURRENT_USER_GROUP_GET_FAILED = 11030;
	// 新增巡检任务成功
	public final static int INSPECT_TASK_ADD_SUCCESS = 11016;
	// 新增巡检任务失败
	public final static int INSPECT_TASK_ADD_FAILED = 11017;
	// 时间格式转换错误
	public final static int TIME_PARSE_ERROR = 11018;
	// 获取巡检任务基本信息失败
	public final static int INSPECT_TASK_INFO_GET_FAILED = 11019;
	// 更新巡检任务成功
	public final static int INSPECT_TASK_UPDATE_SUCCESS = 11020;
	// 更新巡检任务失败
	public final static int INSPECT_TASK_UPDATE_FAILED = 11021;
	// 删除巡检任务成功
	public final static int INSPECT_TASK_DELETE_SUCCESS = 11022;
	// 删除巡检任务失败
	public final static int INSPECT_TASK_DELETE_FAILED = 11023;
	// 获取任务执行情况列表失败
	public final static int INSPECT_TASK_DETIAL_GET_FAILED = 11024;
	// 巡检任务启用成功
	public final static int INSPECT_TASK_START_SUCCESS = 11025;
	// 巡检任务启用失败
	public final static int INSPECT_TASK_START_FAILED = 11026;
	// 巡检任务挂起成功
	public final static int INSPECT_TASK_HANGUP_SUCCESS = 11027;
	// 巡检任务挂起失败
	public final static int INSPECT_TASK_HANGUP_FAILED = 11028;
	// 巡检任务立即执行成功
	public final static int INSPECT_TASK_START_IMMEDIATELY_SUCCESS = 11031;
	// 巡检任务立即执行失败
	public final static int INSPECT_TASK_START_IMMEDIATELY_FAILED = 11032;
	
	
	/** ***********************电路模块 end ********************************* */
	
	/** ***********************割接模块 开始 ********************************* */
	public final static int CUTOVER_DB_ERROR = 10000;
	//割接任务添加成功
	public final static int CUTOVER_TASK_ADD_SUCCESS = 10001;
	//割接任务添加失败
	public final static int CUTOVER_TASK_ADD_FAILED = 10002;
	//时间格式解析失败
	public final static int CUTOVER_TASK_TIME_PARSE_FAILURE = 10003;
	//任务名称重复
	public final static int CUTOVER_TASK_NAME_DUPLICATE = 10004;
	//割接任务修改成功
	public final static int CUTOVER_TASK_MODIFY_SUCCESS = 10005;
	//割接任务修改失败
	public final static int CUTOVER_TASK_MODIFY_FAILED = 10006;
	/** ***********************割接模块 结束 ********************************* */
	
	/** ***********************quartz调度任务 开始 ********************************* */
	//调度任务内部错误！
	public final static int QUARTZ_SCHEDULER_EXCEPTION = 13001;
	//cron表达式解析失败！
	public final static int QUARTZ_PARSE_EXCEPTION = 13002;
	/** ***********************quartz调度任务 结束 ********************************* */
	
	/***************************系统模块 开始***********************************/
	// 用户登陆出错
	public final static int LOGIN_ERROR = 3000;
	// 登陆用户信息失效，请重新登陆
	public final static int USER_LOGIN_AGAIN = 3001;
	
	// license错误--已超过license最大连接数
	public final static int LICENSE_OUT_OF_CONN_NUM = 3097;
	// license错误--license文件不存在
	public final static int LICENSE_NOT_EXIST = 3098;
	//  license错误--license过期
	public final static int LICENSE_OUT_OF_TIME = 3099;
	
	//webService连续三次推送失败
	public final static int WEBSERVICE_ERROR =14001; 
	/***************************系统模块 结束***********************************/

	
}
