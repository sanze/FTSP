package com.fujitsu.manager.systemManager.model;
import java.io.Serializable;
import java.util.List;
/**
 * 权限域实体类
 * wuchao
 * 2013.12
 */
public class AuthRegion implements Serializable{
	private static final long serialVersionUID = -8067616826958603556L;
	private String id = "";//主键ID
	private String name;//权限域名称
	private String note;//描述
	
	private String node;
	private String depth;//树深度
	
	private List<String> authLists;//网元列表
	private String authId;
	private String menuId;
	
	
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	private List<String> ids;//权限域id列表
	
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public List<String> getAuthLists() {
		return authLists;
	}
	public void setAuthLists(List<String> authLists) {
		this.authLists = authLists;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
