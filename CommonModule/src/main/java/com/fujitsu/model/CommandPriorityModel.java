package com.fujitsu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class CommandPriorityModel {

	private boolean isLock;
	private int maxThreads;
	private PriorityQueue<CommandModel> commandList;
	private List<CommandModel> actCmdList;
	
	public CommandPriorityModel(int maxThreads){
		this.maxThreads=maxThreads;
		commandList=new PriorityQueue<CommandModel>();
		actCmdList=new ArrayList<CommandModel>();
		isLock=false;
	}
	
	public boolean isLock() {
		return isLock;
	}
	private void resetLock(){
		isLock=actCmdList.size()>=maxThreads;
	}

	public int getMaxThreads() {
		return maxThreads;
	}
	public void setMaxThreads(int maxThreads) {
		if(this.maxThreads!=maxThreads){
			this.maxThreads = maxThreads;
			resetLock();
		}
	}
	public void addCmd(CommandModel cmd) {
		if(!commandList.contains(cmd))
			commandList.add(cmd);
	}
	public boolean activeCmd(CommandModel cmd){
		// 网管采集非lock状态
		if (!isLock()) {
			// 取得第一条命令
			// poll 移除并返问队列头部的元素 如果队列为空，则返回null
			// peek 返回队列头部的元素 如果队列为空，则返回null
			CommandModel firstCommand = commandList.peek();
			// 判断是否和传入命令一致
			if (firstCommand==null||cmd.equals(firstCommand)) {
				commandList.poll();
				actCmdList.add(cmd);
				resetLock();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public void removeCmd(CommandModel cmd){
		actCmdList.remove(cmd);
		resetLock();
	}
	
}
