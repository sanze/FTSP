package com.fujitsu.dao.mysql.bean;

public class AMPManageView extends ResourceUnitManager{
	private static final long serialVersionUID = 7885546279161805977L;
	
	private String EMSGROUP_DISPLAY_NAME;
	private String EMS_DISPLAY_NAME;
	private String SUBNET_DISPLAY_NAME;
	private String NE_DISPLAY_NAME;
	public String getEMSGROUP_DISPLAY_NAME() {
		return EMSGROUP_DISPLAY_NAME;
	}
	public void setEMSGROUP_DISPLAY_NAME(String eMSGROUP_DISPLAY_NAME) {
		EMSGROUP_DISPLAY_NAME = eMSGROUP_DISPLAY_NAME;
	}
	public String getEMS_DISPLAY_NAME() {
		return EMS_DISPLAY_NAME;
	}
	public void setEMS_DISPLAY_NAME(String eMS_DISPLAY_NAME) {
		EMS_DISPLAY_NAME = eMS_DISPLAY_NAME;
	}
	public String getSUBNET_DISPLAY_NAME() {
		return SUBNET_DISPLAY_NAME;
	}
	public void setSUBNET_DISPLAY_NAME(String sUBNET_DISPLAY_NAME) {
		SUBNET_DISPLAY_NAME = sUBNET_DISPLAY_NAME;
	}
	public String getNE_DISPLAY_NAME() {
		return NE_DISPLAY_NAME;
	}
	public void setNE_DISPLAY_NAME(String nE_DISPLAY_NAME) {
		NE_DISPLAY_NAME = nE_DISPLAY_NAME;
	}
}
