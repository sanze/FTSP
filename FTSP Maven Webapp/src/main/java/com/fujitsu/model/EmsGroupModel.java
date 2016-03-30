package com.fujitsu.model;

public class EmsGroupModel {
	
	//网管分组编号
	private Integer emsGroupId;

	//网管分组名称
	private String emsGroupName;

	//网管分组备注
	private String emsGroupNote;
	
	//网管分组 positionX
	private Integer positionX;
	
	//网管分组 positionY
	private Integer positionY;
	
	//网管分组是否可删除
	private Integer isDel;
	
	//网管分组更新时间
	private String updateTime;
	
	//网管分组创建时间
	private String createTime;

	public Integer getEmsGroupId() {
		return emsGroupId;
	}

	public void setEmsGroupId(Integer emsGroupId) {
		this.emsGroupId = emsGroupId;
	}

	public String getEmsGroupName() {
		return emsGroupName;
	}

	public void setEmsGroupName(String emsGroupName) {
		this.emsGroupName = emsGroupName;
	}

	public String getEmsGroupNote() {
		return emsGroupNote;
	}

	public void setEmsGroupNote(String emsGroupNote) {
		this.emsGroupNote = emsGroupNote;
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

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
}