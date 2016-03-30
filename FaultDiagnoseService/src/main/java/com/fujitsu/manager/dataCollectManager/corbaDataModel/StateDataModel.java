package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

public class StateDataModel{
	public class State{
		public State(String name, Integer value){
			this.name=name;
			this.value=value;
		}
		private String name;
		private Integer value;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getValue() {
			return value;
		}
		public void setValue(Integer value) {
			this.value = value;
		}
	}
	private String notificationId;//中兴无
	private NameAndStringValue_T[] objectName;
	private int objectType;
	private String objectTypeQualifier;//朗讯无
	private String emsTime;
	private String neTime;//中兴无
	private boolean edgePointRelated;//中兴无
	private State[] state;
	
	private int emsId;
	
	public String getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}
	public NameAndStringValue_T[] getObjectName() {
		return objectName;
	}
	public void setObjectName(NameAndStringValue_T[] objectName) {
		this.objectName = objectName;
	}
	public int getObjectType() {
		return objectType;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public String getObjectTypeQualifier() {
		return objectTypeQualifier;
	}
	public void setObjectTypeQualifier(String objectTypeQualifier) {
		this.objectTypeQualifier = objectTypeQualifier;
	}
	public String getEmsTime() {
		return emsTime;
	}
	public void setEmsTime(String emsTime) {
		this.emsTime = emsTime;
	}
	public String getNeTime() {
		return neTime;
	}
	public void setNeTime(String neTime) {
		this.neTime = neTime;
	}
	public boolean isEdgePointRelated() {
		return edgePointRelated;
	}
	public void setEdgePointRelated(boolean edgePointRelated) {
		this.edgePointRelated = edgePointRelated;
	}
	public State[] getState() {
		return state;
	}
	public void setState(State[] state) {
		this.state = state;
	}
	public int getEmsId() {
		return emsId;
	}
	public void setEmsId(int emsId) {
		this.emsId = emsId;
	}

}
