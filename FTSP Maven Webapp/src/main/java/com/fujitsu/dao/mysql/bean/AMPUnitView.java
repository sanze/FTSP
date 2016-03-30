package com.fujitsu.dao.mysql.bean;

public class AMPUnitView extends ResourceUnitManageRelUnit {
	private static final long serialVersionUID = 2998588464578781795L;
	private String unit;
	private String unitDesc;
	private String R_PTP_NAME;
	private String T_PTP_NAME;
	private int rack;
	private int shelf;
	private int slot;
	private String modelName;

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

	public String getR_PTP_NAME() {
		return R_PTP_NAME;
	}

	public void setR_PTP_NAME(String r_PTP_NAME) {
		R_PTP_NAME = r_PTP_NAME;
	}

	public String getT_PTP_NAME() {
		return T_PTP_NAME;
	}

	public void setT_PTP_NAME(String t_PTP_NAME) {
		T_PTP_NAME = t_PTP_NAME;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

}
