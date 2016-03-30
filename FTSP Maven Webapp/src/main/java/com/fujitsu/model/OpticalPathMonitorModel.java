package com.fujitsu.model;

import java.util.List;
import java.util.Map;

public class OpticalPathMonitorModel {
	
	private int mode;
	
	private Integer linkId;
	private String linkName;
	private String downOffset;
	private String upperOffset;
	private String collectDate;
	private String att;
	private Integer aEmsConnectionId;
	private Integer zEmsConnectionId;
	private Integer aPtpId;
	private Integer zPtpId;
	private List<String> selectTargets;
	private List<Map> selectTargetsMap;
	private List<Integer> userCurrentPmLinkIds;
	
	public Integer getLinkId() {
		return linkId;
	}
	public void setLinkId(Integer linkId) {
		this.linkId = linkId;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getDownOffset() {
		return downOffset;
	}
	public void setDownOffset(String downOffset) {
		this.downOffset = downOffset;
	}
	public String getUpperOffset() {
		return upperOffset;
	}
	public void setUpperOffset(String upperOffset) {
		this.upperOffset = upperOffset;
	}
	public String getCollectDate() {
		return collectDate;
	}
	public void setCollectDate(String collectDate) {
		this.collectDate = collectDate;
	}
	public List<String> getSelectTargets() {
		return selectTargets;
	}
	public void setSelectTargets(List<String> selectTargets) {
		this.selectTargets = selectTargets;
	}
	public List<Map> getSelectTargetsMap() {
		return selectTargetsMap;
	}
	public void setSelectTargetsMap(List<Map> selectTargetsMap) {
		this.selectTargetsMap = selectTargetsMap;
	}
	public String getAtt() {
		return att;
	}
	public void setAtt(String att) {
		this.att = att;
	}
	public Integer getaPtpId() {
		return aPtpId;
	}
	public void setaPtpId(Integer aPtpId) {
		this.aPtpId = aPtpId;
	}
	public Integer getzPtpId() {
		return zPtpId;
	}
	public void setzPtpId(Integer zPtpId) {
		this.zPtpId = zPtpId;
	}
	public Integer getaEmsConnectionId() {
		return aEmsConnectionId;
	}
	public void setaEmsConnectionId(Integer aEmsConnectionId) {
		this.aEmsConnectionId = aEmsConnectionId;
	}
	public Integer getzEmsConnectionId() {
		return zEmsConnectionId;
	}
	public void setzEmsConnectionId(Integer zEmsConnectionId) {
		this.zEmsConnectionId = zEmsConnectionId;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public List<Integer> getUserCurrentPmLinkIds() {
		return userCurrentPmLinkIds;
	}
	public void setUserCurrentPmLinkIds(List<Integer> userCurrentPmLinkIds) {
		this.userCurrentPmLinkIds = userCurrentPmLinkIds;
	}
}
