package com.fujitsu.manager.equipmentTestManager.serviceImpl.model;

/**
 * @Description：光开关信息
 * @author cao senrong
 * @date 2015-1-4
 * @version V1.0
 */
public class RoutePointInfoModel {

	private String ctuIp;//CTU设备IP地址
	private int ctuPort;//CTU设备通信端口
	
	private String slot;//光路段OSW槽道号  
	private String port;//光路段OSW端口号 
	
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getCtuIp() {
		return ctuIp;
	}
	public void setCtuIp(String ctuIp) {
		this.ctuIp = ctuIp;
	}
	public int getCtuPort() {
		return ctuPort;
	}
	public void setCtuPort(int ctuPort) {
		this.ctuPort = ctuPort;
	}
	
}
