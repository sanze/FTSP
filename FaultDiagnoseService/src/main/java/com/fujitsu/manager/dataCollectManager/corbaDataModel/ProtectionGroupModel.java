package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class ProtectionGroupModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private int protectionGroupType;
	private int protectionSchemeState;
	private int reversionMode;
	private int rate;
	private NameAndStringValue_T[][] pgpTPList;
	private NameAndStringValue_T[] pgpParameters;
	private NameAndStringValue_T[] additionalInfo;
	
	//extend
	private String nameString;
	private int pgpGroup;
	private int pgpLocation;
	 
	private String switchMode;
	 //恢复时间
	private String wtrTime;
	//持续时间
	private String holdOffTime;
	//倒换数值
	private String lodNumSwitches;
	//和LODNumSwitches配合
	private String lodDuration;
	//环倒换协议
	private String springProtocol;
	//环倒换节点ID
	private String springNodeId;
	//倒换位置
	private String switchPosition;
	//标示保护上是否可以配置非预占业务
	private String nonPreEmptibleTraffic;
	
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
	public void setProtectionGroupType(int protectionGroupType) {
		this.protectionGroupType = protectionGroupType;
	}
	public void setProtectionSchemeState(int protectionSchemeState) {
		this.protectionSchemeState = protectionSchemeState;
	}
	public void setReversionMode(int reversionMode) {
		this.reversionMode = reversionMode;
	}
	public void setRate(int rate) {
		this.rate = rate;
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
	public int getProtectionGroupType() {
		return protectionGroupType;
	}
	public int getProtectionSchemeState() {
		return protectionSchemeState;
	}
	public int getReversionMode() {
		return reversionMode;
	}
	public int getRate() {
		return rate;
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
	public int getPgpGroup() {
		return pgpGroup;
	}
	public void setPgpGroup(int pgpGroup) {
		this.pgpGroup = pgpGroup;
	}
	public int getPgpLocation() {
		return pgpLocation;
	}
	public void setPgpLocation(int pgpLocation) {
		this.pgpLocation = pgpLocation;
	}
	public String getSwitchMode() {
		return switchMode;
	}
	public void setSwitchMode(String switchMode) {
		this.switchMode = switchMode;
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
	public String getLodNumSwitches() {
		return lodNumSwitches;
	}
	public void setLodNumSwitches(String lodNumSwitches) {
		this.lodNumSwitches = lodNumSwitches;
	}
	public String getLodDuration() {
		return lodDuration;
	}
	public void setLodDuration(String lodDuration) {
		this.lodDuration = lodDuration;
	}
	public String getSpringProtocol() {
		return springProtocol;
	}
	public void setSpringProtocol(String springProtocol) {
		this.springProtocol = springProtocol;
	}
	public String getSpringNodeId() {
		return springNodeId;
	}
	public void setSpringNodeId(String springNodeId) {
		this.springNodeId = springNodeId;
	}
	public String getSwitchPosition() {
		return switchPosition;
	}
	public void setSwitchPosition(String switchPosition) {
		this.switchPosition = switchPosition;
	}
	public String getNonPreEmptibleTraffic() {
		return nonPreEmptibleTraffic;
	}
	public void setNonPreEmptibleTraffic(String nonPreEmptibleTraffic) {
		this.nonPreEmptibleTraffic = nonPreEmptibleTraffic;
	}
	
	
}
