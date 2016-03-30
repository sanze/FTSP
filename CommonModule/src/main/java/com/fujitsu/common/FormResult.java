package com.fujitsu.common;

public class FormResult extends Result {
	private String success;
	private String failure;
	@Override
	public void setReturnResult(int returnResult) {
		super.setReturnResult(returnResult);
		if(BaseDefine.SUCCESS == returnResult){
			success = "true";
			failure = "false";
		}else{
			success = "false";
			failure = "true";
		}
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getFailure() {
		return failure;
	}
	public void setFailure(String failure) {
		this.failure = failure;
	}
	
}
