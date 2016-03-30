package com.fujitsu.manager.equipmentTestManager.serviceImpl.model;

/**
 * @Description：OTDR设备信息
 * @author cao senrong
 * @date 2015-1-4
 * @version V1.0
 */
public class EqptInfoModel {

	
	private String rtuIp;//RTU设备IP地址
	private int rtuPort;//RTU设备通信端口
	private String Rcode;//RTU设备编号
	private String factory;//RTU设备类型
	

	public String getRtuIp() {
		return rtuIp;
	}
	public void setRtuIp(String rtuIp) {
		this.rtuIp = rtuIp;
	}
	public int getRtuPort() {
		return rtuPort;
	}
	public void setRtuPort(int rtuPort) {
		this.rtuPort = rtuPort;
	}
	public String getRcode() {
		return Rcode;
	}
	public void setRcode(String rcode) {
		Rcode = rcode;
	}
	public String getFactory() {
		return factory;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}
	
}
