package com.fujitsu.model;

import java.io.Serializable;


public class NeAlterModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9205684853022222069L;
	
	//1.新增 2.删除
	private int changeType;
	//网元名
	private String neName;
	
	public int getChangeType() {
		return changeType;
	}
	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
}
