package com.fujitsu.model;

import java.util.Date;

import net.sf.jasperreports.engine.JRDataSource;

public class InspectDetailModel extends InspectCommonModel {

	//所有异常项的list
	private JRDataSource LIST_DETAIL;

	public InspectDetailModel(String REPORT_NAME, Date REPORT_START_TIME,
			Date REPORT_END_TIME, String REPORT_DESCRIPTION, JRDataSource LIST_DETAIL) {
		super(REPORT_NAME,REPORT_START_TIME,REPORT_END_TIME,REPORT_DESCRIPTION);
		
		this.LIST_DETAIL = LIST_DETAIL;
	}

	public JRDataSource getLIST_DETAIL() {
		return LIST_DETAIL;
	}

	public void setLIST_EXCEPTION(JRDataSource LIST_DETAIL) {
		this.LIST_DETAIL = LIST_DETAIL;
	}
	
}
