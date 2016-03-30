/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fujitsu.model;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author sse
 */
public class AlarmMessageModel implements Serializable {
    public static AlarmMessageModel from(Map table){
		// 定义告警消息体
		AlarmMessageModel alarm = new AlarmMessageModel();
		// 封装告警信息
		alarm.setEmsId(Integer.parseInt(table.get("EMS_ID").toString()));// 网管ID
		alarm.setSubnetId("".equals(table.get("SUBNET_ID").toString())?-1:Integer.parseInt(table.get("SUBNET_ID").toString()));// 子网ID
		alarm.setNeId("".equals(table.get("NE_ID").toString())?-1:Integer.parseInt(table.get("NE_ID").toString()));// 网元ID
		alarm.setType("".equals(table.get("NE_TYPE").toString())?-1:Integer.parseInt(table.get("NE_TYPE").toString()));// 网元类型(网元的设备类型)
		alarm.setRackNo(table.get("RACK_NO").toString());// 机架标识
		alarm.setShelfNo(table.get("SHELF_NO").toString());// 子架标识
		alarm.setSlotNo(table.get("SLOT_NO").toString());// 槽道标识
		alarm.setPortNo(table.get("PORT_NO").toString());// 端口标识
		alarm.setDomain("".equals(table.get("DOMAIN").toString())?-1:Integer.parseInt(table.get("DOMAIN").toString()));// 端口类型标识
		alarm.setAlarmName(table.get("NATIVE_PROBABLE_CAUSE").toString());// 告警名称
		alarm.setSeverity(Integer.parseInt(table.get("PERCEIVED_SEVERITY").toString()));// 告警级别
		alarm.setStatus((table.get("status")==null||table.get("status").toString().isEmpty())
				? 2 : Integer.parseInt(table.get("status").toString()));
		alarm.setObjectType("".equals(table.get("OBJECT_TYPE").toString())?-1:Integer.parseInt(table.get("OBJECT_TYPE").toString()));// 告警对象
		alarm.setPtpId("".equals(table.get("PTP_ID").toString()) ? -1 : (Integer)table.get("PTP_ID"));	// 端口ID
		alarm.setCtpId("".equals(table.get("CTP_ID").toString()) ? -1 : (Integer)table.get("CTP_ID"));  // 通道ID
		return alarm;
    }
	
    private int emsId;
    private int subnetId;
    private int neId;
    private int type;
    private String rackNo;
    private String shelfNo;
    private String slotNo;
    private String portNo;
    private int domain;
    private String alarmName;
    private int severity;
    private int status;
    private int objectType;
    private int ptpId;
    private int ctpId;
    
    public int getEmsId() {
        return emsId;
    }
    public void setEmsId(int emsId) {
        this.emsId = emsId;
    }
    public int getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(int subnetId) {
        this.subnetId = subnetId;
    }
    public int getNeId() {
        return neId;
    }
    public void setNeId(int neId) {
        this.neId = neId;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    
    public String getRackNo() {
		return rackNo;
	}
	public void setRackNo(String rackNo) {
		this.rackNo = rackNo;
	}
	public String getShelfNo() {
		return shelfNo;
	}
	public void setShelfNo(String shelfNo) {
		this.shelfNo = shelfNo;
	}
	public String getSlotNo() {
		return slotNo;
	}
	public void setSlotNo(String slotNo) {
		this.slotNo = slotNo;
	}
	public String getPortNo() {
		return portNo;
	}
	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}
	public int getDomain() {
        return domain;
    }
    public void setDomain(int domain) {
        this.domain = domain;
    }
    public String getAlarmName() {
        return alarmName;
    }
    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }
    public int getSeverity() {
        return severity;
    }
    public void setSeverity(int severity) {
        this.severity = severity;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getObjectType() {
    	return this.objectType;
    }
    public void setObjectType(int objectType) {
    	this.objectType = objectType;
    }
	public int getPtpId() {
		return ptpId;
	}
	public void setPtpId(int ptpId) {
		this.ptpId = ptpId;
	}
	public int getCtpId() {
		return ctpId;
	}
	public void setCtpId(int ctpId) {
		this.ctpId = ctpId;
	}
}
