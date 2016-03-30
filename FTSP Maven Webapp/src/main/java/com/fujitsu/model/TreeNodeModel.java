package com.fujitsu.model;

public class TreeNodeModel {
	private String id;
	private String text;
	private boolean leaf;
	private String checked;
	private int nodeId;
	private int nodeLevel;
	private String parent;
	private int parentId;
	private int parentLevel;
	
	public String getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public String getChecked() {
		return checked;
	}
	public int getNodeId() {
		return nodeId;
	}
	public int getNodeLevel() {
		return nodeLevel;
	}
	public String getParent() {
		return parent;
	}
	public int getParentId() {
		return parentId;
	}
	public int getParentLevel() {
		return parentLevel;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public void setParentLevel(int parentLevel) {
		this.parentLevel = parentLevel;
	}

}
