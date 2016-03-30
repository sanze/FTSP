package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class EquipmentModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private boolean alarmReportingIndicator;
	private int serviceState;
	private String expectedEquipmentObjectType;
	private String installedEquipmentObjectType;
	private String installedPartNumber;
	private String installedVersion;
	private String installedSerialNumber;
	private NameAndStringValue_T[] additionalInfo;
	
	// *********************** ZTE ******************************
	private String hardwareVersion;
	private String softwareVersion;
	private boolean hasProtection;
	private String expectedBoardType;
	private String installedBoardType;
	
	//extend
	private String nameString;
	private String rackNo;
	private String shelfNo;
	private String slotNo;
	private String subSlotNo;
	
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
	public boolean isAlarmReportingIndicator() {
		return alarmReportingIndicator;
	}
	public void setAlarmReportingIndicator(boolean alarmReportingIndicator) {
		this.alarmReportingIndicator = alarmReportingIndicator;
	}
	public int getServiceState() {
		return serviceState;
	}
	public void setServiceState(int serviceState) {
		this.serviceState = serviceState;
	}
	public String getExpectedEquipmentObjectType() {
		return expectedEquipmentObjectType;
	}
	public void setExpectedEquipmentObjectType(String expectedEquipmentObjectType) {
		this.expectedEquipmentObjectType = expectedEquipmentObjectType;
	}
	public String getInstalledEquipmentObjectType() {
		return installedEquipmentObjectType;
	}
	public void setInstalledEquipmentObjectType(String installedEquipmentObjectType) {
		this.installedEquipmentObjectType = installedEquipmentObjectType;
	}
	public String getInstalledPartNumber() {
		return installedPartNumber;
	}
	public void setInstalledPartNumber(String installedPartNumber) {
		this.installedPartNumber = installedPartNumber;
	}
	public String getInstalledVersion() {
		return installedVersion;
	}
	public void setInstalledVersion(String installedVersion) {
		this.installedVersion = installedVersion;
	}
	public String getInstalledSerialNumber() {
		return installedSerialNumber;
	}
	public void setInstalledSerialNumber(String installedSerialNumber) {
		this.installedSerialNumber = installedSerialNumber;
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
	public boolean isHasProtection() {
		return hasProtection;
	}
	public void setHasProtection(boolean hasProtection) {
		this.hasProtection = hasProtection;
	}
	public String getExpectedBoardType() {
		return expectedBoardType;
	}
	public void setExpectedBoardType(String expectedBoardType) {
		this.expectedBoardType = expectedBoardType;
	}
	public String getInstalledBoardType() {
		return installedBoardType;
	}
	public void setInstalledBoardType(String installedBoardType) {
		this.installedBoardType = installedBoardType;
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
	public String getSubSlotNo() {
		return subSlotNo;
	}
	public void setSubSlotNo(String subSlotNo) {
		this.subSlotNo = subSlotNo;
	}
	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
}
