package com.fujitsu.manager.faultManager.model;

public class EquipNameModel {
	private int unitId;
	private int errorCode = -1;
	private String unitName;
	private String neName;
	private String emsName;
	private String subNetName;
	private String stationName;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getEmsName() {
		return emsName;
	}
	public void setEmsName(String emsName) {
		this.emsName = emsName;
	}
	public String getSubNetName() {
		return subNetName;
	}
	public void setSubNetName(String subNetName) {
		this.subNetName = subNetName;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	
}
