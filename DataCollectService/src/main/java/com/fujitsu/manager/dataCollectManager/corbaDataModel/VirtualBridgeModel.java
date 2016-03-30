package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

import java.util.List;

/**
 * @author xuxiaojun
 *
 */
public class VirtualBridgeModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private List<TerminationPointModel> logicalTPList;
	private NameAndStringValue_T[] parameterList;
	private NameAndStringValue_T[] additionalInfo;
	
	//extend
	private String nameString;
	private String vid;
	private String stpMode;
	private String bridgePriority;
	private String macAging;
	private String helloTime;
	private String maxAge;
	private String forwardDelay;
	
	
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
	public List<TerminationPointModel> getLogicalTPList() {
		return logicalTPList;
	}
	public void setLogicalTPList(List<TerminationPointModel> logicalTPList) {
		this.logicalTPList = logicalTPList;
	}
	
	public NameAndStringValue_T[] getParameterList() {
		return parameterList;
	}
	public void setParameterList(NameAndStringValue_T[] parameterList) {
		this.parameterList = parameterList;
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
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getStpMode() {
		return stpMode;
	}
	public void setStpMode(String stpMode) {
		this.stpMode = stpMode;
	}
	public String getBridgePriority() {
		return bridgePriority;
	}
	public void setBridgePriority(String bridgePriority) {
		this.bridgePriority = bridgePriority;
	}
	public String getMacAging() {
		return macAging;
	}
	public void setMacAging(String macAging) {
		this.macAging = macAging;
	}
	public String getHelloTime() {
		return helloTime;
	}
	public void setHelloTime(String helloTime) {
		this.helloTime = helloTime;
	}
	public String getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(String maxAge) {
		this.maxAge = maxAge;
	}
	public String getForwardDelay() {
		return forwardDelay;
	}
	public void setForwardDelay(String forwardDelay) {
		this.forwardDelay = forwardDelay;
	}

	
}
