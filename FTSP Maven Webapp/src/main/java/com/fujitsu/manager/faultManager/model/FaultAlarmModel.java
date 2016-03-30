package com.fujitsu.manager.faultManager.model;

public class FaultAlarmModel {
	private int faultId;
	private int alarmId;
	private int errorCode;
	public FaultAlarmModel(){}
	public FaultAlarmModel(int faultId, int alarmId){
		this.faultId = faultId;
		this.alarmId = alarmId;
	}
	public int getFaultId() {
		return faultId;
	}
	public void setFaultId(int faultId) {
		this.faultId = faultId;
	}
	public int getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
