package com.fujitsu.model;

public class InspectSummaryItemModel {
	private String displayName;
	private Integer neCnt;
	private Integer neErrorCnt;
	
	public InspectSummaryItemModel(String displayName, Integer neCnt,
			Integer neErrorCnt) {
		super();
		this.displayName = displayName;
		this.neCnt = neCnt;
		this.neErrorCnt = neErrorCnt;
	}
	public String getDisplayName() {
		return displayName;
	}
	public Integer getNeCnt() {
		return neCnt;
	}
	public Integer getNeErrorCnt() {
		return neErrorCnt;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setNeCnt(Integer neCnt) {
		this.neCnt = neCnt;
	}
	public void setNeErrorCnt(Integer neErrorCnt) {
		this.neErrorCnt = neErrorCnt;
	}

}
