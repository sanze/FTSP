package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

public class ProtectionSwtichDataModel{

	private String notificationId;
	// 网管ID
	private int emsConnectionId;
	// 网元 ID
	private int neId;
	private String emsTime;
	private String neTime;
	private int protectType;
	private String protectTypeOri;
	private int swtichReason;
	private long layerRate;
	private String groupName;
	private int targetType;
	private NameAndStringValue_T[] protectedTP;
	private NameAndStringValue_T[] switchAwayFromTP;
	private NameAndStringValue_T[] switchToTP;
	private String nativeEMSName;
	private NameAndStringValue_T[] additionalInfo;
	
	//extend 
	private String neSerialNo;
	private String protectCategory;
	
	/**
	 * @return the notificationId
	 */
	public String getNotificationId() {
		return notificationId;
	}
	/**
	 * @param notificationId the notificationId to set
	 */
	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
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
	 * @return the neId
	 */
	public int getNeId() {
		return neId;
	}
	/**
	 * @param neId the neId to set
	 */
	public void setNeId(int neId) {
		this.neId = neId;
	}
	/**
	 * @return the emsTime
	 */
	public String getEmsTime() {
		return emsTime;
	}
	/**
	 * @param emsTime the emsTime to set
	 */
	public void setEmsTime(String emsTime) {
		this.emsTime = emsTime;
	}
	/**
	 * @return the neTime
	 */
	public String getNeTime() {
		return neTime;
	}
	/**
	 * @param neTime the neTime to set
	 */
	public void setNeTime(String neTime) {
		this.neTime = neTime;
	}
	/**
	 * @return the protectType
	 */
	public int getProtectType() {
		return protectType;
	}
	/**
	 * @param protectType the protectType to set
	 */
	public void setProtectType(int protectType) {
		this.protectType = protectType;
	}
	/**
	 * @return the swtichReason
	 */
	public int getSwtichReason() {
		return swtichReason;
	}
	/**
	 * @param swtichReason the swtichReason to set
	 */
	public void setSwtichReason(int swtichReason) {
		this.swtichReason = swtichReason;
	}
	/**
	 * @return the layerRate
	 */
	public long getLayerRate() {
		return layerRate;
	}
	/**
	 * @param layerRate the layerRate to set
	 */
	public void setLayerRate(long layerRate) {
		this.layerRate = layerRate;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
	/**
	 * @return the protectedTP
	 */
	public NameAndStringValue_T[] getProtectedTP() {
		return protectedTP;
	}
	/**
	 * @param protectedTP the protectedTP to set
	 */
	public void setProtectedTP(NameAndStringValue_T[] protectedTP) {
		this.protectedTP = protectedTP;
	}
	/**
	 * @return the switchAwayFromTP
	 */
	public NameAndStringValue_T[] getSwitchAwayFromTP() {
		return switchAwayFromTP;
	}
	/**
	 * @param switchAwayFromTP the switchAwayFromTP to set
	 */
	public void setSwitchAwayFromTP(NameAndStringValue_T[] switchAwayFromTP) {
		this.switchAwayFromTP = switchAwayFromTP;
	}
	/**
	 * @return the switchToTP
	 */
	public NameAndStringValue_T[] getSwitchToTP() {
		return switchToTP;
	}
	/**
	 * @param switchToTP the switchToTP to set
	 */
	public void setSwitchToTP(NameAndStringValue_T[] switchToTP) {
		this.switchToTP = switchToTP;
	}
	/**
	 * @return the nativeEMSName
	 */
	public String getNativeEMSName() {
		return nativeEMSName;
	}
	/**
	 * @param nativeEMSName the nativeEMSName to set
	 */
	public void setNativeEMSName(String nativeEMSName) {
		this.nativeEMSName = nativeEMSName;
	}
	/**
	 * @return the additionalInfo
	 */
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	/**
	 * @param additionalInfo the additionalInfo to set
	 */
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	/**
	 * @return the protectTypeOri
	 */
	public String getProtectTypeOri() {
		return protectTypeOri;
	}
	/**
	 * @param protectTypeOri the protectTypeOri to set
	 */
	public void setProtectTypeOri(String protectTypeOri) {
		this.protectTypeOri = protectTypeOri;
	}
	/**
	 * @return the neSerialNo
	 */
	public String getNeSerialNo() {
		return neSerialNo;
	}
	/**
	 * @param neSerialNo the neSerialNo to set
	 */
	public void setNeSerialNo(String neSerialNo) {
		this.neSerialNo = neSerialNo;
	}
	/**
	 * @return the protectCategory
	 */
	public String getProtectCategory() {
		return protectCategory;
	}
	/**
	 * @param protectCategory the protectCategory to set
	 */
	public void setProtectCategory(String protectCategory) {
		this.protectCategory = protectCategory;
	}

}
