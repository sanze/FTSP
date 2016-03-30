package com.fujitsu.manager.faultDiagnoseManager.model;

import java.util.List;
import java.util.Map;

import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.mongodb.DBObject;

public class MainAlarmAdjacentPort {
	private boolean hasAdjacentPort;
	private boolean hasAlarm;
	private boolean hasPm;
	private boolean hasOptStd;
	private List<DBObject> alarmData;
	private List<PmDataModel> pmData;
	private Map<String, Object> optStd;
	private Map<String, Object> adjacentPortInfo;
	
	public MainAlarmAdjacentPort() {
		hasAdjacentPort = false;
		hasAlarm = false;
		hasPm = false;
		hasOptStd = false;
		alarmData = null;
		pmData = null;
		optStd = null;
	}
	
	public boolean isHasAdjacentPort() {
		return hasAdjacentPort;
	}
	public void setAdjacentPort(boolean value) {
		this.hasAdjacentPort = value;
	}
	public boolean isHasAlarm() {
		return hasAlarm;
	}
	public boolean isHasPm() {
		return hasPm;
	}
	public List<DBObject> getAlarmData() {
		return alarmData;
	}
	public void setAlarmData(List<DBObject> value) {
		if (value != null) {
			this.hasAlarm = true;
			this.alarmData = value;
		} else {
			this.hasAlarm = false;
			this.alarmData = null;
		}
	}
	public List<PmDataModel> getPmData() {
		return pmData;
	}
	public void setPmData(List<PmDataModel> value) {
		if (value != null) {
			this.hasPm = true;
			this.pmData = value;
		} else {
			this.hasPm = false;
			this.pmData = null;
		}
	}

	public boolean isHasOptStd() {
		return hasOptStd;
	}

	public Map<String, Object> getOptStd() {
		return optStd;
	}

	public void setOptStd(Map<String, Object> value) {
		if (value != null) {
			this.hasOptStd = true;
			this.optStd = value;
		} else {
			this.hasOptStd = false;
			this.optStd = null;
		}
		
	}

	public Map<String, Object> getAdjacentPortInfo() {
		return adjacentPortInfo;
	}

	public void setAdjacentPortInfo(Map<String, Object> adjacentPortInfo) {
		this.adjacentPortInfo = adjacentPortInfo;
	}
}
