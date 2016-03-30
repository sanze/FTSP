package com.fujitsu.dao.mysql.bean;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class ResourceUnitManager implements Serializable{
	private static final long serialVersionUID = 3334833736994144485L;

	// 一对多
	private List<ResourceUnitManageRelUnit> units;
	
	
	private int RESOURCE_UNIT_MANAGE_ID;
	private int BASE_NE_ID;
	private int TYPE;//1光放大器2光开关盘3合波盘4分波盘
	private String DEPARTMENT;//单位
	private String DIRECTION;//波分方向
	private String STATION;// 站名
	private String NET_WORK_NAME;// 网络名称
	private String PRODUCT_NAME;// 设备型号
	private Integer STD_WAVE_NUM;//容量
	private Integer ACTUAL_WAVE_NUM;//实开波道数
	private Integer FACTORY;//厂家
	private int IS_DEL;// 是否删除 0：不是 1：是
	private Date CREATE_TIME;//创建时间
	private Date UPDATE_TIME;//更新时间
	public List<ResourceUnitManageRelUnit> getUnits() {
		return units;
	}
	public void setUnits(List<ResourceUnitManageRelUnit> units) {
		this.units = units;
	}
	public int getRESOURCE_UNIT_MANAGE_ID() {
		return RESOURCE_UNIT_MANAGE_ID;
	}
	public void setRESOURCE_UNIT_MANAGE_ID(int rESOURCE_UNIT_MANAGE_ID) {
		RESOURCE_UNIT_MANAGE_ID = rESOURCE_UNIT_MANAGE_ID;
	}
	public int getBASE_NE_ID() {
		return BASE_NE_ID;
	}
	public void setBASE_NE_ID(int bASE_NE_ID) {
		BASE_NE_ID = bASE_NE_ID;
	}
	public int getTYPE() {
		return TYPE;
	}
	public void setTYPE(int tYPE) {
		TYPE = tYPE;
	}
	public String getDEPARTMENT() {
		return DEPARTMENT;
	}
	public void setDEPARTMENT(String dEPARTMENT) {
		DEPARTMENT = dEPARTMENT;
	}
	public String getDIRECTION() {
		return DIRECTION;
	}
	public void setDIRECTION(String dIRECTION) {
		DIRECTION = dIRECTION;
	}
	public String getSTATION() {
		return STATION;
	}
	public void setSTATION(String sTATION) {
		STATION = sTATION;
	}
	public String getNET_WORK_NAME() {
		return NET_WORK_NAME;
	}
	public void setNET_WORK_NAME(String nET_WORK_NAME) {
		NET_WORK_NAME = nET_WORK_NAME;
	}
	public String getPRODUCT_NAME() {
		return PRODUCT_NAME;
	}
	public void setPRODUCT_NAME(String pRODUCT_NAME) {
		PRODUCT_NAME = pRODUCT_NAME;
	}
	public Integer getSTD_WAVE_NUM() {
		return STD_WAVE_NUM;
	}
	public void setSTD_WAVE_NUM(Integer sTD_WAVE_NUM) {
		STD_WAVE_NUM = sTD_WAVE_NUM;
	}
	public Integer getACTUAL_WAVE_NUM() {
		return ACTUAL_WAVE_NUM;
	}
	public void setACTUAL_WAVE_NUM(Integer aCTUAL_WAVE_NUM) {
		ACTUAL_WAVE_NUM = aCTUAL_WAVE_NUM;
	}
	
	public int getIS_DEL() {
		return IS_DEL;
	}
	public void setIS_DEL(int iS_DEL) {
		IS_DEL = iS_DEL;
	}
	public Date getCREATE_TIME() {
		return CREATE_TIME;
	}
	public void setCREATE_TIME(Date cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
	public Date getUPDATE_TIME() {
		return UPDATE_TIME;
	}
	public void setUPDATE_TIME(Date uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	}
	public Integer getFACTORY() {
		return FACTORY;
	}
	public void setFACTORY(Integer fACTORY) {
		FACTORY = fACTORY;
	}
}
