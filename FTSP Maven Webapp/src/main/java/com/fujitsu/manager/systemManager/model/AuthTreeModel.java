package com.fujitsu.manager.systemManager.model;

import java.io.Serializable;
public class AuthTreeModel implements Serializable{
	private static final long serialVersionUID = 8323945842129059361L;
	private String id;
	private String text;//节点显示名称
	private boolean leaf=false;//是否为叶子节点  
	//private boolean disabled=true;//是否可用
	//private String iconCls;//节点图标样式
	private String href;//点击后的链接
	private String hrefTarget;//在何iframe中显示
	private boolean draggable;//是否可拖拽
	
	//private boolean checked=false;
	
	private String node;//前台向后台默认传值  这个参数只是传值用，没有会报错
	
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getHrefTarget() {
		return hrefTarget;
	}
	public void setHrefTarget(String hrefTarget) {
		this.hrefTarget = hrefTarget;
	}
	public boolean isDraggable() {
		return draggable;
	}
	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}
	
	
}
