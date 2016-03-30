package com.fujitsu.model;

import java.util.Date;

public class EMSConnectionTaskModel {

	private Integer sysTaskId;

	private String tsakCycle; // 任务周期
	private Date endTime;
	private String taskStatus; // 任务状态 启用或挂起
	private Date nextTime;

	private String taskRunStatus; // 执行状态

	private String emsDisplayName;

	private String groupName; // 分组地名

	public Integer getSysTaskId() {
		return sysTaskId;
	}

	public void setSysTaskId(Integer sysTaskId) {
		this.sysTaskId = sysTaskId;
	}

	public String getTsakCycle() {
		return tsakCycle;
	}

	public void setTsakCycle(String tsakCycle) {
		this.tsakCycle = tsakCycle;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getNextTime() {
		return nextTime;
	}

	public void setNextTime(Date nextTime) {
		this.nextTime = nextTime;
	}

	public String getTaskRunStatus() {
		return taskRunStatus;
	}

	public void setTaskRunStatus(String taskRunStatus) {
		this.taskRunStatus = taskRunStatus;
	}

	public String getEmsDisplayName() {
		return emsDisplayName;
	}

	public void setEmsDisplayName(String emsDisplayName) {
		this.emsDisplayName = emsDisplayName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
