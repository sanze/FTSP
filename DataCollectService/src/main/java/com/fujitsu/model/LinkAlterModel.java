package com.fujitsu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LinkAlterModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4997768837834365389L;
	//1.新增 2.删除
	private int changeType;
	//link名称
	private String linkName;
	//a端端口
	private int aEndPtp;
	//z端端口
	private int zEndPtp;
	
	/** add 20140805 **/
	//新增或删除的link原始数据，字段与数据库一致
	private Map<String,Object> link;
	//冲突列表
	private List<Map<String, Object>> conflictList=new ArrayList<Map<String, Object>>();
	
	public Map<String,Object> getLink() {
		return link;
	}
	public void setLink(Map<String,Object> link) {
		this.link = link;
	}
	public List<Map<String, Object>> getConflictList() {
		return conflictList;
	}
	public void setConflictList(List<Map<String, Object>> conflictList) {
		this.conflictList = conflictList;
	}
	/** add 20140805 **/

	public int getChangeType() {
		return changeType;
	}
	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}
	public int getaEndPtp() {
		return aEndPtp;
	}
	public void setaEndPtp(int aEndPtp) {
		this.aEndPtp = aEndPtp;
	}
	public int getzEndPtp() {
		return zEndPtp;
	}
	public void setzEndPtp(int zEndPtp) {
		this.zEndPtp = zEndPtp;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

}
