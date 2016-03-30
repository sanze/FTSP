package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

public class CrossConnectModel {
	
	private boolean active;
	private int direction;
	private int ccType;
	private NameAndStringValue_T[][] aEndNameList;
	private NameAndStringValue_T[][] zEndNameList;
	private NameAndStringValue_T[] additionalInfo;

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getCcType() {
		return ccType;
	}
	public void setCcType(int ccType) {
		this.ccType = ccType;
	}
	public NameAndStringValue_T[][] getaEndNameList() {
		return aEndNameList;
	}
	public void setaEndNameList(NameAndStringValue_T[][] aEndNameList) {
		this.aEndNameList = aEndNameList;
	}
	public NameAndStringValue_T[][] getzEndNameList() {
		return zEndNameList;
	}
	public void setzEndNameList(NameAndStringValue_T[][] zEndNameList) {
		this.zEndNameList = zEndNameList;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
}
