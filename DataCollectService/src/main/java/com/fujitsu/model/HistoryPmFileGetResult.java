package com.fujitsu.model;

public class HistoryPmFileGetResult {
	//结果
	private boolean result;
	//是否需要重采
	private boolean needRecollect;
	//错误信息
	private String errorMessage;
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public boolean isNeedRecollect() {
		return needRecollect;
	}
	public void setNeedRecollect(boolean needRecollect) {
		this.needRecollect = needRecollect;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
