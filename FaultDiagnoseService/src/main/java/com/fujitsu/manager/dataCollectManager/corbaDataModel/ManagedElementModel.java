package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class ManagedElementModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private String location;
	private String version;
	private String productName;
	private int communicationState;
	private boolean emsInSyncState;
	private short[] supportedRates;
	private NameAndStringValue_T[] additionalInfo;

	// *********************** ZTE ******************************
	private String hardwareVersion;
	private String softwareVersion;
	private String meType;
	private short connectionRates[];
	private String vendorName;
	private String operationalStatus;
	private int alarmStatus;
	private String descriptionInfo;
	private String netAddress;
	
	//extend
	private String neSerialNo;

	public NameAndStringValue_T[] getName() {
		return name;
	}

	public void setName(NameAndStringValue_T[] name) {
		this.name = name;
	}

	public String getUserLabel() {
		return userLabel;
	}

	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}

	public String getNativeEMSName() {
		return nativeEMSName;
	}

	public void setNativeEMSName(String nativeEMSName) {
		this.nativeEMSName = nativeEMSName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getCommunicationState() {
		return communicationState;
	}

	public void setCommunicationState(int communicationState) {
		this.communicationState = communicationState;
	}

	public boolean isEmsInSyncState() {
		return emsInSyncState;
	}

	public void setEmsInSyncState(boolean emsInSyncState) {
		this.emsInSyncState = emsInSyncState;
	}

	public short[] getSupportedRates() {
		return supportedRates;
	}

	public void setSupportedRates(short[] supportedRates) {
		this.supportedRates = supportedRates;
	}

	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}

	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public String getMeType() {
		return meType;
	}

	public void setMeType(String meType) {
		this.meType = meType;
	}

	public short[] getConnectionRates() {
		return connectionRates;
	}

	public void setConnectionRates(short[] connectionRates) {
		this.connectionRates = connectionRates;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getOperationalStatus() {
		return operationalStatus;
	}

	public void setOperationalStatus(String operationalStatus) {
		this.operationalStatus = operationalStatus;
	}

	public int getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public String getDescriptionInfo() {
		return descriptionInfo;
	}

	public void setDescriptionInfo(String descriptionInfo) {
		this.descriptionInfo = descriptionInfo;
	}

	public String getNetAddress() {
		return netAddress;
	}

	public void setNetAddress(String netAddress) {
		this.netAddress = netAddress;
	}

	public String getNeSerialNo() {
		return neSerialNo;
	}

	public void setNeSerialNo(String neSerialNo) {
		this.neSerialNo = neSerialNo;
	}

}
