package com.fujitsu.model;

public class FaultAlarmModel {
	private int alarmId;
	private String alarmName;
	private int severity;
	private String neName;
	private String startTime;
	private String clearTime;
	private int ptpId;
	private int convergeFlag;
	//EXT
	private String emsName;
	private String stationName;
	private String unitName;
	private int unitId;
	private int factory;
	private int emsId;
	
	public int getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}
	public String getAlarmName() {
		return alarmName;
	}
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getClearTime() {
		return clearTime;
	}
	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}
	public int getPtpId() {
		return ptpId;
	}
	public void setPtpId(int ptpId) {
		this.ptpId = ptpId;
	}
	public int getConvergeFlag() {
		return convergeFlag;
	}
	public void setConvergeFlag(int convergeFlag) {
		this.convergeFlag = convergeFlag;
	}
	public String getEmsName() {
		return emsName;
	}
	public void setEmsName(String emsName) {
		this.emsName = emsName;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public int getFactory() {
		return factory;
	}
	public void setFactory(int factory) {
		this.factory = factory;
	}
	public int getEmsId() {
		return emsId;
	}
	public void setEmsId(int emsId) {
		this.emsId = emsId;
	}
}
