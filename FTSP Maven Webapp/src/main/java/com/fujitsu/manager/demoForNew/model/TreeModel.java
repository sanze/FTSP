package com.fujitsu.manager.demoForNew.model;

import java.io.Serializable;
/**
 * TreModel模型
 * hg
 * 2013.12
 */
public class TreeModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345599856353891978L;
	private String id;
	private String text;//节点显示名称
	private boolean leaf;//是否为叶子节点
	private boolean disabled;//是否可用
	private String iconCls;//节点图标样式
	private String href;//点击后的链接
	private String hrefTarget;//在何iframe中显示
	private boolean draggable;//是否可拖拽
	
	
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
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
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
