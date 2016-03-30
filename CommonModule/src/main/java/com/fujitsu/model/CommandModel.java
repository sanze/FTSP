package com.fujitsu.model;

import java.util.Date;

public class CommandModel implements Comparable<CommandModel> {

	private String name;
	private int collectType;
	private int commandLevel;
	private Date receiveTime;

	public CommandModel(String name,int collectType, int commandLevel,
			Date receiveTime) {
		this.name = name;
		this.collectType = collectType;
		this.commandLevel = commandLevel;
		this.receiveTime = receiveTime;
	}

	// 排序算法
	public int compareTo(CommandModel model) {

		// 按命令等级及时间排序  等级大 时间早 排最前 -1表示倒序，+1表示正序,命令等级1为最大
		if (this.getCommandLevel() < model.getCommandLevel()) {
			
			return -1;
			
		} else if(this.getCommandLevel() == model.getCommandLevel()){
			
			if(this.getReceiveTime().compareTo(model.getReceiveTime())<0){
				return -1;
			}else{
				return 1;
			}
		} else{
			
			return 1;
		}
	}

	public int getCommandLevel() {
		return commandLevel;
	}

	public void setCommandLevel(int commandLevel) {
		this.commandLevel = commandLevel;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public int getCollectType() {
		return collectType;
	}

	public void setCollectType(int collectType) {
		this.collectType = collectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
