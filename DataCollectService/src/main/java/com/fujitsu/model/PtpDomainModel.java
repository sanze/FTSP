package com.fujitsu.model;

import java.util.List;

public class PtpDomainModel implements Comparable<PtpDomainModel>{
	
	private String domain;
	private int domainFlag;
	private List<Short> layerList;
	private String ptpType;
	private String rate;
	private int priority = 99;
	
	// 排序算法
	public int compareTo(PtpDomainModel model) {

		// 按命令等级及时间排序  等级大 排最前 -1表示倒序，+1表示正序,命令等级1为最大
		if (this.priority < model.getPriority()) {
			
			return -1;
			
		} else{
			return 1;
		}
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public int getDomainFlag() {
		return domainFlag;
	}
	public void setDomainFlag(int domainFlag) {
		this.domainFlag = domainFlag;
	}
	public List<Short> getLayerList() {
		return layerList;
	}
	public void setLayerList(List<Short> layerList) {
		this.layerList = layerList;
	}
	public String getPtpType() {
		return ptpType;
	}
	public void setPtpType(String ptpType) {
		this.ptpType = ptpType;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}

}
