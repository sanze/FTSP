package com.fujitsu.model;

public class CurrentAlarmModel {
	
	private int emsId;
	private int subnetId;
	private int neId;
	private int neType;
	private String rackNo;
	private String shelfNo;
	private String slotNo;
	private String portNo;
	private String domain;
	private String perceivedSeverity;
	// 告警源类型
	private int objectType;
	private int ackState;
	
	public int getEmsId() {
		return emsId;
	}
	public void setEmsId(int emsId) {
		this.emsId = emsId;
	}
	public int getSubnetId() {
		return subnetId;
	}
	public void setSubnetId(int subnetId) {
		this.subnetId = subnetId;
	}
	public int getNeId() {
		return neId;
	}
	public void setNeId(int neId) {
		this.neId = neId;
	}
	public int getNeType() {
		return neType;
	}
	public void setNeType(int neType) {
		this.neType = neType;
	}
	public String getRackNo() {
		return rackNo;
	}
	public void setRackNo(String rackNo) {
		this.rackNo = rackNo;
	}
	public String getShelfNo() {
		return shelfNo;
	}
	public void setShelfNo(String shelfNo) {
		this.shelfNo = shelfNo;
	}
	public String getSlotNo() {
		return slotNo;
	}
	public void setSlotNo(String slotNo) {
		this.slotNo = slotNo;
	}
	public String getPortNo() {
		return portNo;
	}
	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPerceivedSeverity() {
		return perceivedSeverity;
	}
	public void setPerceivedSeverity(String perceivedSeverity) {
		this.perceivedSeverity = perceivedSeverity;
	}
	public int getObjectType() {
		return this.objectType;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public int getAckState() {
		return this.ackState;
	}
	public void setAckState(int ack) {
		this.ackState = ack;
	}
}
