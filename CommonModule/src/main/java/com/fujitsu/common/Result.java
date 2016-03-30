package com.fujitsu.common;

public abstract class Result {

	// 0 成功 1 失败 9 权限检查失败
	private int returnResult;

	// 返回消息
	private String returnMessage;

	public int getReturnResult() {
		return returnResult;
	}

	public void setReturnResult(int returnResult) {
		this.returnResult = returnResult;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

}
