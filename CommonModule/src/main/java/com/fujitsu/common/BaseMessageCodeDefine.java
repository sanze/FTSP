package com.fujitsu.common;

/**
 * @author xuxiaojun
 * 
 */
public class BaseMessageCodeDefine {
	/***************************共通部分***********************************/
	
	public final static int MESSAGE_CODE_999999 = 999999;
	/**未知*/
	public static final int COM_EXCPT_UNKNOW = 0;
	/**内部错误*/
	public static final int COM_EXCPT_INTERNAL_ERROR = 1;
	/**逻辑处理错误*/
	public static final int COM_EXCPT_PROCESSING_ERROR = 2;
	/**输入参数错误*/
	public static final int COM_EXCPT_INVALID_INPUT = 3;
	/**对象未找到*/
	public static final int COM_EXCPT_ENTITY_NOT_FOUND = 4;
	/**对象不在有效状态*/
	public static final int COM_EXCPT_NOT_IN_VALID_STATE = 5;
	/**无法完成*/
	public static final int COM_EXCPT_UNABLE_TO_COMPLY = 6;
	/**无权限*/
	public static final int COM_EXCPT_ACCESS_DENIED = 7;
	/**数据采集服务获取失败*/
	public static final int DATA_COLLECT_SERVICE_FAILED = 8;
	/**数据采集服务未配置*/
	public static final int DATA_COLLECT_SERVICE_UNCONFIG= 9;
	/**告警收敛服务获取失败*/
	public static final int ALARM_CONVERGE_SERVICE_FAILED = 10;
	/**故障诊断服务获取失败*/
	public static final int FAULT_DIAGNOSE_SERVICE_FAILED = 11;
	/**数据库连接异常*/
	public static final int COM_EXCPT_DB_CONNECT = 100;
	/**数据库操作异常*/
	public static final int COM_EXCPT_DB_OP = 101;
	/**JMS异常*/
	public static final int COM_EXCPT_JMS = 200;
	
	// 登陆用户信息失效，请重新登陆
	public final static int USER_LOGIN_AGAIN = 3001;
	
	//IOException！
	public final static int CORBA_IO_EXCEPTION = 99011;
	//SocketException！
	public final static int CORBA_SOCKET_EXCEPTION = 99017;

}
