package com.fujitsu.manager.faultDiagnoseManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FaultDiagnoseAction {
	
	private int actionType;
	private int actionStatus;
	private List<String> checkPoint;
	
	public FaultDiagnoseAction(Map<String, Object> paramMap) {
		actionType = (Integer) paramMap.get("ACTION_TYPE");
		actionStatus = (Integer) paramMap.get("STATUS"); 
		if (paramMap.get("ACTION_TARGET") != null && !"".equals(paramMap.get("ACTION_TARGET"))) {
			String[] alms = paramMap.get("ACTION_TARGET").toString().split(",");
			List<String> almList = new ArrayList<String>();
			for (int i=0; i< alms.length; i++) {
				almList.add(alms[i]);
			}
			checkPoint = almList;
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
	
	public List<String> getCheckPoint() {
		return this.checkPoint;
	}
	public void setCheckPoint(List<String> value) {
		if (value != null) {
			this.checkPoint = value;
		} else {
			this.checkPoint = new ArrayList<String>();
		}
	}

}
