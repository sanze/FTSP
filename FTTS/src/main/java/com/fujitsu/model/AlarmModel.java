package com.fujitsu.model;


/**
 * @Description: 告警Model
 * @author Meik
 * @date 2015-1-20
 * @version V1.0
 */

public class AlarmModel {
	
	//告警ID
	private int alarmId;
	
	//告警状态：1 已确认 2 未确认
	private int ACK_STATUS;
	
	//测试结果ID
	private int TEST_RESULT_ID;
	
	//告警级别：1 紧急 2 严重 3 一般 4 提示
	private int ALARM_LEVEL;
	
	//告警内容
	private String content;
	
	//告警类型：1 设备告警 2 线路告警 3 网管告警
	private int ALARM_TYPE;
	
	//区域
	private String REGION_NAME;
	
	//局站
	private String stationName;
	
	//设备编号
	private String EQPT_NO;
	
	//设备名称
	private String EQPT_NAME;
	
	//设备类型:RTU,CTU,SFM,OSM
	private int EQPT_TYPE;
	
	//槽道号
	private String SLOT_NO;
	
	//端口号
	private String CARD_PORT;
	
	//机盘型号:OTDR,OPM
	private String CARD_TYPE;
	
	//测试链路信息
	private String testLinkInfo;
	
	//光路信息1
	private String lightPathInfo1;
	
	//光路信息2
	private String lightPathInfo2;
	
	//断点信息
	private String breakPointInfo;
	
	//告警发生时间
	private String ALARM_OCCUR_DATE;
	
	//告警确认时间
	private String ACK_DATE;
	
	//告警结束时间
	private String ALARM_CLEAR_DATE;
	
	//告警确认者
	private String USER_NAME;
	
	public int getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTestLinkInfo() {
		return testLinkInfo;
	}
	public void setTestLinkInfo(String testLinkInfo) {
		this.testLinkInfo = testLinkInfo;
	}
	public String getLightPathInfo1() {
		return lightPathInfo1;
	}
	public void setLightPathInfo1(String lightPathInfo1) {
		this.lightPathInfo1 = lightPathInfo1;
	}
	public String getLightPathInfo2() {
		return lightPathInfo2;
	}
	public void setLightPathInfo2(String lightPathInfo2) {
		this.lightPathInfo2 = lightPathInfo2;
	}
	public String getBreakPointInfo() {
		return breakPointInfo;
	}
	public void setBreakPointInfo(String breakPointInfo) {
		this.breakPointInfo = breakPointInfo;
	}
	public int getACK_STATUS() {
		return ACK_STATUS;
	}
	public void setACK_STATUS(int aCK_STATUS) {
		ACK_STATUS = aCK_STATUS;
	}
	public int getALARM_LEVEL() {
		return ALARM_LEVEL;
	}
	public void setALARM_LEVEL(int aLARM_LEVEL) {
		ALARM_LEVEL = aLARM_LEVEL;
	}
	public int getALARM_TYPE() {
		return ALARM_TYPE;
	}
	public void setALARM_TYPE(int aLARM_TYPE) {
		ALARM_TYPE = aLARM_TYPE;
	}
	public String getREGION_NAME() {
		return REGION_NAME;
	}
	public void setREGION_NAME(String rEGION_NAME) {
		REGION_NAME = rEGION_NAME;
	}
	public String getEQPT_NO() {
		return EQPT_NO;
	}
	public void setEQPT_NO(String eQPT_NO) {
		EQPT_NO = eQPT_NO;
	}
	public String getEQPT_NAME() {
		return EQPT_NAME;
	}
	public void setEQPT_NAME(String eQPT_NAME) {
		EQPT_NAME = eQPT_NAME;
	}
	public int getEQPT_TYPE() {
		return EQPT_TYPE;
	}
	public void setEQPT_TYPE(int eQPT_TYPE) {
		EQPT_TYPE = eQPT_TYPE;
	}
	public String getCARD_TYPE() {
		return CARD_TYPE;
	}
	public void setCARD_TYPE(String cARD_TYPE) {
		CARD_TYPE = cARD_TYPE;
	}
	public String getALARM_OCCUR_DATE() {
		return ALARM_OCCUR_DATE;
	}
	public void setALARM_OCCUR_DATE(String aLARM_OCCUR_DATE) {
		ALARM_OCCUR_DATE = aLARM_OCCUR_DATE;
	}
	public String getACK_DATE() {
		return ACK_DATE;
	}
	public void setACK_DATE(String aCK_DATE) {
		ACK_DATE = aCK_DATE;
	}
	public String getUSER_NAME() {
		return USER_NAME;
	}
	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}
	public int getTEST_RESULT_ID() {
		return TEST_RESULT_ID;
	}
	public void setTEST_RESULT_ID(int tEST_RESULT_ID) {
		TEST_RESULT_ID = tEST_RESULT_ID;
	}
	public String getALARM_CLEAR_DATE() {
		return ALARM_CLEAR_DATE;
	}
	public void setALARM_CLEAR_DATE(String aLARM_CLEAR_DATE) {
		ALARM_CLEAR_DATE = aLARM_CLEAR_DATE;
	}
	public String getSLOT_NO() {
		return SLOT_NO;
	}
	public void setSLOT_NO(String sLOT_NO) {
		SLOT_NO = sLOT_NO;
	}
	public String getCARD_PORT() {
		return CARD_PORT;
	}
	public void setCARD_PORT(String cARD_PORT) {
		CARD_PORT = cARD_PORT;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
}
