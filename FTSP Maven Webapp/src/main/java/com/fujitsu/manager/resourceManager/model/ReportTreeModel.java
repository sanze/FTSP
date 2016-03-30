package com.fujitsu.manager.resourceManager.model;

import java.io.Serializable;
public class ReportTreeModel implements Serializable{
	private static final long serialVersionUID = 11954240991395342L;
	public String type;
	public String parentIds;
	public String ids;
	
	public String id="0";
	
	
	public String node;
	public int userId=0;
	
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParentIds() {
		return parentIds;
	}
	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

	
	
}
