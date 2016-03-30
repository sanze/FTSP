package com.fujitsu.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FaultModel {

	private int id;
	private String faultNo;
	private int serialNo;
	private int source;
	private int type;
	private int reason1;
	private int reason2;
	private String sysName;
	private String emsName;
	private String stationName;
	private String neName;
	private String unitDesc;
	private int unitId;
	private int factory;
	private String cableName;
	private String fiberName;
	private int cableId;
	private String aStation;
	private String zStation;
	private String memo;
	private int status;
	private int emsId;
	private String startTime;
	
	private List<FaultAlarmModel> alarmList;
	private Date createTime;
	
	public FaultModel() {
		alarmList = new ArrayList<FaultAlarmModel>();
	}
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFaultNo() {
		return faultNo;
	}
	public void setFaultNo(String faultNo) {
		this.faultNo = faultNo;
	}
	public int getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
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
	public String getNeName() {
		return this.neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
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
	
	public int getFactory() {
		return factory;
	}
	public void setFactory(int factory) {
		this.factory = factory;
	}
	public String getCableName() {
		return cableName;
	}
	public void setCableName(String cableName) {
		this.cableName = cableName;
	}
	public String getFiberName() {
		return fiberName;
	}
	public void setFiberName(String fiberName) {
		this.fiberName = fiberName;
	}
	public int getCableId() {
		return cableId;
	}
	public void setCableId(int cableId) {
		this.cableId = cableId;
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

	public int getEmsId() {
		return this.emsId;
	}
	public void setEmsid(int emsId) {
		this.emsId = emsId;
	}
	public List<FaultAlarmModel> getAlarmList() {
		return alarmList;
	}
	public void setAlarmList(List<FaultAlarmModel> alarmList) {
		this.alarmList = alarmList;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
