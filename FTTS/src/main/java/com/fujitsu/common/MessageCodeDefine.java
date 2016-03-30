package com.fujitsu.common;

/**
 * @author xuxiaojun
 * 
 */
public class MessageCodeDefine extends BaseMessageCodeDefine{
	/** ***********************接入模块定义开始******************************** */
	// 命令执行成功
	public final static int CMD_EXECUTE_SUCCESS = 15000;
	
	// 命令执行失败  返回设备消息
	public final static int CMD_EXECUTE_FAIL = 15001;
	// 命令参数不合法
	public final static int CMD_PARA_ILLEG = 15002;
	// 连接设备异常
	public final static int CMD_CONNECT_ERROR = 15003;
	// 设备返回空
	public final static int CMD_RETURN_EMPTY_ERROR = 15004;
	// 设备返回错误值
	public final static int CMD_RETURN_UNKNOW_ERROR = 15005;
	
	//厂家不支持
	public final static int CMD_RETURN_UNKNOW_FACTORY = 15099;
	
	// 推送告警到ＪＭＳ　Ｍｅｓｓａｇｅ编号
	public final static int ALARM_JMS_CODE = 15120;
	
	/** ***********************接入模块定义结束********************************* */
	/** ***********************计划模块定义开始******************************** */
	//成功
	public final static int PLAN_EXECUTE_SUCCESS = 16000;
	//获取数据库信息出错
	public final static int PLAN_DB_INFO_ERROR = 16001;
	
	/** ***********************计划模块定义结束********************************* */
	
}
