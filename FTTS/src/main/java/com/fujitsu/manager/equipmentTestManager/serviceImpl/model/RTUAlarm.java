package com.fujitsu.manager.equipmentTestManager.serviceImpl.model;

/**
 * @Description：RTU告警
 * @author cao senrong
 * @date 2014-5-26
 * @version V1.0
 */
public class RTUAlarm {

//	private String alarmType;//告警类型  2字符 0：设备告警 1：线路告警
	private String alarmMachineType;//机盘型号  10字符
	private String alarmSlot;//槽道号  2字符
	private String alarmPort;//端口号 2字符  0：整板
	private String alarmLevel;//告警等级  2字符  0＝消失，1＝产生
	private String alarmContent;//告警内容  2字符
	private String alarmTime;//告警时间  14字符
	
	private String Rcode;//RTU编号
	private String Amode;//告警模式 1＝当前告警，2＝历史告警
	
	public String getAlarmMachineType() {
		return alarmMachineType;
	}
	public void setAlarmMachineType(String alarmMachineType) {
		this.alarmMachineType = alarmMachineType;
	}
	public String getAlarmSlot() {
		return alarmSlot;
	}
	public void setAlarmSlot(String alarmSlot) {
		this.alarmSlot = alarmSlot;
	}
	public String getAlarmPort() {
		return alarmPort;
	}
	public void setAlarmPort(String alarmPort) {
		this.alarmPort = alarmPort;
	}
	public String getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public String getAlarmContent() {
		return alarmContent;
	}
	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getRcode() {
		return Rcode;
	}
	public void setRcode(String rcode) {
		Rcode = rcode;
	}
	public String getAmode() {
		return Amode;
	}
	public void setAmode(String amode) {
		Amode = amode;
	}

}
