package com.fujitsu.model;

public class LinkAlarmModel {
	
	private int linkId;
	private String aEndPTP;
	private String aNeId;
	private String aNeType;
	private String aRackNo;
	private String aShelfNo;
	private String aSlotNo;
	private String aPortNo;
	private String aDomain;
	private int aCRCount = 0;
	private int aMJCount = 0;
	private int aMNCount = 0;
	private int aWRCount = 0;
	
	private String zEndPTP;
	private String zNeId;
	private String zNeType;
	private String zRackNo;
	private String zShelfNo;
	private String zSlotNo;
	private String zPortNo;
	private String zDomain;
	private int zCRCount = 0;
	private int zMJCount = 0;
	private int zMNCount = 0;
	private int zWRCount = 0;
	
	public int getLinkId() {
		return linkId;
	}
	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}
	
	public int getaCRCount() {
		return aCRCount;
	}
	public void setaCRCount(int aCRCount) {
		this.aCRCount = aCRCount;
	}
	public int getaMJCount() {
		return aMJCount;
	}
	public void setaMJCount(int aMJCount) {
		this.aMJCount = aMJCount;
	}
	public int getaMNCount() {
		return aMNCount;
	}
	public void setaMNCount(int aMNCount) {
		this.aMNCount = aMNCount;
	}
	public int getaWRCount() {
		return aWRCount;
	}
	public void setaWRCount(int aWRCount) {
		this.aWRCount = aWRCount;
	}
	public int getzCRCount() {
		return zCRCount;
	}
	public void setzCRCount(int zCRCount) {
		this.zCRCount = zCRCount;
	}
	public int getzMJCount() {
		return zMJCount;
	}
	public void setzMJCount(int zMJCount) {
		this.zMJCount = zMJCount;
	}
	public int getzMNCount() {
		return zMNCount;
	}
	public void setzMNCount(int zMNCount) {
		this.zMNCount = zMNCount;
	}
	public int getzWRCount() {
		return zWRCount;
	}
	public void setzWRCount(int zWRCount) {
		this.zWRCount = zWRCount;
	}
	
	public String getzDomain() {
		return zDomain;
	}
	public String getaDomain() {
		return aDomain;
	}
	public void setaDomain(String aDomain) {
		this.aDomain = aDomain;
	}
	public void setzDomain(String zDomain) {
		this.zDomain = zDomain;
	}
	public String getaNeType() {
		return aNeType;
	}
	public void setaNeType(String aNeType) {
		this.aNeType = aNeType;
	}
	public String getzNeType() {
		return zNeType;
	}
	public void setzNeType(String zNeType) {
		this.zNeType = zNeType;
	}
	public String getaNeId() {
		return aNeId;
	}
	public void setaNeId(String aNeId) {
		this.aNeId = aNeId;
	}
	public String getzNeId() {
		return zNeId;
	}
	public void setzNeId(String zNeId) {
		this.zNeId = zNeId;
	}
	public String getaEndPTP() {
		return aEndPTP;
	}
	public void setaEndPTP(String aEndPTP) {
		this.aEndPTP = aEndPTP;
	}
	public String getaRackNo() {
		return aRackNo;
	}
	public void setaRackNo(String aRackNo) {
		this.aRackNo = aRackNo;
	}
	public String getaShelfNo() {
		return aShelfNo;
	}
	public void setaShelfNo(String aShelfNo) {
		this.aShelfNo = aShelfNo;
	}
	public String getaSlotNo() {
		return aSlotNo;
	}
	public void setaSlotNo(String aSlotNo) {
		this.aSlotNo = aSlotNo;
	}
	public String getaPortNo() {
		return aPortNo;
	}
	public void setaPortNo(String aPortNo) {
		this.aPortNo = aPortNo;
	}
	public String getzEndPTP() {
		return zEndPTP;
	}
	public void setzEndPTP(String zEndPTP) {
		this.zEndPTP = zEndPTP;
	}
	public String getzRackNo() {
		return zRackNo;
	}
	public void setzRackNo(String zRackNo) {
		this.zRackNo = zRackNo;
	}
	public String getzShelfNo() {
		return zShelfNo;
	}
	public void setzShelfNo(String zShelfNo) {
		this.zShelfNo = zShelfNo;
	}
	public String getzSlotNo() {
		return zSlotNo;
	}
	public void setzSlotNo(String zSlotNo) {
		this.zSlotNo = zSlotNo;
	}
	public String getzPortNo() {
		return zPortNo;
	}
	public void setzPortNo(String zPortNo) {
		this.zPortNo = zPortNo;
	}

}
