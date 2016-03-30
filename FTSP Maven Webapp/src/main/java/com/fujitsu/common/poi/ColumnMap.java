package com.fujitsu.common.poi;

import java.io.UnsupportedEncodingException;

import com.fujitsu.common.CommonDefine;

public class ColumnMap {
	private String key;
	private String columnName;
	private int comboType = 0;
	private final int DEFAULT_WIDTH  = 8;
	private int width = DEFAULT_WIDTH;
	/**
	 * ColumnMap构造函数
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 */
	public ColumnMap(String key, String columnName) {
		this.key = key;
		this.columnName = columnName;
		this.comboType = CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE;
		try {
			this.width = Math.max(DEFAULT_WIDTH, columnName.getBytes("GB2312").length);
		} catch (UnsupportedEncodingException e) {
			this.width = DEFAULT_WIDTH;
		}
	}
	/**
	 * ColumnMap构造函数
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 * @param comboType 是否自动合并
	 */
	public ColumnMap(String key, String columnName, int comboType) {
		this.key = key;
		this.columnName = columnName;
		this.comboType = comboType;
		try {
			this.width = Math.max(DEFAULT_WIDTH, columnName.getBytes("GB2312").length);
		} catch (UnsupportedEncodingException e) {
			this.width = DEFAULT_WIDTH;
		}
	}
	/**
	 * ColumnMap构造函数
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 * @param comboType 合并类型
	 * @param width 列宽，以一个等宽字体的英文字符宽度为1
	 */
	public ColumnMap(String key, String columnName, int comboType, int width) {
		this.key = key;
		this.columnName = columnName;
		this.comboType = comboType;
		try {
			this.width = Math.max(width, columnName.getBytes("GB2312").length);
		} catch (UnsupportedEncodingException e) {
			this.width = width;
		}
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getComboType() {
		return comboType;
	}
	public void setComboType(int comboType) {
		this.comboType = comboType;
	}
}
