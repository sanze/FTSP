package com.fujitsu.manager.systemManager.model;
import java.io.Serializable;
import java.util.List;
/**
 * 设备域实体类
 * wuchao
 * 2013.12
 */
public class DeviceRegion implements Serializable{
	private static final long serialVersionUID = 7825622949999240091L;
	private String id = "";//主键ID
	private String name;//设备域名称
	private String note;//描述
	private String node;
	private String depth;//树深度
	private List<String> neList;//网元列表
	private String neId;
	private List<String> ids;//设备域id列表
	private String saveType;//判断新增还是修改
	
	
	private String emsId;
	private String subnetId;
	
	
	private String neType;//网管组,网管,子网,或网元
	
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}
	public String getEmsId() {
		return emsId;
	}
	public void setEmsId(String emsId) {
		this.emsId = emsId;
	}
	public String getSubnetId() {
		return subnetId;
	}
	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public String getNeId() {
		return neId;
	}
	public void setNeId(String neId) {
		this.neId = neId;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
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
	
	
	
	
	
	public List<String> getNeList() {
		return neList;
	}
	public void setNeList(List<String> neList) {
		this.neList = neList;
	}
	@Override
	public String toString() {
		return "DeviceRegion [id=" + id + ", name=" + name + ", note=" + note
				+ ", node=" + node + "]";
	}
	
}
