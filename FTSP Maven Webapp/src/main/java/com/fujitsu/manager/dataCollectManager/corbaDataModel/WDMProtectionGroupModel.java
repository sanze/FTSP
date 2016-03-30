package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class WDMProtectionGroupModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private String protectionGroupType;
	private int protectionSchemeState;
	private int reversionMode;
	private NameAndStringValue_T[][] pgpTPList;
	private NameAndStringValue_T[] pgpParameters;
	private NameAndStringValue_T[] additionalInfo;
	
	//extend
	private String nameString;
	private int wdmPgpGroup;
	private int wdmPgpLocation;

	 //恢复时间
	private String wtrTime;
	//持续时间
	private String holdOffTime;

	public void setName(NameAndStringValue_T[] name) {
		this.name = name;
	}
	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}
	public void setNativeEMSName(String nativeEMSName) {
		this.nativeEMSName = nativeEMSName;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public void setProtectionGroupType(String protectionGroupType) {
		this.protectionGroupType = protectionGroupType;
	}
	public void setProtectionSchemeState(int protectionSchemeState) {
		this.protectionSchemeState = protectionSchemeState;
	}
	public void setReversionMode(int reversionMode) {
		this.reversionMode = reversionMode;
	}
	public void setPgpTPList(NameAndStringValue_T[][] pgpTPList) {
		this.pgpTPList = pgpTPList;
	}
	public void setPgpParameters(NameAndStringValue_T[] pgpParameters) {
		this.pgpParameters = pgpParameters;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public NameAndStringValue_T[] getName() {
		return name;
	}
	public String getUserLabel() {
		return userLabel;
	}
	public String getNativeEMSName() {
		return nativeEMSName;
	}
	public String getOwner() {
		return owner;
	}
	public String getProtectionGroupType() {
		return protectionGroupType;
	}
	public int getProtectionSchemeState() {
		return protectionSchemeState;
	}
	public int getReversionMode() {
		return reversionMode;
	}
	public NameAndStringValue_T[][] getPgpTPList() {
		return pgpTPList;
	}
	public NameAndStringValue_T[] getPgpParameters() {
		return pgpParameters;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
	public int getWdmPgpGroup() {
		return wdmPgpGroup;
	}
	public void setWdmPgpGroup(int wdmPgpGroup) {
		this.wdmPgpGroup = wdmPgpGroup;
	}
	public int getWdmPgpLocation() {
		return wdmPgpLocation;
	}
	public void setWdmPgpLocation(int wdmPgpLocation) {
		this.wdmPgpLocation = wdmPgpLocation;
	}
	public String getWtrTime() {
		return wtrTime;
	}
	public void setWtrTime(String wtrTime) {
		this.wtrTime = wtrTime;
	}
	public String getHoldOffTime() {
		return holdOffTime;
	}
	public void setHoldOffTime(String holdOffTime) {
		this.holdOffTime = holdOffTime;
	}
	
	
}
