package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class TopologicalLinkModel {
	
	private NameAndStringValue_T[] name;
	private String userLabel;
	private int direction;
	private short rate;
	private NameAndStringValue_T[] aEndTP;
	private NameAndStringValue_T[] zEndTP;
	private NameAndStringValue_T[] additionalInfo;
	
	private String nativeEMSName;
	private String owner;
	
	//extend
	private String nameString;
	private String aEndNESerialNo;
	private String aEndPtpName;
	private String zEndNESerialNo;
	private String zEndPtpName;
	
	private String fdName;
	private String fdfrName;
	private String underlyingType;
	private String protocolType;
	private String serverObj;
	
	public boolean switchDirection(){
		if(direction==globaldefs.ConnectionDirection_T._CD_BI){
			NameAndStringValue_T[] tmpTP=aEndTP;
			aEndTP=zEndTP;
			zEndTP=tmpTP;
			String tmp=aEndNESerialNo;
			aEndNESerialNo=zEndNESerialNo;
			zEndNESerialNo=tmp;
			tmp=aEndPtpName;
			aEndPtpName=zEndPtpName;
			zEndPtpName=tmp;
			return true;
		}
		return false;
	}
	
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
	public String getaEndNESerialNo() {
		return aEndNESerialNo;
	}
	public void setaEndNESerialNo(String aEndNESerialNo) {
		this.aEndNESerialNo = aEndNESerialNo;
	}
	public String getaEndPtpName() {
		return aEndPtpName;
	}
	public void setaEndPtpName(String aEndPtpName) {
		this.aEndPtpName = aEndPtpName;
	}
	public String getzEndNESerialNo() {
		return zEndNESerialNo;
	}
	public void setzEndNESerialNo(String zEndNESerialNo) {
		this.zEndNESerialNo = zEndNESerialNo;
	}
	public String getzEndPtpName() {
		return zEndPtpName;
	}
	public void setzEndPtpName(String zEndPtpName) {
		this.zEndPtpName = zEndPtpName;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public String getFdName() {
		return fdName;
	}

	public void setFdName(String fdName) {
		this.fdName = fdName;
	}

	public String getFdfrName() {
		return fdfrName;
	}

	public void setFdfrName(String fdfrName) {
		this.fdfrName = fdfrName;
	}

	public String getUnderlyingType() {
		return underlyingType;
	}

	public void setUnderlyingType(String underlyingType) {
		this.underlyingType = underlyingType;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	public String getServerObj() {
		return serverObj;
	}

	public void setServerObj(String serverObj) {
		this.serverObj = serverObj;
	}
}
