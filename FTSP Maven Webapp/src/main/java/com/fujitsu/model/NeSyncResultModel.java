package com.fujitsu.model;

public class NeSyncResultModel {

	private int neId;
	private String neName;
	private boolean basicSyncResult = true;
	private String basicSyncMessage="完成";
//	private boolean mstpSyncResult = true;
//	private String mstpSyncMessage="完成";
//	private boolean crsSyncResult = true;
//	private String crsSyncMessage="完成";
	
	public NeSyncResultModel(int neId,String neName){
		this.neId = neId;
		this.neName = neName;
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

	/**
	 * @return the basicSyncResult
	 */
	public boolean isBasicSyncResult() {
		return basicSyncResult;
	}

	/**
	 * @param basicSyncResult
	 *            the basicSyncResult to set
	 */
	public void setBasicSyncResult(boolean basicSyncResult) {
		this.basicSyncResult = basicSyncResult;
	}

	/**
	 * @return the basicSyncMessage
	 */
	public String getBasicSyncMessage() {
		return basicSyncMessage;
	}

	/**
	 * @param basicSyncMessage
	 *            the basicSyncMessage to set
	 */
	public void setBasicSyncMessage(String basicSyncMessage) {
		this.basicSyncMessage = basicSyncMessage;
	}

//	/**
//	 * @return the mstpSyncResult
//	 */
//	public boolean isMstpSyncResult() {
//		return mstpSyncResult;
//	}
//
//	/**
//	 * @param mstpSyncResult
//	 *            the mstpSyncResult to set
//	 */
//	public void setMstpSyncResult(boolean mstpSyncResult) {
//		this.mstpSyncResult = mstpSyncResult;
//	}
//
//	/**
//	 * @return the mstpSyncMessage
//	 */
//	public String getMstpSyncMessage() {
//		return mstpSyncMessage;
//	}
//
//	/**
//	 * @param mstpSyncMessage
//	 *            the mstpSyncMessage to set
//	 */
//	public void setMstpSyncMessage(String mstpSyncMessage) {
//		this.mstpSyncMessage = mstpSyncMessage;
//	}
//
//	/**
//	 * @return the crsSyncResult
//	 */
//	public boolean isCrsSyncResult() {
//		return crsSyncResult;
//	}
//
//	/**
//	 * @param crsSyncResult
//	 *            the crsSyncResult to set
//	 */
//	public void setCrsSyncResult(boolean crsSyncResult) {
//		this.crsSyncResult = crsSyncResult;
//	}
//
//	/**
//	 * @return the crsSyncMessage
//	 */
//	public String getCrsSyncMessage() {
//		return crsSyncMessage;
//	}
//
//	/**
//	 * @param crsSyncMessage
//	 *            the crsSyncMessage to set
//	 */
//	public void setCrsSyncMessage(String crsSyncMessage) {
//		this.crsSyncMessage = crsSyncMessage;
//	}

}
