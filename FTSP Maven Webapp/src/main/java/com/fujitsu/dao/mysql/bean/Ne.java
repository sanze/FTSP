package com.fujitsu.dao.mysql.bean;

import java.io.Serializable;

public class Ne implements Serializable{
	private static final long serialVersionUID = 2051642129448408683L;
	private String emsName;
	private String emsGroupName;
	private String subNetName;
	private int BASE_NE_ID;
	private String DISPLAY_NAME;
	private String PRODUCT_NAME;
	private int FACTORY;
	public String getEmsName() {
		return emsName;
	}
	public void setEmsName(String emsName) {
		this.emsName = emsName;
	}
	public String getEmsGroupName() {
		return emsGroupName;
	}
	public void setEmsGroupName(String emsGroupName) {
		this.emsGroupName = emsGroupName;
	}
	public String getSubNetName() {
		return subNetName;
	}
	public void setSubNetName(String subNetName) {
		this.subNetName = subNetName;
	}
	public int getBASE_NE_ID() {
		return BASE_NE_ID;
	}
	public void setBASE_NE_ID(int bASE_NE_ID) {
		BASE_NE_ID = bASE_NE_ID;
	}
	public String getDISPLAY_NAME() {
		return DISPLAY_NAME;
	}
	public void setDISPLAY_NAME(String dISPLAY_NAME) {
		DISPLAY_NAME = dISPLAY_NAME;
	}
	public String getPRODUCT_NAME() {
		return PRODUCT_NAME;
	}
	public void setPRODUCT_NAME(String pRODUCT_NAME) {
		PRODUCT_NAME = pRODUCT_NAME;
	}
	public int getFACTORY() {
		return FACTORY;
	}
	public void setFACTORY(int fACTORY) {
		FACTORY = fACTORY;
	}
}
