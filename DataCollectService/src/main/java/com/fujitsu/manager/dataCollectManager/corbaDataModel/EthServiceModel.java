package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class EthServiceModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private int serviceType;
	private int direction;
	private boolean activeState;
	private NameAndStringValue_T[] additionalInfo;
	
	private NameAndStringValue_T[] aEndPoint;
	private int aEndPointVlanID;
	private int aEndPointTunnel;
	private int aEndPointVc;
	private NameAndStringValue_T[] aEndPointAdditionalInfo;
	
	private NameAndStringValue_T[] zEndPoint;
	private int zEndPointVlanID;
	private int zEndPointTunnel;
	private int zEndPointVc;
	private NameAndStringValue_T[] zEndPointAdditionalInfo;

	//extend 
	private String nameString;
	private String aEndNESerialNo;
	private String aEndPointName;
	private String zEndNESerialNo;
	private String zEndPointName;
	
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
	public int getServiceType() {
		return serviceType;
	}
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public boolean isActiveState() {
		return activeState;
	}
	public void setActiveState(boolean activeState) {
		this.activeState = activeState;
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
	public NameAndStringValue_T[] getaEndPoint() {
		return aEndPoint;
	}
	public void setaEndPoint(NameAndStringValue_T[] aEndPoint) {
		this.aEndPoint = aEndPoint;
	}
	public int getaEndPointVlanID() {
		return aEndPointVlanID;
	}
	public void setaEndPointVlanID(int aEndPointVlanID) {
		this.aEndPointVlanID = aEndPointVlanID;
	}
	public int getaEndPointTunnel() {
		return aEndPointTunnel;
	}
	public void setaEndPointTunnel(int aEndPointTunnel) {
		this.aEndPointTunnel = aEndPointTunnel;
	}
	public int getaEndPointVc() {
		return aEndPointVc;
	}
	public void setaEndPointVc(int aEndPointVc) {
		this.aEndPointVc = aEndPointVc;
	}
	public NameAndStringValue_T[] getaEndPointAdditionalInfo() {
		return aEndPointAdditionalInfo;
	}
	public void setaEndPointAdditionalInfo(
			NameAndStringValue_T[] aEndPointAdditionalInfo) {
		this.aEndPointAdditionalInfo = aEndPointAdditionalInfo;
	}
	public NameAndStringValue_T[] getzEndPoint() {
		return zEndPoint;
	}
	public void setzEndPoint(NameAndStringValue_T[] zEndPoint) {
		this.zEndPoint = zEndPoint;
	}
	public int getzEndPointVlanID() {
		return zEndPointVlanID;
	}
	public void setzEndPointVlanID(int zEndPointVlanID) {
		this.zEndPointVlanID = zEndPointVlanID;
	}
	public int getzEndPointTunnel() {
		return zEndPointTunnel;
	}
	public void setzEndPointTunnel(int zEndPointTunnel) {
		this.zEndPointTunnel = zEndPointTunnel;
	}
	public int getzEndPointVc() {
		return zEndPointVc;
	}
	public void setzEndPointVc(int zEndPointVc) {
		this.zEndPointVc = zEndPointVc;
	}
	public NameAndStringValue_T[] getzEndPointAdditionalInfo() {
		return zEndPointAdditionalInfo;
	}
	public void setzEndPointAdditionalInfo(
			NameAndStringValue_T[] zEndPointAdditionalInfo) {
		this.zEndPointAdditionalInfo = zEndPointAdditionalInfo;
	}
	public String getaEndPointName() {
		return aEndPointName;
	}
	public void setaEndPointName(String aEndPointName) {
		this.aEndPointName = aEndPointName;
	}
	public String getzEndPointName() {
		return zEndPointName;
	}
	public void setzEndPointName(String zEndPointName) {
		this.zEndPointName = zEndPointName;
	}
	public String getaEndNESerialNo() {
		return aEndNESerialNo;
	}
	public void setaEndNESerialNo(String aEndNESerialNo) {
		this.aEndNESerialNo = aEndNESerialNo;
	}
	public String getzEndNESerialNo() {
		return zEndNESerialNo;
	}
	public void setzEndNESerialNo(String zEndNESerialNo) {
		this.zEndNESerialNo = zEndNESerialNo;
	}

}
