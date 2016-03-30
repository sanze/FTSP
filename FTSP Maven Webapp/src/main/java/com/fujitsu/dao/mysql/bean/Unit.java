package com.fujitsu.dao.mysql.bean;

import java.io.Serializable;

public class Unit implements Serializable{
	private static final long serialVersionUID = -2485801200563885096L;
	private int BASE_UNIT_ID;
	private int RACK_NO;
	private int SHELF_NO;
	private int SLOT_NO;
	private String UNIT_DESC;
	public int getBASE_UNIT_ID() {
		return BASE_UNIT_ID;
	}
	public void setBASE_UNIT_ID(int bASE_UNIT_ID) {
		BASE_UNIT_ID = bASE_UNIT_ID;
	}
	public int getRACK_NO() {
		return RACK_NO;
	}
	public void setRACK_NO(int rACK_NO) {
		RACK_NO = rACK_NO;
	}
	public int getSHELF_NO() {
		return SHELF_NO;
	}
	public void setSHELF_NO(int sHELF_NO) {
		SHELF_NO = sHELF_NO;
	}
	public int getSLOT_NO() {
		return SLOT_NO;
	}
	public void setSLOT_NO(int sLOT_NO) {
		SLOT_NO = sLOT_NO;
	}
	public String getUNIT_DESC() {
		return UNIT_DESC;
	}
	public void setUNIT_DESC(String uNIT_DESC) {
		UNIT_DESC = uNIT_DESC;
	}
	
}
