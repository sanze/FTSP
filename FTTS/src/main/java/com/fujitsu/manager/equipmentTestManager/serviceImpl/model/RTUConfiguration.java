package com.fujitsu.manager.equipmentTestManager.serviceImpl.model;

/**
 * @Description：RTU配置信息
 * @author cao senrong
 * @date 2014-5-26
 * @version V1.0
 */
public class RTUConfiguration {

//	private String rip;//RTU IP 10字符
	private String tu;//子架号  2字符
	private String slot;//槽道号  2字符
//	private String type;//板卡类型  2字符  1：MCU 2：SLF 4：OTDR 5：OSW 6：OPM 7：PWR 9：OLS
//	private String port;//端口数 2字符
	private String madeDate;//生产日期 8字符
	private String sn;//序列号  10字符
	private String hardwareVersion;//硬件版本  4字符
	private String softwareVersion;//软件版本  4字符
	private String status;//状态  2字符 0：正常 1：告警 2：插错 3：拔出
	
	public String getTu() {
		return tu;
	}
	public void setTu(String tu) {
		this.tu = tu;
	}
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}
	public String getMadeDate() {
		return madeDate;
	}
	public void setMadeDate(String madeDate) {
		this.madeDate = madeDate;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getHardwareVersion() {
		return hardwareVersion;
	}
	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
