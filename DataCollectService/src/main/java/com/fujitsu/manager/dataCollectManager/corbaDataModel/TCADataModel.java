package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

public class TCADataModel {

	private String notificationId;
	private NameAndStringValue_T[] objectName;
	private String nativeEMSName;
	private Integer objectType;
	private String emsTime;
	private String neTime;
	private boolean isClearable;
	private int perceivedSeverity;
	private short layerRate;
	private String granularity;
	private String pmParameterName;
	private String pmLocation;
	private int thresholdType;
	private float value;
	private String unit;
	private NameAndStringValue_T[] additionalInfo;

	// *********************** ZTE ******************************
	private String nateiveProbableCause;
	private String objectFilterName;
	private NameAndStringValue_T[] alarmDetectInfo;
	private String clearTime;
	private int alarmType;
	private String correlatedAlarmIds;
	private String description;
	private int serviceAffecting;
	private int confirmStatus;
	private int clearStatus;
	private String ackUser;
	private String ackTime;
	private String ackInfo;
	private String vendorProbableCause;
	private String alarmStatus;
	private String probableCause;
	private String customerName;
	private String diagnoseInfo;
	private String confirmStatusOri;
	private String clearStatusOri;
	
	//extend
	private int emsConnectionId;
	private String filterForClear;
	private int factory;
	private int emsType;
	private String objectNameFullString;
	//位置 1.PML_NEAR_END_Rx 2.PML_FAR_END_Rx 3.PML_NEAR_END_Tx 4.PML_FAR_END_Tx 5.PML_BIDIRECTIONAL
	private int locationFlag;
	private Integer granularityFlag;
	private int targetType;

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

	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
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

