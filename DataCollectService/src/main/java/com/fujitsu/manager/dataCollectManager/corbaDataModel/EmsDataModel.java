package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

public class EmsDataModel {

	// ems名
	private NameAndStringValue_T[] name;
	// 显示名称
	private String userLabel;
	// 显示名称
	private String nativeEMSName;

	private String owner;
	// 显示名称
	private String emsVersion;
	// 显示名称
	private String type;

	private NameAndStringValue_T[] additionalInfo;

	// extend
	private String internalEmsName;
	private String interfaceVersion;

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

	public String getEmsVersion() {
		return emsVersion;
	}

	public void setEmsVersion(String emsVersion) {
		this.emsVersion = emsVersion;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getInternalEmsName() {
		return internalEmsName;
	}

	public void setInternalEmsName(String internalEmsName) {
		this.internalEmsName = internalEmsName;
	}

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

}
