package com.fujitsu.model;
import java.util.List;
public class FilterAlarmParametersModel {
	//割接任务id
	private int cutoverTaskId;
	//开始过滤时间
	private String startTime;	
	//结束过滤时间
	private String endTime;
	//网元id列表
	private List<Integer> neIdList;
	public int getCutoverTaskId() {
		return cutoverTaskId;
	}
	public void setCutoverTaskId(int cutoverTaskId) {
		this.cutoverTaskId = cutoverTaskId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public List<Integer> getNeIdList() {
		return neIdList;
	}
	public void setNeIdList(List<Integer> neIdList) {
		this.neIdList = neIdList;
	}
	
}
