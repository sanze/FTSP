package com.fujitsu.model;

import java.util.List;

public class UserModel {
	
	private String sysUserId;
	private String userName;
	private String loginName;
	private String jobNumber;
	private String timeout;
	private String telephone;
	private String email;
	private String remark;
	private String department;
	private String position;
	private String [] deviceDomain;
	private String [] authDomain;
	
	private String userGroupId;//用户组
	private List<String> sysUserIdList;
	private List<String> authDomainList;
	private List<String> deviceDomainList;
	private String _dc;
	
	private String jsonString;
	
	private String note;
	
	private String oldPass;
	private String newPass;
	private String passVal;//密码有效期
	
	
	
	public String getPassVal() {
		return passVal;
	}
	public void setPassVal(String passVal) {
		this.passVal = passVal;
	}
	public String getOldPass() {
		return oldPass;
	}
	public void setOldPass(String oldPass) {
		this.oldPass = oldPass;
	}
	public String getNewPass() {
		return newPass;
	}
	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getUserGroupId() {
		return userGroupId;
	}
	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}
	public List<String> getSysUserIdList() {
		return sysUserIdList;
	}
	public void setSysUserIdList(List<String> sysUserIdList) {
		this.sysUserIdList = sysUserIdList;
	}
	public List<String> getAuthDomainList() {
		return authDomainList;
	}
	public void setAuthDomainList(List<String> authDomainList) {
		this.authDomainList = authDomainList;
	}
	public List<String> getDeviceDomainList() {
		return deviceDomainList;
	}
	public void setDeviceDomainList(List<String> deviceDomainList) {
		this.deviceDomainList = deviceDomainList;
	}
	public String get_dc() {
		return _dc;
	}
	public void set_dc(String _dc) {
		this._dc = _dc;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public String getSysUserId() {
		return sysUserId;
	}
	public void setSysUserId(String sysUserId) {
		this.sysUserId = sysUserId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getJobNumber() {
		return jobNumber;
	}
	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String[] getDeviceDomain() {
		return deviceDomain;
	}
	public void setDeviceDomain(String[] deviceDomain) {
		this.deviceDomain = deviceDomain;
	}
	public String[] getAuthDomain() {
		return authDomain;
	}
	public void setAuthDomain(String[] authDomain) {
		this.authDomain = authDomain;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	
	
	
	
	

}
