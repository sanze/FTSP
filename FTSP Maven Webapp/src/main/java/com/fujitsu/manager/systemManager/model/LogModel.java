package com.fujitsu.manager.systemManager.model;
import java.io.Serializable;
public class LogModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String userGroupId="";
	private String userGroupName="";
	private String userId="";
	private String userName="";
	private String startDate="";
	private String endDate="";
	private String logKeyword="";
	
	private String hiddenColoumms;
	public String getUserGroupId() {
		return userGroupId;
	}
	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}
	public String getUserGroupName() {
		return userGroupName;
	}
	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getLogKeyword() {
		return logKeyword;
	}
	public void setLogKeyword(String logKeyword) {
		this.logKeyword = logKeyword;
	}
	public String getHiddenColoumms() {
		return hiddenColoumms;
	}
	public void setHiddenColoumms(String hiddenColoumms) {
		this.hiddenColoumms = hiddenColoumms;
	}
	@Override
	public String toString() {
		return "LogModel [userGroupId=" + userGroupId + ", userGroupName="
				+ userGroupName + ", userId=" + userId + ", userName="
				+ userName + ", startDate=" + startDate + ", endDate="
				+ endDate + ", logKeyword=" + logKeyword + "]";
	}

	
}
