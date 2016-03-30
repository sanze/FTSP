package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import java.io.Serializable;


/**
 * @author xuxiaojun
 *
 */
public class PmMeasurementModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -198690443679285557L;
	private String pmParameterName;
	private String pmLocation;
	private String value;
	private String unit;
	private String intervalStatus;
	
	//extend
	//标准pm代号
	private String pmStdIndex;
	//基准值
	private String pmCompareValue;
	//1.物理量 2.计数值
	private String type;
	//阈值1
	private String threshold1;
	//阈值2
	private String threshold2;
	//阈值3
	private String threshold3;
	//偏差值
	private String offset;
	//上限值
	private String upperValue;
	//上限值偏差
	private String upperOffset;
	//下限值
	private String lowerValue;
	//下限值偏差
	private String lowerOffset;
	//pm描述
	private String pmdescription;
	//位置 1.PML_NEAR_END_Rx 2.PML_FAR_END_Rx 3.PML_NEAR_END_Tx 4.PML_FAR_END_Tx 5.PML_BIDIRECTIONAL
	private int locationFlag;
	//异常等级 0：正常 1：告警等级1 2：告警等级2 3：告警等级3
	private int exceptionLv;
	//连续异常次数
	private int exceptionCount;
	//过滤值
	private String filterValue;
	//显示用性能比较值
	private String displayCompareValue;
	
	public String getPmParameterName() {
		return pmParameterName;
	}
	public void setPmParameterName(String pmParameterName) {
		this.pmParameterName = pmParameterName;
	}
	public String getPmLocation() {
		return pmLocation;
	}
	public void setPmLocation(String pmLocation) {
		this.pmLocation = pmLocation;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getIntervalStatus() {
		return intervalStatus;
	}
	public void setIntervalStatus(String intervalStatus) {
		this.intervalStatus = intervalStatus;
	}
	public String getPmStdIndex() {
		return pmStdIndex;
	}
	public void setPmStdIndex(String pmStdIndex) {
		this.pmStdIndex = pmStdIndex;
	}
	public String getPmCompareValue() {
		return pmCompareValue;
	}
	public void setPmCompareValue(String pmCompareValue) {
		this.pmCompareValue = pmCompareValue;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getThreshold1() {
		return threshold1;
	}
	public void setThreshold1(String threshold1) {
		this.threshold1 = threshold1;
	}
	public String getThreshold2() {
		return threshold2;
	}
	public void setThreshold2(String threshold2) {
		this.threshold2 = threshold2;
	}
	public String getThreshold3() {
		return threshold3;
	}
	public void setThreshold3(String threshold3) {
		this.threshold3 = threshold3;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getUpperValue() {
		return upperValue;
	}
	public void setUpperValue(String upperValue) {
		this.upperValue = upperValue;
	}
	public String getUpperOffset() {
		return upperOffset;
	}
	public void setUpperOffset(String upperOffset) {
		this.upperOffset = upperOffset;
	}
	public String getLowerValue() {
		return lowerValue;
	}
	public void setLowerValue(String lowerValue) {
		this.lowerValue = lowerValue;
	}
	public String getLowerOffset() {
		return lowerOffset;
	}
	public void setLowerOffset(String lowerOffset) {
		this.lowerOffset = lowerOffset;
	}
	public String getPmdescription() {
		return pmdescription;
	}
	public void setPmdescription(String pmdescription) {
		this.pmdescription = pmdescription;
	}
	public int getLocationFlag() {
		return locationFlag;
	}
	public void setLocationFlag(int locationFlag) {
		this.locationFlag = locationFlag;
	}
	public int getExceptionLv() {
		return exceptionLv;
	}
	public void setExceptionLv(int exceptionLv) {
		this.exceptionLv = exceptionLv;
	}
	public int getExceptionCount() {
		return exceptionCount;
	}
	public void setExceptionCount(int exceptionCount) {
		this.exceptionCount = exceptionCount;
	}
	public String getFilterValue() {
		return this.filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	public String getDisplayCompareValue() {
		return this.displayCompareValue;
	}
	public void setDisplayCompareValue(String displayCompareValue) {
		this.displayCompareValue = displayCompareValue;
	}
}
