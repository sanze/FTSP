package com.fujitsu.model;

import java.util.Date;

public class InspectCommonModel {
	private String REPORT_NAME;
	private Date REPORT_START_TIME;
	private Date REPORT_END_TIME;
	private String REPORT_DESCRIPTION;
	
	public InspectCommonModel(String REPORT_NAME, Date REPORT_START_TIME,
			Date REPORT_END_TIME, String REPORT_DESCRIPTION) {
		super();
		this.REPORT_NAME = REPORT_NAME;
		this.REPORT_START_TIME = REPORT_START_TIME;
		this.REPORT_END_TIME = REPORT_END_TIME;
		this.REPORT_DESCRIPTION = REPORT_DESCRIPTION;
	}
	
	public String getREPORT_NAME() {
		return REPORT_NAME;
	}
	public void setREPORT_NAME(String REPORT_NAME) {
		this.REPORT_NAME = REPORT_NAME;
	}
	public Date getREPORT_START_TIME() {
		return REPORT_START_TIME;
	}
	public void setREPORT_START_TIME(Date REPORT_START_TIME) {
		this.REPORT_START_TIME = REPORT_START_TIME;
	}
	public Date getREPORT_END_TIME() {
		return REPORT_END_TIME;
	}
	public void setREPORT_END_TIME(Date REPORT_END_TIME) {
		this.REPORT_END_TIME = REPORT_END_TIME;
	}
	public String getREPORT_DESCRIPTION() {
		return REPORT_DESCRIPTION;
	}
	public void setREPORT_DESCRIPTION(String REPORT_DESCRIPTION) {
		this.REPORT_DESCRIPTION = REPORT_DESCRIPTION;
	}
	
}
