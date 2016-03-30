package com.fujitsu.manager.faultManager.model;

import net.sf.json.JSONObject;

public class FaultQueryCondition {
	//数据源：1：自动、2：人工、0：全部
	private int ds;
	//传输系统：0：全部
	private int tranformSystemId;
	private String startDate;
	private String endDate;
	private int errorCode;
	private int start;
	//默认分页大小为200
	private int limit = 200;
	private int count;
	
	public FaultQueryCondition(){}
	public FaultQueryCondition(int ds, int tranformSystemId){
		this(ds,tranformSystemId,null,null);
	}
	public FaultQueryCondition(int ds, int tranformSystemId, String startTime, String endTime){
		this.ds = ds;
		this.tranformSystemId = tranformSystemId;
		this.startDate = startTime;
		this.endDate = endTime;
	}
	public FaultQueryCondition(String jsonString){
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		this.ds = jsonObject.getInt("dataSource");
		if(jsonObject.get("startTime")!=null&&!jsonObject.get("startTime").toString().equals("")){
			this.startDate = jsonObject.getString("startTime");
		}
		if(jsonObject.get("endTime")!=null&&!jsonObject.get("endTime").toString().equals("")){
			this.endDate = jsonObject.getString("endTime");
		}
		if(jsonObject.getString("tranformSystemId").equals("全部")){
			this.tranformSystemId = 0;
		}else{
			this.tranformSystemId = jsonObject.getInt("tranformSystemId");
		}
	}
	public int getDs() {
		return ds;
	}
	public void setDs(int ds) {
		this.ds = ds;
	}
	public int getTranformSystemId() {
		return tranformSystemId;
	}
	public void setTranformSystemId(int tranformSystemId) {
		this.tranformSystemId = tranformSystemId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
