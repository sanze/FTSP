package com.fujitsu.dao.mysql.bean;

import java.io.Serializable;
import java.sql.Date;

public class ResourceUnitManageRelUnit implements Serializable {
	private static final long serialVersionUID = -4584803810040843247L;
	
	private int RESOURCE_UNIT_MANAGE_REL_ID;// id
	private int RESOURCE_UNIT_MANAGE_ID;//
	private int PM_STD_OPT_AMP_ID;// 光放规格
	private int BASE_UNIT_ID;// 板卡id
	private int TYPE;// 1.光放大器2.光开关盘3.合波盘4.分波盘
	private String DIRECTION_LINK;// 环网链路方向
	private String OPTICAL_LEVEL;// 光放大器级数
	private String IN_OUT;// 收/发
	private String DIRECTION;// 方向
	private Integer STD_WAVE_NUM;// 容量
	private Integer ACTUAL_WAVE_NUM;// 开通波数/在用波数
	private String STATION;// 站点
	private String POWER_BUDGET;// 预算功率
	private String INSERTION_LOSS;// 插损
	private Integer SEQUENCE;// 顺序
	private int IS_DEL;// 是否删除 0：不是 1：是
	private Date CREATE_TIME;// 创建时间
	private Date UPDATE_TIME;// 更新时间
	private Integer T_PTP_ID;//发送端口号
	private Integer R_PTP_ID;//接受端口号
	public int getRESOURCE_UNIT_MANAGE_REL_ID() {
		return RESOURCE_UNIT_MANAGE_REL_ID;
	}

	public void setRESOURCE_UNIT_MANAGE_REL_ID(int rESOURCE_UNIT_MANAGE_REL_ID) {
		RESOURCE_UNIT_MANAGE_REL_ID = rESOURCE_UNIT_MANAGE_REL_ID;
	}

	public int getRESOURCE_UNIT_MANAGE_ID() {
		return RESOURCE_UNIT_MANAGE_ID;
	}

	public void setRESOURCE_UNIT_MANAGE_ID(int rESOURCE_UNIT_MANAGE_ID) {
		RESOURCE_UNIT_MANAGE_ID = rESOURCE_UNIT_MANAGE_ID;
	}

	public int getPM_STD_OPT_AMP_ID() {
		return PM_STD_OPT_AMP_ID;
	}

	public void setPM_STD_OPT_AMP_ID(int pM_STD_OPT_AMP_ID) {
		PM_STD_OPT_AMP_ID = pM_STD_OPT_AMP_ID;
	}

	public int getBASE_UNIT_ID() {
		return BASE_UNIT_ID;
	}

	public void setBASE_UNIT_ID(int bASE_UNIT_ID) {
		BASE_UNIT_ID = bASE_UNIT_ID;
	}

	public int getTYPE() {
		return TYPE;
	}

	public void setTYPE(int tYPE) {
		TYPE = tYPE;
	}

	public String getDIRECTION_LINK() {
		return DIRECTION_LINK;
	}

	public void setDIRECTION_LINK(String dIRECTION_LINK) {
		DIRECTION_LINK = dIRECTION_LINK;
	}

	public String getOPTICAL_LEVEL() {
		return OPTICAL_LEVEL;
	}

	public void setOPTICAL_LEVEL(String oPTICAL_LEVEL) {
		OPTICAL_LEVEL = oPTICAL_LEVEL;
	}

	public String getIN_OUT() {
		return IN_OUT;
	}

	public void setIN_OUT(String iN_OUT) {
		IN_OUT = iN_OUT;
	}

	public String getDIRECTION() {
		return DIRECTION;
	}

	public void setDIRECTION(String dIRECTION) {
		DIRECTION = dIRECTION;
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

	public String getSTATION() {
		return STATION;
	}

	public void setSTATION(String sTATION) {
		STATION = sTATION;
	}

	public String getPOWER_BUDGET() {
		return POWER_BUDGET;
	}

	public void setPOWER_BUDGET(String pOWER_BUDGET) {
		POWER_BUDGET = pOWER_BUDGET;
	}

	public String getINSERTION_LOSS() {
		return INSERTION_LOSS;
	}

	public void setINSERTION_LOSS(String iNSERTION_LOSS) {
		INSERTION_LOSS = iNSERTION_LOSS;
	}

	public Integer getSEQUENCE() {
		return SEQUENCE;
	}

	public void setSEQUENCE(Integer sEQUENCE) {
		SEQUENCE = sEQUENCE;
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

	public Integer getT_PTP_ID() {
		return T_PTP_ID;
	}

	public void setT_PTP_ID(Integer t_PTP_ID) {
		T_PTP_ID = t_PTP_ID;
	}

	public Integer getR_PTP_ID() {
		return R_PTP_ID;
	}

	public void setR_PTP_ID(Integer r_PTP_ID) {
		R_PTP_ID = r_PTP_ID;
	}
	
}
