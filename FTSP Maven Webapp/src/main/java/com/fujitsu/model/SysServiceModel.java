package com.fujitsu.model;

public class SysServiceModel {

	private Integer sysSvcRecordId;
	// IP地址
	private String ip;
	// 端口
	private String port;

	private String address;

	private String serviceName;

	private Integer status;

	private String note;

	public Integer getSysSvcRecordId() {
		return sysSvcRecordId;
	}

	public void setSysSvcRecordId(Integer sysSvcRecordId) {
		this.sysSvcRecordId = sysSvcRecordId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}