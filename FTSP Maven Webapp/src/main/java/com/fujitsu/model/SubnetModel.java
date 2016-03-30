package com.fujitsu.model;

public class SubnetModel {
	
	private Integer subnetId;
	
	private Integer emsConnectionId;
	
	private Integer parentSubnetId;
	
	private String subnetName;
	
	private String subnetNote;
	
	private Integer positionX;
	
	private Integer positionY;
	
	private Integer isDel;
	
	private String createTime;
	
	private String updateTime;

	public Integer getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(Integer subnetId) {
		this.subnetId = subnetId;
	}

	public Integer getEmsConnectionId() {
		return emsConnectionId;
	}

	public void setEmsConnectionId(Integer emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}

	public Integer getParentSubnetId() {
		return parentSubnetId;
	}

	public void setParentSubnetId(Integer parentSubnetId) {
		this.parentSubnetId = parentSubnetId;
	}

	public String getSubnetName() {
		return subnetName;
	}

	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}

	public String getSubnetNote() {
		return subnetNote;
	}

	public void setSubnetNote(String subnetNote) {
		this.subnetNote = subnetNote;
	}

	public Integer getPositionX() {
		return positionX;
	}

	public void setPositionX(Integer positionX) {
		this.positionX = positionX;
	}

	public Integer getPositionY() {
		return positionY;
	}

	public void setPositionY(Integer positionY) {
		this.positionY = positionY;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}	
}