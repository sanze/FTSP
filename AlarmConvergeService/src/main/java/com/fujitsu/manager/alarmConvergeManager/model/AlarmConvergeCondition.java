package com.fujitsu.manager.alarmConvergeManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmConvergeCondition {

	private String alarmName;
	private int alarmObjectType;
	private List<String> objectLevel = new ArrayList<String>();
	
	public AlarmConvergeCondition(Map<String, Object> paramMap) {
		alarmName = paramMap.get("ALARM_NAME").toString();
		alarmObjectType = (Integer) paramMap.get("OBJECT_TYPE");
		if (paramMap.get("LEVEL")!=null && !"".equals(paramMap.get("LEVEL"))) {
			List<String> levelList = new ArrayList<String>();
			String [] levels = paramMap.get("LEVEL").toString().split(",");
			for (int i = 0; i < levels.length; i++) {
				levelList.add(levels[i]);
			}
			objectLevel = levelList;
		}
	}
	
	public String getAlarmName() {
		return this.alarmName;
	}
	public void setAlarmName(String value) {
		this.alarmName = value;
	}
	
	public int getAlarmObjectType() {
		return this.alarmObjectType;
	}
	public void setAlarmObjectType(int value) {
		this.alarmObjectType = value;
	}
	
	public List<String> getObjectLevel() {
		return this.objectLevel;
	}
	public void setObjectLevel(List<String> value) {
		if (value != null) {
			this.objectLevel = value;
		} else {
			this.objectLevel = new ArrayList<String>();
		}
	}
}
