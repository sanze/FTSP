package com.fujitsu.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceInfoBean implements Serializable {
	private static final long serialVersionUID = 5222522450106397199L;
	public List<Map<String,Object>> CpuInfo;
	public HashMap<String, Double> Usage;
	public List<Map<String,Object>> DriveInfo;
	public List<Map<String,Object>> MemInfo;
	public Map<String,Object> NetInfo;
	public Map<String,Object> SysInfo;
	public List<String> ips;
	public List<String> getIps() {
		return ips;
	}
	public void setIps(List<String> ips) {
		this.ips = ips;
	}
	public List<Map<String, Object>> getCpuInfo() {
		return CpuInfo;
	}
	public void setCpuInfo(List<Map<String, Object>> cpuInfo) {
		CpuInfo = cpuInfo;
	}
	public HashMap<String, Double> getUsage() {
		return Usage;
	}
	public void setUsage(HashMap<String, Double> usage) {
		Usage = usage;
	}
	public List<Map<String, Object>> getDriveInfo() {
		return DriveInfo;
	}
	public void setDriveInfo(List<Map<String, Object>> driveInfo) {
		DriveInfo = driveInfo;
	}
	public List<Map<String, Object>> getMemInfo() {
		return MemInfo;
	}
	public void setMemInfo(List<Map<String, Object>> memInfo) {
		MemInfo = memInfo;
	}
	public Map<String, Object> getNetInfo() {
		return NetInfo;
	}
	public void setNetInfo(Map<String, Object> netInfo) {
		NetInfo = netInfo;
	}
	public Map<String, Object> getSysInfo() {
		return SysInfo;
	}
	public void setSysInfo(Map<String, Object> sysInfo) {
		SysInfo = sysInfo;
	}
	
	
}
