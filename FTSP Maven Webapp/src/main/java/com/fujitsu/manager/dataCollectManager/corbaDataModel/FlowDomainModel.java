package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class FlowDomainModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private String networkAccessDomain;
	private int fDConnectivityState;
	private String fdType;
	private String domainType;
	private String bridgeType;
	private String stpType;
	private NameAndStringValue_T[] additionalInfo;
	

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

	public String getNetworkAccessDomain() {
		return networkAccessDomain;
	}

	public void setNetworkAccessDomain(String networkAccessDomain) {
		this.networkAccessDomain = networkAccessDomain;
	}

	public int getfDConnectivityState() {
		return fDConnectivityState;
	}

	public void setfDConnectivityState(int fDConnectivityState) {
		this.fDConnectivityState = fDConnectivityState;
	}

	public String getFdType() {
		return fdType;
	}

	public void setFdType(String fdType) {
		this.fdType = fdType;
	}

	public String getDomainType() {
		return domainType;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	public String getBridgeType() {
		return bridgeType;
	}

	public void setBridgeType(String bridgeType) {
		this.bridgeType = bridgeType;
	}

	public String getStpType() {
		return stpType;
	}

	public void setStpType(String stpType) {
		this.stpType = stpType;
	}

	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
}
