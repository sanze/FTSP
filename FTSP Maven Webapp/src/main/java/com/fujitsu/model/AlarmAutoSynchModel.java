package com.fujitsu.model;

public class AlarmAutoSynchModel {

	//自增ID
	private int ID;
	//网管ID
	private int BASE_EMS_CONNECTION_ID;
	//同步周期
	private int SYNCHRONIZATION_CIRCLE;
	//任务状态
	private String TASK_STATUS;
	//执行状态
	private String EXECUTE_STATUS;
	//最近一次同步时间
	private String LATEST_SYNCHRONIZATION_TIME;
	//南向连接恢复后是否自动同步
	private int SYNCHRONIZATION_FLAG;
	//延时
	private int DELAY_TIME;
	//创建时间
	private String CREATE_TIME;
	//更新时间
	private String UPDATE_TIME;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getBASE_EMS_CONNECTION_ID() {
		return BASE_EMS_CONNECTION_ID;
	}
	public void setBASE_EMS_CONNECTION_ID(int bASE_EMS_CONNECTION_ID) {
		BASE_EMS_CONNECTION_ID = bASE_EMS_CONNECTION_ID;
	}
	public int getSYNCHRONIZATION_CIRCLE() {
		return SYNCHRONIZATION_CIRCLE;
	}
	public void setSYNCHRONIZATION_CIRCLE(int sYNCHRONIZATION_CIRCLE) {
		SYNCHRONIZATION_CIRCLE = sYNCHRONIZATION_CIRCLE;
	}
	public String getTASK_STATUS() {
		return TASK_STATUS;
	}
	public void setTASK_STATUS(String tASK_STATUS) {
		TASK_STATUS = tASK_STATUS;
	}
	public String getEXECUTE_STATUS() {
		return EXECUTE_STATUS;
	}
	public void setEXECUTE_STATUS(String eXECUTE_STATUS) {
		EXECUTE_STATUS = eXECUTE_STATUS;
	}
	public String getLATEST_SYNCHRONIZATION_TIME() {
		return LATEST_SYNCHRONIZATION_TIME;
	}
	public void setLATEST_SYNCHRONIZATION_TIME(String lATEST_SYNCHRONIZATION_TIME) {
		LATEST_SYNCHRONIZATION_TIME = lATEST_SYNCHRONIZATION_TIME;
	}
	public int getSYNCHRONIZATION_FLAG() {
		return SYNCHRONIZATION_FLAG;
	}
	public void setSYNCHRONIZATION_FLAG(int sYNCHRONIZATION_FLAG) {
		SYNCHRONIZATION_FLAG = sYNCHRONIZATION_FLAG;
	}
	public int getDELAY_TIME() {
		return DELAY_TIME;
	}
	public void setDELAY_TIME(int dELAY_TIME) {
		DELAY_TIME = dELAY_TIME;
	}
	public String getCREATE_TIME() {
		return CREATE_TIME;
	}
	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
	public String getUPDATE_TIME() {
		return UPDATE_TIME;
	}
	public void setUPDATE_TIME(String uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	}
	
	
}
