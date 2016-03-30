package com.fujitsu.model;

public class AlarmShieldModel {
	// 屏蔽器ID
	private int shieldId;
	// 屏蔽器名称
	private String shieldName;
	// 创建人ID
	private int sysUserId;
	// 创建人名称
	private String creator;
	// 屏蔽器状态
	private int status;
	// 屏蔽器描述
	private String description;
	// 创建时间
	private String createTime;
	// 更新时间
	private String updateTime;
	public int getShieldId() {
		return shieldId;
	}
	public void setShieldId(int shieldId) {
		this.shieldId = shieldId;
	}
	public String getShieldName() {
		return shieldName;
	}
	public void setShieldName(String shieldName) {
		this.shieldName = shieldName;
	}
	public int getSysUserId() {
		return sysUserId;
	}
	public void setSysUserId(int sysUserId) {
		this.sysUserId = sysUserId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
