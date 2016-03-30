package com.fujitsu.common;

public class BaseDefine {
	
	public final static int SUCCESS = 1;
	public final static int FAILED = 0;
	
	// 标识 true or false
	public final static int TRUE = 1;
	public final static int FALSE = 0;
	public final static int NEGATIVE = -1;
	// 网元表中标记删除
	public final static int DELETE_FLAG = 2;

	public static final String MESSAGE_CONFIG_FILE = "messageResource";
	public static final String SYSTEM_CONFIG_FILE = "systemConfig";
	
	// 数据库参数
	public static final String DB_HOST = "host";
	public static final String DB_SID = "sid";
	public static final String DB_PORT = "port";
	public static final String DB_USERNAME = "username";
	public static final String DB_PASSWORD = "password";
	
	// ftp配置参数
	public static final String FTP_IP = "ftpIp";
	public static final String FTP_USER_NAME = "ftpUserName";
	public static final String FTP_PASSWORD = "ftpPassword";
	public static final String FTP_PORT = "ftpPort";
	
	// 编码格式
	public static final String ENCODE_ISO = "ISO-8859-1";
	public static final String ENCODE_UTF_8 = "UTF-8";
	public static final String ENCODE_GBK = "GBK";
	
	/** mongoDB数据库常量定义 */
	// mongoDB数据库库名
	public final static String MONGODB_NAME = "test";
	// 生成自增主键的表名(sequence)
	public final static String SEQUENCE = "SEQUENCE";
	// 日志表名
	public final static String T_JOURNAL = "T_JOURNAL";
	
	public final static int _ID = 1;
	
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
	
	public static final String GROUP_FORMAT = "yyyy-MM";
}
