package com.fujitsu.manager.alarmConvergeManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmConvergeAction {
	
	private int actionType;
	private int actionStatus;
	private List<String> alarms;
	
	public AlarmConvergeAction(Map<String, Object> paramMap) {
		actionType = (Integer) paramMap.get("ACTION_TYPE");
		actionStatus = (Integer) paramMap.get("STATUS"); 
		if (paramMap.get("ALARMS") != null && !"".equals(paramMap.get("ALARMS"))) {
			String[] alms = paramMap.get("ALARMS").toString().split(",");
			List<String> almList = new ArrayList<String>();
			for (int i=0; i< alms.length; i++) {
				almList.add(alms[i]);
			}
			alarms = almList;
		}
	}
	
	public int getActionType() {
		return this.actionType;
	}
	public void setActionType(int value) {
		this.actionType = value;
	}
	
	public int getActionStatus(){
		return this.actionStatus;
	}
	public void setActionStatus(int value) {
		this.actionStatus = value;
	}
	
	public List<String> getAlarms() {
		return this.alarms;
	}
	public void setAlarms(List<String> value) {
		if (value != null) {
			this.alarms = value;
		} else {
			this.alarms = new ArrayList<String>();
		}
	}

}
