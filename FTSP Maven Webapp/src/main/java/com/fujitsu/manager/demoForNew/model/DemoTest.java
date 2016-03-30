package com.fujitsu.manager.demoForNew.model;

import java.io.Serializable;
/**
 * DemoTest模型
 * hg
 * 2013.12
 */
public class DemoTest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1879701681780855144L;
	
	private String id = "";
	private String name;
	private String address;
	private String ip;
	private String phone;
	private String note;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
