package com.fujitsu.model;

public class AlarmConvergeModel { 
	private int convergeId; 
	private String RULE_NAME; 
	private int STATUS; 
	private String DESCRIPTION;  
	private String MODIFIER; 
	private String UPDATE_TIME;
	
	public int getConvergeId() {
		return convergeId;
	}
	public void setConvergeId(int convergeId) {
		this.convergeId = convergeId;
	}
	public String getRULE_NAME() {
		return RULE_NAME;
	}
	public void setRULE_NAME(String rULE_NAME) {
		RULE_NAME = rULE_NAME;
	}
	public int getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(int sTATUS) {
		STATUS = sTATUS;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getMODIFIER() {
		return MODIFIER;
	}
	public void setMODIFIER(String mODIFIER) {
		MODIFIER = mODIFIER;
	}
	public String getUPDATE_TIME() {
		return UPDATE_TIME;
	}
	public void setUPDATE_TIME(String uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	} 
	
}
