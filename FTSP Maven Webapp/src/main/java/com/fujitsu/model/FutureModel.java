package com.fujitsu.model;

import java.util.concurrent.Future;

public class FutureModel {

	private int neId;
	private String neName;
	
	private Future<NeSyncResultModel> future;
	
	public FutureModel(int neId,String neName,Future<NeSyncResultModel> future){
		this.neId = neId;
		this.neName = neName;
		this.future = future;
	}

	/**
	 * @return the neId
	 */
	public int getNeId() {
		return neId;
	}

	/**
	 * @param neId the neId to set
	 */
	public void setNeId(int neId) {
		this.neId = neId;
	}

	/**
	 * @return the future
	 */
	public Future<NeSyncResultModel> getFuture() {
		return future;
	}

	/**
	 * @param future the future to set
	 */
	public void setFuture(Future<NeSyncResultModel> future) {
		this.future = future;
	}

	/**
	 * @return the neName
	 */
	public String getNeName() {
		return neName;
	}

	/**
	 * @param neName the neName to set
	 */
	public void setNeName(String neName) {
		this.neName = neName;
	}

}
