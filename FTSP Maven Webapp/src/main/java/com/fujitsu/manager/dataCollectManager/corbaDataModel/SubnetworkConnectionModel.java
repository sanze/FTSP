package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

public class SubnetworkConnectionModel {
	
	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private int sncState;
	private int direction;
	private short rate;
	private int staticProtectionLevel;
	private int sncType;
	private NameAndStringValue_T[] aEndTP;
	private NameAndStringValue_T[] zEndTP;
	private NameAndStringValue_T[] additionalInfo;
	
	//extend
	private String sncSerialNo;
	private String CreateTime;
	private String LSPType;
	private String ServiceState;
	
	private String subnetworkName;
	private String belong_snc;;
	
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
	public int getSncState() {
		return sncState;
	}
	public void setSncState(int sncState) {
		this.sncState = sncState;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public short getRate() {
		return rate;
	}
	public void setRate(short rate) {
		this.rate = rate;
	}
	public int getStaticProtectionLevel() {
		return staticProtectionLevel;
	}
	public void setStaticProtectionLevel(int staticProtectionLevel) {
		this.staticProtectionLevel = staticProtectionLevel;
	}
	public int getSncType() {
		return sncType;
	}
	public void setSncType(int sncType) {
		this.sncType = sncType;
	}
	public NameAndStringValue_T[] getaEndTP() {
		return aEndTP;
	}
	public void setaEndTP(NameAndStringValue_T[] aEndTP) {
		this.aEndTP = aEndTP;
	}
	public NameAndStringValue_T[] getzEndTP() {
		return zEndTP;
	}
	public void setzEndTP(NameAndStringValue_T[] zEndTP) {
		this.zEndTP = zEndTP;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getSncSerialNo() {
		return sncSerialNo;
	}
	public void setSncSerialNo(String sncSerialNo) {
		this.sncSerialNo = sncSerialNo;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getLSPType() {
		return LSPType;
	}
	public void setLSPType(String lSPType) {
		LSPType = lSPType;
	}
	public String getServiceState() {
		return ServiceState;
	}
	public void setServiceState(String serviceState) {
		ServiceState = serviceState;
	}
	public String getSubnetworkName() {
		return subnetworkName;
	}
	public void setSubnetworkName(String subnetworkName) {
		this.subnetworkName = subnetworkName;
	}
	public String getBelong_snc() {
		return belong_snc;
	}
	public void setBelong_snc(String belong_snc) {
		this.belong_snc = belong_snc;
	}
	
}
