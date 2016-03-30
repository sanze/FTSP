package com.fujitsu.model;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;

import com.fujitsu.common.CommonDefine;

public class InspectDetailItemModel {
	// 网元
	private String displayNe;
	// 巡检项
	private String inspectItemName;
	// 评估项
	private String evaluateItemName;
	// 对象
	private String targetDesc;
	// 巡检值描述
	private String valueDesc;
	// 巡检值
	private String value;
	// 重要度
	private Integer exceptionLv;
	private String displayExceptionLv;
	
	// 网管
	private String displayEms;
	// 子网
	private String displaySubnet;
	// 设备类型
	private String productName;
	// 区域
	private String displayArea;
	// 基准值 
	private String compareValue;
	// 包机人
	private String owner;
	// 日期
	private Date retrievalTime;

	public InspectDetailItemModel(String displayNe, String inspectItemName,
			String evaluateItemName, String targetDesc, String valueDesc,
			String value, Integer exceptionLv, String displayEms,
			String displaySubnet, String productName, String displayArea,
			String compareValue, String owner, Date retrievalTime) {
		super();
		this.displayNe = displayNe;
		this.inspectItemName = inspectItemName;
		this.evaluateItemName = evaluateItemName;
		this.targetDesc = targetDesc;
		this.valueDesc = valueDesc;
		this.value = value;
		setExceptionLv(exceptionLv);
		this.displayEms = displayEms;
		this.displaySubnet = displaySubnet;
		this.productName = productName;
		this.displayArea = displayArea;
		this.compareValue = compareValue;
		this.owner = owner;
		this.retrievalTime = retrievalTime;
	}

	public String getDisplayNe() {
		return displayNe;
	}

	public void setDisplayNe(String displayNe) {
		this.displayNe = displayNe;
	}

	public String getInspectItemName() {
		return inspectItemName;
	}

	public void setInspectItemName(String inspectItemName) {
		this.inspectItemName = inspectItemName;
	}

	public String getEvaluateItemName() {
		return evaluateItemName;
	}

	public void setEvaluateItemName(String evaluateItemName) {
		this.evaluateItemName = evaluateItemName;
	}

	public String getTargetDesc() {
		return targetDesc;
	}

	public void setTargetDesc(String targetDesc) {
		this.targetDesc = targetDesc;
	}

	public String getValueDesc() {
		return valueDesc;
	}

	public void setValueDesc(String valueDesc) {
		this.valueDesc = valueDesc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getExceptionLv() {
		return exceptionLv;
	}

	public void setExceptionLv(Integer exceptionLv) {
		this.exceptionLv = exceptionLv;
		this.displayExceptionLv = CommonDefine.INSPECT.SEVERITY.valueToString(this.exceptionLv);
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDisplayArea() {
		return displayArea;
	}

	public void setDisplayArea(String displayArea) {
		this.displayArea = displayArea;
	}

	public String getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(String compareValue) {
		this.compareValue = compareValue;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getRetrievalTime() {
		return retrievalTime;
	}

	public void setRetrievalTime(Date retrievalTime) {
		this.retrievalTime = retrievalTime;
	}

	public String getDisplayExceptionLv() {
		return displayExceptionLv;
	}

	public void setDisplayExceptionLv(String displayExceptionLv) {
		this.displayExceptionLv = displayExceptionLv;
	}
	
	public InspectDetailItemModel clone(){
		return new InspectDetailItemModel(displayNe, inspectItemName,
				evaluateItemName, targetDesc, valueDesc,
				value, exceptionLv, displayEms,
				displaySubnet, productName, displayArea,
				compareValue, owner, retrievalTime);
	}

	public static String[] getColumnNames(){
		String[] columns = null;
		Field[] fields=InspectDetailItemModel.class.getDeclaredFields();
		if(fields!=null){
			columns=new String[fields.length];
			for(int i=0;i<fields.length;i++){
				Field field=fields[i];
				columns[i]=field.getName();
			}
		}
		return columns;
	}
	public String toCsvRow(char fieldDelimiter){
		StringBuffer row = new StringBuffer();
		Field[] fields=InspectDetailItemModel.class.getDeclaredFields();
		if(fields!=null){
			for(int i=0;i<fields.length;i++){
				Field field=fields[i];
				field.setAccessible(true);
				Object fieldValue=null;
				try {
					fieldValue = field.get(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(fieldValue!=null){
					if(fieldValue instanceof Date){
						row.append(DateFormat.getDateTimeInstance().format(fieldValue));
					}else{
						row.append(fieldValue);
					}
				}
				if(i<fields.length-1)
					row.append(fieldDelimiter);
			}
		}
		return row.toString();
	}
}
