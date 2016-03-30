package com.fujitsu.model;

import java.util.Date;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JRDataSource;

public class InspectSummaryModel extends InspectCommonModel {

	private Long REPORT_ITEM_CNT;
	private Long REPORT_ITEM_SUB_CNT;
	private Long REPORT_EQUIP_NE_CNT;
	private Long REPORT_EQUIP_NE_ERROR_CNT;
	private Long REPORT_ITEM_SUB_ERROR_CNT;
	private JRBeanCollectionDataSource LIST_SUM_NET;
	private JRBeanCollectionDataSource LIST_SUM_ITEM;
	private JRBeanCollectionDataSource LIST_SUM_PS;
	private JRBeanCollectionDataSource LIST_SUM_EQUIP;
	private JRBeanCollectionDataSource LIST_SUM_AREA;
	private JRBeanCollectionDataSource LIST_SUM_ENGINEER;
	private JRBeanCollectionDataSource LIST_SUM_PTP;
	private JRDataSource LIST_EXCEPTION;
	private Long REPORT_PTP_CNT;
	private Long REPORT_PTP_USE_CNT;
	private Long REPORT_CTP_CNT;
	private Long REPORT_CTP_USE_CNT;
	public InspectSummaryModel(String reportName, Date reportStartTime,
			Date reportEndTime, String reportDescription,
			Long report_item_cnt, Long report_item_sub_cnt,
			Long report_equip_ne_cnt, Long report_equip_ne_error_cnt,
			Long report_item_sub_error_cnt,
			JRBeanCollectionDataSource list_sum_net,
			JRBeanCollectionDataSource list_sum_item,
			JRBeanCollectionDataSource list_sum_ps,
			JRBeanCollectionDataSource list_sum_equip,
			JRBeanCollectionDataSource list_sum_area,
			JRBeanCollectionDataSource list_sum_engineer,
			JRDataSource list_exception,
			JRBeanCollectionDataSource list_sum_ptp, Long report_ptp_cnt,
			Long report_ptp_use_cnt, Long report_ctp_cnt,
			Long report_ctp_use_cnt) {
		super(reportName,reportStartTime,reportEndTime,reportDescription);
		REPORT_ITEM_CNT = report_item_cnt;
		REPORT_ITEM_SUB_CNT = report_item_sub_cnt;
		REPORT_EQUIP_NE_CNT = report_equip_ne_cnt;
		REPORT_EQUIP_NE_ERROR_CNT = report_equip_ne_error_cnt;
		REPORT_ITEM_SUB_ERROR_CNT = report_item_sub_error_cnt;
		LIST_SUM_NET = list_sum_net;
		LIST_SUM_ITEM = list_sum_item;
		LIST_SUM_PS = list_sum_ps;
		LIST_SUM_EQUIP = list_sum_equip;
		LIST_SUM_AREA = list_sum_area;
		LIST_SUM_ENGINEER = list_sum_engineer;
		LIST_EXCEPTION = list_exception;
		LIST_SUM_PTP = list_sum_ptp;
		REPORT_PTP_CNT = report_ptp_cnt;
		REPORT_PTP_USE_CNT = report_ptp_use_cnt;
		REPORT_CTP_CNT = report_ctp_cnt;
		REPORT_CTP_USE_CNT = report_ctp_use_cnt;
	}
	
