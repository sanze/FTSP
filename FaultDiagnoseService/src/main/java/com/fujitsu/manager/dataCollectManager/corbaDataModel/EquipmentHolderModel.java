package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

import java.util.List;

/**
 * @author xuxiaojun
 * 
 */
public class EquipmentHolderModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private boolean alarmReportingIndicator;
	private String holderType;
	private NameAndStringValue_T[] expectedOrInstalledEquipment;
	private List<String> acceptableEquipmentTypeList;
	private int holderState;
	private NameAndStringValue_T[] additionalInfo;

	// *********************** ZTE ******************************
	private String location = null;
	private String vendorName = null;
	private String hardwareVersion = null;
	private String serialNo = null;

	// extend
	private String nameString;
	private String rackNo;
	private String shelfNo;
	private String slotNo;
	private String unitNo;
	private String subSlotNo;
	private String subUnitNo;
	private String shelfType;
	
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
	public String getHolderType() {
		return holderType;
	}
	public void setHolderType(String holderType) {
		this.holderType = holderType;
	}
	public NameAndStringValue_T[] getExpectedOrInstalledEquipment() {
		return expectedOrInstalledEquipment;
	}
	public void setExpectedOrInstalledEquipment(
			NameAndStringValue_T[] expectedOrInstalledEquipment) {
		this.expectedOrInstalledEquipment = expectedOrInstalledEquipment;
	}
	public List<String> getAcceptableEquipmentTypeList() {
		return acceptableEquipmentTypeList;
	}
	public void setAcceptableEquipmentTypeList(
			List<String> acceptableEquipmentTypeList) {
		this.acceptableEquipmentTypeList = acceptableEquipmentTypeList;
	}
	public int getHolderState() {
		return holderState;
	}
	public void setHolderState(int holderState) {
		this.holderState = holderState;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getHardwareVersion() {
		return hardwareVersion;
	}
	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
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
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getSubSlotNo() {
		return subSlotNo;
	}
	public void setSubSlotNo(String subSlotNo) {
		this.subSlotNo = subSlotNo;
	}
	public String getSubUnitNo() {
		return subUnitNo;
	}
	public void setSubUnitNo(String subUnitNo) {
		this.subUnitNo = subUnitNo;
	}
	public String getShelfType() {
		return shelfType;
	}
	public void setShelfType(String shelfType) {
		this.shelfType = shelfType;
	}
}
