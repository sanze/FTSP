package com.fujitsu.dao.mysql.bean;

import java.io.Serializable;
//创建联系人
public class Contact implements Serializable{
	private static final long serialVersionUID = -8922669892400275321L;
	//记录Id
	private Integer id;
	//联系人姓名
	private String name;
	//电话
	private String tel;
	//部门
	private String department;
	//工号
	private String staffNo;
	//邮箱
	private String email;
	//备注
	private String note;
	//大客户名称
	private String clientName;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getStaffNo() {
		return staffNo;
	}
	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
}
