package com.fujitsu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LinkAlterResultModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6025883417849023150L;
	//是否有变化 false:无变化 true:有变化
	private boolean isChanged;
	//是否需要同步网管 false:不需要 true:需要
	private boolean  needSyncEms;
	//是否需要同步网元 false:不需要 true:需要
	private boolean  needSyncNe;
	//需要同步网元列表Id
	private List<Integer> syncNeList;
	//变化列表
	private List<LinkAlterModel> changeList=new ArrayList<LinkAlterModel>();
	
	public boolean isChanged() {
		return isChanged;
	}
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	public boolean isNeedSyncNe() {
		return needSyncNe;
	}
	public void setNeedSyncNe(boolean needSyncNe) {
		this.needSyncNe = needSyncNe;
	}
	public List<Integer> getSyncNeList() {
		return syncNeList;
	}
	public void setSyncNeList(List<Integer> syncNeList) {
		this.syncNeList = syncNeList;
	}
	public List<LinkAlterModel> getChangeList() {
		return changeList;
	}
	public void setChangeList(List<LinkAlterModel> changeList) {
		this.changeList = changeList;
	}
	public boolean isNeedSyncEms() {
		return needSyncEms;
	}
	public void setNeedSyncEms(boolean needSyncEms) {
		this.needSyncEms = needSyncEms;
	}
	
}
