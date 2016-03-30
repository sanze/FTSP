package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

import java.util.List;

/**
 * @author xuxiaojun
 *
 */
public class VirtualLANModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private NameAndStringValue_T[] paraList;
	private List<ForwardEndPointModel> forwardTPList;
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
	public NameAndStringValue_T[] getParaList() {
		return paraList;
	}
	public void setParaList(NameAndStringValue_T[] paraList) {
		this.paraList = paraList;
	}
	public List<ForwardEndPointModel> getForwardTPList() {
		return forwardTPList;
	}
	public void setForwardTPList(List<ForwardEndPointModel> forwardTPList) {
		this.forwardTPList = forwardTPList;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
}
