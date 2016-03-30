package com.fujitsu.model;

import java.util.Date;
import java.util.List;

public class UserGroupModel {
	
	private String groupName;
	private String note;
	private Integer isDel;
	private Date createTime;
	private Integer sysUserId;
	private Integer sysUserGroupId;
	
	private List<Integer> sysUserGroupIdList;
	private List<Integer> userList;
	private String jsonString;
	
	private String saveType;
	
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public List<Integer> getSysUserGroupIdList() {
		return sysUserGroupIdList;
	}
	public void setSysUserGroupIdList(List<Integer> sysUserGroupIdList) {
		this.sysUserGroupIdList = sysUserGroupIdList;
	}
	public List<Integer> getUserList() {
		return userList;
	}
	public void setUserList(List<Integer> userList) {
		this.userList = userList;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getSysUserId() {
		return sysUserId;
	}
	public void setSysUserId(Integer sysUserId) {
		this.sysUserId = sysUserId;
	}
	public Integer getSysUserGroupId() {
		return sysUserGroupId;
	}
	public void setSysUserGroupId(Integer sysUserGroupId) {
		this.sysUserGroupId = sysUserGroupId;
	}
	
	
	

}
