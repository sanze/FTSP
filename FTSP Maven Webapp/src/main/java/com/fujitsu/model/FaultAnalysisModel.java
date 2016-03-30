package com.fujitsu.model;

public class FaultAnalysisModel {

	private int alarmId;
	private String alarmName;
	private int severity;
	private String neName;
	private String startTime;
	private String clearTime;
	private int ptpId;
	
	// Ext
	private int id;
	private int source;
	private int type;
	private int reason1;
	private int reason2;
	private String sysName;
	private String emsName;
	private String stationName;
	private String unitDesc;
	private int unitId;
	private String aStation;
	private String zStation;
	private String memo;
	private int status;
	private boolean analysisStatus;
	private int emsId;
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getSource() {
		return this.source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	
	public int getType() {
		return this.type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public int getReason1() {
		return this.reason1;
	}
	public void setReason1(int reason) {
		this.reason1 = reason;
	}
	
	public int getReason2() {
		return this.reason2;
	}
	public void setReason2(int reason) {
		this.reason2 = reason;
	}
	
	public int getAlarmId() {
		return this.alarmId;
	}
	public void setAlarmId(int id) {
		this.alarmId = id;
	}
	
	public String getAlarmName() {
		return this.alarmName;
	}
	public void setAlarmName(String name) {
		this.alarmName = name;
	}
	
	public int getSeverity() {
		return this.severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	
	public String getNeName() {
		return this.neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	
	public String getStartTime() {
		return this.startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getClearTime() {
		return this.clearTime;
	}
	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}
	
	public int getPtpId() {
		return this.ptpId;
	}
	public void setPtpId(int ptpId) {
		this.ptpId = ptpId;
	}
	
	public String getSysName() {
		return this.sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	
	public String getEmsName() {
		return this.emsName;
	}
	public void setEmsName(String emsName) {
		this.emsName = emsName;
	}
	
	public String getStationName() {
		return this.stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	public String getUnitDesc() {
		return this.unitDesc;
	}
	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}
	
	public int getUnitId() {
		return this.unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	
	public String getAStation() {
		return this.aStation;
	}
	public void setAStation(String station) {
		this.aStation = station;
	}
	
	public String getZStation() {
		return this.zStation;
	}
	public void setZStation(String station) {
		this.zStation = station;
	}
	
	public String getMomo() {
		return this.memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public int getStatus() {
		return this.status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean getAnalysisStatus() {
		return this.analysisStatus;
	}
	public void setAnalysisStatus(boolean st) {
		this.analysisStatus = st;
	}
	
	public int getEmsId() {
		return this.emsId;
	}
	public void setEmsid(int emsId) {
		this.emsId = emsId;
	}
}
