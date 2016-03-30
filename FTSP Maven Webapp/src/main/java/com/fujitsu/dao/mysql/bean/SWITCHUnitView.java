package com.fujitsu.dao.mysql.bean;

public class SWITCHUnitView extends ResourceUnitManageRelUnit {

	private static final long serialVersionUID = -3014787062879027808L;
	private String unit;
	private String unitDesc;
	private int rack;
	private int shelf;
	private int slot;
	private String modelName;
	private String UNIT_INFO;
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

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getUNIT_INFO() {
		return UNIT_INFO;
	}

	public void setUNIT_INFO(String uNIT_INFO) {
		UNIT_INFO = uNIT_INFO;
	}
}
