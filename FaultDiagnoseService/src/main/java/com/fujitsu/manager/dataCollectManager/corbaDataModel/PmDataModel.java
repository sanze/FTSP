package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author xuxiaojun
 * 
 */
public class PmDataModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2612701943678491351L;
	private NameAndStringValue_T[] tpName;
	private short layerRate;
	private String granularity;
	private String retrievalTime;
	private List<PmMeasurementModel> pmMeasurementList;

	// extend
	private String tpNameString;
	private Integer emsGroupId;
	private Integer emsConnectionId;
	private Integer subnetId;
	private Integer neId;
	private Integer rackId;
	private Integer shelfId;
	private Integer slotId;
	private Integer subSlotId;
	private Integer unitId;
	private Integer subUnitId;
	private Integer ptpId;
	private Integer otnCtpId;
	private Integer sdhCtpId;

	private Integer pmTemplateId;
	private String ptpType;
	// 业务类型：1.SDH 2.WDM 3.ETH 4.ATM
	private Integer domain;
	//速率
	private String rate;
	// 1.EMSGROUP 2.EMS 3.SUBNET 4.NE 5.Shelf 6.Equipment 7.ptp 8.SDH-CTP
	// 9.OTN-CTP	
	private Integer targetType;
	// 周期 1.15分钟 2.24小时
	private Integer granularityFlag;
	// 接收时间
	private Date retrievalTimeDisplay;
	
	private String displayEmsGroup;
	private String displayEms;
	private String displaySubnet;
	private String displayNe;
	private String displayArea;
	private String displayStation;
	private String displayProductName;
	private String displayPortDesc;
	private String displayCtp;
	private String displayTemplateName;


	public NameAndStringValue_T[] getTpName() {
		return tpName;
	}

	public void setTpName(NameAndStringValue_T[] tpName) {
		this.tpName = tpName;
	}

	public short getLayerRate() {
		return layerRate;
	}

	public void setLayerRate(short layerRate) {
		this.layerRate = layerRate;
	}

	public String getGranularity() {
		return granularity;
	}

	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}

	public String getRetrievalTime() {
		return retrievalTime;
	}

	public void setRetrievalTime(String retrievalTime) {
		this.retrievalTime = retrievalTime;
	}

	public List<PmMeasurementModel> getPmMeasurementList() {
		return pmMeasurementList;
	}

	public void setPmMeasurementList(List<PmMeasurementModel> pmMeasurementList) {
		this.pmMeasurementList = pmMeasurementList;
	}

	public Integer getTargetType() {
		return targetType;
	}

	public void setTargetType(Integer targetType) {
		this.targetType = targetType;
	}

	public Integer getGranularityFlag() {
		return granularityFlag;
	}

	public void setGranularityFlag(Integer granularityFlag) {
		this.granularityFlag = granularityFlag;
	}

	public Date getRetrievalTimeDisplay() {
		return retrievalTimeDisplay;
	}

	public void setRetrievalTimeDisplay(Date retrievalTimeDisplay) {
		this.retrievalTimeDisplay = retrievalTimeDisplay;
	}
	
	public Integer getEmsGroupId() {
		return emsGroupId;
	}
	
	public void setEmsGroupId(Integer emsGroupId) {
		this.emsGroupId = emsGroupId;
	}
	
	public Integer getEmsConnectionId() {
		return emsConnectionId;
	}

	public void setEmsConnectionId(Integer emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}

	public Integer getSubnetId() {
		return subnetId;
	}
	
	public void setSubnetId(Integer subnetId) {
		this.subnetId = subnetId;
	}
	
	public Integer getNeId() {
		return neId;
	}

	public void setNeId(Integer neId) {
		this.neId = neId;
	}

	public Integer getRackId() {
		return rackId;
	}

	public void setRackId(Integer rackId) {
		this.rackId = rackId;
	}

	public Integer getShelfId() {
		return shelfId;
	}

	public void setShelfId(Integer shelfId) {
		this.shelfId = shelfId;
	}

	public Integer getSlotId() {
		return slotId;
	}

	public void setSlotId(Integer slotId) {
		this.slotId = slotId;
	}

	public Integer getSubSlotId() {
		return subSlotId;
	}

	public void setSubSlotId(Integer subSlotId) {
		this.subSlotId = subSlotId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public Integer getSubUnitId() {
		return subUnitId;
	}

	public void setSubUnitId(Integer subUnitId) {
		this.subUnitId = subUnitId;
	}

	public Integer getPtpId() {
		return ptpId;
	}

	public void setPtpId(Integer ptpId) {
		this.ptpId = ptpId;
	}

	public Integer getOtnCtpId() {
		return otnCtpId;
	}

	public void setOtnCtpId(Integer otnCtpId) {
		this.otnCtpId = otnCtpId;
	}

	public Integer getSdhCtpId() {
		return sdhCtpId;
	}

	public void setSdhCtpId(Integer sdhCtpId) {
		this.sdhCtpId = sdhCtpId;
	}

	public Integer getPmTemplateId() {
		return pmTemplateId;
	}
	
	public void setPmTemplateId(Integer pmTemplateId) {
		this.pmTemplateId = pmTemplateId;
	}
	
	public String getPtpType() {
		return ptpType;
	}
	
	public void setPtpType(String ptpType) {
		this.ptpType = ptpType;
	}
	
	public String getTpNameString() {
		return tpNameString;
	}

	public void setTpNameString(String tpNameString) {
		this.tpNameString = tpNameString;
	}

	public String getDisplayEmsGroup() {
		return displayEmsGroup;
	}
	public void setDisplayEmsGroup(String displayEmsGroup) {
		this.displayEmsGroup = displayEmsGroup;
	}
	public String getDisplayEms() {
		return displayEms;
	}
	public void setDisplayEms(String displayEms) {
		this.displayEms = displayEms;
	}
	public String getDisplaySubnet() {
		return displaySubnet;
	}
	public void setDisplaySubnet(String displaySubnet) {
		this.displaySubnet = displaySubnet;
	}
	public String getDisplayNe() {
		return displayNe;
	}
	public void setDisplayNe(String displayNe) {
		this.displayNe = displayNe;
	}
	public String getDisplayArea() {
		return displayArea;
	}
	public void setDisplayArea(String displayArea) {
		this.displayArea = displayArea;
	}
	public String getDisplayStation() {
		return displayStation;
	}
	public void setDisplayStation(String displayStation) {
		this.displayStation = displayStation;
	}
	public String getDisplayProductName() {
		return displayProductName;
	}
	public void setDisplayProductName(String displayProductName) {
		this.displayProductName = displayProductName;
	}
	public String getDisplayPortDesc() {
		return displayPortDesc;
	}
	public void setDisplayPortDesc(String displayPortDesc) {
		this.displayPortDesc = displayPortDesc;
	}
	public String getDisplayCtp() {
		return displayCtp;
	}
	public void setDisplayCtp(String displayCtp) {
		this.displayCtp = displayCtp;
	}
	public String getDisplayTemplateName() {
		return displayTemplateName;
	}
	public void setDisplayTemplateName(String displayTemplateName) {
		this.displayTemplateName = displayTemplateName;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
	
}
