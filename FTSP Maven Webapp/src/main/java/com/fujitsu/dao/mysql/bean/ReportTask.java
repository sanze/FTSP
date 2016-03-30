package com.fujitsu.dao.mysql.bean;

import java.io.Serializable;

public class ReportTask implements Serializable{
	private static final long serialVersionUID = 5717763228450277598L;
	private ResourceUnitManager manager;
	private int SYS_TASK_INFO_ID;//
	private int SYS_TASK_ID;// 任务id
	// 任务对象类型
	// 1.EMSGROUP2.EMS3.SUBNET4.NE5.Shelf6.unit
	//7.ptp8.SDH-CTP9.OTN-CTP10.干线11.复用段
	private int TARGET_TYPE;
	private String TARGET_NAME;// 对象名称
	private int TARGET_ID;// id
	private int IS_SUCCESS;// 成功标识 0.不成功 1.成功
	private int IS_COMPLETE;// 采集是否完成 0.未完成 1.完成
	private String TASK_NAME;
	private int TASK_TYPE;
	private String TASK_DESCRIPTION;
	public String getTASK_NAME() {
		return TASK_NAME;
	}
	public void setTASK_NAME(String tASK_NAME) {
		TASK_NAME = tASK_NAME;
	}
	public int getTASK_TYPE() {
		return TASK_TYPE;
	}
	public void setTASK_TYPE(int tASK_TYPE) {
		TASK_TYPE = tASK_TYPE;
	}
	public String getTASK_DESCRIPTION() {
		return TASK_DESCRIPTION;
	}
	public ResourceUnitManager getManager() {
		return manager;
	}
	public void setManager(ResourceUnitManager manager) {
		this.manager = manager;
	}
	public int getSYS_TASK_INFO_ID() {
		return SYS_TASK_INFO_ID;
	}
	public void setSYS_TASK_INFO_ID(int sYS_TASK_INFO_ID) {
		SYS_TASK_INFO_ID = sYS_TASK_INFO_ID;
	}
	public int getSYS_TASK_ID() {
		return SYS_TASK_ID;
	}
	public void setSYS_TASK_ID(int sYS_TASK_ID) {
		SYS_TASK_ID = sYS_TASK_ID;
	}
	public int getTARGET_TYPE() {
		return TARGET_TYPE;
	}
	public void setTARGET_TYPE(int tARGET_TYPE) {
		TARGET_TYPE = tARGET_TYPE;
	}
	public String getTARGET_NAME() {
		return TARGET_NAME;
	}
	public void setTARGET_NAME(String tARGET_NAME) {
		TARGET_NAME = tARGET_NAME;
	}
	public int getTARGET_ID() {
		return TARGET_ID;
	}
	public void setTARGET_ID(int tARGET_ID) {
		TARGET_ID = tARGET_ID;
	}
	public int getIS_SUCCESS() {
		return IS_SUCCESS;
	}
	public void setIS_SUCCESS(int iS_SUCCESS) {
		IS_SUCCESS = iS_SUCCESS;
	}
	public int getIS_COMPLETE() {
		return IS_COMPLETE;
	}
	public void setIS_COMPLETE(int iS_COMPLETE) {
		IS_COMPLETE = iS_COMPLETE;
	}
	
}
