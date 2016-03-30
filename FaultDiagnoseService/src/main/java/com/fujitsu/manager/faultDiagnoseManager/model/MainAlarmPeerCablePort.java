package com.fujitsu.manager.faultDiagnoseManager.model;

import java.util.List;

import com.mongodb.DBObject;

public class MainAlarmPeerCablePort {
	private boolean hasAlarm;
	private List<DBObject> alarmData;
	private boolean hasPeerCableFault;
	private Integer peerCableFaultId;
	
	public MainAlarmPeerCablePort() {
		hasAlarm = false;
		alarmData = null;
		setPeerCableFaultId(null);
	}
	
	public boolean isHasAlarm() {
		return this.hasAlarm;
	}
	public List<DBObject> getAlarmData() {
		return this.alarmData;
	}
	public void setAlarmData(List<DBObject> value) {
		if (value != null) {
			hasAlarm = true;
			alarmData = value;
		} else {
			hasAlarm = false;
			alarmData = null;
		}
	}

	public boolean isHasPeerCableFault() {
		return hasPeerCableFault;
	}

	public Integer getPeerCableFaultId() {
		return peerCableFaultId;
	}

	public void setPeerCableFaultId(Integer value) {
		if (value != null) {
			this.peerCableFaultId = value;
			this.hasPeerCableFault = true;
		} else {
			this.peerCableFaultId = null;
			this.hasPeerCableFault = false;
		}
	}
}
