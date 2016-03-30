package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

import java.util.List;

/**
 * @author xuxiaojun
 *
 */
public class FdfrModel {

	private String fdName;
	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private int direction;
	private int layer;
	private List<TerminationPointModel> aEnd;
	private List<TerminationPointModel> zEnd;
	private String networkAccessDomain;
	private String fDConnectivityState;
	private int flexible;
	private int administrativeState;
	private int fdfrState;
	private String fdfrType;
	private String Comments;
	private String Customer;
	private String igmpSnoopingState;
	private String serviceType;
	private String serviceState;
	private String trafficType;
	private String oamEnabled;
	private NameAndStringValue_T[] additionalInfo;
	
	private String nameString;

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

	public String getfDConnectivityState() {
		return fDConnectivityState;
	}

	public void setfDConnectivityState(String fDConnectivityState) {
		this.fDConnectivityState = fDConnectivityState;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}


	public String getFdfrType() {
		return fdfrType;
	}

	public void setFdfrType(String fdfrType) {
		this.fdfrType = fdfrType;
	}

	public String getComments() {
		return Comments;
	}

	public void setComments(String comments) {
		Comments = comments;
	}

	public String getCustomer() {
		return Customer;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public String getIgmpSnoopingState() {
		return igmpSnoopingState;
	}

	public void setIgmpSnoopingState(String igmpSnoopingState) {
		this.igmpSnoopingState = igmpSnoopingState;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceState() {
		return serviceState;
	}

	public void setServiceState(String serviceState) {
		this.serviceState = serviceState;
	}

	public String getTrafficType() {
		return trafficType;
	}

	public void setTrafficType(String trafficType) {
		this.trafficType = trafficType;
	}

	public String getOamEnabled() {
		return oamEnabled;
	}

	public void setOamEnabled(String oamEnabled) {
		this.oamEnabled = oamEnabled;
	}

	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}


	public int getFlexible() {
		return flexible;
	}

	public void setFlexible(int flexible) {
		this.flexible = flexible;
	}

	public void setAdministrativeState(int administrativeState) {
		this.administrativeState = administrativeState;
	}

	public void setFdfrState(int fdfrState) {
		this.fdfrState = fdfrState;
	}

	public List<TerminationPointModel> getaEnd() {
		return aEnd;
	}

	public void setaEnd(List<TerminationPointModel> aEnd) {
		this.aEnd = aEnd;
	}

	public List<TerminationPointModel> getzEnd() {
		return zEnd;
	}

	public void setzEnd(List<TerminationPointModel> zEnd) {
		this.zEnd = zEnd;
	}

	public int getAdministrativeState() {
		return administrativeState;
	}

	public int getFdfrState() {
		return fdfrState;
	}

	public String getFdName() {
		return fdName;
	}

	public void setFdName(String fdName) {
		this.fdName = fdName;
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
	
	
}
