package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

public class TCADataModel{

	private String notificationId;
	private NameAndStringValue_T[] objectName;
	private String nativeEMSName;
	private String nateiveProbableCause;
	private int objectType;
	private String emsTime;
	private String neTime;
	private boolean isClearable;
	private short layerRate;
	private String granularity;
	private String pmParameterName;
	private String pmLocation;
	private int thresholdType;
	private float value;
	private String unit;
	private NameAndStringValue_T[] additionalInfo;
	
	// *********************** ZTE ******************************
	private NameAndStringValue_T[] alarmDetectInfo;
	private int perceivedSeverity;
	private String raiseTime;
	private String clearTime;
	private String objectTypeQualifier;
	private String TCASoureLabel;
	public String getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}
	public NameAndStringValue_T[] getObjectName() {
		return objectName;
	}
	public void setObjectName(NameAndStringValue_T[] objectName) {
		this.objectName = objectName;
	}
	public String getNativeEMSName() {
		return nativeEMSName;
	}
	public void setNativeEMSName(String nativeEMSName) {
		this.nativeEMSName = nativeEMSName;
	}
	public String getNateiveProbableCause() {
		return nateiveProbableCause;
	}
	public void setNateiveProbableCause(String nateiveProbableCause) {
		this.nateiveProbableCause = nateiveProbableCause;
	}
	public int getObjectType() {
		return objectType;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public String getEmsTime() {
		return emsTime;
	}
	public void setEmsTime(String emsTime) {
		this.emsTime = emsTime;
	}
	public String getNeTime() {
		return neTime;
	}
	public void setNeTime(String neTime) {
		this.neTime = neTime;
	}
	public boolean isClearable() {
		return isClearable;
	}
	public void setClearable(boolean isClearable) {
		this.isClearable = isClearable;
	}
	public short getLayerRate() {
		return layerRate;
	}
	public void setLayerRate(short layerRate) {
		this.layerRate = layerRate;
	}
	public String getGranularity() {
		return granularity;
	}
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}
	public String getPmParameterName() {
		return pmParameterName;
	}
	public void setPmParameterName(String pmParameterName) {
		this.pmParameterName = pmParameterName;
	}
	public String getPmLocation() {
		return pmLocation;
	}
	public void setPmLocation(String pmLocation) {
		this.pmLocation = pmLocation;
	}
	public int getThresholdType() {
		return thresholdType;
	}
	public void setThresholdType(int thresholdType) {
		this.thresholdType = thresholdType;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public NameAndStringValue_T[] getAlarmDetectInfo() {
		return alarmDetectInfo;
	}
	public void setAlarmDetectInfo(NameAndStringValue_T[] alarmDetectInfo) {
		this.alarmDetectInfo = alarmDetectInfo;
	}
	public int getPerceivedSeverity() {
		return perceivedSeverity;
	}
	public void setPerceivedSeverity(int perceivedSeverity) {
		this.perceivedSeverity = perceivedSeverity;
	}
	public String getRaiseTime() {
		return raiseTime;
	}
	public void setRaiseTime(String raiseTime) {
		this.raiseTime = raiseTime;
	}
	public String getClearTime() {
		return clearTime;
	}
	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}
	public String getObjectTypeQualifier() {
		return objectTypeQualifier;
	}
	public void setObjectTypeQualifier(String objectTypeQualifier) {
		this.objectTypeQualifier = objectTypeQualifier;
	}
	public String getTCASoureLabel() {
		return TCASoureLabel;
	}
	public void setTCASoureLabel(String tCASoureLabel) {
		TCASoureLabel = tCASoureLabel;
	}

}
