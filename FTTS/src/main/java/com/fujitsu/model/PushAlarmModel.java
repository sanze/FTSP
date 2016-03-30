package com.fujitsu.model;

import java.util.Date;

public class PushAlarmModel {
	
	//设备ID
	private int eqptId;
	
	//告警值
	private int alarmValue;
	
	//发送接收标记
	private int transRecvFlag;
	
	//告警名称
	private String alarmName;
	
	//告警类型
	private int alarmType;

	//告警级别
	private int alarmLevel;
	
	//设备类型
	private int eqptType;
	
	//槽道号
	private int slotNo;
	
	//端口号
	private int portNo;
	
	//机盘型号
	private int cardType;
	
	//告警发生时间
	private Date alarmOccurTime;
	
	//区域ID
	private int regionId;
	
	//产生消除标记 0-消除 1-产生
	int alarmFlag;

	public int getEqptId() {
		return eqptId;
	}

	public void setEqptId(int eqptId) {
		this.eqptId = eqptId;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public int getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public int getEqptType() {
		return eqptType;
	}

	public void setEqptType(int eqptType) {
		this.eqptType = eqptType;
	}

	public int getSlotNo() {
		return slotNo;
	}

	public void setSlotNo(int slotNo) {
		this.slotNo = slotNo;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public Date getAlarmOccurTime() {
		return alarmOccurTime;
	}

	public void setAlarmOccurTime(Date alarmOccurTime) {
		this.alarmOccurTime = alarmOccurTime;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public int getAlarmFlag() {
		return alarmFlag;
	}

	public void setAlarmFlag(int alarmFlag) {
		this.alarmFlag = alarmFlag;
	}

	public int getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(int alarmValue) {
		this.alarmValue = alarmValue;
	}

	public int getTransRecvFlag() {
		return transRecvFlag;
	}

	public void setTransRecvFlag(int transRecvFlag) {
		this.transRecvFlag = transRecvFlag;
	}
}