	public String getClearTime() {
		return clearTime;
	}

	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}

	/**
	 * @return the objectFilterName
	 */
	public String getObjectFilterName() {
		return objectFilterName;
	}

	/**
	 * @param objectFilterName
	 *            the objectFilterName to set
	 */
	public void setObjectFilterName(String objectFilterName) {
		this.objectFilterName = objectFilterName;
	}

	/**
	 * @return the alarmType
	 */
	public int getAlarmType() {
		return alarmType;
	}

	/**
	 * @param alarmType
	 *            the alarmType to set
	 */
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}

	/**
	 * @return the correlatedAlarmIds
	 */
	public String getCorrelatedAlarmIds() {
		return correlatedAlarmIds;
	}

	/**
	 * @param correlatedAlarmIds
	 *            the correlatedAlarmIds to set
	 */
	public void setCorrelatedAlarmIds(String correlatedAlarmIds) {
		this.correlatedAlarmIds = correlatedAlarmIds;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the serviceAffecting
	 */
	public int getServiceAffecting() {
		return serviceAffecting;
	}

	/**
	 * @param serviceAffecting
	 *            the serviceAffecting to set
	 */
	public void setServiceAffecting(int serviceAffecting) {
		this.serviceAffecting = serviceAffecting;
	}

	/**
	 * @return the confirmStatus
	 */
	public int getConfirmStatus() {
		return confirmStatus;
	}

	/**
	 * @param confirmStatus
	 *            the confirmStatus to set
	 */
	public void setConfirmStatus(int confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	/**
	 * @return the clearStatus
	 */
	public int getClearStatus() {
		return clearStatus;
	}

	/**
	 * @param clearStatus
	 *            the clearStatus to set
	 */
	public void setClearStatus(int clearStatus) {
		this.clearStatus = clearStatus;
	}

	/**
	 * @return the ackUser
	 */
	public String getAckUser() {
		return ackUser;
	}

	/**
	 * @param ackUser
	 *            the ackUser to set
	 */
	public void setAckUser(String ackUser) {
		this.ackUser = ackUser;
	}

	/**
	 * @return the ackTime
	 */
	public String getAckTime() {
		return ackTime;
	}

	/**
	 * @param ackTime
	 *            the ackTime to set
	 */
	public void setAckTime(String ackTime) {
		this.ackTime = ackTime;
	}

	/**
	 * @return the ackInfo
	 */
	public String getAckInfo() {
		return ackInfo;
	}

	/**
	 * @param ackInfo
	 *            the ackInfo to set
	 */
	public void setAckInfo(String ackInfo) {
		this.ackInfo = ackInfo;
	}

	/**
	 * @return the vendorProbableCause
	 */
	public String getVendorProbableCause() {
		return vendorProbableCause;
	}

	/**
	 * @param vendorProbableCause
	 *            the vendorProbableCause to set
	 */
	public void setVendorProbableCause(String vendorProbableCause) {
		this.vendorProbableCause = vendorProbableCause;
	}

	/**
	 * @return the alarmStatus
	 */
	public String getAlarmStatus() {
		return alarmStatus;
	}

	/**
	 * @param alarmStatus the alarmStatus to set
	 */
	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	/**
	 * @return the probableCause
	 */
	public String getProbableCause() {
		return probableCause;
	}

	/**
	 * @param probableCause
	 *            the probableCause to set
	 */
	public void setProbableCause(String probableCause) {
		this.probableCause = probableCause;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName
	 *            the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the diagnoseInfo
	 */
	public String getDiagnoseInfo() {
		return diagnoseInfo;
	}

	/**
	 * @param diagnoseInfo
	 *            the diagnoseInfo to set
	 */
	public void setDiagnoseInfo(String diagnoseInfo) {
		this.diagnoseInfo = diagnoseInfo;
	}

	/**
	 * @return the emsConnectionId
	 */
	public int getEmsConnectionId() {
		return emsConnectionId;
	}

	/**
	 * @param emsConnectionId the emsConnectionId to set
	 */
	public void setEmsConnectionId(int emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}

	/**
	 * @return the filterForClear
	 */
	public String getFilterForClear() {
		return filterForClear;
	}

	/**
	 * @param filterForClear the filterForClear to set
	 */
	public void setFilterForClear(String filterForClear) {
		this.filterForClear = filterForClear;
	}

	/**
	 * @return the factory
	 */
	public int getFactory() {
		return factory;
	}

	/**
	 * @param factory the factory to set
	 */
	public void setFactory(int factory) {
		this.factory = factory;
	}

	/**
	 * @return the emsType
	 */
	public int getEmsType() {
		return emsType;
	}

	/**
	 * @param emsType the emsType to set
	 */
	public void setEmsType(int emsType) {
		this.emsType = emsType;
	}

	/**
	 * @return the objectNameFullString
	 */
	public String getObjectNameFullString() {
		return objectNameFullString;
	}

	/**
	 * @param objectNameFullString the objectNameFullString to set
	 */
	public void setObjectNameFullString(String objectNameFullString) {
		this.objectNameFullString = objectNameFullString;
	}

	/**
	 * @return the locationFlag
	 */
	public int getLocationFlag() {
		return locationFlag;
	}

	/**
	 * @param locationFlag the locationFlag to set
	 */
	public void setLocationFlag(int locationFlag) {
		this.locationFlag = locationFlag;
	}

	/**
	 * @return the granularityFlag
	 */
	public Integer getGranularityFlag() {
		return granularityFlag;
	}

	/**
	 * @param granularityFlag the granularityFlag to set
	 */
	public void setGranularityFlag(Integer granularityFlag) {
		this.granularityFlag = granularityFlag;
	}

	/**
	 * @return the targetType
	 */
	public int getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public String getConfirmStatusOri() {
		return confirmStatusOri;
	}

	public void setConfirmStatusOri(String confirmStatusOri) {
		this.confirmStatusOri = confirmStatusOri;
	}

	public String getClearStatusOri() {
		return clearStatusOri;
	}

	public void setClearStatusOri(String clearStatusOri) {
		this.clearStatusOri = clearStatusOri;
	}
	

}
