package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class EProtectionGroupModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private String eProtectionGroupType;
	private int protectionSchemeState;
	private int reversionMode;
	private NameAndStringValue_T[][] protectedList;
	private NameAndStringValue_T[][] protectingList;
	private NameAndStringValue_T[] ePgpParameters;
	private NameAndStringValue_T[] additionalInfo;
	
	//extend
	private String nameString;
	private int epgpGroup;
	private int epgpLocation;
	//保护属性_倒换模式
	private String type;
	//保护属性_恢复时间
	private String wtrTime;
	
	
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
	public String geteProtectionGroupType() {
		return eProtectionGroupType;
	}
	public void seteProtectionGroupType(String eProtectionGroupType) {
		this.eProtectionGroupType = eProtectionGroupType;
	}
	public int getProtectionSchemeState() {
		return protectionSchemeState;
	}
	public void setProtectionSchemeState(int protectionSchemeState) {
		this.protectionSchemeState = protectionSchemeState;
	}
	public int getReversionMode() {
		return reversionMode;
	}
	public void setReversionMode(int reversionMode) {
		this.reversionMode = reversionMode;
	}
	public NameAndStringValue_T[][] getProtectedList() {
		return protectedList;
	}
	public void setProtectedList(NameAndStringValue_T[][] protectedList) {
		this.protectedList = protectedList;
	}
	public NameAndStringValue_T[][] getProtectingList() {
		return protectingList;
	}
	public void setProtectingList(NameAndStringValue_T[][] protectingList) {
		this.protectingList = protectingList;
	}
	public NameAndStringValue_T[] getePgpParameters() {
		return ePgpParameters;
	}
	public void setePgpParameters(NameAndStringValue_T[] ePgpParameters) {
		this.ePgpParameters = ePgpParameters;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
	public int getEpgpGroup() {
		return epgpGroup;
	}
	public void setEpgpGroup(int epgpGroup) {
		this.epgpGroup = epgpGroup;
	}
	public int getEpgpLocation() {
		return epgpLocation;
	}
	public void setEpgpLocation(int epgpLocation) {
		this.epgpLocation = epgpLocation;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWtrTime() {
		return wtrTime;
	}
	public void setWtrTime(String wtrTime) {
		this.wtrTime = wtrTime;
	}

}
