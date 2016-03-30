package com.fujitsu.common;

/**
 * @author xuxiaojun
 * 
 */
public class MessageCodeDefine {
	/***************************共通部分***********************************/
	/**数据库连接异常*/
	public static final int COM_EXCPT_DB_CONNECT = 100;
	/**数据库操作异常*/
	public static final int COM_EXCPT_DB_OP = 101;
	/**JMS异常*/
	public static final int COM_EXCPT_JMS = 200;
	/***************************共通部分***********************************/
	
	
	//数据采集模块异常代号定义-----------------------------------------------------
	//通知服务断开异常！
	public final static int CORBA_DISCONNECT_NOTIFY_FAILED_EXCEPTION = 99001;
	//CORBA连接断开异常！
	public final static int CORBA_DISCONNECT_CONNECTION_FAILED_EXCEPTION = 99002;
	//orb init失败！
	public final static int CORBA_ORBINIT_FAILED_EXCEPTION = 99003;
	//nameService取得失败！
	public final static int CORBA_NAME_SERVICE_FAILED_EXCEPTION = 99004;
	//ems session factory取得失败！
	public final static int CORBA_EMS_SESSION_FACTORY_FAILED_EXCEPTION = 99005;
	//root poa取得失败！
	public final static int CORBA_ROOT_POA_FAILED_EXCEPTION = 99006;
	//通知服务启动失败！
	public final static int CORBA_NOTIFICATION_START_FAILED_EXCEPTION = 99007;
	//通知服务已经存在！
	public final static int CORBA_NOTIFICATION_ALERDY_CONNECT_EXCEPTION = 99008;
	//通知服务连接超限！
	public final static int CORBA_NOTIFICATION_CONNECT_LIMITE_EXCEPTION = 99009;
	//UnknownHost！
	public final static int CORBA_UNKNOW_HOST_EXCEPTION = 99010;
	//IOException！
	public final static int CORBA_IO_EXCEPTION = 99011;
	//ParseException！
	public final static int CORBA_PARSE_EXCEPTION = 99012;
	//UnsupportedEncodingException！
	public final static int CORBA_UNSUPPORTED_ENCODING_EXCEPTION = 99013;
	//InterruptedException！
	public final static int CORBA_INTERRUPTED_EXCEPTION = 99014;
	//性能文件解析出错: 下载性能文件失败！
	public final static int CORBA_FILE_DOWNLOAD_EXCEPTION = 99015;
	//FileNotFoundException！
	public final static int CORBA_FILE_NOT_FOUND_EXCEPTION = 99016;
	//SocketException！
	public final static int CORBA_SOCKET_EXCEPTION = 99017;
	//网管获取数据异常！
	public final static int CORBA_EXCUTION_EXCEPTION = 99018;
	//TimeoutException！
	public final static int CORBA_TIMEOUT_EXCEPTION = 99019;
	
	//网元类型未知异常
	public final static int CORBA_UNKNOW_NE_TYPE_EXCEPTION = 99020;
	//网管禁止采集
	public final static int CORBA_COLLECT_FOBIDDEN_EXCEPTION = 99021;
	//网管暂停采集
	public final static int CORBA_COLLECT_PAUSE_EXCEPTION = 99022;
	//网元不在线
	public final static int CORBA_COLLECT_NE_UNAVAILABLE_EXCEPTION = 99023;
	//启动数据采集服务失败
	public final static int CORBA_START_UP_FAILED = 99024;
	//FTP主机ip异常
	public final static int UNKNOW_FTP_HOST_EXCEPTION = 99025;

	//corba采集异常代号，异常信息使用corba抛出异常中的错误原因
	public final static int CORBA_RUNTIME_EXCEPTION = 99099;
	//corba不支持此命令
	public final static int CORBA_UNSUPPORTED_COMMAND_EXCEPTION = 99100;	

	// 错误code定义
	public final static int MESSAGE_CODE_999999 = 999999;

}
