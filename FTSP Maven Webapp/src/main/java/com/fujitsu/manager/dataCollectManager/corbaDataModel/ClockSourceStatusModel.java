package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class ClockSourceStatusModel {

	private NameAndStringValue_T[] name;
	private String nativeEMSName;
	private String status;
	private String timingMode;
	private String quality;
	private String workingMode;
	private NameAndStringValue_T[] additionalInfo;
	
	//extend
	private String nameString;
	private boolean isCurrent;
	private int timingModeFlag;
	private int qualityFlag;
	private int workingModeFlag;
	
	
	public void setName(NameAndStringValue_T[] name) {
		this.name = name;
	}
	public void setNativeEMSName(String nativeEMSName) {
		this.nativeEMSName = nativeEMSName;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setTimingMode(String timingMode) {
		this.timingMode = timingMode;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public void setWorkingMode(String workingMode) {
		this.workingMode = workingMode;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public NameAndStringValue_T[] getName() {
		return name;
	}
	public String getNativeEMSName() {
		return nativeEMSName;
	}
	public String getStatus() {
		return status;
	}
	public String getTimingMode() {
		return timingMode;
	}
	public String getQuality() {
		return quality;
	}
	public String getWorkingMode() {
		return workingMode;
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
	public boolean isCurrent() {
		return isCurrent;
	}
	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}
	public int getTimingModeFlag() {
		return timingModeFlag;
	}
	public void setTimingModeFlag(int timingModeFlag) {
		this.timingModeFlag = timingModeFlag;
	}
	public int getQualityFlag() {
		return qualityFlag;
	}
	public void setQualityFlag(int qualityFlag) {
		this.qualityFlag = qualityFlag;
	}
	public int getWorkingModeFlag() {
		return workingModeFlag;
	}
	public void setWorkingModeFlag(int workingModeFlag) {
		this.workingModeFlag = workingModeFlag;
	}

}
