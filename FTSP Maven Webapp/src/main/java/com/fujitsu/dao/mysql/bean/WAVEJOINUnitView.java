package com.fujitsu.dao.mysql.bean;

public class WAVEJOINUnitView extends ResourceUnitManageRelUnit{
	private static final long serialVersionUID = -7154493901034775088L;
	private String EMSGROUP_DISPLAY_NAME;
	private String EMS_DISPLAY_NAME;
	private String SUBNET_DISPLAY_NAME;
	private String NE_DISPLAY_NAME;
	private String unit;
	private String unitDesc;
	private int rack;
	private int shelf;
	private String T_PTP_NAME;
	private int slot;
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitDesc() {
		return unitDesc;
	}

	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}

	public int getRack() {
		return rack;
	}

	public void setRack(int rack) {
		this.rack = rack;
	}

	public int getShelf() {
		return shelf;
	}

	public void setShelf(int shelf) {
		this.shelf = shelf;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
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

	public String getT_PTP_NAME() {
		return T_PTP_NAME;
	}

	public void setT_PTP_NAME(String t_PTP_NAME) {
		T_PTP_NAME = t_PTP_NAME;
	}
}
