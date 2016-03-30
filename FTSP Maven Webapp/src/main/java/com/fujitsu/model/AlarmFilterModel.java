package com.fujitsu.model;

public class AlarmFilterModel {
	// 过滤器ID
	private int filterId;
	// 过滤器名称
	private String filterName;
	// 创建人ID
	private int sysUserId;
	// 创建人名称
	private String creator;
	// 过滤器状态
	private int status;
	// 过滤器描述
	private String description;
	// 有效开始时间
	private String startTime;
	// 有效结束时间
	private String endTime;
	// 过滤器类型
	private int filterType;
	// 是否是通道告警
	private int ctpAlarmFlag;
	// 过滤器标签
	private int filterFlag;
	// 告警源选择标签
	private int alarmSourceFlag;
	// 创建时间
	private String createTime;
	// 更新时间
	private String updateTime;
	public int getFilterId() {
		return filterId;
	}
	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public int getSysUserId() {
		return sysUserId;
	}
	public void setSysUserId(int sysUserId) {
		this.sysUserId = sysUserId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public int getFilterType() {
		return filterType;
	}
	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}
	public int getCtpAlarmFlag() {
		return ctpAlarmFlag;
	}
	public void setCtpAlarmFlag(int ctpAlarmFlag) {
		this.ctpAlarmFlag = ctpAlarmFlag;
	}
	public int getFilterFlag() {
		return filterFlag;
	}
	public void setFilterFlag(int filterFlag) {
		this.filterFlag = filterFlag;
	}
	public int getAlarmSourceFlag() {
		return alarmSourceFlag;
	}
	public void setAlarmSourceFlag(int alarmSourceFlag) {
		this.alarmSourceFlag = alarmSourceFlag;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
