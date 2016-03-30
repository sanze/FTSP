package com.fujitsu.common;

import java.util.Date;

public class ExportResult extends Result {

	private String filePath;
	private String fileName;
	private Date exportTime;
	private int size;
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Date getExportTime() {
		return exportTime;
	}

	public void setExportTime(Date exportTime) {
		this.exportTime = exportTime;
	}

	public int getSize() {
		return size;
	}
	/**
	 * 文件大小（单位：KB）
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

}