	public Long getREPORT_ITEM_CNT() {
		return REPORT_ITEM_CNT;
	}
	public Long getREPORT_ITEM_SUB_CNT() {
		return REPORT_ITEM_SUB_CNT;
	}
	public Long getREPORT_EQUIP_NE_CNT() {
		return REPORT_EQUIP_NE_CNT;
	}
	public Long getREPORT_EQUIP_NE_ERROR_CNT() {
		return REPORT_EQUIP_NE_ERROR_CNT;
	}
	public Long getREPORT_ITEM_SUB_ERROR_CNT() {
		return REPORT_ITEM_SUB_ERROR_CNT;
	}
	public JRBeanCollectionDataSource getLIST_SUM_NET() {
		return LIST_SUM_NET;
	}
	public JRBeanCollectionDataSource getLIST_SUM_ITEM() {
		return LIST_SUM_ITEM;
	}
	public JRBeanCollectionDataSource getLIST_SUM_PS() {
		return LIST_SUM_PS;
	}
	public JRBeanCollectionDataSource getLIST_SUM_EQUIP() {
		return LIST_SUM_EQUIP;
	}
	public JRBeanCollectionDataSource getLIST_SUM_AREA() {
		return LIST_SUM_AREA;
	}
	public JRBeanCollectionDataSource getLIST_SUM_ENGINEER() {
		return LIST_SUM_ENGINEER;
	}
	public JRBeanCollectionDataSource getLIST_SUM_PTP() {
		return LIST_SUM_PTP;
	}
	public Long getREPORT_PTP_CNT() {
		return REPORT_PTP_CNT;
	}
	public Long getREPORT_PTP_USE_CNT() {
		return REPORT_PTP_USE_CNT;
	}
	public Long getREPORT_CTP_CNT() {
		return REPORT_CTP_CNT;
	}
	public Long getREPORT_CTP_USE_CNT() {
		return REPORT_CTP_USE_CNT;
	}
	public void setREPORT_ITEM_CNT(Long report_item_cnt) {
		REPORT_ITEM_CNT = report_item_cnt;
	}
	public void setREPORT_ITEM_SUB_CNT(Long report_item_sub_cnt) {
		REPORT_ITEM_SUB_CNT = report_item_sub_cnt;
	}
	public void setREPORT_EQUIP_NE_CNT(Long report_equip_ne_cnt) {
		REPORT_EQUIP_NE_CNT = report_equip_ne_cnt;
	}
	public void setREPORT_EQUIP_NE_ERROR_CNT(Long report_equip_ne_error_cnt) {
		REPORT_EQUIP_NE_ERROR_CNT = report_equip_ne_error_cnt;
	}
	public void setREPORT_ITEM_SUB_ERROR_CNT(Long report_item_sub_error_cnt) {
		REPORT_ITEM_SUB_ERROR_CNT = report_item_sub_error_cnt;
	}
	public void setLIST_SUM_NET(JRBeanCollectionDataSource list_sum_net) {
		LIST_SUM_NET = list_sum_net;
	}
	public void setLIST_SUM_ITEM(JRBeanCollectionDataSource list_sum_item) {
		LIST_SUM_ITEM = list_sum_item;
	}
	public void setLIST_SUM_PS(JRBeanCollectionDataSource list_sum_ps) {
		LIST_SUM_PS = list_sum_ps;
	}
	public void setLIST_SUM_EQUIP(JRBeanCollectionDataSource list_sum_equip) {
		LIST_SUM_EQUIP = list_sum_equip;
	}
	public void setLIST_SUM_AREA(JRBeanCollectionDataSource list_sum_area) {
		LIST_SUM_AREA = list_sum_area;
	}
	public void setLIST_SUM_ENGINEER(JRBeanCollectionDataSource list_sum_engineer) {
		LIST_SUM_ENGINEER = list_sum_engineer;
	}
	public void setLIST_SUM_PTP(JRBeanCollectionDataSource list_sum_ptp) {
		LIST_SUM_PTP = list_sum_ptp;
	}
	public void setREPORT_PTP_CNT(Long report_ptp_cnt) {
		REPORT_PTP_CNT = report_ptp_cnt;
	}
	public void setREPORT_PTP_USE_CNT(Long report_ptp_use_cnt) {
		REPORT_PTP_USE_CNT = report_ptp_use_cnt;
	}
	public void setREPORT_CTP_CNT(Long report_ctp_cnt) {
		REPORT_CTP_CNT = report_ctp_cnt;
	}
	public void setREPORT_CTP_USE_CNT(Long report_ctp_use_cnt) {
		REPORT_CTP_USE_CNT = report_ctp_use_cnt;
	}
	public JRDataSource getLIST_EXCEPTION() {
		return LIST_EXCEPTION;
	}
	public void setLIST_EXCEPTION(JRDataSource lIST_EXCEPTION) {
		LIST_EXCEPTION = lIST_EXCEPTION;
	}

}
